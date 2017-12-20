<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<div id="deliverables">
	<div class="media completion">
		<div class="completion-icon">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/deliverables_v2.jsp"/>
		</div>
		<div class="media-body">
			<h4>
				Deliverables
				<a title="Download All" class="deliverables-download-all-icon -hidden" href="/assignments/download_deliverable_assets/${work.workNumber}">
					<jsp:include page="/WEB-INF/views/web/partials/svg-icons/deliverables/icon-download-all.jsp"/>
				</a>
			</h4>
			<c:if test="${not empty work.deliverableRequirementGroupDTO}">
				<c:choose>
					<c:when test="${not empty work.deliverableRequirementGroupDTO.instructions}">
						<h5>Instructions from ${work.company.name}:</h5>
						<div class="deliverables-instructions">
							<c:choose>
								<c:when test="${not is_autotask}">
									<c:out escapeXml="false" value="${work.deliverableRequirementGroupDTO.instructions}"/>
								</c:when>
								<c:otherwise>
									<c:out escapeXml="false" value="${wmfmt:tidy(wmfmt:nl2br(work.deliverableRequirementGroupDTO.instructions))}"/>
								</c:otherwise>
							</c:choose>
						</div>
					</c:when>
					<c:otherwise>
						<h5>No deliverables instructions provided.</h5>
					</c:otherwise>
				</c:choose>
				<c:if test="${work.deliverableRequirementGroupDTO.hoursToComplete > 0}">
					<c:choose>
						<c:when test="${work.status.code == workStatusTypes['ACTIVE'] or work.status.code == workStatusTypes['COMPLETE']}">
							<p class="deliverables-timer-text">Please submit all the required deliverables within <span id="deliverableDeadlineTimer"></span>.</p>
						</c:when>
						<c:otherwise>
							<p>Deliverables are due within <span class="hours-to-complete">${work.deliverableRequirementGroupDTO.hoursToComplete} hours</span>  of assignment ${work.schedule.range ? "end" : "start"} time.</p>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:if>

			<c:if test="${(work.status.code == workStatusTypes['DRAFT'] || work.status.code == workStatusTypes['SENT'])}">
				<c:if test="${not empty work.deliverableRequirementGroupDTO}">
					<h5>Requirements for this assignment:</h5>
					<c:choose>
						<c:when test="${not empty work.deliverableRequirementGroupDTO.deliverableRequirementDTOs}">
							<table class="deliverableRequirementTable">
								<thead>
									<tr>
										<th>Type</th>
										<th>Number Of Files</th>
										<th>Instructions</th>
									</tr>
								</thead>
								<tbody>
								<c:forEach var="deliverableRequirementDTO" items="${work.deliverableRequirementGroupDTO.deliverableRequirementDTOs}">
									<tr>
										<td>
											<c:out value="${wmfn:translateDeliverableTypeToName(deliverableRequirementDTO.type)}"/>
										</td>
										<td>
											<c:out value="${deliverableRequirementDTO.numberOfFiles}"/>
										</td>
										<td>
											<c:out value="${deliverableRequirementDTO.instructions}"/>
										</td>
									</tr>
								</c:forEach>
								</tbody>
							</table>
						</c:when>
						<c:otherwise>
							<p>No requirements set.</p>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:if>

			<c:if test="${(is_owner or is_admin) and not empty work.activeResource.assessmentAttempts}">
					<h5>Survey</h5>
					<ul class="media-list">
						<c:forEach var="survey" items="${work.activeResource.assessmentAttempts}">
							<li class="media">
								<i class="media-object"></i>

								<div class="media-body">
									<c:out value="${survey.assessment.name}"/>
									<a href="<c:url value="/lms/grade/${survey.assessment.id}/${survey.latestAttempt.id}"/>"
									   class="view_survey_results tooltipped tooltipped-n"
									   aria-label="View <c:out value="${work.activeResource.user.name.firstName} ${work.activeResource.user.name.lastName}"/>'s survey responses">
										<i class="wm-icon-download icon-large muted"></i>
									</a>
									<c:if test="${survey.assessment.hasAssetItems}">
										<a href="<c:url value="/lms/manage/assets/${survey.assessment.id}/${survey.latestAttempt.id}"/>" class="tooltipped tooltipped-n" aria-label="View and download survey photos and attachments">
											<i class="wm-icon-download icon-large muted"></i>
										</a>
									</c:if>
								</div>
							</li>
						</c:forEach>
					</ul>
				</c:if>
			<div class="documents"></div>
		</div>


		<script type="text/html" id="carouselArrowsTemplate">
			<i class="wm-icon-left-arrow"></i>
			<i class="wm-icon-right-arrow"></i>
		</script>

	</div>
</div>
