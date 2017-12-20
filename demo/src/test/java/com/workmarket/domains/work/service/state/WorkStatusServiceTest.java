package com.workmarket.domains.work.service.state;

import com.codahale.metrics.Meter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.dao.state.WorkStatusDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeliverableRequirementGroup;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.InvoicePaymentTransaction;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.payments.dao.StatementDAO;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.payments.service.AccountRegisterServicePaymentTermsImpl;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.dao.WorkPriceDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkActionRequestFactory;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkMilestonesService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.domains.work.service.workresource.WorkResourceDetailCache;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.analytics.cache.ScorecardCache;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.dto.StopPaymentDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.business.queue.WorkEventProcessQueue;
import com.workmarket.service.business.status.CloseWorkStatus;
import com.workmarket.service.business.wrapper.CloseWorkResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.summary.SummaryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkStatusServiceTest {
	@Mock AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Mock AccountRegisterServicePaymentTermsImpl accountRegisterServicePaymentTermsImpl;
	@Mock WorkSubStatusService workSubStatusService;
	@Mock WorkStatusDAO workStatusDAO;
	@Mock WorkActionRequestFactory workActionRequestFactory;
	@Mock WorkValidationService workValidationService;
	@Mock UserService userService;
	@Mock WorkNoteService workNoteService;
	@Mock UserNotificationService userNotificationService;
	@Mock RatingService ratingService;
	@Mock WorkChangeLogService workChangeLogService;
	@Mock DeliverableService deliverableService;
	@Mock AuthenticationService authenticationService;
	@Mock AssessmentService assessmentService;
	@Mock BillingService billingService;
	@Mock SummaryService summaryService;
	@Mock WorkAuditService workAuditService;
	@Mock WorkEventProcessQueue workEventProcessQueue;
	@Mock RegisterTransactionDAO registerTransactionDAO;
	@Mock WorkResourceService workResourceService;
	@Mock UserIndexer userIndexer;
	@Mock WorkNegotiationService workNegotiationService;
	@Mock WorkPriceDAO workPriceDAO;
	@Mock AccountPricingService accountPricingService;
	@Mock WebHookEventService webHookEventService;
	@Mock ScorecardCache scorecardCache;
	@Mock EventRouter eventRouter;
	@Mock EventFactory eventFactory;
	@Mock FeatureEvaluator featureEvaluator;
	@Mock ServiceMessageHelper messageHelper;
	@Mock WorkService workService;
	@Mock WorkResourceDetailCache workResourceDetailCache;
	@Mock WorkMilestonesService workMilestonesService;
	@Mock CompanyService companyService;
	@Mock StatementDAO statementDAO;
	@Mock WMMetricRegistryFacade wmMetricRegistryFacade;
	@Mock Meter meter;
	@InjectMocks WorkStatusServiceImpl workStatusService;

	Work work;
	User buyer, worker;
	Company company, resourceCompany;
	WorkResource workResource;
	WorkStatusType workStatusType;
	WorkActionRequest workActionRequest;
	CompleteWorkDTO completeWorkDTO;
	WorkMilestones workMileStones;
	List<ConstraintViolation> violation;
	PricingStrategy pricingStrategy;
	FullPricingStrategy fullPricingStrategy;
	ManageMyWorkMarket manageMyWorkMarket;
	Calendar paymentDate;
	Statement statement;
	FulfillmentStrategy fulfillmentStrategy;

	private static Long WORKER_ID = 21L;
	private static Long WORK_ID = 31L;
	private static Long WORK_RESOURCE_ID = 2L;
	private static Long STATEMENT_ID = 3L;
	public static final String WORK_STATUS_TYPE_SOME_STATUS = "some status";

	@Before
	public void setup() {
		manageMyWorkMarket = mock(ManageMyWorkMarket.class);
		work = mock(Work.class);
		buyer = mock(User.class);
		worker = mock(User.class);
		workResource = mock(WorkResource.class);
		workStatusType= mock(WorkStatusType.class);
		workActionRequest = mock(WorkActionRequest.class);
		completeWorkDTO = mock(CompleteWorkDTO.class);
		workMileStones = mock(WorkMilestones.class);
		pricingStrategy = mock(PricingStrategy.class);
		fullPricingStrategy = mock(FullPricingStrategy.class);
		company = mock(Company.class);
		resourceCompany = mock(Company.class);
		fulfillmentStrategy = mock(FulfillmentStrategy.class);

		violation = Lists.newArrayList();

		when(work.getId()).thenReturn(WORK_ID);
		when(work.getBuyer()).thenReturn(buyer);
		when(work.getWorkStatusType()).thenReturn(workStatusType);
		when(work.getPricingStrategy()).thenReturn(pricingStrategy);
		when(work.getFulfillmentStrategy()).thenReturn(fulfillmentStrategy);
		when(work.getCompany()).thenReturn(company);
		when(work.getManageMyWorkMarket()).thenReturn(manageMyWorkMarket);
		when(work.getStatementId()).thenReturn(STATEMENT_ID);

		when(pricingStrategy.getFullPricingStrategy()).thenReturn(fullPricingStrategy);
		when(buyer.getId()).thenReturn(1L);
		when(workActionRequest.getWorkId()).thenReturn(WORK_ID);
		when(workResource.getId()).thenReturn(WORK_RESOURCE_ID);
		when(workResource.getUser()).thenReturn(worker);
		when(worker.getCompany()).thenReturn(resourceCompany);
		when(worker.getId()).thenReturn(1L);

		when(workService.findWork(anyLong())).thenReturn(work);
		when(workValidationService.validateComplete(any(Work.class), any(CompleteWorkDTO.class), anyBoolean())).thenReturn(violation);
		doNothing().when(workSubStatusService).resolveAllInapplicableCustomWorkSubStatuses(work);
		doNothing().when(workSubStatusService).resolveSystemSubStatusByAction(anyLong(), anyString());
		when(workMilestonesService.findWorkMilestonesByWorkId(WORK_ID)).thenReturn(workMileStones);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(authenticationService.getCurrentUser()).thenReturn(buyer);
		when(workStatusType.getCode()).thenReturn(WORK_STATUS_TYPE_SOME_STATUS);
		when(workResourceService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(workResourceService.findWorkResourceById(anyLong())).thenReturn(workResource);
		when(workService.findActiveWorkerId(anyLong())).thenReturn(WORKER_ID);

		when(company.getId()).thenReturn(1L);
		when(company.getManageMyWorkMarket()).thenReturn(manageMyWorkMarket);
		when(resourceCompany.getId()).thenReturn(2L);

		paymentDate = mock(Calendar.class);
		statement = mock(Statement.class);
		when(statement.getRemainingBalance()).thenReturn(BigDecimal.ONE);
		when(billingService.findStatementById(work.getStatementId())).thenReturn(statement);

		when(fulfillmentStrategy.getBuyerTotalCost()).thenReturn(BigDecimal.ONE);
		when(wmMetricRegistryFacade.meter(any(String.class))).thenReturn(meter);
	}

	/* *************************************************************************** */
	/*                TRANSITION WORK TO ACCEPTED STATUS                           */
	/* *************************************************************************** */
	@Test
	public void transitionToAccepted_noDeliverables_matchingAccountServiceType() {
		// Deliverable requirements & deliverable requirement group
		DeliverableRequirementGroup deliverableRequirementGroup = mock(DeliverableRequirementGroup.class);
		when(work.isDeliverableRequired()).thenReturn(false);
		when(work.getDeliverableRequirementGroup()).thenReturn(deliverableRequirementGroup);

		// Account service type is the same
		AccountServiceType accountServiceType = mock(AccountServiceType.class);
		when(accountServiceType.getCode()).thenReturn("accountServiceTypeCode");
		when(workResource.getWork()).thenReturn(work);
		when(accountPricingService.findAccountServiceTypeConfiguration(eq(work))).thenReturn(accountServiceType);
		when(work.getAccountServiceType()).thenReturn(accountServiceType);

		workStatusService.transitionToAccepted(workActionRequest, workResource);

		verifyTransitionToAccepted();

		verify(deliverableService, never()).reactivateDeliverableDeadlineAndReminder(any(DeliverableRequirementGroup.class));
		verify(work, never()).setAccountServiceType(any(AccountServiceType.class));
	}

	@Test
	public void transitionToAccepted_noDeliverables_differentAccountServiceType() {
		// Deliverable requirements & deliverable requirement group
		DeliverableRequirementGroup deliverableRequirementGroup = mock(DeliverableRequirementGroup.class);
		when(work.isDeliverableRequired()).thenReturn(false);
		when(work.getDeliverableRequirementGroup()).thenReturn(deliverableRequirementGroup);

		// Account service type
		AccountServiceType accountServiceType1 = mock(AccountServiceType.class);
		AccountServiceType accountServiceType2 = mock(AccountServiceType.class);
		when(accountServiceType1.getCode()).thenReturn("accountServiceTypeCode1");
		when(accountServiceType2.getCode()).thenReturn("accountServiceTypeCode2");
		when(workResource.getWork()).thenReturn(work);
		when(accountPricingService.findAccountServiceTypeConfiguration(eq(work))).thenReturn(accountServiceType1);
		when(work.getAccountServiceType()).thenReturn(accountServiceType2);

		workStatusService.transitionToAccepted(workActionRequest, workResource);

		verifyTransitionToAccepted();

		verify(deliverableService, never()).reactivateDeliverableDeadlineAndReminder(any(DeliverableRequirementGroup.class));
		verify(work).setAccountServiceType(eq(accountServiceType1));
	}

	@Test
	public void transitionToAccepted_requiredDeliverables() {

		// Deliverable requirements & deliverable requirement group
		DeliverableRequirementGroup deliverableRequirementGroup = mock(DeliverableRequirementGroup.class);
		when(work.isDeliverableRequired()).thenReturn(true);
		when(work.getDeliverableRequirementGroup()).thenReturn(deliverableRequirementGroup);

		// Account service type is the same
		AccountServiceType accountServiceType = mock(AccountServiceType.class);
		when(accountServiceType.getCode()).thenReturn("accountServiceTypeCode");
		when(workResource.getWork()).thenReturn(work);
		when(accountPricingService.findAccountServiceTypeConfiguration(eq(work))).thenReturn(accountServiceType);
		when(work.getAccountServiceType()).thenReturn(accountServiceType);

		workStatusService.transitionToAccepted(workActionRequest, workResource);

		verifyTransitionToAccepted();

		verify(deliverableService, times(1)).reactivateDeliverableDeadlineAndReminder(eq(deliverableRequirementGroup));
		verify(work, never()).setAccountServiceType(any(AccountServiceType.class));
	}


	private void verifyTransitionToAccepted() {
		verify(workMilestonesService, times(1)).findWorkMilestonesByWorkId(eq(work.getId()));
		verify(summaryService, times(1)).saveWorkResourceHistorySummary(eq(workResource));
		verify(summaryService, times(1)).saveWorkHistorySummary(eq(work));
		verify(summaryService, times(1)).saveWorkStatusTransitionHistorySummary(
			eq(work),
			eq(WorkStatusType.newWorkStatusType(WorkStatusType.SENT)),
			eq(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE)), anyInt()
		);

		verify(workActionRequest, times(1)).setAuditType(eq(WorkAuditType.ACCEPT));
		verify(workAuditService, times(1)).auditAndReindexWork(eq(workActionRequest));


		verify(workSubStatusService, times(1)).resolveSystemSubStatusByAction(eq(WORK_ID), eq(WorkSubStatusType.RESOURCE_CANCELLED));
		verify(workSubStatusService, times(1)).resolveAllInapplicableCustomWorkSubStatuses(eq(work));
		verify(work).setWorkStatusType(eq(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE)));
		verify(workResourceDetailCache, times(1)).evict(WORK_ID);
		verify(wmMetricRegistryFacade, times(1)).meter(eq(WORK_STATUS_TYPE_SOME_STATUS));
	}

	@Test
	public void transitionToInComplete_clearDueOn() {
		workStatusService.transitionToIncomplete(work);
		verify(workMileStones).setDueOn(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void transitionClosedToPaid_withNullArguments_fail() {
		workStatusService.transitionClosedToPaid(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void transitionToFulfilledAndPaidFromInvoiceBulkPayment_withNullWork() {
		when(workService.findWork(anyLong())).thenReturn(null);
		workStatusService.transitionToFulfilledAndPaidFromInvoiceBulkPayment(WORK_ID, 1L, 1L);
		verify(workService).findWork(anyLong());
	}

	@Test(expected = IllegalArgumentException.class)
	public void transitionToFulfilledAndPaidFromInvoiceBulkPayment_withNullInvoicePaymentTransaction() {
		InvoicePaymentTransaction invoicePaymentTransaction = null;
		when(registerTransactionDAO.get(anyLong())).thenReturn(invoicePaymentTransaction);
		workStatusService.transitionToFulfilledAndPaidFromInvoiceBulkPayment(WORK_ID, 1L, 1L);
		verify(registerTransactionDAO).get(anyLong());
	}

	@Test
	public void transitionToFulfilledAndPaidFromInvoiceBulkPayment_withValidatePaidReturnsFalse_returnsViolations() {
		InvoicePaymentTransaction invoicePaymentTransaction = mock(InvoicePaymentTransaction.class);
		AbstractInvoice invoice = mock(AbstractInvoice.class);
		when(invoicePaymentTransaction.getInvoice()).thenReturn(invoice);
		when(registerTransactionDAO.get(anyLong())).thenReturn(invoicePaymentTransaction);
		when(workValidationService.validatePaid(any(Work.class))).thenReturn(Lists.<ConstraintViolation>newArrayList());
		when(accountRegisterServicePaymentTermsImpl.fulfillWorkPayment(any(Work.class), any(AbstractInvoice.class))).thenReturn(false);
		List<ConstraintViolation> violations = workStatusService.transitionToFulfilledAndPaidFromInvoiceBulkPayment(WORK_ID, 1L, 1L);
		verify(accountRegisterServicePaymentTermsImpl).fulfillWorkPayment(any(Work.class), any(AbstractInvoice.class));
		assertTrue(isNotEmpty(violations));
	}

	@Test
	public void transitionToFulfilledAndPaidFromInvoiceBulkPayment_withValidatePaidReturnsTrue_success() {
		InvoicePaymentTransaction invoicePaymentTransaction = mock(InvoicePaymentTransaction.class);
		AbstractInvoice invoice = mock(AbstractInvoice.class);
		when(invoice.getType()).thenReturn(Invoice.INVOICE_TYPE);
		when(invoicePaymentTransaction.getInvoice()).thenReturn(invoice);
		when(registerTransactionDAO.get(anyLong())).thenReturn(invoicePaymentTransaction);
		when(workValidationService.validatePaid(any(Work.class))).thenReturn(Lists.<ConstraintViolation>newArrayList());
		when(accountRegisterServicePaymentTermsImpl.fulfillWorkPayment(any(Work.class), any(AbstractInvoice.class))).thenReturn(true);
		when(userService.findUserById(anyLong())).thenReturn(mock(User.class));
		List<ConstraintViolation> violations = workStatusService.transitionToFulfilledAndPaidFromInvoiceBulkPayment(WORK_ID, 1L, 1L);
		verify(workResourceService).findActiveWorkResource(anyLong());
		assertTrue(isEmpty(violations));
	}

	@Test(expected = IllegalArgumentException.class)
	public void onPostCreateAssignment_withNullArguments_throwsException() {
		workStatusService.onPostCreateAssignment(null, null, null, null);
	}

	@Test
	public void onPostCreateAssignment_success() {
		workStatusService.onPostCreateAssignment(WORK_ID, 1L, null, 1L);
		verify(workMilestonesService).findWorkMilestonesByWorkId(WORK_ID);
		verify(workAuditService).auditWork(any(WorkActionRequest.class));
		verify(workAuditService).auditAndReindexWork(any(WorkActionRequest.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void flatWorkTransitionToCreated_withNullArguments_throwsException() {
		workStatusService.flatWorkTransitionToCreated(null, null, null);
	}

	@Test
	public void flatWorkTransitionToCreated_success() {
		workStatusService.flatWorkTransitionToCreated(WORK_ID, 1L, 1L);
		verify(workMilestonesService).findWorkMilestonesByWorkId(WORK_ID);
		verify(workAuditService).auditWork(any(WorkActionRequest.class));
		verify(workAuditService).auditAndReindexWork(any(WorkActionRequest.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void onPostTransitionToClosed_withNullArguments_throwsException() {
		workStatusService.onPostTransitionToClosed(WORK_ID, null);
	}

	@Test
	public void onPostTransitionToClosed_success() {
		workStatusService.onPostTransitionToClosed(WORK_ID, new CloseWorkDTO());
		verify(workSubStatusService).resolveAllInapplicableCustomWorkSubStatuses(any(Work.class));
		verify(workService).findActiveWorkerId(eq(WORK_ID));

		verify(eventRouter, atLeast(1)).sendEvent(any(WorkUpdateSearchIndexEvent.class));
		verify(eventRouter, atLeast(1)).sendEvent(any(UserSearchIndexEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void transitionClosedToPaid_withNullArguments_throwsException() {
		workStatusService.transitionClosedToPaid(null, null);
	}

	@Test
	public void transitionClosedToPaid_success() {
		List<ConstraintViolation> violations = workStatusService.transitionClosedToPaid(work, workResource);
		assertTrue(violations.isEmpty());
		verify(workValidationService).validatePaid(eq(work));
		verify(accountRegisterAuthorizationService).authorizeOnCompleteWork(eq(workResource));
		verify(billingService).generateInvoiceForWork(eq(work));
	}

	@Test(expected = IllegalArgumentException.class)
	public void transitionClosedToPaid_withBillingServiceFailing_exceptionBubbles() {
		when(billingService.generateInvoiceForWork(any(Work.class))).thenThrow(IllegalArgumentException.class);
		workStatusService.transitionClosedToPaid(work, workResource);
	}

	@Test
	public void transitionPaymentPendingToPaid_withNullArguments_returnsEmpty() {
		Map<String, List<ConstraintViolation>> map = workStatusService.transitionPaymentPendingToPaid(null);
		assertTrue(map.isEmpty());
	}

	@Test
	public void transitionPaymentPendingToPaid_success() {
		Map<Long, Long> paidAssignmentsMap  = Maps.newHashMap();
		paidAssignmentsMap.put(WORK_ID, 1L);
		when(accountRegisterServicePaymentTermsImpl.payPaymentTerms(anyList())).thenReturn(paidAssignmentsMap);
		Map<String, List<ConstraintViolation>> map = workStatusService.transitionPaymentPendingToPaid(Lists.newArrayList(WORK_ID));
		assertTrue(map.isEmpty());
		verify(accountRegisterServicePaymentTermsImpl).payPaymentTerms(anyList());
		verify(eventRouter, atLeast(1)).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void transitionToVoid_withNullArguments_returnsEmpty() {
		workStatusService.transitionToVoid(null);
	}

	@Test
	public void transitionToVoid_success() {
		List<ConstraintViolation> violations = workStatusService.transitionToVoid(new WorkActionRequest().setWorkId(WORK_ID));
		assertTrue(isEmpty(violations));
		verify(workValidationService).validateVoid(eq(WORK_ID));
		verify(workSubStatusService).resolveAllInapplicableCustomWorkSubStatuses(any(Work.class));
	}

	@Test
	public void updateWorkAccountServiceType_success() {
		AccountServiceType accountServiceType = mock(AccountServiceType.class);
		when(accountServiceType.getCode()).thenReturn(AccountServiceType.VENDOR_OF_RECORD);

		AccountServiceType workAccountServiceType = mock(AccountServiceType.class);
		when(workAccountServiceType.getCode()).thenReturn(AccountServiceType.NONE);
		when(accountPricingService.findAccountServiceTypeConfiguration(any(Work.class))).thenReturn(accountServiceType);
		when(work.getAccountServiceType()).thenReturn(workAccountServiceType);


		workStatusService.updateWorkAccountServiceType(work);
		verify(accountPricingService).findAccountServiceTypeConfiguration(any(Work.class));
		verify(eventRouter).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void transitionToDeclined_withNullArguments_returnsEmpty() {
		workStatusService.transitionToDeclined(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void transitionFromAbandonedToOpenWork_withNullArguments_returnsEmpty() {
		workStatusService.transitionFromAbandonedToOpenWork(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void transitionToStopPayment_withNullArguments_returnsEmpty() {
		workStatusService.transitionToStopPayment(null, null);
	}

	@Test
	public void transitionToStopPayment_success() {
		when(billingService.voidWorkInvoice(any(Work.class))).thenReturn(true);
		List<ConstraintViolation> violations = workStatusService.transitionToStopPayment(new WorkActionRequest().setWorkId(WORK_ID), new StopPaymentDTO());
		assertTrue(isEmpty(violations));
		verify(workSubStatusService).resolveAllInapplicableCustomWorkSubStatuses(any(Work.class));
		verify(billingService).voidWorkInvoice(any(Work.class));
		verify(userNotificationService).onWorkStopPayment(eq(WORK_ID), anyString());
		verify(eventRouter, times(1)).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void transitionToClosed_withNullArguments_fails() {
		workStatusService.transitionToClosed(null, null);
	}

	@Test
	public void transitionToClosed_success() {
		WorkActionRequest workActionRequest = new WorkActionRequest();
		workActionRequest.setWorkId(WORK_ID);
		CloseWorkResponse closeWorkResponse = workStatusService.transitionToClosed(workActionRequest, new CloseWorkDTO());
		assertNotNull(closeWorkResponse);
		assertFalse(closeWorkResponse.isFailure());
		verify(workValidationService).validateClosed(any(Work.class));
		verify(workResourceService).findActiveWorkResource(anyLong());
	}

	@Test
	public void transitionToClosed_withViolations_success() {
		when(workValidationService.validateClosed(any(Work.class))).thenReturn(Lists.newArrayList(new ConstraintViolation(MessageKeys.Work.NOT_AUTHORIZED)));

		WorkActionRequest workActionRequest = new WorkActionRequest();
		workActionRequest.setWorkId(WORK_ID);
		CloseWorkResponse closeWorkResponse = workStatusService.transitionToClosed(workActionRequest, new CloseWorkDTO());
		assertNotNull(closeWorkResponse);
		assertTrue(closeWorkResponse.isFailure());
		verify(workResourceService, never()).findActiveWorkResource(anyLong());
	}

	@Test
	public void transitionToClosed_withAutoPay_success() {
		when(manageMyWorkMarket.getAutoPayEnabled()).thenReturn(false);
		when(work.hasPaymentTerms()).thenReturn(false);

		WorkActionRequest workActionRequest = new WorkActionRequest();
		workActionRequest.setWorkId(WORK_ID);
		CloseWorkResponse closeWorkResponse = workStatusService.transitionToClosed(workActionRequest, new CloseWorkDTO());
		assertNotNull(closeWorkResponse);
		assertEquals(closeWorkResponse.getStatus(), CloseWorkStatus.CLOSED_IMMEDIATELY);
	}

	@Test
	public void transitionToClosed_withRating_success() {
		CloseWorkDTO closeWorkDTO = new CloseWorkDTO();
		RatingDTO ratingDTO = new RatingDTO();
		ratingDTO.setValue(80);
		closeWorkDTO.setRating(ratingDTO);

		WorkActionRequest workActionRequest = new WorkActionRequest();
		workActionRequest.setWorkId(WORK_ID);
		CloseWorkResponse closeWorkResponse = workStatusService.transitionToClosed(workActionRequest, closeWorkDTO);
		assertNotNull(closeWorkResponse);
		verify(ratingService).updateLatestRatingForUserForWork(anyLong(), anyLong(), any(RatingDTO.class));
	}

	@Test
	public void transitionToClosed_withAutoRating_success() {
		CloseWorkDTO closeWorkDTO = new CloseWorkDTO();
		RatingDTO excellentRating = new RatingDTO(Rating.EXCELLENT, Rating.EXCELLENT, Rating.EXCELLENT, Rating.EXCELLENT, "");
		when(manageMyWorkMarket.getAutoRateEnabledFlag()).thenReturn(true);

		WorkActionRequest workActionRequest = new WorkActionRequest();
		workActionRequest.setWorkId(WORK_ID);
		CloseWorkResponse closeWorkResponse = workStatusService.transitionToClosed(workActionRequest, closeWorkDTO);
		assertNotNull(closeWorkResponse);
		verify(ratingService).updateLatestRatingForUserForWork(anyLong(), anyLong(), eq(excellentRating));
	}

	@Test
	public void transitionToClosed_offlinePayment() {
		when(workService.isOfflinePayment(work)).thenReturn(true);
		CloseWorkDTO closeWorkDTO = new CloseWorkDTO();

		WorkActionRequest workActionRequest = new WorkActionRequest();
		workActionRequest.setWorkId(WORK_ID);
		workStatusService.transitionToClosed(workActionRequest, closeWorkDTO);

		verify(accountRegisterAuthorizationService, times(1)).authorizeOnCompleteWork(eq(workResource));
	}

	@Test
	public void transitionToClosed_onlinePayment() {
		when(workService.isOfflinePayment(work)).thenReturn(false);
		when(work.hasPaymentTerms()).thenReturn(true);

		CloseWorkDTO closeWorkDTO = new CloseWorkDTO();

		WorkActionRequest workActionRequest = new WorkActionRequest();
		workActionRequest.setWorkId(WORK_ID);
		workStatusService.transitionToClosed(workActionRequest, closeWorkDTO);

		verify(accountRegisterAuthorizationService, never()).authorizeOnCompleteWork(eq(workResource));
	}

	@Test
	public void onPostPayAssignment_ifWorkHasNoFirstPaidAssignment_setFirstPaidAssignmentOn() {
		when(company.getFirstPaidAssignmentOn()).thenReturn(null);

		workStatusService.onPostPayAssignment(work, WORK_RESOURCE_ID, paymentDate, buyer);

		verify(company).setFirstPaidAssignmentOn(paymentDate);
	}
}
