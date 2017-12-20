package com.workmarket.domains.payments.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InvalidBankAccountException;
import com.workmarket.service.exception.account.WithdrawalExceedsDailyMaximumException;
import com.workmarket.service.exception.tax.InvalidTaxEntityException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.converters.AddressFormToAddressDTOConverter;
import com.workmarket.web.forms.funds.AddACHForm;
import com.workmarket.web.forms.funds.AddCreditCardForm;
import com.workmarket.web.forms.funds.AddFundsWireForm;
import com.workmarket.web.forms.funds.AllocateBudgetForm;
import com.workmarket.web.forms.funds.GenerateInvoiceForm;
import com.workmarket.web.forms.funds.WithdrawFundsForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.AddressWorkValidator;
import com.workmarket.web.views.HTML2PDFView;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AutoPopulatingList;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/funds")
@PreAuthorize("(!principal.userFundsAccessBlocked) " +
		" AND !principal.isMasquerading() " +
		" AND (principal.hasCustomAccessSettingsSet OR hasAnyRole('PERMISSION_VIEWBALANCE', 'PERMISSION_ADDFUNDS', 'PERMISSION_WITHDRAW'))")
public class ManageFundsController extends BaseController {

	private static final Log logger = LogFactory.getLog(ManageFundsController.class);

	@Autowired private InvariantDataService invariantDataService;
	@Autowired private ProfileService profileService;
	@Autowired private TaxService taxService;
	@Autowired private AuthenticationService authService;
	@Autowired private CompanyService companyService;
	@Autowired private BankingService bankingService;
	@Autowired private RatingService ratingService;
	@Autowired private ProjectService projectService;
	@Autowired private PricingService pricingService;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private FormOptionsDataHelper formOptionsDataHelper;
	@Autowired private @Qualifier("addressWorkValidator") AddressWorkValidator addressValidator;
	@Autowired private SummaryService summaryService;
	@Autowired private @Qualifier("accountRegisterServicePrefundImpl") AccountRegisterService accountRegisterService;
	@Autowired private AddressFormToAddressDTOConverter addressFormToAddressDTOConverter;
	@Autowired private VaultHelper vaultHelper;

	@RequestMapping(
		value = "/add",
		method = GET)
	public String add(Model model) {
		populateAddCCModel(model);
		populateAddACHModel(model);
		populateAddWire(model);
		Long userId = getCurrentUser().getId();

		AddressDTO companyAddress = new AddressDTO(profileService.findCompanyAddress(userId));
		Map<String, Object> companyAddressJSON = CollectionUtilities.newObjectMap();
		if (companyAddress.getAddressId() != null) {
			companyAddressJSON = CollectionUtilities.newObjectMap(
				"address1", companyAddress.getAddress1(),
				"address2", companyAddress.getAddress2(),
				"city", companyAddress.getCity(),
				"state", companyAddress.getState(),
				"postal_code", companyAddress.getPostalCode(),
				"country", companyAddress.getCountry()
			);
			model.addAttribute("has_company_address", true);
		}
		model.addAttribute("company_address", companyAddressJSON);

		AddressDTO profileAddress = new AddressDTO(profileService.findAddress(userId));
		Map<String, Object> profileAddressJSON = CollectionUtilities.newObjectMap();
		if (profileAddress.getAddressId() != null) {
			profileAddressJSON = CollectionUtilities.newObjectMap(
				"address1", profileAddress.getAddress1(),
				"address2", profileAddress.getAddress2(),
				"city", profileAddress.getCity(),
				"state", profileAddress.getState(),
				"postal_code", profileAddress.getPostalCode(),
				"country", profileAddress.getCountry()
			);
			model.addAttribute("has_profile_address", true);
		}
		model.addAttribute("profile_address", profileAddressJSON);

		model.addAttribute("buyerSums", summaryService.getPaymentCenterAggregateSummaryForBuyer(getCurrentUser().getId()));
		model.addAttribute("project_list", formOptionsDataHelper.getEnabledProjects(getCurrentUser()));

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap(
				"companyAddress", companyAddressJSON,
				"profileAddress", profileAddressJSON
			)
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/funds/add";
	}

