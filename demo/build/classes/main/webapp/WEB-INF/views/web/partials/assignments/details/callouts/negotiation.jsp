<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:if test="${negotiation.approvalStatus.code != 'removed'}">
	<div class="well-b2">
		<div class="well-content">
			<h4>
				Counteroffer Request <span class="label label-success">Action Required</span>
			</h4>
			<p>
				<strong>
					<a href="/profile/${negotiation.requestedBy.userNumber}" class="profile_link">
						<c:out value="${negotiation.requestedBy.name.firstName}" /> <c:out value="${negotiation.requestedBy.name.lastName}" />
					</a>
				</strong>
					<c:if test="${ ! work.offsiteLocation}">
						<small>(<fmt:formatNumber value="${negotiation.distanceToAssignment}" minFractionDigits="2"/> mi)</small>
					</c:if>
				provided a counteroffer
			</p>


			<c:if test="${negotiation.approvalStatus.code != 'declined'}">

				<!-- schedule counter -->
				<c:if test="${negotiation.isScheduleNegotiation}">
					<h6>New Schedule Request:</h6>
					<c:choose>
						<c:when test="${negotiation.schedule.range}">
							${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a z', negotiation.schedule.from, work.timeZone)}
							to
							${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a z', negotiation.schedule.through, work.timeZone)}
						</c:when>
						<c:otherwise>
							${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a z', negotiation.schedule.from, work.timeZone)}
						</c:otherwise>
					</c:choose>
				</c:if>

				<!-- price counter -->
				<c:if test="${negotiation.isPriceNegotiation}">
					<h6>New Assignment Value Request:</h6>
					<h3>Pricing Details</h3>
					<table>
						<tbody>
							<c:choose>
								<c:when test="${negotiation.pricing.id == pricingStrategyTypes['FLAT']}">
									<tr>
										<td class="details">Flat price</td>
										<td><fmt:formatNumber value="${negotiation.pricing.flatPrice}" minFractionDigits="2" currencySymbol="$" type="currency"/></td>
									</tr>
								</c:when>

								<c:when test="${negotiation.pricing.id == pricingStrategyTypes['PER_HOUR']}">
									<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(negotiation.pricing.maxNumberOfHours)}"/>
									<tr>
										<td class="details">
											<fmt:formatNumber value="${negotiation.pricing.perHourPrice}" minFractionDigits="2"/>per hour
											<small>(up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</small>
										</td>
										<td><fmt:formatNumber value="${negotiation.pricing.perHourPrice * negotiation.pricing.maxNumberOfHours}" minFractionDigits="2"/></td>
									</tr>
								</c:when>

								<c:when test="${negotiation.pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}">
									<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(negotiation.pricing.initialNumberOfHours)}" scope="page"/>
									<c:set var="maxAdditionalHoursMinutes" value="${wmfmt:getHoursAndMinutes(negotiation.pricing.maxBlendedNumberOfHours)}"/>
									<tr>
										<td class="details">
											<fmt:formatNumber value="${negotiation.pricing.initialPerHourPrice}" minFractionDigits="2"/> per hour
											<small>(up to ${maxHoursMinutes.hours}h<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes}m</c:if>)</small>
										</td>
										<td><fmt:formatNumber value="${negotiation.pricing.initialPerHourPrice * negotiation.pricing.initialNumberOfHours}" minFractionDigits="2"/></td>
									</tr>
									<tr>
										<td class="details"><fmt:formatNumber value="${negotiation.pricing.additionalPerHourPrice}" minFractionDigits="2" currencySymbol="$" type="currency"/> per additional hour
											<small>(up to ${maxAdditionalHoursMinutes.hours}h<c:if test="${maxAdditionalHoursMinutes.minutes > 0}"> ${maxAddtionalHoursMinutes.minutes}m</c:if>)</small>
										</td>
										<td><fmt:formatNumber value="${negotiation.pricing.additionalPerHourPrice * negotiation.pricing.maxBlendedNumberOfHours}" minFractionDigits="2"/></td>
									</tr>
								</c:when>

								<c:when test="${negotiation.pricing.id == pricingStrategyTypes['PER_UNIT']}">
									<tr>
										<td class="details">
											<fmt:formatNumber value="${negotiation.pricing.perUnitPrice}" minFractionDigits="2" currencySymbol="$" type="currency"/> per unit
											<small>(up to <c:out value="${negotiation.pricing.maxNumberOfUnits}"/> units)</small>
										</td>
										<td><fmt:formatNumber value="${negotiation.pricing.perUnitPrice * negotiation.pricing.maxNumberOfUnits}" minFractionDigits="2" currencySymbol="$" type="currency"/></td>
									</tr>
								</c:when>
							</c:choose>

							<c:if test="${not empty negotiation.pricing.additionalExpenses}">
								<tr>
									<td class="details">Additional expenses</td>
									<td>+ <fmt:formatNumber value="${negotiation.pricing.additionalExpenses}" currencySymbol="$" type="currency"/></td>
								</tr>
							</c:if>

							<tr class="subtotal">
								<td>Resource Max Earnings</td>
								<td><fmt:formatNumber value="${negotiation.pricing.maxSpendLimit}" currencySymbol="$" type="currency"/></td>
							</tr>

							<c:if test="${hasTransactionalPricing}">
								<tr>
									<td class="details">Transaction fee</td>
									<td>+ <fmt:formatNumber value="${negotiation.payment.buyerFee}" currencySymbol="$" type="currency"/></td>
								</tr>
							</c:if>

							<tr class="total">
								<td>New Assignment budget</td>
								<td><strong><fmt:formatNumber value="${negotiation.payment.totalCost}" currencySymbol="$" type="currency"/></strong></td>
							</tr>
						</tbody>
					</table>
				</c:if>

				<c:if test="${not empty negotiation.note}">
					<h6>Message</h6>
					<blockquote><em><c:out value="${negotiation.note.text}"/></em></blockquote>
				</c:if>

			</c:if>


			<!-- counter expire -->
			<c:if test="${negotiation.expiresOn != 0}">
				<p>
					<c:choose>
						<c:when test="${negotiation.requestedBy.id == currentUser.id}">
							Your offer
								<c:out value="${negotiation.isExpired ? 'expired on' : 'is available until'}"/>
								<c:out value="${wmfmt:formatMillisWithTimeZone('MMM d, YYYY h:mma z', negotiation.expiresOn, work.timeZone)}"/>
						</c:when>
						<c:otherwise>
							<c:out value="${negotiation.isExpired ? 'Offer expired on' : 'An answer is requested by'}"/>
							<c:out value="${wmfmt:formatMillisWithTimeZone('MMM d, YYYY h:mma z', negotiation.expiresOn, work.timeZone)}"/>
						</c:otherwise>
					</c:choose>
				</p>
			</c:if>

			<c:if test="${negotiation.approvalStatus.code == 'pending' && !negotiation.isExpired}">
				<c:if test="${is_admin}">
					<sec:authorize access="!principal.counterOfferCustomAuth">
						<c:set var="disable" value="disabled" />
					</sec:authorize>
					<form action="/assignments/accept_negotiation/${work.workNumber}" class="wm-action-container">
						<input type="hidden" name="id" value="${negotiation.encryptedId}"/>
						<a rel="prompt_decline_negotiation" data-negotiation-id="${negotiation.encryptedId}" class="decline-negotiation button" ${disable}>Decline</a>
						<button type="submit" class="accept-negotiation button" ${disable}>Approve Request</button>
					</form>
					<c:if test="${disable == 'disabled'}">
						<div class="alert alert-error">You are not authorized to approve or decline this request. Please contact your manager or account administrator to approve or decline this counteroffer.</div>
					</c:if>
				</c:if>

			</c:if>
		</c:if>
	</div>
</div>
