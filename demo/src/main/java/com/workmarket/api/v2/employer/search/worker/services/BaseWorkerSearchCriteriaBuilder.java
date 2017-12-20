package com.workmarket.api.v2.employer.search.worker.services;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.workmarket.api.v2.employer.search.common.model.SortDirection;
import com.workmarket.api.v2.employer.search.worker.model.CompanyType;
import com.workmarket.api.v2.employer.search.worker.model.LaneType;
import com.workmarket.api.v2.employer.search.worker.model.VerificationType;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRequestDTO;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.query.location.LocationQueryCreationService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.search.cache.StateLookupCache;
import com.workmarket.search.worker.query.model.CompanyCriteria;
import com.workmarket.search.worker.query.model.CompanyCriteriaBuilder;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.FindWorkerCriteriaBuilder;
import com.workmarket.search.worker.query.model.GroupCriteria;
import com.workmarket.search.worker.query.model.GroupCriteriaBuilder;
import com.workmarket.search.worker.query.model.InsuranceCriteria;
import com.workmarket.search.worker.query.model.InsuranceCriteriaBuilder;
import com.workmarket.search.worker.query.model.LocationCriteria;
import com.workmarket.search.worker.query.model.LocationCriteriaBuilder;
import com.workmarket.search.worker.query.model.ProfileCriteria;
import com.workmarket.search.worker.query.model.ProfileCriteriaBuilder;
import com.workmarket.search.worker.query.model.QualificationsCriteria;
import com.workmarket.search.worker.query.model.QualificationsCriteriaBuilder;
import com.workmarket.search.worker.query.model.RatingCriteria;
import com.workmarket.search.worker.query.model.RatingCriteriaBuilder;
import com.workmarket.search.worker.query.model.ScreeningCriteria;
import com.workmarket.search.worker.query.model.ScreeningCriteriaBuilder;
import com.workmarket.search.worker.query.model.SortCriteria;
import com.workmarket.search.worker.query.model.SortCriteriaBuilder;
import com.workmarket.search.worker.query.model.SortType;
import com.workmarket.search.worker.query.model.WorkCriteria;
import com.workmarket.search.worker.query.model.WorkCriteriaBuilder;
import com.workmarket.search.worker.query.model.WorkerRelation;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.external.GeocodingException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Class responsible for mapping from our incoming WorkerSearchRequestDTO to a FindClientCriteria.
 */
public abstract class BaseWorkerSearchCriteriaBuilder {
	private final Logger logger = LoggerFactory.getLogger(BaseWorkerSearchCriteriaBuilder.class);

	private final WorkService workService;
	private final VendorService vendorService;
	private final LocationQueryCreationService locationQueryCreationService;
	private final StateLookupCache stateLookupCache;

	private final ExtendedUserDetails userDetails;
	private final WorkerSearchRequestDTO workerSearchRequestDTO;

	private AbstractWork work;

	protected final FindWorkerCriteriaBuilder findWorkerCriteriaBuilder = new FindWorkerCriteriaBuilder();


	/**
	 * Constructor.
	 * @param workService The work service used to get our work details
	 * @param vendorService The vendor service
	 * @param locationQueryCreationService The location service
	 * @param stateLookupCache The state lookup cache
	 * @param userDetails The user details
	 * @param workerSearchRequestDTO The incoming criteria
	 */
	public BaseWorkerSearchCriteriaBuilder(final WorkService workService,
	                                       final VendorService vendorService,
	                                       final LocationQueryCreationService locationQueryCreationService,
	                                       final StateLookupCache stateLookupCache,
	                                       final ExtendedUserDetails userDetails,
	                                       final WorkerSearchRequestDTO workerSearchRequestDTO) {
		this.workService = workService;
		this.vendorService = vendorService;
		this.locationQueryCreationService = locationQueryCreationService;
		this.stateLookupCache = stateLookupCache;
		this.userDetails = userDetails;
		this.workerSearchRequestDTO = workerSearchRequestDTO;
	}


	/**
	 * Adds the custom settings for the specific derived class.
	 */
	protected abstract void createCustomCriteria();

