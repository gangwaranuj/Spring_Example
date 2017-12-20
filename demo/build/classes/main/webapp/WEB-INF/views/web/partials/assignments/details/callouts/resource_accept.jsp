<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:set var="eligible" value="${eligibility.eligible}"/>
<c:set var="hidePricing" value="${currentUser.companyHidesPricing and not is_in_work_company}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<c:if test="${!eligible}">
	<p><strong>You are not eligible to apply for this assignment. Please complete the eligibility requirements first.</strong></p>
	<div class="well-b2">
		<c:import url='/WEB-INF/views/web/partials/assignments/details/eligibility.jsp'/>
	</div>
</c:if>
<c:choose>
	<c:when test="${hidePricing}">
		You do not have permission to accept this assignment.<br>
		Your company's Team Agent must accept on your behalf.
	</c:when>
	<c:otherwise>
		<div class="alert-actions clear">
			<c:choose>
				<c:when test="${hasScheduleConflicts}">
					<button class="accept conflict_apply button" <c:if test="${!eligible}">disabled="disabled"</c:if>>Accept</button>
				</c:when>
				<c:otherwise>
					<a class="accept assignment_action_accept button" href="/assignments/accept/${work.workNumber}" <c:if test="${!eligible}">disabled="disabled"</c:if>>Accept</a>
				</c:otherwise>
			</c:choose>
			<c:if test="${is_invited_resource}">
				<a class="cancel button worker-decline" href="javascript:void(0);">Decline</a>
			</c:if>
			<c:if test="${not workResponse.workBundle and !(workResponse.viewingResource.user.laneType.value eq laneTypes['LANE_1'])}">
				<a class="negotiate_action button" href="/assignments/negotiate/${work.workNumber}" <c:if test="${!eligible}">disabled="disabled"</c:if>>Counteroffer</a>
			</c:if>
		</div>
	</c:otherwise>
</c:choose>
