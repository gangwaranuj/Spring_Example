package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.uploads.models.CellDTO;

import java.util.List;
import java.util.Map;

public interface CsvValidationService {
	List<ApiBaseError> validate(Map<String, CellDTO> rowData, long rowNumber);
}
