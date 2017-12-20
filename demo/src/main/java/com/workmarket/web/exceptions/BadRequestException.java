package com.workmarket.web.exceptions;

import com.workmarket.web.RestCode;

/**
 * Created by ianha on 6/2/14
 */
public class BadRequestException extends OnboardBaseException {
	public BadRequestException() {
		super(RestCode.BAD_REQUEST);
	}

	public BadRequestException(RestCode code) {
		super(code);
	}
}
