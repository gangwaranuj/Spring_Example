package com.workmarket.domains.model.asset;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="contractVersionAssetAssociation")
@Table(name="contract_version_asset_association")
@AttributeOverride(name="entity_id", column = @Column(name="contract_version_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="contract_version_id"))
})
@AuditChanges
public class ContractVersionAssetAssociation extends AbstractEntityAssetAssociation<ContractVersion> implements EntityAssetAssociation<ContractVersion> {

	private static final long serialVersionUID = 7472356389568488013L;

	public ContractVersionAssetAssociation() {
		super();
	}

	public ContractVersionAssetAssociation(ContractVersion entity, Asset asset, AssetType type) {
		super(entity, asset, type);
	}
}
