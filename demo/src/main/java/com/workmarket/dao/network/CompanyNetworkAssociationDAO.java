package com.workmarket.dao.network;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.network.CompanyNetworkAssociation;
import com.workmarket.domains.model.network.Network;

/**
 * User: alexsilva Date: 8/4/14 Time: 5:05 PM
 */
public interface CompanyNetworkAssociationDAO extends DAOInterface<CompanyNetworkAssociation> {

	CompanyNetworkAssociation findAssociationByNetworkAndCompany(Long networkId, Long companyId);

	void addCompanyToNetworkWithRole(Company company, Network network, long aclNetworkRoleId);

}
