package com.workmarket.domains.payments.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountPagination;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.model.banking.GlobalCashCardAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.domains.payments.service.BankAccountDTOValidator;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.AccountConfirmationAttemptsExceededException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.forms.funds.AutoWithdrawForm;
import com.workmarket.web.forms.funds.VerifyACHForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.domains.payments.validator.BankAccountValidator;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Financial account management - CRUD operations for ACH and PayPal accounts
 */
@Controller
@RequestMapping("/funds")
@PreAuthorize("(!principal.userFundsAccessBlocked) AND (principal.hasCustomAccessSettingsSet OR hasAnyRole('PERMISSION_VIEWBALANCE', 'PERMISSION_ADDFUNDS', 'PERMISSION_WITHDRAW'))")
public class ManageAccountsController extends BaseController {

	private static final Log logger = LogFactory.getLog(ManageAccountsController.class);

	@Autowired private AuthenticationService authService;
	@Autowired private BankingService bankingService;
	@Autowired private SuggestionService suggestions;
	@Autowired private FormOptionsDataHelper formData;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private BankAccountValidator accountValidator;
	@Autowired private ProfileService profileService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private FormOptionsDataHelper formDataHelper;
	@Autowired private BankAccountDTOValidator bankAccountDTOValidator;
	@Autowired protected FeatureEvaluator featureEvaluator;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private VaultHelper vaultHelper;
	@Autowired private TaxService taxService;

	@InitBinder("accountForm")
	public void initAccountFormBinder(WebDataBinder binder) {
		binder.setValidator(accountValidator);
	}

	/**
	 * Account list
	 */
	@RequestMapping(
		method = GET
	)
	public String index(Model model) {

		model.addAttribute("enabled", Boolean.TRUE);
		// GCC card banner message
		boolean hasGccAccount = bankingService.hasGCCAccount(getCurrentUser().getId());
		boolean showCreateGccAccountBanner = (!hasGccAccount && Boolean.TRUE.equals(getCurrentUser().isSeller()));
		model.addAttribute("showGccBanner", showCreateGccAccountBanner);

		if (getCurrentUser().isMbo()) {
			MboProfile mboProfile = profileService.findMboProfile(getCurrentUser().getId());

			if (MboProfile.NORMAL.equals(mboProfile.getStatus())) {
				Map<String,String> mboPaymentOptions = Maps.newHashMap();
				mboPaymentOptions.put("MBO", messageHelper.getMessage("funds.accounts.mboPreference.payViaMbo"));
				mboPaymentOptions.put("ME", messageHelper.getMessage("funds.accounts.mboPreference.payToMe"));
				model.addAttribute("mboPaymentPrefOptions", mboPaymentOptions);
				model.addAttribute("mboProfile", mboProfile);
			}
		}

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/funds/accounts/list";
	}

	@RequestMapping(
		value = "/mbo_preference",
		method = POST
	)
	public String setMboPaymentPreference(

		@RequestParam("paymentPreference") String paymentPreference,
		RedirectAttributes flash) {

		MessageBundle messages = messageHelper.newFlashBundle(flash);

		if (getCurrentUser().isMbo()) {
			MboProfile mboProfile = profileService.findMboProfile(getCurrentUser().getId());
			if (mboProfile != null) {
				mboProfile.setPaymentPreference(paymentPreference);
				profileService.saveMboProfile(mboProfile);
				messageHelper.addSuccess(messages, "funds.accounts.mboPreferenceChange.success");
			} else {
				logger.error(String.format("[MBO] Profile could not be found for userId %d", getCurrentUser().getId()));
				messageHelper.addError(messages, "funds.accounts.mboPreferenceChange.fail");
			}
		}

		return "redirect:/funds";
	}


	@RequestMapping(
		value = "/accounts",
		method = GET)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public String list(Model model) throws Exception {

		// GCC card banner message
		boolean hasGccAccount = bankingService.hasGCCAccount(getCurrentUser().getId());
		boolean showCreateGccAccountBanner = (!hasGccAccount && Boolean.TRUE.equals(getCurrentUser().isSeller()));
		model.addAttribute("showGccBanner", showCreateGccAccountBanner);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/funds/accounts/list";
	}

