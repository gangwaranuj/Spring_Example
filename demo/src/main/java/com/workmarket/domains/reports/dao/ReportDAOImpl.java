package com.workmarket.domains.reports.dao;

import com.google.common.collect.Lists;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.data.report.work.WorkReportRow;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.reports.util.WorkDashboardReportBuilder;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Repository
public class ReportDAOImpl implements ReportDAO {

	private static final Log logger = LogFactory.getLog(ReportDAOImpl.class);

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired private WorkReportDecoratorDAO workReportDecoratorDAO;

	@Override
	public WorkReportPagination generateWorkReportBuyer(Long companyId, Long userId, WorkReportPagination pagination, boolean includeCustomFields) {
		Assert.notNull(pagination);

		SQLBuilder builder = newWorkReportSQLBuilder(pagination, true, false);

		builder.addParam("companyId", companyId)
				.addParam("userId", userId);
		return processWorkReportResultSet(builder, pagination, includeCustomFields, userId, companyId, true, false);
	}

	@Override
	public WorkReportPagination generateBudgetReportBuyer(Long companyId, Long userId, WorkReportPagination pagination, boolean includeCustomFields) {
		Assert.notNull(pagination);

		SQLBuilder builder = newWorkReportSQLBuilder(pagination, true, true);

		builder
			.addParam("companyId", companyId)
			.addParam("userId", userId);
		return processWorkReportResultSet(builder, pagination, includeCustomFields, userId, companyId, true, true);
	}

	@Override
	public WorkReportPagination generateEarningsReportResource(Long companyId, Long userId, WorkReportPagination pagination, boolean includeCustomFields) {
		Assert.notNull(pagination);

		SQLBuilder builder = newWorkReportSQLBuilder(pagination, false, false);

		builder
			.addParam("companyId", companyId)
			.addParam("userId", userId);

		return processWorkReportResultSet(builder, pagination, includeCustomFields, userId, companyId, false, false);
	}

