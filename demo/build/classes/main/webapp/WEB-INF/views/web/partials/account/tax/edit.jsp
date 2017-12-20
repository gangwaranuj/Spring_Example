<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script id="tmpl-tax-edit-step1" type="text/x-jquery-tmpl">

	<input type="hidden" name="return" value="${returnTo}">

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}"/>
	</c:import>

	<div id="tax_type_select">

		<div class="alert alert-info"><fmt:message key="account_taxes.if_you_earn_income"/>
			<strong><a href="http://www.irs.gov/pub/irs-pdf/fw9.pdf"><fmt:message key="account_taxes.irs_w9_instructions"/><i class="wm-icon-information-filled"></i></a></strong>
		</div>

		<fieldset class="form-horizontal">
			<div class="control-group">
				<label class="control-label"><fmt:message key="account_taxes.how_do_you_report"/></label>

				<div class="controls form">
					{{if business_flag}}
					<label>
						<input type="radio" name="business_flag" checked="checked" value="true" checked="true"/> <fmt:message key="account_taxes.as_a_business"/>
					</label>
					<label>
						<input type="radio" name="business_flag" value="false"/> <fmt:message key="account_taxes.as_an_individual"/>
					</label>
					{{else}}
					<label>
						<input type="radio" name="business_flag" value="true"/> <fmt:message key="account_taxes.as_a_business"/>
					</label>
					<label>
						<input type="radio" name="business_flag" checked="checked" value="false" checked="true"/><fmt:message key="account_taxes.as_an_individual"/>
					</label>
					{{/if}}
				</div>
			</div>

			<div class="control-group">
				<label class="control-label"><fmt:message key="account_taxes.which_country"/></label>

				<div class="controls">
					<select name="tax_country" id="country-select" {{if tax_country}}disabled="true"{{/if}}
					title="<fmt:message key="account_taxes.contact_to_change_country"/>">
					<option value="">- <fmt:message key="global.select"/> -</option>
					<option value="usa"
					{{if tax_country === 'usa'}}selected="selected"{{/if}}><fmt:message key="global.united_states"/></option>
					<option value="canada"
					{{if tax_country === 'canada'}}selected="selected"{{/if}}><fmt:message key="global.canada"/></option>
					<option value="other"
					{{if tax_country === 'other'}}selected="selected"{{/if}}><fmt:message key="global.other"/></option>
					</select>
				</div>
			</div>
		</fieldset>

		<div id="tax-edit-form"></div>
	</div>

</script>


<%-- Step 2 of form --------------------------------------------------------------------------------------------------%>

<script id="tmpl-tax-edit-step2" type="text/x-jquery-tmpl">

{{if country == 'usa'}}

<input type="hidden" name="country" value="usa">

{{if business_flag}}

<fieldset class="form-horizontal">
	<div class="control-group">
		<label class="control-label required" for="ein"><fmt:message key="account_taxes.employer_identification_number"/></label>

		<div class="controls" id="ein">
			<input class="input-xlarge" type="text" name="tax_number" maxlength="10" id="einField"
				   value="\${tax_number}" placeholder="99-9999999"/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="last_name"><fmt:message key="global.company_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="last_name" maxlength="255"
				   value="{{if last_name}}\${last_name}{{else}}\${business_name}{{/if}}"/>
		</div>
	</div>
</fieldset>

{{else}}

<fieldset class="form-horizontal">
	<div class="control-group">
		<label class="control-label required" for="ssn"><fmt:message key="account_taxes.social_security_number"/></label>

		<div class="controls" id="ssn">
			<input class="input-xlarge inspectletIgnore" type="text" name="tax_number" maxlength="11" id="ssnField"
				   value="\${tax_number}" placeholder="999-99-9999"/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="first_name"><fmt:message key="global.first_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="first_name" maxlength="255" value="\${first_name}"/>
			<span class="help-block"><fmt:message key="account_taxes.on_income_tax_return"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label" for="middle_name"><fmt:message key="global.middle_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="middle_name" maxlength="255" value="\${middle_name}"/>
			<span class="help-block"><fmt:message key="account_taxes.on_income_tax_return"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="last_name"><fmt:message key="global.last_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="last_name" maxlength="255" value="\${last_name}"/>
			<span class="help-block"><fmt:message key="account_taxes.on_income_tax_return"/></span>
		</div>
	</div>
