package com.workmarket.service.infra.event.transactional;

import com.workmarket.service.business.event.EntityUpdateEvent;
import com.workmarket.service.business.event.MarkUserNotificationsAsReadEvent;
import com.workmarket.service.business.event.RefreshUserNotificationCacheEvent;
import com.workmarket.service.business.event.UserGroupMessageNotificationEvent;
import com.workmarket.service.business.event.WorkSubStatusTypeUpdatedEvent;
import com.workmarket.service.business.event.forums.CreateWorkFromFlaggedPostEvent;
import com.workmarket.domains.forums.service.event.NotifyPostFollowerEvent;
import com.workmarket.service.business.event.reporting.WorkReportGenerateEvent;
import com.workmarket.service.business.event.work.ResourceConfirmationRequiredScheduledEvent;
import com.workmarket.service.business.event.work.WorkCreatedEvent;
import com.workmarket.service.business.event.work.WorkInvoiceGenerateEvent;
import com.workmarket.service.business.event.work.WorkResourceLateLabelScheduledEvent;
import com.workmarket.service.business.event.work.WorkUpdatedEvent;
import com.workmarket.domains.work.service.actions.WorkViewedEvent;
import com.workmarket.service.search.user.SearchCSVGenerateEvent;

public interface EventService {

	void processEvent(EntityUpdateEvent event);

	void processEvent(ResourceConfirmationRequiredScheduledEvent event);

	void processEvent(UserGroupMessageNotificationEvent event);

	void processEvent(WorkCreatedEvent event);

	void processEvent(WorkUpdatedEvent event);

	void processEvent(WorkReportGenerateEvent event);

	void processEvent(WorkResourceLateLabelScheduledEvent event);

	void processEvent(WorkSubStatusTypeUpdatedEvent event);

	void processEvent(WorkInvoiceGenerateEvent event);

	void processEvent(SearchCSVGenerateEvent event);

	void processEvent(RefreshUserNotificationCacheEvent event);

	void processEvent(MarkUserNotificationsAsReadEvent event);

	void processEvent(WorkViewedEvent event);

	void processEvent(NotifyPostFollowerEvent event);

	void processEvent(CreateWorkFromFlaggedPostEvent event);
}
