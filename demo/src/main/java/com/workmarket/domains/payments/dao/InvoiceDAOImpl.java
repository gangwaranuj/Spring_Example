package com.workmarket.domains.payments.dao;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.company.CompanyStatusType;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoicePagination;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.utility.DateUtilities;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import java.math.BigDecimal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InvoiceDAOImpl extends AbstractInvoiceDAOImpl<Invoice> implements InvoiceDAO {

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	@Override
	protected Class<Invoice> getEntityClass() {
		return Invoice.class;
	}

	@Override
	public InvoicePagination findAllByCompanyId(long companyId, InvoicePagination pagination){
		Map<String, Object> params = new HashMap<>();
		params.put("company.id", companyId);
		return findAll(params, pagination);
	}
	
	private InvoicePagination findAll(Map<String, Object> params, InvoicePagination pagination){
		pagination = (InvoicePagination)super.paginationQuery(pagination, params);
		
		// Reuse the where/filter helpers to build a total criteria query
		Criteria total = getFactory().getCurrentSession().createCriteria(getEntityClass());
		total.setProjection(Projections.sum("balance"));
		buildWhereClause(total, null, params);
		applyFilters(pagination, total, null);
		
		pagination.setTotalBalance((BigDecimal)total.uniqueResult());
		
		return pagination;
	}

	@Override
	public void applySorts(Pagination<Invoice> pagination, Criteria query, Criteria count) {
		String sort = "id";
		if (pagination.getSortColumn() != null) {

			if (InvoicePagination.SORTS.valueOf(pagination.getSortColumn()) != null)
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
	public void applyFilters(Pagination<Invoice> pagination, Criteria query, Criteria count) {
		if (pagination.getFilters() == null) {
			return;
		}
		
		if (pagination.hasFilter(InvoicePagination.FILTER_KEYS.DUE_DATE_FROM)) {
			String fromDate = pagination.getFilter(InvoicePagination.FILTER_KEYS.DUE_DATE_FROM);
			query.add(Restrictions.ge("dueDate", DateUtilities.getCalendarFromISO8601(fromDate)));
			if (count != null) {
				count.add(Restrictions.ge("dueDate", DateUtilities.getCalendarFromISO8601(fromDate)));
			}
		}

		if (pagination.hasFilter(InvoicePagination.FILTER_KEYS.DUE_DATE_TO)) {
			String toDate = pagination.getFilter(InvoicePagination.FILTER_KEYS.DUE_DATE_TO);
			query.add(Restrictions.le("dueDate", DateUtilities.getCalendarFromISO8601(toDate)));
			if (count != null) {
				count.add(Restrictions.le("dueDate", DateUtilities.getCalendarFromISO8601(toDate)));
			}
		}

		if (pagination.hasFilter(InvoicePagination.FILTER_KEYS.INVOICE_STATUS)) {
			String status = pagination.getFilter(InvoicePagination.FILTER_KEYS.INVOICE_STATUS);
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

		query.add(Restrictions.eq("deleted", false))
			.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		if (count != null) {
			count.add(Restrictions.eq("deleted", false))
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		}
	}
	
	
	@Override
	public List<Integer> findAutoPayInvoices(java.util.Date dueDate, String invoiceStatusType){
		String sql = "SELECT i.id FROM company c, invoice i " +
			" WHERE c.id = i.company_id " +
			" AND i.type IN (:bundle, :statement, :subscription, :adHoc) " +
			" AND i.deleted = 0 " +
			" AND i.due_date <= :dueDate AND c.auto_pay_enabled = true " +
			" AND i.invoice_status_type_code = :invoiceStatusType";
		
		SQLQuery sqlQuery = getFactory().getCurrentSession().createSQLQuery(sql);
		sqlQuery.setTimestamp("dueDate", dueDate);
		sqlQuery.setString("invoiceStatusType", invoiceStatusType);
		sqlQuery.setString("bundle", InvoiceSummary.INVOICE_SUMMARY_TYPE);
		sqlQuery.setString("statement", Statement.STATEMENT_TYPE);
		sqlQuery.setString("subscription", SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE);
		sqlQuery.setString("adHoc", AdHocInvoice.AD_HOC_INVOICE_TYPE);
		return (List<Integer>) sqlQuery.list();
	}

	@Override
	public InvoiceSummary findInvoiceSummaryByInvoiceBundledId(long childInvoiceId) {
		Query query = getFactory().getCurrentSession().createQuery("SELECT invoice FROM invoiceSummary invoice INNER JOIN invoice.invoices child WHERE child.id = :childInvoiceId")
				.setParameter("childInvoiceId", childInvoiceId);
		return (InvoiceSummary)query.uniqueResult();
	}

	@Override
	public List<Long> findAllNonFastFundedAndDueInvoiceIdsToUser(Calendar dueDateFrom, long userId) {
		String query = "SELECT invoice.id " +
			" FROM invoice " +
			" INNER JOIN work_resource wr ON invoice.active_work_resource_id = wr.id " +
			" INNER JOIN company buyerCompany ON invoice.company_id = buyerCompany.id " +
			" WHERE invoice.invoice_status_type_code = :pending AND " +
			" invoice.deleted = 0 AND " +
			" invoice.balance > 0 AND " +
			" invoice.due_date >= :dueDateFrom AND " +
			" invoice.fast_funded_on IS NULL AND " +
			" buyerCompany.company_status_type_code = :active AND " +
			" (wr.user_id = :userId OR wr.dispatcher_id = :userId)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pending", InvoiceStatusType.PAYMENT_PENDING);
		params.addValue("dueDateFrom", dueDateFrom);
		params.addValue("active", CompanyStatusType.ACTIVE);
		params.addValue("userId", userId);

		return readOnlyJdbcTemplate.queryForList(query, params, Long.class);
	}

	@Override
	public Calendar findFastFundedOnDateForWorkResource(long activeWorkResourceId) {
		String query = "SELECT fast_funded_on " +
			"FROM invoice " +
			"WHERE active_work_resource_id = :activeWorkResourceId AND fast_funded_on IS NOT NULL LIMIT 1";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("activeWorkResourceId", activeWorkResourceId);

		List<Calendar> fastFundedOnCollection = readOnlyJdbcTemplate.query(query, params, new org.springframework.jdbc.core.RowMapper<Calendar>() {
			@Override
			public Calendar mapRow(ResultSet rs, int rowNum) throws SQLException {
				return DateUtilities.getCalendarFromDate(rs.getTimestamp("fast_funded_on"));
			}
		});

		if (CollectionUtils.isNotEmpty(fastFundedOnCollection)) {
			return fastFundedOnCollection.get(0);
		}

		return null;
	}

	@Override
	public boolean isCreditMemoIssuable(long invoiceId) {

		// Count non-creditmemoissuable invoice line items
		String query = "SELECT count(*) from invoice i INNER JOIN invoice_line_item ili ON ili.invoice_id = i.id " +
			"and ili.type not in (:types) WHERE i.id=:id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", invoiceId);
		params.addValue("types", ImmutableList.of(InvoiceLineItem.MISC_FEE,
			InvoiceLineItem.SUBSCRIPTION_SOFTWARE_FEE_INVOICE_LINE_ITEM,
			InvoiceLineItem.SUBSCRIPTION_VOR_SOFTWARE_FEE_INVOICE_LINE_ITEM,
			InvoiceLineItem.SUBSCRIPTION_ADD_ON_INVOICE_LINE_ITEM,
			InvoiceLineItem.SUBSCRIPTION_VOR_INVOICE_LINE_ITEM,
			InvoiceLineItem.SUBSCRIPTION_SETUP_FEE_INVOICE_LINE_ITEM,
			InvoiceLineItem.SUBSCRIPTION_DISCOUNT_INVOICE_LINE_ITEM));

		int count = readOnlyJdbcTemplate.queryForObject(query, params, Integer.class);

		// Return true if invoice has only credit-memo-issuable invoice line items
		return count == 0;
	}

}
