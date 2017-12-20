package com.workmarket.dao;

import com.workmarket.domains.model.ProfileModificationPagination;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.UserProfileModification;

import java.util.List;

public interface UserProfileModificationDAO extends DAOInterface<UserProfileModification> {

	ProfileModificationPagination findAllProfileModificationsByUserId(Long userId, ProfileModificationPagination pagination);
	
	UserProfileModification findUserProfileModificationById(Long id);
	
	ProfileModificationPagination findAllPendingProfileModifications(ProfileModificationPagination pagination);

	List<UserProfileModification> findAllPendingProfileModificationsByUserId(Long userId);
	
	List<UserProfileModification> findAllPendingProfileModificationsByUserIdAndType(Long userId, ProfileModificationType type);

}

