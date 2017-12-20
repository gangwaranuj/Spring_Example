<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Pricing" bodyclass="manage-company" webpackScript="admin">

	<c:set var="isSubscription" value="${payment_configuration.accountPricingType == 'subscription'}"/>
	<c:set var="companyId" value="${requestScope.id}"/>
	<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_CONTROLLER')">
		<c:set var="isAdminOrController" value="true" scope="page"/>
	</sec:authorize>

	<%-- Next possible update date for active subscription--%>
	<c:set var="nextPossibleUpdateDate" scope="request" value=""/>
	<c:if test="${not empty next_possible_update_date}">
		<c:set var="nextPossibleUpdateDate" scope="request" value="${wmfmt:formatCalendar('MM/dd/YYYY', next_possible_update_date)}"/>
	</c:if>

	<%-- Set subscription_status --%>
	<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/subscription_status.jsp"/>

	<%-- Determine when to hide subscription details --%>
	<c:set var="showSubscriptionView" value="${subscription_status eq 'active' || subscription_status eq 'effective' || subscription_status eq 'cancellation_pending' || subscription_status eq 'cancellation_approved'}"/>

	<script>
		var subscriptionTiers = [];
		var subscriptionAddOns = [];
		var subscriptionServiceConfigs = [];

		<%-- Tiers --%>
		<c:forEach items="${subscription.pricingRanges}" var="tier">
			subscriptionTiers.push([${tier.minimum}, ${tier.maximum}, ${tier.paymentAmount}, ${tier.vendorOfRecordAmount} || 0]);
		</c:forEach>

		<%-- Add-ons --%>
		<c:if test="${not empty subscription}">
		<c:forEach items="${subscription.subscriptionAddOnDTOs}" var="addOn">
			subscriptionAddOns.push({type: '<c:out value="${addOn.addOnTypeCode}"/>', cost: '<c:out value="${addOn.costPerPeriod}"/>'});
		</c:forEach>
		</c:if>

		<%-- Service types --%>
		<c:if test="${not empty subscription}">
		<c:forEach items="${subscription.accountServiceTypeDTOs}" var="serviceConfig">
			subscriptionServiceConfigs.push({countryCode: '<c:out value="${serviceConfig.countryCode}"/>', accountServiceTypeCode: '<c:out value="${serviceConfig.accountServiceTypeCode}"/>'});
		</c:forEach>
		</c:if>

		var config = {
			mode: 'companySubscription',
			pricingType: "${payment_configuration.accountPricingType}",
			subscriptionStatus: "${subscription_status}",
			subscriptionCanRenew: ${subscription_can_renew},
			nextPossibleUpdateDate: "${nextPossibleUpdateDate}",
			subscriptionTiers: subscriptionTiers,
			subscriptionAddOns: subscriptionAddOns,
			subscriptionServiceConfigs: subscriptionServiceConfigs,
			isVendorOfRecord: <c:out value="${not empty subscription && subscription.vendorOfRecord}"/>,
			hasServiceType: <c:out value="${not empty subscription && not empty subscription.accountServiceTypeDTOs}"/>
		};
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar admin">
			<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
		</div>

		<div class="content">
			<div id="dynamic_messages"></div>

			<c:import url="/WEB-INF/views/web/partials/admin/manage/company/header.jsp"/>

			<c:if test="${not isSubscription}">
				<h5>Transaction Fee Ranges</h5>

				<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/transactional_details.jsp"/>
			</c:if>
			<c:if test="${not isSubscription and enable_fee_band_edit}">
				<div class="wm-action-container" id="submit_transactional_form">
					<button type="button" class="button">Save</button>
				</div>
			</c:if>

			<hr/>
			<c:if test="${not showSubscriptionView}">
				<button type="button" class="button" id="switch_to_subscription">
					<c:choose>
						<c:when test="${subscription_status eq 'pending_approval' || subscription_status eq 'cancellation_pending'}">
							View pending subscription
						</c:when>
						<c:otherwise>
							Switch to Subscription
						</c:otherwise>
					</c:choose>
				</button>
			</c:if>

			<div id="subscription_details" class="<c:if test="${not showSubscriptionView}">dn</c:if>">
				<div id="subscription_header">
					<h5>
						Subscription Details
						<c:if test="${subStatus.pendingApproval}">
							<span id="subscription_status" class="label label-notice">Pending Approval</span>
						</c:if>
						<c:if test="${subStatus.hasRenewalPending}">
							<span id="subscription_status" class="label label-notice">Renewal Pending</span>
						</c:if>
						<c:if test="${subStatus.hasCancelPending}">
							<span id="subscription_status" class="label label-important">Cancellation Pending</span>
						</c:if>
						<c:if test="${subStatus.hasEditPending}">
							<span id="subscription_status" class="label label-notice">Edit Pending</span>
						</c:if>
					</h5>

						<%-- Only show actions when in subscription mode or there's an approved subscription --%>
					<c:if test="${isSubscription || subscription_status eq 'active'}">
						<div class="dropdown pull-right">
							<a class="btn" data-toggle="dropdown">More Actions</a>
							<ul class="dropdown-menu">
								<c:if test="${not empty nextPossibleUpdateDate}">
									<li>
										<a id="edit_subscription">
											Edit
										</a>
									</li>
								</c:if>
								<c:if test="${subscription_can_renew}">
									<li>
										<a id="renew_subscription">
											Renew
										</a>
									</li>
								</c:if>
								<c:if test="${subscription_status == 'active' || subscription_status == 'effective'}">
									<li>
										<a id="cancel_subscription">
											Cancel
										</a>
									</li>
								</c:if>
								<li>
									<a id="issue_future_invoice" href="/admin/manage/company/issue_future_invoice/${company.id}/${subscription_id}">
										Issue future invoice
									</a>
								</li>
							</ul>
						</div>
					</c:if>
				</div>

				<c:choose>
					<c:when test="${subscription_status eq 'pending_approval'}">
						<i>Submitted on ${wmfmt:formatCalendar("MM/dd/yyyy", subscription_date)} by <c:out value="${subscription_creator_full_name}"/></i>
					</c:when>
					<c:when test="${subscription_status eq 'active'}">
						<i>New Subscription approved on ${wmfmt:formatCalendar("MM/dd/yyyy", subscription_date)}</i>
					</c:when>
					<c:when test="${subscription_status eq 'renewal'}">
						<i>Renewal Subscription approved on ${wmfmt:formatCalendar("MM/dd/yyyy", subscription_date)}</i>
					</c:when>
					<c:when test="${subscription_status eq 'auto_renewal'}">
						<i>Auto-Renewal Subscription approved on ${wmfmt:formatCalendar("MM/dd/yyyy", subscription_date)}. X number of renewals remaining.</i>
					</c:when>
				</c:choose>

				<div id="subscription_form_container">
					<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/subscription_details.jsp"/>
				</div>
			</div>

			<div id="previous_subscriptions_details">
				<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/previous_subscriptions_details.jsp" />
			</div>
		</div>
	</div>

	<%-- Subscription modals--%>
	<c:if test="${isSubscription || subscription_status eq 'active'}">
		<div class="dn">
			<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/cancel_subscription.jsp"/>
		</div>
	</c:if>

	<c:if test="${subscription_can_renew}">
		<div class="dn">
			<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/renew_subscription.jsp"/>
		</div>
	</c:if>

</wm:admin>
