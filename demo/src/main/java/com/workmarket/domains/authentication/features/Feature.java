package com.workmarket.domains.authentication.features;

import java.util.HashMap;
import java.util.Set;

public class Feature {
	private String key;
	private boolean enabled;
	private HashMap<String, EntitledSegment<?>> entitledSegments = new HashMap<>();

	public Feature(String key) {
		this.key = key;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public EntitledSegment<?> getEntitledSegment(String segmentKey) {
		return entitledSegments.get(segmentKey);
	}

	public void addSegment(String segmentKey, EntitledSegment<?> segment) {
		entitledSegments.put(segmentKey, segment);
	}

	public Set<String> getEntitledSegmentKeys() {
		return entitledSegments.keySet();
	}

	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("Feature: \"" + key + "\"" + (enabled?" is ENABLED":" is NOT ENABLED.") + "\n");
		for (String entitledKey : entitledSegments.keySet()) {
			EntitledSegment<?> entitledSegment = entitledSegments.get(entitledKey);
			ret.append("segment \"" + entitledKey + "\": " + entitledSegment.toString() + "\n");
		}
		ret.append("\n");
		return ret.toString();
	}

	@SuppressWarnings("unchecked")
	public <T> boolean isEnabledFor(String segmentKey, T value) {
		EntitledSegment<T> segment = (EntitledSegment<T>)entitledSegments.get(segmentKey);
		return isEnabledFor(segment, value);
	}

	private <T> boolean isEnabledFor(EntitledSegment<T> segment, T value) {
		if (segment != null)
			return segment.contains(value);
		return false;
	}
}
