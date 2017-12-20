package com.workmarket.service.business.asset;

import com.google.common.collect.ListMultimap;
import com.workmarket.domains.model.User;

import java.util.List;

public interface AssetBundlerQueue {
	void bundleAssetsForUser(ListMultimap<User,String> assetMap, Long userId);
	void bundleAssetsForUser(List<String> assetUuids, Long userId);
	void bundleAssetsForUser(List<String> assetUuids,List<String> assignmentIds, Long userId);

}
