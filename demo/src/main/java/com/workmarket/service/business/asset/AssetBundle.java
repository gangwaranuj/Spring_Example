package com.workmarket.service.business.asset;

import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.List;

public class AssetBundle {
	private Long userId;
	private List<String> assetUuids;
	private Calendar requestedOn;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public List<String> getAssetUuids() {
		return assetUuids;
	}
	public void setAssetUuids(List<String> assetUuids) {
		this.assetUuids = assetUuids;
	}
	public Calendar getRequestedOn() {
		return requestedOn;
	}
	public void setRequestedOn(Calendar requestedOn) {
		this.requestedOn = requestedOn;
	}
	public String getName(int index) {
		return  StringUtils.substring(assetUuids.get(index), 0, 8);
	}
}
