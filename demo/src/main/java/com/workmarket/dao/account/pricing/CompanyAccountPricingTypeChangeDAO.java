package com.workmarket.dao.account.pricing;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.pricing.CompanyAccountPricingTypeChange;

import java.util.Calendar;
import java.util.List;

public interface CompanyAccountPricingTypeChangeDAO extends DAOInterface<CompanyAccountPricingTypeChange> {

	/** Returns all pricing changed that occurred or should have occurred before the scheduleDate
	 * @param scheduledDate
	 * @param executed If true returns executed changes. If false returns non executed changes. If null returns both.
	 * @return */
	List<CompanyAccountPricingTypeChange> getCompanyAccountPricingTypeChangeScheduledBeforeDate(Calendar scheduledDate, boolean executed);

	CompanyAccountPricingTypeChange getCompanyAccountPricingTypeChange(long companyId, Calendar scheduleDate);

}
