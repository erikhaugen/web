/**
 * Created:  Dec 12, 2014 2:53:44 PM
 */
package org.kfm.camel.processor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.kfm.camel.converter.StrokeConverter;
import org.kfm.camel.dao.AfdImageDao;
import org.kfm.camel.dao.NoteDao;
import org.kfm.camel.dao.PageDao;
import org.kfm.camel.dao.TemplateDao;
import org.kfm.camel.entity.DocumentFactory;
import org.kfm.camel.entity.NoteResources;
import org.kfm.camel.entity.PageFactory;
import org.kfm.camel.entity.UploadTransaction;
import org.kfm.camel.entity.evernote.XMLStrokes;
import org.kfm.camel.evernote.EvernoteNoteContentClass;
import org.kfm.camel.exception.ENOAuthTokenExpiredException;
import org.kfm.camel.exception.ENStorageQuotaReachedException;
import org.kfm.camel.image.ImageFactory;
import org.kfm.camel.message.MessageHeader;
import org.kfm.camel.util.Utils;
import org.kfm.jpa.AfdImage;
import org.kfm.jpa.Document;
import org.kfm.jpa.Page;
import org.kfm.jpa.Template;
import org.kfm.jpa.TimeMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteAttributes;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.livescribe.afp.Afd;
import com.livescribe.afp.PageStroke;
import com.livescribe.afp.stf.STFStroke;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class StrokesUpdateProcessor implements Processor {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private PageDao pageDao;
	
	@Autowired
	private NoteDao noteDao;
	
	@Autowired
	private TemplateDao templateDao;

	@Autowired
	private AfdImageDao afdImageDao;
	
	@PropertyInject("evernotebookkeeper.evernote.ui.setting.image")
	private String uiSettingImagePath;
	
	@PropertyInject("evernotebookkeeper.evernote.ui.setting.image.mimetype")
	private String uiSettingImageMimeType;
	
	@PropertyInject("evernotebookkeeper.livescribe.logo")
	private String livescribeLogoPath;
	
	@PropertyInject("evernotebookkeeper.livescribe.logo.mimetype")
	private String livescribeLogoMimeType;
	
	/**
	 * <p></p>
	 * 
	 */
	public StrokesUpdateProcessor() {
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {

		String method = "process()";

		Boolean isNewDocument = exchange.getIn().getHeader(MessageHeader.IS_NEW_DOCUMENT.getHeader(), Boolean.class);
		String penDisplayId = exchange.getIn().getHeader(MessageHeader.PEN_DISPLAY_ID.getHeader(), String.class);
		String oAuthAccessToken = exchange.getIn().getHeader(MessageHeader.ACCESS_TOKEN.getHeader(), String.class);
		String uid = exchange.getIn().getHeader(MessageHeader.UID.getHeader(), String.class);
		Long enUserId = exchange.getIn().getHeader(MessageHeader.EN_USER_ID.getHeader(), Long.class);
		
		UploadTransaction upTx = exchange.getIn().getBody(UploadTransaction.class);
		Afd afd = upTx.getAfd();
		String afdGuid = afd.getGuid();
		String afdName = afd.getTitle();
		int afdCopy = afd.getCopy();
		String afdVersion = afd.getVersion();
		
		//	Iterate over the uploaded strokes-per-page objects.
		List<PageStroke> pageStrokes = afd.getPageStrokes();
		for (PageStroke pageStroke : pageStrokes) {
			int pageIndex = pageStroke.getPageIndex();
			String docGuid = pageStroke.getDocumentGuid();
			
			//	Convert the new strokes to XMLStrokes for storage in Evernote.
			TreeSet<STFStroke> stfStrokes = (TreeSet)pageStroke.getStrokes();
			XMLStrokes newXmlStrokes = StrokeConverter.fromSTFStrokes(stfStrokes);
			
			Document document = null;
			Page page = null;
			
			//	Find a Document (from the database) matching the uploaded AFD.
			Document matchingDocument = findMatchingDocument(upTx, docGuid);
			
			//	The Evernote Resource object storing the strokes as XML.
			Resource strokeResource = null;

			//	The Evernote Resource object storing the image of strokes (thumbnail?).
			Resource strokeImageResource = null;

			NoteResources noteResources = null; 
			
			//--------------------------------------------------
			//	After this block, either an existing Document and Page are
			//	found, or new ones are created.
			if (matchingDocument == null) {
				document = DocumentFactory.create(afd, docGuid, penDisplayId, enUserId);				
				page = PageFactory.create(document, pageIndex);
				
			} else {
				document = matchingDocument;
				Page matchingPage = findMatchingPage(matchingDocument, pageIndex);
				if (matchingPage == null) {
					page = PageFactory.create(document, pageIndex);
				} else {
					page = matchingPage;
				}
			}
			
			
			Note note = noteDao.find(page, oAuthAccessToken);
			
			String noteTitle = generatePageNoteTitle(page.getLabel(), page.getDocument().getDocName());

			if (note == null) {
				note = new Note();
				note.setTitle(noteTitle);
				
				NoteAttributes noteAttributes = new NoteAttributes();
				noteAttributes.setContentClass(EvernoteNoteContentClass.PAGE_VERSION_1_0_2.getLabel());
				note.setAttributes(noteAttributes);
				note.setContent("<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\"><en-note><h1>Processing Strokes from your Smartpen...</h1></en-note>");
				
				try {
					note = noteDao.save(note, oAuthAccessToken, docGuid);
				} catch (EDAMNotFoundException e) {
					String msg = "Exception thrown";
					if (e.getIdentifier().equals("Note.notebookGuid")) {
						logger.error(method + " - NotebookGuid '" + note.getNotebookGuid() + "' not found in Evernote.");
						//	TODO:  Finish here.
					}
				}
				addImageResources(page, note);
				
			} else {
				
				String noteNotebookGuid = note.getNotebookGuid();
				String docNotebookGuid = document.getEnNotebookGuid();

				//	ISSUE:
				//	If the Note's NotebookGuid is different from the Document's, 
				//	this places the Note back in the Notebook stored in the database.
				//	
				//	If the user had moved the Note into a different Notebook, 
				//	this part would undo that and place it back in the "original" 
				//	Notebook.
				if ((docNotebookGuid != null) && (!docNotebookGuid.equals(noteNotebookGuid))) {
					note.setNotebookGuid(docNotebookGuid);
				}
				
				//	ISSUE:
				//	If the Note was "deleted" but not "expunged", this
				//	section pulls the Note out of the Trash.
				if ((!note.isActive()) && (note.isSetDeleted())) {
					note.setActive(true);
					note.setDeletedIsSet(false);
				}
				
				if (note.getResourcesSize() > 0) {
					
					//	Find the Image and Stroke Resources.
					noteResources = findNoteResources(note, page);
					
					strokeResource = noteResources.getStrokeResource();
					if ((strokeResource != null) && (strokeResource.getData() != null)) {
						
						//	Add existing XMLStrokes to the new XMLStrokes.
						XMLStrokes xmlStrokes = StrokeConverter.fromResource(strokeResource);
						newXmlStrokes.getList().addAll(xmlStrokes.getList());
					} else {
						logger.warn(method + " - No stroke Resource found for '" + noteTitle + "'.");
					}
				} else {
					logger.warn(method + " - No Resources found for Note '" + noteTitle + "'.");
				}
				
				//	Test what "version" of Note ContentClass to bring up-to-date
				String noteAttributesContentClass = note.getAttributes().getContentClass();
				if (noteAttributesContentClass != null) {
					EvernoteNoteContentClass eNCC = EvernoteNoteContentClass.getEvernoteNoteContentClass(noteAttributesContentClass);
					switch (eNCC) {
					case PAGE_VERSION_1_0_0:
					case PAGE_VERSION_1_0_1: {
						//Need to inject our UI Setting PNG and Logo Inactive frost GIF (as resources) into Evernote Note
						addImageResources(page, note);
						note.getAttributes().setContentClass(EvernoteNoteContentClass.PAGE_VERSION_1_0_2.getLabel());
						break;
					}
					case PAGE_VERSION_1_0_2:
						break;
					default:
						break;
					}
				}
			}
			
			//	TODO:  "Add our strokes"  EBK 2279
			
			
			if (noteResources.getStrokeResource() == null) {
				strokeResource = new Resource();
				strokeResource.setMime("application/xml");
				noteResources.setStrokeResource(strokeResource);
				note.addToResources(strokeResource);
			}
			
			if (noteResources.getImageResource() == null) {
				strokeImageResource = new Resource();
				strokeImageResource.setMime("image/png");
				noteResources.setImageResource(strokeImageResource);
				note.addToResources(strokeImageResource);
			}
			
			Data strokeResourceData = createStrokeData(newXmlStrokes, page);
			strokeResource.setData(strokeResourceData);
			
			//--------------------------------------------------
			//	Generate Page Image
			//--------------------------------------------------
			BufferedImage img = ImageFactory.createImageForPage(page, strokeResource, 1);
			
			Resource newStrokeImageResource = ImageFactory.createImageResource(page, strokeResource, strokeImageResource, scale);
			byte[] newStrokeImageHash = newStrokeImageResource.getData().getBodyHash();
			String strokeImageHashString = Utils.asHexString(newStrokeImageHash);
			page.setEnImageResourceHash(strokeImageHashString);
			
			
			
			
			
			
			
			//--------------------------------------------------
			//	Matching Document
			//--------------------------------------------------
			if (matchingDocument != null) {
				
				Page matchingPage = findMatchingPage(matchingDocument, pageIndex);
				
				//--------------------------------------------------
				//	Matching Page
				//--------------------------------------------------
				if (matchingPage != null) {
					
					String enNoteGuid = matchingPage.getEnNoteGuid();
//					Note note = noteDao.find(matchingPage, oAuthAccessToken);
	
					if (enNoteGuid != null) {
											
						if (note != null) {
							String enNotebookGuid = matchingDocument.getEnNotebookGuid();
							
							//	If the Notebook GUID has changed, update it with
							//	the database version.
							if (!note.getNotebookGuid().equals(enNotebookGuid)) {
								note.setNotebookGuid(enNotebookGuid);
							}
							
							//	If Note was not 'expunged', move it out of the trash.
							if ((!note.isActive()) && (note.isSetDeleted())) {
								logger.warn(method + " - Note has been deleted, but not expunged.  Moving it out of trash.");
								note.setActive(true);
								note.setDeletedIsSet(false);
							}						
		
							//	If the Note has Resources ...
							if (note.getResourcesSize() > 0) {
	
								
								//	If the stroke Resource was found ...
								if ((strokeResource != null) && (strokeResource.getData() != null)) {
									
//									TreeSet<STFStroke> stfStrokes = (TreeSet)pageStroke.getStrokes();
									XMLStrokes psXmlStrokes = StrokeConverter.fromSTFStrokes(stfStrokes);
	
	//								XMLStrokes previousStrokes = null;
									byte[] strokeResourceData = strokeResource.getData().getBody();
									XMLStrokes previousStrokes = StrokeConverter.fromBytesToXMLStrokes(strokeResourceData);
									
									
									previousStrokes.list.addAll(psXmlStrokes.list);
									psXmlStrokes = previousStrokes;
								}
							}
							
						} else {
							logger.debug(method + " - Note not found for Page with document GUID '" + docGuid + "' and page index '" + pageIndex + "'.");
						}
					} else {
						logger.debug(method + " - Note GUID not found in Page.  Page must never have been sent to Evernote.");
//						String noteTitle = generatePageNoteTitle(matchingPage.getLabel(), matchingPage.getDocument().getDocName());
						
						if (note == null) {
							
							note = new Note();
							note.setNotebookGuid(matchingDocument.getEnNotebookGuid());
							note.setTitle(noteTitle);
							
							//	Setting Note attribute contentClass to make note read-only to all other's
							NoteAttributes tNoteAttrib = new NoteAttributes();
							tNoteAttrib.setContentClass(EvernoteNoteContentClass.PAGE_VERSION_1_0_2.getLabel());
							note.setAttributes(tNoteAttrib);
							
							//	TODO:  Refactor to use Velocity.
							
							//	Because we actually commit the "note" to EN (to get 
							//	resources saved) we need empty content to keep from
							//	getting an exception.
							note.setContent("<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\"><en-note><h1>Processing Strokes from your Smartpen...</h1></en-note>");
							
							//--------------------------------------------------
							//	Create the new Page Note in Evernote.
							//--------------------------------------------------
							try {
								note = noteDao.save(note, oAuthAccessToken, uid);
							} catch (EDAMNotFoundException e) {
								e.printStackTrace();
								
								//	TODO:  Retry?
								
							} catch (EDAMUserException e) {
								e.printStackTrace();
							} catch (ENOAuthTokenExpiredException e) {
								e.printStackTrace();
							} catch (ENStorageQuotaReachedException e) {
								e.printStackTrace();
							}
							
							if (note != null) {
								addImageResources(matchingPage, note);
							}
						} else {
							
							note.setTitle(noteTitle);
							String noteAttributesContentClass = note.getAttributes().getContentClass();
							if (noteAttributesContentClass != null) {
								EvernoteNoteContentClass eNCC = EvernoteNoteContentClass.getEvernoteNoteContentClass(noteAttributesContentClass);
								switch (eNCC) {
								case PAGE_VERSION_1_0_0:
								case PAGE_VERSION_1_0_1: {
									
									//	Add the logo and settings images as Evernote Resources of the Note.
									addImageResources(matchingPage, note);
									note.getAttributes().setContentClass(EvernoteNoteContentClass.PAGE_VERSION_1_0_2.getLabel());
									break;
								}
								case PAGE_VERSION_1_0_2:
									break;
								default:
									break;
								}
							}
						}
						
						String tNewStrokeHexHash = null;
						byte[] tNewStrokeBytes = null;
						byte[] tNewStrokeHash = null;
						
						//	TODO:  "Add our strokes"  EBK 2279
						
						String xml = StrokeConverter.toXmlString(xmlStrokes);
						byte[] strokeHash = Utils.toMD5Hash(xml);
						String strokeHashHexString = Utils.asHexString(strokeHash);
						
						matchingPage.setEnStrokeResourceHash(strokeHashHexString);
						
						Data strokeResourceData = new Data();
						byte[] xmlBytes = xml.getBytes("UTF-8");
						
						//	Set the body of the Resource Data to the raw
						//	bytes of the XML String.
						strokeResourceData.setBody(xmlBytes);
						
						//	Set the body hash to the MD5 digest of those 
						//	XML String bytes.
						strokeResourceData.setBodyHash(strokeHash);
						
						//	Set the size of the Resource Data to be the size
						//	(length) of the XML String bytes.
						strokeResourceData.setSize(xmlBytes.length);
						
						
					}
				//--------------------------------------------------
				//	New Page
				//--------------------------------------------------
				} else {
					//	TODO:  Create new Page
					
					Page newPage = new Page();
//					newPage.set
				}
			//--------------------------------------------------
			//	New Document
			//--------------------------------------------------
			} else {
				
				//	TODO:  Create new Document
			}
		}
		
		
		
		
		
		
		
		
		
	}

	/**
	 * <p></p>
	 * 
	 * @param newXmlStrokes
	 * @param page
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	private Data createStrokeData(XMLStrokes newXmlStrokes, Page page) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		String xml = StrokeConverter.toXmlString(newXmlStrokes);
		byte[] strokeHash = Utils.toMD5Hash(xml);
		String strokeHashHexString = Utils.asHexString(strokeHash);
		
		page.setEnStrokeResourceHash(strokeHashHexString);
		
		Data strokeResourceData = new Data();
		byte[] xmlBytes = xml.getBytes("UTF-8");
		
		//	Set the body of the Resource Data to the raw
		//	bytes of the XML String.
		strokeResourceData.setBody(xmlBytes);
		
		//	Set the body hash to the MD5 digest of those 
		//	XML String bytes.
		strokeResourceData.setBodyHash(strokeHash);
		
		//	Set the size of the Resource Data to be the size
		//	(length) of the XML String bytes.
		strokeResourceData.setSize(xmlBytes.length);
		
		return strokeResourceData;
	}

	/**
	 * <p></p>
	 * 
	 * @param page
	 * @param note
	 * @throws IOException 
	 */
	private void addImageResources(Page page, Note note) throws IOException {
		Resource uiSettingsResource = loadResource(uiSettingImagePath, uiSettingImageMimeType);
		note.addToResources(uiSettingsResource);
		
		Resource livescribeLogoResource = loadResource(livescribeLogoPath, livescribeLogoMimeType);
		note.addToResources(livescribeLogoResource);
		
		byte[] uiSettingResourceBytes = uiSettingsResource.getData().getBodyHash();
		String uiSettingResourceHash = new String(Hex.encodeHex(uiSettingResourceBytes));
		page.setEnLsUiSetResourceHash(uiSettingResourceHash);
			
		byte[] livescribeLogoResourceBytes = livescribeLogoResource.getData().getBodyHash();
		String livescribeLogoResourceHash = new String(Hex.encodeHex(livescribeLogoResourceBytes));
		page.setEnLsLogoResourceHash(livescribeLogoResourceHash);
	}

	/**
	 * <p></p>
	 * 
	 * @param matchingDocument
	 * @param pageIndex
	 * @return
	 */
	private Page findMatchingPage(Document document, int pageIndex) {
		
		String method = "findMatchingPage()";
		
		Page matchingPage = null;
		List<Page> pages = document.getPages();
		for (Page page : pages) {
			if (page.getPageIndex() == pageIndex) {
				matchingPage = page;
				break;
			}
		}
		return matchingPage;
	}

	/**
	 * <p>Returns an object containing both the image and stroke 
	 * <code>Resource</code> of the given <code>Note</code> matching the
	 * given <code>Page<code> instance.</p>
	 * 
	 * <p>Returns an <u>empty</u> instance if neither <code>Resource</code> is found.</p>
	 * 
	 * @param note The <code>Note</code> to search.
	 * @param page The <code>Page</code> to match.
	 * 
	 * @return an object containing both the image and stroke 
	 * <code>Resource</code>.
	 */
	private NoteResources findNoteResources(Note note, Page page) {
		
		String method = "findNoteResources()";
		
		NoteResources noteResources = new NoteResources();
		
		if (note != null) {
			
			List<Resource> resources = note.getResources();
			
			for (Resource noteResource : note.getResources()) {
				
				//	Find the Stroke Resource, from Evernote, that 
				//	matches the one associated to this Page.
				if (noteResource.getGuid().equals(page.getEnStrokeResourceGuid())) {
					logger.debug(method + " - Stroke Resource of newly created/updated Note found.");
					noteResources.setStrokeResource(noteResource);
					continue;
				}
				
				//	Find the Image Resource, from Evernote, that
				//	matches the one associated to this Page.
				if (noteResource.getGuid().equals(page.getEnImageResourceGuid())) {
					logger.debug(method + " - Image Resource of newly created/updated Note found.");
					noteResources.setImageResource(noteResource);
					continue;
				}
				
				Data noteData = noteResource.getData();
				if (noteData != null) {
					byte[] dataBytes = noteData.getBodyHash();
					try {
						byte[] pageStrokeHash = Hex.decodeHex(page.getEnStrokeResourceHash().toCharArray());
						
						//	Compare the hash of the Stroke resource from Evernote to the hash stored in the 'page' table record.
						if (Arrays.equals(dataBytes, pageStrokeHash)) {
							logger.debug(method + " - Found Stroke Resource of newly created/updated Note.");
							noteResources.setStrokeResource(noteResource);
							continue;
						}
					} catch (DecoderException e) {
						logger.error(method + "DecoderException thrown when decoding stroke Resource Hex hash.");
						e.printStackTrace();
					}
					
					try {
						byte[] pageImageHash = Hex.decodeHex(page.getEnImageResourceHash().toCharArray());
						
						//	Compare the hash of the Image resource from Evernote to the hash stored in the 'page' table record.
						if (Arrays.equals(dataBytes, pageImageHash)){
							logger.debug(method + " - Found Image Resource of newly created/updated Note.");
							noteResources.setImageResource(noteResource);
							continue;
						}
					} catch (DecoderException e) {
						logger.error(method + "DecoderException thrown when decoding image Resource Hex hash.");
						e.printStackTrace();
					}
				} else {
					logger.warn(method + " - The Note's Data was 'null'.  Continuing ...");
				}
			}
		} else {
			logger.error(method + " - Provided Note instance was 'null'.");
		}
		return noteResources;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param upTx
	 * @param afdGuid
	 * @return
	 */
	private Document findMatchingDocument(UploadTransaction upTx, String afdGuid) {
		Document matchingDocument = null;
		List<Document> documents = upTx.getDocuments();
		for (Document document : documents) {
			if (document.getGuid().equals(afdGuid)) {
				matchingDocument = document;
				break;
			}
		}
		return matchingDocument;
	}

	private String generatePageNoteTitle(String pageNumberString, String documentName) {
		
		String pageTitle = "Blank Page" + " - " + documentName;
		try {
			String padding = null;
			int pageNumber = Integer.parseInt(pageNumberString);
			if (pageNumber <= 0) {
				return pageTitle;
			}
			
			if (pageNumber > 99) {
				padding = "";
			} else if (isBetween(pageNumber, 10, 99)) {
				padding = " ";
			} else {
				padding = "  ";
			}
			pageTitle = String.format("Page %s%i - %s", padding, pageNumber, documentName);
		} catch (NumberFormatException e) {
			pageTitle = "Blank Page" + " - " + documentName;
		}
		return pageTitle;
	}
	
	private boolean isBetween(int pgNum, int lower, int upper) {
		return lower <= pgNum && pgNum <= upper;
	}
	
	private Resource loadResource(String imagePath, String imageMimeType) throws IOException {
		
		byte[] imageBytes = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(imagePath));
//		String imageMimeType = AppProperties.getInstance().getProperty(PROP_EN_UI_SETTING_IMAGE_MIMETYPE);
		
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsae) {
			logger.error("getUISettingPNGAsEvernoteResource() encountered the following exception(returning <NULL>):", nsae);
			return null;
		}
		byte[] imageHash = digest.digest(imageBytes);

		Data resourceData = new Data();
		resourceData.setBody(imageBytes);
		resourceData.setBodyHash(imageHash);
		resourceData.setSize(imageBytes.length);

		ResourceAttributes resourceAttributes = new ResourceAttributes();
		resourceAttributes.setFileName(imagePath);

		Resource resource = new Resource();
		resource.setMime(imageMimeType);
		resource.setAttributes(resourceAttributes);
		resource.setData(resourceData);
		
		return resource;
	}
	
	private void printExchange(Exchange exchange) {
		
		Object obj = exchange.getIn().getBody();
		if (obj instanceof UploadTransaction) {
			logger.debug("Found UploadTransaction in body of Exchange.");
			UploadTransaction upTx = (UploadTransaction)obj;
			logger.debug(upTx);
		} else if (obj instanceof List<?>) {
			logger.debug("Found List<?> in body of Exchange.");
		} else {
			logger.debug("Found '" + obj.getClass().getCanonicalName() + "' in body of Exchange.");
		}
	}
}
