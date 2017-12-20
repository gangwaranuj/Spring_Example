<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<div class="alert alert-block <c:if test="${not useMboServices}">dn</c:if>" id="payment-will-be-made-outside-workmarket">Payment will be made outside of Work Market ${useMboServices}</div>

<form:hidden path="pricing" id="pricing" />
<form:hidden path="pricing_mode" id="pricing_mode" />

<div class="row">
<c:choose>
	<c:when test="${spendLimit > 0 || apLimit > 0}">
		<div class="span5 intro-available-funds-helper" style="position:absolute;top:80px;right:-300px">
			<div class="alert alert-block clear">
				<p>
					<strong>
						Available funds for assignments:
						<span class="tooltipped tooltipped-n" aria-label="These numbers are your current balance, not including the value of this assignment">
							<i class="wm-icon-question-filled"></i>
						</span>
					</strong>
				</p>
				<c:if test="${apLimit > 0}">
					<p>
						You can use up to
						<strong>
								<span class="label label-success" style="font-weight: normal">
									<fmt:formatNumber value="${apLimit}" type="currency" />
								</span>
							of Payment Terms
						</strong>
						to send assignments.
					</p>
				</c:if>
				<c:if test="${spendLimit > 0}">
					<p>
						You can spend up to
						<strong>
								<span class="label label-success" style="font-weight: normal">
									<fmt:formatNumber value="${spendLimit}" type="currency" />
								</span>
							in cash
						</strong>
						to send assignments on immediate terms.
					</p>
				</c:if>
				<c:if test="${mmw.budgetEnabledFlag}">
					<div id="assignment-project-budget" class="dn">
						You can use up to <span id="remainingBudget" class="label label-success"></span> of remaining budget balance to send assignments in this project.
					</div>
				</c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="span4 outside-help intro-available-funds-helper">
			<div class="alert alert-block clear">
				<p>
					Payment Terms have not been set up yet! Visit the <a href="/payments">Payment Center</a>
					or contact (877-654-WORK) a member of our sales team to add funds to your account.
					In the meantime you can create drafts or send assignments to internal users.
				</p>
			</div>
		</div>
	</c:otherwise>
</c:choose>

<div class="span3 tabbable tabs-left">
	<ul class="nav nav-tabs stacked-nav pricing">
		<li><a href="#flat-fee" data-toggle="tab" id="pricing-flat-fee" data-pricing="1">Flat Fee</a></li>
		<li><a href="#per-hour" data-toggle="tab" id="pricing-per-hour" data-pricing="2">Per Hour</a></li>
		<li><a href="#per-unit" data-toggle="tab" id="pricing-per-unit" data-pricing="3">Per Unit</a></li>
		<li><a href="#blended-per-hour" data-toggle="tab" id="pricing-blended-per-hour" data-pricing="4">Blended Per Hour</a></li>
		<li><a href="#internal" data-toggle="tab" id="pricing-internal" data-pricing="7">Internal</a></li>
	</ul>
</div>

<div class="span8">


<table class="tab-content" id="pricing-summary">

<tbody id="flat-fee" class="tab-pane active pricing_flat">
	<tr>
		<td colspan="3" class="text">
			<small class="muted">This assignment has a set amount you can spend regardless of the hours worked on the assignment.</small>
		</td>
	</tr>
	<tr>
		<td class="span3">Flat Fee</td>
		<td>
			<form:input path="flat_price" id="flat_price"  cssClass="span2"/>
		</td>
		<td class="help-inline">
			This is the price of the assignment
		</td>
	</tr>
	<c:if test="${assignment_pricing_type == 2}">
		<tr class="transaction-fee-col">
			<td>Transaction Fee</td>

			<td>
				$<span class="transaction-fee"></span>
			</td>
			<td class="help-inline">
				Fee paid to WorkMarket
			</td>
		</tr>
	</c:if>
	<tr>
		<td><strong>Total</strong></td>
		<td>
			<strong>$<span class="total">0</span></strong>
		</td>
	</tr>
</tbody>

<tbody id="per-hour" class="tab-pane hourly_pricing">
	<tr>
		<td colspan="3" class="text">
			<small class="muted">This contract assignment is priced with an hourly rate. Worker is being contracted for a set number of hours.</small>
		</td>
	</tr>
	<tr>
		<td class="span3">Hourly Rate</td>
		<td>
			<form:input path="per_hour_price" id="per_hour_price" cssClass="span2"/>
		</td>
		<td class="help-inline">
			This is the amount to be paid per hour for the assignment
		</td>
	</tr>
	<tr>
		<td>Hours Allowed</td>
		<td>
			<form:input path="max_number_of_hours" id="max_number_of_hours" cssClass="span2"/>
		</td>
		<td class="help-inline">
			Enter estimated hours spent for this assignment
		</td>
	</tr>
	<c:if test="${assignment_pricing_type == 2}">
		<tr class="transaction-fee-col">
			<td>Transaction Fee</td>
			<td>
				$<span class="transaction-fee"></span>
			</td>
			<td class="help-inline">
				Fee paid to WorkMarket
			</td>
		</tr>
	</c:if>
	<tr>
		<td><strong>Total</strong></td>
		<td>
			<strong>$<span class="total">0</span></strong>
		</td>
	</tr>
</tbody>

