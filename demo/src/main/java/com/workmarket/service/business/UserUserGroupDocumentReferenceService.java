package com.workmarket.service.business;

import com.workmarket.domains.groups.model.UserUserGroupDocumentReference;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReferencePagination;
import com.workmarket.service.business.dto.UploadDocumentDTO;
import com.workmarket.service.exception.HostServiceException;

import java.util.Calendar;
import java.util.List;

/**
 * User: micah
 * Date: 12/17/13
 * Time: 9:10 AM
 */
public interface UserUserGroupDocumentReferenceService {
	UserUserGroupDocumentReferencePagination findAllDocumentReferencesByUserIdAndUserGroupId(Long userId, Long groupId, UserUserGroupDocumentReferencePagination pagination);

	UserUserGroupDocumentReference findDocumentReferenceByUserIdAndDocumentId(Long userId, Long documentId);

	void saveDocumentReference(Long userId, Long groupId, Long requiredDocumentId, Long referencedDocumentId, String expirationDateStr);

	void saveAssetAndDocumentReference(UploadDocumentDTO uploadDocumentDTO) throws HostServiceException;

	void removeDocumentReference(Long userId, Long groupId, Long requiredDocumentId, Long referencedDocumentId);

	List<UserUserGroupDocumentReference> findAllDocumentReferencesByDate(Calendar date);
}
