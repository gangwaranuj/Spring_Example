package com.workmarket.domains.work.service;

import com.google.api.client.util.Lists;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.asset.WorkAssetAssociationDAO;
import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.DeliverableRequirementGroup;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.work.dao.DeliverableRequirementDAO;
import com.workmarket.domains.work.dao.DeliverableRequirementGroupDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.AssetService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

@Service
public class DeliverableServiceImpl implements DeliverableService {

	@Autowired private AssetService assetService;
	@Autowired private DeliverableRequirementDAO deliverableRequirementDAO;
	@Autowired private DeliverableRequirementGroupDAO deliverableRequirementGroupDAO;
	@Autowired private WorkAssetAssociationDAO workAssetAssociationDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private WorkService workService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private NotificationService notificationService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private WorkResourceService workResourceService;

	@Override
	public WorkAssetAssociation addDeliverable(String workNumber, InputStream inputStream, AssetDTO assetDTO) throws Exception {
		Assert.hasText(workNumber);
		Assert.notNull(inputStream);
		Assert.notNull(assetDTO);

		assetDTO.setSourceFilePath(copyStreamToTempFile(inputStream).getAbsolutePath());

		return addDeliverable(workNumber, assetDTO);
	}

	public File copyStreamToTempFile(InputStream inputStream) throws IOException {
		File tmpFile = File.createTempFile("deliverable", null);
		FileUtils.copyInputStreamToFile(inputStream, tmpFile);
		return tmpFile;
	}

	@Override
	public WorkAssetAssociation addDeliverable(String workNumber, AssetDTO assetDTO) throws Exception {
		Assert.hasText(workNumber);
		Assert.notNull(assetDTO);

		assetDTO.setDeliverable(true);
		setDeliverableAssetType(assetDTO);

		Work work = workService.findWorkByWorkNumber(workNumber);
		WorkAssetAssociation workAssetAssociation = assetManagementService.storeAssetForWork(assetDTO, work.getId(), true);
		Asset asset = workAssetAssociation.getAsset();
		userNotificationService.onWorkAttachmentAdded(work, asset);

		if (work.getDeliverableRequirementGroup() != null && isDeliverableRequirementGroupComplete(work.getDeliverableRequirementGroup().getId())) {
			workSubStatusService.resolveSystemSubStatusByAction(work.getId(), WorkSubStatusType.DELIVERABLE_REJECTED);
			userNotificationService.onDeliverableRequirementComplete(work);

			// Deactivate deliverable deadline if all deliverables have been completed
			disableDeliverableDeadline(work.getDeliverableRequirementGroup());

		}

		return workAssetAssociation;
	}

	private void setDeliverableAssetType(AssetDTO assetDTO) {
		String assetType = WorkAssetAssociationType.CLOSING_ASSET;
		if (assetDTO.getDeliverableRequirementId() != null) {
			DeliverableRequirement deliverableRequirement = findDeliverableRequirementById(assetDTO.getDeliverableRequirementId());
			if (deliverableRequirement != null) {
				assetType = deliverableRequirement.getType().getCode();
			}
		}
		assetDTO.setAssociationType(assetType);
	}

	@Override
	public DeliverableRequirement findDeliverableRequirementById(Long deliverableRequirementId) {
		Assert.notNull(deliverableRequirementId);

		return deliverableRequirementDAO.get(deliverableRequirementId);
	}

	@Override
	public List<WorkAssetAssociation> findAllAssetAssociationsByDeliverableRequirementIdAndPosition(Long workId, Long deliverableRequirementId, Integer position) {
		Assert.notNull(workId);
		Assert.notNull(deliverableRequirementId);
		Assert.notNull(position);

		return workAssetAssociationDAO.findAllAssetAssociationsByDeliverableRequirementIdAndPosition(workId, deliverableRequirementId, position);
	}

