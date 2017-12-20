package com.workmarket.dao.state;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.data.report.work.WorkSubStatusTypeCompanyConfig;
import com.workmarket.data.report.work.WorkSubStatusTypeReportRow;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusType.TriggeredBy;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Repository
public class WorkSubStatusDAOImpl extends AbstractDAO<WorkSubStatusType> implements WorkSubStatusDAO {

	private static final Log logger = LogFactory.getLog(WorkSubStatusDAOImpl.class);

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<WorkSubStatusType> getEntityClass() {
		return WorkSubStatusType.class;
	}

	private static final class WorkSubStatusTypeMapper implements RowMapper<WorkSubStatusType> {

		@Override
		public WorkSubStatusType mapRow(ResultSet rs, int rowNum) throws SQLException {
			WorkSubStatusType worksubStatusType = new WorkSubStatusType();
			worksubStatusType.setId(rs.getLong("substatusId"));
			worksubStatusType.setCode(rs.getString("code"));
			worksubStatusType.setDescription(rs.getString("description"));
			worksubStatusType.setTriggeredBy(TriggeredBy.valueOf(rs.getString("triggered_by")));
			worksubStatusType.setClientVisible(rs.getBoolean("client_visible"));
			worksubStatusType.setResourceVisible(rs.getBoolean("resource_visible"));
			worksubStatusType.setUserResolvable(rs.getBoolean("user_resolvable"));
			worksubStatusType.setActionResolvable(rs.getBoolean("action_resolvable"));
			worksubStatusType.setNotifyClientEnabled(rs.getBoolean("notify_client_enabled"));
			worksubStatusType.setNotifyResourceEnabled(rs.getBoolean("notify_resource_enabled"));
			worksubStatusType.setAlert(rs.getBoolean("alert"));
			worksubStatusType.setNoteRequired(rs.getBoolean("note_required"));
			worksubStatusType.setIncludeInstructions(rs.getBoolean("include_instructions"));
			worksubStatusType.setInstructions(rs.getString("instructions"));
			worksubStatusType.setScheduleRequired(rs.getBoolean("schedule_required"));
			worksubStatusType.setRemoveAfterReschedule(rs.getBoolean("remove_after_reschedule"));
			worksubStatusType.setActive(rs.getBoolean("active"));
			worksubStatusType.setCustomColorRgb(rs.getString("color_rgb"));

			if (rs.getString("dashboard_display_type") != null) {
				worksubStatusType.setDashboardDisplayType(WorkSubStatusTypeCompanySetting.DashboardDisplayType.valueOf(rs.getString("dashboard_display_type")));
			}

			return worksubStatusType;
		}
	}

	@Override
	public WorkSubStatusType findByCode(String code) {
		return (WorkSubStatusType) getFactory().getCurrentSession().createQuery("select ss from work_sub_status_type ss where ss.code = :code and ss.deleted = false")
				.setParameter("code", code).uniqueResult();
	}

	@Override
	public List<WorkSubStatusType> findByCode(String... codes) {
		return getFactory().getCurrentSession().createQuery("select ss from work_sub_status_type ss where ss.code in (:codes) and ss.deleted = false")
				.setParameterList("codes", codes)
				.list();
	}

	@Override
	public WorkSubStatusType findById(long workSubStatusTypeId) {
		return (WorkSubStatusType) getFactory().getCurrentSession().createQuery("select ss from work_sub_status_type ss where ss.id = :workSubStatusTypeId and ss.deleted = false")
				.setParameter("workSubStatusTypeId", workSubStatusTypeId).uniqueResult();
	}

	@Override
	public WorkSubStatusType findByIdAndCompany(long workSubStatusTypeId, long companyId) {
		return (WorkSubStatusType) getFactory().getCurrentSession().createQuery(
				"select ss from work_sub_status_type ss " +
						"where ss.id = :workSubStatusTypeId and ss.deleted = false and ss.company.id = :companyId")
				.setParameter("workSubStatusTypeId", workSubStatusTypeId)
				.setParameter("companyId", companyId)
				.uniqueResult();
	}

	@Override
	public WorkSubStatusType findByCodeAndCompany(String code, long companyId) {
		return (WorkSubStatusType) getFactory().getCurrentSession().getNamedQuery("work_sub_status_type.findByCodeAndCompany")
			.setParameter("code", code)
			.setParameter("companyId", companyId)
			.uniqueResult();
	}

