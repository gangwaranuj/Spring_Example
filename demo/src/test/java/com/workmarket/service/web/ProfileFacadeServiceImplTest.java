package com.workmarket.service.web;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.velvetrope.doorman.ESignatureDoorman;
import com.workmarket.service.esignature.EsignatureService;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.domains.model.skill.UserSkillAssociationPagination;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociationPagination;
import com.workmarket.domains.model.tool.UserToolAssociationPagination;
import com.workmarket.service.admin.DefaultBackgroundImageService;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CertificationService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.InsuranceService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.LicenseService;
import com.workmarket.service.business.LinkedInService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.SpecialtyService;
import com.workmarket.service.business.ToolService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.service.network.NetworkService;
import com.workmarket.web.facade.ProfileFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfileFacadeServiceImplTest {

	@Mock AuthenticationService authenticationService;
	@Mock AuthorizationService authorizationService;
	@Mock EsignatureService esignatureService;
	@Mock ESignatureDoorman eSignatureDoorman;
	@Mock LaneService laneService;
	@Mock UserService userService;
	@Mock NetworkService networkService;
	@Mock ProfileService profileService;
	@Mock UserAssetAssociationDAO userAssetAssociationDAO;
	@Mock AddressService addressService;
	@Mock CompanyService companyService;
	@Mock DefaultBackgroundImageService defaultBackgroundImageService;
	@Mock TaxService taxService;
	@Mock UserGroupService groupService;
	@Mock IndustryService industryService;
	@Mock RatingService ratingService;
	@Mock SkillService skillService;
	@Mock ToolService toolService;
	@Mock SpecialtyService specialtyService;
	@Mock ScreeningService screeningService;
	@Mock CertificationService certificationService;
	@Mock LicenseService licenseService;
	@Mock InsuranceService insuranceService;
	@Mock AssessmentService assessmentService;
	@Mock LinkedInService linkedInService;
	@Mock ManagedCompanyUserGroupRowPagination managedCompanyUserGroupRowPagination;
	@Mock ManagedCompanyUserGroupRow managedCompanyUserGroupRow;
	@Mock UserRoleService userRoleService;
	@InjectMocks ProfileFacadeServiceImpl facadeService;

	private static final String
		USER_NUMBER = "1234",
		JOB_TITLE = "JOB_TITLE",
		OVERVIEW = "OVERVIEW",
		ZIP_CODE = "ZIP_CODE",
		WORK_PHONE = "WORK_PHONE",
		WORK_PHONE_EXT = "WORK_PHONE_EXT",
		WORK_PHONE_INT = "WORK_PHONE_INT",
		MOBILE = "MOBILE",
		MOBILE_INT = "MOBILE_INT",
		SHARED_GROUP_NAME = "groupName",
		FIRST_NAME = "FNAME",
		LAST_NAME = "LNAME",
		EMAIL = "email",
		CEMAIL = "cemail",
		COMPANY_EFFECTIVE_NAME = "effectiveName",
		COMPANY_OVERVIEW = "company overview",
		COMPANY_WEBSITE = "website";
	private static final String USER_UUID = "667a44d5-8c28-497c-92b9-9bea3c3aff3c";
	private static final String CURRENT_USER_COMPANY_UUID = "29d2063b-eef5-4435-b0f5-e6958ce0d2c1";
	private static Boolean EMAIL_CONFIRMED = Boolean.TRUE;
	private static final Long
		USER_ID = 1L,
		USER_COMPANY_ID = 2L,
		CURRENT_USER_COMPANY_ID = 3L,
		PROFILE_ID = 4L,
		ADDRESS_ID = 5L,
		SHARED_GROUP_ID = 6L,
		CURRENT_USER_ID = 7L;
	private static final BigDecimal
		MAXTRAVELDISTANCE = BigDecimal.ONE,
		MINONSITEHOURLYRATE = BigDecimal.TEN,
		MINONSITEWORKPRICE = BigDecimal.ZERO,
		MINOFFSITEHOURLYRATE = BigDecimal.valueOf(2),
		MINOFFSITEWORKPRICE = BigDecimal.valueOf(3);
	private List<String> BLACKLISTED_POSTAL_CODES = Lists.newArrayList("13245");
	private User user, currentUser;
	private ExtendedUserDetails extendedUserDetails;
	private Company userCompany, currentUserCompany;
	private ProfileDTO profileDTO;
	private Profile userProfile;

	@Before
	public void setUp() {
		user = mock(User.class);
		userCompany = mock(Company.class);
		userProfile = mock(Profile.class);
		extendedUserDetails = mock(ExtendedUserDetails.class);

		when(user.getId()).thenReturn(USER_ID);
		when(user.getCompany()).thenReturn(userCompany);
		when(user.getProfile()).thenReturn(userProfile);
		when(authenticationService.getUserStatus(user)).thenReturn(UserStatusType.APPROVED_STATUS);
		when(userCompany.getId()).thenReturn(USER_COMPANY_ID);
		when(userProfile.getJobTitle()).thenReturn(JOB_TITLE);
		when(userProfile.getOverview()).thenReturn(OVERVIEW);

		when(user.getUuid()).thenReturn(USER_UUID);
		when(user.getUserNumber()).thenReturn(USER_NUMBER);
		when(user.getFirstName()).thenReturn(FIRST_NAME);
		when(user.getLastName()).thenReturn(LAST_NAME);
		when(user.getEmail()).thenReturn(EMAIL);
		when(user.getChangedEmail()).thenReturn(CEMAIL);
		when(authenticationService.getEmailConfirmed(user)).thenReturn(EMAIL_CONFIRMED);
		when(user.getCreatedOn()).thenReturn(Calendar.getInstance());
		when(authenticationService.getUserStatus(user)).thenReturn(UserStatusType.APPROVED_STATUS);

		when(userCompany.getEffectiveName()).thenReturn(COMPANY_EFFECTIVE_NAME);
		when(userCompany.getOverview()).thenReturn(COMPANY_OVERVIEW);
		when(userCompany.getWebsite()).thenReturn(COMPANY_WEBSITE);

		when(userService.findUserByUserNumber(USER_NUMBER)).thenReturn(user);
		when(userService.findAllUsersByIds(Lists.newArrayList(USER_ID))).thenReturn(Lists.newArrayList(user));
		when(userService.findAllUsersWithProfileAndCompanyByIds(Lists.newArrayList(USER_ID))).thenReturn(Lists.<User>newArrayList(user));
		when(userService.isUserBlockedByCompany(anyLong(), anyLong(), anyLong())).thenReturn(Boolean.FALSE);


		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.OWNER);

		currentUser = mock(User.class);
		currentUserCompany = mock(Company.class);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(authenticationService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getCompany()).thenReturn(currentUserCompany);
		when(currentUserCompany.getId()).thenReturn(CURRENT_USER_COMPANY_ID);
		when(currentUserCompany.getUuid()).thenReturn(CURRENT_USER_COMPANY_UUID);
		when(userService.getUser(CURRENT_USER_ID)).thenReturn(currentUser);

		profileDTO = mock(ProfileDTO.class);
		setUpProfileDTO();
		when(profileService.findProfileDTO(USER_ID)).thenReturn(profileDTO);

		when(addressService.findById(ADDRESS_ID)).thenReturn(mock(Address.class));
		when(industryService.getIndustryDTOsForProfile(PROFILE_ID)).thenReturn(Sets.newHashSet(mock(IndustryDTO.class)));
		when(defaultBackgroundImageService.getCurrentDefaultBackgroundImage()).thenReturn(mock(Optional.class));
		when(groupService.findAllGroupsByCompanyId(eq(CURRENT_USER_COMPANY_ID), any(UserGroupPagination.class)))
			.thenReturn(mock(UserGroupPagination.class));
		when(industryService.getIndustryDTOsForProfile(PROFILE_ID)).thenReturn(Sets.<IndustryDTO>newHashSet());
		when(profileService.findCompany(USER_ID)).thenReturn(userCompany);
		when(skillService.findAllAssociationsByUser(eq(USER_ID), any(UserSkillAssociationPagination.class)))
			.thenReturn(mock(UserSkillAssociationPagination.class));
		when(toolService.findAllAssociationsByUser(eq(USER_ID), any(UserToolAssociationPagination.class)))
			.thenReturn(mock(UserToolAssociationPagination.class));
		when(specialtyService.findAllAssociationsByUser(eq(USER_ID), any(UserSpecialtyAssociationPagination.class)))
			.thenReturn(mock(UserSpecialtyAssociationPagination.class));
		when(certificationService.findAllAssociationsByUserId(eq(USER_ID), any(UserCertificationAssociationPagination.class)))
			.thenReturn(mock(UserCertificationAssociationPagination.class));
		when(licenseService.findAllAssociationsByUserId(eq(USER_ID), any(UserLicenseAssociationPagination.class)))
			.thenReturn(mock(UserLicenseAssociationPagination.class));
		when(insuranceService.findAllUserInsuranceAssociationsByUserId(eq(USER_ID), any(UserInsuranceAssociationPagination.class)))
			.thenReturn(mock(UserInsuranceAssociationPagination.class));
		when(groupService.findMyGroupMemberships(eq(USER_ID), any(ManagedCompanyUserGroupRowPagination.class)))
			.thenReturn(managedCompanyUserGroupRowPagination);
		when(groupService.findMyGroupMemberships(any(User.class), any(ManagedCompanyUserGroupRowPagination.class)))
			.thenReturn(managedCompanyUserGroupRowPagination);
		when(managedCompanyUserGroupRowPagination.getResults()).thenReturn(Lists.newArrayList(managedCompanyUserGroupRow));
		when(managedCompanyUserGroupRow.getGroupId()).thenReturn(SHARED_GROUP_ID);
		when(managedCompanyUserGroupRow.getName()).thenReturn(SHARED_GROUP_NAME);
		when(managedCompanyUserGroupRow.isSharedWithMe(CURRENT_USER_COMPANY_ID)).thenReturn(true);
		when(managedCompanyUserGroupRow.isSharedByMe(CURRENT_USER_COMPANY_ID)).thenReturn(false);
	}

	@Test
	public void findProfileFacade_copyDataFromProfile() throws Exception {
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		assertEquals(facade.getJobTitle(), profileDTO.getJobTitle());
		assertEquals(facade.getOverview(), profileDTO.getOverview());
		assertEquals(facade.getZipCode(), profileDTO.getPostalCode());
		assertEquals(facade.getMaxTravelDistance(), profileDTO.getMaxTravelDistance().doubleValue(), 0);
		assertEquals(facade.getMinOnsiteHourlyRate(), profileDTO.getMinOnsiteHourlyRate().doubleValue(), 0);
		assertEquals(facade.getMinOnsiteWorkPrice(), profileDTO.getMinOnsiteWorkPrice().doubleValue(), 0);
		assertEquals(facade.getMinOffsiteHourlyRate(), profileDTO.getMinOffsiteHourlyRate().doubleValue(), 0);
		assertEquals(facade.getMinOffsiteWorkPrice(), profileDTO.getMinOffsiteWorkPrice().doubleValue(), 0);
		assertEquals(facade.getBlacklistedPostalCodes(), profileDTO.getBlacklistedPostalCodes());
		assertEquals(facade.getWorkPhone(), profileDTO.getWorkPhone());
		assertEquals(facade.getWorkPhoneExtension(), profileDTO.getWorkPhoneExtension());
		assertEquals(facade.getWorkPhoneInternationalCode(), profileDTO.getWorkPhoneInternationalCallingCodeId());
		assertEquals(facade.getMobilePhone(), profileDTO.getMobilePhone());
		assertEquals(facade.getMobilePhoneInternationalCode(), profileDTO.getMobilePhoneInternationalCallingCodeId());
		assertNotNull(facade.getAddress());
		assertNotNull(facade.getIndustries());
	}

	@Test
	public void isCurrentUserAuthorizedToSeeProfile_userIsOwner() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.OWNER);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		assertEquals(true, facadeService.isCurrentUserAuthorizedToSeeProfile(extendedUserDetails, facade));
	}

	@Test
	public void isCurrentUserAuthorizedToSeeProfile_userIsInternal() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.PUBLIC);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(true);
		assertEquals(true, facadeService.isCurrentUserAuthorizedToSeeProfile(extendedUserDetails, facade));
	}


	@Test
	public void isCurrentUserAuthorizedToSeeProfile_userIsBlockedByCompany() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.PUBLIC);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		when(userService.isUserBlockedForCompany(any(Long.class),any(Long.class),any(Long.class))).thenReturn(true);
		assertEquals(false, facadeService.isCurrentUserAuthorizedToSeeProfile(extendedUserDetails, facade));
	}

	@Test
	public void isCurrentUserAuthorizedToSeeProfile_profileIsAvailableToSharing() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.PUBLIC);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		facade.setLaneType(null);
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		when(userService.isUserBlockedForCompany(any(Long.class),any(Long.class),any(Long.class))).thenReturn(false);
		when(networkService.isProfileViewableViaGroupSharing(any(Long.class),any(Long.class))).thenReturn(true);
		assertEquals(true, facadeService.isCurrentUserAuthorizedToSeeProfile(extendedUserDetails, facade));
	}

	@Test
	public void isCurrentUserAuthorizedToSeeProfile_profileIsNotAvailableToSharing() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.PUBLIC);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		facade.setLaneType(null);
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		when(userService.isUserBlockedForCompany(any(Long.class),any(Long.class),any(Long.class))).thenReturn(false);
		when(networkService.isProfileViewableViaGroupSharing(any(Long.class),any(Long.class))).thenReturn(false);
		assertEquals(false, facadeService.isCurrentUserAuthorizedToSeeProfile(extendedUserDetails, facade));
	}

	@Test
	public void isCurrentUserAuthorizedToSeeProfile_userIsAdmin() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.ADMIN);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		facade.setLaneType(null);
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		when(userService.isUserBlockedForCompany(any(Long.class),any(Long.class),any(Long.class))).thenReturn(false);
		when(networkService.isProfileViewableViaGroupSharing(any(Long.class),any(Long.class))).thenReturn(false);
		assertEquals(true, facadeService.isCurrentUserAuthorizedToSeeProfile(extendedUserDetails, facade));
	}

	@Test
	public void isCurrentUserAuthorizedToSeeProfile_laneTypeNotNull() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.ADMIN);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		facade.setLaneType(1);
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		when(userService.isUserBlockedForCompany(any(Long.class),any(Long.class),any(Long.class))).thenReturn(false);
		when(networkService.isProfileViewableViaGroupSharing(any(Long.class),any(Long.class))).thenReturn(false);
		assertEquals(true, facadeService.isCurrentUserAuthorizedToSeeProfile(extendedUserDetails, facade));
	}

	@Test
	public void isCurrentUserAuthorizedToSeeProfile_invisibleProfile() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.PUBLIC);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		when(userService.isUserBlockedForCompany(any(Long.class),any(Long.class),any(Long.class))).thenReturn(false);
		when(networkService.isProfileViewableViaGroupSharing(any(Long.class),any(Long.class))).thenReturn(false);
		assertEquals(false, facadeService.isCurrentUserAuthorizedToSeeProfile(extendedUserDetails, facade));
	}

	@Test
	public void isCurrentUserAuthorizedToEditProfile_userIsOwner() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.OWNER);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		assertEquals(true, facadeService.isCurrentUserAuthorizedToEditProfile(extendedUserDetails, facade));
	}

	@Test
	public void isCurrentUserAuthorizedToEditProfile_userIsInternal() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.PUBLIC);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(true);
		assertEquals(true, facadeService.isCurrentUserAuthorizedToEditProfile(extendedUserDetails, facade));
	}


	@Test
	public void isCurrentUserAuthorizedToEditProfile_userNumberMatchesProfile() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.PUBLIC);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		when(extendedUserDetails.getUserNumber()).thenReturn("abc");
		facade.setUserNumber("abc");
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		assertEquals(true, facadeService.isCurrentUserAuthorizedToEditProfile(extendedUserDetails, facade));
	}

	@Test
	public void isCurrentUserAuthorizedToEditProfile_userNumberDoesntMatchProfile() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.PUBLIC);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		when(extendedUserDetails.getUserNumber()).thenReturn("abc123");
		facade.setUserNumber("abc");
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		assertEquals(false, facadeService.isCurrentUserAuthorizedToEditProfile(extendedUserDetails, facade));
	}

	@Test
	public void isCurrentUserAuthorizedToEditProfile_userIsAdmin() throws Exception {
		when(authorizationService.getRequestContext(USER_ID)).thenReturn(RequestContext.ADMIN);
		ProfileFacade facade = facadeService.findProfileFacadeByUserNumber(USER_NUMBER);
		when(extendedUserDetails.getUserNumber()).thenReturn("abc123");
		facade.setUserNumber("abc");
		when(extendedUserDetails.hasAnyRoles(any(String.class))).thenReturn(false);
		assertEquals(true, facadeService.isCurrentUserAuthorizedToEditProfile(extendedUserDetails, facade));
	}


	@Test
	public void findSearchCardProfileFacade() throws Exception {
		List<ProfileFacade> facades = facadeService.findSearchCardProfileFacadeByUserIds(Lists.newArrayList(USER_ID), currentUser.getId());
		assertEquals(1, facades.size());

		ProfileFacade facade = facades.get(0);

		/*

		Long addressId = profile.getAddressId();

		facade.setCompanyId(company.getId());
		facade.setCompanyName(company.getEffectiveName());
		facade.setCompanyOverview(company.getOverview());
		facade.setCompanyWebsite(company.getWebsite());
		*/

		assertEquals(facade.getId(), user.getId());
		assertEquals(facade.getUserNumber(), user.getUserNumber());
		assertEquals(facade.getFirstName(), user.getFirstName());
		assertEquals(facade.getLastName(), user.getLastName());
		assertEquals(facade.getEmail(), user.getEmail());
		assertEquals(facade.getChangedEmail(), user.getChangedEmail());
		assertEquals(facade.getEmailConfirmed(), authenticationService.getEmailConfirmed(user));
		assertEquals(facade.getCreatedOn(), user.getCreatedOn());
		assertEquals(facade.getUserStatusType(), authenticationService.getUserStatus(user).getCode());
		assertEquals(facade.getJobTitle(), userProfile.getJobTitle());
		assertEquals(facade.getOverview(), userProfile.getOverview());
	}

	private void setUpProfileDTO() {
		when(profileDTO.getProfileId()).thenReturn(PROFILE_ID);
		when(profileDTO.getJobTitle()).thenReturn(JOB_TITLE);
		when(profileDTO.getOverview()).thenReturn(OVERVIEW);
		when(profileDTO.getPostalCode()).thenReturn(ZIP_CODE);
		when(profileDTO.getMaxTravelDistance()).thenReturn(MAXTRAVELDISTANCE);
		when(profileDTO.getMinOnsiteHourlyRate()).thenReturn(MINONSITEHOURLYRATE);
		when(profileDTO.getMinOnsiteWorkPrice()).thenReturn(MINONSITEWORKPRICE);
		when(profileDTO.getMinOffsiteHourlyRate()).thenReturn(MINOFFSITEHOURLYRATE);
		when(profileDTO.getMinOffsiteWorkPrice()).thenReturn(MINOFFSITEWORKPRICE);
		when(profileDTO.getBlacklistedPostalCodes()).thenReturn(BLACKLISTED_POSTAL_CODES);
		when(profileDTO.getWorkPhone()).thenReturn(WORK_PHONE);
		when(profileDTO.getWorkPhoneExtension()).thenReturn(WORK_PHONE_EXT);
		when(profileDTO.getWorkPhoneInternationalCallingCodeId()).thenReturn(WORK_PHONE_INT);
		when(profileDTO.getMobilePhone()).thenReturn(MOBILE);
		when(profileDTO.getMobilePhoneInternationalCallingCodeId()).thenReturn(MOBILE_INT);
		when(profileDTO.getAddressId()).thenReturn(ADDRESS_ID);
	}
}
