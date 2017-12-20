package com.workmarket.web.controllers.requirementsets;

import com.workmarket.domains.model.requirementset.RequirementType;
import com.workmarket.service.business.requirementsets.RequirementTypesService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/requirement_types")
public class RequirementTypesController extends BaseController {
	@Autowired RequirementTypesService service;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public List<RequirementType> list() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		return this.service.findAll();
	}
}
