<%--TODO: move to partial - not a page--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/payments/invoices/email/${invoiceId}" id="form_email_invoices" method="post">
	<wm-csrf:csrfToken />

	<div class="clearfix">
		<label class="required">Send to Email</label>
		<div class="input">
			<input type="text" name="email" value="${defaultEmail}" maxlength="255" class="span8" />
		</div>
	</div>
	<div class="wm-action-container">
		<button type="submit" class="button">Send</button>
	</div>
</form>
