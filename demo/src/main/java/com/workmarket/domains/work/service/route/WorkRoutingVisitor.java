package com.workmarket.domains.work.service.route;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToWorkType;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.WorkGenericNotificationTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.DeliveryStatusType;
import com.workmarket.domains.model.RoutingStrategyTracking;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.AutoRoutingStrategy;
import com.workmarket.domains.work.model.route.Explain;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeGroupVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeGroupsAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeWorkAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeWorkVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.PeopleSearchRoutingStrategy;
import com.workmarket.domains.work.model.route.PolymathAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.PolymathVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.Recommendation;
import com.workmarket.domains.work.model.route.RoutingStrategySummary;
import com.workmarket.domains.work.model.route.RoutingVisitor;
import com.workmarket.domains.work.model.route.UserRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorSearchRoutingStrategy;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.resource.WorkResourceAddOptions;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.UserType;
import com.workmarket.search.worker.query.model.Worker;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.work.WorkBundleRoutingEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.recommendation.RecommendationService;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.search.user.WorkResourceSearchService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.MapUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
@Scope(value = "prototype")
public class WorkRoutingVisitor implements RoutingVisitor {

	private static final Logger logger = LoggerFactory.getLogger(WorkRoutingVisitor.class);

	private static final Set<String> AUTO_ROUTING_TYPES = ImmutableSet.of(
		LikeGroupsAutoRoutingStrategy.LIKE_GROUPS_AUTO_ROUTING_STRATEGY,
		LikeGroupVendorRoutingStrategy.LIKE_GROUP_VENDOR_ROUTING_STRATEGY,
		LikeWorkAutoRoutingStrategy.LIKE_WORK_AUTO_ROUTING_STRATEGY,
		LikeWorkVendorRoutingStrategy.LIKE_WORK_VENDOR_ROUTING_STRATEGY,
		PolymathAutoRoutingStrategy.POLYMATH_AUTO_ROUTING_STRATEGY,
		PolymathVendorRoutingStrategy.POLYMATH_VENDOR_ROUTING_STRATEGY);

	@Autowired private WorkRoutingService workRoutingService;
	@Autowired private UserService userService;
	@Autowired private WorkRoutingSearchRequestBuilder workRoutingSearchRequestBuilder;
	@Autowired private WorkResourceSearchService workResourceSearchService;
	@Autowired private NotificationService notificationService;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private MessageSource messageSource;
	@Autowired private EventRouter eventRouter;
	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired private WorkService workService;
	@Autowired private EventFactory eventFactory;
	@Autowired private RoutingStrategyService routingStrategyService;
	@Autowired private RoutingStrategyTrackingService routingStrategyTrackingService;
	@Autowired private LikeGroupsUserRecommender likeGroupsUserRecommender;
	@Autowired private LikeWorkUserRecommender likeWorkUserRecommender;
	@Autowired private PolymathUserRecommender polymathUserRecommender;
	@Autowired private VendorService vendorService;
	@Autowired private CompanyService companyService;
	@Autowired private PricingService pricingService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private RecommendationService recommendationService;
	@Autowired private FeatureEntitlementService featureEntitlementService;

