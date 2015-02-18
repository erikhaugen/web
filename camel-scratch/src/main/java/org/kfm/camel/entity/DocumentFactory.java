/**
 * Created:  Dec 22, 2014 12:44:33 PM
 */
package org.kfm.camel.entity;

import java.math.BigInteger;
import java.util.Date;

import org.apache.log4j.Logger;
import org.kfm.jpa.Document;

import com.livescribe.afp.Afd;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class DocumentFactory {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public static Document create(Afd afd, String uid, String penDisplayId, Long enUserId) {
		
		String afdGuid = afd.getGuid();
		String afdName = afd.getTitle();
		int afdCopy = afd.getCopy();

		Document document = new Document();
		document.setCopy(BigInteger.valueOf(afdCopy));
		document.setArchive(BigInteger.valueOf(0L));
		document.setDocName(afdName);
		document.setGuid(afdGuid);
		document.setUid(uid);
		document.setCreated(new Date());
		document.setPenDisplayId(penDisplayId);
		document.setEnUserId(BigInteger.valueOf(enUserId));
		
		return document;
	}
}
