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

import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import com.livescribe.community.view.vo.PencastVo;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;

/**
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CategoryListAtomFeedView extends AbstractAtomFeedView {

	private static String MV_NAME_CATEGORY_LIST	= "categoryList";
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.view.feed.AbstractAtomFeedView#buildFeedEntries(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		List<String> categoryList = (List<String>)model.get(MV_NAME_CATEGORY_LIST);
		List<Entry> entries = new ArrayList<Entry>();
		Iterator<String> catIter = categoryList.iterator();
		while (catIter.hasNext()) {
			String category = catIter.next();
			Entry entry = EntryFactory.createEntry(category, req);
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
		
		Date now = new Date();
		String dateStr = sdf.format(now);
		feed.setEncoding("UTF-8");
		feed.setId("tag:livescribe.com," + dateStr + ":/services/community/pencast/categories");
		feed.setTitle("Categories");
		feed.setUpdated(now);
	}
}
