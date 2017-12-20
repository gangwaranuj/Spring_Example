package com.workmarket.dao.account;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.AccountingCreditMemoSummaryDetail;
import com.workmarket.domains.model.account.AccountingEndOfYearTaxSummary;
import com.workmarket.domains.model.account.AccountingSummaryDetail;
import com.workmarket.domains.model.account.FastFundsReceivableSummaryDetail;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.invoice.CreditMemoReasons;
import com.workmarket.service.business.accountregister.CreditMemoType;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

@Repository
public class AccountingSummaryDetailDAOImpl extends AbstractDAO<AccountingSummaryDetail> implements AccountingSummaryDetailDAO {

    @Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

    protected Class<AccountingSummaryDetail> getEntityClass() {
        return AccountingSummaryDetail.class;
    }

    private void putStartAndEndInMap(MapSqlParameterSource params, Calendar start, Calendar end) {
        params.addValue("start", start);
        params.addValue("end", end);
    }

    private static class AccountingSummaryDetailMapper implements RowMapper<AccountingSummaryDetail> {
	    @Override
	    public AccountingSummaryDetail mapRow(ResultSet resultSet, int i) throws SQLException {
	      AccountingSummaryDetail accountingSummaryDetail = new AccountingSummaryDetail();
	      accountingSummaryDetail.setAmount(resultSet.getBigDecimal("amount"));
	      accountingSummaryDetail.setInvoiceType(resultSet.getString("invoiceType"));
	      accountingSummaryDetail.setInvoiceNumber(resultSet.getString("invoice_number"));
	      accountingSummaryDetail.setCompanyName(resultSet.getString("CompanyName"));
	      accountingSummaryDetail.setInvoiceOn(DateUtilities.getCalendarFromDate(resultSet.getTimestamp("invoiceOn")));
	      accountingSummaryDetail.setInvoiceDueDate(DateUtilities.getCalendarFromDate(resultSet.getTimestamp("invoiceDueDate")));
	      accountingSummaryDetail.setPaymentDate(DateUtilities.getCalendarFromDate(resultSet.getTimestamp("payment_date")));
	      return accountingSummaryDetail;
	    }
    }

	private static class FastFundsReceivableSummaryDetailMapper implements RowMapper<FastFundsReceivableSummaryDetail> {
		@Override
		public FastFundsReceivableSummaryDetail mapRow(ResultSet resultSet, int i) throws SQLException {
			FastFundsReceivableSummaryDetail fastFundsReceivableSummaryDetail = new FastFundsReceivableSummaryDetail();
			fastFundsReceivableSummaryDetail.setAmount(resultSet.getBigDecimal("amount"));
			fastFundsReceivableSummaryDetail.setInvoiceNumber(resultSet.getString("invoice_number"));
			fastFundsReceivableSummaryDetail.setWorkNumber(resultSet.getString("workNumber"));
			fastFundsReceivableSummaryDetail.setBuyerCompanyId(resultSet.getLong("buyerCompanyId"));
			fastFundsReceivableSummaryDetail.setCompanyName(resultSet.getString("buyerCompanyName"));
			fastFundsReceivableSummaryDetail.setWorkerCompanyId(resultSet.getLong("workerCompanyId"));
			fastFundsReceivableSummaryDetail.setWorkerCompanyName(resultSet.getString("workerCompanyName"));
			fastFundsReceivableSummaryDetail.setInvoiceDueDate(DateUtilities.getCalendarFromDate(resultSet.getTimestamp("invoiceDueDate")));
			fastFundsReceivableSummaryDetail.setPaymentDate(DateUtilities.getCalendarFromDate(resultSet.getTimestamp("payment_date")));
			fastFundsReceivableSummaryDetail.setFastFundsOnDate(DateUtilities.getCalendarFromDate(resultSet.getTimestamp("fastFundedOn")));

			return fastFundsReceivableSummaryDetail;
		}
	}

