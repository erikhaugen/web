<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Livescribe Admin Tool - User Lookup</title>
<style type="text/css">
<%@ include file="/style.css" %>
</style>
<script language="javascript">
    function setPageAction(action) {
        document.forms["penLookupForm"].pageAction.value = action;
    }
    function lookup() {
        setPageAction('lookup')
    }
    function unregisterConfirm(penSerial, email) {

        var confirmation = confirm("Are you sure you want to unregister pen with serial number " + penSerial + " for user " + email + "?");
        
        if (confirmation) {
            document.forms["unregisterForm"].redirectPath.value=document.URL;
            document.forms["unregisterForm"].submit();
        }
        else {
        }
    }
</script>
</head>
<body>
<authz:authorize access="isAuthenticated()">
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<form id="penLookupForm" method="post" action="penLookup.htm">
    <input type="hidden" name="pageAction" />
    <h2>Pen Lookup</h2>
    <table id="table1">
        <tr>
            <td>
                Pen ID:
            </td>
            <td>
                <input name="penID" type="text" />
            </td>
            <td>
                <input type="submit" value="Find" onclick="lookup()"/>
            </td>
        </tr>
    </table>
    <p>
        Pen ID can be display ID aye-XXX-YYY-ZZ or Serial Number XXXXXXXXXXX (HEX) or Serial Number XXXXXXXXXXXXX (DEC).
    </p>
</form>

<c:if test="${errorMessage != null}">
    <p style="color: red">
        <b>ERROR </b>
    </p>
    <p style="color: red">${errorMessage}</p>
</c:if>

<c:if test="${pen != null}">
<hr/>
    <h3>Results</h3>
    <table id="table1" border="1">
        <tr>
            <th>Serial Number</th>
            <th>Display ID</th>
            <th>Pen Name</th>
            <th>Registration Date</th>
            <th>Registration Completed Date</th>
            <th>Registered To</th>
            <c:if test="${regDevice != null}"><th>Action</th></c:if>
        </tr>
        <tr>
            <td>
                ${pen.serialnumber}
            </td>
            <td>
                <a href="<c:url value='/sync/docs/pen/${pen.displayId}'/>">${pen.displayId}</a>
            </td>
            <td>
                ${penName}
            </td>
            <td>
                ${regDevice.created}
            </td>
            <td>
                ${regDevice.completedDate}
            </td>
            <td>
                ${regDevice.user.primaryEmail}
            </td>
            <c:if test="${regDevice != null}">
            <td>
                <form id="unregisterForm" method="post" action="<c:url value='/'/>unregister/${pen.serialnumber}">
                    <input type="button" value="Unregister" onclick="unregisterConfirm(${pen.serialnumber}, '${regDevice.user.primaryEmail}')" />
                    <input type="hidden" name="redirectPath" value="populate later"/>
                </form>
            </td>
            </c:if>
        </tr>
    </table>
</c:if>

</authz:authorize>
</body>
</html>
