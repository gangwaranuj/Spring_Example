<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<div id="previous_subscriptions">
	<h5>Previous Subscriptions</h5>

	<c:if test="${fn:length(previous_subscriptions) > 0}">
		<c:forEach items="${previous_subscriptions}" var="subscription">
		<div class="previous_subscription">
			<a class="prev_toggle">
				<span class="toggler"></span> Subscription Details from ${wmfmt:formatCalendar('MM/dd/YYYY', subscription.effectiveDate)} to ${wmfmt:formatCalendar('MM/dd/YYYY', subscription.endDate)}
			</a>

			<table class="dn">
				<tbody>
					<tr>
						<td>Effective Date</td>
						<td>${wmfmt:formatCalendar('MM/dd/YYYY', subscription.effectiveDate)}</td>
					</tr>
					<tr>
						<td>Payment Period</td>
						<td><c:out value="${wmfmt:capitalize(subscription.subscriptionPeriod)}" /></td>
					</tr>
					<tr>
						<td>Number of Periods</td>
						<td><c:out value="${subscription.numberOfPeriods}" /></td>
					</tr>
					<tr>
						<td>Vendor of Record</td>
						<td>${wmfn:toYesNo(subscription.vendorOfRecord)}</td>
					</tr>
					<tr>
						<td>Pricing Ranges</td>
						<td>
							<c:set var="subscriptionTierNumber" value="0"/>
							<c:set var="totalTiers" value="${fn:length(subscription.subscriptionPaymentTiers)}"/>
							<table class="previous_subscription_tiers">
								<thead>
									<tr>
										<th>Tier</th>
										<th>Lower Bound</th>
										<th>Upper Bound</th>
										<th>Period Payment Amount</th>

										<c:if test="${subscription.vendorOfRecord}">
											<th>Vendor of Record Amount</th>
										</c:if>
									</tr>
								</thead>
								<tbody>

								<c:forEach items="${subscription.subscriptionPaymentTiers}" var="tier">
								<c:set var="subscriptionTierNumber" value="${subscriptionTierNumber + 1}"/>
									<tr>
										<td><c:out value="${subscriptionTierNumber}" /></td>
										<td><fmt:formatNumber value="${tier.minimum}" currencySymbol="$" type="currency"/></td>

										<c:choose>
											<c:when test="${subscriptionTierNumber < totalTiers}">
												<td><fmt:formatNumber value="${tier.maximum}" currencySymbol="$" type="currency"/></td>
											</c:when>
											<c:otherwise>
												<td>Infinity</td>
											</c:otherwise>
										</c:choose>


										<td><fmt:formatNumber value="${tier.paymentAmount}" currencySymbol="$" type="currency"/></td>

										<c:if test="${subscription.vendorOfRecord}">
											<td><fmt:formatNumber value="${tier.vendorOfRecordAmount}" currencySymbol="$" type="currency"/></td>
										</c:if>
									</tr>
								</c:forEach>

								</tbody>
							</table>
						</td>
					</tr>
					<tr>
						<td>Discount Options</td>
						<td>${wmfn:toYesNo(subscription.discountedPeriods > 0)}</td>
					</tr>
					<tr>
						<td>Set Up Fee</td>
						<td><fmt:formatNumber value="${subscription.setUpFee}" currencySymbol="$" type="currency"/></td>
					</tr>
					<tr>
						<td>Auto Renewal</td>
						<td><c:out value="${subscription.numberOfRenewals}"/></td>
					</tr>
					<c:if test="${not empty subscription.cancellationOption}">
					<tr>
						<td>Cancellation Option</td>
						<td><c:out value="${subscription.cancellationOption}"/></td>
					</tr>
					</c:if>

					<c:set var="hasAddons" value="${fn:length(subscription.activeSubscriptionAddOns) > 0}"/>
					<c:if test="${hasAddons}">
					<tr>
						<td>Add-Ons</td>
						<td>${wmfn:toYesNo(hasAddons)}</td>
					</tr>
					</c:if>

					<c:set var="hasAdditionalNotes" value="${fn:length(subscription.notes) > 0}"/>
					<c:if test="${hasAdditionalNotes}">
					<tr>
						<td>Additional Notes</td>
						<td>
							<table class="zebra-striped" class="previous_notes">
							<c:forEach items="${subscription.notes}" var="note">
								<tr>
									<td><c:out value="${note.content}"/></td>
								</tr>
							</c:forEach>
							</table>
						</td>
					</tr>
					</c:if>
				</tbody>
			</table>
		</div>
		</c:forEach>
	</c:if>

	<c:if test="${fn:length(previous_subscriptions) == 0}">
		No previous subscriptions.
	</c:if>
</div>
