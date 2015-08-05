/**
 * 
 */
package com.livescribe.web.registration.service;

import java.util.Collection;
import java.util.Set;

import com.livescribe.web.registration.dto.UserDto;

/**
 * @author Mohammad M. Naqvi
 * @since 1.3
 *
 */
public interface UserService {
	
	/**
	 * @return
	 */
	public Set<UserDto> findVectorUsersByPartialEmail(String email);
	
	/**
	 * 
	 * @param email
	 * @return
	 */
	public Collection<UserDto> findVectorUsersByEmail(String email);

}
