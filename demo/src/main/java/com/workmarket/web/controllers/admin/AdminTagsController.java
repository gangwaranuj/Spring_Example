
package com.workmarket.web.controllers.admin;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.SkillPagination;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.model.specialty.SpecialtyPagination;
import com.workmarket.domains.model.tool.Tool;
import com.workmarket.domains.model.tool.ToolPagination;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.SpecialtyService;
import com.workmarket.service.business.ToolService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/tags")
public class AdminTagsController extends BaseController {
	public static final String TAG_TOOL = "tool";
	public static final String TAG_SKILL = "skill";
	public static final String TAG_SPECIALTY = "specialty";

	@Autowired private ToolService toolService;
	@Autowired private SkillService skillService;
	@Autowired private SpecialtyService specialtyService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private UserService userService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private IndustryService industryService;

	@RequestMapping(value="/review_tools", method=RequestMethod.GET)
	public String reviewTools(Model model) {
		model.addAttribute("industries", industryService.getAllIndustryDTOs());
		model.addAttribute("current_type", "tools");

		return "web/pages/admin/tags/review_tools";
	}

	@RequestMapping(value="/review_specialties", method=RequestMethod.GET)
	public String reviewSpecialties(Model model) {
		model.addAttribute("industries", industryService.getAllIndustryDTOs());

		model.addAttribute("current_type", "products");

		return "web/pages/admin/tags/review_specialties";
	}

	@RequestMapping(value="/review_skills", method=RequestMethod.GET)
	public String reviewSkills(Model model) {
		model.addAttribute("industries", industryService.getAllIndustryDTOs());

		model.addAttribute("current_type", "skills");

		return "web/pages/admin/tags/review_skills";
	}

