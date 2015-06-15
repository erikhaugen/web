<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="/css/services/pencast.css" />
	</head>
	<body>
		<h1>Search</h1>
		<div>
			<form action="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/community/community/pencast/search" method="get">
				Enter your search:&nbsp;&nbsp;
				<input id="keywords" name="keywords" type="text" />&nbsp;&nbsp;
				<input id="submit" type="submit" value="Submit" />
			</form>
		</div>
	</body>
</html>
