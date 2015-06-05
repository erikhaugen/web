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
        <title>Livescribe Admin Tool - Document Details</title>
        <style type="text/css">
            <%@ include file="/style.css" %>
        </style>
        <script language="javascript">
        </script>
        
    </head>
    <body>
        <authz:authorize access="isAuthenticated()">
            <%@ include file="/WEB-INF/jsp/header.jsp" %>
            <c:if test="${documentId != null}">
                <hr/>
                <h2>Pages and Sessions of document <i>${document.docName}</i></h2>
                <h3><a id="pages">Pages</a></h3>
                <h4><a href="#sessions">Go to Sessions</a></h4>
                <table>
                    <tr>
                        <th>Evernote Note GUID</th>
                        <th>Page Index</th>
                        <th>Label</th>
                        <th>Start Time</th>
                        <th>End Time</th>
                        <th>Created Date</th>
                        <th>Last Modified Date</th>
                    </tr>
                    <c:forEach var="page" items="${pages}">
                    <tr>
                        <td>${page.evernoteGuidNote}</td>
                        <td>${page.pageIndex}</td>
                        <td>${page.label}</td>
                        <td>${page.startTime}</td>
                        <td>${page.endTime}</td>
                        <td>${page.created}</td>
                        <td>${page.lastModified}</td>
                    </tr>
                    </c:forEach>
                </table>
                
                <h3><a id="sessions">Sessions</a></h3>
                <h4><a href="#pages">Go to Pages</a></h4>
                <table>
                    <tr>
                        <th>Evernote Note GUID</th>
                        <th>Start Time</th>
                        <th>End Time</th>
                        <th>Created Date</th>
                        <th>Last Modified Date</th>
                    </tr>
                    <c:forEach var="session" items="${sessions}">
                    <tr>
                        <td>${session.evernoteGuidNote}</td>
                        <td>${session.startTime}</td>
                        <td>${session.endTime}</td>
                        <td>${session.created}</td>
                        <td>${session.lastModified}</td>
                    </tr>
                    </c:forEach>
                </table>
            </c:if>
        </authz:authorize>
    </body>
</html>
