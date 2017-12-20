package com.workmarket.service.business.requirementsets;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Point;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.groups.service.association.UserGroupAssociationService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.analytics.ResourceScoreField;
import com.workmarket.domains.model.analytics.ScoreCard;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.certification.CertificationVendor;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.company.CompanyType;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.domains.model.contract.ContractVersionUserSignature;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.EligibilityUser;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.model.requirementset.abandon.AbandonRequirement;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirable;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirement;
import com.workmarket.domains.model.requirementset.availability.AvailabilityRequirement;
import com.workmarket.domains.model.requirementset.availability.WeekdayRequirable;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirement;
import com.workmarket.domains.model.requirementset.cancelled.CancelledRequirement;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirable;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirement;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirable;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirement;
import com.workmarket.domains.model.requirementset.companywork.CompanyWorkRequirable;
import com.workmarket.domains.model.requirementset.companywork.CompanyWorkRequirement;
import com.workmarket.domains.model.requirementset.country.CountryRequirable;
import com.workmarket.domains.model.requirementset.country.CountryRequirement;
import com.workmarket.domains.model.requirementset.deliverableontime.DeliverableOnTimeRequirement;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirement;
import com.workmarket.domains.model.requirementset.groupmembership.GroupMembershipRequirable;
import com.workmarket.domains.model.requirementset.groupmembership.GroupMembershipRequirement;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirable;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirement;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirable;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirement;
import com.workmarket.domains.model.requirementset.license.LicenseRequirable;
import com.workmarket.domains.model.requirementset.license.LicenseRequirement;
import com.workmarket.domains.model.requirementset.ontime.OntimeRequirement;
import com.workmarket.domains.model.requirementset.paid.PaidRequirement;
import com.workmarket.domains.model.requirementset.profilepicture.ProfilePictureRequirement;
import com.workmarket.domains.model.requirementset.profilevideo.ProfileVideoRequirement;
import com.workmarket.domains.model.requirementset.rating.RatingRequirement;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceType;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirable;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirement;
import com.workmarket.domains.model.requirementset.test.TestRequirable;
import com.workmarket.domains.model.requirementset.test.TestRequirement;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.model.screening.ScreeningObjectConverter;
import com.workmarket.domains.model.screening.ScreeningStatusType;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.domains.work.model.Work;
import com.workmarket.screening.model.Screening;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CertificationService;
import com.workmarket.service.business.ContractService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.InsuranceService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.LicenseService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.infra.business.GeocodingService;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EligibilityVisitorImplTest {

	private static final Long REQUIRABLE_ID = 1111111L;
	private static final Long MISSING_REQUIRABLE_ID = 9999999L;
	private static final Long USER_ID = 2222222L;
	private static final Long PROFILE_ID = 3333333L;
	private static final String REQUIRABLE_NAME = "SOME REQUIRABLE NAME";
	private static final String REQUIREMENT_TYPE_NAME = "SOME REQUIREMENT TYPE";
	private static final Long VERSION_ID = 3333333L;
	private static final Integer DAY_OF_WEEK = 5;
	private static final String FROM_TIME = "9:30am";
	private static final String SOME_OTHER_FROM_TIME = "12:30pm";
	private static final String TO_TIME = "10:30am";
	private static final String SOME_OTHER_TO_TIME = "3:30pm";
	private static final String TIMEZONE_ID = "America/New_York";
	private static final Calendar AVAILABLE_FROM = DateUtilities.getCalendarFromTimeString(FROM_TIME, TIMEZONE_ID);
	private static final Calendar AVAILABLE_TO = DateUtilities.getCalendarFromTimeString(TO_TIME, TIMEZONE_ID);
	private static final String COUNTRY_ID = "US";
	private static final String SOME_OTHER_COUNTRY_ID = "CA";
	private static final List<Long> INDUSTRY_IDS = Lists.newArrayList(1111111L, 4444444L, 5555555L);
	private static final List<Long> EMPTY_IDS = Lists.newArrayList();
	private static final Integer REQUIRED_RATING = 4;
	private static final Double GOOD_RATING = 5.0;
	private static final String RATING = String.valueOf(REQUIRED_RATING.doubleValue());
	private static final String RATING_NAME = RATING + "%";
	private static final Long COMPANY_ID = 6666666L;
	private static final Double REQUIRED_LAT = 99.000001;
	private static final Double REQUIRED_LONG = 99.999999;
	private static final String REQUIREMENT_ADDRESS = "4 Abbot Rd, Smithtown, NY 11787";
	private static final Long REQUIRED_DISTANCE = 50L;
	private static final Double CLOSE_ENOUGH_DISTANCE = 40.0;
	private static final Double TOO_FAR_DISTANCE = 60.0;
	private static final Double ONTIME_RATING_MIN_VALUE = 0.5d;
	private static final Double ONTIME_RESOURCE_CARD_NET90 = 0.5;
	private static final Double DELIVERABLE_ONTIME_RATING_MIN_VALUE = 0.5d;
	private static final Double DELIVERABLE_ONTIME_RESOURCE_CARD_NET90 = 0.5;
	private static final int ABANDON_REQUIREMENT_MAX = 10;
	private static final Double ABANDON_RESOURCE_CARD_NET90 = 10.0;
	private static final int CANCELLED_REQUIREMENT_MAX = 10;
	private static final Double CANCELLED_RESOURCE_CARD_NET90 = 10.0;
	private static final Double COMPLETED_RESOURCE_CARD_NET90 = 10.0;
	private static final Long GROUP_ID = 99999L;
	private static final List<Long> GROUP_IDS = Lists.newArrayList(GROUP_ID);
	private static final int PAID_ASSIGNMENT_COUNT = 5;
	private static final String COMPANY_WORK_NAME = "Company Work Name";
	private static final int COMPANY_PAID_COUNT = 2;
	private static final String VENDOR_NAME = "vendor name";


	@Mock AssessmentService assessmentService;
	@Mock CertificationService certificationService;
	@Mock ContractService contractService;
	@Mock InsuranceService insuranceService;
	@Mock GeocodingService geocodingService;
	@Mock UserService userService;
	@Mock LaneService laneService;
	@Mock LicenseService licenseService;
	@Mock ProfileService profileService;
	@Mock RatingService ratingService;
	@Mock ScreeningService screeningService;
	@Mock AddressService addressService;
	@Mock AnalyticsService analyticsService;
	@Mock IndustryService industryService;
	@Mock ExtendedUserDetailsService extendedUserDetailsService;
	@Mock UserGroupAssociationService userGroupAssociationService;
	@InjectMocks EligibilityVisitorImpl visitor = spy(new EligibilityVisitorImpl());

	EligibilityUser user;
	User baseUser;
	ExtendedUserDetails userDetails;
	Criterion criterion;
	AgreementRequirable agreementRequirable;
	AgreementRequirement agreementRequirement;
	ContractVersion version;
	ContractVersionUserSignature signature;

	WeekdayRequirable weekdayRequirable;
	AvailabilityRequirement availabilityRequirement;
	UserAvailability availability;
	TimeZone timezone;
	Work work;

	BackgroundCheckRequirement backgroundCheckRequirement;
	ScreeningStatusType screeningStatusType;
	BackgroundCheck backgroundCheck;
	Screening backgroundScreening;

	CertificationRequirable certificationRequirable;
	CertificationRequirement certificationRequirement;
	UserCertificationAssociation certificateAssociation;

	CompanyTypeRequirable companyTypeRequirable;
	CompanyTypeRequirement companyTypeRequirement;
	Company company;

	CountryRequirable countryRequirable;
	CountryRequirement countryRequirement;
	Country country;
	PostalCode postalCode;
	Optional<PostalCode> optional;

	DrugTestRequirement drugTestRequirement;
	DrugTest drugTest;
	Screening drugTestScreening;

	Profile profile;

	IndustryRequirable industryRequirable;
	IndustryRequirement industryRequirement;

	InsuranceRequirable insuranceRequirable;
	InsuranceRequirement insuranceRequirement;
	UserInsuranceAssociation insuranceAssociation;

	LicenseRequirable licenseRequirable;
	LicenseRequirement licenseRequirement;
	UserLicenseAssociation licenseAssociation;

	RatingRequirement ratingRequirement;

	ResourceTypeRequirable resourceTypeRequirable;
	RequirementSet requirementSet;
	ResourceTypeRequirement resourceTypeRequirement;
	LaneAssociation lane;

	TestRequirable testRequirable;
	TestRequirement testRequirement;
	AssessmentUserAssociation test;

	Coordinate userCoordinates;
	Coordinate addressCoordinates;
	Point latePoint;
	Coordinate lateCoordinates;
	TravelDistanceRequirement travelDistanceRequirement;

	ScoreCard resourceScoreCard;

	OntimeRequirement ontimeRequirement;
	ScoreCard.DateIntervalData ontimeDateIntervalData;

	DeliverableOnTimeRequirement deliverableOnTimeRequirement;
	ScoreCard.DateIntervalData deliverableOnTimeDateIntervalData;

	AbandonRequirement abandonRequirement;
	ScoreCard.DateIntervalData abandonDateIntervalData;

	CancelledRequirement cancelledRequirement;
	ScoreCard.DateIntervalData cancelledDateIntervalData;

	ProfilePictureRequirement profilePictureRequirement;

	GroupMembershipRequirement groupMembershipRequirement;
	GroupMembershipRequirable groupMembershipRequirable;

	PaidRequirement paidRequirement;
	ScoreCard.DateIntervalData completedDateIntervalData;

	CompanyWorkRequirement companyWorkRequirement;
	CompanyWorkRequirable companyWorkRequirable;

	ProfileVideoRequirement profileVideoRequirement;

	@Before
	public void setUp() throws SearchException {
		company = mock(Company.class);
			when(company.getId()).thenReturn(COMPANY_ID);
			when(company.getOperatingAsIndividualFlag()).thenReturn(true);

		profile = mock(Profile.class);
		when(profile.getId()).thenReturn(PROFILE_ID);
		when(profileService.findProfile(USER_ID)).thenReturn(profile);
		when(industryService.getIndustryIdsForProfile(PROFILE_ID)).thenReturn(INDUSTRY_IDS);

		userCoordinates = mock(Coordinate.class);

		user = mock(EligibilityUser.class);
			when(user.getId()).thenReturn(USER_ID);
			when(user.getCompany()).thenReturn(company);

		baseUser = mock(User.class);
			when(baseUser.getId()).thenReturn(USER_ID);
			when(baseUser.getCompany()).thenReturn(company);
			when(baseUser.getProfile()).thenReturn(profile);

		timezone = mock(TimeZone.class);
			when(timezone.getTimeZoneId()).thenReturn(TIMEZONE_ID);

		work = mock(Work.class);
			when(work.getRequirementSetableTimeZone()).thenReturn(timezone);

		criterion = mock(Criterion.class);
			when(criterion.getUser()).thenReturn((user));
			when(criterion.getRequirementSetable()).thenReturn(work);

		agreementRequirable = mock(AgreementRequirable.class);
			when(agreementRequirable.getId()).thenReturn(REQUIRABLE_ID);
			when(agreementRequirable.getName()).thenReturn(REQUIRABLE_NAME);

		agreementRequirement = mock(AgreementRequirement.class);
			when(agreementRequirement.getAgreementRequirable()).thenReturn(agreementRequirable);
			when(agreementRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(agreementRequirement.isMandatory()).thenReturn(true);

		version = mock(ContractVersion.class);
		when(version.getId()).thenReturn(VERSION_ID);

		signature = mock(ContractVersionUserSignature.class);

		when(contractService.findMostRecentContractVersionByContractId(REQUIRABLE_ID)).thenReturn(version);
		when(contractService.findMostRecentContractVersionIdByContractId(REQUIRABLE_ID)).thenReturn(Optional.of(VERSION_ID));
		when(contractService.findContractVersionUserSignatureByContractVersionIdAndUserId(VERSION_ID, USER_ID)).thenReturn(signature);

		weekdayRequirable = mock(WeekdayRequirable.class);
			when(weekdayRequirable.getName()).thenReturn(REQUIRABLE_NAME);

		availabilityRequirement = mock(AvailabilityRequirement.class);
			when(availabilityRequirement.getWeekdayRequirable()).thenReturn(weekdayRequirable);
			when(availabilityRequirement.getDayOfWeek()).thenReturn(DAY_OF_WEEK);
			when(availabilityRequirement.getFromTime()).thenReturn(FROM_TIME);
			when(availabilityRequirement.getToTime()).thenReturn(TO_TIME);
			when(availabilityRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(availabilityRequirement.isMandatory()).thenReturn(true);

		availability = mock(UserAvailability.class);
			when(availability.getFromTime()).thenReturn(AVAILABLE_FROM);
			when(availability.getToTime()).thenReturn(AVAILABLE_TO);

		when(userService.findActiveWorkingHoursByUserId(USER_ID, DAY_OF_WEEK)).thenReturn(availability);
		when(userService.getUser(USER_ID)).thenReturn(baseUser);

		when(addressService.getCoordinatesForUser(USER_ID)).thenReturn(userCoordinates);

		backgroundCheckRequirement = mock(BackgroundCheckRequirement.class);
			when(backgroundCheckRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(backgroundCheckRequirement.isMandatory()).thenReturn(true);

		screeningStatusType = mock(ScreeningStatusType.class);
			when(screeningStatusType.getCode()).thenReturn(ScreeningStatusType.PASSED);

		backgroundCheck = mock(BackgroundCheck.class);
			when(backgroundCheck.getScreeningStatusType()).thenReturn(screeningStatusType);

		backgroundScreening = ScreeningObjectConverter.convertScreeningFromLegacy(backgroundCheck);
		when(screeningService.findMostRecentBackgroundCheck(user.getId())).thenReturn(backgroundScreening);

		CertificationVendor certificationVendor = mock(CertificationVendor.class);
		when(certificationVendor.getName()).thenReturn(VENDOR_NAME);

		certificationRequirable = mock(CertificationRequirable.class);
			when(certificationRequirable.getId()).thenReturn(REQUIRABLE_ID);
			when(certificationRequirable.getName()).thenReturn(REQUIRABLE_NAME);
			when(certificationRequirable.getCertificationVendor()).thenReturn(certificationVendor);

		certificationRequirement = mock(CertificationRequirement.class);
			when(certificationRequirement.getCertificationRequirable()).thenReturn(certificationRequirable);
			when(certificationRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(certificationRequirement.isMandatory()).thenReturn(true);
			when(certificationRequirement.getId()).thenReturn(REQUIRABLE_ID);

		certificateAssociation = mock(UserCertificationAssociation.class);
		when(certificationService.findActiveVerifiedAssociationByCertificationIdAndUserId(REQUIRABLE_ID, USER_ID)).thenReturn(certificateAssociation);

		companyTypeRequirable = mock(CompanyTypeRequirable.class);
			when(companyTypeRequirable.getId()).thenReturn(CompanyType.SOLE_PROPRIETOR.getId());
			when(companyTypeRequirable.getName()).thenReturn(REQUIRABLE_NAME);

		companyTypeRequirement = mock(CompanyTypeRequirement.class);
			when(companyTypeRequirement.getCompanyTypeRequirable()).thenReturn(companyTypeRequirable);
			when(companyTypeRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(companyTypeRequirement.isMandatory()).thenReturn(true);

		countryRequirable = mock(CountryRequirable.class);
			when(countryRequirable.getId()).thenReturn(COUNTRY_ID);
			when(countryRequirable.getName()).thenReturn(REQUIRABLE_NAME);

		countryRequirement = mock(CountryRequirement.class);
			when(countryRequirement.getCountryRequirable()).thenReturn(countryRequirable);
			when(countryRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(countryRequirement.isMandatory()).thenReturn(true);

		country = mock(Country.class);
		when(country.getId()).thenReturn(COUNTRY_ID);

		postalCode = mock(PostalCode.class);
		when(postalCode.getCountry()).thenReturn(country);

		optional = mock(Optional.class);
		when(optional.isPresent()).thenReturn(Boolean.TRUE);
		when(optional.get()).thenReturn(postalCode);

		when(profileService.findPostalCodeForUser(USER_ID)).thenReturn(optional);

		drugTestRequirement = mock(DrugTestRequirement.class);
			when(drugTestRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(drugTestRequirement.isMandatory()).thenReturn(true);

		drugTest = mock(DrugTest.class);
		when(drugTest.getScreeningStatusType()).thenReturn(screeningStatusType);

		drugTestScreening = ScreeningObjectConverter.convertScreeningFromLegacy(drugTest);
		when(screeningService.findMostRecentDrugTest(user.getId())).thenReturn(drugTestScreening);

		industryRequirable = mock(IndustryRequirable.class);
			when(industryRequirable.getId()).thenReturn(REQUIRABLE_ID);
			when(industryRequirable.getName()).thenReturn(REQUIRABLE_NAME);

		industryRequirement = mock(IndustryRequirement.class);
			when(industryRequirement.getIndustryRequirable()).thenReturn(industryRequirable);
			when(industryRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(industryRequirement.isMandatory()).thenReturn(true);

		insuranceRequirable = mock(InsuranceRequirable.class);
			when(insuranceRequirable.getId()).thenReturn(REQUIRABLE_ID);
			when(insuranceRequirable.getName()).thenReturn(REQUIRABLE_NAME);

		insuranceRequirement = mock(InsuranceRequirement.class);
			when(insuranceRequirement.getInsuranceRequirable()).thenReturn(insuranceRequirable);
			when(insuranceRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(insuranceRequirement.getMinimumCoverageAmount()).thenReturn(new BigDecimal("0"));
			when(insuranceRequirement.isMandatory()).thenReturn(true);

		insuranceAssociation = mock(UserInsuranceAssociation.class);

		when(insuranceService.findActiveVerifiedAssociationByInsuranceIdAndUserId(REQUIRABLE_ID, USER_ID)).thenReturn(insuranceAssociation);

		licenseRequirable = mock(LicenseRequirable.class);
			when(licenseRequirable.getId()).thenReturn(REQUIRABLE_ID);
			when(licenseRequirable.getName()).thenReturn(REQUIRABLE_NAME);

		licenseRequirement = mock(LicenseRequirement.class);
			when(licenseRequirement.getLicenseRequirable()).thenReturn(licenseRequirable);
			when(licenseRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(licenseRequirement.isMandatory()).thenReturn(true);

		licenseAssociation = mock(UserLicenseAssociation.class);
		when(licenseService.findActiveVerifiedAssociationByLicenseIdAndUserId(REQUIRABLE_ID, USER_ID)).thenReturn(licenseAssociation);

		ratingRequirement = mock(RatingRequirement.class);
			when(ratingRequirement.getValue()).thenReturn(REQUIRED_RATING);
			when(ratingRequirement.getName()).thenReturn(RATING);
			when(ratingRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(ratingRequirement.isMandatory()).thenReturn(true);

		when(ratingService.findSatisfactionRateForUser(USER_ID)).thenReturn(GOOD_RATING);

		resourceTypeRequirable = mock(ResourceTypeRequirable.class);
			when(resourceTypeRequirable.getId()).thenReturn(ResourceType.EMPLOYEE.getId());
			when(resourceTypeRequirable.getName()).thenReturn(REQUIRABLE_NAME);

		requirementSet = mock(RequirementSet.class);
			when(requirementSet.getCompany()).thenReturn(company);

		resourceTypeRequirement = mock(ResourceTypeRequirement.class);
			when(resourceTypeRequirement.getResourceTypeRequirable()).thenReturn(resourceTypeRequirable);
			when(resourceTypeRequirement.getRequirementSet()).thenReturn(requirementSet);
			when(resourceTypeRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(resourceTypeRequirement.isMandatory()).thenReturn(true);

		lane = mock(LaneAssociation.class);
		when(lane.getLaneType()).thenReturn(LaneType.LANE_1);
		when(laneService.findActiveAssociationByUserIdAndCompanyId(USER_ID, COMPANY_ID)).thenReturn(lane);

		testRequirable = mock(TestRequirable.class);
			when(testRequirable.getId()).thenReturn(REQUIRABLE_ID);
			when(testRequirable.getName()).thenReturn(REQUIRABLE_NAME);

		testRequirement = mock(TestRequirement.class);
			when(testRequirement.getTestRequirable()).thenReturn(testRequirable);
			when(testRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(testRequirement.isMandatory()).thenReturn(true);

		test = mock(AssessmentUserAssociation.class);
		when(test.getPassedFlag()).thenReturn(Boolean.TRUE);
		when(assessmentService.findAssessmentUserAssociationByUserAndAssessment(USER_ID, REQUIRABLE_ID)).thenReturn(test);

		addressCoordinates = mock(Coordinate.class);
			when(addressCoordinates.getLatitude()).thenReturn(REQUIRED_LAT);
			when(addressCoordinates.getLongitude()).thenReturn(REQUIRED_LONG);
			when(geocodingService.calculateDistance(userCoordinates, addressCoordinates)).thenReturn(CLOSE_ENOUGH_DISTANCE);
			when(geocodingService.geocode(REQUIREMENT_ADDRESS)).thenReturn(latePoint);

		profileVideoRequirement = mock(ProfileVideoRequirement.class);
			when(profileVideoRequirement.isMandatory()).thenReturn(true);
			when(profileVideoRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);

		travelDistanceRequirement = mock(TravelDistanceRequirement.class);
			when(travelDistanceRequirement.getCoordinates()).thenReturn(addressCoordinates);
			when(travelDistanceRequirement.getDistance()).thenReturn(REQUIRED_DISTANCE);
			when(travelDistanceRequirement.getAddress()).thenReturn(REQUIREMENT_ADDRESS);
			when(travelDistanceRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(travelDistanceRequirement.isMandatory()).thenReturn(true);

		latePoint = mock(Point.class);
		lateCoordinates = mock(Coordinate.class);
		doReturn(lateCoordinates).when(visitor).makeCoordinate(latePoint);

		when(geocodingService.calculateDistance(userCoordinates, addressCoordinates)).thenReturn(CLOSE_ENOUGH_DISTANCE);
		when(geocodingService.geocode(REQUIREMENT_ADDRESS)).thenReturn(latePoint);

		userDetails = mock(ExtendedUserDetails.class);
			when(userDetails.isMbo()).thenReturn(true);
			when(extendedUserDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

		ontimeRequirement = mock(OntimeRequirement.class);
			when(ontimeRequirement.getMinimumPercentage()).thenReturn(((Double)(ONTIME_RATING_MIN_VALUE * 100)).intValue());
			when(ontimeRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(ontimeRequirement.getName()).thenReturn(OntimeRequirement.DEFAULT_NAME);
			when(ontimeRequirement.isMandatory()).thenReturn(true);

		ontimeDateIntervalData = mock(ScoreCard.DateIntervalData.class);
			when(ontimeDateIntervalData.getNet90()).thenReturn(ONTIME_RESOURCE_CARD_NET90);

		deliverableOnTimeRequirement = mock(DeliverableOnTimeRequirement.class);
		when(deliverableOnTimeRequirement.getMinimumPercentage()).thenReturn(((Double)(DELIVERABLE_ONTIME_RATING_MIN_VALUE * 100)).intValue());
		when(deliverableOnTimeRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
		when(deliverableOnTimeRequirement.getName()).thenReturn(DeliverableOnTimeRequirement.DEFAULT_NAME);
		when(deliverableOnTimeRequirement.isMandatory()).thenReturn(true);

		deliverableOnTimeDateIntervalData = mock(ScoreCard.DateIntervalData.class);
		when(deliverableOnTimeDateIntervalData.getNet90()).thenReturn(DELIVERABLE_ONTIME_RESOURCE_CARD_NET90);

		abandonRequirement = mock(AbandonRequirement.class);
			when(abandonRequirement.getMaximumAllowed()).thenReturn(ABANDON_REQUIREMENT_MAX);
			when(abandonRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(abandonRequirement.getName()).thenReturn(AbandonRequirement.DEFAULT_NAME);
			when(abandonRequirement.isMandatory()).thenReturn(true);

		abandonDateIntervalData = mock(ScoreCard.DateIntervalData.class);
		when(abandonDateIntervalData.getNet90()).thenReturn(ABANDON_RESOURCE_CARD_NET90);

		cancelledRequirement = mock(CancelledRequirement.class);
			when(cancelledRequirement.getMaximumAllowed()).thenReturn(CANCELLED_REQUIREMENT_MAX);
			when(cancelledRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(cancelledRequirement.getName()).thenReturn(CancelledRequirement.DEFAULT_NAME);
			when(cancelledRequirement.isMandatory()).thenReturn(true);

		cancelledDateIntervalData = mock(ScoreCard.DateIntervalData.class);
		when(cancelledDateIntervalData.getNet90()).thenReturn(CANCELLED_RESOURCE_CARD_NET90);

		completedDateIntervalData = mock(ScoreCard.DateIntervalData.class);
		when(completedDateIntervalData.getNet90()).thenReturn(COMPLETED_RESOURCE_CARD_NET90);

		resourceScoreCard = mock(ScoreCard.class);
		when(resourceScoreCard.getValues()).thenReturn(new HashMap<Enum, ScoreCard.DateIntervalData>() {{
			put(ResourceScoreField.ON_TIME_PERCENTAGE, ontimeDateIntervalData);
			put(ResourceScoreField.DELIVERABLE_ON_TIME_PERCENTAGE, deliverableOnTimeDateIntervalData);
			put(ResourceScoreField.ABANDONED_WORK, abandonDateIntervalData);
			put(ResourceScoreField.CANCELLED_WORK, cancelledDateIntervalData);
			put(ResourceScoreField.COMPLETED_WORK, completedDateIntervalData);
		}});

		when(analyticsService.getResourceScoreCard(any(Long.class))).thenReturn(resourceScoreCard);

		profilePictureRequirement = mock(ProfilePictureRequirement.class);
			when(profilePictureRequirement.isMandatory()).thenReturn(true);
			when(profilePictureRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);

		when(screeningService.hasProfilePicture(any(Long.class))).thenReturn(true);

		groupMembershipRequirable = mock(GroupMembershipRequirable.class);
			when(groupMembershipRequirable.getId()).thenReturn(GROUP_ID);

		groupMembershipRequirement = mock(GroupMembershipRequirement.class);
			when(groupMembershipRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);
			when(groupMembershipRequirement.getGroupMembershipRequirable()).thenReturn(groupMembershipRequirable);
			when(groupMembershipRequirement.isMandatory()).thenReturn(true);

		when(userGroupAssociationService.findAllUserGroupAssociationsByUserId(USER_ID)).thenReturn(GROUP_IDS);

		paidRequirement = mock(PaidRequirement.class);
			when(paidRequirement.getMinimumAssignments()).thenReturn(PAID_ASSIGNMENT_COUNT);
			when(paidRequirement.isMandatory()).thenReturn(true);
			when(paidRequirement.getHumanTypeName()).thenReturn(REQUIREMENT_TYPE_NAME);

		companyWorkRequirable = mock(CompanyWorkRequirable.class);
			when(companyWorkRequirable.getId()).thenReturn(COMPANY_ID);

		companyWorkRequirement = mock(CompanyWorkRequirement.class);
			when(companyWorkRequirement.isMandatory()).thenReturn(true);
			when(companyWorkRequirement.getHumanTypeName()).thenReturn(COMPANY_WORK_NAME);
			when(companyWorkRequirement.getCompanyWorkRequirable()).thenReturn(companyWorkRequirable);
			when(companyWorkRequirement.getMinimumWorkCount()).thenReturn(COMPANY_PAID_COUNT);
	}

	@Test
	public void visit_withAgreementRequirementWithAgreement_setCriterionMet() {
		visitor.visit(criterion, agreementRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withAgreementRequirementWithoutAgreement_setCriterionNotMet() {
		when(agreementRequirable.getId()).thenReturn(MISSING_REQUIRABLE_ID);
		when(contractService.findMostRecentContractVersionIdByContractId(MISSING_REQUIRABLE_ID)).thenReturn(Optional.<Long>absent());
		visitor.visit(criterion, agreementRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withAgreementRequirementEmpty_setCriterionNotMet() {
		when(contractService.findContractVersionUserSignatureByContractVersionIdAndUserId(VERSION_ID, USER_ID)).thenReturn(null);
		visitor.visit(criterion, agreementRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withAgreementRequirementEmptyAndNotMandatory_setCriterionMet() {
		when(agreementRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, agreementRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withAgreementRequirement_setsTheCriterionRequirable() {
		visitor.visit(criterion, agreementRequirement);
		verify(criterion).setRequirable(agreementRequirable);
	}

	@Test
	public void visit_withAgreementRequirement_setsTheCriterionName() {
		visitor.visit(criterion, agreementRequirement);
		verify(criterion).setName(REQUIRABLE_NAME);
	}

	@Test
	public void visit_withAgreementRequirement_setsTheCriterionTypeName() {
		visitor.visit(criterion, agreementRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withAgreementRequirement_setsTheCriterionURL() {
		visitor.visit(criterion, agreementRequirement);
		verify(criterion).setUrl("/agreements/" + REQUIRABLE_ID);
	}

	@Test
	public void visit_withCompanyWorkRequirementAndEqualPaidCount_setsCriterionMet() {
		visitor.visit(criterion, companyWorkRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCompanyWorkRequirementAndFewerPaid_setsCriterionNotMet() {
		when(completedDateIntervalData.getNet90()).thenReturn(COMPLETED_RESOURCE_CARD_NET90 - 10);
		visitor.visit(criterion, companyWorkRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withCompanyWorkRequirementAndFewerPaidAndNotMandadtory_setsCriterionMet() {
		when(companyWorkRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, companyWorkRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCompanyWorkRequirementAndGreaterPaid_setsCriterionNotMet() {
		visitor.visit(criterion, companyWorkRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCompanyWorkRequirementAndEmpty_setsCriterionNotMet() {
		when(completedDateIntervalData.getNet90()).thenReturn(0D);
		visitor.visit(criterion, companyWorkRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withCompanyWorkRequirementAndEmptyAndZeroPaid_setsCriterionMet() {
		when(companyWorkRequirement.getMinimumWorkCount()).thenReturn(0);
		visitor.visit(criterion, companyWorkRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCertificationRequirementWithCertification_setCriterionMet() {
		visitor.visit(criterion, certificationRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCertificationRequirementWithoutCertification_setCriterionNotMet() {
		when(certificationRequirable.getId()).thenReturn(MISSING_REQUIRABLE_ID);
		visitor.visit(criterion, certificationRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withCertificationRequirementWithoutCertificationAndNotMandatory_setCriterionMet() {
		when(certificationRequirable.getId()).thenReturn(MISSING_REQUIRABLE_ID);
		when(certificationRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, certificationRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCertificationRequirementWithEmpty_setCriterionNotMet() {
		when(certificationService.findActiveVerifiedAssociationByCertificationIdAndUserId(REQUIRABLE_ID, USER_ID)).thenReturn(null);
		visitor.visit(criterion, certificationRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withCertificationRequirementAndNoAssociatedCertFound_setsCriterionName() {
		visitor.visit(criterion, certificationRequirement);
		verify(criterion).setName(REQUIRABLE_NAME);
	}

	@Test
	public void visit_withCertificationRequirementAndNoAssociatedCertFound_setsCriterionTypeName() {
		visitor.visit(criterion, certificationRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
		verify(criterion).setUrl("/profile-edit/certifications");
	}

	@Test
	public void visit_withCertificationRequirementAndNoAssociatedCertFound_setsCriterionURL() {
		visitor.visit(criterion, certificationRequirement);
		verify(criterion).setUrl("/profile-edit/certifications");
	}

	@Test
	public void visit_withCertificationRequirement_setsTheCriterionMet() {
		visitor.visit(criterion, certificationRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withPaidRequirementGreaterPaid_setsCriterionMet() {
		visitor.visit(criterion, paidRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withPaidRequirementEqualPaid_setsCriterionMet() {
		visitor.visit(criterion, paidRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withPaidRequirementLessPaid_setsCriterionNotMet() {
		when(completedDateIntervalData.getNet90()).thenReturn(COMPLETED_RESOURCE_CARD_NET90 - 10);
		visitor.visit(criterion, paidRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withPaidRequirementLessPaidAndNotMandatory_setsCriterionMet() {
		when(paidRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, paidRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withGroupMembershipRequirementContains_setsCriterionMet() {
		visitor.visit(criterion, groupMembershipRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withGroupMembershipRequirementWithout_setsCriterionNotMet() {
		when(groupMembershipRequirable.getId()).thenReturn(GROUP_ID + 1);
		visitor.visit(criterion, groupMembershipRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withGroupMembershipRequirementEmpty_setsCriterionNotMet() {
		when(userGroupAssociationService.findAllUserGroupAssociationsByUserId(USER_ID)).thenReturn(EMPTY_IDS);
		visitor.visit(criterion, groupMembershipRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withGroupMembershipRequirementNotMandatory_setsCriterionMet() {
		when(groupMembershipRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, groupMembershipRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withAvailabilityRequirement_findsActiveWorkingHoursForUser() {
		visitor.visit(criterion, availabilityRequirement);
		verify(userService).findActiveWorkingHoursByUserId(USER_ID, DAY_OF_WEEK);
	}

	@Test
	public void visit_withAvailabilityRequirement_setTheCriterionRequirable() {
		visitor.visit(criterion, availabilityRequirement);
		verify(criterion).setRequirable(weekdayRequirable);
	}

	@Test
	public void visit_withAvailabilityRequirement_setTheCriterionMet() {
		visitor.visit(criterion, availabilityRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withAvailabilityRequirementNotOverlappingAndNotMandatory_setTheCriterionMet() {
		when(availabilityRequirement.isMandatory()).thenReturn(false);
		when(availabilityRequirement.getFromTime()).thenReturn(SOME_OTHER_FROM_TIME);
		when(availabilityRequirement.getToTime()).thenReturn(SOME_OTHER_TO_TIME);
		visitor.visit(criterion, availabilityRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withAvailabilityRequirementNotOverlapping_setTheCriterionNotMet() {
		when(availabilityRequirement.getFromTime()).thenReturn(SOME_OTHER_FROM_TIME);
		when(availabilityRequirement.getToTime()).thenReturn(SOME_OTHER_TO_TIME);
		visitor.visit(criterion, availabilityRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withAvailabilityRequirementButNoAvailability_setTheCriterionNotMet() {
		when(userService.findActiveWorkingHoursByUserId(USER_ID, DAY_OF_WEEK)).thenReturn(null);
		visitor.visit(criterion, availabilityRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withAvailabilityRequirement_setTheCriterionName() {
		visitor.visit(criterion, availabilityRequirement);
		verify(criterion).setName(
				String.format(
						AvailabilityRequirement.NAME_TEMPLATE,
						REQUIRABLE_NAME,
						FROM_TIME,
						TO_TIME));
	}

	@Test
	public void visit_withAvailabilityRequirement_setTheCriterionTypeName() {
		visitor.visit(criterion, availabilityRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withAvailabilityRequirement_setTheCriterionURL() {
		visitor.visit(criterion, availabilityRequirement);
		verify(criterion).setUrl("/mysettings/hours");
	}

	@Test
	public void visit_withBackgroundCheckRequirementWithBackground_setCriterionMet() {
		visitor.visit(criterion, backgroundCheckRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withBackgroundCheckRequirementWithoutBackground_setCriterionNotMet() {
		when(screeningService.findMostRecentBackgroundCheck(user.getId())).thenReturn(null);
		visitor.visit(criterion, backgroundCheckRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withBackgroundCheckRequirementWithoutBackgroundAndNotMandatory_setCriterionMet() {
		when(backgroundCheckRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, backgroundCheckRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withBackgroundCheckRequirement_setsTheCriterionRequirable() {
		visitor.visit(criterion, backgroundCheckRequirement);
		verify(criterion).setRequirable(null);
	}

	@Test
	public void visit_withBackgroundCheckRequirement_setsCriterionName() {
		visitor.visit(criterion, backgroundCheckRequirement);
		verify(criterion).setName(BackgroundCheckRequirement.DEFAULT_NAME);
	}

	@Test
	public void visit_withBackgroundCheckRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, backgroundCheckRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withBackgroundCheckRequirement_setsCriterionURL() {
		visitor.visit(criterion, backgroundCheckRequirement);
		verify(criterion).setUrl("/screening/bkgrnd");
	}

	@Test
	public void visit_withCountryRequirementWithCountry_setsCriterionMet() {
		visitor.visit(criterion, countryRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCountryRequirementWithoutCountry_setsCriterionNotMet() {
		when(country.getId()).thenReturn(SOME_OTHER_COUNTRY_ID);
		visitor.visit(criterion, countryRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withCountryRequirementWithoutCountryAndNotMandatory_setsCriterionMet() {
		when(countryRequirement.isMandatory()).thenReturn(false);
		when(country.getId()).thenReturn(SOME_OTHER_COUNTRY_ID);
		visitor.visit(criterion, countryRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCountryRequirement_setsCriterionName() {
		visitor.visit(criterion, countryRequirement);
		verify(criterion).setName(REQUIRABLE_NAME);
	}

	@Test
	public void visit_withCountryRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, countryRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withCountryRequirement_setsCriterionURL() {
		visitor.visit(criterion, countryRequirement);
		verify(criterion).setUrl("/profile-edit");
	}

	@Test
	public void visit_withDrugTestRequirementAndNoDrugCheck_setsCriterionNotMet() {
		when(screeningService.findMostRecentDrugTest(user.getId())).thenReturn(null);
		visitor.visit(criterion, drugTestRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withDrugTestRequirementWithTest_setCriterionMet() {
		visitor.visit(criterion, drugTestRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withDrugTestRequirementWithoutTestAndNotMandatory_setCriterionMet() {
		when(drugTestRequirement.isMandatory()).thenReturn(false);
		when(screeningService.findMostRecentDrugTest(user.getId())).thenReturn(null);
		visitor.visit(criterion, drugTestRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withDrugTestRequirement_setsTheCriterionRequirable() {
		visitor.visit(criterion, drugTestRequirement);
		verify(criterion).setRequirable(null);
	}

	@Test
	public void visit_withDrugTestRequirement_setsCriterionName() {
		visitor.visit(criterion, drugTestRequirement);
		verify(criterion).setName(DrugTestRequirement.DEFAULT_NAME);
	}

	@Test
	public void visit_withDrugTestRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, drugTestRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withDrugTestRequirement_setsCriterionURL() {
		visitor.visit(criterion, drugTestRequirement);
		verify(criterion).setUrl("/screening/drug");
	}

	@Test
	public void visit_withIndustryRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, industryRequirement);
		verify(criterion).setRequirable(industryRequirable);
	}

	@Test
	public void visit_withIndustryRequirementWithIndustry_setsCriterionMet() {
		visitor.visit(criterion, industryRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withIndustryRequirementWithoutIndustry_setsCriterionNotMet() {
		when(industryRequirable.getId()).thenReturn(MISSING_REQUIRABLE_ID);
		visitor.visit(criterion, industryRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withIndustryRequirementWithoutIndustryAndNotMandatory_setsCriterionMet() {
		when(industryRequirable.getId()).thenReturn(MISSING_REQUIRABLE_ID);
		when(industryRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, industryRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withIndustryRequirement_setsCriterionName() {
		visitor.visit(criterion, industryRequirement);
		verify(criterion).setName(REQUIRABLE_NAME);
	}

	@Test
	public void visit_withIndustryRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, industryRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withIndustryRequirement_setsCriterionURL() {
		visitor.visit(criterion, industryRequirement);
		verify(criterion).setUrl("/profile-edit/industries");
	}

	@Test
	public void visit_withInsuranceRequirementAndMatchingInsuranceIds_setsCriterionMet() {
		visitor.visit(criterion, insuranceRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withInsuranceRequirementAndNoMatchingInsuranceIds_setsCriterionNotMet() {
		when(insuranceRequirable.getId()).thenReturn(MISSING_REQUIRABLE_ID);
		visitor.visit(criterion, insuranceRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withInsuranceRequirementAndNoMatchingInsuranceIdsAndNotMandatory_setsCriterionMet() {
		when(insuranceRequirable.getId()).thenReturn(MISSING_REQUIRABLE_ID);
		when(insuranceRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, insuranceRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withInsuranceRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, insuranceRequirement);
		verify(criterion).setRequirable(insuranceRequirable);
	}

	@Test
	public void visit_withInsuranceRequirement_setsCriterionName() {
		visitor.visit(criterion, insuranceRequirement);
		verify(criterion).setName(REQUIRABLE_NAME);
	}

	@Test
	public void visit_withInsuranceRequirement_When_MinCoverageIsGeaterThanZero_setsCriterionName() {
		when(insuranceRequirement.getMinimumCoverageAmount()).thenReturn(new BigDecimal("47000000"));
		visitor.visit(criterion, insuranceRequirement);
		verify(criterion).setName(
			String.format(
				InsuranceRequirement.NAME_TEMPLATE, REQUIRABLE_NAME, "47,000,000"
			)
		);
	}

	@Test
	public void visit_withInsuranceRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, insuranceRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withInsuranceRequirement_setsCriterionURL() {
		visitor.visit(criterion, insuranceRequirement);
		verify(criterion).setUrl("/profile-edit/insurance");
	}

	@Test
	public void visit_withLicenseRequirementAndHasLicense_setsCriterionMet() {
		visitor.visit(criterion, licenseRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withLicenseRequirementAndNoLicense_setsCriterionNotMet() {
		when(licenseRequirable.getId()).thenReturn(MISSING_REQUIRABLE_ID);
		visitor.visit(criterion, licenseRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withLicenseRequirementAndNoLicenseAndNotMandatory_setsCriterionMet() {
		when(licenseRequirable.getId()).thenReturn(MISSING_REQUIRABLE_ID);
		when(licenseRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, licenseRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withLicenseRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, licenseRequirement);
		verify(criterion).setRequirable(licenseRequirable);
	}

	@Test
	public void visit_withLicenseRequirement_setsCriterionName() {
		visitor.visit(criterion, licenseRequirement);
		verify(criterion).setName(REQUIRABLE_NAME);
	}

	@Test
	public void visit_withLicenseRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, licenseRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withLicenseRequirement_setsCriterionURL() {
		visitor.visit(criterion, licenseRequirement);
		verify(criterion).setUrl("/profile-edit/licenses");
	}

	@Test
	public void visit_withRatingRequirementWithGreaterRating_setsCriterionMet() {
		Integer userRating = REQUIRED_RATING + 1;
		when(ratingService.findSatisfactionRateForUser(USER_ID)).thenReturn(userRating.doubleValue());
		visitor.visit(criterion, ratingRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withRatingRequirementWithEqualRating_setsCriterionMet() {
		visitor.visit(criterion, ratingRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withRatingRequirementWithLessRating_setsCriterionMet() {
		Integer userRating = REQUIRED_RATING - 1;
		when(ratingService.findSatisfactionRateForUser(USER_ID)).thenReturn(userRating.doubleValue());
		visitor.visit(criterion, ratingRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withRatingRequirementWithLessRatingAndMandatory_setsCriterionMet() {
		when(ratingRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, ratingRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withRatingRequirement_setsCriterionRequirableToNull() {
		visitor.visit(criterion, ratingRequirement);
		verify(criterion).setRequirable(null);
	}

	@Test
	public void visit_withRatingRequirement_setsCriterionName() {
		visitor.visit(criterion, ratingRequirement);
		verify(criterion).setName(RATING_NAME);
	}

	@Test
	public void visit_withRatingRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, ratingRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withRatingRequirement_setsCriterionURL() {
		visitor.visit(criterion, ratingRequirement);
		verify(criterion).setUrl(null);
	}

	@Test
	public void visit_withResourceTypeRequirementEmployeeAndLane1_setsCriterionMet() {
		visitor.visit(criterion, resourceTypeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withResourceTypeRequirementEmployeeAndLane2_setsCriterionNotMet() {
		when(lane.getLaneType()).thenReturn(LaneType.LANE_2);
		visitor.visit(criterion, resourceTypeRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withResourceTypeRequirementContractorAndLane2_setsCriterionMet() {
		when(resourceTypeRequirable.getId()).thenReturn(ResourceType.CONTRACTOR.getId());
		when(lane.getLaneType()).thenReturn(LaneType.LANE_2);
		visitor.visit(criterion, resourceTypeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withResourceTypeRequirementContractorAndLane3_setsCriterionMet() {
		when(resourceTypeRequirable.getId()).thenReturn(ResourceType.CONTRACTOR.getId());
		when(lane.getLaneType()).thenReturn(LaneType.LANE_3);
		visitor.visit(criterion, resourceTypeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withResourceTypeRequirementContractorAndLane1_setsCriterionNotMet() {
		when(resourceTypeRequirable.getId()).thenReturn(ResourceType.CONTRACTOR.getId());
		visitor.visit(criterion, resourceTypeRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withResourceTypeRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, resourceTypeRequirement);
		verify(criterion).setRequirable(resourceTypeRequirable);
	}

	@Test
	public void visit_withResourceTypeRequirement_setsCriterionName() {
		visitor.visit(criterion, resourceTypeRequirement);
		verify(criterion).setName(REQUIRABLE_NAME);
	}

	@Test
	public void visit_withResourceTypeRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, resourceTypeRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withResourceTypeRequirement_setsCriterionURL() {
		visitor.visit(criterion, resourceTypeRequirement);
		verify(criterion).setUrl(null);
	}

	@Test
	public void visit_withTestRequirementAndPassedAssessment_setsCriterionMet() {
		visitor.visit(criterion, testRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withTestRequirementAndNoPassedAssessment_setsCriterionMet() {
		when(assessmentService.findAssessmentUserAssociationByUserAndAssessment(USER_ID, REQUIRABLE_ID)).thenReturn(null);
		visitor.visit(criterion, testRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withTestRequirementAndNoPassedAssessmentAndNotMandatory_setsCriterionMet() {
		when(testRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, testRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withTestRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, testRequirement);
		verify(criterion).setRequirable(testRequirable);
	}

	@Test
	public void visit_withTestRequirement_setsCriterionName() {
		visitor.visit(criterion, testRequirement);
		verify(criterion).setName(REQUIRABLE_NAME);
	}

	@Test
	public void visit_withTestRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, testRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withTestRequirement_setsCriterionURL() {
		visitor.visit(criterion, testRequirement);
		verify(criterion).setUrl("/lms/view/take/" + REQUIRABLE_ID);
	}

	@Test
	public void visit_withTravelDistanceRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, travelDistanceRequirement);
		verify(criterion).setRequirable(null);
	}

	@Test
	public void visit_withTravelDistanceRequirement_setsCriterionMet() {
		visitor.visit(criterion, travelDistanceRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withTravelDistanceRequirement_setsCriterionMetWhenNotStrict() {
		when(travelDistanceRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, travelDistanceRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withTravelDistanceRequirementAndNoUserCoordinates_setsCriterionNotMet() {
		when(addressService.getCoordinatesForUser(USER_ID)).thenReturn(null);
		visitor.visit(criterion, travelDistanceRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withTravelDistanceRequirementAndTooFar_setsCriterionNotMet() {
		when(geocodingService.calculateDistance(userCoordinates, addressCoordinates)).thenReturn(TOO_FAR_DISTANCE);
		visitor.visit(criterion, travelDistanceRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withTravelDistanceRequirement_setsCriterionName() {
		visitor.visit(criterion, travelDistanceRequirement);
		verify(criterion).setName(String.format(
				TravelDistanceRequirement.NAME_TEMPLATE,
				REQUIRED_DISTANCE,
				REQUIREMENT_ADDRESS
		));
	}

	@Test
	public void visit_withTravelDistanceRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, travelDistanceRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withTravelDistanceRequirement_setsCriterionURL() {
		visitor.visit(criterion, travelDistanceRequirement);
		verify(criterion).setUrl("/profile-edit");
	}

	@Test
	public void visit_withTravelDistanceRequirementAndMissingUserPoint_TriesTheGeocodingServiceAgain() {
		when(addressCoordinates.getLatitude()).thenReturn(null);
		visitor.visit(criterion, travelDistanceRequirement);
		verify(geocodingService).geocode(REQUIREMENT_ADDRESS);
	}

	@Test
	public void visit_withTravelDistanceRequirementAndMissingUserPoint_calculatesDistanceWithLateCoordinates() {
		when(addressCoordinates.getLatitude()).thenReturn(null);
		visitor.visit(criterion, travelDistanceRequirement);
		verify(geocodingService).calculateDistance(userCoordinates, lateCoordinates);
	}

	@Test
	public void visit_withOntimeRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, ontimeRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withOntimeRequirement_setsCriterionName() {
		visitor.visit(criterion, ontimeRequirement);
		verify(criterion).setName(OntimeRequirement.DEFAULT_NAME);
	}

	@Test
	public void visit_withOntimeRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, ontimeRequirement);
		verify(criterion).setRequirable(null);
	}

	@Test
	public void visit_withOntimeRequirement_setsCriterionURL() {
		visitor.visit(criterion, ontimeRequirement);
		verify(criterion).setUrl(null);
	}

	@Test
	public void visit_withOntimeRequirementEqual_setsCriterionMet() {
		visitor.visit(criterion, ontimeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withOntimeRequirementGreater_setsCriterionMet() {
		when(ontimeDateIntervalData.getNet90()).thenReturn(ONTIME_RATING_MIN_VALUE + 0.01d);
		visitor.visit(criterion, ontimeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withOntimeRequirementLess_setsCriterionNotMet() {
		when(ontimeDateIntervalData.getNet90()).thenReturn(ONTIME_RATING_MIN_VALUE - 0.01d);
		visitor.visit(criterion, ontimeRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withOntimeRequirementLessAndMandatory_setsCriterionMet() {
		when(ontimeRequirement.isMandatory()).thenReturn(false);
		when(ontimeDateIntervalData.getNet90()).thenReturn(ONTIME_RATING_MIN_VALUE - 0.01d);
		visitor.visit(criterion, ontimeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withDeliverableOntimeRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, deliverableOnTimeRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withDeliverableOntimeRequirement_setsCriterionName() {
		visitor.visit(criterion, deliverableOnTimeRequirement);
		verify(criterion).setName(DeliverableOnTimeRequirement.DEFAULT_NAME);
	}

	@Test
	public void visit_withDeliverableOntimeRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, deliverableOnTimeRequirement);
		verify(criterion).setRequirable(null);
	}

	@Test
	public void visit_withDeliverableOntimeRequirement_setsCriterionURL() {
		visitor.visit(criterion, deliverableOnTimeRequirement);
		verify(criterion).setUrl(null);
	}

	@Test
	public void visit_withDeliverableOntimeRequirementEqual_setsCriterionMet() {
		visitor.visit(criterion, deliverableOnTimeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withDeliverableOntimeRequirementGreater_setsCriterionMet() {
		when(deliverableOnTimeDateIntervalData.getNet90()).thenReturn(DELIVERABLE_ONTIME_RESOURCE_CARD_NET90 + 0.01d);
		visitor.visit(criterion, deliverableOnTimeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withDeliverableOntimeRequirementLess_setsCriterionNotMet() {
		when(deliverableOnTimeDateIntervalData.getNet90()).thenReturn(DELIVERABLE_ONTIME_RESOURCE_CARD_NET90 - 0.01d);
		visitor.visit(criterion, deliverableOnTimeRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withDeliverableOntimeRequirementLessAndMandatory_setsCriterionMet() {
		when(deliverableOnTimeRequirement.isMandatory()).thenReturn(false);
		when(deliverableOnTimeDateIntervalData.getNet90()).thenReturn(DELIVERABLE_ONTIME_RESOURCE_CARD_NET90 - 0.01d);
		visitor.visit(criterion, deliverableOnTimeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withAbandonRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, abandonRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withAbandonRequirement_setsCriterionName() {
		visitor.visit(criterion, abandonRequirement);
		verify(criterion).setName(AbandonRequirement.DEFAULT_NAME);
	}

	@Test
	public void visit_withAbandonRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, abandonRequirement);
		verify(criterion).setRequirable(null);
	}

	@Test
	public void visit_withAbandonRequirement_setsCriterionURL() {
		visitor.visit(criterion, abandonRequirement);
		verify(criterion).setUrl(null);
	}

	@Test
	public void visit_withAbandonRequirementEqual_setsCriterionMet() {
		visitor.visit(criterion, abandonRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withAbandonRequirementGreater_setsCriterionNotMet() {
		when(abandonDateIntervalData.getNet90()).thenReturn(ABANDON_RESOURCE_CARD_NET90 + 1);
		visitor.visit(criterion, abandonRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withAbandonRequirementGreaterAndNotMandatory_setsCriterionMet() {
		when(abandonRequirement.isMandatory()).thenReturn(false);
		when(abandonDateIntervalData.getNet90()).thenReturn(ABANDON_RESOURCE_CARD_NET90 + 1);
		visitor.visit(criterion, abandonRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withAbandonRequirementLess_setsCriterionMet() {
		when(abandonDateIntervalData.getNet90()).thenReturn(ABANDON_RESOURCE_CARD_NET90 - 1);
		visitor.visit(criterion, abandonRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCancelledRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, cancelledRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withCancelledRequirement_setsCriterionName() {
		visitor.visit(criterion, cancelledRequirement);
		verify(criterion).setName(CancelledRequirement.DEFAULT_NAME);
	}

	@Test
	public void visit_withCancelledRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, cancelledRequirement);
		verify(criterion).setRequirable(null);
	}

	@Test
	public void visit_withCancelledRequirement_setsCriterionURL() {
		visitor.visit(criterion, cancelledRequirement);
		verify(criterion).setUrl(null);
	}

	@Test
	public void visit_withCancelledRequirementEqual_setsCriterionMet() {
		visitor.visit(criterion, cancelledRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCancelledRequirementGreater_setsCriterionNotMet() {
		when(cancelledDateIntervalData.getNet90()).thenReturn(CANCELLED_RESOURCE_CARD_NET90 + 1);
		visitor.visit(criterion, cancelledRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withCancelledRequirementless_setsCriterionMet() {
		when(cancelledDateIntervalData.getNet90()).thenReturn(CANCELLED_RESOURCE_CARD_NET90 - 1);
		visitor.visit(criterion, cancelledRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCancelledRequirementGreaterAndNotMandatory_setsCriterionMet() {
		when(cancelledRequirement.isMandatory()).thenReturn(false);
		when(cancelledDateIntervalData.getNet90()).thenReturn(CANCELLED_RESOURCE_CARD_NET90 + 1);
		visitor.visit(criterion, cancelledRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCompanyTypeRequirement_setsCriterionRequirable() {
		visitor.visit(criterion, companyTypeRequirement);
		verify(criterion).setRequirable(companyTypeRequirable);
	}

	@Test
	public void visit_withCompanyTypeRequirement_setsCriterionMet() {
		visitor.visit(criterion, companyTypeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCompanyTypeRequirement_setsCriterionMetWhenNotStrict() {
		when(companyTypeRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, companyTypeRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withCompanyTypeRequirement_setsCriterionNotMet() {
		when(companyTypeRequirable.getId()).thenReturn(CompanyType.CORPORATION.getId());
		visitor.visit(criterion, companyTypeRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withCompanyTypeRequirement_setsCriterionName() {
		visitor.visit(criterion, companyTypeRequirement);
		verify(criterion).setName(REQUIRABLE_NAME);
	}

	@Test
	public void visit_withCompanyTypeRequirement_setsCriterionTypeName() {
		visitor.visit(criterion, companyTypeRequirement);
		verify(criterion).setTypeName(REQUIREMENT_TYPE_NAME);
	}

	@Test
	public void visit_withCompanyTypeRequirement_setsCriterionURL() {
		visitor.visit(criterion, companyTypeRequirement);
		verify(criterion).setUrl(null);
	}

	@Test
	public void visit_withProfileVideoRequirement_setCriteriaNotMet() {
		visitor.visit(criterion, profileVideoRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withProfileVideoRequirementNotMandatory_setCriteriaMet() {
		when(profileVideoRequirement.isMandatory()).thenReturn(false);
		visitor.visit(criterion, profileVideoRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withProfileVideoRequirementNoProfileVideo_setCriteriaNotMet() {
		visitor.visit(criterion, profileVideoRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withProfileVideoRequirement_setsCriterionURL() {
		visitor.visit(criterion, profileVideoRequirement);
		verify(criterion).setUrl("/profile");
	}

	@Test
	public void visit_withProfilePictureRequirementWithProfileVideo_setCriteriaMet() {
		visitor.visit(criterion, profilePictureRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withProfilePictureRequirementNotMandatory_setCriteriaMet() {
		when(profilePictureRequirement.isMandatory()).thenReturn(false);
		when(screeningService.hasProfilePicture(any(Long.class))).thenReturn(false);
		visitor.visit(criterion, profilePictureRequirement);
		verify(criterion).setMet(true);
	}

	@Test
	public void visit_withProfilePictureRequirementNoProfileVideo_setCriteriaNotMet() {
		when(screeningService.hasProfilePicture(any(Long.class))).thenReturn(false);
		visitor.visit(criterion, profilePictureRequirement);
		verify(criterion).setMet(false);
	}

	@Test
	public void visit_withProfilePictureRequirement_setsCriterionURL() {
		visitor.visit(criterion, profilePictureRequirement);
		verify(criterion).setUrl("/profile-edit/photo");
	}
}
