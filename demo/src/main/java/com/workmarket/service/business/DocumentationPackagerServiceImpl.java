package com.workmarket.service.business;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.domains.groups.service.UserGroupRequirementSetService;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.ZipAssetBundle;
import com.workmarket.domains.model.asset.type.CompanyAssetAssociationType;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReferencePagination;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.domains.model.DocumentationManager;
import com.workmarket.domains.groups.service.DocumentationManagerFactory;
import com.workmarket.domains.model.DocumentationVisitor;
import com.workmarket.domains.model.VisitableDocumentation;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class DocumentationPackagerServiceImpl implements DocumentationPackagerService {
	@Autowired DocumentationVisitor documentationVisitor;
	@Autowired UserGroupService userGroupService;
	@Autowired UserService userService;
	@Autowired AssetManagementService assetManagementService;

	@Autowired DocumentationManagerFactory documentationManagerFactory;
	@Autowired NotificationTemplateFactory notificationTemplateFactory;
	@Autowired UserUserGroupDocumentReferenceService userUserGroupDocumentReferenceService;
	@Autowired LicenseService licenseService;
	@Autowired InsuranceService insuranceService;
	@Autowired CertificationService certificationService;
	@Autowired NotificationService notificationService;
	@Autowired EventRouter eventRouter;
	@Autowired EventFactory eventFactory;
	@Autowired UserGroupRequirementSetService userGroupRequirementSetService;

	private static final Log logger = LogFactory.getLog(DocumentationPackagerServiceImpl.class);

	@Override
	public void buildDocumentationPackage(Long downloaderId, Long groupId, List<Long> userIds) {
		eventRouter.sendEvent(eventFactory.buildGetDocumentationPackageEvent(downloaderId, groupId, userIds));
	}

	@Override
	public Optional<Asset> getDocumentationPackageForUser(Long downloaderId, Long groupId, Long userId) {
		Assert.notNull(groupId);
		Assert.notNull(userId);

		UserGroup userGroup = userGroupService.findGroupById(groupId);
		User user = userService.findUserById(userId);

		return getDocumentationPackageForUser(downloaderId, userGroup, user);
	}

	@Override
	public Optional<Asset> getDocumentationPackageForUsers(Long downloaderId, Long groupId, List<Long> userIds) {
		Assert.notNull(groupId);
		Assert.notNull(userIds);

		UserGroup userGroup = userGroupService.findGroupById(groupId);
		List<User> userList = userService.findAllUsersByIds(userIds);

		return getDocumentationPackage(downloaderId, userGroup, userList);
	}

	@Override
	public Optional<Asset> getDocumentationPackageForUser(Long downloaderId, UserGroup userGroup, User user) {
		List<User> userList = Lists.newArrayList();
		userList.add(user);
		return getDocumentationPackage(downloaderId, userGroup, userList);
	}

	@Override
	public Optional<Asset> getDocumentationPackage(Long downloaderId, Long groupId) {
		Assert.notNull(groupId);

		UserGroup userGroup = userGroupService.findGroupById(groupId);
		UserPagination pagination = userGroupService.findAllUsersOfGroup(groupId, new UserPagination(true));

		return getDocumentationPackage(downloaderId, userGroup, pagination.getResults());
	}

	@Override
	public Optional<Asset> getDocumentationPackage(Long downloaderId, UserGroup userGroup, List<User> users) {
		Assert.notNull(userGroup);
		Assert.notNull(users);

		try {
			DocumentationManager documentationManager = documentationManagerFactory.build();

			documentationManager.start(userGroup.getName());

			for (User user : users) {
				addToDocumentationPackageForUser(documentationManager, user, userGroup);
			}

			// finish up
			ZipAssetBundle zipAssetBundle = documentationManager.finish();

			AssetDTO assetDTO = new AssetDTO();
			assetDTO.setSourceFilePath(zipAssetBundle.getFileName());
			assetDTO.setName(String.format("documentation-download-%s.zip", DateUtilities.getISO8601(new Date())));
			assetDTO.setDescription(String.format(
					"%d documentation bundled on %s", zipAssetBundle.getFileSize(), DateUtilities.format("yyyy-MM-dd @ HH:mm", new Date())
			));
			assetDTO.setAssociationType(CompanyAssetAssociationType.ASSET_BUNDLE);
			assetDTO.setMimeType(MimeType.ZIP.getMimeType());
			assetDTO.setAvailabilityTypeCode(AvailabilityType.ALL);

			// Store new zip file for the company
			Asset asset = assetManagementService.storeAssetForCompany(assetDTO, userGroup.getCompany().getId());
			Optional<Asset> oAsset = Optional.fromNullable(asset);
			if (oAsset.isPresent()) {
				String uri = "/asset/download/" + oAsset.get().getUUID();
				notificationService.sendNotification(
						notificationTemplateFactory.buildDocumentationPackageNotificationTemplate(downloaderId, uri)
				);
			}
			return oAsset;
		} catch (IOException|AssetTransformationException|HostServiceException e) {
			logger.error(e);
			return Optional.fromNullable(null);
		}
	}

	private void addToDocumentationPackageForUser(DocumentationManager documentationManager, User user, UserGroup userGroup) {
		List<VisitableDocumentation> documentationItems = getAllDocumentation(documentationManager, user, userGroup);

		for (VisitableDocumentation documentationItem : documentationItems) {
			documentationItem.accept(documentationManager, documentationVisitor);
		}
	}

	private List<VisitableDocumentation> handleDocuments(DocumentationManager documentationManager, User user, UserGroup userGroup) {
		UserUserGroupDocumentReferencePagination pagination =
				userUserGroupDocumentReferenceService.findAllDocumentReferencesByUserIdAndUserGroupId(
						user.getId(), userGroup.getId(), new UserUserGroupDocumentReferencePagination(true)
				);
		List<VisitableDocumentation> ret = Lists.newArrayList();
		ret.addAll(pagination.getResults());
		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<VisitableDocumentation> handleLicenses(DocumentationManager documentationManager, User user, UserGroup userGroup) {
		List<Long> requiredLicenseIds = userGroupRequirementSetService.findUserGroupsRequiredLicenseIds(userGroup.getId());

		if (requiredLicenseIds.isEmpty()) {
			return Collections.emptyList();
		}

		UserLicenseAssociationPagination pagination =
			licenseService.findAllAssociationsByUserIdInList(
				user.getId(),
				requiredLicenseIds,
				new UserLicenseAssociationPagination(true)
			);
		List<VisitableDocumentation> ret = Lists.newArrayList();
		ret.addAll(pagination.getResults());
		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<VisitableDocumentation> handleInsurances(DocumentationManager documentationManager, User user, UserGroup userGroup) {
		List<Long> requiredInsuranceIds = userGroupRequirementSetService.findUserGroupsRequiredInsuranceIds(userGroup.getId());

		if (requiredInsuranceIds.isEmpty()) {
			return Collections.emptyList();
		}

		UserInsuranceAssociationPagination pagination =
			insuranceService.findAllAssociationsByUserIdInList(
				user.getId(),
				requiredInsuranceIds,
				new UserInsuranceAssociationPagination(true)
			);

		List<VisitableDocumentation> ret = Lists.newArrayList();
		ret.addAll(pagination.getResults());
		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<VisitableDocumentation> handleCertifications(DocumentationManager documentationManager, User user, UserGroup userGroup) {
		List<Long> requiredCertificationIds = userGroupRequirementSetService.findUserGroupsRequiredCertificationIds(userGroup.getId());

		if (requiredCertificationIds.isEmpty()) {
			return Collections.emptyList();
		}

		UserCertificationAssociationPagination pagination =
			certificationService.findAllAssociationsByUserIdInList(
				user.getId(),
				requiredCertificationIds,
				new UserCertificationAssociationPagination(true)
			);

		List<VisitableDocumentation> ret = Lists.newArrayList();
		ret.addAll(pagination.getResults());
		return ret;
	}

	private List<VisitableDocumentation> getAllDocumentation(DocumentationManager documentationManager, User user, UserGroup userGroup) {
		// handle all the different types to be packaged up
		List<VisitableDocumentation> documents = handleDocuments(documentationManager, user, userGroup);
		List<VisitableDocumentation> licenses = handleLicenses(documentationManager, user, userGroup);
		List<VisitableDocumentation> insurances = handleInsurances(documentationManager, user, userGroup);
		List<VisitableDocumentation> certifications = handleCertifications(documentationManager, user, userGroup);

		List<VisitableDocumentation> documentationItems = Lists.newArrayList(documents);
		documentationItems.addAll(licenses);
		documentationItems.addAll(insurances);
		documentationItems.addAll(certifications);

		return documentationItems;
	}
}
