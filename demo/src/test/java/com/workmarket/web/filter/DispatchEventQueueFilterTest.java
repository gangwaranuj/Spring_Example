package com.workmarket.web.filter;

import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.web.EventQueueContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.HashSet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DispatchEventQueueFilterTest {
	@Mock EventQueueContext eventQueueContext;
	@Mock EventRouter eventRouter;
	@InjectMocks DispatchEventQueueFilter dispatchEventQueueFilter;

	@Mock ServletRequest servletRequest;
	@Mock ServletResponse servletResponse;
	@Mock FilterChain filterChain;

	@Before
	public void setup() {
		when(eventQueueContext.getEvents()).thenReturn(new HashSet<Event>());
	}

	@Test
	public void shouldNotSendAnyEventsIfEmpty() throws Exception {
		dispatchEventQueueFilter.doFilter(servletRequest, servletResponse, filterChain);
		verify(eventRouter, never()).sendEvent(any(Event.class));
	}

	@Test
	public void shouldSendEventsIfNotEmpty() throws Throwable {
		when(eventQueueContext.getEvents()).thenReturn(new HashSet<Event>() {{
			add(new WorkUpdateSearchIndexEvent());
		}});
		dispatchEventQueueFilter.doFilter(servletRequest, servletResponse, filterChain);
		verify(eventRouter, times(1)).sendEvent(any(Event.class));
	}

	@Test
	public void shouldClearEventsOnCompletion() throws Throwable {
		when(eventQueueContext.getEvents()).thenReturn(new HashSet<Event>() {{
			add(new WorkUpdateSearchIndexEvent());
		}});
		dispatchEventQueueFilter.doFilter(servletRequest, servletResponse, filterChain);
		verify(eventQueueContext, times(1)).clearEvents();
	}
}