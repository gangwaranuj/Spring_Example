<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="rating" value="${buyerScoreCard.valuesWithStringKey['PERCENTAGE_RATINGS_OVER_4_STARS'].net90}" />
<c:set var="paidCount" value="${buyerScoreCard.valuesWithStringKey['PAID_WORK'].net90}" />
<c:set var="approvalTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS'].net90}" />
<c:set var="paymentTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_PAY_WORK_IN_DAYS'].net90}" />

<span class="buyer-metrics-page">
	<a class="show active" href="javascript:void(0);">
		Company Scorecard
		<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-bargraph.jsp"/>
		<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
		<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
	</a>
	<div class="tell">
		<p>Member Since: <span>${wmfmt:formatMillis('MM/yyyy', work.company.createdOn)}</span></p>
		<c:choose>
			<c:when test="${(paidCount == 0 or rating == 0)}">
				<p>Ratings: <span>No Ratings Yet</span></p>
			</c:when>
			<c:otherwise>
				<p>Satisfaction Rating:
					<c:choose>
						<c:when test="${rating >= 90}">
						<span class="good-text">
						<fmt:formatNumber value="${rating}" maxFractionDigits="1"/>%
						</span>
						</c:when>
						<c:when test="${rating < 90 and rating >= 75 }">
						<span class="neutral-text">
						<fmt:formatNumber value="${rating}" maxFractionDigits="1"/>%
						</span>
						</c:when>
						<c:otherwise>
						<span class="bad-text">
						<fmt:formatNumber value="${rating}" maxFractionDigits="1"/>%
						</span>
						</c:otherwise>
					</c:choose>
				</p><%--satisfaction--%>
				<p>Payment Timeliness:
					<c:choose>
						<c:when test="${paymentTime < 0}">
						<span class="good-text">
						<fmt:formatNumber value="${paymentTime * - 1}" maxFractionDigits="1"/>
						<c:out value="${wmfmt:pluralizeDouble('day', paymentTime)}"/>
						early
						</span>
						</c:when>
						<c:when test="${paymentTime >= 0 and paymentTime < 1}">
						<span class="neutral-text">
						On Time
						</span>
						</c:when>
						<c:otherwise>
						<span class="bad-text">
						<fmt:formatNumber value="${paymentTime}" maxFractionDigits="1"/>
						<c:out value="${wmfmt:pluralizeDouble('day', paymentTime)}"/> late
						</span>
						</c:otherwise>
					</c:choose>
				</p><%--payment timeliness--%>
				<p> Approval Time:
					<c:set var="partsAsterisk" value="${not empty work.partGroup ? '*' : ''}" />
					<c:choose>
						<c:when test="${approvalTime <= 1}">
							<span class="good-text">< 24 Hrs ${partsAsterisk}</span>
						</c:when>
						<c:when test="${approvalTime >1 and approvalTime <=5}">
						<span class="good-text">
						<fmt:formatNumber value="${approvalTime}" maxFractionDigits="1"/>
						<c:out value="${wmfmt:pluralizeDouble('day', approvalTime)}"/> ${partsAsterisk}
						</span>
						</c:when>
						<c:when test="${approvalTime >5 and approvalTime <=10}">
						<span class="neutral-text">
						<fmt:formatNumber value="${approvalTime}" maxFractionDigits="1"/>
						<c:out value="${wmfmt:pluralizeDouble('day', approvalTime)}"/> ${partsAsterisk}
						</span>
						</c:when>
						<c:otherwise>
					<span class="bad-text">
					<fmt:formatNumber value="${approvalTime}" maxFractionDigits="1"/>
					<c:out value="${wmfmt:pluralizeDouble('day', approvalTime)}"/>  ${partsAsterisk}
					</span>
						</c:otherwise>
					</c:choose>
				</p><%--approval time--%>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty work.partGroup}">
			<p class="parts-logistics-message"><span> * ${work.company.name} uses Parts and Logistics tracking which may increase the approval time by few days</span></p>
		</c:if>
	</div><%--tell--%>
</span><%--buyer metrics page--%>

