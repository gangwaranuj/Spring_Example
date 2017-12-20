<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<form action="/assignments/pay_now/${work.workNumber}" method="post">
	<wm-csrf:csrfToken />
	<input type="hidden" name='id' value="${work.workNumber}" />
	<p>You have cancelled this assignment and agreed to pay the worker.</p>

	<sec:authorize access="(!principal.userPaymentAccessBlocked) AND (principal.hasCustomAccessSettingsSet OR hasAnyRole('PERMISSION_INVOICES', 'PERMISSION_PAY_INVOICE', 'PERMISSION_PAY_ASSIGNMENT', 'PERMISSION_PAYABLES'))">
		<c:if test="${!work.invoice.bundled}">
			<div class="alert-actions">
				<button type="submit" class="button">Pay Now</button>
			</div>
		</c:if>
	</sec:authorize>

</form>


