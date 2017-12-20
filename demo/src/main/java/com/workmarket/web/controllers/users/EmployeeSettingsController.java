package com.workmarket.web.controllers.users;

import com.workmarket.service.business.CompanyService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/employee_settings")
public class EmployeeSettingsController extends BaseController {
	@Autowired CompanyService companyService;

	@ResponseBody
	@RequestMapping(method = GET)
	public EmployeeSettingsDTO get() {
		return EmployeeSettingsDTO.newInstance(companyService.findById(getCurrentUser().getCompanyId()));
	}

	@ResponseBody
	@RequestMapping(method = POST)
	public void set(@RequestBody EmployeeSettingsDTO dto) {
		companyService.saveEmployeeSettings(getCurrentUser().getCompanyId(), dto);
	}
}
