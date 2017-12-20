<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>



<form:form id="form_contact_manage" modelAttribute="contactForm" action="/addressbook/contact/manage" method="post" cssClass="form-stacked form-horizontal">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="<c:out value="${requestScope.contactForm.id}" />">

	<div id="custom_form_message">
		<div class="message alert alert-error error dn">
			<a class="close">x</a>
			<div></div>
		</div>
	</div>
	<div>
		<div>
			<form:label path="first_name" cssClass="required control-label">First Name</form:label>
			<div class="controls">
				<form:input id="first_name" path="first_name" maxlength="35" placeholder="Jane"/>
			</div>
		</div>

		<div>
			<form:label path="last_name" cssClass="required control-label">Last Name</form:label>
			<div class="controls">
				<form:input id="last_name" path="last_name" maxlength="35" placeholder="Smith"/>
			</div>
		</div>

		<div>
			<form:label path="title" cssClass="control-label">Title</form:label>
			<div class="controls">
				<form:input id="title" path="title" maxlength="35" placeholder="Manager"/>
			</div>
		</div>

		<div>
			<form:label path="client_company" cssClass="control-label">Client</form:label>
			<div class="controls">
				<form:select id="client_company" path="client_company" data-placeholder="Choose Client">
					<option></option>
					<c:forEach var="client" items="${clientsList}">
						<form:option value="${client.id}"><c:out value="${client.name}" /></form:option>
					</c:forEach>
				</form:select>
			</div>
		</div>

		<div>
			<form:label path="email" cssClass="control-label">Email</form:label>
			<div class="controls">
				<form:input id="email" path="email" maxlength="255" placeholder="email@email.com"/>
			</div>
		</div>

		<div class="phones">
			<form:label path="work_phone" cssClass="required control-label">Work Phone</form:label>
			<div class="controls">
				<form:input path="work_phone" type="tel" id="contact-work-phone" class="phones--main" maxlength="14" alt="phone-us" placeholder="(999) 999-9999" data-mask="" />
				Ext: <form:input path="work_phone_ext" id="contact-work-phone-ext" maxlength="4" cssClass="phones--ext" placeholder="9"/>
			</div>
		</div>

		<div class="phones">
			<form:label path="mobile_phone" cssClass="control-label">Mobile Phone</form:label>
			<div class="controls">
				<form:input path="mobile_phone" type="tel" id="contact-mobile-phone" cssclass="phones--main" maxlength="14" placeholder="(123) 1234-1234" data-mask="" />
			</div>
		</div>

		<div>
			<label class="control-label">Location</label>
			<div class="controls">
				<form:input id="client_location_typeahead" type="text" path="locations" placeholder="Type in location"/>
			</div>
		</div>

		<div>
			<div class="controls">
				<input type="checkbox" name="is_manager" <c:if test="${isManager}">checked</c:if> >
				Contact is the location manager
			</div>
		</div>

	</div>

	<div class="wm-action-container">
		<button type="button" data-modal-close class="button">Cancel</button>
		<button id="contact_submit_form" class="button">Save</button>
	</div>
</form:form>
