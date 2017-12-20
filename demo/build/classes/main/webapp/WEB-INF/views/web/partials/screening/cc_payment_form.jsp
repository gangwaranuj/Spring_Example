<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<fieldset>
	<legend>Credit Card: <small class="meta">Please provide your credit card details for payment below.</small></legend>
	<div class="control-group">
		<form:label path="cardType" for="card_type" cssClass="required control-label">Card Type</form:label>
		<div class="controls">
			<form:select path="cardType" id="card_type">
				<form:option value="">- Select -</form:option>
				<form:option value="visa">Visa</form:option>
				<form:option value="mastercard">MasterCard</form:option>
				<form:option value="amex">American Express</form:option>
			</form:select>
			<span class="help-block"><br><img width="324" height="23" alt="Accepted Payments" src="${mediaPrefix}/images/cc-accepted.png"></span>
		</div>
	</div>

	<div class="control-group">
		<form:label path="firstNameOnCard" for="first_name" cssClass="required control-label">First Name</form:label>
		<div class="controls"><form:input maxlength="50" value="" path="firstNameOnCard" id="first_name"/></div>
	</div>

	<div class="control-group">
		<form:label path="lastNameOnCard" for="last_name" cssClass="required control-label">Last Name</form:label>
		<div class="controls"><form:input maxlength="50" value="" path="lastNameOnCard" id="last_name"/></div>
	</div>

	<div class="control-group">
		<form:label path="cardNumber" for="card_number" class="required control-label">Card Number and CVV</form:label>
		<div class="controls">
			<form:input autocomplete="off" maxlength="16" value="" path="cardNumber" id="card_number" />
			<form:input autocomplete="off" cssClass="span1" maxlength="4" size="4" value="" path="cardSecurityCode" id="card_security_code" />
			<img width="32" height="23" style="vertical-align:middle;" alt="CVV" id="help_cvv" src="${mediaPrefix}/images/icons/credit_card-security_code.png">
		</div>
	</div>

	<div class="control-group">
		<form:label path="cardExpirationMonth" for="card_expiration_month" class="required control-label">Expiration</form:label>
		<div class="controls">
			<form:select cssClass="span2" path="cardExpirationMonth" id="card_expiration_month">
				<form:option value="">- Month -</form:option>
				<c:forEach begin="1" end="9" var="month">
					<form:option value="0${month}">${month}</form:option>
				</c:forEach>
				<c:forEach begin="10" end="12" var="month">
					<form:option value="${month}">${month}</form:option>
				</c:forEach>
			</form:select>
			<form:select cssClass="span2" path="cardExpirationYear" id="card_expiration_year">
				<form:option value="">- Year -</form:option>
				<c:forEach begin="2014" end="2027" var="year">
					<form:option value="${year}">${year}</form:option>
				</c:forEach>
			</form:select>
		</div>
	</div>
</fieldset>

<fieldset>
	<legend>Billing Address</legend>

	<input type="hidden" name="addressTypeCode" value="profile" />

	<c:if test="${!notIntlRequirement}">
		<div class="control-group">
			<label class="required control-label" for="country">Country</label>
			<div class="controls">
				<form:select path="country" id="country">
					<form:option value=""> - Select - </form:option>
					<form:options items="${countries}" />
				</form:select>
			</div>
		</div>
	</c:if>

	<c:if test="${notIntlRequirement}">
		<div class="control-group">
			<form:label path="country" for="country" cssClass="required control-label">Country</form:label>
			<div class="controls">
				<form:select path="country" id="country">
					<form:option value="USA">United States</form:option>
				</form:select>
			</div>
		</div>
	</c:if>

	<div class="control-group">
		<form:label path="address1" for="address1" class="required control-label">Street 1</form:label>
		<div class="controls">
			<form:input maxlength="255" value="" path="address1" id="address1" />
		</div>
	</div>

	<div class="control-group">
		<form:label path="address2" for="address2" class="control-label">Street 2</form:label>
		<div class="controls">
			<form:input maxlength="255" value="" path="address2" id="address2"/>
		</div>
	</div>

	<div class="control-group">
		<form:label path="city" for="city" class="required control-label">City</form:label>
		<div class="controls">
			<form:input maxlength="255" value="" path="city" id="city"/>
		</div>
	</div>

	<c:if test="${!notIntlRequirement}">
		<div class="control-group">
			<form:label path="state" cssClass="control-label" for="state" >State/Province</form:label>
			<div class="controls">
				<form:input id="state" path="state" />
			</div>
		</div>
	</c:if>

	<c:if test="${notIntlRequirement}">
		<div class="control-group">
			<form:label path="state" cssClass="required control-label" for="state">State</form:label>
			<div class="controls">
				<form:select id="state" path="state">
					<form:option value="">- Select -</form:option>
					<form:options items="${requestScope.states}"/>
				</form:select>
			</div>
		</div>
	</c:if>

	<div class="control-group">
		<form:label path="postalCode" for="postal_code" cssClass="control-label">Postal Code</form:label>
		<div class="controls">
			<form:input cssClass="span2" maxlength="7" value="" path="postalCode" id="postal_code"/>
		</div>
	</div>

</fieldset>

<div class="dn">
	<div id="help_cvv_content">
		<img src='${mediaPrefix}/images/cvv.jpg' alt='Security code' height='266' width='251'/>
	</div>
</div>
