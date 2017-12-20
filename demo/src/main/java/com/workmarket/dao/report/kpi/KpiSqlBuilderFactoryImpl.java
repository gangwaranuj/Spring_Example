package com.workmarket.dao.report.kpi;

import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.kpi.KPIReportAggregateInterval;
import com.workmarket.domains.model.summary.TimeDimension;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class KpiSqlBuilderFactoryImpl implements KpiSqlBuilderFactory {

	@Value("${wm_marketcore.schema}")
	private String WM_MARKETCORE_SCHEMA;

	@Override
	public SQLBuilder buildAssignmentHistorySummaryPaidSQL(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension");
		String workHistorySummaryLeftJoin;
		if (kpiRequest.getAggregateInterval() == KPIReportAggregateInterval.MONTH_OF_YEAR){
			workHistorySummaryLeftJoin = "LEFT JOIN fact_monthly_paid_work ON time_dimension.id = fact_monthly_paid_work.date_id " +
					" AND fact_monthly_paid_work.work_status_type_code = :workStatusTypeCode";
			KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.FACT_MONTHLY_PAID_WORK_FILTER_COLUMNS_MAP);
		} else {
			workHistorySummaryLeftJoin = "LEFT JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id " +
					" AND work_history_summary.work_status_type_code = :workStatusTypeCode";
			KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);
		}

		builder.addParam("workStatusTypeCode", WorkStatusType.PAID);
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());

		return builder;
	}

	@Override
	public SQLBuilder buildAssignmentHistorySummarySQL(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension");

		String workHistorySummaryLeftJoin = "LEFT JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id " +
				" AND work_history_summary.work_status_type_code = :workStatusTypeCode";

		builder.addParam("workStatusTypeCode", workStatusTypeCode);
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);
		return builder;
	}

	public SQLBuilder buildAssignmentHistorySummaryMonthlySQL(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension");

		String workHistorySummaryLeftJoin = "LEFT JOIN fact_monthly_draft_work ON time_dimension.id = fact_monthly_draft_work.date_id " +
				" AND fact_monthly_draft_work.work_status_type_code = :workStatusTypeCode";

		builder.addParam("workStatusTypeCode", workStatusTypeCode);
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_MONTHLY_DRAFT_FILTER_COLUMNS_MAP);

		return builder;
	}

	@Override
	public SQLBuilder buildAllAssignmentHistorySummarySQL(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("work_history_summary")
				.addColumn("sysdate() AS displayDate")
				.addWhereClause("work_history_summary.work_status_type_code = :workStatusTypeCode")
				.addParam("workStatusTypeCode", workStatusTypeCode);

		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);

		if (kpiRequest.isSetFrom()) {
			builder.addJoin("INNER JOIN time_dimension ON time_dimension.id = work_history_summary.date_id ");
			KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		}
		return builder;
	}

	@Override
	public SQLBuilder buildAssignmentThroughputActualSQL(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable(WM_MARKETCORE_SCHEMA + ".time_dimension time_dimension");

		String workHistorySummaryLeftJoin = "LEFT JOIN  " + WM_MARKETCORE_SCHEMA + ".work_history_summary work_history_summary " +
				" ON time_dimension.id = work_history_summary.date_id " +
				" AND work_history_summary.work_status_type_code = 'paid'";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);

		return builder;
	}

	@Override
	public SQLBuilder buildAssignmentThroughputActualMonthlySQL(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable(WM_MARKETCORE_SCHEMA + ".time_dimension time_dimension");

		String workHistorySummaryLeftJoin = "LEFT JOIN  " + WM_MARKETCORE_SCHEMA + ".fact_monthly_paid_work fact_monthly_paid_work " +
				" ON time_dimension.id = fact_monthly_paid_work.date_id " +
				" AND fact_monthly_paid_work.work_status_type_code = 'paid'";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_MONTHLY_PAID_FILTER_COLUMNS_MAP);

		return builder;
	}

	@Override
	public SQLBuilder buildCountUserGroupAssociationsByStatusSQL(KPIRequest kpiRequest, String userGroupAssociationStatusCode, boolean recruitingCampaignGroups) {
		SQLBuilder builder = new SQLBuilder();
		//The sub-query will give us the max data per group and user in the period, group by the interval to later get the latest status
		builder.addColumns("MAX(time_dimension.id) as date_id", "group_id", "user_id")
				.addTable("user_group_association_history_summary")
				.addJoin("INNER JOIN time_dimension ON time_dimension.id = user_group_association_history_summary.date_id")
				.addGroupColumns("group_id", "user_id");

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.USER_GROUP_HISTORY_FILTER_COLUMNS_MAP);

		SQLBuilder subQuerySQLBuilder = new SQLBuilder();
		subQuerySQLBuilder.addColumns("user_group_association_history_summary.date_id", "user_group_association_history_summary.user_id", "user_group_association_history_summary.group_id")
				.addTable("user_group_association_history_summary")
				.addJoin("INNER JOIN (" + builder.build() + ") AS maxGroupData " +
						" ON maxGroupData.date_id = user_group_association_history_summary.date_id " +
						" AND maxGroupData.group_id = user_group_association_history_summary.group_id " +
						" AND maxGroupData.user_id = user_group_association_history_summary.user_id ")
				.addWhereClause("user_group_association_history_summary.user_group_association_status_type_code = :userGroupAssociationStatusCode");

		if (recruitingCampaignGroups) {
			subQuerySQLBuilder.addWhereClause("EXISTS(SELECT id FROM recruiting_campaign WHERE company_user_group_id = user_group_association_history_summary.group_id)");
		}

		SQLBuilder mainQuerySQLBuilder = new SQLBuilder();
		mainQuerySQLBuilder.setParams(builder.getParams());
		mainQuerySQLBuilder.addColumns("COUNT(user_id) AS data")
				.addTable("time_dimension")
				.addJoin("LEFT JOIN (" + subQuerySQLBuilder.build() + ") AS statusTable ON time_dimension.id = statusTable.date_id ")
				.addParam("userGroupAssociationStatusCode", userGroupAssociationStatusCode);

		KpiDAOUtils.applyDateFilter(mainQuerySQLBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(mainQuerySQLBuilder, kpiRequest.getAggregateInterval());
		return mainQuerySQLBuilder;
	}

	@Override
	public SQLBuilder buildCountBuyersSendingFirstAssignmentInXDaysAfterSignUpSQL(KPIRequest kpiRequest, Integer numberOfDaysAfterSignup) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("company.id AS companyId", "company.created_on", "MIN(time_dimension.id) AS timeDimensionId")
				.addTable("time_dimension")
				.addJoin("INNER JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id")
				.addJoin("INNER JOIN company ON company.id = work_history_summary.company_id")
				.addWhereClause("work_history_summary.work_status_type_code = :workStatusTypeCode")
				.addGroupColumns("company.id")
				.addHavingClause("COALESCE(DATEDIFF(MIN(time_dimension.date), company.created_on) <= :numberOfDaysAfterSignup, 0) >= 1");

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);


		SQLBuilder mainQueryBuilder = new SQLBuilder();
		mainQueryBuilder.addColumns("COUNT(companyId) AS data")
				.addTable("time_dimension")
				.addJoin("LEFT JOIN (" + builder.build() + ") AS buyersWithFirstAssignmentInPeriod " +
						" ON time_dimension.id = buyersWithFirstAssignmentInPeriod.timeDimensionId ")
				.addParam("workStatusTypeCode", WorkStatusType.SENT)
				.addParam("numberOfDaysAfterSignup", numberOfDaysAfterSignup);

		KpiDAOUtils.applyDateFilter(mainQueryBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(mainQueryBuilder, kpiRequest.getAggregateInterval());
		return mainQueryBuilder;
	}

	@Override
	public SQLBuilder buildAverageNumberOfAssignmentsByNewBuyersByStatusSQL(String workStatusTypeCode, KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumns("work_history_summary.date_id", "work_history_summary.work_id", "work_history_summary.company_id",
				"work_history_summary.buyer_total_cost", "work_history_summary.work_price")
				.addTable("time_dimension")
				.addJoin("INNER JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id")
				.addJoin("INNER JOIN company ON company.id = work_history_summary.company_id")
				.addWhereClause("work_history_summary.work_status_type_code = :workStatusTypeCode")
				.addWhereClause("COALESCE(DATEDIFF(time_dimension.date, company.created_on) <= :numberOfDaysAfterSignup, 0) >= 1");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);

		SQLBuilder mainQueryBuilder = new SQLBuilder();
		mainQueryBuilder.addColumns("COALESCE(COUNT(work_id) / COUNT(DISTINCT company_id),0) AS data")
				.addTable("time_dimension")
				.addJoin("LEFT JOIN (" + subQuerySqlBuilder.build() + ") AS buyersWithFirstAssignmentInPeriod " +
						" ON time_dimension.id = buyersWithFirstAssignmentInPeriod.date_id ")
				.addParam("workStatusTypeCode", workStatusTypeCode)
				.addParam("numberOfDaysAfterSignup", Constants.NUMBER_OF_DAYS_TO_CONSIDER_USERS_AS_NEW);

		KpiDAOUtils.applyDateFilter(mainQueryBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(mainQueryBuilder, kpiRequest.getAggregateInterval());
		return mainQueryBuilder;
	}

	@Override
	public SQLBuilder buildAverageWorkStatusTransitionHistorySummarySQL(String fromWorkStatusTypeCode, String toWorkStatusTypeCode, KPIRequest kpiRequest){
		String whereClause = " from_work_status_type_code = '%s' AND to_work_status_type_code = '%s'";
		/*
		SELECT COALESCE(AVG(transition_time_in_seconds), 0)/3600 AS data
			FROM work_status_transition_history_summary
		INNER JOIN time_dimension ON date_id = time_dimension.id
			where company_id = 1  AND from_work_status_type_code = 'complete' AND to_work_status_type_code = 'closed'
			and time_dimension.date >= '2013-01-24T00:00:00.000Z'  AND time_dimension.date < '2014-01-27T00:00:00.000Z'
		 */
		if (fromWorkStatusTypeCode.equals(WorkStatusType.CLOSED ) && toWorkStatusTypeCode.equals(WorkStatusType.PAID )){
			whereClause = "((from_work_status_type_code = 'closed') OR ( from_work_status_type_code = 'paymentPending' AND to_work_status_type_code = 'paid'))";
		} else {
			whereClause = String.format(whereClause, fromWorkStatusTypeCode, toWorkStatusTypeCode);
		}

		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("COALESCE(AVG(transition_time_in_seconds), 0)/3600 AS data, time_dimension.date as displayDate")
				.addTable("work_status_transition_history_summary")
				.addJoin("INNER JOIN time_dimension ON date_id = time_dimension.id")
				.addWhereClause(whereClause);

		// Now add date filters and company filters
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.WORK_STATUS_TRANSITION_HISTORY_SUMMARY_FILTER_COLUMNS_MAP);

		return builder;
	}

	@Override
	public SQLBuilder buildAverageWorkMilestonesTransitionSummarySQL(String fromStatusColumn, String toStatusColumn, KPIRequest kpiRequest) {
		/*
		 * Select AVG(minutes) AS data from time_dimension LEFT JOIN (SELECT COALESCE(TIME_TO_SEC(TIMEDIFF(sent_on, created_on)),0)/60 AS minutes, MONTH(work_milestones.created_on) as month,
		 * YEAR(work_milestones.created_on) as year FROM work_milestones WHERE work_milestones.created_on between '2011-01-01' and '2011-10-31' GROUP BY MONTH(work_milestones.created_on),
		 * YEAR(work_milestones.created_on)) AS milestones ON time_dimension.month_of_year = milestones.month AND time_dimension.year = milestones.year
		 */

		// Build the sub-query
		SQLBuilder milestonesSQLBuilder = new SQLBuilder();
		milestonesSQLBuilder.addColumn("AVG(COALESCE(TIME_TO_SEC(TIMEDIFF(" + toStatusColumn + "," + fromStatusColumn + ")),0)/3600) AS avgHours")
				.addTable("work_milestones")
				.addJoin("INNER JOIN work ON work.id = work_milestones.work_id")
						// validate required column is not null
				.addWhereClause(toStatusColumn + " IS NOT NULL")
						// validate that the from date is less than the to date
				.addWhereClause(fromStatusColumn + " < " + toStatusColumn);

		KpiDAOUtils.applyDateFilter(milestonesSQLBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), fromStatusColumn);
		KpiDAOUtils.applyAggregateFunction(milestonesSQLBuilder, kpiRequest.getAggregateInterval(), fromStatusColumn);
		KpiDAOUtils.applyFilters(milestonesSQLBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = new SQLBuilder();
		mainQuerySqlBuilder.addColumn("COALESCE(avgHours,0) AS data")
				.addTable("time_dimension")
				.addJoin("LEFT JOIN (" + milestonesSQLBuilder.build() + ") AS timeDimensionData " + KpiDAOUtils.getTimeDimensionTableJoinClause(kpiRequest.getAggregateInterval()));

		KpiDAOUtils.applyDateFilter(mainQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(mainQuerySqlBuilder, kpiRequest.getAggregateInterval());

		// apply the params from the sub-query into the main query
		mainQuerySqlBuilder.getParams().addValues(milestonesSQLBuilder.getParams().getValues());
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountAssignmentsByHourByStatusSQL(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("COUNT(DISTINCT work_history_summary.work_id) AS data")
				.addTable("time_dimension")
				.addParam("workStatusTypeCode", workStatusTypeCode);

		String workHistorySummaryLeftJoin = "LEFT JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id " +
				" AND work_history_summary.work_status_type_code = :workStatusTypeCode";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunctionByHour(builder);
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);

		return builder;
	}

	@Override
	public SQLBuilder buildCountAssignmentsNotAcceptedIn2HrsSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(work_milestones.work_id) AS count")
				.addTable("work_milestones")
				.addJoin("INNER JOIN work ON work.id = work_milestones.work_id")
				.addWhereClause("work_milestones.sent_on IS NOT NULL")
				.addWhereClause("work_milestones.sent_on < IFNULL(work_milestones.accepted_on,SYSDATE())")
				.addWhereClause("COALESCE(TIME_TO_SEC(TIMEDIFF(IFNULL(work_milestones.accepted_on,SYSDATE()), work_milestones.sent_on)),0) > (2*3600)")
				.addWhereClause("(work_milestones.accepted_on IS NOT NULL OR (work_milestones.accepted_on IS NULL AND work.work_status_type_code NOT IN ('void', 'cancelled')))");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "work_milestones.sent_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "work_milestones.sent_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_FILTER_COLUMNS_MAP);

		// Build the main query
		return KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
	}

	@Override
	public SQLBuilder buidPercentageAssignmentsNotAcceptedIn2HrsSQL(KPIRequest kpiRequest) {
		/**
		 * Sub-query to get the list of assignments with a pseudo-column "greaterThan2hours".
		 * If the assignment is not CANCELLED or VOID AND it took more than 2 hours to be accepted OR
		 * hasn't been accepted but it's been more than 2 hours since it was sent then greaterThan2hours = 1, otherwise 0.
		 *
		 */
		//TODO: optimize - taking 6 secs.
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumns("work_milestones.work_id", "work_milestones.sent_on")
				.addColumn("IF ((work_milestones.accepted_on IS NOT NULL OR (work_milestones.accepted_on IS NULL " +
						" AND work.work_status_type_code NOT IN ('void', 'cancelled'))) " +
						" AND COALESCE(TIME_TO_SEC(TIMEDIFF(IFNULL(work_milestones.accepted_on,SYSDATE()), work_milestones.sent_on)),0) > (2*3600), 1, 0) AS greaterThan2hours")
				.addTable("work_milestones")
				.addJoin("INNER JOIN work ON work.id = work_milestones.work_id")
				.addWhereClause("work_milestones.sent_on IS NOT NULL")
				.addWhereClause("work_milestones.sent_on < IFNULL(work_milestones.accepted_on,SYSDATE())");

		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_FILTER_COLUMNS_MAP);

		/**
		 * Using the previous sub-query we can get the percent of assignments not accepted after 2 hours by
		 * dividing the sum of the "greaterThan2hours" by the total number of assignments.
		 *
		 */
		SQLBuilder outerSubQuerySqlBuilder = new SQLBuilder();
		outerSubQuerySqlBuilder.addColumns("SUM(rawData.greaterThan2hours)/COUNT(rawData.work_id) AS count")
				.addTable("(" + subQuerySqlBuilder.build() + ") as rawData");

		KpiDAOUtils.applyDateFilter(outerSubQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "rawData.sent_on");
		KpiDAOUtils.applyAggregateFunction(outerSubQuerySqlBuilder, kpiRequest.getAggregateInterval(), "rawData.sent_on");

		/**
		 * Build the main query by joining the time_dimension table
		 * See: KpiDAOUtils.buildMainCountTimeDimensionQuery(SQLBuilder subQuerySqlBuilder, KPIRequest kpiRequest)
		 */
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(outerSubQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountAssignmentsCompletedToPaidInMoreThan72HrsSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(work_milestones.work_id) AS count")
				.addTable("work_milestones")
				.addJoin("INNER JOIN work ON work.id = work_milestones.work_id")
				.addWhereClause("work_milestones.complete_on IS NOT NULL")
				.addWhereClause("work_milestones.complete_on < IFNULL(work_milestones.paid_on,SYSDATE())")
				.addWhereClause("COALESCE(TIME_TO_SEC(TIMEDIFF(IFNULL(work_milestones.paid_on,SYSDATE()), work_milestones.complete_on)),0) > (72*60*60)")
				.addWhereClause("(work_milestones.paid_on IS NOT NULL OR (work_milestones.paid_on IS NULL AND work.work_status_type_code NOT IN ('void', 'cancelled')))");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "work_milestones.complete_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "work_milestones.complete_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildAmountAssignmentsCompletedToPaidInMoreThan72HrsSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder milestonesSQLBuilder = new SQLBuilder();
		milestonesSQLBuilder.addColumn("SUM(work.work_price) AS total")
				.addTable("work_milestones")
				.addJoin("INNER JOIN work ON work.id = work_milestones.work_id")
				.addWhereClause("work_milestones.complete_on IS NOT NULL")
				.addWhereClause("work_milestones.complete_on < IFNULL(work_milestones.paid_on,SYSDATE())")
				.addWhereClause("COALESCE(TIME_TO_SEC(TIMEDIFF(IFNULL(work_milestones.paid_on,SYSDATE()), work_milestones.complete_on)),0) > (72*60*60)")
				.addWhereClause("(work_milestones.paid_on IS NOT NULL OR (work_milestones.paid_on IS NULL AND work.work_status_type_code NOT IN ('void', 'cancelled')))");

		KpiDAOUtils.applyDateFilter(milestonesSQLBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "work_milestones.complete_on");
		KpiDAOUtils.applyAggregateFunction(milestonesSQLBuilder, kpiRequest.getAggregateInterval(), "work_milestones.complete_on");
		KpiDAOUtils.applyFilters(milestonesSQLBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = new SQLBuilder();
		mainQuerySqlBuilder.addColumn("COALESCE(total,0) AS data")
				.addTable("time_dimension")
				.addJoin("LEFT JOIN (" + milestonesSQLBuilder.build() + ") AS timeDimensionData " + KpiDAOUtils.getTimeDimensionTableJoinClause(kpiRequest.getAggregateInterval()));

		KpiDAOUtils.applyDateFilter(mainQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(mainQuerySqlBuilder, kpiRequest.getAggregateInterval());

		// apply the params from the sub-query into the main query
		mainQuerySqlBuilder.getParams().addValues(milestonesSQLBuilder.getParams().getValues());
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountCompaniesWithXNumberDraftsCreatedSQL(KPIRequest kpiRequest, Integer X) {
		SQLBuilder builder = new SQLBuilder();

		builder.addColumn("work_history_summary.company_id")
				.addTable("time_dimension")
				.addJoin("INNER JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id ")
				.addWhereClause("work_history_summary.work_status_type_code = :workStatusTypeCode")
				.addGroupColumns("work_history_summary.company_id")
				.addHavingClause("COUNT(work_history_summary.work_id) >= :X")
				.addParam("workStatusTypeCode", WorkStatusType.DRAFT)
				.addParam("X", X);

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);

		SQLBuilder companyCountSqlBuilder = new SQLBuilder();
		companyCountSqlBuilder.addColumn("COUNT(company_id) AS companyCount")
				.addTable("(" + builder.build() + " ) AS workCount ");

		switch (kpiRequest.getAggregateInterval()) {
			case DAY_OF_MONTH:
				companyCountSqlBuilder.addColumns("year", "month", "day").addGroupColumns("year", "month", "day");
				break;
			case MONTH_OF_YEAR:
				companyCountSqlBuilder.addColumns("year", "month").addGroupColumns("year", "month");
				break;
			case WEEK_OF_YEAR:
				companyCountSqlBuilder.addColumns("year", "week").addGroupColumns("year", "week");
				break;
			default:
				// YEAR
				companyCountSqlBuilder.addColumns("year").addGroupColumns("year");
				break;
		}

		SQLBuilder mainQuerySqlBuilder = new SQLBuilder();
		mainQuerySqlBuilder.addColumn("COALESCE(companyCount,0) AS data")
				.addTable("time_dimension");

		//build left join clause from companyCountSqlBuilder
		StringBuilder leftJoin = new StringBuilder("LEFT JOIN (" + companyCountSqlBuilder.build() + " ) AS companyCount ");

		switch (kpiRequest.getAggregateInterval()) {
			case DAY_OF_MONTH:
				leftJoin.append(" ON companyCount.year = time_dimension.year AND companyCount.month = time_dimension.month_of_year AND companyCount.day = time_dimension.day_of_month");
				break;
			case MONTH_OF_YEAR:
				leftJoin.append(" ON companyCount.year = time_dimension.year AND companyCount.month = time_dimension.month_of_year");
				break;
			case WEEK_OF_YEAR:
				leftJoin.append(" ON companyCount.year = time_dimension.year AND companyCount.week = time_dimension.week_of_year");
				break;
			default:
				// YEAR
				leftJoin.append(" ON companyCount.year = time_dimension.year");
				break;
		}

		mainQuerySqlBuilder.addJoin(leftJoin.toString());

		KpiDAOUtils.applyDateFilter(mainQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(mainQuerySqlBuilder, kpiRequest.getAggregateInterval());

		//Copy over the params from the sub-queries
		mainQuerySqlBuilder.getParams().addValues(builder.getParams().getValues());
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountBuyersWIthAtLeast1AssignmentTrailing12MonthsByStatusSQL(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder subSelectSQLBuilder = new SQLBuilder();
		subSelectSQLBuilder.addColumns("COUNT(DISTINCT work_history_summary.company_id) AS count")
				.addTable("work_history_summary")
				.addJoin("INNER JOIN time_dimension time ON work_history_summary.date_id = time.id")
				.addWhereClause("work_status_type_code = :workStatusTypeCode")
				.addWhereClause("time.date BETWEEN DATE_SUB(time_dimension.date, INTERVAL 12 MONTH ) AND time_dimension.date");

		KpiDAOUtils.applyFilters(subSelectSQLBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);

		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("MAX(time_dimension.date)")
				.addColumn("(" + subSelectSQLBuilder.build() + ") AS data")
				.addTable("time_dimension")
				.addParam("workStatusTypeCode", workStatusTypeCode);

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		return builder;
	}

	@Override
	public SQLBuilder buildCountUsersSQL(KPIRequest kpiRequest, boolean manageWorkFlag, boolean findWorkFlag) {
		SQLBuilder builder = new SQLBuilder();
		//The sub-query will give us the max data per assignment in the period, group by the interval to later get the latest status the assignment was in
		builder.addColumns("MAX(time_dimension.id) as date_id", "user_id")
				.addTable("user_history_summary")
				.addJoin("INNER JOIN time_dimension ON time_dimension.id = user_history_summary.date_id")
				.addGroupColumns("user_id");

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
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

		SQLBuilder mainQuerySQLBuilder = new SQLBuilder();
		mainQuerySQLBuilder.addColumns("COUNT(user_id) AS data")
				.addTable("time_dimension")
				.addJoin("LEFT JOIN (" + subQuerySQLBuilder.build() + ") AS statusTable ON time_dimension.id = statusTable.date_id ")
						//Add params to the main sql builder since its the one to be executed
				.addParam("approvedStatus", UserStatusType.APPROVED)
				.addParam("manageWorkFlag", manageWorkFlag)
				.addParam("findWorkFlag", findWorkFlag);

		KpiDAOUtils.applyDateFilter(mainQuerySQLBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(mainQuerySQLBuilder, kpiRequest.getAggregateInterval());
		return mainQuerySQLBuilder;
	}

	@Override
	public SQLBuilder buildPercentageBuyersSendingFirstAssignmentSQL(KPIRequest kpiRequest, String accountPricingType) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("company.id AS companyId", "company.created_on", "MIN(time_dimension.id) AS timeDimensionId",
				"COALESCE(DATEDIFF(MIN(time_dimension.date), company.created_on) <= 90, 0) AS isBuyerWithFirstAssignment")
				.addTable("time_dimension")
				.addJoin("INNER JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id")
				.addJoin("INNER JOIN company ON company.id = work_history_summary.company_id")
				.addWhereClause("work_history_summary.work_status_type_code = :workStatusTypeCode")
				.addWhereClause("work_history_summary.account_pricing_type_code = :accountPricingType")
				.addGroupColumns("company.id");

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);

		SQLBuilder mainQueryBuilder = new SQLBuilder();
		mainQueryBuilder.addColumns("COALESCE(COALESCE(SUM(isBuyerWithFirstAssignment),0)/COUNT(companyId),0) AS data")
				.addTable("time_dimension")
				.addJoin("LEFT JOIN (" + builder.build() + ") AS buyersWithFirstAssignmentInPeriod " +
						" ON time_dimension.id = buyersWithFirstAssignmentInPeriod.timeDimensionId ")
				.addParam("workStatusTypeCode", WorkStatusType.SENT)
				.addParam("accountPricingType", accountPricingType)
				.addParam("numberOfDaysAfterSignup", Constants.NUMBER_OF_DAYS_TO_CONSIDER_USERS_AS_NEW);

		KpiDAOUtils.applyDateFilter(mainQueryBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(mainQueryBuilder, kpiRequest.getAggregateInterval());
		return mainQueryBuilder;
	}

	@Override
	public SQLBuilder buildCountAssessmentsUserAssociationsSubQuerySQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(DISTINCT assessment_user_association.id) AS count")
				.addTable("assessment")
				.addJoin("INNER JOIN assessment_user_association ON assessment.id = assessment_user_association.assessment_id ")
				.addWhereClause("assessment.type = 'graded'")
				.addWhereClause("assessment_user_association.attempt_status_type_code = 'graded'");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "assessment_user_association.completed_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "assessment_user_association.completed_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.ASSESSMENTS_FILTER_COLUMNS_MAP);

		return subQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountBlockedUsersSQL(KPIRequest kpiRequest, boolean deleted) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("COUNT(block_user_history_summary.id) AS data");
		builder.addTable("time_dimension");

		String workHistorySummaryLeftJoin = "LEFT JOIN block_user_history_summary ON time_dimension.id = block_user_history_summary.date_id AND block_user_history_summary.deleted = :deleted";

		builder.addParam("deleted", deleted);
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.BLOCKED_USER_HISTORY_FILTER_COLUMNS_MAP);
		return builder;
	}

	@Override
	public SQLBuilder buildCountNewSignupsSQL(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("COUNT(DISTINCT user_history_summary.company_id) AS data")
				.addTable("time_dimension");

		String leftJoinQuery = "LEFT JOIN user_history_summary ON user_history_summary.date_id = time_dimension.id " +
				" AND user_history_summary.user_status_type_code = 'approved'";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), leftJoinQuery, KpiDAOUtils.USER_HISTORY_FILTER_COLUMNS_MAP);
		return builder;
	}

	@Override
	public SQLBuilder buildCountNewBuyerSignupsSQL(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("COUNT(DISTINCT user_history_summary.company_id) AS data")
				.addTable("time_dimension");

		String leftJoinQuery = "LEFT JOIN user_history_summary ON user_history_summary.date_id = time_dimension.id " +
				" AND user_history_summary.user_status_type_code = 'approved'" + "AND user_history_summary.manage_work = 1";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), leftJoinQuery, KpiDAOUtils.USER_HISTORY_FILTER_COLUMNS_MAP);
		return builder;
	}

	@Override
	public SQLBuilder buildCountAssignmentsByIndustryByStatusDatatableSQL(String workStatusTypeCode, KPIRequest kpiRequest) {

		/*
		* 	 	SELECT 	offsetTable.id offsetId, COALESCE(COUNT(work_history_summary.work_id),0) AS data,   work_history_summary.industry_id AS industryId
				FROM 	time_dimension
				INNER 	JOIN time_dimension_offset_5hours offsetTable ON offsetTable.id = time_dimension.id
				LEFT 	JOIN work_history_summary ON  time_dimension.id =  work_history_summary.date_id AND work_history_summary.work_status_type_code = 'sent' AND work_history_summary.company_id = 1  WHERE
						time_dimension.date_unix >= 1325412000  AND time_dimension.date_unix < 1357034400
				GROUP 	BY work_history_summary.industry_id, offsetTable.year, offsetTable.month_of_year
		*/

		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("offsetTable.id offsetId", "COALESCE(COUNT(work_history_summary.work_id),0) AS data", "work_history_summary.industry_id AS industryId")
				.addTable("time_dimension")
				.addGroupColumns("work_history_summary.industry_id")
				.addParam("workStatusTypeCode", workStatusTypeCode);

		String leftJoinQuery = "LEFT JOIN work_history_summary ON time_dimension.id =  work_history_summary.date_id AND work_history_summary.work_status_type_code = :workStatusTypeCode";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), leftJoinQuery, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);
		return builder;
	}

	@Override
	public SQLBuilder buildThroughputAssignmentsByIndustryByStatusDatatableSQL(String workStatusTypeCode, KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("offsetTable.id offsetId", "COALESCE(SUM(work_history_summary.buyer_total_cost),0) AS data", "work_history_summary.industry_id AS industryId")
				.addTable("time_dimension")
				.addGroupColumns("work_history_summary.industry_id")
				.addParam("workStatusTypeCode", workStatusTypeCode);

		String leftJoinQuery = "LEFT JOIN work_history_summary ON time_dimension.id =  work_history_summary.date_id AND work_history_summary.work_status_type_code = :workStatusTypeCode";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), leftJoinQuery, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);
		return builder;
	}

	@Override
	public SQLBuilder buildSumAvailableCashSQL(KPIRequest kpiRequest) {
		return buildSumAccountRegisterDailySnaphost(kpiRequest, "available_cash");
	}

	@Override
	public SQLBuilder buildSumWithdrawableCashSQL(KPIRequest kpiRequest) {
		return buildSumAccountRegisterDailySnaphost(kpiRequest, "withdrawable_cash");
	}

	@Override
	public SQLBuilder buildSumAccountPayableBalanceSQL(KPIRequest kpiRequest) {
		return buildSumAccountRegisterDailySnaphost(kpiRequest, "accounts_payable_balance");
	}

	private SQLBuilder buildSumAccountRegisterDailySnaphost(KPIRequest kpiRequest, String columnName) {

		/*
		 *  SELECT SUM(account_register_monthly_snapshot.withdrawable_cash) AS withdrawable_cash, offsetTable.year, offsetTable.month_of_year
				FROM account_register_monthly_snapshot
				INNER JOIN time_dimension ON time_dimension.id = account_register_monthly_snapshot.date_id
				INNER JOIN time_dimension_offset_4hours offsetTable ON offsetTable.id = time_dimension.id  WHERE 1=1 AND time_dimension.date >= '2012-03-01 05:00:00'  AND 									time_dimension.date < '2013-03-11 05:00:00'
				GROUP BY offsetTable.year, offsetTable.month_of_year
		 */

		SQLBuilder mainQuerySQLBuilder = new SQLBuilder();
		mainQuerySQLBuilder.addColumns("COALESCE(SUM(account_register_monthly_snapshot." + columnName + "),0) AS data")
				.addTable("account_register_monthly_snapshot")
				.addJoin("INNER JOIN time_dimension ON time_dimension.id = account_register_monthly_snapshot.date_id ");

		KpiDAOUtils.applyDateFilter(mainQuerySQLBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(mainQuerySQLBuilder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyFilters(mainQuerySQLBuilder, kpiRequest.getFilters(), KpiDAOUtils.ACCOUNT_REGISTER_FILTER_COLUMNS_MAP);
		return mainQuerySQLBuilder;

	}

	@Override
	public SQLBuilder buildAverageBuyerRatingSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("AVG(COALESCE(value,0)/20) AS count")
				.addTable("rating")
				.addJoin("INNER JOIN work ON work.id = rating.work_id")
				.addWhereClause("rating.deleted = false")
				.addWhereClause("rating.is_buyer_rating = true");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "rating.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "rating.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.AVERAGE_RATINGS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountResourceRatingsSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(rating.id) AS count")
				.addTable("rating")
				.addJoin("INNER JOIN work ON work.id = rating.work_id")
				.addWhereClause("rating.deleted = false")
				.addWhereClause("rating.is_buyer_rating = false");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "rating.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "rating.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.RATINGS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildAverageResourceRatingSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("AVG(COALESCE(value,0)/20) AS count")
				.addTable("rating")
				.addJoin("INNER JOIN work ON work.id = rating.work_id")
				.addWhereClause("rating.deleted = false")
				.addWhereClause("rating.is_buyer_rating = false");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "rating.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "rating.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.AVERAGE_RATINGS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountBuyerRatingsSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(rating.id) AS count")
				.addTable("rating")
				.addJoin("INNER JOIN work ON work.id = rating.work_id")
				.addWhereClause("rating.deleted = false")
				.addWhereClause("rating.is_buyer_rating = true");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "rating.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "rating.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.RATINGS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}


	@Override
	public SQLBuilder buildCountCreatedRecruitingCampaignsSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(recruiting_campaign.id) AS count")
				.addTable("recruiting_campaign");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "recruiting_campaign.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "recruiting_campaign.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.CAMPAIGNS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountRecruitingCampaignClicksSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(*) AS count")
				.addTable("impression")
				.addJoin("INNER JOIN recruiting_campaign ON impression.campaign_id = recruiting_campaign.id");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "impression.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "impression.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.CAMPAIGNS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountRecruitingCampaignSignupsSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(user.id) AS count")
				.addTable("user")
				.addJoin("INNER JOIN  recruiting_campaign ON recruiting_campaign.id = user.recruiting_campaign_id")
				.addWhereClause("user.email_confirmed ='Y'");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "user.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "user.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.CAMPAIGNS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildAverageRecruitingCampaignSignupsSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COALESCE(COUNT(user.id)/COUNT(DISTINCT recruiting_campaign.id),0) AS count")
				.addTable("recruiting_campaign")
				.addJoin("LEFT JOIN  user ON recruiting_campaign.id = user.recruiting_campaign_id AND user.email_confirmed ='Y'");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "user.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "user.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.CAMPAIGNS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountCreatedGroupsSQL(KPIRequest kpiRequest) {
		// Build the sub-query
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(user_group.id) AS count")
				.addTable("user_group");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "user_group.created_on");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "user_group.created_on");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.GROUPS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountSentGroupInvitationsSQL(KPIRequest kpiRequest) {
		SQLBuilder subQuerySqlBuilder = new SQLBuilder();
		subQuerySqlBuilder.addColumn("COUNT(request.id) AS count")
				.addTable("request")
				.addJoin("INNER JOIN request_group_invitation ON request_group_invitation.id = request.id")
				.addJoin("INNER JOIN user_group ON user_group.id = request_group_invitation.user_group_id")
				.addJoin("INNER JOIN user ON user.id = request.invitee_user_id")
				.addWhereClause("request_group_invitation.invitation_type = 'NEW'")
				.addWhereClause("user.email_confirmed = 'Y'");

		KpiDAOUtils.applyDateFilter(subQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "request.request_date");
		KpiDAOUtils.applyAggregateFunction(subQuerySqlBuilder, kpiRequest.getAggregateInterval(), "request.request_date");
		KpiDAOUtils.applyFilters(subQuerySqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.GROUPS_FILTER_COLUMNS_MAP);

		// Build the main query
		SQLBuilder mainQuerySqlBuilder = KpiDAOUtils.buildMainCountTimeDimensionQuery(subQuerySqlBuilder, kpiRequest);
		return mainQuerySqlBuilder;
	}

	@Override
	public SQLBuilder buildCountNewGroupMembersSQL(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("COUNT(user_group_association_history_summary.id) AS data");
		builder.addTable("time_dimension");

		String userGroupHistorySummaryLeftJoin = "LEFT JOIN user_group_association_history_summary ON time_dimension.id = user_group_association_history_summary.date_id " +
				" AND user_group_association_history_summary.user_group_association_status_type_code = :userGroupAssociationStatusCode";

		builder.addParam("userGroupAssociationStatusCode", "approved");
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), userGroupHistorySummaryLeftJoin, KpiDAOUtils.USER_GROUP_HISTORY_FILTER_COLUMNS_MAP);
		return builder;
	}

	@Override
	public SQLBuilder buildTopUsersByCompanySQL(KPIRequest kpiRequest, Integer limit) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension");

		String workHistorySummaryLeftJoin = "LEFT JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id " +
				" AND work_history_summary.work_status_type_code = :workStatusTypeCode";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);

		builder.addColumns("COUNT(work_history_summary.work_id) AS sentAssignments", "COALESCE(SUM(work_history_summary.work_price),0) AS throughput", "buyer_user_id",
				"user.first_name", "user.last_name", "user.email", "user.user_number")
				.addJoin("LEFT JOIN user on user.id = buyer_user_id")
				.addWhereClause(" buyer_user_id IS NOT NULL")
				.addGroupColumns("buyer_user_id")
				.addParam("workStatusTypeCode", WorkStatusType.PAID)
				.addOrderBy("sentAssignments", "DESC");

		builder.setStartRow(0);
		builder.setPageSize((limit > 0) ? limit : 5);
		return builder;
	}

	@Override
	public SQLBuilder buildTopProjectsByCompanySQL(KPIRequest kpiRequest, Integer limit) {
		/*
		SELECT COUNT(work_history_summary.work_id) AS sentAssignments, SUM(work_history_summary.work_price) AS throughput, project_work_association.project_id, project.name
		FROM time_dimension

		LEFT JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id  AND work_history_summary.work_status_type_code = 'paid' AND work_history_summary.company_id = 1
		inner JOIN project_work_association ON project_work_association.work_id = work_history_summary.work_id
		inner join project on project.id = project_work_association.project_id

		WHERE buyer_user_id IS NOT NULL
		GROUP BY project_work_association.project_id
		ORDER BY sentAssignments DESC LIMIT 0, 5;
		 */
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension");

		String workHistorySummaryLeftJoin = "LEFT JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id " +
				" AND work_history_summary.work_status_type_code = :workStatusTypeCode";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);

		builder.addColumns("COUNT(work_history_summary.work_id) AS sentAssignments", "SUM(work_history_summary.work_price) AS throughput", "project_work_association.project_id", "project.name")
				.addJoin("INNER JOIN project_work_association ON project_work_association.work_id = work_history_summary.work_id")
				.addJoin("INNER JOIN project on project.id = project_work_association.project_id")

				.addGroupColumns("project_work_association.project_id")
				.addParam("workStatusTypeCode", WorkStatusType.PAID)
				.addOrderBy("throughput", "DESC");

		builder.setStartRow(0);
		builder.setPageSize((limit > 0) ? limit : 5);
		return builder;
	}

	@Override
	public SQLBuilder buildTopResourcesByCompanySQL(KPIRequest kpiRequest, Integer limit) {
		/*
		SELECT COUNT(work_history_summary.work_id) AS sentAssignments, SUM(work_history_summary.work_price) AS throughput, first_name, last_name, user.id
		 FROM time_dimension
		LEFT JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id  AND work_history_summary.work_status_type_code = 'paid' AND work_history_summary.company_id =  1
		INNER JOIN user on user.id = work_history_summary.active_resource_user_id
		-- WHERE 1=1 AND time_dimension.date >= :fromDate  AND time_dimension.date < :toDate
		 GROUP BY user.id ORDER BY throughput DESC LIMIT 0, 10

		 */
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension");

		String workHistorySummaryLeftJoin = "LEFT JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id " +
				" AND work_history_summary.work_status_type_code = :workStatusTypeCode";

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.WORK_HISTORY_FILTER_COLUMNS_MAP);

		builder.addColumns("COUNT(work_history_summary.work_id) AS sentAssignments", "SUM(work_history_summary.work_price) AS throughput", "first_name", "last_name", "user.id")
				.addJoin("INNER JOIN user on user.id = work_history_summary.active_resource_user_id")

				.addGroupColumns("user.id")
				.addParam("workStatusTypeCode", WorkStatusType.PAID)
				.addOrderBy("throughput", "DESC");

		builder.setStartRow(0);
		builder.setPageSize((limit > 0) ? limit : 5);
		return builder;
	}

	@Override
	public SQLBuilder buildSnapshotReportVoidRate(KPIRequest kpiRequest){
		/*
			SELECT
					COUNT(*) AS count,
					SUM(CASE WHEN work_id in (
						select work_id from work_status_transition where work_status_transition.work_status_type_code = 'cancelled'
					) THEN 1 ELSE 0 END) as cancelled,
					SUM(CASE WHEN work_id in (
						select work_id from work_status_transition where work_status_transition.work_status_type_code = 'void'
					) THEN 1 ELSE 0 END) as void,
					SUM(CASE WHEN work_id in (
						select work_id from work_status_transition where work_status_transition.work_status_type_code = 'paid'
					) THEN 1 ELSE 0 END) as paid,
					time_dimension.month_of_year,
					time_dimension.year
			FROM time_dimension
			INNER JOIN time_dimension_offset_5hours offsetTable ON offsetTable.id = time_dimension.id
			INNER JOIN work_status_transition ON time_dimension.id = work_status_transition.date_id
			WHERE time_dimension.date >= '2014-01-01'
			AND work_status_transition.company_id = 6225
			AND work_status_transition.work_status_type_code = 'sent'
			GROUP BY time_dimension.year, time_dimension.month_of_year
			ORDER BY time_dimension.year, time_dimension.month_of_year
		 */

		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension");
		builder.addColumns("COUNT(*) AS count",
				"SUM(CASE WHEN work_id in (select work_id from work_status_transition where work_status_transition.work_status_type_code = 'cancelled') THEN 1 ELSE 0 END) as cancelled",
				"SUM(CASE WHEN work_id in (select work_id from work_status_transition where work_status_transition.work_status_type_code = 'void') THEN 1 ELSE 0 END) as void",
				"SUM(CASE WHEN work_id in (select work_id from work_status_transition where work_status_transition.work_status_type_code = 'paid') THEN 1 ELSE 0 END) as paid",
				"time_dimension.month_of_year",
				"time_dimension.year",
				"time_dimension.date")
				.addJoin("INNER JOIN " + TimeDimension.getOffsetTableForESTTimeZone() + " offsetTable ON offsetTable.id = time_dimension.id")
				.addJoin("INNER JOIN work_status_transition ON time_dimension.id = work_status_transition.date_id")
				.addWhereClause("work_status_transition.work_status_type_code = 'sent'")
				.addGroupColumns("time_dimension.year", "time_dimension.month_of_year");

		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.WORK_STATUS_TRANSITION_FILTER_COLUMNS_MAP);
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		return builder;
	}

	@Override
	public SQLBuilder buildAssignmentLifeCycleOverTimeSQL(KPIRequest kpiRequest){
		/*
		SELECT
		COALESCE(SUM(CASE WHEN (from_work_status_type_code = 'sent' AND to_work_status_type_code = 'start' ) THEN transition_time_in_seconds ELSE 0 END)/SUM(CASE WHEN (from_work_status_type_code = 'sent' AND to_work_status_type_code = 'start' ) THEN 1 ELSE 0 END), 0) /3600 AS sent_to_start,
		COALESCE(SUM(CASE WHEN (from_work_status_type_code = 'start' AND to_work_status_type_code = 'complete' ) THEN transition_time_in_seconds ELSE 0 END)/SUM(CASE WHEN (from_work_status_type_code = 'start' AND to_work_status_type_code = 'complete' ) THEN 1 ELSE 0 END), 0) /3600 AS start_to_complete,
		COALESCE(SUM(CASE WHEN (from_work_status_type_code = 'complete' AND to_work_status_type_code = 'closed' ) THEN transition_time_in_seconds ELSE 0 END)/SUM(CASE WHEN (from_work_status_type_code = 'complete' AND to_work_status_type_code = 'closed' ) THEN 1 ELSE 0 END), 0) /3600 AS complete_to_closed,
		COALESCE(SUM(CASE WHEN ((from_work_status_type_code = 'closed') OR ( from_work_status_type_code = 'paymentPending' AND to_work_status_type_code = 'paid')) THEN transition_time_in_seconds ELSE 0 END)/SUM(CASE WHEN ((from_work_status_type_code = 'closed') OR ( from_work_status_type_code = 'paymentPending' AND to_work_status_type_code = 'paid')) THEN 1 ELSE 0 END), 0) /3600 AS closed_to_paid,
		time_dimension.year, time_dimension.month_of_year
		FROM work_status_transition_history_summary
		INNER JOIN time_dimension ON date_id = time_dimension.id
		WHERE company_id = 1
		and time_dimension.date >= '2013-01-24T00:00:00.000Z'  AND time_dimension.date < '2014-01-27T00:00:00.000Z'
		group by time_dimension.year, time_dimension.month_of_year
		 */
		SQLBuilder builder = new SQLBuilder();

		builder.addTable("work_status_transition_history_summary")
				.addColumns(
						"COALESCE(SUM(CASE WHEN (from_work_status_type_code = 'sent' AND to_work_status_type_code = 'start' ) THEN transition_time_in_seconds ELSE 0 END)/SUM(CASE WHEN (from_work_status_type_code = 'sent' AND to_work_status_type_code = 'start' ) THEN 1 ELSE 0 END), 0) /3600 AS sent_to_start",
						"COALESCE(SUM(CASE WHEN (from_work_status_type_code = 'start' AND to_work_status_type_code = 'complete' ) THEN transition_time_in_seconds ELSE 0 END)/SUM(CASE WHEN (from_work_status_type_code = 'start' AND to_work_status_type_code = 'complete' ) THEN 1 ELSE 0 END), 0) /3600 AS start_to_complete",
						"COALESCE(SUM(CASE WHEN (from_work_status_type_code = 'complete' AND to_work_status_type_code = 'closed' ) THEN transition_time_in_seconds ELSE 0 END)/SUM(CASE WHEN (from_work_status_type_code = 'complete' AND to_work_status_type_code = 'closed' ) THEN 1 ELSE 0 END), 0) /3600 AS complete_to_closed",
						"COALESCE(SUM(CASE WHEN ((from_work_status_type_code = 'closed') OR ( from_work_status_type_code = 'paymentPending' AND to_work_status_type_code = 'paid')) THEN transition_time_in_seconds ELSE 0 END)/SUM(CASE WHEN ((from_work_status_type_code = 'closed') OR ( from_work_status_type_code = 'paymentPending' AND to_work_status_type_code = 'paid')) THEN 1 ELSE 0 END), 0) /3600 AS closed_to_paid",
						"time_dimension.year", "time_dimension.month_of_year"
				)
				.addJoin("INNER JOIN time_dimension ON date_id = time_dimension.id")
				.addGroupColumns("time_dimension.year", "time_dimension.month_of_year");

		// Now add date filters and company filters
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.WORK_STATUS_TRANSITION_HISTORY_SUMMARY_FILTER_COLUMNS_MAP);

		return builder;
	}

	@Override
	public SQLBuilder buildAssignmentSegmentationReportSQL(KPIRequest kpiRequest) {
		/*
			SELECT
			SUM(CASE WHEN COALESCE(routing_strategy.type, 'search') = 'search' THEN 1 ELSE 0 END) searchSend,
			SUM(CASE WHEN COALESCE(routing_strategy.type, 'search') = 'auto' THEN 1 ELSE 0 END) workSend,
			SUM(CASE WHEN COALESCE(routing_strategy.type, 'search') = 'group' THEN 1 ELSE 0 END) groupSend,
			SUM(CASE WHEN COALESCE(routing_strategy.type, 'search') = 'user' THEN 1 ELSE 0 END) userSend
			FROM	work_status_transition_history_summary
			INNER	JOIN time_dimension on time_dimension.id = work_status_transition_history_summary.date_id
			LEFT	JOIN routing_strategy ON routing_strategy.work_id = work_status_transition_history_summary.work_id  AND delivery_status_type_code = 'sent'
			WHERE from_work_status_type_code = 'draft' AND to_work_status_type_code = 'sent'
			AND work_status_transition_history_summary.company_id = 1
			AND time_dimension.date BETWEEN '2013-01-01' AND '2014-01-01';

		 */
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("work_status_transition_history_summary");

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");

		builder.addColumns("SUM(CASE WHEN COALESCE(routing_strategy.type, 'search') = 'search' THEN 1 ELSE 0 END) searchSend",
				"SUM(CASE WHEN COALESCE(routing_strategy.type, 'search') = 'auto' THEN 1 ELSE 0 END) workSend",
				"SUM(CASE WHEN COALESCE(routing_strategy.type, 'search') = 'group' THEN 1 ELSE 0 END) groupSend",
				"SUM(CASE WHEN COALESCE(routing_strategy.type, 'search') = 'user' THEN 1 ELSE 0 END) userSend")
				.addJoin("INNER JOIN time_dimension on time_dimension.id = work_status_transition_history_summary.date_id")
				.addJoin("LEFT JOIN routing_strategy ON routing_strategy.work_id = work_status_transition_history_summary.work_id  AND delivery_status_type_code = 'sent'")

				.addWhereClause("from_work_status_type_code = 'draft'")
				.addWhereClause("to_work_status_type_code = 'sent'");

		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.WORK_STATUS_TRANSITION_HISTORY_SUMMARY_FILTER_COLUMNS_MAP);

		builder.setStartRow(0);
		return builder;
	}

	@Override
	public SQLBuilder buildAssignmentSegmentationAssignmentStatusSQL(KPIRequest kpiRequest) {
		/*
		SELECT
			SUM(CASE WHEN work_id in (select work_id from work_status_transition where work_status_transition.work_status_type_code = 'cancelled') THEN 1 ELSE 0 END) as cancelled,
			SUM(CASE WHEN work_id in (select work_id from work_status_transition where work_status_transition.work_status_type_code = 'void') THEN 1 ELSE 0 END) as void,
			SUM(CASE WHEN work_id in (select work_id from work_status_transition where work_status_transition.work_status_type_code = 'paid') THEN 1 ELSE 0 END) as paid,
			COUNT(*) AS sent
		FROM time_dimension
		INNER JOIN time_dimension_offset_4hours offsetTable ON offsetTable.id = time_dimension.id
		INNER JOIN work_status_transition ON time_dimension.id = work_status_transition.date_id
		WHERE 1=1
		AND work_status_transition.work_status_type_code = 'sent'
		AND work_status_transition.company_id = 6225
		AND time_dimension.date >= '2014-01-01' -- AND time_dimension.date < :toDate

		*/

		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension");
		builder.addColumns(
				"SUM(CASE WHEN work_id in (select work_id from work_status_transition where work_status_transition.work_status_type_code = 'cancelled') THEN 1 ELSE 0 END) as cancelled",
				"SUM(CASE WHEN work_id in (select work_id from work_status_transition where work_status_transition.work_status_type_code = 'void') THEN 1 ELSE 0 END) as void",
				"SUM(CASE WHEN work_id in (select work_id from work_status_transition where work_status_transition.work_status_type_code = 'paid') THEN 1 ELSE 0 END) as paid",
				"COUNT(*) AS sent"
		)
				.addJoin("INNER JOIN " + TimeDimension.getOffsetTableForESTTimeZone() + " offsetTable ON offsetTable.id = time_dimension.id")
				.addJoin("INNER JOIN work_status_transition ON time_dimension.id = work_status_transition.date_id")
				.addWhereClause("work_status_transition.work_status_type_code = 'sent'");

		KpiDAOUtils.applyFilters(builder, kpiRequest.getFilters(), KpiDAOUtils.WORK_STATUS_TRANSITION_FILTER_COLUMNS_MAP);
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		return builder;
}

	@Override
	public SQLBuilder buildTopUsersMetadataSQL(KPIRequest kpiRequest, List<Long> userIds) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension")
				.addJoin("INNER JOIN work_history_summary ON time_dimension.id = work_history_summary.date_id ");

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");

		builder.addColumns("COUNT(DISTINCT work_history_summary.work_id) AS data", "buyer_user_id", "work_status_type_code")
				.addWhereClause("work_history_summary.work_status_type_code IN ('active', 'closed', 'paymentPending')");

		if (isNotEmpty(userIds)) {
				builder.addWhereClause("work_history_summary.buyer_user_id IN ("+ StringUtils.join(userIds, ",") + ")");
		}
		builder.addGroupColumns("work_history_summary.buyer_user_id", "work_status_type_code");
		return builder;
	}

	@Override
	public SQLBuilder buildPercentagePaymentWithinTermsSQL(KPIRequest kpiRequest) {

		//Inner most query
		SQLBuilder milestonesSqlBuilder = new SQLBuilder();
		milestonesSqlBuilder.addColumns("work_id", "DATEDIFF(m.paid_on, m.due_on) as daysToPay",
				"IF(DATEDIFF(m.paid_on, m.due_on) <= 0, 1, 0) as paidOnTime",
				"YEAR(m.due_on) as year", "MONTH(m.due_on) as month",
				"DAYOFMONTH(m.due_on) as day", "HOUR(m.due_on) as hour")
				.addTable("work_milestones m");

		KpiDAOUtils.applyDateFilter(milestonesSqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "m.due_on");
		milestonesSqlBuilder.addWhereClause("m.paid_on IS NOT NULL")
				.addWhereClause("m.payment_terms_enabled = true");
		KpiDAOUtils.applyFilters(milestonesSqlBuilder, kpiRequest.getFilters(), KpiDAOUtils.WORK_MILESTONES_FILTER_COLUMNS_MAP);

		//Left join query
		SQLBuilder leftJoinSqlBuilder = new SQLBuilder();
		leftJoinSqlBuilder.addColumns("COUNT(totalAssignmentsWithPaymentTerms.work_id) AS total",
				"SUM(totalAssignmentsWithPaymentTerms.paidOnTime) AS paidOnTime",
				"totalAssignmentsWithPaymentTerms.year",
				"totalAssignmentsWithPaymentTerms.month",
				"totalAssignmentsWithPaymentTerms.day",
				"totalAssignmentsWithPaymentTerms.hour")

				//FROM the inner most query
				.addTable(" ( " + milestonesSqlBuilder.build() + " ) AS totalAssignmentsWithPaymentTerms")
				.addGroupColumns("totalAssignmentsWithPaymentTerms.year",
						"totalAssignmentsWithPaymentTerms.month",
						"totalAssignmentsWithPaymentTerms.day",
						"totalAssignmentsWithPaymentTerms.hour");


		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("COALESCE(paidOnTime/total,0) as data")
				.addTable("time_dimension")
				.addJoin("LEFT	JOIN ( " + leftJoinSqlBuilder.build() + " ) AS timeDimensionData " +
						" ON  time_dimension.year = timeDimensionData.year \n" +
						" AND time_dimension.month_of_year  = timeDimensionData.month \n" +
						" AND time_dimension.day_of_month = timeDimensionData.day \n" +
						" AND time_dimension.hour_of_day = timeDimensionData.hour");

		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());

		builder.setParams(milestonesSqlBuilder.getParams());
		/*
		SELECT 	COALESCE(paidOnTime/total,0), time_dimension.year, time_dimension.month_of_year
		FROM 	time_dimension
		LEFT 	JOIN (
				SELECT  	COUNT(totalAssignmentsWithPaymentTerms.work_id) AS total,
							SUM(totalAssignmentsWithPaymentTerms.paidOnTime) AS paidOnTime,
				  			totalAssignmentsWithPaymentTerms.year,
				  			totalAssignmentsWithPaymentTerms.month,
				  			totalAssignmentsWithPaymentTerms.day,
				  			totalAssignmentsWithPaymentTerms.hour
				FROM (
						SELECT  	work_id, DATEDIFF(m.paid_on, m.due_on) as daysToPay,
				  					IF(DATEDIFF(m.paid_on, m.due_on) <= 0, 1, 0) as paidOnTime,
				  					YEAR(m.due_on) as year, MONTH(m.due_on) as month,
				  					DAYOFMONTH(m.due_on) as day, HOUR(m.due_on) as hour
						FROM      	work_milestones m
						WHERE     	m.due_on BETWEEN '2012-01-01 04:00' AND '2012-07-31 04:00' AND m.paid_on IS NOT NULL AND payment_terms_enabled = true
					) 	AS totalAssignmentsWithPaymentTerms

				GROUP 	BY 	totalAssignmentsWithPaymentTerms.year,
							totalAssignmentsWithPaymentTerms.month,
							totalAssignmentsWithPaymentTerms.day,
							totalAssignmentsWithPaymentTerms.hour

		) AS timeDimensionData

		ON  time_dimension.year = timeDimensionData.year
		AND time_dimension.month_of_year  = timeDimensionData.month
		AND time_dimension.day_of_month = timeDimensionData.day
		AND time_dimension.hour_of_day = timeDimensionData.hour
		INNER JOIN time_dimension_offset_4hours offsetTable ON offsetTable.id = time_dimension.id
		WHERE 1=1 AND time_dimension.date >= '2012-01-01 04:00'  AND time_dimension.date <  '2012-07-31 04:00'
		GROUP by time_dimension.year, time_dimension.month_of_year;
		 */
		return builder;
	}

	@Override
	public SQLBuilder buildTotalFundingByCompanySQL(KPIRequest kpiRequest) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("time_dimension")
				.addColumn("COALESCE(SUM(rth.sum_amount),0) AS data");

		String workHistorySummaryLeftJoin = "LEFT JOIN register_transaction_history_summary rth ON time_dimension.id = rth.date_id " +
				" AND rth.register_transaction_type_code = :registerTransactionTypeCode";

		builder.addParam("registerTransactionTypeCode", RegisterTransactionType.ADD_FUNDS);
		KpiDAOUtils.applyDateFilter(builder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		KpiDAOUtils.applyTimeDimensionTableAggregateFunction(builder, kpiRequest.getAggregateInterval());
		KpiDAOUtils.applyLeftJoinFilters(builder, kpiRequest.getFilters(), workHistorySummaryLeftJoin, KpiDAOUtils.REGISTER_TRANSACTION_HISTORY_FILTER_COLUMNS_MAP);

		return builder;
	}
}
