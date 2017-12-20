package com.workmarket.web.controllers.user;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.request.PasswordResetRequest;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.WebUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.ForgotUserPasswordForm;
import com.workmarket.web.forms.user.PasswordResetForm;
import com.workmarket.web.forms.user.PasswordSetupForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.PasswordResetValidator;
import com.workmarket.web.validators.PasswordSetupValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class PasswordResetController extends BaseController {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private RegistrationService registrationService;
	@Autowired private CompanyService companyService;
	@Autowired private RequestService requestService;

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private PasswordResetValidator passwordResetValidator;
	@Autowired private PasswordSetupValidator passwordSetupValidator;
	@Autowired private MetricRegistry registry;

	private MetricRegistryFacade facade;

	@PostConstruct
	public void init() {
		facade = new WMMetricRegistryFacade(registry, "auth.resetpw");
	}

	@RequestMapping(value = "/send_forgot_password", method = RequestMethod.POST)
	public String sendForgotPassword(
			@Valid @ModelAttribute("forgotUserPassword") ForgotUserPasswordForm user,
			BindingResult bindingResult,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes,
			Model model) {
		facade.meter("send_forgot").mark();
		boolean isAjaxRequest = WebUtilities.isAjax(request);

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			if(isAjaxRequest) {
				model.addAttribute("messages", messages);
				model.addAttribute("successful", false);
				return "/login#reset";
			}
			return "redirect:/login#reset";
		}

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		try {
			registrationService.sendForgotPasswordEmail(user.getUserEmail());
		} catch (BadCredentialsException e) {
			// ignore since we will display success message regardless
		}
		messageHelper.addSuccess(messages, "user.password.forgot.success");
		if(isAjaxRequest) {
			model.addAttribute("messages", messages);
			model.addAttribute("successful", true);
			return "/login";
		}
		return "redirect:/login";
	}


	/**
	 * Password recovery screen.
	 * Requires a valid password reset request token.
	 */
	@RequestMapping(value = "/reset_password/{encryptedId}", method = RequestMethod.GET)
	public String resetPassword(
			@PathVariable("encryptedId") String encryptedId,
			@ModelAttribute("passwordResetForm") PasswordResetForm form,
			MessageBundle messages,
			Model model,
			RedirectAttributes redirectAttributes) {
		facade.meter("reset-get").mark();
		User user = registrationService.validatePasswordResetRequest(encryptedId);
		if (user == null) {
			messageHelper.addError(messages, "user.password.reset.invalid");
			redirectAttributes.addFlashAttribute("bundle", messages);
			return "redirect:/login#reset";
		}

		model.addAttribute("user", user);

		return "web/pages/user/reset_password";
	}


	@RequestMapping(value = "/reset_password/{encryptedId}", method = RequestMethod.POST)
	public String doResetPassword(
			@PathVariable("encryptedId") String encryptedId,
			@ModelAttribute("passwordResetForm") PasswordResetForm form,
			BindingResult bindingResult,
			MessageBundle messages,
			RedirectAttributes redirectAttributes) {
		facade.meter("reset-post").mark();

		redirectAttributes.addFlashAttribute("bundle", messages);

		User user = registrationService.validatePasswordResetRequest(encryptedId);
		if (user == null) {
			messageHelper.addError(messages, "user.password.reset.invalid");
			return "redirect:/login#reset";
		}

		final PasswordResetRequest request = requestService.findPasswordResetRequest(encryptedId);
		final String email = request.getInvitedUser().getEmail();
		passwordResetValidator.validate(form, email, bindingResult);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			return "redirect:/user/reset_password/{encryptedId}";
		}

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		authenticationService.doResetPassword(encryptedId, form.getPasswordNew());

		messageHelper.addSuccess(messages, "user.password.reset.success");

		return "redirect:/login";
	}


	/**
	 * Password setup screen.
	 * For new users added via a third-party.
	 * Requires a valid password reset request token.
	 */
	@RequestMapping(value = "/setup_password/{encryptedId}", method = RequestMethod.GET)
	public String setupPassword(
			@PathVariable("encryptedId") String encryptedId,
			@ModelAttribute("passwordSetupForm") PasswordSetupForm form,
			MessageBundle messages,
			Model model,
			RedirectAttributes redirectAttributes) {
		facade.meter("setuppw-get").mark();

		User user = registrationService.validatePasswordResetRequest(encryptedId);
		if (user == null) {
			messageHelper.addError(messages, "user.password.reset.invalid");
			redirectAttributes.addFlashAttribute("bundle", messages);
			return "redirect:/login#reset";
		}

		model.addAttribute("company", companyService.findCompanyById(user.getCompany().getId()));

		return "web/pages/user/setup_password";
	}


	@RequestMapping(value = "/setup_password/{encryptedId}", method = RequestMethod.POST)
	public String doSetupPassword(
			@PathVariable("encryptedId") String encryptedId,
			@ModelAttribute("passwordSetupForm") PasswordSetupForm form,
			BindingResult bindingResult,
			MessageBundle messages,
			RedirectAttributes redirectAttributes) {
		facade.meter("setuppw-post").mark();
		redirectAttributes.addFlashAttribute("bundle", messages);

		User user = registrationService.validatePasswordResetRequest(encryptedId);
		if (user == null) {
			messageHelper.addError(messages, "user.password.reset.invalid");
			return "redirect:/login#reset";
		}

		passwordSetupValidator.validate(form, user.getEmail(), bindingResult);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			return "redirect:/user/setup_password/{encryptedId}";
		}

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		authenticationService.doResetPassword(encryptedId, form.getPasswordNew());
		authenticationService.setCurrentUser(user.getUserNumber());
		registrationService.confirmAndApproveAccount(user.getId());

		messageHelper.addSuccess(messages, "user.password.setup.success");

		return String.format("redirect:/login?login=%s", StringUtilities.urlEncode(user.getEmail(), "utf-8"));
	}
}
