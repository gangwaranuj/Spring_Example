package com.workmarket.web.validators;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.thrift.work.Work;
import com.workmarket.domains.model.pricing.PricingStrategyUtilities;
import com.workmarket.web.converters.WorkFormToThriftWorkConverter;
import com.workmarket.web.forms.work.WorkForm;
import com.workmarket.web.forms.work.WorkFormRouting;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class BulkWorkRouteValidator {

	@Autowired protected WorkFormToThriftWorkConverter toWorkConverter;
	@Autowired protected ProjectService projectService;
	@Autowired protected UserService userService;
	@Autowired protected PricingService pricingService;
	@Autowired protected MessageBundleHelper messageHelper;

	@Autowired
	@Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterServicePrefund;
	@Autowired
	@Qualifier("accountRegisterServicePaymentTermsImpl") private AccountRegisterService accountRegisterServicePaymentTerms;

	private static final Log logger = LogFactory.getLog(BulkWorkRouteValidator.class);

	public void validateMultipleWorkCopy(WorkForm workForm, int selectedNumberOfCopies, int maxNumberOfCopies, User userBuyer, MessageBundle messageBundle) {
		Work work = toWorkConverter.convert(workForm);
		Project project = null;
		if (work.isSetProject()) {
			project = projectService.findById(work.getProject().getId());
		}

		BigDecimal singleWorkCost = pricingService.calculateMaximumResourceCost(PricingStrategyUtilities.copyThrift(work.getPricing()));
		BigDecimal totalWorkCost = singleWorkCost.multiply(new BigDecimal(selectedNumberOfCopies));
		Set<BigDecimal> uniqueAssignmentPrices = Sets.newHashSet(singleWorkCost);

		// Ensure user has enough funds to route [selectedNumberOfCopies] assignments
		validateFunds(totalWorkCost, userBuyer, work, project, uniqueAssignmentPrices, messageBundle);
		// If there are multiple copies, ensure assignments are not routed to individual resources
		validateRoutingStrategy(workForm, selectedNumberOfCopies, userBuyer, messageBundle);
	}

	private void validateFunds(BigDecimal totalWorkCost, User userBuyer, Work work, Project project, Set<BigDecimal> uniqueAssignmentPrices, MessageBundle messageBundle) {
		WorkAuthorizationResponse authorizationResponse = WorkAuthorizationResponse.SUCCEEDED;
		boolean isSetPaymentTermsDays = work.getConfiguration().isSetPaymentTermsDays();

		if (isSetPaymentTermsDays) {
			authorizationResponse = accountRegisterServicePaymentTerms.authorizeMultipleWork(totalWorkCost, userBuyer, project, uniqueAssignmentPrices);
		} else {
			authorizationResponse = accountRegisterServicePrefund.authorizeMultipleWork(totalWorkCost, userBuyer, project, uniqueAssignmentPrices);
		}

		if (authorizationResponse.fail()) {
			logger.error("[BulkWorkRouteValidator] validateFunds failed for work with totalCost: " + totalWorkCost + " reason: " + authorizationResponse.toString());
			if (authorizationResponse == WorkAuthorizationResponse.INSUFFICIENT_FUNDS) {
				messageHelper.addError(messageBundle,
						isSetPaymentTermsDays ? "search.cart.push.assignment.insufficient_funds_terms" : "search.cart.push.assignment.insufficient_funds_prefund");
			} else if (authorizationResponse == WorkAuthorizationResponse.INSUFFICIENT_BUDGET) {
				messageHelper.addError(messageBundle, "search.cart.push.assignment.insufficient_budget");
			} else if (authorizationResponse == WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT) {
				messageHelper.addError(messageBundle, "search.cart.push.assignment.insufficient_spend_limit", userBuyer.getFullName());
			} else if (authorizationResponse == WorkAuthorizationResponse.INVALID_SPEND_LIMIT) {
				messageHelper.addError(messageBundle, "search.cart.push.assignment.invalid_spend_limit");
			}
		}
	}

	private void validateRoutingStrategy(WorkForm workForm, int numberOfCopies, User userBuyer, MessageBundle messageBundle) {
		if (workForm == null) {
			logger.warn("[BulkWorkRouteValidator] Skipping routing strategy validation. WorkForm is null.");
			return;
		}

		WorkFormRouting routing = workForm.getRouting();
		boolean resourceRouted = (routing != null && isNotEmpty(routing.getResourceNumbers()));

		if (resourceRouted && numberOfCopies > 1) {
			logger.error("[BulkWorkRouteValidator] Attempted to route multiple copies of an assignment to individual resources, numberOfCopies: " + numberOfCopies + ", buyerId: " + userBuyer.getId());
			messageHelper.addError(messageBundle, "work.form.error");
		}
	}
}
