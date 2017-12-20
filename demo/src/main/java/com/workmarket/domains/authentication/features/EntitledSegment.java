package com.workmarket.domains.authentication.features;

import com.workmarket.utility.CollectionUtilities;

import java.util.Collection;

public class EntitledSegment<T> {
	Collection<T> values;

	public EntitledSegment(Collection<T> values) {
		this.values = values;
	}

	public boolean contains(T needle) {
		return CollectionUtilities.contains(values, needle);
	}

	public String toString() {
		StringBuffer ret = new StringBuffer();
		for (T value : values) {
			ret.append(value + ",");
		}
		ret.deleteCharAt(ret.length()-1);
		return ret.toString();
	}
}
