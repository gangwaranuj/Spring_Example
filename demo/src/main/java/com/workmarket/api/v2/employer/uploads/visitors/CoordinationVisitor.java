package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.CreateAssignmentsEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateDataEvent;
import com.workmarket.api.v2.employer.uploads.events.CreatePreviewsEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateRowsEvent;
import com.workmarket.api.v2.employer.uploads.events.LabelAssignmentsEvent;

public class CoordinationVisitor implements Visitor, CreateRowsEventVisitor,
	CreateDataEventVisitor, CreatePreviewsEventVisitor, CreateAssignmentsEventVisitor,
	LabelAssignmentsEventVisitor {

	private final String step;
	private final String remaining;
	private boolean conflicting;

	public CoordinationVisitor(String step, String remaining) {
		this.step = step;
		this.remaining = remaining;
	}

	@Override
	public void visit(CreateRowsEvent event) {
		this.conflicting = !(step == null && remaining == null || "rows".equals(step) && "0".equals(remaining));
	}

	@Override
	public void visit(CreateDataEvent event) {
		this.conflicting = !("0".equals(remaining) && ("data".equals(step) || "rows".equals(step)));
	}

	@Override
	public void visit(CreatePreviewsEvent event) {
		this.conflicting = !("0".equals(remaining) && ("previews".equals(step) || "data".equals(step)));
	}

	@Override
	public void visit(CreateAssignmentsEvent event) {
		this.conflicting = !("0".equals(remaining) && "previews".equals(step));
	}

	@Override
	public void visit(LabelAssignmentsEvent event) {
		this.conflicting = !("0".equals(remaining) && "assignments".equals(step));
	}

	public boolean isConflicting() {
		return conflicting;
	}
}
