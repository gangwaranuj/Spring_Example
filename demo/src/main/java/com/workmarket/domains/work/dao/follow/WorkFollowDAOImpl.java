package com.workmarket.domains.work.dao.follow;

import com.google.common.base.Optional;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.service.business.dto.WorkFollowDTO;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class WorkFollowDAOImpl extends AbstractDAO<WorkFollow> implements WorkFollowDAO {

	@Qualifier("readOnlyJdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public Optional<WorkFollow> getWorkFollow(Long workId, Long userId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("workFollow.byWorkIdUserId");
		query.setLong("workId", workId);
		query.setLong("userId", userId).setMaxResults(1);

		return Optional.fromNullable((WorkFollow) query.uniqueResult());
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkFollow> getFollowers(Long workId) {
		return getFactory().getCurrentSession().getNamedQuery("workFollow.findFollowers")
				.setParameter("workId", workId).list();
	}

	@Override
	public List<WorkFollowDTO> getWorkFollowDTOs(Long workId) {
		final SQLBuilder sql = new SQLBuilder()
			.addColumn("f.id, u.id as user_id, u.first_name, u.last_name")
			.addTable("work_follow f")
			.addJoin("INNER JOIN user u on f.user_id = u.id")
			.addWhereClause("f.deleted", SQLOperator.EQUALS, "deleted", Boolean.FALSE)
			.addWhereClause("f.work_id", SQLOperator.EQUALS, "workId", workId)
			.addAscOrderBy("u.first_name")
			.addAscOrderBy("u.last_name");

		return jdbcTemplate.query(sql.build(), sql.getParams(), new RowMapper<WorkFollowDTO>() {
			@Override
			public WorkFollowDTO mapRow(ResultSet resultSet, int i) throws SQLException {
				WorkFollowDTO follower = new WorkFollowDTO();
				follower.setId(resultSet.getLong("f.id"));
				follower.setUserId(resultSet.getLong("user_id"));
				follower.setFollowerFirstName(resultSet.getString("u.first_name"));
				follower.setFollowerLastName(resultSet.getString("u.last_name"));
				return follower;
			}
		});
	}

	@Override
	public List<WorkFollow> getFollowers(String workNumber) {
		Assert.hasText(workNumber);
		return getFactory().getCurrentSession().getNamedQuery("workFollow.findFollowersByWorkNumber")
				.setParameter("workNumber", workNumber).list();
	}

	@Override
	public boolean isFollowingWork(Long workId, Long userId) {
		final SQLBuilder sqlBuilder = new SQLBuilder()
			.addColumn("f.id")
			.addTable("work_follow f")
			.addWhereClause("f.deleted", SQLOperator.EQUALS, "deleted", Boolean.FALSE)
			.addWhereClause("f.work_id", SQLOperator.EQUALS, "workId", workId)
			.addWhereClause("f.user_id", SQLOperator.EQUALS, "userId", userId)
			.addLimitClause(0, 1, true);

		return jdbcTemplate.queryForObject(sqlBuilder.buildCount("f.id"), sqlBuilder.getParams(), Integer.class) > 0;
	}

	@Override
	protected Class<WorkFollow> getEntityClass() {
		return WorkFollow.class;
	}
}
