package com.workmarket.api.internal.dao;

import com.google.common.base.Optional;

import com.workmarket.api.internal.model.FundsHold;
import com.workmarket.dao.AbstractDAO;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.UUID;

@Repository
public class FundsHoldDAOImpl extends AbstractDAO<FundsHold>  implements FundsHoldDAO {
  @Override
  public FundsHold createFundsHold() {
    final FundsHold fundsHold = new FundsHold();
    saveOrUpdate(fundsHold);
    return fundsHold;
  }


  @Override
  protected Class<FundsHold> getEntityClass() {
    return FundsHold.class;
  }


  private void setUuidIfEmpty(final FundsHold entity) {
    if (entity.getUuid() == null) {
      entity.setUuid(UUID.randomUUID().toString());
    }
  }


  @Override
  public void saveOrUpdate(final FundsHold entity) {
    setUuidIfEmpty(entity);
    super.saveOrUpdate(entity);
  }


  @Override
  public void persist(final FundsHold entity) {
    setUuidIfEmpty(entity);
    super.persist(entity);
  }

  @Override
  public void saveAll(final Collection<FundsHold> entities) {
    for (final FundsHold entity : entities) {
      setUuidIfEmpty(entity);
    }
    super.saveAll(entities);
  }


  @Override
  public Optional<FundsHold> findByUuid(String uuid) {
    Assert.notNull(uuid);
    return Optional.fromNullable((FundsHold) getFactory().getCurrentSession().createCriteria(getEntityClass())
        .add(Restrictions.eq("uuid", uuid)).uniqueResult());
  }
}
