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
public class BuyerWorkPaymentCommand extends TransactionCommand {
  public static final RegisterTransactionType REGISTER_TRANSACTION_TYPE = new RegisterTransactionType(RegisterTransactionType.BUYER_WORK_PAYMENT);

  @Override
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
    summaryFields.setAvailableCash(summaryFields.getAvailableCash().subtract(amount));
    deductDepositedAndWithdrawableCash(summaryFields, amount);
    summaryFields.setActualCash(summaryFields.getActualCash().subtract(amount));
    summaryFields.setAssignmentThroughput(summaryFields.getAssignmentThroughput().add(amount));
    final BigDecimal swThroughputAmount = summaryFields.getAssignmentSoftwareThroughput().add(amount);
    summaryFields.setAssignmentSoftwareThroughput(swThroughputAmount);
    summaryFields.setAssignmentVorThroughput(swThroughputAmount);

    return summaryFields;
  }

  @Override
  protected AccountRegisterSummaryFields reverseSummaryFields(
      final AccountRegister accountRegister,
      final RegisterTransaction registerTransaction) {
    return accountRegister.getAccountRegisterSummaryFields();
  }
}
