package com.workmarket.search.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class CacheBuilder {

	private final Map<Long, String> entityIdNameMap = Maps.newLinkedHashMap();
	private final List<Long> entityIds = Lists.newArrayList();
	private final Set<Long> misses = Sets.newHashSet();
	private final List<Object> cachedObjectValues = Lists.newArrayList();

	public CacheBuilder() {
	}

	public CacheBuilder(Collection<Long> entityIds) {
		if (isNotEmpty(entityIds)) {
			setEntityIds(entityIds);
		}
	}

	public Map<Long, String> getEntityIdNameMap() {
		return entityIdNameMap;
	}

	public List<Long> getEntityIds() {
		return entityIds;
	}

	public void setEntityIds(Collection<Long> entityIds) {
		this.entityIds.addAll(entityIds);
	}

	public Set<Long> getMisses() {
		return misses;
	}

	public List<Object> getCachedObjectValues() {
		return cachedObjectValues;
	}

	public void setCachedObjectValues(Collection<Object> cachedObjectValues) {
		this.cachedObjectValues.addAll(cachedObjectValues);
	}

	public List<String> buildKeys(String hashKey) {
		List<String> allKeys = Lists.newArrayListWithExpectedSize(entityIds.size());
		for (Long id : entityIds) {
			allKeys.add(String.format(hashKey, id));
		}
		return allKeys;
	}

	public void synchronize() {
		misses.clear();
		entityIdNameMap.clear();
		for (int i = 0; i < cachedObjectValues.size(); i++) {
			if (cachedObjectValues.get(i) == null) {
				misses.add(entityIds.get(i));
			} else {
				entityIdNameMap.put(entityIds.get(i), (String) cachedObjectValues.get(i));
			}
		}
	}

	public void addToEntityIdNameMap(Long id, String name) {
		entityIdNameMap.put(id, name);
	}
}
