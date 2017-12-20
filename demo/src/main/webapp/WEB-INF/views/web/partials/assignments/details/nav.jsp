<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:set var="inBundleFragment" value="to see this bundle."/>

<div class="well-b2 intro-summary">
	<div class="status">
		<c:import url="/WEB-INF/views/web/partials/assignments/details/nav_helper.jsp"/>
		<div class="assignment-status">
			<c:choose>
				<c:when test="${work.routingStrategies.get(0).getStatus().code == 'scheduled' && work.status.code == workStatusTypes['DRAFT']}">Sent</c:when>
				<c:when test="${work.inProgress}">In Progress</c:when>
				<c:when test="${work.status.code == workStatusTypes['ACTIVE']}">Assigned</c:when>
				<c:when test="${work.status.code == workStatusTypes['SENT'] && is_resource && currentUser.seller && not read_only}">Available</c:when>
				<c:when test="${work.status.code == workStatusTypes['CANCELLED_PAYMENT_PENDING']}">Cancelled - Invoiced</c:when>
				<c:when test="${work.status.code == workStatusTypes['CANCELLED_WITH_PAY']}">Cancelled and Paid</c:when>
				<c:when test="${workResponse.work.status.code == workStatusTypes['PAID'] && (work.pricing.id == pricingStrategyTypes['INTERNAL'] || work.pricing.offlinePayment)}">Finished</c:when>
				<c:otherwise>
					<c:out value="${work.status.description}"/>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div class="well-content">
		<c:if test="${not empty work.activeResource && (isAdmin || isInternal || currentUser.dispatcher)}">
			<c:import url='/WEB-INF/views/web/partials/assignments/details/worker_summary.jsp'/>
			<br/>
		</c:if>

		<c:choose>
			<c:when test="${work.status.code == workStatusTypes['SENT'] || work.status.code == workStatusTypes['DRAFT'] || work.status.code == workStatusTypes['DECLINED']}">
				<div class="wm-action-container assignments-action-container">
					<c:choose>
						<c:when test="${currentUser.seller or (not (is_in_work_company or is_internal) and not read_only)}">
							<c:choose>
								<c:when test="${not work.configuration.assignToFirstResource}">
									<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/resource_apply_notice.jsp"/>
								</c:when>
								<c:otherwise>
									<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/resource_accept_notice.jsp"/>
								</c:otherwise>
							</c:choose>
							<c:if test="${!eligibility.eligible && work.status.code == workStatusTypes['DECLINED']}">
								<div class="well-b2">
									<c:import url='/WEB-INF/views/web/partials/assignments/details/eligibility.jsp'/>
								</div>
							</c:if>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${currentUser.employeeWorker}">
									<c:if test="${workResponse.inWorkBundle}">
										<c:set var="inBundleFragment" value="to invite more workers to this bundle."/>
										<a style="pointer-events: none; cursor: default;" class="button edit-assignment" disabled>Edit Details</a>
									</c:if>
									<c:if test="${not workResponse.inWorkBundle}">
										<a style="pointer-events: none; cursor: default;" class="button js-nav-invite-more-workers" disabled>Invite More Workers</a>
										<a style="pointer-events: none; cursor: default;" class="button edit-assignment" disabled>Edit Details</a>
									</c:if>
								</c:when>
								<c:otherwise>
									<c:if test="${workResponse.inWorkBundle}">
										<c:set var="inBundleFragment" value="to invite more workers to this bundle."/>
										<a href="/assignments/edit/${work.workNumber}" class="button edit-assignment">Edit Details</a>
									</c:if>
									<c:if test="${not workResponse.inWorkBundle}">
										<a href="/assignments/contact/${work.workNumber}" class="button js-nav-invite-more-workers">Invite More Workers</a>
										<a href="/assignments/edit/${work.workNumber}" class="button edit-assignment">Edit Details</a>
									</c:if>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</div>
				<c:import url='/WEB-INF/views/web/partials/assignments/details/details.jsp'/>
			</c:when>
			<c:otherwise>

				<c:import url='/WEB-INF/views/web/partials/assignments/details/details.jsp'/>

				<c:if test="${work.status.code == workStatusTypes['COMPLETE'] }">
					<c:if test="${is_active_resource}">
						<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/resource_pending_approval.jsp"/>
					</c:if>
				</c:if>
				<c:if test="${is_admin and work.pendingPaymentFulfillment}">
					<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/payprocessing.jsp"/>
				</c:if>
				<c:if test="${is_admin and work.status.code eq workStatusTypes['CANCELLED_PAYMENT_PENDING']}">
					<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/paynowcancelled.jsp"/>
				</c:if>
				<c:if test="${work.status.code eq workStatusTypes['PAID']}">
					<h6>Assignment Resolution</h6>
					<blockquote class="wordwrap"><em><c:out value="${work.resolution}"/></em></blockquote>
				</c:if>
				<c:if test="${work.status.code != workStatusTypes['ACTIVE'] && workResponse.workMilestones != null}">
					<h6>Milestone Dates</h6>
					<ul>
						<c:choose>
							<c:when test="${workResponse.workMilestones.paidOn != 0}">
								<li>
									Paid on <c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, yyyy', workResponse.workMilestones.paidOn, work.timeZone)}"/>
									<small class="meta">
										<c:if test="${is_admin && not empty work.invoice}">
											<br/>(Invoice Number: <c:out value="${work.invoice.number}"/>)
										</c:if>
									</small>
								</li>
							</c:when>
							<c:when test="${workResponse.workMilestones.dueOn != 0 && (work.status.code == workStatusTypes['PAYMENT_PENDING'] || work.status.code == workStatusTypes['CANCELLED_PAYMENT_PENDING']) }">
								<li>
									Payment scheduled for
									<c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, yyyy', workResponse.workMilestones.dueOn, work.timeZone)}"/>
									<small class="meta">
										<c:if test="${is_admin && not empty work.invoice}">
											<br/>(Invoice Number: <c:out value="${work.invoice.number}"/>)
										</c:if>
									</small>
								</li>
							</c:when>
						</c:choose>
						<c:if test="${workResponse.workMilestones.cancelledOn != 0}">
							<li>Cancelled on
								<c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, yyyy', workResponse.workMilestones.cancelledOn, work.timeZone)}"/>
							</li>
						</c:if>
						<c:if test="${workResponse.workMilestones.closedOn != 0}">
							<li>
								Approved on
								<c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, yyyy', workResponse.workMilestones.closedOn, work.timeZone)}"/>
							</li>
						</c:if>
						<c:if test="${workResponse.workMilestones.completeOn != 0 }">
							<li>
								Completed on
								<c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, yyyy', workResponse.workMilestones.completeOn, work.timeZone)}"/>
							</li>
						</c:if>
					</ul>
				</c:if>
			</c:otherwise>
		</c:choose>
		<c:if test="${workResponse.inWorkBundle}">
			<div class="form-actions">
				This assignment is part of a bundle called: "${workResponse.workBundleParent.title}".
				You can click <a href="/assignments/view_bundle/${workResponse.workBundleParent.id}">here</a> ${inBundleFragment}
			</div>
		</c:if>
	</div>
</div>
