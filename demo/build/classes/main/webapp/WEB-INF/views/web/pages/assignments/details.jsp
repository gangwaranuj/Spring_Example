<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="${work.title}" bodyclass="assignment-details page-assignment-details" webpackScript="assignments">

	<c:set var="isInternal" value="false" scope="request"/>
	<sec:authorize access="hasRole('ROLE_INTERNAL')">
		<c:set var="isInternal" value="${!currentUser.seller}" scope="request"/>
	</sec:authorize>

	<c:set var="isAdmin" value="${!currentUser.seller && is_admin}" scope="request"/>
	<c:set var="isAdminOrInternal" value="${isInternal || isAdmin}" scope="request"/>
	<c:set var="hasLocation" value="${! work.offsiteLocation}"/>
	<c:set var="latitude" value="${work.location.address.point.latitude + 0}"/>
	<c:set var="longitude" value="${work.location.address.point.longitude + 0}"/>
	<c:set var="companyName" value="${work.company.name}" scope="request"/>
	<c:set var="companyNumber" value="${work.company.companyNumber}" scope="request"/>
	<c:set var="isPaymentLate" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_PAY_WORK_IN_DAYS'].net90Score.isBad()}" scope="request"/>
	<c:set var="hasWorkPastDue" value="${buyerScoreCard.companyStatsCard.valuesWithStringKey['PAST_DUE_MORE_THAN_3_DAYS']}" scope="request"/>
	<c:set var="pendingApprovalWorkPercentage" value="${buyerScoreCard.companyStatsCard.valuesWithStringKey['PENDING_APPROVAL_WORK_PERCENTAGE'] + 0}"  scope="request"/>
	<c:set var="pastDueWorkPercentage" value="${buyerScoreCard.companyStatsCard.valuesWithStringKey['PAST_DUE_WORK_PERCENTAGE'] + 0}" scope="request"/>
	<c:set var="rating" value="${buyerScoreCard.valuesWithStringKey['PERCENTAGE_RATINGS_OVER_4_STARS'].net90}"/>
	<c:set var="paymentTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_PAY_WORK_IN_DAYS'].net90}" />
	<c:set var="isBadRating" value="${buyerScoreCard.valuesWithStringKey['PERCENTAGE_RATINGS_OVER_4_STARS'].net90Score.isBad()}"/>
	<c:set var="offlinePaymentEnabled" value="${work.pricing.offlinePayment}" scope="request" />

	<%-- Velvet Rope for new assignment creation --%>
	<c:set var="assignmentCreationModal" value="false" />
	<vr:rope>
		<vr:venue name="ASSIGNMENTS">
			<c:set var="assignmentCreationModal" value="true" />
		</vr:venue>
	</vr:rope>

	<script>
		var config = {
			assignmentTzMillisOffset: '${wmfmt:escapeJavaScript(assignment_tz_millis_offset)}',
			workEncoded: ${work_encoded},
			authEncoded: ${authEncoded},
			workNumber: ${workNumber},
			latitude: ${latitude},
			longitude: ${longitude},
			hasLocation: ${hasLocation},
			isBuyer: ${currentUser.buyer},
			deliverablesConstants: ${deliverablesConstantsJson},
			companyName: '${wmfmt:escapeJavaScript(companyName)}',
			currentUserCompanyName: '${wmfmt:escapeJavaScript(currentUser.companyName)}',
			isPaymentLate: ${isPaymentLate},
			hasWorkPastDue: ${empty hasWorkPastDue ? false : hasWorkPastDue},
			pendingApprovalWorkPercentage: ${pendingApprovalWorkPercentage},
			pastDueWorkPercentage: ${pastDueWorkPercentage},
			rating: ${rating},
			paymentTime: ${paymentTime},
			isBadRating: ${isBadRating},
			showDocuments: ${showDocuments},
			visibilitySettings: ${visibilitySettingsJson},
			companyId: ${currentUser.companyId},
			partsConstants: ${partsConstantsJson},
			dispatcher: ${currentUser.dispatcher},
			isAdmin: ${isAdmin},
			assignToFirstResource: ${work.configuration.assignToFirstResource || isAssignToFirstToAcceptVendor},
			isNotSentOrDraft: ${!(work.status.code == workStatusTypes['DRAFT'] || work.status.code == workStatusTypes['SENT'])},
			isSuppliedByWorker: ${not empty work.partGroup && work.partGroup.isSuppliedByWorker()},
			isInternal: ${isInternal},
			showAssignButton: ${work.pricing.type == 'INTERNAL' && is_deputy},
			isSent: ${work.status.code == workStatusTypes['SENT']},
			showActions: ${not is_dispatcher && (isInternal || isAdmin) && not isInWorkBundle && (work.status.code == workStatusTypes['DRAFT'] || work.status.code == workStatusTypes['SENT'])},
			showVendorActions: ${is_dispatcher and not isInWorkBundle and (work.status.code eq workStatusTypes['SENT'] or work.status.code eq workStatusTypes['DECLINED'])},
			showBundleActions: ${isInWorkBundle and (work.status.code eq workStatusTypes['DRAFT'] or work.status.code eq workStatusTypes['SENT'] or work.status.code eq workStatusTypes['DECLINED'])},
			<c:if test="${not empty hasInvitedAtLeastOneVendor}">
				hasInvitedAtLeastOneVendor: ${hasInvitedAtLeastOneVendor},
			</c:if>
			<c:if test="${not empty workResponse.workBundleParent.title}">
				bundleTitle: "${workResponse.workBundleParent.title}",
				bundleId: "${workResponse.workBundleParent.id}",
			</c:if>
			assignmentCreationModal: ${assignmentCreationModal},
			hasScheduleConflicts: ${hasScheduleConflicts},
			offlinePaymentEnabled: ${offlinePaymentEnabled}
		}
	</script>
	<c:if test="${isAdminOrInternal and not empty recurrenceDescription}">
		<div class="alert" style="background-color: #FAFAFA; border: 1px solid #E0E0E0; width: 530px; color: #9E9E9E;">
			<div style="display: block; float: left">
				<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/recurrence-large.jsp"/>
			</div>
			<span style="display: block">
				${recurrenceDescription}
				Please click the corresponding label below to view the rest of the recurring series.
			</span>
		</div>
	</c:if>
	<c:if test="${isAdminOrInternal and resourceBlocked}">
		<div class="alert" style="background-color: #ad0303; border: 1px solid #ff0000; width: 530px; color: white;">
			<span style="display: block">
				This worker has been blocked by your company. After this assignment completes, you will not be able to route assignments to this worker without first unblocking them.
			</span>
		</div>
	</c:if>
	<c:choose>
		<c:when test="${(work.status.code == workStatusTypes['CANCELLED'] || work.status.code == workStatusTypes['VOID'] || is_cancelled_resource) && !isAdmin && !isInternal}">
			<div class="alert">
				<p>
					This assignment has been cancelled or voided and is no longer available.
					<a href="/assignments">Back to ${assignmentsPageTitle}</a>.
				</p>
			</div>
			<c:import url="/WEB-INF/views/web/partials/assignments/details/header.jsp"/>
		</c:when>

		<c:when test="${read_only && !isInternal}">
			<div class="alert">
				<p>This assignment has already been assigned to another worker.</p>
			</div>
			<c:import url="/WEB-INF/views/web/partials/assignments/details/header.jsp"/>
		</c:when>

		<c:otherwise>
			<c:if test="${is_declined_resource}">
				<div class="alert">
					<p>You previously declined this assignment.</p>
				</div>
			</c:if>
			<c:import url="/WEB-INF/views/web/partials/message.jsp"/>

			<c:if test="${!is_declined_resource && (is_active_resource || isAdminOrInternal || work.status.code == workStatusTypes['SENT'] || (currentUser.dispatcher && work.status.code == workStatusTypes['DECLINED']))}">
				<c:import url="/WEB-INF/views/web/partials/assignments/details/header.jsp"/>

				<div class="row_details_assignment">
					<c:if test="${hasLocation}">
						<div id="map-canvas" class="worker-map"></div>
					</c:if>
					<div class="content">

						<c:import url='/WEB-INF/views/web/partials/assignments/details/callouts.jsp'/>

						<ul class="wm-tabs">
							<li class="wm-tab -active" data-content="#overview">Assignment</li>
							<li class="wm-tab" data-content="#messaging" id="messaging_tab">Messages</li>
							<c:if test="${(isAdminOrInternal) && !currentUser.dispatcher}">
								<li class="wm-tab" data-content="#history" id="activity-tab">Activity</li>
								<li class="wm-tab" data-content="#workers" class="intro-workers-tab">Talent</li>
								<c:if test="${hasLocation}">
									<li id="workers-map-link" class="wm-tab" data-content="#workers-map"><wm:branding work="Work" name="Map" /></li>
								</c:if>
							</c:if>
							<c:if test="${currentUser.dispatcher && (work.status.code == workStatusTypes['SENT'] || work.status.code == workStatusTypes['DECLINED'])}">
								<li class="wm-tab" data-content="#workers">Candidates</li>
							</c:if>
						</ul>

						<div id="overview" class="wm-tab--content -active">
							<c:if test="${isAdmin and work.status.code == workStatusTypes['COMPLETE']}">
								<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/buyer_pay.jsp"/>
							</c:if>

							<c:if test="${showTaxAlert}">
								<c:import url='/WEB-INF/views/web/partials/assignments/details/tax_reminder.jsp'/>
							</c:if>

							<c:if test="${is_active_resource && (work.status.code == workStatusTypes['INPROGRESS'] || work.status.code == workStatusTypes['ACTIVE'])}">
								<c:import url='/WEB-INF/views/web/partials/assignments/details/completion_bar.jsp'/>
							</c:if>

							<c:import url='/WEB-INF/views/web/partials/assignments/details/description.jsp'/>

							<c:import url='/WEB-INF/views/web/partials/assignments/details/completion.jsp'/>
						</div>

						<div id="messaging" class="assignment-messages wm-tab--content"></div>

						<c:if test="${isAdminOrInternal}">
							<div id="history" class="wm-tab--content"></div>
						</c:if>

						<c:if test="${isAdminOrInternal || (currentUser.dispatcher && (work.status.code == workStatusTypes['SENT'] || work.status.code == workStatusTypes['DECLINED']))}">
							<div id="workers" class="wm-tab--content assignment-workers">
								<div id="workers-bucket"></div>
								<div id="vendors-bucket"></div>
							</div>
						</c:if>

						<c:if test="${isAdminOrInternal}">
							<div id="workers-map" class="clear wm-tab--content">
								<div class="workers-count"></div>
								<wm:spinner />
								<div class="no-worker dn">There are 0 workers.</div>
								<table class="group-list workers-map">
									<tbody class="worker-scroll"></tbody>
								</table>
							</div>
						</c:if>

						<c:if test="${is_resource && not read_only && work.status.code == workStatusTypes['SENT']}">
							<c:if test="${workResponse.viewingResource.pendingNegotiation.approvalStatus.code != 'pending' || workResponse.viewingResource.pendingNegotiation.isExpired}">
								<div class="well-b2">
									<div class="well-content">
										<c:out value="${negotiation.approvalStatus.code}"/>
										<c:choose>
											<c:when test="${workResponse.inWorkBundle}">
												<c:import url='/WEB-INF/views/web/partials/assignments/details/in-bundle.jsp'/>
											</c:when>
											<c:otherwise>
												<c:import url='/WEB-INF/views/web/partials/assignments/details/compliance.jsp'/>

												<c:choose>
													<c:when test="${isWorkerCompany}" >
														<sec:authorize access="!hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
															<c:import url='/WEB-INF/views/web/partials/assignments/details/callouts/resource_accept_or_apply.jsp'>
																<c:param name="isAcceptableOrApplyable" value="${isAcceptableOrApplyable}"/>
															</c:import>
														</sec:authorize>
														<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
															<%-- Accept/Apply not visible when has one of ACL_ADMIN/ACL_MANAGER/ACL_DISPATCHER and currentUser.dispatcher? --%>
															<c:if test="${not currentUser.dispatcher}">
																<c:import url='/WEB-INF/views/web/partials/assignments/details/callouts/resource_accept_or_apply.jsp'>
																	<c:param name="isAcceptableOrApplyable" value="${isAcceptableOrApplyable}"/>
																</c:import>
															</c:if>
														</sec:authorize>
													</c:when>
													<c:otherwise>
														<c:import url='/WEB-INF/views/web/partials/assignments/details/callouts/resource_accept_or_apply.jsp'>
															<c:param name="isAcceptableOrApplyable" value="${isAcceptableOrApplyable}"/>
														</c:import>
													</c:otherwise>
												</c:choose>

											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</c:if>
						</c:if>
					</div>

					<div class="sidebar worker-map">
						<c:if test="${work.status.code == workStatusTypes['SENT']}">
							<c:import url='/WEB-INF/views/web/partials/assignments/details/buyer_scorecard.jsp'/>
						</c:if>

						<c:import url='/WEB-INF/views/web/partials/assignments/details/nav.jsp'/>

						<c:if test="${isAdmin and empty work.activeResource and not empty work.questionAnswerPairs}">
							<div>
								<c:forEach var="qa" items="${work.questionAnswerPairs}">
									<c:if test="${empty qa.answer}">
										<c:set var="qa" value="${qa}" scope="request"/>
										<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/buyer_qa_answer.jsp"/>
										<c:remove var="qa" scope="request"/>
									</c:if>
								</c:forEach>
								<c:set var="qa" value="" scope="request"/>
							</div>
						</c:if>

						<c:import url="/WEB-INF/views/web/partials/assignments/details/completion_list.jsp"/>

						<c:if test="${is_active_resource || (work.status.code == workStatusTypes['SENT'] && !(isAdmin || is_owner))}">
							<c:import url='/WEB-INF/views/web/partials/assignments/details/news.jsp'/>
						</c:if>

						<c:if test="${is_active_resource || isAdmin}">
							<c:import url='/WEB-INF/views/web/partials/assignments/details/contacts.jsp'/>
						</c:if>

						<c:if test="${isAdminOrInternal}">
							<c:import url='/WEB-INF/views/web/partials/assignments/details/followers.jsp'/>
						</c:if>

					</div>
				</div>
			</c:if>
		</c:otherwise>
	</c:choose>


</wm:app>
