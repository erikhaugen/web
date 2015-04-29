/**
 * Created:  Sep 24, 2013 3:50:20 PM
 */
package com.livescribe.aws.login.client.converter;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.response.AuthorizationListResponse;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class AuthorizationListResponseConverter implements Converter {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public AuthorizationListResponseConverter() {
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
//	@Override
	public boolean canConvert(Class type) {

		String method = "canConvert()";

		return type.equals(AuthorizationListResponse.class);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
	 */
//	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {

		String method = "marshal()";

		AuthorizationListResponse response = (AuthorizationListResponse)source;
//		writer.startNode
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
//	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		String method = "unmarshal()";

		AuthorizationListResponse response = new AuthorizationListResponse();
		ArrayList<AuthorizationDto> list = new ArrayList<AuthorizationDto>();
		
		reader.moveDown();
		reader.moveDown();
		reader.moveDown();

		logger.debug(method + " - Node:  " + reader.getNodeName());
		
//		AuthorizationDto auth = new AuthorizationDto();
//		String authorizedStr = reader.getValue();
//		auth.setAuthorized(Boolean.parseBoolean(authorizedStr));
//		
//		logger.debug(method + " - Node:  " + reader.getNodeName());
//
//		if ("authorized".equals(reader.getNodeName())) {
//			String authorizationIdStr = reader.getValue();
//			auth.setAuthorizationId(Long.parseLong(authorizationIdStr));
//		}
//		auth.setUserEmail(reader.getValue());
//		auth.setEnUsername(reader.getValue());
//		auth.setOauthAccessToken(reader.getValue());
//		auth.setProvider(reader.getValue());
//		auth.setEnShardId(reader.getValue());
//		
//		String expDateStr = reader.getValue();
		
		reader.moveUp();
		reader.moveUp();
		reader.moveUp();
		
		return response;
	}

}
