/*
 * Created:  Nov 11, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Deprecated
public interface CertificateService {

	/**
	 * <p></p>
	 * 
	 * @param serialNumber The decimal/numeric serial number of the pen.
	 * 
	 * @return an X.509 certificate.
	 */
	public Certificate getPenCertificate(int serialNumber);
	
	/**
	 * <p></p>
	 * 
	 * @param filename
	 * 
	 * @return
	 * 
	 * @throws CertificateException 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public Certificate readCertificateFromFile(File file) throws CertificateException, IOException;
	
	/**
	 * <p></p>
	 * 
	 * @param filename
	 */
	public void writeCertificateToFile(String filename);
}
