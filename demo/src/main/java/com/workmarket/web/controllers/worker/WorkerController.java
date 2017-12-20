package com.workmarket.web.controllers.worker;

import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.validators.FeedRequestParamsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@RequestMapping("/worker/**")
public class WorkerController extends BaseController {

	@Autowired private JsonSerializationService jsonSerializationService;

	@RequestMapping(method = GET)
	public String index() {
		return "redirect:/worker/browse";
	}

	@RequestMapping(value = "/browse", method = GET)
	@VelvetRope(
		venue = Venue.HIDE_WORKFEED,
		bypass = true,
		redirectPath = "/home",
		message = "You do not have access to this feature."
	)
	public String browse(Model model) {

		Boolean isWorker = getCurrentUser().isSeller();

		if (!isWorker) {
			return "redirect:/error/no_access";
		}

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "worker",
			"features", CollectionUtilities.newObjectMap(
				"limit", "25",
				"distance", getCurrentUser().getMaxTravelDistance(),
				"postalCode", getCurrentUser().getPostalCode(),
				"constants", FeedRequestParamsValidator.VALIDATION_CONSTANTS
			)
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/worker/browse/index";
	}
}
