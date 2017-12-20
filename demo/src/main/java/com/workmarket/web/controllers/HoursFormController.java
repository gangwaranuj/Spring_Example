package com.workmarket.web.controllers;

import com.google.common.collect.Lists;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserAvailabilityDTO;
import com.workmarket.web.editors.CalendarTimeEditor;
import com.workmarket.web.forms.HoursForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.WorkingHoursHelper;
import com.workmarket.web.models.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/mysettings")
public class HoursFormController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(HoursFormController.class);

	@Autowired private UserService userService;
	@Autowired private MessageBundleHelper messageHelper;

	@ModelAttribute("user")
	public User user() {
		return userService.findUserById(getCurrentUser().getId());
	}

	@InitBinder("hoursForm")
	public void initHoursFormBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Calendar.class, new CalendarTimeEditor(getCurrentUser().getTimeZoneId()));
	}

	@ModelAttribute
	public void populateModelData(Model model) {
		List<UserAvailability> workingHours = userService.findWeeklyWorkingHours(getCurrentUser().getId());
		model.addAttribute("workingHours", workingHours);
		model.addAttribute("hoursForm", WorkingHoursHelper.getForm(workingHours, getCurrentUser().getTimeZoneId()));
	}

	@RequestMapping(value="/hours", method= RequestMethod.GET)
	public String showHours() {
		return "web/pages/mysettings/hours";
	}

	@RequestMapping(value="/hours", method= RequestMethod.POST)
	public String saveHours(@ModelAttribute("hoursForm") HoursForm form, RedirectAttributes model, BindingResult result) {
		MessageBundle bundle = messageHelper.newFlashBundle(model);

		ExtendedUserDetails user = getCurrentUser();
		List<UserAvailabilityDTO> workingHours = Lists.newArrayList(form.getWorkingHours().values());

		for (UserAvailabilityDTO hour : workingHours) {
			if ((hour.getToTime() == null) && (hour.getFromTime() == null)) {
				hour.setAllDayAvailable(Boolean.TRUE);
			}
			else {
				hour.setAllDayAvailable(Boolean.FALSE);
			}
		}

		try {
			logger.debug("updating working hours for user id={}: {}", user.getId(), workingHours);

			// Save the settings
			userService.updateUserWorkingHours(user.getId(), workingHours);
			messageHelper.addSuccess(bundle, "mysettings.hours.success");
			return "redirect:/mysettings/hours";
		} catch (Exception ex) {
			logger.error("Error occurred while updating user working hours", ex);
			messageHelper.addError(bundle, "mysettings.hours.failure");
			return "web/pages/mysettings/hours";
		}
	}
}