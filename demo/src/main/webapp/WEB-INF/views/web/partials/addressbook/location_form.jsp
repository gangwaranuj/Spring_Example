<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form:form id="form_location_manage" modelAttribute="locationForm" action="/addressbook/location/manage" method="post" cssClass="form-stacked form-horizontal">
	<wm-csrf:csrfToken />

	<input type="hidden" name="id" value="<c:out value="${requestScope.locationForm.id}" />">

	<div id="custom_form_message">
		<div class="message alert alert-error error dn">
			<a class="close">x</a>
			<div></div>
		</div>
	</div>

	<div>
		<div>
			<form:label path="name" cssClass="required control-label">Location Name</form:label>
			<div class="controls">
				<form:input id="location_name" path="name" maxlength="40" placeholder="Work Market NYC" htmlEscape="false" />
			</div>

		</div>

		<div>
			<form:label path="number" cssClass="control-label">Location Number</form:label>
			<div class="controls">
				<form:input id="location_number" path="number" maxlength="35" placeholder="20"/>
			</div>
		</div>

		<div>
			<form:label path="client_company" cssClass="control-label">Client</form:label>
			<div class="controls">
				<form:select id="clients" path="client_company" data-placeholder="Choose Client">
					<option value=""></option>
					<c:forEach var="client" items="${clientsList}">
						<form:option value="${client.id}"><c:out value="${client.name}" /></form:option>
					</c:forEach>
				</form:select>
			</div>
		</div>

		<div class="address-entry alert alert-info">
			<label class="required">Location</label>
			<input type="text" size="500"  id="addressTyper" placeholder="370 Beech St, Highland Park, IL, 60035" />
		</div>

		<div id="addressBox">

			<div>
				<form:label path="address1" cssClass="control-label">Address</form:label>
				<div class="controls">
					<form:input id="address1" path="address1" maxlength="255" placeholder="Address Line 1" style="background-color:#cecece; margin-bottom:9px;"/>
				</div>
				<div class="controls">
					<form:input id="address2" path="address2" maxlength="255" placeholder="Address Line 2"/>
				</div>
			</div>

			<div>
				<form:label path="city" cssClass="control-label">City</form:label>
				<div class="controls">
					<form:input id="city" path="city" maxlength="100" style="background-color:#cecece;" readonly="true"/>
				</div>
			</div>

			<div>
				<form:label path="state" cssClass="control-label">State</form:label>
				<div class="controls">
					<form:input id="state" path="state" maxlength="2" style="background-color:#cecece;" readonly="true"/>
				</div>
			</div>

			<div>
				<form:label path="postalCode" cssClass="control-label">Postal Code</form:label>
				<div class="controls">
					<form:input id="postalCode" path="postalCode" maxlength="10" style="background-color:#cecece;"  readonly="true"/>
				</div>
			</div>
			<form:input id="longitude" type="hidden" path="longitude" readonly="true"/>
			<form:input id="latitude" type="hidden" path="latitude" readonly="true"/>

		</div>

		<div>
			<form:label path="location_type" cssClass="control-label">Location Type</form:label>
			<div class="controls">
				<form:select id="location_type" path="location_type">
					<c:forEach var="locationType" items="${locationTypesList}">
						<form:option value="${locationType.id}"><c:out value="${locationType.name}" /></form:option>
					</c:forEach>
				</form:select>
			</div>
		</div>

		<div>
			<form:label path="instructions" cssClass="control-label">Travel Instructions</form:label>
			<div class="controls">
				<form:textarea path="instructions" id="location_instructions" style=" margin-bottom:9px;"/>
			</div>
		</div>

		<div>
			<label for="location-contacts" class="control-label">Contacts</label>
			<div class="controls">
				<form:input id="location-contacts" type="text" path="contacts" placeholder="Type in a contact name" />
			</div>
		</div>

		<form:input id="country" type="hidden" path="country"/>
	</div>

	<div class="wm-action-container">
		<button type="button" class="button" data-modal-close>Cancel</button>
		<button id="location_submit_form" class="button">Save</button>
	</div>
</form:form>

