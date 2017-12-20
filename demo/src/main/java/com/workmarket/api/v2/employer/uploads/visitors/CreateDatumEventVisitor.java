package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.CreateDatumEvent;

public interface CreateDatumEventVisitor {
	void visit(CreateDatumEvent event);
}
