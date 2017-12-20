<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div class="inner-container form-horizontal">
	<sf:form action='/funds/addcc' method="POST" modelAttribute="addCreditCardForm">
		<wm-csrf:csrfToken />
		<div class="messages"></div>
		<div id="cc_step1" class="dn">
			<div class="alert alert-info">
				<strong>Step 2: Enter in your payment details</strong>
				<p>Funding via credit card incurs a merchant fee, added to the amount you add to your account. Credit card processing is handled via <a href="http://www.firstdata.com/en_us/home.html" target="_blank">First Data</a>.</p>
			</div>

			<span id="project-allocation-title" class="dn"><strong>Reserve for Projects or Keep Unreserved Cash</strong></span>

			<div class="control-group" id="add_funds_0">
				<label class="control-label" for="amount" class="required">Amount to Add:</label>
				<div class="controls">
					<div class="input-prepend">
						<span class="add-on">$</span>
						<sf:input path="amount" value="${amount}" id="amount" maxlength="10" class="span2"/> USD
						<c:if test="${doesCompanyHaveReservedFundsEnabledProject}">
							<span class="help-block"><a class="add_more" onclick="javascript:add_allocate_dropdown();">Reserve for projects</a></span>
						</c:if>
					</div>
					<input id="percentage" type="hidden" value="0.00">
				</div>
			</div>

			<div class="control-group">
				<label for="card_type" class="control-label required">Credit Card Type:</label>
				<div class="controls">
					<sf:select id="card_type" name="card_type" path="card_type" value="${card_type}">
						<%-- TODO: move this to invariant data service and/or populate via the controller--%>
						<option name="" value="">- Select -</option>
						<sf:option value="visa" label="Visa"/>
						<sf:option value="mastercard" label="MasterCard"/>
						<sf:option value="amex" label="American Express"/>
					</sf:select>
					<img src="${mediaPrefix}/images/cc-accepted.png" alt="Accepted Payments" width="324" height="23"/>
				</div>
			</div>

			<div class="control-group">
				<label class="control-label">Merchant Fee:</label>
				<div class="input mt6 controls">
					<span id="cc_fee"></span>
					<span class="help-block">
						<a href="https://workmarket.zendesk.com/hc/en-us/articles/210052757-How-do-I-fund-my-Work-Market-account" target="_blank">Why is there a merchant fee?</a>
					</span>
				</div>
			</div>

			<div class="control-group">
				<label class="control-label strong">Amount Charged to Card:</label>
				<div class="controls currency-control">
					<strong><span id="calc_total">$0</span></strong>
				</div>
			</div>

			<div class="wm-action-container">
				<button id="btn-addcc-back1" type="button" class="button" >Back</button>
				<button type="button" class="button" id="cc-step1-next" disabled="disabled">Next</button>
			</div>
		</div>


		<div id="cc_step2" class="dn">
			<div class="alert alert-info">
				<strong>Step 3: Enter in your billing information</strong>
			</div>
			<c:if test='${(not empty company_address || not empty profile_address)}'>
				<div class="clearfix input">
					<c:if test="${has_company_address}">
						<input id="use_company_address" type="checkbox" class="address_type">
						<span>Use my company address</span>
					</c:if>
					<c:if test="${has_profile_address}">
						<input id="use_profile_address" type="checkbox" class="address_type">
						<span>Use my personal address</span>
					</c:if>
				</div>
			</c:if>
			<div class="control-group">
				<label for="address1" class="control-label required">Address line 1:</label>
				<div class="controls">
					<sf:input path="address1" value="${address1}" cssClass="address_field" id='address1' maxlength='255' size='30'/>
				</div>
			</div>
			<div class="control-group">
				<label for="address2" class="control-label">Address line 2:</label>
				<div class="controls">
					<sf:input path="address2" value="${address2}" cssClass="address_field" id='address2' maxlength='255' size='30'/>
				</div>
			</div>
			<div class="control-group">
				<label for="city" class="control-label required">City:</label>
				<div class="controls">
					<sf:input path="city" value="${city}" cssClass="address_field" id='city' maxlength='255'/>
				</div>
			</div>
			<div class="control-group">
				<label for="state" class="control-label required">State/Province:</label>

				<div class="controls">
					<sf:select path="state" value="${state}" cssClass="address_field" id="state">
						<option value="">- Select -</option>
						<c:forEach var="country" items="${statesCountries}">
							<optgroup label="${country.key}">
								<c:forEach var="state" items="${country.value}">
									<sf:option value="${state.value}" label="${state.key}"/>
								</c:forEach>
							</optgroup>
						</c:forEach>
					</sf:select>
				</div>
			</div>
			<div class="control-group">
				<label class="required control-label">Postal Code:</label>
				<div class="controls">
					<sf:input path="postalCode" value="${postalCode}" cssClass="address_field" maxlength="7" id="postal_code"/>
				</div>
			</div>
			<p><strong>Card Information</strong></p>

			<div class="control-group">
				<label for="first_name" class="control-label required">First Name:</label>
				<div class="controls">
					<sf:input path="first_name" value="${firstName}" id='first_name' maxlength="255"/>
				</div>
			</div>

			<div class="control-group">
				<label for="last_name" class="control-label required">Last Name:</label>
				<div class="controls">
					<sf:input path="last_name" value="${lastName}" id='last_name' maxlength="255"/>
				</div>
			</div>

			<div class="control-group">
				<label class="required control-label">Card Number:</label>
				<div class="controls">
					<sf:input path="card_number" value="${cardNumber}" maxlength="16" class="" autocomplete="off"/>
				</div>
			</div>

			<div class="control-group">
				<label class="required control-label">Security Code:</label>
				<div class="controls">
					<sf:input path="card_security_code" value="${cardSecurityCode}" maxlength="4" size='4' autocomplete='off' style='width: 50px;'/>
					<a class="tooltip-info tooltipped tooltipped-n" aria-label="This is the 3 digit security code located on the back right of your Visa/ Mastercard or the 4 digit code located on the front fright of your Amex card."><i class="wm-icon-question-filled"></i></a>
				</div>
			</div>

			<div class="control-group">
				<label class="required control-label">Expiration:</label>
				<div class="controls">
					<sf:select path="card_expiration_month" value="${card_expiration_month}" class="span2">
						<option name="" value="">- Month -</option>
						<c:forEach var="month" begin="1" end="12">
							<option value='<fmt:formatNumber minIntegerDigits="2" value="${month}" />'>${month}</option>
						</c:forEach>
					</sf:select>
					<sf:select path="card_expiration_year" value="${card_expiration_year}" class="span2">
						<option name="" value="">- Year -</option>

						<c:forEach var="year" begin="${wmfn:getCurrentYear()}" end="${wmfn:getCurrentYear() + 10}">
							<sf:option name="${year}" value="${year}"/>
						</c:forEach>
					</sf:select>
				</div>
			</div>
			<div class="wm-action-container">
				<button id="btn-addcc-back2" type="button" class="button" >Back</button>
				<button type="submit reset" class="button" id="add-funds-cc">Add Funds</button>
			</div>
		</div>

	</sf:form>
