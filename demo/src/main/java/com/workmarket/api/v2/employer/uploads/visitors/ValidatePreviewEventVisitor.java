package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.ValidatePreviewEvent;

public interface ValidatePreviewEventVisitor {
	void visit(ValidatePreviewEvent event);
}
