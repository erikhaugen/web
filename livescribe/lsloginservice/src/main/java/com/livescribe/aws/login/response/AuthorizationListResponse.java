package com.livescribe.aws.login.response;

import java.util.ArrayList;
import java.util.List;

import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>Represents a list of Evernote authorizations.</p>
 * 
 * @author Mohammad M. Naqvi
 * @version 1.0
 */
@XStreamAlias("response")
public class AuthorizationListResponse extends ServiceResponse {
	
	@XStreamAlias("responseList")
	private List<AuthorizationDto> responseList;
	
	public AuthorizationListResponse() {
	}
	
	public AuthorizationListResponse(List<AuthorizationDto> authDtoList) {
		this(null, authDtoList);
	}

	public AuthorizationListResponse(ResponseCode responseCode) {
		super(responseCode);
	}

	public AuthorizationListResponse(ResponseCode responseCode, List<AuthorizationDto> authDtoList) {
		super(responseCode);
		if (null != authDtoList && !authDtoList.isEmpty()) {
			responseList = new ArrayList<AuthorizationDto>();
			responseList.addAll(authDtoList);
		}
	}

	public List<AuthorizationDto> getResponseList() {
		return responseList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthorizationListResponse [responseList=");
		builder.append(responseList);
		builder.append("]");
		return builder.toString();
	}
}
