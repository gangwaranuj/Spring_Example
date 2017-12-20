package com.workmarket.web.exceptions;

import com.workmarket.web.RestCode;

/**
 * Created by ianha on 6/2/14
 */
public class OnboardBaseException extends Exception {
	private RestCode code;

	public OnboardBaseException(RestCode code) {
		this.code = code;
	}

	public RestCode getCode() { return this.code; }
}
