package com.workmarket.web.controllers.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.reporting.ReportingCriteria;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.CustomReportService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.dto.ReportRecurrenceDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.dto.UserSuggestionDTO;
import com.workmarket.service.web.CSRFTokenService;
import com.workmarket.thrift.ThriftUtilities;
import com.workmarket.thrift.work.display.ColumnValuesRequest;
import com.workmarket.thrift.work.display.FilteringEntityRequest;
import com.workmarket.thrift.work.display.HtmlTagTypeThrift;
import com.workmarket.thrift.work.display.PaginationPageThrift;
import com.workmarket.thrift.work.display.ReportResponse;
import com.workmarket.thrift.work.display.ReportRow;
import com.workmarket.thrift.work.display.ReportingReportType;
import com.workmarket.thrift.work.display.ReportingTypeRequest;
import com.workmarket.thrift.work.display.ReportingTypesInitialRequest;
import com.workmarket.thrift.work.display.SavedCustomReportResponse;
import com.workmarket.thrift.work.display.WorkDisplay;
import com.workmarket.thrift.work.display.WorkReportEntityBucketsCompositeResponse;
import com.workmarket.thrift.work.report.FilteringTypeThrift;
import com.workmarket.thrift.work.report.RelationalOperatorThrift;
import com.workmarket.thrift.work.report.WorkReportColumnType;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.EncryptionUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesWithHeaderResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.ReportRecurrenceValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping("/reports/custom")
public class CustomReportsController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(CustomReportsController.class);

	@Autowired private WorkDisplay.Iface customReportService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private ReportRecurrenceValidator reportRecurrenceValidator;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private CompanyService companyService;
	@Autowired private CustomReportService customReportServiceNew;
	@Autowired private SuggestionService suggestionService;
	@Autowired private CustomFieldService customFieldService;

	@RequestMapping(value = {"", "/", "/manage"}, method = GET)
	public String manage(
		@RequestParam(value = "reportKey", required = false) Long reportKey,
		Model model) {

		model.addAttribute("work_date_range", FilteringTypeThrift.WORK_DATE_RANGE.getValue());
		model.addAttribute("saved_report_key", reportKey);
		ReportingCriteria criteria = customReportService.getCustomReportCriteria(reportKey);
		model.addAttribute("report_name", (criteria == null) ? null : criteria.getReportName());

		model.addAttribute("workReportTypes", CollectionUtilities.newStringMap(
			String.valueOf(ReportingReportType.WORK_ASSIGNMENTS.getValue()), "Work Assignments"
		));

		model.addAttribute("workReportColumnTypes", CollectionUtilities.newStringMap(
			"WORK_SELECT_ALL", String.valueOf(WorkReportColumnType.WORK_SELECT_ALL.getValue())
		));

		model.addAttribute("htmlTagTypes", CollectionUtilities.newStringMap(
			"INPUT_TEXT", String.valueOf(HtmlTagTypeThrift.INPUT_TEXT.getValue()),
			"SELECT_OPTION", String.valueOf(HtmlTagTypeThrift.SELECT_OPTION.getValue()),
			"MULTI_SELECT_OPTION", String.valueOf(HtmlTagTypeThrift.MULTI_SELECT_OPTION.getValue()),
			"DATE", String.valueOf(HtmlTagTypeThrift.DATE.getValue()),
			"TO_FROM_DATES", String.valueOf(HtmlTagTypeThrift.TO_FROM_DATES.getValue()),
			"NUMERIC", String.valueOf(HtmlTagTypeThrift.NUMERIC.getValue()),
			"NUMERIC_RANGE", String.valueOf(HtmlTagTypeThrift.NUMERIC_RANGE.getValue())
		));

		model.addAttribute("relationalOperators", CollectionUtilities.newStringMap(
			"WORK_PLEASE_SELECT", String.valueOf(RelationalOperatorThrift.WORK_PLEASE_SELECT.getValue()),
			"WORK_EQUAL_TO", String.valueOf(RelationalOperatorThrift.WORK_EQUAL_TO.getValue()),
			"WORK_GREATER_THAN_EQUAL_TO", String.valueOf(RelationalOperatorThrift.WORK_GREATER_THAN_EQUAL_TO.getValue()),
			"WORK_LESS_THAN", String.valueOf(RelationalOperatorThrift.WORK_LESS_THAN.getValue())
		));

		model.addAttribute("filterTypes", CollectionUtilities.newStringMap(
			"WORK_PLEASE_SELECT", String.valueOf(FilteringTypeThrift.WORK_PLEASE_SELECT.getValue()),
			"WORK_NEXT_1_DAY", String.valueOf(FilteringTypeThrift.WORK_NEXT_1_DAY.getValue()),
			"WORK_NEXT_7_DAYS", String.valueOf(FilteringTypeThrift.WORK_NEXT_7_DAYS.getValue()),
			"WORK_NEXT_30_DAYS", String.valueOf(FilteringTypeThrift.WORK_NEXT_30_DAYS.getValue()),
			"WORK_LAST_1_DAY", String.valueOf(FilteringTypeThrift.WORK_LAST_1_DAY.getValue()),
			"WORK_LAST_7_DAYS", String.valueOf(FilteringTypeThrift.WORK_LAST_7_DAYS.getValue()),
			"WORK_LAST_30_DAYS", String.valueOf(FilteringTypeThrift.WORK_LAST_30_DAYS.getValue()),
			"WORK_LAST_60_DAYS", String.valueOf(FilteringTypeThrift.WORK_LAST_60_DAYS.getValue()),
			"WORK_LAST_90_DAYS", String.valueOf(FilteringTypeThrift.WORK_LAST_90_DAYS.getValue()),
			"WORK_THIS_YEAR_TO_DATE", String.valueOf(FilteringTypeThrift.WORK_THIS_YEAR_TO_DATE.getValue()),
			"WORK_LAST_YEAR_ONLY", String.valueOf(FilteringTypeThrift.WORK_THIS_YEAR_TO_DATE.getValue()),
			"WORK_LAST_365_DAYS", String.valueOf(FilteringTypeThrift.WORK_LAST_365_DAYS.getValue()),
			"WORK_DATE_RANGE", String.valueOf(FilteringTypeThrift.WORK_DATE_RANGE.getValue()),
			"WORK_NEXT_60_DAYS", String.valueOf(FilteringTypeThrift.WORK_NEXT_60_DAYS.getValue()),
			"WORK_NEXT_90_DAYS", String.valueOf(FilteringTypeThrift.WORK_NEXT_90_DAYS.getValue())
			));
		model.addAttribute("weekdays", invariantDataService.getDaysOfWeek());
		model.addAttribute("timeZoneMap", invariantDataService.findActiveTimeZonesWithShortNames());
		model.addAttribute("customReportCustomFieldGroups", customFieldService.findCustomReportCustomFieldGroupsForCompanyAndReport(getCurrentUser().getCompanyId(), reportKey));
		return "web/pages/reports/custom/manage";
	}


	@RequestMapping(
		value = "/save_report",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> saveReport(
		@RequestParam("model") String modelParam) throws Exception {

		ExtendedUserDetails user = getCurrentUser();
		ObjectMapper mapper = new ObjectMapper();

		// Extract form values from URL string and put them into the "requestData" variable.
		String unescapedUrlParams = StringEscapeUtils.unescapeHtml4(modelParam);
		@SuppressWarnings("unchecked")
		Map<String, ?> postValues = mapper.readValue(unescapedUrlParams, Map.class);

		List<Long> workCustomFieldIds = getWorkCustomFieldIdsFromValues(postValues);
		List<NameValuePair> params = URLEncodedUtils.parse((String) postValues.get("frm"), Charset.forName("UTF-8"));
		removeCustomAndMetaFields(params);
		List<ColumnValuesRequest> columnValuesRequests = getColumnValuesForRequest(params);

		ReportingTypesInitialRequest initialRequest = new ReportingTypesInitialRequest()
			.setUserNumber(user.getUserNumber())
			.setCompanyId(user.getCompanyId())
			.setLocale(Locale.ENGLISH.getLanguage());
		ReportingTypeRequest reportingTypeRequest = new ReportingTypeRequest()
			.setReportingReportType(ReportingReportType.WORK_ASSIGNMENTS)
			.setReportingTypesInitialRequest(initialRequest);

		String reportName = (String) postValues.get("name");
		String reportKey = (String) postValues.get("key");

		FilteringEntityRequest filteringRequest = new FilteringEntityRequest()
			.setReportingTypeRequest(reportingTypeRequest)
			.setColumnValuesRequests(columnValuesRequests)
			.setReportName(reportName)
			.setReportKey(reportKey)
			.setGenerateReport(false)
			.setWorkCustomFieldIds(workCustomFieldIds);

		SavedCustomReportResponse response = customReportService.saveCustomReportType(filteringRequest);

		return CollectionUtilities.newObjectMap(
			"successful", Boolean.TRUE,
			"report_key", response.getReportKey()
		);
	}

	@RequestMapping(
		value = "/delete_report/{id}",
		method = GET)
	public String deleteReport(
		@PathVariable("id") long id,
		RedirectAttributes flash) throws Exception {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (customReportServiceNew.hasAccessToCustomReport(id, getCurrentUser().getCompanyId())) {
			try {
				ExtendedUserDetails user = getCurrentUser();
				ReportingTypesInitialRequest initialRequest = new ReportingTypesInitialRequest(user.getUserNumber(), user.getCompanyId(), "en");
				customReportService.deleteCustomReport(initialRequest, id);
				messageHelper.addSuccess(bundle, "reports.custom.delete.success");
			} catch (Exception ex) {
				logger.error("error occurred while deleting custom report with id={}", id, ex);
				messageHelper.addError(bundle, "reports.custom.delete.failure");
			}
		} else {
			throw new HttpException401();
		}

		return "redirect:/reports";
	}

	@RequestMapping(
		value = "/download_report",
		method = GET)
	public void downloadReport(
		@RequestParam("hash") String hash,
		HttpServletResponse response) throws IOException {

		String filename = null;
		String mimeType = null;
		InputStream in = null;

		try {
			// Get filename from the hash
			filename = EncryptionUtilities.decrypt(hash);
			mimeType = URLConnection.guessContentTypeFromName(filename);

			// Send asset to the browser.
			File file = new File(filename);

			response.setContentType((mimeType == null) ? "application/octet-stream" : mimeType);
			response.setContentLength((int) file.length());
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

			in = new FileInputStream(file);
			FileCopyUtils.copy(in, response.getOutputStream());
		} catch (Exception ex) {
			logger.error("error downloading report for hash={}, filename={} and mimeType={}", new Object[]{hash, filename, mimeType}, ex);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}


	@RequestMapping(
		value = "/results",
		method = GET)
	public String getReportResults(
		@RequestParam("report_id") Long reportId,
		Model model) throws Exception {

		if (!customReportServiceNew.hasAccessToCustomReport(reportId, getCurrentUser().getCompanyId())) {
			throw new HttpException401();
		}

		model.addAttribute("report_id", reportId);
		model.addAttribute("work_date_range", FilteringTypeThrift.WORK_DATE_RANGE.getValue());
		model.addAttribute("workReportColumnTypes", CollectionUtilities.newStringMap(
			"WORK_SELECT_ALL", String.valueOf(WorkReportColumnType.WORK_SELECT_ALL.getValue())
		));

		model.addAttribute("htmlTagTypes", CollectionUtilities.newStringMap(
			"INPUT_TEXT", String.valueOf(HtmlTagTypeThrift.INPUT_TEXT.getValue()),
			"SELECT_OPTION", String.valueOf(HtmlTagTypeThrift.SELECT_OPTION.getValue()),
			"MULTI_SELECT_OPTION", String.valueOf(HtmlTagTypeThrift.MULTI_SELECT_OPTION.getValue()),
			"DATE", String.valueOf(HtmlTagTypeThrift.DATE.getValue()),
			"TO_FROM_DATES", String.valueOf(HtmlTagTypeThrift.TO_FROM_DATES.getValue()),
			"NUMERIC", String.valueOf(HtmlTagTypeThrift.NUMERIC.getValue()),
			"NUMERIC_RANGE", String.valueOf(HtmlTagTypeThrift.NUMERIC_RANGE.getValue())
		));

		model.addAttribute("relationalOperators", CollectionUtilities.newStringMap(
			"WORK_PLEASE_SELECT", String.valueOf(RelationalOperatorThrift.WORK_PLEASE_SELECT.getValue()),
			"WORK_EQUAL_TO", String.valueOf(RelationalOperatorThrift.WORK_EQUAL_TO.getValue()),
			"WORK_GREATER_THAN_EQUAL_TO", String.valueOf(RelationalOperatorThrift.WORK_GREATER_THAN_EQUAL_TO.getValue()),
			"WORK_LESS_THAN", String.valueOf(RelationalOperatorThrift.WORK_LESS_THAN.getValue())
		));

		model.addAttribute("filterTypes", CollectionUtilities.newStringMap(
			"WORK_PLEASE_SELECT", String.valueOf(FilteringTypeThrift.WORK_PLEASE_SELECT.getValue()),
			"WORK_NEXT_1_DAY", String.valueOf(FilteringTypeThrift.WORK_NEXT_1_DAY.getValue()),
			"WORK_NEXT_7_DAYS", String.valueOf(FilteringTypeThrift.WORK_NEXT_7_DAYS.getValue()),
			"WORK_NEXT_30_DAYS", String.valueOf(FilteringTypeThrift.WORK_NEXT_30_DAYS.getValue()),
			"WORK_LAST_1_DAY", String.valueOf(FilteringTypeThrift.WORK_LAST_1_DAY.getValue()),
			"WORK_LAST_7_DAYS", String.valueOf(FilteringTypeThrift.WORK_LAST_7_DAYS.getValue()),
			"WORK_LAST_30_DAYS", String.valueOf(FilteringTypeThrift.WORK_LAST_30_DAYS.getValue()),
			"WORK_LAST_60_DAYS", String.valueOf(FilteringTypeThrift.WORK_LAST_60_DAYS.getValue()),
			"WORK_LAST_90_DAYS", String.valueOf(FilteringTypeThrift.WORK_LAST_90_DAYS.getValue()),
			"WORK_THIS_YEAR_TO_DATE", String.valueOf(FilteringTypeThrift.WORK_THIS_YEAR_TO_DATE.getValue()),
			"WORK_LAST_YEAR_ONLY", String.valueOf(FilteringTypeThrift.WORK_THIS_YEAR_TO_DATE.getValue()),
			"WORK_LAST_365_DAYS", String.valueOf(FilteringTypeThrift.WORK_LAST_365_DAYS.getValue()),
			"WORK_DATE_RANGE", String.valueOf(FilteringTypeThrift.WORK_DATE_RANGE.getValue()),
			"WORK_NEXT_60_DAYS", String.valueOf(FilteringTypeThrift.WORK_NEXT_60_DAYS.getValue()),
			"WORK_NEXT_90_DAYS", String.valueOf(FilteringTypeThrift.WORK_NEXT_90_DAYS.getValue())
		));

		model.addAttribute("weekdays", invariantDataService.getDaysOfWeek());
		model.addAttribute("timeZoneMap", invariantDataService.findActiveTimeZonesWithShortNames());

		return "web/pages/reports/custom/results";
	}

	@RequestMapping(
		value = "/report_data.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getReportData(
		@RequestParam("report_id") Long reportId,
		HttpServletRequest request,
		Model model) throws Exception {

		if (!customReportServiceNew.hasAccessToCustomReport(reportId, getCurrentUser().getCompanyId())) {
			throw new HttpException401();
		}

		ExtendedUserDetails currentUser = getCurrentUser();
		DataTablesRequest dataTablesRequest = DataTablesRequest.newInstance(request);
		ReportResponse reportResponse = customReportServiceNew.generateSavedCustomReport(reportId, companyService.findCompanyById(currentUser.getCompanyId()));
		DataTablesWithHeaderResponse<List<String>,Map<String,Object>> response = DataTablesWithHeaderResponse.newInstance(dataTablesRequest);
		for (ReportRow row : reportResponse.getReportRow()){
			response.addRow(row.getReportFields());
		}
		response.setColumns(customReportServiceNew.createDataTableColumnHeaders(reportId,reportResponse.getReportHeader().getReportFields()));
		model.addAttribute("response", response);
	}

	@RequestMapping(
		value="/report_filters.json",
		method= POST,
		produces = APPLICATION_JSON_VALUE)
	public void saveReportFilters(
		@RequestParam("report_id") Long reportId,
		@RequestBody Map<String,Object> filters) {

		customReportServiceNew.updateFilters(reportId, filters);
	}

	@RequestMapping(
		value = "/report_filters.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody String getReportFilters(
		@RequestParam("report_id") Long reportId) throws Exception {

		if (!customReportServiceNew.hasAccessToCustomReport(reportId, getCurrentUser().getCompanyId())) {
			throw new HttpException401();
		}

		ExtendedUserDetails currentUser = getCurrentUser();
		WorkReportEntityBucketsCompositeResponse workResponse = customReportServiceNew.fetchFiltersForDisplay(reportId, companyService.findCompanyById(currentUser.getCompanyId()));

		return jsonSerializationService.toJson(workResponse);
	}

	@RequestMapping(
		value = "/suggest_users.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getSuggestedUsers(
		@RequestParam("term") String term,
		Model model) throws Exception {

		List<Map<String, String>> response = Lists.newArrayList();
		for (UserSuggestionDTO dto : suggestionService.suggestUser(term,getCurrentUser().getCompanyId())) {
			response.add(ImmutableMap.of(
				"id", dto.getEmail(),
				"value", dto.getValue()));
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/generate_report",
		method = POST,
		produces = "application/x-www-form-urlencoded; charset=UTF-8")
	public @ResponseBody String generateReport(
		@RequestParam("model") String modelParam) throws Exception {

		ExtendedUserDetails user = getCurrentUser();
		ObjectMapper mapper = new ObjectMapper();

		// Extract form values from URL string and put them into the "requestData" variable.
		String unescapedUrlParams = StringEscapeUtils.unescapeHtml4(modelParam);
		Map postValues = mapper.readValue(unescapedUrlParams, Map.class);

		List<Long> workCustomFieldIds = getWorkCustomFieldIdsFromValues(postValues);
		List<NameValuePair> params = URLEncodedUtils.parse((String) postValues.get("frm"), Charset.forName("UTF-8"));
		removeCustomAndMetaFields(params);
		List<ColumnValuesRequest> columnValuesRequests = getColumnValuesForRequest(params);

		ReportingTypesInitialRequest initialRequest = new ReportingTypesInitialRequest()
			.setUserNumber(user.getUserNumber())
			.setCompanyId(user.getCompanyId())
			.setLocale(Locale.ENGLISH.getLanguage());
		ReportingTypeRequest reportingTypeRequest = new ReportingTypeRequest()
			.setReportingReportType(ReportingReportType.WORK_ASSIGNMENTS)
			.setReportingTypesInitialRequest(initialRequest);

		Integer startRow = NumberUtilities.getNullSafe((Integer) postValues.get("start"));
		Integer pageSize = NumberUtilities.getNullSafe((Integer) postValues.get("limit"));
		PaginationPageThrift paginationThrift = new PaginationPageThrift()
			.setStartRow(startRow)
			.setPageSize((pageSize == 0) ? 25 : pageSize);

		String reportName = (String) postValues.get("name");
		boolean generateReport = BooleanUtils.toBoolean(StringUtilities.defaultString(postValues.get("export_csv"), "0"), "1", "0");

		FilteringEntityRequest filteringRequest = new FilteringEntityRequest()
			.setReportingTypeRequest(reportingTypeRequest)
			.setColumnValuesRequests(columnValuesRequests)
			.setReportName(reportName)
			.setPaginationPageThrift(paginationThrift)
			.setGenerateReport(generateReport)
			.setWorkCustomFieldIds(workCustomFieldIds);

		if (user.isMasquerading()) {
			filteringRequest.setMasqueradeUserId(user.getMasqueradeUserId());
		}

		if (generateReport){
			customReportServiceNew.generateAsyncAdhocCustomReport(filteringRequest,user.getCompanyId());
			return ThriftUtilities.serializeToJson(StringUtils.EMPTY);
		}

		ReportResponse reportResponse = customReportService.getGenerateReport(filteringRequest);

		// TODO: use JsonSerializationService.toJson() instead of ThriftUtilties.serializeToJson()
		// which won't be simple because Reporting depends on camelCased names to map fields in filters and field selectors
		return (reportResponse == null) ? StringUtils.EMPTY : ThriftUtilities.serializeToJson(reportResponse);
	}

	private List<Long> getWorkCustomFieldIdsFromValues(Map postValues) {
		List<Long> result = new ArrayList<>();
		List workCustomFieldIds = (List) postValues.get("workCustomFieldIds");

		if (CollectionUtils.isNotEmpty(workCustomFieldIds)) {
			for (Object s : workCustomFieldIds) {
				if (StringUtils.isNotBlank((String)s)) {
					result.add(Long.valueOf((String)s));
				}
			}
		}

		return result;
	}

	private void removeCustomAndMetaFields(List<NameValuePair> params) {
		if (CollectionUtils.isEmpty(params)) {
			return;
		}

		for (int i = params.size()-1; i >= 0; i--) {
			if (params.get(i).getName().toLowerCase().contains("workcustomfieldid") ||
				params.get(i).getName().equals("select_all")) {
				params.remove(i);
			}
		}
	}

	@RequestMapping(
		value = "/get_work_report_entity_buckets",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody String getEntityBuckets() throws Exception {
		return getEntityBucketsWithKey(null);
	}


	@RequestMapping(
		value = "/get_work_report_entity_buckets/{reportKey}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody String getEntityBucketsWithKey(
		@PathVariable("reportKey") Long reportKey) throws Exception {

		ReportingTypesInitialRequest initialRequest = new ReportingTypesInitialRequest()
			.setUserNumber(getCurrentUser().getUserNumber())
			.setCompanyId(getCurrentUser().getCompanyId())
			.setLocale(Locale.ENGLISH.getLanguage());

		WorkReportEntityBucketsCompositeResponse response;

		if (reportKey != null) {
			response = customReportService.getGenerateCustomReport(initialRequest, reportKey);
		} else {
			ReportingTypeRequest reportingTypeRequest = new ReportingTypeRequest()
				.setReportingReportType(ReportingReportType.WORK_ASSIGNMENTS)
				.setReportingTypesInitialRequest(initialRequest);
			response = customReportService.getWorkReportEntityBuckets(reportingTypeRequest);
		}

		return ThriftUtilities.serializeToJson(response.getWorkReportEntityBucketResponses());
	}

	/**
	 * Displays message for csv export
	 */
	@RequestMapping(
		value = "/export_to_csv",
		method = GET)
	public String exportToCsv(Model model) {

		ExtendedUserDetails user = getCurrentUser();
		model.addAttribute("recipientEmail", user.isMasquerading() ? user.getMasqueradeUser().getEmail() : user.getEmail());

		return "web/partials/reports/export_to_csv";
	}

	@RequestMapping(
		value = "/export_saved_to_csv",
		method = GET)
	public String exportSavedToCsv(
		@RequestParam("report_id") Long reportId,
		Model model) {

		if (!customReportServiceNew.hasAccessToCustomReport(reportId, getCurrentUser().getCompanyId())) {
			throw new HttpException401();
		}

		ExtendedUserDetails user = getCurrentUser();
		customReportServiceNew.generateAsyncCustomReport(
			reportId,
			companyService.findCompanyById(user.getCompanyId()), user.getUserNumber(), user.isMasquerading() ? user.getMasqueradeUser().getId() : null);

		model.addAttribute("recipientEmail", user.isMasquerading() ? user.getMasqueradeUser().getEmail() : user.getEmail());

		return "web/partials/reports/export_to_csv";
	}

	@RequestMapping(
		value = "/recurrence",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody String getDefaultRecurrence() {

		ReportRecurrenceDTO dto = new ReportRecurrenceDTO();
		setDefaultsForReportRecurrenceDTO(dto);

		return jsonSerializationService.toJson(dto);
	}


	@RequestMapping(
		value = "/recurrence/{reportKey}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody String getRecurrenceSettings(
		@PathVariable("reportKey") Long reportKey) {

		if (reportKey == null) return "";

		ReportRecurrenceDTO dto = new ReportRecurrenceDTO(customReportService.findReportRecurrence(reportKey));
		setDefaultsForReportRecurrenceDTO(dto);

		return jsonSerializationService.toJson(dto);
	}

	@RequestMapping(
		value = {"/recurrence", "/recurrence/{reportKey}"},
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder saveRecurrenceSettings(
		@RequestParam("model") String jsonRequest) throws Exception {

		// {reportKey} is also present in the payload so we don't bother binding it - backbone uses restful URL only
		MessageBundle bundle = messageHelper.newBundle();

		String dtoData = StringEscapeUtils.unescapeHtml4(jsonRequest);
		ReportRecurrenceDTO dto = jsonSerializationService.fromJson(dtoData, ReportRecurrenceDTO.class);

		BindingResult binding = new BeanPropertyBindingResult(dto, "reports.recurrence_save");
		reportRecurrenceValidator.validate(dto, binding);

		ExtendedUserDetails user = getCurrentUser();
		dto.setCompanyId(user.getCompanyId());
		dto.setUserId(user.getId());

		if (binding.hasErrors()) {
			messageHelper.setErrors(bundle, binding);
			return new AjaxResponseBuilder()
				.setMessages(bundle.getAllMessages())
				.setSuccessful(false);
		}

		SavedCustomReportResponse response = customReportService.saveCustomReportRecurrence(dto);

		if (response == null) {
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(Lists.newArrayList(messageHelper.getMessage("reports.recurrence_save.exception")));
		}

		return new AjaxResponseBuilder()
			.setSuccessful(true)
			.setMessages(Lists.newArrayList(messageHelper.getMessage("reports.recurrence_save.success")));
	}

	private void setDefaultsForReportRecurrenceDTO(ReportRecurrenceDTO dto) {
		if (StringUtils.isEmpty(dto.getTimeZoneId()))
			dto.setTimeZoneId(getCurrentUser().getTimeZoneId());

		// include the current user as a recipient by default
		if (CollectionUtils.isEmpty(dto.getRecipients()))
			dto.setRecipients(Sets.newHashSet(getCurrentUser().getEmail() + ";"));
	}

	private List<ColumnValuesRequest> getColumnValuesForRequest(List<NameValuePair> params) {
		List<ColumnValuesRequest> result = Lists.newLinkedList();
		for (NameValuePair pair : params) {
			// This is a remnant of behavior from PHP's `parse_str()` function which
			// automagically converts parameters with "." characters into "_".
			// @see http://www.php.net/manual/en/function.parse-str.php#91661
			if (CSRFTokenService.TOKEN_PARAMETER_NAME.equals(pair.getName())) // skip the CSRF token
				continue;
			String name = pair.getName().replace(".", "_");

			String value = pair.getValue();
			for (ColumnValuesRequest element : result) {
				if (element.getKeyName().equals(name)) {
					value = element.getValue() + ',' + pair.getValue();
					result.remove(element);
					break;
				}
			}
			result.add(new ColumnValuesRequest(name, value));
		}
		return result;
	}
}
