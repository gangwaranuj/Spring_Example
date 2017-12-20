<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<link href="${mediaPrefix}/mobile-lms.css" rel="stylesheet" type="text/css"/>

<c:set var="webpackScript" value="lms" scope="request"/>

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
		latestAttemptResponsesJson: ${latestAttemptResponsesJson}
	};
</script>

<div class="survey-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Survey" />
	</jsp:include>

	<div class="grid content">

		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div>

		<c:set var="isSurvey" value="${assessment.type.value eq AssessmentType.SURVEY}" />

		<div class="unit whole">
			<div class="page-header">
				<h3><c:out value="${assessment.name}" /></h3>
			</div>

			<div class="content survey">
				<jsp:include page="/WEB-INF/views/web/partials/lms/embed.jsp"/>
			</div>
		</div>
	</div>
</div>
