package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.CreatePreviewsEvent;

public interface CreatePreviewsEventVisitor {
	void visit(CreatePreviewsEvent event);
}
