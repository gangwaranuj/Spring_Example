package com.workmarket.dao.customfield;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.domains.reports.model.CustomReportCustomFieldDTO;
import com.workmarket.domains.reports.model.CustomReportCustomFieldGroupDTO;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class WorkCustomFieldDAOImpl extends AbstractDAO<WorkCustomField> implements WorkCustomFieldDAO {

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<WorkCustomField> getEntityClass() {
		return WorkCustomField.class;
	}

	@SuppressWarnings("unchecked")
	public List<WorkCustomField> findRequiredBuyerFieldsForCustomFieldGroup(Long customFieldGroupId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("workCustomFieldGroup.id", customFieldGroupId))
				.add(Restrictions.eq("requiredFlag", true))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("workCustomFieldType.code", WorkCustomFieldType.OWNER))
				.addOrder(Order.asc("position"))
				.addOrder(Order.asc("id"))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<WorkCustomField> findAllFieldsForCustomFieldGroup(Long customFieldGroupId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("workCustomFieldGroup.id", customFieldGroupId))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.asc("position"))
				.addOrder(Order.asc("id"))
				.list();
	}

	@Override
	public Map<Long, List<CustomFieldReportRow>> getWorkCustomFieldsMap(Long userId, Long companyId, CustomFieldReportFilters filters) {
		Map<Long, List<CustomFieldReportRow>> customFieldsMap = Maps.newLinkedHashMap();

		if (!filters.hasWorkFilter()) {
			return customFieldsMap;
		}

		SQLBuilder builder = buildWorkCustomFieldSQL(userId, companyId, filters, false);
		List<Map<String, Object>> customFields = jdbcTemplate.queryForList(builder.build(), builder.getParams());

		for (Map<String, Object> row : customFields) {
			Long workId = ((Integer) row.get("workId")).longValue();

			if (!customFieldsMap.containsKey(workId)) {
				List<CustomFieldReportRow> list = Lists.newArrayList();
				customFieldsMap.put(workId, list);
			}

			customFieldsMap.get(workId).add(new CustomFieldReportRow()
				.setFieldId(((Integer) row.get("fieldId")).longValue())
				.setFieldName((String) row.get("name"))
				.setFieldValue((String) row.get("value"))
				.setFieldDefaultValue((String) row.get("default_value"))
				.setShowOnDashboard((Boolean) row.get("show_on_dashboard"))
				.setVisibleToResource((Boolean) row.get("visible_to_resource_flag"))
				.setShowOnSent((Boolean) row.get("show_on_sent_status")));
		}

		return customFieldsMap;
	}

	@Override
	public Map<Long, List<CustomFieldReportRow>> getWorkCustomFieldsMap(CustomFieldReportFilters filters) {
		Map<Long, List<CustomFieldReportRow>> customFieldsMap = Maps.newLinkedHashMap();
		if (!filters.hasWorkFilter()) {
			return customFieldsMap;
		}
		SQLBuilder builder = buildWorkCustomFieldSQL(null, null, filters, true);

		List<Map<String, Object>> customFields = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row : customFields) {
			Long workId = ((Integer) row.get("workId")).longValue();

			if (!customFieldsMap.containsKey(workId)) {
				List<CustomFieldReportRow> list = Lists.newArrayList();
				customFieldsMap.put(workId, list);
			}
			customFieldsMap.get(workId).add(new CustomFieldReportRow()
					.setFieldId(((Integer)row.get("fieldId")).longValue())
					.setFieldName((String) row.get("name"))
					.setFieldValue((String) row.get("value"))
					.setShowOnDashboard((Boolean)row.get("show_on_dashboard"))
					.setVisibleToResource((Boolean)row.get("visible_to_resource_flag")));

		}
		return customFieldsMap;
	}

	@Override
	public List<CustomFieldReportRow> findAllWorkCustomFields(Long userId, Long companyId, CustomFieldReportFilters filters) {
		List<CustomFieldReportRow> results = Lists.newArrayList();
		if (!filters.hasWorkFilter()) {
			return results;
		}

		SQLBuilder builder = buildWorkCustomFieldSQL(userId, companyId, filters, false);
		builder.getColumns().clear();
		builder.addColumns("field.id AS fieldId", "field.name", "field.show_on_dashboard", "field.visible_to_resource_flag");

		List<Map<String, Object>> customFields = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row : customFields) {
			results.add(new CustomFieldReportRow()
					.setFieldId(((Integer)row.get("fieldId")).longValue())
					.setFieldName((String) row.get("name"))
					.setFieldValue(StringUtils.EMPTY)
					.setShowOnDashboard((Boolean)row.get("show_on_dashboard"))
					.setVisibleToResource((Boolean)row.get("visible_to_resource_flag")));
		}
		return results;
	}

	@Override
	public List<CustomReportCustomFieldGroupDTO> findCustomReportCustomFieldGroupsForCompanyAndReport(Long companyId, Long reportId) {
		SQLBuilder builder = new SQLBuilder();

		builder.setDistinct(true)
			.addColumns("wcf.id workCustomFieldId", "wcf.name workCustomFieldName", "wcf.deleted workCustomFieldDeleted",
					"wcfg.id workCustomFieldGroupId", "wcfg.name workCustomFieldGroupName", "wcfg.deleted workCustomFieldGroupDeleted", "rcf.id reportingCriteriaId")
			.addTable("work_custom_field wcf")
			.addJoin("INNER JOIN work_custom_field_group wcfg ON wcfg.id=wcf.work_custom_field_group_id")
			.addJoin("LEFT OUTER JOIN reporting_criteria_filtering rcf ON rcf.field_value=wcf.id AND rcf.property = 'workCustomField.id' AND rcf.reporting_criteria_id = :reportId")
			.addWhereClause("wcfg.company_id = :companyId")
			.addParam("reportId", reportId)
			.addParam("companyId", companyId)
			.addAscOrderBy("wcfg.id, wcf.name");

		final List<CustomReportCustomFieldGroupDTO> customFieldGroups = new ArrayList<>();
		jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<CustomReportCustomFieldDTO>() {
			@Override
			public CustomReportCustomFieldDTO mapRow(ResultSet rs, int i) throws SQLException {
				CustomReportCustomFieldDTO dto = new CustomReportCustomFieldDTO();
				dto.setId(rs.getLong("workCustomFieldId"));
				dto.setDeleted(rs.getBoolean("workCustomFieldDeleted"));
				dto.setName(rs.getString("workCustomFieldName"));
				dto.setReportingCriteriaFilter(rs.getString("reportingCriteriaId") != null);
				final long workCustomFieldGroupId = rs.getLong("workCustomFieldGroupId");
				CustomReportCustomFieldGroupDTO group = Iterables.find(customFieldGroups, new Predicate<CustomReportCustomFieldGroupDTO>() {
					@Override
					public boolean apply(CustomReportCustomFieldGroupDTO customReportCustomFieldGroupDTO) {
						return customReportCustomFieldGroupDTO.getId() == workCustomFieldGroupId;
					}
				}, null);

				if (group == null) {
					group = new CustomReportCustomFieldGroupDTO();
					group.setId(rs.getLong("workCustomFieldGroupId"));
					group.setName(rs.getString("workCustomFieldGroupName"));
					group.setDeleted(rs.getBoolean("workCustomFieldGroupDeleted"));
					group.setCustomFields(new ArrayList<CustomReportCustomFieldDTO>());
					customFieldGroups.add(group);
				}

				group.getCustomFields().add(dto);

				return dto;
			}
		});

		return customFieldGroups;
	}

	private SQLBuilder buildWorkCustomFieldSQL(Long userId, Long companyId, CustomFieldReportFilters filters, boolean isBuyerVisible) {
		SQLBuilder builder = new SQLBuilder();
		builder.setDistinct(true);
		builder.addColumns("work.id AS workId", "field.id AS fieldId", "field.name", "field.show_on_dashboard", "field.visible_to_resource_flag", "field.show_on_sent_status")
				.addTable("work")
				.addJoin("INNER JOIN work_custom_field_group_association association ON work.id = association.work_id")
				.addJoin("INNER JOIN work_custom_field_group g ON g.id = association.work_custom_field_group_id")
				.addJoin("INNER JOIN work_custom_field field ON g.id = field.work_custom_field_group_id")
				.addJoin("INNER JOIN work_custom_field_saved saved ON saved.work_custom_field_id = field.id " +
						" AND saved.work_custom_field_group_association_id = association.id");

		builder.addWhereClause(" g.deleted = false")
				.addWhereClause(" field.deleted = false")
				.addWhereClause(" IFNULL(saved.value, '') <> ''")
				.addParam("userId", userId)
				.addParam("companyId", companyId)
				.addAscOrderBy("g.id")
				.addAscOrderBy("field.position")
				.addAscOrderBy("field.id");

		builder.addColumn("field.default_value");
		if (userId == null && companyId == null) {
			if (isBuyerVisible) {
				builder.addColumn("COALESCE(saved.value, field.default_value) AS value");
			} else {
				builder.addColumn("IF(field.visible_to_resource_flag = true, saved.value, '') AS value");
			}
		} else if (userId == null) {
			builder.addColumn("IF((work.company_id = :companyId), saved.value, IF(field.visible_to_resource_flag = true, saved.value, '')) AS value");
		} else if (companyId == null) {
			builder.addColumn("IF((work.buyer_user_id = :userId), saved.value, IF(field.visible_to_resource_flag = true, saved.value, '')) AS value");
		} else {
			builder.addColumn("IF((work.buyer_user_id = :userId OR work.company_id = :companyId), saved.value, IF(field.visible_to_resource_flag = true, saved.value, '')) AS value");
		}

		if (filters != null) {
			if (filters.hasShowOnDashboardFilter()) {
				builder.addWhereClause(" field.show_on_dashboard = :showOnDashboard")
						.addParam("showOnDashboard", filters.getShowOnDashboard());
			}
			if (filters.hasShowOnInvoiceFilter()) {
				builder.addWhereClause(" field.show_on_invoice = :showOnInvoice")
						.addParam("showOnInvoice", filters.getShowOnInvoice());
			}
			if (filters.hasVisibleToBuyerFilter()) {
				builder.addWhereClause(" field.visible_to_owner_flag = :visibleToBuyer ")
						.addParam("visibleToBuyer", filters.getVisibleToBuyer());
			}
			if (filters.hasVisibleToResourceFilter()) {
				builder.addWhereClause(" field.visible_to_resource_flag = :visibleToResource ")
						.addParam("visibleToResource", filters.getVisibleToResource());
			}
			if (filters.hasShowOnSentStatusFilter()) {
				builder.addWhereClause(" field.show_on_sent_status = :showOnSentStatus ")
						.addParam("showOnSentStatus", filters.getShowOnSentStatus());
			}
			if (filters.hasWorkFilter()) {
				builder.addWhereClause(" work.id IN (" + StringUtils.join(filters.getWorkIds(), ",") + ")");
			}
			if (filters.hasShowOnEmailFilter()) {
				builder.addWhereClause(" field.show_in_assignment_email = :showOnEmail ")
						.addParam("showOnEmail", filters.getShowOnEmail());
			}
			if (filters.hasCustomFieldIdFilter()) {
				builder.addWhereClause(" saved.work_custom_field_id IN (" + StringUtils.join(filters.getWorkCustomFieldIds(), ",") + ")");
			}
		}
		return builder;
	}

}