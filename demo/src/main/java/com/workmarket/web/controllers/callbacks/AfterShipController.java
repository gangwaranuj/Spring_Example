package com.workmarket.web.controllers.callbacks;

import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.external.AfterShipEvent;
import com.workmarket.service.external.TrackingNumberAdapter;
import com.workmarket.web.forms.webhooks.AfterShipWebhookForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/callbacks/aftership/{key}")
public class AfterShipController {

	private static final Log LOGGER = LogFactory.getLog(AfterShipController.class);

	@Autowired private PartService partService;
	@Autowired private TrackingNumberAdapter trackingNumberAdapter;

	@Value("${aftership.webhook.token}")
	private String AFTERSHIP_TOKEN;

	@RequestMapping(method = POST)
	public @ResponseBody ResponseEntity<String> updateTrackingNumberStatus(
		final @RequestBody AfterShipWebhookForm form,
		final @PathVariable String key) {

		LOGGER.debug("[AfterShipController] - Webhook request: " + form.toString());

		if (AfterShipEvent.TRACKING_UPDATE.getCode().equals(form.getEvent()) && AFTERSHIP_TOKEN.equals(key)) {
			final AfterShipWebhookForm.Msg payload = form.getMsg();
			final PartDTO dto = new PartDTO(
				payload.getTracking_number(),
				trackingNumberAdapter.translateAftershipTrackingStatus(payload.getTag()),
				ShippingProvider.getShippingProvider(payload.getSlug())
			);

			partService.updateTrackingStatus(dto);
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}
