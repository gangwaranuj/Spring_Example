package com.workmarket.dao.report.kpi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.report.internal.AssignmentReport;
import com.workmarket.data.report.internal.SnapshotReport;
import com.workmarket.data.report.internal.TopEntity;
import com.workmarket.data.report.internal.TopUser;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIAggregateEntityTable;
import com.workmarket.domains.model.kpi.KPIReportAggregateInterval;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Repository
public class KpiDAOImpl implements KpiDAO {

	private static final Log logger = LogFactory.getLog(KpiDAOImpl.class);

	@Autowired
	@Qualifier("readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	private KpiSqlBuilderFactory kpiSqlBuilderFactory;

	@Value("${wm_marketcore.schema}")
	private String WM_MARKETCORE_SCHEMA;

	private enum WorkMilestonesTransitionColumn {
		CREATED_TO_SENT("created", "sent"),
		SENT_TO_ASSIGNED("sent", "assigned"),
		ASSIGNED_TO_COMPLETE("assigned", "complete"),
		COMPLETE_TO_PAID("complete", "paid"),
		SENT_TO_PAID("sent", "paid"),

		DRAFT_TO_SENT("draft", "sent"),
		SENT_TO_ACTIVE("sent", "active"),
		ACTIVE_TO_COMPLETE("active", "complete"),
		COMPLETE_TO_CLOSED("complete", "closed"),
		CLOSED_TO_PAID("closed", "paid"),
		START_TO_PAID("start", "paid"),
		SENT_TO_START("sent", "start"),
		START_TO_COMPLETE("start", "complete");

		private final String fromString;
		private final String toString;

		WorkMilestonesTransitionColumn(String fromString, String toString) {
			this.fromString = fromString;
			this.toString = toString;
		}

		public String getFrom(){
			return this.fromString;
		}

		public String getTo(){
			return this.toString;
		}
	}

	private enum ForecastReportType {
		THROUGHPUT,
		MONTHLY_ASSIGNMENT_VOLUME
	}

	private static final Integer DEFAULT_DECIMAL_SCALE = 2;
	private static final Integer PERCENTAGE_DECIMAL_SCALE = 4;

	@Override
	public List<DataPoint> amountThroughputActual(KPIRequest kpiRequest) {
		SQLBuilder builder;
		if (KPIReportAggregateInterval.MONTH_OF_YEAR.equals(kpiRequest.getAggregateInterval())) {
			builder = kpiSqlBuilderFactory.buildAssignmentThroughputActualMonthlySQL(kpiRequest);
			builder.addColumn("COALESCE(SUM(fact_monthly_paid_work.sum_work_price),0) AS data");
		}
		else {
			builder = kpiSqlBuilderFactory.buildAssignmentThroughputActualSQL(kpiRequest);
			builder.addColumn("COALESCE(SUM(work_history_summary.work_price),0) AS data");
		}
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> amountFeesActual(KPIRequest kpiRequest) {
		SQLBuilder builder;
		if (KPIReportAggregateInterval.MONTH_OF_YEAR.equals(kpiRequest.getAggregateInterval())) {
			 builder = kpiSqlBuilderFactory.buildAssignmentThroughputActualMonthlySQL(kpiRequest);
			 builder.addColumn("COALESCE(SUM(fact_monthly_paid_work.sum_buyer_fee),0) AS data");
		}
		else {
			builder = kpiSqlBuilderFactory.buildAssignmentThroughputActualSQL(kpiRequest);
			builder.addColumn("COALESCE(SUM(work_history_summary.buyer_fee),0) AS data");
		}
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> percentThroughputChange(KPIRequest kpiRequest) {
		int field = Calendar.YEAR;
		switch (kpiRequest.getAggregateInterval()) {
			case DAY_OF_MONTH:
				field = Calendar.DAY_OF_MONTH;
				break;
			case MONTH_OF_YEAR:
				field = Calendar.MONTH;
				break;
			case WEEK_OF_YEAR:
				field = Calendar.WEEK_OF_YEAR;
				break;
			default:
				// YEAR
				break;
		}
		/***
		 * Query for a period before so we can compare
		 */
		kpiRequest.getFrom().add(field, -1);

		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentThroughputActualSQL(kpiRequest);
		builder.addColumn("COALESCE(SUM(work_history_summary.buyer_total_cost),0) AS data");
		List<DataPoint> throughput = generateDataPoints(builder, false, kpiRequest.getAggregateInterval(), PERCENTAGE_DECIMAL_SCALE);
		List<DataPoint> results = Lists.newArrayListWithExpectedSize(throughput.size());

		int limit = throughput.size() - 1;
		for (int i = 0; i <= limit; i++) {
			BigDecimal previousValue = BigDecimal.valueOf(throughput.get(i).getY());
			BigDecimal currentValue = BigDecimal.ZERO;
			BigDecimal y = BigDecimal.ZERO;
			long x = 0;

			if (i < limit) {
				x = (throughput.get(i + 1).getX());
				currentValue = BigDecimal.valueOf(throughput.get(i + 1).getY());
			}
			if (previousValue.compareTo(BigDecimal.ZERO) > 0) {
				y = currentValue.divide(previousValue, MathContext.DECIMAL32).subtract(BigDecimal.ONE);
			}
			if (x > 0) {
				DataPoint dataPoint = new DataPoint();
				dataPoint.setX(x);
				dataPoint.setY(y.setScale(PERCENTAGE_DECIMAL_SCALE, RoundingMode.HALF_UP).doubleValue());
				results.add(dataPoint);
			}
		}
		throughput.clear();
		return results;
	}

	@Override
	public List<DataPoint> averageFeesEarned(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentThroughputActualSQL(kpiRequest);
		builder.addColumn("COALESCE(COALESCE(SUM(work_history_summary.buyer_fee),0)/COALESCE(SUM(work_history_summary.work_price),0),0) AS data");

		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval(), PERCENTAGE_DECIMAL_SCALE);
	}

	@Override
	public List<DataPoint> amountAvailableCash(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildSumAvailableCashSQL(kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> amountWithdrawableCash(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildSumWithdrawableCashSQL(kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> amountAccountPayableBalance(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildSumAccountPayableBalanceSQL(kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countAssignmentsByStatus(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder;
		if (KPIReportAggregateInterval.MONTH_OF_YEAR.equals(kpiRequest.getAggregateInterval())) {
			if (WorkStatusType.DRAFT.equals(workStatusTypeCode)) {
				builder = kpiSqlBuilderFactory.buildAssignmentHistorySummaryMonthlySQL(workStatusTypeCode, kpiRequest);
				builder.addColumn("COALESCE(SUM(fact_monthly_draft_work.count_work),0) AS data");
				return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
			}
			builder = kpiSqlBuilderFactory.buildAssignmentHistorySummarySQL(workStatusTypeCode, kpiRequest);
			builder.addColumn("COUNT(DISTINCT work_history_summary.work_id) AS data");
			return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
		}

		DataPoint point = new DataPoint();
		point.setX(getMaxESTDisplayDate(kpiRequest));
		point.setY(getAssignmentCountByStatus(workStatusTypeCode, kpiRequest));

		return Lists.newArrayList(point);
	}

	private long getMaxESTDisplayDate(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("MAX(time_dimension.date) AS displayDate");
		builder.addTable("time_dimension");
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");

		Timestamp timestamp = jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Timestamp.class);

		if (timestamp != null) {
			int estTimeZoneOffset = Math.abs(DateUtilities.getOffsetHoursForTimeZone(Constants.EST_TIME_ZONE));
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timestamp.getTime());
			Calendar maxESTDisplayDate = DateUtilities.subtractTime(calendar, estTimeZoneOffset, Constants.HOUR);
			return DateUtilities.newCalendar(
				maxESTDisplayDate.get(Calendar.YEAR),
				maxESTDisplayDate.get(Calendar.MONTH),
				maxESTDisplayDate.get(Calendar.DAY_OF_MONTH),
				0,
				0,
				0,
				Constants.EST_TIME_ZONE).getTimeInMillis();
		}
		return 0;
	}

	private double getAssignmentCountByStatus(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("COUNT(DISTINCT work_history_summary.work_id) AS data");
		builder.addTable("time_dimension");
		builder.addJoin("INNER JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id");

		if (kpiRequest.getFrom() != null) {
			builder.addWhereClause("date", ">= ", "fromDate", kpiRequest.getFrom());
		}
		if (kpiRequest.getTo() != null) {
			builder.addWhereClause("date", "< ", "toDate", kpiRequest.getTo());
		}

		builder.addWhereClause("work_history_summary.work_status_type_code", "= ", "workStatusTypeCode", workStatusTypeCode);

		for (Filter filter : kpiRequest.getFilters()) {
			KPIReportFilter kpiReportFilter = filter.getName();
			String filterName = kpiReportFilter.toString();

			if (kpiReportFilter.equals(KPIReportFilter.ACTIVE_RESOURCE_USER_ID)) {
				List <Long> filterValues = Lists.newArrayList();
				for (String value : filter.getValues()) {
					filterValues.add(Long.parseLong(value));
				}
				builder.addWhereInClause("work_history_summary.active_resource_user_id", filterName, filterValues);
			} else if (kpiReportFilter.equals(KPIReportFilter.COMPANY)) {
				Long filterValue = Long.parseLong(filter.getValues().get(0));
				builder.addWhereClause("work_history_summary.company_id", "= ", filterName, filterValue);
			}
		}

		Long value = jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Long.class);
		return (value != null) ? value.doubleValue() : 0;
	}

	@Override
	public List<DataPoint> countAssignmentsByStatusPaid(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentHistorySummaryPaidSQL(kpiRequest);

		if (kpiRequest.getAggregateInterval() == KPIReportAggregateInterval.MONTH_OF_YEAR){
			builder.addColumn("COALESCE(SUM(fact_monthly_paid_work.count_work), 0) AS data");
			return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
		} else {
			builder.addColumn("COUNT(DISTINCT work_history_summary.work_id) AS data");
			return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
		}
	}

	@Override
	public List<DataPoint> countAllAssignmentsByStatus(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAllAssignmentHistorySummarySQL(workStatusTypeCode, kpiRequest);

		builder.addColumn("COUNT(DISTINCT work_history_summary.work_id) AS data");
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> amountAssignmentsByStatus(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentHistorySummarySQL(workStatusTypeCode, kpiRequest);

		builder.addColumn("COALESCE(SUM(work_history_summary.work_price),0) AS data");
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> amountAssignmentsByStatusPaid(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentHistorySummaryPaidSQL(kpiRequest);

		if (kpiRequest.getAggregateInterval() == KPIReportAggregateInterval.MONTH_OF_YEAR){
			builder.addColumn("COALESCE(SUM(fact_monthly_paid_work.sum_work_price),0) AS data");
		} else {
			builder.addColumn("COALESCE(SUM(work_history_summary.work_price),0) AS data");
		}

		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<TopUser> getTopUsersByCompany(KPIRequest kpiRequest, Integer limit) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildTopUsersByCompanySQL(kpiRequest, limit);

		List<TopUser> results = jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<TopUser>() {
			@Override
			public TopUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				TopUser row = new TopUser();
				row.setUserId(rs.getLong("buyer_user_id"));
				row.setEmail(rs.getString("email"));
				row.setUserNumber(rs.getString("user_number"));
				row.setFirstName(rs.getString("first_name"));
				row.setLastName(rs.getString("last_name"));
				row.setSentAssignments(rs.getInt("sentAssignments"));
				row.setThroughput(rs.getDouble("throughput"));
				return row;
			}
		});
		return decorateTopUser(kpiRequest, results);
	}

	@Override
	public List<TopEntity> getTopProjectsByCompany(KPIRequest kpiRequest, Integer limit) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildTopProjectsByCompanySQL(kpiRequest, limit);

		return jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<TopEntity>() {
			@Override
			public TopEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
				TopEntity row = new TopEntity();
				row.setId(rs.getLong("project_id"));
				row.setName(rs.getString("name"));
				row.setSentAssignments(rs.getInt("sentAssignments"));
				row.setThroughput(rs.getDouble("throughput"));
				return row;
			}
		});
	}

	@Override
	public List<TopEntity> getTopResourcesByCompany(KPIRequest kpiRequest, Integer limit) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildTopResourcesByCompanySQL(kpiRequest, limit);

		return jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<TopEntity>() {
			@Override
			public TopEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
				TopEntity row = new TopEntity();
				row.setId(rs.getLong("user.id"));
				row.setName(StringUtilities.fullName(rs.getString("first_name"), rs.getString("last_name")));
				row.setSentAssignments(rs.getInt("sentAssignments"));
				row.setThroughput(rs.getDouble("throughput"));
				return row;
			}
		});
	}

	@Override
	public AssignmentReport getAssignmentSegmentationReportAssignment(KPIRequest kpiRequest){
		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentSegmentationAssignmentStatusSQL(kpiRequest);
		List<AssignmentReport> resultsStatus = jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<AssignmentReport>() {
			@Override
			public AssignmentReport mapRow(ResultSet rs, int rowNum) throws SQLException {
				AssignmentReport row = new AssignmentReport();
				row.setCancelRate(rs.getDouble("cancelled"));
				row.setVoidRate(rs.getDouble("void"));
				row.setPaidRate(rs.getDouble("paid"));
				row.setSentRate(rs.getDouble("sent"));
				return row;
			}
		});

		return resultsStatus.get(0);
	}

	@Override
	public AssignmentReport getAssignmentSegmentationReportRouting(KPIRequest kpiRequest){
		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentSegmentationReportSQL(kpiRequest);
		List<AssignmentReport> results = jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<AssignmentReport>() {
			@Override
			public AssignmentReport mapRow(ResultSet rs, int rowNum) throws SQLException {
				AssignmentReport row = new AssignmentReport();
				row.setWorkSend(rs.getDouble("workSend"));
				row.setUserSend(rs.getDouble("userSend"));
				row.setGroups(rs.getDouble("groupSend"));
				row.setSearch(rs.getDouble("searchSend"));
				return row;
			}
		});

		return results.get(0);
	}

	@Override
	public SnapshotReport getSnapshotReportVoidRateAndLifeCycle(KPIRequest request){
		SnapshotReport result = new SnapshotReport();
		SQLBuilder builder = kpiSqlBuilderFactory.buildSnapshotReportVoidRate(request);
		// This should only decorate snapshotReport with voidRate and lifeCycle

		List<SnapshotReport.SnapshotDataPoint> snapshotDataPoints = jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<SnapshotReport.SnapshotDataPoint>() {
			@Override
			public SnapshotReport.SnapshotDataPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
				SnapshotReport.SnapshotDataPoint row = new SnapshotReport.SnapshotDataPoint();
				row.setVoidRate(rs.getDouble("void")/rs.getDouble("count"));
				row.setMonth(rs.getInt("month_of_year"));
				row.setYear(rs.getInt("year"));
				return row;
			}
		});
		for (SnapshotReport.SnapshotDataPoint point : snapshotDataPoints){
			result.addDataAtYearAndMonth(point, point.getYear(), point.getMonth());
		}

		builder = kpiSqlBuilderFactory.buildAssignmentLifeCycleOverTimeSQL(request);
		List<SnapshotReport.SnapshotDataPoint> snapshotDataPointsLifeCycle = jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<SnapshotReport.SnapshotDataPoint>() {
			@Override
			public SnapshotReport.SnapshotDataPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
				SnapshotReport.SnapshotDataPoint row = new SnapshotReport.SnapshotDataPoint();
				// row.setVoidRate(rs.getDouble("void")/rs.getDouble("count"));
				row.setLifeCycleDays(rs.getDouble("sent_to_start") + rs.getDouble("start_to_complete") + rs.getDouble("complete_to_closed") + rs.getDouble("closed_to_paid"));
				row.setMonth(rs.getInt("month_of_year"));
				row.setYear(rs.getInt("year"));
				return row;
			}
		});
		for (SnapshotReport.SnapshotDataPoint point : snapshotDataPointsLifeCycle){
			result.addDataAtYearAndMonth(point, point.getYear(), point.getMonth());
		}

		return result;
	}

	private List<TopUser> decorateTopUser(KPIRequest kpiRequest, List<TopUser> results) {
		if (isEmpty(results)) {
			return results;
		}
		Map<Long, TopUser> topUserMap = Maps.newLinkedHashMap();
		for (TopUser t : results) {
			topUserMap.put(t.getUserId(), t);
		}
		List<Long> ids = CollectionUtilities.newListPropertyProjection(results, "userId");
		if (isEmpty(ids)) {
			return results;
		}
		SQLBuilder metaDataSQLBuilder = kpiSqlBuilderFactory.buildTopUsersMetadataSQL(kpiRequest, ids);

		List<Map<String, Object>> metaData = jdbcTemplate.queryForList(metaDataSQLBuilder.build(), metaDataSQLBuilder.getParams());
		for (Map<String, Object> row : metaData) {
			Long userId = (Long) row.get("buyer_user_id");
			String status = (String) row.get("work_status_type_code");
			Integer data = ((Long) row.get("data")).intValue();
			TopUser t = topUserMap.get(userId);
			if (t != null) {
				if (status.equals(WorkStatusType.ACTIVE)) {
					t.setActiveAssignments(data);
				}
				if (status.equals(WorkStatusType.PAYMENT_PENDING) || status.equals(WorkStatusType.CLOSED)) {
					t.setClosedAssignments(t.getClosedAssignments() + data);
				}
			}
		}

		return results;
	}

	@Override
	public List<DataPoint> averageHoursSentToAccepted(KPIRequest kpiRequest) {
		return averageWorkMilestonesTransitionSummaryReport(WorkMilestonesTransitionColumn.SENT_TO_ASSIGNED, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursCompleteToClosed(KPIRequest kpiRequest) {
		return averageWorkMilestonesTransitionSummaryReport(WorkMilestonesTransitionColumn.COMPLETE_TO_CLOSED, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursAssignedToComplete(KPIRequest kpiRequest) {
		return averageWorkMilestonesTransitionSummaryReport(WorkMilestonesTransitionColumn.ASSIGNED_TO_COMPLETE, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursSentoToClosed(KPIRequest kpiRequest) {
		return averageWorkMilestonesTransitionSummaryReport(WorkMilestonesTransitionColumn.SENT_TO_PAID, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursAssignmentsDraftToSent(KPIRequest kpiRequest) {
		return averageWorkStatusTransitionHistorySummaryReport(WorkMilestonesTransitionColumn.DRAFT_TO_SENT, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursAssignmentsSentToActive(KPIRequest kpiRequest) {
		return averageWorkStatusTransitionHistorySummaryReport(WorkMilestonesTransitionColumn.SENT_TO_ACTIVE, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursAssignmentsActiveToComplete(KPIRequest kpiRequest) {
		return averageWorkStatusTransitionHistorySummaryReport(WorkMilestonesTransitionColumn.ACTIVE_TO_COMPLETE, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursAssignmentsCompleteToClosed(KPIRequest kpiRequest) {
		return averageWorkStatusTransitionHistorySummaryReport(WorkMilestonesTransitionColumn.COMPLETE_TO_CLOSED, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursAssignmentsClosedToPaid(KPIRequest kpiRequest) {
		return averageWorkStatusTransitionHistorySummaryReport(WorkMilestonesTransitionColumn.CLOSED_TO_PAID, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursAssignmentsSentToPaid(KPIRequest kpiRequest) {
		return averageWorkStatusTransitionHistorySummaryReport(WorkMilestonesTransitionColumn.SENT_TO_PAID, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursAssignmentsSentToStart(KPIRequest kpiRequest) {
		return averageWorkStatusTransitionHistorySummaryReport(WorkMilestonesTransitionColumn.SENT_TO_START, kpiRequest);
	}

	@Override
	public List<DataPoint> averageHoursAssignmentsStartToComplete(KPIRequest kpiRequest) {
		return averageWorkStatusTransitionHistorySummaryReport(WorkMilestonesTransitionColumn.START_TO_COMPLETE, kpiRequest);
	}

	@Override
	public List<DataPoint> countAssignmentsCompletedToPaidInMoreThan72Hrs(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountAssignmentsCompletedToPaidInMoreThan72HrsSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> amountAssignmentsCompletedToPaidInMoreThan72Hrs(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAmountAssignmentsCompletedToPaidInMoreThan72HrsSQL(kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countAssignmentsNotAcceptedIn2Hrs(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountAssignmentsNotAcceptedIn2HrsSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	// this looks in work_status_transition_history_summary, more efficient
	private List<DataPoint> averageWorkStatusTransitionHistorySummaryReport(WorkMilestonesTransitionColumn transitionColumn, KPIRequest kpiRequest) {
		String fromCode = transitionColumn.getFrom();
		String toCode = transitionColumn.getTo();

		SQLBuilder milestonesSQLBuilder = kpiSqlBuilderFactory.buildAverageWorkStatusTransitionHistorySummarySQL(fromCode, toCode, kpiRequest);
		return generateDataPoints(milestonesSQLBuilder, false, kpiRequest.getAggregateInterval(), DEFAULT_DECIMAL_SCALE);
	}

	private List<DataPoint> averageWorkMilestonesTransitionSummaryReport(WorkMilestonesTransitionColumn transitionColumn, KPIRequest kpiRequest) {
		String fromStatusColumn = "work_milestones.created_on";
		String toStatusColumn = "work_milestones.sent_on";

		switch (transitionColumn) {
			case SENT_TO_ASSIGNED:
				fromStatusColumn = "work_milestones.sent_on";
				toStatusColumn = "work_milestones.accepted_on";
				break;
			case ASSIGNED_TO_COMPLETE:
				fromStatusColumn = "work_milestones.accepted_on";
				toStatusColumn = "work_milestones.complete_on";
				break;
			case COMPLETE_TO_PAID:
				fromStatusColumn = "work_milestones.complete_on";
				toStatusColumn = "work_milestones.paid_on";
				break;
			case SENT_TO_PAID:
				fromStatusColumn = "work_milestones.sent_on";
				toStatusColumn = "work_milestones.paid_on";
				break;
			default:
				// CREATED_TO_SENT
				break;
		}

		SQLBuilder milestonesSQLBuilder = kpiSqlBuilderFactory.buildAverageWorkMilestonesTransitionSummarySQL(fromStatusColumn, toStatusColumn, kpiRequest);
		return generateDataPoints(milestonesSQLBuilder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countAssignmentsByHourByStatus(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountAssignmentsByHourByStatusSQL(workStatusTypeCode, kpiRequest);
		logger.debug(builder.build());
		List<Map<String, Object>> data = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		List<DataPoint> results = Lists.newArrayListWithExpectedSize(data.size());

		for (Map<String, Object> row : data) {
			DataPoint point = new DataPoint();
			Long value = (Long) row.get("data");
			point.setY(value.doubleValue());

			Calendar x = DateUtilities.getCalendarFromDate((Timestamp)(row.get("displayDate")));
			x = DateUtilities.newCalendar(x.get(Calendar.YEAR), x.get(Calendar.MONTH), x.get(Calendar.DAY_OF_MONTH), x.get(Calendar.HOUR_OF_DAY), 0, 0, Constants.EST_TIME_ZONE);
			point.setX(x.getTimeInMillis());
			results.add(point);
		}
		return results;
	}

	@Override
	public List<DataPoint> countCreatedRecruitingCampaigns(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountCreatedRecruitingCampaignsSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countRecruitingCampaignClicks(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountRecruitingCampaignClicksSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countRecruitingCampaignSignups(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountRecruitingCampaignSignupsSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countRecruitingCampaignUsersPendingApproval(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountUserGroupAssociationsByStatusSQL(kpiRequest, "pending", true);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> averageRecruitingCampaignSignups(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAverageRecruitingCampaignSignupsSQL(kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countCreatedGroups(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountCreatedGroupsSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countSentGroupInvitations(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountSentGroupInvitationsSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countNewGroupMembers(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountNewGroupMembersSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countUsersPendingApprovalToGroup(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountUserGroupAssociationsByStatusSQL(kpiRequest, "pending", false);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countTotalGroupMembers(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountUserGroupAssociationsByStatusSQL(kpiRequest, "approved", false);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countCreatedAssessments(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(assessment.id) AS count")
				.addTable("assessment")
				.addWhereClause("assessment.type = 'graded'");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "assessment.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "assessment.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.ASSESSMENTS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return generateDataPoints(mainQuerySqlBuilder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countSentAssessmentsInvitations(KPIRequest kpiRequest) {
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(request.id) AS count")
				.addTable("request")
				.addJoin("INNER JOIN request_assessment_invitation ON request_assessment_invitation.id = request.id")
				.addJoin("INNER JOIN assessment ON assessment.id = request_assessment_invitation.assessment_id")
				.addJoin("INNER JOIN user ON user.id = request.invitee_user_id")
				.addWhereClause("user.email_confirmed = 'Y'");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "request.request_date");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "request.request_date");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.ASSESSMENTS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return generateDataPoints(mainQuerySqlBuilder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countPassedAssessments(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = kpiSqlBuilderFactory.buildCountAssessmentsUserAssociationsSubQuerySQL(kpiRequest);
		subQuerySqlBuilder.addWhereClause("assessment_user_association.passed_flag = 1");

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return generateDataPoints(mainQuerySqlBuilder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countFailedAssessments(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = kpiSqlBuilderFactory.buildCountAssessmentsUserAssociationsSubQuerySQL(kpiRequest);
		subQuerySqlBuilder.addWhereClause("assessment_user_association.passed_flag = 0");

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return generateDataPoints(mainQuerySqlBuilder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countTotalTakenAssessments(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = kpiSqlBuilderFactory.buildCountAssessmentsUserAssociationsSubQuerySQL(kpiRequest);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return generateDataPoints(mainQuerySqlBuilder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> forecastThroughput(KPIRequest kpiRequest) {
		return forecast(kpiRequest, ForecastReportType.THROUGHPUT);
	}

	@Override
	public List<DataPoint> forecastMonthlyAssignmentVolume(KPIRequest kpiRequest) {
		return forecast(kpiRequest, ForecastReportType.MONTHLY_ASSIGNMENT_VOLUME);
	}

	@Override
	public List<DataPoint> forecastAverageAssignmentValue(KPIRequest kpiRequest) {
		List<DataPoint> throughput = forecast(kpiRequest, ForecastReportType.THROUGHPUT);
		List<DataPoint> volume = forecast(kpiRequest, ForecastReportType.MONTHLY_ASSIGNMENT_VOLUME);
		List<DataPoint> averageValue = Lists.newArrayListWithExpectedSize(volume.size());

		for (int i = 0; i <= throughput.size() - 1; i++) {
			DataPoint point = new DataPoint();
			point.setX(throughput.get(i).getX());
			double throughputValue = throughput.get(i).getY();
			double volumeValue = volume.get(i).getY();
			if (volumeValue > 0) {
				point.setY(BigDecimal.valueOf(throughputValue / volumeValue).setScale(2, RoundingMode.HALF_UP).doubleValue());
			} else {
				point.setY(0);
			}
			averageValue.add(point);
		}
		return averageValue;
	}

	private List<DataPoint> forecast(KPIRequest kpiRequest, ForecastReportType forecastReportType) {
		Map<Calendar, Double> timeDimensionDataPointValues = getTimeDimensionDataPointValues(kpiRequest);
		return generateDataPointsForForecast(kpiRequest, timeDimensionDataPointValues, forecastReportType);
	}

	private List<DataPoint> generateDataPoints(SQLBuilder builder, boolean isDiscreteValue, KPIReportAggregateInterval interval) {
		return generateDataPoints(builder, isDiscreteValue, interval, DEFAULT_DECIMAL_SCALE);
	}

	private List<DataPoint> generateDataPoints(SQLBuilder builder, boolean isDiscreteValue, KPIReportAggregateInterval interval, Integer scale) {
		logger.debug(builder.build());
		List<Map<String, Object>> data = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		List<DataPoint> results = Lists.newArrayListWithExpectedSize(data.size());

		for (Map<String, Object> row : data) {
			DataPoint point = new DataPoint();
			Calendar x = DateUtilities.getCalendarFromDate((Timestamp)(row.get("displayDate")));
			if (x != null){
				x = DateUtilities.newCalendar(x.get(Calendar.YEAR), x.get(Calendar.MONTH), x.get(Calendar.DAY_OF_MONTH), 0, 0, 0, Constants.EST_TIME_ZONE);
				point.setX(x.getTimeInMillis());
			} else {
				point.setX(0);
			}
			if (isDiscreteValue) {
				Long value = (Long) row.get("data");
				point.setY((value != null ) ? value.doubleValue() : 0);
			} else {
				BigDecimal value = (BigDecimal) row.get("data");

				point.setY((value != null) ? value.setScale(scale, RoundingMode.HALF_UP).doubleValue() : 0 );
			}

			results.add(point);
		}
		return results;
	}

	private List<DataPoint> generateDataPointsForForecast(KPIRequest kpiRequest, Map<Calendar, Double> timeDimensionDataPointValues, ForecastReportType forecastReportType) {
		return Lists.newArrayList();
	}

	@Override
	public List<DataPoint> countApiCreatedAssignments(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(*) AS count")
				.addTable("work")
				.addJoin("INNER JOIN user ON user.id = work.creator_id")
				.addWhereClause("work.type = 'W'")
				.addWhereClause("user.api_enabled = true");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "work.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "work.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return generateDataPoints(mainQuerySqlBuilder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countCreatedTemplates(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(*) AS count")
				.addTable("work")
				.addWhereClause("work.type = 'WT'");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "work.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "work.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return generateDataPoints(mainQuerySqlBuilder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countUsedTemplates(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(*) AS count")
				.addTable("work")
				.addWhereClause("work.type = 'W'")
				.addWhereClause("work.work_template_id IS NOT NULL");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "work.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "work.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return generateDataPoints(mainQuerySqlBuilder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countBuyerRatings(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountBuyerRatingsSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> averageBuyerRating(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAverageBuyerRatingSQL(kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countResourceRatings(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountResourceRatingsSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> averageResourceRating(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAverageResourceRatingSQL(kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countNewSignups(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountNewSignupsSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countNewBuyerSignups(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountNewBuyerSignupsSQL(kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countAssignmentsSentByNewBuyers(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentHistorySummarySQL(WorkStatusType.SENT, kpiRequest);
		builder.addColumn("SUM(COALESCE(DATEDIFF(time_dimension.date, company.created_on) <= :days, 0)) AS data")
				.addJoin("LEFT 	JOIN company ON company.id = work_history_summary.company_id")
				.addParam("days", Constants.NUMBER_OF_DAYS_TO_CONSIDER_USERS_AS_NEW);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countAssignmentsSentByNonNewBuyers(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentHistorySummarySQL(WorkStatusType.SENT, kpiRequest);
		builder.addColumn("SUM(COALESCE(DATEDIFF(time_dimension.date, company.created_on) > :days, 0)) AS data")
				.addJoin("LEFT 	JOIN company ON company.id = work_history_summary.company_id")
				.addParam("days", Constants.NUMBER_OF_DAYS_TO_CONSIDER_USERS_AS_NEW);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countBuyersSendingFirstAssignmentInXDaysAfterSignUp(KPIRequest kpiRequest, Integer numberOfDaysAfterSignup) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountBuyersSendingFirstAssignmentInXDaysAfterSignUpSQL(kpiRequest, numberOfDaysAfterSignup);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> percentageBuyersSendingFirstAssignmentSubscription(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildPercentageBuyersSendingFirstAssignmentSQL(kpiRequest, AccountPricingType.SUBSCRIPTION_PRICING_TYPE);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> percentageBuyersSendingFirstAssignmentTransactional(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildPercentageBuyersSendingFirstAssignmentSQL(kpiRequest, AccountPricingType.TRANSACTIONAL_PRICING_TYPE);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> percentageAssignmentsNotAcceptedIn2Hrs(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buidPercentageAssignmentsNotAcceptedIn2HrsSQL(kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval(),PERCENTAGE_DECIMAL_SCALE);
	}

	@Override
	public List<DataPoint> countSolePropietors(KPIRequest kpiRequest) {
		return countCompanies(kpiRequest, true);
	}

	@Override
	public List<DataPoint> countCompanies(KPIRequest kpiRequest) {
		return countCompanies(kpiRequest, false);
	}

	private List<DataPoint> countCompanies(KPIRequest kpiRequest, boolean solePropietor) {
		/**
		 * Count the number of companies from before the beginning of the selected time frame to use it as a baseline
		 * for the running summary.
		 */
		SQLBuilder baselineSql = new SQLBuilder();
		baselineSql.addColumn("COUNT(company.id) AS count")
				.addTable("company")
				.addWhereClause("operating_as_individual_flag = :solePropietor")
				.addWhereClause("company.created_on < :fromDate")
				.addParam("fromDate", kpiRequest.getFrom())
				.addParam("solePropietor", solePropietor);

		KpiDAOUtils.applyFilters(baselineSql, kpiRequest.getFilters(), KpiDAOUtils.COMPANY_FILTER_COLUMNS_MAP);
		Integer baseline = jdbcTemplate.queryForObject(baselineSql.build(), baselineSql.getParams(), Integer.class);

		/**
		 * For non summary tables we need to build a sub-query to join it with the time_dimension table
		 * applying date-time functions to a DATETIME column. Eg. created_on.
		 * See: KpiDAOUtils.applyAggregateFunction(SQLBuilder builder, KPIReportAggregateInterval interval, String column)
		 */
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(company.id) AS count")
				.addTable("company")
				.addWhereClause("operating_as_individual_flag = :solePropietor")
				.addParam("solePropietor", solePropietor);

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "company.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "company.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.COMPANY_FILTER_COLUMNS_MAP);

		/**
		 * Build the main query by joining the time_dimension table
		 * See: KpiDAOUtils.buildMainCountTimeDimensionQuery(SQLBuilder subQuerySqlBuilder, KPIRequest kpiRequest)
		 */
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);

		/**
		 * Generate the data points from the main Query
		 */
		List<DataPoint> companiesCreated = generateDataPoints(mainQuerySqlBuilder, true, kpiRequest.getAggregateInterval());
		/**
		 * Calculate running summaries
		 */
		return KpiDAOUtils.generateRunningSummaryDataPoints(baseline, companiesCreated);
	}

	@Override
	public List<DataPoint> countManageWorkUsers(KPIRequest kpiRequest) {
		return countUsers(kpiRequest, true, false);
	}

	@Override
	public List<DataPoint> countPerformWorkUsers(KPIRequest kpiRequest) {
		return countUsers(kpiRequest, false, true);
	}

	@Override
	public List<DataPoint> countUsersPerformingAndManagingWork(KPIRequest kpiRequest) {
		return countUsers(kpiRequest, true, true);
	}

	private List<DataPoint> countUsers(KPIRequest kpiRequest, boolean manageWorkFlag, boolean findWorkFlag) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountUsersSQL(kpiRequest, manageWorkFlag, findWorkFlag);
		List<DataPoint> users = generateDataPoints(builder, true, kpiRequest.getAggregateInterval());

		/**
		 * Calculate running summaries
		 */
		Integer baseline = countUsersPriorToDate(kpiRequest, manageWorkFlag, findWorkFlag);
		return KpiDAOUtils.generateRunningSummaryDataPoints(baseline, users);
	}

	private Integer countUsersPriorToDate(KPIRequest kpiRequest, boolean manageWorkFlag, boolean findWorkFlag) {

		SQLBuilder builder = new SQLBuilder();
		//The sub-query will give us the max data per assignment in the period, group by the interval to later get the latest status the assignment was in
		builder.addColumns("MAX(time_dimension.id) as date_id, user_id")
				.addTable("user_history_summary")
				.addJoin("INNER JOIN time_dimension ON time_dimension.id = user_history_summary.date_id")
				.addWhereClause("time_dimension.date < :fromDate")
				.addGroupColumns("user_id");

		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.USER_HISTORY_FILTER_COLUMNS_MAP);

		SQLBuilder subQuerySQLBuilder = new SQLBuilder();
		subQuerySQLBuilder.addColumns("user_history_summary.user_id", "user_history_summary.date_id")
				.addTable("user_history_summary")
				.addJoin("INNER JOIN (" + builder.build() + ") AS maxUsersData " +
						" ON maxUsersData.date_id = user_history_summary.date_id AND maxUsersData.user_id = user_history_summary.user_id ")
				.addWhereClause("user_status_type_code = :approvedStatus")
				.addWhereClause("manage_work = :manageWorkFlag")
				.addWhereClause("find_work = :findWorkFlag");

		//Build the query to calculate the baseline
		SQLBuilder baselineMainSqlBuilder = new SQLBuilder();
		baselineMainSqlBuilder.addColumns("COUNT(user_id) AS data")
				.addTable(" (" + subQuerySQLBuilder.build() + ") AS statusTable ")
						//Add params to the main sql builder since its the one to be executed
				.addParam("approvedStatus", UserStatusType.APPROVED)
				.addParam("manageWorkFlag", manageWorkFlag)
				.addParam("findWorkFlag", findWorkFlag)
				.addParam("fromDate", kpiRequest.getFrom());

		return jdbcTemplate.queryForObject(baselineMainSqlBuilder.build(), baselineMainSqlBuilder.getParams(), Integer.class);
	}

	@Override
	public List<DataPoint> countCompaniesWithXNumberDraftsCreated(KPIRequest kpiRequest, Integer X) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountCompaniesWithXNumberDraftsCreatedSQL(kpiRequest, X);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countResourcesWithAtLeast1Assignment(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("COUNT(DISTINCT work_history_summary.active_resource_user_id) AS data");
		builder.addTable("time_dimension");

		String workHistorySummaryLeftJoin = "LEFT JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id AND work_history_summary.work_status_type_code = 'paid'";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countResourcesWithAtLeast1AssignmentTrailing12Months(KPIRequest kpiRequest) {
		Map<Calendar, Double> timeDimensionDataPointValues = getTimeDimensionDataPointValues(kpiRequest);
		List<DataPoint> results = Lists.newArrayList();

		for (Calendar calendar : timeDimensionDataPointValues.keySet()) {
			Calendar fromDate = (Calendar) calendar.clone();
			fromDate.add(Calendar.YEAR, -1);
			fromDate = DateUtilities.getCalendarWithFirstDayOfTheMonth(fromDate, Constants.EST_TIME_ZONE);

			SQLBuilder countUsersSQLBuilder = new SQLBuilder();
			countUsersSQLBuilder.addColumns("COUNT(DISTINCT work_history_summary.active_resource_user_id) AS count")
					.addTable("time_dimension")
					.addJoin("INNER JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id")
					.addWhereClause("work_history_summary.work_status_type_code = 'paid'");

			KpiDAOUtils.applyFilters(countUsersSQLBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_RESOURCE_HISTORY_FILTER_COLUMNS_MAP);
			KpiDAOUtils.applyDateFilter(countUsersSQLBuilder, fromDate, calendar, "time_dimension.date");

			Integer count = jdbcTemplate.queryForObject(countUsersSQLBuilder.build(), countUsersSQLBuilder.getParams(), Integer.class);

			//for display purposes
			if (kpiRequest.getAggregateInterval().equals(KPIReportAggregateInterval.MONTH_OF_YEAR)) {
				calendar = DateUtilities.getCalendarWithFirstDayOfTheMonth(calendar, Constants.EST_TIME_ZONE);
			}
			DataPoint dataPoint = new DataPoint();
			dataPoint.setX(calendar.getTimeInMillis());
			dataPoint.setY(count.doubleValue());
			results.add(dataPoint);
		}
		return results;
	}

	@Override
	public List<DataPoint> countBlockedUsers(KPIRequest kpiRequest) {
		List<DataPoint> blockedUsers = countBlockedUsers(kpiRequest, false);
		List<DataPoint> unBlockedUsers = countBlockedUsers(kpiRequest, true);
		List<DataPoint> results = Lists.newArrayListWithExpectedSize(blockedUsers.size());

		for (int i = 0; i <= blockedUsers.size() - 1; i++) {
			DataPoint point = new DataPoint();
			point.setX(blockedUsers.get(i).getX());
			double blocked = blockedUsers.get(i).getY();
			double unblocked = unBlockedUsers.get(i).getY();
			point.setY(blocked-unblocked);
			results.add(point);
		}
		return results;
	}

	@Override
	public List<DataPoint> countTotalUsersOnSystem(KPIRequest kpiRequest) {
		String baselineSql = "SELECT COUNT(DISTINCT user_history_summary.user_id) AS count " +
				" FROM 	time_dimension " +
				" INNER JOIN user_history_summary ON user_history_summary.date_id = time_dimension.id " +
				" WHERE date < :fromDate" +
				" AND 	user_history_summary.user_status_type_code = 'approved'";

		MapSqlParameterSource args = new MapSqlParameterSource();
		args.addValue("fromDate", kpiRequest.getFrom());

		Integer baseline = jdbcTemplate.queryForObject(baselineSql, args, Integer.class);

		Calendar toDate = kpiRequest.getTo() != null ? kpiRequest.getTo() : DateUtilities.getCalendarNow();

		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(DISTINCT user_history_summary.user_id) AS count")
				.addTable("time_dimension")
				.addWhereClause("time_dimension.date <= :toDate")
				.addParam("toDate", toDate);

		String leftJoinQuery = "LEFT JOIN user_history_summary ON user_history_summary.date_id = time_dimension.id  " +
				" AND user_history_summary.user_status_type_code = 'approved'";

		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(subQuerySqlBuilder, kpiRequest.getFilters(), leftJoinQuery, KpiDAOUtils.USER_HISTORY_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);

		List<DataPoint> partialUserResults = generateDataPoints(mainQuerySqlBuilder, true, kpiRequest.getAggregateInterval());
		return KpiDAOUtils.generateRunningSummaryDataPoints(baseline, partialUserResults);
	}

	@Override
	public List<DataPoint> countBuyersWithAtLeast1AssignmentByStatus(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentHistorySummarySQL(workStatusTypeCode, kpiRequest);
		builder.addColumn("COUNT(DISTINCT work_history_summary.company_id) AS data");
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countBuyersWIthAtLeast1AssignmentTrailing12MonthsByStatus(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountBuyersWIthAtLeast1AssignmentTrailing12MonthsByStatusSQL(workStatusTypeCode, kpiRequest);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> countActiveUsers(KPIRequest kpiRequest) {
		SQLBuilder subSelectSQLBuilder = new SQLBuilder();
		subSelectSQLBuilder.addColumns("COUNT(DISTINCT user_id)")
				.addTable("login_info")
				.addWhereClause("logged_on BETWEEN DATE_SUB(time_dimension.date, INTERVAL 6 MONTH ) AND time_dimension.date");

		KpiDAOUtils.applyFilters(subSelectSQLBuilder, kpiRequest.getFilters(), KpiDAOUtils.LOGIN_INFO_FILTER_COLUMNS_MAP);

		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("MAX(time_dimension.date)")
				.addColumn("(" + subSelectSQLBuilder.build() + ") AS data")
				.addTable("time_dimension");

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());

		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> averagePaidAssignmentsPerBuyer(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAssignmentHistorySummarySQL(WorkStatusType.PAID, kpiRequest);

		builder.addColumn("COALESCE(SUM(work_history_summary.buyer_total_cost) / COUNT(DISTINCT work_history_summary.company_id),0) AS data");
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> averagePaidAssignmentsPerActiveResource(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("COALESCE(COALESCE(SUM(work_history_summary.work_price),0) / COUNT(DISTINCT work_history_summary.active_resource_user_id),0) AS data");
		builder.addTable("time_dimension");

		String workHistorySummaryLeftJoin = "LEFT JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id AND work_history_summary.work_status_type_code = 'paid'";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> averagePaidAssignmentsValue(KPIRequest kpiRequest) {
		SQLBuilder builder;
		if (KPIReportAggregateInterval.MONTH_OF_YEAR.equals(kpiRequest.getAggregateInterval())) {
			builder = kpiSqlBuilderFactory.buildAssignmentThroughputActualMonthlySQL(kpiRequest);
			builder.addColumn("COALESCE(SUM(fact_monthly_paid_work.sum_work_price) / SUM(count_work), 0) AS data");
		} else {
			builder = kpiSqlBuilderFactory.buildAssignmentHistorySummarySQL(WorkStatusType.PAID, kpiRequest);
			builder.addColumn("COALESCE(SUM(work_history_summary.work_price) / COUNT(DISTINCT work_history_summary.work_id),0) AS data");
		}
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> averageNumberOfAssignmentsSentByNewBuyers(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildAverageNumberOfAssignmentsByNewBuyersByStatusSQL(WorkStatusType.SENT, kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<KPIAggregateEntityTable> countAssignmentsByIndustryByStatusDatatable(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountAssignmentsByIndustryByStatusDatatableSQL(workStatusTypeCode, kpiRequest);
		return generateIndustryAggregateEntityTable(builder, kpiRequest, true);
	}

	@Override
	public List<KPIAggregateEntityTable> throughputAssignmentsByIndustryByStatusDatatable(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildThroughputAssignmentsByIndustryByStatusDatatableSQL(workStatusTypeCode, kpiRequest);
		return generateIndustryAggregateEntityTable(builder, kpiRequest, false);
	}

	private List<KPIAggregateEntityTable> generateIndustryAggregateEntityTable(SQLBuilder builder, KPIRequest kpiRequest, boolean isDiscreteValue) {
		Map<String, Double> industryAssignmentData = Maps.newHashMap();
		Map<Integer, String> industryMap = getIndustryMap();
		Map<Long, Calendar> dateDisplayMap = getDateDisplayMap(kpiRequest);

		List<KPIAggregateEntityTable> result = Lists.newArrayListWithExpectedSize(industryMap.size());

		for (Map<String, Object> row : jdbcTemplate.queryForList(builder.build(), builder.getParams())) {
			Long industryId = (Long)row.get("industryId");
			if (industryId != null) {
				double value;
				if (isDiscreteValue) {
					value = ((Long) row.get("data")).doubleValue();
				} else {
					value = ((BigDecimal) row.get("data")).doubleValue();
				}
				Long offsetId = (Long) row.get("offsetId");
				industryAssignmentData.put(industryId.toString() + "_" + offsetId.toString(), value);
			}
		}

		for (Map.Entry<Integer, String> industry : industryMap.entrySet()) {
			List<DataPoint> dataPoints = Lists.newArrayList();
			KPIAggregateEntityTable entityTable = new KPIAggregateEntityTable(industry.getKey(), industry.getValue(), dataPoints);
			for (Map.Entry<Long, Calendar> date : dateDisplayMap.entrySet()) {
				String key = industry.getKey()  + "_" + date.getKey();
				double value = MapUtils.getDouble(industryAssignmentData, key, 0.00);
				entityTable.addToDataPoints(new DataPoint(date.getValue().getTimeInMillis(), value));
			}
			result.add(entityTable);
		}
		return result;
	}


	@Override
	public List<KPIAggregateEntityTable> countAssignmentsByHourByStatusDatatable(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountAssignmentsByHourByStatusSQL(workStatusTypeCode, kpiRequest);
		logger.debug(builder.build());
		List<Map<String, Object>> data = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		Map<Calendar, KPIAggregateEntityTable> assignmentsPerDayPerHourMap = Maps.newLinkedHashMap();

		for (Map<String, Object> row : data) {
			Long value = (Long) row.get("data");
			Calendar x = DateUtilities.getCalendarFromDate((Timestamp)(row.get("displayDate")));
			x = DateUtilities.newCalendar(x.get(Calendar.YEAR), x.get(Calendar.MONTH), x.get(Calendar.DAY_OF_MONTH), x.get(Calendar.HOUR_OF_DAY), 0, 0, Constants.EST_TIME_ZONE);
			Calendar calendarByDay = DateUtilities.newCalendar(x.get(Calendar.YEAR), x.get(Calendar.MONTH), x.get(Calendar.DAY_OF_MONTH), 0, 0, 0, Constants.EST_TIME_ZONE);

			DataPoint dataPoint = new DataPoint();
			dataPoint.setX(x.getTimeInMillis());
			dataPoint.setY(value.doubleValue());

			if (!assignmentsPerDayPerHourMap.containsKey(calendarByDay)) {
				KPIAggregateEntityTable kpiAggregateEntityTable = new KPIAggregateEntityTable();
				kpiAggregateEntityTable.setEntityId(calendarByDay.getTimeInMillis());
				kpiAggregateEntityTable.setEntityName(DateUtilities.format("yyyy-MM-dd", calendarByDay));
				assignmentsPerDayPerHourMap.put(calendarByDay, kpiAggregateEntityTable);
			}
			assignmentsPerDayPerHourMap.get(calendarByDay).addToDataPoints(dataPoint);
		}
		List<KPIAggregateEntityTable> results = Lists.newArrayList();
		for (Map.Entry<Calendar, KPIAggregateEntityTable> entry : assignmentsPerDayPerHourMap.entrySet()) {
			results.add(entry.getValue());
		}
		return results;
	}

	@Override
	public List<KPIAggregateEntityTable> countActiveResourcesByIndustryDatatable(KPIRequest kpiRequest) {
		return Lists.newArrayList();
	}

	private List<DataPoint> countBlockedUsers(KPIRequest kpiRequest, boolean deleted) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildCountBlockedUsersSQL(kpiRequest, deleted);
		return generateDataPoints(builder, true, kpiRequest.getAggregateInterval());
	}

	private Map<Calendar, Double>  getTimeDimensionDataPointValues(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension");
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());

		List<Map<String, Object>> timeDimensionData = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		Map<Calendar, Double> timeDimensionDataPointValues = Maps.newLinkedHashMap();

		for (Map<String, Object> row : timeDimensionData) {
			Calendar x = DateUtilities.getCalendarFromDate((Timestamp)(row.get("displayDate")));
			x = DateUtilities.newCalendar(x.get(Calendar.YEAR), x.get(Calendar.MONTH), x.get(Calendar.DAY_OF_MONTH), 0, 0, 0, Constants.EST_TIME_ZONE);

			if (kpiRequest.getAggregateInterval().equals(KPIReportAggregateInterval.MONTH_OF_YEAR)) {
				// Get the last day of the month to make the pro-rate calculations based on the entire month
				x = DateUtilities.getCalendarWithLastDayOfTheMonth(x, Constants.EST_TIME_ZONE);
			} else if (kpiRequest.getAggregateInterval().equals(KPIReportAggregateInterval.YEAR)) {
				// Get the last day of the year to make the pro-rate calculations based on the entire year
				x = DateUtilities.getCalendarWithLastDayOfYear(x, Constants.EST_TIME_ZONE);
			}
			timeDimensionDataPointValues.put(x, 0.0);
		}
		return timeDimensionDataPointValues;
	}

	@Override
	public List<DataPoint> percentagePaymentWithinTerms(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildPercentagePaymentWithinTermsSQL(kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	@Override
	public List<DataPoint> totalFundingByCompany(KPIRequest kpiRequest) {
		SQLBuilder builder = kpiSqlBuilderFactory.buildTotalFundingByCompanySQL(kpiRequest);
		return generateDataPoints(builder, false, kpiRequest.getAggregateInterval());
	}

	private Map<Integer, String> getIndustryMap() {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("industry")
				.addColumns("industry.id AS industryId", "industry.name AS industryName");

		List<Map<String, Object>> timeDimensionData = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		Map<Integer, String> industryMap = Maps.newLinkedHashMap();

		for (Map<String, Object> row : timeDimensionData) {
			Integer industryId = (Integer)row.get("industryId");
			if (industryId != null) {
				if (!industryMap.containsKey(industryId)) {
					String industryName = (String) row.get("industryName");
					industryMap.put(industryId, industryName);
				}
			}
		}
		return industryMap;
	}

	private Map<Long, Calendar> getDateDisplayMap(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension")
				.addColumn("offsetTable.id AS offsetId");
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());

		List<Map<String, Object>> timeDimensionData = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		Map<Long, Calendar> datesMap = Maps.newLinkedHashMap();

		for (Map<String, Object> row : timeDimensionData) {
			Long offsetTableId = (Long)row.get("offsetId");
			if (offsetTableId != null && !datesMap.containsKey(offsetTableId)) {
				Calendar x = DateUtilities.getCalendarFromDate((Timestamp)(row.get("displayDate")));
				datesMap.put(offsetTableId, DateUtilities.newCalendar(x.get(Calendar.YEAR), x.get(Calendar.MONTH), x.get(Calendar.DAY_OF_MONTH), 0, 0, 0, Constants.EST_TIME_ZONE));
			}
		}
		return datesMap;
	}

}
