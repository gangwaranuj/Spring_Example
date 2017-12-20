<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- TODO: set these as model attributes --%>
<c:set var="isSpend" value="${work.configuration.useMaxSpendPricingDisplayModeFlag}"/>

<c:set var="isReadOnly" value="${param.readOnly == 'true' && not empty negotiation}"/>

<table class="budget-summary">
	<thead>
		<tr <c:if test="${work.pricing.id == pricingStrategyTypes['FLAT']}">class="double"</c:if>>
			<th></th>
			<th>Current Budget</th>
			<th>${isAdminOrInternal ? "New" : "Requested"} Budget</th>
		</tr>
	</thead>
	<tbody>

	<c:choose>
		<c:when test="${work.pricing.id == pricingStrategyTypes['FLAT']}">
			<tr>
				<td>${isAdminOrInternal ? "Worker Earns" : "Rate"}</td>
				<td><fmt:formatNumber value="${work.pricing.flatPrice}" currencySymbol="$" type="currency"/></td>
				<td id="resource-earns" class="calculated-value">
					<c:choose>
						<c:when test="${isReadOnly}">
							<c:set var="basePrice" value="${negotiation.pricing.maxSpendLimit - work.pricing.additionalExpenses - work.pricing.bonus}"/>
							<fmt:formatNumber value="${basePrice}" currencySymbol="$" type="currency"/>
						</c:when>
						<c:otherwise>
							<input name="flat_price" class="input-mini" type="text"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:when>

		<c:when test="${work.pricing.id == pricingStrategyTypes['PER_HOUR']}">
			<tr>
				<td>Rate</td>
				<td><fmt:formatNumber value="${work.pricing.perHourPrice}" currencySymbol="$" type="currency"/></td>
				<td><fmt:formatNumber value="${work.pricing.perHourPrice}" currencySymbol="$" type="currency"/></td>
			</tr>
			<tr>
				<td>Hours</td>
				<td>
					<fmt:formatNumber value="${work.pricing.maxNumberOfHours}" type="number" maxFractionDigits="1"/>
				</td>
				<td class="calculated-value">
					<c:choose>
						<c:when test="${isReadOnly}">
							<fmt:formatNumber value="${negotiation.pricing.maxNumberOfHours}" type="number" maxFractionDigits="1"/>
						</c:when>
						<c:otherwise>
							<input name="max_number_of_hours" type="text" class="input-mini"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:when>

		<c:when test="${work.pricing.id == pricingStrategyTypes['PER_UNIT']}">
			<tr>
				<td>Rate</td>
				<td><fmt:formatNumber value="${work.pricing.perUnitPrice}" currencySymbol="$" type="currency" maxFractionDigits="3"/></td>
				<td><fmt:formatNumber value="${work.pricing.perUnitPrice}" currencySymbol="$" type="currency" maxFractionDigits="3"/></td>
			</tr>
			<tr>
				<td>Units</td>
				<td><fmt:formatNumber value="${work.pricing.maxNumberOfUnits}" type="number" maxFractionDigits="0"/>
				</td>
				<td><c:choose>
					<c:when test="${isReadOnly}">
						<fmt:formatNumber value="${negotiation.pricing.maxNumberOfUnits}" type="number" maxFractionDigits="0"/>
					</c:when>
					<c:otherwise>
						<input name="max_number_of_units" class="input-mini" type="text" />
					</c:otherwise>
				</c:choose>
				</td>
			</tr>
		</c:when>


		<c:when test="${work.pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}">
			<tr>
				<td>Initial Rate</td>
				<td><fmt:formatNumber value="${work.pricing.initialPerHourPrice}" currencySymbol="$" type="currency"/></td>
				<td><fmt:formatNumber value="${work.pricing.initialPerHourPrice}" currencySymbol="$" type="currency"/></td>
			</tr>
			<tr class="double">
				<td>Initial Hours</td>
				<td>
					<fmt:formatNumber value="${work.pricing.initialNumberOfHours}" type="number" maxFractionDigits="0"/>
				</td>
				<td>
					<fmt:formatNumber value="${work.pricing.initialNumberOfHours}" type="number" maxFractionDigits="0"/>
				</td>
			</tr>
			<tr>
				<td>Secondary Rate</td>
				<td><fmt:formatNumber value="${work.pricing.additionalPerHourPrice}" currencySymbol="$" type="currency"/></td>
				<td><fmt:formatNumber value="${work.pricing.additionalPerHourPrice}" currencySymbol="$" type="currency"/></td>
			</tr>
			<tr class="double">
				<td>Secondary Hours</td>
				<td>
					<fmt:formatNumber value="${work.pricing.maxBlendedNumberOfHours}" type="number" maxFractionDigits="0"/>
				</td>
				<td class="calculated-value">
					<c:choose>
						<c:when test="${isReadOnly}">
							<fmt:formatNumber value="${negotiation.pricing.maxBlendedNumberOfHours}" type="number" maxFractionDigits="0"/>
						</c:when>
						<c:otherwise>
							<input name="max_blended_number_of_hours" class="input-mini" type="text"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:when>
	</c:choose>

	<%--- BONUS --------------------------------------------------%>

	<c:if test="${work.pricing.bonus > 0}">
		<tr>
			<td>Bonus</td>
			<td><fmt:formatNumber value="${work.pricing.bonus}" type="number" maxFractionDigits="0"/></td>
			<td><fmt:formatNumber value="${work.pricing.bonus}" type="number" maxFractionDigits="0"/></td>
		</tr>
	</c:if>


	<%--- EXPENSES -----------------------------------------------%>


	<c:set var="additionalExpenses" value="0"/>
	<c:choose>
		<c:when	test="${not empty work.activeResource.additionalExpenses && work.activeResource.additionalExpenses > 0}">
			<c:set var="additionalExpenses" value="${work.activeResource.additionalExpenses}"/>

			<%-- additional expenses could be overrided here... if so, subtract them (make them negative)--%>
			<c:if test="${work.status.code == workStatusTypes['COMPLETE']
			|| work.status.code == workStatusTypes['PAID']
			|| work.status.code == workStatusTypes['PAYMENT_PENDING']}">
				<c:set var="additionalExpenses" value="${work.activeResource.additionalExpenses - work.pricing.additionalExpenses}"/>
			</c:if>
		</c:when>
		<c:when test="${not empty work.pricing.additionalExpenses && work.pricing.additionalExpenses > 0}">
			<c:set var="additionalExpenses" value="${work.pricing.additionalExpenses}"/>
		</c:when>
	</c:choose>

	<c:if test="${additionalExpenses > 0.0 }">
		<tr>
			<td>Approved Expense Reimbursements</td>
			<td><fmt:formatNumber value="${additionalExpenses}" currencySymbol="$" type="currency"/></td>
			<td><fmt:formatNumber value="${additionalExpenses}" currencySymbol="$" type="currency"/></td>
		</tr>
	</c:if>

	<%--- TRANSACTION FEE -----------------------------------------------%>

	<c:if test="${isReadOnly}"> <%--- in this case don't use the javascript to calculate this --%>
		<c:set var="newFee" value="${isAdminOrInternal ? (negotiation.pricing.maxSpendLimit * workFee) / 100.00 : 0.00}"/>
		<c:if test="${newFee > maxWorkFee}">
			<c:set var="newFee" value="${maxWorkFee}"/>
		</c:if>
		<c:set var="newBudget" value="${negotiation.pricing.maxSpendLimit + newFee}"/>
	</c:if>
	<c:set var="defaultFee" value="${(work.pricing.maxSpendLimit * workFee) / 100.00}"/>

	<c:if test="${isAdminOrInternal}">
		<tr>
			<td>Transaction Fee</td>
			<td><fmt:formatNumber value="${defaultFee}" currencySymbol="$" type="currency"/></td>
			<td id="transaction-fee" class="calculated-value">
				<c:if test="${isReadOnly}">
					<fmt:formatNumber value="${newFee}" currencySymbol="$" type="currency"/>
				</c:if>
			</td>
		</tr>
	</c:if>

	<%--- TOTAL BUDGET -----------------------------------------------%>

	<c:set var="defaultBudget" value="${(isAdminOrInternal ?
			work.pricing.maxSpendLimit * (1 + workFee / 100.00) :
			work.pricing.maxSpendLimit)}"/>

	<tr>
		<td>Assignment Budget</td>
		<td><fmt:formatNumber value="${defaultBudget}" currencySymbol="$" type="currency"/></td>
		<td id="assignment-budget" class="calculated-total">
			<c:if test="${isReadOnly}"><fmt:formatNumber value="${newBudget}" currencySymbol="$" type="currency"/></c:if>
		</td>
	</tr>

	<c:if test="${!isReadOnly}">
		<tr>
			<td><span class="required">Reason for increase</span></td>
			<td colspan="2">
				<textarea class="input-block-level" rows="2" name="note"></textarea>
			</td>
		</tr>
	</c:if>
	</tbody>
</table>
