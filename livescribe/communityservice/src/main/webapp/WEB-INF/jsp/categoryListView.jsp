<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="/css/services/pencast.css" />
	</head>
	<body>
		<h1>Categories</h1>
		<p>View list in <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/services/community/pencast/categories.atom">Atom/RSS</a> format.</p>
		<table>
			<tr>
				<th>No.</th>
				<th>Category</th>
			</tr>
			<c:set var="i" value="0" />
			<c:forEach items="${categoryList}" var="category">	
				<tr>
					<td><c:out value="${i}" /></td>
					<td>${category}</td>
				</tr>
				<c:set var="i" value="${i + 1}" /> 
			</c:forEach>
		</table>
	</body>
</html>
