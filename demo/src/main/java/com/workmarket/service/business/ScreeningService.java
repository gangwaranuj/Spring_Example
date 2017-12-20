package com.workmarket.service.business;

import com.codahale.metrics.MetricRegistry;
import com.google.api.client.util.Sets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.workmarket.ScreeningServiceClient;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.model.screening.ScreenedUser;
import com.workmarket.domains.model.screening.ScreenedUserPagination;
import com.workmarket.domains.model.screening.ScreeningObjectConverter;
import com.workmarket.domains.model.screening.ScreeningPagination;
import com.workmarket.domains.model.screening.ScreeningStatusType;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.helpers.WMCallable;
import com.workmarket.reporting.model.EvidenceReport;
import com.workmarket.screening.dto.Address;
import com.workmarket.screening.dto.ScreeningSearchRequest;
import com.workmarket.screening.dto.ScreeningSearchRequestBuilder;
import com.workmarket.screening.dto.SterlingScreeningRequest;
import com.workmarket.screening.dto.SterlingScreeningRequestBuilder;
import com.workmarket.screening.model.Direction;
import com.workmarket.screening.model.Gender;
import com.workmarket.screening.model.OrderField;
import com.workmarket.screening.model.Screening;
import com.workmarket.screening.model.ScreeningStatusCode;
import com.workmarket.screening.model.VendorRequestCode;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.ScreeningDTO;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.screening.ExternalScreeningService;
import com.workmarket.service.business.screening.ScreeningAndUser;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.vo.ScreeningSearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import rx.Observable;
import rx.functions.Func1;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

@Service("backgroundCheckService")
public class ScreeningService {

	private static final String SCREENING_INDEXER_ENABLED = "ScreeningIndexerEnabled";

	@Autowired @Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterServiceNetMoneyImpl;
	@Autowired @Qualifier("externalScreeningService") private ExternalScreeningService externalScreeningService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private ProfileService profileService;
	@Autowired private UserDAO userDAO;
	@Autowired private EventRouter eventRouter;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	private ScreeningServiceClient uServiceClient;

	private static final Log logger = LogFactory.getLog(ScreeningService.class);

	ScreeningService() {
	}

	@PostConstruct
	public void postConstruct() {
		uServiceClient = new ScreeningServiceClient();
	}

	// Background Check
	public Screening requestBackgroundCheck(Long userId, ScreeningDTO dto) throws Exception {
		Assert.notNull(userId);
		Assert.isTrue(validateBackgroundCheck(dto));

		BackgroundCheck backgroundCheck = buildBackgroundCheck(userId);
		boolean paid = accountRegisterServiceNetMoneyImpl.payForBackgroundCheckUsingBalance(userId, backgroundCheck,
				dto.getCountry());
		if (paid) {
			return runRequestBackgroundCheck(userId, dto, backgroundCheck);
		}
		return null;
	}


	public Screening requestBackgroundCheck(final Long userId,
											final ScreeningDTO dto,
											final PaymentDTO paymentDTO) throws Exception {
		Assert.notNull(userId);
		Assert.notNull(paymentDTO);
		Assert.isTrue(validateBackgroundCheck(dto));

		final BackgroundCheck backgroundCheck = buildBackgroundCheck(userId);
		boolean paid = accountRegisterServiceNetMoneyImpl.payForBackgroundCheckUsingCreditCard(userId,
				backgroundCheck, paymentDTO, dto.getCountry());

		if (paid) {
			return runRequestBackgroundCheck(userId, dto, backgroundCheck);
		}
		return null;
	}

	private Screening runRequestBackgroundCheck(Long userId, ScreeningDTO dto, BackgroundCheck backgroundCheck) {
		final Screening result = uServiceClient.save(buildScreeningRequest(backgroundCheck, dto),
				webRequestContextProvider.getRequestContext()).toBlocking().single();
		if (!featureEvaluator.hasGlobalFeature(SCREENING_INDEXER_ENABLED)) {
			eventRouter.sendEvent(new UserSearchIndexEvent(userId));
		}
		return result;
	}

