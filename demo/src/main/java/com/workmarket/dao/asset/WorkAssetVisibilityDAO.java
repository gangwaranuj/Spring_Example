package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.WorkAssetVisibility;

/**
 * Created by alejandrosilva on 1/19/15.
 */
public interface WorkAssetVisibilityDAO extends DeletableDAOInterface<WorkAssetVisibility> {

	WorkAssetVisibility findByWorkAssetAssociationId(Long workAssetAssociationId);

}
