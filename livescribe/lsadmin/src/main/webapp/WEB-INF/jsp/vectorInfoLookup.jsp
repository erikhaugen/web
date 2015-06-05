<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@page import="com.livescribe.admin.config.AppProperties"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Livescribe Admin Tool - Vector Info Lookup</title>
	<style type="text/css">
		<%@ include file="/style.css" %>
	</style>
</head>
<body>
<script language="javascript" >
<%@ include file="/functions_infoLookup.js" %>
</script>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<authz:authorize access="!hasAnyRole('superUser', 'lsadminUser', 'deleteInformationUser')">
	<c:redirect url="/login.jsp">
    	<c:param name="unauthorizedUser" value="true"/>
	</c:redirect>
</authz:authorize>

<authz:authorize access="hasAnyRole('superUser', 'lsadminUser', 'deleteInformationUser')">

<form method="post" action="vectorInfoLookup.htm">
    <h2>Vector Info Lookup</h2>
    <p><b>Note</b></p>
    <ul>
        <li><b><i>contains</i></b> comparator can include wildcards such as '<strong>%</strong>' to match an arbitrary number of characters (including zero characters), '<strong>_</strong>' to match any single character</li>
        <li><b><i>Pen Serial Number</i></b> is the Pen ID in Serial Number <b><i>XXXXXXXXXXXXX</i></b> (DEC) format. </li>
        <li><b><i>Pen Display Id</i></b> is the Pen ID in <b><i>AYE-XXX-YYY-ZZ</i></b> format. </li>
    </ul>
    <table id="criteriaTbl">
        <tr>
            <th>Key</th>
            <th>Comparator</th>
            <th>Value</th>
            <th>&nbsp;</th>
        </tr>
        <c:set var="rowIdCounter" value="0"/>
        <c:forEach var="criteria" items="${criteriaList}">
        <c:set var="rowId" value="${'row'}${rowIdCounter}"/>
        <tr id=${rowId}>
            <td>
                <select name="keyParam">
                    <option value="primaryEmail" <c:if test="${criteria.queryField.field eq 'primaryEmail'}">selected="selected"</c:if>>Email</option>
                    <option value="penSerialNumber" <c:if test="${criteria.queryField.field eq 'penSerialNumber'}">selected="selected"</c:if>>Pen Serial Number</option>
                    <option value="penDisplayId" <c:if test="${criteria.queryField.field eq 'penDisplayId'}">selected="selected"</c:if>>Pen Display Id</option>
                    <!--
                    <option value="uid" <c:if test="${criteria.queryField.field eq 'uid'}">selected="selected"</c:if>>uid</option>
                    <option value="enUsername" <c:if test="${criteria.queryField.field eq 'enUsername'}">selected="selected"</c:if>>enUserName</option>
                    <option value="enUserId" <c:if test="${criteria.queryField.field eq 'enUserId'}">selected="selected"</c:if>>enUserId</option>
                    -->
                </select>
            </td>
            <td>
                <select name="comparatorParam">
                    <option value="contains" <c:if test="${criteria.comparator.name eq 'contains'}">selected="selected"</c:if>>contains</option>
                    <option value="isEqualTo" <c:if test="${criteria.comparator.name eq 'isEqualTo'}">selected="selected"</c:if>>isEqualTo</option>
                    <!--
                    <option value="isNotEqualTo" <c:if test="${criteria.comparator.name eq 'isNotEqualTo'}">selected="selected"</c:if>>isNotEqualTo</option>
                    <option value="isGreaterThan" <c:if test="${criteria.comparator.name eq 'isGreaterThan'}">selected="selected"</c:if>>isGreaterThan</option>
                    <option value="isGreaterThanOrEqualTo" <c:if test="${criteria.comparator.name eq 'isGreaterThanOrEqualTo'}">selected="selected"</c:if>>isGreaterThanOrEqualTo</option>
                    <option value="isLessThan" <c:if test="${criteria.comparator.name eq 'isLessThan'}">selected="selected"</c:if>>isLessThan</option>
                    <option value="isLessThanOrEqualTo" <c:if test="${criteria.comparator.name eq 'isLessThanOrEqualTo'}">selected="selected"</c:if>>isLessThanOrEqualTo</option>
                    -->
                </select>
            </td>
            <td>
                <input name="valueParam" type="text" value="<c:out value='${criteria.value}'/>"/>
                <c:if test="${not empty errorMessage}"><span style="color: red;">${errorMessage}</span></c:if>
            </td>
            <td>
                <!-- input type="button" value="Remove" onclick="deleteRow(${rowId})" / -->
                <input type="submit" value="Search" onclick=""/>
            </td>
        </tr>
        <c:set var="rowIdCounter" value="${rowIdCounter + 1}"/>
        </c:forEach>
    </table>
