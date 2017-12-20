package com.workmarket.web.controllers;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.web.forms.MetricsForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/metrics")
public class MetricsController extends BaseController {

	private static final String EMPTY_METRICS_REQUEST_MESSAGE = "metrics.request.empty";
	private static final String METRICS_SUBSYSTEM_NAME = "frontend";

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private MetricRegistry metricRegistry;

	private WMMetricRegistryFacade wmMetricRegistryFacade;

	@PostConstruct
	private void initializeFacade() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, METRICS_SUBSYSTEM_NAME);
	}

	@RequestMapping(
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody ApiV2Response track(
		final @RequestBody MetricsForm[] metrics,
		final HttpServletResponse httpResponse) {

		if (metrics == null) {
			httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return ApiV2Response.valueWithMessage(messageHelper.getMessage(EMPTY_METRICS_REQUEST_MESSAGE), HttpStatus.BAD_REQUEST);
		}

		for (MetricsForm metric : metrics) {
			if (metric.getValue() != null && metric.isMeter()) {
				wmMetricRegistryFacade.meter(metric.getValue()).mark(metric.getCount());
			}
		}

		return new ApiV2Response();
	}
}
