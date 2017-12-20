package com.workmarket.domains.model;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class AddressUtilities {
	//markers=color:blue|label:S|40.702147,-74.015794
	private static final String MARKER_FORMAT = "markers=color:%s|label:%s|%.6f,%.6f";

	public static String formatAddressLong(Address a) {
		return formatAddressLong(a, "<br/>");
	}

	public static String formatAddressLong(Address a, String separator) {
		if (a == null) return "";
		return PostalCodeUtilities.formatAddressLong(a.getAddress1(), a.getAddress2(), a.getCity(), a.getState() != null ? a.getState().getShortName() : "", a.getPostalCode(), a.getCountry().getId(), separator);
	}

	public static String formatAddressLongWithLongState(Address a, String separator) {
		if (a == null) { return ""; }
		return PostalCodeUtilities.formatAddressLong(a.getAddress1(), a.getAddress2(), a.getCity(), a.getState().getName(), a.getPostalCode(), a.getCountry().getId(), separator);
	}

	public static String formatAddressShort(Address a) {
		if (a == null) return "";
		return PostalCodeUtilities.formatAddressShort(a.getCity(), a.getState().getShortName(), a.getPostalCode(), a.getCountry().getId());
	}

	public static String getGoogleMapsMarkersFromAddressList(List<Address> addressList, String color) {
		if (addressList == null) {
			return null;
		}

		List<String> markerStrings = Lists.newArrayList();
		int count = 1;
		for (Address address : addressList) {
			CollectionUtils.addIgnoreNull(markerStrings, getGoogleMapsMarkerFromAddress(address, "" + (count++), "orange"));
		}

		return org.apache.commons.lang.StringUtils.join(markerStrings, "&");
	}

	public static String getGoogleMapsMarkerFromAddress(Address address, String label, String color) {
		if (address == null || address.getLatitude() == null || address.getLongitude() == null) {
			return null;
		}

		return String.format(MARKER_FORMAT, color, label, address.getLatitude().floatValue(), address.getLongitude().floatValue());
	}
}
