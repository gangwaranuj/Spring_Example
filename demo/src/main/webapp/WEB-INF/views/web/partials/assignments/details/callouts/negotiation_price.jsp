<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="negotiation" value="${work.activeResource.budgetNegotiation}"/>

<div class="well-b2">
	<div class="well-content">
		<h4>Budget Increase Request
			<span class="label label-${is_active_resource ? "warning" : "success"}">
			${is_active_resource ? "Pending Approval" : "Action Required"}
			</span>
		</h4>

		<c:set var="budgetIncreaseText" value=""/>
		<c:choose>
			<c:when test="${work.pricing.id == pricingStrategyTypes['FLAT']}">
				<c:set var="increaseValue" value="${negotiation.pricing.maxSpendLimit - work.pricing.maxSpendLimit}"/>
				<fmt:formatNumber var="formatted" value="${increaseValue}" type="currency"/>
				<c:set var="budgetIncreaseText" value="${formatted}"/>
			</c:when>
			<c:when test="${work.pricing.id == pricingStrategyTypes['PER_HOUR']}">
				<c:set var="increaseValue" value="${negotiation.pricing.maxNumberOfHours - work.pricing.maxNumberOfHours}"/>
				<fmt:formatNumber var="formatted" value="${increaseValue}" type="number" maxFractionDigits="1"/>
				<c:set var="budgetIncreaseText" value="${formatted} ${wmfmt:pluralize('hour', increaseValue)}"/>
			</c:when>
			<c:when test="${work.pricing.id == pricingStrategyTypes['PER_UNIT']}">
				<c:set var="increaseValue" value="${negotiation.pricing.maxNumberOfUnits - work.pricing.maxNumberOfUnits}"/>
				<fmt:formatNumber var="formatted" value="${increaseValue}" type="number" maxFractionDigits="0"/>
				<c:set var="budgetIncreaseText" value="${formatted} ${wmfmt:pluralize('unit', increaseValue)}"/>
			</c:when>
			<c:when test="${work.pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}">
				<c:set var="increaseValue"
					   value="${negotiation.pricing.maxBlendedNumberOfHours - work.pricing.maxBlendedNumberOfHours}"/>
				<fmt:formatNumber var="formatted" value="${increaseValue}" type="number" maxFractionDigits="0"/>
				<c:set var="budgetIncreaseText" value="${formatted} additional ${wmfmt:pluralize('hour', increaseValue)}"/>
			</c:when>
		</c:choose>

		<c:choose>
			<c:when test="${is_active_resource}">
					<p><c:out value="${negotiation.requestedBy.name.getFullName()}" /> requested a Budget Increase of <c:out value="${budgetIncreaseText}" /></p>
			</c:when>
			<c:when test="${isAdminOrInternal}">
					<p>Budget Increase request of <c:out value="${budgetIncreaseText}" /> by
						<c:out value="${negotiation.requestedBy.name.getFullName()}" />
					</p>
			</c:when>
		</c:choose>

		<c:if test="${not empty negotiation.note}">
			<blockquote><em><c:out value="${negotiation.note.text}"/></em></blockquote>
		</c:if>

		<c:import url="/WEB-INF/views/web/partials/assignments/details/budget_display.jsp">
			<c:param name="readOnly" value="true"/>
		</c:import>

		<c:choose>
			<c:when test="${is_active_resource}">
				<form action="/assignments/cancel_negotiation/${work.workNumber}" class="wm-action-container">
					<input type="hidden" name="id" value="${negotiation.encryptedId}"/>
					<button type="submit" class="accept-negotiation button">Cancel Request</button>
				</form>
			</c:when>
			<c:otherwise>
				<sec:authorize access="!principal.editPricingCustomAuth">
					<c:set var="disable" value="disabled" />
				</sec:authorize>
				<form action="/assignments/accept_negotiation/${work.workNumber}" class="wm-action-container">
					<input type="hidden" name="id" value="${negotiation.encryptedId}"/>
					<a rel="prompt_decline_negotiation" data-negotiation-id="${negotiation.encryptedId}" class="decline-negotiation button -small" ${disable}>Decline</a>
					<button type="submit" class="accept-negotiation button -small" ${disable}>Approve</button>
				</form>

				<c:if test="${disable == 'disabled'}">
					<div class="alert alert-danger">You are not authorized to approve or decline this request. Please contact your manager or account administrator to approve or decline.</div>
				</c:if>
			</c:otherwise>
		</c:choose>
	</div>
</div>
