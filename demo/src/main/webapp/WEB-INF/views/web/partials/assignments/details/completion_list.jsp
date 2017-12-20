<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isConfirm" value="${work.resourceConfirmationRequired && (is_active_resource || is_admin)}" />
<c:set var="confirmComplete" value="${work.activeResource.confirmed}" />
<c:set var="checkinComplete" value="${empty work.activeResource.timeTrackingLog}" />
<c:set var="isAssessment" value="${not empty work.assessments}" />
<c:set var="assignmentComplete" value="${workResponse.work.status.code == workStatusTypes['COMPLETE'] or workResponse.work.status.code == workStatusTypes['PAYMENT_PENDING'] or workResponse.work.status.code == workStatusTypes['INVOICED'] or workResponse.work.status.code == workStatusTypes['PAID']}" />
<c:set var="assignmentApproved" value="${workResponse.work.status.code == workStatusTypes['PAYMENT_PENDING'] or workResponse.work.status.code == workStatusTypes['INVOICED'] or workResponse.work.status.code == workStatusTypes['PAID']}" />
<c:set var="assignmentPaid" value="${workResponse.work.status.code == workStatusTypes['PAID'] or workResponse.work.status.code == workStatusTypes['CANCELLED_WITH_PAY']}" />
<c:set var="internalPricing" value="${work.pricing.id == pricingStrategyTypes['INTERNAL']}" />

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

<c:set var="closeoutRequirementsSet" value="false"/>

<c:set var="closeoutRequirementsSet" value="${not empty work.deliverableRequirementGroupDTO and not empty work.deliverableRequirementGroupDTO.deliverableRequirementDTOs}"/>

<c:if test="${!(work.status.code == workStatusTypes['DRAFT'] || work.status.code == workStatusTypes['SENT'])}">
<div class="well-b2">
	<h3>Assignment Checklist</h3>
	<div class="well-content">
		<table id="completion_table">
			<tbody>
				<tr class="completion-success">
					<td width="90%">Assigned</td>
					<td width="10%">
						<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/>
					</td>
				</tr>

				<c:if test="${isConfirm}">
					<tr class="<c:if test="${confirmComplete}">completion-success</c:if> required-item">
						<td width="90%">Confirmed Schedule</td>
						<c:choose>
							<c:when test="${confirmComplete}">
								<td width="10%">
									<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/>
								</td>
							</c:when>
							<c:otherwise>
								<td width="10%">
									<i class="muted icon-minus icon-large"></i>
								</td>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:if>

				<c:if test="${work.configuration.checkinRequiredFlag || work.checkinCallRequired}">
					<tr id="checkin_completion_list" class="required-item">
						<td width="90%">Check-In/Out
							<c:if test="${work.checkinCallRequired}">
								<small> (via phone)</small>
							</c:if>
						</td>
						<td width="10%" id="checkout_list_incomplete"><i id="checkin_task_status" class="icon-minus muted icon-large"></i></td>
						<td width="10%" id="checkout_list_completed" class="dn"><jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/></td>
					</tr>
				</c:if>

				<c:if test="${hasResourceFields}">
					<tr id="cf_completion_list" class="required-item">
						<td width="90%">Required Custom Fields</td>
						<td width="10%" id="custom_fields_list_incomplete"><i class="icon-large icon-minus muted"></i></td>
						<td width="10%" id="custom_fields_list_complete" class="dn"><jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/></td>
					</tr>
				</c:if>

				<c:if test="${isAssessment}">
					<c:forEach var="assessment" items="${work.assessments}">
						<c:if test="${assessment.isRequired}">
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
							<tr class="<c:if test="${assessmentTaken}">completion-success</c:if> required-item">
								<td width="90%">Survey
									<small>(<c:out value="${assessment.name}"/>)</small>
								</td>
								<c:choose>
									<c:when test="${assessmentTaken}">
										<td width="10%">
											<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/>
										</td>
									</c:when>
									<c:otherwise>
										<td width="10%"><i class="muted icon-minus icon-large"></i></td>
									</c:otherwise>
								</c:choose>
							</tr>
						</c:if>
					</c:forEach>
				</c:if>
				<c:if test="${closeoutRequirementsSet}">
					<tr id="deliverables_completion_list" class="required-item">
						<td width="90%">Deliverables</td>
						<td width="10%" id="deliverables_task_incomplete"><i class="muted icon-minus icon-large"></i></td>
						<td width="10%" id="deliverables_task_complete" class="dn"><jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/></td>
					</tr>
				</c:if>
				<c:if test="${not internalPricing}">
					<tr class="<c:if test="${assignmentComplete}">completion-success</c:if>">
						<td width="90%">Assignment Complete</td>
						<c:choose>
							<c:when test="${assignmentComplete}">
								<td width="10%">
									<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/>
								</td>
							</c:when>
							<c:otherwise>
								<td width="10%"><i class="muted icon-minus icon-large"></i></td>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:if>

				<c:if test="${not internalPricing}">
					<tr class="<c:if test="${assignmentApproved}">completion-success</c:if>">
						<td width="90%">Assignment Approved</td>
						<c:choose>
							<c:when test="${assignmentApproved}">
								<td width="10%">
									<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/>
								</td>
							</c:when>
							<c:otherwise>
								<td width="10%"><i class="muted icon-minus icon-large"></i></td>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:if>

				<tr class="<c:if test="${assignmentPaid}">completion-success</c:if>">
					<td width="90%">
						<c:choose>
							<c:when test="${not internalPricing}">
								Assignment Paid
							</c:when>
							<c:otherwise>
								Assignment Finished
							</c:otherwise>
						</c:choose>
					</td>
					<c:choose>
						<c:when test="${assignmentPaid}">
							<td width="10%">
								<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/>
							</td>
						</c:when>
						<c:otherwise>
							<td width="10%"><i class="muted icon-minus icon-large"></i></td>
						</c:otherwise>
					</c:choose>
				</tr>

			</tbody>
		</table>

		<c:if test="${(isAdmin || isInternal) && workResponse.work.status.code == workStatusTypes['ACTIVE']}">
			<ul class="media-list">
				<li>
					<i class="wm-icon-envelope icon-2x icon-gray pull-left"></i>
					<div id="completion-reminder">
						<small>
						<a href="/assignments/${work.workNumber}/send_reminder_to_complete" class="resend_invite alert-message-btn tooltipped tooltipped-n" aria-label="Send an email to your worker to remind him to closeout this assignment.">
							Remind <c:out value="${wmfmt:toPrettyName(work.activeResource.user.name.firstName)}"/>
							<c:out value="${wmfmt:toPrettyName(work.activeResource.user.name.lastName)}"/>
						</a>to close out the assignment
						</small>
						<c:if test="${active_resource.setLastRemindedToCompleteOn}">
							<small><br/>(Last sent <c:out value="${wmfmt:fuzzySpanMillis(active_resource.lastRemindedToCompleteOn)}"/>)</small>
						</c:if>
					</div>
				</li>
			</ul>
		</c:if>
	</div>
</div>
</c:if>
