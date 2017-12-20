package com.workmarket.api.v2.employer.settings.controllers.support;


import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.settings.models.PermissionSettingsDTO;
import com.workmarket.api.v2.employer.settings.models.RoleSettingsDTO;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import com.workmarket.api.v2.employer.support.NullDonor;
import com.workmarket.api.v2.model.AddressApiDTO;
import com.workmarket.domains.model.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;

public class UserMaker {

	public static final Property<UserDTO, Long> id = newProperty();
	public static final Property<UserDTO, String> userNumber = newProperty();
	public static final Property<UserDTO, String> firstName = newProperty();
	public static final Property<UserDTO, String> lastName = newProperty();
	public static final Property<UserDTO, String> email = newProperty();
	public static final Property<UserDTO, String> workPhone = newProperty();
	public static final Property<UserDTO, String> workPhoneExtension = newProperty();
	public static final Property<UserDTO, String> workPhoneInternationalCode = newProperty();
	public static final Property<UserDTO, String> jobTitle = newProperty();
	public static final Property<UserDTO, Long> industryId = newProperty();
	public static final Property<UserDTO, BigDecimal> spendLimit = newProperty();
	public static final Property<UserDTO, RoleSettingsDTO.Builder> roleSettings = newProperty();
	public static final Property<UserDTO, PermissionSettingsDTO.Builder> permissionSettings = newProperty();
	public static final Property<UserDTO, String> workStatus = newProperty();
	public static final Property<UserDTO, AddressApiDTO.Builder> address = newProperty();

	private static final String FIRST_NAME = "First Name";
	private static final String LAST_NAME = "Last Name";
	private static final String JOB_TITLE = "Job Title";
	private static final String WORK_PHONE = "999-999-9999";
	private static final String WORK_PHONE_EXT = "1234";
	private static final String WORK_PHONE_INTL_CODE = "609";
	private static final String SPEND_LIMIT = "1000.00";

	public static final Instantiator<UserDTO> UserDTO = new Instantiator<UserDTO>() {
		@Override
		public UserDTO instantiate(PropertyLookup<UserDTO> lookup) {
			return new UserDTO.Builder()
				.setId(lookup.valueOf(id, new NullDonor<Long>()))
				.setUserNumber(lookup.valueOf(userNumber, new NullDonor<String>()))
				.setFirstName(lookup.valueOf(firstName, FIRST_NAME))
				.setLastName(lookup.valueOf(lastName, LAST_NAME))
				.setEmail(lookup.valueOf(email, RandomStringUtils.randomAlphanumeric(20).concat("@domain.com")))
				.setJobTitle(lookup.valueOf(jobTitle, JOB_TITLE))
				.setWorkPhone(lookup.valueOf(workPhone, WORK_PHONE))
				.setWorkPhoneExtension(lookup.valueOf(workPhoneExtension, WORK_PHONE_EXT))
				.setWorkPhoneInternationalCode(lookup.valueOf(workPhoneInternationalCode, WORK_PHONE_INTL_CODE))
				.setSpendLimit(lookup.valueOf(spendLimit, new BigDecimal(SPEND_LIMIT)))
				.setRoleSettings(lookup.valueOf(roleSettings,
					new RoleSettingsDTO.Builder(make(a(RoleSettingsMaker.DefaultRoleSettings)))))
				.setPermissionSettings(lookup.valueOf(permissionSettings,
					new PermissionSettingsDTO.Builder(make(a(PermissionSettingsMaker.DefaultPermissionSettings)))))
				.setIndustryId(lookup.valueOf(industryId, 1L))
				.setWorkStatus(lookup.valueOf(workStatus, User.WorkStatus.PUBLIC.name()))
				.build();
		}
	};

	public static final Instantiator<UserDTO> UserDTOWithNoRole = new Instantiator<UserDTO>() {
		@Override
		public UserDTO instantiate(PropertyLookup<UserDTO> lookup) {
			return new UserDTO.Builder()
				.setId(lookup.valueOf(id, new NullDonor<Long>()))
				.setUserNumber(lookup.valueOf(userNumber, new NullDonor<String>()))
				.setFirstName(lookup.valueOf(firstName, FIRST_NAME))
				.setLastName(lookup.valueOf(lastName, LAST_NAME))
				.setEmail(lookup.valueOf(email, RandomStringUtils.randomAlphanumeric(20).concat("@domain.com")))
				.setJobTitle(lookup.valueOf(jobTitle, JOB_TITLE))
				.setWorkPhone(lookup.valueOf(workPhone, WORK_PHONE))
				.setWorkPhoneExtension(lookup.valueOf(workPhoneExtension, WORK_PHONE_EXT))
				.setSpendLimit(lookup.valueOf(spendLimit, new BigDecimal(SPEND_LIMIT)))
				.setWorkPhoneInternationalCode(lookup.valueOf(workPhoneInternationalCode, WORK_PHONE_INTL_CODE))
				.setRoleSettings(lookup.valueOf(roleSettings,
					new RoleSettingsDTO.Builder(make(a(RoleSettingsMaker.EmptyRoleSettings)))))
				.setPermissionSettings(lookup.valueOf(permissionSettings,
					new PermissionSettingsDTO.Builder(make(a(PermissionSettingsMaker.DefaultPermissionSettings)))))
				.setIndustryId(lookup.valueOf(industryId, 1000L))
				.setWorkStatus(lookup.valueOf(workStatus, User.WorkStatus.PUBLIC.name()))
				.build();
		}
	};

