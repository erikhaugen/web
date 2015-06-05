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
	<title>Livescribe Admin Tool - User Lookup</title>
	<style type="text/css">
		<%@ include file="/style.css" %>
	</style>
<script language="javascript">
<%@ include file="/functions_infoLookup.js" %>
</script>
</head>
<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<authz:authorize access="!hasAnyRole('superUser', 'lsadminUser', 'deleteInformationUser')">
	<c:redirect url="/login.jsp">
    	<c:param name="unauthorizedUser" value="true"/>
	</c:redirect>
</authz:authorize>

<authz:authorize access="hasAnyRole('superUser', 'lsadminUser', 'deleteInformationUser')">

<form method="post" action="infoLookup.htm">
    <h2>Info Lookup</h2>
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
                    <option value="enUsername" <c:if test="${criteria.queryField.field eq 'enUsername'}">selected="selected"</c:if>>Evernote Username</option>
                    <option value="penSerialNumber" <c:if test="${criteria.queryField.field eq 'penSerialNumber'}">selected="selected"</c:if>>Pen Serial Number</option>
                    <option value="penDisplayId" <c:if test="${criteria.queryField.field eq 'penDisplayId'}">selected="selected"</c:if>>Pen Display Id</option>

                    <!--
                    <option value="deviceSerialNumber" <c:if test="${criteria.queryField.field eq 'deviceSerialNumber'}">selected="selected"</c:if>>penID</option>
                    <option value="uid" <c:if test="${criteria.queryField.field eq 'uid'}">selected="selected"</c:if>>uid</option>
                    <option value="enUserId" <c:if test="${criteria.queryField.field eq 'enUserId'}">selected="selected"</c:if>>enUserId</option>
                    <option value="penDisplayId" <c:if test="${criteria.queryField.field eq 'penDisplayId'}">selected="selected"</c:if>>penDisplayId</option>
                    <option value="deviceSerialNumber" <c:if test="${criteria.queryField.field eq 'deviceSerialNumber'}">selected="selected"</c:if>>penSerialNumber</option>
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
	
	<c:if test="${not empty param.errorMessage}">
		<h3><b><font color="red">&nbsp;&nbsp;&nbsp;&nbsp;${param.errorMessage}</font></b></h3>
	</c:if>
	
	<h2>Search Result</h2>
	<c:if test="${fn:length(users) eq 0}">
	   <h3>User Info: No result found</h3>
	</c:if>
	<c:if test="${fn:length(users) gt 0}">
		<h3>User Info: (Found <c:out value="${fn:length(users)}" /> results out of max 200 results.)</h3>
		<table border="1" cellpadding="10">
		    <tr>
		        <th>User Email</th>
		        <th>User ID</th>
		        <th>Evernote Authorization</th>
		        <th>Registered Pens</th>
		        <th>Synced Documents</th>
		    </tr>
					<c:forEach var="user" items="${users}">
						<c:set var="authorizations" value="${user.evernoteAuthorizations}" />
						<c:set var="registeredPens" value="${user.registeredDevices}" />
						<tr>
							<td><a
								href="<c:url value='/infoLookup.htm'><c:param name="keyParam" value="primaryEmail"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${user.primaryEmail}"/></c:url>">${user.primaryEmail}</a></td>
							<td>${user.uid}</td>
							<c:if test="${empty authorizations}">
								<td><center>This user has NO Evernote authorizations.</center></td>
							</c:if>
							<c:if test="${not empty authorizations}">
							<td>
								<!-- The inner table containing the user's Evernote Authorization info -->
								<table border="0" cellpadding="10">
									<tr>
										<th>User ID</th>
										<th>User Name</th>
										<th>Auth Expiration Date</th>
		    						</tr>
									<c:forEach var="authorization" items="${authorizations}">
										<tr>
											<c:set var="isPrimary" value="${authorization.primary}" />
											<c:if test="${isPrimary eq true}">
												<td><font color="green"><strong>${authorization.evernoteUserId}</strong></font></td>
												<td><font color="green"><strong>${authorization.evernoteUserName} (Syncing Account)</strong></font></td>
												<td><c:set var="oauthToken" value="${authorization.evernoteOAuthToken}" />
													<c:set var="isOAuthExpired" value='<%=com.livescribe.admin.utils.EvernoteAPIUtils.isEvernoteOAuthExpired((String) pageContext.getAttribute("oauthToken"))%>' />
													<c:if test="${isOAuthExpired eq true}">
														<font color="red"><strong>EXPIRED</strong></font>
													</c:if> 
													<c:if test="${isOAuthExpired eq false}">
														<font color="green"><strong><fmt:formatDate type="both" value="${authorization.evernoteAuthExpirationDate}" /></strong></font>
														<authz:authorize access="hasAnyRole('superUser', 'deleteInformationUser')">
															<td><c:set var="uid" value="${user.uid}" /> 
																<c:set var="hasENDataSynced" value='<%=com.livescribe.admin.utils.EvernoteAPIUtils.hasEvernoteSyncedData((String) pageContext.getAttribute("uid"))%>' />
																<form id="clearAuthorizationForm" method="post" action="<c:url value='/auth/remove/${user.uid}' />">
																	<c:if test="${hasENDataSynced eq true}">
																		<input type="button" value="De-authorize" disabled="disabled" />
																	</c:if>
																	<c:if test="${hasENDataSynced eq false}">
																		<input type="submit" value="De-authorize" onclick="return clearAuthorizationConfirm(this.parentNode, '${user.primaryEmail}');" />
																	</c:if>
																	<input type="hidden" name="redirectPath" id="redirectPath" value="<c:url value='/infoLookup.htm'><c:param name="keyParam" value="primaryEmail"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${user.primaryEmail}"/></c:url>" />
																</form>
															</td>
														</authz:authorize>
													</c:if>
												</td>
											</c:if>
											<c:if test="${isPrimary eq false}">
												<td>${authorization.evernoteUserId}</td>
												<td>${authorization.evernoteUserName}</td>
												<td><c:set var="oauthToken" value="${authorization.evernoteOAuthToken}" />
													<c:set var="isOAuthExpired" value='<%=com.livescribe.admin.utils.EvernoteAPIUtils.isEvernoteOAuthExpired((String) pageContext.getAttribute("oauthToken"))%>' />
													<c:if test="${isOAuthExpired eq true}">
														<font color="red"><strong>EXPIRED</strong></font>
													</c:if> 
													<c:if test="${isOAuthExpired eq false}">
														<fmt:formatDate type="both" value="${authorization.evernoteAuthExpirationDate}" />
														<authz:authorize access="hasAnyRole('superUser', 'deleteInformationUser')">
															<td><c:set var="uid" value="${user.uid}" /> 
																<c:set var="hasENDataSynced" value='<%=com.livescribe.admin.utils.EvernoteAPIUtils.hasEvernoteSyncedData((String) pageContext.getAttribute("uid"))%>' />
																<form id="clearAuthorizationForm" method="post" action="<c:url value='/auth/remove/${user.uid}' />">
																	<c:if test="${hasENDataSynced eq true}">
																		<input type="button" value="De-authorize" disabled="disabled" />
																	</c:if>
																	<c:if test="${hasENDataSynced eq false}">
																		<input type="submit" value="De-authorize" onclick="return clearAuthorizationConfirm(this.parentNode, '${user.primaryEmail}');" />
																	</c:if>
																	<input type="hidden" name="redirectPath" id="redirectPath" value="<c:url value='/infoLookup.htm'><c:param name="keyParam" value="primaryEmail"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${user.primaryEmail}"/></c:url>" />
																</form>
															</td>
														</authz:authorize>
													</c:if>
												</td>
											</c:if>
										</tr>
									</c:forEach>
								</table>
							</td>
							</c:if>
							
							<c:if test="${empty registeredPens}">
								<td><center>This user has NO registered pens.</center></td>
							</c:if>
							<c:if test="${not empty registeredPens}">
							<td>
								<!-- The inner table containing the user's Registered Devices info -->
								<table border="0" cellpadding="10">
									<tr>
										<th>Display ID</th>
										<th>Serial Number</th>
										<th>Serial Number (HEX)</th>
										<th>Device Type</th>
										<th>Registration Completed Date</th>
										<th>Pen Name</th>
										<th>Action</th>
		    						</tr>
											
									<form id="unregisterForm" method="post" action="<c:url value='/unregister'/>">
										<c:forEach var="registeredPen" items="${registeredPens}">
											<tr>
												<td><a href="<c:url value='/sync/docs/user/${user.uid}/pen/${registeredPen.displayId}'/>">${registeredPen.displayId}</a></td>
												<td>${registeredPen.serialNumber}</td>
												<td>${registeredPen.hexSerialNumber}</td>
												<td>${registeredPen.type}</td>
												<td><fmt:formatDate type="both" value="${registeredPen.registrationCompletionDate}" /></td>
												<td>${registeredPen.name}</td>
												<authz:authorize access="hasAnyRole('deleteInformationUser', 'superUser')">
												<td>
													<input type="button" value="Unregister" onclick="unregisterConfirm(${registeredPen.serialNumber}, '${user.primaryEmail}')" />
												</td>
												</authz:authorize>
											</tr>
										</c:forEach>
									    <input type="hidden" name="redirectPath" value="<c:url value='../infoLookup.htm'><c:param name="keyParam" value="primaryEmail"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${users[0].primaryEmail}"/></c:url>"/>
									</form>

								</table>
							</td>
							</c:if>
							
							<td>
								<a href="<c:url value='/sync/docs/user/${user.uid}'/>">All Sync Docs</a>
							</td>
						</tr>					

                        <!-- Display Delete User/Change password button in a seperate row when search result has only 1 match -->
                        <authz:authorize access="hasAnyRole('deleteInformationUser', 'superUser')">
                            <c:set var="backURL"><c:url value="/infoLookup.htm"><c:param name="keyParam" value="primaryEmail"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${user.primaryEmail}"/></c:url></c:set>
                            <tr>
                                <td>
                                    <form id="changePasswdForm" method="post"
                                        action="<c:url value='/changePasswd.xml'><c:param name='email' value='${user.primaryEmail}'/></c:url>">
                                        <input type="submit" value="Change Password"
                                            onclick="return changePasswdConfirm(this.parentNode, '${user.primaryEmail}');" />
                                        <input type="hidden" name="newPasswd" id="newPasswd"/>
                                        <input type="hidden" name="redirectPath" id="redirectPath"
                                            value="${backURL}" />
                                    </form>
                                </td>
                                <td>
									<form id="deleteUserForm" method="post"
										action="<c:url value='/deleteuser.xml'><c:param name='email' value='${user.primaryEmail}'/></c:url>">
										<input type="submit" value="Delete User"
											onclick="return deleteUserConfirm(this.parentNode, '${user.primaryEmail}');" />
										<input type="hidden" name="redirectPath" id="redirectPath"
											value="<c:url value='/infoLookup.htm'></c:url>" />
									</form>
								</td>
                            </tr>
                        </authz:authorize>

					</c:forEach>
				</table>
	</c:if>
	


    <%-- for unregister pen only--%>
    <c:if test="${not empty unregisteredDevices}">
        <h3>Unregistered Pens Info: (Found ${fn:length(unregisteredDevices)} results out of max 200 results.)</h3>
        <table border="1" cellpadding="10">
            <tr>
                <th>Display ID</th>
                <th>Serial Number (DEC)</th>
                <th>Serial Number (HEX)</th>
                <th>Device Type</th>
            </tr>
            <c:forEach var="unregisteredDevice" items="${unregisteredDevices}">
            <tr>
                <c:if test="${not (unregisteredDevice.type eq 'WIFI') and not (unregisteredDevice.type eq 'Vector')}">
                    <td>${unregisteredDevice.displayId}</td>
                </c:if>
                <c:if test="${(unregisteredDevice.type eq 'WIFI')}">
                    <td><a href="<c:url value='/sync/docs/pen/${unregisteredDevice.displayId}'/>">${unregisteredDevice.displayId}</a></td>
                </c:if>
                <c:if test="${(unregisteredDevice.type eq 'Vector')}">
                    <td><a href="<c:url value='/vectorInfoLookup.htm'><c:param name="keyParam" value="penDisplayId"/><c:param name="comparatorParam" value="isEqualTo"/><c:param name="valueParam" value="${unregisteredDevice.displayId}"/></c:url>">${unregisteredDevice.displayId}</a></td>
                </c:if>
                <td>${unregisteredDevice.serialNumber}</td>
                <td>${unregisteredDevice.hexSerialNumber}</td>
                <td>${unregisteredDevice.type}</td>
            </tr>
            </c:forEach>
        </table>
        <c:if test="${not (unregisteredDevice.type eq 'WIFI') and not (unregisteredDevice.type eq 'Vector') }">
            <p style="color: red">
                <b>NOTE: </b>With non-WIFI pen, please go to <a href="<%=AppProperties.getIntance().getProperty("lssupport.url")%>/wa/penSearch">Pen Lookup on LSSupport</a> for further information. 
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
