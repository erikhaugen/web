/**
 * 
 */
package com.livescribe.community.dto;

import java.io.File;

import org.dom4j.Document;

import com.livescribe.community.orm.UGFile;
import com.livescribe.community.util.FlashXmlParser;

/**
 * Factory class encapsulating the logic to create <code>Pencast<code> objects.
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PencastFactory {

//	private static Logger logger = Logger.getLogger(PencastFactory.class);

	/**
	 * <p>Creates new {@link com.livescribe.community.dto.Pencast} from a given 
	 * {@link com.livescribe.community.orm.UGFile}.</p>
	 * 
	 * <p>This method depends on an existing file structure where pencasts are stored.  
	 * The location of each pencast is obtained by this method from its <code>UGFile</code>
	 * record in the database.</p>
	 * 
	 * <p>If the file structure where pencasts are stored is changed, this method will
	 * need to be updated to correctly access the files.</p>
	 * 
	 * @param ugFile The file to transform into a pencast.
	 * 
	 * @return a new <code>Pencast</code> from the given <code>UGFile</code>.
	 */
	public static Pencast createPencast(UGFile ugFile) {

//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		Pencast pencast = new Pencast();

		pencast.setDescription(ugFile.getDescription());

		pencast.setNumberOfViews(ugFile.getViews());
		pencast.setRating(ugFile.getRating());
		pencast.setShortId(ugFile.getShortId());
		pencast.setFileDate(ugFile.getFileDate());

		//			Date fd = ugFile.getFileDate();
		//			String pubDate = sdf.format(fd);
		//			pencast.setPublishDate(pubDate);

		//	Take path to 'content.afd', strip off the filename, add 'derivative'
		//	and use the result as the path.
		String afdPath = ugFile.getFilePath();
		int idx = afdPath.indexOf("content.afd");
		String derivPath = afdPath.substring(0, idx) + "derivative";
		pencast.setDerivativePath(derivPath);

		String flashXmlPath = derivPath + "/flash.xml";
		File flashXmlFile = new File(flashXmlPath);
		FlashXmlParser parser = new FlashXmlParser();
		Document flashXmlDom = parser.parse(flashXmlFile);
		pencast.setFlashXmlDom(flashXmlDom);

		return pencast;
	}
}