</form>

<c:if test="${empty errorMessage and (not empty resultsFound)}">
	<hr />
	<h2>Search Result</h2>
	<c:if test="${(empty users) or (fn:length(users) eq 0)}">
	   <h3>User Info: No result found</h3>
	</c:if>
	<c:if test="${fn:length(users) gt 0}">
		<h3>User Info: (Found <c:out value="${fn:length(users)}" /> user(s))</h3>
		<table border="1" cellpadding="10">
		    <tr>
		        <th>First Name</th>
		        <th>Last Name</th>
		        <th>Email</th>
		    </tr>
					<c:forEach var="user" items="${users}">
						<tr>
							<td>${user.firstName}</td>
							<td>${user.lastName}</td>
							<td><a href="<c:url value='/vectorInfoLookup.htm'><c:param name="keyParam" value="primaryEmail"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${user.email}"/></c:url>">${user.email}</a></td>
							<c:set var="backURL"><c:url value="/vectorInfoLookup.htm?${submittedKeyParam}&amp;${submittedComparatorParam}&amp;${submittedValueParam}"></c:url></c:set>
							<td><a href="<c:url value='/registration/history/email/${user.email}.htm'><c:param name="backURL" value="${backURL}"/></c:url>">Vector Warranty & History</a></td>
						</tr>

					</c:forEach>
				</table>
		</c:if>

	   <c:if test="${not empty vectorRegistrationList}">
	           <br/>
	           <h3>Registered Vector pens: (Found <c:out value="${fn:length(vectorRegistrationList)}" /> pen(s))</h3>
                <table border="1" cellpadding="10">
                <tr>
                    <th>Display ID</th>
                    <th>Serial Number</th>
                    <th>Device Type</th>
                    <th>Registered Date</th>
                    <th>Registration Completed Date</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email</th>
                    <th>Pen Name</th>
                    <th>Edition</th>
                    <th>App ID</th>
                    <th>Country</th>
                </tr>
                <c:forEach var="vectorRegistration" items="${vectorRegistrationList}">
                    <tr>
                        <td><a href="<c:url value="/vectorInfoLookup.htm"><c:param name="keyParam" value="penDisplayId"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${vectorRegistration.displayId}"/></c:url>">${vectorRegistration.displayId}</a></td>
                        <td><a href="<c:url value="/vectorInfoLookup.htm"><c:param name="keyParam" value="penSerialNumber"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${vectorRegistration.penSerial}"/></c:url>">${vectorRegistration.penSerial}</a></td>
                        <td>Vector</td>
                        <td><fmt:formatDate type="both" value="${vectorRegistration.created}" /></td>
                        <td><fmt:formatDate type="both" value="${vectorRegistration.created}" /></td>
                        <td>${vectorRegistration.firstName}</td>
                        <td>${vectorRegistration.lastName}</td>
                        <td><a href="<c:url value="/vectorInfoLookup.htm"><c:param name="keyParam" value="primaryEmail"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${vectorRegistration.email}"/></c:url>">${vectorRegistration.email}</a></td>
                        <td>${vectorRegistration.penName}</td>
                        <td>${vectorRegistration.edition}</td>
                        <td>${vectorRegistration.appId}</td>
                        <td>${vectorRegistration.country}</td>
                        <c:set var="backURL"><c:url value="/vectorInfoLookup.htm?${submittedKeyParam}&amp;${submittedComparatorParam}&amp;${submittedValueParam}"></c:url></c:set>
                        <authz:authorize access="hasAnyRole('deleteInformationUser', 'superUser')">
                        <td>
                            <form id="unregisterForm" method="post" action="<c:url value='/unregister'/>">
                                <input type="button" value="Unregister" onclick="return unregisterVectorConfirm('${vectorRegistration.penSerial}', '${users[0].email}','${vectorRegistration.appId}')" />
                                <input type="hidden" name="redirectPath" value="<c:url context='/' value='/vectorInfoLookup.htm?${submittedKeyParam}&amp;${submittedComparatorParam}&amp;${submittedValueParam}'/>"/>
                            </form>
                        </td>
                        </authz:authorize>
                        <td><a href="<c:url value='/registration/history/pen/${vectorRegistration.penSerial}.htm'><c:param name="backURL" value="${backURL}"/></c:url>">Warranty & History</a></td>
                    </tr>
                </c:forEach>
                </table>
            </c:if>

    <%-- for unregister pen only--%>
    <c:if test="${not empty unregisteredDevices}">
        <h3>Unregistered Vector Pens: (Showing ${fn:length(unregisteredDevices)} pen(s) out of maximum 200)</h3>
        <table border="1" cellpadding="10">
            <tr>
                <th>Display ID</th>
                <th>Serial Number (DEC)</th>
                <th>Serial Number (HEX)</th>
                <th>Device Type</th>
            </tr>
            <c:forEach var="unregisteredDevice" items="${unregisteredDevices}">
            <tr>
                <c:if test="${(not (unregisteredDevice.penType eq 'WIFI')) and not (unregisteredDevice.penType eq 'Vector')}">
                    <td>${unregisteredDevice.displayId}</td>
                </c:if>
                <c:if test="${(unregisteredDevice.penType eq 'WIFI')}">
                    <td><a href="<c:url value='/infoLookup.htm'><c:param name="keyParam" value="deviceSerialNumber"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${unregisteredDevice.displayId}"/></c:url>">${unregisteredDevice.displayId}</a></td>
                </c:if>
                <c:if test="${(unregisteredDevice.penType eq 'Vector')}">
                    <td><a href="<c:url value='/vectorInfoLookup.htm'><c:param name="keyParam" value="deviceSerialNumber"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${unregisteredDevice.displayId}"/></c:url>">${unregisteredDevice.displayId}</a></td>
                </c:if>
                <td>${unregisteredDevice.serialnumber}</td>
                <td>${unregisteredDevice.serialnumberHex}</td>
                <td>${unregisteredDevice.penType}</td>
                <c:if test="${(unregisteredDevice.penType eq 'Vector')}">
                    <c:set var="backURL"><c:url value="/vectorInfoLookup.htm?${submittedKeyParam}&amp;${submittedComparatorParam}&amp;${submittedValueParam}"></c:url></c:set>
                    <td><a href="<c:url value='/registration/history/pen/${unregisteredDevice.serialnumber}.htm'><c:param name="backURL" value="${backURL}"/></c:url>">Warranty & History</a></td>
                </c:if>
            </tr>
            </c:forEach>
        </table>
        <c:if test="${not (unregisteredDevice.penType eq 'WIFI') and not (unregisteredDevice.penType eq 'Vector')}">
            <p style="color: red">
                <b>NOTE: </b>With wired pen, please go to <a href="<%=AppProperties.getIntance().getProperty("lssupport.url")%>/wa/penSearch">Pen Lookup on LSSupport</a> for further information. 
            </p>
        </c:if>
    </c:if>
</c:if>

<c:if test="${not empty param.successMessage}">
<script>
    alert("${param.successMessage}");
</script>
</c:if>

<input type="hidden" id="submittedKeyParam" name="submittedKeyParam" value="${submittedKeyParam}" />
<input type="hidden" id="submittedComparatorParam" name="submittedComparatorParam" value="${submittedComparatorParam}" />
<input type="hidden" id="submittedValueParam" name="submittedValueParam" value="${submittedValueParam}" />
</authz:authorize>
</body>
</html>
