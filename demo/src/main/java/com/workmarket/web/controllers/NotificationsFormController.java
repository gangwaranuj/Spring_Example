package com.workmarket.web.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserNotificationPreferencePojo;
import com.workmarket.service.business.UserNotificationPrefsService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.NotificationPreferenceDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.NotificationsForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/mysettings")
public class NotificationsFormController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(NotificationsFormController.class);

	@Autowired private UserService userService;
	@Autowired private BillingService billingService;
	@Autowired private ProfileService profileService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserNotificationPrefsService userNotificationPrefsService;

	private static final Map<Integer,String> weekdayMap = new ImmutableMap.Builder<Integer,String>()
		.put(0, "Sunday")
		.put(1, "Monday")
		.put(2, "Tuesday")
		.put(3, "Wednesday")
		.put(4, "Thursday")
		.put(5, "Friday")
		.put(6, "Saturday")
		.build();

	private UserAvailability buildUserAvailability(List<UserAvailability> notificationHours) {
		// Get the from & to times. Right now, they are the same for all days. So we just grab the first one in the list.
		Iterator<UserAvailability> i = notificationHours.iterator();

		if (i.hasNext()) {
			UserAvailability current = i.next();

			if (!current.isAllDayAvailable()) {
				return current;
			}
		}

		return null;
	}

	private String buildHours(List<UserAvailability> notificationHours) {

		if (!notificationHours.isEmpty()) {
			// Get the current SMS notification hours.
			List<String> hours = new LinkedList<>();

			for (UserAvailability availability : notificationHours) {
				if (!availability.getDeleted()) {
					String weekDay = weekdayMap.get(availability.getWeekDay());
					hours.add(weekDay);
				}
			}

			return StringUtils.collectionToDelimitedString(hours, ", ");
		}

		return null;
	}

	private Map<String, String> buildPhoneNumbers(ProfileDTO profile) {
		Map<String, String> phoneNumbers = Maps.newHashMap();

		if (StringUtilities.isNotEmpty(profile.getWorkPhone())) {
			phoneNumbers.put("ivr.work", String.format("%s (work)", StringUtilities.formatPhoneNumber(profile.getWorkPhone())));
		}
		if (StringUtilities.isNotEmpty(profile.getMobilePhone())) {
			phoneNumbers.put("ivr.mobile", String.format("%s (mobile)", StringUtilities.formatPhoneNumber(profile.getMobilePhone())));
		}
		if (StringUtilities.isNotEmpty(profile.getSmsPhone())) {
			phoneNumbers.put("ivr.sms", String.format("%s (sms)", StringUtilities.formatPhoneNumber(profile.getSmsPhone())));
		}

		return phoneNumbers;
	}

	private NotificationsForm buildNotificationForm() {

		boolean isDispatcher = getCurrentUser().isDispatcher();
		final List<UserNotificationPreferencePojo> types = userNotificationPrefsService.findByUserWithDefault(getCurrentUser().getId());

		Map<String, NotificationPreferenceDTO> typesMap = Maps.newHashMap();

		for (UserNotificationPreferencePojo type : types) {
			boolean emailFlag = isDispatcher ? type.getDispatchEmailFlag() : type.getEmailFlag();
			boolean followFlag = type.getFollowFlag();
			boolean bullhornFlag = isDispatcher ? type.getDispatchBullhornFlag() : type.getBullhornFlag();
			boolean pushFlag = isDispatcher ? type.getDispatchPushFlag() : type.getPushFlag();
			boolean smsFlag = isDispatcher ? type.getDispatchSmsFlag() : type.getSmsFlag();
			boolean voiceFlag = isDispatcher ? type.getDispatchVoiceFlag() : type.getVoiceFlag();
			NotificationPreferenceDTO dto = new NotificationPreferenceDTO(type.getNotificationType(), emailFlag, followFlag, bullhornFlag, pushFlag, smsFlag, voiceFlag);
			typesMap.put(type.getNotificationType(), dto);
		}

		NotificationsForm form = new NotificationsForm();
		form.setNotifications(typesMap);

		return form;
	}

	@ModelAttribute
	public void populateModelData(Model model) {
		model.addAttribute("pushEnabled", userService.hasDevice(getCurrentUser().getId()));
		model.addAttribute("notificationsForm", buildNotificationForm());

		ProfileDTO profile = profileService.findProfileDTO(getCurrentUser().getId());
		model.addAttribute("profile", profile);
		model.addAttribute("phoneNumbers", buildPhoneNumbers(profile));
		List<UserAvailability> notificationHours = userService.findWeeklyNotificationHours(getCurrentUser().getId());
		model.addAttribute("hours", buildHours(notificationHours));
		model.addAttribute("userAvailability", buildUserAvailability(notificationHours));
	}

	@RequestMapping(
		value = "/notifications",
		method = GET)
	public String notificationsForm(Model model) {

		ExtendedUserDetails user = getCurrentUser();

		PaymentConfiguration configuration = billingService.findStatementPaymentConfigurationByCompany(user.getCompanyId());

		model.addAttribute("email", user.getEmail());
		model.addAttribute("statements_configuration", configuration);
		model.addAttribute("hasStatementEnabled", user.isStatementsEnabled());
		model.addAttribute("hasSubscriptionEnabled", user.isSubscriptionEnabled());
		model.addAttribute("hasPaymentCenterAndEmailsAccess", authenticationService.hasPaymentCenterAndEmailsAccess(user.getId(), Boolean.TRUE));
		model.addAttribute("hasManageFundsAccess", authenticationService.hasManageBankAndFundsAccess(user.getId(), Boolean.TRUE));

		return "web/pages/mysettings/notifications";
	}

	@RequestMapping(
		value = "/notifications",
		method = POST)
	public String saveNotifications(
		RedirectAttributes flash,
		@ModelAttribute("notificationsForm") NotificationsForm form) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		try {
			Collection<NotificationPreferenceDTO> values = form.getNotifications().values();
			NotificationPreferenceDTO[] dtos = values.toArray(new NotificationPreferenceDTO[values.size()]);
			userNotificationPrefsService.setPrefs(getCurrentUser().getId(), dtos);
			messageHelper.addSuccess(bundle, "mysettings.notifications.save.success");

			return "redirect:/mysettings/notifications";
		} catch (Exception ex) {
			// Output errors.
			logger.error("Error saving notifications", ex);
			messageHelper.addError(bundle, "mysettings.notifications.save.failure");
		}

		return notificationsForm(flash);
	}
}
