package com.workmarket.utility;

import com.amazonaws.util.json.JSONObject;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.workmarket.configuration.Constants;
import com.workmarket.vault.models.Secured;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jvnet.inflector.Noun;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtilities {

	private static final String[] INVALID_SSN_PREFIXES = {"000", "666", "9"};
	private static final String[] INVALID_EIN_PREFIXES =
			{"00", "07", "08", "09", "17", "18", "19", "28", "29", "49", "69", "70", "78", "79", "89"};

	public static final Whitelist WHITELIST_XSS;

	static {
		WHITELIST_XSS = Whitelist
				.basic()
				.addTags("div")
				.addAttributes("a", "href", "target")
				.addAttributes("span", "class")
				.preserveRelativeLinks(true);
	}

	private StringUtilities() {
	}

	public static String toYesNo(Boolean value) {
		return BooleanUtils.toStringYesNo(value);
	}

	public static boolean isNotEmpty(String s) {
		return StringUtils.isNotEmpty(s);
	}

	public static boolean isLengthBetween(String s, int lower, int upper) {
		return (s != null && s.length() >= lower && s.length() <= upper);
	}

	public static String urlEncode(String value) {
		return urlEncode(value, "UTF-8");
	}

	public static String urlEncode(String value, String charset) {
		try {
			if (value == null) return "";
			return URLEncoder.encode(value, charset);
		} catch (UnsupportedEncodingException e) {
			return value;
		}
	}

	public static String urlToFilenameValidateAndDecode(String value) {
		try {
			return StringUtilities.validateURLPattern(value) ? StringUtilities.urlDecode(value) : value;
		} catch (IllegalArgumentException e) {
			return value;
		}
	}

	public static String urlDecode(String value) {
		return urlDecode(value, "UTF-8");
	}

	public static String urlDecode(String value, String charset) {
		try {
			return URLDecoder.decode(value, charset);
		} catch (UnsupportedEncodingException e) {
			return value;
		}
	}

	public static String stripUriProtocol(String value) {
		if (StringUtils.isEmpty(value))
			return null;
		return StringUtils.removeStartIgnoreCase(StringUtils.removeStartIgnoreCase(value, "http:"), "https:");
	}

	public static String stripTrailingSlashes(String s) {
		if (s != null)
			s = s.replaceAll("/+$", "");
		return s;
	}

	public static String encodeHex(byte[] bytes) {
		Assert.notNull(bytes);
		return new String(Hex.encodeHex(bytes));
	}

	public static Integer parseInteger(String s) {
		if (NumberUtils.isNumber(s))
			return NumberUtils.createInteger(s);
		return null;
	}

	public static Long parseLong(String s) {
		if (NumberUtils.isNumber(s))
			return NumberUtils.createLong(s);
		return null;
	}

	public static String fullName(String firstName, String lastName) {
		return smartJoin(firstName, " ", lastName);
	}

	public static String fullName(String firstName, String middleName, String lastName) {
		return smartJoin(smartJoin(firstName, " ", middleName), " ", lastName);
	}

	public static String[] split(String s, String separator) {
		String[] a = StringUtils.split(s, separator);
		if (a == null)
			return new String[]{};
		return StringUtils.stripAll(a);
	}

	public static String truncate(String s, int length) {
		return StringUtils.substring(s, 0, length);
	}

	public static String truncate(String s, int length, String end) {
		return truncate(s, length, -1, end);
	}

	public static String truncate(String s, int lower, int upper, String end) {
		return WordUtils.abbreviate(s, lower, upper, end);
	}

	public static String processForSearch(String s) {
		return StringUtils.defaultString(s, "");
	}

	public static String smartJoin(String s1, String j, String s2) {
		if (StringUtils.isBlank(s1) && StringUtils.isBlank(s2))
			return "";
		if (StringUtils.isBlank(s2))
			return s1;
		if (StringUtils.isBlank(s1))
			return s2;
		return s1 + j + s2;
	}

	public static String defaultString(String s, String defaultString) {
		if (StringUtils.equals(s, "null"))
			return defaultString;
		return StringUtils.defaultString(s, defaultString);
	}

	public static String defaultString(Object o, String defaultString) {
		if (o == null) return defaultString;
		return defaultString(o.toString(), defaultString);
	}

	// TODO Switch to Google's libphonenumber
	// @see https://code.google.com/p/libphonenumber/
	public static String standardizePhoneNumber(String phoneNumber) {
		Assert.notNull(phoneNumber);
		Assert.hasLength(phoneNumber);
		Assert.isTrue(phoneNumber.length() > 7);

		String number = phoneNumber.replaceAll("[^0-9]", "");
		number = number.replaceAll("^1", "");

		Assert.isTrue(number.length() == 10);

		return number;
	}

	public static String formatPhoneNumber(String phoneNumber) {
		try {
			PhoneNumberUtil util = PhoneNumberUtil.getInstance();
			Phonenumber.PhoneNumber n = util.parse(phoneNumber, Locale.US.getCountry());
			return util.format(n, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
		} catch (NumberParseException e) {
			return phoneNumber;
		}
	}

	public static String formatPhoneNumber(String phoneNumber, String internationalCode, String extension) {
		if (!isNotEmpty(phoneNumber))
			return "";

		String phone = "+" + internationalCode + " " + formatPhoneNumber(phoneNumber);

		if (extension != null) {
			phone += " ext." + extension;
		}

		return phone;
	}

	/**
	 * Format phone number in E.164 format (used by Twilio). For now, assumes the US/Canada international country code.
	 *
	 * @see http://en.wikipedia.org/wiki/E.164
	 */
	public static String formatE164PhoneNumber(String phoneNumber) {
		return String.format("+1%s", standardizePhoneNumber(phoneNumber));
	}

	public static String formatPhoneNumberForIvr(String phoneNumber) {
		return standardizePhoneNumber(phoneNumber).replace("", " ").trim();
	}

	public static String getSecureSsn(String value) {
		value = removePrepend(value);
		return String.format("*****%s", org.apache.commons.lang3.StringUtils.substring(value, 5));
	}

	public static String formatSecureSsn(String value) {
		return formatSsn(getSecureSsn(value), true);
	}

	public static String formatSsn(String value) {
		return formatSsn(value, false);
	}

	public static String formatSsn(String value, boolean allowNonNumbers) {
		value = removePrepend(value);

		if (StringUtils.isBlank(value)) { return value; }

		if (!allowNonNumbers) {
			value = value.replaceAll("[^0-9]", "");
		}

		return String.format("%s-%s-%s", StringUtils.substring(value, 0, 3), StringUtils.substring(value, 3, 5),
			StringUtils.substring(value, 5));
	}

	public static String removePrepend(String value) {
		if (!StringUtils.isBlank(value) && value.startsWith(Secured.PREPEND_MASKING_PATTERN)) {
			return value.substring(Secured.PREPEND_MASKING_PATTERN.length());
		}

		return value;
	}

	public static String getSecureEin(String value) {
		value = removePrepend(value);
		return String.format("*****%s", org.apache.commons.lang3.StringUtils.substring(value, 5));
	}

	public static String formatSecureEin(String value) {
		return formatEin(getSecureEin(value), true);
	}

	public static String formatEin(String value) {
		return formatEin(value, false);
	}

	public static String formatEin(String value, boolean allowNonNumbers) {
		value = removePrepend(value);
		if (StringUtils.isBlank(value)) return value;
		if (!allowNonNumbers) {
			value = value.replaceAll("[^0-9]", "");
		}
		return String.format("%s-%s", StringUtils.substring(value, 0, 2), StringUtils.substring(value, 2));
	}

	public static String getSecureCanadaBn(String value) {
		value = removePrepend(value);
		return String.format("***********%s", org.apache.commons.lang3.StringUtils.substring(value, 11));
	}

	public static String formatSecureCanadaBn(String value) {
		return formatCanadaBn(getSecureCanadaBn(value), true);
	}
	public static String formatCanadaBn(String value) {
		return formatCanadaBn(value, false);
	}

	public static String formatCanadaBn(String value, boolean allowNonAlphaNumeric) {
		value = removePrepend(value);
		if (StringUtils.isBlank(value)) return value;
		if (!allowNonAlphaNumeric) {
			value = value.replaceAll("[^0-9A-Za-z]", "");
		}
		return String.format("%s-%s-%s", StringUtils.substring(value, 0, 9), StringUtils.substring(value, 9, 11), StringUtils.substring(value, 11));
	}

	public static String getSecureCanadaSin(String value) {
		value = removePrepend(value);
		return String.format("******%s", StringUtils.substring(value, 6));
	}

	public static String formatSecureCanadaSin(String value) {
		return formatCanadaSin(getSecureCanadaSin(value), true);
	}

	public static String formatCanadaSin(String value) {
		return formatCanadaSin(value, false);
	}


	public static String formatCanadaSin(String value, boolean allowNonNumbers) {
		value = removePrepend(value);
		if (StringUtils.isBlank(value)) return value;
		if (!allowNonNumbers) {
			value = value.replaceAll("[^0-9]", "");
		}
		return String.format("%s-%s-%s", StringUtils.substring(value, 0, 3), StringUtils.substring(value, 3, 6), StringUtils.substring(value, 6));
	}

	public static String getSecureForeignTaxNumber(String value) {
		value = removePrepend(value);
		StringBuilder str = new StringBuilder();
		if (value.length() <= 4) {
			return "****";
		}
		for (int i = 0; i < value.length() - 4; i++) {
			str.append("*");
		}
		str.append(value.substring(value.length() - 4));
		return str.toString();
	}

	public static String formatSecureForeignTaxNumber(String value) {
		return formatForeignTaxNumber(getSecureForeignTaxNumber(value), true);
	}

	public static String formatForeignTaxNumber(String value) {
		return formatForeignTaxNumber(value, false);
	}

	public static String formatForeignTaxNumber(String value, boolean allowNonNumbers) {
		value = removePrepend(value);
		if (StringUtils.isBlank(value)) return value;
		if (!allowNonNumbers) {
			value = value.replaceAll("[^0-9]", "");
		}
		return value;
	}

	public static boolean same(String s1, String s2) {
		return (s1 == null ? s2 == null : s1.equals(s2));
	}

	public static int getStringIndex(String[] strings, String s) {
		Assert.notNull(strings);
		Assert.noNullElements(strings);
		Assert.notNull(s);
		Assert.isTrue(strings.length > 0);

		int i = -1;
		for (i = 0; i < strings.length; i++)
			if (s.equals(strings[i]))
				break;

		Assert.isTrue(i < strings.length);
		return i;

	}

	public static String[] quote(String[] a) {
		Assert.notNull(a);
		Assert.noNullElements(a);
		for (int i = 0; i < a.length; i++)
			a[i] = "\"" + a[i] + "\"";
		return a;
	}

	public static List<String> quoteAll(List<String> subqueries, String left, String right) {
		Assert.notNull(subqueries);
		Assert.noNullElements(subqueries.toArray());
		Assert.hasText(left);
		Assert.hasText(right);

		List<String> list = Lists.newArrayList();

		for (String s : subqueries) {
			list.add(left + s + right);
		}

		return list;
	}

	public static Boolean validateWebAddressPatternWithOptionalProtocol(String s) {
		//Default protocol is http://
		if(s.equals(StringUtils.EMPTY)) {
			return false;
		}

		String url = s.trim();
		if(!(url.startsWith("https://") || url.startsWith("http://"))) {
			//Defaults to http if no protocol is mentioned.
			url = "http://" + url;
		}

		String[] schemes = CollectionUtilities.newArray("http", "https");
		UrlValidator urlValidator = new UrlValidator(schemes);

		return urlValidator.isValid(url);
	}

	public static Boolean validateURLPattern(String s) {
		String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		Pattern patt = Pattern.compile(regex);
		Matcher matcher = patt.matcher(s);

		return matcher.matches();
	}

	// Address Formatters

	public static Boolean validatePostalCodePattern(String s) {
		String regex = "^\\d{5}(-\\d{4})?$";
		Pattern patt = Pattern.compile(regex);
		Matcher matcher = patt.matcher(s);

		return matcher.matches();
	}

	/* 999-99-9999 */
	public static Boolean isUsaSsn(String s) {

		if (StringUtils.isEmpty(s) || s.length() < 9) { return false; }

		// check for all the same digit
		if (Pattern.compile("^(\\d)\\1{2}[-\\s]?\\1{2}[-\\s]?\\1{4}$").matcher(s).matches()) { return false; }

		// check for all sequential
		if (Pattern.compile("^123[-\\s]45[-\\s]?6789$").matcher(s).matches()) {return false; }

		// check for invalid prefixes
		for (String prefix : INVALID_SSN_PREFIXES) { if (StringUtils.startsWith(s, prefix)) { return false; } }

		// check for zero group
		if (StringUtils.startsWith(s, "000") || StringUtils.endsWith(s, "0000")
				|| Pattern.compile("^\\d{3}[-\\s]00[-\\s]?\\d{4}$").matcher(s).matches()) {
			return false;
		}

		// check for reserved SSNs
		if (Pattern.compile("^987[-\\s]?65[-\\s]432\\d$").matcher(s).matches()) {
			return false;
		}

		return Pattern.compile("^\\d{3}[-\\s]?\\d{2}[-\\s]?\\d{4}$").matcher(s).matches();
	}

	/* 99-9999999 */
	public static Boolean isUsaEin(String s) {

		if (StringUtils.isEmpty(s) || s.length() < 9) {
			return false;
		}

		// check for all the same digit
		if (Pattern.compile("^(\\d)\\1[-\\s]?\\1{7}$").matcher(s).matches()) {
			return false;
		}

		// check for all sequential
		if (Pattern.compile("^12[-\\s]3456789$").matcher(s).matches()
				|| Pattern.compile("^98[-\\s]7654321$").matcher(s).matches()) {
			return false;
		}

		// check for invalid prefixes
		for (String prefix : INVALID_EIN_PREFIXES) { if (StringUtils.startsWith(s, prefix)) { return false; } }

		return Pattern.compile("^\\d{2}[-\\s]?\\d{7}$").matcher(s).matches();
	}

	/**
	 * An Individual Taxpayer Identification Number (ITIN) is a nine-digit number that:
	 * - always begins with the number 9
	 * - has a range of 70-88, 90-92, or 94-99 in the fourth and fifth digit.
	 */
	public static boolean isUsaItin(String s) {
		if (StringUtils.isEmpty(s) || s.length() < 9) {
			return false;
		}

		// check validity
		return Pattern.compile("^9\\d{2}[-\\s]((7[0-9])|(8[0-8])|(9[0-24-9]))[-\\s]?\\d{4}$").matcher(s).matches();
	}

	/* ssn, ein or itin*/
	public static boolean isUsaTaxIdentificationNumber(String s) {
		return isUsaSsn(s) || isUsaEin(s) || isUsaItin(s);
	}

	/* ssn or itin */
	public static boolean isUsaIndividualTaxIdentificationNumber(String s) {
		return isUsaSsn(s) || isUsaItin(s);
	}

	/* 999-999-999 + Luhn algorithm*/
	public static Boolean isCanadaSin(String s) {
		if (!Pattern.compile("^\\d{3}[-\\s]?\\d{3}[-\\s]?\\d{3}$").matcher(s).matches())
			return false;
		return validateLuhn(s.replaceAll("[-\\s]", ""));
	}

	/* 999999999-XX-9999 where XX = R[TPCMRDENGZ] */
	public static Boolean isCanadaBn(String s) {
		// APP-9874
		//return Pattern.compile("^\\d{9}[-\\s]R[TPCMRDENGZ][-\\s]\\d{4}$", Pattern.CASE_INSENSITIVE).matcher(s).matches();
		return Pattern.compile("^\\d{9}[-\\s][A-Z][A-Z][-\\s]\\d{4}$", Pattern.CASE_INSENSITIVE).matcher(s).matches();
	}

	/**
	 * Computes the Luhn Algorithm on a numeric string
	 * http://en.wikipedia.org/wiki/Luhn_algorithm
	 */
	public static boolean validateLuhn(String number) {
		if (!StringUtils.isNumeric(number)) return false;
		int sum = 0;
		boolean alternate = false;
		for (int i = number.length() - 1; i >= 0; i--) {
			int n = Integer.parseInt(number.substring(i, i + 1));
			if (alternate) {
				n *= 2;
				if (n > 9) {
					n = (n % 10) + 1;
				}
			}
			sum += n;
			alternate = !alternate;
		}
		return (sum % 10 == 0);
	}


	public static String processForLike(String s) {
		return "%" + s.replaceAll("'", "").replaceAll("%", "").replaceAll(" ", "%") + "%";
	}

	public static boolean isNumber(String s) {
		return StringUtils.isNumeric(s);
	}

	public static String[] surround(String[] a, String surroundedBy) {
		Assert.notNull(a);
		Assert.noNullElements(a);
		Assert.notNull(surroundedBy);
		for (int i = 0; i < a.length; i++) {
			a[i] = surroundedBy + a[i].replaceAll(surroundedBy, "") + surroundedBy;
		}
		return a;
	}

	public static String[] surround(List<String> list, String surroundedBy) {
		Assert.notNull(list);
		Assert.notNull(surroundedBy);
		String[] a = new String[list.size()];
		a = list.toArray(a);
		return surround(a, surroundedBy);
	}

	public static boolean isSurroundedBy(String string, String surroundedBy) {
		return StringUtils.isNotBlank(string) && StringUtils.isNotBlank(surroundedBy)
				&& StringUtils.startsWith(string, surroundedBy) && StringUtils.endsWith(string, surroundedBy);
	}

	public static String extractSurroundedString(String string, String surroundedBy) {
		if (isSurroundedBy(string, surroundedBy)) {
			return StringUtils.substringBefore(StringUtils.substringAfter(string, surroundedBy), surroundedBy);
		}
		return string;
	}

	/**
	 * Validation to see if ANY of the values in the list are NOT empty.
	 *
	 * @param list
	 * @return
	 */
	public static boolean any(String... list) {
		return any(Lists.newArrayList(list));
	}

	public static boolean any(List<String> list) {
		for (String str : list)
			if (StringUtils.isNotEmpty(str))
				return true;
		return false;
	}

	/**
	 * Return true if all input strings are not empty, false otherwise
	 *
	 * @param list
	 * @return
	 */
	public static boolean all(String... list) {
		for (String str : list) {
			if (StringUtils.isEmpty(str)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return true if all input strings are empty, false otherwise
	 *
	 * @param list
	 * @return
	 */
	public static boolean none(String... list) {
		for (String str : list)
			if (!StringUtils.isEmpty(str))
				return false;
		return true;
	}

	public static String normalizePostalCode(String postalCode) {
		if (StringUtils.isBlank(postalCode)) {
			return StringUtils.EMPTY;
		}
		postalCode = StringUtils.strip(postalCode);
		postalCode = StringUtils.deleteWhitespace(postalCode);
		postalCode = StringUtils.remove(postalCode, "-");

		if (StringUtils.isNumeric(postalCode)) {
			//US zip code
			if (postalCode.length() < 5) {
				return StringUtils.leftPad(postalCode, 5, "0");
			}
			if (postalCode.length() > 5) {
				return postalCode.substring(0, 5);
			}

		} else {
			//Canada format ANA NAN
			if (postalCode.length() > 6) {
				return postalCode.substring(0, 6);
			}
		}
		return postalCode;
	}

	public static boolean equalsIgnoreCaseAndSpaces(String str1, String str2) {
		  return StringUtils.equalsIgnoreCase(str1.replaceAll("\\s",""), str2.replaceAll("\\s",""));
	}

	public static boolean equalsAny(String str, String... list) {
		for (String s : list) {
			if (StringUtils.equals(str, s)) { return true; }
		}
		return false;
	}

	public static boolean equalsAnyIgnoreCase(String str, String... list) {
		for (String s : list) {
			if (StringUtils.equalsIgnoreCase(str, s)) { return true; }
		}
		return false;
	}

	public static boolean equalsAnyIgnoreCase(String str, Collection<String> list) {
		for (String s : list) {
			if (StringUtils.equalsIgnoreCase(str, s)) { return true; }
		}
		return false;
	}

	public static String stripHTML(String input) {
		if (StringUtils.isBlank(input)) {
			return input;
		}
		String cleaned = Jsoup.clean(input, Whitelist.none());
		return cleaned.replaceAll("\\r", " ").replaceAll("\\n", " ");
	}

	public static String stripTags(String input) {
        return Jsoup.clean(input, Whitelist.simpleText().addTags("p","br","ol","ul","li"));
	}

	public static String stripTags(String input, String... tags) {
		if (StringUtils.isBlank(input)) return StringUtils.EMPTY;
		return Jsoup.clean(input, new Whitelist().addTags(tags));
	}

	public static String stripTags(String input, String allowed) {
		String[] tags = StringUtils.split(allowed, ",");
		return stripTags(input, tags);
	}

	public static String extractTextFromHTML(String input) {
		return Jsoup.parse(input).text();
	}

	public static String limitWords(String input, int count) {
		return limitWords(input, count, null);
	}

	public static String limitWords(String input, int count, String end) {
		if (StringUtils.isEmpty(input))
			return input;

		String regex = "^\\s*+(?:\\S++\\s*+){1," + Integer.toString(count) + "}";
		Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(input);

		if (!matcher.find())
			return input;

		String limited = StringUtils.stripEnd(matcher.group(0), null);
		// Only attach the end character if the matched string is shorter than the starting string.
		if (limited.length() == input.length())
			return limited;
		return smartJoin(limited, "", end);
	}

	public static String pluralize(String noun, Integer count) {
		return Noun.pluralOf(noun, count);
	}

	public static String pluralize(String noun, Double count) {
		if (count == null) return noun;
		return pluralize(noun, count.intValue());
	}

	/**
	 * return "1st", "2nd", "3rd" etc. for 1, 2, 3
	 *
	 * @param i
	 * @return
	 */
	public static String ordinalize(int i) {
		String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
		switch (i % 100) {
			case 11:
			case 12:
			case 13:
				return i + "th";
			default:
				return i + suffixes[i % 10];
		}
	}

	public static String newLineToXhtmlBreak(String str) {
		return newLineToHtmlBreak(str, true);
	}

	public static String newLineToHtmlBreak(String str, boolean isXhtml) {
		if (StringUtils.isNotEmpty(str)) {
			String breakTag = (isXhtml) ? "<br/>" : "<br>";
			return str.replaceAll("(\\r\\n)|(\\n\\r)|(\\r)|(\\n)", breakTag);
		}

		return str;
	}

	public static String escapeHtmlAndnl2br(String str) {
		return newLineToXhtmlBreak(StringEscapeUtils.escapeHtml4(str));
	}

	public static String showLastNDigits(String str, Character maskChar, Integer numDigits) {
		if (StringUtils.isEmpty(str)) return str;
		if (maskChar == null) return null;
		if (numDigits < 1) return str;
		if (numDigits > str.length()) return StringUtils.repeat(Character.toString(maskChar), numDigits);

		StringBuilder result = new StringBuilder(str);
		int totalSkipped = 0;
		for (int i = str.length() - 1; i >= 0; i--) {
			if (Character.isDigit(str.charAt(i))) {
				if (totalSkipped >= numDigits) {
					result.setCharAt(i, maskChar);
				} else {
					totalSkipped++;
				}
			}
		}
		return result.toString();
	}

	public static String showLastNCharacters(String str, String maskPattern, int numToShow) {
		if (StringUtils.isEmpty(str)) return str;
		if (StringUtils.isEmpty(maskPattern)) return str;
		if (numToShow >= str.length()) return str;

		return StringUtils.repeat(maskPattern, str.length()-numToShow) + str.substring(str.length()-numToShow, str.length());
	}

	/**
	 * Wraps matched urls with HTML &lt;a&gt; tags, eg:
	 * www.yahoo.com = <a href="http://www.yahoo.com">www.yahoo.com</a>
	 * http://google.com = <a href="http://google.com">http://google.com</a>
	 *
	 * @param text to auto link
	 * @return text with inserted &lt;&gt; tags around matched URLs
	 */
	public static String autoLink(String text) {
		if (isNotEmpty(text)) {
			StringBuilder out = new StringBuilder(text.length());
			Pattern pattern = Pattern.compile("((?:www)|(?:(?:http|https|ftp)\\://))[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(?:\\:[a-zA-Z0-9]*)?/?(?:[a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*[^\\.\\,\\)\\(\\s<]");
			Matcher matcher = pattern.matcher(text);
			int mark = 0;

			while (matcher.find()) {
				String schema = matcher.group(1);
				String match = matcher.group();

				out.append(text.substring(mark, matcher.start()));
				mark = matcher.end();

				out.append("<a href=\"");

				if ("www".equalsIgnoreCase(schema)) {
					out.append("http://");
				}

				out.append(match).append("\" target=\"_blank\">").append(match).append("</a>");
			}

			return out.append(text.substring(mark)).toString();
		} else {
			return text;
		}
	}


	/**
	 * Converts THIS_STRING_FORMAT to ThisStringFormat
	 *
	 * @param s
	 * @return
	 */
	public static String convertUnderscoredToCamelCase(String s) {
		String[] parts = s.split("_");
		String camelCaseString = "";
		for (String part : parts) {
			String trimmed = part.trim();
			camelCaseString = camelCaseString + trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
		}
		return camelCaseString;
	}

	/**
	 * Converts "camelCase" to "camel case".
	 * TODO Switch to a more exhaustive library that supports acronyms, etc.
	 */
	public static String humanize(String s) {
		return StringUtils.lowerCase(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(s), " "));
	}

	public static String convertCamelCaseToUnderscored(String s) {
		return StringUtils.lowerCase(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(s), "_"));
	}

	public static String JoinWithLastSeparator(Iterable<?> iterable, String separator, String lastSeparator) {
		StringBuilder joinedString = new StringBuilder(StringUtils.join(iterable, separator));
		if (lastSeparator != null) {
			int lastSeparatorIndex = joinedString.lastIndexOf(separator);
			if (lastSeparatorIndex > 0) {
				joinedString.deleteCharAt(lastSeparatorIndex);
				joinedString.insert(lastSeparatorIndex, " and");
			}
		}
		return joinedString.toString();
	}

	/**
	 * Gets filename extension or empty string if not found.
	 *
	 * @param filename
	 * @return
	 */
	public static String getFileExtension(String filename) {
		if (!StringUtils.isEmpty(filename)) {
			int dotPos = filename.lastIndexOf('.');
			if (dotPos != -1 && dotPos < filename.length())
				return filename.substring(dotPos + 1, filename.length());
		}
		return "";
	}

	/**
	 * Converts a string to an underscored, punctuation-free filename
	 *
	 * @param filename
	 * @return
	 */
	public static String convertToFilename(String filename) {
		if (StringUtils.isEmpty(filename))
			return "";
		return filename.replaceAll("[\\p{Punct}]", "").replaceAll("[^a-zA-Z0-9-_\\.]", "_");
	}

	/**
	 * Formats monetory values.
	 *
	 * @param value
	 * @param decimalPlaces
	 * @param showNegative
	 * @return
	 */
	public static String formatMoney(BigDecimal value, int decimalPlaces, boolean showNegative) {
		DecimalFormat format = new DecimalFormat("");
		format.setParseBigDecimal(true);
		format.setMaximumFractionDigits(decimalPlaces);
		format.setMinimumFractionDigits(decimalPlaces);

		if (!showNegative) {
			format.setNegativePrefix("");
		}

		return format.format(value);
	}

	public static String formatMoney(BigDecimal value) {
		return formatMoney(value, 2, false);
	}

	public static String formatMoneyForAccounting(BigDecimal value) {
		if (value == null) return "-";
		if (value.compareTo(BigDecimal.ZERO) == 0) return "-";
		if (value.compareTo(BigDecimal.ZERO) > 0) {
			return formatMoney(value, 2, false);
		}
		return "(" + formatMoney(value, 2, false) + ")";
	}

	public static String formatMoneyForDisplay(BigDecimal value) {
		if (value == null) return "-";
		if (value.compareTo(BigDecimal.ZERO) == 0) return "$0.00";
		if (value.compareTo(BigDecimal.ZERO) > 0) {
			return "$" + formatMoney(value, 2, false);
		}
		return "$" + "(" + formatMoney(value, 2, false) + ")";
	}

	public static String parseInetAddress(String s) {
		if (InetAddresses.isInetAddress(s))
			return InetAddresses.forString(s).getHostAddress();
		return null;
	}

	/**
	 * For an integer i, return a left-zero-padded binary string representation (e.g. 2 with 5 pad digits --> 00010)
	 * padDigits is ignored if it is smaller than the size of the converted binary string
	 *
	 * @param i
	 * @param padDigits
	 * @return
	 */
	public static String convertToPaddedBinaryString(int i, int padDigits) {
		return StringUtils.leftPad(Integer.toBinaryString(i), padDigits, "0");
	}

	/**
	 * Replaces non whitespace characters with spaces and capitalizes each
	 * word in the string as in following examples:
	 * <p/>
	 * "SOME_CAMEL_CASE_STRING" will be converted to "Some Camel Case String"
	 * "SOME-CAMEL-CASE_STRING" will be converted to "Some Camel Case String"
	 * "SOME CAMEL CASE STRING" will be converted to "Some Camel Case String"
	 * "some_camel_case_string" will be converted to "Some Camel Case String"
	 * "SOME_camel-Case_StRiNG" will be converted to "Some Camel Case String"
	 * "SOME camel Case  StRiNG" will be converted to "Some Camel Case String"
	 *
	 * @param value - ugly string
	 * @return pretty string
	 */
	public static String toPrettyName(String value) {

		if (StringUtils.isBlank(value)) return value;

		StringBuilder pretty = new StringBuilder();

		String v = value.replaceAll("[\\W|_]++", " ");
		if (!v.contains(" ")) { // if one word
			if (v.toUpperCase().equals(value)) { // if all caps
				char C = Character.toUpperCase(v.charAt(0));
				pretty.append(C).append(v.substring(1).toLowerCase()).append(" ");
			} else { // split camel case into words
				char[] chars = v.toCharArray();
				for (char c : chars) {
					if (Character.isUpperCase(c)) {
						pretty.append(' ');
					}
					pretty.append(c);
				}
			}
		} else {
			String[] words = v.toLowerCase().split(" ");
			for (String word : words) {
				if (StringUtils.isNotBlank(word)) {
					char C = Character.toUpperCase(word.charAt(0));
					pretty.append(C).append(word.substring(1)).append(" ");
				}
			}
		}

		return pretty.toString().trim();
	}

	public static String upcaseFirstLetter(String s) {
		if (StringUtils.isBlank(s)) return s;
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	/* returns true if a string address starts with P.O. Box [num] or some variant, false otherwise */
	public static boolean isPoBox(String address) {
		return StringUtils.isNotBlank(address)
				&& Pattern.compile("^(([pP]\\.?[oO]\\.?(([bB]\\.?)|(\\s+[bB]ox)))|([Pp]ost\\s+[Oo]ffice\\s+[bB]ox))\\s+\\d+(-\\d+)*")
				.matcher(address.trim()).find();

	}

	public static String maskDigits(String value, String mask) {
		// mask digits that are immediately following forward slashes
		// so words with numbers in them will not have the mask inserted in them
		return value.replaceAll("/\\d+", "/" + mask);
	}

	/**
	 * Escapes double and single quotes by adding backslashes (' => \', " => \")
	 * @param value string to be escaped
	 * @return escaped string
	 */
	public static String escapeQuotes(String value) {
		if (value == null)
			return "";

		return value.replace("\"", "\\\"").replace("'", "\\'");
	}

	public static String getPathFromURL(String url) {
		String path;
		try{
			path = new URL(url).getPath();
		} catch(MalformedURLException e) {
			path = "";
		}
		return path;
	}

	public static String stripXSSAndEscapeHtml(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}

		// Avoid null characters
		value = value.replaceAll("", "");

		return stripXSSInString(escapeHtmlInString(value));
	}

	public static String stripXSS(String value ) {
		if (StringUtils.isBlank(value)) {
			return value;
		}

		return stripXSSInString(value);
	}

	private static String escapeHtmlInString(String value) {
		Document.OutputSettings settings = new Document.OutputSettings();
		//Jsoup HTML output methods will not re-format the output, and the output will generally look like the input.
		settings.prettyPrint(false);
		// Strip HTML - relaxed because the standard one adds ref="nofollow" to links
		return(Jsoup.clean(value, Constants.PROD_BASE_URL, WHITELIST_XSS, settings));
	}

	private static String stripXSSInString(String value) {
		// Avoid anything between script tags
		Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid anything in a src='...' type of expression
		scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		value = scriptPattern.matcher(value).replaceAll("");

		scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid anything in a onmouseover='...' type of expression
		scriptPattern = Pattern.compile("onmouseover[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid anything in a onmouseover="..." type of expression
		scriptPattern = Pattern.compile("onmouseover[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid anything in a onclick='...' type of expression
		scriptPattern = Pattern.compile("onclick[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid anything in a onclick="..." type of expression
		scriptPattern = Pattern.compile("onclick[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		value = scriptPattern.matcher(value).replaceAll("");

		// Remove any lonesome </script> tag
		scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
		value = scriptPattern.matcher(value).replaceAll("");

		// Remove any lonesome <script ...> tag
		scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid eval(...) expressions
		scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid expression(...) expressions
		scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid alert= expressions
		scriptPattern = Pattern.compile("alert(.*?)=", Pattern.CASE_INSENSITIVE);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid javascript:... expressions
		scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid vbscript:... expressions
		scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
		value = scriptPattern.matcher(value).replaceAll("");

		// Avoid onload= expressions
		scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		value = scriptPattern.matcher(value).replaceAll("");

		return value;
	}

	public static boolean isValidUSADriverLicence(String value){
		if (StringUtils.isEmpty(value) || StringUtils.length(value) != 9) {
			return false;
		} else {
			return Pattern.compile("^\\d{9}$").matcher(value).matches();
		}
	}

	public static String getBankAccountLastFourDigits(String accountNumber) {
		int accountLength = accountNumber.length();
		if (StringUtils.isNotBlank(accountNumber)) {
			if (accountLength < 4) {
				if ((accountLength / 2) == 0) {
					return StringUtils.EMPTY;
				}
				return StringUtils.right(accountNumber, (accountLength / 2));
			}
			return StringUtils.right(accountNumber, 4);
		}
		return StringUtils.EMPTY;
	}

	public static String getPdfTemplateFilename(int taxYear){
		return String.format("%s%d%s", Constants.TAX_FORM_1099_PDF_TEMPLATE_FILEPATH_PREFIX, taxYear, Constants.PDF_EXTENSION);
	}

	public static String getDeletedName(String name, int length) {
		if (StringUtils.isBlank(name)) {
			return StringUtils.EMPTY;
		}
		String result = name.length() <= length ? name : name.substring(0, length);
		return result.concat(" DELETED_ON_").concat(String.valueOf(Calendar.getInstance().getTimeInMillis()));
	}


	/**
	 * Remove characters from a string
	 * @param str string to be removed
	 * @param remove chars to be removed
	 * @return removed string
	 */
	public static String remove(String str, String remove) {
		char[] ca = remove.toCharArray();
		for (char c: ca) {
			str = str.replace(""+c, "");
		}
		return str;
	}

	public static String stripSugarAccountOwnerResponse(String body) {
		try {
			JSONObject result = new JSONObject(body);
			return (new JSONObject(StringUtilities.remove(result.get("records").toString(), "[]"))).get("assigned_user_name").toString();
		} catch (Exception e) {
			return "NA";
		}
	}

	public static String capitalizeFirstLetter(String str) {
		if (str == null) {
			return str;
		}
		if(str.length() <= 1) {
			return str.toUpperCase();
		}
		return Character.toString(str.toLowerCase().charAt(0)).toUpperCase() + str.toLowerCase().substring(1);
	}

	public static String removeControlCharsIfAny(String in){
		if(in != null && !in.isEmpty())
			return CharMatcher.JAVA_ISO_CONTROL.removeFrom(in);
		else
			return in;
	}

}
