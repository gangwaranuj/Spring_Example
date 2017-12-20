package com.workmarket.service.business.event.asset;

import com.workmarket.service.business.event.ScheduledEvent;

public class AssetBundleExpirationEvent extends ScheduledEvent {
	private static final long serialVersionUID = 1L;
	
	private String assetUuid;

	public String getAssetUuid() {
		return assetUuid;
	}

	public void setAssetUuid(String assetUuid) {
		this.assetUuid = assetUuid;
	}
}
