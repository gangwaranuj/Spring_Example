package com.workmarket.domains.work.service;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.asset.AssetDAOImpl;
import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.compliance.service.ComplianceService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.DeliverableRequirementGroup;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.payments.dao.InvoiceDAO;
import com.workmarket.domains.work.dao.WorkDAOImpl;
import com.workmarket.domains.work.dao.WorkNegotiationDAO;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.core.Status;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkValidationServiceTest {

	@Mock private WorkService workService;
	@Mock private AuthenticationService authenticationService;
	@Mock private LaneService laneService;
	@Mock private WorkDAOImpl workDao;
	@Mock private AssetDAOImpl assetDAO;
	@Mock private InvoiceDAO invoiceDAO;
	@Mock private DeliverableService deliverableService;
	@Mock private ComplianceService complianceService;
	@Mock private WorkNegotiationDAO workNegotiationDAO;
	@Mock private MessageBundleHelper messageHelper;
	@Mock private UserService userService;
	@Mock private PricingService pricingService;
	@Mock private UserRoleService userRoleService;

	@InjectMocks WorkValidationServiceImpl workValidationService;

	Work work;
	Long workId;
	Status status;
	String workNumber;
	String workNumberForNonExistentAssignment;
	com.workmarket.thrift.work.Work tWork;
	UnassignDTO unassignWorkDto;
	CompleteWorkDTO completeWorkDTO;
	DeliverableRequirementGroup deliverableRequirementGroup;
	DeliverableRequirement deliverableRequirementOne;
	DeliverableRequirement deliverableRequirementTwo;
	List<DeliverableRequirement> deliverableRequirements;
	WorkAssetAssociationType deliverableRequirementOneWorkAssetType;
	WorkAssetAssociationType deliverableRequirementTwoWorkAssetType;
	Long deliverableRequirementOneId;
	Long deliverableRequirementTwoId;
	String deliverableRequirementOneType;
	String deliverableRequirementTwoType;
	int numberOfFilesRequiredForDeliverableRequirementOne;
	int numberOfFilesRequiredForDeliverableRequirementTwo;
	User adminUser;
	User nonAdminUser;
	Invoice invoice;
	Company company;
	User workerUser;
	Company workerCompany;
	Calendar calendar;
	WorkNegotiation negotiation;
	Compliance compliance;
	WorkStatusType workStatusType;
	Optional<PersonaPreference> personaPreferenceOptional;
	PersonaPreference personaPreferenceDispatcher;
	PersonaPreference personaPreferenceNotDispatcher;
	List<WorkResource> workResources;
	PerHourPricingStrategy perHourPricingStrategy;
	BlendedPerHourPricingStrategy blendedPerHourPricingStrategy;
	WorkResource workResource;
	FullPricingStrategy fullPricingStrategy;

	@Before
	public void setup() {
		Industry industry = mock(Industry.class);
		company = mock(Company.class);

		workId = 1L;
		workNumber = "1";
		workNumberForNonExistentAssignment = "123";
		deliverableRequirementOneId = 1L;
		deliverableRequirementTwoId = 2L;
		deliverableRequirementOneType = "photos";
		deliverableRequirementTwoType = "sign_off";
		numberOfFilesRequiredForDeliverableRequirementOne = 1;
		numberOfFilesRequiredForDeliverableRequirementTwo = 2;
		workResources = Lists.newArrayList();

		when(company.getId()).thenReturn(20L);
		work = mock(Work.class);
		when(work.getWorkNumber()).thenReturn(workNumber);
		when(work.getIndustry()).thenReturn(industry);
		when(work.getCompany()).thenReturn(company);
		when(work.getId()).thenReturn(workId);
		when(work.getWorkStatusType()).thenReturn(workStatusType);
		when(industry.getId()).thenReturn(1000L);

		status = mock(Status.class);

		tWork = mock(com.workmarket.thrift.work.Work.class);
		when(tWork.getStatus()).thenReturn(status);

		unassignWorkDto = mock(UnassignDTO.class);

		workerUser = mock(User.class);
		when(authenticationService.isLane3Active(workerUser)).thenReturn(false);
		when(workerUser.getId()).thenReturn(50L);
		workerCompany = mock(Company.class);
		when(workerCompany.getId()).thenReturn(51L);
		when(workerUser.getCompany()).thenReturn(workerCompany);

		deliverableRequirementOne = mock(DeliverableRequirement.class);
		deliverableRequirementOneWorkAssetType = mock(WorkAssetAssociationType.class);
		when(deliverableRequirementOneWorkAssetType.getCode()).thenReturn(deliverableRequirementOneType);
		when(deliverableRequirementOne.getType()).thenReturn(deliverableRequirementOneWorkAssetType);
		when(deliverableRequirementOne.getNumberOfFiles()).thenReturn(numberOfFilesRequiredForDeliverableRequirementOne);

		deliverableRequirementTwo = mock(DeliverableRequirement.class);
		deliverableRequirementTwoWorkAssetType = mock(WorkAssetAssociationType.class);
		when(deliverableRequirementOne.getId()).thenReturn(deliverableRequirementOneId);
		when(deliverableRequirementTwo.getId()).thenReturn(deliverableRequirementTwoId);
		when(deliverableRequirementTwoWorkAssetType.getCode()).thenReturn(deliverableRequirementTwoType);
		when(deliverableRequirementTwo.getType()).thenReturn(deliverableRequirementTwoWorkAssetType);
		when(deliverableRequirementTwo.getNumberOfFiles()).thenReturn(numberOfFilesRequiredForDeliverableRequirementTwo);

		deliverableRequirements = Lists.newArrayList();
		deliverableRequirements.add(deliverableRequirementOne);
		deliverableRequirements.add(deliverableRequirementTwo);

		deliverableRequirementGroup = mock(DeliverableRequirementGroup.class);
		when(deliverableRequirementGroup.getDeliverableRequirements()).thenReturn(deliverableRequirements);

		invoice = mock(Invoice.class);
		when(invoice.isPaymentPending()).thenReturn(Boolean.TRUE);
		when(work.getInvoice()).thenReturn(invoice);
		when(work.getDeliverableRequirementGroup()).thenReturn(deliverableRequirementGroup);

		adminUser = mock(User.class);
		when(adminUser.getId()).thenReturn(1L);
		when(adminUser.getCompany()).thenReturn(company);
		when(adminUser.isAdmin()).thenReturn(Boolean.TRUE);

		nonAdminUser = mock(User.class);
		when(nonAdminUser.getId()).thenReturn(1L);
		when(nonAdminUser.getCompany()).thenReturn(company);
		when(nonAdminUser.isAdmin()).thenReturn(Boolean.FALSE);

		when(authenticationService.getCurrentUser()).thenReturn(adminUser);

		when(workDao.findWorkByWorkNumber(workNumber)).thenReturn(work);

		when(laneService.isLane3Active(anyLong())).thenReturn(false);
		when(laneService.isUserPartOfLane123(anyLong(), anyLong())).thenReturn(false);
		when(deliverableService.isDeliverableRequirementComplete(any(DeliverableRequirement.class))).thenReturn(true);

		personaPreferenceDispatcher = mock(PersonaPreference.class);
		when(personaPreferenceDispatcher.isDispatcher()).thenReturn(true);

		personaPreferenceNotDispatcher = mock(PersonaPreference.class);
		when(personaPreferenceNotDispatcher.isDispatcher()).thenReturn(false);

		personaPreferenceOptional = Optional.of(personaPreferenceNotDispatcher);
		when(userService.getPersonaPreference(anyLong())).thenReturn(personaPreferenceOptional);

		calendar = mock(Calendar.class);
		negotiation = mock(WorkNegotiation.class);
		when(negotiation.getWork()).thenReturn(work);
		when(negotiation.getRequestedBy()).thenReturn(workerUser);
		when(negotiation.getScheduleRangeFlag()).thenReturn(false);
		when(negotiation.getScheduleFrom()).thenReturn(calendar);
		when(negotiation.isScheduleNegotiation()).thenReturn(false);
		when(negotiation.getApprovalStatus()).thenReturn(ApprovalStatus.PENDING);
		when(negotiation.getWork()).thenReturn(work);
		compliance = mock(Compliance.class);
		when(compliance.isCompliant()).thenReturn(true);
		when(workNegotiationDAO.get(1L)).thenReturn(negotiation);
		workStatusType = mock(WorkStatusType.class);
		when(work.getWorkStatusType()).thenReturn(workStatusType);
		when(work.isSent()).thenReturn(true);
		when(complianceService.getComplianceFor(any(User.class), any(Work.class))).thenReturn(compliance);
		when(complianceService.getComplianceFor(any(User.class), any(Work.class), any(DateRange.class))).thenReturn(compliance);
		when(workDao.get(1L)).thenReturn(work);
		when(work.isDraft()).thenReturn(false);

		completeWorkDTO = mock(CompleteWorkDTO.class);
		perHourPricingStrategy = mock(PerHourPricingStrategy.class);
		blendedPerHourPricingStrategy = mock(BlendedPerHourPricingStrategy.class);
		workResource = mock(WorkResource.class);
		fullPricingStrategy = mock(FullPricingStrategy.class);
		when(pricingService.calculateMaximumResourceCost(work)).thenReturn(BigDecimal.ONE);
		when(perHourPricingStrategy.getFullPricingStrategy()).thenReturn(fullPricingStrategy);
		when(blendedPerHourPricingStrategy.getFullPricingStrategy()).thenReturn(fullPricingStrategy);
		when(perHourPricingStrategy.getMaxNumberOfHours()).thenReturn(BigDecimal.ONE);
		when(perHourPricingStrategy.getPerHourPrice()).thenReturn(BigDecimal.ONE);
		when(blendedPerHourPricingStrategy.getInitialNumberOfHours()).thenReturn(BigDecimal.ONE);
		when(blendedPerHourPricingStrategy.getInitialPerHourPrice()).thenReturn(BigDecimal.ONE);
		when(blendedPerHourPricingStrategy.getMaxBlendedNumberOfHours()).thenReturn(BigDecimal.ONE);
		when(blendedPerHourPricingStrategy.getAdditionalPerHourPrice()).thenReturn(BigDecimal.ONE);
		when(fullPricingStrategy.getSalesTaxCollectedFlag()).thenReturn(false);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(work.getRequiredAssessments()).thenReturn(null);
	}

	@Test
	public void validateApproveWorkNegotiation_calls_getComplianceFor() {
		when(negotiation.isInitiatedByResource()).thenReturn(false);
		when(workService.isUserActiveResourceForWork(anyLong(), anyLong())).thenReturn(true);
		when(negotiation.getNegotiationType()).thenReturn(WorkNegotiation.RESCHEDULE);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.ACTIVE);
		workValidationService.validateApproveWorkNegotiation(1L, null);
		verify(complianceService, times(1)).getComplianceFor(any(User.class), any(Work.class));
	}

	@Test
	public void validateApproveWorkNegotiation_calls_getComplianceFor_withSchedule() {
		when(negotiation.isScheduleNegotiation()).thenReturn(true);
		when(negotiation.isInitiatedByResource()).thenReturn(false);
		when(workService.isUserActiveResourceForWork(anyLong(), anyLong())).thenReturn(true);
		when(negotiation.getNegotiationType()).thenReturn(WorkNegotiation.RESCHEDULE);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.ACTIVE);
		workValidationService.validateApproveWorkNegotiation(1L, null);
		verify(complianceService, times(1)).getComplianceFor(any(User.class), any(Work.class), any(DateRange.class));
	}

	@Test
	public void validateApproveWorkNegotiation_notCompliant() {
		when(compliance.isCompliant()).thenReturn(false);
		when(authenticationService.getCurrentUser()).thenReturn(workerUser);
		when(negotiation.isInitiatedByResource()).thenReturn(false);
		when(workService.isUserActiveResourceForWork(anyLong(), anyLong())).thenReturn(true);
		when(negotiation.getNegotiationType()).thenReturn(WorkNegotiation.RESCHEDULE);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.ACTIVE);
		workValidationService.validateApproveWorkNegotiation(1L, null);
		verify(messageHelper, times(1)).getMessage(eq("assignment.compliance.user_schedule_not_allowed"), anyString());
	}

	@Test
	public void validateStopPayment_withAdmin_Succeeds() {
		when(work.isPaymentPending()).thenReturn(Boolean.TRUE);
		when(authenticationService.getCurrentUser()).thenReturn(adminUser);
		when(invoice.isEditable()).thenReturn(Boolean.TRUE);
		when(userRoleService.isAdmin(adminUser)).thenReturn(true);
		List<ConstraintViolation> list = workValidationService.validateStopPayment(work);
		assertEquals(0, list.size());
	}

	@Test
	public void validateStopPayment_withBundledInvoice_fail() {
		when(work.isPaymentPending()).thenReturn(Boolean.TRUE);
		when(authenticationService.getCurrentUser()).thenReturn(adminUser);
		when(userRoleService.isAdmin(adminUser)).thenReturn(true);
		when(invoice.isEditable()).thenReturn(Boolean.TRUE);
		when(invoice.isBundled()).thenReturn(Boolean.TRUE);
		InvoiceSummary bundle = mock(InvoiceSummary.class);
		when(bundle.isEditable()).thenReturn(false);
		when(invoiceDAO.findInvoiceSummaryByInvoiceBundledId(anyLong())).thenReturn(bundle);
		List<ConstraintViolation> list = workValidationService.validateStopPayment(work);
		assertEquals(1, list.size());
	}

	@Test
	public void validateStopPayment_withNonBundledInvoice_success() {
		when(work.isPaymentPending()).thenReturn(Boolean.TRUE);
		when(authenticationService.getCurrentUser()).thenReturn(adminUser);
		when(invoice.isEditable()).thenReturn(Boolean.TRUE);
		when(invoice.isBundled()).thenReturn(Boolean.TRUE);
		InvoiceSummary bundle = mock(InvoiceSummary.class);
		when(userRoleService.isAdmin(adminUser)).thenReturn(true);
		when(bundle.isEditable()).thenReturn(true);
		when(invoiceDAO.findInvoiceSummaryByInvoiceBundledId(anyLong())).thenReturn(bundle);
		List<ConstraintViolation> list = workValidationService.validateStopPayment(work);
		assertEquals(0, list.size());
	}

	@Test
	public void validateStopPayment_withNonAdmin_Fails() {
		when(work.isPaymentPending()).thenReturn(Boolean.TRUE);
		when(authenticationService.getCurrentUser()).thenReturn(nonAdminUser);
		when(invoice.isEditable()).thenReturn(Boolean.TRUE);
		List<ConstraintViolation> list = workValidationService.validateStopPayment(work);
		assertEquals(1, list.size());
	}

	@Test
	public void validateStopPayment_withNonAdminWithPaymentCenterAccess_Succeeds() {
		when(work.isPaymentPending()).thenReturn(Boolean.TRUE);
		when(authenticationService.getCurrentUser()).thenReturn(nonAdminUser);
		when(authenticationService.hasPaymentCenterAndEmailsAccess(any(Long.class), anyBoolean())).thenReturn(Boolean.TRUE);
		when(invoice.isEditable()).thenReturn(Boolean.TRUE);
		List<ConstraintViolation> list = workValidationService.validateStopPayment(work);
		assertEquals(0, list.size());
	}

	@Test
	public void validateStopPayment_withLockedInvoice_Fails() {
		when(work.isPaymentPending()).thenReturn(Boolean.TRUE);
		when(authenticationService.getCurrentUser()).thenReturn(adminUser);
		when(userRoleService.isAdmin(adminUser)).thenReturn(true);
		when(invoice.isEditable()).thenReturn(Boolean.FALSE);
		List<ConstraintViolation> list = workValidationService.validateStopPayment(work);
		assertEquals(1, list.size());
	}

	@Test
	public void validateStopPayment_withInvalidStatus_Fails() {
		when(work.isPaymentPending()).thenReturn(Boolean.FALSE);
		when(authenticationService.getCurrentUser()).thenReturn(adminUser);
		when(invoice.isEditable()).thenReturn(Boolean.TRUE);
		when(userRoleService.isAdmin(adminUser)).thenReturn(true);
		List<ConstraintViolation> list = workValidationService.validateStopPayment(work);
		assertEquals(1, list.size());
	}

	@Test
	public void validateStopPayment_withInvoicePaymentPending_Fails() {
		when(work.isPaymentPending()).thenReturn(Boolean.TRUE);
		when(authenticationService.getCurrentUser()).thenReturn(nonAdminUser);
		when(authenticationService.hasPaymentCenterAndEmailsAccess(any(Long.class), anyBoolean())).thenReturn(Boolean.TRUE);
		when(invoice.isEditable()).thenReturn(Boolean.TRUE);
		when(workDao.isWorkPendingFulfillment(work.getId())).thenReturn(Boolean.TRUE);
		List<ConstraintViolation> list = workValidationService.validateStopPayment(work);
		assertEquals(1, list.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateDeliverableRequirements_workNumberForNonExistentAssignment_IllegalArgumentException() {
		workValidationService.validateDeliverableRequirements(false, workNumberForNonExistentAssignment);
	}

	@Test
	public void validateDeliverableRequirements_validWorkNumberAndRequirementsMet_NoErrors() {
		when(assetDAO.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementOneId)).thenReturn(1);
		when(assetDAO.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementTwoId)).thenReturn(2);

		List<ConstraintViolation> errors = workValidationService.validateDeliverableRequirements(false, workNumber);

		assertEquals(errors.size(), 0);
	}

	@Test
	public void validateDeliverableRequirements_onBehalfTrue_NoValidationPerformed() {
		List<ConstraintViolation> errors = workValidationService.validateDeliverableRequirements(true, work);

		verify(work, never()).getDeliverableRequirementGroup();
		verify(deliverableRequirementGroup, never()).getDeliverableRequirements();
		assertTrue(CollectionUtils.isEmpty(errors));
	}

	@Test
	public void validateDeliverableRequirements_MissingPhotosUpload_OneError() {
		when(deliverableService.isDeliverableRequirementComplete(deliverableRequirementOne)).thenReturn(false);

		List<ConstraintViolation> errors = workValidationService.validateDeliverableRequirements(false, work);

		assertEquals(errors.size(), 1);
	}

	@Test
	public void validateDeliverableRequirements_NoUploads_TwoErrors() {
		when(assetDAO.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementOneId)).thenReturn(0);
		when(assetDAO.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementTwoId)).thenReturn(0);
		when(deliverableService.isDeliverableRequirementComplete(any(DeliverableRequirement.class))).thenReturn(false);

		List<ConstraintViolation> errors = workValidationService.validateDeliverableRequirements(false, work);

		assertEquals(errors.size(), 2);
	}

	@Test
	public void validateDeliverableRequirements_EnoughUploadsToFulfillDeliverableRequirements_NoErrors() {
		when(assetDAO.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementOneId)).thenReturn(1);
		when(assetDAO.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementTwoId)).thenReturn(2);

		List<ConstraintViolation> errors = workValidationService.validateDeliverableRequirements(false, work);

		assertEquals(errors.size(), 0);
	}

	@Test
	public void validateDeliverableRequirements_MoreUploadsThanRequired_NoErrors() {
		when(assetDAO.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementOneId)).thenReturn(10);
		when(assetDAO.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementTwoId)).thenReturn(10);

		List<ConstraintViolation> errors = workValidationService.validateDeliverableRequirements(false, work);

		assertEquals(errors.size(), 0);
	}


	@Test
	public void isWorkResourceValidForWork_zeroRelationships_isNotOk() {
		assertFalse(workValidationService.isWorkResourceValidForWork(workerUser.getId(), workerUser.getCompany().getId(), work.getCompany().getId()));
	}

	@Test
	public void isWorkResourceValidForWork_explicitLane123_isOk() {
		when(laneService.isUserPartOfLane123(workerUser.getId(), work.getCompany().getId())).thenReturn(true);
		assertTrue(workValidationService.isWorkResourceValidForWork(workerUser.getId(), workerUser.getCompany().getId(), work.getCompany().getId()));
	}

	@Test
	public void isWorkResourceValidForWork_isLane3Active_isOk() {
		when(laneService.isLane3Active(workerUser.getId())).thenReturn(true);
		assertTrue(workValidationService.isWorkResourceValidForWork(workerUser.getId(), workerUser.getCompany().getId(), work.getCompany().getId()));
	}

	@Test
	public void isWorkResourceValidForWork_sameCompany_isOk() {
		assertTrue(workValidationService.isWorkResourceValidForWork(workerUser.getId(), workerUser.getCompany().getId(), workerUser.getCompany().getId()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateCancelWorkNegotiation_nullNegotiationId_exceptionThrown() {
		workValidationService.validateCancelWorkNegotiation(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateCancelWorkNegotiation_negotiationNotFound_exceptionThrown() {
		when(workNegotiationDAO.get(anyLong())).thenReturn(null);

		workValidationService.validateCancelWorkNegotiation(1L);
	}

	@Test
	public void validateCancelWorkNegotiation_alreadyApprovedNegotiation_returnedWithViolations() {
		when(negotiation.getApprovalStatus()).thenReturn(ApprovalStatus.APPROVED);

		assertTrue(workValidationService.validateCancelWorkNegotiation(1L).size() > 0);
	}

	@Test
	public void validateCancelWorkNegotiation_isInitiatedByResourceAndCurrentUserIsNotNegotiationRequestorOrDispatcher_returnedWithViolations() {
		when(negotiation.isInitiatedByResource()).thenReturn(true);

		assertTrue(workValidationService.validateCancelWorkNegotiation(1L).size() > 0);
	}

	@Test
	public void validateCancelWorkNegotiation_currentUserIsNegotiationRequestorButNotDispatcher_returnedWithNoViolations() {
		when(negotiation.isInitiatedByResource()).thenReturn(true);
		when(authenticationService.getCurrentUser()).thenReturn(workerUser);

		assertTrue(workValidationService.validateCancelWorkNegotiation(1L).size() == 0);
	}

	@Test
	public void validateCancelWorkNegotiation_currentUserIsNotNegotiationRequestorButIsDispatcher_returnedWithNoViolations() {
		when(negotiation.isInitiatedByResource()).thenReturn(true);
		personaPreferenceOptional = Optional.of(personaPreferenceDispatcher);
		when(userService.getPersonaPreference(anyLong())).thenReturn(personaPreferenceOptional);

		assertTrue(workValidationService.validateCancelWorkNegotiation(1L).size() == 0);
	}

	@Test
	public void validateCancelWorkNegotiation_isAuthorizedToAdministerOnNegotiation_returnedWithNoViolations() {
		when(workService.isAuthorizedToAdminister(anyLong(), anyLong())).thenReturn(true);

		assertTrue(workValidationService.validateCancelWorkNegotiation(1L).size() == 0);
	}

	@Test
	public void validateCancelWorkNegotiation_isNotAuthorizedToAdministerOnNegotiation_returnedWithViolations() {
		when(workService.isAuthorizedToAdminister(anyLong(), anyLong())).thenReturn(false);

		assertTrue(workValidationService.validateCancelWorkNegotiation(1L).size() == 1);
	}

	@Test
	public void validateRepriceWorkWithValidRepriceStatus_returnedWithoutViolations() {
		for (String type : WorkStatusType.REPRICE_WORK_VALID_STATUS_TYPES) {
			when(workStatusType.getCode()).thenReturn(type);
			assertTrue(workValidationService.validateRepriceWork(1L, new PerHourPricingStrategy(), workResources, true).size() == 0);
		}
	}

	@Test
	public void validateRepriceClosedWork_returnedWithViolations() {
		for (String type : WorkStatusType.CLOSED_WORK_STATUS_TYPES) {
			when(workStatusType.getCode()).thenReturn(type);
			assertTrue(workValidationService.validateRepriceWork(1L, new PerHourPricingStrategy(), workResources, true).size() == 1);
		}
	}

	@Test
	public void validateRepricePaidWork_returnedWithViolations() {
		for (String type : WorkStatusType.PAID_STATUS_TYPES) {
			when(workStatusType.getCode()).thenReturn(type);
			assertTrue(workValidationService.validateRepriceWork(1L, new PerHourPricingStrategy(), workResources, true).size() == 1);
		}
	}

	@Test
	public void validateRepricePaymentPendingWork_returnedWithViolations() {
		for (String type : WorkStatusType.PAYMENT_PENDING_STATUS_TYPES) {
			when(workStatusType.getCode()).thenReturn(type);
			assertTrue(workValidationService.validateRepriceWork(1L, new PerHourPricingStrategy(), workResources, true).size() == 1);
		}
	}

	@Test
	public void validateRepriceCancelledWork_returnedWithViolations() {
		for (String type : WorkStatusType.CANCELLED_WORK_STATUS_TYPES) {
			when(workStatusType.getCode()).thenReturn(type);
			assertTrue(workValidationService.validateRepriceWork(1L, new PerHourPricingStrategy(), workResources, true).size() == 1);
		}
	}

	@Test
	public void validateRepriceVoidedWork_returnedWithViolations() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.VOID);
		assertTrue(workValidationService.validateRepriceWork(1L, new PerHourPricingStrategy(), workResources, true).size() == 1);
	}

	@Test
	public void validateRepriceDeletedWork_returnedWithViolations() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.DELETED);
		assertTrue(workValidationService.validateRepriceWork(1L, new PerHourPricingStrategy(), workResources, true).size() == 1);
	}

	@Test
	public void validateRepriceWork_fromInternal_returnedWithViolations() {
		when(work.getPricingStrategyType()).thenReturn(PricingStrategyType.INTERNAL);
		assertTrue(workValidationService.validateRepriceWork(1L, new PerHourPricingStrategy(), workResources, true).size() == 1);
	}

	@Test
	public void validateRepriceWork_toInternal_returnedWithViolations() {
		assertTrue(workValidationService.validateRepriceWork(1L, new InternalPricingStrategy(), workResources, true).size() == 1);
	}

	@Test
	public void validateUnassignWork_badWorkStatus_violation() {
		when(status.getCode()).thenReturn(WorkStatusType.COMPLETE);
		when(unassignWorkDto.getCancellationReasonTypeCode()).thenReturn(null);

		List<ConstraintViolation> result = workValidationService.validateUnassign(new WorkStatusType(tWork.getStatus().getCode()), unassignWorkDto);
		assertEquals(1, result.size());
		assertEquals("assignment.unassign.notallowed", result.get(0).getKey());
	}

	@Test
	public void validateUnassignWork_okStatusAndNoCancellationReasonCode_noViolations() {
		when(status.getCode()).thenReturn(WorkStatusType.ACTIVE);
		when(unassignWorkDto.getCancellationReasonTypeCode()).thenReturn(null);

		List<ConstraintViolation> result = workValidationService.validateUnassign(new WorkStatusType(tWork.getStatus().getCode()), unassignWorkDto);
		assertEquals(0, result.size());
	}

	@Test
	public void validateUnassignWork_badCancelCode_violation() {
		when(status.getCode()).thenReturn(WorkStatusType.ACTIVE);
		when(unassignWorkDto.getCancellationReasonTypeCode()).thenReturn(CancellationReasonType.PERSONAL_EMERGENCY);

		List<ConstraintViolation> result = workValidationService.validateUnassign(new WorkStatusType(tWork.getStatus().getCode()), unassignWorkDto);
		assertEquals(1, result.size());
		assertEquals("assignment.unassign.generic_error", result.get(0).getKey());
	}

	@Test
	public void validateUnassignWork_goodCodeNoNote_violation() {
		when(status.getCode()).thenReturn(WorkStatusType.ACTIVE);
		when(unassignWorkDto.getCancellationReasonTypeCode()).thenReturn(CancellationReasonType.RESOURCE_ABANDONED);
		when(unassignWorkDto.getNote()).thenReturn(null);

		List<ConstraintViolation> result = workValidationService.validateUnassign(new WorkStatusType(tWork.getStatus().getCode()), unassignWorkDto);
		assertEquals(1, result.size());
		assertEquals("assignment.unassign.note_required", result.get(0).getKey());
	}

	@Test
	public void validateUnassignWork_goodCodeWithNote_Noviolation() {
		when(status.getCode()).thenReturn(WorkStatusType.ACTIVE);
		when(unassignWorkDto.getCancellationReasonTypeCode()).thenReturn(CancellationReasonType.RESOURCE_ABANDONED);
		when(unassignWorkDto.getNote()).thenReturn("This is a note");

		List<ConstraintViolation> result = workValidationService.validateUnassign(new WorkStatusType(tWork.getStatus().getCode()), unassignWorkDto);
		assertEquals(0, result.size());
	}

	@Test
	public void validateUnassignWork_badStatusAndBadCode() {
		when(status.getCode()).thenReturn(WorkStatusType.COMPLETE);
		when(unassignWorkDto.getCancellationReasonTypeCode()).thenReturn(CancellationReasonType.PERSONAL_EMERGENCY);

		List<ConstraintViolation> result = workValidationService.validateUnassign(new WorkStatusType(tWork.getStatus().getCode()), unassignWorkDto);
		assertEquals(2, result.size());
		assertEquals("assignment.unassign.notallowed", result.get(0).getKey());
		assertEquals("assignment.unassign.generic_error", result.get(1).getKey());
	}

	@Test
	public void validateUnassignWork_badStatusAndGoodCodeNoNote() {
		when(status.getCode()).thenReturn(WorkStatusType.COMPLETE);
		when(unassignWorkDto.getCancellationReasonTypeCode()).thenReturn(CancellationReasonType.RESOURCE_ABANDONED);
		when(unassignWorkDto.getNote()).thenReturn(null);

		List<ConstraintViolation> result = workValidationService.validateUnassign(new WorkStatusType(tWork.getStatus().getCode()), unassignWorkDto);
		assertEquals(2, result.size());
		assertEquals("assignment.unassign.notallowed", result.get(0).getKey());
		assertEquals("assignment.unassign.note_required", result.get(1).getKey());
	}

	@Test
	public void validateCompleteWork_perHour_negativeHoursWorked_priceOverride() {
		when(work.isCheckinRequired()).thenReturn(false);
		when(work.isCheckinCallRequired()).thenReturn(false);

		when(completeWorkDTO.getResolution()).thenReturn("work done");

		when(work.getPricingStrategy()).thenReturn(perHourPricingStrategy);
		when(completeWorkDTO.getHoursWorked()).thenReturn(-1.0);
		when(completeWorkDTO.getOverridePrice()).thenReturn(1.0);
		when(work.isDeliverableRequired()).thenReturn(false);

		List<ConstraintViolation> result = workValidationService.validateComplete(work, completeWorkDTO, false);
		assertEquals(1, result.size());
		assertEquals("hours_worked_required", result.get(0).getKey());
	}

	@Test
	public void validateCompleteWork_perHour_negativeHoursWorked() {
		when(work.isCheckinRequired()).thenReturn(false);
		when(work.isCheckinCallRequired()).thenReturn(false);

		when(completeWorkDTO.getResolution()).thenReturn("work done");

		when(work.getPricingStrategy()).thenReturn(perHourPricingStrategy);
		when(completeWorkDTO.getHoursWorked()).thenReturn(-1.0);
		when(completeWorkDTO.getOverridePrice()).thenReturn(null);
		when(work.isDeliverableRequired()).thenReturn(false);

		List<ConstraintViolation> result = workValidationService.validateComplete(work, completeWorkDTO, false);
		assertEquals(1, result.size());
		assertEquals("hours_worked_required", result.get(0).getKey());
	}

	@Test
	public void validateCompleteWork_blendedPerHour_negativeHoursWorked() {
		when(work.isCheckinRequired()).thenReturn(false);
		when(work.isCheckinCallRequired()).thenReturn(false);

		when(completeWorkDTO.getResolution()).thenReturn("work done");

		when(work.getPricingStrategy()).thenReturn(blendedPerHourPricingStrategy);
		when(completeWorkDTO.getHoursWorked()).thenReturn(-1.0);
		when(completeWorkDTO.getOverridePrice()).thenReturn(null);
		when(work.isDeliverableRequired()).thenReturn(false);

		List<ConstraintViolation> result = workValidationService.validateComplete(work, completeWorkDTO, false);
		assertEquals(1, result.size());
		assertEquals("hours_worked_required", result.get(0).getKey());
	}

	@Test
	public void validateCompleteWork_blendedPerHour_negativeHoursWorked_priceOverride() {
		when(work.isCheckinRequired()).thenReturn(false);
		when(work.isCheckinCallRequired()).thenReturn(false);

		when(completeWorkDTO.getResolution()).thenReturn("work done");

		when(work.getPricingStrategy()).thenReturn(blendedPerHourPricingStrategy);
		when(completeWorkDTO.getHoursWorked()).thenReturn(-1.0);
		when(completeWorkDTO.getOverridePrice()).thenReturn(1.0);
		when(work.isDeliverableRequired()).thenReturn(false);

		List<ConstraintViolation> result = workValidationService.validateComplete(work, completeWorkDTO, false);
		assertEquals(1, result.size());
		assertEquals("hours_worked_required", result.get(0).getKey());
	}
}
