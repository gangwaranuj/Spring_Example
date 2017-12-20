package com.workmarket.api.v2.employer.settings.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.Ostermiller.util.RandPass;
import com.workmarket.api.exceptions.ForbiddenException;
import com.workmarket.api.v2.employer.assignments.services.UseCase;
import com.workmarket.api.v2.employer.settings.models.CreateUserDTO;
import com.workmarket.api.v2.employer.settings.models.PermissionSettingsDTO;
import com.workmarket.api.v2.employer.settings.models.RoleSettingsDTO;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import com.workmarket.api.v2.model.AddressApiDTO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.acl.UserAclRoleAssociation;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.service.validation.ValidationService;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.negate;

public abstract class AbstractUserSettingUseCase<T, K> implements UseCase<T,K> {

	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private ProfileService profileService;
	@Autowired private UserService userService;
	@Autowired private UserRoleService userRoleService;
	@Autowired private RegistrationService registrationService;
	@Autowired private IndustryService industryService;
	@Autowired private CompanyService companyService;
	@Autowired private AddressService addressService;
	@Autowired private @Qualifier("addressValidationService") ValidationService addressValidationService;
	@Autowired private @Qualifier("userValidationService") ValidationService userValidationService;
	@Autowired private OrgStructureService orgStructureService;

