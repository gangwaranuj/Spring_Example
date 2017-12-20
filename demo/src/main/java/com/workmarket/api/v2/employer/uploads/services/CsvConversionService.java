package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.uploads.models.CellDTO;

import java.util.Map;

public interface CsvConversionService {
	AssignmentDTO convert(Map<String, CellDTO> rowData);
	AssignmentDTO convert(Map<String, CellDTO> rowData, AssignmentDTO templateAssignment);
}
