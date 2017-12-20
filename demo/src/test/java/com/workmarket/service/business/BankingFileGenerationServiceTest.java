package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.dao.banking.BankingIntegrationGenerationRequestDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.BankAccountTransactionStatus;
import com.workmarket.domains.model.account.GlobalCashCardTransactionResponse;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequest;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequestStatus;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequestType;
import com.workmarket.domains.model.banking.EmptyHeaderMapper;
import com.workmarket.domains.model.banking.NachaRowMapper;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.dao.GlobalCashCardAccountDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.factory.BankingIntegrationGenerationRequestRowMapperFactory;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionExecutableFactory;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.event.UpdateBankTransactionsStatusEvent;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.jms.JmsService;
import com.workmarket.service.infra.payment.GCCPaymentAdapterImpl;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.services.VaultHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankingFileGenerationServiceTest {

	@Mock BankingIntegrationGenerationRequestDAO bankingIntegrationGenerationRequestDAO;
	@Mock RegisterTransactionDAO registerTransactionDAO;
	@Mock AssetManagementService assetManagementService;
	@Mock JmsService jmsService;
	@Mock UserNotificationService userNotificationService;
	@Mock BankingIntegrationGenerationRequestRowMapperFactory rowMapperFactory;
	@Mock RegisterTransactionExecutableFactory registerTransactionExecutableFactory;
	@Mock UserService userService;
	@Mock GlobalCashCardAccountDAO globalCashCardAccountDAO;
	@Mock GCCPaymentAdapterImpl globalCashCardService;
	@Mock EventRouter eventRouter;
	@Mock FeatureEvaluator featureEvaluator;
	@Mock AuthenticationService authenticationService;
	@Mock VaultHelper vaultHelper;
	@Mock PricingService pricingService;
	@InjectMocks BankingFileGenerationServiceImpl bankingFileGenerationService;

	private List<Long> transactionIds;
	private Long userId;
	private String notes;
	private BankingIntegrationGenerationRequest bankingIntegrationGenerationRequest;
	private final BankingIntegrationGenerationRequestType outboundType = new BankingIntegrationGenerationRequestType(BankingIntegrationGenerationRequestType.OUTBOUND);
	private BankAccountTransaction gccTransaction;
	private GlobalCashCardTransactionResponse globalCashCardTransactionResponse;

	@Before
	public void setup() throws Exception {
		transactionIds = Lists.newArrayList();
		userId = 1L;
		notes = "notes";

		gccTransaction = mock(BankAccountTransaction.class);
		globalCashCardTransactionResponse = mock(GlobalCashCardTransactionResponse.class);
		when(gccTransaction.getAmount()).thenReturn(new BigDecimal(-100));
		when(gccTransaction.getId()).thenReturn(1L);
		when(gccTransaction.getBankAccount()).thenReturn(new BankAccount());

		bankingIntegrationGenerationRequest = mock(BankingIntegrationGenerationRequest.class);
		when(bankingIntegrationGenerationRequest.getBankingIntegrationGenerationRequestType()).thenReturn(outboundType);

		when(rowMapperFactory.getHeaderMapper(any(BankingIntegrationGenerationRequestType.class))).thenReturn(new EmptyHeaderMapper());
		when(rowMapperFactory.getRowMapper(any(BankingIntegrationGenerationRequestType.class))).thenReturn(new NachaRowMapper());

		when(globalCashCardTransactionResponse.getStatus()).thenReturn(BankingFileGenerationServiceImpl.GCC_SUCCESS);
		when(globalCashCardService.loadCard(anyString(), any(BigDecimal.class))).thenReturn(globalCashCardTransactionResponse);

		when(featureEvaluator.hasFeature(anyLong(), anyString())).thenReturn(false);
		when(authenticationService.getCurrentUserCompanyId()).thenReturn(1L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void initiateBankFileProcessing_withNullArguments() {
		bankingFileGenerationService.initiateBankFileProcessing(null, null, null);
	}

	@Test
	public void updateBankTransactionsStatusAsync_approve_success() {
		bankingFileGenerationService.updateBankTransactionsStatusAsync(userId, transactionIds, notes, BankAccountTransactionStatus.APPROVED);
		verify(eventRouter).sendEvent(any(UpdateBankTransactionsStatusEvent.class));
	}

	@Test
	public void markBankAccountTransactionProcessing_success() {
		bankingFileGenerationService.markBankAccountTransactionProcessing(transactionIds);
		verify(registerTransactionDAO).markBankAccountTransactionProcessing(transactionIds);
	}

	@Test
	public void processPendingOutbound_withNullCountry_success() throws Exception {
		Country country = null;
		bankingFileGenerationService.processPendingOutbound(country);
		verify(bankingIntegrationGenerationRequestDAO, times(1)).findByTypeAndStatus(eq(BankingIntegrationGenerationRequestType.NON_USA_OUTBOUND), eq(BankingIntegrationGenerationRequestStatus.SUBMITTED));
	}

	@Test
	public void processPendingOutbound_withUSACountry_success() throws Exception {
		Country country = Country.USA_COUNTRY;
		bankingFileGenerationService.processPendingOutbound(country);
		verify(bankingIntegrationGenerationRequestDAO, times(1)).findByTypeAndStatus(eq(BankingIntegrationGenerationRequestType.OUTBOUND), eq(BankingIntegrationGenerationRequestStatus.SUBMITTED));
	}

	@Test
	public void processPendingOutbound_withCanadaCountry_success() throws Exception {
		Country country = Country.CANADA_COUNTRY;
		bankingFileGenerationService.processPendingOutbound(country);
		verify(bankingIntegrationGenerationRequestDAO, times(1)).findByTypeAndStatus(eq(BankingIntegrationGenerationRequestType.NON_USA_OUTBOUND), eq(BankingIntegrationGenerationRequestStatus.SUBMITTED));
	}

	@Test(expected = IllegalArgumentException.class)
	public void processTransactionsForType_withNullArguments_fail() throws Exception {
		bankingFileGenerationService.processTransactionsForType(null, null, false);
	}

	@Test
	public void processTransactionsForType_withEmptyList_wontGenerateFile() throws Exception {
		bankingFileGenerationService.processTransactionsForType(bankingIntegrationGenerationRequest, new ArrayList<BankAccountTransaction>(), false);
		verify(rowMapperFactory, times(1)).getHeaderMapper(eq(outboundType));
		verify(rowMapperFactory, times(1)).getRowMapper(eq(outboundType));
		verify(assetManagementService, never()).storeAssetForBankingFile(any(AssetDTO.class), any(BankingIntegrationGenerationRequest.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void processRemoveFundsGCC_withNullArguments_fail() throws Exception {
		bankingFileGenerationService.processRemoveFundsGCC(null);
	}

	@Test
	public void processRemoveFundsGCC_success() throws Exception {
		bankingFileGenerationService.processRemoveFundsGCC(gccTransaction);
		verify(globalCashCardAccountDAO, times(1)).findAccountNumber(anyLong());
		verify(globalCashCardService, times(1)).loadCard(anyString(), eq(BigDecimal.valueOf(100)));
	}

	@Test
	public void processPendingAch_doesNotSetAccountNumberFromVault() throws Exception {
		BankingIntegrationGenerationRequest request = mock(BankingIntegrationGenerationRequest.class);
		BankAccountTransaction tx = mock(BankAccountTransaction.class);
		BankAccount bankAccount = mock(BankAccount.class);
		Company company = mock(Company.class);
		BankAccountTransaction bankAccountTransaction = mock(BankAccountTransaction.class);
		RegisterTransactionType registerTransactionType = mock(RegisterTransactionType.class);
		AccountRegister accountRegister = mock(AccountRegister.class);
		BankAccountType bankAccountType = mock(BankAccountType.class);
		BankingIntegrationGenerationRequestType bankingIntegrationGenerationRequestType =
				mock(BankingIntegrationGenerationRequestType.class);
		when(bankingIntegrationGenerationRequestType.getCode()).thenReturn("some-code");
		when(registerTransactionType.getCode()).thenReturn("some-code");
		when(bankAccountTransaction.getRegisterTransactionType()).thenReturn(registerTransactionType);
		when(tx.getBankAccount()).thenReturn(bankAccount);
		when(tx.getRegisterTransactionType()).thenReturn(registerTransactionType);
		when(tx.getAccountRegister()).thenReturn(accountRegister);
		when(accountRegister.getCompany()).thenReturn(company);
		when(bankAccount.getCompany()).thenReturn(company);
		when(bankAccount.getRoutingNumber()).thenReturn("011000138");
		when(bankAccount.getBankAccountType()).thenReturn(bankAccountType);
		when(bankAccountType.isCheckingAccount()).thenReturn(true);
		when(company.getId()).thenReturn(1L);
		when(bankAccountTransaction.getBankAccount()).thenReturn(bankAccount);
		when(registerTransactionDAO.get(any(Long.class))).thenReturn(bankAccountTransaction);
		when(request.getBankingIntegrationGenerationRequestType()).thenReturn(bankingIntegrationGenerationRequestType);
		when(bankingIntegrationGenerationRequestDAO
						.findByTypeAndStatus(
								BankingIntegrationGenerationRequestType.ACHVERIFY,
								BankingIntegrationGenerationRequestStatus.SUBMITTED
						)
		).thenReturn(ImmutableList.of(request));
		when(registerTransactionDAO.findBankAccountTransactions(any(String.class), any(String.class), any(Country.class)))
				.thenReturn(ImmutableList.of(tx));
		when(vaultHelper.get(any(AbstractEntity.class), eq("accountNumber"), eq("")))
				.thenReturn(new VaultKeyValuePair("key", "unobfuscatedAccountNumber"));

		bankingFileGenerationService.processPendingAch();

		// Verify we don't accidentally override the account number and initiate an errant Hibernate commit under the covers
		verify(bankAccount, times(0)).setAccountNumber(any(String.class));
		verify(bankAccount, times(1)).getAccountNumberSanitized();
		// Verify we aren't going to the vault when feature flag is off
		verify(vaultHelper, times(0)).setVaultedValues(any(AbstractEntity.class));
		verify(vaultHelper, times(0)).get(any(AbstractEntity.class), eq("accountNumber"), eq(""));

		// Verify when we do need to get unobfuscated value from Vault
		when(featureEvaluator.hasFeature(any(Long.class), eq("vaultRead"))).thenReturn(true);

		bankingFileGenerationService.processPendingAch();

		verify(vaultHelper, times(0)).setVaultedValues(any(AbstractEntity.class));
		verify(vaultHelper, times(1)).get(any(AbstractEntity.class), eq("accountNumber"), eq(""));
	}
}
