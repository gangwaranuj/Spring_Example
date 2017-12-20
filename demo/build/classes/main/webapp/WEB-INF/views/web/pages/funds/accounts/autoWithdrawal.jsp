<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form:form modelAttribute="autoWithdrawForm" action="/funds/accounts/auto_withdrawal/${id}" method="POST" id="auto_withdraw_form">
	<wm-csrf:csrfToken />
	<p>
		<p>
			When automatic withdrawal is on, and your account has an "available to withdraw" balance, we will deposit your full balance into your Work Market Visa Card.
		</p>
		<p>
			Funds will be available shortly after 4PM EST (excluding weekends and bank holidays).
		</p>
	</p>

	<div class="clearfix">
		<div class="input">
			<label>
				<form:checkbox path="autoWithdraw" id="auto_withdraw"/> Automatic Withdrawal On
			</label>
		</div>

	</div>

	<div class="wm-action-container">
		<button type="button" class="button cancel">Cancel</button>
		<button type="submit" class="button">Save</button>
	</div>
</form:form>
