package com.workmarket.dao.company;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.data.aggregate.CompanyAggregate;
import com.workmarket.data.aggregate.CompanyAggregatePagination;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.workmarket.utility.ConcatenableIlikeCriterion.ilike;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Repository
public class CompanyDAOImpl extends AbstractDAO<Company> implements CompanyDAO {

	private static final Log logger = LogFactory.getLog(CompanyDAOImpl.class);

	@Autowired @Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<Company> getEntityClass() {
		return Company.class;
	}

	@Override
	public Company findById(Long id) {
		Assert.notNull(id);
		return (Company) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("id", id)).uniqueResult();
	}

	@Override
	public Company findByUuid(String uuid) {
		Assert.notNull(uuid);
		return (Company) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	@Override
	public Long findCompanyIdByUuid(String companyUuid) {
		Assert.notNull(companyUuid);

		Query query = getFactory().getCurrentSession().createQuery("select id from company where uuid = :companyUuid");
		query.setParameter("companyUuid", companyUuid);

		return (Long) query.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findCompanyIdsByUuids(Collection<String> uuids) {
		return getFactory()
				.getCurrentSession()
				.createQuery("select id from company where uuid in (:uuids)")
				.setParameterList("uuids", uuids)
				.list();
	}

	@Override
	public List<CompanyIdentityDTO> findCompanyIdentitiesByCompanyNumbers(Collection<String> companyNumbers) {
		if (isEmpty(companyNumbers)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		SQLBuilder builder = new SQLBuilder()
			.addColumns("id", "company_number", "uuid")
			.addTable("company")
			.addWhereClause("company_number IN (:companyNumbers)")
			.addParam("companyNumbers", companyNumbers);

		return jdbcTemplate.query(builder.build(), builder.getParams(), new CompanyIdentityRowMapper());
	}

	@Override
	public List<CompanyIdentityDTO> findCompanyIdentitiesByUuids(Collection<String> uuids) {
		if (isEmpty(uuids)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		SQLBuilder builder = new SQLBuilder()
			.addColumns("id", "company_number", "uuid")
			.addTable("company")
			.addWhereClause("uuid IN (:uuids)")
			.addParam("uuids", uuids);

		return jdbcTemplate.query(builder.build(), builder.getParams(), new CompanyIdentityRowMapper());
	}

	@Override
	public List<CompanyIdentityDTO> findCompanyIdentitiesByIds(Collection<Long> ids) {
		if (isEmpty(ids)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		SQLBuilder builder = new SQLBuilder()
				.addColumns("id", "company_number", "uuid")
				.addTable("company")
				.addWhereClause("id IN (:ids)")
				.addParam("ids", ids);

		return jdbcTemplate.query(builder.build(), builder.getParams(), new CompanyIdentityRowMapper());
	}

	@Override
	public Company findCompanyById(Long companyId) {
		Assert.notNull(companyId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("avatarOriginal", FetchMode.JOIN)
			.setFetchMode("avatarSmall", FetchMode.JOIN)
			.setFetchMode("avatarLarge", FetchMode.JOIN)
			.add(Restrictions.eq("id", companyId));

		Company company = (Company) criteria.uniqueResult();
		if (company != null) {
			Hibernate.initialize(company.getAgingAlertEmails());
			Hibernate.initialize(company.getSubscriptionInvoiceEmails());
		}
		return company;
	}

	@Override
	public Company findCompanyByNumber(String companyNumber) {
		Assert.notNull(companyNumber);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("avatarOriginal", FetchMode.JOIN)
			.setFetchMode("avatarSmall", FetchMode.JOIN)
			.setFetchMode("avatarLarge", FetchMode.JOIN)
			.add(Restrictions.eq("companyNumber", companyNumber));

		Company company = (Company) criteria.uniqueResult();
		if (company != null) {
			Hibernate.initialize(company.getAgingAlertEmails());
			Hibernate.initialize(company.getSubscriptionInvoiceEmails());
		}
		return company;
	}

	@Override
	public Company findCompanyByName(String companyName) {
		Assert.notNull(companyName);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("avatarOriginal", FetchMode.JOIN)
			.setFetchMode("avatarSmall", FetchMode.JOIN)
			.setFetchMode("avatarLarge", FetchMode.JOIN)
			.add(Restrictions.eq("name", companyName));

		//If company is not unique, handle exception with a null result
		if (criteria.list().size() > 1) {
			return null;
		}

		Company company = (Company) criteria.uniqueResult();
		if (company != null) {
			Hibernate.initialize(company.getAgingAlertEmails());
			Hibernate.initialize(company.getSubscriptionInvoiceEmails());
		}
		return company;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Company> findSimilarCompaniesByName(Long companyId, String matchName) {
		StringBuilder sql = new StringBuilder("FROM company c WHERE c.id != :companyId");
		String[] names = matchName.toLowerCase().split(" ");
		Map<String, String> params = Maps.newHashMap();
		String name, param;

		for (int i = 0; i < names.length; i++) {
			name = names[i];
			param = "param" + i;

			if (i == 0) {
				sql.append(" AND ( c.name like :").append(param);
				params.put(param, StringUtilities.processForLike(name));
			} else {
				if (name.length() > 3) {
					sql.append(" OR c.name like :").append(param);
					params.put(param, StringUtilities.processForLike(name));
				}
			}

			if (i == (names.length - 1)) {
				sql.append(" )");
			}
		}

		Query query = getFactory().getCurrentSession().createQuery(sql.toString())
			.setParameter("companyId", companyId);

		for (Map.Entry<String, String> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		query.setMaxResults(5);
		return query.list();
	}

	@Override
	public CompanyAggregatePagination findAllCompanies(CompanyAggregatePagination pagination) {

		SQLBuilder sql = new SQLBuilder();
		SQLBuilder sqlCount = new SQLBuilder();

		sql.addColumns("this.id", "this.company_status_type_code AS status", "this.customer_type as customerType","this.effective_name AS companyName",
				"this.created_on", "this.locked_on",
				"(SELECT count(*) FROM user WHERE	user.email_confirmed = 'Y' AND user.user_status_type_code IN ('pending', 'approved') AND user.company_id = this.id ) AS lane0 ",
				"(SELECT 	count(*) FROM lane_association l  \n" +
						"LEFT 		JOIN user   \n" +
						"ON			l.user_id = user.id  \n" +
						"LEFT 		JOIN user_acl_role role  \n" +
						"ON 		user.id = role.user_id  \n" +
						"WHERE  	user.email_confirmed = 'Y' AND user.user_status_type_code IN ('pending', 'approved')  \n" +
						"AND 		l.lane_type_id = 1 AND l.deleted = 0 and l.approval_status IN (1,5) AND l.verification_status = 1 \n" +
						"AND 		role.acl_role_id = 6 AND role.deleted = 0  \n" +
						"AND  		l.company_id = this.id) AS lane1",
				// lane 2
				"(SELECT 	count(*)  \n" +
				"FROM 		lane_association l  \n" +
				"LEFT 		JOIN user  \n" +
				"ON 		l.user_id = user.id  \n" +
				"WHERE     user.email_confirmed = 'Y' AND user.user_status_type_code IN ('pending', 'approved')  \n" +
				"AND 		l.lane_type_id = 2 AND l.deleted = 0 AND l.approval_status IN (1,5) AND l.verification_status = 1 \n" +
				"AND 		l.company_id = this.id) AS lane2",
				// lane 3
				"(SELECT 	count(*)  \n" +
						"FROM 		lane_association l  \n" +
						"LEFT 		JOIN user  \n" +
						"ON 		l.user_id = user.id  \n" +
						"LEFT 		JOIN user_acl_role role  \n" +
						"on 		user.id = role.user_id  \n" +
						"WHERE		user.email_confirmed = 'Y' AND user.user_status_type_code IN ('pending', 'approved')  \n" +
						"AND 		l.lane_type_id = 3 AND l.deleted = 0 and l.approval_status IN (1,5) AND l.verification_status = 1 \n" +
						"AND 		role.acl_role_id = 7  AND role.deleted = 0 AND l.company_id = this.id) AS lane3",
				"0 AS YTDWork")
				.addTable("company this");

		sqlCount
			.addColumn("COUNT(1)")
			.addTable("company this");

		if (pagination.getFilters() != null) {
			if (pagination.hasFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_NAME)) {

				String companyName = pagination.getFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_NAME);

				sql.addWhereClause("this.effective_name like :companyName ");
				sqlCount.addWhereClause("this.effective_name like :companyName ");
				sql.addParam("companyName", StringUtilities.processForLike(companyName.toLowerCase()));
			}
			if (pagination.hasFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_ID)) {
				String id = pagination.getFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_ID);

				sql.addWhereClause("id = :id ");
				sqlCount.addWhereClause("id = :id ");
				sql.addParam("id", Long.parseLong(id));
			}
			if (pagination.hasFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_STATUS)) {
				String statusCode = pagination.getFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_STATUS);

				sql.addWhereClause("company_status_type_code = :statusCode ");
				sqlCount.addWhereClause("company_status_type_code = :statusCode ");
				sql.addParam("statusCode", statusCode);
			}
	       if(pagination.hasFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_TYPE)) {

		       sql.addWhereClause("operating_as_individual_flag = :companyType");
		       sqlCount.addWhereClause("operating_as_individual_flag = :companyType");
		       sql.addParam("companyType", 0);
	       }
		}

		String sort = "ASC";
		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			sort = "DESC";
		}

		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(CompanyAggregatePagination.SORTS.COMPANY_NAME.toString())) {
				sql.addOrderBy("this.effective_name", sort);
			} else if (pagination.getSortColumn().equals(CompanyAggregatePagination.SORTS.LANE_0.toString())) {
				sql.addOrderBy("lane0", sort);
			} else if (pagination.getSortColumn().equals(CompanyAggregatePagination.SORTS.LANE_1.toString())) {
				sql.addOrderBy("lane1", sort);
			} else if (pagination.getSortColumn().equals(CompanyAggregatePagination.SORTS.LANE_2.toString())) {
				sql.addOrderBy("lane2", sort);
			} else if (pagination.getSortColumn().equals(CompanyAggregatePagination.SORTS.LANE_3.toString())) {
				sql.addOrderBy("lane3", sort);
			} else if (pagination.getSortColumn().equals(CompanyAggregatePagination.SORTS.YTD_ASSIGNMENTS.toString())) {
				sql.addOrderBy("YTDWork", sort);
			} else if (pagination.getSortColumn().equals(CompanyAggregatePagination.SORTS.CREATED_ON.toString())) {
				sql.addOrderBy("created_on", sort);
			} else if (pagination.getSortColumn().equals(CompanyAggregatePagination.SORTS.COMPANY_STATUS.toString())) {
				sql.addOrderBy("company_status_type_code", sort);
			}
			else {
				sql.addOrderBy("this.created_on", sort);
			}
		} else {
			sql.addOrderBy("this.created_on", sort);
		}

		RowMapper<CompanyAggregate> mapper = new RowMapper<CompanyAggregate>() {
			public CompanyAggregate mapRow(ResultSet rs, int rowNum) throws SQLException {
				CompanyAggregate company = new CompanyAggregate();

				company.setCompanyId(rs.getLong("id"));
				company.setCompanyName(rs.getString("companyName"));
				company.setLane0Users(rs.getInt("lane0"));
				company.setLane1Users(rs.getInt("lane1"));
				company.setLane2Users(rs.getInt("lane2"));
				company.setLane3Users(rs.getInt("lane3"));
				company.setYTDAssignments(rs.getInt("YTDWork"));
				company.setCreatedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("created_on")));
				company.setCompanyStatus(rs.getString("status"));
				company.setLockedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("locked_on")));
				company.setCustomerType(rs.getString("customerType"));
				return company;
			}
		};

		sql.setStartRow(pagination.getStartRow());
		sql.setPageSize(pagination.getResultsLimit());

		pagination.setRowCount(jdbcTemplate.queryForObject(sqlCount.build(), sql.getParams(), Integer.class));
		pagination.setResults(jdbcTemplate.query(sql.build(), sql.getParams(), mapper));

		return pagination;
	}

	@Override
	public Integer countAllLane3UsersWithEINsByCompany(Long companyId) {
		String sql = "  SELECT 	count(l.id) " +
			" FROM 		lane_association l " +
			" INNER 	JOIN user " +
			" ON 		l.user_id = user.id " +
			" INNER 	JOIN company " +
			" ON 		company.id = user.company_id " +
			" LEFT 		JOIN user_acl_role role " +
			" ON 		user.id = role.user_id " +
			" WHERE		company.operating_as_individual_flag = false " +
			" AND 		user.email_confirmed = 'Y' AND user.user_status_type_code IN ('pending', 'approved') " +
			" AND 		l.lane_type_id = 3 AND l.deleted = 0 and l.approval_status IN (1,5) AND l.verification_status = 1" +
			" AND 		role.acl_role_id = 7  AND role.deleted = 0 AND l.company_id = :companyId ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyId", companyId);

		return jdbcTemplate.queryForObject(sql, params, Integer.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findCompaniesWithAgingAlert() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setProjection(Projections.property("id"))
				.add(Restrictions.eq("manageMyWorkMarket.agingAssignmentAlertEnabled", true));
		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllCompaniesWithLowBalanceAlertEnabled() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.property("id"))
			.add(Restrictions.eq("customLowBalanceFlag", Boolean.TRUE));

		return criteria.list();
	}

	@Override
	public List<Long> findAllCompaniesWithWorkPayment(Calendar fromDate) {
		String sql = "SELECT DISTINCT company.id \n" +
			"FROM	company \n" +
			"INNER 	JOIN account_register on account_register.company_id = company.id \n" +
			"INNER 	JOIN register_transaction on account_register.id = register_transaction.account_register_id \n" +
			"WHERE 	register_transaction_type_code = :workPayment " +
			"AND 	register_transaction.transaction_date >= :fromDate ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fromDate", fromDate);
		params.addValue("workPayment", RegisterTransactionType.BUYER_WORK_PAYMENT);

		return jdbcTemplate.queryForList(sql, params, Long.class);
	}

	@Override
	public List<Long> findAllCompaniesWithWorkCancellations(Calendar fromDate) {
		String sql = "SELECT DISTINCT work_status_transition.company_id \n" +
			"FROM	work_status_transition \n" +
			"INNER 	JOIN time_dimension on time_dimension.id = work_status_transition.date_id \n" +
			"WHERE 	work_status_type_code = :cancelled " +
			"AND 	time_dimension.date >= :fromDate ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fromDate", fromDate);
		params.addValue("cancelled", WorkStatusType.CANCELLED);

		return jdbcTemplate.queryForList(sql, params, Long.class);
	}

	@Override
	public boolean isPaymentTermsEnabledForCompany(Long companyId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("c.payment_terms_enabled")
			.addTable("company c")
			.addWhereClause("c.id", SQLOperator.EQUALS, "companyId", companyId);
		return BooleanUtils.isTrue(jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Boolean.class));
	}

	@Override
	public Map<Long, Map<String, String>> getAllCompaniesForCache(Set<Long> companyIds) {
		final Map<Long, Map<String, String>> result = new HashMap<>();
		SQLBuilder builder = new SQLBuilder()
			.addColumn("c.id AS companyId")
			.addColumn("c.effective_name")
			.addColumn("c.company_status_type_code")
			.addColumn("EXISTS(SELECT bank_account.id FROM bank_account WHERE company_id = c.id AND active_flag = 'Y' AND confirmed_flag = 'Y') AS confirmedBankAccount")
			.addColumn("EXISTS(SELECT tax_entity.id FROM tax_entity WHERE company_id = c.id AND active_flag = 'Y' AND status = :approved) AS approvedTIN")
			.addTable("company c")
			.addParam("approved", TaxVerificationStatusType.APPROVED);

		if (isNotEmpty(companyIds)) {
			builder.addWhereInClause("c.id", "companyId", companyIds);
		}
		List<Map<String, Object>> companies = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row: companies) {
			Long companyId = ((Integer) row.get("companyId")).longValue();
			Map<String, String> attributes = MapUtils.getMap(result, companyId);
			if (MapUtils.isEmpty(attributes)) {
				attributes = new HashMap<>(4);
			}
			attributes.put("name", (String)row.get("effective_name"));
			attributes.put("status", (String)row.get("company_status_type_code"));
			attributes.put("confirmedBankAccount", String.valueOf(row.get("confirmedBankAccount")));
			attributes.put("approvedTIN", String.valueOf(row.get("approvedTIN")));
			result.put(companyId, attributes);
		}
		return result;
	}

	@Override
	public boolean doesCompanyHaveReservedFundsEnabledProject(Long companyId) {
		Assert.notNull(companyId);

		final String sql =
			" SELECT  count(*) "
				+ " FROM  company c "
				+ " INNER JOIN project p "
				+ " ON    p.company_id = c.id "
				+ " WHERE p.enable_reserved_funds = 1 "
				+ " AND   p.deleted = 0 "
				+ " AND   c.id = :companyId ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyId", companyId);
		logger.debug(sql);

		return (jdbcTemplate.queryForObject(sql, params, Integer.class) > 0);
	}

	@Override
	public boolean isInstantWorkerPoolEnabled(Long companyId) {
		Assert.notNull(companyId);

		final String sql =
			" SELECT  c.instant_network "
				+ " FROM  company c "
				+ " WHERE c.id = :companyId ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyId", companyId);

		return BooleanUtils.isTrue(jdbcTemplate.queryForObject(sql, params, Boolean.class));
	}

	@Override
	public Company getSharingCompany(Long userGroupId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("c.*")
			.addTable("company c")
			.addJoin("join company_network_association cna on cna.company_id = c.id")
			.addJoin("join company_role_association cra on cra.company_network_association_id = cna.id")
			.addJoin("join user_group_network_association ugna on ugna.network_id = cna.network_id")
			.addWhereClause(String.format("ugna.user_group_id = %s", userGroupId))
			.addWhereClause(String.format("cra.role_id = %s", AclRole.ACL_NETWORK_OWNER));

		return jdbcTemplate.query(builder.build(), new ResultSetExtractor<Company>() {
			@Override
			public Company extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					Company c = new Company();
					c.setId(rs.getLong("id"));
					c.setName(rs.getString("name"));
					return c;
				} else {
					return null;
				}
			}
		});
	}

	@Override
	public boolean doesCompanyHaveOverdueInvoice(Long companyId, Calendar calendar) {
		SQLBuilder builder = new SQLBuilder()
			.addColumns("IF(COUNT(*) > 0, 1, 0)")
			.addTable("invoice i")
			.addWhereClause("i.company_id = :companyId")
			.addWhereClause("i.due_date < :date")
			.addWhereClause("i.invoice_status_type_code = :status")
			.addParam("companyId", companyId)
			.addParam("date", calendar.getTime())
			.addParam("status", InvoiceStatusType.PAYMENT_PENDING);

		return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Boolean.class);
	}

	@Override
	public boolean hasWorkPastDueMoreThanXDays(long companyId, int pastDueDays) {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_YEAR, -1 * pastDueDays);
		SQLBuilder builder = new SQLBuilder()
			.addColumns("IF(COUNT(*) > 0, 1, 0)")
			.addTable("work w")
			.addWhereClause("w.company_id = :companyId")
			.addWhereClause("w.due_on < :date")
			.addWhereClause("w.work_status_type_code IN (:status)")
			.addParam("companyId", companyId)
			.addParam("date", date.getTime())
			.addParam("status", StringUtils.join(StringUtilities.surround(WorkStatusType.PAYMENT_PENDING_STATUS_TYPES, "'"), ","));

		return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Boolean.class);
	}

	@Override
	public boolean hasAtLeastOneUserWithActiveRoles(long companyId, Long... aclRoleIds) {
		if (aclRoleIds.length == 0) { return false; }

		SQLBuilder builder = new SQLBuilder()
			.addColumn("1")
			.addTable("user u")
			.addJoin("INNER JOIN user_acl_role r ON u.id = r.user_id")
			.addWhereClause("u.company_id", SQLOperator.EQUALS, "companyId", companyId)
			.addWhereClause("r.acl_role_id IN (:aclRoleIds)")
			.addWhereClause("r.deleted = 0")
			.addParam("aclRoleIds", Lists.newArrayList(aclRoleIds))
			.addLimitClause(0, 1, true);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Boolean.class).size() > 0;
	}

	@Override
	public List<Long> getUserIdsWithActiveRole(long companyId, long aclRoleId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("u.id")
			.addTable("user u")
			.addJoin("INNER JOIN user_acl_role r ON u.id = r.user_id")
			.addWhereClause("u.company_id", SQLOperator.EQUALS, "companyId", companyId)
			.addWhereClause("r.acl_role_id = (:aclRoleId)")
			.addWhereClause("r.deleted = 0")
			.addParam("aclRoleId", aclRoleId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	@Override
	public Integer getMaxCompanyId() {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("MAX(company.id) as companyId")
			.addTable("company");
		try {
			return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Long.class).intValue();
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	@Override
	public List<Long> findCompanyIdsForUserIds(List<Long> userIds) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("distinct company_id")
			.addTable("user")
			.addWhereInClause("id", "userIds", userIds);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	@Override
	public List<String> findCompanyNumbersFromCompanyIds(Collection<Long> companyIds) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("company_number")
			.addTable("company")
			.addWhereClause("id IN (:companyIds)")
			.addParam("companyIds", companyIds);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	public List<String> findCompanyUuidsByCompanyNumbers(Collection<String> companyNumbers) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("uuid")
			.addTable("company")
			.addWhereClause("company_number IN (:companyNumbers)")
			.addParam("companyNumbers", companyNumbers);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	public List<String> findCompanyUuidsByCompanyIds(Collection<Long> companyIds) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("uuid")
			.addTable("company")
			.addWhereClause("id IN (:companyIds)")
			.addParam("companyIds", companyIds);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	public List<String> findWorkerNumbers(String companyNumber) {
		final String sql =
			"SELECT distinct u.user_number " +
				"FROM user u " +
				"LEFT JOIN company c on c.id = u.company_id " +
				"LEFT JOIN user_acl_role uar ON u.id = uar.user_id " +
				"LEFT JOIN acl_role ar ON ar.id = uar.acl_role_id " +
				"WHERE c.company_number = :companyNumber " +
				"    AND u.user_status_type_code = 'approved' " +
				"    AND (u.lane3_approval_status = 1 and ar.name = 'External' and uar.deleted = 0) " +
				"ORDER BY u.created_on ASC";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyNumber", companyNumber);
		return jdbcTemplate.queryForList(sql, params, String.class);
	}

	@Override
	public List<Long> findVendorIdsFromCompanyIds(final Collection<Long> companyIds) {
		final String sql =
			"SELECT id "
				+ "FROM company "
				+ "WHERE in_vendor_search = 1 "
				+ "AND id in (:companyIds)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyIds", companyIds);
		return jdbcTemplate.queryForList(sql, params, Long.class);
	}

	@Override
	public int getTeamSize(final Long companyId) {
		final String sql =
			"SELECT COUNT(DISTINCT u.id) " +
				"FROM user u " +
				"LEFT JOIN company c on c.id = u.company_id " +
				"LEFT JOIN user_acl_role uar ON u.id = uar.user_id " +
				"LEFT JOIN acl_role ar ON ar.id = uar.acl_role_id " +
				"WHERE c.id = :companyId " +
				"    AND u.user_status_type_code = 'approved' " +
				"    AND ((u.lane3_approval_status = 1 and ar.name = 'External' and uar.deleted = 0)" +
				"         OR (u.lane3_approval_status = 4 and ar.name = 'Internal' and uar.deleted = 0)) ";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyId", companyId);
		return jdbcTemplate.queryForObject(sql, params, Integer.class);
	}

	@Override
	public List<String> findWorkerNumbersForCompanies(List<String> companyNumbers) {
		final String sql =
			"SELECT distinct u.user_number " +
				"FROM user u " +
				"LEFT JOIN company c on c.id = u.company_id " +
				"LEFT JOIN user_acl_role uar ON u.id = uar.user_id " +
				"LEFT JOIN acl_role ar ON ar.id = uar.acl_role_id " +
				"WHERE c.company_number in (:companyNumbers) " +
				"    AND u.user_status_type_code = 'approved' " +
				"    AND ((u.lane3_approval_status = 1 and ar.name = 'External' and uar.deleted = 0) " +
				"		OR (u.lane3_approval_status = 4 and ar.name = 'Internal' and uar.deleted = 0)) ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyNumbers", companyNumbers);
		return jdbcTemplate.queryForList(sql, params, String.class);
	}

	@Override
	public List<Company> suggest(String prefix, boolean vendorOnly) {
		Criteria criteria =
			getFactory().getCurrentSession().createCriteria(Company.class);
		if (vendorOnly) {
			criteria.add(Restrictions.eq("inVendorSearch", true));
		}
		criteria
			.add(ilike(prefix, MatchMode.START, "name", "effectiveName"))
			.setMaxResults(Constants.MAX_RESOURCES_SUGGESTION_RESULTS_FOR_ASSIGNMENT)
			.addOrder(Order.asc("effectiveName"));

		return criteria.list();
	}

	@Override
	public void saveOrUpdate(Company company) {
		setUuidIfEmpty(company);
		super.saveOrUpdate(company);
	}


	private void setUuidIfEmpty(Company company) {
		if (company.getUuid() == null) {
			company.setUuid(UUID.randomUUID().toString());
		}
	}

	@Override
	public void persist(Company company) {
		setUuidIfEmpty(company);
		super.persist(company);
	}

	@Override
	public void saveAll(Collection<Company> companies) {
		for (final Company company : companies) {
			setUuidIfEmpty(company);
		}
		super.saveAll(companies);
	}

	private static final class CompanyIdentityRowMapper implements RowMapper<CompanyIdentityDTO> {
		CompanyIdentityRowMapper() { }

		@Override public CompanyIdentityDTO mapRow(ResultSet rs, int i) throws SQLException {
			return new CompanyIdentityDTO(rs.getLong("id"), rs.getString("company_number"), rs.getString("uuid"));
		}
	}

}
