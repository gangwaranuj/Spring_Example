package com.workmarket.api.internal.dao;

import com.workmarket.api.internal.model.PlutusInvoiceInvoice;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.invoice.Invoice;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class PlutusInvoiceInvoiceDAOImpl extends AbstractDAO<PlutusInvoiceInvoice> implements PlutusInvoiceInvoiceDAO {

  @Override
  protected Class<PlutusInvoiceInvoice> getEntityClass() {
    return PlutusInvoiceInvoice.class;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<Invoice> findAllInvoicesByPlutusInvoiceUuid(final String plutusInvoiceUuid) {
    final Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
        .setFetchMode("invoice", FetchMode.JOIN)
        .add(Restrictions.eq("plutusInvoiceUuid", plutusInvoiceUuid))
        .setProjection(Projections.property("invoice"));
    return criteria.list();
  }
}
