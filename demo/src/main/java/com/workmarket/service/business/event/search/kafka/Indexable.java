package com.workmarket.service.business.event.search.kafka;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Value object for indexable object.
 */
public class Indexable {

	private Map<String, Object> other = new HashMap<>();

	/**
	 * Sets all other key-value pairs.
	 *
	 * @param name
	 * @param value
	 */
	@JsonAnySetter
	private void set(String name, Object value) {
		other.put(name, value);
	}

	/**
	 * Gets fieldValue from fieldName.
	 *
	 * @param fieldName The fieldName to get.
	 * @return fieldValue The value object.
	 */
	public Object getFieldValue(final String fieldName) {
		return other.get(fieldName);
	}

	/**
	 * Gets all field names of the instance.
	 *
	 * @return set of field names.
	 */
	public Set<String> getFieldNames() {
		return other.keySet();
	}

	/**
	 * Checks if a field exists in the object.
	 *
	 * @param fieldName The field name to check.
	 * @return boolean flag to indicate existence.
	 */
	public boolean hasFieldName(final String fieldName) {
		return other.containsKey(fieldName);
	}
}