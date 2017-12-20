package com.workmarket.service.business.scheduler;

import com.google.common.collect.Sets;
import com.workmarket.service.business.CompanyService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@ManagedResource(objectName="bean:name=summaryExecutor", description="kickoff summary operations")
public class SummaryExecutor {

	private static final Log logger = LogFactory.getLog(SummaryExecutor.class);

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired private SummaryService summaryService;
	@Autowired private CompanyService companyService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private GroupSearchService groupSearchService;

	public void updateCompanySummary() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		logger.info("****** Running updateCompanySummaryStats at " + new Date());
		Calendar today = DateUtilities.getMidnightToday();

		Set<Long> updateCompanyList = Sets.newHashSet();
		updateCompanyList.addAll(companyService.findAllCompaniesWithWorkPayment(today));
		updateCompanyList.addAll(companyService.findAllCompaniesWithWorkCancellations(today));
		for (Long id : updateCompanyList) {
			summaryService.updateCompanySummary(id);
		}
	}

	/**
	 * Update UserGroup statistics.
	 */
	@ManagedOperation(description="kickoff UserGroupSummary update.")
	public void updateUserGroupSummary() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID); // define user doing action

		logger.info("****** Running updateUserGroupSummaryStats at " + new Date());
		List<Long> ids = summaryService.updateUserGroupSummary();

		logger.info("****** Reindexing " + ids.size() + " Ids.");
		if (CollectionUtils.isNotEmpty(ids)) {
			groupSearchService.reindexGroups(ids);
		}
	}

	public void createAccountRegisterDailySnapshot() {
		logger.info("****** Running createAccountRegisterSnapshot at " + new Date());

		Long dateId = summaryService.findTimeDimensionId(DateUtilities.getMidnightToday());

		if (dateId != null) {
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("dateId", dateId);
			String sql = "SELECT count(id) FROM account_register_daily_snapshot WHERE date_id = :dateId";
			int count = jdbcTemplate.queryForObject(sql, params, Integer.class);

			if (count == 0) {
				sql = "INSERT 	INTO account_register_daily_snapshot " +
						"		(account_register_id, credit,company_id, work_credit_balance_lane2, work_credit_balance_lane3, \n" +
						"		assignment_throughput, assignment_throughput_software, assignment_throughput_vor, \n" +
						"		current_work_fee_percentage, available_cash, general_cash, project_cash, deposited_cash, withdrawable_cash, pending_earned_cash, accounts_payable_balance, \n" +
						"		accounts_receivable_balance,pending_commitments, payment_summation, work_fee_level, ap_limit, date_id, industry_id) \n" +

						"SELECT	account_register.id, credit,company_id, work_credit_balance_lane2, work_credit_balance_lane3 , \n" +
						"account_register.assignment_throughput, account_register.assignment_throughput_software, account_register.assignment_throughput_vor, " +
						"current_work_fee_percentage, available_cash, general_cash, project_cash, deposited_cash, withdrawable_cash, pending_earned_cash, accounts_payable_balance, \n" +
						"accounts_receivable_balance,pending_commitments, payment_summation, work_fee_level, ap_limit, :dateId,  \n" +
						"(SELECT industry_id FROM company_industry WHERE company_industry.company_id = company.id LIMIT 1) \n" +
						"FROM   account_register \n" +
						"INNER 	JOIN company \n" +
						"ON 	company.id = account_register.company_id";

				Integer records = jdbcTemplate.update(sql, params);
				logger.info("****** " + records + " records inserted");
			} else {
				logger.info("Summary for date id : " + dateId + " already exists");
			}
		} else {
			logger.info("Unable to find date id for " + DateUtilities.formatDateForEmail(DateUtilities.getMidnightToday()));
		}
	}

	public void createAccountRegisterMonthlySnapshot() {
		logger.info("****** Running createAccountRegisterMonthlySnapshot at " + new Date());

		Long dateId = summaryService.findTimeDimensionId(DateUtilities.getMidnightToday());

		if (dateId != null) {
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("dateId", dateId);
			String sql = "SELECT count(id) FROM account_register_monthly_snapshot WHERE date_id = :dateId";
			int count = jdbcTemplate.queryForObject(sql, params, Integer.class);

			if (count == 0) {
				sql = "INSERT 	INTO account_register_monthly_snapshot " +
						"		(account_register_id, credit,company_id, work_credit_balance_lane2, work_credit_balance_lane3, \n" +
						"		assignment_throughput, assignment_throughput_software, assignment_throughput_vor, \n" +
						"		current_work_fee_percentage, available_cash, general_cash, project_cash, deposited_cash, withdrawable_cash, pending_earned_cash, accounts_payable_balance, \n" +
						"		accounts_receivable_balance,pending_commitments, payment_summation, work_fee_level, ap_limit, date_id, industry_id) \n" +

						"SELECT	account_register.id, credit,company_id, work_credit_balance_lane2, work_credit_balance_lane3 , \n" +
						"account_register.assignment_throughput, account_register.assignment_throughput_software, account_register.assignment_throughput_vor, " +
						"current_work_fee_percentage, available_cash, general_cash, project_cash, deposited_cash, withdrawable_cash, pending_earned_cash, accounts_payable_balance, \n" +
						"accounts_receivable_balance,pending_commitments, payment_summation, work_fee_level, ap_limit, :dateId,  \n" +
						"(SELECT industry_id FROM company_industry WHERE company_industry.company_id = company.id LIMIT 1) \n" +
						"FROM   account_register \n" +
						"INNER 	JOIN company \n" +
						"ON 	company.id = account_register.company_id";

				Integer records = jdbcTemplate.update(sql, params);
				logger.info("****** " + records + " records inserted");
			} else {
				logger.info("Summary for date id : " + dateId + " already exists");
			}
		} else {
			logger.info("Unable to find date id for " + DateUtilities.formatDateForEmail(DateUtilities.getMidnightToday()));
		}
	}

	/**
	 * Creates an aggregate by hour of the register transactions per company and type
	 * based on the last record found on the register_transaction_history_summary table.
	 * <p/>
	 * Assumes that if there's a record for a particular date (including hour of day)
	 * all the transactions that happened between the minute 0 to the minute 59
	 * of that particular hour are already included on the summary.
	 * <p/>
	 * Used to calculate TOTAL_FUNDING_BY_COMPANY Kpi
	 */
	public void createHourlyRegisterTransactionHistorySummary() {
		logger.info("****** Running createHourlyRegisterTransactionHistorySummary at " + new Date());

		String sql = "SELECT MAX(time_dimension.date) " +
				" FROM 	register_transaction_history_summary \n" +
				" INNER JOIN time_dimension " +
				" ON 	time_dimension.id = register_transaction_history_summary.date_id ";

		Calendar lastSummary = DateUtilities.getCalendarFromDate(jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Timestamp.class));
		if (lastSummary != null) {
			logger.info("****** Last hourly RegisterTransactionHistorySummary found: " + DateUtilities.formatDateForEmail(lastSummary));
			Calendar now = Calendar.getInstance();
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			logger.info("****** Now: " + DateUtilities.formatDateForEmail(now));

			Integer hoursBetween = DateUtilities.getHoursBetween(lastSummary, now);
			logger.info("****** hoursBetween: " + hoursBetween);

			if (hoursBetween > 0) {

				for (int i = 1; i <= hoursBetween; i++) {
					lastSummary.add(Calendar.HOUR_OF_DAY, 1);
					logger.info("****** Taking hourly summary for date: " + DateUtilities.formatDateForEmail(lastSummary));

					Long dateId = summaryService.findTimeDimensionId(lastSummary);
					logger.info("****** Date id: " + dateId + " for date: " + DateUtilities.formatDateForEmail(lastSummary));

					String insertSQL = "INSERT INTO register_transaction_history_summary " +
							" 		(date_id, account_register_id, company_id, register_transaction_type_code, sum_amount, created_on) " +
							" SELECT :dateId, rt.account_register_id, r.company_id, rt.register_transaction_type_code, SUM(rt.amount), SYSDATE() " +
							" FROM 	register_transaction rt " +
							" INNER JOIN account_register r ON r.id = rt.account_register_id " +
							" LEFT 	JOIN bank_account_transaction ON bank_account_transaction.id = rt.id " +
							" WHERE rt.effective_date >= :fromDate " +
							" AND	rt.effective_date < :toDate " +
							" AND 	rt.pending_flag = 'N' " +
							" AND COALESCE(bank_account_transaction.bank_account_transaction_status_code, 'empty')  IN ('empty', 'processed') " +
							" GROUP BY account_register_id, register_transaction_type_code ";

					MapSqlParameterSource params = new MapSqlParameterSource();
					params.addValue("dateId", dateId);
					params.addValue("fromDate", lastSummary);

					Calendar newHourlySummaryDate = (Calendar) lastSummary.clone();
					newHourlySummaryDate.add(Calendar.HOUR_OF_DAY, 1);
					params.addValue("toDate", newHourlySummaryDate);

					logger.info("****** Querying from date : " + DateUtilities.formatDateForEmail(lastSummary));
					logger.info("****** Querying to date : " + DateUtilities.formatDateForEmail(newHourlySummaryDate));

					Integer records = jdbcTemplate.update(insertSQL, params);
					logger.info("****** " + records + " records inserted");
				}
			}
		}
	}

	public void updateFactMonthlyWork() {
		Calendar today = Calendar.getInstance();
		//We always want to query a full month in business hours. Example from Oct 1, 04:00 to Nov 1, 04:00 EST
		Calendar firstDayOfTheCurrentMonth = DateUtilities.getCalendarWithFirstDayOfTheMonth(today, Constants.EST_TIME_ZONE);
		firstDayOfTheCurrentMonth.set(Calendar.DAY_OF_MONTH, 1);
		Calendar firstDayOfTheNextMonth = DateUtilities.cloneCalendar(firstDayOfTheCurrentMonth);
		firstDayOfTheNextMonth.add(Calendar.MONTH, 1);

		String sql = "SELECT MAX(time_dimension.date) " +
				" FROM         fact_monthly_paid_work \n" +
				" INNER JOIN time_dimension " +
				" ON         time_dimension.id = fact_monthly_paid_work.date_id ";

		String insertSQL = "INSERT  INTO  fact_monthly_paid_work (date_id, company_id, industry_id, sum_work_price, sum_buyer_fee, payment_terms_enabled,\n" +
				"              sum_buyer_total_cost, account_pricing_type_code, account_service_type_code, count_work, work_status_type_code) \n" +
				"SELECT        MAX(offsetTable.id) dateId, company_id, industry_id,\n" +
				"              COALESCE(SUM(work_history_summary.work_price),0),\n" +
				"              COALESCE(SUM(work_history_summary.buyer_fee),0), payment_terms_enabled,\n" +
				"              COALESCE(SUM(work_history_summary.buyer_total_cost),0),\n" +
				"              account_pricing_type_code, account_service_type_code, count(DISTINCT work_id), work_status_type_code\n" +
				"FROM          time_dimension_offset_4hours offsetTable\n" +
				"LEFT          JOIN work_history_summary ON offsetTable.id = work_history_summary.date_id  AND work_history_summary.work_status_type_code = 'paid'\n" +
				"WHERE         company_id IS NOT NULL\n" +
				"AND           date >= :fromDate AND date < :toDate\n" +
				"GROUP         BY offsetTable.year, offsetTable.month_of_year, work_id, company_id, industry_id\n" +
				"ORDER         BY dateId ASC;";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fromDate", firstDayOfTheCurrentMonth);

		//Check the last record.
		Calendar lastSummary = DateUtilities.getCalendarFromDate(jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Timestamp.class));
		logger.info("****** Last fact_monthly_paid_work found: " + DateUtilities.formatDateForEmail(lastSummary));
		//If there are already records for the current month, delete them, in order to insert the more updated ones.

		String deleteSQL = "DELETE FROM fact_monthly_paid_work " +
				" WHERE EXISTS (SELECT time_dimension.id FROM time_dimension " +
				" WHERE fact_monthly_paid_work.date_id = time_dimension.id " +
				" AND         time_dimension.date >= :fromDate)";

		jdbcTemplate.update(deleteSQL, params);
		//Either way execute the insert.
		params.addValue("toDate", firstDayOfTheNextMonth);
		jdbcTemplate.update(insertSQL, params);

		sql = "SELECT MAX(time_dimension.date) " +
				" FROM         fact_monthly_draft_work \n" +
				" INNER JOIN time_dimension " +
				" ON         time_dimension.id = fact_monthly_draft_work.date_id ";

		insertSQL = "INSERT  INTO  fact_monthly_draft_work (date_id, company_id, industry_id, payment_terms_enabled,\n" +
				"              account_pricing_type_code, account_service_type_code, count_work, work_status_type_code)\n" +
				"SELECT        MAX(offsetTable.id) dateId, company_id, industry_id, payment_terms_enabled,\n" +
				"              account_pricing_type_code, account_service_type_code, count(DISTINCT work_id), work_status_type_code\n" +
				"FROM          time_dimension_offset_4hours offsetTable\n" +
				"LEFT          JOIN work_history_summary ON offsetTable.id = work_history_summary.date_id  AND work_history_summary.work_status_type_code = 'draft'\n" +
				"WHERE         company_id IS NOT NULL\n" +
				"AND           date >= :fromDate AND date < :toDate \n" +
				"GROUP         BY offsetTable.year, offsetTable.month_of_year, work_id, company_id, industry_id\n" +
				"ORDER         BY dateId ASC;";

		params.addValue("fromDate", firstDayOfTheCurrentMonth);

		//Check the last record.
		lastSummary = DateUtilities.getCalendarFromDate(jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Timestamp.class));

		logger.info("****** Last fact_monthly_draft_work found: " + DateUtilities.formatDateForEmail(lastSummary));
		//If there are already records for the current month, delete them, in order to insert the more updated ones.

		deleteSQL = "DELETE FROM fact_monthly_draft_work " +
				" WHERE EXISTS (SELECT time_dimension.id FROM time_dimension " +
				" WHERE fact_monthly_draft_work.date_id = time_dimension.id " +
				" AND         time_dimension.date >= :fromDate)";
		jdbcTemplate.update(deleteSQL, params);

		//Either way execute the insert.
		params.addValue("toDate", firstDayOfTheNextMonth);
		jdbcTemplate.update(insertSQL, params);
	}
}
