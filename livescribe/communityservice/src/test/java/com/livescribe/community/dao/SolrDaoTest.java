/**
 * Created:  Oct 14, 2010 7:34:03 PM
 */
package com.livescribe.community.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.community.BaseTest;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SolrDaoTest extends BaseTest {

	public static final String FACET_AUTHOR_EMAIL		= "authorEmail";
	public static final String FACET_AUTHOR_LAST_NAME	= "authorLastName";

	@Autowired
	private SolrDao solrDao;
	
	/**
	 * <p></p>
	 * 
	 */
	public SolrDaoTest() {
		super();
	}

	/**
	 * <p></p>
	 * 
	 */
	@Test
	public void testPerformQuery() {
		
		if (!skipTests) {
			ArrayList<String> facetList = new ArrayList<String>();
			facetList.add(FACET_AUTHOR_EMAIL);
			facetList.add(FACET_AUTHOR_LAST_NAME);
			
			String keywords = "sketch";
			
			QueryResponse resp = solrDao.performQuery(keywords, facetList);
			
			assertNotNull("The returned response was 'null'.");
			
			List<FacetField> facetFields = resp.getFacetFields();
			
			int ffSize = facetFields.size();
			
			assertEquals("Wrong number of facet responses found.", 2, ffSize);
		}
	}
	
}
