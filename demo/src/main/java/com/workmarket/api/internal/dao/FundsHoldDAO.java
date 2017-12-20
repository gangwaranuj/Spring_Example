package com.workmarket.api.internal.dao;

import com.google.common.base.Optional;

import com.workmarket.api.internal.model.FundsHold;
import com.workmarket.dao.DAOInterface;

public interface FundsHoldDAO extends DAOInterface<FundsHold> {
  FundsHold createFundsHold();

  Optional<FundsHold> findByUuid(String uuid);
}
