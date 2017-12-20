package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.CreateAssignmentsEvent;

public interface CreateAssignmentsEventVisitor {
	void visit(CreateAssignmentsEvent event);
}
