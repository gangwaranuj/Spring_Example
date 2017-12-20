package com.workmarket.web.controllers.requirementsets;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.service.business.CompanySerializationService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by ianha on 1/5/14
 */
@Controller
@RequestMapping("/company_works")
public class CompanyWorksController extends BaseController {
	@Autowired private CompanySerializationService jsonService;
	@Autowired private CompanyService companyService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String list() {
		List<Company> results = Lists.newArrayList();
		results.add(companyService.findCompanyById(getCurrentUser().getCompanyId()));
		return jsonService.toJson(results);
	}
}
