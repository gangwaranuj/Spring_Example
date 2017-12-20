package com.workmarket.domains.payments.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

@Repository
public class PaymentConfigurationDAOImpl extends AbstractDAO<PaymentConfiguration> implements PaymentConfigurationDAO {

	@Autowired @Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<PaymentConfiguration> getEntityClass() {
		return PaymentConfiguration.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Company> findAllCompanyAccountsByNextStatementDate(Calendar asOfDate) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(Company.class)
				.createAlias("paymentConfiguration", "paymentConfiguration", CriteriaSpecification.INNER_JOIN)
				.add(Restrictions.eq("manageMyWorkMarket.statementsEnabled", true))
				.add(Restrictions.le("paymentConfiguration.nextStatementDate", asOfDate));
		return criteria.list();
	}

	@Override
	public List<Company> findAllCompaniesByAccountPricingType(AccountPricingType accountPricingType) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(Company.class)
				.createAlias("paymentConfiguration", "paymentConfiguration", CriteriaSpecification.INNER_JOIN)
				.add(Restrictions.eq("paymentConfiguration.accountPricingType.code", accountPricingType.getCode()));
		return criteria.list();
	}

}
