package com.workmarket.domains.work.service;

import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.asset.WorkAssetVisibilityDAO;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.WorkAssetVisibility;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;

@Service
public class DocumentServiceImpl implements DocumentService {

	private static final Log logger = LogFactory.getLog(DocumentServiceImpl.class);

	@Autowired private AssetManagementService assetManagementService;
	@Autowired private LookupEntityDAO lookupEntityDAO;
	@Autowired private WorkAssetVisibilityDAO workAssetVisibilityDAO;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkService workService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private WorkActionRequestFactory workActionRequestFactory;
	@Autowired private WorkAuditService workAuditService;

	@Override
	public WorkAssetAssociation addDocument(String workNumber, AssetDTO assetDTO, InputStream inputStream) throws Exception {

		// Create temp file from stream
		File tmpFile = File.createTempFile("document", null);
		FileUtils.copyInputStreamToFile(inputStream, tmpFile);
		return addDocument(workNumber, assetDTO.setSourceFilePath(tmpFile.getAbsolutePath()));
	}

	@Override
	public WorkAssetAssociation addDocument(String workNumber, AssetDTO assetDTO) throws Exception {

		assetDTO.setAssociationType(WorkAssetAssociationType.ATTACHMENT);

		Work work = workService.findWorkByWorkNumber(workNumber);
		WorkAssetAssociation workAssetAssociation = assetManagementService.storeAssetForWork(assetDTO, work.getId(), true);

		VisibilityType visibilityType = lookupEntityDAO.findByCodeWithDefault(VisibilityType.class, assetDTO.getVisibilityTypeCode(), VisibilityType.createDefaultVisibility());
		workAssetVisibilityDAO.saveOrUpdate(new WorkAssetVisibility(workAssetAssociation, visibilityType));

		Asset asset = workAssetAssociation.getAsset();
		userNotificationService.onWorkAttachmentAdded(work, asset);

		Long onBehalfOfUserId = workService.findActiveWorkerId(work.getId());
		WorkActionRequest workActionRequest = workActionRequestFactory.create(work, authenticationService.getCurrentUserId(), onBehalfOfUserId, authenticationService.getMasqueradeUserId(), WorkAuditType.DOCUMENT_ATTACHMENT);

		workAuditService.auditAndReindexWork(workActionRequest);

		return workAssetAssociation;
	}

	@Override
	public void updateDocumentVisibility(Long workId, Long assetId, String visibilityTypeCode) {
		Assert.notNull(workId);
		Assert.notNull(assetId);
		Assert.notNull(visibilityTypeCode);

		VisibilityType visibilityType = lookupEntityDAO.findByCode(VisibilityType.class, visibilityTypeCode);
		if (visibilityType == null) {
			logger.error(String.format("Error updating document visibility: Unknown visibility type code: %s",  visibilityTypeCode));
			return;
		}

		WorkAssetAssociation workAssetAssociation = assetManagementService.findAssetAssociationsByWorkAndAsset(workId, assetId);
		if (workAssetAssociation == null) {
			logger.error(String.format("Error updating document visibility: Could not find workAssetAssociation. wordId: %d, assetId: %d", workId, assetId));
			return;
		}
		WorkAssetVisibility existingVisibility = workAssetVisibilityDAO.findByWorkAssetAssociationId(workAssetAssociation.getId());
		if (existingVisibility == null) {
			existingVisibility = new WorkAssetVisibility();
		}
		existingVisibility.setVisibilityType(visibilityType);
		existingVisibility.setWorkAssetAssociation(workAssetAssociation);

		workAssetVisibilityDAO.saveOrUpdate(existingVisibility);
	}

	@Override
	public boolean isDocumentVisible(WorkAssetVisibility workAssetVisibility, AbstractWork work) {
		Assert.notNull(work);
		Assert.notNull(workAssetVisibility);

		VisibilityType visibilityType = workAssetVisibility.getVisibilityType();

		return isDocumentVisible(visibilityType, work);
	}

	@Override
	public boolean isDocumentVisible(VisibilityType visibilityType, AbstractWork work) {
		Assert.notNull(work);
		Assert.notNull(visibilityType);

		if (visibilityType.isPublic()) {
			return true;
		}

		Long currentUserId = authenticationService.getCurrentUserId();
		Long currentUserCompanyId = authenticationService.getCurrentUserCompanyId();

		boolean isUserWorkAdmin = currentUserCompanyId.equals(work.getCompany().getId());
		boolean isUserAssignedWorker = currentUserId.equals(workService.findActiveWorkerId(work.getId()));

		if (visibilityType.isInternal()) {
			return isUserWorkAdmin;
		} else if (visibilityType.isAssignedWorker()) {
			return isUserWorkAdmin || isUserAssignedWorker;
		}
		return true;
	}
}
