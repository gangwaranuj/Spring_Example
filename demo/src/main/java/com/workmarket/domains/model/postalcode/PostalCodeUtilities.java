package com.workmarket.domains.model.postalcode;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.workmarket.integration.autotask.util.StringUtil;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;
import java.util.regex.Matcher;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

public class PostalCodeUtilities {
	private static final String POSTAL_CODE_PATTERN = "\\d{5}(-\\d{4})?";
	private static final Logger logger = LoggerFactory.getLogger(PostalCodeUtilities.class);

	public static final String SKIP_ADDRESS2_FOR_GEOCODING_REDIS_FLAG = "skip-address2-for-geocoding";

	/**
	 * Google prefers the postal code on the end for Canada, plus the given spacing
	 */
	public static String formatAddressForGeocoder(final String address1, final String address2, final String city, final String state, final String postalCode, final String country) {

		logger.info(String.format("Dropping address2 from %s, %s, %s, %s %s, %s", address1, address2, city, state, postalCode, country));

		if (StringUtilities.equalsAny(country, Country.USA, Country.newInstance(Country.USA).getName()))
			return String.format("%s, %s, %s %s", address1, city, state, postalCode);
		if (StringUtilities.equalsAny(country, Country.CANADA, Country.newInstance(Country.CANADA).getName()))
			return String.format("%s, %s, %s %s, %s", address1, city, state, Country.valueOf(country).getName(), formatCanadianPostalCode(postalCode));

		Country countryObject = Country.valueOf(country);
		if (!countryObject.equals(Country.newInstance(Country.WITHOUTCOUNTRY)))
			return String.format("%s, %s %s, %s", address1, postalCode, city, Country.valueOf(country).getName()); // TODO: Resolve country name from DB/cache

		return String.format("%s %s", postalCode, city);
	}

	public static String formatAddressShort(String city, String state, String postalCode, String country) {
		if (StringUtilities.none(city, state, postalCode, country)) return "";
		if (StringUtils.equals(Country.USA, country) || StringUtils.equals(Country.US, country) || StringUtils.equals(Country.CANADA, country))
			return String.format("%s, %s %s", city, state, postalCode);
		return String.format("%s %s, %s", postalCode, city, StringUtilities.toPrettyName(Country.valueOf(country).getName()));
	}

	/**
	 * Adds the space if it does not already exist
	 *
	 * @return
	 */
	public static String formatCanadianPostalCode(String code) {
		Assert.notNull(code);

		Matcher matcher = PostalCode.canadaPattern.matcher(code);

		Assert.isTrue(matcher.matches());

		if(StringUtil.isNullOrEmpty(matcher.group(2))) {
			return matcher.group(1);
		} else {
			return matcher.group(1) + " " + matcher.group(2);
		}
	}

	public static String normalizePostalCodeInternational(String postalCode, String country) {
		if (StringUtils.isBlank(postalCode) || StringUtils.isBlank(country)) {
			return StringUtils.EMPTY;
		}
		postalCode = StringUtils.strip(postalCode);
		postalCode = StringUtils.deleteWhitespace(postalCode);
		postalCode = StringUtils.remove(postalCode, "-");
		if (country.equals(Country.USA) || country.equals(Country.US)) {
			//US zip code
			if (postalCode.length() < 5) {
				return StringUtils.leftPad(postalCode, 5, "0");
			}
			if (postalCode.length() > 5) {
				return postalCode.substring(0, 5);
			}

		} else if (country.equals(Country.CANADA) || country.equals(Country.CANADA_COUNTRY.getISO())) {
			//Canada format ANA NAN
			if (postalCode.length() > 6) {
				return postalCode.substring(0, 6);
			}
		}
		return postalCode;
	}

	public static boolean isValidPostalCode(String zip) {
		if (isEmpty(zip)) {
			return false;
		}
		return trimToEmpty(zip).matches(POSTAL_CODE_PATTERN);
	}

	public static String formatAddressLong(String address1, String address2, String city, String state, String postalCode, String country, String separator) {
		List<String> filteredList = Lists.newArrayList(
			StringEscapeUtils.escapeXml(address1),
			StringEscapeUtils.escapeXml(address2),
			formatAddressShort(city, state, postalCode, country));

		filteredList.removeAll(Lists.newArrayList(""));

		return Joiner.on(separator).skipNulls().join(filteredList);
	}
}
