/**
 * Created:  Oct 14, 2010 7:12:06 PM
 */
package com.livescribe.community.dao;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.livescribe.community.config.ConfigClient;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SolrDao extends BaseDao {

	public static final String FACET_AUTHOR_LAST_NAME = "authorLastName";

	private String serverUrl;
	private SolrServer solrServer;
	
	/**
	 * <p></p>
	 * 
	 */
	public SolrDao() {

		super();
		
		serverUrl = ConfigClient.getSolrUrl();
		logger.debug("serverUrl:  " + serverUrl);
	}

	/**
	 * <p></p>
	 * 
	 * @param keywords
	 * @return
	 */
	public QueryResponse performQuery(String keywords, List<String>facets) {
	
		String method = "performQuery():  ";
		
		SolrServer server = getSolrServer();
		SolrQuery query = new SolrQuery();
		String qStr = keywords;
//		String qStr = "fileDisplayName:" + keywords + " catDisplayName:" + keywords + " authorScreenName:" + keywords;
		query.addField("fileDisplayName");
		query.addField("catDisplayName");
		query.addField("authorScreenName");
		
		logger.debug(method + "query string (keywords):  '" + qStr + "'");
		
		query.setQuery(qStr);
		if ((facets != null) && (!facets.isEmpty())) {
			logger.debug(method + "Number of facets = " + facets.size());
			query.setFacet(true);
			query.setFacetMinCount(1);
			Iterator<String> ffIter = facets.iterator();
			while (ffIter.hasNext()) {
				String facet = ffIter.next();
				query.addFacetField(facet);
			}
		}
		
		QueryResponse resp = null;
		try {
			resp = server.query( query );
		} 
		catch (SolrServerException sse) {
			logger.error(method + "SolrServerException thrown when querying Solr server.");
			sse.printStackTrace();
			return null;
		}
		return resp;
	}

	/**
	 * <p></p>
	 * 
	 * @return the serverUrl
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	/**
	 * <p></p>
	 * 
	 * @return the solrServer
	 */
	public SolrServer getSolrServer() {

		String method = "getSolrServer():  ";
		
		serverUrl = ConfigClient.getSolrUrl();
		
		logger.debug(method + "serverUrl:  " + serverUrl);
		
		try {
			solrServer = new CommonsHttpSolrServer(serverUrl);
		} 
		catch (MalformedURLException mue) {
			logger.error("MalformedURLException thrown while creating connection to Solr server.");
			mue.printStackTrace();
		}
		return solrServer;
	}

	/**
	 * <p></p>
	 * 
	 * @param serverUrl the serverUrl to set
	 */
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	/**
	 * <p></p>
	 * 
	 * @param solrServer the solrServer to set
	 */
	public void setSolrServer(SolrServer solrServer) {
		this.solrServer = solrServer;
	}
}
