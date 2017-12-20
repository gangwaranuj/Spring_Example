package com.workmarket.domains.groups.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReference;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReferencePagination;

import java.util.Calendar;
import java.util.List;

/**
 * User: micah
 * Date: 12/16/13
 * Time: 4:29 PM
 */
public interface UserUserGroupDocumentReferenceDAO extends DAOInterface<UserUserGroupDocumentReference> {
	UserUserGroupDocumentReferencePagination findAllDocumentReferencesByUserIdAndUserGroupId(Long userId, Long userGroupId, UserUserGroupDocumentReferencePagination pagination);

	List<UserUserGroupDocumentReference> findAllDocumentReferencesByDate(Calendar date);

	UserUserGroupDocumentReference findDocumentReferenceByUserIdAndDocumentId(Long userId, Long documentId);
}
