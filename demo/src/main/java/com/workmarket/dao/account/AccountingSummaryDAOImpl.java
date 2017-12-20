package com.workmarket.dao.account;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.AccountingSummary;
import com.workmarket.domains.model.account.BankAccountTransactionStatus;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Repository
public class AccountingSummaryDAOImpl extends AbstractDAO<AccountingSummary> implements AccountingSummaryDAO {

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	private static final String SUBSCRIPTION_REVENUE_SQL = " SELECT COALESCE(SUM(revenue.revenue_amount), 0.00) " +
			" FROM 	register_transaction r  " +
			" INNER JOIN service_transaction st ON st.id = r.id " +
			" INNER JOIN service_transaction_revenue revenue ON st.id = revenue.service_transaction_id ";

	protected Class<AccountingSummary> getEntityClass() {
		return AccountingSummary.class;
	}

	@SuppressWarnings("unchecked")
	public List<AccountingSummary> findAllAccountingSummaries() {
		return (List<AccountingSummary>) getFactory().getCurrentSession().getNamedQuery("accounting_summary.findAll").list();
	}

	private void putStartAndEndInMap(MapSqlParameterSource params, Calendar start, Calendar end) {

		params.addValue("start", start);
		params.addValue("end", end);
	}

	private void putStartAndEndInMap(MapSqlParameterSource params, Calendar start, Calendar end, String transactionType) {

		putStartAndEndInMap(params, start, end);
		params.addValue("transactionType", transactionType);
	}