</div>

<div class="dn">
	<div id="help_cvv_content">
		<img src='${mediaPrefix}/images/cvv.jpg' alt='Security code' height='266' width='251'/>
	</div>
</div>

<script id="tmpl-allocate-dropdown" type="text/x-jquery-tmpl">
	<div class="clearfix" id="add_funds_\${dropdown_id}">
		<label for='amount' class="required">Amount to Add:</label>
		<div class="input">
			<div class="input-prepend">
				<span class="add-on">$</span>
				<input id="add_funds_amount_\${dropdown_id}" onkeyup="javascript:calc_cc_amount();" name="project_amount[\${dropdown_id}]" maxlength="10" class="span2"/> USD
				<select id="add_funds_project_\${dropdown_id}" name="project_id[\${dropdown_id}]" onchange="javascript:calc_cc_amount();">
					<option name="" value="">- Select -</option>
					<option value="general_cash" label="Unreserved Cash"></option>
					<c:forEach items="${project_list}" var="project">
						<option value="${project.key}" label="${project.value}"></option>
					</c:forEach>
				</select>
				<a id="add_funds_add_more_\${dropdown_id}" class="add_more"  onclick="javascript:add_allocate_dropdown();">Add another</a>
				<a id="add_funds_remove_\${dropdown_id}" class="dn" onclick="javascript:remove_allocate_dropdown(\${dropdown_id});"><i class="wm-icon-trash icon-inline"></i></a>
			</div>
			<input id="add_funds_percentage_\${dropdown_id}" type="hidden" value="0.00">
		</div>
	</div>
</script>
