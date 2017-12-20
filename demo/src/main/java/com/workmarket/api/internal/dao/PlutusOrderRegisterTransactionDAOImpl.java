package com.workmarket.api.internal.dao;

import com.workmarket.api.internal.model.PlutusOrderRegisterTransaction;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.RegisterTransaction;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class PlutusOrderRegisterTransactionDAOImpl extends AbstractDAO<PlutusOrderRegisterTransaction> implements PlutusOrderRegisterTransactionDAO {

  @Override
  protected Class<PlutusOrderRegisterTransaction> getEntityClass() {
    return PlutusOrderRegisterTransaction.class;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<RegisterTransaction> findAllRegisterTransactionsByPlutusOrderUuid(
      final String plutusOrderUuid) {
    final Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
        .setFetchMode("register_transaction", FetchMode.JOIN)
        .add(Restrictions.eq("plutusOrderUuid", plutusOrderUuid))
        .setProjection(Projections.property("registerTransaction"));
    return criteria.list();
  }
}