	@Override
	public boolean isDeliverableRequirementOfType(Long deliverableRequirementId, WorkAssetAssociationType type) {
		Assert.notNull(deliverableRequirementId);
		Assert.notNull(type);

		DeliverableRequirement deliverableRequirement = deliverableRequirementDAO.get(deliverableRequirementId);
		return deliverableRequirement != null && type.equals(deliverableRequirementDAO.get(deliverableRequirementId).getType());
	}

	@Override
	public DeliverableRequirementGroup saveOrUpdateDeliverableRequirementGroup(DeliverableRequirementGroupDTO deliverableRequirementGroupDTO) {
		Assert.notNull(deliverableRequirementGroupDTO);

		Long deliverableRequirementGroupId = deliverableRequirementGroupDTO.getId();
		DeliverableRequirementGroup deliverableRequirementGroup;
		if (deliverableRequirementGroupId == null) {
			deliverableRequirementGroup = new DeliverableRequirementGroup();
		} else {
			deliverableRequirementGroup = deliverableRequirementGroupDAO.get(deliverableRequirementGroupId);
		}

		BeanUtilities.copyProperties(deliverableRequirementGroup, deliverableRequirementGroupDTO);
		deliverableRequirementGroupDAO.saveOrUpdate(deliverableRequirementGroup);

		List<DeliverableRequirementDTO> deliverableRequirements = deliverableRequirementGroupDTO.getDeliverableRequirementDTOs();
		saveOrUpdateDeliverableRequirements(deliverableRequirements, deliverableRequirementGroup);

		return deliverableRequirementGroup;
	}

	@Override
	public void saveOrUpdateDeliverableRequirements(List<DeliverableRequirementDTO> deliverableRequirements, DeliverableRequirementGroup deliverableRequirementGroup) {
		Assert.notNull(deliverableRequirements);
		Assert.notNull(deliverableRequirementGroup);

		List<DeliverableRequirement> savedDeliverableRequirements = Lists.newArrayList();
		for (DeliverableRequirementDTO deliverableRequirementDTO : deliverableRequirements) {
			Long deliverableRequirementId = deliverableRequirementDTO.getId();

			DeliverableRequirement deliverableRequirement;

			if (deliverableRequirementId != null && !deliverableRequirementId.equals(0L)) {
				deliverableRequirement = deliverableRequirementDAO.get(deliverableRequirementId);
			} else {
				// Reuse a deleted deliverable requirement, if possible
				deliverableRequirement = deliverableRequirementDAO.findDeletedDeliverableRequirementByGroupIdAndType(deliverableRequirementGroup.getId(), deliverableRequirementDTO.getType());
			}

			if (deliverableRequirement == null) {
				deliverableRequirement = new DeliverableRequirement();
			}

			BeanUtilities.copyProperties(deliverableRequirement, deliverableRequirementDTO, new String[]{"id", "type"});
			WorkAssetAssociationType type = new WorkAssetAssociationType(deliverableRequirementDTO.getType());

			// Clean instructions before saving to DB
			String cleanedInstructions = StringUtilities.stripXSSAndEscapeHtml(deliverableRequirementDTO.getInstructions());
			deliverableRequirement.setInstructions(cleanedInstructions);

			deliverableRequirement.setType(type);
			//Set deliverableRequirement --> deliverableRequirementGroup association
			deliverableRequirement.setDeliverableRequirementGroup(deliverableRequirementGroup);
			deliverableRequirement.setDeleted(false);
			deliverableRequirementDAO.saveOrUpdate(deliverableRequirement);
			savedDeliverableRequirements.add(deliverableRequirement);
		}

		// Update other side of relationship.
		// We assign the deliverableRequirements to the deliverableRequirementGroup to ensure that we preserve
		// the bidirectional relationship between DeliverableRequirements and DeliverableRequirementGroup
		deliverableRequirementGroup.setDeliverableRequirements(savedDeliverableRequirements);

		List<DeliverableRequirement> deliverableRequirementsToDelete = deliverableRequirementDAO.findAllDeliverableRequirementsByGroupId(deliverableRequirementGroup.getId());
		deliverableRequirementsToDelete.removeAll(savedDeliverableRequirements);
		for (DeliverableRequirement deliverableRequirementToDelete : deliverableRequirementsToDelete) {
			deliverableRequirementToDelete.setDeleted(true);
		}
	}

