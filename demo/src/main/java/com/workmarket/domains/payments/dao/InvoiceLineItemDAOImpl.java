package com.workmarket.domains.payments.dao;


import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InvoiceLineItemDAOImpl extends PaginationAbstractDAO<InvoiceLineItem> implements InvoiceLineItemDAO {

	@Override
	protected Class<InvoiceLineItem> getEntityClass() {
		return InvoiceLineItem.class;
	}

	@Override
	public void applySorts(Pagination<InvoiceLineItem> pagination, Criteria query, Criteria count) {
	}

	@Override
	public void applyFilters(Pagination<InvoiceLineItem> pagination, Criteria query, Criteria count) {
	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
	}
}
