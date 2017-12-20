package com.workmarket.web.controllers.admin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.dto.UserSuggestionDTO;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.admin.AddEditEmployeeForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/usermanagement")
public class UsermanagementController extends BaseController {

	@Autowired AuthenticationService authService;
	@Autowired ProfileService profileService;
	@Autowired RegistrationService registrationService;
	@Autowired SuggestionService suggestionService;
	@Autowired UserService userService;
	@Autowired WorkService workService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired WorkSearchService workSearchService;
	@Autowired EventRouter eventRouter;


	@PreAuthorize("hasAnyRole('ACL_ADMIN','ROLE_WM_ADMIN','ROLE_WM_EMPLOYEE_MGMT')")
	@RequestMapping(value = {"", "/", "/index"}, method = RequestMethod.GET)
	public String index(Model model) throws Exception {

		model.addAttribute("user_roles", getAllInternalUserRolesMap());
		model.addAttribute("useAdmin", true);

		// TODO: load CRITS here
		model.addAttribute("user_info", getAllInternalUsers());
		model.addAttribute("contractors",authService.getInternalContractors());

		return "web/pages/admin/usermanagement/index";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String showNew(Model model) throws Exception {

		model.addAttribute("roles", getAllUserRolesMap());
		model.addAttribute("internal_roles", getAllInternalUserRolesMap());

		// set Errors
		if (model.containsAttribute("errors"))
			model.addAttribute("errors", model.asMap().get("errors"));

		model.addAttribute("form", new AddEditEmployeeForm());

		return "web/pages/admin/usermanagement/new";
	}


	@RequestMapping(value = {"/add"}, method = RequestMethod.GET)
	public String showAdd() {
		return "redirect:/admin/usermanagement/new";
	}


	@RequestMapping(value = {"/new"}, method = RequestMethod.POST)
	public String submitAdd(
		@Valid @ModelAttribute("addEmployeeForm") AddEditEmployeeForm form,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) throws Exception {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasFieldErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/admin/usermanagement/new";
		}
		if (!StringUtilities.isNotEmpty(form.getPassword())) { // doing this here so we can reuse the form
			bundle.addError(messageHelper.getMessage("NotEmpty", "Password"));
			return "redirect:/admin/usermanagement/new";
		}

		User user = registrationService.registerNewInternalUser(form.getDTO());

		if (user == null) {
			messageHelper.addError(bundle, "admin.usermanagement.new.usernull");
			return "redirect:/admin/usermanagement/new";
		}

		// TODO: add to CRITS

		model.addAttribute("id", user.getId());

		return "web/pages/admin/usermanagement/adddone";
	}


