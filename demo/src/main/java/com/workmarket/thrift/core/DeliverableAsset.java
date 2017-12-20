package com.workmarket.thrift.core;

import com.workmarket.domains.model.asset.WorkAssetAssociation;

import java.util.Calendar;

/**
 * Created by rahul on 3/27/14
 */
public class DeliverableAsset extends Asset {
	private String uploadedBy;
	private long uploadDate;
	private Long rejectedOn;
	private String rejectionReason;
	private String rejectedBy;
	private Long deliverableRequirementId;
	private Integer position;

	public DeliverableAsset() {}

	public DeliverableAsset(String uploadedBy, long uploadDate, Long rejectedOn, Long deliverableRequirementId, Integer position, String rejectionReason, String rejectedBy) {
		this.uploadedBy = uploadedBy;
		this.uploadDate = uploadDate;
		this.rejectedOn = rejectedOn;
		this.rejectionReason = rejectionReason;
		this.rejectedBy = rejectedBy;
		this.deliverableRequirementId = deliverableRequirementId;
		this.position = position;
	}

	public DeliverableAsset(
		long id,
		String uuid,
		String name,
		String description,
		String mimeType,
		String type,
		String uri,
		String uploadedBy,
		long uploadDate,
		Long rejectedOn,
		Long deliverableRequirementId,
		Integer position,
		String rejectionReason,
		String rejectedBy) {

		super(id, uuid, name, description, mimeType, type, uri);
		this.uploadedBy = uploadedBy;
		this.uploadDate = uploadDate;
		this.rejectedOn = rejectedOn;
		this.rejectionReason = rejectionReason;
		this.rejectedBy = rejectedBy;
		this.deliverableRequirementId = deliverableRequirementId;
		this.position = position;
	}

	public DeliverableAsset(WorkAssetAssociation workAssetAssociation) {
		super(workAssetAssociation.getAsset().getId(),
				workAssetAssociation.getAsset().getUUID(),
				workAssetAssociation.getAsset().getName(),
				workAssetAssociation.getAsset().getDescription(),
				workAssetAssociation.getAsset().getMimeType(),
				workAssetAssociation.getAssetType().getCode(),
				workAssetAssociation.getAsset().getUri());

		com.workmarket.domains.model.asset.Asset asset = workAssetAssociation.getAsset();
		this.setUploadDate(asset.getCreatedOn().getTimeInMillis());

		Calendar rejectedOn = workAssetAssociation.getRejectedOn();
		if (rejectedOn != null) {
			this.setRejectedOn(rejectedOn.getTimeInMillis());
			this.setRejectionReason(workAssetAssociation.getRejectionReason());
			com.workmarket.domains.model.User rejector = workAssetAssociation.getRejectedBy();
			if (rejector != null) {
				this.setRejectedBy(rejector.getFullName());
			}
		}

		com.workmarket.domains.model.asset.Asset transformSmallAsset = workAssetAssociation.getTransformedSmallAsset();
		com.workmarket.domains.model.asset.Asset transformLargeAsset = workAssetAssociation.getTransformedLargeAsset();

		if (transformSmallAsset != null) {
			this.setTransformSmallUuid(transformSmallAsset.getUUID());
		}
		if (transformLargeAsset != null) {
			this.setTransformLargeUuid(transformLargeAsset.getUUID());
		}

		this.deliverableRequirementId = workAssetAssociation.getDeliverableRequirementId();
		this.position = workAssetAssociation.getPosition();
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public long getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(long uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Long getRejectedOn() {
		return rejectedOn;
	}

	public void setRejectedOn(Long rejectedOn) {
		this.rejectedOn = rejectedOn;
	}

	public boolean isRejected() {
		return this.rejectedOn != null;
	}

	public Long getDeliverableRequirementId() {
		return deliverableRequirementId;
	}

	public void setDeliverableRequirementId(Long deliverableRequirementId) {
		this.deliverableRequirementId = deliverableRequirementId;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public String getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(String rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DeliverableAsset)) return false;
		if (!super.equals(o)) return false;

		DeliverableAsset that = (DeliverableAsset) o;

		if (uploadDate != that.uploadDate) return false;
		if (position != null ? !position.equals(that.position) : that.position != null)
			return false;
		if (deliverableRequirementId != null ? !deliverableRequirementId.equals(that.deliverableRequirementId) : that.deliverableRequirementId != null)
			return false;
		if (rejectedBy != null ? !rejectedBy.equals(that.rejectedBy) : that.rejectedBy != null) return false;
		if (rejectedOn != null ? !rejectedOn.equals(that.rejectedOn) : that.rejectedOn != null) return false;
		if (rejectionReason != null ? !rejectionReason.equals(that.rejectionReason) : that.rejectionReason != null)
			return false;
		if (uploadedBy != null ? !uploadedBy.equals(that.uploadedBy) : that.uploadedBy != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (uploadedBy != null ? uploadedBy.hashCode() : 0);
		result = 31 * result + (int) (uploadDate ^ (uploadDate >>> 32));
		result = 31 * result + (rejectedOn != null ? rejectedOn.hashCode() : 0);
		result = 31 * result + (rejectionReason != null ? rejectionReason.hashCode() : 0);
		result = 31 * result + (rejectedBy != null ? rejectedBy.hashCode() : 0);
		result = 31 * result + (deliverableRequirementId != null ? deliverableRequirementId.hashCode() : 0);
		result = 31 * result + (position != null ? position.hashCode() : 0);
		return result;
	}
}
