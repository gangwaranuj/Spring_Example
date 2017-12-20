package com.workmarket.service.business.dto;

import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.asset.type.AssetType;

public class FileDTO {
	private String sourceFilePath;
	private String name;
	private String description;
	private String mimeType;
	private Integer fileByteSize;
	private String availabilityTypeCode = AvailabilityType.ALL;
	private boolean addToCompanyLibrary = false;
	private boolean largeTransformation = false;
	private boolean smallTransformation = false;
	private String associationType = AssetType.NONE;

	public String getSourceFilePath() {
		return sourceFilePath;
	}

	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	public String getName() {
		return name;
	}

	public FileDTO setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public FileDTO setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public FileDTO setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}

	public Integer getFileByteSize() {
		return fileByteSize;
	}

	public void setFileByteSize(Integer fileByteSize) {
		this.fileByteSize = fileByteSize;
	}

	public String getAvailabilityTypeCode() {
		return availabilityTypeCode;
	}

	public void setAvailabilityTypeCode(String availabilityTypeCode) {
		this.availabilityTypeCode = availabilityTypeCode;
	}

	public boolean isAddToCompanyLibrary() {
		return addToCompanyLibrary;
	}

	public void setAddToCompanyLibrary(boolean addToCompanyLibrary) {
		this.addToCompanyLibrary = addToCompanyLibrary;
	}

	public boolean isLargeTransformation() {
		return largeTransformation;
	}

	public void setLargeTransformation(boolean largeTransformation) {
		this.largeTransformation = largeTransformation;
	}

	public boolean isSmallTransformation() {
		return smallTransformation;
	}

	public void setSmallTransformation(boolean smallTransformation) {
		this.smallTransformation = smallTransformation;
	}
	
	public boolean requiresTransformations() {
		return isSmallTransformation() || isLargeTransformation();
	}

	public String getAssociationType() {
		return associationType;
	}

	public FileDTO setAssociationType(String associationType) {
		this.associationType = associationType;
		return this;
	}

	public String toString() {
		return "FileDTO [name=" + name + ", mimeType=" + mimeType + ", sourceFilePath=" + sourceFilePath + "]";
	}
}