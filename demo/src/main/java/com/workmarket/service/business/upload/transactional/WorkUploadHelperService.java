package com.workmarket.service.business.upload.transactional;

import com.google.common.collect.Multimap;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.service.business.upload.parser.WorkUploadLocation;
import com.workmarket.service.business.upload.parser.WorkUploadLocationContact;

import java.util.List;

public interface WorkUploadHelperService {

	public void saveUpload(List<WorkSaveRequest> requests,
		Multimap<WorkUploadLocation, Integer> newLocations,
		Multimap<WorkUploadLocationContact, Integer> newContacts,
		Long companyId
	) throws ValidationException;
}
