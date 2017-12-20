package com.workmarket.web.forms.mmw;

import java.util.Map;

public class SSOConfigurationForm {

	private String idpMetadata;
	private String spMetadata;
	private String entityId;
	private Long defaultRoleId;
	private Map<Long, String> roles;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public Long getDefaultRoleId() {
		return defaultRoleId;
	}

	public void setDefaultRoleId(Long defaultRoleId) {
		this.defaultRoleId = defaultRoleId;
	}

	public Map<Long, String> getRoles() {
		return roles;
	}

	public void setRoles(Map<Long, String> roles) {
		this.roles = roles;
	}

	public String getIdpMetadata() {
		return idpMetadata;
	}

	public void setIdpMetadata(String idpMetadata) {
		this.idpMetadata = idpMetadata;
	}

	public String getSpMetadata() {
		return spMetadata;
	}

	public void setSpMetadata(String spMetadata) {
		this.spMetadata = spMetadata;
	}
}