	@Override
	public void disableDeliverableDeadline(String workNumber) {
		Assert.hasText(workNumber);

		DeliverableRequirementGroup deliverableRequirementGroup = deliverableRequirementGroupDAO.findDeliverableGroupByWorkNumber(workNumber);

		disableDeliverableDeadline(deliverableRequirementGroup);
	}

	@Override
	public void disableDeliverableDeadline(DeliverableRequirementGroup deliverableRequirementGroup) {

		if (deliverableRequirementGroup != null) {
			deliverableRequirementGroup.setDeadlineActive(false);
			deliverableRequirementGroupDAO.saveOrUpdate(deliverableRequirementGroup);
		}
	}

	@Override
	public void disableDeliverableReminder(String workNumber) {
		Assert.hasText(workNumber);

		DeliverableRequirementGroup deliverableRequirementGroup = deliverableRequirementGroupDAO.findDeliverableGroupByWorkNumber(workNumber);

		if (deliverableRequirementGroup != null) {
			deliverableRequirementGroup.setReminderSent(true);
			deliverableRequirementGroupDAO.saveOrUpdate(deliverableRequirementGroup);
		}
	}

	@Override
	public void reactivateDeliverableDeadlineAndReminder(Long deliverableRequirementGroupId) {
		Assert.notNull(deliverableRequirementGroupId);

		DeliverableRequirementGroup deliverableRequirementGroup = deliverableRequirementGroupDAO.get(deliverableRequirementGroupId);

		if (deliverableRequirementGroup != null) {
			deliverableRequirementGroup.setDeadlineActive(true);
			deliverableRequirementGroup.setReminderSent(false);
			deliverableRequirementGroupDAO.saveOrUpdate(deliverableRequirementGroup);
		}
	}

