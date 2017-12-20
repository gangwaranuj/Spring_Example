<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div>
	<h4>
		<c:out value="${status.description}" />
		<span class="label label-important">Required</span>
	</h4>
	<p>Note from <c:out value="${work.buyer.name.firstName}" /> <c:out value="${work.buyer.name.lastName}" /> </p>
	<p><c:out value="${status.note}" /></p>

	<form action='/assignments/remove_label/${work.workNumber}' method="POST">
		<wm-csrf:csrfToken />
		<input type="hidden" name='substatus_code' value="${status.code}"/>
		<div class="alert-actions">
			<button type="primary" class="button">Dismiss and continue</button>
		</div>
	</form>
</div>
