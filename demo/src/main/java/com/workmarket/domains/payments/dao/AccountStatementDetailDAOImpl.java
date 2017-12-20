package com.workmarket.domains.payments.dao;

import com.google.common.collect.Maps;
import com.workmarket.dao.report.sql.SQLBuilderFactory;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.domains.reports.dao.WorkReportDecoratorDAO;
import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.model.account.AccountTransactionReportRow;
import com.workmarket.domains.model.account.AccountTransactionReportRowPagination;
import com.workmarket.domains.model.account.WeeklyReportRowPagination;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Repository
public class AccountStatementDetailDAOImpl implements AccountStatementDetailDAO {
	private static final Log logger = LogFactory.getLog(AccountStatementDetailDAOImpl.class);

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired private SQLBuilderFactory sqlBuilderFactory;
	@Autowired private WorkReportDecoratorDAO workReportDecoratorDAO;

	@Override
	public AccountStatementDetailPagination findInvoices(Long userId, Long companyId, AccountStatementDetailPagination pagination, AccountStatementFilters accountStatementFilters, boolean isAdminOrController) {
		accountStatementFilters.setBundledInvoices(false);
		SQLBuilder builder = sqlBuilderFactory.buildAccountStatementSQL(userId, companyId, isAdminOrController, pagination, accountStatementFilters);
		return getAccountStatementDetailDashboard(userId, builder, pagination);
	}

	@Override
	public AccountStatementDetailPagination findInvoicesWithBundledInvoices(Long userId, Long companyId, AccountStatementDetailPagination pagination, AccountStatementFilters filters, boolean isAdminOrController) {
		pagination = findInvoices(userId, companyId, pagination, filters, isAdminOrController);

		Map<Long, AccountStatementDetailRow> bundleLookup = Maps.newHashMap();
		for (AccountStatementDetailRow row : pagination.getResults()) {
			if (row.isBundle()) {
				bundleLookup.put(row.getInvoiceId(), row);
			}
		}
		
		if (MapUtils.isNotEmpty(bundleLookup)){
			AccountStatementDetailPagination bundledPagination = new AccountStatementDetailPagination(true);
			filters.setInvoiceSummaryIds(bundleLookup.keySet());
			filters.resetDateFilters();
			filters.setInvoiceType(StringUtils.EMPTY);
			bundledPagination = findBundledInvoices(userId, companyId, bundledPagination, filters, isAdminOrController);
	
			for (AccountStatementDetailRow row : bundledPagination.getResults()) {
				bundleLookup.get(row.getInvoiceSummaryId()).getBundledInvoices().add(row);
			}
		}
		
		return pagination;
	}

	@Override
	public AccountStatementDetailPagination findBundledInvoices(Long userId, Long companyId, AccountStatementDetailPagination pagination, AccountStatementFilters accountStatementFilters, boolean isAdminOrController) {
		accountStatementFilters.setBundledInvoices(true);
		SQLBuilder builder = sqlBuilderFactory.buildAccountStatementSQL(userId, companyId, isAdminOrController, pagination, accountStatementFilters);
		return getAccountStatementDetailDashboard(userId, builder, pagination);
	}

	@Override
	public BigDecimal sumTotalPastDue(Long userId, Long companyId, boolean isAdminOrController, boolean isPayables) {
		return sumTotalInvoices(userId, companyId, isAdminOrController, isPayables, true, null, false);
	}

	@Override
	public BigDecimal sumTotalUpcomingDue(Long userId, Long companyId, boolean isAdminOrController, boolean isPayables) {
		return sumTotalInvoices(userId, companyId, isAdminOrController, isPayables, false, null, false);
	}

	@Override
	public BigDecimal sumTotalUpcomingDueIn24Hours(Long userId, Long companyId, boolean isAdminOrController, boolean isPaybles) {
		DateFilter dataFilter = new DateFilter();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		Calendar dayAfterTomorrow = DateUtilities.cloneCalendar(tomorrow);
		dayAfterTomorrow.add(Calendar.DAY_OF_MONTH, 1);
		dataFilter.setFromDate(tomorrow);
		dataFilter.setToDate(dayAfterTomorrow);
		return sumTotalInvoices(userId, companyId, isAdminOrController, isPaybles, false, dataFilter, false);
	}

