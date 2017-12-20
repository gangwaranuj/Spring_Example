package com.workmarket.dao.asset;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.asset.AttemptResponseAssetAssociation;

public interface AttemptResponseAssetAssociationDAO extends DAOInterface<AttemptResponseAssetAssociation> {
	List<AttemptResponseAssetAssociation> findByAttemptResponse(Long attemptResponseId);
	AttemptResponseAssetAssociation findLatestByAsset(Long assetId);
}