<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:set var="pricing" value="${work.pricing}"/>
<c:set var="additionalExpenses" value="0"/>
<c:set var="displayOverrideExpenses" value="${
	not empty work.activeResource.additionalExpenses
	&& not empty work.pricing.additionalExpenses
	&& work.activeResource.additionalExpenses < work.pricing.additionalExpenses
	&& work.status.code == workStatusTypes['COMPLETE']}"/>
<c:set var="bonus" value="0"/>

<strong>Pricing Details</strong>

<table>
	<tbody>
		<c:if test="${work.status.code == 'active' and work.pricing.overridePrice > 0}">
			<tr>
				<td><small class="meta">The work was previously approved for</small></td>
				<td><small class="meta"><fmt:formatNumber value="${work.pricing.overridePrice}" currencySymbol="$" type="currency"/></small></td>
			</tr>
		</c:if>

		<c:choose>
			<c:when test="${pricing.id == pricingStrategyTypes['FLAT']}">
				<tr>
					<td class="details">Flat Fee</td>
					<td><fmt:formatNumber value="${pricing.flatPrice}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:when>
			<c:when test="${pricing.id == pricingStrategyTypes['PER_HOUR']}">
				<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxNumberOfHours)}"/>
				<tr>
					<td class="details"><fmt:formatNumber value="${pricing.perHourPrice}" currencySymbol="$" type="currency"/> per hour
						<small>(up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</small>
					</td>
					<td><fmt:formatNumber value="${pricing.perHourPrice * pricing.maxNumberOfHours}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:when>
			<c:when test="${pricing.id == pricingStrategyTypes['PER_UNIT']}">
				<tr>
					<td class="details"><fmt:formatNumber value="${pricing.perUnitPrice}" currencySymbol="$" type="currency" maxFractionDigits="3"/> per unit
						<small>(up to <c:out value="${pricing.maxNumberOfUnits}"/> units)</small>
					</td>
					<td><fmt:formatNumber value="${pricing.maxUnitPrice}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:when>
			<c:when test="${pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}">
				<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.initialNumberOfHours)}" scope="page"/>
				<c:set var="maxAdditionalHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxBlendedNumberOfHours)}"/>
				<tr>
					<td class="details"><fmt:formatNumber value="${pricing.initialPerHourPrice}" currencySymbol="$" type="currency"/> per hour
						<small>(up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</small>
					</td>
					<td><fmt:formatNumber value="${pricing.initialPerHourPrice * pricing.initialNumberOfHours}" currencySymbol="$" type="currency"/></td>
				</tr>

				<tr>
					<td class="details"><fmt:formatNumber value="${pricing.additionalPerHourPrice}" currencySymbol="$" type="currency"/> per additional hour
						<small>(up to ${maxAdditionalHoursMinutes.hours}h<c:if test="${maxAdditionalHoursMinutes.minutes > 0}"> ${maxAdditionalHoursMinutes.minutes}m</c:if>)</small>
					</td>
					<td><fmt:formatNumber value="${pricing.additionalPerHourPrice * pricing.maxBlendedNumberOfHours}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:when>
		</c:choose>

		<c:choose>
			<c:when test="${not empty work.pricing.additionalExpenses && work.pricing.additionalExpenses > 0}">
				<c:set var="additionalExpenses" value="${work.pricing.additionalExpenses}"/>
			</c:when>
			<c:when test="${not empty work.payment.additionalExpenses && work.payment.additionalExpenses > 0}">
				<c:set var="additionalExpenses" value="${work.payment.additionalExpenses}"/>
			</c:when>
			<c:when test="${not empty work.activeResource.additionalExpenses && work.activeResource.additionalExpenses > 0}">
				<c:set var="additionalExpenses" value="${work.activeResource.additionalExpenses}"/>
			</c:when>
		</c:choose>

		<c:if test="${additionalExpenses > 0}">
			<tr>
				<td class="details">Approved Expense Reimbursements</td>
				<td><fmt:formatNumber value="${additionalExpenses}" currencySymbol="$" type="currency"/></td>
			</tr>
		</c:if>

		<c:if test="${displayOverrideExpenses}">
			<tr>
				<td class="details">Expenses Requested by Worker</td>
				<td class="">+ <fmt:formatNumber value="${work.activeResource.additionalExpenses}" currencySymbol="$" type="currency"/></td>
			</tr>
		</c:if>

		<c:choose>
			<c:when test="${not empty work.pricing.bonus && work.pricing.bonus > 0}">
				<c:set var="bonus" value="${work.pricing.bonus}"/>
			</c:when>
			<c:when test="${not empty work.payment.bonus && work.payment.bonus > 0}">
				<c:set var="bonus" value="${work.payment.bonus}"/>
			</c:when>
			<c:when test="${not empty work.activeResource.bonus && work.activeResource.bonus > 0}">
				<c:set var="bonus" value="${work.activeResource.bonus}"/>
			</c:when>
		</c:choose>

		<c:if test="${bonus > 0}">
			<tr>
				<td class="details">Bonus Amount</td>
				<td>+ <fmt:formatNumber value="${bonus}" currencySymbol="$" type="currency"/></td>
			</tr>
		</c:if>

		<c:if test="${isAdminOrInternal}">
			<c:choose>
				<c:when test="${work.status.code == workStatusTypes['COMPLETE']}">
					<c:choose>
						<c:when test="${pricing.id == pricingStrategyTypes['PER_HOUR'] || pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}">
							<c:set var="hoursMinutes" value="${wmfmt:getHoursAndMinutes(work.activeResource.hoursWorked)}"/>
							<tr>
								<td class="details">Hours Worked</td>
								<td><fmt:formatNumber value="${hoursMinutes.hours}"/><c:if test="${hoursMinutes.minutes > 0}"> hr <fmt:formatNumber value="${hoursMinutes.minutes}"/> min</c:if></td>
							</tr>
						</c:when>
						<c:when test="${pricing.id == pricingStrategyTypes['PER_UNIT']}">
							<tr>
								<td class="details">Units Processed</td>
								<td><fmt:formatNumber value="${work.activeResource.unitsProcessed}"/></td>
							</tr>
						</c:when>
					</c:choose>

					<tr class="subtotal">
						<td>Requested Payment</td>
						<td><fmt:formatNumber value="${work.payment.actualSpendLimit}" currencySymbol="$" type="currency"/></td>
					</tr>
				</c:when>
				<c:otherwise>
					<tr class="subtotal">
						<td>Worker Max Earnings</td>
						<td><fmt:formatNumber value="${pricing.maxSpendLimit}" currencySymbol="$" type="currency"/></td>
					</tr>
				</c:otherwise>
			</c:choose>

			<c:if test="${hasTransactionalPricing}">
				<tr>
					<td class="normal">Transaction Fee</td>
					<td>+ <fmt:formatNumber value="${work.payment.buyerFee}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:if>
		</c:if>

		<tr class="total">
			<c:choose>
				<c:when test="${isAdminOrInternal}">
					<c:choose>
						<%-- need to show the "potential" assignment value unless hours/units worked has been entered --%>
						<c:when test="${work.status.code == workStatusTypes['COMPLETE']}">
							<td>Total Requested</td>
							<td>
								<strong><fmt:formatNumber value="${work.payment.actualSpendLimit + work.payment.buyerFee}" currencySymbol="$" type="currency"/></strong>
							</td>
						</c:when>
						<c:otherwise>
							<td>Budget</td>
							<td><strong>
								<c:choose>
									<c:when test="${isAdminOrInternal}">
										<fmt:formatNumber value="${work.payment.totalCost}" currencySymbol="$" type="currency"/>
									</c:when>
									<c:otherwise>
										<fmt:formatNumber value="${pricing.maxSpendLimit}" currencySymbol="$" type="currency"/>
									</c:otherwise>
								</c:choose>
							</strong></td>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<%-- need to show the "potential" assignment value unless hours/units worked has been entered --%>
						<c:when test="${work.status.code == workStatusTypes['COMPLETE']}">
							<td>Total Requested</td>
							<td>
								<fmt:formatNumber value="${work.payment.actualSpendLimit}" currencySymbol="$" type="currency"/>
							</td>
						</c:when>
						<c:otherwise>
							<td>Budget</td>
							<td><strong>
								<fmt:formatNumber value="${pricing.maxSpendLimit}" currencySymbol="$" type="currency"/></strong></td>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</tr>
	</tbody>
</table>
