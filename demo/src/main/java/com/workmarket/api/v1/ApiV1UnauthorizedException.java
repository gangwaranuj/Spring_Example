package com.workmarket.api.v1;

import org.springframework.http.HttpStatus;

/**
 * Created by joshlevine on 1/23/17.
 */
public class ApiV1UnauthorizedException extends ApiV1Exception {
	public ApiV1UnauthorizedException(Exception ex) {
		super("Unauthorized", HttpStatus.UNAUTHORIZED.value(), ex);
	}
}
