package com.workmarket.dao.summary.user;

import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.summary.user.BlockedUserHistorySummary;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Repository
public class BlockedUserHistorySummaryDAOImpl extends AbstractDAO<BlockedUserHistorySummary> implements BlockedUserHistorySummaryDAO {

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<BlockedUserHistorySummary> getEntityClass() {
		return BlockedUserHistorySummary.class;
	}

	@Override
	public Map<Long, Integer> countDistinctBlockingCompaniesByUser(Calendar fromDate, List<Long> userIds) {
		Assert.notNull(fromDate);
		Map<Long, Integer> blocksMap = Maps.newHashMap();
		SQLBuilder sqlBuilder = new SQLBuilder();
		sqlBuilder.addColumns("COUNT(distinct block_user_history_summary.blocking_company_id) count", "block_user_history_summary.user_id")
				.addTable("block_user_history_summary")
				.addJoin("INNER JOIN 	time_dimension ON time_dimension.id = block_user_history_summary.date_id")
				.addWhereClause("block_user_history_summary.deleted = false")
				.addWhereClause("time_dimension.date >= :fromDate")
				.addGroupColumns("block_user_history_summary.user_id")
				.addParam("fromDate", fromDate)
				.addParam("paid", WorkStatusType.PAID);

		if (CollectionUtils.isEmpty(userIds)) {
			sqlBuilder.addWhereClause("block_user_history_summary.user_id IN (:userIds)")
					.addParam("userIds", userIds);
		}
		List<Map<String, Object>> blocks = jdbcTemplate.queryForList(sqlBuilder.build(), sqlBuilder.getParams());
		for (Map<String, Object> row : blocks) {
			blocksMap.put((Long)row.get("user_id"), ((Long)row.get("count")).intValue());
		}
		return blocksMap;
	}
}
