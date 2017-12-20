package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
* User: iloveopt
* Date: 11/5/14
*/

@Entity(name = "assetCdnUri")
@Table(name = "asset_cdn_uri")
public class AssetCdnUri extends AbstractEntity {

	private String cdnUriPrefix;
	public AssetCdnUri(){
	}

	@Column(name = "cdn_uri_prefix", nullable = false, length = 200)
	public String getCdnUriPrefix() {
		return cdnUriPrefix;
	}

	public void setCdnUriPrefix(String cdnUriPrefix) {
		this.cdnUriPrefix = cdnUriPrefix;
	}
}
