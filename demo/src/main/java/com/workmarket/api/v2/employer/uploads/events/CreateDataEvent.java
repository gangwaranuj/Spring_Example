package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.visitors.CreateDataEventVisitor;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;

import java.util.List;

public class CreateDataEvent extends UploadEvent {
	private static final long serialVersionUID = 7993644556502646428L;

	private final String uuid;
	private final List<MappingDTO> headers;

	public CreateDataEvent(String uuid, List<MappingDTO> headers, Long userId) {
		super(userId);
		this.uuid = uuid;
		this.headers = headers;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public List<MappingDTO> getHeaders() {
		return headers;
	}

	@Override
	public void accept(Visitor visitor) {
		try {
			((CreateDataEventVisitor) visitor).visit(this);
		} catch (ClassCastException e) {
			// 	unimplemented visitor should be a no-op
		}
	}
}
