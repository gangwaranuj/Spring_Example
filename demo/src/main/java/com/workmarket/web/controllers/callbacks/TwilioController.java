
package com.workmarket.web.controllers.callbacks;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.dto.VoiceResponseDTO;
import com.workmarket.service.exception.IllegalWorkAccessException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.VoiceService;
import com.workmarket.web.controllers.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.naming.OperationNotSupportedException;

/**
 * Handle Twilio callbacks
 * For documentation on parameters we are expecting:
 *
 * @see http://www.twilio.com/docs/api/2010-04-01/twiml/twilio_request
 */
@Controller
@RequestMapping("/callbacks/twilio")
public class TwilioController extends BaseController {

	@Autowired private AuthenticationService authn;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private VoiceService voiceService;

	private Meter respond;

	@PostConstruct
	public void init() {
		final MetricRegistryFacade metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "twilio_controller");
		this.respond = metricRegistryFacade.meter("respond");
	}

	@RequestMapping(value = "/respond", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String respond(
		@RequestParam("From") String from,
		@RequestParam("To") String to,
		@RequestParam("CallSid") String callSid,
		@RequestParam("CallStatus") String callStatus,
		@RequestParam(value = "CallDuration", required = false) String callDuration,
		@RequestParam(value = "redirect", required = false) String redirect,
		@RequestParam(value = "AnsweredBy", required = false) String answeredBy,
		@RequestParam(value = "Digits", required = false) String digits) throws OperationNotSupportedException, IllegalWorkAccessException {

		respond.mark();
		VoiceResponseDTO dto = new VoiceResponseDTO();
		dto.setMsg(digits);
		dto.setFromNumber(from);
		dto.setToNumber(to);
		dto.setCallId(callSid);
		dto.setCallStatus(callStatus);
		dto.setCallDuration(callDuration);

		if (StringUtils.isNotBlank(redirect))
			dto.setRedirectToSubStatus(redirect);
		if (StringUtils.equals(answeredBy, "machine"))
			dto.setRedirectToSubStatus(answeredBy);

		authn.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		return voiceService.respond(dto);
	}
}
