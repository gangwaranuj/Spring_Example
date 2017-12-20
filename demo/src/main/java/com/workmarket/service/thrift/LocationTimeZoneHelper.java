package com.workmarket.service.thrift;

import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;

import java.util.Map;

public interface LocationTimeZoneHelper {
	public void setLocationAndTimeZone(WorkUploaderBuildResponse workUploaderBuildResponse, Map<String, String> uploadColumns, int lineNum);

	public void setLocationAndTimeZoneByLocationId(WorkUploaderBuildResponse workUploaderBuildResponse, ClientLocation location, String id);

}
