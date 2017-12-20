package com.workmarket.domains.work.facade.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.service.WorkActionRequestFactory;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkMilestonesService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkInvoiceGenerateEvent;
import com.workmarket.service.business.event.work.WorkInvoiceSendType;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.URIService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.thrift.core.Status;
import com.workmarket.thrift.work.WorkResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkFacadeServiceTest {

	@Mock WorkService workService;
	@Mock UserService userService;
	@Mock URIService uriService;
	@Mock EventRouter eventRouter;
	@Mock WorkMilestonesService workMilestonesService;
	@Mock AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Mock WorkActionRequestFactory workActionRequestFactory;
	@Mock WorkValidationService workValidationService;
	@Mock AuthenticationService authenticationService;
	@Mock WorkResourceService workResourceService;
	@Mock WebHookEventService webHookEventService;
	@Mock WorkSearchService workSearchService;
	@Mock WorkBundleService workBundleService;
	@Mock Status status;
	@InjectMocks WorkFacadeServiceImpl workFacadeService;

	CancelWorkDTO cancelWorkDTO;
	UnassignDTO unassignDto;
	WorkBundleDTO workBundleDto;
	Work work;
	com.workmarket.thrift.work.Work tWork;
	User buyer, worker;
	Company company, workerCompany;
	WorkActionRequest workActionRequest;
	WorkResource workResource;
	WorkResponse workResponse;
	WorkStatusType workStatusType;
	PricingStrategy pricingStrategy;
	FullPricingStrategy fullPricingStrategy;
	Calendar dueOn;
	Invoice invoice;
	List<ConstraintViolation> violations;

	private static final Long WORK_ID = 1L, COMPANY_ID = 2L, BUYER_ID = 3L, INVOICE_ID = 4L, WORKER_COMPANY_ID = 5L,
	                          WORK_BUNDLE_DTO_ID = 6L;
	private static final double WORK_PRICE = 1d;
	private static final String WORK_NUMBER = "1234";

	@Before
	public void setUp() {
		cancelWorkDTO = mock(CancelWorkDTO.class);
		when(cancelWorkDTO.getCancellationReasonTypeCode()).thenReturn(CancellationReasonType.BUYER_CANCELLED);
		when(cancelWorkDTO.getWorkId()).thenReturn(WORK_ID);
		when(cancelWorkDTO.getPrice()).thenReturn(WORK_PRICE);
		when(cancelWorkDTO.isPaid()).thenReturn(true);

		unassignDto = new UnassignDTO();

		workBundleDto = mock(WorkBundleDTO.class);
		when(workBundleDto.getId()).thenReturn(WORK_BUNDLE_DTO_ID);

		company = mock(Company.class);
		workerCompany = mock(Company.class);
		when(company.getId()).thenReturn(COMPANY_ID);
		when(workerCompany.getId()).thenReturn(WORKER_COMPANY_ID);

		buyer = mock(User.class);
		when(buyer.getId()).thenReturn(BUYER_ID);

		workStatusType = mock(WorkStatusType.class);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.CANCELLED);

		pricingStrategy = mock(PricingStrategy.class);
		fullPricingStrategy = mock(FullPricingStrategy.class);
		when(pricingStrategy.getFullPricingStrategy()).thenReturn(fullPricingStrategy);

		dueOn = mock(Calendar.class);

		invoice = mock(Invoice.class);
		when(invoice.getId()).thenReturn(INVOICE_ID);

		work = mock(Work.class);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getBuyer()).thenReturn(buyer);
		when(work.getWorkNumber()).thenReturn(WORK_NUMBER);
		when(work.getCompany()).thenReturn(company);
		when(work.getWorkStatusType()).thenReturn(workStatusType);
		when(work.getPricingStrategy()).thenReturn(pricingStrategy);
		when(work.getDueDate()).thenReturn(dueOn);
		when(workService.findWork(WORK_ID)).thenReturn(work);

		workActionRequest = mock(WorkActionRequest.class);
		when(workActionRequest.getWorkId()).thenReturn(WORK_ID);
		when(workActionRequestFactory.create(WORK_ID, WorkAuditType.CANCEL)).thenReturn(workActionRequest);

		workResource = mock(WorkResource.class);
		worker = mock(User.class);
		when(workResource.getUser()).thenReturn(worker);
		when(workResource.getWork()).thenReturn(work);
		when(worker.getCompany()).thenReturn(workerCompany);
		when(workResourceService.findActiveWorkResource(WORK_ID)).thenReturn(workResource);

		when(authenticationService.getCurrentUserId()).thenReturn(BUYER_ID);

		tWork = mock(com.workmarket.thrift.work.Work.class);
		when(tWork.getId()).thenReturn(WORK_ID);

		workResponse = mock (WorkResponse.class);
		when(workResponse.getWork()).thenReturn(tWork);
		when(workResponse.getWorkBundleParent()).thenReturn(workBundleDto);

		violations = mock(ArrayList.class);
		when(violations.isEmpty()).thenReturn(true);

		when(workValidationService.validateCancel(work)).thenReturn(violations);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cancelWork_noCancellationReason_throwException() throws Exception {
		when(cancelWorkDTO.getCancellationReasonTypeCode()).thenReturn(null);

		workFacadeService.cancelWork(cancelWorkDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cancelWork_priceIsNull_throwException() throws Exception {
		when(cancelWorkDTO.getPrice()).thenReturn(null);

		workFacadeService.cancelWork(cancelWorkDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cancelWork_priceIsLessThanZero_throwException() throws Exception {
		when(cancelWorkDTO.getPrice()).thenReturn(-1d);

		workFacadeService.cancelWork(cancelWorkDTO);
	}

	@Test
	public void cancelWork_validateCancel() throws Exception {
		workFacadeService.cancelWork(cancelWorkDTO);

		verify(workValidationService).validateCancel(work);
	}

	@Test
	public void cancelWork_ifValidationReturnsErrors_thenEarlyReturn() throws Exception {
		violations = Lists.newArrayList(new ConstraintViolation(""));

		workFacadeService.cancelWork(cancelWorkDTO);

		verify(workService, never()).transitionWorkToCanceledState(
			eq(WORK_ID), eq(cancelWorkDTO),
			eq(workActionRequest), eq(WorkStatusType.newWorkStatusType(WorkStatusType.CANCELLED))
		);
	}

	@Test
	public void cancelWork_isPaid_hasPaymentTerms_statusIsCancelledPaymentPending() throws Exception {
		when(work.hasPaymentTerms()).thenReturn(true);

		workFacadeService.cancelWork(cancelWorkDTO);

		ArgumentCaptor<WorkStatusType> workStatusCaptor1 = ArgumentCaptor.forClass(WorkStatusType.class);
		verify(workService).transitionWorkToCanceledState(
			eq(WORK_ID), eq(cancelWorkDTO),
			eq(workActionRequest), workStatusCaptor1.capture()
		);
		assertEquals(WorkStatusType.CANCELLED_PAYMENT_PENDING, workStatusCaptor1.getValue().getCode());
	}

	@Test
	public void cancelWork_isPaid_noPaymentTerms_statusIsCancelledWithPay() throws Exception {
		workFacadeService.cancelWork(cancelWorkDTO);

		ArgumentCaptor<WorkStatusType> workStatusCaptor1 = ArgumentCaptor.forClass(WorkStatusType.class);
		verify(workService).transitionWorkToCanceledState(
			eq(WORK_ID), eq(cancelWorkDTO),
			eq(workActionRequest), workStatusCaptor1.capture()
		);
		assertEquals(WorkStatusType.CANCELLED_WITH_PAY, workStatusCaptor1.getValue().getCode());
	}

	@Test
	public void cancelWork_isNotPaid_statusIsCancelled() throws Exception {
		when(cancelWorkDTO.isPaid()).thenReturn(false);

		workFacadeService.cancelWork(cancelWorkDTO);

		ArgumentCaptor<WorkStatusType> workStatusCaptor1 = ArgumentCaptor.forClass(WorkStatusType.class);
		verify(workService).transitionWorkToCanceledState(
			eq(WORK_ID), eq(cancelWorkDTO),
			eq(workActionRequest), workStatusCaptor1.capture()
		);
		assertEquals(WorkStatusType.CANCELLED, workStatusCaptor1.getValue().getCode());
	}

	@Test
	public void cancelWork_isPaid_authorizeOnCompleteWork() throws Exception {
		workFacadeService.cancelWork(cancelWorkDTO);

		verify(accountRegisterAuthorizationService).authorizeOnCompleteWork(WORK_ID, WORK_PRICE);
	}

	@Test
	public void cancelWork_isNotPaid_voidWork() throws Exception {
		when(cancelWorkDTO.isPaid()).thenReturn(false);

		workFacadeService.cancelWork(cancelWorkDTO);

		verify(accountRegisterAuthorizationService).voidWork(work);
	}

	public void verifyErrorMessageWasReturned() {
		ArgumentCaptor<ConstraintViolation> constraintViolationArgumentCaptor = ArgumentCaptor.forClass(ConstraintViolation.class);
		verify(violations).add(constraintViolationArgumentCaptor.capture());
		assertEquals(MessageKeys.Work.UNEXPECTED_ERROR, constraintViolationArgumentCaptor.getValue().getKey());
	}

	@Test
	public void cancelWork_isPaid_exceptionThrownOnAuthorizeCompleteWork_doNotRollbackAccountRegisterAuth() {
		doThrow(Exception.class).when(accountRegisterAuthorizationService).authorizeOnCompleteWork(WORK_ID, WORK_PRICE);

		workFacadeService.cancelWork(cancelWorkDTO);

		verify(accountRegisterAuthorizationService, never()).acceptWork(WORK_ID);
		verifyErrorMessageWasReturned();
	}

	@Test
	public void cancelWork_isPaid_exceptionThrownOnTransitionToCancel_rollbackAccountRegisterAuth() {
		doThrow(Exception.class).when(workService).transitionWorkToCanceledState(
			eq(WORK_ID), eq(cancelWorkDTO),
			eq(workActionRequest), any(WorkStatusType.class)
		);

		workFacadeService.cancelWork(cancelWorkDTO);

		verify(accountRegisterAuthorizationService).acceptWork(WORK_ID);
		verifyErrorMessageWasReturned();
	}

	@Test
	public void cancelWork_isNotPaid_exceptionThrownOnVoidWork_doNotRollbackAccountRegisterAuth() {
		when(cancelWorkDTO.isPaid()).thenReturn(false);
		doThrow(Exception.class).when(accountRegisterAuthorizationService).voidWork(work);

		workFacadeService.cancelWork(cancelWorkDTO);

		verify(accountRegisterAuthorizationService, never()).acceptWork(WORK_ID);
		verifyErrorMessageWasReturned();
	}

	@Test
	public void cancelWork_isNotPaid_exceptionThrownOnTransitionToCancel_rollbackAccountRegisterAuth() {
		when(cancelWorkDTO.isPaid()).thenReturn(false);
		doThrow(Exception.class).when(workService).transitionWorkToCanceledState(
			eq(WORK_ID), eq(cancelWorkDTO),
			eq(workActionRequest), any(WorkStatusType.class)
		);

		workFacadeService.cancelWork(cancelWorkDTO);

		verify(accountRegisterAuthorizationService).acceptWork(WORK_ID);
		verifyErrorMessageWasReturned();
	}

	@Test
	public void cancelWork_sendOnWorkCancelledWebHook() throws Exception {
		workFacadeService.cancelWork(cancelWorkDTO);

		verify(webHookEventService).onWorkCancelled(work.getId(), work.getCompany().getId());
	}

	@Test
	public void cancelWork_ifCancelledWithPay_sendOnWorkPaidWebHook() throws Exception {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.CANCELLED_WITH_PAY);

		workFacadeService.cancelWork(cancelWorkDTO);

		verify(webHookEventService).onWorkCancelled(work.getId(), work.getCompany().getId());
	}

	@Test
	public void cancelWork_ifNotCancelledWithPay_doNotSendOnWorkPaidWebHook() throws Exception {
		workFacadeService.cancelWork(cancelWorkDTO);

		verify(webHookEventService, never()).onWorkPaid(work.getId(), work.getCompany().getId());
	}

	@Test
	public void cancelWork_ifWorkHasInvoice_sendInvoiceGeneratedEvent() throws Exception {
		workFacadeService.cancelWork(cancelWorkDTO);

		ArgumentCaptor<WorkInvoiceGenerateEvent> eventArgumentCaptor = ArgumentCaptor.forClass(WorkInvoiceGenerateEvent.class);
		verify(eventRouter, atLeastOnce()).sendEvent(eventArgumentCaptor.capture());
		for (Event event : eventArgumentCaptor.getAllValues()) {
			if (event instanceof WorkInvoiceGenerateEvent) {
				WorkInvoiceGenerateEvent me = (WorkInvoiceGenerateEvent) event;
				assertEquals(me.getInvoiceId(), invoice.getId());
				assertEquals(me.getWorkId(), work.getId());
				assertEquals(me.getSendType(), WorkInvoiceSendType.ALL);
			}
		}
	}

	@Test
	public void cancelWork_sendWorkUpdateSearchIndexEvent() throws Exception {
		workFacadeService.cancelWork(cancelWorkDTO);

		ArgumentCaptor<WorkUpdateSearchIndexEvent> eventArgumentCaptor = ArgumentCaptor.forClass(WorkUpdateSearchIndexEvent.class);
		verify(eventRouter, atLeastOnce()).sendEvent(eventArgumentCaptor.capture());
		for (Event event : eventArgumentCaptor.getAllValues()) {
			if (event instanceof WorkUpdateSearchIndexEvent) {
				WorkUpdateSearchIndexEvent me = (WorkUpdateSearchIndexEvent) event;
				assertEquals(me.getWorkIds().get(0), work.getId());
			}
		}
	}

	@Test
	public void cancelWork_ifMasqUser_sendUserSearchIndexEvent() throws Exception {
		Long masqUserId = 10L;
		when(workActionRequest.getMasqueradeId()).thenReturn(masqUserId);

		workFacadeService.cancelWork(cancelWorkDTO);

		ArgumentCaptor<UserSearchIndexEvent> eventArgumentCaptor = ArgumentCaptor.forClass(UserSearchIndexEvent.class);
		verify(eventRouter, atLeastOnce()).sendEvent(eventArgumentCaptor.capture());
		assertTrue(eventArgumentCaptor.getAllValues().get(1).getUserIds().contains(masqUserId));
	}
}
