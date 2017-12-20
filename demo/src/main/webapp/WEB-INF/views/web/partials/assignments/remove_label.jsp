<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/remove_label/${work.workNumber}" class="form-stacked" method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name="label_id" value="${labelId}"/>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/>

	<div class="clearfix">
		<label>Note: (optional)</label>

		<div class="input">
			<textarea name="note" class="span8" rows="3"><c:out value="${note}" /></textarea>
			<div class="input"></div>

			<div class="wm-action-container">
				<button type="submit" class="button">Remove</button>
			</div>
		</div>
	</div>
</form>
