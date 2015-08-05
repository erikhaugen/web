package com.livescribe.web.registration.response;

import java.util.ArrayList;
import java.util.List;

import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.dto.RegistrationHistoryDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class RegistrationHistoryListResponse extends ServiceResponse {

	@XStreamAlias("registrations")
	private List<RegistrationHistoryDTO> registrationHistories;
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 */
	public RegistrationHistoryListResponse() {
		super();
		registrationHistories = new ArrayList<RegistrationHistoryDTO>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 */
	public RegistrationHistoryListResponse(ResponseCode responseCode) {
		super(responseCode);
		registrationHistories = new ArrayList<RegistrationHistoryDTO>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 * @param regs
	 */
	public RegistrationHistoryListResponse(ResponseCode responseCode, List<RegistrationHistory> regHistories) {
		super(responseCode);
		this.registrationHistories = new ArrayList<RegistrationHistoryDTO>();
		
		if (regHistories != null) {
			for (RegistrationHistory regHistory : regHistories) {
				RegistrationHistoryDTO regDTO = new RegistrationHistoryDTO(regHistory);
				this.registrationHistories.add(regDTO);
			}
		}
	}

	public List<RegistrationHistoryDTO> getRegistrationHistories() {
		return registrationHistories;
	}

	public void setRegistrationHistories(
			List<RegistrationHistoryDTO> registrationHistories) {
		this.registrationHistories = registrationHistories;
	}
	
}
