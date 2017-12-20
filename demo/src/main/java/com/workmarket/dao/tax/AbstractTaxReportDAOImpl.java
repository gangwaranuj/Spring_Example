package com.workmarket.dao.tax;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.EarningDetailReport;
import com.workmarket.domains.model.tax.EarningDetailReportDownloadAudit;
import com.workmarket.domains.model.tax.EarningDetailReportRow;
import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxReportDetailRow;
import com.workmarket.domains.model.tax.TaxReportRow;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import com.workmarket.domains.model.tax.TaxServiceReport;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.factory.TaxEntityFactory;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@SuppressWarnings(value = "unchecked")
public class AbstractTaxReportDAOImpl extends AbstractDAO<AbstractTaxReport> implements AbstractTaxReportDAO {

	@Autowired TaxEntityFactory taxEntityFactory;

	protected Class<AbstractTaxReport> getEntityClass() {
		return AbstractTaxReport.class;
	}

	@Qualifier("jdbcTemplate") @Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	@SuppressWarnings("unchecked")
	public List<TaxForm1099> findAvailable1099s(Long companyId) {
		Criteria query = getFactory().getCurrentSession()
				.createCriteria(TaxForm1099.class)
				.setFetchMode("taxForm1099Set", FetchMode.JOIN)
				.createAlias("taxForm1099Set", "taxForm1099Set")
				.add(Restrictions.eq("companyId", companyId))
				.add(Restrictions.eq("taxForm1099Set.taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.addOrder(Order.asc("taxYear"));
		return query.list();
	}

	@Override
	public List<EarningReport> findAvailableEarningReports(Long companyId) {
		Criteria query = getFactory().getCurrentSession()
				.createCriteria(EarningReport.class)
				.setFetchMode("earningReportSet", FetchMode.JOIN)
				.createAlias("earningReportSet", "earningReportSet")
				.add(Restrictions.eq("companyId", companyId))
				.add(Restrictions.eq("earningReportSet.taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.addOrder(Order.asc("taxYear"));
		return query.list();
	}

	@Override
	public List<EarningDetailReport> findAvailableEarningDetailReportsByCompanyAndReportSetId(Long userId, Long companyId, Long earningDetailResultSetId)  {
		EarningDetailReportDownloadAudit audit = new EarningDetailReportDownloadAudit();
		audit.setUserId(userId);
		audit.setDownloadedOn(Calendar.getInstance());
		audit.setEarningDetailReportSetId(earningDetailResultSetId);
		getFactory().getCurrentSession().saveOrUpdate(audit);

		Criteria query = getFactory().getCurrentSession()
			.createCriteria(EarningDetailReport.class)
			.setFetchMode("earningDetailReportSet", FetchMode.JOIN)
			.createAlias("earningDetailReportSet", "earningDetailReportSet")
			.add(Restrictions.eq("buyerCompanyId", companyId))
			.add(Restrictions.eq("earningDetailReportSet.taxReportSetStatusType.code",
				TaxReportSetStatusType.PUBLISHED))
			.add(Restrictions.eq("earningDetailReportSet.id", earningDetailResultSetId))
			.addOrder(Order.asc("taxYear"));
		return query.list();
	}

	@Override
	public List<EarningDetailReportSet> findAvailableEarningDetailReportSets(Long companyId) {
		DetachedCriteria subCriteria = DetachedCriteria.forClass(EarningDetailReport.class)
				.setProjection(Projections.distinct(Projections.property("earningDetailReportSet.id")))
				.add(Restrictions.eq("buyerCompanyId", companyId));

		Criteria query = getFactory().getCurrentSession()
				.createCriteria(EarningDetailReportSet.class)
				.add(Restrictions.eq("taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.add(Subqueries.propertyIn("id", subCriteria))
				.addOrder(Order.asc("taxYear"));

		return query.list();
	}

	@Override
	public List<TaxServiceReport> findAvailableTaxServiceDetailReports(Long companyId) {
		// Get tax service detail report by buyer tax entity
		Criteria query = getFactory().getCurrentSession()
				.createCriteria(TaxServiceReport.class)
				.setFetchMode("taxServiceReportSet", FetchMode.JOIN)
				.createAlias("taxServiceReportSet", "taxServiceReportSet")
				.add(Restrictions.eq("buyerCompanyId", companyId))
				.add(Restrictions.eq("taxServiceReportSet.taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.addOrder(Order.asc("taxYear"));
		return query.list();
	}

	@Override
	public List<TaxServiceReport> findAvailableTaxServiceDetailReportsByTaxEntityCompany(Long companyId) {
		// Get tax service detail report by worker tax entity
		// Only get Client's TIN with more than $600
		Assert.notNull(companyId);
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("tsdr.id, earnings, expenses, use_wm_tax_entity, tsdr.created_on ")
				.addTable("tax_service_detail_report tsdr")
				.addJoin("INNER JOIN tax_service_detail_report_set tsdrs on tsdr.tax_service_detail_report_set_id = tsdrs.id")
				.addWhereClause("tsdrs.tax_report_set_status_type_code = :publishedStatus")
				.addWhereClause("tsdr.company_id = :companyId ")
				.addWhereClause("earnings + expenses >= :threshold ")
				.addWhereClause("use_wm_tax_entity = 0")
				.addParam("threshold", Constants.TAX_FORM_1099_GENERATION_AMOUNT_THRESHOLD)
				.addParam("publishedStatus", TaxReportSetStatusType.PUBLISHED)
				.addParam("companyId", companyId);

		return this.jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper() {
			public TaxServiceReport mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxServiceReport row = new TaxServiceReport();
				row.setId(rs.getLong("id"));
				row.setEarnings(rs.getBigDecimal("earnings"));
				row.setExpenses(rs.getBigDecimal("expenses"));
				row.setUseWMTaxEntity(rs.getBoolean("use_wm_tax_entity"));
				row.setCreatedOn(DateUtilities.getCalendarFromDate(rs.getDate("created_on")));
				row.setCreatorId(Constants.WM_COMPANY_ID);
				return row;
			}
		});

	}

	@Override
	public boolean doesUserHaveRESTaxDetailReport(Long companyId) {
		SQLBuilder resBuilder = new SQLBuilder();
		resBuilder.addColumns("COUNT(*)")
				.addTable("tax_service_detail_report tsdr")
				.addJoin("INNER JOIN tax_service_detail_report_set tsdrs on tsdr.tax_service_detail_report_set_id = tsdrs.id")
				.addWhereClause("tsdrs.tax_report_set_status_type_code = :publishedStatus")
				.addWhereClause("tsdr.company_id = :companyId ")
				.addWhereClause("earnings + expenses >= :threshold ")
				.addWhereClause("use_wm_tax_entity = 1")
				.addParam("threshold", Constants.TAX_FORM_1099_GENERATION_AMOUNT_THRESHOLD)
				.addParam("publishedStatus", TaxReportSetStatusType.PUBLISHED)
				.addParam("companyId", companyId);

		return jdbcTemplate.queryForObject(resBuilder.build(), resBuilder.getParams(), Integer.class) > 0;
	}

	@Override
	public Optional<TaxServiceReport> getRESTaxServiceReportForYear(int year, Long companyId) {

		// Get tax service detail report by worker tax entity
		// Only get RES's TIN with more than $600

		Criteria query = getFactory().getCurrentSession()
				.createCriteria(TaxServiceReport.class)
				.setFetchMode("taxServiceReportSet", FetchMode.JOIN)
				.createAlias("taxServiceReportSet", "taxServiceReportSet")
				.add(Restrictions.eq("companyId", companyId))
				.add(Restrictions.eq("taxServiceReportSet.taxYear", year))
				.add(Restrictions.eq("taxServiceReportSet.taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.addOrder(Order.asc("taxYear"));
		List<TaxServiceReport> reportList = query.list();
		TaxServiceReport report = reportList.get(0);


		SQLBuilder resBuilder = new SQLBuilder();
		resBuilder.addColumns("sum(earnings) as earnings, sum(work_earned_amount) as work_earned_amount, sum(payment_reversal_amount) as payment_reversal_amount ")
				.addColumns("sum(marketing_payments_amount) as marketing_payments_amount, sum(expenses) as expenses")
				.addTable("tax_service_detail_report tsdr")
				.addJoin("INNER JOIN tax_service_detail_report_set tsdrs on tsdr.tax_service_detail_report_set_id = tsdrs.id")
				.addWhereClause("tsdrs.tax_report_set_status_type_code = :publishedStatus")
				.addWhereClause("tsdr.company_id = :companyId ")
				.addWhereClause("earnings + expenses >= :threshold ")
				.addWhereClause("use_wm_tax_entity = 1")
				.addParam("threshold", Constants.TAX_FORM_1099_GENERATION_AMOUNT_THRESHOLD)
				.addParam("publishedStatus", TaxReportSetStatusType.PUBLISHED)
				.addParam("companyId", companyId);

		List<TaxServiceReport> resTaxReportList = jdbcTemplate.query(resBuilder.build(), resBuilder.getParams(), new RowMapper() {
			public TaxServiceReport mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxServiceReport row = new TaxServiceReport();
				row.setEarnings(rs.getBigDecimal("earnings"));
				row.setWorkPayments(rs.getBigDecimal("work_earned_amount"));
				row.setPaymentReversals(rs.getBigDecimal("payment_reversal_amount"));
				row.setMarketingPayments(rs.getBigDecimal("marketing_payments_amount"));
				row.setExpenses(rs.getBigDecimal("expenses"));
				return row;
			}
		});

		TaxServiceReport sumReport = resTaxReportList.get(0);
		sumReport.setUseWMTaxEntity(true);
		sumReport.setTaxServiceReportSet(report.getTaxServiceReportSet());
		sumReport.setTaxEntity(report.getTaxEntity());
		sumReport.setAddress(report.getAddress());
		sumReport.setCity(report.getCity());
		sumReport.setState(report.getState());
		sumReport.setPostalCode(report.getPostalCode());
		sumReport.setCountry(report.getCountry());
		sumReport.setFirstName(report.getFirstName());
		sumReport.setLastName(report.getLastName());
		sumReport.setBusinessFlag(report.getBusinessFlag());

		return Optional.fromNullable(sumReport);
	}

	@Override
	public List<EarningDetailReportRow> getEarningDetailReportForUserInYear(Long companyId, int year) {
		Assert.notNull(companyId);

		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("company.name AS company_name, edr.earnings, edr.expenses ")
				.addTable("earning_detail_report edr ")
				.addJoin("INNER JOIN earning_detail_report_set edrs on edr.earning_detail_report_set_id = edrs.id ")
				.addJoin("INNER JOIN company on company.id = edr.buyer_company_id ")
				.addWhereClause("edrs.tax_report_set_status_type_code = :publishedStatus ")
				.addWhereClause("edr.company_id = :companyId ")
				.addWhereClause("edr.tax_year = :year ")
				.addParam("publishedStatus", TaxReportSetStatusType.PUBLISHED)
				.addParam("year", year)
				.addParam("companyId", companyId);


		return this.jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper() {
			public EarningDetailReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
				EarningDetailReportRow row = new EarningDetailReportRow();
				row.setBuyerCompanyName(rs.getString("company_name"));
				row.setEarnings(rs.getBigDecimal("earnings"));
				row.setExpenses(rs.getBigDecimal("expenses"));
				return row;
			}
		});
	}

	@Override
	public List<TaxReportDetailRow> getTaxDetailReportForUserInYear(Long companyId, int year) {

		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("company.name AS company_name, tsdr.earnings, tsdr.expenses, tsdr.use_wm_tax_entity ")
				.addTable("tax_service_detail_report tsdr ")
				.addJoin("INNER JOIN tax_service_detail_report_set tsdrs on tsdr.tax_service_detail_report_set_id = tsdrs.id ")
				.addJoin("INNER JOIN company on company.id = tsdr.buyer_company_id ")
				.addWhereClause("tsdrs.tax_report_set_status_type_code = :publishedStatus ")
				.addWhereClause("tsdr.company_id = :companyId ")
				.addWhereClause("tsdr.tax_year = :year ")
				.addParam("publishedStatus", TaxReportSetStatusType.PUBLISHED)
				.addParam("year", year)
				.addParam("companyId", companyId);

		return this.jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper() {
			public TaxReportDetailRow mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxReportDetailRow row = new TaxReportDetailRow();
				row.setBuyerCompanyName(rs.getString("company_name"));
				row.setEarnings(rs.getBigDecimal("earnings"));
				row.setExpenses(rs.getBigDecimal("expenses"));
				row.setUseWMTaxEntity(rs.getBoolean("use_wm_tax_entity"));
				return row;
			}
		});


	}

	@Override
	public List<TaxForm1099> findAllTaxForm1099NoHibernate(final long taxForm1099SetId) {
		final String sql =
			"SELECT tf.id, tf.amount, tf.country, tf.postal_code, tf.state, tf.city, tf.address, tf.last_name," +
				" tf.first_name, tf.business_flag as taxFormBusinessFlag, tf.tax_entity_id, te.tax_number," +
				" te.country as taxEntityCountry, te.business_flag as taxEntityBusinessFlag" +
				" FROM tax_form_1099 tf INNER JOIN tax_entity te on te.id = tf.tax_entity_id" +
				" WHERE tf.tax_form_1099_set_id = :taxForm1099SetId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("taxForm1099SetId", taxForm1099SetId);

		return this.jdbcTemplate.query(sql, params, new RowMapper<TaxForm1099>() {
			@Override
			public TaxForm1099 mapRow(final ResultSet rs, final int i) throws SQLException {
				TaxForm1099 form = new TaxForm1099();
				form.setAmount(rs.getBigDecimal("amount"));
				form.setId(rs.getLong("id"));
				form.setCountry(rs.getString("country"));
				form.setPostalCode(rs.getString("postal_code"));
				form.setState(rs.getString("state"));
				form.setAddress(rs.getString("address"));
				form.setCity(rs.getString("city"));
				form.setLastName(rs.getString("last_name"));
				form.setFirstName(rs.getString("first_name"));
				form.setBusinessFlag(rs.getBoolean("taxFormBusinessFlag"));

				final AbstractTaxEntity abstractTaxEntity = getTaxEntityFromResultSet(rs);
				form.setTaxEntity(abstractTaxEntity);

				return form;
			}
		});
	}

	@Override
	public List<EarningReport> findAllEarningReportsNoHibernate(final long earningReportSetId) {
		final String sql =
			"SELECT er.id, er.country, er.postal_code, er.state, er.city, er.address, er.last_name, er.first_name," +
				" er.business_flag as taxFormBusinessFlag, er.earnings, er.expenses, er.tax_entity_id, te.tax_number," +
				" te.country as taxEntityCountry, te.business_flag as taxEntityBusinessFlag" +
				" FROM earning_report er INNER JOIN tax_entity te on te.id = er.tax_entity_id" +
				" WHERE er.earning_report_set_id = :earningReportSetId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("earningReportSetId", earningReportSetId);

		return this.jdbcTemplate.query(sql, params, new RowMapper<EarningReport>() {
			@Override
			public EarningReport mapRow(final ResultSet rs, final int i) throws SQLException {
				EarningReport form = new EarningReport();
				form.setId(rs.getLong("id"));
				form.setCountry(rs.getString("country"));
				form.setPostalCode(rs.getString("postal_code"));
				form.setState(rs.getString("state"));
				form.setAddress(rs.getString("address"));
				form.setCity(rs.getString("city"));
				form.setLastName(rs.getString("last_name"));
				form.setFirstName(rs.getString("first_name"));
				form.setBusinessFlag(rs.getBoolean("taxFormBusinessFlag"));
				form.setEarnings(rs.getBigDecimal("earnings"));
				form.setExpenses(rs.getBigDecimal("expenses"));

				final AbstractTaxEntity abstractTaxEntity = getTaxEntityFromResultSet(rs);
				form.setTaxEntity(abstractTaxEntity);

				return form;
			}
		});
	}

	@Override
	public List<EarningDetailReport> findAllEarningDetailReportsNoHibernate(final long earningDetailReportSetId) {
		final String sql =
			"SELECT edr.id, edr.company_id, edr.country, edr.postal_code, edr.state, edr.city, edr.address," +
				" edr.buyer_company_id, edr.last_name, edr.first_name, edr.business_flag as taxFormBusinessFlag," +
				" edr.earnings, edr.expenses, edr.tax_entity_id, te.tax_number, te.country as taxEntityCountry," +
				" te.business_flag as taxEntityBusinessFlag" +
				" FROM earning_detail_report edr INNER JOIN tax_entity te on te.id = edr.tax_entity_id" +
				" WHERE edr.earning_detail_report_set_id = :earningDetailReportSetId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("earningDetailReportSetId", earningDetailReportSetId);

		return this.jdbcTemplate.query(sql, params, new RowMapper<EarningDetailReport>() {
			@Override
			public EarningDetailReport mapRow(final ResultSet rs, final int i) throws SQLException {
				EarningDetailReport form = new EarningDetailReport();
				form.setId(rs.getLong("id"));
				form.setCompanyId(rs.getLong("company_id"));
				form.setCountry(rs.getString("country"));
				form.setPostalCode(rs.getString("postal_code"));
				form.setState(rs.getString("state"));
				form.setAddress(rs.getString("address"));
				form.setCity(rs.getString("city"));
				form.setLastName(rs.getString("last_name"));
				form.setFirstName(rs.getString("first_name"));
				form.setBusinessFlag(rs.getBoolean("taxFormBusinessFlag"));
				form.setEarnings(rs.getBigDecimal("earnings"));
				form.setExpenses(rs.getBigDecimal("expenses"));
				form.setBuyerCompanyId(rs.getLong("buyer_company_id"));

				final AbstractTaxEntity abstractTaxEntity = getTaxEntityFromResultSet(rs);
				form.setTaxEntity(abstractTaxEntity);

				return form;
			}
		});
	}

	@Override
	public List<TaxServiceReport> findAllTaxServiceReportsNoHibernate(final long taxServiceDetailSetId) {
		final String sql =
			"SELECT tsr.id, tsr.company_id, tsr.country, tsr.postal_code, tsr.state, tsr.city, tsr.address," +
				" tsr.buyer_company_id, tsr.last_name, tsr.first_name, tsr.business_flag as taxFormBusinessFlag," +
				" tsr.earnings, tsr.expenses, tsr.tax_entity_id, te.tax_number, te.country as taxEntityCountry," +
				" te.business_flag as taxEntityBusinessFlag" +
				" FROM tax_service_detail_report tsr INNER JOIN tax_entity te on te.id = tsr.tax_entity_id" +
				" WHERE tsr.tax_service_detail_report_set_id = :taxServiceDetailSetId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("taxServiceDetailSetId", taxServiceDetailSetId);

		return this.jdbcTemplate.query(sql, params, new RowMapper<TaxServiceReport>() {
			@Override
			public TaxServiceReport mapRow(final ResultSet rs, final int i) throws SQLException {
				TaxServiceReport form = new TaxServiceReport();
				form.setId(rs.getLong("id"));
				form.setCompanyId(rs.getLong("company_id"));
				form.setCountry(rs.getString("country"));
				form.setPostalCode(rs.getString("postal_code"));
				form.setState(rs.getString("state"));
				form.setAddress(rs.getString("address"));
				form.setCity(rs.getString("city"));
				form.setLastName(rs.getString("last_name"));
				form.setFirstName(rs.getString("first_name"));
				form.setBusinessFlag(rs.getBoolean("taxFormBusinessFlag"));
				form.setEarnings(rs.getBigDecimal("earnings"));
				form.setExpenses(rs.getBigDecimal("expenses"));
				form.setBuyerCompanyId(rs.getLong("buyer_company_id"));

				final AbstractTaxEntity abstractTaxEntity = getTaxEntityFromResultSet(rs);
				form.setTaxEntity(abstractTaxEntity);

				return form;
			}
		});
	}

	private AbstractTaxEntity getTaxEntityFromResultSet(final ResultSet rs) throws RuntimeException {
		AbstractTaxEntity entity;
		try {
			entity = taxEntityFactory.newInstance(rs.getString("taxEntityCountry"));
			entity.setId(rs.getLong("tax_entity_id"));
			entity.setTaxNumber(rs.getString("tax_number"));
			entity.setBusinessFlag(rs.getBoolean("taxEntityBusinessFlag"));
		} catch (InstantiationException|SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException("error fetching tax entity country");
		}
		return entity;
	}

	@Override
	public List<? extends AbstractTaxReport> getAllReports(final Calendar fromCreatedOn) {
		Criteria criteria = getFactory()
			.getCurrentSession()
			.createCriteria(getEntityClass())
			.add(Restrictions.ge("createdOn", fromCreatedOn));
		return criteria.list();
	}

	@Override
	public <T extends AbstractTaxReport> T get(Class clazz, long id) {
		Assert.isAssignable(getEntityClass(), clazz);
		return (T) getFactory().getCurrentSession().createCriteria(clazz)
				.add(Restrictions.eq("id", id)).uniqueResult();
	}

	@Override
	public boolean isTax1099ReportYearAvailable(int taxYear) {
		String query = "SELECT COUNT(*) FROM tax_form_1099_set WHERE tax_report_set_status_type_code = :publishedStatus AND tax_year = :taxYear";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("taxYear", taxYear);
		params.addValue("publishedStatus", TaxReportSetStatusType.PUBLISHED);
		return jdbcTemplate.queryForObject(query, params, Integer.class) == 0;
	}

	@Override
	public boolean isEarningReportYearAvailable(int taxYear) {
		String query = "SELECT COUNT(*) FROM earning_report_set WHERE tax_report_set_status_type_code = :publishedStatus AND tax_year = :taxYear";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("taxYear", taxYear);
		params.addValue("publishedStatus", TaxReportSetStatusType.PUBLISHED);
		return jdbcTemplate.queryForObject(query, params, Integer.class) == 0;
	}

	@Override
	public boolean isEarningDetailReportYearAvailable(int taxYear) {
		String query = "SELECT COUNT(*) FROM earning_detail_report_set WHERE tax_report_set_status_type_code = :publishedStatus AND tax_year = :taxYear";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("taxYear", taxYear);
		params.addValue("publishedStatus", TaxReportSetStatusType.PUBLISHED);
		return jdbcTemplate.queryForObject(query, params, Integer.class) == 0;
	}

	@Override
	public boolean isTaxServiceDetailReportYearAvailable(int taxYear) {
		String query = "SELECT COUNT(*) FROM tax_service_detail_report_set WHERE tax_report_set_status_type_code = :publishedStatus AND tax_year = :taxYear";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("taxYear", taxYear);
		params.addValue("publishedStatus", TaxReportSetStatusType.PUBLISHED);
		return jdbcTemplate.queryForObject(query, params, Integer.class) == 0;
	}


	// For earning detail report and tax service report.
	@Override
	public List<TaxReportDetailRow> getAllCompaniesWithEarningsInPeriodByBuyerCompany(DateRange dateRange, BigDecimal amountThreshold, List<String> accountServiceType) {
		Assert.notNull(dateRange);
		Assert.notNull(dateRange.getFrom());
		Assert.notNull(dateRange.getThrough());
		Assert.notNull(accountServiceType);
		SQLBuilder builder = newTaxReportDetailSqlBuilder(dateRange.getFrom(), dateRange.getThrough(), accountServiceType)
			.addWhereClause("EXISTS(SELECT te.id FROM tax_entity te WHERE te.company_id = company.id AND active_flag = 'Y')");

		return this.jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper() {
			public TaxReportDetailRow mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxReportDetailRow row = new TaxReportDetailRow();
				row.setWorkPayments(rs.getBigDecimal("amount"));
				row.setCompanyId(rs.getLong("resource_company_id"));
				row.setBuyerCompanyId(rs.getLong("buyer_company_id"));
				return row;
			}
		});

	}

	@Override
	public TaxReportDetailRow getEarningsInPeriodByCompanyAndBuyerCompany(DateRange dateRange, long resourceCompanyId, long buyerCompanyId, List<String> accountServiceType) {
		Assert.notNull(dateRange);
		Assert.notNull(dateRange.getFrom());
		Assert.notNull(dateRange.getThrough());
		Assert.notNull(accountServiceType);
		SQLBuilder builder = newTaxReportDetailSqlBuilder(dateRange.getFrom(), dateRange.getThrough(), accountServiceType);

		builder.addWhereClause("company.id = :companyId")
				.addWhereClause("w.company_id = :buyerCompanyId")
				.addParam("companyId", resourceCompanyId)
				.addParam("buyerCompanyId", buyerCompanyId);

		List<TaxReportDetailRow> taxReportDetailRows = jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper() {
			public TaxReportDetailRow mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxReportDetailRow row = new TaxReportDetailRow();
				row.setWorkPayments(rs.getBigDecimal("amount"));
				row.setCompanyId(rs.getLong("resource_company_id"));
				row.setBuyerCompanyId(rs.getLong("buyer_company_id"));
				return row;
			}
		});
		return taxReportDetailRows.isEmpty() ? new TaxReportDetailRow() : taxReportDetailRows.get(0);
	}

	@Override
	public Set<Long> findAllCompaniesWithNonVorEarningsDetailsForCompany(DateRange dateRange, long resourceCompanyId, List<String> accountServiceType) {
		Assert.notNull(dateRange);
		Assert.notNull(dateRange.getFrom());
		Assert.notNull(dateRange.getThrough());
		Assert.notNull(accountServiceType);
		SQLBuilder builder = newTaxReportDetailSqlBuilder(dateRange.getFrom(), dateRange.getThrough(), accountServiceType);

		builder.addWhereClause("company.id = :companyId")
				.addParam("companyId", resourceCompanyId);

		return Sets.newHashSet(jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper() {
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("buyer_company_id");
			}
		}));
	}

	@Override
	public List<TaxReportRow> getAllCompaniesWithEarningsInPeriod(DateRange dateRange, BigDecimal amountThreshold, List<String> accountServiceType) {
		Assert.notNull(dateRange);
		Assert.notNull(dateRange.getFrom());
		Assert.notNull(dateRange.getThrough());
		SQLBuilder builder = newTaxReportEarningSqlBuilder(dateRange.getFrom(), dateRange.getThrough(), accountServiceType);

		if (amountThreshold != null) {
			builder.addHavingClause("amount >= :amountThreshold")
					.addParam("amountThreshold", amountThreshold);
		}

		if (AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES.equals(accountServiceType)) {
			return itemizeTaxReportResults(builder);
		} else {
			return this.jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper() {
				public TaxReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
					TaxReportRow row = new TaxReportRow();
					row.setEarnings(rs.getBigDecimal("amount"));
					row.setCompanyId(rs.getLong("companyId"));
					return row;
				}
			});
		}
	}

	private List<TaxReportRow> itemizeTaxReportResults(SQLBuilder sqlBuilder) {
		Map<Integer, TaxReportRow> taxReportMap = Maps.newLinkedHashMap();
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlBuilder.build(), sqlBuilder.getParams());
		for (Map<String, Object> row : results) {
			String transactionCode = (String) row.get("register_transaction_type_code");
			String accountServiceType = (String) row.get("accountServiceType");
			BigDecimal amount = (BigDecimal) row.get("amount");
			Integer companyId = (Integer) row.get("companyId");

			TaxReportRow taxReportRow = (TaxReportRow)MapUtils.getObject(taxReportMap, companyId, new TaxReportRow(companyId.longValue()));
			taxReportRow.setItemizedAmount(amount, transactionCode, accountServiceType);
			taxReportMap.put(companyId, taxReportRow);
		}
		return Lists.newArrayList(taxReportMap.values());
	}

	public EarningReport findEarningReportForUser(long companyId, Integer year) {
		String query = "SELECT * from earning_report earningReport " + "INNER JOIN  earning_report_set set ON earningReport.earning_report_set_id = set.id "
				+ "WHERE earningReport.company_id = :companyId AND earningReport.tax_year = :taxYear AND set.status = :publishedStatus " + "LIMIT 1";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyId", companyId);
		params.addValue("taxYear", year);
		params.addValue("publishedStatus", TaxReportSetStatusType.PUBLISHED);
		return jdbcTemplate.queryForObject(query, params, EarningReport.class);
	}

	@Override
	public List<TaxForm1099> findAllTaxForm1099ByTaxForm1099SetId(long taxForm1099SetId) {
		Criteria query = getFactory().getCurrentSession()
				.createCriteria(TaxForm1099.class)
				.createAlias("taxForm1099Set", "taxForm1099Set")
				.add(Restrictions.eq("taxForm1099Set.id", taxForm1099SetId));
		return query.list();
	}

	public List<EarningReport> findAllEarningReportByEarningReportSetId(long earningReportSetId) {
		Criteria query = getFactory().getCurrentSession()
				.createCriteria(EarningReport.class)
				.createAlias("earningReportSet", "earningReportSet")
				.add(Restrictions.eq("earningReportSet.id", earningReportSetId));
		return query.list();
	}

	@Override
	public List<EarningDetailReport> findAllEarningDetailReportByEarningReportSetId(long earningDetailReportSetId) {
		Criteria query = getFactory().getCurrentSession()
				.createCriteria(EarningDetailReport.class)
				.createAlias("earningDetailReportSet", "earningDetailReportSet")
				.add(Restrictions.eq("earningDetailReportSet.id", earningDetailReportSetId));
		return query.list();
	}

	@Override
	public List<TaxServiceReport> findAllTaxServiceReportByReportSetId(long taxServiceReportSetId) {
		Criteria query = getFactory().getCurrentSession()
				.createCriteria(TaxServiceReport.class)
				.createAlias("taxServiceReportSet", "taxServiceReportSet")
				.add(Restrictions.eq("taxServiceReportSet.id", taxServiceReportSetId));
		return query.list();
	}

	@Override
	public TaxReportRow getEarningsInPeriodByCompany(Calendar fromDate, Calendar toDate, long companyId, List<String> accountServiceType) {
		SQLBuilder builder = newTaxReportEarningSqlBuilder(fromDate, toDate, accountServiceType);
		builder.addWhereClause("company.id = :companyId")
				.addParam("companyId", companyId);

		List<TaxReportRow> taxReportRows;
		if (AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES.equals(accountServiceType)) {
			taxReportRows = itemizeTaxReportResults(builder);
		} else {
			taxReportRows = this.jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper() {
				public TaxReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
					TaxReportRow row = new TaxReportRow();
					row.setEarnings(rs.getBigDecimal("amount"));
					row.setCompanyId(rs.getLong("companyId"));
					return row;
				}
			});
		}
		return taxReportRows.isEmpty() ? new TaxReportRow(companyId) : taxReportRows.get(0);
	}

	private SQLBuilder newTaxReportEarningSqlBuilder(Calendar fromDate, Calendar toDate, List<String> accountServiceType) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("COALESCE(SUM(rt.amount),0) amount", "company.id AS companyId")
				.addTable("company")
				.addJoin("INNER JOIN account_register ar ON ar.company_id = company.id ")
				.addJoin("INNER JOIN register_transaction rt ON rt.account_register_id = ar.id ")
				.addWhereClause("rt.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.EARNINGS_TRANSACTION_TYPE_CODES, "'"), ",") + ")")
				.addWhereClause("rt.pending_flag = 'N'")
				.addWhereClause("rt.effective_date >= :fromDate AND rt.effective_date < :toDate ")
				.addParam("fromDate", fromDate)
				.addParam("toDate", toDate)
				.addGroupColumns("company.id");

		if (AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES.equals(accountServiceType)) {
			builder.addWhereClause("EXISTS(SELECT id FROM tax_entity WHERE company_id = company.id AND active_flag = 'Y')")
					.addJoin("LEFT JOIN work_resource_transaction wrt ON wrt.id = rt.id ")
					.addColumn("rt.register_transaction_type_code")
					.addGroupColumns("rt.register_transaction_type_code")
					.addColumn("COALESCE(wrt.account_service_type_code, 'none') AS accountServiceType ")
					.addGroupColumns("wrt.account_service_type_code");

			//1099 is only for VOR
		} else if (AccountServiceType.VOR_SERVICE_TYPES.equals(accountServiceType)) {
			builder.addJoin("LEFT JOIN work_resource_transaction wrt ON wrt.id = rt.id AND wrt.account_service_type_code = :vor ")
					.addWhereClause("\n EXISTS(SELECT id FROM tax_entity WHERE country = :usa AND company_id = company.id AND tax_entity.status = :approved)")
					.addWhereClause("\n ((rt.register_transaction_type_code = :wrkpayment AND wrt.account_service_type_code = :vor ) " +
							"\n OR rt.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.TAX_FORM_1099_EARNINGS_NO_WORK_PAYMENTS_TRANSACTION_TYPE_CODES, "'"), ",") + "))")
					.addParam("usa", AbstractTaxEntity.COUNTRY_USA)
					.addParam("approved", TaxVerificationStatusType.APPROVED)
					.addParam("wrkpayment", RegisterTransactionType.RESOURCE_WORK_PAYMENT)
					.addParam("vor", AccountServiceType.VENDOR_OF_RECORD);

		} else if (AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE.equals(accountServiceType)) {
			builder.addJoin("INNER JOIN work_resource_transaction wrt ON wrt.id = rt.id AND wrt.account_service_type_code = :tax ")
					.addWhereClause("\n EXISTS(SELECT id FROM tax_entity WHERE country = :usa AND company_id = company.id AND tax_entity.status = :approved)")
					.addParam("usa", AbstractTaxEntity.COUNTRY_USA)
					.addParam("approved", TaxVerificationStatusType.APPROVED)
					.addParam("wrkpayment", RegisterTransactionType.RESOURCE_WORK_PAYMENT)
					.addParam("tax", AccountServiceType.TAX_SERVICE_1099);
		}

		return builder;
	}



		// Report for none vor account service type
	private SQLBuilder newTaxReportDetailSqlBuilder(Calendar fromDate, Calendar toDate, List<String> accountServiceType) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("COALESCE(SUM(rt.amount),0) AS amount", "company.id AS resource_company_id", "w.company_id AS buyer_company_id")
				.addTable("company")
				.addJoin("INNER JOIN account_register ar ON ar.company_id = company.id ")
				.addJoin("INNER JOIN register_transaction rt ON rt.account_register_id = ar.id ")
				.addJoin("INNER JOIN work_resource_transaction wrt ON wrt.id = rt.id ")
				.addJoin("INNER JOIN work w ON w.id = rt.work_id")
				.addWhereClause("rt.pending_flag = 'N'")
				.addWhereClause("rt.register_transaction_type_code = :wrkpayment ")
				.addWhereClause("rt.effective_date >= :fromDate AND rt.effective_date < :toDate ")
				.addWhereClause("wrt.account_service_type_code in (" +  StringUtils.join(StringUtilities.surround(accountServiceType, "'"), ",") + " )")
				.addParam("fromDate", fromDate)
				.addParam("toDate", toDate)
				.addParam("wrkpayment", RegisterTransactionType.RESOURCE_WORK_PAYMENT)
				.addGroupColumns("company.id", "w.company_id");
		return builder;
	}

}
