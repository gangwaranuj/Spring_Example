package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.CreateDataEvent;

public interface CreateDataEventVisitor {
	void visit(CreateDataEvent event);
}