	@RequestMapping(
		value = "/accounts.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public void listData(
		HttpServletRequest httpRequest,
		Model model) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(CollectionUtilities.<Integer, String>newTypedObjectMap(
			0, BankAccountPagination.SORTS.TYPE.name(),
			1, BankAccountPagination.SORTS.ACCOUNT_NAME.name()
		));
		BankAccountPagination pagination = request.newPagination(BankAccountPagination.class);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.ACTIVE, true);
		pagination = bankingService.findBankAccounts(getCurrentUser().getId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		for (AbstractBankAccount account : pagination.getResults()) {
			vaultHelper.unobfuscateEntityFields(account);
			List<String> data = Lists.newArrayList(
				account.getType(),
				account.getAccountDescription(),
				account.getType(),
				account.getCountry().getId(),
				BooleanUtils.toStringTrueFalse(account.getConfirmedFlag()),
				null
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", account.getId(),
				"type", account.getType(),
				"accountType", (account instanceof BankAccount || account instanceof GlobalCashCardAccount) ? account.getBankAccountType().getDescription() : null,
				"confirmed", account.getConfirmedFlag(),
				"confirmedOn", (account instanceof BankAccount) ? DateUtilities.format("MM/dd/yyyy", ((BankAccount) account).getConfirmedOn(), getCurrentUser().getTimeZoneId()) : null,
				"createdOn", DateUtilities.format("MM/dd/yyyy", account.getCreatedOn(), getCurrentUser().getTimeZoneId()),
				"isAutoWithdraw",account.getAutoWithdraw(),
				"country", account.getCountry().getId()
			);

			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@SuppressWarnings("unchecked")
	private Set<String> getAdminUserNames() {
		Set<String> userNames = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);

		if (!getCurrentUser().getCompanyIsIndividual()) {
			Set<User> adminUsers = authService.findAllAdminAndControllerUsersByCompanyId(getCurrentUser().getCompanyId());
			userNames.addAll(CollectionUtilities.newListPropertyProjection(adminUsers, "fullName"));
		}

		if (!Constants.DEFAULT_COMPANY_NAME.equals(getCurrentUser().getCompanyName())) {
			userNames.add(getCurrentUser().getCompanyName());
		}

		userNames.add(getCurrentUser().getFullName());

		return userNames;
	}

	/**
	 * Account creation
	 */
	@RequestMapping(
		value = {"/accounts/new", "/addaccount"},
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEBANK') AND !principal.isMasquerading()")
	public String create(
		Model model,
		@RequestParam(value = "return_to_page", required = false) String returnToPage) {

		BankAccountDTO form = new BankAccountDTO();

		// tax country is only applicable to sellers, buyers must be US
		if (getCurrentUser().isSeller()) {
			Country taxCountry = getCurrentUser().getTaxCountry();
			form.setCountry(taxCountry != null ? taxCountry.getId() : "");
		}
		else {
			form.setCountry(Country.USA_COUNTRY.getId());
		}

		form.setEmailAddress(getCurrentUser().getEmail());

		boolean hasGccAccount = bankingService.hasGCCAccount(getCurrentUser().getId());
		boolean showCreateGccAccountBanner = (!hasGccAccount && getCurrentUser().isSeller());
		model.addAttribute("showGccBanner", showCreateGccAccountBanner);

		model.addAttribute("accountForm", form);
		model.addAttribute("returnToPage", returnToPage);
		model.addAttribute("enabled", Boolean.TRUE);

		Boolean hasVerifiedTaxEntity = false;
		AbstractTaxEntity activeTaxEntity = taxService.findActiveTaxEntity(getCurrentUser().getId());
		if (activeTaxEntity != null && activeTaxEntity instanceof UsaTaxEntity) {
			hasVerifiedTaxEntity = activeTaxEntity.getStatus().isApproved();
		}

		model.addAttribute("hasVerifiedTaxEntity", hasVerifiedTaxEntity);

		return showCreateForm(model, form);
	}

	@RequestMapping(
		value = "/accounts",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEBANK') AND !principal.isMasquerading()")
	public String doCreate(
		@RequestParam(value = "return", required = false) String returnUri,
		@Valid @ModelAttribute("accountForm") BankAccountDTO form,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes flash) throws Exception {

		if (bindingResult.hasErrors()) {
			MessageBundle messages = messageHelper.newBundle(model);
			messageHelper.setErrors(messages, bindingResult);

			return showCreateForm(model, form);
		}

		Long userId = getCurrentUser().getId();
		AbstractBankAccount result = bankingService.saveBankAccount(userId, form);

		MessageBundle messages = messageHelper.newFlashBundle(flash);
		String messageKey = StringUtils.equals(result.getType(), AbstractBankAccount.ACH) && StringUtils.equals(Country.USA_COUNTRY.getId(), form.getCountry()) ?
			"funds.accounts.create.ach.success" :
			"funds.accounts.create.success";
		messageHelper.addSuccess(messages, messageKey);
		profileService.sendProfileUpdateEmail(userId, AbstractBankAccount.ACH);

		return (StringUtils.isNotEmpty(returnUri)) ? "redirect:/" + returnUri : "redirect:/funds/accounts";
	}

	private String showCreateForm(Model model, BankAccountDTO form) {
		model.addAttribute("accountUserNames", getAdminUserNames());
		model.addAttribute("hasPayPal", bankingService.hasPayPalAccount(getCurrentUser().getId()));
		model.addAttribute("countries", formData.getCountries());
		model.addAttribute("supportedTaxCountries", Country.WM_SUPPORTED_COUNTRIES_OBJECTS);
		model.addAttribute("isInternational", false);

		// tax info check is only applicable to sellers
		if (getCurrentUser().isSeller()) {
			boolean isInternational = !Country.USA_COUNTRY.equals(getCurrentUser().getTaxCountry())
				&& !Country.CANADA_COUNTRY.equals(getCurrentUser().getTaxCountry());

			model.addAttribute("isInternational", isInternational);

			if (getCurrentUser().getTaxCountry() != null) {
				model.addAttribute("taxInfoCountries", ImmutableList.of(getCurrentUser().getTaxCountry()));
			} else {
				model.addAttribute("taxInfoCountries", new ArrayList<>());
			}
		}
		else {
			model.addAttribute("taxInfoCountries", ImmutableList.of(Country.USA_COUNTRY));
		}

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap(
				"isBuyer", getCurrentUser().isBuyer(),
				"country", form.getCountry()
			)
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/funds/accounts/new";
	}

	@RequestMapping(
		value = "/accounts/gcc",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEBANK') AND !principal.isMasquerading()")
	public String createGcc(
		Model model,
		RedirectAttributes flash) {

		MessageBundle messages = messageHelper.newFlashBundle(flash);
		if (bankingService.hasGCCAccount(getCurrentUser().getId())) {
			messageHelper.addError(messages, "funds.accounts.create.gcc.account.exists.error");

			return "redirect:/funds/accounts";
		}

		BankAccountDTO form = new BankAccountDTO();
		form.setType(AbstractBankAccount.GCC);
		form.setBankAccountTypeCode(BankAccountType.GLOBAL_CASH_CARD);
		BeanUtilities.copyProperties(form, getCurrentUser());
		BeanUtilities.copyProperties(form, profileService.findAddress(getCurrentUser().getId()));
		model.addAttribute("accountForm", form);

		return showGccCreateForm(model);
	}

	@RequestMapping(
		value = "/accounts/gcc",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEBANK') AND !principal.isMasquerading()")
	public String doCreateGcc(
		@RequestParam(value = "return", required = false) String returnUri,
		@Valid @ModelAttribute("accountForm") BankAccountDTO form,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes flash) {

		if (bankingService.hasGCCAccount(getCurrentUser().getId())) {
			MessageBundle messages = messageHelper.newFlashBundle(flash);
			messageHelper.addError(messages, "funds.accounts.create.gcc.account.exists.error");
			return "redirect:/funds/accounts";
		}

		List<String> errors = bankAccountDTOValidator.validate(form);
		if (!errors.isEmpty()) {
			MessageBundle messages = messageHelper.newFlashBundle(flash);
			for (String propertyCode : errors) {
				messageHelper.addError(messages, String.format(messageHelper.getMessage(propertyCode)));
			}

			return "redirect:/funds/accounts";
		}

		if (bindingResult.hasErrors()) {
			MessageBundle messages = messageHelper.newBundle(model);
			messageHelper.setErrors(messages, bindingResult);

			return showGccCreateForm(model);
		}

		/**
		 * TODO: gcc accepts only 2 letter codes there is a fix somewhere that sends codes as is, adding
		 * this if for now and as soon as fix is ready will remove this.
		 */
		if (Country.USA.equals(form.getCountry())) {
			form.setCountry(Country.US);
		} else if (Country.CANADA.equals(form.getCountry())) {
			form.setCountry(Country.CANADA_COUNTRY.getISO());
		}

		if (Country.USA.equals(form.getCountry2())) {
			form.setCountry2(Country.US);
		} else if (Country.CANADA.equals(form.getCountry2())) {
			form.setCountry2(Country.CANADA_COUNTRY.getISO());
		}

		MessageBundle messages = messageHelper.newFlashBundle(flash);
		try {
			Long userId = getCurrentUser().getId();
			AbstractBankAccount result = bankingService.saveBankAccount(userId, form);
			profileService.sendProfileUpdateEmail(userId, AbstractBankAccount.GCC);
			messageHelper.addSuccess(messages, "funds.accounts.create.gcc.success");
			return (StringUtils.isNotEmpty(returnUri)) ? "redirect:/" + returnUri : "redirect:/funds/accounts";

		} catch (Exception e) {
			messages = messageHelper.newBundle(model);
			messageHelper.addError(messages, "funds.accounts.gcc.exception", e.getMessage());
			return showGccCreateForm(model);
		}

	}


	private String showGccCreateForm(Model model) {
		model.addAttribute("enabled", Boolean.TRUE);
		Calendar now = Calendar.getInstance();
		model.addAttribute("minYear",now.get(Calendar.YEAR) - 100);
		model.addAttribute("maxYear",now.get(Calendar.YEAR) - 18);
		model.addAttribute("states", formDataHelper.getStates(Country.USA));
		model.addAttribute("countries", formDataHelper.getCountries());
		model.addAttribute("provinces", formDataHelper.getStates(Country.CANADA));
		model.addAttribute("months",invariantDataService.getMonthsOfYear());
		model.addAttribute("govIdTypes", BankAccountDTO.GOV_ID_TYPE_MAP);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/funds/accounts/gcc";
	}


	/**
	 * Account delete
	 */
	@RequestMapping(
		value = "/accounts/delete/{id}",
		method = GET)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public String delete(@PathVariable("id") final Long id, Model model) {

		List<? extends AbstractBankAccount> accounts = getAuthorizedAccountList(id);

		model.addAttribute("accounts", accounts);

		return "web/pages/funds/accounts/delete";
	}

	@RequestMapping(
		value = "/accounts/delete/{id}",
		method = {RequestMethod.DELETE, RequestMethod.POST})
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public String doDelete(@PathVariable("id") Long accountId, RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		AbstractBankAccount result = null;
		ExtendedUserDetails currentUser = getCurrentUser();
		try {
			result = bankingService.deactivateBankAccount(accountId, currentUser.getCompanyId());
		} catch (Exception e) {
			logger.warn(String.format("Error deleting bank account %d by user %d", accountId, getCurrentUser().getId()));
		}

		if (result != null) {
			messageHelper.addSuccess(bundle, "funds.accounts.delete.success");
			profileService.sendProfileUpdateEmail(currentUser.getId(), result.getType());
		} else {
			messageHelper.addError(bundle, "funds.accounts.delete.exception");
		}

		return "redirect:/funds/accounts";
	}

	/**
	 * Account verification
	 */
	@RequestMapping(
		value = "/accounts/verify/{id}",
		method = RequestMethod.GET)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public String verify(@PathVariable("id") Long id, Model model) {

		getAuthorizedAccountList(id);

		model.addAttribute("verifyForm", new VerifyACHForm());

		return "web/pages/funds/accounts/verify";
	}


	@RequestMapping(
		value = "/accounts/verify/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public @ResponseBody AjaxResponseBuilder doVerify(
		@PathVariable("id") Long accountId,
		@Valid @ModelAttribute("verifyForm") VerifyACHForm form,
		BindingResult bindingResult) throws Exception {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(response, bindingResult);
			return response;
		}

		logger.debug(String.format("validating %d, %d, %d", accountId, form.getAmount1(), form.getAmount2()));

		try {
			response.setSuccessful(bankingService.confirmBankAccount(accountId, form.getAmount1(), form.getAmount2(), getCurrentUser().getCompanyId()));
			if (response.isSuccessful()) {
				messageHelper.addMessage(response, "funds.verify_ach.success");
			} else {
				messageHelper.addMessage(response, "funds.verify_ach.error");
			}
		} catch (AccountConfirmationAttemptsExceededException e) {
			messageHelper.addMessage(response, "funds.verify_ach.max_attempt");
		} catch (Exception e) {
			messageHelper.addMessage(response, "funds.verify_ach.exception");
			logger.warn(String.format("Error verifying bank account %d by user %d", accountId, getCurrentUser().getId()));
		}

		return response;
	}

	/**
	 * ACH routing number lookup for auto-complete
	 */
	@RequestMapping(
		value = "/accounts/routing-numbers",
		method = GET)
	@ResponseBody
	public List<BankRouting> lookupRouting(@RequestParam(defaultValue = "") String term, @RequestParam(defaultValue = "") String countryId) throws Exception {
		return suggestions.suggestBankRouting(term, countryId);
	}

	private List<? extends AbstractBankAccount> getAuthorizedAccountList(Long id) {
		List<? extends AbstractBankAccount> accounts = bankingService.findBankAccounts(getCurrentUser().getId());
		for (AbstractBankAccount account : accounts) {
			if (account.getId().equals(id)) {
				return accounts;
			}
		}
		throw new HttpException401("funds.accounts.unauthorized")
			.setRedirectUri("redirect:/funds/accounts");
	}

	/**
	 *
	 * Auto Withdrawal functionality
	 */

	@RequestMapping(
		value = "/accounts/auto_withdrawal/{id}",
		method = GET)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public String autoWithdrawal(@PathVariable Long id, Model model) {

		AbstractBankAccount bankAccount = null;

		List<? extends AbstractBankAccount> accounts = getAuthorizedAccountList(id);

		for(AbstractBankAccount ba : accounts){
			if(ba.getId().equals(id)){
				bankAccount = ba;
			}
		}
		/* NOTE: this condition will always be true becase we check if account exists in getAuthorizedAccountList function */
		if(bankAccount != null){
			model.addAttribute("autoWithdrawForm", new AutoWithdrawForm(id,bankAccount.getAutoWithdraw()));
		}

		return "web/pages/funds/accounts/autoWithdrawal";
	}

	@RequestMapping(
		value = "/accounts/auto_withdrawal/{id}",
		method = POST)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public String doSetAutoWithdraw(
		@PathVariable("id") Long accountId,
		@Valid @ModelAttribute("autoWithdrawForm") AutoWithdrawForm form,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		logger.debug(String.format("setting auto withdraw for %d to %s", accountId, form.getAutoWithdraw()));

		try {
			if (bankingService.updateAutoWithdrawSettings(accountId, getCurrentUser().getCompanyId(), form.getAutoWithdraw())) {

				messageHelper.addSuccess(bundle, "funds.set_auto_withdraw.success");
			} else {
				messageHelper.addError(bundle, "funds.set_auto_withdraw.error");
			}
		} catch (Exception e) {
			messageHelper.addSuccess(bundle, "funds.set_auto_withdraw.error");
			logger.warn(String.format("Error setting bank account %d by user %d to autowithdraw \n %s", accountId, getCurrentUser().getCompanyId(),ExceptionUtils.getStackTrace(e)));
		}

		return "redirect:/funds/accounts";
	}
}
