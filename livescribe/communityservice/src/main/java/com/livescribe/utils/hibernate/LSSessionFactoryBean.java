package com.livescribe.utils.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import com.livescribe.community.config.ConfigClient;

public class LSSessionFactoryBean extends LocalSessionFactoryBean {

	private Logger logger = Logger.getLogger(LSSessionFactoryBean.class);
	
	private String dsBeanName;

	public LSSessionFactoryBean(String dsBeanName) {
		super();
		this.dsBeanName = dsBeanName;
		logger.info("Creating LSSessionFactoryBean with dsBeanName = " + dsBeanName );
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		logger.info("Setting datasource for LSSessionFactoryBean with dsBeanName = " + dsBeanName );

		DataSource myDataSource = ConfigClient.getBean(dsBeanName, DataSource.class);
		myDataSource = (myDataSource==null)  ? dataSource : myDataSource;

//		printConnectionDetails(myDataSource);
//		printConnectionDetails(dataSource);

		super.setDataSource(myDataSource);
	}
	
	private void printConnectionDetails(DataSource dataSource) {
		
		try {
			Connection conn = dataSource.getConnection();
			if (conn != null) {
				Properties clientProps = conn.getClientInfo();
				Set keySet = clientProps.keySet();
				Iterator<String> keyIter = keySet.iterator();
				StringBuilder builder = new StringBuilder();
				builder.append("\n\n    DataSource Configuration Properties\n");
				builder.append("-------------------------------------------\n");
				while (keyIter.hasNext()) {
					String key = keyIter.next();
					String value = (String)clientProps.get(key);
					builder.append("  " + key + " = " + value + "\n");
				}
				builder.append("\n");
				this.logger.info(builder.toString());
			}
			else {
				this.logger.error("DataSource returned a 'null' Connection.");
			}
		} 
		catch (SQLException sqle) {
			this.logger.error("SQLException thrown while attempting to access properties of DataSource.  " + sqle.getMessage());
			sqle.printStackTrace();
		}
	}
}
