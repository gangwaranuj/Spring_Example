package com.workmarket.web.controllers.mobile;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.model.pricing.PricingStrategyUtilities;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.web.controllers.assignments.BaseWorkController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.assignments.WorkNegotiationForm;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/mobile/assignments")
public class MobileWorkNegotiationController extends BaseWorkController {

	private static final Log logger = LogFactory.getLog(MobileWorkNegotiationController.class);

	@Autowired private AnalyticsService analyticsService;

	@ModelAttribute("PricingStrategyType")
	private Map<String, Object> getPricingStrategyTypes() {
		return ModelEnumUtilities.pricingStrategyTypes;
	}

	/**
	 * Budget negotiation
	 */
	@RequestMapping(
		value = "/budgetincrease/{workNumber}",
		method = GET)
	public String budgetIncrease(@PathVariable("workNumber") String workNumber, Model model) {

		Work work = getWorkForActiveResourceNegotiation(workNumber, "mobile.budgetincrease");

		model.addAttribute("title", "Budget Increase");
		model.addAttribute("work", work);
		model.addAttribute("form_request_budget_increase", new WorkNegotiationForm());
		return "mobile/pages/v2/assignments/budget_increase";
	}


	@RequestMapping(
		value = "/budgetincrease/{workNumber}",
		method = POST)
	public String doBudgetIncrease(
		@PathVariable("workNumber") String workNumber,
		@Valid @ModelAttribute("budgetIncreaseForm") WorkNegotiationForm form,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) {

		Work work = getWorkForActiveResourceNegotiation(workNumber, "mobile.budgetincrease");

		model.addAttribute("title", "Budget Increase");
		model.addAttribute("work", work);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mobile/assignments/budgetincrease/{workNumber}";
		}
		// check that the set value is higher than what exists on the work object already
		if (PricingStrategyUtilities.compareThriftPricingStrategyToValue(work.getPricing(), form.getSetValue()) != -1) {
			messageHelper.addError(bundle, "Size.budgetIncreaseForm");
			return "redirect:/mobile/assignments/budgetincrease/{workNumber}";
		}
		if (StringUtils.isBlank(form.getNote())) {
			messageHelper.addError(bundle, "NotEmpty.budgetIncreaseForm.note");
			return "redirect:/mobile/assignments/budgetincrease/{workNumber}";
		}

		try {
			WorkNegotiationResponse result = createBudgetIncreaseNegotiation(work.getId(), form.toDTO(work));
			if (result.isSuccessful()) {
				messageHelper.addSuccess(bundle, "assignment.mobile.budgetincrease.success");
				return "redirect:/mobile/assignments/details/{workNumber}";
			}
		} catch (Exception e) {
			logger.error(e);
		}

