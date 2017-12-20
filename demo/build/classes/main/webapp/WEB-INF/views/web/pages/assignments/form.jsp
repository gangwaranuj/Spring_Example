<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="New Assignment" bodyclass="accountSettings page-add-assignment" breadcrumbSection="Work" breadcrumbSectionURI="/assignments" breadcrumbPage="New Assignment" webpackScript="assignmentcreation">

	<c:set var="attachmentsJson" value="${empty form.attachmentsJson ? 'undefined' : form.attachmentsJson}"/>
	<c:set var="surveysJson" value="${empty form.assessmentsJson ? 'undefined' : form.assessmentsJson}"/>
	<c:set var="locationJson" value="${empty form.locationJson ? 'undefined' : form.locationJson}"/>
	<c:set var="partGroupUuid" value="${empty form.partGroup.uuid ? 'undefined' : form.partGroup.uuid}"/>
	<c:set var="partsJson" value="${empty form.partsJson ? 'undefined' : form.partsJson}"/>
	<c:set var="partsConstantsJson" value="${empty partsConstantsJson ? 'undefined' : partsConstantsJson}"/>
	<c:set var="workNumber" value="${empty workNumber ? 'undefined' : workNumber}"/>
	<c:set var="newRoutingFeatureToggle" value="true" />

	<script>
		var config = {
			main: {
				isTemplate: '${isTemplate}',
				isTemplateId: ${not empty template_id},
				templateId: '${wmfmt:escapeJavaScript(template_id)}',
				isAssignmentFor: ${not empty assignment_for},
				isPricingMode: ${not empty form.pricing_mode},
				pricingMode: '${wmfn:boolean(form.pricing_mode == 'spend', 'spend', 'pay')}',
				pricing: '${wmfmt:escapeJavaScript(form.pricing)}',
				pricingType: '${wmfmt:escapeJavaScript(assignment_pricing_type)}',
				wmFee: '${wmfmt:escapeJavaScript(workFee / 100)}',
				isMmwGlobal: ${not empty mmw_global},
				mmwGlobal: ${mmw_global},
				isMmwTemplates: ${not empty mmw_templates},
				mmwTemplatesJson: ${mmw_templates},
				isOneTimeLocation: ${not empty form.onetime_location_id},
				isClientCompany: ${not empty form.clientcompany},
				clientCompany: '${wmfmt:escapeJavaScript(form.clientcompany)}',
				project: '${wmfmt:escapeJavaScript(form.project)}',
				clientLocations: '${wmfmt:escapeJavaScript(form.clientlocations)}',
				isClientLocationId: ${not empty form.clientlocation_id},
				clientLocationId: '${wmfmt:escapeJavaScript(form.clientlocation_id)}',
				isOnsiteContact: ${not empty form.onsite_contact},
				onSiteContact: '${wmfmt:escapeJavaScript(form.onsite_contact)}',
				locationJson: ${locationJson},
				isOnsiteSecondaryContact: ${not empty form.onsite_secondary_contact},
				onSiteSecondaryContact: '${wmfmt:escapeJavaScript(form.onsite_secondary_contact)}',
				requirementSetIds: ${form.requirementSetIds},
				attachmentsJson: ${attachmentsJson},
				visibilitySettingsMap: ${visibilitySettingsMap},
				defaultVisibilitySetting: '${defaultVisibilitySetting}',
				isPartsLogisticsEnabledFlag: ${mmw.partsLogisticsEnabledFlag},
				workNumber: ${workNumber},
				partsJson: ${partsJson},
				partsConstantsJson: ${partsConstantsJson},
				isCustomCloseOutEnabledFlag: ${mmw.customCloseOutEnabledFlag},
				resourceCompletionJson: '${wmfmt:escapeJavaScript(form.resourceCompletionJson)}',
				workerDirectSelections:'${wmfmt:escapeJavaScript(form.resourceSelections)}',
				isRequiredCustomField: ${not empty form.requiredCustomField},
				requiredCustomField: '${form.requiredCustomField}',
				isCustomFieldsJson: ${not empty form.customFieldsJson},
				isCustomFieldsJsonRequiredCustomField: ${not empty form.customFieldsJson[form.requiredCustomField]},
				surveysJson: ${surveysJson},
				isAdminOrActiveResource: ${is_admin or is_active_resource}
			},
			form: {
				isAdmin: ${is_admin},
				isTemplate: '${isTemplate}',
				<c:if test="${not empty routableGroupsJson}">
					routableGroups: ${routableGroupsJson},
				</c:if>
				<c:if test="${not empty form.routing.needToApplyGroupIds}">
					groupIds: ${form.routing.needToApplyGroupIds},
				</c:if>
				<c:if test="${not empty form.routing.assignToFirstToAcceptGroupIds}">
					assignToFirstGroupIds: ${form.routing.assignToFirstToAcceptGroupIds},
				</c:if>
				showInFeed: ${form.show_in_feed || false},
				assignToFirstResource: ${form.assign_to_first_resource || false},
				<c:if test="${not empty form.workNumber}">
					workNumber: ${form.workNumber}
				</c:if>
			}
		};
	</script>

	<c:choose>
		<c:when test="${not empty form.workNumber and empty work_template_id}">
			<c:choose>
				<c:when test="${isTemplate}">
					<c:url var="formActionUrl" value="/assignments/template_edit/${workNumber}" />
				</c:when>
				<c:otherwise>
					<c:url var="formActionUrl" value="/assignments/edit/${workNumber}" />
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${isTemplate}">
					<c:url var="formActionUrl" value="/assignments/template_create" />
				</c:when>
				<c:otherwise>
					<c:url var="formActionUrl" value="/assignments/add" />
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>

	<form:form action="${formActionUrl}" commandName="form" name="form" id="assignments_form" class="form-horizontal">
		<wm-csrf:csrfToken />

		<form:hidden path="id" id="assignment_id"/>

		<form:hidden path="onetime_location_id" id="onetime_location_id" />
		<form:hidden path="assignToUserId" />
		<form:hidden path="work_template_id" />

		<c:import url="/WEB-INF/views/web/partials/message.jsp" />
		<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
			<c:param name="containerId" value="dynamic_message" />
		</c:import>

		<c:if test="${is_admin}">
			<div id="newAssignmentModalOptInBanner" style="width: 1020px; margin: 0 0 40px 0"></div>
		</c:if>

		<div id="custom_message">
			<div class="message alert alert-error error dn">
				<a href="javascript:void(0);" onclick="javascript:close_message(this); return false;" class="close">x</a>
				<div></div>
			</div>

			<div class="message alert alert-success success dn">
				<a href="javascript:void(0);" onclick="javascript:close_message(this); return false;" class="close">x</a>
				<div></div>
			</div>
		</div>

		<div class="row_sidebar_right" style="width:1280px;">
			<div class="content" style="width:720px;">
				<c:if test="${isTemplate}">
					<div id="templatescustomization" class="inner-container">
						<a name="templates"></a>
						<div class="page-header">
							<h2>Template</h2>
						</div>
						<fieldset id="templates-container">
							<div class="control-group">
								<label for="template_name" class="control-label required">Template Name</label>
								<div class="controls">
									<form:input path="template_name" id="template_name" cssClass="span7" htmlEscape="false" />
									<span class="help-block">Provide a descriptive template name so that you can easily find and reuse this template again.</span>
								</div>
							</div>
							<div class="control-group">
								<label for="template_description" class="control-label">Template Description</label>
								<div class="controls">
									<form:textarea path="template_description" id="template_description" class="large" cssClass="span7" rows="4" />
									<div class="help-block">Describe the purpose of your template by highlighting data entered or settings provided, so others can know when to use this template.</div>
								</div>
							</div>
						</fieldset>
					</div>
				</c:if>
				<c:import url="/WEB-INF/views/web/partials/assignments/add/description.jsp" />

				<div id="assignment-documents" class="inner-container">
					<div class="page-header">
						<h4>Assignment Documents</h4>

					</div>
					<div id="documents-container">
						<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
							<c:param name="containerId" value="attachment_messages" />
						</c:import>
					</div>
				</div>

				<c:if test="${mmw.useRequirementSets}">
					<c:import url="/WEB-INF/views/web/partials/assignments/add/requirement-sets.jsp" />
					<input type="hidden" id="useRequirementSets" name="useRequirementSets" value="${mmw.useRequirementSets}" />
				</c:if>

				<c:if test="${mmw.customFieldsEnabledFlag}">
					<c:import url="/WEB-INF/views/web/partials/assignments/add/customfields.jsp" />
				</c:if>

				<c:if test="${mmw.partsLogisticsEnabledFlag}">
					<c:import url="/WEB-INF/views/web/partials/assignments/add/parts.jsp" />
				</c:if>

				<c:import url="/WEB-INF/views/web/partials/assignments/add/location.jsp" />
				<c:import url="/WEB-INF/views/web/partials/assignments/add/schedule.jsp" />

				<c:if test="${not pricingNotEditable}">
					<c:import url="/WEB-INF/views/web/partials/assignments/add/price.jsp"/>
				</c:if>
				<c:if test="${mmw.customCloseOutEnabledFlag}">
					<c:import url="/WEB-INF/views/web/partials/assignments/add/deliverables.jsp" />
				</c:if>
				<c:if test="${mmw.assessmentsEnabled}">
					<c:import url="/WEB-INF/views/web/partials/assignments/add/assessments.jsp" />
				</c:if>

				<c:if test="${not empty bundleParent}">
					<c:import url="/WEB-INF/views/web/partials/assignments/add/in-bundle.jsp" />
				</c:if>

			</div>
			<div id="add-assignment-well" class="sidebar">
				<div class="well-b2">
					<h3>Options</h3>
					<div class="well-content">
						<div class="form-stacked">
							<c:choose>
								<c:when test="${is_copy}">
									<div class="clearfix">
										<label for="numberOfCopies" class="strong">Number of Copies
											<span style= "margin-top:6px;" class="tooltipped tooltipped-n" aria-label="Multiple copies, routed or saved as drafts, can be found on your Dashboard under the 'Sent' or 'Draft' filters, respectively.">
												<i class="wm-icon-question-filled"></i>
											</span>
										</label>
										<c:choose>
											<c:when test="${form.requiresUniqueExternalId}">
												<div class="input">
													<form:select path="numberOfCopies" id="numberOfCopies" cssClass="span4" items="${numberOfCopies}" disabled="true"/>
													<div class="help-block">To copy more than 10 assignments use our <a href="https://www.workmarket.com/assignments/upload#new">Bulk Uploader.</a></div>
												</div>
											</c:when>
											<c:otherwise>
												<div class="input">
													<form:select path="numberOfCopies" id="numberOfCopies" cssClass="span4" items="${numberOfCopies}"/>
													<div class="help-block">To copy more than 10 assignments use our <a href="https://www.workmarket.com/assignments/upload#new">Bulk Uploader.</a></div>
												</div>
											</c:otherwise>
										</c:choose>
										<hr/>
									</div>
								</c:when>
								<c:otherwise>
									<div id="custom-forms" class="dn">
										<div class="clearfix">
											<label for="custom-forms-dropdown">Template</label>
											<div class="input">
												<form:select path="customforms" id="custom-forms-dropdown" cssClass="wm-select">
													<form:option value="">None</form:option>
												</form:select>
											</div>
										</div>
										<hr/>
									</div>
								</c:otherwise>
							</c:choose>
							<div class="clearfix">
								<label for="industry" class="strong">Primary Industry</label>
								<div class="input">
									<form:select path="industry" id="industry" cssClass="wm-select" items="${industries}"/>
								</div>
							</div>
							<div class="clearfix">
								<label for="internal-owner-dropdown" class="strong">Internal Owner</label>
								<div class="input">
									<form:select path="internal_owner" id="internal-owner-dropdown" cssClass="wm-select" items="${users}" />
									<div class="help-block">Internal owner is the employee responsible for overseeing this assignment. The owner will receive all alerts and notifications.</div>
								</div>
							</div>

							<div class="clearfix">
								<label for="support-contact-dropdown" class="strong">Support Contact</label>
								<div class="input">
									<form:select path="support_contact" id="support-contact-dropdown" cssClass="wm-select" items="${users}" />
									<div class="help-block">Support contact is the employee responsible for communicating with the worker on this assignment.</div>
								</div>
							</div>
							<div class="clearfix">
								<label for="followers-dropdown" class="strong">Followers</label>
								<div class="input">
									<div class="dn" id="followers-hidden-fields"></div>
									<select id="followers-dropdown" data-placeholder="Followers" multiple="multiple">
										<c:forEach var="follower" items="${followers}">
											<option <c:if test="${wmfn:contains(form.followers, follower.key)}">selected</c:if> value="<c:out value="${follower.key}"/>"><c:out value="${follower.value}" /></option>
										</c:forEach>
									</select>
									<div class="help-block">Select names of one or more people who should get notifications related to this assignment.</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="alert alert-info">
					<div class="media">
						<i class="media-object icon-copy icon-3x pull-left"></i>
						<div class="media-body">
							<p>Need to create several assignments? <a href="/assignments/upload?ref=rr"><strong>Click here <i class="icon-info-sign"></i></strong></a></p>
						</div>
					</div>
				</div>
			</div>
		</div>
		<c:if test="${empty bundleParent}">
			<div class="routing-bucket">
			</div>
		</c:if>

		<div class="save-container">
			<div class="pull-left">
				<c:if test="${!isTemplate}">
					<button type="button" id="assignment-actions-draft" class="button">
						<c:out value="${(empty form.workNumber) ? 'Save as Draft' : 'Save Changes'}"></c:out>
					</button>
					<c:if test="${(is_admin and not is_bundle) and empty form.workNumber}">
						<button type="button" id="assignment-actions-template" class="button">Save as Template</button>
					</c:if>
				</c:if>
				<c:if test="${isTemplate}">
					<a href="/settings/manage/templates" class="submit button">Cancel</a>
					<button type="button" id="submit-form" class="button">
						<c:out value="${(empty form.workNumber) ? 'Create Template' : 'Save Template Changes'}"></c:out>
					</button>
				</c:if>
			</div>
		</div>

	</form:form>

	<%--template creation--%>
	<div class="dn">
		<div id="give_template_name">
			<form:form commandName="templateForm" action="/assignments/save_template" id="give_template_name_form" cssClass="form-horizontal">

				<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp" />

				<div class="control-group">
					<label for="new_template_name" class="control-label required">Name</label>
					<div class="controls">
						<form:input path="new_template_name" id="new_template_name" />
					</div>
				</div>

				<div class="control-group">
					<label for="new_template_description" class="control-label">Description</label>
					<div class="controls">
						<form:input path="new_template_description" id="new_template_description" />
					</div>
				</div>
				<div class="wm-action-container">
					<button type="button" id="give_template_name_submit" class="button">Save Template</button>
				</div>
			</form:form>
		</div>
	</div>

	<div id="load_selected_template">
		<form action="/assignments/load_template" id="load_template_form" method="POST">
			<wm-csrf:csrfToken />
			<input type="hidden" name="current_template_fields" id="current_template_fields" />
			<input type="hidden" name="template_id" id="load_template_form-template_id" />
			<input type="hidden" name="forResource" id="forResource" />
		</form>
	</div>

	<c:if test="${not empty form.requiredCustomField && not empty form.customFieldsJson[form.requiredCustomField]}">
		<script type="application/json" id="json_required_custom_field">
			${form.customFieldsJson[form.requiredCustomField]}
		</script>
	</c:if>

	<c:if test="${not empty form.customFieldsJson}">
		<script type="application/json" id="json_custom_fields">
			{
				<c:forEach varStatus="status" var="key" items="${form.customfield}">
					<c:out value="\"${key}\": ${form.customFieldsJson[key]}${status.last ? '' : ','}" />
				</c:forEach>
			}
		</script>
	</c:if>

</wm:app>
