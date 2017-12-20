package com.workmarket.service.business.event;

import com.workmarket.service.business.dto.UserImportDTO;

import java.util.List;

public class BulkUserUploadDispatchEvent extends Event {

	private static final long serialVersionUID = -744531513186565884L;
	private final Long userId;
	private final String uuid;
	private final UserImportDTO userImportDTO;
	private final List<String> orgUnitPath;

	public BulkUserUploadDispatchEvent(
		final Long userId,
		final String uuid,
		final UserImportDTO userImportDTO,
		final List<String> orgUnitPath) {

		this.userId = userId;
		this.uuid = uuid;
		this.userImportDTO = userImportDTO;
		this.orgUnitPath = orgUnitPath;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUuid() {
		return uuid;
	}

	public UserImportDTO getUserImportDTO() {
		return userImportDTO;
	}

	public List<String> getOrgUnitPath() {
		return orgUnitPath;
	}
}
