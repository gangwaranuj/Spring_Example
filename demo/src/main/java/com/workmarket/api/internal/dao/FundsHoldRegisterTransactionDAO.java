package com.workmarket.api.internal.dao;

import com.workmarket.api.internal.model.FundsHoldRegisterTransaction;
import com.workmarket.dao.DAOInterface;

import java.util.Collection;

public interface FundsHoldRegisterTransactionDAO extends DAOInterface<FundsHoldRegisterTransaction> {
  Collection<FundsHoldRegisterTransaction> findAllByFundsHoldId(long fundsHoldId);
}
