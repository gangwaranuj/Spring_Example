package com.workmarket.dao.requirement;

import com.google.common.collect.ImmutableMap;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.test.TestRequirement;
import com.workmarket.utility.sql.SQLBuilder;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class TestRequirementDAOImpl extends AbstractDAO<TestRequirement> implements TestRequirementDAO {
	@Resource(name = "readOnlyJdbcTemplate") private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<?> getEntityClass() {
		return TestRequirement.class;
	}

	@Override
	public List<Long> findSentWorkIdsWithTestRequirement(Long assessmentId, Long userId) {
		SQLBuilder builder =
			new SQLBuilder()
				.addTable("work w")
				.addColumns("w.id")
				.addJoin("LEFT JOIN work_requirement_set_association wrs on wrs.work_id = w.id")
				.addJoin("LEFT JOIN requirement_set rs on rs.id = wrs.requirement_set_id")
				.addJoin("LEFT JOIN requirement r on r.requirement_set_id = rs.id")
				.addJoin("JOIN test_requirement tr on tr.id = r.id")
				.addWhereClause("w.work_status_type_code = 'sent'")
				.addWhereClause("tr.test_id = :assessmentId");
		return jdbcTemplate.queryForList(builder.build(), ImmutableMap.of("assessmentId", assessmentId), Long.class);
	}

	@Override
	public List<Long> findSentWorkIdsWithTestRequirementFromGroup(final Long assessmentId, final Long userId) {
		SQLBuilder builder =
			new SQLBuilder()
				.addTable("work w")
				.addColumn("DISTINCT w.id")
				.addJoin("LEFT JOIN work_to_company_association wca ON wca.work_id = w.id")
				.addJoin("LEFT JOIN work_vendor_invitation_to_group_association wviga ON wviga.work_vendor_invitation_id = wca.id")
				.addJoin("LEFT JOIN user_group_requirement_set_association ugrsa ON ugrsa.user_group_id = wviga.user_group_id")
				.addJoin("LEFT JOIN requirement_set rs ON rs.id = ugrsa.requirement_set_id")
				.addJoin("LEFT JOIN requirement r ON r.requirement_set_id = rs.id")
				.addJoin("JOIN test_requirement tr ON tr.id = r.id")
				.addWhereClause("w.work_status_type_code = 'sent'")
				.addWhereClause("tr.test_id = :assessmentId");
		return jdbcTemplate.queryForList(builder.build(), ImmutableMap.of("assessmentId", assessmentId), Long.class);
	}
}
