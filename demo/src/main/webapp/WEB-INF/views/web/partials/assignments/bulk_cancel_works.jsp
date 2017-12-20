<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<p>${modelDescription}</p>

<form action="/assignments/cancel_works_multiple" id="cancel_work_form" class="form-horizontal" method="POST">
	<wm-csrf:csrfToken/>
	<input type="hidden" name="workNumbers" value="" />

	<div class="messages"></div>

	<div class="control-group">
		<label for="amount" class="control-label required">Amount to Pay Workers</label>

		<div class="controls">
			<input type="text" name='price' id="amount" maxlength="10" class="span2" />
		</div>
	</div>

	<div class="control-group">
		<label for="reason" class="control-label required">Reason for Cancellation</label>

		<div class="controls">
			<select id="reason" name="cancellationReasonTypeCode" class="input-block-level">
				<option value="buyer_cancelled">I want to cancel this assignment</option>
				<option value="resource_cancelled">Worker cancelled prior to start</option>
				<option value="resource_abandoned">Worker abandoned assignment</option>
			</select>
		</div>
	</div>

	<div class="cancellation-notice-outlet alert-message block-message notice dn">
		<p>
			<strong>Important:</strong>
			Worker cancellation / abandonment issues will be reflected on the
			worker&rsquo;s profile. Please report with care.
		</p>
	</div>

	<div class="control-group">
		<label class="control-label required" for="cancel_note">Note</label>

		<div class="controls">
			<textarea name='note' id='cancel_note' class='input-block-level' rows="4"></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<button data-modal-close class="button">Cancel</button>
		<button class="button" id="cancel_work_button">Cancel Assignment(s)</button>
	</div>

</form>