	/**
	 * RowMapper for query used to populate credit memo transaction CSV that is created when user clicks on credit
	 * memo total row in JES report
	 */
	private static class AccountingCreditMemoSummaryDetailMapper implements RowMapper<AccountingCreditMemoSummaryDetail> {
		@Override
		public AccountingCreditMemoSummaryDetail mapRow(ResultSet rs, int i) throws SQLException {
			AccountingCreditMemoSummaryDetail accountingSummaryDetail = new AccountingCreditMemoSummaryDetail();
			accountingSummaryDetail.setAmount(rs.getBigDecimal("revenue_amount"))
				.setInvoiceType(rs.getString("cmInvoiceType"))
				.setInvoiceNumber(rs.getString("cmInvoiceNumber"))
				.setReason(rs.getInt("reason") == 0 ? null : CreditMemoReasons.CREDIT_MEMO_REASONS_MAP.get(rs.getInt("reason")).getLabel())
				.setNote(rs.getString("note"))
				.setSubscriptionInvoiceTypeCode(rs.getString("cmSubscriptionInvoiceTypeCode"))
				.setEffectiveName(rs.getString("companyName"))
				.setRegisterTransactionTypeCode(rs.getString("cmRegisterTransactionTypeCode"))
				.setInvoicedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("cmInvoicedOn")))
				.setCreditMemoType(CreditMemoType.values()[rs.getInt("credit_memo_type")].code())
				.setOriginalInvoiceEffectiveName(rs.getString("companyName"))
				.setOriginalTransactionAmount(rs.getBigDecimal("revenue_amount"))
				.setOriginalInvoiceRevenueEffectiveDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("revenue_effective_date")))
				.setOriginalInvoiceDueDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("invoiceDueDate")))
				.setOriginalInvoiceRegisterTransactionTypeCode(rs.getString("invoiceRegisterTransactionTypeCode"))
				.setOriginalInvoiceType(rs.getString("invoiceInvoiceType"))
				.setOriginalInvoicedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("invoiceInvoicedOn")))
				.setOriginalInvoiceNumber(rs.getString("invoiceInvoiceNumber"))
				.setOriginalInvoiceSubscriptionInvoiceTypeCode(rs.getString("invoiceSubscriptionInvoiceTypeCode"))
				.setOriginalInvoicePaymentDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("invoicePaymentDate")));
			return accountingSummaryDetail;
		}
	}

	@Override
	public List<AccountingEndOfYearTaxSummary> getEndOfYearTaxReport(Calendar start, Calendar end) {
		String sql =
			"SELECT " +
				"t2.companyId, t2.companyName, " +
				"MIN(t2.account_service_type_code) AS serviceType, " +
				"MIN(t2.account_pricing_type_code) AS pricingType, " +
				"SUM(t2.amount) AS paidToWorkers, " +
				"MIN(t2.transaction_date) AS startDate, " +
				"MAX(t2.transaction_date) AS endDate " +
			"FROM " +
				"( " +
					"SELECT " +
						"@label := @label + CASE WHEN " +
							"t.account_service_type_code <> @sc OR " +
							"t.account_pricing_type_code <> @pc OR " +
							"t.id <> @cid THEN 1 ELSE 0 END AS label, " +
						"@cid := t.id AS companyId, " +
						"@sc := t.account_service_type_code AS account_service_type_code, " +
						"@pc := t.account_pricing_type_code AS account_pricing_type_code, " +
						"t.companyName, " +
						"t.transaction_date, " +
						"t.amount " +
					"FROM " +
					"( " +
						"SELECT " +
							"comp.id, " +
							"comp.name AS companyName, " +
							"reg.transaction_date, " +
							"wr.account_service_type_code, " +
							"wr.account_pricing_type_code, " +
							"-1 * reg.amount AS amount " +
						"FROM " +
							"register_transaction reg " +
						"JOIN work wor ON wor.id = reg.work_id " +
						"JOIN company comp ON comp.id = wor.company_id " +
						"JOIN work_resource_transaction wr ON reg.id = wr.id " +
						"WHERE reg.pending_flag = 'N' AND reg.register_transaction_type_code = 'payment' AND " +
						"reg.transaction_date > :start AND reg.transaction_date <= :end " +
						"ORDER BY comp.id, reg.transaction_date " +
					") t, " +
					"(SELECT @label:=0) AS l, " +
					"( " +
						"SELECT @sc := wr.account_service_type_code " +
						"FROM work_resource_transaction wr " +
						"LIMIT 1 " +
					") AS sc, " +
					"( " +
						"SELECT @pc := wr.account_pricing_type_code " +
						"FROM work_resource_transaction wr " +
						"LIMIT 1 " +
					") AS pc, " +
					"(SELECT @cid:=0) AS cid " +
				") t2 " +
			"GROUP BY t2.companyId, t2.companyName, t2.label";

		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("start", start);
		params.addValue("end", end);

		return jdbcTemplate.query(sql, params, new RowMapper<AccountingEndOfYearTaxSummary>() {

			@Override
			public AccountingEndOfYearTaxSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
				AccountingEndOfYearTaxSummary s = new AccountingEndOfYearTaxSummary();

				s.setCompanyName(rs.getString("companyName"));
				s.setCompanyId(rs.getLong("companyId"));
				s.setPaidToWorkers(rs.getBigDecimal("paidToWorkers"));
				s.setServiceType(rs.getString("serviceType"));
				s.setPricingType(rs.getString("pricingType"));
				s.setStartDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("startDate")));
				s.setEndDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("endDate")));

				return s;
			}
		});
	}

    @Override
    public List<AccountingSummaryDetail> getMoneyOutSubscriptionSoftwareFeesDetail(Calendar start, Calendar end, boolean companyHasVORServiceType) {
	    String sql = "SELECT r.amount, invoice.type invoiceType, invoice.invoice_number, company.effective_name companyName, \n" +
	      "invoice.created_on invoiceOn, invoice.due_date invoiceDueDate, invoice.payment_date\n" +
	      "FROM register_transaction r \n" +
	      "INNER JOIN service_transaction st ON st.id = r.id\n" +
	      "INNER JOIN payment_period ON subscription_payment_period_id = payment_period.id \n" +
	      "INNER JOIN invoice ON subscription_invoice_id =invoice.id \n" +
	      "INNER JOIN company ON company.id = invoice.company_id \n" +
	      "WHERE r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES, "'"), ",") + ")" +
	      "AND r.effective_date > :start \n" +
	      "AND r.effective_date < :end \n" +
	      "AND st.subscription_vendor_of_record = :companyHasVORServiceType";

	    MapSqlParameterSource params = new MapSqlParameterSource();
	    putStartAndEndInMap(params, start, end);
	    params.addValue("companyHasVORServiceType", companyHasVORServiceType);
	    return jdbcTemplate.query(sql, params, new AccountingSummaryDetailMapper());
    }

    @Override
    public List<AccountingSummaryDetail> getMoneyOutSubscriptionVORFeesDetail(Calendar start, Calendar end) {
	    String sql = "SELECT r.amount, invoice.type invoiceType, invoice.invoice_number, company.effective_name companyName, \n" +
	      "invoice.created_on invoiceOn, invoice.due_date invoiceDueDate, invoice.payment_date\n" +
	      "FROM register_transaction r \n" +
	      "INNER JOIN service_transaction st ON st.id = r.id\n" +
	      "INNER JOIN payment_period ON subscription_payment_period_id = payment_period.id \n" +
	      "INNER JOIN invoice ON subscription_invoice_id =invoice.id \n" +
	      "INNER JOIN company ON company.id = invoice.company_id \n" +
	      "WHERE r.register_transaction_type_code = :subsVor \n" +
	      "AND r.effective_date > :start \n" +
	      "AND r.effective_date < :end \n";

	    MapSqlParameterSource params = new MapSqlParameterSource();
	    putStartAndEndInMap(params, start, end);
	    params.addValue("subsVor", RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);
	    return jdbcTemplate.query(sql, params, new AccountingSummaryDetailMapper());
    }

	@Override
	public List<FastFundsReceivableSummaryDetail> getFastFundsReceivableSummaryDetails(Calendar previousRequestDate, Calendar requestDate) {
		String sql =
			"SELECT invoice.invoice_number, buyerCompany.effective_name buyerCompanyName, buyerCompany.id buyerCompanyId," +
				" workerCompany.effective_name workerCompanyName, workerCompany.id workerCompanyId, " +
				"invoice.due_date invoiceDueDate, invoice.payment_date, ffrc.transaction_date fastFundedOn, work.work_number workNumber, " +
				"ffrc.amount amount  " +
				"FROM fast_funds_receivable_commitment ffrc " +
				"INNER JOIN work ON ffrc.work_id = work.id " +
				"LEFT JOIN invoice ON work.invoice_id = invoice.id " +
				"INNER JOIN company buyerCompany ON buyerCompany.id = work.company_id " +
				"INNER JOIN user worker ON ffrc.creator_id = worker.id " +
				"INNER JOIN company workerCompany ON workerCompany.id = worker.company_id " +
				"WHERE (ffrc.transaction_date >= :start AND ffrc.transaction_date <= :end) OR (ffrc.effective_date >= :start AND ffrc.effective_date <= :end)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, previousRequestDate, requestDate);

		return jdbcTemplate.query(sql, params, new FastFundsReceivableSummaryDetailMapper());
	}

	@Override
	public List<AccountingSummaryDetail> getAccItemRevSubVorSw(Calendar previousRequestDate, Calendar requestDate) {
		return getAccItemRev(previousRequestDate, requestDate, RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES, true);
	}

	@Override
	public List<AccountingSummaryDetail> getAccItemRevSubVorVor(Calendar previousRequestDate, Calendar requestDate) {
		return getAccItemRev(previousRequestDate, requestDate, RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT, true);
	}

	@Override
	public List<AccountingSummaryDetail> getAccItemRevSubNVor(Calendar previousRequestDate, Calendar requestDate) {
		return getAccItemRev(previousRequestDate, requestDate, RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES, false);
	}

	private List<AccountingSummaryDetail> getAccItemRev(Calendar previousRequestDate, Calendar requestDate, Object type, boolean isVor) {
		String sql =
			  " SELECT revenue_amount amount, invoice.type invoiceType, invoice.invoice_number, company.effective_name companyName, \n "
			+ "        invoice.created_on invoiceOn, invoice.due_date invoiceDueDate, invoice.payment_date "
			+ " FROM register_transaction rt "
			+ " INNER JOIN service_transaction st ON rt.id = st.id "
			+ " INNER JOIN service_transaction_revenue revenue ON st.id = revenue.service_transaction_id "
			+ " INNER JOIN invoice_line_item item ON item.register_transaction_id = rt.id "
			+ " INNER JOIN invoice ON invoice.id = item.invoice_id "
			+ " INNER JOIN company ON company.id = invoice.company_id "
			+ " WHERE rt.register_transaction_type_code IN (:status) "
			+ " AND  revenue_effective_date >= :start AND revenue.revenue_effective_date < :end ";

		if (!RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT.equals(type)) {
			// For legacy reason, we don't have VOR type set correctly, we will ignore this for VOR fee.
			sql += " AND st.subscription_vendor_of_record = :isVor ";
		}
		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, previousRequestDate, requestDate);
		params.addValue("status", type);
		params.addValue("isVor", isVor);
		return jdbcTemplate.query(sql, params, new AccountingSummaryDetailMapper());
	}

	/**
	 * Get Credit Memo transaction details to build CSV report showing each transaction along with the details
	 * for the original invoice transaction
	 */
	@Override
	public List<AccountingCreditMemoSummaryDetail> getCreditMemoTransactions(
		Calendar previousRequestDate, Calendar requestDate, List<Integer> creditMemoTypeIds, boolean filterByPaid,
		boolean paid, boolean filterByVOR, boolean isVOR) {

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, previousRequestDate, requestDate);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT i.type cmInvoiceType, i.invoice_number cmInvoiceNumber, ")
			.append("company.effective_name companyName, i.created_on cmInvoicedOn, ")
			.append("i.due_date cmDueDate, i.payment_date cmPaymentDate, ct.credit_memo_type, ")
			.append("rt.register_transaction_type_code cmRegisterTransactionTypeCode, ")
			.append("cma.reason, cma.note, i.subscription_invoice_type_code cmSubscriptionInvoiceTypeCode, ")
			.append("revenue_amount, oi.type invoiceInvoiceType, oi.invoice_number invoiceInvoiceNumber, ")
			.append("oi.created_on invoiceInvoicedOn, ")
			.append("oi.due_date invoiceDueDate, oi.payment_date invoicePaymentDate, ")
			.append("ort.register_transaction_type_code invoiceRegisterTransactionTypeCode, ")
			.append("str.revenue_effective_date, oi.subscription_invoice_type_code invoiceSubscriptionInvoiceTypeCode ")
			.append("FROM register_transaction rt ")
			.append("INNER JOIN credit_memo_transaction ct ON rt.id = ct.id ")
			.append("INNER JOIN invoice_line_item ili ON ili.register_transaction_id = rt.id ")
			.append("INNER JOIN invoice i ON i.id = ili.invoice_id ")
			.append("INNER JOIN credit_memo_audit cma ON cma.invoice_id = i.id ")
			.append("INNER JOIN invoice oi ON cma.original_invoice_id = oi.id ")
			.append("INNER JOIN invoice_line_item oili ON oili.invoice_id = oi.id and oili.type=ili.type ")
			.append("INNER JOIN register_transaction ort on ort.id = oili.register_transaction_id ")
			.append("INNER JOIN company ON company.id = i.company_id ")
			.append("INNER JOIN service_transaction st ON ort.id = st.id ")
			.append("INNER JOIN service_transaction_revenue str ON st.id = str.service_transaction_id ")
			.append("WHERE ct.credit_memo_type IN (:ids)")
			.append("AND rt.effective_date >= :start AND rt.effective_date <= :end ");
			params.addValue("ids", creditMemoTypeIds);
		if (filterByVOR) {
			sql.append("AND ct.subscription_vendor_of_record = :isVor ");
			params.addValue("isVor", isVOR);
		}
		if (filterByPaid) {
			sql.append("AND ct.original_invoice_paid = :paid ");
			params.addValue("paid", paid);
		}

		return jdbcTemplate.query(sql.toString(), params, new AccountingCreditMemoSummaryDetailMapper());
	}

	@Override
	public List<AccountingSummaryDetail> getOfflinePaymentsDetail(Calendar start, Calendar end, boolean subscription, boolean vor) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT reg.amount, i.type invoiceType, i.invoice_number, c.effective_name companyName, ")
			.append("i.created_on invoiceOn, i.due_date invoiceDueDate, i.payment_date  ")
			.append("FROM register_transaction reg ")
			.append("INNER JOIN work_resource_transaction wt ON wt.id = reg.id ")
			.append("INNER JOIN work w ON w.id = reg.work_id ")
			.append("INNER JOIN invoice i ON i.id = w.invoice_id ")
			.append("INNER JOIN company c ON c.id = w.company_id ")
			.append("WHERE reg.pending_flag = 'N' ")
			.append("AND reg.transaction_date > :start AND reg.transaction_date <= :end ")
			.append("AND reg.register_transaction_type_code = :transactionTypeCode ")
			.append("AND wt.account_pricing_type_code = :pricingTypeCode ")
			.append("AND wt.account_service_type_code in (:serviceTypeCodes)");

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("transactionTypeCode", RegisterTransactionType.RESOURCE_OFFLINE_WORK_PAYMENT);
		params.addValue("pricingTypeCode", subscription ? AccountPricingType.SUBSCRIPTION_PRICING_TYPE : AccountPricingType.TRANSACTIONAL_PRICING_TYPE);
		params.addValue("serviceTypeCodes", vor ? AccountServiceType.VOR_SERVICE_TYPES : AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES);

		return jdbcTemplate.query(sql.toString(), params, new AccountingSummaryDetailMapper());
	}
}