</fieldset>

{{/if}}

<fieldset class="form-horizontal">
	<div class="control-group">
		<label class="control-label required" for="address"><fmt:message key="global.address"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="address" maxlength="255" value="\${address}"/>
			<span class="help-block"><fmt:message key="global.street_address"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="city"><fmt:message key="global.city"/></label>

		<div id="city" class="controls">
			<input class="input-xlarge" type="text" name="city" maxlength="255" class="xsmall" value="\${city}"/><br/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="state"><fmt:message key="global.state"/></label>

		<div class="controls">
			<select name="state" class="xsmall">
				<option value="">- <fmt:message key="global.state"/> -</option>
				<c:forEach var="state" items="${states}">
					<option value="${state.key}"
					{{if state === '${state.key}' }}selected="selected"{{/if}}><c:out value="${state.value}"/></option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="postal_code"><fmt:message key="global.zip_code"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="postal_code" id="postal_code" maxlength="5" class="small"
				   value="\${postal_code}"/>
		</div>
	</div>
</fieldset>

{{if business_flag}}

<fieldset class="form-horizontal">
	<div class="control-group">
		<label class="control-label required"><fmt:message key="account_taxes.federal_tax_classification"/></label>

		<div class="controls">
			<label>
				<input type="radio" name="tax_entity_type_code" value="individual" id="individual"
					   class="tax_entity_type"
				{{if tax_entity_type_code === "individual"}}checked="checked"{{/if}} />
				<fmt:message key="account_taxes.sole_proprietor"/>
			</label>
			<label>
				<input type="radio" name="tax_entity_type_code" value="c_corp" id="c-corp" class="tax_entity_type"
				{{if tax_entity_type_code === "c_corp"}}checked="checked"{{/if}} />
				<fmt:message key="account_taxes.c_corporation"/>
			</label>
			<label>
				<input type="radio" name="tax_entity_type_code" value="s_corp" id="s-corp" class="tax_entity_type"
				{{if tax_entity_type_code === "s_corp"}}checked="checked"{{/if}} />
				<fmt:message key="account_taxes.s_corporation"/>
			</label>
			<label>
				<input type="radio" name="tax_entity_type_code" value="partner" id="partner" class="tax_entity_type"
				{{if tax_entity_type_code === "partner"}}checked="checked"{{/if}}/>
				<fmt:message key="account_taxes.partnership"/>
			</label>
			<label>
				<input type="radio" name="tax_entity_type_code" value="trust" id="trust" class="tax_entity_type"
				{{if tax_entity_type_code === "trust"}}checked="checked"{{/if}} />
				<fmt:message key="account_taxes.trust_estate"/>
			</label>
			<label>
				<input type="radio" name="tax_entity_type_code" value="llc" id="llc-toggle" class="tax_entity_type"
				{{if tax_entity_type_code === 'llc-c-corp' || tax_entity_type_code === 'llc-s-corp' ||
				tax_entity_type_code ===
				'llc-part'}}checked="checked"{{/if}} />
				<fmt:message key="account_taxes.limited_liability_company"/>
			</label>
		</div>
		<br>

		<div class="control-group" id="llc_type">
			<label class="control-label required"><fmt:message key="account_taxes.enter_tax_classification"/></label>

			<div class="controls">
				<select name="tax_entity_type_code" class="tax_entity_type" disabled="true" style="small">
					<option value="">- <fmt:message key="global.select"/> -</option>
					<option
					{{if tax_entity_type_code === "llc-c-corp"}}selected="selected"{{/if}} value="llc-c-corp"><fmt:message key="account_taxes.c_corporation"/></option>
					<option
					{{if tax_entity_type_code === "llc-s-corp"}}selected="selected"{{/if}} value="llc-s-corp"><fmt:message key="account_taxes.s_corporation"/></option>
					<option
					{{if tax_entity_type_code === "llc-part"}}selected="selected"{{/if}}
					value="llc-part"><fmt:message key="account_taxes.partnership"/></option>
				</select>
			</div>
		</div>
