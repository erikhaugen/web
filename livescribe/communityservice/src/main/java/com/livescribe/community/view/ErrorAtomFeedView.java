/**
 * 
 */
package com.livescribe.community.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import com.livescribe.community.view.vo.PencastVo;
import com.sun.syndication.feed.atom.Entry;

/**
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ErrorAtomFeedView extends AbstractAtomFeedView {

	private static String ATTRIB_ERROR			= "error";

	/**
	 * 
	 */
	public ErrorAtomFeedView() {
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.view.feed.AbstractAtomFeedView#buildFeedEntries(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<Entry> entries = new ArrayList<Entry>(); 
		
		//	Get the Model named 'error' for use in generating the feed.
		String error = (String)model.get(ATTRIB_ERROR);
		Entry entry = EntryFactory.createErrorEntry(error);
		entries.add(entry);
		
		return entries;
	}

}
