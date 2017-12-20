<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<style type="text/css">
	.ui-datepicker {
		z-index:10000 !important;
	}
</style>

<div class="alert" id="schedule-requires-approval">
	<c:choose>
	<c:when test="${isActiveResource}">
		<p><strong>Note:</strong> If the client declines your reschedule request, you will be unassigned from this assignment.</p>
	</c:when>
	<c:otherwise>
		<p><strong>Note:</strong> If the worker declines your reschedule request, they will be unassigned from this assignment.</p>
	</c:otherwise>
	</c:choose>
</div>

<p>
	<c:choose>
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

		<strong>Current window:</strong>
		<span class="schedule-from span2" data-timestamp="${work.schedule.from}"><c:out value="${wmfmt:formatMillisWithTimeZone(fromFmt, work.schedule.from, work.timeZone)}"/></span> to
		<span class="schedule-through span2" data-timestamp="${work.schedule.through}"><c:out value="${wmfmt:formatMillisWithTimeZone(throughFmt, work.schedule.through, work.timeZone)}"/></span>
	</c:when>
	<c:otherwise>
		<strong>Current time:</strong>
		<span class="schedule-from" data-timestamp="${work.schedule.from}"><c:out value="${wmfmt:formatMillisWithTimeZone('EEEE, MMM d, YYYY h:mma z', work.schedule.from, work.timeZone)}"/></span>
	</c:otherwise>
	</c:choose>
</p>

<c:choose>
<c:when test="${resource.appointment.range}">
	<c:set var="schedule_time_selected" value=""/>
	<c:set var="schedule_window_selected" value="checked='checked'"/>
	<c:set var="showToDateOptions" value="inline"/>
</c:when>
<c:otherwise>
	<c:set var="schedule_time_selected" value="checked='checked'"/>
	<c:set var="schedule_window_selected" value=""/>
	<c:set var="showToDateOptions" value="none"/>
</c:otherwise>
</c:choose>

<script type="text/javascript">
	var resourceAppointmentFrom = '${wmfmt:escapeJavaScript(resource.appointment.from)}';
	var resourceAppointmentThrough = '${wmfmt:escapeJavaScript(resource.appointment.through)}';
</script>

<div class="clearfix">
	<div class="input">
		<div class="inline-inputs">
			<label class="dib normal">
				<input type="radio" id="new_time" name="reschedule_option" value="time" ${schedule_time_selected} />
				<span id="label_specific_time">Propose</span> <strong>time</strong>
			</label>
			<label class="dib normal">
				<input type="radio" id="new_window" name="reschedule_option" value="window" ${schedule_window_selected} />
				<span id="label_time_window">Propose</span> <strong>time window</strong>
			</label>
		</div>
	</div>
</div>

<div class="clearfix">
	<div class="input">
		<div class="inline-inputs">
			<div class="assignments-date-ranges">
				<c:choose>
				<c:when test="${!empty resource.appointment.from}">
					<input type="text" name="from" value='<c:out value="${wmfmt:formatCalendarWithTimeZone('MM/dd/YYYY', resource.appointment.from, work.timeZone)}"/>' class='span2' placeholder='Select Date'/>
					<input type="text" name="fromtime" value='<c:out value="${wmfmt:formatCalendarWithTimeZone('hh:mma', resource.appointment.from, work.timeZone)}"/>' class='span2' placeholder='Select Time'/>
				</c:when>
				<c:otherwise>
					<input type="text" name="from" value='' class='span2' placeholder='Select Date'/>
					<input type="text" name="fromtime" value='' class='span2' placeholder='Select Time'/>
				</c:otherwise>
				</c:choose>

				<span class="to-date" style="display:${showToDateOptions};">
					to
					<c:choose>
					<c:when test="${!empty resource.appointment.through}">
						<input type="text" name="to" value='<c:out value="${wmfmt:formatCalendarWithTimeZone('MM/dd/YYYY', resource.appointment.through, work.timeZone)}"/>' class='span2' placeholder='Select Date'/>
						<input type="text" name="totime" value='<c:out value="${wmfmt:formatCalendarWithTimeZone('hh:mma', resource.appointment.through, work.timeZone)}"/>' class='span2' placeholder='Select Time'/>
					</c:when>
					<c:otherwise>
						<input type="text" name="to" value='' class='span2' placeholder='Select Date'/>
						<input type="text" name="totime" value='' class='span2' placeholder='Select Time'/>
					</c:otherwise>
					</c:choose>
				</span>
			</div>
		</div>
	</div>
</div>

<div class="alert-message block-message dn" id="appointment-requires-approval">
	<strong>Note:</strong> The selected date &amp; time is outside the current window and will require approval.
	<c:if test="${currentUser.seller || currentUser.dispatcher}">
		If the client declines your request, you will be unassigned from this assignment.
	</c:if>
	Times within the current window are automatically approved.
</div>
