package com.workmarket.web.controllers.screening;

import com.google.common.collect.Maps;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.screening.ScreeningObjectConverter;
import com.workmarket.domains.model.screening.ScreeningStatusType;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.ScreeningDTO;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.PaymentValidator;
import com.workmarket.web.validators.ScreeningValidator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/screening/bkgrnd")
@PreAuthorize("!principal.isMasquerading()")
@SessionAttributes({BackgroundCheckController.SCREENING_FORM, BackgroundCheckController.SCREENING_STEP})
@VelvetRope(
	venue = Venue.HIDE_BG_CHECKS,
	bypass = true,
	redirectPath = "/home",
	message = "You do not have access to this feature."
)
public class BackgroundCheckController extends BaseController {

	public static final String SCREENING_FORM = "screeningForm";
	public static final String SCREENING_STEP = "bkgrndScreeningStep";
	private static final Logger logger = LoggerFactory.getLogger(BackgroundCheckController.class);

	@Autowired private ScreeningService screeningService;
	@Autowired private ProfileService profileService;
	@Autowired private FormOptionsDataHelper dataHelper;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private PricingService pricingService;
	@Autowired private PaymentValidator paymentValidator;
	@Autowired private ScreeningValidator screeningValidator;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;
	@Autowired private UserService userService;
	@Autowired private AddressService addressService;

	@ModelAttribute("states")
	public Map<Long, String> populateStates() {
		return dataHelper.getStates();
	}

	@ModelAttribute("countries")
	public Map<String, String> populateCountries() {
		return dataHelper.getCountries();
	}

