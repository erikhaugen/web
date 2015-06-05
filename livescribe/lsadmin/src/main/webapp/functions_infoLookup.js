var rowIdCounter = ${fn:length(criteriaList)};

function addRow() {
    var table = document.getElementById("criteriaTbl");

    // Create new row
    var rowCount = table.rows.length;
    var row = table.insertRow(rowCount);
    row.setAttribute("id", "row" + rowIdCounter);

    // Create cells
    var cell1 = row.insertCell(0);
    var keyDropdown = createKeyDropdown("keyParam");
    cell1.appendChild(keyDropdown);

    var cell2 = row.insertCell(1);
    var comparatorDropdown = createComparatorDropdown("comparatorParam");
    cell2.appendChild(comparatorDropdown);

    var cell3 = row.insertCell(2);
    var element3 = document.createElement("input");
    element3.type = "text";
    element3.setAttribute("name", "valueParam");
    cell3.appendChild(element3);
    
    var cell4 = row.insertCell(3);
    var element4 = document.createElement("input");
    element4.type = "button";
    element4.value = "Remove";
    element4.setAttribute("onclick", "deleteRow(row" + rowIdCounter + ");");
    cell4.appendChild(element4);
    
    rowIdCounter++;
}

function createKeyDropdown(name) {
	var dropdown = document.createElement("select");
	dropdown.setAttribute("name", name);
	
	var option1 = document.createElement("option");
	option1.setAttribute("value", "primaryEmail");
	option1.innerHTML = "primaryEmail";
	dropdown.appendChild(option1);
	
	var option2 = document.createElement("option");
    option2.setAttribute("value", "uid");
    option2.innerHTML = "uid";
    dropdown.appendChild(option2);
    
    var option3 = document.createElement("option");
    option3.setAttribute("value", "enUsername");
    option3.innerHTML = "enUserName";
    dropdown.appendChild(option3);
    
    var option4 = document.createElement("option");
    option4.setAttribute("value", "enUserId");
    option4.innerHTML = "enUserId";
    dropdown.appendChild(option4);
    
    var option5 = document.createElement("option");
    option5.setAttribute("value", "penDisplayId");
    option5.innerHTML = "penDisplayId";
    dropdown.appendChild(option5);
    
    var option6 = document.createElement("option");
    option6.setAttribute("value", "deviceSerialNumber");
    option6.innerHTML = "penSerialNumber";
    dropdown.appendChild(option6);
    
    return dropdown;
}

function createComparatorDropdown(name) {
    var dropdown = document.createElement("select");
    dropdown.setAttribute("name", name);
    
    var option1 = document.createElement("option");
    option1.setAttribute("value", "isEqualTo");
    option1.innerHTML = "isEqualTo";
    dropdown.appendChild(option1);
    
    var option2 = document.createElement("option");
    option2.setAttribute("value", "isNotEqualTo");
    option2.innerHTML = "isNotEqualTo";
    dropdown.appendChild(option2);
    
    var option3 = document.createElement("option");
    option3.setAttribute("value", "isGreaterThan");
    option3.innerHTML = "isGreaterThan";
    dropdown.appendChild(option3);
    
    var option4 = document.createElement("option");
    option4.setAttribute("value", "isGreaterThanOrEqualTo");
    option4.innerHTML = "isGreaterThanOrEqualTo";
    dropdown.appendChild(option4);
    
    var option5 = document.createElement("option");
    option5.setAttribute("value", "isLessThan");
    option5.innerHTML = "isLessThan";
    dropdown.appendChild(option5);
    
    var option6 = document.createElement("option");
    option6.setAttribute("value", "isLessThanOrEqualTo");
    option6.innerHTML = "isLessThanOrEqualTo";
    dropdown.appendChild(option6);
    
    return dropdown;
}

function deleteRow(row) {
	var table = document.getElementById("criteriaTbl");
	table.deleteRow(row.rowIndex);
}

function unregisterConfirm(penSerial, email) {

    var confirmation = confirm("Are you sure you want to unregister pen with serial number " + penSerial + " for user " + email + "?");
    
    if (confirmation) {
    	document.forms["unregisterForm"].action = document.forms["unregisterForm"].action + "/" + penSerial;
        document.forms["unregisterForm"].submit();
    }
    else {
    	return false;
    }
}

function unregisterVectorConfirm(penSerial, email, appId) {

    var confirmation = confirm("Are you sure you want to unregister vector pen with serial number " + penSerial + " for user " + email + "?");
    
    if (confirmation) {
    	document.forms["unregisterForm"].action = document.forms["unregisterForm"].action + "/vector/" + penSerial + "/" + email + "/" + appId + ".html";
        document.forms["unregisterForm"].submit();
        return true;
    }
    else {
    	return false;
    }
}

function changePasswdConfirm(form, email) {
	
	var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789";
    var string_length = 8;
    var randomstring = '';
    for (var i=0; i<string_length; i++) {
        var rnum = Math.floor(Math.random() * chars.length);
        randomstring += chars.substring(rnum,rnum+1);
    }

    var newPasswd = prompt("Enter new password for user " + email, randomstring);
    
    if (newPasswd!=null && newPasswd!="")
    {
        form.elements["newPasswd"].value = newPasswd;
        form.submit();
    } else {
        alert("No change is made!");
        return false;
    }
}

function deleteUserConfirm(form, email) {
	var confirmation = confirm("Are you sure you want to delete user '" + email + "'?");
	
	if (confirmation == false) {
		return false;
	}
	
	var keyParam = document.getElementsByName("keyParam")[0].value;
	var comparatorParam = document.getElementsByName("comparatorParam")[0].value;
	var valueParam = document.getElementsByName("valueParam")[0].value;
	
	var redirectPathElement = form.elements["redirectPath"];
	redirectPathElement.value = "<c:url value='/infoLookup.htm' />" + "?keyParam=" + keyParam + "&comparatorParam=" + comparatorParam + "&valueParam=" + valueParam;
	
	return true;
}

function clearAuthorizationConfirm(form, email) {
	var confirmation = confirm("Are you sure you want to clear Evernote authorization for user '" + email + "'?");
	
	if (confirmation == false) {
		return false;
	}
	
	return true;
}

function getSelectedValues() {
	var submittedKeyParam = document.getElementById("submittedKeyParam").value;
	var submittedComparatorParam = document.getElementById("submittedComparatorParam").value;
	var submittedValueParam = document.getElementById("submittedValueParam").value;
	
	return submittedKeyParam + "&" + submittedComparatorParam + "&" + submittedValueParam;
}

function deleteDocument(docId, displayId) {
	
	var confirmation = confirm("Are you sure you want to delete the document '" + docId + "' for the pen '" + displayId + "'?");

	if (confirmation) {
		document.forms["deleteDocumentForm"].submit();
	}
}