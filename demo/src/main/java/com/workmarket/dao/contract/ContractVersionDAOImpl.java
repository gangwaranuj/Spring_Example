package com.workmarket.dao.contract;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.domains.model.contract.ContractVersionPagination;

import javax.annotation.Resource;
import java.util.List;

import static com.workmarket.utility.CollectionUtilities.isEmpty;

@Repository
public class ContractVersionDAOImpl extends AbstractDAO<ContractVersion> implements ContractVersionDAO {

	protected Class<ContractVersion> getEntityClass() {
		return ContractVersion.class;
	}

	@Resource private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	@Override
	@SuppressWarnings(value = "unchecked")
	public ContractVersionPagination findAllContractVersionsByContractId(Long contractId, ContractVersionPagination pagination) {
		Assert.notNull(contractId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("id"));
		}

		criteria.add(Restrictions.eq("contract.id", contractId));
		count.add(Restrictions.eq("contract.id", contractId));

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@Override
	public ContractVersion findContractVersionByIdAndCompany(Long contractVersionId, Long companyId) {
		Assert.notNull(contractVersionId);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("contract", FetchMode.JOIN)
				.createAlias("contract.company", "company", Criteria.INNER_JOIN)
				.add(Restrictions.eq("id", contractVersionId))
				.add(Restrictions.eq("company.id", companyId));

		ContractVersion version = (ContractVersion) criteria.uniqueResult();
		if (version != null) {
			Hibernate.initialize(version.getContractVersionAssetAssociations());
			Hibernate.initialize(version.getContractVersionUserSignatures());
		}
		return version;
	}

	/*
	* This method can return null values when the creator of a contract does not exist
	*/
	@Override
	public ContractVersion findMostRecentContractVersionByContractId(Long contractId) {
		 ContractVersion contractVersion = (ContractVersion) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("contract.id", contractId))
			.setFetchMode("contract", FetchMode.JOIN)
			.addOrder(Order.desc("id"))
			.setMaxResults(1)
			.uniqueResult();

		if (contractVersion != null) {
			Hibernate.initialize(contractVersion.getContractVersionAssetAssociations());
			Hibernate.initialize(contractVersion.getContractVersionUserSignatures());
		}
		return contractVersion;
	}

	@Override
	public Optional<Long> findMostRecentContractVersionIdByContractId(final long contractId) {
		final String sql =
			"SELECT MAX(contract_version.id) \n" +
				"FROM contract_version \n" +
				"JOIN contract ON contract_version.contract_id = contract.id \n" +
				"WHERE contract.id = :contractId \n";

		final List<Long> results =
			readOnlyJdbcTemplate.queryForList(sql, ImmutableMap.of("contractId", contractId), Long.class);

		if (isEmpty(results) || results.get(0) == null) {
			return Optional.absent();
		}

		return Optional.of(results.get(0));
	}

}