	private Screening runRequestDrugTest(final DrugTest drugTest, final ScreeningDTO dto) {

		logger.debug("vendorId=" + drugTest.getScreeningId() + "; userUuid=" + drugTest.getUser().getUuid()
				+ "; status=" + drugTest.getScreeningStatusType());
		final Screening result = uServiceClient.save(buildScreeningRequest(drugTest, dto),
				webRequestContextProvider.getRequestContext()).toBlocking().single();
		if (!featureEvaluator.hasGlobalFeature(SCREENING_INDEXER_ENABLED)) {
			eventRouter.sendEvent(new UserSearchIndexEvent(drugTest.getUser().getId()));
		}

		return result;
	}

	public Screening requestFreeBackgroundCheck(Long userId, ScreeningDTO dto) throws Exception {
		Assert.notNull(userId);
		Assert.isTrue(validateBackgroundCheck(dto));

		BackgroundCheck backgroundCheck = buildBackgroundCheck(userId);
		return runRequestBackgroundCheck(userId, dto, backgroundCheck);
	}

	private boolean validateBackgroundCheck(ScreeningDTO dto) {
		Assert.notNull(dto);
		Assert.notNull(dto.getCountry());
		Assert.state(CollectionUtilities.containsAny(dto.getCountry(), Country.USA, Country.CANADA), "Country not " +
				"supported for drug test.");
		return true;
	}

	private BackgroundCheck buildBackgroundCheck(Long userId) {
		Assert.notNull(userId);

		User user = userDAO.get(userId);

		BackgroundCheck backgroundCheck = new BackgroundCheck();

		backgroundCheck.setUser(user);
		backgroundCheck.setScreeningStatusType(new ScreeningStatusType(ScreeningStatusType.REQUESTED));
		backgroundCheck.setRequestDate(Calendar.getInstance());
		backgroundCheck.setScreeningId(UUID.randomUUID().toString());

		return backgroundCheck;
	}

