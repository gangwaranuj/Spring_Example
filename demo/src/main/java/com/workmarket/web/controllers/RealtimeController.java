package com.workmarket.web.controllers;

import com.google.common.collect.Lists;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.realtime.RealtimeRowDecorator;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.service.realtime.TRealtimeService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.AddressUtilities;
import com.workmarket.thrift.core.TimeRange;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.services.realtime.*;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.RescheduleRequest;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.thrift.work.VoidWorkRequest;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkActionResponse;
import com.workmarket.thrift.work.WorkActionResponseCodeType;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.funds.RescheduleWorkForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.models.BaseResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/realtime")
public class RealtimeController extends BaseController {

	private static final Log logger = LogFactory.getLog(RealtimeController.class);

	@Autowired private TRealtimeService realTimeService;
	@Autowired private TWorkService tWorkService;
	@Autowired private MessageBundleHelper messageBundleHelper;
	@Autowired private SummaryService summaryService;
	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private WorkService workService;

	@RequestMapping(
		value = {"", "/", "/admin"},
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String index(Model model, HttpServletRequest request) {

		model.addAttribute("has_deputy_role", request.isUserInRole("PERMISSION_REALTIMEACTIONS"));
		model.addAttribute("resourceNoteTypes", ModelEnumUtilities.resourceNoteTypes);
		model.addAttribute("resourceNoteActionTypes", ModelEnumUtilities.resourceNoteActionTypes);
		model.addAttribute("declineWorkActionTypes", ModelEnumUtilities.declineWorkActionTypes);

		Boolean isAdminMode = request.getServletPath().contains("admin") || (StringUtils.isNotBlank("admin_mode") && "true".equals(request.getAttribute("admin_mode")));

		return String.format("web/pages/realtime/%s", isAdminMode ? "admin" : "index");
	}


	@RequestMapping(
		value = {"/update", "/admin_update"},
		method = POST,
		consumes = APPLICATION_FORM_URLENCODED_VALUE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> update(
		HttpServletRequest request)
		throws RealtimeStatusException {

		Boolean isAdminMode = request.getServletPath().contains("admin") || (StringUtils.isNotBlank("admin_mode") && "true".equals(request.getAttribute("admin_mode")));

		long totalResults;
		Integer maxUnansweredQuestions;
		List<Object> result = Lists.newArrayList();
		List<RealtimeUser> ownersList;
		List<RealtimeDropDownOption> clientsList;
		List<RealtimeDropDownOption> projectsList;
		final RealtimeFilter filter = new RealtimeFilter();
		String timeZone = getCurrentUser().getTimeZoneId();

		Map<String, Object> map = CollectionUtilities.getAndFlattenTypedParameterMap(request.getParameterMap());

		// parse request properties - any of these could be null
		String offers = (String) map.get("filters[offers]");
		String questions = (String) map.get("filters[questions]");
		String rejections = (String) map.get("filters[rejections]");
		String viewed = (String) map.get("filters[viewed]");
		String paginationStart = (String) map.get("start");
		String pageSize = (String) map.get("pagesize");
		String expiredValue = (String) map.get("filters[timeexpired][value]");
		String expiredCmp = (String) map.get("filters[timeexpired][comparison]");
		String timeToApptValue = (String) map.get("filters[timetoappt][value]");
		String timeToApptCmp = (String) map.get("filters[timetoappt][comparison]");
		String sortBy = (String) map.get("sortby");
		String sortOrder = (String) map.get("sortorder");

		// set search filters
		if (StringUtils.isNotBlank(offers)) {
			filter.setPercentWithOffers(NumberUtils.toShort(offers));
		}
		if (StringUtils.isNotBlank(questions)) {
			filter.setNumberOfUnansweredQuestions(NumberUtils.toShort(questions));
		}
		if (StringUtils.isNotBlank(rejections)) {
			filter.setPercentWithRejections(NumberUtils.toShort(rejections));
		}
		if (StringUtils.isNotBlank(viewed)) {
			filter.setPercentResourcesWhoViewedAssignment(NumberUtils.toShort(viewed));
		}
		if (StringUtilities.all(expiredValue, expiredCmp)) {
			filter.setTimeExpired(new TimeFilter("gt".equals(expiredCmp), NumberUtils.toLong(expiredValue)));
		}

		if (StringUtilities.all(timeToApptCmp, timeToApptValue)) {
			filter.setTimeToAppointment(new TimeFilter("gt".equals(timeToApptCmp), NumberUtils.toLong(timeToApptValue)));
		}

		String[] internalOwners = (String[]) request.getParameterMap().get("filters[internal_owners][]");
		String[] clients = (String[]) request.getParameterMap().get("filters[clients][]");
		String[] projects = (String[]) request.getParameterMap().get("filters[projects][]");

		if (internalOwners != null) {
			filter.setInternalOwnerFilter(Arrays.asList(internalOwners));
		}

		if (clients != null) {
			List<Long> reqClients = new ArrayList<>();
			for (String c : clients) {
				reqClients.add(NumberUtils.toLong(c));
			}
			filter.setClientFilter(reqClients);
		}
		if (projects != null) {
			List<Long> reqProjects = new ArrayList<>();
			for (String p : projects) {
				reqProjects.add(NumberUtils.toLong(p));
			}
			filter.setProjectFilter(reqProjects);
		}

		// create pagination
		final RealtimePagination pagination = new RealtimePagination();
		if (StringUtils.isNotBlank(paginationStart)) {
			pagination.setCursorPosition(NumberUtils.toInt(paginationStart));
		}
		if (StringUtils.isNotBlank(pageSize)) {
			pagination.setPageSize(NumberUtils.toInt(pageSize));
		}
		if (StringUtils.isNotBlank(sortOrder)) {
			pagination.setSortDirection("desc".equals(sortOrder) ? SortDirectionType.DESC : SortDirectionType.ASC);
		}

		if (StringUtils.isNotBlank(sortBy))
			if ("time_to_appt".equals(sortBy))
				pagination.setSortBy(SortByType.TIME_TO_APPOINTMENT);
			else if ("scheduled_time".equals(sortBy))
				pagination.setSortBy(SortByType.SCHEDULED_TIME);
			else if ("details".equals(sortBy))
				pagination.setSortBy(SortByType.DETAILS);
			else if ("last_updated".equals(sortBy))
				pagination.setSortBy(SortByType.MODIFIED_TIME);
			else if ("order_age".equals(sortBy))
				pagination.setSortBy(SortByType.ORDER_AGE);
			else
				pagination.setSortBy(SortByType.TIME_TO_APPOINTMENT);

		RealtimeStatusPage resp;

		// get status requests
		if (isAdminMode) {
			resp = realTimeService.getRealtimeCSR(new RealtimeCSRStatusRequest() {{
				setPaginationRequest(pagination);
				setFilters(filter);
			}});
		} else {
			resp = realTimeService.getRealtime(new RealtimeStatusRequest() {{
				setPaginationRequest(pagination);
				setCompanyId(getCurrentUser().getCompanyId());
				setFilters(filter);
			}});
		}

		ownersList = resp.getOwnerFilterOptions();
		clientsList = resp.getClients();
		projectsList = resp.getProjects();

		Calendar now = Calendar.getInstance();

		if (resp.getRows() != null) {
			for (RealtimeRow item : resp.getRows()) {
				Long apptTimeFrom = item.getAssignmentTimeRange().getFrom();

				// Determine the amount of time until the appointment.
				String timeToApptResult = "";
				String apptScheduledTimeResult = "";
				if (apptTimeFrom != null && apptTimeFrom > 0) {
					DateTime timeFrom = new DateTime(apptTimeFrom).withZone(DateTimeZone.UTC);
					DateTime localizedTimeFrom = timeFrom.toDateTime(DateTimeZone.forID(timeZone));

					Long appTimeTo = item.getAssignmentTimeRange().getTo();
					DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MM/dd/yyyy h:mma z");

					if (appTimeTo != null && appTimeTo > 0) {
						DateTime timeTo = new DateTime(appTimeTo).withZone(DateTimeZone.UTC).toDateTime(DateTimeZone.forID(timeZone));
						apptScheduledTimeResult = dateFormat.print(localizedTimeFrom) + " to " + dateFormat.print(timeTo);
					} else {
						apptScheduledTimeResult = dateFormat.print(localizedTimeFrom);
					}

					long apptFromDiff = apptTimeFrom - now.getTimeInMillis();
					timeToApptResult = DateUtilities.getDurationBreakdown(apptFromDiff);
				}

				// TODO: determine hours-till-expiry?

				// Determine the assignment's age.
				Long orderSentOn = item.getOrderSentOn();
				String orderAgeResult = "";
				if (orderSentOn != null) {
					Long orderAgeMillis = now.getTimeInMillis() - orderSentOn;
					orderAgeResult = DateUtilities.getDurationBreakdown(orderAgeMillis);
				}

				// Determine last updated time of assignment
				Long modifiedOn = item.getModifiedOn();
				String modifiedOnResult = "";
				if (modifiedOn != null) {
					Long modifiedOnMillis = now.getTimeInMillis() - modifiedOn;
					modifiedOnResult = DateUtilities.getDurationBreakdown(modifiedOnMillis);
				}

				String modifierFirstName = StringUtils.substring(item.getModifierFirstName(), 0, 1);

				// populate resources, notes, statuses
				int i = 0;
				List<Object> resources = new ArrayList<>();
				Map<String, Object> resourcesIndex = new HashMap<>();
				List<RealtimeResource> invitedResources = item.getInvitedResources();
				if (invitedResources == null) invitedResources = Lists.newArrayList();

				// get invited resource info
				for (RealtimeResource res : invitedResources) {
					Map<String, Object> icons = new HashMap<>();
					if (res.getIcons() != null) {
						CollectionUtilities.addToObjectMap(icons,
							"employee", res.getIcons().contains(ResourceIconType.IS_EMPLOYEE),
							"question", res.getIcons().contains(ResourceIconType.QUESTION),
							"offer_open", res.getIcons().contains(ResourceIconType.OFFER_OPEN),
							"offer_expired", res.getIcons().contains(ResourceIconType.OFFER_EXPIRED),
							"offer_declined", res.getIcons().contains(ResourceIconType.OFFER_DECLINED),
							"note", res.getIcons().contains(ResourceIconType.NOTE),
							"viewed_on_mobile", res.getIcons().contains(ResourceIconType.VIEWED_ON_MOBILE),
							"viewed_on_web", res.getIcons().contains(ResourceIconType.VIEWED_ON_WEB)
						);
					}

					Calendar cal = Calendar.getInstance();

					// get notes
					List<Object> notes = new ArrayList<>();
					if (res.getHoverNotes() != null) {
						for (ResourceNote note : res.getHoverNotes()) {
							cal.setTimeInMillis(note.getDateOfNote());
							String noteDate = DateUtilities.format("MM/dd/yyyy h:mma z", cal, timeZone);
							notes.add(CollectionUtilities.newObjectMap(
								"type", note.getHoverType(),
								"note", note.getNote(),
								"actionCode", note.getActionCodeDescription(),
								"date", noteDate,
								"deputy", CollectionUtilities.newObjectMap(
									"firstName", note.getOnBehalfOfUser().getName().getFirstName(),
									"lastName", note.getOnBehalfOfUser().getName().getLastName(),
									"isEmployee", note.getOnBehalfOfUser().isIsWorkMarketEmployee()
								)
							));
						}
					}

					resourcesIndex.put(res.getUserNumber(), i);

					cal.setTimeInMillis(res.getDateSentOn());
					String sentOnDate = DateUtilities.format("MM/dd/yyyy h:mma z", cal, timeZone);

					// get resource address info
					Address address = res.getAddress();
					String formattedAddress = AddressUtilities.formatAddressShort(address);
					Integer laneType = null;
					if (res.getLaneType() != null) {
						laneType = res.getLaneType().ordinal();
					}
					Double distance = (res.getDistance() == 0F) ? null : res.getDistance(); // view expects this
					resources.add(i++, CollectionUtilities.newObjectMap(
						"user_number", res.getUserNumber(),
						"name", res.getFirstName() + " " + res.getLastName(),
						"distance", distance,
						"icons", icons,
						"address", formattedAddress,
						"rating", res.getAverageStarRating(),
						"num_ratings", res.getNumberOfRatings(),
						"work_phone", StringUtilities.formatPhoneNumber(res.getWorkPhoneNumber()),
						"mobile_phone", StringUtilities.formatPhoneNumber(res.getMobilePhoneNumber()),
						"email", res.getEmail(),
						"sent_on", sentOnDate,
						"lane", laneType,
						"company_name", res.getCompanyName(),
						"notes", notes));
				}

				// is user working on this assignment?
				Map<String, Object> userWorkingOnIt = null;
				RealtimeUser uwoi = item.getUserWorkingOnIt();

				if (uwoi != null) {
					userWorkingOnIt = CollectionUtilities.newObjectMap(
						"user_id", uwoi.getUserId(),
						"user_number", uwoi.getUserNumber(),
						"first_name", uwoi.getFirstName(),
						"last_name", uwoi.getLastName());
				}

				NumberFormat fmt = NumberFormat.getCurrencyInstance();

				Map<String, Object> row = CollectionUtilities.newObjectMap(
					"work_number", item.getWorkNumber(),
					"time_to_appt", timeToApptResult,
					"age", orderAgeResult,
					"date_time", apptScheduledTimeResult,
					"title", item.getDetailsText(),
					"spend_limit", fmt.format(item.getSpendLimit()),
					"group_sent", item.isGroupSent(),
					"questions", item.getQuestionCount(),
					"offers", item.getOffers(),
					"declines", item.getDeclines(),
					"resources", resources,
					"resources_index", resourcesIndex,
					"resource_count", invitedResources.size(),
					"last_modified_on", modifiedOnResult,
					"modifier_first_name", modifierFirstName,
					"modifier_last_name", item.getModifierLastName(),
					"user_working_on_it", userWorkingOnIt,
					"number_of_unanswered_questions", item.getNumberOfUnansweredQuestions(),
					"percent_resources_declined", item.getPercentResourcesDeclined(),
					"percent_time_to_work_elapsed", item.getPercentTimeToWorkElapsed(),
					"percent_with_offers", item.getPercentWithOffers(),
					"percent_with_rejections", item.getPercentWithRejections(),
					"expires_in_3_hours", (item.getFacts() != null && item.getFacts().contains(RealtimeRowFact.EXPIRES_IN_3_HOURS)),
					"is_work_notify_allowed", workService.isWorkNotifyAllowed(((RealtimeRowDecorator)item).getWorkId()),
					"is_work_notify_available", workService.isWorkNotifyAvailable(((RealtimeRowDecorator)item).getWorkId()));

				RealtimeCompany company = item.getCompany();
				if (isAdminMode && company != null) {
					CollectionUtilities.addToObjectMap(row,
						"company_id", company.getCompanyId(),
						"company_name", company.getCompanyName()
					);
				}

				result.add(row);
			}
		}

		totalResults = resp.getNumResults();
		maxUnansweredQuestions = resp.getMaxUnansweredQuestions();

		return CollectionUtilities.newObjectMap(
			"results", result,
			"results_count", totalResults,
			"owners", ownersList,
			"projects", projectsList,
			"clients", clientsList,
			"max_unanswered_questions", maxUnansweredQuestions,
			"last_updated", DateUtilities.format("MMMM d, yyyy 'at' h:mm a z", Calendar.getInstance(), timeZone),
			"request_ts", now.getTimeInMillis()
		);
	}

	@RequestMapping(value = "/admin_update", method = GET)
	public @ResponseBody Map<String, Object> adminUpdate(HttpServletRequest request)
		throws RealtimeStatusException {

		request.setAttribute("admin_mode", "true");
		return update(request);
	}


	@RequestMapping(value = "/counts", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> counts() {
		return getCounts(false);
	}


	@RequestMapping(value = "/admin_counts", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> adminCounts() {
		return getCounts(true);
	}


	@RequestMapping(value = {"/counts", "/update"})
	public String redirectToRealtime() {
		return "redirect:/realtime/";
	}


	@RequestMapping(value = {"/admin_counts", "/admin_update"})
	public String redirectToAdminRealtime() {
		return "redirect:/realtime/admin/";
	}


	@RequestMapping(value = "/work_notes/{workNumber}", method = GET)
	public String workNotes(
		@PathVariable("workNumber") String workNumber,
		HttpServletRequest request,
		Model model) throws WorkActionException {

		WorkResponse response = loadWorkResponse(workNumber, WorkRequestInfo.CONTEXT_INFO, WorkRequestInfo.NOTES_INFO);

		if (response != null) {
			Set<AuthorizationContext> contexts = response.getAuthorizationContexts();

			if (!CollectionUtils.isEmpty(contexts)) {
				boolean isAdmin = contexts.contains(AuthorizationContext.ADMIN);
				boolean isActiveResource = contexts.contains(AuthorizationContext.ACTIVE_RESOURCE);
				boolean isInternal = request.isUserInRole("ROLE_INTERNAL");
				model.addAttribute("is_admin", isAdmin);
				model.addAttribute("is_active_resource", isActiveResource);
				model.addAttribute("internal", isInternal);
				if (isAdmin || isActiveResource || isInternal) {
					model.addAttribute("work", response.getWork());
				}
			}
		}

		return "web/pages/realtime/assignments/notes";
	}


	@RequestMapping(value = "/work_history/{workNumber}", method = GET)
	public String workHistory(
		@PathVariable("workNumber") String workNumber,
		HttpServletRequest request,
		Model model) throws WorkActionException {

		WorkResponse response = loadWorkResponse(workNumber, WorkRequestInfo.CONTEXT_INFO, WorkRequestInfo.CHANGE_LOG_INFO);

		if (response != null) {
			Set<AuthorizationContext> contexts = response.getAuthorizationContexts();
			if ((!CollectionUtils.isEmpty(contexts) && (contexts.contains(AuthorizationContext.ADMIN)) || request.isUserInRole("ROLE_INTERNAL"))) {
				model.addAttribute("work", response.getWork());
			}
		}

		return "web/pages/realtime/assignments/history";
	}


	@RequestMapping(value = "/toggle_workingonit/{id}/{status}", method = POST)
	public @ResponseBody Map<String, Object> toggleWorkingonit(
		@PathVariable("id") String id,
		@PathVariable("status") Integer status,
		HttpServletRequest request) throws WorkActionException, RealtimeStatusException {

		Map<String, Object> result = new HashMap<>();
		WorkResponse workResponse = loadWorkResponse(id, WorkRequestInfo.CONTEXT_INFO);

		Map<String, Object> exceptionResponse = CollectionUtilities.newObjectMap(result,
			"success", false,
			"messages",
			new String[]{messageBundleHelper.getMessage("realtime.toggle_workingonit.exception", null, null)}
		);

		if (workResponse != null) {
			Set<AuthorizationContext> contexts = workResponse.getAuthorizationContexts();
			if ((!CollectionUtils.isEmpty(contexts) && (contexts.contains(AuthorizationContext.ADMIN)) || request.isUserInRole("ROLE_INTERNAL"))) {
				ExtendedUserDetails user = getCurrentUser();

				WorkActionRequest workAction = new WorkActionRequest();
				workAction.setWorkNumber(id);
				workAction.setResourceUserNumber(null);
				if (getCurrentUser().isMasquerading()) {
					workAction.setMasqueradeUserNumber(Long.toString(getCurrentUser().getMasqueradeUserId()));
				}
				workAction.setOnBehalfOfUserNumber(user.getUserNumber());
				workAction.setCurrentUserId(user.getId());

				WorkingOnItStatusType workStatus = (status == 1) ? WorkingOnItStatusType.ON : WorkingOnItStatusType.OFF;
				WorkingOnItRequest workRequest = new WorkingOnItRequest(workAction, workStatus);

				WorkActionResponse response = realTimeService.markWorkingOnIt(workRequest);

				if (response.getResponseCode().equals(WorkActionResponseCodeType.SUCCESS)) {
					Map<String, Object> userWorkingOnItData = (status == 1)
						? CollectionUtilities.newObjectMap(
						"user_id", user.getId(),
						"first_name", user.getFirstName(),
						"last_name", user.getLastName())
						: null;

					CollectionUtilities.addToObjectMap(result,
						"success", true,
						"user_working_on_it", userWorkingOnItData,
						"work_number", id);
				} else {
					result = exceptionResponse;
				}
			} else {
				CollectionUtilities.addToObjectMap(result,
					"success", false,
					"messages", new String[]{messageBundleHelper.getMessage("realtime.toggle_workingonit.notauthorized", null, null)});
			}
		}

		return result;
	}


	@RequestMapping(value = "/cancel_work/{workNumber}", method = GET)
	public String showCancelWork(
		@PathVariable("workNumber") String workNumber,
		HttpServletRequest request,
		Model model,
		RedirectAttributes flash) throws WorkActionException {

		WorkResponse response = loadWorkResponse(workNumber,
			WorkRequestInfo.CONTEXT_INFO, WorkRequestInfo.SCHEDULE_INFO, WorkRequestInfo.STATUS_INFO);

		MessageBundle bundle = messageBundleHelper.newFlashBundle(flash);

		if (response != null) {
			model.addAttribute("work", response.getWork());

			Set<AuthorizationContext> contexts = response.getAuthorizationContexts();
			if ((CollectionUtils.isEmpty(contexts) || (!contexts.contains(AuthorizationContext.ADMIN)) && !request.isUserInRole("ROLE_INTERNAL"))) {
				messageBundleHelper.addError(bundle, "realtime.cancel_work.notauthorized");
				flash.addFlashAttribute("bundle", bundle);

				return "redirect:/error/no_access";
			}

			long fromDateMillis = response.getWork().getSchedule().getFrom();
			boolean isChargedFee = (fromDateMillis - Calendar.getInstance().getTimeInMillis()) < 24L;
			model.addAttribute("is_charged_fee", isChargedFee);

		} else {
			messageBundleHelper.addError(bundle, "realtime.cancel_work.exception");
			return "redirect:/realtime";
		}

		return "web/pages/realtime/assignments/cancel_work";
	}


	@RequestMapping(value = "/cancel_work", method = POST)
	public @ResponseBody Map<String, Object> submitCancelWork(
		@RequestParam(value = "id", required = false) String id,
		@RequestParam(value = "note", required = false) String note) throws WorkActionException {

		Map<String, Object> result = new HashMap<>();

		if (StringUtils.isBlank(note)) {
			result.put("errors", Lists.newArrayList(messageBundleHelper.getMessage("NotEmpty", "Note")));
			result.put("successful", false);
			return result;
		}

		WorkActionRequest workAction = new WorkActionRequest();
		workAction.setWorkNumber(id);
		workAction.setResourceUserNumber(null);
		if (getCurrentUser().isMasquerading()) {
			workAction.setMasqueradeUserNumber(getCurrentUser().getMasqueradeUser().getUserNumber());
		}
		workAction.setOnBehalfOfUserNumber(getCurrentUser().getUserNumber());
		workAction.setCurrentUserId(getCurrentUser().getId());
		VoidWorkRequest voidWorkRequest = new VoidWorkRequest(note, workAction);

		WorkActionResponse response = tWorkService.voidWork(voidWorkRequest);
		if (response.getResponseCode() == WorkActionResponseCodeType.SUCCESS) {
			result.put("successful", true);
		} else {
			result.put("successful", false);
			result.put("errors", Lists.newArrayList(response.getMessage()));
		}

		return result;
	}


	@RequestMapping(value = "/reschedule_work/{workNumber}", method = GET)
	public String showRescheduleWork(
		@PathVariable("workNumber") String workNumber,
		HttpServletRequest request,
		Model model) throws WorkActionException {

		WorkResponse response = loadWorkResponse(workNumber,
			WorkRequestInfo.CONTEXT_INFO, WorkRequestInfo.SCHEDULE_INFO);

		if (response != null) {
			Set<AuthorizationContext> contexts = response.getAuthorizationContexts();
			if ((!CollectionUtils.isEmpty(contexts) && (contexts.contains(AuthorizationContext.ADMIN))
				|| request.isUserInRole("ROLE_INTERNAL"))) {
				model.addAttribute("work", response.getWork());
			}
		}

		return "web/pages/realtime/assignments/reschedule_work";
	}


	@RequestMapping(value = "/reschedule_work", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody BaseResponse submitRescheduleWork(
		@Valid @ModelAttribute("form_reschedule_assignment") RescheduleWorkForm form,
		BindingResult binding) throws ParseException, WorkActionException {

		BaseResponse result = new BaseResponse();

		MessageBundle bundle = messageBundleHelper.newBundle();
		if (binding.hasErrors()) {
			messageBundleHelper.setErrors(bundle, binding);
			result.setErrors(bundle.getErrors());
			result.setSuccessful(false);
		}

		String timeZoneId = getCurrentUser().getTimeZoneId();
		DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("MM/dd/yyyy h:mma").withZone(DateTimeZone.forID(timeZoneId));
		DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MM/dd/yyyy").withZone(DateTimeZone.forID(timeZoneId));

		DateTime fromTime = null;
		if (StringUtils.isNotBlank(form.getFrom())) {
			try {
				fromTime = ((StringUtils.isNotBlank(form.getFromtime())) ? dateTimeFormat.parseDateTime(form.getFrom() + " " + form.getFromtime()) : dateFormat.parseDateTime(form.getFrom()));
			} catch (IllegalArgumentException exception) {
				result.setErrors((Lists.newArrayList(messageBundleHelper.getMessage("Pattern", "New date and time"))));
				result.setSuccessful(false);
			}
		}

		DateTime toTime = null;
		if (StringUtils.isNotBlank(form.getTo())) {
			try {
				toTime = ((StringUtils.isNotBlank(form.getTotime())) ? dateTimeFormat.parseDateTime(form.getTo() + " " + form.getTotime()) : dateFormat.parseDateTime(form.getTo()));
			} catch (IllegalArgumentException exception) {
				result.setErrors((Lists.newArrayList(messageBundleHelper.getMessage("Pattern", "End date and time"))));
				result.setSuccessful(false);
			}
		}

		TimeRange range = new TimeRange();
		if (fromTime != null) {
			if (fromTime.isBeforeNow()) {
				result.setErrors((Lists.newArrayList(messageBundleHelper.getMessage("inpast.assignment.reschedule.from"))));
				result.setSuccessful(false);
			}
			range.setFrom(fromTime.getMillis());
		}
		if (toTime != null) {
			range.setTo(toTime.getMillis());
		}
		if (!(result.getErrors() == null || result.getErrors().isEmpty())) {
			return result;
		}

		WorkActionRequest workActionRequest = new WorkActionRequest();
		workActionRequest.setWorkNumber(form.getId());
		workActionRequest.setResourceUserNumber(null);
		if (getCurrentUser().isMasquerading()) {
			workActionRequest.setMasqueradeUserNumber(getCurrentUser().getMasqueradeUserId().toString());
		}
		workActionRequest.setOnBehalfOfUserNumber(getCurrentUser().getUserNumber());
		workActionRequest.setCurrentUserId(getCurrentUser().getId());

		RescheduleRequest request = new RescheduleRequest();
		request.setWorkAction(workActionRequest);
		request.setAssignmentTimeRange(range);

		try {
			WorkActionResponse response = tWorkService.rescheduleWork(request);
			if (response.getResponseCode().equals(WorkActionResponseCodeType.SUCCESS)) {
				result.setSuccessful(true);
			} else {
				result.setErrors(Lists.newArrayList(response.getMessage()));
				result.setSuccessful(false);
			}
		} catch (ValidationException e) {
			messageBundleHelper.setErrors(bundle, ThriftValidationMessageHelper.buildBindingResult(e));
			result.setErrors(bundle.getErrors());
			result.setSuccessful(false);
		}

		return result;
	}

	@RequestMapping(value = "/workNotify/{workNumber}", method = GET)
	public String showWorkNotify(
		@PathVariable("workNumber") String workNumber,
		HttpServletRequest request,
		Model model) throws WorkActionException {

		WorkResponse response = loadWorkResponse(workNumber, WorkRequestInfo.CONTEXT_INFO);

		if (response != null) {
			Set<AuthorizationContext> contexts = response.getAuthorizationContexts();
			if ((!CollectionUtils.isEmpty(contexts) && (contexts.contains(AuthorizationContext.ADMIN)) || request.isUserInRole("ROLE_INTERNAL"))) {
				model.addAttribute("work", response.getWork());
			}
		}

		return "web/pages/realtime/assignments/work_notify";
	}

	@RequestMapping(value = "/workNotify", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody BaseResponse submitWorkNotify(
		@RequestParam(value = "workNumber", required = false) String workNumber) throws WorkActionException {

		BaseResponse result = new BaseResponse();
		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		try {
			workService.workNotifyResourcesForWork(work.getId());
			result.setSuccessful(true);
		} catch (OperationNotSupportedException ex) {
			String errMsg = messageBundleHelper.getMessage("assignment.workNotify.failure", workNumber);
			logger.error(errMsg, ex);
			result.setErrors(Lists.newArrayList(errMsg));
			result.setSuccessful(false);
		}

		return result;
	}

	@RequestMapping(value = "/resend/{workNumber}", method = GET, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody BaseResponse resend(
		@PathVariable("workNumber") String workNumber,
		HttpServletRequest request) throws WorkActionException {

		BaseResponse result = new BaseResponse();

		WorkResponse workResponse = loadWorkResponse(workNumber, WorkRequestInfo.CONTEXT_INFO);

		if (workResponse != null) {
			Set<AuthorizationContext> contexts = workResponse.getAuthorizationContexts();
			if ((!CollectionUtils.isEmpty(contexts) && (contexts.contains(AuthorizationContext.ADMIN)) || request.isUserInRole("ROLE_INTERNAL"))) {

				WorkActionRequest workActionRequest = new WorkActionRequest();
				workActionRequest.setWorkNumber(workNumber);
				workActionRequest.setResourceUserNumber(null);
				workActionRequest.setOnBehalfOfUserNumber(getCurrentUser().getUserNumber());
				workActionRequest.setCurrentUserId(getCurrentUser().getId());
				if (getCurrentUser().isMasquerading()) {
					workActionRequest.setMasqueradeUserNumber(Long.toString(getCurrentUser().getMasqueradeUserId()));
				}

				WorkActionResponse response = tWorkService.resendAllAssignments(workActionRequest);

				if (response.getResponseCode() == WorkActionResponseCodeType.SUCCESS) {
					result.setSuccessful(true);
				} else {
					result.setErrors(Lists.newArrayList(response.getMessage()));
					result.setSuccessful(false);
				}
			} else {
				result.setSuccessful(false);
				result.setErrors(Arrays.asList(messageBundleHelper.getMessage("realtime.resend.notauthorized", null, null)));
			}
		} else {
			result.setSuccessful(false);
			result.setErrors(Arrays.asList(messageBundleHelper.getMessage("realtime.resend.exception", null, null)));
		}

		return result;
	}

	private WorkResponse loadWorkResponse(String workNumber, final WorkRequestInfo... includes) throws WorkActionException {

		WorkRequest workRequest = new WorkRequest();
		workRequest.setUserId(getCurrentUser().getId());

		if (workNumber != null && !workNumber.isEmpty()) {
			workRequest.setWorkNumber(workNumber);
			workRequest.setWorkId(0L);
		} else {
			logger.warn(String.format("/work_notes - work ID is null! uid=%s", getCurrentUser().getId()));
		}

		workRequest.setIncludes(new HashSet<WorkRequestInfo>() {{
			for (WorkRequestInfo i : includes) {
				add(i);
			}
		}});

		return tWorkFacadeService.findWork(workRequest);
	}

	private Map<String, Object> getCounts(boolean isAdminMode) {
		final TotalAssignmentCount resp;
		TotalAssignmentCountRequest req = new TotalAssignmentCountRequest(getCurrentUser().getTimeZoneId());
		int gccBankAccounts = 0;
		if (isAdminMode) {
			resp = realTimeService.calculateAssignmentCountsCSR(req);
			gccBankAccounts = summaryService.countGccBankAccountsSinceRelease();
		} else {
			req.setCompanyId(getCurrentUser().getCompanyId());
			resp = realTimeService.calculateAssignmentCounts(req);
		}

		Map<String, Object> result = (resp == null)
			? new HashMap<String, Object>()
			: CollectionUtilities.newObjectMap(
			"open_assignments", resp.getOpenAssignments(),
			"today_sent_assignments", resp.getTodaySentAssignments(),
			"today_created_assignments", resp.getTodayCreatedAssignments(),
			"today_voided_assignments", resp.getTodayVoidedAssignments(),
			"today_cancelled_assignments", resp.getTodayCancelledAssignments(),
			"today_accepted_assignments", resp.getTodayAcceptedAssignments(),
			"gccBankAccounts", gccBankAccounts);

		return CollectionUtilities.newObjectMap("counts", result);
	}

}
