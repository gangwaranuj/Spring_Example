package com.workmarket.dao.contract;


import com.mchange.v1.lang.BooleanUtils;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.contract.Contract;
import com.workmarket.domains.model.contract.ContractPagination;
import com.workmarket.utility.HibernateUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
@Qualifier("contractDAO")
public class ContractDAOImpl extends AbstractDAO<Contract> implements ContractDAO {

	protected Class<Contract> getEntityClass() {
		return Contract.class;
	}


	@Override
	public Contract findContractById(Long id) {
		return (Contract) getFactory().getCurrentSession().get(Contract.class, id);
	}


	@Override
	@SuppressWarnings(value = "unchecked")
	public ContractPagination findAllContractsByCompanyId(Long companyId, ContractPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		String sortColumn = "name";
		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(ContractPagination.SORTS.MODIFICATION_DATE)) {
				sortColumn = "modifiedOn";
			} else if (pagination.getSortColumn().equals(ContractPagination.SORTS.NAME)) {
				sortColumn = "name";
			} else if (pagination.getSortColumn().equals(ContractPagination.SORTS.CREATOR)) {
				sortColumn = "firstName";
			}
			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				criteria.addOrder(Order.desc(sortColumn));
			} else {
				criteria.addOrder(Order.asc(sortColumn));
			}
		} else
			criteria.addOrder(Order.desc(sortColumn));

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().containsKey(ContractPagination.FILTER_KEYS.ACTIVE.toString())) {
				boolean active = BooleanUtils.parseBoolean(pagination.getFilters().get(ContractPagination.FILTER_KEYS.ACTIVE.toString()));
				criteria.add(Restrictions.eq("active", active));
				count.add(Restrictions.eq("active", active));
			}
		}

		Object[] params = {"company.id", companyId};

		HibernateUtilities.addRestrictionsEq(criteria, params);
		HibernateUtilities.addRestrictionsEq(count, params);

		long rowCount = HibernateUtilities.getRowCount(count);

		pagination.setResults(criteria.list());
		pagination.setRowCount(rowCount);

		return pagination;
	}


	@Override
	public Contract findContractByIdAndCompany(Long contractId, Long companyId) {
		return (Contract) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("active", true))
				.add(Restrictions.eq("id", contractId))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public Contract findActiveContractByNameAndCompanyId(String name, Long companyId) {
		Assert.hasText(name);
		return (Contract) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("active", true))
				.add(Restrictions.ilike("name", name, MatchMode.EXACT))
				.setMaxResults(1)
				.uniqueResult();
	}

}
