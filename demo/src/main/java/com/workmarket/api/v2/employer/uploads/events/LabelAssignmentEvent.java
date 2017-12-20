package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.visitors.LabelAssignmentEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

public class LabelAssignmentEvent extends UploadEvent {
	private static final long serialVersionUID = 1L;

	private final String uuid;
	private final long index;
	private final Long labelId;

	public LabelAssignmentEvent(String uuid, long index, Long labelId, Long userId) {
		super(userId);
		this.uuid = uuid;
		this.index = index;
		this.labelId = labelId;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public long getIndex() {
		return index;
	}

	public Long getLabelId() {
		return labelId;
	}

	@Override
	public void accept(Visitor visitor) {
		try {
			((LabelAssignmentEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
