package com.workmarket.api.internal.endpoints;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.workmarket.api.exceptions.ForbiddenException;
import com.workmarket.api.exceptions.UnauthorizedException;
import com.workmarket.common.api.exception.ApiException;
import com.workmarket.common.api.exception.BadRequest;
import com.workmarket.common.api.vo.Metadata;
import com.workmarket.common.api.vo.Response;
import com.workmarket.common.core.RequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException403;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

public class MicroserviceBaseController {
  private static Logger logger = LoggerFactory.getLogger(UserAndCompanyInfoController.class);

  @Autowired
  private WebRequestContextProvider webRequestContextProvider;

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public <RESP> Response<RESP> handleBaseApiExceptions(
      final Exception throwable,
      final HttpServletResponse response) throws Exception {
    final RequestContext requestContext = webRequestContextProvider.getRequestContext();
    final HttpStatus errorStatus;
    final Response<RESP> r;
    try {
      logger.error("Request Error", throwable);

      if (throwable instanceof HystrixRuntimeException && throwable.getCause() != null) {
        if (throwable.getCause() instanceof ApiException) {
          errorStatus = HttpStatus.valueOf(((ApiException) throwable.getCause()).getHttpStatus());
        } else if (throwable.getCause() instanceof BadRequest) {
          errorStatus = HttpStatus.BAD_REQUEST;
        } else {
          errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        r = createErrorResponse(requestContext, throwable.getCause());
      } else if (throwable instanceof HystrixBadRequestException) {
        errorStatus = HttpStatus.BAD_REQUEST;
        r = createErrorResponse(requestContext, throwable);
      } else if (throwable instanceof UnauthorizedException || throwable instanceof HttpException401) {
        errorStatus = HttpStatus.UNAUTHORIZED;
        r = createErrorResponse(requestContext, throwable);
      } else if (throwable instanceof ForbiddenException || throwable instanceof HttpException403) {
        errorStatus = HttpStatus.FORBIDDEN;
        r = createErrorResponse(requestContext, throwable);
      } else if (throwable instanceof ApiException) {
        errorStatus = HttpStatus.valueOf(((ApiException) throwable).getHttpStatus());
        r = createErrorResponse(requestContext, throwable);
      } else {
        r = createErrorResponse(requestContext, throwable);
        errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
      response.setStatus(errorStatus.value());
      return r;
    } catch (final Exception e) {
      logger.error("BUG - something went wrong while returning the status", e);
      return createErrorResponse(requestContext, e);
    }
  }

  /**
   * Creates an error response.
   *
   * @param rCtx The request context
   * @param t    The exception
   * @param <T>  The type of object that would normally be contained with our response (as results)
   * @return ImmutableMap&lt;String, Object&gt; The response
   */
  public static <T> Response<T> createErrorResponse(final RequestContext rCtx, final Throwable t) {
    final Metadata metadata = getResponseMeta(rCtx);
    final Metadata.MetadataBuilder builder = Metadata.builder();
    builder.merge(metadata);
    if (t != null) {
      if (t instanceof BadRequest || t instanceof HystrixBadRequestException) {
        builder.put("message", "Exception thrown processing request - " + t.getMessage());
      } else if (t instanceof UnauthorizedException || t instanceof HttpException401) {
        builder.put("message", "Unauthorized access:" + t.getMessage());
      } else if (t instanceof ForbiddenException || t instanceof HttpException403) {
        builder.put("message", "Forbidden access:" + t.getMessage());
      } else {
        builder.put("message", "Exception thrown processing request.");
      }
    }
    return Response.valueWithMeta(builder.build());
  }

  /**
   * Get our response meta.
   *
   * @param rCtx The request context
   * @return ImmutableMap&lt;String, Object&gt; The response
   */
  public static Metadata getResponseMeta(final RequestContext rCtx) {
    if (rCtx != null) {
      return Metadata.of("requestId", rCtx.getRequestId());
    } else {
      return new Metadata();
    }
  }
}
