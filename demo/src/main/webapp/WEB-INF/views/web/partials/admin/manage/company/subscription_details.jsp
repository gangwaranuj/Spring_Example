<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form:form id="subscription_form" class="form-horizontal" modelAttribute="subscription" action="/admin/manage/company/submit_subscription" method="post">
	<wm-csrf:csrfToken />
	<input type="hidden" name="companyId" value="<c:out value="${requestScope.id}"/>">
	<input type="hidden" name="subscriptionConfigurationId" value="<c:out value="${subscription_id}"/>">

	<div id="messages"></div>

	<div class="control-group">
		<label class="control-label required">Effective Date</label>
		<div class="controls">
			<form:select cssClass="span3" id="effective_date_month" path="effectiveDateMonth" data-constraints='@Selected(message="Must select a valid month")'>
				<form:option value="" label="- Month -" />
				<form:option value="0" label="January" />
				<form:option value="1" label="February" />
				<form:option value="2" label="March" />
				<form:option value="3" label="April" />
				<form:option value="4" label="May" />
				<form:option value="5" label="June" />
				<form:option value="6" label="July" />
				<form:option value="7" label="August" />
				<form:option value="8" label="September" />
				<form:option value="9" label="October" />
				<form:option value="10" label="November" />
				<form:option value="11" label="December" />
			</form:select>

			<%-- Filled with JavaScript --%>
			<form:select cssClass="span2" id="effective_date_year" path="effectiveDateYear" data-constraints='@Numeric(label="Year")'>
				<form:option value="${subscription.effectiveDateYear}"/>
			</form:select>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required">Signed Date</label>
		<div class="controls">
			<form:input id="signedDate" path="signedDate"/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required">Payment Period</label>
		<div class="controls">
			<form:select path="subscriptionPeriod" id="subscription_period" data-constraints='@Selected(message="Must select a payment period")' cssClass="span3">
				<form:option value="0" label="- Select -"/>
				<form:option value="1" label="Monthly" />
				<form:option value="3" label="Quarterly" />
				<form:option value="6" label="Semiannually" />
				<form:option value="12" label="Annually" />
			</form:select>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required">Term (months)</label>
		<div class="controls">
			<form:input type="text" cssClass="span1" path="numberOfMonths" id="number_of_months" data-constraints='@MultipleOfPeriod(label="Term")'/>
			<em>Subscription will terminate on: <span id="termination_date"></span></em>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required">Payment Terms</label>
		<div class="controls">
			<form:input type="text" id="paymentTermsDays" path="paymentTermsDays" cssClass="span1" data-constraints='@Min(value=30, label="Payment Terms")'/> days
		</div>
	</div>

	<div class="control-group">
		<label class="control-label">Service Type</label>
		<div class="controls" id="service_type_configurations">
			<%-- Filled with JavaScript --%>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required">Pricing Ranges</label>
		<div class="controls">
			<div class="form-horizontal well tier-form" id="subscription_tier_form">
				<div class="transaction-fee-ranges-title">
					<div class="item">Lower Bound Throughput</div>
					<div class="item">Upper Bound Throughput</div>
					<div class="item">Payment Period Amount</div>
					<div class="item vendor-of-record">Vendor of Record Amount</div>
				</div>
			</div>
			<div class="row dn">
				<label class="span4 required">Effective Date</span>
				<form:input id="paymentTierEffectiveDate" path="paymentTierEffectiveDate" data-constraints='@EditEffectiveDate(label="Effective Date", date="${nextPossibleUpdateDate}")'/>
			</div>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required">Subscription Type</label>
		<div class="controls">
			<div class="input-prepend">
				<form:select path="subscriptionTypeCode" id="subscription-type" items="${subscriptionTypes}" cssClass="span3"/>
			</div>

			<div class="input-append">
				<form:input path="blockTierPercentage" id="block-tier-percentage" cssClass="input-small"/>			
				<span class="add-on">%</span>
			</div>
		</div>
	</div>


	<div class="control-group">
		<label class="control-label">Client Ref ID</label>
		<div class="controls">
			<div class="input-prepend">
				<form:input path="clientRefId" maxlength="50"/>
			</div>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">Discount Options</label>
		<div class="controls">
			<div class="options-div row">
				<label class="radio inline span1">
					<form:radiobutton path="hasDiscountOptions" value="yes"/> Yes
				</label>
				<label class="radio inline span1">
					<form:radiobutton path="hasDiscountOptions" value="no"/> No
				</label>
			</div>
			<div id="discountOptions">
				<div class="row">
					<label class="span4 period-label required">Number of Payment Period(s)</label>
					<div class="span2">
						<form:select cssClass="span2" path="discountNumberOfPeriods" data-constraints='@Selected(label="Number of Payment Periods")'>
							<form:option value="0" label="- Select -"/>
							<form:option value="1" label="1"/>
							<form:option value="2" label="2"/>
							<form:option value="3" label="3"/>
							<form:option value="4" label="4"/>
							<form:option value="5" label="5"/>
							<form:option value="6" label="6"/>
						</form:select>
					</div>
				</div>
				<div class="row">
					<label class="span4">Discount per Period</label>
					<div class="span3">
						<div class="input-prepend">
							<span class="add-on">$</span>
							<form:input type="text" cssClass="input-small" path="discountPerPeriod" data-constraints='@PaymentAmountRange(lower=0, upper=100000000, label="Discount per Period")'/>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required">Set Up Fee</label>
		<div class="controls">
			<div class="input-prepend">
				<span class="add-on">$</span>
				<form:input path="setUpFee" class="input-small currency" data-constraints='@SetupFee(label="Set Up Fee")'/>
			</div>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label">Auto Renewal</label>
		<div class="controls">
			<form:select path="autoRenewal">
				<form:option value="0">None</form:option>
				<form:option value="1">1 Renewal</form:option>
				<form:option value="2">2 Renewals</form:option>
				<form:option value="3">3 Renewals</form:option>
				<form:option value="4">4 Renewals</form:option>
			</form:select>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label">Cancellation Option</label>
		<div class="controls">
			<form:textarea path="cancellationOption" cssClass="subscription-textarea" placeholder="Add cancellation option details (Penalty payment)"/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label">Additional monthly recurring charge</label>
		<div class="controls">
			<div class="options-div">
				<div class="row">
					<label class="radio inline span1">
						<form:radiobutton path="hasAddOns" value="yes"/> Yes
					</label>
					<label class="radio inline span1">
						<form:radiobutton path="hasAddOns" value="no"/> No
					</label>
				</div>
				<div id="AddOnsOptions">
					<%-- Filled with JavaScript --%>
				</div>
			</div>
			<div class="control-group dn">
				<span class="required control-label">Effective Date</span>
				<div class="controls">
					<form:input id="addOnsEffectiveDate" path="addOnsEffectiveDate" data-constraints='@EditEffectiveDate(label="Effective Date", date="${nextPossibleUpdateDate}")'/>
				</div>
			</div>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label">Additional Notes</label>
		<div class="controls">
			<form:textarea path="additionalNotes" cssClass="subscription-textarea" placeholder="Enter additional notes"/>
		</div>
	</div>

	<hr/>

	<div class="wm-action-container submit_pricing_btns">
		<button type="button" class="button" id="submit_subscription">Submit for approval</button>
		<button type="button" class="button" id="save_subscription_form">Save</button>
	</div>
