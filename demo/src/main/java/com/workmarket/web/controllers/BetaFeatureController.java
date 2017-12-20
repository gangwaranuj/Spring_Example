package com.workmarket.web.controllers;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.domains.velvetrope.service.BetaFeatureService;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/beta_features")
public class BetaFeatureController extends BaseController {

	private static final String TURN_BETA_FEATURE_ON = "/on";
	private static final String TURN_BETA_FEATURE_OFF = "/off";

	@Resource private AdmissionService admissionService;
	@Resource private BetaFeatureService betaFeatureService;
	@Resource private MessageBundleHelper messageBundleHelper;

	@RequestMapping(
		value = TURN_BETA_FEATURE_ON,
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody
	AjaxResponseBuilder turnBetaFeatureOn(@RequestBody Venue betaFeature) {

		ExtendedUserDetails user = getCurrentUser();
		if(!betaFeatureService.canToggleOwnCompanyBetaFeatureParticipation(user.getId())) {
			String message = getAdminErrorMessage("on");
			return AjaxResponseBuilder.fail().addMessage(message);
		}

		admissionService.saveAdmissionForCompanyIdAndVenue(user.getCompanyId(), betaFeature);
		String message = getToggleSuccessMessage("on");
		return AjaxResponseBuilder.success().addMessage(message);
	}

	@RequestMapping(
		value = TURN_BETA_FEATURE_OFF,
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody
	AjaxResponseBuilder turnBetaFeatureOff(@RequestBody Venue betaFeature) {

		ExtendedUserDetails user = getCurrentUser();
		if(!betaFeatureService.canToggleOwnCompanyBetaFeatureParticipation(user.getId())) {
			String message = getAdminErrorMessage("off");
			return AjaxResponseBuilder.fail().addMessage(message);
		}

		admissionService.destroyAdmissionForCompanyIdAndVenue(user.getCompanyId(), betaFeature);
		String message = getToggleSuccessMessage("off");
		return AjaxResponseBuilder.success().addMessage(message);
	}

	private String getToggleSuccessMessage(String toggleState) {
		return messageBundleHelper.getMessage("betafeature.toggle.success", toggleState);
	}

	private String getAdminErrorMessage(String toggleState) {
		return messageBundleHelper.getMessage("betafeature.toggle.failure.admin", toggleState);
	}
}
