package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.invoice.InvoicePagination;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.StatementPagination;
import com.workmarket.utility.DateUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class StatementDAOImpl extends AbstractInvoiceDAOImpl<Statement> implements StatementDAO {

	@Override
	protected Class<Statement> getEntityClass() {
		return Statement.class;
	}

	@Override
	public StatementPagination findAllStatements(Long companyId, StatementPagination pagination) {
		Map<String, Object> params = new HashMap<>();
		params.put("company.id", companyId);		
		return (StatementPagination) super.paginationQuery(pagination, params);
	}

	@Override
	public void applySorts(Pagination<Statement> pagination, Criteria query, Criteria count) {
		String sort = "id";
		if (pagination.getSortColumn() != null) {

			if (StatementPagination.SORTS.valueOf(pagination.getSortColumn()) != null)
				sort = InvoicePagination.SORTS.valueOf(pagination.getSortColumn()).getColumn();

			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				query.addOrder(Order.desc(sort));
				count.addOrder(Order.desc(sort));
			} else {
				query.addOrder(Order.asc(sort));
				count.addOrder(Order.asc(sort));
			}
		} else {
			query.addOrder(Order.desc("id"));
		}
	}

	@Override
	public void applyFilters(Pagination<Statement> pagination, Criteria query, Criteria count) {
		if (pagination.getFilters() == null) {
			return;
		}

		if (pagination.getFilters().get(StatementPagination.FILTER_KEYS.DUE_DATE_FROM.toString()) != null) {
			String fromDate = pagination.getFilters().get(StatementPagination.FILTER_KEYS.DUE_DATE_FROM.toString());
			query.add(Restrictions.ge("dueDate", DateUtilities.getCalendarFromISO8601(fromDate)));
			if (count != null) {
				count.add(Restrictions.ge("dueDate", DateUtilities.getCalendarFromISO8601(fromDate)));
			}
		}

		if (pagination.getFilters().get(StatementPagination.FILTER_KEYS.DUE_DATE_TO.toString()) != null) {
			String toDate = pagination.getFilters().get(StatementPagination.FILTER_KEYS.DUE_DATE_TO.toString());
			query.add(Restrictions.le("dueDate", DateUtilities.getCalendarFromISO8601(toDate)));
			if (count != null) {
				count.add(Restrictions.le("dueDate", DateUtilities.getCalendarFromISO8601(toDate)));
			}
		}

		if (pagination.getFilters().get(StatementPagination.FILTER_KEYS.INVOICE_STATUS.toString()) != null) {
			String status = pagination.getFilters().get(StatementPagination.FILTER_KEYS.INVOICE_STATUS.toString());
			query.add(Restrictions.eq("invoiceStatusType.code", status));
			if (count != null) {
				count.add(Restrictions.eq("invoiceStatusType.code", status));
			}
		}

	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
		for (Map.Entry<String, Object> e : params.entrySet()) {
			if (e.getKey() != null && e.getValue() != null) {
				query.add(Restrictions.eq(e.getKey(), e.getValue()));
				if (count != null) {
					count.add(Restrictions.eq(e.getKey(), e.getValue()));
				}
			}
		}

		query.add(Restrictions.eq("deleted", false))
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		if (count != null) {
			count.add(Restrictions.eq("deleted", false))
					.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		}
	}
}
