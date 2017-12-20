package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractEntity;
import javax.persistence.*;

@Entity(name = "defaultBackgroundImage")
@Table(name = "default_background_image")
public class DefaultBackgroundImage extends AbstractEntity {

	private static final long serialVersionUID = 2135388003032071029L;
	public static final String MBO_URL = "/media/images/backgrounds/mbo-(400k).jpg";
	private Asset asset;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "asset_id")
	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}
}
