package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.visitors.ValidatePreviewEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

public class ValidatePreviewEvent extends UploadEvent {
	private static final long serialVersionUID = -6126820534045806983L;

	private final String uuid;
	private final long index;

	public ValidatePreviewEvent(String uuid, long index, Long userId) {
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
			((ValidatePreviewEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
