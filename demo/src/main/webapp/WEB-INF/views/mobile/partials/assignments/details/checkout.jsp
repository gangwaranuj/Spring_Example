<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div id="checkout-note">
	<div class="brs">
		Note (${work.checkoutNoteRequiredFlag ? 'required' : 'optional'}):
	</div>
	<div class="brs" style="background-color:rgba(0, 0, 0, 0);">
		<c:if test="${not empty work.checkoutNoteInstructions}"><em><c:out value="${work.checkoutNoteInstructions}" /></em></c:if>
	</div>
	<form action="/mobile/assignments/checkout/${work.workNumber}"  data-role="none" data-ajax="false" method="POST">
		<wm-csrf:csrfToken />

		<textarea class="checkout-note" name="noteText"></textarea>

		<button data-theme="b">Check Out</button>
	</form>
</div>
