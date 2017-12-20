package com.workmarket.dao.requirement;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceType;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ResourceTypeDAOImpl implements ResourceTypeDAO {
	private final List<ResourceTypeRequirable> types = Lists.newArrayList();
	private boolean loaded = false;

	@Override
	public List<ResourceTypeRequirable> findAll() {
		this.loadTypes();
		return this.types;
	}

	private void loadTypes() {
		if (this.loaded) {return;} // Should only load once
		for (ResourceType type : ResourceType.values()) {
			this.types.add(new ResourceTypeRequirable(type));
		}
		this.loaded = true;
	}
}
