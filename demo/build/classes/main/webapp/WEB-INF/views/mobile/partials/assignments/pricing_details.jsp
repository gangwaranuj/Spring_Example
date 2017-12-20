<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<ul class="pricing-details">
	<li><h3>Pricing Details</h3></li>
	<c:choose>
		<c:when test="${pricingType.equals('INTERNAL')}">
			<li>
				<div>This assignment is internal to your organization and does not have an associated price.</div>
			</li>
		</c:when>
		<c:when test="${work.pricing.id == PricingStrategyType.FLAT}">
			<li>
				<div>Flat Fee</div>
				<div><fmt:formatNumber value="${work.pricing.flatPrice}" currencySymbol="$" type="currency"/></div>
			</li>
		</c:when>
		<c:when test="${work.pricing.id == PricingStrategyType.PER_HOUR}">
			<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxNumberOfHours)}"/>
			<li>
				<div>
					<span class="currency"><fmt:formatNumber value="${work.pricing.perHourPrice}"
						type="currency"/> </span>
					<span>/hr </span>
						<span class="pricing-units">(up to ${maxHoursMinutes.hours}h<c:if
							test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</span>
				</div>
			</li>
		</c:when>
		<c:when test="${work.pricing.id == PricingStrategyType.PER_UNIT}">
			<li>
				<div>
					<span class="currency"><fmt:formatNumber value="${work.pricing.perUnitPrice}"
						type="currency" maxFractionDigits="3"/> </span>
					<span>per unit </span>
						<span class="pricing-units">(up to <fmt:formatNumber
							value="${work.pricing.maxNumberOfUnits}" type="number" maxFractionDigits="0"/> units)</span>
				</div>
			</li>
		</c:when>
		<c:otherwise> <%-- Blended Per Hour --%>
			<c:set var="maxHoursMinutes"
			       value="${wmfmt:getHoursAndMinutes(work.pricing.initialNumberOfHours)}" scope="page"/>
			<c:set var="maxAdditionalHoursMinutes"
			       value="${wmfmt:getHoursAndMinutes(work.pricing.maxBlendedNumberOfHours)}"/>
			<li>
				<div>
					<span class="currency"><fmt:formatNumber value="${work.pricing.initialPerHourPrice}"
						type="currency"/> </span>
					per hour
						<span class="pricing-units">(up to ${maxHoursMinutes.hours}h<c:if
							test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</span>
				</div>
			</li>
			<li>
				<div>
					<span class="currency"><fmt:formatNumber value="${work.pricing.additionalPerHourPrice}"
						type="currency"/> </span>
					per additional hour
						<span class="pricing-units">(up to ${maxAdditionalHoursMinutes.hours}h<c:if
							test="${maxAdditionalHoursMinutes.minutes > 0}"> ${maxAdditionalHoursMinutes.minutes}m</c:if>)</span>
				</div>
			</li>
		</c:otherwise>
	</c:choose>
	<jsp:include page="/WEB-INF/views/mobile/partials/assignments/pricing/completion_inputs.jsp"/>
	<c:if test="${not empty work.pricing.additionalExpenses && ((not empty work.activeResource.expenseNegotiation && work.activeResource.expenseNegotiation.approvalStatus.code == 'approved') || work.approvedAdditionalExpenses)}">
		<li>
			<div>Additional Expenses:</div>
			<div>
				<input alt="integer" class='additional-expenses' name='additional_expenses'
					value="<fmt:formatNumber value="${work.pricing.additionalExpenses}" minFractionDigits="2" maxFractionDigits="2" />"/>
			</div>
		</li>
		<li>
			<div class="error additional-expenses-maxvalue-error-txt" style="display: none;" ></div>
		</li>
	</c:if>
	<c:if test="${not empty work.pricing.bonus && ((not empty work.activeResource.bonus && work.activeResource.bonusNegotiation.approvalStatus.code == 'approved') || work.approvedBonus)}">
		<li>
			<div>Bonus:</div>
			<div>
				$<fmt:formatNumber value="${work.pricing.bonus}" minFractionDigits="2" maxFractionDigits="2"/>
				<input type="hidden" class='bonus' name="bonus" value="${work.pricing.bonus}"/>
			</div>
		</li>
	</c:if>
	<li class="final-value-container"
	    <c:if test="${pricingType.equals('INTERNAL')}">style="display: none;"</c:if> >
		<div>
			Final Assignment Value
			<small><a href="javascript:void(0);" class="edit-pricing-earnings-outlet">adjust</a></small>
		</div>
		<div>
			<span class="pricing-earnings-outlet"></span>
			<input style="display: none;" type="text" class="override-price" alt="integer" name="override_price"/>
		</div>
	</li>
	<li>
		<div class="error max-spend-limit-error-txt" style="display: none;"></div>
	</li>
</ul>
