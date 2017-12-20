package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.CreateRowsEvent;

public interface CreateRowsEventVisitor {
	void visit(CreateRowsEvent event);
}
