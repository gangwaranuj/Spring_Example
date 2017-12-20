<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:set var="scoreCard" value="${buyerScoreCard}" scope="request"/>
<c:set var="statsCard" value="${buyerScoreCard.companyStatsCard}" />
<c:set var="rating" value="${buyerScoreCard.valuesWithStringKey['PERCENTAGE_RATINGS_OVER_4_STARS']}" />
<c:set var="paidCount" value="${buyerScoreCard.valuesWithStringKey['PAID_WORK']}" />
<c:set var="approvalTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS']}" />
<c:set var="paymentTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_PAY_WORK_IN_DAYS']}" />
<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<div class="assignment-response">
	<%--Make the payment terms tag to remind user of terms for this asssignment--%>
	<c:set var="termsTagText" value="${work.configuration.paymentTermsDays} day terms" scope="request"/>
	<c:choose>
		<c:when test="${work.configuration.paymentTermsDays > 21}">
			<c:set var="termsTagClass" value="bad" scope="request"/>
		</c:when>
		<c:when test="${work.configuration.paymentTermsDays > 7}">
			<c:set var="termsTagClass" value="neutral" scope="request"/>
		</c:when>
		<c:when test="${work.configuration.paymentTermsDays > 0}">
			<c:set var="termsTagClass" value="good" scope="request"/>
		</c:when>
		<c:otherwise>
			<c:set var="termsTagClass" value="good" scope="request"/>
			<c:set var="termsTagText" value="Paid Immediately" scope="request"/>
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${not work.configuration.assignToFirstResource}">
			<c:set var="doWorkUri" value="/mobile/assignments/apply/${work.workNumber}" scope="request"/>
			<c:set var="doWorkTitle" value="Apply" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="doWorkUri" value="/mobile/assignments/accept/${work.workNumber}" scope="request"/>
			<c:set var="doWorkTitle" value="Accept" scope="request"/>
		</c:otherwise>
	</c:choose>

	<div class="response-buttons">
		<c:choose>
			<c:when test="${not empty workResponse.workBundleParent}">
				<p>This assignment is part of a bundle called: "${workResponse.workBundleParent.title}". To view this bundle on the full site, tap below.</p>
				<a class="view-bundle" href="/assignments/view_bundle/${workResponse.workBundleParent.id}">View Bundle on Full Site</a>

			</c:when>
			<c:otherwise>
				<c:import url="/WEB-INF/views/mobile/partials/assignments/details/accept_terms.jsp"/>
				<c:import url="/WEB-INF/views/mobile/partials/assignments/details/buyer_scorecard_warning.jsp"/>

				<c:choose>
					<c:when test="${isWorkerCompany}" >
						<c:choose>
							<c:when test="${!hidePricing}">
								<c:import url="/WEB-INF/views/mobile/partials/assignments/details/assignment_response_buttons.jsp" />
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${not work.configuration.assignToFirstResource}">
										<p class="notice">You do not have permission to apply for this assignment.</p>
									</c:when>
									<c:otherwise>
										<p class="notice">You do not have permission to accept this assignment.</p>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:import url="/WEB-INF/views/mobile/partials/assignments/details/assignment_response_buttons.jsp" />
					</c:otherwise>
				</c:choose>

			</c:otherwise>
		</c:choose>
	</div><%--response buttons--%>
</div><%--assignment response--%>

<div id="buyer-warning-popup" class="popup-content buyer-scorecard-warning-content">
	<h2>
		<c:choose>
			<c:when test="${paymentTime.net90Score.isBad()}" >
				<fmt:formatNumber value="${paymentTime.net90}" maxFractionDigits="1"/>
				I understand that <c:out value="${work.company.name}" /> has assignments that are more than <c:out value="${wmfmt:pluralizeDouble('day', paymentTime.net90)}"/> past due.
			</c:when>
			<c:otherwise>
				I understand that <c:out value="${work.company.name}" /> has
			</c:otherwise>
		</c:choose>
	</h2>
	<div class="buyer-scorecard-warning-content-metrics">
		<ul>
			<strong class="scorecard-warning"><c:out value="${statsCard.valuesWithStringKey['PENDING_APPROVAL_WORK_PERCENTAGE']}"/>%</strong> assignments pending approval<br/>
			<strong class="scorecard-warning"><c:out value="${statsCard.valuesWithStringKey['PAST_DUE_WORK_PERCENTAGE']}"/>%</strong> assignments past due<br/>
			<strong class="scorecard-warning"><c:out value="${rating.net90}"/>%</strong> satisfaction rating
		</ul>
	</div>
	<a href="<c:out value="${doWorkUri}" />" class="close-button ${termsTagClass}">Confirm</a>
	<a class="popup-close close-button">Cancel</a>
</div><%--buyer scorecard warning contect--%>
