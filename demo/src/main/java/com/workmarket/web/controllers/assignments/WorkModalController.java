package com.workmarket.web.controllers.assignments;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.common.service.wrapper.response.Response;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.AbstractEntityUtilities;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.changelog.work.WorkInternalOwnerChangedChangeLog;
import com.workmarket.domains.model.changelog.work.WorkSupportContactChangedChangeLog;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.rating.RatingWorkData;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.facade.service.WorkFacadeService;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkQuestionService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.ClientContactDTO;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.dto.StopPaymentDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.event.work.WorkRepriceEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InsufficientSpendLimitException;
import com.workmarket.service.infra.business.GoogleCalendarService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.search.cart.CartMaxExceededException;
import com.workmarket.thrift.search.cart.SearchCart;
import com.workmarket.thrift.search.cart.SearchCartRequest;
import com.workmarket.thrift.search.cart.UserNotFoundException;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException403;
import com.workmarket.web.forms.assignments.AddLabelForm;
import com.workmarket.web.forms.assignments.EditLocationContactForm;
import com.workmarket.web.forms.assignments.FeedbackForm;
import com.workmarket.web.forms.assignments.StopPaymentForm;
import com.workmarket.web.forms.work.WorkForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.ValidationMessageHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.CancelWorkValidator;
import com.workmarket.web.validators.DateRangeValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller handles the modal dialogues in the assignment flow
 */
