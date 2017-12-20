package com.workmarket.api.internal.dao;

import com.workmarket.api.internal.model.FundsHoldRegisterTransaction;
import com.workmarket.dao.AbstractDAO;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class FundsHoldRegisterTransactionDAOImpl extends AbstractDAO<FundsHoldRegisterTransaction> implements FundsHoldRegisterTransactionDAO {
  @Override
  protected Class<FundsHoldRegisterTransaction> getEntityClass() {
    return FundsHoldRegisterTransaction.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<FundsHoldRegisterTransaction> findAllByFundsHoldId(long fundsHoldId) {
    final Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
        .setFetchMode("register_transaction", FetchMode.JOIN)
        .add(Restrictions.eq("fundsHold.id", fundsHoldId));
    return criteria.list();
  }
}
