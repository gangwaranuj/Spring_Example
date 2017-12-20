<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<c:if test="${not empty workResponse.work.assessments}">
	<c:set var="assessmentRequirements">
		<table>
			<tbody>
			<c:forEach var="assessment" items="${work.assessments}">
				<c:set var="assessmentTaken" value="${false}"/>
				<c:if test="${not empty workResponse.work.activeResource.assessmentAttempts}">
					<c:forEach var="attempt" items="${workResponse.work.activeResource.assessmentAttempts}">
						<c:if test="${attempt.assessment.id eq assessment.id and attempt.latestAttempt.status.code eq 'complete'}">
							<c:set var="assessmentTaken" value="${true}"/>
							<c:set var="assessmentAttemptID" value="${attempt.latestAttempt.id}"/>
						</c:if>
					</c:forEach>
				</c:if>
				<c:if test="${!is_active_resource and not empty attempt_on_behalf}">
					<c:forEach var="attemptOnBehalf" items="${attempt_on_behalf}">
						<c:if test="${attemptOnBehalf eq assessment.id}">
							<c:set var="assessmentTaken" value="${true}"/>
						</c:if>
					</c:forEach>
				</c:if>

				<tr>
					<td>
						<strong>
							<c:out value="${assessment.name}"/> <c:if test="${assessment.isRequired}"> <small class="meta">(required)</small></c:if>
						</strong>
					</td>
					<td>
						<c:choose>
							<c:when test="${assessmentTaken}">
								<c:if test="${is_admin}">
									<a class="button pull-right" href="/lms/grade/${assessment.id}/${assessmentAttemptID}">View Results</a>
								</c:if>
							</c:when>
							<c:when test="${!assessmentTaken and is_admin and not empty work.activeResource.user}">
								<a class="button pull-right completion_button" href="/lms/view/take/${assessment.id}?assignment=${work.id}&onBehalfOf=${work.activeResource.user.userNumber}">Take For Worker</a>
								<a href="/lms/print/${assessment.id}<c:if test='${not empty work}'>?assignment=${work.workNumber}</c:if>" class="button pull-right">Print</a>
							</c:when>
							<c:when test="${(is_admin or is_internal) and (work.status.code eq workStatusTypes['SENT'] or work.status.code eq workStatusTypes['DRAFT'])}">
								<a href="/lms/print/${assessment.id}<c:if test='${not empty work}'>?assignment=${work.workNumber}</c:if>" class="button">Print</a>
							</c:when>
							<c:otherwise>
								<a class="button pull-right" href="/lms/view/take/${assessment.id}?assignment=${work.id}">Take Survey</a>
								<a href="/lms/print/${assessment.id}<c:if test='${not empty work}'>?assignment=${work.workNumber}</c:if>" class="button pull-right">Print</a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:set>
</c:if>


<c:if test="${not empty workResponse.work.assessments}">
	<div class="media completion">
		<img src="${mediaPrefix}/images/live_icons/assignments/survey.svg"/>
		<div class="media-body">
			<h4>
				Survey
				<c:choose>
					<c:when test="${assessmentTaken}">
						<div class="fr completed">
							<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/>
							<small class="meta">completed</small>
						</div>
					</c:when>
					<c:when test="${hasRequiredAssessments}">
						<small class="meta incomplete"><span class="label label-important">Required</span></small>
					</c:when>
				</c:choose>
			</h4>
			<c:choose>
				<c:when test="${(is_admin || is_internal) && hasRequiredAssessments}">
					<p>Worker must complete before closing out the assignment</p>
				</c:when>
				<c:otherwise>
					<c:if test="${not assessmentTaken}">
						<p>Please complete before closing out the assignment</p>
					</c:if>
				</c:otherwise>
			</c:choose>

			<c:out value="${assessmentRequirements}" escapeXml="false"/>
		</div>
	</div>
</c:if>
