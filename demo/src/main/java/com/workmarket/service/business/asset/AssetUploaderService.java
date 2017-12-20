package com.workmarket.service.business.asset;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.service.business.dto.AssetDTO;

import java.util.List;

/**
 * Author: rocio
 */
public interface AssetUploaderService {

	void uploadAsset(AssetDTO dto,Asset asset, Long workId, User currentUser);
	void uploadAssets(AssetDTO dto,Asset asset, List<Long> workIds, User currentUser);
}
