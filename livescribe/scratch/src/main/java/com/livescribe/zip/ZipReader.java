/**
 * Created:  Apr 23, 2014 4:58:38 PM
 */
package com.livescribe.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ZipReader {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public ZipReader() {
	}

	/**
	 * <p></p>
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws ZipException 
	 */
	public static void main(String[] args) throws ZipException, IOException {

		String method = "main()";

		if (args.length < 1) {
			System.out.println("Must include path to ZIP file.");
			return;
		}
		
		File f = new File(args[0]);
		if (!f.exists()) {
			System.out.println("Unable to locate ZIP file:  " + args[0]);
			return;
		}
		ZipFile zipFile = new ZipFile(f);
		ZipEntry entry = zipFile.getEntry("synctimes.info");
		if (entry != null) {
			
			InputStream is = null;
			try {
				is = zipFile.getInputStream(entry);
			} catch (IOException ioe) {
				System.out.println("IOException thrown.");
				ioe.printStackTrace();
			}
		}
	}

}
