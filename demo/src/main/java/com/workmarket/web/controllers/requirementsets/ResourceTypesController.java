package com.workmarket.web.controllers.requirementsets;

import com.workmarket.service.business.ResourceTypeSerializerService;
import com.workmarket.service.business.ResourceTypeService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/resource_types")
public class ResourceTypesController extends BaseController {
	@Autowired private ResourceTypeService service;
	@Autowired private ResourceTypeSerializerService jsonService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String list() {
		return jsonService.toJson(service.findAll());
	}
}
