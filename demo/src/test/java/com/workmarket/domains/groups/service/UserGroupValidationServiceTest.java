package com.workmarket.domains.groups.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.groups.service.UserGroupAssociationValidationUpdateServiceImpl.AssociationUpdateType;
import com.workmarket.domains.groups.service.association.UserGroupAssociationService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CertificationService;
import com.workmarket.service.business.InsuranceService;
import com.workmarket.service.business.LicenseService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.summary.SummaryService;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserGroupValidationServiceTest {
	@Mock private AssessmentService assessmentService;
	@Mock private UserGroupService userGroupService;
	@Mock private GroupSearchService groupSearchService;
	@Mock private CertificationService certificationService;
	@Mock private RequestService requestService;
	@Mock private EventRouter eventRouter;
	@Mock private EventFactory eventFactory;
	@Mock private LicenseService licenseService;
	@Mock private InsuranceService insuranceService;
	@Mock private InvariantDataService invariantDataService;
	@Mock private UserIndexer userIndexer;
	@Mock private SummaryService summaryService;
	@Mock private UserGroupAssociationValidationUpdateService userGroupAssociationValidationUpdateService;
	@Mock private UserGroupAssociationService userGroupAssociationService;
	@Mock private ExtendedUserDetailsService extendedUserDetailsService;
	@Mock private Eligibility eligibility;
	@InjectMocks private UserGroupValidationServiceImpl userGroupValidationService;

	User user;
	Company company;

	UserGroup userGroup;
	List<Long> userIdsInGroup = Lists.newArrayList();
	UserInsuranceAssociationPagination userInsuranceAssociationPagination;

	UserCertificationAssociationPagination userCertificationAssociationPagination;
	UserLicenseAssociationPagination userLicenseAssociationPagination;
	UserUserGroupAssociation userUserGroupAssociation;

	Calendar yesterday = Calendar.getInstance();
	Calendar tomorrow = Calendar.getInstance();

	ExtendedUserDetails userDetails;

	private static final long ASSOCIATION_ID = 5L;
	private static final long GROUP_ID = 2L;
	private static final long USER_ID = 1L;
	private static final long MODIFICATION_ID = 10L;

	@Before
	public void before() {
		yesterday.add(Calendar.DATE, -1);
		tomorrow.add(Calendar.DATE, 1);

		user = mock(User.class);
		company = mock(Company.class);
		userGroup = mock(UserGroup.class);
		userUserGroupAssociation = mock(UserUserGroupAssociation.class);
		userDetails = mock(ExtendedUserDetails.class);

		when(user.getId()).thenReturn(4L);
		when(user.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(5L);

		when(userGroup.getId()).thenReturn(GROUP_ID);
		when(userGroup.getCreatorId()).thenReturn(4L);
		when(userGroup.getOwner()).thenReturn(user);

		when(userUserGroupAssociation.getId()).thenReturn(ASSOCIATION_ID);
		when(userUserGroupAssociation.getUserGroup()).thenReturn(userGroup);
		when(userUserGroupAssociation.getUser()).thenReturn(user);
		when(userUserGroupAssociation.getVerificationStatus()).thenReturn(VerificationStatus.VERIFIED);
		when(userUserGroupAssociation.getApprovalStatus()).thenReturn(ApprovalStatus.APPROVED);

		when(userGroupService.findAllUserIdsOfGroup(GROUP_ID)).thenReturn(userIdsInGroup);

		Insurance insurance = mock(Insurance.class);
		when(insurance.getId()).thenReturn(10L);

		Certification certification = mock(Certification.class);
		when(certification.getId()).thenReturn(20L);

		License license = mock(License.class);
		when(license.getId()).thenReturn(20L);

		userInsuranceAssociationPagination = mock(UserInsuranceAssociationPagination.class);
		List<UserInsuranceAssociation> insuranceAssociations = Lists.newArrayList();
		when(userInsuranceAssociationPagination.getResults()).thenReturn(insuranceAssociations);

		userCertificationAssociationPagination = mock(UserCertificationAssociationPagination.class);
		List<UserCertificationAssociation> certificationAssociations = Lists.newArrayList();
		when(userCertificationAssociationPagination.getResults()).thenReturn(certificationAssociations);

		userLicenseAssociationPagination = mock(UserLicenseAssociationPagination.class);
		List<UserLicenseAssociation> licenseAssociations = Lists.newArrayList();
		when(userLicenseAssociationPagination.getResults()).thenReturn(licenseAssociations);

		when(insuranceService.findAllAssociationsByUserIdInList(eq(2L), anyList(), any(UserInsuranceAssociationPagination.class))).
			thenReturn(userInsuranceAssociationPagination);
		when(certificationService.findAllAssociationsByUserIdInList(eq(2L), anyList(), any(UserCertificationAssociationPagination.class))).
			thenReturn(userCertificationAssociationPagination);
		when(licenseService.findAllAssociationsByUserIdInList(eq(2L), anyList(), any(UserLicenseAssociationPagination.class))).
			thenReturn(userLicenseAssociationPagination);

		when(extendedUserDetailsService.loadUserByEmail(anyString(), any(ExtendedUserDetailsOptionsService.OPTION[].class))).thenReturn(userDetails);
		when(eligibility.isEligible()).thenReturn(true);
	}

	@Test
	public void revalidateAllAssociationsByUser_document() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.DOCUMENT, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findUserGroupAssociationByUserIdAndGroupId(USER_ID, MODIFICATION_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_certification() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.CERTIFICATION, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithCertification(USER_ID, MODIFICATION_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_license() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.LICENSE, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithLicense(USER_ID, MODIFICATION_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_assessment() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.ASSESSMENT, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithAssessment(USER_ID, MODIFICATION_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_industry() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.INDUSTRY, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithIndustry(USER_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_insuranceAdded() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.INSURANCE_ADDED, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithInsurance(USER_ID, MODIFICATION_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_laneAssociation() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.LANE_ASSOCIATION, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithLaneRequirement(USER_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_backgroundCheck() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.BACKGROUND_CHECK, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithBackgroundCheck(USER_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_drugTest() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.DRUG_TEST, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithDrugTest(USER_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_workingHours() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.WORKING_HOURS, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithWorkingHours(USER_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_maxTravelDistance() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.MAX_TRAVEL_DISTANCE, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithLocationRequirements(USER_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_address() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.ADDRESS, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithLocationRequirements(USER_ID);
	}

	@Test
	public void revalidateAllAssociationsByUser_rating() {
		Map<String, Object> modificationType = ImmutableMap.of(ProfileModificationType.RATING, (Object) MODIFICATION_ID);
		userGroupValidationService.revalidateAllAssociationsByUser(USER_ID, modificationType);

		verify(userGroupAssociationService).findAllPendingAssociationsWithRating(USER_ID);
	}

	@Test
	public void revalidateAllAssociations_update_triggersHistoryUpdate() {
		when(userUserGroupAssociation.getVerificationStatus()).thenReturn(VerificationStatus.UNVERIFIED);

		when(userGroupService.findGroupById(anyLong())).thenReturn(userGroup);
		when(userGroupService.findAllActiveAssociations(anyLong())).thenReturn(ImmutableList.of(userUserGroupAssociation));
		when(userGroupService.reValidateRequirementSets(anyLong(), anyLong())).thenReturn(eligibility);
		when(userGroupAssociationService.findUserUserGroupAssociationById(ASSOCIATION_ID)).thenReturn(userUserGroupAssociation);

		when(userGroupAssociationValidationUpdateService.getMetRequirementsAssociationUpdateTypes(userUserGroupAssociation))
			.thenReturn(EnumSet.of(AssociationUpdateType.MEETS_REQUIREMENTS_SET_VERIFIED));

		userGroupValidationService.revalidateAllAssociations(GROUP_ID);

		verify(summaryService).saveUserGroupAssociationHistorySummary(userUserGroupAssociation);
	}

	@Test
	public void revalidateAllAssociations_noUpdate_noHistoryUpdate() {
		when(userGroup.getRequiresApproval()).thenReturn(false);

		when(userUserGroupAssociation.getApprovalStatus()).thenReturn(ApprovalStatus.PENDING);

		when(userGroupService.findGroupById(anyLong())).thenReturn(userGroup);
		when(userGroupService.findAllActiveAssociations(anyLong())).thenReturn(ImmutableList.of(userUserGroupAssociation));
		when(userGroupService.reValidateRequirementSets(anyLong(), anyLong())).thenReturn(eligibility);

		when(userGroupAssociationService.findUserUserGroupAssociationById(ASSOCIATION_ID)).thenReturn(userUserGroupAssociation);
		when(userGroupAssociationValidationUpdateService.getMetRequirementsAssociationUpdateTypes(userUserGroupAssociation))
			.thenReturn(Collections.emptySet());

		userGroupValidationService.revalidateAllAssociations(GROUP_ID);

		verify(summaryService, never()).saveUserGroupAssociationHistorySummary(userUserGroupAssociation);
	}
}
