package com.workmarket.web.controllers.lms.admin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.dto.AssessmentUser;
import com.workmarket.dto.AssessmentUserPagination;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.service.search.SearchPreferencesService;
import com.workmarket.service.search.SearchService;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/lms/manage")
public class AssessmentInvitesController extends BaseAssessmentAdminController {

	@Autowired private EventFactory eventFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private InvariantDataService invariantService;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private UserGroupService groupService;
	@Autowired private SearchService peopleSearchService;
	@Autowired private UserService userService;
	@Autowired private RequestService requestService;
	@Autowired private SearchPreferencesService searchPreferencesService;

	@RequestMapping(
		value="/invite/{assessmentId}",
		method = GET,
		produces = TEXT_HTML_VALUE)
 /*
 * TODO: this method and the corresponding 'index' method in SearchController have lots of overlap
 * Refactor and remove unnecessary services
 */
	public String index(
		@RequestParam(value = "keyword", required = false) String defaultSearchText,
		@RequestParam(value = "lane", required = false) Integer defaultSearchLane,
		@PathVariable("assessmentId") Long assessmentId,
		Model model) throws SearchException {

		UserGroupPagination groupPagination = new UserGroupPagination(true);
		groupPagination = groupService.findAllGroupsByCompanyId(getCurrentUser().getCompanyId(), groupPagination);
		Map<Long, String> groups = Maps.newHashMap();
		Map<Long, Boolean> groupPermissions = Maps.newHashMap();
		for (UserGroup g : groupPagination.getResults()) {
			groups.put(g.getId(), g.getName());
			groupPermissions.put(g.getId(), g.getOpenMembership());
		}

		int totalCount;
		try {
			com.workmarket.search.request.user.Pagination pagination = new com.workmarket.search.request.user.Pagination();
			pagination.setCursorPosition(0);
			pagination.setPageSize(0);

			PeopleSearchRequest searchRequest = new PeopleSearchRequest();
			searchRequest.setUserId(getCurrentUser().getId());
			searchRequest.setPaginationRequest(pagination);
			searchRequest.setCurrentAssessmentId(assessmentId);

			PeopleSearchResponse searchResponse = peopleSearchService.searchPeople(searchRequest);
			totalCount = searchResponse.getTotalResultsCount();
		} catch (Exception e) {
			throw new RuntimeException("search failed", e);
		}

		model.addAttribute("assessment", getAssessment(assessmentId));
		model.addAttribute("companyIsLocked", getCurrentUser().getCompanyIsLocked());
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("groups", groups);
		model.addAttribute("groupPermissions", jsonSerializationService.toJson(groupPermissions));
		model.addAttribute("defaultsearch", defaultSearchText);
		model.addAttribute("default_lane", defaultSearchLane);
		model.addAttribute("states", invariantService.getStateDTOs());
		model.addAttribute("preferences", searchPreferencesService.get(getCurrentUser().getId()).get("search_preferences"));

		return "web/pages/lms/manage/invite";
	}

	@RequestMapping(
		value = "/invite/{assessmentId}.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void inviteList(
		@PathVariable("assessmentId") Long id,
		HttpServletRequest httpRequest,
		Model model) throws Exception {

		Assessment assessment = getAssessment(id);
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(CollectionUtilities.<Integer, String>newTypedObjectMap(
			1, AssessmentUserPagination.SORTS.USER_LAST_NAME.toString(),
			2, AssessmentUserPagination.SORTS.LOCATION.toString(),
			3, AssessmentUserPagination.SORTS.STATUS.toString()
		));
		request.setFilterMapping(CollectionUtilities.<String, Enum<?>>newTypedObjectMap(
			"filters.type", AssessmentUserPagination.FILTER_KEYS.LANE_TYPE_ID,
			"filters.status", AssessmentUserPagination.FILTER_KEYS.STATUS
		));

		AssessmentUserPagination pagination = request.newPagination(AssessmentUserPagination.class);
		pagination = assessmentService.findAllAssessmentUsers(assessment.getId(), pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		for (AssessmentUser row : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				null,
				StringUtilities.fullName(row.getFirstName(), row.getLastName()),
				PostalCodeUtilities.formatAddressShort(row.getCity(), row.getState(), row.getPostalCode(), row.getCountry()),
				row.getInvitationStatus()
			);

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"user_id", row.getUserId(),
				"user_number", row.getUserNumber(),
				"lane_type", row.getLaneType().ordinal(),
				"company_name", row.getCompanyName(),
				"status", row.getInvitationStatus(),
				"invited_on", DateUtilities.format("MM/dd/YYYY", row.getDateAdded(), getCurrentUser().getTimeZoneId())
			);

			response.addRow(data, meta);
		}

		response.setResponseMeta(CollectionUtilities.newObjectMap(
			"aggregates", assessmentService.countAssessmentUsers(assessment.getId(), pagination)
		));

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/send_invite/{assessmentId}",
		method = POST)
	@ResponseBody
	public AjaxResponseBuilder sendInvite(
		@PathVariable("assessmentId") Long id,
		@RequestParam(value="user_numbers[]", required=false) Set<String> userNumbers,
		MessageBundle messages) {

		if (userNumbers == null || userNumbers.isEmpty()) {
			messageHelper.addError(messages, "lms.admin.invite.not_empty");
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setRedirect(String.format("/lms/manage/invite/%d", id))
				.setMessages(messages.getAllMessages());
		}

		if (!CollectionUtilities.containsAny(assessmentService.getRequestContext(id), RequestContext.ADMIN)) {
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setRedirect(String.format("/lms/manage/invite/%d", id))
				.setMessages(messages.getAllMessages());
		}
		try {
			eventRouter.sendEvent(
				eventFactory.buildInviteUsersToAssessmentEvent(getCurrentUser().getId(), userNumbers, id));
			messageHelper.addSuccess(messages, "lms.admin.invite.success", userNumbers.size());
			return new AjaxResponseBuilder()
				.setSuccessful(true)
				.setMessages(messages.getAllMessages());
		} catch (Exception e) {
			messageHelper.addSuccess(messages, "lms.admin.invite.failure");
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setRedirect(String.format("/lms/manage/invite/%d", id))
				.setMessages(messages.getAllMessages());
		}
	}

	@RequestMapping(
		value = "/send_invite_user",
		method = POST)
	@ResponseBody
	public AjaxResponseBuilder sendInviteUser(
		@RequestParam("user_number") String userNumber,
		@RequestParam(value="assessment_ids[]", required=false) Long[] assessmentIds,
		MessageBundle messages) {

		Long userId = userService.findUserId(userNumber);

		if (ArrayUtils.isEmpty(assessmentIds)) {
			messageHelper.addSuccess(messages, "lms.admin.invite_user.not_empty");
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(messages.getAllMessages());
		}

		try {
			requestService.inviteUserToAssessments(getCurrentUser().getId(), userId, assessmentIds);
			messageHelper.addSuccess(messages, "lms.admin.invite_user.success");
			return new AjaxResponseBuilder()
				.setSuccessful(true)
				.setMessages(messages.getAllMessages());
		} catch (Exception e) {
			messageHelper.addError(messages, "lms.admin.invite_user.failure");
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(messages.getAllMessages());
		}
	}
}
