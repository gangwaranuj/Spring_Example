package com.workmarket.service.business.asset;



import com.google.common.collect.ListMultimap;
import com.workmarket.domains.model.User;

public class AssetGroupBundle  extends AssetBundle{
	private ListMultimap<User, String> userAssetMap;


	public ListMultimap<User, String> getUserAssetMap() {
		return userAssetMap;
	}

	public void setUserAssetMap(ListMultimap<User, String> userAssetMap) {
		this.userAssetMap = userAssetMap;
	}

}
