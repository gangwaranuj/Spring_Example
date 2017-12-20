package com.workmarket.service.business.dto;

import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.asset.Upload;

public class UploadDTO extends FileDTO {
	private Long uploadId;
	private String uploadUuid;
	private String visibilityTypeCode = VisibilityType.DEFAULT_VISIBILITY;

	public static UploadDTO newDTO(Upload upload) {
		UploadDTO dto = new UploadDTO();
		dto.setUploadId(upload.getId());
		dto.setUploadUuid(upload.getUUID());
		dto.setSourceFilePath(upload.getFilePath());
		dto.setName(upload.getFilename());
		dto.setMimeType(upload.getMimeType());
		dto.setFileByteSize(upload.getFileByteSize());
		return dto;
	}

	public Long getUploadId() {
		return uploadId;
	}
	public void setUploadId(Long uploadId) {
		this.uploadId = uploadId;
	}
	public String getUploadUuid() {
		return uploadUuid;
	}
	public void setUploadUuid(String uploadUuid) {
		this.uploadUuid = uploadUuid;
	}
	public String getVisibilityTypeCode() { return visibilityTypeCode; }
	public void setVisibilityTypeCode(String visibilityTypeCode) { this.visibilityTypeCode = visibilityTypeCode; }
}
