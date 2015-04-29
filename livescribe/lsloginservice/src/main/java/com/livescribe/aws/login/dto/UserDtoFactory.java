/**
 * Created:  Nov 11, 2013 6:14:10 PM
 */
package com.livescribe.aws.login.dto;

import com.livescribe.framework.dto.UserDTO;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserDtoFactory {

	/**
	 * <p></p>
	 * 
	 */
	public UserDtoFactory() {
	}

	/**
	 * <p></p>
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static UserDTO create(User user) {
		
		UserDTO userDto = new UserDTO();
//		userDto.setBirthYear(user.getBirthYear());
		userDto.setConfirmationDate(user.getConfirmationDate());
		userDto.setConfirmed(user.isConfirmed());
//		userDto.setEnabled(user.getEnabled());
//		userDto.setFirstName(user.getFirstName());
//		userDto.setMiddleName(user.get)
//		userDto.setLastName(user.getLastName());
//		userDto.setGradYear(user.getGradYear());
//		userDto.setLocale(user.get)
//		userDto.setMajor(user.getMajor());
//		userDto.setOccupation(user.getOccupation());
//		userDto.setOrganization(user.getOrganization());
//		userDto.setPhone(user.getPhone());
		userDto.setPrimaryEmail(user.getPrimaryEmail());
		userDto.setUid(user.getUid());
//		userDto.setSendEmail(user.getSendEmail());
//		String sex = user.getSex();
//		if ((sex != null) && (!sex.isEmpty())) {
//			Character chSex = sex.charAt(0);
//			userDto.setSex(chSex);
//		}
//		userDto.setUniversity(user.getUniversity());
//		Country country = user.getCountry();
//		userDto.setCountry(country.getName());
//		Address userAddr = user.getAddress();
//		String addr1 = userAddr.getAddress1();
//		String city = userAddr.getCity();
//		String zip = userAddr.getPostCode();
//		UsState state = userAddr.getUsState();
//		String stateName = state.getName();
//		userDto.setAddress(addr1);
//		userDto.setCity(city);
//		userDto.setUsState(stateName);
//		userDto.setPostalCode(zip);
		
		return userDto;
	}
}
