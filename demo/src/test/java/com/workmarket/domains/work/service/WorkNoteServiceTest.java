package com.workmarket.domains.work.service;

import com.workmarket.dao.note.NoteDAO;
import com.workmarket.dao.note.NoteMetadataDAO;
import com.workmarket.dao.note.WorkNoteDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NoteMetadata;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkNoteServiceTest {

	@Mock AuthenticationService authenticationService;
	@Mock UserNotificationService userNotificationService;
	@Mock WorkAuditService workAuditService;
	@Mock WebHookEventService webHookEventService;
	@Mock WorkActionRequestFactory workActionRequestFactory;
	@Mock NoteDAO noteDAO;
	@Mock NoteMetadataDAO noteMetadataDAO;
	@Mock WorkNoteDAO workNoteDAO;
	@Mock WorkService workService;
	@Mock WorkChangeLogService workChangeLogService;
	@Mock FeatureEvaluator featureEvaluator;

	@InjectMocks WorkNoteServiceImpl workNoteService;

	Work work;
	NoteDTO noteDTO;
	WorkNote workNote;
	User user;
	Company company;
	WorkSubStatusType workSubStatusType;

	@Before
	public void setup() {
		noteDTO =  new NoteDTO();
		noteDTO.setContent("My note");

		work = mock(Work.class);
		workNote = mock(WorkNote.class);

		user = mock(User.class);
		company = mock(Company.class);

		workSubStatusType = mock(WorkSubStatusType.class);

		when(workService.findWork(anyLong())).thenReturn(work);
		when(workService.findWorkByWorkNumber(anyString())).thenReturn(work);
		when(work.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.ACTIVE));
		when(work.getBuyer()).thenReturn(new User());
		when(work.getCompany()).thenReturn(new Company());
		when(workNote.getId()).thenReturn(1L);
		when(workNote.getWork()).thenReturn(work);
		when(authenticationService.getCurrentUser()).thenReturn(user);
		when(user.getCompany()).thenReturn(company);
		when(user.getId()).thenReturn(2L);
		when(workNoteDAO.get(anyLong())).thenReturn(workNote);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNoteToWork_withNullWorkId_fail() throws Exception {
		workNoteService.addNoteToWork(null, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNoteToWork_withNullNotDTO_fail() throws Exception {
		workNoteService.addNoteToWork(1L, (NoteDTO) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNoteToWork_withNullContent_fail() throws Exception {
		workNoteService.addNoteToWork(1L, new NoteDTO(), null);
	}

	@Test
	public void addNoteToWork_savesNote() throws Exception {
		workNoteService.addNoteToWork(1L, noteDTO);
		verify(noteDAO, times(1)).saveOrUpdate(any(WorkNote.class));
	}

	@Test
	public void saveNoteMetadata_withNullArguments_doesNothing() throws Exception {
		workNoteService.saveNoteMetadata(null, null);
		verify(noteMetadataDAO, never()).saveOrUpdate(any(NoteMetadata.class));
	}

	@Test
	public void saveNoteMetadata_savesMetadata() throws Exception {
		workNoteService.saveNoteMetadata(workNote, noteDTO);
		verify(noteMetadataDAO, times(1)).saveOrUpdate(any(NoteMetadata.class));
	}

	@Test
	public void saveNoteMetadata_withParent_savesMetadata() throws Exception {
		noteDTO.setParentId(2L);
		workNoteService.saveNoteMetadata(workNote, noteDTO);
		verify(workNoteDAO, times(1)).get(eq(2L));
		verify(noteMetadataDAO, times(1)).saveOrUpdate(any(NoteMetadata.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveNoteMetadata_withParentIdSameAsNoteId_fails() throws Exception {
		noteDTO.setParentId(1L);
		workNoteService.saveNoteMetadata(workNote, noteDTO);
		verify(workNoteDAO, times(1)).get(eq(2L));
		verify(noteMetadataDAO, times(1)).saveOrUpdate(any(NoteMetadata.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findNoteById_withNullArguments_fails() throws Exception {
		workNoteService.findNoteById(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveOrUpdate_withNullArguments_fails() throws Exception {
		workNoteService.saveOrUpdate(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findAllNotesByWorkForCompany_withNullArguments_fails() throws Exception {
		workNoteService.findAllNotesByWorkForCompany(null, null, null);
	}

	@Test
	public void findAllNotesByWorkForCompany_success() throws Exception {
		workNoteService.findAllNotesByWorkForCompany(1L, 1L, new NotePagination());
		verify(noteDAO, times(1)).findAllNotesByWorkIdForCompany(eq(1L), eq(1L), any(NotePagination.class));
	}


	@Test
	public void findNoteById_success() throws Exception {
		workNoteService.findNoteById(1L);
		verify(noteDAO, times(1)).get(eq(1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveOrUpdate_withNullArguments() throws Exception {
		workNoteService.saveOrUpdate(null);
	}

	@Test
	public void saveOrUpdate_success() throws Exception {
		workNoteService.saveOrUpdate(workNote);
		verify(noteDAO, times(1)).saveOrUpdate(any(Note.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void addWorkSubStatusTransitionNote_withNullArguments_fail() throws Exception {
		workNoteService.addWorkSubStatusTransitionNote(null, null, null, "note");
	}

	@Test
	public void addWorkSubStatusTransitionNote_success() throws Exception {
		workNoteService.addWorkSubStatusTransitionNote(user, work, workSubStatusType, "note");
		verify(workNoteDAO,times(1)).saveOrUpdate(any(WorkNote.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void setNotePrivacy_withNullArguments_fail() throws Exception {
		workNoteService.setNotePrivacy(null, null, null, null);
	}

	@Test
	public void setNotePrivacy_setsDefaultAsPrivate_success() throws Exception {
		when(workService.isUserActiveResourceForWork(anyLong(), anyLong())).thenReturn(false);
		WorkNote workNote = workNoteService.setNotePrivacy(new WorkNote(), user, work, workSubStatusType);
		assertNotNull(workNote);
		assertTrue(workNote.getIsPrivate());
	}

	@Test
	public void setNotePrivacy_withActiveWorkerAddingNote_setsPrivileged() throws Exception {
		when(workService.isUserActiveResourceForWork(anyLong(), anyLong())).thenReturn(true);
		WorkNote workNote = workNoteService.setNotePrivacy(new WorkNote(), user, work, workSubStatusType);
		assertNotNull(workNote);
		assertTrue(workNote.getIsPrivileged());
		assertEquals(workNote.getReplyToId(), user.getId());
	}

	@Test
	public void setNotePrivacy_withNoActiveWorkerAddingNoteAndPrivateRequiredNote_success() throws Exception {
		when(workService.isUserActiveResourceForWork(anyLong(), anyLong())).thenReturn(false);
		when(workSubStatusType.isNoteRequired()).thenReturn(true);
		when(workSubStatusType.getNotePrivacy()).thenReturn(PrivacyType.PRIVATE);

		WorkNote workNote = workNoteService.setNotePrivacy(new WorkNote(), user, work, workSubStatusType);
		assertNotNull(workNote);
		assertTrue(workNote.getIsPrivate());
	}

	@Test
	public void setNotePrivacy_withNoActiveWorkerAddingNoteAndPrivilegedRequiredNote_success() throws Exception {
		when(workService.isUserActiveResourceForWork(anyLong(), anyLong())).thenReturn(false);
		when(workSubStatusType.isNoteRequired()).thenReturn(true);
		when(workSubStatusType.getNotePrivacy()).thenReturn(PrivacyType.PRIVILEGED);

		WorkNote workNote = workNoteService.setNotePrivacy(new WorkNote(), user, work, workSubStatusType);
		assertNotNull(workNote);
		assertTrue(workNote.getIsPrivileged());
		assertEquals(workNote.getReplyToId(), user.getId());
	}
}
