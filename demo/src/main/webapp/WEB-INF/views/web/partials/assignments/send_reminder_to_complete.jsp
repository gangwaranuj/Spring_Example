<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/${workNumber}/send_reminder_to_complete" method="POST" class="form-stacked">
	<wm-csrf:csrfToken />

	<c:import url="/WEB-INF/views/web/partials/message.jsp" />

	<p>Send a reminder to <c:out value="${resource.user.name.firstName}" /> <c:out value="${resource.user.name.lastName}" /> asking them to close out the assignment. ${maxMsgLength} characters max.</p>

	<div class="clearfix">
		<label>Note</label>
		<div class="input">
			<textarea name="message" class="span6" rows="4"></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<a class="button" href="/assignments/details/${workNumber}">Cancel</a>
		<button type="submit" class="button">Send Reminder</button>
	</div>

</form>
