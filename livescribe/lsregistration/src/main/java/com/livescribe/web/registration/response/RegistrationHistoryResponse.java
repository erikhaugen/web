package com.livescribe.web.registration.response;

import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.web.registration.dto.RegistrationHistoryDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class RegistrationHistoryResponse extends RegistrationResponse {

	@XStreamAlias("registration")
	private RegistrationHistoryDTO registrationHistoryDto;
	
	/**
	 * <p></p>
	 * 
	 * @param code
	 * @param registrationHistory
	 */
	public RegistrationHistoryResponse(ResponseCode code, RegistrationHistory registrationHistory) {
		super(code);
		registrationHistoryDto = new RegistrationHistoryDTO(registrationHistory);
	}

	public RegistrationHistoryDTO getRegistrationHistoryDto() {
		return registrationHistoryDto;
	}
	
}
