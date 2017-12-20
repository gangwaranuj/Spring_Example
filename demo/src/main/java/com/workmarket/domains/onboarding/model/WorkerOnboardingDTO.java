package com.workmarket.domains.onboarding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import com.workmarket.api.v2.model.ApiJobTitleDTO;
import com.workmarket.domains.model.ImageDTO;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by ianha on 5/14/14
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkerOnboardingDTO {
	public static final String ID = "id";
	public static final String PHONES = "phones";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String EMAIL = "email";
	public static final String JOB_TITLE = "jobTitle";
	public static final String ADDRESS = "address";
	public static final String ADDRESS1 = "address1";
	public static final String ADDRESS2 = "address2";
	public static final String CITY = "city";
	public static final String STATE_SHORT_NAME = "stateShortName";
	public static final String COUNTRY_ISO = "countryIso";
	public static final String POSTAL_CODE = "postalCode";
	public static final String MAX_TRAVEL_DISTANCE = "maxTravelDistance";
	public static final String OVERVIEW = "overview";
	public static final String INDIVIDUAL = "individual";
	public static final String WEBSITE = "companyWebsite";
	public static final String COMPANY_NAME = "companyName";
	public static final String COMPANY_OVERVIEW = "companyOverview";
	public static final String COMPANY_YEAR_FOUNDED = "companyYearFounded";
	public static final String COMPANY_EMPLOYEES = "companyEmployees";
	public static final String COMPANY_LOGO_URL = "logo";
	public static final String INDUSTRIES = "industries";
	public static final String YEARS_OF_EXPERIENCE = "yearsOfExperience";
	public static final String VIDEO_WATCHED = "videoWatched";
	public static final String GENDER = "gender";
	public static final String PROFILE_IMAGE_URL = "avatar";
	public static final String COUNTRY_PHONE_CODES = "countryPhoneCodes";
	public static final String IS_LAST_STEP = "isLastStep";
	public static final String SKILLS = "skills";

	public static final List<String> ALL_PROFILE_FIELDS = Lists.newArrayList(ID, VIDEO_WATCHED, PHONES, FIRST_NAME, LAST_NAME, EMAIL, JOB_TITLE, GENDER,
			ADDRESS1, ADDRESS2, CITY, STATE_SHORT_NAME, COUNTRY_ISO, POSTAL_CODE, MAX_TRAVEL_DISTANCE, OVERVIEW, INDUSTRIES, INDIVIDUAL, WEBSITE, COMPANY_NAME, COMPANY_OVERVIEW,
			COMPANY_YEAR_FOUNDED, COMPANY_EMPLOYEES, COMPANY_LOGO_URL, GENDER, YEARS_OF_EXPERIENCE, PROFILE_IMAGE_URL, COUNTRY_PHONE_CODES, SKILLS);

	@Size(max = 50)
	private String firstName;
	@Size(max = 50)
	private String lastName;
	@Pattern(regexp = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
	private String secondaryEmail;
	@Pattern(regexp = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
	private String email;
	//@Size(max = 128)
	private ApiJobTitleDTO jobTitle;
	@Size(max = 10)
	private String gender;
	@Size(max = 200)
	private String address1;
	@Size(max = 100)
	private String address2;
	@Size(max = 100)
	private String city;
	@Size(max = 100)
	private String stateShortName;
	@Size(max = 9)
	private String postalCode;
	@Size(max = 2)
	private String countryIso;
	@Min(5)
	@Max(250)
	private Integer maxTravelDistance;
	private String latitude;
	private String longitude;
	private String overview;
	private Boolean individual;
	private String companyWebsite;
	@Size(max = 255)
	private String companyName;
	@Size(max = 1000)
	private String companyOverview;
	private Integer companyYearFounded;
	private List<SimpleValueDTO> companyEmployees;
	private List<SimpleValueDTO> yearsOfExperience;
	private List<OnboardingIndustryDTO> industries;
	private List<PhoneInfoDTO> phones;
	private ImageDTO avatar;
	private ImageDTO logo;
	private Boolean videoWatched;
	private Boolean isLastStep;
	private List<OnboardingSkillDTO> skills;

	public WorkerOnboardingDTO() {}

	public Boolean isVideoWatched() {
		return videoWatched;
	}

	public void setVideoWatched(Boolean videoWatched) {
		this.videoWatched = videoWatched;
	}

	public List<PhoneInfoDTO> getPhones() {
		return phones;
	}

	public void setPhones(List<PhoneInfoDTO> phones) {
		this.phones = phones;
	}

	public ImageDTO getAvatar() {
		return avatar;
	}

	public ImageDTO getLogo() {
		return logo;
	}

	public void setLogo(ImageDTO logo) {
		this.logo = logo;
	}

	public void setAvatar(ImageDTO avatar) {
		this.avatar = avatar;
	}

	public List<OnboardingIndustryDTO> getIndustries() {
		return industries;
	}

	public void setIndustries(List<OnboardingIndustryDTO> industryDTOs) {
		this.industries = industryDTOs;
	}

	public List<SimpleValueDTO> getYearsOfExperience() {
		return yearsOfExperience;
	}

	public void setYearsOfExperience(List<SimpleValueDTO> yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	public WorkerOnboardingDTO setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
		return this;
	}

	public ApiJobTitleDTO getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(final ApiJobTitleDTO jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStateShortName() {
		return stateShortName;
	}

	public void setStateShortName(String stateShortName) {
		this.stateShortName = stateShortName;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Integer getMaxTravelDistance() {
		return maxTravelDistance;
	}

	public void setMaxTravelDistance(Integer maxTravelDistance) {
		this.maxTravelDistance = maxTravelDistance;
	}

	public String getCountryIso() {
		return countryIso;
	}

	public void setCountryIso(String countryIso) {
		this.countryIso = countryIso;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public Boolean isIndividual() {
		return individual;
	}

	public void setIndividual(Boolean individual) {
		this.individual = individual;
	}

	public String getCompanyWebsite() {
		return companyWebsite;
	}

	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyOverview() {
		return companyOverview;
	}

	public void setCompanyOverview(String companyOverview) {
		this.companyOverview = companyOverview;
	}

	public Integer getCompanyYearFounded() {
		return companyYearFounded;
	}

	public void setCompanyYearFounded(Integer companyYearFounded) {
		this.companyYearFounded = companyYearFounded;
	}

	public List<SimpleValueDTO> getCompanyEmployees() {
		return companyEmployees;
	}

	public void setCompanyEmployees(List<SimpleValueDTO> companyEmployees) {
		this.companyEmployees = companyEmployees;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean hasAvatar() {
		return this.avatar != null && this.avatar.hasImage();
	}

	public boolean hasCompanyLogo() {
		return this.logo != null && this.logo.hasImage();
	}

	public Boolean getIsLastStep() {
		return isLastStep != null;
	}

	public void setIsLastStep(Boolean isLastStep) {
		this.isLastStep = isLastStep;
	}

	public List<OnboardingSkillDTO> getSkills() { return skills; }

	public void setSkills(List<OnboardingSkillDTO> skills) { this.skills = skills; }

}
