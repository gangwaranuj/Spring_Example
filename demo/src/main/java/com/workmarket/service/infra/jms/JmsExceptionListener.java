package com.workmarket.service.infra.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class JmsExceptionListener implements ExceptionListener {

	private static final Log logger = LogFactory.getLog(JmsExceptionListener.class);

	@Override
	public void onException(JMSException exception) {
		logger.error(exception.getCause());
	}

}
