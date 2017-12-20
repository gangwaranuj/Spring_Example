package com.workmarket.api.model.resolver;

import com.workmarket.api.model.UserNotificationSearchRequest;
import com.workmarket.notification.Direction;
import com.workmarket.notification.OrderField;
import com.workmarket.notification.user.vo.UserNotificationStatus;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class UserNotificationSearchRequestArgumentResolver implements HandlerMethodArgumentResolver {
  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.getParameterType().equals(UserNotificationSearchRequest.class);
  }

  @Override
  public Object resolveArgument(
      final MethodParameter methodParameter,
      final ModelAndViewContainer modelAndViewContainer,
      final NativeWebRequest webRequest,
      final WebDataBinderFactory webDataBinderFactory) throws Exception {
    final ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
    final HttpServletRequest request = servletWebRequest.getRequest();

    return UserNotificationSearchRequest.builder()
        .setArchived("true".equals(request.getParameter("archived")))
        .setViewed("true".equals(request.getParameter("viewed")))
        .setOrder(request.getParameter("order") == null
            ? null : OrderField.valueOf(request.getParameter("order")))
        .setDirection(request.getParameter("direction") == null
            ? null : Direction.valueOf(request.getParameter("direction")))
        .setStatus(request.getParameter("status") == null
            ? null : UserNotificationStatus.valueOf(request.getParameter("status")))
        .setLimit(request.getParameter("limit") == null
            ? UserNotificationSearchRequest.LIMIT_DEFAULT : Integer.valueOf(request.getParameter("limit")))
        .setOffset(request.getParameter("offset") == null
            ? UserNotificationSearchRequest.OFFSET_DEFAULT : Integer.valueOf(request.getParameter("offset")))
        .setType(request.getParameter("type"))
        .build();
  }
}
