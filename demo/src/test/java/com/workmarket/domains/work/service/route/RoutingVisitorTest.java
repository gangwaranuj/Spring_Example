package com.workmarket.domains.work.service.route;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.WorkGenericNotificationTemplate;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeliveryStatusType;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.AutoRoutingStrategy;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeGroupVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeGroupsAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeWorkAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeWorkVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.PeopleSearchRoutingStrategy;
import com.workmarket.domains.work.model.route.PolymathAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.PolymathVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.Recommendation;
import com.workmarket.domains.work.model.route.RecommendedResource;
import com.workmarket.domains.work.model.route.RoutingStrategySummary;
import com.workmarket.domains.work.model.route.UserRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorSearchRoutingStrategy;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.resource.WorkResourceAddOptions;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.UserType;
import com.workmarket.search.worker.query.model.Worker;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.work.RoutingStrategyCompleteEvent;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.search.user.WorkResourceSearchService;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoutingVisitorTest {

	@Mock private WorkRoutingService workRoutingService;
	@Mock private UserService userService;
	@Mock private WorkRoutingSearchRequestBuilder workRoutingSearchRequestBuilder;
	@Mock private WorkResourceSearchService workResourceSearchService;
	@Mock private WorkBundleService workBundleService;
	@Mock private NotificationService notificationService;
	@Mock private NotificationTemplateFactory notificationTemplateFactory;
	@Mock private MessageSource messageSource;
	@Mock private EventRouter eventRouter;
	@Mock private EventFactory eventFactory;
	@Mock private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Mock private AssignmentResourceSearchRequest assignmentResourceSearchRequest;
	@Mock private Pagination pagination;
	@Mock private RoutingStrategyService routingStrategyService;
	@Mock private WorkService workService;
	@Mock private RoutingStrategyTrackingService routingStrategyTrackingService;
	@Mock private CompanyService companyService;
	@Mock private VendorService vendorService;
	@Mock private LikeGroupsUserRecommender likeGroupsRecommender;
	@Mock private LikeWorkUserRecommender likeWorkUserRecommender;
	@Mock private PolymathUserRecommender polymathUserRecommender;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@Mock private FeatureEvaluator featureEvaluator;
	@Mock private FeatureEntitlementService featureEntitlementService;
	@InjectMocks WorkRoutingVisitor routingVisitor;

	private static final Long WORK_ID = 1L;
	private static final Set<Long> USER_GROUP_IDS = Sets.newHashSet(101L);

	private Work work;
	private User user1, user2, user3, user4, user5;
	private Company workCompany, userCompany;
	private Industry industry;
	private PeopleSearchRequest peopleSearchRequest;
	private PeopleSearchResult peopleSearchResult, peopleSearchResult2, peopleSearchResult3;
	private PeopleSearchResponse peopleSearchResponse;
	private RoutingStrategyCompleteEvent routingStrategyCompleteEvent;
	private Recommendation recommendation;
	private RoutingStrategySummary routingStrategySummary;


	ManageMyWorkMarket companyManageMyWorkMarket = new ManageMyWorkMarket();
	ManageMyWorkMarket manageMyWorkMarket = mock(ManageMyWorkMarket.class);
	Map<WorkAuthorizationResponse, Set<String>> responseSetMap;

	@Before
	public void setUp() throws Exception {
		peopleSearchResponse = new PeopleSearchResponse();
		peopleSearchResult = new PeopleSearchResult().setUserId(1L);
		peopleSearchResult2 = new PeopleSearchResult().setUserId(2L);
		peopleSearchResult3 = new PeopleSearchResult().setUserId(3L);
		peopleSearchResponse.addToResults(peopleSearchResult);
		peopleSearchResponse.addToResults(peopleSearchResult2);
		peopleSearchResponse.addToResults(peopleSearchResult3);

		peopleSearchResult.setLocationPoint(new GeoPoint());
		peopleSearchResult2.setLocationPoint(new GeoPoint());
		peopleSearchResult3.setLocationPoint(new GeoPoint());

		peopleSearchResult.setMaxTravelDistance(60F);
		peopleSearchResult2.setMaxTravelDistance(60F);
		peopleSearchResult3.setMaxTravelDistance(60F);

		peopleSearchResult.setDistance(10D);
		peopleSearchResult2.setDistance(10D);
		peopleSearchResult3.setDistance(10D);

		peopleSearchRequest = new PeopleSearchRequest();
		peopleSearchRequest.addToGroupFilter(1L);
		peopleSearchRequest.setPaginationRequest(pagination);

		industry = new Industry();
		industry.setId(1L);
		industry.setName("Technology");

		workCompany = new Company();
		workCompany.setId(1L);

		userCompany = new Company();
		userCompany.setId(1000L);

		manageMyWorkMarket.setPaymentTermsDays(15);
		manageMyWorkMarket.setPaymentTermsEnabled(true);

		work = new Work();
		work.setId(WORK_ID);
		work.setManageMyWorkMarket(manageMyWorkMarket);
		work.setCompany(workCompany);
		work.setIndustry(industry);
		work.setTitle("Title");
		workCompany.setManageMyWorkMarket(companyManageMyWorkMarket);

		user1 = new User();
		user1.setId(1L);
		user1.setUserNumber("243243244");
		user1.setCompany(userCompany);
		user2 = new User();
		user2.setId(2L);
		user2.setUserNumber("455435");
		user2.setCompany(userCompany);
		user3 = new User();
		user3.setId(3L);
		user3.setUserNumber("6877979");
		user3.setCompany(userCompany);
		user4 = new User();
		user4.setId(4L);
		user4.setUserNumber("2368643");
		user4.setCompany(userCompany);
		user5 = new User();
		user5.setId(5L);
		user5.setUserNumber("1234567");
		user5.setCompany(workCompany);
		work.setBuyer(user5);

		Set<Long> userIds = Sets.newHashSet(user1.getId(), user2.getId(), user3.getId(), user4.getId());
		List<Long> userIdList = Lists.newArrayList(userIds);
		List<String> userNumberList = Lists.newArrayList(user1.getUserNumber(), user2.getUserNumber(), user3.getUserNumber(), user4.getUserNumber());
		ImmutableList<RecommendedResource> resources = ImmutableList.of(
			new RecommendedResource(user1.getId(), user1.getUserNumber(), SolrUserType.VENDOR),
			new RecommendedResource(user2.getId(), user2.getUserNumber(), SolrUserType.WORKER),
			new RecommendedResource(user3.getId(), user3.getUserNumber(), SolrUserType.WORKER),
			new RecommendedResource(user4.getId(), user4.getUserNumber(), SolrUserType.WORKER));

		when(userService.findAllUserIdsByUserNumbers(anyList())).thenReturn(userIds);

		when(userService.findUserById(1L)).thenReturn(user1);
		when(userService.findUserById(2L)).thenReturn(user2);
		when(userService.findUserById(3L)).thenReturn(user3);
		when(userService.findUserById(4L)).thenReturn(user4);

		when(workResourceSearchService.searchWorkersForAutoRouting(any(AssignmentResourceSearchRequest.class))).thenReturn(peopleSearchResponse);
		WorkRoutingResponseSummary responseSummary = new WorkRoutingResponseSummary();
		when(workRoutingService.addToWorkResources(any(Long.class), anyListOf(PeopleSearchResult.class), any(WorkResourceAddOptions.class), anyBoolean())).thenReturn(responseSummary);
		when(workRoutingService.addToWorkResources(any(Long.class), anySetOf(Long.class), any(WorkResourceAddOptions.class), anyBoolean())).thenReturn(responseSummary);
		when(workRoutingService.addToWorkResourcesAsDispatcher(any(String.class), anySetOf(String.class), any(Long.class), anyBoolean())).thenReturn(responseSummary);
		when(workRoutingService.addToWorkResources(any(String.class), anySetOf(String.class), anyBoolean())).thenReturn(responseSummary);
		when(workRoutingService.addToWorkResources(any(Long.class), anySetOf(Long.class), anyBoolean())).thenReturn(responseSummary);
		when(vendorService.inviteVendorsToWork(anySetOf(String.class), anyLong(), anyBoolean(), anyCollectionOf(Long.class))).thenReturn(responseSummary);


		responseSetMap = Maps.newHashMap();
		when(accountRegisterAuthorizationService.authorizeWork(work)).thenReturn(WorkAuthorizationResponse.SUCCEEDED);

		when(workService.findWork(WORK_ID)).thenReturn(work);

		when(messageSource.getMessage(eq("work.routed.failed"), any(Object[].class), any(Locale.class))).thenReturn("work.routed.failed");
		when(messageSource.getMessage("work.routed.worker", null, null)).thenReturn("work.routed.worker");
		when(messageSource.getMessage("work.routed.reason", null, null)).thenReturn("work.routed.reason");
		when(messageSource.getMessage("work.routed.insufficient_funds", null, null)).thenReturn("work.routed.insufficient_funds");
		when(messageSource.getMessage("work.routed.insufficient_budget", null, null)).thenReturn("work.routed.insufficient_budget");
		when(messageSource.getMessage("work.routed.illegal_state", null, null)).thenReturn("work.routed.illegal_state");

		routingStrategyCompleteEvent = mock(RoutingStrategyCompleteEvent.class);
		when(eventFactory.buildRoutingStrategyCompleteEvent(any(AbstractRoutingStrategy.class))).thenReturn(routingStrategyCompleteEvent);

		recommendation = mock(Recommendation.class);
		when(recommendation.getRecommendedResources()).thenReturn(resources);
		when(recommendation.getRecommendedResourceIdsByUserType(any(SolrUserType.class))).thenReturn(userIdList);
		when(recommendation.getRecommendedResourceNumbersByUserType(any(SolrUserType.class))).thenReturn(userNumberList);

		when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
		doCallRealMethod().when(webRequestContextProvider).setWebRequestContext(any(WebRequestContext.class));

		routingStrategySummary = mock(RoutingStrategySummary.class);

		when(featureEntitlementService.hasPercentRolloutFeatureToggle(anyString())).thenReturn(false);
	}

	@Test
	public void executeRoutingStrategy_WithUserRoutingStrategyByUserNumbers_success() throws Exception {
		UserRoutingStrategy routingStrategy = new UserRoutingStrategy();
		routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SCHEDULED));
		routingStrategy.setWork(work);
		routingStrategy.setUserNumbers(Sets.newHashSet("10383949", "6583930"));
		routingVisitor.visit(routingStrategy);
		verify(userService).findAllUserIdsByUserNumbers(anyCollectionOf(String.class));
		assertFalse(routingStrategy.getSummary().hasErrors());
		assertNotNull(routingStrategy.getRoutedOn());
		assertTrue(routingStrategy.getDeliveryStatus().isSent());
	}

	@Test
	public void executeRoutingStrategy_WithUserRoutingStrategyByUserIds_success() throws Exception {
		UserRoutingStrategy routingStrategy = new UserRoutingStrategy();
		routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SCHEDULED));
		routingStrategy.setWork(work);
		routingStrategy.setUserIds(Sets.newHashSet(1L, 2L));
		routingVisitor.visit(routingStrategy);
		verify(userService).findAllUserNumbersByUserIds(anyCollectionOf(Long.class));
		assertFalse(routingStrategy.getSummary().hasErrors());
		assertNotNull(routingStrategy.getRoutedOn());
		assertTrue(routingStrategy.getDeliveryStatus().isSent());
	}

	@Test
	public void executeRoutingStrategy_WithPeopleSearchRoutingStrategy_success() throws Exception {
		PeopleSearchRoutingStrategy routingStrategy = new PeopleSearchRoutingStrategy();
		routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SCHEDULED));
		routingStrategy.setWork(work);
		routingStrategy.setUserNumbers(Sets.newHashSet("4085011", "2063098", "5879784", "1539990"));
		routingVisitor.visit(routingStrategy);
		verify(userService).findAllUserIdsByUserNumbers(anyCollectionOf(String.class));
		assertFalse(routingStrategy.getSummary().hasErrors());
		assertNotNull(routingStrategy.getRoutedOn());
		assertTrue(routingStrategy.getDeliveryStatus().isSent());
	}

	@Test
	public void executeRoutingStrategy_WithVendorSearchRoutingStrategy_success() throws Exception {
		VendorSearchRoutingStrategy routingStrategy = new VendorSearchRoutingStrategy();
		routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SCHEDULED));
		routingStrategy.setWork(work);
		routingStrategy.setCompanyNumbers(Sets.newHashSet("4085011", "2063098", "5879784", "1539990"));
		routingVisitor.visit(routingStrategy);
		verify(companyService).findCompanyIdentitiesByCompanyNumbers(anyCollectionOf(String.class));
		assertFalse(routingStrategy.getSummary().hasErrors());
		assertNotNull(routingStrategy.getRoutedOn());
		assertTrue(routingStrategy.getDeliveryStatus().isSent());
	}

	@Test
	public void executeRoutingStrategy_WithVendorRoutingStrategy_success() throws Exception {
		VendorRoutingStrategy routingStrategy = new VendorRoutingStrategy();
		routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SCHEDULED));
		routingStrategy.setWork(work);
		routingStrategy.setCompanyIds(Sets.newHashSet(1L, 2L, 3L, 4L, 5L));
		routingVisitor.visit(routingStrategy);
		verify(companyService).findCompanyNumbersFromCompanyIds(anyCollectionOf(Long.class));
		assertFalse(routingStrategy.getSummary().hasErrors());
		assertNotNull(routingStrategy.getRoutedOn());
		assertTrue(routingStrategy.getDeliveryStatus().isSent());
	}

	@Test
	public void executeRoutingStrategy_WithAutoRoutingStrategy_success() throws Exception {
		AutoRoutingStrategy routingStrategy = new AutoRoutingStrategy();
		routingStrategy.setWork(work);

		routingVisitor.visit(routingStrategy);
		verify(eventRouter).sendEvent(any(RoutingStrategyCompleteEvent.class));
	}

	@Test
	public void executeRoutingStrategy_WithLikeGroupsAutoRoutingStrategy_success() throws Exception {
		LikeGroupVendorRoutingStrategy likeGroupVendorRoutingStrategy = mock(LikeGroupVendorRoutingStrategy.class);
		when(routingStrategyService.addLikeGroupVendorRoutingStrategy(anyLong())).thenReturn(likeGroupVendorRoutingStrategy);
		when(likeGroupVendorRoutingStrategy.getWork()).thenReturn(work);
		when(likeGroupVendorRoutingStrategy.getSummary()).thenReturn(routingStrategySummary);
		when(likeGroupsRecommender.recommend(any(Work.class), anyBoolean())).thenReturn(recommendation);

		LikeGroupsAutoRoutingStrategy routingStrategy = new LikeGroupsAutoRoutingStrategy();
		routingStrategy.setWork(work);

		routingVisitor.visit(routingStrategy);
		verify(routingStrategyService).addLikeGroupVendorRoutingStrategy(work.getId());
		verify(eventRouter, times(2)).sendEvent(routingStrategyCompleteEvent);
	}

	@Test
	public void executeRoutingStrategy_WithLikeWorkAutoRoutingStrategy_success() throws Exception {
		LikeWorkVendorRoutingStrategy likeWorkVendorRoutingStrategy = mock(LikeWorkVendorRoutingStrategy.class);
		when(routingStrategyService.addLikeWorkVendorRoutingStrategy(anyLong())).thenReturn(likeWorkVendorRoutingStrategy);
		when(likeWorkVendorRoutingStrategy.getWork()).thenReturn(work);
		when(likeWorkVendorRoutingStrategy.getSummary()).thenReturn(routingStrategySummary);
		when(likeWorkUserRecommender.recommend(any(Work.class), anyBoolean())).thenReturn(recommendation);

		LikeWorkAutoRoutingStrategy routingStrategy = new LikeWorkAutoRoutingStrategy();
		routingStrategy.setWork(work);

		routingVisitor.visit(routingStrategy);
		verify(routingStrategyService).addLikeWorkVendorRoutingStrategy(work.getId());
		verify(eventRouter, times(2)).sendEvent(routingStrategyCompleteEvent);
	}

	@Test
	public void executeRoutingStrategy_WithPolymathAutoRoutingStrategy_success() throws Exception {
		PolymathVendorRoutingStrategy polymathVendorRoutingStrategy = mock(PolymathVendorRoutingStrategy.class);
		when(routingStrategyService.addPolymathVendorRoutingStrategy(anyLong())).thenReturn(polymathVendorRoutingStrategy);
		when(polymathVendorRoutingStrategy.getWork()).thenReturn(work);
		when(polymathVendorRoutingStrategy.getSummary()).thenReturn(routingStrategySummary);
		when(polymathUserRecommender.recommend(any(Work.class), anyBoolean())).thenReturn(recommendation);

		PolymathAutoRoutingStrategy routingStrategy = new PolymathAutoRoutingStrategy();
		routingStrategy.setWork(work);

		routingVisitor.visit(routingStrategy);
		verify(routingStrategyService).addPolymathVendorRoutingStrategy(work.getId());
		verify(eventRouter, times(2)).sendEvent(routingStrategyCompleteEvent);
	}

	@Test
	public void executeRoutingStrategy_WithLikeGroupVendorRoutingStrategy_success() throws Exception {
		LikeGroupVendorRoutingStrategy routingStrategy = mock(LikeGroupVendorRoutingStrategy.class);
		routingVisitor.visit(routingStrategy);
		verify(eventRouter, times(0)).sendEvent(routingStrategyCompleteEvent);
	}
	@Test
	public void executeRoutingStrategy_WithLikeWorkVendorRoutingStrategy_success() throws Exception {
		LikeWorkVendorRoutingStrategy routingStrategy = mock(LikeWorkVendorRoutingStrategy.class);
		routingVisitor.visit(routingStrategy);
		verify(eventRouter, times(0)).sendEvent(routingStrategyCompleteEvent);
	}

	@Test
	public void executeRoutingStrategy_WithPolymathVendorRoutingStrategy_success() throws Exception {
		PolymathVendorRoutingStrategy routingStrategy = mock(PolymathVendorRoutingStrategy.class);
		routingVisitor.visit(routingStrategy);
		verify(eventRouter, times(0)).sendEvent(routingStrategyCompleteEvent);
	}

	@Test
	public void executeGroupRoutingStrategy_WithNoWorkers_DoesNotAuthorizeTransaction() throws Exception {
		peopleSearchResult.setDistance(100D);
		peopleSearchResult2.setDistance(100D);
		peopleSearchResult3.setDistance(100D);
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.DRAFT));
		GroupRoutingStrategy routingStrategy = new GroupRoutingStrategy();
		routingStrategy.setWork(work);
		when(workResourceSearchService.searchWorkersForAutoRouting(any(AssignmentResourceSearchRequest.class))).thenReturn(peopleSearchResponse);
		when(workRoutingSearchRequestBuilder.build(routingStrategy)).thenReturn(assignmentResourceSearchRequest);
		when(assignmentResourceSearchRequest.getRequest()).thenReturn(peopleSearchRequest);
		when(messageSource.getMessage(eq("work.routed.failed"), any(Object[].class), any(Locale.class))).thenReturn("work.routed.failed");

		routingVisitor.visitSearchBasedRoutingStrategy(routingStrategy);

		verify(accountRegisterAuthorizationService, never()).authorizeWork(work);
	}

	@Test
	public void executeGroupRoutingStrategyNewSearch_WithNoWorkers_DoesNotAuthorizeTransaction() throws Exception {
		FindWorkerCriteria findWorkerCriteria = mock(FindWorkerCriteria.class);
		Long offset = 0L;
		Long limit = 300L;
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.DRAFT));
		GroupRoutingStrategy routingStrategy = new GroupRoutingStrategy();
		routingStrategy.setWork(work);
		routingStrategy.setUserGroups(USER_GROUP_IDS);
		when(workRoutingSearchRequestBuilder.buildFindWorkerCriteriaForGroupRouting(routingStrategy)).thenReturn(findWorkerCriteria);
		when(workResourceSearchService.searchWorkersForGroupRouting(findWorkerCriteria, offset, limit, webRequestContextProvider.getRequestContext()))
			.thenReturn(Lists.<Worker>newArrayList());
		when(messageSource.getMessage(eq("work.routed.failed"), any(Object[].class), any(Locale.class))).thenReturn("work.routed.failed");

		routingVisitor.visitNewSearchBasedGroupRoutingStrategy(routingStrategy);

		verify(accountRegisterAuthorizationService, never()).authorizeWork(work);
	}

	@Test
	public void executeGroupRoutingStrategy_WithWorkers_AuthorizesTransaction() throws Exception {
		GroupRoutingStrategy routingStrategy = new GroupRoutingStrategy();
		routingStrategy.setWork(work);
		when(workResourceSearchService.searchWorkersForAutoRouting(any(AssignmentResourceSearchRequest.class))).thenReturn(peopleSearchResponse);
		when(workRoutingSearchRequestBuilder.build(routingStrategy)).thenReturn(assignmentResourceSearchRequest);
		when(assignmentResourceSearchRequest.getRequest()).thenReturn(peopleSearchRequest);

		routingVisitor.visitSearchBasedRoutingStrategy(routingStrategy);

		verify(accountRegisterAuthorizationService).authorizeWork(work);
	}

	@Test
	public void executeGroupRoutingStrategyNewSearch_WithWorkers_AuthorizesTransaction() throws Exception {
		FindWorkerCriteria findWorkerCriteria = mock(FindWorkerCriteria.class);
		Long offset = 0L;
		Long limit = 300L;
		Worker worker = new Worker("1", "uuid", UserType.WORKER.getUserTypeCode(), 1.0, null);
		List<Worker> workers = Lists.newArrayList(worker);
		GroupRoutingStrategy routingStrategy = new GroupRoutingStrategy();
		routingStrategy.setWork(work);
		routingStrategy.setUserGroups(USER_GROUP_IDS);
		when(workRoutingSearchRequestBuilder.buildFindWorkerCriteriaForGroupRouting(routingStrategy)).thenReturn(findWorkerCriteria);
		when(workResourceSearchService.searchWorkersForGroupRouting(findWorkerCriteria, offset, limit, webRequestContextProvider.getRequestContext()))
			.thenReturn(workers);
		when(workResourceSearchService.searchWorkersForGroupRouting(findWorkerCriteria, 1L, limit, webRequestContextProvider.getRequestContext()))
			.thenReturn(Lists.<Worker>newArrayList());

		routingVisitor.visitNewSearchBasedGroupRoutingStrategy(routingStrategy);

		verify(accountRegisterAuthorizationService, times(1)).authorizeWork(work);
	}

	@Test
	public void executeGroupRoutingStrategy_FeatureToggleOff() throws Exception {
		GroupRoutingStrategy routingStrategy = new GroupRoutingStrategy();
		routingStrategy.setWork(work);
		when(featureEvaluator.hasGlobalFeature(anyString())).thenReturn(false);
		when(workResourceSearchService.searchWorkersForAutoRouting(any(AssignmentResourceSearchRequest.class))).thenReturn(peopleSearchResponse);
		when(workRoutingSearchRequestBuilder.build(routingStrategy)).thenReturn(assignmentResourceSearchRequest);
		when(assignmentResourceSearchRequest.getRequest()).thenReturn(peopleSearchRequest);

		routingVisitor.visit(routingStrategy);

		verify(workResourceSearchService, times(1)).searchWorkersForAutoRouting(any(AssignmentResourceSearchRequest.class));
		verify(workResourceSearchService, never()).searchWorkersForGroupRouting(any(FindWorkerCriteria.class), anyLong(), anyLong(), any(RequestContext.class));
	}

	@Test
	public void executeGroupRoutingStrategy_FeatureToggleOn() throws Exception {
		GroupRoutingStrategy routingStrategy = new GroupRoutingStrategy();
		routingStrategy.setWork(work);
		routingStrategy.setUserGroups(USER_GROUP_IDS);
		when(featureEvaluator.hasGlobalFeature(anyString())).thenReturn(true);
		when(workResourceSearchService.searchWorkersForGroupRouting(any(FindWorkerCriteria.class), anyLong(), anyLong(), any(RequestContext.class)))
			.thenReturn(Lists.<Worker>newArrayList());

		routingVisitor.visit(routingStrategy);

		verify(workResourceSearchService, times(1)).searchWorkersForGroupRouting(any(FindWorkerCriteria.class), anyLong(), anyLong(), any(RequestContext.class));
		verify(workResourceSearchService, never()).searchWorkersForAutoRouting(any(AssignmentResourceSearchRequest.class));
	}

	@Test
	public void populateRoutingStrategySummary_success() throws Exception {
		WorkRoutingResponseSummary workRoutingResponseSummary = mock(WorkRoutingResponseSummary.class);
		Map<WorkAuthorizationResponse, Set<String>> responseSetMap = Maps.newHashMap();
		responseSetMap.put(WorkAuthorizationResponse.SUCCEEDED, Sets.newHashSet("2324", "2343244", "78999"));
		responseSetMap.put(WorkAuthorizationResponse.INVALID_USER, Sets.newHashSet("6282", "9284625"));

		when(workRoutingResponseSummary.getResponse()).thenReturn(responseSetMap);

		AbstractRoutingStrategy routingStrategy = new UserRoutingStrategy();
		routingStrategy.setWork(new Work());
		
		routingVisitor.populateRoutingStrategySummary(WorkAuthorizationResponse.SUCCEEDED, workRoutingResponseSummary, routingStrategy, work);
		
		verify(eventRouter, times(1)).sendEvent(any(Event.class));
		assertNotNull(routingStrategy.getSummary());
		assertTrue(routingStrategy.getSummary().hasErrors());
		assertTrue(routingStrategy.getSummary().getFailedIllegalState() == 2);
		assertTrue(routingStrategy.getSummary().getSent() == 3);
	}

	private void populateRoutingStrategySummaryVerifier(Map<WorkAuthorizationResponse, Set<String>> responseSetMap, String expectedMsg) {
		WorkRoutingResponseSummary workRoutingResponseSummary = mock(WorkRoutingResponseSummary.class);
		AbstractRoutingStrategy routingStrategy = mock(AbstractRoutingStrategy.class);
		RoutingStrategySummary routingStrategySummary = mock(RoutingStrategySummary.class);
		Work work = mock(Work.class);
		User buyer = mock(User.class);

		when(workRoutingResponseSummary.getResponse()).thenReturn(responseSetMap);
		when(work.getTitle()).thenReturn("My Work");
		when(work.getBuyer()).thenReturn(buyer);
		when(routingStrategy.getWork()).thenReturn(work);

		when(routingStrategySummary.hasErrors()).thenReturn(true);
		when(routingStrategy.getSummary()).thenReturn(routingStrategySummary);

		routingVisitor.populateRoutingStrategySummary(WorkAuthorizationResponse.SUCCEEDED, workRoutingResponseSummary, routingStrategy, work);
		verify(notificationTemplateFactory).buildWorkGenericNotificationTemplate(anyLong(), any(Work.class), eq(expectedMsg));
		verify(notificationService).sendNotification(any(WorkGenericNotificationTemplate.class));
	}

	@Test
	public void populateRoutingStrategySummary_SingleFailureSingleWorker() throws Exception {
		responseSetMap.put(WorkAuthorizationResponse.INTERNAL_PRICING, Sets.newHashSet("2324"));

		String expectedMsg = "work.routed.failed work.routed.worker: work.routed.reason (work.routed.illegal_state).";
		populateRoutingStrategySummaryVerifier(responseSetMap, expectedMsg);
		verify(messageSource).getMessage("work.routed.failed", new Object[]{"My Work", 1}, null);
		verify(messageSource).getMessage("work.routed.illegal_state", null, null);
	}

	@Test
	public void populateRoutingStrategySummary_SingleFailureMultipleWorkers() throws Exception {
		responseSetMap.put(WorkAuthorizationResponse.INSUFFICIENT_FUNDS, Sets.newHashSet("2324", "2343244", "78999"));

		String expectedMsg = "work.routed.failed work.routed.workers: work.routed.reason (work.routed.insufficient_funds).";
		populateRoutingStrategySummaryVerifier(responseSetMap, expectedMsg);

		verify(messageSource).getMessage("work.routed.failed", new Object[]{"My Work", 3}, null);
		verify(messageSource).getMessage("work.routed.insufficient_funds", null, null);
	}

	@Test
	public void populateRoutingStrategySummary_MultipleFailures() throws Exception {
		responseSetMap.put(WorkAuthorizationResponse.INSUFFICIENT_FUNDS, Sets.newHashSet("2324", "2343244", "121212"));
		responseSetMap.put(WorkAuthorizationResponse.INSUFFICIENT_BUDGET, Sets.newHashSet("78999"));

		String expectedMsg = "work.routed.failed work.routed.workers: work.routed.reasons (work.routed.insufficient_budget: 1; work.routed.insufficient_funds: 3).";
		populateRoutingStrategySummaryVerifier(responseSetMap, expectedMsg);
		verify(messageSource).getMessage("work.routed.failed", new Object[]{"My Work", 4}, null);
		verify(messageSource).getMessage("work.routed.insufficient_funds", null, null);
		verify(messageSource).getMessage("work.routed.insufficient_budget", null, null);
	}

	@Test
	public void populateRoutingStrategySummary_SingleSuccessSingleFailureSingleWorker() throws Exception {
		responseSetMap.put(WorkAuthorizationResponse.SUCCEEDED, Sets.newHashSet("2323"));
		responseSetMap.put(WorkAuthorizationResponse.INSUFFICIENT_FUNDS, Sets.newHashSet("2324"));

		String expectedMsg = "work.routed.failed work.routed.worker: work.routed.reason (work.routed.insufficient_funds).";
		populateRoutingStrategySummaryVerifier(responseSetMap, expectedMsg);
		verify(messageSource).getMessage("work.routed.failed", new Object[]{"My Work", 1}, null);
	}

	@Test
	public void populateRoutingStrategySummary_SingleSuccessSingleFailureMultipleWorkers() throws Exception {
		responseSetMap.put(WorkAuthorizationResponse.SUCCEEDED, Sets.newHashSet("2323"));
		responseSetMap.put(WorkAuthorizationResponse.INSUFFICIENT_FUNDS, Sets.newHashSet("2324", "2325"));

		String expectedMsg = "work.routed.failed work.routed.workers: work.routed.reason (work.routed.insufficient_funds).";
		populateRoutingStrategySummaryVerifier(responseSetMap, expectedMsg);
		verify(messageSource).getMessage("work.routed.failed", new Object[]{"My Work", 2}, null);
	}
}
