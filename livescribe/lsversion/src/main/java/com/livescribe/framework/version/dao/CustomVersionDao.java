/**
 * Created:  Apr 2, 2014 1:42:15 PM
 */
package com.livescribe.framework.version.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.livescribe.framework.lsconfiguration.Env;
import com.livescribe.framework.orm.versions.Version;
import com.livescribe.framework.orm.versions.VersionDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
//@Repository("versionDao")
public class CustomVersionDao extends VersionDao {

	/**
	 * <p></p>
	 * 
	 */
	public CustomVersionDao() {
		super();
	}

	/**
	 * <p>Returns the <code>Version</code> of the given application name deployed
	 * to the given environment.</p>
	 * 
	 * @param appName The name of the application to find.
	 * @param env The environment to search for.
	 * 
	 * @return a <code>Version</code> matching the given name and environment.
	 */
	public Version findByAppAndEnvironment(String appName, Env env) {
		
		Criteria crit = this.sessionFactoryVersions.getCurrentSession().createCriteria(Version.class);
		crit.add(Restrictions.eq("appName", appName)).add(Restrictions.eq("deployEnv", env.toString()));
		
		Version version = (Version)crit.uniqueResult();
		
		return version;
	}
	
	/**
	 * <p>Returns the <code>Version</code> of the given application name deployed
	 * to the given environment on the server with the given name.</p>
	 * 
	 * @param appName The name of the application to find.
	 * @param env The environment to search for.
	 * @param serverName The name of the server to use in the search.
	 * 
	 * @return a <code>List</code> of <code>Version</code> objects.
	 */
	public List<Version> findByAppEnvironmentServer(String appName, Env env, String serverName) {
		
		String method = "findByAppEnvironmentServer()";
		
		logger.debug(method + " - Looking up version with:  " + appName + " - " + env.getEnvName() + " - " + serverName);
		
		Criteria crit = this.sessionFactoryVersions.getCurrentSession().createCriteria(Version.class);
		crit.add(Restrictions.eq("appName", appName)).add(Restrictions.eq("deployEnv", env.toString())).add(Restrictions.eq("appServer", serverName));
		
		List<Version> versions = crit.list();
		
		return versions;
	}
}
