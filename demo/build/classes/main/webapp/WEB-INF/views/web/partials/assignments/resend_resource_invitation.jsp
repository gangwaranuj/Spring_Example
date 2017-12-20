<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/resend_resource_invitation/${work.workNumber}" method="POST" class="form-stacked">
	<wm-csrf:csrfToken />
	<c:forEach items="${workerNumbers}" var="strId">
		<c:set var="number" value="${wmfn:strip(strId, '//')}" />
		<input type="hidden" name="workerNumber" value="${number}" />
	</c:forEach>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}"/>
	</c:import>

	<p class="alert">You are about to resend this assignment to <strong><c:out value="${fn:length(workerNumbers)}"/>
		selected <c:out value="${wmfmt:pluralize('worker', fn:length(workerNumbers))}"/></strong>.</p>

	<c:choose>
		<c:when test="${work.schedule.range}">
			<p><span class="span2"><strong>Date: </strong></span> ${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy', work.schedule.from, work.timeZone)}
				to ${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy', work.schedule.through, work.timeZone)}</p>
			<p><span class="span2"><strong>Time: </strong></span> ${wmfmt:formatMillisWithTimeZone('h:mm a', work.schedule.from, work.timeZone)}
				to ${wmfmt:formatMillisWithTimeZone('h:mm a z', work.schedule.through, work.timeZone)}</p>
		</c:when>
		<c:otherwise>
			<p><span class="span2"><strong>Date:</strong></span> ${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy', work.schedule.from, work.timeZone)}</p>
			<p><span class="span2"><strong>Time:</strong></span> ${wmfmt:formatMillisWithTimeZone('h:mm a z', work.schedule.from, work.timeZone)}</p>
		</c:otherwise>
	</c:choose>

	<p>
		<span class="span2"><strong>Value:</strong>
			<span class="tooltipped tooltipped-n" aria-label="Amount paid to worker">
				<i class="wm-icon-question-filled"></i>
			</span>
		</span>
		<c:choose>
			<c:when test="${work.pricing.id == PricingStrategyType['FLAT']}">
				<fmt:formatNumber value="${work.pricing.flatPrice}" currencySymbol="$" type="currency"/>
			</c:when>
			<c:when test="${work.pricing.id == PricingStrategyType['PER_HOUR']}">
				<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxNumberOfHours)}"/>
				<strong><fmt:formatNumber value="${work.pricing.perHourPrice * work.pricing.maxNumberOfHours}" currencySymbol="$" type="currency"/> </strong>
				(<fmt:formatNumber value="${work.pricing.perHourPrice}" currencySymbol="$" type="currency"/> per hour
				<small>up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if></small>)
			</c:when>
			<c:when test="${work.pricing.id == PricingStrategyType['PER_UNIT']}">
				<strong><fmt:formatNumber value="${work.pricing.maxUnitPrice}" currencySymbol="$" type="currency"/></strong>
				(<small><fmt:formatNumber value="${work.pricing.perUnitPrice}" currencySymbol="$" type="currency" maxFractionDigits="3"/> per unit
				up to <c:out value="${work.pricing.maxNumberOfUnits}"/> units</small>)
			</c:when>
			<c:when test="${work.pricing.id == PricingStrategyType['BLENDED_PER_HOUR']}">
				<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.initialNumberOfHours)}" scope="page"/>
				<c:set var="maxAdditionalHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxBlendedNumberOfHours)}"/>
				<strong><fmt:formatNumber value="${work.pricing.initialPerHourPrice * work.pricing.initialNumberOfHours}" currencySymbol="$" type="currency"/></strong>
				(<small><fmt:formatNumber value="${work.pricing.initialPerHourPrice}" currencySymbol="$" type="currency"/> per hour
				<small>up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if></small>)
				<strong><fmt:formatNumber value="${work.pricing.additionalPerHourPrice * work.pricing.maxBlendedNumberOfHours}" currencySymbol="$" type="currency"/></strong>
				(<small><fmt:formatNumber value="${work.pricing.additionalPerHourPrice}" currencySymbol="$" type="currency"/> per additional hour
				up to ${maxAdditionalHoursMinutes.hours}h<c:if test="${maxAdditionalHoursMinutes.minutes > 0}"> ${maxAdditionalHoursMinutes.minutes}m</c:if></small>)
			</c:when>
		</c:choose>
	</p>

	<p>If you have updated the date or price, the updated information will be provided.</p>

	<div class="wm-action-container">
		<button type="submit" class="button -primary">Resend Assignment</button>
	</div>
</form>
