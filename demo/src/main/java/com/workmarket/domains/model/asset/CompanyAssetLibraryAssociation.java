package com.workmarket.domains.model.asset;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="companyAssetLibraryAssociation")
@Table(name="company_asset_library_association")
@AttributeOverride(name="entity_id", column = @Column(name="company_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="company_id"))
})
@AuditChanges
public class CompanyAssetLibraryAssociation extends AbstractEntityAssetAssociation<Company> implements EntityAssetAssociation<Company> {

	private static final long serialVersionUID = 1L;
	private boolean active = true;

	public CompanyAssetLibraryAssociation() {
		super();
	}

	public CompanyAssetLibraryAssociation(Company entity, Asset asset) {
		super(entity, asset, new AssetType(AssetType.NONE));
	}

	@Column(name = "active")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}

