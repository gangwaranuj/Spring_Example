package com.workmarket.web.controllers.workerservices;

import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.controllers.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/workerservices/**")
@VelvetRope(
	venue = Venue.HIDE_WORKER_SERVICES,
	bypass = true,
	redirectPath = "/home",
	message = "You do not have access to this feature."
)
public class WorkerServicesController extends BaseController {
	@RequestMapping(method=RequestMethod.GET)
	public String index() {
		return "web/pages/workerservices/index";
	}
}
