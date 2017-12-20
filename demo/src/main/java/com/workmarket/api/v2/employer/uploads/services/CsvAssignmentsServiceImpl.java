package com.workmarket.api.v2.employer.uploads.services;

import com.google.common.base.Optional;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentService;
import com.workmarket.api.v2.employer.uploads.models.AssignmentsDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import static com.workmarket.api.v2.employer.uploads.services.PreviewStorageServiceImpl.A_DAY_IN_SECONDS;

@Service
public class CsvAssignmentsServiceImpl implements CsvAssignmentsService {
	@Autowired private CoordinationService coordinationService;
	@Autowired private PreviewStorageService previewStorageService;
	@Autowired private AssignmentService assignmentService;
	@Autowired private MessageSource messageSource;
	@Autowired private RedisAdapter redisAdapter;

	@Override
	public void create(String uuid) {
		long count = previewStorageService.size(uuid);
		redisAdapter.set("uploads:" + uuid + ":step", "assignments", A_DAY_IN_SECONDS);
		redisAdapter.set("uploads:" + uuid + ":remaining", count, A_DAY_IN_SECONDS);

		for (long index = 0; index < count; index++) {
			coordinationService.createAssignment(uuid, index);
		}
	}

	@Override
	public void create(String uuid, long index) {
		Optional<PreviewDTO> optional = previewStorageService.get(uuid, index);
		if (optional.isPresent()) {
			PreviewDTO previewDTO = optional.get();
			PreviewDTO.Builder builder = new PreviewDTO.Builder(previewDTO);

			try {
				builder.setAssignmentDTO(
					new AssignmentDTO.Builder(
						assignmentService.create(previewDTO.getAssignmentDTO(), false)
					)
				);
			} catch (ValidationException e) {
				BindingResult bindingResult = ThriftValidationMessageHelper.buildBindingResult(e);

				for (ObjectError error : bindingResult.getAllErrors()) {
					ApiBaseError validationError;

					if (error instanceof FieldError) {
						FieldError fieldError = (FieldError) error;
						validationError = new ApiBaseError(error.getCode(), messageSource.getMessage(error, null), fieldError.getField(), fieldError.getObjectName());
					}
					else {
						validationError = new ApiBaseError(error.getCode(), messageSource.getMessage(error, null));
					}

					builder.addValidationError(validationError);
				}
			} catch (WorkAuthorizationException e) {
					ApiBaseError validationError = new ApiBaseError(e.getMessage(), null);
					builder.addValidationError(validationError);
			}

			previewStorageService.set(uuid, index, builder.build());
			redisAdapter.decrement("uploads:" + uuid + ":remaining");
		}
	}

	@Override
	public AssignmentsDTO get(String uuid) {
		return get(uuid, 1L, 10L);
	}

	@Override
	public AssignmentsDTO get(String uuid, long page, long size) {
		long start = (page - 1) * size;
		long end = start + size - 1;

		AssignmentsDTO.Builder builder = new AssignmentsDTO.Builder()
			.setUuid(uuid)
			.setCount(previewStorageService.size(uuid));

		for (PreviewDTO previewDTO : previewStorageService.get(uuid, start, end)) {
			builder.addAssignment(new AssignmentDTO.Builder(previewDTO.getAssignmentDTO()));
		}

		return builder.build();
	}
}
