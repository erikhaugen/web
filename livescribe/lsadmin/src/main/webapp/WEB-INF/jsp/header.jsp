<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@page import="com.livescribe.admin.config.AppProperties"%>
<p>
	<authz:authorize access="!hasAnyRole('superUser', 'lsadminUser', 'deleteInformationUser')">
		<c:redirect url="/login.jsp">
    		<c:param name="unauthorizedUser" value="true"/>
		</c:redirect>
	</authz:authorize>
    <authz:authorize access="isAuthenticated()">
    	<a href="<c:url value='/'/>">Home</a> |
    	<a href="<c:url value='/infoLookup.htm'/>">Info Lookup</a> | 
    	<a href="<c:url value='/vectorInfoLookup.htm'/>">Vector Info Lookup</a> | 
    </authz:authorize>
    <a href="<%=AppProperties.getIntance().getProperty("lssupport.url")%>/wa/supportHome"><b>LSSupport</b></a>
    <authz:authorize access="isAuthenticated()">
     | <a href="<c:url value='/j_spring_security_logout'/>">Logout</a>
    </authz:authorize>
</p>