@Controller
@RequestMapping("/assignments")
public class
WorkModalController extends BaseWorkController {

	private static final Log logger = LogFactory.getLog(WorkModalController.class);
	public static final int MAX_MESSAGE_LENGTH = 1000;
	public static final List<CancellationReasonType> UNASSIGN_REASONS = ImmutableList.of(
		CancellationReasonType.createResourceCanceledReasonType(),
		CancellationReasonType.createResourceAbandonedReasonType()
	);

	@Autowired private DateRangeValidator dateRangeValidator;
	@Autowired private PricingService pricingService;
	@Autowired private WorkQuestionService workQuestionService;
	@Autowired private SearchCart.Iface cartService;
	@Autowired private CancelWorkValidator cancelWorkValidator;
	@Autowired private GoogleCalendarService googleCalendarService;
	@Autowired private EventRouter eventRouter;
	@Autowired private WorkFacadeService workFacadeService;
	@Autowired private WorkChangeLogService workChangeLogService;

	@RequestMapping(
		value = "/assign/{workNumber}",
		method = GET)
	public String showAssign(
		@PathVariable("workNumber") String workNumber,
		Model model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN
		), "assign");

		Work work = workResponse.getWork();

		// Get the list of employees to which the assignment can be, ahem, assigned
		List<User> users = authenticationService.findAllUsersByACLRoleAndCompany(getCurrentUser().getCompanyId(), AclRole.ACL_WORKER);
		Collections.sort(users, new Comparator<User>() {
			@Override
			public int compare(final User object1, final User object2) {
				return object1.getFirstName().compareTo(object2.getFirstName());
			}
		});

		// Load the assign modal
		model.addAttribute("work", work);
		model.addAttribute("users", users);

		return "web/partials/assignments/assign";
	}

	@RequestMapping(
		value = "/assign/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder submitAssign(
		@PathVariable String workNumber,
		@RequestParam Long assignee,
		@RequestParam(required = false) String note) {

		AjaxResponseBuilder responseBody = AjaxResponseBuilder.fail();

		try {
			String userNumber = getCurrentUser().getUserNumber();
			User assigneeUser = userService.findUserById(assignee);
			if (assigneeUser == null || assigneeUser.getUserNumber() == null) {
				messageHelper.addMessage(responseBody, "search.cart.push.assignment.invalid_assignee");
				return responseBody;
			}

			// Add the user to the cart - the cart will be pushed by the front end following return from this POST (puke)
			cartService.clearCart(userNumber);
			cartService.addToCart(new SearchCartRequest(userNumber, Sets.newHashSet(assigneeUser.getUserNumber())));

			return responseBody
				.addData("id", workNumber)
				.addData("note", note)
				.setSuccessful(true);
		} catch (UserNotFoundException | CartMaxExceededException e) {
			messageHelper.addMessage(responseBody, "search.cart.push.assignment.assign_exception");
			return responseBody;
		}
	}

	@RequestMapping(
		value = "/reassign_internal/{workNumber}",
		method = GET)
	public String showReassignInternal(
		@PathVariable("workNumber") String workNumber,
		Model model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN
		), "reassign");

		Work work = workResponse.getWork();

		// Get the list of employees to which the assignment can be, ahem, assigned
		List<User> users = authenticationService.findAllUsersByACLRoleAndCompany(getCurrentUser().getCompanyId(), AclRole.ACL_WORKER);
		Collections.sort(users, new Comparator<User>() {
			@Override
			public int compare(final User object1, final User object2) {
				return object1.getFirstName().compareTo(object2.getFirstName());
			}
		});

		// Load the assign modal
		model.addAttribute("work", work);
		model.addAttribute("users", users);

		return "web/partials/assignments/reassign_internal";
	}

	@RequestMapping(
		value = "/reassign_internal/{workNumber}",
		method = POST)
	public @ResponseBody AjaxResponseBuilder submitReassignInternal(
		@PathVariable("workNumber") String workNumber,
		@RequestParam Long assignee,
		@RequestParam(required = false) String note) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN
		), "reassign");
		Long workId = workResponse.getWork().getId();

		AjaxResponseBuilder responseBody = AjaxResponseBuilder.fail();

		// Unassign the current user
		workService.removeWorkerFromWork(workId, true);

		try {
			String userNumber = getCurrentUser().getUserNumber();
			User assigneeUser = userService.findUserById(assignee);
			if (assigneeUser == null || assigneeUser.getUserNumber() == null) {
				messageHelper.addMessage(responseBody, "search.cart.push.assignment.invalid_assignee");
				return responseBody;
			}

			// Add the user to the cart - the cart will be pushed by the front end following return from this POST (puke)
			cartService.clearCart(userNumber);
			cartService.addToCart(new SearchCartRequest(userNumber, Sets.newHashSet(assigneeUser.getUserNumber())));

			return responseBody
				.addData("id", workNumber)
				.addData("note", note)
				.setSuccessful(true);
		} catch (UserNotFoundException | CartMaxExceededException e) {
			messageHelper.addMessage(responseBody, "search.cart.push.assignment.assign_exception");
			return responseBody;
		}
	}

	@RequestMapping(
		value = "/edit_price/{workNumber}",
		method = GET)
	public String showEditPrice(
		@PathVariable("workNumber") String workNumber,
		Model model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.PAYMENT_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN
		), "edit_price");

		Work work = workResponse.getWork();
		normalizeWorkResponsePricing(work);
		WorkForm form = toWorkFormConverter.convert(work);

		model.addAttribute("workFee", pricingService.getCurrentFeePercentageForWork(work.getId()));
		model.addAttribute("pricingStrategyTypes", ModelEnumUtilities.pricingStrategyTypes);
		model.addAttribute("form", form);
		model.addAttribute("work", work);
		model.addAttribute("assignment_pricing_type", companyService.getPaymentConfiguration(getCurrentUser().getCompanyId()).getPaymentCalculatorType());
		model.addAttribute("payterms_available", false);
		model.addAttribute("show_payterms", false);
		model.addAttribute("isModal", true);
		model.addAttribute("spendLimit", getSpendLimit());
		model.addAttribute("apLimit", getAPLimit());

		return "web/partials/assignments/edit_price";
	}

	@RequestMapping(
		value = "/edit_price/{workNumber}",
		method = POST,
		produces = TEXT_HTML_VALUE)
	public String submitEditPrice(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "pricing", required = false) Long pricingStrategyId,
		@RequestParam(value = "flat_price", required = false) Double flatPrice,
		@RequestParam(value = "per_hour_price", required = false) Double perHourPrice,
		@RequestParam(value = "max_number_of_hours", required = false) Double maxNumberOfHours,
		@RequestParam(value = "per_unit_price", required = false) Double perUnitPrice,
		@RequestParam(value = "max_number_of_units", required = false) Double maxNumberOfUnits,
		@RequestParam(value = "initial_per_hour_price", required = false) Double initialPerHourPrice,
		@RequestParam(value = "initial_number_of_hours", required = false) Double initialNumberOfHours,
		@RequestParam(value = "additional_per_hour_price", required = false) Double additionalPerHourPrice,
		@RequestParam(value = "max_blended_number_of_hours", required = false) Double maxBlendedNumberOfHours,
		@RequestParam(value = "pricing_mode", required = false) String pricingMode,
		RedirectAttributes flash) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN
		), "edit_price");

		Work work = workResponse.getWork();

		WorkDTO dto = new WorkDTO();
		dto.setPricingStrategyId(pricingStrategyId);
		dto.setFlatPrice(flatPrice);
		dto.setPerHourPrice(perHourPrice);
		dto.setMaxNumberOfHours(maxNumberOfHours);
		dto.setPerUnitPrice(perUnitPrice);
		dto.setMaxNumberOfUnits(maxNumberOfUnits);
		dto.setInitialPerHourPrice(initialPerHourPrice);
		dto.setInitialNumberOfHours(initialNumberOfHours);
		dto.setAdditionalPerHourPrice(additionalPerHourPrice);
		dto.setMaxBlendedNumberOfHours(maxBlendedNumberOfHours);
		dto.setUseMaxSpendPricingDisplayModeFlag("spend".equals(pricingMode));

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		try {
			List<ConstraintViolation> violations = workService.repriceWork(work.getId(), dto);

			if (isEmpty(violations)) {
				messageHelper.addSuccess(bundle, "assignment.edit_price.success");
			} else {
				BindingResult bind = ValidationMessageHelper.newBindingResult("assignment.edit_price");
				ValidationMessageHelper.rejectViolations(violations, bind);
				messageHelper.setErrors(bundle, bind);
			}
		} catch (InsufficientFundsException e) {
			logger.error(e);
			messageHelper.addError(bundle, "insufficient_funds.assignment.edit_price");
		} catch (InsufficientSpendLimitException e) {
			logger.error(e);
			messageHelper.addError(bundle, "insufficient_spend_limit.assignment.edit_price");
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.edit_price.exception");
		}

		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/edit_price_multiple",
		method = GET)
	public String showEditPriceMultiple(
		@RequestParam("workNumbers") List<String> workNumbers,
		Model model) {

		WorkForm form = new WorkForm();

		model.addAttribute("workFee", pricingService.getCurrentFeePercentageForCompany(getCurrentUser().getCompanyId()));
		model.addAttribute("pricingStrategyTypes", ModelEnumUtilities.pricingStrategyTypes);
		model.addAttribute("form", form);
		model.addAttribute("assignment_pricing_type", companyService.getPaymentConfiguration(getCurrentUser().getCompanyId()).getPaymentCalculatorType());
		model.addAttribute("payterms_available", false);
		model.addAttribute("show_payterms", false);
		model.addAttribute("isModal", true);
		model.addAttribute("spendLimit", getSpendLimit());
		model.addAttribute("apLimit", getAPLimit());
		model.addAttribute("workNumbers", workNumbers);

		return "web/partials/assignments/edit_price";
	}

	@RequestMapping(
		value = "/edit_price_multiple.json",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder submitEditPriceMultiple(
		@RequestParam("workNumbers") List<String> workNumbers,
		@RequestParam(value = "pricing", required = false) Long pricingStrategyId,
		@RequestParam(value = "flat_price", required = false) Double flatPrice,
		@RequestParam(value = "per_hour_price", required = false) Double perHourPrice,
		@RequestParam(value = "max_number_of_hours", required = false) Double maxNumberOfHours,
		@RequestParam(value = "per_unit_price", required = false) Double perUnitPrice,
		@RequestParam(value = "max_number_of_units", required = false) Double maxNumberOfUnits,
		@RequestParam(value = "initial_per_hour_price", required = false) Double initialPerHourPrice,
		@RequestParam(value = "initial_number_of_hours", required = false) Double initialNumberOfHours,
		@RequestParam(value = "additional_per_hour_price", required = false) Double additionalPerHourPrice,
		@RequestParam(value = "max_blended_number_of_hours", required = false) Double maxBlendedNumberOfHours,
		@RequestParam(value = "pricing_mode", required = false) String pricingMode,
		RedirectAttributes flash) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
		if (CollectionUtilities.isEmpty(workNumbers)) {
			logger.error("Unable to perform bulk reprice. No work numbers were sent");
			return response
				.setSuccessful(false)
				.setMessages(Lists.newArrayList(messageHelper.getMessage("assignment.reschedule.missing_work")))
				.setRedirect("/assignments");
		}

		List<Long> workIds = Lists.newArrayList();
		for (String workNumber : workNumbers) {
			WorkResponse workResponse;
			try {
				workResponse = getWork(
					workNumber,
					ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO),
					ImmutableSet.of(AuthorizationContext.ADMIN),
					"edit_price"
				);
			} catch (Exception e) {
				continue;
			}
			workIds.add(workResponse.getWork().getId());
		}

		if (CollectionUtilities.isEmpty(workIds)) {
			return response
				.setSuccessful(false)
				.setMessages(Lists.newArrayList(messageHelper.getMessage("assignment.reschedule.none.applicable")))
				.setRedirect("/assignments");
		}

		logger.info(String.format("Will try to reschedule %d assignments", CollectionUtils.size(workNumbers)));

		WorkRepriceEvent event = new WorkRepriceEvent()
			.setWorkIds(workIds)
			.setPricingStrategyId(pricingStrategyId)
			.setFlatPrice(flatPrice)
			.setPerHourPrice(perHourPrice)
			.setMaxNumberOfHours(maxNumberOfHours)
			.setPerUnitPrice(perUnitPrice)
			.setMaxNumberOfUnits(maxNumberOfUnits)
			.setInitialPerHourPrice(initialPerHourPrice)
			.setInitialNumberOfHours(initialNumberOfHours)
			.setAdditionalPerHourPrice(additionalPerHourPrice)
			.setMaxBlendedNumberOfHours(maxBlendedNumberOfHours)
			.setPricingMode(pricingMode);

		eventRouter.sendEvent(event);

		return response.setMessages(Lists.newArrayList("Successfully submitted assignment reprice action."));
	}

	@RequestMapping(
		value = "/edit_price/{workNumber}.json",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder submitJsonEditPrice(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "pricing", required = false) Long pricingStrategyId,
		@RequestParam(value = "flat_price", required = false) Double flatPrice,
		@RequestParam(value = "per_hour_price", required = false) Double perHourPrice,
		@RequestParam(value = "max_number_of_hours", required = false) Double maxNumberOfHours,
		@RequestParam(value = "per_unit_price", required = false) Double perUnitPrice,
		@RequestParam(value = "max_number_of_units", required = false) Double maxNumberOfUnits,
		@RequestParam(value = "initial_per_hour_price", required = false) Double initialPerHourPrice,
		@RequestParam(value = "initial_number_of_hours", required = false) Double initialNumberOfHours,
		@RequestParam(value = "additional_per_hour_price", required = false) Double additionalPerHourPrice,
		@RequestParam(value = "max_blended_number_of_hours", required = false) Double maxBlendedNumberOfHours,
		@RequestParam(value = "pricing_mode", required = false) String pricingMode) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN
		), "edit_price");

		Work work = workResponse.getWork();

		WorkDTO dto = new WorkDTO();
		dto.setPricingStrategyId(pricingStrategyId);
		dto.setFlatPrice(flatPrice);
		dto.setPerHourPrice(perHourPrice);
		dto.setMaxNumberOfHours(maxNumberOfHours);
		dto.setPerUnitPrice(perUnitPrice);
		dto.setMaxNumberOfUnits(maxNumberOfUnits);
		dto.setInitialPerHourPrice(initialPerHourPrice);
		dto.setInitialNumberOfHours(initialNumberOfHours);
		dto.setAdditionalPerHourPrice(additionalPerHourPrice);
		dto.setMaxBlendedNumberOfHours(maxBlendedNumberOfHours);
		dto.setUseMaxSpendPricingDisplayModeFlag("spend".equals(pricingMode));

		MessageBundle bundle = messageHelper.newBundle();
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		try {
			List<ConstraintViolation> violations = workService.repriceWork(work.getId(), dto);

			if (isEmpty(violations)) {
				messageHelper.addSuccess(bundle, "assignment.edit_price.success");
				return response
					.setSuccessful(true)
					.setMessages(bundle.getSuccess());
			} else {
				BindingResult bind = ValidationMessageHelper.newBindingResult("assignment.edit_price");
				ValidationMessageHelper.rejectViolations(violations, bind);
				messageHelper.setErrors(bundle, bind);
			}
		} catch (InsufficientFundsException e) {
			logger.error(e);
			messageHelper.addError(bundle, "insufficient_funds.assignment.edit_price");
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.edit_price.exception");
		}

		return response.setMessages(bundle.getErrors());
	}

	@RequestMapping(
		value = "/ask_question/{workNumber}",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_ACCEPTWORK1', 'PERMISSION_ACCEPTWORK3', 'PERMISSION_ACCEPTWORKD')")
	public String askQuestionShow(
		@PathVariable("workNumber") String workNumber,
		Model model) {

		AbstractWork work = getWorkByNumber(workNumber);

		model.addAttribute("work", work);

		return "web/partials/assignments/ask_question";
	}

	@RequestMapping(
		value = "/ask_question/{workNumber}",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_ACCEPTWORK1', 'PERMISSION_ACCEPTWORK3', 'PERMISSION_ACCEPTWORKD')")
	public String askQuestionSubmit(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "question", required = false) String question,
		RedirectAttributes flash) {

		AbstractWork work = getWorkByNumber(workNumber);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (StringUtils.isBlank(question)) {
			messageHelper.addError(bundle, "assignment.ask_question.empty");
			return "redirect:/assignments/details/" + workNumber;
		}

		WorkQuestionAnswerPair response = workQuestionService.saveQuestion(work.getId(), getCurrentUser().getId(), question);

		if (response != null) {
			messageHelper.addSuccess(bundle, "assignment.ask_question.success");
		} else {
			messageHelper.addError(bundle, "assignment.ask_question.exception");
		}

		return "redirect:/assignments/details/" + workNumber;
	}

	@RequestMapping(
		value = "/answer_question/{workNumber}",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK')")
	public String answerQuestion(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "question_id", required = false) Long questionId,
		@RequestParam(value = "answer", required = false) String answer,
		RedirectAttributes flash) {

		if (!StringUtils.isNumeric(workNumber) || questionId == null)
			return "redirect:/assignments/details/{workNumber}";

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (StringUtils.isEmpty(answer)) {
			messageHelper.addError(bundle, "assignment.answer_question.answerempty");
		} else {
			WorkQuestionAnswerPair response = workQuestionService.saveAnswerToQuestion(questionId, getCurrentUser().getId(), answer, workService.findWorkId(workNumber));

			if (response != null) {
				messageHelper.addSuccess(bundle, "assignment.answer_question.success");
			} else {
				messageHelper.addError(bundle, "assignment.answer_question.exception");
			}
		}

		return "redirect:/assignments/details/{workNumber}";
	}


	@RequestMapping(
		value = "/edit_support_contact/{workNumber}",
		method = GET)
	public String showEditSupportContact(
		@PathVariable("workNumber") String workNumber,
		Model model) throws Exception {

		AbstractWork work = getAndAuthorizeWorkByNumber(workNumber, ImmutableList.of(
				WorkContext.OWNER,
				WorkContext.COMPANY_OWNED),
			"assignment.edit_support_contact.notallowed");

		model.addAttribute("work", work);
		model.addAttribute("users", getAllCompanyUsersMap(getCurrentUser().getCompanyId()));

		return "web/partials/assignments/edit_support_contact";
	}


	@RequestMapping(
		value = "/edit_support_contact/{workNumber}",
		method = POST)
	public String submitEditSupportContact(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "support_contact", required = false) Long supportContact,
		RedirectAttributes flash) throws Exception {

		AbstractWork work = getAndAuthorizeWorkByNumber(workNumber, ImmutableList.of(
				WorkContext.OWNER,
				WorkContext.COMPANY_OWNED),
			"assignment.edit_support_contact.notallowed");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		try {
			workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap(
				"buyerSupportUser.id", Long.toString(supportContact)
			));

			workChangeLogService.saveWorkChangeLog(new WorkSupportContactChangedChangeLog(work.getId(), getCurrentUser().getId()));
			messageHelper.addSuccess(bundle, "assignment.edit_support_contact.success");
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));

		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.edit_support_contact.exception");
		}

		return "redirect:/assignments/details/{workNumber}";
	}


	@RequestMapping(
		value = "/edit_internal_owner/{workNumber}",
		method = GET)
	public String showEditInternalOwner(
		@PathVariable("workNumber") String workNumber,
		Model model) throws Exception {

		AbstractWork work = getAndAuthorizeWorkByNumber(workNumber, ImmutableList.of(
			WorkContext.OWNER,
			WorkContext.COMPANY_OWNED),
			"assignment.edit_internal_owner.notallowed");

		model.addAttribute("work", work);
		model.addAttribute("users", getAllCompanyUsersMap(getCurrentUser().getCompanyId()));

		return "web/partials/assignments/edit_internal_owner";
	}

	@RequestMapping(
		value = "/edit_internal_owner/{workNumber}",
		method = POST)
	public String submitEditInternalOwner(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "internal_owner", required = false) Long internalOwner,
		RedirectAttributes flash) throws Exception {

		AbstractWork work = getAndAuthorizeWorkByNumber(workNumber, ImmutableList.of(
			WorkContext.OWNER,
			WorkContext.COMPANY_OWNED),
			"assignment.edit_internal_owner.notallowed");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		try {
			workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap(
				"internalOwner.id", Long.toString(internalOwner)
			));

			workChangeLogService.saveWorkChangeLog(new WorkInternalOwnerChangedChangeLog(work.getId(), getCurrentUser().getId()));
			messageHelper.addSuccess(bundle, "assignment.edit_internal_owner.success");
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));

		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.edit_internal_owner.exception");
		}

		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/edit_location_contact/{workNumber}",
		method = GET)
	public String showEditLocationContact(
		@PathVariable("workNumber") String workNumber,
		RedirectAttributes flash,
		Model model) throws Exception {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.LOCATION_CONTACT_INFO,
			WorkRequestInfo.LOCATION_INFO,
			WorkRequestInfo.CLIENT_COMPANY_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN
		), "edit_location_contact");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		Work work = workResponse.getWork();
		Location location = work.getLocation();

		if (location == null) {
			messageHelper.addError(bundle, "assignment.edit_location_contact.nolocation");
			return "redirect:/assignments/details/{workNumber}";
		}

		model.addAttribute("work", work);

		// If a client company is associated, pull all contacts for the given location;
		// otherwise pull a list of unassociated CRM contacts.
		Company clientCompany = work.getClientCompany();
		List<ClientContact> contacts;
		if (clientCompany != null) {
			contacts = crmService.findAllClientContactsByLocation(location.getId(), true);
		} else {
			contacts = crmService.findIndividualClientContactsByClientCompanyId(getCurrentUser().getCompanyId());
		}

		Map<Long, Object> clientContacts = Maps.newHashMap();
		Map<Long, Object> clientContactsJson = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(contacts)) {
			for (ClientContact contact : contacts) {
				clientContacts.put(contact.getId(), contact.getFullName());

				Phone phone = contact.getMostRecentWorkPhone();
				Email email = contact.getMostRecentEmail();
				clientContactsJson.put(contact.getId(), CollectionUtilities.newStringMap(
					"first_name", contact.getFirstName(),
					"last_name", contact.getLastName(),
					"work_phone", (phone == null) ? "" : phone.getPhone(),
					"work_phone_extension", (phone == null) ? "" : phone.getExtension(),
					"email", (email == null) ? "" : email.getEmail()
				));
			}
		}

		// If there are no contacts and no client company is selected, manually add the entered contacts
		if (isEmpty(contacts) && work.getLocationContact() != null) {
			ClientContact contact = crmService.findClientContactById(work.getLocationContact().getId());
			if (contact != null) {
				clientContacts.put(contact.getId(), contact.getFullName());

				Phone phone = contact.getMostRecentWorkPhone();
				Email email = contact.getMostRecentEmail();
				clientContactsJson.put(contact.getId(), CollectionUtilities.newObjectMap(
					"first_name", contact.getFirstName(),
					"last_name", contact.getLastName(),
					"work_phone", (phone == null) ? "" : phone.getPhone(),
					"work_phone_extension", (phone == null) ? "" : phone.getExtension(),
					"email", (email == null) ? "" : email.getEmail(),
					"phone_id", (phone == null) ? null : phone.getId(),
					"email_id", (email == null) ? null : email.getId()
				));
			}
		}

		if (isEmpty(contacts) && work.getSecondaryLocationContact() != null) {
			ClientContact contact = crmService.findClientContactById(work.getSecondaryLocationContact().getId());
			if (contact != null) {
				clientContacts.put(contact.getId(), contact.getFullName());

				Phone phone = contact.getMostRecentWorkPhone();
				Email email = contact.getMostRecentEmail();
				clientContactsJson.put(contact.getId(), CollectionUtilities.newObjectMap(
					"first_name", contact.getFirstName(),
					"last_name", contact.getLastName(),
					"work_phone", (phone == null) ? "" : phone.getPhone(),
					"work_phone_extension", (phone == null) ? "" : phone.getExtension(),
					"email", (email == null) ? "" : email.getEmail(),
					"phone_id", (phone == null) ? null : phone.getId(),
					"email_id", (email == null) ? null : email.getId()
				));
			}
		}

		model.addAttribute("client_contacts", clientContacts);
		model.addAttribute("client_contacts_json", jsonService.toJson(clientContactsJson));
		model.addAttribute("form_edit_location_contact", new EditLocationContactForm());

		return "web/partials/assignments/edit_location_contact";
	}

	@RequestMapping(
		value = "/edit_location_contact/{workNumber}",
		method = POST)
	public String submitEditLocationContact(
		@PathVariable("workNumber") String workNumber,
		@ModelAttribute("form_edit_location_contact") EditLocationContactForm form,
		RedirectAttributes flash) throws Exception {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.CLIENT_COMPANY_INFO,
			WorkRequestInfo.LOCATION_CONTACT_INFO,
			WorkRequestInfo.LOCATION_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN
		), "edit_location_contact");

		Work work = workResponse.getWork();
		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		com.workmarket.thrift.core.Location location = work.getLocation();
		if (location == null) {
			messageHelper.addError(bundle, "assignment.edit_location_contact.nolocation");
			return "redirect:/assignments/details/{workNumber}";
		}

		Map<String, String> updateWorkProperties = Maps.newHashMap();
		Long companyId = getCurrentUser().getCompanyId();

		// Determine if this is a CRM location or a one-time location.
		if (work.isSetClientCompany()) {
			String onsiteContact = form.getOnsite_contact();

			// Did they select an existing contact or create a new one?
			if (StringUtils.isNumeric(onsiteContact)) {
				updateWorkProperties.put("serviceClientContact.id", onsiteContact);
			} else if (StringUtils.equals("new", onsiteContact)) {
				ClientContactDTO dto = form.toOnsiteContactDTO(false);
				dto.setClientCompanyId(work.getClientCompany().getId());
				dto.setClientLocationId(location.getId());

				ClientContact onsiteContactResult = crmService.saveOrUpdateClientContact(companyId, dto, null);

				if (onsiteContactResult != null) {
					if (StringUtils.isNotEmpty(form.getContactphone())) {
						crmService.addPhoneToClientContact(onsiteContactResult.getId(), form.toOnsitePhoneNumberDTO(false));
					}

					if (StringUtils.isNotEmpty(form.getContactemail())) {
						crmService.addEmailToClientContact(onsiteContactResult.getId(), form.toOnsiteEmailAddressDTO(false));
					}

					updateWorkProperties.put("serviceClientContact.id", onsiteContactResult.getId().toString());
				} else {
					messageHelper.addError(bundle, "assignment.edit_location_contact.exception");
				}
			} else {
				updateWorkProperties.put("serviceClientContact.id", "");
			}

			String secondaryOnsiteContact = form.getOnsite_secondary_contact();
			if (StringUtils.isNumeric(secondaryOnsiteContact)) {
				updateWorkProperties.put("secondaryServiceClientContact.id", secondaryOnsiteContact);
			} else if (StringUtils.equals("new", secondaryOnsiteContact)) {
				ClientContactDTO dto = form.toSecondaryOnsiteContactDTO(false);
				dto.setClientCompanyId(work.getClientCompany().getId());
				dto.setClientLocationId(location.getId());

				ClientContact secondaryContactResult = crmService.saveOrUpdateClientContact(companyId, dto, null);

				if (secondaryContactResult != null) {
					if (StringUtils.isNotEmpty(form.getSecondarycontactphone())) {
						crmService.addPhoneToClientContact(secondaryContactResult.getId(), form.toSecondaryOnsitePhoneNumberDTO(false));
					}

					if (StringUtils.isNotEmpty(form.getSecondarycontactemail())) {
						crmService.addEmailToClientContact(secondaryContactResult.getId(), form.toSecondaryOnsiteEmailAddressDTO(false));
					}

					updateWorkProperties.put("secondaryServiceClientContact.id", secondaryContactResult.getId().toString());
				} else {
					messageHelper.addError(bundle, "assignment.edit_location_contact.exception");
				}
			} else {
				updateWorkProperties.put("secondaryServiceClientContact.id", "");
			}

		} else {

			String onsiteContact = form.getOnsite_contact();
			if (StringUtils.isNotBlank(onsiteContact)) {

				ClientContactDTO dto = form.toOnsiteContactDTO(StringUtils.isNumeric(onsiteContact));
				ClientContact onsiteContactResult = crmService.saveOrUpdateClientContact(companyId, dto, null);

				if (onsiteContactResult != null) {
					PhoneNumberDTO phoneDTO = form.toOnsitePhoneNumberDTO(true);

					if (phoneDTO.getEntityId() == null) {
						crmService.addPhoneToClientContact(onsiteContactResult.getId(), phoneDTO);
					} else {
						directoryService.saveOrUpdatePhoneNumber(phoneDTO);
					}

					EmailAddressDTO emailDTO = form.toOnsiteEmailAddressDTO(true);

					if (emailDTO.getEntityId() == null) {
						crmService.addEmailToClientContact(onsiteContactResult.getId(), emailDTO);
					} else {
						directoryService.saveOrUpdateEmailAddress(emailDTO);
					}

					updateWorkProperties.put("serviceClientContact.id", onsiteContactResult.getId().toString());
				}
			} else {
				updateWorkProperties.put("serviceClientContact.id", "");
			}

			String secondaryContact = form.getOnsite_secondary_contact();
			if (StringUtils.isNotBlank(secondaryContact)) {
				ClientContactDTO dto = form.toSecondaryOnsiteContactDTO(StringUtils.isNumeric(secondaryContact));
				ClientContact secondaryContactResult = crmService.saveOrUpdateClientContact(companyId, dto, null);

				if (secondaryContactResult != null) {
					PhoneNumberDTO phoneDTO = form.toSecondaryOnsitePhoneNumberDTO(true);

					if (phoneDTO.getEntityId() == null) {
						crmService.addPhoneToClientContact(secondaryContactResult.getId(), phoneDTO);
					} else {
						directoryService.saveOrUpdatePhoneNumber(phoneDTO);
					}

					EmailAddressDTO emailDTO = form.toSecondaryOnsiteEmailAddressDTO(true);

					if (emailDTO.getEntityId() == null) {
						crmService.addEmailToClientContact(secondaryContactResult.getId(), emailDTO);
					} else {
						directoryService.saveOrUpdateEmailAddress(emailDTO);
					}
					updateWorkProperties.put("secondaryServiceClientContact.id", secondaryContactResult.getId().toString());

				}
			} else {
				updateWorkProperties.put("secondaryServiceClientContact.id", "");
			}
		}

		if (!bundle.hasErrors()) {
			if (!updateWorkProperties.isEmpty()) {
				try {
					workService.updateWorkProperties(work.getId(), updateWorkProperties);
					messageHelper.addSuccess(bundle, "assignment.edit_location_contact.success");
					eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));

					return "redirect:/assignments/details/{workNumber}";

				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
					logger.error(e);
				}

				messageHelper.addError(bundle, "assignment.edit_location_contact.exception");

				return "redirect:/assignments/details/{workNumber}";
			} else {
				messageHelper.addSuccess(bundle, "assignment.edit_location_contact.success");
			}
		}

		return "redirect:/assignments/details/{workNumber}";
	}

	private WorkSubStatusTypeFilter filterLabels(WorkResponse workResponse) {
		Boolean isOwner = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);
		Boolean isInternal = getCurrentUser().hasAnyRoles("ROLE_INTERNAL");
		Boolean isActiveResource = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE);

		WorkSubStatusTypeFilter filter = new WorkSubStatusTypeFilter();
		filter.setShowSystemSubStatus(true);
		filter.setShowCustomSubStatus(true);
		filter.setShowDeactivated(false);
		filter.setClientVisible(isOwner || isInternal);
		filter.setResourceVisible(isActiveResource);

		return filter;
	}

	@RequestMapping(
		value = "/validate_label/{workNumber}/{labelId}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder validateLabel(
		@PathVariable("workNumber") String workNumber,
		@PathVariable("labelId") Long labelId) {
		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.SCHEDULE_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "validateLabel");

		Long workId = workResponse.getWork().getId();
		WorkSubStatusTypeFilter filter = filterLabels(workResponse);

		List<WorkSubStatusType> labels = workSubStatusService.findAllEditableSubStatusesByWork(workId, filter);
		Map<Long, WorkSubStatusType> labelLookup = AbstractEntityUtilities.newEntityIdMap(labels);

		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (!labelLookup.containsKey(labelId)) {
			return response.setSuccessful(false);
		}

		// Validation
		try {
			Boolean validationResult = workSubStatusService.validateAddSubStatus(workId, labelId);
			return response.setSuccessful(validationResult);
		} catch (Exception e) {
			logger.error(e);
			return response.setSuccessful(false);
		}
	}

	@RequestMapping(
		value = "/add_label/{workNumber}",
		method = GET)
	public String addLabel(
		@PathVariable("workNumber") String workNumber,
		Model model) {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.SCHEDULE_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "add_label");

		Long workId = workResponse.getWork().getId();
		Boolean isActiveResource = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE);
		WorkSubStatusTypeFilter filter = filterLabels(workResponse);

		List<WorkSubStatusType> workLabels = workSubStatusService.findAllEditableSubStatusesByWork(workId, filter);

		Map<Long, Map<String, Object>> labelsMap = Maps.newLinkedHashMap();

		if (CollectionUtils.isNotEmpty(workLabels)) {
			for (WorkSubStatusType label : workLabels) {
				labelsMap.put(label.getId(), CollectionUtilities.newObjectMap(
					"id", label.getId(),
					"code", label.getCode(),
					"description", label.getDescription(),
					"instructions", label.getSubStatusDescriptor().getInstructions(),
					"sub_status_type", label.getSubStatusDescriptor().getSubStatusType().name(),
					"triggered_by", label.getSubStatusDescriptor().getTriggeredBy().name(),
					"action_resolvable", label.getSubStatusDescriptor().isActionResolvable(),
					"user_resolvable", label.getSubStatusDescriptor().isUserResolvable(),
					"client_visible", label.getSubStatusDescriptor().getClientVisible(),
					"resource_visible", label.getSubStatusDescriptor().getResourceVisible(),
					"custom_color_rgb", label.getCustomColorRgb(),
					"is_active", label.isActive(),
					"alert", label.getSubStatusDescriptor().isAlert(),
					"is_custom", label.getSubStatusDescriptor().isCustom(),
					"is_notify_client_enabled", label.getSubStatusDescriptor().isNotifyClientEnabled(),
					"is_notify_resource_enabled", label.getSubStatusDescriptor().isNotifyResourceEnabled(),
					"is_include_instructions", label.getSubStatusDescriptor().isIncludeInstructions(),
					"is_note_required", label.getSubStatusDescriptor().isNoteRequired(),
					"is_schedule_required", label.getSubStatusDescriptor().isScheduleRequired(),
					"is_remove_after_reschedule", label.getSubStatusDescriptor().isRemoveAfterReschedule(),
					"is_resource_editable", label.isResourceEditable()
				));
			}
		}

		model.addAttribute("workStatusTypes", ModelEnumUtilities.workStatusTypes);
		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("isActiveResource", isActiveResource);
		model.addAttribute("labels", labelsMap);
		model.addAttribute("label_json", jsonService.toJson(labelsMap));
		model.addAttribute("form", new AddLabelForm());

		return "web/partials/assignments/add_label";
	}

	@RequestMapping(
		value = "/request_calendar_sync_access",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder requestCalendarSyncAccess() {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		String authUrl = googleCalendarService.getCalendarAuthURL();

		if (StringUtils.isNotBlank(authUrl)) {
			response.setSuccessful(true);
			response.addData("authUrl", authUrl);
		}
		return response;
	}

	@RequestMapping(
		value = "/calendar_sync_settings",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder validateCalendarAccess() {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);
		boolean isAuthorizedToWM = googleCalendarService.isAuthorizedToWM(getCurrentUser().getId());
		boolean hasCalendarSettings = googleCalendarService.hasCalendarSettings(getCurrentUser().getId());

		if (isAuthorizedToWM) {
			Map<String, String> calendars = googleCalendarService.getCalendars(getCurrentUser().getId());
			if (calendars != null) {
				response.addData("calendars", calendars);
			}
		}

		response.addData("has_settings", hasCalendarSettings);
		response.setSuccessful(isAuthorizedToWM);
		return response;
	}

	@RequestMapping(
		value = "/cancel_calendar_sync",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder cancelCalendarSync() {
		AjaxResponseBuilder responseBuilder = new AjaxResponseBuilder().setSuccessful(false);

		if (googleCalendarService.isAuthorizedToWM(getCurrentUser().getId())) {
			googleCalendarService.cancelSync(getCurrentUser().getId());
		}

		responseBuilder.setSuccessful(true);

		return responseBuilder;
	}

	@RequestMapping(
		value = "/save_calendar_sync_settings",
		method = POST)
	public @ResponseBody AjaxResponseBuilder saveCalendarSyncSettings(
		@RequestParam(value = "newCalendar") boolean newCalendar,
		@RequestParam(value = "calendarId") String calendarId,
		@RequestParam(value = "calendarName") String calendarName) {

		AjaxResponseBuilder responseBuilder = new AjaxResponseBuilder().setSuccessful(false);

		responseBuilder.setSuccessful(googleCalendarService.saveCalendarSyncSettings(getCurrentUser().getId(), calendarId, calendarName, newCalendar));
		return responseBuilder;
	}

	@RequestMapping(
		value = "/get_labels_mult_assignments",
		method = GET)
	public String addMultipleLabel(
		@RequestParam("workNumbers") List<String> workNumbers,
		Model model) {

		List<WorkSubStatusType> workLabels = new ArrayList<>();
		boolean isDraft = false;
		List<WorkResponse> workResponses = null;

		if (CollectionUtilities.isEmpty(workNumbers)) {
			model.addAttribute("success", false);
			model.addAttribute("errorMessages", messageHelper.getMessage("assignment.add_bulk_label.no_assignments"));

			return "web/partials/assignments/add_bulk_label";
		}
		try {
			workResponses = getWorks(workNumbers, ImmutableSet.of(
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.SCHEDULE_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ADMIN,
				AuthorizationContext.ACTIVE_RESOURCE
			), "add_label");
		} catch (Exception e) {
			model.addAttribute("success", false);
			model.addAttribute("errorMessages", messageHelper.getMessage("assignment.add_bulk_label.no_permission"));

			return "web/partials/assignments/add_bulk_label";
		}
		if (CollectionUtilities.isEmpty(workResponses)) {
			model.addAttribute("success", false);
			model.addAttribute("errorMessages", messageHelper.getMessage("assignment.add_bulk_label.none_retrieved"));

			return "web/partials/assignments/add_bulk_label";
		}
		try {
			WorkSubStatusTypeFilter filter = filterLabels(workResponses.get(0));
			workLabels.addAll(workSubStatusService.findAllEditableSubStatusesByWork(workResponses.get(0).getWork().getId(), filter));
		} catch (Exception e) {
			model.addAttribute("success", false);
			model.addAttribute("errorMessages", messageHelper.getMessage("assignment.add_bulk_label.no_labels"));

			return "web/partials/assignments/add_bulk_label";
		}
		for (WorkResponse workResponse : workResponses) {
			Long workId = workResponse.getWork().getId();
			WorkStatusType type = workService.findWorkByWorkNumber(workResponse.getWork().getWorkNumber()).getWorkStatusType();
			if (type.isDraft()) {
				isDraft = true;
			}
			WorkSubStatusTypeFilter filter = filterLabels(workResponse);
			workLabels.retainAll(workSubStatusService.findAllEditableSubStatusesByWork(workId, filter));
		}
		Map<Long, Map<String, Object>> labelsMap = Maps.newLinkedHashMap();

		if (CollectionUtils.isNotEmpty(workLabels)) {
			for (WorkSubStatusType label : workLabels) {
				labelsMap.put(label.getId(), CollectionUtilities.newObjectMap(
					"id", label.getId(),
					"code", label.getCode(),
					"description", label.getDescription(),
					"instructions", label.getSubStatusDescriptor().getInstructions(),
					"sub_status_type", label.getSubStatusDescriptor().getSubStatusType().name(),
					"triggered_by", label.getSubStatusDescriptor().getTriggeredBy().name(),
					"action_resolvable", label.getSubStatusDescriptor().isActionResolvable(),
					"user_resolvable", label.getSubStatusDescriptor().isUserResolvable(),
					"client_visible", label.getSubStatusDescriptor().getClientVisible(),
					"resource_visible", label.getSubStatusDescriptor().getResourceVisible(),
					"custom_color_rgb", label.getCustomColorRgb(),
					"is_active", label.isActive(),
					"alert", label.getSubStatusDescriptor().isAlert(),
					"is_custom", label.getSubStatusDescriptor().isCustom(),
					"is_notify_client_enabled", label.getSubStatusDescriptor().isNotifyClientEnabled(),
					"is_notify_resource_enabled", label.getSubStatusDescriptor().isNotifyResourceEnabled(),
					"is_include_instructions", label.getSubStatusDescriptor().isIncludeInstructions(),
					"is_note_required", label.getSubStatusDescriptor().isNoteRequired(),
					"is_schedule_required", label.getSubStatusDescriptor().isScheduleRequired(),
					"is_remove_after_reschedule", label.getSubStatusDescriptor().isRemoveAfterReschedule(),
					"is_resource_editable", label.isResourceEditable()
				));
			}
		}
		model.addAttribute("success", true);
		model.addAttribute("errorMessages", "none");
		model.addAttribute("labels", labelsMap);
		model.addAttribute("label_json", jsonService.toJson(labelsMap));
		model.addAttribute("isThereDraft", isDraft);

		return "web/partials/assignments/add_bulk_label";
	}

	@RequestMapping(
		value = "/drop_add_label/{workNumber}/{labelId}",
		method = GET)
	public String dropAddLabel(
		@PathVariable("workNumber") String workNumber,
		@PathVariable("labelId") Long labelId,
		Model model) {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.SCHEDULE_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "add_label");

		Boolean isActiveResource = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE);
		Work work = workResponse.getWork();
		String timeZoneName = work.getTimeZone();

		WorkSubStatusType label = workSubStatusService.findWorkStatusById(labelId);
		Map<String, Object> labelMap = CollectionUtilities.newObjectMap(
			"id", label.getId(),
			"code", label.getCode(),
			"description", label.getDescription(),
			"instructions", label.getSubStatusDescriptor().getInstructions(),
			"sub_status_type", label.getSubStatusDescriptor().getSubStatusType().name(),
			"triggered_by", label.getSubStatusDescriptor().getTriggeredBy().name(),
			"action_resolvable", label.getSubStatusDescriptor().isActionResolvable(),
			"user_resolvable", label.getSubStatusDescriptor().isUserResolvable(),
			"client_visible", label.getSubStatusDescriptor().getClientVisible(),
			"resource_visible", label.getSubStatusDescriptor().getResourceVisible(),
			"custom_color_rgb", label.getCustomColorRgb(),
			"is_active", label.isActive(),
			"alert", label.getSubStatusDescriptor().isAlert(),
			"is_custom", label.getSubStatusDescriptor().isCustom(),
			"is_notify_client_enabled", label.getSubStatusDescriptor().isNotifyClientEnabled(),
			"is_notify_resource_enabled", label.getSubStatusDescriptor().isNotifyResourceEnabled(),
			"is_include_instructions", label.getSubStatusDescriptor().isIncludeInstructions(),
			"is_note_required", label.getSubStatusDescriptor().isNoteRequired(),
			"is_schedule_required", label.getSubStatusDescriptor().isScheduleRequired(),
			"is_remove_after_reschedule", label.getSubStatusDescriptor().isRemoveAfterReschedule(),
			"is_resource_editable", label.isResourceEditable()
		);

		model.addAttribute("workStatusTypes", ModelEnumUtilities.workStatusTypes);
		model.addAttribute("work", work);
		model.addAttribute("isActiveResource", isActiveResource);
		model.addAttribute("label", labelMap);
		model.addAttribute("label_json", jsonService.toJson(labelMap));
		model.addAttribute("form", new AddLabelForm());
		model.addAttribute("assignment_tz_millis_offset", java.util.TimeZone.getTimeZone(timeZoneName).getOffset(Calendar.getInstance().getTimeInMillis()));

		return "web/partials/assignments/drop_add_label";
	}

	private AjaxResponseBuilder validateAddLabelPostAction(
		BindingResult bindingResult,
		AddLabelForm form,
		RedirectAttributes flash,
		Work work,
		WorkResponse workresponse,
		Long labelId) {
		Long workId = work.getId();
		DateRange dateRange = null;
		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		AjaxResponseBuilder response = new AjaxResponseBuilder();
		WorkSubStatusTypeFilter filter = filterLabels(workresponse);
		List<WorkSubStatusType> labels = workSubStatusService.findAllEditableSubStatusesByWork(workId, filter);
		Map<Long, WorkSubStatusType> labelLookup = AbstractEntityUtilities.newEntityIdMap(labels);
		if (!labelLookup.containsKey(labelId)) {
			messageHelper.addError(bundle, "assignment.add_label.not_authorized");
			return response.setSuccessful(false).setMessages(bundle.getErrors());
		}
		WorkSubStatusType label = labelLookup.get(labelId);
		Boolean isScheduleRequired = (label.isScheduleRequired() && CollectionUtilities.containsAny(work.getStatus().getCode(), WorkStatusType.ACTIVE));
		// Validation
		if (label.isNoteRequired()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "note", "NotNull");
		}
		if (isScheduleRequired) {
			String tz = work.getTimeZone();
			dateRange = new DateRange(form.getFrom(tz), form.getTo(tz));
			dateRangeValidator.validate(dateRange, bindingResult);
		}
		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return response
				.setSuccessful(false)
				.setMessages(bundle.getErrors());
		}
		return response
			.setSuccessful(true)
			.setMessages(bundle.getSuccess())
			.addData("workNumber", work.getWorkNumber())
			.addData("labelId", labelId)
			.addData("labelDescription", label.getDescription())
			.addData("dateRange", dateRange)
			.addData("isScheduleRequired", isScheduleRequired);
	}

	private AjaxResponseBuilder doAddLabelPostAction(
		BindingResult bindingResult,
		AddLabelForm form,
		RedirectAttributes flash,
		Work work,
		Long labelId,
		WorkSubStatusType label,
		Boolean wasLabelDropped) {
		Long workId = work.getId();
		DateRange dateRange = null;
		Boolean isScheduleRequired = (label.isScheduleRequired() && CollectionUtilities.containsAny(work.getStatus().getCode(), WorkStatusType.ACTIVE));

		// Validation
		if (label.isNoteRequired()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "note", "NotNull");
		}
		if (isScheduleRequired) {
			String tz = work.getTimeZone();
			dateRange = new DateRange(form.getFrom(tz), form.getTo(tz));

			dateRangeValidator.validate(dateRange, bindingResult);
		}

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		AjaxResponseBuilder response = new AjaxResponseBuilder();
		if (!wasLabelDropped) {
			response.setRedirect("/assignments/details/" + workId.toString());
		}

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return response
				.setSuccessful(false)
				.setMessages(bundle.getErrors());
		}

		try {
			workSubStatusService.addSubStatus(workId, labelId, form.getNote());

			if (isScheduleRequired && dateRange != null) {
				workService.setAppointmentTime(work.getId(), dateRange, null);
			}

			messageHelper.addSuccess(bundle, "assignment.add_label.success");
			return response
				.setSuccessful(true)
				.setMessages(bundle.getSuccess())
				.addData("workNumber", work.getWorkNumber())
				.addData("labelId", labelId)
				.addData("labelDescription", label.getDescription());
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.add_label.exception");
			return response
				.setSuccessful(false)
				.setMessages(bundle.getErrors());
		}
	}

	@RequestMapping(
		value = "/add_label/{workNumber}",
		method = POST)
	public @ResponseBody AjaxResponseBuilder addLabel(
		@PathVariable String workNumber,
		@ModelAttribute AddLabelForm form,
		BindingResult bindingResult,
		RedirectAttributes flash) {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.SCHEDULE_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "add_label");

		Long workId = workResponse.getWork().getId();
		WorkSubStatusTypeFilter filter = filterLabels(workResponse);
		List<WorkSubStatusType> labels = workSubStatusService.findAllEditableSubStatusesByWork(workId, filter);
		Map<Long, WorkSubStatusType> labelLookup = AbstractEntityUtilities.newEntityIdMap(labels);
		Long labelId = form.getLabel_id();

		if (!labelLookup.containsKey(labelId)) {
			throw new HttpException401()
				.setMessageKey("NotNull.add_label_form.label_id")
				.setRedirectUri("redirect:/assignments/details/{workNumber}");
		}

		WorkSubStatusType label = labelLookup.get(labelId);

		return doAddLabelPostAction(bindingResult, form, flash, workResponse.getWork(), labelId, label, false);
	}

	@RequestMapping(
		value = "/add_label_reschedule_multiple",
		method = POST)
	public @ResponseBody AjaxResponseBuilder addMultipleLabels(
		@RequestParam("workNumbers") List<String> workNumbers,
		@ModelAttribute("form") AddLabelForm form,
		BindingResult bindingResult,
		RedirectAttributes flash) {

		AjaxResponseBuilder response = new AjaxResponseBuilder();
		List<WorkResponse> workResponses;
		Long labelId = form.getLabel_id();
		if (labelId == null) {
			messageHelper.addMessage(response, "NotNull.add_label_form.label_id");
			return response.setSuccessful(false);
		}
		try {
			workResponses = getWorks(workNumbers, ImmutableSet.of(
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.SCHEDULE_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ADMIN,
				AuthorizationContext.ACTIVE_RESOURCE
			), "add_label");
		} catch (Exception e) {
			messageHelper.addMessage(response, "assignment.add_label.not_authorized");
			return response.setSuccessful(false);
		}
		List<Long> workIds = new ArrayList<>();
		for (WorkResponse work : workResponses) {
			response = validateAddLabelPostAction(bindingResult, form, flash, work.getWork(), work, labelId);
			if (!response.isSuccessful()) {
				return response.setSuccessful(false);
			}
			workIds.add(work.getWork().getId());
		}
		DateRange dateRange = (DateRange) response.getData().get("dateRange");
		String note = form.getNote();
		Boolean isScheduleRequired = (Boolean) response.getData().get("isScheduleRequired");
		try {

			if (isScheduleRequired && dateRange != null) {
				workSubStatusService.addSubStatus(workIds, labelId, note, dateRange);
			} else {
				workSubStatusService.addSubStatus(workIds, labelId, note);
			}

			messageHelper.addMessage(response, "assignment.add_label.success");
			return response.setSuccessful(true);
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addMessage(response, "assignment.add_label.exception");
			return response.setSuccessful(false);
		}
	}

	@RequestMapping(
		value = "/drop_add_label/{workNumber}/{labelId}",
		method = POST)
	public
	@ResponseBody AjaxResponseBuilder dropAddLabel(
		@PathVariable("workNumber") String workNumber,
		@PathVariable("labelId") Long labelId,
		@ModelAttribute("form") AddLabelForm form,
		BindingResult bindingResult,
		RedirectAttributes flash) {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.SCHEDULE_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "add_label");

		WorkSubStatusType label = workSubStatusService.findWorkStatusById(labelId);

		return doAddLabelPostAction(bindingResult, form, flash, workResponse.getWork(), labelId, label, true);
	}

	@RequestMapping(
		value = "/remove_label/{workNumber}",
		method = GET)
	public String showRemoveLabel(
		@PathVariable String workNumber,
		@RequestParam(value = "label_id", required = false) Integer labelId,
		Model model) throws Exception {
		AbstractWork work = getWorkByNumber(workNumber);

		model.addAttribute("work", work);
		model.addAttribute("labelId", labelId);

		return "web/partials/assignments/remove_label";
	}

	@RequestMapping(
		value = "/drop_remove_label/{workNumber}",
		method = GET)
	public String dropRemoveLabel(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "labelId", required = false) Integer labelId,
		Model model) throws Exception {

		AbstractWork work = getWorkByNumber(workNumber);

		model.addAttribute("work", work);
		model.addAttribute("label_id", labelId);

		return "web/partials/assignments/drop_remove_label";
	}

	@RequestMapping(
		value = "/remove_label/{workNumber}",
		method = POST)
	public String submitRemoveLabel(
		@PathVariable String workNumber,
		@RequestParam(value = "label_id", required = false) Long labelId,
		@RequestParam(value = "note", required = false) String note,
		RedirectAttributes flash) {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "add_label");

		Long workId = workResponse.getWork().getId();
		WorkSubStatusType label = workSubStatusService.findWorkSubStatus(labelId);
		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		Boolean isAuthorized = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE) && WorkSubStatusType.INCOMPLETE_WORK.equals(label.getCode());
		if (!(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN) || isAuthorized)) {
			throw new HttpException401()
				.setMessageKey("assignment.remove_label.not_authorized")
				.setRedirectUri("redirect:/assignments/details/{workNumber}");
		}

		try {
			workSubStatusService.resolveSubStatus(getCurrentUser().getId(), workId, labelId, note);
			messageHelper.addSuccess(bundle, "assignment.remove_label.success");
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.remove_label.exception");
		}

		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/drop_remove_label/{workNumber}",
		method = POST)
	public @ResponseBody AjaxResponseBuilder dropRemoveLabel(
		@PathVariable String workNumber,
		@RequestParam(value = "labelId", required = false) Long labelId,
		@RequestParam(value = "note", required = false) String note,
		RedirectAttributes flash) {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "add_label");

		Long workId = workResponse.getWork().getId();
		WorkSubStatusType label = workSubStatusService.findWorkSubStatus(labelId);
		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		Boolean isAuthorized = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE) && WorkSubStatusType.INCOMPLETE_WORK.equals(label.getCode());
		if (!(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN) || isAuthorized)) {
			messageHelper.addError(bundle, "assignment.remove_label.not_authorized");
			return response.setSuccessful(false).setMessages(bundle.getErrors());
		}

		try {
			workSubStatusService.resolveSubStatus(getCurrentUser().getId(), workId, labelId, note);
			messageHelper.addSuccess(bundle, "assignment.remove_label.success");
			return response
				.setSuccessful(true)
				.setMessages(bundle.getSuccess())
				.addData("workNumber", workNumber)
				.addData("labelId", labelId);
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.remove_label.exception");
			return response.setSuccessful(false).setMessages(bundle.getErrors());
		}
	}

	/**
	 * Ignore/remove resource label
	 */

	@RequestMapping(
		value = "/{workNumber}/resources/{userNumber}/labels/{encryptedLabelId}/remove",
		method = GET)
	public String removeResourceLabel(
		@PathVariable String workNumber,
		@PathVariable String userNumber,
		@PathVariable String encryptedLabelId) {

		return "web/partials/assignments/remove_resource_label";
	}

	@RequestMapping(
		value = "/{workNumber}/resources/{userNumber}/labels/{encryptedLabelId}/remove",
		method = POST)
	public String doRemoveResourceLabel(
		@PathVariable String workNumber,
		@PathVariable String encryptedLabelId,
		RedirectAttributes flash) {

		ExtendedUserDetails user = getCurrentUser();
		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		MessageBundle messages = messageHelper.newFlashBundle(flash);
		List<WorkContext> contexts = workService.getWorkContext(work.getId(), user.getId());
		if (contexts.contains(WorkContext.OWNER) ||
			(contexts.contains(WorkContext.COMPANY_OWNED) && user.hasAnyRoles("ACL_ADMIN", "ACL_DEPUTY"))) {
			Long labelId = encryptionService.decryptId(encryptedLabelId);
			workResourceService.ignoreWorkResourceLabel(labelId);
			messageHelper.addSuccess(messages, "assignment.resources.remove_label.successful");
		} else {
			messageHelper.addError(messages, "assignment.resources.remove_label.not_authorized");
		}

		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/{workNumber}/workers/{userNumber}/removeautoassign",
		method = POST)
	@ResponseBody
	public Map<String, Object>  removeAutoAssignLabel(
		@PathVariable String workNumber,
		@PathVariable String userNumber) {

		ExtendedUserDetails user = getCurrentUser();
		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		List<WorkContext> contexts = workService.getWorkContext(work.getId(), user.getId());
		if (!contexts.contains(WorkContext.OWNER) &&
			!(contexts.contains(WorkContext.COMPANY_OWNED) && user.hasAnyRoles("ACL_ADMIN", "ACL_DEPUTY"))) {
			return CollectionUtilities.newObjectMap("error", messageHelper.getMessage("assignment.resources.remove_auto_assign_label.not_authorized"));
		}

		User worker = userService.findUserByUserNumber(userNumber);
		workResourceService.removeAutoAssign(worker.getId(), work.getId());
		return CollectionUtilities.newObjectMap("success", messageHelper.getMessage("assignment.resources.remove_auto_assign_label.successful", worker.getFullName()));
	}

	@RequestMapping(
		value = "/unassign/{workNumber}",
		method = GET)
	public String showUnassignWork(
		@PathVariable String workNumber,
		Model model) throws Exception {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO,
			WorkRequestInfo.PRICING_HISTORY_INFO,
			WorkRequestInfo.PRICING_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN
		), "unassign");

		Work work = workResponse.getWork();
		model.addAttribute("isInternal", work.getPricing().getType() == PricingStrategyType.INTERNAL);
		model.addAttribute("work", work);
		model.addAttribute("isWorkBundle", workResponse.isWorkBundle());
		model.addAttribute("isInWorkBundle", workResponse.isInWorkBundle());
		model.addAttribute("workerFullName", work.getActiveWorkerFullName());
		model.addAttribute("unassignReasons", UNASSIGN_REASONS);
		model.addAttribute("hasPriceHistory", work.getPricingHistory() != null && !work.getPricingHistory().isEmpty());
		model.addAttribute("pricingStrategyTypes", ModelEnumUtilities.pricingStrategyTypes);

		// Get original price total and pass to UI so it can be displayed in the radio button label
		PricingStrategy originalPricingStrategy = workService.getOriginalWorkPricingStategy(work.getId());
		model.addAttribute("originalPriceTotal", pricingService.calculateMaximumResourceCost(originalPricingStrategy));

		// Get current price total and pass to UI so it can be displayed in the radio button label
		PricingStrategy currentPricingStrategy = workService.getCurrentWorkPricingStategy(work.getId());
		model.addAttribute("currentPriceTotal", pricingService.calculateMaximumResourceCost(currentPricingStrategy));

		return "web/partials/assignments/unassign";
	}

	@RequestMapping(
		value = "/unassign/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder doUnassignWork(
		@PathVariable String workNumber,
		@ModelAttribute("unassign_form") UnassignDTO dto,
		BindingResult bind) throws Exception {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail().setRedirect("/assignments/details/" + workNumber);
		try {
			final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ADMIN
			), "unassign");

			dto.setWorkId(workResponse.getWork().getId());

			List<ConstraintViolation> violations = workService.unassignWorker(dto);
			if (violations.isEmpty()) {
				final String activeResourceName = workResponse.getWork().getActiveWorkerFullName();
				messageHelper.addMessage(response, "assignment.unassign.success", activeResourceName);
				response.setSuccessful(true);
			} else {
				ValidationMessageHelper.rejectViolations(violations, bind);
				messageHelper.setErrors(response, bind);
			}
		} catch (Exception e) {
			logger.error(e);
			messageHelper.clearMessages(response);
			messageHelper.addMessage(response, "assignment.unassign.exception");
		}
		return response;
	}

	@RequestMapping(
		value = "/cancel_work/{workNumber}",
		method = GET)
	public String showCancelWork(
		@PathVariable("workNumber") String workNumber,
		Model model) throws Exception {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN
		), "cancel_work");

		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("pricingStrategyType", ModelEnumUtilities.pricingStrategyTypes);
		model.addAttribute("workFee", pricingService.getCurrentFeePercentageForWork(workResponse.getWork().getId()));

		return "web/partials/assignments/cancel_work";
	}

	@RequestMapping(
		value = "/cancel_work/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder submitCancelWork(
		@PathVariable("workNumber") String workNumber,
		@ModelAttribute("form_cancel_work") CancelWorkDTO dto,
		BindingResult bind) throws Exception {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN
		), "cancel_work");

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setSuccessful(false)
			.setRedirect("/assignments/details/" + workNumber);

		dto.setWorkId(workResponse.getWork().getId());

		cancelWorkValidator.validate(dto, bind);

		if (PricingStrategyType.INTERNAL.equals(workResponse.getWork().getPricing().getType()) && dto.getPrice() > 0D) {
			messageHelper.addMessage(response, "assignment.cancel_work.noauth");
			return response;
		}

		if (bind.hasErrors()) {
			messageHelper.setErrors(response, bind);
			return response;
		}

		try {
			List<ConstraintViolation> violations = workFacadeService.cancelWork(dto);

			if (isEmpty(violations)) {
				messageHelper.addMessage(response, "assignment.cancel_work.success");
				return response.setSuccessful(true);
			}

			ValidationMessageHelper.rejectViolations(violations, bind);
			messageHelper.setErrors(response, bind);
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addMessage(response, "assignment.cancel_work.exception");
		}

		return response;
	}

	@RequestMapping(
		value = "/void_work/{workNumber}",
		method = GET)
	public String showVoidWork(
		@PathVariable("workNumber") String workNumber,
		Model model) throws Exception {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN
		), "void_work");

		model.addAttribute("work", workResponse.getWork());

		return "web/partials/assignments/void_work";
	}

	@RequestMapping(
		value = "/void_work/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder submitVoidWork(
		@PathVariable("workNumber") String workNumber,
		@RequestParam("void_note") String voidNote) throws Exception {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN
		), "void_work");

		MessageBundle bundle = messageHelper.newBundle();

		AjaxResponseBuilder response = new AjaxResponseBuilder().setRedirect("/assignments/details/" + workNumber);

		if (StringUtils.isEmpty(voidNote)) {
			messageHelper.addError(bundle, "NotEmpty", "Note");
		} else {
			try {
				List<ConstraintViolation> violations = workService.voidWork(workResponse.getWork().getId(), voidNote);

				if (isEmpty(violations)) {
					messageHelper.addMessage(response, "assignment.void_work.success");
					return response
						.setSuccessful(true)
						.setMessages(response.getMessages());
				}

				BindingResult bind = ValidationMessageHelper.newBindingResult();
				ValidationMessageHelper.rejectViolations(violations, bind);
				messageHelper.setErrors(bundle, bind);
			} catch (Exception e) {
				logger.error(e);
				messageHelper.addError(bundle, "assignment.void_work.exception");
			}
		}

		return response
			.setSuccessful(false)
			.setMessages(bundle.getErrors());
	}

	@RequestMapping(
		value = "/abandon_bundle_work/{workNumber}",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_ACCEPTWORK1', 'PERMISSION_ACCEPTWORK3', 'PERMISSION_ACCEPTWORKD')")
	public String showAbandonBundleWork(
		@PathVariable String workNumber,
		Model model) throws Exception {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.BUYER_INFO,
			WorkRequestInfo.COMPANY_INFO,
			WorkRequestInfo.CLIENT_COMPANY_INFO,
			WorkRequestInfo.PROJECT_INFO
		));

		final Set<AuthorizationContext> authContexts = workResponse.getAuthorizationContexts();

		final boolean
			isActiveResource = authContexts.contains(AuthorizationContext.ACTIVE_RESOURCE),
			isAdmin = authContexts.contains(AuthorizationContext.ADMIN);

		model.addAttribute("is_admin", isAdmin);
		model.addAttribute("is_active_resource", isActiveResource);
		model.addAttribute("work", workResponse.getWork());

		return "web/partials/assignments/abandon_bundle_work";
	}

	@RequestMapping(
		value = "/abandon_work/{workNumber}",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_ACCEPTWORK1', 'PERMISSION_ACCEPTWORK3', 'PERMISSION_ACCEPTWORKD')")
	public String showAbandonWork(
		@PathVariable("workNumber") String workNumber,
		Model model) throws Exception {

		AbstractWork work = getAndAuthorizeWorkByNumber(workNumber,
			ImmutableList.of(WorkContext.ACTIVE_RESOURCE, WorkContext.DISPATCHER), "assignment.abandon_work.notauthorized");

		model.addAttribute("work", work);

		return "web/partials/assignments/abandon_work";
	}

	@RequestMapping(
		value = "/abandon_work/{workNumber}",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_ACCEPTWORK1', 'PERMISSION_ACCEPTWORK3', 'PERMISSION_ACCEPTWORKD')")
	public String submitAbandonWork(
		@PathVariable("workNumber") String workNumber,
		@RequestParam("cancel_note") String cancelNote,
		RedirectAttributes flash) throws Exception {

		AbstractWork work = getAndAuthorizeWorkByNumber(workNumber,
			ImmutableList.of(WorkContext.ACTIVE_RESOURCE, WorkContext.DISPATCHER), "assignment.abandon_work.notauthorized");

		Long userId = getCurrentUser().getId();
		List<WorkContext> contexts = workService.getWorkContext(work.getId(), userId);

		if (contexts.contains(WorkContext.DISPATCHER)) {
			userId = workService.findActiveWorkerId(work.getId());
		}

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (StringUtils.isEmpty(cancelNote)) {
			messageHelper.addError(bundle, "NotEmpty", "Note");
		} else {
			Long workId = work.getId();
			try {
				List<ConstraintViolation> violations = workService.abandonWork(userId, workId, cancelNote);

				if (isEmpty(violations)) {
					messageHelper.addSuccess(bundle, "assignment.abandon_work.success");

					return "redirect:/assignments";
				}

				BindingResult bind = ValidationMessageHelper.newBindingResult();
				ValidationMessageHelper.rejectViolations(violations, bind);
				messageHelper.setErrors(bundle, bind);
			} catch (Exception e) {
				logger.error(e);
			}
			messageHelper.addError(bundle, "assignment.abandon_work.exception");
		}

		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/resend_resource_invitation/{workNumber}",
		method = GET)
	public String showResendResourceInvitation(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "workerNumber", required = false) String[] workerNumbers,
		Model model,
		RedirectAttributes flash) throws WorkUnauthorizedException {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN
		), "resend_resource_invitation");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("PricingStrategyType", ModelEnumUtilities.pricingStrategyTypes);
		model.addAttribute("workerNumbers", workerNumbers);

		return "web/partials/assignments/resend_resource_invitation";
	}

	@RequestMapping(
		value = "/resend_resource_invitation/{workNumber}",
		method = POST)
	public String submitResendResourceInvitation(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "workerNumber", required = false) List<String> workerNumbers,
		RedirectAttributes flash) throws WorkUnauthorizedException {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN
		), "resend_resource_invitation");

		boolean foundBlockedAssociations = false;
		Long companyId = getCurrentUser().getCompanyId();
		List<Long> idsToDelete = new ArrayList<>();
		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		List<Long> workerIds = Lists.newArrayList();
		if (workerNumbers != null) {
			workerIds.addAll(userService.findAllUserIdsByUserNumbers(workerNumbers));
		}

		if (!WorkStatusType.SENT.equals(workResponse.getWork().getStatus().getCode())) {
			messageHelper.addError(bundle, "assignment.resend_resource_invitation.wrongstatus");
			return "redirect:/assignments/details/{workNumber}";
		}

		if (workerIds.isEmpty()) {
			messageHelper.addError(bundle, "assignment.resend_resource_invitation.noresources");
			return "redirect:/assignments/details/{workNumber}";
		}

		for (Long workerId : workerIds) {
			if (userService.isCompanyBlockedByUser(workerId, companyId)) {
				logger.debug("Found blocked resource-client association between resource: " + workerId + ", company: " + companyId);
				messageHelper.addError(bundle, "assignment.resend_resource_invitation.single_resource_exception", userService.findUserById(workerId).getFullName());
				idsToDelete.add(workerId);
				foundBlockedAssociations = true;
			}
		}

		workerIds.removeAll(idsToDelete);

		try {
			Response response = workService.resendInvitationsAsync(workResponse.getWork().getId(), workerIds);
			if (response.isSuccessful()) {
				messageHelper.addSuccess(bundle, foundBlockedAssociations ?
					"assignment.resend_resource_invitation.partial_success" :
					"assignment.resend_resource_invitation.success");
				return "redirect:/assignments/details/{workNumber}";
			}
			messageHelper.addError(bundle, "assignment.resend_resource_invitation.exception");
			return "redirect:/assignments/details/{workNumber}";

		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.resend_resource_invitation.exception");
			return "redirect:/assignments/details/{workNumber}";
		}
	}

	@RequestMapping(
		value = "/delete/{workNumber}",
		method = GET)
	public String showDelete(
		@PathVariable("workNumber") String workNumber,
		Model model) throws WorkUnauthorizedException {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN
		), "delete");

		if (!WorkStatusType.DRAFT.equals(workResponse.getWork().getStatus().getCode())) {
			throw new HttpException401()
				.setMessageKey("assignment.delete.wrongstatus")
				.setRedirectUri("redirect:/assignments/details/" + workNumber);
		}

		model.addAttribute("work", workResponse.getWork());

		return "web/partials/assignments/delete";
	}

	@RequestMapping(
		value = "/delete/{workNumber}",
		method = POST)
	public String submitDelete(
		@PathVariable("workNumber") String workNumber,
		RedirectAttributes flash) throws WorkUnauthorizedException {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN
		), "delete");

		if (!WorkStatusType.DRAFT.equals(workResponse.getWork().getStatus().getCode())) {
			throw new HttpException401()
				.setMessageKey("assignment.delete.wrongstatus")
				.setRedirectUri("redirect:/assignments/details/{workNumber}");
		}

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		try {
			workService.deleteDraft(getCurrentUser().getId(), workResponse.getWork().getId());

			messageHelper.addSuccess(bundle, "assignment.delete.success");
			return "redirect:/assignments/manage";
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.delete.exception");
			return "redirect:/assignments/details/{workNumber}";
		}
	}

	@RequestMapping(
		value = "/company_payment_terms/{workNumber}",
		method = GET)
	public String companyPaymentTerms(
		@PathVariable("workNumber") String workNumber,
		Model model) throws WorkActionException {

		WorkRequest workRequest = new WorkRequest()
			.setUserId(getCurrentUser().getId())
			.setWorkNumber(workNumber)
			.setIncludes(Sets.newHashSet(
				WorkRequestInfo.BUYER_INFO,
				WorkRequestInfo.COMPANY_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
			));
		WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);
		model.addAttribute("work", workResponse.getWork());

		return "web/pages/assignments/company_payment_terms";
	}

	/**
	 * TODO: test
	 */
	@RequestMapping(
		value = "/get_assignment_history/{workNumber}",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK')")
	public String getAssignmentHistory(
		@PathVariable("workNumber") String workNumber,
		Model model) throws WorkActionException {

		WorkRequest workRequest = new WorkRequest()
			.setUserId(getCurrentUser().getId())
			.setWorkNumber(workNumber)
			.setIncludes(Sets.newHashSet(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.CHANGE_LOG_INFO
			));

		WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);
		if (workResponse != null) {
			if (!workResponse.getAuthorizationContexts().contains(AuthorizationContext.READ_ONLY)) {
				model.addAttribute("log", workResponse.getWork().getChangelog());
			}
		}

		return "web/partials/assignments/view_history";
	}

	/**
	 * TODO: test
	 */
	@RequestMapping(
		value = "/get_assignment_notes/{workNumber}",
		method = GET)
	public String getAssignmentNotes(
		@PathVariable("workNumber") String workNumber,
		Model model) throws WorkActionException {

		WorkRequest workRequest = new WorkRequest()
			.setUserId(getCurrentUser().getId())
			.setWorkNumber(workNumber)
			.setIncludes(Sets.newHashSet(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.NOTES_INFO
			));

		WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);
		if (workResponse != null) {
			if (!workResponse.getAuthorizationContexts().contains(AuthorizationContext.READ_ONLY)) {
				model.addAttribute("notes", workResponse.getWork().getNotes());
				model.addAttribute("workTimeZone", workResponse.getWork().getTimeZone());
			}
		}

		return "web/partials/assignments/view_notes";
	}

	@RequestMapping(
		value = "/add_checkout_note/{workNumber}",
		method = GET)
	public String showAddCheckoutNote(
		@PathVariable("workNumber") String workNumber,
		Model model) throws Exception {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "add_checkout_note");

		Work work = workResponse.getWork();

		model.addAttribute("work", work);
		model.addAttribute("mmw", work.getConfiguration());

		return "web/partials/assignments/add_checkout_note";
	}

	@RequestMapping(
		value = "/sendback/{id}",
		method = GET)
	public String sendBack(
		@PathVariable("id") String workNumber,
		Model model) throws Exception {

		return sendBackWithRedirect(workNumber, null, model);
	}

	@RequestMapping(
		value = "/sendback/{id}/{redirect}",
		method = GET)
	public String sendBackWithRedirect(
		@PathVariable("id") String workNumber,
		@PathVariable("redirect") String redirect,
		Model model) throws Exception {

		ExtendedUserDetails user = getCurrentUser();

		// Check for account suspension.
		// TODO: this needs to be handled gracefully, which will require a bit of front-end rework
		com.workmarket.domains.model.Company company = companyService.findCompanyById(user.getCompanyId());
		if (company.isSuspended()) {
			throw new HttpException401().setMessageKey("assignment.sendback.suspended").setRedirectUri("redirect:/assignment/details/" + workNumber);
		}

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN,
			AuthorizationContext.BUYER
		), "sendback");

		if (!WorkStatusType.COMPLETE.equals(workResponse.getWork().getStatus().getCode())) {
			throw new HttpException403().setMessageKey("assignment.sendback.incomplete");
		}

		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("redirect", redirect);

		return "web/partials/assignments/sendback";
	}

	/**
	 * Sends a assignment back to the assigned resource.
	 */
	@RequestMapping(
		value = "/sendback/{workNumber}",
		method = POST)
	public String doSendBack(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "redirect", required = false) String redirect,
		@RequestParam(value = "reason", required = false) String reason,
		RedirectAttributes flash) throws Exception {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		ExtendedUserDetails user = getCurrentUser();

		if (StringUtils.equals("assignments", redirect)) {
			redirect = String.format("/%s#status/complete/managing", redirect);
		}

		String redirectUrl = StringUtils.isNotBlank(redirect) ? String.format("redirect:%s", redirect) : "redirect:/assignments/details/{workNumber}";

		// Check for account suspension.
		com.workmarket.domains.model.Company company = companyService.findCompanyById(user.getCompanyId());
		if (company.isSuspended()) {
			messageHelper.addError(bundle, "assignment.sendback.suspended");
			return redirectUrl;
		}

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN,
			AuthorizationContext.BUYER
		), "sendback");

		if (!WorkStatusType.COMPLETE.equals(workResponse.getWork().getStatus().getCode())) {
			messageHelper.addError(bundle, "assignment.sendback.incomplete");
			return redirectUrl;
		}

		if (StringUtils.isBlank(reason)) {
			messageHelper.addError(bundle, "assignment.sendback.reason.required");
		} else {
			workService.incompleteWork(workResponse.getWork().getId(), reason);
			messageHelper.addSuccess(bundle, "assignment.sendback.sent");
		}

		return redirectUrl;
	}

	@RequestMapping(
		value = "/create_update_rating/{workNumber}",
		method = GET)
	public String createOrUpdateRating(
		@PathVariable("workNumber") String workNumber,
		Model model) {

		com.workmarket.domains.work.model.Work work = workService.findWorkByWorkNumber(workNumber);
		Long activeResourceId = workService.findActiveWorkResource(work.getId()).getUser().getId();
		RatingWorkData data = ratingService.findLatestRatingDataForResourceByWorkNumber(activeResourceId, workNumber);

		model.addAttribute("rating", data);
		model.addAttribute("workNumber", workNumber);
		model.addAttribute("workId", work.getId());
		model.addAttribute("ratedUserId", activeResourceId);

		if (data!= null) {
			model.addAttribute("value", data.getRatingValue());
			model.addAttribute("quality", data.getRatingQuality());
			model.addAttribute("professionalism", data.getRatingProfessionalism());
			model.addAttribute("communication", data.getRatingCommunication());
			model.addAttribute("review", data.getRatingReview());
			model.addAttribute("modified_on", data.getModifiedOn() == null ? "" : data.getModifiedOn().toString());
			model.addAttribute("raterUserName", data.getRaterUserName());
			model.addAttribute("ratedUserName", data.getRatedUserName());
		}
		return "web/partials/assignments/create_update_rating";
	}

	@RequestMapping(
		value = "/create_update_rating/{workNumber}",
		method = POST)
	public String doCreateOrUpdateRating(
		@PathVariable("workNumber") String workNumber,
		@Valid FeedbackForm form) {

		RatingDTO ratingDTO = form.getRating();
		ratingService.updateLatestRatingForUserForWork(form.getWorkId(), form.getRatedUserId(), ratingDTO);
		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/stop_payment/{workNumber}",
		method = GET)
	public String stopPaymentForm(
		@PathVariable("workNumber") String workNumber,
		Model model) {

		model.addAttribute("workNumber", workNumber);

		return "web/partials/assignments/stop_payment";
	}

	/**
	 * Stop payment and send assignment back to the assigned resource.
	 *
	 * @return
	 */
	@RequestMapping(
		value = "/stop_payment/{workNumber}",
		method = POST)
	public String doStopPayment(
		@PathVariable("workNumber") String workNumber,
		@Valid @ModelAttribute("stop_payment") StopPaymentForm form,
		BindingResult bindingResult,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return "redirect:/assignments/details/{workNumber}";
		}

		Long workId = workService.findWorkId(workNumber);

		StopPaymentDTO dto = new StopPaymentDTO();
		dto.setReason(form.getReason());

		try {
			List<ConstraintViolation> violations = workService.stopWorkPayment(workId, dto);

			if (isEmpty(violations)) {
				messageHelper.addSuccess(bundle, "assignment.stop_payment.success");
				return "redirect:/assignments/details/{workNumber}";
			} else {
				BindingResult bind = ValidationMessageHelper.newBindingResult("assignment.stop_payment");
				ValidationMessageHelper.rejectViolations(violations, bind);
				messageHelper.setErrors(bundle, bind);
			}
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.stop_payment.exception");
		}

		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/{workNumber}/send_reminder_to_complete",
		method = GET)
	public String sendReminderToComplete(
		@PathVariable String workNumber,
		Model model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN
		));

		if (!WorkStatusType.ACTIVE.equals(workResponse.getWork().getStatus().getCode())) {
			throw new HttpException403().setMessageKey("assignment.invalid_status");
		}

		model.addAttribute("resource", workResponse.getWork().getActiveResource());
		model.addAttribute("maxMsgLength", MAX_MESSAGE_LENGTH);

		return "web/partials/assignments/send_reminder_to_complete";
	}

	@RequestMapping(
		value = "/{workNumber}/send_reminder_to_complete",
		method = RequestMethod.POST)
	public String doSendReminderToComplete(
		@PathVariable String workNumber,
		@RequestParam String message,
		RedirectAttributes flash) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN
		));

		if (!WorkStatusType.ACTIVE.equals(workResponse.getWork().getStatus().getCode())) {
			throw new HttpException403().setMessageKey("assignment.invalid_status");
		}

		BindingResult bindingResult = ValidationMessageHelper.newBindingResult();

		if (StringUtils.isEmpty(message)) {
			bindingResult.rejectValue("message", "NotNull", CollectionUtilities.newArray("message"), "Message is required");
		}

		if (StringUtils.length(message) > MAX_MESSAGE_LENGTH) {
			bindingResult.rejectValue("message", "CharMax", CollectionUtilities.newArray("Message", MAX_MESSAGE_LENGTH), "Message longer than max allowed.");
		}

		MessageBundle messages = messageHelper.newFlashBundle(flash);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
		} else {
			workService.remindResourceToComplete(workResponse.getWork().getId(), message);
			messageHelper.addSuccess(messages, "assignment.send_reminder_to_complete.success");
		}

		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/complete_work_on_behalf/{workNumber}",
		method = GET)
	public String completeWorkOnBehalf(
		@PathVariable String workNumber) {
		return "web/partials/assignments/buyer_complete";
	}

	/**
	 * Creates a mapping between user ID and fullname
	 */
	private Map<Long, String> getAllCompanyUsersMap(Long companyId) {
		return profileService.findAllActiveUsersByCompanyId(companyId);
	}

	@ModelAttribute("is_subscription")
	protected Boolean isSubscription() throws Exception {
		if (isAuthenticated()) {
			return companyService.findCompanyById(getCurrentUser().getCompanyId())
				.getPaymentConfiguration()
				.isSubscriptionPricing();
		}
		return Boolean.FALSE;
	}
}
