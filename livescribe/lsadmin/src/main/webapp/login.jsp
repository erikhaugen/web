<%@ page import="org.springframework.security.web.authentication.AuthenticationProcessingFilter"%>
<%@ page import="org.springframework.security.web.authentication.AbstractProcessingFilter"%>
<%@ page import="org.springframework.security.core.AuthenticationException"%>

<html>
	<head>
		<title>Livescribe Admin Tool Login Page</title>
		<style type="text/css">
			<%@ include file="/style.css" %>
		</style>
	</head>
	<body onload='document.f.j_username.focus();'>
	   <%@ include file="/WEB-INF/jsp/header.jsp" %>
	    <h1 align="center">Livescribe Admin Tool</h1>
	    <form name='f' action='j_spring_security_check' method='POST'>
	        <table align="center">
	            <tr>
	                <td align="right">User:</td>
	                <td><input type='text' name='j_username' value='' size="40"></td>
	            </tr>
	            <tr>
	                <td align="right">Password:</td>
	                <td><input type='password' name='j_password' size="40" /></td>
	            </tr>
	            <tr>
	                <td colspan="2" align="center" style="height: 30x"><input name="submit" type="submit" value="Login" style="width: 100px"/></td>
	            </tr>
	        </table>
	    </form>
	    <div align="center">
	        <c:if test="${not empty param.login_error}">
	            <font color="red"> Your login attempt was not successful, try again.<br /> <br /> Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />. </font>
	        </c:if>
	        <c:if test="${not empty param.unauthorizedUser}">
	            <font color="red"> Your login attempt was not successful, try again.<br /> <br /> Reason: <c:out value="You do not have sufficient privileges to access the Livescribe Admin tool." />. </font>
	        </c:if>
	    </div>
	    <%@ include file="/WEB-INF/jsp/debuginfo.jsp" %>
	</body>
</html>