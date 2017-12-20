package com.workmarket.web.controllers.onboarding;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.common.collect.ImmutableMap;
import com.workmarket.web.RestCode;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.BadRequestException;
import com.workmarket.web.exceptions.InternalServerErrorException;
import com.workmarket.web.exceptions.NotFoundException;
import com.workmarket.web.exceptions.OnboardBaseException;
import com.workmarket.web.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ianha on 6/2/14
 */
public class WorkerOnboardingBaseController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(WorkerOnboardingBaseController.class);

	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	@ExceptionHandler(BadRequestException.class)
	public @ResponseBody Map handleBadRequest(HttpServletRequest req, HttpServletResponse resp, Exception ex) {
		logException(ex);
		return formatResponse(ex);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND) // 404
	@ExceptionHandler(NotFoundException.class)
	public @ResponseBody Map handleNotFound(HttpServletRequest req, HttpServletResponse resp, Exception ex) {
		logException(ex);
		return formatResponse(ex);
	}

	@ExceptionHandler(Exception.class)
	public @ResponseBody Map handleAllRequest(HttpServletRequest req, HttpServletResponse resp, Exception ex) {
		logException(ex);

		if (ex instanceof MissingServletRequestParameterException) {
			resp.setStatus(400);
			return formatResponse(ex);
		} else if (ex instanceof BindException) {
			resp.setStatus(400);
			return formatValidationErrorResponse(((BindException) ex).getAllErrors());
		} else if (ex instanceof OnboardBaseException) {
			resp.setStatus(400);
			return formatResponse(ex);
		} else if (ex instanceof HttpMessageNotReadableException) {
			resp.setStatus(400);
			logger.error("Incorrect message payload format");
			return new HashMap() {{ put("message", "Incorrect payload format"); }};
		} else if (ex instanceof MethodArgumentNotValidException) {
			resp.setStatus(400);
			return formatValidationErrorResponse(((MethodArgumentNotValidException)ex).getBindingResult().getAllErrors());
		}

		resp.setStatus(500);
		return formatResponse(new InternalServerErrorException());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	@ExceptionHandler(ValidationException.class)
	public @ResponseBody Map handleValidationErrors(HttpServletRequest req, Exception ex) {
		logException(ex);
		return formatValidationErrorResponse(((ValidationException)ex).getErrors());
	}

	private void logException(Exception ex) {
		logger.error(ex.getMessage(), ex);
	}

	private Map formatValidationErrorResponse(List<ObjectError> errors) {
		List resultList = Lists.newArrayList();
		Map resultMap = Maps.newLinkedHashMap();

		for (ObjectError error : errors) {
			FieldError err = (FieldError) error;
			resultList.add(ImmutableMap.of("field", err.getField(), "message", error.getDefaultMessage()));
		}

		resultMap.put("errors", resultList);

		logger.error(resultMap.toString());

		return resultMap;
	}

	private Map formatResponse(Exception ex) {
		Map result = Maps.newLinkedHashMap();

		if (ex instanceof OnboardBaseException) {
			OnboardBaseException e = (OnboardBaseException) ex;
			RestCode code = e.getCode();
			result.put("message", code.getDescription());
			result.put("code", code.getValue());
		} else {
			result.put("message", ex.getMessage());
		}

		return result;
	}
}
