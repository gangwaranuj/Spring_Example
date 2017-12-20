package com.workmarket.service.business.upload.transactional;

import com.workmarket.service.business.upload.parser.WorkUploaderBuildData;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;

public interface WorkUploadBuilder {
	WorkUploaderBuildResponse buildFromRow(WorkUploaderBuildData buildData);
}
