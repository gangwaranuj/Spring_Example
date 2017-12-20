package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@Repository
public class AbstractRequirementDAOImpl extends AbstractDAO<AbstractRequirement> implements AbstractRequirementDAO {

	private static final String MANDATORY_REQUIREMENT_COUNT_SQL =
		"SELECT COUNT(*) FROM requirement r " +
		"INNER JOIN requirement_set rs " +
		"ON rs.id = r.requirement_set_id " +
		"INNER JOIN work_requirement_set_association wrsa " +
		"ON wrsa.requirement_set_id = rs.id " +
		"WHERE wrsa.work_id = :workId AND r.mandatory = 1 ";

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<AbstractRequirement> getEntityClass() {
		return AbstractRequirement.class;
	}

	@Override
	public int getMandatoryRequirementCountByWorkId(Long workId) {
		Assert.notNull(workId);

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("workId", workId);

		return jdbcTemplate.queryForObject(MANDATORY_REQUIREMENT_COUNT_SQL, params, Integer.class);
	}
}
