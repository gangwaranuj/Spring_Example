package com.workmarket.web.controllers.assignments;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.configuration.Constants;
import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.compliance.service.ComplianceService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.customfield.BulkSaveCustomFieldsRequest;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.AvoidScheduleConflictsModelRope;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectPagination;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.DocumentService;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.actions.AddAttachmentsWorkEvent;
import com.workmarket.domains.work.service.actions.AddNotesWorkEvent;
import com.workmarket.domains.work.service.actions.BulkCancelWorksEvent;
import com.workmarket.domains.work.service.actions.BulkEditClientProjectEvent;
import com.workmarket.domains.work.service.actions.BulkLabelRemovalEvent;
import com.workmarket.domains.work.service.actions.GetAttachmentsEvent;
import com.workmarket.domains.work.service.actions.RemoveAttachmentsEvent;
import com.workmarket.domains.work.service.actions.RescheduleEvent;
import com.workmarket.domains.work.service.actions.WorkEventFactory;
import com.workmarket.domains.work.service.actions.WorkEventService;
import com.workmarket.domains.work.service.actions.WorkListFetcherService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.SpecialtyService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.asset.AssetAssignmentBundle;
import com.workmarket.service.business.asset.AssetBundlerQueue;
import com.workmarket.service.business.asset.AssetBundlerService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.CancelWorkNumbersDTO;
import com.workmarket.service.business.dto.ClientCompanyDTO;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.business.dto.ProjectDTO;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.dto.RemoveLabelsDTO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkRatingDTO;
import com.workmarket.service.business.event.BulkSaveCustomFieldsEvent;
import com.workmarket.service.business.event.asset.DeleteDeliverableEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.rating.RatingValidatingService;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.RatingException;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.thrift.ThriftUtilities;
import com.workmarket.thrift.core.DeliverableAsset;
import com.workmarket.thrift.search.cart.SearchCart;
import com.workmarket.thrift.search.cart.UserNotFoundException;
import com.workmarket.thrift.work.AcceptWorkOfferRequest;
import com.workmarket.thrift.work.AddResourcesToWorkResponse;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.CustomFieldGroupSet;
import com.workmarket.thrift.work.DeclineWorkActionType;
import com.workmarket.thrift.work.DeclineWorkOfferRequest;
import com.workmarket.thrift.work.RequestContext;
import com.workmarket.thrift.work.ResourceNoteActionType;
import com.workmarket.thrift.work.ResourceNoteRequest;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkActionResponse;
import com.workmarket.thrift.work.WorkActionResponseCodeType;
import com.workmarket.thrift.work.WorkQuestionRequest;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.FileUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.forms.assignments.FeedbackForm;
import com.workmarket.web.forms.assignments.RemoveLabelForm;
import com.workmarket.web.forms.assignments.WorkCompleteAndApproveForm;
import com.workmarket.web.forms.assignments.WorkCompleteForm;
import com.workmarket.web.forms.assignments.WorkMassRescheduleForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.BulkActionAjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.ValidationMessageHelper;
import com.workmarket.web.helpers.WorkDetailsControllerHelperService;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.models.WorkNumbers;
import com.workmarket.web.validators.CancelWorkValidator;
import com.workmarket.web.validators.DeliverableValidator;
import com.workmarket.web.validators.PartValidator;
import com.workmarket.web.validators.RejectDeliverableValidator;
import com.workmarket.web.validators.UpdateDocumentVisibilityValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.workmarket.utility.CollectionUtilities.isEmpty;
import static com.workmarket.utility.CollectionUtilities.newObjectMap;
import static com.workmarket.utility.CollectionUtilities.newStringMap;
import static com.workmarket.utility.StringUtilities.pluralize;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/assignments")
public class WorkDetailsController extends BaseWorkController {

	private static final Logger logger = LoggerFactory.getLogger(WorkDetailsController.class);
	@Autowired private JsonSerializationService jsonService;
	@Autowired private SearchCart.Iface cartServiceInterface;
	@Autowired private WorkPayController workPayController;
	@Autowired private AssetBundlerQueue assetBundler;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private AssetBundlerService assetBundlerService;
	@Autowired private WorkEventFactory workEventFactory;
	@Autowired private WorkEventService workEventService;
	@Autowired private WorkListFetcherService workListFetcherService;
	@Autowired private RatingValidatingService ratingValidatingService;
	@Autowired private FormOptionsDataHelper formOptionsDataHelper;
	@Autowired private CancelWorkValidator cancelWorkValidator;
	@Autowired private RejectDeliverableValidator rejectDeliverableValidator;
	@Autowired private DeliverableValidator deliverableValidator;
	@Autowired private WorkValidationService workValidationService;
	@Autowired private ServiceMessageHelper serviceMessageHelper;
	@Autowired private EventRouter eventRouter;
	@Autowired private UpdateDocumentVisibilityValidator updateDocumentVisibilityValidator;
	@Autowired private DeliverableService deliverableService;
	@Autowired private DocumentService documentService;
	@Autowired private ComplianceService complianceService;
	@Autowired private PartService partService;
	@Autowired private PartValidator partValidator;
	@Autowired private WorkDetailsControllerHelperService workDetailsControllerHelperService;
	@Autowired private VendorService vendorService;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired SkillService skillService;
	@Autowired SpecialtyService specialtyService;
	@Autowired WebRequestContextProvider webRequestContextProvider;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Qualifier("avoidScheduleConflictsModelDoorman")
	@Autowired private Doorman doorman;
	private WMMetricRegistryFacade wmMetricRegistryFacade;
	private static final String COMPANY_PAGES_SPA_FEATURE = "companyPagesSPA";