	private SQLBuilder newWorkReportSQLBuilder(WorkReportPagination pagination, boolean isBuyer, boolean isBudget) {
		SQLBuilder builder = new SQLBuilder();

		builder
			.addColumns("DISTINCT work.id AS workId", "work.title", "work.work_number", "time_zone.time_zone_id AS timeZoneId",
					"work.schedule_from", "work.schedule_through", "work.schedule_is_range_flag", "work.created_on",
					"work.work_price", "work.buyer_fee AS buyer_fee", "work.company_id", "buyer.id AS buyerId",
					"buyer.last_name AS buyerLastName", "buyer.first_name AS buyerFirstName",
					"client_company.name AS clientCompanyName",
					"address.line1", "address.line2", "address.city", "state.short_name as state", "address.postal_code", "address.country", "address.latitude", "address.longitude",
					"assignedUser.id", "assignedUser.last_name", "assignedUser.first_name", "assignedResource.hours_worked",
					"location.name AS locationName", "location.location_number", "c.name AS assignedResourceCompany",
					"milestones.complete_on", "milestones.closed_on", "milestones.sent_on", "milestones.paid_on",
					"work.payment_terms_days", "work.payment_terms_enabled", "work.due_on",
					"invoice.id as invoiceId", "invoice.invoice_number", "IF(work.company_id = :companyId, 1, 0) AS isOwner",
					"(work.buyer_fee + work.work_price) AS totalCost",
					"(SELECT bundle.invoice_number FROM invoice_summary_detail " +
							"INNER JOIN invoice bundle ON bundle.id = invoice_summary_detail.invoice_summary_id " +
							"WHERE invoice.id = invoice_summary_detail.invoice_id AND bundle.type = 'bundle') AS invoiceSummaryNumber",
					"assignedResource.appointment_from AS assignedResourceAppointmentDate",
					"IF (work.account_pricing_type_code = 'transactional', COALESCE(legacy_work_fee_configuration.percentage, COALESCE(account_register.current_work_fee_percentage,0)), 0) AS workFeePercentage",
					"work.sales_tax_collected_flag AS taxCollected", "work.sales_tax_rate AS taxRate", "work.pricing_strategy_type",

					(isBuyer ? WorkDashboardReportBuilder.BUYER_WORK_STATUS_CASE_CLAUSE : WorkDashboardReportBuilder.WORK_STATUS_CASE_CLAUSE),
					WorkDashboardReportBuilder.SPEND_LIMIT_CASE_CLAUSE,
					WorkDashboardReportBuilder.HOURS_BUDGETED_CASE_CLAUSE);

				if (isBuyer) {
					builder.addColumn("IF (work.work_status_type_code = 'complete', \n" +
							"CASE 	work.pricing_strategy_type \n" +
							"WHEN 	'FLAT' THEN work.flat_price + COALESCE(assignedResource.additional_expenses, 0) \n" +
							"WHEN 	'PER_HOUR' THEN (work.per_hour_price * COALESCE(assignedResource.hours_worked,0)) + COALESCE(assignedResource.additional_expenses, 0)\n" +
							"WHEN 	'PER_UNIT' THEN (work.per_unit_price * COALESCE(assignedResource.units_processed,0)) + COALESCE(assignedResource.additional_expenses, 0) \n" +
							"WHEN 	'BLENDED_PER_HOUR' THEN \n" +
							" 	(work.initial_per_hour_price * IF(COALESCE(assignedResource.hours_worked,0) > work.initial_number_of_hours, work.initial_number_of_hours, assignedResource.hours_worked)) \n" +
							" + (work.additional_per_hour_price * IF(COALESCE(assignedResource.hours_worked,0) > work.initial_number_of_hours, COALESCE(assignedResource.hours_worked,0) - work.initial_number_of_hours, 0)) \n" +
							" + COALESCE(assignedResource.additional_expenses, 0) \n" +
							"WHEN 'BLENDED_PER_UNIT' THEN \n" +
							"	(work.initial_per_unit_price * IF(COALESCE(assignedResource.units_processed,0) > work.initial_number_of_units, work.initial_number_of_units, assignedResource.units_processed)) \n" +
							" + (work.additional_per_unit_price * IF(COALESCE(assignedResource.units_processed,0) > work.initial_number_of_units, COALESCE(assignedResource.units_processed,0) - work.initial_number_of_units, 0)) \n" +
							" + COALESCE(assignedResource.additional_expenses, 0) \n" +
							"WHEN 	'INTERNAL' THEN 0 \n" +
							"ELSE 	NULL \n" +
							"END, NULL) AS proposed_spend_limit");
					if (isBudget) {
						builder.addColumns("work_negotiation.requested_on",
								"work_negotiation.approver_id", "work_negotiation.approved_on", "work_negotiation.type", "work_negotiation.additional_expenses",
								"work_negotiation.bonus", "work_negotiation.approval_status", "work_negotiation.pricing_strategy_type", "work_negotiation.flat_price", "work_negotiation.per_unit_price",
								"work_negotiation.per_hour_price", "work_negotiation.max_number_of_hours", "work_negotiation.max_number_of_units", "note.note_content",
								"approver.first_name", "approver.last_name", "work_negotiation.id");
					}
				} else {
					builder.addColumn(" 0 AS proposed_spend_limit");
				}

				builder
					.addTable("work")
					.addWhereClause(" work.deleted = 0")
					.addWhereClause(" work.type = 'W'")
					.addParam("today", DateUtilities.formatTodayForSQL())
					.addParam("isBuyer", isBuyer);

		buildJoinClause(builder, pagination, isBudget);

		applySorts(builder, pagination);

		applyFilters(builder, pagination, isBuyer);

		return builder;
	}

