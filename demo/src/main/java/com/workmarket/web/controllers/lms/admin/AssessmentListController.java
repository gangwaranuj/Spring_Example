package com.workmarket.web.controllers.lms.admin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentPagination;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/lms/manage")
public class AssessmentListController extends BaseController {

	@Autowired private AssessmentService assessmentService;

	@RequestMapping(
		value = "index.json",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE)
	public void indexList(HttpServletRequest httpRequest, Model model) throws Exception {
		assessmentList(httpRequest, model, AbstractAssessment.GRADED_ASSESSMENT_TYPE);
	}

	@RequestMapping(
		value = "/tests.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void listTests(Model model) {
		AssessmentPagination pagination = new AssessmentPagination(true);
		pagination.addFilter(AssessmentPagination.FILTER_KEYS.TYPE, AbstractAssessment.GRADED_ASSESSMENT_TYPE);
		pagination.addFilter(AssessmentPagination.FILTER_KEYS.NOT_REMOVED, true);
		pagination.addFilter(AssessmentPagination.FILTER_KEYS.STATUS, AssessmentStatusType.ACTIVE);
		pagination = assessmentService.findAssessmentsByCompany(getCurrentUser().getCompanyId(), pagination);
		List<AssessmentJsonResponse> tests = new ArrayList<>();
		for (AbstractAssessment a : pagination.getResults()){
			tests.add(new AssessmentJsonResponse(a.getId().toString(),a.getName()));
		}
		model.addAttribute("response", tests);
	}

	@RequestMapping(
		value = "/surveys",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String surveys() {
		return "web/pages/lms/manage/surveys";
	}

	@RequestMapping(
		value = "/surveys.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void surveysList(HttpServletRequest httpRequest, Model model) throws Exception {
		assessmentList(httpRequest, model, AbstractAssessment.SURVEY_ASSESSMENT_TYPE);
	}

	private void assessmentList(HttpServletRequest httpRequest, Model model, String assessmentType) throws InstantiationException, IllegalAccessException {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
			0, AssessmentPagination.SORTS.NAME.toString(),
			1, AssessmentPagination.SORTS.CREATED_BY.toString(),
			2, AssessmentPagination.SORTS.CREATED_ON.toString(),
			3, AssessmentPagination.SORTS.STATUS.toString()
		));

		AssessmentPagination pagination = request.newPagination(AssessmentPagination.class);
		pagination.addFilter(AssessmentPagination.FILTER_KEYS.TYPE, assessmentType);
		pagination.addFilter(AssessmentPagination.FILTER_KEYS.NOT_REMOVED, true);
		pagination = assessmentService.findAssessmentsByCompany(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (AbstractAssessment a : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				a.getName(),
				a.getUser().getFullName(),
				DateUtilities.format("MMM d, yyyy", a.getCreatedOn(), getCurrentUser().getTimeZoneId()),
				StringUtils.capitalize(a.getAssessmentStatusType().getCode()),
				null
			);

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"id", a.getId(),
				"user_number", a.getUser().getUserNumber(),
				"status", a.getAssessmentStatusType().getCode(),
				"creator", a.getCreatorId()
			);
			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/list_view",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String list_view() {
		return "web/pages/lms/manage/list_view";
	}
}

class AssessmentJsonResponse {
	private String id;
	private String name;

	AssessmentJsonResponse(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
