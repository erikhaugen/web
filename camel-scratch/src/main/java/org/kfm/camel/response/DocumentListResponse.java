/**
 * Created:  Mar 12, 2014 5:33:12 PM
 */
package org.kfm.camel.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kfm.camel.entity.DocumentDTO;

import com.livescribe.framework.web.response.ResponseCode;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class DocumentListResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5062223685883459809L;

	@XStreamAlias("responseCode")
	private ResponseCode responseCode;

	private List<DocumentDTO> documents = new ArrayList<DocumentDTO>();
	
	/**
	 * <p></p>
	 * 
	 */
	public DocumentListResponse() {
	}

	/**
	 * <p></p>
	 * 
	 * @param documentDto
	 */
	public void add(DocumentDTO documentDto) {
		this.documents.add(documentDto);
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the documents
	 */
	public List<DocumentDTO> getDocuments() {
		return documents;
	}

	/**
	 * <p></p>
	 * 
	 * @param documents the documents to set
	 */
	public void setDocuments(List<DocumentDTO> documents) {
		this.documents = documents;
	}

	/**
	 * <p></p>
	 * 
	 * @return the responseCode
	 */
	public ResponseCode getResponseCode() {
		return responseCode;
	}

	/**
	 * <p></p>
	 * 
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

}
