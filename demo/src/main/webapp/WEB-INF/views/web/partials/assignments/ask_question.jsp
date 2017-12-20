<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action='/assignments/ask_question/${work.workNumber}' class='form-stacked' method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="${work.workNumber}"/>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<label>Question:</label>
	<div class="input">
		<textarea rows="10" cols="30" name="question" class='span8'></textarea>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Ask Question</button>
	</div>
</form>
