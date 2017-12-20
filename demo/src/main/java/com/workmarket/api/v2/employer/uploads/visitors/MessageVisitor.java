package com.workmarket.api.v2.employer.uploads.visitors;

import com.workmarket.api.v2.employer.uploads.events.CreateAssignmentEvent;
import com.workmarket.api.v2.employer.uploads.events.CreateAssignmentsEvent;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

public class MessageVisitor implements Visitor,
	CreateAssignmentsEventVisitor, CreateAssignmentEventVisitor {

	private final ObjectMessage message;

	public MessageVisitor(ObjectMessage message) {
		this.message = message;
	}

	@Override
	public void visit(CreateAssignmentEvent event) {
		setMessageGroup(event.getUuid());
	}

	@Override
	public void visit(CreateAssignmentsEvent event) {
		setMessageGroup(event.getUuid());
	}

	private void setMessageGroup(String uuid) {
		try {
			// assignments need to be created sequentially to avoid problems with the
			//   account register. Jobs will be grouped by UUID and processed in
			//   order.
			message.setStringProperty("JMSXGroupID", uuid);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
