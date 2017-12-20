<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

	<form action="/assignments/pay_now/${work.workNumber}" method="post">
		<wm-csrf:csrfToken />
		<input type="hidden" name="id" value="${work.workNumber}"/>
		<small class="alert alert-info">You have approved this assignment for payment.</small>

		<h6>Resolution</h6>
		<blockquote class="wordwrap"><em><c:out value="${work.resolution}"/></em></blockquote>

		<sec:authorize access="(!principal.userPaymentAccessBlocked) AND (principal.hasCustomAccessSettingsSet OR hasAnyRole('PERMISSION_INVOICES', 'PERMISSION_PAY_INVOICE', 'PERMISSION_PAY_ASSIGNMENT', 'PERMISSION_PAYABLES')) AND !principal.isMasquerading()">
			<c:if test="${!work.invoice.bundled}">
				<div class="wm-action-container">
					<button type="submit" class="button">Pay Now</button>
				</div>
			</c:if>
		</sec:authorize>
	</form>
