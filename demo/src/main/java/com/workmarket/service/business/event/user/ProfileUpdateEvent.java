package com.workmarket.service.business.event.user;

import com.workmarket.service.business.event.Event;
import java.util.Map;

public class ProfileUpdateEvent extends Event {
	private static final long serialVersionUID = 8354830339488689936L;

	Long userId;
	Map<String, Object> properties;

	public ProfileUpdateEvent() {}

	public ProfileUpdateEvent(Long userId, Map<String, Object> properties) {
		this.userId = userId;
		this.properties = properties;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
}
