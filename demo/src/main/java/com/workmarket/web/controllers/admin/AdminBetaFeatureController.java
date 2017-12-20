package com.workmarket.web.controllers.admin;

import com.workmarket.domains.velvetrope.model.BetaFeatureAdmissionRequest;
import com.workmarket.domains.velvetrope.service.BetaFeatureService;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/admin/beta_features")
public class AdminBetaFeatureController extends BaseController {

	@Resource private BetaFeatureService betaFeatureService;

	private static final String GET_ALL_BETA_FEATURES = "/";
	private static final String BETA_FEATURES_PAGE = "/beta_features";
	private static final String UPDATE_FEATURE_PARTICIPATION = "/update_participation";

	@RequestMapping(
		value = BETA_FEATURES_PAGE,
		method = GET)
	public String indexPage() throws Exception {
		return "web/pages/admin/betafeatures/index";
	}

	@RequestMapping(
		value = GET_ALL_BETA_FEATURES,
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	public @ResponseBody AjaxResponseBuilder getAllBetaFeatures() {
		ServiceResponseBuilder serviceResponseBuilder = betaFeatureService.getBetaFeaturesResponseBuilder();
		return finish(GET_ALL_BETA_FEATURES, serviceResponseBuilder);
	}

	@RequestMapping(
		value = UPDATE_FEATURE_PARTICIPATION,
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody AjaxResponseBuilder updateFeatureParticipation(
		@RequestBody @Valid BetaFeatureAdmissionRequest betaFeatureAdmissionRequest) {

		betaFeatureService.updateFeatureParticipation(betaFeatureAdmissionRequest);

		ServiceResponseBuilder serviceResponseBuilder = new ServiceResponseBuilder();
		serviceResponseBuilder.setSuccessful(true);

		return finish("Update to beta feature participants", serviceResponseBuilder);
	}

	private AjaxResponseBuilder finish(String action, ServiceResponseBuilder serviceResponseBuilder) {
		AjaxResponseBuilder ajaxResponseBuilder = new AjaxResponseBuilder();

		if (serviceResponseBuilder.failed()) {
			ajaxResponseBuilder.setSuccessful(false);
			return ajaxResponseBuilder.addMessage(action + " failed.");
		}

		return ajaxResponseBuilder
			.setData(serviceResponseBuilder.getData())
			.addMessage(action + " succeeded.")
			.setSuccessful(true);
	}
}
