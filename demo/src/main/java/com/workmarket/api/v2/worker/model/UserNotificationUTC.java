package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.workmarket.notification.user.vo.UserNotification;
import com.workmarket.serializer.JodaDateTimeUTCMillisSerializer;

import org.joda.time.DateTime;

/**
 * Class used to override {@link UserNotification} date serialization to UTC epoch millis.
 */
public class UserNotificationUTC extends UserNotification {
  // API TODO - ??
  @JsonSerialize(using = JodaDateTimeUTCMillisSerializer.class)
  private final DateTime viewedAt;
  @JsonSerialize(using = JodaDateTimeUTCMillisSerializer.class)
  private final DateTime createdOn;
  @JsonSerialize(using = JodaDateTimeUTCMillisSerializer.class)
  private final DateTime modifiedOn;

  public UserNotificationUTC(final UserNotification un) {
    super(un.getUuid(), un.getType(), un.getDisplayMessage(), un.isSticky(), un.getStatus(), un.getFromUserId(),
        un.getToUserId(), un.getViewedAt(), un.getModalMessage(), un.isArchived(), un.getCreatedBy(),
        un.getModifiedBy(), un.getCreatedOn(), un.getModifiedOn(), un.getIsDeleted(), null, null);
    this.viewedAt = un.getViewedAt();
    this.createdOn = un.getCreatedOn();
    this.modifiedOn = un.getModifiedOn();
  }

  @Override
  public DateTime getViewedAt() {
    return viewedAt;
  }

  @Override
  public DateTime getCreatedOn() {
    return createdOn;
  }

  @Override
  public DateTime getModifiedOn() {
    return modifiedOn;
  }
}
