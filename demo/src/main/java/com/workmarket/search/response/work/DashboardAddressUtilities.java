package com.workmarket.search.response.work;

import com.workmarket.domains.model.postalcode.PostalCodeUtilities;

public class DashboardAddressUtilities {
	public static String formatAddressShort(DashboardAddress a) {
		if (a == null) return "";
		return PostalCodeUtilities.formatAddressShort(a.getCity(), a.getState(), a.getPostalCode(), a.getCountry());
	}
}
