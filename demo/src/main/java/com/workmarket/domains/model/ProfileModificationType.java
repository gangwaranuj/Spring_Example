package com.workmarket.domains.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
@Entity(name="profileModificationType")
@Table(name="profile_modification_type")
public class ProfileModificationType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;
	
	public static final String  USER_NAME = "userName";
	public static final String  COMPANY_NAME = "companyName";
	public static final String  COMPANY_OVERVIEW = "companyOverview";
	public static final String  COMPANY_WEBSITE = "companyWebsite";
	public static final String  USER_AVATAR = "userPhoto";
	public static final String  COMPANY_AVATAR = "companyPhoto";
	public static final String  MOBILE_PHONE_NUMBER = "mobileNumber";
	public static final String  SMS_PHONE_NUMBER = "smsNumber";
	public static final String  WORK_PHONE_NUMBER = "workNumber";
	public static final String  USER_OVERVIEW = "summary";
	public static final String  PROFESSIONAL_REFERENCE = "reference";
	public static final String  RESUME = "resume";
	
	//This won't be registered as profile modifications anymore
	public static final String  LICENSE = "newLicense";
	public static final String  CERTIFICATION = "newCertification";
	public static final String  INSURANCE_ADDED = "newInsurance";
	public static final String  SKILL_ADDED = "newSkill";
	
	public static final String  ASSESSMENT = "assessment";
	public static final String  INDUSTRIES = "industries";
	public static final String  INDUSTRY = "industry";
	public static final String  BACKGROUND_CHECK = "background";
	public static final String  DRUG_TEST = "drug";
	public static final String  WORKING_HOURS = "hours";
	public static final String  LANE_ASSOCIATION = "lanes";
	public static final String  RATING = "rating";
	public static final String  MAX_TRAVEL_DISTANCE = "travelDistance";
	public static final String  HOURLY_RATE = "rate";
	public static final String  ADDRESS = "address";
	public static final String  COMPANY_TYPE = "companyType";
	public static final String  DOCUMENT = "document";
		
	public ProfileModificationType(){
		super();
	}
	
	public ProfileModificationType(String code){
		super(code);
	}
}
