package com.workmarket.api.internal.dao;

import com.workmarket.api.internal.model.PlutusOrderRegisterTransaction;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.RegisterTransaction;

import java.util.Collection;

public interface PlutusOrderRegisterTransactionDAO extends DAOInterface<PlutusOrderRegisterTransaction> {
  Collection<RegisterTransaction> findAllRegisterTransactionsByPlutusOrderUuid(final String plutusOrderUuid);
}
