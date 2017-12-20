package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.utility.DateUtilities;

public class AbstractWorkRescheduleNegotiationNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = 5145335905698856793L;

	protected WorkRescheduleNegotiation negotiation;

	public AbstractWorkRescheduleNegotiationNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation, NotificationType notificationType) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, notificationType, ReplyToType.TRANSACTIONAL, work);
		this.negotiation = negotiation;
	}

	public AbstractWorkRescheduleNegotiationNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation, NotificationType notificationType, Long activeWorkerId) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, notificationType, ReplyToType.TRANSACTIONAL, work, activeWorkerId);
		this.negotiation = negotiation;
	}

	public WorkRescheduleNegotiation getNegotiation() {
		return negotiation;
	}

	public String getNegotiationDate() {
		if (negotiation.getScheduleRangeFlag() && negotiation.getScheduleThrough() != null) {
			return String.format("%s to: %s", DateUtilities.formatDateForEmail(negotiation.getScheduleFrom(), getTimeZoneId()), DateUtilities.formatDateForEmail(negotiation.getScheduleThrough(), getTimeZoneId()));
		} else {
			return DateUtilities.formatDateForEmail(negotiation.getScheduleFrom(), getTimeZoneId());
		}
	}

	public boolean getIsInitiatedByResource() {
		return negotiation.isInitiatedByResource();
	}
}