	@Override
	public List<WorkSubStatusType> findAllSubStatusesByCompany(WorkSubStatusTypeFilter filter, long companyId) {
		List<String> triggeredBy = filter.getTriggeredBy();
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("substatus.id AS substatusId", "substatus.code", "substatus.description", "substatus.triggered_by",
				"substatus.client_visible", "substatus.resource_visible", "substatus.user_resolvable", "substatus.action_resolvable", "substatus.notify_client_enabled",
				"substatus.notify_resource_enabled", "substatus.alert", "substatus.note_required", "substatus.include_instructions", "substatus.instructions",
				"substatus.schedule_required", "substatus.remove_after_reschedule", "substatus.active", "setting.color_rgb", "setting.dashboard_display_type")

		.addTable("work_sub_status_type substatus")
				.addJoin("LEFT 	JOIN work_sub_status_type_company_setting setting ON (substatus.id = setting.work_sub_status_type_id" +
						" AND 	setting.company_id = :companyId)")
				.addWhereClause(" ((substatus.client_visible = true AND true = :clientVisible) " +
						" OR (substatus.resource_visible = true AND true = :resourceVisible)) ")
				.addWhereClause("substatus.deleted = false")
				.addOrderBy("substatus.description", "ASC");

		if (filter.getWorkId() != null) {
			/*
			 * If an undeleted association exists between the works work-status and the sub-status, return it.
			 * If no undeleted associations exist between work-statuses and the sub-status, return it
			 */
			builder.addWhereClause("(NOT EXISTS (SELECT scope.id FROM work_sub_status_type_work_status_scope scope"
					+ " WHERE scope.work_sub_status_type_id = substatus.id AND scope.deleted = false) "
					+ " OR EXISTS (SELECT w.id FROM work w INNER JOIN work_sub_status_type_work_status_scope scope ON (scope.work_status_type_code = w.work_status_type_code)"
					+ " WHERE w.id = :workId AND scope.work_sub_status_type_id = substatus.id AND scope.deleted = false))");
			/*
			 * If templates are disabled for this company, return it.
			 * If an undeleted association exists between the works template and the sub-status, return it.
			 * If no undeleted associations exist between templates and the sub-status, return it.
			 */
			builder.addWhereClause("(EXISTS (SELECT company.id FROM company WHERE company.id = :companyId AND company.custom_forms_enabled_flag = false)"
					+ " OR NOT EXISTS (SELECT wsstta.id FROM work_sub_status_type_template_association wsstta"
					+ " WHERE wsstta.work_sub_status_type_id = substatus.id AND wsstta.deleted = false)"
					+ " OR EXISTS (SELECT w.id FROM work w "
					+ " INNER JOIN work_sub_status_type_template_association wsstta ON (w.work_template_id = wsstta.work_template_id)"
					+ " WHERE w.id = :workId AND wsstta.work_sub_status_type_id = substatus.id AND wsstta.deleted = false))");
			builder.addParam("workId", filter.getWorkId());
		}

		if (triggeredBy != null && !triggeredBy.isEmpty()) {
			builder.addWhereClause("substatus.triggered_by IN (" + StringUtils.join(StringUtilities.surround(triggeredBy, "'"), ",") + ")");
		}

		if (!filter.isShowDeactivated()) {
			builder.addWhereClause("substatus.active = true");
		}

		if (!filter.isShowRequiresRescheduleSubStatus()) {
			builder.addWhereClause("substatus.schedule_required = false");
		}

		if (filter.isShowCustomSubStatus() && filter.isShowSystemSubStatus()) {
			builder.addWhereClause("(substatus.company_id = :companyId OR substatus.custom = false) ");
		} else if (filter.isShowCustomSubStatus()) {
			builder.addWhereClause("substatus.custom = true AND substatus.company_id = :companyId ");
		} else {
			builder.addWhereClause("substatus.custom = false ");
		}

		builder.addParam("clientVisible", filter.isClientVisible())
				.addParam("resourceVisible", filter.isResourceVisible())
				.addParam("companyId", companyId);

		logger.debug(filter.toString());
		logger.debug(builder.build());
		return this.jdbcTemplate.query(builder.build(), builder.getParams(), new WorkSubStatusTypeMapper());
	}

	@Override
	public List<WorkSubStatusType> findAllUnresolvedSubStatusTypeWithColorByWork(Long workId) {
		return findAllUnresolvedSubStatusTypeByWork(workId, true);
	}

