package com.livescribe.web.registration.response;

import java.util.ArrayList;
import java.util.List;

import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.dto.RegistrationDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class RegistrationListResponse extends ServiceResponse {

	@XStreamAlias("registrations")
	private List<RegistrationDTO> registrations;
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 */
	public RegistrationListResponse() {
		super();
		registrations = new ArrayList<RegistrationDTO>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 */
	public RegistrationListResponse(ResponseCode responseCode) {
		super(responseCode);
		registrations = new ArrayList<RegistrationDTO>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 * @param regs
	 */
	public RegistrationListResponse(ResponseCode responseCode, List<Registration> regs) {
		super(responseCode);
		this.registrations = new ArrayList<RegistrationDTO>();
		
		if (regs != null) {
			for (Registration reg : regs) {
				RegistrationDTO regDTO = new RegistrationDTO(reg);
				this.registrations.add(regDTO);
			}
		}
	}

	public List<RegistrationDTO> getRegistrations() {
		return registrations;
	}

	public void setRegistrations(List<RegistrationDTO> registrations) {
		this.registrations = registrations;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param regDTO
	 */
	public void addRegistrationDTO(RegistrationDTO regDTO) {
		if (registrations == null) {
			registrations = new ArrayList<RegistrationDTO>();
		}
		
		registrations.add(regDTO);
	}
}
 