</form:form>

<script type="text/x-jquery-tmpl" id="subscription_tier_template">
<div class="subscription-tier control-group">
	<label>Tier \${nTier}</label>
	<div class="input-prepend">
		<span class="add-on">$</span>
		<input type="text" class="input-small" readOnly="readOnly" name="pricingRanges[\${idx}].minimum" value="0">
	</div>
	<div class="input-prepend">
		<span class="add-on">$</span>
		<input type="text" class="input-small subscription-tier-upper" name="pricingRanges[\${idx}].maximum" readonly="readonly" placeholder="infinity">
	</div>
	<div class="input-prepend">
		<span class="add-on">$</span>
		<input type="text" class="input-small" name="pricingRanges[\${idx}].paymentAmount" value="0" data-constraints='@PaymentAmountRange(lower=0, upper=100000000, label="Payment Period Amount")'>
	</div>
	<div class="input-prepend vendor-of-record">
		<span class="add-on">$</span>
		<input type="text" class="input-small" name="pricingRanges[\${idx}].vendorOfRecordAmount" value="0" data-constraints='@PaymentAmountRange(lower=0, upper=100000000, label="Vendor of Record Amount")'>
	</div>

	<div class="tier-actions">
		<button type="button" class="button add-tier-btn">Add Tier</button>
		<span class="dn remove"><a>Remove</a></span>
	</div>
</div>
</script>

<script type="text/x-jquery-tmpl" id="add_ons_template">
	<div class="addOnItem row">
		<div class="span3">
			<select name="subscriptionAddOnDTOs[\${idx}].addOnTypeCode" data-constraints='@Selected(label="Add-on type")'>
				<option value="">- Select -</option>
				<c:forEach items="${addOnTypes}" var="addOnType">
					<option value="${addOnType.code}"><c:out value="${addOnType.description}"/></option>
				</c:forEach>
			</select>
		</div>
		<label class="span3">Cost per Period</label>
		<div class="span3 addOnCost">
			<div class="input-prepend">
				<span class="add-on">$</span>
				<input type="text" class="input-small" name="subscriptionAddOnDTOs[\${idx}].costPerPeriod" data-constraints='@PaymentAmountRange(lower=0, upper=100000000, label="Add-on Cost per Period")'>
			</div>
		</div>

		<div class="addon-actions span3">
			<button type="button" class="button -small add-addons-btn">+ Add</button>
			<span class="dn remove"><a>Remove</a></span>
		</div>
	</div>
</script>

<script type="text/x-jquery-tmpl" id="service_type_template">
	<div class="control-group serviceTypeConfig">
		<label>
			Country:
			<select class="span3 country" name="accountServiceTypeDTOs[\${idx}].countryCode">
				<c:forEach var="country" items="${countries}">
					<option value="${country.id}"><c:out value="${country.name}" /></option>
				</c:forEach>
			</select>
		</label>

		<label>
			Service Type:
			<select class="span3" name="accountServiceTypeDTOs[\${idx}].accountServiceTypeCode">
				<c:forEach var="serviceType" items="${account_service_types}">
					<option value="${serviceType.code}"><c:out value="${serviceType.description}" /></option>
				</c:forEach>
			</select>
		</label>

		<div class="service-actions">
			<button type="button" class="button -small add-btn">+ Add</button>
			<span class="dn remove"><a>Remove</a></span>
		</div>
	</div>
</script>