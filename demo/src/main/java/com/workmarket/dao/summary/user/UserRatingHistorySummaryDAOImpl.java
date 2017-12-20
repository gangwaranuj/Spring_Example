package com.workmarket.dao.summary.user;

import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.rating.AverageRating;
import com.workmarket.domains.model.summary.user.UserRatingHistorySummary;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Repository
public class UserRatingHistorySummaryDAOImpl extends AbstractDAO<UserRatingHistorySummary> implements UserRatingHistorySummaryDAO {

	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<UserRatingHistorySummary> getEntityClass() {
			 return UserRatingHistorySummary.class;
	}

	private static final String SATISFACTION_FOR_USER_BY_COMPANY_SQL =
		" SELECT COUNT(rating_value) as count, " +
			" SUM(case when rating_value > 1 THEN 1 ELSE 0 END) as good_rating_count " +
			" FROM 	user_rating_history_summary " +
			" WHERE rater_company_id = :companyId" +
			" AND 	rated_user_id = :userId " +
			" GROUP BY rater_company_id, rated_user_id ";

	@Override
	public BigDecimal calculatePercentageRatingsByCompany(long companyId, int ratingValue, DateRange dateRange, Boolean buyerRatings) {
		return calculatePercentageRatings(companyId, ratingValue, dateRange, buyerRatings);
	}

	private BigDecimal calculatePercentageRatings(Long companyId, int ratingValue, DateRange dateRange, Boolean buyerRatings) {
		SQLBuilder sql = new SQLBuilder();
		sql.addColumns("user_rating_history_summary.id ratingId", "IF(rating_value >= :value, 1, 0) ratingValue ")
			.addTable("user_rating_history_summary")
			.addJoin("INNER JOIN time_dimension on time_dimension.id = user_rating_history_summary.date_id")
			.addParam("value", ratingValue);

		if (companyId != null) {
			sql.addWhereClause("user_rating_history_summary.rated_company_id = :companyId")
				.addParam("companyId", companyId);
		}

		if (buyerRatings != null) {
			sql.addWhereClause("user_rating_history_summary.buyer_rating = :buyerRatings")
				.addParam("buyerRatings", buyerRatings);
		}

		if (dateRange != null) {
			Assert.notNull(dateRange.getFrom());
			sql.addWhereClause("time_dimension.date >= :fromDate")
				.addParam("fromDate", dateRange.getFrom());
			if (dateRange.getThrough() != null) {
				sql.addWhereClause("time_dimension.date <= :toDate")
					.addParam("toDate", dateRange.getThrough());
			}
		}

		String finalSQL = "SELECT IF(COALESCE(count(ratingId),0) > 0, COALESCE(sum(ratingValue),0) * 100 /  COALESCE(count(ratingId),0), 0) ratingPercentage " + "FROM ( " + sql.build() + " ) ratings";

		return jdbcTemplate.queryForObject(finalSQL, sql.getParams(), BigDecimal.class);
	}

	@Override
	public int deleteRatingHistorySummaryByRatingId(long ratingId) {
		String sqlDelete = "DELETE userRatingHistorySummary r WHERE r.ratingId = :ratingId";
		return getFactory().getCurrentSession().createQuery(sqlDelete)
				.setParameter("ratingId", ratingId)
				.executeUpdate();
	}

	@Override
	public Map<Long, Map<Long, AverageRating>> findAllSatisfactionRatePerUserPerCompany() {
		String sql = " SELECT COUNT(rating_value) count, " +
				" SUM(case when rating_value > 1 THEN 1 ELSE 0 END) as good_rating_count, rated_user_id, rater_company_id \n" +
				" FROM 	user_rating_history_summary \n" +
				" GROUP BY rater_company_id, rated_user_id";

		Map<Long, Map<Long, AverageRating>> ratingsMap = Maps.newHashMap();

		List<Map<String, Object>> ratings = jdbcTemplate.queryForList(sql, new MapSqlParameterSource());
		for (Map<String, Object> row : ratings) {
			Long userId = ((Integer) row.get("rated_user_id")).longValue();
			Long companyId = ((Integer) row.get("rater_company_id")).longValue();
			Long count = (Long) row.get("count");
			BigDecimal goodRatingCount = (BigDecimal) row.get("good_rating_count");
			Double average = NumberUtilities.percentage(count.intValue(), goodRatingCount.intValue()).doubleValue();

			@SuppressWarnings("unchecked")
			Map<Long, AverageRating> ratingByCompanyMap = (Map<Long, AverageRating>)MapUtils.getObject(ratingsMap, userId, new HashMap<Long, AverageRating>());
			ratingByCompanyMap.put(companyId, new AverageRating(average, count));
			ratingsMap.put(userId, ratingByCompanyMap);
		}
		return ratingsMap;
	}

	@Override
	public Map<Long, AverageRating> findSatisfactionForUsersByCompany(List<Long> userIds, Long companyId) {
		String sql = " SELECT COUNT(rating_value) as count, " +
				" SUM(case when rating_value > 1 THEN 1 ELSE 0 END) as good_rating_count, rated_user_id, rater_company_id \n" +
				" FROM 	user_rating_history_summary \n" +
				" WHERE rater_company_id = :companyId" +
				" AND 	rated_user_id IN (" + StringUtils.join(userIds, ",") + ")" +
				" GROUP BY rater_company_id, rated_user_id";

		Map<Long, AverageRating> ratingsMap = Maps.newHashMap();
		MapSqlParameterSource parameterSource = new MapSqlParameterSource() ;
		parameterSource.addValue("companyId", companyId);

		List<Map<String, Object>> ratings = jdbcTemplate.queryForList(sql, parameterSource);
		for (Map<String, Object> row : ratings) {
			Long userId = ((Integer) row.get("rated_user_id")).longValue();
			Long count = (Long) row.get("count");
			BigDecimal goodRatingCount = (BigDecimal) row.get("good_rating_count");
			Double average = NumberUtilities.percentage(count.intValue(), goodRatingCount.intValue()).doubleValue();

			ratingsMap.put(userId, new AverageRating(average, count));
		}
		return ratingsMap;
	}

	@Override
	public AverageRating findAverageRatingForUserByCompany(final Long userId, final Long companyId) {
		final MapSqlParameterSource parameterSource = new MapSqlParameterSource() ;
		parameterSource.addValue("companyId", companyId);
		parameterSource.addValue("userId", userId);

		List<AverageRating> averageRatings = jdbcTemplate.query(SATISFACTION_FOR_USER_BY_COMPANY_SQL, parameterSource, new RowMapper<AverageRating>() {
			@Override
			public AverageRating mapRow(ResultSet resultSet, int i) throws SQLException {
				Long count = resultSet.getLong("count");
				BigDecimal goodRatingCount = resultSet.getBigDecimal("good_rating_count");
				Double average = NumberUtilities.percentage(count.intValue(), goodRatingCount.intValue()).doubleValue();
				return new AverageRating(average, count);
			}
		});

		if (isEmpty(averageRatings)) {
			return null;
		}
		return averageRatings.get(0);
	}
}
