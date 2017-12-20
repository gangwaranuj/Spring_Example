package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.visitors.CreateAssignmentEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

public class CreateAssignmentEvent extends UploadEvent {
	private static final long serialVersionUID = 4361373922731123589L;

	private final String uuid;
	private long index;

	public CreateAssignmentEvent(String uuid, long index, Long userId) {
		super(userId);
		this.uuid = uuid;
		this.index = index;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public long getIndex() {
		return index;
	}

	@Override
	public void accept(Visitor visitor) {
		try {
			((CreateAssignmentEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
