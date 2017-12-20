package com.workmarket.service.business.requirementsets;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Point;
import com.workmarket.biz.esignature.gen.Messages.GetRequestByTemplateIdAndUserUuidResp;
import com.workmarket.biz.esignature.gen.Messages.RequestState;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReference;
import com.workmarket.domains.groups.service.association.UserGroupAssociationService;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.analytics.ResourceScoreField;
import com.workmarket.domains.model.analytics.ScoreCard;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.company.CompanyType;
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
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.RequirementSetable;
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
import com.workmarket.domains.model.requirementset.document.DocumentRequirable;
import com.workmarket.domains.model.requirementset.document.DocumentRequirement;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirement;
import com.workmarket.domains.model.requirementset.esignature.EsignatureRequirable;
import com.workmarket.domains.model.requirementset.esignature.EsignatureRequirement;
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
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.domains.work.model.Work;
import com.workmarket.screening.model.Screening;
import com.workmarket.screening.model.ScreeningStatusCode;
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
import com.workmarket.service.business.UserUserGroupDocumentReferenceService;
import com.workmarket.service.esignature.EsignatureService;
import com.workmarket.service.infra.business.GeocodingService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class EligibilityVisitorImpl implements EligibilityVisitor {

	@Autowired private AddressService addressService;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private CertificationService certificationService;
	@Autowired private ContractService contractService;
	@Autowired private EsignatureService esignatureService;
	@Autowired private GeocodingService geocodingService;
	@Autowired private IndustryService industryService;
	@Autowired private InsuranceService insuranceService;
	@Autowired private LaneService laneService;
	@Autowired private LicenseService licenseService;
	@Autowired private ProfileService profileService;
	@Autowired private RatingService ratingService;
	@Autowired private ScreeningService screeningService;
	@Autowired private UserService userService;
	@Autowired private UserUserGroupDocumentReferenceService userUserGroupDocumentReferenceService;
	@Autowired private UserGroupAssociationService userGroupAssociationService;

	@Override
	public void visit(Criterion criterion, AgreementRequirement requirement) {
		AgreementRequirable requirable = requirement.getAgreementRequirable();
		final Long contractId = requirable.getId();
		final Long userId = criterion.getUser().getId();
		boolean hasContractVersionUserSignature = hasContractVersionUserSignature(contractId, userId);

		criterion.setRequirable(requirable);
		criterion.setMet(!requirement.isMandatory() || hasContractVersionUserSignature);
		criterion.setName(requirable.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/agreements/" + contractId);
	}

	private boolean hasContractVersionUserSignature(final Long contractId, final Long userId) {
		final Optional<Long> requiredVersionId = contractService.findMostRecentContractVersionIdByContractId(contractId);
		if (!requiredVersionId.isPresent()) {
			return false;
		}

		final Long contractVersionId = requiredVersionId.get();
		final ContractVersionUserSignature signature =
				contractService.findContractVersionUserSignatureByContractVersionIdAndUserId(contractVersionId, userId);

		return signature != null;
	}

	@Override
	@Deprecated
	public void visit(Criterion criterion, AvailabilityRequirement requirement) {
		WeekdayRequirable requirable = requirement.getWeekdayRequirable();

		UserAvailability availability = userService
				.findActiveWorkingHoursByUserId(
						criterion.getUser().getId(),
						requirement.getDayOfWeek());

		TimeZone tz;
		if (criterion.getRequirementSetable().getClass() == UserGroup.class) {
			User user = userService.getUser(criterion.getUser().getId());
			tz = user.getProfile().getTimeZone();
		} else {
			tz = criterion.getRequirementSetable().getRequirementSetableTimeZone();
		}

		Calendar requiredFromTime =
				DateUtilities.getCalendarFromTimeString(
						requirement.getFromTime(),
						tz.getTimeZoneId());

		Calendar requiredToTime =
				DateUtilities.getCalendarFromTimeString(
						requirement.getToTime(),
						tz.getTimeZoneId());

		boolean eligible = false;

		if (availability != null) {
			Calendar availableFromTime =
					DateUtilities.changeTimeZone(
							availability.getFromTime(),
							tz.getTimeZoneId());

			Calendar availableToTime =
					DateUtilities.changeTimeZone(
							availability.getToTime(),
							tz.getTimeZoneId());

			eligible = DateUtilities.timeIntervalsOverlap(
					requiredFromTime,
					requiredToTime,
					availableFromTime,
					availableToTime
			);
		}

		criterion.setRequirable(requirable);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(
				String.format(
						AvailabilityRequirement.NAME_TEMPLATE,
						requirable.getName(),
						requirement.getFromTime(),
						requirement.getToTime()
				));
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/mysettings/hours");
	}

	@Override
	public void visit(Criterion criterion, BackgroundCheckRequirement requirement) {
		Screening backgroundCheck = screeningService
				.findMostRecentBackgroundCheck(criterion.getUser().getId());

		boolean eligible = (
				backgroundCheck != null && backgroundCheck.getStatus().equals(ScreeningStatusCode.PASSED)
		);

		criterion.setRequirable(null);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(BackgroundCheckRequirement.DEFAULT_NAME);
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/screening/bkgrnd");
	}

	@Override
	public void visit(Criterion criterion, CertificationRequirement requirement) {
		CertificationRequirable requirable = requirement.getCertificationRequirable();
		UserCertificationAssociation userCertificationAssociation =
				certificationService.findActiveVerifiedAssociationByCertificationIdAndUserId(
						requirable.getId(),
						criterion.getUser().getId()
				);

		criterion.setRequirable(requirable);
		criterion.setMet(
				!requirement.isMandatory() ||
						(userCertificationAssociation != null && !isExpired(userCertificationAssociation.getExpirationDate())));

		if (userCertificationAssociation != null) {
			criterion.setExpired(isExpired(userCertificationAssociation.getExpirationDate()));
			criterion.setExpirationDate(userCertificationAssociation.getExpirationDate());
		}

		criterion.setRemoveWhenExpired(requirement.isRemoveMembershipOnExpiry());
		criterion.setWarnWhenExpired(requirement.isNotifyOnExpiry());
		criterion.setName(requirable.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/profile-edit/certifications");
	}

	@Override
	public void visit(Criterion criterion, CompanyTypeRequirement requirement) {
		CompanyTypeRequirable requirable = requirement.getCompanyTypeRequirable();

		boolean flag = userService.getUser(criterion.getUser().getId()).getCompany().getOperatingAsIndividualFlag();
		CompanyType userCompanyType = flag ? CompanyType.SOLE_PROPRIETOR : CompanyType.CORPORATION;
		CompanyType requiredCompanyType = CompanyType.getById(requirable.getId());

		criterion.setRequirable(requirable);
		criterion.setMet(!requirement.isMandatory() || userCompanyType.equals(requiredCompanyType));
		criterion.setName(requirable.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setAnyOf(true);
		// There is no url the user can go to change this. Just being explicit.
		criterion.setUrl(null);
	}

	@Override
	public void visit(Criterion criterion, CountryRequirement requirement) {
		CountryRequirable requirable = requirement.getCountryRequirable();
		Country country = null;

		Optional<PostalCode> postalCode = profileService
				.findPostalCodeForUser(criterion.getUser().getId());

		if (postalCode.isPresent()) {
			country = postalCode.get().getCountry();
		}

		boolean eligible = country != null && requirable.getId().equals(country.getId());

		criterion.setRequirable(requirable);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(requirable.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/profile-edit");
		criterion.setAnyOf(true);
	}

	@Override
	public void visit(Criterion criterion, DrugTestRequirement requirement) {
		Screening drugTest = screeningService.findMostRecentDrugTest(criterion.getUser().getId());

		boolean eligible = (
				drugTest != null && drugTest.getStatus().equals(ScreeningStatusCode.PASSED)
		);

		criterion.setRequirable(null);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(DrugTestRequirement.DEFAULT_NAME);
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/screening/drug");
	}

	@Override
	public void visit(Criterion criterion, IndustryRequirement requirement) {
		IndustryRequirable requirable = requirement.getIndustryRequirable();

		Profile userProfile = profileService.findProfile(criterion.getUser().getId());
		List<Long> industries = industryService.getIndustryIdsForProfile(userProfile.getId());

		boolean eligible = industries != null && industries.contains(requirable.getId());

		criterion.setRequirable(requirable);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(requirable.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setAnyOf(true);
		criterion.setUrl("/profile-edit/industries");
	}

	@Override
	public void visit(Criterion criterion, InsuranceRequirement requirement) {
		InsuranceRequirable requirable = requirement.getInsuranceRequirable();
		UserInsuranceAssociation userInsuranceAssociation =
				insuranceService.findActiveVerifiedAssociationByInsuranceIdAndUserId(
						requirable.getId(),
						criterion.getUser().getId()
				);

		criterion.setRequirable(requirable);
		// TODO: enforce minimum coverage
		criterion.setMet(
				!requirement.isMandatory() ||
						(userInsuranceAssociation != null && !isExpired(userInsuranceAssociation.getExpirationDate())));

		if (userInsuranceAssociation != null) {
			criterion.setExpired(isExpired(userInsuranceAssociation.getExpirationDate()));
			criterion.setExpirationDate(userInsuranceAssociation.getExpirationDate());
		}

		criterion.setRemoveWhenExpired(requirement.isRemoveMembershipOnExpiry());
		criterion.setWarnWhenExpired(requirement.isNotifyOnExpiry());

		if (requirement.getMinimumCoverageAmount().compareTo(BigDecimal.ZERO) > 0) {
			criterion.setName(String.format(
					InsuranceRequirement.NAME_TEMPLATE,
					requirement.getInsuranceRequirable().getName(),
					NumberFormat.getInstance().format(requirement.getMinimumCoverageAmount())
			));
		} else {
			criterion.setName(requirement.getInsuranceRequirable().getName());
		}

		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/profile-edit/insurance");
	}

	@Override
	public void visit(Criterion criterion, LicenseRequirement requirement) {
		LicenseRequirable requirable = requirement.getLicenseRequirable();
		UserLicenseAssociation userLicenseAssociation =
				licenseService.findActiveVerifiedAssociationByLicenseIdAndUserId(
						requirable.getId(),
						criterion.getUser().getId()
				);

		criterion.setRequirable(requirable);
		criterion.setMet(
				!requirement.isMandatory() ||
						(userLicenseAssociation != null && !isExpired(userLicenseAssociation.getExpirationDate())));

		if (userLicenseAssociation != null) {
			criterion.setExpired(isExpired(userLicenseAssociation.getExpirationDate()));
			criterion.setExpirationDate(userLicenseAssociation.getExpirationDate());
		}

		criterion.setRemoveWhenExpired(requirement.isRemoveMembershipOnExpiry());
		criterion.setWarnWhenExpired(requirement.isNotifyOnExpiry());
		criterion.setName(requirable.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/profile-edit/licenses");
	}

	@Override
	public void visit(Criterion criterion, RatingRequirement requirement) {
		Double rating = ratingService.findSatisfactionRateForUser(criterion.getUser().getId());

		boolean eligible = rating != null && (rating.compareTo(requirement.getValue().doubleValue()) > -1);

		criterion.setRequirable(null);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(requirement.getName() + "%");
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl(null);
	}

	@Override
	public void visit(Criterion criterion, ResourceTypeRequirement requirement) {
		ResourceTypeRequirable requirable = requirement.getResourceTypeRequirable();
		Set<LaneType> requiredLanes = new HashSet<>();
		ResourceType requiredResourceType = ResourceType.getById(requirable.getId());

		switch (requiredResourceType) {
			case EMPLOYEE:
				requiredLanes.add(LaneType.LANE_1);
				break;
			case CONTRACTOR:
				requiredLanes.add(LaneType.LANE_2);
				requiredLanes.add(LaneType.LANE_3);
				break;
			default:
				// noop
		}

		LaneAssociation lane = laneService
				.findActiveAssociationByUserIdAndCompanyId(
						criterion.getUser().getId(),
						requirement.getRequirementSet().getCompany().getId());

		boolean eligible = lane != null && requiredLanes.contains(lane.getLaneType());

		criterion.setRequirable(requirable);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(requirable.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setAnyOf(true);
		criterion.setUrl(null);
	}

	@Override
	public void visit(Criterion criterion, TestRequirement requirement) {
		TestRequirable requirable = requirement.getTestRequirable();
		AssessmentUserAssociation test = assessmentService
				.findAssessmentUserAssociationByUserAndAssessment(
						criterion.getUser().getId(),
						requirable.getId());

		boolean eligible = test != null && test.getPassedFlag();

		criterion.setRequirable(requirable);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(requirable.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/lms/view/take/" + requirable.getId());
	}

	@Override
	public void visit(Criterion criterion, TravelDistanceRequirement requirement) {
		Coordinate userCoordinates = addressService.getCoordinatesForUser(criterion.getUser().getId());
		Coordinate addressCoordinates = requirement.getCoordinates();

		if (userCoordinates == null) {
			criterion.setMet(false);
		} else {
			// if the requirement has bad coordinates, try one more time
			if (addressCoordinates.getLatitude() == null || addressCoordinates.getLongitude() == null) {
				addressCoordinates = makeCoordinate(geocodingService.geocode(requirement.getAddress()));
			}

			double distanceFromUser = geocodingService.calculateDistance(userCoordinates, addressCoordinates);
			double maxTravelDistance = requirement.getDistance();
			criterion.setMet(!requirement.isMandatory() || distanceFromUser <= maxTravelDistance);
		}

		criterion.setRequirable(null);
		criterion.setName(String.format(
				TravelDistanceRequirement.NAME_TEMPLATE,
				requirement.getDistance(),
				requirement.getAddress()
		));
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setAnyOf(true);
		criterion.setUrl("/profile-edit");
	}

	@Override
	public void visit(Criterion criterion, ProfileVideoRequirement requirement) {
		boolean eligible = screeningService.hasProfileVideo(criterion.getUser().getId());

		criterion.setRequirable(null);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(ProfileVideoRequirement.DEFAULT_NAME);
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/profile");
	}

	@Override
	public void visit(Criterion criterion, OntimeRequirement requirement) {
		ScoreCard scoreCard = analyticsService.getResourceScoreCard(criterion.getUser().getId());
		long minPercentage = Math.round(((ScoreCard.DateIntervalData) scoreCard.getValues().get(ResourceScoreField.ON_TIME_PERCENTAGE)).getNet90() * 100);

		boolean eligible = minPercentage >= requirement.getMinimumPercentage();

		criterion.setRequirable(null);
		criterion.setName(requirement.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl(null);
		criterion.setMet(!requirement.isMandatory() || eligible);
	}

	@Override
	public void visit(Criterion criterion, DeliverableOnTimeRequirement requirement) {
		ScoreCard scoreCard = analyticsService.getResourceScoreCard(criterion.getUser().getId());
		long minPercentage = Math.round(((ScoreCard.DateIntervalData) scoreCard.getValues().get(ResourceScoreField.DELIVERABLE_ON_TIME_PERCENTAGE)).getNet90() * 100);

		boolean eligible = minPercentage >= requirement.getMinimumPercentage();

		criterion.setRequirable(null);
		criterion.setName(requirement.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl(null);
		criterion.setMet(!requirement.isMandatory() || eligible);
	}

	@Override
	public void visit(Criterion criterion, AbandonRequirement requirement) {
		ScoreCard scoreCard = analyticsService.getResourceScoreCard(criterion.getUser().getId());
		Double abandons = ((ScoreCard.DateIntervalData) scoreCard.getValues().get(ResourceScoreField.ABANDONED_WORK)).getNet90();

		boolean eligible = abandons <= requirement.getMaximumAllowed();

		criterion.setRequirable(null);
		criterion.setName(requirement.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl(null);
		criterion.setMet(!requirement.isMandatory() || eligible);
	}

	@Override
	public void visit(Criterion criterion, CancelledRequirement requirement) {
		ScoreCard scoreCard = analyticsService.getResourceScoreCard(criterion.getUser().getId());
		Double cancelled = ((ScoreCard.DateIntervalData) scoreCard.getValues().get(ResourceScoreField.CANCELLED_WORK)).getNet90();

		boolean eligible = cancelled <= requirement.getMaximumAllowed();

		criterion.setRequirable(null);
		criterion.setName(requirement.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl(null);
		criterion.setMet(!requirement.isMandatory() || eligible);
	}

	@Override
	public void visit(Criterion criterion, ProfilePictureRequirement requirement) {
		boolean eligible = screeningService.hasProfilePicture(criterion.getUser().getId());

		criterion.setRequirable(null);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(ProfilePictureRequirement.DEFAULT_NAME);
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/profile-edit/photo");
	}

	@Override
	public void visit(Criterion criterion, GroupMembershipRequirement requirement) {
		GroupMembershipRequirable requirable = requirement.getGroupMembershipRequirable();
		List<Long> groupIds = userGroupAssociationService.findAllUserGroupAssociationsByUserId(criterion.getUser().getId());

		boolean eligible = groupIds.contains(requirable.getId());

		criterion.setRequirable(requirable);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(requirable.getName());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl("/groups/" + requirable.getId());
	}

	@Override
	public void visit(Criterion criterion, PaidRequirement requirement) {
		ScoreCard scoreCard = analyticsService.getResourceScoreCard(criterion.getUser().getId());
		Double completedAssignments = ((ScoreCard.DateIntervalData) scoreCard.getValues().get(ResourceScoreField.COMPLETED_WORK)).getNet90();

		boolean eligible = completedAssignments >= requirement.getMinimumAssignments();

		criterion.setRequirable(null);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(PaidRequirement.DEFAULT_NAME);
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl(null);
	}

	@Override
	public void visit(Criterion criterion, CompanyWorkRequirement requirement) {
		CompanyWorkRequirable requirable = requirement.getCompanyWorkRequirable();
		ScoreCard scoreCard = analyticsService.getResourceScoreCard(criterion.getUser().getId());
		Double completedAssignments =
				((ScoreCard.DateIntervalData) scoreCard.getValues().get(ResourceScoreField.COMPLETED_WORK)).getNet90();

		boolean eligible = completedAssignments != null ?
				completedAssignments >= requirement.getMinimumWorkCount() : requirement.getMinimumWorkCount() == 0;

		criterion.setRequirable(requirable);
		criterion.setMet(!requirement.isMandatory() || eligible);
		criterion.setName(CompanyWorkRequirement.DEFAULT_NAME);
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setUrl(null);
	}

	@Override
	public void visit(Criterion criterion, DocumentRequirement requirement) {
		DocumentRequirable requirable = requirement.getDocumentRequirable();
		Long userId = criterion.getUser().getId();

		criterion.setName(requirable.getName());
		criterion.setExpires(requirement.isRequiresExpirationDate());
		criterion.setUrl("/asset/download/" + requirable.getUuid());
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));
		criterion.setRequirable(requirable);

		UserUserGroupDocumentReference documentReference =
				userUserGroupDocumentReferenceService.findDocumentReferenceByUserIdAndDocumentId(userId, requirable.getId());

		if (documentReference != null) {
			criterion.setMet(true);
			criterion.setName(documentReference.getReferencedDocument().getName());
			criterion.setUrl(documentReference.getReferencedDocument().getId().toString());
			criterion.setExpirationDate(documentReference.getExpirationDate());
		}
	}

	@Override
	public void visit(final Criterion criterion, final EsignatureRequirement requirement) {
		criterion.setTypeName(requirement.getHumanTypeName());
		criterion.setTypeClassName(StringUtilities.convertCamelCaseToUnderscored(requirement.getClass().getSimpleName()));

		final Optional<String> templateName =
				getTemplateName(criterion.getRequirementSetable(), requirement.getTemplateUuid());
		if (!templateName.isPresent()) {
			return;
		}
		criterion.setName(templateName.get());
		final EsignatureRequirable esignatureRequirable = EsignatureRequirable.newBuilder()
				.setTemplateUuid(requirement.getTemplateUuid())
				.build();
		criterion.setRequirable(esignatureRequirable);

		final String userUuid = criterion.getUser().getUuid();
		final GetRequestByTemplateIdAndUserUuidResp esignatureStatus =
				esignatureService.getStatus(requirement.getTemplateUuid(), userUuid);
		if (!esignatureStatus.getStatus().getSuccess()) {
			return;
		}

		if (esignatureStatus.getRequest().getStatus().equals(RequestState.SIGNED)) {
			criterion.setMet(true);
		}
	}

	private Optional<String> getTemplateName(final RequirementSetable requirementSetable, final String templateUuid) {
		final Optional<String> companyUuid = getCompanyUuid(requirementSetable);
		if (!companyUuid.isPresent()) {
			return Optional.absent();
		}
		return esignatureService.getTemplateName(templateUuid, companyUuid.get());
	}

	private Optional<String> getCompanyUuid(final RequirementSetable requirementSetable) {
		if (requirementSetable instanceof UserGroup) {
			return Optional.of(((UserGroup) requirementSetable).getCompany().getUuid());
		} else if (requirementSetable instanceof Work) {
			return Optional.of(((Work) requirementSetable).getCompany().getUuid());
		}
		return Optional.absent();
	}

	public Coordinate makeCoordinate(Point point) {
		return new Coordinate(point);
	}

	private boolean isExpired(Calendar expirationDate) {
		return expirationDate != null && expirationDate.compareTo(Calendar.getInstance()) < 0;
	}
}