</fieldset>

{{else}}

<fieldset class="form-horizontal">
	<div class="contol-group" id="tax_classification">
		<label class="control-label required"><fmt:message key="account_taxes.federal_tax_classification"/></label>

		<div class="controls">
			<label>
				<input type="checkbox" name="tax_entity_type_code" value="individual" id="individual"
					   class="tax_entity_type" checked="true"/><fmt:message key="account_taxes.individual_sole_proprietor"/>
			</label>
		</div>
	</div>
</fieldset>

{{/if}}


<div id="business-name">
	<label class="control-label"><fmt:message key="account_taxes.different_business_name"/></label>

	<div class="offset2">
		{{if business_name_flag}}
		<label class="checkbox inline"><input type="radio" name="business_name_flag" value="true" checked="true"/>
			<fmt:message key="global.yes"/></label>
		<label class="checkbox inline"><input type="radio" name="business_name_flag" value="false"/> <fmt:message key="global.no"/></label>
		{{else}}
		<label class="checkbox inline"><input type="radio" name="business_name_flag" value="true"/> <fmt:message key="global.yes"/></label>
		<label class="checkbox inline"><input type="radio" name="business_name_flag" value="false" checked="true"/>
			<fmt:message key="global.no"/></label>
		{{/if}}
	</div>


	<fieldset class="form-horizontal">
		<br/>

		<div id="business-name-input" class="control-group dn">
			<label class="control-label required"><fmt:message key="account_taxes.business_name_disregarded_entity_name"/></label>

			<div class="controls">
				<input class="input-xlarge" type="text" name="business_name" maxlength="255" value="\${business_name}"/>
				<span class="help-block"><fmt:message key="global.if_different"/></span>
			</div>
		</div>
	</fieldset>
</div>
<fieldset class="form-horizontal">
	<div id="effective_date" class="control-group dn">
		<label class="control-label"><strong><fmt:message key="global.effective_date"/></strong></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="effective_date_string" maxlength="16"
				   placeholder="<fmt:message key="global.enter_date"/>"/>
			<span class="help-block"><fmt:message key="account_taxes.irs_change_date"/></span>
		</div>
	</div>
</fieldset>

<div class="alert alert-info">
	<div class="inputs-list" style="margin-left:20px">
		<label for="delivery-policy-flag" style="pl">
			<input type="checkbox" name="delivery_policy_flag" id="delivery-policy-flag" value="true"
				   style="margin-left:-20px"/>
			<fmt:message key="account_taxes.agree_to_communications"/></label>
	</div>
</div>

<div class="wm-action-container">
	{{if tax_number === null}} <%-- if it's null it hasn't been saved yet --%>
	<button type="button" class="button" id="save-tax-form-btn"><fmt:message key="global.submit"/></button>
	{{else}}
	<button type="button" class="button" id="cancel-edit-tax-form-btn"><fmt:message key="global.cancel"/></button>
	<button type="button" class="button" id="save-tax-form-btn"><fmt:message key="global.confirm_and_sign"/></button>
	{{/if}}
</div>
</fieldset>


<%-- CANADA ----------------------------------------------------------------------------------------------------------%>

{{else country === 'canada'}}

<input type="hidden" name="country" value="CAN"/>

{{if business_flag}}

<fieldset class="form-horizontal">
	<div class="control-group">
		<label class="control-label required" for="bn"><fmt:message key="account_taxes.business_number"/></label>

		<div id="bin" class="controls">
			<input class="input-xlarge" type="text" name="tax_number" maxlength="17"
			       value="\${tax_number}" placeholder="999999999-XX-9999"/>
			<span class="help-block"><fmt:message key="account_taxes.assigned_by_cra"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="tax_name"><fmt:message key="global.company_name"/></label>

		<div id="tax_name" class="controls">
			<input class="input-xlarge" type="text" name="tax_name" maxlength="255" value="\${tax_name}"/>
		</div>
	</div>
</fieldset>

{{else}}

