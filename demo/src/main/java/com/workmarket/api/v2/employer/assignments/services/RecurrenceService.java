package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.RecurrenceDTO;
import com.workmarket.domains.model.Company;

public interface RecurrenceService {

	RecurrenceDTO.Builder createRecurrence(AssignmentDTO assignmentDTO, Company company, Long workId, String timeZone);

	void createRecurringAssignments(
		Long firstOccurrenceWorkId,
		AssignmentDTO firstOccurrence,
		String recurrenceUUID,
		Long recurrenceLabelId);

	String getRecurrenceUUID(Long workId);

	void saveWorkRecurrence(Long workId, Long recurringWorkId, String recurrenceUUID);

	RecurrenceDTO getRecurrence(Long workId);

	RecurrenceDTO getRecurrence(String recurrenceUUID);
}
