
package com.workmarket.web.controllers;

import com.workmarket.domains.model.MobileProvider;
import com.workmarket.domains.model.Profile;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.web.controllers.assignments.WorkDetailsController;
import com.workmarket.web.forms.SmsResponseForm;
import com.workmarket.web.forms.sms.SmsForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sms")
public class SmsController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(WorkDetailsController.class);
	private static final String SMS_PHONE = "smsPhone";

	@Autowired private ProfileService profileService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private NotificationService notificationService;
	@Autowired private MessageBundleHelper messageHelper;

	@RequestMapping(
		value = "/step1",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody SmsResponseForm step1(final HttpServletResponse response) {
		final Long id = getCurrentUser().getId();
		String smsPhone = null;
		Long mobileProviderId = null;
		List<MobileProvider> providers = new ArrayList<>();

		try {
			Profile profile = profileService.findProfile(id);
			smsPhone = profile.getSmsPhone();
			mobileProviderId = profile.getMobileProvider().getId();
			providers.addAll(invariantDataService.findAllMobileProviders());
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("Error fetching all mobile providers for user %s", getCurrentUser().getId()), e);
		}

		return new SmsResponseForm(smsPhone, mobileProviderId, providers);
	}

	@RequestMapping(
		value = "/step1",
		method = RequestMethod.POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody SmsResponseForm step1Submit(
		final @RequestBody SmsForm form,
		final HttpServletResponse response) {

		final Long id = getCurrentUser().getId();
		final Profile profile = profileService.findProfile(id);
		String smsPhone = form.getSmsPhone();
		String message = null;

		if (smsPhone.isEmpty()) {
			message = messageHelper.getMessage("profile.notifications.sms_phone.required");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			smsPhone = profile.getSmsPhone();
		} else {
			Map<String, String> profileProperties = new HashMap<>();
			profileProperties.put(SMS_PHONE, smsPhone);

			try {
				profileService.updateProfileProperties(id, profileProperties);
				notificationService.sendMobileVerificationCode(id);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				message = messageHelper.getMessage("profile.notifications.sms_phone.required");
				logger.error(String.format("Error updating profile properties for user %s", id), e);
			}
		}

		return new SmsResponseForm(message, smsPhone);
	}

	@RequestMapping(
		value = "/step2",
		method = RequestMethod.POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody SmsResponseForm step2Submit(
		final @RequestBody SmsForm form,
		final HttpServletResponse response) {

		final Long id = getCurrentUser().getId();
		String message;

		if (notificationService.verifyMobileVerificationCode(id, form.getCode())) {
			message = messageHelper.getMessage("profile.notifications.code.valid");
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			message = messageHelper.getMessage("profile.notifications.code.invalid");
			logger.error(String.format("Error verifying mobile code for user %s", id));
		}

		return new SmsResponseForm(message);
	}
}
