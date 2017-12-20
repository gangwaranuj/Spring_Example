package com.workmarket.api.v2.employer.assignments.services;

import com.google.api.client.util.Lists;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.RecurrenceDTO;
import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.api.v2.employer.uploads.services.CsvAssignmentsService;
import com.workmarket.api.v2.employer.uploads.services.PreviewStorageService;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.recurrence.RecurrenceClient;
import com.workmarket.recurrence.common.RecurrenceType;
import com.workmarket.recurrence.gen.Messages;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.service.web.WebRequestContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecurrenceServiceTest {

	@Mock private PreviewStorageService previewStorageService;
	@Mock private CsvAssignmentsService csvAssignmentsService;
	@Mock private WorkSubStatusService workSubStatusService;
	@Mock private RecurrenceClient recurrenceClient;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@Mock private WorkService workService;
	@InjectMocks private RecurrenceServiceImpl recurrenceService;

	private List<String> datesList;
	private Company company;
	private String recurrenceUUID = UUID.randomUUID().toString();

	@Before
	public void init() {
		datesList = Lists.newArrayList();
		datesList.add("2017/04/14 09:30am");
		datesList.add("2017/05/14 09:30am");
		datesList.add("2017/06/14 09:30am");
		datesList.add("2017/07/14 09:30am");
		datesList.add("2017/08/14 09:30am");
		datesList.add("2017/09/14 09:30am");
		datesList.add("2017/10/14 09:30am");
		datesList.add("2017/11/14 09:30am");
		Messages.OccurrencesMessage occurences = Messages.OccurrencesMessage.newBuilder().addAllDates(datesList).build();

		when(recurrenceClient.getRecurrenceDates(anyString(), any(RequestContext.class))).thenReturn(Observable.just(occurences));
		when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
		when(webRequestContextProvider.getWebRequestContext(any(String.class), any(String.class))).thenCallRealMethod();

		Messages.RecurrenceMessage recurrenceMessage = Messages.RecurrenceMessage.newBuilder()
				.setType(RecurrenceType.MONTHLY.toString())
				.setEndDate("2017/04/14")
				.setUuid(recurrenceUUID)
				.setDescription("recurrence test").build();
		when(recurrenceClient.getRecurrence(
				anyString(), any(RequestContext.class))).thenReturn(Observable.just(recurrenceMessage));

		String companyUUID = UUID.randomUUID().toString();
		company = mock(Company.class);
		when(company.getUuid()).thenReturn(companyUUID);
		when(company.getId()).thenReturn(1L);

		WorkSubStatusType label = mock(WorkSubStatusType.class);
		when(label.getId()).thenReturn(1L);
		when(workSubStatusService.saveOrUpdateCustomWorkSubStatus(any(WorkSubStatusTypeDTO.class))).thenReturn(label);
	}

	@Test
	public void testSaveRecurrence_nullRecurrence() {
		AssignmentDTO assignmentDTO = new AssignmentDTO.Builder().build();
		recurrenceService.createRecurrence(assignmentDTO, company, 1L, "EST");
		verify(recurrenceClient, never()).createRecurrence(any(Messages.RecurrenceMessage.class), any(RequestContext.class));
		verify(workService, never()).saveWorkRecurrence(anyLong(), anyLong(), anyString());
	}

	/**
	 * first assignment has non-null recurrence.type
	 */
	@Test
	public void testSaveRecurrence_firstAssignment() {

		when(recurrenceClient.createRecurrence(any(Messages.RecurrenceMessage.class), any(RequestContext.class))).thenReturn(
				Observable.just(Messages.UuidMessage.newBuilder()
						.setUuid(recurrenceUUID)
						.build()));

		AssignmentDTO assignmentDTO = new AssignmentDTO.Builder()
				.setTitle("test recurrence")
				.setSchedule(new ScheduleDTO.Builder()
				.setFrom("04/14/2017 09:30AM"))
				.setRecurrence(new RecurrenceDTO.Builder()
						.setType(RecurrenceType.MONTHLY.toString())
						.setUuid(recurrenceUUID)
				.setDescription("recurrence description")).build();
		recurrenceService.createRecurrence(assignmentDTO, company, 1L, "EST");
		verify(previewStorageService, times(datesList.size())).add(eq(recurrenceUUID), any(PreviewDTO.class));
		verify(csvAssignmentsService).create(eq(recurrenceUUID));
		verify(workSubStatusService, times(2)).addSubStatus(anyLong(), anyLong(), anyString());
	}

	/**
	 * auto generated recurrence assignments have null recurrence.type
	 */
	@Test
	public void testSaveRecurrence_autoGeneratedAssignment() {

		String recurrenceUUID = UUID.randomUUID().toString();

		when(recurrenceClient.createRecurrence(any(Messages.RecurrenceMessage.class), any(RequestContext.class))).thenReturn(
				Observable.just(Messages.UuidMessage.newBuilder()
						.setUuid(recurrenceUUID)
						.build()));

		AssignmentDTO assignmentDTO = new AssignmentDTO.Builder()
				.setSchedule(new ScheduleDTO.Builder()
						.setFrom("04/14/2017 09:30AM"))
				.setRecurrence(new RecurrenceDTO.Builder()
						.setUuid(recurrenceUUID)).build();
		recurrenceService.createRecurrence(assignmentDTO, company, 1L, "EST");
		verify(previewStorageService, never()).add(anyString(), any(PreviewDTO.class));
		verify(csvAssignmentsService, never()).create(anyString());
	}

	@Test
	public void testSaveWorkRecurrence() {
		String recurrenceUUID = UUID.randomUUID().toString();
		Long recurrenceLabelId = 1L;
		recurrenceService.createRecurringAssignments(1L, new AssignmentDTO.Builder().build(), recurrenceUUID, recurrenceLabelId);
		verify(previewStorageService, times(datesList.size())).add(eq(recurrenceUUID), any(PreviewDTO.class));
		verify(csvAssignmentsService).create(eq(recurrenceUUID));
	}

	@Test
	public void testGetRecurrenceByUUID_withNullUUID() {
		assertNotNull(recurrenceService.getRecurrence((String) null));
	}

	@Test
	public void testGetRecurrenceByUUID() {
		RecurrenceDTO recurrence = recurrenceService.getRecurrence(recurrenceUUID);
		assertEquals(recurrenceUUID, recurrence.getUuid());
	}

	@Test
	public void testGetRecurrenceByWorkId() {
		when(workService.getRecurrenceUUID(anyLong())).thenReturn(recurrenceUUID);

		RecurrenceDTO recurrence = recurrenceService.getRecurrence(1L);
		assertEquals(recurrenceUUID, recurrence.getUuid());
	}

	@Test
	public void testGetRecurrenceByWorkId_withoutRecurrence() {
		when(workService.getRecurrenceUUID(anyLong())).thenReturn(null);
		RecurrenceDTO recurrence = recurrenceService.getRecurrence(1L);
		assertNotNull(recurrence);
		assertNull(recurrence.getUuid());
	}
}
