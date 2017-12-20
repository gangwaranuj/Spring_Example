<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/add_checkout_note/${workNumber}" method="POST" id='checkout-note' class="form-stacked">
	<wm-csrf:csrfToken />
	<div id="add_checkout_note_messages"></div>

	<div>
		<c:if test="${not empty mmw.checkoutNoteInstructions}">
			<div class="clearfix">
				<em><c:out value="${mmw.checkoutNoteInstructions}"/></em>
			</div>
		</c:if>
		<div class="clearfix">
			<div class="input">
				<textarea name="noteText" class="span7"><c:out value="${noteText}" /></textarea>
			</div>
		</div>
	</div>


	<div class="wm-action-container">
		<button type="submit" class="button">Save</button>
	</div>
</form>
