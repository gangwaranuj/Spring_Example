<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:choose>
	<%--Internal is dealt with separately--%>
	<c:when test="${work.pricing.id == PricingStrategyType.FLAT}">
		<li class="pricing-type flat-fee">
			<div>Flat Fee: </div>
			<div class="currency"><fmt:formatNumber value="${work.pricing.flatPrice}" currencySymbol="$" type="currency"/></div>
		</li>
	</c:when>
	<c:when test="${work.pricing.id == PricingStrategyType.PER_HOUR}">
		<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxNumberOfHours)}"/>
		<li class="pricing-type per-hour">
			<div class="currency"><fmt:formatNumber value="${work.pricing.perHourPrice}" currencySymbol="$" type="currency"/> </div>
			<div>/hr </div>
			<div class="pricing-units">(up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</div>
		</li>
	</c:when>

	<c:when test="${work.pricing.id == PricingStrategyType.PER_UNIT}">
		<li class="pricing-type per-unit">
			<div class="currency"><fmt:formatNumber value="${work.pricing.perUnitPrice}" currencySymbol="$" type="currency"/> </div>
			<div>/unit </div>
			<div class="pricing-units">(up to <fmt:formatNumber value="${work.pricing.maxNumberOfUnits}" type="number" maxFractionDigits="0"/> units)</div>
		</li>
	</c:when>
	<c:otherwise> <%-- Blended Per Hour --%>
		<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.initialNumberOfHours)}" scope="page"/>
		<c:set var="maxAdditionalHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxBlendedNumberOfHours)}"/>
		<li class="pricing-type blended-per-hour-primary">
			<div class="currency"><fmt:formatNumber value="${work.pricing.initialPerHourPrice}" currencySymbol="$" type="currency"/> </div>
			<div>per hour </div>
			<div class="pricing-units">(up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</div>
		</li>
		<li class="pricing-type blended-per-hour-secondary">
			<div class="currency"><fmt:formatNumber value="${work.pricing.additionalPerHourPrice}" currencySymbol="$" type="currency"/> </div>
			<div>per additional hour </div>
			<div class="pricing-units">(up to ${maxAdditionalHoursMinutes.hours}h<c:if test="${maxAdditionalHoursMinutes.minutes > 0}"> ${maxAdditionalHoursMinutes.minutes}m</c:if>)</div>
		</li>
	</c:otherwise>
</c:choose>