	@Override
	public void visit(AutoRoutingStrategy routingStrategy) {
		logger.info("Starting WorkSend v2");
		routingStrategy.setRoutedOn(DateUtilities.getCalendarNow());
		routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SENT));
		routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
		eventRouter.sendEvent(eventFactory.buildRoutingStrategyCompleteEvent(routingStrategy));
	}

	@Override
	public void visit(UserRoutingStrategy routingStrategy) {
		visitUserBasedRoutingStrategy(routingStrategy);
		logger.info("user routing strategy done");
	}

	@Override
	public void visit(VendorRoutingStrategy routingStrategy) {
		logger.debug("vendor routing strategy");
		visitVendorBasedRoutingStrategy(routingStrategy);
		logger.info("vendor routing strategy complete");
	}

	@Override
	public void visit(VendorSearchRoutingStrategy routingStrategy) {
		logger.debug("vendor search routing strategy");
		visitVendorBasedRoutingStrategy(routingStrategy);
		logger.info("vendor search routing strategy complete");
	}

	@Override
	public void visit(LikeGroupsAutoRoutingStrategy routingStrategy) {
		logger.debug("Like Groups Auto-Routing Strategy");
		visitRoutingStrategyWithRecommender(routingStrategy, RecommendTalentToWorkType.LIKEGROUP, likeGroupsUserRecommender);
		logger.info("Like Groups auto-routing strategy complete");
	}

	@Override
	public void visit(LikeWorkAutoRoutingStrategy routingStrategy) {
		logger.debug("Like Work Auto-Routing Strategy");
		visitRoutingStrategyWithRecommender(routingStrategy, RecommendTalentToWorkType.LIKEWORK, likeWorkUserRecommender);
		logger.info("Like Work auto-routing strategy complete");
	}

	@Override
	public void visit(PolymathAutoRoutingStrategy routingStrategy) {
		logger.debug("Polymath Auto-Routing strategy for skill  match");
		visitRoutingStrategyWithRecommender(routingStrategy, RecommendTalentToWorkType.POLYMATH, polymathUserRecommender);
		logger.info("Polymath Auto-Routing strategy complete");
	}

	@Override
	public void visit(LikeGroupVendorRoutingStrategy routingStrategy) {
		logger.info("There should be no active visit; this strategy is part of likegroups strategy");
	}

	@Override
	public void visit(LikeWorkVendorRoutingStrategy routingStrategy) {
		logger.info("There should be no active visit; this strategy is part of likework strategy");
	}

	@Override
	public void visit(PolymathVendorRoutingStrategy routingStrategy) {
		logger.info("There should be no active visit; this strategy is part of polymath strategy");
	}

	@Override
	public void visit(GroupRoutingStrategy routingStrategy) {
		// TODO: when we remove VENDOR_POOLS_FEATURE, we should change it to NewWorkerSearchAssignment
		// TODO: unless NewWorkerSearchAssignment is removed.
		if (featureEvaluator.hasGlobalFeature(Constants.VENDOR_POOLS_FEATURE)) {
			visitNewSearchBasedGroupRoutingStrategy(routingStrategy);
			logger.info("NEW group routing strategy done");
		} else {
			visitSearchBasedRoutingStrategy(routingStrategy);
			logger.info("LEGACY group routing strategy done");
		}
	}

	@Override
	public void visit(PeopleSearchRoutingStrategy routingStrategy) {
		visitUserBasedRoutingStrategy(routingStrategy);
		logger.info("peoplesearch routing strategy done");
	}

	@Deprecated
	public List<PeopleSearchResult> getWorkersWithinTravelDistanceForStrategy(AbstractRoutingStrategy routingStrategy, Long workId)
		throws WorkNotFoundException, SearchException {

		AssignmentResourceSearchRequest request = workRoutingSearchRequestBuilder.build(routingStrategy);
		Pagination pagination = request.getRequest().getPaginationRequest();
		long cursor = pagination.getCursorPosition();
		pagination.setPageSize(300); // 300 seems right amount without overloading memory requirements
		PeopleSearchResponse response = workResourceSearchService.searchWorkersForAutoRouting(request);

		int availableSent = Constants.MAX_RESOURCES_PER_ASSIGNMENT;
		if (routingStrategy.getType().equals(GroupRoutingStrategy.GROUP_ROUTING_STRATEGY)) {
			availableSent = Constants.GROUP_SEND_RESOURCES_LIMIT;
		}

		List<PeopleSearchResult> results = new ArrayList<>();
		while (response != null && isNotEmpty(response.getResults())) {
			logger.info(String.format("[routing] Found [%d] resources for work [%d]", response.getResults().size(), workId));

			for (PeopleSearchResult r : response.getResults()) {
				if (r.isSetLocationPoint() && r.getMaxTravelDistance() > 0 && r.getDistance() <= r.getMaxTravelDistance()) {
					results.add(r);
					if (results.size() >= availableSent) {
						break;
					}
				}
			}

			cursor += response.getResults().size();

			if (results.size() >= availableSent) {
				break; // exceeded max number of invited workers per assignment
			} else if (cursor >= response.getTotalResultsCount()) {
				break; // no more results
			} else {
				pagination.setCursorPosition(cursor);
				response = workResourceSearchService.searchWorkersForAutoRouting(request); // get next page
			}
		}

		return results;
	}

	private <T extends AbstractRoutingStrategy> void visitRoutingStrategyWithRecommender(
		final T routingStrategy, final RecommendTalentToWorkType type, final UserRecommender recommender) {

		Assert.notNull(routingStrategy.getWork());
		Work work = workService.findWork(routingStrategy.getWork().getId());
		int invitesRemaining =
			Constants.MAX_WORKSEND_RESOURCES_PER_ASSIGNMENT - getTotalSentByAutoRoutings(work.getId());

		if (invitesRemaining <= 0) {
			routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.CANCELLED));
			logger.warn("auto routing exceeds maximum allowed invites {}", Constants.MAX_WORKSEND_RESOURCES_PER_ASSIGNMENT);
			routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
			return;
		}

		try {
			final Recommendation recommendation =
				featureEntitlementService.hasPercentRolloutFeatureToggle(Constants.RECOMMENDATION_SERVICE_WORKSEND)
				? recommendationService.recommendTalentForWork(work, type, webRequestContextProvider.getRequestContext())
				: recommender.recommend(work, false);
			final Recommendation remainedRecommendation =
				recommendation.getRecommendedResources().size() > invitesRemaining ?
					new Recommendation(
						recommendation.getWorkId(),
						Lists.newArrayList(recommendation.getRecommendedResources().subList(0, invitesRemaining)),
						new Explain())
					: recommendation;
			final List<Long> workerIds = remainedRecommendation.getRecommendedResourceIdsByUserType(SolrUserType.WORKER);
			final List<String> workerNumbers = remainedRecommendation.getRecommendedResourceNumbersByUserType(SolrUserType.WORKER);
			route(routingStrategy, Sets.newHashSet(workerNumbers), Sets.newHashSet(workerIds), null, false);

			final List<Long> vendorIds = remainedRecommendation.getRecommendedResourceIdsByUserType(SolrUserType.VENDOR);
			final List<String> vendorNumbers = remainedRecommendation.getRecommendedResourceNumbersByUserType(SolrUserType.VENDOR);
			// create a new strategy for vendor and then route
			AbstractRoutingStrategy vendorStrategy;
			if (LikeGroupsAutoRoutingStrategy.LIKE_GROUPS_AUTO_ROUTING_STRATEGY.equals(routingStrategy.getType())) {
				vendorStrategy = routingStrategyService.addLikeGroupVendorRoutingStrategy(work.getId());
			} else if (LikeWorkAutoRoutingStrategy.LIKE_WORK_AUTO_ROUTING_STRATEGY.equals(routingStrategy.getType())) {
				vendorStrategy = routingStrategyService.addLikeWorkVendorRoutingStrategy(work.getId());
			} else {
				vendorStrategy = routingStrategyService.addPolymathVendorRoutingStrategy(work.getId());
			}
			route(vendorStrategy, Sets.newHashSet(vendorNumbers), Sets.newHashSet(vendorIds), null, true);
		} catch (SolrServerException e) {
			routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.FAILED));
			logger.error(String.format("[routing] Failed to route to work [%d]", work.getId()), e);
			routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
		}
	}

	<T extends AbstractRoutingStrategy> void visitSearchBasedRoutingStrategy(T routingStrategy) {
		Assert.notNull(routingStrategy.getWork());
		Work work = workService.findWork(routingStrategy.getWork().getId()); // Because this context is non-transactional

		if (work.isRoutable() || work.isWorkBundle()) {

			WorkRoutingResponseSummary summary = null;
			WorkAuthorizationResponse authorizationResponse = WorkAuthorizationResponse.UNKNOWN;
			try {
				authorizationResponse = WorkAuthorizationResponse.SUCCEEDED;
				List<PeopleSearchResult> results = getWorkersWithinTravelDistanceForStrategy(routingStrategy, work.getId());
				if (isNotEmpty(results)) {

					authorizationResponse = accountRegisterAuthorizationService.authorizeWork(work);
					routingStrategy.setWorkAuthorizationResponse(authorizationResponse);
					if (authorizationResponse.fail()) {
						routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.FAILED));
						logger.info(String.format("[routing] Failed to route to work [%d]", work.getId()));
						routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
						return;
					}

					addRoutingStrategyTracking(work.getId(), results, routingStrategy);
					WorkResourceAddOptions workResourceAddOptions = new WorkResourceAddOptions(true);
					summary = workRoutingService.addToWorkResources(
						work.getId(),
						results,
						workResourceAddOptions,
						routingStrategy.isAssignToFirstToAccept());
					routingStrategy.setWorkRoutingResponseSummary(summary);
					logger.debug("[routing] Summary " + summary.toString());

				}
			} catch (Exception e) {
				// TODO: Alex - should we not also send a notification to companies for these failures too?
				routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.FAILED));
				logger.error(String.format("[routing] Failed to route to work [%d]", work.getId()), e);
				if (authorizationResponse.success()) {
					accountRegisterAuthorizationService.deauthorizeWork(work);
				}

				routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
				return;
			}

			populateRoutingStrategySummary(authorizationResponse, summary, routingStrategy, work);
		} else {
			routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.CANCELLED));
		}

		routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
		eventRouter.sendEvent(eventFactory.buildRoutingStrategyCompleteEvent(routingStrategy));
	}

	private <T extends AbstractRoutingStrategy> void visitVendorBasedRoutingStrategy(final T routingStrategy) {
		Set<String> companyNumbers = Sets.newHashSet();
		Set<Long> companyIds = Sets.newHashSet();
		if (VendorRoutingStrategy.VENDOR_ROUTING_STRATEGY.equals(routingStrategy.getType())) {
			companyIds = ((VendorRoutingStrategy) routingStrategy).getCompanyIds();
			companyNumbers.addAll(companyService.findCompanyNumbersFromCompanyIds(companyIds));
		} else if (VendorSearchRoutingStrategy.VENDOR_SEARCH_ROUTING_STRATEGY.equals(routingStrategy.getType())) {
			companyNumbers = ((VendorSearchRoutingStrategy) routingStrategy).getCompanyNumbers();
			List<CompanyIdentityDTO> companyIdentities =
				companyService.findCompanyIdentitiesByCompanyNumbers(companyNumbers);
			for (CompanyIdentityDTO identity : companyIdentities) {
				companyIds.add(identity.getCompanyId());
			}
		}
		route(routingStrategy, companyNumbers, companyIds, null, true);
	}

	private <T extends AbstractRoutingStrategy> void visitUserBasedRoutingStrategy(final T routingStrategy) {
		Set<Long> userIds = Sets.newHashSet();
		Set<String> userNumbers = Sets.newHashSet();
		Long dispatcherId = null;
		if (UserRoutingStrategy.USER_ROUTING_STRATEGY.equals(routingStrategy.getType())) {
			userIds = ((UserRoutingStrategy) routingStrategy).getUserIds();
			if (isEmpty(userIds)) {
				userNumbers = ((UserRoutingStrategy) routingStrategy).getUserNumbers();
				userIds = userService.findAllUserIdsByUserNumbers(userNumbers);
			} else {
				userNumbers = userService.findAllUserNumbersByUserIds(userIds);
			}
		} else if (PeopleSearchRoutingStrategy.PEOPLE_SEARCH_ROUTING_STRATEGY.equals(routingStrategy.getType())) {
			userNumbers = ((PeopleSearchRoutingStrategy) routingStrategy).getUserNumbers();
			userIds = userService.findAllUserIdsByUserNumbers(userNumbers);
			dispatcherId = ((PeopleSearchRoutingStrategy) routingStrategy).getDispatcherId();
		}
		route(routingStrategy, userNumbers, userIds, dispatcherId, false);
	}


	private <T extends AbstractRoutingStrategy> void route(
		final T routingStrategy,
		final Set<String> resourceNumbers,
		final Set<Long> resourceIds,
		final Long dispatcherId,
		final boolean routeToVendors) {

		Assert.notNull(routingStrategy.getWork());
		Work work = workService.findWork(routingStrategy.getWork().getId());

		WorkRoutingResponseSummary summary;
		WorkAuthorizationResponse authorizationResponse = WorkAuthorizationResponse.UNKNOWN;

		if (work.isRoutable() || work.isWorkBundle()) {
			try {
				if (isNotEmpty(resourceIds)) {
					authorizationResponse = accountRegisterAuthorizationService.authorizeWork(work);
					routingStrategy.setWorkAuthorizationResponse(authorizationResponse);
					if (authorizationResponse.fail()) {
						routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.FAILED));
						routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
						logger.info(
							String.format(
								"[routing] Failed to route strategy [%s] to work [%d]",
								routingStrategy.getType(), work.getId()));
						return;
					}
					addRoutingStrategyTracking(work.getId(), resourceIds, routingStrategy);

					if (routeToVendors) {
						summary = vendorService.inviteVendorsToWork(
							resourceNumbers,
							work.getId(),
							routingStrategy.isAssignToFirstToAccept(),
							Collections.<Long>emptySet());
					} else {
						if (dispatcherId != null) {
							summary = workRoutingService.addToWorkResourcesAsDispatcher(
								work.getWorkNumber(),
								resourceNumbers,
								dispatcherId,
								routingStrategy.isAssignToFirstToAccept());
						} else {
							summary = workRoutingService.addToWorkResources(
								work.getId(),
								resourceIds,
								routingStrategy.isAssignToFirstToAccept());
						}
					}
					routingStrategy.setWorkRoutingResponseSummary(summary);
				} else {
					// no workers recommended - we still want to set our status to sent so create a dummy summary
					summary = new WorkRoutingResponseSummary();
					routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SENT));
				}
			} catch (Exception e) {
				routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.FAILED));
				logger.error(
					String.format(
						"[routing] Failed to route strategy [%s] to work [%d]",
						routingStrategy.getType(), work.getId()),
					e);
				if (authorizationResponse.success()) {
					accountRegisterAuthorizationService.deauthorizeWork(work);
				}
				routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
				return;
			}
			populateRoutingStrategySummary(authorizationResponse, summary, routingStrategy, work);
		} else {
			routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.CANCELLED));
		}

		routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);

		// send a complete event
		logger.info("send complete event " + routingStrategy.getType());
		eventRouter.sendEvent(eventFactory.buildRoutingStrategyCompleteEvent(routingStrategy));
	}

	@SuppressWarnings("unchecked")
	private int extractValue(WorkAuthorizationResponse workAuthorizationResponse, WorkRoutingResponseSummary responseSummary) {
		return ((Set<String>) (MapUtils.getObject(responseSummary.getResponse(), workAuthorizationResponse, Collections.EMPTY_SET))).size();
	}

	<T extends AbstractRoutingStrategy> void populateRoutingStrategySummary(
		WorkAuthorizationResponse workAuthorizationResponse,
		WorkRoutingResponseSummary responseSummary,
		T routingStrategy,
		Work work) {

		Assert.notNull(routingStrategy);
		Assert.notNull(work);

		if (responseSummary == null) {
			logger.info(String.format("[routing] Failed to route to work [%d]", work.getId()));
			if (workAuthorizationResponse.success()) {
				accountRegisterAuthorizationService.deauthorizeWork(work);
			}
			routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.FAILED));
			WorkGenericNotificationTemplate workGenericNotificationTemplate = notificationTemplateFactory.buildWorkGenericNotificationTemplate(
				work.getBuyer().getId(),
				work.getId(),
				messageSource.getMessage("work.routed.failed", new Object[]{work.getTitle(), routingStrategy.getType()}, null) + ": " + "Worker(s) too far from assignment location."
			);
			notificationService.sendNotification(workGenericNotificationTemplate);
			return;
		}

		routingStrategy.setRoutedOn(DateUtilities.getCalendarNow());
		if (routingStrategy.getSummary().hasErrors()) {
			routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SENT_WITH_ERRORS));
		} else {
			routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SENT));
		}

		RoutingStrategySummary summary = routingStrategy.getSummary();

		int sent = extractValue(WorkAuthorizationResponse.SUCCEEDED, responseSummary);
		int invalid = extractValue(WorkAuthorizationResponse.INTERNAL_PRICING, responseSummary);
		invalid += extractValue(WorkAuthorizationResponse.INVALID_USER, responseSummary);
		invalid += extractValue(WorkAuthorizationResponse.LANE0_NOT_ALLOWED, responseSummary);

		Map<String, Integer> failures = Maps.newHashMap();
		failures.put("work.routed.insufficient_funds", extractValue(WorkAuthorizationResponse.INSUFFICIENT_FUNDS, responseSummary));
		failures.put("work.routed.insufficient_budget", extractValue(WorkAuthorizationResponse.INSUFFICIENT_BUDGET, responseSummary));
		failures.put("work.routed.insufficient_spend_limit", extractValue(WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT, responseSummary));
		failures.put("work.routed.insufficient_payment_terms", extractValue(WorkAuthorizationResponse.PAYMENT_TERMS_AP_CREDIT_LIMIT, responseSummary));
		failures.put("work.routed.unknown_failure", extractValue(WorkAuthorizationResponse.UNKNOWN, responseSummary));
		failures.put("work.routed.illegal_state", invalid);

		summary.setSent(summary.getSent() + sent);
		summary.setFailedInsufficientFunds(summary.getFailedInsufficientFunds() + failures.get("work.routed.insufficient_funds"));
		summary.setFailedInsufficientBudget(summary.getFailedInsufficientBudget() + failures.get("work.routed.insufficient_budget"));
		summary.setFailedInsufficientSpendLimit(summary.getFailedInsufficientSpendLimit() + failures.get("work.routed.insufficient_spend_limit"));
		summary.setFailedPaymentTermsCreditLimit(summary.getFailedPaymentTermsCreditLimit() + failures.get("work.routed.insufficient_payment_terms"));
		summary.setFailedIllegalState(summary.getFailedIllegalState() + failures.get("work.routed.illegal_state"));
		summary.setFailed(summary.getFailed() + failures.get("work.routed.unknown_failure"));

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
		if (work.isWorkBundle()) {
			WorkBundleRoutingEvent event =  new WorkBundleRoutingEvent((work.getId()));
			AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(work.getCompany().getId());
			event.setMessageGroupId(String.format(Constants.ACCOUNT_REGISTER_MESSAGE_GROUP_ID, accountRegister.getId()));
			eventRouter.sendEvent(event);
		}

		int failed =
			failures.get("work.routed.insufficient_funds") + failures.get("work.routed.insufficient_budget") +
			failures.get("work.routed.insufficient_spend_limit") + failures.get("work.routed.insufficient_payment_terms") +
			failures.get("work.routed.unknown_failure") + failures.get("work.routed.illegal_state");
		boolean shouldContinue = (failed > 0 && work.getBuyer() != null);
		if (!shouldContinue) {
			return;
		}

		StringBuilder message = new StringBuilder(messageSource.getMessage("work.routed.failed", new Object[]{work.getTitle(), failed}, null));
		message.append(" ").append(StringUtilities.pluralize(messageSource.getMessage("work.routed.worker", null, null), failed)).append(": ");

		StringBuilder reasons = new StringBuilder(" (");
		int failTypes = 0;

		for (String key : new TreeSet<>(failures.keySet())) {
			int failureCount = failures.get(key);
			if (failureCount > 0) {
				reasons.append(messageSource.getMessage(key, null, null));
				failed = failed - failureCount;
				failTypes++;
				if (failed > 0) {
					reasons.append(": ").append(failureCount).append("; ");
				} else if (failTypes > 1) {
					reasons.append(": ").append(failureCount);
				}
			}
		}

		message.append(StringUtilities.pluralize(messageSource.getMessage("work.routed.reason", null, null), failTypes)).append(reasons).append(").");
		WorkGenericNotificationTemplate workGenericNotificationTemplate = notificationTemplateFactory.buildWorkGenericNotificationTemplate(
			work.getBuyer().getId(), work, message.toString()
		);
		notificationService.sendNotification(workGenericNotificationTemplate);
	}

	private <T extends AbstractRoutingStrategy> void addRoutingStrategyTracking(final long workId,
	                                                                            final List<PeopleSearchResult> results,
	                                                                            final T routingStrategy) {
		if (results.size() == 0) {
			return;
		}
		Collections.sort(results, new Comparator<PeopleSearchResult>() {
			@Override
			public int compare(PeopleSearchResult o1, PeopleSearchResult o2) {
				return o1.getRank() - o2.getRank();
			}
		});

		List<RoutingStrategyTracking> trackings = Lists.newArrayListWithCapacity(results.size());
		int rank = 1;
		for (PeopleSearchResult result : results) {
			RoutingStrategyTracking routingStrategyTracking = new RoutingStrategyTracking(workId, result.getUserId(), routingStrategy.getId(), rank);
			trackings.add(routingStrategyTracking);
			rank++;
		}
		routingStrategyTrackingService.saveAll(trackings);
	}

	private <T extends AbstractRoutingStrategy> void addRoutingStrategyTracking(final long workId,
	                                                                            final Set<Long> userIds,
	                                                                            final T routingStrategy) {
		if (userIds.size() == 0) {
			return;
		}

		List<RoutingStrategyTracking> trackings = Lists.newArrayListWithCapacity(userIds.size());
		for (Long userId : userIds) {
			RoutingStrategyTracking routingStrategyTracking = new RoutingStrategyTracking(workId, userId, routingStrategy.getId());
			trackings.add(routingStrategyTracking);
		}
		routingStrategyTrackingService.saveAll(trackings);
	}

	private int getTotalSentByAutoRoutings(final Long workId) {
		final List<AbstractRoutingStrategy> routingStrategies = routingStrategyService.findAllRoutingStrategiesByWork(workId);
		int totalSent = 0;
		for (AbstractRoutingStrategy routingStrategy : routingStrategies) {
			if (AUTO_ROUTING_TYPES.contains(routingStrategy.getType())) {
				RoutingStrategySummary summary = routingStrategy.getSummary();
				if (!summary.hasErrors()) {
					totalSent += summary.getSent();
				}
			}
		}
		return totalSent;
	}

	private List<Worker> getWorkersWithinTravelDistanceFilter(final AbstractRoutingStrategy routingStrategy)
		throws WorkNotFoundException, SearchException
	{
		int numberOfWorkers = 0;
		long offset = 0;
		long limit = 300;
		final List<Worker> workResources = Lists.newArrayList();
		// SPAM!!! We should reduce the limits.
		int availableSent = Constants.MAX_RESOURCES_PER_ASSIGNMENT;
		if (routingStrategy.getType().equals(GroupRoutingStrategy.GROUP_ROUTING_STRATEGY)) {
			availableSent = Constants.GROUP_SEND_RESOURCES_LIMIT;
		}

		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		final FindWorkerCriteria findWorkerCriteria =
			workRoutingSearchRequestBuilder.buildFindWorkerCriteriaForGroupRouting(routingStrategy);
		while (availableSent > numberOfWorkers) {
			List<Worker> searchResults =
				workResourceSearchService.searchWorkersForGroupRouting(findWorkerCriteria, offset, limit, requestContext);
			if (searchResults.size() == 0) {
				break;
			}
			for (final Worker worker : searchResults) {
				workResources.add(worker);
				if (UserType.WORKER.getUserTypeCode() == worker.getUserTypeCode()) {
					numberOfWorkers++; // we only count workers against limit, and send to as many vendors as possible!
				}
				if (availableSent <= numberOfWorkers) {
					break;
				}
			}
			offset += searchResults.size();
		}
		return workResources;
	}

	<T extends AbstractRoutingStrategy> void visitNewSearchBasedGroupRoutingStrategy(T routingStrategy) {
		Assert.notNull(routingStrategy.getWork());
		Assert.isTrue(GroupRoutingStrategy.GROUP_ROUTING_STRATEGY.equals(routingStrategy.getType()));
		Assert.notNull(((GroupRoutingStrategy) routingStrategy).getUserGroups());
		final Set<Long> groupIds = ((GroupRoutingStrategy) routingStrategy).getUserGroups();
		Work work = workService.findWork(routingStrategy.getWork().getId());

		if (work.isRoutable() || work.isWorkBundle()) {
			WorkRoutingResponseSummary workerSummary = null;
			WorkRoutingResponseSummary vendorSummary = null;
			WorkAuthorizationResponse authorizationResponse = WorkAuthorizationResponse.UNKNOWN;
			try {
				authorizationResponse = WorkAuthorizationResponse.SUCCEEDED;
				List<Worker> results = getWorkersWithinTravelDistanceFilter(routingStrategy);
				if (isNotEmpty(results)) {
					authorizationResponse = accountRegisterAuthorizationService.authorizeWork(work);
					routingStrategy.setWorkAuthorizationResponse(authorizationResponse);
					if (authorizationResponse.fail()) {
						routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.FAILED));
						logger.info(String.format("[routing] Failed to route to work [%d]", work.getId()));
						routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
						return;
					}

					Set<Long> workers = Sets.newHashSet();
					Set<Long> vendors = Sets.newHashSet();
					for (final Worker workResource : results) {
						if (workResource.getUserTypeCode() == UserType.VENDOR.getUserTypeCode()) {
							vendors.add(Long.parseLong(workResource.getId()));
						} else {
							workers.add(Long.parseLong(workResource.getId()));
						}
					}

					if (workers.size() > 0) {
						addRoutingStrategyTracking(work.getId(), workers, routingStrategy);
						WorkResourceAddOptions workResourceAddOptions = new WorkResourceAddOptions(true);
						workerSummary =  workRoutingService.addToWorkResources(
							work.getId(),
							workers,
							workResourceAddOptions,
							routingStrategy.isAssignToFirstToAccept());
					}

					if (vendors.size() > 0) {
						addRoutingStrategyTracking(work.getId(), vendors, routingStrategy);
						final List<String> vendorNumbers = companyService.findCompanyNumbersFromCompanyIds(vendors);
						vendorSummary = vendorService.inviteVendorsToWork(
							Sets.newHashSet(vendorNumbers),
							work.getId(),
							routingStrategy.isAssignToFirstToAccept(),
							groupIds);
					}

					if (workerSummary != null) {
						populateRoutingStrategySummary(authorizationResponse, workerSummary, routingStrategy, work);
					}
					if (vendorSummary != null) {
						populateRoutingStrategySummary(authorizationResponse, vendorSummary, routingStrategy, work);
					}
					if (workerSummary == null && vendorSummary == null) {
						routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.FAILED));
						logger.info(String.format("[routing] Failed to route to work [%d]", work.getId()));
					}
				} else {
					routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.FAILED));
					logger.info(String.format("[routing] Failed to route to work [%d], no one to route", work.getId()));
				}
			} catch (Exception e) {
				routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.FAILED));
				logger.error(String.format("[routing] Failed to route to work [%d]", work.getId()), e);
				if (authorizationResponse.success()) {
					accountRegisterAuthorizationService.deauthorizeWork(work);
				}
				routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
				return;
			}
		} else {
			routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.CANCELLED));
		}
		routingStrategyService.saveOrUpdateRoutingStrategy(routingStrategy);
		eventRouter.sendEvent(eventFactory.buildRoutingStrategyCompleteEvent(routingStrategy));
	}
}
