<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="isReadOnly" value="${param.readOnly == 'true' && not empty negotiation}"/>

<c:set var="bonusFeeAmount" value="${isAdminOrInternal ? (negotiation.pricing.bonus * (1 + workFee / 100.00)) - negotiation.pricing.bonus : 0.00}"/>
<c:if test="${bonusFeeAmount > maxWorkFee}">
	<c:set var="bonusFeeAmount" value="${maxWorkFee}"/>
</c:if>
<c:set var="defaultBonus" value="${negotiation.pricing.bonus + bonusFeeAmount}"/>

<c:choose>
	<c:when test="${isAdminOrInternal}">
		<table class="budget-summary">
			<thead>
				<tr>
					<th>Bonus Amount</th>
					<th>Transaction Fee</th>
					<th>Total Bonus Cost</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<c:choose>
							<c:when test="${isReadOnly}">
								<fmt:formatNumber value="${negotiation.pricing.bonus}" currencySymbol="$" type="currency"/>
							</c:when>
							<c:otherwise>
								<div>
									<input type="text" name="bonus" class="input-mini" style="width: 8em;"/>
								</div>
							</c:otherwise>
						</c:choose>
					</td>
					<td id="transaction-fee">
						<c:if test="${isReadOnly}">
							<fmt:formatNumber value="${bonusFeeAmount}" currencySymbol="$" type="currency"/>
						</c:if>
					</td>
					<td id="total-bonus">
						<c:if test="${isReadOnly}">
							<fmt:formatNumber value="${defaultBonus}" currencySymbol="$" type="currency"/>
						</c:if>
					</td>
				</tr>
			</tbody>
		</table>

		<c:if test="${!isReadOnly}">
			<tr>
				<td>Reason for increase</td>
				<td colspan="2">
					<span class="required"><textarea class="span5" rows="2" cols="30" name="note"></textarea></span>
				</td>
			</tr>
		</c:if>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${isReadOnly}">
			</c:when>
			<c:otherwise>
				<fieldset>
					<div class="control-group">
						<label class="required control-label" for='bonus'>Bonus Requested</label>

						<div class="controls">
							<input type="text" name="bonus" id="bonus" class="input-mini"  style="width: 8em;"/>
						</div>
					</div>
					<div class="control-group">
						<label class="required control-label" for='negotiation_note'>Reason for bonus</label>

						<div class="controls">
							<textarea class="input-block-level" rows="3" name="note" id="negotiation_note"></textarea>
						</div>
					</div>
				</fieldset>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
