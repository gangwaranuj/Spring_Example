package com.workmarket.domains.model.settings;

import java.math.BigDecimal;

public class FundingCompletenessPredicate implements CompletenessPredicate<BigDecimal> {

	@Override
	public boolean test(final BigDecimal bigDecimal) {
		return bigDecimal.compareTo(BigDecimal.ZERO) > 0;
	}
}
