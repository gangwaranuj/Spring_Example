package com.workmarket.domains.payments.dao;

import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.invoice.WorkMarketSummaryInvoice;
import com.workmarket.domains.model.invoice.WorkMarketSummaryInvoicePagination;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Repository
public class WorkMarketSummaryInvoiceDAOImpl extends PaginationAbstractDAO<WorkMarketSummaryInvoice> implements WorkMarketSummaryInvoiceDAO {

	@Override
	protected Class<WorkMarketSummaryInvoice> getEntityClass() { return WorkMarketSummaryInvoice.class; }

	@Override
	public WorkMarketSummaryInvoicePagination findAll(WorkMarketSummaryInvoicePagination pagination) {
		return (WorkMarketSummaryInvoicePagination) super.paginationQuery(pagination, new HashMap<String, Object>());
	}

	@Override
	public void applySorts(Pagination<WorkMarketSummaryInvoice> pagination, Criteria query, Criteria count) {
		Assert.notNull(pagination);
		Assert.notNull(query);

		String sort = "id";
		if (pagination.getSortColumn() != null) {
			if (WorkMarketSummaryInvoicePagination.SORTS.valueOf(pagination.getSortColumn()) != null) {
				sort = WorkMarketSummaryInvoicePagination.SORTS.valueOf(pagination.getSortColumn()).getColumn();
			}

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
	public void applyFilters(Pagination<WorkMarketSummaryInvoice> pagination, Criteria query, Criteria count) {
		Assert.notNull(pagination);
		Assert.notNull(query);

		if (pagination.getFilters() == null) { return; }

		if (pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.CREATED_DATE_FROM)) {
			String fromDate = pagination.getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.CREATED_DATE_FROM);
			query.add(Restrictions.ge("createdOn", DateUtilities.getCalendarFromISO8601(fromDate)));
			if (count != null) {
				count.add(Restrictions.ge("createdOn", DateUtilities.getCalendarFromISO8601(fromDate)));
			}
		}

		if (pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.DUE_DATE_FROM)) {
			String fromDate = pagination.getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.DUE_DATE_FROM);
			query.add(Restrictions.ge("dueDate", DateUtilities.getCalendarFromISO8601(fromDate)));
			if (count != null) {
				count.add(Restrictions.ge("dueDate", DateUtilities.getCalendarFromISO8601(fromDate)));
			}
		}

		if (pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.DUE_DATE_TO)) {
			String toDate = pagination.getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.DUE_DATE_TO);
			query.add(Restrictions.le("dueDate", DateUtilities.getCalendarFromISO8601(toDate)));
			if (count != null) {
				count.add(Restrictions.le("dueDate", DateUtilities.getCalendarFromISO8601(toDate)));
			}
		}

		if (pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_STATUS)) {
			String status = pagination.getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_STATUS);
			query.add(Restrictions.eq("invoiceStatusType.code", status));
			if (count != null) {
				count.add(Restrictions.eq("invoiceStatusType.code", status));
			}
		}

		if (pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.COMPANY_NAME)) {
			String companyName = pagination.getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.COMPANY_NAME);
			buildLikeClause(query, count, "company.effectiveName", companyName);
		}

		if (pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_NUMBER)) {
			String invoiceNumber = pagination.getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_NUMBER);
			buildLikeClause(query, count, "invoiceNumber", invoiceNumber);
		}
	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
		Assert.notNull(query);

		query.add(Restrictions.in("type", new Object[] {"adHoc", "subscription", "creditMemo"}));
		query.add(Restrictions.eq("deleted", false)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		query.createAlias("company", "company", CriteriaSpecification.INNER_JOIN);
		if (count != null) {
			count.add(Restrictions.in("type", new Object[] {"adHoc", "subscription", "creditMemo"}));
			count.add(Restrictions.eq("deleted", false)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			count.createAlias("company", "company", CriteriaSpecification.INNER_JOIN);
		}
	}

	protected void buildLikeClause(Criteria query, Criteria count, final String column, final String value) {
		query.add(Restrictions.ilike(column, StringUtilities.processForLike(value)));
		if (count != null) {
			count.add(Restrictions.ilike(column, StringUtilities.processForLike(value)));
		}
	}
}