	public Screening findMostRecentBackgroundCheck(final Long userId) {
		Assert.notNull(userId);
		return uServiceClient.search(ScreeningSearchRequest.builder()
				.setUserIds(listOf(userId))
				.setVendorRequestCode(VendorRequestCode.BACKGROUND)
				.setLimit(1)
				.build(), webRequestContextProvider.getRequestContext())
				.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
					@Override
					public Iterable<Screening> call(final ScreeningSearchResponse resp) {
						return resp.getResults();
					}
				})
				.toBlocking().singleOrDefault(null);
	}


	public Screening findPreviousPassedBackgroundCheck(final Long userId) {
		Assert.notNull(userId);

		return uServiceClient.search(ScreeningSearchRequest.builder()
				.setUserIds(listOf(userId))
				.setVendorRequestCode(VendorRequestCode.BACKGROUND)
				.setScreeningStatusCode(ScreeningStatusCode.PASSED)
				.setDirection(Direction.ASC)
				.setLimit(1)
				.build(), webRequestContextProvider.getRequestContext())
				.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
					@Override
					public Iterable<Screening> call(final ScreeningSearchResponse resp) {
						return resp.getResults();
					}
				})
				.toBlocking().single();
	}


	public List<Screening> findBackgroundChecksByUser(final Long userId) {
		Assert.notNull(userId);
		return ImmutableList.copyOf(uServiceClient.search(ScreeningSearchRequest.builder()
				.setVendorRequestCode(VendorRequestCode.BACKGROUND)
				.setUserIds(listOf(userId))
				.build(), webRequestContextProvider.getRequestContext())
				.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
					@Override
					public Iterable<Screening> call(final ScreeningSearchResponse resp) {
						return resp.getResults();
					}
				}).toBlocking().toIterable());
	}

	public List<Screening> findMostRecentScreeningsByUserIds(
		final Collection<Long> userIds,
		final VendorRequestCode screeningType,
		final boolean findPassedStatusOnly
	) {
		if (CollectionUtils.isEmpty(userIds)) {
			return Collections.emptyList();
		}

		final ScreeningSearchRequestBuilder requestBuilder = ScreeningSearchRequest.builder()
			.setVendorRequestCode(screeningType)
			.setLimit(userIds.size() * 2) // in case every user has more than one successful screenings
			.setUserIds(convertIdsToStrings(Lists.newArrayList(userIds)))
			.setDirection(Direction.DESC)
			.setOrderField(OrderField.REQUEST_DATE);

		if (findPassedStatusOnly) {
			requestBuilder.setScreeningStatusCode(ScreeningStatusCode.PASSED);
		}

		final ScreeningSearchResponse resp = uServiceClient
			.search(requestBuilder.build(), webRequestContextProvider.getRequestContext())
			.toBlocking().singleOrDefault(null);
		return resp != null ? resp.getResults() : Collections.<Screening>emptyList();
	}

	public List<EvidenceReport> findBulkMostRecentEvidenceReport(List<Long> userIds, String screeningType) {
		Assert.notNull(userIds);
		return runFindBulkMostRecentEvidenceReportTrial(userIds, screeningType);
	}

	private List<EvidenceReport> runFindBulkMostRecentEvidenceReportTrial(final List<Long> userIds,
																		  final String screeningType) {
		return ImmutableList.copyOf(uServiceClient.search(ScreeningSearchRequest.builder()
				.setLimit(userIds.size())
				.setUserIds(convertIdsToStrings(userIds))
				.setVendorRequestCode(VendorRequestCode.getStatusByCode(screeningType))
				.setScreeningStatusCode(ScreeningStatusCode.PASSED)
				.setDirection(Direction.DESC)
				.setOrderField(OrderField.REQUEST_DATE)
				.build(), webRequestContextProvider.getRequestContext())
				.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
					@Override
					public Iterable<Screening> call(final ScreeningSearchResponse resp) {
						return resp.getResults();
					}
				})
				.map(new Func1<Screening, EvidenceReport>() {
					@Override
					public EvidenceReport call(final Screening screening) {
						final Long userId = Long.valueOf(screening.getUserId());
						final User u = userDAO.getUser(userId);
						final EvidenceReport evidenceReport = new EvidenceReport();
						evidenceReport.setRequestDate(screening.getCreatedOn().toGregorianCalendar());
						evidenceReport.setResponseDate(screening.getVendorResponseDate().toGregorianCalendar());
						evidenceReport.setUserId(userId);
						evidenceReport.setFirstName(u.getFirstName());
						evidenceReport.setLastName(u.getLastName());
						evidenceReport.setCompanyId(u.getCompany().getId());
						evidenceReport.setCompanyName(u.getCompany().getName());
						return evidenceReport;
					}
				})
				.toBlocking().toIterable());
	}

	private List<String> convertIdsToStrings(final List<Long> userIds) {
		final List<String> userIdStrings = Lists.newArrayList();
		for (final Long id : userIds) {
			userIdStrings.add(id.toString());
		}
		return userIdStrings;
	}

	public List<ScreeningAndUser> findBackgroundChecksByStatus(
			final String status, final ScreeningPagination pagination) throws Exception {
		Assert.notNull(status);
		Assert.notNull(pagination);


		final Callable<Observable<Screening>> experiment = new WMCallable<Observable<Screening>>(webRequestContextProvider) {
			@Override
			public Observable<Screening> apply() throws Exception {
				return uServiceClient.search(ScreeningSearchRequest.builder()
						.setVendorRequestCode(VendorRequestCode.BACKGROUND)
						.setScreeningStatusCode(ScreeningStatusCode.getStatusByCode(status))
						.setOffset(pagination.getStartRow())
						.setLimit(pagination.getResultsLimit())
						.setDirection(Direction.ASC)
						.build(), webRequestContextProvider.getRequestContext())
						.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
							@Override
							public Iterable<Screening> call(final ScreeningSearchResponse resp) {
								return resp.getResults();
							}
						});
			}
		};

		return mapScreeningAndUsers(experiment).call().toList().toBlocking().single();
	}


	public Screening updateScreeningStatus(String screeningUuid, String status) throws Exception {
		Assert.notNull(screeningUuid);
		Assert.notNull(status);

		final ScreeningSearchResponse result = uServiceClient.search(new ScreeningSearchRequestBuilder()
				.setUuids(ImmutableList.of(screeningUuid))
				.build(), webRequestContextProvider.getRequestContext()).toBlocking().single();

		if (result.getResults().size() == 0) {
			return null;
		}

		final Screening screening = result.getResults().get(0);
		logger.debug("vendorId=" + screening.getVendorRequestId() + "; userUuid=" + screening.getUserId()
				+ "; status=" + screening.getStatus());

		final DateTime now = DateTime.now();

		final SterlingScreeningRequestBuilder builder = SterlingScreeningRequest.builder();
		if (!ScreeningStatusType.EXPIRED.equals(status)) {
			builder.setVendorResponseDate(now);
		}

		return uServiceClient.save(builder
				.setVendorRequestCode(screening.getVendorRequestCode())
				.setScreeningUuid(screening.getUuid())
				.setScreeningStatusCode(ScreeningStatusCode.getStatusByCode(status))
				.setUserId(screening.getUserId())
				.setCallExternal(false) // just updating the status, no need to call out to Sterling
				.build(), webRequestContextProvider.getRequestContext())
				.toBlocking().single();
	}

	// Drug Test


	public Screening requestDrugTest(Long userId, ScreeningDTO dto) throws Exception {
		Assert.notNull(userId);
		Assert.isTrue(validateDrugTest(dto));

		DrugTest drugTest = buildDrugTest(userId);
		accountRegisterServiceNetMoneyImpl.payForDrugTestUsingBalance(userId, drugTest);
		Screening result = runRequestDrugTest(drugTest, dto);

		User u = userDAO.get(userId);
		ScreeningAndUser screeningAndUser = new ScreeningAndUser(
				ScreeningObjectConverter.convertScreeningFromLegacy(drugTest), u);
		userNotificationService.onDrugTestRequest(screeningAndUser);

		return result;
	}


	public Screening requestDrugTest(Long userId, ScreeningDTO dto, PaymentDTO paymentDTO) throws Exception {
		Assert.notNull(userId);
		Assert.notNull(paymentDTO);
		Assert.isTrue(validateDrugTest(dto));

		DrugTest drugTest = buildDrugTest(userId);
		boolean paid = accountRegisterServiceNetMoneyImpl.payForDrugTestUsingCreditCard(userId, drugTest, paymentDTO);

		if (paid) {
			return runRequestDrugTest(drugTest, dto);
		}

		return null;
	}


	public Screening requestFreeDrugTest(Long userId, ScreeningDTO dto) throws Exception {
		Assert.notNull(userId);
		Assert.isTrue(validateDrugTest(dto));

		DrugTest drugTest = buildDrugTest(userId);
		return runRequestDrugTest(drugTest, dto);
	}

	private DrugTest buildDrugTest(Long userId) throws Exception {
		Assert.notNull(userId);

		User user = userDAO.get(userId);

		DrugTest drugTest = new DrugTest();

		drugTest.setUser(user);
		drugTest.setScreeningStatusType(new ScreeningStatusType(ScreeningStatusType.REQUESTED));
		drugTest.setRequestDate(Calendar.getInstance());
		drugTest.setScreeningId(UUID.randomUUID().toString());

		return drugTest;
	}

	private boolean validateDrugTest(ScreeningDTO dto) {
		Assert.notNull(dto);
		Assert.notNull(dto.getCountry());
		Assert.state(CollectionUtilities.containsAny(dto.getCountry(), Country.USA),
				"Country not supported for drug test.");
		return true;
	}


	public Screening findMostRecentDrugTest(final Long userId) {
		Assert.notNull(userId);

		return uServiceClient.search(ScreeningSearchRequest.builder()
				.setVendorRequestCode(VendorRequestCode.DRUG)
				.setUserIds(listOf(userId))
				.setLimit(1)
				.build(), webRequestContextProvider.getRequestContext())
				.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
					@Override
					public Iterable<Screening> call(final ScreeningSearchResponse resp) {
						return resp.getResults();
					}
				}).toBlocking().singleOrDefault(null);
	}


	public Screening findPreviousPassedDrugTest(final Long userId) {
		Assert.notNull(userId);

		return uServiceClient.search(ScreeningSearchRequest.builder()
				.setVendorRequestCode(VendorRequestCode.DRUG)
				.setScreeningStatusCode(ScreeningStatusCode.PASSED)
				.setDirection(Direction.ASC)
				.setUserIds(listOf(userId))
				.setLimit(1)
				.build(), webRequestContextProvider.getRequestContext())
				.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
					@Override
					public Iterable<Screening> call(final ScreeningSearchResponse resp) {
						return resp.getResults();
					}
				}).toBlocking().single();

	}


	public List<Screening> findDrugTestsByUser(final Long userId) {
		Assert.notNull(userId);

		return uServiceClient.search(ScreeningSearchRequest.builder()
				.setVendorRequestCode(VendorRequestCode.DRUG)
				.setUserIds(listOf(userId))
				.build(), webRequestContextProvider.getRequestContext())
				.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
					@Override
					public Iterable<Screening> call(final ScreeningSearchResponse resp) {
						return resp.getResults();
					}
				}).toList().toBlocking().single();
	}


	public boolean hasProfileVideo(Long userId) {
		Assert.notNull(userId);
		Integer count = profileService.findAllUserProfileVideoAssociations(userId).size();
		count += (profileService.findAllUserProfileEmbedVideoAssociations(userId)).size();

		return count > 0;
	}


	public boolean hasProfilePicture(Long userId) {
		Assert.notNull(userId);
		Integer count = profileService.findAllUserProfileAndAvatarImageAssociations(userId).size();

		return count > 0;
	}


	public boolean hasPassedBackgroundCheck(final Long userId) {
		Assert.notNull(userId);

		return uServiceClient.search(ScreeningSearchRequest.builder()
				.setUserIds(listOf(userId))
				.setVendorRequestCode(VendorRequestCode.BACKGROUND)
				.setScreeningStatusCode(ScreeningStatusCode.PASSED)
				.setDirection(Direction.DESC)
				.setOrderField(OrderField.REQUEST_DATE)
				.build(), webRequestContextProvider.getRequestContext())
				.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
					@Override
					public Iterable<Screening> call(final ScreeningSearchResponse resp) {
						return resp.getResults();
					}
				}).count().toBlocking().single() > 0;
	}


	public boolean hasPassedDrugTest(final Long userId) {
		Assert.notNull(userId);

		return uServiceClient.search(ScreeningSearchRequest.builder()
				.setVendorRequestCode(VendorRequestCode.DRUG)
				.setUserIds(listOf(userId))
				.setScreeningStatusCode(ScreeningStatusCode.PASSED)
				.build(), webRequestContextProvider.getRequestContext())
				.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
					@Override
					public Iterable<Screening> call(final ScreeningSearchResponse resp) {
						return resp.getResults();
					}
				}).count().toBlocking().single() > 0;
	}

	private List<String> listOf(final Long userId) {
		return ImmutableList.of(userId.toString());
	}

	private Callable<Observable<ScreeningAndUser>> mapScreeningAndUsers(
			final Callable<Observable<Screening>> experiment) {

		return new WMCallable<Observable<ScreeningAndUser>>(webRequestContextProvider) {
			public Observable<ScreeningAndUser> apply() throws Exception {
				final ImmutableList.Builder<String> uuids = ImmutableList.builder();
				final ImmutableList.Builder<Long> userIds = ImmutableList.builder();
				final ImmutableList<Screening> screenings =
						ImmutableList.copyOf(experiment.call().toBlocking().toIterable());
				for (final Screening screening : screenings) {
					uuids.add(screening.getUuid());
					userIds.add(Long.valueOf(screening.getUserId()));
				}
				final List<ScreeningAndUser> results = new ArrayList<>();

				final Map<String, User> map = getIndexedUsers(userIds.build());
				for (final Screening s : screenings) {
					final User user = map.get(s.getUserId());
					results.add(new ScreeningAndUser(s, user));
				}

				return Observable.from(results);
			}
		};
	}

	public List<ScreeningAndUser> findDrugTestsByStatus(
			final String status, final ScreeningPagination pagination) throws Exception {
		Assert.notNull(status);
		Assert.notNull(pagination);


		final Callable<Observable<Screening>> experiment = new WMCallable<Observable<Screening>>(webRequestContextProvider) {
			@Override
			public Observable<Screening> apply() throws Exception {
				return uServiceClient.search(ScreeningSearchRequest.builder()
						.setVendorRequestCode(VendorRequestCode.DRUG)
						.setScreeningStatusCode(ScreeningStatusCode.getStatusByCode(status))
						.setOffset(pagination.getStartRow())
						.setLimit(pagination.getResultsLimit())
						.setDirection(Direction.DESC)
						.build(), webRequestContextProvider.getRequestContext())
						.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
							@Override
							public Iterable<Screening> call(final ScreeningSearchResponse resp) {
								return resp.getResults();
							}
						});
			}
		};

		return  mapScreeningAndUsers(experiment).call().toList().toBlocking().single();
	}

	// Screening Results


	public Boolean handleSterlingScreeningResults(String results) throws Exception {
		Assert.notNull(results);

		// Sterling Vendor ID is the UUid we assign to the request
		String screeningId = externalScreeningService.getVendorId(results);

		Screening screening = findByScreeningId(screeningId);

		if (screening == null) {
			return false;
		}

		final ScreeningStatusCode status = externalScreeningService.getScreeningStatus(results);

		logger.debug("screeningId=" + screeningId + "; dbStatus=" + screening.getStatus().code() + "; payloadStatus=" + status.code());

		if (status.equals(screening.getStatus())) {
			return true;
		}

		updateScreeningStatus(screening.getUuid(), status.code());

		return ScreeningStatusCode.PASSED.equals(status);
	}



	public void updateUserScreeningStatus(Long userId) {
		logger.debug("***** updateUserScreeningStatus(Long userId) *****");

		User user = userDAO.get(userId);
		Screening backgroundCheck = findMostRecentBackgroundCheck(userId);
		Screening drugTest = findMostRecentDrugTest(userId);

		ScreeningStatusCode backgroundCheckStatus = ScreeningStatusCode.NOREQUEST;
		ScreeningStatusCode drugTestStatus = ScreeningStatusCode.NOREQUEST;
		String userScreeningStatus = ScreeningStatusType.NOT_REQUESTED;

		if (drugTest != null) {
			drugTestStatus = drugTest.getStatus();
		}

		if (backgroundCheck != null) {
			backgroundCheckStatus = backgroundCheck.getStatus();
		}


		if (backgroundCheckStatus == ScreeningStatusCode.FAILED
				|| drugTestStatus == ScreeningStatusCode.FAILED) {
			userScreeningStatus = ScreeningStatusType.FAILED;
		} else if (backgroundCheckStatus == ScreeningStatusCode.PASSED
				|| drugTestStatus == ScreeningStatusCode.PASSED) {
			userScreeningStatus = ScreeningStatusType.PASSED;
		} else if (backgroundCheckStatus == ScreeningStatusCode.REQUESTED
				|| drugTestStatus == ScreeningStatusCode.REQUESTED) {
			userScreeningStatus = ScreeningStatusType.REQUESTED;
		}

		user.setScreeningStatusType(new ScreeningStatusType(userScreeningStatus));

		if (userId == null) {
			logger.debug("Trying to reindex null user for update screening pass");
		}

		// if our screening indexer is enabled then skip this event, we'll get this data
		// from Kafka
		if (!featureEvaluator.hasGlobalFeature(SCREENING_INDEXER_ENABLED)) {
			eventRouter.sendEvent(new UserSearchIndexEvent(userId));
		}
	}


	public ScreenedUserPagination findAllScreenedUsers(ScreenedUserPagination pagination) throws Exception {
		Assert.notNull(pagination);
		return runFindAllScreenedUsersTrial(pagination);
	}

	private ScreenedUserPagination runFindAllScreenedUsersTrial(final ScreenedUserPagination pagination) throws Exception {
		final ScreeningSearchRequestBuilder builder = ScreeningSearchRequest.builder();
		if (pagination.getResultsLimit() == null) {
			builder.setLimit(pagination.getResultsLimit());
		} else {
			builder.setLimit(500);
		}
		if (pagination.getStartRow() != null) {
			builder.setOffset(pagination.getStartRow());
		}
		final String sortColumn = pagination.getSortColumn();
		if (Objects.equals(sortColumn, ScreenedUserPagination.SORTS.BACKGROUND_CHECK_STATUS.toString())) {
			builder.setOrderField(OrderField.BACKGROUND_CHECK_DATE);
		} else if (Objects.equals(sortColumn, ScreenedUserPagination.SORTS.DRUGTEST_STATUS.toString())) {
			builder.setOrderField(OrderField.DRUG_TEST_DATE);
		}

		return uServiceClient.search(builder.build(), webRequestContextProvider.getRequestContext())
				// Grab the relevant userIds
				.map(new Func1<ScreeningSearchResponse, Set<String>>() {
					@Override
					public Set<String> call(final ScreeningSearchResponse response) {
						final Set<String> userIds = Sets.newHashSet();
						for (final Screening screening : response.getResults()) {
							userIds.add(screening.getUserId());
						}
						return userIds;
					}
				})
				// Grab all the data for those users.
				.flatMap(new Func1<Set<String>, Observable<ScreeningSearchResponse>>() {
					@Override
					public Observable<ScreeningSearchResponse> call(final Set<String> userIds) {
						final ScreeningSearchRequest request = ScreeningSearchRequest.builder()
								.setUserIds(ImmutableList.copyOf(userIds))
								.setLimit(-1).build();
						return uServiceClient.search(request, webRequestContextProvider.getRequestContext());
					}
				})
				// Now collate the whole thing together
				.map(new Func1<ScreeningSearchResponse, ScreenedUserPagination>() {
					@Override
					public ScreenedUserPagination call(final ScreeningSearchResponse response) {
						return collateFindAllScreenedUsers(pagination, sortColumn, response);
					}
				}).toBlocking().single();
	}

	private ScreenedUserPagination collateFindAllScreenedUsers(final ScreenedUserPagination pagination,
															   final String sortColumn,
															   final ScreeningSearchResponse response) {
		// Collate screenings by user id
		final Multimap<String, Screening> userData = HashMultimap.create();
		for (final Screening screening : response.getResults()) {
			userData.put(screening.getUserId(), screening);
		}
		final Map<String, User> indexedUsers = getIndexedUsers(userData.keySet());
		// This makes me die a little inside... :(
		final List<ScreenedUser> screenedUsers = pagination.getResults();

		for (final Entry<String, Collection<Screening>> entry : userData.asMap().entrySet()) {
			final User user = indexedUsers.get(entry.getKey());
			if (user == null) {
				continue; // for dev testing, the user may not exist in local db
			}
			final ScreenedUser screenedUser = new ScreenedUser();
			screenedUser.setId(user.getId());
			screenedUser.setUserNumber(user.getUserNumber());
			screenedUser.setFirstName(user.getFirstName());
			screenedUser.setLastName(user.getLastName());
			screenedUser.setCompanyId(user.getCompany().getId());
			screenedUser.setCompanyName(user.getCompany().getName());
			// Go through this user's screenings, and set the fields.
			for (final Screening s : entry.getValue()) {
				if (s.getVendorRequestCode().equals(VendorRequestCode.BACKGROUND)) {
					screenedUser.setBackgroundCheckStatus(s.getStatus().code());
					screenedUser.setBackgroundCheckRequestDate(s.getCreatedOn().toGregorianCalendar());
				} else if (s.getVendorRequestCode().equals(VendorRequestCode.DRUG)) {
					screenedUser.setDrugTestStatus(s.getStatus().code());
					screenedUser.setDrugTestRequestDate(s.getCreatedOn().toGregorianCalendar());
				}
			}
		}
		pagination.setRowCount(response.getRowCount());
		if (sortColumn != null) {
			Collections.sort(screenedUsers, getComparator(sortColumn));
		}
		return pagination;
	}

	private Comparator<ScreenedUser> getComparator(final String sortColumn) {
		if (Objects.equals(sortColumn, ScreenedUserPagination.SORTS.BACKGROUND_CHECK_STATUS.toString())) {
			return new Comparator<ScreenedUser>() {
				@Override
				public int compare(final ScreenedUser o1, final ScreenedUser o2) {
					final Calendar o1Date = o1.getBackgroundCheckRequestDate();
					if ((o1Date == null)
							!= (o2.getBackgroundCheckRequestDate() == null)) {
						return (o1Date == null) ? -1 : 1;
					}
					return o1Date.compareTo(o2.getBackgroundCheckRequestDate());
				}
			};
		}
		if (Objects.equals(sortColumn, ScreenedUserPagination.SORTS.DRUGTEST_STATUS.toString())) {
			return new Comparator<ScreenedUser>() {
				@Override
				public int compare(final ScreenedUser o1, final ScreenedUser o2) {
					final Calendar o1Date = o1.getDrugTestRequestDate();
					if ((o1Date == null) != (o2.getDrugTestRequestDate() == null)) {
						return (o1Date == null) ? -1 : 1;
					}
					return o1Date.compareTo(o2.getDrugTestRequestDate());
				}
			};
		}
		if (Objects.equals(sortColumn, ScreenedUserPagination.SORTS.USER_FIRSTNAME.toString())) {
			return new Comparator<ScreenedUser>() {
				@Override
				public int compare(final ScreenedUser o1, final ScreenedUser o2) {
					final String first = o1.getFirstName();
					if ((first == null) != (o2.getFirstName() == null)) {
						return (first == null) ? -1 : 1;
					}
					return first.compareTo(o2.getFirstName());
				}
			};
		}
		if (Objects.equals(sortColumn, ScreenedUserPagination.SORTS.USER_LASTNAME.toString())) {
			return new Comparator<ScreenedUser>() {
				@Override
				public int compare(final ScreenedUser o1, final ScreenedUser o2) {
					final String last = o1.getLastName();
					if ((last == null) != (o2.getLastName() == null)) {
						return (last == null) ? -1 : 1;
					}
					return last.compareTo(o2.getLastName());
				}
			};
		}

		if (Objects.equals(sortColumn, ScreenedUserPagination.SORTS.COMPANY_NAME.toString())) {
			return new Comparator<ScreenedUser>() {
				@Override
				public int compare(final ScreenedUser o1, final ScreenedUser o2) {
					final String coName = o1.getCompanyName();
					if ((coName == null) != (o2.getCompanyName() == null)) {
						return (coName == null) ? -1 : 1;
					}
					return coName.compareTo(o2.getCompanyName());
				}
			};
		}
		// if nothing matches, we'll say it's id.
		//if (sortColumn == ScreenedUserPagination.SORTS.USER_ID.toString()) {
		return new Comparator<ScreenedUser>() {
			@Override
			public int compare(final ScreenedUser o1, final ScreenedUser o2) {
				final Long uid = o1.getId();
				if ((uid == null) != (o2.getCompanyName() == null)) {
					return (uid == null) ? -1 : 1;
				}
				return uid.compareTo(o2.getId());
			}
		};
	}

	private Map<String, User> getIndexedUsers(final Set<String> userIds) {
		final List<Long> numericIds = Lists.newArrayList();
		for (final String userId : userIds) {
			numericIds.add(Long.valueOf(userId));
		}
		return getIndexedUsers(numericIds);
	}

	private Map<String, User> getIndexedUsers(final List<Long> numericIds) {
		final Collection<User> users = userDAO.findAllWithCompanyByUserIds(numericIds);
		final Map<String, User> indexedUsers = Maps.newHashMap();
		for (final User user : users) {
			indexedUsers.put(user.getId().toString(), user);
		}
		return indexedUsers;
	}


	Screening findByScreeningId(final String screeningId) throws Exception {
		return uServiceClient.search(ScreeningSearchRequest.builder()
				.setUuids(ImmutableList.of(screeningId))
				.setLimit(1)
				.build(), webRequestContextProvider.getRequestContext())
				.flatMapIterable(new Func1<ScreeningSearchResponse, Iterable<Screening>>() {
					@Override
					public Iterable<Screening> call(final ScreeningSearchResponse resp) {
						return resp.getResults();
					}
				}).toBlocking().single();
	}


	private SterlingScreeningRequest buildScreeningRequest
			(final com.workmarket.domains.model.screening.Screening screeningDomainModel, final ScreeningDTO screeningDto) {

		Assert.notNull(screeningDomainModel);
		Assert.notNull(screeningDto);

		final Country country = Country.valueOf(screeningDto.getCountry());

		return SterlingScreeningRequest.builder()
				.setUserId(screeningDomainModel.getUser().getId().toString())
				.setTitle(screeningDto.getTitle())
				.setFirstName(screeningDto.getFirstName())
				.setMiddleName(screeningDto.getMiddleName())
				.setLastName(screeningDto.getLastName())
				.setMaidenName(screeningDto.getMaidenName())
				.setGender(StringUtils.isBlank(screeningDto.getGender()) ? null :
						(screeningDto.getGender().equals("male") ? Gender.MALE : Gender.FEMALE))
				.setGovtIdNumber(screeningDto.getWorkIdentificationNumber())
				.setEmail(screeningDto.getEmail())
				.setDob(new LocalDate(screeningDto.getBirthYear(), screeningDto.getBirthMonth(),
						screeningDto.getBirthDay()))
				.setAddresses(ImmutableList.of(new Address(screeningDto.getFullAddress(), screeningDto.getCity(),
						screeningDto.getState(), screeningDto.getPostalCode(), country.getISO())))
				.setVendorRequestCode(screeningDomainModel instanceof BackgroundCheck ?
						VendorRequestCode.BACKGROUND : VendorRequestCode.DRUG)
				.setScreeningUuid(screeningDomainModel.getScreeningId())
				.setScreeningStatusCode(ScreeningStatusCode.REQUESTED)
				.setCallExternal(featureEvaluator.hasGlobalFeature("screeningExperimentCallExternal"))
				.build();
	}
}
