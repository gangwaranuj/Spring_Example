package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.LabelAssignmentsEvent;

public interface LabelAssignmentsEventVisitor {
	void visit(LabelAssignmentsEvent event);
}