<fieldset class="form-horizontal">
	<div class="control-group">
		<label class="control-label required" for="sin"><fmt:message key="account_taxes.social_insurance_number"/></label>

		<div id="sin" class="controls">
			<input class="input-xlarge" type="text" name="tax_number" maxlength="11"
			       value="\${tax_number}" placeholder="999-999-999"/>
			<span class="help-block"><fmt:message key="account_taxes.on_sin_card"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="first_name"><fmt:message key="global.first_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="first_name" maxlength="255" value="\${first_name}"/>
			<span class="help-block"><fmt:message key="account_taxes.on_income_tax_return"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label" for="middle_name"><fmt:message key="global.middle_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="middle_name" maxlength="255" value="\${middle_name}"/>
			<span class="help-block"><fmt:message key="account_taxes.on_income_tax_return"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="last_name"><fmt:message key="global.last_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="last_name" maxlength="255" value="\${last_name}"/>
			<span class="help-block"><fmt:message key="account_taxes.on_income_tax_return"/></span>
		</div>
	</div>
</fieldset>

<p>
	<fmt:message key="account_taxes.declaring_true_information"/>
</p>
{{/if}}

<fieldset class="form-horizontal">
	<div class="control-group">
		<label class="control-label required" for="address"><fmt:message key="global.address"/></label>

		<div id="address" class="controls">
			<input name="address" class="input-xlarge" type="text" maxlength="255" value="\${address}"/>
			<span class="help-block"><fmt:message key="global.street_address"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="city"><fmt:message key="global.city"/></label>

		<div class="controls">
			<input name="city" maxlength="255" class="input-xlarge" type="text" value="\${city}"/><br/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="state"><fmt:message key="global.province"/></label>

		<div class="controls">
			<select name="state" class="xsmall">
				<option value="">- <fmt:message key="global.province"/> -</option>
				<c:forEach var="province" items="${provinces}">
					<option value="${province.key}"
					{{if state === '${province.key}'}}selected="selected"{{/if}}><c:out value="${province.value}"/></option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="postal_code"><fmt:message key="global.postal_code"/></label>

		<div class="controls" id="canada-postal-code">
			<input name="postal_code" maxlength="7" class="small" type="text" value="\${postal_code}"/>
		</div>
	</div>

	<div class="control-group">
		<div class="controls">
			<button type="button" class="button" id="save-tax-form-btn"><fmt:message key="global.submit"/></button>
		</div>
	</div>
</fieldset>

<%-- OTHER ----------------------------------------------------------------------------------------------------------%>

{{else country == 'other'}}

{{if business_flag}}

<fieldset class="form-horizontal">
	<div class="control-group">
		<label class="control-label" for="tax_name"><fmt:message key="global.company_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="tax_name" maxlength="255" value="\${tax_name}"/>
			<span class="help-block"><fmt:message key="account_taxes.on_income_tax_return"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="country-of-incorporation"><fmt:message key="account_taxes.country_of_incorporation"/></label>

		<div class="controls" id="country-of-incorporation" value="\${tax_number}">
			<select name="country_of_incorporation" maxlength="255">
				<option value="">- <fmt:message key="global.country"/> -</option>
				<c:forEach var="country" items="${allCountries}">
					<option value="${country.key}"
					{{if country_of_incorporation === '${country.key}'}}selected="selected"{{/if}}><c:out
						value="${country.value}"/></option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required"><fmt:message key="account_taxes.type_of_beneficial_owner"/></label>

		<div class="controls">
			<label>
				<input type="radio" name="tax_entity_type_code" value="corp" id="corporation"
					   class="tax_entity_type" {{if tax_entity_type_code === 'corp'}}checked="checked"{{/if}}/>
				<fmt:message key="account_taxes.corporation"/>
			</label>
			<label>
				<input type="radio" name="tax_entity_type_code" value="llc-dis" id="disregarded"
					   class="tax_entity_type" {{if tax_entity_type_code === 'llc-dis'}}checked="checked"{{/if}}/>
				<fmt:message key="account_taxes.disregarded_entity"/>
			</label>
			<label>
				<input type="radio" name="tax_entity_type_code" value="partner" id="partnership"
					   class="tax_entity_type" {{if tax_entity_type_code === 'partner'}}checked="checked"{{/if}}/>
				<fmt:message key="account_taxes.partnership"/>
			</label>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="address"><fmt:message key="account_taxes.permanent_residence_address"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="address" maxlength="255" value="\${address}"/>
			<span class="help-block"><fmt:message key="global.street_address_or_rural_route"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="city"><fmt:message key="global.city_town"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="city" maxlength="255" value="\${city}"/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="state"><fmt:message key="global.state_or_province"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="state" maxlength="16" value="\${state}"/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="postal_code"><fmt:message key="global.postal_code"/></label>

		<div class="controls">
			<input class="small" type="text" name="postal_code" maxlength="16" value="\${postal_code}"/>
		</div>
	</div>


	<div class="control-group">
		<label class="control-label required" for="foreign_tax_identifier"><fmt:message key="account_taxes.national_id"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="tax_number" id="foreign_tax_identifier" value="\${tax_number}" />
			<span id="foreign-tax-identifier-modal"></span>
		</div>
	</div>
