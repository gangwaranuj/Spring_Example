package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.models.AssignmentsDTO;

public interface CsvAssignmentsService {
	void create(String uuid);
	void create(String uuid, long index);
	AssignmentsDTO get(String uuid);
	AssignmentsDTO get(String uuid, long page, long size);
}
