package com.workmarket.api.v2.worker.notification;

import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.model.UserNotificationSearchRequest;
import com.workmarket.api.model.resolver.UserNotificationSearchRequestArgumentResolver;
import com.workmarket.api.v2.ApiPaginationUserNotifications;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.BadRequestApiException;
import com.workmarket.notification.user.vo.UserNotificationSearchResponse;
import com.workmarket.service.business.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/worker/v2/usernotification")
@Controller(value = "workerUserNotificationController")
public class UserNotificationController extends ApiBaseController {
  @Autowired
  UserNotificationService userNotificationService;

  /**
   * Search notifications.
   *
   * @param searchRequest resolved from query parameters; see {@link UserNotificationSearchRequestArgumentResolver}.
   * @return
   */
  @RequestMapping(
      value = "/search",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody
	ApiV2Response search(final UserNotificationSearchRequest searchRequest) {
    if (searchRequest == null) {
      throw new BadRequestApiException("Invalid request.");
    }

    final UserNotificationSearchResponse response =
        userNotificationService.search(convertToMicroserviceRequest(searchRequest));

    if (response == null) {
      throw new RuntimeException("There was an error processing your request.");
    }

    return new ApiV2Response(
        new ApiJSONPayloadMap(),
        UserNotificationUTCConverter.convert(response.getResults()),
        new ApiPaginationUserNotifications(response.getLimit(), response.getOffset(), response.getRowCount()));
  }

  private com.workmarket.notification.user.vo.UserNotificationSearchRequest convertToMicroserviceRequest(
      final UserNotificationSearchRequest req) {
    return new com.workmarket.notification.user.vo.UserNotificationSearchRequest(
        req.getOffset(), req.getLimit(), req.getArchived(), req.getViewed(), req.getOrder(), req.getDirection(),
        getCurrentUser().getId().toString(), null, null, req.getType(), req.getStatus(), true, null);
  }
}
