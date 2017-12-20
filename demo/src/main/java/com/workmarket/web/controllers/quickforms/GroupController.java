package com.workmarket.web.controllers.quickforms;

import com.google.common.collect.Maps;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.quickforms.GroupQuickformForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quickforms")
public class GroupController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

	@Autowired private UserGroupService userGroupService;
	@Autowired private MessageBundleHelper messageHelper;


	// Quick form for creating private groups
	@RequestMapping(value = "/group", method = RequestMethod.GET)
	public String group(Model model) {
		model.addAttribute("create_quickform", new GroupQuickformForm());

		return "web/partials/quickforms/group";
	}


	// Quick form for creating private groups
	// TODO: refactor to use AjaxResponseBuilder
	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveGroup(
			@Valid @ModelAttribute("create_quickform") GroupQuickformForm form,
			BindingResult errors) {

		MessageBundle bundle = messageHelper.newBundle();
		Boolean successful = Boolean.FALSE;

		// companyUserGroupValidator.validate(groupDTO, errors);
		if (errors.hasErrors())
			messageHelper.setErrors(bundle, errors);

		if (StringUtils.isNotBlank(form.getName()) &&
				userGroupService.findCompanyUserGroupByName(getCurrentUser().getCompanyId(), form.getName()) != null)
			messageHelper.addError(bundle, "quickforms.groups.duplicate");

		Map<String, Object> output = Maps.newHashMap();

		// Validate input.
		if (!bundle.hasErrors()) {
			UserGroup group = null;
			try {
				// Save the submitted data.
				UserGroupDTO groupDTO = new UserGroupDTO();
				groupDTO.setName(form.getName());
				groupDTO.setDescription(form.getDescription());
				groupDTO.setOpenMembership(Boolean.FALSE); // Private.
				groupDTO.setRequiresApproval(Boolean.FALSE); // Join.
				groupDTO.setCompanyId(getCurrentUser().getCompanyId());
				groupDTO.setActiveFlag(Boolean.TRUE);

				//The owner of the group can't be null
				groupDTO.setOwnerId(getCurrentUser().getId());

				group = userGroupService.saveOrUpdateCompanyUserGroup(groupDTO);
				List<Long> resources = form.getResources();
				if (!CollectionUtils.isEmpty(resources)) {
					try {
						userGroupService.addUsersToGroup(resources, group.getId(), getCurrentUser().getId());
						output.put("id", group.getId());
						output.put("name", group.getName());
						successful = Boolean.TRUE;
						messageHelper.addSuccess(bundle, "quickforms.groups.create.success");
					} catch (Exception ex) {
						if (logger.isErrorEnabled()) {
							logger.error("error saving a list of group members: resources={} and group id={}",
									new Object[]{resources, group.getId()}, ex);
						}

						messageHelper.addError(bundle, "quickforms.groups.save.list.error");
					}
				} else {
					output.put("id", group.getId());
					output.put("name", group.getName());
					successful = Boolean.TRUE;
					messageHelper.addSuccess(bundle, "quickforms.groups.create.success");
				}
			} catch (Exception ex) {
				if (logger.isErrorEnabled()) {
					logger.error("Failed to save or update UserGroup: {}",
							new Object[]{ToStringBuilder.reflectionToString(group)}, ex);
				}

				messageHelper.addError(bundle, "quickforms.groups.create.error");
			}
		}

		output.put("successful", successful);

		if (bundle.hasErrors()) {
			output.put("errors", bundle.getErrors());
		}

		if (!bundle.getSuccess().isEmpty()) {
			output.put("message", bundle.getSuccess().iterator().next());
		}

		return output;
	}
}
