<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form id="form_stop_payment" action="/assignments/stop_payment/${workNumber}" class="form-stacked" method="post" accept-charset="utf-8">
	<wm-csrf:csrfToken />
	<div class="clearfix mb">
		<label for="reason" class="required">Why are you stopping payment on this assignment?</label>
		<div class="input">
			<textarea id="reason" name="reason" rows="7" class="span8"></textarea>
			<span class="help-block">
				Please be as detailed as possible. Stop payment on this assignment will reset the payment terms and move
				back to in progress status.
			</span>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Stop Payment</button>
	</div>
</form>
