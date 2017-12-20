package com.workmarket.web.controllers.admin;

import com.workmarket.web.controllers.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin/bullhorn")
public class AdminBullhornController extends BaseController {

	@RequestMapping(
		value={"", "/"},
		method= RequestMethod.GET)
	public String index() throws Exception {

		return "web/pages/admin/bullhorn/index";
	}
}
