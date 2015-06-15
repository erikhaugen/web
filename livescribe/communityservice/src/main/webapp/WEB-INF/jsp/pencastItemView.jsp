<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/pencast.css" />
	</head>
	<body>
		<h2>${pencastList[0].displayName}</h2>
		<table>
			<tr>
				<td>Short ID:</td><td>${pencastList[0].shortId}</td>
			</tr>
			<tr>
				<td>Category</td><td>${pencastList[0].categoryName}</td>
			</tr>
			<tr>
				<td>Description</td><td>${pencastList[0].description}</td>
			</tr>
			<tr>
				<td>Published</td><td><fmt:formatDate pattern="yyyy-MM-dd" value="${pencast[0].fileDate}" /></td>
			</tr>
			<tr>
				<td>Rating</td><td><fmt:formatNumber value="${pencastList[0].rating}" maxFractionDigits="2" minFractionDigits="2" /></td>
			</tr>
		</table>
	</body>
</html>	
	