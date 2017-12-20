package com.workmarket.dao.screening;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.model.screening.ScreenedUser;
import com.workmarket.domains.model.screening.ScreenedUserPagination;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.screening.ScreeningPagination;
import com.workmarket.domains.model.screening.ScreeningStatusType;
import com.workmarket.reporting.model.EvidenceReport;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ScreeningDAOImpl extends PaginationAbstractDAO<Screening> implements ScreeningDAO {

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<Screening> getEntityClass() {
		return Screening.class;
	}

	@SuppressWarnings("JpaQueryApiInspection")
	public Screening findByScreeningId(String screeningId) {
		Assert.notNull(screeningId);

		return (Screening) getFactory().getCurrentSession().createCriteria(Screening.class)
				.add(Restrictions.eq("screeningId", screeningId))
				.uniqueResult();
	}

	@SuppressWarnings("JpaQueryApiInspection")
	public BackgroundCheck findMostRecentBackgroundCheck(Long userId) {
		Assert.notNull(userId);

		return (BackgroundCheck) getFactory().getCurrentSession().createCriteria(BackgroundCheck.class)
				.add(Restrictions.eq("user.id", userId))
				.addOrder(Order.desc("requestDate"))
				.setMaxResults(1)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public BackgroundCheck findPreviousPassedBackgroundCheck(Long userId) {
		Assert.notNull(userId);

		return (BackgroundCheck) getFactory().getCurrentSession().createCriteria(BackgroundCheck.class)
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("screeningStatusType.code", ScreeningStatusType.PASSED))
				.addOrder(Order.asc("requestDate"))
				.setMaxResults(1)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<BackgroundCheck> findBackgroundChecksByUser(Long userId) {
		Assert.notNull(userId);

		return getFactory().getCurrentSession().createCriteria(BackgroundCheck.class)
				.add(Restrictions.eq("user.id", userId))
				.addOrder(Order.desc("requestDate"))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<BackgroundCheck> findBackgroundChecksByUserAndStatus(Long userId, String status) {
		Assert.notNull(userId);

		return getFactory().getCurrentSession().createCriteria(BackgroundCheck.class)
				.add(Restrictions.eq("screeningStatusType.code", status))
				.add(Restrictions.eq("user.id", userId))
				.addOrder(Order.desc("requestDate"))
				.list();
	}


	@SuppressWarnings("serial")
	public ScreeningPagination findBackgroundChecksByStatus(final String status, ScreeningPagination pagination) {
		Assert.notNull(status);
		Assert.notNull(pagination);

		return (ScreeningPagination) super.paginationQuery(BackgroundCheck.class, pagination, ImmutableMap.<String, Object>of("screeningStatusType.code", status));
	}

	@SuppressWarnings("JpaQueryApiInspection")
	public DrugTest findMostRecentDrugTest(Long userId) {
		return (DrugTest) getFactory().getCurrentSession().createCriteria(DrugTest.class)
				.add(Restrictions.eq("user.id", userId))
				.addOrder(Order.desc("requestDate"))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public DrugTest findPreviousPassedDrugTest(Long userId) {
		return (DrugTest) getFactory().getCurrentSession().createCriteria(DrugTest.class)
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("screeningStatusType.code", ScreeningStatusType.PASSED))
				.addOrder(Order.asc("requestDate"))
				.setMaxResults(1)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<DrugTest> findDrugTestsByUser(Long userId) {
		Assert.notNull(userId);

		return getFactory().getCurrentSession().createCriteria(DrugTest.class)
				.add(Restrictions.eq("user.id", userId))
				.addOrder(Order.desc("requestDate"))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<DrugTest> findDrugTestsByUserAndStatus(Long userId, String status) {
		Assert.notNull(userId);

		return getFactory().getCurrentSession().createCriteria(DrugTest.class)
				.add(Restrictions.eq("screeningStatusType.code", status))
				.add(Restrictions.eq("user.id", userId))
				.addOrder(Order.desc("requestDate"))
				.list();
	}

	@SuppressWarnings("serial")
	public ScreeningPagination findDrugTestsByStatus(final String status, ScreeningPagination pagination) {
		Assert.notNull(status);
		Assert.notNull(pagination);

		return (ScreeningPagination) super.paginationQuery(DrugTest.class, pagination, ImmutableMap.<String, Object>of("screeningStatusType.code", status));
	}

	@Override
	public List<EvidenceReport> findBulkEvidenceReportForUsers(List<Long> userIds,String screeningType){
		Assert.notNull(userIds);
		if(userIds.isEmpty()) {
			return new ArrayList<>();
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT screening.request_date,screening.response_date")
				.append("   ,screening.user_id,screening.type,screening.screening_status_type_code")
				.append("   ,user.id,user.first_name,user.last_name,user.company_id")
				.append("   ,company.id,company.name")
				.append("   FROM screening, user,company").append("   WHERE type='").append(screeningType).append("'")
				.append("   AND user.id = screening.user_id")
				.append("   AND user.company_id = company.id")
				.append("   AND screening.screening_status_type_code='" + ScreeningStatusType.PASSED + "'")
				.append("   AND screening.user_id in (").append(CollectionUtilities.join(userIds, ",")).append(")")
				.append("   ORDER BY screening.request_date desc");


		RowMapper<EvidenceReport> mapper = new RowMapper<EvidenceReport>() {
			public EvidenceReport mapRow(ResultSet rs, int rowNum) throws SQLException {
				EvidenceReport evidenceReport = new EvidenceReport();
				Date date = rs.getTimestamp("request_date");
				evidenceReport.setRequestDate(DateUtilities.getCalendarFromDate(date));
				date = rs.getTimestamp("response_date");
				evidenceReport.setResponseDate(DateUtilities.getCalendarFromDate(date));
				evidenceReport.setUserId(rs.getLong("user_id"));
				evidenceReport.setFirstName(rs.getString("first_name"));
				evidenceReport.setLastName(rs.getString("last_name"));
				evidenceReport.setCompanyId(rs.getLong("company_id"));
				evidenceReport.setCompanyName(rs.getString("name"));
				return evidenceReport;
			}
		};

		Map<String, String> params = Maps.newHashMap();
		return jdbcTemplate.query(sql.toString(),params, mapper);
	}

	@Override
	public List<String> findScreeningUuids(List<Long> userIds) {
		String sql = "SELECT DISTINCT vendor_request_id from screening where user_id in (:userIds)";
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("userIds", userIds);
		return jdbcTemplate.queryForList(sql, paramMap, String.class);
	}

	@Override
	public ScreenedUserPagination findAllScreenedUsersOnly(ScreenedUserPagination pagination) {
		String sql = "SELECT u.user_number, u.id AS userId, u.first_name, u.last_name,  u.company_id, c.name AS companyName"
			+ " FROM user u"
			+ " INNER JOIN company c"
			+ " ON u.company_id = c.id";
		String sort = " ORDER BY ";

		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.USER_ID.toString())) {
				sort += " u.id ";
			}

			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.USER_LASTNAME.toString())) {
				sort += " u.last_name ";
			}

			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.USER_FIRSTNAME.toString())) {
				sort += " u.first_name ";
			}

			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.COMPANY_NAME.toString())) {
				sort += " c.name ";
			}
		} else {
			sort += " u.first_name, u.last_name ";
		}

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			sort += " DESC";
		} else {
			sort += " ASC";
		}

		Integer limit = (pagination.getResultsLimit() == null ? Pagination.MAX_ROWS : pagination.getResultsLimit());

		sort += " LIMIT " + pagination.getStartRow() + ", " + limit;
		sql += sort;

		RowMapper<ScreenedUser> mapper = new RowMapper<ScreenedUser>() {
			public ScreenedUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				ScreenedUser screeningUser = new ScreenedUser();
				screeningUser.setId(rs.getLong("userId"));
				screeningUser.setUserNumber(rs.getString("user_number"));
				screeningUser.setFirstName(rs.getString("first_name"));
				screeningUser.setLastName(rs.getString("last_name"));
				screeningUser.setCompanyId(rs.getLong("company_id"));
				screeningUser.setCompanyName(rs.getString("companyName"));
				return screeningUser;
			}
		};

		Map<String, String> params = Maps.newHashMap();
		pagination.setRowCount(jdbcTemplate.queryForObject("SELECT IFNULL(count(distinct user_id), 0) FROM screening", params, Integer.class));
		pagination.setResults(jdbcTemplate.query(sql, params, mapper));

		return pagination;
	}

	@Override
	public ScreenedUserPagination findAllScreenedUsers(ScreenedUserPagination pagination) {
		StringBuilder sql = new StringBuilder();
		StringBuilder sort = new StringBuilder(" ORDER BY ");

		sql.append(" SELECT u.user_number, u.id AS userId, u.first_name, u.last_name,  u.company_id, c.name AS companyName, ")
				.append(" 		report.backgroundCheckRequestDate, IFNULL(report.backgroundCheck, :defaultStatus) AS backgroundCheck, ")
				.append(" 		report.creditCheckRequestDate, IFNULL(report.creditCheck, :defaultStatus) AS creditCheck, ")
				.append(" 		report.drugTestRequestDate, IFNULL(report.drugTest, :defaultStatus) AS drugTest")
				.append(" FROM 	user u ")
				.append(" INNER JOIN company c ")
				.append(" ON 	u.company_id = c.id ")
				.append(" INNER JOIN  ")
				.append(" (SELECT scr.user_id, ")
				.append(" 		MAX(IF(scr.type = 'background', scr.request_date, null)) AS backgroundCheckRequestDate, ")
				.append(" 		GROUP_CONCAT(IF(scr.type = 'background', scr.screening_status_type_code, null)) AS backgroundCheck, ")
				.append(" 		MAX(IF(scr.type = 'credit', scr.request_date, null)) AS creditCheckRequestDate, ")
				.append(" 		GROUP_CONCAT(IF(scr.type = 'credit', scr.screening_status_type_code, null)) AS creditCheck, ")
				.append(" 		MAX(IF(scr.type = 'drug', scr.request_date, null)) AS drugTestRequestDate, ")
				.append(" 		GROUP_CONCAT(IF(scr.type = 'drug', scr.screening_status_type_code, null)) AS drugTest ")
				.append(" FROM 	screening scr ")
				.append(" INNER JOIN (SELECT  max(id) as id, user_id, type ")
				.append(" 		FROM 	  screening ")
				.append(" 		GROUP   BY user_id, type ORDER BY NULL) data ")
				.append(" ON 	scr.id = data.id ")
				.append(" GROUP BY	scr.user_id ORDER BY NULL) report ")
				.append(" ON	u.id = report.user_id");

		if (pagination.getSortColumn() != null) {

			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.BACKGROUND_CHECK_STATUS.toString())) {
				sort.append(" backgroundCheckRequestDate ");
			}

			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.CREDITCHECK_STATUS.toString())) {
				sort.append(" creditCheckRequestDate ");
			}

			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.DRUGTEST_STATUS.toString())) {
				sort.append(" drugTestRequestDate ");
			}

			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.USER_ID.toString())) {
				sort.append(" u.id ");
			}

			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.USER_LASTNAME.toString())) {
				sort.append(" u.last_name ");
			}

			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.USER_FIRSTNAME.toString())) {
				sort.append(" u.first_name ");
			}

			if (pagination.getSortColumn().equals(ScreenedUserPagination.SORTS.COMPANY_NAME.toString())) {
				sort.append(" c.name ");
			}
		} else {
			sort.append(" u.first_name, u.last_name ");
		}

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			sort.append(" DESC");
		} else {
			sort.append(" ASC");
		}

		Integer limit = (pagination.getResultsLimit() == null ? Pagination.MAX_ROWS : pagination.getResultsLimit());

		sql.append(sort)
				.append(" LIMIT ")
				.append(pagination.getStartRow())
				.append(", ")
				.append(limit);

		Map<String, String> params = Maps.newHashMap();
		params.put("defaultStatus", "Not requested");

		RowMapper<ScreenedUser> mapper = new RowMapper<ScreenedUser>() {
			public ScreenedUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				ScreenedUser screeningUser = new ScreenedUser();

				screeningUser.setId(rs.getLong("userId"));
				screeningUser.setUserNumber(rs.getString("user_number"));
				screeningUser.setFirstName(rs.getString("first_name"));
				screeningUser.setLastName(rs.getString("last_name"));
				screeningUser.setCompanyId(rs.getLong("company_id"));
				screeningUser.setCompanyName(rs.getString("companyName"));
				screeningUser.setBackgroundCheckStatus(rs.getString("backgroundCheck"));
				screeningUser.setDrugTestStatus(rs.getString("drugTest"));
				screeningUser.setCreditCheckStatus(rs.getString("creditCheck"));

				Date date = rs.getTimestamp("backgroundCheckRequestDate");
				if (date != null)
					screeningUser.setBackgroundCheckRequestDate(DateUtilities.getCalendarFromDate(date));

				date = rs.getTimestamp("drugTestRequestDate");
				if (date != null)
					screeningUser.setDrugTestRequestDate(DateUtilities.getCalendarFromDate(date));

				date = rs.getTimestamp("creditCheckRequestDate");
				if (date != null)
					screeningUser.setCreditCheckRequestDate(DateUtilities.getCalendarFromDate(date));

				return screeningUser;
			}
		};

		pagination.setRowCount(jdbcTemplate.queryForObject("SELECT IFNULL(count(distinct user_id), 0) FROM screening", params, Integer.class));
		pagination.setResults(jdbcTemplate.query(sql.toString(), params, mapper));

		return pagination;

	}

	public void applySorts(Pagination<Screening> pagination, Criteria query, Criteria count) {
		String sort = "requestDate";

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			query.addOrder(Order.desc(sort));
		} else {
			query.addOrder(Order.asc(sort));
		}
	}

	public void applyFilters(Pagination<Screening> pagination, Criteria criteria, Criteria count) {}

	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
		for (Map.Entry<String, Object> e : params.entrySet()) {
			query.add(Restrictions.eq(e.getKey(), e.getValue()));
			count.add(Restrictions.eq(e.getKey(), e.getValue()));
		}

		query
				.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("user.company", FetchMode.JOIN);
	}
}