	private List<ConstraintViolation> errors =  Lists.newArrayList();
	private Exception exception;
	private ExtendedUserDetails userDetails;
	private com.workmarket.service.business.dto.UserDTO employeeDTO;
	private Set<Long> roleIds = Sets.newHashSet();
	private Set<UserAclRoleAssociation> userAclRoleAssociations;
	private RandPass randomPasswords = new RandPass(new char[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
	private List<String> userOrgUnitUuids = Lists.newArrayList();

	protected Long id;
	protected String userNumber;
	protected User user;
	protected UserDTO userDTO;
	protected UserDTO.Builder userDTOBuilder;
	protected RoleSettingsDTO roleSettingsDTO;
	protected RoleSettingsDTO.Builder roleSettingsDTOBuilder;
	protected PermissionSettingsDTO permissionSettingsDTO;
	protected PermissionSettingsDTO.Builder permissionSettingsDTOBuilder;
	protected CreateUserDTO.OnboardingNotificationStrategy onboardingNotificationStrategy;
	
	protected abstract T me();
	protected abstract T handleExceptions() throws Exception;

	@Override
	public T execute() {
		try {
			failFast();
			init();
			prepare();
			process();
			save();
			finish();
		} catch (Exception e) {
			exception = e;
		}
		return me();
	}

	protected void failFast() {}

	protected void init() throws Exception {}

	protected void prepare() throws BeansException {}

	protected void process() throws ValidationException {}

	protected void save() throws Exception {}

	protected void finish() {}

	protected void getUserDetails() {
		userDetails = securityContextFacade.getCurrentUser();
	}

	protected void checkAccessPermissions() throws ForbiddenException {
		User currentUser = authenticationService.getCurrentUser();

		// Gotta be updating an employee of same company!
		User userToUpdate = userService.findUserByUserNumber(userNumber);
		if (!Objects.equals(userToUpdate.getCompany().getId(), currentUser.getCompany().getId())) {
			throw new ForbiddenException("Forbidden");
		}
	}

	protected void createEmployeeDTO() {
		employeeDTO = new com.workmarket.service.business.dto.UserDTO();
	}

	protected void copyUserDTO() {
		this.userDTOBuilder = new UserDTO.Builder(userDTO);
	}

	protected void getRoleSettingsDTO() {
		Assert.notNull(userDTO);
		roleSettingsDTO = userDTO.getRoleSettings();
	}

	protected void getPermissionSettingsDTO() {
		Assert.notNull(userDTO);
		permissionSettingsDTO = userDTO.getPermissionSettings();
	}

	protected void getUser() {
		Assert.notNull(userNumber);
		user = userService.findUserByUserNumber(userNumber);
	}

	protected void getUserAclRoleAssociations() {
		Assert.notNull(user);
		userAclRoleAssociations = userRoleService.getUserRoleAssociations(user);
	}

	protected void getUserOrgUnitMemberships() {
		Assert.notNull(user);
		userOrgUnitUuids = orgStructureService.getUserOrgUnitUuids(user.getUuid());
	}

	protected void loadRoles() {
		if (roleSettingsDTO.isAdmin()) {
			roleIds.add(AclRole.ACL_ADMIN);
		}

		if (roleSettingsDTO.isManager()) {
			roleIds.add(AclRole.ACL_MANAGER);
		}

		if (roleSettingsDTO.isController()) {
			roleIds.add(AclRole.ACL_CONTROLLER);
		}

		if (roleSettingsDTO.isUser()) {
			roleIds.add(AclRole.ACL_USER);
		}

		if (roleSettingsDTO.isViewOnly()) {
			roleIds.add(AclRole.ACL_VIEW_ONLY);
		}

		if (roleSettingsDTO.isStaff()) {
			roleIds.add(AclRole.ACL_STAFF);
		}

		if (roleSettingsDTO.isDeputy()) {
			roleIds.add(AclRole.ACL_DEPUTY);
		}

		if (roleSettingsDTO.isEmployeeWorker()) {
			roleIds.add(AclRole.ACL_EMPLOYEE_WORKER);
		}

		String workStatus = userDTO.getWorkStatus();
		if (User.WorkStatus.UNLISTED.name().equals(workStatus)
			|| User.WorkStatus.PUBLIC.name().equals(workStatus)
			|| roleSettingsDTO.isEmployeeWorker()) {
			roleIds.add(AclRole.ACL_WORKER);
		}

		if (User.WorkStatus.PUBLIC.name().equals(workStatus)) {
			roleIds.add(AclRole.ACL_SHARED_WORKER);
		}

		if (roleSettingsDTO.isDispatcher()) {
			roleIds.add(AclRole.ACL_DISPATCHER);
		}
	}

	protected void loadEmployeeDTO() throws BeansException {
		Assert.notNull(employeeDTO);
		Assert.notNull(userDTO);
		BeanUtils.copyProperties(userDTO, employeeDTO);

		if (employeeDTO.getId() == null) {
			employeeDTO.setPassword(randomPasswords.getPass(4));
		}
	}

	protected void loadRoleSettingsDTO() {
		Long aclRole;
		roleSettingsDTOBuilder = new RoleSettingsDTO.Builder();
		for (UserAclRoleAssociation userAclRoleAssociation : userAclRoleAssociations) {
			if (userAclRoleAssociation.getDeleted()) {
				continue;
			}
			
			aclRole = userAclRoleAssociation.getRole().getId();

			if (AclRole.ACL_ADMIN == aclRole) {
				roleSettingsDTOBuilder.setAdmin(true);
			}

			if (AclRole.ACL_MANAGER == aclRole) {
				roleSettingsDTOBuilder.setManager(true);
			}

			if (AclRole.ACL_USER == aclRole) {
				roleSettingsDTOBuilder.setUser(true);
			}

			if (AclRole.ACL_VIEW_ONLY == aclRole) {
				roleSettingsDTOBuilder.setViewOnly(true);
			}

			if (AclRole.ACL_STAFF == aclRole) {
				roleSettingsDTOBuilder.setStaff(true);
			}

			if (AclRole.ACL_DEPUTY == aclRole) {
				roleSettingsDTOBuilder.setDeputy(true);
			}

			if (AclRole.ACL_DISPATCHER == aclRole) {
				roleSettingsDTOBuilder.setDispatcher(true);
			}

			if (AclRole.ACL_EMPLOYEE_WORKER == aclRole) {
				roleSettingsDTOBuilder.setEmployeeWorker(true);
			}
		}
	}

	protected void loadPermissionSettingsDTO() {
		permissionSettingsDTOBuilder = new PermissionSettingsDTO.Builder();
		permissionSettingsDTOBuilder
			.setPaymentAccessible(authenticationService.hasPaymentCenterAndEmailsAccess(user.getId(), Boolean.TRUE))
			.setFundsAccessible(authenticationService.hasPaymentCenterAndEmailsAccess(user.getId(), Boolean.TRUE) ?
				authenticationService.hasManageBankAndFundsAccess(user.getId(), Boolean.TRUE) : Boolean.FALSE)
			.setCounterOfferAccessible(userRoleService.hasPermissionsForCustomAuth(user.getId(), Permission.COUNTEROFFER_AUTH))
			.setPricingEditable(userRoleService.hasPermissionsForCustomAuth(user.getId(), Permission.EDIT_PRICING_AUTH))
			.setWorkApprovalAllowed(userRoleService.hasPermissionsForCustomAuth(user.getId(), Permission.APPROVE_WORK_AUTH))
			.setProjectAccessible(authenticationService.hasProjectAccess(user.getId()));
	}

	protected void loadUserDTO() {
		if (user != null) {
			userDTOBuilder = new UserDTO.Builder();
			userDTOBuilder
				.setId(user.getId())
				.setUserNumber(user.getUserNumber())
				.setFirstName(user.getFirstName())
				.setLastName(user.getLastName())
				.setEmail(user.getEmail())
				.setRoleSettings(roleSettingsDTOBuilder)
				.setPermissionSettings(permissionSettingsDTOBuilder)
				.setSpendLimit(user.getSpendLimit())
				.setWorkStatus(userRoleService.getWorkStatus(user).name())
				.setOrgUnitUuids(userOrgUnitUuids);

			Profile profile = user.getProfile();
			if (profile != null) {
				userDTOBuilder
					.setJobTitle(profile.getJobTitle())
					.setWorkPhone(profile.getWorkPhone())
					.setWorkPhoneExtension(profile.getWorkPhoneExtension());

				if (profile.getAddressId() != null) {
					Address address = addressService.findById(profile.getAddressId());
					userDTOBuilder.setAddress(
						new AddressApiDTO.Builder()
							.setAddressLine1(address.getAddress1())
							.setAddressLine2(address.getAddress2())
							.setCity(address.getCity())
							.setState(address.getState().getShortName())
							.setPostalCode(address.getPostalCode())
							.setCountry(address.getCountry().getISO3())
							.setAddressTypeCode(address.getAddressType().getCode())
					);
				}

				String workPhoneInternationalCode = profile.isWorkPhoneInternationalCodeSet() ?
					String.valueOf(profile.getWorkPhoneInternationalCode().getId()) : "";
				userDTOBuilder.setWorkPhoneInternationalCode(workPhoneInternationalCode);

				Long industryId = industryService.getDefaultIndustryForProfile(profile.getId()).getId();
				userDTOBuilder.setIndustryId(industryId);
			}
		}
	}

	protected void validateUser() {
		userValidationService.validate(userDTO, errors);
	}

	protected void validateAddress() {
		if (!allAddressFieldsAreNull(userDTO.getAddress())) {
			addressValidationService.validate(userDTO.getAddress().asAddressDTO(), errors);
		}
	}

	protected void saveOrUpdateUser() throws Exception {
		if (isNotEmpty(errors)) {
			throw new ValidationException("Unable to save employee", errors);
		}

		if (user == null) {
			boolean shouldEmailNewUser = negate(CreateUserDTO.OnboardingNotificationStrategy.SUPPRESS.equals(onboardingNotificationStrategy));
			user = registrationService.registerNewForCompany(
				employeeDTO,
				userDetails.getCompanyId(),
				roleIds.toArray(new Long[roleIds.size()]),
				shouldEmailNewUser
			);
			id = user.getId();
			userNumber = user.getUserNumber();
		} else {
			Map<String, String> userProperties = CollectionUtilities.newStringMap(
				"firstName", employeeDTO.getFirstName(),
				"lastName", employeeDTO.getLastName(),
				"email", employeeDTO.getEmail(),
				"spendLimit", String.valueOf(employeeDTO.getSpendLimit()),
				"salary", String.valueOf(employeeDTO.getSalary()));
			userService.updateUserProperties(user.getId(), userProperties);
			authenticationService.updateUserAclRoles(user.getId(), Lists.newArrayList(roleIds));
			saveOrUpdatePersonaPreference(user.getId());
		}
	}

	protected void saveOrUpdateProfile(boolean shouldSendEmail) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Assert.notNull(id);
		Map<String, String> profileMap = CollectionUtilities.newStringMap(
			"workPhone", userDTO.getWorkPhone(),
			"workPhoneInternationalCode", userDTO.getWorkPhoneInternationalCode() == null ? "" : userDTO.getWorkPhoneInternationalCode(),
			"workPhoneExtension", userDTO.getWorkPhoneExtension(),
			"jobTitle", userDTO.getJobTitle(),
			"industry.id", String.valueOf(userDTO.getIndustryId())
		);

		profileService.updateProfileProperties(id, profileMap, shouldSendEmail);

		// Only update address if at least one field was set
		// Can't just check if getAddress() is null bc we instantiate an object
		// where all the *fields* are null
		if (!allAddressFieldsAreNull(userDTO.getAddress())) {
			Map<String, String> addressProperties = CollectionUtilities.newStringMap(
				"address1", userDTO.getAddress().getAddressLine1(),
				"address2", userDTO.getAddress().getAddressLine2(),
				"city", userDTO.getAddress().getCity(),
				"state", userDTO.getAddress().getState(),
				"postalCode", userDTO.getAddress().getPostalCode(),
				"country", userDTO.getAddress().getCountry(),
				"addressType", "profile");
			profileService.updateProfileAddressProperties(id, addressProperties);
		}
	}

	private boolean allAddressFieldsAreNull(AddressApiDTO addressApiDTO) {
		return (addressApiDTO.getAddressLine1() == null
			&& addressApiDTO.getAddressLine2() == null
			&& addressApiDTO.getCity() == null
			&& addressApiDTO.getState() == null
			&& addressApiDTO.getPostalCode() == null
			&& addressApiDTO.getCountry() == null
		);
	}

	protected void saveOrUpdateCustomPermissions() {
		authenticationService.setCustomAccess(
			permissionSettingsDTO.isPaymentAccessible(),
			permissionSettingsDTO.isFundsAccessible(),
			permissionSettingsDTO.isCounterOfferAccessible(),
			permissionSettingsDTO.isPricingEditable(),
			permissionSettingsDTO.isWorkApprovalAllowed(),
			permissionSettingsDTO.isProjectAccessible(),
			id
		);
	}

	protected void saveOrUpdateOrgUnitMemberships() {
		// null means don't modify, empty list means remove all
		if (userDTO.getOrgUnitUuids() != null) {
			orgStructureService.setUserMemberships(userDTO.getOrgUnitUuids(), user.getUuid());
			return;
		}

		// option to use the path version instead, but this is only additive at the moment
		if (!CollectionUtils.isEmpty(userDTO.getOrgUnitPaths())) {
			String companyUuid = userService.findUserById(user.getId()).getCompany().getUuid();
			final String orgChartUuid = orgStructureService.getOrgChartUuidFromCompanyUuid(companyUuid);
			if (!StringUtils.isEmpty(orgChartUuid)) {
				orgStructureService.assignUsersFromBulk(userDTO.getOrgUnitPaths(), user.getUuid(), companyUuid, user.getEmail(), orgChartUuid);
			}
		}
	}

	private void saveOrUpdatePersonaPreference(Long userId) {
		PersonaPreference personaPreference = userService.getPersonaPreference(userId).or(new PersonaPreference());
		final boolean isBuyer = negate(userService.belongsToWorkerCompany());
		if (userDTO.getRoleSettings().isEmployeeWorker()) {
			personaPreference.setBuyer(!isBuyer)
				.setSeller(isBuyer);
		} else {
			personaPreference.setBuyer(isBuyer)
				.setSeller(!isBuyer);
		}
		userService.saveOrUpdatePersonaPreference(personaPreference);
	}

	protected void handleValidationException() throws ValidationException {
		if (exception instanceof ValidationException) {
			throw (ValidationException) exception;
		}
	}

	protected void handleForbiddenException() throws ForbiddenException {
		if (exception instanceof ForbiddenException) {
			throw (ForbiddenException) exception;
		}
	}

	protected void handleNoSuchMethodException() throws NoSuchMethodException {
		if (exception instanceof NoSuchMethodException) {
			throw (NoSuchMethodException) exception;
		}
	}

	protected void handleIllegalAccessException() throws IllegalAccessException {
		if (exception instanceof IllegalAccessException) {
			throw (IllegalAccessException) exception;
		}
	}

	protected void handleInvocationTargetException() throws InvocationTargetException {
		if (exception instanceof InvocationTargetException) {
			throw (InvocationTargetException) exception;
		}
	}

	protected void handleBeansException() throws BeansException {
		if (exception instanceof BeansException) {
			throw (BeansException) exception;
		}
	}
}
