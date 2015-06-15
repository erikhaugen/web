/**
 * 
 */
package com.livescribe.community.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import com.livescribe.base.StringUtils;
import com.livescribe.community.CommunityConstants;
import com.livescribe.community.config.ConfigClient;
import com.livescribe.community.view.vo.PencastVo;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CommunityAtomFeedView extends AbstractAtomFeedView {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static String PARAM_START		= "start";
	private static String PARAM_FETCH_SIZE	= "fetchSize";
	private static int DEFAULT_START_VALUE 	= 0;
	private static int DEFAULT_FETCH_SIZE 	= ConfigClient.getDefaultFetchSize();
	private static int MAX_FETCH_SIZE = ConfigClient.getMaxFetchSize();
	private static String REST_PATH_PENCAST	= "/services/community/pencast";
	private static String REL_TYPE_FIRST	= "first";
	private static String REL_TYPE_PREV		= "previous";
	private static String REL_TYPE_NEXT		= "next";
	private static String REL_TYPE_LAST		= "last";
	
	@Override
	protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		String method = "buildFeedEntries():  ";
				
		//	Get the Model named 'pencastList' for use in generating the feed.
		List<PencastVo> pencastList = (List<PencastVo>)model.get("pencastList");
		
		List<Entry> entries = new ArrayList<Entry>(); 
		Iterator<PencastVo> pcIter = pencastList.iterator();
		while (pcIter.hasNext()) {
			PencastVo pencast = pcIter.next();
			Entry entry = EntryFactory.createEntry(pencast, req);
			entries.add(entry);
		}
		return entries;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.view.feed.AbstractFeedView#buildFeedMetadata(java.util.Map, com.sun.syndication.feed.WireFeed, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void buildFeedMetadata(Map model, Feed feed, HttpServletRequest request) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuilder hrefBuilder = new StringBuilder();
		
		String host = request.getServerName();
		int port = request.getServerPort();
		
		StringBuilder hostBuilder = new StringBuilder("http://");
		hostBuilder.append(host);
		
		//	If we are testing internally, use the specific port.
		if (port != 80) {
			hostBuilder.append(":" + port);
		}
		
		hrefBuilder.append(hostBuilder.toString());
		
		String path = request.getRequestURI();

		logger.debug("URI path: " + path);
		
		hrefBuilder.append(REST_PATH_PENCAST);
		
//		hrefBuilder.append(path);
		
		int pcstIdx = path.indexOf("/pencast") + 8;
		String endOfPath = path.substring(pcstIdx);
		
		logger.debug("End of path: " + endOfPath);
		
		hrefBuilder.append(endOfPath);
		
		//	Build the 'start' and 'fetchSize' query string.
		String startParam = request.getParameter(PARAM_START);
		String fetchSizeParam = request.getParameter(PARAM_FETCH_SIZE);
		int start = DEFAULT_START_VALUE;
		int fetchSize = DEFAULT_FETCH_SIZE;
		
		//	Capture the 'start' and 'fetchSize' parameters.
		try {
			if ( StringUtils.isNotBlank(startParam)  && StringUtils.isInteger(startParam) ) {
				start = Integer.parseInt(startParam);
			}
			if ( StringUtils.isNotBlank(fetchSizeParam)  && StringUtils.isInteger(fetchSizeParam) ) {
				fetchSize = Integer.parseInt(fetchSizeParam);
				if ( fetchSize > MAX_FETCH_SIZE ) {
					fetchSize = MAX_FETCH_SIZE;
				}
			}
			
		}
		catch (NumberFormatException nfe) {
			logger.error("NumberFormatException thrown while attempting to convert 'start' or 'fetchSize' parameters.  Defaults used.");
			nfe.printStackTrace();
		}
		
		Date now = new Date();
		String dateStr = sdf.format(now);
		feed.setEncoding("UTF-8");
		feed.setId("tag:livescribe.com," + dateStr + ":" + REST_PATH_PENCAST);
		feed.setTitle("Pencasts");
		feed.setUpdated(now);
		
		//	First
		Link firstLink = new Link();
		String firstQStr = "?" + PARAM_START + "= 0&" + PARAM_FETCH_SIZE + "=" + DEFAULT_FETCH_SIZE;
		firstLink.setHref(hrefBuilder.toString() + firstQStr);
		firstLink.setRel(REL_TYPE_FIRST);
		firstLink.setTitle("First");
		
		//	Previous
		int prevStart = 0;
		if (start > fetchSize) {
			prevStart = start - fetchSize;
		}
		Link prevLink = new Link();
		String prevQStr = "?" + PARAM_START + "=" + prevStart + "&" + PARAM_FETCH_SIZE + "=" + DEFAULT_FETCH_SIZE;
		prevLink.setHref(hrefBuilder.toString() + prevQStr);
		prevLink.setRel(REL_TYPE_PREV);
		prevLink.setTitle("Previous");

		int nextStart = start + fetchSize;
		String nextQStr = "?" + PARAM_START + "=" + nextStart + "&" + PARAM_FETCH_SIZE + "=" + fetchSize;
		
		//	Next
		Link nextLink = new Link();
		nextLink.setHref(hrefBuilder.toString() + nextQStr);
		nextLink.setRel(REL_TYPE_NEXT);
		nextLink.setTitle("Next");
		
		//	Don't know where the last 'chunk' is at this point of execution.
//		Link lastLink = new Link();
//		lastLink.setHref(hostBuilder.toString() + REST_PATH_PENCAST + path);
//		lastLink.setRel(REL_TYPE_LAST);
//		lastLink.setTitle("Last");
		
		ArrayList<Link> feedLinks = new ArrayList<Link>();
		feedLinks.add(firstLink);
		if (start != 0) {
			feedLinks.add(prevLink);
		}

		//	Get the size of the pencast list and determine if there will be more
		//	pencasts to return in a subsequent request.  If not, then don't add
		//	the 'next' link to the feed.
		List<PencastVo> pencastList = (List<PencastVo>)model.get("pencastList");
		if ((pencastList != null) && (pencastList.size() == fetchSize)) {
			feedLinks.add(nextLink);
		}
//		feedLinks.add(lastLink);
		
		feed.setOtherLinks(feedLinks);
		
//		List<Pencast> pencastList = (List<Pencast>)model.get("pencastList");
//		Iterator<Pencast> pcIter = pencastList.iterator();
//		while (pcIter.hasNext()) {
//			Pencast pencast = pcIter.next();
//			Date date = pencast.getFileDate();
//		}
//			Date date = content.getPublicationDate();
//			if (feed.getUpdated() == null || date.compareTo(feed.getUpdated()) > 0) {
//					feed.setUpdated(date);
//			}
//		}
	}

	/**
	 * <p>Returns &quot;application/pcc+xml&quot; as the MIME type of this feed.</p>
	 * 
	 * @see org.springframework.web.servlet.view.AbstractView#getContentType()
	 */
	@Override
	public String getContentType() {
		
		return CommunityConstants.LINK_TYPE_ATOM;
	}
}
