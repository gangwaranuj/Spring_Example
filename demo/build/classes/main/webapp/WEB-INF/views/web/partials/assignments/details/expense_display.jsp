<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="isReadOnly" value="${param.readOnly == 'true' && not empty negotiation}"/>

<c:set var="expenseFeeAmount" value="${isAdminOrInternal ? (negotiation.pricing.additionalExpenses * (1 + workFee / 100.00)) - negotiation.pricing.additionalExpenses : 0.00}"/>
<c:if test="${expenseFeeAmount > maxWorkFee}">
	<c:set var="expenseFeeAmount" value="${maxWorkFee}"/>
</c:if>
<c:set var="defaultExpense" value="${negotiation.pricing.additionalExpenses + expenseFeeAmount}"/>

<div class="form-horizontal">
	<c:choose>
		<c:when test="${isAdminOrInternal}">
			<table class="budget-summary">
				<tbody>
					<tr>
						<td>Reimbursement Requested</td>
						<td>
							<c:choose>
								<c:when test="${isReadOnly}">
									<fmt:formatNumber value="${negotiation.pricing.additionalExpenses}" currencySymbol="$" type="currency"/>
								</c:when>
								<c:otherwise>
									<input type="text" class="input-mini" name="additional_expenses" style="width: 8em;" />
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr class="double">
						<td>Transaction Fee</td>
						<td id="transaction-fee">
							<c:if test="${isReadOnly}">
								<fmt:formatNumber value="${expenseFeeAmount}" currencySymbol="$" type="currency"/>
							</c:if>
						</td>
					</tr>
					<tr class="total">
						<td>Total Reimbursement</td>
						<td id="total-reimbursement">
							<c:if test="${isReadOnly}">
								<fmt:formatNumber value="${defaultExpense}" currencySymbol="$" type="currency"/>
							</c:if>
						</td>
					</tr>
				</tbody>
			</table>

			<c:if test="${!isReadOnly}">
				<fieldset>
					<div class="control-group">
						<label class="control-label required" for='negotiation_note'>Reason for expense reimbursement</label>

						<div class="controls">
							<textarea class="input-block-level" rows="3" name="note" id="negotiation_note"></textarea>
						</div>
					</div>
				</fieldset>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${!isReadOnly}">
				<fieldset>
					<div class="control-group">
						<label class="control-label required" for='additionalExpenses'>Amount of Expenses</label>
						<div class="controls">
								<input type="text" name="additional_expenses" id="additionalExpenses" style="width: 6em;"/>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label required" for='negotiation_note'>Reason for expense reimbursement</label>

						<div class="controls">
							<textarea class="input-block-level" rows="3" name="note" id="negotiation_note"></textarea>
						</div>
					</div>
				</fieldset>
			</c:if>
		</c:otherwise>
	</c:choose>
</div>
