/**
 * Created:  Oct 13, 2010 1:44:04 PM
 */
package com.livescribe.community.service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.community.dao.SolrDao;
import com.livescribe.community.view.vo.PencastVo;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SearchService extends BaseService {
	
	public static final String FACET_AUTHOR_EMAIL		= "authorEmail";
	public static final String FACET_AUTHOR_LAST_NAME	= "authorLastName";
	public static final String FACETS_KEY				= "facets_key";
	public static final String DOC_LIST_KEY				= "doc_list_key";
	
	@Autowired
	private SolrDao solrDao;
	
	/**
	 * <p></p>
	 * 
	 */
	public SearchService() {
		super();		
	}

	/**
	 * <p></p>
	 * 
	 * @param keywords
	 * @return
	 */
	public Map<String, Object> findPencasts(String keywords) {
		
		String method = "findPencasts():  ";
		
		ArrayList<String> facetList = new ArrayList<String>();
		facetList.add(FACET_AUTHOR_EMAIL);
		facetList.add(FACET_AUTHOR_LAST_NAME);
		
		logger.debug(method + "About to performQuery().");
		
		QueryResponse resp = solrDao.performQuery(keywords, facetList);
		
		logger.debug(method + "After performQuery().");
		
		SolrDocumentList docs = null;
		HashMap<String, Object> resultMap = null;
		
		if (resp != null) {

			resultMap = new HashMap<String, Object>();

			HashMap<String, SortedMap<String, Long>> facetResultsMap = new HashMap<String, SortedMap<String, Long>>();
			List<FacetField> facetFields = resp.getFacetFields();
			if (facetFields != null) {
				Iterator<FacetField> ffIter = facetFields.iterator();
				while (ffIter.hasNext()) {
					FacetField ff = ffIter.next();
					String ffName = ff.getName();
		//			logger.debug(method + "ffName = '" + ffName + "'");
					SortedMap<String, Long> facetMap = getFacetMap(ff);
					facetResultsMap.put(ffName, facetMap);
				}
			}
			docs = resp.getResults();
			resultMap.put(FACETS_KEY, facetResultsMap);
			resultMap.put(DOC_LIST_KEY, docs);
		}
		else {
			//	No results found.  An empty Map will be returned.
		}
		return resultMap;
	}

	/**
	 * <p></p>
	 * 
	 * @param ff
	 */
	private SortedMap<String, Long> getFacetMap(FacetField ff) {
		
		TreeMap<String, Long> facetMap = new TreeMap<String, Long>();
		String name = ff.getName();
		int count = ff.getValueCount();
		if (count > 0) {
			List<FacetField.Count> values = ff.getValues();
			Iterator<FacetField.Count> ffvIter = values.iterator();
			while (ffvIter.hasNext()) {
				FacetField.Count ffv = ffvIter.next();
				String ffvName = ffv.getName();
				long ffvCount = ffv.getCount();
				facetMap.put(ffvName, ffvCount);
			}
		}
		return facetMap;
	}


	/**
	 * <p></p>
	 * 
	 * @return the solrDao
	 */
	public SolrDao getSolrDao() {
		return solrDao;
	}


	/**
	 * <p></p>
	 * 
	 * @param solrDao the solrDao to set
	 */
	public void setSolrDao(SolrDao solrDao) {
		this.solrDao = solrDao;
	}
}
