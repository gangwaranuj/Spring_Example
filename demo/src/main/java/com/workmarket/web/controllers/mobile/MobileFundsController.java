package com.workmarket.web.controllers.mobile;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.cache.PaymentCenterAggregateSummary;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InvalidBankAccountException;
import com.workmarket.service.exception.account.WithdrawalExceedsDailyMaximumException;
import com.workmarket.service.exception.tax.InvalidTaxEntityException;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.funds.WithdrawFundsForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/mobile/funds")
@PreAuthorize("!principal.userFundsAccessBlocked AND hasRole('PERMISSION_WITHDRAW')")
public class MobileFundsController extends BaseController {

	private static final Log logger = LogFactory.getLog(MobileFundsController.class);

	@Autowired private TaxService taxService;
	@Autowired private BankingService bankingService;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private SummaryService summaryService;


	@RequestMapping(method = GET)
	public String withdrawFunds(
		@ModelAttribute("withdrawFundsForm") WithdrawFundsForm form,
		@ModelAttribute("bundle") MessageBundle messages, Model model) {

		Long userId = getCurrentUser().getId();
		AbstractTaxEntity taxEntity = lookupTaxEntity(userId);
		populateTaxEntityModelAndMessages(model, taxEntity, messages);

		Long companyId = getCurrentUser().getCompanyId();
		model.addAttribute("accounts", bankingService.findConfirmedBankAccounts(userId));
		model.addAttribute("available_balance", getAvailableBalance(companyId));
		model.addAttribute("paypalFees", jsonSerializationService.toJson(pricingService.findCostForTransactionTypesByCompany
		    (companyId, RegisterTransactionType.PAY_PAL_FEE_REGISTER_TRANSACTION_TYPES)));
		model.addAttribute("paypalCountryCodes", jsonSerializationService.toJson(RegisterTransactionType.PAYPAL_COUNTRY_TRANSACTION_CODE_MAP));

		if (taxEntity != null) {
			model.addAttribute("payto", taxEntity.getTaxName());
		}
		model.addAttribute("sellerSums", getSellerSums(userId));

		return "mobile/pages/v2/funds/index";
	}

	@RequestMapping(
		value = "/withdraw",
		method = POST)
	public String doWithdrawMobile(
		@Valid @ModelAttribute("withdrawFundsForm") WithdrawFundsForm form,
		BindingResult bind,
		RedirectAttributes flash) throws InstantiationException, IllegalAccessException {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		List<String> validationErrors = validateFundsWithdrawal(getCurrentUser());
		if (CollectionUtils.isNotEmpty(validationErrors)) {
			for(String errorKey : validationErrors) {
				messageHelper.addError(bundle, errorKey);
			}
			return "redirect:/mobile/funds";
		}

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mobile/funds";
		}

		Long userId = getCurrentUser().getId();

		try {
			accountRegisterServicePrefundImpl.withdrawFundsFromRegister(userId, form.getAccount(), form.getAmount().toString());
			AbstractBankAccount account = bankingService.findBankAccount(form.getAccount());
			messageHelper.addSuccess(bundle, String.format("funds.withdraw.%s.success", StringUtils.lowerCase(account.getType())));
			flash.addFlashAttribute("success", true);

			return "redirect:/mobile/funds";

		} catch (WithdrawalExceedsDailyMaximumException e) {
			messageHelper.addError(bundle, "funds.withdraw.exceed_max", Constants.DAILY_WITHDRAWAL_LIMIT);
		} catch (InsufficientFundsException e) {
			messageHelper.addError(bundle, "funds.withdraw.insufficient");
		} catch (InvalidBankAccountException e) {
			messageHelper.addError(bundle, "funds.withdraw.invalid_account");
		} catch (InvalidTaxEntityException e) {
			messageHelper.addError(bundle, (Country.USA.equals(getCurrentUser().getCountry())) ?
				"funds.withdraw.no_usa_taxentity" :
				"funds.withdraw.no_taxentity");
		} catch (Exception e) {
			logger.error("Problem withdrawing funds: ", e);
			messageHelper.addError(bundle, "funds.withdraw.exception");
		}

		messageHelper.setErrors(bundle, bind);
		return "redirect:/mobile/funds";
	}

	public AbstractTaxEntity lookupTaxEntity(Long userId) {
		return taxService.findActiveTaxEntity(userId);
	}

	public BigDecimal getAvailableBalance(Long companyId) {
		return accountRegisterServicePrefundImpl.calculateWithdrawableCashByCompany(companyId);
	}

	public PaymentCenterAggregateSummary getSellerSums(Long userId) {
		return summaryService.getPaymentCenterAggregateSummaryForSeller(userId);
	}

	public List<String> validateFundsWithdrawal(ExtendedUserDetails user) {
		List<String> errorList = new LinkedList<String>();

		if (!getCurrentUser().hasAllRoles("PERMISSION_WITHDRAW")) {
			errorList.add("funds.withdraw.unauthorized");
		}

		if (getCurrentUser().getCompanyIsLocked()) {
			errorList.add("funds.withdraw.company_locked");
		}
		return errorList;
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
}
