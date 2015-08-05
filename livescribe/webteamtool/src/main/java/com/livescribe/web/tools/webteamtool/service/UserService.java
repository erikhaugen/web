/**
 * Created:  Sep 30, 2013 4:59:22 PM
 */
package com.livescribe.web.tools.webteamtool.service;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface UserService {

	public Authorization findPrimaryAuthorization(String email) throws MultipleRecordsFoundException;
}
