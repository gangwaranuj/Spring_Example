package com.workmarket.web.controllers.settings;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.business.UserService;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.user.ReassignUserForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.ReassignValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings")
public class SettingsHomeController extends BaseController {

	private static final String EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH = "web/pages/settings/employer-onboarding/";

	@Autowired private MessageBundleHelper messageBundleHelper;
	@Autowired private BankingService bankingService;
	@Autowired private VaultHelper vaultHelper;
	@Autowired private ReassignValidator reassignValidator;
	@Autowired private UserService userService;

	@RequestMapping(method = GET)
	public String index() {
		return "redirect:/settings/manage";
	}

	@PreAuthorize("hasAnyRole('ACL_ADMIN')")
	@RequestMapping(value = "/onboarding", method = GET)
	public String onboarding() {

		if (!getCurrentUser().isBuyer()) {
			return "redirect:/error/no_access";
		}

		return EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH + "index";
	}

	@PreAuthorize("hasAnyRole('ACL_ADMIN')")
	@RequestMapping(value = "/onboarding/employees", method = GET)
	public String onboardEmployees(Model model) {
		if (!getCurrentUser().isBuyer()) {
			return "redirect:/error/no_access";
		}

		return EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH + "employees";
	}

	@PreAuthorize("hasAnyRole('ACL_ADMIN')")
	@RequestMapping(value = "/onboarding/assignment-preferences", method = GET)
	public String onboardAssignmentPreferences(Model model) {
		if (!getCurrentUser().isBuyer()) {
			return "redirect:/error/no_access";
		}

		return EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH + "assignmentPreferences";
	}

	@PreAuthorize("hasAnyRole('ACL_ADMIN')")
	@RequestMapping(value = "/onboarding/first-assignment", method = GET)
	public String onboardFirstAssignment (Model model) {
		if (!getCurrentUser().isBuyer()) {
			return "redirect:/error/no_access";
		}

		return EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH + "firstAssignment";
	}

	@PreAuthorize("hasAnyRole('ACL_ADMIN')")
	@RequestMapping(value = "/onboarding/add_funds", method = GET)
	public String addFunds(Model model) {
		if (!getCurrentUser().isBuyer()) {
			return "redirect:/error/no_access";
		}

		populateAddACHModel(model);

		return EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH + "addFunds";
	}

	public String getEmployerOnboardingWebpagesRootPath() {
		return EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH;
	}

	private void populateAddACHModel(Model model) {
		final List<? extends AbstractBankAccount> accounts =
			bankingService.findConfirmedACHBankAccounts(getCurrentUser().getId());
		final List<String> unobfuscatedAccountNumbers = bankingService.getUnobfuscatedAccountNumbers(accounts);
		boolean hasUsaBankAccount = false;

		for (int i = 0; i < accounts.size(); i++) {
			AbstractBankAccount account = accounts.get(i);

			if (Country.USA_COUNTRY.getId().equals(account.getCountry().getId())) {
				hasUsaBankAccount = true;
				break;
			}
		}

		model.addAttribute("accounts", accounts);
		model.addAttribute("unobfuscatedAccountNumbers", unobfuscatedAccountNumbers);
		model.addAttribute("spendLimit", getSpendLimit());
		model.addAttribute("apLimit", getAPLimit());
		model.addAttribute("hasUsaBankAccount", hasUsaBankAccount);
	}
}