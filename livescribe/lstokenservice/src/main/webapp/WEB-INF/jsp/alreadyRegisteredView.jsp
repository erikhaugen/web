<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.livescribe.aws.tokensvc.orm.consumer.RegisteredDevice" %>
<%@ page import="com.livescribe.aws.tokensvc.orm.consumer.User" %>
<%
	RegisteredDevice registeredDevice = (RegisteredDevice)request.getAttribute("registeredDevice");
	User user = registeredDevice.getUser();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Success</title>
	</head>
	<body>
		<h1>Device Already Registered!</h1>
		
		<p>The device with serial number <%= registeredDevice.getDeviceSerialNumber() %> is already registered to <%= user.getFirstName() %> <%= user.getLastName() %>.</p>
	</body>
</html>