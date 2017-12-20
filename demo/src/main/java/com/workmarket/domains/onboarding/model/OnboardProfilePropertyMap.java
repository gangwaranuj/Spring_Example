package com.workmarket.domains.onboarding.model;

import java.util.HashMap;

/**
 * OnboardProfilePropertyMap represents a hash map of updatable key/value pairs
 * applied to a Profile object.
 *
 * Created by ianha on 8/27/14
 */
public class OnboardProfilePropertyMap extends HashMap<String, String> {
	public static final String MOBILE_CODE              = "mobilePhoneInternationalCode.callingCodeId";
	public static final String MOBILE_PHONE             = "mobilePhone";
	public static final String SMS_CODE                 = "smsPhoneInternationalCode.callingCodeId";
	public static final String SMS_PHONE                = "smsPhone";
	public static final String WORK_CODE                = "workPhoneInternationalCode.callingCodeId";
	public static final String WORK_PHONE               = "workPhone";
	public static final String OPERATING_AS_IND_FLAG    = "operatingAsIndividualFlag";
	public static final String COMPANY_NAME             = "name";
	public static final String COMPANY_WEBSITE          = "website";
	public static final String COMPANY_NUM_WORKERS_ENUM = "companyNumWorkersEnum";
	public static final String COMPANY_YEAR_FOUNDED     = "yearFounded";
	public static final String COMPANY_OVERVIEW         = "overview";
	public static final String FIRST_NAME               = "firstName";
	public static final String LAST_NAME                = "lastName";
	public static final String JOB_TITLE                = "jobTitle";
	public static final String OVERVIEW                 = "overview";
	public static final String YEARS_OF_EXPERIENCE      = "yearsOfExperience";
	public static final String GENDER                   = "gender";
	public static final String ADDRESS1                 = "address1";
	public static final String ADDRESS2                 = "address2";
	public static final String CITY                     = "city";
	public static final String STATE                    = "state";
	public static final String POSTAL_CODE              = "postalCode";
	public static final String MAX_TRAVEL_DISTANCE      = "maxTravelDistance";
	public static final String COUNTRY                  = "country";
	public static final String ADDRESS_TYPE             = "addressType";
	public static final String VIDEO_WATCHED_ON         = "onboardVideoWatchedOn";
	public static final String LATITUDE                 = "latitude";
	public static final String LONGITUDE                = "longitude";
	public static final String SECONDARY_EMAIL          = "secondaryEmail";

	public OnboardProfilePropertyMap() {
		super();
	}

	public String getMobileCode() {
		return get(MOBILE_CODE);
	}

	public String getSmsCode() {
		return get(SMS_CODE);
	}

	public String getWorkCode() {
		return get(WORK_CODE);
	}
}
