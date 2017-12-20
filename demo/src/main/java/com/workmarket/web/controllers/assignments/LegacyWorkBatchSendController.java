package com.workmarket.web.controllers.assignments;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.configuration.Constants;
import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.data.report.work.WorkReportRow;
import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.domains.model.Sort;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.search.gen.GroupMessages.TalentPool;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolRequest;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolResponse;
import com.workmarket.search.gen.GroupMessages.SearchType;
import com.workmarket.search.gen.GroupMessages.SortField;
import com.workmarket.search.gen.Common.SortDirectionType;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.SearchService;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.thrift.work.MultipleWorkSendRequest;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkSendRequest;
import com.workmarket.thrift.work.uploader.WorkUploadException;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.work.WorkBatchSendConfigForm;
import com.workmarket.web.forms.work.WorkBatchSendForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.workmarket.utility.StringUtilities.pluralize;
import static com.workmarket.web.helpers.AjaxResponseBuilder.fail;
import static com.workmarket.web.helpers.AjaxResponseBuilder.success;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/assignments/bulk_send")
@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK', 'PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK')")
public class LegacyWorkBatchSendController extends BaseController {

	@Autowired private WorkReportService workReportService;
	@Autowired private TWorkService thriftWorkService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private SearchService searchService;
	@Autowired private EventRouter eventRouter;
	@Autowired JsonSerializationService jsonService;
	@Autowired private FeatureEntitlementService featureEntitlementService;

	private static final Log logger = LogFactory.getLog(LegacyWorkBatchSendController.class);

	@RequestMapping(
		method = POST,
		produces = TEXT_HTML_VALUE)
	public String index(
		@RequestParam("ids") List<String> workNumbers,
		@RequestParam(value = "resources", required = false, defaultValue = "0") int resourceCount,
		Model model) throws WorkUploadException {

		if (resourceCount > 0) {
			MessageBundle bundle = messageHelper.newBundle();
			messageHelper.addSuccess(bundle, "work.resources.send.success", workNumbers.size(), resourceCount);
			model.addAttribute("bundle", bundle);
		}

		if (featureEntitlementService.hasPercentRolloutFeatureToggle(Constants.SEARCH_SERVICE_GROUP)) {
			FindTalentPoolRequest request = FindTalentPoolRequest.newBuilder()
				.setStart(0)
				.setRows(500)
				.setUserId(getCurrentUser().getId())
				.setCompanyId(getCurrentUser().getCompanyId())
				.setSortField(SortField.NAME)
				.setSortDirection(SortDirectionType.desc)
				.setSearchType(SearchType.SEARCH_COMPANY_ROUTABLE_GROUPS)
				.build();

			FindTalentPoolResponse resp = searchService.findTalentPools(request);
			Map<String, Object> groups = Maps.newLinkedHashMap();
			for (TalentPool talentPool : resp.getTalentPoolsList()) {
				if (!Constants.MY_COMPANY_FOLLOWERS.equals(talentPool.getName())) {
					groups.put(String.valueOf(talentPool.getId()), talentPool.getName());
				}
			}
			model.addAttribute("groups", groups);
		} else {
			GroupSolrDataPagination pagination = new GroupSolrDataPagination();
			pagination.getSearchFilter().setCompanyId(getCurrentUser().getCompanyId());
			pagination.getSearchFilter().setUserId(getCurrentUser().getId());
			pagination.setSearchType(GroupSolrDataPagination.SEARCH_TYPE.SEARCH_COMPANY_ROUTABLE_GROUPS); // fetch public and private groups

			final Sort sort = new Sort("name", com.workmarket.domains.model.Pagination.SORT_DIRECTION.DESC);
			pagination.setSorts(Lists.newArrayList(sort));

			GroupSolrDataPagination userGroups = null;
			try {
				userGroups = searchService.searchAllGroups(pagination);
			} catch (Exception e) {
				logger.error("There was an error searching for groups", e);
			}

			model.addAttribute("groups", CollectionUtilities.extractKeyValues(userGroups.getResults(), "id", "name"));
		}
		model.addAttribute("ids", jsonService.toJson(workNumbers));

		return "web/pages/assignments/bulk_send";
	}

	@RequestMapping(
		value = "/list.json",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public void indexList(
		@RequestParam(value = "ids", required = false) List<String> workNumbers,
		HttpServletRequest httpRequest,
		Model model) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		WorkReportPagination pagination = request.newPagination(WorkReportPagination.class, true);

		pagination = workReportService.findAllWorkByWorkNumber(getCurrentUser().getCompanyId(), getCurrentUser().getId(), workNumbers, pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (WorkReportRow row : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				null,
				row.getTitle(),
				DateUtilities.format("MMMM d, yyyy H:mma z", row.getScheduleFrom(), row.getTimeZoneId()),
				StringUtilities.defaultString(StringUtilities.smartJoin(row.getCity(), ", ", row.getState()), "Offsite"),
				NumberUtilities.currency(row.getPrice()),
				row.getType(),
				"" + row.getChildCount(),
				null
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"work_number", row.getWorkNumber()
			);

			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder mapGroup(WorkBatchSendForm form) {

		Map<String, WorkBatchSendConfigForm> workMapping = form.getWork();
		if (MapUtils.isEmpty(workMapping)) {
			return fail()
				.setMessages(Lists.newArrayList(messageHelper.getMessage("work.batch.send.routing_empty")));
		}

		MultipleWorkSendRequest sendRequest = new MultipleWorkSendRequest();
		for (Map.Entry<String, WorkBatchSendConfigForm> entry : workMapping.entrySet()) {
			WorkBatchSendConfigForm sendConfig = entry.getValue();

			sendRequest.addToRequests(new WorkSendRequest()
				.setUserNumber(getCurrentUser().getUserNumber())
				.setWorkNumber(entry.getKey())
				.setGroupIds(sendConfig.getGroupIds())
				.setAutoSend(sendConfig.isUsingAutoSend()));
		}

		try {
			thriftWorkService.sendMultipleWork(sendRequest);
			// Reindex work after successful send
			List<String> workNumbers = extract(sendRequest.getRequests(), on(WorkSendRequest.class).getWorkNumber());
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent().setWorkNumbers(workNumbers));
		} catch (WorkActionException e) {
			logger.error(String.format("[bulk-send] error routing %d assignments", sendRequest.getRequestsSize()), e);
			return fail()
				.setMessages(Lists.newArrayList(messageHelper.getMessage("work.batch.send.exception")));
		}

		return success()
			.setRedirect("/assignments")
			.setMessages(Lists.newArrayList(messageHelper.getMessage(
				"work.batch.send.success", workMapping.size(), pluralize("assignment", workMapping.size()))));
	}
}
