package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.CreatePreviewEvent;

public interface CreatePreviewEventVisitor {
	void visit(CreatePreviewEvent event);
}
