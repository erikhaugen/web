/**
 * Created:  Dec 12, 2014 2:20:56 PM
 */
package org.kfm.camel.entity;

import org.apache.log4j.Logger;

import com.livescribe.afp.AFPException;
import com.livescribe.afp.Afd;
import com.livescribe.afp.PenID;
import com.livescribe.framework.exception.InvalidParameterException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class DocumentDTOFactory {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p>Creates a new <code>DocumentDTO</code> object from the given 
	 * <code>Afd</code> object.</p>
	 * 
	 * Sets:
	 * <ul>
	 * <li>archive</li>
	 * <li>copy</li>
	 * <li>guid</li>
	 * <li>name</li>
	 * <li>penDisplayId</li>
	 * <li>penSerial</li>
	 * <li>version</li>
	 * </ul>
	 * 
	 * @param afd The Afd to use in creating the new DocumentDTO object.
	 * 
	 * @return a new DocumentDTO object.
	 * 
	 * @throws InvalidParameterException 
	 * @throws AFPException 
	 */
	public static DocumentDTO create(Afd afd) throws InvalidParameterException, AFPException {
		
		if (afd == null) {
			throw new InvalidParameterException();
		}
		
		DocumentDTO docDto = new DocumentDTO();
		docDto.setArchive(0);
		docDto.setCopy(afd.getCopy());
		docDto.setGuid(afd.getGuid());
		docDto.setName(afd.getTitle());
		String penDisplayId = afd.getPenDisplayId();
		PenID penId = new PenID(penDisplayId);
		long penSerial = penId.getId();
		docDto.setPenDisplayId(afd.getPenDisplayId());
		docDto.setPenSerial(penSerial);
		docDto.setVersion(afd.getVersion());
		
		return docDto;
	}
}
