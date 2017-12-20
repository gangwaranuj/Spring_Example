package com.workmarket.api.internal.service.accounting;

import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.utility.BeanUtilities;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Scope(value = "prototype")
public abstract class AbstractBuyerWorkFeeCommand extends TransactionCommand {
  @Override
  protected AccountRegisterSummaryFields executeSummaryFields(
      final AccountRegister accountRegister,
      final RegisterTransaction registerTransaction) {
    final AccountRegisterSummaryFields summaryFields = new AccountRegisterSummaryFields();
    BeanUtilities.copyProperties(summaryFields, accountRegister.getAccountRegisterSummaryFields());

    final BigDecimal amount = registerTransaction.getAmount().abs();
    summaryFields.setAvailableCash(summaryFields.getAvailableCash().subtract(amount));
    deductDepositedAndWithdrawableCash(summaryFields, amount);
    summaryFields.setActualCash(summaryFields.getActualCash().subtract(amount));

    return summaryFields;
  }

  @Override
  protected AccountRegisterSummaryFields reverseSummaryFields(
      final AccountRegister accountRegister,
      final RegisterTransaction registerTransaction) {
    return accountRegister.getAccountRegisterSummaryFields();
  }
}
