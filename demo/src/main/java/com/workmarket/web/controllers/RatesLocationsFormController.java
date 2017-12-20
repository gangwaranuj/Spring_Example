package com.workmarket.web.controllers;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.Profile;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.LocationTypeDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.editors.BigDecimalEditor;
import com.workmarket.web.forms.RatesLocationsForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.RatesLocationsFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profile-edit/rates_locations")
public class RatesLocationsFormController extends BaseController {

	@Autowired private ProfileService profileService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private RatesLocationsFormValidator formValidator;
	@Autowired private MessageBundleHelper messageHelper;

	@ModelAttribute("locationTypes")
	public ArrayList<LocationTypeDTO> locationTypes() {
		return Lists.newArrayList(invariantDataService.getLocationTypeDTOs());
	}

	@ModelAttribute("ratesLocationsForm")
	public RatesLocationsForm createModel(@ModelAttribute("locationTypes") ArrayList<LocationTypeDTO> locationTypes) {
		Profile profile = profileService.findProfile(getCurrentUser().getId());
		RatesLocationsForm form = new RatesLocationsForm();
		form.setMinOnsiteHourlyRate(profile.getMinOnsiteHourlyRate());
		form.setMinOnsiteWorkPrice(profile.getMinOnsiteWorkPrice());
		form.setMinOffsiteHourlyRate(profile.getMinOffsiteHourlyRate());
		form.setMinOffsiteWorkPrice(profile.getMinOffsiteWorkPrice());
		form.setMaxTravelDistance(profile.getMaxTravelDistance());

		// copy all available values
		Map<Long,Boolean> currentLocationTypes = new HashMap<>();
		for (LocationTypeDTO locationType : locationTypes) {
			currentLocationTypes.put(locationType.getId(), Boolean.FALSE);
		}

		List<LocationType> userLocationTypes = profileService.findLocationTypesPreferenceByUserId(getCurrentUser().getId());

		if (userLocationTypes.isEmpty()) {
			Iterator<LocationTypeDTO> it = locationTypes.iterator();
			if (it.hasNext()) {
				// Default to "Commercial" if empty (that is if assuming that it is the first on the list)
				LocationTypeDTO locationType = it.next();
				currentLocationTypes.put(locationType.getId(), Boolean.TRUE);
			}
		} else {
			for (LocationType locationType : userLocationTypes) {
				currentLocationTypes.put(locationType.getId(), Boolean.TRUE);
			}
		}

		form.setCurrentLocationTypes(currentLocationTypes);
		return form;
	}

	@ModelAttribute("excludedPostalCodes")
	public List<String> excludedPostalCodes() {
		return profileService.findBlacklistedZipcodesForUser(getCurrentUser().getId());
	}

	@InitBinder("ratesLocationsForm")
	public void initRatesLocationsFormBinder(WebDataBinder binder) {
		binder.registerCustomEditor(BigDecimal.class, new BigDecimalEditor());
	}

	@RequestMapping(method=RequestMethod.GET)
	public String ratesLocations(@ModelAttribute("ratesLocationsForm") final RatesLocationsForm form) {
		return "web/pages/profileedit/rates_locations";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String saveRatesLocations(
			@ModelAttribute("ratesLocationsForm") RatesLocationsForm form,
			BindingResult result,
			RedirectAttributes flash) throws Exception {

		MessageBundle messages = messageHelper.newFlashBundle(flash);

		formValidator.validate(form, result);

		if (result.hasErrors()) {
			messageHelper.setErrors(messages, result);
			return "redirect:/profile-edit/rates_locations";
		}

		Long userId = getCurrentUser().getId();
		List<Long> locationTypeIds = form.getSelectedCurrentLocationTypes();
		profileService.updateLocationTypePreferences(userId, locationTypeIds.toArray(new Long[locationTypeIds.size()]));

		BigDecimal maxTravelDistance;
		if (form.getMaxTravelDistance() != null) {
			// validate the travel distance, which can't possibly be more than the earth's circumference, 24,901 miles
			maxTravelDistance = new BigDecimal(Math.min(form.getMaxTravelDistance().floatValue(), Constants.EARTHS_CIRCUMFERENCE_AS_MAX_TRAVEL_DISTANCE_IN_MILES.floatValue()));
		} else { // default
			maxTravelDistance = new BigDecimal(Constants.MAX_TRAVEL_DISTANCE);
		}

		Map<String, String > properties = CollectionUtilities.newStringMap(
			"minOnsiteHourlyRate", String.valueOf(form.getMinOnsiteHourlyRate()),
			"minOnsiteWorkPrice", String.valueOf(form.getMinOnsiteWorkPrice()),
			"minOffsiteHourlyRate", String.valueOf(form.getMinOffsiteHourlyRate()),
			"minOffsiteWorkPrice", String.valueOf(form.getMinOffsiteWorkPrice()),
			"maxTravelDistance", maxTravelDistance.toString()
		);
		profileService.updateProfileProperties(userId, properties);

		messageHelper.addSuccess(messages, "profile.rates_locations.success");
		return "redirect:/profile-edit/rates_locations";
	}
}
