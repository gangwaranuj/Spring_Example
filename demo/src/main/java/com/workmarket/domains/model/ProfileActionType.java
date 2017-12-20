package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="profileActionType")
@Table(name="profile_action_type")
public class ProfileActionType extends LookupEntity {

	private static final long serialVersionUID = 1L;
	
	private Integer weight;
	
	public static final String TAGS = "tags";
	public static final String CERTIFICATIONS = "certifications";
	public static final String LICENSES = "licenses";
	public static final String BANK = "bank";
	public static final String TAX = "tax";
	public static final String BACKGROUND_CHECK = "background";
	public static final String DRUG_TEST = "drugTest";
	public static final String SKILLS = "skills";
	public static final String LINKEDIN = "linkedin";
	public static final String WORKMARKET_101 = "wm101";
	public static final String TESTS = "tests";
	public static final String PHOTO = "photo";
	public static final String EDUCATION = "education";
	public static final String EMPLOYMENT_HISTORY = "employment";
	public static final String INSURANCE = "insurance";
	public static final String COMPANY_NAME = "coName";
	public static final String COMPANY_WEBSITE = "coWebsite";
	public static final String COMPANY_OVERVIEW = "coOverview";
	public static final String COMPANY_LOGO = "coLogo";
	public static final String LANGUAGES = "languages";
	public static final String HOURLY_RATE = "hrRate";
	public static final String EXCLUDED_ZIP_CODES = "zipCodes";
	public static final String WORKING_HOURS = "hours";
	public static final String PERSONAL_ADDRESS = "personaladd";
	public static final String COMPANY_ADDRESS = "companyadd";
	public static final String TRAVEL_DISTANCE = "travel";
	public static final String LANE_3 = "lane3";
	public static final String RESUME = "resume";
	public static final String TIMEZONE = "timezone";
	
	public ProfileActionType(){}
	
	public ProfileActionType(String code){
		super(code);
	}

	@Column(name="weight", nullable = false)
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}