	/**
	 * Create our FindWorkerCriteria needed to make our service call.
	 * @return FindWorkerCriteria The service criteria
	 */
	public FindWorkerCriteria build() {
		findWorkerCriteriaBuilder.setRequestingUserId(String.valueOf(userDetails.getId()));
		findWorkerCriteriaBuilder.setRequestingCompanyId(String.valueOf(userDetails.getCompanyId()));

		// if we have a work number then retrieve it for use by various create methods below
		if (StringUtils.isNotEmpty(workerSearchRequestDTO.getWork_number())) {
			work = workService.findWorkByWorkNumber(workerSearchRequestDTO.getWork_number(), false);
		}

		createCompanyCriteria();
		createGroupCriteria();
		createProfileCriteria();
		createLocationCriteria();
		createQualificationsCriteria();
		createRatingCriteria();
		createScreeningCriteria();
		createInsuranceCriteria();
		createWorkCriteria();
		createSortCriteria();
		createCustomCriteria();
		findWorkerCriteriaBuilder.setKeywords(StringUtils.isEmpty(workerSearchRequestDTO.getKeyword()) ? null : workerSearchRequestDTO.getKeyword());

		return findWorkerCriteriaBuilder.build().get();
	}

	private void createProfileCriteria() {
		ProfileCriteriaBuilder builder = new ProfileCriteriaBuilder();

		if (BooleanUtils.isTrue(workerSearchRequestDTO.getAvatar())) {
			builder.setHasProfilePicture(workerSearchRequestDTO.getAvatar());
		}

		// worker relations
		// if our work assignment is internal pricing or the user is a dispatcher then only allow lane1 regardless
		// of what filters might show
		if ((work != null && work.getPricingStrategyType() != null
			&& work.getPricingStrategyType() == PricingStrategyType.INTERNAL)
			|| (userDetails.isDispatcher())) {
			builder.addWorkerRelation(WorkerRelation.employees);
		} else if (userDetails.getCompanyIsLocked()) {
			// locked companies can only see their internal/employees
			builder.addWorkerRelations(Lists.newArrayList(WorkerRelation.internal, WorkerRelation.employees));
		} else {
			Set<LaneType> laneTypes = workerSearchRequestDTO.getLanes();
			if (laneTypes != null) {
				for (LaneType lane : laneTypes) {
					if (lane == LaneType.lane0) {
						builder.addWorkerRelation(WorkerRelation.internal);
					} else if (lane == LaneType.lane1) {
						builder.addWorkerRelation(WorkerRelation.employees);
					} else if (lane == LaneType.lane2) {
						builder.addWorkerRelation(WorkerRelation.contractors);
					} else if (lane == LaneType.lane3) {
						builder.addWorkerRelation(WorkerRelation.thirdParties);
					} else if (lane == LaneType.lane4) {
						builder.addWorkerRelation(WorkerRelation.everyoneElse);
					} else if (lane == LaneType.lane4) {
						builder.addWorkerRelation(WorkerRelation.contractors);
						builder.addWorkerRelation(WorkerRelation.thirdParties);
					}
				}
			}
		}

		builder.build().transform(new Function<ProfileCriteria, FindWorkerCriteriaBuilder>() {
			@Nullable
			@Override
			public FindWorkerCriteriaBuilder apply(@Nullable ProfileCriteria profileCriteria) {
				return findWorkerCriteriaBuilder.setProfileCriteria(profileCriteria);
			}
		});
	}

	private void createCompanyCriteria() {
		CompanyCriteriaBuilder builder = new CompanyCriteriaBuilder();

		List<String> industries = convertToList(workerSearchRequestDTO.getIndustries());
		if (CollectionUtils.isNotEmpty(industries)) {
			builder.addIndustries(industries);
		}

		List<com.workmarket.search.worker.query.model.CompanyType> companyTypes =
			convertCompanyType(workerSearchRequestDTO.getCompanyTypes());
		if (CollectionUtils.isNotEmpty(companyTypes)) {
			builder.addCompanyTypes(companyTypes);

		}

		builder.build().transform(new Function<CompanyCriteria, FindWorkerCriteriaBuilder>() {
			@Nullable
			@Override
			public FindWorkerCriteriaBuilder apply(@Nullable CompanyCriteria companyCriteria) {
				return findWorkerCriteriaBuilder.setCompanyCriteria(companyCriteria);
			}
		});
	}

