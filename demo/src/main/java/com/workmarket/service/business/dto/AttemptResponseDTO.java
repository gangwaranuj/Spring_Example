package com.workmarket.service.business.dto;

import java.util.List;

import com.google.common.collect.Lists;

public class AttemptResponseDTO {
	private Long choiceId;
	private String value;
	private List<AssetDTO> assetDTOs = Lists.newArrayList();
	private List<UploadDTO> uploadDTOs = Lists.newArrayList();
	
	public AttemptResponseDTO() {}
	public AttemptResponseDTO(Long choiceId, String value) {
		this.choiceId = choiceId;
		this.value = value;
	}
	
	public Long getChoiceId() {
		return choiceId;
	}
	public void setChoiceId(Long choiceId) {
		this.choiceId = choiceId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<AssetDTO> getAssets() {
		return assetDTOs;
	}
	public void setAssets(List<AssetDTO> assetDTOs) {
		this.assetDTOs = assetDTOs;
	}
	public void addToAssets(AssetDTO dto) {
		assetDTOs.add(dto);
	}
	public List<UploadDTO> getUploads() {
		return uploadDTOs;
	}
	public void setUploads(List<UploadDTO> uploadDTOs) {
		this.uploadDTOs = uploadDTOs;
	}
	public void addToUploads(UploadDTO dto) {
		uploadDTOs.add(dto);
	}
}