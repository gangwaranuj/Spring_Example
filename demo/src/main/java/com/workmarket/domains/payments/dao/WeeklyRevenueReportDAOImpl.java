package com.workmarket.domains.payments.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.account.WeekReportDetail;
import com.workmarket.domains.model.account.WeeklyReportRow;
import com.workmarket.domains.model.account.WeeklyReportRowPagination;
import com.workmarket.domains.model.summary.TimeDimension;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Repository
public class WeeklyRevenueReportDAOImpl implements WeeklyRevenueReportDAO {

	private static final Log logger = LogFactory.getLog(WeeklyRevenueReportDAOImpl.class);

	private final static Integer FINANCIAL_REPORT_PREVIOUS_WEEK_COUNT = 12;
	private final static Integer FINANCIAL_REPORT_PREVIOUS_WEEK_AVERAGE_COMPARISON = 4;

	@Autowired
	@Qualifier("readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public WeekReportDetail getCompanyCurrentWeekRevenueTrend(Long companyId) {

		Calendar toDate = DateUtilities.getCalendarNow();
		Calendar fromDate = DateUtilities.getMidnightTodayRelativeToTimezone(Constants.EST_TIME_ZONE);
		fromDate.add(Calendar.WEEK_OF_YEAR, FINANCIAL_REPORT_PREVIOUS_WEEK_AVERAGE_COMPARISON * -1);
		fromDate = DateUtilities.getCalendarWithFirstDayOfWeek(fromDate);

		MapSqlParameterSource args = new MapSqlParameterSource();
		args.addValue("companyId", companyId);
		args.addValue("previousNumberOfWeeksToCalculateTrend", FINANCIAL_REPORT_PREVIOUS_WEEK_AVERAGE_COMPARISON);
		args.addValue("toDate", toDate);
		args.addValue("fromDate", fromDate);

		String sqlPreviousWeeksTotalRevenue = "SELECT COALESCE(SUM(work_history_summary.buyer_total_cost),0)/ :previousNumberOfWeeksToCalculateTrend AS weeklyAveragePreviousWeeks \n" +
				" FROM 	time_dimension  \n" +
				" INNER JOIN work_history_summary ON work_history_summary.date_id = time_dimension.id \n" +
				" WHERE work_history_summary.work_status_type_code ='paid' \n" +
				" AND 	time_dimension.date BETWEEN :fromDate AND :toDate \n" +
				" AND 	time_dimension.week_of_year BETWEEN WEEK(SYSDATE(), 3) - :previousNumberOfWeeksToCalculateTrend AND WEEK(SYSDATE(), 3) -1 \n" +
				" AND 	work_history_summary.company_id = :companyId";

		BigDecimal weeklyAveragePreviousWeeks = jdbcTemplate.queryForObject(sqlPreviousWeeksTotalRevenue, args, BigDecimal.class);
		BigDecimal dailyRevenueAveragePreviousWeeks = weeklyAveragePreviousWeeks.divide(BigDecimal.valueOf(7), MathContext.DECIMAL32);

		String sqlCurrentWeekTotalRevenue = "SELECT COALESCE(SUM(work_history_summary.buyer_total_cost),0) AS totalThisWeek \n" +
				" FROM 	time_dimension  \n" +
				" INNER JOIN work_history_summary ON work_history_summary.date_id = time_dimension.id \n" +
				" WHERE work_history_summary.work_status_type_code ='paid' \n" +
				" AND 	time_dimension.year = YEAR(SYSDATE()) \n" +
				" AND 	time_dimension.week_of_year = WEEK(SYSDATE(),3) \n" +
				" AND 	work_history_summary.company_id = :companyId";

		BigDecimal totalThisWeek = jdbcTemplate.queryForObject(sqlCurrentWeekTotalRevenue, args, BigDecimal.class);
		BigDecimal dailyAverageThisWeek = totalThisWeek.divide(new BigDecimal(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)), RoundingMode.HALF_UP);

