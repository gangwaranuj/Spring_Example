package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.events.CreateAssignmentEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateAssignmentsEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateDataEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateDatumEvent;
import com.workmarket.api.v2.employer.uploads.events.CreatePreviewEvent;
import com.workmarket.api.v2.employer.uploads.events.CreatePreviewsEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateRowsEvent;
import com.workmarket.api.v2.employer.uploads.events.LabelAssignmentEvent;
import com.workmarket.api.v2.employer.uploads.events.LabelAssignmentsEvent;
import com.workmarket.api.v2.employer.uploads.events.UploadEvent;
import com.workmarket.api.v2.employer.uploads.events.ValidateDatumEvent;
import com.workmarket.api.v2.employer.uploads.events.ValidatePreviewEvent;
import com.workmarket.api.v2.employer.uploads.exceptions.ConflictException;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.models.StatusDTO;
import com.workmarket.api.v2.employer.uploads.visitors.CoordinationVisitor;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.infra.business.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoordinationServiceImpl implements CoordinationService {
	@Qualifier("uploadsEventService")
	@Autowired private EventService eventService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private PreviewStorageService previewStorageService;
	@Autowired private RedisAdapter redisAdapter;

	@Override
	public void reset(String uuid) {
		// TODO[Jim]: nuke pid too but only if we're finished with current processing?
		// step QUIT?
		previewStorageService.destroy(uuid);
	}

	@Override
	public void createRows(String uuid) throws ConflictException {
		eventService.emit(guard(new CreateRowsEvent(uuid, getUserId())));
	}

	@Override
	public void createData(String uuid, List<MappingDTO> headers) throws ConflictException {
		eventService.emit(guard(new CreateDataEvent(uuid, headers, getUserId())));
	}

	@Override
	public void createDatum(String uuid, List<MappingDTO> headers, long index) {
		eventService.emit(new CreateDatumEvent(uuid, headers, index, getUserId()));
	}

	@Override
	public void validateDatum(String uuid, long index) {
		eventService.emit(new ValidateDatumEvent(uuid, index, getUserId()));
	}

	@Override
	public void createPreviews(String uuid) throws ConflictException {
		eventService.emit(guard(new CreatePreviewsEvent(uuid, getUserId())));
	}

	@Override
	public void createPreview(String uuid, long index) {
		eventService.emit(new CreatePreviewEvent(uuid, index, getUserId()));
	}

	@Override
	public void validatePreview(String uuid, long index) {
		eventService.emit(new ValidatePreviewEvent(uuid, index, getUserId()));
	}

	@Override
	public void createAssignments(String uuid) throws ConflictException {
		eventService.emit(guard(new CreateAssignmentsEvent(uuid, getUserId())));
	}

	@Override
	public void createAssignment(String uuid, long index) {
		eventService.emit(new CreateAssignmentEvent(uuid, index, getUserId()));
	}

	@Override
	public void labelAssignments(String uuid) throws ConflictException {
		eventService.emit(guard(new LabelAssignmentsEvent(uuid, getUserId())));
	}

	@Override
	public void labelAssignment(String uuid, long index, Long labelId) {
		eventService.emit(new LabelAssignmentEvent(uuid, index, labelId, getUserId()));
	}

	@Override
	public StatusDTO getStatus(String uuid) {
		return new StatusDTO.Builder()
			.setStep(getStep(uuid))
			.setRemaining(getRemaining(uuid))
			.build();
	}

	private <T extends UploadEvent> T guard(T event) throws ConflictException {
		// TODO[Jim]: Move to the EventService to short-circuit emit?
		String step = getStep(event.getUuid());
		String remaining = getRemaining(event.getUuid());

		CoordinationVisitor visitor = new CoordinationVisitor(step, remaining);
		event.accept(visitor);

		if (visitor.isConflicting()) {
			throw new ConflictException(String.format("The %s process for %s is already running. %s items remain.", step, event.getUuid(), remaining));
		}

		return event;
	}

	private String getStep(String uuid) {
		return (String) redisAdapter.get("uploads:" + uuid + ":step").orNull();
	}

	private String getRemaining(String uuid) {
		return (String) redisAdapter.get("uploads:" + uuid + ":remaining").orNull();
	}

	private Long getUserId() {
		return authenticationService.getCurrentUser().getId();
	}
}
