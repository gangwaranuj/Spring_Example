package com.workmarket.service.business.dto.integration;

import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by nick on 2012-12-23 11:38 AM
 */
public class AutotaskUserDTO {

	private Long id;

	@NotEmpty
	private String userName;

	private String password;

	private boolean updatePassword;

	private Long userId;

	// TODO: validate on pattern
	private String zoneUrl;

	public static AutotaskUserDTO newDTO(AutotaskUser autotaskUser) {
		return new AutotaskUserDTO()
				.setId(autotaskUser.getId())
				.setUserId(autotaskUser.getUserId())
				.setUserName(autotaskUser.getUserName())
				.setPassword(autotaskUser.getPassword()) // TODO: mask on front end
				.setZoneUrl(autotaskUser.getZoneUrl());
	}

	public Long getId() {
		return id;
	}

	public AutotaskUserDTO setId(Long id) {
		this.id = id;
		return this;
	}

	public String getUserName() {
		return userName;
	}

	public AutotaskUserDTO setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public AutotaskUserDTO setPassword(String password) {
		this.password = password;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public AutotaskUserDTO setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public String getZoneUrl() {
		return zoneUrl;
	}

	public AutotaskUserDTO setZoneUrl(String zoneUrl) {
		this.zoneUrl = zoneUrl;
		return this;
	}

	public boolean hasZoneUrl() {
		return StringUtils.isNotBlank(zoneUrl);
	}

	public boolean isUpdatePassword() {
		return updatePassword;
	}

	public void setUpdatePassword(boolean updatePassword) {
		this.updatePassword = updatePassword;
	}
}