	@Override
	public Calendar findDateOfLastSummary() {
		String query = "SELECT max(request_date) FROM accounting_summary";
		return DateUtilities.getCalendarFromDate(jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Timestamp.class));
	}

	@Override
	public Calendar findPreviousRequestDateOfSummary(long accountSummaryId) {
		String query = "SELECT previous_request_date FROM accounting_summary WHERE id = " + accountSummaryId;
		return DateUtilities.getCalendarFromDate(jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Timestamp.class));
	}

	@Override
	public Calendar findRequestDateOfSummary(long accountSummaryId) {
		String query = "SELECT request_date FROM accounting_summary WHERE id = " + accountSummaryId;
		return DateUtilities.getCalendarFromDate(jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Timestamp.class));
	}

	@Override
	public Calendar findStartFiscalYearByYear(Integer year) {
		String query = "SELECT fiscal_year_start_date FROM fiscal_year WHERE year = :year";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("year", year);
		try {
			return DateUtilities.getCalendarFromDate(jdbcTemplate.queryForObject(query, params, Timestamp.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public BigDecimal calculateMoneyInFastFunds(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) " +
			" FROM register_transaction reg " +
			" WHERE register_transaction_type_code = :transactionType" +
			" AND reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end, RegisterTransactionType.FAST_FUNDS_PAYMENT);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyInChecks(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) " +
				" FROM 	register_transaction reg, credit_transaction ct " +
				" WHERE pending_flag = 'N' " +
				" AND 	reg.id = ct.id " +
				" AND 	register_transaction_type_code = :transactionType" +
				" AND 	reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end, RegisterTransactionType.CREDIT_CHECK_DEPOSIT);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyInAch(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, bank_account_transaction bt WHERE pending_flag = 'N' " +
				" AND reg.id = bt.id AND register_transaction_type_code = :transactionType AND bank_account_transaction_status_code = :bank_account_status" +
				" AND bt.approved_by_bank_date > :start AND bt.approved_by_bank_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bank_account_status", BankAccountTransactionStatus.PROCESSED);
		putStartAndEndInMap(params, start, end, RegisterTransactionType.ADD_FUNDS);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyInWire(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, credit_transaction wt WHERE pending_flag = 'N' " +
				" AND reg.id = wt.id AND register_transaction_type_code = :transactionType" +
				" AND reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end, RegisterTransactionType.CREDIT_WIRE_DIRECT_DEPOSIT);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}
	
	@Override
	public BigDecimal calculateMoneyInCreditCard(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, credit_card_transaction ct WHERE pending_flag = 'N' " +
				" AND reg.id = ct.id AND register_transaction_type_code = :transactionType" +
				" AND reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end, RegisterTransactionType.ADD_FUNDS);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateCreditMemoTotalByType(
		Integer creditMemoTypeId, Calendar start, Calendar end, boolean filterByPaid, boolean paid) {

		return calculateCreditMemoTotalByType(Collections.singletonList(creditMemoTypeId), start, end, filterByPaid, paid, false, false);
	}

	@Override
	public BigDecimal calculateCreditMemoTotalByType(
		List<Integer> creditMemoTypeIds, Calendar start, Calendar end, boolean filterByPaid,
		boolean paid, boolean filterByVOR, boolean isVOR) {

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, credit_memo_transaction ct ")
			.append(" WHERE pending_flag = 'N' ")
			.append(" AND reg.id = ct.id")
			.append(" AND reg.transaction_date > :start AND reg.transaction_date <= :end")
			.append(" AND ct.credit_memo_type IN (:ids)");
		params.addValue("ids", creditMemoTypeIds);
		if(filterByPaid){
			sql.append(" AND ct.original_invoice_paid = :originalInvoicePaid");
			params.addValue("originalInvoicePaid", paid);
		}
		if(filterByVOR){
			sql.append(" AND ct.subscription_vendor_of_record = :isVOR ");
			params.addValue("isVOR", isVOR);
		}

		return jdbcTemplate.queryForObject(sql.toString(), params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutWithdrawals(Calendar start, Calendar end, boolean usaTransactions) {

		StringBuilder query = new StringBuilder("SELECT COALESCE(SUM(amount), 0.00) " +
				" FROM 	register_transaction reg " +
				" INNER JOIN bank_account_transaction bt ON reg.id = bt.id " +
				" INNER JOIN bank_account ON bank_account.id = bt.bank_account_id " +
				" WHERE pending_flag = 'N' " +
				" AND 	register_transaction_type_code = :transactionType " +
				" AND 	bank_account_transaction_status_code = :bank_account_status" +
				" AND 	bt.approved_by_bank_date > :start AND bt.approved_by_bank_date <= :end ");

		if (usaTransactions) {
	   		query.append(" AND bank_account.country_id = :usa");
		} else  {
			query.append(" AND bank_account.country_id NOT IN (:usa)");
		}

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("bank_account_status", BankAccountTransactionStatus.PROCESSED)
				.addValue("usa", Country.USA_COUNTRY.getId());

		putStartAndEndInMap(params, start, end, RegisterTransactionType.REMOVE_FUNDS);
		return jdbcTemplate.queryForObject(query.toString(), params, BigDecimal.class);

	}

	@Override
	public BigDecimal calculateMoneyOutFees(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) " +
				" FROM 	register_transaction reg INNER JOIN work_resource_transaction wt ON wt.id = reg.id " +
				" WHERE pending_flag = 'N' "+
				" AND 	reg.register_transaction_type_code IN (:lane2_work,:lane3_work, :lane2_fee, :lane3_fee)" +
				" AND 	reg.transaction_date > :start AND reg.transaction_date <= :end " +
				" AND 	wt.account_pricing_type_code = :transactional";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lane2_work", RegisterTransactionType.FINISHED_WORK_FEE_LANE2);
		params.addValue("lane3_work", RegisterTransactionType.FINISHED_WORK_FEE_LANE3);
		params.addValue("lane2_fee", RegisterTransactionType.NEW_WORK_LANE_2);
		params.addValue("lane3_fee", RegisterTransactionType.NEW_WORK_LANE_3);
		params.addValue("transactional", AccountPricingType.TRANSACTIONAL_PRICING_TYPE);
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutCreditCardFees(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, credit_card_transaction ct WHERE  " +
				" pending_flag = 'N' AND reg.id = ct.id AND register_transaction_type_code IN ('ccfee', 'amexCcFee')" +
				" AND reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutBackgroundChecks(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) " +
				" FROM 	register_transaction reg " +
				" INNER JOIN background_check_transaction ct ON reg.id = ct.id " +
				" WHERE pending_flag = 'N'" +
				" AND 	register_transaction_type_code IN (:bkgrdchk, :bkgrdchkCA, :bkgrdchkIN) " +
				" AND 	reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("bkgrdchk", RegisterTransactionType.BACKGROUND_CHECK);
		params.addValue("bkgrdchkCA", RegisterTransactionType.BACKGROUND_CHECK_CANADA);
		params.addValue("bkgrdchkIN", RegisterTransactionType.BACKGROUND_CHECK_INTERNATIONAL);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutDrugTests(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, drug_test_transaction ct WHERE pending_flag = 'N'" +
				" AND reg.id = ct.id AND register_transaction_type_code = :transactionType" +
				" AND reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end, RegisterTransactionType.DRUG_TEST);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutAchVerifications(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, bank_account_transaction bt WHERE pending_flag = 'N' " +
				" AND reg.id = bt.id AND register_transaction_type_code = :transactionType AND bank_account_transaction_status_code = :bank_account_status" +
				" AND bt.approved_by_bank_date > :start AND bt.approved_by_bank_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bank_account_status", BankAccountTransactionStatus.PROCESSED);
		putStartAndEndInMap(params, start, end, RegisterTransactionType.ACH_VERIFY);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	public BigDecimal calculateTotalMoneyOutDebitTransactions(Calendar start, Calendar end) {
		//All types of debit transactions
		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg " +
			" INNER 	JOIN debit_transaction ON debit_transaction.id = reg.id " +
			" WHERE 	reg.pending_flag = 'N'" +
			" AND 		reg.effective_date > :start AND reg.effective_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateTotalMoneyOutCreditTransactions(Calendar start, Calendar end) {
		List<String> creditTxsTypes = Lists.newArrayList(RegisterTransactionType.CREDIT_REGISTER_TRANSACTION_TYPE_CODES);
		creditTxsTypes.removeAll(
			ImmutableList.of(
				RegisterTransactionType.CREDIT_WIRE_DIRECT_DEPOSIT,
				RegisterTransactionType.CREDIT_CHECK_DEPOSIT
			)
		);

		//All types of credit transactions EXCEPT CHECKS and WIRE TRANSFER
		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg " +
			" INNER 	JOIN credit_transaction ON credit_transaction.id = reg.id " +
			" WHERE 	reg.pending_flag = 'N'" +
			" AND 		reg.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(creditTxsTypes, "'"), ",") + ")" +
			" AND 		reg.effective_date > :start AND reg.effective_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateTotalMoneyOutFastFundsReceivablePayments(Calendar start, Calendar end) {
		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM fast_funds_receivable_commitment ffrc " +
			" WHERE ffrc.is_pending = false " +
			" AND ffrc.effective_date > :start AND ffrc.effective_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateTotalMoneyOutFastFundsFees(Calendar start, Calendar end) {
		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg " +
			" WHERE reg.register_transaction_type_code = :transactionType " +
			" AND reg.effective_date > :start AND reg.effective_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end, RegisterTransactionType.FAST_FUNDS_FEE);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateTotalMoneyOnSystem(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg WHERE pending_flag = 'N' " +
				" AND register_transaction_type_code not in " +
				" (:commitment, :paycommit, :achverify, :pytrmscmmt, :pytrmspyct, :removefund, :addfunds, :invoicePay, :batchPay, :removefpp, " +
				" :payPalFee, :payPalFeeC, :payPalFeeI, :wmPPFee, :wmPPFeeC, :wmPPFeeI, :subInvoice, :subsPeriod, :subsVoR, :subsSetup, :subsAddOn, :subDisc, :checkDep, :directDep, " +
				" :adhInvoice, :depositFee, :withdraFee, :latePayFee, :miscellFee, :addgenr, :addproj, :removegenr, :removeproj, :removefgcc, :bundleAuth, :bundlePPAu, :fastFunFee, :fastFunDbt) " +
				" AND reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("commitment", RegisterTransactionType.BUYER_COMMITMENT_TO_PAY);
		params.addValue("paycommit", RegisterTransactionType.RESOURCE_COMMITMENT_TO_RECEIVE_PAY);
		params.addValue("achverify", RegisterTransactionType.ACH_VERIFY);
		params.addValue("pytrmscmmt", RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT);
		params.addValue("pytrmspyct", RegisterTransactionType.RESOURCE_PAYMENT_TERMS_COMMITMENT_TO_RECEIVE_PAY);
		params.addValue("addfunds", RegisterTransactionType.ADD_FUNDS);
		params.addValue("removefund", RegisterTransactionType.REMOVE_FUNDS);
		params.addValue("addgenr", RegisterTransactionType.ADD_FUNDS_TO_GENERAL);
		params.addValue("removegenr", RegisterTransactionType.REMOVE_FUNDS_FROM_GENERAL);
		params.addValue("addproj", RegisterTransactionType.ADD_FUNDS_TO_PROJECT);
		params.addValue("removeproj", RegisterTransactionType.REMOVE_FUNDS_FROM_PROJECT);
		params.addValue("invoicePay", RegisterTransactionType.INVOICE_PAYMENT);
		params.addValue("batchPay", RegisterTransactionType.ASSIGNMENT_BATCH_PAYMENT);
		params.addValue("removefpp", RegisterTransactionType.REMOVE_FUNDS_PAYPAL);
		params.addValue("payPalFee", RegisterTransactionType.PAY_PAL_FEE_USA);
		params.addValue("payPalFeeC", RegisterTransactionType.PAY_PAL_FEE_CANADA);
		params.addValue("payPalFeeI", RegisterTransactionType.PAY_PAL_FEE_INTL);
		params.addValue("wmPPFee", RegisterTransactionType.WM_PAY_PAL_FEE_USA);
		params.addValue("wmPPFeeC", RegisterTransactionType.WM_PAY_PAL_FEE_CANADA);
		params.addValue("wmPPFeeI", RegisterTransactionType.WM_PAY_PAL_FEE_INTL);
		params.addValue("checkDep", RegisterTransactionType.CREDIT_CHECK_DEPOSIT);
		params.addValue("directDep", RegisterTransactionType.CREDIT_WIRE_DIRECT_DEPOSIT);
		params.addValue("removefgcc", RegisterTransactionType.REMOVE_FUNDS_GCC);
		params.addValue("bundleAuth", RegisterTransactionType.BUYER_COMMITMENT_TO_PAY_WORK_BUNDLE);
		params.addValue("bundlePPAu", RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT_WORK_BUNDLE);
		params.addValue("fastFunFee", RegisterTransactionType.FAST_FUNDS_FEE);
		params.addValue("fastFunDbt", RegisterTransactionType.FAST_FUNDS_DEBIT);

		//Subscription and service transactions
		params.addValue("subInvoice", RegisterTransactionType.SUBSCRIPTION_INVOICE_PAYMENT);
		params.addValue("subsPeriod", RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT);
		params.addValue("subsVoR", RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);
		params.addValue("subsSetup", RegisterTransactionType.SUBSCRIPTION_SETUP_FEE_PAYMENT);
		params.addValue("subsAddOn", RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT);
		params.addValue("subDisc", RegisterTransactionType.SUBSCRIPTION_DISCOUNT);
		params.addValue("adhInvoice", RegisterTransactionType.AD_HOC_SERVICE_INVOICE_PAYMENT);
		params.addValue("depositFee", RegisterTransactionType.SERVICE_FEE_DEPOSIT_RETURN);
		params.addValue("withdraFee", RegisterTransactionType.SERVICE_FEE_WITHDRAWAL_RETURN);
		params.addValue("latePayFee", RegisterTransactionType.SERVICE_FEE_LATE_PAYMENT);
		params.addValue("miscellFee", RegisterTransactionType.SERVICE_FEE_MISCELLANEOUS);


		BigDecimal result = jdbcTemplate.queryForObject(query, params, BigDecimal.class);

		BigDecimal withdrawalsUsa = calculateMoneyOutWithdrawals(start, end, true);
		BigDecimal withdrawalsInternational = calculateMoneyOutWithdrawals(start, end, false);
		BigDecimal fastFundsReceivablePayments = calculateTotalMoneyOutFastFundsReceivablePayments(start, end);
		BigDecimal fastFundFees = calculateTotalMoneyOutFastFundsFees(start, end);
		BigDecimal achFunding = calculateMoneyInAch(start, end);
		BigDecimal wire = calculateMoneyInWire(start, end);
		BigDecimal creditCards =  calculateMoneyInCreditCard(start, end);
		BigDecimal checks = calculateMoneyInChecks(start, end);
		BigDecimal payPalWithdrawals = calculateMoneyOutPayPalWithdrawal(start, end);
		BigDecimal payPalFees = calculateMoneyOutPayPalFees(start, end);
		BigDecimal subscriptionFees = calculateMoneyOutAllSubscriptionFees(start, end);
		BigDecimal serviceFees = calculateMoneyOutAdHocServiceFees(start, end);
		BigDecimal wmVisaFees = calculateMoneyOutGCCWithdrawal(start, end);

		return result
				.add(withdrawalsUsa)
				.add(withdrawalsInternational)
				.add(fastFundsReceivablePayments)
				.add(fastFundFees)
				.add(achFunding)
				.add(wire)
				.add(creditCards)
				.add(checks)
				.add(payPalWithdrawals)
				.add(payPalFees)
				.add(subscriptionFees)
				.add(serviceFees)
				.add(wmVisaFees);
	}

	@Override
	public BigDecimal calculateTotalCompletedAssignments(Calendar start, Calendar end) {
		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, work_resource_transaction wt WHERE pending_flag = 'N' " +
				" AND reg.id = wt.id AND register_transaction_type_code = :transactionType" +
				" AND reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end, RegisterTransactionType.BUYER_WORK_PAYMENT);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateTotalEarnedForAssignments(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, work_resource_transaction wt WHERE pending_flag = 'N' " +
				" AND reg.id = wt.id AND register_transaction_type_code = :transactionType" +
				" AND reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end, RegisterTransactionType.RESOURCE_WORK_PAYMENT);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateTotalInAPStatus(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, work_resource_transaction wt WHERE pending_flag = true " +
				" AND reg.id = wt.id AND register_transaction_type_code = :transactionType" +
				" AND reg.transaction_date > :start AND reg.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end, RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutPayPalWithdrawal(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, bank_account_transaction bt " +
				" WHERE pending_flag = 'N' " +
				" AND 	reg.id = bt.id AND register_transaction_type_code = :transactionType AND bank_account_transaction_status_code = :bank_account_status" +
				" AND 	bt.approved_by_bank_date > :start AND bt.approved_by_bank_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bank_account_status", BankAccountTransactionStatus.PROCESSED);
		putStartAndEndInMap(params, start, end, RegisterTransactionType.REMOVE_FUNDS_PAYPAL);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutGCCWithdrawal(Calendar start, Calendar end) {
		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, bank_account_transaction bt " +
				" WHERE pending_flag = 'N' " +
				" AND 	reg.id = bt.id AND register_transaction_type_code = :transactionType AND bank_account_transaction_status_code = :bank_account_status" +
				" AND 	bt.approved_by_bank_date > :start AND bt.approved_by_bank_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bank_account_status", BankAccountTransactionStatus.PROCESSED);
		putStartAndEndInMap(params, start, end, RegisterTransactionType.REMOVE_FUNDS_GCC);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutPayPalFees(Calendar start, Calendar end) {

		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg, bank_account_transaction bt " +
				" WHERE pending_flag = 'N' " +
				" AND 	reg.id = bt.id AND register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.PAY_PAL_FEE_REGISTER_TRANSACTION_TYPES, "'"), ",") + ")" +
				" AND 	bank_account_transaction_status_code = :bank_account_status" +
				" AND 	bt.approved_by_bank_date > :start AND bt.approved_by_bank_date <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bank_account_status", BankAccountTransactionStatus.PROCESSED);
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutWorkMarketFeesPaidToPayPal(Calendar start, Calendar end) {
		String query = "SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg " +
				" INNER JOIN secret_transaction st ON st.id = reg.id " +
				" WHERE pending_flag = 'N' " +
				" AND register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.WORK_MARKET_FEES_PAID_TO_PAY_PAL, "'"), ",") + ")" +
				" AND st.approved_date > :start AND st.approved_date <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bank_account_status", BankAccountTransactionStatus.PROCESSED);
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(query, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateTotalApAvailable() {

		String query = "SELECT COALESCE(SUM(ap_limit - accounts_payable_balance), 0.00) FROM account_register";
		return jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), BigDecimal.class);
	}

	@Override
	public BigDecimal calculateTotalApExposure() {
		String sql = "SELECT SUM(amount) FROM register_transaction WHERE pending_flag = :pendingFlag " +
				" AND register_transaction_type_code = :transactionTypeCode";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pendingFlag", "Y");
		params.addValue("transactionTypeCode", RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public Map<String, Double> calculateMoneyOutAdHocServiceFeesMap(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) AS total, r.register_transaction_type_code " +
				" FROM		register_transaction r " +
				" INNER 	JOIN service_transaction st ON st.id = r.id " +
				" WHERE 	r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.AD_HOC_INVOICES_FEES_TRANSACTION_TYPE_CODES, "'"), ",") + ")" +
				" AND 		r.pending_flag = 'N' " +
				" AND 		r.effective_date > :start AND r.effective_date <= :end " +
				" GROUP 	BY r.register_transaction_type_code ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);

		Map<String, Double> result = Maps.newLinkedHashMap();
		List<Map<String, Object>> transactionMap = jdbcTemplate.queryForList(sql, params);
		for (Map<String, Object> row : transactionMap) {
			result.put( (String) row.get("register_transaction_type_code"), ((BigDecimal) row.get("total")).doubleValue());
		}
		return result;
	}

	@Override
	public BigDecimal calculateMoneyOutAdHocServiceFees(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) " +
				" FROM		register_transaction r " +
				" INNER 	JOIN service_transaction st ON st.id = r.id " +
				" WHERE 	r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.AD_HOC_INVOICES_FEES_TRANSACTION_TYPE_CODES, "'"), ",") + ")" +
				" AND 		r.pending_flag = 'N' " +
				" AND 		r.effective_date > :start AND r.effective_date <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateThroughputByServiceTypeAndPricingType(Calendar start, Calendar end, List<String> serviceTypes, String pricingType) {
		if (CollectionUtils.isEmpty(serviceTypes) || StringUtils.isEmpty(pricingType)) {
			return BigDecimal.ZERO;
		}

		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) FROM register_transaction r" +
				" INNER 	JOIN work_resource_transaction wr ON wr.id = r.id " +
				" WHERE 	r.pending_flag = :pendingFlag " +
				" AND 		r.register_transaction_type_code = :transactionTypeCode " +
				" AND 		wr.account_pricing_type_code = :pricingType " +
				" AND 		wr.account_service_type_code IN (" + StringUtils.join(StringUtilities.surround(serviceTypes, "'"), ",") + ")" +
				" AND 		r.transaction_date > :start AND r.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pendingFlag", "N");
		params.addValue("transactionTypeCode", RegisterTransactionType.BUYER_WORK_PAYMENT);
		params.addValue("pricingType", pricingType);
		params.addValue("start", start);
		params.addValue("end", end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutTransactionalFeesByServiceType(Calendar start, Calendar end, List<String> serviceTypes) {
		if (CollectionUtils.isEmpty(serviceTypes)) {
			return BigDecimal.ZERO;
		}

		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) FROM register_transaction r" +
				" INNER 	JOIN work_resource_transaction wr ON wr.id = r.id " +
				" WHERE 	r.pending_flag = :pendingFlag " +
				" AND 		r.register_transaction_type_code in (:finwork2,:finwork3, :lane2work, :lane3work) " +
				" AND 		wr.account_pricing_type_code = :pricingType " +
				" AND 		wr.account_service_type_code IN (" + StringUtils.join(StringUtilities.surround(serviceTypes, "'"), ",") + ")" +
				" AND 		r.transaction_date > :start AND r.transaction_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("finwork2", RegisterTransactionType.FINISHED_WORK_FEE_LANE2);
		params.addValue("finwork3", RegisterTransactionType.FINISHED_WORK_FEE_LANE3);
		params.addValue("lane2work", RegisterTransactionType.NEW_WORK_LANE_2);
		params.addValue("lane3work", RegisterTransactionType.NEW_WORK_LANE_3);
		params.addValue("pendingFlag", "N");
		params.addValue("pricingType", AccountPricingType.TRANSACTIONAL_PRICING_TYPE);
		params.addValue("start", start);
		params.addValue("end", end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutSubscriptionSoftwareFees(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) FROM register_transaction r" +
				" INNER 	JOIN service_transaction st ON st.id = r.id  " +
				" WHERE 	r.pending_flag = 'N' " +
				" AND 		r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES, "'"), ",") + ")" +
				" AND 		r.effective_date > :start AND r.effective_date <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutSubscriptionSoftwareFees(Calendar start, Calendar end, boolean companyHasVORServiceType) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) FROM register_transaction r" +
				" INNER 	JOIN service_transaction st ON st.id = r.id  " +
				" WHERE 	r.pending_flag = 'N' " +
				" AND 		r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES, "'"), ",") + ")" +
				" AND 		r.effective_date > :start AND r.effective_date <= :end " +
				" AND		st.subscription_vendor_of_record = :companyHasVORServiceType ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("companyHasVORServiceType", companyHasVORServiceType);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutSubscriptionVORFees(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) FROM register_transaction r" +
				" INNER 	JOIN service_transaction st ON st.id = r.id  " +
				" WHERE 	r.pending_flag = :pendingFlag " +
				" AND 		r.register_transaction_type_code = :subsVoR " +
				" AND 		r.effective_date > :start AND r.effective_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pendingFlag", "N");
		params.addValue("subsVoR", RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	private BigDecimal calculateMoneyOutAllSubscriptionFees(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) FROM register_transaction r" +
				" INNER 	JOIN service_transaction st ON st.id = r.id  " +
				" WHERE 	r.pending_flag = 'N' " +
				" AND 		r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.SUBSCRIPTION_ALL_FEES_TRANSACTION_CODES, "'"), ",") + ")" +
				" AND 		r.effective_date > :start AND r.effective_date <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateMoneyOutProfessionalServicesFees(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) FROM register_transaction r" +
				" INNER 	JOIN service_transaction st ON st.id = r.id  " +
				" WHERE 	r.pending_flag = 'N' " +
				" AND 		r.register_transaction_type_code = :subsAddOn " +
				" AND 		r.effective_date > :start AND r.effective_date <= :end";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("subsAddOn", RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT);
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateSubscriptionRevenueSoftwareFees(Calendar start, Calendar end, boolean companyHasVORServiceType) {
		String sql = SUBSCRIPTION_REVENUE_SQL +
			" WHERE 	r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES, "'"), ",") + ")" +
			" AND 		revenue.revenue_effective_date > :start AND revenue.revenue_effective_date <= :end " +
			" AND		st.subscription_vendor_of_record = :companyHasVORServiceType ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("companyHasVORServiceType", companyHasVORServiceType);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateSubscriptionRevenueVORFees(Calendar start, Calendar end) {
		String sql = SUBSCRIPTION_REVENUE_SQL +
			" WHERE		r.register_transaction_type_code = :subsVoR " +
			" AND 		revenue.revenue_effective_date > :start AND revenue.revenue_effective_date <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("subsVoR", RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateRevenueProfessionalServicesFees(Calendar start, Calendar end) {
		String sql = SUBSCRIPTION_REVENUE_SQL +
				" WHERE		r.register_transaction_type_code = :addOn " +
				" AND 		revenue.revenue_effective_date > :start AND revenue.revenue_effective_date <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("addOn", RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
	}

	@Override public BigDecimal calculateNonImmediateSubscriptionRevenueSoftwareFees(Calendar start, Calendar end, boolean companyHasVORServiceType) {
		String sql = SUBSCRIPTION_REVENUE_SQL +
				" WHERE 	r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES, "'"), ",") + ")" +
				" AND 		revenue.revenue_effective_date > :start AND revenue.revenue_effective_date <= :end " +
				" AND		st.subscription_vendor_of_record = :companyHasVORServiceType " +
				" AND 		revenue.deferred_revenue = true";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("companyHasVORServiceType", companyHasVORServiceType);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
	}

	@Override public BigDecimal calculateNonImmediateSubscriptionRevenueVORFees(Calendar start, Calendar end) {
		String sql = SUBSCRIPTION_REVENUE_SQL +
				" WHERE		r.register_transaction_type_code = :subsVoR " +
				" AND 		revenue.revenue_effective_date > :start AND revenue.revenue_effective_date <= :end " +
				" AND 		revenue.deferred_revenue = true";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("subsVoR", RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
	}

	@Override public BigDecimal calculateNonImmediateRevenueProfessionalServicesFees(Calendar start, Calendar end) {
		String sql = SUBSCRIPTION_REVENUE_SQL +
				" WHERE		r.register_transaction_type_code = :addOn " +
				" AND 		revenue.revenue_effective_date > :start AND revenue.revenue_effective_date <= :end " +
				" AND 		revenue.deferred_revenue = true";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("addOn", RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public Map<String, Double> calculateRevenueAdHocServiceFees(Calendar start, Calendar end) {
		String sql = " SELECT COALESCE(SUM(revenue.revenue_amount), 0.00) AS revenue, r.register_transaction_type_code " +
				" FROM 	register_transaction r  " +
				" INNER JOIN service_transaction st ON st.id = r.id " +
				" INNER JOIN service_transaction_revenue revenue ON st.id = revenue.service_transaction_id " +
				" WHERE 	r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.AD_HOC_INVOICES_FEES_TRANSACTION_TYPE_CODES, "'"), ",") + ")" +
				" AND 		revenue.revenue_effective_date > :start AND revenue.revenue_effective_date <= :end " +
				" GROUP BY 	r.register_transaction_type_code ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);

		Map<String, Double> result = Maps.newLinkedHashMap();
		List<Map<String, Object>> transactionMap = jdbcTemplate.queryForList(sql, params);
		for (Map<String, Object> row : transactionMap) {
			result.put( (String) row.get("register_transaction_type_code"), ((BigDecimal) row.get("revenue")).doubleValue());
		}
		return result;
	}

	@Override
	public BigDecimal calculateSubscriptionDeferredRevenueSoftwareFees(Calendar start, Calendar end, boolean companyHasVORServiceType) {
		String sql = SUBSCRIPTION_REVENUE_SQL +
				" WHERE 	r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES, "'"), ",") + ")" +
				" AND		st.invoiced_on > :start AND st.invoiced_on <= :end " +
				" AND 		revenue.revenue_effective_date > :end " +
				" AND		st.subscription_vendor_of_record = :companyHasVORServiceType " +
				" AND 		revenue.deferred_revenue = true ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("companyHasVORServiceType", companyHasVORServiceType);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateSubscriptionDeferredRevenueVORFees(Calendar start, Calendar end) {
		String sql = SUBSCRIPTION_REVENUE_SQL +
				" WHERE		r.register_transaction_type_code = :subsVoR " +
				" AND		st.invoiced_on > :start AND st.invoiced_on <= :end " +
				" AND 		revenue.revenue_effective_date > :end " +
				" AND 		revenue.deferred_revenue = true ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("subsVoR", RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateDeferredRevenueProfessionalServicesFees(Calendar start, Calendar end) {
		String sql = SUBSCRIPTION_REVENUE_SQL +
				" WHERE		r.register_transaction_type_code = :addOn " +
				" AND		st.invoiced_on > :start AND st.invoiced_on <= :end " +
				" AND 		revenue.revenue_effective_date > :end " +
				" AND 		revenue.deferred_revenue = true ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("addOn", RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public Map<String, Double> calculateMoneyOutCreditTransactions(Calendar start, Calendar end) {
		List<String> creditTxsTypes = Lists.newArrayList(RegisterTransactionType.CREDIT_REGISTER_TRANSACTION_TYPE_CODES);
		creditTxsTypes.removeAll(
			ImmutableList.of(
				RegisterTransactionType.CREDIT_WIRE_DIRECT_DEPOSIT,
				RegisterTransactionType.CREDIT_CHECK_DEPOSIT
			)
		);

		//All types of credit transactions EXCEPT CHECKS and WIRE TRANSFER
		String query = "SELECT COALESCE(SUM(amount), 0.00) AS total, reg.register_transaction_type_code " +
				" FROM 		register_transaction reg " +
				" INNER 	JOIN credit_transaction ON credit_transaction.id = reg.id " +
				" WHERE 	reg.pending_flag = 'N'" +
				" AND 		reg.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(creditTxsTypes, "'"), ",") + ")" +
				" AND 		reg.effective_date > :start AND reg.effective_date <= :end " +
				" GROUP 	BY reg.register_transaction_type_code ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);

		Map<String, Double> result = Maps.newLinkedHashMap();
		List<Map<String, Object>> transactionMap = jdbcTemplate.queryForList(query, params);
		for (Map<String, Object> row : transactionMap) {
			result.put( (String) row.get("register_transaction_type_code"), ((BigDecimal) row.get("total")).doubleValue());
		}
		return result;
	}

	@Override
	public Map<String, Double> calculateMoneyOutDebitTransactions(Calendar start, Calendar end) {
		//All types of debit transactions
		String query = "SELECT COALESCE(SUM(amount), 0.00) AS total, reg.register_transaction_type_code " +
				" FROM 		register_transaction reg " +
				" INNER 	JOIN debit_transaction ON debit_transaction.id = reg.id " +
				" WHERE 	reg.pending_flag = 'N'" +
				" AND 		reg.effective_date > :start AND reg.effective_date <= :end " +
				" GROUP 	BY reg.register_transaction_type_code ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);

		Map<String, Double> result = Maps.newLinkedHashMap();
		List<Map<String, Object>> transactionMap = jdbcTemplate.queryForList(query, params);
		for (Map<String, Object> row : transactionMap) {
			result.put( (String) row.get("register_transaction_type_code"), ((BigDecimal) row.get("total")).doubleValue());
		}
		return result;
	}

	@Override
	public BigDecimal calculatePendingPaymentSubscriptionSoftwareAndVORFees(Calendar start, Calendar end) {

		List<String> transactions = Lists.newArrayList(RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES);
		transactions.add(RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);

		//Transactions created in that period
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) " +
				" FROM		register_transaction r " +
				" INNER 	JOIN service_transaction st ON st.id = r.id " +
				" WHERE 	r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(transactions, "'"), ",") + ")" +
				" AND 		r.pending_flag = 'Y' " +
				" AND 		r.transaction_date > :start AND r.transaction_date <= :end " +
				" AND 		st.invoiced = true ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculatePendingPaymentProfessionalServiceFees(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) " +
				" FROM		register_transaction r " +
				" INNER 	JOIN service_transaction st ON st.id = r.id " +
				" WHERE 	r.register_transaction_type_code = :addOn " +
				" AND 		r.pending_flag = 'Y' " +
				" AND 		r.transaction_date > :start AND r.transaction_date <= :end " +
				" AND 		st.invoiced = true ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("addOn", RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculatePendingPaymentFastFundReceivables(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(ffrc.amount), 0.00) " +
			" FROM fast_funds_receivable_commitment ffrc " +
			" WHERE ffrc.is_pending = true " +
			" AND ffrc.transaction_date > :start AND ffrc.transaction_date <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculatePendingPaymentAdHocServiceFees(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) " +
				" FROM		register_transaction r " +
				" INNER 	JOIN service_transaction st ON st.id = r.id " +
				" WHERE 	r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.AD_HOC_INVOICES_FEES_TRANSACTION_TYPE_CODES, "'"), ",") + ")" +
				" AND 		r.pending_flag = 'Y' " +
				" AND 		r.transaction_date > :start AND r.transaction_date <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateSubscriptionSoftwareAndVORFeesByIssueDate(Calendar start, Calendar end) {
		List<String> transactions = Lists.newArrayList(RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES);
		transactions.add(RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);

		//Transactions created in that period
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) " +
				" FROM		register_transaction r " +
				" INNER 	JOIN service_transaction st ON st.id = r.id " +
				" WHERE 	r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(transactions, "'"), ",") + ")" +
				" AND 		st.invoiced_on > :start AND st.invoiced_on <= :end " +
				" AND 		st.invoiced = true ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateProfessionalServiceFeesByIssueDate(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) " +
				" FROM		register_transaction r " +
				" INNER 	JOIN service_transaction st ON st.id = r.id " +
				" WHERE 	r.register_transaction_type_code = :subsAddOn " +
				" AND 		st.invoiced_on > :start AND st.invoiced_on <= :end " +
				" AND 		st.invoiced = true ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("subsAddOn", RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateFastFundsReceivablesByIssueDate(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(ffrc.amount), 0.00) " +
			" FROM fast_funds_receivable_commitment ffrc " +
			" WHERE ffrc.transaction_date > :start AND ffrc.transaction_date <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateAdHocServiceFeesByIssueDate(Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(r.amount), 0.00) " +
				" FROM		register_transaction r " +
				" INNER 	JOIN service_transaction st ON st.id = r.id " +
				" WHERE 	r.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.AD_HOC_INVOICES_FEES_TRANSACTION_TYPE_CODES, "'"), ",") + ")" +
				" AND 		st.invoiced_on > :start AND st.invoiced_on <= :end ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal calculateOfflinePayments(Calendar start, Calendar end, boolean subscription, boolean vor) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COALESCE(SUM(amount), 0.00) FROM register_transaction reg ")
			.append(" INNER JOIN work_resource_transaction wt ON wt.id = reg.id")
			.append(" WHERE reg.pending_flag = 'N' ")
			.append(" AND reg.transaction_date > :start AND reg.transaction_date <= :end")
			.append(" AND reg.register_transaction_type_code = :transactionTypeCode")
			.append(" AND wt.account_pricing_type_code = :pricingTypeCode")
			.append(" AND wt.account_service_type_code in (:serviceTypeCodes)");

		MapSqlParameterSource params = new MapSqlParameterSource();
		putStartAndEndInMap(params, start, end);
		params.addValue("transactionTypeCode", RegisterTransactionType.RESOURCE_OFFLINE_WORK_PAYMENT);
		params.addValue("pricingTypeCode", subscription ? AccountPricingType.SUBSCRIPTION_PRICING_TYPE : AccountPricingType.TRANSACTIONAL_PRICING_TYPE);
		params.addValue("serviceTypeCodes", vor ? AccountServiceType.VOR_SERVICE_TYPES : AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES);

		return jdbcTemplate.queryForObject(sql.toString(), params, BigDecimal.class);
	}
}
