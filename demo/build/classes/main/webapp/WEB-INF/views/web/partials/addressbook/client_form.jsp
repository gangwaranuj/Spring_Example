<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form:form id="form_client_manage" modelAttribute="clientCompanyForm" action="/addressbook/client/manage" method="post" cssClass="form-horizontal">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="<c:out value="${requestScope.clientCompanyForm.id}" />">

	<div id="custom_form_message">
		<div class="message alert alert-error error dn">
			<a class="close">x</a>
			<div></div>
		</div>
	</div>
	<div>
		<div>
			<form:label path="company_name" cssClass="required control-label">Client Name</form:label>
			<div class="controls">
				<form:input id="company_name" path="company_name" maxlength="35"  placeholder="Work Market" htmlEscape="false" />
			</div>
		</div>

		<div>
			<form:label path="customer_id" cssClass="control-label">Client Number</form:label>
			<div class="controls">
				<form:input id="customer_id" path="customer_id" maxlength="35"  placeholder="999"/>
			</div>
		</div>

		<div>
			<form:label path="region" cssClass="control-label">Region</form:label>
			<div class="controls">
				<form:input id="region" path="region" maxlength="35"  placeholder="East Coast"/>
			</div>
		</div>

		<div>
			<form:label path="division" cssClass="control-label">Division</form:label>
			<div class="controls">
				<form:input id="division" path="division" maxlength="35"  placeholder="Client Services"/>
			</div>
		</div>

		<div>
			<form:label path="industry_name" cssClass="control-label">Industry</form:label>
			<div class="controls">
				<form:select id="industry_name" path="industry_name">
					<c:forEach items="${industries}" var="industry">
						<option value="${industry.key}" ${industry.value.equals(clientCompanyForm.industry_name) ? 'selected=selected' : ''}>${industry.value}</option>
					</c:forEach>
				</form:select>
			</div>
		</div>

		<div>
			<form:label path="website" cssClass="control-label">Website</form:label>
			<div class="controls">
				<form:input id="website" path="website" maxlength="35" placeholder="www.workmarket.com"/>
			</div>
		</div>

		<div class="phones">
			<form:label path="work_phone" cssClass="control-label">Work Phone</form:label>
			<div class="controls">
				<div>
					<form:input path="work_phone" type="tel" id="client-phone" cssClass="phones--main" maxlength="13" alt="phone-us" placeholder="(999) 999-9999"/>
					Ext: <form:input path="work_phone_ext" id="client-phone-ext" maxlength="4" cssClass="phones--ext" placeholder="9"/>
				</div>
			</div>
		</div>

		<div class="wm-action-container">
			<button type="button" data-modal-close class="button">Cancel</button>
			<button id="client_submit_form" class="button">Save</button>
		</div>
	</div>
</form:form>
