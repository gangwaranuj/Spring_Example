package com.workmarket.api.v2.employer.uploads.services;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentTemplateService;
import com.workmarket.api.v2.employer.uploads.models.ErrorsDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewsDTO;
import com.workmarket.api.v2.employer.uploads.models.SettingsDTO;
import com.workmarket.api.ApiBaseError;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.workmarket.api.v2.employer.uploads.services.PreviewStorageServiceImpl.A_DAY_IN_SECONDS;

@Service
public class CsvPreviewsServiceImpl implements CsvPreviewsService {
	@Autowired private CoordinationService coordinationService;
	@Autowired private PreviewStorageService previewStorageService;
	@Autowired private CsvConversionService csvConversionService;
	@Autowired private AssignmentService assignmentService;
	@Autowired private AssignmentTemplateService assignmentTemplateService;
	@Autowired private UploadSettingsService uploadSettingsService;
	@Autowired private RedisAdapter redisAdapter;

	@Override
	public void create(String uuid) {
		long count = previewStorageService.size(uuid);
		redisAdapter.set("uploads:" + uuid + ":step", "previews", A_DAY_IN_SECONDS);
		redisAdapter.set("uploads:" + uuid + ":remaining", count, A_DAY_IN_SECONDS);

		for (long index = 0; index < count; index++) {
			coordinationService.createPreview(uuid, index);
		}
	}

	@Override
	public void create(String uuid, long index) {
		Optional<PreviewDTO> optionalPreview = previewStorageService.get(uuid, index);
		if (optionalPreview.isPresent()) {
			PreviewDTO previewDTO = optionalPreview.get();
			Optional<SettingsDTO> optionalSettings = uploadSettingsService.get(uuid);

			TemplateDTO template = null;
			if (optionalSettings.isPresent() && optionalSettings.get().getTemplateId() != null) {
				try {
					template = assignmentTemplateService.get(optionalSettings.get().getTemplateId());
				} catch (WorkActionException e) {
					List<ApiBaseError> errors = Lists.newArrayList();
					errors.add(new ApiBaseError("csv.preview.templateNotFound","Template could not be found", "template"));
					previewStorageService.set(uuid, index, new PreviewDTO.Builder(previewDTO)
						.setValidationErrors(errors)
						.build()
					);
					redisAdapter.decrement("uploads:" + uuid + ":remaining");
					return;
				}
			}

			AssignmentDTO assignmentDTO;
			if (template != null) {
				AssignmentDTO templateAssignment = template.getAssignment();
				assignmentDTO = csvConversionService.convert(previewDTO.getRowData(), templateAssignment);
			} else {
				assignmentDTO = csvConversionService.convert(previewDTO.getRowData());
			}

			previewStorageService.set(uuid, index, new PreviewDTO.Builder(previewDTO)
				.setAssignmentDTO(new AssignmentDTO.Builder(assignmentDTO))
				.build()
			);

			coordinationService.validatePreview(uuid, index);
		}
	}

	@Override
	public void validate(String uuid, long index) {
		Optional<PreviewDTO> optional = previewStorageService.get(uuid, index);

		if (optional.isPresent()) {
			PreviewDTO previewDTO = optional.get();
			List<ApiBaseError> errors = assignmentService.validate(previewDTO.getAssignmentDTO(), false);

			PreviewDTO updatedPreviewDTO = new PreviewDTO.Builder(previewDTO)
				.setValidationErrors(errors)
				.build();

			previewStorageService.set(uuid, index, updatedPreviewDTO);
			redisAdapter.decrement("uploads:" + uuid + ":remaining");
		}
	}

	@Override
	public PreviewsDTO get(String uuid) {
		return get(uuid, 1L, 10L);
	}

	@Override
	public PreviewsDTO get(String uuid, long page, long size) {
		long start = (page - 1) * size;
		long end = start + size - 1;

		PreviewsDTO.Builder builder = new PreviewsDTO.Builder()
			.setUuid(uuid)
			.setCount(previewStorageService.size(uuid));

		for (PreviewDTO previewDTO : previewStorageService.get(uuid, start, end)) {
			builder.addPreview(new PreviewDTO.Builder(previewDTO));
		}

		return builder.build();
	}

	@Override
	public ErrorsDTO getValidationErrors(String uuid) {
		return getValidationErrors(uuid, 1L, 10L);
	}

	@Override
	public ErrorsDTO getValidationErrors(String uuid, long page, long size) {
		long start = (page - 1) * size;
		long end = start + size - 1;

		long errorCount = 0L;

		ErrorsDTO.Builder builder = new ErrorsDTO.Builder()
			.setUuid(uuid);

		for (PreviewDTO previewDTO : previewStorageService.get(uuid, start, end)) {
			List<ApiBaseError> errors = previewDTO.getValidationErrors();
			if (!errors.isEmpty()) {
				builder.addErrors(errors);
				errorCount++;
			}
		}

		return builder
			.setCount(errorCount)
			.build();
	}
}
