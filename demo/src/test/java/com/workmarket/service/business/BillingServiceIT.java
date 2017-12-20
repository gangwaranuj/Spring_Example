package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.payment.AccountingProcessTime;
import com.workmarket.domains.model.account.payment.BiweeklyPaymentDays;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentCycle;
import com.workmarket.domains.model.account.payment.PaymentTermsDays;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriodType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoicePagination;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.PaymentFulfillmentStatusType;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.SubscriptionInvoiceType;
import com.workmarket.domains.model.invoice.item.InvoiceLineItemType;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkMilestonesService;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.PaymentConfigurationDTO;
import com.workmarket.service.business.dto.StopPaymentDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.service.business.dto.invoice.InvoiceDTO;
import com.workmarket.service.business.dto.invoice.InvoiceLineItemDTO;
import com.workmarket.service.business.dto.invoice.InvoiceSummaryDTO;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.payment.InvoiceAlreadyPaidException;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class BillingServiceIT extends BaseServiceIT {

	@Autowired private WorkMilestonesService workMilestonesService;
	@Autowired private BillingService billingService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterService;

	@Test
	public void payInvoice_withEmployeeWithCashBalance_deductsAvailableFunds() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		BigDecimal availableCashBefore = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());
		User contractor = newContractor();
		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);
		assertTrue(work.hasPaymentTerms());

		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		Invoice invoice = billingService.findInvoiceByWorkId(work.getId());
		assertNotNull(invoice);

		work = workService.findWork(work.getId());
		assertTrue(work.isPaymentPending());

		Map<String, List<ConstraintViolation>> violations = billingService.payInvoice(employee.getId(), invoice.getId());
		assertTrue(violations.isEmpty());

		invoice = billingService.findInvoiceByWorkId(work.getId());
		assertEquals(invoice.getInvoiceStatusType().getCode(), InvoiceStatusType.PAID);
		assertEquals(invoice.getPaymentFulfillmentStatusType().getCode(), PaymentFulfillmentStatusType.FULFILLED);

		BigDecimal availableCashAfter = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());

		assertEquals(invoice.getBalance().add(availableCashAfter).compareTo(availableCashBefore), 0);
	}

	@Test
	public void assignmentWithPaymentTerms_cancelled_returnsPaymentTerms() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		AccountRegister accountRegister = accountRegisterService.findDefaultRegisterForCompany(employee.getCompany().getId());

		BigDecimal pendingCommitmentsBefore = accountRegisterService.findPaymentTermsCommitmentBalance(accountRegister.getId());
		assertTrue(pendingCommitmentsBefore.intValue() == 0);

		User contractor = newContractor();
		Work work1 = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);
		assertTrue(work1.hasPaymentTerms());
		workService.acceptWork(contractor.getId(), work1.getId());

		BigDecimal pendingCommitmentsAfter = accountRegisterService.findPaymentTermsCommitmentBalance(accountRegister.getId());
		assertTrue(pendingCommitmentsAfter.abs().intValue() == 100);

		CancelWorkDTO cancelWorkDTO = new CancelWorkDTO();
		cancelWorkDTO.setNote("NOTE");
		cancelWorkDTO.setPrice(0d);
		cancelWorkDTO.setWorkId(work1.getId());
		cancelWorkDTO.setCancellationReasonTypeCode(CancellationReasonType.BUYER_CANCELLED);
		workService.cancelWork(cancelWorkDTO);

		pendingCommitmentsAfter = accountRegisterService.findPaymentTermsCommitmentBalance(accountRegister.getId());
		assertTrue(pendingCommitmentsAfter.intValue() == 0);
	}

	@Test(expected = InsufficientFundsException.class)
	public void payInvoice_withEmployeeWithoutCash_throwsInsufficientFundsException() throws Exception {
		User employee = newFirstEmployeeWithNOCashBalanceAndPaymentTerms();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		assertTrue(work.hasPaymentTerms());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		work = workService.findWork(work.getId());
		// Test the dueDate
		Calendar dueDate = work.getClosedOn();
		dueDate.add(Calendar.DATE, work.getPaymentTermsDays());
		dueDate = DateUtilities.getCalendarWithLastMinuteOfDay(dueDate, Constants.EST_TIME_ZONE);
		assertTrue(work.getInvoice().getDueDate().compareTo(dueDate) == 0);

		billingService.payInvoice(employee.getId(), work.getInvoice().getId());
	}

	@Test
	public void generateInvoice_withApprovedWork_createsInvoice() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		Invoice invoice = billingService.findInvoiceByWorkId(work.getId());
		assertNotNull(invoice);

		assertNotNull(invoice.getInvoiceNumber());
	}

	@Test
	public void saveInvoiceSummary_withMoreThanOneInvoice_success() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work1 = newWorkWithPaymentTerms(employee.getId(), 30);
		Work work2 = newWorkWithPaymentTerms(employee.getId(), 30);

		workRoutingService.addToWorkResources(work1.getId(), contractor.getId());
		workRoutingService.addToWorkResources(work2.getId(), contractor.getId());

		workService.acceptWork(contractor.getId(), work1.getId());
		workService.acceptWork(contractor.getId(), work2.getId());
		workService.updateWorkProperties(work1.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.updateWorkProperties(work2.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work1.getId(), new CompleteWorkDTO());
		workService.completeWork(work2.getId(), new CompleteWorkDTO());

		workService.closeWork(work1.getId());
		workService.closeWork(work2.getId());

		work1 = workService.findWork(work1.getId());
		assertTrue(work1.isPaymentPending());
		assertNotNull(work1.getDueOn());

		work2 = workService.findWork(work2.getId());
		assertTrue(work2.isPaymentPending());
		assertNotNull(work2.getDueOn());

		InvoicePagination pagination = new InvoicePagination(true);
		pagination = billingService.findAllInvoicesByCompany(employee.getCompany().getId(), pagination);
		assertNotNull(pagination);
		assertEquals(pagination.getResults().size(), 2);

		authenticationService.setCurrentUser(employee.getId());
		InvoiceSummaryDTO invoiceDTO = new InvoiceSummaryDTO();
		invoiceDTO.setInvoicesIds(Lists.newArrayList(work1.getInvoice().getId(), work2.getInvoice().getId()));
		invoiceDTO.setDescription("Some description");
		InvoiceSummary invoice = billingService.saveInvoiceSummary(invoiceDTO);
		assertNotNull(invoice);
		assertTrue(invoice.getInvoices().size() == 2);
		assertEquals(invoice.getDescription(), "Some description");
		assertTrue(invoice.isPaymentPending());
		assertTrue(invoice.getBalance().compareTo(BigDecimal.valueOf(200.00)) == 0);
	}

	@Test
	public void saveStatementPaymentConfigurationForCompany_success() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		assertNotNull(billingService.findStatementPaymentConfigurationByCompany(employee.getCompany().getId()));
		PaymentConfigurationDTO dto = new PaymentConfigurationDTO();
		dto.setAccountingProcessDays(AccountingProcessTime.FIVE_DAYS.getPaymentDays());
		dto.setAchPaymentMethodEnabled(true);
		dto.setPaymentCycleDays(PaymentCycle.BIWEEKLY.getPaymentDays());
		dto.setPreferredDayOfMonth(15);
		dto.setPreferredDayOfWeek(Calendar.FRIDAY);
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(employee.getCompany().getId(), dto);
		assertNotNull(config);
		assertNotNull(config.getAccountingProcessTime());
		assertNotNull(config.getNextStatementDate());
		assertNotNull(config.getPaymentCycle());
		assertNotNull(config.getStartDatePaymentCycle());
		assertEquals(config.getAccountingProcessTime(), AccountingProcessTime.FIVE_DAYS);
		assertEquals(config.getPaymentCycle(), PaymentCycle.BIWEEKLY);

		Company company = companyService.findCompanyById(employee.getCompany().getId());
		assertTrue(company.hasStatementsEnabled());
		assertTrue(company.getManageMyWorkMarket().getPaymentTermsEnabled());
		assertEquals(company.getManageMyWorkMarket().getPaymentTermsDays().intValue(), PaymentTermsDays.THIRTY.getPaymentDays());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveInvoiceSummary_withStatementsON_throwsIllegalArgumentException() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		assertNotNull(billingService.findStatementPaymentConfigurationByCompany(employee.getCompany().getId()));
		PaymentConfigurationDTO dto = new PaymentConfigurationDTO();
		dto.setAccountingProcessDays(AccountingProcessTime.FIVE_DAYS.getPaymentDays());
		dto.setAchPaymentMethodEnabled(true);
		dto.setPaymentCycleDays(PaymentCycle.BIWEEKLY.getPaymentDays());
		dto.setPreferredDayOfMonth(15);
		dto.setPreferredDayOfWeek(Calendar.FRIDAY);
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(employee.getCompany().getId(), dto);
		assertNotNull(config);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWork(employee.getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		authenticationService.setCurrentUser(employee.getId());
		workService.closeWork(work.getId());
		work = workService.findWork(work.getId());
		assertTrue(work.isPaymentPending());
		assertNotNull(work.getDueOn());

		authenticationService.setCurrentUser(employee.getId());

		InvoiceSummaryDTO invoiceDTO = new InvoiceSummaryDTO();
		invoiceDTO.setInvoicesIds(Lists.newArrayList(work.getInvoice().getId()));
		invoiceDTO.setDescription("Some description");
		billingService.saveInvoiceSummary(invoiceDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveInvoiceSummary_withInvoiceBundle_throwsIllegalArgumentException() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		work = workService.findWork(work.getId());
		assertTrue(work.isPaymentPending());
		assertNotNull(work.getDueOn());

		InvoicePagination pagination = new InvoicePagination(true);
		pagination = billingService.findAllInvoicesByCompany(employee.getCompany().getId(), pagination);
		assertNotNull(pagination);

		authenticationService.setCurrentUser(employee.getId());
		InvoiceSummaryDTO invoiceDTO = new InvoiceSummaryDTO();
		invoiceDTO.setInvoicesIds(Lists.newArrayList(work.getInvoice().getId()));
		invoiceDTO.setDescription("Some description");
		InvoiceSummary invoice = billingService.saveInvoiceSummary(invoiceDTO);
		assertNotNull(invoice);
		assertTrue(invoice.isPaymentPending());

		invoiceDTO = new InvoiceSummaryDTO();
		invoiceDTO.setInvoicesIds(Lists.newArrayList(invoice.getId()));
		invoiceDTO.setDescription("Some description");
		billingService.saveInvoiceSummary(invoiceDTO);
	}

	@Test
	public void testPayAssignmentPendingFulfillment() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);
		BigDecimal availableCashBefore = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work1 = newWorkWithPaymentTerms(employee.getId(), 30);
		workRoutingService.addToWorkResources(work1.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work1.getId());
		workService.updateWorkProperties(work1.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work1.getId(), new CompleteWorkDTO());
		workService.closeWork(work1.getId());

		work1 = workService.findWork(work1.getId());
		assertTrue(work1.isPaymentPending());
		assertNotNull(work1.getDueOn());

		authenticationService.setCurrentUser(employee.getId());
		InvoiceSummaryDTO invoiceDTO = new InvoiceSummaryDTO();
		invoiceDTO.setInvoicesIds(Lists.newArrayList(work1.getInvoice().getId()));
		invoiceDTO.setDescription("Some description");
		InvoiceSummary invoice = billingService.saveInvoiceSummary(invoiceDTO);
		assertNotNull(invoice);
		assertTrue(invoice.isPaymentPending());

		billingService.payInvoice(employee.getId(), invoice.getId());
		invoice = billingService.findInvoiceById(invoice.getId());
		assertTrue(invoice.isPaid());

		assertTrue(workService.isWorkPendingFulfillment(work1.getId()));

		List<ConstraintViolation> violations = billingService.payAssignment(work1.getId());
		assertFalse(violations.isEmpty());

		BigDecimal availableCashAfter = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());

		assertEquals(invoice.getBalance().add(availableCashAfter).compareTo(availableCashBefore), 0);

	}

	@Test
	public void payInvoices_doNotAddToSummaryIfPresentInAnother() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work1 = newWorkWithPaymentTerms(employee.getId(), 30);
		Work work2 = newWorkWithPaymentTerms(employee.getId(), 30);
		Work work3 = newWorkWithPaymentTerms(employee.getId(), 30);

		workRoutingService.addToWorkResources(work1.getId(), contractor.getId());
		workRoutingService.addToWorkResources(work2.getId(), contractor.getId());
		workRoutingService.addToWorkResources(work3.getId(), contractor.getId());

		workService.acceptWork(contractor.getId(), work1.getId());
		workService.acceptWork(contractor.getId(), work2.getId());
		workService.acceptWork(contractor.getId(), work3.getId());
		workService.updateWorkProperties(work1.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.updateWorkProperties(work2.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.updateWorkProperties(work3.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work1.getId(), new CompleteWorkDTO());
		workService.completeWork(work2.getId(), new CompleteWorkDTO());
		workService.completeWork(work3.getId(), new CompleteWorkDTO());

		workService.closeWork(work1.getId());
		workService.closeWork(work2.getId());
		workService.closeWork(work3.getId());

		work1 = workService.findWork(work1.getId());
		assertTrue(work1.isPaymentPending());
		assertNotNull(work1.getDueOn());

		work2 = workService.findWork(work2.getId());
		assertTrue(work2.isPaymentPending());
		assertNotNull(work2.getDueOn());

		work3 = workService.findWork(work3.getId());
		assertTrue(work3.isPaymentPending());
		assertNotNull(work3.getDueOn());

		InvoicePagination pagination = new InvoicePagination(true);
		pagination = billingService.findAllInvoicesByCompany(employee.getCompany().getId(), pagination);
		assertNotNull(pagination);
		assertEquals(pagination.getResults().size(), 3);

		authenticationService.setCurrentUser(employee.getId());
		final Invoice invoice1 = work1.getInvoice();
		final Invoice invoice2 = work2.getInvoice();
		final Invoice invoice3 = work3.getInvoice();

		BigDecimal availableCashBefore = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());

		billingService.payInvoices(employee.getId(), Lists.newArrayList(invoice1.getId(), invoice2.getId()));
		billingService.payInvoices(employee.getId(), Lists.newArrayList(invoice1.getId(), invoice3.getId()));

		BigDecimal availableCashAfter = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());

		assertEquals(invoice1.getBalance().add(invoice2.getBalance()).add(invoice3.getBalance()).add(availableCashAfter).compareTo(availableCashBefore), 0);
	}


	@Test
	public void payInvoices_errorIfDuplicateSummary() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work1 = newWorkWithPaymentTerms(employee.getId(), 30);
		Work work2 = newWorkWithPaymentTerms(employee.getId(), 30);

		workRoutingService.addToWorkResources(work1.getId(), contractor.getId());
		workRoutingService.addToWorkResources(work2.getId(), contractor.getId());

		workService.acceptWork(contractor.getId(), work1.getId());
		workService.acceptWork(contractor.getId(), work2.getId());
		workService.updateWorkProperties(work1.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.updateWorkProperties(work2.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work1.getId(), new CompleteWorkDTO());
		workService.completeWork(work2.getId(), new CompleteWorkDTO());

		workService.closeWork(work1.getId());
		workService.closeWork(work2.getId());

		work1 = workService.findWork(work1.getId());
		assertTrue(work1.isPaymentPending());
		assertNotNull(work1.getDueOn());

		work2 = workService.findWork(work2.getId());
		assertTrue(work2.isPaymentPending());
		assertNotNull(work2.getDueOn());

		InvoicePagination pagination = new InvoicePagination(true);
		pagination = billingService.findAllInvoicesByCompany(employee.getCompany().getId(), pagination);
		assertNotNull(pagination);
		assertEquals(pagination.getResults().size(), 2);

		authenticationService.setCurrentUser(employee.getId());
		final Invoice invoice1 = work1.getInvoice();
		final Invoice invoice2 = work2.getInvoice();

		BigDecimal availableCashBefore = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());

		billingService.payInvoices(employee.getId(), Lists.newArrayList(invoice1.getId(), invoice2.getId()));
		try {
			billingService.payInvoices(employee.getId(), Lists.newArrayList(invoice1.getId(), invoice2.getId()));
			fail("Should error when paying same summary more than once");
		} catch (InvoiceAlreadyPaidException ignored) {	}

		BigDecimal availableCashAfter = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());

		assertEquals(invoice1.getBalance().add(invoice2.getBalance()).add(availableCashAfter).compareTo(availableCashBefore), 0);
	}


	@Test
	public void testAllowUserToTurnStatementsONWithPaymentPendingInvoices() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work1 = newWorkWithPaymentTerms(employee.getId(), 30);

		workRoutingService.addToWorkResources(work1.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work1.getId());
		workService.updateWorkProperties(work1.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work1.getId(), new CompleteWorkDTO());
		workService.closeWork(work1.getId());

		work1 = workService.findWork(work1.getId());
		assertTrue(work1.isPaymentPending());
		assertNotNull(work1.getDueOn());

		PaymentConfigurationDTO dto = new PaymentConfigurationDTO();
		dto.setAccountingProcessDays(AccountingProcessTime.FIVE_DAYS.getPaymentDays());
		dto.setAchPaymentMethodEnabled(true);
		dto.setPaymentCycleDays(PaymentCycle.BIWEEKLY.getPaymentDays());
		dto.setPreferredDayOfMonth(15);
		dto.setPreferredDayOfWeek(Calendar.FRIDAY);
		PaymentConfiguration c = billingService.saveStatementPaymentConfigurationForCompany(employee.getCompany().getId(), dto);
		assertNotNull(c);
	}

	@Test
	public void testBiweeklyPaymentConfiguration() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		PaymentConfigurationDTO dto = new PaymentConfigurationDTO();
		dto.setAccountingProcessDays(AccountingProcessTime.FIVE_DAYS.getPaymentDays());
		dto.setAchPaymentMethodEnabled(true);
		dto.setPaymentCycleDays(PaymentCycle.BIWEEKLY.getPaymentDays());
		dto.setPreferredDayOfMonth(15);
		dto.setPreferredDayOfWeek(Calendar.FRIDAY);
		dto.setBiweeklyPaymentOnSpecificDayOfMonth(true);
		dto.setPreferredDayOfMonthBiweeklyFirstPayment(THREE);
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(employee.getCompany().getId(), dto);
		assertNotNull(config);
		assertNotNull(config.getAccountingProcessTime());
		assertNotNull(config.getNextStatementDate());
		assertNotNull(config.getPaymentCycle());
		assertNotNull(config.getStartDatePaymentCycle());
		assertEquals(config.getAccountingProcessTime(), AccountingProcessTime.FIVE_DAYS);
		assertEquals(config.getPaymentCycle(), PaymentCycle.BIWEEKLY);
		assertTrue(config.isBiweeklyPaymentOnSpecificDayOfMonth());
		assertEquals(config.getBiweeklyPaymentDays(), BiweeklyPaymentDays.FIRST_PAYMENT_EVERY_DAY_3);

		Company company = companyService.findCompanyById(employee.getCompany().getId());
		assertTrue(company.hasStatementsEnabled());
		ManageMyWorkMarket manageMyWorkMarket = company.getManageMyWorkMarket();
		assertTrue(manageMyWorkMarket.getPaymentTermsEnabled());
		assertEquals(manageMyWorkMarket.getPaymentTermsDays().intValue(), PaymentTermsDays.THIRTY.getPaymentDays());
	}

	@Test
	public void testWeeklyWith7daysPaymentConfiguration() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		PaymentConfigurationDTO dto = new PaymentConfigurationDTO();
		dto.setAccountingProcessDays(AccountingProcessTime.ZERO_DAYS.getPaymentDays());
		dto.setWireTransferPaymentMethodEnabled(false);
		dto.setPrefundPaymentMethodEnabled(true);
		dto.setPaymentCycleDays(PaymentCycle.WEEKLY.getPaymentDays());
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(employee.getCompany().getId(), dto);
		assertNotNull(config);
		assertNotNull(config.getAccountingProcessTime());
		assertNotNull(config.getNextStatementDate());
		assertNotNull(config.getPaymentCycle());
		assertNotNull(config.getStartDatePaymentCycle());
		assertEquals(config.getAccountingProcessTime(), AccountingProcessTime.ZERO_DAYS);
		assertEquals(config.getPaymentCycle(), PaymentCycle.WEEKLY);

		Company company = companyService.findCompanyById(employee.getCompany().getId());
		assertTrue(company.hasStatementsEnabled());
		ManageMyWorkMarket manageMyWorkMarket = company.getManageMyWorkMarket();
		assertTrue(manageMyWorkMarket.getPaymentTermsEnabled());
		assertEquals(manageMyWorkMarket.getPaymentTermsDays().intValue(), PaymentTermsDays.SEVEN.getPaymentDays());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCantRemoveAllInvoicesFromBundle() throws Exception {

		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work1 = newWorkWithPaymentTerms(employee.getId(), 30);
		Work work2 = newWorkWithPaymentTerms(employee.getId(), 30);

		workRoutingService.addToWorkResources(work1.getId(), contractor.getId());
		workRoutingService.addToWorkResources(work2.getId(), contractor.getId());

		workService.acceptWork(contractor.getId(), work1.getId());
		workService.acceptWork(contractor.getId(), work2.getId());
		workService.updateWorkProperties(work1.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.updateWorkProperties(work2.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work1.getId(), new CompleteWorkDTO());
		workService.completeWork(work2.getId(), new CompleteWorkDTO());
		workService.closeWork(work1.getId());
		workService.closeWork(work2.getId());

		work1 = workService.findWork(work1.getId());
		assertTrue(work1.isPaymentPending());
		assertNotNull(work1.getDueOn());

		work2 = workService.findWork(work2.getId());
		assertTrue(work2.isPaymentPending());
		assertNotNull(work2.getDueOn());

		InvoicePagination pagination = new InvoicePagination(true);
		pagination = billingService.findAllInvoicesByCompany(employee.getCompany().getId(), pagination);
		assertNotNull(pagination);
		assertTrue(pagination.getResults().size() == 2);

		authenticationService.setCurrentUser(employee.getId());
		InvoiceSummaryDTO invoiceDTO = new InvoiceSummaryDTO();
		invoiceDTO.setInvoicesIds(Lists.newArrayList(work1.getInvoice().getId(), work2.getInvoice().getId()));
		invoiceDTO.setDescription("Some description");
		InvoiceSummary invoice = billingService.saveInvoiceSummary(invoiceDTO);
		assertNotNull(invoice);
		assertTrue(invoice.getInvoices().size() == 2);
		assertEquals(invoice.getDescription(), "Some description");
		assertTrue(invoice.isPaymentPending());
		assertTrue(invoice.getBalance().compareTo(BigDecimal.valueOf(200.00)) == 0);

		billingService.removeInvoiceFromInvoiceSummary(invoice.getId(), work1.getInvoice().getId());
		invoice = billingService.findInvoiceById(invoice.getId());
		assertTrue(invoice.getBalance().compareTo(BigDecimal.valueOf(100.00)) == 0);

		billingService.removeInvoiceFromInvoiceSummary(invoice.getId(), work2.getInvoice().getId());

	}

	@Test
	public void payAssignment_withPrefundAssignment_sucess() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		work = workService.findWork(work.getId());
		assertTrue(work.isInvoiced());

		billingService.payAssignment(work.getId());

		InvoicePagination pagination = new InvoicePagination(true);
		pagination = billingService.findAllInvoicesByCompany(employee.getCompany().getId(), pagination);
		assertNotNull(pagination);
		assertEquals(pagination.getRowCount().intValue(), 1);

		Invoice invoice = pagination.getResults().get(0);
		assertNotNull(invoice);
		assertTrue(invoice.isPaid());
		assertNotNull(invoice.getPaymentDate());
		assertNotNull(invoice.getPaidBy());

	}

	@Test
	public void testActualCashPrefundBasicCase() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		authenticationService.setCurrentUser(employee);

		BigDecimal initialAvailableCash = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());
		BigDecimal initialActualCash = accountRegisterService.getActualCashFundsByCompany(employee.getCompany().getId());

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		BigDecimal availableCashWithCommitment = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());
		BigDecimal actualCashWithCommitment = accountRegisterService.getActualCashFundsByCompany(employee.getCompany().getId());

		assertTrue(initialAvailableCash.compareTo(availableCashWithCommitment) > 0);
		assertTrue(initialActualCash.compareTo(actualCashWithCommitment) == 0);

		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		work = workService.findWork(work.getId());
		assertTrue(work.isInvoiced());

		billingService.payAssignment(work.getId());

		BigDecimal afterAvailableCash = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());
		BigDecimal afterActualCash = accountRegisterService.getActualCashFundsByCompany(employee.getCompany().getId());

		assertTrue(initialActualCash.compareTo(afterActualCash) > 0);
		assertTrue(afterAvailableCash.compareTo(availableCashWithCommitment) == 0);

	}


	@Test
	public void testActualCashPaymentTermsBasicCase() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		authenticationService.setCurrentUser(employee);

		BigDecimal initialAvailableCash = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());
		BigDecimal initialActualCash = accountRegisterService.getActualCashFundsByCompany(employee.getCompany().getId());

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		BigDecimal availableCashWithCommitment = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());
		BigDecimal actualCashWithCommitment = accountRegisterService.getActualCashFundsByCompany(employee.getCompany().getId());

		assertTrue(initialAvailableCash.compareTo(availableCashWithCommitment) == 0);
		assertTrue(initialActualCash.compareTo(actualCashWithCommitment) == 0);

		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		work = workService.findWork(work.getId());
		assertTrue(work.isInvoiced());

		billingService.payAssignment(work.getId());

		BigDecimal afterAvailableCash = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());
		BigDecimal afterActualCash = accountRegisterService.getActualCashFundsByCompany(employee.getCompany().getId());

		assertTrue(initialActualCash.compareTo(afterActualCash) > 0);
		assertTrue(initialAvailableCash.compareTo(afterAvailableCash) > 0);

	}

	@Test
	public void cancelWork_withAssignmentPrefunded_success() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWork(employee.getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		CancelWorkDTO cancelWorkDTO = new CancelWorkDTO();
		cancelWorkDTO.setCancellationReasonTypeCode(CancellationReasonType.BUYER_CANCELLED);
		cancelWorkDTO.setNote("Cancellation note");
		cancelWorkDTO.setPrice(Double.parseDouble("200"));
		cancelWorkDTO.setWorkId(work.getId());
		assertTrue(workService.cancelWork(cancelWorkDTO).isEmpty());

		work = workService.findWork(work.getId());
		assertTrue(work.isPaid());
		assertTrue(work.getCancelledOn() != null);
		assertTrue(work.getDueOn() != null);

		WorkMilestones workMilestones = workMilestonesService.findWorkMilestonesByWorkId(work.getId());
		assertTrue(workMilestones.getCancelledOn() != null);
		assertTrue(workMilestones.getDueOn() != null);
		assertTrue(workMilestones.getPaidOn() != null);
	}

	@Test
	public void cancelWork_withAssignmentPaymentTerms_success() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 15);
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		CancelWorkDTO cancelWorkDTO = new CancelWorkDTO();
		cancelWorkDTO.setCancellationReasonTypeCode(CancellationReasonType.BUYER_CANCELLED);
		cancelWorkDTO.setNote("Cancellation note");
		cancelWorkDTO.setPrice(Double.parseDouble("200"));
		cancelWorkDTO.setWorkId(work.getId());
		assertTrue(workService.cancelWork(cancelWorkDTO).isEmpty());

		work = workService.findWork(work.getId());
		assertTrue(work.isCancelled());
		assertTrue(work.getCancelledOn() != null);
		assertTrue(work.getDueOn() != null);

		WorkMilestones workMilestones = workMilestonesService.findWorkMilestonesByWorkId(work.getId());
		assertTrue(workMilestones.getCancelledOn() != null);
		assertTrue(workMilestones.getDueOn() != null);
		assertTrue(workMilestones.getPaidOn() == null);
	}

	@Test
	public void testDefaultPricingType() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		assertNotNull(employee);
		assertNotNull(employee.getCompany());
		assertTrue(employee.getCompany().getAccountPricingType().isTransactionalPricing());
	}

	@Test
	public void getStatementDashboard_withSubscriptionInvoice_returnsEmptyPagination() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		AccountStatementDetailPagination pagination = new AccountStatementDetailPagination(true);
		AccountStatementFilters filters = new AccountStatementFilters();
		filters.setInvoiceType(SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE);
		filters.setPaidStatus(false);
		filters.setPayables(true);
		pagination = billingService.getStatementDashboard(filters, pagination);
		assertNotNull(pagination);
		assertTrue(pagination.getResults().isEmpty());
	}

	@Test
	public void issueAdHocInvoice_success() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();

		InvoiceDTO dto = new InvoiceDTO();
		dto.setCompanyId(employee.getCompany().getId());
		dto.setDueDate(DateUtilities.getMidnightNextWeek());

		BigDecimal balance = BigDecimal.ZERO;
		List<InvoiceLineItemDTO> invoiceLineItemDTOs = Lists.newArrayList();
		for (InvoiceLineItemType lineItemType : InvoiceLineItemType.values()) {
			InvoiceLineItemDTO lineItemDTO = new InvoiceLineItemDTO(lineItemType);
			lineItemDTO.setAmount(BigDecimal.valueOf(100));
			lineItemDTO.setDescription(lineItemType.getDescription());
			invoiceLineItemDTOs.add(lineItemDTO);
			balance = balance.add(lineItemDTO.getAmount());
		}
		dto.setLineItemDTOList(invoiceLineItemDTOs);
		AdHocInvoice invoice = billingService.issueAdHocInvoice(dto);
		assertTrue(invoice.getBalance().compareTo(balance) == 0);
		assertTrue(DateUtilities.equals(DateUtilities.getCalendarWithLastMinuteOfDay(dto.getDueDate(), Constants.EST_TIME_ZONE), invoice.getDueDate()));
		assertTrue(invoice.getCompany().getId().equals(dto.getCompanyId()));
	}

	@Test
	public void testVoidBundledInvoice() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);

		assertTrue(work.hasPaymentTerms());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		Invoice invoice = billingService.findInvoiceByWorkId(work.getId());
		assertNotNull(invoice);

		InvoiceSummaryDTO invoiceDTO = new InvoiceSummaryDTO();
		invoiceDTO.setDescription("new bundle");
		invoiceDTO.setInvoicesIds(Lists.newArrayList(invoice.getId()));
		InvoiceSummary invoiceSummary = billingService.saveInvoiceSummary(invoiceDTO);

		work = workService.findWork(work.getId());
		assertTrue(work.isPaymentPending());
		assertTrue(work.getInvoice().isBundled());

		StopPaymentDTO dto = new StopPaymentDTO();
		dto.setReason("testing");

		List<ConstraintViolation> violations = workService.stopWorkPayment(work.getId(), dto);
		assertTrue(violations.isEmpty());
		invoiceSummary = billingService.findInvoiceById(invoiceSummary.getId());
		assertTrue(invoiceSummary.isVoid());
	}

	/**
	 * Removing a 'void' invoice from a bundle should not change the invoice's status
	 */
	@Test
	public void unbundleInvoice_withVoidStatus_remainsVoid() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		// Create 'completed' assignment
		Work work1 = newWorkWithPaymentTerms(employee.getId(), 30);
		Long invoiceId1 = createCompletedWork(work1, contractor.getId());

		// Create another 'completed' assignment
		Work work2 = newWorkWithPaymentTerms(employee.getId(), 30);
		Long invoiceId2 = createCompletedWork(work2, contractor.getId());

		// Add 'completed' assignments to bundle
		InvoiceSummaryDTO invoiceDTO = new InvoiceSummaryDTO();
		invoiceDTO.setDescription("new bundle");
		invoiceDTO.setInvoicesIds(Arrays.asList(new Long[]{invoiceId1, invoiceId2}));
		InvoiceSummary invoiceSummary = billingService.saveInvoiceSummary(invoiceDTO);

		// Stop payment on one invoice
		StopPaymentDTO dto = new StopPaymentDTO();
		dto.setReason("testing");
		workService.stopWorkPayment(work1.getId(), dto);

		// Invoice should now have 'void' status
		Invoice voidedInvoice = billingService.findInvoiceById(invoiceId1);
		assertTrue("Invoice status is: " + voidedInvoice.getInvoiceStatusType().getCode(), voidedInvoice.isVoid());

		// Remove 'voided' invoice from bundle
		billingService.removeInvoiceFromInvoiceSummary(invoiceSummary.getId(), invoiceId1);

		// Voided invoice should still have 'void' status
		voidedInvoice = billingService.findInvoiceById(invoiceId1);
		assertTrue("Invoice status is now: " + voidedInvoice.getInvoiceStatusType().getCode(), voidedInvoice.isVoid());

	}

	@Test
	public void invoicesDue_statementGenerated() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		// Create 'completed' assignment
		Work work1 = newWorkWithPaymentTerms(employee.getId(), 30);
		createCompletedWork(work1, contractor.getId());

		// Create another 'completed' assignment
		Work work2 = newWorkWithPaymentTerms(employee.getId(), 30);
		createCompletedWork(work2, contractor.getId());

		PaymentConfiguration paymentConfiguration = employee.getCompany().getPaymentConfiguration();
		Calendar lastSentOn = Calendar.getInstance();
		lastSentOn.add(Calendar.DATE, -8);
		paymentConfiguration.setLastStatementSentOn(lastSentOn);
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		paymentConfiguration.setNextStatementDate(yesterday);
		paymentConfiguration.setPaymentCycleDays(7);
		billingService.saveStatementPaymentConfiguration(paymentConfiguration);

		Statement statement = billingService.generateStatement(employee.getCompany().getId());
		assertNotNull(statement);
	}

	@Test
	public void invoicesDue_nextStatementDateTomorrow_noStatementGenerated() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		// Create 'completed' assignment
		Work work1 = newWorkWithPaymentTerms(employee.getId(), 30);
		createCompletedWork(work1, contractor.getId());

		// Create another 'completed' assignment
		Work work2 = newWorkWithPaymentTerms(employee.getId(), 30);
		createCompletedWork(work2, contractor.getId());

		PaymentConfiguration paymentConfiguration = employee.getCompany().getPaymentConfiguration();
		Calendar lastSentOn = Calendar.getInstance();
		lastSentOn.add(Calendar.DATE, 6);
		paymentConfiguration.setLastStatementSentOn(lastSentOn);
		Calendar nextStatementDate = Calendar.getInstance();
		nextStatementDate.add(Calendar.DATE, 1);
		paymentConfiguration.setNextStatementDate(nextStatementDate);
		paymentConfiguration.setPaymentCycleDays(7);
		billingService.saveStatementPaymentConfiguration(paymentConfiguration);

		Statement statement = billingService.generateStatement(employee.getCompany().getId());
		assertNull(statement);
	}

	private Long createCompletedWork(Work work, Long contractorId) throws WorkNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		assertTrue(work.hasPaymentTerms());

		workRoutingService.addToWorkResources(work.getId(), contractorId);
		workService.acceptWork(contractorId, work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		Invoice invoice = billingService.findInvoiceByWorkId(work.getId());
		return invoice.getId();
	}

	@Test
	public void issueAdHocInvoice_withSubscriptionInvoice_andPlanSubscriptionType_andActiveSubscription_auditCurrentSubConfigOnPaymentPeriod() throws Exception {
		SubscriptionConfiguration subscriptionConfiguration = createSubscriptionConfigFixture();

		InvoiceDTO dto = new InvoiceDTO();
		dto.setCompanyId(subscriptionConfiguration.getCompany().getId());
		dto.setDueDate(DateUtilities.getMidnightNextWeek());
		dto.setSubscriptionInvoiceTypeCode(SubscriptionInvoiceType.REGULAR);

		Calendar paymentPeriod = Calendar.getInstance();
		paymentPeriod.add(Calendar.MONTH, 2);
		dto.setPaymentPeriod(paymentPeriod);

		BigDecimal balance = BigDecimal.ZERO;
		List<InvoiceLineItemDTO> invoiceLineItemDTOs = Lists.newArrayList();
		for (InvoiceLineItemType lineItemType : InvoiceLineItemType.ADHOC_SERVICE_INVOICE_LINE_ITEMS_SUBSCRIPTION) {
			InvoiceLineItemDTO lineItemDTO = new InvoiceLineItemDTO(lineItemType);
			lineItemDTO.setAmount(BigDecimal.valueOf(100));
			lineItemDTO.setDescription(lineItemType.getDescription());
			invoiceLineItemDTOs.add(lineItemDTO);
			balance = balance.add(lineItemDTO.getAmount());
		}
		dto.setLineItemDTOList(invoiceLineItemDTOs);

		AdHocInvoice adHocInvoice = billingService.issueAdHocInvoice(dto);
		assertTrue(adHocInvoice.getPaymentPeriod() instanceof SubscriptionPaymentPeriod);

		SubscriptionPaymentPeriod subscriptionPaymentPeriod = (SubscriptionPaymentPeriod) adHocInvoice.getPaymentPeriod();
		assertEquals(SubscriptionPeriodType.ADHOC, subscriptionPaymentPeriod.getSubscriptionPeriodType().getCode());

		SubscriptionPaymentTier paymentTier = subscriptionConfiguration.getActiveSubscriptionFeeConfiguration().getSubscriptionPaymentTiers().get(0);

		assertEquals(
			subscriptionConfiguration.getId(),
			subscriptionPaymentPeriod.getSubscriptionConfiguration().getId()
		);
		assertEquals(
			subscriptionConfiguration.getActiveSubscriptionFeeConfiguration().getId(),
			subscriptionPaymentPeriod.getSubscriptionFeeConfigurationId()
		);
		assertEquals(
			paymentTier.getId(),
			subscriptionPaymentPeriod.getSubscriptionPaymentTierSWId()
		);
	}

	private SubscriptionConfiguration createSubscriptionConfigFixture() throws Exception {
		User currentUser = userService.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);
		authenticationService.setCurrentUser(currentUser);

		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();

		List<SubscriptionPaymentTierDTO> paymentTiers = Lists.newArrayList();
		SubscriptionPaymentTierDTO subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
		subscriptionPaymentTierDTO.setMinimum(BigDecimal.ZERO);
		subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.ONE);
		subscriptionPaymentTierDTO.setVendorOfRecordAmount(BigDecimal.TEN);
		paymentTiers.add(subscriptionPaymentTierDTO);

		SubscriptionConfigurationDTO subscriptionDTO = new SubscriptionConfigurationDTO();
		subscriptionDTO.setSubscriptionPaymentTierDTOs(paymentTiers);
		subscriptionDTO.setEffectiveDate(DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.getMidnightNextMonth(), Constants.EST_TIME_ZONE));
		subscriptionDTO.setNumberOfPeriods(12);
		subscriptionDTO.setSubscriptionPeriod(SubscriptionPeriod.MONTHLY);
		subscriptionDTO.setVendorOfRecord(true);
		subscriptionDTO.setSubscriptionTypeCode(SubscriptionType.BAND);

		SubscriptionConfiguration subscriptionConfiguration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		return subscriptionService.approveSubscriptionConfiguration(subscriptionConfiguration.getId());
	}

	/**
	 * invoice.downloaded on should get updated when the invoice is downloaded by the assignment owner
	 */
	@Test
	public void updateLastDownloadedDate_byAssignmentOwner_updated() throws Exception {

		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled(30, "1000.00");
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();

		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		Invoice invoice = billingService.findInvoiceByWorkId(work.getId());

		billingService.updateInvoiceLastDownloadedDate(Collections.singletonList(invoice.getId()), DateUtilities.getCalendarNow(), employee.getId());
		invoice = billingService.findInvoiceByWorkId(work.getId());
		assertNotNull(invoice.getDownloadedOn());
	}

	/**
	 * invoice.downloaded on should not get updated when the invoice is downloaded by the Work Market system user
	 */
	@Test
	public void updateLastDownloadedDate_byWMSystemUser_notUpdated() throws Exception {

		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled(30, "1000.00");
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();

		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		Invoice invoice = billingService.findInvoiceByWorkId(work.getId());

		billingService.updateInvoiceLastDownloadedDate(Collections.singletonList(invoice.getId()), DateUtilities.getCalendarNow(), Constants.WORKMARKET_SYSTEM_USER_ID);
		invoice = billingService.findInvoiceByWorkId(work.getId());
		assertNull(invoice.getDownloadedOn());
	}
}
