package com.workmarket.web.controllers.admin;

import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.service.infra.business.FeatureToggleService;
import com.workmarket.domains.authentication.features.FeatureEvaluatorConfiguration;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/admin/features")
public class FeatureToggleController extends BaseController {
	@Autowired FeatureToggleService featureToggleService;
	@Autowired FeatureEvaluatorConfiguration featureEvaluatorConfiguration;

	private static final String ADD_FEATURE = "/add_feature";
	private static final String ADD_SEGMENT = "/add_segment";
	private static final String UPDATE_FEATURE = "/update_feature";
	private static final String REMOVE_FEATURE = "/remove_feature";
	private static final String REMOVE_SEGMENT = "/remove_segment";
	private static final String REMOVE_REFERENCE = "/remove_reference";
	private static final String GET_FEATURE = "/get_feature";
	private static final String GET_ALL_FEATURES = "/";
	private static final String RELOAD = "/reload";

	private static final String FEATURE_TOGGLE_PAGE = "/feature_toggles";

	@RequestMapping(
		value = FEATURE_TOGGLE_PAGE,
		method = GET)
	public String index() throws Exception {
		return "web/pages/admin/featuretoggle/index";
	}

	@RequestMapping(
		value = ADD_FEATURE,
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody AjaxResponseBuilder addFeature(
		@RequestParam(value = "feature_name", required = true) String featureName,
		@RequestParam(value = "is_allowed", required = true) boolean isAllowed,
		@RequestParam(value = "segment_name", required = false) String segmentName,
		@RequestParam(value = "reference_value", required = false) String referenceValue) {

		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.addFeature(featureName, isAllowed, segmentName, referenceValue);

		return finish(ADD_FEATURE, serviceResponseBuilder);
	}

	@RequestMapping(
		value = ADD_SEGMENT,
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody AjaxResponseBuilder addSegment(
		@RequestParam(value = "feature_name", required = true) String featureName,
		@RequestParam(value = "segment_name", required = true) String segmentName,
		@RequestParam(value = "reference_value", required = true) String referenceValue) {

		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.addSegment(featureName, segmentName, referenceValue);

		return finish(ADD_FEATURE, serviceResponseBuilder);
	}

	@RequestMapping(
		value = UPDATE_FEATURE,
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody AjaxResponseBuilder updateFeature(
		@RequestParam(value = "feature_name", required = true) String featureName,
		@RequestParam(value = "is_allowed", required = true) boolean isAllowed) {

		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.updateFeature(featureName, isAllowed);

		return finish(UPDATE_FEATURE, serviceResponseBuilder);
	}

	@RequestMapping(
		value = REMOVE_FEATURE,
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody AjaxResponseBuilder removeFeature(
		@RequestParam(value = "feature_name", required = true) String featureName) {

		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.removeFeature(featureName);

		return finish(REMOVE_FEATURE, serviceResponseBuilder);
	}

	@RequestMapping(
		value = REMOVE_SEGMENT,
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody AjaxResponseBuilder removeSegment(
		@RequestParam(value = "feature_name", required = true) String featureName,
		@RequestParam(value = "segment_name", required = true) String segmentName) {

		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.removeSegment(featureName, segmentName);

		return finish(REMOVE_SEGMENT, serviceResponseBuilder);
	}

	@RequestMapping(
		value = REMOVE_REFERENCE,
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody AjaxResponseBuilder removeReference(
		@RequestParam(value = "feature_name", required = true) String featureName,
		@RequestParam(value = "segment_name", required = true) String segmentName,
		@RequestParam(value = "reference_value", required = true) String referenceValue) {

		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.removeReferenceValue(featureName, segmentName, referenceValue);

		return finish(REMOVE_REFERENCE, serviceResponseBuilder);
	}

	@RequestMapping(
		value = GET_FEATURE,
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	public @ResponseBody AjaxResponseBuilder getAllFeature(
		@RequestParam(value = "feature_name", required = true) String featureName) {

		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.getFeature(featureName);

		return finish(GET_FEATURE, serviceResponseBuilder);
	}

	@RequestMapping(
		value = GET_ALL_FEATURES,
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	public @ResponseBody AjaxResponseBuilder getAllFeatures() {

		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.getAllFeatures();

		return finish(GET_ALL_FEATURES, serviceResponseBuilder);
	}

	@RequestMapping(
		value = RELOAD,
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	public @ResponseBody AjaxResponseBuilder reload() throws IOException {
		featureEvaluatorConfiguration.reload();
		return getAllFeatures();
	}

	private AjaxResponseBuilder finish(String action, ServiceResponseBuilder serviceResponseBuilder) {
		AjaxResponseBuilder ajaxResponseBuilder = new AjaxResponseBuilder();
		ajaxResponseBuilder.setSuccessful(false);

		if (!serviceResponseBuilder.isSuccessful()) {
			return ajaxResponseBuilder.addMessage(action + " failed.");
		}

		return ajaxResponseBuilder
			.setData(serviceResponseBuilder.getData())
			.addMessage(action + " succeeded.")
			.setSuccessful(true);
	}
}
