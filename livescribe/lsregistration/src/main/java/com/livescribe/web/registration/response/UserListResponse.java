package com.livescribe.web.registration.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.dto.UserDto;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class UserListResponse extends ServiceResponse {

	@XStreamAlias("userDtoList")
	private List<UserDto> users;
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 */
	public UserListResponse() {
		super();
		users = new ArrayList<UserDto>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 */
	public UserListResponse(ResponseCode responseCode) {
		super(responseCode);
		users = new ArrayList<UserDto>();
	}
	


	public UserListResponse(ResponseCode responseCode, Collection<UserDto> usersCollection) {
		super(responseCode);
		users = new ArrayList<UserDto>();
		users.addAll(usersCollection);
	}

	public List<UserDto> getUsers() {
		return users;
	}

	public void setUsers(List<UserDto> users) {
		this.users = users;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param regDTO
	 */
	public void addUserDto(UserDto regDTO) {
		if (users == null) {
			users = new ArrayList<UserDto>();
		}
		
		users.add(regDTO);
	}
}
 