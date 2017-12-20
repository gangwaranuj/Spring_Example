package com.workmarket.dto;

import com.workmarket.domains.model.postalcode.PostalCodeUtilities;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class AddressDTOUtilities {
	public static String formatAddressForGeocoder(final AddressDTO a) {
		if (a == null) return "";

		final String postalCode = isEmpty(a.getPostalCode()) ? "" : a.getPostalCode();
		final String city = isEmpty(a.getCity()) ? "" : a.getCity();
		final String state = isEmpty(a.getState()) ? "" : a.getState();
		final String country = isEmpty(a.getCountry()) ? "" : a.getCountry();

		return PostalCodeUtilities.formatAddressForGeocoder(a.getAddress1(), a.getAddress2(), city, state, postalCode, country);
	}

	public static String formatAddressLong(AddressDTO a) {
		return formatAddressLong(a, "<br/>");
	}

	public static String formatAddressLong(AddressDTO a, String separator) {
		if (a == null) return "";
		return PostalCodeUtilities.formatAddressLong(a.getAddress1(), a.getAddress2(), a.getCity(), a.getState(), a.getPostalCode(), a.getCountry(), separator);
	}

	public static String formatAddressShort(AddressDTO a) {
		if (a == null) return "";
		return PostalCodeUtilities.formatAddressShort(a.getCity(), a.getState(), a.getPostalCode(), a.getCountry());
	}
}
