package com.workmarket.domains.model;

import com.google.common.collect.Maps;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

public class AbstractEntityUtilities {
	public static <T extends AbstractEntity> Map<Long, T> newEntityIdMap(List<T> list) {
		Assert.notNull(list);

		if (list.size() == 0)
			return Maps.newHashMap();

		Map<Long, T> map = Maps.newHashMap();

		for (T entity : list) {
			if (entity != null)
				map.put(entity.getId(), entity);
		}

		return map;
	}
}
