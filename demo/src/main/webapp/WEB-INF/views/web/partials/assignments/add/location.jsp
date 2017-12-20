<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script src="//maps.google.com/maps/api/js?key=AIzaSyAWD12qVRbpnGyNF_fmYMERR0gyvdbHNvE&libraries=places" type="text/javascript"></script>

<div class="inner-container <c:if test="${reserveFundsEnabledFlag}"> dn </c:if>">
	<div class="page-header">
		<h4>Client &amp; Location</h4>
	</div>

	<div id="assignment-location-container">
		<div class="control-group">
			<label for="client_company_list" class="control-label strong <c:if test="${requireProject}"> required </c:if>">Client</label>
			<div class="controls">
				<form:select path="clientcompany" id="client_company_list" cssClass="client_company_list">
					<form:option value="">Select</form:option>
					<form:options items="${client_company_list}" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><a id="add-new-client" class="add-new">Add New Client</a></span>

				<label style="display: none;">
					<form:checkbox path="badge_show_client_name" id="badge_show_client_name" value="1"/>
					Show client name on Worker Badge
					<span class="tooltipped tooltipped-n" aria-label='Your company name shows on the Worker badge by default. Enable this feature to show "On behalf of" your client instead'>
						<i class="wm-icon-question-filled"></i>
					</span>
				</label>
			</div>
		</div>

		<div id="new-client" class="dn">
			<fieldset>
				<div class="control-group">
					<label for="newclient-name" class="control-label required">Client Name</label>
					<div class="controls">
						<input type="text" id="newclient-name" name="newclient[name]" class="newclient" />
					</div>
				</div>

				<div class="control-group">
					<label for="customer_id" class="control-label">Client ID</label>
					<div class="controls">
						<input type="text" id="customer_id" name="customer_id" class="newclient" />
					</div>
				</div>

				<div class="control-group">
					<label for="region" class="control-label">Region</label>
					<div class="controls">
						<input type="text" id="region" name="region" class="newclient" />
					</div>
				</div>

				<div class="control-group">
					<label for="division" class="control-label">Division</label>
					<div class="controls">
						<input type="text" id="division" name="division" class="newclient" />
					</div>
				</div>

				<div class="control-group">
					<label for="industry_id" class="control-label">Industry</label>
					<div class="controls">
							<select id="industry_id" name="industry_id" class="newclient">
								<c:forEach var="industry" items="${industries}">
									<option value="${industry.key}"><c:out value="${industry.value}" /></option>
								</c:forEach>
							</select>
					</div>
				</div>

				<div class="control-group">
					<label for="website" class="control-label">Website</label>
					<div class="controls">
						<input type="text" id="website" name="website" maxlength="35" class="newclient"/>
					</div>
				</div>

				<div class="control-group">
					<label for="client-phone" class="control-label">Work Phone</label>
					<div class="controls">
						<div class="input-prepend">
							<span class="add-on"><i class="icon-phone"></i></span>
							<input type="text" id="client-phone" name="client-phone" class="input-with-add-on newclient" maxlength="10" alt="phone-us"/>
						</div>
						<div class="input-prepend">
							<span class="add-on">ext.</span>
							<input type="text" id="client-phone-ext" name="client-phone-ext" maxlength="4" class="span1 newclient"/>
						</div>
					</div>
				</div>

				<div class="control-group">
					<div class="controls">
						<a class="submit button" id="newclient_form_submit">Add Client</a>
					</div>
				</div>
				<hr/>
			</fieldset>
		</div>

		<div class="control-group">
			<label for="project-dropdown" class="control-label strong <c:if test="${requireProject}"> required </c:if>">Project</label>
			<div class="controls">
				<form:select path="project" id="project-dropdown" disabled="true" >
					<form:option value="">Select</form:option>
					<form:options items="${projects}"/>
				</form:select>

				<c:choose>
					<c:when test="${hasFeatureProjectPermission}">
						<c:if test="${hasProjectAccess}">
							<span class="help-inline"><a id="add-new-project" href="javascript:void(0);">Add New Project</a></span>
						</c:if>
					</c:when>
					<c:otherwise>
						<span class="help-inline"><a id="add-new-project" href="javascript:void(0);">Add New Project</a></span>
					</c:otherwise>
				</c:choose>

			</div>
		</div>

		<div id="new-project" class="pop-content dn">
			<fieldset>
				<input type="hidden" name="newproject[client]" id="newproject_client_company" class="newproject" />

				<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
					<c:param name="containerId" value="newproject_messages" />
				</c:import>

				<div class="control-group">
					<label for="newproject_owner" class="control-label required">Project Owner</label>
					<div class="controls">
						<select name="newproject[owner]" id="newproject_owner" class="newproject">
							<c:forEach var="item" items="${users}">
								<option value="${item.key}"><c:out value="${item.value}" /></option>
							</c:forEach>
						</select>
					</div>
				</div>

				<div class="control-group">
					<label for="newproject_name" class="control-label required">Project Title</label>
					<div class="controls">
						<input type="text" name="newproject[name]" id="newproject_name" class="newproject" maxlength="255" />
					</div>
				</div>

				<div class="control-group">
					<label for="newproject__description" class="control-label required">Description</label>
					<div class="controls">
						<textarea name="newproject[description]" id="newproject__description" class="newproject" style="height:100px;"></textarea>
					</div>
				</div>

				<div class="control-group">
					<label for="newproject_due_date" class="control-label">Completion Date</label>
					<div class="controls">
						<input type="text" name="newproject[due_date]" id="newproject_due_date" class="newproject" />
					</div>
				</div>
				<sec:authorize access="hasFeature('reserveFunds')">
					<div class="control-group">
						<label for="newproject_is_reserved_funds_enabled" class="control-label">Enabled Project Reserved Funds</label>
						<div class="controls">
							<input type="checkbox" name="newproject[enable_reserved_funds]" id="newproject_is_reserved_funds_enabled" class="newproject" />
						</div>
					</div>
				</sec:authorize>

				<div class="control-group">
					<div class="controls">
						<a class="submit button" id="newproject_form_submit">Add Project</a>
					</div>
				</div>
				<hr/>
			</fieldset>
		</div>

		<div class="control-group">
			<label class="control-label">Location Type</label>
			<div class="controls">
				<label>
					<form:radiobutton path="clientlocations" value="0" id="clientlocations3" cssClass="clientlocations_select" />
					<span>Create new location</span>
				</label>
				<label>
					<form:radiobutton path="clientlocations" value="-2" id="clientlocations1" cssClass="clientlocations_select" />
					<span>Select location from your <a href="/addressbook">Contact Manager</a></span>
				</label>
				<label>
					<form:radiobutton path="clientlocations" value="-1" id="clientlocations2" cssClass="clientlocations_select" />
					<span>Virtual/off-site</span>
					<span class="help-inline">This assignment can be done anywhere.</span>
				</label>
			</div>
		</div>

		<div id="onetime-location">
			<hr/>
			<div class="control-group">
				<label for="location-name-text" class="control-label">Location Name</label>
				<div class="controls controls-row">
					<form:input path="location_name" id="location-name-text" cssClass="span4" maxlength="255" placeholder="Location Name" />
				</div>
			</div>

			<div class="control-group">
				<label for="location-number-text" class="control-label">Location Number</label>
				<div class="controls controls-row">
					<form:input path="location_number" id="location-number-text" cssClass="span4" maxlength="50" placeholder="Location Number" />
				</div>
			</div>

			<div class="control-group">
				<label for="location_type" class="control-label">Location Type</label>
				<div class="controls">
					<form:select path="location_type" id="location_type" items="${location_types}" />
				</div>
			</div>

			<div class="address-entry alert alert-info">
				<label class="required">Location</label>
				<input type="text" size="500"  id="addressTyper" placeholder="370 Beech St, Highland Park, IL, 60035" />
			</div>
			<div class="control-group">
				<label for="address1" class="control-label required">Address</label>
				<div class="controls">
					<form:input path="location_address1" id="address1" cssClass="span4" maxlength="100" placeholder="Address Line 1" cssStyle="margin-bottom:9px;" />
				</div>
				<div class="controls">
					<form:input path="location_address2" id="address2" cssClass="span4" maxlength="100" placeholder="Address Line 2" />
				</div>
			</div>

			<div class="control-group">
				<label for="city" class="control-label">City</label>
				<div class="controls">
					<form:input path="location_city" id="city" cssClass="span4" readonly="true" placeholder="City" />
				</div>
			</div>

			<div class="control-group">
				<label for="state" class="control-label">State/Province</label>
				<div class="controls">
					<form:input path="location_state" id="state" cssClass="span4" readonly="true" placeholder="State/Province" />
				</div>
			</div>

			<div class="control-group">
				<label for="postalCode" class="control-label">Postal Code</label>
				<div class="controls">
					<form:input path="location_postal_code" id="postalCode" cssClass="span2" readonly="true" placeholder="Postal Code" />
				</div>
			</div>
			<div class="control-group">
				<label for="postalCode" class="control-label">Country</label>
				<div class="controls">
					<form:input id="country" path="location_country" readonly="true"/>
				</div>
			</div>
			<form:input id="longitude" type="hidden" path="location_longitude" readonly="true"/>
			<form:input id="latitude" type="hidden" path="location_latitude" readonly="true"/>

			<hr/>
		</div>

		<div id="location-selector">
			<hr/>
			<div class="control-group">
				<label for="client_location_typeahead" class="control-label required">Location name</label>
				<div id="client-location" class="controls">
					<input name="clientlocation" id="client_location_typeahead" data-placeholder="Type in a location name" />
				</div>
			</div>
		</div>

		<div id="location-selected-text" class="dn">
			<div class="span7 offset3">
				<address>
					<span class="location-name" id="location-name-selected-text"></span><br/>
					<span class="location-number" id="location-number-selected-text"></span><br/>
					<span class="address-one" id="address-one-selected-text"></span><br/>
					<span class="address-two" id="address-two-selected-text"></span><br/>
					<span class="city" id="city-selected-text"></span>,
					<span class="state" id="state-selected-dropdown"></span>
					<span class="zip-code field-with-placeholder" id="postal-code-selected-text"></span><br/>
					<br/>
					Type:
					<span class="location-type field-with-placeholder" id="location_type-selected"></span>
				</address>
			</div>
		</div>

		<div id="contact-entry">
			<div id="onsite-contact-select">
				<div class="control-group">
					<label for="onsite-contact-dropdown" class="control-label">Location Contact</label>
					<div class="controls">
						<form:select path="onsite_contact" id="onsite-contact-dropdown">
							<form:option value="">Location Contact</form:option>
						</form:select>
					</div>
				</div>
			</div>

			<div id="onsite-contact-selected" class="dn input">
				<div class="span7 offset3">
					<span class="onsite-phone field-with-placeholder" id="onsite-phone-selected"></span><br/>
					<span class="onsite-email field-with-placeholder" id="onsite-email-selected"></span>
				</div>
			</div>

			<fieldset id="primary-contact">
				<legend>
					Location Contact

					<span id="onsite-contact-new-contact" class="help-block">
						This contact will be added to your Location Manager and associated
						with this location.
					</span>

					<span id="onsite-contact-new-location-contact" class="help-block dn">
						This location and contact will be added to your Location Manager.
						The new contact will be set as the primary contact for the location.
					</span>
				</legend>

				<div class="control-group onsite-firstname field-with-placeholder">
					<label for="onsite-firstname" class="control-label">Name</label>
					<div class="controls controls-row">
						<form:input path="contactfirstname" id="onsite-firstname" maxlength="50" placeholder="First Name" cssClass="span3"/>
						<form:input path="contactlastname" id="onsite-lastname" maxlength="50" placeholder="Last Name" cssClass="span4"/>
					</div>
				</div>

				<div class="control-group onsite-phone field-with-placeholder">
					<label for="onsite-phone" class="control-label">Phone</label>
					<div class="controls controls-row">
						<form:input path="contactphone" id="onsite-phone" cssClass="span2" maxlength="14" placeholder="Phone" />
						<form:input path="contactphone_extension" id="onsite-phone-extension" cssClass="span2 extension" maxlength="6" placeholder="Ext" />
					</div>
				</div>
				<div class="control-group onsite-email field-with-placeholder">
					<label for="onsite-email" class="control-label">Email</label>
					<div class="controls">
						<form:input path="contactemail" id="onsite-email" maxlength="255" placeholder="Email" />
					</div>
				</div>
				<hr/>
			</fieldset>


			<div id="onsite-secondary-contact-select">
				<div class="control-group">
					<label for="onsite-secondary-contact-dropdown" class="control-label">Secondary Contact</label>
					<div class="controls">
						<form:select path="onsite_secondary_contact" id="onsite-secondary-contact-dropdown">
							<form:option value="">Secondary Location Contact</form:option>
						</form:select>
					</div>
				</div>
			</div>

			<div id="onsite-secondary-contact-selected" class="dn input">
				<div class="span7 offset3">
					<span class="onsite-secondary-phone field-with-placeholder" id="onsite-secondary-phone-selected"></span><br/>
					<span class="onsite-secondary-email field-with-placeholder" id="onsite-secondary-email-selected"></span>
				</div>
			</div>

			<fieldset id="secondary-contact">
				<legend>
					Secondary Contact
					<span id="onsite-secondary-contact-new-contact" class="help-block">
						This contact will be added to your Location Manager and associated
						with this location.
					</span>

					<span id="onsite-secondary-contact-new-location-contact" class="help-block dn">
						This location and contact will be added to your Location Manager.
						The new contact will be set as the primary contact for the location.
					</span>
				</legend>

				<div class="control-group">
					<label for="onsite-secondary-firstname" class="control-label">Name</label>
					<div class="controls controls-row">
						<form:input path="secondarycontactfirstname" id="onsite-secondary-firstname" maxlength="50" placeholder="First Name" cssClass="span3"/>
						<form:input path="secondarycontactlastname" id="onsite-secondary-lastname" maxlength="50" placeholder="Last Name" cssClass="span4" />
					</div>
				</div>

				<div class="control-group">
					<label for="onsite-secondary-phone" class="control-label">Phone</label>
					<div class="controls controls-row">
						<form:input path="secondarycontactphone" id="onsite-secondary-phone" cssClass="span2" maxlength="14" placeholder="Phone" />
						<form:input path="secondarycontactphone_extension" id="onsite-secondary-phone-extension" cssClass="span2 extension" maxlength="6" placeholder="Ext" />
					</div>
				</div>
				<div class="control-group">
					<label for="onsite-secondary-email" class="control-label">Email</label>
					<div class="controls">
						<form:input path="secondarycontactemail" id="onsite-secondary-email" maxlength="255" placeholder="Email" />
					</div>
				</div>
			</fieldset>
			<div class="clear"></div>
		</div>

		<div id="virtual" class="dn"></div>
		<form:hidden path="clientlocation_id" id="clientlocation_id" />
	</div>
</div>