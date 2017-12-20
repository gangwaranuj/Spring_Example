<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="custom_fields.edit_custom_fields" var="edit_custom_fields"/>
<wm:app
	pagetitle="${edit_custom_fields}"
	bodyclass="accountSettings edit-custom-fields"
	webpackScript="settings"
>

	<script>
		var config = {
			mode: 'editCustomFields',
			jsonFieldGroup: ${requestScope.field_group_json}
		};
	</script>

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/settings/manage/customfields" scope="request"/>
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header clear">
					<a class="button pull-right" href="/settings/manage/customfields"><fmt:message key="custom_fields.back_to_list"/></a>
					<h3>${edit_custom_fields}</h3>
				</div>

				<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

				<form:form modelAttribute="workCustomFieldGroupDTO" action="/settings/manage/custom_fields_edit" method="post" cssClass="form-horizontal">
					<wm-csrf:csrfToken />
					<form:hidden path="workCustomFieldGroupId"/>

					<div id="work-display-build1">
						<fieldset>
							<h4><fmt:message key="global.overview"/></h4>

							<div class="clearfix control-group">
								<form:label path="name" class="control-label"><fmt:message key="custom_fields.set_name"/></form:label>
								<div class="input controls">
									<form:input id="field_group_name" path="name" maxlength="255" cssClass="span5" htmlEscape="false" />
								</div>
							</div>

							<div class="clearfix control-group">
								<form:label path="required" class="control-label"><fmt:message key="custom_fields.required_set_status"/></form:label>
								<div class="input controls">
									<ul class="inputs-list">
										<li>
											<label>
												<form:checkbox id="field_group_required" path="required"/>
												<fmt:message key="custom_fields.field_group_required" var="custom_fields_group_required">
													<fmt:param value="${requestScope.companyName}"/>
												</fmt:message>
												<span>${custom_fields_group_required}</span>
											</label>
										</li>
									</ul>
									<span class="help-block"><fmt:message key="custom_fields.enabling_warning"/></span>
								</div>
							</div>

							<div id="custom-fields">
								<div id="owner-custom-fields-container">
									<h4><fmt:message key="custom_fields.fields_to_complete"/>
										<small><fmt:message key="custom_fields.optional_display_to_workers"/></small>
									</h4>
									<p><fmt:message key="custom_fields.add_custom_data_fields"/></p>

									<ul id="owner-custom-fields" style="margin-left: 0; list-style: none;"></ul>
									<div class="wm-action-container">
										<a class="button" id="add-owner-custom-field"><fmt:message key="custom_fields.add_field"/></a>
									</div>
								</div>
								<div id="resource-custom-fields-container">
									<h4><fmt:message key="custom_fields.fields_for_worker_to_complete"/></h4>

									<p><fmt:message key="custom_fields.add_custom_data_fields_for_workers"/></p>

									<ul id="resource-custom-fields" style="margin-left: 0; list-style: none;"></ul>
									<div class="wm-action-container">
										<a class="button" id="add-resource-custom-field"><fmt:message key="custom_fields.add_field"/></a>
									</div>
								</div>
							</div>
						</fieldset>

						<div class="wm-action-container">
							<a class="button" href="<c:url value="/settings/manage/customfields"/>"><fmt:message key="global.cancel"/></a>
							<a href="javascript:void(0);" class="button" id="save-custom-field-group"><fmt:message key="global.save_changes"/></a>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>

	<script id="owner-custom-field-template" type="text/x-jquery-tmpl">
	<li class="custom_field_set">
		<input type="hidden" name="workCustomFields[\${index}].id" value="\${data.id}" class="field_id"/>
		<input type="hidden" name="workCustomFields[\${index}].workCustomFieldTypeCode" value="owner" class="field_type"/>
		<div style="position:absolute;top:5px;right:5px">
			<a href="javascript:void(0);" class="icon-sort owner_sort_handle fr" title="<fmt:message key="custom_fields.change_order"/>"><fmt:message key="custom_fields.sort_choice"/></a>
			<a href="javascript:void(0);" class="icon-delete remove_choice fr" title="<fmt:message key="custom_fields.delete_answer"/>"><fmt:message key="custom_fields.delete_choice"/></a>
		</div>

		<div class="controls-row">
			<div class="span5">
				<label><fmt:message key="custom_fields.custom_field"/></label>
			</div>
			<div class="span5">
				<label><fmt:message key="custom_fields.default_field_value"/></label>
			</div>
			<div class="span5">
				<input type="text" class="field_name" name="workCustomFields[\${index}].name" value="\${data.name}" maxlength="256"/>
				<span class="span5 mt3">
					<input type="checkbox" name="workCustomFields[\${index}].requiredFlag" value="true" class="field_required"/>
				<span><fmt:message key="global.required"/></span>
				{{if data.id}}
					<small class="meta id">ID: \${data.id}</small>
				{{/if}}
				</span>
			</div>
			<div class="span5">
				<textarea class="field_value" name="workCustomFields[\${index}].value" value="\${data.default_value}" maxlength="1000">\${data.default_value}</textarea>
				<span class="help-block span5"><fmt:message key="custom_fields.optional_dropdown"/></span>
			</div>
		</div>

		<fieldset>
			<div class="controls-row">
				<div class="span5">
					<label><strong><fmt:message key="custom_fields.who_can_see_field"/></strong></label>
					<ul class="inputs-list">
						<li>
							<label>
								<input type="checkbox" disabled="disabled" checked="checked"/>
								<span><fmt:message key="custom_fields.assignment_owner_and_followers"/></span>
							</label>
						</li>
						<li>
							<label>
								<input type="checkbox" name="workCustomFields[\${index}].showOnSentStatus"
									   value="true" class="field_show_on_sent_status"/>
								<span><fmt:message key="custom_fields.sent_workers"/></span>
							</label>
						</li>
						<li>
							<label>
								<input type="checkbox" id="assigned-resources\${index}"
									   name="workCustomFields[\${index}].visibleToResourceFlag"
									   value="true" class="field_visible"/>
								<span><fmt:message key="custom_fields.assigned_worker"/></span>
							</label>
						</li>
					</ul>
				</div>
				<div class="span5">
					<label><strong><fmt:message key="custom_fields.where_can_they_see"/></strong></label>
					<ul class="inputs-list">
						<li>
							<label>
								<input type="checkbox" name="workCustomFields[\${index}].showOnDashboard"
									   value="true" class="field_show_on_dashboard"/>
								<span><fmt:message key="global.dashboard"/></span>
							</label>
						</li>
						<li>
							<label>
								<input type="checkbox" name="workCustomFields[\${index}].showInAssignmentEmail" value="true" class="field_show_in_assignment_email"/>
								<span><fmt:message key="global.emails"/></span>
								<span class="tooltipped tooltipped-n info" aria-label="<fmt:message key="custom_fields.fields_will_be"/>"><i class="wm-icon-question-filled"></i></span>
							</label>
						</li>
						<li>
							<label>
								<input type="checkbox" name="workCustomFields[\${index}].showOnInvoice" value="true"
									   class="field_show_on_invoice"/>
								<span><fmt:message key="global.invoices"/></span>
							</label>
						</li>
						<li>
							<label>
								<input type="checkbox"
									   name="workCustomFields[\${index}].showInAssignmentHeader"
									   value="true" class="field_show_in_assignment_header"/>
								<span><fmt:message key="custom_fields.next_to_assignment_title"/></span>
							</label>
						</li>
						<li>
							<label>
								<input type="checkbox" name="workCustomFields[\${index}].showOnPrintout"
									   value="true" class="field_show_on_printout"/>
								<span><fmt:message key="custom_fields.assignment_printout"/></span>
							</label>
						</li>
					</ul>
				</div>
			</div>
		</fieldset>
	</li>
	</script>

	<script id="resource-custom-field-template" type="text/x-jquery-tmpl">
	<li class="custom_field_set">
		<input type="hidden" name="workCustomFields[\${index}].id" value="\${data.id}" class="field_id"/>
		<input type="hidden" name="workCustomFields[\${index}].workCustomFieldTypeCode" value="resource"
			   class="field_type"/>
		<input type="hidden" name="workCustomFields[\${index}].visibleToResourceFlag" value="true" class="field_visible"/>

		<div style="position:absolute;top:5px;right:5px">
			<a href="javascript:void(0);" class="icon-sort resource_sort_handle fr" title="<fmt:message key="custom_fields.change_order"/>"><fmt:message key="custom_fields.sort_choice"/></a>
			<a href="javascript:void(0);" class="icon-delete remove_choice fr" title="<fmt:message key="custom_fields.delete_answer"/>"><fmt:message key="custom_fields.delete_choice"/></a>
		</div>

		<div class="controls-row">
			<div class="span5">
				<label><strong><fmt:message key="custom_fields.custom_field"/></strong></label>
			</div>
			<div class="span5">
				<label><strong><fmt:message key="custom_fields.default_field_value"/></strong></label>
			</div>
			<div class="span5">
				<input type="text" class="field_name" name="workCustomFields[\${index}].name" value="\${data.name}" maxlength="256"/>
				<span class="span5 mt3">
					<input type="checkbox" name="workCustomFields[\${index}].requiredFlag" value="true" class="field_required"/>
					<span class="mt"><fmt:message key="global.required"/></span>
					{{if data.id}}
						<small class="meta id">ID: \${data.id}</small>
					{{/if}}
				</span>
			</div>
			<div class="span5">
				<textarea class="field_value" name="workCustomFields[\${index}].value" value="\${data.default_value}" maxlength="1000">\${data.default_value}</textarea>
				<span class="help-block span5"><fmt:message key="custom_fields.optional_dropdown"/></span>
			</div>
		</div>

		<fieldset>
			<div class="controls-row">
				<div class="span5">
					<div class="clearfix">
						<label><strong><fmt:message key="custom_fields.who_can_see_field"/></strong></label>
					</div>
					<ul class="inputs-list">
						<li>
							<label>
								<input type="checkbox" disabled="disabled" checked="checked"/>
								<span><fmt:message key="custom_fields.assignment_owner_and_followers"/></span>
							</label>
						</li>
						<li>
							<label>
								<input type="checkbox" disabled="disabled" checked="checked"/>
								<span><fmt:message key="custom_fields.assigned_worker"/></span>
							</label>
						</li>
					</ul>
				</div>
				<div class="span5">
					<div class="clearfix">
						<label><strong>Where can ${currentUser.companyEffectiveName} see it?</strong></label>
					</div>
					<ul class="inputs-list">
						<li>
							<label>
								<input type="checkbox" name="workCustomFields[\${index}].showOnInvoice" value="true"
									   class="field_show_on_invoice"/>
								<span><fmt:message key="custom_fields.show_on_invoices"/></span>
							</label>
						</li>
					</ul>
				</div>
			</div>
		</fieldset>
	</li>
	</script>
</wm:app>
