package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.visitors.CreatePreviewsEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

public class CreatePreviewsEvent extends UploadEvent {
	private static final long serialVersionUID = 2103966839862774140L;

	private final String uuid;

	public CreatePreviewsEvent(String uuid, Long userId) {
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
			((CreatePreviewsEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