	@RequestMapping(
		value = "/addcc",
		method = POST)
	public @ResponseBody AjaxResponseBuilder addCreditCard(
		@Valid @ModelAttribute("addCreditCardForm") AddCreditCardForm form,
		RedirectAttributes flash,
		Model model) throws Exception {

		MessageBundle messages = messageHelper.newFlashBundle(flash);
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		Long userId = getCurrentUser().getId();
		Address companyAddress = profileService.findCompanyAddress(userId);
		Address profileAddress = profileService.findAddress(userId);

		AutoPopulatingList<String> projectIds = form.getProject_id();
		AutoPopulatingList<Float> projectAmounts = form.getProject_amount();

		if (form.hasNewAddress()) {
			AddressDTO address = addressFormToAddressDTOConverter.convert(form);
			BindingResult addressBinding = new BeanPropertyBindingResult(address, "address");
			addressValidator.validate(address, addressBinding);
			messageHelper.setErrors(messages, addressBinding);
		} else if (form.getUse_company_address() && (null == companyAddress)) {
			messageHelper.addError(messages, "funds.addcc.address_exception");
		} else if (form.getUse_profile_address() && (null == profileAddress)) {
			messageHelper.addError(messages, "funds.addcc.address_exception");
		}
		if (form.getAmount().compareTo(new Float(0.00)) == -1) {
			messageHelper.addError(messages, "funds.addcc.invalid_amount");
		}

		if (messages.hasErrors()) {
			flash.addFlashAttribute("addCreditCardForm", model.asMap().get("addCreditCardForm"));
			return response.setMessages(messages.getErrors());
		}

		PaymentDTO dto = new PaymentDTO();
		dto.setAmount(form.getAmount().toString());
		dto.setCardType(form.getCard_type());
		dto.setCardNumber(form.getCard_number());
		dto.setCardExpirationDateString(form.getCard_expiration_month() + form.getCard_expiration_year());
		dto.setCardSecurityCode(form.getCard_security_code());
		dto.setFirstName(form.getFirst_name());
		dto.setLastName(form.getLast_name());

		if (form.getUse_company_address() && companyAddress != null) {
			dto.setAddress1(companyAddress.getAddress1());
			dto.setAddress2(companyAddress.getAddress2());
			dto.setCity(companyAddress.getCity());
			dto.setState(companyAddress.getState().getShortName());
			dto.setCountry(companyAddress.getCountry().getId());
			dto.setPostalCode(companyAddress.getPostalCode());
		} else if (form.getUse_profile_address()) {
			if (profileAddress != null) {
				dto.setAddress1(profileAddress.getAddress1());
				dto.setAddress2(profileAddress.getAddress2());
				dto.setCity(profileAddress.getCity());
				dto.setState(profileAddress.getState().getShortName());
				dto.setCountry(profileAddress.getCountry().getId());
				dto.setPostalCode(profileAddress.getPostalCode());
			}
		} else {
			dto.setAddress1(form.getAddress1());
			dto.setAddress2(form.getAddress2());
			dto.setCity(form.getCity());
			dto.setState(form.getState());
			dto.setCountry(form.getCountry());
			dto.setPostalCode(form.getPostalCode());
		}

		try {
			PaymentResponseDTO responseDTO = accountRegisterServicePrefundImpl.addFundsToRegisterFromCreditCard(projectIds, projectAmounts, userId, dto, true);

			if (responseDTO.isApproved()) {
				// Convert the ID to a string now because the message helper will format it with a comma
				// e.g. 123456 => 123,456
				messageHelper.addSuccess(messages, "funds.addcc.success", String.valueOf(responseDTO.getCreditCardTransactionId()));
				flash.addFlashAttribute("success", true);
				return response.setMessages(messages.getSuccess()).setSuccessful(true);
			} else {
				messageHelper.addError(messages, "funds.addcc.exception");
				messageHelper.addError(messages, responseDTO.getResponseMessage());
			}
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(messages, "funds.addcc.exception");
		}

		return response.setMessages(messages.getErrors());

	}

