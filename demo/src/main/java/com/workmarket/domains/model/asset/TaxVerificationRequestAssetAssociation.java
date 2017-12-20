package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.model.tax.TaxVerificationRequest;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by nick on 12/2/12 11:04 AM
 */
@Entity(name="taxVerificationRequestAssetAssociation")
@Table(name="tax_verification_request_asset_association")
@AuditChanges
public class TaxVerificationRequestAssetAssociation extends AbstractEntityAssetAssociation<TaxVerificationRequest> implements EntityAssetAssociation<TaxVerificationRequest> {

	public TaxVerificationRequestAssetAssociation() {
		super();
	}
	public TaxVerificationRequestAssetAssociation(TaxVerificationRequest entity, Asset asset, AssetType type) {
		super(entity, asset, type);
	}
}
