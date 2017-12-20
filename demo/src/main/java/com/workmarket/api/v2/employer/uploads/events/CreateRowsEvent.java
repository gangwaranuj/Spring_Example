package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.visitors.CreateRowsEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

public class CreateRowsEvent extends UploadEvent {
	private static final long serialVersionUID = -3148179289955667629L;

	private final String uuid;

	public CreateRowsEvent(String uuid, Long userId) {
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
			((CreateRowsEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
