/**
 * Created:  Sep 24, 2013 4:28:06 PM
 */
package com.livescribe.aws.login.client.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.response.AuthorizationListResponse;
import com.livescribe.framework.login.BaseTest;
import com.livescribe.framework.web.response.ResponseCode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Ignore
public class AuthorizationListResponseConverterTest extends BaseTest {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private AuthorizationListResponse response = null;
	
	/**
	 * <p></p>
	 * 
	 */
	public AuthorizationListResponseConverterTest() {
	}

	@Before
	public void setUp() {
		
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
		authDto.setUid(";alksdfjadl;sfjasf");
		authDto.setIsPrimary(true);

		ArrayList<AuthorizationDto> list = new ArrayList<AuthorizationDto>();
		list.add(authDto);
		response = new AuthorizationListResponse(ResponseCode.SUCCESS, list);
//		response.getResponseList().add(authDto);
	}
	
	@Test
	public void testUnmarshall() {
		
		String method = "testUnmarshall()";
		
		XStream xStream = new XStream(new DomDriver());
		xStream.alias("response", AuthorizationListResponse.class);
		xStream.alias("responseList", List.class);
		xStream.alias("authorization", AuthorizationDto.class);
		String xml = xStream.toXML(response);
		
		logger.debug(method + " - " + xml);
		
		AuthorizationListResponse resp = (AuthorizationListResponse) xStream.fromXML(xml);
		Assert.assertNotNull("The deserialized AuthorizationListResponse object was 'null'.", resp);
	}
}
