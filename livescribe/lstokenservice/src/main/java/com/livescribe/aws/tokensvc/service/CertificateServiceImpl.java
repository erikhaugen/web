/*
 * Created:  Nov 11, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.aws.tokensvc.config.AppProperties;
import com.livescribe.aws.tokensvc.crypto.EncryptionUtils;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.framework.orm.manufacturing.PenCertificate;
import com.livescribe.framework.orm.manufacturing.PenCertificateDao;
import com.livescribe.framework.orm.manufacturing.PenDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Deprecated
public class CertificateServiceImpl implements CertificateService {

	private static final String PROP_CERTIFICATE_BASE_PATH	= "certificate.base.path";
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private EncryptionUtils encryptionUtils;
	
	@Autowired
	private PenDao penDao;
	
	@Autowired
	private PenCertificateDao penCertificateDao;
	
	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public CertificateServiceImpl() {
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.CertificateService#getPenCertificate(int)
	 */
	public Certificate getPenCertificate(int serialNumber) {
		
		String serialNumberStr = String.valueOf(serialNumber);
		
		//--------------------------------------------------
		//	Find the pen in the database.
		//--------------------------------------------------
		Pen p = new Pen();
		p.setSerialnumber(serialNumberStr);
		List<Pen> pens = penDao.findByExample(p);
		if ((pens == null) || (pens.isEmpty())) {
			//	TODO:
		}
		if (pens.size() > 1) {
			//	TODO:
		}
		Pen pen = pens.get(0);
		
		//--------------------------------------------------
		//	Find the pen's certificate details in the database.
		//--------------------------------------------------
		PenCertificate pc = new PenCertificate();
		Set<Pen> penSet = new TreeSet<Pen>();
		penSet.add(pen);
		pc.setPens(penSet);
		List<PenCertificate> penCerts = penCertificateDao.findByExample(pc);
		if ((penCerts == null) || (penCerts.isEmpty())) {
			//	TODO:
		}
		if (penCerts.size() > 1) {
			//	TODO:
		}
		
		//--------------------------------------------------
		//	Locate pen's certificate on filesystem.
		//--------------------------------------------------
		StringBuilder pathBuilder = encryptionUtils.hashValueFor(serialNumberStr);
		String certBasePath = appProperties.getProperty(PROP_CERTIFICATE_BASE_PATH);
		String certPath = certBasePath + "/" + pathBuilder.toString() + "/pen.crt";
		
		File file = new File(certPath);
		
		X509Certificate cert = null;
		try {
			cert = (X509Certificate)readCertificateFromFile(file);
		}
		catch (CertificateException ce) {
			ce.printStackTrace();
		}
		catch (IOException ioe) {
			Throwable cause = ioe.getCause();
			if (cause instanceof FileNotFoundException) {
				//	TODO:
			}
			ioe.printStackTrace();
		}
		
		return cert;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.CertificateService#readCertificateFromFile(java.lang.String)
	 */
	@Override
	public Certificate readCertificateFromFile(File file) throws CertificateException, IOException {
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		X509Certificate cert = null;
		while (in.available() > 0) {
			cert = (X509Certificate)factory.generateCertificate(in);
		}
		in.close();
		
		return cert;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.CertificateService#writeCertificateToFile(java.lang.String)
	 */
	@Override
	public void writeCertificateToFile(String filename) {
		

	}
}
