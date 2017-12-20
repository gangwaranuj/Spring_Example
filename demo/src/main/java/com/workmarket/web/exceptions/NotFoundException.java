package com.workmarket.web.exceptions;

import com.workmarket.web.RestCode;
import com.workmarket.web.exceptions.OnboardBaseException;

/**
 * Created by ianha on 6/2/14
 */
public class NotFoundException extends OnboardBaseException {
	public NotFoundException() {
		super(RestCode.NOT_FOUND);
	}

	public NotFoundException(RestCode code) {
		super(code);
	}
}
