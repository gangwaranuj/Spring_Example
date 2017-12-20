package com.workmarket.web.controllers.compliancerulesets;

import com.workmarket.domains.compliance.model.ComplianceRuleType;
import com.workmarket.domains.compliance.service.ComplianceRuleTypeService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@RequestMapping("/compliance_rule_types")
public class ComplianceRuleTypesController extends BaseController {
	@Autowired ComplianceRuleTypeService service;

	@ResponseBody
	@RequestMapping(method = GET)
	public List<ComplianceRuleType> list() throws InstantiationException, IllegalAccessException, NoSuchFieldException {

		return service.findAll();
	}
}
