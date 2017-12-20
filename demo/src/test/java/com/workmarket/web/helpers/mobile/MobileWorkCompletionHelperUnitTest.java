package com.workmarket.web.helpers.mobile;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.Work;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * User: micah
 * Date: 9/6/13
 * Time: 3:56 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class MobileWorkCompletionHelperUnitTest {
	@Mock WorkService workService;
	@Mock AssessmentService assessmentService;
	@Mock DeliverableService deliverableService;
	@Mock FeatureEvaluator featureEvaluator;
	@Mock SecurityContextFacade securityContextFacade;
	@Mock MessageBundleHelper messageHelper;

	@InjectMocks MobileWorkCompletionHelper mobileWorkCompletionHelper = spy(new MobileWorkCompletionHelper());

	AbstractWork abstractWork;
	Work work;
	DeliverableRequirementDTO deliverableRequirementDTOOne;
	DeliverableRequirementDTO deliverableRequirementDTOTwo;
	List<DeliverableRequirementDTO> deliverableRequirementDTOs;
	DeliverableRequirementGroupDTO deliverableRequirementGroupDTO;
	ExtendedUserDetails user;
	SecurityContext securityContext;

	int deliverableRequirementOneNumberOfFiles = 1;
	int deliverableRequirementTwoNumberOfFiles = 2;
	String workNumber = "1";

	@Before
	public void setup() {
		abstractWork = mock(AbstractWork.class);
		work = mock(Work.class);
		user = mock(ExtendedUserDetails.class);
		securityContext = mock(SecurityContext.class);
		when(securityContextFacade.getSecurityContext()).thenReturn(securityContext);
		when(securityContext.getAuthentication()).thenReturn((Authentication)null);
		when(featureEvaluator.hasFeature(any(Authentication.class), any(Object.class))).thenReturn(true);
		when(messageHelper.getMessage(anyString())).thenReturn("WM");
		when(workService.findWork(work.getId(), true)).thenReturn(abstractWork);
	}

	private void setupConfirmation(Boolean isWorkResourceConfirmed) {
		WorkResource workResource = mock(WorkResource.class);
		when(workResource.isConfirmed()).thenReturn(isWorkResourceConfirmed);

		when(abstractWork.getId()).thenReturn(1L);
		when(workService.findActiveWorkResource(1L)).thenReturn(workResource);
	}

	@Test
	public void validateConfirmation_NotRequired() {
		setupConfirmation(Boolean.FALSE);
		when(abstractWork.isResourceConfirmationRequired()).thenReturn(Boolean.FALSE);

		mobileWorkCompletionHelper.validateAll(work, user);
		verify(mobileWorkCompletionHelper, never()).validateConfirmation(abstractWork);
	}

	@Test
	public void validateConfirmation_Required_HappyPath() {
		setupConfirmation(Boolean.TRUE);
		when(abstractWork.isResourceConfirmationRequired()).thenReturn(Boolean.TRUE);

		MobileResponse response = mobileWorkCompletionHelper.validateConfirmation(abstractWork);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void validateConfirmation_Fault() {
		setupConfirmation(Boolean.FALSE);
		when(abstractWork.isResourceConfirmationRequired()).thenReturn(Boolean.TRUE);

		MobileResponse response = mobileWorkCompletionHelper.validateConfirmation(abstractWork);
		assertFalse(response.isSuccessful());
	}

	@Test
	public void validateCheckInCheckOut_NotRequired() {
		when(abstractWork.isCheckinRequired()).thenReturn(Boolean.FALSE);
		when(abstractWork.isCheckinCallRequired()).thenReturn(Boolean.FALSE);

		mobileWorkCompletionHelper.validateAll(work, user);
		verify(mobileWorkCompletionHelper, never()).validateCheckInOut(abstractWork);
	}

	private void setupCheckInCheckOut() {
		when(abstractWork.getId()).thenReturn(1L);
		when(abstractWork.isCheckinRequired()).thenReturn(Boolean.TRUE);

		WorkResource workResource = mock(WorkResource.class);
		when(workResource.getId()).thenReturn(2L);

		when(workService.findActiveWorkResource(1L)).thenReturn(workResource);
	}

	@Test
	public void validateCheckInCheckOut_Required_Success() {
		setupCheckInCheckOut();
		WorkResourceTimeTracking workResourceTimeTracking = mock(WorkResourceTimeTracking.class);
		when(workService.findLatestTimeTrackRecordByWorkResource(2L)).thenReturn(workResourceTimeTracking);

		when(workService.isActiveResourceCurrentlyCheckedIn(1L)).thenReturn(Boolean.FALSE);

		MobileResponse response = mobileWorkCompletionHelper.validateCheckInOut(abstractWork);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void validateCheckInCheckOut_Required_NeverCheckedIn_Fault() {
		setupCheckInCheckOut();
		when(workService.findLatestTimeTrackRecordByWorkResource(2L)).thenReturn(null);

		when(workService.isActiveResourceCurrentlyCheckedIn(1L)).thenReturn(Boolean.FALSE);

		MobileResponse response = mobileWorkCompletionHelper.validateCheckInOut(abstractWork);
		assertFalse(response.isSuccessful());
	}

	@Test
	public void validateCheckInCheckOut_Required_CheckedInButNotOut_Fault() {
		setupCheckInCheckOut();
		WorkResourceTimeTracking workResourceTimeTracking = mock(WorkResourceTimeTracking.class);
		when(workService.findLatestTimeTrackRecordByWorkResource(2L)).thenReturn(workResourceTimeTracking);

		when(workService.isActiveResourceCurrentlyCheckedIn(1L)).thenReturn(Boolean.TRUE);

		MobileResponse response = mobileWorkCompletionHelper.validateCheckInOut(abstractWork);
		assertFalse(response.isSuccessful());
	}

	private void setupAssessments() {
		Set<AbstractAssessment> requiredAssessments = mock(Set.class);
		when(requiredAssessments.size()).thenReturn(1);
		when(abstractWork.getRequiredAssessments()).thenReturn(requiredAssessments);
		Iterator<AbstractAssessment> iterator = mock(Iterator.class);
		when(requiredAssessments.iterator()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true, false);
		AbstractAssessment abstractAssessment = mock(AbstractAssessment.class);
		when(abstractAssessment.getId()).thenReturn(1L);
		when(iterator.next()).thenReturn(abstractAssessment);
	}

	@Test
	public void validateAssessments_NotRequired() {
		Set<AbstractAssessment> requiredAssessments = mock(Set.class);
		when(requiredAssessments.size()).thenReturn(0);
		when(abstractWork.getRequiredAssessments()).thenReturn(requiredAssessments);

		mobileWorkCompletionHelper.validateAll(work, user);
		verify(mobileWorkCompletionHelper, never()).validateAssessments(abstractWork, user);
	}

	@Test
	public void validateAssessments_Required_HappyPath() {
		setupAssessments();
		Attempt attempt = mock(Attempt.class);
		when(attempt.isComplete()).thenReturn(Boolean.TRUE);

		when(user.getId()).thenReturn(2L);
		when(abstractWork.getId()).thenReturn(3L);

		when(assessmentService.findLatestAttemptForAssessmentByUserScopedToWork(1L, 2L, 3L)).thenReturn(attempt);

		MobileResponse response = mobileWorkCompletionHelper.validateAssessments(abstractWork, user);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void validateAssessments_Required_NullAttempt_Fault() {
		setupAssessments();
		when(user.getId()).thenReturn(2L);
		when(abstractWork.getId()).thenReturn(3L);

		when(assessmentService.findLatestAttemptForAssessmentByUserScopedToWork(1L, 2L, 3L)).thenReturn(null);

		MobileResponse response = mobileWorkCompletionHelper.validateAssessments(abstractWork, user);
		assertFalse(response.isSuccessful());
	}

	@Test
	public void validateAssessments_Required_IncompleteAttempt_Fault() {
		setupAssessments();
		Attempt attempt = mock(Attempt.class);
		when(attempt.isComplete()).thenReturn(Boolean.FALSE);

		when(user.getId()).thenReturn(2L);
		when(abstractWork.getId()).thenReturn(3L);

		when(assessmentService.findLatestAttemptForAssessmentByUserScopedToWork(1L, 2L, 3L)).thenReturn(attempt);

		MobileResponse response = mobileWorkCompletionHelper.validateAssessments(abstractWork, user);
		assertFalse(response.isSuccessful());
	}

	private void deliverablesSetup() {
		deliverableRequirementDTOOne = mock(DeliverableRequirementDTO.class);
		deliverableRequirementDTOTwo = mock(DeliverableRequirementDTO.class);
		deliverableRequirementGroupDTO = mock(DeliverableRequirementGroupDTO.class);
		deliverableRequirementDTOs = Lists.newArrayListWithExpectedSize(2);
		deliverableRequirementDTOs.add(deliverableRequirementDTOOne);
		deliverableRequirementDTOs.add(deliverableRequirementDTOTwo);

		when(deliverableRequirementGroupDTO.getDeliverableRequirementDTOs()).thenReturn(deliverableRequirementDTOs);
		when(deliverableRequirementDTOOne.getNumberOfFiles()).thenReturn(deliverableRequirementOneNumberOfFiles);
		when(deliverableRequirementDTOTwo.getNumberOfFiles()).thenReturn(deliverableRequirementTwoNumberOfFiles);

		when(deliverableService.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementDTOOne.getId())).thenReturn(deliverableRequirementOneNumberOfFiles);
		when(deliverableService.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementDTOTwo.getId())).thenReturn(deliverableRequirementTwoNumberOfFiles);
	}

	@Test
	public void validateDeliverables_requirementsMet_SuccessfulValidation() {
		deliverablesSetup();
		MobileResponse response = mobileWorkCompletionHelper.validateDeliverables(deliverableRequirementGroupDTO, workNumber);

		assertTrue(response.isSuccessful());
	}

	@Test
	public void validateDeliverables_requirementsNotMet_FailedValidation() {
		deliverablesSetup();
		when(deliverableService.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementDTOOne.getId())).thenReturn(deliverableRequirementTwoNumberOfFiles - 1);
		MobileResponse response = mobileWorkCompletionHelper.validateDeliverables(deliverableRequirementGroupDTO, workNumber);

		assertFalse(response.isSuccessful());
	}

	private CustomField customFieldsSetup() {
		CustomField customField = mock(CustomField.class);
		CustomFieldGroup customFieldGroup = mock(CustomFieldGroup.class);

		List<CustomFieldGroup> customFieldGroups = mock(List.class);
		when(work.getCustomFieldGroups()).thenReturn(customFieldGroups);

		Iterator<CustomFieldGroup> groupIterator = mock(Iterator.class);
		when(customFieldGroups.iterator()).thenReturn(groupIterator);
		when(groupIterator.hasNext()).thenReturn(true, false);
		when(groupIterator.next()).thenReturn(customFieldGroup);

		List<CustomField> customFields = mock(List.class);
		when(customFieldGroup.getFields()).thenReturn(customFields);

		Iterator<CustomField> fieldIterator = mock(Iterator.class);
		when(customFields.iterator()).thenReturn(fieldIterator);
		when(fieldIterator.hasNext()).thenReturn(true, false);
		when(fieldIterator.next()).thenReturn(customField);

		return customField;
	}

	@Test
	public void validateCustomFields_NotRequired() {
		CustomField customField = customFieldsSetup();
		when(customField.getType()).thenReturn("resource");
		when(customField.isIsRequired()).thenReturn(Boolean.FALSE);

		MobileResponse response = mobileWorkCompletionHelper.validateCustomFields(work);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void validateCustomFields_Required_HappyPath() {
		CustomField customField = customFieldsSetup();
		when(customField.getType()).thenReturn("resource");
		when(customField.isIsRequired()).thenReturn(Boolean.TRUE);
		when(customField.getValue()).thenReturn("A Value");

		MobileResponse response = mobileWorkCompletionHelper.validateCustomFields(work);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void validateCustomFields_Required_NoValue_Fault() {
		CustomField customField = customFieldsSetup();
		when(customField.getType()).thenReturn("resource");
		when(customField.isIsRequired()).thenReturn(Boolean.TRUE);
		when(customField.getValue()).thenReturn("");

		MobileResponse response = mobileWorkCompletionHelper.validateCustomFields(work);
		assertFalse(response.isSuccessful());
	}
}