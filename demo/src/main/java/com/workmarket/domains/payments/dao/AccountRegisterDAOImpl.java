package com.workmarket.domains.payments.dao;

import com.newrelic.api.agent.NewRelic;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Repository
@SuppressWarnings("unchecked")
public class AccountRegisterDAOImpl extends AbstractDAO<AccountRegister> implements AccountRegisterDAO {
	private static final Logger logger = LoggerFactory.getLogger(AccountRegisterDAOImpl.class);

	@Autowired @Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<AccountRegister> getEntityClass() {
		return AccountRegister.class;
	}

	private static final String
		CURRENT_WORK_FEE_PERCENTAGE = "current_work_fee_percentage",
		PAYMENT_SUMMATION = "payment_summation",
		ACCOUNTS_PAYABLE_BALANCE = "accounts_payable_balance",
		AP_LIMIT = "ap_limit";

	@Override
	public AccountRegister findByCompanyId(Long companyId) {
		return findByCompanyId(companyId, false);
	}

	@Override
	public AccountRegister findByCompanyId(Long companyId, boolean lockForWriting) {
		logger.warn("LOCK - accountregister.byCompanyId - company[" + companyId + "] for writing");
		long start = System.currentTimeMillis();
		if(lockForWriting) {
			NewRelic.addCustomParameter("accountRegisterLock", companyId);
		}
		Query query = getFactory().getCurrentSession().getNamedQuery("accountregister.byCompanyId")
			.setLong("companyId", companyId);

		if (lockForWriting) {
			query.setLockOptions(LockOptions.UPGRADE);
		}

		List<AccountRegister> results = query.list();

		long end = System.currentTimeMillis();
		logger.warn("LOCK Acquired[" + (end - start) + "] - accountregister.byCompanyId - company[" + companyId + "] for writing");

		if (isNotEmpty(results)) {
			return results.get(0);
		}
		return null;
	}

	@Override
	public AccountRegister findByCompanyNumber(String companyNumber) {
		if (StringUtils.isEmpty(companyNumber)) {
			return null;
		}
		return (AccountRegister)getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("company", "company")
				.add(Restrictions.eq("company.companyNumber", companyNumber)).setMaxResults(1).uniqueResult();
	}

	@Override
	public List<Long> findAllIds() {
		return getFactory().getCurrentSession().getNamedQuery("accountregister.select_all_ids").list();
	}

	@Override
	public AccountRegister findById(Long accountRegisterId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("company", FetchMode.JOIN)
				.add(Restrictions.eq("id", accountRegisterId));
		return (AccountRegister) criteria.uniqueResult();
	}

	@Override
	public BigDecimal calcRemainingAPBalance(Long companyId) {
		SQLBuilder sqlBuilder = new SQLBuilder()
				.addTable("account_register")
				.addColumn("ap_limit")
				.addColumn("accounts_payable_balance")
				.addWhereClause("company_id", SQLOperator.EQUALS, "companyId", companyId)
				.addParam("companyId", companyId);

		List<Map<String, Object>> queryResult = jdbcTemplate.query(sqlBuilder.build(), sqlBuilder.getParams(), new ColumnMapRowMapper());

		if (isEmpty(queryResult)) { return BigDecimal.ZERO; }

		Map<String, Object> resultMap = queryResult.get(0);
		BigDecimal apLimit = (BigDecimal) resultMap.get("ap_limit");
		BigDecimal accounts_payable_balance = (BigDecimal) resultMap.get("accounts_payable_balance");
		return apLimit.subtract(accounts_payable_balance);
	}

	@Override
	public BigDecimal getPaymentSummation(Long companyId) {
		return getAccountRegisterSummaryField(PAYMENT_SUMMATION, companyId);
	}

	@Override
	public BigDecimal getCurrentWorkFeePercentage(Long companyId) {
		return getAccountRegisterSummaryField(CURRENT_WORK_FEE_PERCENTAGE, companyId);
	}

	@Override
	public BigDecimal getAccountsPayableBalance(Long companyId) {
		return getAccountRegisterSummaryField(ACCOUNTS_PAYABLE_BALANCE, companyId);
	}

	@Override
	public BigDecimal getAPLimit(Long companyId) {
		return getAccountRegisterSummaryField(AP_LIMIT, companyId);
	}

	private BigDecimal getAccountRegisterSummaryField(final String field, final Long companyId) {
		SQLBuilder sqlBuilder = new SQLBuilder()
			.addTable("account_register")
			.addColumn(field)
			.addWhereClause("company_id", SQLOperator.EQUALS, "companyId", companyId)
			.addParam("companyId", companyId);

		List<BigDecimal> paymentSummation = jdbcTemplate.queryForList(sqlBuilder.build(), sqlBuilder.getParams(), BigDecimal.class);
		if (isEmpty(paymentSummation)) {
			return BigDecimal.ZERO;
		}
		return paymentSummation.get(0);
	}
}
