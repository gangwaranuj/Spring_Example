package com.workmarket.web.controllers.lms.admin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.report.assessment.AttemptReportPagination;
import com.workmarket.data.report.assessment.AttemptReportRow;
import com.workmarket.data.report.assessment.AttemptResponseAssetReportPagination;
import com.workmarket.data.report.assessment.AttemptResponseAssetReportRow;
import com.workmarket.data.report.assessment.AttemptResponseReportRow;
import com.workmarket.dto.AssessmentUser;
import com.workmarket.dto.AssessmentUserPagination;
import com.workmarket.service.business.AssetService;
import com.workmarket.service.business.asset.AssetBundlerQueue;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.AssessmentAttemptItemsNotGradedException;
import com.workmarket.thrift.assessment.AssessmentGradingRequest;
import com.workmarket.thrift.assessment.AssessmentRequestException;
import com.workmarket.thrift.assessment.AssessmentResponse;
import com.workmarket.thrift.assessment.AssessmentType;
import com.workmarket.thrift.assessment.GradeAttemptRequest;
import com.workmarket.thrift.assessment.GradeResponsesRequest;
import com.workmarket.thrift.assessment.RequestContext;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.lms.AssetFilterForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.views.CSVView;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/lms/manage")
public class AssessmentGraderController extends BaseAssessmentAdminController {

	@Autowired private AssetBundlerQueue assetBundlerQueue;
	@Autowired private AssetService assetService;

	@RequestMapping(
		value = "/grade_response/{assessmentId}/{attemptId}",
		method = POST)
	@ResponseBody
	public AjaxResponseBuilder gradeResponse(
		@PathVariable("assessmentId") Long id,
		@PathVariable("attemptId") Long attemptId,
		@RequestParam("itemId") Long itemId,
		@RequestParam("correct") boolean isCorrect) throws AssessmentRequestException {

		getAssessment(id);
		GradeResponsesRequest gradeRequest = new GradeResponsesRequest()
			.setCurrentUserId(getCurrentUser().getId())
			.setAttemptId(attemptId)
			.setItemId(itemId)
			.setPassed(isCorrect);

		thriftAssessmentService.gradeResponses(gradeRequest);

		return new AjaxResponseBuilder().setSuccessful(true);
	}

	@RequestMapping(
		value = "/grade_attempt/{assessmentId}/{attemptId}/{userId}",
		method = GET)
	public String gradeAttempt(
		@PathVariable("assessmentId") Long id,
		@PathVariable("attemptId") Long attemptId,
		@PathVariable("userId") Long userId,
		MessageBundle messages,
		RedirectAttributes redirectAttributes) throws AssessmentRequestException {

		Assessment assessment = getAssessment(id);
		GradeAttemptRequest gradeRequest = new GradeAttemptRequest()
			.setCurrentUserId(getCurrentUser().getId())
			.setAssessmentId(assessment.getId())
			.setUserId(userId);

		redirectAttributes.addFlashAttribute("bundle", messages);

		try {
			thriftAssessmentService.gradeAttempt(gradeRequest);
			messageHelper.addSuccess(messages, "lms.admin.grade.success");
			return "redirect:/lms/view/details/{assessmentId}";
		} catch (AssessmentAttemptItemsNotGradedException e) {
			messageHelper.addError(messages, "lms.admin.grade.items_not_graded");
			return "redirect:/lms/grade/{assessmentId}/{attemptId}";
		}
	}

	@RequestMapping(
		value = "/download_attempt_assets/{assessmentId}/{attemptId}",
		method = GET)
	public String downloadAttemptAssets(
		@PathVariable("assessmentId") Long id,
		@PathVariable("attemptId") Long attemptId) {

		AssessmentGradingRequest gradingRequest = new AssessmentGradingRequest()
			.setUserId(getCurrentUser().getId())
			.setAssessmentId(id)
			.setAttemptId(attemptId);

		AssessmentResponse gradingResponse;
		try {
			gradingResponse = thriftAssessmentService.findAssessmentForGrading(gradingRequest);
		} catch (AssessmentRequestException e) {
			throw new HttpException404();
		}

		if (!CollectionUtilities.containsAny(gradingResponse.getRequestContexts(), RequestContext.OWNER, RequestContext.COMPANY_OWNED))
			throw new HttpException401();

		List<String> assetUuids = assetService.getAssessmentAttemptResponseAssetUuidsByAttempt(attemptId);
		assetBundlerQueue.bundleAssetsForUser(assetUuids, getCurrentUser().getId());

		return "web/pages/lms/manage/download_assets";
	}

	@RequestMapping(
		value = "/download_assessment_assets/{assessmentId}",
		method = GET)
	public String downloadAssessmentAssets(@PathVariable("assessmentId") Long id) {

		Assessment assessment = getAssessment(id);

		assetBundlerQueue.bundleAssetsForUser(assetService.getAssessmentAttemptResponseAssetUuidsByAssessment(assessment.getId()), getCurrentUser().getId());

		return "web/pages/lms/manage/download_assets";
	}

