package com.livescribe.framework.login.service.result;

import com.livescribe.framework.orm.consumer.User;

public class LoginServiceResult implements ServiceResult {
	private String loginToken;
	private User user;
	
	public String getLoginToken() {
		return loginToken;
	}
	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
