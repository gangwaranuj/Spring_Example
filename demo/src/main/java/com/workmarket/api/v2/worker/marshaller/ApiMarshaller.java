package com.workmarket.api.v2.worker.marshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Map;

public class ApiMarshaller {

	protected void putNonEmptyIntoMap(Map map, Object key, Object value) {
		if (value instanceof String) {
			if (StringUtils.isNotBlank((String) value)) {
				map.put(key, value);
			}
			return;
		}
		if (value instanceof Collection) {
			if (CollectionUtils.isNotEmpty((Collection) value)) {
				map.put(key, value);
			}
			return;
		}
		if (value instanceof Map) {
			if (value != null && ((Map) value).size() > 0) {
				map.put(key, value);
			}
			return;
		}
		if (value != null) {
			map.put(key, value);
		}
	}

	protected void putNonZeroIntoMap(Map map, Object key, Number value) {
		if (value == null) {
			return;
		}
		if (value instanceof Double || value instanceof Float) {
			if (value.doubleValue() > 0) {
				map.put(key, value);
			}
		} else {
			if (value.longValue() > 0) {
				map.put(key, value);
			}
		}
	}
}
