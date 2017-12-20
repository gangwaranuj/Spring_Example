<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script src="//maps.google.com/maps/api/js?key=AIzaSyAWD12qVRbpnGyNF_fmYMERR0gyvdbHNvE&libraries=places" type="text/javascript"></script>

<c:if test="${empty param.addressForm}">
	<div id="address-entry" class="alert alert-info">
		<p><i class="wm-icon-globe-circle icon-2x"></i><fmt:message key="public.update_location"/></p>
		<input type="text" size="500"  id="addressTyper" placeholder="<fmt:message key="public.address_in_locale"/>" />
		<span class="help-block-signup"><i class="wm-icon-information-filled"></i><fmt:message key="public.the_more_accurate"/></span>
	</div>

	<div id="addressBox">

		<div class="control-group">
			<form:label path="address1" cssClass="control-label"><fmt:message key="global.address"/></form:label>
			<div class="controls">
				<form:input path="address1" maxlength="100" id="address1" cssClass="span5" readonly="${mboProfile.status eq 'NORMAL'}"/>
			</div>
		</div>

		<div class="control-group">
			<form:label path="city" cssClass="control-label"><fmt:message key="global.city_town"/></form:label>
			<div class="controls">
				<form:input path="city" maxlength="255" id="city" cssClass="span5" readonly="true"/>
			</div>
		</div>

		<div class="control-group">
			<form:label cssClass="control-label" path="state"><fmt:message key="global.state_province"/></form:label>
			<div class="controls">
				<form:input path="state" maxlength="255" id="state" cssClass="span5" readonly="true"/>
			</div>
		</div>

		<div class="control-group">
			<form:label cssClass="control-label" path="postalCode"><fmt:message key="global.postal_code"/></form:label>
			<div class="controls">
				<form:input path="postalCode" maxlength="11" id="postalCode" cssClass="span5" readonly="true"/>
			</div>
		</div>


		<div class="control-group">
			<form:label path="country" cssClass="control-label"><fmt:message key="global.country"/></form:label>
			<div class="controls">
				<form:input path="country" maxlength="255" id="country" cssClass="span5" readonly="true"/>
			</div>
		</div>

		<form:hidden path="longitude" maxlength="255" id="longitude"/>
		<form:hidden path="latitude" maxlength="255" id="latitude"/>
	</div>
</c:if>

<c:if test="${param.addressForm == ('signup' or 'findwork')}">
	<div class="control-group">
		<c:choose>
			<c:when test="${param.addressForm == 'signup'}">
				<div class="controls">
					<form:errors path="postalCode" cssClass="alert alert-error inlineError span5"/>
					<fmt:message key="public.address_postal" var="address_postal"/>
					<form:input class="invitation" path="addressTyper" type="text" size="500" placeholder="${address_postal}" id="addressTyper" name="addressTyper"/>
					<div class="help-block-client"><fmt:message key="public.location_you_can_work"/></div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="controls">
				<form:errors path="postalCode" cssClass="findwork-alert">
					<c:set var="set_error_class" value="findwork-alert-input"/>
				</form:errors>
				<fmt:message key="public.address_postal" var="address_postal"/>
				<form:input path="addressTyper" type="text" size="500" cssClass="${set_error_class}" placeholder="${address_postal}" id="addressTyper" name="addressTyper"/>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
	<div>
		<form:hidden path="address1" maxlength="255" id="address1" />
		<form:hidden path="city" maxlength="255" id="city"   />
		<form:hidden path="state" maxlength="255" id="state" />
		<form:hidden path="postalCode" maxlength="11" id="postalCode" />
		<form:hidden path="country" maxlength="255" id="country" />
		<form:hidden path="longitude" maxlength="255" id="longitude"/>
		<form:hidden path="latitude" maxlength="255" id="latitude"/>
	</div>
</c:if>
