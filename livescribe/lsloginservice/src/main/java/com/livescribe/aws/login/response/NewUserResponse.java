/**
 * 
 */
package com.livescribe.aws.login.response;

import com.livescribe.framework.dto.UserDTO;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class NewUserResponse extends ServiceResponse {

	@XStreamAlias("uid")
	private String uid;

	@XStreamAlias("user")
	private UserDTO userDTO;
	
	/**
	 * <p></p>
	 *
	 */
	public NewUserResponse() {
		
	}

	/**
	 * <p></p>
	 *
	 * @param code
	 */
	public NewUserResponse(ResponseCode code) {
		super(code);
	}

	/**
	 * <p></p>
	 *
	 * @param code
	 * @param uid
	 */
	public NewUserResponse(ResponseCode code, String uid) {
		super(code);
		this.uid = uid;
	}
	
	public NewUserResponse(ResponseCode code, UserDTO userDTO) {

		super(code);
		this.userDTO = userDTO;
	}

	public UserDTO getUserDTO() {
		return userDTO;
	}

	public void setUserDTO(UserDTO userDTO) {
		this.userDTO = userDTO;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
}