	private void createGroupCriteria() {
		GroupCriteriaBuilder builder = new GroupCriteriaBuilder();

		List<String> sharedTalentPools = convertToList( workerSearchRequestDTO.getSharedGroups());
		if (CollectionUtils.isNotEmpty(sharedTalentPools)) {
			builder.addSharedTalentPools(sharedTalentPools);
		}

		List<String> privateTalentPools = convertToList( workerSearchRequestDTO.getGroups());
		if (CollectionUtils.isNotEmpty(privateTalentPools)) {
			builder.addPrivateTalentPools(privateTalentPools);
		}

		builder.build().transform(new Function<GroupCriteria, FindWorkerCriteriaBuilder>() {
			@Nullable
			@Override
			public FindWorkerCriteriaBuilder apply(@Nullable GroupCriteria groupCriteria) {
				return findWorkerCriteriaBuilder.setGroupCriteria(groupCriteria);
			}
		});
	}

	private void createWorkCriteria() {
		WorkCriteriaBuilder builder = new WorkCriteriaBuilder();
		if (work != null) {
			builder.setWorkNumber(work.getWorkNumber())
				.setTitle(work.getTitle())
				.setDescription(Jsoup.parse(work.getDescription()).text())
				.setSkills(work.getDesiredSkills());
			// handle declines
			List<Long> declinedCompanies = vendorService.getDeclinedVendorIdsByWork(work.getId());
			if (declinedCompanies.contains(userDetails.getCompanyId())) {
				declinedCompanies.remove(userDetails.getCompanyId());
			}

			if (CollectionUtils.isNotEmpty(declinedCompanies)) {
				for (Long companyId : declinedCompanies) {
					builder.addDeclinedCompany(companyId.toString());
				}
			}
		}

		builder.build().transform(new Function<WorkCriteria, FindWorkerCriteriaBuilder>() {
			@Nullable
			@Override
			public FindWorkerCriteriaBuilder apply(@Nullable WorkCriteria workCriteria) {
				return findWorkerCriteriaBuilder.setWorkCriteria(workCriteria);
			}
		});
	}

	private void createInsuranceCriteria() {
		InsuranceCriteriaBuilder builder = new InsuranceCriteriaBuilder();


		if ( workerSearchRequestDTO.getWorkersCompCoverage() != null) {
			builder.setWorkersCompCoverageAmount( workerSearchRequestDTO.getWorkersCompCoverage().longValue());
		}

		if ( workerSearchRequestDTO.getGeneralLiabilityCoverage() != null) {
			builder.setGeneralLiabilityCoverageAmount( workerSearchRequestDTO.getGeneralLiabilityCoverage().longValue());
		}

		if ( workerSearchRequestDTO.getErrorsAndOmissionsCoverage() != null) {
			builder.setErrorsAndOmissionsCoverageAmount( workerSearchRequestDTO.getErrorsAndOmissionsCoverage().longValue());
		}

		if ( workerSearchRequestDTO.getAutomobileCoverage() != null) {
			builder.setAutomobileCoverageAmount( workerSearchRequestDTO.getAutomobileCoverage().longValue());
		}

		if ( workerSearchRequestDTO.getContractorsCoverage() != null) {
			builder.setContractorsCoverageAmount( workerSearchRequestDTO.getContractorsCoverage().longValue());
		}

		if ( workerSearchRequestDTO.getCommercialGeneralLiabilityCoverage() != null) {
			builder.setCommercialGeneralLiabilityCoverageAmount( workerSearchRequestDTO.getCommercialGeneralLiabilityCoverage().longValue());
		}

		if ( workerSearchRequestDTO.getBusinessLiabilityCoverage() != null) {
			builder.setBusinessLiabilityCoverageAmount( workerSearchRequestDTO.getBusinessLiabilityCoverage().longValue());
		}

		builder.build().transform(new Function<InsuranceCriteria, FindWorkerCriteriaBuilder>() {
			@Nullable
			@Override
			public FindWorkerCriteriaBuilder apply(@Nullable InsuranceCriteria insuranceCriteria) {
				return findWorkerCriteriaBuilder.setInsuranceCriteria(insuranceCriteria);
			}
		});
	}

