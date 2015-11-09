package com.livescribe.web.utils.hibernate;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import com.livescribe.lsconfig.LSConfigurationFactory;

public class LSSessionFactoryBean extends LocalSessionFactoryBean {

	private Logger logger = Logger.getLogger(LSSessionFactoryBean.class);
	
	private String dsBeanName;
	private String appName;

	public LSSessionFactoryBean() {
		this(null, null);
	}
	
	public LSSessionFactoryBean(String appName, String dsBeanName) {
		super();
		this.dsBeanName = dsBeanName;
		this.appName = appName;
		logger.info("Creating LSSessionFactoryBean " + 
				(appName==null ? "" : " appName=" + appName)  + 
				(dsBeanName==null ? "" : " dsBeanName=" + dsBeanName)
				+"." );
	}
	
	public LSSessionFactoryBean(String dsBeanName) {
		this(null, dsBeanName);
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		DataSource myDataSource = null;
		if ( StringUtils.isNotBlank(dsBeanName) ) {
			logger.info("Setting datasource in LSSessionFactoryBean with dsBeanName = " + dsBeanName );
			myDataSource = LSConfigurationFactory.getBean(appName, dsBeanName, DataSource.class);
		} else {
			logger.info("Setting default datasource in LSSessionFactoryBean.");
		}
		myDataSource = (myDataSource == null)  ? dataSource : myDataSource;
		logger.info("Using datasource - " + myDataSource.toString());

		super.setDataSource(myDataSource);
	}
	
}

