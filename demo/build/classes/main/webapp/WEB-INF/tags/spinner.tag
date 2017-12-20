<%@ tag description="Radio Input" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="classlist" required="false" %>

<c:choose>
	<c:when test="${shouldShowMDL}">
		<div class="wm-spinner mdl-spinner mdl-js-spinner is-active ${classlist}" id="${id}"></div>
	</c:when>
	<c:otherwise>
		<div class="wm-spinner ${classlist}" id="${id}">
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
			<div class="wm-spinner--blade"></div>
		</div>
	</c:otherwise>
</c:choose>
