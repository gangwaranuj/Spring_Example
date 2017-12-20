package com.workmarket.service.business.scheduler;

import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccountPagination;
import com.workmarket.domains.model.banking.GlobalCashCardAccount;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.infra.payment.GCCPaymentAdapterImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GccExecutorTest {

	@Mock AuthenticationService authenticationService;
	@Mock UserNotificationService userNotificationService;
	@Mock BillingService billingService;
	@Mock CompanyService companyService;
	@Mock BankingService bankingService;
	@Mock GCCPaymentAdapterImpl globalCashCardService;
	@Mock NotificationService notificationService;
	@Mock EmailTemplateFactory emailTemplateFactory;
	@InjectMocks GccExecutor gccExecutor;

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void deactivateGCCcard_success() {
		try {

			final GlobalCashCardAccount activeCard = new GlobalCashCardAccount() {{
				setAccountNumber("123");
				setId(000l);
				setCompany(new Company() {{
					setId(123l);
				}});
			}};

			final GlobalCashCardAccount inActiveCard = new GlobalCashCardAccount() {{
				setAccountNumber("321");
				setId(111l);
				setCompany(new Company() {{
					setId(321l);
				}});
			}};

			final List bankAccounts = Collections.unmodifiableList(new ArrayList<AbstractBankAccount>() {{
				add(activeCard);
				add(inActiveCard);
			}});

			BankAccountPagination pagination = new BankAccountPagination() {{
				setResults(bankAccounts);
				setRowCount(1);
				setResultsLimit(100);
			}};


			when(globalCashCardService.isDeleted(activeCard.getAccountNumber())).thenReturn(false);
			when(globalCashCardService.isDeleted(inActiveCard.getAccountNumber())).thenReturn(true);
			when(bankingService.findAllActiveGlobalCashCardAccounts(any(BankAccountPagination.class))).thenReturn(pagination);

			gccExecutor.deActivateGccCard();

			verify(bankingService, times(1)).deactivateBankAccount(inActiveCard.getId(), inActiveCard.getCompany().getId());
			verify(bankingService, times(0)).deactivateBankAccount(activeCard.getId(), activeCard.getCompany().getId());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void activateGCCAccount_successs() throws Exception {

		final GlobalCashCardAccount activatedCard = new GlobalCashCardAccount();
		activatedCard.setAccountNumber("123");

		final GlobalCashCardAccount pendingVerificationCard = new GlobalCashCardAccount();
		pendingVerificationCard.setAccountNumber("456");

		final GlobalCashCardAccount cardWithException = new GlobalCashCardAccount();
		cardWithException.setAccountNumber("789");

		List bankAccounts = Collections.unmodifiableList(new ArrayList<AbstractBankAccount>() {{
			add(activatedCard);
			add(pendingVerificationCard);
			add(cardWithException);
		}});

		BankAccountPagination pagination = new BankAccountPagination();
		pagination.setResults(bankAccounts);
		pagination.setRowCount(1);
		pagination.setResultsLimit(100);

		when(bankingService.findAllUnConfirmedGccAccounts(any(BankAccountPagination.class))).thenReturn(pagination);
		when(globalCashCardService.isActive(activatedCard.getAccountNumber())).thenReturn(true);
		when(globalCashCardService.isActive(pendingVerificationCard.getAccountNumber())).thenReturn(false);
		when(globalCashCardService.isActive(cardWithException.getAccountNumber())).thenThrow(new RuntimeException("Couldn't get status"));

		gccExecutor.activateGCCAccounts();
		verify(bankingService, times(1)).confirmGCCAccount(any(Long.class));
	}
}