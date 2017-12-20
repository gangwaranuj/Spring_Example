package com.workmarket.service.business.queue.integration;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Reservoir;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.integration.event.IntegrationEvent;
import com.workmarket.service.business.integration.event.IntegrationListenerService;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jms.JMSException;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationListenerTest {
  public static final String SECOND_REQUEST_ID = "xyz876";
  public static final String FIRST_REQUEST_ID = "abc123";

  public static final String FIRST_REQUEST_TENANT = "hello456";
  public static final String SECOND_REQUEST_TENANT = "yolo123";
  @Mock
  IntegrationListenerService integrationListenerService;
  @Mock
  MetricRegistry metricRegistry;

  @Mock
	WebRequestContextProvider webRequestContextProvider;
  @InjectMocks
  IntegrationListener integrationListener = spy(new IntegrationListener());

  @Before
  public void setup() {
    when(metricRegistry.meter(anyString())).thenReturn(new Meter());
    when(metricRegistry.histogram(anyString())).thenReturn(new Histogram(mock(Reservoir.class)));
    integrationListener.init();
    when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
    when(webRequestContextProvider.getWebRequestContext(any(String.class), any(String.class))).thenCallRealMethod();
    doCallRealMethod().when(webRequestContextProvider).inject(any(IntegrationEvent.class));
    doCallRealMethod().when(webRequestContextProvider).extract(any(IntegrationEvent.class));
  }

  @Test
  public void shouldSetRequestContextInfoThenClear() throws JMSException {
    final ActiveMQObjectMessage message = mock(ActiveMQObjectMessage.class);
    final IntegrationEvent event = IntegrationEvent.newWorkApproveEvent(null, null, false);
    final String requestId = "request-Id";
    final String userId = "userId";

    webRequestContextProvider.getWebRequestContext(FIRST_REQUEST_ID, FIRST_REQUEST_TENANT);
    webRequestContextProvider.inject(event);
    when(message.getObject()).thenReturn(event);

    integrationListener.onMessage(message);

    assertSame("Expect requestId to match", FIRST_REQUEST_ID, webRequestContextProvider.getWebRequestContext().getRequestId());
    assertSame("Expect tenant to match", FIRST_REQUEST_TENANT, webRequestContextProvider.getWebRequestContext().getTenant());

    assertSame("Expect requestId to match", FIRST_REQUEST_ID, event.getWebRequestContext().getRequestId());

    verify(webRequestContextProvider, times(1)).extract(event);
    verify(webRequestContextProvider).clear();
  }
}