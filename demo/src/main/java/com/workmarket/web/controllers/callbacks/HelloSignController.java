package com.workmarket.web.controllers.callbacks;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.biz.esignature.EsignatureClient;
import com.workmarket.biz.esignature.EsignatureClientHelper;
import com.workmarket.biz.esignature.gen.Messages.UpdateRequestResp;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.web.controllers.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

@Controller
@RequestMapping("/callbacks/hellosign")
public class HelloSignController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(HelloSignController.class);

	@Autowired private EsignatureClient esignatureClient;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	private Meter update;

	@PostConstruct
	public void init() {
		final MetricRegistryFacade metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "hellosign_controller");
		this.update = metricRegistryFacade.meter("update");
	}

	@RequestMapping(
			value = "/",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public String callback(@ModelAttribute("json") String event) throws Exception {
		update.mark();
		//hellosign expects the success message or else it will retry later if null, error, etc.
		return StringUtils.join(esignatureClient.updateSignatureRequest(
				EsignatureClientHelper.hellosignEventToInternalEvent(event),
				webRequestContextProvider.getRequestContext())
			.toBlocking()
			.singleOrDefault(UpdateRequestResp.getDefaultInstance())
			.getStatus()
			.getMessageList()
			.toArray());
	}
}
