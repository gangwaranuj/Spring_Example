package com.workmarket.service.external;

import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

public class ShippingProviderDetectResponse {

	private HttpStatus responseCode;
	private boolean successful;
	private List<ShippingProvider> shippingProviders = Collections.emptyList();

	public ShippingProviderDetectResponse() {}

	public HttpStatus getResponseCode() {
		return responseCode;
	}

	public ShippingProviderDetectResponse setResponseCode(HttpStatus responseCode) {
		this.responseCode = responseCode;
		return this;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public ShippingProviderDetectResponse setSuccessful(boolean successful) {
		this.successful = successful;
		return this;
	}

	public List<ShippingProvider> getShippingProviders() {
		return shippingProviders;
	}

	public ShippingProvider getShippingProvider() {
		if (CollectionUtils.isNotEmpty(shippingProviders)) {
			return CollectionUtilities.first(shippingProviders);
		}
		return ShippingProvider.OTHER;
	}

	public ShippingProviderDetectResponse setShippingProviders(List<ShippingProvider> shippingProviders) {
		this.shippingProviders = shippingProviders;
		return this;
	}

	public static ShippingProviderDetectResponse fail() {
			return new ShippingProviderDetectResponse()
				.setSuccessful(false);
		}

	public static ShippingProviderDetectResponse badRequest() {
		return new ShippingProviderDetectResponse()
			.setResponseCode(HttpStatus.BAD_REQUEST)
			.setSuccessful(false);
	}

	public static ShippingProviderDetectResponse serverError() {
		return new ShippingProviderDetectResponse()
			.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR)
			.setSuccessful(false);
	}
}