		messageHelper.addError(bundle, "assignment.mobile.budgetincrease.exception");
		return "redirect:/mobile/assignments/details/{workNumber}";
	}


	/**
	 * reimbursement negotiation
	 */
	@RequestMapping(
		value = "/reimbursement/{workNumber}",
		method = GET)
	public String reimbursement(@PathVariable("workNumber") String workNumber, Model model) {

		Work work = getWorkForActiveResourceNegotiation(workNumber, "mobile.reimbursement");

		model.addAttribute("title", "Expense Reimbursement");
		model.addAttribute("work", work);
		model.addAttribute("reimbursementForm", new WorkNegotiationForm());

		return "mobile/pages/v2/assignments/reimbursement";
	}


	@RequestMapping(
		value = "/reimbursement/{workNumber}",
		method = POST)
	public String doReimbursement(
		@PathVariable("workNumber") String workNumber,
		@Valid @ModelAttribute("reimbursementForm") WorkNegotiationForm form,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) {

		Work work = getWorkForActiveResourceNegotiation(workNumber, "mobile.reimbursement");

		model.addAttribute("title", "Expense Reimbursement");
		model.addAttribute("work", work);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mobile/assignments/reimbursement/{workNumber}";
		}

		if (form.getAdditional_expenses() == null)
			messageHelper.addError(bundle, "NotNull.reimbursementForm.additional_expenses");
		if (StringUtils.isBlank(form.getNote()))
			messageHelper.addError(bundle, "NotEmpty.reimbursementForm.note");
		if (bundle.hasErrors())
			return "redirect:/mobile/assignments/reimbursement/{workNumber}";

		try {
			WorkNegotiationResponse result = createExpenseIncreaseNegotiation(work.getId(), form.toDTO(work));
			if (result != null && result.isSuccessful()) {
				messageHelper.addSuccess(bundle, "assignment.mobile.reimbursement.success");
				return "redirect:/mobile/assignments/details/{workNumber}";
			}
		} catch (Exception e) {
			logger.error(e);
		}

		messageHelper.addError(bundle, "assignment.mobile.reimbursement.exception");
		return "redirect:/mobile/assignments/details/{workNumber}";
	}


	/**
	 * reimbursement negotiation
	 */
	@RequestMapping(
		value = "/bonus/{workNumber}",
		method = GET)
	public String showBonus(@PathVariable("workNumber") String workNumber, Model model) {

		Work work = getWorkForActiveResourceNegotiation(workNumber, "mobile.bonus");

		model.addAttribute("title", "Request Bonus");
		model.addAttribute("work", work);
		model.addAttribute("bonusForm", new WorkNegotiationForm());

		return "mobile/pages/v2/assignments/bonus";
	}


	@RequestMapping(
		value = "/bonus/{workNumber}",
		method = POST)
	public String doBonus(
		@PathVariable("workNumber") String workNumber,
		@Valid @ModelAttribute("bonusForm") WorkNegotiationForm form,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) {

		Work work = getWorkForActiveResourceNegotiation(workNumber, "mobile.bonus");

		model.addAttribute("title", "Request Bonus");
		model.addAttribute("work", work);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mobile/assignments/bonus/{workNumber}";
		}

		if (form.getBonus() == null || form.getBonus() == 0)
			messageHelper.addError(bundle, "NotNull.bonusForm.bonus");
		if (StringUtils.isBlank(form.getNote()))
			messageHelper.addError(bundle, "NotEmpty.bonusForm.note");
		if (bundle.hasErrors())
			return "redirect:/mobile/assignments/bonus/{workNumber}";

		try {
			WorkNegotiationResponse result = createBonusNegotiation(work.getId(), form.toDTO(work));
			if (result != null && result.isSuccessful()) {
				messageHelper.addSuccess(bundle, "assignment.mobile.bonus.success");
				return "redirect:/mobile/assignments/details/{workNumber}";
			}
		} catch (Exception e) {
			logger.error(e);
		}

		messageHelper.addError(bundle, "assignment.mobile.bonus.exception");
		return "redirect:/mobile/assignments/details/{workNumber}";
	}


	/**
	 * Counteroffer negotiation
	 */
	@RequestMapping(
		value = "/negotiate/{workNumber}",
		method = GET)
	public String negotiate(@PathVariable("workNumber") String workNumber, Model model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.LOCATION_CONTACT_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
			WorkRequestInfo.COMPANY_INFO
		), ImmutableSet.of(
			AuthorizationContext.RESOURCE,
			AuthorizationContext.ACTIVE_RESOURCE,
			AuthorizationContext.ADMIN
		), "mobile.negotiate");

		com.workmarket.domains.work.model.Work work = workService.findWork(workResponse.getWork().getId());

		model.addAttribute("form_negotiate_assignment", new WorkNegotiationForm());
		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("buyerScoreCard", analyticsService.getBuyerScoreCardByUserId(work.getBuyer().getId()));
		model.addAttribute("title", "Counteroffer");
		model.addAttribute("maxSpendOfAssignment", pricingService.getMaxSpendOfAssignment(workResponse.getWork()));
		model.addAttribute("is_employee", getCurrentUser().isSeller() && getCurrentUser().getCompanyId().equals(workResponse.getWork().getCompany().getId()));

		return "mobile/pages/v2/home/apply";
	}

	@RequestMapping(
		value = "/negotiate/{workNumber}",
		method = POST)
	public String doNegotiate(
		@PathVariable("workNumber") String workNumber,
		@Valid @ModelAttribute("form_negotiate_assignment") WorkNegotiationForm form,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.LOCATION_CONTACT_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		), ImmutableSet.of(
			AuthorizationContext.RESOURCE,
			AuthorizationContext.ACTIVE_RESOURCE,
			AuthorizationContext.ADMIN
		), "mobile.negotiate");

		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("title", "Counteroffer");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mobile/assignments/negotiate/{workNumber}";
		}

		try {
			WorkNegotiationResponse result = workNegotiationService.createNegotiation(workResponse.getWork().getId(), form.toDTO(workResponse.getWork()));
			if (result != null && result.isSuccessful()) {
				messageHelper.addSuccess(bundle, "assignment.mobile.negotiate.success");
				return "redirect:/mobile/assignments/details/{workNumber}";
			}
		} catch (IllegalStateException e) {
			messageHelper.addError(bundle, "assignment.mobile.negotiate.empty");
			return "redirect:/mobile/assignments/negotiate/{workNumber}";
		} catch (Exception e) {
			logger.error("", e);
			messageHelper.addError(bundle, "assignment.mobile.negotiate.exception");
			return "redirect:/mobile/assignments/negotiate/{workNumber}";
		}
		messageHelper.addError(bundle, "assignment.mobile.negotiate.exception");
		return "redirect:/mobile/assignments/negotiate/{workNumber}";
	}

	/**
	 * Apply negotiation
	 */
	@RequestMapping(
		value = "/apply/{workNumber}",
		method = GET)
	public String apply(@PathVariable("workNumber") String workNumber, Model model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.LOCATION_CONTACT_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
			WorkRequestInfo.COMPANY_INFO
		), ImmutableSet.of(
			AuthorizationContext.RESOURCE
		), "mobile.apply");

		model.addAttribute("form_negotiate_assignment", new WorkNegotiationForm());
		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("title", "Apply");
		model.addAttribute("maxSpendOfAssignment", pricingService.getMaxSpendOfAssignment(workResponse.getWork()));
		model.addAttribute("is_employee", getCurrentUser().isSeller() && getCurrentUser().getCompanyId().equals(workResponse.getWork().getCompany().getId()));

		return "mobile/pages/v2/home/apply";
	}

	@RequestMapping(
		value = "/apply/{workNumber}",
		method = POST)
	public String doApply(
		@PathVariable("workNumber") String workNumber,
		@Valid @ModelAttribute("form_negotiate_assignment") WorkNegotiationForm form,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) {

		WorkResponse workResponse = getWorkForApplication(workNumber);
		Work work = workResponse.getWork();
		model.addAttribute("work", work);
		model.addAttribute("title", "Apply");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mobile/assignments/apply/{workNumber}";
		}

		try {
			WorkNegotiationResponse result = workNegotiationService.createApplyNegotiation(work.getId(), form.toDTO(work));
			if (result != null && result.isSuccessful()) {
				messageHelper.addSuccess(bundle, "assignment.mobile.apply.success");
				return "redirect:/mobile/assignments/details/{workNumber}";
			}
		} catch (IllegalStateException e) {
			bundle.addError(e.getMessage());
			return "redirect:/mobile/assignments/details/{workNumber}";
		} catch (Exception e) {
			logger.error("", e);
			messageHelper.addError(bundle, "assignment.mobile.apply.exception");
			return "redirect:/mobile/assignments/details/{workNumber}";
		}
		messageHelper.addError(bundle, "assignment.mobile.apply.exception");
		return "redirect:/mobile/assignments/details/{workNumber}";
	}

	/**
	 * reschedule
	 */
	@RequestMapping(
		value = "/reschedule/{workNumber}",
		method = GET)
	public String reschedule(@PathVariable() String workNumber, Model model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		), ImmutableSet.of(
			AuthorizationContext.ACTIVE_RESOURCE
		), "mobile.reschedule");

		Work work = workResponse.getWork();
		if (work.getConfiguration().isDisablePriceNegotiation()) {
			throw new HttpException401();
		}

		model.addAttribute("work", work);
		model.addAttribute("reschedule", new WorkNegotiationForm());

		return "mobile/pages/v2/assignments/reschedule";
	}


	@RequestMapping(
		value = "/reschedule/{workNumber}",
		method = POST)
	public String doReschedule(
		@PathVariable() String workNumber,
		@Valid @ModelAttribute("rescheduleForm") WorkNegotiationForm form,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		), ImmutableSet.of(
			AuthorizationContext.ACTIVE_RESOURCE
		), "mobile.reschedule");

		Work work = workResponse.getWork();

		model.addAttribute("work", work);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mobile/assignments/reschedule/{workNumber}";
		}

		try {
			workNegotiationService.createRescheduleNegotiation(workResponse.getWork().getId(), form.toDTO(workResponse.getWork()));
			messageHelper.addSuccess(bundle, "assignment.reschedule.success");
			return "redirect:/mobile/assignments/details/{workNumber}";
		} catch (IllegalStateException e) {
			bundle.addError(e.getMessage());
			return "mobile/pages/assignments/reschedule";
		} catch (Exception e) {
			logger.error("", e);
			messageHelper.addError(bundle, "assignment.reschedule.exception");
			return "mobile/pages/assignments/reschedule";
		}
	}


	public Work getWorkForActiveResourceNegotiation(String workNumber, String messageKey) {
		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SUPPORT_CONTACT_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		), ImmutableSet.of(
			AuthorizationContext.ACTIVE_RESOURCE
		), messageKey);

		if (workResponse.getWork().getConfiguration().isDisablePriceNegotiation())
			throw new HttpException401();

		return workResponse.getWork();
	}

	public WorkResponse getWorkForApplication(String workNumber) {
		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
				WorkRequestInfo.BUYER_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.COMPANY_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		), ImmutableSet.of(
				AuthorizationContext.RESOURCE
		), "mobile.apply");

		if (workResponse == null || workResponse.getWork() == null) {
			throw new HttpException404();
		}
		return workResponse;
	}

	public WorkNegotiationResponse createBudgetIncreaseNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception {
		return workNegotiationService.createBudgetIncreaseNegotiation(workId, dto);
	}

	public WorkNegotiationResponse createExpenseIncreaseNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception {
		return workNegotiationService.createExpenseIncreaseNegotiation(workId, dto);
	}

	public WorkNegotiationResponse createBonusNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception {
		return workNegotiationService.createBonusNegotiation(workId, dto);
	}
}
