package com.workmarket.api.v2.employer.search.worker.services;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.api.v2.employer.search.common.model.Scorecard;
import com.workmarket.api.v2.employer.search.worker.model.Verification;
import com.workmarket.api.v2.employer.search.worker.model.VerificationType;
import com.workmarket.api.v2.employer.search.worker.model.Worker;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.analytics.ResourceScoreCard;
import com.workmarket.domains.model.analytics.ResourceScoreField;
import com.workmarket.domains.model.analytics.ScoreCard;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.assessment.AssessmentUserAssociationPagination;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.dto.AddressDTO;
import com.workmarket.helpers.WMCallable;
import com.workmarket.screening.model.Screening;
import com.workmarket.screening.model.ScreeningStatusCode;
import com.workmarket.screening.model.VendorRequestCode;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CertificationService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.LicenseService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.service.web.ProfileFacadeService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.FileUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.facade.ProfileFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class WorkerHydratorImpl implements WorkerHydrator {
	private static final Logger logger = LoggerFactory.getLogger(WorkerHydratorImpl.class);

	private static final String METRIC_ROOT = "WorkerSearch";
	private static final String WORKER_HYDRATION = "workerHydration";

	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private UserService userService;
	@Autowired private ProfileFacadeService profileFacadeService;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private LaneService laneService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private CertificationService certificationService;
	@Autowired private LicenseService licenseService;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired @Qualifier("taskExecutor") private ThreadPoolTaskExecutor taskExecutor;
	@Autowired @Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate readOnlyJdbcTemplate;
	@Autowired private ScreeningService screeningService;

	private WMMetricRegistryFacade wmMetricRegistryFacade;

	private Timer hydrationTimer;
	private Timer profileHydrationTimer;
	private Timer resourceScoreCardTimer;
	private Timer companyScoreCardTimer;
	private Timer backgroundCheckTimer;
	private Timer drugTestTimer;
	private Timer avatarTimer;
	private Timer laneAssociationTimer;
	private Timer assessmentTimer;
	private Timer certificationTimer;
	private Timer licenseTimer;


	/**
	 * Constructor.
	 */
	public WorkerHydratorImpl() {
		logger.debug("New instance");
	}

	@PostConstruct
	public void init() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, METRIC_ROOT);
		hydrationTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION);
		profileHydrationTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION + ".profile");
		resourceScoreCardTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION + ".scorecard.resource");
		companyScoreCardTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION + "scorecard.company");
		backgroundCheckTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION + ".screening.background");
		drugTestTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION + ".screening.drug");
		avatarTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION + ".avatar");
		laneAssociationTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION + ".lane");
		assessmentTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION + ".assessment");
		certificationTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION + ".certification");
		licenseTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION + ".license");
	}

	/**
	 * Hydrates workers given a list of uuids.
	 *
	 * @param currentUser
	 * @param uuids
	 * @return
	 */
	@Override
	public List<Worker> hydrateWorkersByUuids(
		final ExtendedUserDetails currentUser,
		final Collection<String> uuids
	) {
		if (CollectionUtils.isNotEmpty(uuids)) {
			final Set<Long> ids = userService.findAllUserIdsByUuids(uuids);
			return hydrateWorkers(currentUser, ids);
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Hydrate workers given a list of user numbers.
	 *
	 * @param currentUser
	 * @param userNumbers
	 * @return
	 */
	@Override
	public List<Worker> hydrateWorkersByUserNumbers(
		final ExtendedUserDetails currentUser,
		final Collection<String> userNumbers
	) {
		if (CollectionUtils.isNotEmpty(userNumbers)) {
			final Set<Long> ids = userService.findAllUserIdsByUserNumbers(userNumbers);
			return hydrateWorkers(currentUser, ids);
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Hydrate workers given a list of user ids.
	 * @param currentUser
	 * @param ids
	 * @return
	 */
	private List<Worker> hydrateWorkers(
		final ExtendedUserDetails currentUser,
		final Collection<Long> ids
	) {
		Timer.Context timerContext = hydrationTimer.time();
		List<Worker> results = Lists.newArrayList();

		try {
			// no error so let's parse our results
			if (CollectionUtils.isNotEmpty(ids)) {
				final List<Long> userIds = Lists.newArrayList(ids);
				// collect profile facade data in bulk
				final Future<Map<Long, ProfileFacade>> profiles =
						taskExecutor.submit(new WMCallable<Map<Long, ProfileFacade>>(webRequestContextProvider) {
							@Override
							public Map<Long, ProfileFacade> apply() throws Exception {
								return getUserProfiles(userIds, currentUser.getId());
							}
						});

				// collect all the users to get our scorecard data
				final Future<Map<Long, ResourceScoreCard>> companyScorecards =
						taskExecutor.submit(new WMCallable<Map<Long, ResourceScoreCard>>(webRequestContextProvider) {
							@Override
							public Map<Long, ResourceScoreCard> apply() throws Exception {
								return getCompanyScoreCards(userIds, currentUser.getCompanyId());
							}
						});
				final Future<Map<Long, ResourceScoreCard>> resourceScorecards =
						taskExecutor.submit(new WMCallable<Map<Long, ResourceScoreCard>>(webRequestContextProvider) {
							@Override
							public Map<Long, ResourceScoreCard> apply() throws Exception {
								return getResourceScoreCards(userIds);
							}
						});

				// collect screening data by bulk
				final Future<Map<Long, Screening>> backgroundChecks =
						taskExecutor.submit(new WMCallable<Map<Long, Screening>>(webRequestContextProvider) {
							@Override
							public Map<Long, Screening> apply() throws Exception {
								return getLatestScreenings(userIds, VendorRequestCode.BACKGROUND);
							}
						});

				final Future<Map<Long, Screening>> drugTests =
						taskExecutor.submit(new WMCallable<Map<Long, Screening>>(webRequestContextProvider) {
							@Override
							public Map<Long, Screening> apply() throws Exception {
								return getLatestScreenings(userIds, VendorRequestCode.DRUG);
							}
						});

				// collect avatars in bulk
				final Future<Map<Long, String>> avatars =
						taskExecutor.submit(new WMCallable<Map<Long, String>>(webRequestContextProvider) {
							@Override
							public Map<Long, String> apply() throws Exception {
								return getUserAvatars(userIds);
							}
						});

				// collect lane association data in bulk
				final Future<Map<Long, LaneContext>> laneContexts =
						taskExecutor.submit(new WMCallable<Map<Long, LaneContext>>(webRequestContextProvider) {
							@Override
							public Map<Long, LaneContext> apply() throws Exception {
								//return Maps.newHashMap();
								return getUserLaneContexts(userIds, currentUser.getCompanyId());
							}
						});

				// collect assessments in bulk
				final Future<Map<Long, List<ProfileFacade.Documentation>>> assessments =
						taskExecutor.submit(new WMCallable<Map<Long, List<ProfileFacade.Documentation>>>(webRequestContextProvider) {
							@Override
							public Map<Long, List<ProfileFacade.Documentation>> apply() throws Exception {
								//return Maps.newHashMap();
								return getUserAssessments(userIds, currentUser.getCompanyId());
							}
						});

				// collect certifications in bulk
				final Future<Map<Long, List<ProfileFacade.Documentation>>> certifications =
						taskExecutor.submit(new WMCallable<Map<Long, List<ProfileFacade.Documentation>>>(webRequestContextProvider) {
							@Override
							public Map<Long, List<ProfileFacade.Documentation>> apply() throws Exception {
								//return Maps.newHashMap();
								return getUserCertifications(userIds);
							}
						});

				// collect licenses in bulk
				final Future<Map<Long, List<ProfileFacade.Documentation>>> licenses =
						taskExecutor.submit(new WMCallable<Map<Long, List<ProfileFacade.Documentation>>>(webRequestContextProvider) {
							@Override
							public Map<Long, List<ProfileFacade.Documentation>> apply() throws Exception {
								//return Maps.newHashMap();
								return getUserLicenses(userIds);
							}
						});


				for (Long workerId : userIds) {
					ProfileFacade profileFacade = profiles.get().get(workerId);
					if (profileFacade != null) {
						results.add(toWorker(
								profileFacade,
								companyScorecards.get().get(workerId),
								resourceScorecards.get().get(workerId),
								backgroundChecks.get().get(workerId),
								drugTests.get().get(workerId),
								avatars.get().get(workerId),
								laneContexts.get().get(workerId),
								assessments.get().get(workerId),
								certifications.get().get(workerId),
								licenses.get().get(workerId)));
					}
				}
			}
		} catch (ExecutionException | InterruptedException e) {
			logger.warn("execution exception {}", e.getMessage());
		} finally {
			logger.info("Worker hydration took {} ms", TimeUnit.NANOSECONDS.toMillis(timerContext.stop()));
		}
		return results;
	}




	private Worker toWorker(final ProfileFacade user,
	                        final ResourceScoreCard companyScorecard,
	                        final ResourceScoreCard userScorecard,
	                        final Screening backgroundCheck,
	                        final Screening drugTest,
	                        final String avatarUri,
	                        final LaneContext laneContext,
	                        final List<ProfileFacade.Documentation> assessments,
	                        final List<ProfileFacade.Documentation> certifications,
	                        final List<ProfileFacade.Documentation> licenses) {

		final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		final AddressDTO address = user.getAddress();

		final ScoreCard.DateIntervalData satisfactionRate = userScorecard.getValueForField(ResourceScoreField.SATISFACTION_OVER_ALL);

		final List<String> groups = convertGroupsToStringList(user.getPublicGroups());
		groups.addAll(convertGroupsToStringList(user.getPrivateGroups()));

		final List<Verification> verifications = Lists.newArrayList();
		if (backgroundCheck != null && backgroundCheck.getStatus() == ScreeningStatusCode.PASSED) {
			verifications.add(createVerification(backgroundCheck, VerificationType.background_check, "Background Check"));
		}
		if (drugTest != null && drugTest.getStatus() == ScreeningStatusCode.PASSED) {
			verifications.add(createVerification(drugTest, VerificationType.drug_test, "Drug Test"));
		}

		final Worker.Builder workerBuilder = new Worker.Builder()
			.setUuid(user.getUuid())
			.setUserNumber(user.getUserNumber())
			.setFirstName(StringUtilities.toPrettyName(user.getFirstName()))
			.setLastName(StringUtilities.toPrettyName(user.getLastName()))
			.setEmail(user.getEmail())
			.setAvatarAssetUri(avatarUri)
			.setJobTitle(user.getJobTitle())
			.setCompanyName(user.getCompanyName())
			.setCity(address == null ? StringUtils.EMPTY : StringUtilities.toPrettyName(address.getCity()))
			.setState(address == null ? StringUtils.EMPTY : address.getState())
			.setPostalCode(address == null ? StringUtils.EMPTY : address.getPostalCode())
			.setCountry(address == null ? StringUtils.EMPTY : address.getCountry())
			.setLane((laneContext != null && laneContext.getLaneType() != null) ? laneContext.getLaneType().getValue() : null)
			.setRating((satisfactionRate != null) ? BigDecimal.valueOf(satisfactionRate.getAll()).movePointRight(2).setScale(2, RoundingMode.HALF_UP).intValue() : 0)
			.setBlocked(user.getBlocked())
			.setCertifications(convertToStringList(certifications))
			.setInsurances(convertToStringList(user.getInsurance()))
			.setLicenses(convertToStringList(licenses))
			.setCompanyAssessments(convertToStringList(assessments))
			.setCompanyScorecard(createScorecard(companyScorecard))
			.setScorecard(createScorecard(userScorecard))
			.setGroups(groups)
			.setLanguages(convertToStringList(user.getLanguages()))
			.setSkills(convertToStringList(user.getSkills()))
			.setVerifications(verifications)
			.setCreatedOn(dateFormat.format(user.getCreatedOn().getTime()));

		return workerBuilder.build();
	}


	private Scorecard createScorecard(final ResourceScoreCard resourceScoreCard) {
		ScoreCard.DateIntervalData ontimeReliability = resourceScoreCard.getValueForField(ResourceScoreField.ON_TIME_PERCENTAGE);
		ScoreCard.DateIntervalData deliverableOnTimeReliability = resourceScoreCard.getValueForField(ResourceScoreField.DELIVERABLE_ON_TIME_PERCENTAGE);
		ScoreCard.DateIntervalData workCompleted = resourceScoreCard.getValueForField(ResourceScoreField.COMPLETED_WORK);
		ScoreCard.DateIntervalData abandonedWork = resourceScoreCard.getValueForField(ResourceScoreField.ABANDONED_WORK);
		ScoreCard.DateIntervalData cancelledWork = resourceScoreCard.getValueForField(ResourceScoreField.CANCELLED_WORK);
		ScoreCard.DateIntervalData satisfactionRate = resourceScoreCard.getValueForField(ResourceScoreField.SATISFACTION_OVER_ALL);

		return new Scorecard.Builder()
			.setDeliverableOnTimePercentage(deliverableOnTimeReliability != null ?
				BigDecimal.valueOf(deliverableOnTimeReliability.getAll()).movePointRight(2).setScale(2, RoundingMode.HALF_UP).intValue() :
				0)
			.setOnTimePercentage(ontimeReliability != null ?
				BigDecimal.valueOf(ontimeReliability.getAll()).movePointRight(2).setScale(2, RoundingMode.HALF_UP).intValue() :
				0)
			.setSatisfactionRate(satisfactionRate != null ?
				BigDecimal.valueOf(satisfactionRate.getAll()).movePointRight(2).setScale(2, RoundingMode.HALF_UP).intValue() :
				0)
			.setWorkCompletedCount(workCompleted != null ? workCompleted.getAll().intValue() : 0)
			.setWorkAbandonedCount(abandonedWork != null ? abandonedWork.getAll().intValue() : 0)
			.setWorkCancelledCount(cancelledWork != null ? cancelledWork.getAll().intValue() : 0)
			.build();
	}

	private List<String> convertToStringList(final List<ProfileFacade.Documentation> docs) {
		List<String> result = Lists.newArrayList();
		if (docs != null) {
			for (ProfileFacade.Documentation doc : docs) {
				result.add(doc.getName());
			}
		}
		return result;
	}

	private List<String> convertGroupsToStringList(final List<Map> groups) {
		List<String> result = Lists.newArrayList();
		if (groups != null) {
			for (Map group : groups) {
				result.add((String)group.get("name"));
			}
		}
		return result;
	}

	/**
	 * Gets scorecards for a list of users
	 * @param userIds a list of user ids
	 * @return map of user id and its scorecard
	 */
	private Map<Long, ResourceScoreCard> getResourceScoreCards(final List<Long> userIds) {
		Timer.Context context = resourceScoreCardTimer.time();
		try {
			Map<Long, ResourceScoreCard> resourceScoreCardMap = analyticsService.getResourceScoreCards(userIds);
			return resourceScoreCardMap;
		} catch (Throwable t) {
			logger.error("Failed to retrieve the resource scorecard.", t);
			return Maps.newHashMap();
		} finally {
			logger.info("Retrieving resource scorecard took {} ms", TimeUnit.NANOSECONDS.toMillis(context.stop()));
		}

	}

	/**
	 * Gets company scorecards for a list of users
	 * @param userIds   a list of user ids
	 * @param companyId the company id
	 * @return map of user id and it scorecard
	 */
	private Map<Long, ResourceScoreCard> getCompanyScoreCards(final List<Long> userIds, final Long companyId) {
		Timer.Context context = companyScoreCardTimer.time();
		try {
			Map<Long, ResourceScoreCard> companyScoreCardMap = analyticsService
				.getResourceScoreCardsForCompany(companyId, userIds);
			return companyScoreCardMap;
		} catch (Throwable t) {
			logger.error("Failed to retrieve the company scorecard.", t);
			return Maps.newHashMap();
		} finally {
			logger.info("Retrieving company scorecard took {} ms", TimeUnit.NANOSECONDS.toMillis(context.stop()));
		}
	}

	/**
	 * Gets latest screenings for a list of user ids.
	 * @param userIds         a list of user ids
	 * @param screeningType   the screening type
	 * @return map of user id and its latest screening
	 */
	private Map<Long, Screening> getLatestScreenings(
		final List<Long> userIds,
		final VendorRequestCode screeningType
	) {
		Timer.Context context;
		if (screeningType == VendorRequestCode.BACKGROUND) {
			context = backgroundCheckTimer.time();
		} else {
			context = drugTestTimer.time();
		}

		final Map<Long, Screening> result = Maps.newHashMap();
		final List<Screening> screenings =
			screeningService.findMostRecentScreeningsByUserIds(userIds, screeningType, false);

		for (final Screening screening : screenings) {
			Long userId = Long.parseLong(screening.getUserId());
			if (!result.containsKey(userId)) {
				result.put(userId, screening);
			}
		}

		logger.info("Retrieving {} screening took {}", screeningType.toString(), TimeUnit.NANOSECONDS.toMillis(context.stop()));
		return result;
	}

	/**
	 * Gets lane associations (with searcher's company) for a list of user ids.
	 * @param userIds         a list of user ids
	 * @param companyId       searcher's company id
	 * @return map of user id and lane relationships with this company
	 */
	private Map<Long, LaneContext> getUserLaneContexts(final List<Long> userIds, final Long companyId) {
		Timer.Context context;
		context = laneAssociationTimer.time();

		Map<Long, LaneContext> laneContextMap = new HashMap<>(userIds.size());
		try {

			Set<LaneAssociation> laneAssociationSet =
				laneService.findAllAssociationsWhereUserIdIn(companyId, new HashSet<>(userIds));

			for (LaneAssociation laneAssociation : laneAssociationSet) {
				laneContextMap.put(
					laneAssociation.getUser().getId(),
					new LaneContext(laneAssociation.getLaneType(), laneAssociation.getApprovalStatus()));
			}
		} catch (Throwable t) {
			logger.error("Failed to retrieve the user lane contexts.", t);
		}

		logger.info("Retrieving lane associations took {} ms", TimeUnit.NANOSECONDS.toMillis(context.stop()));


		return laneContextMap;
	}

	/**
	 * Gets assessments for a list of user ids.
	 * @param userIds         a list of user ids
	 * @param companyId       searcher's company id
	 * @return map of user id and assessments
	 */
	private Map<Long, List<ProfileFacade.Documentation>> getUserAssessments(final List<Long> userIds,
	                                                                        final Long companyId) {
		Timer.Context context;
		context = assessmentTimer.time();

		Map<Long, List<ProfileFacade.Documentation>> assessmentMap = new HashMap<>(userIds.size());

		try {
			List<AssessmentUserAssociation> assessments = Lists.newArrayList();
			AssessmentUserAssociationPagination assessmentUserAssociationPagination =
				new AssessmentUserAssociationPagination(true);

			assessmentUserAssociationPagination.setFilters(ImmutableMap.of(
				AssessmentUserAssociationPagination.FILTER_KEYS.COMPANY_ID.toString(), companyId.toString(),
				AssessmentUserAssociationPagination.FILTER_KEYS.COMPLETED_FLAG.name(), "true",
				AssessmentUserAssociationPagination.FILTER_KEYS.PASSED_FLAG.name(), "true"));

			assessmentUserAssociationPagination =
				assessmentService.findAssessmentUserAssociationsByUsers(
					new HashSet<>(userIds),
					assessmentUserAssociationPagination);

			if (assessmentUserAssociationPagination != null && assessmentUserAssociationPagination.getResults() != null) {
				assessments = assessmentUserAssociationPagination.getResults();
			}

			for (AssessmentUserAssociation a : assessments) {
				if (AbstractAssessment.SURVEY_ASSESSMENT_TYPE.equals(a.getAssessment().getType())) continue;

				ProfileFacade.Documentation d = new ProfileFacade.Documentation(
					a.getAssessment().getId(),
					a.getAssessment().getName(),
					a.getAssessment().getDescription(),
					a.getScore() != null ? a.getScore().toString() : ""
				);
				// NOTES: we don't set additional fields because search card needs only passed assessment name
				//d.setSecondaryId(CollectionUtilities.last(a.getAttempts()).getId());
				//d.setVerficationStatus(a.getPassedFlag() ? VerificationStatus.VERIFIED.name() : VerificationStatus.FAILED.name());
				//d.setCreatedOn(a.getCompletedOn().getTime());

				// Add assessment to map for userId
				Long userId = a.getUser().getId();
				if (!assessmentMap.containsKey(userId)) {
					assessmentMap.put(userId, new ArrayList<ProfileFacade.Documentation>());
				}
				assessmentMap.get(userId).add(d);

			}
		} catch (Throwable t) {
			logger.error("Failed to retrieve the user assessments.", t);
		}
		logger.info("Retrieving assessments took {} ms", TimeUnit.NANOSECONDS.toMillis(context.stop()));


		return assessmentMap;
	}

	private Map<Long,List<ProfileFacade.Documentation>> getUserCertifications(List<Long> userIds) {
		Timer.Context context = certificationTimer.time();

		Map<Long, List<ProfileFacade.Documentation>> certificationMap = new HashMap<>(userIds.size());

		try {
			List<UserCertificationAssociation> userCertifications = Lists.newArrayList();
			UserCertificationAssociationPagination certificationAssociationPagination =
				new UserCertificationAssociationPagination(true);

			certificationAssociationPagination =
				certificationService.findAllVerifiedCertificationsByUserIds(
					new HashSet<>(userIds),
					certificationAssociationPagination);

			if (certificationAssociationPagination != null && certificationAssociationPagination.getResults() != null) {
				userCertifications = certificationAssociationPagination.getResults();
			}

			for (UserCertificationAssociation c : userCertifications) {
				ProfileFacade.Documentation d = new ProfileFacade.Documentation(
					c.getCertification().getId(),
					c.getCertification().getName(),
					c.getCertification().getCertificationVendor().getName(),
					null
				);
				d.setVerficationStatus(c.getVerificationStatus().name());

				// Add assessment to map for userId
				Long userId = c.getUser().getId();
				if (!certificationMap.containsKey(userId)) {
					certificationMap.put(userId, new ArrayList<ProfileFacade.Documentation>());
				}
				certificationMap.get(userId).add(d);

			}
		} catch (Throwable t) {
			logger.error("Failed to retrieve the user certifications.", t);
		}
		logger.info("Retrieving certifications took {} ms", TimeUnit.NANOSECONDS.toMillis(context.stop()));

		return certificationMap;
	}

	private Map<Long,List<ProfileFacade.Documentation>> getUserLicenses(List<Long> userIds) {
		Timer.Context context = licenseTimer.time();

		Map<Long, List<ProfileFacade.Documentation>> licenseMap = new HashMap<>(userIds.size());

		try {
			List<UserLicenseAssociation> userLicenses = Lists.newArrayList();
			UserLicenseAssociationPagination licenseAssociationPagination = new UserLicenseAssociationPagination(true);
			licenseAssociationPagination =
				licenseService.findAllVerifiedAssociationsByUserIds(
					new HashSet(userIds),
					licenseAssociationPagination);

			if (licenseAssociationPagination != null && licenseAssociationPagination.getResults() != null) {
				userLicenses = licenseAssociationPagination.getResults();
			}

			for (UserLicenseAssociation l : userLicenses) {
				ProfileFacade.Documentation d = new ProfileFacade.Documentation(
					l.getLicense().getId(),
					l.getLicense().getName(),
					l.getLicense().getState(),
					null
				);
				d.setVerficationStatus(l.getVerificationStatus().name());

				// Add assessment to map for userId
				Long userId = l.getUser().getId();
				if (!licenseMap.containsKey(userId)) {
					licenseMap.put(userId, new ArrayList<ProfileFacade.Documentation>());
				}
				licenseMap.get(userId).add(d);

			}
		} catch (Throwable t) {
			logger.error("Failed to retrieve the user license.", t);
		}
		logger.info("Retrieving licenses took {} ms", TimeUnit.NANOSECONDS.toMillis(context.stop()));

		return licenseMap;
	}


		/**
		 * Gets the profiles for the given list of workers
		 * @param userIds a list of user ids
		 * @return map of user id and its profile
		 */
	private Map<Long, ProfileFacade> getUserProfiles(final List<Long> userIds, final Long currentUserId) {
		Timer.Context context = profileHydrationTimer.time();
		Map<Long, ProfileFacade> result = Maps.newHashMap();

		try {
			List<ProfileFacade> profileFacades = profileFacadeService.findSearchCardProfileFacadeByUserIds(userIds, currentUserId);
			for (ProfileFacade profileFacade : profileFacades) {
				result.put(profileFacade.getId(), profileFacade);
			}
		} catch (Exception e) {
			logger.warn("failed to get user profiles " + e.getMessage());
		} finally {
			logger.info("Retrieving user profiles took {} ms", TimeUnit.NANOSECONDS.toMillis(context.stop()));
		}
		return result;
	}

	/**
	 * Gets user avatar
	 * @param userIds a list of user ids
	 * @return map of user id to avatar url
	 */
	private Map<Long, String> getUserAvatars(final List<Long> userIds) {
		Timer.Context context = avatarTimer.time();
		Map<Long, String> result = Maps.newHashMap();

		final String AVATAR_SQL =
				"select ua.user_id, COALESCE(a.cdn_uri, a.remote_uri) as avatar_uri, a.uuid, COALESCE(acu.cdn_uri_prefix, aru.remote_uri_prefix) AS avatar_prefix from user_asset_association ua \n" +
				"left join asset a on ua.transformed_small_asset_id = a.id \n" +
				"left join asset_remote_uri aru on aru.id = a.asset_remote_uri_id \n" +
				"left join asset_cdn_uri acu on acu.id = a.asset_cdn_uri_id \n" +
				"where ua.user_id in (:userIds) \n" +
				"and ua.approval_status = 1 \n" +
				"and ua.active = 1 \n" +
				"and ua.deleted = 0 \n" +
				"and ua.asset_type_code = 'avatar' \n" +
				"order by ua.created_on desc";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userIds", userIds);

		try {
			List<Map<String, Object>> avatars = readOnlyJdbcTemplate.queryForList(AVATAR_SQL, params);
			for (Map<String, Object> avatar : avatars) {
				Long userId = ((Integer) avatar.get("user_id")).longValue();
				if (!result.containsKey(userId)) {
					final String avatarUri = (String) avatar.get("cdn_uri");
					final String avatarUUID = (String) avatar.get("uuid");
					final String avatarPrefix = (String) avatar.get("avatar_prefix");
					if ( !StringUtils.isEmpty(avatarUri)) {
						result.put(userId, avatarUri);
					} else if (avatarUUID != null && avatarPrefix != null) {
						result.put(userId, FileUtilities.createRemoteFileandDirectoryStructor(avatarPrefix, avatarUUID));
					}
				}
			}
		} catch (Throwable t) {
			logger.error("Failed to retrieve the user avatars.", t);
		} finally {
			logger.info("Retrieving user avatars took {} ms", TimeUnit.NANOSECONDS.toMillis(context.stop()));
		}
		return result;
	}

	private Verification createVerification(final Screening screening, final VerificationType type, final String verificationName) {
		final DateTime screeningDate =
			screening.getVendorResponseDate() != null ? screening.getVendorResponseDate() : screening.getModifiedOn();

		return new Verification.Builder()
			.setId(type.name())
			.setName(verificationName)
			.setCheckDate(screeningDate.toLocalDate().toString())
			.build();
	}
}
