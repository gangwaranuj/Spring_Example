package com.workmarket.web.controllers.requirementsets;

import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.service.business.requirementsets.AbstractRequirementsService;
import com.workmarket.service.business.requirementsets.RequirementSetsSerializationService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class RequirementsController extends BaseController {
	@Autowired private AbstractRequirementsService service;
	@Autowired private RequirementSetsSerializationService jsonService;

	@RequestMapping(
		value = {"requirement_sets/{requirementSetId}/requirements"},
		method = RequestMethod.GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public @ResponseBody String listRequirementsFor(
		@PathVariable("requirementSetId") Long requirementSetId
	) {
		List<AbstractRequirement> requirements = service.findAllByRequirementSetId(requirementSetId);
		return jsonService.toJson(requirements);
	}
}
