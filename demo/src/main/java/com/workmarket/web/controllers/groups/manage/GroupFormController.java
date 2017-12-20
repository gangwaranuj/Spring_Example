package com.workmarket.web.controllers.groups.manage;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.forms.groups.manage.GroupAddEditForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.validators.GroupAddFormValidator;
import com.workmarket.web.validators.GroupEditFormValidator;
import com.workmarket.web.validators.UserGroupEditPermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@PreAuthorize("!principal.companyIsLocked")
public class GroupFormController extends BaseGroupsManageController {

	@Autowired private GroupAddFormValidator groupAddFormValidator;
	@Autowired private GroupEditFormValidator groupEditFormValidator;
	@Autowired private UserGroupEditPermissionValidator userGroupEditPermissionValidator;

	@PreAuthorize("hasAnyRole('ACL_ADMIN', 'ACL_MANAGER') AND !principal.companyIsLocked")
	@RequestMapping(
			value = "/groups/v2/create",
			method = POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder doV2Add(
			@Valid @ModelAttribute("groupForm") GroupAddEditForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) throws Exception {

		if (form.getOpenMembership() && form.getIndustryId() == 0) {
			bindingResult.rejectValue(
				"industryId", "NotEmpty", getMessageHelper().getMessage("groups.industry.missing"));
		}

		groupAddFormValidator.validate(form, bindingResult);
		if (bindingResult.hasErrors()) {
			String errorString = bindingResult.getFieldError().getDefaultMessage();
			Map<String, Object> returnJson = CollectionUtilities.newObjectMap(
					"error", errorString
			);

			return AjaxResponseBuilder.fail().addData("result", returnJson);
		}

		UserGroupDTO dto = getUserGroupDtoFromForm(form);

		// TODO validation exceptions here cause it to barf, fix
		UserGroup group = getGroupService().saveOrUpdateCompanyUserGroup(dto);
		redirectAttributes.addAttribute("id", group.getId());

		Map<String, Object> returnJson = CollectionUtilities.newObjectMap(
			"success", "true",
			"id", group.getId()
		);

		return AjaxResponseBuilder.success().addData("result", returnJson);
	}

	@RequestMapping(
			value = "/groups/v2/update/{groupId}",
			method = POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder doV2Edit(
			@PathVariable("groupId") Long groupId,
			@Valid @ModelAttribute("groupForm") GroupAddEditForm form,
			BindingResult bindingResult) throws Exception {

		UserGroup existingGroup = getGroupService().findGroupById(groupId);
		if (form.getOpenMembership() && form.getIndustryId() == 0) {
			bindingResult.rejectValue(
				"industryId", "NotEmpty", getMessageHelper().getMessage("groups.industry.missing"));
		}
		userGroupEditPermissionValidator.validate(existingGroup, bindingResult);
		if (bindingResult.hasErrors()) {
			String errorString = bindingResult.getFieldError().getDefaultMessage();
			Map<String, Object> returnJson = CollectionUtilities.newObjectMap(
					"error", errorString
			);

			return AjaxResponseBuilder.fail().addData("result", returnJson);
		}

		form.setId(groupId);

		groupEditFormValidator.validate(form, bindingResult);
		if (bindingResult.hasErrors()) {
			String errorString = bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage();
			Map<String, Object> returnJson = CollectionUtilities.newObjectMap(
					"error", errorString
			);

			return AjaxResponseBuilder.fail().addData("result", returnJson);
		}
		UserGroupDTO dto = getUserGroupDtoFromFormAndExistingUserGroup(form, existingGroup);

		// TODO validation exceptions here cause it to barf, fix
		UserGroup group = getGroupService().saveOrUpdateCompanyUserGroup(dto);

		Map<String, Object> returnJson = CollectionUtilities.newObjectMap(
				"success", "true",
				"id", group.getId()
		);

		return AjaxResponseBuilder.success().addData("result", returnJson);
	}

	private UserGroupDTO getUserGroupDtoFromForm(GroupAddEditForm form) {
		UserGroupDTO dto = new UserGroupDTO();
		// Public groups should default to inactive
		dto.setActiveFlag(!form.getOpenMembership());
		dto.setOpenMembership(form.getOpenMembership());
		copyFormToDto(form, dto);
		return dto;
	}

	private UserGroupDTO getUserGroupDtoFromFormAndExistingUserGroup(GroupAddEditForm form, UserGroup group) {
		UserGroupDTO dto = new UserGroupDTO();
		dto.setUserGroupId(group.getId());
		dto.setActiveFlag(group.getActiveFlag());
		dto.setOpenMembership(group.getOpenMembership());
		copyFormToDto(form, dto);
		return dto;
	}

	private void copyFormToDto(GroupAddEditForm form, UserGroupDTO dto) {
		dto.setName(form.getName());
		dto.setOwnerId(form.getGroupOwner());
		dto.setCompanyId(getCurrentUser().getCompanyId());
		dto.setDescription(form.getDescription());
		dto.setIndustryId(form.getIndustryId());
		dto.setSkillIds(form.getSkillIds());
		dto.setOrgUnitUuids(form.getOrgUnitUuids());

		if (dto.getOpenMembership()) {
			copyFormToDtoForPublicGroup(form, dto);
		} else {
			copyFormToDtoForPrivateGroup(dto);
		}
	}

	private void copyFormToDtoForPublicGroup(GroupAddEditForm form, UserGroupDTO dto) {
		dto.setRequiresApproval(form.getRequiresApproval());
		dto.setSearchable(form.getSearchable());
		dto.setPublic(form.getPubliclyAvailable());
	}

	private void copyFormToDtoForPrivateGroup(UserGroupDTO dto) {
		dto.setRequiresApproval(false);
		dto.setSearchable(false);
		dto.setPublic(false);
	}
}
