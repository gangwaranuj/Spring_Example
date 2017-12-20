<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="${assessment.name}" bodyclass="lms" webpackScript="lms">

	<script>
		var config = {
			mode: 'take',
			id: '${wmfmt:escapeJavaScript(requestScope.assessment.id)}',
			type: '${wmfmt:escapeJavaScript((requestScope.survey) ? 'survey' : 'graded')}',
			isLatestAttemptInprogress: ${requestScope.latestAttempt.status.code eq 'inprogress'},
			assessmentItemType: ${AssessmentItemTypeJSON},
			assignment: '${wmfmt:escapeJavaScript(param.assignment)}',
			onBehalfOf: '${wmfmt:escapeJavaScript(param.onBehalfOf)}',
			isWorkNotEmpty: ${not empty(work)},
			workNumber: '${wmfmt:escapeJavaScript(requestScope.work.work.workNumber)}',
			isGroupNotEmpty: ${not empty(param['group'])},
			group: '${wmfmt:escapeJavaScript(param['group'])}',
			assessmentItemsJson: ${assessmentItemsJson},
			latestAttemptResponsesJson: ${latestAttemptResponsesJson},
			durationMinutes: '${wmfmt:escapeJavaScript(requestScope.durationMinutes)}',
			timeLeft: '${wmfmt:escapeJavaScript(requestScope.timeLeft)}',
		};
	</script>

	<c:set var="isSurvey" value="${assessment.type.value eq AssessmentType.SURVEY}" />
	<div class="inner-container">
	<div class="page-header">
		<h2><c:out value="${assessment.name}" /></h2>
	</div>

	<div class="row_wide_sidebar_right">
		<div class="content">
			<c:if test="${not empty(requestScope.work)}">
				<p>This survey is to be completed as part of your assignment, "<c:out value="${requestScope.work.work.title}"/>". When you finish the survey, you will be automatically taken back to the assignment to complete your notes and payment request.</p>
			</c:if>
			<jsp:include page="/WEB-INF/views/web/partials/lms/embed.jsp"/>
		</div>
		<div class="sidebar">
			<div class="well-b2">
				<h3>Stats</h3>
				<div class="stats clear">
					<ul>
						<li>
							<span>${fn:length(requestScope.assessment.items)}</span>
							<br/>
							Questions
						</li>
						<c:if test="${graded && (assessment.configuration.passingScoreShared || assessment.configuration.statisticsShared || assessment.setApproximateDurationMinutes)}">
							<c:if test="${requestScope.graded and requestScope.assessment.configuration.passingScoreShared}">
								<li>
									<span><c:out value="${assessment.configuration.passingScore}"/></span>
									<br/>
									% to pass
								</li>
							</c:if>
							<c:if test="${not empty(requestScope.assessment.configuration.durationMinutes) and (requestScope.assessment.configuration.durationMinutes > 0)}">
								<li class="last">
									<span><c:out value="${assessment.approximateDurationMinutes}"/></span>
									<br/>
									Minutes<br/>
									Time Limit
								</li>
							</c:if>
						</c:if>
					</ul>
				</div>
			</div>
			<c:if test="${isSurvey}">
					<a href="/lms/print/${assessment.id}<c:if test='${not empty work}'>?assignment=${work.work.workNumber}</c:if>" class="alert-message-btn">
						<div class="alert">
							<div class="media">
								<i class="media-object icon-print icon-4x icon-gray pull-left"></i>
									<div class="media-body">
										<h5>Print Survey</h5>
										<p>Need to complete the survey offline and enter your answers later? Print a copy to take with you.</p>
									</div>
							</div>
						</div>
					</a>
			</c:if>
		</div>
		<div id="time-up-modal"></div>
		<div id="confirm-submission-view"></div>
	</div>
	</div>

</wm:app>

