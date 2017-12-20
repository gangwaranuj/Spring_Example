package com.workmarket.web.controllers.settings;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.WorkTemplatePagination;
import com.workmarket.service.business.CompanyService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.views.CSVView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings/manage")
public class SettingsTemplatesController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SettingsTemplatesController.class);

	@Autowired private WorkService workService;
	@Autowired private WorkTemplateService workTemplateService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private CompanyService companyService;

	@ModelAttribute("mmw")
	public ManageMyWorkMarket createModel() {
		ManageMyWorkMarket mmw = companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId());

		if (mmw == null) {
			mmw = new ManageMyWorkMarket();
		}

		return mmw;
	}

	@RequestMapping(value="/templates", method = GET)
	public String templates() {
		return "web/pages/settings/manage/templates/active";
	}

	@RequestMapping(value="/templates/inactive", method = GET)
	public String templatesInactive() {
		return "web/pages/settings/manage/templates/inactive";
	}


	@RequestMapping(value="/enable_customfields", method = GET)
	public @ResponseBody Map<String,Object> enableCustomFields() {
		Map<String,Object> output = new HashMap<>();

		// TODO: This is for testing until implemented.
		output.put("successful", Boolean.TRUE);
		return output;
	}

	@RequestMapping(value="/templates.csv", method = GET)
	public CSVView templatesExport(Model model) {

		List<String[]> rows = Lists.newArrayList();
		rows.add(CollectionUtilities.newArray(
			"Name",
			"ID",
			"Last Used",
			"Times Used"
		));

		WorkTemplatePagination pagination = new WorkTemplatePagination(true);
		pagination.setSortColumn(WorkTemplatePagination.SORTS.NAME.toString());
		pagination.setSortDirection(Pagination.SORT_DIRECTION.ASC);

		pagination = workTemplateService.findAllActiveWorkTemplates(getCurrentUser().getCompanyId(), pagination);

		for (WorkTemplate w : pagination.getResults()) {

			rows.add(CollectionUtilities.newArray(
				w.getTemplateName(),
				w.getWorkNumber(),
				(w.getLatestCreatedWork() != null) ? DateUtilities.format("MMM d, yyyy h:mma z", w.getLatestCreatedWork(), getCurrentUser().getTimeZoneId()) : "",
				w.getWorkCount().toString()
			));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("templates-%s.csv", DateUtilities.format("yyyyMMddHH:mm", DateUtilities.getCalendarNow())));
		return view;
	}

	@RequestMapping(
		value="/templates_list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void templatesList(Model model, HttpServletRequest httpRequest) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.<Integer, String>of(
			0, WorkTemplatePagination.SORTS.NAME.toString(),
			1, WorkTemplatePagination.SORTS.LATEST_CREATED_WORK_DATE.toString()
		));

		WorkTemplatePagination pagination = request.newPagination(WorkTemplatePagination.class);
		pagination = workTemplateService.findAllActiveWorkTemplates(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (WorkTemplate w : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				w.getTemplateName(),
				(w.getLatestCreatedWork() != null) ? DateUtilities.format("MMM d, yyyy", w.getLatestCreatedWork(), getCurrentUser().getTimeZoneId()) : "Unused",
				null
			);

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"name", w.getTemplateName(),
				"id", w.getId(),
				"number", w.getWorkNumber()
			);
			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value="/inactive_templates_list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void inactiveTemplatesList(Model model, HttpServletRequest httpRequest) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.<Integer, String>of(
			0, WorkTemplatePagination.SORTS.NAME.toString(),
			1, WorkTemplatePagination.SORTS.LATEST_CREATED_WORK_DATE.toString()
		));

		WorkTemplatePagination pagination = request.newPagination(WorkTemplatePagination.class);
		pagination = workTemplateService.findAllTemplatesByStatusCode(getCurrentUser().getCompanyId(), pagination, WorkStatusType.DEACTIVATED);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (WorkTemplate w : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				w.getTemplateName(),
				(w.getLatestCreatedWork() != null) ? DateUtilities.format("MMM d, yyyy", w.getLatestCreatedWork(), getCurrentUser().getTimeZoneId()) : "Unused",
				null
			);

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"name", w.getTemplateName(),
				"id", w.getId(),
				"number", w.getWorkNumber()
			);
			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value="/templates_status_update",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder templatesDeactivate(
		@RequestParam("id") String workNumber) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			messageHelper.addMessage(response, "mmw.manage.templates_deactivate.notfound");
			return response;
		}

		List<WorkContext> contexts = workService.getWorkContext(work.getId(),getCurrentUser().getId());

		if (!CollectionUtilities.containsAny(contexts, WorkContext.OWNER, WorkContext.COMPANY_OWNED)) {
			throw new HttpException404().setRedirectUri("/settings/templates");
		}

		try {
			workTemplateService.toggleWorkTemplateActiveStatusById(work.getId());
			messageHelper.addMessage(response, "mmw.manage.templates_deactivate.success");
			response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("Error deleting work template for workNumber={} and workId={}", new Object[] {workNumber, work.getId()}, ex);
			messageHelper.addMessage(response, "mmw.manage.templates_deactivate.error");
		}

		return response;
	}



	@RequestMapping(
		value = "/templates_delete",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder templateDelete(
		@RequestParam(value = "id", required = false) String workNumber) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			messageHelper.addMessage(response, "mmw.manage.templates_deactivate.notfound");
			return response;
		}

		List<WorkContext> contexts = workService.getWorkContext(work.getId(),getCurrentUser().getId());

		if (!CollectionUtilities.containsAny(contexts, WorkContext.OWNER, WorkContext.COMPANY_OWNED)) {
			messageHelper.addMessage(response, "mmw.manage.templates_deleted.error");
			return response;
		}

		try {
			workTemplateService.deleteWorkTemplate(work.getId());
			response.setSuccessful(true);
			messageHelper.addMessage(response, "mmw.manage.templates_deleted.success");
		} catch (Exception ex) {
			logger.error("Error deleting work template for workNumber={} and workId={}", new Object[] {workNumber, work.getId()}, ex);
			messageHelper.addMessage(response, "mmw.manage.templates_deleted.error");
		}

		return response;
	}
}