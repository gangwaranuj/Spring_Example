<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<div class="lightweight time-tracking">
	<c:forEach var="entry" items="${work.activeResource.timeTrackingLog}">
		<div class="time-tracking-pair">
			<c:choose>
				<c:when test="${not empty entry.checkedOutOn && entry.checkedOutOn > 0}">
					<div class="time-tracking-entry">
						<strong>Check-In: </strong> ${wmfmt:formatMillisWithTimeZone('M/dd/yyyy H:mma z', entry.checkedInOn, currentUser.timeZoneId)} <c:if test="${entry.distanceIn >= 0.0}">(${entry.distanceIn} mi)</c:if>
					</div>
					<div>
						<strong>Check-Out: </strong> ${wmfmt:formatMillisWithTimeZone('M/dd/yyyy H:mma z', entry.checkedOutOn, currentUser.timeZoneId)} <c:if test="${entry.distanceOut >= 0.0}">(${entry.distanceOut} mi)</c:if>
					</div>
				</c:when>
				<c:otherwise>
					<div class="time-tracking-entry">
						<strong>Check-In: </strong> ${wmfmt:formatMillisWithTimeZone('M/dd/yyyy H:mma z', entry.checkedInOn, currentUser.timeZoneId)} <c:if test="${entry.distanceIn >= 0.0}">(${entry.distanceIn} mi)</c:if>
					</div>
					<div>
						<strong>Check-Out: </strong>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</c:forEach>
</div>