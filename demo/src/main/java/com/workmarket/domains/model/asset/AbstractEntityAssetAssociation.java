package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.asset.type.AssetType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntityAssetAssociation<T extends AbstractEntity> extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private T entity;
	private Asset asset;
	private AssetType type = new AssetType(AssetType.NONE);

	public AbstractEntityAssetAssociation() {}
	public AbstractEntityAssetAssociation(T entity, Asset asset) {
		this.entity = entity;
		this.asset = asset;
	}

	public AbstractEntityAssetAssociation(T entity, Asset asset, AssetType type) {
		this.entity = entity;
		this.asset = asset;
		this.type = type;
	}

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="entity_id", updatable = false)
	public T getEntity() {
		return entity;
	}
	public void setEntity(T entity) {
		this.entity = entity;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional=false)
	@JoinColumn(name="asset_id", updatable = false)
	public Asset getAsset() {
		return asset;
	}
	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="asset_type_code", referencedColumnName="code", nullable=false)
	public AssetType getAssetType() {
		return type;
	}
	public void setAssetType(AssetType type) {
		this.type = type;
	}
}
