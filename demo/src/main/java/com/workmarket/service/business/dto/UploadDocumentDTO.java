package com.workmarket.service.business.dto;

/**
 * User: micah
 * Date: 12/18/13
 * Time: 5:36 PM
 */
public class UploadDocumentDTO {
	Long userId;
	Long groupId;
	Long requiredDocumentId;
	Long referenceDocumentId;
	String uploadUuid;
	String mimeType;
	String name;
	String expirationDateStr;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getRequiredDocumentId() {
		return requiredDocumentId;
	}

	public void setRequiredDocumentId(Long requiredDocumentId) {
		this.requiredDocumentId = requiredDocumentId;
	}

	public Long getReferenceDocumentId() {
		return referenceDocumentId;
	}

	public void setReferenceDocumentId(Long referenceDocumentId) {
		this.referenceDocumentId = referenceDocumentId;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getUploadUuid() {
		return uploadUuid;
	}

	public void setUploadUuid(String uploadUuid) {
		this.uploadUuid = uploadUuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpirationDateStr() {
		return expirationDateStr;
	}

	public void setExpirationDateStr(String expirationDateStr) {
		this.expirationDateStr = expirationDateStr;
	}
}
