<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@page import="com.livescribe.admin.config.AppProperties"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Livescribe Admin Tool - User Lookup</title>
    <style type="text/css">
        <%@ include file="/style.css" %>
    </style>
</head>
<body>
<script language="javascript" >
<%@ include file="/functions_infoLookup.js" %>
</script>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<authz:authorize access="!hasAnyRole('superUser', 'lsadminUser', 'deleteInformationUser')">
    <c:redirect url="/login.jsp">
        <c:param name="unauthorizedUser" value="true"/>
    </c:redirect>
</authz:authorize>

<authz:authorize access="hasAnyRole('superUser', 'lsadminUser', 'deleteInformationUser')">
<c:if test="${ not empty penSerial}">
<h3>Warranty information and registration history of the pen having serial as ${penSerial}</h3>
</c:if>

<c:if test="${ not empty param.errorMessage}">
	<h3 class="err">${param.errorMessage}</h3>
</c:if>

<c:if test="${ not empty email}">
<h3>Warranty informations of pens and registration history of the user having email as ${email}</h3>
</c:if>

<h1>Warranty Information</h1>
<table>
    <tr>
        <th>Display ID</th>
        <th>Pen Serial</th>
        <th>Pen Name</th>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Email</th>
        <th>First registration</th>
        <th>Locale</th>
        <th>App ID</th>
        <th>Edition</th>
    </tr>
    <c:if test="${not empty warranties }">
    <c:forEach var="warranty" items="${warranties}">
    <tr>
        <th>${warranty.displayId}</th>
        <th>${warranty.penSerial}</th>
        <th>${warranty.penName}</th>
        <th>${warranty.firstName}</th>
        <th>${warranty.lastName}</th>
        <th>${warranty.email}</th>
        <th>${warranty.created}</th>
        <th>${warranty.locale}</th>
        <th>${warranty.appId}</th>
        <th>${warranty.edition}</th>
    </tr>
    </c:forEach>
    </c:if>
</table>

<h1>Registration History</h1>
<table>
    <tr>
        <th>Display ID</th>
        <th>Pen Serial</th>
        <th>Pen Name</th>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Email</th>
        <th>Registration date</th>
        <th>Locale</th>
        <th>App ID</th>
        <th>Edition</th>
        <th>Country</th>
    </tr>
    <c:forEach var="aRegistration" items="${registrations}">
    <tr>
        <th>${aRegistration.displayId}</th>
        <th>${aRegistration.penSerial}</th>
        <th>${aRegistration.penName}</th>
        <th>${aRegistration.firstName}</th>
        <th>${aRegistration.lastName}</th>
        <th>${aRegistration.email}</th>
        <th>${aRegistration.registrationDate}</th>
        <th>${aRegistration.locale}</th>
        <th>${aRegistration.appId}</th>
        <th>${aRegistration.edition}</th>
        <th>${aRegistration.country}</th>
    </tr>
    </c:forEach>
</table>
</authz:authorize>
<br/>
<a href="${backURL}">Back</a>
</body>
</html>