</fieldset>


{{else}}


<p><fmt:message key="account_taxes.please_fill_in_information"/></p>

<fieldset class="form-horizontal">
	<div class="control-group">
		<label class="control-label required" for="first_name"><fmt:message key="global.first_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="first_name" maxlength="255" value="\${first_name}"/>
			<span class="help-block"><fmt:message key="account_taxes.on_income_tax_return"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label" for="middle_name"><fmt:message key="global.middle_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="middle_name" maxlength="255" value="\${middle_name}"/>
			<span class="help-block"><fmt:message key="account_taxes.on_income_tax_return"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="last_name"><fmt:message key="global.last_name"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="last_name" maxlength="255" value="\${last_name}"/>
			<span class="help-block"><fmt:message key="account_taxes.on_income_tax_return"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label"><fmt:message key="account_taxes.type_of_beneficial_owner"/></label>

		<div class="controls">
			<label>
				<input type="radio" name="tax_entity_type_code" value="individual" class="tax_entity_type"
					   checked="true"/>
				<fmt:message key="account_taxes.individual"/>
			</label>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="address"><fmt:message key="account_taxes.permanent_residence_address"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="address" maxlength="255" value="\${address}"/>
			<span class="help-block"><fmt:message key="global.street_address_or_rural_route"/></span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="city"><fmt:message key="global.city_town"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="city" maxlength="255" value="\${city}"/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="state"><fmt:message key="global.state_or_province"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="state" maxlength="16" value="\${state}"/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="postal_code"><fmt:message key="global.postal_code"/></label>

		<div class="controls">
			<input class="small" type="text" name="postal_code" maxlength="16" value="\${postal_code}"/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required" for="foreign_tax_identifier"><fmt:message key="account_taxes.national_id"/></label>

		<div class="controls">
			<input class="input-xlarge" type="text" name="tax_number" id="foreign_tax_identifier" value="\${tax_number}" />
			<span id="foreign-tax-identifier-modal"></span>
		</div>
	</div>

	<div class="well">
		<h4><fmt:message key="account_taxes.foreign_status_certification"/></h4>

		<p><fmt:message key="account_taxes.if_not_us_citizen"/></p>

		<p><fmt:message key="account_taxes.declare_under_penalty"/></p>

		<ul>
			<li><fmt:message key="account_taxes.not_us_citizen"/></li>
			<li><fmt:message key="account_taxes.i_perform_all_labour"/></li>
			<li><fmt:message key="account_taxes.meets_tax_exemption_qualifications"/></li>
		</ul>

		<ul class="inputs-list">
			<li>
				<label><input type="checkbox" name="foreign_status_accepted_flag" value="true" {{if
					foreign_status_accepted_flag}}checked="checked"{{/if}}/><fmt:message key="account_taxes.accept_foreign_status_certification"/></label>
			</li>
		</ul>
	</div>
</fieldset>
{{/if}}

<div class="wm-action-container">
	<button type="button" class="button" id="save-tax-form-btn"><fmt:message key="global.submit"/></button>
</div>

{{/if}}
</script>
