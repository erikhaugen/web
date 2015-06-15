package com.livescribe.community.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

import com.livescribe.base.StringUtils;
import com.livescribe.community.CommunityConstants;
import com.livescribe.community.orm.PencastAudio;
import com.livescribe.community.orm.PencastPage;
import com.livescribe.base.utils.WOAppMigrationUtils;
import com.livescribe.community.view.vo.PencastVo;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;

/**
 * <p>Creates an Atom <code>Entry</code> node compliant with the Atom 1.0 spec.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EntryFactory {
	
	public static Logger logger = Logger.getLogger(EntryFactory.class.getName());
	
	public static String REST_PATH_PENCAST = "/services/community/pencast";
	
	//	Entry.setForeignMarkup(< JDOM element >);   <-- For 'duration' info
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public EntryFactory() {}

	/**
	 * <p></p>
	 * 
	 * @param categoryName
	 * @param req
	 * 
	 * @return
	 */
	public static Entry createEntry(String categoryName, HttpServletRequest req) {
		
		Entry entry = new Entry();
		entry.setTitle(categoryName);
		
		//	Create the Href to the getPencastsByCategory() method.
		StringBuilder builder = new StringBuilder();
		String host = req.getServerName();
		int port = req.getServerPort();

		String hostString = "http://";
		if (port == 80) {
//			builder.append("http://" + host);
			hostString = hostString + host;
		}
		else {
			hostString = hostString + host + ":" + port;
//			builder.append("http://" + host + ":" + port);
		}
		builder.append(hostString);

//		builder.append("http://" + host + ":" + port);
		builder.append(REST_PATH_PENCAST + "/category/" + categoryName + ".atom");
		
		//	Create Link to getPencastsByCategory() method.
		Link link = new Link();
		link.setRel("self");
		link.setHref(builder.toString());
		link.setType(CommunityConstants.LINK_TYPE_ATOM);
		
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(link);

		entry.setOtherLinks(links);
		
		return entry;
	}
	
	/**
	 * <p>Creates an <code>&lt;Entry&gt;</code> node for use in an Atom feed.</p>
	 * 
	 * @param pencast The pencast to use in creating the <code>Entry</code>.
	 * 
	 * @return an <code>&lt;Entry&gt;</code> node for use in an Atom feed.
	 */
	public static Entry createEntry(PencastVo pencast, HttpServletRequest req) {
		
		logger.debug("createEntry():  Begin");
		
		Entry entry = new Entry();
		String entryId = createEntryId(pencast);
		entry.setId(entryId);
		
		String title = pencast.getDisplayName();
		String xmlTitle = StringUtils.removeInvalidXMLCharacters(title);
		entry.setTitle(xmlTitle);
		
		entry.setPublished(pencast.getFileDate());
		
		//	Build the URL to the 'flash.xml' file.
		StringBuilder builder = new StringBuilder();
		String host = req.getServerName();
		int port = req.getServerPort();
		
		String hostString = "http://";
		if (port == 80) {
			hostString = hostString + host;
		}
		else {
			hostString = hostString + host + ":" + port;
		}
		builder.append(hostString);
		builder.append("/cgi-bin/WebObjects/LDApp.woa/wa/flashXML?xml=");
		byte[] primaryKey = pencast.getPrimaryKey();
		String pkString = WOAppMigrationUtils.convertPrimaryKeyToString(primaryKey);
		builder.append(pkString);
					
		//	Build the URL to the HTML overview page.
		StringBuilder htmlUrlBuilder = new StringBuilder();
		htmlUrlBuilder.append(hostString);
		htmlUrlBuilder.append("/cgi-bin/WebObjects/LDApp.woa/wa/MLSOverviewPage?sid=");
		htmlUrlBuilder.append(pencast.getShortId());
		
		//	Build the URL to the thumbnail image.
		StringBuilder thumbUrlBuilder = new StringBuilder();
		thumbUrlBuilder.append(hostString);
		thumbUrlBuilder.append("/cgi-bin/WebObjects/LDApp.woa/wa/pencastPreview?sid=");
		thumbUrlBuilder.append(pencast.getShortId());
		pencast.setThumbnailUrl(thumbUrlBuilder.toString());
		
		//	Build the URL to the alternate thumbnail image.
		StringBuilder altThumbUrlBuilder = new StringBuilder();
		altThumbUrlBuilder.append(hostString);
		altThumbUrlBuilder.append("/images/icons/default_thumb_small.gif");
		
		//	Create the <link> to the flash.xml file.
		Link selfLink = new Link();
		selfLink.setHref(builder.toString());
		selfLink.setRel("self");
		selfLink.setType(CommunityConstants.LINK_TYPE_PCC);
		
		//	Create the <link> to the HTML overview page.
		Link htmlLink = new Link();
		htmlLink.setHref(htmlUrlBuilder.toString());
		htmlLink.setRel("alternate");
		htmlLink.setType(CommunityConstants.LINK_TYPE_HTML);
		
		//	Create the <link> to the thumbnail image.
		Link thumbLink = new Link();
		thumbLink.setHref(thumbUrlBuilder.toString());
		thumbLink.setRel("thumbnail");
		thumbLink.setType(CommunityConstants.LINK_TYPE_GIF);
		
		//	Create the <link> to the alternate thumbnail image.
		Link altThumbLink = new Link();
		altThumbLink.setHref(altThumbUrlBuilder.toString());
		altThumbLink.setRel("altthumb");
		altThumbLink.setType(CommunityConstants.LINK_TYPE_GIF);
		
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(selfLink);
		links.add(htmlLink);
		links.add(thumbLink);
		links.add(altThumbLink);

		//	Create the <link> to the audio files.
		List<PencastAudio> audioClips = pencast.getAudioClips();
		Iterator<PencastAudio> audioIter = audioClips.iterator();
		while (audioIter.hasNext()) {
			Link audioLink = new Link();
			PencastAudio pa = audioIter.next();
			String url = pa.getFileUrl();
			audioLink.setHref(hostString + url);
			audioLink.setRel("audio");
			audioLink.setType("audio/x-mp4");
			links.add(audioLink);
		}
		
		//	Create the <link> to the page files.
		List<PencastPage> pages = pencast.getPages();
		Iterator<PencastPage> pageIter = pages.iterator();
		while (pageIter.hasNext()) {
			Link pageLink = new Link();
			PencastPage pp = pageIter.next();
			String url = pp.getFileUrl();
			pageLink.setHref(hostString + url);
			pageLink.setRel("strokes");
			pageLink.setType("application/pcc");
			links.add(pageLink);
		}
		
		entry.setOtherLinks(links);
		
		//	Create the Summary of the Entry.
		Content content = new Content();
		content.setType(CommunityConstants.LINK_TYPE_HTML);
		String desc = pencast.getDescription();
		String xmlDescription = StringUtils.removeInvalidXMLCharacters(desc);
		content.setValue(xmlDescription);
		entry.setSummary(content);
		
		//	Add the Author of the pencast.
		ArrayList<Person> authors = new ArrayList<Person>();
		Person author = new Person();
		author.setEmail(pencast.getAuthorEmail());
		String name = pencast.getAuthorFirstName() + " " + pencast.getAuthorLastName();
		String xmlName = StringUtils.removeInvalidXMLCharacters(name);
		author.setName(xmlName);
		authors.add(author);
		entry.setAuthors(authors);
		
//		PencastModule module = createPencastModule(pencast);
		Element durationElement = new Element("duration");
		Long duration = pencast.getAudioDuration();
		logger.debug("duration = " + duration);
		durationElement.setText(duration.toString());
		
		ArrayList<Element> elements = new ArrayList<Element>();
		elements.add(durationElement);
		
		entry.setForeignMarkup(elements);
		
		//	Set the 'last updated' date to right now.
//		Date now = new Date();
//		entry.setUpdated(now);
		
//		logger.debug(entry.toString());
		
		return entry;
	}

	/**
	 * @param pencast
	 * @return
	 */
	private static PencastModule createPencastModule(PencastVo pencast) {
		
		ArrayList<PencastModule> modules = new ArrayList<PencastModule>();
		PencastModule module = new PencastModuleImpl();
		
		ArrayList<String> audioUrls = new ArrayList<String>();
		ArrayList<String> pageUrls = new ArrayList<String>();
		
		//	Add audio references.
		List<PencastAudio> audios = pencast.getAudioClips();
		Iterator<PencastAudio> audioIter = audios.iterator();
		while (audioIter.hasNext()) {
			PencastAudio audio = audioIter.next();
			String audioUrl = audio.getFileUrl();
			logger.debug("Adding audio URL:  " + audioUrl);
			audioUrls.add(audioUrl);
		}
//		module.setAudio(audios);
		
		//	Add page references.
		List<PencastPage> pages = pencast.getPages();
		Iterator<PencastPage> pageIter = pages.iterator();
		while (pageIter.hasNext()) {
			PencastPage page = pageIter.next();
			String pageUrl = page.getFileUrl();
			logger.debug("Adding page URL:  " + pageUrl);
			pageUrls.add(pageUrl);
		}
//		module.setStrokes(pageUrls);
		
		module.setDuration(pencast.getAudioDuration());
		
//		modules.add(module);
		
		return module;
	}

	private static String createEntryId(PencastVo pencast) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("tag:livescribe.com,");
		
		Date pubDate = pencast.getFileDate();
		String dateStr = sdf.format(pubDate);
		builder.append(dateStr + ":");
		builder.append(REST_PATH_PENCAST);
		builder.append("/" + pencast.getShortId());
		
		return builder.toString();
	}
	
	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public static Link createEnclosureLink() {
		
		Link enclosure = new Link();
		
//		enclosure.setHref(href);
//		enclosure.setRel(rel);
//		enclosure.setLength(length);
		
		return enclosure;
	}
	
	public static Entry createErrorEntry(String error) {
		
		Entry entry = new Entry();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("tag:livescribe.com,");
		
		Date now = new Date();
		String dateStr = sdf.format(now);
		builder.append(dateStr + ":");
		builder.append(REST_PATH_PENCAST + "/error/");
		String entryId = builder.toString();
		entry.setId(entryId);
		
		entry.setTitle("Error");
		entry.setPublished(now);

		//	Create the Summary of the Entry.
		Content content = new Content();
		content.setType(CommunityConstants.LINK_TYPE_HTML);
//		String escapedDesc = StringEscapeUtils.escapeJavaScript(error);
//		content.setValue(escapedDesc);
		content.setValue(error);
		entry.setSummary(content);
		
		return entry;
	}
}
