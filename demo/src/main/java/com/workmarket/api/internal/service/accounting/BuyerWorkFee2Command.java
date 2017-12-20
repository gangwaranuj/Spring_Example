package com.workmarket.api.internal.service.accounting;

import com.workmarket.domains.model.account.RegisterTransactionType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class BuyerWorkFee2Command extends AbstractBuyerWorkFeeCommand {
  public static final RegisterTransactionType REGISTER_TRANSACTION_TYPE = new RegisterTransactionType(RegisterTransactionType.NEW_WORK_LANE_2);

  @Override
  protected RegisterTransactionType getRegisterTransactionType() {
    return REGISTER_TRANSACTION_TYPE;
  }
}
