package com.workmarket.web.filter;

import com.workmarket.service.business.event.Event;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.web.EventQueueContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component(value = "dispatchEventQueueFilter")
public class DispatchEventQueueFilter implements Filter {
	private final Log logger = LogFactory.getLog(DispatchEventQueueFilter.class);
	@Autowired EventQueueContext eventQueueContext;
	@Autowired EventRouter eventRouter;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		eventQueueContext.startThrottlingEvents();
		filterChain.doFilter(servletRequest, servletResponse);
		eventQueueContext.stopThrottlingEvents();

		if (CollectionUtils.isNotEmpty(eventQueueContext.getEvents())) {
			logger.info("Dispatching throttled events for processing: " + eventQueueContext.getEvents());

			try {
				for (Event event : eventQueueContext.getEvents()) {
					eventRouter.sendEvent(event);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				eventQueueContext.clearEvents();
			}
		}
	}

	@Override
	public void destroy() {
		eventQueueContext.clearEvents();
	}

}
