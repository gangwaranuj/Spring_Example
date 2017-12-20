<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<h4><c:out value="${work.title}"/></h4>
<c:if test="${not empty work.schedule and not empty work.schedule.from}">
	<div>Date:</div>
	<p><small>	<c:choose>
		<c:when test="${work.schedule.range}">
			<c:choose>
			<c:when test="${work.schedule.through - work.schedule.from < 24 * 60 * 60 * 1000}">
				<c:set var="fromFmt" value="EEEE, MMM d, YYYY h:mma" />
				<c:set var="throughFmt" value="h:mma z" />
			</c:when>
			<c:otherwise>
				<c:set var="fromFmt" value="EEEE, MMM d, YYYY h:mma" />
				<c:set var="throughFmt" value="EEEE, MMM d, YYYY h:mma z" />
			</c:otherwise>
			</c:choose>

			<span class="schedule-from" data-timestamp="${work.schedule.from}"><c:out value="${wmfmt:formatMillisWithTimeZone(fromFmt, work.schedule.from, work.timeZone)}"/></span> to
			<span class="schedule-through" data-timestamp="${work.schedule.through}"><c:out value="${wmfmt:formatMillisWithTimeZone(throughFmt, work.schedule.through, work.timeZone)}"/></span>
		</c:when>
		<c:otherwise>
			<span class="schedule-from" data-timestamp="${work.schedule.from}"><c:out value="${wmfmt:formatMillisWithTimeZone('EEEE, MMM d, YYYY h:mma z', work.schedule.from, work.timeZone)}"/></span>
		</c:otherwise>
		</c:choose>
	</small></p>
</c:if>