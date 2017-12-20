package com.workmarket.service.business.integration.mbo;

import com.workmarket.service.business.dto.ProfileDTO;

public class UserProfileDTO extends ProfileDTO {
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}