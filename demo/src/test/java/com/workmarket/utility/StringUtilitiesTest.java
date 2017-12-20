package com.workmarket.utility;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.dto.AddressDTO;
import com.workmarket.dto.AddressDTOUtilities;
import com.workmarket.search.response.work.DashboardAddress;
import com.workmarket.search.response.work.DashboardAddressUtilities;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.AddressUtilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static com.workmarket.vault.models.Secured.PREPEND_MASKING_PATTERN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class StringUtilitiesTest {
	private static final String WORKMARKET_ADDRESS1 = "240 W 37th St";
	private static final String WORKMARKET_ADDRESS2 = "10th Floor";
	private static final String WORKMARKET_CITY = "New York";
	private static final String WORKMARKET_STATE = "NY";
	private static final String WORKMARKET_POSTALCODE = "10018";
	private static final String WORKMARKET_COUNTRY = "USA";

	@Test
	public void test_parseLong() throws Exception {
		assertEquals(Long.valueOf(1L), StringUtilities.parseLong("1"));
		assertEquals(Long.valueOf(7832L), StringUtilities.parseLong("7832"));
	}

	@Test
	public void test_split() throws Exception {
		assertArrayEquals(new String[]{"tag1", "tag2", "tag3"}, StringUtilities.split("tag1,tag2,tag3", ","));
		assertArrayEquals(new String[]{"tag1", "tag2", "tag3"}, StringUtilities.split("tag1 , tag2, tag3 ", ","));
	}

	@Test
	public void test_stripTrailingSlashes() {
		assertEquals(null, StringUtilities.stripTrailingSlashes(null));
		assertEquals("http://workmarket.com/normalized/url", StringUtilities.stripTrailingSlashes("http://workmarket.com/normalized/url/"));
		assertEquals("http://workmarket.com/normalized/url", StringUtilities.stripTrailingSlashes("http://workmarket" +
			".com/normalized/url///"));
		assertEquals("http://workmarket.com/normalized/url", StringUtilities.stripTrailingSlashes("http://workmarket" +
			".com/normalized/url"));
	}

	@Test
	public void equalsAny_EmptyArgs_False() {
		assertFalse(StringUtilities.equalsAny("burp"));
	}

	@Test
	public void equalsAny_DifferentArgs_False() {
		assertFalse(StringUtilities.equalsAny("burp", "barf"));
	}

	@Test
	public void equalsAny_SameArgExists_True() {
		assertTrue(StringUtilities.equalsAny("burp", "barf", "burp"));
	}

	@Test
	public void equalsAnyIgnoreCase_EmptyArgs_False() {
		assertFalse(StringUtilities.equalsAnyIgnoreCase("burp"));
	}

	@Test
	public void equalsAnyIgnoreCase_DifferentArgs_False() {
		assertFalse(StringUtilities.equalsAnyIgnoreCase("burp", "barf"));
	}

	@Test
	public void equalsAnyIgnoreCase_SameArgExists_True() {
		assertTrue(StringUtilities.equalsAnyIgnoreCase("burp", "barf", "BURP"));
	}

	@Test
	public void equalsAnyIgnoreCaseListArg_EmptyArgs_False() {
		assertFalse(StringUtilities.equalsAnyIgnoreCase("burp"));
	}

	@Test
	public void equalsAnyIgnoreCaseListArg_DifferentArgs_False() {
		assertFalse(StringUtilities.equalsAnyIgnoreCase("burp", "barf"));
	}

	@Test
	public void equalsAnyIgnoreCaseListArg_SameArgExists_True() {
		assertTrue(StringUtilities.equalsAnyIgnoreCase("burp", Lists.newArrayList("barf", "BURP")));
	}

	@Test
	public void testStringEquality_Null() {
		assertTrue(StringUtilities.same(null, null));
	}

	@Test
	public void testStringEquality_SameString() {
		assertTrue(StringUtilities.same("abc123", "abc123"));
	}

	@Test
	public void testStringEquality_DifferentString() {
		assertFalse(StringUtilities.same("abc123", "abc"));
	}

	@Test
	public void testStringEquality_StringAndNull() {
		assertFalse(StringUtilities.same("abc123", null));
	}

	@Test
	public void testSanitizeHtml_Null_unchanged() throws Exception {
		assertEquals(null, StringUtilities.stripXSSAndEscapeHtml(null));
	}

	@Test
	public void testSanitizeHtml_EmptyString_unchanged() throws Exception {
		assertEquals("", StringUtilities.stripXSSAndEscapeHtml(""));
	}

	@Test
	public void testSanitizeHtml_NoHTML_unchanged() throws Exception {
		assertEquals("text", StringUtilities.stripXSSAndEscapeHtml("text"));
	}

	@Test
	public void testSanitizeHtml_NoHTMLOnlyNewlineAndCarriageReturn_unchanged() throws Exception {
		assertEquals("Hello Paul,\r\n\r\ntest", StringUtilities.stripXSSAndEscapeHtml("Hello Paul,\r\n\r\ntest"));
	}

	@Test
	public void testSanitizeHtml_HTMLWithNewlineAndCarriageReturn_unchanged() throws Exception {
		assertEquals("<p>Hello Paul,\r\n\r\ntest</p>", StringUtilities.stripXSSAndEscapeHtml("<p>Hello Paul,\r\n\r\ntest</p>"));
	}

	@Test
	public void testSanitizeHtml_OnlyNewline_unchanged() throws Exception {
		assertEquals("<p>Hello Paul,\ntest</p>", StringUtilities.stripXSSAndEscapeHtml("<p>Hello Paul,\ntest</p>"));
	}

	@Test
	public void testSanitizeHtml_OnlyCarriageReturn_unchanged() throws Exception {
		assertEquals("<p>Hello Paul,\rtest</p>", StringUtilities.stripXSSAndEscapeHtml("<p>Hello Paul,\rtest</p>"));
	}

	@Test
	public void testSanitizeHtml_XSSValidTag_unchanged() throws Exception {
		String text = "<b>test</b>";
		assertEquals(text, StringUtilities.stripXSSAndEscapeHtml(text));
	}

	@Test
	public void testSanitizeHtml_XSSInvalidSelfClosingTag_removed() throws Exception {
		assertEquals("", StringUtilities.stripXSSAndEscapeHtml("<keygen src=\"test\" />"));
	}

	@Test
	public void testSanitizeHtml_XSSInvalidTag_stripped() throws Exception {
		assertEquals("text", StringUtilities.stripXSSAndEscapeHtml("<keygen src=\"test\">text</keygen>"));
	}

	@Test
	public void testSanitizeHtml_XSSValidAbsoluteLinkTag_escaped() throws Exception {
		String link = "<a target=\"_blank\" href=\"http://www.zombo.com/do_anything?userId=1353&groupId=2236\">test</a>";
		String escaped = "<a target=\"_blank\" href=\"http://www.zombo.com/do_anything?userId=1353&amp;groupId=2236\" rel=\"nofollow\">test</a>";
		assertEquals(escaped, StringUtilities.stripXSSAndEscapeHtml(link));
	}

	@Test
	public void testSanitizeHtml_XSSValidRelativeBasicLinkTag_escaped() throws Exception {
		String link = "<a target=\"_blank\" href=\"/do_anything\" rel=\"nofollow\">test</a>";
		assertEquals(link, StringUtilities.stripXSSAndEscapeHtml(link));
	}

	@Test
	public void testSanitizeHtml_XSSValidRelativeLinkQueryStringTag_escaped() throws Exception {
		String link = "<a target=\"_blank\" href=\"/do_anything?userId=1353&groupId=2236\">test</a>";
		String escaped = "<a target=\"_blank\" href=\"/do_anything?userId=1353&amp;groupId=2236\" rel=\"nofollow\">test</a>";
		assertEquals(escaped, StringUtilities.stripXSSAndEscapeHtml(link));
	}

	@Test
	public void testSanitizeHtml_XSSWhitehatAttack1_stripped() throws Exception {
		assertEquals("test", StringUtilities.stripXSSAndEscapeHtml("test<keygen autofocus onfocus=alert(1)>"));
	}

	@Test
	public void testSanitizeHtml_XSSWhitehatAttack2_escaped() throws Exception {
		assertEquals("test')&quot;onmouseover=&quot;alert(1)&quot;", StringUtilities.stripXSSAndEscapeHtml("test')\"onmouseover=\"alert(1)\""));
	}

	@Test
	public void testSanitizeHtml_XSSWhitehatAttack3_escaped() throws Exception {
		assertEquals("<a rel=\"nofollow\">test</a>", StringUtilities.stripXSSAndEscapeHtml("<a href=j&#X41vascript:alert('test2')" +
			">test</a>"));
	}

	@Test
	public void test_standardizePhoneNumber() throws Exception {
		assertEquals("6465842294", StringUtilities.standardizePhoneNumber("+1 (646) 584-2294"));
		assertEquals("6465842294", StringUtilities.standardizePhoneNumber("+16465842294"));
	}

	@Test
	public void test_formatE164PhoneNumber() throws Exception {
		assertEquals("+16465842294", StringUtilities.formatE164PhoneNumber("+1 (646) 584-2294"));
		assertEquals("+16465842294", StringUtilities.formatE164PhoneNumber("646-584-2294"));
		assertEquals("+16465842294", StringUtilities.formatE164PhoneNumber("6465842294"));
	}

	@Test
	public void test_formatPhoneNumberForIvr() throws Exception {
		assertEquals("6 4 6 5 8 4 2 2 9 4", StringUtilities.formatPhoneNumberForIvr("+1 (646) 584-2294"));
		assertEquals("6 4 6 5 8 4 2 2 9 4", StringUtilities.formatPhoneNumberForIvr("646-584-2294"));
		assertEquals("6 4 6 5 8 4 2 2 9 4", StringUtilities.formatPhoneNumberForIvr("6465842294"));
	}

	@Test
	public void test_formatPhoneNumber() throws Exception {
		assertEquals("+1 (646) 584-2294", StringUtilities.formatPhoneNumber("6465842294", "1", null));
		assertEquals("+1 (646) 584-2294 ext.5", StringUtilities.formatPhoneNumber("6465842294", "1", "5"));
	}

	@Test
	public void test_validateWebAddressPatternWithOptionalProtocol() throws Exception {
		assertFalse(StringUtilities.validateWebAddressPatternWithOptionalProtocol(""));
		assertTrue(StringUtilities.validateWebAddressPatternWithOptionalProtocol("google.com"));
		assertTrue(StringUtilities.validateWebAddressPatternWithOptionalProtocol("   http://workmarket.com"));
		assertTrue(StringUtilities.validateWebAddressPatternWithOptionalProtocol("https://www.workmarket.com   "));
		assertFalse(StringUtilities.validateWebAddressPatternWithOptionalProtocol("ttp://workmarket.com"));
		assertFalse(StringUtilities.validateWebAddressPatternWithOptionalProtocol("workmarket"));
		assertFalse(StringUtilities.validateWebAddressPatternWithOptionalProtocol("com.workmarket"));
		assertFalse(StringUtilities.validateWebAddressPatternWithOptionalProtocol("workmarket/"));
	}

	@Test
	public void test_validatePostalCodePattern() throws Exception {
		assertTrue(StringUtilities.validatePostalCodePattern("12345"));
		assertTrue(StringUtilities.validatePostalCodePattern("12345-1234"));
		assertFalse(StringUtilities.validatePostalCodePattern("1234"));
		assertFalse(StringUtilities.validatePostalCodePattern("12345-"));
		assertFalse(StringUtilities.validatePostalCodePattern("12345-12345"));
		assertFalse(StringUtilities.validatePostalCodePattern("12345-123"));
	}

	@Test
	public void normalizePostalCode() throws Exception {
		assertEquals("12345", StringUtilities.normalizePostalCode("12345"));
		assertEquals("12345", StringUtilities.normalizePostalCode("12345-1234"));
		assertEquals("01234", StringUtilities.normalizePostalCode("1234"));

		assertEquals("H0H0H0", StringUtilities.normalizePostalCode("H0H0H0"));
		assertEquals("H0H0H0", StringUtilities.normalizePostalCode("H0H 0H0"));
		assertEquals("H0H0H0", StringUtilities.normalizePostalCode("H0H-0H0"));
	}

	@Test
	public void normalizePostalCodeInternational() throws Exception {
		assertEquals("12345", PostalCodeUtilities.normalizePostalCodeInternational("12345", Country.USA));
		assertEquals("12345", PostalCodeUtilities.normalizePostalCodeInternational("12345-1234", Country.USA));
		assertEquals("01234", PostalCodeUtilities.normalizePostalCodeInternational("1234", Country.USA));

		assertEquals("H0H0H0", PostalCodeUtilities.normalizePostalCodeInternational("H0H0H0", Country.CANADA));
		assertEquals("H0H0H0", PostalCodeUtilities.normalizePostalCodeInternational("H0H 0H0", Country.CANADA));
		assertEquals("H0H0H0", PostalCodeUtilities.normalizePostalCodeInternational("H0H-0H0", Country.CANADA));
		assertEquals("1234567", PostalCodeUtilities.normalizePostalCodeInternational("1234567", Country.valueOf("AR").getISO()));
	}

	@Test
	public void formatUSAddress() throws Exception {
		Address a1 = new Address()
				.setAddressLine1("20 West 20th Street")
				.setAddressLine2("Suite 402")
				.setCity("New York")
				.setState("NY")
				.setZip("10010")
				.setCountry("USA");

		DashboardAddress a2 = new DashboardAddress()
				.setCity("New York")
				.setState("NY")
				.setPostalCode("10010")
				.setCountry("USA");

		AddressDTO a3 = new AddressDTO();
		a3.setAddress1("20 West 20th Street");
		a3.setAddress2("Suite 402");
		a3.setCity("New York");
		a3.setState("NY");
		a3.setPostalCode("10010");
		a3.setCountry("USA");

		String expectedShort = "New York, NY 10010";
		String expectedLong = "20 West 20th Street<br/>Suite 402<br/>New York, NY 10010";

		assertEquals(expectedShort, AddressUtilities.formatAddressShort(a1));
		assertEquals(expectedShort, DashboardAddressUtilities.formatAddressShort(a2));
		assertEquals(expectedShort, AddressDTOUtilities.formatAddressShort(a3));

		assertEquals(expectedLong, AddressUtilities.formatAddressLong(a1));
		assertEquals(expectedLong, AddressDTOUtilities.formatAddressLong(a3, "<br/>"));
	}

	@Test
	public void formatCanadianAddress() throws Exception {
		Address a1 = new Address()
				.setAddressLine1("Bank of & Canada")
				.setAddressLine2("234 Wellington Street")
				.setCity("Ottawa")
				.setState("ON")
				.setZip("K1A 0G9")
				.setCountry("CAN");

		DashboardAddress a2 = new DashboardAddress()
				.setCity("Ottawa")
				.setState("ON")
				.setPostalCode("K1A 0G9")
				.setCountry("CAN");

		AddressDTO a3 = new AddressDTO();
		a3.setAddress1("Bank of & Canada");
		a3.setAddress2("234 Wellington Street");
		a3.setCity("Ottawa");
		a3.setState("ON");
		a3.setPostalCode("K1A 0G9");
		a3.setCountry("CAN");

		String expectedShort = "Ottawa, ON K1A 0G9";
		String expectedLong = "Bank of &amp; Canada<br/>234 Wellington Street<br/>Ottawa, ON K1A 0G9";

		assertEquals(expectedShort, AddressUtilities.formatAddressShort(a1));
		assertEquals(expectedShort, DashboardAddressUtilities.formatAddressShort(a2));
		assertEquals(expectedShort, AddressDTOUtilities.formatAddressShort(a3));

		assertEquals(expectedLong, AddressUtilities.formatAddressLong(a1));
		assertEquals(expectedLong, AddressDTOUtilities.formatAddressLong(a3, "<br/>"));
	}

	@Test
	public void formatAddressForGeocode_US() throws Exception {
		String address1 = "20 West 20th St";
		String address2 = "Suite 402";
		String city = "New York";
		String state = "NY";
		String postalCode = "10010";
		String country = "USA";

		assertEquals("20 West 20th St, New York, NY 10010", PostalCodeUtilities.formatAddressForGeocoder(address1,
			null, city, state, postalCode, country));
		assertEquals("20 West 20th St, New York, NY 10010", PostalCodeUtilities.formatAddressForGeocoder
			(address1, address2, city, state, postalCode, country));
	}

	@Test
	public void formatAddressForGeocode_CAN() throws Exception {
		String address1 = "Bank of Canada";
		String address2 = "234 Wellington Street";
		String city = "Ottawa";
		String state = "ON";
		String postalCode = "K1A0G9";
		String country = "CAN";

		assertEquals("Bank of Canada, Ottawa, ON Canada, K1A 0G9", PostalCodeUtilities.formatAddressForGeocoder(address1, null, city, state, postalCode, country));
		assertEquals("Bank of Canada, Ottawa, ON Canada, K1A 0G9", PostalCodeUtilities
			.formatAddressForGeocoder(address1, address2, city, state, postalCode, country));
	}

	@Test
	public void formatAddress_US() {
		assertEquals(
			"240 W 37th St, New York, NY 10018",
			PostalCodeUtilities.formatAddressForGeocoder(
				WORKMARKET_ADDRESS1,
				WORKMARKET_ADDRESS2,
				WORKMARKET_CITY,
				WORKMARKET_STATE,
				WORKMARKET_POSTALCODE,
				WORKMARKET_COUNTRY));
	}

	@Test
	public void equalsIgnoreCaseAndSpaces_success() throws Exception {
		assertTrue(StringUtilities.equalsIgnoreCaseAndSpaces("v2g0a5", "V2G 0A5 "));
	}

	@Test
	public void test_newLineToHtmlBreak() throws Exception {
		String str = "One\nTwo\rThree\r\nFour\n\r";
		assertEquals("One<br>Two<br>Three<br>Four<br>", StringUtilities.newLineToHtmlBreak(str, false));
	}

	@Test
	public void test_newLineToXhtmlBreak() throws Exception {
		String str = "One\nTwo\rThree\r\nFour\n\r";
		assertEquals("One<br/>Two<br/>Three<br/>Four<br/>", StringUtilities.newLineToXhtmlBreak(str));
	}

	@Test
	public void test_escapeHtmlAndnl2br() throws Exception {
		String str = "One\n<script>alert(1)</script>\r</div>\r\nFour\n\r";
		assertEquals("One<br/>&lt;script&gt;alert(1)&lt;/script&gt;<br/>&lt;/div&gt;<br/>Four<br/>", StringUtilities.escapeHtmlAndnl2br(str));
	}

	@Test
	public void limitWords() throws Exception {
		String input = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
		assertEquals("Lorem ipsum dolor", StringUtilities.limitWords(input, 3));
		assertEquals("Lorem ipsum dolor...", StringUtilities.limitWords(input, 3, "..."));

		input = "Lorem ipsum dolor.";
		assertEquals("Lorem ipsum dolor.", StringUtilities.limitWords(input, 3));
		assertEquals("Lorem ipsum dolor.", StringUtilities.limitWords(input, 3, "..."));
	}

	@Test
	public void autoLink() throws Exception {
		String input = "Lorem ipsum http://www.amet.com, consectetur www.elit.com, sed do http://tempor.com incididunt ut labore et dolore.<p>http://workmarket.com/</p>";
		assertEquals("Lorem ipsum <a href=\"http://www.amet.com\" target=\"_blank\">http://www.amet.com</a>, consectetur " +
						"<a href=\"http://www.elit.com\" target=\"_blank\">www.elit.com</a>, sed do " +
						"<a href=\"http://tempor.com\" target=\"_blank\">http://tempor.com</a> incididunt ut labore et dolore." +
						"<p><a href=\"http://workmarket.com/\" target=\"_blank\">http://workmarket.com/</a></p>",
				StringUtilities.autoLink(input));
	}

	@Test
	public void formatMoney() throws Exception {
		BigDecimal positiveNumber = new BigDecimal("243.513099");
		String positiveResult = StringUtilities.formatMoney(positiveNumber, 2, false);
		assertEquals("243.51", positiveResult);

		BigDecimal absNegativeNumber = new BigDecimal("-312.1209031");
		String absNegativeResult = StringUtilities.formatMoney(absNegativeNumber, 2, false);
		assertEquals("312.12", absNegativeResult);

		BigDecimal negativeNumber = new BigDecimal("-3532.143321");
		String negativeResult = StringUtilities.formatMoney(negativeNumber, 2, true);
		assertEquals("-3,532.14", negativeResult);
	}

	@Test
	public void formatSsn() throws Exception {
		assertEquals("123-45-6789", StringUtilities.formatSsn("123456789"));
		assertEquals("123-45-6789", StringUtilities.formatSsn("123-45-6789"));
		assertEquals("123-45-6789", StringUtilities.formatSsn(PREPEND_MASKING_PATTERN + "123456789"));
		assertEquals("123-45-6789", StringUtilities.formatSsn(PREPEND_MASKING_PATTERN + "123-45-6789"));
	}

	@Test
	public void formatEin() throws Exception {
		assertEquals("12-3456789", StringUtilities.formatEin("123456789"));
		assertEquals("12-3456789", StringUtilities.formatEin("12-3456789"));
		assertEquals("12-3456789", StringUtilities.formatEin(PREPEND_MASKING_PATTERN + "123456789"));
		assertEquals("12-3456789", StringUtilities.formatEin(PREPEND_MASKING_PATTERN + "12-3456789"));
	}

	@Test
	public void formatSin() throws Exception {
		assertEquals("123-456-789", StringUtilities.formatCanadaSin("123456789"));
		assertEquals("123-456-789", StringUtilities.formatCanadaSin("123-456-789"));
		assertEquals("123-456-789", StringUtilities.formatCanadaSin(PREPEND_MASKING_PATTERN + "123456789"));
		assertEquals("123-456-789", StringUtilities.formatCanadaSin(PREPEND_MASKING_PATTERN + "123-456-789"));
	}

	@Test
	public void formatBn() throws Exception {
		assertEquals("123456789-RN-2345", StringUtilities.formatCanadaBn("123456789RN2345"));
		assertEquals("123456789-RN-2345", StringUtilities.formatCanadaBn("123456789-RN-2345"));
		assertEquals("123456789-RN-2345", StringUtilities.formatCanadaBn(PREPEND_MASKING_PATTERN + "123456789RN2345"));
		assertEquals("123456789-RN-2345", StringUtilities.formatCanadaBn(PREPEND_MASKING_PATTERN
			+ "123456789-RN-2345"));
	}

	@Test
	public void isValidUsaTaxId() {
		assertTrue(StringUtilities.isUsaTaxIdentificationNumber("123-12-1234"));
		assertTrue(StringUtilities.isUsaTaxIdentificationNumber("12-3123123"));
		assertTrue(StringUtilities.isUsaTaxIdentificationNumber("123123123"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("12-312-3123"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("1232-1234"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("12322"));

		// ssn rules
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("666-45-1234"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("966-45-1234"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("000-00-0000"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("123-45-6789"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("000-45-6789"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("123-00-6789"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("123-45-0000"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("987-65-4323"));

		// ein rules
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("09-4540000"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("78-6549323"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("12-3456789"));

		// itin rules
		assertTrue(StringUtilities.isUsaTaxIdentificationNumber("900-73-4324"));
		assertTrue(StringUtilities.isUsaTaxIdentificationNumber("999-83-4923"));
		assertTrue(StringUtilities.isUsaTaxIdentificationNumber("999-90-4923"));
		assertTrue(StringUtilities.isUsaTaxIdentificationNumber("999-98-4923"));
		assertTrue(StringUtilities.isUsaTaxIdentificationNumber("999-88-4923"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("999-89-4923"));
		assertFalse(StringUtilities.isUsaTaxIdentificationNumber("999-93-4923"));
	}

	@Test
	public void showLastNDigitsTest() {
		assertEquals("xxx-xx-1234", StringUtilities.showLastNDigits("123-12-1234", 'x', 4));
		assertEquals("xxx-1x-134", StringUtilities.showLastNDigits("123-1x-134", 'x', 4));
		assertEquals("AB-CDx2345", StringUtilities.showLastNDigits("AB-CD12345", 'x', 4));
		assertEquals("xxxx", StringUtilities.showLastNDigits("123", 'x', 4));
		assertEquals("", StringUtilities.showLastNDigits("", 'x', 4));
	}

	@Test
	public void convertToPaddedBinaryStringTest() {
		assertEquals("00010", StringUtilities.convertToPaddedBinaryString(2, 5));
		assertEquals("10000", StringUtilities.convertToPaddedBinaryString(16, 4));
		assertEquals("0", StringUtilities.convertToPaddedBinaryString(0, 0));
	}

	@Test
	public void ordinalizeTest() {
		assertEquals("0th", StringUtilities.ordinalize(0));
		assertEquals("1st", StringUtilities.ordinalize(1));
		assertEquals("2nd", StringUtilities.ordinalize(2));
		assertEquals("3rd", StringUtilities.ordinalize(3));
		assertEquals("4th", StringUtilities.ordinalize(4));
		assertEquals("5th", StringUtilities.ordinalize(5));
		assertEquals("10th", StringUtilities.ordinalize(10));
		assertEquals("11th", StringUtilities.ordinalize(11));
		assertEquals("12th", StringUtilities.ordinalize(12));
		assertEquals("13th", StringUtilities.ordinalize(13));
		assertEquals("14th", StringUtilities.ordinalize(14));
		assertEquals("20th", StringUtilities.ordinalize(20));
		assertEquals("21st", StringUtilities.ordinalize(21));
		assertEquals("22nd", StringUtilities.ordinalize(22));
		assertEquals("23rd", StringUtilities.ordinalize(23));
		assertEquals("24th", StringUtilities.ordinalize(24));
		assertEquals("100th", StringUtilities.ordinalize(100));
		assertEquals("101st", StringUtilities.ordinalize(101));
		assertEquals("102nd", StringUtilities.ordinalize(102));
	}

	@Test
	public void toPrettyName() {
		assertEquals(null, StringUtilities.toPrettyName(null));
		assertEquals("", StringUtilities.toPrettyName(""));
		assertEquals("Some Camel Case String", StringUtilities.toPrettyName("SOME_CAMEL_CASE_STRING"));
		assertEquals("Some Camel Case String", StringUtilities.toPrettyName("SOME-CAMEL-CASE_STRING"));
		assertEquals("Some Camel Case String", StringUtilities.toPrettyName("SOME CAMEL CASE STRING"));
		assertEquals("Some Camel Case String", StringUtilities.toPrettyName("some_camel_case_string"));
		assertEquals("Some Camel Case String", StringUtilities.toPrettyName("SOME_camel-Case_StRiNG"));
		assertEquals("Some Camel Case String", StringUtilities.toPrettyName("SOME camel Case  StRiNG"));
		assertEquals("Some Camel Case String", StringUtilities.toPrettyName("SomeCamelCaseString"));
		assertEquals("Some Camel Case St Ring", StringUtilities.toPrettyName("SomeCamelCaseStRing"));
		assertEquals("Some Test", StringUtilities.toPrettyName("  some  test "));
		assertEquals("Oneword", StringUtilities.toPrettyName("ONEWORD"));
	}

	@Test
	public void upcaseFirstLetter() {
		assertEquals("So what", StringUtilities.upcaseFirstLetter("so what"));
		assertEquals("S", StringUtilities.upcaseFirstLetter("s"));
		assertEquals("", StringUtilities.upcaseFirstLetter(""));
	}

	@Test
	public void toFilename() {
		assertEquals("", StringUtilities.convertToFilename(null));
		assertEquals("", StringUtilities.convertToFilename(""));
		assertEquals("Jeff", StringUtilities.convertToFilename("Jeff"));
		assertEquals("Jeff_Leventhals_Cool_New_Car", StringUtilities.convertToFilename("Jeff Leventhal's Cool New Car"));
		assertEquals("Jeff_Wald_Scares_ME111", StringUtilities.convertToFilename("Jeff Wald& Scares ME111!!"));
	}

	@Test
	public void isPoBoxTest() {
		assertTrue(StringUtilities.isPoBox("PO Box 123, anywhere, USA, 10011"));
		assertTrue(StringUtilities.isPoBox("P.O. Box 1234-4324, anywhere, USA, 10011"));
		assertTrue(StringUtilities.isPoBox("Post Office Box 1234-4324, anywhere, USA, 10011"));
		assertTrue(StringUtilities.isPoBox("po box 123"));
		assertTrue(StringUtilities.isPoBox("pob 123")); // this is valid according to USPS
		assertTrue(StringUtilities.isPoBox("post office box 1234-4324"));
		assertTrue(StringUtilities.isPoBox("pob    1234-4324-213   new york"));

		assertFalse(StringUtilities.isPoBox("P.O Box, New York, NY USA"));
		assertFalse(StringUtilities.isPoBox("20 W 20th St, PO Box 123, USA")); // not valid
		assertFalse(StringUtilities.isPoBox("post box 200, USA"));
		assertFalse(StringUtilities.isPoBox("20 W 20th St, New York, NY, USA"));
		assertFalse(StringUtilities.isPoBox(""));
		assertFalse(StringUtilities.isPoBox(null));
	}

	@Test
	public void maskDigits() {
		String value = "/assignments/details/1234567";
		String mask  = "some_id";
		assertEquals("/assignments/details/some_id", StringUtilities.maskDigits(value, mask));
	}

	@Test
	public void Encode() {
		String plaintext = "Hello & There; Buddy!";
		String encoded = "Hello+%26+There%3B+Buddy%21";
		assertEquals(encoded, StringUtilities.urlEncode(plaintext, "UTF-8"));

		assertEquals("", StringUtilities.urlEncode(null));
	}

	@Test
	public void isSurroundedBy_true_success() {
		assertTrue(StringUtilities.isSurroundedBy("\"keyword\"", "\""));
		assertTrue(StringUtilities.isSurroundedBy("|keyword|", "|"));
	}

	@Test
	public void isSurroundedBy_false_success() {
		assertFalse(StringUtilities.isSurroundedBy("keyword", "\""));
	}

	@Test
	public void extractSurroundedString_success() {
		assertEquals(StringUtilities.extractSurroundedString("\"keyword\"", "\""), "keyword");
	}

	@Test
	public void extractSurroundedString_withEmptyString_success() {
		assertEquals(StringUtilities.extractSurroundedString("", "\""), "");
	}

	@Test
	public void extractSurroundedString_withNonSurroundedString_success() {
		assertEquals(StringUtilities.extractSurroundedString("keyword", "\""), "keyword");
	}

	@Test
	public void testGetPdfTemplateFilenameForMultipleTaxYears(){
		assertEquals(StringUtilities.getPdfTemplateFilename(2012), "classpath:files/f1099msc_2012.pdf");
		assertEquals(StringUtilities.getPdfTemplateFilename(2013), "classpath:files/f1099msc_2013.pdf");
		assertEquals(StringUtilities.getPdfTemplateFilename(242424), "classpath:files/f1099msc_242424.pdf");
	}

	@Test
	public void getDeletedName() {
		assertTrue(StringUtilities.getDeletedName("newname", 30).contains("DELETED_ON_"));
		assertTrue(StringUtilities.getDeletedName(null, 30).isEmpty());
	}

	@Test
	public void urlToFilenameValidateAndDecode_normalFileNameNonUrl(){
		assertEquals("testFileName", StringUtilities.urlToFilenameValidateAndDecode("testFileName"));
	}

	@Test
	public void urlToFilenameValidateAndDecode_urlDecodeFileName(){
		assertEquals("http://www.google.com?q=a b", StringUtilities.urlToFilenameValidateAndDecode("http://www.google.com?q=a%20b"));
	}

	@Test
	public void urlToFilenameValidateAndDecode_InvalidArgumentExceptionShouldReturnOriginalValue(){
		assertEquals("http://www.google.com?q=a%Nb", StringUtilities.urlToFilenameValidateAndDecode("http://www.google.com?q=a%Nb"));
	}

	@Test
	public void testRemoveCharFromString() {
		assertEquals("Whats up", StringUtilities.remove("What's up", "'"));
		assertEquals("Hi he said", StringUtilities.remove("\"Hi\", he said", "\","));
		assertEquals("Ths fucto s for remove chars", StringUtilities.remove("This function is for remove chars", "in"));
	}

	@Test
	public void testStripSugarAccountOwnerResponse() {
		assertEquals("Name", StringUtilities.stripSugarAccountOwnerResponse("{'id':'1', 'records':[{'assigned_user_name':'Name'}]}"));
		assertEquals("NA", StringUtilities.stripSugarAccountOwnerResponse("{format is wrong}"));
		assertEquals("NA", StringUtilities.stripSugarAccountOwnerResponse(""));
	}

	@Test
	public void testCapitalizeFirstLetter() {
		assertEquals("Transactional", StringUtilities.capitalizeFirstLetter("TRANSACTIONAL"));
		assertEquals("Professional", StringUtilities.capitalizeFirstLetter("PROFESSIONAL"));
		assertEquals("Enterprise", StringUtilities.capitalizeFirstLetter("ENTERPRISE"));
		assertEquals("1001", StringUtilities.capitalizeFirstLetter("1001"));
		assertEquals("", StringUtilities.capitalizeFirstLetter(""));
		assertEquals("S", StringUtilities.capitalizeFirstLetter("s"));
		assertEquals("S", StringUtilities.capitalizeFirstLetter("S"));
		assertEquals("?", StringUtilities.capitalizeFirstLetter("?"));
		assertEquals(null, StringUtilities.capitalizeFirstLetter(null));
	}

	@Test
	public void processForLike_Simple() {
		assertEquals("%blarg%", StringUtilities.processForLike("blarg"));
	}

	@Test
	public void processForLike_Space() {
		assertEquals("%blarg%blarg%", StringUtilities.processForLike("blarg blarg"));
	}

	@Test
	public void processForLike_Quote() {
		assertEquals("%blarg%", StringUtilities.processForLike("'blarg'"));
	}

	@Test
	public void processForLike_Percent() {
		assertEquals("%blarg%", StringUtilities.processForLike("%%%%%%%%%%%%%%%%%%%blarg%%%%%%%%%%%%%%%%%%%"));
	}

	@Test
	public void shouldValidateTrueUpperCaseCanadianBN() {
		assertTrue(StringUtilities.isCanadaBn("999999999-RN-9999"));
	}

	@Test
	public void shouldValidateTrueLowerCaseCanadianBN() {
		assertTrue(StringUtilities.isCanadaBn("999999999-rn-9999"));
	}

	@Test
	public void testFormatSSN() {
		String ssn = "046454286";
		assertEquals("046-45-4286", StringUtilities.formatSsn(ssn));
		assertEquals("046-45-4286", StringUtilities.formatSsn(PREPEND_MASKING_PATTERN + ssn));
	}

	@Test
	public void testFormatSSNAllowLetters() {
		String ssn = "***454286";
		assertEquals("***-45-4286", StringUtilities.formatSsn(ssn, true));
		assertEquals("***-45-4286", StringUtilities.formatSsn(PREPEND_MASKING_PATTERN + ssn, true));
	}

	@Test
	public void testFormatSSNAllowLettersFalse() {
		String ssn = "***454286";
		assertEquals("454-28-6", StringUtilities.formatSsn(ssn, false));
		assertEquals("454-28-6", StringUtilities.formatSsn(PREPEND_MASKING_PATTERN + ssn, false));
	}

	@Test
	public void testFormatSSNAllowLettersFalseShort() {
		String ssn = "*****4286";
		assertEquals("428-6-", StringUtilities.formatSsn(ssn, false));
		assertEquals("428-6-", StringUtilities.formatSsn(PREPEND_MASKING_PATTERN + ssn, false));
	}

	@Test
	public void testGetSecureSsn() {
		String ssn = "046454286";
		assertEquals("*****4286", StringUtilities.getSecureSsn(ssn));
		assertEquals("*****4286", StringUtilities.getSecureSsn(PREPEND_MASKING_PATTERN + ssn));
	}

	@Test
	public void testFormatSecureSsn() {
		String ssn = "046454286";
		assertEquals("***-**-4286", StringUtilities.formatSecureSsn(ssn));
		assertEquals("***-**-4286", StringUtilities.formatSecureSsn(PREPEND_MASKING_PATTERN + ssn));
	}


	@Test
	public void testFormatEIN() {
		String ein = "567811234";
		assertEquals("56-7811234", StringUtilities.formatEin(ein));
		assertEquals("56-7811234", StringUtilities.formatEin(PREPEND_MASKING_PATTERN + ein));
	}

	@Test
	public void testFormatEINAllowLetters() {
		String ein = "****11234";
		assertEquals("**-**11234", StringUtilities.formatEin(ein, true));
		assertEquals("**-**11234", StringUtilities.formatEin(PREPEND_MASKING_PATTERN + ein, true));
	}

	@Test
	public void testFormatEINAllowLettersFalse() {
		String ein = "****11234";
		assertEquals("11-234", StringUtilities.formatEin(ein, false));
		assertEquals("11-234", StringUtilities.formatEin(PREPEND_MASKING_PATTERN + ein, false));
	}

	@Test
	public void testFormatEINAllowLettersFalseShort() {
		String ein = "********4";
		assertEquals("4-", StringUtilities.formatEin(ein, false));
		assertEquals("4-", StringUtilities.formatEin(PREPEND_MASKING_PATTERN + ein, false));
	}

	@Test
	public void testGetSecureEIN() {
		String ein = "567811234";
		assertEquals("*****1234", StringUtilities.getSecureEin(ein));
		assertEquals("*****1234", StringUtilities.getSecureEin(PREPEND_MASKING_PATTERN + ein));
	}

	@Test
	public void testFormatSecureEIN() {
		String ein = "567811234";
		assertEquals("**-***1234", StringUtilities.formatSecureEin(ein));
		assertEquals("**-***1234", StringUtilities.formatSecureEin(PREPEND_MASKING_PATTERN + ein));
	}


	@Test
	public void testFormatCanadaSin() {
		String sin = "046454286";
		assertEquals("046-454-286", StringUtilities.formatCanadaSin(sin));
		assertEquals("046-454-286", StringUtilities.formatCanadaSin(PREPEND_MASKING_PATTERN + sin));
	}

	@Test
	public void testFormatCanadaSinAllowLetters() {
		String sin = "****54286";
		assertEquals("***-*54-286", StringUtilities.formatCanadaSin(sin, true));
		assertEquals("***-*54-286", StringUtilities.formatCanadaSin(PREPEND_MASKING_PATTERN + sin, true));
	}

	@Test
	public void testFormatCanadaSinAllowLettersFalse() {
		String sin = "****54286";
		assertEquals("542-86-", StringUtilities.formatCanadaSin(sin, false));
		assertEquals("542-86-", StringUtilities.formatCanadaSin(PREPEND_MASKING_PATTERN + sin, false));
	}

	@Test
	public void testFormatCanadaSinAllowLettersFalseShort() {
		String sin = "*******86";
		assertEquals("86--", StringUtilities.formatCanadaSin(sin, false));
		assertEquals("86--", StringUtilities.formatCanadaSin(PREPEND_MASKING_PATTERN + sin, false));
	}

	@Test
	public void testGetSecureCanadaSin() {
		String sin = "046454286";
		assertEquals("******286", StringUtilities.getSecureCanadaSin(sin));
		assertEquals("******286", StringUtilities.getSecureCanadaSin(PREPEND_MASKING_PATTERN + sin));
	}

	@Test
	public void testFormatSecureCanadaSin() {
		String sin = "046454286";
		assertEquals("***-***-286", StringUtilities.formatSecureCanadaSin(sin));
		assertEquals("***-***-286", StringUtilities.formatSecureCanadaSin(PREPEND_MASKING_PATTERN + sin));
	}


	@Test
	public void testFormatCanadaBn() {
		String bn = "567811234RT001";
		assertEquals("567811234-RT-001", StringUtilities.formatCanadaBn(bn));
		assertEquals("567811234-RT-001", StringUtilities.formatCanadaBn(PREPEND_MASKING_PATTERN + bn));
	}

	@Test
	public void testFormatCanadaBnAllowLetters() {
		String bn = "*********RT001";
		assertEquals("*********-RT-001", StringUtilities.formatCanadaBn(bn, true));
		assertEquals("*********-RT-001", StringUtilities.formatCanadaBn(PREPEND_MASKING_PATTERN + bn, true));
	}

	@Test
	public void testFormatCanadaBnAllowLettersFalse() {
		String bn = "*********RT001";
		assertEquals("RT001--", StringUtilities.formatCanadaBn(bn, false));
		assertEquals("RT001--", StringUtilities.formatCanadaBn(PREPEND_MASKING_PATTERN + bn, false));
	}

	@Test
	public void testFormatCanadaBnAllowLettersFalseShort() {
		String bn = "***********001";
		assertEquals("001--", StringUtilities.formatCanadaBn(bn, false));
		assertEquals("001--", StringUtilities.formatCanadaBn(PREPEND_MASKING_PATTERN + bn, false));
	}

	@Test
	public void testgetSecureCanadaBn() {
		String bn = "567811234RT001";
		assertEquals("***********001", StringUtilities.getSecureCanadaBn(bn));
		assertEquals("***********001", StringUtilities.getSecureCanadaBn(PREPEND_MASKING_PATTERN + bn));
	}

	@Test
	public void testFormatSecureCanadaBn() {
		String bn = "567811234RT001";
		assertEquals("*********-**-001", StringUtilities.formatSecureCanadaBn(bn));
		assertEquals("*********-**-001", StringUtilities.formatSecureCanadaBn(PREPEND_MASKING_PATTERN + bn));
	}

	@Test
	public void testGetSecureForeignTaxNumber() {
		String tn = "567811234RT001";
		assertEquals("**********T001", StringUtilities.getSecureForeignTaxNumber(tn));
		assertEquals("**********T001", StringUtilities.getSecureForeignTaxNumber(PREPEND_MASKING_PATTERN + tn));
	}

	@Test
	public void testFormatSecureForeignTaxNumber() {
		String tn = "567811234RT001";
		assertEquals("**********T001", StringUtilities.formatSecureForeignTaxNumber(tn));
		assertEquals("**********T001", StringUtilities.formatSecureForeignTaxNumber(PREPEND_MASKING_PATTERN
			+ tn));
	}

	@Test
	public void testFormatForeignTaxNumber() {
		String tn = "567811234RT001";
		assertEquals("567811234001", StringUtilities.formatForeignTaxNumber(tn));
		assertEquals("567811234001", StringUtilities.formatForeignTaxNumber(PREPEND_MASKING_PATTERN + tn));
	}

	@Test
	public void testGetSecureForeignTaxNumberShort() {
		String tn = "56781";
		assertEquals("*6781", StringUtilities.getSecureForeignTaxNumber(tn));
		assertEquals("*6781", StringUtilities.getSecureForeignTaxNumber(PREPEND_MASKING_PATTERN + tn));
	}

	@Test
	public void testGetSecureForeignTaxNumberShort2() {
		String tn = "5678";
		assertEquals("****", StringUtilities.getSecureForeignTaxNumber(tn));
		assertEquals("****", StringUtilities.getSecureForeignTaxNumber(PREPEND_MASKING_PATTERN + tn));
	}

	@Test
	public void testGetSecureForeignTaxNumberShort3() {
		String tn = "567";
		assertEquals("****", StringUtilities.getSecureForeignTaxNumber(tn));
		assertEquals("****", StringUtilities.getSecureForeignTaxNumber(PREPEND_MASKING_PATTERN + tn));
	}

	@Test
	public void formatValidSixDigitCanadaPostCode() {
		assertEquals("E4H 3A8", PostalCodeUtilities.formatCanadianPostalCode("E4H3A8"));
	}

	@Test
	public void formatValidSevenDigitCanadaPostCode() {
		assertEquals("K1A 0A4", PostalCodeUtilities.formatCanadianPostalCode("K1A 0A4"));
	}

	@Test
	public void formatValidThreeDigitCanadaPostCode() {
		assertEquals("V7X", PostalCodeUtilities.formatCanadianPostalCode("V7X"));
	}

	@Test
	public void validSixDigitCanadaPostCode() {
		assertTrue(PostalCode.canadaPattern.matcher("E4H3A8").matches());
	}

	@Test
	public void validSevenDigitCanadaPostCode() {
		assertTrue(PostalCode.canadaPattern.matcher("K1A 0A4").matches());
	}

	@Test
	public void validThreeDigitCanadaPostCode() {
		assertTrue(PostalCode.canadaPattern.matcher("V7X").matches());
	}

	@Test
	public void invalidCanadaPostCodeTooLong() {
		assertFalse(PostalCode.canadaPattern.matcher("A0C 9B7Z").matches());
	}

	@Test
	public void invalidCanadaPostCodeTooShort() {
		assertFalse(PostalCode.canadaPattern.matcher("K1A3V").matches());
	}

	@Test
	public void invalidCanadaPostCodeNoLetters() {
		assertFalse(PostalCode.canadaPattern.matcher("100018").matches());
	}

	@Test
	public void invalidCanadaPostCodeNoNumbers() {
		assertFalse(PostalCode.canadaPattern.matcher("KLANGO").matches());
	}

}
