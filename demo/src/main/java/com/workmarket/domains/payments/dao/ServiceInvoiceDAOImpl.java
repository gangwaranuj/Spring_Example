package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.model.invoice.ServiceInvoicePagination;
import com.workmarket.dto.AggregatesDTO;
import com.workmarket.utility.DateUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ServiceInvoiceDAOImpl extends AbstractInvoiceDAOImpl<AbstractServiceInvoice> implements ServiceInvoiceDAO {

	@Override
	protected Class<AbstractServiceInvoice> getEntityClass() {
		return AbstractServiceInvoice.class;
	}

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public ServiceInvoicePagination findAll(ServiceInvoicePagination pagination) {
		return findAll(new HashMap<String, Object>(), pagination);
	}

	@Override
	public AggregatesDTO getAllServiceInvoicesTotal() {
		AggregatesDTO dto = new AggregatesDTO();
		String sql = "SELECT SUM(balance) balance, invoice_status_type_code \n" +
				"FROM 	invoice WHERE type IN ('adHoc', 'subscription') \n" +
				"GROUP 	BY invoice_status_type_code ";
		List<Map<String, Object>> customFields = jdbcTemplate.queryForList(sql, new MapSqlParameterSource());
		for (Map<String, Object> row : customFields) {
			String invoiceStatus = (String)row.get("invoice_status_type_code");
			BigDecimal balance = (BigDecimal)row.get("balance");
			dto.setTotalForStatus(invoiceStatus, balance);
		}
		return dto;
	}

	private ServiceInvoicePagination findAll(Map<String, Object> params, ServiceInvoicePagination pagination) {
		pagination = (ServiceInvoicePagination) super.paginationQuery(pagination, params);
		return pagination;
	}

	@Override
	public void applySorts(Pagination<AbstractServiceInvoice> pagination, Criteria query, Criteria count) {
		String sort = "id";
		if (pagination.getSortColumn() != null) {

			if (ServiceInvoicePagination.SORTS.valueOf(pagination.getSortColumn()) != null)
				sort = ServiceInvoicePagination.SORTS.valueOf(pagination.getSortColumn())
						.getColumn();

			if (pagination.getSortDirection()
					.equals(Pagination.SORT_DIRECTION.DESC)) {
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
	public void applyFilters(Pagination<AbstractServiceInvoice> pagination, Criteria query, Criteria count) {
		if (pagination.getFilters() == null) {
			return;
		}

		if (pagination.hasFilter(ServiceInvoicePagination.FILTER_KEYS.CREATED_DATE_FROM)) {
			String fromDate = pagination.getFilter(ServiceInvoicePagination.FILTER_KEYS.CREATED_DATE_FROM);
			query.add(Restrictions.ge("createdOn", DateUtilities.getCalendarFromISO8601(fromDate)));
			if (count != null) {
				count.add(Restrictions.ge("createdOn", DateUtilities.getCalendarFromISO8601(fromDate)));
			}
		}

		if (pagination.hasFilter(ServiceInvoicePagination.FILTER_KEYS.DUE_DATE_FROM)) {
			String fromDate = pagination.getFilter(ServiceInvoicePagination.FILTER_KEYS.DUE_DATE_FROM);
			query.add(Restrictions.ge("dueDate", DateUtilities.getCalendarFromISO8601(fromDate)));
			if (count != null) {
				count.add(Restrictions.ge("dueDate", DateUtilities.getCalendarFromISO8601(fromDate)));
			}
		}

		if (pagination.hasFilter(ServiceInvoicePagination.FILTER_KEYS.DUE_DATE_TO)) {
			String toDate = pagination.getFilter(ServiceInvoicePagination.FILTER_KEYS.DUE_DATE_TO);
			query.add(Restrictions.le("dueDate", DateUtilities.getCalendarFromISO8601(toDate)));
			if (count != null) {
				count.add(Restrictions.le("dueDate", DateUtilities.getCalendarFromISO8601(toDate)));
			}
		}

		if (pagination.hasFilter(ServiceInvoicePagination.FILTER_KEYS.INVOICE_STATUS)) {
			String status = pagination.getFilter(ServiceInvoicePagination.FILTER_KEYS.INVOICE_STATUS);
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
				if (count != null)
					count.add(Restrictions.eq(e.getKey(), e.getValue()));
			}
		}

		query.add(Restrictions.eq("deleted", false)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		query.createAlias("company", "company", CriteriaSpecification.INNER_JOIN);
		if (count != null) {
			count.add(Restrictions.eq("deleted", false)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			count.createAlias("company", "company", CriteriaSpecification.INNER_JOIN);
		}
	}

}
