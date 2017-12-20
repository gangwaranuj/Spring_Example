package com.workmarket.web.controllers.settings;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkCustomFieldGroupDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings/manage")
public class SettingsCustomFieldsController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(SettingsCustomFieldsController.class);

	@Autowired private CompanyService companyService;
	@Autowired private CustomFieldService customFieldService;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private MessageBundleHelper messageHelper;

	@ModelAttribute("mmw")
	public ManageMyWorkMarket createModel() {
		ManageMyWorkMarket mmw = companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId());

		if (mmw == null) {
			mmw = new ManageMyWorkMarket();
		}

		return mmw;
	}

	@RequestMapping(
		value="/load_inactive_customfields",
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE)
	public void listInactiveCustomFields(HttpServletRequest httpRequest, Model model) {

		List<WorkCustomFieldGroup> fieldGroups = customFieldService.findInactiveWorkCustomFieldGroups(getCurrentUser().getCompanyId());

		DataTablesResponse<List<String>, Map<String, Object>> response = getListMapDataTablesResponse(httpRequest, fieldGroups);

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value="/load_customfields",
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE)
	public void listCustomFields(HttpServletRequest httpRequest, Model model) {

		List<WorkCustomFieldGroup> fieldGroups = customFieldService.findActiveWorkCustomFieldGroups(getCurrentUser().getCompanyId());

		DataTablesResponse<List<String>, Map<String, Object>> response = getListMapDataTablesResponse(httpRequest, fieldGroups);

		model.addAttribute("response", response);
	}

	private DataTablesResponse<List<String>, Map<String, Object>> getListMapDataTablesResponse(HttpServletRequest httpRequest, List<WorkCustomFieldGroup> fieldGroups) {
		Pagination<WorkCustomFieldGroup> pagination = new AbstractPagination<WorkCustomFieldGroup>() {
			// no methods to implement
		};

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		pagination.setStartRow(request.getStart());
		pagination.setRowCount(fieldGroups.size());

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		pagination.setReturnAllRows();
		for (WorkCustomFieldGroup fieldGroup : fieldGroups) {
			List<String> data = Lists.newArrayList(
				fieldGroup.getName(),
				"",
				String.valueOf(fieldGroup.getId())
			);

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"name", fieldGroup.getName(),
				"required", fieldGroup.isRequired(),
				"id", fieldGroup.getId()
			);

			response.addRow(data, meta);
		}
		return response;
	}

	@ModelAttribute("workCustomFieldGroupDTO")
	public WorkCustomFieldGroupDTO createModel(
		@RequestParam(value = "id", required = false) Long id,
		HttpServletRequest request) {

		WorkCustomFieldGroupDTO dto = new WorkCustomFieldGroupDTO();

		if ("GET".equalsIgnoreCase(request.getMethod()) && (id != null)) {
			WorkCustomFieldGroup fieldGroup = customFieldService.findWorkCustomFieldGroupByCompany(id, getCurrentUser().getCompanyId());

			if (fieldGroup != null) {
				dto.setWorkCustomFieldGroupId(fieldGroup.getId());
				dto.setName(fieldGroup.getName());
				dto.setRequired(fieldGroup.isRequired());

				List<WorkCustomFieldDTO> customFieldDTOs = new LinkedList<>();
				dto.setWorkCustomFields(customFieldDTOs);

				for (WorkCustomField customField : customFieldService.findAllFieldsForCustomFieldGroup(fieldGroup.getId())) {
					WorkCustomFieldDTO customFieldDTO = new WorkCustomFieldDTO();
					BeanUtils.copyProperties(customField, customFieldDTO);
					customFieldDTO.setWorkCustomFieldTypeCode(customField.getWorkCustomFieldType().getCode());
					customFieldDTOs.add(customFieldDTO);
				}
			} else {
				dto = null;
			}
		}

		return dto;
	}

	@RequestMapping(value="/customfields", method = GET)
	public String customFields() {
		return "web/pages/settings/manage/customfields/active";
	}

	@RequestMapping(value="/customfields/inactive", method = GET)
	public String customFieldsInactive() {
		return "web/pages/settings/manage/customfields/inactive";
	}

	@RequestMapping(value = "/custom_fields_edit", method = GET)
	public String customFieldsEdit(
		@ModelAttribute("workCustomFieldGroupDTO") WorkCustomFieldGroupDTO dto,
		Model model,
		RedirectAttributes flash) {

		if (dto == null) {
			MessageBundle bundle = messageHelper.newFlashBundle(flash);
			messageHelper.addError(bundle, "mmw.manage.custom_fields_edit.notfound");

			return "redirect:/settings/manage/customfields";
		}

		model.addAttribute("field_group_json", jsonSerializationService.toJson(dto));
		model.addAttribute("companyName", getCurrentUser().getCompanyName());

		return "web/pages/settings/manage/customfields/edit";
	}

	@RequestMapping(value = "/custom_fields_edit", method = POST)
	public String saveCustomFields(
		@Valid @ModelAttribute("workCustomFieldGroupDTO") WorkCustomFieldGroupDTO dto,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes flash) {

		if (dto == null) {
			MessageBundle bundle = messageHelper.newFlashBundle(flash);
			messageHelper.addError(bundle, "mmw.manage.custom_fields_edit.notfound");
			return "redirect:/settings/manage/customfields";
		}

		if (dto.getWorkCustomFields() != null) {
			Integer position = 0;
			for (WorkCustomFieldDTO field : dto.getWorkCustomFields()) {
				field.setDefaultValue(field.getValue());
				field.setPosition(position++);
				field.setVisibleToOwnerFlag(Boolean.TRUE);
			}
		}

		if (bindingResult.hasErrors()) {
			MessageBundle messages = messageHelper.newBundle();
			messageHelper.setErrors(messages, bindingResult);

			model.addAttribute("bundle", messages);
			model.addAttribute("field_group_json", jsonSerializationService.toJson(dto));
			model.addAttribute("companyName", getCurrentUser().getCompanyName());

			return "web/pages/settings/manage/customfields/edit";
		}

		MessageBundle messages = messageHelper.newFlashBundle(flash);
		try {
			customFieldService.saveOrUpdateWorkFieldGroup(getCurrentUser().getId(), dto);
			messageHelper.addSuccess(messages, "mmw.manage.custom_fields_edit.success");
		} catch (Exception e) {
			messageHelper.addError(messages, "mmw.manage.custom_fields_edit.error");
			logger.warn(String.format("Error saving custom fields for group %d, user %d: ", dto.getWorkCustomFieldGroupId(), getCurrentUser().getId()), e);
		}

		return "redirect:/settings/manage/customfields";
	}

	@RequestMapping(
		value = "/custom_fields_remove",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody
	AjaxResponseBuilder customFieldsRemove(
		@RequestParam(value = "id", required = false) Long fieldGroupId) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		try {
			customFieldService.deleteWorkCustomFieldGroupByCompany(fieldGroupId, getCurrentUser().getCompanyId());
			messageHelper.addMessage(response, "mmw.manage.custom_fields_remove.success");
			response.setSuccessful(true);
		} catch (Exception e) {
			messageHelper.addMessage(response, "mmw.manage.custom_fields_remove.exception");
			logger.warn(String.format("Error removing custom fields for group %d, user %d: ", fieldGroupId, getCurrentUser().getId()), e);
		}

		return response;
	}

	@RequestMapping(
		value = "/custom_fields_copy",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody
	AjaxResponseBuilder customFieldsCopySubmit(
		@RequestParam(value = "id", required = true) Long fieldGroupId,
		@RequestParam String name) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		if (isBlank(name) || name.length() > WorkCustomFieldGroup.MAX_NAME_LENGTH) {
			messageHelper.addMessage(response, "Size", "Custom Field Set Name", WorkCustomFieldGroup.MAX_NAME_LENGTH, 1);
			return response;
		}

		try {
			customFieldService.copyCustomFieldGroupByCompany(fieldGroupId, name, getCurrentUser().getCompanyId());
			messageHelper.addMessage(response, "mmw.manage.custom_fields_copy.success", name);
			response.setSuccessful(true);
		} catch (Exception e) {
			messageHelper.addMessage(response, "mmw.manage.custom_fields_copy.exception");
			logger.error(String.format("Error copying custom fields for group %d, user %d: ", fieldGroupId, getCurrentUser().getId()), e);
		}
		return response;
	}

	@RequestMapping(
		value = "/custom_fields_required",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder customFieldsRequired(
		@RequestParam(value = "id", required = false) Long fieldGroupId) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		if (fieldGroupId != null) {
			WorkCustomFieldGroup fieldGroup = customFieldService.findWorkCustomFieldGroup(fieldGroupId);

			if (fieldGroup != null) {
				try {
					customFieldService.requireWorkCustomFieldGroup(fieldGroup.getId(), getCurrentUser().getCompanyId(), !fieldGroup.isRequired());
					response.setSuccessful(true);
				} catch (Exception ex) {
					logger.error("error setting requireWorkCustomFieldGroup for fieldGroupId={}", fieldGroupId, ex);
					messageHelper.addMessage(response, "mmw.manage.custom_fields_required.error");
					return response;
				}
			} else {
				messageHelper.addMessage(response, "mmw.manage.custom_fields_required.notfound");
				return response;
			}
		}
		messageHelper.addMessage(response, "mmw.manage.custom_fields_required.success");
		return response;
	}

	@RequestMapping(
		value = "/custom_fields_activate",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder customFieldsActivate(
		@RequestParam(value = "id", required = false) Long fieldGroupId) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		if (fieldGroupId != null) {
			try {
				customFieldService.activateWorkCustomFieldGroupByCompany(fieldGroupId, getCurrentUser().getCompanyId());
			} catch (Exception ex) {
				logger.error("error activating for fieldGroupId={}", fieldGroupId, ex);
				messageHelper.addMessage(response, "mmw.manage.custom_fields_activate.error");
				return response;
			}
		}
		response.setSuccessful(true);
		messageHelper.addMessage(response, "mmw.manage.custom_fields_activate.success");
		return response;
	}

	@RequestMapping(
		value = "/custom_fields_deactivate",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder customFieldsDeactivate(
		@RequestParam(value = "id", required = false) Long fieldGroupId) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		if (fieldGroupId != null) {
			try {
				customFieldService.deactivateWorkCustomFieldGroupByCompany(fieldGroupId, getCurrentUser().getCompanyId());
			} catch (Exception ex) {
				logger.error("error deactivating for fieldGroupId={}", fieldGroupId, ex);
				messageHelper.addMessage(response, "mmw.manage.custom_fields_deactivate.error");
				return response;
			}
		}
		response.setSuccessful(true);
		messageHelper.addMessage(response, "mmw.manage.custom_fields_deactivate.success");
		return response;
	}
}
