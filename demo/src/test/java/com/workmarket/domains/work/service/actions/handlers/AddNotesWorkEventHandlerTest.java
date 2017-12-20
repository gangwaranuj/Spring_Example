package com.workmarket.domains.work.service.actions.handlers;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.actions.AddNotesWorkEvent;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddNotesWorkEventHandlerTest {
	@Mock WorkNoteService workNoteService;
	@Mock MessageBundleHelper messageBundleHelper;
	@Mock EventRouter eventRouter;
	@InjectMocks AddNotesWorkEventHandler addNotesWorkEventHandler;

	AddNotesWorkEvent event;
	AjaxResponseBuilder response;
	List<String> workNumbers = Lists.newArrayList();
	List<Work> works;
	List<WorkNote> workNotes;
	User user;


	@Before
	public void setup(){

		user = mock(User.class);

		workNumbers = mock(ArrayList.class);
		when(workNumbers.isEmpty()).thenReturn(false);

		response = mock(AjaxResponseBuilder.class);

		works = mock(ArrayList.class);
		when(works.isEmpty()).thenReturn(false);

		event = mock(AddNotesWorkEvent.class);
		when(event.getResponse()).thenReturn(response);
		when(event.getMessageKey()).thenReturn("");
		when(event.getWorkNumbers()).thenReturn(workNumbers);
		when(event.getContent()).thenReturn("test");
		when(event.getUser()).thenReturn(user);
		when(event.getOnBehalfOfUser()).thenReturn(user);
		when(event.getWorks()).thenReturn(works);
		when(event.isValid()).thenReturn(true);

		workNotes = Lists.newArrayList(new WorkNote());
		when(workNoteService.bulkAddNoteToWorkList(any(ArrayList.class),any(NoteDTO.class),any(User.class))).thenReturn(workNotes);
	}

	@Test(expected=Exception.class)
	public void test_handleEvent_nullEvent(){
		addNotesWorkEventHandler.handleEvent(null);
	}


	@Test
	public void test_handleEvent_emptyContent(){
		when(event.getContent()).thenReturn("");
		addNotesWorkEventHandler.handleEvent(event);
		verify(event).getResponse();
	}

	@Test
	public void handleEvent_validateEventFails_returns(){
		when(event.isValid()).thenReturn(false);
		addNotesWorkEventHandler.handleEvent(event);
		verify(event,times(2)).getResponse();
	}

	@Test
	public void test_bulkAddNote(){
		addNotesWorkEventHandler.handleEvent(event);
		verify(workNoteService).bulkAddNoteToWorkList(any(ArrayList.class), (NoteDTO) anyObject(), any(User.class));
		verify(eventRouter).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void test_bulkAddNote_returnsNull(){
		when(workNoteService.bulkAddNoteToWorkList(any(ArrayList.class),any(NoteDTO.class),any(User.class))).thenReturn(null);
		addNotesWorkEventHandler.handleEvent(event);
		verify(messageBundleHelper).addMessage(event.getResponse().setSuccessful(false),event.getMessageKey() + ".exception");
	}

	@Test
	public void test_bulkAddNote_returnsEmpty(){
		workNotes.clear();
		addNotesWorkEventHandler.handleEvent(event);
		verify(messageBundleHelper).addMessage(event.getResponse().setSuccessful(false),event.getMessageKey() + ".exception");
	}

	@Test
	public void test_handleEvent_getResponse(){
		addNotesWorkEventHandler.handleEvent(event);
		verify(event).getResponse();
	}
}
