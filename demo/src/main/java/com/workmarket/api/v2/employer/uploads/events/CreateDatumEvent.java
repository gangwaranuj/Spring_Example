package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.visitors.CreateDatumEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

import java.util.List;

public class CreateDatumEvent extends UploadEvent {
	private static final long serialVersionUID = 7993644556502646428L;

	private final String uuid;
	private final List<MappingDTO> headers;
	private final long index;

	public CreateDatumEvent(String uuid, List<MappingDTO> headers, long index, Long userId) {
		super(userId);
		this.uuid = uuid;
		this.headers = headers;
		this.index = index;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public List<MappingDTO> getHeaders() {
		return headers;
	}

	public long getIndex() {
		return index;
	}

	@Override
	public void accept(Visitor visitor) {
		try {
			((CreateDatumEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
