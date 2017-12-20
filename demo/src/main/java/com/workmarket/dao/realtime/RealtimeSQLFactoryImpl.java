package com.workmarket.dao.realtime;

import com.workmarket.domains.model.SortDirection;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.realtime.RealtimeReportType;
import com.workmarket.domains.model.realtime.RealtimeServicePagination;
import com.workmarket.thrift.services.realtime.RealtimeFilter;
import com.workmarket.thrift.services.realtime.SortByType;
import com.workmarket.thrift.services.realtime.TimeFilter;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.TimeZone;

@Component
public class RealtimeSQLFactoryImpl implements RealtimeSQLFactory {

	/**
	 * Parameters: 1 - work IDs 2 - sort column 3 - direction
	 */
	private static final String resourceSQL =
			"select  wr.work_id, wr.user_id, wr.id resource_id, wr.work_resource_status_type_code, u.id, u.first_name, u.last_name, u.user_number, \n" + 
			"IFNULL(a.latitude, postal_code.latitude) AS latitude, \n" +
			"IFNULL(a.longitude, postal_code.longitude) AS longitude, \n" +
			"IF(EXISTS(SELECT wq1.id  FROM work_question_answer_pair wq1 WHERE wq1.work_id = w.id AND wq1.questioner_user_id = wr.user_id), 1, 0) AS is_question_asked,\n" + 
			"IF(EXISTS(SELECT wn.expires_on  from work_negotiation wn where  wn.work_id = w.id AND wn.requestor_id = wr.user_id AND  wn.approval_status <> 6 AND wn.expires_on <= :today  ORDER by wn.created_on Limit 1),1,0)  AS is_expired,\n" +
			"(SELECT wn.approval_status  from work_negotiation wn \n" + 
			"where  wn.work_id = w.id AND wn.requestor_id = wr.user_id AND wn.approval_status <> 6 ORDER by wn.created_on Limit 1) AS latest_negotiation_approval_status,\n" + 
			"(SELECT wn.id  from work_negotiation wn \n" + 
			"where wn.work_id = w.id AND wn.requestor_id = wr.user_id AND  wn.approval_status <> 6 ORDER by wn.created_on Limit 1) AS negotiationId,\n" +
			"wr.viewed_on viewed_on,\n" +
			"u.email email,\n" +
			"lane.lane_type_id AS laneType, \n" +
			"c.name company_name,\n" +
			"a.line1 address1,\n" +
			"a.line2 address2,\n" +
			"a.city city,\n" +
			"a.state state,\n" +
			"a.postal_code zip, \n" +
			"a.country country, \n" +
			"wr.created_on date_sent_on, \n" +
			"p.work_phone work_phone, \n" +
			"p.mobile_phone mobile_phone, \n" +
			"wr.view_type view_type, \n" +
			"IF(u.company_id = w.company_id, 1, 0) AS is_employee,\n" +
			//Rating count
			" (SELECT 	COUNT(*) FROM rating \n" +
			" WHERE 	rating_shared_flag = 'Y' AND deleted = 0 \n" + 
			" AND 		rated_user_id = u.id) AS ratingCount, \n" +
			//Rating sum 
			" (SELECT 	IFNULL(SUM(value), 0) FROM rating  \n" +
			" WHERE 	rating_shared_flag = 'Y' AND deleted = 0 \n" + 
			" AND 		rated_user_id = u.id) AS ratingSum \n" +
			"FROM 	work w \n" + 
			"INNER 	JOIN work_resource wr \n" + 
			"ON 	w.id = wr.work_id \n" +		
			"INNER 	JOIN user u \n" + 
			"ON 	u.id = wr.user_id \n" + 
			"INNER 	JOIN profile p \n" + 
			"ON 	u.id = p.user_id \n" + 
			"INNER 	JOIN company c\n" +
			"ON 	u.company_id = c.id\n" +
			"LEFT 	JOIN address a \n" + 
			"ON 	a.id = p.address_id \n" +
			"LEFT  	JOIN postal_code \n" +
			"ON		postal_code.id = p.postal_code_id  \n" +
			"LEFT JOIN lane_association lane \n" +
			"ON (lane.user_id = u.id AND lane.company_id = w.company_id " +
			" AND lane.deleted = false AND lane.approval_status IN (1,5) AND lane.verification_status = 1) " +
			"where 	w.id in (:workIds)" + //PARAMETER!
			"AND 	w.deleted = 0 \n" +
			"AND 	w.type = 'W' \n";
		
		
	private static SQLBuilder createAssignmentSQLPrefix() {
		SQLBuilder builder = new SQLBuilder();
		
		builder.addTable("work w")
				.setDistinct(true)
				.addColumn("w.id")
				.addColumn("w.work_number")
				.addColumn("w.created_on")
				.addColumn("w.schedule_from")
				.addColumn("w.schedule_through")
				.addColumn("w.title")
				.addColumn("c.id as company_id")
				.addColumn("c.name as company_name")
				.addColumn("time_zone.time_zone_id as timeZoneId")
				.addColumn("a.longitude")
				.addColumn("a.latitude")
				.addColumn("waa.last_action_on AS last_modified_on")
				.addColumn("milestones.sent_on")
				.addColumn("modifier.last_name")
				.addColumn("modifier.first_name")
				.addColumn("(SELECT count(q.id) FROM work_question_answer_pair q WHERE q.work_id = w.id) AS question_count")
				.addColumn("w.buyer_user_id creator_id")
				.addColumn("creator.first_name")
				.addColumn("creator.last_name")
				.addColumn("creator.user_number")
				.addColumn("w.due_on due_time")
				.addColumn("proj.id project_id")
				.addColumn("proj.name project_name")
				.addColumn("IF(EXISTS(SELECT id FROM routing_strategy WHERE work_id = w.id AND type = 'group' AND deleted = 0), 1, 0) AS isSentToGroup")
				.addColumn(
					"CASE    w.pricing_strategy_type\n" + 
					"    WHEN 'FLAT' THEN w.flat_price + w.additional_expenses\n" + 
					"    WHEN 'PER_HOUR' THEN (w.per_hour_price * w.max_number_of_hours) + w.additional_expenses\n" + 
					"    WHEN 'PER_UNIT' THEN (w.per_unit_price * w.max_number_of_units) + w.additional_expenses\n" + 
					"    WHEN 'BLENDED_PER_HOUR' THEN ((w.initial_per_hour_price * w.initial_number_of_hours) + (w.additional_per_hour_price * w.max_blended_number_of_hours)) + w.additional_expenses\n" + 
					"    WHEN 'BLENDED_PER_UNIT' THEN ((w.initial_per_unit_price * w.initial_number_of_units) + (w.additional_per_unit_price * w.max_blended_number_of_units)) + w.additional_expenses\n" + 
					"    WHEN 'INTERNAL' THEN 0\n" + 
					"    ELSE NULL\n" + 
					"END AS spend_limit")
				.addColumn(
					"(SELECT count(*) FROM work_negotiation wn \n" + 
					"    WHERE wn.approval_status = 0 AND wn.work_id = w.id\n" + 
					"    AND (wn.expires_on >= :today OR wn.expires_on IS NULL)) " +
					"AS offer_count")
				.addColumn(
					"(SELECT count(*) \n" + 
					"    FROM work_resource wr\n" + 
					"    WHERE wr.work_resource_status_type_code='declined'\n" + 
					"    AND wr.work_id = w.id) " +
					"AS decline_count")
				.addColumn(
					//percent_of_open_offers
					"coalesce((SELECT count(*) FROM work_negotiation wn  \n" + 
					"			    WHERE wn.approval_status = 0 AND wn.work_id = w.id\n" + 
					"			    AND (wn.expires_on >= :today OR wn.expires_on IS NULL)) \n" + 
					"			    / \n" + 
					"                       (select count(id) from work_resource wr where wr.work_id = w.id) , 0) as percent_of_open_offers")
				.addColumn(
					//percent_of_rejections
					"coalesce ((select count(id) from work_resource where work_id = w.id and work_resource_status_type_code = 'declined') \n" + 
					"	/ (select count(id) from work_resource where work_id = w.id) , 0) " +
					"AS percent_of_rejections")
				.addColumn(
					//number_of_unanwered_questions
					"coalesce((select count(id) from work_question_answer_pair where work_id = w.id and answerer_user_id is null), 0) " +
					"AS number_of_unanswered_questions")
				.addColumn(
					//percent_resources_who_viewed_assignment
					"coalesce((select count(id) from work_resource where work_id = w.id and work_resource_status_type_code = 'open' and viewed_on is not null)\n" + 
					"    / (select count(id) from work_resource where work_id = w.id and work_resource_status_type_code = 'open'), 0) " +
					"AS percent_resources_who_viewed_assignment")
				.addColumn("woi_user.first_name working_on_it_first_name")
				.addColumn("woi_user.last_name working_on_it_last_name")
				.addColumn("woi_user.id working_on_it_id")
				.addColumn("woi_user.user_number working_on_it_user_number")
				.addJoin("inner join company c on c.id = w.company_id")
				.addJoin("inner join work_action_audit waa on waa.work_id = w.id")
				.addJoin("inner join user as modifier on waa.modifier_id = modifier.id")
				.addJoin("inner join user creator on creator.id = w.buyer_user_id")
				.addJoin("inner join time_zone on time_zone.id = w.time_zone_id")
				.addJoin("left join work_realtime_working_on_it wrwoi on wrwoi.work_id = w.id and wrwoi.is_open = true")
				.addJoin("left join project_work_association pwa on pwa.work_id = w.id")
				.addJoin("left join project proj on proj.id = pwa.project_id")
				.addJoin("left join address a on w.address_id = a.id")
				.addJoin("left join user woi_user on wrwoi.working_on_user_number = woi_user.user_number")
				.addJoin("left join work_milestones milestones on w.id = milestones.work_id ");
		return builder;
	}
	// @formatter:on

