<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
	<c:param name="bundle" value="${bundle}"/>
</c:import>

<c:set var="isSpend" value="${work.configuration.useMaxSpendPricingDisplayModeFlag}"/>

<c:set var="additionalExpenses" value="0"/>
<c:choose>
	<c:when test="${not empty work.activeResource.additionalExpenses && work.activeResource.additionalExpenses > 0}">
		<c:set var="additionalExpenses" value="${work.activeResource.additionalExpenses}"/>
	</c:when>
	<c:when test="${not empty work.payment.additionalExpenses && work.payment.additionalExpenses > 0}">
		<c:set var="additionalExpenses" value="${work.payment.additionalExpenses}"/>
	</c:when>
	<c:when test="${not empty work.pricing.additionalExpenses && work.pricing.additionalExpenses > 0}">
		<c:set var="additionalExpenses" value="${work.pricing.additionalExpenses}"/>
	</c:when>
</c:choose>

<p>
	<c:choose>
		<c:when test="${isAdminOrInternal}">
			You can increase the budget of this assignment if more work is needed, or if the scope of work is larger than expected.
		</c:when>
		<c:otherwise>
			You can request an increase in the budget of this assignment if more work is needed, or if the scope of work is larger than expected.
		</c:otherwise>
	</c:choose>
</p>

<c:if test="is_in_work_company && !isBuyerAuthorizedToEditPrice">
	<c:set var="disable" value="disabled" />
</c:if>

<form class="form-stacked" action='/assignments/budgetincrease/${work.workNumber}' id='budgetIncreaseForm'
	  method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name="price_negotiation" value="1" id="price_negotiation"/>
	<input type="hidden" name="pricing" value="${work.pricing.id}"/>
	<input type="hidden" id="wmFee" name="wmFee" value="${wmfmt:escapeJavaScript(workFee)}"/>
	<input type="hidden" id="maxFee" name="maxFee" value="${wmfmt:escapeJavaScript(maxWorkFee)}"/>
	<input type="hidden" id="isSpend" name="isSpend" value="${wmfn:boolean(isSpend, '1', '0')}"/>
	<input type="hidden" id="isAdmin" name="isAdmin" value="${wmfn:boolean(isAdminOrInternal, '1', '0')}"/>
	<input type="hidden" id="additionalExpenses" name="additionalExpenses" value="${wmfmt:escapeJavaScript(additionalExpenses)}"/>
	<input type="hidden" id="bonus" name="bonus" value="${wmfmt:escapeJavaScript(work.pricing.bonus)}"/>
	<input type="hidden" id="isFlat" name="isFlat" value="${work.pricing.id == pricingStrategyTypes['FLAT']}"/>
	<input type="hidden" id="isPerHour" name="isPerHour" value="${work.pricing.id == pricingStrategyTypes['PER_HOUR']}"/>
	<input type="hidden" id="perHourRate" name="perHourRate" value="${wmfmt:escapeJavaScript(work.pricing.perHourPrice)}"/>
	<input type="hidden" id="isPerUnit" name="isPerUnit" value="${work.pricing.id == pricingStrategyTypes['PER_UNIT']}"/>
	<input type="hidden" id="perUnitPrice" name="perUnitPrice" value="${wmfmt:escapeJavaScript(work.pricing.perUnitPrice)}"/>
	<input type="hidden" id="isBlendedPerHour" name="isBlendedPerHour" value="${work.pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}"/>
	<input type="hidden" id="initialSpend" name="initialSpend" value="${wmfmt:escapeJavaScript(work.pricing.initialPerHourPrice * work.pricing.initialNumberOfHours)}"/>
	<input type="hidden" id="secondaryRate" name="secondaryRate" value="${wmfmt:escapeJavaScript(work.pricing.additionalPerHourPrice)}"/>


	<c:import url="/WEB-INF/views/web/partials/assignments/details/budget_display.jsp">
		<c:param name="readOnly" value="false"/>
	</c:import>

	<div class="wm-action-container">
		<button type="submit" class="button" ${disable}>Submit</button>

		<c:if test="${disable == 'disabled'}">
			<div style="color:red; clear:both;">You are not authorized to submit this request. Please contact your manager or account administrator.</div>
		</c:if>
	</div>
</form>