		WeekReportDetail detail = new WeekReportDetail();
		detail.setTotalAmount(totalThisWeek);
		detail.setAverage(dailyAverageThisWeek);
		if (dailyRevenueAveragePreviousWeeks.compareTo(dailyAverageThisWeek) < 0) {
			detail.setTrendingUp(true);
		}
		return detail;
	}


	@Override
	public List<WeeklyReportRow> getCompanyWeeklyRevenueReport(WeeklyReportRowPagination pagination) {
		Integer currentYear = DateUtilities.getCalendarNow().get(Calendar.YEAR);
		// Get a map with the weeks that we are going to report on
		Map<Long, Calendar> weeksToReport = getWeeklyRevenueWeekMapToReportOn();

		//The key is the companyId, value is a map where the key is the week number.
		Map<Long, Map<Integer, WeekReportDetail>> accountRegisterMap = Maps.newLinkedHashMap();

		/**
		 * Getting all the companies with the previous week totals to compare with.
		 * The key is the companyId.
		 */
		Map<Long, Map<Integer, BigDecimal>> previousWeekDetail = getWeeklyReportPriorWeeksDetail(pagination);

		// Map to hold the basic company information
		Map<Long, WeeklyReportRow> companyInfoMap = Maps.newLinkedHashMap();

		// Build the SQL query
		SQLBuilder builder = buildCompanyWeeklyRevenueReportSQL(FINANCIAL_REPORT_PREVIOUS_WEEK_COUNT, pagination);
		List<Map<String, Object>> queryResults = jdbcTemplate.queryForList(builder.build(), builder.getParams());

		// Iterate over the results
		for (Map<String, Object> row : queryResults) {
			Long companyId = ((Integer) row.get("companyId")).longValue();
			String companyName = (String) row.get("companyName");
			String companyNumber = (String) row.get("company_number");
			Integer currentWeek = (Integer) row.get("thisWeekNumber");

			Integer week = ((Long) row.get("week_of_year")).intValue();
			BigDecimal weekTotal = (BigDecimal) row.get("weekTotal");
			BigDecimal weekAverage = (BigDecimal) row.get("weekAverage");
			BigDecimal averageCurrentWeek = (BigDecimal) row.get("averageThisWeek");

			if (!accountRegisterMap.containsKey(companyId)) {
				/**
				 * Fill the map for this company with 0.00 for all the weeks up to the current week in case the are no records coming from the DB to compare with
				 */
				Map<Integer, WeekReportDetail> defaultWeekReportDetail = getDefaultWeekReportDetail(week, weeksToReport);
				accountRegisterMap.put(companyId, defaultWeekReportDetail);
			}

			if (!companyInfoMap.containsKey(companyId)) {
				/**
				 * Fill the company map with the company's basic data
				 */
				WeeklyReportRow reportRow = new WeeklyReportRow();
				reportRow.setCompanyId(companyId);
				reportRow.setCurrentWeek(currentWeek);
				reportRow.setYear(currentYear);
				reportRow.setCompanyName(companyName);
				reportRow.setCompanyNumber(companyNumber);
				companyInfoMap.put(companyId, reportRow);
			}

			BigDecimal previous4weeksAverage = getPreviousWeeksAverage(week, previousWeekDetail.get(companyId));

			/**
			 * Set the trendingUp flag to true if needed, by default is false because of 0.0 total
			 */
			WeekReportDetail detail = accountRegisterMap.get(companyId).get(week);
			detail.setAverage(weekAverage);
			detail.setTotalAmount(weekTotal);
			if (!detail.isInitialWeek() && currentWeek > week) {
				if (previous4weeksAverage.compareTo(weekTotal) < 0) {
					detail.setTrendingUp(true);
				}
			}

			/**
			 * The current week average is calculated differently. Suppose today is a Wednesday, the daily average should only account for 4 days.
			 */
			if (week.equals(currentWeek)) {
				previous4weeksAverage = previous4weeksAverage.divide(BigDecimal.valueOf(7), MathContext.DECIMAL32);
				if (previous4weeksAverage.compareTo(averageCurrentWeek) < 0) {
					detail.setTrendingUp(true);
				}
			}
		}
		queryResults.clear();
		return buildFinalWeeklyReportList(accountRegisterMap, companyInfoMap);
	}

	private List<WeeklyReportRow> buildFinalWeeklyReportList(Map<Long, Map<Integer, WeekReportDetail>> accountRegisterMap, Map<Long, WeeklyReportRow> companyInfoMap) {
		List<WeeklyReportRow> weeklyReport = Lists.newArrayListWithExpectedSize(accountRegisterMap.keySet().size());
		/**
		 * Compose the final list based on the 2 maps
		 */
		for (Map.Entry<Long, Map<Integer, WeekReportDetail>> entry : accountRegisterMap.entrySet()) {
			WeeklyReportRow reportRow = companyInfoMap.get(entry.getKey());
			reportRow.setWeekDetail(entry.getValue());
			weeklyReport.add(reportRow);
		}

		accountRegisterMap.clear();
		companyInfoMap.clear();
		return weeklyReport;
	}


	/**
	 *
	 * @return A map where the Key is the week of the year number and the value,
	 * the date representing the beginning of that week.
	 */
	private Map<Long, Calendar> getWeeklyRevenueWeekMapToReportOn() {
		Calendar toDate = DateUtilities.getCalendarNow();
		Calendar fromDate = DateUtilities.getMidnightTodayRelativeToTimezone(Constants.EST_TIME_ZONE);
		fromDate.add(Calendar.WEEK_OF_YEAR, FINANCIAL_REPORT_PREVIOUS_WEEK_COUNT * -1);
		fromDate = DateUtilities.getCalendarWithFirstDayOfWeek(fromDate);

		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("MIN(offsetTable.date) as displayDate", "offsetTable.week_of_year")
				.addTable("time_dimension")
				.addJoin("INNER JOIN " + TimeDimension.getOffsetTableForESTTimeZone() + " offsetTable ON offsetTable.id = time_dimension.id ")
				.addWhereClause("time_dimension.date BETWEEN :fromDate AND :toDate")
				.addGroupColumns("offsetTable.week_of_year")
				.addDescOrderBy("displayDate")
				.addParam("toDate", toDate)
				.addParam("fromDate", fromDate);

		Map<Long, Calendar> weeks = Maps.newLinkedHashMap();

		List<Map<String, Object>> timeDimensionData = jdbcTemplate.queryForList(builder.build(), builder.getParams());

		for (Map<String, Object> row : timeDimensionData) {
			Calendar date = DateUtilities.getCalendarFromDate((Timestamp)(row.get("displayDate")));
			if (date != null) {
				date = DateUtilities.newCalendar(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0, Constants.EST_TIME_ZONE);
			}
			Long week = (Long) row.get("week_of_year");
			weeks.put(week, date);
		}
		return weeks;
	}

	private Map<Integer, WeekReportDetail> getDefaultWeekReportDetail(Integer firstWeek, Map<Long, Calendar> weeks) {
		Map<Integer, WeekReportDetail> detailMap = Maps.newLinkedHashMap();

		for (Map.Entry<Long, Calendar> entry : weeks.entrySet()) {
			Long week = entry.getKey();
			WeekReportDetail detail = new WeekReportDetail();
			detail.setWeekStartDate((Calendar)weeks.get(week).clone());
			detail.setInitialWeek(firstWeek.equals(week.intValue()));
			detailMap.put(week.intValue(), detail);
		}
		return detailMap;
	}

	private Map<Long, Map<Integer, BigDecimal>> getWeeklyReportPriorWeeksDetail(WeeklyReportRowPagination pagination) {
		Map<Long, Map<Integer, BigDecimal>> weekTotalsMap = Maps.newLinkedHashMap();

		SQLBuilder builder = buildCompanyWeeklyRevenueReportSQL(FINANCIAL_REPORT_PREVIOUS_WEEK_COUNT + FINANCIAL_REPORT_PREVIOUS_WEEK_AVERAGE_COMPARISON, pagination);

		List<Map<String, Object>> queryResults = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row : queryResults) {
			Long companyId = ((Integer) row.get("companyId")).longValue();
			Integer week = ((Long) row.get("week_of_year")).intValue();
			BigDecimal weekTotal = (BigDecimal) row.get("weekTotal");

			if (!weekTotalsMap.containsKey(companyId)) {
				/**
				 * Fill the map for this company with 0.00 for all the weeks up to the current week in case the are no records coming from the DB to compare with
				 */
				Map<Integer, BigDecimal> weekMap = Maps.newHashMap();
				weekTotalsMap.put(companyId, weekMap);
			}

			weekTotalsMap.get(companyId).put(week, weekTotal);
		}
		return weekTotalsMap;
	}

	private static BigDecimal getPreviousWeeksAverage(Integer weekToCalculate, Map<Integer, BigDecimal> previousWeekDetail) {
		BigDecimal total = BigDecimal.ZERO;
		if (previousWeekDetail != null) {
			for (int i = weekToCalculate - FINANCIAL_REPORT_PREVIOUS_WEEK_AVERAGE_COMPARISON; i < weekToCalculate; i++) {
				if (previousWeekDetail.containsKey(i)) {
					total = total.add(previousWeekDetail.get(i));
				}
			}
			total = total.divide(BigDecimal.valueOf(FINANCIAL_REPORT_PREVIOUS_WEEK_AVERAGE_COMPARISON));
		}
		return total;
	}

	private SQLBuilder buildCompanyWeeklyRevenueReportSQL(Integer numberOfWeeks, WeeklyReportRowPagination pagination) {
		Calendar toDate = DateUtilities.getCalendarNow();
		Calendar fromDate = DateUtilities.getMidnightTodayRelativeToTimezone(Constants.EST_TIME_ZONE);
		String column_filter = "0";
		if (!pagination.getSortColumn().equals(WeeklyReportRowPagination.SORTS.COMPANY_NAME.toString())) {
			column_filter =  pagination.getSortColumn();
		}
		fromDate.add(Calendar.WEEK_OF_YEAR, numberOfWeeks * -1);
		fromDate = DateUtilities.getCalendarWithFirstDayOfWeek(fromDate);

		DateUtilities.formatDateForEmail(toDate);
		DateUtilities.formatDateForEmail(fromDate);
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("MIN(offsetTable.date) as displayDate", "offsetTable.year", "offsetTable.week_of_year",
				"c.id AS companyId", "c.effective_name AS companyName",
				"c.company_number",
				"COALESCE(sum(work_history_summary.buyer_total_cost),0) as weekTotal",
				"COALESCE(sum(work_history_summary.buyer_total_cost),0)/7 as weekAverage",
				"WEEK(SYSDATE(),3) AS thisWeekNumber",
				"ABS(COALESCE(toDate.totalThisWeek, 0)) AS totalThisWeek",
				"ABS(COALESCE(toDate.totalThisWeek, 0)/DAYOFWEEK(SYSDATE())) AS averageThisWeek",
				"ABS(COALESCE(lastWeek.totalLastWeek, 0)) AS totalLastWeek")

				.addTable("company c")
				.addJoin("INNER JOIN work_history_summary ON c.id = work_history_summary.company_id")
				.addJoin("INNER JOIN time_dimension ON work_history_summary.date_id = time_dimension.id")
				.addJoin("INNER JOIN " + TimeDimension.getOffsetTableForESTTimeZone() + " offsetTable ON offsetTable.id = time_dimension.id ");

		builder.addJoin("LEFT JOIN ( \n" +
				" SELECT work_history_summary.company_id, COALESCE(SUM(work_history_summary.buyer_total_cost),0) AS totalThisWeek \n" +
				" FROM 	time_dimension \n" +
				" INNER JOIN work_history_summary ON work_history_summary.date_id = time_dimension.id \n" +
				" WHERE work_history_summary.work_status_type_code ='paid' \n" +
				" AND 	time_dimension.date BETWEEN :fromDate AND :toDate \n" +
				" AND 	time_dimension.week_of_year = WEEK(SYSDATE(),3) \n" +
				" GROUP BY work_history_summary.company_id ORDER BY NULL) AS toDate  \n" +
				" ON 	toDate.company_id = work_history_summary.company_id \n")

				// Total last week is used only for sorting
				.addJoin("LEFT JOIN ( \n" +
						" SELECT work_history_summary.company_id, COALESCE(SUM(work_history_summary.buyer_total_cost),0) AS totalLastWeek \n" +
						" FROM 	time_dimension \n" +
						" INNER JOIN work_history_summary ON work_history_summary.date_id = time_dimension.id \n" +
						" WHERE work_history_summary.work_status_type_code ='paid' \n" +
						" AND 	time_dimension.date BETWEEN :fromDate AND :toDate \n" +
						" AND 	time_dimension.week_of_year = WEEK(SYSDATE(),3) -:weekFilter \n" +
						" GROUP BY work_history_summary.company_id ORDER BY NULL) AS lastWeek  \n" +
						" ON 	lastWeek.company_id = work_history_summary.company_id \n")

				.addWhereClause("time_dimension.date BETWEEN :fromDate AND :toDate")
				.addWhereClause("work_history_summary.work_status_type_code ='paid'")
				.addGroupColumns("work_history_summary.company_id", "offsetTable.year", "offsetTable.week_of_year")
				.addParam("weekFilter", column_filter)
				.addParam("toDate", toDate)
				.addParam("fromDate", fromDate);


    if (pagination.getSortColumn().equals(WeeklyReportRowPagination.SORTS.COMPANY_NAME.toString())) {
			builder.addOrderBy(WeeklyReportRowPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else {
			builder.addDescOrderBy("totalLastWeek");
		}

		builder.addAscOrderBy("offsetTable.year")
				.addAscOrderBy("offsetTable.week_of_year");
		return builder;
	}
}
