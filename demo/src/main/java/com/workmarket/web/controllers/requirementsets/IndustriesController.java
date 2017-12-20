package com.workmarket.web.controllers.requirementsets;

import com.workmarket.service.business.IndustrySerializationService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/industries-list")
public class IndustriesController extends BaseController {

	@Autowired private IndustryService industryService;
	@Autowired private IndustrySerializationService jsonService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String list() {
		List<IndustryDTO> industries = industryService.getAllIndustryDTOs();
		return jsonService.toJson(industries);
	}
}