	@PostConstruct
	private void postContstruct() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "workdetailscontroller");
	}

	@RequestMapping(
		value = "/details/{workNumber}",
		method = GET)
	public String showDetails(
		@PathVariable String workNumber,
		HttpServletRequest request,
		ModelMap model,
		SitePreference site,
		RedirectAttributes flash) {

		if (isMobile(request, site)) {
			return "redirect:/mobile/assignments/details/{workNumber}";
		}

		if (workBundleService.isAssignmentBundleLight(workNumber)) {
			flash.addFlashAttribute("bundle", model.get("bundle"));
			// temp fix. view_bundle should work off of workNumber
			Long workId = workService.findWorkId(workNumber);
			return "redirect:/assignments/view_bundle/" + workId;
		}

		final WorkResponse workResponse = getWorkForWorkDetails(workNumber);
		detailsModelHelper(workNumber, model, request, workResponse);

		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/assignment/details"));
		model.addAttribute("email", getCurrentUser().getEmail());
		model.addAttribute("assignmentsPageTitle", getCurrentUser().isSeller() ? "My Work" : "Assignment Dashboard");
		model.addAttribute("companyPagesSPAEnabled", hasFeature(getCurrentUser().getCompanyId(), COMPANY_PAGES_SPA_FEATURE));

		return "web/pages/assignments/details";
	}

	@RequestMapping(
		value = "/details/{workNumber}/description",
		method = GET,
		produces = "text/html")
	public @ResponseBody String showDescription(
		@PathVariable String workNumber,
		HttpServletResponse response) {

		WorkResponse work = getWorkForWorkDetails(workNumber);
		response.setContentType("text/html");
		return work.getWork().getDescription();
	}

	@RequestMapping(
		value = "/attachment_description/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder addAttachmentDescription(
		@RequestParam String description,
		@PathVariable Long id) throws Exception {

		AssetDTO assetDTO = AssetDTO.newAssetDTO()
			.setAssetId(id)
			.setDescription(description);
		assetManagementService.updateAsset(id, assetDTO);
		return AjaxResponseBuilder.success().setData(ImmutableMap.<String, Object>of("description", description, "id", id));
	}

	/**
	 * As a resource, block company owning the assignment
	 */
	@RequestMapping(
		value = "/block_client/{workNumber}",
		method = POST)
	public String blockClientByAssignment(
		@PathVariable String workNumber,
		RedirectAttributes redirectAttributes) throws Exception {

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(WorkRequestInfo.COMPANY_INFO));

		final Work work = workResponse.getWork();
		final Long
			userId = getCurrentUser().getId(),
			clientId = work.getCompany().getId();
		final String clientName = work.getCompany().getName();

		try {
			userService.blockCompany(userId, clientId);
			messageHelper.addSuccess(messages, "blockclient.success", clientName, "/relationships/blocked_clients");
			messageHelper.addSuccess(messages, "assignment.decline.bulk.success", clientName);
		} catch (IllegalArgumentException i) {
			messageHelper.addError(messages, i.getMessage(), clientName);
		} catch (Exception e) {
			messageHelper.addError(messages, "blockclient.error", clientName);
		}

		return "redirect:/assignments";
	}

	@RequestMapping(
		value = "/unblock_client/{workNumber}",
		method = GET)
	public String unblockClientByAssignment(
		@PathVariable String workNumber,
		RedirectAttributes redirectAttributes) throws Exception {

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);

		final Long
			clientId = getWork(workNumber, ImmutableSet.of(WorkRequestInfo.COMPANY_INFO))
			.getWork().getCompany().getId(),
			userId = getCurrentUser().getId();

		try {
			userService.unblockCompany(userId, clientId);
			messageHelper.addSuccess(messages, "unblockclient.success");
		} catch (Exception e) {
			messageHelper.addError(messages, "unblockclient.exception");
		}

		return "redirect:/assignments/details/" + workNumber;
	}

	/**
	 * Download work schedule as an ICS file.
	 */
	@RequestMapping(
		value = "/download_ics/{workNumber}",
		method = GET)
	public void downloadIcs(@PathVariable("workNumber") String workNumber, HttpServletResponse httpResponse) {

		Long workId = workService.findWorkId(workNumber);
		if (workId == null) {
			return;
		}

		List<WorkContext> context = workService.getWorkContext(workId, getCurrentUser().getId());

		if (!CollectionUtilities.containsAny(context, WorkContext.OWNER, WorkContext.ACTIVE_RESOURCE, WorkContext.INVITED)) {
			return;
		}

		try {
			String filename = workService.createCalendar(getCurrentUser().getId(), workId);
			FileUtils.copyFile(new File(filename), httpResponse.getOutputStream());

			httpResponse.setContentType(MimeType.ICS.getMimeType());
			httpResponse.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.ics\"", workNumber));
		} catch (Exception e) {
			logger.error(String.format("Error downloading .ics for %s: ", workNumber), e);
		}
	}

	/**
	 * NOTE: this ONLY supports calling it via AJAX. Doesn't seem to be used any other way, though the PHP implements a regular GET.
	 */
	@RequestMapping(
		value = "/add_note/{workNumber}",
		method = POST)
	public @ResponseBody AjaxResponseBuilder submitAddNote(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "model", required = false) String model,
		@RequestParam(value = "content", required = false) String content,
		@RequestParam(value = "is_private", required = false) String isPrivate,
		@RequestParam(value = "is_privileged", required = false) boolean isPrivileged,
		@RequestParam(value = "company_name", required = false) String companyName,
		@RequestParam(value = "where", required = false) String where) {

		WorkResponse workResponse = getWork(workNumber, Sets.newHashSet(
				WorkRequestInfo.CONTEXT_INFO
		), Sets.newHashSet(
				AuthorizationContext.RESOURCE,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
		), "add_note");

		NoteDTO dto;
		if (StringUtils.isNotEmpty(model)) {
			String dtoData = StringEscapeUtils.unescapeHtml4(model);
			dto = jsonService.fromJson(dtoData, NoteDTO.class);
		} else {
			dto = new NoteDTO();
			dto.setContent(content);
			dto.setIsPrivate("1".equals(isPrivate));
			dto.setPrivileged(isPrivileged);
		}

		AjaxResponseBuilder ajaxResponseBuilder = AjaxResponseBuilder.fail();
		MessageBundle bundle = messageHelper.newBundle();

		if (isBlank(dto.getContent())) {
			messageHelper.addError(bundle, "assignment.add_note.empty");
			return ajaxResponseBuilder.setMessages(bundle.getErrors());
		}

		Note note;
		String replyToName = "";
		try {
			note = workNoteService.addNoteToWork(workResponse.getWork().getId(), dto);
			if (note.getReplyToId() != null) {
				Map<String, Object> propMap = userService.getProjectionMapById(note.getReplyToId(), "firstName", "lastName");
				replyToName = StringUtilities.fullName((String) propMap.get("firstName"), (String) propMap.get("lastName"));
			}
		} catch (Exception e) {
			logger.error(String.format("Unable to add note assignment id=%s, user=%d", workNumber, getCurrentUser().getId()), e);
			messageHelper.addError(bundle, "assignment.add_note.exception");
			return ajaxResponseBuilder
				.setMessages(bundle.getErrors())
				.setRedirect("/assignments/details/" + workNumber);
		}

		where = StringUtilities.defaultString(where, "details");
		String redirect = workResponse.isWorkBundle() ? String.format("/view_bundle/%s", workResponse.getWork().getId()) : String.format("/assignments/%s/%s", where, workNumber);

		messageHelper.addSuccess(bundle, "assignment.add_note.success");
		return ajaxResponseBuilder
			.setSuccessful(true)
			.setMessages(bundle.getSuccess())
			.setRedirect(redirect)
			.addData("id", note.getId())
			.addData("content", note.getContent())
			.addData("is_private", note.getIsPrivate())
			.addData("is_privileged", note.getIsPrivileged())
			.addData("reply_to_name", replyToName)
			.addData("company_name", companyName)
			.addData("is_resource", note.getReplyToId() != null && getCurrentUser().getId().equals(note.getReplyToId()))
			.addData("creator", getCurrentUser().getFullName())
			.addData("created_on", DateUtilities.format("MM/dd/yy h:mmaa z", note.getCreatedOn(), getCurrentUser().getTimeZoneId()));
	}

	@RequestMapping(
		value = "/add_notes",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder submitAddNotesNew(
		@RequestParam(value = "model", required = false) String model) {

		Type hashMapType = new TypeToken<HashMap<String, String>>() {}.getType();
		String mapData = StringEscapeUtils.unescapeHtml4(model);
		HashMap<String, String> jsonMap = jsonService.fromJson(mapData, hashMapType);
		String workNumbersArray[] = jsonService.fromJson("[" + jsonMap.get("assignment_ids") + "]", String[].class);

		AddNotesWorkEvent addNotesWorkEvent = workEventFactory.createAddNotesWorkAction(Arrays.asList(workNumbersArray),
				userService.findUserById(getCurrentUser().getId()),
				"add_note",
				"assignment.add_note",
				jsonMap.get("content"),
				"1".equals(jsonMap.get("is_private")));

		return workEventService.doAction(addNotesWorkEvent);
	}


	/**
	 * Quickform for creating assignment notes
	 */
	@RequestMapping(
		value = "/add_note_form",
		method = POST)
	public String addNoteForm(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "content", required = false) String content,
		@RequestParam(value = "is_private", required = false) boolean isPrivate,
		RedirectAttributes model) {

		MessageBundle bundle = messageHelper.newBundle();

		if (isBlank(workNumber)) {
			messageHelper.addError(bundle, "assignment.add_note.missing.id");
			model.addAttribute("bundle", bundle);
			return "redirect:/assignments";
		}

		NoteDTO note = new NoteDTO();
		note.setContent(content);
		note.setIsPrivate(isPrivate);

		if (isBlank(note.getContent())) {
			messageHelper.addError(bundle, "NotEmpty", "Note");
			model.addAttribute("bundle", bundle);
			return "redirect:/assignments/details/" + workNumber;
		}

		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (!(work instanceof com.workmarket.domains.work.model.Work)) {
			messageHelper.addError(bundle, "assignment.add_note.missing.work");
			return "redirect:/assignments";
		}

		try {
			workNoteService.addNoteToWork(work.getId(), note);
			messageHelper.addSuccess(bundle, "assignment.add_note.success");
		} catch (Exception ex) {
			logger.error("failed to add a note for workNumber={}, content={}, and isPrivate={}",
				workNumber, content, isPrivate);
			messageHelper.addError(bundle, "assignment.add_note.problem");
		}

		model.addAttribute("bundle", bundle);
		return "redirect:/assignments/details/" + workNumber;
	}


	@RequestMapping(
		value = "/accept/{workNumber}",
		method = GET)
	public String accept(@PathVariable("workNumber") String workNumber, HttpServletRequest request, RedirectAttributes flash, Model model, SitePreference site) {

		MessageBundle bundle = messageHelper.newBundle();
		flash.addFlashAttribute("bundle", bundle);

		if (!StringUtils.isNumeric(workNumber)) {
			messageHelper.addError(bundle, "assignment.accept.notavailable");
			return isMobile(request, site) ? "redirect:/mobile/assignments/list/available" : "redirect:/assignments";
		}

		Long userId = getCurrentUser().getId();
		User user = userService.getUserWithRoles(userId);

		if (authenticationService.isSuspended(user)) {
			messageHelper.addError(bundle, "assignment.accept.suspended");
			return isMobile(request, site) ? "redirect:/mobile" : "redirect:/home";
		}

		AbstractWork work = getWorkByNumber(workNumber);
		if (work == null) {
			messageHelper.addError(bundle, "assignment.accept.notavailable");
			return isMobile(request, site) ? "redirect:/mobile/assignments/list/available" : "redirect:/assignments";
		}

		Compliance compliance = complianceService.getComplianceFor(user, work);
		if (!compliance.isCompliant()) {
			messageHelper.addError(bundle, "assignment.compliance.user_accept_not_allowed",
				work.getCompany() != null ? work.getCompany().getEffectiveName() : "unknown");
			return isMobile(request, site) ? "redirect:/mobile/assignments/list/available" : "redirect:/assignments";
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
			return isMobile(request, site) ? "redirect:/mobile/assignments/list/available" : "redirect:/assignments";
		}

		Long workCompanyId = work.getCompany() != null ? work.getCompany().getId() : null;

		if (!workValidationService.isWorkResourceValidForWork(getCurrentUser().getId(), getCurrentUser().getCompanyId(), workCompanyId)) {
			messageHelper.addError(bundle, "assignment.accept.invalid_resource", "You", "are", "you", work.getCompany().getEffectiveName());
			return isMobile(request, site) ? "redirect:/mobile/assignments/list/available" : "redirect:/assignments";
		}

		// check if work has been accepted yet
		Long workId = work.getId();
		Long numAccepted = numWorkByResourceStatus(workId, WorkResourceStatusType.ACCEPTED);

		if (numAccepted > 0) {
			messageHelper.addError(bundle, "assignment.accept.closed");
			return isMobile(request, site) ? "redirect:/mobile/assignments/list/available" : "redirect:/assignments";
		}

		// check if the work has been closed

		if (!work.isSent()) {
			messageHelper.addError(bundle, "assignment.accept.notopen");
			return (isMobile(request, site) ? "redirect:/mobile/assignments/details/" : "redirect:/assignments/details/") + workNumber;
		}

		// check if this user is trying to accept his own work
		boolean isResource = workService.isUserWorkResourceForWork(userId, workId);

		List<WorkContext> context = workService.getWorkContext(workId, userId);

		if (!isResource && context.contains(WorkContext.OWNER)) {
			messageHelper.addError(bundle, "assignment.accept.own");
			return (isMobile(request, site) ? "redirect:/mobile/assignments/details/" : "redirect:/assignments/details/") + workNumber;
		}

		try {
			AcceptWorkResponse acceptanceResponse = tWorkFacadeService.acceptWork(userId, workId);

			if (acceptanceResponse.isSuccessful()) {
				messageHelper.addSuccess(bundle, "assignment.accept.success");
			} else {
				messageHelper.addError(bundle, StringUtils.join(acceptanceResponse.getMessages(), "\n"));
			}
		} catch (Exception e) {
			logger.error(String.format("Error accepting work [userId=%s, workId=%s]", userId, workNumber), e);
			messageHelper.addError(bundle, "assignment.accept.exception");
		}

		/* TODO: this needs to be moved into the service - this catch block is only to handle a transaction error under very high load */
		try {
			sendAcceptedWorkDetailsPDFtoResource(workNumber, user.getId(), request, model);
		} catch (Exception e) {
			logger.error(String.format("Error generating PDF for work [userId=%s, workId=%s]", userId, workNumber), e);
		}

		return (isMobile(request, site) ? "redirect:/mobile/assignments/details/" : "redirect:/assignments/details/") + workNumber;
	}


	@RequestMapping(
		value = "/reject/{workNumber}",
		method = GET)
	public String reject(@PathVariable("workNumber") String workNumber, RedirectAttributes redirectAttributes, HttpServletRequest request, SitePreference site) {

		MessageBundle bundle = messageHelper.newBundle();
		redirectAttributes.addFlashAttribute("bundle", bundle);

		AbstractWork work = getWorkByNumber(workNumber);

		if (work == null) {
			messageHelper.addError(bundle, "assignment.decline.invalid_work");
			return (isMobile(request, site)) ? "redirect:/mobile/" : "redirect:/assignments";
		}

		if (!work.isSent()) {
			messageHelper.addError(bundle, "assignment.decline.invalid_status");
			return (isMobile(request, site)) ? "redirect:/mobile/assignments/details/" + workNumber : "redirect:/assignments/details/" + workNumber;
		}

		List<WorkContext> context = workService.getWorkContext(work.getId(), getCurrentUser().getId());
		if (context.contains(WorkContext.OWNER)) {
			messageHelper.addError(bundle, "assignment.decline.not_owner");
			return (isMobile(request, site)) ? "redirect:/mobile/assignments/details/" + workNumber : "redirect:/assignments/details/" + workNumber;
		}

		try {
			workService.declineWork(getCurrentUser().getId(), work.getId());
			messageHelper.addSuccess(bundle, "assignment.decline.success");
			return (isMobile(request, site)) ? "redirect:/mobile/" : "redirect:/assignments";

		} catch (Exception e) {
			logger.error("", e);
			messageHelper.addError(bundle, "assignment.decline.exception");
			return (isMobile(request, site)) ? "redirect:/mobile/assignments/details/" + workNumber : "redirect:/assignments/details/" + workNumber;
		}
	}

	@RequestMapping(
		value = "/vendor/reject/{workNumber}",
		method = POST)
	@ResponseBody
	public Map<String, Object> vendorReject(@PathVariable String workNumber) {
		String message = null;
		if (isBlank(workNumber)) {
			message = messageHelper.getMessage("assignment.complete.notfound");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		Long workId = workService.findWorkId(workNumber);
		if (workId == null) {
			message = messageHelper.getMessage("assignment.complete.notfound");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		AbstractWork work = getWorkByNumber(workNumber);

		if (work == null) {
			message = messageHelper.getMessage("assignment.decline.invalid_work");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		if (!work.isSent()) {
			message = messageHelper.getMessage("assignment.decline.invalid_status");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		Long currentUserId = getCurrentUser().getId();
		Long currentUserCompanyId = getCurrentUser().getCompanyId();

		vendorService.declineWork(workId, currentUserCompanyId, currentUserId);
		List<Long> indexIds = Lists.newArrayList(workId);
		if (workBundleService.isAssignmentBundle(workId)) {
			indexIds.addAll(workBundleService.getAllWorkIdsInBundle(workId));
		}
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(indexIds));

		message = messageHelper.getMessage("assignment.decline.success");
		return CollectionUtilities.newObjectMap(
			"success", message
		);
	}

	@RequestMapping(
		value = "/reject/{workNumber}",
		method = POST)
	@ResponseBody
	public Map<String, Object> workerReject(@PathVariable String workNumber) {
		String message = null;
		if (isBlank(workNumber)) {
			message = messageHelper.getMessage("assignment.complete.notfound");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		Long workId = workService.findWorkId(workNumber);
		if (workId == null) {
			message = messageHelper.getMessage("assignment.complete.notfound");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		AbstractWork work = getWorkByNumber(workNumber);

		if (work == null) {
			message = messageHelper.getMessage("assignment.decline.invalid_work");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		if (!work.isSent()) {
			message = messageHelper.getMessage("assignment.decline.invalid_status");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		try {
			workService.declineWork(getCurrentUser().getId(), work.getId());
			message = messageHelper.getMessage("assignment.decline.success");
			List<Long> indexIds = Lists.newArrayList(workId);
			if (workBundleService.isAssignmentBundle(workId)) {
				indexIds.addAll(workBundleService.getAllWorkIdsInBundle(workId));
			}
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(indexIds));
			return CollectionUtilities.newObjectMap(
				"success", message
			);
		} catch (Exception e) {
			logger.error("", e);
		}

		message = messageHelper.getMessage("assignment.decline.exception");
		return CollectionUtilities.newObjectMap(
			"error", message
		);
	}

	@RequestMapping(
		value = "/workNotify/{workNumber}",
		method = POST)
	@ResponseBody
	public Map<String, Object> workNotify(@PathVariable String workNumber) {
		String message = null;
		if (isBlank(workNumber)) {
			message = messageHelper.getMessage("assignment.complete.notfound");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (!workService.isWorkNotifyAllowed(work.getId())) {
			message = messageHelper.getMessage("assignment.workNotify.not_allowed");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		if (!workService.isWorkNotifyAvailable(work.getId())) {
			message = messageHelper.getMessage("assignment.workNotify.not_available");
			return CollectionUtilities.newObjectMap(
				"error", message
			);
		}

		try {
			workService.workNotifyResourcesForWork(work.getId());
		} catch (OperationNotSupportedException ex) {
			logger.error("Failed to notify invited work resource with id= " + workNumber + " for work.", ex);
			message = messageHelper.getMessage("assignment.workNotify.exception");
			return CollectionUtilities.newObjectMap(
				"success", message
			);
		}

		message = messageHelper.getMessage("assignment.workNotify.success");
		return CollectionUtilities.newObjectMap(
			"success", message
		);
	}

	// AJAX controller to add a client company from assignment creation page.
	@RequestMapping(
		value = "/addclientcompany",
		method = POST,
		headers = "accept=application/json",
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> addClientCompany(@RequestParam(value = "data", required = false) String clientData) {
		Map<String, Object> data = Collections.emptyMap();
		MessageBundle bundle = messageHelper.newBundle();
		Boolean successful = Boolean.FALSE;

		if (isBlank(clientData)) {
			messageHelper.addError(bundle, "assignment.add_client_company.missing.address");
		} else {
			// Unserialize the new client company fields.
			Map<String, String> params = newStringMap();
			for (NameValuePair pair : URLEncodedUtils.parse(clientData, Charset.forName("UTF-8"))) {
				params.put(pair.getName(), pair.getValue());
			}

			ClientCompanyDTO company = new ClientCompanyDTO();
			company.setName(params.get("newclient[name]"));
			company.setIndustryId(Long.parseLong(params.get("industry_id")));
			company.setCustomerId(params.get("customer_id"));
			company.setRegion(params.get("region"));
			company.setDivision(params.get("division"));
			company.setWebsite(params.get("website"));
			company.setPhoneNumber(params.get("client-phone"));
			company.setPhoneExtension(params.get("client-phone-ext"));

			if (isBlank(company.getName())) {
				messageHelper.addError(bundle, "NotEmpty", "Name");
			} else if (company.getName().length() > 100) {
				messageHelper.addError(bundle, "Max", "Name", "100");
			}

			if (!bundle.hasErrors()) {
				try {
					ClientCompany clientCompany = crmService.saveOrUpdateClientCompany(getCurrentUser().getId(), company, null);

					data = CollectionUtilities.newObjectMap(
						"id", clientCompany.getId(),
						"name", clientCompany.getName()
					);

					successful = Boolean.TRUE;
				} catch (Exception ex) {
					if (logger.isErrorEnabled()) {
						logger.error("error saving client company: {} for userId={}",
							new Object[]{ToStringBuilder.reflectionToString(company), getCurrentUser().getId()}, ex);
					}
				}
			}
		}

		return CollectionUtilities.newObjectMap(
			"successful", successful,
			"errors", bundle.getErrors(),
			"data", data
		);
	}


	// Add a new client project.
	@RequestMapping(
		value = "/add_new_project",
		method = POST,
		headers = "accept=application/json",
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> addNewProject(
		@RequestParam(value = "data", required = false) String projectData) {
		// Initialize data.
		MessageBundle bundle = messageHelper.newBundle();
		Map<String, Object> data = Collections.emptyMap();
		Boolean successful = Boolean.FALSE;

		if (StringUtils.isNotBlank(projectData)) {
			// Unserialize the new project fields.
			Map<String, String> params = newStringMap();
			for (NameValuePair pair : URLEncodedUtils.parse(projectData, Charset.forName("UTF-8"))) {
				params.put(pair.getName(), pair.getValue());
			}

			ProjectDTO projectDTO = new ProjectDTO();
			projectDTO.setName(params.get("newproject[name]"));
			projectDTO.setDescription(params.get("newproject[description]"));
			projectDTO.setReservedFundsEnabled(params.get("newproject[enable_reserved_funds]") != null);

			String dueDate = params.get("newproject[due_date]");

			if (StringUtils.isNotBlank(dueDate)) {
				projectDTO.setDueDate(DateUtilities.formatDateStringToISO8601(dueDate));
			}

			String clientCompanyId = params.get("newproject[client]");


			if (StringUtils.isNumeric(clientCompanyId)) {
				projectDTO.setClientCompanyId(Long.valueOf(clientCompanyId));
			} else {
				messageHelper.addError(bundle, "NotEmpty", "Client Company");
			}

			String owner = params.get("newproject[owner]");
			Long ownerId = StringUtils.isNumeric(owner) ? Long.valueOf(owner) : null;

			if (ownerId == null) {
				messageHelper.addError(bundle, "NotEmpty", "Project Owner");
			}

			if (isBlank(projectDTO.getName())) {
				messageHelper.addError(bundle, "NotEmpty", "Project Title");
			} else {
				if (!StringUtilities.stripXSSAndEscapeHtml(projectDTO.getName()).equals(projectDTO.getName()) ||
					!StringUtilities.stripHTML(projectDTO.getName()).equals(projectDTO.getName())) {
					messageHelper.addError(bundle, "InvalidCharacters", "Project Title");
				}
			}

			if (isBlank(projectDTO.getDescription())) {
				messageHelper.addError(bundle, "NotEmpty", "Description");
			}

			if (bundle.hasErrors()) {
				return CollectionUtilities.newObjectMap(
					"successful", false,
					"errors", bundle.getErrors(),
					"data", data
				);
			}

			try {
				Project project = projectService.saveOrUpdateProject(ownerId, projectDTO);

				data = CollectionUtilities.newObjectMap(
					"id", project.getId(),
					"name", project.getName()
				);

				successful = Boolean.TRUE;
			} catch (Exception ex) {
				logger.error("error adding new project for data={}", new Object[]{data}, ex);
				messageHelper.addError(bundle, "assignment.add_new_project.error");
			}
		}

		return CollectionUtilities.newObjectMap(
			"successful", successful,
			"errors", bundle.getErrors(),
			"data", data
		);
	}

	@RequestMapping(
		value = "/rate_assignment",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder saveSinglePendingResourceRatingData(
		@RequestParam long workId,
		@RequestParam long ratedUserId,
		@RequestParam int value,
		@RequestParam int quality,
		@RequestParam int professionalism,
		@RequestParam int communication,
		@RequestParam String review) {

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setRedirect("/assignments")
			.setSuccessful(false);

		final long raterUserId = getCurrentUser().getId();
		final WorkRatingDTO workRatingDTO = new WorkRatingDTO(raterUserId, ratedUserId, workId, value, quality, professionalism, communication, review);

		try {
			ratingService.rateWork(workRatingDTO);
		} catch (RatingException ex) {
			messageHelper.addMessage(response, "rating.post.error");
			return response.setSuccessful(true);
		}

		messageHelper.addMessage(response, "rating.flag.single_success");
		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/submit_feedback/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder submitFeedback(
		@Valid FeedbackForm form,
		BindingResult bindingResult,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return AjaxResponseBuilder.fail()
				.setMessages(bundle.getAllMessages());
		}

		Long raterUserId = getCurrentUser().getId();
		RatingDTO ratingDTO = form.getRating();

		if (!ratingValidatingService.isWorkRatingRatableByUser(form.getWorkId(), raterUserId)) {
			return AjaxResponseBuilder.fail()
				.setMessages(bundle.getAllMessages());
		}

		if (ratingValidatingService.isWorkRatingEditableByUser(form.getWorkId(), raterUserId)) {
			ratingService.updateLatestRatingForUserForWork(form.getWorkId(), form.getRatedUserId(), ratingDTO);
		} else {
			// Create Rating
			WorkRatingDTO workRatingDTO = new WorkRatingDTO(raterUserId, form.getRatedUserId(), form.getWorkId(), ratingDTO.getValue(),
				ratingDTO.getQuality(), ratingDTO.getProfessionalism(), ratingDTO.getCommunication(), ratingDTO.getReview());
			ratingService.createRatingForWork(workRatingDTO);
		}

		if (!bundle.hasErrors()) {
			messageHelper.addSuccess(bundle, "rating.flag.single_success");
			return AjaxResponseBuilder.success();
		} else {
			return AjaxResponseBuilder.fail()
				.setMessages(bundle.getAllMessages());
		}
	}

	@RequestMapping(
		value = "/complete/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder complete(
		@PathVariable("workNumber") String workNumber,
		RedirectAttributes flash,
		@Valid WorkCompleteForm form,
		BindingResult bindingResult,
		@RequestParam(value = "onBehalfOf", required = false) Long onBehalfOf) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		Company company = profileService.findCompanyById(getCurrentUser().getCompanyId());

		if (company.isSuspended())
			throw new HttpException400()
				.setMessageKey("assignment.complete.suspended")
				.setRedirectUri("redirect:/assignments/home");

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.BUYER_INFO,
			WorkRequestInfo.ASSETS_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO
			), (onBehalfOf != null) ?
				ImmutableSet.of(AuthorizationContext.ADMIN, AuthorizationContext.BUYER) :
				ImmutableSet.of(AuthorizationContext.ACTIVE_RESOURCE)
			, "complete");

		Work work = workResponse.getWork();

		if (!work.getStatus().getCode().equals(WorkStatusType.ACTIVE))
			throw new HttpException400()
				.setMessageKey("assignment.complete.notinprogress")
				.setRedirectUri("redirect:/assignments/details/" + workNumber);

		// Validate pricing
		PricingStrategy pricingStrategy = pricingService.findPricingStrategyById(work.getPricing().getId());
		if (pricingStrategy instanceof PerHourPricingStrategy || pricingStrategy instanceof BlendedPerHourPricingStrategy) {
			ValidationUtils.rejectIfEmpty(bindingResult, "hours", "hours_worked_required");
		} else if (pricingStrategy instanceof PerUnitPricingStrategy) {
			ValidationUtils.rejectIfEmpty(bindingResult, "units", "units_processed_required");
		}

		// Validate additional expense override
		if (form.getAdditional_expenses() != null && form.getAdditional_expenses() > work.getPricing().getAdditionalExpenses()) {
			bindingResult.reject("additional_expenses_exceeded", new Object[]{work.getPricing().getAdditionalExpenses()}, "");
		}

		// Validate existence of required custom fields
		if (work.getCustomFieldGroupsSize() > 0) {
			for (CustomFieldGroup customFieldGroup : work.getCustomFieldGroups()) {
				if (customFieldGroup.hasFields()) {
					for (CustomField field : customFieldGroup.getFields()) {
						// if buyer validate all required fields, if worker only validate worker required fields
						if (field.isIsRequired() && StringUtils.isEmpty(field.getValue()) && (field.getType().equals(WorkCustomFieldType.RESOURCE) || !getCurrentUser().getId().equals(work.getActiveResource().getUser().getId()))) {
							messageHelper.addError(bundle, "assignment.complete.specific_custom_fields_missing", field.getName());
							break;
						}
					}
				}
			}
		}

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return response
				.setSuccessful(false)
				.setMessages(bundle.getAllMessages());
		}

		CompleteWorkDTO completeWork = new CompleteWorkDTO();
		completeWork.setAdditionalExpenses(form.getAdditional_expenses());
		completeWork.setBonus(work.getPricing().getBonus());
		completeWork.setResolution(form.getResolution());

		// Taxes
		if (form.isCollect_tax()) {
			ValidationUtils.rejectIfEmpty(bindingResult, "tax_percent", "NotEmpty", CollectionUtilities.newArray("tax rate"));
			completeWork.setSalesTaxRate(form.getTax_percent());
			completeWork.setSalesTaxCollectedFlag(true);
		}

		if (pricingStrategy instanceof FlatPricePricingStrategy || pricingStrategy instanceof InternalPricingStrategy) {
			completeWork.setOverridePrice(form.getOverride_price());
		} else if (pricingStrategy instanceof PerHourPricingStrategy || pricingStrategy instanceof BlendedPerHourPricingStrategy) {
			if (form.getHours() == 0 && form.getMinutes() == 0) {
				messageHelper.addError(bundle, "assignment.complete.notime");
			}
			completeWork.setOverridePrice(form.getOverride_price());
			completeWork.setHoursWorked(DateUtilities.getDecimalHours(form.getHours(), form.getMinutes(), Constants.PRICING_STRATEGY_ROUND_SCALE));
		} else if (pricingStrategy instanceof PerUnitPricingStrategy) {
			completeWork.setOverridePrice(form.getOverride_price());
			completeWork.setUnitsProcessed(form.getUnits());
		}

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
		}

		if (bundle.hasErrors()) {
			return response
				.setSuccessful(false)
				.setMessages(bundle.getAllMessages());
		}

		List<ConstraintViolation> completionResult = workService.completeWork(work.getId(), onBehalfOf, completeWork);

		if (completionResult.isEmpty()) {
			workSearchService.reindexWorkAsynchronous(work.getId());
			return response.setSuccessful(true);
		} else {
			bindingResult = ValidationMessageHelper.newBindingResult();

			for (ConstraintViolation v : completionResult)
				ValidationMessageHelper.rejectViolation(v, bindingResult);

			messageHelper.setErrors(bundle, bindingResult);
			return response
				.setSuccessful(false)
				.setMessages(bundle.getAllMessages());
		}
	}

	@RequestMapping(
		value = "/pay_now/{workNumber}",
		method = POST)
	@PreAuthorize("!principal.isMasquerading()")
	public String payNow(
		@PathVariable String workNumber,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
				AuthorizationContext.PAY
		), "pay_now");

		try {
			List<ConstraintViolation> violations = billingService.payAssignment(workResponse.getWork().getId());

			if (violations.isEmpty()) {
				messageHelper.addSuccess(bundle, "assignment.pay_now.success");
				return "redirect:/assignments";
			} else {
				BindingResult bindingResult = ValidationMessageHelper.newBindingResult();
				for (ConstraintViolation v : violations)
					ValidationMessageHelper.rejectViolation(v, bindingResult);
				messageHelper.setErrors(bundle, bindingResult);
			}
		} catch (InsufficientFundsException ex) {
			messageHelper.addError(bundle, "assignment.pay_now.insufficient_funds");
		}

		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/updateratings",
		method = POST)
	public String updateRatings(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "rating_id", required = false) Long ratingId,
		@RequestParam(value = "remove", required = false) boolean remove,
		@RequestParam(value = "review", required = false) String review,
		@RequestParam(value = "share", required = false) boolean share,
		RedirectAttributes model) {

		if (StringUtils.isEmpty(workNumber)) {
			return "redirect:/assignments";
		}

		MessageBundle bundle = messageHelper.newBundle();

		// Make sure the work is valid.
		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (!(work instanceof com.workmarket.domains.work.model.Work)) {
			messageHelper.addError(bundle, "assignment.notfound");
			model.addAttribute("bundle", bundle);
			return "redirect:/assignments";
		}

		ExtendedUserDetails user = getCurrentUser();
		List<WorkContext> contexts = workService.getWorkContext(work.getId(), user.getId());

		if (!CollectionUtilities.containsAll(contexts, WorkContext.ACTIVE_RESOURCE, WorkContext.OWNER)) {
			messageHelper.addError(bundle, "assignment.updateratings.not.authorized");
			model.addAttribute("bundle", bundle);
			return "redirect:/assignments/details/" + workNumber;
		}

		if (!Arrays.asList(WorkStatusType.COMPLETE, WorkStatusType.PAID, WorkStatusType.CANCELLED)
			.contains(work.getWorkStatusType().getCode())) {
			messageHelper.addError(bundle, "assignment.updateratings.locked");
			model.addAttribute("bundle", bundle);
			return "redirect:/assignments/details/" + workNumber;
		}

		if (!remove) {
			if (ratingId == null) {
				messageHelper.addError(bundle, "NoEmpty", "rating");
				model.addAttribute("bundle", bundle);
				return "redirect:/assignments/details/" + workNumber;
			}
		}

		// Update ratings
		try {
			// Delete the current rating first.
			if (ratingId != null) {
				ratingService.deleteRating(ratingId);
			}

			if (!remove) {
				Long ratedUserId;

				// Get the rated user id
				if (contexts.contains(WorkContext.ACTIVE_RESOURCE)) {
					ratedUserId = work.getBuyer().getId();
				} else {
					WorkResource activeResource = workService.findActiveWorkResource(work.getId());
					ratedUserId = activeResource.getUser().getId();
				}

				RatingDTO ratingDTO = new RatingDTO();
				ratingDTO.setValue(ratingId.intValue());

				if (StringUtils.isNotEmpty(review)) {
					ratingDTO.setReview(review);
				}

				if (share) {
					ratingDTO.setRatingSharedFlag(Boolean.TRUE);
					ratingDTO.setReviewSharedFlag(Boolean.TRUE);
				}

				Rating rating = ratingService.createRatingForWork(user.getId(), ratedUserId, work.getId(), ratingDTO);

				if (rating == null) {
					messageHelper.addError(bundle, "assignment.updateratings.error");
				} else {
					messageHelper.addSuccess(bundle, "assignment.updateratings.saved");
				}
			} else {
				messageHelper.addSuccess(bundle, "assignment.updateratings.removed");
			}
		} catch (Exception ex) {
			logger.error("error occurred updating a review & rating for workNumber={} and rating_id={}",
				new Object[]{workNumber, ratingId}, ex);
			messageHelper.addError(bundle, "assignment.updateratings.exception");
		}

		model.addAttribute("bundle", bundle);
		return "redirect:/assignments/details/" + workNumber;
	}


	@RequestMapping(
		value = "/projectactions",
		method = GET)
	public String projectactions() {
		return "web/pages/assignments/projectactions";
	}


	@RequestMapping(
		value = "/save_custom_fields",
		method = POST)
	@ResponseBody public AjaxResponseBuilder saveBulkCustomFields(
		@RequestParam("assignment_id_custom_field") List<String> workNumbers,
		@RequestParam("bulk_field_update[]") List<Long> fieldIds,
		@RequestParam("custom_field_group_ids") List<Long> customFieldGroupIds,
		CustomFieldGroup form,
		BindingResult bindingResult) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		List<WorkResponse> workResponses = getWorks(workNumbers, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "save_custom_fields");

		List<BulkSaveCustomFieldsRequest> bulkSaveCustomFieldsRequests = Lists.newArrayList();

		for (WorkResponse workResponse : workResponses) {
			for (Long customFieldGroupId : customFieldGroupIds) {
				WorkCustomFieldGroup fieldGroup = (form == null) ? null : customFieldService.findWorkCustomFieldGroup(customFieldGroupId);

				if (fieldGroup == null) {
					messageHelper.addMessage(response, "assignment.save_custom_fields.notfound");
					return response.setSuccessful(false);
				}

				boolean isAdmin = CollectionUtilities.contains(workResponse.getAuthorizationContexts(), AuthorizationContext.ADMIN);
				boolean isActiveResource = CollectionUtilities.contains(workResponse.getAuthorizationContexts(), AuthorizationContext.ACTIVE_RESOURCE);

				Map<Long, String> valueLookup = Maps.newHashMap();
				if (form.getFields() != null)
					for (CustomField f : form.getFields())
						valueLookup.put(f.getId(), f.getValue());
				List<WorkCustomFieldDTO> dtos = Lists.newArrayList();

				for (WorkCustomField field : customFieldService.findAllFieldsForCustomFieldGroup(fieldGroup.getId())) {
					if (fieldIds.contains(field.getId())) {
						if (valueLookup.containsKey(field.getId()) && StringUtils.isNotBlank(valueLookup.get(field.getId()))) {
							WorkCustomFieldDTO dto = new WorkCustomFieldDTO();
							dto.setId(field.getId());
							dto.setValue(valueLookup.get(field.getId()));
							dtos.add(dto);
						} else if (field.isOwnerType() && field.getRequiredFlag() && isAdmin) {
							bindingResult.reject("NotNull", new Object[]{field.getName()}, "");
						} else if (field.isResourceType() && field.getRequiredFlag() && isActiveResource) {
							bindingResult.reject("NotNull", new Object[]{field.getName()}, "");
						}
					}
				}

				messageHelper.setErrors(response, bindingResult);
				if (bindingResult.hasErrors()) {
					return response.setSuccessful(false);
				}

				bulkSaveCustomFieldsRequests.add(
					new BulkSaveCustomFieldsRequest(customFieldGroupId, workResponse.getWork().getId(), dtos));
			}
		}

		eventRouter.sendEvent(
			new BulkSaveCustomFieldsEvent(
				bulkSaveCustomFieldsRequests,
				getCurrentUser().getMasqueradeUserId()
			)
		);

		messageHelper.addMessage(response, "assignment.save_custom_fields.success");
		return response.setSuccessful(true);
	}


	@RequestMapping(
		value = "/save_custom_fields/{workNumber}",
		method = POST)
	@ResponseBody
	public AjaxResponseBuilder saveCustomFields(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "onComplete", required = false, defaultValue = "false") Boolean onComplete,
		@RequestParam(value = "isPendingSets", required = false, defaultValue = "false") boolean isPendingSets,
		CustomFieldGroupSet form,
		BindingResult bindingResult,
		RedirectAttributes flash) {

		MessageBundle messages = messageHelper.newFlashBundle(flash);

		Map<String, Object> retMap = doSaveCustomFields(workNumber, form, bindingResult, onComplete);

		/* Check if doSaveCustomFields was successful.  If not, show the errors it generated. */
		if (!(Boolean) retMap.get("successful")) {
			for (Object error : (ArrayList) retMap.get("errors")) {
				messageHelper.addError(messages, (String) error);
			}
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(messages.getAllMessages())
				.setRedirect("/assignments/details/{workNumber}");
		} else {
			messageHelper.addSuccess(messages, isPendingSets ? "assignment.save_pending_custom_fields.success" : "assignment.save_custom_fields.success");
			return new AjaxResponseBuilder()
				.setSuccessful(true)
				.setMessages(messages.getAllMessages())
				.setRedirect("/assignments/details/{workNumber}");
		}
	}

	// TODO: should this be in WorkFormController?
	@RequestMapping(
		value = "/getcustomfield",
		method = GET)
	public void getCustomField(Model model, @RequestParam("id") Long fieldGroupId) {
		WorkCustomFieldGroup fieldGroup = customFieldService.findWorkCustomFieldGroup(fieldGroupId);
		if (fieldGroup == null) {
			model.addAttribute("response", CollectionUtilities.newObjectMap(
				"success", false
			));
			return;
		}

		// TODO Refactor JS to use native object property names to allow for straight JSON serialization
		List<Map<String, Object>> fields = Lists.newArrayList();
		for (WorkCustomField field : customFieldService.findAllFieldsForCustomFieldGroup(fieldGroupId)) {
			fields.add(CollectionUtilities.newObjectMap(
				"id", field.getId(),
				"name", field.getName(),
				"value", "",
				"defaults", field.getDefaultValue(),
				"options", field.isDropdown() ? field.getDropdownValues() : Lists.newArrayListWithCapacity(0),
				"required", field.getRequiredFlag(),
				"is_dropdown", field.isDropdown(),
				"type", field.getWorkCustomFieldType().getCode()
			));
		}

		model.addAttribute("response", CollectionUtilities.newObjectMap(
				"success", true,
				"data", fields
		));
	}

	// Time Tracking

	/**
	 * Assignment check-in action for the active resource.
	 * Used for both new check-ins or to update an existing entry.
	 *
	 * @param workNumber
	 * @param trackingId The identifier for an existing time-tracking entry. Provide if updating; leave null if new.
	 * @param userNumber The user who is being checked in. Should be the active resource.
	 * @param date       Timezone adjusted date of check-in.
	 * @param time       Timezone adjusted time of check-in.
	 */
	@RequestMapping(
		value = "/update_checkin/{workNumber}",
		method = POST)
	public @ResponseBody AjaxResponseBuilder updateCheckin(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "id", required = false) String trackingId,
		@RequestParam(value = "user_number", required = false) String userNumber,
		@RequestParam(value = "date", required = false) String date,
		@RequestParam(value = "time", required = false) String time) throws WorkUnauthorizedException {

		MessageBundle bundle = messageHelper.newBundle();

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setRedirect("/assignments");

		if (!StringUtils.isNumeric(workNumber) || StringUtils.isEmpty(userNumber)) {
			messageHelper.addError(bundle, "assignment.update_checkin.exception");
			return response.setMessages(bundle.getErrors());
		}

		AbstractWork work = getAndAuthorizeWorkByNumber(workNumber, ImmutableList.of(
			WorkContext.ACTIVE_RESOURCE,
			WorkContext.DISPATCHER,
			WorkContext.COMPANY_OWNED,
			WorkContext.OWNER),
			"update_checkin");

		if (work == null) {
			messageHelper.addError(bundle, "assignment.update_checkin.exception");
			return response.setMessages(bundle.getErrors());
		}

		if (work.getTimeZone() == null) {
			messageHelper.addError(bundle, "assignment.update_checkin.exception");
			return response.setMessages(bundle.getErrors());
		}

		Calendar checkinDateTime = getCheckInOutDateTime(work, date, time);

		if (checkinDateTime == null) {
			messageHelper.addError(bundle, "assignment.update_checkin.invaliddate");
			return response.setMessages(bundle.getErrors());
		}

		TimeTrackingRequest checkinRequest = new TimeTrackingRequest()
			.setWorkId(work.getId())
			.setTimeTrackingId(StringUtilities.parseLong(trackingId))
			.setDate(checkinDateTime);

		TimeTrackingResponse checkinResponse = tWorkFacadeService.checkInActiveResource(checkinRequest);
		if (checkinResponse.isSuccessful() && checkinResponse.getTimeTracking() != null) {
			messageHelper.addSuccess(bundle, "assignment.update_checkin.success");
			return response
				.setSuccessful(true)
				.setMessages(bundle.getSuccess())
				.setRedirect("/assignments/details/" + workNumber)
				.addData("id", checkinResponse.getTimeTracking().getId())
				.addData("millis", checkinResponse.getTimeTracking().getCheckedInOn().getTimeInMillis());
		} else {
			messageHelper.addError(bundle, checkinResponse.getMessage());
			return response.setMessages(bundle.getErrors());
		}
	}

	/**
	 * Assignment check-out action for the active resource.
	 * Used for both new check-outs or to update an existing entry.
	 *
	 * @param workNumber
	 * @param trackingId The identifier for an existing time-tracking entry. Provide if updating; leave null if new.
	 * @param userNumber The user who is being checked out. Should be the active resource.
	 * @param date       Timezone adjusted date of check-out.
	 * @param time       Timezone adjusted time of check-out.
	 * @param noteText   Note text to attach to the check-out entry.
	 * @return
	 */
	@RequestMapping(
		value = "/update_checkout/{workNumber}",
		method = POST)
	public @ResponseBody AjaxResponseBuilder updateCheckout(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "id", required = false) String trackingId,
		@RequestParam(value = "user_number", required = false) String userNumber,
		@RequestParam(value = "date", required = false) String date,
		@RequestParam(value = "time", required = false) String time,
		@RequestParam(value = "note_text", required = false) String noteText) throws WorkUnauthorizedException {

		MessageBundle bundle = messageHelper.newBundle();

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setRedirect("/assignments");

		if (!StringUtils.isNumeric(workNumber) || StringUtils.isEmpty(userNumber)) {
			messageHelper.addError(bundle, "assignment.update_checkout.exception");
			return response.setMessages(bundle.getErrors());
		}

		AbstractWork work = getAndAuthorizeWorkByNumber(workNumber, ImmutableList.of(
			WorkContext.ACTIVE_RESOURCE,
			WorkContext.DISPATCHER,
			WorkContext.COMPANY_OWNED,
			WorkContext.OWNER),
			"update_checkout");

		if (work == null) {
			messageHelper.addError(bundle, "assignment.update_checkout.exception");
			return response.setMessages(bundle.getErrors());
		}

		if (work.getTimeZone() == null) {
			messageHelper.addError(bundle, "assignment.update_checkout.exception");
			return response.setMessages(bundle.getErrors());
		}

		Calendar checkoutDateTime = getCheckInOutDateTime(work, date, time);

		if (checkoutDateTime == null) {
			messageHelper.addError(bundle, "assignment.update_checkout.invaliddate");
			return response.setMessages(bundle.getErrors());
		}

		TimeTrackingRequest timeTrackingRequest = new TimeTrackingRequest()
			.setWorkId(work.getId())
			.setTimeTrackingId(StringUtilities.parseLong(trackingId))
			.setDate(checkoutDateTime)
			.setNoteOnCheckOut(noteText);
		TimeTrackingResponse checkoutResponse = tWorkFacadeService.checkOutActiveResource(timeTrackingRequest);

		if (checkoutResponse.isSuccessful() && checkoutResponse.getTimeTracking() != null) {
			messageHelper.addSuccess(bundle, "assignment.update_checkout.success");
			return response
				.setSuccessful(true)
				.setMessages(bundle.getSuccess())
				.setRedirect("/assignments/details/" + workNumber)
				.addData("id", checkoutResponse.getTimeTracking().getId())
				.addData("millis", checkoutResponse.getTimeTracking().getCheckedOutOn().getTimeInMillis());
		} else {
			messageHelper.addError(bundle, checkoutResponse.getMessage());
			return response.setMessages(bundle.getErrors());
		}
	}

	//soft delete checkin/checkout
	@RequestMapping(
		value = "/delete_checkin/{workNumber}",
		method = POST)
	public @ResponseBody AjaxResponseBuilder deleteCheckin(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "id", required = false) String trackingId) throws WorkUnauthorizedException {

		MessageBundle bundle = messageHelper.newBundle();

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setRedirect("/assignments")
			.setSuccessful(false);

		if (!StringUtils.isNumeric(workNumber) || !StringUtils.isNumeric(trackingId)) {
			messageHelper.addError(bundle, "assignment.delete_checkin.exception");
			return response.setMessages(bundle.getErrors());
		}

		AbstractWork work = getAndAuthorizeWorkByNumber(workNumber, ImmutableList.of(
			WorkContext.ACTIVE_RESOURCE,
			WorkContext.DISPATCHER,
			WorkContext.COMPANY_OWNED,
			WorkContext.OWNER),
			"assignment.delete_checkin.exception");

		workService.deleteCheckInResource(work.getId(), StringUtilities.parseLong(trackingId));

		response.addData("id", trackingId);
		response.setSuccessful(true);

		return response;
	}

	@RequestMapping(
		value = "/delete_checkout/{workNumber}",
		method = POST)
	public @ResponseBody AjaxResponseBuilder deleteCheckout(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "id", required = false) String trackingId) throws WorkUnauthorizedException {

		MessageBundle bundle = messageHelper.newBundle();

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setRedirect("/assignments")
			.setSuccessful(false);

		if (!StringUtils.isNumeric(workNumber) || !StringUtils.isNumeric(trackingId)) {
			messageHelper.addError(bundle, "assignment.delete_checkout.exception");
			return response.setMessages(bundle.getErrors());
		}

		AbstractWork work = getAndAuthorizeWorkByNumber(workNumber, ImmutableList.of(
			WorkContext.ACTIVE_RESOURCE,
			WorkContext.DISPATCHER,
			WorkContext.COMPANY_OWNED,
			WorkContext.OWNER),
			"assignment.delete_checkout.exception");

		workService.deleteCheckOutResource(work.getId(), StringUtilities.parseLong(trackingId));

		response.addData("id", trackingId);
		response.setSuccessful(true);

		return response;
	}

	private Calendar getCheckInOutDateTime(AbstractWork work, String date, String time) {
		if (isBlank(date) && isBlank(time)) {
			return Calendar.getInstance();
		} else if (StringUtilities.all(time, date)) {
			return DateUtilities.getCalendarFromDateTimeString(String.format("%s %s", date, time), work.getTimeZone().getTimeZoneId());
		}

		return null;
	}

	@RequestMapping(
		value = "/workflow_status_extras/{workNumber}",
		method = GET)
	public @ResponseBody AjaxResponseBuilder workflowStatusExtras(
		@PathVariable String workNumber) throws WorkUnauthorizedException {

		MessageBundle bundle = messageHelper.newBundle();

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setRedirect("/assignments");

		if (!StringUtils.isNumeric(workNumber)) {
			messageHelper.addError(bundle, "assignment.workflow_status_extras.exception");
			return response.setMessages(bundle.getErrors());
		}

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.COMPANY_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO
		), ImmutableSet.of(
			AuthorizationContext.RESOURCE,
			AuthorizationContext.ADMIN
		), "workflow_status_extras");
		Work work = workResponse.getWork();

		Long duration = work.getActiveResource().getTimeTrackingDuration();
		String fmtDuration = DateUtilities.getDurationBreakdown(duration);

		String substatuses;
		try {
			substatuses = ThriftUtilities.serializeToJson(work.getSubStatuses());
		} catch (Exception e) {
			substatuses = "{}";
		}

		Set<AuthorizationContext> authContexts = workResponse.getAuthorizationContexts();
		boolean isAdmin = authContexts.contains(AuthorizationContext.ADMIN);
		boolean isOwner = authContexts.contains(AuthorizationContext.BUYER);
		boolean isActiveResource = authContexts.contains(AuthorizationContext.ACTIVE_RESOURCE);
		boolean isResource = authContexts.contains(AuthorizationContext.RESOURCE);
		boolean isCheckedIn = workService.isActiveResourceCurrentlyCheckedIn(work.getId());

		// filter labels
		WorkSubStatusTypeFilter labelFilter = new WorkSubStatusTypeFilter();
		labelFilter.setShowSystemSubStatus(true);
		labelFilter.setShowCustomSubStatus(true);
		labelFilter.setShowDeactivated(false);
		if (isAdmin) {
			labelFilter.setClientVisible(true);
		} else if (isResource) {
			labelFilter.setResourceVisible(true);
		}
		List<WorkSubStatusType> availableLabels = workSubStatusService.findAllSubStatuses(work.getCompany().getId(), labelFilter);

		response.addData("is_admin", isAdmin);
		response.addData("is_owner", isOwner);
		response.addData("is_active_resource", isActiveResource);
		response.addData("available_labels", availableLabels);
		response.addData("active_resource", work.getActiveResource());
		response.addData("substatuses", substatuses);
		response.addData("time_tracking_duration_out", fmtDuration);
		response.addData("is_checked_in", isCheckedIn);
		response.setSuccessful(true);
		return response;
	}


	@RequestMapping(
		value = "/event_capture",
		method = POST
	)
	public @ResponseBody AjaxResponseBuilder eventCapture(@RequestBody Map<String, Object> circumstances) {
		logger.error(String.format(
			"An 'undefined' file upload has been detected.\nsource: %s\ndata: %s\nevent: %s",
			circumstances.get("source"),
			circumstances.get("data"),
			circumstances.get("event")
		));

		return AjaxResponseBuilder.success();
	}

	@RequestMapping(
		value = "/add_document",
		method = POST,
		produces = APPLICATION_JSON_VALUE,
		consumes = MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody Map<String, Object> addDocument(
		@RequestParam("work_id") String workNumber,
		@RequestParam(value = "qqfile", required = false) MultipartFile attachment) throws IOException {

		String fileName = attachment.getOriginalFilename();
		BindingResult bind = getFilenameErrors(fileName);
		if (bind.hasErrors()) {
			return ImmutableMap.of(
				"successful", false,
				"errors", messageHelper.getAllErrors(bind)
			);
		}

		AssetDTO assetDTO = AssetDTO.newAssetDTO()
			.setName(fileName)
			.setMimeType(attachment.getContentType())
			.setLargeTransformation(MimeTypeUtilities.isImage(attachment.getContentType()));

		WorkAssetAssociation workAssetAssociation;
		try {
			workAssetAssociation = documentService.addDocument(workNumber, assetDTO, attachment.getInputStream());
		} catch (Exception e) {
			logger.error("There was an error uploading an attachment", e);
			MessageBundle bundle = messageHelper.newBundle();
			messageHelper.addError(bundle, "assignment.add_attachment.exception");
			return ImmutableMap.of(
				"successful", false,
				"errors", bundle.getErrors());
		}

		return buildDocumentResponse(workAssetAssociation, workNumber);
	}

	@RequestMapping(
		value = "/add_deliverable",
		method = POST,
		produces = APPLICATION_JSON_VALUE,
		consumes = MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody Map<String, Object> addDeliverable(
		@RequestParam("work_id") String workNumber,
		@RequestParam(value = "qqfile", required = false) MultipartFile attachment,
		@RequestParam(value = "deliverable_requirement_id", required = false) Long deliverableRequirementId,
		@RequestParam(value = "position", required = false) Integer position) throws IOException {

		String fileName = attachment.getOriginalFilename();
		BindingResult bind = getFilenameErrors(fileName);
		if (bind.hasErrors()) {
			return ImmutableMap.of(
				"successful", false,
				"errors", messageHelper.getAllErrors(bind)
			);
		}

		MessageBundle bundle = messageHelper.newBundle();
		deliverableValidator.validate(deliverableRequirementId, attachment.getContentType(), bundle);
		if (bundle.hasErrors()) {
			return ImmutableMap.of(
				"successful", false,
				"errors", bundle.getErrors());
		}

		AssetDTO assetDTO = AssetDTO.newAssetDTO()
			.setName(fileName)
			.setDeliverableRequirementId(deliverableRequirementId)
			.setPosition(position)
			.setName(fileName)
			.setMimeType(attachment.getContentType())
			.setLargeTransformation(MimeTypeUtilities.isImage(attachment.getContentType()));

		WorkAssetAssociation workAssetAssociation;
		try {
			workAssetAssociation = deliverableService.addDeliverable(workNumber, attachment.getInputStream(), assetDTO);
		} catch (Exception e) {
			logger.error("There was an error uploading an attachment", e);
			messageHelper.addError(bundle, "assignment.add_attachment.exception");
			return ImmutableMap.of(
				"successful", false,
				"errors", bundle.getErrors());
		}

		return buildDeliverableAssetResponse(workAssetAssociation, position, workNumber, deliverableRequirementId);
	}

	/**
	 * Handle file uploads from the <code>qquploader</code> jQuery file uploader for IE
	 * which DOES upload via <code>multipart/form-data</code> content type requests and
	 * NOT as a <code>application/octet-stream</code> content type.
	 */
	@RequestMapping(
		value = "/add_attachments",
		method = POST,
		produces = TEXT_HTML_VALUE,
		consumes = MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody String addAttachmentForIE(
		@RequestParam("work_numbers") List<String> workNumbers,
		@RequestParam(value = "qqfile", required = false) MultipartFile attachment,
		@RequestParam(value = "description", required = false) String description) throws IOException {

		MessageBundle bundle = messageHelper.newBundle();
		List<WorkResponse> workResponses;

		if (CollectionUtilities.isEmpty(workNumbers)) {
			messageHelper.addError(bundle, "assignment.no_assignment_selected");
		}
		String fileName = attachment.getOriginalFilename();
		BindingResult bind = getFilenameErrors(fileName);
		if (bind.hasErrors()) {
			return new JSONObject(ImmutableMap.of(
				"successful", false,
				"errors", messageHelper.getAllErrors(bind)
			)).toString();
		}

		try {
			workResponses = getWorkAndAuthorizeNotCancelled(workNumbers, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE
			), "add_attachment");
		} catch (Exception e) {
			messageHelper.addError(bundle, "assignment.attachment.not_authorized");

			return new JSONObject(newObjectMap(
				"successful", false,
				"errors", bundle.getErrors())).toString();
		}
		List<Long> workIds = new ArrayList<>();
		for (WorkResponse workResp : workResponses) {
			workIds.add(workResp.getWork().getId());
		}
		Map<String, Object> retMap = doAddAttachments(workResponses, workIds, attachment.getOriginalFilename(),
				description, WorkAssetAssociationType.ATTACHMENT, attachment.getContentType(), attachment.getSize(),
				attachment.getInputStream());

		if (!retMap.containsKey("asset")) {
			return retMap.toString();
		}

		com.workmarket.domains.model.asset.Asset asset = (com.workmarket.domains.model.asset.Asset) retMap.get("asset");

		return new JSONObject(newObjectMap(
			"successful", true,
			"id", asset.getUUID(),
			"file_name", attachment.getOriginalFilename(),
			"uuid", asset.getUUID(),
			"mimeType", attachment.getContentType(),
			"description", description,
			"type", attachment.getContentType(),
			"mime_type_icon", MimeTypeUtilities.getMimeIconName(attachment.getContentType()),
			"message", messageHelper.getMessage("assignment.add_attachment.success")
		)).toString();

	}

	// TODO - Micah - rename this to async again when setup is done.
	@RequestMapping(
		value = "/add_attachments_sync",
		method = POST)
	public @ResponseBody Map<String, Object> addAttachmentsBBone(@RequestParam("work_numbers") List<String> workNumbers, MultipartHttpServletRequest request) {

		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf = request.getFile(itr.next());

		File file;
		try {
			file = FileUtilities.temporaryStoreFile(mpf.getInputStream());
		} catch (IOException e) {
			return ImmutableMap.of(
				"successful", false,
				"errors", (Object) messageHelper.getMessage("assignment.add_attachment.exception")
			);
		}

		AddAttachmentsWorkEvent addAttachmentsWorkEvent = workEventFactory.createAddAttachmentsEventQueue(
			workNumbers,
			userService.findUserById(getCurrentUser().getId()),
			"assignment.add_attachment",
			"assignment.add_attachment",
			WorkAssetAssociationType.ATTACHMENT,
			mpf.getContentType(),
			mpf.getOriginalFilename(),
			"",
			request.getContentLength(),
			file.getAbsolutePath()
		);

		return workEventService.doAction(addAttachmentsWorkEvent).getData();
	}

	@RequestMapping(
		value = "/add_attachments",
		method = POST,
		produces = APPLICATION_JSON_VALUE,
		consumes = APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody Map<String, Object> addAttachmentsNew(
		@RequestParam("work_numbers") List<String> workNumbers,
		@RequestParam(value = "description", required = false) String description,
		HttpServletRequest request) throws IOException {

		String fileName = StringUtilities.urlDecode(request.getHeader("X-File-Name"));
		String contentType = MimeTypeUtilities.guessMimeType(fileName);
		BindingResult bind = getFilenameErrors(fileName);
		if (bind.hasErrors()) {
			return ImmutableMap.of(
				"successful", false,
				"errors", messageHelper.getAllErrors(bind)
			);
		}

		File file;
		try {
			file = FileUtilities.temporaryStoreFile(request.getInputStream());
		} catch (IOException e) {
			return ImmutableMap.of(
				"successful", false,
				"errors", (Object) messageHelper.getMessage("assignment.add_attachment.exception")
			);
		}

		AddAttachmentsWorkEvent addAttachmentsWorkEvent = workEventFactory.createAddAttachmentsEvent(
			workNumbers,
			userService.findUserById(getCurrentUser().getId()),
			"assignment.add_attachment",
			"assignment.add_attachment",
			WorkAssetAssociationType.ATTACHMENT,
			contentType,
			fileName,
			description == null ? "" : description,
			request.getContentLength(),
			file.getAbsolutePath());

		return workEventService.doAction(addAttachmentsWorkEvent).getData();
	}

	@RequestMapping(value = "/update_document_visibility",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder updateDocumentVisibility(
		@RequestParam(value = "work_number") String workNumber,
		@RequestParam(value = "asset_id") Long assetId,
		@RequestParam(value = "visibility_code") String visibilityCode) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		MessageBundle messageBundle = messageHelper.newBundle();

		updateDocumentVisibilityValidator.validate(workNumber, assetId, visibilityCode, messageBundle);
		if (messageBundle.hasErrors()) {
			for (String message : messageBundle.getErrors()) {
				messageHelper.addMessage(response, message);
			}
			return response;
		}

		Long workId = workService.findWorkId(workNumber);
		try {
			documentService.updateDocumentVisibility(workId, assetId, visibilityCode);
			response.setSuccessful(true);
		} catch (Exception e) {
			logger.error(String.format("An error occurred while updating document visibility: wordId: %d, assetId: %d, visibilityCode: %s", workId, assetId, visibilityCode));
			messageHelper.addMessage(response, UpdateDocumentVisibilityValidator.GENERIC_ERROR);
		}

		return response;
	}


	@RequestMapping(value = "/reject_deliverable",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder rejectDeliverable(
		@RequestParam(value = "work_number", required = true) String workNumber,
		@RequestParam(value = "asset_id", required = true) Long assetId,
		@RequestParam(value = "rejection_reason", required = false) String rejectionReason) throws IOException {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		MessageBundle messageBundle = messageHelper.newBundle();

		Long currentUserId = getCurrentUser().getId();
		Long currentUserCompanyId = getCurrentUser().getCompanyId();

		WorkRequest request = new WorkRequest()
			.setUserId(currentUserId)
			.setWorkNumber(workNumber)
			.setIncludes(Sets.newHashSet(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO
			));

		WorkResponse workResponse;
		try {
			workResponse = tWorkFacadeService.findWork(request);
		} catch (WorkActionException e) {
			logger.error("An error occurred while finding work with workNumber:" + workNumber, e);
			messageHelper.addMessage(response, "rejectDeliverable.validation.genericError");
			return response;
		}

		rejectDeliverableValidator.validate(workResponse, rejectionReason, assetId, currentUserCompanyId, messageBundle);

		if (messageBundle.hasErrors()) {
			for (String message : messageBundle.getErrors()) {
				messageHelper.addMessage(response, message);
			}
			return response;
		}

		Work work = workResponse.getWork();
		Calendar rejectionDate = deliverableService.rejectDeliverable(rejectionReason, currentUserId, work.getId(), assetId);

		Map<String, Object> responseData = Maps.newHashMap();
		responseData.put("rejectedOn", rejectionDate.getTimeInMillis());
		responseData.put("rejectedBy", getCurrentUser().getFullName());
		responseData.put("rejectionReason", rejectionReason);

		// If the assignment is in "completed" status, perform additional actions
		if (WorkStatusType.COMPLETE.equals(work.getStatus().getCode())) {
			workService.incompleteWork(work.getId(), serviceMessageHelper.getMessage("rejectDeliverable.pending.approval.message", rejectionReason));
			response.setRedirect("/assignments/details/" + workNumber);
			messageHelper.addMessage(response, "assignment.sendback.sent");
		}

		response.setData(responseData);
		response.setSuccessful(true);

		return response;
	}

	@RequestMapping(
		value = "/{workNumber}/parts",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getParts(
		@PathVariable String workNumber) throws Exception {
		wmMetricRegistryFacade.meter("getParts").mark();
		AjaxResponseBuilder response = AjaxResponseBuilder
			.fail()
			.setRedirect("/assignments/details/" + workNumber);
		MessageBundle messageBundle = messageHelper.newBundle();

		final List<WorkResponse> workResponses;
		try {
			workResponses = getWorkAndAuthorizeNotCancelled(Lists.newArrayList(workNumber), ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.PARTS_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.RESOURCE,
				AuthorizationContext.ADMIN
			), "partsAndLogistics.assignment");

		} catch (Exception e) {
			messageHelper.addError(messageBundle, "partsAndLogistics.assignment.fetch.not_authorized");
			return response.setMessages(messageBundle.getErrors());
		}

		final Work work = workResponses.get(0).getWork();
		final PartGroupDTO partGroup = work.getPartGroup();

		final List<PartDTO> parts;
		if (partGroup == null) {
			parts = Lists.newArrayListWithCapacity(0);
		} else {
			parts = partService.getPartsByGroupUuid(partGroup.getUuid());
		}

		return response
			.setSuccessful(true)
			.setData(ImmutableMap.<String, Object>of("parts", parts));
	}

	@RequestMapping(
		value = "/{workNumber}/parts",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder savePart(
		@PathVariable String workNumber,
		@RequestBody PartDTO partDTO,
		BindingResult bindingResult,
		HttpServletResponse httpServletResponse) throws Exception {
		wmMetricRegistryFacade.meter("savePart").mark();
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		MessageBundle messageBundle = messageHelper.newBundle();

		List<WorkResponse> workResponses;
		try {
			workResponses = getWorkAndAuthorizeNotCancelled(Lists.newArrayList(workNumber), ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.PARTS_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			), "partsAndLogistics.assignment");

		} catch (Exception e) {
			messageHelper.addError(messageBundle, "partsAndLogistics.assignment.add.not_authorized");
			return response.setMessages(messageBundle.getErrors());
		}

		partValidator.validate(partDTO, bindingResult);

		if (bindingResult.hasErrors()) {
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return response.setMessages(extract(bindingResult.getAllErrors(), on(ObjectError.class).getDefaultMessage()));
		}

		final Work work = workResponses.get(0).getWork();
		PartDTO savedPart = partService.saveOrUpdatePart(partDTO, work.getPartGroup().getUuid());

		if (savedPart != null) {
			return response
				.setSuccessful(true)
				.setData(ImmutableMap.<String, Object>of("part", savedPart));
		}
		return response;
	}

	@RequestMapping(
		value = "/{workNumber}/parts/{uuid}",
		method = DELETE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deletePart(
		@PathVariable String workNumber,
		@PathVariable String uuid) throws Exception {
		wmMetricRegistryFacade.meter("deletePart").mark();
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		MessageBundle messageBundle = messageHelper.newBundle();

		try {
			getWorkAndAuthorizeNotCancelled(Lists.newArrayList(workNumber), ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.PARTS_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			), "partsAndLogistics.assignment");

		} catch (Exception e) {
			messageHelper.addError(messageBundle, "partsAndLogistics.assignment.add.not_authorized");
			return response;
		}

		partService.deletePart(uuid);

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/delete_assignments",
		method = POST)
	public @ResponseBody AjaxResponseBuilder deleteAssignments(
		@RequestParam(value = "model") String model) throws IOException {

		AjaxResponseBuilder response = new AjaxResponseBuilder();
		String[] assignIds;
		List<WorkResponse> workResponses;
		List<Long> deleteDrafts = new ArrayList<>();
		List<Long> voidSent = new ArrayList<>();

		Type hashMapType = new TypeToken<HashMap<String, String>>() {}.getType();
		String mapData = StringEscapeUtils.unescapeHtml4(model);
		HashMap<String, String> jsonMap = jsonService.fromJson(mapData, hashMapType);
		assignIds = jsonService.fromJson(jsonMap.get("assignment_id_delete"), String[].class);

		if (ArrayUtils.isEmpty(assignIds)) {
			messageHelper.addMessage(response, "assignment.bulk_delete.none_selected");
			return response.setSuccessful(false);
		}
		try {
			workResponses = getWorkAndAuthorizeNotCancelled(Arrays.asList(assignIds), ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ADMIN
			), "delete_assignment");
		} catch (Exception e) {
			messageHelper.addMessage(response, "assignment.bulk_delete.failure_auth");
			return response.setSuccessful(false);
		}
		for (WorkResponse workresponse : workResponses) {
			com.workmarket.domains.work.model.Work work = workService.findWork(workresponse.getWork().getId());
			if (!work.isDraft() && !work.isSent()) {
				messageHelper.addMessage(response, "assignment.bulk_delete.failure_notDraftOrSent");
				return response.setSuccessful(false);
			} else if (work.isDraft()) {
				deleteDrafts.add(workresponse.getWork().getId());
			} else if (work.isSent()) {
				voidSent.add(workresponse.getWork().getId());
			}
		}

		workService.deleteDraftAndSent(getCurrentUser().getId(), deleteDrafts, voidSent);

		messageHelper.addMessage(response, "assignment.bulk_delete.success");
		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/get_custom_fields",
		method = GET)
	public String getCustomFields(
		@RequestParam List<String> workNumbers,
		ModelMap model) throws IOException {

		List<WorkResponse> workResponses;
		try {
			workResponses = getWorks(workNumbers, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO
			), ImmutableSet.of(
				AuthorizationContext.ADMIN,
				AuthorizationContext.ACTIVE_RESOURCE
			), "get_custom_fields");
		} catch (Exception e) {
			model.addAttribute("errorMessage", messageHelper.getMessage("assignment.bulk_field_change.no_access"));
			model.addAttribute("showForm", false);
			return "web/partials/assignments/details/bulk_custom_fields";
		}

		if (CollectionUtilities.isEmpty(workNumbers)) {
			model.addAttribute("errorMessage", messageHelper.getMessage("assignment.bulk_field_change.no_assignments"));
			model.addAttribute("showForm", false);
			return "web/partials/assignments/details/bulk_custom_fields";
		}

		List<CustomFieldGroup> firstCustomField;
		if (isEmpty(workResponses)) {
			firstCustomField = Collections.emptyList();
		} else {
			firstCustomField = workResponses.get(0).getWork().getCustomFieldGroups();
		}

		for (WorkResponse workResponse : workResponses) {
			Work work = workResponse.getWork();

			List<CustomFieldGroup> workCustomField = work.getCustomFieldGroups();
			if (CollectionUtils.isEmpty(workCustomField)) {
				model.addAttribute("errorMessage", messageHelper.getMessage("assignment.bulk_field_change.no_group"));
				model.addAttribute("showForm", false);
				return "web/partials/assignments/details/bulk_custom_fields";
			}
			List<Long> firstGroupList = CollectionUtilities.newListPropertyProjection(firstCustomField, "id");
			List<Long> workGroupList = CollectionUtilities.newListPropertyProjection(workCustomField, "id");
			if (!workGroupList.containsAll(firstGroupList) || !firstGroupList.containsAll(workGroupList)) {
				model.addAttribute("errorMessage", messageHelper.getMessage("assignment.bulk_field_change.not_matching_group"));
				model.addAttribute("showForm", false);
				return "web/partials/assignments/details/bulk_custom_fields";
			}

			Collection<CustomField> allFields = Lists.newArrayList();
			for (CustomFieldGroup fieldGroup : work.getCustomFieldGroups()) {
				if (CollectionUtils.isEmpty(fieldGroup.getFields())) continue;
				allFields = Collections2.filter(fieldGroup.getFields(), new Predicate<CustomField>() {
					@Override
					public boolean apply(@Nullable CustomField field) {
						return field != null;
					}
				});
			}
			model.addAttribute("groupedCustomForms", allFields);
			model.addAttribute("work", work);
			String timeZoneName = work.getTimeZone();
			model.addAttribute("assignment_tz_millis_offset", java.util.TimeZone.getTimeZone(timeZoneName).getOffset(Calendar.getInstance().getTimeInMillis()));
			String workJson;
			try {
				workJson = ThriftUtilities.serializeToJson(work);
			} catch (Exception e) {
				workJson = "{}";
			}
			model.addAttribute("work_encoded", workJson);
		}
		model.addAttribute("showForm", true);
		return "web/partials/assignments/details/bulk_custom_fields";
	}

	@RequestMapping(
		value = "/get_attachments",
		method = GET)
	public @ResponseBody AjaxResponseBuilder getAttachments(
		@RequestParam("work_numbers") List<String> workNumbers) {

		GetAttachmentsEvent event = workEventFactory.createGetAttachmentsEvent(workNumbers,
			userService.findUserById(getCurrentUser().getId()),
			"get_attachments",
			"get_attachments");

		return workEventService.doAction(event);
	}

	@RequestMapping(
		value = {"/download_deliverable_assets/{work_number}/deliverable_requirement/{deliverable_requirement_id}"},
		method = GET)
	public String downloadAssetsFromDeliverableRequirement(
		@PathVariable("work_number") String workNumber,
		@PathVariable("deliverable_requirement_id") Long deliverableRequirementId) throws HostServiceException {

		return downloadAssetsByType(Lists.newArrayList(workNumber), WorkAssetAssociationType.DELIVERABLE_TYPES, true, deliverableRequirementId);
	}

	@RequestMapping(
		value = {"/download_deliverable_assets/{work_numbers}"},
		method = GET)
	public String downloadDeliverableAssets(
		@PathVariable("work_numbers") List<String> workNumbers) throws HostServiceException {

		return downloadAssetsByType(workNumbers, WorkAssetAssociationType.DELIVERABLE_TYPES, true);
	}

	@RequestMapping(
		value = "/download_attachment_assets/{work_numbers}",
		method = GET)
	public String downloadAttachmentAssets(
		@PathVariable("work_numbers") List<String> workNumbers) throws HostServiceException {

		return downloadAssetsByType(workNumbers, WorkAssetAssociationType.ATTACHMENT_TYPES, false);
	}

	private String downloadAssetsByType(List<String> workNumbers, Set<String> types, boolean filterForDeliverables) throws HostServiceException {
		return downloadAssetsByType(workNumbers, types, filterForDeliverables, null);
	}

	private String downloadAssetsByType(List<String> workNumbers, Set<String> types, boolean filterForDeliverables, Long deliverableRequirementId) throws HostServiceException {
		List<WorkResponse> workResponsesList;

		try {
			workResponsesList = getWorks(workNumbers, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.ASSETS_INFO
			), ImmutableSet.of(
				AuthorizationContext.ADMIN,
				AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE
			), "download_assets");
		} catch (Exception e) {
			throw new HttpException401();
		}

		AssetAssignmentBundle assetAssignmentBundle;

		assetAssignmentBundle = assetBundlerService.createAssignmentAssetBundle(workResponsesList, types, filterForDeliverables, deliverableRequirementId);

		int assetListSize = assetAssignmentBundle.getAssetUuids().size();
		if (assetListSize == 0) {
			throw new HttpException401();
		}

		Optional<com.workmarket.domains.model.asset.Asset> assetOptional;

		try {
			assetOptional = assetBundlerService.downloadAsset(assetAssignmentBundle);
		} catch (Exception e) {
			throw new HttpException401();
		}

		if (!assetOptional.isPresent()) {
			throw new HttpException401();
		}

		return String.format("redirect:%s", assetManagementService.getAuthorizedDownloadUriById(assetOptional.get().getId()));
	}

	@RequestMapping(
		value = "/email_assets",
		method = GET)
	public @ResponseBody AjaxResponseBuilder emailAttachments(
		@RequestParam("work_numbers") List<String> workNumbers) throws IOException {

		AjaxResponseBuilder response = new AjaxResponseBuilder();
		List<WorkResponse> workResponsesList;

		if (CollectionUtilities.isEmpty(workNumbers)) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.download_assets.no_assignment");
			return response;
		}

		try {
			workResponsesList = getWorks(workNumbers, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.ASSETS_INFO
			), ImmutableSet.of(
				AuthorizationContext.ADMIN,
				AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE
			), "email_assets");
		} catch (Exception e) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.download_assets.no_access");
			return response;
		}

		List<String> assetsList = new ArrayList<>();
		List<String> assignmentsList = new ArrayList<>();

		for (WorkResponse workResponse : workResponsesList) {
			Work work = workResponse.getWork();
			if (work.isSetDeliverableAssets()) {
				TreeSet<DeliverableAsset> assets = work.getDeliverableAssets();
				for (DeliverableAsset a : assets) {
					if (!a.isRejected()) {
						assetsList.add(a.getUuid());
						assignmentsList.add(work.getWorkNumber());
					}
				}
			}
		}

		if (assetsList.size() < 1) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.download_assets.no_assets");
			return response;
		}

		assetBundler.bundleAssetsForUser(assetsList, assignmentsList, getCurrentUser().getId());
		messageHelper.addMessage(response.setSuccessful(true), "assignment.download_assets.success");
		return response;
	}


	@RequestMapping(
		value = "/change_project",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder changeProject(
		@RequestParam(value = "model", required = false) String model) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();
		List<String> workNumbers;
		Long clientNumber;
		Long projectNumber;
		List<Long> workIds = new ArrayList<>();
		try {
			Type hashMapType = new TypeToken<HashMap<String, String>>() {}.getType();
			Type workNumbersType = new TypeToken<List<String>>() {}.getType();
			String mapData = StringEscapeUtils.unescapeHtml4(model);
			HashMap<String, String> jsonMap = jsonService.fromJson(mapData, hashMapType);
			workNumbers = jsonService.fromJson(jsonMap.get("assignment_id_project"), workNumbersType);
			if (CollectionUtilities.isEmpty(workNumbers)) {
				messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.no_assignments");
				return response;
			}
			projectNumber = Long.parseLong(jsonMap.get("project_id_project"));
			clientNumber = Long.parseLong(jsonMap.get("client_id_project"));
		} catch (Exception e) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.error");
			return response;
		}
		try {
			// Called for authorization validation side-effects
			getWorks(workNumbers, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO
			), ImmutableSet.of(
				AuthorizationContext.ADMIN,
				AuthorizationContext.BUYER
			), "change_project");
		} catch (Exception e) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.access");
			return response;
		}
		List<AbstractWork> works = workService.findWorkByWorkNumbers(workNumbers);
		for (AbstractWork work : works) {
			if (work.getClientCompany() == null) {
				messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.noclient");
				return response;
			}
			Long workClientId = work.getClientCompany().getId();
			if (!workClientId.equals(clientNumber)) {
				messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.differentclients");
				return response;
			}
			workIds.add(work.getId());
		}
		projectService.addWorksToProject(workIds, projectNumber);

		messageHelper.addMessage(response.setSuccessful(true), "assignment.bulk_project_change.success");
		return response;
	}


	@RequestMapping(
		value = "/get_clients_projects",
		method = GET)
	public @ResponseBody AjaxResponseBuilder getProjectAssignments(
		@RequestParam("work_numbers") List<String> workNumbers, HttpServletRequest httpRequest) throws IOException {

		AjaxResponseBuilder response = new AjaxResponseBuilder();
		User user = userService.findUserById(getCurrentUser().getId());

		if (CollectionUtilities.isEmpty(workNumbers)) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.no_assignments");
			return response;
		}

		try {
			// Called for authorization validation side-effects
			getWorks(workNumbers, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO
			), ImmutableSet.of(
				AuthorizationContext.ADMIN,
				AuthorizationContext.BUYER
			), "change_project");
		} catch (Exception e) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.access");
			return response;
		}
		List<com.workmarket.domains.work.model.Work> works = workListFetcherService.fetchValidatedWork(user, workNumbers, response, "assignment.reschedule");


		if (works.isEmpty()) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.nodata");
			return response;
		}

		Map<Long, String> clients = formOptionsDataHelper.getClients(getCurrentUser());
		if (!clients.isEmpty()) {
			response.addData("clients", clients);

			DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
			request.setSortableColumnMapping(ImmutableMap.of(
				0, ProjectPagination.SORTS.NAME.toString(),
				1, ProjectPagination.SORTS.CLIENT.toString(),
				2, ProjectPagination.SORTS.OWNER.toString(),
				3, ProjectPagination.SORTS.DUE_DATE.toString()
			));

			ProjectPagination projectsPagination = new ProjectPagination(true);
			projectsPagination.setStartRow(0);
			projectsPagination.setSortColumn(request.getSortColumn());
			projectsPagination.setSortDirection(request.getSortColumnDirection());
			projectsPagination = projectService.findProjectsForCompany(getCurrentUser().getCompanyId(), projectsPagination);
			response.addData("projects", CollectionUtilities.extractPropertiesList(projectsPagination.getResults(), "id", "name", "clientCompany.id"));
		} else {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.noclient");
			return response;
		}

		messageHelper.addMessage(response.setSuccessful(true), "success");
		return response;
	}

	@RequestMapping(
		value = "/remove_attachment",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder removeAttachment(
		@RequestParam(value = "work_id") String workNumber,
		@RequestParam(value = "asset_id", required = false) Long assetId) {

		AbstractWork work = getWorkByNumber(workNumber);

		AjaxResponseBuilder response = AjaxResponseBuilder.success();
		MessageBundle bundle = messageHelper.newBundle();

		try {
			assetManagementService.removeAssetFromWork(assetId, work.getId());
			messageHelper.addSuccess(bundle, "assignment.remove_attachment.success");

			return response.setMessages(bundle.getSuccess());
		} catch (Exception e) {
			logger.error("", e);
		}

		messageHelper.addError(bundle, "assignment.remove_attachment.exception");

		return response
			.setSuccessful(false)
			.setMessages(bundle.getErrors());
	}

	@RequestMapping(
		value = "/remove_deliverables_at_position",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder removeAttachment(
		@RequestParam(value = "work_id") String workNumber,
		@RequestParam(value = "deliverable_requirement_id") Long deliverableRequirementId,
		@RequestParam Integer position) {

		AbstractWork work = getWorkByNumber(workNumber);

		AjaxResponseBuilder response = AjaxResponseBuilder.success();

		try {
			eventRouter.sendEvent(new DeleteDeliverableEvent(work.getId(), deliverableRequirementId, position));
			return response.addMessage(messageHelper.getMessage("assignment.remove_attachment.success"));
		} catch (Exception e) {
			logger.error("", e);
		}

		return response
			.setSuccessful(false)
			.addMessage(messageHelper.getMessage("assignment.remove_attachment.success"));
	}

	@RequestMapping(
		value = "/remove_attachments",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder removeAttachmentsNew(
		@RequestParam("work_numbers") List<String> workNumbers,
		@RequestParam(value = "asset_id", required = false) String Uuid) {

		RemoveAttachmentsEvent event = workEventFactory.createRemoveAttachmentsEvent(workNumbers,
				userService.findUserById(getCurrentUser().getId()), "remove_attachment", "assignment.remove_attachment", Uuid);
		return workEventService.doAction(event);
	}

	@RequestMapping(
		value = "/confirmation/{workNumber}",
		method = GET)
	public String confirmation(@PathVariable("workNumber") String workNumber, RedirectAttributes flash) throws WorkUnauthorizedException {
		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		Map<String, Object> retMap = doConfirmAssignment(workNumber);

		/* Check if doConfirmAssignment was successful.  If not, show the errors it generated. */
		if (!(Boolean) retMap.get("successful")) {
			for (Object error : (ArrayList) retMap.get("errors")) {
				messageHelper.addError(bundle, (String) error);
			}
			return "redirect:/assignments/details/" + workNumber;
		} else {
			messageHelper.addSuccess(bundle, "assignment.confirmation.success");
			return "redirect:/assignments/details/" + workNumber;
		}
	}

	/**
	 * Returns data required to render the "Payment Details" section
	 * on the assignment dashboard for assignments pending approval.
	 *
	 * @param workNumber
	 * @return
	 */
	@RequestMapping(
		value = "/get_assignment_payment_info/{workNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody public AjaxResponseBuilder getAssignmentPaymentInfo(@PathVariable("workNumber") String workNumber, HttpServletRequest request) {
		Set<WorkRequestInfo> includes = ImmutableSet.of(
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.PAYMENT_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.ASSETS_INFO
		);

		AjaxResponseBuilder responseBuilder = new AjaxResponseBuilder();

		try {
			WorkRequest workRequest = new WorkRequest(getCurrentUser().getId(), workNumber, includes);
			WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);
			Work work = workResponse.getWork();
			boolean isInternal = request.isUserInRole("ROLE_INTERNAL");

			Set<RequestContext> requestContexts = workResponse.getRequestContexts();
			if (requestContexts == null || (requestContexts.contains(RequestContext.UNRELATED) && !isInternal)) {
				throw new HttpException401()
					.setMessageKey("assignment.details.notavailable")
					.setRedirectUri("redirect:/assignments");
			}

			if (work.isSetDeliverableAssets()) {
				TreeSet<DeliverableAsset> closingAssets = work.getDeliverableAssets();
				responseBuilder.addData("closingAssets", closingAssets);
			}

			return responseBuilder
				.setSuccessful(true)
				.addData("resolution", work.getResolution())
				.addData("configuration", work.getConfiguration())
				.addData("pricing", work.getPricing())
				.addData("payment", work.getPayment());

		} catch (Exception ex) {
			logger.error("error getting assignment payment info for id={}", new Object[]{workNumber}, ex);

			return new AjaxResponseBuilder().setSuccessful(false);
		}
	}

	@RequestMapping(
		value = "/accept_work_on_behalf/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder acceptWorkOnBehalf(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "note", required = false) String note,
		@RequestParam(value = "workerNumber", required = false) String workerNumber,
		@RequestBody String modelStr,
		HttpServletRequest request,
		Model model) {

		if (workerNumber == null) {
			String modelData = StringEscapeUtils.unescapeHtml4(modelStr);
			JsonElement modelObj = new JsonParser().parse(modelData);
			workerNumber = modelObj.getAsJsonObject().get("workerNumber").toString();
		}

		return workDetailsControllerHelperService.acceptWorkOnBehalf(workNumber, note, workerNumber, request, model, null);
	}

	// TODO: when we do real Delegate, nuke this craziness and get it into a service.
	// Merge the logic with CartController "/push_to_assignment"
	@RequestMapping(
		value = "/assign_work_to_employee/{workId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder assignWorkToEmployee(
		@PathVariable("workId") String workNumber,
		@RequestParam(value = "note", required = false) String note,
		HttpServletRequest request,
		Model model) {

		AjaxResponseBuilder responseBody = new AjaxResponseBuilder().setRedirect("/assignments/details/" + workNumber);

		WorkRequest workRequest = new WorkRequest()
			.setUserId(getCurrentUser().getId())
			.setWorkNumber(workNumber)
			.setIncludes(ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.INDUSTRY_INFO
			));
		ExtendedUserDetails userDetails = getCurrentUser();
		String userNumber = userDetails.getUserNumber();

		// Authorize
		if (!userDetails.isInternal() && !userDetails.hasAnyRoles("ACL_DEPUTY")) {
			messageHelper.addMessage(responseBody, "assignment.not_authorized");
			return responseBody.setSuccessful(false);
		}

		WorkResponse workResponse;
		try {
			workResponse = tWorkFacadeService.findWork(workRequest);
		} catch (WorkActionException e) {
			messageHelper.addMessage(responseBody, "search.cart.push.assignment.invalid_work");
			return responseBody.setSuccessful(false);
		}

		if (!workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN)) {
			messageHelper.addMessage(responseBody, "search.cart.push.assignment.not_authorized");
			return responseBody.setSuccessful(false);
		}

		// Validate Cart for Assigning
		if (!cartServiceInterface.validateCartForAssign(userNumber)) {
			messageHelper.addMessage(responseBody, "search.cart.push.assignment.invalid_assign_attempt");
			return responseBody.setSuccessful(false);
		}

		// Send the assignment to the user; report any failures.
		AddResourcesToWorkResponse response;
		try {
			response = cartServiceInterface.pushCartToAssignment(userNumber, workNumber);
			if (response.getUserMapSize() != 1) {
				messageHelper.addMessage(responseBody, "search.cart.push.assignment.invalid_assign_attempt");
				return responseBody.setSuccessful(false);
			}
		} catch (UserNotFoundException e) {
			messageHelper.addMessage(responseBody, "search.cart.push.assignment.invalid_user");
			return responseBody.setSuccessful(false);
		}

		// Check the response of the sending of the assignment
		// It could be successful, or it could have a specific error message (invalid field, etc.)
		Map<WorkAuthorizationResponse, Set<String>> userMap = response.getUserMap();
		String userId = "";
		if (userMap != null) {
			if (userMap.size() != 1) {
				messageHelper.addMessage(responseBody, "search.cart.push.assignment.invalid_assign_attempt");
				return responseBody.setSuccessful(false);
			}

			WorkAuthorizationResponse statusType = userMap.keySet().iterator().next();

			String messageKey = statusType.getMessagePropertyKey();
			int size = userMap.get(statusType).size();
			String workResourceUserNumber = userMap.get(statusType).iterator().next();
			if (isNotBlank(workResourceUserNumber)) {
				userId = workResourceUserNumber;
			}
			String label = StringUtilities.pluralize("resource", size);

			if (WorkAuthorizationResponse.SUCCEEDED.equals(statusType) || WorkAuthorizationResponse.ALREADY_ADDED.equals(statusType)) {
				messageHelper.addMessage(responseBody, messageKey, size, label, workNumber);
			} else if (WorkAuthorizationResponse.INVALID_INDUSTRY_FOR_RESOURCE.equals(statusType)) {
				messageHelper.addMessage(responseBody, messageKey, userMap.get(statusType).size());
			} else {
				messageHelper.addMessage(responseBody, messageKey, size, label, workNumber);
			}

			if (!(userMap.containsKey(WorkAuthorizationResponse.SUCCEEDED) || userMap.containsKey(WorkAuthorizationResponse.ALREADY_ADDED))) {
				return responseBody.setSuccessful(false);
			}
		} else {
			messageHelper.addMessage(responseBody, "search.cart.push.assignment.empty_cart");
			return responseBody.setSuccessful(false);
		}

		// Assign the sent assignment to the employee
		try {
			WorkActionRequest actionRequest = new WorkActionRequest(workNumber);
			actionRequest.setResourceUserNumber(userId);
			actionRequest.setOnBehalfOfUserNumber(userDetails.getUserNumber());
			if (userDetails.isMasquerading()) {
				ExtendedUserDetails masqueradeUser = userDetails.getMasqueradeUser();
				actionRequest.setMasqueradeUserNumber(masqueradeUser.getUserNumber());
			}

			AcceptWorkOfferRequest acceptWorkOfferRequest = new AcceptWorkOfferRequest();
			acceptWorkOfferRequest.setWorkAction(actionRequest);
			WorkActionResponse actionResponse = workService.acceptWorkOnBehalf(acceptWorkOfferRequest);

			// If the assignment was assigned successfully, we return successful
			if (actionResponse.getResponseCode() == WorkActionResponseCodeType.SUCCESS) {

				workResourceService.setWorkResourceAppointmentFromWork(workService.findWorkId(workNumber));

				// Only add the corresponding note if we were successful
				if (StringUtils.isNotEmpty(note)) {
					NoteDTO dto = new NoteDTO();
					dto.setContent(note);
					workNoteService.addNoteToWork(workResponse.getWork().getId(), dto, userService.findUserByUserNumber(userNumber));
				}

				sendAcceptedWorkDetailsPDFtoResource(workNumber, userService.findUserId(userId), request, model);
				messageHelper.addMessage(responseBody, "assignment.assign.success");
				return responseBody.setSuccessful(true);
			} else {
				messageHelper.addMessage(responseBody, "assignment.assign.exception");
				return responseBody.setSuccessful(false);
			}
		} catch (Exception ex) {
			messageHelper.addMessage(responseBody, "assignment.assign.exception");
			return responseBody.setSuccessful(false);
		}
	}

	@RequestMapping(
		value = "/complete_work_on_behalf/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder completeWorkOnBehalf(
		@PathVariable("workNumber") String workNumber,
		@Valid WorkCompleteAndApproveForm form,
		BindingResult bindingResult,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return response
				.setSuccessful(false)
				.setMessages(bundle.getAllMessages());
		}

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.ACTIVE_RESOURCE_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN,
			AuthorizationContext.BUYER
		), "complete_on_behalf");


		// User authorization
		if (!getCurrentUser().hasAnyRoles("PERMISSION_APPROVEWORK")
			|| (!workResponse.getWork().getConfiguration().isSetPaymentTermsDays() && getCurrentUser().isMasquerading())) {
			throw new HttpException401()
				.setMessageKey("assignment.not_authorized")
				.setRedirectUri("/assignments/details/" + workNumber);
		}

		if (form.getCustomFieldGroup() != null) {
			CustomFieldGroupSet set = new CustomFieldGroupSet();
			set.add(form.getCustomFieldGroup());
			AjaxResponseBuilder ajaxResp = this.saveCustomFields(workNumber, true, false, set, bindingResult, flash);
			flash.addFlashAttribute("bundle", bundle);

			if (!ajaxResp.isSuccessful()) {
				return ajaxResp;
			}
		}

		WorkCompleteForm workCompleteForm = new WorkCompleteForm();
		workCompleteForm.setAdditional_expenses(form.getAdditional_expenses());
		workCompleteForm.setCollect_tax(form.getCollect_tax());
		workCompleteForm.setHours(form.getHours());
		workCompleteForm.setMinutes(form.getMinutes());
		workCompleteForm.setOverride_price(form.getOverride_price());
		workCompleteForm.setResolution(form.getResolution());
		workCompleteForm.setShare(form.getShare());
		workCompleteForm.setTax_percent(form.getTax_percent());
		workCompleteForm.setUnits(form.getUnits());

		try {
			this.complete(workNumber, flash, workCompleteForm, bindingResult, workResponse.getWork().getActiveResource().getUser().getId());
		} catch (HttpException400 e) {
			messageHelper.addError(bundle, e.getMessageKey());
			return response
				.setSuccessful(false)
				.setMessages(bundle.getAllMessages());
		} catch (HttpException401 e) {
			messageHelper.addError(bundle, e.getMessageKey());
			return response
				.setSuccessful(false)
				.setMessages(bundle.getAllMessages());
		}

		if (flash.getFlashAttributes().get("bundle") != null && ((MessageBundle) flash.getFlashAttributes().get("bundle")).hasErrors()) {
			bundle = (MessageBundle) flash.getFlashAttributes().get("bundle");
			return response
				.setSuccessful(false)
				.setMessages(bundle.getAllMessages());
		}

		CloseWorkDTO closeWorkDTO = new CloseWorkDTO();
		closeWorkDTO.setArrivedOnTime(form.getArrivedOnTime());
		closeWorkDTO.setBlockResource(form.getBlockResource());
		closeWorkDTO.setCompletedOnTime(form.getCompletedOnTime());
		closeWorkDTO.setRating(form.getRating());
		closeWorkDTO.setShareRating(form.getShareRating());

		if (closeWorkDTO.hasRating() && closeWorkDTO.isShareRating()) {
			closeWorkDTO.getRating().setRatingSharedFlag(Boolean.TRUE);
			closeWorkDTO.getRating().setReviewSharedFlag(Boolean.TRUE);
		}

		workPayController.payWorkResource(bundle, workNumber, closeWorkDTO);

		flash.addFlashAttribute("bundle", bundle);

		if (!bundle.hasErrors()) {
			messageHelper.addSuccess(bundle, "assignment.complete_on_behalf.success");
			return response.setSuccessful(true);
		} else {
			return response
				.setSuccessful(false)
				.setMessages(bundle.getAllMessages());
		}
	}

	@RequestMapping(
		value = "/decline_work_on_behalf/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> declineWorkOnBehalf(
		@PathVariable("id") String workNumber,
		@RequestParam(value = "note", required = false) String note,
		@RequestParam(value = "workerNumber", required = false) String workerNumber,
		@RequestParam(value = "action_code", required = false) int actionCode) {

		MessageBundle bundle = messageHelper.newBundle();
		ExtendedUserDetails userDetails = getCurrentUser();
		Boolean success = Boolean.FALSE;

		try {
			WorkRequest workRequest = new WorkRequest(userDetails.getId(), workNumber,
				ImmutableSet.of(
					WorkRequestInfo.CONTEXT_INFO,
					WorkRequestInfo.ACTIVE_RESOURCE_INFO
				));
			WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);

			if (workResponse == null) {
				messageHelper.addError(bundle, "assignment.details.notfound");
			} else {
				// Check work permissions.
				if (userDetails.isInternal() ||
					(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN) &&
						authenticationService.userHasAclRole(userDetails.getId(), AclRole.ACL_DEPUTY))) {

					try {
						if (StringUtils.length(note) < 3) {
							messageHelper.addError(bundle, "NotEmpty", "Note");
						}

						Work work = workResponse.getWork();
						if (work != null && work.getActiveResource() != null
							&& work.getActiveResource().getUser().getUserNumber().equals(workerNumber)) {
							messageHelper.addError(bundle, "assignment.decline.invalid_action");
						}

						if (!bundle.hasErrors()) {
							WorkActionRequest actionRequest = new WorkActionRequest(workNumber);
							actionRequest.setResourceUserNumber(workerNumber);
							actionRequest.setOnBehalfOfUserNumber(userDetails.getUserNumber());

							if (userDetails.isMasquerading()) {
								ExtendedUserDetails masqueradeUser = userDetails.getMasqueradeUser();
								actionRequest.setMasqueradeUserNumber(masqueradeUser.getUserNumber());
							}

							DeclineWorkOfferRequest declineRequest = new DeclineWorkOfferRequest();
							declineRequest.setWorkAction(actionRequest);
							declineRequest.setNote(note);
							declineRequest.setActionCode(DeclineWorkActionType.findByValue(actionCode));

							WorkActionResponse actionResponse = thriftWorkService.declineWorkOnBehalf(declineRequest);

							if (actionResponse.getResponseCode() == WorkActionResponseCodeType.SUCCESS) {
								success = Boolean.TRUE;
							}
						}
					} catch (Exception ex) {
						messageHelper.addError(bundle, "assignment.exception");
					}
				} else {
					messageHelper.addError(bundle, "assignment.not.authorized");
				}
			}
		} catch (Exception ex) {
			logger.error("error getting template for workNumber={}", new Object[]{workNumber}, ex);
			messageHelper.addError(bundle, "assignment.contact.exception");
		}

		return CollectionUtilities.newObjectMap("success", success, "messages", bundle.getAllMessages());
	}

	@RequestMapping(
		value = "/ask_question_on_behalf/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder askQuestionOnBehalf(
		@PathVariable("id") String workNumber,
		@RequestParam(value = "workerNumber", required = false) String workerNumber,
		@RequestParam(value = "question", required = false) String question) {

		MessageBundle bundle = messageHelper.newBundle();
		ExtendedUserDetails userDetails = getCurrentUser();
		Boolean success = Boolean.FALSE;

		AjaxResponseBuilder response = new AjaxResponseBuilder();
		response.setSuccessful(success);

		try {
			WorkRequest workRequest = new WorkRequest(userDetails.getId(), workNumber, ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO));
			WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);

			if (workResponse == null) {
				messageHelper.addError(bundle, "assignment.details.notfound");
			} else {
				// Check work permissions.
				if (userDetails.isInternal() ||
					(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN) &&
						authenticationService.userHasAclRole(userDetails.getId(), AclRole.ACL_DEPUTY))) {

					try {
						if (StringUtils.isNotEmpty(question) && (question.length() < 3)) {
							messageHelper.addError(bundle, "NotEmpty", "Question");
						}

						if (!bundle.hasErrors()) {
							WorkActionRequest actionRequest = new WorkActionRequest(workNumber);
							actionRequest.setResourceUserNumber(workerNumber);
							actionRequest.setOnBehalfOfUserNumber(userDetails.getUserNumber());

							if (userDetails.isMasquerading()) {
								ExtendedUserDetails masqueradeUser = userDetails.getMasqueradeUser();
								actionRequest.setMasqueradeUserNumber(masqueradeUser.getUserNumber());
							}

							WorkQuestionRequest questionRequest = new WorkQuestionRequest();
							questionRequest.setWorkAction(actionRequest);
							questionRequest.setQuestion(question);

							WorkActionResponse actionResponse = thriftWorkService.askQuestion(questionRequest);

							if (actionResponse.getResponseCode() == WorkActionResponseCodeType.SUCCESS) {
								response.setSuccessful(Boolean.TRUE);
								messageHelper.addSuccess(bundle, "assignment.ask_question.success");
							}
						}
					} catch (Exception ex) {
						messageHelper.addError(bundle, "assignment.exception");
					}
				} else {
					messageHelper.addError(bundle, "assignment.not.authorized");
				}
			}
		} catch (Exception ex) {
			logger.error("error getting template for workNumber={}", new Object[]{workNumber}, ex);
			messageHelper.addError(bundle, "assignment.contact.exception");
		}

		response.setMessages(bundle.getAllMessages());

		return response;
	}

	@RequestMapping(
		value = "/add_note_on_behalf/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder addNoteOnBehalf(
		@PathVariable("id") String workNumber,
		@RequestParam(value = "workerNumber", required = false) String workerNumber,
		@RequestParam(value = "note", required = false) String note,
		@RequestParam(value = "action_code", required = false) Integer actionCode) {

		MessageBundle bundle = messageHelper.newBundle();
		ExtendedUserDetails userDetails = getCurrentUser();
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		try {
			WorkRequest workRequest = new WorkRequest(userDetails.getId(), workNumber, ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO));
			WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);

			if (workResponse == null) {
				messageHelper.addError(bundle, "assignment.details.notfound");
			} else {
				// Check work permissions.
				if (userDetails.isInternal() ||
					(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN) &&
						authenticationService.userHasAclRole(userDetails.getId(), AclRole.ACL_DEPUTY))) {

					try {
						if (StringUtils.isNotEmpty(note) && (note.length() < 3)) {
							messageHelper.addError(bundle, "NotEmpty", "Note");
						}

						if (!bundle.hasErrors()) {
							WorkActionRequest actionRequest = new WorkActionRequest(workNumber);
							actionRequest.setResourceUserNumber(workerNumber);
							actionRequest.setOnBehalfOfUserNumber(userDetails.getUserNumber());


							if (userDetails.isMasquerading()) {
								ExtendedUserDetails masqueradeUser = userDetails.getMasqueradeUser();
								actionRequest.setMasqueradeUserNumber(masqueradeUser.getUserNumber());
							}

							ResourceNoteRequest noteRequest = new ResourceNoteRequest();
							noteRequest.setWorkAction(actionRequest);
							noteRequest.setNote(note);
							noteRequest.setActionType(ResourceNoteActionType.findByValue(actionCode));

							WorkActionResponse actionResponse = thriftWorkService.noteWorkResource(noteRequest);

							if (actionResponse.getResponseCode() == WorkActionResponseCodeType.SUCCESS) {
								response.setSuccessful(Boolean.TRUE);
								messageHelper.addSuccess(bundle, "assignment.add_note.success");
							}
						}
					} catch (Exception ex) {
						messageHelper.addError(bundle, "assignment.exception");
					}
				} else {
					messageHelper.addError(bundle, "assignment.not.authorized");
				}
			}
		} catch (Exception ex) {
			logger.error("error getting template for workNumber={}", new Object[]{workNumber}, ex);
			messageHelper.addError(bundle, "assignment.contact.exception");
		}

		response.setMessages(bundle.getAllMessages());

		return response;
	}

	// TODO: new dashboard. remove this comment when the old dashboard is retired.
	@RequestMapping(
		value = "/bulk_approve_payment",
		method = POST)
	public @ResponseBody AjaxResponseBuilder bulkApprovePaymentWithEvent(@RequestBody WorkNumbers workNumbersModel) {
		List<String> workNumbers = workNumbersModel.getWorkNumbers();
		int numRelevant = workNumbersModel.getNumRelevant();
		MessageBundle bundle = messageHelper.newBundle();
		ExtendedUserDetails user = getCurrentUser();

		if (!user.getApproveWorkCustomAuth()) {
			throw new HttpException401();
		}
		AjaxResponseBuilder response = workEventService.doAction(
			workEventFactory.createApproveForPaymentEvent(
				workNumbers,
				userService.findUserById(user.getId()),
				"assignment.bulk_approved",
				"assignment.bulk_approved"
			)
		);
		messageHelper.addSuccessOrErrorMessage(
			bundle, response, "assignment.bulk_approved",
			numRelevant, pluralize("assignment", numRelevant)
		);
		response.setMessages(bundle.getAllMessages());

		return response;
	}

	// TODO: old dashboard. remove this method when the old dashboard is retired.
	@RequestMapping(
		value = "/bulk_approve",
		method = POST)
	public String bulkApproveWithEvent(@RequestParam("ids") List<String> workNumbers, RedirectAttributes flash) {
		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		ExtendedUserDetails user = getCurrentUser();

		if (!user.getApproveWorkCustomAuth()) {
			throw new HttpException401();
		}
		AjaxResponseBuilder response = workEventService.doAction(
				workEventFactory.createApproveForPaymentEvent(
						workNumbers,
						userService.findUserById(user.getId()),
						"assignment.bulk_approved",
						"assignment.bulk_approved")
		);
		messageHelper.addSuccessOrErrorMessage(bundle, response, "assignment.bulk_approved", workNumbers.size(), pluralize("assignment", workNumbers.size()));

		return "redirect:/assignments";
	}


	@RequestMapping(
		value = "/bulk_approve",
		method = GET)
	public String listBulkApprove(@RequestParam("ids[]") List<String> workNumbers, Model model) {
		ExtendedUserDetails userDetails = getCurrentUser();

		WorkReportPagination pagination = new WorkReportPagination();
		pagination.setReturnAllRows();

		WorkReportPagination results = workReportService.findAllWorkByWorkNumber(
				userDetails.getCompanyId(), userDetails.getId(), workNumbers, pagination
		);

		model.addAttribute("results", results.getResults());
		model.addAttribute("ids", workNumbers);
		model.addAttribute("totalDue", results.getPriceTotal());

		return "web/partials/assignments/bulk_approve";
	}

	@RequestMapping(
		value = "/hours_worked/{workNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getHoursWorked(@PathVariable String workNumber) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (!StringUtils.isNumeric(workNumber)) {
			return response;
		}

		AbstractWork work;
		try {
			work = getAndAuthorizeWorkByNumber(workNumber, ImmutableList.of(
				WorkContext.ACTIVE_RESOURCE,
				WorkContext.DISPATCHER,
				WorkContext.COMPANY_OWNED,
				WorkContext.OWNER),
				"hours_worked");
		} catch (WorkUnauthorizedException e) {
			return response;
		}

		if (work == null) {
			return response;
		}

		response.setData(workService.findActiveWorkerTimeWorked(work.getId()));

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/get_labels_for_work.json",
		method = POST)
	@ResponseBody AjaxResponseBuilder getLabelsForBulkRemoveJSON(@RequestBody RemoveLabelsDTO dto) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();
		response.setSuccessful(false);
		if (CollectionUtils.isEmpty(dto.getWorkNumbers())) {
			response.addMessage(messageHelper.getMessage("assignment.add_bulk_label.no_assignments"));
			return response;
		}

		List<WorkSubStatusType> workLabels = new ArrayList<>();
		List<WorkResponse> workResponses;

		try {
			workResponses = getWorks(dto.getWorkNumbers(), ImmutableSet.of(
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.SCHEDULE_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ADMIN,
				AuthorizationContext.ACTIVE_RESOURCE
			), "remove_label");
		} catch (Exception e) {
			response.addMessage(messageHelper.getMessage("assignment.add_bulk_label.no_permission"));
			return response;
		}

		for (WorkResponse workResponse : workResponses) {
			Long workId = workResponse.getWork().getId();
			workLabels.addAll(workSubStatusService.findAllUnResolvedSubStatuses(workId));
		}

		Map<Long, String> labelsMap = Maps.newTreeMap();
		if (CollectionUtils.isEmpty(workLabels)) {
			response.addMessage(messageHelper.getMessage("assignment.remove_bulk_label.no_labels_to_remove"));
			return response;
		}

		for (WorkSubStatusType label : workLabels) {
			labelsMap.put(label.getId(), label.getDescription());
		}
		response.setSuccessful(true);
		response.setData(CollectionUtilities.newObjectMap("labels", labelsMap));
		return response;
	}

	// TODO - Micah - This can go away when we retire the old dashboard
	@RequestMapping(
		value = "/label_remove_multiple",
		method = RequestMethod.GET)
	public String getLabelsForBulkRemove(@RequestParam List<String> workNumbers, Model model) {

		List<WorkSubStatusType> workLabels = new ArrayList<>();
		List<WorkResponse> workResponses;
		Map<Long, Map<String, Object>> labelsMap = Maps.newLinkedHashMap();

		if (CollectionUtils.isEmpty(workNumbers)) {
			model.addAttribute("success", false);
			model.addAttribute("errorMessages", messageHelper.getMessage("assignment.add_bulk_label.no_assignments"));

			return "web/partials/assignments/remove_bulk_label";
		}
		try {
			workResponses = getWorks(workNumbers, ImmutableSet.of(
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.SCHEDULE_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ADMIN,
				AuthorizationContext.ACTIVE_RESOURCE
			), "remove_label");
		} catch (Exception e) {
			model.addAttribute("success", false);
			model.addAttribute("errorMessages", messageHelper.getMessage("assignment.add_bulk_label.no_permission"));

			return "web/partials/assignments/remove_bulk_label";
		}

		for (WorkResponse workResponse : workResponses) {
			Long workId = workResponse.getWork().getId();
			workLabels.addAll(workSubStatusService.findAllUnResolvedSubStatuses(workId));
		}

		if (CollectionUtils.isEmpty(workLabels)) {
			model.addAttribute("success", false);
			model.addAttribute("errorMessages", messageHelper.getMessage("assignment.remove_bulk_label.no_labels_to_remove"));

			return "web/partials/assignments/remove_bulk_label";
		} else {
			for (WorkSubStatusType label : workLabels) {
				labelsMap.put(label.getId(), CollectionUtilities.newObjectMap(
					"id", label.getId(),
					"description", label.getDescription()
				));
			}
			model.addAttribute("success", true);
			model.addAttribute("errorMessages", "none");
			model.addAttribute("labels", labelsMap);
			model.addAttribute("label_json", jsonService.toJson(labelsMap));

			model.addAttribute("success", true);

			return "web/partials/assignments/remove_bulk_label";
		}
	}

	// TODO - Micah - this is setup for pure JSON <--> object mapping
	// Eventually the logic will be moved in here and the method below will disappear
	@RequestMapping(
		value = "/label_remove_multiple.json",
		method = POST)
	@ResponseBody AjaxResponseBuilder bulkLabelRemoveJSON(@RequestBody RemoveLabelsDTO dto) {

		RemoveLabelForm removeLabelForm = new RemoveLabelForm();
		removeLabelForm.setNote(dto.getNote());
		return bulkLabelRemove(
			dto.getWorkNumbers(),
			dto.getLabelIds(),
			removeLabelForm
		);
	}

	@RequestMapping(
		value = "/label_remove_multiple",
		method = RequestMethod.POST)
	public @ResponseBody AjaxResponseBuilder bulkLabelRemove(
		@RequestParam("workNumbers") List<String> workNumbers,
		@RequestParam("labelIds") List<Long> labelIds,
		@ModelAttribute("form") RemoveLabelForm form) {

		AjaxResponseBuilder response = new AjaxResponseBuilder();
		List<Long> validLabelIds = new ArrayList<>();
		User user = userService.findUserById(getCurrentUser().getId());

		String note = form.getNote();

		if (CollectionUtils.sizeIsEmpty(labelIds)) {
			messageHelper.addMessage(response, "NotNull.add_label_form.label_id");
			return response.setSuccessful(false);
		}

		for (Long labelId : labelIds) {
			if (isValidLabelForRemoval(user, labelId)) {
				validLabelIds.add(labelId);
			}
		}

		List<com.workmarket.domains.work.model.Work> works = workListFetcherService.fetchValidatedWork(user, workNumbers, response, "assignment.remove_bulk_label");
		Set<Long> workIds = new HashSet<>(extract(works, on(com.workmarket.domains.work.model.Work.class).getId()));

		if (CollectionUtils.isNotEmpty(works)) {
			logger.info(String.format("Will try to remove %d labels from %d assignments", CollectionUtils.size(validLabelIds), CollectionUtils.size(works)));

			BulkLabelRemovalEvent bulkLabelRemovalEvent = workEventFactory.createBulkLabelRemovalEvent(
				workIds,
				user,
				note,
				labelIds,
				"label_remove_multiple",
				"assignment.remove_bulk_label"
			);

			return workEventService.doAction(bulkLabelRemovalEvent);
		} else {
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(Lists.newArrayList(messageHelper.getMessage("assignment.remove_bulk_label.remove.none.assignments.applicable")))
				.setRedirect("label_remove_multiple");
		}
	}


	@RequestMapping(
		value = "/cancel_works_multiple",
		method = RequestMethod.GET)
	public String getWorksForBulkCancel(
		@RequestParam("workNumbers") List<String> workNumbers,
		Model model) {

		List<WorkResponse> workResponses;

		if (CollectionUtils.isEmpty(workNumbers)) {
			model.addAttribute("success", false);
			model.addAttribute("errorMessages", messageHelper.getMessage("assignment.bulk_cancel_works.no.assignments"));

			return "web/partials/assignments/bulk_cancel_works";
		}
		try {
			workResponses = getWorks(workNumbers, ImmutableSet.of(
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.CONTEXT_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ADMIN,
				AuthorizationContext.ACTIVE_RESOURCE
			), "cancel_work");
		} catch (Exception e) {
			model.addAttribute("success", false);
			model.addAttribute("errorMessages", messageHelper.getMessage("assignment.bulk_cancel_works.no_permission"));

			return "web/partials/assignments/bulk_cancel_works";
		}

		model.addAttribute("success", true);
		model.addAttribute("modelDescription", messageHelper.getMessage("assignment.bulk_cancel_works.action.description", workResponses.size()));

		return "web/partials/assignments/bulk_cancel_works";
	}

	private BulkActionAjaxResponseBuilder handleCancel(CancelWorkNumbersDTO dto, BindingResult bindingResult) {
		MessageBundle bundle = messageHelper.newBundle();
		List<com.workmarket.domains.work.model.Work> works;
		Set<Long> workIdsToSend = Sets.newHashSet();

		BulkActionAjaxResponseBuilder finalResponse = new BulkActionAjaxResponseBuilder().setSuccessful(false);
		User user = userService.findUserById(getCurrentUser().getId());

		cancelWorkValidator.validate(dto, bindingResult);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(finalResponse, bindingResult);
			return finalResponse;
		}

		try {
			works = workListFetcherService.fetchValidatedWork(user, dto.getWorkNumbers(), finalResponse, "assignment.bulk_cancel_works");
		} catch (Exception e) {
			messageHelper.addMessage(finalResponse, "assignment.cancel_works.no_permission");
			return finalResponse.setSuccessful(false);
		}

		for (com.workmarket.domains.work.model.Work work : works) {
			if (cancelWorkValidator.getWorkCancelErrors(dto, work, bundle)) {
				workIdsToSend.add(work.getId());
			}
		}

		if (bundle.hasErrors()) {
			finalResponse.setPartialErrors(bundle.getErrors());
		}

		if (workIdsToSend.size() > 0) {
			BulkCancelWorksEvent bulkCancelWorksEvent = workEventFactory.createBulkCancelWorksEvent(
				workIdsToSend,
				user,
				"cancel_works_multiple",
				"assignment.bulk_cancel_works",
				dto.getNote(),
				dto.getPrice(),
				dto.getCancellationReasonTypeCode());

			AjaxResponseBuilder eventResponse = workEventService.doAction(bulkCancelWorksEvent);
			finalResponse.setSuccessful(eventResponse.isSuccessful());
			finalResponse.setMessages(eventResponse.getMessages());
		} else {
			messageHelper.addMessage(finalResponse, "assignment.bulk_cancel_works.failure");
			finalResponse.setSuccessful(false);
		}

		return finalResponse;
	}

	// new dashboard
	@RequestMapping(
		value = "/cancel_works_multiple_json",
		method = POST)
	public @ResponseBody BulkActionAjaxResponseBuilder bulkCancelWorks(
		@RequestBody CancelWorkNumbersDTO dto,
		BindingResult bindingResult) {

		return handleCancel(dto, bindingResult);
	}

	// old dashboard
	@RequestMapping(
		value = "/cancel_works_multiple",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody BulkActionAjaxResponseBuilder oldBulkCancelWorks(
		@RequestParam("workNumbers") List<String> workNumbers,
		@ModelAttribute("form_cancel_work") CancelWorkDTO dto,
		BindingResult bindingResult) {

		CancelWorkNumbersDTO cwnDTO = new CancelWorkNumbersDTO(dto.getPrice(), dto.getCancellationReasonTypeCode(), dto.getNote(), workNumbers);
		return handleCancel(cwnDTO, bindingResult);
	}

	private boolean isValidLabelForRemoval(User user, Long labelId) {
		WorkSubStatusType label = workSubStatusService.findWorkSubStatus(labelId);
		return label != null && label.getCompany().getId().equals(user.getId());
	}

	@RequestMapping(
		value = "/reschedule_assignments_multiple",
		method = POST)
	public @ResponseBody AjaxResponseBuilder rescheduleAssignmentsMultiple(
		@RequestParam("workNumbers") List<String> workNumbers,
		@ModelAttribute("form") WorkMassRescheduleForm form,
		BindingResult bindingResult) {

		MessageBundle bundle = messageHelper.newBundle();

		final String errorRedirect = "/reschedule_assignments_multiple";

		if (CollectionUtilities.isEmpty(workNumbers)) {
			logger.error("Unable to perform mass reschedule. No work numbers were sent");
			messageHelper.addError(bundle, "assignment.reschedule.missing_work");
			return errorResponse(errorRedirect, bundle.getErrors());
		}

		// TODO refactor date/time validation since we want to pass the date-time String, not a Calendar
		final String timeZone = getCurrentUser().getTimeZoneId();
		Calendar fromTime = null;
		Calendar toTime = null;

		try {
			if (StringUtils.isNotBlank(form.getFrom())) {
				fromTime = StringUtils.isNotBlank(form.getFromtime())
						? DateUtilities.getCalendarFromDateTimeString(form.getFrom() + " " + form.getFromtime(), timeZone)
						: getDate(form.getFrom(), timeZone);
			}

			if (StringUtils.isNotBlank(form.getTo())) {
				toTime = StringUtils.isNotBlank(form.getTotime())
						? DateUtilities.getCalendarFromDateTimeString(form.getTo() + " " + form.getTotime(), timeZone)
						: getDate(form.getTo(), timeZone);
			}
		} catch (IllegalArgumentException ex) {
			return errorResponse(errorRedirect, messageHelper.getMessage("Pattern", "New date and time"));
		}

		if (fromTime == null) {
			return errorResponse(errorRedirect, messageHelper.getMessage("NotNull", "From Date"));
		}

		if (fromTime != null) {
			if (fromTime.before(Calendar.getInstance())) {
				return errorResponse(errorRedirect, messageHelper.getMessage("inpast.assignment.reschedule.from"));
			}
		}

		if (toTime != null) {
			if (toTime.before(fromTime)) {
				return errorResponse(errorRedirect, messageHelper.getMessage("inpast.assignment.reschedule.through"));
			}
		}

		User user = userService.findUserById(getCurrentUser().getId());
		AjaxResponseBuilder response = new AjaxResponseBuilder();
		List<com.workmarket.domains.work.model.Work> works =
			workListFetcherService.fetchValidatedWork(user, workNumbers, response, "assignment.reschedule");

		if (CollectionUtils.sizeIsEmpty(works)) {
			return errorResponse(errorRedirect, messageHelper.getMessage("assignment.reschedule.none.applicable"));
		}

		List<String> workNumbersToSend =
			extract(works, on(com.workmarket.domains.work.model.Work.class).getWorkNumber());
		List<com.workmarket.domains.work.model.Work> workToSend = new ArrayList<>();
		for (com.workmarket.domains.work.model.Work work : works) {
			workToSend.add(work);
		}

		final Optional<String> startDateTime = getDateTime(form.getFrom(), form.getFromtime());
		final Optional<String> startDateTimeFormat = getDateTimeFormat(form.getFrom(), form.getFromtime());
		final Optional<String> endDateTime = getDateTime(form.getTo(), form.getTotime());
		final Optional<String> endDateTimeFormat = getDateTimeFormat(form.getTo(), form.getTotime());

		if (CollectionUtils.isNotEmpty(workNumbersToSend)) {
			logger.info(String.format("Will try to reschedule %d assignments", CollectionUtils.size(works)));

			RescheduleEvent rescheduleEvent = workEventFactory.createRescheduleEvent(
					workNumbersToSend,
					workToSend,
					user,
					form.getNote(),
					"reschedule_assignments_multiple",
					"assignment.reschedule",
					startDateTime,
					startDateTimeFormat,
					endDateTime,
					endDateTimeFormat);

			return workEventService.doAction(rescheduleEvent);
		} else {
			return errorResponse(errorRedirect, messageHelper.getAllErrors(bindingResult));
		}
	}

	@VisibleForTesting
	protected Optional<String> getDateTime(final String date, final String time) {
		if (StringUtils.isBlank(date)) {
			return Optional.absent();
		}

		if (StringUtils.isBlank(time)) {
			return Optional.of(date);
		}

		return Optional.of(date + " " + time);
	}

	@VisibleForTesting
	Optional<String> getDateTimeFormat(final String date, final String time) {
		if (StringUtils.isBlank(date)) {
			return Optional.absent();
		}

		if (StringUtils.isBlank(time)) {
			return Optional.of("MM/dd/yyyy");
		}

		return Optional.of("MM/dd/yyyy hh:mmaa");
	}

	private AjaxResponseBuilder errorResponse(final String redirect, final String message) {
		return errorRedirectResponse(redirect).setMessages(ImmutableList.of(message));
	}

	private AjaxResponseBuilder errorResponse(final String redirect, final List<String> messages) {
		return errorRedirectResponse(redirect).setMessages(messages);
	}

	private AjaxResponseBuilder errorRedirectResponse(final String redirect) {
		return new AjaxResponseBuilder().setSuccessful(false).setRedirect(redirect);
	}

	@RequestMapping(
		value = "/bulk_update_client_project",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder bulkUpdateClientProject(
		@RequestParam(value = "clientId") Long clientId,
		@RequestParam(value = "projectId", required = false) Long projectId,
		@RequestParam("workNumbers") List<String> workNumbers) {

		User user = userService.findUserById(getCurrentUser().getId());
		AjaxResponseBuilder response = new AjaxResponseBuilder();
		List<com.workmarket.domains.work.model.Work> clientOrProjectChangeableWork = new ArrayList<>();
		ClientCompany client;
		Project project = null;

		if (CollectionUtilities.isEmpty(workNumbers)) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.no_assignments");
			return response;
		}

		if (clientId == null) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.noclient");
			return response;
		}

		client = crmService.findClientCompanyById(clientId);
		if (client == null) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.noclient");
			return response;
		}
		if (projectId != null) {
			project = projectService.findById(projectId);
			if (project == null) {
				messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.noprojects");
				return response;
			}
		}

		try {
			// Called for authorization validation side-effects
			getWorks(workNumbers, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO
			), ImmutableSet.of(
				AuthorizationContext.ADMIN,
				AuthorizationContext.BUYER
			), "change_project");
		} catch (Exception e) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.access");
			return response;
		}
		List<com.workmarket.domains.work.model.Work> works = workListFetcherService.fetchWork(workNumbers, response, "assignment.bulk_project_change");
		for (com.workmarket.domains.work.model.Work work : works) {
			clientOrProjectChangeableWork.add(work);
		}

		if (clientOrProjectChangeableWork.size() == 0) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment.bulk_project_change.nodata");
			return response;
		}

		BulkEditClientProjectEvent bulkEditClientProjectEvent = workEventFactory.createBulkEditClientProjectEvent(
			clientOrProjectChangeableWork,
			workNumbers,
			user,
			"bulk_edit_client_project",
			"assignment.bulk_project_change",
			client,
			project
		);

		AjaxResponseBuilder eventResponse = workEventService.doAction(bulkEditClientProjectEvent);
		response.setSuccessful(eventResponse.isSuccessful());
		response.setMessages(eventResponse.getMessages());

		return response;
	}

	private Calendar getDate(String date, String tz) {
		return DateUtilities.getCalendarFromDateString(date, tz);
	}

	@RequestMapping(
		value = "/generate_pdf/{workNumber}",
		method = GET)
	public View generatePdf(@PathVariable String workNumber, Model model) {
		return super.generatePdf(workNumber, model);
	}
}
