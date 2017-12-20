package com.workmarket.api.internal.service.accounting;

import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.utility.BeanUtilities;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Scope(value = "prototype")
public class SellerWorkPaymentCommand extends TransactionCommand {
  public static final RegisterTransactionType REGISTER_TRANSACTION_TYPE = new RegisterTransactionType(RegisterTransactionType.RESOURCE_WORK_PAYMENT);

  protected RegisterTransactionType getRegisterTransactionType() {
    return REGISTER_TRANSACTION_TYPE;
  }

  @Override
  protected AccountRegisterSummaryFields executeSummaryFields(
      final AccountRegister accountRegister,
      final RegisterTransaction registerTransaction) {
    final AccountRegisterSummaryFields summaryFields = new AccountRegisterSummaryFields();
    BeanUtilities.copyProperties(summaryFields, accountRegister.getAccountRegisterSummaryFields());

    final BigDecimal amount = registerTransaction.getAmount().abs();
    summaryFields.setWithdrawableCash(summaryFields.getWithdrawableCash().add(amount));
    summaryFields.setAvailableCash(summaryFields.getAvailableCash().add(amount));
    summaryFields.setActualCash(summaryFields.getActualCash().add(amount));
    summaryFields.setGeneralCash(summaryFields.getGeneralCash().add(amount));

    return summaryFields;
  }

  @Override
  protected AccountRegisterSummaryFields reverseSummaryFields(
      final AccountRegister accountRegister,
      final RegisterTransaction registerTransaction) {
    return accountRegister.getAccountRegisterSummaryFields();
  }
}