	@RequestMapping(
		value = "/withdraw",
		method = GET)
	@PreAuthorize("!principal.userFundsAccessBlocked AND hasRole('PERMISSION_WITHDRAW')")
	public String withdraw(
		@ModelAttribute("withdrawFundsForm") WithdrawFundsForm form,
		@ModelAttribute("bundle") MessageBundle messages, Model model) throws IllegalAccessException, InstantiationException {

		Long userId = getCurrentUser().getId();

		model.addAttribute("email", getCurrentUser().getEmail());

		AbstractTaxEntity taxEntity = taxService.findActiveTaxEntity(userId);

		populateTaxEntityModelAndMessages(model, taxEntity, messages);

		List<? extends AbstractEntity> accounts = bankingService.findConfirmedBankAccounts(userId);
		vaultHelper.unobfuscateEntityFields(accounts);
		model.addAttribute("accounts", accounts);
		model.addAttribute("available_balance", accountRegisterServicePrefundImpl.calculateWithdrawableCashByCompany(getCurrentUser().getCompanyId()));
		Map paypalFees = pricingService.findCostForTransactionTypesByCompany(getCurrentUser().getCompanyId(), RegisterTransactionType.PAY_PAL_FEE_REGISTER_TRANSACTION_TYPES);
		model.addAttribute("paypalFees", jsonSerializationService.toJson(paypalFees));
		Map paypalCountryCodes = RegisterTransactionType.PAYPAL_COUNTRY_TRANSACTION_CODE_MAP;
		model.addAttribute("paypalCountryCodes", jsonSerializationService.toJson(paypalCountryCodes));

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap(
				"paypalFees", paypalFees,
				"paypalCountryCodes", paypalCountryCodes
			)
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		if (!getCurrentUser().hasAllRoles("PERMISSION_WITHDRAW")) {
			messageHelper.addNotice(messageHelper.newBundle(model), "funds.withdraw.unauthorized");
			return "web/pages/funds/withdraw";
		}

		if (taxEntity != null) {
			model.addAttribute("payto", taxEntity.getTaxName());
		}

		boolean hasWorkPendingRating = ratingService.hasWorkPendingRatingByResource(userId);
		boolean hasGccAccount = bankingService.hasGCCAccount(userId);
		boolean showCreateGccAccountBanner = (!hasGccAccount && Boolean.TRUE.equals(getCurrentUser().isSeller()));

		model.addAttribute("showGccBanner", showCreateGccAccountBanner);
		model.addAttribute("hasWorkPendingRating", hasWorkPendingRating);

		return "web/pages/funds/withdraw";
	}

	@RequestMapping(
		value = "/withdraw",
		method = POST)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasRole('PERMISSION_WITHDRAW')")
	public @ResponseBody AjaxResponseBuilder doWithdraw(
		@Valid @ModelAttribute("withdrawFundsForm") WithdrawFundsForm form,
		BindingResult bind,
		RedirectAttributes flash) {

		MessageBundle messages = messageHelper.newFlashBundle(flash);
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		if (!getCurrentUser().hasAllRoles("PERMISSION_WITHDRAW")) {
			messageHelper.addMessage(response, "funds.withdraw.unauthorized");
			return response;
		}

		if (getCurrentUser().getCompanyIsLocked()) {
			messageHelper.addError(messages, "funds.withdraw.company_locked");
			return response;
		}

		if (bind.hasErrors()) {
			messageHelper.setErrors(messages, bind);
			return response.setMessages(messages.getErrors());
		}

		Long userId = getCurrentUser().getId();

		try {
			accountRegisterServicePrefundImpl.withdrawFundsFromRegister(userId, form.getAccount(), form.getAmount().toString());
			AbstractBankAccount account = bankingService.findBankAccount(form.getAccount());
			messageHelper.addSuccess(messages, String.format("funds.withdraw.%s.success", StringUtils.lowerCase(account.getType())));
			flash.addFlashAttribute("success", true);

			return response
				.setSuccessful(true)
				.setMessages(messages.getSuccess())
				.setData(ImmutableMap.<String, Object>of("rate_work", ratingService.hasWorkPendingRatingByResource(getCurrentUser().getId())));

		} catch (WithdrawalExceedsDailyMaximumException e) {
			messageHelper.addError(messages, "funds.withdraw.exceed_max", Constants.DAILY_WITHDRAWAL_LIMIT);
		} catch (InsufficientFundsException e) {
			messageHelper.addError(messages, "funds.withdraw.insufficient");
		} catch (InvalidBankAccountException e) {
			messageHelper.addError(messages, "funds.withdraw.invalid_account");
		} catch (InvalidTaxEntityException e) {
			messageHelper.addError(messages, (Country.USA.equals(getCurrentUser().getCountry())) ?
				"funds.withdraw.no_usa_taxentity" :
				"funds.withdraw.no_taxentity");
		} catch (Exception e) {
			logger.error("Problem withdrawing funds: ", e);
			messageHelper.addError(messages, "funds.withdraw.exception");
		}

		return response.setMessages(messages.getErrors());
	}

