<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="timeZone" value="${work.timeZone}"/>
<c:if test="${! work.offsiteLocation and not empty work.location}">
	<input id="addressLat" type="hidden" class="addressLat" value="${work.location.address.point.latitude}"/>
	<input id="addressLon" type="hidden" class="addressLon" value="${work.location.address.point.longitude}"/>
</c:if>
<c:choose>
	<%--If confirmation is still needed, that's gotta happen before check in / out--%>
	<c:when test="${work.resourceConfirmationRequired && !work.activeResource.confirmed && (isActiveResource || isAdmin)}">
		<div class="unit whole timetracking-container">
			<h4>Confirm Assignment</h4>

			<form id="confirmation-form" action="/mobile/assignments/confirmation/${work.workNumber}"
				  data-ajax="false" method="POST">
				<wm-csrf:csrfToken/>

				<input type="hidden" name='id' value="${work.workNumber}">
				<div class="div-notice">
					<c:choose>
						<c:when test="${isActiveResource}">
							<c:choose>
								<c:when test="${work.confirmable}">
									Please confirm this assignment prior to
									<strong>${wmfmt:formatCalendarWithTimeZone("EEE',' MMM d 'at' h:mm aa z", work.confirmByDate, timeZone)}.</strong>
								</c:when>
								<c:when test="${not work.confirmable}">
									It's too early to confirm this assignment. <strong>You will be able to confirm
									between ${wmfmt:formatCalendarWithTimeZone("EEE',' MMM d 'at' h:mm aa", work.confirmableDate, timeZone)}</strong> and
									<strong>${wmfmt:formatMillisWithTimeZone("EEE',' MMM d 'at' h:mm aa z", work.schedule.from, timeZone)}.</strong>
								</c:when>
								<c:otherwise>
									You cannot confirm this assignment.
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:when test="${isAdmin}">
							Confirm your worker is available to perform work.<strong>
							This assignment is set for confirmation
							<fmt:formatNumber
									value="${work.resourceConfirmationHours}"/> ${wmfmt:pluralize("hour", work.resourceConfirmationHours)}</strong> prior to the assignment start time.
						</c:when>
					</c:choose>
				</div>
				<div>
					<c:if test="${isActiveResource or isAdmin}">
						<button type="submit" class="default-button"
								<c:if test="${not work.confirmable}">disabled="true"</c:if> data-ajax="false">
							Confirm
						</button>
					</c:if>
				</div>
			</form>
		</div>
		<%--timetracking thing--%>
	</c:when>
	<c:when test="${(isAdmin or isActiveResource) and (work.status.code eq WorkStatusType.ACTIVE)}">
		<div class="unit whole timetracking-container">
			<div class="timetracking-header">
				<h4>Check In / Out</h4>
				<%--commenting out until Josh gives us new images per his request--%>
				<%--<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-stopwatch.jsp"/>--%>
			</div>
			<ul id="time-tracking">
				<c:if test="${work.status.code eq WorkStatusType.ACTIVE and isActiveResource}">
					<%--If check in by phone, provide that info --%>
					<c:if test="${work.checkinCallRequired}">
						<li>
							When checking in / out for this assignment, please call <c:out
								value="${work.checkinContactName}"/> at <c:out value="${work.checkinContactPhone}"/>.
						</li>
					</c:if>
					<%--
						If checkin call is not required or BOTH checkin call required and checkin required is selected
						then we we display the check in buttons to the user. This is intentional.
					--%>
					<c:if test="${!work.checkinCallRequired || work.configuration.checkinRequiredFlag}">
						<li>
							<c:choose>
								<%-- If worker is currently checked in, show checkout --%>
								<c:when test="${currentlyCheckedIn}">
									<c:if test="${! work.offsiteLocation and not empty work.location}">
										<p class="distance-text">(current distance: <span class="distance">?</span>)</p>
									</c:if>
									<a class="checkout-button popup-open" data-popup-selector="#checkout-popup"
									   href="javascript:void(0);">Check Out</a>
								</c:when>
								<c:otherwise>
									<div class="checkin-notice">Only check in for this assignment if you are starting work
										or resuming work.
									</div>
									<c:if test="${! work.offsiteLocation and not empty work.location}">
										<p class="distance-text">(current distance:<span class="distance">?</span>)</p>
									</c:if>
									<form class="check-in-form" action="/mobile/assignments/checkin/${work.workNumber}" method="POST">
										<wm-csrf:csrfToken/>
										<input type="hidden" id="latitudeField" name="latitude"
											   class="latitudeField" value="">
										<input type="hidden" id="longitudeField" name="longitude"
											   class="longitudeField" value="">
										<input type="hidden" id="distanceField" name="distance"
											   class="distanceField" value="">

										<button type="submit" class="checkin-button spin">
											Check In<c:if test="${work.activeResource.checkedIn}"> Again</c:if>
										</button>
									</form>
								</c:otherwise>
							</c:choose>
						</li>
					</c:if>
				</c:if>
				<%-- Show time tracking log --%>
				<c:if test="${not empty work.activeResource.timeTrackingLog}">
					<div class="time-tracking-log">
						<c:import url="/WEB-INF/views/mobile/partials/assignments/details/timetracking-log.jsp"/>
					</div>
				</c:if>
			</ul>
		</div>
		<%--timetracking module--%>
	</c:when>
</c:choose>
