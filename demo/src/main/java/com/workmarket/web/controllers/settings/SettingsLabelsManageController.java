package com.workmarket.web.controllers.settings;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.WorkTemplatePagination;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;
import com.workmarket.domains.work.service.LabelValidationService;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.dto.WorkSubStatusTypeCompanySettingDTO;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.mmw.LabelsManageForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.workmarket.utility.CollectionUtilities.newObjectMap;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings/manage")
public class SettingsLabelsManageController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SettingsLabelsManageController.class);

	@Autowired private CompanyService companyService;
	@Autowired private EventFactory eventFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private WorkIndexer workIndexer;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private WorkTemplateService workTemplateService;
	@Autowired private LabelValidationService labelValidationService;

	@SuppressWarnings("unchecked")
	@ModelAttribute("labelsManageForm")
	public LabelsManageForm populateLabelsManageForm(@RequestParam(value = "id", required = false) Long id) {

		if (id == null) {
			//set for new
			LabelsManageForm form = new LabelsManageForm();
			form.setWorkStatusTypeScopeRangeFrom(0);
			form.setWorkStatusTypeScopeRangeTo(WorkStatusType.ScopeRange.values().length);
			return form;
		}

		WorkSubStatusType workSubStatus = workSubStatusService.findCustomWorkSubStatusByCompany(id, getCurrentUser().getCompanyId());

		if (workSubStatus == null) {
			return null; // invalid case, should fail in the request handler
		}

		LabelsManageForm form = new LabelsManageForm();
		form.setWorkSubStatusTypeId(workSubStatus.getId());
		form.setActive(workSubStatus.isActive());
		form.setCompanyId(getCurrentUser().getCompanyId());
		form.setCode(workSubStatus.getCode());
		form.setDescription(workSubStatus.getDescription());

		// set resource access
		form.setResourceEditable(workSubStatus.isResourceEditable());
		form.setResourceVisible(workSubStatus.getResourceVisible());

		if (!form.isResourceEditable() && workSubStatus.getResourceVisible()) {
			form.setResourceAccess("view");
		} else if (form.isResourceEditable() && form.getResourceVisible()) {
			form.setResourceAccess("view_edit");
		}

		// set notify
		form.setNotifyClientEnabled(workSubStatus.isNotifyClientEnabled());
		form.setNotifyResourceEnabled(workSubStatus.isNotifyResourceEnabled());

		if (form.isNotifyClientEnabled() && !form.isNotifyResourceEnabled()) {
			form.setNotify("io");
		} else if (!form.isNotifyClientEnabled() && form.isNotifyResourceEnabled()) {
			form.setNotify("r");
		} else if (form.isNotifyClientEnabled() && form.isNotifyResourceEnabled()) {
			form.setNotify("io_r");
		}

		List<String> recipientIds = workSubStatusService.findAllRecipientsUserNumbersByWorkSubStatusId(workSubStatus.getId());
		form.setWorkSubStatusTypeRecipientIds(recipientIds.toArray(new String[recipientIds.size()]));

		form.setAlert(workSubStatus.isAlert());
		// set Note required and resource access
		form.setNoteRequired(workSubStatus.isNoteRequired());
		if (workSubStatus.isNoteRequired() && workSubStatus.getNotePrivacy().equals(PrivacyType.PUBLIC)) {
			form.setNoteRequiredAccess("sh");
		}
		form.setIncludeInstructions(workSubStatus.isIncludeInstructions());
		form.setInstructions(workSubStatus.getInstructions());
		form.setScheduleRequired(workSubStatus.isScheduleRequired());
		form.setRemoveAfterReschedule(workSubStatus.isRemoveAfterReschedule());

		//set label scope
		Integer[] workStatusTypeScopeRangeIndex = WorkStatusType.ScopeRange.getWorkStatusTypeScopeRangeAsIndex(CollectionUtilities.newSetPropertyProjection(workSubStatus.getWorkScopes(), "code"));
		form.setWorkStatusTypeScopeRangeFrom(workStatusTypeScopeRangeIndex[0]);
		form.setWorkStatusTypeScopeRangeTo(workStatusTypeScopeRangeIndex[1]);

		//set remove on void or cancel
		form.setRemoveOnVoidOrCancelled(workSubStatus.isRemoveOnVoidOrCancelled());
		//set templates
		form.setWorkTemplateIds(CollectionUtilities.newGenericArrayPropertyProjection(workSubStatus.getWorkTemplates(), Long.class, "id"));
		return form;
	}


	@RequestMapping(value="/labels", method = GET)
	public String displayLabels(Model model) {
		WorkSubStatusTypeFilter filter = new WorkSubStatusTypeFilter();
		filter.setShowSystemSubStatus(true);
		filter.setShowCustomSubStatus(false);
		filter.setShowDeactivated(true);
		filter.setClientVisible(true);
		filter.setResourceVisible(true);
		model.addAttribute("dashboard", workSubStatusService.findWorkSubStatusDashboardByCompany(filter));

		filter = new WorkSubStatusTypeFilter();
		filter.setShowSystemSubStatus(false);
		filter.setShowCustomSubStatus(true);
		filter.setShowDeactivated(true);
		filter.setClientVisible(true);
		filter.setResourceVisible(true);
		model.addAttribute("customDashboard", workSubStatusService.findWorkSubStatusDashboardByCompany(filter));

		return "web/pages/settings/manage/labels";
	}

	@RequestMapping(
		value = "/label_delete/{id}",
		method = GET)
	public String labelDelete(@PathVariable("id") long workSubStatusId, RedirectAttributes model) {
		MessageBundle bundle = MessageBundle.newInstance();
		model.addFlashAttribute("bundle", bundle);

		try {
			List<WorkSubStatusTypeAssociation> associations =
				workSubStatusService.findAllWorkSubStatusTypeAssociationBySubStatusId(workSubStatusId);
			Collection<Long> workIdsWithLabel =
				extract(associations, on(WorkSubStatusTypeAssociation.class).getWork().getId());
			workSubStatusService.deleteWorkSubStatus(workSubStatusId);
			workSubStatusService.deleteAllWorkSubStatusTypeRecipientAssociationsByWorkSubStatusId(workSubStatusId);
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workIdsWithLabel));
			messageHelper.addSuccess(bundle, "mmw.manage.label_delete.success");
		} catch (Exception ex) {
			logger.error("failed to delete label with workSubStatusId={}", workSubStatusId, ex);
			messageHelper.addError(bundle, "mmw.manage.label_delete.failure");
		}

		return "redirect:/settings/manage/labels";
	}

	@RequestMapping(
		value="/label_dashboard_display/{id}",
		method = RequestMethod.POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder labelDashboardDisplay(
		@PathVariable("id") Long id,
		@RequestParam("type") WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType) {

		WorkSubStatusTypeCompanySettingDTO dto = new WorkSubStatusTypeCompanySettingDTO();
		dto.setWorkSubStatusTypeId(id);
		dto.setDashboardDisplayType(dashboardDisplayType);

		try {
			workSubStatusService.saveWorkSubStatusTypeCompanySetting(getCurrentUser().getCompanyId(), dto);
			return new AjaxResponseBuilder().setSuccessful(true);
		} catch (Exception ex) {
			logger.error("failed to save WorkSubStatusTypeCompanySetting for id={} and dashboardDisplayType={}", new Object[] {id, dashboardDisplayType}, ex);
			return new AjaxResponseBuilder().setSuccessful(false);
		}
	}

	@RequestMapping(value="/label_color", method = GET)
	public String labelColor() {
		return "web/pages/settings/manage/labels";
	}

	@RequestMapping(
		value="/label_color",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder saveLabelColor(@RequestParam("id") Long id, @RequestParam("color") String color) {
		WorkSubStatusTypeCompanySettingDTO dto = new WorkSubStatusTypeCompanySettingDTO();
		dto.setWorkSubStatusTypeId(id);
		dto.setColorRgb(color);

		try {
			workSubStatusService.saveWorkSubStatusTypeCompanySetting(getCurrentUser().getCompanyId(), dto);
			return new AjaxResponseBuilder().setSuccessful(true);
		} catch (Exception ex) {
			logger.error("failed to save WorkSubStatusTypeCompanySetting for id={} and color={}", new Object[] {id, color}, ex);
			return new AjaxResponseBuilder().setSuccessful(false);
		}
	}

	@RequestMapping(
		value = "/labels_manage",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	public @ResponseBody AjaxResponseBuilder displayLabelsManage(
		@ModelAttribute("labelsManageForm") LabelsManageForm form,
		Model model) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		Boolean templatesEnabled = companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId()).getCustomFormsEnabledFlag();

		if (templatesEnabled) {
			WorkTemplatePagination pagination = new WorkTemplatePagination(true);
			pagination.setSortColumn(WorkTemplatePagination.SORTS.NAME.toString());
			pagination.setSortDirection(Pagination.SORT_DIRECTION.ASC);

			pagination = workTemplateService.findAllActiveWorkTemplates(getCurrentUser().getCompanyId(), pagination);
			Map<Long, String> templateMap = Maps.newLinkedHashMap();

			for (WorkTemplate template : pagination.getResults()) {
				templateMap.put(template.getId(), template.getTemplateName());
			}
			response.addData("templates", templateMap);
			response.addData("form", form);
			response.addData("templatesEnabled", templatesEnabled);
			response.addData("companyId", getCurrentUser().getCompanyId());
		}

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/labels_manage",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder saveLabelsManage(@ModelAttribute("labelsManageForm") LabelsManageForm form) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (form == null) {
			messageHelper.addMessage(response, "mmw.manage.labels.exception");
			return response.setRedirect("/settings/manage/labels");
		}

		form.setResourceEditable(form.shouldBeResourceEditable());
		form.setResourceVisible(form.shouldBeResourceVisible());
		form.setNotifyClientEnabled(form.shouldBeNotifyClientEnabled());
		form.setNotifyResourceEnabled(form.shouldBeNotifyResourceEnabled());

		if (form.isNoteRequired() && form.getNoteRequiredAccess().equals("sh")) {
			form.setNotePrivacyType(PrivacyType.PUBLIC);
		} else if (form.isNoteRequired()) {
			form.setNotePrivacyType(PrivacyType.PRIVATE);
		}

		try {
			WorkSubStatusTypeDTO dto = new WorkSubStatusTypeDTO();
			BeanUtils.copyProperties(form, dto);
			if (!(form.getWorkStatusTypeScopeRangeFrom() == 0 && form.getWorkStatusTypeScopeRangeTo() == WorkStatusType.ScopeRange.values().length - 1)) {
				Set<String> allWorkStatusCodesInScopeRange = WorkStatusType.ScopeRange.getAllWorkStatusCodesInScopeRange(
					form.getWorkStatusTypeScopeRangeFrom(), form.getWorkStatusTypeScopeRangeTo()
				);
				dto.setWorkStatusCodes(allWorkStatusCodesInScopeRange.toArray(new String[allWorkStatusCodesInScopeRange.size()]));
			} else {
				dto.setWorkStatusCodes(ArrayUtils.EMPTY_STRING_ARRAY);
			}
			dto.setCompanyId(getCurrentUser().getCompanyId());
			dto.setCode(form.getDescription().toLowerCase().replaceAll("\\p{Punct}", "").replaceAll("\\s+", "_"));

			List<ConstraintViolation> violations = labelValidationService.validateLabel(dto, messageHelper);
			if (!violations.isEmpty()){
				for(ConstraintViolation error : violations)
					messageHelper.addMessage(response, error.getError(), error.getProperty());
				return response;
			}
			else {
				WorkSubStatusType newLabel = workSubStatusService.saveOrUpdateCustomWorkSubStatus(dto);
				eventRouter.sendEvent(eventFactory.buildWorkSubStatusTypeUpdatedEvent(getCurrentUser().getId(), newLabel.getId()));

				return response
					.setSuccessful(true)
					.setData(newObjectMap(
						"id", newLabel.getId(),
						"code", newLabel.getCode()));
			}
		} catch (Exception ex) {
			logger.error("failed to save CustomWorkSubStatus", ex);
		}

		messageHelper.addMessage(response, "mmw.manage.labels_manage.error");
		return response;
	}

}
