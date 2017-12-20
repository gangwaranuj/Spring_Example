<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<p>
	You are about to cancel this assignment.
	<c:if test="${work.pricing.id != pricingStrategyType['INTERNAL']}">
		You have the option to provide a
		payment to the assigned worker for an amount up to the assignment spend
		limit. The default suggestion is $25.
	</c:if>
</p>

<form action="/assignments/cancel_work/${work.workNumber}" id="cancel_work_form" class="form-horizontal" method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="${work.workNumber}"/>
	<input type="hidden" name="work_fee" value="${workFee}" id="work_fee" />
	<c:if test="${work.pricing.id eq pricingStrategyType['INTERNAL']}">
		<input type="hidden" name='price' id='amount' value='0' maxlength="10"/>
	</c:if>

	<div class="messages"></div>

	<c:if test="${work.pricing.id != pricingStrategyType['INTERNAL']}">
		<div class="control-group">
			<label for="amount" class="control-label required">Amount to Pay Worker</label>
			<div class="controls">
					<input type="text" name='price' id='amount' maxlength="10" style="width: 6em; display: inline;"/>
					<span class="add-on">
						x <fmt:formatNumber value="${workFee / 100}" type="percent"/>
						= <span id="cancel_total">$0</span>
					</span>
			</div>
		</div>
	</c:if>

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
			<strong>
				Important:
			</strong>
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
		<button type="submit" class="button">Cancel Assignment</button>
	</div>
</form>
