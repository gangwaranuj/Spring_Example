package com.workmarket.web.controllers.assignments;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.pricing.PricingStrategyUtilities;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.domains.velvetrope.guest.ThriftGuest;
import com.workmarket.domains.velvetrope.rope.AvoidScheduleConflictsThriftRope;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.SpendLimitNegotiationType;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.event.work.WorkBundleDeclineOfferEvent;
import com.workmarket.service.business.status.WorkNegotiationResponseStatus;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.project.InsufficientBudgetException;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.utility.WebUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.forms.assignments.WorkNegotiationForm;
import com.workmarket.web.forms.assignments.WorkRescheduleForm;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.DateRangeValidator;
import groovy.lang.Tuple2;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/assignments")
public class WorkNegotiationController extends BaseWorkController {

	private static final Log logger = LogFactory.getLog(WorkNegotiationController.class);

	@Autowired private PricingService pricingService;
	@Autowired private DateRangeValidator dateRangeValidator;
	@Autowired private WorkValidationService workValidationService;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Qualifier("avoidScheduleConflictsThriftDoorman")
	@Autowired private Doorman doorman;
	@Autowired private EventRouter eventRouter;

	private static final ImmutableMap<String, String> NEGOTIATION_EXTENSION_UNITS = new ImmutableMap.Builder<String, String>()
		.put("MINUTE", "Minutes")
		.put("HOUR", "Hours")
		.put("DAY", "Days")
		.put("WEEK", "Weeks")
		.build();