	@Override
	public BigDecimal sumTotalPaid(Long userId, Long companyId, boolean isAdminOrController, boolean isPayables, DateFilter dateFilter) {
		return sumTotalInvoices(userId, companyId, isAdminOrController, isPayables, false, dateFilter, true);
	}

	private BigDecimal sumTotalInvoices(Long userId, Long companyId, boolean isAdminOrController, boolean isPayables, boolean isPastDue, DateFilter dateFilter, boolean isPaid) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("invoice")
				.addJoin("INNER JOIN company ON invoice.company_id = company.id")
				.addJoin("LEFT JOIN work ON invoice.id = work.invoice_id")
				.addJoin("LEFT JOIN work_resource ON invoice.active_work_resource_id = work_resource.id")
				.addJoin("LEFT JOIN user AS assignedUser ON work_resource.user_id = assignedUser.id")
				.addWhereClause("invoice.deleted = 0")
				.addWhereClause("invoice.invoice_status_type_code = :invoiceStatusType")
				.addWhereClause("invoice.invoice_status_type_code <> :offlineInvoiceStatusType")
				.addParam("companyId", companyId)
				.addParam("userId", userId)
				.addParam("today", DateUtilities.formatTodayForSQL())
				.addParam("bundle", InvoiceSummary.INVOICE_SUMMARY_TYPE)
				.addParam("adHoc", AdHocInvoice.AD_HOC_INVOICE_TYPE)
				.addParam("subscription", SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE)
				.addParam("invoice", Invoice.INVOICE_TYPE);

		String invoiceStatusType = InvoiceStatusType.PAYMENT_PENDING;
		if (isPaid) {
			invoiceStatusType = InvoiceStatusType.PAID;
		}
		builder.addParam("invoiceStatusType", invoiceStatusType);
		builder.addParam("offlineInvoiceStatusType", InvoiceStatusType.PAID_OFFLINE);

		if (isPaid && dateFilter != null && dateFilter.isSetFromDate() && dateFilter.isSetToDate()) {
			builder.addWhereClause("invoice.payment_date BETWEEN :fromDate AND :toDate")
					.addParam("fromDate", dateFilter.getFromDate())
					.addParam("toDate", dateFilter.getToDate());
		} else if (!isPaid && dateFilter != null && dateFilter.isSetFromDate() && dateFilter.isSetToDate()) {
			builder.addWhereClause("invoice.due_date BETWEEN :fromDate AND :toDate")
					.addParam("fromDate", dateFilter.getFromDate())
					.addParam("toDate", dateFilter.getToDate());
		} else if (isPastDue) {
			builder.addWhereClause("invoice.due_date <= :today");
		} else {
			builder.addWhereClause("invoice.due_date >= :today");
		}

