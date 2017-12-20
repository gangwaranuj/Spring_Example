<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="global.manage_settings" var="global_manage_settings"/>
<wm:app
	pagetitle="${global_manage_settings}"
	bodyclass="accountSettings"
	webpackScript="settings"
>

	<script>
		var config = {
			mode: 'settings',
			isBudgetEnabledFlag: ${mmw.budgetEnabledFlag}
		};
	</script>

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/settings/manage" scope="request"/>
			<jsp:include page="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>


		<div class="content">
			<div class="inner-container">
				<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

				<form:form modelAttribute="mmw" action="/settings/manage" method="post" acceptCharset="utf-8">
				<wm-csrf:csrfToken />
				<div class="page-header">
					<h3><fmt:message key="global.assignment_settings"/></h3>
				</div>

				<div class="alert alert-info">
					<div class="row-fluid"><fmt:message key="manage_settings.configure_assignment"/>
						<strong><a href="https://workmarket.zendesk.com/hc/en-us/articles/209336748" target="_blank"><fmt:message key="global.learn_more"/> <i class="icon-info-sign"></i></a></strong>
					</div>
				</div>

				<div class="clearfix">
					<form:label path="instantWorkerPoolEnabled"><h5><fmt:message key="manage_settings.instant_network"/></h5></form:label>
					<label>
						<form:checkbox path="instantWorkerPoolEnabled"/>
						<span><fmt:message key="manage_settings.enable_instant_network"/></span>
					</label>

					<span class="help-block">
						<fmt:message key="manage_settings.instant_network_details"/>
					</span>
				</div>

				<div class="clearfix">
					<form:label path="customFieldsEnabledFlag"><h5><fmt:message key="global.custom_fields"/></h5></form:label>
					<label>
						<form:checkbox path="customFieldsEnabledFlag"/>
						<span><fmt:message key="manage_settings.enable_custom_fields"/></span>
					</label>
						<span class="help-block">
							<fmt:message key="manage_settings.custom_field_availablity"/>
						</span>
				</div>

				<div class="clearfix">
					<form:label path="standardTerms"><h5><fmt:message key="manage_settings.terms_of_agreement"/> <small class="muted">(<fmt:message key="global.optional"/>)</small></h5></form:label>
					<label>
						<form:checkbox id="standard_terms_flag" path="standardTermsFlag" data-well="#standard_terms"/>
						<span><fmt:message key="manage_settings.enable_terms_of_agreement"/></span>
					</label>
					<label>
						<form:textarea id="standard_terms" path="standardTerms" cssClass="span9" rows="3"/>
						<span></span>
					</label>
				<span class="help-block">
					<fmt:message key="manage_settings.display_when_worker_accepts_declines"/>
				</span>
				</div>

				<div class="clearfix">
					<fmt:message key="manage_settings.code_of_conduct" var="manage_settings_code_of_conduct"/>
					<fmt:message key="global.optional" var="global_option"/>
					<form:label path="standardInstructions"><h5>${manage_settings_code_of_conduct} <small class="muted">(${global_option})</small></h5></form:label>
					<label>
						<form:checkbox id="standard_instructions_flag" path="standardInstructionsFlag" data-well="#standard_instructions"/>
						<span><fmt:message key="manage_settings.enable_code_of_conduct"/></span>
					</label>
					<label>
						<input type="hidden" name="maxlength" value="500" />
						<form:textarea id="standard_instructions" path="standardInstructions" cssClass="span9" rows="5"/>
					</label>
					<span class="help-block">
						<fmt:message key="manage_settings.include_details"/>
					</span>
				</div>

				<div class="clearfix">
					<form:label path="enableAssignmentPrintout"><h5><fmt:message key="global.assignment_settings"/></h5></form:label>
					<label class="radio inline">
						<c:set var="wmPrintout"><c:if test="${requestScope.printedForm eq 'wm'}">checked="checked"</c:if></c:set>
						<input class="radio-aligned" type="radio" id="enable_print"name="printedForm" value="wm" ${wmPrintout}>
						<span><fmt:message key="manage_settings.use_wm_printout"/></span>
					</label>
					<label class="inline radio">
						<c:set var="companyPrintout"><c:if test="${requestScope.printedForm eq 'company'}">checked="checked"</c:if></c:set>
						<input class="radio-aligned" type="radio"  id="disable_wm_print" name="printedForm" value="company" ${companyPrintout}>
						<span><fmt:message key="manage_settings.use_company_printout"/></span>
					</label>
					<span id="buyer_printout_help" class="help-block dn">
						<fmt:message key="manage_settings.separate_assignment_form"/>
					</span>
				</div>
				<hr/>
				<fieldset class="form-horizontal" id="printout-settings">
					<p id="print_settings_header"><strong><fmt:message key="manage_settings.customize_wm_printout_settings"/></strong></p>

					<div class="control-group">
						<label class="control-label"><fmt:message key="global.logo"/></label>
						<div class="controls">
							<select path="useCompanyLogoFlag" name="printedFormLogo">
								<option value="wm" <c:if test="${requestScope.printedFormLogo eq 'wm'}">selected</c:if> ><fmt:message key="manage_settings.use_wm_logo"/></option>
								<option value="company" <c:if test="${requestScope.printedFormLogo eq 'company'}">selected</c:if> ><fmt:message key="manage_settings.use_company_logo"/></option>
								<option value="none" <c:if test="${requestScope.printedFormLogo eq 'none'}">selected</c:if> ><fmt:message key="manage_settings.do_not_use_either_logo"/></option>
							</select>
						</div>
					</div>

					<div class="control-group">
						<form:label path="standardTermsEndUserFlag" cssClass="control-label"><fmt:message key="manage_settings.end_user_general_terms"/></form:label>
						<div class="controls">
							<form:checkbox id="enduser_terms" path="standardTermsEndUserFlag" data-well="#enduser_terms_text"/>
							<span><fmt:message key="manage_settings.include_end_user_general_terms_on_printout"/></span>
							<span class="help-block"><fmt:message key="manage_settings.end_user_general_terms_displayed_on_assignment_printout"/></span>
							<form:label path="standardTermsEndUser"></form:label>
							<form:textarea id="enduser_terms_text" path="standardTermsEndUser" cssClass="span8" rows="3"/>
						</div>
					</div>

					<div class="control-group">
						<form:label path="enablePrintoutSignature" cssClass="control-label"><fmt:message key="global.signature"/></form:label>
						<div class="controls">
							<form:checkbox id="printout_signature" path="enablePrintoutSignature" data-well="#custom_signature_line"/>
							<span><fmt:message key="manage_settings.include_signature_section"/></span>
							<span class="help-block"><fmt:message key="manage_settings.signature_section_displayed_on_assignment_printout"/></span>
							<input type="hidden" maxlength="1024" />
							<textarea id="custom_signature_line" name="custom_signature_line" class="span8" rows="3"><c:out value="${requestScope.custom_signature_line}" /></textarea>
						</div>
					</div>

					<div class="control-group">
						<form:label path="badgeIncludedOnPrintout" cssClass="control-label"><fmt:message key="global.badge"/></form:label>
						<div class="controls">
							<form:checkbox path="badgeIncludedOnPrintout"/>
							<span><fmt:message key="manage_settings.include_badge_in_printout"/></span>
							<span class="help-block"><a id="render_preview" href="javascript:void(0);"><fmt:message key="manage_settings.signature_section_displayed_on_assignment_printout"/></a></span>
						</div>
					</div>

				</fieldset>

				<div class="clearfix">
					<div class="clearfix">
					<sec:authorize access="hasFeature('projectBudget')">
						<div class="clearfix" >
							<form:label path="budgetEnabledFlag">
								<h5><fmt:message key="manage_settings.project_budget_management"/></h5>
							</form:label>

							<label>
								<form:checkbox path="budgetEnabledFlag" id="budget_enabled_flag"/>
								<span><fmt:message key="manage_settings.enable_project_budget_management"/></span>
							</label>
							<span class="help-block">
								<fmt:message key="manage_settings.allows_budget_association_with_project"/>
							</span>

						</div>
					</sec:authorize>

					<div class="page-header clear">
						<h3><fmt:message key="manage_settings.advanced_assignment_settings"/></h3>
					</div>

					<sec:authorize access="hasFeature('requireProject')">
						<fieldset>
							<div class="clearfix mb">
								<form:label path="requireProjectEnabledFlag"><h5><fmt:message key="manage_settings.clients_and_projects"/></h5></form:label>
								<div class="input">

									<label>
										<form:checkbox path="requireProjectEnabledFlag"/>
										<span><fmt:message key="manage_settings.require_assignments_to_have_client_and_project"/></span>
									</label>

								</div>
							</div>
						</fieldset>
					</sec:authorize>


					<div class="clearfix">
						<form:label path="useRequirementSets">
							<h5><fmt:message key="manage_settings.worker_requirements"/></h5>
						</form:label>

						<label>
							<form:checkbox path="useRequirementSets"/>
							<span><fmt:message key="manage_settings.enable_worker_requirements"/></span>
						</label>
					<span class="help-block">
						<fmt:message key="manage_settings.ensure_workers_eligibility"/>
					</span>

					</div>

					<div class="clearfix">
						<form:label path="partsLogisticsEnabledFlag"><h5><fmt:message key="manage_settings.parts_and_logistics"/></h5></form:label>
						<label>
							<form:checkbox path="partsLogisticsEnabledFlag"/>
							<span><fmt:message key="manage_settings.enable_parts_logistics"/></span>
						</label>
						<span class="help-block">
							<fmt:message key="manage_settings.company_requires_parts_and_logistics"/>
						</span>
					</div>

					<div class="clearfix">
						<form:label path="assessmentsEnabled"><h5><fmt:message key="global.surveys"/></h5></form:label>
						<label>
							<form:checkbox path="assessmentsEnabled"/>
							<span><fmt:message key="manage_settings.enable_surveys"/></span>
						</label>
						<span class="help-block">
							<fmt:message key="manage_settings.company_surveys_workers_on_assignment"/>
						</span>
					</div>

					<div class="clearfix">
						<h5><fmt:message key="global.documents"/></h5>
						<label>
							<c:set var="documentsEnabled"><c:if test="${requestScope.documentsEnabled}">checked="checked"</c:if></c:set>
							<input class="radio-aligned" type="checkbox" id="documents_enabled" name="documentsEnabled" ${documentsEnabled} />
							<span><fmt:message key="manage_settings.enable_documents"/></span>
						</label>
						<span class="help-block">
							<fmt:message key="manage_settings.company_documents"/>
						</span>
					</div>

					<div class="clearfix">
						<h5><fmt:message key="global.assignment_contact"/></h5>
						<label>
							<c:set var="hideContactEnabled"><c:if test="${requestScope.hideContactEnabled}">checked="checked"</c:if></c:set>
							<input class="radio-aligned" type="checkbox" id="hide_contact_enabled" name="hideContactEnabled" ${hideContactEnabled} />
							<span><fmt:message key="manage_settings.enable_hide_contact"/></span>
						</label>
						<span class="help-block">
							<fmt:message key="manage_settings.company_hide_contact"/>
						</span>
					</div>

					<div class="clearfix">
						<form:label path="autoRateEnabledFlag"><h5><fmt:message key="global.auto_rate"/></h5></form:label>
						<label>
							<form:checkbox path="autoRateEnabledFlag"/>
							<span><fmt:message key="manage_settings.enable_auto_rate"/></span>
						</label>
						<span class="help-block">
							<fmt:message key="manage_settings.default_worker_rating"/>
						</span>
					</div>

						<sec:authorize access="hasFeature('uniqueExternalId')">
						<div class="clearfix">
							<label cssClass="control-label"><h5><fmt:message key="manage_settings.require_unique_id"/></h5></label>
							<label>
								<c:set var="requireUniqueIdChecked"><c:if test="${requestScope.requireUniqueIdFlag}">checked="checked"</c:if></c:set>
								<input id="require_unique_id_flag" name="requireUniqueIdFlag" type="checkbox" ${requireUniqueIdChecked}/>
								<span><fmt:message key="manage_settings.include_required_unique_id_on_assignments"/></span>
							</label>
							<span class="clearfix dn" id="unique_id_name_view">
								<input id="unique_id_name" name="uniqueIdName" class="span8" placeholder="ID Name (Purchase Order, Invoice Number, etc.)" type="text" value="${requestScope.uniqueIdName}"/>
								<c:if test="${requestScope.uniqueIdNameVersion gt '0'}">
									<span class="help-block inline">
										(Version: <c:out value="${requestScope.uniqueIdNameVersion}"/>)
									</span>
								</c:if>
								<span class="help-block">
									<fmt:message key="manage_settings.unique_data_required"/>
								</span>
                        	</span>
							<input type="hidden" id="unique_id_name_active" name="uniqueIdNameActive" value="${requestScope.uniqueIdName}"/>
						</div>
						</sec:authorize>

						<div class="clearfix">
							<form:label path="autocloseEnabledFlag"><h5><fmt:message key="global.auto_close"/></h5></form:label>
							<label>
								<form:checkbox path="autocloseEnabledFlag"/>
								<span><fmt:message key="manage_settings.enable_auto_close"/>: <form:input path="autocloseDelayInHours" maxlength="2" cssClass="span1"/> <fmt:message key="manage_settings.hours_after_work_complete"/></span>
							</label>
						<span class="help-block">
							 <fmt:message key="manage_settings.assignments_closed_details"/>
						</span>
					</div>

					<div class="clearfix">
						<form:label path="customCloseOutEnabledFlag"><h5><fmt:message key="manage_settings.custom_deliverables_configuration"/></h5></form:label>
						<label>
							<form:checkbox path="customCloseOutEnabledFlag"/>
							<span><fmt:message key="manage_settings.enable_custom_deiverables_configuration"/></span>
						</label>
						<span class="help-block">
							<fmt:message key="manage_settings.specify_type_of_deliverables"/>
						</span>
					</div>

					<div class="clearfix">
						<form:label path="ivrEnabledFlag"><h5><fmt:message key="global.ivr"/></h5></form:label>
						<label>
							<form:checkbox path="ivrEnabledFlag"/>
							<span><fmt:message key="manage_settings.enable_ivr"/></span>
						</label>
						<span class="help-block">
							<fmt:message key="manage_settings.company_features_to_update_assignment_status"/>
						</span>
					</div>

					<div class="clearfix">
						<form:label path="agingAssignmentAlertEnabled"><h5><fmt:message key="manage_settings.aging_assignment_email"/></h5></form:label>
						<label>
							<form:checkbox path="agingAssignmentAlertEnabled"/>
							<span><fmt:message key="manage_settings.enable_aging_assignment_email"/></span>
						</label>
						<label>
							<input type="hidden" maxlength="256" />
							<textarea id="assignment_aging_email" name="assignment_aging_email" cssClass="span11" class="span11" placeholder="Enter an email address to receive this email."><c:out value="${requestScope.assignment_aging_email}" /></textarea>
						</label>
					</div>

					<div class="wm-action-container">
						<button type="submit" class="button"><fmt:message key="global.save_changes"/></button>
					</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>

</wm:app>
