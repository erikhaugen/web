<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="/css/services/pencast.css" />
	</head>
	<body>
		<h1>Pencasts</h1>
		<p>View list in <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/services/community/pencast.atom">Atom/RSS</a> format.</p>
		<table>
			<tr>
<%-- 				<th>Primary Key</th> --%>
				<th>No.</th>
				<th>Image</th>
				<th>Short ID</th>
				<th>Title</th>
				<th>Category</th>
				<th>Description</th>
				<th># of Views</th>
				<th>Rating</th>
				<th>Publish Date</th>
			</tr>
			<c:set var="i" value="0" />
			<c:forEach items="${pencastList}" var="pencast">	
				<tr>
<%--					<td><a href='"http://localhost:8080/community/community/pencast/" + ${pencast.primaryKey}'>${pencast.primaryKey}</a></td> --%>
					<td><c:out value="${i}" /></td>
					<td><img src="${pencast.thumbnailUrl}" /></td>
					<td>
						<c:choose>
							<c:when test="${pencast.flashFileFound}">
								<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/services/community/pencast/${pencast.shortId}.xml">${pencast.shortId}</a>
							</c:when>
							<c:otherwise>
								${pencast.shortId}
							</c:otherwise>
						</c:choose>
					</td>
					<td><a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/services/community/pencast/${pencast.shortId}">${pencast.displayName}</a></td>
					<td>${pencast.categoryName}</td>
					<td>${pencast.description}</td>
<%-- 					<td>${pencast.publishDate}</td> --%>
					<td>${pencast.numberOfViews}</td>
					<td><fmt:formatNumber value="${pencast.rating}" maxFractionDigits="2" minFractionDigits="2" /></td>
					<td><fmt:formatDate pattern="yyyy-MM-dd" value="${pencast.fileDate}" /></td>
				</tr>
				<c:set var="i" value="${i + 1}" /> 
			</c:forEach>
		</table>
	</body>
</html>
