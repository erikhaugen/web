/**
 * 
 */
package com.livescribe.framework.version.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class JarManifestNotFoundException extends Exception {

	/**
	 * <p></p>
	 *
	 */
	public JarManifestNotFoundException() {
		
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 */
	public JarManifestNotFoundException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 *
	 * @param cause
	 */
	public JarManifestNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 * @param cause
	 */
	public JarManifestNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
