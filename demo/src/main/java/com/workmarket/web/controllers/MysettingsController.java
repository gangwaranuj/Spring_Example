package com.workmarket.web.controllers;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserNotificationPrefsService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.forms.PasswordChangeForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.PasswordChangeValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/mysettings")
public class MysettingsController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(MysettingsController.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserNotificationPrefsService userNotificationPrefsService;
	@Autowired private ProfileService profileService;
	@Autowired private UserService userService;
	@Autowired private PasswordChangeValidator passwordChangeValidator;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private MessageSource messageSource;



	@RequestMapping(
			value = "/password",
			method = GET)
	public String passwordForm(Model model) {
		model.addAttribute("passwordChangeForm", new PasswordChangeForm());
		return "web/pages/mysettings/password";
	}


	@RequestMapping(
			value = "/password",
			method = POST)
	public String changePassword(
			@ModelAttribute("passwordChangeForm") PasswordChangeForm form,
			BindingResult result,
			RedirectAttributes flash) {
		ExtendedUserDetails user = getCurrentUser();

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		passwordChangeValidator.validate(form, user.getEmail(), result);

		if (result.hasErrors()) {
			messageHelper.setErrors(bundle, result);
		} else {
			try {
				authenticationService.changePassword(form.getCurrentPassword(), form.getNewPassword());
				messageHelper.addSuccess(bundle, "mysettings.password.change.success");
			} catch (Exception ex) {
				logger.error("Failed to update password", ex);
				messageHelper.addError(bundle, "mysettings.password.change.failure");
			}
		}

		return "redirect:/mysettings/password";
	}


	@RequestMapping(
			value = "/suspend",
			method = POST,
			produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map suspend(HttpServletResponse response) {
		ExtendedUserDetails user = getCurrentUser();
		String message;

		try {
			userService.holdUser(user.getId(), Boolean.TRUE);
			message = messageSource.getMessage("mysettings.suspend.success", null, null);
		} catch (Exception ex) {
			message = messageSource.getMessage("mysettings.suspend.failure", null, null);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("failed to suspend user id: %s", user.getId()), ex);
		}

		return CollectionUtilities.newObjectMap(
				"message", message
		);
	}

	@RequestMapping(
			value = "/reactivate",
			method = POST,
			produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map reactivate(HttpServletResponse response) {
		ExtendedUserDetails user = getCurrentUser();
		String message;

		try {
			userService.holdUser(user.getId(), Boolean.FALSE);
			message = messageSource.getMessage("mysettings.reactivate.success", null, null);
		} catch (Exception ex) {
			message = messageSource.getMessage("mysettings.reactivate.failure", null, null);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("failed to reactivate account for user id: %s", user.getId()), ex);
		}

		return CollectionUtilities.newObjectMap(
				"message", message
		);
	}

	@RequestMapping(
			value = "/remove_sms",
			method = POST)
	public @ResponseBody Map removeSms( HttpServletResponse response ) {

		ExtendedUserDetails user = getCurrentUser();

		String message;
		Map<String, String> properties = CollectionUtilities.newStringMap(
				"smsPhone", null,
				"smsPhoneVerified", "0"
		);

		try {
			profileService.updateProfileProperties(user.getId(), properties);
			userNotificationPrefsService.removeSMSNotifications(user.getId());
			message = messageSource.getMessage("mysettings.removeSms.success", null, null);
		} catch (Exception ex) {
			logger.error(String.format("Error removing sms preferences for user id: %s", user.getId()), ex);
			message = messageSource.getMessage("mysettings.removeSms.failure", null, null);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		return CollectionUtilities.newObjectMap(
				"message", message
		);
	}


	@RequestMapping(
		value = "/devices",
		method = GET)
	public String devices(Model model) {

		model.addAttribute("devices", userService.findAllUserDevicesByUserId(getCurrentUser().getId()));
		return "web/pages/mysettings/devices";
	}

	@RequestMapping(
			value = "/remove_device",
			method = POST)
	public AjaxResponseBuilder removeDevice(
			@RequestParam("device_uid") String deviceUID) {

		AjaxResponseBuilder ajaxResponseBuilder = new AjaxResponseBuilder().setSuccessful(false);
		boolean success = userService.removeDevice(getCurrentUser().getId(), deviceUID);
		return ajaxResponseBuilder.setSuccessful(success);
	}
}
