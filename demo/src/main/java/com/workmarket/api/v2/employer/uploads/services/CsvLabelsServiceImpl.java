package com.workmarket.api.v2.employer.uploads.services;

import com.google.common.base.Optional;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.api.v2.employer.uploads.models.SettingsDTO;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.redis.RedisAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.workmarket.api.v2.employer.uploads.services.PreviewStorageServiceImpl.A_DAY_IN_SECONDS;

@Service
public class CsvLabelsServiceImpl implements CsvLabelsService {

	@Autowired private CoordinationService coordinationService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private PreviewStorageService previewStorageService;
	@Autowired private UploadSettingsService uploadSettingsService;
	@Autowired private WorkService workService;
	@Autowired private RedisAdapter redisAdapter;

	@Override
	public void label(String uuid) {
		long count = previewStorageService.size(uuid);
		redisAdapter.set("uploads:" + uuid + ":step", "labels", A_DAY_IN_SECONDS);
		redisAdapter.set("uploads:" + uuid + ":remaining", count, A_DAY_IN_SECONDS);

		Optional<SettingsDTO> optionalSettings = uploadSettingsService.get(uuid);
		if (optionalSettings.isPresent()) {
			Long labelId = optionalSettings.get().getLabelId();

			for (long index = 0; index < count; index++) {
				coordinationService.labelAssignment(uuid, index, labelId);
			}
		}
	}

	@Override
	public void label(String uuid, long index, Long labelId) {
		Optional<PreviewDTO> optionalPreview = previewStorageService.get(uuid, index);
		if (optionalPreview.isPresent()) {
			PreviewDTO preview = optionalPreview.get();
			AssignmentDTO assignment = preview.getAssignmentDTO();
			Long workId = workService.findWorkId(assignment.getId());

			if (labelId != null) {
				workSubStatusService.addSubStatus(workId, labelId, null);
			}
		}

		redisAdapter.decrement("uploads:" + uuid + ":remaining");
	}
}
