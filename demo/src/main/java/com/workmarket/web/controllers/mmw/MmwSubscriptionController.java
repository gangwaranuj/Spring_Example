package com.workmarket.web.controllers.mmw;

import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;

@Controller
@RequestMapping("/mmw/subscription")
public class MmwSubscriptionController extends BaseController {
	@Autowired private SubscriptionService subscriptionService;

	@Autowired @Qualifier("accountRegisterServicePaymentTermsImpl")
	private AccountRegisterService accountRegisterService;

	@RequestMapping(method=RequestMethod.GET)
	public String index(Model model) {
		if (!getCurrentUser().isBuyer())
			throw new HttpException401();

		if (getCurrentUser().isSubscriptionEnabled()) {
			Long companyId = getCurrentUser().getCompanyId();
			SubscriptionConfiguration subscription = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(companyId);
			AccountRegisterSummaryFields accountRegisterSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(companyId);

			model.addAttribute("effectiveDate", subscription.getEffectiveDate())
				.addAttribute("subscriptionPeriod", subscription.getSubscriptionPeriod().getPeriodAsString())
				.addAttribute("paymentTermsDays", subscription.getPaymentTermsDays())
				.addAttribute("accountServiceTypeConfigurations", subscription.getAccountServiceTypeConfigurations())
				.addAttribute("subscriptionPaymentTiers", subscription.getSubscriptionPaymentTiers())
				.addAttribute("vendorOfRecord", subscription.isVendorOfRecord())
				.addAttribute("activeSubscriptionAddOns", subscription.getActiveSubscriptionAddOns())
				.addAttribute("vorThroughput", subscription.isVendorOfRecord() ? accountRegisterSummaryFields.getAssignmentSoftwareThroughput() : BigDecimal.ZERO)
				.addAttribute("softwareThroughput", accountRegisterSummaryFields.getAssignmentSoftwareThroughput());
		}

		return "web/pages/mmw/subscription/index";
	}
}
