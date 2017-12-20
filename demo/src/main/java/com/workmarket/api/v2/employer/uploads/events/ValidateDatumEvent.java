package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.visitors.ValidateDatumEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

public class ValidateDatumEvent extends UploadEvent {
	private static final long serialVersionUID = 7993644556502646428L;

	private final String uuid;
	private final long index;

	public ValidateDatumEvent(String uuid, long index, Long userId) {
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
			((ValidateDatumEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
