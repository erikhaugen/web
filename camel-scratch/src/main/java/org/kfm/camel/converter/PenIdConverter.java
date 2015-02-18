/**
 * Created:  Dec 8, 2014 5:50:48 PM
 */
package org.kfm.camel.converter;

import org.apache.activemq.util.TypeConversionSupport;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;
import org.apache.log4j.Logger;

import com.livescribe.afp.AFPException;
import com.livescribe.afp.PenID;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
//@Converter
public class PenIdConverter extends TypeConverterSupport {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public PenIdConverter() {
	}

	
//	@Converter
	public Long fromString(String displayId) {
		
		try {
			PenID penId = new PenID(displayId);
			return Long.valueOf(penId.getId());
		} catch (AFPException ae) {
			ae.printStackTrace();
		}
		return null;
	}
	
//	@Converter
	public String toString(Long penSerialNumber) {
		
		PenID penId = new PenID(penSerialNumber);
		return penId.getSerial();
	}


	/* (non-Javadoc)
	 * @see org.apache.camel.TypeConverter#convertTo(java.lang.Class, org.apache.camel.Exchange, java.lang.Object)
	 */
	@Override
	public <T> T convertTo(Class<T> type, Exchange exchange, Object value)
			throws TypeConversionException {
		
		String method = "convertTo()";
		
		return null;
	}
}
