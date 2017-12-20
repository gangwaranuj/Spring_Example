package com.workmarket.web.controllers.requirementsets;

import com.workmarket.service.business.CompanyTypeSerializerService;
import com.workmarket.service.business.CompanyTypeService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/company_types")
public class CompanyTypesController extends BaseController {
	@Autowired private CompanyTypeService service;
	@Autowired private CompanyTypeSerializerService jsonService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String list() {
		return jsonService.toJson(service.findAll());
	}
}
