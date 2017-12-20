package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

import com.workmarket.dao.UserDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.BankAccountTransactionStatus;
import com.workmarket.domains.model.account.BankAccountTransactionStatusHistory;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequestType;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.accountregister.BankTransaction;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionExecutableFactory;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.BeanUtilities;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class BankingFileGenerationIT extends BaseServiceIT {

   @Autowired private UserDAO userDAO;
   @Autowired private BankAccountDAO bankAccountDAO;
   @Autowired @Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterService;

   @Autowired private BankingFileGenerationService bankingFileGenerationService;
   @Autowired @Qualifier("bankAccountTransaction") private BankTransaction bankTransactionExecutor;
   @Autowired private RegisterTransactionExecutableFactory registerTransactionExecutableFactory;

   @Before
   @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
   @Rollback(value=false)
   public void before() throws Exception {

    	User user = userDAO.get(ANONYMOUS_USER_ID);

    	BankAccount bankAccount = super.newBankAccount(user.getCompany());
    	bankAccountDAO.saveOrUpdate(bankAccount);
    	// create pending transactions
    	accountRegisterService.createACHVerificationTransactions(user.getId(), bankAccount);
    	// request processing
    	bankingFileGenerationService.initiateBankFileProcessing(user.getId(), BankingIntegrationGenerationRequestType.ACHVERIFY, "");
  }

   @Test
   @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
   @Rollback(value=false)
   @SuppressWarnings("unchecked")
   public void testACHFileCreation() throws Exception{
	   // process transactions
   		bankingFileGenerationService.processPendingAch();
   }


  @Test
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  @Rollback(value=false)
  public void updateBankTransactionStatus_Update_SingleApprove() throws Exception {
    final BankAccountTransaction transaction = removeFunds();
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.SUBMITTED);
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.APPROVED);

    assertEquals(BankAccountTransactionStatus.APPROVED, transaction.getBankAccountTransactionStatus().getCode());

    checkSummaryRemoveApplied(transaction);
    checkHistory(transaction, ImmutableMap.of(
        BankAccountTransactionStatus.SUBMITTED, 1,
        BankAccountTransactionStatus.APPROVED, 1));
  }


  @Test
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  @Rollback(value=false)
  public void updateBankTransactionStatus_Update_SingleReject() throws Exception {
    final BankAccountTransaction transaction = removeFunds();
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.SUBMITTED);
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.REJECTED);

    assertEquals(BankAccountTransactionStatus.REJECTED, transaction.getBankAccountTransactionStatus().getCode());

    checkSummaryRemoveReversed(transaction);
    checkHistory(transaction, ImmutableMap.of(
        BankAccountTransactionStatus.SUBMITTED, 1,
        BankAccountTransactionStatus.REJECTED, 1));
  }


  @Test
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  @Rollback(value=false)
  public void updateBankTransactionStatus_NoUpdate_ApproveAfterApprove() throws Exception {
    final BankAccountTransaction transaction = removeFunds();
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.SUBMITTED);
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.APPROVED);
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.APPROVED);

    assertEquals(BankAccountTransactionStatus.APPROVED, transaction.getBankAccountTransactionStatus().getCode());

    checkSummaryRemoveApplied(transaction);
    checkHistory(transaction, ImmutableMap.of(
        BankAccountTransactionStatus.SUBMITTED, 1,
        BankAccountTransactionStatus.APPROVED, 2));
  }


  @Test
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  @Rollback(value=false)
  public void updateBankTransactionStatus_NoUpdate_RejectAfterReject() throws Exception {
    final BankAccountTransaction transaction = removeFunds();
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.SUBMITTED);
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.REJECTED);
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.REJECTED);

    assertEquals(BankAccountTransactionStatus.REJECTED, transaction.getBankAccountTransactionStatus().getCode());

    checkSummaryRemoveReversed(transaction);
    checkHistory(transaction, ImmutableMap.of(
        BankAccountTransactionStatus.SUBMITTED, 1,
        BankAccountTransactionStatus.REJECTED, 2));
  }


  @Test
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  @Rollback(value=false)
  public void updateBankTransactionStatus_NoUpdate_ApproveAfterReject() throws Exception {
    final BankAccountTransaction transaction = removeFunds();
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.SUBMITTED);
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.REJECTED);
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.APPROVED);

    assertEquals(BankAccountTransactionStatus.REJECTED, transaction.getBankAccountTransactionStatus().getCode());

    checkSummaryRemoveReversed(transaction);
    checkHistory(transaction, ImmutableMap.of(
        BankAccountTransactionStatus.SUBMITTED, 1,
        BankAccountTransactionStatus.REJECTED, 1,
        BankAccountTransactionStatus.APPROVED, 1));
  }


  @Test
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  @Rollback(value=false)
  public void updateBankTransactionStatus_NoUpdate_RejectAfterApprove() throws Exception {
    final BankAccountTransaction transaction = removeFunds();
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.SUBMITTED);
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.APPROVED);
    bankingFileGenerationService.updateBankTransactionStatus(ANONYMOUS_USER_ID, transaction.getId(), "", BankAccountTransactionStatus.REJECTED);

    assertEquals(BankAccountTransactionStatus.APPROVED, transaction.getBankAccountTransactionStatus().getCode());

    checkSummaryRemoveApplied(transaction);
    checkHistory(transaction, ImmutableMap.of(
        BankAccountTransactionStatus.SUBMITTED, 1,
        BankAccountTransactionStatus.APPROVED, 1,
        BankAccountTransactionStatus.REJECTED, 1));
  }


  private BankAccountTransaction removeFunds() throws Exception {
    final User user = newFirstEmployeeWithCashBalance();
    final BankAccount bankAccount = newBankAccount(user.getCompany());
    bankAccountDAO.saveOrUpdate(bankAccount);

    final AccountRegister accountRegister = accountRegisterService.findDefaultRegisterForCompany(user.getCompany());
    final RegisterTransactionExecutor registerTransactionExecutor = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.FAST_FUNDS_PAYMENT);
    registerTransactionExecutor.execute(accountRegister, new BigDecimal("5000.00"), new RegisterTransaction());

    return bankTransactionExecutor.executeRemove(bankAccount, accountRegister, new BigDecimal("100.00"), new RegisterTransactionType(RegisterTransactionType.REMOVE_FUNDS));
  }


  private void checkSummaryRemoveApplied(final RegisterTransaction transaction) {
    final AccountRegisterSummaryFields actualSummaryFields = transaction.getAccountRegister().getAccountRegisterSummaryFields();
    final AccountRegisterSummaryFields expectedSummaryFields = new AccountRegisterSummaryFields();
    BeanUtilities.copyProperties(expectedSummaryFields, actualSummaryFields);
    expectedSummaryFields.setAvailableCash(new BigDecimal("9900.00"));
    expectedSummaryFields.setGeneralCash(new BigDecimal("9900.00"));
    expectedSummaryFields.setWithdrawableCash(new BigDecimal("4900.00"));
    expectedSummaryFields.setActualCash(new BigDecimal("9900.00"));

    assertEquals(expectedSummaryFields, actualSummaryFields);
  }


  private void checkSummaryRemoveReversed(final RegisterTransaction transaction) {
    final AccountRegisterSummaryFields actualSummaryFields = transaction.getAccountRegister().getAccountRegisterSummaryFields();
    final AccountRegisterSummaryFields expectedSummaryFields = new AccountRegisterSummaryFields();
    BeanUtilities.copyProperties(expectedSummaryFields, actualSummaryFields);
    expectedSummaryFields.setAvailableCash(new BigDecimal("10000.00"));
    expectedSummaryFields.setGeneralCash(new BigDecimal("10000.00"));
    expectedSummaryFields.setWithdrawableCash(new BigDecimal("5000.00"));
    expectedSummaryFields.setActualCash(new BigDecimal("10000.00"));

    assertEquals(expectedSummaryFields, actualSummaryFields);
  }


  private void checkHistory(final BankAccountTransaction transaction, Map<String, Integer> statusCodeFrequencies) {
    final Builder<String> statusCodesBuilder = ImmutableList.builder();
    for (BankAccountTransactionStatusHistory history : transaction.getBankAccountTransactionStatusHistories()) {
      statusCodesBuilder.add(history.getBankAccountTransactionStatus().getCode());
    }
    final ImmutableList<String> statusCodes = statusCodesBuilder.build();
    for (Entry<String, Integer> entry : statusCodeFrequencies.entrySet()) {
      assertEquals(entry.getValue().intValue(), Collections.frequency(statusCodes, entry.getKey()));
    }
  }


  public UserDAO getUserDAO() {
	return userDAO;
}

public BankAccountDAO getBankAccountDAO() {
	return bankAccountDAO;
}


public BankingFileGenerationService getBankingFileGenerationService() {
	return bankingFileGenerationService;
}

public void setUserDAO(UserDAO userDAO) {
	this.userDAO = userDAO;
}

public void setBankAccountDAO(BankAccountDAO bankAccountDAO) {
	this.bankAccountDAO = bankAccountDAO;
}


public void setBankingFileGenerationService(BankingFileGenerationService bankingFileGenerationService) {
	this.bankingFileGenerationService = bankingFileGenerationService;
}
}
