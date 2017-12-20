package com.workmarket.domains.model.asset;

import javax.persistence.*;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="companyAssetAssociation")
@Table(name="company_asset_association")
@AttributeOverride(name="entity_id", column = @Column(name="company_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="company_id"))
})
@AuditChanges
public class CompanyAssetAssociation extends AbstractEntityAssetAssociation<Company> implements EntityAssetAssociation<Company> {

	private static final long serialVersionUID = 1L;
	private Asset transformedSmallAsset;
	private Asset transformedLargeAsset;
	private ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;
	private boolean active = true;

	public CompanyAssetAssociation() {
		super();
	}

	public CompanyAssetAssociation(Company entity, Asset asset, AssetType type) {
		super(entity, asset, type);
	}

	@ManyToOne
	@JoinColumn(name="transformed_small_asset_id", updatable = false)
	public Asset getTransformedSmallAsset() {
		return transformedSmallAsset;
	}

	public void setTransformedSmallAsset(Asset transformedSmallAsset) {
		this.transformedSmallAsset = transformedSmallAsset;
	}

	@ManyToOne
	@JoinColumn(name="transformed_large_asset_id", updatable = false)
	public Asset getTransformedLargeAsset() {
		return transformedLargeAsset;
	}

	public void setTransformedLargeAsset(Asset transformedLargeAsset) {
		this.transformedLargeAsset = transformedLargeAsset;
	}

	@Column(name = "approval_status", nullable = false)
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public Asset getSmall() {
		return transformedSmallAsset;
	}

	@Transient
	public Asset getLarge() {
		return transformedLargeAsset;
	}
}
