package com.workmarket.dao.report.subscription;

import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionStatusType;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionAggregate;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionReportPagination;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionReportRow;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.sql.SQLBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class SubscriptionReportDAOImpl implements SubscriptionReportDAO {

	@Value("${wm_marketcore.schema}")
	private String WM_MARKETCORE_SCHEMA;

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	private static final class SubscriptionReportRowMapper implements RowMapper<SubscriptionReportRow> {

		@Override
		public SubscriptionReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			SubscriptionReportRow row = new SubscriptionReportRow();

			row.setSubscriptionConfigurationId(rs.getLong("subscriptionId"));
			row.setCompanyId(rs.getLong("companyId"));
			row.setCompanyName(rs.getString("companyName"));
			row.setCompanyAccountManager(rs.getString("companyAccountManager"));
			row.setCurrentAnnualThroughput(rs.getBigDecimal("currentAnnualThroughput"));
			row.setCurrentTierUpperBoundThroughput(rs.getBigDecimal("currentTierMaximum"));
			row.setCurrentTierLowerBoundThroughput(rs.getBigDecimal("currentTierMinimum"));
			row.setCurrentTierPaymentAmount(rs.getBigDecimal("currentTierPaymentAmount"));
			row.setCurrentTierVORAmount(rs.getBigDecimal("currentTierVORAmount"));
			row.setEffectiveDate(DateUtilities.getCalendarFromDate(rs.getDate("effectiveDate")));
			row.setSignedDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("signedDate")));
			row.setNextTierPaymentAmount(rs.getBigDecimal("nextTierPaymentAmount"));
			row.setNextTierVORAmount(rs.getBigDecimal("nextTierVORAmount"));
			row.setNumberOfRenewals(rs.getInt("numberOfRenewals"));
			row.setPaymentPeriod(rs.getString("paymentPeriod"));
			row.setRenewalDate(DateUtilities.getCalendarFromDate(rs.getDate("renewalDate")));
			row.setTermsUsed(rs.getBigDecimal("termsUsed"));
			row.setVendorOfRecord(rs.getBoolean("vendorOfRecord"));
			row.setTermsInMonths(rs.getInt("termsInMonths"));
			row.setInvoicePastDue(rs.getBigDecimal("pastDue"));
			row.setInvoicesComingDue(rs.getBigDecimal("comingDue"));
			BigDecimal totalPaidAssignments = NumberUtilities.defaultValue(rs.getBigDecimal("total_paid_assignments"));
			if (NumberUtilities.isPositive(totalPaidAssignments)) {
				BigDecimal latePaidAssignments = NumberUtilities.defaultValue(rs.getBigDecimal("total_late_paid_assignments"));
				row.setOnTimePaymentPercentage(BigDecimal.valueOf(100).subtract(NumberUtilities.rate(latePaidAssignments, BigDecimal.valueOf(100), totalPaidAssignments)).setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			return row;
		}
	}

	private static final class SubscriptionAggregateRowMapper implements RowMapper<SubscriptionAggregate> {

		@Override
		public SubscriptionAggregate mapRow(ResultSet rs, int rowNum) throws SQLException {
			SubscriptionAggregate row = new SubscriptionAggregate();

			row.setTotalCompanies(rs.getInt("totalCompanies"));
			row.setSumOfAnnualRecurringRevenue(rs.getBigDecimal("arr"));
			row.setSumOfMonthlyRecurringRevenue(rs.getBigDecimal("monthlyPaymentAmount"));
			row.setSumOfTerms(rs.getBigDecimal("termsUsed"));
			row.setTotalVorCompanies(rs.getInt("totalVendorOfRecord"));
			return row;
		}
	}

	@Override
	public SubscriptionReportPagination getStandardReport(SubscriptionReportPagination pagination) {
		Assert.notNull(pagination);
		SQLBuilder builder = buildSubscripitonReportSQLBuilder();
		builder.addColumn(" 0 AS comingDue");
		builder.addColumn(" 0 AS pastDue");

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		pagination.setResults(jdbcTemplate.query(builder.build(), builder.getParams(), new SubscriptionReportRowMapper()));
		pagination.setRowCount(jdbcTemplate.queryForObject(builder.buildCount(), builder.getParams(), Integer.class));

		return pagination;
	}

	@Override
	public SubscriptionReportPagination getUsageReport(SubscriptionReportPagination pagination) {
		Assert.notNull(pagination);
		SQLBuilder builder = buildSubscripitonReportSQLBuilder();

		builder.addColumn(" (SELECT COALESCE(SUM(balance),0) AS balance \n" +
				" FROM 	invoice \n" +
				" WHERE invoice.deleted = 0 AND invoice.invoice_status_type_code = :invoiceStatusType \n" +
				" AND 	invoice.due_date >= :today \n" +
				" AND 	invoice.bundled = false \n" +
				" AND 	invoice.type IN (:bundle, :adHoc, :subscription, :invoice) AND invoice.company_id = company.id) AS comingDue");

		builder.addColumn(" (SELECT COALESCE(SUM(balance),0) AS balance \n" +
				" FROM 	invoice \n" +
				" WHERE invoice.deleted = 0 AND invoice.invoice_status_type_code = :invoiceStatusType \n" +
				" AND 	invoice.due_date <= :today \n" +
				" AND 	invoice.bundled = false \n" +
				" AND 	invoice.type IN (:bundle, :adHoc, :subscription, :invoice) AND invoice.company_id = company.id) AS pastDue");

		builder.addParam("today", DateUtilities.formatTodayForSQL())
				.addParam("bundle", InvoiceSummary.INVOICE_SUMMARY_TYPE)
				.addParam("adHoc", AdHocInvoice.AD_HOC_INVOICE_TYPE)
				.addParam("subscription", SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE)
				.addParam("invoice", Invoice.INVOICE_TYPE)
				.addParam("invoiceStatusType", InvoiceStatusType.PAYMENT_PENDING);

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		pagination.setResults(jdbcTemplate.query(builder.build(), builder.getParams(), new SubscriptionReportRowMapper()));
		pagination.setRowCount(jdbcTemplate.queryForObject(builder.buildCount(), builder.getParams(), Integer.class));
		return pagination;
	}

	private SQLBuilder buildSubscripitonReportSQLBuilder() {
		SQLBuilder builder = new SQLBuilder();
		builder.setDistinct(true);

		String[] columns = new String[] {
				" subscription.id AS subscriptionId",
				" company.id AS companyId",
				" company.effective_name AS companyName",
				" 'NA' AS companyAccountManager",
				" subscription.subscription_period AS paymentPeriod",
				" subscription.effective_date AS effectiveDate",
				" subscription.signed_date AS signedDate",
				" CASE subscription.subscription_period\n" +
						"	WHEN 'QUARTERLY'	THEN 3 * subscription.number_of_periods\n" +
						" 	WHEN 'MONTHLY'		THEN subscription.number_of_periods\n" +
						" 	WHEN 'SEMIANNUAL'	THEN 6 * subscription.number_of_periods\n" +
						" 	WHEN 'ANNUAL' 		THEN 12 * subscription.number_of_periods\n" +
						" 	ELSE NULL END as termsInMonths ",
				" subscription.number_of_renewals AS numberOfRenewals",
				" IF(subscription.number_of_renewals > 0, subscription.end_date, NULL) AS renewalDate",

				" (EXISTS (SELECT id FROM subscription_account_service_type_configuration astc " +
						" WHERE astc.subscription_configuration_id = subscription.id " +
						" AND astc.account_service_type_code = 'vor')) AS vendorOfRecord ",

				" currentSWTier.minimum AS currentTierMinimum",
				" currentSWTier.maximum AS currentTierMaximum",
				" COALESCE(currentVORTier.vendor_of_record_amount, 0) AS currentTierVORAmount",
				" COALESCE(currentSWTier.payment_amount, 0) AS currentTierPaymentAmount",

				" (SELECT COALESCE(nextVORTier.vendor_of_record_amount, 0) " +
						" FROM subscription_payment_tier nextVORTier " +
						" WHERE nextVORTier.subscription_fee_configuration_id = fee.id AND nextVORTier.subscription_payment_tier_vor_status_type_code = 'not_reached' " +
						" ORDER BY nextVORTier.minimum ASC LIMIT 1)  AS nextTierVORAmount",

				" (SELECT COALESCE(nextSWTier.payment_amount, 0) " +
						" FROM subscription_payment_tier nextSWTier " +
						" WHERE nextSWTier.subscription_fee_configuration_id = fee.id AND nextSWTier.subscription_payment_tier_sw_status_type_code = 'not_reached' " +
						" ORDER BY nextSWTier.minimum ASC LIMIT 1) AS nextTierPaymentAmount",
				" register.accounts_payable_balance AS termsUsed",
				" register.assignment_throughput_software AS currentAnnualThroughput",
				" company_summary.total_paid_assignments",
				" company_summary.total_late_paid_assignments"
		};

		builder.addColumns(columns)
				.addTable(WM_MARKETCORE_SCHEMA + ".subscription_configuration subscription")
				.addJoin("INNER	JOIN " + WM_MARKETCORE_SCHEMA + ".company company ON company.id = subscription.company_id")
				.addJoin("LEFT	JOIN " + WM_MARKETCORE_SCHEMA + ".company_summary company_summary ON company.id = company_summary.company_id")
				.addJoin("INNER JOIN " + WM_MARKETCORE_SCHEMA + ".payment_configuration paymentConfiguration ON paymentConfiguration.id = company.payment_configuration_id")
				.addJoin("INNER	JOIN " + WM_MARKETCORE_SCHEMA + ".account_register register ON company.id = register.company_id")
				.addJoin("INNER JOIN " + WM_MARKETCORE_SCHEMA + ".subscription_fee_configuration fee ON fee.subscription_configuration_id = subscription.id AND fee.active = 1")
				.addJoin("INNER JOIN " + WM_MARKETCORE_SCHEMA + ".subscription_payment_tier currentSWTier ON fee.id = currentSWTier.subscription_fee_configuration_id "
						+ "AND currentSWTier.subscription_payment_tier_sw_status_type_code = 'active'")
				.addJoin("LEFT 	JOIN " + WM_MARKETCORE_SCHEMA + ".subscription_payment_tier currentVORTier ON fee.id = currentVORTier.subscription_fee_configuration_id "
						+ "AND currentVORTier.subscription_payment_tier_vor_status_type_code = 'active'")
				.addWhereClause("subscription.subscription_status_type_code = :active ")
				.addWhereClause("subscription.deleted = false")
				.addParam("active", SubscriptionStatusType.ACTIVE)
				.addParam("subscription", AccountPricingType.SUBSCRIPTION_PRICING_TYPE);
		return builder;
	}

	@Override
	public SubscriptionAggregate getSubscriptionAggregateReport(SubscriptionReportPagination pagination) {
		Assert.notNull(pagination);
		SQLBuilder builder = new SQLBuilder();
		builder.setDistinct(true);

		String[] columns = new String[] {
				" subscription.id AS subscriptionId",
				" subscription.subscription_period AS paymentPeriod",
				" CASE subscription.subscription_period\n" +
						"	WHEN 'QUARTERLY'	THEN 3 \n" +
						" 	WHEN 'MONTHLY'		THEN 1 \n" +
						" 	WHEN 'SEMIANNUAL'	THEN 6 \n" +
						" 	WHEN 'ANNUAL' 		THEN 12 \n" +
						" 	ELSE NULL END as monthsPerPeriod ",
				"  subscription.number_of_periods ",
				" (EXISTS (SELECT id FROM subscription_account_service_type_configuration astc " +
						" WHERE astc.subscription_configuration_id = subscription.id " +
						" AND astc.account_service_type_code = 'vor')) AS vendorOfRecord ",

				" COALESCE(currentVORTier.vendor_of_record_amount, 0) AS currentTierVORAmount",
				" COALESCE(currentSWTier.payment_amount, 0) AS currentTierPaymentAmount",
				" register.accounts_payable_balance AS termsUsed",
		};

		builder.addColumns(columns)
				.addTable(WM_MARKETCORE_SCHEMA + ".subscription_configuration subscription")
				.addJoin("INNER	JOIN " + WM_MARKETCORE_SCHEMA + ".company company ON company.id = subscription.company_id")
				.addJoin("INNER JOIN " + WM_MARKETCORE_SCHEMA + ".payment_configuration paymentConfiguration ON paymentConfiguration.id = company.payment_configuration_id")
				.addJoin("INNER	JOIN " + WM_MARKETCORE_SCHEMA + ".account_register register ON company.id = register.company_id")
				.addJoin("INNER JOIN " + WM_MARKETCORE_SCHEMA + ".subscription_fee_configuration fee ON fee.subscription_configuration_id = subscription.id AND fee.active = 1")
				.addJoin("INNER JOIN " + WM_MARKETCORE_SCHEMA + ".subscription_payment_tier currentSWTier ON fee.id = currentSWTier.subscription_fee_configuration_id "
						+ "AND currentSWTier.subscription_payment_tier_sw_status_type_code = 'active'")
				.addJoin("LEFT 	JOIN " + WM_MARKETCORE_SCHEMA + ".subscription_payment_tier currentVORTier ON fee.id = currentVORTier.subscription_fee_configuration_id "
						+ "AND currentVORTier.subscription_payment_tier_vor_status_type_code = 'active'")
				.addWhereClause("subscription.subscription_status_type_code = :active ")
				.addWhereClause("subscription.deleted = false")
				.addWhereClause("paymentConfiguration.account_pricing_type_code = :subscription ")
				.addParam("active", SubscriptionStatusType.ACTIVE)
				.addParam("subscription", AccountPricingType.SUBSCRIPTION_PRICING_TYPE);

		String sql = "SELECT COUNT(subscriptionId) AS totalCompanies, " +
				" SUM(currentTierPaymentAmount + IF(vendorOfRecord, currentTierVORAmount, 0)) / monthsPerPeriod AS monthlyPaymentAmount, " +
				" SUM(termsUsed) AS termsUsed, " +
				" SUM(vendorOfRecord) AS totalVendorOfRecord, " +
				" COALESCE(IF (monthsPerPeriod * number_of_periods > 12, 12, monthsPerPeriod * number_of_periods) * " +
				"	(SUM(currentTierPaymentAmount + IF(vendorOfRecord, currentTierVORAmount, 0)) / monthsPerPeriod), 0) AS arr " +
				" FROM ( " + builder.build() + ") subscriptions ";

		return jdbcTemplate.query(sql, builder.getParams(), new SubscriptionAggregateRowMapper()).get(0);
	}
}