	private void createSortCriteria() {
		SortCriteriaBuilder builder = new SortCriteriaBuilder();

		if (workerSearchRequestDTO.getSortType() == null) {
			builder.setSortType(SortType.relevancy);
		} else {
			com.workmarket.api.v2.employer.search.common.model.SortType sortByType = workerSearchRequestDTO.getSortType();

			com.workmarket.search.core.model.SortDirection sortDirection;
			if (workerSearchRequestDTO.getSortDirection() == null || workerSearchRequestDTO.getSortDirection() == SortDirection.desc) {
				sortDirection = com.workmarket.search.core.model.SortDirection.desc;
			} else {
				sortDirection = com.workmarket.search.core.model.SortDirection.asc;
			}

			switch (sortByType) {
				case distance:
					try {
						if (locationQueryCreationService.getGeoLocationPoint(workerSearchRequestDTO.getAddress()) != null) {
							builder.setSortType(SortType.distance);
						}
					} catch (GeocodingException ge) {
						builder.setSortType(SortType.relevancy);
						builder.setSortDirection(sortDirection);
					}
					break;
				case name:
					builder.setSortType(SortType.name);
					builder.setSortDirection(sortDirection);
					break;
				case hourly_rate:
					builder.setSortType(SortType.hourlyRate);
					builder.setSortDirection(sortDirection);
					break;
				case rating:
					builder.setSortType(SortType.rating);
					break;
				case work_completed:
					builder.setSortType(SortType.workCompletedCount);
					break;
				case created_on:
					builder.setSortType(SortType.createdOn);
					builder.setSortDirection(sortDirection);
					break;
				default:
					builder.setSortType(SortType.relevancy);
			}
		}


		builder.build().transform(new Function<SortCriteria, FindWorkerCriteriaBuilder>() {
			@Nullable
			@Override
			public FindWorkerCriteriaBuilder apply(@Nullable SortCriteria sortCriteria) {
				return findWorkerCriteriaBuilder.setSortCriteria(sortCriteria);
			}
		});
	}

	private void createScreeningCriteria() {
		ScreeningCriteriaBuilder builder = new ScreeningCriteriaBuilder();

		Set<VerificationType> screeningChoices = workerSearchRequestDTO.getVerifications();
		if (CollectionUtils.isNotEmpty(screeningChoices)) {
			for (VerificationType choice : screeningChoices) {
				switch (choice) {
					case background_check:
						builder.setHasBackgroundCheck(Boolean.TRUE);
						break;
					case background_check_last_6months:
						builder.setHasCurrentBackgroundCheck(Boolean.TRUE);
						break;
					case drug_test:
						builder.setHasPassedDrugTest(Boolean.TRUE);
						break;
				}
			}

		}

		builder.build().transform(new Function<ScreeningCriteria, FindWorkerCriteriaBuilder>() {
			@Nullable
			@Override
			public FindWorkerCriteriaBuilder apply(@Nullable ScreeningCriteria screeningCriteria) {
				return findWorkerCriteriaBuilder.setScreeningCriteria(screeningCriteria);
			}
		});
	}

	private void createRatingCriteria() {
		RatingCriteriaBuilder builder = new RatingCriteriaBuilder();

		if ( workerSearchRequestDTO.getOnTimePercentage() != null) {
			builder.setOnTimePercent( workerSearchRequestDTO.getOnTimePercentage().longValue());
		}

		if ( workerSearchRequestDTO.getSatisfactionRate() != null) {
			builder.setOnTimePercent( workerSearchRequestDTO.getSatisfactionRate().longValue());
		}

		if ( workerSearchRequestDTO.getDeliverableOnTimePercentage() != null) {
			builder.setOnTimePercent( workerSearchRequestDTO.getDeliverableOnTimePercentage().longValue());
		}

		builder.build().transform(new Function<RatingCriteria, FindWorkerCriteriaBuilder>() {
			@Nullable
			@Override
			public FindWorkerCriteriaBuilder apply(@Nullable RatingCriteria ratingCriteria) {
				return findWorkerCriteriaBuilder.setRatingCriteria(ratingCriteria);
			}
		});
	}

