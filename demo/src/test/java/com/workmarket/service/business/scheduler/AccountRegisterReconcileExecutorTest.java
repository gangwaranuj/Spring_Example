package com.workmarket.service.business.scheduler;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AccountRegisterReconcileExecutorTest {

	@Mock private AuthenticationService authenticationService;
	@Mock private AccountRegisterService accountRegisterService;
	@Mock private UserNotificationService userNotificationService;

	@InjectMocks AccountRegisterReconcileExecutor accountRegisterReconcileExecutor;

	private AccountRegister accountRegister;
	private AccountRegisterSummaryFields summaryFields;
	private Company company;

	private Long accountRegisterId;

	@Before
	public void setUp() throws Exception {
		accountRegisterId = new Long(10);
		accountRegister = mock(AccountRegister.class);
		summaryFields = mock(AccountRegisterSummaryFields.class);
		company = mock(Company.class);

		when(company.getName()).thenReturn("Company");
		when(company.getId()).thenReturn(1l);

		when(accountRegister.getAccountRegisterSummaryFields()).thenReturn(summaryFields);
		when(accountRegister.getCompany()).thenReturn(company);
		when(accountRegisterService.getAccountRegisterById(accountRegisterId)).thenReturn(accountRegister);
	}


	@Test
	public void reconcile_account_negative_balance_returns_message() {
		// available cash
		when(accountRegisterService.calculateAvailableCashByAccountRegister(anyLong())).thenReturn(new BigDecimal(-60.5));
		when(accountRegisterService.getSumSpentAvailableCash(anyLong())).thenReturn(new BigDecimal(-50));
		when(summaryFields.getAvailableCash()).thenReturn(new BigDecimal(-110.5));

		// payment terms
		when(accountRegisterService.findPaymentTermsCommitmentBalance(anyLong())).thenReturn(new BigDecimal(-30));
		when(summaryFields.getAccountsPayableBalance()).thenReturn(new BigDecimal(30));

		String result = accountRegisterReconcileExecutor.reconcileAccount(accountRegisterId);
		assertEquals("URGENT: AccountRegisterId:10 has a negative balance of:-110.5<br>", result);
	}

	@Test
	public void reconcile_account_available_cash_mismatch_returns_message() {
		// available cash
		when(accountRegisterService.calculateAvailableCashByAccountRegister(anyLong())).thenReturn(new BigDecimal(50.5));
		when(accountRegisterService.getSumSpentAvailableCash(anyLong())).thenReturn(new BigDecimal(-35));
		when(summaryFields.getAvailableCash()).thenReturn(new BigDecimal(25));

		// payment terms
		when(accountRegisterService.findPaymentTermsCommitmentBalance(anyLong())).thenReturn(new BigDecimal(-30));
		when(summaryFields.getAccountsPayableBalance()).thenReturn(new BigDecimal(30));

		String result = accountRegisterReconcileExecutor.reconcileAccount(accountRegisterId);
		assertEquals("Company: Company:1, accountRegisterId:10, availableCash:25 - registerTransaction-Balance:15.5 = 9.5 \n" +
			"<br>", result);
	}

	@Test
	public void reconcile_account_available_cash_mismatch_known_issue_returns_message() {
		accountRegisterId = new Long(39177l);
		accountRegister = mock(AccountRegister.class);
		summaryFields = mock(AccountRegisterSummaryFields.class);
		company = mock(Company.class);

		when(company.getName()).thenReturn("The King Inc");
		when(company.getId()).thenReturn(40236l);

		when(accountRegister.getAccountRegisterSummaryFields()).thenReturn(summaryFields);
		when(accountRegister.getCompany()).thenReturn(company);
		when(accountRegisterService.getAccountRegisterById(accountRegisterId)).thenReturn(accountRegister);


		// available cash
		when(accountRegisterService.calculateAvailableCashByAccountRegister(accountRegisterId)).thenReturn(new BigDecimal("2.66"));
		when(accountRegisterService.getSumSpentAvailableCash(accountRegisterId)).thenReturn(new BigDecimal("-.20"));
		when(summaryFields.getAvailableCash()).thenReturn(new BigDecimal("2.26"));

		// payment terms
		when(accountRegisterService.findPaymentTermsCommitmentBalance(accountRegisterId)).thenReturn(new BigDecimal(-30));
		when(summaryFields.getAccountsPayableBalance()).thenReturn(new BigDecimal(30));

		String result = accountRegisterReconcileExecutor.reconcileAccount(accountRegisterId);
		assertEquals("KNOWN ISSUE: Company: The King Inc:40236, accountRegisterId:39177, availableCash:2.26 - registerTransaction-Balance:2.46 = -0.20 \n" +
			"<br>", result);
	}

	@Test
	public void reconcile_account_payment_terms_mismatch_returns_message() {
		// available cash
		when(accountRegisterService.calculateAvailableCashByAccountRegister(anyLong())).thenReturn(new BigDecimal(50.5));
		when(accountRegisterService.getSumSpentAvailableCash(anyLong())).thenReturn(new BigDecimal(-35));
		when(summaryFields.getAvailableCash()).thenReturn(new BigDecimal(15.5));

		// payment terms
		when(accountRegisterService.findPaymentTermsCommitmentBalance(anyLong())).thenReturn(new BigDecimal(-20));
		when(summaryFields.getAccountsPayableBalance()).thenReturn(new BigDecimal(30));

		String result = accountRegisterReconcileExecutor.reconcileAccount(accountRegisterId);
		assertEquals("accountRegisterId: 10, accountsPayableBalanceFromRegister:30, accountsPayableBalance:-20, differenceInPaymentTermsBalance:10 \n" +
			"<br>", result);
	}

	@Test
	public void reconcile_account_valid_returns_blank() {
		// available cash
		when(accountRegisterService.calculateAvailableCashByAccountRegister(anyLong())).thenReturn(new BigDecimal(50.5));
		when(accountRegisterService.getSumSpentAvailableCash(anyLong())).thenReturn(new BigDecimal(-35));
		when(summaryFields.getAvailableCash()).thenReturn(new BigDecimal(15.5));

		// payment terms
		when(accountRegisterService.findPaymentTermsCommitmentBalance(anyLong())).thenReturn(new BigDecimal(-20));
		when(summaryFields.getAccountsPayableBalance()).thenReturn(new BigDecimal(20));

		String result = accountRegisterReconcileExecutor.reconcileAccount(accountRegisterId);
		assertEquals("", result);
	}

	@Test
	public void reconcile_account_available_cash_and_payment_terms_mismatch_returns_message() {
		// available cash
		when(accountRegisterService.calculateAvailableCashByAccountRegister(anyLong())).thenReturn(new BigDecimal(50.5));
		when(accountRegisterService.getSumSpentAvailableCash(anyLong())).thenReturn(new BigDecimal(-35));
		when(summaryFields.getAvailableCash()).thenReturn(new BigDecimal(25.5));

		// payment terms
		when(accountRegisterService.findPaymentTermsCommitmentBalance(anyLong())).thenReturn(new BigDecimal(-20));
		when(summaryFields.getAccountsPayableBalance()).thenReturn(new BigDecimal(30));

		String result = accountRegisterReconcileExecutor.reconcileAccount(accountRegisterId);
		assertEquals("Company: Company:1, accountRegisterId:10, availableCash:25.5 - registerTransaction-Balance:15.5 = 10.0 \n" +
			"<br>accountRegisterId: 10, accountsPayableBalanceFromRegister:30, accountsPayableBalance:-20, differenceInPaymentTermsBalance:10 \n" +
			"<br>", result);
	}
}
