/**
 * Created:  Oct 14, 2010 9:34:58 PM
 */
package com.livescribe.community.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.community.BaseTest;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SearchServiceTest extends BaseTest {

	public static final String FACET_AUTHOR_EMAIL		= "authorEmail";
	public static final String FACET_AUTHOR_LAST_NAME	= "authorLastName";
	public static final String FACETS_KEY				= "facets_key";
	public static final String DOC_LIST_KEY				= "doc_list_key";

	@Autowired
	private SearchService searchService;
	
	/**
	 * <p></p>
	 * 
	 */
	public SearchServiceTest() {
		super();
	}

	/**
	 * <p></p>
	 * 
	 */
	@Test
	public void testFindPencasts() {
		
		if (!skipTests) {
			ArrayList<String> facetList = new ArrayList<String>();
			facetList.add(FACET_AUTHOR_EMAIL);
			facetList.add(FACET_AUTHOR_LAST_NAME);
			
			String keywords = "sketch";
	
			Map<String, Object> results = searchService.findPencasts(keywords);
			
			Map<String, SortedMap<String, Long>> facetResults = (Map<String, SortedMap<String, Long>>)results.get(FACETS_KEY);
			
			assertEquals("Wrong number of facet results found.", 2, facetResults.size());
			
	//		Set frKeys = facetResults.keySet();
	//		Iterator<String> frKeyIter = frKeys.iterator();
	//		while (frKeyIter.hasNext()) {
	//			String ffKey = frKeyIter.next();
	//			SortedMap<String, Long> ffMap = facetResults.get(ffKey);
	//			Set ffKeys = ffMap.keySet();
	//			Iterator<String> ffKeyIter = ffKeys.iterator();
	//			while (ffKeyIter.hasNext()) {
	//				
	//			}
	//		}
		}
	}
}
