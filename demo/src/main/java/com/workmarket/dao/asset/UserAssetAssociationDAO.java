package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.UserAssetAssociation;

import java.util.Collection;
import java.util.List;

public interface UserAssetAssociationDAO extends DeletableDAOInterface<UserAssetAssociation>{

	List<UserAssetAssociation> findAllActiveUserAssetsByUserAndType(Long userId, String assetTypeCode);

	List<UserAssetAssociation> findAllActiveUserAssetsWithAvailabilityByUserAndType(Long userId, String assetTypeCode);

	List<UserAssetAssociation> findAllActiveOrderedUserAssetsWithAvailabilityByUserAndType(Long userId, String[] assetTypeCode);

	UserAssetAssociation findUserAssetAssociation(Long userId, Long assetId);

	Asset findUserAvatarOriginal(Long userId);

	List<UserAssetAssociation> findUserAssetAssociationAvatarWithAvailability(Long userId);

	UserAssetAssociation findUserAvatars(Long userId);
	List<UserAssetAssociation> findUserAvatars(final Collection<Long> userIds);
	UserAssetAssociation findPreviousUserAvatars(Long userId);
	UserAssetAssociation findBackgroundImage(Long userId);
}

