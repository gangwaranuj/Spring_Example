package com.workmarket.web.controllers.mobile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.compliance.service.ComplianceService;
import com.workmarket.domains.model.AbstractEntityUtilities;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.audit.ViewType;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.AvoidScheduleConflictsModelRope;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkQuestionService;
import com.workmarket.domains.work.service.dashboard.MobileDashboardService;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.search.response.work.DashboardResponse;
import com.workmarket.search.response.work.DashboardResultList;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.feed.Feed;
import com.workmarket.service.business.feed.FeedItem;
import com.workmarket.service.business.requirementsets.EligibilityService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.CustomFieldGroupSet;
import com.workmarket.thrift.work.RequestContext;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.controllers.assignments.BaseWorkController;
import com.workmarket.web.controllers.feed.FeedController;
import com.workmarket.web.editors.CalendarTimeEditor;
import com.workmarket.web.editors.LookupEntityEditor;
import com.workmarket.web.exceptions.MobileHttpException401;
import com.workmarket.web.exceptions.MobileHttpException404;
import com.workmarket.web.forms.assignments.AddLabelForm;
import com.workmarket.web.forms.assignments.WorkSetAppointmentForm;
import com.workmarket.web.forms.feed.FeedRequestParams;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.mobile.MobileResponse;
import com.workmarket.web.helpers.mobile.MobileWorkCompletionHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.DateRangeValidator;
import com.workmarket.web.validators.PartValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/mobile/assignments")
public class MobileWorkController
	extends BaseWorkController {

	private static final Log logger = LogFactory.getLog(MobileWorkController.class);

	@Autowired private JsonSerializationService jsonService;
	@Autowired private ComplianceService complianceService;
	@Autowired private DateRangeValidator dateRangeValidator;
	@Autowired private WorkQuestionService workQuestionService;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private EligibilityService eligibilityService;
	@Autowired private MobileWorkCompletionHelper mobileWorkCompletionHelper;
	@Autowired private MobileDashboardService mobileDashboardService;
	@Autowired private FeedController feedController;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private PartService partService;
	@Autowired private PartValidator partValidator;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Qualifier("avoidScheduleConflictsModelDoorman")
	@Autowired private Doorman doorman;

	private static final Map<String, String> statusLabels;

	static {
		statusLabels = ImmutableMap.<String, String>builder()
			.put(WorkStatusType.ACTIVE, "Assigned")
			.put(WorkStatusType.COMPLETE, "Pending Approval")
			.put(WorkStatusType.INPROGRESS, "In Progress")
			.put(WorkStatusType.PAYMENT_PENDING, "Invoices")
			.put(WorkStatusType.PAID, "Paid")
			.put(WorkStatusType.AVAILABLE, "Available")
			.put(WorkStatusType.ACTIVE_TODAY, "Today")
			.build();
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(WorkStatusType.class, new LookupEntityEditor(WorkStatusType.class));
	}

	@InitBinder("set_appointment_form")
	public void initHoursFormBinder(WebDataBinder binder) {

		binder.registerCustomEditor
			(
				Calendar.class,
				new CalendarTimeEditor(getCurrentUser().getTimeZoneId())
			);
	}

	@ModelAttribute("WorkStatusType")
	private Map<String, Object> getWorkStatusTypes() {

		return ModelEnumUtilities.workStatusTypes;
	}

	@ModelAttribute("PricingStrategyType")
	private Map<String, Object> getPricingStrategyTypes() {

		return ModelEnumUtilities.pricingStrategyTypes;
	}

	/**
	 * List of assignments filtered by status.
	 */
	@RequestMapping(method = GET)
	public String index(final Model model) {

		final WorkStatusType available = WorkStatusType.newWorkStatusType(WorkStatusType.AVAILABLE);

		final Map<String, Object> list = getList(available, 1);

		model.addAttribute("title", statusLabels.get(available.getCode()));
		model.addAttribute("status", available);
		model.addAllAttributes(list);

		return "mobile/pages/v2/assignments/list";
	}

	@RequestMapping(
		value = "/list/{statusType}",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String list(@PathVariable("statusType") final WorkStatusType statusType, final Model model) {

		final Map<String, Object> list = getList(statusType, 1);

		model.addAttribute("title", statusLabels.get(statusType.getCode()));
		model.addAttribute("status", statusType);
		model.addAttribute("pageClass", "list-page");
		model.addAllAttributes(list);

		if (CollectionUtilities.containsAny(statusType.getCode(), WorkStatusType.PAID)) {

			AccountRegisterSummaryFields summary = accountRegisterServicePrefundImpl.getAccountRegisterSummaryFields(getCurrentUser().getCompanyId());

			model.addAttribute("money_available_to_withdraw", summary.getWithdrawableCash());
			model.addAttribute("money_pending_earned", summary.getPendingEarnedCash());
			model.addAttribute("money_receivable", summary.getAccountsReceivableBalance());
		}

		return "mobile/pages/v2/assignments/list";
	}

	@RequestMapping(
		value = "/list/{statusType}/{page}",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String list(
		@PathVariable("statusType") final WorkStatusType statusType,
		@PathVariable("page") final Integer page,
		final Model model) {

		final Map<String, Object> list = getList(statusType, page);

		model.addAttribute("title", statusLabels.get(statusType.getCode()));
		model.addAttribute("status", statusType);
		model.addAttribute("pageClass", "list-page");
		model.addAllAttributes(list);

		if (CollectionUtilities.containsAny(statusType.getCode(), WorkStatusType.PAID)) {

			final AccountRegisterSummaryFields summary = accountRegisterServicePrefundImpl
				.getAccountRegisterSummaryFields(getCurrentUser().getCompanyId());

			model.addAttribute("money_available_to_withdraw", summary.getWithdrawableCash());
			model.addAttribute("money_pending_earned", summary.getPendingEarnedCash());
			model.addAttribute("money_receivable", summary.getAccountsReceivableBalance());
		}

		return "mobile/pages/v2/assignments/list";
	}

	@RequestMapping(
		value = "/findwork/count",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Feed count(
		@RequestParam(value = "lat", required = false) final String latitude,
		@RequestParam(value = "lon", required = false) final String longitude,
		@RequestParam(value = "page", required = false) final Integer page) {

		return mobileDashboardService.getWorkFeed(getCurrentUser().getId(), latitude, longitude, page, null);
	}

	@RequestMapping(
		value = "/findwork",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String list(
		@RequestParam(value = "lat", required = false) final String latitude,
		@RequestParam(value = "lon", required = false) final String longitude,
		@RequestParam(value = "page", defaultValue = "1") final Integer page,
		final Model model) {

		final Feed feed = mobileDashboardService.getWorkFeed(getCurrentUser().getId(), latitude, longitude, page, null);

		model.addAttribute("title", "Find Work");
		model.addAttribute("feed", feed);
		model.addAttribute("hasMore", feed.hasMorePages());

		feed.setPage(page);

		for (FeedItem item : feed.getResults()) {
			item.setPricingType(getNicePricingType(item.getPricingType()));
		}

		return "mobile/pages/assignments/feed";
	}

	@RequestMapping(
		value = "/available",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String available(final Model model) {

		model.addAttribute("title", "Available Work");
		model.addAttribute("pageClass", "available-page");

		return "mobile/pages/v2/assignments/available";
	}

	@RequestMapping(
		value = "/feed/{page}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder feedJson(
		@PathVariable("page") final Integer page,
		@RequestParam(value = "lat", required = false) final String latitude,
		@RequestParam(value = "lon", required = false) final String longitude,
		@RequestParam(value = "m", required = false) final Integer pageSize) {

		final Map<String, Object> feed = getWorkFeed(null, null, latitude, longitude, null, null, null, page, pageSize);

		return new AjaxResponseBuilder().setData(feed).setSuccessful(true);
	}

	@RequestMapping(
		value = "/listjson/{statusType}/{page}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder listJson(
		@PathVariable("statusType") final WorkStatusType statusType,
		@PathVariable("page") final Integer page) {

		final Map<String, Object> list = getList(statusType, page);

		return new AjaxResponseBuilder().setData(list).setSuccessful(true);
	}

	@RequestMapping(
		value = "/accept/{workNumber}",
		method = GET)
	public String accept(
		@PathVariable("workNumber") final String workNumber,
		final Model model,
		final HttpServletRequest request,
		final RedirectAttributes flash) {

		final MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (!StringUtils.isNumeric(workNumber)) {
			messageHelper.addError(bundle, "assignment.accept.notavailable");
			return "redirect:/mobile/assignments/list/available";
		}

		final Long userId = getCurrentUser().getId();
		final User user = userService.findUserById(userId);

		if (authenticationService.isSuspended(user)) {
			messageHelper.addError(bundle, "assignment.accept.suspended");
			return "redirect:/mobile";
		}

		final AbstractWork work = getWorkByNumber(workNumber);

		if (work == null) {
			messageHelper.addError(bundle, "assignment.accept.notavailable");
			return "redirect:/assignments";
		}

		// check if work has been accepted yet
		final Long workId = work.getId();
		final Long numAccepted = numWorkByResourceStatus(workId, WorkResourceStatusType.ACCEPTED);
		if (numAccepted > 0) {
			messageHelper.addError(bundle, "assignment.accept.closed");
			return "redirect:/mobile/assignments/list/available";
		}

		// check if the work has been closed
		if (!work.isSent()) {
			messageHelper.addError(bundle, "assignment.accept.notopen");
			return "redirect:/mobile/assignments/details/" + workNumber;
		}

		// check if this user is trying to accept his own work
		final boolean isResource = workService.isUserWorkResourceForWork(userId, workId);
		final List<WorkContext> context = workService.getWorkContext(workId, userId);
		if (!isResource && context.contains(WorkContext.OWNER)) {
			messageHelper.addError(bundle, "assignment.accept.own");
			return "redirect:/mobile/assignments/details/" + workNumber;
		}

		List<AbstractWork> conflicts = Lists.newArrayList();
		doorman.welcome(
			new UserGuest(work.getBuyer()),
			new AvoidScheduleConflictsModelRope(
				workResourceDAO,
				workService,
				work,
				getCurrentUser().getId(),
				conflicts
			)
		);
		if (!conflicts.isEmpty()) {
			messageHelper.addError(bundle, "assignment.accept.user_has_conflicts");
			return "redirect:/mobile/assignments/details/" + workNumber;
		}

		final Compliance compliance = complianceService.getComplianceFor(userId, workId);
		if (!compliance.isCompliant()) {
			messageHelper.addError(
				bundle,
				"assignment.compliance.user_accept_not_allowed",
				work.getCompany() != null ? work.getCompany().getEffectiveName() : "unknown"
			);

			return "redirect:/mobile/assignments/details/" + workNumber;
		}

		try {
			tWorkFacadeService.acceptWork(userId, workId);

			messageHelper.addSuccess(bundle, "assignment.accept.success");
		} catch (final Exception e) {
			logger.error(String.format("Error accepting work [userId=%s, workId=%s]", userId, workNumber), e);
			messageHelper.addError(bundle, "assignment.accept.exception");
		}

        /* TODO: this needs to be moved into the service - this catch block is only to handle a transaction error under very high load */
		try {

			sendAcceptedWorkDetailsPDFtoResource(workNumber, user.getId(), request, model);
		} catch (final Exception e) {

			logger.error(String.format("Error generating PDF for work [userId=%s, workId=%s]", userId, workNumber), e);
		}

		return "redirect:/mobile/assignments/details/" + workNumber;
	}

	@RequestMapping(
		value = "/reject/{workNumber}",
		method = GET)
	public String reject(
		@PathVariable("workNumber") final String workNumber,
		final RedirectAttributes redirectAttributes) {

		final MessageBundle bundle = messageHelper.newBundle();
		redirectAttributes.addFlashAttribute("bundle", bundle);

		final AbstractWork work = getWorkByNumber(workNumber);

		if (work == null) {
			messageHelper.addError(bundle, "assignment.decline.invalid_work");
			return "redirect:/mobile/";
		}

		if (!work.isSent()) {
			messageHelper.addError(bundle, "assignment.decline.invalid_status");
			return "redirect:/mobile/assignments/details/";
		}

		final List<WorkContext> context = workService.getWorkContext(work.getId(), getCurrentUser().getId());

		if (context.contains(WorkContext.OWNER)) {
			messageHelper.addError(bundle, "assignment.decline.not_owner");
			return "redirect:/mobile/assignments/details/" + workNumber;
		}

		try {
			workService.declineWork(getCurrentUser().getId(), work.getId());
			messageHelper.addSuccess(bundle, "assignment.decline.success");
			return "redirect:/mobile/";
		} catch (final Exception e) {
			logger.error("", e);
			messageHelper.addError(bundle, "assignment.decline.exception");
			return "redirect:/mobile/assignments/details/" + workNumber;
		}
	}

	@RequestMapping(
		value = "/details/{workNumber}",
		method = GET)
	public String details(
		@PathVariable("workNumber") final String workNumber,
		final Model model,
		final RedirectAttributes flash) {

		final MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (workBundleService.isAssignmentBundleLight(workNumber)) {
			flash.addFlashAttribute("bundle", model.asMap().get("bundle"));
			final Long workId = workService.findWorkId(workNumber);
			return "redirect:/assignments/view_bundle/" + workId;
		}

		final WorkResponse workResponse = getWorkDetails(workNumber);
		final Work work = workResponse.getWork();
		final boolean isResource = workResponse.getAuthorizationContexts().contains(AuthorizationContext.RESOURCE);

		model.addAttribute("work", work);
		model.addAttribute("workResponse", workResponse);
		// TODO: Alex - reference the class vars from BaseWorkController instead for these constants
		model.addAttribute("partGroup", jsonService.toJsonIdentity(work.getPartGroup()));
		model.addAttribute("partsConstantsJson", jsonService.toJsonIdentity(PartDTO.PARTS_CONSTANTS));

		 // Mobile Complete Button functionality
		final AbstractWork abstractWork = getWorkByNumber(workNumber);

		if (abstractWork == null) {
			messageHelper.addError(bundle, "assignment.accept.notavailable");
			return "redirect:/assignments";
		}

		final List<MobileResponse> validationResults = mobileWorkCompletionHelper.validateAll(work, getCurrentUser());
		final List<String> completionFaults = Lists.newArrayList();
		final List<String> completionSuccesses = Lists.newArrayList();

		for (final MobileResponse response : validationResults) {

			if (response.isSuccessful()) {
				completionSuccesses.add(response.getMessage());
			} else {
				completionFaults.add(response.getMessage());
			}
		}

		model.addAttribute("completionFaults", completionFaults);
		model.addAttribute("completionSuccesses", completionSuccesses);
		model.addAttribute("eligibility", eligibilityService.getEligibilityFor(getCurrentUser().getId(), work));

		getCustomFieldConfiguration(model, workResponse.getWork());

		model.addAttribute("isOwner", workResponse.getAuthorizationContexts().contains(AuthorizationContext.BUYER));
		model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		model.addAttribute("isResource", isResource);
		model.addAttribute("isWmEmployee", getCurrentUser().isInternal());
		model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));
		model.addAttribute("isReadOnly", workResponse.getAuthorizationContexts().contains(AuthorizationContext.READ_ONLY));
		model.addAttribute("isCancelledResource", workResponse.getRequestContexts().contains(RequestContext.CANCELLED_RESOURCE));
		model.addAttribute("isDeclinedResource", workResponse.getRequestContexts().contains(RequestContext.DECLINED_RESOURCE));
		model.addAttribute("workResourceStatusTypes", ModelEnumUtilities.workResourceStatusTypes);
		model.addAttribute("workStatusTypes", ModelEnumUtilities.workStatusTypes);
		model.addAttribute("is_employee", getCurrentUser().isSeller() && getCurrentUser().getCompanyId().equals(work.getCompany().getId()));
		model.addAttribute("buyerScoreCard", analyticsService.getBuyerScoreCardByUserId(work.getBuyer().getId()));
		model.addAttribute("allowMobileSignature", workResponse.getWork().getConfiguration().isEnableAssignmentPrintout() && workResponse.getWork().getConfiguration().isEnablePrintoutSignature());

		model.addAttribute("currentlyCheckedIn", workService.isActiveResourceCurrentlyCheckedIn(work.getId()));

		if (work.isResourceConfirmationRequired()) {
			// only do this is confirmation setting is true bc it requires another findWork call
			AbstractWork theAssignment = getWorkByNumber(workNumber);

			model.addAttribute("confirmable_date", workService.calculateRequiredConfirmationNotificationDate(theAssignment));
			model.addAttribute("confirm_by", workService.calculateRequiredConfirmationDate(theAssignment));
			model.addAttribute("in_confirmation_window", workService.isConfirmableNow(theAssignment));
		}

		model.addAttribute("title", "Details");
		model.addAttribute("assignmentsPageTitle", getCurrentUser().isSeller() ? "My Work" : "Assignment Dashboard");

		return "mobile/pages/v2/assignments/details";
	}

	@RequestMapping(
		value = "/notes/{workNumber}",
		method = GET)
	public String notes(@PathVariable("workNumber") final String workNumber, final Model model) {

		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.NOTES_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO),
			ImmutableSet.of(AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN),
			"mobile.notes");

		model.addAttribute("isOwner", workResponse.getAuthorizationContexts().contains(AuthorizationContext.BUYER));
		model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));
		model.addAttribute("title", "Notes");
		model.addAttribute("work", workResponse.getWork());

		return "mobile/pages/v2/assignments/notes";
	}

	@RequestMapping(
		value = "/dialogs/add_note/{workNumber}",
		method = GET)
	public String addNote(@PathVariable("workNumber") final String workNumber, final Model model) {
		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.PRICING_INFO),
			ImmutableSet.of(AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN),
			"mobile.add_note");

		model.addAttribute("title", "Add Note");
		model.addAttribute("work", workResponse.getWork());

		return "mobile/pages/assignments/dialogs/add_note";
	}

	@RequestMapping(
		value = "/dialogs/add_note/{workNumber}",
		method = POST)
	public String doAddNote(
		@PathVariable("workNumber") final String workNumber,
		@RequestParam(required = false) final String noteText,
		final RedirectAttributes flash,
		final Model model) {

		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.PRICING_INFO),
			ImmutableSet.of(AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN),
			"mobile.add_note");

		final MessageBundle bundle = messageHelper.newFlashBundle(flash);

		model.addAttribute("title", "Add Note");
		model.addAttribute("work", workResponse.getWork());

		if (StringUtils.isBlank(noteText)) {
			messageHelper.addError(bundle, "assignment.mobile.add_note.noteempty");
			return "redirect:/mobile/assignments/notes/" + workNumber;
		}

		final NoteDTO dto = new NoteDTO();
		dto.setContent(noteText);
		dto.setIsPrivate(false);

		final Note note = workNoteService.addNoteToWork(workResponse.getWork().getId(), dto);

		if (note != null) {
			messageHelper.addSuccess(bundle, "assignment.mobile.add_note.success");
		} else {
			messageHelper.addError(bundle, "assignment.mobile.add_note.exception");
		}

		return "redirect:/mobile/assignments/notes/" + workNumber;
	}

	@RequestMapping(
		value = "/customfields/{workNumber}",
		method = GET)
	public String customfields(@PathVariable("workNumber") final String workNumber, final Model model) {
		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.COMPANY_INFO,
				WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO),
			ImmutableSet.of(AuthorizationContext.BUYER,
				AuthorizationContext.RESOURCE,
				AuthorizationContext.ADMIN),
			"mobile.customfields");

		model.addAttribute("isOwner", workResponse.getAuthorizationContexts().contains(AuthorizationContext.BUYER));
		model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));
		model.addAttribute("isResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.RESOURCE));
		model.addAttribute("title", "Custom Fields");
		model.addAttribute("work", workResponse.getWork());

		getCustomFieldConfiguration(model, workResponse.getWork());

		return "mobile/pages/v2/assignments/customfields";
	}

	@RequestMapping(
		value = "/customfields/{workNumber}",
		method = POST)
	public String saveCustomFields(
		@PathVariable("workNumber") final String workNumber,
		@RequestParam(value = "onComplete", required = false, defaultValue = "false") final Boolean onComplete,
		final CustomFieldGroupSet form,
		final BindingResult bindingResult,
		final RedirectAttributes flash) {

		final MessageBundle messages = messageHelper.newFlashBundle(flash);
		final Map<String, Object> retMap = doSaveCustomFields(workNumber, form, bindingResult, onComplete);

        /* Check if doSaveCustomFields was successful.  If not, show the errors it generated. */
		if (!(Boolean) retMap.get("successful")) {
			for (Object error : (ArrayList) retMap.get("errors")) {
				messageHelper.addError(messages, (String) error);
			}
		} else {
			messageHelper.addSuccess(messages, "assignment.save_custom_fields.success");
		}

		return "redirect:/mobile/assignments/customfields/{workNumber}";
	}

	@RequestMapping(
		value = "/add_label/{workNumber}",
		method = GET)
	public String addLabel(@PathVariable("workNumber") final String workNumber, final Model model) {
		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.PARTS_INFO,
				WorkRequestInfo.SCHEDULE_INFO),
			ImmutableSet.of(AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN),
			"mobile.add_label");

		final WorkSubStatusTypeFilter filter = new WorkSubStatusTypeFilter();
		filter.setShowSystemSubStatus(true);
		filter.setShowCustomSubStatus(true);
		filter.setShowDeactivated(false);
		filter.setClientVisible(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		filter.setResourceVisible(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));

		final List<WorkSubStatusType> labels = workSubStatusService.findAllEditableSubStatusesByWork(workResponse.getWork().getId(), filter);
		final Map<Long, WorkSubStatusType> labelLookup = AbstractEntityUtilities.newEntityIdMap(labels);

		model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));
		model.addAttribute("title", "Add Label");
		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("labels", labelLookup);
		model.addAttribute("labelsJson", jsonService.toJson(labelLookup));

		return "mobile/pages/v2/assignments/add-label";
	}

	@RequestMapping(
		value = "/add_label/{workNumber}",
		method = POST)
	public String doAddLabel(
		@PathVariable("workNumber") final String workNumber,
		final AddLabelForm form,
		final BindingResult bindingResult,
		final RedirectAttributes flash) {

		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.PARTS_INFO,
				WorkRequestInfo.SCHEDULE_INFO),
			ImmutableSet.of(AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN),
			"mobile.add_label");

		final Long labelId = form.getLabel_id();
		final Work work = workResponse.getWork();
		final MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (labelId == null) {
			messageHelper.addError(bundle, "assignment.mobile.add_label.labelnotempty");
			return "redirect:/mobile/assignments/add_label/workNumber";
		}

		final WorkSubStatusTypeFilter filter = new WorkSubStatusTypeFilter();
		filter.setShowSystemSubStatus(true);
		filter.setShowCustomSubStatus(true);
		filter.setShowDeactivated(false);
		filter.setClientVisible(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		filter.setResourceVisible(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));

		final List<WorkSubStatusType> labels = workSubStatusService.findAllEditableSubStatusesByWork(workResponse.getWork().getId(), filter);
		final Map<Long, WorkSubStatusType> labelLookup = AbstractEntityUtilities.newEntityIdMap(labels);

		if (!labelLookup.containsKey(labelId)) {
			throw new MobileHttpException401()
				.setMessageKey("assignment.mobile.add_label.notallowed")
				.setRedirectUri("redirect:/mobile/assignments/add_label/{workNumber}");
		}

		final WorkSubStatusType label = labelLookup.get(labelId);

		DateRange dateRange = null;

		final boolean isScheduleRequired = label.isScheduleRequired();

		if (label.isNoteRequired()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "note", "NotNull");
		}

		if (isScheduleRequired) {
			final String tz = work.getTimeZone();
			dateRange = new com.workmarket.domains.model.DateRange(form.getFrom(tz), form.getTo(tz));
			dateRangeValidator.validate(dateRange, bindingResult);
		}

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return "redirect:/mobile/assignments/add_label/{workNumber}";
		}

		try {
			workSubStatusService.addSubStatus(work.getId(), labelId, form.getNote());
			if (isScheduleRequired) {
				workService.setAppointmentTime(work.getId(), dateRange, null);
			}

			messageHelper.addSuccess(bundle, "assignment.mobile.add_label.success");
			return "redirect:/mobile/assignments/details/{workNumber}";
		} catch (final Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "assignment.mobile.add_label.exception");

			return "redirect:/mobile/assignments/details/{workNumber}";
		}
	}

	@RequestMapping(
		value = "/ask_question/{workNumber}",
		method = GET)
	public String askQuestion(@PathVariable("workNumber") final String workNumber, final Model model) {

		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SUPPORT_CONTACT_INFO,
				WorkRequestInfo.SCHEDULE_INFO),
			ImmutableSet.of(AuthorizationContext.RESOURCE,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN),
			"mobile.ask_question");

		model.addAttribute("title", "Ask a Question");
		model.addAttribute("work", workResponse.getWork());

		return "mobile/pages/assignments/ask_question";
	}

	@RequestMapping(
		value = "/ask_question/{workNumber}",
		method = RequestMethod.POST)
	public String doAskQuestion(
		@PathVariable("workNumber") final String workNumber,
		@RequestParam(required = false) final String question,
		final RedirectAttributes flash,
		final Model model) {

		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SUPPORT_CONTACT_INFO,
				WorkRequestInfo.SCHEDULE_INFO),
			ImmutableSet.of(AuthorizationContext.RESOURCE,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN),
			"mobile.ask_question");

		model.addAttribute("title", "Ask a Question");
		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("is_admin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));

		final MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (StringUtils.isBlank(question)) {
			messageHelper.addError(bundle, "assignments.mobile.ask_question.notempty");
			return "redirect:/mobile/assignments/ask_question/" + workNumber;
		}

		final WorkQuestionAnswerPair saveResponse = workQuestionService
			.saveQuestion(workResponse.getWork().getId(), getCurrentUser().getId(), question);

		if (saveResponse != null) {
			messageHelper.addSuccess(bundle, "assignment.mobile.ask_question.success");
			return "redirect:/mobile/assignments/details/" + workNumber;
		}

		messageHelper.addError(bundle, "assignment.mobile.ask_question.exception");
		return "redirect:mobile/pages/assignments/ask_question/" + workNumber;
	}

	@RequestMapping(
		value = "/confirmation/{workNumber}",
		method = POST)
	public String confirmation(@PathVariable("workNumber") final String workNumber, final RedirectAttributes flash)
		throws WorkUnauthorizedException {

		final MessageBundle bundle = messageHelper.newFlashBundle(flash);
		final Map<String, Object> retMap = doConfirmAssignment(workNumber);

		if (!(boolean) retMap.get("successful")) {
			for (Object error : (ArrayList) retMap.get("errors")) {
				messageHelper.addError(bundle, (String) error);
			}
			return "redirect:/mobile/assignments/details/" + workNumber;
		} else { messageHelper.addSuccess(bundle, "assignment.confirmation.success");
			return "redirect:/mobile/assignments/details/" + workNumber;
		}
	}

	@RequestMapping(
		value = "/checkin/{workNumber}",
		method = POST)
	public String checkin(
		@PathVariable("workNumber") final String workNumber,
		@RequestParam(required = false) final Double latitude,
		@RequestParam(required = false) final Double longitude,
		@RequestParam(required = false) final Double distance,
		final RedirectAttributes flash) {

		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO, WorkRequestInfo.STATUS_INFO),
			ImmutableSet.of(AuthorizationContext.ACTIVE_RESOURCE),
			"mobile.checkin");

		final TimeTrackingRequest checkinRequest = new TimeTrackingRequest()
			.setWorkId(workResponse.getWork().getId())
			.setDate(Calendar.getInstance())
			.setLatitude(latitude)
			.setLongitude(longitude)
			.setDistance(distance);

		final TimeTrackingResponse checkinResponse = tWorkFacadeService.checkInActiveResource(checkinRequest);
		final MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (checkinResponse.isSuccessful()) {
			messageHelper.addSuccess(bundle, "assignment.mobile.checkin.success");
		} else {
			messageHelper.addError(bundle, checkinResponse == null ? "generic.error" : checkinResponse.getMessage());
		}

		return "redirect:/mobile/assignments/details/" + workNumber;
	}

	@RequestMapping(
		value = "/checkout/{workNumber}",
		method = POST)
	public String submitCheckout(
		@PathVariable("workNumber") final String workNumber,
		@RequestParam(required = false) final String noteText,
		@RequestParam(required = false) final Double latitude,
		@RequestParam(required = false) final Double longitude,
		@RequestParam(required = false) final Double distance,
		final RedirectAttributes flash) {

		final MessageBundle bundle = messageHelper.newFlashBundle(flash);
		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO, WorkRequestInfo.STATUS_INFO),
			ImmutableSet.of(AuthorizationContext.ACTIVE_RESOURCE),
			"mobile.checkout");

		final Work work = workResponse.getWork();

		if (work.isCheckoutNoteRequiredFlag() && StringUtils.isBlank(noteText)) {
			messageHelper.addError(bundle, "assignment.add_checkout_note.empty");
			return "redirect:/mobile/assignments/details/" + workNumber;
		}

		final TimeTrackingRequest timeTrackingRequest = new TimeTrackingRequest()
			.setWorkId(work.getId())
			.setDate(Calendar.getInstance())
			.setLatitude(latitude)
			.setLongitude(longitude)
			.setDistance(distance)
			.setNoteOnCheckOut(noteText);

		final TimeTrackingResponse checkoutResponse = tWorkFacadeService
			.checkOutActiveResource(timeTrackingRequest);

		if (!checkoutResponse.isSuccessful()) {
			messageHelper.addError(bundle, checkoutResponse.getMessage());
		}

		return "redirect:/mobile/assignments/details/" + workNumber;
	}

	@RequestMapping(
		value = "/set_appointment/{workNumber}",
		method = GET)
	public String setAppointment(@PathVariable("workNumber") final String workNumber, final Model model) {

		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SUPPORT_CONTACT_INFO,
				WorkRequestInfo.SCHEDULE_INFO),
			ImmutableSet.of(AuthorizationContext.ACTIVE_RESOURCE),
			"mobile.set_appointment");

		model.addAttribute("title", "Set Appointment Time");
		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("set_appointment_form", new WorkSetAppointmentForm());

		return "mobile/pages/assignments/set_appointment";
	}

	@RequestMapping(
		value = "/set_appointment/{workNumber}",
		method = POST)
	public String doSetAppointment(
		@PathVariable("workNumber") final String workNumber,
		@ModelAttribute("set_appointment_form") final WorkSetAppointmentForm form,
		final BindingResult bind,
		final RedirectAttributes flash,
		final Model model) {

		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SUPPORT_CONTACT_INFO,
				WorkRequestInfo.SCHEDULE_INFO),
			ImmutableSet.of(AuthorizationContext.ACTIVE_RESOURCE),
			"mobile.set_appointment");

		model.addAttribute("title", "Set Appointment Time");
		model.addAttribute("work", workResponse.getWork());

		final MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mobile/assignments/set_appointment/" + workNumber;
		}

		return "mobile/pages/assignments/set_appointment";
	}

	@RequestMapping(
		value = "/generate_pdf/{workNumber}",
		method = GET)
	public View generatePdf(@PathVariable("workNumber") final String workNumber, final Model model) {
		return super.generatePdf(workNumber, model);
	}

	@RequestMapping(
		value = "/part_details/{workNumber}",
		method = GET)
	public String parts(@PathVariable("workNumber") final String workNumber, final Model model) {
		final WorkResponse workResponse = getWork(workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO,
				WorkRequestInfo.PARTS_INFO),
			ImmutableSet.of(AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN),
			"mobile.parts");

		final Work work = workResponse.getWork();

		final Set<AuthorizationContext> authContext = workResponse.getAuthorizationContexts();

		model.addAttribute("isOwner", authContext.contains(AuthorizationContext.BUYER));
		model.addAttribute("isAdmin", authContext.contains(AuthorizationContext.ADMIN));
		model.addAttribute("isActiveResource", authContext.contains(AuthorizationContext.ACTIVE_RESOURCE));
		model.addAttribute("title", messageHelper.getMessage("partsAndLogistics.mobile.title"));
		model.addAttribute("work", work);
		model.addAttribute("partGroup", jsonService.toJsonIdentity(work.getPartGroup()));
		model.addAttribute("partsConstantsJson", jsonService.toJsonIdentity(PartDTO.PARTS_CONSTANTS));

		return "mobile/pages/v2/assignments/parts";
	}

	@RequestMapping(
		value = "/parts/{workNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getParts(@PathVariable final String workNumber) throws Exception {

		final AjaxResponseBuilder response = AjaxResponseBuilder
			.fail()
			.setRedirect("/assignments/details/" + workNumber);

		final MessageBundle messageBundle = messageHelper.newBundle();

		List<WorkResponse> workResponses;

		try {
			workResponses = getWorkAndAuthorizeNotCancelled(Lists.newArrayList(workNumber),
				ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO, WorkRequestInfo.PARTS_INFO),
				ImmutableSet.of(AuthorizationContext.BUYER, AuthorizationContext.RESOURCE),
				"partsAndLogistics.assignment");
		} catch (final Exception e) {
			messageHelper.addError(messageBundle, "partsAndLogistics.assignment.not_authorized");
			return response.setMessages(messageBundle.getErrors());
		}

		final Work work = workResponses.get(0).getWork();

		final List<PartDTO> partDTOs = partService.getPartsByGroupUuid(work.getPartGroup().getUuid());

		return response.setSuccessful(true).setData(ImmutableMap.<String, Object>of("parts", partDTOs));
	}

	@RequestMapping(
		value = "/parts/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder savePart(
		@PathVariable final String workNumber,
		@RequestBody final PartDTO partDTO,
		final BindingResult bindingResult) throws Exception {

		final AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		final MessageBundle messageBundle = messageHelper.newBundle();

		List<WorkResponse> workResponses = Lists.newArrayList();

		try {
			workResponses = getWorkAndAuthorizeNotCancelled(Lists.newArrayList(workNumber),
				ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO, WorkRequestInfo.PARTS_INFO),
				ImmutableSet.of(AuthorizationContext.BUYER, AuthorizationContext.ACTIVE_RESOURCE),
				"partsAndLogistics.assignment");

		} catch (final Exception e) {
			messageHelper.addError(messageBundle, "partsAndLogistics.assignment.add.not_authorized");
		}

		partValidator.validate(partDTO, bindingResult);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messageBundle, bindingResult);
			return response.setMessages(messageBundle.getErrors());
		}

		final Work work = workResponses.get(0).getWork();
		final PartDTO savedPart = partService.saveOrUpdatePart(partDTO, work.getPartGroup().getUuid());

		if (savedPart != null) {

			return response.setSuccessful(true).setData(ImmutableMap.<String, Object>of("part", savedPart));
		}

		return response;
	}

	public Map<String, Object> getList(final WorkStatusType statusType, final Integer page) {
		return getList(statusType, page, null, "");
	}

	public Map<String, Object> getList(
		final WorkStatusType statusType,
		final Integer page,
		final Integer pageSize,
		final String sort) {

		final DashboardResponse dashboardResponse =
			mobileDashboardService.getAssignmentListByStatus(getCurrentUser(), statusType, page, pageSize, sort);
		final DashboardResultList results = dashboardResponse.getDashboardResultList();
		final List<Map<String, Object>> rows = mobileDashboardService.parseResults(getCurrentUser(), results);
		final Map<String, Object> pagination = new ImmutableMap.Builder<String, Object>()
			.put("page", page)
			.put("url", String.format("/mobile/assignments/list/%s/%d", statusType.getCode(), page + 1))
			.put("hasMore", page < results.getTotalNumberOfPages())
			.put("totalPages", results.getTotalNumberOfPages())
			.put("totalResults", results.getTotalResults())
			.put("pageSize", (pageSize == null) ? mobileDashboardService.DEFAULT_ASSIGNMENT_LIST_PAGE_SIZE : pageSize)
			.build();

		return ImmutableMap.of("rows", rows, "pagination", pagination);
	}

	public Map<String, Object> getWorkFeed(final FeedRequestParams params) {

		Feed feed;

		try {
			feed = feedController.firehose(params);
		} catch (final Exception ex) {
			return null;
		}

		final List<Map<String, Object>> rows = mobileDashboardService.parseResults(getCurrentUser(), feed);

		final Map<String, Object> pagination = ImmutableMap.<String, Object>of(
			"hasMore", feed.hasMorePages(),
			"totalResults", new Long(feed.getTotalCount()).intValue(),
			"totalPages", new Double(Math.floor((feed.getTotalCount() - 1) / feed.getPageSize()) + 1).intValue(),
			"page", new Double(feed.getPage() + 1).intValue(),
			"pageSize", feed.getPageSize());

		return ImmutableMap.of("rows", rows, "pagination", pagination);
	}

	public Map<String, Object> getWorkFeed(
		final String keyword,
		final Integer industryId,
		final String latitude,
		final String longitude,
		final Double radius,
		final Boolean virtual,
		final String[] requestedFields,
		final Integer page,
		final Integer pageSize) {

		final Feed feed = mobileDashboardService
			.getWorkFeed(getCurrentUser().getId(), latitude, longitude, page, pageSize);

		final List<Map<String, Object>> rows = mobileDashboardService.parseResults(getCurrentUser(), feed);

		final Map<String, Object> pagination = ImmutableMap.<String, Object>of(
			"hasMore", feed.hasMorePages(),
			"totalResults", new Long(feed.getTotalCount()).intValue(),
			"totalPages", new Double(Math.floor((feed.getTotalCount() - 1) / feed.getPageSize()) + 1).intValue(),
			"page", page,
			"pageSize", feed.getPageSize());

		return ImmutableMap.of("rows", rows, "pagination", pagination);
	}

	public WorkResponse getWorkDetails(final String workNumber) {
		final WorkRequest workRequest = new WorkRequest()
			.setUserId(getCurrentUser().getId())
			.setWorkNumber(workNumber)
			.setViewType(ViewType.MOBILE);

		final WorkResponse workResponse;

		try {
			workResponse = tWorkFacadeService.findWorkDetail(workRequest);
		} catch (final Exception e) {
			throw new MobileHttpException404().setMessageKey("assignment.mobile.notfound");
		}

		if (workResponse.getRequestContexts().contains(RequestContext.UNRELATED) && !getCurrentUser().isInternal()) {
			throw new MobileHttpException401().setMessageKey("assignment.mobile.notallowed");
		}

		return workResponse;
	}

	private void getCustomFieldConfiguration(final Model model,
											 final Work work) {

		final CustomFieldGroupSet customFieldGroupSet = new CustomFieldGroupSet();
		customFieldGroupSet.setCustomFieldGroupSet(work.getCustomFieldGroups());

		if (CollectionUtils.isNotEmpty(work.getCustomFieldGroups())) {
			final Collection<CustomField> headerDisplayFields = Lists.newArrayList();
			for (CustomFieldGroup fieldGroup : work.getCustomFieldGroups()) {
				if (CollectionUtils.isEmpty(fieldGroup.getFields())) {
					continue;
				}
				for (final CustomField field : fieldGroup.getFields()) {
					if (field == null) {
						continue;
					}
					if (field.isShowInAssignmentHeader()) {
						headerDisplayFields.add(field);
					}
				}
			}

			model.addAttribute("headerDisplayFields", headerDisplayFields);
		}

		model.addAttribute("hasResourceCustomFields", customFieldGroupSet.hasResourceFields());
		model.addAttribute("hasBuyerCustomFields", customFieldGroupSet.hasBuyerFields());
		model.addAttribute("hasBuyerFieldsVisibleToResource", customFieldGroupSet.hasBuyerFieldsVisibleToResourceOnSentStatus());
		model.addAttribute("hasRequiredResourceFields", customFieldGroupSet.hasRequiredResourceFields());
	}

	private String getNicePricingType(final String pricingTypeCode) {

		if (StringUtils.isBlank(pricingTypeCode)) {
			return StringUtils.EMPTY;
		}

		switch (pricingTypeCode) {
			case "FLAT":
				return "Flat";
			case "PER_HOUR":
				return "Hourly";
			case "BLENDED_PER_HOUR":
				return "Blended";
			case "PER_UNIT":
				return "Unit";
			case "INTERNAL":
				return "Internal";
			default:
				return StringUtils.EMPTY;
		}
	}
}
