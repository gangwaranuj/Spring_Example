package com.workmarket.web.controllers.groups.manage;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.service.business.DocumentationPackagerService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.service.network.NetworkService;
import com.workmarket.web.forms.MessageForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.RedirectValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@PreAuthorize("hasAnyRole('ACL_ADMIN', 'ACL_MANAGER') AND !principal.companyIsLocked")
@RequestMapping("/groups")
public class GroupManageController extends BaseGroupsManageController {

	private static final Logger logger = LoggerFactory.getLogger(GroupManageController.class);

	@Autowired RedirectValidator redirectValidator;
	@Autowired DocumentationPackagerService documentationPackagerService;
	@Autowired NetworkService networkService;
	@Autowired UserService userService;
	@Autowired MessageBundleHelper messageBundleHelper;

	@RequestMapping(
		value = "/manage",
		method = GET)
	public String index() {
		return "redirect:/groups";
	}

	@RequestMapping(
		value = "/{id}/documentation",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public String getDocumentation(
		@PathVariable("id") Long groupId,
		RedirectAttributes redirectAttributes) {

		List<RequestContext> authz = getGroupService().getRequestContext(groupId);

		if (authz.contains(RequestContext.ADMIN)) {
			Optional<Asset> asset = documentationPackagerService.getDocumentationPackage(getCurrentUser().getId(), groupId);
			if (asset.isPresent()) {
				return "redirect:/asset/download/" + asset.get().getUUID();
			}
		}

		MessageBundle messages = getMessageHelper().newBundle();
		getMessageHelper().addError(messages, "groups.manage.documentation.error");
		redirectAttributes.addFlashAttribute("bundle", messages);

		return "redirect:/groups/" + groupId;
	}

	@RequestMapping(
		value = "/{id}/documentations",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getDocumentations(
		@PathVariable("id") Long groupId,
		@RequestParam("userNumbers[]") String[] selectedWorkers,
		RedirectAttributes redirectAttributes) {

		MessageBundle messages = getMessageHelper().newFlashBundle(redirectAttributes);
		List<RequestContext> authz = getGroupService().getRequestContext(groupId);

		try {
			if (authz.contains(RequestContext.ADMIN)) {
				documentationPackagerService.buildDocumentationPackage(getCurrentUser().getId(), groupId, new ArrayList<>(userService.findAllUserIdsByUserNumbers(Arrays.asList(selectedWorkers))));
				getMessageHelper().addSuccess(messages, "groups.manage.documentation-bulk.success");
				return new AjaxResponseBuilder()
					.setSuccessful(true)
					.setMessages(messages.getSuccess());
			} else {
				getMessageHelper().addError(messages, "groups.manage.documentation-bulk.no-privileges");
				return new AjaxResponseBuilder()
					.setSuccessful(false)
					.setMessages(messages.getErrors());
			}

		} catch (Exception e) {
			getMessageHelper().addError(messages, "groups.manage.documentation-bulk.error");
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(messages.getErrors());
		}
	}

	@RequestMapping(
		value = "/{id}/delete",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder delete(@PathVariable("id") Long groupId) throws Exception {

		if (!canDelete(groupId)) {
			return AjaxResponseBuilder.fail();
		}

		getGroupService().deleteGroup(groupId);
		MessageBundle messages = getMessageHelper().newBundle();
		getMessageHelper().addSuccess(messages, "groups.manage.deleted");
		return AjaxResponseBuilder.success().setMessages(messages.getAllMessages());
	}

	private boolean canDelete(long groupId) {
		List<RequestContext> authz = getGroupService().getRequestContext(groupId);
		return authz.contains(RequestContext.ADMIN);
	}

	@RequestMapping(
		value = "/reindex_members/{id}",
		method = GET)
	public String reindexMembers(
		@PathVariable("id") Long groupId,
		RedirectAttributes redirectAttributes) throws Exception {

		MessageBundle messages = getMessageHelper().newBundle();
		try {
			getGroupSearchService().reindexGroupMembers(groupId);
		} catch (Exception e) {
			logger.error(String.format("There was an error indexing group %d members ", groupId));
			getMessageHelper().addError(messages, "groups.manage.member_reindex.failure");
		}

		getMessageHelper().addSuccess(messages, "groups.manage.member_reindex.success");
		redirectAttributes.addFlashAttribute("bundle", messages);

		return "redirect:/groups/" + groupId;
	}

	@RequestMapping(
		value = "/{id}/toggle_active",
		method = GET)
	public String toggleActive(
		@PathVariable("id") Long groupId,
		@RequestParam(required = false) String redirectTo,
		RedirectAttributes redirectAttributes) {

		redirectTo = String.format("redirect:%s", redirectValidator.validateWithDefault(redirectTo, "/groups/{id}"));

		List<RequestContext> authz = getGroupService().getRequestContext(groupId);
		if (!(authz.contains(RequestContext.ADMIN) ||getCurrentUser().isInternal())) {
			return redirectTo;
		}

		UserGroup group = getGroupService().findGroupById(groupId);
		boolean flag = !group.getActiveFlag();
		getGroupService().updateGroupActiveFlag(groupId, flag);

		MessageBundle messages = getMessageHelper().newBundle();
		if (flag) {
			getMessageHelper().addSuccess(messages, "groups.manage.activated");
		} else {
			getMessageHelper().addSuccess(messages, "groups.manage.deactivated");
		}
		redirectAttributes.addFlashAttribute("bundle", messages);

		return redirectTo;
	}

	@RequestMapping(
		value = "/v2/{id}/activate",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder activate(
		@PathVariable("id") Long groupId) {

		return setActivation(true, groupId);
	}

	@RequestMapping(
		value = "/v2/{id}/deactivate",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deactivate(
		@PathVariable("id") Long groupId) {

		return setActivation(false, groupId);
	}

	private AjaxResponseBuilder setActivation(boolean activationFlag, Long groupId) {
		List<String> messages = Lists.newArrayList();
		try {
			List<RequestContext> requestContexts = getGroupService().getRequestContext(groupId);
			UserGroup group = getGroupService().findGroupById(groupId);
			if (getCurrentUser().isInternal() || requestContexts.contains(RequestContext.ADMIN) || group.getOwner().getId() == getCurrentUser().getId()) {
				getGroupService().updateGroupActiveFlag(groupId, activationFlag);
				messages.add(messageBundleHelper.getMessage("groups.manage.activate", group.getName(), activationFlag ? "activated" : "deactivated"));
				return new AjaxResponseBuilder()
					.setData(ImmutableMap.<String, Object>of("isActive", activationFlag))
					.setSuccessful(true)
					.setMessages(messages);
			} else {
				messages.add(messageBundleHelper.getMessage("groups.manage.activate.privilege", activationFlag ? "activate" : "deactivate", group.getName()));
				return new AjaxResponseBuilder()
					.setSuccessful(false)
					.setMessages(messages);
			}

		} catch (Exception e) {
			messages.add(messageBundleHelper.getMessage("groups.manage.activate.error"));
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(messages);
		}
	}

	@RequestMapping(
		value = "/{id}/messages",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder sendMessage(
		@PathVariable("id") Long groupId,
		@Valid @ModelAttribute("messageForm") MessageForm messageForm,
		BindingResult bindingResult) {

		MessageBundle messages = getMessageHelper().newBundle();

		if (bindingResult.hasErrors()) {
			getMessageHelper().setErrors(messages, bindingResult);
			return AjaxResponseBuilder.fail().setMessages(messages.getAllMessages());
		}

		saveMessageAndEmailGroupMembers(groupId, messageForm);
		getMessageHelper().addSuccess(messages, "groups.message.success");
		return AjaxResponseBuilder.success().setMessages(messages.getAllMessages());
	}

	private void saveMessageAndEmailGroupMembers(final Long groupId, final MessageForm messageForm) {
		EMailDTO dto = new EMailDTO();
		dto.setFromId(getCurrentUser().getId());
		dto.setSubject(messageForm.getTitle());
		dto.setText(messageForm.getMessage());
		getMessagingService().sendEmailToGroupMembers(getCurrentUser().getId(), groupId, dto);
	}

	@RequestMapping(
		value = "/sharing/{groupId}/share",
		method = POST)
	@ResponseStatus(value = OK)
	public void shareGroup(
		@PathVariable("groupId") final Long groupId) {

		networkService.addGroupToCompanyNetworks(groupId, getCurrentUser().getCompanyId());
	}

	@RequestMapping(
		value = "/sharing/{groupId}/revoke",
		method = POST)
	@ResponseStatus(value = OK)
	public void revokeGroupSharing(
		@PathVariable("groupId") final Long groupId) {

		networkService.removeGroupFromCompanyNetworks(groupId, getCurrentUser().getCompanyId());
	}
}
