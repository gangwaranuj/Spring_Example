package com.workmarket.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.workmarket.api.v2.worker.model.UserNotificationUTC;
import com.workmarket.notification.user.vo.UserNotification;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JodaDateTimeUTCMillisSerializerTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final String DATE_AS_STRING = "2015-01-01T00:00:00.000Z";
  private static final DateTime DATE_TIME = new DateTime(DATE_AS_STRING);
  private static final long DATE_IN_MILLIS = 1420070400000L;
  private static final String SERIALIZED_STRING = "{\"dateTime\":" + DATE_IN_MILLIS + "}";
  private static final String VIEWED_AT_STRING = "\"viewedAt\":" + DATE_IN_MILLIS;
  private static final String CREATED_ON_STRING = "\"createdOn\":" + DATE_IN_MILLIS;
  private static final String MODIFIED_ON_STRING = "\"modifiedOn\":" + DATE_IN_MILLIS;

  private static class ClassWithDate {
    @JsonSerialize(using = JodaDateTimeUTCMillisSerializer.class)
    private DateTime dateTime;

    public DateTime getDateTime() {
      return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
      this.dateTime = dateTime;
    }
  }

  @Test
  public void testSerializer() throws JsonProcessingException {
    final ClassWithDate test = new ClassWithDate();
    test.setDateTime(DATE_TIME);

    final String str = MAPPER.writeValueAsString(test);

    assertEquals(SERIALIZED_STRING, str);
  }

  @Test
  public void testSerializerUserNotification() throws JsonProcessingException {
    final UserNotificationUTC test =
        new UserNotificationUTC(
            UserNotification.builder().setViewedAt(DATE_TIME).setCreatedOn(DATE_TIME).setModifiedOn(DATE_TIME).build());

    final String str = MAPPER.writeValueAsString(test);

    assertTrue(str.contains(VIEWED_AT_STRING));
    assertTrue(str.contains(CREATED_ON_STRING));
    assertTrue(str.contains(MODIFIED_ON_STRING));
  }
}