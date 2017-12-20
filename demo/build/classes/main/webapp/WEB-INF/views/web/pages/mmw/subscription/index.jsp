<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Subscription Information" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="Subscription Information">

<div class="row_wide_sidebar_left">
	<div class="sidebar">
		<c:set var="selected_navigation_link" value="/mmw/subscription" scope="request"/>
		<jsp:include page="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
	</div>

	<div class="content">
		<div class="inner-container">
			<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

			<div class="page-header">
				<h3>Subscription Information</h3>
			</div>

			<c:if test="${empty effectiveDate }">
				No active subscription.
			</c:if>

			<c:if test="${not empty effectiveDate}">
				<div>
					<h5>General Details</h5>
					<table>
						<tr>
							<td>Effective Date</td>
							<td>${wmfmt:formatCalendar('MMM dd, YYYY', effectiveDate)}</td>
						</tr>

						<tr>
							<td>Payment Period</td>
							<td><c:out value="${subscriptionPeriod}" /></td>
						</tr>

						<tr>
							<td>Payment Terms</td>
							<td><c:out value="${paymentTermsDays}"/> days</td>
						</tr>

						<tr>
							<td>Service Type</td>
							<td>
								<c:choose>
									<c:when test="${fn:length(accountServiceTypeConfigurations) > 0}">
										<c:forEach items="${accountServiceTypeConfigurations}" var="serviceType">
											<div>
												<c:out value="${serviceType.country.name}"/>: <c:out value="${serviceType.accountServiceType.description}"/>
											</div>
										</c:forEach>
									</c:when>
									<c:otherwise>
										None
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</table>
				</div>
				<br>
				<div>
					<h5>Pricing</h5>
					<div class="control-group">
						<div>
							<c:set var="subscriptionTierNumber" value="0"/>
							<c:set var="totalTiers" value="${fn:length(subscriptionPaymentTiers)}"/>
							<table>
								<thead>
									<tr>
										<th class="text text-center">Tier</th>
										<th class="text text-center">Lower Bound</th>
										<th class="text text-center">Upper Bound</th>
										<th class="text text-center">Period Payment Amount</th>
										<c:if test="${vendorOfRecord}">
											<th class="text text-center">Vendor of Record Amount</th>
										</c:if>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${subscriptionPaymentTiers}" var="tier">
										<c:set var="subscriptionTierNumber" value="${subscriptionTierNumber + 1}"/>
										<tr>
											<td class="text text-center"><c:out value="${subscriptionTierNumber}" /></td>
											<c:choose>
												<c:when test="${subscriptionTierNumber < totalTiers}">
													<td class="text text-right"><fmt:formatNumber value="${tier.minimum + 0.01}" currencySymbol="$" type="currency"/></td>
													<td class="text text-right"><fmt:formatNumber value="${tier.maximum}" currencySymbol="$" type="currency"/></td>
												</c:when>
												<c:otherwise>
													<td class="text text-right"><fmt:formatNumber value="${(tier.minimum + 0.01)}" currencySymbol="$" type="currency"/></td>
													<td class="text text-right">+</td>
												</c:otherwise>
											</c:choose>

											<td class="text text-right"><fmt:formatNumber value="${tier.paymentAmount}" currencySymbol="$" type="currency"/></td>

											<c:if test="${vendorOfRecord}">
												<td class="text text-right"><fmt:formatNumber value="${tier.vendorOfRecordAmount}" currencySymbol="$" type="currency"/></td>
											</c:if>
										</tr>

										<%-- Check for software and VOR active tiers --%>
										<c:if test="${tier.subscriptionPaymentTierSoftwareStatusType == 'active'}">
											<c:set var="activeSoftwareTierNumber" value="${subscriptionTierNumber}"/>
											<c:set var="activeSoftwareTier" value="${tier}"/>
										</c:if>
										<c:if test="${tier.subscriptionPaymentTierVorStatusType == 'active'}">
											<c:set var="activeVORTierNumber" value="${subscriptionTierNumber}"/>
											<c:set var="activeVORTier" value="${tier}"/>
										</c:if>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
					<br>
					<div>
						<h5>Current Subscription Usage</h5>
						<table>
							<thead>
								<tr>
									<th class="text text-center">Type</th>
									<th class="text text-center">Current Tier</th>
									<th class="text text-center">Percent Used</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>Software</td>
									<td>
										Tier <c:out value="${activeSoftwareTierNumber}"/>
										from <fmt:formatNumber value="${activeSoftwareTier.minimum + 0.01}" currencySymbol="$" type="currency"/>
										to <fmt:formatNumber value="${activeSoftwareTier.maximum}" currencySymbol="$" type="currency"/>
									</td>
									<td>
										<div><fmt:formatNumber value="${softwareTierUsage}" type="percent" maxFractionDigits="0"/></div>
										<c:set var="softwareTierUsage" value="${(softwareThroughput - activeSoftwareTier.minimum)/ (activeSoftwareTier.maximum - activeSoftwareTier.minimum)}"/>
									</td>
								</tr>
								<c:if test="${vendorOfRecord}">
									<tr>
										<td>Vendor of Record</td>
										<td>
										Tier <c:out value="${activeVORTierNumber}"/>
										from <fmt:formatNumber value="${activeVORTier.minimum + 0.01}" currencySymbol="$" type="currency"/>
										to <fmt:formatNumber value="${activeVORTier.maximum}" currencySymbol="$" type="currency"/>
										</td>
										<td>
										<c:if test="${vendorOfRecord}">
											<c:set var="vorTierUsage" value="${(vorThroughput - activeVORTier.minimum) / (activeVORTier.maximum - activeVORTier.minimum)}"/>
											<div><fmt:formatNumber value="${vorTierUsage}" type="percent" maxFractionDigits="0"/></div>
										</c:if>
										</td>
									</tr>
								</c:if>
							</tbody>
						</table>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</div>
</wm:app>
