package com.workmarket.domains.work.service;

import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.DeliverableRequirementGroup;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

/**
 * Created by rahul on 3/12/14
 */
public interface DeliverableService {

	WorkAssetAssociation addDeliverable(String workNumber, InputStream inputStream, AssetDTO assetDTO) throws Exception;

	WorkAssetAssociation addDeliverable(String workNumber, AssetDTO assetDTO) throws Exception;

	List<WorkAssetAssociation> findAllAssetAssociationsByDeliverableRequirementIdAndPosition(Long workId, Long deliverableRequirementId, Integer position);

	DeliverableRequirement findDeliverableRequirementById(Long deliverableRequirementId);

	DeliverableRequirementGroup saveOrUpdateDeliverableRequirementGroup(DeliverableRequirementGroupDTO deliverableRequirementGroupDTO);

	void setPositionOfDeliverableAssets(Long deliverableRequirementId);

	Calendar rejectDeliverable(String rejectionReason, Long userId, Long workId, Long assetId);

	void removeAllDeliverablesFromWork(Long workId);

	void removeDeliverablesAtPositionFromWork(Long workId, Long deliverableRequirementId, Integer position);

	boolean isDeliverableRequirementOfType(Long deliverableRequirementId, WorkAssetAssociationType type);

	boolean isDeliverableRequirementGroupComplete(Long deliverableRequirementGroupId);

	boolean isDeliverableRequirementComplete(DeliverableRequirement deliverableRequirement);

	void saveOrUpdateDeliverableRequirements(List<DeliverableRequirementDTO> deliverableRequirementDTO, DeliverableRequirementGroup deliverableRequirementGroup);

	void disableDeliverableDeadline(String workNumber);

	void disableDeliverableDeadline(DeliverableRequirementGroup deliverableRequirementGroup);

	void disableDeliverableReminder(String workNumber);

	void reactivateDeliverableDeadlineAndReminder(Long deliverableRequirementGroupId);

	void reactivateDeliverableDeadlineAndReminder(DeliverableRequirementGroup deliverableRequirementGroup);

	/**
	 *
	 * @param deliverableRequirementGroupId
	 * @return - The count of non-deleted, non-rejected assets in the deliverable requirement
	 */
	int countDeliverableAssetsByDeliverableRequirementId(Long deliverableRequirementGroupId);

}
