/**
 * 
 */
package com.livescribe.community.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.livescribe.community.orm.PencastAudio;
import com.livescribe.community.orm.PencastPage;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class FlashXmlParser {
	
	private static Logger logger = Logger.getLogger(FlashXmlParser.class.getName());
	
	/**
	 * <p></p>
	 */
	public FlashXmlParser() {
		
	}
	
	public static List<PencastAudio> getAudioClips(Document flashXmlDoc, String derivativePath) {
		
		logger.debug("getAudioClips():  Begin");
		
		ArrayList<PencastAudio> list = new ArrayList<PencastAudio>();
		
		List nodes = flashXmlDoc.selectNodes("/pencast/audio");
		logger.debug("getAudioClips():  Found " + nodes.size() + " 'audio' nodes.");

		Iterator<Node> nodeIter = nodes.iterator();
		while (nodeIter.hasNext()) {
			Node node = nodeIter.next();
			PencastAudio audio = new PencastAudio();
			
			String bt = node.valueOf("@begintime");
			audio.setBeginTime(Long.parseLong(bt));
			
			String dur = node.valueOf("@duration");
			audio.setDuration(Long.parseLong(dur));
			
			audio.setFilePath(node.valueOf("@src"));
			
			String fs = node.valueOf("@begintime");
			audio.setFileSize(Long.parseLong(fs));
			
			audio.setFileUrl(node.valueOf("@src"));
			logger.debug("Audio URL:  " + node.valueOf("@src"));
			
			audio.setType(node.valueOf("@type"));
			
			list.add(audio);
		}
		
		return list;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param flashXmlDoc
	 * 
	 * @return
	 */
	public static List<PencastPage> getPages(Document flashXmlDoc, String derivativePath) {
		
		logger.debug("getPages():  Begin");
		
		ArrayList<PencastPage> list = new ArrayList<PencastPage>();
		
		List nodes = flashXmlDoc.selectNodes("/pencast/page");
		logger.debug("getPages():  Found " + nodes.size() + " 'page' nodes.");
		
		Iterator<Node> nodeIter = nodes.iterator();
		while (nodeIter.hasNext()) {
			Node node = nodeIter.next();
			PencastPage page = new PencastPage();
			
			String idStr = node.valueOf("@id");
			int id = Integer.parseInt(idStr);
			page.setPageId(id);
			
			page.setLabel(node.valueOf("@label"));
			
			String wStr = node.valueOf("@width");
			int width = Integer.parseInt(wStr);
			page.setWidth(width);
			
			String hStr = node.valueOf("@height");
			int height = Integer.parseInt(hStr);
			page.setHeight(height);
			
			page.setBackground(node.valueOf("@background"));
			page.setFormat(node.valueOf("coordinates/@format"));
			
			String fSize = node.valueOf("coordinates/@filesize");
			long fileSize = Long.parseLong(fSize);
			page.setFileSize(fileSize);
			
			String pagePath = derivativePath + "/page" + id + ".pcc"; 
			page.setFilePath(pagePath);
			
			String pageUrl = node.valueOf("coordinates/@src");
			page.setFileUrl(pageUrl);
			logger.debug("Page URL:  " + pageUrl);
			
			File f = new File(pagePath);
			if (f.exists()) {
				page.setExists(true);
			}
			else {
				page.setExists(false);
			}
				
			list.add(page);
		}
		return list;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param file
	 * 
	 * @return
	 */
	public static Document parse(File file) {
		
		String method = "parse():  ";
		
		Document document = null;
		
		try {
			FileInputStream stream = new FileInputStream(file);
			SAXReader reader = new SAXReader();
			document = reader.read(stream);
			return document;
		} 
		catch (FileNotFoundException fnfe) {
			logger.error(method = "FileNotFoundException thrown.  " + fnfe);
		} 
		catch (DocumentException de) {
			logger.error(method = "DocumentException thrown.  " + de);
		}
		return document;
	}
}
