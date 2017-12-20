package com.workmarket.domains.model.asset;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "userAssetAssociation")
@Table(name = "user_asset_association")
@AttributeOverride(name = "entity_id", column = @Column(name = "user_id"))
@AssociationOverrides({
		@AssociationOverride(name = "entity", joinColumns = @JoinColumn(name = "user_id"))
})
@NamedQueries({
		@NamedQuery(
				name = "userAssetAssociation.byUserAndType",
				query = "from userAssetAssociation ua " +
						"inner join ua.asset " +
						"where ua.entity.id = :userId"
		),
		@NamedQuery(
				name = "userAssetAssociation.byUserStatusAndType",
				query = "from userAssetAssociation ua " +
						"inner join fetch ua.asset a " +
						"inner join fetch ua.assetType at " +
						"where ua.entity.id = :userId " +
						"and at.code = :assetTypeCode " +
						"and ua.approvalStatus = :approvalStatus " +
						"and ua.active = 1 " +
						"and ua.deleted = 0 " +
						"order by ua.createdOn desc"
		),
		@NamedQuery(
				name = "userAssetAssociation.approvedByUserAndTypeWithAvailability",
				query = "from userAssetAssociation ua " +
						"inner join fetch ua.asset a " +
						"inner join fetch ua.assetType at " +
						"inner join fetch a.availability " +
						"where ua.entity.id = :userId " +
						"and at.code = :assetTypeCode " +
						"and ua.approvalStatus = 1 " + // Approved
						"and ua.active = 1 " +
						"and ua.deleted = 0 " +
						"order by ua.createdOn desc"
		),
		@NamedQuery(
				name = "userAssetAssociation.byUserStatusAndTypeMulti",
				query = "from userAssetAssociation ua " +
						"inner join fetch ua.asset a " +
						"inner join fetch ua.assetType at " +
						"where ua.entity.id in :userIds " +
						"and at.code = :assetTypeCode " +
						"and ua.approvalStatus = :approvalStatus " +
						"and ua.active = 1 " +
						"and ua.deleted = 0 " +
						"order by ua.createdOn desc"
		)
})
@AuditChanges
public class UserAssetAssociation extends AbstractEntityAssetAssociation<User> implements EntityAssetAssociation<User> {

	private static final long serialVersionUID = -7083863459466164838L;
	private Asset transformedSmallAsset;
	private Asset transformedLargeAsset;
	private ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;
	private boolean active = true;

	public UserAssetAssociation() {
		super();
	}

	public UserAssetAssociation(User entity, Asset asset, AssetType type) {
		super(entity, asset, type);
	}

	@ManyToOne
	@JoinColumn(name = "transformed_small_asset_id", updatable = false)
	public Asset getTransformedSmallAsset() {
		return transformedSmallAsset;
	}

	public void setTransformedSmallAsset(Asset transformedSmallAsset) {
		this.transformedSmallAsset = transformedSmallAsset;
	}

	@ManyToOne
	@JoinColumn(name = "transformed_large_asset_id", updatable = false)
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
}