		if (isPayables) {
			builder.addColumn("COALESCE(SUM(balance),0) AS balance")
				.addWhereClause("invoice.bundled = false")
				.addWhereClause("invoice.type IN (:bundle, :adHoc, :subscription, :invoice)");
			if (isAdminOrController) {
				builder.addWhereClause("invoice.company_id = :companyId");
			} else {
				builder.addWhereClause("work.buyer_user_id = :userId");
			}
		} else {
			builder.addColumn("COALESCE(SUM(amount_earned),0) AS balance")
					.addWhereClause("invoice.type = :invoice");
			if (isAdminOrController) {
				builder.addWhereClause("assignedUser.company_id = :companyId");
			} else {
				builder.addWhereClause("(assignedUser.id = :userId OR work_resource.dispatcher_id = :userId)");
			}
		}
		return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), BigDecimal.class);
	}

	@Override
	public AccountTransactionReportRowPagination getPaymentPendingInvoicesRunningTotalsByCompany(Calendar fromDueDate, Calendar toDueDate, AccountTransactionReportRowPagination pagination) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns(
				"c.id AS companyId", "c.effective_name AS companyName",
				"c.company_number", "SUM(invoice.balance) AS amount")

				.addTable("invoice")
				.addJoin("INNER JOIN company c ON invoice.company_id = c.id")
				.addWhereClause("invoice.type = 'invoice'")
				.addWhereClause("invoice.invoice_status_type_code = 'pending'")
				.addWhereClause("invoice.deleted = false")
				.addWhereClause("invoice.due_date BETWEEN :fromDueDate AND :toDueDate")
				.addGroupColumns("c.id")
				.addParam("fromDueDate", fromDueDate)
				.addParam("toDueDate", toDueDate);

		if (pagination.getSortColumn() != null) {
			builder.addOrderBy(WeeklyReportRowPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else {
			builder.addDescOrderBy("amount")
					.addDescOrderBy("c.effective_name");
		}

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());
		logger.debug(builder.build());

		pagination.setResults(jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<AccountTransactionReportRow>() {
			@Override
			public AccountTransactionReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
				AccountTransactionReportRow row = new AccountTransactionReportRow();

				row.setCompanyId(rs.getLong("companyId"));
				row.setCompanyName(rs.getString("companyName"));
				row.setCompanyNumber(rs.getString("company_number"));
				row.setAmount(rs.getBigDecimal("amount"));
				return row;
			}
		}));

		pagination.setRowCount(jdbcTemplate.queryForObject(builder.buildCount(), builder.getParams(), Integer.class));
		return pagination;
	}

	private static final class AccountStatementDetailRowMapper implements RowMapper<AccountStatementDetailRow> {

		@Override
		public AccountStatementDetailRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			AccountStatementDetailRow row = new AccountStatementDetailRow();

			row.setWorkId(rs.getLong("workId"));
			row.setWorkNumber(rs.getString("workNumber"));
			row.setWorkStatusTypeCode(rs.getString("work_status_type_code"));
			row.setWorkTitle(rs.getString("workTitle"));
			row.setWorkCloseDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("closeDate")));
			row.setWorkDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("workDate")));

			row.setWorkCity(rs.getString("workCity"));
			row.setWorkState(rs.getString("workState"));
			row.setWorkPostalCode(rs.getString("workPostalCode"));
			row.setWorkCountry(rs.getString("workCountry"));

			row.setWorkResourceName(StringUtilities.fullName(rs.getString("resourceFirstName"), rs.getString("resourceLastName")));
			row.setWorkResourceCompanyName(rs.getString("resourceCompanyName"));

			row.setClientCompanyName(rs.getString("clientCompanyName"));

			row.setBuyerFullName(StringUtilities.fullName(rs.getString("buyerFirstName"), rs.getString("buyerLastName")));
			row.setBuyerId(rs.getLong("buyerId"));
			row.setBuyerPhone(rs.getString("buyer_work_phone"));
			row.setBuyerPhoneExtension(rs.getString("buyer_work_phone_extension"));

			row.setCompanyId(rs.getLong("company_id"));
			row.setCompanyName(rs.getString("companyName"));

			row.setAmountEarned(rs.getBigDecimal("amount_earned"));
			row.setBuyerTotalCost(rs.getBigDecimal("buyer_total_cost"));

			row.setInvoiceId(rs.getLong("invoiceId"));
			row.setInvoiceNumber(rs.getString("invoice_number"));
			row.setInvoiceDescription(rs.getString("invoiceDescription"));
			row.setInvoiceStatusTypeCode(rs.getString("invoiceStatusTypeCode"));
			row.setInvoiceType(rs.getString("invoiceType"));
			row.setInvoiceBalance(rs.getBigDecimal("balance"));
			row.setInvoiceDueDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("invoiceDueDate")));
			row.setInvoiceVoidDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("invoiceVoidDate")));
			row.setInvoiceCreatedDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("invoiceCreatedDate")));
			row.setInvoicePaymentDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("invoicePaymentDate")));
			row.setInvoiceSummaryId(rs.getLong("invoiceSummaryId"));
			row.setInvoiceSummaryNumber(rs.getString("invoiceSummaryNumber"));
			row.setInvoiceSummaryDescription(rs.getString("invoiceSummaryDescription"));
			row.setInvoiceSummaryDueDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("invoiceSummaryDueDate")));
			row.setInvoiceIsBundled(rs.getBoolean("invoiceIsBundled"));

			row.setPaymentTermsDays(rs.getInt("work.payment_terms_days"));
			row.setPaymentTermsEnabled(rs.getBoolean("work.payment_terms_enabled"));
			row.setTimeZoneId(rs.getString("timeZoneId"));
			row.setOwner(rs.getBoolean("isOwner"));

			row.setInvoiceFulfillmentStatus(rs.getString("invoiceFulfillmentStatus"));
			row.setInvoiceRemainingBalance(rs.getBigDecimal("invoiceRemainingBalance"));
			row.setPendingPaymentFulfillment(rs.getBoolean("isPendingFulfillment"));
			row.setNumberOfInvoices(rs.getInt("numberOfInvoices"));
			row.setEditable(rs.getBoolean("editable"));
			row.setDownloadedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("invoiceDownloadedDate")));

			row.setUniqueIdDisplayName(rs.getString("uniqueIdDisplayName"));
			row.setUniqueIdValue(rs.getString("uniqueIdValue"));
			return row;
		}
	}

	private AccountStatementDetailPagination getAccountStatementDetailDashboard(Long userId, SQLBuilder builder, AccountStatementDetailPagination pagination) {
		Assert.notNull(pagination);
		logger.debug(builder.build());
		logger.debug(builder.buildCount());
		int rowCount = jdbcTemplate.queryForObject(builder.buildCount(), builder.getParams(), Integer.class);
		pagination.setRowCount(rowCount);

		if (rowCount > 0) {
			List<AccountStatementDetailRow> rows = jdbcTemplate.query(builder.build(), builder.getParams(), new AccountStatementDetailRowMapper());

			CustomFieldReportFilters filters = new CustomFieldReportFilters();
			filters.setShowOnInvoice(true);

			rows = workReportDecoratorDAO.addCustomFields(userId, rows, filters);

			pagination.setResults(rows);

			// Balance running totals
			// return getAccountStatementDashboardRunningTotals(builder, pagination);
		}
		return pagination;
	}

	@Override
	public AccountStatementDetailRow findAccountStatementDetailByInvoiceId(Long invoiceId, Long userId, Long companyId, boolean isAdminOrController) {
		AccountStatementFilters filters = new AccountStatementFilters();
		filters.setInvoiceId(invoiceId);
		AccountStatementDetailPagination pagination = new AccountStatementDetailPagination();
		SQLBuilder builder = sqlBuilderFactory.buildAccountStatementSQL(userId, companyId, isAdminOrController, pagination, filters);
		getAccountStatementDetailDashboard(userId, builder, pagination);

		CustomFieldReportFilters customFieldReportFilters = new CustomFieldReportFilters();
		customFieldReportFilters.setShowOnInvoice(true);
		
		if (pagination.getRowCount() > 0) {
			AccountStatementDetailRow invoice = pagination.getResults().get(0);
			Assert.notNull(invoice.getInvoiceId());
			if (invoice.getInvoiceType().equals(InvoiceSummary.INVOICE_SUMMARY_TYPE)) {
				AccountStatementFilters bundledInvoicesFilters = new AccountStatementFilters();
				bundledInvoicesFilters.setInvoiceSummaryId(invoiceId);
				SQLBuilder bundledInvoicesSql = sqlBuilderFactory.buildAccountStatementSQL(userId, companyId, isAdminOrController, pagination, bundledInvoicesFilters);
				bundledInvoicesSql.setStartRow(null);
				bundledInvoicesSql.setPageSize(null);
				logger.debug(bundledInvoicesSql.build());
				List<AccountStatementDetailRow> statementDetail = jdbcTemplate.query(bundledInvoicesSql.build(), bundledInvoicesSql.getParams(), new AccountStatementDetailRowMapper());
				statementDetail = workReportDecoratorDAO.addCustomFields(userId, statementDetail, customFieldReportFilters);
				invoice.getBundledInvoices().addAll(statementDetail);
			}
			return invoice;
		}
		return null;
	}
}