	@RequestMapping(
		value = "/addach",
		method = POST)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public @ResponseBody AjaxResponseBuilder submitAddACH(
		@Valid @ModelAttribute("addACHForm") AddACHForm form,
		BindingResult bind,
		RedirectAttributes flash) {

		ExtendedUserDetails user = getCurrentUser();

		MessageBundle messages = messageHelper.newFlashBundle(flash);
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);
		AutoPopulatingList<String> projectIds = form.getProject_id();
		AutoPopulatingList<Float> projectAmounts = form.getProject_amount();

		// Check that amount is non-negative
		if (form.getAmount() != null && form.getAmount().floatValue() <= 0) {
			messageHelper.addError(messages, "funds.addach.negative_amount");
			return response.setMessages(messages.getErrors());
		}

		if (bind.hasFieldErrors()) {
			messageHelper.setErrors(messages, bind);
			return response.setMessages(messages.getErrors());
		}

		BankAccount bankAccount = bankingService.findBankAccount(form.getAccount());
		if (bankAccount == null) {
			messageHelper.addError(messages, "funds.addach.invalid_account");
		} else if (!bankAccount.getCountry().getId().equals(Country.USA)) {
			messageHelper.addError(messages, "funds.addach.non_usa_account");
		}

		if (bind.hasErrors()) {
			messageHelper.setErrors(messages, bind);
			return response.setMessages(messages.getErrors());
		}

		try {
			// TODO: we should check against available funds here
			accountRegisterServicePrefundImpl.addFundsToRegisterFromAch(projectIds, projectAmounts, user.getId(), form.getAccount(), form.getAmount().toString());
			messageHelper.addSuccess(messages, "funds.addach.success");
			flash.addFlashAttribute("success", true);
			return response.setMessages(messages.getSuccess()).setSuccessful(true);
		} catch (Exception e) {
			messageHelper.addError(messages, "funds.addach.exception");
			logger.error("Error on account register add: ", e);
		}

