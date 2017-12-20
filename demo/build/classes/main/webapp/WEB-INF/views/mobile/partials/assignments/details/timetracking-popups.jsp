<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="timeZone" value="${work.timeZone}" />
<c:if test="${! work.offsiteLocation and not empty work.location}" >
	<input id="addressLat" type="hidden" class="addressLat" value="${work.location.address.point.latitude}" />
	<input id="addressLon" type="hidden" class="addressLon" value="${work.location.address.point.longitude}" />
</c:if>
<div class="popup-content" id="confirm-popup">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Confirm Assignment" />
	</jsp:include>
	<div class="grid">
		<div class="unit whole">
			<form id="confirmation-form" action="/mobile/assignments/confirmation/${work.workNumber}" method="POST">
				<wm-csrf:csrfToken />

				<input type="hidden" name='id' value="${work.workNumber}">
				<div class="confirmation-message">
					<c:choose>
						<c:when test="${isActiveResource}">
							<c:choose>
								<c:when test="${work.confirmable}">
									Please confirm this assignment prior to <strong>${wmfmt:formatCalendarWithTimeZone("EEE',' MMM d 'at' h:mm aa z", work.confirmByDate, timeZone)}.</strong>
								</c:when>
								<c:when test="${not work.confirmable}">
									It's too early to confirm this assignment. <strong>You will be able to confirm
									between ${wmfmt:formatCalendarWithTimeZone("EEE',' MMM d 'at' h:mm aa", work.confirmableDate, timeZone)}</strong> and <strong>${wmfmt:formatMillisWithTimeZone("EEE',' MMM d 'at' h:mm aa z", work.schedule.from, timeZone)}.</strong>
								</c:when>
								<c:otherwise>
									You cannot confirm this assignment.
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:when test="${isAdmin}">
							Confirm your worker is available to perform work.<strong>
							This assignment is set for confirmation
							<fmt:formatNumber value="${work.resourceConfirmationHours}"/> ${wmfmt:pluralize("hour", work.resourceConfirmationHours)}</strong> prior to the assignment start time.
						</c:when>
					</c:choose>
				</div>
				<c:if test="${isActiveResource or isAdmin}">
					<button type="submit" class="confirm-button default-button" <c:if test="${not work.confirmable}">disabled="true"</c:if> >Confirm</button>
				</c:if>

				<a href="javascript:void(0);" class="cancel-button popup-close">Cancel</a>
			</form>
		</div>
	</div>
</div>

<div class="popup-content" id="checkin-popup">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Check In" />
	</jsp:include>
	<div class="grid">
		<div class="unit whole">
				<%--If check in by phone, provide that info --%>
			<c:if test="${work.checkinCallRequired}">
				<p>When checking in / out for this assignment, please call <c:out value="${work.checkinContactName}"/> at <c:out value="${work.checkinContactPhone}"/>.</p>
			</c:if>
			<c:if test="${!work.checkinCallRequired || work.configuration.checkinRequiredFlag}">
				<%--
					If checkin call is not required or BOTH checkin call required and checkin required is selected
					then we we display the check in buttons to the user. This is intentional.
				--%>
				<div class="checkin-notice">Only check in for this assignment if you are starting work or resuming work.</div>
				<form class="check-in-form" action="/mobile/assignments/checkin/${work.workNumber}" method="POST">
					<wm-csrf:csrfToken />
					<input type="hidden" id="latitudeField" name="latitude" class="latitudeField" value="">
					<input type="hidden" id="longitudeField" name="longitude" class="longitudeField" value="">
					<input type="hidden" id="distanceField" name="distance" class="distanceField" value="">
					<c:if test="${! work.offsiteLocation and not empty work.location}" >
						<p class="distance-text">(current distance: <span class="distance">?</span>)</p>
					</c:if>
					<button type="submit" class="checkin-button spin">
						Check In<c:if test="${work.activeResource.checkedIn}"> Again</c:if>
					</button>
					<a href="javascript:void(0);" class="cancel-button popup-close">Cancel</a>

				</form>
			</c:if>
		</div><%--unit whole--%>
	</div><%--grid--%>
</div><%--popup--%>

<%--Checkout popup -- not visible until checkout is pressed --%>
<div class="popup-content" id="checkout-popup">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Check Out" />
	</jsp:include>
	<div class="grid">
		<div class="unit whole">
				<%--If check in by phone, provide that info --%>
			<c:if test="${work.checkinCallRequired}">
				<p>When checking in / out for this assignment, please call <c:out value="${work.checkinContactName}"/> at <c:out value="${work.checkinContactPhone}"/>.</p>
			</c:if>
				<%--
					If checkin call is not required or BOTH checkin call required and checkin required is selected
					then we we display the check in buttons to the user. This is intentional.
				--%>
			<c:if test="${!work.checkinCallRequired || work.configuration.checkinRequiredFlag}">
				<p>Please provide a note (${work.checkoutNoteRequiredFlag ? 'required' : 'optional'}):</p>
				<p><c:if test="${not empty work.checkoutNoteInstructions}"><em><c:out value="${work.checkoutNoteInstructions}" /></em></c:if></p>
				<form id="check-out-form" action="/mobile/assignments/checkout/${work.workNumber}" method="POST">
					<wm-csrf:csrfToken />

					<input type="hidden" id="latitudeField" name="latitude" class="latitudeField" value="">
					<input type="hidden" id="longitudeField" name="longitude" class="longitudeField" value="">
					<input type="hidden" id="distanceField" name="distance" class="distanceField" value="">

					<textarea name="noteText"></textarea>
					<c:if test="${! work.offsiteLocation and not empty work.location}" >
						<p class="distance-text">(current distance: <span class="distance">?</span>)</p>
					</c:if>
					<button type="submit" class="checkout-button spin">Check Out</button>
					<a href="javascript:void(0);" class="cancel-button popup-close">Cancel</a>
				</form>
			</c:if>
		</div><%--unit whole--%>
	</div><%--grid--%>
</div><%--popup--%>
