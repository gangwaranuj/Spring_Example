package com.workmarket.dao.customfield;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newLinkedHashMap;

@Repository
public class WorkCustomFieldGroupDAOImpl extends AbstractDAO<WorkCustomFieldGroup> implements WorkCustomFieldGroupDAO {

	private static final Log logger = LogFactory.getLog(WorkCustomFieldGroupDAOImpl.class);

	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<WorkCustomFieldGroup> getEntityClass() {
		return WorkCustomFieldGroup.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkCustomFieldGroup> findActiveWorkCustomFieldGroups(Long companyId){
		Query query = getFactory().getCurrentSession().getNamedQuery("active_work_custom_field_group.byCompany");
		query.setLong("companyId", companyId);
		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkCustomFieldGroup> findInactiveWorkCustomFieldGroups(Long companyId){
		Query query = getFactory().getCurrentSession().getNamedQuery("inactive_work_custom_field_group.byCompany");
		query.setLong("companyId", companyId);
		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkCustomFieldGroup> findWorkCustomFieldGroups(Long companyId){
		Query query = getFactory().getCurrentSession().getNamedQuery("work_custom_field_group.byCompany");
		query.setLong("companyId", companyId);
		return query.list();
	}

	@Override
	public WorkCustomFieldGroup findRequiredWorkCustomFieldGroup(Long companyId) {
		return (WorkCustomFieldGroup)getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("required", true))
			.setMaxResults(1)
			.uniqueResult();
	}

	@Override
	public List<WorkCustomFieldGroup> findByWork(Long... workIds) {
		if (workIds.length > 0) {
			SQLBuilder builder = new SQLBuilder();
			builder.setDistinct(true);
			builder.addColumn("work_custom_field_group_id")
				.addTable("work_custom_field_group_association")
				.addWhereClause("work_custom_field_group_association.deleted = false")
				.addWhereClause("work_custom_field_group_association.work_id IN (" + StringUtils.join(workIds, ",")+ ")");

			List<Long> groupIds = jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
			if (groupIds.isEmpty()) {
				return Collections.EMPTY_LIST;
			}
			return get(groupIds.toArray(new Long[groupIds.size()]));
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public Map<Long, String> findClientFieldSetIdsMap(Long companyId) {
		SQLBuilder sqlBuilder = new SQLBuilder();
		sqlBuilder.setDistinct(true)
			.addTable("work_custom_field_group wcfg")
			.addColumn("wcfg.id")
			.addColumn("wcfg.name")
			.addJoin(" LEFT JOIN work_custom_field wcf ON wcf.work_custom_field_group_id = wcfg.id ")
			.addWhereClause(" wcfg.deleted = 0 ")
			.addWhereClause(" wcf.deleted = 0 ")
			.addWhereClause(" wcfg.company_id = :companyId ")
			.addWhereClause(" wcfg.id NOT IN (SELECT DISTINCT wcfg.id from work_custom_field_group wcfg" +
				" INNER JOIN work_custom_field wcf ON wcf.work_custom_field_group_id = wcfg.id" +
				" WHERE wcf.work_custom_field_type_code = 'resource' AND wcf.deleted = false " +
				"AND wcfg.deleted = false AND wcfg.company_id = :companyId) ")
			.addParam("companyId", companyId)
			.addAscOrderBy("wcfg.name");

		List<Map<Long, String>> results = jdbcTemplate.query(sqlBuilder.build(), sqlBuilder.getParams(),
			new RowMapper() {
				public Map<Long, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
					try {
						return Maps.newHashMap(ImmutableMap.<Long, String>builder().put(rs.getLong("wcfg.id"), rs.getString("wcfg.name")).build());
					} catch (Exception e) {
						logger.error(e.getMessage());
						throw new SQLException(e);
					}
				}
			});

		Map<Long, String> groupsMap = newLinkedHashMap();
		for (Map<Long, String> row : results) {
			for (Map.Entry<Long, String> keyVal : row.entrySet()) {
				groupsMap.put(keyVal.getKey(), keyVal.getValue());
			}
		}
		return groupsMap;
	}
}
