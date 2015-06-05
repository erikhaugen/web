<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Livescribe Admin Tool</title>
		<style type="text/css">
			<%@ include file="/style.css" %>
		</style>
    </head>
    <body>
        <authz:authorize access="isAuthenticated()">
        <%@ include file="/WEB-INF/jsp/header.jsp" %>
        <h1>Livescribe Admin Tool</h1>
        <p>
        <a href="infoLookup.htm">Wifi/old wired Info Lookup</a> : looking up User and wifi and old wired Pen information (list of pens, files, ...)
        </p>
        <p>
        <a href="vectorInfoLookup.htm">Vector Info Lookup</a> : looking up User and vector Pen information (list of pens, files, ...)
        </p>
        <br/>
        <p>
        <a href="<c:url value='j_spring_security_logout'/>">Logout</a>
        </p>
        </authz:authorize>
        <%@ include file="/WEB-INF/jsp/debuginfo.jsp" %>
    </body>
</html>
