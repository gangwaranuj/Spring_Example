package com.workmarket.service.helpers;

import com.workmarket.helpers.ResponseBuilderBase;

public class ServiceResponseBuilder extends ResponseBuilderBase<ServiceResponseBuilder> {
	public boolean failed() {
		return ! isSuccessful();
	}
}
