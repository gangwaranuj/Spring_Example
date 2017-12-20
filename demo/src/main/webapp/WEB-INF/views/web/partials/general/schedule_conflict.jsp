<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<c:set var="type" value="apply to"/>
<c:if test="${work.configuration.assignToFirstResource}">
	<c:set var="type" value="accept"/>
</c:if>

<div class="dn">
	<div id="schedule_conflict_dialog_container">
		<c:set var="fragment" value="an assignment"/>
		<c:if test="${wmfn:collectionSize(scheduleConflicts) > 1}">
			<c:set var="fragment" value="${wmfn:collectionSize(scheduleConflicts)} assignments"/>
		</c:if>
		<p class="conflict-text">You can't ${type} this assignment because you are already booked for ${fragment} at this time.</p>

		<c:forEach var="conflict" items="${scheduleConflicts}">
			<div class="conflict-row">
				<a class="conflict-assignment" href="/assignments/details/${conflict.workNumber}">${conflict.title}</a>
				<c:choose>
					<c:when test="${conflict.schedule.range}">
						<span class="conflict-time">
							<c:out value="${wmfmt:formatCalendarWithTimeZone('MMM d, YYYY h:mma', conflict.schedule.from, conflict.timeZone.timeZoneId)}"/>
							<strong>to</strong>
							<c:choose>
								<c:when test="${wmfn:isSameDay(conflict.schedule.from, conflict.schedule.through)}">
									<c:out value="${wmfmt:formatCalendarWithTimeZone('h:mma z', conflict.schedule.through, conflict.timeZone.timeZoneId)}"/>
								</c:when>
								<c:otherwise>
									<c:out value="${wmfmt:formatCalendarWithTimeZone('MMM d, YYYY h:mma z', conflict.schedule.through, conflict.timeZone.timeZoneId)}"/>
								</c:otherwise>
							</c:choose>
						</span>
					</c:when>
					<c:otherwise>
						<span class="conflict-time">
							<c:out value="${wmfmt:formatCalendarWithTimeZone('MMM d, YYYY h:mma z', conflict.schedule.from, conflict.timeZone.timeZoneId)}"/>
							<c:choose>
								<c:when test="${conflict.pricingStrategy.name == 'Per Hour'}">
									<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(conflict.pricingStrategy.maxNumberOfHours)}"/>
									<c:choose>
										<c:when test="${maxHoursMinutes.minutes > 0}">
											<c:out value="(${maxHoursMinutes.hours} hours ${maxHoursMinutes.minutes} minutes)"/>
										</c:when>
										<c:otherwise>
											<c:out value="(${maxHoursMinutes.hours} hours)"/>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:when test="${conflict.pricingStrategy.name == 'Blended Per Hour'}">
									<c:set var="maxHoursMinutes"
										value="${wmfmt:getHoursAndMinutes(
											conflict.pricingStrategy.initialNumberOfHours +
											conflict.pricingStrategy.maxBlendedNumberOfHours)}"
									/>
									<c:choose>
										<c:when test="${maxHoursMinutes.minutes > 0}">
											<c:out value="(${maxHoursMinutes.hours} hours ${maxHoursMinutes.minutes} minutes)"/>
										</c:when>
										<c:otherwise>
											<c:out value="(${maxHoursMinutes.hours} hours)"/>
										</c:otherwise>
									</c:choose>
								</c:when>
							</c:choose>
						</span>
					</c:otherwise>
				</c:choose>
			</div>
		</c:forEach>

		<div class="wm-action-container">
			<button class="button" data-modal-close>Dismiss</button>
		</div>
	</div>
</div>
