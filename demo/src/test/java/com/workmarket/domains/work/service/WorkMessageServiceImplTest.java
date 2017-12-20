package com.workmarket.domains.work.service;

import com.workmarket.dao.note.NoteDAO;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.WorkMessagePagination;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.utility.CollectionUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkMessageServiceImplTest {

	@Mock AuthenticationService authenticationService;
	@Mock WorkService workService;
	@Mock NoteDAO noteDAO;
	@InjectMocks WorkMessageServiceImpl workMessageService;

	Work work;
	List<WorkContext> context;

	@Before
	public void setup() {
		work = mock(Work.class);
		context = mock(List.class);
		when(workService.getWorkContext(anyLong(), anyLong())).thenReturn(context);
		when(workService.findActiveWorkerId(anyLong())).thenReturn(2L);
		when(authenticationService.getCurrentUserId()).thenReturn(1L);
		when(workService.findWorkByWorkNumber(anyString())).thenReturn(work);
		when(noteDAO.get(anyLong())).thenReturn(mock(Note.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findAllTopMessagesByWork_withNullArguments_fails() throws Exception {
		workMessageService.findAllTopMessagesByWork(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findAllRepliesToMessage_withNullArguments_fails() throws Exception {
		workMessageService.findAllRepliesToMessage(null, null, null);
	}

	@Test
	public void findAllTopMessagesByWork_success() throws Exception {
		workMessageService.findAllTopMessagesByWork("36363", new WorkMessagePagination());
		verify(workService, times(1)).getWorkContext(eq(work.getId()), eq(1L));
		verify(noteDAO, times(1)).findAllTopMessagesVisibleToUser(anyLong(), anyLong(), any(WorkMessagePagination.class), !CollectionUtilities.containsAny(anyList(), WorkContext.OWNER, WorkContext.COMPANY_OWNED, WorkContext.ACTIVE_RESOURCE, WorkContext.DISPATCHER, WorkContext.INVITED, WorkContext.WORK_MARKET_INTERNAL), CollectionUtilities.containsAny(anyList(), WorkContext.ACTIVE_RESOURCE, WorkContext.INVITED, WorkContext.DISPATCHER));
	}

	@Test
	public void findAllRepliesToMessage_success() throws Exception {
		workMessageService.findAllRepliesToMessage("838393", 1L, new WorkMessagePagination());
		verify(workService, times(1)).getWorkContext(eq(work.getId()), eq(1L));
		verify(noteDAO, times(1)).findAllMessagesVisibleToUser(anyLong(), anyLong(), anyLong(), any(WorkMessagePagination.class), !CollectionUtilities.containsAny(anyList(), WorkContext.OWNER, WorkContext.COMPANY_OWNED, WorkContext.ACTIVE_RESOURCE, WorkContext.DISPATCHER, WorkContext.INVITED, WorkContext.WORK_MARKET_INTERNAL), CollectionUtilities.containsAny(anyList(), WorkContext.ACTIVE_RESOURCE, WorkContext.INVITED, WorkContext.DISPATCHER));
	}

	@Test(expected = IllegalArgumentException.class)
	public void editWorkMessage_withNullArguments_fail() throws Exception {
		workMessageService.editWorkMessage(null);
	}

	@Test
	public void editWorkMessage_success() throws Exception {
		workMessageService.editWorkMessage(new NoteDTO().setNoteId(1L).setContent("new note content"));
		verify(noteDAO, times(1)).get(eq(1L));
	}

	@Test
	public void findAllMessagesByWork_success() throws Exception {
		workMessageService.findAllMessagesByWork("1", new WorkMessagePagination());
		verify(noteDAO, times(1)).findAllMessagesVisibleToUser(anyLong(), eq(1L), anyLong(), any(WorkMessagePagination.class), !CollectionUtilities.containsAny(anyList(), WorkContext.OWNER, WorkContext.COMPANY_OWNED, WorkContext.ACTIVE_RESOURCE, WorkContext.DISPATCHER, WorkContext.INVITED, WorkContext.WORK_MARKET_INTERNAL), CollectionUtilities.containsAny(anyList(), WorkContext.ACTIVE_RESOURCE, WorkContext.INVITED, WorkContext.DISPATCHER));
	}

	@Test
	public void findAllMessagesByWorkAsDispatcher_success() throws Exception {
		when(context.contains(WorkContext.DISPATCHER)).thenReturn(true);
		when(work.isActive()).thenReturn(true);
		workMessageService.findAllMessagesByWork("1", new WorkMessagePagination());
		verify(noteDAO, times(1)).findAllMessagesVisibleToUser(anyLong(), eq(2L), anyLong(), any(WorkMessagePagination.class), !CollectionUtilities.containsAny(anyList(), WorkContext.OWNER, WorkContext.COMPANY_OWNED, WorkContext.ACTIVE_RESOURCE, WorkContext.DISPATCHER, WorkContext.INVITED, WorkContext.WORK_MARKET_INTERNAL), CollectionUtilities.containsAny(anyList(), WorkContext.ACTIVE_RESOURCE, WorkContext.INVITED, WorkContext.DISPATCHER));
	}
}
