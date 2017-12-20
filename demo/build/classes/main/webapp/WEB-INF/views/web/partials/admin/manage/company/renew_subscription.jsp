<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div id="renew_subscription_modal" class="subscription_modal" title="Subscription Renewal Request" cbox-inner-width="550">
	<div class="messages"></div>

	<form action="/admin/manage/company/renew_subscription" method="post" class="form-horizontal">
		<wm-csrf:csrfToken />
		<input type="hidden" name="parentSubscriptionId" value="<c:out value="${subscription_id}"/>">
		<input type="hidden" name="numberOfPeriods" value="0">

		<div class="control-group">
			<label class="control-label required">Renewal Term (months)</label>
			<div class="controls">
				<input type="text" name="numberOfMonths" class="input-mini" data-constraints='@NotEmpty'>
				<em>New termination date: <span class="renewal_end_date"></span></em>

				<span class="inlineError">Renewal Term must be a multiple of <c:out value="${subscription.subscriptionPeriod}" /></span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">Modify pricing?</label>
			<div class="controls">
				<label class="radio inline">
					<input type="radio" name="modifyPricing" value="yes"/> Yes
				</label>
				<label class="radio inline">
					<input type="radio" name="modifyPricing" value="no" checked="checked"/> No
				</label>
			</div>
		</div>

		<div id="renew_pricing_tiers" class="dn well">
			<div class="control-group">
				<div class="span3 offset1">Throughput Range</div>
				<div class="span3">Payment Period Amount</div>

				<c:if test="${subscription.vendorOfRecord}">
					<div class="span3">Vendor of Record Amount</div>
				</c:if>
			</div>
		</div>

		<div class="wm-action-container">
			<button class="button">Submit</button>
			<button type="button" class="button cancel">Cancel</button>
		</div>
	</form>
</div>

<script type="text/javascript">
	function format_currency(num) {
		num = (num || 0).toString().replace(/\$|\,/g, '');
		if (!isFinite(num))
			num = "0";

		sign = (num == (num = Math.abs(num)));
		num = Math.floor(num * 100 + 0.50000000001);
		cents = num % 100;
		num = Math.floor(num / 100).toString();

		if (cents < 10)
			cents = "0" + cents;

		for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
			num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3));

		return (((sign) ? '' : '-') + '$' + num + '.' + cents);
	}
</script>

<script type="text/x-jquery-tmpl" id="renewal_tier_template">
	<div class="control-group">
		<label class="control-label span1">Tier \${idx+1}</label>
		<div class="span3 text">
			\${format_currency(min)} to \${isFinite(max) ? format_currency(max) : max}
		</div>

		<div class="span3">
			<span class="add-on">$</span>
			<input type="text" class="input-small" name="subscriptionPaymentTierDTOs[\${idx}].paymentAmount" value="\${payAmount}" data-constraints='@PaymentAmountRange(lower=0, upper=100000000, label="")'>
		</div>

		<c:if test="${subscription.vendorOfRecord}">
		<div class="span3">
			<span class="add-on">$</span>
			<input type="text" class="input-small" name="subscriptionPaymentTierDTOs[\${idx}].vendorOfRecordAmount" value="\${vorAmount}" data-constraints='@PaymentAmountRange(lower=0, upper=100000000, label="")'>
		</div>
		</c:if>
	</div>
</script>