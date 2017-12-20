<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div id="payment_issue_future_invoice">
	<hr/>
	<p>
		<strong>Issue subscription invoice for the period from ${wmfmt:formatCalendar("MM/dd/yyyy", future_range_from)} to ${wmfmt:formatCalendar("MM/dd/yyyy", future_range_to)}.</strong>
	</p>

	<table>
		<tr>
			<td><strong>Due Date</strong></td>
			<td>${wmfmt:formatCalendar("MM/dd/yyyy", future_invoice_due_date)}</td>
		</tr>
		<tr>
			<td><strong>Balance Due</strong></td>
			<td>$ <c:out value="${future_invoice_balance}" /></td>
		</tr>
	</table>

	There are currently <c:out value="${current_invoices_outstanding_number}" /> invoice(s) outstanding and the most recent issued invoice is for subscription period from ${wmfmt:formatCalendar("MM/dd/yyyy", future_range_from)} to ${wmfmt:formatCalendar("MM/dd/yyyy", future_range_to)}
	<form action="/admin/manage/company/issue_future_invoice/${company_id}/${subscription_id}" method="POST">
		<wm-csrf:csrfToken />
		<div class="wm-action-container">
			<button type="button" data-modal-close class="button">Cancel</button>
			<input type="submit" class="button" value="Issue" />
		</div>
	</form>
</div>