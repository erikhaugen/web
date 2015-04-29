/**
 * Created:  Apr 11, 2014 4:03:07 PM
 */
package com.livescribe.framework.version.tx;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.livescribe.framework.lsconfiguration.AppProperties;
import com.livescribe.framework.lsconfiguration.Env;
import com.livescribe.framework.orm.versions.Version;
import com.livescribe.framework.version.dao.CustomVersionDao;
import com.livescribe.framework.version.dao.CustomVersionHistoryDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class VersionTxCallback implements TransactionCallback<List<Version>> {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private AppProperties appProperties;
	
    @Autowired
    private CustomVersionDao versionDao;

    @Autowired
    private CustomVersionHistoryDao versionHistoryDao;

    @Autowired
    private TransactionTemplate txTmpl;
    
    @Autowired
    private String hostname;
    
    private String appName;
    private Env env;

    public VersionTxCallback() {
    	
    }
    
    /**
	 * <p></p>
	 * 
	 */
	public VersionTxCallback(String appName, Env env) {
		this.appName = appName;
		this.env = env;
	}

	/* (non-Javadoc)
	 * @see org.springframework.transaction.support.TransactionCallback#doInTransaction(org.springframework.transaction.TransactionStatus)
	 */
	@Override
	public List<Version> doInTransaction(TransactionStatus status) {
		
		String method = "doInTransaction()";
		
		Env env = appProperties.getRunningEnvironment();
		
		List<Version> versionList = versionDao.findByAppEnvironmentServer(appName, env, hostname);
		
		return null;
	}

}
