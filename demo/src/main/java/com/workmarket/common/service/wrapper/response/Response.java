package com.workmarket.common.service.wrapper.response;

import com.workmarket.common.service.status.ResponseStatus;

/**
 * Created by nick on 4/23/13 9:29 AM
 */
public interface Response {
	ResponseStatus getStatus();

	void setStatus(ResponseStatus status);

	boolean isSuccessful();
}
