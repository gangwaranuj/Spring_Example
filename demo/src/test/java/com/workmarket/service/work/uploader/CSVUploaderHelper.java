package com.workmarket.service.work.uploader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.AddressType;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.RandomUtilities;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CSVUploaderHelper extends BaseServiceIT {
	public static final String EXISTING_BUNDLE_ID = WorkUploadColumn.EXISTING_BUNDLE_ID.getUploadColumnDescription();
	public static final String NEW_BUNDLE_NAME = WorkUploadColumn.NEW_BUNDLE_NAME.getUploadColumnDescription();
	public static final String NEW_BUNDLE_DESCRIPTION = WorkUploadColumn.NEW_BUNDLE_DESCRIPTION.getUploadColumnDescription();
	public static final String CUSTOM_FIELD = WorkUploadColumn.CUSTOM_FIELD.getUploadColumnDescription();
	public static final String CLIENT_NAME = WorkUploadColumn.CLIENT_NAME.getUploadColumnDescription();
	public static final String DESCRIPTION = WorkUploadColumn.DESCRIPTION.getUploadColumnDescription();
	public static final String DESIRED_SKILLS = WorkUploadColumn.DESIRED_SKILLS.getUploadColumnDescription();
	public static final String INDUSTRY_ID = WorkUploadColumn.INDUSTRY_ID.getUploadColumnDescription();
	public static final String INDUSTRY_NAME = WorkUploadColumn.INDUSTRY_NAME.getUploadColumnDescription();
	public static final String INSTRUCTIONS = WorkUploadColumn.INSTRUCTIONS.getUploadColumnDescription();
	public static final String OWNER_EMAIL = WorkUploadColumn.OWNER_EMAIL.getUploadColumnDescription();
	public static final String OWNER_USER_NUMBER = WorkUploadColumn.OWNER_USER_NUMBER.getUploadColumnDescription();
	public static final String PROJECT_NAME = WorkUploadColumn.PROJECT_NAME.getUploadColumnDescription();
	public static final String USER_NUMBER = WorkUploadColumn.USER_NUMBER.getUploadColumnDescription();
	public static final String SUPPORT_CONTACT_EMAIL = WorkUploadColumn.SUPPORT_CONTACT_EMAIL.getUploadColumnDescription();
	public static final String SUPPORT_CONTACT_USER_NUMBER = WorkUploadColumn.SUPPORT_CONTACT_USER_NUMBER.getUploadColumnDescription();
	public static final String TEMPLATE_NUMBER = WorkUploadColumn.TEMPLATE_NUMBER.getUploadColumnDescription();
	public static final String TITLE = WorkUploadColumn.TITLE.getUploadColumnDescription();
	public static final String IGNORE = WorkUploadColumn.IGNORE.getUploadColumnDescription();
	public static final String LOCATION_ADDRESS_1 = WorkUploadColumn.LOCATION_ADDRESS_1.getUploadColumnDescription();
	public static final String LOCATION_ADDRESS_2 = WorkUploadColumn.LOCATION_ADDRESS_2.getUploadColumnDescription();
	public static final String LOCATION_CITY = WorkUploadColumn.LOCATION_CITY.getUploadColumnDescription();
	public static final String LOCATION_COUNTRY = WorkUploadColumn.LOCATION_COUNTRY.getUploadColumnDescription();
	public static final String LOCATION_DRESS_CODE = WorkUploadColumn.LOCATION_DRESS_CODE.getUploadColumnDescription();
	public static final String LOCATION_NAME = WorkUploadColumn.LOCATION_NAME.getUploadColumnDescription();
	public static final String LOCATION_NUMBER = WorkUploadColumn.LOCATION_NUMBER.getUploadColumnDescription();
	public static final String LOCATION_INSTRUCTIONS = WorkUploadColumn.LOCATION_INSTRUCTIONS.getUploadColumnDescription();
	public static final String LOCATION_POSTAL_CODE = WorkUploadColumn.LOCATION_POSTAL_CODE.getUploadColumnDescription();
	public static final String LOCATION_STATE = WorkUploadColumn.LOCATION_STATE.getUploadColumnDescription();
	public static final String LOCATION_TYPE = WorkUploadColumn.LOCATION_TYPE.getUploadColumnDescription();
	public static final String LOCATION_OFFSITE = WorkUploadColumn.LOCATION_OFFSITE.getUploadColumnDescription();
	public static final String CONTACT_EMAIL = WorkUploadColumn.CONTACT_EMAIL.getUploadColumnDescription();
	public static final String CONTACT_FIRST_NAME = "First Name (primary contact)";
	public static final String CONTACT_LAST_NAME = "Last Name (primary contact)";
	public static final String CONTACT_PHONE = WorkUploadColumn.CONTACT_PHONE.getUploadColumnDescription();
	public static final String CONTACT_PHONE_EXTENSION = WorkUploadColumn.CONTACT_PHONE_EXTENSION.getUploadColumnDescription();
	public static final String SECONDARY_CONTACT_EMAIL = WorkUploadColumn.SECONDARY_CONTACT_EMAIL.getUploadColumnDescription();
	public static final String SECONDARY_CONTACT_FIRST_NAME = WorkUploadColumn.SECONDARY_CONTACT_FIRST_NAME.getUploadColumnDescription();
	public static final String SECONDARY_CONTACT_LAST_NAME = WorkUploadColumn.SECONDARY_CONTACT_LAST_NAME.getUploadColumnDescription();
	public static final String SECONDARY_CONTACT_PHONE = WorkUploadColumn.SECONDARY_CONTACT_PHONE.getUploadColumnDescription();
	public static final String SECONDARY_CONTACT_PHONE_EXTENSION = WorkUploadColumn.SECONDARY_CONTACT_PHONE_EXTENSION.getUploadColumnDescription();
	public static final String DISTRIBUTION_METHOD = WorkUploadColumn.DISTRIBUTION_METHOD.getUploadColumnDescription();
	public static final String SUPPLIED_BY_RESOURCE = WorkUploadColumn.SUPPLIED_BY_RESOURCE.getUploadColumnDescription();
	public static final String PICKUP_LOCATION_ADDRESS_1 = WorkUploadColumn.PICKUP_LOCATION_ADDRESS_1.getUploadColumnDescription();
	public static final String PICKUP_LOCATION_ADDRESS_2 = WorkUploadColumn.PICKUP_LOCATION_ADDRESS_2.getUploadColumnDescription();
	public static final String PICKUP_LOCATION_CITY = WorkUploadColumn.PICKUP_LOCATION_CITY.getUploadColumnDescription();
	public static final String PICKUP_LOCATION_COUNTRY = WorkUploadColumn.PICKUP_LOCATION_COUNTRY.getUploadColumnDescription();
	public static final String PICKUP_LOCATION_NAME = WorkUploadColumn.PICKUP_LOCATION_NAME.getUploadColumnDescription();
	public static final String PICKUP_LOCATION_NUMBER = WorkUploadColumn.PICKUP_LOCATION_NUMBER.getUploadColumnDescription();
	public static final String PICKUP_LOCATION_POSTAL_CODE = WorkUploadColumn.PICKUP_LOCATION_POSTAL_CODE.getUploadColumnDescription();
	public static final String PICKUP_LOCATION_STATE = WorkUploadColumn.PICKUP_LOCATION_STATE.getUploadColumnDescription();
	public static final String PICKUP_LOCATION_TYPE = WorkUploadColumn.PICKUP_LOCATION_TYPE.getUploadColumnDescription();
	public static final String PICKUP_PART_VALUE = WorkUploadColumn.PICKUP_PART_VALUE.getUploadColumnDescription();
	public static final String PICKUP_SHIPPING_PROVIDER = WorkUploadColumn.PICKUP_SHIPPING_PROVIDER.getUploadColumnDescription();
	public static final String PICKUP_TRACKING_NUMBER = WorkUploadColumn.PICKUP_TRACKING_NUMBER.getUploadColumnDescription();
	public static final String RETURN_LOCATION_ADDRESS_1 = WorkUploadColumn.RETURN_LOCATION_ADDRESS_1.getUploadColumnDescription();
	public static final String RETURN_LOCATION_ADDRESS_2 = WorkUploadColumn.RETURN_LOCATION_ADDRESS_2.getUploadColumnDescription();
	public static final String RETURN_LOCATION_CITY = WorkUploadColumn.RETURN_LOCATION_CITY.getUploadColumnDescription();
	public static final String RETURN_LOCATION_COUNTRY = WorkUploadColumn.RETURN_LOCATION_COUNTRY.getUploadColumnDescription();
	public static final String RETURN_LOCATION_NAME = WorkUploadColumn.RETURN_LOCATION_NAME.getUploadColumnDescription();
	public static final String RETURN_LOCATION_NUMBER = WorkUploadColumn.RETURN_LOCATION_NUMBER.getUploadColumnDescription();
	public static final String RETURN_LOCATION_POSTAL_CODE = WorkUploadColumn.RETURN_LOCATION_POSTAL_CODE.getUploadColumnDescription();
	public static final String RETURN_LOCATION_STATE = WorkUploadColumn.RETURN_LOCATION_STATE.getUploadColumnDescription();
	public static final String RETURN_LOCATION_TYPE = WorkUploadColumn.RETURN_LOCATION_TYPE.getUploadColumnDescription();
	public static final String RETURN_PART_VALUE = WorkUploadColumn.RETURN_PART_VALUE.getUploadColumnDescription();
	public static final String RETURN_REQUIRED = WorkUploadColumn.RETURN_REQUIRED.getUploadColumnDescription();
	public static final String RETURN_SHIPPING_PROVIDER = WorkUploadColumn.RETURN_SHIPPING_PROVIDER.getUploadColumnDescription();
	public static final String RETURN_TRACKING_NUMBER = WorkUploadColumn.RETURN_TRACKING_NUMBER.getUploadColumnDescription();
	public static final String ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE = WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription();
	public static final String ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE = WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription();
	public static final String INITIAL_PER_HOUR_PRICE_RESOURCE_FEE = WorkUploadColumn.INITIAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription();
	public static final String INITIAL_PER_HOUR_PRICE_CLIENT_FEE = WorkUploadColumn.INITIAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription();
	public static final String MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE = WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE.getUploadColumnDescription();
	public static final String MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE = WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE.getUploadColumnDescription();
	public static final String FLAT_PRICE_RESOURCE_FEE = WorkUploadColumn.FLAT_PRICE_RESOURCE_FEE.getUploadColumnDescription();
	public static final String FLAT_PRICE_CLIENT_FEE = WorkUploadColumn.FLAT_PRICE_CLIENT_FEE.getUploadColumnDescription();
	public static final String MAX_NUMBER_OF_HOURS = WorkUploadColumn.MAX_NUMBER_OF_HOURS.getUploadColumnDescription();
	public static final String PER_HOUR_PRICE_RESOURCE_FEE = WorkUploadColumn.PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription();
	public static final String PER_HOUR_PRICE_CLIENT_FEE = WorkUploadColumn.PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription();
	public static final String MAX_NUMBER_OF_UNITS = WorkUploadColumn.MAX_NUMBER_OF_UNITS.getUploadColumnDescription();
	public static final String PER_UNIT_PRICE_RESOURCE_FEE = WorkUploadColumn.PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnDescription();
	public static final String PER_UNIT_PRICE_CLIENT_FEE = WorkUploadColumn.PER_UNIT_PRICE_CLIENT_FEE.getUploadColumnDescription();
	public static final String END_DATE = WorkUploadColumn.END_DATE.getUploadColumnDescription();
	public static final String END_DATE_TIME = WorkUploadColumn.END_DATE_TIME.getUploadColumnDescription();
	public static final String END_TIME = WorkUploadColumn.END_TIME.getUploadColumnDescription();
	public static final String START_DATE = WorkUploadColumn.START_DATE.getUploadColumnDescription();
	public static final String START_DATE_TIME = WorkUploadColumn.START_DATE_TIME.getUploadColumnDescription();
	public static final String START_TIME = WorkUploadColumn.START_TIME.getUploadColumnDescription();

	public static final String PASS = "pass";
	public static final String FAIL = "fail";

	public static Map<String, Map<String, String>> fixtures = Maps.newHashMap();
	public static Map<String, Map<String, String[]>> dynamicFixtures = Maps.newHashMap();

	private static Calendar rightNow;
	private static Calendar threeDaysLater;
	private static SimpleDateFormat sdf;

	private static final String OBNOXIOUS_DESCRIPTION = "\"Ball tip ham strip steak pork beef rump, beef ribs turducken andouille jerky fatback shank short loin drumstick. " +
		"Tail corned beef t-bone biltong jowl, rump boudin bacon brisket tenderloin filet mignon drumstick ham. Pork belly chuck tongue, t-bone andouille tri-tip kielbasa ham. " +
		"Frankfurter tail jerky bresaola salami corned beef. Pig biltong filet mignon, hamburger kielbasa chuck turducken rump andouille swine pork loin ribeye. Turducken " +
		"ribeye tongue pork belly, spare ribs swine pastrami ham chuck fatback. Flank brisket pig, boudin corned beef swine beef ribs strip steak cow.\n\nHamburger boudin " +
		"ground round shoulder spare ribs turkey. Corned beef pig hamburger flank fatback andouille. Ham hock chicken ham, shankle filet mignon pork loin fatback bresaola " +
		"pork venison biltong leberkäse ground round ball tip chuck. Strip steak jerky tongue spare ribs. Frankfurter meatball brisket, tri-tip pork belly venison corned " +
		"beef sirloin filet mignon jowl tenderloin short loin. Short loin spare ribs bresaola, boudin ham salami venison flank shankle cow. Beef fatback pastrami, tongue " +
		"shankle chicken meatloaf tail drumstick ball tip ribeye.\"";

	private static final String DEFAULT_CLIENT_NAME = "VIP Inc";

	public static final LocationDTO LOCATION_DTO = new LocationDTO();
	public static final LocationDTO LOCATION_DTO_WITH_NO_LOCATION_NUMBER = new LocationDTO();

	static {
		rightNow = Calendar.getInstance();
		threeDaysLater = Calendar.getInstance();
		threeDaysLater.add(Calendar.DAY_OF_MONTH, 3);
		sdf = new SimpleDateFormat("MM/dd/yy");

		Map<String, String> pass = Maps.newHashMap();
		Map<String, String> fail = Maps.newHashMap();
		fixtures.put(PASS, pass);
		fixtures.put(FAIL, fail);

		Map<String, String[]> dynamicPass = Maps.newHashMap();
		Map<String, String[]> dynamicFail = Maps.newHashMap();
		dynamicFixtures.put(PASS, dynamicPass);
		dynamicFixtures.put(FAIL, dynamicFail);

		// PASSES

		String[] blendedPerHour = {
			StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, INITIAL_PER_HOUR_PRICE_CLIENT_FEE, ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE, MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE, MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE, CONTACT_FIRST_NAME, CONTACT_LAST_NAME, CONTACT_PHONE, CONTACT_EMAIL, LOCATION_DRESS_CODE}, ","),
			StringUtils.join(new String[]{"Uploaded Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00", "$50.16", "5", "3", "Jeff", "Hicks", "865 952 2190", "Jeff@Hicks.com", "Business Casual"}, ",")
		};
		addFixture(PASS, "blended_per_hour", blendedPerHour);

		List<String> contrivedData = Lists.newArrayList();
		contrivedData.add("Name,Desc,Start Date,End Date,Start Time,End Time,Address,City,State,Zip,Pay");
		for (int i = 0; i < 17; i++) {
			contrivedData.add(
				StringUtils.join(new String[]{
						"Random Assignment " + (9478 + i), OBNOXIOUS_DESCRIPTION, sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "08:00:00", "17:00:00", "600 Pennsylvania Ave", "Washington", "DC", "20500", "$90"
				}, ",")
			);
		}
		addFixture(PASS, "contrived", contrivedData);

		String[] flatRateNewBundle = {
			StringUtils.join(new String[]{NEW_BUNDLE_NAME, NEW_BUNDLE_DESCRIPTION, TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE}, ","),
			StringUtils.join(new String[]{"My New Bundle", "My Bundle Description", "Bundle Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
			StringUtils.join(new String[]{"My New Bundle", "My Bundle Description", "Bundle Assignment 67405", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ",")
		};
		addFixture(PASS, "flat_rate_new_bundle", flatRateNewBundle);

		String[] perHour = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, PER_HOUR_PRICE_CLIENT_FEE, MAX_NUMBER_OF_HOURS}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00", "5"}, ",")
		};
		addFixture(PASS, "per_hour", perHour);

		String[] perUnit = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, PER_UNIT_PRICE_CLIENT_FEE, MAX_NUMBER_OF_UNITS}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00", "5"}, ",")
		};
		addFixture(PASS, "per_unit", perUnit);

		String[] newLocationsNoClient = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$10.00"}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 2", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "61 Maspeth Av", "Brooklyn", "NY", "11211", "$10.00"}, ",")
		};
		addFixture(PASS, "new_locations_no_client", newLocationsNoClient);

		final String locationName = RandomUtilities.generateAlphaString(5);
		final String locationNumber = RandomUtilities.generateAlphaString(5);
		final String address = "7 High Street";
		final String city = "Huntington";
		final String state = "NY";
		final String zip = "11743";

		LOCATION_DTO.setAddressTypeCode(AddressType.CLIENT_LOCATION);
		LOCATION_DTO.setName(locationName);
		LOCATION_DTO.setLocationNumber(locationNumber);
		LOCATION_DTO.setCompanyId(1L);
		BeanUtilities.copyProperties(LOCATION_DTO, createAddressDTO());

		BeanUtilities.copyProperties(LOCATION_DTO_WITH_NO_LOCATION_NUMBER, LOCATION_DTO);
		LOCATION_DTO_WITH_NO_LOCATION_NUMBER.setLocationNumber(null);

		String[] newLocationsNoDuplicate = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", address, city, state, zip, "$10.00"}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 2", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", address, city, state, zip, "$10.00"}, ",")
		};
		addFixture(PASS, "new_locations_no_duplicate", newLocationsNoDuplicate);

		String[] newLocationsAndClientNoDuplicate = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_NAME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE, CLIENT_NAME}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", locationName, address, city, state, zip, "$10.00", DEFAULT_CLIENT_NAME}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 2", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", locationName, address, city, state, zip, "$10.00", DEFAULT_CLIENT_NAME}, ",")
		};
		addFixture(PASS, "new_locations_and_client_no_duplicate", newLocationsAndClientNoDuplicate);

		String[] newLocationsNoClientMultipleCountries = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, LOCATION_COUNTRY, FLAT_PRICE_CLIENT_FEE}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "413 Graham Ave", "Brooklyn", "NY", "11211", "USA", "$10.00"}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 2", "\"Poutine, maple syrup beaver tails tourtière Yukon Jack.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "14 Singer Place", "Ottawa", "ON", "K1V 0J3", "Canada", "$10.00"}, ",")
		};
		addFixture(PASS, "new_locations_no_client_multiple_countries", newLocationsNoClientMultipleCountries);

		String[] existingLocation = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_NAME, FLAT_PRICE_CLIENT_FEE, CLIENT_NAME}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", locationName, "5", DEFAULT_CLIENT_NAME}, ",")
		};
		addFixture(PASS, "existing_locations_by_name", existingLocation);

		String[] existingLocationByNumber = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_NUMBER, FLAT_PRICE_CLIENT_FEE, CLIENT_NAME}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", locationNumber, "5", DEFAULT_CLIENT_NAME}, ",")
		};
		addFixture(PASS, "existing_location_by_number", existingLocationByNumber);

		String[] existingClientNewLocationAddress = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_NAME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, LOCATION_COUNTRY, FLAT_PRICE_CLIENT_FEE, CLIENT_NAME}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", locationName, "413 Graham Ave", "Brooklyn", "NY", "11211", "USA", "5", DEFAULT_CLIENT_NAME}, ",")
		};
		addFixture(PASS, "existing_client_new_location_with_address_by_name", existingClientNewLocationAddress);

		String[] newLocationWithProjectAndClient = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_NAME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, LOCATION_COUNTRY, FLAT_PRICE_CLIENT_FEE, CLIENT_NAME}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", locationName, "413 Graham Ave", "Brooklyn", "NY", "11211", "USA", "5", DEFAULT_CLIENT_NAME}, ",")
		};
		addFixture(PASS, "new_location_with_project_and_client", newLocationWithProjectAndClient);

		String[] newLocationNewContact = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, LOCATION_COUNTRY, FLAT_PRICE_CLIENT_FEE, CONTACT_FIRST_NAME, CONTACT_LAST_NAME}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "413 Graham Ave", "Brooklyn", "NY", "11211", "USA", "5", "Bob" + RandomUtilities.generateAlphaString(10), "Smith" + RandomUtilities.generateAlphaString(10)}, ",")
		};
		addFixture(PASS, "new_location_with_new_contact", newLocationNewContact);

		String[] fourAssignmentsWithRouting = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE, USER_NUMBER}, ","),
				StringUtils.join(new String[]{"Assignment 1234567", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
				StringUtils.join(new String[]{"Assignment 2345678", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
				StringUtils.join(new String[]{"Assignment 3456789", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
				StringUtils.join(new String[]{"Assignment 4567890", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ",")
		};
		addDynamicFixture(PASS, "four_assignments_with_routing", fourAssignmentsWithRouting);


		String[] twoGoodBundles = {
				StringUtils.join(new String[]{NEW_BUNDLE_NAME, NEW_BUNDLE_DESCRIPTION, TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE}, ","),
				StringUtils.join(new String[]{"My First Bundle", "My Bundle Description", "Bundle Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
				StringUtils.join(new String[]{"My First Bundle", "My Bundle Description", "Bundle Assignment 67405", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
				StringUtils.join(new String[]{"My Second Bundle", "My Bundle Description", "Bundle Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
				StringUtils.join(new String[]{"My Second Bundle", "My Bundle Description", "Bundle Assignment 67405", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ",")
		};
		addFixture(PASS, "two_good_bundles", twoGoodBundles);

		// The following has 2 bundles with user routing in the header.
		// NOTE: The user number should be added to the string dynamically
		String[] twoGoodBundlesWithRouting = {
				StringUtils.join(new String[]{NEW_BUNDLE_NAME, NEW_BUNDLE_DESCRIPTION, TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE, USER_NUMBER}, ","),
				StringUtils.join(new String[]{"My First Bundle", "My Bundle Description", "Bundle Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
				StringUtils.join(new String[]{"My First Bundle", "My Bundle Description", "Bundle Assignment 67405", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
				StringUtils.join(new String[]{"My Second Bundle", "My Bundle Description", "Bundle Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
				StringUtils.join(new String[]{"My Second Bundle", "My Bundle Description", "Bundle Assignment 67405", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ",")
		};
		addDynamicFixture(PASS, "two_good_bundles_with_routing", twoGoodBundlesWithRouting);

		// FAILS

		String[] badBundleMix = {
			StringUtils.join(new String[]{EXISTING_BUNDLE_ID, NEW_BUNDLE_NAME, NEW_BUNDLE_DESCRIPTION, TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE}, ","),
			StringUtils.join(new String[]{"123", "My New Bundle", "My Bundle Description", "Bundle Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
		};
		addFixture(FAIL, "bad_bundle_mix", badBundleMix);

		String[] badBundleMissingDesc = {
			StringUtils.join(new String[]{NEW_BUNDLE_NAME, TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE}, ","),
			StringUtils.join(new String[]{"My New Bundle", "Bundle Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
		};
		addFixture(FAIL, "bad_bundle_missing_desc", badBundleMissingDesc);

		String[] badBundleMissingName = {
			StringUtils.join(new String[]{NEW_BUNDLE_DESCRIPTION, TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE}, ","),
			StringUtils.join(new String[]{"My Bundle Description", "Bundle Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
		};
		addFixture(FAIL, "bad_bundle_missing_name", badBundleMissingName);

		String[] goodBundleBadWork = {
			StringUtils.join(new String[]{NEW_BUNDLE_NAME, NEW_BUNDLE_DESCRIPTION, TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE}, ","),
			StringUtils.join(new String[]{"My New Bundle", "My Bundle Description", "Bundle Assignment 67404", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ","),
			// missing title
			StringUtils.join(new String[]{"My New Bundle", "My Bundle Description", "", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "181 Lenox St", "Chicago", "IL", "60302", "$76.00"}, ",")
		};
		addFixture(FAIL, "good_bundle_bad_work", goodBundleBadWork);

		String[] newLocationsNewClient = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, FLAT_PRICE_CLIENT_FEE, CLIENT_NAME}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "413 Graham Ave", "Brooklyn", "NY", "11211", "$10.00", "Beer Street"}, ",")
		};
		addFixture(FAIL, "new_locations_new_client", newLocationsNewClient);

		String[] existingClientNewLocation = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_NAME, FLAT_PRICE_CLIENT_FEE, CLIENT_NAME}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "VIP Headquarters", "5", DEFAULT_CLIENT_NAME}, ",")
		};
		addFixture(FAIL, "existing_client_new_location_by_name", existingClientNewLocation);

		String[] nonexistentLocationByNumber = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_NUMBER, FLAT_PRICE_CLIENT_FEE, CLIENT_NAME}, ","),
				StringUtils.join(new String[]{"Uploaded Assignment 1", "\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"", sdf.format(rightNow.getTime()), sdf.format(threeDaysLater.getTime()), "13:25:00", "14:00:00", "123456", "5", DEFAULT_CLIENT_NAME}, ",")
		};
		addFixture(FAIL, "nonexistent_location_by_number", nonexistentLocationByNumber);

		addFixture(PASS, CLIENT_NAME, Lists.newArrayList(DEFAULT_CLIENT_NAME));
	}

	public static String addFixture(String passOrFail, String key, String[] fixture) {
		fixtures.get(passOrFail).put(key, StringUtils.join(fixture, "\n"));
		return fixtures.get(passOrFail).get(key);
	}

	public static String addFixture(String passOrFail, String key, List<String> fixture) {
		fixtures.get(passOrFail).put(key, StringUtils.join(fixture, "\n"));
		return fixtures.get(passOrFail).get(key);
	}

	public static void addDynamicFixture(String passOrFail, String key, String[] fixture) {
		dynamicFixtures.get(passOrFail).put(key, fixture);
	}

	public static String completeDynamicFixture(String passOrFail, String key, String addToEnd) {
		String[] dynamicFixture = dynamicFixtures.get(passOrFail).get(key);
		// skip header line
		for (int i=1; i<dynamicFixture.length; i++) { dynamicFixture[i] += ", " + addToEnd; }
		return addFixture(passOrFail, key, dynamicFixture);
	}

	public static void addBlendedPerHourWithClientCompany(String key, String companyName) {
		String[] blendedPerHourWithClient = {
				StringUtils.join(new String[]{TITLE, DESCRIPTION, START_DATE, END_DATE, START_TIME, END_TIME, LOCATION_NAME, LOCATION_ADDRESS_1, LOCATION_CITY, LOCATION_STATE, LOCATION_POSTAL_CODE, INITIAL_PER_HOUR_PRICE_CLIENT_FEE, ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE, MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE, MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE, CLIENT_NAME, CONTACT_FIRST_NAME, CONTACT_LAST_NAME, CONTACT_PHONE, CONTACT_EMAIL, LOCATION_DRESS_CODE}, ","),
				StringUtils.join(new String[]{ "Uploaded Assignment 67404","\"Hamburger, boudin ground round, shoulder spare ribs turkey.\"",sdf.format(rightNow.getTime()),sdf.format(threeDaysLater.getTime()),"13:25:00","14:00:00","My baby's place","181 Lenox St","Chicago","IL","60302","$76.00","$50.16","5","3",companyName,"Jeff","Hicks","865 952 2190","Jeff@Hicks.com","Business Casual" }, ",")
		};

		addFixture(PASS, key, blendedPerHourWithClient);
	}
}
