package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;

import java.util.List;

public interface WorkAssetAssociationDAO extends DeletableDAOInterface<WorkAssetAssociation> {
	List<WorkAssetAssociation> findByWork(Long workId);

	WorkAssetAssociation findWorkAssetAssociation(Long workId, Long assetId);

	List<WorkAssetAssociation> findWorkAssetAssociationsByWork(List<Long> workIds, Long assetId);

	List<WorkAssetAssociation> findAllAssetAssociationsByWork(List<Long> workIds);

	List<WorkAssetAssociation> findAllAssetAssociationsByDeliverableRequirementId(Long deliverableRequirementId);

	List<WorkAssetAssociation> findAllAssetAssociationsByDeliverableRequirementIdAndPosition(Long workId, Long deliverableRequirementId, Integer position);

	List<WorkAssetAssociation> findAllDeliverablesByWork(Long workId);
}