package com.workmarket.api.internal.service.accounting;

import com.workmarket.api.internal.dao.PlutusOrderRegisterTransactionDAO;
import com.workmarket.api.internal.model.PlutusOrderRegisterTransaction;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;

@Component
@Scope(value = "prototype")
public abstract class TransactionCommand {
  @Autowired private RegisterTransactionDAO registerTransactionDAO;
  @Autowired private PlutusOrderRegisterTransactionDAO plutusOrderRegisterTransactionDAO;


  protected abstract RegisterTransactionType getRegisterTransactionType();


  protected abstract AccountRegisterSummaryFields executeSummaryFields(
      final AccountRegister accountRegister,
      final RegisterTransaction registerTransaction);


  public void execute(
      final AccountRegister accountRegister,
      final RegisterTransaction registerTransaction,
      final String plutusOrderUuid) {
    registerTransaction.setAccountRegister(accountRegister);
    registerTransaction.setRegisterTransactionType(getRegisterTransactionType());
    registerTransaction.setTransactionDate(Calendar.getInstance());
    registerTransaction.setEffectiveDate(Calendar.getInstance());

    final AccountRegisterSummaryFields summaryFields = executeSummaryFields(accountRegister, registerTransaction);
    accountRegister.setAccountRegisterSummaryFields(summaryFields);
    registerTransaction.setAccountRegisterSummaryFields(summaryFields);

    registerTransactionDAO.saveOrUpdate(registerTransaction);
    final PlutusOrderRegisterTransaction plutusOrderRegisterTransaction = new PlutusOrderRegisterTransaction(plutusOrderUuid, registerTransaction);
    plutusOrderRegisterTransactionDAO.saveOrUpdate(plutusOrderRegisterTransaction);
  }


  protected abstract AccountRegisterSummaryFields reverseSummaryFields(
      final AccountRegister accountRegister,
      final RegisterTransaction registerTransaction);


  public void reverse(
      final AccountRegister accountRegister,
      final RegisterTransaction registerTransaction) {
    registerTransaction.setPendingFlag(false);
    registerTransaction.setEffectiveDate(Calendar.getInstance());

    final AccountRegisterSummaryFields summaryFields = reverseSummaryFields(accountRegister, registerTransaction);
    accountRegister.setAccountRegisterSummaryFields(summaryFields);
    registerTransaction.setAccountRegisterSummaryFields(summaryFields);

    registerTransactionDAO.saveOrUpdate(registerTransaction);
  }


  void deductDepositedAndWithdrawableCash(
      final AccountRegisterSummaryFields summaryFields,
      final BigDecimal amount) {
    if (summaryFields.getDepositedCash().compareTo(amount) > 0) {
      summaryFields.setDepositedCash(summaryFields.getDepositedCash().subtract(amount));
    }
    else {
      final BigDecimal toTakeFromWithdrawableCash = amount.subtract(summaryFields.getDepositedCash());
      summaryFields.setWithdrawableCash(summaryFields.getWithdrawableCash().subtract(toTakeFromWithdrawableCash));
      summaryFields.setDepositedCash(BigDecimal.valueOf(0, summaryFields.getDepositedCash().scale()));
    }
  }
}
