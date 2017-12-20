<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="global.thankyou" var="global_thankyou"/>
<wm:public
	pagetitle="${global_thankyou}"
	bodyclass="page-public"
>

	<div class="container">
		<h3><fmt:message key="public.thankyou"/></h3>
		<c:choose>
			<c:when test="${thankyouType == 'client'}">
				<div class="thankyou-text"><fmt:message key="public.thankyou_client"/></div>
			</c:when>
			<c:otherwise>
				<div class="thankyou-text"><fmt:message key="public.thankyou_worker"/></div>
			</c:otherwise>
		</c:choose>
	</div>
</wm:public>
