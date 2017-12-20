<%@ page buffer="none" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.details" scope="request"/>
<%-- the + 0 is an easy way to avoid blank parameter --%>
<c:set var="pageScriptParams" value="${work.deliverableRequirementGroupDTO.hoursToComplete + 0}, ${work.schedule.from}, ${work.deliverableRequirementGroupDTO.deliverableRequirementDTOs.size() + 0}, ${allowMobileSignature eq true}, ${work.workNumber}" scope="request" />
<c:set var="status" value="${work.inProgress ? 'IN PROGRESS' : fn:toUpperCase(work.status.description)}" />
<c:set var="status" value="${(status eq 'SENT' and isResource) ? 'AVAILABLE' : status}" />
<c:set var="hasCompletionMsgs" value="${not empty completionFaults || not empty completionSuccesses}"/>
<c:set var="hasFaults" value="${not empty completionFaults}" scope="request"/>
<c:set var="deliverableCount" value="${work.deliverableRequirementGroupDTO.deliverableRequirementDTOs.size()}" scope="request" />
<c:set var="deliverableInstructions" value="${work.deliverableRequirementGroupDTO.instructions}" scope="request" />
<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<span class="details-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="${title}" />
		<jsp:param name="isNotPaid" value="${work.status.code ne WorkStatusType.PAID}" />
	</jsp:include>

	<div class="assignment-status">Status:
		<span class="status-name">${status}</span>
		<span class="payment-info">
			<c:choose>
				<c:when test="${work.status.code eq WorkStatusType.PAYMENT_PENDING}">
					Payment due ${wmfmt:formatMillisWithTimeZone('M/d', workResponse.workMilestones.dueOn, work.timeZone)}
				</c:when>
				<c:when test="${work.status.code eq WorkStatusType.PAID && !isInternal}">
					Paid on ${wmfmt:formatMillisWithTimeZone('M/d', workResponse.workMilestones.paidOn, work.timeZone)}
				</c:when>
			</c:choose>
		</span>
	</div>

	<div class="wrap">
	<c:if test="${not empty git && not empty git['git.commit.id.abbrev']}">
		<c:set var="cachebuster" value="?${git['git.commit.id.abbrev']}"/>
	</c:if>

	<%--sliding panel--%>
	<c:import url="/WEB-INF/views/mobile/partials/panel.jsp"/>

	<div class="content" >
	<div class="unit whole" id="public-message">
		<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
	</div><%--unit whole--%>

	<div class="assignment-title">
		<h3 class="assignment-title-name">${work.title}</h3>
		<span class="work-number">(<c:out value="${work.workNumber}"/>)</span>
	</div><%--grid assignment title--%>

	<c:if test="${not empty workResponse.workBundleParent}">
		<div class="bundle-notice">
			<p>This assignment is part of a bundle. <a href="/assignments/view_bundle/${workResponse.workBundleParent.id}">View Bundle on Full Site</a>.</p>
		</div>
	</c:if>
	<%-- Company --%>
	<div class="company-info">
		<span class="company-name"><c:out value="${work.company.name}"/></span>
		<c:if test="${not empty work.clientCompany}">
			(for <c:out value="${work.clientCompany.name}"/>)
		</c:if>
	</div><%--company-info--%>

	<c:if test="${(not empty work.subStatuses) and (isAdmin or isActiveResource) and ((work.status.code ne WorkStatusType.SENT and work.status.code ne WorkStatusType.PAID) or (not empty work.subStatuses))}" >
		<div class="whole labels-container">
			<c:import url="/WEB-INF/views/mobile/partials/assignments/details/labels.jsp"/>
		</div><%--labels-container--%>
	</c:if>

	<div class="details-wrap">
		<c:if test="${hasCompletionMsgs and (work.status.code eq WorkStatusType.ACTIVE or work.status.code eq WorkStatusType.INPROGRESS)}">
			<div class="validation-container">
				<div class="unit whole">
					<c:choose>
						<c:when test="${completionFaults.size() > 0}">
							<div class="div-notice">The following red tasks must be completed before you can complete this assignment:</div>
						</c:when>
						<c:otherwise>
							<div class="div-success">You have completed all the required tasks for this assignment. Submit this assignment for approval at the bottom of the page:</div>
						</c:otherwise>
					</c:choose>
					<ul class="completion-validation-list">
						<c:forEach items="${completionSuccesses}" var="message">
							<li><c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-green-checkmark.jsp"/><p>${message}</p></li>
						</c:forEach>
						<c:forEach items="${completionFaults}" var="message">
							<li><c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-red-x.jsp"/>${message}</li>
						</c:forEach>
					</ul>
				</div>
			</div>
		</c:if>
	</div>

	<div class="details-wrap">
		<c:choose>
			<%--Is it void or cancelled? --%>
		<c:when test="${(work.status.code == workStatusTypes['CANCELLED'] || work.status.code == workStatusTypes['VOID']  || isCancelledResource) && !isAdmin && !isInternal}">
			<div class="error">
				<p>This assignment has been cancelled or voided and is no longer available. <a href="/assignments">Back to ${assignmentsPageTitle}</a>.</p>
			</div><%--error--%>
		</c:when>

			<%-- Did I decline or miss it? --%>
		<c:when test="${(isReadOnly && workResponse.viewingResource.status.code != workResourceStatusTypes['DELEGATED']) && !isInternal}">
			<div class="error">
				<c:choose>
					<c:when test="${isDeclinedResource}">
						<p>You have declined this assignment.</p>
					</c:when>
					<c:otherwise>
						<p>This assignment has already been assigned to another worker.</p>
					</c:otherwise>
				</c:choose>
			</div><%--error--%>
		</c:when>

			<%-- Has it been delegated to someone else? --%>
		<c:when test="${isResource && workResponse.viewingResource.status.code == workResourceStatusTypes['DELEGATED']}">
			<div class="error">
				<p>This assignment has been delegated to <a href="/profile/${work.activeResource.user.userNumber}"><c:out value="${work.activeResource.user.userNumber}"/></a>.</p>
			</div><%--error--%>
		</c:when>

		<c:otherwise>
			<%-- OK - so I have a right to see this assignment, so let's show the details --%>
		<div class="details grid">
			<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-dogear-ribbon.jsp" />
			<div class="unit whole">

					<%--Time and Place--%>
				<c:import url="/WEB-INF/views/mobile/partials/assignments/details/v2/time-and-place.jsp" />

					<%--Buyer Metrics--%>
				<c:if test="${work.status.code eq WorkStatusType.SENT}">
					<c:import url="/WEB-INF/views/mobile/partials/assignments/details/buyer_metrics.jsp"/>
				</c:if>

				<c:choose>
					<c:when test="${isWorkerCompany}" >
						<c:if test="${!hidePricing}">
								<%-- Pricing --%>
								<a href="javascript:void(0);" class="show<c:if test="${work.status.code eq WorkStatusType.SENT}"> active</c:if>">
									Budget Details
									<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-dollarsign.jsp"/>
									<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
									<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
								</a>
								<div class="pricing-summary-container tell">
									<c:import url="/WEB-INF/views/mobile/partials/assignments/pricing.jsp"/>
								</div>
						</c:if>
					</c:when>
					<c:otherwise>
						<%-- Pricing --%>
						<a href="javascript:void(0);" class="show<c:if test="${work.status.code eq WorkStatusType.SENT}"> active</c:if>">
							Budget Details
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-dollarsign.jsp"/>
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
						</a>
						<div class="pricing-summary-container tell">
							<c:import url="/WEB-INF/views/mobile/partials/assignments/pricing.jsp"/>
						</div>
					</c:otherwise>
				</c:choose>

					<%-- Description --%>
				<a href="javascript:void(0);" class="show active">
					Description
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-info.jsp"/>
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
				</a>
				<div class="description-text tell"><c:out value="${work.description}" escapeXml="false"/></div>

					<%-- Instructions --%>
				<c:if test="${not empty work.instructions && (!work.privateInstructions || isAdmin || isActiveResource || isInternal)}">
					<a href="javascript:void(0);" class="show">
						Special Instructions
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-checkmark.jsp"/>
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
					</a>
					<div class="instructions tell">
						<p><c:out value="${work.instructions}" escapeXml="false"/></p>
					</div><%--tell--%>
				</c:if>

				<c:if test="${(work.status.code ne WorkStatusType.SENT) and (not empty work.locationContact or not empty work.supportContact or not empty work.buyer)}">
					<%-- Contacts --%>
					<a href="javascript:void(0);" class="show">Contacts
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-cellphone.jsp"/>
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
					</a>
					<div class="contacts tell">
						<c:if test="${not empty work.locationContact}">
							<h5>Location Contact:</h5>
							<p class="vip"><c:out value="${work.locationContact.name.firstName}" /> <c:out value="${work.locationContact.name.lastName}" /></p>
							<c:if test="${not empty work.locationContact.email}">
								<a data-ajax="false" href="mailto:<c:out value="${work.locationContact.email}"/>"><c:out value="${work.locationContact.email}"/></a>
							</c:if>

							<c:if test="${not empty work.locationContact.profile.phoneNumbers}">
								<c:forEach var="phone" items="${work.locationContact.profile.phoneNumbers}">
									<c:if test="${not empty phone.phone}">
										<a href="tel:<c:out value="${phone.phone}"/>"><c:out value="${wmfmt:phone(phone.phone)}"/></a>
										<c:if test="${phone.extension}">
											<p>x</p><c:out value="${phone.extension}"/>
										</c:if>
									</c:if>
								</c:forEach>
							</c:if>
						</c:if>

						<c:if test="${not empty work.supportContact}">
							<h5>Support Contact:</h5>
							<p class="vip"><c:out value="${work.supportContact.name.firstName}" /> <c:out value="${work.supportContact.name.lastName}" /></p>
							<c:if test="${not empty work.supportContact.email}">
								<a data-ajax="false" href="mailto:<c:out value="${work.supportContact.email}"/>"><c:out value="${work.supportContact.email}"/></a><br />
							</c:if>

							<c:if test="${not empty work.supportContact.profile.phoneNumbers}">
								<c:forEach var="phone" items="${work.supportContact.profile.phoneNumbers}">
									<c:if test="${not empty phone.phone}">
										<a href="tel:<c:out value="${phone.phone}"/>"><c:out value="${wmfmt:phone(phone.phone)}"/></a>
										<c:if test="${phone.extension}">
											<p>x</p><c:out value="${phone.extension}"/>
										</c:if>
									</c:if>
								</c:forEach>
							</c:if>
						</c:if>

						<c:if test="${not empty work.buyer}">
							<h5>Owner:</h5>
							<p class="vip"><c:out value="${work.buyer.name.firstName}" /> <c:out value="${work.buyer.name.lastName}" /></p>
							<c:if test="${not empty work.buyer.email}">
								<a data-ajax="false" href="mailto:<c:out value="${work.buyer.email}"/>"><c:out value="${work.buyer.email}"/></a><br />
							</c:if>

							<c:if test="${not empty work.buyer.profile.phoneNumbers}">
								<c:forEach var="phone" items="${work.buyer.profile.phoneNumbers}">
									<c:if test="${not empty phone.phone}">
										<a href="tel:<c:out value="${phone.phone}"/>"><c:out value="${wmfmt:phone(phone.phone)}"/></a>
										<c:if test="${phone.extension}">
											<p>x</p><c:out value="${phone.extension}"/>
										</c:if>
									</c:if>
								</c:forEach>
							</c:if>
						</c:if>
					</div><%--contacts--%>
				</c:if>

				<c:if test="${not empty work.assets}">
					<a href="/mobile/assignments/documents/${work.workNumber}" class="details-list-button show">
						<span>( ${work.assetsSize} )</span> Assignment Documents
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-orange.jsp"/>
					</a>
				</c:if>

				<a href="/mobile/assignments/notes/${work.workNumber}" class="details-list-button show active">
					Messages
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-orange.jsp"/>
					<c:if test="${work.notes != null && !work.notes.isEmpty()}">
						<span>(${work.notes.size()})</span>
					</c:if>
				</a>

				<%-- Surveys --%>
				<c:import url="/WEB-INF/views/mobile/partials/assignments/details/surveys.jsp"/>

				<%--Parts Logistics --%>
				<c:if test="${not empty work.partGroup}">
					<c:import url="/WEB-INF/views/mobile/partials/assignments/details/parts.jsp"/>
				</c:if>

			</div><%--unit-whole--%>
		</div><%--details-grid--%>
	</div><%--details--wrap--%>

	<c:import url="/WEB-INF/views/mobile/partials/assignments/details/timetracking.jsp"/>

	<%-- Mobile Completion well - custom fields, deliverables--%>
	<div class="details-wrap closeout-container">
		<div class="section-header">
			<h4>Assignment Closeout</h4>
		</div>
		<div class="details grid">
			<div class="unit whole">
				<c:if test="${isAdmin or isResource or currentUser.internal}">
					<%--Only show custom fields button if there are some custom fields to show --%>
					<c:if test="${((isAdmin or isOwner) and (hasBuyerCustomFields or hasResourceCustomFields)) or (isResource and (hasBuyerFieldsVisibleToResource or hasResourceCustomFields))}">
						<a class="default-button spin" href="/mobile/assignments/customfields/${work.workNumber}">Custom Fields</a>
					</c:if>
					<c:if test="${work.status.code eq WorkStatusType.SENT and deliverableCount > 0}">
						<%--Deliverables overview--%>
						<a href="javascript:void(0);" class="show active">
							Required Deliverables
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-checkmark.jsp"/>
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
						</a>
						<c:import url="/WEB-INF/views/mobile/partials/assignments/details/deliverables-overview.jsp"/>
					</c:if>
					<c:if test="${isActiveResource or isAdmin or currentUser.internal}">
						<c:choose>
							<c:when test="${deliverableCount > 0}">
								<a class="spin deliverables-button button" href="/mobile/assignments/deliverables/${work.workNumber}">
									<img class="deliverables-button-icon" src="${mediaPrefix}/images/live_icons/assignments/paperclip-icon.svg">
									Deliverables
								</a>
								<div class="deliverables-timing-box" style="display: none;">
									<div class="time-remaining" id="deliverableDeadlineTimer"></div>
									<div class="time-help">Time Remaining For Deliverables</div>
								</div>
							</c:when>
							<c:otherwise>
								<c:if test="${not empty deliverableInstructions}">
									<div class="checkin-notice">
										Instructions:<br/>
										<c:out escapeXml="false" value="${deliverableInstructions}"/>
									</div>
								</c:if>
								<a class="call-to-action spin deliverables-button button" href="/mobile/assignments/assets/${work.workNumber}">
									<img class="deliverables-button-icon" src="${mediaPrefix}/images/live_icons/assignments/paperclip-icon.svg">
									Attachments
								</a>
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:if>
			</div>
		</div>
	</div>

	<%-- Complete Assignment--%>
	<div class="unit whole">
		<c:if test="${isResource and (work.status.code eq WorkStatusType.ACTIVE or work.status.code eq WorkStatusType.INPROGRESS)}">
			<c:choose>
				<c:when test="${!hasFaults}">
					<a  class="complete-assignment-button spin" href="/mobile/assignments/complete/${work.workNumber}${cachebuster}" target="_self">Complete Assignment</a>
				</c:when>
				<c:otherwise>
					<a href="javascript:void(0);" class="complete-assignment-button disabled-button">Complete Assignment</a>
				</c:otherwise>
			</c:choose>
		</c:if>

		<%-- Apply / Accept / Decline / Counteroffer --%>
		<c:if test="${isResource and work.status.code eq WorkStatusType.SENT}">
			<c:import url="/WEB-INF/views/mobile/partials/assignments/details/assignment_response.jsp"/>
		</c:if>
	</div>
	</c:otherwise>
	</c:choose>
	</div><%--content--%>
	</div><%--wrap--%>
</span><%--details page--%>

<%--Timetracking Popups --%>
<c:import url="/WEB-INF/views/mobile/partials/assignments/details/timetracking-popups.jsp"/>

<%-- Add attachment popup --%>
<c:import url="/WEB-INF/views/mobile/pages/v2/assignments/add-attachment-popup.jsp" />

<%-- Add note popup --%>
<c:import url="/WEB-INF/views/mobile/pages/v2/assignments/add_note_popup.jsp"/>

<script src="//maps.google.com/maps/api/js?libraries=places" type="text/javascript"></script>
