<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action='/assignments/edit_location_contact/${work.workNumber}' method="POST" id="form_edit_location_contact" class="form-horizontal">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="${work.workNumber}"/>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="container_id" value="location_contact_messages"/>
	</c:import>

	<fieldset>
		<div id="onsite-contact-select">
			<div class="control-group">
				<label for="onsite_contact" class="control-label">Onsite Contact</label>

				<div class="controls">

					<select name="onsite_contact" id='onsite-contact-dropdown'>
						<c:forEach items="${client_contacts}" var="contact">
							<c:if test="${not empty work}">
								<option value="${contact.key}" <c:if test="${not empty work.locationContact && contact.key == work.locationContact.id}">selected="selected"</c:if>  ><c:out value="${contact.value}" /></option>
							</c:if>
						</c:forEach>
						<option value="new">- Create new contact</option>
						<option value=""
						        <c:if test="${empty work.locationContact}">selected="selected"</c:if> >- No contact
						</option>
					</select>

					<c:if test="${empty work.clientCompany}">
						<a id="onsite-contact-edit">edit</a>
					</c:if>

					<span class="help-block">Who is the onsite contact for the worker?</span>

					<div id="onsite-contact-selected" class="dn">
						<address>
							<strong>
								<span class="onsite-firstname field-with-placeholder" id="onsite-firstname-selected"></span>
								<span class="onsite-lastname field-with-placeholder" id="onsite-lastname-selected"></span>
							</strong><br/>
							<strong>Phone:</strong> <span class="onsite-phone field-with-placeholder" id="onsite-phone-selected"></span><br/>
							<strong>Email:</strong> <span class="onsite-email field-with-placeholder" id="onsite-email-selected"></span>
						</address>
					</div>
				</div>
			</div>
		</div>

		<div id="onsite-contact" <c:if test="${not empty client_contacts}">class="dn"</c:if>>
			<div class="control-group">
				<label for="onsite-firstname" class="control-label">First Name</label>

				<div class="controls">
					<input type="text" name="contactfirstname" id='onsite-firstname' maxlength='50' value="${not empty work && not empty locationContact ? locationContact.firstName : ''}"/>
				</div>
			</div>
			<div class="control-group">
				<label for="onsite-lastname" class="control-label">Last Name</label>

				<div class="controls">
					<input type="text" name="contactlastname" id='onsite-lastname' maxlength='50' value="${not empty work && not empty locationContact ? locationContact.lastName : ''}"/>
				</div>
			</div>
			<div class="control-group">
				<label for="onsite-phone" class="control-label">Phone</label>

				<div class="controls">
					<input type="tel" name="contactphone" id="onsite-phone" maxlength="25" alt="phone-us"/>
				</div>
			</div>
			<div class="control-group">
				<label for="onsite-email" class="control-label">Email</label>

				<div class="controls">
					<input type="text" name="contactemail" id='onsite-email' maxlength='255' value="${not empty work && not empty locationContact ? locationContact.mostRecentEmail.email : ''}"/>
				</div>
			</div>
			<c:if test="${empty work.clientCompany}">
				<input type="hidden" name="isEmptyClientCompany" value="true" />
				<input type="hidden" name="contactphone_id" value="" id='onsite-phone-id'/>
				<input type="hidden" name="contactemail_id" value="" id='onsite-email-id'/>
				<input type="hidden" name="contact_edit" value="" id='onsite-contact-edit-flag'/>
			</c:if>
		</div>
	</fieldset>

	<c:out value="${locationContactId} ${secondaryLocationContactId}"/>
	<fieldset>
		<div id="onsite-secondary-contact-select">
			<div class="control-group">
				<label for="onsite_secondary_contact" class="control-label">Secondary Contact</label>

				<div class="controls">
					<select name="onsite_secondary_contact" id='onsite-secondary-contact-dropdown'>
						<c:forEach items="${client_contacts}" var="contact">
							<c:if test="${not empty work}">
								<option value="${contact.key}"
								        <c:if test="${not empty work.secondaryLocationContact && contact.key == work.secondaryLocationContact.id}">selected="selected"</c:if> ><c:out value="${contact.value}" /></option>
							</c:if>
						</c:forEach>
						<option value="new">- Create new contact</option>
						<option value=""
						        <c:if test="${empty work.secondaryLocationContact}">selected="selected"</c:if> >-
							No contact
						</option>
					</select>
					<c:if test="${ empty work.clientCompany}">
						<a id="onsite-secondary-contact-edit">edit</a>
					</c:if>

					<span class="help-block">Who is the secondary onsite contact for the worker?</span>

					<div id="onsite-secondary-contact-selected" class="dn">
						<address>
							<strong>
								<span class="onsite-secondary-firstname field-with-placeholder" id="onsite-secondary-firstname-selected"></span>
								<span class="onsite-secondary-lastname field-with-placeholder" id="onsite-secondary-lastname-selected"></span>
							</strong><br/>
							<strong>Phone:</strong> <span class="onsite-secondary-phone field-with-placeholder" id="onsite-secondary-phone-selected"></span><br/>
							<strong>Email:</strong> <span class="onsite-secondary-email field-with-placeholder" id="onsite-secondary-email-selected"></span>
						</address>
					</div>
				</div>
			</div>
		</div>

		<div id="onsite-secondary-contact" <c:if test="${ ! empty($client_contacts)}">class="dn"</c:if>>
			<div class="control-group">
				<label for="onsite-secondary-firstname" class="control-label">First Name</label>

				<div class="controls">
					<input type="text" name="secondarycontactfirstname" id='onsite-secondary-firstname' maxlength='50' value="${not empty work && not empty secondaryLocationContact ? secondaryLocationContact.firstName : ''}"/>
				</div>
			</div>
			<div class="control-group">
				<label for="onsite-secondary-lastname" class="control-label">Last Name</label>

				<div class="controls">
					<input type="text" name="secondarycontactlastname" id='onsite-secondary-lastname' maxlength='50' value="${not empty work && not empty secondaryLocationContact ? secondaryLocationContact.lastName : ''}"/>
				</div>
			</div>
			<div class="control-group">
				<label for="onsite-secondary-phone" class="control-label">Phone</label>

				<div class="controls">
					<input type="tel" name="secondarycontactphone" id="onsite-secondary-phone" maxlength="25" alt="phone-us"/>
				</div>
			</div>
			<div class="control-group">
				<label for="onsite-secondary-email" class="control-label">Email</label>

				<div class="controls">
					<input type="text" name="secondarycontactemail" id='onsite-secondary-email' maxlength='255' value="${not empty work && not empty secondaryLocationContact ? secondaryLocationContact.mostRecentEmail.email : ''}"/>
				</div>
			</div>
			<c:if test="${empty work.clientCompany}">
				<input type="hidden" name="secondary_phone_id" value="" id='onsite-secondary-phone-id'/>
				<input type="hidden" name="secondary_email_id" value="" id='onsite-secondary-email-id'/>
				<input type="hidden" name="secondary_contact_edit" value="" id='onsite-secondary-contact-edit-flag'/>
			</c:if>
		</div>
	</fieldset>

	<div class="wm-action-container">
		<button type="submit" class="button">Update</button>
	</div>

</form>

<script type="application/json" id="json_client_contacts"><c:out value="${client_contacts_json}" /></script>