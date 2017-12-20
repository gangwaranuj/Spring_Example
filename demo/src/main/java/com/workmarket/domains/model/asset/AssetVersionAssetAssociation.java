package com.workmarket.domains.model.asset;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="assetVersionAssetAssociation")
@Table(name="asset_version_asset_association")
@AttributeOverride(name="entity_id", column = @Column(name="asset_version_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="asset_version_id"))
})
@AuditChanges
public class AssetVersionAssetAssociation extends AbstractEntityAssetAssociation<Asset> implements EntityAssetAssociation<Asset> {

	private static final long serialVersionUID = 1L;

	public AssetVersionAssetAssociation() {
		super();
	}

	public AssetVersionAssetAssociation(Asset entity, Asset asset) {
		super(entity, asset);
	}
}
