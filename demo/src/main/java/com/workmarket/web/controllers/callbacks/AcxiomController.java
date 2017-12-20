
package com.workmarket.web.controllers.callbacks;

import com.workmarket.service.business.ScreeningService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.workmarket.web.controllers.BaseController;

@Controller
@RequestMapping("/callbacks/acxiom")
public class AcxiomController extends BaseController {

	@Autowired private ScreeningService screeningService;
	@Autowired private AuthenticationService authn;
	private static final Logger logger = LoggerFactory.getLogger(AcxiomController.class);

	@RequestMapping(value="/results", method=RequestMethod.POST)
	public @ResponseBody String results(@RequestBody String payload) throws Exception {
		if (StringUtils.isBlank(payload))
			return "INVALID";

		authn.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		Boolean response = screeningService.handleSterlingScreeningResults(payload);
		logger.debug("acxiom payload=" + payload + "; response=" + response);
		return BooleanUtils.toString(response, "SUCCESS", "ERROR");
	}
}
