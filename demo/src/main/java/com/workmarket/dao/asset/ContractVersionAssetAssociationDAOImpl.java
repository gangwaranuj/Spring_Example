package com.workmarket.dao.asset;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.ContractVersionAssetAssociation;

@Repository
public class ContractVersionAssetAssociationDAOImpl extends AbstractDAO<ContractVersionAssetAssociation> implements ContractVersionAssetAssociationDAO {

	protected Class<CompanyAssetAssociation> getEntityClass() {
        return CompanyAssetAssociation.class;
    }
	
}
