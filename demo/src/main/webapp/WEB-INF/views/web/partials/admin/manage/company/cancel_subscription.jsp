<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div id="payment_cancel_subscription" class="subscription_modal" title="Cancel Subscription">
	<div class="messages"></div>
	<form class="form-horizontal" method="post" action="/admin/manage/company/cancel_subscription">
		<wm-csrf:csrfToken />
		<input type="hidden" name="company_id" value="<c:out value="${requestScope.id}"/>">
		<div class="control-group">
			<label class="control-label required">Cancellation Date</label>
			<div class="controls">
				<input type="text" id="cancellation_date" name="cancellation_date" class="span2" data-constraints='@NotEmpty(label="Cancellation Date") @Future(format="MDY", label="Cancellation Date")'>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label required">Penalty Amount</label>
			<div class="controls">
				<div class="input-prepend">
					<span class="add-on">$</span>
					<input type="text" name="penalty_amount" class="span2" data-constraints='@NotEmpty(label="Penalty Amount") @Numeric(label="Penalty Amount")'/>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Notes</label>
			<div class="controls">
				<textarea name="note"></textarea>
			</div>
		</div>
		<div class="form-actions">
			<button class="button">Submit</button>
			<button type="button" class="button cancel">Cancel</button>
		</div>
	</form>
</div>