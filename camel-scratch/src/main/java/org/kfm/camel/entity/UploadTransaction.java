/**
 * Created:  Dec 10, 2014 4:33:57 PM
 */
package org.kfm.camel.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.kfm.jpa.Document;

import com.evernote.edam.type.Notebook;
import com.livescribe.afp.Afd;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("upload-transaction")
public class UploadTransaction {

	@XStreamAlias("upload-time")
	private Calendar uploadTime;
	
	@XStreamAlias("afd")
	private Afd afd;
	
	@XStreamAlias("documents")
	private List<Document> documents = new ArrayList<Document>();
	
	@XStreamAlias("notebooks")
	private List<Notebook> notebooks = new ArrayList<Notebook>();
	
	@XStreamAlias("document-dtos")
	private List<DocumentDTO> documentDtos = new ArrayList<DocumentDTO>();
	
	/**
	 * <p></p>
	 * 
	 */
	public UploadTransaction() {
	}

	/**
	 * <p></p>
	 * 
	 * @param afd
	 */
	public void add(Afd afd) {
		this.afd = afd;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param document
	 */
	public void add(Document document) {
		this.documents.add(document);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param notebook
	 */
	public void add(Notebook notebook) {
		this.notebooks.add(notebook);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param documentDto
	 */
	public void add(DocumentDTO documentDto) {
		this.documentDtos.add(documentDto);
	}

	/**
	 * <p></p>
	 * 
	 * @return the afd
	 */
	public Afd getAfd() {
		return afd;
	}

	/**
	 * <p></p>
	 * 
	 * @return the documents
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * <p></p>
	 * 
	 * @return the notebooks
	 */
	public List<Notebook> getNotebooks() {
		return notebooks;
	}

	/**
	 * <p></p>
	 * 
	 * @return the documentDtos
	 */
	public List<DocumentDTO> getDocumentDtos() {
		return documentDtos;
	}

	/**
	 * <p></p>
	 * 
	 * @param afd the afd to set
	 */
	public void setAfd(Afd afd) {
		this.afd = afd;
	}

	/**
	 * <p></p>
	 * 
	 * @param documents the documents to set
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	/**
	 * <p></p>
	 * 
	 * @param notebooks the notebooks to set
	 */
	public void setNotebooks(List<Notebook> notebooks) {
		this.notebooks = notebooks;
	}

	/**
	 * <p></p>
	 * 
	 * @param documentDtos the documentDtos to set
	 */
	public void setDocumentDtos(List<DocumentDTO> documentDtos) {
		this.documentDtos = documentDtos;
	}

	/**
	 * <p></p>
	 * 
	 * @return the uploadTime
	 */
	public Calendar getUploadTime() {
		return uploadTime;
	}

	/**
	 * <p></p>
	 * 
	 * @param uploadTime the uploadTime to set
	 */
	public void setUploadTime(Calendar uploadTime) {
		this.uploadTime = uploadTime;
	}
}
