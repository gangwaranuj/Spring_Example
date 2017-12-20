package com.workmarket.service.business;

import com.workmarket.service.business.dto.UserImportDTO;
import com.workmarket.service.business.upload.users.model.BulkUserUploadRequest;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;

import java.util.List;

public interface UserBulkUploadService {
	void start(final BulkUserUploadRequest request, final BulkUserUploadResponse response, final boolean orgEnabledForUser);
	void upload(final Long userId, final String uuid, final UserImportDTO userImportDTO, final List<String> orgUnitPaths);
	void finish(BulkUserUploadResponse response);
}
