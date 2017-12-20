package com.workmarket.domains.model.changelog.work;

import com.workmarket.domains.model.changelog.PropertyChangeType;

public enum WorkPropertyChangeType implements PropertyChangeType {
	PRICING,
	SCHEDULE,
	LOCATION,
	INFO,
	CONTACT,
	OTHER
}