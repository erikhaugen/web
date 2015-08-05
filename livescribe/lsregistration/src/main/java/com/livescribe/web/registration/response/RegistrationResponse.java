/**
 * Created:  Aug 23, 2013 2:49:10 PM
 */
package com.livescribe.web.registration.response;

import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.controller.RegistrationData;
import com.livescribe.web.registration.dto.RegistrationDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class RegistrationResponse extends ServiceResponse {

	@XStreamAlias("registration")
	private RegistrationDTO registrationDto;

    /**
	 * <p></p>
	 * 
	 */
	public RegistrationResponse() {
	}

	/**
	 * <p></p>
	 * 
	 * @param code
	 */
	public RegistrationResponse(ResponseCode code) {
		super(code);
	}

	/**
	 * <p></p>
	 * 
	 * @param data
	 */
	public RegistrationResponse(ResponseCode code, RegistrationData data) {
		super(code);
		registrationDto = new RegistrationDTO(data);
	}

	/**
	 * <p></p>
	 * 
	 * @param data
	 */
	public RegistrationResponse(ResponseCode code, Registration registration) {
		super(code);
		registrationDto = new RegistrationDTO(registration);
	}

	public RegistrationDTO getRegistrationDto() {
		return registrationDto;
	}
	
}
