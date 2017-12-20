package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.CreateAssignmentEvent;

public interface CreateAssignmentEventVisitor {
	void visit(CreateAssignmentEvent event);
}
