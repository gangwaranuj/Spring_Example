package com.workmarket.api.v2.employer.uploads.async;

import com.workmarket.api.v2.employer.uploads.events.UploadEvent;
import com.workmarket.api.v2.employer.uploads.visitors.MessageVisitor;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

public class UploadMessageCreator implements MessageCreator {
	private final UploadEvent event;

	public UploadMessageCreator(UploadEvent event) {
		this.event = event;
	}

	@Override
	public ObjectMessage createMessage(Session session) throws JMSException {
		ObjectMessage message = session.createObjectMessage(event);
		MessageVisitor visitor = new MessageVisitor(message);
		event.accept(visitor);
		return message;
	}
}
