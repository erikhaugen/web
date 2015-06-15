<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
	Map params = (Map)request.getAttribute("responseParams");
	String token = (String)params.get("token");
	Cookie cookie = new Cookie("tk", token);
	String host = request.getServerName();
	cookie.setDomain(host);
	cookie.setPath("/");
	cookie.setMaxAge(1800);
	response.addCookie(cookie);
--%>		
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Success</title>
	</head>
	<body>
		<h1>Success!</h1>
		
		<table>
			<tr>
				<td>Status</td>
				<td><c:out value='${responseParams["returnCode"]}' /></td>
			</tr>
			<tr>
				<td>Token</td>
				<td><c:out value='${responseParams["token"]}' /></td>
			</tr>
			<tr>
				<td>Unique ID</td>
				<td><c:out value='${responseParams["uniqueId"]}' /></td>
			</tr>
			<tr>
				<td>User Certificate</td>
				<td><c:out value='${responseParams["userCertificate"]}' /></td>
			</tr>
			<tr>
				<td>Community URL</td>
				<td><c:out value='${responseParams["communityUrl"]}' /></td>
			</tr>
			<tr>
				<td>Return URL</td>
				<td><c:out value='${responseParams["returnUrl"]}' /></td>
			</tr>
		</table>
		
		<h3>Cookies</h3>
		
		<table>
			<tr>
				<th>Name</th>
				<th>Value</th>
			</tr>
			<c:forEach items="${request.cookies}" var="cookie">
			<tr>
				<td><c:out value="${cookie.name}" /></td>
				<td><c:out value="${cookie.value}" /></td>
			</tr>
			</c:forEach>
		</table>
		<br />		
		<a href="/community/community/logout">Log Out</a>
		
	</body>
</html>
