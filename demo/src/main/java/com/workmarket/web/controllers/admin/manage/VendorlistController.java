
package com.workmarket.web.controllers.admin.manage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.workmarket.web.controllers.BaseController;

@Controller
@RequestMapping("/admin/manage/vendorlist")
public class VendorlistController extends BaseController {

	@RequestMapping(value={"", "/", "/index"}, method=RequestMethod.GET)
	public String index() {

		return "web/pages/admin/manage/vendorlist";
	}

}
