package com.workmarket.web.controllers.requirementsets;

import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.service.business.AssessmentSerializationService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/tests")
public class TestsController extends BaseController {
	@Autowired AssessmentService service;
	@Autowired private AssessmentSerializationService jsonService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String list() {
		List<AbstractAssessment> tests = service.findAllTestsByCompanyId(getCurrentUser().getCompanyId());
		return jsonService.toJson(tests);
	}
}
