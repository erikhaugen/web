/**
 * Created:  Aug 13, 2013 4:45:33 PM
 */
package com.livescribe.web.registration.converter;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Converter
public class RequestToRegistrationConverter extends TypeConverterSupport {

	/**
	 * <p></p>
	 * 
	 */
	public RequestToRegistrationConverter() {
	}

	@Override
	public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
		
		String method = "convertTo()";
		
		return null;
	}

}
