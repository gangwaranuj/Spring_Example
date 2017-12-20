package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.visitors.CreateAssignmentsEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

public class CreateAssignmentsEvent extends UploadEvent {
	private static final long serialVersionUID = 7620947159404283520L;

	private final String uuid;

	public CreateAssignmentsEvent(String uuid, Long userId) {
		super(userId);
		this.uuid = uuid;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public void accept(Visitor visitor) {
		try {
			((CreateAssignmentsEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
