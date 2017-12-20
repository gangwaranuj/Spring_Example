<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Grading" bodyclass="page-lms" fluid="true" webpackScript="lms">

	<script>
		var config = {
			mode: 'grade'
		};
	</script>

	<c:set var="isSurvey" value="${gradingResponse.assessment.type.value eq AssessmentType.SURVEY}" />

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<div class="page-header">
		<h2><c:out value="${gradingResponse.assessment.name}" /></h2>
	</div>

	<div id="grade-attempt-container" class="row_wide_sidebar_right">
		<div class="content">
			<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
				<c:param name="containerId" value="grade_notices" />
			</c:import>
			<c:import url="/WEB-INF/views/web/partials/lms/listall.jsp" />
		</div>

		<div class="sidebar">
			<c:if test="${gradingResponse.requestedAttempt.status.code == 'gradePending'}">
				<div class="well-b2">
					<div class="well-content tac">
						<p>Grade pending. Submit for final score.</p>
						<a href="/lms/manage/grade_attempt/${gradingResponse.assessment.id}/${gradingResponse.requestedAttempt.id}/${gradingResponse.requestedAttempt.user.id}" class="btn success large">Submit Grade</a>
					</div>
				</div>
			</c:if>

			<div class="well-b2">
				<h3>Results</h3>

				<div class="worker-details">
					<c:choose>
						<c:when test="${not empty gradingResponse.requestedAttempt.user.avatarSmall}">
							<wm:avatar src="${wmfn:stripUriProtocol(wmfmt:stripXSS(gradingResponse.requestedAttempt.user.avatarSmall.uri))}" />
						</c:when>
						<c:otherwise>
							<wm:avatar hash="${gradingResponse.requestedAttempt.user.userNumber}" />
						</c:otherwise>
					</c:choose>
					<div class="overview">
						<strong><a href="/profile/${gradingResponse.requestedAttempt.user.userNumber}" target="_blank"><c:out value="${gradingResponse.requestedAttempt.user.name.firstName}"/> <c:out value="${gradingResponse.requestedAttempt.user.name.lastName}"/></a></strong><br />
						<c:out value="${gradingResponse.requestedAttempt.user.company.name}"/>
					</div>
				</div>

				<div class="stats clear" style="border-top:1px solid #ddd;">
					<ul>
						<li>
							<span><c:out value="${wmfmt:formatMillisWithTimeZone('M/dd/yyyy', gradingResponse.requestedAttempt.completeOn, currentUser.timeZoneId)}" /></span>
							<br/>
							Completed
						</li>
					</ul>
				</div>

				<div class="stats clear">
					<ul>
						<li>
							<span><c:out value="${gradingResponse.requestedAttempt.totalAttemptsCount}"/></span>
							<br/>
							Attempts
						</li>
						<li>

							<span><c:out value="${wmfmt:getDurationBreakdownHours(gradingResponse.requestedAttempt.completeOn - gradingResponse.requestedAttempt.createdOn)}" /></span>
							<br/>
							Time Taken
						</li>
					</ul>
				</div>

				<c:if test="${isGrader && gradingResponse.assessment.type.value == AssessmentType.GRADED}">
					<div class="stats clear">
						<ul>
							<c:if test="${gradingResponse.assessment.type.value == AssessmentType.GRADED}">
								<li>
									<span><c:out value="${fn:length(gradingForm.responses)}" /></span>
									<br/>
									Answered
								</li>

								<li>
									<span><c:out value="${gradedCount}"/></span>
									<br/>
									Need Grading
								</li>
							</c:if>
						</ul>
					</div>
				</c:if>
				<c:if test="${gradingResponse.requestedAttempt.status.code == 'graded'}">
					<div class="stats clear">
						<ul>
							<li><span><fmt:formatNumber value="${gradingResponse.requestedAttempt.score}" maxFractionDigits="1"/></span>
								<br/>
								% Final score
							</li>
							<li><c:out value="${gradingResponse.requestedAttempt.passed ? 'Passed' : 'Failed'}" /></li>
						</ul>
					</div>
				</c:if>
				<div class="well-content">
					<ul class="unstyled">
						<c:if test="${gradingResponse.assessment.hasAssetItems}">
							<li><a href="/lms/manage/download_attempt_assets/${gradingResponse.assessment.id}/${gradingResponse.requestedAttempt.id}" title="Download Photos" id="cta-download-assets">Download photo answers</a></li>
							<li><a href="/lms/manage/assets/${gradingResponse.assessment.id}/" title="View Survey Attachments">View Survey Attachments</a></li>
						</c:if>
						<li><a href="/lms/view/details/${gradingResponse.assessment.id}">Return to overview</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>

</wm:app>
