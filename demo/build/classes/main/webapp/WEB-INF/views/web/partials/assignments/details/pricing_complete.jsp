<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<div id="pricing_complete">
<c:choose>
	<c:when test="${work.pricing.id == pricingStrategyTypes['INTERNAL']}">
	</c:when>
	<c:otherwise>
		<p><strong>Pricing Details</strong></p>
		<table>
			<tbody>
			<c:if test="${work.status.code == 'active' and work.pricing.overridePrice > 0}">
				<tr>
					<td><small class="meta">The work was previously approved for</small></td>
					<td><small class="meta"><fmt:formatNumber value="${work.pricing.overridePrice}" currencySymbol="$" type="currency"/></small></td>
				</tr>
			</c:if>
			<c:choose>
				<c:when test="${work.pricing.id == pricingStrategyTypes['FLAT']}">
					<tr>
						<td class="Details">Flat Fee</td>
						<td>
							<fmt:formatNumber value="${work.pricing.flatPrice}" currencySymbol="$" type="currency"/>
						</td>
					</tr>
				</c:when>

				<c:when test="${work.pricing.id == pricingStrategyTypes['PER_HOUR']}">
					<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxNumberOfHours)}"/>
					<tr>
						<td class="Details">
							<fmt:formatNumber value="${work.pricing.perHourPrice}" currencySymbol="$" type="currency"/> per hour
							<small>(up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</small>
						</td>
						<td>
							<fmt:formatNumber value="${work.pricing.perHourPrice * work.pricing.maxNumberOfHours}" currencySymbol="$" type="currency"/>
						</td>
					</tr>
				</c:when>

				<c:when test="${work.pricing.id == pricingStrategyTypes['PER_UNIT']}">
					<tr>
						<td class="Details">
							<fmt:formatNumber value="${work.pricing.perUnitPrice}" currencySymbol="$" type="currency" maxFractionDigits="3"/> per unit
							<small>(up to <c:out value="${work.pricing.maxNumberOfUnits}"/> units)</small>
						</td>
						<td>
							<fmt:formatNumber value="${work.pricing.maxUnitPrice}" currencySymbol="$" type="currency"/>
						</td>
					</tr>
				</c:when>

				<c:when test="${work.pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}">
					<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.initialNumberOfHours)}" scope="page"/>
					<c:set var="maxAdditionalHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxBlendedNumberOfHours)}"/>
					<tr>
						<td class="Details">
							<fmt:formatNumber value="${work.pricing.initialPerHourPrice}" currencySymbol="$" type="currency"/> per hour
							<small>(up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</small>
						</td>
						<td>
							<fmt:formatNumber value="${work.pricing.initialPerHourPrice * work.pricing.initialNumberOfHours}" currencySymbol="$" type="currency"/>
						</td>
					</tr>
					<tr>
						<td class="Details">
							<fmt:formatNumber value="${work.pricing.additionalPerHourPrice}" currencySymbol="$" type="currency"/> per additional hour
							<small>(up to ${maxAdditionalHoursMinutes.hours}h<c:if test="${maxAdditionalHoursMinutes.minutes > 0}"> ${maxAdditionalHoursMinutes.minutes}m</c:if>)</small>
						</td>
						<td>
							<fmt:formatNumber value="${work.pricing.additionalPerHourPrice * work.pricing.maxBlendedNumberOfHours}" currencySymbol="$" type="currency"/>
						</td>
					</tr>
				</c:when>
			</c:choose>

			<c:choose>
				<c:when test="${work.pricing.id == pricingStrategyTypes['PER_HOUR'] || work.pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}">
					<%-- display blank instead of zero if nothing is set --%>
					<c:set var="workedhoursMinutes" value="${wmfmt:getHoursAndMinutes(work.activeResource.hoursWorked)}"/>
					<c:set var="workedHoursVal" value="${not empty workedhoursMinutes && workedhoursMinutes.hours > 0 ? workedhoursMinutes.hours : ''}"/>
					<c:set var="workedMinutesVal" value="${not empty workedhoursMinutes && workedhoursMinutes.minutes > 0 ? workedhoursMinutes.minutes : ''}"/>

					<tr>
						<td class="normal"><label class="required">Hours Worked</label></td>
						<td class="tar">
							<div class="input-append">
								<input type="text" name='hours' value="${workedHoursVal}" class='span1' alt="integer"/>
								<span class="add-on">hr</span>
							</div>
							<div class="input-append">
								<input type="text" name='minutes' value="${workedMinutesVal}" class='span1' alt="integer"/>
								<span class="add-on">min</span>
							</div>
						</td>
					</tr>
				</c:when>
				<c:when test="${work.pricing.id == pricingStrategyTypes['PER_UNIT']}">
					<tr>
						<td class="normal"><span class="required">Units</span></td>
						<td>
							<input
								name='units'
								value="${work.activeResource.unitsProcessed >  0 ? work.activeResource.unitsProcessed : ''}"
								class='span1 tar'/>
						</td>
					</tr>
				</c:when>
			</c:choose>

			<c:if test="${not empty work.pricing.additionalExpenses && ((not empty work.activeResource.expenseNegotiation && work.activeResource.expenseNegotiation.approvalStatus.code == 'approved') || work.approvedAdditionalExpenses)}">
				<tr>
					<td class="Details">
						Additional Expenses
						<span class="label important dn" id="additional_expenses_maxvalue_error_txt"></span>
					</td>
					<td>
						<div class="input-prepend">
							<span class="add-on">$</span>
							<input name="additional_expenses" type="text" value="<fmt:formatNumber value="${work.pricing.additionalExpenses}" minFractionDigits="0" maxFractionDigits="2" />" class="span2 tar"/>
						</div>
						<input type="hidden" name="additional_expenses_maxvalue" value="${work.pricing.additionalExpenses}" id='additional_expenses_maxvalue'/>
					</td>
				</tr>
			</c:if>

			<c:if test="${not empty work.pricing.bonus && ((not empty work.activeResource.bonus && work.activeResource.bonusNegotiation.approvalStatus.code == 'approved') || work.approvedBonus)}">
				<tr>
					<td class="Details">
						Bonus
						<span class="label important dn" id="bonus_maxvalue_error_txt"></span>
					</td>
					<td>
						$<fmt:formatNumber value="${work.pricing.bonus}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<input type="hidden" name="bonus" value="${work.pricing.bonus}" id='bonus_value'/>
				</tr>
			</c:if>

			<tr class="total">
				<td>Final Assignment Value
					<small>(<a class="edit_pricing_earnings_outlet">Edit</a>)</small>
				</td>
				<td>
					<strong>
					<span class="pricing_earnings_outlet">
						<div class="read-only-price">
							<c:choose>
								<c:when test="${work.pricing.id == pricingStrategyTypes['FLAT']}">
									<fmt:formatNumber value="${work.payment.maxSpendLimit}" currencySymbol="$" type="currency"/>
								</c:when>
								<c:otherwise>
									<fmt:formatNumber value="0" currencySymbol="$" type="currency"/>
								</c:otherwise>
							</c:choose>
						</div>
						<div class="input-prepend override-price dn">
							<span class="add-on">$</span>
							<input type="text" name="override_price" value="" class="span2 tar" />
						</div>
					</span>
					</strong>
				</td>
			</tr>
			</tbody>
		</table>

		<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing_taxes.jsp"/>

	</c:otherwise>
</c:choose>
</div>
