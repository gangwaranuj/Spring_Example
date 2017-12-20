<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<c:if test="${not empty work.activeResource && not empty work.assessments && isActiveResource}">
	<a href="javascript:void(0);" class="show active">Surveys
		<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-survey.jsp"/>
		<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
		<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
	</a>
	<ul class="tell">
	<c:forEach var="assessment" items="${work.assessments}">
		<c:set var="assessmentTaken" value="${false}"/>
		<c:if test="${not empty workResponse.work.activeResource.assessmentAttempts}">
			<c:forEach var="attempt" items="${workResponse.work.activeResource.assessmentAttempts}">
				<c:if test="${attempt.assessment.id eq assessment.id and attempt.latestAttempt.status.code eq 'complete'}">
					<c:set var="assessmentTaken" value="${true}"/>
				</c:if>
			</c:forEach>
		</c:if>
		<li class="survey-item">
			<span><c:out value="${assessment.name}"/></span>
			<c:choose>
				<c:when test="${assessmentTaken}">
					<img src="${mediaPrefix}/images/icons/tick.png"/>
				</c:when>
				<c:otherwise>
					<c:if test="${assessment.isRequired}"><span class="required">(required)</span></c:if>
					<a class="take-survey" href="/mobile/surveys/take/${assessment.id}?assignment=${work.id}">Take</a>
				</c:otherwise>
			</c:choose>
		</li>
	</c:forEach>
	</ul>
</c:if>
