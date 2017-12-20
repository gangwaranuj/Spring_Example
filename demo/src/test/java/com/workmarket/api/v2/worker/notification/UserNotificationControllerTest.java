package com.workmarket.api.v2.worker.notification;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.exceptions.BadRequestApiException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.ApiPaginationUserNotifications;
import com.workmarket.api.v2.worker.model.UserNotificationUTC;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.notification.user.vo.UserNotification;
import com.workmarket.notification.user.vo.UserNotificationSearchRequest;
import com.workmarket.notification.user.vo.UserNotificationSearchResponse;
import com.workmarket.service.business.UserNotificationService;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserNotificationControllerTest extends BaseApiControllerTest {
  @Mock
  UserNotificationService userNotificationService;
  @Mock
  SecurityContextFacade securityContextFacade;
  @InjectMocks
  private UserNotificationController controller = new UserNotificationController();

  @Before
  public void setup() throws Exception {
    super.setup(controller);
  }

  @Test
  public void shouldReturnCorrectPaginationAnResults() {
    final int offset = 0;
    final int limit = 5;
    final Long recordCount = 1L;
    final UserNotification notification = UserNotification.builder().setUuid("some-id").build();
    final List<UserNotification> results = ImmutableList.of(notification);
    final List<UserNotificationUTC> responseResults = ImmutableList.of(new UserNotificationUTC(notification));
    final UserNotificationSearchResponse serverResonse =
        new UserNotificationSearchResponse(limit, offset, results, recordCount);
    when(userNotificationService.search(any(UserNotificationSearchRequest.class))).thenReturn(serverResonse);

    final ApiV2Response response =
        controller.search(com.workmarket.api.model.UserNotificationSearchRequest.builder().build());

    final ApiPaginationUserNotifications info = (ApiPaginationUserNotifications) response.getPagination();
    assertTrue(CollectionUtils.isEqualCollection(responseResults, response.getResults()));
    assertEquals(limit, info.getLimit());
    assertEquals(offset, info.getOffset());
    assertEquals(recordCount, info.getRowCount());
  }

  @Test
  public void shouldThrowBadRequestOnNullRequest() {
    try {
      controller.search(null);
    } catch (final BadRequestApiException e) {
      return;
    }

    fail();
  }

  @Test
  public void shouldThrowGenericErrorOnServerNull() {
    try {
      when(userNotificationService.search(any(UserNotificationSearchRequest.class))).thenReturn(null);

      controller.search(com.workmarket.api.model.UserNotificationSearchRequest.builder().build());
    } catch (final RuntimeException e) {
      assertEquals("There was an error processing your request.", e.getMessage());
      return;
    }

    fail();
  }
}