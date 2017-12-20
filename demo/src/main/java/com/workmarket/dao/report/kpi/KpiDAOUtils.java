package com.workmarket.dao.report.kpi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.KPIReportAggregateInterval;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.summary.TimeDimension;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

class KpiDAOUtils {

	static final  Map<KPIReportFilter, String> WORK_HISTORY_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> WORK_HISTORY_MONTHLY_DRAFT_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> WORK_HISTORY_MONTHLY_PAID_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> WORK_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> GROUPS_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> CAMPAIGNS_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> ASSESSMENTS_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> RATINGS_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> AVERAGE_RATINGS_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> USER_HISTORY_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> COMPANY_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> WORK_RESOURCE_HISTORY_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> USER_GROUP_HISTORY_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> BLOCKED_USER_HISTORY_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> LOGIN_INFO_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> ACCOUNT_REGISTER_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> WORK_MILESTONES_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> REGISTER_TRANSACTION_HISTORY_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> WORK_STATUS_TRANSITION_HISTORY_SUMMARY_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> WORK_STATUS_TRANSITION_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);
	static final  Map<KPIReportFilter, String> FACT_MONTHLY_PAID_WORK_FILTER_COLUMNS_MAP = Maps.newHashMapWithExpectedSize(5);

	static {
		// work history
		WORK_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "work_history_summary.company_id");
		WORK_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "work_history_summary.industry_id");
		WORK_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, "work_history_summary.payment_terms_enabled");
		WORK_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, "work_history_summary.active_resource_user_id");
		WORK_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		WORK_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);

		// work history - fact monthly draft work fact_monthly_draft_work
		WORK_HISTORY_MONTHLY_DRAFT_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "fact_monthly_draft_work.company_id");
		WORK_HISTORY_MONTHLY_DRAFT_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "fact_monthly_draft_work.industry_id");
		WORK_HISTORY_MONTHLY_DRAFT_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, "fact_monthly_draft_work.payment_terms_enabled");
		WORK_HISTORY_MONTHLY_DRAFT_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);
		WORK_HISTORY_MONTHLY_DRAFT_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		WORK_HISTORY_MONTHLY_DRAFT_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);

		// work history - fact monthly paid work fact_monthly_paid_work
		WORK_HISTORY_MONTHLY_PAID_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "fact_monthly_paid_work.company_id");
		WORK_HISTORY_MONTHLY_PAID_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "fact_monthly_paid_work.industry_id");
		WORK_HISTORY_MONTHLY_PAID_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, "fact_monthly_paid_work.payment_terms_enabled");
		WORK_HISTORY_MONTHLY_PAID_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);
		WORK_HISTORY_MONTHLY_PAID_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		WORK_HISTORY_MONTHLY_PAID_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);

		// work
		WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "work.company_id");
		WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "work.industry_id");
		WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, "work.payment_terms_enabled");
		// Filter not supported for work
		WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		// groups
		GROUPS_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "user_group.company_id");
		GROUPS_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "user_group.industry_id");
		// Filter not supported for groups
		GROUPS_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		GROUPS_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		GROUPS_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		GROUPS_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		// campaigns
		CAMPAIGNS_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "recruiting_campaign.company_id");
		// Filter not supported for campaigns
		CAMPAIGNS_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, StringUtils.EMPTY);
		CAMPAIGNS_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		CAMPAIGNS_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		CAMPAIGNS_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		CAMPAIGNS_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		// assessments
		ASSESSMENTS_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "assessment.company_id");
		ASSESSMENTS_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "assessment.industry_id");
		ASSESSMENTS_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		ASSESSMENTS_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		ASSESSMENTS_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		ASSESSMENTS_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		// ratings
		RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "rating.rater_company_id");
		RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "work.industry_id");
		RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, "COALESCE(rating.value,0)/20");
		RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		AVERAGE_RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "rating.rater_company_id");
		AVERAGE_RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "work.industry_id");
		AVERAGE_RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		AVERAGE_RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		AVERAGE_RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		AVERAGE_RATINGS_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		//Users
		USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "user_history_summary.company_id");
		USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "user_history_summary.industry_id");
		USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		//Company
		COMPANY_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "company.id");
		COMPANY_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "company.industry_id");
		COMPANY_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		COMPANY_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		COMPANY_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		COMPANY_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		//Work resource
		WORK_RESOURCE_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "work_resource_history_summary.user_company_id");
		WORK_RESOURCE_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "work_resource_history_summary.user_industry_id");
		WORK_RESOURCE_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		WORK_RESOURCE_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		WORK_RESOURCE_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		WORK_RESOURCE_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		// user group associations
		USER_GROUP_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "user_group_association_history_summary.group_company_id");
		USER_GROUP_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "user_group_association_history_summary.group_industry_id");
		USER_GROUP_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		USER_GROUP_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		USER_GROUP_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		USER_GROUP_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		//blocked users
		BLOCKED_USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "block_user_history_summary.blocking_company_id");
		BLOCKED_USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "block_user_history_summary.user_industry_id");
		BLOCKED_USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		BLOCKED_USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		BLOCKED_USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		BLOCKED_USER_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		LOGIN_INFO_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "login_info.company_id");
		LOGIN_INFO_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "profile.industry_id");
		LOGIN_INFO_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		LOGIN_INFO_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		LOGIN_INFO_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		LOGIN_INFO_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		ACCOUNT_REGISTER_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "account_register_daily_snapshot.company_id");
		ACCOUNT_REGISTER_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "account_register_daily_snapshot.industry_id");
		ACCOUNT_REGISTER_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		ACCOUNT_REGISTER_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		ACCOUNT_REGISTER_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		ACCOUNT_REGISTER_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		WORK_MILESTONES_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "m.company_id");
		WORK_MILESTONES_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, StringUtils.EMPTY);
		WORK_MILESTONES_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		WORK_MILESTONES_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		WORK_MILESTONES_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		WORK_MILESTONES_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		REGISTER_TRANSACTION_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "rth.company_id");
		REGISTER_TRANSACTION_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, StringUtils.EMPTY);
		REGISTER_TRANSACTION_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		REGISTER_TRANSACTION_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		REGISTER_TRANSACTION_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		REGISTER_TRANSACTION_HISTORY_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		//work_status_transition_history_summary
		WORK_STATUS_TRANSITION_HISTORY_SUMMARY_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "work_status_transition_history_summary.company_id");
		WORK_STATUS_TRANSITION_HISTORY_SUMMARY_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, StringUtils.EMPTY);
		WORK_STATUS_TRANSITION_HISTORY_SUMMARY_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		WORK_STATUS_TRANSITION_HISTORY_SUMMARY_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		WORK_STATUS_TRANSITION_HISTORY_SUMMARY_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		WORK_STATUS_TRANSITION_HISTORY_SUMMARY_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		//work_status_transition_history_summary
		WORK_STATUS_TRANSITION_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "work_status_transition.company_id");
		WORK_STATUS_TRANSITION_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, StringUtils.EMPTY);
		WORK_STATUS_TRANSITION_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, StringUtils.EMPTY);
		WORK_STATUS_TRANSITION_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		WORK_STATUS_TRANSITION_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
		WORK_STATUS_TRANSITION_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);

		// fact_monthly_paid_work
		FACT_MONTHLY_PAID_WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.COMPANY, "fact_monthly_paid_work.company_id");
		FACT_MONTHLY_PAID_WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.INDUSTRY, "fact_monthly_paid_work.industry_id");
		FACT_MONTHLY_PAID_WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.PAYMENT_TERMS, "fact_monthly_paid_work.payment_terms_enabled");
		FACT_MONTHLY_PAID_WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, StringUtils.EMPTY);
		FACT_MONTHLY_PAID_WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.LEAD_STATUS, StringUtils.EMPTY);
		FACT_MONTHLY_PAID_WORK_FILTER_COLUMNS_MAP.put(KPIReportFilter.RATING_STAR_VALUE, StringUtils.EMPTY);
	}

	static SQLBuilder applyDateFilter(SQLBuilder builder, Calendar from, Calendar to, String filterColumn) {
		if (from != null) {
			builder.addWhereClause(filterColumn + " >= :fromDate ")
					.addParam("fromDate", from);
		}
		if (to != null) {
			builder.addWhereClause(filterColumn + " < :toDate")
					.addParam("toDate", to);
		}
		return builder;
	}

	/**
	 * Utility method to add the right aggregate function for the when there isn't a summary table with reference to the time_dimension table.
	 *
	 * @param builder
	 * @param interval
	 * @param column
	 * @return
	 */
	static SQLBuilder applyAggregateFunction(SQLBuilder builder, KPIReportAggregateInterval interval, String column) {
		switch (interval) {
			case DAY_OF_MONTH:
				builder.addColumns("YEAR(" + column + ") AS year", "MONTH(" + column + ") AS month", "DAYOFMONTH(" + column + ") AS day");
				builder.addGroupColumns("YEAR(" + column + ")", "MONTH(" + column + ")", "DAYOFMONTH(" + column + ")");
				break;
			case MONTH_OF_YEAR:
				builder.addColumns("YEAR(" + column + ") AS year", "MONTH(" + column + ") AS month");
				builder.addGroupColumns("YEAR(" + column + ")", "MONTH(" + column + ")");
				break;
			case WEEK_OF_YEAR:
				//http://dev.mysql.com/doc/refman/5.5/en/date-and-time-functions.html#function_week
				//Mode 3 since we are Monday based and 1 based
				builder.addColumns("YEAR(" + column + ") AS year",  "MONTH(" + column + ") AS month", "WEEK(" + column + ",3) AS week");
				builder.addGroupColumns("YEAR(" + column + ")", "MONTH(" + column + ")", "WEEK(" + column + ",3)");
				break;
			default:
				// YEAR
				builder.addColumns("YEAR(" + column + ") AS year");
				builder.addGroupColumns("YEAR(" + column + ")");
				break;
		}
		return builder;
	}

	/**
	 * Utility method to add the right aggregate function for the time_dimension table.
	 *
	 * @param builder
	 * @param interval
	 * @return
	 */
	static SQLBuilder applyTimeDimensionTableAggregateFunction(SQLBuilder builder, KPIReportAggregateInterval interval) {

		builder.addColumn("MAX(offsetTable.date) AS displayDate");
		builder.addJoin("INNER JOIN " + TimeDimension.getOffsetTableForESTTimeZone() + " offsetTable ON offsetTable.id = time_dimension.id ");
		builder.addAscOrderBy("displayDate");

		switch (interval) {
			case DAY_OF_MONTH:
				builder.addColumns("offsetTable.year", "offsetTable.month_of_year AS month", "offsetTable.day_of_month AS day");
				builder.addGroupColumns("offsetTable.year", "offsetTable.month_of_year", "offsetTable.day_of_month");
				break;
			case MONTH_OF_YEAR:
				builder.addColumns("offsetTable.year", "offsetTable.month_of_year AS month");
				builder.addGroupColumns("offsetTable.year", "offsetTable.month_of_year");
				break;
			// Group by week only. Do not use this for weekly report more than 1 year
			case WEEK_OF_YEAR:
				builder.addColumns("offsetTable.year", "offsetTable.week_of_year AS week");
				builder.addGroupColumns("offsetTable.week_of_year");
				break;
			case YEAR:
				builder.addColumns("offsetTable.year");
				builder.addGroupColumns("offsetTable.year");
				break;
			default:
				break;
		}
		return builder;
	}

	static SQLBuilder applyTimeDimensionTableAggregateFunctionByHour(SQLBuilder builder) {

		builder.addColumn("MAX(offsetTable.date) AS displayDate");
		builder.addJoin("INNER JOIN " + TimeDimension.getOffsetTableForESTTimeZone() + " offsetTable ON offsetTable.id = time_dimension.id ");

		builder.addColumns("offsetTable.year", "offsetTable.month_of_year AS month", "offsetTable.day_of_month AS day", "offsetTable.hour_of_day AS hour");
		builder.addGroupColumns("offsetTable.year", "offsetTable.month_of_year", "offsetTable.day_of_month", "offsetTable.hour_of_day");
		return builder;
	}

	static SQLBuilder applyLeftJoinFilters(SQLBuilder builder, List<Filter> filters, String leftJoinQuery, Map<KPIReportFilter, String> filterColumnMaps) {
		return applyFilters(builder, filters, leftJoinQuery, filterColumnMaps, true);
	}

	static SQLBuilder applyFilters(SQLBuilder builder, List<Filter> filters, Map<KPIReportFilter, String> filterColumnMaps) {
		return applyFilters(builder, filters, StringUtils.EMPTY, filterColumnMaps, false);
	}

	/**
	 * Apply filters to the left join part of the query. 
	 *
	 * @param builder
	 * @param filters
	 * @param leftJoinQuery
	 * @param filterColumnMaps
	 * @param isLeftJoin
	 * @return
	 */
	private static SQLBuilder applyFilters(SQLBuilder builder, List<Filter> filters, String leftJoinQuery, Map<KPIReportFilter, String> filterColumnMaps, boolean isLeftJoin) {
		String paymentTermsColumn = filterColumnMaps.get(KPIReportFilter.PAYMENT_TERMS);
		List<String> whereClauses = Lists.newArrayList();

		for (Filter f : filters) {
			if (f.isSetName() && f.isSetValues() && f.getName() != null && !f.getValues().isEmpty()) {
				// For company, industry and lead status the behavior is the same
				if (f.getName().equals(KPIReportFilter.COMPANY) || f.getName().equals(KPIReportFilter.INDUSTRY) || f.getName().equals(KPIReportFilter.ACTIVE_RESOURCE_USER_ID) ||
						f.getName().equals(KPIReportFilter.LEAD_STATUS) || f.getName().equals(KPIReportFilter.RATING_STAR_VALUE)) {
					String filterColumn = filterColumnMaps.get(f.getName());
					if (StringUtils.isNotBlank(filterColumn)) {
						if (f.getValuesSize() > 1) {
							whereClauses.add(filterColumn + " IN (" + StringUtils.join(f.getValues(), ",") + ")");
						} else if (StringUtils.isNotBlank(f.getValues().get(0))) {
							whereClauses.add(filterColumn + " = :" + f.getName().toString());
							builder.addParam(f.getName().toString(), Long.parseLong(f.getValues().get(0)));
						}
					}
				}
			}

			if (KPIReportFilter.PAYMENT_TERMS.equals(f.getName()) && StringUtils.isNotBlank(paymentTermsColumn)) {
				whereClauses.add(paymentTermsColumn + " = :" + f.getName().toString());
				builder.addParam(f.getName().toString(), Boolean.parseBoolean(f.getValues().get(0)));
			}
		}

		if (isLeftJoin) {
			if (!whereClauses.isEmpty()) {
				leftJoinQuery = leftJoinQuery + " AND " + StringUtils.join(whereClauses, " AND ");
			}
			builder.addJoin(leftJoinQuery);
		} else {
			builder.getWhereClauses().addAll(whereClauses);
		}

		return builder;
	}

	/**
	 * Utility method to get the right join with the time_dimension table based on the interval selected for the report.
	 *
	 * @param interval
	 * @return
	 */
	static String getTimeDimensionTableJoinClause(KPIReportAggregateInterval interval) {
		switch (interval) {
			case DAY_OF_MONTH:
				return " ON time_dimension.day_of_month = timeDimensionData.day AND time_dimension.month_of_year = timeDimensionData.month AND time_dimension.year = timeDimensionData.year";
			case MONTH_OF_YEAR:
				return " ON time_dimension.month_of_year = timeDimensionData.month AND time_dimension.year = timeDimensionData.year";
			case WEEK_OF_YEAR:
				return " ON time_dimension.week_of_year = timeDimensionData.week AND time_dimension.year = timeDimensionData.year";
			default:
				// YEAR
				return " ON time_dimension.year = timeDimensionData.year";
		}
	}

	/**
	 * Utility method to build queries against the time dimension table when there are no summary tables via a sub-query. 
	 *
	 * @param subQuerySqlBuilder
	 * @param kpiRequest
	 * @return
	 */
	static SQLBuilder buildMainCountTimeDimensionQuery(SQLBuilder subQuerySqlBuilder, KPIRequest kpiRequest) {
		// Build the main query
		SQLBuilder mainQuerySqlBuilder = new SQLBuilder();
		mainQuerySqlBuilder.addColumn("COALESCE(count,0) AS data")
				.addTable("time_dimension")
				.addJoin("LEFT JOIN (" + subQuerySqlBuilder.build() + ") AS timeDimensionData " + getTimeDimensionTableJoinClause(kpiRequest.getAggregateInterval()));

		applyDateFilter(mainQuerySqlBuilder, kpiRequest.getFrom(), kpiRequest.getTo(), "time_dimension.date");
		applyTimeDimensionTableAggregateFunction(mainQuerySqlBuilder, kpiRequest.getAggregateInterval());

		// apply the params from the subquery into the main query
		mainQuerySqlBuilder.getParams().addValues(subQuerySqlBuilder.getParams().getValues());
		return mainQuerySqlBuilder;
	}

	/**
	 * Utility method to get trailing data from a current list of data points. 
	 * Example: when we get how many users were created by month, what we really want to know how many users were in the system per period.
	 *
	 * @param list
	 * @return
	 */
	static List<DataPoint> generateRunningSummaryDataPoints(Integer baseline, List<DataPoint> list) {
		List<DataPoint> results = Lists.newArrayListWithExpectedSize(list.size());
		double sum = baseline.doubleValue();
		for (DataPoint p: list) {
			sum = sum + p.getY();
			DataPoint newDataPoint = new DataPoint();
			newDataPoint.setX(p.getX());
			newDataPoint.setY(sum);
			results.add(newDataPoint);
		}
		return results;
	}
}
