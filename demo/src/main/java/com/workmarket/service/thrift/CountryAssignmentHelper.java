package com.workmarket.service.thrift;

import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;

public interface CountryAssignmentHelper {

	public String getCountryForAssignments(WorkUploadColumn type, WorkUploaderBuildResponse response, String country);

}
