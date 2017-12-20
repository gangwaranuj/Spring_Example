<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<p>Only cancel an assignment in case of emergency, or if you have made arrangements with the client.</p>
<p>Cancellations are tracked and displayed on your worker score card and may lead to fewer assignments.</p>
<p>If you are certain you have to cancel the assignment, please explain your reason for canceling below.</p>

<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp" />

<form action="/assignments/abandon_work/${work.workNumber}" class='form-stacked'  method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="${work.workNumber}"/>

	<div class="clearfix">
		<label name="cancel_note" class="required">Cancellation Reason</label>
		<div class="input span8">
			<textarea class="span8" name="cancel_note" id="cancel_note" rows="4"></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Cancel Assignment</button>
	</div>
</form>
