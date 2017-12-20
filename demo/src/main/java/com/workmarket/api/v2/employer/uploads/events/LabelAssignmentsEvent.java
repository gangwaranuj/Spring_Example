package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.visitors.LabelAssignmentsEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

public class LabelAssignmentsEvent extends UploadEvent {
	private static final long serialVersionUID = -5025699425200349018L;

	private final String uuid;

	public LabelAssignmentsEvent(String uuid, Long userId) {
		super(userId);
		this.uuid = uuid;
	}

	@Override
	public String getUuid() {
		return this.uuid;
	}

	@Override
	public void accept(Visitor visitor) {
		try {
			((LabelAssignmentsEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
