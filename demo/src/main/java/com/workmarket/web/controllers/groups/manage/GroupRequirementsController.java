package com.workmarket.web.controllers.groups.manage;

import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.service.UserGroupRequirementSetService;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.UserUserGroupDocumentReferenceService;
import com.workmarket.service.business.dto.UploadDocumentDTO;
import com.workmarket.service.business.event.group.GroupUpdateSearchIndexEvent;
import com.workmarket.service.business.requirementsets.RequirementSetsSerializationService;
import com.workmarket.service.business.requirementsets.RequirementSetsService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/groups")
public class GroupRequirementsController extends BaseGroupsManageController {

	private final static String GROUP_REQUIREMENTS_MESSAGES_PARAM = "group.requirements.messages";

	private static final Logger logger = LoggerFactory.getLogger(GroupRequirementsController.class);

	@Autowired UserUserGroupDocumentReferenceService userUserGroupDocumentReferenceService;
	@Autowired UserGroupRequirementSetService userGroupRequirementSetService;
	@Autowired RequirementSetsSerializationService requirementSetsSerializationService;
	@Autowired RequirementSetsService requirementSetsService;
	@Autowired IndustryService industryService;

	@ModelAttribute("group")
	public UserGroup getGroup(@PathVariable("id") Long groupId) {
		return getGroupService().findGroupById(groupId);
	}

	@ResponseBody
	@RequestMapping(
		value = {"/{id}/requirements"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	public String getRequirementSet(@PathVariable("id") Long groupId) {
		return requirementSetsSerializationService.toJson(
			userGroupRequirementSetService.findOrCreateRequirementSetByUserGroupId(groupId)
		);
	}

	@ResponseBody
	@RequestMapping(
		value = {"/{id}/requirements/{requirementSetId}"},
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	public AjaxResponseBuilder update(
		@PathVariable("id") Long userGroupId,
		@RequestParam("model") String model
	) {
		RequirementSet requirementSet = userGroupRequirementSetService.findOrCreateRequirementSetByUserGroupId(userGroupId);
		String requirementSetData = StringEscapeUtils.unescapeHtml4(model);
		RequirementSet updatedRequirementSet = requirementSetsSerializationService.mergeJson(requirementSet, requirementSetData);
		requirementSetsService.update(updatedRequirementSet);
		eventRouter.sendEvent(new GroupUpdateSearchIndexEvent(Lists.newArrayList(userGroupId)));
		AjaxResponseBuilder responseBody = new AjaxResponseBuilder();
		responseBody.setSuccessful(true);
		responseBody.setData(CollectionUtilities.newObjectMap("requirements", requirementSetsSerializationService.toJson(updatedRequirementSet)));
		return responseBody;
	}

	// TODO - Micah - Need a real response here
	@RequestMapping(
		value = "/manage/register_uploaded_document/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder registerUploadedDocument(
		@RequestBody UploadDocumentDTO uploadDocumentDTO) throws HostServiceException {

		AjaxResponseBuilder responseBody = new AjaxResponseBuilder();

		userUserGroupDocumentReferenceService.saveAssetAndDocumentReference(uploadDocumentDTO);

		responseBody.setSuccessful(true);
		return responseBody;
	}

	@RequestMapping(
		value = "/manage/remove_reference/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder removeReference(
		@RequestBody UploadDocumentDTO uploadDocumentDTO) {

		AjaxResponseBuilder responseBody = new AjaxResponseBuilder();

		userUserGroupDocumentReferenceService.removeDocumentReference(
			uploadDocumentDTO.getUserId(), uploadDocumentDTO.getGroupId(), uploadDocumentDTO.getRequiredDocumentId(), uploadDocumentDTO.getReferenceDocumentId()
		);

		responseBody.setSuccessful(true);
		return responseBody;
	}
}
