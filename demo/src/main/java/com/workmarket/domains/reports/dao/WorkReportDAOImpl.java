package com.workmarket.domains.reports.dao;

import com.google.api.client.util.Maps;
import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.data.report.work.WorkReportRow;
import com.workmarket.data.report.work.WorkSubStatusTypeReportRow;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.SortDirection;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.reports.util.WorkDashboardReportBuilder;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;
import com.workmarket.service.business.dto.WorkAggregatesDTO;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class WorkReportDAOImpl implements WorkReportDAO {
	private static final Log logger = LogFactory.getLog(WorkReportDAOImpl.class);

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;
	private static final List<String> EXCLUDED_WORK_STATUS_TYPES = Lists.newArrayList();
	static {
		EXCLUDED_WORK_STATUS_TYPES.add(WorkStatusType.PAID);
		EXCLUDED_WORK_STATUS_TYPES.add(WorkStatusType.VOID);
	}

	@Autowired private WorkReportDecoratorDAO workReportDecoratorDAO;
	public static final String SELECT_WORK = 
		"select w.id as work_id\n" +
		"     , w.work_number\n" +
    "     , ( select max(project_id)\n" +
    "           from project_work_association pwa\n" +
    "           join project p\n" +
    "             on p.id = pwa.project_id\n" +
    "          where p.deleted = 0\n" +
    "            and pwa.work_id = w.id\n" +
    "     ) as project_id\n" +
    "     , w.title\n" +
    "     , w.schedule_from\n" +
    "     , w.schedule_through\n" +
    "     , a.city\n" +
    "     , a.state\n" +
    "     , a.postal_code\n" +
    "     , l.id as location_id\n" +
    "     , w.buyer_total_cost\n" +
    "     , w.pricing_strategy_type\n" +
    "     , w.flat_price\n" +
    "     , w.work_status_type_code\n" +
    "     , w.additional_expenses\n" +
    "     , w.bonus\n" +
    "     , w.per_hour_price\n" +
    "     , w.max_number_of_hours\n" +
    "     , w.per_unit_price\n" +
    "     , w.max_number_of_units\n" +
    "     , w.initial_per_hour_price\n" +
    "     , w.initial_number_of_hours\n" +
    "     , w.additional_per_hour_price\n" +
    "     , w.max_blended_number_of_hours\n" +
    "     , w.initial_per_unit_price\n" +
    "     , w.initial_number_of_units\n" +
    "     , w.additional_per_unit_price\n" +
    "     , w.max_blended_number_of_units\n" +
    "     , wr_assigned.checkedin_flag\n" +
    "     , wr_assigned.additional_expenses as assigned_additional_expenses\n" +
    "     , wr_assigned_user.user_number as assigned_user_number\n" +
    "     , wr_assigned_user.first_name as assigned_first_name\n" +
    "     , wr_assigned_user.last_name as assigned_last_name\n" +
    "     , wr_assigned_company.name as assigned_company_name\n" +
    "     , w.checkin_required_flag\n" +
    "     , w.checkin_call_required\n" +
    "     , wsst.id work_sub_status_type_id\n" +
    "     , wsst.code work_sub_status_type_code\n" +
    "     , wsst.description work_sub_status_type_description\n" +
    "     , wsst.alert work_sub_status_type_alert\n" +
    "     , wsstcs.color_rgb work_sub_status_type_color_rgb\n" +
    "     , u_buyer.user_number as buyer_user_number\n" +
    "     , u_buyer.first_name as buyer_first_name\n" +
    "     , u_buyer.last_name as buyer_last_name\n" +
    "     , c_buyer.id as buyer_company_id\n" +
    "     , c_buyer.name as buyer_company_name\n" +
    "     , c_client.id as client_company_id\n" +
    "     , c_client.name as client_company_name\n" +
    "     , i.invoice_number as invoice_number\n" +
    "     , i.payment_date as invoice_payment_date\n" +
    "     , w.modified_on\n" +
    "     , u_modifier.first_name as modifier_first_name\n" +
    "     , u_modifier.last_name as modifier_last_name\n" +
		"     , n.type as negotiation_type\n" +
		"     , n.requestor_is_resource\n" +
		"     , n.approval_status\n" +
    "  FROM work w\n" +
    "  JOIN user u_modifier\n" +
    "    on w.modifier_id = u_modifier.id\n" +
		"  LEFT JOIN work_negotiation n\n" +
		"    ON w.id = n.work_id\n" +
    "  LEFT JOIN user u_buyer\n" +
    "    on u_buyer.id = w.buyer_user_id\n" +
    "  LEFT JOIN company c_buyer\n" +
    "    on c_buyer.id = w.company_id\n" +
    "  LEFT JOIN client_company c_client\n" +
    "    on c_client.id = w.client_company_id\n" +
    "  LEFT JOIN invoice i\n" +
    "    on i.id = w.invoice_id\n" +
    "  LEFT JOIN address a\n" +
    "    ON w.address_id = a.id\n" +
    "  LEFT JOIN location l\n" +
    "    ON w.client_location_id = l.id\n" +
    "  LEFT JOIN (work_resource wr_assigned, user wr_assigned_user, company wr_assigned_company)\n" +
    "    ON wr_assigned.work_id = w.id\n" +
    "   and wr_assigned.assigned_to_work = 1\n" +
    "   and wr_assigned.user_id = wr_assigned_user.id\n" +
    "   and wr_assigned_user.company_id = wr_assigned_company.id\n" +
    "  LEFT JOIN (work_sub_status_type wsst, work_sub_status_type_association wssta, work_sub_status_type_company_setting wsstcs)\n" +
    "    on wssta.work_id = w.id\n" +
    "   and wsst.id = wssta.work_sub_status_type_id\n" +
    "   and wsst.id = wsstcs.work_sub_status_type_id\n" +
    "   and w.company_id = wsstcs.company_id\n" +
    "   and wsst.deleted = 0\n" +
    "   and wsst.client_visible = 1\n" +
		" WHERE 1=1\n";

	@Override
	public WorkSearchDataPagination generateWorkDashboardReportBuyer(Long companyId, Long userId, WorkSearchDataPagination pagination) {
		Assert.notNull(pagination);

		SQLBuilder builder = WorkDashboardReportBuilder.newWorkDashboardReportSQLBuilder(userId, pagination, true, ArrayUtils.EMPTY_STRING_ARRAY);

		builder.addJoin("LEFT JOIN work_resource as resource ON (resource.work_id = work.id AND resource.work_resource_status_type_code = 'open' AND resource.user_id = :userId)");

		if (pagination.isShowAllCompanyAssignments()) {
			builder.addWhereClause(WorkSearchDataPagination.FILTER_KEYS.COMPANY_ID.getColumn() + " = :companyId ");
		} else {
			builder.addWhereClause(WorkSearchDataPagination.FILTER_KEYS.BUYER_ID.getColumn() + " = :userId ");
		}

		builder.addParam("companyId", companyId).addParam("userId", userId);

		return getBuyerDashboard(userId, builder, pagination, true);
	}

	@Override
	public WorkSearchDataPagination generateWorkDashboardReportBuyerForList(Long companyId, Long userId, WorkSearchDataPagination pagination) {
		Assert.notNull(pagination);

		StringBuilder sql = new StringBuilder(SELECT_WORK);
		Map<String, Object> params = Maps.newHashMap();
		String workStatusFilter = null;

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_TYPE.toString())) {
				sql.append("   AND n.type = :negotiationType\n");
				params.put("negotiationType", pagination.getFilters().get(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_TYPE.toString()));
			}
			if(pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_INITIATED_BY_RESOURCE.toString())) {
				sql.append("   AND n.requestor_is_resource = :requestorIsResource\n");
				params.put("requestorIsResource", pagination.getFilters().get(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_INITIATED_BY_RESOURCE.toString()));
			}
			if(pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_APPROVAL_STATUS.toString())) {
				sql.append("   AND n.approval_status = :approvalStatus\n");
				params.put("approvalStatus", pagination.getFilters().get(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_APPROVAL_STATUS.toString()));
			}

			if (pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.toString())) {
				workStatusFilter = pagination.getFilters().get(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.toString());

				if (workStatusFilter.equals(WorkStatusType.ACTIVE) || workStatusFilter.equals(WorkStatusType.INPROGRESS)) {
					sql.append("   AND w.work_status_type_code = 'active'\n");
				} else if (workStatusFilter.equals(WorkStatusType.CANCELLED)) {
					sql.append("   AND w.work_status_type_code IN ('cancelled','cancelledPayPending','cancelledWithPay') \n");
				} else if (workStatusFilter.equals(WorkStatusType.EXCEPTION)) {
				  sql.append("   AND w.work_status_type_code IN (" + StringUtils.join(StringUtilities.surround(WorkStatusType.WORK_STATUSES_FOR_DASHBOARD_ALERT_TAB_AND_FILTER, "'"), ",") + ")\n");
				}
				else {
					sql.append("   AND w.work_status_type_code = :workStatus\n");
					params.put("workStatus", workStatusFilter);
				}
			}
			if (pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.WORK_SUB_STATUS_TYPE_ID.toString())) {
				String workSubStatus = pagination.getFilters().get(WorkSearchDataPagination.FILTER_KEYS.WORK_SUB_STATUS_TYPE_ID.toString());

				sql.append("   AND wsst.code = :workSubStatusId\n");
				params.put("workSubStatusId", workSubStatus);
			}

			if (pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.CLIENT_ID.toString())) {
				String clientCompanyId = pagination.getFilters().get(WorkSearchDataPagination.FILTER_KEYS.CLIENT_ID.toString());

				sql.append("   AND c_client.id = :clientCompanyId\n");
				params.put("clientCompanyId", clientCompanyId);
			}
		}

		sql.append("   AND w.company_id = :companyId\n");
		params.put("companyId", companyId);

		final Map<Long, SolrWorkData> resultMap = Maps.newHashMap();
		final Map<Long, Set<Long>> workSubStatusTypeReportRowMap = Maps.newHashMap();
		final String workStatusFilterValue = workStatusFilter;


		jdbcTemplate.query(sql.toString(), params, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Long workId = rs.getLong("work_id");
				SolrWorkData solrWorkData = resultMap.get(workId);
				if(solrWorkData == null) {
					final String workStatusTypeCode = rs.getString("work_status_type_code");
					solrWorkData = new SolrWorkData();

					String derivedWorkStatusTypeCode = workStatusTypeCode;

					switch(workStatusTypeCode) {
						case "cancelledWithPay" :
						case "cancelledPayPending" :
							derivedWorkStatusTypeCode = "cancelled";
							break;
						case "active" : {
							Boolean checkedIn = rs.getBoolean("checkedin_flag");
							Boolean checkInRequired = rs.getBoolean("checkin_required_flag");
							Boolean checkInCallRequired = rs.getBoolean("checkin_call_required");
							Boolean scheduledBeforeNow = solrWorkData.getScheduleFrom() != null && solrWorkData.getScheduleFrom().compareTo(Calendar.getInstance()) < 0;

							if(checkedIn ||
								(!checkInRequired && !checkInCallRequired && scheduledBeforeNow)) {
								derivedWorkStatusTypeCode = "inprogress";
							}
						}
					}
					solrWorkData.setWorkStatusTypeCode(derivedWorkStatusTypeCode);

					if(workStatusFilterValue != null) {
						if(!derivedWorkStatusTypeCode.equals(workStatusFilterValue)) {
							return;
						}
					}

					solrWorkData.setWorkNumber(rs.getString("work_number"));
					solrWorkData.setProjectId(rs.getLong("project_id"));
					solrWorkData.setTitle(rs.getString("title"));
					solrWorkData.setScheduleFromDate(rs.getDate("schedule_from"));
					solrWorkData.setScheduleThroughDate(rs.getDate("schedule_Through"));
					solrWorkData.setCity(rs.getString("city"));
					solrWorkData.setState(rs.getString("state"));
					solrWorkData.setPostalCode(rs.getString("postal_code"));
					solrWorkData.setLocationId(rs.getLong("location_id"));
					solrWorkData.setPricingType(rs.getString("pricing_strategy_type"));
					solrWorkData.setPaidDate(rs.getDate("invoice_payment_date"));
					solrWorkData.setBuyerFullName(StringUtilities.fullName(rs.getString("buyer_first_name"), rs.getString("buyer_last_name")));
					BigDecimal clientCompanyId = rs.getBigDecimal("client_company_id");
					solrWorkData.setClientCompanyId(clientCompanyId != null ? clientCompanyId.longValue() : null);
					solrWorkData.setClientCompanyName(rs.getString("client_company_name"));
					solrWorkData.setAssignedResourceUserNumber(rs.getString("assigned_user_number"));
					solrWorkData.setAssignedResourceFirstName(rs.getString("assigned_first_name"));
					solrWorkData.setAssignedResourceLastName(rs.getString("assigned_last_name"));
					solrWorkData.setAssignedResourceCompanyName(rs.getString("assigned_company_name"));
					solrWorkData.setModifierFirstName(rs.getString("modifier_first_name"));
					solrWorkData.setModifierLastName(rs.getString("modifier_last_name"));
					solrWorkData.setLastModifiedDate(rs.getDate("modified_on"));

					BigDecimal buyerTotalCost = rs.getBigDecimal("buyer_total_cost");
					solrWorkData.setBuyerTotalCost(buyerTotalCost != null ? buyerTotalCost.doubleValue() : 0);

					double spendLimit = 0;
					BigDecimal bonus = rs.getBigDecimal("bonus");
					BigDecimal additionalExpenses = rs.getBigDecimal("additional_expenses");
					BigDecimal assignedAdditionalExpenses = rs.getBigDecimal("assigned_additional_expenses");

					if(workStatusTypeCode.equals("complete")) {
						spendLimit += assignedAdditionalExpenses != null ? assignedAdditionalExpenses.doubleValue() : 0;
					}
					else {
						spendLimit += additionalExpenses != null ? additionalExpenses.doubleValue() : 0;
					}
					spendLimit += bonus.doubleValue();

					switch(solrWorkData.getPricingType()) {
						case "FLAT" : {
							BigDecimal flatPrice = rs.getBigDecimal("flat_price");
							spendLimit += flatPrice != null ? flatPrice.doubleValue() : 0;
							break;
						}
						case "PER_HOUR" : {
							BigDecimal perHourPrice = rs.getBigDecimal("per_hour_price");
							BigDecimal maxNumberOfHours = rs.getBigDecimal("max_number_of_hours");

							spendLimit += perHourPrice.doubleValue() * maxNumberOfHours.doubleValue();
							break;
						}
						case "PER_UNIT" : {
							BigDecimal perUnitPrice = rs.getBigDecimal("per_unit_price");
							BigDecimal maxNumberOfUnits = rs.getBigDecimal("max_number_of_units");

							spendLimit += perUnitPrice.doubleValue() * maxNumberOfUnits.doubleValue();
							break;

						}
						case "BLENDED_PER_HOUR" : {
							BigDecimal initialPerHourPrice = rs.getBigDecimal("initial_per_hour_price");
							BigDecimal initialNumberOfHours = rs.getBigDecimal("initial_number_of_hours");
							BigDecimal additionalPerHourPrice = rs.getBigDecimal("additional_per_hour_price");
							BigDecimal maxBlendedNumberOfHours = rs.getBigDecimal("max_blended_number_of_hours");

							spendLimit += initialPerHourPrice.doubleValue() * initialNumberOfHours.doubleValue();
							spendLimit += additionalPerHourPrice.doubleValue() * maxBlendedNumberOfHours.doubleValue();
							break;

						}
						case "BLENDED_PER_UNIT" : {
							BigDecimal initialPerUnitPrice = rs.getBigDecimal("initial_per_unit_price");
							BigDecimal initialNumberOfUnits = rs.getBigDecimal("initial_number_of_units");
							BigDecimal additionalPerUnitPrice = rs.getBigDecimal("additional_per_unit_price");
							BigDecimal maxBlendedNumberOfUnits = rs.getBigDecimal("max_blended_number_of_units");

							spendLimit += initialPerUnitPrice.doubleValue() * initialNumberOfUnits.doubleValue();
							spendLimit += additionalPerUnitPrice.doubleValue() * maxBlendedNumberOfUnits.doubleValue();
							break;

						}
						case "INTERNAL" : {}
						default : {
							spendLimit = 0;
						}
					}
					solrWorkData.setSpendLimit(spendLimit);

					resultMap.put(workId, solrWorkData);
					workSubStatusTypeReportRowMap.put(workId, Sets.<Long>newHashSet());
				}

				Set<Long> workSubStausTypeIds = workSubStatusTypeReportRowMap.get(workId);
				Long workSubStatusTypeId = rs.getLong("work_sub_status_type_id");
				if(workSubStatusTypeId > 0 && !workSubStausTypeIds.contains(workSubStatusTypeId)) {
					WorkSubStatusTypeReportRow workSubStatusTypeReportRow = new WorkSubStatusTypeReportRow();
					workSubStatusTypeReportRow.setWorkSubStausTypeId(workSubStatusTypeId);
					workSubStatusTypeReportRow.setCode((String) rs.getString("work_sub_status_type_code"));
					workSubStatusTypeReportRow.setDescription((String) rs.getString("work_sub_status_type_description"));
					workSubStatusTypeReportRow.setColorRgb((String) rs.getString("work_sub_status_type_color_rgb"));
					solrWorkData.getWorkSubStatusTypes().add(workSubStatusTypeReportRow);
					workSubStausTypeIds.add(workSubStatusTypeId);
				}
			}
		});

		ArrayList<SolrWorkData> solrWorkDatas = Lists.newArrayList(resultMap.values());
		final int sortMultiplier = Pagination.SORT_DIRECTION.ASC.equals(pagination.getSortDirection()) ? 1 : -1;
		Collections.sort(solrWorkDatas, new Comparator<SolrWorkData>() {
			@Override
			public int compare(SolrWorkData o1, SolrWorkData o2) {
				if(o1.getScheduleFrom() == null) {
			    return 1 * sortMultiplier;
				}
				return o1.getScheduleFrom().compareTo(o2.getScheduleFrom()) * sortMultiplier;
			}
		});
		pagination.setResults(solrWorkDatas.subList(pagination.getStartRow(), Math.min(pagination.getStartRow() + pagination.getResultsLimit(), solrWorkDatas.size())));
		pagination.setRowCount(resultMap.size());
		return pagination;
	}

	public WorkSearchDataPagination generateWorkDashboardReportAvailable(Long companyId, Long userId, WorkSearchDataPagination pagination) {
		Assert.notNull(pagination);

		SQLBuilder builder = WorkDashboardReportBuilder.newWorkDashboardReportSQLBuilder(userId, pagination, false, WorkDashboardReportBuilder.RESOURCE_IGNORE_FILTERS);

		builder.addJoin("LEFT JOIN work_resource as resource ON (resource.work_id = work.id AND resource.work_resource_status_type_code = 'open' AND resource.user_id = :userId)")

		.addWhereClause("work.work_status_type_code = 'sent'")
				.addWhereClause("( " + WorkSearchDataPagination.FILTER_KEYS.INVITED_RESOURCE_ID.getColumn() + "  = :userId " +
						" AND " + WorkSearchDataPagination.FILTER_KEYS.RESOURCE_ID.getColumn() + " IS NULL)")
				.addParam("companyId", companyId);

		return getResourceDashboard(userId, builder, pagination, true);
	}

	private WorkSearchDataPagination getBuyerDashboard(Long userId, SQLBuilder builder, WorkSearchDataPagination pagination, boolean includeCustomFields) {
		return getDashboard(userId, builder, pagination, includeCustomFields, true);
	}

	private WorkSearchDataPagination getResourceDashboard(Long userId, SQLBuilder builder, WorkSearchDataPagination pagination, boolean includeCustomFields) {
		return getDashboard(userId, builder, pagination, includeCustomFields, false);
	}

	private WorkSearchDataPagination getDashboard(Long userId, SQLBuilder builder, WorkSearchDataPagination pagination, boolean includeCustomFields, boolean isBuyerDashboard) {
		Assert.notNull(pagination);
		
		List<SolrWorkData> reportResults = jdbcTemplate.query(builder.build(), builder.getParams(), new WorkDashboardReportRowMapper());

		String rowCountSql = builder.getHaving().isEmpty() ? builder.buildCount("DISTINCT work.id") : builder.buildCount();
		Integer rowCount = jdbcTemplate.queryForObject(rowCountSql, builder.getParams(), Integer.class);
		pagination.setRowCount(rowCount);

		if (includeCustomFields) {
			boolean showOnInvoice = false;
			String statusFilter = null;

			if (pagination.getFilters() != null) {
				showOnInvoice = pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.INVOICE_ID.toString());
				if (pagination.getFilters().containsKey(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.toString())) {
					statusFilter = pagination.getFilters().get(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.toString());
				}
			}

			CustomFieldReportFilters customFieldReportFilters = new CustomFieldReportFilters();

			if (isBuyerDashboard) {
				if (showOnInvoice) {
					customFieldReportFilters.setShowOnInvoice(true);
				} else {
					customFieldReportFilters.setVisibleToBuyer(true);
					customFieldReportFilters.setShowOnDashboard(true);
				}
			} else if (StringUtils.isNotBlank(statusFilter)) {
				if (WorkStatusType.ACTIVE.equals(statusFilter) ||
					WorkStatusType.INPROGRESS.equals(statusFilter) ||
					WorkStatusType.PAID.equals(statusFilter) ||
					WorkStatusType.PAYMENT_PENDING.equals(statusFilter) ||
					WorkStatusType.COMPLETE.equals(statusFilter)) {
					customFieldReportFilters.setVisibleToResource(true);
					customFieldReportFilters.setShowOnDashboard(true);
				} else if (WorkStatusType.SENT.equals(statusFilter)) {
					customFieldReportFilters.setVisibleToResource(true);
					customFieldReportFilters.setShowOnDashboard(true);
					customFieldReportFilters.setShowOnSentStatus(true);
				}
			}

			if (userId != null) {
				reportResults = workReportDecoratorDAO.addCustomFields(userId, reportResults, customFieldReportFilters);
			}
		}

		WorkSubStatusTypeFilter workSubStatusTypeFilter = new WorkSubStatusTypeFilter();
		workSubStatusTypeFilter.setShowCustomSubStatus(true);
		workSubStatusTypeFilter.setShowDeactivated(true);
		workSubStatusTypeFilter.setClientVisible(isBuyerDashboard);
		workSubStatusTypeFilter.setResourceVisible(!isBuyerDashboard);
		reportResults = workReportDecoratorDAO.addWorkSubStatus(reportResults, workSubStatusTypeFilter);

		pagination.setResults(reportResults);
		return pagination;
	}

	@Override
	public WorkAggregatesDTO generateWorkDashboardStatusAggregateBuyer(Long companyId, Long userId, WorkSearchDataPagination pagination) {
		Assert.notNull(pagination);

		String[] ignoreFilters = new String[] { WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.toString(),
					WorkSearchDataPagination.FILTER_KEYS.WORK_SUB_STATUS_TYPE_ID.toString(),
					WorkSearchDataPagination.FILTER_KEYS.HAS_OPEN_NEGOTIATION.toString(),
					WorkSearchDataPagination.FILTER_KEYS.HAS_UNANSWERED_QUESTION.toString() };

		SQLBuilder builder = WorkDashboardReportBuilder.newWorkDashboardReportAggregateSQLBuilder(userId, pagination, true, ignoreFilters);
		builder.addWhereClause("work.work_status_type_code NOT IN (" + StringUtils.join(StringUtilities.surround(EXCLUDED_WORK_STATUS_TYPES, "'"), ",") + ")");

		if (pagination.isShowAllCompanyAssignments()) {
			builder.addWhereClause(WorkSearchDataPagination.FILTER_KEYS.COMPANY_ID.getColumn() + " = :companyId ");
		} else {
			builder.addWhereClause(WorkSearchDataPagination.FILTER_KEYS.BUYER_ID.getColumn() + " = :userId ");
		}

		builder.addParam("companyId", companyId)
				.addParam("userId", userId);

		builder.addColumn(WorkDashboardReportBuilder.BUYER_WORK_STATUS_CASE_CLAUSE);

		builder.getGroupColumns().add("statusTypeCode");
		builder.getOrderColumns().add("NULL");

		/**
		 * Count assignments group by work status
		 */
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(builder.build(), builder.getParams());

		WorkAggregatesDTO dto = new WorkAggregatesDTO();
		for (Map<String, Object> row : rows) {
			dto.setCountForStatus((String) row.get("statusTypeCode"), ((Long) row.get("count")).intValue());
		}

		/**
		 * Count sent assignments with unanswered questions
		 */
		builder.getColumns().clear();
		builder.addColumn(" COUNT(DISTINCT work.id) as count ")
				.addWhereClause(" work.work_status_type_code = :sentStatus")
				.addWhereClause(WorkDashboardReportBuilder.HAS_UNANSWERED_QUESTION_WHERE_CLAUSE)
				.addParam("sentStatus", WorkStatusType.SENT);

		builder.getGroupColumns().clear();

		dto.setCountForStatus(WorkStatusType.SENT_WITH_OPEN_QUESTIONS, jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Integer.class));
		builder.getWhereClauses().remove(WorkDashboardReportBuilder.HAS_UNANSWERED_QUESTION_WHERE_CLAUSE);

		/**
		 * Count sent assignments with open negotiations
		 */
		builder.addWhereClause(WorkDashboardReportBuilder.HAS_OPEN_NEGOTIATION_WHERE_CLAUSE)
				.addParam("today", DateUtilities.format("yyyy-MM-dd HH:mm", DateUtilities.getCalendarNow()));

		
		dto.setCountForStatus(WorkStatusType.SENT_WITH_OPEN_NEGOTIATIONS, jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Integer.class));

		builder.getWhereClauses().remove(WorkDashboardReportBuilder.HAS_OPEN_NEGOTIATION_WHERE_CLAUSE);
		builder.getWhereClauses().remove(" work.work_status_type_code = :sentStatus");

		/**
		 * Count assignments group by work sub status
		 */
		dto = getWorkSubStatusAggregates(builder, dto);

		return dto;
	}

	private WorkAggregatesDTO getWorkSubStatusAggregates(SQLBuilder builder, WorkAggregatesDTO aggregatesDTO) {
		builder.getColumns().clear();
		builder.getGroupColumns().clear();
		builder
			.addColumn(" COUNT(DISTINCT work.id) as count ")
			.addColumns(
				"work_sub_status_type.id AS workSubStatusId",
				"work_sub_status_type.code", "setting.color_rgb",
				"work_sub_status_type.description",
				"setting.dashboard_display_type"
			)
			.addGroupColumns("work_sub_status_type.id")
			.addOrderBy("work_sub_status_type.description", "ASC");

		String subStatusCondition = "work_sub_status_type.id IS NOT NULL ";
		builder.addWhereClause(subStatusCondition);

		logger.debug("SQL sub statuses : " + builder.build());
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(builder.build(), builder.getParams());

		for (Map<String, Object> row : rows) {
			Long workSubStatusId = (Long) row.get("workSubStatusId");

			WorkSubStatusTypeReportRow subStatusReportRow = new WorkSubStatusTypeReportRow();
			subStatusReportRow.setWorkSubStausTypeId(workSubStatusId);
			subStatusReportRow.setCode((String) row.get("code"));
			subStatusReportRow.setColorRgb((String) row.get("color_rgb"));
			subStatusReportRow.setCount(((Long) row.get("count")).intValue());
			subStatusReportRow.setDescription((String) row.get("description"));

			if (row.get("dashboard_display_type") == null) {
				subStatusReportRow.setDashboardDisplayType(WorkSubStatusTypeCompanySetting.DashboardDisplayType.SHOW);
			} else {
				subStatusReportRow.setDashboardDisplayType((WorkSubStatusTypeCompanySetting.DashboardDisplayType.valueOf((String) row.get("dashboard_display_type"))));
			}

			aggregatesDTO.setCountForWorkSubStatus(workSubStatusId, subStatusReportRow);
		}

		/**
		 * Count assignments with sub status marked as alert
		 */
		builder.getColumns().clear();
		builder.getGroupColumns().clear();
		builder.getWhereClauses().remove(subStatusCondition);

		builder
			.addColumn(" COUNT(DISTINCT work.id) as count ")
			.addWhereClause(
				" work_sub_status_type.alert = TRUE " +
				" AND work.work_status_type_code IN (" +
				StringUtils.join(StringUtilities.surround(WorkStatusType.WORK_STATUSES_FOR_DASHBOARD_ALERT_TAB_AND_FILTER, "'"), ",") + ")"
			);

		logger.debug("SQL Alert sub statuses : " + builder.build());
		aggregatesDTO.setCountForStatus(WorkStatusType.EXCEPTION, jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Integer.class));

		return aggregatesDTO;
	}

	@Override
	public Integer countInprogressAssignmentsWithPaymentTermsByCompany(Long companyId, WorkSearchDataPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		pagination.getFilters().put(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.toString(), WorkStatusType.INPROGRESS);

		SQLBuilder builder = WorkDashboardReportBuilder.newWorkDashboardReportAggregateSQLBuilder(null, pagination, true, ArrayUtils.EMPTY_STRING_ARRAY);
		builder.addWhereClause(WorkSearchDataPagination.FILTER_KEYS.COMPANY_ID.getColumn() + "= :companyId")
				.addWhereClause("work.payment_terms_enabled = true")
				.addWhereClause("work.payment_terms_days > 0")
				.addParam("companyId", companyId);

		return jdbcTemplate.queryForObject(builder.buildCount("DISTINCT work.id"), builder.getParams(), Integer.class);
	}

	@Override
	public Integer countInprogressAssignmentsPrefundByCompany(Long companyId, WorkSearchDataPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		pagination.getFilters().put(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS.toString(), WorkStatusType.INPROGRESS);

		SQLBuilder builder = WorkDashboardReportBuilder.newWorkDashboardReportAggregateSQLBuilder(null, pagination, true, ArrayUtils.EMPTY_STRING_ARRAY);
		builder.addWhereClause(WorkSearchDataPagination.FILTER_KEYS.COMPANY_ID.getColumn() + "= :companyId")
				.addWhereClause("work.payment_terms_enabled = false")
				.addParam("companyId", companyId);

		return jdbcTemplate.queryForObject(builder.buildCount("DISTINCT work.id"), builder.getParams(), Integer.class);
	}

	private void addWhereClause(SQLBuilder builder, Long companyId) {
		builder.addWhereClause(
				WorkSearchDataPagination.FILTER_KEYS.COMPANY_ID.getColumn(),
				WorkSearchDataPagination.FILTER_KEYS.COMPANY_ID.getOperator(),
				"companyId",
				companyId
		);
	}

	@Override
	public WorkReportPagination findAllWorkByWorkNumber(Long companyId, Long userId, String[] workNumbers, WorkReportPagination pagination) {
		if (ArrayUtils.isEmpty(workNumbers)) {
			pagination.setResults(Collections.<WorkReportRow>emptyList());
			pagination.setRowCount(0);
			return pagination;
		}

		SQLBuilder workBuilder = WorkDashboardReportBuilder.newWorkByWorkNumberSQLBuilder(workNumbers, pagination);
		SQLBuilder bundleBuilder = WorkDashboardReportBuilder.newBundlesByWorkNumberSQLBuilder(workNumbers, pagination, WorkDashboardReportBuilder.NOT_FOR_COUNT);
		SQLBuilder bundleCountBuilder = WorkDashboardReportBuilder.newBundlesByWorkNumberSQLBuilder(workNumbers, pagination, WorkDashboardReportBuilder.FOR_COUNT);

		addWhereClause(workBuilder, companyId);
		addWhereClause(bundleBuilder, companyId);
		bundleBuilder.addAscOrderBy("workId");
		addWhereClause(bundleCountBuilder, companyId);

		List<WorkReportRow> results = jdbcTemplate.query(workBuilder.build() + " UNION " + bundleBuilder.build(), workBuilder.getParams(), new RowMapper<WorkReportRow>() {
			@Override
			public WorkReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
				WorkReportRow row = new WorkReportRow();
				row.setWorkId(rs.getLong("workId"));
				row.setWorkNumber(rs.getString("work_number"));
				row.setTitle(rs.getString("title"));
				row.setStatus(rs.getString("status"));

				row.setScheduleFrom(DateUtilities.getCalendarFromDate(rs.getTimestamp("scheduleFrom")));
				row.setScheduleThrough(DateUtilities.getCalendarFromDate(rs.getTimestamp("scheduleThrough")));

				row.setOffSite(rs.getBoolean("offsite"));
				row.setCity(rs.getString("city"));
				row.setState(rs.getString("state"));
				row.setPostalCode(rs.getString("postalCode"));
				row.setCountry(rs.getString("country"));
				row.setLatitude(rs.getDouble("latitude"));
				row.setLongitude(rs.getDouble("longitude"));

				row.setBuyerTotalCost(NumberUtilities.defaultValue(rs.getBigDecimal("buyer_total_cost")).doubleValue());

				BigDecimal spendLimit = rs.getBigDecimal("spend_limit");
				row.setSpendLimit(spendLimit.doubleValue());
				row.setWorkOverridePrice(rs.getBigDecimal("override_price"));

				BigDecimal workFeePercentage = rs.getBigDecimal("workFeePercentage");
				row.setWorkFeePercentage(workFeePercentage);
				row.setSpendLimitWithFee(
					WorkReportRow.calculateSpendLimitWithFee(spendLimit, workFeePercentage)
				);

				row.setTimeZoneId(rs.getString("timeZoneId"));
				row.setType(rs.getString("type"));
				row.setChildCount(rs.getLong("childCount"));

				return row;
			}
		});

		int workCount = jdbcTemplate.queryForObject(workBuilder.buildCount("DISTINCT work.id"), workBuilder.getParams(), Integer.class);
		int bundleCount = jdbcTemplate.queryForObject(bundleCountBuilder.buildCount("DISTINCT work.id"), bundleBuilder.getParams(), Integer.class);

		pagination.setResults(results);
		pagination.setRowCount(workCount + bundleCount);

		return pagination;
	}

	private static final class WorkDashboardReportRowMapper implements RowMapper<SolrWorkData> {

		@Override
		public SolrWorkData mapRow(ResultSet rs, int rowNum) throws SQLException {
			SolrWorkData row = new SolrWorkData();

			row.setWorkId(rs.getLong("workId"));
			row.setWorkNumber(rs.getString("work_number"));
			row.setTitle(rs.getString("title"));
			row.setWorkStatusTypeCode(rs.getString("statusTypeCode"));

			long projectId = rs.getLong("projectId");
			row.setProjectId((projectId != 0L) ? projectId : null);
			row.setProjectName(rs.getString("projectName"));

			row.setScheduleFrom(DateUtilities.getCalendarFromDate(rs.getTimestamp("work.schedule_from")));
			row.setScheduleThrough(DateUtilities.getCalendarFromDate(rs.getTimestamp("work.schedule_through")));

			long clientCompanyId = rs.getLong("clientCompanyId");
			row.setClientCompanyId((clientCompanyId != 0L) ? clientCompanyId : null);
			row.setClientCompanyName(rs.getString("clientCompanyName"));

			row.setOffSite(rs.getBoolean("offsite"));
			row.setCity(rs.getString("address.city"));
			row.setState(rs.getString("state"));
			row.setPostalCode(rs.getString("address.postal_code"));
			row.setCountry(rs.getString("address.country"));
			row.setLatitude(rs.getDouble("address.latitude"));
			row.setLongitude(rs.getDouble("address.longitude"));

			row.setLocationName(rs.getString("location.name"));
			row.setLocationNumber(rs.getString("location.location_number"));

			// Don't show resource info if the assignment is missed or cancelled
			if (row.getWorkStatusTypeCode() != null
					&& !row.getWorkStatusTypeCode().equals(WorkStatusType.CANCELLED)) {
				row.setAssignedResourceFirstName(rs.getString("assignedUser.first_name"));
				row.setAssignedResourceLastName(rs.getString("assignedUser.last_name"));
				row.setAssignedResourceId(rs.getLong("assignedUserId"));
				row.setAssignedResourceUserNumber(rs.getString("assignedUser.user_number"));
				row.setAssignedResourceCompanyName(rs.getString("assignedCompanyName"));
			}
			row.setBuyerFullName(StringUtilities.fullName(rs.getString("buyerFirstName"), rs.getString("buyerLastName")));
			row.setBuyerId(rs.getLong("buyerId"));
			row.setCompanyId(rs.getLong("company_id"));
			row.setAutoPayEnabled(rs.getBoolean("auto_pay_enabled"));

			BigDecimal spendLimit = rs.getBigDecimal("spend_limit");
			if (spendLimit != null) {
				row.setSpendLimit(
					WorkReportRow.calculateSpendLimitWithFee(spendLimit, rs.getBigDecimal("workFeePercentage"))
				);
				row.setPricingType(rs.getString("pricing_strategy_type"));
			}

			row.setConfirmed(rs.getBoolean("confirmed_flag"));
			row.setResourceConfirmationRequired(rs.getBoolean("resource_confirmation_flag"));

			row.setAmountEarned(rs.getDouble("amount_earned"));
			row.setPaidOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("paid_on")));
			row.setInvoiceId(rs.getLong("invoiceId"));
			row.setInvoiceNumber(rs.getString("invoice_number"));
			row.setDueOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("dueDate")));
			row.setPaymentTermsDays(rs.getInt("work.payment_terms_days"));
			row.setPaymentTermsEnabled(rs.getBoolean("work.payment_terms_enabled"));

			row.setBuyerTotalCost(NumberUtilities.defaultValue(rs.getBigDecimal("buyer_total_cost")).doubleValue());
			row.setCompanyName(rs.getString("companyName"));
			row.setCreatedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("work.created_on")));
			row.setSentOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("milestones.sent_on")));

			Calendar modifiedOn = DateUtilities.getCalendarFromDate(rs.getTimestamp("last_modified_on"));
			if (modifiedOn != null) row.setLastModifiedDate(modifiedOn.getTime());
			row.setModifierFirstName(rs.getString("modifierFirstName"));
			row.setModifierLastName(rs.getString("modifierLastName"));

			row.setTimeZoneId(rs.getString("timeZoneId"));
			row.setAssignedResourceAppointmentFrom(DateUtilities.getCalendarFromDate(rs.getTimestamp("assignedResource.appointment_from")));
            row.setAssignedResourceAppointmentThrough(DateUtilities.getCalendarFromDate(rs.getTimestamp("assignedResource.appointment_through")));

			row.setAssignToFirstResource(rs.getBoolean("assign_to_first_resource"));
			row.setApplied(rs.getBoolean("resourceApplied"));
			row.setApplicationsPending(rs.getBoolean("applicationsPending"));

			return row;
		}
	}
}
