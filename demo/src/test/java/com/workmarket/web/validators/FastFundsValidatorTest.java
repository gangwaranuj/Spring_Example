package com.workmarket.web.validators;

import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class FastFundsValidatorTest {

	@Mock MessageBundleHelper messageBundleHelper;
	@Mock BillingService billingService;
	@Mock WorkService workService;
	@Mock FeatureEvaluator featureEvaluator;
	@Mock CompanyService companyService;
	@Spy @InjectMocks FastFundsValidator fastFundsValidator;

	private static final Long INVOICE_ID = 1L, WORK_ID = 1L, COMPANY_ID = 1L,
	INVOICE_DUE_DATE_IN_A_MILLI = 1457723430L;
	private static final String WORK_NUMBER = "1";
	private MessageBundle messageBundle;
	private Company company;

	@Mock Invoice invoice;
	@Mock Calendar invoiceDueDate;

	@Before
	public void setup() {
		messageBundle = mock(MessageBundle.class);

		invoiceDueDate = Calendar.getInstance();
		invoiceDueDate.setTimeInMillis(INVOICE_DUE_DATE_IN_A_MILLI);

		company = mock(Company.class);
		when(company.getId()).thenReturn(COMPANY_ID);

		invoice = mock(Invoice.class);
		when(invoice.getId()).thenReturn(INVOICE_ID);
		when(invoice.isPaymentPending()).thenReturn(true);
		when(invoice.getFastFundedOn()).thenReturn(null);
		when(invoice.getDeleted()).thenReturn(false);
		when(invoice.getDueDate()).thenReturn(invoiceDueDate);
		when(invoice.getCompany()).thenReturn(company);

		Calendar now = (Calendar) invoiceDueDate.clone();
		now.add(Calendar.HOUR, -billingService.getFastFundsAvailabilityThresholdHours());
		when(fastFundsValidator.createCalendarForNow()).thenReturn(now);

		when(billingService.validateAccessToInvoice(anyLong())).thenReturn(true);
		when(billingService.findInvoiceByWorkId(anyLong())).thenReturn(invoice);
		when(billingService.findInvoiceById(anyLong())).thenReturn(invoice);
		when(billingService.validateAccessToFastFundInvoice(INVOICE_ID)).thenReturn(true);
		when(workService.findWorkId(anyString())).thenReturn(WORK_ID);
		when(companyService.isFastFundsEnabled(any(Company.class))).thenReturn(true);
		when(fastFundsValidator.createMessageBundle()).thenReturn(messageBundle);
	}

	@Test
	public void isInvoiceFastFundable_noErrorsFound_returnTrue() {
		assertTrue(fastFundsValidator.isInvoiceFastFundable(INVOICE_ID));
	}

	@Test
	public void isInvoiceFastFundable_errorsFound_returnFalse() {
		when(messageBundle.hasErrors()).thenReturn(true);

		assertFalse(fastFundsValidator.isInvoiceFastFundable(INVOICE_ID));
	}

	@Test
	public void isAssignmentFastFundable_noErrorsFound_returnTrue() {
		assertTrue(fastFundsValidator.isWorkFastFundable(WORK_NUMBER));
	}

	@Test
	public void isAssignmentFastFundable_errorsFound_returnFalse() {
		when(messageBundle.hasErrors()).thenReturn(true);

		assertFalse(fastFundsValidator.isWorkFastFundable(WORK_NUMBER));
	}

	@Test
	public void validate_givenWorkNumber_workIdCouldNotBeFound_errorsFound() {
		when(workService.findWorkId(WORK_NUMBER)).thenReturn(null);

		fastFundsValidator.validate(WORK_NUMBER, messageBundle);

		verify(messageBundleHelper).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_givenWorkNumber_invoiceCouldNotBeFound_errorsFound() {
		when(billingService.findInvoiceByWorkId(WORK_ID)).thenReturn(null);

		fastFundsValidator.validate(WORK_NUMBER, messageBundle);

		verify(messageBundleHelper).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_givenWorkNumber_noErrorsFound() {
		fastFundsValidator.validate(WORK_NUMBER, messageBundle);

		verify(messageBundleHelper, never()).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_invoiceCanBeFastFunded_noErrorsFound() {
		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper, never()).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_invoiceCannotBeFound_errorsFound() {
		when(billingService.findInvoiceById(anyLong())).thenReturn(null);

		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_invoiceIsNotInPaymentPendingStatus_errorsFound() {
		when(invoice.isPaymentPending()).thenReturn(false);

		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_invoiceHasAlreadyBeenFastFunded_errorsFound() {
		when(invoice.getFastFundedOn()).thenReturn(Calendar.getInstance());

		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_invoiceIsDeleted_errorsFound() {
		when(invoice.getDeleted()).thenReturn(true);

		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_invoicingCompanyDoesNotHaveFastFundsEnabled_errorsFound() {
		when(companyService.isFastFundsEnabled(any(Company.class))).thenReturn(false);
		when(featureEvaluator.hasFeature(anyLong(), anyString())).thenReturn(false);

		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_invoiceDoesNotHaveADueDate_errorsFound() {
		when(invoice.getDueDate()).thenReturn(null);

		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_fastFundsDeadlinePassed_errorsFound() {
		Calendar oneMinuteAfterFastFundsDeadline = (Calendar) invoiceDueDate.clone();
		oneMinuteAfterFastFundsDeadline.add(Calendar.HOUR, -billingService.getFastFundsAvailabilityThresholdHours());
		oneMinuteAfterFastFundsDeadline.add(Calendar.MINUTE, 1);

		when(fastFundsValidator.createCalendarForNow()).thenReturn(oneMinuteAfterFastFundsDeadline);

		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_invoiceFastFundedOnDeadline_noErrorsFound() {
		Calendar oneMinuteAfterFastFundsDeadline = (Calendar) invoiceDueDate.clone();
		oneMinuteAfterFastFundsDeadline.add(Calendar.HOUR, -billingService.getFastFundsAvailabilityThresholdHours());

		when(fastFundsValidator.createCalendarForNow()).thenReturn(oneMinuteAfterFastFundsDeadline);

		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper, never()).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_invoiceFastFundedMinuteBeforeDeadline_noErrorsFound() {
		Calendar oneMinuteAfterFastFundsDeadline = (Calendar) invoiceDueDate.clone();
		oneMinuteAfterFastFundsDeadline.add(Calendar.HOUR, -billingService.getFastFundsAvailabilityThresholdHours());
		oneMinuteAfterFastFundsDeadline.add(Calendar.MINUTE, -1);

		when(fastFundsValidator.createCalendarForNow()).thenReturn(oneMinuteAfterFastFundsDeadline);

		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper, never()).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_givenInvoiceId_userDoesNotHaveAccessToFastFundInvoice_errorsFound() {
		when(billingService.validateAccessToFastFundInvoice(anyLong())).thenReturn(false);

		fastFundsValidator.validate(INVOICE_ID, messageBundle);

		verify(messageBundleHelper).addError(eq(messageBundle), anyString());
	}
}
