package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.asset.type.AssetType;

public interface EntityAssetAssociation<T extends AbstractEntity> {
	public T getEntity();
	public Asset getAsset();
	public AssetType getAssetType();
}