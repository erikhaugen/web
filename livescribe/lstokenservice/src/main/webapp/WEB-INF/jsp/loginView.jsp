<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Login</title>
</head>
<body>
    <h2>Login</h2>
    <form method="POST">
        <c:if test="${errResponse != null}">
        <table>
            <tr>
                <td>
                    <h3 style="color:red;">ERROR:</h3>
                </td>
            </tr>
            <tr>
                <td style="color:red;">
                    ${errResponse.responseCode}
                </td>
            </tr>
            <tr>
                <td style="color:red;">
                    ${errResponse.message}
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
        </table>
        </c:if>
        <table>
            <tr>
                <td>Email</td>
                <td><input type="text" id="email" name="email"></td>
            </tr>
            <tr>
                <td>Password</td>
                <td><input type="password" id="password" name="password"></td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="submit" value="Login" name="submitBtn" id="submitBtn">
                </td>
            </tr>
        </table>
    </form>
</body>
</html>