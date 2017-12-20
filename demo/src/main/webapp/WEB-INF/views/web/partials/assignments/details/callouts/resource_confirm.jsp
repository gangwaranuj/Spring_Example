<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:if test="${work.resourceConfirmationRequired && !work.activeResource.confirmed && (is_active_resource || is_admin)}">
	<c:set var="timeZone" value="${work.timeZone}" />

	<div class="media completion">
		<div class="completion-icon">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/checkin-icon.jsp"/>
		</div>
		<div class="media-body">
			<h4>
				Confirm Assignment
				<small class="meta"><span class="label label-important">Required</span></small>
			</h4>

			<form action='/assignments/confirmation/${work.workNumber}' method="GET">
				<input type="hidden" name='id' value="${work.workNumber}">

				<c:choose>
					<c:when test="${is_active_resource}">
						<c:choose>
							<c:when test="${work.confirmable}">
								<p>Please confirm this assignment prior to <strong>${wmfmt:formatCalendarWithTimeZone("EEE',' MMM d 'at' h:mm aa z", work.confirmByDate, timeZone)}</strong>.</p>
							</c:when>
							<c:when test="${not work.confirmable}">
								<p>It's too early to confirm this assignment. You will be able to confirm
									between <strong>${wmfmt:formatCalendarWithTimeZone("EEE',' MMM d 'at' h:mm aa", work.confirmableDate, timeZone)}</strong> and <strong>${wmfmt:formatMillisWithTimeZone("EEE',' MMM d 'at' h:mm aa z", work.schedule.from, timeZone)}</strong>.</p>
							</c:when>
							<c:otherwise>
								<p>You cannot confirm this assignment.</p>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${is_admin and work.status.code != workStatusTypes['PAYMENT_PENDING']}">
						<p>Confirm on behalf of your worker if he or she is available to perform work.
							<em>Note: you will be alerted if your worker has not confirmed at least <fmt:formatNumber
									value="${work.resourceConfirmationHours}"/> ${wmfmt:pluralize("hour", work.resourceConfirmationHours)} prior to the assignment start
								time.</em></p>
					</c:when>
				</c:choose>

				<div class="alert-actions">
					<c:if test="${(is_active_resource or is_admin) && work.status.code != workStatusTypes['PAYMENT_PENDING']}">
						<button type="submit" class="button pull-right completion_button" <c:if test="${not work.confirmable and not is_admin}">disabled="true"</c:if> >
							Confirm Schedule<c:if test="${is_admin}"> on Behalf </c:if>
						</button>
					</c:if>
				</div>
			</form>
		</div>
	</div>
</c:if>