		return response.setMessages(messages.getErrors());
	}

	@RequestMapping(
		value = "/invoice",
		method = GET)
	public String showGenerateInvoiceForm(Model model) {

		populateInvoiceModel(model);
		model.addAttribute("enabled", Boolean.TRUE);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/funds/invoice";
	}

	@RequestMapping(
		value = "/invoice",
		method = POST)
	public View submitGenerateInvoiceForm(
		@Valid @ModelAttribute("generateInvoiceForm") GenerateInvoiceForm form,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bindingResult.hasFieldErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
		}
		//check all other required fields that are not constrained at POJO
		if (StringUtils.isEmpty(form.getAddress1())) {
			messageHelper.addError(bundle, "funds.invoice.invalid.address1");
		}
		if (StringUtils.isEmpty(form.getCity())) {
			messageHelper.addError(bundle, "funds.invoice.invalid.city");
		}
		if (StringUtils.isEmpty(form.getState())) {
			messageHelper.addError(bundle, "funds.invoice.invalid.state");
		}
		if (StringUtils.isEmpty(form.getPostalCode())) {
			messageHelper.addError(bundle, "funds.invoice.invalid.postal_code");
		}

		if(bundle.hasErrors()) {
			flash.addFlashAttribute("generateInvoiceForm", form);
			return new RedirectView("/funds/invoice");
		}

		model.addAttribute("form", form);
		return new HTML2PDFView("pdf/funds/generic_invoice");
	}

	@RequestMapping(
		value = "/allocate-budget",
		method = GET)
	public String allocateBudget(Model model, @ModelAttribute("allocateBudgetForm") AllocateBudgetForm form) throws Exception {

		List<Project> projectList =  projectService.findReservedFundsEnabledProjectsForCompany(getCurrentUser().getCompanyId());

		model.addAttribute("general_cash", getGeneralCash());
		model.addAttribute("project_list", projectList);
		model.addAttribute("transfer_from", formOptionsDataHelper.getEnabledProjects(getCurrentUser()));
		model.addAttribute("transfer_to", formOptionsDataHelper.getEnabledProjects(getCurrentUser()));

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/funds/allocate-budget";
	}

	@RequestMapping(
		value = "/allocate-budget",
		method = POST)
	public String doAllocateBudget(
		@ModelAttribute("allocateBudgetForm") AllocateBudgetForm form,
		RedirectAttributes flash
	) throws Exception{
		MessageBundle messages = messageHelper.newFlashBundle(flash);

		if(!StringUtils.isNotBlank(form.getTransfer_from())) {
			messageHelper.addError(messages, "You need to select transfer from");
			return "redirect:/payments";
		}

		if(!StringUtils.isNotBlank(form.getTransfer_to())) {
			messageHelper.addError(messages, "You need to select transfer to");
			return "redirect:/payments";
		}

		if(form.getAmount() == null) {
			messageHelper.addError(messages, "The amount cannot be empty");
			return "redirect:/payments";
		}

		if(!NumberUtilities.isPositive(form.getAmount())) {
			messageHelper.addError(messages, "The transfer amount must be greater than $0.00");
			return "redirect:/payments";
		}

		String from = form.getTransfer_from();
		String to = form.getTransfer_to();
		try {

			if(AllocateBudgetForm.GENERAL_CASH.equals(from)) {
				if(accountRegisterService.calculateGeneralCashByCompany(getCurrentUser().getCompanyId()).compareTo(form.getAmount()) == -1){
					messageHelper.addError(messages, "The transfer amount is greater than unreserved cash");
					return "redirect:/payments";
				}
				Project projectTo = projectService.findById(new Long(form.getTransfer_to()));
				accountRegisterService.transferFundsToProject(projectTo.getId(), getCurrentUser().getCompanyId(), form.getAmount());

			} else if (AllocateBudgetForm.GENERAL_CASH.equals(to)) {
				Project projectFrom = projectService.findById(new Long(form.getTransfer_from()));
				if(projectFrom.getReservedFunds().compareTo(form.getAmount()) == -1) {
					messageHelper.addError(messages, "The transfer amount is greater than the available balance in " + projectFrom.getName());
					return "redirect:/payments";
				}
				accountRegisterService.transferFundsToGeneral(projectFrom.getId(), getCurrentUser().getCompanyId(), form.getAmount());

			} else {
				Project projectFrom = projectService.findById(new Long(form.getTransfer_from()));
				Project projectTo = projectService.findById(new Long(form.getTransfer_to()));
				if(projectFrom.getReservedFunds().compareTo(form.getAmount()) == -1) {
					messageHelper.addError(messages, "The transfer amount is greater than the available balance in " + projectFrom.getName());
					return "redirect:/payments";
				}
				accountRegisterService.transferFundsBetweenProjects(projectFrom.getId(), projectTo.getId(), getCurrentUser().getCompanyId(), form.getAmount());
			}

			String messageKey = String.format("funds.allocate_funds.success");
			messageHelper.addSuccess(messages, messageKey);
			flash.addFlashAttribute("success", true);
			return "redirect:/payments";

		} catch (Exception e) {
			messageHelper.addError(messages, "funds.transfer.exception");
			return "redirect:/payments";
		}

	}


	private void populateTaxEntityModelAndMessages(Model model, AbstractTaxEntity taxEntity, MessageBundle messages) {
		// check if tax entity exists - if USA it must also be in "approved" status
		if (taxEntity == null) {
			messageHelper.addWarning(messages, (Country.USA.equals(getCurrentUser().getCountry())) ?
				"funds.withdraw.no_usa_taxentity" :
				"funds.withdraw.no_taxentity");
			model.addAttribute("has_verified_taxentity", false);

		} else if (taxEntity instanceof UsaTaxEntity) {
			if (taxEntity.getStatus().isApproved()) {
				model.addAttribute("has_verified_taxentity", true);
			} else {
				messageHelper.addWarning(messages, "funds.withdraw.unverified_taxentity");
				model.addAttribute("has_verified_taxentity", false);
			}

		} else {
			model.addAttribute("has_verified_taxentity", true);
		}
	}

	private void populateAddCCModel(Model model) {
		if (!model.asMap().containsKey("addCreditCardForm")) {
			model.addAttribute("addCreditCardForm", new AddCreditCardForm());
		}
		model.addAttribute("statesCountries", formOptionsDataHelper.getStatesAsOptgroup());
		model.addAttribute("countries", invariantDataService.getCountryDTOs());
		model.addAttribute("doesCompanyHaveReservedFundsEnabledProject", companyService.doesCompanyHaveReservedFundsEnabledProject(getCurrentUser().getCompanyId()));
		Long userId = getCurrentUser().getId();

		Address companyAddress = profileService.findCompanyAddress(userId);
		Address profileAddress = profileService.findAddress(userId);

		JSONObject companyAddressJSON = new JSONObject();
		if (companyAddress != null) {
			try {
				companyAddressJSON.put("address1", companyAddress.getAddress1());
				companyAddressJSON.put("address2", companyAddress.getAddress2());
				companyAddressJSON.put("city", companyAddress.getCity());
				companyAddressJSON.put("state", companyAddress.getState().getShortName());
				companyAddressJSON.put("postal_code", companyAddress.getPostalCode());
				companyAddressJSON.put("country", companyAddress.getCountry());
				model.addAttribute("company_address", companyAddressJSON);
			} catch (JSONException e) {
				logger.error(e);
			}
		}

		JSONObject profileAddressJSON = new JSONObject();

		if (profileAddress != null) {
			try {
				profileAddressJSON.put("address1", profileAddress.getAddress1());
				profileAddressJSON.put("address2", profileAddress.getAddress2());
				profileAddressJSON.put("city", profileAddress.getCity());
				profileAddressJSON.put("state", profileAddress.getState().getShortName());
				profileAddressJSON.put("postal_code", profileAddress.getPostalCode());
				profileAddressJSON.put("country", profileAddress.getCountry());
				model.addAttribute("profile_address", profileAddressJSON);
			} catch (JSONException e) {
				logger.error(e);
			}
		}
	}


	private void populateInvoiceModel(Model model) {
		if (!model.asMap().containsKey("generateInvoiceForm"))
			model.addAttribute("generateInvoiceForm", new GenerateInvoiceForm());
		model.addAttribute("statesCountries", formOptionsDataHelper.getStatesAsOptgroup());
		model.addAttribute("countries", invariantDataService.getCountryDTOs());
	}

	private void populateAddACHModel(Model model) {
		final List<? extends AbstractBankAccount> accounts =
			bankingService.findConfirmedACHBankAccounts(getCurrentUser().getId());
		final List<String> unobfuscatedAccountNumbers = bankingService.getUnobfuscatedAccountNumbers(accounts);

		boolean hasUsaBankAccount = false;

		for (int i = 0; i < accounts.size(); i++) {
			final AbstractBankAccount account = accounts.get(i);
			if (Country.USA_COUNTRY.getId().equals(account.getCountry().getId())) {
				hasUsaBankAccount = true;
				break;
			}
		}

		model.addAttribute("accounts", accounts);
		model.addAttribute("unobfuscatedAccountNumbers", unobfuscatedAccountNumbers);
		model.addAttribute("addACHForm", new AddACHForm());
		model.addAttribute("spendLimit", getSpendLimit());
		model.addAttribute("hasUsaBankAccount", hasUsaBankAccount);
	}

	private void populateAddWire(Model model) {
		Set<User> users = authService.findAllAdminAndControllerUsersByCompanyId(getCurrentUser().getCompanyId());
		model.addAttribute("users", users);
		model.addAttribute("addFundsWireForm", new AddFundsWireForm());
	}
}
