package com.workmarket.api.v2.worker.notification;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.v2.worker.model.UserNotificationUTC;
import com.workmarket.notification.user.vo.UserNotification;

import java.util.List;

public class UserNotificationUTCConverter {
  public static List<UserNotificationUTC> convert(final List<UserNotification> notifications) {
    final ImmutableList.Builder<UserNotificationUTC> builder = ImmutableList.builder();
    for (final UserNotification un : notifications) {
      builder.add(new UserNotificationUTC(un));
    }
    return builder.build();
  }
}
