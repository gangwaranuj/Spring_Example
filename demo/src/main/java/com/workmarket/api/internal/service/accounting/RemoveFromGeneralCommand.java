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
public class RemoveFromGeneralCommand extends TransactionCommand {
  public static final RegisterTransactionType REGISTER_TRANSACTION_TYPE = new RegisterTransactionType(RegisterTransactionType.REMOVE_FUNDS_FROM_GENERAL);

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
    summaryFields.setGeneralCash(summaryFields.getGeneralCash().subtract(amount));

    return summaryFields;
  }

  @Override
  protected AccountRegisterSummaryFields reverseSummaryFields(
      final AccountRegister accountRegister,
      final RegisterTransaction registerTransaction) {
    final AccountRegisterSummaryFields summaryFields = new AccountRegisterSummaryFields();
    BeanUtilities.copyProperties(summaryFields, accountRegister.getAccountRegisterSummaryFields());

    final BigDecimal amount = registerTransaction.getAmount().abs();
    summaryFields.setGeneralCash(summaryFields.getGeneralCash().add(amount));

    return summaryFields;
  }
}
