package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.CreateAssignmentEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateAssignmentsEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateDataEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateDatumEvent;
import com.workmarket.api.v2.employer.uploads.events.CreatePreviewEvent;
import com.workmarket.api.v2.employer.uploads.events.CreatePreviewsEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateRowsEvent;
import com.workmarket.api.v2.employer.uploads.events.LabelAssignmentEvent;
import com.workmarket.api.v2.employer.uploads.events.LabelAssignmentsEvent;
import com.workmarket.api.v2.employer.uploads.events.ValidateDatumEvent;
import com.workmarket.api.v2.employer.uploads.events.ValidatePreviewEvent;
import com.workmarket.api.v2.employer.uploads.services.CsvAssignmentsService;
import com.workmarket.api.v2.employer.uploads.services.CsvDataService;
import com.workmarket.api.v2.employer.uploads.services.CsvLabelsService;
import com.workmarket.api.v2.employer.uploads.services.CsvPreviewsService;
import com.workmarket.api.v2.employer.uploads.services.CsvRowsService;
import com.workmarket.service.exception.HostServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ListenerVisitor implements Visitor,
	CreateRowsEventVisitor, CreateDatumEventVisitor, CreateDataEventVisitor,
	ValidateDatumEventVisitor, CreatePreviewEventVisitor, CreatePreviewsEventVisitor,
	ValidatePreviewEventVisitor, CreateAssignmentsEventVisitor, CreateAssignmentEventVisitor,
	LabelAssignmentEventVisitor, LabelAssignmentsEventVisitor {

	@Autowired private CsvRowsService csvRowsService;
	@Autowired private CsvDataService csvDataService;
	@Autowired private CsvPreviewsService csvPreviewsService;
	@Autowired private CsvAssignmentsService csvAssignmentsService;
	@Autowired private CsvLabelsService csvLabelsService;

	@Override
	public void visit(CreateRowsEvent event) {
		try {
			csvRowsService.create(
				event.getUuid()
			);
		} catch (HostServiceException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visit(CreateDatumEvent event) {
		try {
			csvDataService.create(
				event.getUuid(),
				event.getHeaders(),
				event.getIndex()
			);
		} catch (IOException | HostServiceException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visit(CreateDataEvent event) {
		try {
			csvDataService.create(
				event.getUuid(),
				event.getHeaders()
			);
		} catch (IOException | HostServiceException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visit(ValidateDatumEvent event) {
		csvDataService.validate(
			event.getUuid(),
			event.getIndex()
		);
	}

	@Override
	public void visit(CreatePreviewEvent event) {
		csvPreviewsService.create(
			event.getUuid(),
			event.getIndex()
		);
	}

	@Override
	public void visit(CreatePreviewsEvent event) {
		csvPreviewsService.create(
			event.getUuid()
		);
	}

	@Override
	public void visit(ValidatePreviewEvent event) {
		csvPreviewsService.validate(
			event.getUuid(),
			event.getIndex()
		);
	}

	@Override
	public void visit(CreateAssignmentEvent event) {
		csvAssignmentsService.create(
			event.getUuid(),
			event.getIndex()
		);
	}

	@Override
	public void visit(CreateAssignmentsEvent event) {
		csvAssignmentsService.create(
			event.getUuid()
		);
	}

	@Override
	public void visit(LabelAssignmentEvent event) {
		csvLabelsService.label(
			event.getUuid(),
			event.getIndex(),
			event.getLabelId()
		);
	}

	@Override
	public void visit(LabelAssignmentsEvent event) {
		csvLabelsService.label(
			event.getUuid()
		);
	}
}
