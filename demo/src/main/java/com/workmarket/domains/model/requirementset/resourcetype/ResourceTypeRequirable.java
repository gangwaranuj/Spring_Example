package com.workmarket.domains.model.requirementset.resourcetype;

import com.workmarket.domains.model.requirementset.Requirable;

public class ResourceTypeRequirable implements Requirable {

	private static final long serialVersionUID = -2767428047732550660L;

	private Long id;
	private String name;

	public ResourceTypeRequirable() {}
	public ResourceTypeRequirable(ResourceType resourceType) {
		this.id = resourceType.getId();
		this.name = resourceType.getDescription();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
