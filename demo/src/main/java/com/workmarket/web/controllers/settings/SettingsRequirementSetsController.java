package com.workmarket.web.controllers.settings;

import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.requirementsets.RequirementSetsSerializationService;
import com.workmarket.service.business.requirementsets.RequirementSetsService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings/manage/requirement_sets")
public class SettingsRequirementSetsController extends BaseController {
	@Autowired private RequirementSetsService service;
	@Autowired private RequirementSetsSerializationService jsonService;
	@Autowired private UserService userService;

	@RequestMapping(value = {"/",""}, method = GET)
	public String index(Model model) {
		// Just load the base html page
		return "web/pages/settings/manage/requirementsets/index";
	}

	@RequestMapping(
		value = "/list",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	public @ResponseBody String list() {
		List<RequirementSet> requirementSets = service.findAll();

		Map<Long, Map<String, Object>> propMap = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(
			requirementSets, "creatorId"), "firstName", "lastName");

		for (RequirementSet rs : requirementSets) {
			rs.setCreatorFullName(propMap.get(rs.getCreatorId()).get("firstName") + " " + propMap.get(rs.getCreatorId()).get("lastName"));
		}

		return jsonService.toJson(requirementSets);
	}

	@RequestMapping(
		value = "/list",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	public @ResponseBody String create(@RequestParam("model") String model) {
		String requirementSetData = StringEscapeUtils.unescapeHtml4(model);
		RequirementSet requirementSet = jsonService.fromJson(requirementSetData);
		service.save(requirementSet);
		return jsonService.toJson(requirementSet);
	}

	@RequestMapping(
		value = "/list/{id}",
		method = POST,
		headers = "X-HTTP-Method-Override=PUT"
	)
	public String update(@PathVariable("id") Long id, @RequestParam("model") String model) {
		RequirementSet requirementSet = service.find(id);
		String requirementSetData = StringEscapeUtils.unescapeHtml4(model);
		RequirementSet updatedRequirementSet = jsonService.mergeJson(requirementSet, requirementSetData);
		service.update(updatedRequirementSet);
		return jsonService.toJson(updatedRequirementSet);
	}

	@RequestMapping(
		value = "/list/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE,
		headers = "X-HTTP-Method-Override=DELETE"
	)
	public void destroy(@PathVariable("id") Long id) {
		service.destroy(id);
	}
}
