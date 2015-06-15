/**
 * Created:  Oct 14, 2010 1:59:26 PM
 */
package com.livescribe.community.search;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.livescribe.community.service.PencastService;
import com.livescribe.community.view.vo.PencastVo;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SearchTesterApp {

	/**
	 * <p></p>
	 * 
	 */
	public SearchTesterApp() {
	}

	/**
	 * <p></p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String url = "http://localhost:8983/solr";
		
//		ApplicationContext ac = new ClassPathXmlApplicationContext("communityservice-context.xml");
//		PencastService pSvc = (PencastService)ac.getBean("pencastService");
//		List<PencastVo> pencasts = pSvc.getTopPencasts(0, 50);
		SolrServer server = null;
		try {
			server = new CommonsHttpSolrServer(url);
		} 
		catch (MalformedURLException mue) {
			mue.printStackTrace();
			System.exit(1);
		}
		SolrQuery query = new SolrQuery();
		query.setQuery("sketch");
		query.setFacet(true);
		query.setFacetMinCount(1);
		query.addFacetField("authorLastName");
		
		QueryResponse resp = null;
		try {
			resp = server.query(query);
		} 
		catch (SolrServerException sse) {
			sse.printStackTrace();
			System.exit(2);
		}
		
		List<FacetField> facetFields = resp.getFacetFields();
		Iterator<FacetField> ffIter = facetFields.iterator();
		while (ffIter.hasNext()) {
			FacetField ff = ffIter.next();
			String name = ff.getName();
			int count = ff.getValueCount();
			System.out.println(name + " (" + count + ")");
			if (count > 0) {
				List<FacetField.Count> values = ff.getValues();
				Iterator<FacetField.Count> ffvIter = values.iterator();
				while (ffvIter.hasNext()) {
					FacetField.Count ffv = ffvIter.next();
					String ffvName = ffv.getName();
					long ffvCount = ffv.getCount();
					System.out.println(ffvName + " (" + ffvCount + ")");
				}
			}
		}
		Iterator<SolrDocument> docIter = resp.getResults().iterator();
		if (docIter.hasNext()) {
			System.out.println("We have results!");
		}
		else {
			System.out.println("No results were returned.");
		}
		while (docIter.hasNext()) {
			SolrDocument doc = docIter.next();
			String content = (String)doc.getFieldValue("fileDisplayName");
			System.out.println(content);
		}
	}

}
