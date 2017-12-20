package com.workmarket.api.v2.employer.uploads.controllers;


import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.ApiV1Exception;
import com.workmarket.api.v2.employer.uploads.exceptions.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class BaseUploadsController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(BaseUploadsController.class);

	@ResponseStatus(HttpStatus.CONFLICT) // 409
	@ExceptionHandler(ConflictException.class)
	@ResponseBody
	// TODO API - this should throw a V2 compatible exception
	public Object conflict(Exception e) throws ApiV1Exception {
		logger.error("409 Conflict", e);
		throw new ApiV1Exception(e.getMessage(), HttpStatus.CONFLICT.value());
	}
}
