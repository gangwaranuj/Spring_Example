<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/drop_remove_label/${work.workNumber}" id="drop_remove_label_form" class="form-stacked" method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name="labelId" value="${label_id}"/>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/>

	<div class="messages"></div>

	<div class="clearfix">
		<label for='note'>Note: (optional)</label>

		<div class="input">
			<textarea name='note' id='note' class='large'><c:out value="${note}" /></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Remove</button>
	</div>
</form>
