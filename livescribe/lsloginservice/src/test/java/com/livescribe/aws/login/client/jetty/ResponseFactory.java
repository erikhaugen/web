/**
 * Created:  Sep 26, 2013 1:00:35 PM
 */
package com.livescribe.aws.login.client.jetty;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.response.AuthorizationListResponse;
import com.livescribe.framework.web.response.ResponseCode;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ResponseFactory {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public static AuthorizationListResponse createAuthorizationListResponse() {
		
		AuthorizationDto authDto = new AuthorizationDto();
		authDto.setAuthorizationId(1234567L);
		authDto.setAuthorized(true);
		authDto.setUserEmail("kmurdoff@livescribe.com");
		authDto.setEnUsername("kmurdoff");
		authDto.setOauthAccessToken("");
		authDto.setProvider("EN");
		authDto.setEnShardId("s1");
		authDto.setExpiration(new Date());
		authDto.setCreated(new Date());
		authDto.setLastModified(new Date());
		authDto.setLastModifiedBy("KFM");
		authDto.setEnUserId(440556L);
		authDto.setUid("e1809d06-0c28-40b0-8b69-48fd8d3f13fa");
		authDto.setIsPrimary(true);

		ArrayList<AuthorizationDto> list = new ArrayList<AuthorizationDto>();
		list.add(authDto);
		AuthorizationListResponse response = new AuthorizationListResponse(ResponseCode.SUCCESS, list);
		
		return response;
	}
}
