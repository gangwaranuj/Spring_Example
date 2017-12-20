package com.workmarket.api.v1.assignments;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v1.model.ApiCustomFieldDTO;
import com.workmarket.api.v1.model.ApiCustomFieldGroupDTO;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Api(tags = "Custom Fields")
@Controller("CustomfieldsController")
@RequestMapping(value = {"/v1/api/assignments/customfields", "/api/v1/assignments/customfields"})
public class CustomfieldsController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(CustomfieldsController.class);

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private CustomFieldService customFieldService;

	@ApiOperation(value = "List custom fields")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiCustomFieldGroupDTO>> list() {
		ApiV1Response<List<ApiCustomFieldGroupDTO>> apiResponse = new ApiV1Response<>();
		MessageBundle bundle = messageHelper.newBundle();
		Long companyId = authenticationService.getCurrentUser().getCompany().getId();

		logger.debug("retrieving custom field list for companyId={}", companyId);

		List<WorkCustomFieldGroup> customFieldGroups = customFieldService.findWorkCustomFieldGroups(companyId);

		if (!customFieldGroups.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("found {} work custom field groups for companyId={}", customFieldGroups.size(), companyId);
			}

			List<Map<String,Object>> itemList = new LinkedList<Map<String, Object>>();

			List<ApiCustomFieldGroupDTO> customFieldGroupDTOList = new LinkedList<>();
			for (WorkCustomFieldGroup group : customFieldGroups) {
				List<ApiCustomFieldDTO> fields = new LinkedList<>();
				WorkCustomFieldGroup fieldGroup = customFieldService.findWorkCustomFieldGroup(group.getId());

				if (fieldGroup != null) {
					for (WorkCustomField field : customFieldService.findAllFieldsForCustomFieldGroup(fieldGroup.getId())) {
						fields.add(
							new ApiCustomFieldDTO.Builder()
								.withId(field.getId())
								.withName(field.getName())
								.withDefaultValue(field.getDefaultValue())
								.withRequired(field.getRequiredFlag())
								.build()
						);
					}
				}
				else {
					logger.debug("work custom field group not found for groupId={} and companyId={}", group.getId(), companyId);
				}

				customFieldGroupDTOList.add(new ApiCustomFieldGroupDTO.Builder()
					.withId(group.getId())
					.withName(group.getName())
					.withFields(fields)
					.withRequired(group.isRequired())
					.build()
				);
			}

			apiResponse.setResponse(customFieldGroupDTOList);
		}
		else {
			messageHelper.addError(bundle, "api.v1.assignments.customfields.list.error");
			logger.debug("custom field list for companyId={} is empty!", companyId);
		}

		if (bundle.hasErrors()) {
			apiResponse.getMeta().setErrorMessages(bundle.getErrors());
		}

		return apiResponse;
	}
}