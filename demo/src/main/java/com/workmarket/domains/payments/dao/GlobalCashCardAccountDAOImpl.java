package com.workmarket.domains.payments.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccountPagination;
import com.workmarket.domains.model.banking.GlobalCashCardAccount;
import com.workmarket.utility.HibernateUtilities;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class GlobalCashCardAccountDAOImpl extends AbstractDAO<AbstractBankAccount> implements GlobalCashCardAccountDAO {

	@Override
	protected Class<?> getEntityClass() {
		return AbstractBankAccount.class;
	}

	@Override
	public String findAccountNumber(Long id) {
		return (String) getFactory().getCurrentSession().getNamedQuery("gcc.accountNumber")
				.setLong("id", id)
				.uniqueResult();
	}

	@Override
	public BankAccountPagination find(BankAccountPagination pagination) {
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

		HibernateUtilities.addRestrictionsEq(criteria, "class", GlobalCashCardAccount.GCC);
		HibernateUtilities.addRestrictionsEq(count, "class", GlobalCashCardAccount.GCC);


		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(HibernateUtilities.getRowCount(count));
		pagination.setResults(criteria.list());

		return pagination;


	}


}
