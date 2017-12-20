package com.workmarket.configuration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class Constants {

	private Constants() {}

	public static final String PROD_BASE_URL = "https://www.workmarket.com";
	public static final String PROD_PUBLIC_BASE_URL = "http://www.workmarket.com";
	public static final String ACCOUNT_REGISTER_MESSAGE_GROUP_ID = "AccountRegister#%s";

	public static final long WORKMARKET_SYSTEM_USER_ID = 1;
	public static final long FRONT_END_USER_ID = 2;
	public static final long BACK_END_USER_ID = 3;
	public static final long ASYNCHRONOUS_USER_ID = 4;

	public static final long JEFF_WALD_USER_ID = 1003;
	public static final long LIZ_SUH_USER_ID = 7225;
	public static final long BRIDGET_QUINN_SUPPORT_USER_ID = 78357;
	public static final String RUSSELL_SACHS_SUGAR_ID = "1240c8f3-fc2f-460f-8219-532732dcebc9";
	public static final String ACCOUNT_MANAGEMENT_TEAM_EMAIL = "amteam@workmarket.com";
	public static final String DEV_TEAM_EMAIL = "dev@workmarket.com";


	public static final int EQUALS = 0;
	public static final int LESS_THAN = -1;
	public static final int GREATER_THAN = 1;

	public static final Set<String> COUNTRIES_WITH_NO_STATE = ImmutableSet.of(
		"MC", // Monaco
		"SM", // San Marino
		"MT", // Malta
		"VA" // Vatican City
	);
	// source: https://gist.github.com/kennwilson/3902548
	// note: South Africa (ZA) removed from the list as google returns postal codes there.
	public static final Set<String> COUNTRIES_WITH_NO_POSTALCODE = ImmutableSet.of(
		"AO", // Angola
		"AG", // Antigua and Barbuda
		"AW", // Aruba
		"BS", // Bahamas
		"BZ", // Belize
		"BJ", // Benin
		"BW", // Botswana
		"BF", // Burkina Faso
		"BI", // Burundi
		"CM", // Cameroon
		"CF", // Central African Republic
		"KM", // Comoros
		"CG", // Congo
		"CD", // Congo, the Democratic Republic of the
		"CK", // Cook Islands
		"CI", // Cote d'Ivoire
		"DJ", // Djibouti
		"DM", // Dominica
		"GQ", // Equatorial Guinea
		"ER", // Eritrea
		"FJ", // Fiji
		"TF", // French Southern Territories
		"GM", // Gambia
		"GH", // Ghana
		"GD", // Grenada
		"GN", // Guinea
		"GY", // Guyana
		"HK", // Hong Kong
		"IE", // Ireland
		"JM", // Jamaica
		"KE", // Kenya
		"KI", // Kiribati
		"MO", // Macao
		"MW", // Malawi
		"ML", // Mali
		"MR", // Mauritania
		"MU", // Mauritius
		"MS", // Montserrat
		"NR", // Nauru
		"AN", // Netherlands Antilles
		"NU", // Niue
		"KP", // North Korea
		"PA", // Panama
		"QA", // Qatar
		"RW", // Rwanda
		"KN", // Saint Kitts and Nevis
		"LC", // Saint Lucia
		"ST", // Sao Tome and Principe
		"SA", // Saudi Arabia
		"SC", // Seychelles
		"SL", // Sierra Leone
		"SB", // Solomon Islands
		"SO", // Somalia
		"SR", // Suriname
		"SY", // Syria
		"TZ", // Tanzania, United Republic of
		"TL", // Timor-Leste
		"TK", // Tokelau
		"TO", // Tonga
		"TT", // Trinidad and Tobago
		"TV", // Tuvalu
		"UG", // Uganda
		"AE", // United Arab Emirates
		"VU", // Vanuatu
		"YE", // Yemen
		"ZW" // Zimbabwe
	);

	public static final Set<Long> SYSTEM_USER_IDS = ImmutableSet.of(
			WORKMARKET_SYSTEM_USER_ID, FRONT_END_USER_ID, BACK_END_USER_ID, ASYNCHRONOUS_USER_ID);

	// User IDs that have access to invoices of any company
	public static final List<Long> ACCESS_ALL_INVOICES_USER_IDS = ImmutableList.of(
			WORKMARKET_SYSTEM_USER_ID,
			JEFF_WALD_USER_ID,
			LIZ_SUH_USER_ID
	);

	/*
	 * All transactional emails From: hi@myworkmarket.com Display name: Work Market Reply to: replies@workmarket.com (note: workmarket, not myworkmarket)
	 */
	public static final Long EMAIL_USER_ID_TRANSACTIONAL = 2L;
	/*
	 * Emails sent via Invite or Invite reminder From: hi@wminvites.com Display Name: Work Market Reply to: user email who clicked send **(this is the item we are most hotly debating right now)
	 */
	public static final Long EMAIL_USER_ID_INVITES = 3L;

	// system users for invitations only
	public static final String EMAIL_INVITATION_REPLY_TO = "hi@wminvites.com";
	public static final String EMAIL_CLIENT_SERVICES = "clientservice@workmarket.com";

	public static final String EMAIL_DO_NOT_REPLY = "do_not_reply@workmarket.com";
	public static final String EMAIL_REPORTS_REPLY = "reports@workmarket.com";

	public static final Double EMAIL_CONFIRM_ADJUSTMENT_HRS = 24d;
	public static final Double CONFIRM_ADJUSTMENT_HRS = 24d;

	public static final String INVOICES_EMAIL = "invoices@workmarket.com";
	public static final String SUPPORT_EMAIL = "hi@workmarket.com";

	public static final int FORUM_MAX_POST_LENGTH = 1500;

	// email display name
	public static final String EMAIL_DISPLAY_NAME = " via Work Market";
	// email templates
	public static final String EMAIL_HEADER_TEMPLATE = "HeaderEmailTemplate";
	public static final String EMAIL_FOOTER_TEMPLATE = "FooterEmailTemplate";
	public static final String EMAIL_TEMPLATE_DIRECTORY_PATH = "/template/email";
	public static final String EMAIL_SUBJECT_TEMPLATE_DIRECTORY_PATH = "/template/email/subject";
	public static final String EMAIL_TEMPLATE_EXTENSION = ".vm";

	public static final String SMS_HEADER_TEMPLATE = "HeaderSMSTemplate";
	public static final String SMS_FOOTER_TEMPLATE = "FooterSMSTemplate";
	public static final String SMS_TEMPLATE_DIRECTORY_PATH = "/template/sms";
	public static final String SMS_TEMPLATE_EXTENSION = ".vm";

	public static final String USER_NOTIFICATION_HEADER_TEMPLATE = "HeaderNotificationTemplate";
	public static final String USER_NOTIFICATION_FOOTER_TEMPLATE = "FooterNotificationTemplate";
	public static final String USER_NOTIFICATION_TEMPLATE_DIRECTORY_PATH = "/template/notification";
	public static final String USER_NOTIFICATION_TEMPLATE_EXTENSION = ".vm";

	public static final String VOICE_TEMPLATE_DIRECTORY_PATH = "/template/voice";
	public static final String VOICE_TEMPLATE_EXTENSION = ".vm";

	// Default Group Names
	public static final String LEGACY_MY_PAID_RESOURCES_GROUP_NAME = "My Paid Resources";
	public static final String MY_PAID_RESOURCES_GROUP_NAME = "My Paid Workers";
	public static final String AUTO_GENERATED_GROUP_DESCRIPTION = "Auto generated group ";
	public static final String MY_COMPANY_FOLLOWERS = "My Followers";

	// Default API user Names
	public static final String DEFAULT_API_USER_FIRST_NAME = "API";
	public static final String DEFAULT_API_USER_LAST_NAME = "API";
	public static final String DEFAULT_API_USER_EMAIL_FORMAT_USING_COMPANY_UUID = "api+%s@workmarket.com";
	public static final String DEFAULT_API_USER_EMAIL_FORMAT_USING_COMPANY_ID = "api+%d@workmarket.com";

	// field sizes
	public static final int TEXT_LONG = 65000;
	public static final int TEXT_SHORT = 200;

	// Calendar
	public static final String CALENDAR_DIRECTORY_PATH = "/tmp/calendar";
	public static final String DEFAULT_CALENDAR_FILE_NAME = "myCalendar_";
	public static final String CALENDAR_EXTENSION = ".ics";
	public static final String DEFAULT_TIMEZONE = "America/New_York";

	public static final String PDF_EXTENSION = ".pdf";

	// Field Length BEGIN
	public static final int FIRST_NAME_MIN_LENGTH = 1;
	public static final int FIRST_NAME_MAX_LENGTH = 50;

	public static final int LAST_NAME_MIN_LENGTH = 1;
	public static final int LAST_NAME_MAX_LENGTH = 50;

	public static final int PHONE_NUMBER_MIN_LENGTH = 0;
	public static final int PHONE_NUMBER_MAX_LENGTH = 25;

	public static final int EMAIL_MIN_LENGTH = 0;
	public static final int EMAIL_MAX_LENGTH = 255;

	public static final int PASSWORD_MIN_LENGTH = 6;

	public static final int ADDRESS_LINE_1_MAX_LENGTH = 200;

	public static final int ADDRESS_LINE_2_MAX_LENGTH = 100;

	public static final int CITY_MIN_LENGTH = 1;
	public static final int CITY_MAX_LENGTH = 100;

	public static final int STATE_MAX_LENGTH = 100;

	public static final int COUNTRY_MIN_LENGTH = 2;
	public static final int COUNTRY_MAX_LENGTH = 45;

	public static final int POSTAL_CODE_MAX_LENGTH = 9;

	public static final int NAME_MIN_LENGTH = 3;
	public static final int NAME_MAX_LENGTH = 100;

	public static final int TYPE_MAX_LENGTH = 20;
	public static final int ENUM_MAX_LENGTH = 20;

	public static final int TEXT_MIN_LENGTH = 0;
	public static final int TEXT_MAX_LENGTH = 65000;

	// System ACLRoles
	public static final String ADMINISTRATOR_ROLE = "Administrator";

	public static final String SECOND = "SECOND";
	public static final String MINUTE = "MINUTE";
	public static final String HOUR = "HOUR";
	public static final String DAY = "DAY";
	public static final String WEEK = "WEEK";

	public static final int MAX_TRAVEL_DISTANCE = 60;
	public static final BigDecimal DEFAULT_MAX_TRAVEL_DISTANCE = BigDecimal.valueOf(MAX_TRAVEL_DISTANCE);
	public static final BigDecimal MAX_GROUP_SEND_RADIUS = BigDecimal.valueOf(100.0);

	public static final int MIN_FILENAME = 1;
	public static final int MAX_FILENAME = 200;

	public static final int MIN_PATH = 1;
	public static final int MAX_PATH = 500;

	// Types of work objects
	public static final String WORK_TYPE_ASSIGNMENT = "assignment";
	public static final String WORK_TYPE_BUNDLE = "bundle";

	public static final int PROFILE_COMPLETENESS_THRESHOLD = 60;

	public static final int PROFILE_MAX_SKILLS = 250;
	public static final int PROFILE_MAX_TOOLS = 250;
	public static final int PROFILE_MAX_SPECIALTIES = 250;

	public static final String DEFAULT_COMPANY_NAME = "Sole Proprietor";

	public static final int LINKEDIN_STRING_FIELD_MAX = 1000;

	// Default percent to calculate low balance amount
	public static final Integer LOW_BALANCE_PERCENTAGE = 20;

	public static final int WORK_RESOURCE_CHECKIN_REMINDER_MINUTES = 10;
	public static final int WORK_RESOURCE_CHECKIN_GRACE_PERIOD_MINUTES = 5;
	public static final int WORK_RESOURCE_LATE_LABEL_GRACE_PERIOD_MINUTES = 15;

	public static final int WORK_DELIVERABLE_DUE_REMINDER_THRESHOLD_HOURS = 12;
	public static final int WORK_DELIVERABLE_DUE_GRACE_PERIOD_HOURS = 0;

	public static final int MAX_RESOURCES_PER_ASSIGNMENT = 200;
	public static final int MAX_WORKSEND_RESOURCES_PER_ASSIGNMENT = 110;
	public static final int MAX_RESOURCES_SUGGESTION_RESULTS_FOR_ASSIGNMENT = 10;

	public static final int VOICE_CALL_FAILED_PROMPTS_CUTOFF = 3;

	// Work Notify
	public static final int WORK_NOTIFY_THROTTLE_HOURS = 1;

	public static final String CONTRACT_VERSION_ASSET_TYPE = "Agreement";

	public static final Integer DEFAULT_SESSION_DURATION_IN_MINUTES = 60;

	public static final BigDecimal DEFAULT_SPEND_LIMIT = new BigDecimal(750);

	public static final String DEFAULT_CURRENCY = "USD";

	public static final Integer INVITATIONS_PER_DAY_PER_COMPANY_LIMIT = 200;

	public static final Integer MAX_PAYMENT_TERMS_DAYS = 120;

	public static final Long WM_COMPANY_ID = 1L;
	public static final Long WM_SUPPORT_COMPANY_ID = 26311L;
	public static final Set<Long> FORUM_ADMINS_COMPANY_IDS = ImmutableSet.of(WM_COMPANY_ID, WM_SUPPORT_COMPANY_ID);
	public static final String WM_TIME_ZONE = "US/Eastern";
	public static final Long WM_TIME_ZONE_ID = 595L;
	public static final String WM_POSTAL_CODE = "10018";
	public static final String WM_NAME = "WORK MARKET INC.";
	public static final String WM_TAX_POSTAL_CODE = "11743";
	public static final String WM_TAX_ADDRESS_LINE_1 = "7 HIGH ST";
	public static final String WM_TAX_ADDRESS_LINE_2 = "SUITE 407";
	public static final String WM_TAX_CITY = "HUNTINGTON";
	public static final String WM_TAX_STATE = "NY";
	public static final String WM_TAX_COUNTRY = "USA";
	public static final String WM_TAX_NAME = "RESOURCES ENTERPRISE SERVICES LLC";
	public static final String WM_TAX_PHONE = "1-212-229-WORK(9675)";
	public static final String WM_TAX_EIN = "46-1974888";

	public static final String WM_NET_MONEY_SWITCHOVER_DATE = "2011-05-16T04:00:00Z";
	public static final TimeZone EST_TIME_ZONE = TimeZone.getTimeZone(WM_TIME_ZONE);
	public static final Long WM_TIME_INDUSTRY_ID = 1000L;

	public static final int API_TOKEN_LENGTH = 20;
	public static final int API_SECRET_LENGTH = 40;

	public static final Integer WORK_REPORT_CSV_EXPIRATION_HOURS = 48;

	public static final Integer ASSET_BUNDLE_EXPIRATION_HOURS = 48;

	public static final Integer ASSET_AVATAR_SMALL_THUMBNAIL_WIDTH = 48;
	public static final Integer ASSET_AVATAR_SMALL_THUMBNAIL_HEIGHT = 48;
	public static final Integer ASSET_AVATAR_LARGE_THUMBNAIL_WIDTH = 144;
	public static final Integer ASSET_AVATAR_LARGE_THUMBNAIL_HEIGHT = 144;
	public static final Integer ASSET_ATTEMPT_RESPONSE_LARGE_THUMBNAIL_WIDTH = 480;
	public static final Integer ASSET_ATTEMPT_RESPONSE_LARGE_THUMBNAIL_HEIGHT = 480;
	public static final Integer ASSET_DESCRIPTION_TEXT_LENGTH = 512;

	public static final Integer WORK_ASSET_SMALL_THUMBNAIL_WIDTH = 120;
	public static final Integer WORK_ASSET_SMALL_THUMBNAIL_HEIGHT = 120;
	public static final Integer WORK_ASSET_LARGE_THUMBNAIL_WIDTH = 480;
	public static final Integer WORK_ASSET_LARGE_THUMBNAIL_HEIGHT = 720;

	public static final Integer NUMBER_OF_DAYS_TO_CONSIDER_USERS_AS_NEW = 90;
	public static final int STANDARD_CALENDAR_YEAR_DAYS = 365;

	public static final BigDecimal DAYS_PER_MONTH = BigDecimal.valueOf(365 / 12);
	public static final BigDecimal WEEKS_PER_MONTH = DAYS_PER_MONTH.divide(BigDecimal.valueOf(7), MathContext.DECIMAL32);

	public static final String INVOICE_BUNDLE_NUMBER_PREFIX = "BUN";
	public static final String STATEMENT_NUMBER_PREFIX = "STA";

	public static final String PASSWORD_VALIDATION_REGEX = "^[a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*]{8,}$";
	public static final String PASSWORD_DIGIT_VALIDATION_REGEX = ".*\\d.*";

	public static final String EMAIL_INVOICE_DATE_FORMAT = "MMM d yyyy";

	public static final String NO_STATE = "NO PROVINCE";
	public static final String NO_POSTALCODE = "NOCODE";

	/**
	 * Days in which we will send the overdue warning
	 */
	public static final List<Integer> LOCKED_ACCOUNT_OVERDUE_WARNING_DAYS = ImmutableList.of(0, 1, 2);
	/**
	 * Days that we will give the user to pay it's overdue assignments before locking it
	 */
	public static final Integer LOCKED_ACCOUNT_WINDOW_DAYS = 3;

	public static final int GROUP_SEND_RESOURCES_LIMIT = 150;

	public static final int UPLOAD_SEND_RESOURCES_LIMIT = 50;

	public static final int MAX_UPLOAD_ASSIGNMENTS = 3000;

	public static final long MAX_UPLOAD_SIZE = 150 * 1024 * 1024; // 150MB

	public static final String CSV_EXTENSION = ".csv";
	public static final String ZIP_EXTENSION = ".zip";

	public static final String EXPORT_SEARCH_CSV_DIRECTORY = "/tmp";

	public static final int ASSIGNMENT_AGE_ALERT_DAYS = 15;

	public static final String TEMPORARY_FILE_DIRECTORY = "/tmp/";

	public static final String CUSTOM_REPORTS_DATE_FORMAT = "yyyy-MM-dd HH:mm";

	public static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("100000.00");

	public static final BigDecimal DEFAULT_ACCOUNTS_PAYABLE_LIMIT = new BigDecimal("1000.00");
	public static final BigDecimal DEFAULT_WORK_FEE_PERCENTAGE = new BigDecimal("10.00");

	public static final BigDecimal MAX_WORK_FEE = new BigDecimal("400.00");
	public static final BigDecimal MAX_AP_BALANCE_LIMIT = new BigDecimal("100000000.00");
	public static final BigDecimal MAX_WORK_FEE_PERCENTAGE = DEFAULT_WORK_FEE_PERCENTAGE.divide(BigDecimal.valueOf(100));

	public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

	public static final int PRICING_STRATEGY_ROUND_SCALE = 8;

	public static final BigDecimal TAX_FORM_1099_GENERATION_AMOUNT_THRESHOLD = BigDecimal.valueOf(600);

	/**
	 * Percentages which when reached will trigger the throughput reports. Important to preserve the order
	 */
	public static final List<Integer> SUBSCRIPTION_THROUGHPUT_THRESHOLD_PERCENTAGES = ImmutableList.of(25, 50, 75, 100);

	public static final int MAX_CUSTOM_REPORT_CSV_SIZE = 4194304;
	public static final int MAX_ASSIGNMENT_LIST_RESULTS = 100; // Used in API

	public static final int USER_NUMBER_IDENTIFIER_LENGTH = 8;
	public static final int COMPANY_NUMBER_IDENTIFIER_LENGTH = 8;
	public static final int WORK_NUMBER_IDENTIFIER_LENGTH = 10;

	public static final String REGISTER_RESOURCE_FIND_WORK = "findwork";
	public static final String REGISTER_RESOURCE_MANAGE_LABOR = "managelabor";
	public static final String TIMESTAMP_DUMMY_VALUE = "1231110600";

	public static final int NEW_USER_DAYS = 30;

	public static final String GLOBAL_CASH_CARD_BANK_NAME = "Global Cash Card";
	public static final String GOOGLE_API_KEY_RESTRICTED_REFERRERS = "AIzaSyAWD12qVRbpnGyNF_fmYMERR0gyvdbHNvE";
	public static final int GOOGLE_RECAPTCHA_ENABLED_ON_FAILED_ATTEMPTS = 1;
	public static final String GOOGLE_RECAPTCHA_RESPONSE_PARAMEMTER_NAME = "g-recaptcha-response";

	public static final String MBO_COMPANY_NUMBER = "00023407";

	/* NETWORK */
	public static final Long WORKMARKET_NETWORK_ID = 1L;

	public static final String TAX_FORM_1099_PDF_FILENAME_PREFIX = "f1099_";
	public static final String TAX_FORM_1099_PDF_TEMPLATE_FILEPATH_PREFIX = "classpath:files/f1099msc_";

	public static final Long WORKERS_COMP_INSURANCE_ID = 1000L;

	public static final int BULK_OPERATIONS_BUFFER_SIZE = 300;

	public static final int BULK_INVITE_REQUEST_SIZE = 50;

	public static final Long DAY_IN_SECONDS = 86400L;

	public static final BigDecimal EARTHS_CIRCUMFERENCE_AS_MAX_TRAVEL_DISTANCE_IN_MILES = new BigDecimal(24901);
	public static final BigDecimal MAX_TRAVEL_DISTANCE_IN_MILES = new BigDecimal(250);

	public static final String LANGUAGE_CODE_ENGLISH = "en-us";

	public static final String VENDOR_POOLS_FEATURE = "VendorPools";
	public static final String MULTIPLE_APPROVALS_FEATURE = "MultipleApprovals";

	public static final String ORG_UNITS_JWT_CLAIM_KEY = "orgUnitUUIDs";

	public static final String SEARCH_SERVICE_GROUP = "group.from.search.service";
	public static final String SEARCH_SERVICE_WORKFEED = "workfeed.from.search.service";
	public static final String SEARCH_SERVICE_WORK = "work.from.search.service";
	public static final String RECOMMENDATION_SERVICE_WORKSEND = "worksend.from.service";
	public static final String INDEXING_TO_KAFKA_ONLY = "indexing.to.kafka.only";
}