	@Override
	public int countDeliverableAssetsByDeliverableRequirementId(Long deliverableRequirementId) {
		Assert.notNull(deliverableRequirementId);
		return assetService.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementId);
	}

	@Override
	public boolean isDeliverableRequirementGroupComplete(Long deliverableRequirementGroupId) {
		Assert.notNull(deliverableRequirementGroupId);
		DeliverableRequirementGroup deliverableRequirementGroup = deliverableRequirementGroupDAO.get(deliverableRequirementGroupId);
		List<DeliverableRequirement> deliverableRequirements = deliverableRequirementGroup.getDeliverableRequirements();
		if (CollectionUtilities.isEmpty(deliverableRequirements)) {
			return false;
		}
		for (DeliverableRequirement deliverableRequirement : deliverableRequirements) {
			if (!isDeliverableRequirementComplete(deliverableRequirement)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isDeliverableRequirementComplete(DeliverableRequirement deliverableRequirement) {
		Assert.notNull(deliverableRequirement);
		return countDeliverableAssetsByDeliverableRequirementId(deliverableRequirement.getId()) >= deliverableRequirement.getNumberOfFiles();
	}

	@Override
	public void reactivateDeliverableDeadlineAndReminder(DeliverableRequirementGroup deliverableRequirementGroup) {
		Assert.notNull(deliverableRequirementGroup);

		deliverableRequirementGroup.setDeadlineActive(true);
		deliverableRequirementGroup.setReminderSent(false);
		deliverableRequirementGroupDAO.saveOrUpdate(deliverableRequirementGroup);
	}

	@Override
	public void setPositionOfDeliverableAssets(Long deliverableRequirementId) {
		Assert.notNull(deliverableRequirementId);

		List<WorkAssetAssociation> workAssetAssociations =
			workAssetAssociationDAO.findAllAssetAssociationsByDeliverableRequirementId(deliverableRequirementId);

		if (CollectionUtils.isNotEmpty(workAssetAssociations)) {
			int position = 0;
			for (WorkAssetAssociation workAssetAssociation : workAssetAssociations) {
				workAssetAssociation.setPosition(position);
				position++;
			}
		}
	}

	@Override
	public void removeDeliverablesAtPositionFromWork(Long workId, Long deliverableRequirementId, Integer position) {
		List<WorkAssetAssociation> workAssetAssociations = findAllAssetAssociationsByDeliverableRequirementIdAndPosition(workId, deliverableRequirementId, position);

		for (WorkAssetAssociation workAssetAssociation : workAssetAssociations) {
			workAssetAssociation.setDeleted(true);
		}

		// Check deliverables and activate/deactivate deliverables deadline accordingly
		AbstractWork work = workService.findWork(workId);
		setDeliverableDeadlineStatus(work.getDeliverableRequirementGroup());
	}

	@Override
	public Calendar rejectDeliverable(String rejectionReason, Long userId, Long workId, Long assetId) {
		Assert.isTrue(StringUtils.isNotBlank(rejectionReason));
		Assert.notNull(userId);
		Assert.notNull(workId);
		Assert.notNull(assetId);

		User currentUser = userDAO.getUser(userId);

		WorkAssetAssociation workAssetAssociation = assetManagementService.findAssetAssociationsByWorkAndAsset(workId, assetId);
		Calendar now = DateUtilities.getCalendarNow();
		workAssetAssociation.setRejectedBy(currentUser);
		workAssetAssociation.setRejectedOn(now);
		workAssetAssociation.setRejectionReason(rejectionReason);

		WorkResource workResource = workService.findActiveWorkResource(workId);
		Work work = workResource.getWork();

		workSubStatusService.addSystemSubStatus(currentUser, work.getId(), WorkSubStatusType.DELIVERABLE_REJECTED);

		notificationService.sendNotification(
			notificationTemplateFactory.buildWorkDeliverableRejectedNotificationTemplate(
				workResource.getUser().getId(), work, workAssetAssociation.getAsset().getName(), rejectionReason
			)
		);

		Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), workResource.getUser().getId());
		if (dispatcherId != null) {
			NotificationTemplate forDispatcher = notificationTemplateFactory.buildWorkDeliverableRejectedNotificationTemplate(
				dispatcherId, work, workAssetAssociation.getAsset().getName(), rejectionReason
			);
			forDispatcher.setOnBehalfOfId(workResource.getUser().getId());
			notificationService.sendNotification(forDispatcher);
		}

		// Check deliverables and activate/deactivate deliverables deadline accordingly
		setDeliverableDeadlineStatus(work.getDeliverableRequirementGroup());

		return now;
	}

	/**
	 * Update deadlineActive for given DeliverableRequirementGroup.
	 * We disable the deadline if all deliverables are complete.
	 * Otherwise we enable it
	 */
	private void setDeliverableDeadlineStatus(DeliverableRequirementGroup deliverableRequirementGroup) {

		// Deactivate deadline if all deliverables have been completed. Otherwise activate it.
		if (deliverableRequirementGroup != null) {
			if (isDeliverableRequirementGroupComplete(deliverableRequirementGroup.getId())) {
				disableDeliverableDeadline(deliverableRequirementGroup);
			} else {
				reactivateDeliverableDeadlineAndReminder(deliverableRequirementGroup);
			}
		}
	}

	@Override
	public void removeAllDeliverablesFromWork(Long workId) {
		Assert.notNull(workId);

		List<WorkAssetAssociation> workAssetAssociations = workAssetAssociationDAO.findAllDeliverablesByWork(workId);

		if (CollectionUtils.isEmpty(workAssetAssociations)) {
			return;
		}

		for (WorkAssetAssociation workAssetAssociation : workAssetAssociations) {
			workAssetAssociation.setDeleted(true);
		}


		// Check deliverables and activate/deactivate deliverables deadline accordingly
		Work work = workService.findWork(workId);
		if (work.getDeliverableRequirementGroup() != null) {
			setDeliverableDeadlineStatus(work.getDeliverableRequirementGroup());
		}
	}

}
