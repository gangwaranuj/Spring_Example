package com.workmarket.domains.velvetrope.rope;

import com.workmarket.api.v2.employer.paymentconfiguration.models.PaymentConfigurationDTO;
import com.workmarket.velvetrope.Rope;
import org.apache.commons.lang.mutable.MutableBoolean;

public class OfflinePayAllRope implements Rope {
	private MutableBoolean payAllOffLine;

	public OfflinePayAllRope(final MutableBoolean payAllOffLine) {
		this.payAllOffLine = payAllOffLine;
	}

	@Override
	public void enter() {
		payAllOffLine.setValue(true);
	}
}