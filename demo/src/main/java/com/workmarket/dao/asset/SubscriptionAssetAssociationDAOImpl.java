package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.asset.SubscriptionAssetAssociation;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionAssetAssociationDAOImpl extends DeletableAbstractDAO<SubscriptionAssetAssociation> implements SubscriptionAssetAssociationDAO {

	protected Class<SubscriptionAssetAssociation> getEntityClass() {
		return SubscriptionAssetAssociation.class;
	}

}