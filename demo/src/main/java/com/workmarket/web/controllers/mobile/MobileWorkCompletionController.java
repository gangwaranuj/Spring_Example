package com.workmarket.web.controllers.mobile;

import com.google.common.collect.ImmutableSet;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.web.controllers.assignments.BaseWorkController;
import com.workmarket.web.exceptions.MobileHttpException400;
import com.workmarket.web.forms.assignments.WorkCompleteForm;
import com.workmarket.web.helpers.ValidationMessageHelper;
import com.workmarket.web.helpers.mobile.MobileWorkCompletionHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/mobile/assignments/complete")
public class MobileWorkCompletionController extends BaseWorkController {

	private static final Log logger = LogFactory.getLog(MobileWorkCompletionController.class);

	@Autowired MobileWorkCompletionHelper mobileWorkCompletionHelper;

	@ModelAttribute("PricingStrategyType")
	private Map<String, Object> getPricingStrategyTypes() {
		return ModelEnumUtilities.pricingStrategyTypes;
	}

	@ModelAttribute("WorkStatusType")
	private Map<String, Object> getWorkStatusTypes() {
		return ModelEnumUtilities.workStatusTypes;
	}

	@RequestMapping(
		value = "/{workNumber}",
		method = GET)
	public String complete(
		@PathVariable("workNumber") String workNumber,
		RedirectAttributes flash,
		HttpServletRequest request,
		ModelMap model) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ACTIVE_RESOURCE,
			AuthorizationContext.ADMIN
		), "mobile.complete_assignment");

		Work work = workResponse.getWork();
		double maxSpendLimit = work.getPricing().getMaxSpendLimit();
		String pricingType = work.getPricing().getType().toString();
		workResponse = getWorkForWorkDetails(workNumber);
		detailsModelHelper(workNumber, model, request, workResponse);
		model.addAttribute("workNumber", workNumber);
		model.addAttribute("work", work);
		model.addAttribute("pricingType", pricingType);
		model.addAttribute("maxSpendLimit", maxSpendLimit);
		model.addAttribute("pricingJson", jsonService.toJson(work.getPricing()));
		model.addAttribute("completion", true);

		// Get the hours & minutes worked for hourly assignments.  If hours_worked is set, use that, otherwise calc from time tracking entries.
		if (work.getActiveResource() != null
			&& CollectionUtilities.containsAny(work.getPricing().getType(), PricingStrategyType.PER_HOUR, PricingStrategyType.BLENDED_PER_HOUR)) {
			if (work.getActiveResource().getHoursWorked() > 0) {
				Period hoursMinutes = DateUtilities.getHoursAndMinutes((float) work.getActiveResource().getHoursWorked());
				model.addAttribute("hours", hoursMinutes.getHours());
				model.addAttribute("minutes", hoursMinutes.getMinutes());
			} else {
				Map<String, Object> hoursMinutes;
				if (work.getActiveResource().getUser() != null) {
					hoursMinutes = workService.findActiveWorkerTimeWorked(work.getId(), work.getActiveResource().getUser().getId());
				} else {
					hoursMinutes = workService.findActiveWorkerTimeWorked(work.getId());
				}
				if (! "0".equals(hoursMinutes.get("hours"))) {
					model.addAttribute("hours", hoursMinutes.get("hours"));
				}
				if (! "0".equals(hoursMinutes.get("minutes"))) {
					model.addAttribute("minutes", hoursMinutes.get("minutes"));
				}
			}
		}

		return "mobile/pages/v2/assignments/complete";
	}

	@RequestMapping(
		value = "/{workNumber}",
		method = POST)
	public String doComplete(
		@PathVariable("workNumber") String workNumber,
		RedirectAttributes flash,
		@Valid WorkCompleteForm form,
		BindingResult bindingResult,
		@RequestParam(value = "onBehalfOf", required = false) Long onBehalfOf) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		Company company = profileService.findCompanyById(getCurrentUser().getCompanyId());

		if (company.isSuspended()) {
			throw new MobileHttpException400()
				.setMessageKey("assignment.complete.suspended")
				.setRedirectUri("redirect:/assignments/home");
		}

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.BUYER_INFO,
			WorkRequestInfo.ASSETS_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO
		), (onBehalfOf != null) ?
			ImmutableSet.of(AuthorizationContext.ADMIN, AuthorizationContext.BUYER) :
			ImmutableSet.of(AuthorizationContext.ACTIVE_RESOURCE)
			, "complete");

		Work work = workResponse.getWork();

		if (!work.getStatus().getCode().equals(WorkStatusType.ACTIVE)) {
			throw new MobileHttpException400()
				.setMessageKey("assignment.complete.notinprogress")
				.setRedirectUri("redirect:/assignments/details/" + workNumber);
		}

		// Validate pricing
		com.workmarket.domains.model.pricing.PricingStrategy pricingStrategy = pricingService.findPricingStrategyById(work.getPricing().getId());
		if (pricingStrategy instanceof PerHourPricingStrategy || pricingStrategy instanceof BlendedPerHourPricingStrategy) {
			Integer hours = form.getHours();
			Integer minutes = form.getMinutes();
			if (hours != null && minutes != null && hours == 0 && minutes == 0) {
				messageHelper.addError(bundle, "assignment.complete.notime");
			} else {
				ValidationUtils.rejectIfEmpty(bindingResult, "hours", "hours_worked_required");
			}
		} else if (pricingStrategy instanceof PerUnitPricingStrategy) {
			ValidationUtils.rejectIfEmpty(bindingResult, "units", "units_processed_required");
		}

		// Validate additional expense override
		if (form.getAdditional_expenses() != null && form.getAdditional_expenses() > work.getPricing().getAdditionalExpenses()) {
			bindingResult.reject("additional_expenses_exceeded", new Object[]{work.getPricing().getAdditionalExpenses()}, "");
		}

		// Validate existence of required custom fields
		if (work.getCustomFieldGroupsSize() > 0) {
			for (CustomFieldGroup customFieldGroup : work.getCustomFieldGroups()) {
				if (customFieldGroup.hasFields()) {
					for (CustomField field : customFieldGroup.getFields()) {
						// if buyer validate all required fields, if worker only validate worker required fields
						if (field.isIsRequired() && StringUtils.isEmpty(field.getValue()) && (field.getType().equals(WorkCustomFieldType.RESOURCE) || !getCurrentUser().getId().equals(work.getActiveResource().getUser().getId()))) {
							messageHelper.addError(bundle, "assignment.complete.specific_custom_fields_missing", field.getName());
							break;
						}
					}
				}
			}
		}

		CompleteWorkDTO completeWork = new CompleteWorkDTO();
		completeWork.setAdditionalExpenses(form.getAdditional_expenses());
		completeWork.setBonus(work.getPricing().getBonus());
		completeWork.setResolution(form.getResolution());

		// Taxes
		if (form.isCollect_tax()) {
			ValidationUtils.rejectIfEmpty(bindingResult, "tax_percent", "NotEmpty", CollectionUtilities.newArray("tax rate"));
		}

		completeWork.setSalesTaxCollectedFlag(true); /* always true now, only thing that changes is % */
		completeWork.setSalesTaxRate(NumberUtilities.defaultValue(form.getTax_percent(), 0D));

		if (bundle.hasErrors() || bindingResult.hasErrors()){
			messageHelper.setErrors(bundle, bindingResult);
			return "redirect:/mobile/assignments/complete/" + workNumber;
		}

		if (pricingStrategy instanceof FlatPricePricingStrategy || pricingStrategy instanceof InternalPricingStrategy) {
			completeWork.setOverridePrice(form.getOverride_price());
		} else if (pricingStrategy instanceof PerHourPricingStrategy || pricingStrategy instanceof BlendedPerHourPricingStrategy) {
			completeWork.setOverridePrice(form.getOverride_price());
			completeWork.setHoursWorked(DateUtilities.getDecimalHours(form.getHours(), form.getMinutes(), Constants.PRICING_STRATEGY_ROUND_SCALE));
		} else if (pricingStrategy instanceof PerUnitPricingStrategy) {
			completeWork.setOverridePrice(form.getOverride_price());
			completeWork.setUnitsProcessed(form.getUnits());
		}

		List<ConstraintViolation> completionResult = workService.completeWork(work.getId(), onBehalfOf, completeWork);

		if (completionResult.isEmpty()) {
			workSearchService.reindexWorkAsynchronous(work.getId());
			messageHelper.addSuccess(bundle, "assignment.mobile.complete.success");
			return "redirect:/mobile/assignments/details/" + workNumber;
		} else {
			bindingResult = ValidationMessageHelper.newBindingResult();

			for (ConstraintViolation v : completionResult)
				ValidationMessageHelper.rejectViolation(v, bindingResult);

			messageHelper.setErrors(bundle, bindingResult);
			return "redirect:/mobile/assignments/complete/" + workNumber;
		}
	}

}
