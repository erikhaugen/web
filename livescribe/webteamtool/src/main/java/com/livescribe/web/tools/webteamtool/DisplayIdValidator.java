/**
 * Created:  Jun 17, 2013 11:26:52 AM
 */
package com.livescribe.web.tools.webteamtool;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class DisplayIdValidator implements IParameterValidator {

	/**
	 * <p></p>
	 * 
	 */
	public DisplayIdValidator() {
		
	}

	/* (non-Javadoc)
	 * @see com.beust.jcommander.IParameterValidator#validate(java.lang.String, java.lang.String)
	 */
	public void validate(String name, String value) throws ParameterException {
		
		String[] parts = value.split("-");
		if (parts.length != 4) {
			throw new ParameterException("The '" + name + "' parameter must be formatted as:  XXX-XXX-XXX-XX");
		}
		
		if ((parts[0].length() != 3) || (parts[1].length() != 3) || (parts[2].length() != 3) || (parts[3].length() != 2)) {
			throw new ParameterException("The '" + name + "' parameter must be formatted as:  XXX-XXX-XXX-XX");
		}
	}
}
