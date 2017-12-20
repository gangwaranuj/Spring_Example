<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form:form method="post" cssClass="form-stacked">
	<wm-csrf:csrfToken />
	<p>Are you sure you would like to remove this issue?</p>
	<div class="wm-action-container">
		<button type="button" class="button cancel">Cancel</button>
		<button type="submit" class="button">Remove</button>
	</div>
</form:form>
