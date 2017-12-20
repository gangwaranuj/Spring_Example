package com.workmarket.dao.asset;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.asset.AssetVersionAssetAssociation;

@Repository
public class AssetVersionAssetAssociationDAOImpl extends AbstractDAO<AssetVersionAssetAssociation> implements AssetVersionAssetAssociationDAO {
	
	protected Class<AssetVersionAssetAssociation> getEntityClass() {
		return AssetVersionAssetAssociation.class;
	}

}
