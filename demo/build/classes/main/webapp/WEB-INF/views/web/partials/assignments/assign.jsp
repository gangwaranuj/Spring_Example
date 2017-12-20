<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/assign/${work.workNumber}" id="form_assign" method="post" class='form-stacked'>
	<wm-csrf:csrfToken />
	<div class="message_container"></div>

	<div class="clearfix">
		<label for="assign-dropdown">Select employee:</label>

		<div class="input">
			<select name="assignee" id="assign-dropdown">
				<c:forEach items="${users}" var="user">
					<option value="${user.id}"><c:out value="${user.firstName}" /> <c:out value="${user.lastName}" /></option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="clearfix">
		<label for="assign_note">Note:</label>

		<div class="input">
			<textarea name="note" id="assign_note" class="span8" placeholder="(Optional)"></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Assign</button>
	</div>
</form>