	@RequestMapping(value="/list_tags/{tagType}", method=RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
	public void listTags(Model model, HttpServletRequest httpRequest, @PathVariable String tagType) {
		DataTablesResponse<List<String>, Map<String, Object>> response;

		if (TAG_TOOL.equals(tagType)) {
			response = listTools(httpRequest);
		} else if (TAG_SKILL.equals(tagType)) {
			response = listSkills(httpRequest);
		} else if (TAG_SPECIALTY.equals(tagType)) {
			response = listSpecialties(httpRequest);
		} else {
			throw new HttpException400();
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/decline_tag/{tagType}/{tagId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	AjaxResponseBuilder declineTag(
			@PathVariable String tagType,
			@PathVariable Long tagId) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (TAG_TOOL.equals(tagType)) {
			toolService.declineTool(tagId);
		} else if (TAG_SKILL.equals(tagType)) {
			skillService.declineSkill(tagId);
		} else if (TAG_SPECIALTY.equals(tagType)) {
			specialtyService.declineSpecialty(tagId);
		} else {
			throw new HttpException400();
		}

		return response.setSuccessful(true);
	}

	@RequestMapping(value = "/approve_tag/{tagType}/{tagId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	AjaxResponseBuilder approveTag(
			@PathVariable String tagType,
			@PathVariable Long tagId) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (TAG_TOOL.equals(tagType)) {
			toolService.approveTool(tagId);
		} else if (TAG_SKILL.equals(tagType)) {
			skillService.approveSkill(tagId);
		} else if (TAG_SPECIALTY.equals(tagType)) {
			specialtyService.approveSpecialty(tagId);
		} else {
			throw new HttpException400();
		}

		return response.setSuccessful(true);
	}

	@RequestMapping(value = "/save_tag/{tagType}/{tagId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder renameTag(
			@PathVariable String tagType,
			@PathVariable Long tagId,
			@RequestParam String name,
			@RequestParam Long industryId) {
		Assert.isTrue(StringUtils.isNotBlank(name));
		Assert.notNull(industryId);

		Industry industry = invariantDataService.findIndustry(industryId);
		Assert.notNull(industry);

		if (TAG_TOOL.equals(tagType)) {
			Tool tool = toolService.findToolById(tagId);
			Assert.notNull(tool);

			if (toolService.findToolByNameAndIndustryId(name, industryId) != null) {
				return AjaxResponseBuilder
						.fail()
						.addMessage(messageHelper.getMessage("admin.tags.modify.exists", tagType, name, industry.getName()));
			}
			tool.setName(name);
			tool.setIndustry(industry);
			toolService.saveOrUpdateTool(tool);

		} else if (TAG_SKILL.equals(tagType)) {
			Skill skill = skillService.findSkillById(tagId);
			Assert.notNull(skill);

			if (skillService.findSkillByNameAndIndustry(name, industryId) != null) {
				return AjaxResponseBuilder
						.fail()
						.addMessage(messageHelper.getMessage("admin.tags.modify.exists", tagType, name, industry.getName()));
			}
			skill.setName(name);
			skill.setIndustry(industry);
			skillService.saveOrUpdateSkill(skill);

		} else if (TAG_SPECIALTY.equals(tagType)) {
			Specialty specialty = specialtyService.findSpecialtyById(tagId);
			Assert.notNull(specialty);

			if (specialtyService.findSpecialtyByNameAndIndustryId(name, industryId) != null) {
				return AjaxResponseBuilder
						.fail()
						.addMessage(messageHelper.getMessage("admin.tags.modify.exists", tagType, name, industry.getName()));
			}
			specialty.setName(name);
			specialty.setIndustry(industry);
			specialtyService.saveOrUpdateSpecialty(specialty);
		} else {
			throw new HttpException400();
		}

		return AjaxResponseBuilder.success();
	}

	@RequestMapping(value = "/merge_tag/{tagType}/{tagId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder mergeTag(
			@PathVariable String tagType,
			@PathVariable Long tagId,
			@RequestParam Long mergeIntoTagId) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (TAG_TOOL.equals(tagType)) {
			toolService.mergeTools(tagId, mergeIntoTagId);
		} else if (TAG_SKILL.equals(tagType)) {
			skillService.mergeSkills(tagId, mergeIntoTagId);
		} else if (TAG_SPECIALTY.equals(tagType)) {
			specialtyService.mergeSpecialties(tagId, mergeIntoTagId);
		} else {
			throw new HttpException400();
		}

		return response.setSuccessful(true);
	}

	// TODO: Refactor the following private methods... they all do the same thing
	private DataTablesResponse<List<String>, Map<String, Object>> listTools(HttpServletRequest httpRequest) {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, ToolPagination.SORTS.NAME.toString());
			put(1, ToolPagination.SORTS.INDUSTRY_NAME.toString());
			put(2, ToolPagination.SORTS.CREATOR_LAST_NAME.toString());
			put(3, ToolPagination.SORTS.CREATED_ON.toString());
		}});

		ToolPagination pagination = new ToolPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		if (!httpRequest.getParameter("sSearch").isEmpty()) {
			pagination.addFilter(ToolPagination.FILTER_KEYS.NAME.toString(), httpRequest.getParameter("sSearch"));
		}

		pagination.addFilter(ToolPagination.FILTER_KEYS.POPULARITY.toString(), String.valueOf(toolService.getToolPopularityThreshold()));

		pagination = toolService.findAllTools(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		List<Tool> tools = pagination.getResults();
		Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(tools, "creatorId"),
				"firstName", "lastName");

		for (Tool item : tools) {
			String createdOn = DateUtilities.format("MM/dd/yyyy h:mm a", item.getCreatedOn(), getCurrentUser().getTimeZoneId());

			List<String> row = Lists.newArrayList(
					item.getName(),
					item.getIndustry().getName(),
					StringUtilities.fullName((String) creatorProps.get(item.getCreatorId()).get("firstName"), (String) creatorProps.get(item.getCreatorId()).get("lastName")),
					createdOn
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
					"id", item.getId(),
					"approved", ApprovalStatus.APPROVED.equals(item.getApprovalStatus())
			);

			response.addRow(row, meta);
		}
		return response;
	}

	private DataTablesResponse<List<String>, Map<String, Object>> listSkills(HttpServletRequest httpRequest) {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, SkillPagination.SORTS.NAME.toString());
			put(1, SkillPagination.SORTS.INDUSTRY_NAME.toString());
			put(2, SkillPagination.SORTS.CREATOR_LAST_NAME.toString());
			put(3, SkillPagination.SORTS.CREATED_ON.toString());
		}});

		SkillPagination pagination = new SkillPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		if (!httpRequest.getParameter("sSearch").isEmpty()) {
			pagination.addFilter(SkillPagination.FILTER_KEYS.NAME.toString(), httpRequest.getParameter("sSearch"));
		}

		pagination.addFilter(SkillPagination.FILTER_KEYS.POPULARITY.toString(), String.valueOf(skillService.getSkillPopularityThreshold()));

		pagination = skillService.findAllSkills(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		List<Skill> skills = pagination.getResults();
		Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(skills, "creatorId"),
				"firstName", "lastName");

		for (Skill item : skills) {
			String createdOn = DateUtilities.format("MM/dd/yyyy h:mm a", item.getCreatedOn(), getCurrentUser().getTimeZoneId());

			List<String> row = Lists.newArrayList(
					item.getName(),
					item.getIndustry().getName(),
					creatorProps.get(item.getCreatorId()).get("firstName") + " " + creatorProps.get(item.getCreatorId()).get("lastName"),
					createdOn
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
					"id", item.getId(),
					"approved", ApprovalStatus.APPROVED.equals(item.getApprovalStatus())
			);

			response.addRow(row, meta);
		}
		return response;
	}

	private DataTablesResponse<List<String>, Map<String, Object>> listSpecialties(HttpServletRequest httpRequest) {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, SpecialtyPagination.SORTS.NAME.toString());
			put(1, SpecialtyPagination.SORTS.INDUSTRY_NAME.toString());
			put(2, SpecialtyPagination.SORTS.CREATOR_LAST_NAME.toString());
			put(3, SpecialtyPagination.SORTS.CREATED_ON.toString());
		}});

		SpecialtyPagination pagination = new SpecialtyPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		if (!httpRequest.getParameter("sSearch").isEmpty()) {
			pagination.addFilter(SpecialtyPagination.FILTER_KEYS.NAME.toString(), httpRequest.getParameter("sSearch"));
		}

		pagination.addFilter(SpecialtyPagination.FILTER_KEYS.POPULARITY.toString(), String.valueOf(specialtyService.getSpecialityPopularityThreshold()));

		pagination = specialtyService.findAllSpecialties(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		List<Specialty> specialties = pagination.getResults();
		Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(specialties, "creatorId"),
				"firstName", "lastName");

		for (Specialty item : pagination.getResults()) {
			String createdOn = DateUtilities.format("MM/dd/yyyy h:mm a", item.getCreatedOn(), getCurrentUser().getTimeZoneId());

			List<String> row = Lists.newArrayList(
					item.getName(),
					item.getIndustry().getName(),
					StringUtilities.fullName((String) creatorProps.get(item.getCreatorId()).get("firstName"), (String) creatorProps.get(item.getCreatorId()).get("lastName")),
					createdOn
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
					"id", item.getId(),
					"approved", ApprovalStatus.APPROVED.equals(item.getApprovalStatus())
			);

			response.addRow(row, meta);
		}
		return response;
	}
}
