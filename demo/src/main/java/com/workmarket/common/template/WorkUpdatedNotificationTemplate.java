package com.workmarket.common.template;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.changelog.PropertyChange;
import com.workmarket.domains.model.changelog.PropertyChangeType;
import com.workmarket.domains.model.changelog.work.WorkPropertyChangeType;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

import java.util.List;
import java.util.Map;

public class WorkUpdatedNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = 1L;

	private Map<PropertyChangeType,List<PropertyChange>> propertyChanges = Maps.newHashMap();

	protected WorkUpdatedNotificationTemplate(Long toId, Work work, double distanceInMilesToWork) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.RESOURCE_WORK_UPDATED), ReplyToType.TRANSACTIONAL, work, distanceInMilesToWork);
	}

	public void setWorkFollow(WorkFollow workFollow) {
		super.setWorkFollow(workFollow);

		// work update notifications are sent to either invited resources or followers
		// we assume it is going to resources unless the work follow is set
		setNotificationType(new NotificationType(NotificationType.WORK_UPDATED));
	}

	public Map<PropertyChangeType, List<PropertyChange>> getPropertyChanges() {
		return propertyChanges;
	}
	public void setPropertyChanges(Map<PropertyChangeType, List<PropertyChange>> propertyChanges) {
		this.propertyChanges = propertyChanges;
	}

	public boolean isPricingUpdated() {
		return propertyChanges.containsKey(WorkPropertyChangeType.PRICING);
	}
	public boolean isScheduleUpdated() {
		return propertyChanges.containsKey(WorkPropertyChangeType.SCHEDULE);
	}
	public boolean isInfoUpdated() {
		return propertyChanges.containsKey(WorkPropertyChangeType.INFO);
	}
	public boolean isContactUpdated() {
		return propertyChanges.containsKey(WorkPropertyChangeType.CONTACT);
	}
	public boolean isOtherUpdated() {
		return propertyChanges.containsKey(WorkPropertyChangeType.OTHER);
	}
}
