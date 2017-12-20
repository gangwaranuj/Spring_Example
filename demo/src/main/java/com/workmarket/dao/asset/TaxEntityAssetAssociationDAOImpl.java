package com.workmarket.dao.asset;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.asset.TaxEntityAssetAssociation;

@Repository
public class TaxEntityAssetAssociationDAOImpl extends DeletableAbstractDAO<TaxEntityAssetAssociation> implements TaxEntityAssetAssociationDAO {
	protected Class<TaxEntityAssetAssociation> getEntityClass() {
		return TaxEntityAssetAssociation.class;
	}
}
