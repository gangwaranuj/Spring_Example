package com.workmarket.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.workmarket.api.exceptions.ApiException;
import com.workmarket.api.exceptions.ApiRateLimitException;
import com.workmarket.api.exceptions.BadRequestApiException;
import com.workmarket.api.exceptions.ForbiddenException;
import com.workmarket.api.exceptions.GenericApiException;
import com.workmarket.api.exceptions.IncrementException;
import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.exceptions.NotFoundApiException;
import com.workmarket.api.exceptions.ResourceNotFoundException;
import com.workmarket.api.exceptions.ResourceValidationException;
import com.workmarket.api.exceptions.UnauthorizedException;
import com.workmarket.api.exceptions.UnparseableUriException;
import com.workmarket.api.exceptions.UnprocessableEntityException;
import com.workmarket.api.v1.ApiHelper;
import com.workmarket.api.v1.ApiV1Exception;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.User;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.thrift.work.WorkAuthorizationException;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException403;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.exceptions.MobileHttpException401;
import com.workmarket.web.exceptions.ValidationException;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

/**
 * Base class for all web controllers, includes methods for generating a response pagination object, error
 * handling, and other common controller functions
 */
public class ApiBaseController {
	public static final String MESSAGE_OK = "Success";

	private static final Log logger = LogFactory.getLog(ApiBaseController.class);

	protected static final String METADATA_MESSAGE_KEY = "message";
	protected static final String METADATA_CODE_KEY = "code";

	@Autowired private AuthenticationService authenticationService;
	@Autowired protected MessageBundleHelper messageHelper;
	@Autowired private ExtendedUserDetailsService extendedUserDetailsService;
	@Autowired private ApiHelper apiHelper;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Object handleBaseApiExceptions(Exception ex, HttpServletResponse response) throws Exception {
		if (ex instanceof ApiV1Exception) {
			return handleApiV1Exception(ex, response);
		}
		return handleApiV2Exception(ex, response);
	}

	private Object handleApiV1Exception(Exception ex, HttpServletResponse response) {
		ApiV1Response apiV1Response;
		ApiV1Exception exi = (ApiV1Exception) ex;
		logger.warn("ApiBaseController caught V1 exception: "
								+ ex.getMessage(), ex);
		MessageBundle bundle = messageHelper.newBundle();
		apiV1Response = new ApiV1Response();
		logger.error(apiHelper.getErrorDescription(exi));
		messageHelper.addError(bundle, "api.general_error", webRequestContextProvider.getWebRequestContext().getRequestId());
		apiV1Response.setSuccessful(false);
		apiV1Response.getMeta().setErrors(bundle.getErrors());
		apiV1Response.getMeta().setStatusCode(exi.getStatusCode());
		response.setStatus(exi.getStatusCode());
		return apiV1Response;
	}