	@RequestMapping(
		value = "/download_assets",
		method = POST)
	public String downloadAssets(@RequestParam("uuids[]") List<String> uuids) {
		assetBundlerQueue.bundleAssetsForUser(uuids, getCurrentUser().getId());
		return "web/pages/lms/manage/download_assets";
	}

	@RequestMapping(
		value = "/assets/{assessmentId}",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String assets(
		@PathVariable("assessmentId") Long id,
		@ModelAttribute("filterForm") AssetFilterForm filters,
		Model model,
		HttpServletRequest httpServletRequest) throws Exception {
		return showAssets(id, null, model, httpServletRequest);
	}

	@RequestMapping(
		value = "/assets/{assessmentId}/{attemptId}",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String assetsForAttempt(
		@PathVariable("assessmentId") Long id,
		@PathVariable("attemptId") Long attemptId,
		@ModelAttribute("filterForm") AssetFilterForm filters,
		Model model,
		HttpServletRequest httpRequest) throws Exception {
		filters.setAttemptId(attemptId);
		return showAssets(id, attemptId, model, httpRequest);
	}

	private String showAssets(Long assessmentId, Long attemptId, Model model, HttpServletRequest request) throws Exception {

		Assessment assessment = getAssessment(assessmentId);

		model.addAttribute("assessment", assessment);
		model.addAttribute("clients", getDataHelper().getClients(getCurrentUser()));
		model.addAttribute("projects", getDataHelper().getProjects(getCurrentUser()));

		DataTablesRequest dataTablesRequest = DataTablesRequest.newInstance(request);
		AssessmentUserPagination pagination = dataTablesRequest.newPagination(AssessmentUserPagination.class);
		pagination = assessmentService.findLatestAssessmentUserAttempts(assessment.getId(), pagination);

		Map<String, String> resources = Maps.newHashMap();
		for (AssessmentUser resource : pagination.getResults()) {
			resources.put(resource.getUserNumber(), StringUtilities.fullName(resource.getFirstName(), resource.getLastName()));
		}

		model.addAttribute("resources", resources);

		if (attemptId != null) {
			AssessmentGradingRequest gradingRequest = new AssessmentGradingRequest()
				.setUserId(getCurrentUser().getId())
				.setAssessmentId(assessment.getId())
				.setAttemptId(attemptId);
			AssessmentResponse gradingResponse = thriftAssessmentService.findAssessmentForGrading(gradingRequest);

			model.addAttribute("attemptId", attemptId);
			model.addAttribute("work", gradingResponse.getRequestedAttempt().getWork());
		}

		return "web/pages/lms/manage/assets";
	}

	@RequestMapping(
		value = "/assets/{assessmentId}.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void assetsList(
		@PathVariable("assessmentId") Long id,
		AssetFilterForm filters,
		HttpServletRequest httpRequest,
		Model model) throws Exception {

		Assessment assessment = getAssessment(id);

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest, filters);
		request.setSortableColumnMapping(CollectionUtilities.<Integer, String>newTypedObjectMap(
			0, AttemptResponseAssetReportPagination.SORTS.CREATED_ON.toString(),
			1, AttemptResponseAssetReportPagination.SORTS.CREATOR_LAST_NAME.toString(),
			2, AttemptResponseAssetReportPagination.SORTS.CREATED_ON.toString()
		));
		request.setFilterMapping(CollectionUtilities.<String, Enum<?>>newTypedObjectMap(
			"client", AttemptResponseAssetReportPagination.FILTER_KEYS.WORK_CLIENT_ID,
			"project", AttemptResponseAssetReportPagination.FILTER_KEYS.WORK_PROJECT_ID,
			"createdOnFrom", AttemptResponseAssetReportPagination.FILTER_KEYS.CREATED_ON_FROM,
			"createdOnThrough", AttemptResponseAssetReportPagination.FILTER_KEYS.CREATED_ON_THROUGH,
			"userNumber", AttemptResponseAssetReportPagination.FILTER_KEYS.RESOURCE_USER_NUMBER
		));

		AttemptResponseAssetReportPagination pagination = request.newPagination(AttemptResponseAssetReportPagination.class);
		pagination.addFilter(AttemptResponseAssetReportPagination.FILTER_KEYS.ASSESSMENT_ID, assessment.getId());
		pagination = assessmentService.findAssessmentAttemptResponseAssets(pagination);

		Map<String, Integer> fileSizes = Maps.newHashMap();
		DataTablesResponse<List<String>, AttemptResponseAssetReportRow> response = DataTablesResponse.newInstance(request, pagination);

		for (AttemptResponseAssetReportRow row : pagination.getResults()) {
			fileSizes.put(row.getUuid(), row.getFileByteSize());

			List<String> data = Lists.newArrayListWithCapacity(7);
			Collections.fill(data, null);
			response.addRow(data, row);
		}

		response.setResponseMeta(CollectionUtilities.newObjectMap(
			"totalBytes", pagination.getTotalFileByteSize(),
			"fileSizes", fileSizes
		));

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/asset/{assessmentId}/{assetId}",
		method = GET)
	public String asset(
		@PathVariable("assessmentId") Long id,
		@PathVariable("assetId") Long assetId,
		Model model) {

		AttemptResponseAssetReportPagination pagination = new AttemptResponseAssetReportPagination();
		pagination.setStartRow(0);
		pagination.setResultsLimit(1);
		pagination.addFilter(AttemptResponseAssetReportPagination.FILTER_KEYS.ASSESSMENT_ID, id);
		pagination.addFilter(AttemptResponseAssetReportPagination.FILTER_KEYS.ASSET_ID, assetId);
		pagination = assessmentService.findAssessmentAttemptResponseAssets(pagination);

		if (pagination.getRowCount() != 1)
			throw new HttpException404();

		model.addAttribute("asset", pagination.getResults().get(0));

		return "web/pages/lms/manage/asset";
	}

	@RequestMapping(
		value = "/attempts/{assessmentId}",
		method = GET)
	public void attempts(
		@PathVariable("assessmentId") Long id,
		HttpServletRequest httpRequest,
		Model model) throws InstantiationException, IllegalAccessException {

		Assessment assessment = getAssessment(id);

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
			0, AssessmentUserPagination.SORTS.USER_LAST_NAME.toString(),
			1, AssessmentUserPagination.SORTS.ATTEMPT_STATUS.toString(),
			2, AssessmentUserPagination.SORTS.DATE_COMPLETED.toString()
		));
		request.setFilterMapping(CollectionUtilities.<String, Enum<?>>newTypedObjectMap(
			"filters[type]", AssessmentUserPagination.FILTER_KEYS.LANE_TYPE_ID,
			"filters[status]", AssessmentUserPagination.FILTER_KEYS.STATUS
		));

