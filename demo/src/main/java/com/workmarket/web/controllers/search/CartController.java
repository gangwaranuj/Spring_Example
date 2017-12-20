package com.workmarket.web.controllers.search;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.model.route.PeopleSearchRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorSearchRoutingStrategy;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.wrapper.ValidateWorkResponse;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.search.cart.UserNotFoundException;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.assignments.BaseWorkController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.WorkAuthorizationFailureHelper;
import com.workmarket.web.helpers.WorkBundleValidationHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/search/cart")
public class CartController extends BaseWorkController {

	private static final Log logger = LogFactory.getLog(CartController.class);
	private static final String DEFAULT_REDIRECT = "/assignments";

	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private WorkRoutingService workRoutingService;
	@Autowired private LaneService laneService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private WorkBundleValidationHelper workBundleValidationHelper;
	@Autowired private EventRouter eventRouter;
	@Autowired private WorkSearchService workSearchService;
	@Autowired private VendorService vendorService;
	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired private RoutingStrategyService routingStrategyService;
	@Autowired private WorkAuthorizationFailureHelper workAuthorizationFailureHelper;

	@RequestMapping(
		value = "/push_to_worker_pool",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder pushToWorkerPool(
		@RequestParam(value = "selected_workers[]", required = false) String[] selectedWorkers) {

		Set<String> selectedWorkerUserNumbers = new HashSet<>(Arrays.asList(selectedWorkers));
		try {
			laneService.addUsersToWorkerPool(getCurrentUser().getUserNumber(), selectedWorkerUserNumbers);
		} catch (Exception e) {
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.addMessage(messageHelper.getMessage("search.cart.push.worker_pool.error"));
		}

		return new AjaxResponseBuilder()
			.setSuccessful(true)
			.addMessage(messageHelper.getMessage("search.cart.push.worker_pool.success"));

	}

	@RequestMapping(
		value = "/push_to_test",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder pushToTest(
		@RequestParam("id") Long testId,
		@RequestParam(value = "selected_workers[]", required = false) String[] selectedWorkers,
		@RequestParam(value = "selected_vendors[]", required = false) String[] selectedVendors) throws UserNotFoundException {

		Set<String> selectedWorkersUserNumbers = new HashSet<>();
		if (selectedWorkers != null && selectedWorkers.length > 0) {
			selectedWorkersUserNumbers.addAll(Arrays.asList(selectedWorkers));
		}
		if (selectedVendors != null && selectedVendors.length > 0) {
			selectedWorkersUserNumbers.addAll(companyService.findWorkerNumbersForCompanies(Lists.newArrayList(selectedVendors)));
		}

		try {
			assessmentService.addUsersToTest(getCurrentUser().getId(), getCurrentUser().getUserNumber(), testId, selectedWorkersUserNumbers);
		} catch (Exception e) {
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.addMessage(messageHelper.getMessage("search.cart.push.test.error"));

		}
		return new AjaxResponseBuilder()
			.setSuccessful(true)
			.addMessage(messageHelper.getMessage("search.cart.push.test.success"));
	}

	private boolean processSendResults(
		Map<WorkAuthorizationResponse, Set<String>> results,
		MessageBundle messages,
		String pushId,
		String buyerName,
		String label
	) {
		boolean success = false;
		if (results == null) {
			messageHelper.addError(messages, "search.cart.push.assignment.internal_error");
			return false;
		}
		for (Map.Entry<WorkAuthorizationResponse, Set<String>> workAuthorizationResponseEntry : results.entrySet()) {
			WorkAuthorizationResponse statusType = workAuthorizationResponseEntry.getKey();
			String messageKey = statusType.getMessagePropertyKey();
			int size = workAuthorizationResponseEntry.getValue().size();
			switch (statusType) {
				case SUCCEEDED:
					messageHelper.addSuccess(messages, messageKey, size, StringUtilities.pluralize(label, size), pushId);
					success = true;
					break;
				case INVALID_INDUSTRY_FOR_RESOURCE:
					messageHelper.addError(messages, messageKey, size);
					break;
				case INSUFFICIENT_SPEND_LIMIT:
					messageHelper.addError(messages, messageKey, buyerName);
					break;
				default:
					messageHelper.addError(messages, messageKey, size, StringUtilities.pluralize(label, size), pushId);
			}
		}
		return success;
	}

	private AjaxResponseBuilder processError(MessageBundle messages, String errorKey, String redirect, Object... args) {
		if (errorKey != null) { messageHelper.addError(messages, errorKey, args); }

		return new AjaxResponseBuilder()
			.setSuccessful(false)
			.setMessages(messages.getErrors())
			.setRedirect(redirect);
	}

	@RequestMapping(
		value = "/push_to_assignment",
		method = POST)
	public @ResponseBody AjaxResponseBuilder pushToAssignment(
		@RequestParam("id") String workNumber,
		@RequestParam(value = "selected[]", required = false) String[] selectedResources,
		@RequestParam(value = "assign_to_first_to_accept", required = false) boolean assignToFirstToAccept
	) throws UserNotFoundException, WorkNotFoundException {

		// Basic validation:
		// * Valid assignment?
		// * Authorized to send the assignment?
		// * Suspended?

		MessageBundle messages = messageHelper.newBundle();

		WorkRequest workRequest = new WorkRequest()
			.setUserId(getCurrentUser().getId())
			.setWorkNumber(workNumber)
			.setIncludes(ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.INDUSTRY_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.BUYER_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
			));

		WorkResponse workResponse;
		try {
			workResponse = tWorkFacadeService.findWork(workRequest);
		} catch (WorkActionException e) {
			return processError(messages, "search.cart.push.assignment.invalid_work", DEFAULT_REDIRECT);
		}

		Set<AuthorizationContext> authorizationContexts = workResponse.getAuthorizationContexts();
		Boolean isDispatcher =  authorizationContexts.contains(AuthorizationContext.DISPATCHER);
		Boolean isAdmin =  authorizationContexts.contains(AuthorizationContext.ADMIN);
		if (!(isAdmin || isDispatcher)) {
			throw new HttpException401().setMessageKey("search.cart.push.assignment.not_authorized");
		}

		if (getCurrentUser().getCompanyIsLocked() && !workResponse.getWork().getPricing().getType().equals(PricingStrategyType.INTERNAL)) {
			return processError(messages, "search.cart.push.assignment.locked", DEFAULT_REDIRECT);
		}

		if (ArrayUtils.isEmpty(selectedResources)) {
			return processError(messages, "search.cart.push.assignment.empty_cart", DEFAULT_REDIRECT);
		}

		Set<String> selectedResourcesUserNumbers = Sets.newHashSet(selectedResources);
		Work work = workResponse.getWork();

		if (selectedResourcesUserNumbers.contains(work.getBuyer().getUserNumber()) && (!(work.getPricing().getType().isInternal()))) {
			return processError(messages, "search.cart.push.assignment.invalid_self_routing", DEFAULT_REDIRECT);
		}

		String redirectTo = String.format("/assignments/details/%s", workNumber);

		if (isDispatcher) {
			if (!(work.getStatus().getCode().equals(WorkStatusType.SENT) || work.getStatus().getCode().equals(WorkStatusType.DECLINED))) {
				return processError(messages, "search.cart.push.assignment.invalid_work", DEFAULT_REDIRECT);
			}
		} else {
			if (workResponse.isWorkBundle()) {
				// validate that work is still in a ready to send state
				Multimap<String, ValidateWorkResponse> validationResponses =
					workBundleValidationHelper.readyToSend(workRequest.getWorkNumber(), getCurrentUser().getId(), messages);
				Collection<ValidateWorkResponse> validationErrors = validationResponses.get(WorkBundleValidationHelper.VALIDATION_ERRORS);

				if (isNotEmpty(validationErrors)) {
					return processError(messages, "assignment_bundle.add.fail.no_valid_work", redirectTo);
				}
			}

			WorkAuthorizationResponse authorizationResponse = accountRegisterAuthorizationService.authorizeWork(workResponse.getWork().getId());
			if (authorizationResponse.fail()) {
				workAuthorizationFailureHelper.handleErrorsFromAuthResponse(authorizationResponse, work, messages);
				return processError(messages, null, DEFAULT_REDIRECT);
			}
		}

		Long dispatcherId = isDispatcher ? getCurrentUser().getId() : null;
		PeopleSearchRoutingStrategy routingStrategy = routingStrategyService.addPeopleSearchRoutingStrategy(workResponse.getWork().getId(), selectedResourcesUserNumbers, dispatcherId, assignToFirstToAccept);
		Map<WorkAuthorizationResponse, Set<String>> workResponseSummary = routingStrategy.getWorkRoutingResponseSummary().getResponse();

		String buyerName = (work.isSetBuyer() && work.getBuyer().isSetName()) ? work.getBuyer().getName().getFullName() : "";
		boolean wasRoutingSuccessful = processSendResults(workResponseSummary, messages, workNumber, buyerName, "worker");
		boolean isNotSentOrDeclined = !WorkStatusType.SENT.equals(work.getStatus().getCode()) && !WorkStatusType.DECLINED.equals(work.getStatus().getCode());
		if (!wasRoutingSuccessful && isNotSentOrDeclined) {
			if (!isDispatcher) {
				accountRegisterAuthorizationService.deauthorizeWork(work.getId());
			}
		} else if (wasRoutingSuccessful) {
			workSearchService.reindexWorkAsynchronous(workResponse.getWork().getId());

			if (workResponseSummary.containsKey(WorkAuthorizationResponse.MAX_RESOURCES_EXCEEDED)) {
				redirectTo = String.format("/assignments/contact/%s?empty=0", workNumber);
			}
		}

		return new AjaxResponseBuilder()
			.setSuccessful(wasRoutingSuccessful)
			.setMessages(messages.getAllMessages())
			.setRedirect(redirectTo);
	}

	@RequestMapping(
		value = "/invite_vendor",
		method = POST)
	public @ResponseBody AjaxResponseBuilder inviteVendorToAssignment(
		@RequestParam(value = "id") String workNumber,
		@RequestParam(value = "selected[]", required = false) String[] companyNumbers,
		@RequestParam(value = "assign_to_first_to_accept", required = false) boolean assignToFirstToAccept
	) {

		WorkResponse workResponse = getWork(workNumber, Sets.newHashSet(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.STATUS_INFO
		), Sets.newHashSet(
			AuthorizationContext.ADMIN
		), "invite_vendor");

		Work work = workResponse.getWork();
		String redirectTo = String.format("/assignments/details/%s", workNumber);
		MessageBundle messages = messageHelper.newBundle();

		if (workResponse.isWorkBundle()) {
			// validate that work is still in a ready to send state
			Multimap<String, ValidateWorkResponse> validationResponses =
					workBundleValidationHelper.readyToSend(work.getWorkNumber(), getCurrentUser().getId(), messages);
			Collection<ValidateWorkResponse> validationErrors = validationResponses.get(WorkBundleValidationHelper.VALIDATION_ERRORS);

			if (isNotEmpty(validationErrors)) {
				return processError(messages, "assignment_bundle.add.fail.no_valid_work", redirectTo);
			}
		}

		VendorSearchRoutingStrategy routingStrategy = routingStrategyService.addVendorSearchRoutingStrategy(workResponse.getWork().getId(), Sets.newHashSet(companyNumbers), null, assignToFirstToAccept);
		Map<WorkAuthorizationResponse, Set<String>> workResponseSummary = routingStrategy.getWorkRoutingResponseSummary().getResponse();

		String buyerName = (work.isSetBuyer() && work.getBuyer().isSetName()) ? work.getBuyer().getName().getFullName() : "";
		boolean wasRoutingSuccessful = processSendResults(workResponseSummary, messages, workNumber, buyerName, "vendor");

		if (wasRoutingSuccessful) {
			workSearchService.reindexWorkAsynchronous(work.getId());

			if (workResponseSummary.containsKey(WorkAuthorizationResponse.MAX_RESOURCES_EXCEEDED)) {
				redirectTo = String.format("/assignments/contact/%s?empty=0", workNumber);
			}
		}

		return new AjaxResponseBuilder()
				.setSuccessful(wasRoutingSuccessful)
				.setMessages(messages.getAllMessages())
				.setRedirect(redirectTo);
	}
}
