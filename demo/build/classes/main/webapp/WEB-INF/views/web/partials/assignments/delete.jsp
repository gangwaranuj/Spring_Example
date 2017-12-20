<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/delete/${work.workNumber}" method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" value="${work.workNumber}"/>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/>

	<p>Are you sure you want to delete this draft?</p>

	<div class="wm-action-container">
		<button type="button" class="button cancel" data-modal-close="true">No</button>
		<button type="submit" class="button">Yes</button>
	</div>
</form>
