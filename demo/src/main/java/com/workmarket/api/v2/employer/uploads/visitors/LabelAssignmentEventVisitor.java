package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.LabelAssignmentEvent;

public interface LabelAssignmentEventVisitor {
	void visit(LabelAssignmentEvent event);
}
