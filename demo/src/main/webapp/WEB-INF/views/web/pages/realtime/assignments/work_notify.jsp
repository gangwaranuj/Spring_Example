<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/realtime/workNotify" id="form_work_notify" class="form-stacked" method="POST">
	<input type="hidden" name="workNumber" value="${work.workNumber}"/>
	<wm-csrf:csrfToken />
	<div class="messages"></div>

	<p>Work Notify will attempt to send a Push Notification / SMS to workers who have not viewed, declined or countered the assignment.</p>

	<div class="wm-action-container">
		<button type="submit" class="button">Send</button>
	</div>
</form>
