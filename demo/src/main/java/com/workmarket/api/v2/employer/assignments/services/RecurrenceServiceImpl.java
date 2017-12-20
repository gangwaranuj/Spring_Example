package com.workmarket.api.v2.employer.assignments.services;

import com.google.api.client.util.Lists;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.RecurrenceDTO;
import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.api.v2.employer.uploads.services.CsvAssignmentsService;
import com.workmarket.api.v2.employer.uploads.services.PreviewStorageService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.recurrence.RecurrenceClient;
import com.workmarket.recurrence.common.DateHelper;
import com.workmarket.recurrence.common.RecurrenceType;
import com.workmarket.recurrence.gen.Messages.OccurrencesMessage;
import com.workmarket.recurrence.gen.Messages.RecurrenceMessage;
import com.workmarket.recurrence.gen.Messages.UuidMessage;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.DateUtilities;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RecurrenceServiceImpl implements RecurrenceService {

	@Autowired private PreviewStorageService previewStorageService;
	@Autowired private CsvAssignmentsService csvAssignmentsService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private RecurrenceClient recurrenceClient;
	@Autowired private WorkService workService;
	@Autowired private WorkSubStatusService workSubStatusService;

	@Override
	public RecurrenceDTO.Builder createRecurrence(AssignmentDTO assignmentDTO, Company company, Long workId, String timeZone) {

		RecurrenceDTO.Builder recurrenceBuilder = new RecurrenceDTO.Builder();

		// if this assignment was created with a recurrence, save the recurrence and create the recurring assignments
		if (assignmentDTO.getRecurrence() != null && StringUtils.isNotEmpty(assignmentDTO.getRecurrence().getType())) {

			Date startDate = DateHelper.parseDate(assignmentDTO.getSchedule().getFrom());
			Calendar calendar = DateUtilities.getCalendarNow();
			calendar.setTime(startDate);

			// Build recurrence message and send to microservice
			RecurrenceMessage message = convert(
				assignmentDTO.getRecurrence(),
				company.getUuid(),
				assignmentDTO.getSchedule().getFrom());
			UuidMessage uuidMessage = recurrenceClient.createRecurrence(
				message,
				webRequestContextProvider.getRequestContext()
			).toBlocking().single();

			WorkSubStatusType recurrenceLabel = createRecurrenceLabel(company.getId(), assignmentDTO, timeZone);
			workSubStatusService.addSubStatus(workId, recurrenceLabel.getId(), "Adding recurrence");

			recurrenceBuilder.setUuid(uuidMessage.getUuid())
				.setRecurringAssignmentId(workId)
				.setLabelId(recurrenceLabel.getId());

			// Create draft assignments for recurrence schedule
			createRecurringAssignments(workId, assignmentDTO, uuidMessage.getUuid(), recurrenceLabel.getId());
			saveWorkRecurrence(workId, workId, uuidMessage.getUuid());
		}

		// if this assignment belongs to a recurrence, create the work -> recurrence association
		if (StringUtils.isNotEmpty(assignmentDTO.getRecurrence().getUuid())) {
			saveWorkRecurrence(
				workId,
				assignmentDTO.getRecurrence().getRecurringAssignmentId(),
				assignmentDTO.getRecurrence().getUuid()
			);
			workSubStatusService.addSubStatus(
				workId,
				assignmentDTO.getRecurrence().getLabelId(),
				"Adding recurrence"
			);
		}

		return recurrenceBuilder;
	}

	/**
	 * Create assignments based on recurrence schedule with given recurrenceUUID.
	 * Assignments are created asynchronously using the previewStorageService
	 * Recurrence object is set on new assignments with uuid and recurring assignment populated prior to saving
	 * new assignments
	 *
	 * @param firstOccurrenceWorkId
	 * @param firstOccurrence
	 * @param recurrenceUUID
	 */
	@Override
	public void createRecurringAssignments(
		Long firstOccurrenceWorkId,
		AssignmentDTO firstOccurrence,
		String recurrenceUUID,
		Long recurrenceLabelId) {

		OccurrencesMessage occurences = recurrenceClient.getRecurrenceDates(
			recurrenceUUID,
			webRequestContextProvider.getRequestContext()
		).toBlocking().first();

		for (String date : occurences.getDatesList()) {
			AssignmentDTO.Builder assignmentOccurrence = new AssignmentDTO.Builder(firstOccurrence)
				.setSchedule(new ScheduleDTO.Builder(firstOccurrence.getSchedule())
					.setFrom(date))
				.setRecurrence(new RecurrenceDTO.Builder()
					.setUuid(recurrenceUUID)
					.setRecurringAssignmentId(firstOccurrenceWorkId)
					.setLabelId(recurrenceLabelId));

			previewStorageService.add(recurrenceUUID, new PreviewDTO.Builder()
				.setAssignmentDTO(assignmentOccurrence)
				.build()
			);
		}
		csvAssignmentsService.create(recurrenceUUID);
	}

	@Override
	public String getRecurrenceUUID(Long workId) {
		return workService.getRecurrenceUUID(workId);
	}

	@Override
	public void saveWorkRecurrence(Long workId, Long recurringWorkId, String recurrenceUUID) {
		workService.saveWorkRecurrence(workId, recurringWorkId, recurrenceUUID);
	}

	@Override
	public RecurrenceDTO getRecurrence(Long workId) {
		String recurrenceUUID = workService.getRecurrenceUUID(workId);
		return getRecurrence(recurrenceUUID);
	}

	/**
	 * Fetch the recurrence associated with the given recurrenceUUID
	 *
	 * @param recurrenceUUID
	 * @return
	 */
	@Override
	public RecurrenceDTO getRecurrence(String recurrenceUUID) {
		if (recurrenceUUID != null) {
			RecurrenceMessage recurrenceMessage = recurrenceClient.getRecurrence(recurrenceUUID,
				webRequestContextProvider.getRequestContext()).toBlocking().first();
			return convert(recurrenceUUID, recurrenceMessage);
		}
		return new RecurrenceDTO.Builder().build();
	}

	/**
	 * Convert RecurrenceDTO to RecurrenceMessage
	 */
	private RecurrenceMessage convert(RecurrenceDTO recurrenceDTO, String companyUUID, String assignmentStartDate) {
		Date startDate = DateHelper.parseDate(assignmentStartDate);
		Calendar calendar = DateUtilities.getCalendarNow();
		calendar.setTime(startDate);

		RecurrenceMessage.Builder recurrenceMessageBuilder = RecurrenceMessage.newBuilder()
			.setStartedOn(assignmentStartDate)
			.setCompanyUuid(companyUUID)
			.setType(recurrenceDTO.getType())
			.setHour(calendar.get(Calendar.HOUR))
			.setMinute(calendar.get(Calendar.MINUTE))
			.setRepetitions(recurrenceDTO.getRepetitions())
			.setFrequencyModifier(recurrenceDTO.getFrequencyModifier())
			.setDescription(recurrenceDTO.getDescription());

		if (RecurrenceType.WEEKLY.toString().equals(recurrenceDTO.getType())) {
			List<Integer> weekdayList = Lists.newArrayList();
			if (recurrenceDTO.getWeekdays() != null) {
				for (int i = 0; i < recurrenceDTO.getWeekdays().size(); i++) {
					if (recurrenceDTO.getWeekdays().get(i)) {
						weekdayList.add(i);
					}
				}
			}
			recurrenceMessageBuilder.addAllWeekdays(weekdayList);
		} else if (RecurrenceType.MONTHLY.toString().equals(recurrenceDTO.getType())) {
			recurrenceMessageBuilder.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
		}
		if (recurrenceDTO.getEndDate() != null) {
			recurrenceMessageBuilder.setEndDate(recurrenceDTO.getEndDate() + " 11:59pm");
		}
		return recurrenceMessageBuilder.build();
	}

	/**
	 * Convert RecurrenceMessage to RecurrenceDTO
	 */
	private RecurrenceDTO convert(String recurrenceUUID, RecurrenceMessage recurrenceMessage) {
		List<Integer> weekdayList = recurrenceMessage.getWeekdaysList();

		Boolean[] weekdayFlags = {false, false, false, false, false, false, false};
		for (Integer weekdayIndex : weekdayList) {
			weekdayFlags[weekdayIndex] = true;
		}
		return new RecurrenceDTO.Builder()
			.setUuid(recurrenceUUID)
			.setEndDate(recurrenceMessage.getEndDate())
			.setType(recurrenceMessage.getType())
			.setRepetitions(recurrenceMessage.getRepetitions())
			.setWeekdays(Arrays.asList(weekdayFlags))
			.setFrequencyModifier(recurrenceMessage.getFrequencyModifier())
			.setDescription(recurrenceMessage.getDescription())
			.build();
	}

	private WorkSubStatusType createRecurrenceLabel(Long companyId, AssignmentDTO assignmentDTO, String timeZone) {
		WorkSubStatusTypeDTO dto = new WorkSubStatusTypeDTO();
		dto.setWorkStatusCodes(ArrayUtils.EMPTY_STRING_ARRAY);
		dto.setNotifyClientEnabled(false);
		dto.setCompanyId(companyId);

		String label = String.format("%s - %s",
			StringUtils.left(assignmentDTO.getTitle(), 18),
			DateUtilities.format("MM/dd/yy HH:mm", DateUtilities.getCalendarNow(), timeZone));
		dto.setCode(label);
		dto.setDescription(label);
		return workSubStatusService.saveOrUpdateCustomWorkSubStatus(dto);
	}
}
