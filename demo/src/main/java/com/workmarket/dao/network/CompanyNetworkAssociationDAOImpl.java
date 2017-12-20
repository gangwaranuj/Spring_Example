package com.workmarket.dao.network;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.acl.AclNetworkRoleAssociation;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.network.CompanyNetworkAssociation;
import com.workmarket.domains.model.network.Network;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyNetworkAssociationDAOImpl extends AbstractDAO<CompanyNetworkAssociation> implements CompanyNetworkAssociationDAO {

	protected Class<CompanyNetworkAssociation> getEntityClass() {
		return CompanyNetworkAssociation.class;
	}

	@Override
	public CompanyNetworkAssociation findAssociationByNetworkAndCompany(Long networkId, Long companyId)  {
		Criteria criteria = getFactory().getCurrentSession()
				.createCriteria(CompanyNetworkAssociation.class)
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("network.id", networkId))
				.setMaxResults(1);

		return (CompanyNetworkAssociation) criteria.uniqueResult();
	}

	@Override
	public void addCompanyToNetworkWithRole(Company company, Network network, long aclNetworkRoleId) {
		CompanyNetworkAssociation companyNetworkAssociation = findAssociationByNetworkAndCompany(network.getId(), company.getId());
		AclRole companyNetworkRole = (AclRole) getFactory().getCurrentSession().get(AclRole.class, aclNetworkRoleId);

		if (companyNetworkAssociation == null) {
			companyNetworkAssociation = new CompanyNetworkAssociation();
			companyNetworkAssociation.setNetwork(network);
			companyNetworkAssociation.setCompany(company);
		}

		companyNetworkAssociation.setActive(true);
		companyNetworkAssociation.setDeleted(Boolean.FALSE);

		saveOrUpdate(companyNetworkAssociation);

		AclNetworkRoleAssociation networkRoleAssociation = new AclNetworkRoleAssociation();
		networkRoleAssociation.setActive(true);
		networkRoleAssociation.setRole(companyNetworkRole);
		networkRoleAssociation.setCompanyNetworkAssociation(companyNetworkAssociation);

		getFactory().getCurrentSession().saveOrUpdate(networkRoleAssociation);
	}

}
