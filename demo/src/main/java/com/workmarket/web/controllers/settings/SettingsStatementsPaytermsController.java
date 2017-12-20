package com.workmarket.web.controllers.settings;

import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.account.payment.AccountingProcessTime;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentCycle;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.ManageMyWorkMarketDTO;
import com.workmarket.service.business.dto.PaymentConfigurationDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.mmw.MmwStatementsPayTermsForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/settings/manage")
public class SettingsStatementsPaytermsController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SettingsStatementsPaytermsController.class);

	@Autowired private CompanyService companyService;
	@Autowired private ProfileService profileService;
	@Autowired private BillingService billingService;
	@Autowired private WorkService workService;
	@Autowired private MessageBundleHelper messageHelper;

	private static final Map<Integer, String> weekdaysMap = ImmutableMap.<Integer, String>builder()
		.put(1, "Monday")
		.put(2, "Tuesday")
		.put(3, "Wednesday")
		.put(4, "Thursday")
		.put(5, "Friday (most popular)").build();

	private static final Map<Integer, String> biweeklySetMap = ImmutableMap.<Integer, String>builder()
		.put(1, "1st and 15th")
		.put(2, "2nd and 16th")
		.put(3, "3rd and 17th")
		.put(4, "4th and 18th")
		.put(5, "5th and 19th")
		.put(6, "6th and 20th")
		.put(7, "7th and 21st")
		.put(8, "8th and 22nd")
		.put(9, "9th and 23rd")
		.put(10, "10th and 24th")
		.put(11, "11th and 25th")
		.put(12, "12th and 26th")
		.put(13, "13th and 27th")
		.put(14, "14th and 28th")
		.put(15, "15th and 29th")
		.put(16, "16th and 30th").build();


	@ModelAttribute("statements_configuration")
	public PaymentConfiguration paymentConfiguration() {
		PaymentConfiguration configuration = billingService.findStatementPaymentConfigurationByCompany(getCurrentUser().getCompanyId());

		if (configuration == null) {
			configuration = new PaymentConfiguration();
		}

		return configuration;
	}

	@ModelAttribute("termsForm")
	public MmwStatementsPayTermsForm createManageMyWorkMarket(
		@ModelAttribute("statements_configuration") PaymentConfiguration configuration) {

		MmwStatementsPayTermsForm form = new MmwStatementsPayTermsForm();

		ManageMyWorkMarket mmw = companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId());
		form.setStatementsEnabled(mmw.getStatementsEnabled());

		if (form.isStatementsEnabled()) {
			form.setPaymentType("statement");
		} else {
			form.setPaymentType("invoice");
		}

		form.setFrequency(configuration.getPaymentCycleDays());
		form.setWeekday(configuration.getPreferredDayOfWeek());

		if (configuration.isBiweeklyPaymentOnSpecificDayOfMonth()) {
			form.setBiweeklyCycle("daysEachMonth");
		} else {
			//A day of the week (Monday, Tuesday, etc)
			form.setBiweeklyCycle("dayOfWeek");
		}

		form.setBiweeklyWeekdays(configuration.getPreferredDayOfWeek());
		form.setBiweeklySet(configuration.getPreferredDayOfMonthBiweeklyFirstPayment());
		form.setMonthDays(configuration.getPreferredDayOfMonth());
		form.setDelay(configuration.getAccountingProcessDays());
		form.setWireTransferPaymentMethodEnabled(configuration.isWireTransferPaymentMethodEnabled());
		form.setAchPaymentMethodEnabled(configuration.isAchPaymentMethodEnabled());
		form.setCreditCardPaymentMethodEnabled(configuration.isCreditCardPaymentMethodEnabled());
		form.setCheckPaymentMethodEnabled(configuration.isCheckPaymentMethodEnabled());
		form.setPrefundPaymentMethodEnabled(configuration.isPrefundPaymentMethodEnabled());
		form.setAutoPayEnabled(mmw.getAutoPayEnabled());
		form.setPaymentTermsDays(mmw.getPaymentTermsDays());
		form.setAutoPayEnabled(mmw.getAutoPayEnabled());
		return form;
	}

	@RequestMapping(value="/statements_payterms", method= RequestMethod.GET)
	public String statementsPayTerms(@ModelAttribute("termsForm") MmwStatementsPayTermsForm form, Model model) {
		ExtendedUserDetails user = getCurrentUser();

		model.addAttribute("companyName", user.getCompanyName());

		model.addAttribute("num_assignments_payment_pending",
			workService.countAllAssignmentsPaymentPendingByCompany(user.getCompanyId()));

		model.addAttribute("paymentTermsDurations", companyService.findPaymentTermsDurations(user.getCompanyId()));
		model.addAttribute("weekdays", weekdaysMap);
		model.addAttribute("biweeklySet", biweeklySetMap);

		return "web/pages/settings/manage/statements_payterms";
	}

	@RequestMapping(
		value = "/statements_payterms_save",
		method = RequestMethod.POST,
		produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public Map<String,Object> saveStatementsPayTerms(
		@ModelAttribute("termsForm") MmwStatementsPayTermsForm form,
		BindingResult result,
		@RequestParam("paymentTermDurations[]") List<Integer> paymentTermsDurations) {

		ExtendedUserDetails user = getCurrentUser();
		Map<String, Object> response = new HashMap<String, Object>();

		MessageBundle bundle = messageHelper.newBundle();

		if (result.hasErrors()) {
			messageHelper.setErrors(bundle, result);
		}

		companyService.setPaymentTermsDurations(user.getCompanyId(), paymentTermsDurations);

		// Invoices.
		if ("invoice".equals(form.getPaymentType())) {
			try {
				billingService.turnStatementsOff(user.getCompanyId());

				// Load the current settings and save the new input.
				ManageMyWorkMarket mmw = companyService.getManageMyWorkMarket(user.getCompanyId());
				mmw.setPaymentTermsEnabled(Boolean.TRUE);
				mmw.setPaymentTermsDays( NumberUtilities.getNullSafe(form.getPaymentTermsDays()) );

				try {
					ManageMyWorkMarketDTO dto = new ManageMyWorkMarketDTO();
					BeanUtils.copyProperties(mmw, dto);
					profileService.updateManageMyWorkMarket(user.getCompanyId(), dto);
				} catch (Exception ex) {
					logger.error("failed to update ManageMyWorkMarket invoice for companyId={}", user.getCompanyId(), ex);
					messageHelper.addError(bundle, "mmw.manage.statements.payterms.settings.error");
				}

				mmw.setAutoPayEnabled(form.getAutoPayEnabled());

				try {
					ManageMyWorkMarketDTO dto = new ManageMyWorkMarketDTO();
					BeanUtils.copyProperties(mmw, dto);
					profileService.updateManageMyWorkMarket(user.getCompanyId(), dto);
				} catch (Exception ex) {
					logger.error("failed to update ManageMyWorkMarket for companyId={}", user.getCompanyId(), ex);
					messageHelper.addError(bundle, "mmw.manage.statements.payterms.settings.error");
				}
			} catch (Exception ex) {
				logger.info("failed to turn statements off for companyId={}", user.getCompanyId(), ex);
				messageHelper.addError(bundle, "mmw.manage.statements.payterms.invoice.error");
			}
		}
		// Statements.
		else {
			if(!form.getFrequency().equals(PaymentCycle.WEEKLY.getPaymentDays()) && form.getDelay().equals(AccountingProcessTime.FIFTEEN_DAYS.getPaymentDays())){
				messageHelper.addError(bundle, "mmw.manage.statements.payterms.settings.fifteen_days.error");
				response.put("bundle", bundle);
				response.put("successful", false);
				return response;
			}

			PaymentConfiguration configuration = new PaymentConfiguration();
			configuration.setAccountingProcessDays(form.getDelay());
			configuration.setPaymentCycleDays(form.getFrequency());
			configuration.setAchPaymentMethodEnabled(form.isAchPaymentMethodEnabled());
			configuration.setCheckPaymentMethodEnabled(form.isCheckPaymentMethodEnabled());
			configuration.setCreditCardPaymentMethodEnabled(form.isCreditCardPaymentMethodEnabled());
			configuration.setWireTransferPaymentMethodEnabled(form.isWireTransferPaymentMethodEnabled());
			configuration.setPrefundPaymentMethodEnabled(form.isPrefundPaymentMethodEnabled());

			switch (form.getFrequency()) {
				// Weekly.
				case 7:
					configuration.setPreferredDayOfWeek(form.getWeekday());
					break;
				// Bi-weekly.
				case 14:
					configuration.setPreferredDayOfWeek(form.getBiweeklyWeekdays());
					configuration.setBiweeklyPaymentOnSpecificDayOfMonth( "daysEachMonth".equals(form.getBiweeklyCycle()) );
					configuration.setPreferredDayOfMonth(form.getBiweeklySet());
					break;
				// Monthly.
				case 30:
					configuration.setPreferredDayOfMonth(form.getMonthDays());
					break;
			}

			try {
				PaymentConfigurationDTO dto = new PaymentConfigurationDTO();
				BeanUtils.copyProperties(configuration, dto);
				billingService.saveStatementPaymentConfigurationForCompany(user.getCompanyId(), dto);
			} catch (Exception ex) {
				logger.error("failed to save StatementPaymentConfiguration for companyId={}", user.getCompanyId(), ex);
				messageHelper.addError(bundle, "mmw.manage.statements.payterms.settings.error");
			}

			try {
				ManageMyWorkMarket mmw = companyService.getManageMyWorkMarket(user.getCompanyId());
				mmw.setAutoPayEnabled(form.getAutoPayEnabled());

				ManageMyWorkMarketDTO dto = new ManageMyWorkMarketDTO();
				BeanUtils.copyProperties(mmw, dto);
				profileService.updateManageMyWorkMarket(user.getCompanyId(), dto);
			} catch (Exception ex) {
				logger.error("failed to update ManageMyWorkMarket statements for companyId={}", user.getCompanyId(), ex);
				messageHelper.addError(bundle, "mmw.manage.statements.payterms.statements.error");
			}
		}

		response.put("bundle", bundle);
		response.put("successful", !bundle.hasErrors());

		return response;
	}

}
