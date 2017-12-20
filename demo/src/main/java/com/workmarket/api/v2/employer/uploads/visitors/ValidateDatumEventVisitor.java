package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.ValidateDatumEvent;

public interface ValidateDatumEventVisitor {
	void visit(ValidateDatumEvent event);
}
