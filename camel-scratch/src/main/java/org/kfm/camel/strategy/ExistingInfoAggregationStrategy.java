/**
 * Created:  Dec 5, 2014 1:33:56 PM
 */
package org.kfm.camel.strategy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy;
import org.apache.log4j.Logger;
import org.kfm.camel.dao.PageDao;
import org.kfm.camel.entity.DocumentDTO;
import org.kfm.camel.entity.DocumentDTOFactory;
import org.kfm.camel.entity.ImageDTO;
import org.kfm.camel.entity.ImageDTOFactory;
import org.kfm.camel.entity.TemplateDTO;
import org.kfm.camel.entity.TemplateDTOFactory;
import org.kfm.camel.entity.UploadTransaction;
import org.kfm.camel.util.Utils;
import org.kfm.jpa.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.evernote.edam.type.Notebook;
import com.livescribe.afp.AFPException;
import com.livescribe.afp.Afd;
import com.livescribe.afp.GraphicsCollection;
import com.livescribe.afp.GraphicsElement;
import com.livescribe.afp.PageStroke;
import com.livescribe.afp.PageTemplate;
import com.livescribe.afp.Region;
import com.livescribe.afp.stf.STFSample;
import com.livescribe.afp.stf.STFStroke;
import com.livescribe.base.utils.Base64;
import com.livescribe.datatype.ValueMetaData;
import com.livescribe.datatype.basic.TypeFactory;
import com.livescribe.datatype.basic.ValueType;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.util.RegionID;
import com.livescribe.web.lssettingsservice.client.Setting;
import com.thoughtworks.xstream.XStream;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ExistingInfoAggregationStrategy implements AggregationStrategy, CompletionAwareAggregationStrategy {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private PageDao pageDao;
	
	/**
	 * <p></p>
	 * 
	 */
	public ExistingInfoAggregationStrategy() {
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.processor.aggregate.AggregationStrategy#aggregate(org.apache.camel.Exchange, org.apache.camel.Exchange)
	 */
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

		String method = "aggregate()";

//		if (oldExchange == null) {
//			
//			Object newObj = newExchange.getIn().getBody();
//			if (newObj != null) {
//				if (newObj instanceof Afd) {
//					logger.debug(method + " - oldExchange is 'null', and newExchange contains '" + newObj.getClass().getCanonicalName() + "'");
//					UploadTransaction upTx = getUploadTransaction(newObj);
//					try {
//						handleAfd((Afd)newObj, newExchange, upTx);
//					} catch (InvalidParameterException e) {
//						e.printStackTrace();
//					} catch (AFPException e) {
//						e.printStackTrace();
//					}
//				} else {
//					logger.debug(method + " - oldExchange is 'null', and newExchange contains '" + newObj.getClass().getCanonicalName() + "'");
//				}
//			}
//			return newExchange;
//		}
		
		Afd afd = null;
		UploadTransaction upTx = null;
		
		//--------------------------------------------------
		//	Get an UploadTransaction from the old Exchange body.  If no
		//	old Exchange is given, create a new UploadTransaction.
		Object oldBody = null;
		if (oldExchange != null) {
			oldBody = oldExchange.getIn().getBody();
		}
		upTx = getUploadTransaction(oldBody);
		
		Object newObj = newExchange.getIn().getBody();
		if (newObj != null) {

			logger.debug(method + " - newObj is an instance of '" + newObj.getClass().getCanonicalName() + "'.");
			
			Boolean isNewDocument = newExchange.getIn().getHeader("isNewDocument", Boolean.class);
			logger.debug(method + " - isNewDocument = " + isNewDocument);
			
			if (newObj instanceof List<?>) {
				List<?> list = (List<?>)newObj;
				
				if (!list.isEmpty()) {
					
					logger.debug(method + " - Number of items in the List: " + list.size());

					Object obj = list.get(0);
					
					//--------------------------------------------------
					//	Handle List of Documents from the database.
					if (obj instanceof Document) {
						logger.debug(method + " - Handling List<Document> ...");
						
						Document doc = (Document)obj;
						logger.debug(method + " - Found matching Document: " + doc.getDocName());
						upTx.add(doc);
						
						//	Add the Documents from the database to the upload transaction.
						for (int i = 1; i < list.size(); i++) {
							doc = (Document)list.get(i);
							upTx.add(doc);
						}
						
					//--------------------------------------------------
					//	Handle List of Notebooks from Evernote.
					} else if (obj instanceof Notebook) {
						logger.debug(method + " - Handling List<Notebook> ...");
						
						Notebook notebook = (Notebook)obj;
						upTx.add(notebook);
						
						for (int j = 1; j < list.size(); j++) {
							notebook = (Notebook)list.get(j);
							upTx.add(notebook);
						}
					} else {
						logger.debug(method + " - Found object of other type: " + obj.getClass().getCanonicalName());
					}
				} else {
					//	No Documents found in the database.
					if (isNewDocument != null) {
						if (isNewDocument) {
							logger.debug(method + " - No Documents found.  Upload is a new document.");
						} else {
							logger.error(method + " - No Documents found, but given List was empty.  (???)");
						}
					} else {
						logger.debug(method + " - isNewDocument header was 'null'.");
					}
				}
				
			//--------------------------------------------------
			//	Afd
			//--------------------------------------------------
			} else if (newObj instanceof Afd) {
				logger.debug(method + " - Handling Afd ...");
				
				afd = (Afd)newObj;
				
				try {
					handleAfd(afd, newExchange, upTx);
				} catch (InvalidParameterException e) {
					logger.error(method + " - InvalidParameterException thrown when handling Afd.", e);
					e.printStackTrace();
				} catch (AFPException e) {
					logger.error(method + " - AFPException thrown when handling Afd.", e);
					e.printStackTrace();
				}

			//--------------------------------------------------
			//	Setting
			//--------------------------------------------------
			} else if (newObj instanceof Setting) {
				logger.debug(method + " - Found Setting object.");
				Setting setting = (Setting)newObj;
				logger.debug(method + " - Pen name is '" + setting.getValue() + "'.");
				
				String penName = extractPenNameFromSetting(setting);
				newExchange.getIn().setHeader("penName", penName);

			//--------------------------------------------------
			//	Upload Transaction
			//--------------------------------------------------
			} else if (newObj instanceof UploadTransaction) {
				logger.error(method + " - Found UploadTransaction object.  (???)");
				upTx = (UploadTransaction)newObj;
				
			} else {
				logger.error(method + " - Unknown object found: " + newObj.getClass().getName());
			}
		} else {
			logger.error(method + " - newObj was 'null'.");
		}
		updateExchangeHeaders(oldExchange, newExchange);
		
		//	The UploadTransaction object is the result of this merge.  It
		//	should only appear in the old Exchange.  We set it here on the
		//	new Exchange because it will be provided as the old Exchange if
		//	this merge is not complete and another message comes in related
		//	to this merge.
		newExchange.getIn().setBody(upTx);
		
		if (isMergeComplete(newExchange)) {
			newExchange.getIn().setHeader("mergeComplete", true);
		}
		printHeaders(newExchange);
		return newExchange;
	}

	/**
	 * <p>Returns an <code>UploadTransaction</code> found within the given 
	 * <code>Object</code>, or creates a new one if one is not present.</p>
	 * 
	 * @param obj The object to extract the <code>UploadTransaction</code> 
	 * from, if present.
	 * 
	 * @return an <code>UploadTransaction</code> instance.
	 */
	private UploadTransaction getUploadTransaction(Object obj) {
		
		String method = "getUploadTransaction()";
		
		UploadTransaction upTx;
		if (obj != null) {
			if (obj instanceof UploadTransaction) {
				logger.debug(method + " - Found instance of UploadTransaction.");
				upTx = (UploadTransaction)obj;
			} else {
				logger.debug(method + " - Given obj is an instance of '" + obj.getClass().getCanonicalName() + "'.");
				upTx = new UploadTransaction();
			}
		} else {
			logger.error(method + " - oldObj was 'null'.");
			upTx = new UploadTransaction();
		}
		return upTx;
	}

	private void handleAfd(Afd afd, Exchange exchange, UploadTransaction upTx) throws InvalidParameterException, AFPException {
		
		String method = "handleAfd()";
		
		//	Save the AFD as part of the upload transaction.
		//	Assumes a single Afd at this time.
		if (upTx.getAfd() == null) {
			upTx.add(afd);
		}
		
		String uid = exchange.getIn().getHeader("uid", String.class);
		Long enUserId = exchange.getIn().getHeader("enUserId", Long.class);
		
		DocumentDTO documentDto = DocumentDTOFactory.create(afd);
		documentDto.setUid(uid);
		documentDto.setEnUserId(enUserId);
		
		//--------------------------------------------------
		//	Templates
		//--------------------------------------------------
		List<PageTemplate> afdPageTemplates = afd.getPageTemplates();
		logger.debug(method + " - Found " + afdPageTemplates.size() + " page templates in uploaded AFD.");
		
		for (PageTemplate apt : afdPageTemplates) {
//			if (!apt exists in DB) {
//				logger.debug(method + " - PageTemplate page number: " + apt.getPageNumber());
				Collection<Region> regions = apt.getRegions();
				TemplateDTO templateDto = TemplateDTOFactory.createDefaultTemplate();
				for (Region region : regions) {
					if (RegionID.getAppAreaId(region.getId()) == 0) {
						templateDto = TemplateDTOFactory.create(region, apt);
						break;
					}
				}
				
				//--------------------------------------------------
				//	Images
				//--------------------------------------------------
//				Collection<GraphicsElement> graphicsElements = apt.getGraphicsElements();
				GraphicsCollection gc = apt.getGraphicsCollection();
//				logger.debug(method + " - Found " + gc.size() + " graphics elements in PageTemplate #" + apt.getPageNumber());
				for (GraphicsElement ge : gc) {
					if (ge instanceof GraphicsElement.Image){ 
						ImageDTO imageDto = ImageDTOFactory.create(afd, ge);
						templateDto.add(imageDto);
						break;
					}
				}
				documentDto.add(templateDto);
//			}
		}
		upTx.add(documentDto);
		
		//--------------------------------------------------
		//	Strokes
		//--------------------------------------------------
		List<PageStroke> pageStrokes = afd.getPageStrokes();
		for (PageStroke pageStroke : pageStrokes) {
			TreeSet<STFStroke> stfStrokes = (TreeSet)pageStroke.getStrokes();
			for (STFStroke stfStroke : stfStrokes) {
				generateStrokeXmlHash(stfStroke);
			}
		}
	}
	
	/**
	 * <p></p>
	 * 
	 * @param exchange
	 * 
	 * @return
	 */
	private boolean isMergeComplete(Exchange exchange) {
		
		String method = "isMergeComplete()";
		
		String displayId = exchange.getIn().getHeader("displayId", String.class);
		String uid = exchange.getIn().getHeader("uid", String.class);
		String accessToken = exchange.getIn().getHeader("accessToken", String.class);
		String enUserId = exchange.getIn().getHeader("enUserId", String.class);
		Long penSerial = exchange.getIn().getHeader("penSerial", Long.class);
		Boolean isNewDocument = exchange.getIn().getHeader("isNewDocument", Boolean.class);
		String camelFileName = exchange.getIn().getHeader("CamelFileName", String.class);
		String penName = exchange.getIn().getHeader("penName", String.class);
		String stackName = exchange.getIn().getHeader("stackName", String.class);
		
		if ((displayId != null) && (!displayId.isEmpty()) &&
				(uid != null) && (!uid.isEmpty()) &&
				(accessToken != null) && (!accessToken.isEmpty()) &&
				(enUserId != null) && (!enUserId.isEmpty()) &&
				(penSerial != null) && (penSerial > 0) &&
				(camelFileName != null) && (!camelFileName.isEmpty()) &&
				(penName != null) && (!penName.isEmpty()) &&
				(stackName != null) && (!stackName.isEmpty()) &&
				(isNewDocument != null)) {
			
			logger.debug(method + " - Merge Complete!");
			
			return true;
		}
		logger.debug(method + " - Merge incomplete ...");
		printHeaders(exchange);
		
		return false;
	}

	/**
	 * <p></p>
	 * 
	 * @param setting
	 * @return
	 */
	private String extractPenNameFromSetting(Setting setting) {
		
		String settingValue = setting.getValue();
		String settingMeta = setting.getMeta();
		
		if (settingMeta == null || settingMeta.length() == 0 
				|| settingValue == null || settingValue.length() == 0) {
			return null;
		}
		
		String decoded = null;
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(Base64.decode(settingMeta.getBytes("UTF-8")));
			ValueMetaData meta = new ValueMetaData();
			meta.read(is);
			
			byte[] decodedBytes = Base64.decode(settingValue.getBytes("UTF-8"));
			decoded = new String(decodedBytes);
			logger.debug("extractPenNameFromSetting() - decoded = " + decoded.trim());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String penName = decoded.trim();
		
		return penName;
	}

	/**
	 * <p></p>
	 * 
	 * @param stfStroke
	 */
	private void generateStrokeXmlHash(STFStroke stfStroke) {
		
		XStream xstream = new XStream();
		xstream.processAnnotations(STFStroke.class);
		xstream.processAnnotations(STFSample.class);
		String stfXml = xstream.toXML(stfStroke);
		String stfXmlHash = null;
		try {
			stfXmlHash = Utils.toMD5HashString(stfXml);
		} catch (UnsupportedEncodingException e) {
			String msg = "UnsupportedEncodingException thrown";
		} catch (NoSuchAlgorithmException e) {
			String msg = "NoSuchAlgorithmException thrown";
		}
	}

	private void printHeaders(Exchange exchange) {
		
		String displayId = exchange.getIn().getHeader("displayId", String.class);
		String uid = exchange.getIn().getHeader("uid", String.class);
		String accessToken = exchange.getIn().getHeader("accessToken", String.class);
		String enUserId = exchange.getIn().getHeader("enUserId", String.class);
		Long penSerial = exchange.getIn().getHeader("penSerial", Long.class);
		Boolean isNewDocument = exchange.getIn().getHeader("isNewDocument", Boolean.class);
		String camelFileName = exchange.getIn().getHeader("CamelFileName", String.class);
		String penName = exchange.getIn().getHeader("penName", String.class);
		String stackName = exchange.getIn().getHeader("stackName", String.class);

		StringBuilder sb = new StringBuilder();
		sb.append("\n----------------------------------------------------------------------\n");
		sb.append("      displayId: ").append(displayId).append("\n");
		sb.append("            uid: ").append(uid).append("\n");
		sb.append("    accessToken: ").append(accessToken).append("\n");
		sb.append("       enUserId: ").append(enUserId).append("\n");
		sb.append("      penSerial: ").append(penSerial).append("\n");
		sb.append("  isNewDocument: ").append(isNewDocument).append("\n");
		sb.append("  CamelFileName: ").append(camelFileName).append("\n");
		sb.append("        penName: ").append(penName).append("\n");
		sb.append("      stackName: ").append(stackName).append("\n");
		sb.append("----------------------------------------------------------------------\n");
		
		logger.debug(sb.toString());
	}
	
	/**
	 * <p>Updates the headers on the <code>newExchange</code> with headers
	 * from the <code>oldExchange</code>.</p>
	 * 
	 * <p>If the old <code>Exchange</code> is <code>null</code>, this
	 * method simply returns without performing any operations.</p>
	 * 
	 * @param oldExchange
	 * @param newExchange
	 */
	private void updateExchangeHeaders(Exchange oldExchange, Exchange newExchange) {
		
		String method = "updateExchangeHeaders()";
		
		if (oldExchange == null) {
			return;
		}
		logger.debug(method + " - Updating headers ...");
		
		String displayId = oldExchange.getIn().getHeader("displayId", String.class);
		String uid = oldExchange.getIn().getHeader("uid", String.class);
		String accessToken = oldExchange.getIn().getHeader("accessToken", String.class);
		String enUserId = oldExchange.getIn().getHeader("enUserId", String.class);
		Long penSerial = oldExchange.getIn().getHeader("penSerial", Long.class);
		Boolean isNewDocument = oldExchange.getIn().getHeader("isNewDocument", Boolean.class);
		String camelFileName = oldExchange.getIn().getHeader("CamelFileName", String.class);
		String penName = oldExchange.getIn().getHeader("penName", String.class);
		String stackName = oldExchange.getIn().getHeader("stackName", String.class);
		
		//	Update the header value on the *old* Exchange only if there is a non-empty value.
		if ((displayId != null) && (!displayId.isEmpty())) {
			newExchange.getIn().setHeader("displayId", displayId);
		}
		if ((uid != null) && (!uid.isEmpty())) {
			newExchange.getIn().setHeader("uid", uid);
		}
		if ((accessToken != null) && (!accessToken.isEmpty())) {
			newExchange.getIn().setHeader("accessToken", accessToken);
		}
		if ((enUserId != null) && (!enUserId.isEmpty())) {
			newExchange.getIn().setHeader("enUserId", enUserId);
		}
		if (penSerial != null) {
			newExchange.getIn().setHeader("penSerial", penSerial);
		}
		if (isNewDocument != null) {
			logger.debug(method + " - Setting 'isNewDocument' header to: " + isNewDocument);
			newExchange.getIn().setHeader("isNewDocument", isNewDocument);
		}
		if (camelFileName != null) {
			newExchange.getIn().setHeader("CamelFileName", camelFileName);
		}
		if ((penName != null) && (!penName.isEmpty())) {
			newExchange.getIn().setHeader("penName", penName);
		}
		if ((stackName != null) && (!stackName.isEmpty())) {
			newExchange.getIn().setHeader("stackName", stackName);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy#onCompletion(org.apache.camel.Exchange)
	 */
	@Override
	public void onCompletion(Exchange exchange) {
		
		String method = "onCompletion()";
		
		printHeaders(exchange);
		logger.debug(method + " - Aggregation Complete!");
	}
}
//if ((notebooks != null) && (notebooks.size() > 0)) {
//
//
//if ((documents != null) && (!documents.isEmpty())) {
//	
//	//	For each uploaded Document ...
//	for (Document doc : documents) {
//		
//		String enNotebookGuid = doc.getEnNotebookGuid();
//		logger.debug(method + " - enNotebookGuid = " + enNotebookGuid);
//		
//		for (Notebook notebook : notebooks) {
//			String nbGuid = notebook.getGuid();
//			if (enNotebookGuid.equals(nbGuid)) {
//				logger.debug(method + " - Adding matching Notebook ... " + doc.getGuid());
//				matchingNotebooks.add(notebook);
//			}
//		}
//	}
//} else {
//	logger.info(method + " - No Documents found in given Exchange for UID = " + uid);
//}
//} else {
//logger.info(method + " - No Notebooks found in Evernote for UID = " + uid);
//}
//logger.debug(method + " - Found " + matchingNotebooks.size() + " matching Notebooks in Evernote.");
//if (matchingNotebooks.size() > 0) {
//	
//} else {
//	
//}
