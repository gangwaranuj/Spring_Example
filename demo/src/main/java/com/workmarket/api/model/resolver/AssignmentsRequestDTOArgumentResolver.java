package com.workmarket.api.model.resolver;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;

import com.workmarket.api.v2.worker.model.AssignmentsRequestDTO;
import com.workmarket.domains.model.WorkStatusType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class AssignmentsRequestDTOArgumentResolver implements HandlerMethodArgumentResolver {
  public boolean supportsParameter(MethodParameter methodParameter) {
    Class<?> parameterType = methodParameter.getParameterType();
    return methodParameter.hasParameterAnnotation(ApiArgumentResolver.class)
        && parameterType.isAssignableFrom(AssignmentsRequestDTO.class);
  }

  @Override
  public Object resolveArgument(
      final MethodParameter methodParameter,
      final ModelAndViewContainer modelAndViewContainer,
      final NativeWebRequest nativeWebRequest,
      final WebDataBinderFactory webDataBinderFactory) throws Exception {
    if (supportsParameter(methodParameter)) {
      final HttpServletRequest r = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
      return evaluate(r);
    }

    return null;
  }

  @VisibleForTesting
  protected AssignmentsRequestDTO evaluate(final HttpServletRequest r) {
    final AssignmentsRequestDTO.Builder builder = new AssignmentsRequestDTO.Builder();

    int pageSize = NumberUtils.toInt(r.getParameter("pageSize"), 0);
    if (pageSize < 1 || pageSize > 30) pageSize = 25;
    builder.withPageSize(pageSize);

    if (StringUtils.isEmpty(r.getParameter("status"))) {
      builder.withStatus(new WorkStatusType(WorkStatusType.AVAILABLE));
    } else {
      builder.withStatus(new WorkStatusType(r.getParameter("status")));
    }

    if (StringUtils.isEmpty(r.getParameter("fields"))) {
      builder.withFields("");
    } else {
      builder.withFields(r.getParameter("fields"));
    }

    builder.withSort(r.getParameter("sort"));
    builder.withPage(NumberUtils.toInt(r.getParameter("page"), 1));

    return builder.build();
  }
}
