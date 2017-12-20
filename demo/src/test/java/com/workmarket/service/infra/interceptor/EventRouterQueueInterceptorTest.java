package com.workmarket.service.infra.interceptor;

import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.web.EventQueueContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventRouterQueueInterceptorTest {

	@Mock EventQueueContext eventQueueContext;
	@InjectMocks EventRouterQueueInterceptor interceptor;

	Set<Event> hasSet;
	WorkUpdateSearchIndexEvent event;
	Object[] args;
	ProceedingJoinPoint pjp;

	@Before
	public void setup() {
		hasSet = mock(HashSet.class);
		event = mock(WorkUpdateSearchIndexEvent.class);
		when(eventQueueContext.isThrottlingEvents()).thenReturn(false);
		when(eventQueueContext.getEvents()).thenReturn(hasSet);
		pjp = mock(ProceedingJoinPoint.class);
		args = new Object[] { event };
		when(pjp.getArgs()).thenReturn(args);
	}

	@Test
	public void shouldNotThrottleIfNotRequested() throws Throwable {
		interceptor.onSendEventAround(pjp);
		verify(pjp).proceed(args);
	}

	@Test
	public void shouldNotThrottleIfEventTypeIsWorkUpdateSearchIndexEvent() throws Throwable {
		when(eventQueueContext.isThrottlingEvents()).thenReturn(true);
		interceptor.onSendEventAround(pjp);
		verify(hasSet).add(any(WorkUpdateSearchIndexEvent.class));
		verify(pjp).proceed(new Object[] { null });
	}
}