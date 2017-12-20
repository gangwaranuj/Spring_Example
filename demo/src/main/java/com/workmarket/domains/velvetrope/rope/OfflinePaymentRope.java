package com.workmarket.domains.velvetrope.rope;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;
import com.workmarket.api.v2.employer.paymentconfiguration.models.PaymentConfigurationDTO;
import com.workmarket.velvetrope.Rope;

public class OfflinePaymentRope implements Rope {
	private PaymentConfigurationDTO.Builder paymentConfigurationDTOBuilder;

	public OfflinePaymentRope(final PaymentConfigurationDTO.Builder paymentConfigurationDTOBuilder) {
		this.paymentConfigurationDTOBuilder = paymentConfigurationDTOBuilder;
	}

	@Override
	public void enter() {
		paymentConfigurationDTOBuilder.setOfflinePaymentEnabled(true);
	}
}