package com.workmarket.web.controllers.callbacks;

import com.google.common.collect.ImmutableMap;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.external.MultipassTokenFactory;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;

@Controller
@RequestMapping("/callbacks/desk")
public class DeskDotComController extends BaseController {

	private final static String HOST = "workmarket.desk.com";
	private final static String ENDPOINT = "/customer/authentication/multipass/callback";

	@Resource(name="deskMultipassTokenFactory") private MultipassTokenFactory multipassFactory;
	@Autowired private JsonSerializationService jsonService;

	@RequestMapping
	public String auth() throws Exception {

		String payload = jsonService.toJson(ImmutableMap.builder()
			.put("uid", getCurrentUser().getUserNumber())
			.put("customer_email", getCurrentUser().getEmail())
			.put("customer_name", getCurrentUser().getFullName())
			.put("expires", DateUtilities.getISO8601(DateUtilities.addMinutes(DateUtilities.getCalendarNow(), 5)))
			.build());

		String encodedPayload = multipassFactory.encode(payload);
		String signature = multipassFactory.sign(encodedPayload);

		String redirect = UriComponentsBuilder.newInstance()
			.scheme("http")
			.host(HOST)
			.path(ENDPOINT)
			.queryParam("multipass", encodedPayload)
			.queryParam("signature", signature)
			.build()
			.toUriString();

		return String.format("redirect:%s", redirect);
	}
}
