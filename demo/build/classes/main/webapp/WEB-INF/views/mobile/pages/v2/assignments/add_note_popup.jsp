<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<%-- Add note popup --%>
<div id="add-note-popup" class="popup-content grid wrap">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Add Message" />
	</jsp:include>

	<div class="unit whole">
		<form action="/mobile/assignments/dialogs/add_note/${work.workNumber}" id="add-note-form" method="post">
			<wm-csrf:csrfToken />

			<input type="hidden" name="id" value="${work.workNumber}" />
			<label for="note-text">Enter a message</label>
			<textarea id="note-text" name="noteText"></textarea>
			<input type="submit" name="submit" value="Add Message" />
			<a href="javascript:void(0);" class="popup-close close-button">Cancel</a>
		</form>

	</div>	<%--unit--%>
</div>
