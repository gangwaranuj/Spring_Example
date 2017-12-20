<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/reassign_internal/${work.workNumber}" id="form_reassigninternal" method="post" class='form-stacked'>
	<wm-csrf:csrfToken />
	<div class="message_container"></div>

	<div class="clearfix">
		<label for="reassigninternal-dropdown">Select employee:</label>

		<div class="input">
			<select name="assignee" id="reassigninternal-dropdown">
				<c:forEach items="${users}" var="user">
					<option value="${user.id}"><c:out value="${user.firstName}" /> <c:out value="${user.lastName}" /></option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="clearfix">
		<label for="reassign_note">Note:</label>

		<div class="input">
			<textarea name='note' id='reassign_note' class='span8' placeholder="(Optional)"></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Reassign</button>
	</div>
</form>
