package com.workmarket.web.controllers.users;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.Ostermiller.util.RandPass;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.RoleType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.company.CustomerType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.UserProfileCompletenessDTO;
import com.workmarket.service.business.event.BulkUserUploadStarterEvent;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.business.upload.users.model.BulkUserUploadRequest;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.editors.CallingCodeEditor;
import com.workmarket.web.editors.IndustryEditor;
import com.workmarket.web.forms.user.ReassignUserForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.UserFormControllerValidator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller("userFormController")
@RequestMapping("/users")
public class UserFormController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(UserFormController.class);

	@Autowired private UserService userService;
	@Autowired private ProfileService profileService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private RegistrationService registrationService;
	@Autowired private IndustryService industryService;
	@Autowired private FormOptionsDataHelper formDataHelper;
	@Autowired private CallingCodeEditor callingCodeEditor;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private CompanyService companyService;
	@Autowired @Qualifier("industryEditor") private IndustryEditor industryEditor;
	@Autowired @Qualifier("userFormControllerValidator") private UserFormControllerValidator userFormControllerValidator;
	@Autowired private TaxService taxService;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private UserRoleService userRoleService;
	@Autowired private FeatureEntitlementService featureEntitlementService;

	private static final String VIEW_FORM = "web/pages/users/form";

	@InitBinder("user")
	public void initProjectBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Industry.class, industryEditor);
	}

	@ModelAttribute("industries")
	public List<IndustryDTO> populateIndustries() {
		try {
			return industryService.getAllIndustryDTOs();
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@ModelAttribute("roles")
	public List<RoleType> populateRoles() {
		// hack - we we will swap RoleType values with AclRole to ensure mapping from UI.
		// Only doing this to ensure this can be retrofitted into further used persistence services.

		List<AclRole> aclRoles = authenticationService.findAllAvailableAclRolesByCompany(getCurrentUser().getCompanyId());
		List<RoleType> roleTypes = new ArrayList<>(aclRoles.size());

		for (AclRole role : aclRoles) {
			RoleType type = new RoleType(String.valueOf(role.getId()));
			type.setDescription(role.getDescription());
			roleTypes.add(type);
		}
		return roleTypes;
	}

	@InitBinder("user")
	public void initcallingCodeBinder(WebDataBinder binder) {
		binder.registerCustomEditor(CallingCode.class, callingCodeEditor);
	}

	@RequestMapping(
		value = "/add_user",
		method = GET)
	public String add(Model model) {
		ExtendedUserDetails userDetails = getCurrentUser();
		User user = new User();
		user.setProfile(new Profile());

		userRoleService.setRoles(user, ImmutableSet.of(new RoleType(String.valueOf(AclRole.ACL_STAFF))));

		model.addAttribute("user", user);
		model.addAttribute("isDeputy", userRoleService.hasAclRole(user, AclRole.ACL_DEPUTY));
		model.addAttribute("companyName", userDetails.getCompanyName());
		//model.addAttribute("isEmployeeWorker", isUserEmployeeWorker());

		List<CallingCode> callingCodes = invariantDataService.findAllActiveCallingCodes();
		model.addAttribute("callingCodesList", callingCodes);

		return VIEW_FORM;
	}

	@RequestMapping(
		value = "/add_user",
		method = POST)
	public String add(
		@RequestParam(value = "isAdmin", required = true, defaultValue = "false") boolean isAdmin,
		@RequestParam(value = "isManager", required = true, defaultValue = "false") boolean isManager,
		@RequestParam(value = "isController", required = true, defaultValue = "false") boolean isController,
		@RequestParam(value = "isUser", required = true, defaultValue = "false") boolean isUser,
		@RequestParam(value = "isViewOnly", required = true, defaultValue = "false") boolean isViewOnly,
		@RequestParam(value = "isStaff", required = true, defaultValue = "false") boolean isStaff,
		@RequestParam(value = "isDeputy", required = true, defaultValue = "false") boolean isDeputy,
		@RequestParam(value = "isDispatcher", required = true, defaultValue = "false") boolean isDispatcher,
		@RequestParam(value = "isEmployeeWorker", required = false, defaultValue = "false") boolean isEmployeeWorker,
		@RequestParam(value = "workStatus", required = true, defaultValue = "UNAVAILABLE") User.WorkStatus workStatus,
		@RequestParam(value = "hasPaymentAccess", required = false, defaultValue = "false") Boolean hasPaymentAccess,
		@RequestParam(value = "hasFundsAccess", required = false, defaultValue = "false") Boolean hasFundsAccess,
		@RequestParam(value = "hasCounterOfferAccess", required = false, defaultValue = "false") Boolean hasCounterOfferAccess,
		@RequestParam(value = "hasEditPricingAccess", required = false, defaultValue = "false") Boolean hasEditPricingAccess,
		@RequestParam(value = "hasWorkApproveAccess", required = false, defaultValue = "false") Boolean hasWorkApproveAccess,
		@RequestParam(value = "hasProjectAccess", required = false, defaultValue = "false") Boolean hasProjectAccess,
		@RequestParam(value = "industry", required = true, defaultValue = "0") Long industryId,
		@ModelAttribute("user") User user,
		BindingResult result,
		Model model,
		RedirectAttributes redirectAttributes) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

		AbstractTaxEntity taxEntity = taxService.findActiveTaxEntity(getCurrentUser().getId());
		String customerType = companyService.getCustomerType(getCurrentUser().getCompanyId());
		MessageBundle bundle = messageHelper.newBundle(model);

		if (CustomerType.RESOURCE.value().equals(customerType)) {
			if (taxEntity == null || !taxEntity.getBusinessFlag()) {
				messageHelper.addError(bundle, "user.validation.businessTaxInfoRequired");
				messageHelper.setErrors(bundle, result);

				return VIEW_FORM;
			}
		}

		ExtendedUserDetails userDetails = getCurrentUser();

		userFormControllerValidator.validate(user, result);

		User existingUser = userService.findUserByEmail(user.getEmail());
		boolean isNewUser = StringUtils.isBlank(user.getUserNumber());
		Industry industry = industryService.getIndustryById(industryId);

		if (industry == null && existingUser == null && isNewUser) {

			messageHelper.addError(bundle, "user.validation.industryRequired");
			messageHelper.setErrors(bundle, result);

			return VIEW_FORM;
		}

		if (!isEmployeeWorker) {
			if (!isAdmin && !isManager && !isController && !isUser && !isViewOnly && !isStaff && !isDeputy && !isDispatcher) {
				messageHelper.addError(bundle, "user.validation.notWorkerRolesRequired");
				messageHelper.setErrors(bundle, result);

				return VIEW_FORM;
			}
		} else {
			if (isAdmin || isManager || isController || isUser || isViewOnly || isStaff || isDeputy || isDispatcher) {
				messageHelper.addError(bundle, "user.validation.employeeWorkerNoOtherRolesAllowed");
				messageHelper.setErrors(bundle, result);

				return VIEW_FORM;
			}

			if (hasPaymentAccess || hasFundsAccess || hasCounterOfferAccess || hasEditPricingAccess || hasWorkApproveAccess ||
				hasProjectAccess) {
				messageHelper.addError(bundle, "user.validation.employeeWorkerNoCustomPermissionsAllowed");
				messageHelper.setErrors(bundle, result);
				return VIEW_FORM;
			}
		}

		if (result.hasErrors()) {
			messageHelper.setErrors(bundle, result);

			model.addAttribute("isAdmin", userRoleService.hasAclRole(user, AclRole.ACL_ADMIN));
			model.addAttribute("isManager", userRoleService.hasAclRole(user, AclRole.ACL_MANAGER));
			model.addAttribute("isController", userRoleService.hasAclRole(user, AclRole.ACL_CONTROLLER));
			model.addAttribute("isUser", userRoleService.hasAclRole(user, AclRole.ACL_USER));
			model.addAttribute("isViewOnly", userRoleService.hasAclRole(user, AclRole.ACL_VIEW_ONLY));
			model.addAttribute("isStaff", userRoleService.hasAclRole(user, AclRole.ACL_STAFF));
			model.addAttribute("isDeputy", userRoleService.hasAclRole(user, AclRole.ACL_DEPUTY));
			model.addAttribute("isDispatcher", userRoleService.hasAclRole(user, AclRole.ACL_DISPATCHER));
			model.addAttribute("isEmployeeWorker", userRoleService.hasAclRole(user, AclRole.ACL_EMPLOYEE_WORKER));
			model.addAttribute("companyName", userDetails.getCompanyName());
			model.addAttribute("workStatus", userRoleService.getWorkStatus(user));

			List<CallingCode> callingCodes = invariantDataService.findAllActiveCallingCodes();
			model.addAttribute("callingCodesList", callingCodes);

			return VIEW_FORM;
		}

		bundle = messageHelper.newFlashBundle(redirectAttributes);

		RandPass randomPasswords = new RandPass(new char[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});

		UserDTO dto = new UserDTO();
		dto.setEmail(user.getEmail());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setSpendLimit(user.getSpendLimit());
		dto.setPassword(randomPasswords.getPass(4));

		Set<RoleType> roles = userRoleService.getUserRoles(user);
		List<Long> submittedRoles = getRoleIds(roles);

		if (isAdmin) {
			submittedRoles.add(AclRole.ACL_ADMIN);
		}

		if (isManager) {
			submittedRoles.add(AclRole.ACL_MANAGER);
		}

		if (isController) {
			submittedRoles.add(AclRole.ACL_CONTROLLER);
		}

		if (isUser) {
			submittedRoles.add(AclRole.ACL_USER);
		}

		if (isViewOnly) {
			submittedRoles.add(AclRole.ACL_VIEW_ONLY);
		}

		if (isStaff) {
			submittedRoles.add(AclRole.ACL_STAFF);
		}

		if (isDeputy) {
			submittedRoles.add(AclRole.ACL_DEPUTY);
		}

		if (workStatus.equals(User.WorkStatus.UNLISTED) || workStatus.equals(User.WorkStatus.PUBLIC)) {
			submittedRoles.add(AclRole.ACL_WORKER);
		}

		if (workStatus.equals(User.WorkStatus.PUBLIC)) {
			submittedRoles.add(AclRole.ACL_SHARED_WORKER);
		}

		if (isDispatcher){
			submittedRoles.add(AclRole.ACL_DISPATCHER);
		}

		if (isEmployeeWorker) {
			if (!submittedRoles.contains(AclRole.ACL_WORKER)) {
				submittedRoles.add(AclRole.ACL_WORKER);
			}
			submittedRoles.add(AclRole.ACL_EMPLOYEE_WORKER);
		}

		User savedUser;

		try {
			savedUser = registrationService.registerNewForCompany(dto, userDetails.getCompanyId(), submittedRoles.toArray(new Long[roles.size()]));
			Profile profile = user.getProfile();
			Map<String, String> profileMap = CollectionUtilities.newStringMap(
				"workPhone", profile.getWorkPhone(),
				"workPhoneInternationalCode", profile.isWorkPhoneInternationalCodeSet()?user.getProfile().getWorkPhoneInternationalCode().getId().toString():"",
				"workPhoneExtension", profile.getWorkPhoneExtension(),
				"jobTitle", profile.getJobTitle(),
				"industry.id", String.valueOf(industry.getId())
			);

			authenticationService.setCustomAccess(hasPaymentAccess, hasFundsAccess, hasCounterOfferAccess, hasEditPricingAccess,
					hasWorkApproveAccess, hasProjectAccess, savedUser.getId());

			profileService.updateProfileProperties(savedUser.getId(), profileMap);
			messageHelper.addSuccess(bundle, "users.added");

			return "redirect:/users";
		} catch (Exception e) {
			messageHelper.addError(bundle, "users.add.exception");
			return "redirect:/users/add_user";
		}
	}

	@RequestMapping(
		value = "/import/{uuid}",
		method = POST,
		produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder uploadUsers(@PathVariable("uuid") String uuid) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		MessageBundle messages = messageHelper.newBundle();

		if (StringUtils.isBlank(uuid)){
			messageHelper.addError(messages, "users.upload.fail");
			return response;
		}

		final boolean orgEnabledForUser = featureEntitlementService.hasFeatureToggle(getCurrentUser().getId(), "org_structures");
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, getCurrentUser().getId());
		BulkUserUploadStarterEvent event = eventFactory.buildBulkUserUploadStartEvent(request, new BulkUserUploadResponse(), orgEnabledForUser);
		eventRouter.sendEvent(event);
		return new AjaxResponseBuilder().setSuccessful(true);
	}

	@RequestMapping(
		value = "/edit_user/{userNumber}",
		method = GET)
	public String edit(@PathVariable("userNumber") String userNumber, Model model) {
		ExtendedUserDetails userDetails = getCurrentUser();
		User user = userService.findUserByUserNumber(userNumber);

		if (!canEditUser(user)) {
			MessageBundle bundle = messageHelper.newBundle(model);
			messageHelper.addError(bundle, "users.edit_user.authorize");
			return "redirect:/users";
		}

		// hack - we we will swap RoleType values with AclRole to ensure mapping from UI.
		// Only doing this to ensure this can be retrofitted into further used persistence services.
		List<AclRole> roles = authenticationService.findAllAssignedAclRolesByUser(user.getId());
		ImmutableSet.Builder<RoleType> rolesToSet = ImmutableSet.builder();

		for (AclRole aclRole : roles) {
			RoleType type = new RoleType(String.valueOf(aclRole.getId()));
			type.setDescription(aclRole.getDescription());
			rolesToSet.add(type);
		}
		userRoleService.setRoles(user, rolesToSet.build());

		Boolean hasPaymentCenterAndEmailsAccess = authenticationService.hasPaymentCenterAndEmailsAccess(user.getId(), Boolean.TRUE);
		Boolean hasManageBankAndFundsAccess = hasPaymentCenterAndEmailsAccess? authenticationService.hasManageBankAndFundsAccess(user.getId(), Boolean.TRUE) : Boolean.FALSE;

		model.addAttribute("user", user);
		model.addAttribute("toggleStatusTo", UserStatusType.DEACTIVATED.equals(authenticationService.getUserStatus(user).getCode()) ? "approved" : "deactivate");
		model.addAttribute("company", userDetails.getCompanyName());
		model.addAttribute("PROFILE_COMPLETENESS_THRESHOLD", Constants.PROFILE_COMPLETENESS_THRESHOLD);
		model.addAttribute("isAdmin", userRoleService.hasAclRole(user, AclRole.ACL_ADMIN));
		model.addAttribute("isManager", userRoleService.hasAclRole(user, AclRole.ACL_MANAGER));
		model.addAttribute("isController", userRoleService.hasAclRole(user, AclRole.ACL_CONTROLLER));
		model.addAttribute("isUser", userRoleService.hasAclRole(user, AclRole.ACL_USER));
		model.addAttribute("isViewOnly", userRoleService.hasAclRole(user, AclRole.ACL_VIEW_ONLY));
		model.addAttribute("isStaff", userRoleService.hasAclRole(user, AclRole.ACL_STAFF));
		model.addAttribute("isDeputy", userRoleService.hasAclRole(user, AclRole.ACL_DEPUTY));
		model.addAttribute("isDispatcher", userRoleService.hasAclRole(user, AclRole.ACL_DISPATCHER));
		model.addAttribute("isEmployeeWorker", userRoleService.hasAclRole(user, AclRole.ACL_EMPLOYEE_WORKER));
		model.addAttribute("isLastDispatcher", isLastDispatcher(user));
		model.addAttribute("workStatus", userRoleService.getWorkStatus(user));
		model.addAttribute("hasPaymentCenterAndEmailAccess", hasPaymentCenterAndEmailsAccess);
		model.addAttribute("hasManageBankAndFundsAccess", hasManageBankAndFundsAccess);
		model.addAttribute("hasCounterOfferAccess", userRoleService.hasPermissionsForCustomAuth(user.getId(), Permission.COUNTEROFFER_AUTH));
		model.addAttribute("hasEditPricingAccess", userRoleService.hasPermissionsForCustomAuth(user.getId(), Permission.EDIT_PRICING_AUTH));
		model.addAttribute("hasWorkApproveAccess", userRoleService.hasPermissionsForCustomAuth(user.getId(), Permission.APPROVE_WORK_AUTH));
		model.addAttribute("hasProjectAccess", authenticationService.hasProjectAccess(user.getId()));
		model.addAttribute("companyName", userDetails.getCompanyName());
		model.addAttribute("workPhoneInternationalCode", user.getProfile().isWorkPhoneInternationalCodeSet() ? user.getProfile().getWorkPhoneInternationalCode().getCallingCodeId() : "");
		model.addAttribute("callingCodesList", invariantDataService.findAllActiveCallingCodes());

		try {
			UserProfileCompletenessDTO completeness = profileService.getUserProfileCompleteness(user.getId());
			model.addAttribute("completeness", completeness);
		} catch (Exception e) {
			logger.error("unable to open edit form for user number" + userNumber, e);
			MessageBundle bundle = messageHelper.newBundle(model);
			messageHelper.addError(bundle, "users.edit.exception");
		}

		return VIEW_FORM;
	}

	@RequestMapping(
		value = "/edit_user/{userNumber}",
		method = POST)
	public String edit(
		@PathVariable("userNumber") String userNumber,
		@RequestParam(value = "isAdmin", required = true, defaultValue = "false") boolean isAdmin,
		@RequestParam(value = "isManager", required = true, defaultValue = "false") boolean isManager,
		@RequestParam(value = "isController", required = true, defaultValue = "false") boolean isController,
		@RequestParam(value = "isUser", required = true, defaultValue = "false") boolean isUser,
		@RequestParam(value = "isViewOnly", required = true, defaultValue = "false") boolean isViewOnly,
		@RequestParam(value = "isStaff", required = true, defaultValue = "false") boolean isStaff,
		@RequestParam(value = "isDeputy", required = true, defaultValue = "false") boolean isDeputy,
		@RequestParam(value = "isDispatcher", required = true, defaultValue = "false") boolean isDispatcher,
		@RequestParam(value = "isEmployeeWorker", required = false, defaultValue = "false") boolean isEmployeeWorker,
		@RequestParam(value = "workStatus", required = true, defaultValue = "UNAVAILABLE") User.WorkStatus workStatus,
		@RequestParam(value = "hasPaymentAccess", required = false, defaultValue = "false") Boolean hasPaymentAccess,
		@RequestParam(value = "hasFundsAccess", required = false, defaultValue = "false") Boolean hasFundsAccess,
		@RequestParam(value = "hasCounterOfferAccess", required = false, defaultValue = "false") Boolean hasCounterOfferAccess,
		@RequestParam(value = "hasEditPricingAccess", required = false, defaultValue = "false") Boolean hasEditPricingAccess,
		@RequestParam(value = "hasWorkApproveAccess", required = false, defaultValue = "false") Boolean hasWorkApproveAccess,
		@RequestParam(value = "hasProjectAccess", required = false, defaultValue = "false") Boolean hasProjectAccess,
		@ModelAttribute("user") User user,
		BindingResult result,
		Model model,
		RedirectAttributes redirectAttributes) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidAclRoleException {

		ExtendedUserDetails userDetails = getCurrentUser();

		if (user == null) {
			return "redirect:/users";
		}

		User submittedUser = userService.findUserByUserNumber(user.getUserNumber());
		// Check that the user belongs to the same company
		if (user.getUserNumber() != null) {
			if (submittedUser != null && !getCurrentUser().getCompanyId().equals(submittedUser.getCompany().getId()))
				return "redirect:/users";
		}

		MessageBundle bundle = messageHelper.newBundle();

		if (!isEmployeeWorker) {
			if (!isAdmin && !isManager && !isController && !isUser && !isViewOnly && !isStaff && !isDeputy && !isDispatcher) {
				messageHelper.addError(bundle, "user.validation.notWorkerRolesRequired");
			}
		} else {
			if (isAdmin || isManager || isController || isUser || isViewOnly || isStaff || isDeputy || isDispatcher) {
				messageHelper.addError(bundle, "user.validation.employeeWorkerNoOtherRolesAllowed");
				messageHelper.setErrors(bundle, result);
			}

			if (hasPaymentAccess || hasFundsAccess || hasCounterOfferAccess || hasEditPricingAccess || hasWorkApproveAccess ||
				hasProjectAccess) {
				messageHelper.addError(bundle, "user.validation.employeeWorkerNoCustomPermissionsAllowed");
				messageHelper.setErrors(bundle, result);
			}
		}

		userFormControllerValidator.validate(user, result);

		if (result.hasErrors() || bundle.hasErrors()) {

			messageHelper.setErrors(bundle, result);
			redirectAttributes.addFlashAttribute("bundle", bundle);
			model.addAttribute("bundle", bundle);
			model.addAttribute("isAdmin", userRoleService.hasAclRole(submittedUser, AclRole.ACL_ADMIN));
			model.addAttribute("isManager", userRoleService.hasAclRole(submittedUser, AclRole.ACL_MANAGER));
			model.addAttribute("isController", userRoleService.hasAclRole(submittedUser, AclRole.ACL_CONTROLLER));
			model.addAttribute("isUser", userRoleService.hasAclRole(submittedUser, AclRole.ACL_USER));
			model.addAttribute("isViewOnly", userRoleService.hasAclRole(submittedUser, AclRole.ACL_VIEW_ONLY));
			model.addAttribute("isStaff", userRoleService.hasAclRole(submittedUser, AclRole.ACL_STAFF));
			model.addAttribute("isDeputy", userRoleService.hasAclRole(submittedUser, AclRole.ACL_DEPUTY));
			model.addAttribute("isDispatcher", userRoleService.hasAclRole(submittedUser, AclRole.ACL_DISPATCHER));
			model.addAttribute("isEmployeeWorker", userRoleService.hasAclRole(submittedUser, AclRole.ACL_EMPLOYEE_WORKER));
			model.addAttribute("companyName", userDetails.getCompanyName());
			model.addAttribute("workStatus", userRoleService.getWorkStatus(submittedUser));

			return VIEW_FORM;
		}

		Map<String, String> userProperties = CollectionUtilities.newStringMap(
			"firstName", user.getFirstName(),
			"lastName", user.getLastName(),
			"email", user.getEmail(),
			"spendLimit", String.valueOf(user.getSpendLimit()),
			"salary", String.valueOf(user.getSalary()));

		Long userId = user.getId();
		userService.updateUserProperties(user.getId(), userProperties);

		Set<RoleType> roles = userRoleService.getUserRoles(user);
		List<Long> submittedRoles = getRoleIds(roles);

		if (isAdmin) {
			submittedRoles.add(AclRole.ACL_ADMIN);
		}

		if (isManager) {
			submittedRoles.add(AclRole.ACL_MANAGER);
		}

		if (isController) {
			submittedRoles.add(AclRole.ACL_CONTROLLER);
		}

		if (isUser) {
			submittedRoles.add(AclRole.ACL_USER);
		}

		if (isViewOnly) {
			submittedRoles.add(AclRole.ACL_VIEW_ONLY);
		}

		if (isStaff) {
			submittedRoles.add(AclRole.ACL_STAFF);
		}

		if (isDeputy) {
			submittedRoles.add(AclRole.ACL_DEPUTY);
		}

		if(isDispatcher) {
			submittedRoles.add(AclRole.ACL_DISPATCHER);
		}

		if (workStatus.equals(User.WorkStatus.UNLISTED) || workStatus.equals(User.WorkStatus.PUBLIC)) {
			submittedRoles.add(AclRole.ACL_WORKER);
		}

		if (workStatus.equals(User.WorkStatus.PUBLIC)) {
			submittedRoles.add(AclRole.ACL_SHARED_WORKER);
		}

		if (isEmployeeWorker) {
			if (!submittedRoles.contains(AclRole.ACL_WORKER)) {
				submittedRoles.add(AclRole.ACL_WORKER);
			}
			submittedRoles.add(AclRole.ACL_EMPLOYEE_WORKER);
		}

		Map<String, String> profileProperties = CollectionUtilities.newStringMap(
			"workPhone", user.getProfile().getWorkPhone(),
			"workPhoneExtension", user.getProfile().getWorkPhoneExtension(),
			"workPhoneInternationalCode", user.getProfile().isWorkPhoneInternationalCodeSet() ? String.valueOf(user.getProfile().getWorkPhoneInternationalCode().getId()) : "",
			"jobTitle", user.getProfile().getJobTitle(),
			"findWork", BooleanUtils.toStringTrueFalse(CollectionUtilities.containsAny(submittedRoles, AclRole.ACL_SHARED_WORKER, AclRole.ACL_WORKER, AclRole.ACL_EMPLOYEE_WORKER)));


		authenticationService.setCustomAccess(hasPaymentAccess, hasFundsAccess, hasCounterOfferAccess,
			hasEditPricingAccess, hasWorkApproveAccess, hasProjectAccess, userId);

		profileService.updateProfileProperties(userId, profileProperties);

		List<User> companyAdmins = authenticationService.findAllUsersByACLRoleAndCompany(submittedUser.getCompany().getId(), AclRole.ACL_ADMIN);
		if (companyAdmins.size() == 0) {
			if (!isAdmin && user.getUserNumber() != null) {
				messageHelper.addError(bundle, "user.validation.cannotRemoveAdministrator", getCurrentUser().getCompanyName(), "");
				redirectAttributes.addFlashAttribute("bundle", bundle);
				return "redirect:/users";
			}
		}

		authenticationService.updateUserAclRoles(user.getId(), submittedRoles);
		companyService.updateListInVendorSearch(submittedUser.getCompany().getId());

		final Company company = companyService.findById(submittedUser.getCompany().getId());
		boolean isBuyer = CustomerType.BUYER.value().equals(company.getCustomerType()) ||
			CustomerType.MANAGED.value().equals(company.getCustomerType());
		if (isEmployeeWorker) {
			updatePersonaPreference(user, false, isDispatcher);
		} else {
			updatePersonaPreference(user, isBuyer, isDispatcher);
		}

		messageHelper.addSuccess(bundle, "users.updated");
		redirectAttributes.addFlashAttribute("bundle", bundle);

		return "redirect:/users";
	}

	@RequestMapping(
		value = "/reassign/{userNumber}",
		method = GET)
	public String showReassignUsersModal(
		@PathVariable("userNumber") String userNumber,
		@ModelAttribute("reassignForm") ReassignUserForm form,
		Model model,
		RedirectAttributes flash) {

		Map<Long, String> activeUsers = formDataHelper.getActiveUsers(getCurrentUser());
		User user = userService.findUserByUserNumber(userNumber);

		if (!canEditUser(user)) {
			MessageBundle bundle = messageHelper.newFlashBundle(flash);
			messageHelper.addError(bundle, "users.reassign.authorize");
			return "redirect:/users";
		}

		activeUsers.remove(user.getId());
		model.addAttribute("users", activeUsers);
		form.setCurrentOwner(userNumber);
		return "web/pages/user/reassign";
	}

	@RequestMapping(
		value = "/reassign",
		method = POST)
	public String submitReassignUsersModal(
		@Valid @ModelAttribute("reassignForm") ReassignUserForm form,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes redirectAttributes) {

		User user = userService.findUserByUserNumber(form.getCurrentOwner());

		if (user == null || user.getId() == null) {
			MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);
			messageHelper.addError(bundle, "users.edit.exception");
			return "redirect:/users";
		}

		if (!canEditUser(user)) {
			MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);
			messageHelper.addError(bundle, "users.reassign.authorize");
			return "redirect:/users";
		}

		userService.deactivateUser(
			user.getId(),
			form.getNewWorkOwner(),
			form.getNewGroupsOwner(),
			form.getNewAssessmentsOwner());

		if (bindingResult.hasErrors()) {
			MessageBundle messages = messageHelper.newBundle(model);
			messageHelper.setErrors(messages, bindingResult);
			return "web/pages/users/reassign";
		}

		MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);
		messageHelper.addSuccess(bundle, "users.deactivated");

		return "redirect:/users/edit_user/" + form.getCurrentOwner();
	}

	@RequestMapping(
		value = "/reactivate",
		method = POST)
	public String toggleStatus(
		@RequestParam("id") Long id,
		@RequestParam("status") String status,
		RedirectAttributes flash) {

		User user = userService.findUserById(id);

		if (!canEditUser(user)) {
			MessageBundle bundle = messageHelper.newFlashBundle(flash);
			messageHelper.addError(bundle, "users.reactivate.authorize");
			return "redirect:/users";
		}

		userService.reactivateUser(id);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		messageHelper.addSuccess(bundle, "users.activated");

		return "redirect:/users/edit_user/" + user.getUserNumber();
	}

	private List<Long> getRoleIds(Set<RoleType> roles) {
		List<Long> resultList = Lists.newArrayList();

		CollectionUtils.collect(roles, new Transformer() {
			@Override public Long transform(Object input) {
				RoleType roleType = (RoleType) input;
				return Long.parseLong(roleType.getCode());
			}
		}, resultList);
		return resultList;
	}

	private boolean canEditUser(User user) {
		// only admin, manager or same user can edit the user
		return user != null
			&& (user.getId().equals(getCurrentUser().getId()) ||
			(user.getCompany().getId().equals(getCurrentUser().getCompanyId()) && getCurrentUser().hasAnyRoles("ACL_ADMIN", "ROLE_SUPERUSER")));
	}

	private boolean isLastDispatcher(User user) {
		if (!userRoleService.hasAclRole(user, AclRole.ACL_DISPATCHER)) {
			return false;
		}
		List<Long> dispatcherUserIds = companyDAO.getUserIdsWithActiveRole(user.getCompany().getId(), AclRole.ACL_DISPATCHER);
		return dispatcherUserIds.size() == 1;
	}

	private void updatePersonaPreference(User user, boolean isBuyer, boolean isDispatcher) {
		Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(user.getId());
		PersonaPreference personaPreference;
		if (personaPreferenceOptional.isPresent()) {
			personaPreference = personaPreferenceOptional.get();
		} else {
			personaPreference = new PersonaPreference();
			personaPreference.setUserId(user.getId());
		}
		personaPreference
			.setBuyer(isBuyer)
			.setSeller(!isBuyer)
			.setDispatcher(isDispatcher);
		userService.saveOrUpdatePersonaPreference(personaPreference);
	}
}