<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="control-group">
	<form:errors path="postalCode" cssClass="errorMessage span5"/>
	<div class="controls">
		<form:input path="addressTyper" type="text" placeholder="Your Location" size="500" id="addressTyper" name="addressTyper"/>
		<c:choose>
			<c:when test="${(signupForm.registrationType == 'managelabor')}">
				<small class="help-block-client">This is your company address.</small>
			</c:when>
			<c:otherwise>
				<small>This is the location where you can do work.</small>
			</c:otherwise>
		</c:choose>
	</div>
</div>

<div>
	<form:hidden path="address1" maxlength="255" id="address1"/>
	<form:hidden path="city" maxlength="255" id="city"/>
	<form:hidden path="state" maxlength="255" id="state"/>
	<form:hidden path="postalCode" maxlength="11" id="postalCode"/>
	<form:hidden path="country" maxlength="255" id="country"/>
	<form:hidden path="longitude" maxlength="255" id="longitude"/>
	<form:hidden path="latitude" maxlength="255" id="latitude"/>
	<input type="hidden" id="original_address"/>
</div>
