<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<div id="panel-background"></div>
<%--panel-background--%>

<div id="wm-panel-page">
	<div class="panel-content">
		<ul>
			<%-- Confirm/Check-in/Check-out options--%>
			<c:if test="${(work.status.code eq WorkStatusType.ACTIVE or work.status.code eq WorkStatusType.INPROGRESS)}">
				<c:choose>
					<c:when test="${work.resourceConfirmationRequired && !work.activeResource.confirmed && (isActiveResource || isAdmin)}">
						<li><a class="popup-open" data-popup-selector="#confirm-popup" href="javascript:void(0);">Confirm</a>
						</li>
					</c:when>
					<c:when test="${isActiveResource}">
						<c:choose>
							<c:when test="${currentlyCheckedIn}">
								<li><a class="popup-open" data-popup-selector="#checkout-popup"
								       href="javascript:void(0);">Check Out</a></li>
							</c:when>
							<c:otherwise>
								<li><a class="popup-open" data-popup-selector="#checkin-popup"
								       href="javascript:void(0);">Check In</a></li>
							</c:otherwise>
						</c:choose>
					</c:when>
				</c:choose>
			</c:if>

			<c:if test="${isActiveResource and (work.status.code eq WorkStatusType.ACTIVE or work.status.code eq WorkStatusType.INPROGRESS)}">
				<li><a href="/mobile/assignments/generate_pdf/${work.workNumber}" target="_blank" id="print-assignment">Print Assignment</a></li>
					<c:if test="${allowMobileSignature}">
						<c:choose>
							<c:when test="${deliverableCount > 0}">
								<li><a href="/mobile/assignments/deliverables/${work.workNumber}">Get Signature</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="/mobile/assignments/signature/${work.workNumber}">Get Signature</a></li>
							</c:otherwise>
						</c:choose>
					</c:if>
				<c:if test="${!hasFaults and (work.status.code eq WorkStatusType.ACTIVE or work.status.code eq WorkStatusType.INPROGRESS)}">
					<li><a href="/mobile/assignments/complete/${work.workNumber}${cachebuster}" target="_self"
					       class="spin">Complete Assignment</a></li>
				</c:if>

				<li><a href="/mobile/assignments/part_details/${work.workNumber}">Parts and Logistics</a></li>
			</c:if>

			<c:if test="${work.status.code ne WorkStatusType.SENT and work.status.code ne WorkStatusType.PAID and (isActiveResource)}">
				<li><a href="javascript:void(0);" id="add-note" class="popup-open"
				       data-popup-selector="#add-note-popup">Add Message</a></li>
				<c:choose>
					<c:when test="${deliverableCount > 0}">
						<li><a href="/mobile/assignments/deliverables/${work.workNumber}">Add Deliverables</a></li>
					</c:when>
					<c:otherwise>
						<li><a href="javascript:void(0);" id="add-attachment" class="popup-open"
						       data-popup-selector="#add-attachment-popup">Add Attachment</a></li>
					</c:otherwise>
				</c:choose>
				<li><a href="/mobile/assignments/add_label/${work.workNumber}">Add Label</a></li>
			</c:if>
			<c:if test="${(work.status.code eq WorkStatusType.ACTIVE or work.status.code eq WorkStatusType.INPROGRESS) and (isActiveResource)}">
				<li><a href="/mobile/assignments/reschedule/${work.workNumber}" id="reschedule">Request Reschedule</a>
				</li>
				<c:if test="${not is_employee}">
					<c:if test="${isActiveResource && not work.configuration.disablePriceNegotiation}">

						<c:choose>
							<c:when test="${isWorkerCompany}" >
								<c:if test="${!hidePricing}">
									<li><a href="/mobile/assignments/budgetincrease/${work.workNumber}" class="spin">Request Budget Increase</a></li>
									<li><a href="/mobile/assignments/reimbursement/${work.workNumber}" class="spin">Request Reimbursement</a></li>
									<li><a href="/mobile/assignments/bonus/${work.workNumber}" class="spin">Request Bonus</a></li>
								</c:if>
							</c:when>
							<c:otherwise>
								<li><a href="/mobile/assignments/budgetincrease/${work.workNumber}" class="spin">Request Budget Increase</a></li>
								<li><a href="/mobile/assignments/reimbursement/${work.workNumber}" class="spin">Request Reimbursement</a></li>
								<li><a href="/mobile/assignments/bonus/${work.workNumber}" class="spin">Request Bonus</a></li>
							</c:otherwise>
						</c:choose>

					</c:if>
				</c:if>
			</c:if>
			<%-- Apply / Accept / Decline / Counteroffer --%>
			<c:if test="${isResource and work.status.code eq WorkStatusType.SENT and empty workResponse.workBundleParent}">
					<c:choose>
						<c:when test="${eligibility.eligible}">
							<c:choose>
								<c:when test="${not work.configuration.assignToFirstResource}">
									<c:set var="doWorkUri" value="/mobile/assignments/apply/${work.workNumber}"/>
									<c:set var="doWorkTitle" value="Apply"/>
								</c:when>
								<c:otherwise>
									<c:set var="doWorkUri" value="/mobile/assignments/accept/${work.workNumber}"/>
									<c:set var="doWorkTitle" value="Accept"/>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${work.configuration.assignToFirstResource}">
									<li><a href="<c:out value="${doWorkUri}" />" class="accept-action spin"><c:out
											value="${doWorkTitle}"/></a></li>
									<li><a href="/mobile/assignments/negotiate/${work.workNumber}" class="spin">Counteroffer</a>
									</li>
									<li><a href="/mobile/assignments/reject/${work.workNumber}"
									       class="decline-action spin">Decline</a></li>
								</c:when>
								<c:otherwise>
									<li><a href="<c:out value="${doWorkUri}" />" class="spin">${doWorkTitle}</a>
									</li>
									<li><a href="/mobile/assignments/reject/${work.workNumber}"
									       class="decline-action spin">Decline</a></li>
								</c:otherwise>
							</c:choose>
						</c:when>
					</c:choose>
			</c:if>

			<c:if test="${not empty workResponse.workBundleParent}">
				<li><a href="/assignments/view_bundle/${workResponse.workBundleParent.id}">View Bundle on Full Site</a></li>
			</c:if>
			<li><a href="/assignments/details/${work.workNumber}?site_preference=normal">View on Full Site</a></li>
		</ul>
		<hr>
		<div class="unit whole bottom-section">
			<div class="whole unit">
				<div class="one-third">
					<a class="spin" href="javascript:history.go(-1);">Back</a>
				</div>
				<div class="one-third">
					<a class="spin" href="/mobile" class="spin">Home</a>
				</div>
				<div class="one-third">
					<a class="spin" href="/mobile/help" class="spin">Help</a>
				</div>
			</div>
			<div class="one-half">
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-signout.jsp"/>
				<a class="spin" href="/logout" id="logout">Sign Out</a>
			</div>
		</div>
		<%--bottom section whole--%>
	</div>
	<%--panel content--%>
	<div class="rocket-container">
		<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-rocket.jsp"/>
		<small>Designed and Engineered in NYC</small>
	</div>
	<%--rocket container--%>
</div>
<%--panel page--%>
