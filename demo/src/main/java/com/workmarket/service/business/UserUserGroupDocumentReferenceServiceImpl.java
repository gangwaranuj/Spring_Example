package com.workmarket.service.business;

import com.workmarket.domains.groups.dao.UserUserGroupDocumentReferenceDAO;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReference;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReferencePagination;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.business.dto.UploadDocumentDTO;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: micah
 * Date: 12/17/13
 * Time: 9:11 AM
 */
@Service
public class UserUserGroupDocumentReferenceServiceImpl implements UserUserGroupDocumentReferenceService {
	@Autowired private UserUserGroupDocumentReferenceDAO userUserGroupDocumentReferenceDAO;
	@Autowired private UserGroupService userGroupService;
	@Autowired private UserGroupValidationService userGroupValidationService;
	@Autowired private UserService userService;
	@Autowired private AssetManagementService assetManagementService;

	@Override
	public List<UserUserGroupDocumentReference> findAllDocumentReferencesByDate(Calendar date) {
		return userUserGroupDocumentReferenceDAO.findAllDocumentReferencesByDate(date);
	}

	@Override
	public UserUserGroupDocumentReferencePagination findAllDocumentReferencesByUserIdAndUserGroupId(Long userId, Long groupId, UserUserGroupDocumentReferencePagination pagination) {
		return userUserGroupDocumentReferenceDAO.findAllDocumentReferencesByUserIdAndUserGroupId(userId, groupId, pagination);
	}

	@Override
	public UserUserGroupDocumentReference findDocumentReferenceByUserIdAndDocumentId(Long userId, Long documentId) {
		return userUserGroupDocumentReferenceDAO.findDocumentReferenceByUserIdAndDocumentId(userId, documentId);
	}

	@Override
	public void saveDocumentReference(Long userId, Long groupId, Long requiredDocumentId, Long referencedDocumentId, String expirationDateStr) {
		User user = userService.findUserById(userId);
		UserGroup userGroup = userGroupService.findGroupById(groupId);
		Asset requiredDocument = assetManagementService.findAssetById(requiredDocumentId);
		Asset referencedDocument = assetManagementService.findAssetById(referencedDocumentId);

		UserUserGroupDocumentReference ref = new UserUserGroupDocumentReference();
		ref.setUser(user);
		ref.setUserGroup(userGroup);
		ref.setRequiredDocument(requiredDocument);
		ref.setReferencedDocument(referencedDocument);
		if (expirationDateStr != null) {
			ref.setExpirationDate(DateUtilities.getCalendarFromString(expirationDateStr));
		}

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.DOCUMENT, userGroup.getId());

		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);

		userUserGroupDocumentReferenceDAO.saveOrUpdate(ref);
	}

	@Override
	public void removeDocumentReference(Long userId, Long groupId, Long requiredDocumentId, Long referencedDocumentId) {
		UserUserGroupDocumentReference reference = userUserGroupDocumentReferenceDAO.findBy(
			"user.id", userId, "userGroup.id", groupId, "requiredDocument.id", requiredDocumentId, "referencedDocument.id", referencedDocumentId
		);
		userUserGroupDocumentReferenceDAO.delete(reference);
	}


	@Override
	public void saveAssetAndDocumentReference(UploadDocumentDTO uploadDocumentDTO) throws HostServiceException {
		UploadDTO uploadDTO = new UploadDTO();
		uploadDTO.setAssociationType(UserAssetAssociationType.NONE);
		BeanUtilities.copyProperties(uploadDTO, uploadDocumentDTO);
		Asset asset = assetManagementService.addUploadToUser(uploadDTO, uploadDocumentDTO.getUserId());
		saveDocumentReference(uploadDocumentDTO.getUserId(), uploadDocumentDTO.getGroupId(), uploadDocumentDTO.getRequiredDocumentId(), asset.getId(), uploadDocumentDTO.getExpirationDateStr());
	}
}
