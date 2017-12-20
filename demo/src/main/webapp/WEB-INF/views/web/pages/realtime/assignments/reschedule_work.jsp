<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/realtime/reschedule_work" id="form_reschedule_assignment" class="form-horizontal" method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="${work.workNumber}"/>
	<input type="hidden" name='schedule_negotiation' value="1"/>

	<div class="messages"></div>

	<div class="control-group">
		<label class="control-label">Current date and time</label>

		<div class="controls">
			<c:choose>
				<c:when test="${not empty work.schedule && not empty work.schedule.from}">
					<c:choose>
						<c:when test="${work.schedule.range}">
							<c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, yyyy h:mma z', work.schedule.from, work.timeZone)}"/>
							to <c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, yyyy h:mma z', work.schedule.through, work.timeZone)}"/>
						</c:when>
						<c:otherwise>
							<c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, yyyy h:mma z', work.schedule.from, work.timeZone)}"/>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					Not set.
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label">New date and time</label>

		<div class="controls">
			<div class="assignments-date-ranges">
				<div class="inline-inputs">
					<input type="text" name="from" id="from" class="span2" placeholder='Select Date' value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', work.schedule.from, work.timeZone)}"/>
					<input type="text" name="fromtime" id="fromtime" class="span2" placeholder='Select Time' value="${wmfmt:formatMillisWithTimeZone('h:mma', work.schedule.from, work.timeZone)}"/>
					to
					<input type="text" name="to" id="to" class="span2" placeholder='Select Date' value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', work.schedule.through, work.timeZone)}"/>
					<input type="text" name="totime" id="totime" class="span2" placeholder='Select Time' value="${wmfmt:formatMillisWithTimeZone('h:mma', work.schedule.through, work.timeZone)}"/>
				</div>
				<span class="help-block">Leave end time blank to set specific appointment time</span>
			</div>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">OK</button>
	</div>
</form>
