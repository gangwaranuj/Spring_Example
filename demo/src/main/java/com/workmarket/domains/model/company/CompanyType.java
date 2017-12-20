package com.workmarket.domains.model.company;

import org.springframework.util.Assert;

public enum CompanyType {
	CORPORATION(1L, "Corporation"),
	SOLE_PROPRIETOR(2L, "Sole Proprietor");

	CompanyType(Long id, String description) {
		this.id = id;
		this.description = description;
	}

	private Long id;
	private String description;

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public static CompanyType getById(Long id) {
		Assert.notNull(id);
		switch (id.intValue()) {
			case 1: return CORPORATION;
			case 2: return SOLE_PROPRIETOR;
		}
		Assert.isTrue(false, "Unsupported company type");
		return null;
	}
}