	public static final Instantiator<UserDTO> UserDTOWithEmployeeWorkerRole = new Instantiator<UserDTO>() {
		@Override
		public UserDTO instantiate(PropertyLookup<UserDTO> lookup) {
			return new UserDTO.Builder()
				.setId(lookup.valueOf(id, new NullDonor<Long>()))
				.setUserNumber(lookup.valueOf(userNumber, new NullDonor<String>()))
				.setFirstName(lookup.valueOf(firstName, FIRST_NAME))
				.setLastName(lookup.valueOf(lastName, LAST_NAME))
				.setEmail(lookup.valueOf(email, RandomStringUtils.randomAlphanumeric(20).concat("@domain.com")))
				.setJobTitle(lookup.valueOf(jobTitle, JOB_TITLE))
				.setWorkPhone(lookup.valueOf(workPhone, WORK_PHONE))
				.setWorkPhoneExtension(lookup.valueOf(workPhoneExtension, WORK_PHONE_EXT))
				.setSpendLimit(lookup.valueOf(spendLimit, new BigDecimal(SPEND_LIMIT)))
				.setWorkPhoneInternationalCode(lookup.valueOf(workPhoneInternationalCode, WORK_PHONE_INTL_CODE))
				.setRoleSettings(lookup.valueOf(roleSettings,
					new RoleSettingsDTO.Builder(make(a(RoleSettingsMaker.EmployeeWorkerRoleSettings)))))
				.setPermissionSettings(lookup.valueOf(permissionSettings,
					new PermissionSettingsDTO.Builder(make(a(PermissionSettingsMaker.NoPermissionSettings)))))
				.setIndustryId(lookup.valueOf(industryId, 1000L))
				.setWorkStatus(lookup.valueOf(workStatus, User.WorkStatus.PUBLIC.name()))
				.build();
		}
	};

	public static final Instantiator<UserDTO> UserDTOWithAddress = new Instantiator<UserDTO>() {
		@Override
		public UserDTO instantiate(PropertyLookup<UserDTO> lookup) {
			return new UserDTO.Builder()
				.setId(lookup.valueOf(id, new NullDonor<Long>()))
				.setUserNumber(lookup.valueOf(userNumber, new NullDonor<String>()))
				.setFirstName(lookup.valueOf(firstName, FIRST_NAME))
				.setLastName(lookup.valueOf(lastName, LAST_NAME))
				.setEmail(lookup.valueOf(email, RandomStringUtils.randomAlphanumeric(20).concat("@domain.com")))
				.setJobTitle(lookup.valueOf(jobTitle, JOB_TITLE))
				.setWorkPhone(lookup.valueOf(workPhone, WORK_PHONE))
				.setWorkPhoneExtension(lookup.valueOf(workPhoneExtension, WORK_PHONE_EXT))
				.setSpendLimit(lookup.valueOf(spendLimit, new BigDecimal(SPEND_LIMIT)))
				.setWorkPhoneInternationalCode(lookup.valueOf(workPhoneInternationalCode, WORK_PHONE_INTL_CODE))
				.setRoleSettings(lookup.valueOf(roleSettings,
					new RoleSettingsDTO.Builder(make(a(RoleSettingsMaker.EmployeeWorkerRoleSettings)))))
				.setPermissionSettings(lookup.valueOf(permissionSettings,
					new PermissionSettingsDTO.Builder(make(a(PermissionSettingsMaker.NoPermissionSettings)))))
				.setIndustryId(lookup.valueOf(industryId, 1000L))
				.setWorkStatus(lookup.valueOf(workStatus, User.WorkStatus.PUBLIC.name()))
				.setAddress(lookup.valueOf(address, new AddressApiDTO.Builder()
					.setAddressLine1("240 W 37th St")
					.setAddressLine2("9th Fl")
					.setCity("New York")
					.setPostalCode("10018")
					.setState("NY")
					.setCountry("USA")
					.setAddressTypeCode("profile"))
				).build();
		}
	};

	public static final Instantiator<UserDTO> UserDTOWithInvlalidAddress = new Instantiator<UserDTO>() {
		@Override
		public UserDTO instantiate(PropertyLookup<UserDTO> lookup) {
			return new UserDTO.Builder()
				.setId(lookup.valueOf(id, new NullDonor<Long>()))
				.setUserNumber(lookup.valueOf(userNumber, new NullDonor<String>()))
				.setFirstName(lookup.valueOf(firstName, FIRST_NAME))
				.setLastName(lookup.valueOf(lastName, LAST_NAME))
				.setEmail(lookup.valueOf(email, RandomStringUtils.randomAlphanumeric(20).concat("@domain.com")))
				.setJobTitle(lookup.valueOf(jobTitle, JOB_TITLE))
				.setWorkPhone(lookup.valueOf(workPhone, WORK_PHONE))
				.setWorkPhoneExtension(lookup.valueOf(workPhoneExtension, WORK_PHONE_EXT))
				.setSpendLimit(lookup.valueOf(spendLimit, new BigDecimal(SPEND_LIMIT)))
				.setWorkPhoneInternationalCode(lookup.valueOf(workPhoneInternationalCode, WORK_PHONE_INTL_CODE))
				.setRoleSettings(lookup.valueOf(roleSettings,
					new RoleSettingsDTO.Builder(make(a(RoleSettingsMaker.EmployeeWorkerRoleSettings)))))
				.setPermissionSettings(lookup.valueOf(permissionSettings,
					new PermissionSettingsDTO.Builder(make(a(PermissionSettingsMaker.NoPermissionSettings)))))
				.setIndustryId(lookup.valueOf(industryId, 1000L))
				.setWorkStatus(lookup.valueOf(workStatus, User.WorkStatus.PUBLIC.name()))
				.setAddress(new AddressApiDTO.Builder()
					.setAddressLine2("invalid")
					.setCountry("invalid")
					.setAddressTypeCode("profile"))
				.build();
		}
	};
}
