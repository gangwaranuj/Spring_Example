package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AvailabilityType;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: rocio
 * Date: 4/25/12
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompanyAsset {

	private Long assetId;
	private String name;
	private String description;
	private String uuid;
	private String mimeType;
	private boolean displayable = false;
	private AvailabilityType availability = new AvailabilityType(AvailabilityType.ALL);

	private String cdnUri;
	private String remoteUri;
	private String localUri;
	private Integer fileByteSize;

	private String content;
	private boolean active = true;

	private Calendar createdOn;
	private Calendar modifiedOn;
	private String creatorFullName;

	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public boolean isDisplayable() {
		return displayable;
	}

	public void setDisplayable(boolean displayable) {
		this.displayable = displayable;
	}

	public AvailabilityType getAvailability() {
		return availability;
	}

	public void setAvailability(AvailabilityType availability) {
		this.availability = availability;
	}

	public String getCdnUri() {
		return cdnUri;
	}

	public void setCdnUri(String cdnUri) {
		this.cdnUri = cdnUri;
	}

	public String getRemoteUri() {
		return remoteUri;
	}

	public void setRemoteUri(String remoteUri) {
		this.remoteUri = remoteUri;
	}

	public String getLocalUri() {
		return localUri;
	}

	public void setLocalUri(String localUri) {
		this.localUri = localUri;
	}

	public Integer getFileByteSize() {
		return fileByteSize;
	}

	public void setFileByteSize(Integer fileByteSize) {
		this.fileByteSize = fileByteSize;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Calendar getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}

	public Calendar getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Calendar modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getCreatorFullName() {
		return creatorFullName;
	}

	public void setCreatorFullName(String creatorFullName) {
		this.creatorFullName = creatorFullName;
	}

	public String getUri() {
		// Bypass the need for initializing the availability type lookup entity
		return availability.getUri(uuid, cdnUri, remoteUri);
	}

	@Override
	public String toString() {
		return "CompanyAsset{" +
				"name='" + name + '\'' +
				", description='" + description + '\'' +
				", uuid='" + uuid + '\'' +
				", mimeType='" + mimeType + '\'' +
				", displayable=" + displayable +
				", availability=" + availability +
				", cdnUri='" + cdnUri + '\'' +
				", remoteUri='" + remoteUri + '\'' +
				", localUri='" + localUri + '\'' +
				", fileByteSize=" + fileByteSize +
				", content='" + content + '\'' +
				", active=" + active +
				", createdOn=" + createdOn +
				", modifiedOn=" + modifiedOn +
				", creatorFullName='" + creatorFullName + '\'' +
				'}';
	}
}
