package com.workmarket.domains.work.service.actions;

import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkEventServiceUnitTest {
	@Mock MessageBundleHelper messageBundleHelper;
	@Mock EventRouter eventRouter;
	@InjectMocks WorkEventServiceImpl workEventService;

	@Mock AbstractWorkEvent event;
	@Mock AjaxResponseBuilder response;

	@Before
	public void setup(){
		when(event.isValid()).thenReturn(true);
		when(event.isQueue()).thenReturn(false);
		when(event.getResponse()).thenReturn(response);
		when(response.setSuccessful(anyBoolean())).thenReturn(response);
	}

	@Test(expected=Exception.class)
	public void doAction_nullEvent(){
		workEventService.doAction(null);
	}

	@Test
	public void doAction_validate_success(){
		workEventService.doAction(event);
		verify(event).isValid();
	}

	@Test
	public void doAction_validateFails_returns(){
		when(event.isValid()).thenReturn(false);
		workEventService.doAction(event);
		verify(response).setSuccessful(false);
	}

	@Test
	public void doAction_isQueue_eventRouterCalled(){
		when(event.isQueue()).thenReturn(true);
		workEventService.doAction(event);
		verify(eventRouter).sendEvent(event);
		verify(response).setSuccessful(true);
		verify(messageBundleHelper).addMessage(response,event.getMessageKey() + ".success");
	}

	@Test
	public void doAction_isNotQueue_handleEventCalled(){
		when(event.isQueue()).thenReturn(false);
		workEventService.doAction(event);
		verify(event).handleEvent();

	}

}