	private SQLBuilder addCompanyIdWhere(SQLBuilder builder, Long companyId) {
		return addCompanyIdWhere(builder, companyId, "w.company_id");
	}
	
	private SQLBuilder addCompanyIdWhere(SQLBuilder builder, Long companyId, String columnName) {
		builder.addWhereClause(columnName, "=", "companyId", companyId);
		return builder;
	}
		
	@Override
	public String getResourceSQL() {
		return resourceSQL;
	}
	
	
	@Override
	public SQLBuilder getMaxUnansweredQuestionsSQL(Long companyId, RealtimeReportType reportType) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("coalesce(max(uq.unanswered_questions), 0)");

		SQLBuilder embeddedSQL = new SQLBuilder();
		embeddedSQL.addColumn("count(wq.id) unanswered_questions")
		.addTable("work_question_answer_pair wq");
		if (reportType == RealtimeReportType.REALTIME_COMPANY) {
			embeddedSQL.addJoin("inner join work w on wq.work_id = w.id and w.company_id = :companyId").addParam("companyId", companyId);
		} else {
			embeddedSQL.addJoin("inner join work w on wq.work_id = w.id");
		}
		addWorkWhere(embeddedSQL);
		embeddedSQL.addGroupColumns("w.id");
		
		builder.addTable("(" + embeddedSQL.build() + ") uq").addParam("companyId", companyId);
		
