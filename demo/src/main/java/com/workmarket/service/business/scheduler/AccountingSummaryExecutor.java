package com.workmarket.service.business.scheduler;

import com.workmarket.domains.model.account.AccountingPricingServiceTypeSummary;
import com.workmarket.domains.model.account.AccountingSummary;
import com.workmarket.service.business.account.JournalEntrySummaryService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

@Service
public class AccountingSummaryExecutor implements ScheduledExecutor {

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired AuthenticationService authenticationService;
	@Autowired JournalEntrySummaryService journalEntrySummaryService;

	public void execute() {
		String sql = "SELECT id, previous_request_date, request_date  " +
				"FROM accounting_summary " +
				"WHERE id >= 427";

		String insertSQL = "INSERT INTO revenue_accounting_summary (id, previous_request_date, request_date, revenue_subscription_vor_software_fee, revenue_subscription_vor_vor_fee, " +
				" revenue_subscription_non_vor_software_fee, revenue_subscription_vor_software_fee_historical, revenue_subscription_vor_vor_fee_historical, " +
				" revenue_subscription_non_vor_software_fee_historical, " +
				" def_revenue_subscription_vor_software_fee, 	def_revenue_subscription_vor_vor_fee, def_revenue_subscription_non_vor_software_fee, " +
				" def_revenue_subscription_vor_software_fee_historical, def_revenue_subscription_vor_vor_fee_historical, def_revenue_subscription_non_vor_software_fee_historical) " +
				" VALUES (:id, :pRequestDate, :requestDate, :revenue_subscription_vor_software_fee, :revenue_subscription_vor_vor_fee, :revenue_subscription_non_vor_software_fee, "+
				" :revenue_subscription_vor_software_fee_historical, :revenue_subscription_vor_vor_fee_historical, :revenue_subscription_non_vor_software_fee_historical, " +
				" :def_revenue_subscription_vor_software_fee, :def_revenue_subscription_vor_vor_fee, :def_revenue_subscription_non_vor_software_fee, :def_revenue_subscription_vor_software_fee_historical, " +
				" :def_revenue_subscription_vor_vor_fee_historical, :def_revenue_subscription_non_vor_software_fee_historical)";

		authenticationService.setCurrentUser(1L);
		List<AccountingSummary> accountingSummaryList = jdbcTemplate.query(sql, new MapSqlParameterSource(), new RowMapper<AccountingSummary>() {

			@Override
			public AccountingSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
				AccountingSummary row = new AccountingSummary();
				row.setId(rs.getLong("id"));
				row.setPreviousRequestDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("previous_request_date")));
				row.setRequestDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("request_date")));
				return row;
			}
		});

		for (AccountingSummary accountingSummary : accountingSummaryList) {
			AccountingSummary summary = reCalculateSummary(accountingSummary);
			if (summary != null) {
				MapSqlParameterSource params = new MapSqlParameterSource();
				params.addValue("id", accountingSummary.getId());
				params.addValue("pRequestDate", accountingSummary.getPreviousRequestDate());
				params.addValue("requestDate", accountingSummary.getRequestDate());
				params.addValue("revenue_subscription_vor_software_fee", summary.getRevenueSubscriptionVorSoftwareFee());
				params.addValue("revenue_subscription_vor_vor_fee", summary.getRevenueSubscriptionVorSoftwareFee());
				params.addValue("revenue_subscription_non_vor_software_fee", summary.getRevenueSubscriptionNonVorSoftwareFee());
				params.addValue("revenue_subscription_vor_software_fee_historical", summary.getRevenueSubscriptionVorSoftwareFeeHistorical());
				params.addValue("revenue_subscription_vor_vor_fee_historical", summary.getRevenueSubscriptionVorSoftwareFeeHistorical());
				params.addValue("revenue_subscription_non_vor_software_fee_historical", summary.getRevenueSubscriptionNonVorSoftwareFeeHistorical());
				params.addValue("def_revenue_subscription_vor_software_fee", summary.getDefRevenueSubscriptionVorSoftwareFee());
				params.addValue("def_revenue_subscription_vor_vor_fee", summary.getDefRevenueSubscriptionVorVorFee());
				params.addValue("def_revenue_subscription_non_vor_software_fee", summary.getDefRevenueSubscriptionNonVorSoftwareFee());
				params.addValue("def_revenue_subscription_vor_software_fee_historical", summary.getDefRevenueSubscriptionVorSoftwareFeeHistorical());
				params.addValue("def_revenue_subscription_vor_vor_fee_historical", summary.getDefRevenueSubscriptionVorVorFeeHistorical());
				params.addValue("def_revenue_subscription_non_vor_software_fee_historical", summary.getDefRevenueSubscriptionNonVorSoftwareFeeHistorical());
				jdbcTemplate.update(insertSQL, params);
			}
		}
	}


	private AccountingSummary reCalculateSummary(AccountingSummary accountingSummary) {
		Calendar startFiscalYear = journalEntrySummaryService.findOrCreateStartFiscalYearForDate(accountingSummary.getRequestDate());
		Calendar start = DateUtilities.cloneCalendar(accountingSummary.getPreviousRequestDate());
		Calendar end = DateUtilities.cloneCalendar(accountingSummary.getRequestDate());

		AccountingSummary summary = new AccountingSummary();
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummary = new AccountingPricingServiceTypeSummary();
		//Revenue
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(accountingPricingServiceTypeSummary, start, end, startFiscalYear);
		//Def Revenue
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryDeferredRevenueValues(accountingPricingServiceTypeSummary, start, end);

		summary.setAccountingPricingServiceTypeSummary(accountingPricingServiceTypeSummary);
		return summary;
	}


}