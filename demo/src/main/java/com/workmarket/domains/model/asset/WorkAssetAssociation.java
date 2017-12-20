package com.workmarket.domains.model.asset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

import java.util.Calendar;

@Entity(name="workAssetAssociation")
@Table(name="work_asset_association")
@NamedQueries({
	@NamedQuery(name="workAssetAssociation.countDeliverableAssetsByDeliverableRequirementId", query="select count(*) " +
			"from workAssetAssociation a where a.deliverableRequirementId = :deliverableRequirementId and a.deleted = false and a.deliverable = true and a.rejectedBy IS NULL"),
	@NamedQuery(name="workAssetAssociation.findDeliverableAssetPositionsByDeliverableRequirementId", query="select a.position " +
			"from workAssetAssociation a where a.deliverableRequirementId = :deliverableRequirementId and a.deleted = false and a.deliverable = true and a.rejectedBy IS NULL order by a.position asc"),
	@NamedQuery(name="workAssetAssociation.findByDeliverableRequirement", query = "select a from workAssetAssociation a where a.deliverableRequirementId = :deliverableRequirementId"),
	@NamedQuery(
		name="workAssetAssociation.findByDeliverableRequirementAndPosition",
		query =
			"select a from workAssetAssociation a " +
			"where a.entity.id = :workId and a.deliverableRequirementId = :deliverableRequirementId and a.position = :position and a.deleted = false"
	),
	@NamedQuery(name="workAssetAssociation.findAllDeliverablesByWorkId", query = "select a from workAssetAssociation a where a.entity.id = :workId and a.deleted = false and a.deliverable = true"),
	@NamedQuery(name="workAssetAssociation.countAssets", query="select count(*) from workAssetAssociation a where a.entity.id = :workId and a.assetType.code = 'attachment' and a.deleted = false"),
	@NamedQuery(name="workAssetAssociation.countClosingAssets", query="select count(*) from workAssetAssociation a where a.entity.id = :workId and a.assetType.code = 'closing' and a.deleted = false"),
	@NamedQuery(name="workAssetAssociation.findByWork", query="select a from workAssetAssociation a where a.entity.id = :workId and a.deleted = false order by a.createdOn asc"),
	@NamedQuery(name="workAssetAssociation.find", query="select a from workAssetAssociation a where a.entity.id = :workId and a.asset.id = :assetId"),
	@NamedQuery(name="workAssetAssociation.findBulkByWork", query="select a from workAssetAssociation a where a.entity.id IN (:workIds) and a.asset.id = :assetId"),
	@NamedQuery(name="workAssetAssociation.findAllBulkByWork", query="select a from workAssetAssociation a where a.entity.id IN (:workIds) and a.deleted = false")
})

@AuditChanges
public class WorkAssetAssociation extends AbstractEntityAssetAssociation<AbstractWork> implements EntityAssetAssociation<AbstractWork> {
	private static final long serialVersionUID = 1L;

	private Asset transformedSmallAsset;
	private Asset transformedLargeAsset;
	private boolean deliverable = false;
	private Long deliverableRequirementId;
	private Integer position;
	private User rejectedBy;
	private Calendar rejectedOn;
	private String rejectionReason;

	public WorkAssetAssociation() {}
	public WorkAssetAssociation(AbstractWork entity, Asset asset, AssetType type) {
		super(entity, asset, type);
	}
	public WorkAssetAssociation(AbstractWork entity, Asset asset, AssetType type, boolean isDeliverable, Long deliverableRequirementId, Integer position) {
		super(entity, asset, type);
		this.deliverable = isDeliverable;
		this.deliverableRequirementId = deliverableRequirementId;
		this.position = position;
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

	@Column(name = "is_deliverable")
	public boolean isDeliverable() {
		return deliverable;
	}

	public void setDeliverable(boolean deliverable) {
		this.deliverable = deliverable;
	}

	@Column(name = "deliverable_requirement_id")
	public Long getDeliverableRequirementId() {
		return deliverableRequirementId;
	}

	public void setDeliverableRequirementId(Long deliverableRequirementId) {
		this.deliverableRequirementId = deliverableRequirementId;
	}

	@Column(name = "position")
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rejected_by", referencedColumnName = "id")
	public User getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(User rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	@Column(name = "rejected_on")
	public Calendar getRejectedOn() {
		return rejectedOn;
	}

	public void setRejectedOn(Calendar rejectedOn) {
		this.rejectedOn = rejectedOn;
	}

	@Size(min = 0, max = Constants.TEXT_SHORT)
	@Column(name = "rejection_reason", nullable = true)
	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	@Transient
	public boolean isRejected() {
		return rejectedOn != null;
	}
}
