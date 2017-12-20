package com.workmarket.web.controllers.screening;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.screening.ScreeningObjectConverter;
import com.workmarket.domains.model.screening.ScreeningStatusType;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.PricingService;
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@RequestMapping("/screening/drug")
@PreAuthorize("!principal.isMasquerading()")
@SessionAttributes({DrugTestController.SCREENING_FORM, DrugTestController.SCREENING_STEP})
@VelvetRope(
	venue = Venue.HIDE_DRUG_TESTS,
	bypass = true,
	redirectPath = "/home",
	message = "You do not have access to this feature."
)
public class DrugTestController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(DrugTestController.class);

	public static final String SCREENING_FORM = "screeningForm";
	public static final String SCREENING_STEP = "drugScreeningStep";

	@Autowired private ScreeningService screeningService;
	@Autowired private UserService userService;
	@Autowired private AddressService addressService;
	@Autowired private FormOptionsDataHelper dataHelper;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private PricingService pricingService;
	@Autowired private PaymentValidator paymentValidator;
	@Autowired private ScreeningValidator screeningValidator;

	@ModelAttribute("states")
	public Map<Long, String> populateStates() {
		return dataHelper.getStates();
	}

	@ModelAttribute("countries")
	public Map<String, String> populateCountries() {
		return dataHelper.getCountries();
	}

	@ModelAttribute("screeningPrice")
	public BigDecimal populateScreeningPrice() {
		ExtendedUserDetails userDetails = getCurrentUser();
		BigDecimal screeningPrice = null;
		try {
			screeningPrice = pricingService.findDrugTestPrice(userDetails.getCompanyId());
		} catch (Exception ex) {
			logger.warn("error populating drug test price for companyId={} and userId={}",
					new Object[] {userDetails.getCompanyId(), userDetails.getId()}, ex);
		}

		return screeningPrice;
	}

	@RequestMapping(method=RequestMethod.GET)
	public String screeningForm(Model model) throws Exception {
		ExtendedUserDetails userDetails = getCurrentUser();
		DrugTest screening = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.findMostRecentDrugTest(userDetails.getId()));

		if (screening != null) {
			if (DateUtilities.getDaysBetween(screening.getResponseDate(), Calendar.getInstance()) > (Constants.STANDARD_CALENDAR_YEAR_DAYS)) {
				model.addAttribute("drugTestOld", true);
			}

			String status = screening.getScreeningStatusType().getCode();

			List<com.workmarket.screening.model.Screening> screenings =
				screeningService.findDrugTestsByUser(userDetails.getId());

			List<DrugTest> drugTests = new ArrayList<>();
			for (com.workmarket.screening.model.Screening s : screenings) {
				drugTests.add((DrugTest)ScreeningObjectConverter.convertScreeningResponseToMonolith(s));
			}

			model.addAttribute("previousDrugList", drugTests);
			model.addAttribute("drugTestPassed", ScreeningStatusType.PASSED.equals(status));
			model.addAttribute("drugTestFailed", ScreeningStatusType.FAILED.equals(status));
			model.addAttribute("drugTestPending", ScreeningStatusType.REQUESTED.equals(status));

			if (ScreeningStatusType.ERROR.equals(status) || ScreeningStatusType.PASSED.equals(status)) {
				model.addAttribute("drugTestPurchase", true);
			}
		} else {
			model.addAttribute("drugTestPurchase", true);
		}

		User user = userService.findUserById(userDetails.getId());
		Address address = addressService.findById(user.getProfile().getAddressId());
		ScreeningDTO screeningForm = new ScreeningDTO();

		BeanUtilities.copyProperties(screeningForm, user);
		BeanUtilities.copyProperties(screeningForm, address);

		// This logic is similar to BackgroundCheckController.java:~public String screeningForm(ModelMap model) throws Exception
		if (!StringUtils.isBlank(getCurrentUser().getCountry())) {
			screeningForm.setCountry(getCurrentUser().getCountry());
		} else if (null != address.getCountry() && !StringUtils.isBlank(address.getCountry().getId())) {
			screeningForm.setCountry(address.getCountry().getId());
		} else {
			screeningForm.setCountry(Country.USA);
		}

		if (!screeningForm.getCountry().equals(Country.USA)) {
			model.addAttribute("intlMessage", Boolean.TRUE);
		}
		model.addAttribute(SCREENING_FORM, screeningForm);
		model.addAttribute(SCREENING_STEP, "/payment");
		model.addAttribute("screeningFormUri", "/screening/drug");
		return "web/pages/screening/drug/index";
	}

	@RequestMapping(value="", method=RequestMethod.POST)
	public @ResponseBody AjaxResponseBuilder doScreeningForm(
			HttpSession session,
			@ModelAttribute(SCREENING_FORM) ScreeningDTO screeningForm,
			BindingResult bindingResult) throws Exception {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		screeningForm.setScreeningType(Screening.DRUG_TEST_TYPE);
		ValidationUtils.invokeValidator(screeningValidator, screeningForm, bindingResult);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(response, bindingResult);
			screeningForm.setWorkIdentificationNumber(""); // hide SSN
			return response;
		}

		session.setAttribute(SCREENING_FORM, screeningForm);
		return response.setSuccessful(true).setRedirect("/screening/drug/payment");
	}

	@RequestMapping(value="/payment", method=RequestMethod.GET)
	public String paymentForm(Model model, HttpSession session) throws Exception {
		model.addAttribute("available_balance", accountRegisterServicePrefundImpl.calculateWithdrawableCashByCompany(getCurrentUser().getCompanyId()));

		if ("/payment".equals(session.getAttribute(SCREENING_STEP))) {
			PaymentDTO paymentForm = new PaymentDTO();
			paymentForm.setPaymentType("account");
			model.addAttribute("paymentForm", paymentForm);
			return "web/pages/screening/drug/payment";
		}
		return "redirect:/screening/drug";
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
			return "redirect:/screening/drug";
		}

		MessageBundle messages = messageHelper.newBundle();
		model.addAttribute("bundle", messages);

		ValidationUtils.invokeValidator(paymentValidator, paymentForm, bindingResult);

		ExtendedUserDetails userDetails = getCurrentUser();
		ScreeningDTO screeningForm = (ScreeningDTO)session.getAttribute(SCREENING_FORM);
		BigDecimal screeningCost = pricingService.findDrugTestPrice(userDetails.getCompanyId());

		// explicitly set the country. blanks are still getting through
		if (StringUtils.isBlank(screeningForm.getCountry())) {
			if (!StringUtils.isBlank(userDetails.getCountry())) {
				screeningForm.setCountry(userDetails.getCountry());
			} else {
				screeningForm.setCountry(Country.USA);
			}
		}

		// We were getting some behaviour where empty screening form data was being passed in,
		// so let's do another validation check here
		BindingResult screeningFormErrors = new BeanPropertyBindingResult(screeningForm, "screeningForm");
		ValidationUtils.invokeValidator(screeningValidator, screeningForm, screeningFormErrors);
		if (screeningFormErrors.hasErrors()) {
			MessageBundle bundle = messageHelper.newFlashBundle(flash);
			messageHelper.setErrors(bundle, screeningFormErrors);
			return "redirect:/screening/drug";
		}

		if ("cc".equals(paymentForm.getPaymentType())) {
			if (bindingResult.hasErrors()) {
				messageHelper.setErrors(messages, bindingResult);
				return "web/pages/screening/drug/payment";
			}
			paymentForm.setAmount(screeningCost.toString());
			screeningService.requestDrugTest(userDetails.getId(), screeningForm, paymentForm);
		} else {
			try {
				screeningService.requestDrugTest(userDetails.getId(), screeningForm);
			} catch (InsufficientFundsException e) {
				messageHelper.addError(messages, "insufficient_funds");
				return "web/pages/screening/drug/payment";
			}
		}

		sessionStatus.setComplete();
		return "redirect:/screening/drug/thanks";
	}

	@RequestMapping(value="/passed", method=RequestMethod.GET)
	public String passed(Model model) {
		return "web/pages/screening/drug/passed";
	}

	@RequestMapping(value="/pending", method=RequestMethod.GET)
	public String pending(Model model) {
		return "web/pages/screening/drug/pending";
	}

	@RequestMapping(value="/failed", method=RequestMethod.GET)
	public String failed(Model model) {
		return "web/pages/screening/drug/failed";
	}

	@RequestMapping(value="/thanks", method=RequestMethod.GET)
	public String thanks(Model model) {
		return "web/pages/screening/drug/thanks";
	}
}