	@ModelAttribute("screeningPrices")
	public Map<String, BigDecimal> populateScreeningPrices() {
		ExtendedUserDetails userDetails = getCurrentUser();
		Map<String, BigDecimal> prices = Maps.newHashMap();

		try {
			prices.put(RegisterTransactionType.BACKGROUND_CHECK, pricingService.findBackgroundCheckPrice(userDetails.getCompanyId(), Country.USA));
			prices.put(RegisterTransactionType.BACKGROUND_CHECK_INTERNATIONAL, pricingService.findBackgroundCheckPrice(userDetails.getCompanyId(), Country.CANADA));
		} catch (Exception ex) {
			logger.warn("error populating background check prices for companyId={} and userId={}",
					new Object[]{userDetails.getCompanyId(), userDetails.getId()}, ex);
		}

		return prices;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String screeningForm(ModelMap model) throws Exception {
		ExtendedUserDetails userDetails = getCurrentUser();
		BackgroundCheck screening = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.findMostRecentBackgroundCheck(userDetails.getId()));

		model.addAttribute("accountSummary", accountRegisterService.getAccountRegisterSummaryFields(getCurrentUser().getCompanyId()));

		if (screening != null) {

			if (DateUtilities.getDaysBetween(screening.getResponseDate(), Calendar.getInstance()) > Constants.STANDARD_CALENDAR_YEAR_DAYS) {
				model.addAttribute("bkgrndCheckOld", true);
			}
			String status = screening.getScreeningStatusType().getCode();

			List<com.workmarket.screening.model.Screening> screenings = screeningService.findBackgroundChecksByUser(userDetails.getId());
			List<BackgroundCheck> backcheckScreenings = new ArrayList<>();
			for (com.workmarket.screening.model.Screening s : screenings) {
				backcheckScreenings.add((BackgroundCheck)ScreeningObjectConverter.convertScreeningResponseToMonolith(s));
			}
			model.addAttribute("previousBackgroundList", backcheckScreenings);
			model.addAttribute("bkgrndCheckPassed", ScreeningStatusType.PASSED.equals(status));
			model.addAttribute("bkgrndCheckFailed", ScreeningStatusType.FAILED.equals(status));
			model.addAttribute("bkgrndCheckPending", ScreeningStatusType.REQUESTED.equals(status));
			model.addAttribute("bkgrndCheckCancelled", ScreeningStatusType.CANCELLED.equals(status));

			if (ScreeningStatusType.BACKGROUND_CHECK_PURCHASE_STATUS_TYPES.contains(status)) {
				model.addAttribute("bkgrndCheckPurchase", true);
			}
		} else {
			model.addAttribute("bkgrndCheckPurchase", true);
		}

		User user = userService.findUserById(getCurrentUser().getId());
		Address address = addressService.findById(user.getProfile().getAddressId());
		ScreeningDTO screeningForm = new ScreeningDTO();

		BeanUtilities.copyProperties(screeningForm, user);
		BeanUtilities.copyProperties(screeningForm, address);

		// explicitly set the country, User.peopleSearchResult might not be returning correctly. Could this be a re-indexing issue?
		if (null != address.getCountry() && !StringUtils.isBlank(address.getCountry().getId())) {
			screeningForm.setCountry(address.getCountry().getId());
		} else {
			screeningForm.setCountry(Country.USA);
		}

		if (!screeningForm.getCountry().equals(Country.USA)) {
			model.addAttribute("intlMessage", Boolean.TRUE);
		}
		model.addAttribute("screeningType", Country.USA.equals(address.getCountry().getId()) ? RegisterTransactionType.BACKGROUND_CHECK : RegisterTransactionType.BACKGROUND_CHECK_INTERNATIONAL);
		model.addAttribute(SCREENING_FORM, screeningForm);
		model.addAttribute(SCREENING_STEP, "/payment");
		model.addAttribute("screeningFormUri", "/screening/bkgrnd");

		return "web/pages/screening/bkgrnd/index";
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public @ResponseBody AjaxResponseBuilder doScreeningForm(
			HttpSession session,
			@ModelAttribute(SCREENING_FORM) ScreeningDTO screeningForm,
			BindingResult bindingResult) throws Exception {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (hasFeature("background_check_international")) {

			screeningForm.setScreeningType(Screening.BACKGROUND_CHECK_TYPE);
			ValidationUtils.invokeValidator(screeningValidator, screeningForm, bindingResult);

			if (bindingResult.hasErrors()) {
				messageHelper.setErrors(response, bindingResult);
				screeningForm.setWorkIdentificationNumber(""); // hide SSN/SIN
				return response;
			}

			session.setAttribute(SCREENING_FORM, screeningForm);
			return response.setSuccessful(true).setRedirect("/screening/bkgrnd/payment");
		} else {
			return response.setRedirect("/");
		}
	}

	@RequestMapping(value = "/payment", method = RequestMethod.GET)
	public String paymentForm(Model model, HttpSession session) throws Exception {
		ExtendedUserDetails userDetails = getCurrentUser();

		model.addAttribute("available_balance", accountRegisterServicePrefundImpl.calculateWithdrawableCashByCompany(getCurrentUser().getCompanyId()));

		Address userAddress = profileService.findAddress(getCurrentUser().getId());
		if (userAddress != null) {
			model.addAttribute("address1", userAddress.getAddress1());
			model.addAttribute("address2", userAddress.getAddress2());
			model.addAttribute("city", userAddress.getCity());
			model.addAttribute("state", userAddress.getState());
			model.addAttribute("notIntlRequirement", Country.US.equals(userDetails.getCountry()));
			model.addAttribute("screeningType", Country.USA.equals(userAddress.getCountry().getId()) ? RegisterTransactionType.BACKGROUND_CHECK : RegisterTransactionType.BACKGROUND_CHECK_INTERNATIONAL);
		}

		if ("/payment".equals(session.getAttribute(SCREENING_STEP))) {
			PaymentDTO paymentForm = new PaymentDTO();
			paymentForm.setPaymentType("account");
			model.addAttribute("paymentForm", paymentForm);
			return "web/pages/screening/bkgrnd/payment";
		}

		return "redirect:/screening/bkgrnd";
	}

	@RequestMapping(value = "/payment", method = RequestMethod.POST)
	public String doPaymentForm(
			@ModelAttribute("paymentForm") PaymentDTO paymentForm,
			BindingResult bindingResult,
			Model model,
			HttpSession session,
			SessionStatus sessionStatus,
			RedirectAttributes flash) throws Exception {

		if (!"/payment".equals(session.getAttribute(SCREENING_STEP))) {
			return "redirect:/screening/bkgrnd";
		}

		MessageBundle messages = messageHelper.newBundle();

		model.addAttribute("bundle", messages);

		ValidationUtils.invokeValidator(paymentValidator, paymentForm, bindingResult);

		ExtendedUserDetails userDetails = getCurrentUser();

		// explicitly set the country. blanks are still getting through
		ScreeningDTO screeningForm = (ScreeningDTO)session.getAttribute(SCREENING_FORM);
		if (StringUtils.isBlank(screeningForm.getCountry())) {
			if (!StringUtils.isBlank(userDetails.getCountry())) {
				screeningForm.setCountry(userDetails.getCountry());
			} else {
				screeningForm.setCountry(Country.USA);
			}
		}

		logger.info("about to call pricingService.findBackgroundCheckPrice(userDetails.getCompanyId(), " +
			"screeningForm.getCountry()):  " + getCurrentUser().getCompanyId() + " and " + screeningForm.getCountry());

		BigDecimal screeningCost = pricingService.findBackgroundCheckPrice(userDetails.getCompanyId(), screeningForm.getCountry());
		BigDecimal withdrawableAmount = accountRegisterServicePrefundImpl.calculateWithdrawableCashByCompany(getCurrentUser().getCompanyId());

		if ("cc".equals(paymentForm.getPaymentType())) {
			if (bindingResult.hasErrors()) {
				messageHelper.setErrors(messages, bindingResult);
				return "web/pages/screening/bkgrnd/payment";
			}

			// We were getting some behaviour where empty screening form data was being passed in,
			// so let's do another validation check here
			BindingResult screeningFormErrors = new BeanPropertyBindingResult(screeningForm, "screeningForm");
			ValidationUtils.invokeValidator(screeningValidator, screeningForm, screeningFormErrors);
			if (screeningFormErrors.hasErrors()) {
				MessageBundle bundle = messageHelper.newFlashBundle(flash);
				messageHelper.setErrors(bundle, screeningFormErrors);
				return "redirect:/screening/bkgrnd";
			}

			paymentForm.setAmount(screeningCost.toString());
			screeningService.requestBackgroundCheck(getCurrentUser().getId(), screeningForm, paymentForm);
		}

		if ("account".equals(paymentForm.getPaymentType())) {
			if (withdrawableAmount.compareTo(screeningCost) < 0) {    // Is withdraw < test cost
				messageHelper.addError(messages, "insufficient_funds");
				return "web/pages/screening/bkgrnd/payment";
			}
			try {
				// We were getting some behaviour where empty screening form data was being passed in,
				// so let's do another validation check here
				BindingResult screeningFormErrors = new BeanPropertyBindingResult(screeningForm, "screeningForm");
				ValidationUtils.invokeValidator(screeningValidator, screeningForm, screeningFormErrors);
				if (screeningFormErrors.hasErrors()) {
					MessageBundle bundle = messageHelper.newFlashBundle(flash);
					messageHelper.setErrors(bundle, screeningFormErrors);
					return "redirect:/screening/bkgrnd";
				}

				screeningService.requestBackgroundCheck(getCurrentUser().getId(), screeningForm);
			} catch (InsufficientFundsException e) {
				return "web/pages/screening/bkgrnd/payment";
			}
		}

		sessionStatus.setComplete();
		session.removeAttribute(SCREENING_FORM);
		return "redirect:/screening/bkgrnd/thanks";
	}

	@RequestMapping(value="/thanks", method=RequestMethod.GET)
	public String thanks(Model model) {
		return "web/pages/screening/bkgrnd/thanks";
	}
}
