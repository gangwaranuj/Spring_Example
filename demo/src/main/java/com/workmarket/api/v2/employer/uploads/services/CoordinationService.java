package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.exceptions.ConflictException;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.models.StatusDTO;

import java.util.List;

public interface CoordinationService {
	void reset(String uuid);
	void createRows(String uuid) throws ConflictException;
	void createData(String uuid, List<MappingDTO> headers) throws ConflictException;
	void createDatum(String uuid, List<MappingDTO> headers, long index);
	void validateDatum(String uuid, long index);
	void createPreviews(String uuid) throws ConflictException;
	void createPreview(String uuid, long index);
	void validatePreview(String uuid, long index);
	void createAssignments(String uuid) throws ConflictException;
	void createAssignment(String uuid, long index);
	void labelAssignments(String uuid) throws ConflictException;
	void labelAssignment(String uuid, long index, Long labelId);
	StatusDTO getStatus(String uuid);
}
