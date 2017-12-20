<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="Not Confirmed" bodyclass="page-public">
	<c:choose>
		<c:when test="${not empty param.un}">
			<c:set var="userNumber" value="${param.un}" scope="request"/>
		</c:when>
		<c:otherwise>
			<c:set var="userNumber" value="${un}" scope="request"/>
		</c:otherwise>
	</c:choose>

	<div class="container">
		<h1>You still need to confirm your email address!</h1>
		<p>If you believe that this is a mistake, please contact your company administrator for access.</p>
		<p><a class="button -primary" href="/user/resend_confirmation_email/<c:out value="${userNumber}"/>">Resend Confirmation</a></p>
		<p><c:import url="/WEB-INF/views/web/partials/message.jsp" /></p>
	</div>

</wm:public>
