package com.workmarket.api.v2.employer.settings.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.base.Optional;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.api.v2.employer.settings.controllers.support.PermissionSettingsMaker;
import com.workmarket.api.v2.employer.settings.controllers.support.RoleSettingsMaker;
import com.workmarket.api.v2.employer.settings.controllers.support.UserMaker;
import com.workmarket.api.v2.employer.settings.models.CreateUserDTO;
import com.workmarket.api.v2.employer.settings.models.PermissionSettingsDTO;
import com.workmarket.api.v2.employer.settings.models.RoleSettingsDTO;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import com.workmarket.api.v2.model.AddressApiDTO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.RoleType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.test.IntegrationTest;
import com.workmarket.web.helpers.MessageBundleHelper;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static com.natpryce.makeiteasy.MakeItEasy.withNull;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.errorType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mapType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.userResponseType;
import static com.workmarket.api.v2.employer.settings.controllers.support.PermissionSettingsMaker.counterOfferAccessible;
import static com.workmarket.api.v2.employer.settings.controllers.support.PermissionSettingsMaker.paymentAccessible;
import static com.workmarket.api.v2.employer.settings.controllers.support.RoleSettingsMaker.admin;
import static com.workmarket.api.v2.employer.settings.controllers.support.RoleSettingsMaker.dispatcher;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.address;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.email;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.firstName;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.id;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.industryId;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.lastName;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.permissionSettings;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.roleSettings;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.spendLimit;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.userNumber;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.workPhone;
import static org.apache.commons.lang3.StringUtils.join;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserSettingControllerIT extends ApiV2BaseIT {

	private static final String ENDPOINT = "/employer/v2/settings/users/";
	
	@Autowired private MessageBundleHelper messageHelper;
	private ObjectMapper jackson = new ObjectMapper();
	private FilterProvider filters;

	@Before
	public void setUp() throws Exception {
		filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(Collections.emptySet())
		);
		login();
	}

	@Test
	public void saveUserWithEmptyFirstName() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(firstName)));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("firstName")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.firstNameRequired"))));
	}

	@Test
	public void saveUserWithEmptyLastName() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(lastName)));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("lastName")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.lastNameRequired"))));
	}

	@Test
	public void saveUserWithEmptyEmail() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(email)));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("email")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.emailRequired"))));
	}

	@Test
	public void saveUserWithInvalidEmail() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO, with(email, "abc@.com")));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("email")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.emailInvalid"))));
	}

	@Test
	public void saveUserWithExistingEmail() throws Exception {
		User currentUser = authenticationService.getCurrentUser();
		UserDTO employee = make(a(UserMaker.UserDTO, with(email, currentUser.getEmail())));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("email")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.emailExists"))));
	}

	@Test
	public void saveUserWithEmptyWorkPhone() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(workPhone)));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("workPhone")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.workPhoneRequired"))));
	}

	@Test
	public void saveUserWithInvalidWorkPhone() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO, with(workPhone, "9#0 999-99#9")));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("workPhone")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.workPhoneInvalid"))));
	}

	@Test
	public void saveUserWithEmptySpendLimit() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(spendLimit)));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("spendLimit")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.spendLimit"))));
	}

	@Test
	public void saveUserWithNegativeSpendLimit() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO, with(spendLimit, new BigDecimal(-1))));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("spendLimit")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.spendLimit"))));
	}

	@Test
	public void saveUserWithEmptyIndustry() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(industryId)));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("industryId")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.industryRequired"))));
	}

	@Test
	public void saveUserWithNoRoleSelected() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTOWithNoRole));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("field", is("roleSettings")));
		assertThat(result, hasProperty("message", is(messageHelper.getMessage("user.validation.notWorkerRolesRequired"))));
	}

	@Test
	public void saveUserSuccess() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO));
		UserDTO result = postNewUserAndApprove(employee);
		assertThat(result, hasProperty("userNumber", is(not(nullValue()))));
		assertThat(result, hasProperty("firstName", is(employee.getFirstName())));
		assertThat(result, hasProperty("lastName", is(employee.getLastName())));
		assertThat(result, hasProperty("email", is(employee.getEmail())));
		assertThat(result, hasProperty("workPhone", is(employee.getWorkPhone())));
		assertThat(result, hasProperty("workPhoneExtension", is(employee.getWorkPhoneExtension())));
		assertThat(result, hasProperty("workPhoneInternationalCode", is(employee.getWorkPhoneInternationalCode())));
		assertThat(result, hasProperty("jobTitle", is(employee.getJobTitle())));
		assertThat(result, hasProperty("industryId", is(employee.getIndustryId())));
		assertThat(result, hasProperty("spendLimit", is(employee.getSpendLimit())));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("admin", is(employee.getRoleSettings().isAdmin()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("manager", is(employee.getRoleSettings().isManager()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("controller", is(employee.getRoleSettings().isController()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("user", is(employee.getRoleSettings().isUser()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("viewOnly", is(employee.getRoleSettings().isViewOnly()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("staff", is(employee.getRoleSettings().isStaff()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("deputy", is(employee.getRoleSettings().isDeputy()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("dispatcher", is(employee.getRoleSettings().isDispatcher()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("employeeWorker", is(employee.getRoleSettings().isEmployeeWorker()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("paymentAccessible", is(employee.getPermissionSettings().isPaymentAccessible()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("fundsAccessible", is(employee.getPermissionSettings().isFundsAccessible()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("counterOfferAccessible", is(employee.getPermissionSettings().isCounterOfferAccessible()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("pricingEditable", is(employee.getPermissionSettings().isPricingEditable()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("workApprovalAllowed", is(employee.getPermissionSettings().isWorkApprovalAllowed()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("projectAccessible", is(employee.getPermissionSettings().isProjectAccessible()))));
		assertThat(result, hasProperty("workStatus", is(employee.getWorkStatus())));
	}

	@Test
	public void saveUserAsEmployeeWorkerFailRoleValidation() throws Exception {
		RoleSettingsDTO roleSettingsDTO = make(a(RoleSettingsMaker.EmployeeWorkerRoleSettings, with(admin, true)));
		UserDTO employee = make(a(UserMaker.UserDTOWithEmployeeWorkerRole, with(roleSettings, new RoleSettingsDTO.Builder(roleSettingsDTO))));

		String employeeJson = jackson.writer(filters).writeValueAsString(employee);
		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError updatedResult = getFirstResult(mvcResult, errorType);
		assertThat(updatedResult, hasProperty("field", is("roleSettings")));
		assertThat(updatedResult, hasProperty("message", is(messageHelper.getMessage("user.validation.employeeWorkerNoOtherRolesAllowed"))));
	}

	@Test
	public void saveUserAsEmployeeWorkerFailPermissionValidation() throws Exception {
		PermissionSettingsDTO permissionSettingsDTO = make(a(PermissionSettingsMaker.NoPermissionSettings, with(paymentAccessible, true)));
		UserDTO employee = make(a(UserMaker.UserDTOWithEmployeeWorkerRole, with(permissionSettings, new PermissionSettingsDTO.Builder(permissionSettingsDTO))));

		String employeeJson = jackson.writer(filters).writeValueAsString(employee);
		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError updatedResult = getFirstResult(mvcResult, errorType);
		assertThat(updatedResult, hasProperty("field", is("permissionSettings")));
		assertThat(updatedResult, hasProperty("message", is(messageHelper.getMessage("user.validation.employeeWorkerNoCustomPermissionsAllowed"))));
	}

	@Test
	public void saveUserAsEmployeeWorkerSuccess() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTOWithEmployeeWorkerRole));
		UserDTO result = postNewUserAndApprove(employee);
		User newUser = userService.findUserByUserNumber(result.getUserNumber());
		Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(newUser.getId());
		PersonaPreference personaPreference = personaPreferenceOptional.orNull();

		assertThat(result, hasProperty("userNumber", is(not(nullValue()))));
		assertThat(result, hasProperty("firstName", is(employee.getFirstName())));
		assertThat(result, hasProperty("lastName", is(employee.getLastName())));
		assertThat(result, hasProperty("email", is(employee.getEmail())));
		assertThat(result, hasProperty("workPhone", is(employee.getWorkPhone())));
		assertThat(result, hasProperty("workPhoneExtension", is(employee.getWorkPhoneExtension())));
		assertThat(result, hasProperty("workPhoneInternationalCode", is(employee.getWorkPhoneInternationalCode())));
		assertThat(result, hasProperty("jobTitle", is(employee.getJobTitle())));
		assertThat(result, hasProperty("industryId", is(employee.getIndustryId())));
		assertThat(result, hasProperty("spendLimit", is(employee.getSpendLimit())));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("admin", is(employee.getRoleSettings().isAdmin()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("manager", is(employee.getRoleSettings().isManager()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("controller", is(employee.getRoleSettings().isController()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("user", is(employee.getRoleSettings().isUser()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("viewOnly", is(employee.getRoleSettings().isViewOnly()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("staff", is(employee.getRoleSettings().isStaff()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("deputy", is(employee.getRoleSettings().isDeputy()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("dispatcher", is(employee.getRoleSettings().isDispatcher()))));
		assertThat(result, hasProperty("roleSettings",
			hasProperty("employeeWorker", is(employee.getRoleSettings().isEmployeeWorker()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("paymentAccessible", is(employee.getPermissionSettings().isPaymentAccessible()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("fundsAccessible", is(employee.getPermissionSettings().isFundsAccessible()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("counterOfferAccessible", is(employee.getPermissionSettings().isCounterOfferAccessible()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("pricingEditable", is(employee.getPermissionSettings().isPricingEditable()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("workApprovalAllowed", is(employee.getPermissionSettings().isWorkApprovalAllowed()))));
		assertThat(result, hasProperty("permissionSettings",
			hasProperty("projectAccessible", is(employee.getPermissionSettings().isProjectAccessible()))));
		assertThat(result, hasProperty("workStatus", is(employee.getWorkStatus())));
		assertThat(personaPreferenceOptional.isPresent(), is(true));
		assertThat(personaPreference.isSeller(), is(true));
		assertThat(personaPreference.isBuyer(), is(false));
	}

	@Test
	public void saveUserWithAddressSuccess() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTOWithAddress));
		UserDTO result = postNewUserAndApprove(employee);

		assertThat(result, hasProperty("userNumber", is(not(nullValue()))));
		assertThat(result, hasProperty("firstName", is(employee.getFirstName())));

		assertThat(result, hasProperty("address",
			hasProperty("addressLine1", is(employee.getAddress().getAddressLine1()))));
		assertThat(result, hasProperty("address",
			hasProperty("addressLine2", is(employee.getAddress().getAddressLine2()))));
		assertThat(result, hasProperty("address",
			hasProperty("city", is(employee.getAddress().getCity()))));
		assertThat(result, hasProperty("address",
			hasProperty("state", is(employee.getAddress().getState()))));
		assertThat(result, hasProperty("address",
			hasProperty("postalCode", is(employee.getAddress().getPostalCode()))));
		assertThat(result, hasProperty("address",
			hasProperty("country", is(employee.getAddress().getCountry()))));
	}

	@Test
	public void saveUserWithInvalidAddressFail() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTOWithInvlalidAddress));

		String employeeJson = jackson.writer(filters).writeValueAsString(employee);
		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();
	}

	@Test
	public void getUserSuccess() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO));
		UserDTO postResult = postNewUserAndApprove(employee);
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + postResult.getUserNumber())
		).andExpect(status().isOk()).andReturn();
		UserDTO getResult = getFirstResult(mvcResult, userResponseType).build();

		assertThat(getResult, hasProperty("userNumber", samePropertyValuesAs(postResult.getUserNumber())));
		assertThat(getResult, hasProperty("firstName", samePropertyValuesAs(postResult.getFirstName())));
		assertThat(getResult, hasProperty("lastName", samePropertyValuesAs(postResult.getLastName())));
		assertThat(getResult, hasProperty("email", samePropertyValuesAs(postResult.getEmail())));
		assertThat(getResult, hasProperty("workPhone", samePropertyValuesAs(postResult.getWorkPhone())));
		assertThat(getResult, hasProperty("workPhoneExtension", samePropertyValuesAs(postResult.getWorkPhoneExtension())));
		assertThat(getResult, hasProperty("workPhoneInternationalCode", samePropertyValuesAs(postResult.getWorkPhoneInternationalCode())));
		assertThat(getResult, hasProperty("jobTitle", samePropertyValuesAs(postResult.getJobTitle())));
		assertThat(getResult, hasProperty("industryId", samePropertyValuesAs(postResult.getIndustryId())));
		assertThat(getResult, hasProperty("spendLimit", samePropertyValuesAs(postResult.getSpendLimit())));
		assertThat(getResult, hasProperty("roleSettings",
			hasProperty("admin", samePropertyValuesAs(postResult.getRoleSettings().isAdmin()))));
		assertThat(getResult, hasProperty("roleSettings",
			hasProperty("manager", samePropertyValuesAs(postResult.getRoleSettings().isManager()))));
		assertThat(getResult, hasProperty("roleSettings",
			hasProperty("controller", samePropertyValuesAs(postResult.getRoleSettings().isController()))));
		assertThat(getResult, hasProperty("roleSettings",
			hasProperty("user", samePropertyValuesAs(postResult.getRoleSettings().isUser()))));
		assertThat(getResult, hasProperty("roleSettings",
			hasProperty("viewOnly", samePropertyValuesAs(postResult.getRoleSettings().isViewOnly()))));
		assertThat(getResult, hasProperty("roleSettings",
			hasProperty("staff", samePropertyValuesAs(postResult.getRoleSettings().isStaff()))));
		assertThat(getResult, hasProperty("roleSettings",
			hasProperty("deputy", samePropertyValuesAs(postResult.getRoleSettings().isDeputy()))));
		assertThat(getResult, hasProperty("roleSettings",
			hasProperty("dispatcher", samePropertyValuesAs(postResult.getRoleSettings().isDispatcher()))));
		assertThat(getResult, hasProperty("roleSettings",
			hasProperty("employeeWorker", is(employee.getRoleSettings().isEmployeeWorker()))));
		assertThat(getResult, hasProperty("permissionSettings",
			hasProperty("paymentAccessible", samePropertyValuesAs(postResult.getPermissionSettings().isPaymentAccessible()))));
		assertThat(getResult, hasProperty("permissionSettings",
			hasProperty("fundsAccessible", samePropertyValuesAs(postResult.getPermissionSettings().isFundsAccessible()))));
		assertThat(getResult, hasProperty("permissionSettings",
			hasProperty("counterOfferAccessible", samePropertyValuesAs(postResult.getPermissionSettings().isCounterOfferAccessible()))));
		assertThat(getResult, hasProperty("permissionSettings",
			hasProperty("pricingEditable", samePropertyValuesAs(postResult.getPermissionSettings().isPricingEditable()))));
		assertThat(getResult, hasProperty("permissionSettings",
			hasProperty("workApprovalAllowed", samePropertyValuesAs(postResult.getPermissionSettings().isWorkApprovalAllowed()))));
		assertThat(getResult, hasProperty("permissionSettings",
			hasProperty("projectAccessible", samePropertyValuesAs(postResult.getPermissionSettings().isProjectAccessible()))));
		assertThat(getResult, hasProperty("workStatus", samePropertyValuesAs(postResult.getWorkStatus())));
	}

	@Test
	public void getUserDifferentCompanyFail() throws Exception {
		User employee = newFirstEmployee();
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + employee.getUserNumber())
		).andExpect(status().isForbidden()).andReturn();
	}

	@Test
	public void getAllUsersDifferentCompanyFail() throws Exception {
		User employee = newFirstEmployee();

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("companyId", String.valueOf(employee.getCompany().getCompanyNumber()))
				.param("fields", "userNumber", "fullName", "email", "rolesString", "latestActivityOn")
		).andExpect(status().isForbidden()).andReturn();
	}

	@Test
	public void getAllActiveUsersSuccess() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("companyId", String.valueOf(details.getCompanyNumber()))
				.param("fields", "userNumber", "fullName", "email", "rolesString", "latestActivityOn")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> getResult = getFirstResult(mvcResult, mapType);
		String roleNames = getRoleNamesStringByUserId(user.getId());

		assertThat(getResult, hasEntry("userNumber", String.valueOf(user.getUserNumber())));
		assertThat(getResult, hasEntry("fullName", String.valueOf(user.getFullName())));
		assertThat(getResult, hasEntry("email", String.valueOf(user.getEmail())));
		assertThat(getResult, hasEntry("rolesString", String.valueOf(roleNames)));
	}

	@Test
	public void putUserRevokingLastAdmin() throws Exception {
		Company company = companyService.findCompanyById(user.getCompany().getId());

		UserDTO employee = make(a(UserMaker.UserDTO));
		UserDTO result = postNewUserAndApprove(employee);

		// remove admin role from current user
		authenticationService.removeAclRoleFromUser(user.getId(), AclRole.ACL_ADMIN);
		userService.assignRole(user.getId(), RoleType.WM_ADMIN);

		RoleSettingsDTO roleSettingsDTO = make(a(RoleSettingsMaker.DefaultRoleSettings, with(admin, false)));
		employee = make(a(UserMaker.UserDTO,
			with(id, result.getId()),
			with(userNumber, result.getUserNumber()),
			with(email, result.getEmail()),
			with(roleSettings, new RoleSettingsDTO.Builder(roleSettingsDTO))));
		String employeeJson = jackson.writer(filters).writeValueAsString(employee);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + result.getUserNumber())
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError error = getFirstResult(mvcResult, errorType);
		assertThat(error, hasProperty("field", is("roleSettings")));
		assertThat(error, hasProperty("message",
			is(messageHelper.getMessage("user.validation.cannotRemoveAdministrator", new Object[]{company.getName()}))));
	}

	@Test
	public void putUserSuccess() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO));
		UserDTO result = postNewUserAndApprove(employee);
		RoleSettingsDTO roleSettingsDTO = make(a(RoleSettingsMaker.DefaultRoleSettings, with(admin, false)));
		PermissionSettingsDTO permissionSettingsDTO = make(a(PermissionSettingsMaker.DefaultPermissionSettings, with(counterOfferAccessible, false)));
		employee = make(a(UserMaker.UserDTO,
			with(id, result.getId()),
			with(userNumber, result.getUserNumber()),
			with(email, result.getEmail()),
			with(spendLimit, new BigDecimal("1500.00")),
			with(workPhone, "888-888-8888"),
			with(roleSettings, new RoleSettingsDTO.Builder(roleSettingsDTO)),
			with(permissionSettings, new PermissionSettingsDTO.Builder(permissionSettingsDTO))));
		UserDTO updatedResult = updateUser(result.getUserNumber(), employee);

		assertThat(updatedResult, hasProperty("userNumber", samePropertyValuesAs(result.getUserNumber())));
		assertThat(updatedResult, hasProperty("firstName", samePropertyValuesAs(result.getFirstName())));
		assertThat(updatedResult, hasProperty("lastName", samePropertyValuesAs(result.getLastName())));
		assertThat(updatedResult, hasProperty("email", samePropertyValuesAs(result.getEmail())));
		assertThat(updatedResult, hasProperty("workPhone", not(samePropertyValuesAs(result.getWorkPhone()))));
		assertThat(updatedResult, hasProperty("workPhoneExtension", samePropertyValuesAs(result.getWorkPhoneExtension())));
		assertThat(updatedResult, hasProperty("workPhoneInternationalCode", samePropertyValuesAs(result.getWorkPhoneInternationalCode())));
		assertThat(updatedResult, hasProperty("jobTitle", samePropertyValuesAs(result.getJobTitle())));
		assertThat(updatedResult, hasProperty("industryId", samePropertyValuesAs(result.getIndustryId())));
		assertThat(updatedResult, hasProperty("spendLimit", is(not(result.getSpendLimit()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("admin", is(not((result.getRoleSettings().isAdmin()))))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("manager", is(result.getRoleSettings().isManager()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("controller", is(result.getRoleSettings().isController()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("user", is(result.getRoleSettings().isUser()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("viewOnly", is(result.getRoleSettings().isViewOnly()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("staff", is(result.getRoleSettings().isStaff()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("deputy", is(result.getRoleSettings().isDeputy()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("dispatcher", is(result.getRoleSettings().isDispatcher()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("employeeWorker", is(employee.getRoleSettings().isEmployeeWorker()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("paymentAccessible", is(result.getPermissionSettings().isPaymentAccessible()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("fundsAccessible", is(result.getPermissionSettings().isFundsAccessible()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("counterOfferAccessible", is(not(result.getPermissionSettings().isCounterOfferAccessible())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("pricingEditable", is(result.getPermissionSettings().isPricingEditable()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("workApprovalAllowed", is(result.getPermissionSettings().isWorkApprovalAllowed()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("projectAccessible", is(result.getPermissionSettings().isProjectAccessible()))));
		assertThat(updatedResult, hasProperty("workStatus", samePropertyValuesAs(result.getWorkStatus())));
	}

	@Test
	public void putUserWithAddressSuccess() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO));
		UserDTO result = postNewUserAndApprove(employee);
		RoleSettingsDTO roleSettingsDTO = make(a(RoleSettingsMaker.DefaultRoleSettings, with(admin, false)));
		PermissionSettingsDTO permissionSettingsDTO = make(a(PermissionSettingsMaker.DefaultPermissionSettings, with(counterOfferAccessible, false)));
		AddressApiDTO addressApiDTO = new AddressApiDTO.Builder()
			.setAddressLine1("1 Main St")
			.setCity("Summit")
			.setState("NJ")
			.setPostalCode("07901")
			.setCountry("USA")
			.build();

		employee = make(a(UserMaker.UserDTOWithAddress,
			with(id, result.getId()),
			with(userNumber, result.getUserNumber()),
			with(email, result.getEmail()),
			with(spendLimit, new BigDecimal("1500.00")),
			with(workPhone, "888-888-8888"),
			with(roleSettings, new RoleSettingsDTO.Builder(roleSettingsDTO)),
			with(permissionSettings, new PermissionSettingsDTO.Builder(permissionSettingsDTO)),
			with(address, new AddressApiDTO.Builder(addressApiDTO))));
		UserDTO updatedResult = updateUser(result.getUserNumber(), employee);

		assertThat(updatedResult, hasProperty("userNumber", samePropertyValuesAs(result.getUserNumber())));
		assertThat(updatedResult, hasProperty("firstName", samePropertyValuesAs(result.getFirstName())));
		assertThat(updatedResult, hasProperty("lastName", samePropertyValuesAs(result.getLastName())));
		assertThat(updatedResult, hasProperty("email", samePropertyValuesAs(result.getEmail())));
		assertThat(updatedResult, hasProperty("workPhone", not(samePropertyValuesAs(result.getWorkPhone()))));
		assertThat(updatedResult, hasProperty("workPhoneExtension", samePropertyValuesAs(result.getWorkPhoneExtension())));
		assertThat(updatedResult, hasProperty("workPhoneInternationalCode", samePropertyValuesAs(result.getWorkPhoneInternationalCode())));
		assertThat(updatedResult, hasProperty("jobTitle", samePropertyValuesAs(result.getJobTitle())));
		assertThat(updatedResult, hasProperty("industryId", samePropertyValuesAs(result.getIndustryId())));
		assertThat(updatedResult, hasProperty("spendLimit", is(not(result.getSpendLimit()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("admin", is(not((result.getRoleSettings().isAdmin()))))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("manager", is(result.getRoleSettings().isManager()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("controller", is(result.getRoleSettings().isController()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("user", is(result.getRoleSettings().isUser()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("viewOnly", is(result.getRoleSettings().isViewOnly()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("staff", is(result.getRoleSettings().isStaff()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("deputy", is(result.getRoleSettings().isDeputy()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("dispatcher", is(result.getRoleSettings().isDispatcher()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("employeeWorker", is(employee.getRoleSettings().isEmployeeWorker()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("paymentAccessible", is(result.getPermissionSettings().isPaymentAccessible()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("fundsAccessible", is(result.getPermissionSettings().isFundsAccessible()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("counterOfferAccessible", is(not(result.getPermissionSettings().isCounterOfferAccessible())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("pricingEditable", is(result.getPermissionSettings().isPricingEditable()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("workApprovalAllowed", is(result.getPermissionSettings().isWorkApprovalAllowed()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("projectAccessible", is(result.getPermissionSettings().isProjectAccessible()))));
		assertThat(updatedResult, hasProperty("workStatus", samePropertyValuesAs(result.getWorkStatus())));
		assertThat(updatedResult, hasProperty("address",
			hasProperty("addressLine1", is(employee.getAddress().getAddressLine1()))));
		assertThat(updatedResult, hasProperty("address",
			hasProperty("addressLine2", is(employee.getAddress().getAddressLine2()))));
		assertThat(updatedResult, hasProperty("address",
			hasProperty("city", is(employee.getAddress().getCity()))));
		assertThat(updatedResult, hasProperty("address",
			hasProperty("state", is(employee.getAddress().getState()))));
		assertThat(updatedResult, hasProperty("address",
			hasProperty("postalCode", is(employee.getAddress().getPostalCode()))));
		assertThat(updatedResult, hasProperty("address",
			hasProperty("country", is(employee.getAddress().getCountry()))));
	}

	@Test
	public void putUserFromDifferentCompanyFail() throws Exception {
		User employee = newFirstEmployee();
		UserDTO employeeUpdate = make(a(UserMaker.UserDTO));

		String employeeJson = jackson.writer(filters).writeValueAsString(employeeUpdate);
		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + employee.getUserNumber())
				.content(employeeJson)
		).andExpect(status().isForbidden()).andReturn();
		
	}

	@Test
	public void putUserAsEmployeeWorkerFailRoleValidation() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO));
		postNewUserAndApprove(employee);
		RoleSettingsDTO roleSettingsDTO = make(a(RoleSettingsMaker.EmployeeWorkerRoleSettings, with(admin, true)));
		employee = make(a(UserMaker.UserDTO, with(roleSettings, new RoleSettingsDTO.Builder(roleSettingsDTO))));

		String employeeJson = jackson.writer(filters).writeValueAsString(employee);
		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError updatedResult = getFirstResult(mvcResult, errorType);
		assertThat(updatedResult, hasProperty("field", is("roleSettings")));
		assertThat(updatedResult, hasProperty("message", is(messageHelper.getMessage("user.validation.employeeWorkerNoOtherRolesAllowed"))));
	}

	@Test
	public void putUserAsEmployeeWorkerFailPermissionValidation() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO));
		postNewUserAndApprove(employee);
		RoleSettingsDTO roleSettingsDTO = make(a(RoleSettingsMaker.EmployeeWorkerRoleSettings));
		PermissionSettingsDTO permissionSettingsDTO = make(a(PermissionSettingsMaker.NoPermissionSettings, with(paymentAccessible, true)));
		employee = make(a(UserMaker.UserDTO,
			with(roleSettings, new RoleSettingsDTO.Builder(roleSettingsDTO)),
			with(permissionSettings, new PermissionSettingsDTO.Builder(permissionSettingsDTO))));

		String employeeJson = jackson.writer(filters).writeValueAsString(employee);
		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(employeeJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError updatedResult = getFirstResult(mvcResult, errorType);
		assertThat(updatedResult, hasProperty("field", is("permissionSettings")));
		assertThat(updatedResult, hasProperty("message", is(messageHelper.getMessage("user.validation.employeeWorkerNoCustomPermissionsAllowed"))));
	}

	@Test
	public void putUserAsEmployeeWorkerSuccess() throws Exception {
		UserDTO employee = make(a(UserMaker.UserDTO));
		UserDTO result = postNewUserAndApprove(employee);
		User newUser = userService.findUserByUserNumber(result.getUserNumber());
		RoleSettingsDTO roleSettingsDTO = make(a(RoleSettingsMaker.EmployeeWorkerRoleSettings));
		PermissionSettingsDTO permissionSettingsDTO = make(a(PermissionSettingsMaker.NoPermissionSettings));
		employee = make(a(UserMaker.UserDTO,
			with(id, result.getId()),
			with(userNumber, result.getUserNumber()),
			with(email, result.getEmail()),
			with(spendLimit, new BigDecimal("1500.00")),
			with(workPhone, "888-888-8888"),
			with(roleSettings, new RoleSettingsDTO.Builder(roleSettingsDTO)),
			with(permissionSettings, new PermissionSettingsDTO.Builder(permissionSettingsDTO))));
		UserDTO updatedResult = updateUser(result.getUserNumber(), employee);
		Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(newUser.getId());
		PersonaPreference personaPreference = personaPreferenceOptional.orNull();

		assertThat(updatedResult, hasProperty("userNumber", samePropertyValuesAs(result.getUserNumber())));
		assertThat(updatedResult, hasProperty("firstName", samePropertyValuesAs(result.getFirstName())));
		assertThat(updatedResult, hasProperty("lastName", samePropertyValuesAs(result.getLastName())));
		assertThat(updatedResult, hasProperty("email", samePropertyValuesAs(result.getEmail())));
		assertThat(updatedResult, hasProperty("workPhone", not(samePropertyValuesAs(result.getWorkPhone()))));
		assertThat(updatedResult, hasProperty("workPhoneExtension", samePropertyValuesAs(result.getWorkPhoneExtension())));
		assertThat(updatedResult, hasProperty("workPhoneInternationalCode", samePropertyValuesAs(result.getWorkPhoneInternationalCode())));
		assertThat(updatedResult, hasProperty("jobTitle", samePropertyValuesAs(result.getJobTitle())));
		assertThat(updatedResult, hasProperty("industryId", samePropertyValuesAs(result.getIndustryId())));
		assertThat(updatedResult, hasProperty("spendLimit", is(not(result.getSpendLimit()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("admin", is(not(result.getRoleSettings().isAdmin())))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("manager", is(not(result.getRoleSettings().isManager())))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("controller", is(result.getRoleSettings().isController()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("user", is(result.getRoleSettings().isUser()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("viewOnly", is(result.getRoleSettings().isViewOnly()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("staff", is(result.getRoleSettings().isStaff()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("deputy", is(result.getRoleSettings().isDeputy()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("dispatcher", is(result.getRoleSettings().isDispatcher()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("employeeWorker", is(employee.getRoleSettings().isEmployeeWorker()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("paymentAccessible", is(not(result.getPermissionSettings().isPaymentAccessible())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("fundsAccessible", is(not(result.getPermissionSettings().isFundsAccessible())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("counterOfferAccessible", is(not(result.getPermissionSettings().isCounterOfferAccessible())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("pricingEditable", is(not(result.getPermissionSettings().isPricingEditable())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("workApprovalAllowed", is(not(result.getPermissionSettings().isWorkApprovalAllowed())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("projectAccessible", is(not(result.getPermissionSettings().isProjectAccessible())))));
		assertThat(updatedResult, hasProperty("workStatus", samePropertyValuesAs(result.getWorkStatus())));
		assertThat(personaPreferenceOptional.isPresent(), is(true));
		assertThat(personaPreference.isSeller(), is(true));
		assertThat(personaPreference.isBuyer(), is(false));
	}

	@Test
	public void putUserAsNonEmployeeWorkerSuccess() throws Exception {
		RoleSettingsDTO roleSettingsDTO = make(a(RoleSettingsMaker.EmployeeWorkerRoleSettings));
		PermissionSettingsDTO permissionSettingsDTO = make(a(PermissionSettingsMaker.NoPermissionSettings));
		UserDTO employee = make(a(UserMaker.UserDTO,
			with(roleSettings, new RoleSettingsDTO.Builder(roleSettingsDTO)),
			with(permissionSettings, new PermissionSettingsDTO.Builder(permissionSettingsDTO))));
		UserDTO result = postNewUserAndApprove(employee);

		employee = make(a(UserMaker.UserDTO,
			with(id, result.getId()),
			with(userNumber, result.getUserNumber()),
			with(email, result.getEmail()),
			with(spendLimit, new BigDecimal("1500.00")),
			with(workPhone, "888-888-8888")));
		UserDTO updatedResult = updateUser(result.getUserNumber(), employee);
		User newUser = userService.findUserByUserNumber(result.getUserNumber());
		Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(newUser.getId());
		PersonaPreference personaPreference = personaPreferenceOptional.orNull();
		
		assertThat(updatedResult, hasProperty("userNumber", samePropertyValuesAs(result.getUserNumber())));
		assertThat(updatedResult, hasProperty("firstName", samePropertyValuesAs(result.getFirstName())));
		assertThat(updatedResult, hasProperty("lastName", samePropertyValuesAs(result.getLastName())));
		assertThat(updatedResult, hasProperty("email", samePropertyValuesAs(result.getEmail())));
		assertThat(updatedResult, hasProperty("workPhone", not(samePropertyValuesAs(result.getWorkPhone()))));
		assertThat(updatedResult, hasProperty("workPhoneExtension", samePropertyValuesAs(result.getWorkPhoneExtension())));
		assertThat(updatedResult, hasProperty("workPhoneInternationalCode", samePropertyValuesAs(result.getWorkPhoneInternationalCode())));
		assertThat(updatedResult, hasProperty("jobTitle", samePropertyValuesAs(result.getJobTitle())));
		assertThat(updatedResult, hasProperty("industryId", samePropertyValuesAs(result.getIndustryId())));
		assertThat(updatedResult, hasProperty("spendLimit", is(not(result.getSpendLimit()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("admin", is(not(result.getRoleSettings().isAdmin())))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("manager", is(not(result.getRoleSettings().isManager())))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("controller", is(result.getRoleSettings().isController()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("user", is(result.getRoleSettings().isUser()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("viewOnly", is(result.getRoleSettings().isViewOnly()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("staff", is(result.getRoleSettings().isStaff()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("deputy", is(result.getRoleSettings().isDeputy()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("dispatcher", is(result.getRoleSettings().isDispatcher()))));
		assertThat(updatedResult, hasProperty("roleSettings",
			hasProperty("employeeWorker", is(employee.getRoleSettings().isEmployeeWorker()))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("paymentAccessible", is(not(result.getPermissionSettings().isPaymentAccessible())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("fundsAccessible", is(not(result.getPermissionSettings().isFundsAccessible())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("counterOfferAccessible", is(not(result.getPermissionSettings().isCounterOfferAccessible())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("pricingEditable", is(not(result.getPermissionSettings().isPricingEditable())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("workApprovalAllowed", is(not(result.getPermissionSettings().isWorkApprovalAllowed())))));
		assertThat(updatedResult, hasProperty("permissionSettings",
			hasProperty("projectAccessible", is(not(result.getPermissionSettings().isProjectAccessible())))));
		assertThat(updatedResult, hasProperty("workStatus", samePropertyValuesAs(result.getWorkStatus())));
		assertThat(personaPreferenceOptional.isPresent(), is(true));
		assertThat(personaPreference.isSeller(), is(false));
		assertThat(personaPreference.isBuyer(), is(true));
	}


	@Test
	public void verifyUser() throws Exception {

		RoleSettingsDTO roleSettingsDTO = make(a(RoleSettingsMaker.DefaultRoleSettings, with(dispatcher, true)));
		UserDTO employee = make(
			a(UserMaker.UserDTO, with(roleSettings, new RoleSettingsDTO.Builder(roleSettingsDTO)))
		);
		UserDTO postResult = postNewUserAndApprove(employee);
		User newUser = userService.findUserByUserNumber(postResult.getUserNumber());
		authenticationService.setEmailConfirmed(newUser, true);
		newUser.setUserStatusType(UserStatusType.APPROVED_STATUS);
		userService.saveOrUpdateUser(newUser);

		// remove admin role from current user
		authenticationService.removeAclRoleFromUser(user.getId(), AclRole.ACL_ADMIN);
		userService.assignRole(user.getId(), RoleType.WM_ADMIN);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + postResult.getUserNumber() + "/verification")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> getResult = getFirstResult(mvcResult, mapType);
		assertThat(getResult, hasEntry("isLastDispatcher", String.valueOf(Boolean.TRUE)));
		assertThat(getResult, hasEntry("isLastAdmin", String.valueOf(Boolean.TRUE)));
	}

	private UserDTO postNewUserAndApprove(UserDTO userDTO) throws Exception {
		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(Collections.emptySet())
		);

		CreateUserDTO.Builder createUserDTObuilder = new CreateUserDTO.Builder(userDTO);

		String userDTOJson = jackson.writer(filters).writeValueAsString(createUserDTObuilder.build());
		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT)
				.content(userDTOJson)
		).andExpect(status().isOk()).andReturn();

		UserDTO result = getFirstResult(mvcResult, userResponseType).build();
		User user = userService.findUserByUserNumber(result.getUserNumber());
		authenticationService.setEmailConfirmed(user, true);
		user.setUserStatusType(UserStatusType.APPROVED_STATUS);
		userService.saveOrUpdateUser(user);

		return result;
	}

	private UserDTO updateUser(String userNumber, UserDTO userDTO) throws Exception {
		String userDTOJson = jackson.writer(filters).writeValueAsString(userDTO);
		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + userNumber)
				.content(userDTOJson)
		).andExpect(status().isOk()).andReturn();
		return getFirstResult(mvcResult, userResponseType).build();
	}

	private String getRoleNamesStringByUserId(Long userId) {
		List<AclRole> aclRoles = authenticationService.findAllAssignedAclRolesByUser(userId);
		List<String> aclRoleNames = extract(aclRoles, on(AclRole.class).getName());
		return join(aclRoleNames,", ");
	}
}
