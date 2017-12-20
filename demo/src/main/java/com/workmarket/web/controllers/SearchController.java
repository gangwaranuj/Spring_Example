package com.workmarket.web.controllers;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.data.solr.query.location.LocationQueryCreationService;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.model.company.CustomerType;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.qualification.WorkToQualification;
import com.workmarket.domains.search.worker.VendorHydrator;
import com.workmarket.domains.search.worker.WorkerFacetHydrator;
import com.workmarket.domains.search.worker.WorkerHydrator;
import com.workmarket.domains.velvetrope.guest.WebGuest;
import com.workmarket.domains.velvetrope.rope.ESignatureRope;
import com.workmarket.domains.velvetrope.rope.MarketplaceFacetHidingRope;
import com.workmarket.domains.velvetrope.rope.MarketplaceResultHidingRope;
import com.workmarket.domains.velvetrope.rope.MarketplaceRope;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.dto.UserSuggestionDTO;
import com.workmarket.dto.VendorSuggestionDTO;
import com.workmarket.search.SearchWarning;
import com.workmarket.search.SortDirectionType;
import com.workmarket.search.cache.StateLookupCache;
import com.workmarket.search.core.model.SortDirection;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.request.user.BackgroundScreeningChoice;
import com.workmarket.search.request.user.CompanyType;
import com.workmarket.search.request.user.GroupPeopleSearchRequest;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.request.user.PeopleSearchSortByType;
import com.workmarket.search.request.user.RatingsChoice;
import com.workmarket.search.response.FacetResult;
import com.workmarket.search.response.user.PeopleFacetResultType;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.search.worker.FindWorkerClient;
import com.workmarket.search.worker.FindWorkerSearchResponse;
import com.workmarket.search.worker.query.model.*;
import com.workmarket.service.business.CertificationService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.qualification.QualificationAssociationService;
import com.workmarket.service.business.qualification.QualificationRecommender;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.external.GeocodingException;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.service.search.SearchFilterService;
import com.workmarket.service.search.SearchPreferencesService;
import com.workmarket.service.search.SearchService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.service.thrift.transactional.work.WorkResponseBuilder;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.forms.search.UserSearchForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.views.CSVView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import rx.functions.Action1;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static com.workmarket.utility.CollectionUtilities.addToObjectMap;
import static com.workmarket.utility.CollectionUtilities.containsAny;
import static com.workmarket.utility.CollectionUtilities.newObjectMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/search")
public class SearchController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	private static final String METRIC_ROOT = "WorkerSearch";

	public static final String SEARCH_FORM_SESSION_ATTRIBUTE = "searchRequest";
	private final static String SUGGEST_COMPANIES = "/suggest_companies";

	private static final String NEW_WORKER_SEARCH_DEBUG_FEATURE = "NewWorkerSearchDebug";

	private static final String NEW_WORKER_SEARCH_FEATURE = "NewWorkerSearch";
	private static final String NEW_WORKER_SEARCH_COMPANY_FEATURE = "NewWorkerSearchCompany";

	private static final String NEW_WORKER_SEARCH_ASSIGNMENT_FEATURE = "NewWorkerSearchAssignment";
	private static final String NEW_WORKER_SEARCH_ASSIGNMENT_COMPANY_FEATURE = "NewWorkerSearchAssignmentCompany";

	@Autowired private UserGroupService groupService;
	@Autowired private CompanyService companyService;
	@Autowired private SearchService searchService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private SearchFilterService searchFilterService;
	@Autowired private SearchPreferencesService searchPreferencesService;
	@Autowired private VendorService vendorService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private WorkerHydrator workerHydrator;
	@Autowired private WorkerFacetHydrator workerFacetHydrator;
	@Autowired private VendorHydrator vendorHydrator;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private LocationQueryCreationService locationQueryCreationService;
	@Autowired private WorkService workService;
	@Autowired private WorkResponseBuilder workResponseBuilder;
	@Autowired private StateLookupCache stateLookupCache;
	@Autowired private CertificationService certificationService;
	@Autowired private QualificationRecommender qualificationRecommender;
	@Autowired private QualificationAssociationService qualificationAssociationService;
	@Autowired private SuggestionService suggestionService;
	@Autowired private UserService userService;
	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private OrgStructureService orgStructureService;
	@Autowired @Qualifier("ratingsChoiceEditor") private PropertyEditor ratingsChoiceEditor;
	@Autowired @Qualifier("companyTypeEditor") private PropertyEditor companyTypeEditor;
	@Autowired @Qualifier("backgroundScreeningChoiceEditor") private PropertyEditor backgroundScreeningChoiceEditor;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private FindWorkerClient findWorkerClient;
	@Autowired @Qualifier("ESignatureDoorman") private Doorman esignatureDoorman;
	@Autowired @Qualifier("marketplaceDoorman") private Doorman marketplaceDoorman;
	@Autowired @Qualifier("marketplaceResultHidingDoorman") private Doorman marketplaceResultHidingDoorman;
	@Autowired @Qualifier("marketplaceFacetHidingDoorman") private Doorman marketplaceFacetHidingDoorman;

	private WMMetricRegistryFacade wmMetricRegistryFacade;

	private Timer newSearchTimer;

	private Timer legacySearchTimer;

	@PostConstruct
	public void init() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, METRIC_ROOT);
		newSearchTimer = wmMetricRegistryFacade.timer("newWorkerSearch");
		legacySearchTimer = wmMetricRegistryFacade.timer("legacyWorkerSearch");
	}

	@RequestMapping(method = GET)
	public String index(
		@RequestParam(value = "keyword", required = false) String defaultSearchText,
		@RequestParam(value = "lane", required = false) Integer defaultSearchLane,
		Model model) throws SearchException {

		model.addAttribute("companyIsLocked", getCurrentUser().getCompanyIsLocked());
		model.addAttribute("totalCount", 0);
		model.addAttribute("defaultsearch", defaultSearchText);
		model.addAttribute("default_lane", defaultSearchLane);
		model.addAttribute("is_admin", getCurrentUser().hasAnyRoles("ACL_ADMIN", "ACL_MANAGER"));

		Long userId = getCurrentUser().getId();

		Map<String, Object> responseMap = newObjectMap(
			"filters", searchFilterService.get(userId).get("search_filters"),
			"hasVendorPoolsFeature", hasFeature(getCurrentUser().getCompanyId(), Constants.VENDOR_POOLS_FEATURE),
			"preferences", searchPreferencesService.get(userId).get("search_preferences")
		);

		MutableBoolean hasMarketplace = new MutableBoolean(false);
		marketplaceDoorman.welcome(new WebGuest(getCurrentUser()), new MarketplaceRope(hasMarketplace));
		responseMap.put("hasMarketplace", hasMarketplace.isTrue());

		MutableBoolean hasEsignatureEnabled = new MutableBoolean(false);
		esignatureDoorman.welcome(new WebGuest(getCurrentUser()), new ESignatureRope(hasEsignatureEnabled));
		responseMap.put("hasESignatureEnabled", hasEsignatureEnabled.isTrue());

		model.addAllAttributes(responseMap);

		return "web/pages/search/index";
	}

	@RequestMapping(
		value = "/filters",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody String getFilters() {
		return searchFilterService.get(getCurrentUser().getId()).get("search_filters");
	}

	@RequestMapping(
		value = "/filters",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public void setFilters(@RequestParam String filters) {
		searchFilterService.set(getCurrentUser().getId(), ImmutableMap.of("search_filters", filters));
	}

	@InitBinder("searchRequest")
	public void initProjectBinder(WebDataBinder binder) {
		binder.registerCustomEditor(RatingsChoice.class, ratingsChoiceEditor);
		binder.registerCustomEditor(CompanyType.class, companyTypeEditor);
		binder.registerCustomEditor(BackgroundScreeningChoice.class, backgroundScreeningChoiceEditor);
	}

	@RequestMapping(
		value = "/retrieve")
	private void retrieveThrift(
		@ModelAttribute("searchRequest") UserSearchForm form,
		@RequestParam(required = false) Boolean useSavedSearch,
		MessageBundle messages,
		HttpServletRequest httpRequest,
		HttpSession httpSession,
		Model model) throws SearchException, WorkActionException {

		// Populate saved search in session if requested, otherwise clear it
		if (BooleanUtils.isTrue(useSavedSearch) && httpSession.getAttribute(SEARCH_FORM_SESSION_ATTRIBUTE) != null) {
			form = (UserSearchForm) httpSession.getAttribute(SEARCH_FORM_SESSION_ATTRIBUTE);
		} else {
			httpSession.removeAttribute(SEARCH_FORM_SESSION_ATTRIBUTE);
		}

		// Force lane 0, 1 for worker companies and for users from companies with the Private Employee feature.
		// They are not allowed to search the marketplace.
		String customerType = companyService.getCustomerType(getCurrentUser().getCompanyId());
		if (CustomerType.RESOURCE.value().equals(customerType) || isUserPrivateEmployee()) {
			form.setLane(Sets.newHashSet(0, 1));
		}

		// Force worker search only with internal pricing, dispatcher or locked company
		if (isSearchInternalOnly(form)) {
			form.setUserTypes(Sets.newHashSet(SolrUserType.WORKER));
		}

		String searchType = StringUtils.defaultIfEmpty(form.getSearch_type(), SearchType.PEOPLE_SEARCH.toString());

		// PEOPLE_SEARCH (i.e. Find Talent) requests can go to either new search (if the user or user's company is
		// directly feature toggled in or we've toggled split test and their company id mode 10 is 0) or legacy
		if (StringUtils.equals(SearchType.PEOPLE_SEARCH.toString(), searchType)) {
			if (userHasFeature(getCurrentUser(), NEW_WORKER_SEARCH_FEATURE) ||
				hasFeature(getCurrentUser().getCompanyId(), NEW_WORKER_SEARCH_COMPANY_FEATURE)) {
				findWorkers(form, messages, httpSession, model);
				return;
			} else {
				findWorkersLegacy(form, searchType, messages, httpRequest, httpSession, model);
				return;
			}
		}
		// otherwise if it is people search for an assignment
		else if (StringUtils.equals(SearchType.PEOPLE_SEARCH_ASSIGNMENT.toString(), searchType)) {
			if (userHasFeature(getCurrentUser(), NEW_WORKER_SEARCH_ASSIGNMENT_FEATURE) ||
				hasFeature(getCurrentUser().getCompanyId(), NEW_WORKER_SEARCH_ASSIGNMENT_COMPANY_FEATURE)) {
				findWorkers(form, messages, httpSession, model);
				return;
			} else {
				findWorkersLegacy(form, searchType, messages, httpRequest, httpSession, model);
				return;
			}
		}
		else if (StringUtils.equals(SearchType.PEOPLE_SEARCH_GROUP_MEMBER.toString(), searchType) ||
				StringUtils.equals(SearchType.PEOPLE_SEARCH_TALENT_POOL_INVITE.toString(), searchType)) {
			if (!userHasFeature(getCurrentUser(), Constants.VENDOR_POOLS_FEATURE)) {
				form.setUserTypes(Sets.newHashSet(SolrUserType.WORKER));
			}
			findWorkers(form, messages, httpSession, model);
		}
		// otherwise it is another search type so use our legacy
		else {
			findWorkersLegacy(form, searchType, messages, httpRequest, httpSession, model);
			return;
		}
	}

	public void findWorkers(
		final UserSearchForm form,
		final MessageBundle messages,
		final HttpSession httpSession,
		final Model model) throws SearchException, WorkActionException {

		Timer.Context timerContext = newSearchTimer.time();
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		logger.info("User {} is using new worker search. RequestId: {}", getCurrentUser().getId(),
			requestContext.getRequestId());

		try {
			final FindWorkerCriteria criteria = buildFindWorkerCriteria(form, requestContext);

			long offset = 0;
			long limit = 50;
			if (form.getPagination() != null) {
				Pagination pagination = form.getPagination();
				offset = Long.valueOf(pagination.getCursorPosition());
				limit = Long.valueOf(pagination.getPageSize());
			}

			final MutableObject<Optional<SearchException>> exceptionMutableObject =
				new MutableObject<>(Optional.<SearchException>absent());
			findWorkerClient.findWorkers(criteria, offset, limit, requestContext)
				.subscribe(
					new Action1<FindWorkerSearchResponse>() {
						@Override
						public void call(FindWorkerSearchResponse findWorkerSearchResponse) {
							// no error so let's parse our results
							List<Map<String, Object>> results = Lists.newArrayList();
							results.addAll(
								workerHydrator.hydrateWorkers(
									webRequestContextProvider.getRequestContext(),
									getCurrentUser(),
									findWorkerSearchResponse,
									criteria));

							results.addAll(
								vendorHydrator.hydrateVendors(
									webRequestContextProvider.getRequestContext(),
									getCurrentUser(),
									findWorkerSearchResponse,
									criteria));

							Map<String, Object> filters = workerFacetHydrator.hydrateFacets(findWorkerSearchResponse, criteria, getCurrentUser());

							// TODO: Alex - remove this, setting attributes directly on the session is bad
							// Save filters back into the search session.
							httpSession.setAttribute(SEARCH_FORM_SESSION_ATTRIBUTE, form);

							// When accessing this endpoint, the Vendor/Worker toggle in search is set to Workers
							searchPreferencesService.set(getCurrentUser().getId(), ImmutableMap.of("search_preferences", "workers"));

							Map<String, Object> responseMap = newObjectMap(
								"results", sort(results, on(Map.class).get("position")),
								"mode", form.getResource_mode(),
								"results_count", findWorkerSearchResponse.getTotalResults(),
								"filters", filters,
								"hasVendorPoolsFeature", hasFeature(getCurrentUser().getCompanyId(), Constants.VENDOR_POOLS_FEATURE),
								"warnings", messages.getWarnings(),
								"search_version", "2.0",
								"request_id", webRequestContextProvider.getWebRequestContext().getRequestId()
							);

							MutableBoolean hasMarketplace = new MutableBoolean(false);
							marketplaceDoorman.welcome(new WebGuest(getCurrentUser()), new MarketplaceRope(hasMarketplace));
							responseMap.put("hasMarketplace", hasMarketplace.isTrue());

							model.addAttribute("response", responseMap);
						}
					},
					new Action1<Throwable>() {
						@Override
						public void call(Throwable throwable) {
							logger.error("User {} is using new worker search. RequestId: {} Error: {}",
								getCurrentUser().getId(),
								requestContext.getRequestId(),
								throwable.getMessage(),
								throwable);

							exceptionMutableObject.setValue(
								Optional.of(new SearchException("Failed retrieving our search results",
									throwable.getCause())));
						}
					});

			if (exceptionMutableObject.getValue().isPresent()) {
				throw exceptionMutableObject.getValue().get();
			}
		} finally {
			long time = timerContext.stop();

			logger.info("New worker search took " + TimeUnit.NANOSECONDS.toMillis(time) + "ms");
		}

	}

	/**
	 * Check to see if our "user" (either the actual logged in user or the masquerading user)
	 *
	 * @param user    The user we are checking to see if they have the feature
	 * @param feature The feature we are checking
	 * @return boolean Return true if the user has the feature, false otherwise
	 */
	private boolean userHasFeature(final ExtendedUserDetails user, final String feature) {
		if (user.isMasquerading()) {
			return featureEvaluator.hasFeature(user.getEmail(), ExtendedUserDetailsOptionsService.ALL_OPTIONS, feature);
		} else {
			return hasFeature(user, feature);
		}
	}

	private FindWorkerCriteria buildFindWorkerCriteria(final UserSearchForm form, final RequestContext requestContext) {
		final FindWorkerCriteriaBuilder builder = new FindWorkerCriteriaBuilder();
		builder.setDebug(featureEvaluator.hasGlobalFeature(NEW_WORKER_SEARCH_DEBUG_FEATURE));
		builder.setExcludeHighlights(false);

		builder.setRequestingUserId(String.valueOf(getCurrentUser().getId()));
		builder.setRequestingCompanyId(String.valueOf(getCurrentUser().getCompanyId()));
		builder.setSearchType(getSearchType(form));
		if (form.getGroup_id() != null) {
			builder.setRequestingTalentPoolId(form.getGroup_id().toString());
		}

		// if we have a work number then retrieve it for use by various create methods below
		AbstractWork work = null;
		if (StringUtils.isNotEmpty(form.getWork_number())) {
			work = workService.findWorkByWorkNumber(form.getWork_number(), false);
		}

		//
		// Company criteria
		//
		createCompanyCriteria(form).transform(new Function<CompanyCriteria, CompanyCriteria>() {
			@Nullable
			@Override
			public CompanyCriteria apply(@Nullable CompanyCriteria companyCriteria) {
				builder.setCompanyCriteria(companyCriteria);
				return companyCriteria;
			}
		});

		//
		// Group criteria
		//
		createGroupCriteria(form).transform(new Function<GroupCriteria, GroupCriteria>() {
			@Nullable
			@Override
			public GroupCriteria apply(@Nullable GroupCriteria groupCriteria) {
				builder.setGroupCriteria(groupCriteria);
				return groupCriteria;
			}
		});

		//
		// Group membership criteria
		//
		createGroupMembershipCriteria(form).transform(new Function<GroupMembershipCriteria, GroupMembershipCriteria>() {
			@Nullable
			@Override
			public GroupMembershipCriteria apply(@Nullable GroupMembershipCriteria groupMembershipCriteria) {
				builder.setGroupMembershipCriteria(groupMembershipCriteria);
				return groupMembershipCriteria;
			}
		});

		//
		// Profile criteria
		//
		createProfileCriteria(form, work).transform(new Function<ProfileCriteria, ProfileCriteria>() {
			@Nullable
			@Override
			public ProfileCriteria apply(@Nullable ProfileCriteria profileCriteria) {
				builder.setProfileCriteria(profileCriteria);
				return profileCriteria;
			}
		});

		//
		// Location criteria
		//
		Optional<LocationCriteria> locationCriteria = createLocationCriteria(form).transform(new Function<LocationCriteria, LocationCriteria>() {
			@Nullable
			@Override
			public LocationCriteria apply(@Nullable LocationCriteria locationCriteria) {
				builder.setLocationCriteria(locationCriteria);
				return locationCriteria;
			}
		});


		//
		// Qualification criteria
		//
		createQualificationsCriteria(form).transform(new Function<QualificationsCriteria, QualificationsCriteria>() {
			@Nullable
			@Override
			public QualificationsCriteria apply(@Nullable QualificationsCriteria qualificationsCriteria) {
				builder.setQualificationsCriteria(qualificationsCriteria);
				return qualificationsCriteria;
			}
		});

		//
		// Rating criteria
		//
		createRatingCriteria(form).transform(new Function<RatingCriteria, RatingCriteria>() {
			@Nullable
			@Override
			public RatingCriteria apply(@Nullable RatingCriteria ratingCriteria) {
				builder.setRatingCriteria(ratingCriteria);
				return ratingCriteria;
			}
		});

		//
		// Screening criteria
		//
		createScreeningCriteria(form).transform(new Function<ScreeningCriteria, ScreeningCriteria>() {
			@Nullable
			@Override
			public ScreeningCriteria apply(@Nullable ScreeningCriteria screeningCriteria) {
				builder.setScreeningCriteria(screeningCriteria);
				return screeningCriteria;
			}
		});

		//
		// blocked criteria
		//
		createBlockedCriteria(form).transform(new Function<BlockedCriteria, Object>() {
			@Nullable
			@Override
			public BlockedCriteria apply(@Nullable BlockedCriteria blockedCriteria) {
				builder.setBlockedCriteria(blockedCriteria);
				return blockedCriteria;
			}
		});

		//
		// Insurance criteria
		//
		createInsuranceCriteria(form).transform(new Function<InsuranceCriteria, InsuranceCriteria>() {
			@Nullable
			@Override
			public InsuranceCriteria apply(@Nullable InsuranceCriteria insuranceCriteria) {
				builder.setInsuranceCriteria(insuranceCriteria);
				return insuranceCriteria;
			}
		});

		//
		// Work criteria (when doing this for an assignment)
		//
		createWorkCriteria(form, work).transform(new Function<WorkCriteria, WorkCriteria>() {
			@Nullable
			@Override
			public WorkCriteria apply(@Nullable WorkCriteria workCriteria) {
				builder.setWorkCriteria(workCriteria);
				return workCriteria;
			}
		});

		//
		// userType criteria
		//
		createUserTypeCriteria(form).transform(new Function<UserTypeCriteria, UserTypeCriteria>() {
			@Nullable
			@Override
			public UserTypeCriteria apply(@Nullable UserTypeCriteria userTypeCriteria) {
				builder.setUserTypeCriteria(userTypeCriteria);
				return userTypeCriteria;
			}
		});

		//
		// OrgUnits criteria
		//
		createOrgUnitsCriteria(form).transform(new Function<OrgUnitsCriteria, OrgUnitsCriteria>() {
			@Nullable
			@Override
			public OrgUnitsCriteria apply(@Nullable OrgUnitsCriteria orgUnitsCriteria) {
				builder.setOrgUnitsCriteria(orgUnitsCriteria);
				return orgUnitsCriteria;
			}
		});

		//
		// Sorting
		//
		boolean hasGeoPoint = locationCriteria.isPresent() && locationCriteria.get().getGeoPoint() != null;
		createSortCriteria(form, hasGeoPoint).transform(new Function<SortCriteria, SortCriteria>() {
			@Nullable
			@Override
			public SortCriteria apply(@Nullable SortCriteria sortCriteria) {
				builder.setSortCriteria(sortCriteria);
				return sortCriteria;
			}
		});

		builder.setKeywords(StringUtils.isEmpty(form.getKeyword()) ? null : form.getKeyword());

		return builder.build().get();
	}

	private com.workmarket.search.worker.query.model.SearchType getSearchType(final UserSearchForm searchForm) {
		final String searchTypeStr = searchForm.getSearch_type();
		if (searchTypeStr == null) {
			return com.workmarket.search.worker.query.model.SearchType.UNKNOWN;
		}

		try {
			final SearchType searchType = SearchType.valueOf(searchTypeStr);
			switch(searchType) {
				case PEOPLE_SEARCH:
					return com.workmarket.search.worker.query.model.SearchType.PEOPLE_SEARCH;
				case PEOPLE_SEARCH_ASSESSMENT_INVITE:
					return com.workmarket.search.worker.query.model.SearchType.PEOPLE_SEARCH_ASSESSMENT_INVITE;
				case PEOPLE_SEARCH_ASSIGNMENT:
					return com.workmarket.search.worker.query.model.SearchType.PEOPLE_SEARCH_ASSIGNMENT;
				case PEOPLE_SEARCH_ASSIGNMENT_FULL_NAME:
					return com.workmarket.search.worker.query.model.SearchType.PEOPLE_SEARCH_ASSIGNMENT_FULL_NAME;
				case PEOPLE_SEARCH_GROUP:
					return com.workmarket.search.worker.query.model.SearchType.PEOPLE_SEARCH_GROUP;
				case PEOPLE_SEARCH_GROUP_MEMBER:
					return com.workmarket.search.worker.query.model.SearchType.PEOPLE_SEARCH_GROUP_MEMBER;
				case PEOPLE_SEARCH_TALENT_POOL_INVITE:
					return com.workmarket.search.worker.query.model.SearchType.PEOPLE_SEARCH_TALENT_POOL_INVITE;
				case PEOPLE_SEARCH_ELIGIBILITY:
					return com.workmarket.search.worker.query.model.SearchType.PEOPLE_SEARCH_ELIGIBILITY;
				case PEOPLE_SEARCH_TYPE_AHEAD:
					return com.workmarket.search.worker.query.model.SearchType.PEOPLE_SEARCH_TYPE_AHEAD;
				default:
					return com.workmarket.search.worker.query.model.SearchType.UNKNOWN;
			}
		} catch (final IllegalArgumentException e) {
			logger.error("Unknown search type:" + searchTypeStr, e);
			return com.workmarket.search.worker.query.model.SearchType.UNKNOWN;
		}
	}

	private Optional<ProfileCriteria> createProfileCriteria(final UserSearchForm form, final AbstractWork work) {
		ProfileCriteriaBuilder builder = new ProfileCriteriaBuilder();

		if (form.getAvatar()) {
			builder.setHasProfilePicture(form.getAvatar());
		}

		// worker relations
		// if our work assignment is internal pricing or the user is a dispatcher then only allow lane1 regardless
		// of what filters might show
		if ((work != null && work.getPricingStrategyType() != null
			&& work.getPricingStrategyType() == PricingStrategyType.INTERNAL)
			|| (getCurrentUser().isDispatcher())) {
			builder.addWorkerRelation(WorkerRelation.employees);
		} else if (getCurrentUser().getCompanyIsLocked()) {
			// locked companies can only see their internal/employees
			builder.addWorkerRelations(Lists.newArrayList(WorkerRelation.internal, WorkerRelation.employees));
		} else {

			Set<LaneType> laneTypes = Sets.newHashSet();
			if (form.getLaneFilter() != null) {
				laneTypes.addAll(form.getLaneFilter());
			}
			marketplaceResultHidingDoorman.welcome(
				new WebGuest(getCurrentUser()),
				new MarketplaceResultHidingRope(form.getLaneFilter(), laneTypes)
			);

			if (laneTypes != null) {
				for (LaneType lane : laneTypes) {
					if (lane == LaneType.LANE_0) {
						builder.addWorkerRelation(WorkerRelation.internal);
					} else if (LaneType.LANE_1.equals(lane)) {
						builder.addWorkerRelation(WorkerRelation.employees);
					} else if (LaneType.LANE_2.equals(lane)) {
						builder.addWorkerRelation(WorkerRelation.contractors);
					} else if (LaneType.LANE_3.equals(lane)) {
						builder.addWorkerRelation(WorkerRelation.thirdParties);
					} else if (LaneType.LANE_4.equals(lane)) {
						builder.addWorkerRelation(WorkerRelation.everyoneElse);
					} else if (LaneType.LANE_23.equals(lane)) {
						builder.addWorkerRelation(WorkerRelation.contractors);
						builder.addWorkerRelation(WorkerRelation.thirdParties);
					}
				}
			}
		}

		return builder.build();
	}

	private Optional<CompanyCriteria> createCompanyCriteria(final UserSearchForm form) {
		CompanyCriteriaBuilder builder = new CompanyCriteriaBuilder();

		List<String> industries = convertToList(form.getIndustry());
		if (CollectionUtils.isNotEmpty(industries)) {
			builder.addIndustries(industries);
		}

		List<com.workmarket.search.worker.query.model.CompanyType> companyTypes =
			convertCompanyType(form.getCompanytypes());
		if (CollectionUtils.isNotEmpty(companyTypes)) {
			builder.addCompanyTypes(companyTypes);

		}

		if(CollectionUtils.isNotEmpty(form.getCompany())) {
			for(Long companyId : form.getCompany()) {
				builder.addCompanyId(companyId.toString());
			}
		}

		return builder.build();
	}

	private Optional<UserTypeCriteria> createUserTypeCriteria(final UserSearchForm form) {
		UserTypeCriteriaBuilder builder = new UserTypeCriteriaBuilder();
		if (form.getUserTypes() != null) {
			for (SolrUserType solrUserType : form.getUserTypes()) {
				if (solrUserType.equals(SolrUserType.VENDOR)) {
					builder.addUserType(UserType.VENDOR);
				}
				if (solrUserType.equals(SolrUserType.WORKER)) {
					builder.addUserType(UserType.WORKER);
				}
			}
		}
		return builder.build();
	}

	private Optional<OrgUnitsCriteria> createOrgUnitsCriteria(final UserSearchForm form) {
		final Long currentUserId = getCurrentUser().getId();
		final boolean hasOrgFeatureToggle = featureEntitlementService.hasFeatureToggle(currentUserId, "org_structures");
		if (!hasOrgFeatureToggle) {
			return Optional.absent();
		}

		final Set<String> orgUnitUuids = form.getOrgUnits();
		final List<String> visibleOrgUnitUuids = orgStructureService
				.getSubtreePathOrgUnitUuidsForCurrentOrgMode(currentUserId, getCurrentUser().getCompanyId());
		if (CollectionUtils.isEmpty(orgUnitUuids)) {
			return new OrgUnitsCriteriaBuilder()
					.addOrgUnits(visibleOrgUnitUuids)
					.build();
		}

		orgUnitUuids.retainAll(visibleOrgUnitUuids);
		return new OrgUnitsCriteriaBuilder()
				.addOrgUnits(ImmutableList.copyOf(orgUnitUuids))
				.build();
	}

	private Optional<GroupCriteria> createGroupCriteria(final UserSearchForm form) {
		GroupCriteriaBuilder builder = new GroupCriteriaBuilder();

		List<String> sharedTalentPools = convertToList(form.getSharedgroup());
		if (CollectionUtils.isNotEmpty(sharedTalentPools)) {
			builder.addSharedTalentPools(sharedTalentPools);
		}

		List<String> privateTalentPools = convertToList(form.getGroup());
		if (CollectionUtils.isNotEmpty(privateTalentPools)) {
			builder.addPrivateTalentPools(privateTalentPools);
		}

		return builder.build();
	}

	private Optional<GroupMembershipCriteria> createGroupMembershipCriteria(final UserSearchForm form) {
		GroupMembershipCriteriaBuilder builder = new GroupMembershipCriteriaBuilder();
		if (form.getGroup_id() == null) {
			return builder.build();
		}

		String groupId = form.getGroup_id().toString();
		List<String> membershipStatusGroupIds = ImmutableList.of(groupId);

		if (form.isMember()) {
			builder.addTalentPoolMemberships(membershipStatusGroupIds);
		}

		if (form.isMemberoverride()) {
			builder.addTalentPoolMembershipOverrides(membershipStatusGroupIds);
		}

		if (form.isPending()) {
			builder.addPendingPassedTalentPools(membershipStatusGroupIds);
		}

		if (form.isPendingoverride()) {
			builder.addPendingFailedTalentPools(membershipStatusGroupIds);
		}

		if (form.isInvited()) {
			builder.addInvitedTalentPools(membershipStatusGroupIds);
		}

		if (form.isDeclined()) {
			builder.addDeclinedTalentPools(membershipStatusGroupIds);
		}

		return builder.build();
	}

	private Optional<WorkCriteria> createWorkCriteria(final UserSearchForm form, final AbstractWork work) {
		final WorkCriteriaBuilder builder = new WorkCriteriaBuilder();
		if (work != null) {
			builder.setWorkNumber(work.getWorkNumber())
				.setTitle(work.getTitle())
				.setDescription(Jsoup.parse(work.getDescription()).text())
				.setSkills(work.getDesiredSkills());
			// handle declines
			List<Long> declinedCompanies = vendorService.getDeclinedVendorIdsByWork(work.getId());
			if (declinedCompanies.contains(getCurrentUser().getCompanyId())) {
				declinedCompanies.remove(getCurrentUser().getCompanyId());
			}

			if (CollectionUtils.isNotEmpty(declinedCompanies)) {
				for (Long companyId : declinedCompanies) {
					builder.addDeclinedCompany(companyId.toString());
				}
			}
			final List<String> bundledJobTitles = Lists.newArrayList();
			final int jobTitlePrefixPosition = StringUtils.lastIndexOf(work.getDesiredSkills(), "--");
			if (work.getId() != null && jobTitlePrefixPosition > -1
				&& jobTitlePrefixPosition + 2 < work.getDesiredSkills().length()) {
				List<WorkToQualification> workToQualifications =
					qualificationAssociationService.findWorkQualifications(work.getId(), QualificationType.job_title, false);
				if (workToQualifications.size() > 0) {
					// we use first one only
					final String qualificationUuid = workToQualifications.get(0).getQualificationUuid();
					qualificationRecommender.searchSimilarQualifications(qualificationUuid, webRequestContextProvider.getRequestContext())
						.subscribe(
							new Action1<Qualification>() {
								@Override
								public void call(com.workmarket.search.qualification.Qualification qualification) {
									bundledJobTitles.add(qualification.getName());
								}
							},
							new Action1<Throwable>() {
								@Override
								public void call(Throwable throwable) {
									logger.error("Failed to fetch job titles from qualification service: " + throwable);
								}
							});
				}
			}
			if (bundledJobTitles.size() > 0) {
				builder.addJobTitles(bundledJobTitles);
			}
		}

		return builder.build();
	}

	private Optional<InsuranceCriteria> createInsuranceCriteria(final UserSearchForm form) {
		InsuranceCriteriaBuilder builder = new InsuranceCriteriaBuilder();


		if (form.getWorkersCompCoverage() != null) {
			builder.setWorkersCompCoverageAmount(form.getWorkersCompCoverage().longValue());
		}

		if (form.getGeneralLiabilityCoverage() != null) {
			builder.setGeneralLiabilityCoverageAmount(form.getGeneralLiabilityCoverage().longValue());
		}

		if (form.getErrorsAndOmissionsCoverage() != null) {
			builder.setErrorsAndOmissionsCoverageAmount(form.getErrorsAndOmissionsCoverage().longValue());
		}

		if (form.getAutomobileCoverage() != null) {
			builder.setAutomobileCoverageAmount(form.getAutomobileCoverage().longValue());
		}

		if (form.getContractorsCoverage() != null) {
			builder.setContractorsCoverageAmount(form.getContractorsCoverage().longValue());
		}

		if (form.getCommercialGeneralLiabilityCoverage() != null) {
			builder.setCommercialGeneralLiabilityCoverageAmount(form.getCommercialGeneralLiabilityCoverage().longValue());
		}

		if (form.getBusinessLiabilityCoverage() != null) {
			builder.setBusinessLiabilityCoverageAmount(form.getBusinessLiabilityCoverage().longValue());
		}

		return builder.build();
	}

	private Optional<SortCriteria> createSortCriteria(final UserSearchForm form, boolean hasGeoPoint) {
		SortCriteriaBuilder builder = new SortCriteriaBuilder();

		if (form.getPagination() == null || form.getPagination().getSortBy() == null) {
			builder.setSortType(SortType.relevancy);
		} else {
			PeopleSearchSortByType sortByType = form.getPagination().getSortBy();
			SortDirection sortDirection;
			if (form.getPagination().getSortDirection() == null || form.getPagination().getSortDirection() == SortDirectionType.DESC) {
				sortDirection = SortDirection.desc;
			} else {
				sortDirection = SortDirection.asc;
			}
			switch (sortByType) {
				case DISTANCE:
					if (hasGeoPoint) {
						builder.setSortType(SortType.distance);
					}
					break;
				case NAME:
					builder.setSortType(SortType.name);
					builder.setSortDirection(sortDirection);
					break;
				case HOURLY_RATE:
					builder.setSortType(SortType.hourlyRate);
					builder.setSortDirection(sortDirection);
					break;
				case RATING:
					builder.setSortType(SortType.rating);
					break;
				case WORK_COMPLETED:
					builder.setSortType(SortType.workCompletedCount);
					break;
				case CREATED_ON:
					builder.setSortType(SortType.createdOn);
					builder.setSortDirection(sortDirection);
					break;
				default:
					builder.setSortType(SortType.relevancy);
			}
		}


		return builder.build();
	}

	private Optional<ScreeningCriteria> createScreeningCriteria(final UserSearchForm form) {
		ScreeningCriteriaBuilder builder = new ScreeningCriteriaBuilder();

		Set<BackgroundScreeningChoice> screeningChoices = form.getVerification();
		if (CollectionUtils.isNotEmpty(screeningChoices)) {
			for (BackgroundScreeningChoice choice : screeningChoices) {
				if (choice == BackgroundScreeningChoice.backgroundCheck) {
					builder.setHasBackgroundCheck(Boolean.TRUE);
				} else if (choice == BackgroundScreeningChoice.backgroundCheckedWithinLast6Months) {
					builder.setHasCurrentBackgroundCheck(Boolean.TRUE);
				} else if (choice == BackgroundScreeningChoice.backgroundCheckedWithinLast12Months) {
					builder.setHas12MonthBackgroundCheck(Boolean.TRUE);
				} else if (choice == BackgroundScreeningChoice.drugTested) {
					builder.setHasPassedDrugTest(Boolean.TRUE);
				} else if (choice == BackgroundScreeningChoice.drugTestedWithinLast6Months) {
					builder.setHas6MonthPassedDrugTest(Boolean.TRUE);
				} else if (choice == BackgroundScreeningChoice.drugTestedWithinLast12Months) {
					builder.setHas12MonthPassedDrugTest(Boolean.TRUE);
				}
			}

		}

		return builder.build();
	}

	private Optional<BlockedCriteria> createBlockedCriteria(final UserSearchForm form) {
		BlockedCriteriaBuilder builder = new BlockedCriteriaBuilder();
		if (form.isShowBlockedUsers()) {
			builder.setShowBlockedUsers(true);
		}
		if (form.isShowBlockedCompanies()) {
			builder.setShowBlockedCompanies(true);
		}
		return builder.build();
	}

	private Optional<RatingCriteria> createRatingCriteria(final UserSearchForm form) {
		RatingCriteriaBuilder builder = new RatingCriteriaBuilder();

		if (form.getOnTimePercentage() != null) {
			builder.setOnTimePercent(form.getOnTimePercentage().longValue());
		}

		if (form.getSatisfactionRate() != null) {
			builder.setOnTimePercent(form.getSatisfactionRate().longValue());
		}

		if (form.getDeliverableOnTimePercentage() != null) {
			builder.setOnTimePercent(form.getDeliverableOnTimePercentage().longValue());
		}

		return builder.build();
	}

	private Optional<LocationCriteria> createLocationCriteria(final UserSearchForm form) {
		LocationCriteriaBuilder builder = new LocationCriteriaBuilder();

		if (!StringUtils.isEmpty(form.getAddress())) {

			if (stateLookupCache.isStateQuery(form.getAddress())) {
				builder.setState(stateLookupCache.getStateCode(form.getAddress()));
			} else {
				try {
					GeoPoint gp = locationQueryCreationService.getGeoLocationPoint(form.getAddress());
					com.workmarket.search.core.model.GeoPoint geoPoint = new com.workmarket.search.core.model.GeoPoint(gp.getLatitude(), gp.getLongitude());
					builder.setGeoPoint(geoPoint);

				} catch (GeocodingException e) {
					logger.warn("failed to get geoPoint for location " + form.getAddress());
				}
			}
		}

		if (form.getRadius() != null && !form.getRadius().equals("any")) {
			try {
				Long radius = Long.valueOf(form.getRadius());
				BigDecimal kilometers = new BigDecimal(radius);
				kilometers = kilometers.multiply(new BigDecimal(1.609344));
				builder.setRadiusKilometers(kilometers.longValue());
			} catch (NumberFormatException e) {
				logger.warn("failed to parse radius " + form.getRadius());
			}
		}

		if (CollectionUtils.isNotEmpty(form.getCountries())) {
			builder.addCountries(convertToList(form.getCountries()));
		}

		return builder.build();
	}

	private Optional<QualificationsCriteria> createQualificationsCriteria(final UserSearchForm form) {
		QualificationsCriteriaBuilder builder = new QualificationsCriteriaBuilder();

		if (CollectionUtils.isNotEmpty(form.getCertification())) {
			builder.addCertifications(convertToList(form.getCertification()));
		}

		if (CollectionUtils.isNotEmpty(form.getLicense())) {
			builder.addLicenses(convertToList(form.getLicense()));
		}

		if (CollectionUtils.isNotEmpty(form.getAssessment())) {
			builder.addTests(convertToList(form.getAssessment()));
		}

		return builder.build();
	}

	private List<com.workmarket.search.worker.query.model.CompanyType> convertCompanyType(final Set<CompanyType> companyTypes) {
		List<com.workmarket.search.worker.query.model.CompanyType> result = null;
		if (companyTypes != null && companyTypes.size() > 0) {
			result = Lists.newArrayList();
			for (CompanyType ct : companyTypes) {
				if (ct == CompanyType.Corporation) {
					result.add(com.workmarket.search.worker.query.model.CompanyType.corp);
				} else if (ct == CompanyType.SoleProprietor) {
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

	private void findWorkersLegacy(
		final UserSearchForm form,
		final String searchType,
		final MessageBundle messages,
		final HttpServletRequest httpRequest,
		final HttpSession httpSession,
		final Model model) throws SearchException, WorkActionException {

		Timer.Context timerContext = legacySearchTimer.time();

		logger.info("User " + getCurrentUser().getId() + " is using legacy worker search", webRequestContextProvider.getRequestContext());

		PeopleSearchRequest searchRequest = buildPeopleSearchRequest(form);
		PeopleSearchResponse searchResponse = augmentRequestAndExecuteSearch(searchType, form, searchRequest, httpRequest);

		List<Map<String, Object>> results = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(searchResponse.getResults())) {
			boolean internal = getCurrentUser().isInternal();
			// TODO: Alex - cache
			boolean isInstantWorkerPoolEnabled = companyService.isInstantWorkerPoolEnabled(getCurrentUser().getCompanyId());

			for (PeopleSearchResult r : searchResponse.getResults()) {
				results.add(r.toObjectMap(isInstantWorkerPoolEnabled, internal));
			}
		}

		Map<Enum<PeopleFacetResultType>, List<FacetResult>> facets = searchResponse.getFacets();
		Map<String, Object> filters = Maps.newHashMap();
		filters.put("labels", Maps.newHashMap());

		marketplaceFacetHidingDoorman.welcome(new WebGuest(getCurrentUser()), new MarketplaceFacetHidingRope(facets));

		buildFilterResponse(facets, filters, PeopleFacetResultType.LANE, "lanes");
		buildFilterResponse(facets, filters, PeopleFacetResultType.RATING, "ratings");
		buildFilterResponse(facets, filters, PeopleFacetResultType.ASSESSMENT, "assessments");
		buildFilterResponse(facets, filters, PeopleFacetResultType.CERTIFICATION, "certifications");
		buildFilterResponse(facets, filters, PeopleFacetResultType.GROUP, "groups");
		buildFilterResponse(facets, filters, PeopleFacetResultType.SHARED_GROUP, "sharedgroups");
		buildFilterResponse(facets, filters, PeopleFacetResultType.INDUSTRY, "industries");
		buildFilterResponse(facets, filters, PeopleFacetResultType.LICENSE, "licenses");
		buildFilterResponse(facets, filters, PeopleFacetResultType.VERIFICATION, "verifications");
		buildFilterResponse(facets, filters, PeopleFacetResultType.COMPANY_TYPE, "companytypes");
		buildFilterResponse(facets, filters, PeopleFacetResultType.COUNTRY, "countries");
		buildFilterResponse(facets, filters, PeopleFacetResultType.AVATAR, "avatar");
		buildFilterResponse(facets, filters, PeopleFacetResultType.COMPANY_ID, "companies");

		if (searchType.equals(SearchType.PEOPLE_SEARCH_GROUP_MEMBER.toString())) {
			buildFilterResponse(facets, filters, PeopleFacetResultType.GROUP_MEMBERS, "member");
			buildFilterResponse(facets, filters, PeopleFacetResultType.GROUP_MEMBERS_OVERRIDE, "memberoverride");
			buildFilterResponse(facets, filters, PeopleFacetResultType.GROUP_PENDING_MEMBERS, "pending");
			buildFilterResponse(facets, filters, PeopleFacetResultType.GROUP_PENDING_OVERRIDE_MEMBERS, "pendingoverride");
			buildFilterResponse(facets, filters, PeopleFacetResultType.GROUP_INVITED, "invited");
			buildFilterResponse(facets, filters, PeopleFacetResultType.GROUP_DECLINED, "declined");
		}

		if (searchType.equals(SearchType.PEOPLE_SEARCH_ASSESSMENT_INVITE.toString())) {
			buildFilterResponse(facets, filters, PeopleFacetResultType.INVITED_ASSESSMENT, "invitedassessments");
			buildFilterResponse(facets, filters, PeopleFacetResultType.NOT_INVITED_ASSESSMENT, "notinvitedassessments");
			buildFilterResponse(facets, filters, PeopleFacetResultType.PASSED_ASSESSMENT, "passedassessments");
			buildFilterResponse(facets, filters, PeopleFacetResultType.FAILED_TEST, "failedtests");
		}

		// Get warnings
		if (searchResponse.isSetWarnings() && !searchResponse.getWarnings().isEmpty()) {
			for (SearchWarning warning : searchResponse.getWarnings()) {
				if (warning != null && warning.getSearchWarning() != null) {
					switch (warning.getSearchWarning()) {
						case LOCATION_ZERO_RESULTS:
						case LOCATION_OVER_QUERY_LIMIT:
						case LOCATION_REQUEST_DENIED:
						case LOCATION_INVALID_REQUEST:
						case LOCATION_UNKNOWN_ERROR:
							messageHelper.addError(messages, "search.location.invalid");
							break;
					}
				}
			}
		}

		// TODO: Alex - remove this, setting attributes directly on the session is bad
		// Save filters back into the search session.
		httpSession.setAttribute(SEARCH_FORM_SESSION_ATTRIBUTE, form);

		// When accessing this endpoint, the Vendor/Worker toggle in search is set to Workers
		searchPreferencesService.set(getCurrentUser().getId(), ImmutableMap.of("search_preferences", "workers"));

		Map<String, Object> responseMap = newObjectMap(
			"results", results,
			"mode", form.getResource_mode(),
			"results_count", searchResponse.getTotalResultsCount(),
			"filters", filters,
			"hasVendorPoolsFeature", hasFeature(getCurrentUser().getCompanyId(), Constants.VENDOR_POOLS_FEATURE),
			"warnings", messages.getWarnings(),
			"search_version", "1.0",
			"request_id", webRequestContextProvider.getWebRequestContext().getRequestId()
		);

		MutableBoolean hasMarketplace = new MutableBoolean(false);
		marketplaceDoorman.welcome(new WebGuest(getCurrentUser()), new MarketplaceRope(hasMarketplace));
		responseMap.put("hasMarketplace", hasMarketplace.isTrue());

		model.addAttribute("response", responseMap);

		long time = timerContext.stop();
		logger.info("Legacy worker search took " + TimeUnit.NANOSECONDS.toMillis(time) + "ms");
	}

	@RequestMapping(
		value = "/export_csv",
		method = POST)
	public CSVView exportToCsv(@ModelAttribute("searchRequest") UserSearchForm form) {
		PeopleSearchRequest searchRequest = buildPeopleSearchRequest(form);
		searchService.exportPeopleSearch(searchRequest);
		return new CSVView();
	}

	@RequestMapping(
		value = "/suggest_users.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getSuggestedUsers(
		@RequestParam("term") String term,
		@RequestParam(value = "laneFilter", required = false) Set<LaneType> laneFilter,
		@ModelAttribute("searchRequest") UserSearchForm form,
		Model model
	) throws Exception {
		List<Map<String, String>>  response = Lists.newArrayList();

		final boolean isSearchInternal = isSearchInternalOnly(form);
		final List<UserSuggestionDTO> suggestions = isSearchInternal
			? suggestionService.suggestWorkers(term, getCurrentUser().getCompanyId(), true, false)
			: suggestionService.suggestWorkers(term, getCurrentUser().getCompanyId(), false, false);
		// given that this method for blocking is cache-able at DAO layer, we may not see significant performance hit
		// when we eventually move to solr suggest, this will be fast
		final List<Long> blockedUserIds = isSearchInternal
			? Lists.<Long>newArrayList()
			: userService.findAllBlockedUserIdsByBlockingUserId(getCurrentUser().getId());

		for (UserSuggestionDTO suggestion : suggestions) {
			if (!blockedUserIds.contains(suggestion.getId())) {
				response.add(ImmutableMap.of(
					"id", suggestion.getUserNumber(),
					"address", suggestion.getCityStateCountry(),
					"name", suggestion.getValue()
				));
			}
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/suggest_certifications.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getSuggestedCertifications(
		@RequestParam("term") String term,
		Model model) throws Exception {

		List<Map<String, String>> results = certificationService.findAllCertificationFacetsForTypeAheadFilter(term);
		model.addAttribute("response", results);
	}

	private AjaxResponseBuilder finish(String action, ServiceResponseBuilder serviceResponseBuilder) {
		AjaxResponseBuilder ajaxResponseBuilder = new AjaxResponseBuilder();

		if (serviceResponseBuilder.failed()) {
			ajaxResponseBuilder.setSuccessful(false);
			return ajaxResponseBuilder.addMessage(action + " failed.");
		}

		return ajaxResponseBuilder
			.setData(serviceResponseBuilder.getData())
			.addMessage(action + " succeeded.")
			.setSuccessful(true);
	}

	// Build default searchRequest
	private PeopleSearchRequest buildPeopleSearchRequest(UserSearchForm form) {

		Pagination pagination = form.getPagination();
		PeopleSearchRequest searchRequest = new PeopleSearchRequest();
		searchRequest.setUserId(getCurrentUser().getId());
		searchRequest.setPaginationRequest(pagination);

		// Apply filters.
		((PeopleSearchRequest) searchRequest.setLocationFilter(form.getLocationFilter()))
			.setCountryFilter(form.getCountries())
			.setLaneFilter(form.getLaneFilter())
			.setGroupFilter(form.getGroup())
			.setSharedGroupFilter(form.getSharedgroup())
			.setBackgroundScreeningFilter(form.getVerification())
			.setAssessmentFilter(form.getAssessment())
			.setCertificationFilter(form.getCertification())
			.setStateLicenseFilter(form.getLicense())
			.setIndustryFilter(form.getIndustry())
			.setCompanyTypeFilter(form.getCompanytypes())
			.setRatingsFilter(form.getRatings())
			.setSatisfactionRateFilter(form.getSatisfactionRateFilter())
			.setOnTimePercentageFilter(form.getOnTimePercentageFilter())
			.setDeliverableOnTimePercentageFilter(form.getDeliverableOnTimePercentageFilter())
			.setAvatarFilter(form.getAvatar())
			.setCompanyFilter(form.getCompany())
			.setMasqueradeUserId(getCurrentUser().getMasqueradeUserId() != null ? getCurrentUser().getMasqueradeUserId() : 0)
			.setMboFilter(form.getMbo())
			.setKeyword(form.getKeyword());

		Set<LaneType> laneTypes = Sets.newHashSet();
		if (form.getLaneFilter() != null) {
			laneTypes.addAll(form.getLaneFilter());
		}
		marketplaceResultHidingDoorman.welcome(
			new WebGuest(getCurrentUser()),
			new MarketplaceResultHidingRope(
				form.getLaneFilter(),
				laneTypes
			)
		);

		searchRequest.setLaneFilter(laneTypes);
		return searchRequest;
	}

	// Check for special search types and execute search
	public PeopleSearchResponse augmentRequestAndExecuteSearch(String searchType, UserSearchForm form, PeopleSearchRequest searchRequest, HttpServletRequest httpRequest)
		throws SearchException, WorkActionException {
		if (searchType.equals(SearchType.PEOPLE_SEARCH_ASSIGNMENT.toString())) {
			try {
				ExtendedUserDetails user = getCurrentUser();
				// For locked accounts filter initial assignment search results to Lane0 and 1
				if (user.getCompanyIsLocked()) {
					searchRequest.setLaneFilter(Sets.newHashSet(LaneType.LANE_0, LaneType.LANE_1));
				}

				String workNumber = httpRequest.getParameter("work_number");
				AbstractWork abstractWork = workService.findWorkByWorkNumber(workNumber, false);
				WorkResponse response = workResponseBuilder.buildWorkResponse(abstractWork.getId(), user.getId(), WorkRequestInfo.getWorkDetailsEnumSet());
				Work work = response.getWork();
				List<Long> declinedCompanies = vendorService.getDeclinedVendorIdsByWork(work.getId());
				if (declinedCompanies.contains(user.getCompanyId())) {
					declinedCompanies.remove(user.getCompanyId());
				}
				if (!declinedCompanies.isEmpty()) {
					searchRequest.setDeclinedCompanyFilter(Sets.newHashSet(declinedCompanies));
				}
				AssignmentResourceSearchRequest assignmentSearchRequest = new AssignmentResourceSearchRequest()
					.setWorkNumber(workNumber)
					.setRequest(searchRequest)
					.setDescription(Jsoup.parse(work.getDescription()).text())
					.setSkills(work.getDesiredSkills());

				List<WorkContext> workContexts = workService.getWorkContext(abstractWork.getId(), getCurrentUser().getId());
				boolean isDispatcher = workContexts.contains(WorkContext.DISPATCHER);

				if (isDispatcher) {
					assignmentSearchRequest.setWork(work);
				}

				// Apply industry filter and boost for assignment searches.
				if (abstractWork.getIndustry() != null) {
					searchRequest.addToIndustryFilter(abstractWork.getIndustry().getId());
					assignmentSearchRequest.setBoostIndustryId(abstractWork.getIndustry().getId());
				}

				if (StringUtils.isNotEmpty(form.getLimit_country())) {
					searchRequest.setCountryFilter(Sets.newHashSet(form.getLimit_country()));
				}

				boolean isInternal = abstractWork.getPricingStrategyType() != null && abstractWork.getPricingStrategyType().equals(PricingStrategyType.INTERNAL);
				return isInternal || isDispatcher ?
					searchService.searchInternalAssignmentResources(assignmentSearchRequest) :
					searchService.searchAssignmentResources(assignmentSearchRequest);
			} catch (Exception e) {
				logger.error("Failed retrieving user from user service!", e);
				throw new SearchException("Failed to retrieve user " + getCurrentUser().getId() + " from user service", e);
			}

		} else if (searchType.equals(SearchType.PEOPLE_SEARCH_GROUP.toString())) {
			GroupPeopleSearchRequest groupSearchRequest = new GroupPeopleSearchRequest()
				.setGroupId(form.getGroup_id())
				.setRequest(searchRequest);
			return searchService.searchPeopleGroups(groupSearchRequest);

		} else if (searchType.equals(SearchType.PEOPLE_SEARCH_GROUP_MEMBER.toString())) {
			form.setWorkersCompToggle(true);
			form.setGeneralLiabilityToggle(true);
			form.setErrorsAndOmissionsToggle(true);
			form.setAutomobileToggle(true);
			form.setContractorsToggle(true);
			form.setBusinessLiabilityToggle(true);
			form.setCommercialGeneralLiabilityToggle(true);

			searchRequest
				// Status Filters
				.setGroupMemberFilter(form.isMember())
				.setGroupOverrideMemberFilter(form.isMemberoverride())
				.setGroupPendingFilter(form.isPending())
				.setGroupPendingFailedFilter(form.isPendingoverride())
				.setGroupInvitedFilter(form.isInvited())
				.setGroupDeclinedFilter(form.isDeclined())

				// Insurance Filters
				.setWorkersCompCoverageFilter(form.getWorkersCompCoverageFilter())
				.setGeneralLiabilityCoverageFilter(form.getGeneralLiabilityCoverageFilter())
				.setErrorsAndOmissionsCoverageFilter(form.getErrorsAndOmissionsCoverageFilter())
				.setAutomobileCoverageFilter(form.getAutomobileCoverageFilter())
				.setContractorsCoverageFilter(form.getContractorsCoverageFilter())
				.setBusinessLiabilityCoverageFilter(form.getBusinessLiabilityCoverageFilter())
				.setCommercialGeneralLiabilityCoverageFilter(form.getCommercialGeneralLiabilityCoverageFilter())
				.setNoFacetsFlag(form.getNoFacetsFlag());

			if (userHasFeature(getCurrentUser(), Constants.VENDOR_POOLS_FEATURE)) {
				searchRequest.setUserTypeFilter(Collections.<SolrUserType>emptySet());
			}

			GroupPeopleSearchRequest groupSearchRequest = new GroupPeopleSearchRequest()
				.setGroupId(form.getGroup_id())
				.setRequest(searchRequest);
			return searchService.searchGroupMembers(groupSearchRequest);

		} else if (searchType.equals(SearchType.PEOPLE_SEARCH_ASSESSMENT_INVITE.toString())) {
			searchRequest
				.setCurrentAssessmentId(form.getAssessmentId())
				.setInvitedAssessmentFilter(form.getInvitedassessment())
				.setNotInvitedAssessmentFilter(form.getNotinvitedassessment())
				.setPassedAssessmentFilter(form.getPassedassessment())
				.setFailedTestFilter(form.getFailedtest());
			return searchService.searchPeopleForAssessment(searchRequest);

		} else {
			if (hasFeature("searchfilter")) {
				//These toggles need to be depercated in UserSearchForm once we remove legacy requirements
				form.setWorkersCompToggle(true);
				form.setGeneralLiabilityToggle(true);
				form.setErrorsAndOmissionsToggle(true);
				form.setAutomobileToggle(true);
				form.setContractorsToggle(true);
				form.setBusinessLiabilityToggle(true);
				form.setCommercialGeneralLiabilityToggle(true);

				searchRequest
					.setWorkersCompCoverageFilter(form.getWorkersCompCoverageFilter())
					.setGeneralLiabilityCoverageFilter(form.getGeneralLiabilityCoverageFilter())
					.setErrorsAndOmissionsCoverageFilter(form.getErrorsAndOmissionsCoverageFilter())
					.setAutomobileCoverageFilter(form.getAutomobileCoverageFilter())
					.setContractorsCoverageFilter(form.getContractorsCoverageFilter())
					.setBusinessLiabilityCoverageFilter(form.getBusinessLiabilityCoverageFilter())
					.setCommercialGeneralLiabilityCoverageFilter(form.getCommercialGeneralLiabilityCoverageFilter());
			}

			return searchService.searchPeople(searchRequest);
		}
	}

	private void buildFilterResponse(Map<Enum<PeopleFacetResultType>, List<FacetResult>> facets, Map<String, Object> filters, PeopleFacetResultType type, String key) {
		Map<String, String> labels = Maps.newHashMap();
		List<Map<String, Object>> results = Lists.newArrayList();
		List<FacetResult> facetData = facets.get(type);

		if (facets.containsKey(type)) {
			// Additional information that will be needed by some facets
			// Map<FacetId, Map<ObjectNameToBeShown, ObjectToBeAdded>>;
			Map<String, Map<String, Object>> additionalInformation = Maps.newHashMap();

			// Facet is of type GROUP or SHARED_GROUP, we need open memberships as additional information
			if (containsAny(Sets.newHashSet(PeopleFacetResultType.GROUP, PeopleFacetResultType.SHARED_GROUP), type)) {
				ManagedCompanyUserGroupRowPagination pagination = getManagedCompanyUserGroupRowPagination(type, facetData);

				for (final ManagedCompanyUserGroupRow r : pagination.getResults()) {
					Map<String, Object> objectMap = Maps.newHashMap();
					objectMap.put("open_membership", r.isOpenMembership());

					// If shared group, add group company owner and, if the facet's group owner is current user's company, remove that facet
					if (PeopleFacetResultType.SHARED_GROUP.equals(type)) {
						objectMap.put("group_owner", r.getCompanyName());
						if (r.getCompanyId().equals(getCurrentUser().getCompanyId())) {
							FacetResult facetToRemove = new FacetResult();
							for (FacetResult f : facetData) {
								if (f.getFacetId().equals(r.getGroupId().toString())) {
									facetToRemove = f;
								}
							}
							facetData.remove(facetToRemove);
						}
					}
					additionalInformation.put(r.getGroupId().toString(), objectMap);
				}
			}

			for (FacetResult r : facetData) {
				Map<String, Object> objectMap;
				if (PeopleFacetResultType.COUNTRY.equals(type)) {
					String countryName = Country.valueOf(r.getFacetId()).getName();
					objectMap = newObjectMap("id", r.getFacetId(), "name", countryName, "count", r.getFacetCount(), "filter_on", r.isActive());
					labels.put(r.getFacetId(), countryName);
				} else {
					objectMap = newObjectMap("id", r.getFacetId(), "name", r.getFacetName(), "count", r.getFacetCount(), "filter_on", r.isActive());
					labels.put(r.getFacetId(), r.getFacetName());
				}
				// Add all additional information that is needed to the returned result
				Map<String, Object> information = (Map<String, Object>) MapUtils.getObject(additionalInformation, r.getFacetId(), Collections.EMPTY_MAP);
				if (MapUtils.isNotEmpty(information)) {
					for (Map.Entry<String, Object> entry : information.entrySet()) {
						addToObjectMap(objectMap, entry.getKey(), entry.getValue());
					}
				}
				results.add(objectMap);
			}
		}
		filters.put(key, results);

		Map<String, Map<String, String>> labelLookup = (Map<String, Map<String, String>>) filters.get("labels");
		labelLookup.put(key, labels);
	}

	private ManagedCompanyUserGroupRowPagination getManagedCompanyUserGroupRowPagination(
			final PeopleFacetResultType type,
			final List<FacetResult> facetData) {
		final boolean returnAllRows = true;
		final ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination(returnAllRows);
		if (PeopleFacetResultType.GROUP.equals(type)) {
			return groupService.findCompanyGroupsActiveOpenMembership(getCurrentUser().getCompanyId(), pagination);
		}
		final Set<Long> sharedGroupIds = getSharedGroupIds(facetData);
		return groupService.findGroupsActiveOpenMembershipByGroupIds(sharedGroupIds, pagination);
	}

	private Set<Long> getSharedGroupIds(final List<FacetResult> facetData) {
		final List<String> sharedGroupIdsStr = extract(facetData, on(FacetResult.class).getFacetId());
		final Set<Long> sharedGroupIds = Sets.newHashSet();
		for (String id : sharedGroupIdsStr) {
			sharedGroupIds.add(Long.valueOf(id));
		}
		return sharedGroupIds;
	}

	@RequestMapping("/suggest_vendors.json")
	@ResponseBody
	public List<VendorSuggestionDTO> suggestVendor(@RequestParam("term") String query) {
		return suggestionService.suggestVendor(query);
	}

	private boolean isSearchInternalOnly(final UserSearchForm form) {
		return BooleanUtils.isTrue(form.getInternal_only()) || getCurrentUser().isDispatcher() || getCurrentUser().getCompanyIsLocked();
	}

}
