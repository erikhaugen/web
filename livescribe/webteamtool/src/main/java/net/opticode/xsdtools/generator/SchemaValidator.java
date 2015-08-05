/**
 * Created:  Feb 6, 2014 8:35:12 PM
 */
package net.opticode.xsdtools.generator;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SchemaValidator extends Validator {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public SchemaValidator() {
	}

	/* (non-Javadoc)
	 * @see javax.xml.validation.Validator#getErrorHandler()
	 */
	@Override
	public ErrorHandler getErrorHandler() {

		String method = "getErrorHandler()";

		return null;
	}

	/* (non-Javadoc)
	 * @see javax.xml.validation.Validator#getResourceResolver()
	 */
	@Override
	public LSResourceResolver getResourceResolver() {

		String method = "getResourceResolver()";

		return null;
	}

	/* (non-Javadoc)
	 * @see javax.xml.validation.Validator#reset()
	 */
	@Override
	public void reset() {

		String method = "reset()";

	}

	/* (non-Javadoc)
	 * @see javax.xml.validation.Validator#setErrorHandler(org.xml.sax.ErrorHandler)
	 */
	@Override
	public void setErrorHandler(ErrorHandler arg0) {

		String method = "setErrorHandler()";

	}

	/* (non-Javadoc)
	 * @see javax.xml.validation.Validator#setResourceResolver(org.w3c.dom.ls.LSResourceResolver)
	 */
	@Override
	public void setResourceResolver(LSResourceResolver arg0) {

		String method = "setResourceResolver()";

	}

	/* (non-Javadoc)
	 * @see javax.xml.validation.Validator#validate(javax.xml.transform.Source, javax.xml.transform.Result)
	 */
	@Override
	public void validate(Source arg0, Result arg1) throws SAXException,
			IOException {

		String method = "validate()";

	}

}
