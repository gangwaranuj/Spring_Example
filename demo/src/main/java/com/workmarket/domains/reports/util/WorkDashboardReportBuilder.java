package com.workmarket.domains.reports.util;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class WorkDashboardReportBuilder {
	private static final Log logger = LogFactory.getLog(WorkDashboardReportBuilder.class);

	public static final boolean WITH_SUM = true;
	public static final boolean WITHOUT_SUM = false;

	public static String getSpendLimitCaseClause(String prefix, boolean withSum) {
		if (StringUtils.isEmpty(prefix)) {
			prefix = "work";
		}

		String pricingStrategyType = prefix + ".pricing_strategy_type";
		String flatPrice = prefix + ".flat_price";
		String additionalExpenses = prefix + ".additional_expenses";
		String bonus = prefix + ".bonus";
		String perHourPrice = prefix + ".per_hour_price";
		String perUnitPrice = prefix + ".per_unit_price";
		String maxNumberOfHours = prefix + ".max_number_of_hours";
		String maxNumberOfUnits = prefix + ".max_number_of_units";
		String workStatusTypeCode = prefix + ".work_status_type_code";
		String initialPerHourPrice = prefix + ".initial_per_hour_price";
		String initialPerUnitPrice = prefix + ".initial_per_unit_price";
		String initialNumberOfHours = prefix + ".initial_number_of_hours";
		String initialNumberOfUnits = prefix + ".initial_number_of_units";
		String additionalPerHourPrice = prefix + ".additional_per_hour_price";
		String additionalPerUnitPrice = prefix + ".additional_per_unit_price";
		String maxBlendedNumberOfHours = prefix + ".max_blended_number_of_hours";
		String maxBlendedNumberOfUnits = prefix + ".max_blended_number_of_units";

		StringBuilder caseClause = new StringBuilder();

		if (withSum) {
			caseClause.append("SUM(");
		}

		caseClause.append("CASE %s \n");
		caseClause.append("WHEN 'FLAT' THEN COALESCE(%s, 0) + IF(%s = 'complete', COALESCE(assignedResource.additional_expenses, 0), %s) + %s \n");
		caseClause.append("WHEN 'PER_HOUR' THEN (%s * %s) + IF(%s = 'complete', COALESCE(assignedResource.additional_expenses, 0), %s) + %s \n");
		caseClause.append("WHEN 'PER_UNIT' THEN (%s * %s) + IF(%s = 'complete', COALESCE(assignedResource.additional_expenses, 0), %s) + %s \n");
		caseClause.append("WHEN 'BLENDED_PER_HOUR' THEN ((%s * %s) + (%s * %s)) + IF(%s = 'complete', COALESCE(assignedResource.additional_expenses, 0), %s) + %s \n");
		caseClause.append("WHEN 'BLENDED_PER_UNIT' THEN ((%s * %s) + (%s * %s)) + IF(%s = 'complete', COALESCE(assignedResource.additional_expenses, 0), %s) + %s \n");
		caseClause.append("WHEN 'INTERNAL' THEN 0 \n");
		caseClause.append("ELSE NULL \n");
		caseClause.append("END");

		if (withSum) {
			caseClause.append(")");
		}

		caseClause.append(" AS spend_limit \n");

		return String.format(caseClause.toString(),
			pricingStrategyType,
			flatPrice, workStatusTypeCode, additionalExpenses, bonus,
			perHourPrice, maxNumberOfHours, workStatusTypeCode, additionalExpenses, bonus,
			perUnitPrice, maxNumberOfUnits, workStatusTypeCode, additionalExpenses, bonus,
			initialPerHourPrice, initialNumberOfHours, additionalPerHourPrice, maxBlendedNumberOfHours, workStatusTypeCode, additionalExpenses, bonus,
			initialPerUnitPrice, initialNumberOfUnits, additionalPerUnitPrice, maxBlendedNumberOfUnits, workStatusTypeCode, additionalExpenses, bonus
		);
	}
	
	public static final String SPEND_LIMIT_CASE_CLAUSE = getSpendLimitCaseClause("work", WITHOUT_SUM);

	public static final String HOURS_BUDGETED_CASE_CLAUSE =
		" CASE work.pricing_strategy_type \n" +
				" WHEN 'PER_HOUR' THEN work.max_number_of_hours \n" +
				" WHEN 'BLENDED_PER_HOUR' THEN work.initial_number_of_hours + work.max_blended_number_of_hours \n" +
				" ELSE 	NULL \n" +
				" END 	AS hoursBudgeted";

	public static final String[] COMMON_COLUMNS = new String[] { "DISTINCT work.id AS workId",
		"work.title",
		"work.work_number",
		"work.created_on",
		"work.pricing_strategy_type",
		"work.schedule_from",
		"work.schedule_through",
		"work.confirmed_flag",
		"work.resource_confirmation_flag",
		"work.due_on AS dueDate",
		"work.payment_terms_days",
		"work.payment_terms_enabled",
		"work.assign_to_first_resource",
		"time_zone.time_zone_id AS timeZoneId",
		"client_company.name AS clientCompanyName",
		"client_company.id AS clientCompanyId",
		"CASE work.address_onsite_flag WHEN 'Y' THEN false WHEN 'N' THEN true ELSE NULL END AS offsite",
		"address.city",
		"state.short_name as state",
		"address.postal_code",
		"address.country",
		"address.latitude",
		"address.longitude",
		"location.name",
		"location.location_number",
		"assignedUser.id AS assignedUserId",
		"assignedUser.user_number",
		"assignedUser.first_name",
		"assignedUser.last_name",
		"buyer.id AS buyerId",
		"buyer.last_name AS buyerLastName",
		"project.id AS projectId",
		"project.name AS projectName",
		"buyer.first_name AS buyerFirstName",
		"work.company_id",
		"work.amount_earned",
		"company.auto_pay_enabled",
		"IFNULL(work.buyer_fee, 0) AS buyer_fee",
		"IFNULL(work.buyer_total_cost, 0) AS buyer_total_cost",
		"lane.lane_type_id AS laneType",
		"assignedCompany.name AS assignedCompanyName",
		"assignedCompany.operating_as_individual_flag",
		"company.name AS companyName",
		"milestones.sent_on",
		"milestones.accepted_on",
		"milestones.complete_on",
		"milestones.closed_on",
		"milestones.paid_on",
		"invoice.id as invoiceId",
		"invoice.invoice_number",
		"waa.last_action_on AS last_modified_on",
		"modifier.last_name AS modifierLastName",
		"modifier.first_name AS modifierFirstName",
		"assignedResource.appointment_from",
		"assignedResource.appointment_through \n",
		"'' AS subStatuses",
		"IF(:isBuyer = TRUE AND work.account_pricing_type_code = 'transactional', COALESCE(legacy_work_fee_configuration.percentage, COALESCE(account_register.current_work_fee_percentage,0)),0) AS workFeePercentage",
	};

	private static final String PROJECT_ID_COLUMN = "project.id as projectId";

	public static final String WORK_STATUS_CASE_CLAUSE =
		" IF	( (resource.user_id = :userId AND resource.work_resource_status_type_code = 'cancelled'), 'cancelled', \n" +
				" CASE 	work.work_status_type_code \n" +
				" WHEN 	'active' \n" +
				" THEN 	IF((resource.user_id = :userId AND assignedUser.id <> :userId AND work.buyer_user_id <> :userId AND :isBuyer = FALSE), 'missed', \n" +
				" 		IF( (assignedResource.checkedin_flag = true \n" +
				" 		OR (work.checkin_required_flag = false AND work.checkin_call_required = false \n" +
				" 		AND IFNULL(assignedResource.checkedin_flag, false) = false AND work.schedule_from <= :today)),  'inprogress', 'active')) \n" +
				" WHEN 'cancelledWithPay' THEN 'cancelled' \n" +
				" WHEN 'cancelledPayPending' THEN 'cancelled' \n" +
				" ELSE 	work.work_status_type_code END) AS statusTypeCode \n";

	public static final String BUYER_WORK_STATUS_CASE_CLAUSE =
		" CASE 	work.work_status_type_code " +
				" WHEN 	'active' " +
				" THEN 	IF( (assignedResource.checkedin_flag = true \n" +
				"		OR (work.checkin_required_flag = false AND work.checkin_call_required = false \n" +
				" 		AND IFNULL(assignedResource.checkedin_flag, false) = false AND work.schedule_from <= :today)),  'inprogress', 'active')" +
				" WHEN 'cancelledWithPay' THEN 'cancelled' " +
				" WHEN 'cancelledPayPending' THEN 'cancelled' " +
				" ELSE 	work.work_status_type_code END AS statusTypeCode ";

	public static final String HAS_RESOURCE_PENDING_APPLY_NEGOTIATION_COLUMN =
		"IF(work.work_status_type_code = 'sent' " +
			"AND work.assign_to_first_resource = FALSE " +
			"AND :isBuyer = FALSE " +
			"AND resource.id IS NOT NULL " +
			"AND EXISTS(SELECT negotiation.id FROM work_negotiation negotiation WHERE negotiation.approval_status = 0 AND negotiation.work_id = work.id AND negotiation.requestor_id = resource.user_id AND (negotiation.expires_on >= :today OR negotiation.expires_on IS NULL)), " +
		"1, 0) AS resourceApplied";

	public static final String HAS_BUYER_PENDING_APPLY_NEGOTIATIONS_COLUMN =
		"IF(work.work_status_type_code = 'sent' " +
			"AND :isBuyer = TRUE " +
			"AND EXISTS(SELECT negotiation.id FROM work_negotiation negotiation WHERE negotiation.approval_status = 0 AND negotiation.work_id = work.id AND (negotiation.expires_on >= :today OR negotiation.expires_on IS NULL)), " +
		"1, 0) AS applicationsPending";

	public static final String HAS_UNANSWERED_QUESTION_WHERE_CLAUSE =
		" EXISTS ( SELECT wqa.id FROM work_question_answer_pair wqa " +
				" WHERE wqa.answer IS NULL AND wqa.work_id = work.id) \n";

	public static final String HAS_OPEN_NEGOTIATION_WHERE_CLAUSE =
		" EXISTS ( SELECT wn.id FROM work_negotiation wn " +
				" WHERE wn.approval_status = 0 AND (wn.expires_on >= :today OR wn.expires_on IS NULL) AND wn.work_id = work.id) \n";

	public static final String[] RESOURCE_IGNORE_FILTERS = new String[] {
		WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.toString(),
		WorkSearchDataPagination.FILTER_KEYS.WORK_SUB_STATUS_TYPE_ID.toString(),
		WorkSearchDataPagination.FILTER_KEYS.HAS_OPEN_NEGOTIATION.toString(),
		WorkSearchDataPagination.FILTER_KEYS.HAS_UNANSWERED_QUESTION.toString(),
		WorkSearchDataPagination.FILTER_KEYS.BUYER_ID.toString(),
		WorkSearchDataPagination.FILTER_KEYS.RESOURCE_ID.toString()
	};

	public static SQLBuilder newWorkDashboardReportSQLBuilder(Long userId, WorkSearchDataPagination pagination, boolean isBuyer, String... excludeFilters) {
		SQLBuilder builder = new SQLBuilder();

		builder
			.addColumns(COMMON_COLUMNS)
			.addColumn(WORK_STATUS_CASE_CLAUSE)
			.addColumn(SPEND_LIMIT_CASE_CLAUSE)
			.addColumn(HAS_RESOURCE_PENDING_APPLY_NEGOTIATION_COLUMN)
			.addColumn(HAS_BUYER_PENDING_APPLY_NEGOTIATIONS_COLUMN)
			.addColumn(PROJECT_ID_COLUMN)
			.addTable("work");

		buildJoinClause(builder, pagination, isBuyer);
		builder
			.addWhereClause(" work.deleted = 0 AND work.type = 'W'")
			.addParam("userId", userId)
				/*
				 * We use this format of date to avoid the use of sysdate() or now() which invalidate the use of the query cache
				 */
			.addParam("today", DateUtilities.formatTodayForSQL())
			.addParam("isBuyer", isBuyer);


		if (pagination.getSortColumn() != null) {
			builder.addOrderBy(WorkSearchDataPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else {
			builder.addOrderBy(WorkSearchDataPagination.SORTS.WORK_ID.getColumn(), WorkSearchDataPagination.SORT_DIRECTION.ASC.toString());
		}

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		return applyFilters(builder, pagination, excludeFilters);
	}

	public static SQLBuilder newWorkDashboardReportAggregateSQLBuilder(Long userId, WorkSearchDataPagination pagination, boolean isBuyer, String... excludeFilters) {
		SQLBuilder builder = new SQLBuilder();

		builder
			.addColumn("COUNT(DISTINCT work.id) AS count")
			.addTable("work")
			.addWhereClause(" work.deleted = 0 AND work.type = 'W'")
			/*
			 * We use this format of date to avoid the use of sysdate() or now() which invalidate the use of the query cache
			 */
			.addParam("today", DateUtilities.formatTodayForSQL())
			.addParam("isBuyer", isBuyer);

		if (userId != null) {
			builder.addParam("userId", userId);
		}

		buildAggregateJoinClause(builder, pagination, isBuyer);

		return applyFilters(builder, pagination, excludeFilters);
	}

	public static final boolean FOR_COUNT = true;
	public static final boolean NOT_FOR_COUNT = false;

	public static SQLBuilder newBundlesByWorkNumberSQLBuilder(String[] workNumbers, WorkReportPagination pagination, boolean forCount) {
		SQLBuilder builder = new SQLBuilder()
			.addColumns(
				"DISTINCT work.id AS workId",
				"work.work_number",
				"work.title",
				"work.work_status_type_code AS status",
				"work.schedule_from AS scheduleFrom",
				"work.schedule_through AS scheduleThrough",
				"work.override_price",
				"IFNULL(work.buyer_total_cost, 0) AS buyer_total_cost",
				"time_zone.time_zone_id AS timeZoneId",
				"CASE work.address_onsite_flag WHEN 'Y' THEN false WHEN 'N' THEN true ELSE NULL END AS offsite",
				"address.city AS city",
				"state.short_name AS state",
				"address.postal_code AS postalCode",
				"address.country AS country",
				"address.latitude AS latitude",
				"address.longitude AS longitude",
				"'B' AS type"
			);

		if (!forCount) {
			builder.addColumn("count(work1.id) AS childCount");
		}

		builder.addColumn(
			"IF(work.account_pricing_type_code = 'transactional', " +
			"\t COALESCE(legacy_work_fee_configuration.percentage, COALESCE(account_register.current_work_fee_percentage, 0)), " +
			"\t  0) AS workFeePercentage"
		);

		builder
			.addColumn(getSpendLimitCaseClause("work1", WITH_SUM))
			.addTable("work");

		builder
			.addJoin("INNER JOIN time_zone ON time_zone.id = work.time_zone_id ")
			.addJoin("INNER JOIN account_register on work.company_id = account_register.company_id")
			.addJoin("LEFT JOIN address ON work.address_id = address.id")
			.addJoin("LEFT JOIN state ON state.id = address.state")
			.addJoin("LEFT JOIN work_resource AS assignedResource ON (work.id = assignedResource.work_id and assignedResource.assigned_to_work = true)")
			.addJoin("LEFT JOIN legacy_work_fee_configuration ON legacy_work_fee_configuration.work_id = work.id")
			.addJoin("LEFT OUTER JOIN work AS work1 ON work.id = work1.parent_id");

		builder
			.addWhereInClause("work1.work_number", "workNumbers", Lists.newArrayList(workNumbers));

		if (pagination.getSortColumn() != null) {
			builder.addOrderBy(WorkReportPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		}

		if (!forCount) {
			builder.addGroupColumns("workId");
		}

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		return builder;
	}

	public static SQLBuilder newWorkByWorkNumberSQLBuilder(String[] workNumbers, WorkReportPagination pagination) {
		SQLBuilder builder = new SQLBuilder()
			.addColumns(
				"DISTINCT work.id AS workId",
				"work.work_number",
				"work.title",
				"work.work_status_type_code AS status",
				"work.schedule_from AS scheduleFrom",
				"work.schedule_through AS scheduleThrough",
				"work.override_price",
				"IFNULL(work.buyer_total_cost, 0) AS buyer_total_cost",
				"time_zone.time_zone_id AS timeZoneId",
				"CASE work.address_onsite_flag WHEN 'Y' THEN false WHEN 'N' THEN true ELSE NULL END AS offsite",
				"address.city AS city",
				"state.short_name AS state",
				"address.postal_code AS postalCode",
				"address.country AS country",
				"address.latitude AS latitude",
				"address.longitude AS longitude",
				"'W' AS type",
				"0 AS childCount",
				"IF(work.account_pricing_type_code = 'transactional', " +
					"\t COALESCE(legacy_work_fee_configuration.percentage, COALESCE(account_register.current_work_fee_percentage, 0)), " +
					"\t  0) AS workFeePercentage"
				)
			.addColumn(SPEND_LIMIT_CASE_CLAUSE)
			.addTable("work");

		builder
			.addJoin("INNER JOIN time_zone ON time_zone.id = work.time_zone_id ")
			.addJoin("INNER JOIN account_register on work.company_id = account_register.company_id")
			.addJoin("LEFT JOIN address ON work.address_id = address.id")
			.addJoin("LEFT JOIN state ON state.id = address.state")
			.addJoin("LEFT JOIN work_resource AS assignedResource ON (work.id = assignedResource.work_id and assignedResource.assigned_to_work = true)")
			.addJoin("LEFT JOIN legacy_work_fee_configuration ON legacy_work_fee_configuration.work_id = work.id");

		builder
			.addWhereInClause("work_number", "workNumbers", Lists.newArrayList(workNumbers))
			.addWhereClause("work.deleted = 0 AND work.type = 'W' AND work.parent_id IS NULL");

		if (pagination.getSortColumn() != null) {
			builder.addOrderBy(WorkReportPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		}

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		return builder;
	}

	private static SQLBuilder buildJoinClause(SQLBuilder builder, WorkSearchDataPagination pagination, boolean isBuyer) {
		builder
			.addJoin("INNER JOIN company ON work.company_id = company.id ")
			.addJoin("INNER JOIN account_register on company.id = account_register.company_id ")
			.addJoin("INNER JOIN user as buyer ON work.buyer_user_id = buyer.id")
			.addJoin("INNER JOIN work_action_audit waa ON waa.work_id = work.id")
			.addJoin("INNER JOIN user as modifier ON waa.modifier_id = modifier.id")
			.addJoin("INNER JOIN time_zone ON time_zone.id = work.time_zone_id ")
			.addJoin("INNER JOIN work_milestones milestones ON work.id = milestones.work_id");

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_TYPE.toString()) ||
					pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_INITIATED_BY_RESOURCE.toString()) ||
					pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_APPROVAL_STATUS.toString())) {
				builder.addJoin("INNER JOIN work_negotiation negotiation ON work.id = negotiation.work_id");
			}
		}

		builder
			.addJoin("LEFT JOIN invoice  ON (invoice.id = work.invoice_id AND invoice.deleted = 0) ")
			.addJoin("LEFT JOIN client_company ON work.client_company_id = client_company.id")
			.addJoin("LEFT JOIN address ON work.address_id = address.id")
			.addJoin("LEFT JOIN state ON state.id = address.state")
			.addJoin("LEFT JOIN location ON work.client_location_id = location.id")
			.addJoin("LEFT JOIN project_work_association pwa ON pwa.work_id = work.id")
			.addJoin("LEFT JOIN project ON project.id = pwa.project_id")
			.addJoin("LEFT JOIN work_sub_status_type_association sub_status " +
					" ON (sub_status.work_id = work.id  AND sub_status.deleted = FALSE AND sub_status.resolved = FALSE )")

			.addJoin("LEFT JOIN work_sub_status_type ON (work_sub_status_type.id = sub_status.work_sub_status_type_id " +
					" AND " + (isBuyer ? "work_sub_status_type.client_visible" : "work_sub_status_type.resource_visible") + " = TRUE " +
					" AND work_sub_status_type.deleted = FALSE)")

			.addJoin("LEFT JOIN work_resource AS assignedResource ON (work.id = assignedResource.work_id and assignedResource.assigned_to_work = true)")
			.addJoin("LEFT JOIN user AS assignedUser ON assignedResource.user_id = assignedUser.id ")
			.addJoin("LEFT JOIN company AS assignedCompany ON assignedUser.company_id = assignedCompany.id ")

			.addJoin("LEFT JOIN lane_association lane ON (lane.user_id = assignedUser.id AND lane.company_id = work.company_id " +
					" AND lane.deleted = false) ")
			.addJoin(" LEFT JOIN legacy_work_fee_configuration ON legacy_work_fee_configuration.work_id = work.id", false);

		return builder;
	}

	private static SQLBuilder buildAggregateJoinClause(SQLBuilder builder, WorkSearchDataPagination pagination, boolean isBuyer) {

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.INVOICE_ID.toString())) {
				builder.addJoin("INNER JOIN invoice  ON (invoice.id = work.invoice_id AND invoice.deleted = 0) ");
			}

			if (pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.PROJECT_ID.toString())) {
				builder.addJoin("INNER JOIN project_work_association pwa ON pwa.work_id = work.id");
			}
		}

		// Always include these tables (the order is important!!)
		builder
			.addJoin("LEFT JOIN work_resource  as assignedResource ON (work.id = assignedResource.work_id and assignedResource.assigned_to_work = true)")
			.addJoin("LEFT JOIN user as assignedUser ON assignedResource.user_id = assignedUser.id ")
			.addJoin("LEFT JOIN work_sub_status_type_association sub_status " +
					" ON (sub_status.work_id = work.id  AND sub_status.deleted = FALSE AND sub_status.resolved = FALSE )")
			.addJoin("LEFT JOIN work_sub_status_type ON (work_sub_status_type.id = sub_status.work_sub_status_type_id " +
					" AND " + (isBuyer ? "work_sub_status_type.client_visible" : "work_sub_status_type.resource_visible") + " = TRUE " +
					" AND  work_sub_status_type.deleted = FALSE)")
			.addJoin("LEFT JOIN work_sub_status_type_company_setting setting ON " +
					" (work_sub_status_type.id = setting.work_sub_status_type_id AND setting.company_id = work.company_id)");

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.LANE_TYPE_ID.toString())) {
				builder.addJoin("LEFT JOIN lane_association lane ON (lane.user_id = assignedUser.id AND lane.company_id = work.company_id " +
						" AND lane.deleted = false AND lane.approval_status IN (1,5) AND lane.verification_status = 1) ");
			}
			
			if(CollectionUtilities.containsAny(pagination.getFilters().keySet(), WorkSearchDataPagination.FILTER_KEYS.DATE_RANGES)) {
				builder.addJoin("LEFT JOIN work_milestones milestones ON work.id = milestones.work_id");
			}

			if (pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_TYPE.toString()) ||
					pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_INITIATED_BY_RESOURCE.toString()) ||
					pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_APPROVAL_STATUS.toString())) {
				builder.addJoin("INNER JOIN work_negotiation negotiation ON work.id = negotiation.work_id");
			}
		}
		return builder;
	}

	private static SQLBuilder applyFilters(SQLBuilder builder, WorkSearchDataPagination pagination, String... excludeFilters) {
		logger.debug("pagination filters: " + pagination.getFilters());
		Iterator<String> it = pagination.getFilters().keySet().iterator();

		List<String> exclusions = new ArrayList<>();
		if (excludeFilters != null) {
			exclusions = Arrays.asList(excludeFilters);
		}

		int i = 0;
		while (it.hasNext()) {

			String filter = it.next();
			if (exclusions.contains(filter)) { continue; }

			WorkSearchDataPagination.FILTER_KEYS filterKey = WorkSearchDataPagination.FILTER_KEYS.valueOf(filter);
			String filterValue = pagination.getFilters().get(filter);

			// All the date filters
			if (filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.START_DATE) ||
					filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.END_DATE) ||
					filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.CREATED_DATE_FROM) ||
					filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.CREATED_DATE_TO) ||
					filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.ACCEPTED_DATE_FROM) ||
					filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.ACCEPTED_DATE_TO) ||
					filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.DUE_DATE_FROM) ||
					filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.DUE_DATE_TO) ||
					filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.SENT_DATE_FROM) ||
					filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.SENT_DATE_TO)) {

				builder.addWhereClause(filterKey.getOperationWithParam(":param" + i))
						.addParam("param" + i, DateUtilities.getCalendarFromISO8601(pagination.getFilters().get(filter)));

			} 
			else if (filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.MODIFIED_DATE_FROM) ||
						filterKey.equals(WorkSearchDataPagination.FILTER_KEYS.MODIFIED_DATE_TO)) {
				builder
					.addWhereClause(filterKey.getOperationWithParam(":param" + i))
					.addParam("param" + i, DateUtilities.getCalendarFromISO8601(pagination.getFilters().get(filter)));
			} 
			else if (WorkSearchDataPagination.FILTER_KEYS.WORK_TITLE.equals(filterKey)) {

				builder
					.addWhereClause(WorkSearchDataPagination.FILTER_KEYS.WORK_TITLE.getOperationWithParam(":title"))
					.addParam("title", StringUtilities.processForLike(filterValue));

			} else if (WorkSearchDataPagination.FILTER_KEYS.RESOURCE_ID.equals(filterKey)) {
				buildWhereInClause(builder, filterKey, filterValue, "param" + i);
			} else if (WorkSearchDataPagination.FILTER_KEYS.COMPANY_ID.equals(filterKey)) {
				continue;
			} else if (WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.equals(filterKey) && filterValue.equals(WorkStatusType.ALL)) {
				logger.debug("Filtering on ALL statuses: " + WorkStatusType.BUYER_STATUSES_FOR_DISPLAY);
				String values = CollectionUtilities.join(WorkStatusType.BUYER_STATUSES_FOR_DISPLAY, ",");
				buildWhereInClause(builder, filterKey, values, "status");
			} else if(WorkSearchDataPagination.FILTER_KEYS.WORK_SUB_STATUS_TYPE_ID.equals(filterKey) && filterValue.equals(WorkStatusType.ALL)) {
				logger.debug("Filtering on ALL sub-statuses: " + WorkStatusType.BUYER_SENT_SUB_STATUSES_FOR_DISPLAY);
			} else if (WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.equals(filterKey) &&
					(filterValue.equals(WorkStatusType.ACTIVE) || filterValue.equals(WorkStatusType.INPROGRESS))) {

				builder.addWhereClause(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.getOperationWithParam(":param" + i))
						.addParam("param" + i, WorkStatusType.ACTIVE);

				if (filterValue.equals(WorkStatusType.ACTIVE)) {
					builder.addWhereClause(" IFNULL(assignedResource.checkedin_flag, false) = false");
					builder.addWhereClause(" ( work.schedule_from > :today OR ( work.schedule_from <= :today AND ( work.checkin_required_flag = true OR work.checkin_call_required = true) ) )");

				} else {
					builder.addWhereClause(" (assignedResource.checkedin_flag = true OR (work.checkin_required_flag = false AND work.checkin_call_required = false " +
							" AND IFNULL(assignedResource.checkedin_flag, false) = false AND work.schedule_from <= :today) )");
				}
				// Exception Case = Payment Pending includes mutiple work_status_type_code
			} else if (WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.equals(filterKey) &&
					(filterValue.equals(WorkStatusType.CANCELLED))) {

				builder.addWhereClause(" work.work_status_type_code IN ('cancelled','cancelledPayPending','cancelledWithPay') ");

				// Exception Case = Payment Pending includes mutiple work_status_type_code
			} else if (WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.equals(filterKey) &&
					(filterValue.equals(WorkStatusType.EXCEPTION))) {

				builder.addWhereClause(" work_sub_status_type.alert = TRUE " +
						" AND work.work_status_type_code IN (" + StringUtils.join(StringUtilities.surround(WorkStatusType.WORK_STATUSES_FOR_DASHBOARD_ALERT_TAB_AND_FILTER, "'"), ",") + ")");

			} else if (WorkSearchDataPagination.FILTER_KEYS.HAS_UNANSWERED_QUESTION.equals(filterKey) ||
					(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.equals(filterKey) && filterValue.equals(WorkStatusType.SENT_WITH_OPEN_QUESTIONS))) {

				builder.addWhereClause(" work.work_status_type_code = :sentStatus")
						.addWhereClause(HAS_UNANSWERED_QUESTION_WHERE_CLAUSE)
						.addParam("sentStatus", WorkStatusType.SENT);

			} else if (WorkSearchDataPagination.FILTER_KEYS.HAS_OPEN_NEGOTIATION.equals(filterKey) ||
					(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.equals(filterKey) && filterValue.equals(WorkStatusType.SENT_WITH_OPEN_NEGOTIATIONS))) {

				builder.addWhereClause(" work.work_status_type_code = :sentStatus")
						.addWhereClause(HAS_OPEN_NEGOTIATION_WHERE_CLAUSE)
						.addParam("sentStatus", WorkStatusType.SENT);

			} else if (WorkSearchDataPagination.FILTER_KEYS.LANE_TYPE_ID.equals(filterKey) &&
					!pagination.getFilters().get(filter).equals(String.valueOf(LaneType.LANE_1.ordinal()))) {

				builder.addWhereInClause(WorkReportPagination.FILTER_KEYS.LANE_TYPE_ID.getColumn(), "lane23Ids",
						Arrays.asList(LaneType.LANE_2.ordinal(), LaneType.LANE_3.ordinal()));
			} else if (StringUtilities.isNotEmpty(filterValue)) {
				switch(filterKey) {
					case BUYER_ID:
					case CLIENT_ID:
					case PROJECT_ID:
						buildWhereInClause(builder, filterKey, filterValue, "param" + i);
						break;
					default:
						builder.addWhereClause(filterKey.getOperationWithParam(":param" + i));
						builder.addParam("param" + i, filterValue);
				}
			}
			i++;
		}

		return builder;
	}
	
	private static void buildWhereInClause(SQLBuilder builder,	WorkSearchDataPagination.FILTER_KEYS filterKey, String filterValue, String paramName) {
		String [] filterVals = filterValue.split(",");
		if (filterVals.length == 0) { return; }
		List<Object> listOfIds = Lists.newArrayList();
		Collections.addAll(listOfIds, filterVals);
		builder.addWhereInClause(filterKey.getColumn(), paramName, listOfIds);
	}
}