	@RequestMapping(value = "/adddone", method = RequestMethod.GET)
	public String adddone() {

		return "web/pages/admin/usermanagement/adddone";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String showEditEmployee(
		@RequestParam(value = "id", required = false) Long userId,
		RedirectAttributes flash,
		Model model) throws Exception {

		if (userId == null)
			return "redirect:/admin/usermanagement/index";

		User user = userService.findUserById(userId);
		if (user == null) {
			messageHelper.newFlashBundle(flash)
				.addError(messageHelper.getMessage("admin.usermanagement.edit.usernull"));
			return "redirect:/admin/usermanagement/index";
		}
		model.addAttribute("editform", populateEditUserForm(user));
		// TODO: errors

		model.addAttribute("roles", getAllInternalAclRolesMap());
		model.addAttribute("internal_roles", authService.getInternalRoles());
		model.addAttribute("user_roles", roleListToMap(authService.findAllAssignedAclRolesByUser(user.getId())));

		return "web/pages/admin/usermanagement/edit";
	}


	@RequestMapping(value = "/editsave", method = RequestMethod.GET)
	public String showEditsave() {
		return "redirect:/admin/usermanagement/edit";
	}



	@RequestMapping( value = "/delete", method = RequestMethod.POST)
	public String submitDelete(
		@RequestParam(value = "id", required = false) Long userId,
		RedirectAttributes flash,
		Model model) throws Exception
	{
		Assert.notNull(userId);
		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		try {
			userService.deleteUser(userId);
			messageHelper.addSuccess(bundle, "admin.usermanagement.editsave.deleteuser");
			return "redirect:/admin/usermanagement/index";
		} catch (Exception e) {
			messageHelper.addSuccess(bundle, "admin.usermanagement.editsave.deleteuser.exception");
			return "redirect:/admin/usermanagement/edit?id=" + userId;
		}
	}


	@RequestMapping(value = "/editsave", method = RequestMethod.POST)
	public String submitEditsave(
		@Valid @ModelAttribute("editform") AddEditEmployeeForm form,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidAclRoleException {

		Long userId = form.getId();
		User user = userService.findUserById(userId);
		if (user == null) return "redirect:/admin/usermanagement/index";

		Long id = user.getId();
		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (StringUtilities.isNotEmpty(form.getAction()) && form.getAction().equals("delete")) {

			// TODO: add crits

			try {
				userService.deleteUser(id);
				messageHelper.addSuccess(bundle, "admin.usermanagement.editsave.deleteuser");
				return "redirect:/admin/usermanagement/index";
			} catch (Exception e) {
				messageHelper.addSuccess(bundle, "admin.usermanagement.editsave.deleteuser.exception");
				return "redirect:/admin/usermanagement/edit?id=" + userId;
			}
		}

		if (bind.hasFieldErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/admin/usermanagement/edit?id=" + userId;
		}

		userService.updateUserProperties(id, CollectionUtilities.newStringMap(
			"firstName", form.getFirst_name(),
			"lastName", form.getLast_name(),
			"email", form.getEmail()));

		if (StringUtilities.all(form.getPassword(), form.getPassword_confirm())) {
			try {
				authService.changePassword(form.getPassword(), form.getPassword_confirm());
			} catch (Exception e) {
				// ignore
			}
		}

		Collection<String> submittedRoles = form.getRoles();

		if (submittedRoles != null) {
			List<AclRole> assignedRoles = authService.findAllAssignedAclRolesByUser(id);

			// if the role not found in the assignedRoles, add it
			List<Long> rolesAdd = Lists.newArrayList();
			for (final String newRole : submittedRoles)
				if (null == CollectionUtils.find(assignedRoles, new Predicate() {
					@Override public boolean evaluate(Object o) {
						return ((AclRole) o).getId().toString().equals(newRole);
					}
				}))
					rolesAdd.add(Long.valueOf(newRole));

			authService.assignAclRolesToUser(id, rolesAdd.toArray(new Long[rolesAdd.size()]));

			// if the existing role is found in the submittedRoles, remove it
			for (final AclRole role : assignedRoles)
				if (null != CollectionUtils.find(submittedRoles, new Predicate() {
					@Override public boolean evaluate(Object o) {
						return o.equals(role.getId().toString());
					}
				}))
					authService.removeAclRoleFromUser(id, role.getId());

		}

		Collection<String> submittedInternalRoles = form.getInternal_roles();
		if (submittedInternalRoles != null) {
			List<String> assignedRoles = Arrays.asList(authService.getRoles(id));
			Collection<String> rolesAdd;
			Collection<String> rolesRemove;

			rolesAdd = CollectionUtils.subtract(CollectionUtils.union(assignedRoles, submittedInternalRoles), assignedRoles);
			rolesRemove = CollectionUtils.subtract(assignedRoles, submittedInternalRoles);

			authService.addRoles(id, rolesAdd.toArray(new String[rolesAdd.size()]));
			authService.removeRoles(id, rolesRemove.toArray(new String[rolesRemove.size()]));
		} else { //delete all roles
			authService.removeRoles(id,authService.getRoles(id));

		}

		// TODO: do CRITS related access settings

		model.addAttribute("id", id);

		return "redirect:/admin/usermanagement/index";
	}


	@RequestMapping(value = "/editdone", method = RequestMethod.GET)
	public String editdone() {
		return "web/pages/admin/usermanagement/editdone";
	}


	@RequestMapping(value = "/masquerade", method = RequestMethod.GET)
	public String masquerade() throws Exception {
		return "web/pages/admin/usermanagement/masquerade";
	}

	@RequestMapping(value ="/reindex_work/{workNumber}", method = RequestMethod.GET)
	public String reindexWork(@PathVariable("workNumber") String workNumber) throws Exception {
		Long workId = workService.findWorkId(workNumber);
		workSearchService.reindexWorkAsynchronous(workId);
		return "redirect:/assignments/details/" + workNumber;
	}

	@RequestMapping(value ="/reindex_user/{userNumber}", method = RequestMethod.GET)
	public String reindexUser(@PathVariable("userNumber") String userNumber) throws Exception {
		Long userId = userService.findUserId(userNumber);
		eventRouter.sendEvent(new UserSearchIndexEvent(userId));
		return "redirect:/profile/" + userNumber;
	}

	@RequestMapping(value = "/suggest_users", method = RequestMethod.GET)
	public void suggestUsers(@RequestParam(value = "term") String term, Model model) {
		List<Map<String, String>> response = Lists.newArrayList();
		for (UserSuggestionDTO dto : suggestionService.suggestUser(term)) {
			response.add(ImmutableMap.of(
				"id", dto.getEmail(),
				"value", String.format("%s (%s)", dto.getValue(), dto.getCompanyName())));
		}
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/suggest_contractors", method = RequestMethod.GET)
	public void suggestContractors(@RequestParam(value = "term") String term, Model model) {
		List<Map<String, String>> response = Lists.newArrayList();
		for (UserSuggestionDTO dto : suggestionService.suggestUser(term)) {
			response.add(ImmutableMap.of(
				"id", Long.toString(dto.getId()),
				"value", String.format("%s (%s)", dto.getValue(), dto.getCompanyName())));
		}
		model.addAttribute("response", response);
	}

	/**
	 * ***************************************************************************************
	 */

	private Set<User> getAllInternalUsers() throws Exception {
		return authService.getActiveInternalUsers();
	}


	private Map<Long, String> getAllUserRolesMap() {
		List<AclRole> roles = authService.findAllAclRoles();
		return roleListToMap(roles);
	}


	private Map<Long, String> getAllAclRolesForUserMap(Long id) {
		List<AclRole> roles = authService.findAllAssignedAclRolesByUser(id);
		return roleListToMap(roles);
	}


	private Map<Long, String> getAllInternalAclRolesMap() {
		List<AclRole> roles = authService.findAllInternalAclRoles();
		return roleListToMap(roles);
	}


	private Map<Long, String> getAllInternalUserRolesMap() throws Exception {
		List<AclRole> roles = authService.findAllInternalAclRoles();
		return roleListToMap(roles);
	}


	private Map<Long, String> roleListToMap(List<AclRole> roles) {
		HashMap<Long, String> result = Maps.newHashMap();
		if (roles != null)
			for (AclRole r : roles)
				result.put(r.getId(), r.getName());

		return result;
	}


	private AddEditEmployeeForm populateEditUserForm(User user) {
		Long userId = user.getId();
		AddEditEmployeeForm form = new AddEditEmployeeForm();
		form.setId(userId);
		form.setFirst_name(user.getFirstName());
		form.setLast_name(user.getLastName());
		form.setEmail(user.getEmail());
		if (CollectionUtilities.isEmpty(form.getRoles()))
			form.setRoles(getAllAclRolesForUserMap(userId).values());
		if (CollectionUtilities.isEmpty(form.getInternal_roles()))
			form.setInternal_roles(Arrays.asList(authService.getRoles(userId)));

		return form;
	}

}
