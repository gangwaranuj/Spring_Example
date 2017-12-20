package com.workmarket.service.infra.security;

import org.springframework.util.Assert;

public enum RequestContext {
	
	OWNER(1L, "owner", "Owner authorization"),
	ADMIN(2L, "admin", "Admin of the company authorization"),
	PUBLIC(3L, "public", "Public authorization"),
	ADMIN_OTHER_COMPANY(4L, "admin_other_company", "Admin of another company"),

	COMPANY_OWNED(5L, "ownerCompany", "User belongs to the company of the requested entity's owner."),

	INVITED(6L, "invited", "User has been invited to entity."),
	WORKER_POOL(7L, "worker_pool", "User is in entity owner's worker pool."),
	RESOURCE(8L, "resource", "User is a resource of an assignment related to the entity");

	// COMPANY_USER
	// COMPANY_ADMIN
	// PUBLIC_USER
	// PUBLIC_ADMIN

	private Long id;
	private String code;
	private String description;

	RequestContext(Long id, String code, String description) {
		this.id = id;
		this.code = code;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public static RequestContext newRequestContext(String code) {
		try {
			return RequestContext.valueOf(code);
		} catch (IllegalArgumentException e) {
			Assert.isTrue(false, "Invalid code");
			return null;
		}
	}
}
