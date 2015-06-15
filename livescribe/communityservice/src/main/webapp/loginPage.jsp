<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Login [communityservice]</title>
	</head>
	<body>
		
		<h1>Login</h1>
		
		<form id="loginForm" name="loginForm" method="post" action="/community/community/login">
		
			<input type="hidden" id="clientCulture" name="clientCulture" value="en_US" />
			<input type="hidden" id="clientGUID" name="clientGUID" value="" />
			<input type="hidden" id="clientVersion" name="clientVersion" value="" />
			<input type="hidden" id="clientApp" name="clientApp" value="WEB" />
		
			<table>
				<tr>
					<td><label for="email">Username:&nbsp;&nbsp;</label><input id="email" name="email" type="text" /></td>
				</tr>
				<tr>
					<td><label for="password">Password:&nbsp;&nbsp;</label><input id="password" name="password" type="password" /></td>
				</tr>
				<tr>
					<td><input id="submit" name="submit" type="submit" value="Submit" /></td>
				</tr>
			</table>
			
		</form>
		<br />
		<a href="/community/createAccount.jsp">Create a new account.</a>
		
	</body>
</html>