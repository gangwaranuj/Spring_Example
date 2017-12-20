package com.workmarket.thrift.core;

import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.utility.StringUtilities;

public class AddressUtilities {
	public static String formatAddress(Address a, String separator, boolean address2Break) {
		if (a == null) return "";
		if (address2Break)
			return PostalCodeUtilities.formatAddressLong(a.getAddressLine1(), a.getAddressLine2(), a.getCity(), a.getState(), a.getZip(), a.getCountry(), separator);
		return PostalCodeUtilities.formatAddressLong(StringUtilities.smartJoin(a.getAddressLine1(), " ", a.getAddressLine2()), null, a.getCity(), a.getState(), a.getZip(), a.getCountry(), separator);
	}

	public static String formatAddressLong(Address a) {
		if (a == null) return "";
		return PostalCodeUtilities.formatAddressLong(a.getAddressLine1(), a.getAddressLine2(), a.getCity(), a.getState(), a.getZip(), a.getCountry(), "<br/>");
	}

	public static String formatAddressShort(Address a) {
		if (a == null) return "";
		return PostalCodeUtilities.formatAddressShort(a.getCity(), a.getState(), a.getZip(), a.getCountry());
	}
}
