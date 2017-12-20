package com.workmarket.domains.payments.dao;


import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountPagination;
import com.workmarket.utility.HibernateUtilities;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Repository
public class BankAccountDAOImpl extends AbstractDAO<AbstractBankAccount> implements BankAccountDAO  {

	protected Class<AbstractBankAccount> getEntityClass() {
		return AbstractBankAccount.class;
	}

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Override
	public BankAccountPagination find(Long companyId, BankAccountPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		if (pagination.hasFilter(BankAccountPagination.FILTER_KEYS.ACTIVE)) {
			boolean value = BooleanUtils.toBoolean(pagination.getFilter(BankAccountPagination.FILTER_KEYS.ACTIVE));
			HibernateUtilities.addRestrictionsEq(criteria, "activeFlag", value);
			HibernateUtilities.addRestrictionsEq(count, "activeFlag", value);
		}

		if (pagination.hasFilter(BankAccountPagination.FILTER_KEYS.CONFIRMED)) {
			boolean value = BooleanUtils.toBoolean(pagination.getFilter(BankAccountPagination.FILTER_KEYS.CONFIRMED));
			HibernateUtilities.addRestrictionsEq(criteria, "confirmedFlag", value);
			HibernateUtilities.addRestrictionsEq(count, "confirmedFlag", value);
		}

		if (pagination.hasFilter(BankAccountPagination.FILTER_KEYS.COUNTRY)) {
			String value = pagination.getFilter(BankAccountPagination.FILTER_KEYS.COUNTRY);
			HibernateUtilities.addRestrictionsEq(criteria, "country.id", value);
			HibernateUtilities.addRestrictionsEq(count, "country.id", value);
		}

		if (pagination.hasFilter(BankAccountPagination.FILTER_KEYS.TYPE)) {
			String value = pagination.getFilter(BankAccountPagination.FILTER_KEYS.TYPE);
			HibernateUtilities.addRestrictionsEq(criteria, "class", value);
			HibernateUtilities.addRestrictionsEq(count, "class", value);
		}

		List<String> sorts = Collections.emptyList();
		String sortColumn = MoreObjects.firstNonNull(pagination.getSortColumn(), BankAccountPagination.SORTS.TYPE.name());
		if (sortColumn.equals(BankAccountPagination.SORTS.TYPE.name())) {
			sorts = ImmutableList.of("class");
		} else if (sortColumn.equals(BankAccountPagination.SORTS.ACCOUNT_NAME.name())) {
			sorts = ImmutableList.of("bankName", "accountNumber", "emailAddress");
		}

		boolean isAscending = Pagination.SORT_DIRECTION.ASC.equals(pagination.getSortDirection());
		HibernateUtilities.addSorts(criteria, sorts, isAscending);
		HibernateUtilities.addJoins(criteria, Criteria.INNER_JOIN, "bankAccountType");
		HibernateUtilities.addRestrictionsEq(criteria, "company.id", companyId);
		HibernateUtilities.addRestrictionsEq(count, "company.id", companyId);
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(HibernateUtilities.getRowCount(count));
		pagination.setResults(criteria.list());

		return pagination;
	}

	@Override
	public int countGccBankAccounts(Calendar date) {
		Assert.notNull(date);
		String sql = "SELECT count(id) data FROM bank_account WHERE type = 'GCC' and created_on >= :date";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("date", date);
		return jdbcTemplate.queryForObject(sql, params, Integer.class);
	}

	@Override
	public List<BankAccount> getAllBankAccountsFrom(final Calendar fromCreatedOnDate) {
		Criteria criteria = getFactory()
			.getCurrentSession()
			.createCriteria(BankAccount.class)
			.add(Restrictions.ge("createdOn", fromCreatedOnDate));
		return criteria.list();
	}

	@Override
	public List<BankAccount> getAllBankAccountsFromModifiedDate(final Calendar fromModifiedOnDate) {
		Criteria criteria = getFactory()
				.getCurrentSession()
				.createCriteria(BankAccount.class)
				.add(Restrictions.ge("modifiedOn", fromModifiedOnDate));
		return criteria.list();
	}
}
