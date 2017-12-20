package com.workmarket.dao.tax;

import ch.lambdaj.function.matcher.Predicate;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.ForeignTaxEntity;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.exists;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.hamcrest.Matchers.is;

@Repository
public class TaxEntityDAOImpl extends AbstractDAO<AbstractTaxEntity> implements TaxEntityDAO {

	protected Class<AbstractTaxEntity> getEntityClass() {
		return AbstractTaxEntity.class;
	}

	@Qualifier("jdbcTemplate") @Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public AbstractTaxEntity findTaxEntityByIdAndCompany(long taxEntityId, long companyId) {
		String query = "FROM tax_entity WHERE id = :taxEntityId AND company_id = :companyId";

		Query hibQuery = getFactory().getCurrentSession().createQuery(query);
		hibQuery.setProperties(ImmutableMap.of("companyId", companyId, "taxEntityId", taxEntityId));
		return (AbstractTaxEntity) hibQuery.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends AbstractTaxEntity> findUnverifiedActiveTaxEntitiesByCountry(String country) {

		Class clazz = checkNotNull(getClassOfTaxEntity(country));

		return (List<? extends AbstractTaxEntity>) getFactory().getCurrentSession().createCriteria(clazz)
				.add(Restrictions.isNotNull("status"))
				.add(Restrictions.eq("status", TaxVerificationStatusType.newInstance(TaxVerificationStatusType.UNVERIFIED)))
				.add(Restrictions.eq("activeFlag", Boolean.TRUE))
				.add(Restrictions.eq("verificationPending", Boolean.FALSE))
				.addOrder(Order.desc("id"))
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends AbstractTaxEntity> findTaxEntitiesByTinAndCountry(String tin, String country) {

		Class clazz = checkNotNull(getClassOfTaxEntity(country));

		Criteria criteria = getFactory().getCurrentSession().createCriteria(clazz)
				.add(Restrictions.isNotNull("status"))
				.addOrder(Order.desc("id"))
				.add(Restrictions.eq("taxNumber", tin));

		return (List<? extends AbstractTaxEntity>) criteria.list();
	}

	@Override
	public List<AbstractTaxEntity> findAllUnverifiedTaxEntitiesWhereActiveOrRejectedExists() {

		Map<Long, AbstractTaxEntity> result = Maps.newHashMap();
		List<? extends AbstractTaxEntity> entities = findAllTaxEntitiesForCompaniesHavingUnverifiedInactiveTaxEntities();

		// convert to multimap for easier manipulation
		Multimap<Long, AbstractTaxEntity> mapEntities = ArrayListMultimap.create();
		for (AbstractTaxEntity entity : entities) {
			mapEntities.put(entity.getCompany().getId(), entity);
		}

		// for each company,
		// 		if there is an approved or rejected entity created earlier than the unverified one,
		// 		add the unverified to the result, max one entity per company (the most recent)
		for (Long companyId : mapEntities.keySet()) {

			Collection<AbstractTaxEntity> companyEntities = mapEntities.get(companyId);

			// skip companies that already have an entity pending verification
			if (exists(companyEntities, having(on(AbstractTaxEntity.class).getVerificationPending(), is(true))))
				continue;

			AbstractTaxEntity approvedEntity = selectFirst(companyEntities, new Predicate<AbstractTaxEntity>() {
				@Override public boolean apply(AbstractTaxEntity taxEntity) {
					return taxEntity.getStatus().isApproved() && taxEntity.getActiveFlag();
				}
			});
			AbstractTaxEntity rejectedActiveEntity = selectFirst(companyEntities, new Predicate<AbstractTaxEntity>() {
				@Override public boolean apply(AbstractTaxEntity taxEntity) {
					return taxEntity.getStatus().isRejected() && taxEntity.getActiveFlag();
				}
			});

			for (AbstractTaxEntity entity : companyEntities) {
				if (!entity.getStatus().isUnverified())
					continue;

				if (approvedEntity != null) {
					if (DateUtilities.isBefore(approvedEntity.getActiveDate(), entity.getActiveDate()))
						result.put(approvedEntity.getCompany().getId(), entity); // filter out dupes by company

				} else if (rejectedActiveEntity != null) {
					if (DateUtilities.isBefore(rejectedActiveEntity.getActiveDate(), entity.getActiveDate()))
						result.put(rejectedActiveEntity.getCompany().getId(), entity); // filter out dupes by company
				}
			}
		}
		return Lists.newArrayList(result.values());
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends AbstractTaxEntity> findAllApprovedTaxEntitiesByCompanyId(long companyId) {

		final List<AbstractTaxEntity> taxEntities = Lists.newArrayList();
		taxEntities.addAll(findAllUsaApprovedTaxEntitiesByCompanyId(companyId));

		taxEntities.addAll((List<AbstractTaxEntity>)getFactory().getCurrentSession().createCriteria(CanadaTaxEntity.class)
				.add(Restrictions.eq("company.id", companyId))
				.addOrder(Order.asc("activeDate"))
				.list());

		taxEntities.addAll((List<AbstractTaxEntity>)getFactory().getCurrentSession().createCriteria(ForeignTaxEntity.class)
				.add(Restrictions.eq("company.id", companyId))
				.addOrder(Order.asc("activeDate"))
				.list());

		return taxEntities;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UsaTaxEntity> findAllUsaApprovedTaxEntitiesByCompanyId(long companyId) {
		List<UsaTaxEntity> taxEntities = (List<UsaTaxEntity>) getFactory().getCurrentSession().createCriteria(UsaTaxEntity.class)
				.add(Restrictions.eq("status.code", TaxVerificationStatusType.APPROVED))
				.add(Restrictions.eq("company.id", companyId))
				.list();
		Collections.sort(taxEntities, new Comparator<UsaTaxEntity>(){
			@Override
			public int compare(UsaTaxEntity o1, UsaTaxEntity o2) {
				return firstNonNull(o1.getEffectiveDate(), o1.getActiveDate()).compareTo(
						firstNonNull(o2.getEffectiveDate(), o2.getActiveDate()));
			}
		});
		return taxEntities;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractTaxEntity> T findActiveTaxEntityByCompany(long companyId) {
		Assert.notNull(companyId);

		return (T) getFactory().getCurrentSession().createCriteria(AbstractTaxEntity.class)
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("activeFlag", true))
				.setMaxResults(1).uniqueResult();
	}

	@Override
	public boolean hasTaxEntityPendingApproval(long companyId, String taxNumber) {
		Assert.notNull(companyId);

		String query = "SELECT count(*) " +
				" FROM tax_entity " +
				" WHERE company_id = :companyId " +
				" AND status = :unverified AND tax_number = :taxNumber" ;

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("unverified", TaxVerificationStatusType.UNVERIFIED);
		params.addValue("companyId", companyId);
		params.addValue("taxNumber", taxNumber);

		return jdbcTemplate.queryForObject(query, params, Integer.class) > 0;
	}

	@Override
	public boolean hasTaxEntityPendingApproval(long companyId) {
		Assert.notNull(companyId);

		String query = "SELECT count(*) " +
				" FROM tax_entity " +
				" WHERE company_id = :companyId " +
				" AND status = :unverified " ;

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("unverified", TaxVerificationStatusType.UNVERIFIED);
		params.addValue("companyId", companyId);

		return jdbcTemplate.queryForObject(query, params, Integer.class) > 0;
	}

	@Override
	public Set<Long> findAllCompaniesWithMultipleApprovedTaxEntities(DateRange dateRange) {
		String query = "SELECT 	company_id " +
				" FROM 		tax_entity tax " +
				" WHERE 	status = :approved " +
				" AND 		EXISTS  (SELECT tax_entity.id FROM tax_entity WHERE tax_entity.status = :approved AND tax_entity.company_id = tax.company_id  " +
				" 			GROUP 	BY 		company_id "+
				" 			HAVING 	COUNT(tax_entity.id) > 1) " +
				" AND  		NOT EXISTS (SELECT * FROM tax_entity WHERE status = :approved AND active_flag = 'Y' " +
				" 			AND		(effective_date is null or effective_date <= :fromDate) " +
				" 			AND		(active_date is null or active_date <= :fromDate) AND company_id = tax.company_id ) " +
				" ORDER 	BY company_id" ;

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("approved", TaxVerificationStatusType.APPROVED);
		params.addValue("fromDate", dateRange.getFrom());
		params.addValue("toDate", dateRange.getThrough());

		Set<Long> companies = Sets.newHashSet();
		List<Map<String, Object>> results = jdbcTemplate.queryForList(query, params);
		for (Map<String, Object> row : results) {
			companies.add(((Integer) row.get("company_id")).longValue());
		}
		return companies;
	}

	@Override
	public Set<Long> getAllCompaniesWithFirstTaxEntityInPeriodAndNoTaxReportForYear(DateRange dateRange, int taxReportYearToExclude) {
		Assert.notNull(dateRange);
		Assert.notNull(dateRange.getFrom());
		Assert.notNull(dateRange.getThrough());
		String sql = " SELECT 	MIN(tax_entity.active_date) activeDate, company_id AS companyId \n" +
				" FROM	tax_entity " +
				" GROUP	BY company_id " +
				" HAVING	activeDate BETWEEN :fromDate AND :toDate " +
				" AND 		NOT EXISTS (SELECT earning_report.id FROM earning_report " +
				" INNER 	JOIN earning_report_set ON earning_report_set.id = earning_report_set_id " +
				" WHERE 	earning_report_set.tax_year = :taxReportYearToExclude " +
				" AND 		tax_report_set_status_type_code = :published AND tax_entity.company_id =  company_id) " +
				" AND 		EXISTS ( " +
				" SELECT	company_id AS companyId  " +
				" FROM      account_register ar  " +
				" INNER     JOIN register_transaction rt ON rt.account_register_id = ar.id  " +
				" WHERE    	rt.pending_flag = 'N'  " +
				" AND		rt.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.EARNINGS_TRANSACTION_TYPE_CODES, "'"), ",") + ")" +
				" AND		rt.effective_date < :toDate " +
				" AND		tax_entity.company_id = ar.company_id)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("toDate", dateRange.getThrough())
				.addValue("fromDate", dateRange.getFrom())
				.addValue("taxReportYearToExclude", taxReportYearToExclude)
				.addValue("published", TaxReportSetStatusType.PUBLISHED);

		return Sets.newHashSet(this.jdbcTemplate.query(sql, params, new RowMapper<Long>() {
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("companyId");
			}
		}));
	}

	@Override
	public List<Long> getAllActivatedAccountIds() {
		String sql = "SELECT DISTINCT t.id"
			+ " FROM tax_entity t INNER JOIN company c ON t.company_id=c.id"
			+ " INNER JOIN user u ON u.company_id=c.id"
			+ " WHERE u.user_status_type_code in ('"
				+ Joiner.on("','").join(UserStatusType.ACTIVE_USER_STATUS_TYPES) + "')";

		return jdbcTemplate.queryForList(sql, new MapSqlParameterSource(), Long.class);
	}

	@Override
	public List<Long> getAccountIdsFromId(final long fromId) {
		String sql = "SELECT t.id FROM tax_entity t WHERE t.id >= " + fromId;

		return jdbcTemplate.queryForList(sql, new MapSqlParameterSource(), Long.class);
	}

	@Override
	public List<? extends AbstractTaxEntity> findAllTaxEntitiesFromModifiedDate(final Calendar fromModifiedDate) {
		String query = "FROM tax_entity WHERE modified_on >= :modifiedOn";

		return (List<? extends AbstractTaxEntity>) getFactory().getCurrentSession().createQuery(query)
			.setProperties(CollectionUtilities.newObjectMap("modifiedOn", fromModifiedDate))
			.list();
	}

	@Override
	public List<? extends AbstractTaxEntity> findAllAccountsFromId(final long fromId) {
		String query = "FROM tax_entity WHERE id >= :fromId";

		return (List<? extends AbstractTaxEntity>) getFactory().getCurrentSession().createQuery(query)
				.setProperties(CollectionUtilities.newObjectMap("fromId", fromId))
				.list();
	}

	@SuppressWarnings("unchecked")
	private List<? extends AbstractTaxEntity> findAllTaxEntitiesForCompaniesHavingUnverifiedInactiveTaxEntities() {
		String query = String.format(
				"FROM tax_entity " +
						"WHERE company.id IN " +
						"(SELECT company.id FROM tax_entity WHERE status = '%s' AND activeFlag='N') " +
						"ORDER BY company.id, activeDate", TaxVerificationStatusType.UNVERIFIED);

		return (List<? extends AbstractTaxEntity>) getFactory().getCurrentSession().createQuery(query).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends AbstractTaxEntity> findAllTaxEntitiesByCompany(long companyId) {

		String query = "FROM tax_entity WHERE company_id = :companyId";

		return (List<? extends AbstractTaxEntity>) getFactory().getCurrentSession().createQuery(query)
				.setProperties(CollectionUtilities.newObjectMap("companyId", companyId))
				.list();
	}

	private Class getClassOfTaxEntity(String country) {
		switch (country) {
			case AbstractTaxEntity.COUNTRY_CANADA:
				return CanadaTaxEntity.class;
			case AbstractTaxEntity.COUNTRY_USA:
				return UsaTaxEntity.class;
			case AbstractTaxEntity.COUNTRY_OTHER:
				return ForeignTaxEntity.class;
		}
		return null;
	}
}
