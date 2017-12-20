package com.workmarket.service.business;

import com.google.common.collect.Sets;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.BankAccountTransactionStatus;
import com.workmarket.domains.model.account.RegisterTransactionActivityPagination;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.request.FundsRequest;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.PaymentFulfillmentStatusType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.NumberUtilities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AccountRegisterServiceIT extends BaseServiceIT {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private LaneService laneService;
	@Autowired private CompanyService companyService;
	@Autowired private BankingService bankingService;
	@Autowired private BillingService billingService;
	private User employee;
	private User contractor;

	@Before
	public void before() throws Exception {
		this.employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		this.contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
	}

	@Test
	public void addFundsToRegisterFromWire_increasesDepositedCash() throws Exception {
		User user = newEmployeeWithCashBalance();
		BigDecimal withdrawable = accountRegisterService.calculateWithdrawableCashByCompany(user.getCompany().getId());
		BigDecimal available = accountRegisterService.calcAvailableCashByCompany(user.getCompany().getId());
		assertNotNull(withdrawable);
		assertNotNull(available);
		assertTrue(NumberUtilities.isPositive(available));
		assertTrue(withdrawable.compareTo(BigDecimal.ZERO) == 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void withdrawFunds_LockedCompany_IsNotAllowed() throws Exception {
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		authenticationService.setCurrentUser(contractor);
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		authenticationService.setCurrentUser(employee);
		workService.closeWork(work.getId());

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		companyService.lockCompanyAccount(contractor.getCompany().getId());

		assertTrue(companyService.findCompanyById(contractor.getCompany().getId()).isLocked());

		authenticationService.setCurrentUser(contractor);
		assertTrue(accountRegisterService.calculateWithdrawableCashByCompany(contractor.getCompany().getId()).compareTo(BigDecimal.ZERO) > 0);

		accountRegisterService.withdrawFundsFromRegister(contractor.getId(), 1L, "20.00");
	}

	@Test
	public void withdrawFundsToPaypal_ValidCompany_IsAllowed() throws Exception {
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		BankAccountDTO bankAccountDTO = new BankAccountDTO();
		bankAccountDTO.setType(AbstractBankAccount.PAYPAL);
		bankAccountDTO.setBankName(AbstractBankAccount.PAYPAL);
		bankAccountDTO.setCountryCode(Country.USA);
		bankAccountDTO.setEmailAddress(contractor.getEmail());
		bankAccountDTO.setNameOnAccount(contractor.getFullName());
		bankAccountDTO.setAccountNumber(buildRandomAccountNumber());
		AbstractBankAccount bankAccount = bankingService.saveBankAccount(contractor.getId(), bankAccountDTO);
		assertNotNull(bankAccount);

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		authenticationService.setCurrentUser(contractor);
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		authenticationService.setCurrentUser(employee);
		workService.closeWork(work.getId());

		authenticationService.setCurrentUser(contractor);
		assertTrue(accountRegisterService.calculateWithdrawableCashByCompany(contractor.getCompany().getId()).compareTo(BigDecimal.ZERO) > 0);

		accountRegisterService.withdrawFundsFromRegister(contractor.getId(), bankAccount.getId(), "29.00");
		List<BankAccountTransaction> transactionList = accountRegisterService.findPayPalAccountWithdrawalTransactions();
		assertFalse(transactionList.isEmpty());
		for (BankAccountTransaction b : transactionList) {
			if (b.getBankAccount().getId().equals(bankAccount.getId())) {
				assertTrue(b.getBankAccountTransactionStatus().getCode().equals(BankAccountTransactionStatus.SUBMITTED));
				Assert.assertEquals(b.getAmount().negate(), new BigDecimal("28.00"));
			}
		}
	}

	private String buildRandomAccountNumber() {
		return UUID.randomUUID().toString();
	}

	@Test
	public void withdrawFundsToPaypal_OverTheLimitFee_TransactionsHaveCorrectValue() throws Exception {
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		BankAccountDTO bankAccountDTO = new BankAccountDTO();
		bankAccountDTO.setType(AbstractBankAccount.PAYPAL);
		bankAccountDTO.setBankName(AbstractBankAccount.PAYPAL);
		bankAccountDTO.setCountryCode(Country.USA);
		bankAccountDTO.setEmailAddress(contractor.getEmail());
		bankAccountDTO.setNameOnAccount(contractor.getFullName());
		bankAccountDTO.setAccountNumber(buildRandomAccountNumber());
		AbstractBankAccount bankAccount = bankingService.saveBankAccount(contractor.getId(), bankAccountDTO);

		Assert.assertNotNull(bankAccount);

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		authenticationService.setCurrentUser(contractor);
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		authenticationService.setCurrentUser(employee);
		workService.closeWork(work.getId());

		authenticationService.setCurrentUser(contractor);

		assertTrue(accountRegisterService.calculateWithdrawableCashByCompany(contractor.getCompany().getId()).compareTo(BigDecimal.ZERO) > 0);

		accountRegisterService.withdrawFundsFromRegister(contractor.getId(), bankAccount.getId(), "80.00");
		List<BankAccountTransaction> transactionList = accountRegisterService.findPayPalAccountWithdrawalTransactions();

		Assert.assertFalse(transactionList.isEmpty());

		for (BankAccountTransaction b : transactionList) {
			if (b.getBankAccount().getId().equals(bankAccount.getId())) {
				assertTrue(b.getBankAccountTransactionStatus().getCode().equals(BankAccountTransactionStatus.SUBMITTED));
				Assert.assertEquals(b.getAmount().negate(), new BigDecimal("79.00"));
			}
		}
	}

	@Test
	public void pendingTransactions_WorkIsAccepted_PendingTransactionsExist() throws Exception {
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Work work2 = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workRoutingService.addToWorkResources(work2.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));

		authenticationService.setCurrentUser(contractor);
		workService.acceptWork(contractor.getId(), work.getId());
		workService.acceptWork(contractor.getId(), work2.getId());

		RegisterTransactionActivityPagination pagination = accountRegisterService.getPendingTransactions(employee.getCompany().getId(), new RegisterTransactionActivityPagination());
		//There should only be one transaction for assignment commitments
		assertTrue(pagination.getResults().size() == 1);
		assertTrue(pagination.getResults().get(0).isPreFundAssignmentAuthorization());
	}

	@Test
	public void throughputRunningSummary_HasClosedAssignments_ThroughputSummaryUpdated() throws Exception {
		authenticationService.setCurrentUser(employee);

		BigDecimal throughputBefore = pricingService.findDefaultRegisterForCompany(employee.getCompany().getId()).getAccountRegisterSummaryFields().getAssignmentThroughput();

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);

		assertTrue(work.hasPaymentTerms());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		Invoice invoice = billingService.findInvoiceByWorkId(work.getId());
		Assert.assertNotNull(invoice);
		Map<String, List<ConstraintViolation>> violations = billingService.payInvoice(employee.getId(), invoice.getId());
		assertTrue(violations.isEmpty());

		invoice = billingService.findInvoiceByWorkId(work.getId());
		Assert.assertEquals(invoice.getInvoiceStatusType().getCode(), InvoiceStatusType.PAID);
		Assert.assertEquals(invoice.getPaymentFulfillmentStatusType().getCode(), PaymentFulfillmentStatusType.FULFILLED);

		work = workService.findWork(work.getId());
		BigDecimal throughputAfter = pricingService.findDefaultRegisterForCompany(employee.getCompany().getId()).getAccountRegisterSummaryFields().getAssignmentThroughput();

		BigDecimal throughputSummary = throughputBefore.add(work.getFulfillmentStrategy().getWorkPrice());
		assertTrue(throughputAfter.compareTo(throughputSummary) == 0);
	}

	@Test
	public void addFundsToRegisterAsCredit_ValidRegister_BalanceCredited() throws Exception {
		User employee = newEmployeeWithCashBalance();
		BigDecimal cashBalance = accountRegisterService.calcAvailableCash(employee.getId());
		assertTrue(cashBalance.compareTo(BigDecimal.ZERO) > 0);
		accountRegisterService.addFundsToRegisterAsCredit(new FundsRequest(employee.getCompany().getId(), " ", new BigDecimal(500), RegisterTransactionType.CREDIT_ASSIGNMENT_PAYMENT_REVERSAL));
		BigDecimal cashBalanceAfter = accountRegisterService.calcAvailableCash(employee.getId());
		assertTrue(cashBalance.compareTo(BigDecimal.ZERO) > 0);
		assertTrue(cashBalanceAfter.compareTo(cashBalance) > 0);
	}

	@Test
	public void removeFundsFromRegisterAsCredit_ValidRegister_BalanceDeducted() throws Exception {
		User employee = newEmployeeWithCashBalance();
		BigDecimal cashBalance = accountRegisterService.calcAvailableCash(employee.getId());
		assertTrue(cashBalance.compareTo(BigDecimal.ZERO) > 0);
		accountRegisterService.removeFundsFromRegisterAsCash(new FundsRequest(employee.getCompany().getId(), " ", new BigDecimal(500), RegisterTransactionType.DEBIT_CREDIT_CARD_REFUND));
		BigDecimal cashBalanceAfter = accountRegisterService.calcAvailableCash(employee.getId());
		assertTrue(cashBalance.compareTo(BigDecimal.ZERO) > 0);
		assertTrue(cashBalanceAfter.compareTo(cashBalance) < 0);
	}

	@Test
	public void removeFundsFromRegisterAsCash_MoreThanAvailableCash_NotAllowed() throws Exception {
		User employee = newEmployeeWithCashBalance();
		BigDecimal cashBalance = accountRegisterService.calcAvailableCash(employee.getId());
		assertTrue(cashBalance.compareTo(BigDecimal.ZERO) > 0);
		Assert.assertNull(accountRegisterService.removeFundsFromRegisterAsCash(new FundsRequest(employee.getCompany().getId(), " ", new BigDecimal(500), RegisterTransactionType.CREDIT_CARD_FEE)));
		BigDecimal cashBalanceAfter = accountRegisterService.calcAvailableCash(employee.getId());
		assertTrue(cashBalance.compareTo(BigDecimal.ZERO) > 0);
		assertTrue(cashBalanceAfter.compareTo(cashBalance) == 0);
	}

	@Test
	public void isWorkAuthorized_WithPaymentTermsPositiveCase_Success() throws Exception {
		authenticationService.setCurrentUser(employee);
		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, newContractor());
		assertTrue(accountRegisterServicePaymentTerms.isWorkAuthorized(work));
	}

	@Test
	public void isWorkAuthorized_WithPaymentTermsNegativeCase_Success() throws Exception {
		authenticationService.setCurrentUser(employee);
		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		assertFalse(accountRegisterServicePaymentTerms.isWorkAuthorized(work));
	}

	@Test
	public void isWorkAuthorized_WithPrefundPositiveCase_Success() throws Exception {
		authenticationService.setCurrentUser(employee);
		Work work = createWorkAndSendToResourceNoPaymentTerms(employee, newContractor());
		assertTrue(accountRegisterService.isWorkAuthorized(work));
	}

	@Test
	public void isWorkAuthorized_WithPrefundNegativeCase_Success() throws Exception {
		authenticationService.setCurrentUser(employee);
		Work work = newWorkWithPaymentTerms(employee.getId(), 0);
		assertFalse(accountRegisterService.isWorkAuthorized(work));
	}

	@Test
	public void pendingTransactions_OfflinePaymentWorkIsAccepted_PendingTransactionsDoNotExist() throws Exception {
		User mboEmployee = newFirstEmployeeWithMboEnabled();
		authenticationService.setCurrentUser(mboEmployee);
		Work work = newWork(mboEmployee.getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));

		authenticationService.setCurrentUser(contractor);
		workService.acceptWork(contractor.getId(), work.getId());

		authenticationService.setCurrentUser(mboEmployee);
		RegisterTransactionActivityPagination pagination = accountRegisterService.getPendingTransactions(mboEmployee.getCompany().getId(), new RegisterTransactionActivityPagination());
		//There should not be any transactions
		assertTrue(pagination.getResults().size() == 0);
	}

	@Test
	public void payWork_MBOWorkIsClosed_NoCashChange() throws Exception {
		User mboEmployee = newFirstEmployeeWithMboEnabled();
		authenticationService.setCurrentUser(mboEmployee);
		Work work = newWork(mboEmployee.getId());

		BigDecimal availableCashBefore = accountRegisterService.calcAvailableCash(mboEmployee.getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		authenticationService.setCurrentUser(contractor);
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		authenticationService.setCurrentUser(mboEmployee);
		workService.closeWork(work.getId());

		BigDecimal availableCashAfter = accountRegisterService.calcAvailableCash(mboEmployee.getId());
		assertTrue(availableCashAfter.compareTo(availableCashBefore) == 0);
	}

	@Test
	public void payWork_OfflinePaymentWorkIsClosed_NoCashChange() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		workService.setOfflinePayment(work, true);
		BigDecimal availableCashBefore = accountRegisterService.calcAvailableCash(employee.getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
			CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		authenticationService.setCurrentUser(contractor);
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		authenticationService.setCurrentUser(employee);
		workService.closeWork(work.getId());

		BigDecimal availableCashAfter = accountRegisterService.calcAvailableCash(employee.getId());
		assertTrue(availableCashAfter.compareTo(availableCashBefore) == 0);
	}

	@Test
	public void newUserDoesntHaveAPLimit() throws Exception {
		User user = newFirstEmployee();
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(user.getCompany().getId());
		assertEquals(accountRegister.getApLimit().intValue(), 0);
	}
}
