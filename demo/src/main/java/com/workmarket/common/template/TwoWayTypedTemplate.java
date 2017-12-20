package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;

/**
 * This is an abstraction for any template with fromId, toId, and notificationType.
 */
public abstract class TwoWayTypedTemplate extends Template {
    private Long fromId;
    private Long toId;
    private NotificationType notificationType;

    public TwoWayTypedTemplate() {

    }

    public TwoWayTypedTemplate(final Long fromId) {
        this.fromId = fromId;
    }

    public TwoWayTypedTemplate(final Long fromId, final Long toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    public TwoWayTypedTemplate(final Long fromId, final Long toId, final NotificationType notificationType) {
        this.fromId = fromId;
        this.toId = toId;
        this.notificationType = notificationType;
    }

    public TwoWayTypedTemplate(final Long fromId, final NotificationType notificationType) {
        this.fromId = fromId;
        this.notificationType = notificationType;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public Long getFromId() {
        return fromId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public Long getToId() {
        return toId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

}
