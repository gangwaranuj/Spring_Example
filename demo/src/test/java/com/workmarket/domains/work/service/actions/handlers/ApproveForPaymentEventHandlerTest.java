package com.workmarket.domains.work.service.actions.handlers;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.actions.ApproveForPaymentWorkEvent;
import com.workmarket.service.business.wrapper.CloseWorkBulkResponse;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApproveForPaymentEventHandlerTest {
	@Mock WorkService workService;
	@InjectMocks ApproveForPaymentEventHandler approveForPaymentEventHandler;

	Work work;
	Iterator mockIter;
	ApproveForPaymentWorkEvent event;
	AjaxResponseBuilder response;
	List<String> workNumbers = Lists.newArrayList();
	List<Work> works;
	User user;
	CloseWorkBulkResponse workBulkResponse;

	@Before
	public void setup(){

		user = mock(User.class);

		workNumbers = mock(ArrayList.class);
		when(workNumbers.isEmpty()).thenReturn(false);

		response = mock(AjaxResponseBuilder.class);
		when(response.setSuccessful(anyBoolean())).thenReturn(response);

		mockIter = mock(Iterator.class);
		work = mock(Work.class);
		when(work.getWorkNumber()).thenReturn("jkljkl");
		works = mock(ArrayList.class);
		when(works.isEmpty()).thenReturn(false);
		when(works.iterator()).thenReturn(mockIter);
		when(mockIter.hasNext()).thenReturn(true,false);
		when(mockIter.next()).thenReturn(work);

		event = mock(ApproveForPaymentWorkEvent.class);
		when(event.getResponse()).thenReturn(response);
		when(event.getMessageKey()).thenReturn("");
		when(event.getWorkNumbers()).thenReturn(workNumbers);
		when(event.getUser()).thenReturn(user);
		when(event.getOnBehalfOfUser()).thenReturn(user);
		when(event.getWorks()).thenReturn(works);
		when(event.isValid()).thenReturn(true);

		workBulkResponse = mock(CloseWorkBulkResponse.class);
		when(workBulkResponse.isSuccessful()).thenReturn(true);
	}

	@Test(expected = Exception.class)
	public void handleEvent_nullEvent_throwsException(){
		approveForPaymentEventHandler.handleEvent(null);
	}

	@Test(expected = Exception.class)
	public void handleEvent_validateFails_throwsException(){
		when(event.isValid()).thenReturn(false);
		approveForPaymentEventHandler.handleEvent(event);
	}

	@Test(expected = Exception.class)
	public void handleEvent_validateThrowsException_throwsException(){
		doThrow(Exception.class).when(event).isValid();
		approveForPaymentEventHandler.handleEvent(event);
	}

	@Test
	public void handleEvent_validEvent_closeWorkCalled(){
		approveForPaymentEventHandler.handleEvent(event);
		verify(workService).closeWork(work.getId());
	}

	@Test
	public void handleEvent_validEvent_successfulReturn(){
		approveForPaymentEventHandler.handleEvent(event);
		verify(event).setSuccessful(true);
	}



}