		return builder;
		
	}

	@Override
	public SQLBuilder getOwnerDropDownSQL(RealtimeReportType reportType, Long companyId) {
		SQLBuilder builder = new SQLBuilder();
		builder
		.setDistinct(true)
		.addColumn("u.first_name")
		.addColumn("u.last_name")
		.addColumn("u.id")
		.addColumn("u.user_number")
		.addTable("user u")
		.addJoin("inner join work w on w.buyer_user_id = u.id").addOrderBy("u.last_name", SortDirection.ASC.name());		
		addWorkWhere(builder);
		if (reportType == RealtimeReportType.REALTIME_COMPANY) {
			addCompanyIdWhere(builder, companyId);
		}
		
		return builder;
	}
	
	@Override
	public SQLBuilder getProjectDropdownSQL(RealtimeReportType reportType, Long companyId) {
		SQLBuilder builder = new SQLBuilder();
		builder.setDistinct(true)
		.addTable("project p")
		.addColumn("p.id id")
		.addColumn("p.name name")
		.addJoin("inner join project_work_association pwa on pwa.project_id = p.id")
		.addJoin("inner join work w on pwa.work_id = w.id");
		addWorkWhere(builder);
		if (reportType == RealtimeReportType.REALTIME_COMPANY) {
			addCompanyIdWhere(builder, companyId);
		}
		builder.addOrderBy("name", "asc");
		return builder;
	}
	
	private void addWorkWhere(SQLBuilder builder) {
		builder.addWhereClause("w.type = 'W'")
			.addWhereClause("w.deleted = 0")
			.addWhereClause("(w.work_status_type_code in ('sent','declined'))");
	}

	@Override
	public SQLBuilder getClientDropdownSQL(RealtimeReportType reportType, Long companyId) {
		SQLBuilder builder = new SQLBuilder();
		builder.setDistinct(true)
			.addColumn("cc.id id")
			.addColumn("cc.name name")
			.addTable("client_company cc");
		if (reportType == RealtimeReportType.REALTIME_COMPANY) {
			builder.addWhereClause("cc.company_id", "=", "companyId", companyId);
		}
		builder.addOrderBy("name", "asc");
		return builder;
	}

	@Override
	public SQLBuilder getNumberOfResultsSql(Long companyId, RealtimeReportType reportType,
			RealtimeFilter filters, RealtimeServicePagination pagination) {

		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("count(distinct w.id)")
			.addTable("work w")
			.addJoin("inner join user creator on creator.id = w.buyer_user_id")
			.addJoin("left join project_work_association pwa on pwa.work_id = w.id")
			.addJoin("left join project proj on proj.id = pwa.project_id");
		addWorkWhere(builder);
		createWhereSQL(filters, builder);
		
		if (reportType == RealtimeReportType.REALTIME_COMPANY) {
			addCompanyIdWhere(builder, companyId);
		}
		return builder;
	}

	private void createOrderBy(RealtimeServicePagination pagination, SQLBuilder builder) {
		String sortColumn = findSortColumnString(pagination);
		String sortDirection = findSortDirectionString(pagination);
		builder.addOrderBy(sortColumn, sortDirection);
	}
	
	private String findSortDirectionString(RealtimeServicePagination pagination) {
		SortDirection order = pagination.isSetSortDirection() ?
				pagination.getSortDirection() :
				SortDirection.DESC;

		// sounds strange, but you have to transpose the order for order age since it goes by seconds
		if (pagination.isSetSortBy() && pagination.getSortBy() == SortByType.ORDER_AGE) {
			order = transposeOrder(order);
		}
		switch (order) {
		case ASC:
			return "ASC";
		case DESC:
			return "DESC";
		}
		return null;
	}

	private SortDirection transposeOrder(SortDirection order) {
		switch (order) {
		case ASC:
			return SortDirection.DESC;
		case DESC:
			return SortDirection.ASC;
		default:
			return null;
		}
	}
	
	private String findSortColumnString(RealtimeServicePagination pagination) {
		if (pagination.isSetSortBy()) {
			return realtimeSortColumnStringMapper(pagination.getSortBy());
		}
		return realtimeSortColumnStringMapper(SortByType.SCHEDULED_TIME);
	}

	private String realtimeSortColumnStringMapper(SortByType realtimeSortColumn) {
		switch (realtimeSortColumn) {
		case DECLINES:
			return SORTS.DECLINES.column;
		case DETAILS:
			return SORTS.DETAILS.column;
		case OFFERS:
			return SORTS.OFFERS.column;
		case ORDER_AGE:
			return SORTS.ORDER_AGE.column;
		case QUESTIONS:
			return SORTS.QUESTIONS.column;
		case SCHEDULED_TIME:
			return SORTS.SCHEDULED_TIME.column;
		case SPEND_LIMIT:
			return SORTS.SPEND_LIMIT.column;
		case TIME_TO_APPOINTMENT:
			return SORTS.SCHEDULED_TIME.column;
		case MODIFIED_TIME:
			return SORTS.MODIFIED_TIME.column;
		}
		return null;
	}

	public enum SORTS {
		WORK_NUMBER("w.work_number"),
		CREATED_ON("w.created_on"),
		ORDER_AGE("milestones.sent_on"),
		SCHEDULED_TIME("w.schedule_from"),
		DETAILS("w.title"),
		SPEND_LIMIT("spend_limit"),
		QUESTIONS("question_count"),
		OFFERS("offer_count"),
		DECLINES("decline_count"),
		COMPANY_ID("company_id"),
		COMPANY_NAME("company_name"),
		MODIFIED_TIME("last_modified_on");

		private final String column;

		private SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	@Override
	public SQLBuilder createRealtimeAssignmentSQL(Long companyId, RealtimeReportType reportType,
			RealtimeFilter filters, RealtimeServicePagination pagination) {
		SQLBuilder builder = createAssignmentSQLPrefix();
		if (reportType == RealtimeReportType.REALTIME_COMPANY) {
			addCompanyIdWhere(builder, companyId);
		}
		addWorkWhere(builder);
		builder.addParam("today",  DateUtilities.formatTodayForSQL());
		createWhereSQL(filters, builder);

		//pagination stuff
		createOrderBy(pagination, builder);
		builder.setStartRow(pagination.getCursorPosition());
		builder.setPageSize(pagination.getPageSize());
		return builder;
	}


	private void createWhereSQL(RealtimeFilter filters, SQLBuilder builder) {
		
		if (filters != null) {
			if (filters.isSetInternalOwnerFilter()) {
				//creator.user_number in (:creatorNumbers)
				builder.addWhereInClause("creator.user_number", "creatorNumbers", filters.getInternalOwnerFilter());
			}
			if (filters.isSetProjectFilter()) {
				builder.addWhereInClause("proj.id", "projectIds", filters.getProjectFilter());
			}
			if (filters.isSetClientFilter()) {
				builder.addWhereInClause("w.client_company_id", "client_companyIds", filters.getClientFilter());
			}
			
			if (filters.isSetNumberOfUnansweredQuestions()) {
				//"and number_of_unanswered_questions >= :unansweredQuestions\n";
				builder.addWhereClause("COALESCE((SELECT count(id) FROM work_question_answer_pair where work_id = w.id and answerer_user_id is null), 0) ", ">=", "unansweredQuestions", filters.getNumberOfUnansweredQuestions());
			}
			if (filters.isSetPercentResourcesWhoViewedAssignment() ) {
				//"and percent_resources_who_viewed_assignment >= :percentViewedAssignment\n";
				builder.addWhereClause("COALESCE((SELECT count(id) FROM work_resource where work_id = w.id and work_resource_status_type_code = 'open' and viewed_on is not null)\n" + 
						"    / (SELECT count(id) from work_resource where work_id = w.id and work_resource_status_type_code = 'open'), 0) ", ">=", "percentViewedAssignment", ((float)filters.getPercentResourcesWhoViewedAssignment()/100.0f));
			}
			if (filters.isSetPercentWithOffers()) {
				//"and percent_of_open_offers >= :percentWithOffers\n";
				builder.addWhereClause("COALESCE((SELECT count(*) FROM work_negotiation wn  \n" + 
						"			    WHERE wn.approval_status = 0 AND wn.work_id = w.id\n" + 
						"			    AND (wn.expires_on >= :today OR wn.expires_on IS NULL)) \n" + 
						"			    / \n" + 
						"                       (SELECT count(id) from work_resource wr where wr.work_id = w.id) , 0)", ">=", "percentWithOffers", ((float)filters.getPercentWithOffers()/100.0f));
				builder.addParam("today",  DateUtilities.formatTodayForSQL());
			}
			if (filters.isSetPercentWithRejections()) {
				//"and  percent_of_rejections >= :percentWithRejections\n";
				builder.addWhereClause("COALESCE ((SELECT count(id) FROM work_resource where work_id = w.id and work_resource_status_type_code = 'declined') \n" + 
						"	/ (SELECT count(id) FROM work_resource where work_id = w.id) , 0) ", ">=", "percentWithRejections", ((float)filters.getPercentWithRejections())/100.0f);
			}
			if (filters.isSetTimeToAppointment()) {
				TimeFilter timeFilter = filters.getTimeToAppointment();
				long appointmentSeconds = timeFilter.getNumberOfSeconds();
				
				Calendar timeSelected = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				timeSelected.add(Calendar.SECOND,  (int)appointmentSeconds);
				
				if (timeFilter.isGreaterThan()) {
					builder.addWhereClause("w.schedule_from", ">=", "scheduleFromDateTime", DateUtilities.formatCalendarForSQL(timeSelected));				

				} else {
					builder.addWhereClause("w.schedule_from", "<=", "scheduleFromDateTime", DateUtilities.formatCalendarForSQL(timeSelected));				
					builder.addWhereClause("w.schedule_from > :today");
					builder.addParam("today",  DateUtilities.formatTodayForSQL());
				}
			}
			if (filters.isSetTimeExpired()) {
				TimeFilter timeFilter = filters.getTimeExpired();
				long expireSeconds = timeFilter.getNumberOfSeconds();
				
				Calendar timeSelected = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				timeSelected.add(Calendar.SECOND,  -(int)expireSeconds);
				
				if (timeFilter.isGreaterThan()) {
					builder.addWhereClause("w.schedule_from", "<=", "scheduleFromDateTime", DateUtilities.formatCalendarForSQL(timeSelected));				

				} else {
					builder.addWhereClause("w.schedule_from", ">=", "scheduleFromDateTime", DateUtilities.formatCalendarForSQL(timeSelected));				
					builder.addWhereClause("w.schedule_from < :today");
					builder.addParam("today",  DateUtilities.formatTodayForSQL());
				}
			}
		}
	}


	@Override
	public SQLBuilder getAssignmentTotalCountSQL(long companyId) {
		SQLBuilder builder = getAssignmentTotalCountSQL();
		addCompanyIdWhere(builder, companyId);
		return builder;
	}


	@Override
	public SQLBuilder getAssignmentTotalCountSQL() {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("work w");
		builder.addColumn("w.id");
		addWorkWhere(builder);
		return builder;
	}


	@Override
	public SQLBuilder getAssignmentTodayTotalSQL(long companyId, String timeZone) {
		SQLBuilder todaySummary = getAssignmentTodayTotalSQL(timeZone);
		addCompanyIdWhere(todaySummary, companyId, "whs.company_id");
		return todaySummary;
	}

	@Override
	public SQLBuilder getAssignmentTodayTotalSQL(String timeZone) {
		Calendar midnightToday = DateUtilities.getMidnightTodayRelativeToTimezone(timeZone);
		Calendar midnightTomorrow = DateUtilities.getMidnightTomorrowRelativeToTimezone(timeZone);
		SQLBuilder todaySummary = new SQLBuilder();

		todaySummary.addColumn("COUNT(whs.work_id) assignment_count")
			.addColumn("whs.work_status_type_code work_status")
			.addTable("time_dimension")
			.addJoin("INNER JOIN work_status_transition whs ON time_dimension.id = whs.date_id ")
			.addWhereClause("whs.work_status_type_code in ('sent', 'active', 'cancelled', 'void', 'draft')")
			.addWhereClause("time_dimension.date BETWEEN :beginDate AND :endDate")
			.addGroupColumns("whs.work_status_type_code")
			.addParam("beginDate", midnightToday)
			.addParam("endDate", midnightTomorrow);

		return todaySummary;
	}
	
	@Override
	public SQLBuilder getAssignmentSentTodayTotalSQL(String timeZone, Long companyId) {
		Calendar midnightToday = DateUtilities.getMidnightTodayRelativeToTimezone(timeZone);
		Calendar midnightTomorrow = DateUtilities.getMidnightTomorrowRelativeToTimezone(timeZone);

		SQLBuilder todaySent = new SQLBuilder();
		todaySent.addColumn("COUNT(whs.work_id) sent_count")
			.addTable("time_dimension")
			.addJoin("INNER JOIN work_status_transition whs ON time_dimension.id = whs.date_id ")
			.addWhereClause("whs.work_status_type_code = :sent")
			.addWhereClause("time_dimension.date BETWEEN :beginDate AND :endDate")
			.addParam("beginDate", midnightToday)
			.addParam("endDate", midnightTomorrow)
			.addParam("sent", WorkStatusType.SENT);
		
		if (companyId != null){
			todaySent.addWhereClause("whs.company_id = :companyId")
				.addParam("companyId", companyId);
		}
		
		return todaySent;
	}

}
