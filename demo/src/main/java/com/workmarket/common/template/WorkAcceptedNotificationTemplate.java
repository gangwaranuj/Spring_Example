package com.workmarket.common.template;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.utility.DateUtilities;

public class WorkAcceptedNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = 1073325313063452032L;
	private User resource;
	private WorkNegotiation negotiation;

	public WorkAcceptedNotificationTemplate(Long toId, Work work, User resource, WorkNegotiation negotiation) {
		super(resource.getId(), toId, new NotificationType(NotificationType.WORK_ACCEPTED), ReplyToType.TRANSACTIONAL_FROM_USER, work, negotiation);
		this.resource = resource;
		this.negotiation = negotiation;
	}

	public User getResource() {
		return resource;
	}
	
	public WorkNegotiation getNegotiation() {
		return negotiation;
	}
	
	public String getNegotiationDate() {
		if (!negotiation.isScheduleNegotiation()) return null;
		if (negotiation.getScheduleRangeFlag() && negotiation.getScheduleThrough() != null) {
			return String.format("%s to: %s", DateUtilities.formatDateForEmail(negotiation.getScheduleFrom(), getTimeZoneId()), DateUtilities.formatDateForEmail(negotiation.getScheduleThrough(), getTimeZoneId()));
		}
		return DateUtilities.formatDateForEmail(negotiation.getScheduleFrom(), getTimeZoneId());
	}
}