	@RequestMapping(
		value = "/negotiate/{workNumber}",
		method = GET)
	public String negotiate(
		@PathVariable String workNumber,
		Model model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
			WorkRequestInfo.COMPANY_INFO
		), ImmutableSet.of(
			AuthorizationContext.RESOURCE
		), "negotiate");

		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("PricingStrategyType", ModelEnumUtilities.pricingStrategyTypes);
		model.addAttribute("maxSpendOfAssignment", pricingService.getMaxSpendOfAssignment(workResponse.getWork()));
		model.addAttribute("form_negotiate_assignment", new WorkNegotiationForm());
		model.addAttribute("is_employee", getCurrentUser().isSeller() && getCurrentUser().getCompanyId().equals(workResponse.getWork().getCompany().getId()));

		return "web/partials/assignments/negotiate";
	}

	@RequestMapping(
		value = "/negotiate/{workNumber}",
		method = POST)
	public String negotiateSubmit(
		@PathVariable String workNumber,
		@Valid @ModelAttribute("form_negotiate_assignment") WorkNegotiationForm form,
		BindingResult bindingResult,
		@RequestBody String formString,
		RedirectAttributes flash) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		), ImmutableSet.of(
			AuthorizationContext.RESOURCE,
			AuthorizationContext.DISPATCHER
		), "negotiate");

		Work work = workResponse.getWork();
		String onBehalfOf = "";

		// Is this a groovy form, or do we need to deserialize the JSON Object
		if (form.getIsform() == null) {
			String formData = StringEscapeUtils.unescapeHtml4(formString);
			form = jsonService.fromJson(formData, WorkNegotiationForm.class);
			onBehalfOf = form.getWorkerNumber();
		}

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return "redirect:/assignments/negotiate/{workNumber}";
		}

		if (!form.validScheduleNegotiation(work.getTimeZone())) {
			messageHelper.addError(bundle, "assignment.apply.schedule_negotiation.error");
			return "redirect:/assignments/details/{workNumber}";
		}

		if (!form.validOfferExpiration(work.getTimeZone())) {
			messageHelper.addError(bundle, "assignment.apply.offer_expiration.error");
			return "redirect:/assignments/details/{workNumber}";
		}

		WorkNegotiationDTO dto = form.toDTO(workResponse.getWork());

		if (StringUtils.isNotEmpty(onBehalfOf)) {
			Long userId = userService.findUserId(onBehalfOf);
			if (userId != null) {
				dto.setOnBehalfOfId(userId);
			}
		}

		try {
			workNegotiationService.createNegotiation(workResponse.getWork().getId(), dto);
			messageHelper.addSuccess(bundle, "assignment.negotiate.success");
		} catch (IllegalStateException e) {
			bundle.addError(e.getMessage());
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.negotiate.exception");
		}

		return "redirect:/assignments/details/{workNumber}";
	}


	@RequestMapping(
		value = "/budgetincrease/{workNumber}",
		method = GET)
	public String populateBudgetIncrease(
		@PathVariable String workNumber,
		Model model) throws Exception {

		WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.COMPANY_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO),
			ImmutableSet.of(
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			),
			"budgetincrease");

		Work work = workResponse.getWork();
		boolean isInWorkCompany = getCurrentUser().getCompanyId().equals(work.getCompany().getId());
		boolean isAdmin = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);
		boolean isBuyerAuthorizedToEditPrice = isInWorkCompany && (getCurrentUser().getEditPricingCustomAuth() || isAdmin);

		if (work.getConfiguration().isDisablePriceNegotiation() || (isInWorkCompany && !isBuyerAuthorizedToEditPrice)) {
			throw new HttpException401();
		}

		model.addAttribute("work", work);
		model.addAttribute("pricingStrategyTypes", ModelEnumUtilities.pricingStrategyTypes);
		model.addAttribute("negotiationReasonOptions", CollectionUtilities.newObjectMap(
			SpendLimitNegotiationType.NEED_MORE_TIME, "More work needed",
			SpendLimitNegotiationType.NEED_MORE_EXPENSES, "Expenses",
			SpendLimitNegotiationType.NEED_MORE_TIME_AND_EXPENSES, "Expenses and more work"
		));
		model.addAttribute("buyerNegotiationReasonOptions", CollectionUtilities.newObjectMap(
			SpendLimitNegotiationType.BONUS, "Bonus"
		));
		model.addAttribute("isAdminOrInternal",
			workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN) || getCurrentUser().hasAnyRoles("ROLE_INTERNAL"));
		model.addAttribute("budgetIncreaseForm", new WorkNegotiationForm());
		model.addAttribute("workFee", pricingService.getCurrentFeePercentageForWork(work.getId()));
		model.addAttribute("maxWorkFee", Constants.MAX_WORK_FEE);
		model.addAttribute("is_in_work_company", isInWorkCompany);
		model.addAttribute("isBuyerAuthorizedToEditPrice", isBuyerAuthorizedToEditPrice);

		return "web/partials/assignments/budgetincrease";
	}

	@RequestMapping(
		value = "/budgetincrease/{workNumber}",
		method = POST)
	public String submitBudgetIncrease(
		@PathVariable String workNumber,
		@Valid @ModelAttribute("budgetIncreaseForm") WorkNegotiationForm form,
		BindingResult bind,
		RedirectAttributes flash) {

		WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.COMPANY_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO),
			ImmutableSet.of(
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			),
			"budgetincrease");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/assignments/details/{workNumber}";
		}

		Work work = workResponse.getWork();
		boolean isAdmin = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);
		boolean isInWorkCompany = getCurrentUser().getCompanyId().equals(work.getCompany().getId());
		boolean isBuyerAuthorizedToEditPrice = isInWorkCompany && (getCurrentUser().getEditPricingCustomAuth() || isAdmin);

		if (work.getConfiguration().isDisablePriceNegotiation() || (isInWorkCompany && !isBuyerAuthorizedToEditPrice)) {
			throw new HttpException401();
		}

		// check that the set value is higher than what exists on the work object already
		if (PricingStrategyUtilities.compareThriftPricingStrategyToValue(work.getPricing(), form.getSetValue()) != -1) {
			messageHelper.addError(bundle, "Size.budgetIncreaseForm");
		}

		if (StringUtils.isBlank(form.getNote())) {
			messageHelper.addError(bundle, "NotEmpty.budgetIncreaseForm.note");
		}

		if (bundle.hasErrors()) {
			return "redirect:/assignments/details/{workNumber}";
		}

		WorkNegotiationDTO dto = form.toDTO(work);
		dto.setTimeZoneId(work.getTimeZoneId());
		dto.setInitiatedByResource(!isAdmin);
		dto.setPreapproved(isAdmin);
		try {
			WorkNegotiationResponse response = workNegotiationService.createBudgetIncreaseNegotiation(work.getId(), dto);

			if (response != null && response.isSuccessful()) {
				messageHelper.addSuccess(bundle, "assignment.budgetincrease.success", (isAdmin) ? "added" : "requested");
				return "redirect:/assignments/details/{workNumber}";
			}
		} catch (InsufficientFundsException e) {
			messageHelper.addError(bundle, "assignment.budgetincrease.insufficient_funds");
			return "redirect:/assignments/details/{workNumber}";
		} catch (InsufficientBudgetException e) {
			messageHelper.addError(bundle, "assignment.budgetincrease.insufficient_budget");
			return "redirect:/assignments/details/{workNumber}";
		} catch (Exception e) {
			logger.error(e);
		}

		messageHelper.addError(bundle, "assignment.budgetincrease.exception", (isAdmin) ? "add" : "request");
		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/reimbursement/{workNumber}",
		method = GET)
	public String showReimbursementRequest(
		@PathVariable String workNumber,
		Model model) throws Exception {

		WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO),
			ImmutableSet.of(
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			),
			"reimbursement");

		Work work = workResponse.getWork();

		model.addAttribute("work", work);
		model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		model.addAttribute("isAdminOrInternal",
			workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN) || getCurrentUser().hasAnyRoles("ROLE_INTERNAL"));
		model.addAttribute("reimbursementForm", new WorkNegotiationForm());
		model.addAttribute("workFee", pricingService.getCurrentFeePercentageForWork(work.getId()));
		model.addAttribute("maxWorkFee", Constants.MAX_WORK_FEE);

		return "web/partials/assignments/reimbursement";
	}

	@RequestMapping(
		value = "/reimbursement/{workNumber}",
		method = POST)
	@PreAuthorize("principal.editPricingCustomAuth")
	public String submitReimbursementRequest(
		@PathVariable String workNumber,
		@Valid @ModelAttribute("reimbursementForm") WorkNegotiationForm form,
		BindingResult bind, RedirectAttributes flash) {

		WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.CONTEXT_INFO),
			ImmutableSet.of(
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			),
			"reimbursement");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/assignments/details/{workNumber}";
		}

		Work work = workResponse.getWork();

		if (form.getAdditional_expenses() == null) {
			messageHelper.addError(bundle, "NotNull.reimbursementForm.additional_expenses");
		}

		if (StringUtils.isBlank(form.getNote())) {
			messageHelper.addError(bundle, "NotEmpty.reimbursementForm.note");
		}

		if (bundle.hasErrors()) {
			return "redirect:/assignments/details/{workNumber}";
		}

		boolean isAdmin = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);

		WorkNegotiationDTO dto = form.toDTO(work);
		dto.setTimeZoneId(work.getTimeZoneId());
		dto.setInitiatedByResource(!isAdmin);
		dto.setPreapproved(isAdmin);
		dto.setSpendLimitNegotiationTypeCode(SpendLimitNegotiationType.NEED_MORE_EXPENSES);

		try {
			WorkNegotiationResponse response = workNegotiationService.createExpenseIncreaseNegotiation(work.getId(), dto);

			if (response.isSuccessful()) {
				messageHelper.addSuccess(bundle, "assignment.reimbursement.success", (isAdmin) ? "added" : "requested");
				return "redirect:/assignments/details/{workNumber}";
			}
		} catch (InsufficientFundsException e) {
			messageHelper.addError(bundle, "assignment.reimbursement.insufficient_funds");
			return "redirect:/assignments/details/{workNumber}";
		} catch (InsufficientBudgetException e) {
			messageHelper.addError(bundle, "assignment.reimbursement.insufficient_budget");
			return "redirect:/assignments/details/{workNumber}";
		} catch (Exception e) {
			logger.error(e);
		}

		messageHelper.addError(bundle, "assignment.reimbursement.exception", (isAdmin) ? "add" : "request");
		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/bonus/{workNumber}",
		method = GET)
	public String showBonusRequest(
		@PathVariable String workNumber,
		Model model) throws Exception {

		WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO),
			ImmutableSet.of(
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			),
			"reimbursement");

		Work work = workResponse.getWork();

		model.addAttribute("work", work);
		model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		model.addAttribute("isAdminOrInternal",
			workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN) || getCurrentUser().hasAnyRoles("ROLE_INTERNAL"));
		model.addAttribute("bonusForm", new WorkNegotiationForm());
		model.addAttribute("workFee", pricingService.getCurrentFeePercentageForWork(work.getId()));
		model.addAttribute("maxWorkFee", Constants.MAX_WORK_FEE);

		return "web/partials/assignments/bonus";
	}

	@RequestMapping(
		value = "/bonus/{workNumber}",
		method = POST)
	@PreAuthorize("principal.editPricingCustomAuth")
	public String submitBonusRequest(
		@PathVariable String workNumber,
		@Valid @ModelAttribute("bonusForm") WorkNegotiationForm form,
		BindingResult bind, RedirectAttributes flash) {

		WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.CONTEXT_INFO),
			ImmutableSet.of(
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			),
			"bonus");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/assignments/details/{workNumber}";
		}

		Work work = workResponse.getWork();

		if (form.getBonus() == null || form.getBonus() == 0) {
			messageHelper.addError(bundle, "NotNull.bonusForm.bonus");
		}

		if (StringUtils.isBlank(form.getNote())) {
			messageHelper.addError(bundle, "NotEmpty.bonusForm.note");
		}

		if (bundle.hasErrors()) {
			return "redirect:/assignments/details/{workNumber}";
		}

		boolean isAdmin = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);

		WorkNegotiationDTO dto = form.toDTO(work);
		dto.setTimeZoneId(work.getTimeZoneId());
		dto.setInitiatedByResource(!isAdmin);
		dto.setPreapproved(isAdmin);

		try {
			WorkNegotiationResponse response = workNegotiationService.createBonusNegotiation(work.getId(), dto);
			if (response.getStatus().equals(WorkNegotiationResponseStatus.DUPLICATES)) {
				messageHelper.addError(bundle, "assignment.bonus.existing_bonus");
				return "redirect:/assignments/details/{workNumber}";
			}
			if (response != null && response.isSuccessful()) {
				messageHelper.addSuccess(bundle, "assignment.bonus.success", (isAdmin) ? "added" : "requested");
				return "redirect:/assignments/details/{workNumber}";
			}
		} catch (InsufficientFundsException e) {
			messageHelper.addError(bundle, "assignment.bonus.insufficient_funds");
			return "redirect:/assignments/details/{workNumber}";
		} catch (InsufficientBudgetException e) {
			messageHelper.addError(bundle, "assignment.bonus.insufficient_budget");
			return "redirect:/assignments/details/{workNumber}";
		} catch (Exception e) {
			logger.error(e);
		}

		messageHelper.addError(bundle, "assignment.bonus.exception", (isAdmin) ? "add" : "requested");
		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/accept_negotiation/{workNumber}",
		method = GET)
	public String acceptNegotiation(
		@PathVariable("workNumber") String workNumber,
		@RequestParam String id,
		@RequestParam(required = false) String onBehalfOf,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		Long negId = null;
		Long onBehalfOfUserId = null;
		User onBehalfOfUser = null;
		try {
			negId = NumberUtils.createLong(encryptionService.decrypt(id));
			if (StringUtils.isNotEmpty(onBehalfOf)) {
				onBehalfOfUser = userService.findUserByUserNumber(onBehalfOf);
				onBehalfOfUserId = (onBehalfOfUser != null) ? onBehalfOfUser.getId() : null;
			}

			WorkNegotiationResponse response = workNegotiationService.approveNegotiation(negId, onBehalfOfUserId);

			if (response.isSuccessful()) {
				if (onBehalfOfUserId != null) {
					messageHelper.addSuccess(
						bundle,
						"assignment.accept_negotiation_on_behalf_of.success",
						onBehalfOfUser.getFullName()
					);
				}
				else {
					messageHelper.addSuccess(bundle, "assignment.accept_negotiation.success");
				}
			} else {
				if (response.hasMessages()) {
					for (String message : response.getMessages()) {
						messageHelper.addError(bundle, message);
					}
				} else {
					messageHelper.addError(bundle, "assignment.accept_negotiation.exception");
				}
			}

		} catch (InsufficientFundsException e) {
			try {
				AbstractWorkNegotiation negotiation = checkNotNull(workNegotiationService.findById(negId));

				if (negotiation instanceof WorkBudgetNegotiation) {
					messageHelper.addError(bundle, "assignment.budgetincrease.insufficient_funds");
				} else if (negotiation instanceof WorkExpenseNegotiation) {
					messageHelper.addError(bundle, "assignment.reimbursement.insufficient_funds");
				} else if (negotiation instanceof WorkBonusNegotiation) {
					messageHelper.addError(bundle, "assignment.bonus.insufficient_funds");
				} else {
					messageHelper.addError(bundle, "assignment.accept_negotiation.insufficient_funds");
				}

			} catch (Exception e1) {
				logger.error(String.format("Error accepting negotiation for workNum=%s, userId=%s", workNumber, getCurrentUser().getId()), e1);
				messageHelper.addError(bundle, "assignment.accept_negotiation.exception");
			}
		} catch (Exception e) {
			logger.error(String.format("Error accepting negotiation for workNum=%s, userId=%s", workNumber, getCurrentUser().getId()), e);
			messageHelper.addError(bundle, "assignment.accept_negotiation.exception");
		}

		return "redirect:/assignments/details/" + workNumber;
	}

	/**
	 * Buyer facing
	 */
	@RequestMapping(
		value = "/decline_negotiation/{workNumber}",
		method = GET)
	public String declineNegotiation(
		@PathVariable("workNumber") String workNumber,
		RedirectAttributes flash,
		HttpServletRequest request) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		String negId = encryptionService.decrypt(request.getParameter("id"));
		String declineNote = request.getParameter("decline_negotiation_note");

		try {
			WorkNegotiationResponse response = workNegotiationService.declineNegotiation(NumberUtils.createLong(negId), declineNote, null);

			if (response.isSuccessful()) {
				if (workBundleService.isAssignmentBundle(workNumber)) {
					eventRouter.sendEvent(new WorkBundleDeclineOfferEvent(workNumber, NumberUtils.createLong(negId), declineNote));
				}
				messageHelper.addSuccess(bundle, "assignment.decline_negotiation.success");
			} else {
				if (response.hasMessages()) {
					for (String message : response.getMessages()) {
						messageHelper.addError(bundle, message);
					}
				} else {
					messageHelper.addError(bundle, "assignment.decline_negotiation.exception");
				}
			}
		} catch (Exception e) {
			logger.error(String.format("Error declining negotiation for workNum=%s, userId=%s", workNumber, getCurrentUser().getId()), e);
			messageHelper.addError(bundle, "assignment.decline_negotiation.exception");

			return "redirect:/assignments/details/" + workNumber + "#resources";
		}

		return "redirect:/assignments/details/" + workNumber;
	}

	/**
	 * Worker facing
	 */
	@RequestMapping(
		value = "/cancel_negotiation/{workNumber}",
		method = GET)
	public String cancelNegotiation(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "id", required = false) String encryptedNegId,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		try {
			Long negId = encryptionService.decryptId(encryptedNegId);

			WorkNegotiationResponse response = workNegotiationService.cancelNegotiation(negId);

			if (response.isSuccessful()) {
				messageHelper.addSuccess(bundle, "assignment.cancel_negotiation.success");
			} else {
				if (response.hasMessages()) {
					for (String message : response.getMessages()) {
						messageHelper.addError(bundle, message);
					}
				} else {
					messageHelper.addError(bundle, "assignment.cancel_negotiation.exception");
				}
			}
		} catch (Exception e) {
			logger.error(String.format("Error cancelling negotiation for workNum=%s, userId=%s", workNumber, getCurrentUser().getId()), e);
			messageHelper.addError(bundle, "assignment.cancel_negotiation.exception");
		}

		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/extend_negotiation/{workNumber}",
		method = GET)
	public String showExtendNegotiation(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "id", required = false) String encryptedNegId,
		Model model) {

		model.addAttribute("workNumber", workNumber);
		model.addAttribute("negotiationEncryptedId", encryptedNegId);
		model.addAttribute("negotiationExtensionUnits", NEGOTIATION_EXTENSION_UNITS);

		return "web/partials/assignments/extend_negotiation";
	}

	@RequestMapping(
		value = "/extend_negotiation/{workNumber}",
		method = POST)
	public String submitExtendNegotiation(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "id", required = false) String encryptedNegId,
		@RequestParam(required = false) String time,
		@RequestParam(required = false) String unit,
		RedirectAttributes flash) {

		String negId = encryptionService.decrypt(encryptedNegId);
		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (StringUtils.isNotEmpty(negId)
			&& StringUtils.isNumeric(time)
			&& StringUtils.isNotEmpty(unit)
			&& NEGOTIATION_EXTENSION_UNITS.containsKey(unit)) {
			try {
				workNegotiationService.extendNegotiationExpiration(NumberUtils.createLong(negId), NumberUtils.createInteger(time), unit);

				messageHelper.addSuccess(bundle, "assignment.extend_negotiation.success");
				return "redirect:/assignments/details/" + workNumber;

			} catch (Exception e) {
				logger.error(String.format("Error extending negotiation for workNum=%s, userId=%s", workNumber, getCurrentUser().getId()), e);
			}
		}

		messageHelper.addError(bundle, "assignment.extend_negotiation.exception");
		return "redirect:/assignments/details/" + workNumber + "#resources";
	}

	@RequestMapping(
		value = "/apply/{workNumber}",
		method = POST)
	public String applySubmit(
		Model model,
		@PathVariable("workNumber") String workNumber,
		@Valid @ModelAttribute("form_negotiate_assignment") WorkNegotiationForm form,
		@RequestParam(required = false) String onBehalfOf,
		@RequestBody String formString,
		BindingResult bindingResult,
		HttpServletRequest request,
		RedirectAttributes flash) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.COMPANY_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
			WorkRequestInfo.BUYER_INFO
		), ImmutableSet.of(
			AuthorizationContext.RESOURCE,
			AuthorizationContext.DISPATCHER
		), "negotiate");

		Work work = workResponse.getWork();
		boolean isWorkBundle = workResponse.isWorkBundle();
		boolean isAjaxRequest = WebUtilities.isAjax(request);
		String redirect = isAjaxRequest ? "/assignments/details/{workNumber}" : "redirect:/assignments/details/{workNumber}";

		if (isAjaxRequest) {
			model.addAttribute("successful", false);
		}

		// Is this a groovy form, or do we need to deserialize the JSON Object
		if (form.getIsform() == null) {
			String formData = StringEscapeUtils.unescapeHtml4(formString);
			form = jsonService.fromJson(formData, WorkNegotiationForm.class);
		}

		onBehalfOf = form.getWorkerNumber();

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bindingResult.hasErrors()) {
			if (isAjaxRequest) {
				model.addAttribute("messages", messageHelper.getMessage(bindingResult.getAllErrors().get(0)));
			} else {
				messageHelper.setErrors(bundle, bindingResult);
			}
			return redirect;
		}

		if (!form.validScheduleNegotiation(work.getTimeZone())) {
			if (isAjaxRequest) {
				model.addAttribute("messages", messageHelper.getMessage("assignment.apply.schedule_negotiation.error"));
			} else {
				messageHelper.addError(bundle, "assignment.apply.schedule_negotiation.error");
			}
			return redirect;
		}

		if (!form.validOfferExpiration(work.getTimeZone())) {
			if (isAjaxRequest) {
				model.addAttribute("messages", messageHelper.getMessage("assignment.apply.offer_expiration.error"));
			} else {
				messageHelper.addError(bundle, "assignment.apply.offer_expiration.error");
			}
			return redirect;
		}

		User worker = authenticationService.getCurrentUser();
		if (StringUtils.isNotEmpty(onBehalfOf)) {
			User onBehalfOfUser = userService.findUserByUserNumber(onBehalfOf);
			if (onBehalfOfUser != null) {
				worker = onBehalfOfUser;
			}
		}

		Set<AuthorizationContext> authorizationContexts = workResponse.getAuthorizationContexts();
		Boolean isDispatcher = authorizationContexts.contains(AuthorizationContext.DISPATCHER);

		List<AbstractWork> conflicts = Lists.newArrayList();

		doorman.welcome(
			new ThriftGuest(work.getBuyer()),
			new AvoidScheduleConflictsThriftRope(
				workResourceDAO,
				workService,
				work,
				getCurrentUser().getId(),
				conflicts
			)
		);

		if (!conflicts.isEmpty()) {
			messageHelper.addError(bundle, "assignment.apply.user_has_conflicts");
			return redirect;
		}

		if (!workValidationService.isWorkResourceValidForWork(worker.getId(), worker.getCompany().getId(), work.getCompany().getId()) &&
			!(isDispatcher && workValidationService.isWorkResourceValidForDispatch(worker.getId(), work.getCompany().getId()))) {
			String nounCapital, noun, verb;
			if (StringUtils.isNotEmpty(onBehalfOf)) {
				nounCapital = worker.getFullName();
				noun = "they";
				verb = "is";
			} else {
				nounCapital = "You";
				noun = "you";
				verb = "are";
			}

			if (isAjaxRequest) {
				model.addAttribute("messages", messageHelper.getMessage("assignment.accept.invalid_resource", nounCapital, verb, noun, work.getCompany().getName()));
			} else {
				messageHelper.addError(bundle, "assignment.accept.invalid_resource", nounCapital, verb, noun, work.getCompany().getName());
			}
			return redirect;
		}

		if (isWorkBundle) {
			workBundleService.applySubmitBundle(work.getId(), worker);
		}

		Eligibility eligibility = eligibilityService.getEligibilityFor(worker.getId(), work);
		if (!eligibility.isEligible()) {
			if (isAjaxRequest) {
				model.addAttribute("messages", messageHelper.getMessage("assignment.apply.not_eligible.error"));
			} else {
				messageHelper.addError(bundle, "assignment.apply.not_eligible.error");
			}
			return redirect;
		}

		WorkNegotiationDTO dto = form.toDTO(work);

		try {
			workNegotiationService.createApplyNegotiation(work.getId(), worker.getId(), dto);
			workSearchService.reindexWorkAsynchronous(work.getId());
			if (isAjaxRequest) {
				model.addAttribute("successful", true);
				model.addAttribute("messages", messageHelper.getMessage((isWorkBundle ? "assignment.apply.bundle.success" : "assignment.apply.success")));
			} else {
				messageHelper.addSuccess(bundle, (isWorkBundle ? "assignment.apply.bundle.success" : "assignment.apply.success"));
			}
		} catch (IllegalStateException e) {
			if (isAjaxRequest) {
				model.addAttribute("messages", e.getMessage());
			} else {
				bundle.addError(e.getMessage());
			}
		} catch (Exception e) {
			logger.error("Apply negotiation error", e);
			if (isAjaxRequest) {
				model.addAttribute("messages", messageHelper.getMessage("assignment.apply.exception"));
			} else {
				messageHelper.addError(bundle, "assignment.apply.exception");
			}
		}

		return redirect;
	}

	/**
	 * Update the schedule for an active assignment. This takes two forms, either:
	 * <ol>
	 * <li>set a specific appointment time for an assignment within the scheduled window</li>
	 * <li>negotiating a time that's different or out of the range of the current schedule</li>
	 * </ol>
	 */
	@RequestMapping(
		value = "/reschedule/{workNumber}",
		method = GET)
	public String showReschedule(@PathVariable("workNumber") String workNumber, Model model) {

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.SCHEDULE_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "reschedule");

		WorkResource resource = workService.findActiveWorkResource(workResponse.getWork().getId());

		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("resource", resource);
		model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));
		model.addAttribute("formRescheduleWork", new WorkRescheduleForm());

		return "web/partials/assignments/reschedule";
	}

	@RequestMapping(
		value = "/reschedule/{workNumber}",
		method = POST)
	public String submitReschedule(
		@PathVariable String workNumber,
		@Valid @ModelAttribute("formRescheduleWork") WorkRescheduleForm form,
		BindingResult bindingResult,
		RedirectAttributes flash) throws WorkUnauthorizedException {

		final MessageBundle messages = messageHelper.newFlashBundle(flash);

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.SCHEDULE_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "reschedule");

		final String tz = workResponse.getWork().getTimeZone();
		final DateRange dateRange = new DateRange(form.getFrom(tz), form.getTo(tz));

		final BindingResult dateBindingResult = new DataBinder(dateRange, "assignment.reschedule").getBindingResult();
		dateRangeValidator.validate(dateRange, dateBindingResult);

		messageHelper.setErrors(messages, bindingResult);
		messageHelper.setErrors(messages, dateBindingResult);

		if (messages.hasErrors()) {
			return "redirect:/assignments/details/{workNumber}";
		}

		final Tuple2<ImmutableList<String>, String> result =
			workNegotiationService.reschedule(workResponse.getWork().getId(), dateRange, form.getNotes());

		if (StringUtils.isNotBlank(result.getSecond())) {
			messages.addSuccess(result.getSecond());
		} else {
			for (final String msg : result.getFirst()) {
				messages.addError(msg);
			}
		}

		return "redirect:/assignments/details/{workNumber}";
	}
}
