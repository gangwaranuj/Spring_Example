package com.workmarket.service.business.event.work;

import java.util.List;
import java.util.Map;

import com.workmarket.domains.model.changelog.PropertyChange;
import com.workmarket.domains.model.changelog.PropertyChangeType;
import com.workmarket.service.business.event.Event;

public class WorkUpdatedEvent extends Event {

	private static final long serialVersionUID = -680291160957452733L;

	private Long workId;
	private Map<PropertyChangeType, List<PropertyChange>> propertyChanges;

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public Map<PropertyChangeType, List<PropertyChange>> getPropertyChanges() {
		return propertyChanges;
	}

	public void setPropertyChanges(Map<PropertyChangeType, List<PropertyChange>> propertyChanges) {
		this.propertyChanges = propertyChanges;
	}
}
