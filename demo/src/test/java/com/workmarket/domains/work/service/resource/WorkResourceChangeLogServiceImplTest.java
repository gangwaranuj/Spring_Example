package com.workmarket.domains.work.service.resource;

import com.workmarket.dao.UserDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.work.dao.ResourceNoteDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkResourceChangeLogDAO;
import com.workmarket.domains.work.dao.WorkResourceChangeNoteDAO;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.WorkResourceChangeLog;
import com.workmarket.domains.work.model.WorkResourceChangeNote;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.domains.work.service.resource.action.WorkResourceActionService;
import com.workmarket.domains.work.service.workresource.WorkResourceDetailCache;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.exception.InvalidParameterException;
import com.workmarket.service.infra.notification.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkResourceChangeLogServiceImplTest {

	@Mock WorkResourceActionService actionService;
	@Mock WorkResourceChangeLogDAO workResourceChangeLogDAO;
	@Mock WorkResourceChangeNoteDAO workResourceChangeNoteDAO;
	@Mock WorkResourceDAO workResourceDAO;
	@Mock UserDAO userDAO;
	@Mock WorkDAO workDAO;
	@Mock ResourceNoteDAO resourceNoteDAO;
	@Mock NotificationService notificationService;
	@Mock WorkAuditService workAuditService;
	@Mock WorkNoteService workNoteService;
	@Mock WorkResourceDetailCache workResourceDetailCache;
	@InjectMocks WorkResourceChangeLogServiceImpl workResourceChangeLogService;

	private User workerUser;
	private WorkResourceChangeLog changeLog;

	@Before
	public void setUp() throws Exception {
		workerUser = mock(User.class);
		changeLog = mock(WorkResourceChangeLog.class);
		when(workResourceChangeLogDAO.get(anyLong())).thenReturn(changeLog);
		when(workNoteService.addNoteToWork(anyLong(), any(NoteDTO.class), any(User.class))).thenReturn(mock(Note.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void resourceNoteSuccess_withNullArguments_fail() throws Exception {
		workResourceChangeLogService.resourceNoteSuccess(null);
	}

	@Test(expected = InvalidParameterException.class)
	public void createWorkResourceChangeLog_withNullArguments_fail() throws Exception {
		workResourceChangeLogService.createWorkResourceChangeLog(null, null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveNoteForWorkResourceChangeLog_withNullArguments_fail() throws Exception {
		workResourceChangeLogService.saveNoteForWorkResourceChangeLog(null, null, 0, null);
	}

	@Test
	public void saveNoteForWorkResourceChangeLog_success() throws Exception {
		workResourceChangeLogService.saveNoteForWorkResourceChangeLog("a note", workerUser, 0, changeLog);
		verify(workNoteService, times(1)).addNoteToWork(anyLong(), any(NoteDTO.class), any(User.class));
		verify(workResourceChangeNoteDAO, times(1)).saveOrUpdate(any(WorkResourceChangeNote.class));
	}

}