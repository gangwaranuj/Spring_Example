package com.workmarket.service.infra.interceptor;

import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.web.EventQueueContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

/**
 * Buffers events sent via the EventRouter.send() method. Buffering is
 * used to avoid sending duplicate events to the workers.
 */
public class EventRouterQueueInterceptor {
	private final Log logger = LogFactory.getLog(EventRouterQueueInterceptor.class);
	@Autowired EventQueueContext eventQueueContext;

	public Object onSendEventAround(ProceedingJoinPoint pjp) throws Throwable {
		Object[] arguments = pjp.getArgs();
		if (isNotEmpty(arguments) && eventQueueContext.isThrottlingEvents()) {
			Object firstArgument = arguments[0];

			if (firstArgument instanceof WorkUpdateSearchIndexEvent) {
				WorkUpdateSearchIndexEvent event = (WorkUpdateSearchIndexEvent) firstArgument;

				if (eventQueueContext.getEvents().contains(event)) {
					logger.info("Duplicate event found: " + event);
				}

				eventQueueContext.getEvents().add(event); // buffer the event to be processed later

				if (logger.isDebugEnabled()) {
					logger.debug("Buffering WorkUpdateSearchIndexEvent " + event.getWorkIds());
				}

				return pjp.proceed(new Object[] { null }); // continue with null to enforce a noop
			}
		}

		return pjp.proceed(arguments);
	}
}