	private WorkReportPagination processWorkReportResultSet(
		SQLBuilder builder, WorkReportPagination pagination, boolean includeCustomFields,
		Long userId, Long companyId, final boolean isBuyer, final boolean isBudget) {

		Assert.notNull(pagination);
		RowMapper<WorkReportRow> mapper = new RowMapper<WorkReportRow>() {
			public WorkReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
				WorkReportRow row = new WorkReportRow();

				row.setWorkId(rs.getLong("workId"));
				row.setTitle(rs.getString("title"));
				row.setWorkNumber(rs.getString("work.work_number"));
				row.setStatus(rs.getString("statusTypeCode"));
				row.setScheduleFrom(DateUtilities.getCalendarFromDate(rs.getTimestamp("work.schedule_from")));
				row.setScheduleThrough(DateUtilities.getCalendarFromDate(rs.getTimestamp("work.schedule_through")));
				row.setCompletedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("milestones.complete_on")));
				row.setClosedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("milestones.closed_on")));
				row.setCreatedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("work.created_on")));
				if (isBudget){
					row.setExpenseCreateDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("work_negotiation.requested_on")));
					if (rs.getString("work_negotiation.type").equals(AbstractWorkNegotiation.EXPENSE)) {
						row.setExpenseAmount(rs.getDouble("work_negotiation.additional_expenses"));
					} else if (rs.getString("work_negotiation.type").equals(AbstractWorkNegotiation.BONUS)) {
						row.setExpenseAmount(rs.getDouble("work_negotiation.bonus"));
					} else {
						String pricingStrategyType = rs.getString("work_negotiation.pricing_strategy_type");
						if(pricingStrategyType.equals(PricingStrategyType.FLAT.name())) {
							row.setExpenseAmount(rs.getDouble("work_negotiation.flat_price"));
						} else if (pricingStrategyType.equals(PricingStrategyType.PER_UNIT.name()))  {
							row.setExpenseAmount(rs.getDouble("work_negotiation.per_unit_price") * rs.getDouble("work_negotiation.max_number_of_units"));
						} else if (pricingStrategyType.equals(PricingStrategyType.PER_HOUR.name())) {
							row.setExpenseAmount(rs.getDouble("work_negotiation.per_hour_price") * rs.getDouble("work_negotiation.max_number_of_hours"));
						} else {
							row.setExpenseAmount(0.0);
						}
					}
					row.setExpenseActionDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("work_negotiation.approved_on")));

					String approverFirstName = rs.getString("approver.first_name");
					if(approverFirstName != null){
						row.setExpenseApproverName(approverFirstName + " " + rs.getString("approver.last_name"));
					} else {
						row.setExpenseApproverName("-");
					}
					row.setExpenseNote(rs.getString("note.note_content"));
					if(rs.getLong("work_negotiation.approval_status") == 1) {
						row.setExpenseApprovalStatus("approved");
					} else if (rs.getLong("work_negotiation.approval_status") == 2) {
						row.setExpenseApprovalStatus("declined");
					}
					row.setExpenseType(rs.getString("work_negotiation.type"));
				}

				row.setState(rs.getString("state"));
				row.setCity(rs.getString("city"));
				row.setPostalCode(rs.getString("postal_code"));
				row.setCountry(rs.getString("country"));
				row.setLatitude(rs.getDouble("latitude"));
				row.setLongitude(rs.getDouble("longitude"));
				row.setOwner(rs.getBoolean("isOwner"));

				row.setBuyerId(rs.getLong("buyerId"));
				row.setBuyerFullName(StringUtilities.fullName(rs.getString("buyerFirstName"), rs.getString("buyerLastName")));

				if (row.getStatus() != null) {
					if (!row.getStatus().equals("missed")) {
						row.setAssignedResourceFirstName(rs.getString("assignedUser.first_name"));
						row.setAssignedResourceLastName(rs.getString("assignedUser.last_name"));
						row.setAssignedResourceCompanyName(rs.getString("assignedResourceCompany"));
						row.setHoursWorked(rs.getDouble("hours_worked"));
					}
					if (row.isOwner() || (!WorkStatusType.SENT.equals(row.getStatus()))) {
						row.setClientCompanyName(rs.getString("clientCompanyName"));
						row.setAddress1(rs.getString("line1"));
						row.setAddress2(rs.getString("line2"));
					}
				}

				row.setLocationName(rs.getString("locationName"));
				row.setLocationNumber(rs.getString("location.location_number"));
				row.setSpendLimit(rs.getDouble("spend_limit"));
				row.setPricingType(rs.getString("pricing_strategy_type"));

				row.setWorkMarketFee(isBuyer ? rs.getBigDecimal("buyer_fee") : BigDecimal.ZERO);
				row.setHoursBudgeted(rs.getDouble("hoursBudgeted"));

				row.setWorkPrice(rs.getBigDecimal("work_price"));
				row.setTimeZoneId(rs.getString("timeZoneId"));

				row.setSentOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("milestones.sent_on")));
				row.setDueOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("work.due_on")));
				row.setPaymentTermsDays(rs.getInt("work.payment_terms_days"));
				row.setPaymentTermsEnabled(rs.getBoolean("work.payment_terms_enabled"));

				row.setPaidOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("paid_on")));
				row.setInvoiceId(rs.getLong("invoiceId"));
				row.setInvoiceNumber(rs.getString("invoice_number"));
				row.setWorkTotalCost(isBuyer ? rs.getBigDecimal("totalCost") : BigDecimal.ZERO);
				row.setInvoiceSummaryNumber(rs.getString("invoiceSummaryNumber"));
				row.setAssignedResourceAppointmentDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("assignedResourceAppointmentDate")));

				BigDecimal pendingApprovalCost = isBuyer ? rs.getBigDecimal("proposed_spend_limit") : BigDecimal.ZERO;
				BigDecimal workFeePercentage = rs.getBigDecimal("workFeePercentage");
				if (pendingApprovalCost != null && workFeePercentage != null) {
					BigDecimal fee = workFeePercentage.divide(BigDecimal.valueOf(100)).multiply(pendingApprovalCost);
					pendingApprovalCost = pendingApprovalCost.add(fee);
				}
				row.setPendingApprovalCost(pendingApprovalCost);
				row.setTaxCollected(rs.getBoolean("taxCollected"));
				row.setTaxRate(rs.getBigDecimal("taxRate"));
				return row;
			}
		};

		Integer rowCount;
		String countParam = (isBuyer && isBudget)? "work_negotiation.id" : "DISTINCT work.id";
		rowCount = jdbcTemplate.queryForObject(builder.buildCount(countParam), builder.getParams(), Integer.class);

		List<WorkReportRow> reportResults = Lists.newArrayList();

		if (rowCount > 0) {
			reportResults = jdbcTemplate.query(builder.build(), builder.getParams(), mapper);

			if (includeCustomFields) {
				CustomFieldReportFilters customFieldReportFilters = new CustomFieldReportFilters();

				if (isBuyer) {
					customFieldReportFilters.setVisibleToBuyer(true);
				} else {
					customFieldReportFilters.setVisibleToResource(true);
				}

				reportResults = workReportDecoratorDAO.addCustomFields(userId, companyId, reportResults, customFieldReportFilters);
			}

			WorkSubStatusTypeFilter workSubStatusTypeFilter = new WorkSubStatusTypeFilter();
			workSubStatusTypeFilter.setShowCustomSubStatus(true);
			workSubStatusTypeFilter.setShowDeactivated(true);
			workSubStatusTypeFilter.setClientVisible(isBuyer);
			workSubStatusTypeFilter.setResourceVisible(!isBuyer);

			reportResults = workReportDecoratorDAO.addWorkSubStatus(reportResults, workSubStatusTypeFilter);
		}

		pagination.setRowCount(rowCount);
		pagination.setResults(reportResults);

		return pagination;
	}

	private SQLBuilder buildJoinClause(SQLBuilder builder, WorkReportPagination pagination, boolean isBudget) {

		builder
				.addJoin(" INNER JOIN time_zone ON time_zone.id = work.time_zone_id ")
				.addJoin(" INNER JOIN work_milestones milestones ON milestones.work_id = work.id ")
				.addJoin(" INNER JOIN account_register on work.company_id = account_register.company_id ");

		if(isBudget){
			builder
					.addJoin(" INNER JOIN work_negotiation ON work.id = work_negotiation.work_id ")
					.addJoin(" INNER JOIN note ON work_negotiation.note_id = note.id ")
					.addJoin(" LEFT JOIN user as approver on approver.id = work_negotiation.approver_id ");
		}

		builder
				.addJoin(" LEFT JOIN client_company ON work.client_company_id = client_company.id", pagination.hasFilter(WorkReportPagination.FILTER_KEYS.CLIENT_ID))
				.addJoin(" LEFT JOIN location ON work.client_location_id = location.id", false)
				.addJoin(" LEFT JOIN address ON work.address_id = address.id", false)
				.addJoin(" LEFT JOIN state ON address.state = state.id", false)
				.addJoin(" LEFT JOIN user as buyer ON work.buyer_user_id = buyer.id", pagination.hasFilter(WorkReportPagination.FILTER_KEYS.BUYER_ID))
				.addJoin(" LEFT JOIN work_resource  as assignedResource ON (work.id = assignedResource.work_id and assignedResource.assigned_to_work = true)")
				.addJoin(" LEFT JOIN user as assignedUser ON assignedResource.user_id = assignedUser.id ")
				.addJoin(" LEFT JOIN company c ON c.id = assignedUser.company_id", false)
				.addJoin(" LEFT JOIN project_work_association project ON project.work_id = work.id", pagination.hasFilter(WorkReportPagination.FILTER_KEYS.PROJECT_ID))
				.addJoin(" LEFT JOIN lane_association lane ON (lane.user_id = assignedUser.id AND lane.company_id = work.company_id AND lane.deleted = false) ", pagination.hasFilter(WorkReportPagination.FILTER_KEYS.LANE_TYPE_ID))
				.addJoin(" LEFT JOIN work_sub_status_type_association sub_status " +
						" ON (sub_status.work_id = work.id  AND sub_status.deleted = FALSE AND sub_status.resolved = FALSE )", pagination.hasFilter(WorkReportPagination.FILTER_KEYS.WORK_SUB_STATUS_CODE))
				.addJoin(" LEFT JOIN work_sub_status_type ON (work_sub_status_type.id = sub_status.work_sub_status_type_id AND work_sub_status_type.deleted = FALSE)", pagination.hasFilter(WorkReportPagination.FILTER_KEYS.WORK_SUB_STATUS_CODE))
				.addJoin(" LEFT JOIN invoice ON (invoice.id = work.invoice_id AND invoice.deleted = 0) ", false)
				.addJoin(" LEFT JOIN work_resource as resource ON (resource.work_id = work.id AND resource.work_resource_status_type_code IN ('open', 'cancelled'))", false)
				.addJoin(" LEFT JOIN legacy_work_fee_configuration ON legacy_work_fee_configuration.work_id = work.id", false);

		return builder;
	}

	private SQLBuilder applySorts(SQLBuilder builder, WorkReportPagination pagination) {

		if (pagination.getSortColumn() != null) {
			builder.addOrderBy(WorkReportPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else
			builder.addOrderBy(WorkReportPagination.SORTS.WORK_ID.getColumn(), WorkReportPagination.SORT_DIRECTION.ASC.toString());

		builder.addLimitClause(pagination.getStartRow(), pagination.getResultsLimit(), pagination.isLimitMaxRows());

		return builder;
	}

	private SQLBuilder applyFilters(SQLBuilder builder, WorkReportPagination pagination, boolean isBuyer) {

		Iterator<String> it = pagination.getFilters().keySet().iterator();

		int i = 0;
		while (it.hasNext()) {

			String filter = it.next();

			if (WorkReportPagination.FILTER_KEYS.valueOf(filter).equals(WorkReportPagination.FILTER_KEYS.START_DATE) ||
					WorkReportPagination.FILTER_KEYS.valueOf(filter).equals(WorkReportPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_FROM) ||
					WorkReportPagination.FILTER_KEYS.valueOf(filter).equals(WorkReportPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_FROM)) {

				builder.addWhereClause(WorkReportPagination.FILTER_KEYS.valueOf(filter).getColumn() + " >= :param" + i);
				builder.addParam("param" + i, DateUtilities.getCalendarFromISO8601(pagination.getFilters().get(filter)));
			} else if (WorkReportPagination.FILTER_KEYS.valueOf(filter).equals(WorkReportPagination.FILTER_KEYS.END_DATE) ||
					WorkReportPagination.FILTER_KEYS.valueOf(filter).equals(WorkReportPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_TO) ||
					WorkReportPagination.FILTER_KEYS.valueOf(filter).equals(WorkReportPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_TO)) {

				builder.addWhereClause(WorkReportPagination.FILTER_KEYS.valueOf(filter).getColumn() + " <= :param" + i);
				builder.addParam("param" + i, DateUtilities.getCalendarFromISO8601(pagination.getFilters().get(filter)));

			} else if (WorkReportPagination.FILTER_KEYS.COMPANY_ID.equals(WorkReportPagination.FILTER_KEYS.valueOf(filter))) {
				continue;
			} else if (WorkReportPagination.FILTER_KEYS.RESOURCE_ID.equals(WorkReportPagination.FILTER_KEYS.valueOf(filter))) {
				continue;
			} else if (WorkReportPagination.FILTER_KEYS.FROM_PRICE.equals(WorkReportPagination.FILTER_KEYS.valueOf(filter))) {
				builder.addHavingClause(WorkReportPagination.FILTER_KEYS.valueOf(filter).getColumn() + " >= :param" + i);
				builder.addParam("param" + i, new Double(pagination.getFilters().get(filter)));
			} else if (WorkReportPagination.FILTER_KEYS.TO_PRICE.equals(WorkReportPagination.FILTER_KEYS.valueOf(filter))) {
				builder.addHavingClause(WorkReportPagination.FILTER_KEYS.valueOf(filter).getColumn() + " <= :param" + i);
				builder.addParam("param" + i, new Double(pagination.getFilters().get(filter)));
			} else if (WorkReportPagination.FILTER_KEYS.LANE_TYPE_ID.equals(WorkReportPagination.FILTER_KEYS.valueOf(filter)) &&
					!pagination.getFilters().get(filter).equals(String.valueOf(LaneType.LANE_1.ordinal()))) {

					builder.addWhereInClause(WorkReportPagination.FILTER_KEYS.LANE_TYPE_ID.getColumn(), "lane23Ids",
							Arrays.asList(LaneType.LANE_2.ordinal(), LaneType.LANE_3.ordinal()));
			} else {
				builder.addWhereClause(WorkReportPagination.FILTER_KEYS.valueOf(filter).getColumn() + " = :param" + i);
				builder.addParam("param" + i, pagination.getFilters().get(filter));
			}
			i++;
		}

		return generateWorkStatusFilter(builder, pagination, isBuyer);
	}

	private SQLBuilder generateWorkStatusFilter(SQLBuilder builder, WorkReportPagination pagination, boolean isBuyer) {
		List<String> statusFilter = Lists.newArrayList();
		if (pagination.getWorkStatusTypeFilter() != null) {
			statusFilter.addAll(pagination.getWorkStatusTypeFilter());
		}
		List<String> filters = Lists.newArrayList();

		if (statusFilter != null && !statusFilter.isEmpty()) {

			if (statusFilter.contains(WorkStatusType.CANCELLED)) {
				String resourceCancelledFilter = " work.work_status_type_code IN ('cancelled','cancelledPayPending','cancelledWithPay') ";
				statusFilter.remove(WorkStatusType.CANCELLED);
				filters.add(resourceCancelledFilter);
			}

			// If active and In-progress, just leave active since In-progress is not a real status code
			if (statusFilter.contains(WorkStatusType.INPROGRESS) && statusFilter.contains(WorkStatusType.ACTIVE)) {
				statusFilter.remove(WorkStatusType.INPROGRESS);
			}
			// If active or In-progress, include the special condition for each
			else if (statusFilter.contains(WorkStatusType.INPROGRESS) || statusFilter.contains(WorkStatusType.ACTIVE)) {

				String activeStatusFilter = "( " + WorkReportPagination.FILTER_KEYS.WORK_STATUS.getColumn() + " = :activeStatus ";
				builder.getParams().addValue("activeStatus", WorkStatusType.ACTIVE);

				if (statusFilter.contains(WorkStatusType.INPROGRESS)) {
					String inprogressFilter = activeStatusFilter +
							" AND (assignedResource.checkedin_flag = true OR (work.checkin_required_flag = false AND work.checkin_call_required = false " +
							" AND IFNULL(assignedResource.checkedin_flag, false) = false AND work.schedule_from <= sysdate()) ) )";
					statusFilter.remove(WorkStatusType.INPROGRESS);
					filters.add(inprogressFilter);
				}

				if (statusFilter.contains(WorkStatusType.ACTIVE)) {
					String activeFilter = activeStatusFilter +
							" AND (IFNULL(assignedResource.checkedin_flag, false) = false " +
							" AND ( work.schedule_from > sysdate() OR ( work.schedule_from <= sysdate() AND (work.checkin_required_flag = true OR work.checkin_call_required = true) ) ) ) )";
					statusFilter.remove(WorkStatusType.ACTIVE);
					filters.add(activeFilter);
				}
			}

			// Exception is not a real status code either, so we need to include the special condition
			if (statusFilter.contains(WorkStatusType.EXCEPTION)) {
				String exceptionFilter = " ( work_sub_status_type.alert = TRUE " +
						" AND work.work_status_type_code IN (" + StringUtils.join(StringUtilities.surround(WorkStatusType.WORK_STATUSES_FOR_DASHBOARD_ALERT_TAB_AND_FILTER, "'"), ",") + ") )";
				statusFilter.remove(WorkStatusType.EXCEPTION);
				filters.add(exceptionFilter);
			}

			/*
			 * For all the other status add a regular equality condition considering if the report is run as buyer or resource
			 */
			if (!statusFilter.isEmpty()) {
				filters.add(" work.work_status_type_code IN (" + StringUtils.join(StringUtilities.surround(statusFilter, "'"), ",") + ")");
			}

			// Join all the conditions with an OR
			String defaultFilter = null;
			if (!filters.isEmpty()) {
				defaultFilter = "(" + pagination.getDefaultFilter(isBuyer) + " AND (" + StringUtils.join(filters, " OR ") + " ))";
			}

			if (StringUtils.isNotBlank(defaultFilter)) {
				builder.addWhereClause(defaultFilter);
			}
		}
		// Build the ALL filter
		else {
			builder.addWhereClause(pagination.getDefaultFilter(isBuyer));
			if (!isBuyer) {
				builder.addWhereClause(" work.work_status_type_code IN ('cancelled','cancelledPayPending','cancelledWithPay', 'paid')");
			}
		}

		return generateWorkSubStatusFilter(builder, pagination);
	}

	private SQLBuilder generateWorkSubStatusFilter(SQLBuilder builder, WorkReportPagination pagination) {
		List<String> subStatusFilter = Lists.newArrayList();
		if (pagination.getWorkSubStatusTypeFilter() != null) {
			subStatusFilter.addAll(pagination.getWorkSubStatusTypeFilter());
		}

		if (subStatusFilter != null && !subStatusFilter.isEmpty()) {
			builder.addWhereClause(WorkReportPagination.FILTER_KEYS.WORK_SUB_STATUS_CODE.getColumn() + " IN (" + StringUtils.join(StringUtilities.surround(subStatusFilter, "'"), ",") + ")");
		}

		return builder;
	}
}
