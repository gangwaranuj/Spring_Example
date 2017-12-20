package com.workmarket.service.business.asset;

import com.workmarket.common.template.email.AttachmentUploadFailedNotificationTemplate;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.dao.asset.WorkAssetAssociationDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.changelog.work.WorkCloseoutAttachmentUploadedChangeLog;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

/**
 * Author: rocio
 */
@Service
public class AssetUploaderServiceImpl implements AssetUploaderService {

	@Autowired private NotificationService notificationService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private WorkService workService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private FeatureEvaluator featureEvaluator;

	@Autowired private WorkAssetAssociationDAO workAssetAssociationDAO;
	@Autowired private BaseWorkDAO abstractWorkDAO;

	@Override
	public void uploadAsset(AssetDTO dto, Asset asset, Long workId, User currentUser){
		Asset savedAsset;
		AbstractWork work = abstractWorkDAO.get(workId);
		authenticationService.setCurrentUser(currentUser.getId());
		Assert.notNull(work);
		try {
			savedAsset = assetManagementService.storeAsset(dto, asset, true);
		} catch (AssetTransformationException | IOException | HostServiceException assetException) {
			notificationService.sendNotification(new AttachmentUploadFailedNotificationTemplate(dto.getName(), work));
			return;
		}

		if (savedAsset == null) {
			return;
		}

		boolean isUploadedAssetADeliverable;
		WorkAssetAssociation workAssetAssociation;

		isUploadedAssetADeliverable = dto.isDeliverable();
		workAssetAssociation = new WorkAssetAssociation(work,
			savedAsset,
			new WorkAssetAssociationType(dto.getAssociationType()),
			dto.isDeliverable(),
			dto.getDeliverableRequirementId(),
			dto.getPosition());

		workAssetAssociationDAO.saveOrUpdate(workAssetAssociation);

		if (work instanceof Work && isUploadedAssetADeliverable) {
			Long activeWorkerId = workService.findActiveWorkerId(work.getId());
			workChangeLogService.saveWorkChangeLog(new WorkCloseoutAttachmentUploadedChangeLog(
					work.getId(), currentUser.getId(), authenticationService.getMasqueradeUserId(), activeWorkerId, savedAsset.getName(), savedAsset.getUri()
				)
			);
		}
		webHookEventService.onAssetAdded(work.getId(), work.getCompany().getId(), savedAsset.getId());

		if (work instanceof Work) {
			userNotificationService.onWorkAttachmentAdded((Work) work, asset);
		}
	}

	@Override
	public void uploadAssets(AssetDTO dto, Asset asset, List<Long> workIds, User currentUser) {
		Asset savedAsset = null;
		int count = 0;
		authenticationService.setCurrentUser(currentUser.getId());
		for (Long workId : workIds) {
			AbstractWork work = abstractWorkDAO.get(workId);
			Assert.notNull(work);
			if (count == 0) {
				try {
					savedAsset = assetManagementService.storeAsset(dto, asset, true);
				} catch (AssetTransformationException assetException) {
					notificationService.sendNotification(new AttachmentUploadFailedNotificationTemplate(dto.getName(), work));
					return;

				} catch (IOException ioException) {
					notificationService.sendNotification(new AttachmentUploadFailedNotificationTemplate(dto.getName(), work));
					return;

				} catch (HostServiceException hostServiceException) {
					notificationService.sendNotification(new AttachmentUploadFailedNotificationTemplate(dto.getName(), work));
					return;
				}

				webHookEventService.onAssetAdded(work.getId(), work.getCompany().getId(), savedAsset.getId());

				if (savedAsset == null) return;
			}

			boolean isUploadedAssetADeliverable = dto.isDeliverable();

			WorkAssetAssociation workAssetAssociation = new WorkAssetAssociation(work,
				savedAsset,
				new WorkAssetAssociationType(dto.getAssociationType()),
				dto.isDeliverable(),
				dto.getDeliverableRequirementId(),
				dto.getPosition());

			workAssetAssociationDAO.saveOrUpdate(workAssetAssociation);

			if (work instanceof Work && isUploadedAssetADeliverable) {
				Long activeWorkerId = workService.findActiveWorkerId(work.getId());
				workChangeLogService.saveWorkChangeLog(new WorkCloseoutAttachmentUploadedChangeLog(
						work.getId(), currentUser.getId(), authenticationService.getMasqueradeUserId(), activeWorkerId, savedAsset.getName(), savedAsset.getUri()
					)
				);
			}

			webHookEventService.onAssetAdded(work.getId(), work.getCompany().getId(), savedAsset.getId());

			if (work instanceof Work) {
				userNotificationService.onWorkAttachmentAdded((Work)work, asset);
			}

			count++;
		}
	}
}
