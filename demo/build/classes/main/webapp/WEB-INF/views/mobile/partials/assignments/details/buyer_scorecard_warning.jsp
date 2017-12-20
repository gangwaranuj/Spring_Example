<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<c:set var="scoreCard" value="${buyerScoreCard}" />
<c:set var="statsCard" value="${buyerScoreCard.companyStatsCard}" />
<c:set var="rating" value="${buyerScoreCard.valuesWithStringKey['PERCENTAGE_RATINGS_OVER_4_STARS']}" />
<c:set var="paidCount" value="${buyerScoreCard.valuesWithStringKey['PAID_WORK']}" />
<c:set var="approvalTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS']}" />
<c:set var="paymentTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_PAY_WORK_IN_DAYS']}" />

<div class="buyer-scorecard-warning-content">
	<c:if test="${scoreCard.hasBadScore()}">
		<p>
			<c:choose>
				<c:when test="${paymentTime.net90Score.isBad()}" >
					<fmt:formatNumber value="${paymentTime.net90}" maxFractionDigits="1"/>
					I understand that <c:out value="${work.company.name}" /> has assignments that are more than <c:out value="${wmfmt:pluralizeDouble('day', paymentTime.net90)}"/> past due.
				</c:when>
				<c:otherwise>
					I understand that <c:out value="${work.company.name}" /> has
				</c:otherwise>
			</c:choose>
		</p>
		<p><strong class="scorecard-warning"><c:out value="${statsCard.valuesWithStringKey['PENDING_APPROVAL_WORK_PERCENTAGE']}"/>%</strong> assignments pending approval</p>
		<p><strong class="scorecard-warning"><c:out value="${statsCard.valuesWithStringKey['PAST_DUE_WORK_PERCENTAGE']}"/>%</strong> assignments past due</p>
		<p><strong class="scorecard-warning"><c:out value="${rating.net90}"/>%</strong> satisfaction rating</p>
	</c:if>
</div>