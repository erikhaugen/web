/**
 * Created:  Nov 12, 2013 5:25:01 PM
 */
package com.livescribe.framework.login.mock;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import com.livescribe.framework.login.TestConstants;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MockAuthorizationFactory implements TestConstants {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public static Authorization create() {
		
		Authorization auth = new Authorization();
		auth.setEnShardId(XML_LOADED_EN_SHARD_ID_2);
		long enUserId = Long.parseLong(XML_LOADED_EN_USER_ID_2);
		auth.setEnUserId(enUserId);
		auth.setEnUsername(XML_LOADED_EN_USER_NAME_2);
		Calendar cal = new GregorianCalendar(1999, 01, 23, 13, 57, 22);
		auth.setExpiration(cal.getTime());
		auth.setIsPrimary(true);
		auth.setOauthAccessToken(XML_LOADED_ACCESS_TOKEN_2);
		auth.setProvider("EN");
		User user = new User();
		user.setUid(XML_LOADED_USER_UID_2);
		auth.setUser(user);
		
		return auth;
	}
}