<tbody id="per-unit" class="tab-pane unit_pricing">
	<tr>
		<td colspan="3" class="text">
			<small class="muted">This contract assignment is priced with a per unit rate. Worker is being contracted for a set number of units of work.</small>
		</td>
	</tr>
	<tr>
		<td class="span3">Per Unit Rate</td>
		<td>
			<form:input path="per_unit_price" id="per_unit_price" cssClass="span2"/>
		</td>
		<td class="help-inline">
			This is the amount to be paid per unit for the assignment
		</td>
	</tr>
	<tr>
		<td>Per Unit Allowed</td>
		<td>
			<form:input path="max_number_of_units" id="max_number_of_units" cssClass="span2"/>
		</td>
		<td class="help-inline">
			Enter estimated units spent for this assignment
		</td>
	</tr>
	<c:if test="${assignment_pricing_type == 2}">
		<tr class="transaction-fee-col">
			<td>Transaction Fee</td>
			<td>
				$<span class="transaction-fee"></span>
			</td>
			<td class="help-inline">
				Fee paid to WorkMarket
			</td>
		</tr>
	</c:if>
	<tr>
		<td><strong>Total</strong></td>
		<td>
			<strong>$<span class="total">0</span></strong>
		</td>
	</tr>
</tbody>

<tbody id="blended-per-hour" class="tab-pane pricing_blended">
	<tr>
		<td colspan="3" class="text">
			<small class="muted">
				This contract assignment is priced with an initial hourly rate for a certain number of hours.
				Work beyond that, up to a max number of hours is paid at a secondary hourly rate.
			</small>
		</td>
	</tr>
	<tr>
		<td class="span3">Initial Hourly Rate</td>
		<td>
			<form:input path="initial_per_hour_price" id="initial_per_hour_price" cssClass="span2"/>
		</td>
		<td class="help-inline">
			This is the initial per hour rate paid for the assignment
		</td>
	</tr>
	<tr>
		<td>Initial Hours Allowed</td>
		<td>
			<form:input path="initial_number_of_hours" id="initial_number_of_hours" cssClass="span2"/>
		</td>
		<td class="help-inline">
			Enter estimated initial hours needed for this assignment
		</td>
	</tr>
	<tr>
		<td>Secondary Hourly Rate</td>
		<td>
			<form:input path="additional_per_hour_price" id="additional_per_hour_price" cssClass="span2"/>
		</td>
		<td class="help-inline">
			This is the secondary per hour rate paid for this assignment (if necessary)
		</td>
	</tr>
	<tr>
		<td>Secondary Hours Allowed</td>
		<td>
			<form:input path="max_blended_number_of_hours" id="max_blended_number_of_hours" cssClass="span2"/>
		</td>
		<td class="help-inline">
			Enter estimated secondary hours needed for this assignment
		</td>
	</tr>
	<c:if test="${assignment_pricing_type == 2}">
		<tr class="transaction-fee-col">
			<td>Transaction Fee</td>
			<td>
				$<span class="transaction-fee"></span>
			</td>
			<td class='info'>
				Fee paid to WorkMarket
			</td>
		</tr>
	</c:if>
	<tr>
		<td><strong>Total</strong></td>
		<td>
			<strong>$<span class="total">0</span></strong>
		</td>
	</tr>
</tbody>

<tbody id="internal" class="tab-pane internal_pricing">
	<tr>
		<td colspan="3" class="text">
			<small class="muted">
				The internal assignment function is designed to allow you to manage work with your internal employees.
				Assignments can be sent to anyone at your company whose profile is enabled to receive your assignments.
			</small>
		</td>
	</tr>
</tbody>
</table>

<c:if test="${assignment_pricing_type == 0 && is_subscription==false}">
	<div id="pricing_summary_outlet" class="form-stacked">

		<table>
			<thead>
				<tr>
					<th class="nowrap tac">Your Cost</th>
					<th class="nowrap tac">Transaction Fee</th>
					<th class="nowrap tac">Worker Nets</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="ginormous tac">$<span id="you-pay"></span></td>
					<td class="ginormous tac"><fmt:formatNumber value="${workFee}"/>%</td>
					<td class="ginormous tac">$<span id="resource-earns"></span></td>
				</tr>
			</tbody>
		</table>

		<p class="clearfix">
			<span class="pricing_switch_text fl">
				Fee structure: Paid by worker<br/> <small class="meta">Switch calculator to allocate fees to your account.</small>
			</span>
			<button type="button" class="pull-right button -small pricing_switch_trigger">
				Switch Calculator
			</button>
		</p>
	</div>
</c:if>

<div id="pricing-options">
	<table id="terms-summary">
		<tr>
			<c:choose>
				<c:when test="${payterms_available && show_payterms && not isModal}">
					<td class="span3">Payment Terms</td>
					<td class="terms">
						<form:select path="payment_terms_days" items="${form.paymentTermsDurations}" id="payment_terms_days"/>
						<small class="help-inline"><form:checkbox path="disablePriceNegotiation" /> Price is non-negotiable
							<span class="tooltipped tooltipped-n" aria-label="If checked, price counteroffers and spend limit increase requests are disabled.">
								<i class="wm-icon-question-filled"></i>
							</span>
						</small>
					</td>
				</c:when>
				<c:otherwise>
					<form:hidden path="payment_terms_days" id="payment_terms_days"/>
				</c:otherwise>
			</c:choose>
		</tr>
		<vr:rope>
			<vr:venue name="OFFLINE_PAY">
				<tr>
					<td class="span3">Offline Payment</td>
					<td>
						<small class="help-inline"><form:checkbox path="offlinePayment" /> Pay this assignment outside Work Market
							<span class="tooltipped tooltipped-n" aria-label="If checked, payment must be made outside of Work Market.">
								<i class="wm-icon-question-filled"></i>
							</span>
						</small>
					</td>
				</tr>
			</vr:venue>
		</vr:rope>
	</table>
</div>


</div>
</div>
