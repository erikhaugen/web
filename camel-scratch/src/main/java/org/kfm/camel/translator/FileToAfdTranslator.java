/**
 * Created:  Dec 3, 2014 6:05:56 PM
 */
package org.kfm.camel.translator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.livescribe.afp.AFPException;
import com.livescribe.afp.Afd;
import com.livescribe.afp.AfdFactory;
import com.livescribe.afp.DocumentException;
import com.livescribe.afp.DocumentInfo;
import com.livescribe.afp.PageAddress;
import com.livescribe.afp.PageStroke;
import com.livescribe.afp.PageTemplate;
import com.livescribe.afp.PatternChunk;
import com.livescribe.afp.PenID;
import com.livescribe.afp.SyncTimesInfo;
import com.livescribe.afp.stf.STFParser;
import com.livescribe.afp.stf.STFStroke;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class FileToAfdTranslator {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public FileToAfdTranslator() {
	}

	public Afd translate(Exchange exchange, File afdFile) {
		
		String method = "translate()";
		logger.debug(method + " - Reading File into Afd object.");
		
		String displayId = afdFile.getName().substring(0, 14);
		if (displayId != null) {
			long penSerial = -1L;
			try {
				PenID penId = new PenID(displayId);
				penSerial = penId.getId();
				exchange.getIn().setHeader("penSerial", penSerial);
			} catch (AFPException e) {
				e.printStackTrace();
			}
		} else {
			
		}
		
		Afd afd = AfdFactory.create(afdFile);
		
		return afd;
	}
}
