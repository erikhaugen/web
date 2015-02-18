/**
 * Created:  Mar 12, 2014 4:12:36 PM
 */
package org.kfm.camel.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kfm.jpa.Document;

import com.livescribe.afp.Afd;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("json-document")
public class DocumentDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7338519537315682861L;

	@XStreamAlias("id")
	private long id = 0; //documentId
	
	@XStreamAlias("guid")
	private String guid = "";
	
	@XStreamAlias("version")
	private String version = "";
	
	@XStreamAlias("copy")
	private long copy = Long.MIN_VALUE;
	
	@XStreamAlias("archive")
	private long archive = 0L;
	
	@XStreamAlias("uid")
	@XStreamOmitField
	private String uid = "";
	
	@XStreamAlias("enUserId")
	@XStreamOmitField
	private long enUserId = 0;

	@XStreamAlias("pen_display_id")
	@XStreamOmitField
	private String penDisplayId = "";

	@XStreamAlias("name")
	private String name = ""; //docName
	
	@XStreamAlias("evernote_notebook_guid")
	private String evernoteNotebookGuid = "";
	
	@XStreamAlias("penSerial")
	@XStreamOmitField
	private Long penSerial = null;
	
	@XStreamAlias("needdocument")
	private boolean needDocument = false;
	
	@XStreamAlias("lastaudiotime")
	private long lastAudioTime = 0;
	
	@XStreamAlias("lasttimemaptime")
	private long lastTimeMapTime = 0;

	@XStreamAlias("json-template")
	@XStreamImplicit
	private List<TemplateDTO> templates = new ArrayList<TemplateDTO>();
	
	@XStreamAlias("json-page")
	@XStreamImplicit
	private List<PageDTO> pages = new ArrayList<PageDTO>();
	
	@XStreamAlias("json-session")
	@XStreamImplicit
	private List<SessionDTO> sessions = new ArrayList<SessionDTO>();

	
	/**
	 * <p></p>
	 * 
	 */
	public DocumentDTO() {
	}

	/**
	 * <p></p>
	 * 
	 * @param afd
	 */
	public DocumentDTO(Afd afd) {
		if (afd != null) {
			this.guid = afd.getGuid();
			this.copy = (long)afd.getCopy();
		}
	}
	
	/**
	 * <p>Returns a persistable version of this <code>DocumentDTO</code> object.</p>
	 * 
	 * @return a persistable version of this <code>DocumentDTO</code> object.
	 */
	public Document toDocument() {
		
		Document document = new Document();
		document.setGuid(this.guid);
		document.setCopy(BigInteger.valueOf(this.copy));
		document.setArchive(BigInteger.valueOf(this.archive));
		document.setDocName(this.name);
		Date now = new Date();
		document.setCreated(now);
		document.setLastModified(now);
		document.setEnNotebookGuid(this.evernoteNotebookGuid);
		document.setEnUserId(BigInteger.valueOf(this.enUserId));
		document.setPenDisplayId(this.penDisplayId);
		document.setUid(this.uid);

		return document;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param pageDto
	 */
	public void add(PageDTO pageDto) {
		this.pages.add(pageDto);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param sessionDto
	 */
	public void add(SessionDTO sessionDto) {
		this.sessions.add(sessionDto);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param templateDto
	 */
	public void add(TemplateDTO templateDto) {
		this.templates.add(templateDto);
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the id
	 */
	public long getId() {
		return id;
	}


	/**
	 * <p></p>
	 * 
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}


	/**
	 * <p></p>
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}


	/**
	 * <p></p>
	 * 
	 * @return the copy
	 */
	public long getCopy() {
		return copy;
	}


	/**
	 * <p></p>
	 * 
	 * @return the archive
	 */
	public long getArchive() {
		return archive;
	}


	/**
	 * <p></p>
	 * 
	 * @return the user
	 */
	public String getUid() {
		return uid;
	}


	/**
	 * <p></p>
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * <p></p>
	 * 
	 * @return the evernoteNotebookGuid
	 */
	public String getEvernoteNotebookGuid() {
		return evernoteNotebookGuid;
	}


	/**
	 * <p></p>
	 * 
	 * @return the penDisplayId
	 */
	public String getPenDisplayId() {
		return penDisplayId;
	}


	/**
	 * <p></p>
	 * 
	 * @return the needDocument
	 */
	public boolean isNeedDocument() {
		return needDocument;
	}


	/**
	 * <p></p>
	 * 
	 * @return the lastAudioTime
	 */
	public long getLastAudioTime() {
		return lastAudioTime;
	}


	/**
	 * <p></p>
	 * 
	 * @return the lastTimeMapTime
	 */
	public long getLastTimeMapTime() {
		return lastTimeMapTime;
	}


	/**
	 * <p></p>
	 * 
	 * @return the templateList
	 */
	public List<TemplateDTO> getTemplates() {
		return templates;
	}


	/**
	 * <p></p>
	 * 
	 * @return the pageList
	 */
	public List<PageDTO> getPages() {
		return pages;
	}


	/**
	 * <p></p>
	 * 
	 * @return the sessionList
	 */
	public List<SessionDTO> getSessions() {
		return sessions;
	}


	/**
	 * <p></p>
	 * 
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}


	/**
	 * <p></p>
	 * 
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}


	/**
	 * <p></p>
	 * 
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	/**
	 * <p></p>
	 * 
	 * @param copy the copy to set
	 */
	public void setCopy(long copy) {
		this.copy = copy;
	}


	/**
	 * <p></p>
	 * 
	 * @param archive the archive to set
	 */
	public void setArchive(long archive) {
		this.archive = archive;
	}


	/**
	 * <p></p>
	 * 
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}


	/**
	 * <p></p>
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * <p></p>
	 * 
	 * @param evernoteNotebookGuid the evernoteNotebookGuid to set
	 */
	public void setEvernoteNotebookGuid(String evernoteNotebookGuid) {
		this.evernoteNotebookGuid = evernoteNotebookGuid;
	}


	/**
	 * <p></p>
	 * 
	 * @param penDisplayId the penDisplayId to set
	 */
	public void setPenDisplayId(String penDisplayId) {
		this.penDisplayId = penDisplayId;
	}


	/**
	 * <p></p>
	 * 
	 * @param needDocument the needDocument to set
	 */
	public void setNeedDocument(boolean needDocument) {
		this.needDocument = needDocument;
	}


	/**
	 * <p></p>
	 * 
	 * @param lastAudioTime the lastAudioTime to set
	 */
	public void setLastAudioTime(long lastAudioTime) {
		this.lastAudioTime = lastAudioTime;
	}


	/**
	 * <p></p>
	 * 
	 * @param lastTimeMapTime the lastTimeMapTime to set
	 */
	public void setLastTimeMapTime(long lastTimeMapTime) {
		this.lastTimeMapTime = lastTimeMapTime;
	}


	/**
	 * <p></p>
	 * 
	 * @param templateList the templateList to set
	 */
	public void setTemplates(List<TemplateDTO> templates) {
		this.templates = templates;
	}


	/**
	 * <p></p>
	 * 
	 * @param pageList the pageList to set
	 */
	public void setPages(List<PageDTO> pages) {
		this.pages = pages;
	}


	/**
	 * <p></p>
	 * 
	 * @param sessionList the sessionList to set
	 */
	public void setSessions(List<SessionDTO> sessions) {
		this.sessions = sessions;
	}

	/**
	 * <p></p>
	 * 
	 * @return the enUserId
	 */
	public long getEnUserId() {
		return enUserId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the penSerial
	 */
	public Long getPenSerial() {
		return penSerial;
	}

	/**
	 * <p></p>
	 * 
	 * @param enUserId the enUserId to set
	 */
	public void setEnUserId(long enUserId) {
		this.enUserId = enUserId;
	}

	/**
	 * <p></p>
	 * 
	 * @param penSerial the penSerial to set
	 */
	public void setPenSerial(Long penSerial) {
		this.penSerial = penSerial;
	}

}
