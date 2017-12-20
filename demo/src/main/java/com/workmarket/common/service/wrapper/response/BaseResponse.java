package com.workmarket.common.service.wrapper.response;

import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.common.service.status.ResponseStatus;

public class BaseResponse implements Response {

	protected ResponseStatus status;

	public static BaseResponse success() {
		return new BaseResponse(BaseStatus.SUCCESS);
	}

	public BaseResponse() {
		status = BaseStatus.FAILURE;
	}

	public BaseResponse(ResponseStatus status) {
		this.status = status;
	}

	@Override
	public ResponseStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	@Override
	public boolean isSuccessful() {
		return status.isSuccessful();
	}
}
