package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.visitors.CreatePreviewEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

public class CreatePreviewEvent extends UploadEvent {
	private static final long serialVersionUID = 2103966839862774140L;

	private final String uuid;
	private final long index;

	public CreatePreviewEvent(String uuid, long index, Long userId) {
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
			((CreatePreviewEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
