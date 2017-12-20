package com.workmarket.service.infra.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

/**
 * Created by nick on 4/17/13 11:19 AM
 */
@Component
public class JmsErrorHandler implements ErrorHandler {
	private static final Log logger = LogFactory.getLog(JmsErrorHandler.class);
	private final String name;

	public JmsErrorHandler() {
		this.name = "none";
	}

	public JmsErrorHandler(String name) {
		this.name = name;
	}

	@Override
	public void handleError(Throwable t) {
		logger.error(String.format("Error in JMS listener '%s': ", name), t);
	}
}
