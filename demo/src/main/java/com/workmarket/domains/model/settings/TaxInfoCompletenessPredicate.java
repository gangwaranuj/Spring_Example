package com.workmarket.domains.model.settings;

import com.workmarket.domains.model.tax.AbstractTaxEntity;

public class TaxInfoCompletenessPredicate implements CompletenessPredicate<AbstractTaxEntity> {
	@Override
	public boolean test(final AbstractTaxEntity abstractTaxEntity) {
		return abstractTaxEntity != null &&
			abstractTaxEntity.getActiveFlag() == Boolean.TRUE;
	}
}
