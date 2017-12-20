<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="rating" value="${buyerScoreCard.valuesWithStringKey['PERCENTAGE_RATINGS_OVER_4_STARS'].net90}" />
<c:set var="paidCount" value="${buyerScoreCard.valuesWithStringKey['PAID_WORK'].net90}" />
<c:set var="approvalTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS'].net90}" />
<c:set var="paymentTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_PAY_WORK_IN_DAYS'].net90}" />

<div class="well-b2 scorecard -company">
	<h3><c:if test="${is_in_work_company or is_internal}">My </c:if>Company Scorecard
		<span class="member-since">Member Since ${wmfmt:formatMillis('MM/yyyy', work.company.createdOn)}</span>
	</h3>
	<div class="well-content">
		<c:choose>
			<c:when test="${(paidCount == 0 or rating == 0)}">
				<em>No Ratings Yet</em><br/>
				<small>New client on Work Market</small>
			</c:when>
			<c:otherwise>
				<ul class="unstyled">
					<li>
						<c:choose>
							<c:when test="${rating >= 90}">
								<span class="label -good -detail-page"><fmt:formatNumber value="${rating}" maxFractionDigits="1"/>% </span>
							</c:when>
							<c:when test="${rating < 90 and rating >= 75 }">
								<span class="label -neutral -detail-page"><fmt:formatNumber value="${rating}" maxFractionDigits="1"/>% </span>
							</c:when>
							<c:otherwise>
								<span class="label -bad -detail-page"><fmt:formatNumber value="${rating}" maxFractionDigits="1"/>% </span>
							</c:otherwise>
						</c:choose>
						Satisfaction Rating
					</li>
					<li>
						<c:choose>
							<c:when test="${paymentTime < 0}">
								<span class="label -good -detail-page">
									<fmt:formatNumber value="${paymentTime * - 1}" maxFractionDigits="1"/>
									<c:out value="${wmfmt:pluralizeDouble('day', paymentTime)}"/> early
								</span>
							</c:when>
							<c:when test="${paymentTime >= 0 and paymentTime < 1}">
								<span class="label -neutral -detail-page">On-Time</span>
							</c:when>
							<c:otherwise>
								<span class="label -bad -detail-page">
									<fmt:formatNumber value="${paymentTime}" maxFractionDigits="1"/>
									<c:out value="${wmfmt:pluralizeDouble('day', paymentTime)}"/> late
								</span>
							</c:otherwise>
						</c:choose>
						Payment Timeliness
					</li>
					<li>
						<c:choose>
							<c:when test="${approvalTime <= 1}">
								<span class="label -good -detail-page">
									<c:if test="${not empty work.partGroup}">
										<i class="icon-truck icon-large tooltipped tooltipped-n" aria-label="<c:out value="${companyName}"/> uses Parts and Logistics tracking which may increase the approval time by few days"></i>
									</c:if>
									&lt; 24 Hrs
								</span>
							</c:when>
							<c:when test="${approvalTime > 1 and approvalTime <= 5}">
								<span class="label -good -detail-page">
									<c:if test="${not empty work.partGroup}">
										<i class="icon-truck icon-large tooltipped tooltipped-n" aria-label="<c:out value="${companyName}"/> uses Parts and Logistics tracking which may increase the approval time by a few days"></i>
									</c:if>
									<fmt:formatNumber value="${approvalTime}" maxFractionDigits="1"/>
									<c:out value="${wmfmt:pluralizeDouble('day', approvalTime)}"/>
								</span>
							</c:when>
							<c:when test="${approvalTime > 5 and approvalTime <= 10}">
								<span class="label -neutral -detail-page">
									<c:if test="${not empty work.partGroup}">
										<i class="icon-truck icon-large tooltipped tooltipped-n" aria-label="<c:out value="${companyName}"/> uses Parts and Logistics tracking which may increase the approval time by a few days"></i>
									</c:if>
									<fmt:formatNumber value="${approvalTime}" maxFractionDigits="1"/>
									<c:out value="${wmfmt:pluralizeDouble('day', approvalTime)}"/>
								</span>
							</c:when>
							<c:otherwise>
								<span class="label -bad -detail-page">
									<c:if test="${not empty work.partGroup}">
										<i class="icon-truck icon-large tooltipped tooltipped-n" aria-label="<c:out value="${companyName}"/> uses Parts and Logistics tracking which may increase the approval time by a few days"></i>
									</c:if>
									<fmt:formatNumber value="${approvalTime}" maxFractionDigits="1"/>
									<c:out value="${wmfmt:pluralizeDouble('day', approvalTime)}"/>
								</span>
							</c:otherwise>
						</c:choose>
						Approval Time
					</li>
				</ul>
				<small><em>Based on last 90 days</em>
					<a href="/profile/company/${companyNumber}" class="fr">Learn More &raquo;</a>
				</small>
			</c:otherwise>
		</c:choose>
		<div class="clearfix"></div>
	</div>
</div>