		AssessmentUserPagination pagination = request.newPagination(AssessmentUserPagination.class);
		pagination = assessmentService.findLatestAssessmentUserAttempts(assessment.getId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (AssessmentUser row : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				StringUtilities.fullName(row.getFirstName(), row.getLastName()),
				StringUtils.capitalize(row.getAttemptStatus()), // TODO decamelize
				(row.getCompletedOn() != null) ? DateUtilities.format("MM/dd/yyyy", row.getCompletedOn(), getCurrentUser().getTimeZoneId()) : "-"
			);

			if (assessment.getType().equals(AssessmentType.SURVEY)) {
				data.add(row.getWorkNumber());
				data.add(row.getWorkTitle());
			}

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"attempt_id", row.getAttemptId(),
				"user_id", row.getUserId(),
				"user_number", row.getUserNumber(),
				"company_name", row.getCompanyName(),
				"status", row.getAttemptStatus(),
				"passed", row.getPassedFlag(),
				"score", String.format("%.1f", row.getScore())
			);
			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/export/{assessmentId}",
		method = GET
	)
	public CSVView export(
		@PathVariable Long assessmentId,
		Model model) {

		Assessment assessment = getAssessment(assessmentId);

		AttemptReportPagination pagination = new AttemptReportPagination(true);
		pagination.setStartRow(0);
		pagination = assessmentService.generateReportForAssessment(assessment.getId(), pagination);

		List<String> headers = Lists.newArrayList(
			"User Number",
			"First Name",
			"Last Name",
			"Company",
			"Status",
			"Completed On"
		);
		if (assessment.getType().equals(AssessmentType.SURVEY)) {
			headers.add("Assignment ID");
			headers.add("Assignment Title");
		} else {
			headers.add("Passed");
			headers.add("Score");
		}
		headers.addAll(pagination.getColumnNames());

		List<String[]> rows = Lists.newArrayList();
		rows.add(headers.toArray(new String[headers.size()]));

		for (AttemptReportRow row : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				row.getUserNumber(),
				row.getFirstName(),
				row.getLastName(),
				row.getCompanyName(),
				row.getStatus().equals("gradePending") ? "Pending" : StringUtilities.capitalizeFirstLetter(row.getStatus()),
				DateUtilities.getISO8601WithSpaces(row.getCompletedOn(), getCurrentUser().getTimeZoneId())
			);
			if (assessment.getType().equals(AssessmentType.SURVEY)) {
				data.add(row.getWorkNumber());
				data.add(row.getWorkTitle());
			} else {
				if (row.getStatus().equals("inprogress") || row.getStatus().equals("gradePending") || row.getStatus().equals("invited")) {
					data.add(null);
					data.add(null);
				} else {
					data.add(BooleanUtils.toString(row.getPassedFlag(), "Passed", "Failed"));
					data.add(row.getScore().toPlainString());

				}
			}
			for (AttemptResponseReportRow response : row.getResponses()) {
				data.add(response.getResponseValue());
			}
			rows.add(data.toArray(new String[data.size()]));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("lms-export-%d-%s.csv", assessmentId, DateUtilities.getISO8601(DateUtilities.getCalendarNow())));
		return view;
	}
}
