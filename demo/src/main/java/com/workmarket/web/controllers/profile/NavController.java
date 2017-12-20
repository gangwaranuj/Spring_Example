package com.workmarket.web.controllers.profile;

import com.workmarket.service.business.UserNavService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/nav")
public class NavController extends BaseController {

	@Autowired private UserNavService userNavService;

	@RequestMapping(value = "/get_preferences", method = GET)
	public @ResponseBody Map<String, String> getPreferences() {
		return userNavService.get(getCurrentUser().getId());
	}

	@RequestMapping(value = "/set_preferences", method = POST)
	@ResponseStatus(value = OK)
	public void getPreferences(@RequestParam Map<String, String> preferences) {
		userNavService.set(getCurrentUser().getId(), preferences);
	}
}
