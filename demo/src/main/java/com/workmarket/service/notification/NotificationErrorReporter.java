package com.workmarket.service.notification;

import com.google.common.collect.ImmutableMap;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.common.kafka.KafkaData;
import com.workmarket.common.template.TwoWayTypedTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationErrorReporter {
    public static final String ERROR_EMPTY_TEXT = "emptyText";
    public static final String ERROR_EXCEPTION_THROWN = "exceptionThrown";

    private static final String ERROR_TOPIC = "notification_error";

    private final KafkaClient kafkaClient;

    @Autowired
    public NotificationErrorReporter(final @Qualifier("AppKafkaClient") KafkaClient kafkaClient) {
        this.kafkaClient = kafkaClient;
    }

    public void sendNotificationErrorToKafka(final TwoWayTypedTemplate template, final String errorType) {
        final Map<String, Object> map = ImmutableMap.<String, Object>of(
            "template", template.getTemplateTemplate(),
            "notificationType", template.getNotificationType(),
            "errorType", errorType,
            "fromId", template.getFromId(),
            "toId", template.getToId());
        final KafkaData kafkaData = new KafkaData(map);
        kafkaClient.send(ERROR_TOPIC, kafkaData);
    }
}
