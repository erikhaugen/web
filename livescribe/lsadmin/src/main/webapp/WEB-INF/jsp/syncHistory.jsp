<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Livescribe Admin Tool - Sync History</title>
		<style type="text/css">
			<%@ include file="/style.css" %>
		</style>
		<script language="javascript">
			<%@ include file="/functions_infoLookup.js" %>
		</script>
		
	</head>
	<body>
		<authz:authorize access="isAuthenticated()">
			<%@ include file="/WEB-INF/jsp/header.jsp" %>
			<form id="backToUserInfoForm" method="post" action="<c:url value='/infoLookup.htm'><c:param name="keyParam" value="primaryEmail"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${user.primaryEmail}"/></c:url>">
			    <input id="/>backToUserInfo" type="submit" value="Back To User Page" />
			</form>
			<br />
			<h2>Sync History</h2>
			<h3>Sync Docs from</h3>
			<ul>
			<c:if test="${not empty displayId}">
				<li><h4>Pen Display ID:&nbsp;&nbsp;${displayId}</h4></li>
			</c:if>
			<c:if test="${not empty user}">
				<li><h4>User's Email:&nbsp;&nbsp;${user.primaryEmail}</h4></li>
			</c:if>
			</ul>
			
			<c:if test="${not empty param.errorMessage}">
				<h2><b><font color="red">Delete Document(s) Failed!!&nbsp;&nbsp;&nbsp; Reason:&nbsp;${param.errorMessage}</font></b></h2>
			</c:if>
			<c:if test="${not empty param.successMessage}">
				<h3><b><font color="green">${param.successMessage}</font></b></h3>
			</c:if>
			
			<c:if test="${not empty deleteCount}">
				<span class="successMsg">Deleted ${deleteCount} documents.</span>
			</c:if>

			<authz:authorize access="hasAnyRole('deleteInformationUser', 'superUser')">
				<c:choose>
					<c:when test="${not empty displayId && not empty user.uid}">
						<a href="<c:url value='/clearSyncDocs/user/${user.uid}/pen/${displayId}'/>"
							onclick="return confirm('Are you sure to clear all sync data for user ${user.primaryEmail} with the pen ${displayId}?')">
							Clear All Synced Data
						</a>
						<br />
					</c:when>
					<c:otherwise>
						<span style="color: #B2B2B2">Clear All Synced Data</span>
					</c:otherwise>
				</c:choose>
			</authz:authorize>

			<c:if test="${not empty docsSyncedToPrimaryEn}">
				<c:forEach var="entry" items="${docsSyncedToPrimaryEn}" varStatus="status">
					<li><h4><b>Current Syncing Evernote Username:&nbsp;&nbsp;${entry.key}</b></h4></li>
					<c:set var="documents" value="${entry.value}"/>
					<c:set var="showDelete" value='<%=AppProperties.getIntance().getProperty("showdeleteforarchiveddocs")%>' />
			
					<table>
						<tr>
						    <th>ID</th>
							<th>AFD GUID</th>
							<th>User UID</th>
							<th>Pen Display ID</th>
							<th>Document Name</th>
							<th>Evernote Notebook GUID</th>
							<th>Created</th>
							<th>Last Modified</th>
 							<th></th>
						</tr>
						<c:forEach var="doc" items="${documents}">
							<tr>
					    		<td>${doc.documentId}</td>
								<td>${doc.guid}</td>
								<td>${doc.user}</td>
								<td>${doc.penSerial}</td>
								<td><a href="<c:url value='/sync/docs/detail/${doc.documentId}'/>">${doc.docName}</a></td>
								<td>${doc.evernoteGuidNotebook}</td>
								<td><fmt:formatDate type="both" value="${doc.created}" /></td>
								<td><fmt:formatDate type="both" value="${doc.lastModified}" /></td>
								<authz:authorize access="hasAnyRole('deleteInformationUser', 'superUser')">								
									<c:if test="${showDelete || not fn:containsIgnoreCase(doc.docName, 'Archived')}">
										<td>
											<%-- <a href="<c:url value='/delete/doc/${doc.documentId}/pen/${doc.penSerial}'/>" onclick="deleteDocument('${doc.documentId}', '${doc.penSerial}')" >Delete</a> --%>
											<span style="color: #B2B2B2">Delete</span>
										</td>
									</c:if>
								</authz:authorize>
							</tr>
						</c:forEach>
					</table>
				</c:forEach>
			</c:if>

			<c:if test="${not empty docsSyncedToNonPrimaryEn}">
				<c:forEach var="entry" items="${docsSyncedToNonPrimaryEn}" varStatus="status">
					<li><h4>Evernote Username:&nbsp;&nbsp;${entry.key}</h4></li>
					<c:set var="documents" value="${entry.value}"/>
			
					<table>
						<tr>
						    <th>ID</th>
							<th>AFD GUID</th>
							<th>User UID</th>
							<th>Pen Display ID</th>
							<th>Document Name</th>
							<th>Evernote Notebook GUID</th>
							<th>Created</th>
							<th>Last Modified</th>
 							<th></th>
						</tr>
						<c:forEach var="doc" items="${documents}">
							<tr>
					    		<td>${doc.documentId}</td>
								<td>${doc.guid}</td>
								<td>${doc.user}</td>
								<td>${doc.penSerial}</td>
								<td><a href="<c:url value='/sync/docs/detail/${doc.documentId}'/>">${doc.docName}</a></td>
								<td>${doc.evernoteGuidNotebook}</td>
								<td><fmt:formatDate type="both" value="${doc.created}" /></td>
								<td><fmt:formatDate type="both" value="${doc.lastModified}" /></td>
								<authz:authorize access="hasAnyRole('deleteInformationUser', 'superUser')">
									<td>
										<%-- <a href="<c:url value='/delete/doc/${doc.documentId}/pen/${doc.penSerial}'/>" onclick="deleteDocument('${doc.documentId}', '${doc.penSerial}')" >Delete</a> --%>
										<span style="color: #B2B2B2">Delete</span>
									</td>
								</authz:authorize>
							</tr>
						</c:forEach>
					</table>
				</c:forEach>
			</c:if>
			
		</authz:authorize>
	</body>
</html>
