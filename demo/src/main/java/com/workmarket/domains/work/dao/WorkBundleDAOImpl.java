package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.infra.dto.WorkBundleSuggestionDTO;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WorkBundleDAOImpl extends AbstractDAO<WorkBundle> implements WorkBundleDAO {

	@Qualifier("readOnlyJdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	private static final String FIND_BY_CHILD_ID_SQL =
		" SELECT p.id, p.title " +
		" FROM work c " +
		" INNER join work p ON c.parent_id = p.id " +
		" WHERE c.id = :childId ";

	@SuppressWarnings("unchecked")
	@Override
	public List<Work> findAllInByWorkNumbers(List<String> workNumbers) {
		Assert.notNull(workNumbers);
		return (List<Work>) getFactory().getCurrentSession().getNamedQuery("workBundle.findAllInByWorkNumbers")
				.setParameterList("workNumbers", workNumbers).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Work> findAllInByIds(List<Long> ids) {
		Assert.notNull(ids);
		return (List<Work>) getFactory().getCurrentSession().getNamedQuery("workBundle.findAllInByIds")
				.setParameterList("ids", ids).list();
	}

	@Override
	public WorkBundleDTO findByChildId(Long childId) {
		List<WorkBundleDTO> workBundleDTOs = jdbcTemplate.query(FIND_BY_CHILD_ID_SQL, new MapSqlParameterSource("childId", childId), new RowMapper<WorkBundleDTO>() {
			@Override
			public WorkBundleDTO mapRow(ResultSet resultSet, int i) throws SQLException {
				WorkBundleDTO workBundleDTO = new WorkBundleDTO();
				workBundleDTO.setId(resultSet.getLong("id"));
				workBundleDTO.setTitle(resultSet.getString("title"));
				return workBundleDTO;
			}
		});
		if (CollectionUtils.isNotEmpty(workBundleDTOs)) {
			return workBundleDTOs.get(0);
		}
		return null;
	}

	@Override
	protected Class<WorkBundle> getEntityClass() {
		return WorkBundle.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkBundleSuggestionDTO> suggest(String prefix, Long userId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumns(
				"DISTINCT wb.id AS id",
				"wb.workNumber AS workNumber",
				"wb.title AS title",
				"CONCAT(wb.buyer.firstName, ' ', wb.buyer.lastName) AS internalOwner"
			)
			.addTable("workBundle wb")
			.addTable("user u")
			.addJoin("JOIN wb.company c")
			.addWhereClause("u.company = c and u.id = :userId")
			.addWhereClause("LOWER(wb.title) LIKE :prefix")
			.addWhereClause("wb.deleted = false")
			.addWhereClause("wb.workStatusType <> 'complete'")
			.addWhereClause("wb.workStatusType <> 'void'")
			.addAscOrderBy("wb.title");

		Query query = getFactory().getCurrentSession().createQuery(builder.build());
		query.setParameter("userId", userId);
		query.setParameter("prefix", StringUtilities.processForLike(prefix.toLowerCase()));
		query.setMaxResults(10);

		return query.setResultTransformer(Transformers.aliasToBean(WorkBundleSuggestionDTO.class)).list();
	}

	@Override
	public boolean isAssignmentBundle(String workNumber) {
		final SQLBuilder builder = new SQLBuilder()
			.addColumn("id")
			.addTable("work")
			.addWhereClause("type", SQLOperator.EQUALS, "type", "B")
			.addWhereClause("work_number", SQLOperator.EQUALS, "workNumber", workNumber);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Integer.class).size() > 0;
	}
}
