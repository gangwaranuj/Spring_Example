<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<form:form class="form-horizontal" modelAttribute="project" action="${param.form_uri}" method="post" id="form_project" accept-charset="utf-8">
	<wm-csrf:csrfToken />
	<input type="hidden" id="add_assignment" name="add_assignment" value="0" />
	<form:hidden path="id"/>

	<div class="clearfix control-group">
		<form:label path="name" cssClass="required control-label">Project Title</form:label>
		<div class="input controls">
			<form:input path="name" id="project_title_name" maxlength="255" cssClass="span6" htmlEscape="false" />
		</div>
	</div>

	<div class="clearfix control-group">
		<form:label path="description" cssClass="required control-label">Description</form:label>
		<div class="input controls">
			<form:textarea path="description" id="project_description" rows="10" cssClass="span6" />
		</div>
	</div>

	<div class="clearfix control-group">
		<form:label path="owner" cssClass="required control-label">Project Owner</form:label>
		<div class="input controls">
			<spring:bind path="owner">
				<select name="${status.expression}" id="project_owner">
					<c:forEach var="user" items="${users}">
						<option value="${user.id}" ${(status.displayValue eq user.id) ? 'selected' : ''}><c:out value="${user.fullName}"/></option>
					</c:forEach>
				</select>
			</spring:bind>
		</div>
	</div>

	<div class="clearfix control-group">
		<form:label path="clientCompany" cssClass="required control-label">Client</form:label>
		<div class="input controls">
			<form:select path="clientCompany" id="project_client_company_list" tabindex="0" cssStyle="width:337px;">
				<form:option value="">- Select Client -</form:option>
				<form:options items="${clientCompanies}" itemLabel="name" itemValue="id" />
			</form:select>
			<span class="help-inline"><a id="add-new-client" value="" class="add-new">Add New Client</a></span>
		</div>

		<div id="new-client" class="dn lastNoResultsMessage">
			<fieldset>
				<hr/>
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
						<input type="tel" id="client-phone" name="client-phone" class="newclient"/>
						<span>ext.</span>
						<input type="text" id="client-phone-ext" name="client-phone-ext" maxlength="4" class="span1 newclient"/>

					</div>
				</div>

				<div class="control-group">
					<div class="controls span3">
						<wm:button id="newclient_form_submit">Add Client</wm:button>
					</div>
				</div>
				<hr/>
			</fieldset>
		</div>
	</div>

	<c:if test="${budgetEnabledFlag}">
		<div class="clearfix control-group">
			<form:label path="budgetEnabledFlag" class="control-label">Project Budget</form:label>
			<div class="input controls">
				<span class="span2">
					<form:radiobutton path="budgetEnabledFlag" id="enable_budget_button" value="1" />
					Yes
				</span>
				<span class="span1">
					<form:radiobutton path="budgetEnabledFlag" id="disable_budget_button" value="0"/>
					No
				</span>
			</div>
		</div>

		<c:choose>
			<c:when test="${project.budgetEnabledFlag}">
				<div class="clearfix control-group"  id="budget-amount">
			</c:when>
			<c:otherwise>
				<div class="clearfix dn control-group"  id="budget-amount">
			</c:otherwise>
		</c:choose>
			<div class="controls">
				<form:label path="budget">Total Budget Amount</form:label>
				<form:input path="budget" id="budget" />
			</div>
		</div>
	</c:if>


	<div class="clearfix control-group">
		<form:label path="startDate" class="control-label">Start Date</form:label>
		<div class="input controls">
			<form:input path="startDate" id="project_start_date" cssClass="small" />
		</div>
	</div>

	<div class="clearfix control-group">
		<form:label path="dueDate" class="control-label">Completion Date</form:label>
		<div class="input controls">
			<form:input path="dueDate" id="project_due_date" cssClass="small" />
		</div>
	</div>

	<c:if test="${reserveFundsEnabledFlag}">
		<div class="clearfix control-group">
			<div class="input controls">
				<c:if test="${canNotEdit}">
					<span id="disable-checkbox" style="width: 20px;" class="tooltipped tooltipped-n" aria-label="Changing this setting is disabled because you have one or more assignments in progress for this project that is on immediate payment terms.">
						<i class="wm-icon-question-filled"></i>
						<span class="disabled-overlay"></span>
				</c:if>
						<form:checkbox path="reservedFundsEnabled" value="1" disabled="${canNotEdit}" id="project_is_reserved_funds_enabled" />
				<c:if test="${canNotEdit}">
					</span>
				</c:if>
				Enable reserved funds management for this project
				<span class="tooltipped tooltipped-n" aria-label="Enable this setting if you want to be able to manage reserved cash funds for this project.">
					<i class="wm-icon-question-filled"></i>
				</span>
				<span class="help-block">Once enabled, you can add funds to the project via the Manage Cash Funds link in your the payment center.</span>
			</div>
		</div>
	</c:if>

	<div class="wm-action-container">
		<a class="button" href="/projects">Go Back</a>
		<button type="submit" class="button">Save Changes</button>
	</div>

</form:form>
