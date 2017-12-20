package com.workmarket.dao.rating;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.rating.RatingPagination;
import com.workmarket.domains.model.rating.RatingSummary;
import com.workmarket.domains.model.rating.RatingWorkData;
import com.workmarket.domains.model.reporting.RatingReport;
import com.workmarket.domains.model.reporting.RatingReportPagination;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class RatingDAOImpl extends PaginationAbstractDAO<Rating> implements RatingDAO {

	private static final Log logger = LogFactory.getLog(RatingDAOImpl.class);

	@Autowired @Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	@Autowired @Resource(name = "jdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	private static final String SELECT_PERCENTAGE = " SELECT   IFNULL(count(*) / (SELECT count(*) FROM rating r1 WHERE r1.rated_user_id = :userId and r1.deleted = 0 and r1.is_pending = 0), 0)";
	private static final String SELECT_PERCENTAGE_SINCE_DATE = " SELECT   IFNULL(count(*) / (SELECT count(*) FROM rating r1 WHERE r1.rated_user_id = :userId and r1.deleted = 0 and r1.is_pending = 0 and r1.created_on > :fromDate), 0)";
	private static final String SELECT_VENDOR_PERCENTAGE = " SELECT   IFNULL(count(*) / (SELECT count(*) FROM rating r1 WHERE r1.rated_user_id IN (:userIds) and r1.deleted = 0 and r1.is_pending = 0), 0)";
	private static final String SELECT_VENDOR_PERCENTAGE_SINCE_DATE = " SELECT   IFNULL(count(*) / (SELECT count(*) FROM rating r1 WHERE r1.rated_user_id IN (:userIds) and r1.deleted = 0 and r1.is_pending = 0 and r1.created_on > :fromDate), 0)";

	@Override
	protected Class<Rating> getEntityClass() {
		return Rating.class;
	}

	@Override
	public void applySorts(Pagination<Rating> pagination, Criteria query, Criteria count) {
		if (pagination.getSortColumn() == null)
			return;

		query.createAlias("work", "wk")
				.createAlias("ratedUser", "rdu")
				.createAlias("ratingUser", "rgu");

		String sort = "createdOn";
		if (pagination.getSortColumn().equals(RatingPagination.SORTS.CREATED_ON.toString())) {
			sort = "createdOn";
		} else if (pagination.getSortColumn().equals(RatingPagination.SORTS.VALUE.toString())) {
			sort = "value";
		} else if (pagination.getSortColumn().equals(RatingPagination.SORTS.TITLE.toString())) {
			sort = "wk.title";
		} else if (pagination.getSortColumn().equals(RatingPagination.SORTS.WORK_NUMBER.toString())) {
			sort = "wk.workNumber";
		} else if (pagination.getSortColumn().equals(RatingPagination.SORTS.CLIENT_NAME.toString())) {
			sort = "rdu.lastName";
		} else if (pagination.getSortColumn().equals(RatingPagination.SORTS.RESOURCE_NAME.toString())) {
			sort = "rgu.lastName";
		}

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			query.addOrder(Order.desc(sort));
		} else {
			query.addOrder(Order.asc(sort));
		}
	}

	@Override
	public void applyFilters(Pagination<Rating> pagination, Criteria criteria, Criteria count) {
		if (pagination.getFilters() == null)
			return;

		if (pagination.hasFilter(RatingPagination.FILTER_KEYS.REVIEW_SHARED_FLAG)) {
			Boolean flag = BooleanUtils.toBoolean(pagination.getFilter(RatingPagination.FILTER_KEYS.REVIEW_SHARED_FLAG));
			criteria.add(Restrictions.eq("reviewSharedFlag", flag));
			count.add(Restrictions.eq("reviewSharedFlag", flag));
		}

		if (pagination.hasFilter(RatingPagination.FILTER_KEYS.RATING_SHARED_FLAG)) {
			Boolean flag = BooleanUtils.toBoolean(pagination.getFilter(RatingPagination.FILTER_KEYS.RATING_SHARED_FLAG));
			criteria.add(Restrictions.eq("ratingSharedFlag", flag));
			count.add(Restrictions.eq("ratingSharedFlag", flag));
		}

		if (pagination.hasFilter(WorkPagination.FILTER_KEYS.FROM_DATE)) {

			String from_date = pagination.getFilter(WorkPagination.FILTER_KEYS.FROM_DATE);

			criteria.add(Restrictions.ge("createdOn", DateUtilities.getCalendarFromISO8601(from_date)));
			count.add(Restrictions.ge("createdOn", DateUtilities.getCalendarFromISO8601(from_date)));
		}

		if (pagination.hasFilter(WorkPagination.FILTER_KEYS.THROUGH_DATE)) {

			String through_date = pagination.getFilter(WorkPagination.FILTER_KEYS.THROUGH_DATE);

			criteria.add(Restrictions.le("createdOn", DateUtilities.getCalendarFromISO8601(through_date)));
			count.add(Restrictions.le("createdOn", DateUtilities.getCalendarFromISO8601(through_date)));
		}

	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {

		query.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));

		query.setFetchMode("work", FetchMode.JOIN)
				.setFetchMode("ratedUser", FetchMode.JOIN)
				.setFetchMode("ratingUser", FetchMode.JOIN);

		if (params == null)
			return;

		if (params.containsKey("ratedUser")) {
			query.add(Restrictions.eq("ratedUser.id", params.get("ratedUser")));
			count.add(Restrictions.eq("ratedUser.id", params.get("ratedUser")));
		}

		if (params.containsKey("ratingUser")) {
			query.add(Restrictions.eq("ratingUser.id", params.get("ratingUser")));
			count.add(Restrictions.eq("ratingUser.id", params.get("ratingUser")));
		}

		if (params.containsKey("work")) {
			query.add(Restrictions.eq("work.id", params.get("work")));
			count.add(Restrictions.eq("work.id", params.get("work")));
		}

		if (params.containsKey("isFlaggedForReview")) {
			query.add(Restrictions.eq("flaggedForReview", params.get("isFlaggedForReview")));
			count.add(Restrictions.eq("flaggedForReview", params.get("isFlaggedForReview")));
		}

		if (params.containsKey("company")) {
			query.createAlias("rdu.company", "company");
			count.createAlias("ratedUser", "rdu")
					.createAlias("rdu.company", "company");
			query.add(Restrictions.eq("company.id", params.get("company")));
			count.add(Restrictions.eq("company.id", params.get("company")));
		}
	}

	@Override
	public RatingPagination findByUser(final Long userId, RatingPagination pagination) {
		return (RatingPagination) super.paginationQuery(pagination, ImmutableMap.<String, Object>of("ratingUser", userId));
	}

	@Override
	public RatingPagination findForUserForWork(final Long userId, final Long workId, RatingPagination pagination) {
		return (RatingPagination) super.paginationQuery(pagination, ImmutableMap.<String, Object>of("ratingUser", userId, "work", workId));
	}

	@Override
	public RatingPagination findByUserCompanyForWork(final Long companyId, final Long workId, RatingPagination pagination) {
		return (RatingPagination) super.paginationQuery(pagination, ImmutableMap.<String, Object>of("ratingCompany", companyId, "work", workId));
	}

	@Override
	public RatingReportPagination buildRatingReportForCompany(final Long companyId, RatingReportPagination pagination) {
		Assert.notNull(companyId, "Invalid company id");
		Assert.notNull(pagination, "Invalid pagination");

		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("r.value", "r.review", "r.created_on", "r.flagged_for_review_flag", "rater.first_name", "rater.last_name", "ratee.first_name", "ratee.last_name", "w.title", "w.work_number", "wm.due_on", "wm.paid_on")
				.addTable("rating r")
				.addJoin("INNER JOIN user rater on rater.id = r.rater_user_id")
				.addJoin("INNER JOIN user ratee on ratee.id = r.rated_user_id")
				.addJoin("INNER JOIN work w on w.id = r.work_id")
				.addJoin("INNER JOIN work_milestones wm on wm.work_id = r.work_id")
				.addWhereClause("r.is_buyer_rating = 1")
				.addWhereClause("r.deleted = 0")
				.addWhereClause("r.is_pending = 0")
				.addWhereClause("ratee.company_id = :company_id")
				.addParam("company_id", companyId);

		if (pagination.getFilters() != null) {
			if(pagination.hasFilter(RatingReportPagination.FILTER_KEYS.FROM_DATE)) {
				String fromDate = pagination.getFilter(RatingReportPagination.FILTER_KEYS.FROM_DATE);
				builder.addWhereClause("wm.paid_on >= :from_date");
				builder.addParam("from_date", fromDate);
			}
			if (pagination.hasFilter(RatingReportPagination.FILTER_KEYS.THROUGH_DATE)) {
				String throughDate = pagination.getFilter(RatingReportPagination.FILTER_KEYS.THROUGH_DATE);
				builder.addWhereClause("wm.paid_on <= :through_date");
				builder.addParam("through_date", throughDate);
			}
		}

		if (pagination.getSortColumn() == null) {
			pagination.setSortColumn(RatingReportPagination.SORTS.CREATED_ON);
		}

		String sortColumn = "r.created_on";
		if (RatingReportPagination.SORTS.CLIENT_NAME.toString().equals(pagination.getSortColumn())) {
			sortColumn = "rater.first_name";
		} else if (RatingReportPagination.SORTS.RESOURCE_NAME.toString().equals(pagination.getSortColumn())) {
			sortColumn = "ratee.first_name";
		} else if (RatingReportPagination.SORTS.CREATED_ON.toString().equals(pagination.getSortColumn())) {
			sortColumn = "r.created_on";
		} else if (RatingReportPagination.SORTS.TITLE.toString().equals(pagination.getSortColumn())) {
			sortColumn = "w.title";
		} else if (RatingReportPagination.SORTS.VALUE.toString().equals(pagination.getSortColumn())) {
			sortColumn = "r.value";
		} else if (RatingReportPagination.SORTS.WORK_NUMBER.toString().equals(pagination.getSortColumn())) {
			sortColumn = "w.work_number";
		} else if (RatingReportPagination.SORTS.PAID_ON.toString().equals(pagination.getSortColumn())) {
			sortColumn = "wm.paid_on";
		}

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.ASC)) {
			builder.addOrderBy(sortColumn, "ASC");
		} else {
			builder.addOrderBy(sortColumn, "DESC");
		}

		builder.setStartRow(pagination.getStartRow());
		if (!pagination.isFetchAll()) {
			builder.setPageSize(pagination.getResultsLimit());
		}

		RowMapper<RatingReport> mapper = new RowMapper<RatingReport>() {
			@Override
			public RatingReport mapRow(ResultSet rs, int rowNum) throws SQLException {
				RatingReport report = new RatingReport();
				report.setValue(rs.getInt("r.value"));
				report.setReview(rs.getString("r.review"));
				report.setFlaggedForReview(rs.getBoolean("r.flagged_for_review_flag"));
				report.setTitle(rs.getString("w.title"));
				report.setWorkNumber(rs.getString("w.work_number"));
				report.setRatingUserFirstName(rs.getString("rater.first_name"));
				report.setRatingUserLastName(rs.getString("rater.last_name"));
				report.setRatedUserFirstName(rs.getString("ratee.first_name"));
				report.setRatedUserLastName(rs.getString("ratee.last_name"));
				report.setRatingDate(DateUtilities.getCalendarFromDate(rs.getDate("r.created_on")));
				report.setDueOn(DateUtilities.getCalendarFromDate(rs.getDate("wm.due_on")));
				report.setPaidOn(DateUtilities.getCalendarFromDate(rs.getDate("wm.paid_on")));
				return report;
			}
		};

		logger.debug(builder.build());
		pagination.setRowCount(readOnlyJdbcTemplate.queryForObject(builder.buildCount("*"), builder.getParams(), Integer.class));
		pagination.setResults(readOnlyJdbcTemplate.query(builder.build(), builder.getParams(), mapper));
		return pagination;
	}

	private String buildFromAndWhere() {
		return
				" FROM   rating r3"
					+ " WHERE  r3.rated_user_id = :userId"
					+ " AND    r3.deleted = 0"
					+ " AND    r3.is_pending = 0"
					+ " AND    r3.value > 1" ;
	}

	private String buildFromAndWhereSinceDate() {
		return
				" FROM   rating r3"
					+ " WHERE  r3.rated_user_id = :userId"
					+ " AND    r3.deleted = 0"
					+ " AND    r3.is_pending = 0"
					+ " AND    r3.value > 1"
					+ " AND    r3.created_on > :fromDate";
	}

	private String buildFromAndWhereIn() {
		return
			" FROM   rating r3"
				+ " WHERE  r3.rated_user_id IN (:userIds)"
				+ " AND    r3.deleted = 0"
				+ " AND    r3.is_pending = 0"
				+ " AND    r3.value > 1" ;
	}

	private String buildFromAndWhereInSinceDate() {
		return
			" FROM   rating r3"
				+ " WHERE  r3.rated_user_id IN (:userIds)"
				+ " AND    r3.deleted = 0"
				+ " AND    r3.is_pending = 0"
				+ " AND    r3.value > 1"
				+ " AND    r3.created_on > :fromDate";
	}

	@Override
	public Double findSatisfactionRateForUser(Long userId) {

		final String sql = SELECT_PERCENTAGE + buildFromAndWhere();

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", userId);

		// This method was average rating, all the caller are expecting a value not a percentage
		return readOnlyJdbcTemplate.queryForObject(sql, params, Double.class) * 100;
	}

	@Override
	public Double findSatisfactionRateForVendor(List<Long> userIds) {
		final String sql = SELECT_VENDOR_PERCENTAGE + buildFromAndWhereIn();

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userIds", userIds);

		// This method was average rating, all the caller are expecting a value not a percentage
		return readOnlyJdbcTemplate.queryForObject(sql, params, Double.class) * 100;
	}

	@Override
	public RatingPagination findFlaggedForUser(final Long userId, RatingPagination pagination) {
		return (RatingPagination) super.paginationQuery(pagination, ImmutableMap.<String, Object>of("ratedUser", userId, "isFlaggedForReview", true));
	}

	@Override
	public Map<Long, Rating> findLatestForUserVisibleToCompanyInWork(Long forUserId, Long byCompanyId, Collection<Long> workIds) {
		if (CollectionUtils.isEmpty(workIds)) return Collections.emptyMap();

		@SuppressWarnings("unchecked") List<Rating> ratings = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("ratedUser.id", forUserId))
				.add(Restrictions.or(
						Restrictions.eq("ratingCompany.id", byCompanyId),
						Restrictions.eq("ratingSharedFlag", Boolean.TRUE)
				))
				.add(Restrictions.in("work.id", workIds))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.asc("id"))
				.list();

		Map<Long, Rating> lookup = Maps.newHashMap();
		for (Rating r : ratings)
			lookup.put(r.getWork().getId(), r);

		return lookup;
	}

	@Override
	@SuppressWarnings("JpaQueryApiInspection")
	public Integer countAllUserRatings(Long userId) {
		Assert.notNull(userId);
		return ((Long) getFactory().getCurrentSession().getNamedQuery("rating.countAllUserRatings").setLong("user_id", userId)
				.uniqueResult()).intValue();
	}

	@Override
	public RatingSummary findRatingSummaryForUser(Long userId) {
		return findRatingSummary(userId, null, null);
	}

	@Override
	public RatingSummary findRatingSummaryForVendor(List<Long> userIds) {
		return findRatingSummary(userIds, null, null);
	}

	@Override
	public RatingSummary findRatingSummaryForUserSinceDate(Long userId, Calendar fromDate) {
		return findRatingSummary(userId, null, fromDate);
	}

	@Override
	public RatingSummary findRatingSummaryForVendorSinceDate(List<Long> userIds, Calendar fromDate) {
		return findRatingSummary(userIds, null, fromDate);
	}

	@Override
	public RatingSummary findRatingSummaryForUserByCompany(Long userId, Long companyId) {
		return findRatingSummary(userId, companyId, null);
	}

	@Override
	public RatingSummary findRatingSummaryForVendorByCompany(List<Long> userIds, Long companyId) {
		return findRatingSummary(userIds, companyId, null);
	}

	@Override
	public RatingSummary findRatingSummaryForUserByCompanySinceDate(Long userId, Long companyId, Calendar fromDate) {
		return findRatingSummary(userId, companyId, fromDate);
	}

	@Override
	public RatingSummary findRatingSummaryForVendorByCompanySinceDate(List<Long> userIds, Long companyId, Calendar fromDate) {
		return findRatingSummary(userIds, companyId, fromDate);
	}

	private RatingSummary findRatingSummary(List<Long> userIds, Long companyId, Calendar fromDate) {
		SQLBuilder builder = new SQLBuilder();
		if (userIds != null && userIds.size() > 0) {
			builder.addWhereInClause("r.rated_user_id", "userId", userIds);
		}
		return findRatingSummary(builder, companyId, fromDate);
	}

	private RatingSummary findRatingSummary(Long userId, Long companyId, Calendar fromDate) {
		SQLBuilder builder = new SQLBuilder();
		if (userId != null) {
			builder
				.addWhereClause("r.rated_user_id = :userId")
				.addParam("userId", userId);
		}
		return findRatingSummary(builder, companyId, fromDate);
	}

	private RatingSummary findRatingSummary(SQLBuilder builder, Long companyId, Calendar fromDate) {

		Assert.notNull(builder);
		builder
				.addColumns("IFNULL(SUM(IF(value > 1, 1, 0)) / count(*), 0) as satisfactionRate")
				.addColumns("IFNULL(SUM(IF(quality > 1, 1, 0)) / SUM(IF(quality > 0, 1, 0)), 0) as quality")
				.addColumns("IFNULL(SUM(IF(professionalism > 1, 1, 0)) / SUM(IF(professionalism > 0, 1, 0)), 0) as professionalism")
				.addColumns("IFNULL(SUM(IF(communication > 1, 1, 0)) / SUM(IF(communication > 0, 1, 0)), 0) as communication")
				.addColumns("count(*) as count")
				.addTable("rating r")
				.addWhereClause("r.deleted = 0")
				.addWhereClause("r.is_pending = 0");

		if (fromDate != null) {
			builder
					.addWhereClause("r.created_on > :fromDate")
					.addParam("fromDate", fromDate);
		}
		if (companyId != null) {
			builder
					.addWhereClause("r.rater_company_id = :companyId")
					.addParam("companyId", companyId);
		}
		List<Map<String, Object>> ratingInfo = readOnlyJdbcTemplate.queryForList(builder.build(), builder.getParams());

		RatingSummary ratingSummary = new RatingSummary();
		for(Map<String, Object> row: ratingInfo) {
			ratingSummary.setSatisfactionRate(((BigDecimal) row.get("satisfactionRate")).doubleValue());
			ratingSummary.setQuality(((BigDecimal) row.get("quality")).doubleValue());
			ratingSummary.setProfessionalism(((BigDecimal) row.get("professionalism")).doubleValue());
			ratingSummary.setCommunication(((BigDecimal) row.get("communication")).doubleValue());
			ratingSummary.setCount((Long)row.get("count"));
		}

		return ratingSummary;
	}

	@Override
	public Double findSatisfactionRateOverallSinceDate(Long userId, Calendar fromDate) {

		final String sql = SELECT_PERCENTAGE_SINCE_DATE + buildFromAndWhereSinceDate();

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", userId);
		params.addValue("fromDate", fromDate);

		return readOnlyJdbcTemplate.queryForObject(sql, params, Double.class);
	}

	@Override
	public Double findVendorSatisfactionRateOverallSince(List<Long> userIds, Calendar fromDate) {
		final String sql = SELECT_VENDOR_PERCENTAGE_SINCE_DATE + buildFromAndWhereInSinceDate();

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userIds", StringUtils.join(userIds, ','));
		params.addValue("fromDate", fromDate);

		return readOnlyJdbcTemplate.queryForObject(sql, params, Double.class);
	}

	@Override
	public Rating findLatestForUserForWork(Long ratedUserId, Long workId) {
		return (Rating) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("ratedUser.id", ratedUserId))
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.desc("id"))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public void markRatingsNonPendingByWorkId(Long workId) {
		final String sql =
				"UPDATE rating "
			+ "SET is_pending = 0 "
			+ "WHERE work_id = :workId ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("workId", workId);
		jdbcTemplate.update(sql, params);
	}

	@Override
	public RatingWorkData findLatestRatingDataForResourceByWorkNumber(Long userId, String workNumber) {

		SQLBuilder builder = new SQLBuilder();

		builder.addColumns("r.value, r.quality, r.professionalism, r.communication, r.review, r.modified_on")
				.addColumns("rater_user.first_name AS raterUserFirstName, rater_user.last_name AS raterUserLastName")
				.addColumns("rated_user.first_name AS ratedUserFirstName, rated_user.last_name AS ratedUserLastName, rated_user.id AS ratedUserId")
				.addColumns("w.id AS workId")
				.addTable("rating r")
				.addJoin("INNER JOIN work w ON w.id = r.work_id")
				.addJoin("INNER JOIN user rater_user ON rater_user.id = r.rater_user_id")
				.addJoin("INNER JOIN user rated_user ON rated_user.id = r.rated_user_id")
				.addWhereClause("w.work_number = :workNumber")
				.addWhereClause("r.rated_user_id = :userId")
				.addWhereClause("r.deleted = 0")
				.addParam("userId", userId)
				.addParam("workNumber", workNumber);

		@SuppressWarnings("unchecked")
		List<RatingWorkData> data = readOnlyJdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper() {
			public RatingWorkData mapRow(ResultSet rs, int rowNum) throws SQLException {
				RatingWorkData row = new RatingWorkData();
				row.setRatingValue(rs.getInt("value"));
				row.setRatingQuality(rs.getInt("quality"));
				row.setRatingProfessionalism(rs.getInt("professionalism"));
				row.setRatingCommunication(rs.getInt("communication"));
				row.setRatingReview(rs.getString("review"));
				row.setModifiedOn(rs.getDate("modified_on"));
				row.setRaterUserName(StringUtilities.fullName(rs.getString("raterUserFirstName"), rs.getString("raterUserLastName")));
				row.setRatedUserName(StringUtilities.fullName(rs.getString("ratedUserFirstName"), rs.getString("ratedUserLastName")));
				row.setWorkId(rs.getLong("workId"));
				row.setRatedUserId(rs.getLong("ratedUserId"));
				return row;
			}
		});

		return data.isEmpty() ? null : data.get(0);
	}
}
