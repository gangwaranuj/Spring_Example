package com.workmarket.service.infra.jms;

import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.service.business.dto.NotificationDTO;
import com.workmarket.service.business.event.Event;

import java.util.Calendar;


public interface JmsService {

	void sendMessage(final NotificationDTO notificationDTO);

	void sendMessage(final NotificationDTO notificationDTO, final Calendar scheduleDate);

	void sendMessage(final NotificationTemplate notificationTemplate, final Calendar scheduleDate);

	void sendEventMessage(final Event event);

	void sendBatchMessage(final BatchMessageType type);

}