	private Object handleApiV2Exception(Exception ex, HttpServletResponse response) {
		ApiV2Response apiV2Response;
		if (ex instanceof HttpException401
				|| ex instanceof MobileHttpException401
				|| ex instanceof UnauthorizedException) {
			apiV2Response = ApiV2Response.valueWithMessage(ex.getMessage(), HttpStatus.UNAUTHORIZED);
		} else if (ex instanceof HttpException403
							 || ex instanceof HttpException404
							 || ex instanceof NotFoundApiException
							 || ex instanceof WorkAuthorizationException
							 || ex instanceof ResourceNotFoundException
							 // TODO API - IllegalArgumentException is thrown by spring's "@PreAuthorize" annotation.
							 // We either need to change that exception type, or reserve the use of IllegalArgumentExceptions for permissions/security purposes
							 || ex instanceof IllegalArgumentException
							 || ex instanceof AccessDeniedException
							 || ex instanceof ForbiddenException) {
			apiV2Response = ApiV2Response.valueWithMessage(ex.getMessage(), HttpStatus.FORBIDDEN);
		} else if (ex instanceof HttpException400) {
			apiV2Response = ApiV2Response.valueWithMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
		} else if (ex instanceof ResourceValidationException
							 || ex instanceof MissingServletRequestParameterException
							 || ex instanceof TypeMismatchException
							 || ex instanceof HttpMessageNotReadableException
							 || ex instanceof MessageSourceApiException
							 || ex instanceof BadRequestApiException) {

			apiV2Response = ApiV2Response.valueWithMessage(messageHelper.getMessage(ex.getMessage()), HttpStatus.BAD_REQUEST);
		} else if (ex instanceof GenericApiException) {
			GenericApiException exi = (GenericApiException) ex;
			apiV2Response = ApiV2Response.valueWithMessageAndResults(messageHelper.getMessage(ex.getMessage()),
					ImmutableList.copyOf(exi.getErrors()),
					HttpStatus.BAD_REQUEST);
		} else if (ex instanceof UnparseableUriException
							 || ex instanceof ApiException) {
			apiV2Response = ApiV2Response.valueWithMessage(messageHelper.getMessage(ex.getMessage()), HttpStatus.BAD_REQUEST);
		} else if (ex instanceof ValidationException) {
			ValidationException exi = (ValidationException) ex;
			ObjectError error = exi.getErrors().get(0); // return the first error message

			if (error instanceof FieldError) {
				FieldError err = (FieldError) error;
				apiV2Response = ApiV2Response.valueWithMessage(String.format(
								"Invalid/out of range field value '%s' for field '%s'",
								err.getRejectedValue(),
								err.getField()), HttpStatus.BAD_REQUEST);
			} else {
				apiV2Response = ApiV2Response.valueWithMessage(error.getDefaultMessage(), HttpStatus.BAD_REQUEST);
			}
		} else if (ex instanceof BindException) {
			BindException exi = (BindException) ex;
			List<ObjectError> bindingErrors = exi.getBindingResult().getAllErrors();
			List<ApiBaseError> errors;

			errors = convertObjectErrors(bindingErrors);
			apiV2Response = ApiV2Response.valueWithMessageAndResults("",
				ImmutableList.copyOf(errors),
				HttpStatus.BAD_REQUEST);
		} else if (ex instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException exi = (MethodArgumentNotValidException) ex;
			List<ObjectError> bindingErrors = exi.getBindingResult().getAllErrors();

			List<ApiBaseError> errors = convertObjectErrors(bindingErrors);
			apiV2Response = ApiV2Response.valueWithMessageAndResults(ex.getMessage(),
																															 ImmutableList.copyOf(errors),
																															 HttpStatus.BAD_REQUEST);

		} else if (ex instanceof com.workmarket.thrift.core.ValidationException) {
			com.workmarket.thrift.core.ValidationException exi = (com.workmarket.thrift.core.ValidationException) ex;

			BindingResult bindingResult = ThriftValidationMessageHelper.buildBindingResult(exi);
			List<ApiBaseError> errors = convertObjectErrors(bindingResult.getAllErrors());
			apiV2Response = ApiV2Response.valueWithMessageAndResults("Validation error: "
							+ ex.getMessage(),
					ImmutableList.copyOf(errors),
					HttpStatus.BAD_REQUEST);
		} else if (ex instanceof ApiRateLimitException) {
			ApiRateLimitException exi = (ApiRateLimitException) ex;

			// TODO API - this seems unnecessary to load a Calendar here.
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			calendar.clear();
			calendar.setTime(new Date());
			long secondsSinceEpoch = calendar.getTimeInMillis()
															 / 1000L;
			response.addHeader("X-Rate-Limit-Limit", String.valueOf(exi.getLimit()));
			response.addHeader("X-Rate-Limit-Reset",
												 String.valueOf(exi.getExpiryInSeconds()
																				- (secondsSinceEpoch
																					 % exi.getExpiryInSeconds())));
			apiV2Response = ApiV2Response.valueWithMessage(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
		} else if (ex instanceof IncrementException) {
			apiV2Response = ApiV2Response.valueWithMessage("Rate limit increment exception: "
																										 + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (ex instanceof UnprocessableEntityException) {
			apiV2Response = ApiV2Response.valueWithMessageAndResults(
					"Validation error.", ((UnprocessableEntityException)ex).getErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
		} else {
			apiV2Response = ApiV2Response.valueWithMessage(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (apiV2Response.getMeta().getStatusCode()
				== HttpStatus.INTERNAL_SERVER_ERROR.value()) {
			logger.error("ApiBaseController caught unhandled exception: "
									 + ex.getMessage(), ex);
		} else {
			logger.warn("ApiBaseController caught exception: "
									+ ex.getMessage(), ex);
		}

		response.setStatus(apiV2Response.getMeta().getStatusCode());
		return apiV2Response;
	}

	protected ExtendedUserDetails getCurrentUser() {
		User user = authenticationService.getCurrentUser();
		if (user
				!= null) {
			return (ExtendedUserDetails) extendedUserDetailsService.loadUser(user);
		}
		return null;
	}

	// Enables testing
	public void setMessageHelper(MessageBundleHelper messageHelper) {
		this.messageHelper = messageHelper;
	}


	private List<ApiBaseError> convertObjectErrors(List<ObjectError> bindingErrors) {
		List<ApiBaseError> errors = new ArrayList<>();
		for (ObjectError err : bindingErrors) {

			if (err instanceof FieldError) {
				FieldError fieldError = (FieldError) err;
				errors.add(new ApiBaseError(fieldError.getCode(),
																		messageHelper.getMessage(fieldError),
																		fieldError.getField(),
					decorateObjectName(fieldError)));
			} else {
				errors.add(new ApiBaseError(err.getCode(), messageHelper.getMessage(err)));
			}

		}
		return errors;
	}

	private String decorateObjectName(FieldError fieldError) {
		String objectName = fieldError.getObjectName();
		if(objectName.startsWith("api")) {
			objectName = objectName.substring(3);
		}
		if(objectName.endsWith("DTO")) {
			objectName = objectName.substring(0, objectName.length() - 3);
		}
		return objectName;
	}


	protected <T> Set<T> collectSet(Collection<T> builders) {
		if (builders
				== null) {
			return ImmutableSet.of();
		}
		return ImmutableSet.copyOf(builders);
	}

	protected <T> List<T> collectList(Collection<T> builders) {
		if (builders
				== null) {
			return ImmutableList.of();
		}
		return ImmutableList.copyOf(builders);
	}

}
