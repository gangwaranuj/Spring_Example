package com.workmarket.web.controllers.search;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.model.route.PeopleSearchRoutingStrategy;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.wrapper.ValidateWorkResponse;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.web.cachebusting.CacheBusterServiceImpl;
import com.workmarket.thrift.core.Status;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.controllers.BaseControllerUnitTest;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.WorkBundleValidationHelper;
import com.workmarket.web.models.MessageBundle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.View;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class CartControllerTest extends BaseControllerUnitTest {
	private static final String WORK_NUMBER = "12345";

	protected static class PushToAssignmentBuilder {
		public static MockHttpServletRequestBuilder create() {
			return MockMvcRequestBuilders.post("/search/cart/push_to_assignment")
				.param("id", WORK_NUMBER);
		}
	}

	@Mock private View mockView;
	@Mock private CacheBusterServiceImpl cacheBusterServiceImpl;
	@Mock private MessageBundleHelper messageHelper;
	@Mock private TWorkFacadeService tWorkFacadeService;
	@Mock private WorkBundleService workBundleService;
	@Mock private EventRouter eventRouter;
	@Mock private WorkSearchService workSearchService;
	@Mock private UserGroupService groupService;
	@Mock private WorkBundleValidationHelper workBundleValidationHelper;
	@Mock private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Mock private RoutingStrategyService routingStrategyService;

	@InjectMocks CartController controller;

	private MockMvc mockMvc;

	private static String CACHE_BUSTER_HASH = "hash";

	@Mock private MessageBundle messageBundle;
	@Mock private WorkResponse workResponse;
	@Mock private PricingStrategy pricingStrategy;
	@Mock private Work work;
	@Mock private WorkRoutingResponseSummary workRoutingResponseSummary;
	@Mock private PeopleSearchRoutingStrategy peopleSearchRoutingStrategy;

	private Set<AuthorizationContext> authorizationContexts = Sets.newHashSet();

	private User buyer;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		initController(controller);

		when(cacheBusterServiceImpl.getMediaPrefix()).thenReturn(CACHE_BUSTER_HASH);
		when(tWorkFacadeService.findWork(any(WorkRequest.class))).thenReturn(workResponse);
		when(routingStrategyService.addPeopleSearchRoutingStrategy(any(Long.class), anySetOf(String.class), any(Long.class), any(Boolean.class))).thenReturn(peopleSearchRoutingStrategy);
		when(peopleSearchRoutingStrategy.getWorkRoutingResponseSummary()).thenReturn(workRoutingResponseSummary);
		when(messageHelper.newBundle()).thenReturn(messageBundle);


		buyer = new User();
		buyer.setUserNumber("123456789");
		work.setBuyer(buyer);
		when(work.getBuyer()).thenReturn(buyer);
		Status status = mock(Status.class);
		when(status.getCode()).thenReturn("blerp");
		when(work.getStatus()).thenReturn(status);

		mockMvc = standaloneSetup(controller)
			.setSingleView(mockView)
			.build();
	}

	@Test
	public void pushToAssignment_WorkActionException() throws Exception {
		when(tWorkFacadeService.findWork(any(WorkRequest.class))).thenThrow(new WorkActionException());

		mockMvc.perform(PushToAssignmentBuilder.create())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.successful", is(false)))
			.andExpect(jsonPath("$.redirect", is("/assignments")));

		verify(messageHelper).addError(any(MessageBundle.class), eq("search.cart.push.assignment.invalid_work"));
	}

	@Test
	public void pushToAssignment_NotAdmin() throws Exception {
		mockMvc.perform(PushToAssignmentBuilder.create())
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void pushToAssignment_CompanyLocked_NotInternal() throws Exception {
		authorizationContexts.add(AuthorizationContext.ADMIN);
		when(workResponse.getAuthorizationContexts()).thenReturn(authorizationContexts);
		securityContextFacade.getCurrentUser().setCompanyIsLocked(true);
		when(workResponse.getWork()).thenReturn(work);
		when(work.getPricing()).thenReturn(pricingStrategy);
		when(pricingStrategy.getType()).thenReturn(PricingStrategyType.FLAT);

		mockMvc.perform(PushToAssignmentBuilder.create())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.successful", is(false)));

		verify(messageHelper).addError(any(MessageBundle.class), eq("search.cart.push.assignment.locked"));
	}

	@Test
	public void pushToAssignment_NoResources() throws Exception {
		authorizationContexts.add(AuthorizationContext.ADMIN);
		when(workResponse.getAuthorizationContexts()).thenReturn(authorizationContexts);
		when(workResponse.getWork()).thenReturn(work);
		when(work.getPricing()).thenReturn(pricingStrategy);
		when(pricingStrategy.getType()).thenReturn(PricingStrategyType.FLAT);

		securityContextFacade.getCurrentUser().setCompanyIsLocked(false);

		mockMvc.perform(PushToAssignmentBuilder.create())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.successful", is(false)))
			.andExpect(jsonPath("$.redirect", is("/assignments")));

		verify(messageHelper).addError(any(MessageBundle.class), eq("search.cart.push.assignment.empty_cart"));
	}

	@Test
	public void pushToAssignment_IsWorkBundle_NotReadyToSend() throws Exception {
		authorizationContexts.add(AuthorizationContext.ADMIN);
		when(workResponse.getAuthorizationContexts()).thenReturn(authorizationContexts);
		when(workResponse.getWork()).thenReturn(work);
		when(work.getPricing()).thenReturn(pricingStrategy);
		when(pricingStrategy.getType()).thenReturn(PricingStrategyType.FLAT);
		when(work.getId()).thenReturn(WORK_ID);

		securityContextFacade.getCurrentUser().setCompanyIsLocked(false);

		Multimap<String, ValidateWorkResponse> validationResponses = ArrayListMultimap.create();
		Collection<ValidateWorkResponse> validateWorkResponses = Lists.newArrayList();
		validateWorkResponses.add(ValidateWorkResponse.fail());
		validationResponses.putAll(WorkBundleValidationHelper.VALIDATION_ERRORS, validateWorkResponses);
		when(workBundleValidationHelper.readyToSend(eq("12345"), eq(USER_ID), any(MessageBundle.class))).thenReturn(validationResponses);

		when(workResponse.isWorkBundle()).thenReturn(true);

		mockMvc.perform(PushToAssignmentBuilder.create()
			.param("selected[]", "123")
			.param("selected[]", "456")
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.successful", is(false)))
		.andExpect(jsonPath("$.redirect", is("/assignments/details/" + WORK_NUMBER)));

		verify(messageHelper).addError(any(MessageBundle.class), eq("assignment_bundle.add.fail.no_valid_work"));
	}

	private enum SUCCESSFUL { YES, NO }
	private enum IS_BUNDLE { YES, NO }

	private void setupSending(WorkAuthorizationResponse type, SUCCESSFUL successful, IS_BUNDLE isBundle) throws Exception {
		authorizationContexts.add(AuthorizationContext.ADMIN);
		when(workResponse.getAuthorizationContexts()).thenReturn(authorizationContexts);
		when(workResponse.getWork()).thenReturn(work);
		when(work.getPricing()).thenReturn(pricingStrategy);
		when(pricingStrategy.getType()).thenReturn(PricingStrategyType.FLAT);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getWorkNumber()).thenReturn(WORK_NUMBER);

		securityContextFacade.getCurrentUser().setCompanyIsLocked(false);

		Multimap<String, ValidateWorkResponse> validationResponses = ArrayListMultimap.create();
		when(workBundleValidationHelper.readyToSend(eq("12345"), eq(USER_ID), any(MessageBundle.class))).thenReturn(validationResponses);

		Map<Long, Map<WorkAuthorizationResponse, Set<String>>> bundleSendResults = Maps.newHashMap();
		Map<WorkAuthorizationResponse, Set<String>> resultOne = Maps.newHashMap();
		resultOne.put(type, Sets.newHashSet("123", "456"));
		bundleSendResults.put(WORK_ID, resultOne);

		when(workResponse.isWorkBundle()).thenReturn(IS_BUNDLE.YES.equals(isBundle));

		// This only gets hit if it's a bundle and success or if it's not a bundle
		WorkRoutingResponseSummary workRoutingResponseSummary = mock(WorkRoutingResponseSummary.class);
		Map<WorkAuthorizationResponse, Set<String>> userMap = Maps.newHashMap();
		userMap.put(type, Sets.newHashSet("123", "456"));
		when(workRoutingResponseSummary.getResponse()).thenReturn(userMap);
		when(accountRegisterAuthorizationService.authorizeWork(WORK_ID)).thenReturn(WorkAuthorizationResponse.SUCCEEDED);
		when(routingStrategyService.addPeopleSearchRoutingStrategy(any(Long.class), anySetOf(String.class), any(Long.class), any(Boolean.class))).thenReturn(peopleSearchRoutingStrategy);
		when(peopleSearchRoutingStrategy.getWorkRoutingResponseSummary()).thenReturn(workRoutingResponseSummary);

		mockMvc.perform(PushToAssignmentBuilder.create()
			.param("selected[]", "123")
			.param("selected[]", "456")
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.successful", is(SUCCESSFUL.YES.equals(successful))))
		.andExpect(jsonPath("$.redirect", is("/assignments/details/" + WORK_NUMBER)));
	}

	@Test
	public void pushToAssignment_IsWorkBundle_InsufficientSpendLimit() throws Exception {
		setupSending(WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT, SUCCESSFUL.NO, IS_BUNDLE.YES);

		String message = "search.cart.push.assignment.insufficient_spend_limit";
		verify(messageHelper).addError(any(MessageBundle.class), eq(message), eq(""));
	}

	@Test
	public void pushToAssignment_IsWorkBundle_InvalidIndustry() throws Exception {
		setupSending(WorkAuthorizationResponse.INVALID_INDUSTRY_FOR_RESOURCE, SUCCESSFUL.NO, IS_BUNDLE.YES);

		String message = "search.cart.push.assignment.invalid_industry_for_resource";
		verify(messageHelper).addError(any(MessageBundle.class), eq(message), eq(2));
	}

	@Test
	public void pushToAssignment_IsWorkBundle_FailOther() throws Exception {
		setupSending(WorkAuthorizationResponse.INTERNAL_PRICING, SUCCESSFUL.NO, IS_BUNDLE.YES);

		String message = "search.cart.push.assignment.internal_pricing";
		verify(messageHelper).addError(any(MessageBundle.class), eq(message), eq(2), eq("workers"), eq(WORK_NUMBER));
	}

	@Test
	public void pushToAssignment_IsWorkBundle_Success() throws Exception {
		setupSending(WorkAuthorizationResponse.SUCCEEDED, SUCCESSFUL.YES, IS_BUNDLE.YES);
	}

	@Test
	public void pushToAssignment_IsNotWorkBundle_Success() throws Exception {
		setupSending(WorkAuthorizationResponse.SUCCEEDED, SUCCESSFUL.YES, IS_BUNDLE.NO);
	}

	@Test
	public void pushToAssignment_MaxResourcesExceeded() throws Exception {
		authorizationContexts.add(AuthorizationContext.ADMIN);
		when(workResponse.getAuthorizationContexts()).thenReturn(authorizationContexts);
		when(workResponse.getWork()).thenReturn(work);
		when(work.getPricing()).thenReturn(pricingStrategy);
		when(pricingStrategy.getType()).thenReturn(PricingStrategyType.FLAT);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getWorkNumber()).thenReturn(WORK_NUMBER);

		securityContextFacade.getCurrentUser().setCompanyIsLocked(false);

		when(workResponse.isWorkBundle()).thenReturn(false);

		WorkRoutingResponseSummary workRoutingResponseSummary = mock(WorkRoutingResponseSummary.class);
		Map<WorkAuthorizationResponse, Set<String>> userMap = Maps.newHashMap();
		userMap.put(WorkAuthorizationResponse.MAX_RESOURCES_EXCEEDED, Sets.newHashSet("123"));
		userMap.put(WorkAuthorizationResponse.SUCCEEDED, Sets.newHashSet("456"));
		when(workRoutingResponseSummary.getResponse()).thenReturn(userMap);
		when(accountRegisterAuthorizationService.authorizeWork(WORK_ID)).thenReturn(WorkAuthorizationResponse.SUCCEEDED);
		when(routingStrategyService.addPeopleSearchRoutingStrategy(any(Long.class), anySetOf(String.class), any(Long.class), any(Boolean.class))).thenReturn(peopleSearchRoutingStrategy);
		when(peopleSearchRoutingStrategy.getWorkRoutingResponseSummary()).thenReturn(workRoutingResponseSummary);

		mockMvc
			.perform(PushToAssignmentBuilder.create()
				.param("selected[]", "123")
				.param("selected[]", "456")
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.successful", is(true)))
		.andExpect(jsonPath("$.redirect", is("/assignments/contact/" + WORK_NUMBER + "?empty=0")));
	}
}
