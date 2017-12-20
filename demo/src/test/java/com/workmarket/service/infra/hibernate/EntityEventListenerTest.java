package com.workmarket.service.infra.hibernate;

import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.jms.JmsService;
import com.workmarket.service.infra.security.SecurityContext;
import org.hibernate.event.PostUpdateEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class EntityEventListenerTest {

	@Mock JmsService jmsService;
	@Mock SecurityContext securityContext;
	@InjectMocks EntityEventListener entityEventListener;

	private PostUpdateEvent postUpdateEvent;
	private Work work;

	@Before
	public void setup() {
		postUpdateEvent = mock(PostUpdateEvent.class);
	    work = mock(Work.class);
		when(postUpdateEvent.getEntity()).thenReturn(work);

	}

	@Test
	public void onPostUpdate_success() throws Exception {

		entityEventListener.onPostUpdate(postUpdateEvent);
		verify(postUpdateEvent, atLeastOnce()).getEntity();

	}
}
