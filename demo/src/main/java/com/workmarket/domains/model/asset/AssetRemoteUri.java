package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: iloveopt
 * Date: 11/5/14
 */

@Entity(name = "assetRemoteUri")
@Table(name = "asset_remote_uri")
public class AssetRemoteUri extends AbstractEntity {

	private String assetRemoteUri;
	public AssetRemoteUri(){
	}

	@Column(name = "remote_uri_prefix", nullable = false, length = 200)
	public String getAssetRemoteUri() {
		return assetRemoteUri;
	}

	public void setAssetRemoteUri(String assetRemoteUri) {
		this.assetRemoteUri = assetRemoteUri;
	}
}
