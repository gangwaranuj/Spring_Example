package com.workmarket.domains.model.asset;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="taxEntityAssetAssociation")
@Table(name="tax_entity_asset_association")
@AuditChanges
public class TaxEntityAssetAssociation extends AbstractEntityAssetAssociation<AbstractTaxEntity> implements EntityAssetAssociation<AbstractTaxEntity> {

	private static final long serialVersionUID = 1L;


	public TaxEntityAssetAssociation() {
		super();
	}
	public TaxEntityAssetAssociation(AbstractTaxEntity entity, Asset asset, AssetType type) {
		super(entity, asset, type);
	}
}
