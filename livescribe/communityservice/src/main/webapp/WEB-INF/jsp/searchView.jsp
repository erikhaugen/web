<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.util.Map" %>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="/css/services/pencast.css" />
	</head>
	<body>
		<h1>Search Results</h1>
		Go back to <a href="/community/searchPage.jsp">search page</a>.<br>
		<table>
			<c:forEach items="${facetResults}" var="entry">
				<tr>
					<td><c:out value="${entry}" /></td>
				</tr>
			</c:forEach>
		</table>
		<br>
		<table>
			<tr>
				<th>Titles</th>
			</tr>
			<c:forEach items="${searchResults}" var="result">
				<tr>
					<td><c:out value="${result}" /></td>
				</tr>
			</c:forEach>
		</table>
		
	</body>
</html>
