<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="scoreCard" value="${buyerScoreCard}" />
<c:set var="statsCard" value="${buyerScoreCard.companyStatsCard}" />
<c:set var="rating" value="${buyerScoreCard.valuesWithStringKey['PERCENTAGE_RATINGS_OVER_4_STARS']}" />
<c:set var="paidCount" value="${buyerScoreCard.valuesWithStringKey['PAID_WORK']}" />
<c:set var="approvalTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS']}" />
<c:set var="paymentTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_PAY_WORK_IN_DAYS']}" />
<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<div id="accept-box">
	<c:if test="${!hidePricing}">
		<div class="${param.isAcceptableOrApplyable ? 'accept-box-modal-hide' : 'accept-box-modal'}">
			You are trying to apply/accept an assignment but your profile is not listed in search results (or you have yet to be approved by Work Market).
			In order to apply/accept your profile must be approved and listed in search. Click below to update your search preferences.
			<br><a href="/profile-edit/lanes">Update Search Preferences</a>
		</div>
		<c:choose>

			<c:when test="${!(workResponse.viewingResource.user.laneType.value eq laneTypes['LANE_1'])}">
				<c:choose>
					<c:when test="${not work.configuration.assignToFirstResource}">
						<h4>By Applying, I understand that if my application is accepted:</h4>
					</c:when>
					<c:otherwise>
						<h4>By Accepting, I understand that:</h4>
				</c:otherwise>
				</c:choose>

				<ul>
					<li>I will be contracting for <c:out value="${work.company.name}" /></li>
					<li>I agree to the terms of this assignment</li>
					<li>Payment is the responsibility of <c:out value="${work.company.name}" /><c:if test="${offlinePaymentEnabled}">, and <b>will be handled outside the Work Market Platform</b></c:if></li>
					<c:choose>
						<c:when test="${work.configuration.paymentTermsDays > 0}">
							<li>I will be paid within <c:out value="${work.configuration.paymentTermsDays}"/> days of approval of my work
						</c:when>
						<c:otherwise>
							<li>I will be paid upon approval of my work</li>
						</c:otherwise>
					</c:choose>
				</ul>

				<c:if test="${scoreCard.hasBadScore()}">
					<div class="buyer-scorecard-warning-well">
							<p>
								<c:choose>
									<c:when test="${paymentTime.net90Score.isBad()}" >
										I understand that <c:out value="${work.company.name}" /> has assignments that are more than
										<fmt:formatNumber value="${paymentTime.net90}" maxFractionDigits="1"/>
										<c:out value="${wmfmt:pluralizeDouble('day', paymentTime.net90)}"/> past due.
									</c:when>
									<c:otherwise>
										I understand that <c:out value="${work.company.name}" /> has
								</c:otherwise>
								</c:choose>
							</p>
							<ul>
								<p><strong class="scorecard-warning"><c:out value="${statsCard.valuesWithStringKey['PENDING_APPROVAL_WORK_PERCENTAGE']}"/>%</strong> assignments pending approval</p>
								<p><strong class="scorecard-warning"><c:out value="${statsCard.valuesWithStringKey['PAST_DUE_WORK_PERCENTAGE']}"/>%</strong> assignments past due</p>
								<p><strong class="scorecard-warning"><c:out value="${rating.net90}"/>%</strong> satisfaction rating</p>
							</ul>
					</div>
				</c:if>

			</c:when>
			<c:otherwise>
				<p>This is an internal assignment for <c:out value="${work.company.name}" />. If you are not a W2 employee of <c:out value="${work.company.name}" />, then accepting this work is in violation of the
					<a href="/tos">Terms of Use Agreement</a> and your account will be suspended immediately.
				</p>
			</c:otherwise>

		</c:choose>
	</c:if>

	<p>
	<c:choose>
		<c:when test="${not work.configuration.assignToFirstResource}">
			<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/resource_apply.jsp" />
		</c:when>
		<c:otherwise>
			<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/resource_accept.jsp" />
		</c:otherwise>
	</c:choose>
	</p>

	<c:if test="${!hidePricing and is_invited_resource and !(workResponse.viewingResource.user.laneType.value eq laneTypes['LANE_1'])}">
		<p><small><a href="javascript:void(0);" class="blockclient_action">Block this company from sending you more assignments</a></small></p>
	</c:if>
</div>