	private void createLocationCriteria() {
		LocationCriteriaBuilder builder = new LocationCriteriaBuilder();

		if (!StringUtils.isEmpty( workerSearchRequestDTO.getAddress())) {

			if (stateLookupCache.isStateQuery( workerSearchRequestDTO.getAddress())) {
				builder.setState(stateLookupCache.getStateCode( workerSearchRequestDTO.getAddress()));
			} else {
				try {
					GeoPoint gp = locationQueryCreationService.getGeoLocationPoint(workerSearchRequestDTO.getAddress());
					com.workmarket.search.core.model.GeoPoint geoPoint = new com.workmarket.search.core.model.GeoPoint(gp.getLatitude(), gp.getLongitude());
					builder.setGeoPoint(geoPoint);

				} catch (GeocodingException e) {
					logger.warn("failed to get geoPoint for location " + workerSearchRequestDTO.getAddress());
				}
			}
		}

		if ( workerSearchRequestDTO.getRadius() != null && ! workerSearchRequestDTO.getRadius().equals("any")) {
			try {
				Long radius = Long.valueOf( workerSearchRequestDTO.getRadius());
				BigDecimal kilometers = new BigDecimal(radius);
				kilometers = kilometers.multiply(new BigDecimal(1.609344));
				builder.setRadiusKilometers(kilometers.longValue());
			} catch (NumberFormatException e) {
				logger.warn("failed to parse radius " + workerSearchRequestDTO.getRadius());
			}
		}

		if (CollectionUtils.isNotEmpty( workerSearchRequestDTO.getCountries())) {
			builder.addCountries(convertToList( workerSearchRequestDTO.getCountries()));
		}

		builder.build().transform(new Function<LocationCriteria, FindWorkerCriteriaBuilder>() {
			@Nullable
			@Override
			public FindWorkerCriteriaBuilder apply(@Nullable LocationCriteria locationCriteria) {
				return findWorkerCriteriaBuilder.setLocationCriteria(locationCriteria);
			}
		});
	}

	private void createQualificationsCriteria() {
		QualificationsCriteriaBuilder builder = new QualificationsCriteriaBuilder();

		if (CollectionUtils.isNotEmpty(workerSearchRequestDTO.getCertifications())) {
			builder.addCertifications(convertToList(workerSearchRequestDTO.getCertifications()));
		}

		if (CollectionUtils.isNotEmpty(workerSearchRequestDTO.getLicenses())) {
			builder.addLicenses(convertToList(workerSearchRequestDTO.getLicenses()));
		}

		if (CollectionUtils.isNotEmpty(workerSearchRequestDTO.getAssessments())) {
			builder.addTests(convertToList(workerSearchRequestDTO.getAssessments()));
		}

		builder.build().transform(new Function<QualificationsCriteria, FindWorkerCriteriaBuilder>() {
			@Nullable
			@Override
			public FindWorkerCriteriaBuilder apply(@Nullable QualificationsCriteria qualificationsCriteria) {
				return findWorkerCriteriaBuilder.setQualificationsCriteria(qualificationsCriteria);
			}
		});
	}

	private List<com.workmarket.search.worker.query.model.CompanyType> convertCompanyType(final Set<CompanyType> companyTypes) {
		List<com.workmarket.search.worker.query.model.CompanyType> result = null;
		if (companyTypes != null && companyTypes.size() > 0) {
			result = Lists.newArrayList();
			for (CompanyType ct : companyTypes) {
				if (ct == CompanyType.corporation) {
					result.add(com.workmarket.search.worker.query.model.CompanyType.corp);
				} else if (ct == CompanyType.sole_proprietor) {
					result.add(com.workmarket.search.worker.query.model.CompanyType.individual);
				}
			}
		}

		return result;
	}

	private <T> List<String> convertToList(final Set<T> input) {
		List<String> result = null;
		if (input != null && input.size() > 0) {
			result = Lists.newArrayList();
			for (T item : input) {
				result.add(item.toString());
			}
		}
		return result;
	}

}
