package com.workmarket.api.model.resolver;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;

import com.workmarket.api.v2.worker.model.FeedRequestDTO;
import com.workmarket.domains.work.service.dashboard.MobileDashboardService;
import com.workmarket.utility.NumberUtilities;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import edu.emory.mathcs.backport.java.util.Arrays;

public class FeedRequestDTOArgumentResolver implements HandlerMethodArgumentResolver {
  public boolean supportsParameter(MethodParameter methodParameter) {
    Class<?> parameterType = methodParameter.getParameterType();
    return methodParameter.hasParameterAnnotation(ApiArgumentResolver.class)
        && parameterType.isAssignableFrom(FeedRequestDTO.class);
  }

  @Override
  public Object resolveArgument(
      final MethodParameter methodParameter,
      final ModelAndViewContainer modelAndViewContainer,
      final NativeWebRequest nativeWebRequest,
      final WebDataBinderFactory webDataBinderFactory) throws Exception {
    if (supportsParameter(methodParameter)) {
      final HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
      return evaluate(request);
    }

    return null;
  }

  @VisibleForTesting
  protected FeedRequestDTO evaluate(final HttpServletRequest r) {
    final FeedRequestDTO.Builder builder = new FeedRequestDTO.Builder();

    int pageSize = NumberUtils.toInt(r.getParameter("pageSize"),
        MobileDashboardService.DEFAULT_ASSIGNMENT_FEED_PAGE_SIZE);
    if (pageSize > MobileDashboardService.DEFAULT_ASSIGNMENT_FEED_PAGE_SIZE) {
      pageSize = MobileDashboardService.DEFAULT_ASSIGNMENT_FEED_PAGE_SIZE;
    }
    builder.withPageSize(pageSize);

    final Boolean sortByDistance = BooleanUtils.toBooleanObject(r.getParameter("sortByDistance"));
    builder.withSortByDistance(sortByDistance == null ? false : sortByDistance);

    final Boolean filterOutApplied = BooleanUtils.toBooleanObject(r.getParameter("filterOutApplied"));
    if (filterOutApplied != null) {
      builder.withFilterOutApplied(filterOutApplied);
    }

    final Boolean virtual = BooleanUtils.toBooleanObject(r.getParameter("virtual"));
    if (virtual != null) {
      builder.withVirtual(virtual);
    }

    builder.withIndustryId(NumberUtilities.safeParseInteger(r.getParameter("industryId")));
    builder.withKeyword(r.getParameter("keyword"));
    builder.withLatitude(NumberUtilities.safeParseDouble(r.getParameter("latitude")));
    builder.withLongitude(NumberUtilities.safeParseDouble(r.getParameter("longitude")));
    builder.withRadius(NumberUtilities.safeParseDouble(r.getParameter("radius")));
    builder.withFields(r.getParameter("fields"));
    builder.withWhen(r.getParameter("when") == null ? "all" : r.getParameter("when"));
    builder.withPage(NumberUtils.toInt(r.getParameter("page"), 1));
    builder.withSort(getAsList(r.getParameter("sort")));
    builder.withEndDate(NumberUtilities.safeParseLong(r.getParameter("endDate")));
    builder.withStartDate(NumberUtilities.safeParseLong(r.getParameter("startDate")));
    builder.withFilter(getAsList(r.getParameter("filter")));

    return builder.build();
  }

  private List<String> getAsList(final String sort) {
    if (StringUtils.isEmpty(sort)) {
      return new ArrayList<>();
    }

    return Arrays.asList(sort.split(","));
  }
}
