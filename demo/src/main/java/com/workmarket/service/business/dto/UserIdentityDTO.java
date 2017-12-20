package com.workmarket.service.business.dto;

/**
 * DTO for user identity.
 */
public class UserIdentityDTO {
	private final String uuid;
	private final Long userId;
	private final String userNumber;

	public UserIdentityDTO(final Long userId, final String userNumber, final String uuid) {
		this.userId = userId;
		this.userNumber = userNumber;
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUserNumber() {
		return userNumber;
	}
}
