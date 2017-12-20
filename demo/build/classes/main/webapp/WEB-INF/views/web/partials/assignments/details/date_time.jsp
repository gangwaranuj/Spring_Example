<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>


<c:set var="timeZone" value="${work.timeZone}" />
<dl class="iconed-dl">
	<drt><jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/calendar_v2.jsp"/></dt>
	<dd>
		<c:choose>
			<c:when test="${not empty work.schedule && not empty work.schedule.from}">
				<c:choose>
					<c:when test="${not empty work.activeResource and not empty work.activeResource.appointment}">
						<c:choose>
							<c:when test="${work.activeResource.appointment.range}"><br/>
								<strong>${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy', work.activeResource.appointment.from, timeZone)}
									to ${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy', work.activeResource.appointment.through, timeZone)}</strong><br/>
								${wmfmt:formatMillisWithTimeZone('h:mm a z', work.activeResource.appointment.from, timeZone)}
								to ${wmfmt:formatMillisWithTimeZone('h:mm a z', work.activeResource.appointment.through, timeZone)}
							</c:when>
							<c:otherwise>
								<strong>${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy', work.activeResource.appointment.from, timeZone)}</strong><br/>
								${wmfmt:formatMillisWithTimeZone('h:mm a z', work.activeResource.appointment.from, timeZone)}
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${work.schedule.range}">
						<strong>${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy', work.schedule.from, timeZone)}
							to ${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy', work.schedule.through, timeZone)}</strong><br/>
						${wmfmt:formatMillisWithTimeZone('h:mm a', work.schedule.from, timeZone)}
						to ${wmfmt:formatMillisWithTimeZone('h:mm a z', work.schedule.through, timeZone)}
					</c:when>
					<c:otherwise>
						<strong>${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy', work.schedule.from, timeZone)}</strong><br/>
						${wmfmt:formatMillisWithTimeZone('h:mm a z', work.schedule.from, timeZone)}
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>Unknown</c:otherwise>
		</c:choose>
		<br/>
		<small class="meta">
			<c:if test="${work.activeResource.confirmed && work.resourceConfirmationRequired}">
				Confirmed on ${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy', work.activeResource.confirmedOn, timeZone)}
			</c:if>
		</small>
	</dd>
</dl>
