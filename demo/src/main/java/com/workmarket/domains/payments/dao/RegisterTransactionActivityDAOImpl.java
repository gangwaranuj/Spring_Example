package com.workmarket.domains.payments.dao;

import com.workmarket.dao.report.sql.SQLBuilderFactory;
import com.workmarket.domains.reports.dao.WorkReportDecoratorDAO;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.domains.model.account.AccountTransactionReportRow;
import com.workmarket.domains.model.account.AccountTransactionReportRowPagination;
import com.workmarket.domains.model.account.RegisterTransactionActivity;
import com.workmarket.domains.model.account.RegisterTransactionActivityPagination;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.WeeklyReportRowPagination;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

@Repository
public class RegisterTransactionActivityDAOImpl implements RegisterTransactionActivityDAO {

	private static final Log logger = LogFactory.getLog(RegisterTransactionActivityDAOImpl.class);

	@Resource(name = "readOnlyJdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired private WorkReportDecoratorDAO workReportDecoratorDAO;
	@Autowired private SQLBuilderFactory sqlBuilderFactory;


	private static final class RegisterTransactionActivityMapper implements RowMapper<RegisterTransactionActivity> {

		@Override
		public RegisterTransactionActivity mapRow(ResultSet rs, int rowNum) throws SQLException {
			RegisterTransactionActivity row = new RegisterTransactionActivity();

			row.setRegisterTransactionId(rs.getLong("transactionId"));
			row.setRegisterTransactionDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("effective_date")));
			row.setAmount(rs.getBigDecimal("amount"));
			row.setRegisterTransactionTypeCode(rs.getString("transactionType"));
			row.setRegisterTransactionTypeDescription(rs.getString("transactionDescription"));
			row.setShowAssignmentTitle(rs.getBoolean("show_assignment_title"));
			row.setWorkId(rs.getLong("workId"));
			row.setWorkTitle(rs.getString("title"));
			row.setWorkNumber(rs.getString("work_number"));
			row.setOwner(rs.getBoolean("isOwner"));
			if (row.isOwner()) {
				row.setInvoiceId(rs.getLong("invoiceId"));
				row.setInvoiceNumber(rs.getString("invoice_number"));
				row.setInvoiceSummaryNumber(rs.getString("invoiceSummaryNumber"));
			}
			row.setWorkResourceId(rs.getLong("workResourceId"));
			row.setWorkResourceUserNumber(rs.getString("user_number"));
			row.setWorkResourceFirstName(rs.getString("first_name"));
			row.setWorkResourceLastName(rs.getString("last_name"));
			row.setDisplayTypeCode(rs.getString("displayCode"));
			row.setDisplayTypeDescription(rs.getString("displayDescription"));
			row.setAvailableCash(rs.getBigDecimal("available_cash"));
			row.setGeneralCash(rs.getBigDecimal("general_cash"));
			row.setProjectCash(rs.getBigDecimal("project_cash"));
			row.setActualCash(rs.getBigDecimal("actual_cash"));
			row.setOwnerCompanyName(rs.getString("companyName"));
			row.setClientName(rs.getString("clientName"));

			row.setScheduleDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("schedule_from")));
			row.setPaidOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("paid_on")));
			row.setClosedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("closed_on")));

			row.setPaidInvoiceSummaryId(rs.getLong("paidInvoiceSummaryId"));
			row.setPaidInvoiceSummaryNumber(rs.getString("paidInvoiceSummaryNumber"));
			row.setPaidInvoiceSummaryType(rs.getString("paidInvoiceSummaryType"));
			row.setPaidInvoiceSummaryDescription(rs.getString("paidInvoiceSummaryDescription"));
			row.setBulkInvoicePayment(rs.getBoolean("bulkInvoicePayment"));
			row.setStatementPeriodStartDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("statement_period_start_date")));
			row.setStatementPeriodEndDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("statement_period_end_date")));
			row.setBankAccountName(rs.getString("bank_name"));
			row.setBankAccountNumber(rs.getString("account_number"));
			row.setBankAccountTransactionStatus(rs.getString("bankAccountTransactionStatus"));
			row.setCreditCardTransaction(rs.getBoolean("creditCardTransaction"));
			row.setBankAccountTransaction(rs.getBoolean("bankAccountTransaction"));
			row.setPendingTransaction(rs.getString("pendingFlag"));
			return row;
		}
	}

	private static final class AccountTransactionReportRowMapper implements RowMapper<AccountTransactionReportRow> {

		@Override
		public AccountTransactionReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			AccountTransactionReportRow row = new AccountTransactionReportRow();

			row.setCompanyId(rs.getLong("companyId"));
			row.setCompanyName(rs.getString("companyName"));
			row.setCompanyNumber(rs.getString("company_number"));
			row.setAmount(rs.getBigDecimal("amount"));
			return row;
		}
	}

	@Override
	public RegisterTransactionActivityPagination getLedgerForCompany(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination) {
		SQLBuilder builder = buildLedgerSql(companyId, accountRegisterId, pagination);
		return findDisplayableTransactions(builder, companyId, pagination, false);
	}

	@Override
	public RegisterTransactionActivityPagination getOfflineLedgerForCompany(Long companyId, Long accountRegisterId,
		RegisterTransactionActivityPagination pagination) {
		SQLBuilder builder = buildOfflineLedgerSql(companyId, accountRegisterId, pagination);
		return findDisplayableTransactions(builder, companyId, pagination, false);
	}

	@Override
	public RegisterTransactionActivityPagination getPendingTransactions(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination) {
		SQLBuilder builder = newRegisterTransactionActivitySQLBuilder(companyId, accountRegisterId, pagination);
		builder.addWhereClause("(rt.pending_flag = 'Y' OR  bankT.bank_account_transaction_status_code = 'submitted') ")
				.addWhereClause("(rtType.display_on_reports = true OR rtType.code = :commitment )")
				// Excluding rejected bank account transactions.
				.addWhereClause(" IFNULL(bankT.bank_account_transaction_status_code, 'empty') != 'rejected'")
				.addParam("commitment", RegisterTransactionType.BUYER_COMMITMENT_TO_PAY);
		return findDisplayableTransactions(builder, companyId, pagination, true);
	}

	@Override
	public RegisterTransactionActivityPagination getAccountRegisterTransactionReport(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination) {
		SQLBuilder builder = newRegisterTransactionActivitySQLBuilder(companyId, accountRegisterId, pagination);
		builder.addJoin("LEFT OUTER JOIN project_work_association project ON project.work_id = work.id", pagination.hasProjectFilter())
				.addJoin("LEFT OUTER JOIN work_sub_status_type_association sub_status " +
						" ON (sub_status.work_id = work.id  AND sub_status.deleted = FALSE AND sub_status.resolved = FALSE )", pagination.hasSubStatusFilter())
				.addJoin("LEFT OUTER JOIN work_sub_status_type ON work_sub_status_type.id = sub_status.work_sub_status_type_id " +
						"  AND work_sub_status_type.deleted = FALSE", pagination.hasSubStatusFilter());

		builder.addWhereClause(" ( (rtType.display_on_reports = true AND rt.pending_flag = 'N' AND rtType.code != :invoicePay) OR  ( rtType.code  =  'commitment' and rt.pending_flag  =  'Y' ) ) ")
				.addParam("invoicePay", RegisterTransactionType.INVOICE_PAYMENT)
				.addParam("authorization", RegisterTransactionType.AUTHORIZATION_TRANSACTION_TYPE);

		return findDisplayableTransactions(builder, companyId, pagination, true);
	}

	private RegisterTransactionActivityPagination findDisplayableTransactions(SQLBuilder builder, Long companyId, RegisterTransactionActivityPagination pagination, boolean includeCustomFields) {
		Assert.notNull(pagination);

		String sql = builder.build();

		// Clearing the order clause and limit for the count query
		builder.getOrderColumns().clear();
		builder.setStartRow(null);
		builder.setPageSize(null);

		Integer rowCount = jdbcTemplate.queryForObject(builder.buildCount("DISTINCT rt.id"), builder.getParams(), Integer.class);

		if (rowCount > 0) {
			List<RegisterTransactionActivity> results = jdbcTemplate.query(sql, builder.getParams(), new RegisterTransactionActivityMapper());
			if (includeCustomFields) {
				results = workReportDecoratorDAO.addCustomFields(null, companyId, results, new CustomFieldReportFilters());
			}
			pagination.setResults(results);
		}

		pagination.setRowCount(rowCount);
		return pagination;
	}

	private SQLBuilder buildLedgerSql(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination) {
		SQLBuilder builder = newRegisterTransactionActivitySQLBuilder(companyId, accountRegisterId, pagination);
		// WORK-7743: only show higher level payment when paying an invoice bundle or a statement
		/**
		 *  We remove commitments on the front end, better not query for them
		 *  For authorizations just do  ( rtType.code  =  'commitment' and rt.pending_flag  =  'Y' )
		 */

		builder.addWhereClause("(rt.pending_flag = 'N' AND rtType.display_on_reports = true )")
				// Excluding rejected bank account transactions.
				.addWhereClause(" IFNULL(bankT.bank_account_transaction_status_code, 'empty') NOT IN ('rejected', 'submitted')")
				.addWhereClause(" COALESCE(wresourceT.bundle_payment, false) = false ");
		return builder;
	}

	private SQLBuilder buildOfflineLedgerSql(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination) {
		SQLBuilder builder = newRegisterTransactionActivitySQLBuilder(companyId, accountRegisterId, pagination);
		builder.addWhereClause(String.format("rt.register_transaction_type_code in ('%s', '%s')",
			RegisterTransactionType.BUYER_OFFLINE_WORK_PAYMENT, RegisterTransactionType.RESOURCE_OFFLINE_WORK_PAYMENT));
		return builder;
	}

	private SQLBuilder newRegisterTransactionActivitySQLBuilder(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination) {
		SQLBuilder builder = new SQLBuilder();

		builder.addColumns("DISTINCT rt.id AS transactionId", "rt.effective_date", "rt.available_cash", "rt.general_cash", "rt.project_cash", "rt.actual_cash", "rt.pending_flag AS pendingFlag",
				/**
				 * Showing the buyer_total_cost in case of payments since we want to show only one row per assignment (remove fees)
				 */
				"IF (rt.register_transaction_type_code = 'payment' AND work.id IS NOT NULL, IF(work.buyer_total_cost < 0,work.buyer_total_cost,work.buyer_total_cost* -1), rt.amount) AS amount",
				"rtType.code AS transactionType", "rtType.description AS transactionDescription",
				"rtType.show_assignment_title",
				"rtType.transaction_type_code AS displayCode", "rtType.transaction_display_type_description AS displayDescription",
				"work.id AS workId", "work.title", "work.work_number",
				"invoice.id AS invoiceId", "invoice.invoice_number",
				"wr.id AS workResourceId", "user.user_number", "user.first_name", "user.last_name",
				"company.name AS companyName",
				"client.name AS clientName",
				"IF(work.company_id = :companyId, 1, 0) AS isOwner",
				"work.schedule_from", "work.closed_on", "milestones.paid_on",
				//Since an invoice can be part of a invoice collection too, we need to make sure we only get the bundle information to avoid duplicate rows in the ledger
				"IF(invoice.id IS NOT NULL, (SELECT bundle.invoice_number FROM invoice INNER JOIN invoice_summary_detail ON invoice_summary_detail.invoice_id = invoice.id \n" +
						"INNER JOIN invoice bundle ON bundle.id = invoice_summary_detail.invoice_summary_id AND bundle.type = 'bundle' \n" +
						"WHERE invoice.id = work.invoice_id), NULL) AS invoiceSummaryNumber",

				"paidInvoice.id AS paidInvoiceSummaryId",
				"paidInvoice.invoice_number AS paidInvoiceSummaryNumber",
				"paidInvoice.description AS paidInvoiceSummaryDescription",
				"paidInvoice.type AS paidInvoiceSummaryType",
				"COALESCE(wresourceT.bundle_payment, false) AS bulkInvoicePayment",
				"paidInvoice.statement_period_start_date", "paidInvoice.statement_period_end_date",
				"bank.bank_name", "bank.account_number",
				"IFNULL(bankT.bank_account_transaction_status_code, 'empty') as bankAccountTransactionStatus",
				"IF(creditCardTransaction.id IS NOT NULL, 1, 0) AS creditCardTransaction",
				"IF(bankAccountTransaction.id IS NOT NULL, 1, 0) AS bankAccountTransaction"
		)

		.addParam("companyId", companyId)

		.addTable("register_transaction rt")

		.addJoin(" INNER JOIN register_transaction_type rtType ON rt.register_transaction_type_code = rtType.code ")
				.addJoin(" LEFT OUTER JOIN bank_account_transaction bankT ON rt.id = bankT.id ")
				.addJoin(" LEFT OUTER JOIN bank_account bank ON bank.id = bankT.bank_account_id ")
				.addJoin(" LEFT OUTER JOIN invoice_payment_transaction ON rt.id = invoice_payment_transaction.id ", false)
				.addJoin(" LEFT OUTER JOIN invoice paidInvoice ON paidInvoice.id = invoice_payment_transaction.invoice_id AND paidInvoice.deleted = 0", false)
				.addJoin(" LEFT OUTER JOIN work_resource_transaction wresourceT ON rt.id = wresourceT.id ")
				.addJoin(" LEFT OUTER JOIN work ON work.id = rt.work_id ")
				.addJoin(" LEFT OUTER JOIN company ON work.company_id = company.id ", false)
				.addJoin(" LEFT OUTER JOIN invoice ON invoice.id = work.invoice_id AND invoice.deleted = 0", false)
				.addJoin(" LEFT OUTER JOIN work_milestones milestones ON work.id = milestones.work_id", pagination.hasAssignmentPaidDateFilter())
				.addJoin(" LEFT OUTER JOIN work_resource wr	ON (wr.id = wresourceT.work_resource_id AND wr.work_id = work.id)", false)
				.addJoin(" LEFT OUTER JOIN user ON user.id = wr.user_id ", false)
				.addJoin(" LEFT OUTER JOIN client_company client ON work.client_company_id = client.id ", false)
				.addJoin(" LEFT OUTER JOIN credit_card_transaction creditCardTransaction ON (rt.id = creditCardTransaction.id)", false)
				.addJoin(" LEFT OUTER JOIN bank_account_transaction bankAccountTransaction ON (rt.id = bankAccountTransaction.id)", false)

				.addWhereClause(" rt.account_register_id = :accountRegisterId")
				.addParam("accountRegisterId", accountRegisterId);

		// sorts
		if (pagination.getSortColumn() != null) {
			builder.addOrderBy(RegisterTransactionActivityPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else {
			builder.addOrderBy(RegisterTransactionActivityPagination.SORTS.TRANSACTION_DATE.getColumn(), RegisterTransactionActivityPagination.SORT_DIRECTION.ASC.toString());
		}
		builder.addOrderBy("rt.actual_cash", "ASC");
		builder.addLimitClause(pagination.getStartRow(), pagination.getResultsLimit(), pagination.isLimitMaxRows());

		// filters
		if (pagination.getFilters() != null) {

			// Transaction date
			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_FROM)) {
				Calendar transactionDateFrom = DateUtilities.getCalendarFromISO8601(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_FROM));
				builder.addWhereClause("rt.effective_date >= :transactionDateFrom ").addParam("transactionDateFrom", transactionDateFrom);
			}

			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_TO)) {
				Calendar transactionDateTo = DateUtilities.getCalendarFromISO8601(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_TO));
				builder.addWhereClause("rt.effective_date <= :transactionDateTo ").addParam("transactionDateTo", transactionDateTo);
			}

			// Assignment schedule date
			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_SCHEDULED_DATE_FROM)) {
				Calendar scheduleDateFrom = DateUtilities.getCalendarFromISO8601(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_SCHEDULED_DATE_FROM));
				builder.addWhereClause("work.schedule_from >= :scheduleDateFrom ").addParam("scheduleDateFrom", scheduleDateFrom);
			}

			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_SCHEDULED_DATE_TO)) {
				Calendar scheduleDateTo = DateUtilities.getCalendarFromISO8601(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_SCHEDULED_DATE_TO));
				scheduleDateTo = DateUtilities.getMidnight(scheduleDateTo);
				builder.addWhereClause("work.schedule_from <= :scheduleDateTo ").addParam("scheduleDateTo", scheduleDateTo);
			}

			// Assignment approved date
			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_FROM)) {
				Calendar approvedDateFrom = DateUtilities.getCalendarFromISO8601(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_FROM));
				builder.addWhereClause("work.closed_on >= :approvedDateFrom ").addParam("approvedDateFrom", approvedDateFrom);
			}

			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_TO)) {
				Calendar approvedDateTo = DateUtilities.getCalendarFromISO8601(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_TO));
				approvedDateTo = DateUtilities.getMidnight(approvedDateTo);
				builder.addWhereClause("work.closed_on <= :approvedDateTo ").addParam("approvedDateTo", approvedDateTo);
			}

			// Assignment paid date
			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_FROM)) {
				Calendar paidDateFrom = DateUtilities.getCalendarFromISO8601(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_FROM));
				builder.addWhereClause("milestones.paid_on >= :paidDateFrom ").addParam("paidDateFrom", paidDateFrom);
			}

			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_TO)) {
				Calendar paidDateTo = DateUtilities.getCalendarFromISO8601(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_TO));
				paidDateTo = DateUtilities.getMidnight(paidDateTo);
				builder.addWhereClause("milestones.paid_on <= :paidDateTo ").addParam("paidDateTo", paidDateTo);
			}

			// Buyer id
			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.BUYER_ID)) {
				Long value = Long.parseLong(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.BUYER_ID));
				builder.addWhereClause("work.buyer_user_id = :buyerId")
						.addParam("buyerId", value);
			}
			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.WORK_STATUS_TYPE_CODE)) {
				String value = pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.WORK_STATUS_TYPE_CODE);
				builder.addWhereClause("work.work_status_type_code = :workStatusTypeCode")
						.addParam("workStatusTypeCode", value);
			}
			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.CLIENT_COMPANY_ID)) {
				Long value = Long.parseLong(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.CLIENT_COMPANY_ID));
				builder.addWhereClause("work.client_company_id = :clientCompanyId")
						.addParam("clientCompanyId", value);
			}
			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.PROJECT_ID)) {
				Long value = Long.parseLong(pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.PROJECT_ID));
				builder.addWhereClause("project.project_id = :projectId")
						.addParam("projectId", value);
			}
			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.SUBSTATUS_TYPE_CODE)) {
				String value = pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.SUBSTATUS_TYPE_CODE);
				builder.addWhereClause("work_sub_status_type.code = :workSubStatusTypeCode")
						.addParam("workSubStatusTypeCode", value);
			}
			if (pagination.hasFilter(RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_TYPE)) {
				String value = pagination.getFilter(RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_TYPE);
				builder.addWhereClause("rtType.transaction_type_code = :transactionTypeCode")
						.addParam("transactionTypeCode", value);
			}
		}

		return builder;
	}

	@Override
	public AccountTransactionReportRowPagination findFundingTransactionsByTransactionDate(Calendar fromDate, Calendar toDate, AccountTransactionReportRowPagination pagination) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns(
				"c.id AS companyId", "c.effective_name AS companyName",
				"c.company_number", "SUM(rt.amount) AS amount")

				.addTable("register_transaction rt")
				.addJoin("INNER JOIN account_register ON rt.account_register_id = account_register.id")
				.addJoin("INNER JOIN company c ON account_register.company_id = c.id")
				.addJoin("LEFT OUTER JOIN bank_account_transaction bankT ON rt.id = bankT.id")

				.addWhereClause("rt.register_transaction_type_code IN ('addfunds', 'directDep', 'checkDep')")
				.addWhereClause("rt.pending_flag = 'N'")
				.addWhereClause("rt.effective_date BETWEEN :fromDate AND :toDate")
				.addWhereClause("IFNULL(bankT.bank_account_transaction_status_code, 'empty') != 'rejected'")

				.addGroupColumns("account_register.id")
				.addParam("toDate", toDate)
				.addParam("fromDate", fromDate);

		if (pagination.getSortColumn() != null) {
			builder.addOrderBy(WeeklyReportRowPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else {
			builder.addDescOrderBy("amount")
					.addDescOrderBy("c.effective_name");
		}

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());
		logger.debug(builder.build());

		pagination.setResults(jdbcTemplate.query(builder.build(), builder.getParams(), new AccountTransactionReportRowMapper()));
		pagination.setRowCount(jdbcTemplate.queryForObject(builder.buildCount(), builder.getParams(), Integer.class));
		return pagination;
	}

	@Override
	public BigDecimal getBalanceFromLedger(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination) {
		SQLBuilder builder = buildLedgerSql(companyId, accountRegisterId, pagination);
		builder.getOrderColumns().clear();
		String sql = "SELECT sum(amount) FROM (" + builder.build() + ") AS displayableTransactions";
		BigDecimal balance = jdbcTemplate.queryForObject(sql, builder.getParams(), BigDecimal.class);
		if (balance != null) {
			return balance;
		}
		return BigDecimal.ZERO;
	}
}
