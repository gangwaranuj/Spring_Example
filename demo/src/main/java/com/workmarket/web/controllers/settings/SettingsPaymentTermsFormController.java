package com.workmarket.web.controllers.settings;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentMethod;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.ManageMyWorkMarketDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.MmwValidator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings/manage")
public class SettingsPaymentTermsFormController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SettingsPaymentTermsFormController.class);

	@Autowired private ProfileService profileService;
	@Autowired private CompanyService companyService;
	@Autowired private BillingService billingService;
	@Autowired private WorkService workService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private MmwValidator mmwValidator;

	@ModelAttribute("mmw")
	public ManageMyWorkMarket createModel(HttpServletRequest request) {
		ManageMyWorkMarket mmw = companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId());

		if (mmw == null) {
			mmw = new ManageMyWorkMarket();
		}

		if ("POST".equalsIgnoreCase(request.getMethod())) {
			try {
				mmw.setBusinessYears(Integer.valueOf(request.getParameter("business_years")));
			} catch (NumberFormatException e) {
				mmw.setBusinessYears(null);
			}

			mmw.setAutoSendInvoiceEmail((request.getParameter("auto_send_invoice_email") != null));
		}

		return mmw;
	}

	@ModelAttribute("assignment_pricing_type")
	public String populateAssignmentPricingType(HttpServletRequest request){
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			return request.getParameter("assignment_pricing_type");
		} else {
			Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
			return company.getPaymentConfiguration().getPaymentCalculatorType().toString();

		}

	}

	@ModelAttribute("is_subscription")
	public Boolean isSubscription(HttpServletRequest request){
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			return Boolean.valueOf(request.getParameter("is_subscription"));
		} else {
			Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
			return company.getPaymentConfiguration().isSubscriptionPricing();
		}
	}


	@ModelAttribute("invoice_sent_to_email")
	public String populateInvoiceSentToEmail(HttpServletRequest request) {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			return request.getParameter("invoice_sent_to_email");
		} else {
			Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
			return company.getInvoiceSentToEmail();
		}
	}

	@ModelAttribute("subscription_invoice_sent_to_email")
	public String populateSubscriptionInvoiceSentToEmail(HttpServletRequest request) {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			return request.getParameter("subscription_invoice_sent_to_email");
		} else {
			Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
			return CollectionUtilities.join(company.getSubscriptionInvoiceEmails(), "email", EmailAddressDTO.JOIN_EMAILS);
		}
	}

	protected String getPaymentMethods(PaymentConfiguration config) {
		if (config == null) {
			return null;
		}

		List<String> paymentMethods = new LinkedList<String>();

		if (config.isCreditCardPaymentMethodEnabled()) {
			paymentMethods.add("Credit Card");
		}

		if (config.isWireTransferPaymentMethodEnabled()) {
			paymentMethods.add("Wire Transfer");
		}

		if (config.isAchPaymentMethodEnabled()) {
			paymentMethods.add("Direct Deposit");
		}

		if (config.isCheckPaymentMethodEnabled()) {
			paymentMethods.add("Check");
		}

		return StringUtils.join(paymentMethods, ", ");
	}

	@RequestMapping(value = "/paymenterms", method = GET)
	public String showPaymentTerms(@ModelAttribute("mmw") ManageMyWorkMarket mmw, Model model) {
		Long companyId = getCurrentUser().getCompanyId();

		model.addAttribute("company", companyService.findCompanyById(companyId));
		model.addAttribute("has_accounts", companyService.hasConfirmedBankAccounts(companyId));
		model.addAttribute("num_assignments_payment_pending", workService.countAllAssignmentsPaymentPendingByCompany(companyId));

		PaymentConfiguration paymentConfiguration = billingService.findStatementPaymentConfigurationByCompany(companyId);
		model.addAttribute("statements_configuration", paymentConfiguration);
		model.addAttribute("paymentMethodsList", getPaymentMethods(paymentConfiguration));
		model.addAttribute("paymentTermsDurations", companyService.findPaymentTermsDurations(companyId));

		Integer paymentMethodMaxDays = null;
		String dayOfWeekName = null;

		if (paymentConfiguration != null) {
			PaymentMethod paymentMethod = PaymentMethod.getPaymentMethodWithGreatestProcessTime(
				paymentConfiguration.isCheckPaymentMethodEnabled(),
				paymentConfiguration.isWireTransferPaymentMethodEnabled(),
				paymentConfiguration.isAchPaymentMethodEnabled(),
				paymentConfiguration.isCreditCardPaymentMethodEnabled(),
				paymentConfiguration.isPrefundPaymentMethodEnabled());
			if (paymentMethod != null) {
				paymentMethodMaxDays = paymentMethod.getPaymentDays();
			}
			dayOfWeekName = DateUtilities.getWeekdayName(paymentConfiguration.getPreferredDayOfWeek());
		}

		model.addAttribute("payment_method_max_days", paymentMethodMaxDays);
		model.addAttribute("day_of_week_name", dayOfWeekName);
		model.addAttribute("enabled", Boolean.TRUE);

		return "web/pages/settings/manage/paymenterms";
	}

	@RequestMapping(
		value = "/paymenterms",
		method = POST)
	public String savePaymentTerms(
		@ModelAttribute("mmw") ManageMyWorkMarket mmw,
		BindingResult result,
		Model model,
		HttpServletRequest request) {

		MessageBundle bundle = messageHelper.newBundle();
		model.addAttribute("bundle", bundle);

		mmwValidator.validate(mmw, result);

		if (!result.hasErrors()) {
			ExtendedUserDetails user = getCurrentUser();

			try {

				Company company = companyService.findCompanyById(user.getCompanyId());

				// Update subscription invoices email to company
				EmailAddressDTO emailAddressDTO = new EmailAddressDTO();
				companyService.saveOrUpdateSubscriptionInvoicesEmailToCompany(user.getCompanyId(), emailAddressDTO.getNewEmailAddressDTOFromString(request.getParameter("subscription_invoice_sent_to_email")));

				// See if invoice email address needs to be updated.
				Map<String, String> properties = new HashMap<String, String>();
				properties.put("invoiceSentToEmail", request.getParameter("invoice_sent_to_email"));
				companyService.updateCompanyProperties(company.getId(), properties);

				companyService.saveOrUpdatePaymentCalculatorType(company.getId(),Integer.parseInt(request.getParameter("assignment_pricing_type")));

				ManageMyWorkMarketDTO manageMyWorkMarketDTO = new ManageMyWorkMarketDTO();
				BeanUtils.copyProperties(mmw, manageMyWorkMarketDTO);
				if (mmw.getBusinessYears() == null) manageMyWorkMarketDTO.setBusinessYears(0); // TODO null breaks service call
				profileService.updateManageMyWorkMarket(user.getCompanyId(), manageMyWorkMarketDTO);
			} catch (Exception ex) {
				logger.error("failed to save payment terms", ex);
				messageHelper.addError(bundle, "mmw.manage.paymenterms.save.error");
			}
		} else {
			messageHelper.setErrors(bundle, result);
		}

		if (!bundle.hasErrors()) {
			messageHelper.addSuccess(bundle, "mmw.manage.paymenterms.save.success");
		}

		return showPaymentTerms(mmw, model);
	}
}