	@Override
	public List<WorkSubStatusType> findAllUnresolvedSubStatusTypeByWork(Long workId) {
		return findAllUnresolvedSubStatusTypeByWork(workId, false);
	}

	@Override
	public Map<Long, List<WorkSubStatusType>> findAllUnresolvedSubStatusType(List<Long> workIds) {
		if (CollectionUtils.isEmpty(workIds)) return Maps.newHashMap();
		SQLBuilder builder = newUnresolvedSubStatusSQLBuilder();
		builder.addColumn("work.id AS workId")
				.addWhereClause("work.id IN (:workIds)")
				.addParam("workIds", workIds);

		Map<Long, List<WorkSubStatusType>> workSubStatusMap = Maps.newLinkedHashMap();
		List<Map<String, Object>> subStatuses = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row : subStatuses) {
			Long workId = ((Integer) row.get("workId")).longValue();

			WorkSubStatusType subStatusReportRow = new WorkSubStatusType();
			subStatusReportRow.setId((Long) row.get("substatusId"));
			subStatusReportRow.setCode((String) row.get("code"));
			subStatusReportRow.setDescription((String) row.get("description"));
			subStatusReportRow.setAlert((Boolean)row.get("alert"));

			if (!workSubStatusMap.containsKey(workId)) {
				List<WorkSubStatusType> list = Lists.newArrayList();
				workSubStatusMap.put(workId, list);
			}
			workSubStatusMap.get(workId).add(subStatusReportRow);
		}
		return workSubStatusMap;
	}

	@Override
	public Map<String, WorkSubStatusTypeCompanyConfig> findAllWorkSubStatusColorConfiguration() {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("substatus.id AS substatusId", "setting.company_id AS companyId")
				.addColumn("setting.color_rgb")
				.addColumn("setting.dashboard_display_type")
				.addTable("work_sub_status_type substatus")
				.addJoin("INNER	JOIN work_sub_status_type_company_setting setting ON substatus.id = setting.work_sub_status_type_id ")
				.addWhereClause("substatus.deleted = false");

		Map<String, WorkSubStatusTypeCompanyConfig> results = Maps.newHashMap();
		List<Map<String, Object>> subStatuses = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		logger.info("Found " + subStatuses.size() + " labels");
		for (Map<String, Object> row : subStatuses) {
			Long subStatusId = (Long) row.get("substatusId");
			Long companyId = (Long) row.get("companyId");
			WorkSubStatusTypeCompanyConfig setting = new WorkSubStatusTypeCompanyConfig();
			setting.setColorRgb((String) row.get("color_rgb"));
			setting.setCompanyId(companyId);
			if (row.get("dashboard_display_type") == null) {
				setting.setDashboardDisplayType(WorkSubStatusTypeCompanySetting.DashboardDisplayType.SHOW);
			} else {
				setting.setDashboardDisplayType(
					WorkSubStatusTypeCompanySetting.DashboardDisplayType.valueOf((String) row.get("dashboard_display_type")
				));
			}
			String key = subStatusId + "_" + companyId;
			results.put(key, setting);
		}
		return results;
	}

	private SQLBuilder newUnresolvedSubStatusSQLBuilder() {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("substatus.id AS substatusId", "substatus.code", "substatus.description", "substatus.triggered_by",
				"substatus.client_visible", "substatus.resource_visible", "substatus.user_resolvable", "substatus.action_resolvable", "substatus.notify_client_enabled",
				"substatus.notify_resource_enabled", "substatus.alert", "substatus.note_required", "substatus.include_instructions", "substatus.instructions",
				"substatus.schedule_required", "substatus.remove_after_reschedule", "substatus.active")

				.addTable("work")
				.addJoin("INNER JOIN work_sub_status_type_association subStatusWorkAssociation ON subStatusWorkAssociation.work_id = work.id")
				.addJoin("INNER JOIN work_sub_status_type substatus ON substatus.id = subStatusWorkAssociation.work_sub_status_type_id")
				.addWhereClause("work.deleted = false")
				.addWhereClause("work.type = 'W'")
				.addWhereClause("substatus.deleted = false")
				.addWhereClause("subStatusWorkAssociation.deleted = false")
				.addWhereClause("subStatusWorkAssociation.resolved = false")
				.addOrderBy("substatus.description", "ASC");
		return builder;
	}

	private List<WorkSubStatusType> findAllUnresolvedSubStatusTypeByWork(Long workId, boolean includeCustomColor) {
		SQLBuilder builder = newUnresolvedSubStatusSQLBuilder();
		builder.addWhereClause("work.id = :workId")
				.addParam("workId", workId);

		if (includeCustomColor) {
			builder.addColumn("setting.color_rgb")
					.addJoin("LEFT 	JOIN work_sub_status_type_company_setting setting ON (substatus.id = setting.work_sub_status_type_id " +
							" AND	setting.company_id = work.company_id)");
		} else {
			builder.addColumn("'' AS color_rgb");
		}

		builder.addColumn("'" + WorkSubStatusTypeCompanySetting.DashboardDisplayType.SHOW + "' AS dashboard_display_type");

		logger.debug(builder.build());
		return jdbcTemplate.query(builder.build(), builder.getParams(), new WorkSubStatusTypeMapper());
	}

	@Override
	public WorkSubStatusType findSystemWorkSubStatus(String code) {
		return findWorkSubStatusByCompany(code, Constants.WM_COMPANY_ID, false);
	}

	@Override
	public WorkSubStatusType findCustomWorkSubStatus(String code, Long companyId){
		return findWorkSubStatusByCompany(code, companyId, true);
	}

	private WorkSubStatusType findWorkSubStatusByCompany(String code, long companyId, boolean isCustom) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("code", code))
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("subStatusDescriptor.custom", isCustom));

		return (WorkSubStatusType) criteria.uniqueResult();
	}

	@Override
	public Map<Long, List<WorkSubStatusTypeReportRow>> getUnresolvedWorkSubStatusTypeWorkMap(WorkSubStatusTypeFilter filter, List<Long> workIds) {
		Map<Long, List<WorkSubStatusTypeReportRow>> workSubStatusMap = Maps.newLinkedHashMap();
		if (workIds == null || workIds.isEmpty()) {
			return workSubStatusMap;
		}
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("substatus.id AS substatusId", "substatus.code", "substatus.description", "subStatusWorkAssociation.work_id AS workId", "setting.color_rgb", "setting.dashboard_display_type")

		.addTable("work")
				.addJoin("INNER JOIN work_sub_status_type_association subStatusWorkAssociation ON subStatusWorkAssociation.work_id = work.id")
				.addJoin("INNER JOIN work_sub_status_type substatus ON substatus.id = subStatusWorkAssociation.work_sub_status_type_id")
				.addJoin("LEFT 	JOIN work_sub_status_type_company_setting setting ON (substatus.id = setting.work_sub_status_type_id " +
						" AND	setting.company_id = work.company_id)")
				.addWhereClause("work.deleted = false")
				.addWhereClause("work.type = 'W'")
				.addWhereClause(" ((substatus.client_visible = true AND true = :clientVisible) " +
						" OR (substatus.resource_visible = true AND true = :resourceVisible)) ")
				.addWhereClause("substatus.deleted = false")
				.addWhereClause("subStatusWorkAssociation.deleted = false")
				.addWhereClause("subStatusWorkAssociation.resolved = false")
				.addOrderBy("substatus.description", "ASC")
				.addParam("clientVisible", filter.isClientVisible())
				.addParam("resourceVisible", filter.isResourceVisible());

		if (isNotEmpty(workIds)) {
			builder.addWhereClause("work.id IN(" + StringUtils.join(workIds, ", ") + ")");
		}
		logger.debug(builder.build());

		List<Map<String, Object>> subStatuses = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row : subStatuses) {
			Long workId = ((Integer) row.get("workId")).longValue();

			WorkSubStatusTypeReportRow subStatusReportRow = new WorkSubStatusTypeReportRow();
			subStatusReportRow.setWorkSubStausTypeId((Long) row.get("substatusId"));
			subStatusReportRow.setCode((String) row.get("code"));
			subStatusReportRow.setDescription((String) row.get("description"));
			subStatusReportRow.setColorRgb((String) row.get("color_rgb"));

			if (!workSubStatusMap.containsKey(workId)) {
				List<WorkSubStatusTypeReportRow> list = Lists.newArrayList();
				workSubStatusMap.put(workId, list);
			}
			workSubStatusMap.get(workId).add(subStatusReportRow);
		}
		return workSubStatusMap;
	}
}
