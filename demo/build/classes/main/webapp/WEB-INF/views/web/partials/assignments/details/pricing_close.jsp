<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${work.pricing.id != pricingStrategyTypes['INTERNAL']}">
	<c:if test="${work.pricing.offlinePayment}">
		<p><strong>Scheduled to be paid Off-Platform</strong></p>
	</c:if>
	<p><strong>Pricing Details</strong></p>
	<table>
		<tbody>
		<c:choose>
			<c:when test="${work.pricing.id == pricingStrategyTypes['FLAT']}">
				<tr>
					<td class="details">Flat Fee</td>
					<td><fmt:formatNumber value="${work.pricing.flatPrice}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:when>

			<c:when test="${work.pricing.id == pricingStrategyTypes['PER_HOUR']}">
				<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxNumberOfHours)}"/>
				<tr>
					<td class="details">
						<fmt:formatNumber value="${work.pricing.perHourPrice}" currencySymbol="$" type="currency"/> per hour
						<small>(up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</small>
					</td>
					<td><fmt:formatNumber value="${work.pricing.perHourPrice * work.pricing.maxNumberOfHours}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:when>

			<c:when test="${work.pricing.id == pricingStrategyTypes['PER_UNIT']}">
				<tr>
					<td class="details"><fmt:formatNumber value="${work.pricing.perUnitPrice}" currencySymbol="$" type="currency" maxFractionDigits="3"/> per unit
						<small>(up to <c:out value="${work.pricing.maxNumberOfUnits}"/> units)</small>
					</td>
					<td><fmt:formatNumber value="${work.pricing.maxUnitPrice}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:when>

			<c:when test="${work.pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}">
				<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.initialNumberOfHours)}" scope="page"/>
				<c:set var="maxAdditionalHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxBlendedNumberOfHours)}"/>
				<tr>
					<td class="details"><fmt:formatNumber value="${work.pricing.initialPerHourPrice}" currencySymbol="$" type="currency"/> per hour
						<small>(up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</small>
					</td>
					<td><fmt:formatNumber value="${work.pricing.initialPerHourPrice * work.pricing.initialNumberOfHours}" currencySymbol="$" type="currency"/></td>
				</tr>

				<tr>
					<td class="details">
						<fmt:formatNumber value="${work.pricing.additionalPerHourPrice}" currencySymbol="$" type="currency"/> per additional hour
						<small>(up to ${maxAdditionalHoursMinutes.hours}h<c:if test="${maxAdditionalHoursMinutes.minutes > 0}"> ${maxAdditionalHoursMinutes.minutes}m</c:if>)</small>
					</td>
					<td><fmt:formatNumber value="${work.pricing.additionalPerHourPrice *work.pricing.maxBlendedNumberOfHours}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:when>
		</c:choose>

		<c:choose>
			<c:when test="${work.pricing.id == pricingStrategyTypes['PER_HOUR'] || work.pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}">
				<c:set var="hoursMinutes" value="${wmfmt:getHoursAndMinutes(work.activeResource.hoursWorked)}"/>
				<tr>
					<td class="details">Hours Worked</td>
					<td><fmt:formatNumber value="${hoursMinutes.hours}"/><c:if test="${hoursMinutes.minutes > 0}"> hr <fmt:formatNumber value="${hoursMinutes.minutes}"/> min</c:if></td>
				</tr>
			</c:when>
			<c:when test="${work.pricing.id == pricingStrategyTypes['PER_UNIT']}">
				<tr>
					<td class="details">Units Processed</td>
					<td><fmt:formatNumber value="${work.activeResource.unitsProcessed}"/></td>
				</tr>
			</c:when>
		</c:choose>

		<c:set var="additionalExpenses" value="0"/>
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

		<c:set var="displayOverrideExpenses" value="${
			not empty work.activeResource.additionalExpenses
			&& not empty work.pricing.additionalExpenses
			&& work.activeResource.additionalExpenses < work.pricing.additionalExpenses
			&& work.activeResource.additionalExpenses > 0}"/>


		<c:if test="${additionalExpenses > 0 && !displayOverrideExpenses}">
			<tr>
				<td class="details">Expense Requested</td>
				<td><fmt:formatNumber value="${additionalExpenses}" currencySymbol="$" type="currency"/></td>
			</tr>
		</c:if>

		<c:if test="${displayOverrideExpenses}">
			<tr>
				<td class="details">
					Expenses Requested <small>(approved <fmt:formatNumber value="${additionalExpenses}" currencySymbol="$" type="currency"/>)</small>
				</td>
				<td>
					<fmt:formatNumber value="${work.activeResource.additionalExpenses}" currencySymbol="$" type="currency"/>
				</td>
			</tr>
		</c:if>

		<c:set var="bonus" value="0"/>
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
				<td><fmt:formatNumber value="${bonus}" currencySymbol="$" type="currency"/></td>
			</tr>
		</c:if>

		<c:choose>
			<c:when test="${is_admin || isInternal}">
				<tr class="subtotal">
					<td>Requested Payment</td>
					<td class="nowrap">
						<c:if test="${work.pricing.overridePrice > 0}">
							<span class="label warning">MANUALLY SET</span>
						</c:if>
						<fmt:formatNumber value="${work.payment.actualSpendLimit}" currencySymbol="$" type="currency"/>
					</td>
				</tr>
				<c:if test="${hasTransactionalPricing}">
					<tr>
						<td class="details">Transaction Fee</td>
						<td>+ <fmt:formatNumber value="${work.payment.buyerFee}" currencySymbol="$" type="currency"/></td>
					</tr>
				</c:if>
				<tr class="total">
					<td>Assignment Value</td>
					<td><strong><fmt:formatNumber value="${work.payment.totalCost}" currencySymbol="$" type="currency"/></strong></td>
				</tr>
				<tr>
					<td>
						<span>
							<c:if test="${workResponse.work.status.code == workStatusTypes['PAYMENT_PENDING'] or workResponse.work.status.code == workStatusTypes['INVOICED']}">
							<small>
								Payable in <c:out value="${wmfmt:daySpanFromMillis(work.payment.paymentDueOn)}"/> days
							</small>
							</c:if>
						</span>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr class="total">
					<td>
						Payment
						<c:choose>
							<c:when test="${workResponse.work.status.code == workStatusTypes['PAYMENT_PENDING'] or workResponse.work.status.code == workStatusTypes['INVOICED']}">
								<span class="label warning">PENDING PAYMENT</span>
							</c:when>
							<c:when test="${workResponse.work.status.code == workStatusTypes['COMPLETE']}">
								<span class="label warning">PENDING APPROVAL</span>
							</c:when>
						</c:choose>
						<c:if test="${workResponse.work.status.code == workStatusTypes['PAID']}">
							<small>
								(Paid on <c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, YYYY', work.payment.paidOn, work.timeZone)}"/> )
							</small>
						</c:if>
					</td>
					<td class="total">
						<fmt:formatNumber value="${work.payment.actualSpendLimit}" currencySymbol="$" type="currency"/><br/>
						<c:if test="${work.pricing.overridePrice > 0}">
							<small>(manually set)</small>
						</c:if>
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
		</tbody>
	</table>
</c:if>
