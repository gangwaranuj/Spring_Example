package com.workmarket.web.exceptions;

import com.workmarket.web.RestCode;
import com.workmarket.web.exceptions.OnboardBaseException;

/**
 * Created by ianha on 6/2/14
 */
public class InternalServerErrorException extends OnboardBaseException {
	public InternalServerErrorException() {
		super(RestCode.INTERNAL_SERVER_ERROR);
	}
}
