<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Setup Your Pen</title>
    <script type="text/javascript" language="javascript">
    function nextInputFocus(currentInputId, nextInputId) {
    	currentInput = document.getElementById(currentInputId);
    	if (currentInput.value.length == 4) {
    		nextInput = document.getElementById(nextInputId);
    		nextInput.focus();
    	}
    }
    
    function prepareSubmit() {
    	penId1 = document.getElementById("penId1");
    	penId2 = document.getElementById("penId2");
    	penName = document.getElementById("penName");
    	code = document.getElementById("code");
    	
    	// Do validation
    	if (penId1.value.length < 4 || penId2.value.length < 4) {
    		alert("Invalid Pen ID format!");
    		return false;
    	}
    	
        if (penName.value.length == 0) {
            alert("Please enter name for your pen.");
            return false;
        }
    	
    	code.value = penId1.value + penId2.value + "";
    	return true;
    }
    
    </script>
</head>
<body>
    <h2>Setup your pen</h2>
    <form method="POST" action="penSetup.htm">
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
                <td colspan="2"><h3>Enter your penID to link it to your Livescribe account</h3></td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="text" name="penId1" id="penId1" maxlength="4" style="width: 50px" onkeyup="javascript:nextInputFocus('penId1', 'penId2')"> - 
                    <input type="text" name="penId2" id="penId2" maxlength="4" style="width: 50px">
                    <input type="hidden" name="code" id="code" value="">
                </td>
            </tr>
            <tr>
                <td colspan=21">&nbsp;</td>
            </tr>
            <tr>
                <td colspan=21">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2"><h3>Enter a name for your pen</h3></td>
            </tr>
            <tr>
                <td colspan="2"><input type="text" name="penName" id="penName" maxlength="40" style="width: 250px"></td>
            </tr>
            <tr>
                <td colspan=21">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="Now let's connect your pen to a network ->" name="submitBtn" id="submitBtn" onclick="return prepareSubmit();"></td>
            </tr>
        </table>
    </form>
</body>
</html>