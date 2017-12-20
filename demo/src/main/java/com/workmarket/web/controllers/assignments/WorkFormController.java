package com.workmarket.web.controllers.assignments;

import com.google.api.client.util.Maps;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.WorkProperties;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.changelog.work.WorkPropertyChangeLog;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.option.CompanyOption;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.velvetrope.guest.WebGuest;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.domains.work.service.route.WorkRoutingVisitor;
import com.workmarket.domains.work.service.validator.WorkSaveRequestValidator;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.business.requirementsets.RequirementSetsService;
import com.workmarket.service.business.wrapper.ValidateWorkResponse;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.service.external.ShippingProviderDetectResponse;
import com.workmarket.service.external.TrackingNumberAdapter;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.option.OptionsService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.Status;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.RoutingStrategy;
import com.workmarket.thrift.work.Template;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkFormRoutingAdapter;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.SearchController;
import com.workmarket.web.editors.DateOrTimeEditor;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException405;
import com.workmarket.web.forms.work.PartGroupForm;
import com.workmarket.web.forms.work.WorkAssetForm;
import com.workmarket.web.forms.work.WorkBundleForm;
import com.workmarket.web.forms.work.WorkForm;
import com.workmarket.web.forms.work.WorkFormRouting;
import com.workmarket.web.forms.work.WorkTemplateForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.helpers.WorkAuthorizationFailureHelper;
import com.workmarket.web.helpers.WorkBundleValidationHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.models.WorkSaveCallback;
import com.workmarket.web.validators.BulkWorkRouteValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * The work form controller manages add/edit/save actions for assignments.
 * The same form JSP is used for all assignment editing activity:
 * <p/>
 * <li>add/edit assignment (+ via template)</li>
 * <li>copy assignment</li>
 * <li>add/edit template</li>
 * <p/>
 * Because each has variations in the incoming request and expected response payload,
 * a generic <code>saveWork</code> method exists which accepts a {@link com.workmarket.web.models.WorkSaveCallback}
 * for managing the specifics of each variation by implementing the callback's
 * <code>before</code>, <code>success</code> and <code>error</code> methods.
 */
@Controller
@RequestMapping("/assignments")
public class WorkFormController extends BaseWorkController {

	private static final Logger logger = LoggerFactory.getLogger(WorkFormController.class);

	@Autowired private FormOptionsDataHelper formOptionsDataHelper;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private JsonSerializationService jsonService;
	@Autowired private RequirementSetsService requirementSetsService;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private IndustryService industryService;
	@Autowired private PartService partService;
	@Autowired private TrackingNumberAdapter trackingNumberAdapter;
	@Autowired private WorkBundleValidationHelper workBundleValidationHelper;
	@Autowired private BulkWorkRouteValidator bulkWorkRouteValidator;
	@Autowired private EventRouter eventRouter;
	@Autowired protected MessageBundleHelper messageHelper;
	@Autowired private WorkRoutingVisitor workRoutingVisitor;
	@Autowired private WorkRoutingService workRoutingService;
	@Autowired private WorkAuthorizationFailureHelper workAuthorizationFailureHelper;
	@Autowired @Qualifier("companyOptionsService") protected OptionsService<Company> companyOptionsService;
	@Autowired private WorkValidationService workValidationService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private SuggestionService suggestionService;

	private static final String GROUP_ID_PARAM = "routing.groupIds";

	private List<Integer> assignmentCopyQuantities = new ImmutableList.Builder<Integer>()
		.add(1)
		.add(2)
		.add(3)
		.add(4)
		.add(5)
		.add(6)
		.add(7)
		.add(8)
		.add(9)
		.add(10)
		.build();

	private static Map<ShippingDestinationType, String> SHIPPING_DESTINATIONS = ImmutableMap.of(
		ShippingDestinationType.WORKER, "Shipped to worker",
		ShippingDestinationType.ONSITE, "Onsite",
		ShippingDestinationType.PICKUP, "Specify other location"
	);

	@ModelAttribute("templateForm")
	public WorkTemplateForm createTemplateForm() {
		return new WorkTemplateForm();
	}

	protected void authorize(WorkResponse response, String... statusTypes) {
		if (getCurrentUser().getCompanyIsSuspended()) {
			throw new HttpException401()
				.setMessageKey("work.form.company_suspended")
				.setRedirectUri("redirect:/home");
		}

		if (response == null) {
			return;
		}

		if (!CollectionUtilities.containsAny(response.getAuthorizationContexts(), AuthorizationContext.BUYER, AuthorizationContext.ADMIN)) {
			throw new HttpException401()
				.setMessageKey("work.form.not_authorized")
				.setRedirectUri("redirect:/assignments");
		}

		if (ArrayUtils.isNotEmpty(statusTypes) && !CollectionUtilities.containsAny(response.getWork().getStatus().getCode(), statusTypes)) {
			throw new HttpException405()
				.setMessageKey("work.form.invalid_status")
				.setRedirectUri("redirect:/assignments/details/" + response.getWork().getWorkNumber());
		}
	}

	// Add/edit actions
	@RequestMapping(
		value = "/clean_html",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> cleanHtmlContent(
		@RequestParam("htmlContent") String htmlContent,
		Model model) throws Exception {

		htmlContent = StringEscapeUtils.unescapeHtml(htmlContent);
		HashMap<String, Object> response = new HashMap<>();
		response.put("cleanHtml", Jsoup.clean(htmlContent, Whitelist.basic()));
		model.addAttribute("response", response);
		return response;
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String add(
		@ModelAttribute("form") WorkForm form,
		@RequestParam(value = "user", required = false) String userNumber,
		@RequestParam(value = "vendor", required = false) String vendorNumber,
		Model model) throws Exception {

		model.addAttribute("hasFeatureProjectPermission", hasFeature("projectPermission"));
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/assignments/add"));
		if (hasFeature("projectPermission")) {
			model.addAttribute("hasProjectAccess", authenticationService.hasProjectAccess(getCurrentUser().getId()));
		}

		MessageBundle messages = messageHelper.newBundle(model);

		authorize(null);

		if (getCurrentUser().getCompanyIsLocked()) {
			messageHelper.addNotice(messages, "work.form.company_locked");
		}

		if (userNumber != null) {
			User forUser = userService.findUserByUserNumber(userNumber);
			if (forUser != null) {
				model.addAttribute("assignment_for", forUser.getUserNumber());
				messageHelper.addNotice(messages, "work.form.for_user", forUser.getFullName());
			}
		}

		if (vendorNumber != null) {
			Company company = companyService.findCompanyByNumber(vendorNumber);
			if (company != null) {
				model.addAttribute("assignment_for", company.getCompanyNumber());
				messageHelper.addNotice(
					messages,
					"work.form.for_vendor",
					company.getEffectiveName() != null ? company.getEffectiveName() : company.getName());
			}
		}

		if (form.getProject() != null) {
			Project project = projectService.findById(form.getProject());
			if (project != null) {
				form.setClientcompany(project.getClientCompany().getId());
			}
		}

		Map<String, Boolean> mbo = (Map<String, Boolean>) model.asMap().get("mbo");
		form.setUseMboServices(mbo.get("mboUsageRequired"));

		ManageMyWorkMarket mmw = (ManageMyWorkMarket) model.asMap().get("mmw");
		form.setInternal_owner(getCurrentUser().getId());
		form.setSupport_contact(getCurrentUser().getId());
		form.setIndustry(getDefaultIndustry());
		form.setPayment_terms_days(mmw.getPaymentTermsDays());

		model.addAttribute("industries", formOptionsDataHelper.getIndustries());
		model.addAttribute("is_admin", getCurrentUser().hasAnyRoles("ACL_ADMIN", "ACL_MANAGER"));

		Long templateId = form.getWork_template_id();
		if (templateId != null) {
			WorkForm newForm = buildFormFromWork(form, templateId);
			model.addAttribute("form", newForm);
			model.addAttribute("template_id", templateId);

			return addForm(model, newForm);
		}

		return addForm(model, form);
	}

	@RequestMapping(
		value = "/get_project_remaining_budget",
		method = GET)
	public void getClientProjects(
		@RequestParam("id") Long projectId,
		Model model) {

		Assert.notNull(projectId, "Project Id is required");
		Project project = projectService.findById(projectId);
		boolean budgetEnabledFlag = project.getBudgetEnabledFlag();
		BigDecimal remainingBudget = project.getRemainingBudget();

		model.addAttribute("response", CollectionUtilities.newObjectMap(
			"success", true,
			"budgetEnabledFlag", budgetEnabledFlag,
			"remainingBudget", StringUtilities.formatMoneyForDisplay(remainingBudget)
		));
	}

	@RequestMapping(
		value = "/add",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String doAdd(
		final Model model,
		final RedirectAttributes redirectAttributes,
		final @ModelAttribute("form") WorkForm form,
		BindingResult bindingResult, HttpSession httpSession) throws Exception {

		WebGuest guest = new WebGuest(getCurrentUser());

		final MessageBundle messages = messageHelper.newBundle();

		// wiping out search results so that previous results aren't remembered.
		httpSession.removeAttribute(SearchController.SEARCH_FORM_SESSION_ATTRIBUTE);

		authorize(null);
		String redirect;
		form.setId(0L);
		int selectedNumberOfCopies = form.getNumberOfCopies();
		boolean isMultipleCopies = form.isMultipleCopies();

		// Make sure that first to accept assignments don't go to the work feed.
		if (form.getAssign_to_first_resource()) {
			form.setShow_in_feed(false);
		}

		if (isMultipleCopies) {
			redirect = doMultipleSaves(form, bindingResult, WorkSaveType.WORK, selectedNumberOfCopies, messages);
			if (messages.hasErrors()) {
				model.addAttribute("bundle", messages);
				return copyForm(model, form);
			}
			redirectAttributes.addFlashAttribute("bundle", messages);
		} else {
			redirect = doSave(form, bindingResult, WorkSaveType.WORK, messages, new WorkSaveCallback() {
				// TODO: Tech Debt
				// Instead of having this success callback clutter up the controller, we should move this a non-transactional service
				@Override
				public String success(WorkSaveRequest request, WorkResponse response) {
					redirectAttributes.addFlashAttribute("bundle", messages);

					String workNumber = response.getWork().getWorkNumber();

					form.setWorkNumber(workNumber);
					webHookEventService.onWorkCreated(workNumber, form.getAutotaskId());

					boolean groupRouted = form.hasRoutingToGroups();
					boolean resourceRouted = form.hasRoutingToUsers();
					boolean vendorRouted = form.hasRoutingToVendorCompanies();

					if (request.isSetAssignTo()) {
						messageHelper.addSuccess(messages, "work.form.saved");
						return String.format("redirect:/assignments/details/%s", workNumber);
					} else if (groupRouted || resourceRouted || vendorRouted) {
						if (groupRouted) {
							messageHelper.addSuccess(messages, "work.form.saved.group_routed");
						}
						if (resourceRouted) {
							messageHelper.addSuccess(messages, "work.form.saved.resource_routed");
						}
						if (vendorRouted) {
							messageHelper.addSuccess(messages, "work.form.saved.vendor_routed");
						}
						return String.format("redirect:/assignments/details/%s", workNumber);
					} else if (request.isSmartRoute()){
						messageHelper.addSuccess(messages,"work.form.saved.work_send");
						return String.format("redirect:/assignments/details/%s", workNumber);
					} else {
						messageHelper.addSuccess(messages, "work.form.saved.draft", workNumber);
						return String.format("redirect:/assignments/contact/%s", workNumber);
					}
				}

				@Override
				public String error() {
					model.addAttribute("bundle", messages);

					Long templateId = form.getWork_template_id();
					WorkForm newForm = buildFormFromWork(form, templateId);
					model.addAttribute("form", newForm);
					if (templateId != null) {
						model.addAttribute("template_id", templateId);
					}
					return addForm(model, newForm);
				}
			});

			if (form.isShownInFeed() && form.getWorkNumber() != null) {
				WorkAuthorizationResponse workAuthorizationResponse = workRoutingService.openWork(form.getWorkNumber());
				if (workAuthorizationResponse.fail()) {
					WorkResponse response = findWorkForForm(form.getWorkNumber());
					Work work = response.getWork();
					workAuthorizationFailureHelper.handleErrorsFromAuthResponse(workAuthorizationResponse, work, messages);
				}
			}
		}

		return redirect;
	}

	@SuppressWarnings("MVCPathVariableInspection")
	@RequestMapping(
		value = {"/edit/{workNumber}", "/{workNumber}/edit"},
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String edit(
		@PathVariable("workNumber") String workNumber,
		@ModelAttribute("templateForm") WorkTemplateForm templateForm,
		Model model) throws Exception {

		WorkResponse response = findWorkForForm(workNumber);
		Work work = response.getWork();
		Status status = work.getStatus();

		if (!PricingStrategyType.INTERNAL.equals(work.getPricing().getType())) {
			authorize(response, WorkStatusType.DRAFT, WorkStatusType.SENT, WorkStatusType.DECLINED);
		} else {
			authorize(response);
		}

		int negotiationSize = workNegotiationService.findAllNegotiationsByWorkId(work.getId()).size();
		boolean hideFirstToAccept = (!(WorkStatusType.DRAFT.equals(status.getCode())) && (negotiationSize > 0));
		boolean reserveFundsEnabledFlag = false;
		if (WorkStatusType.SENT.equals(status.getCode())) {
			reserveFundsEnabledFlag = companyService.findCompanyById(getCurrentUser().getCompanyId())
				.getManageMyWorkMarket().getReserveFundsEnabledFlag();
		}

		WorkForm form = toWorkFormConverter.convert(response.getWork());

		model.addAttribute("bundleParent", workBundleService.findByChild(work.getId()));
		model.addAttribute("form", form);
		model.addAttribute("pricingNotEditable", !work.isPricingEditable());
		model.addAttribute("reserveFundsEnabledFlag", reserveFundsEnabledFlag);
		model.addAttribute("hideFirstToAccept", hideFirstToAccept);

		ManageMyWorkMarket mmw = (ManageMyWorkMarket) model.asMap().get("mmw");
		mmw.setUseRequirementSets(work.getConfiguration().isUseRequirementSets());
		mmw.setEnableAssignmentPrintout(work.getConfiguration().isEnableAssignmentPrintout());
		mmw.setStandardTermsEndUserFlag(work.getConfiguration().isStandardTermsEndUserFlag());
		mmw.setEnablePrintoutSignature(work.getConfiguration().isEnablePrintoutSignature());
		mmw.setBadgeIncludedOnPrintout(work.getConfiguration().isBadgeIncludedOnPrintout());

		return editForm(form);
	}

	@RequestMapping(
		value = "/save_bundle/{workNumber}",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String doSaveBundle(
		final @PathVariable("workNumber") String workNumber,
		final @ModelAttribute("form") WorkBundleForm form,
		BindingResult bindingResult,
		final MessageBundle messages,
		final Model model,
		final RedirectAttributes redirectAttributes) throws Exception {

		boolean isSent = workBundleService.findById(form.getId()).isSent();

		StopWatch timer = new StopWatch("Send Bundle.");
		if(!isSent) {
			timer.start("1. authorizing bundle budget and spend limits.");
			WorkAuthorizationResponse responseType = workBundleService.verifyBundleFunds(getCurrentUser().getId(), form.getId());
			timer.stop();
			if (!WorkAuthorizationResponse.SUCCEEDED.equals(responseType)) {
				String msg = "assignment_bundle.send.insufficient_budget";
				switch (responseType) {
					case INSUFFICIENT_BUDGET:
						msg = "assignment_bundle.send.insufficient_budget";
						break;
					case INSUFFICIENT_SPEND_LIMIT:
						msg = "assignment_bundle.send.insufficient_spend_limit";
						break;
					case INVALID_SPEND_LIMIT:
						msg = "assignment_bundle.send.invalid_spend_limit";
						break;
					case INSUFFICIENT_FUNDS:
						msg = "assignment_bundle.send.insufficient_funds";
						break;
				}
				messageHelper.addError(messages, msg);
				redirectAttributes.addFlashAttribute("bundle", messages);
				logger.debug("Bundle did not pass validation. " + timer.prettyPrint());
				return "redirect:/assignments/view_bundle/" + form.getId();
			}
		}

		// validate that work is still in a ready to send state
		timer.start("2. Ensure all the assignments are ready to send.");
		Multimap<String, ValidateWorkResponse> validationResponses = workBundleValidationHelper.readyToSend(form.getId(), getCurrentUser().getId(), messages);
		timer.stop();
		Collection<ValidateWorkResponse> validationErrors = validationResponses.get(WorkBundleValidationHelper.VALIDATION_ERRORS);
		if (validationErrors.size() > 0) {
			redirectAttributes.addFlashAttribute("bundle", messages);
			logger.debug("Not all assignments are ready to send. " + timer.prettyPrint());
			return "redirect:/assignments/view_bundle/" + form.getId();
		}

		logger.debug("Done processing bundle assignments for send. " + timer.prettyPrint());
		return doEdit(workNumber, form, bindingResult, messages, model, redirectAttributes);
	}

	@RequestMapping(
		value = "/edit/{workNumber}",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String doEdit(
		final @PathVariable("workNumber") String workNumber,
		final @ModelAttribute("form") WorkForm form,
		BindingResult bindingResult,
		final MessageBundle messages,
		final Model model,
		final RedirectAttributes redirectAttributes) throws Exception {

		WorkResponse response = findWorkForForm(workNumber);
		Work work = response.getWork();

		if (!PricingStrategyType.INTERNAL.equals(work.getPricing().getType())) {
			authorize(response, WorkStatusType.DRAFT, WorkStatusType.SENT, WorkStatusType.DECLINED);
		} else {
			authorize(response);
		}

		form.setId(work.getId());
		form.setWorkNumber(work.getWorkNumber());

		//When there is more than one negotiation for work, "FirstToAccept" value should not be changeable and return an error message
		long negotiationSize = workNegotiationService.findAllNegotiationsByWorkId(work.getId()).size();
		if ((negotiationSize > 0) && (!form.getAssign_to_first_resource().equals(response.getWork().getConfiguration().isAssignToFirstResource()))) {
			messageHelper.addError(messages, "work.form.error");
			redirectAttributes.addFlashAttribute("bundle", messages);
			return String.format("redirect:/assignments/edit/%s", workNumber);
		}

		String redirect = doSave(form, bindingResult, WorkSaveType.WORK, messages, new WorkSaveCallback() {
			@Override
			public String success(WorkSaveRequest request, WorkResponse response) {
				redirectAttributes.addFlashAttribute("bundle", messages);

				boolean groupRouted = form.hasRoutingToGroups();
				boolean resourceRouted = form.hasRoutingToUsers();
				boolean vendorRouted = form.hasRoutingToVendorCompanies();

					if (groupRouted || resourceRouted || vendorRouted) {
					if (groupRouted) messageHelper.addSuccess(messages, "work.form.saved.group_routed");
					if (resourceRouted) {
						messageHelper.addSuccess(messages, response.isWorkBundle() ? "assignment_bundle.send.success" : "work.form.saved.resource_routed");
					}
					if (vendorRouted) {
						messageHelper.addSuccess(messages, "work.form.saved.vendor_routed");
					}
					return String.format("redirect:/assignments/details/%s", workNumber);
				} else if (request.isSmartRoute()) {
					messageHelper.addSuccess(messages,"work.form.saved.work_send");
					return String.format("redirect:/assignments/details/%s", workNumber);
				} else {
					messageHelper.addSuccess(messages, "work.form.saved.draft", workNumber);
					return String.format("redirect:/assignments/contact/%s", workNumber);
				}
			}

			@Override
			public String error() {
				model.addAttribute("bundle", messages);
				WorkForm newForm = null;
				try {
					newForm = buildFormFromWork(form);
					model.addAttribute("form", newForm);

				} catch (WorkActionException e) {
					logger.error(String.format("Error loading edit form for work id=%d: ", form.getId()), e);
				}
				return editForm(newForm);
			}
		});

		if (form.isShownInFeed() && !messages.hasErrors()) {
			WorkAuthorizationResponse workAuthorizationResponse = workRoutingService.openWork(form.getWorkNumber());
			if (workAuthorizationResponse.fail()) {
				workAuthorizationFailureHelper.handleErrorsFromAuthResponse(workAuthorizationResponse, work, messages);
			}
		}

		return redirect;
	}

	@RequestMapping(
		value = "/copy/{workNumber}",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String copy(
		@PathVariable("workNumber") String workNumber,
		@ModelAttribute("templateForm") WorkTemplateForm templateForm,
		Model model) throws Exception {

		model.addAttribute("hasFeatureProjectPermission", hasFeature("projectPermission"));
		if (hasFeature("projectPermission")) {
			model.addAttribute("hasProjectAccess", authenticationService.hasProjectAccess(getCurrentUser().getId()));
		}

		WorkResponse response = findWorkForForm(workNumber);
		authorize(response);

		Work work = response.getWork();
		work.setId(0L);
		work.setWorkNumber(null);

		cleanDeliverablesForCopy(work);
		cleanPartsForCopy(work);

		WorkForm form = toWorkFormConverter.convert(work);

		model.addAttribute("form", form);

		return copyForm(model, form);
	}

	/**
	 * redirect if you try to access template url directly
	 *
	 * @return String
	 */
	@RequestMapping(
		value = "/load_template",
		method = GET)
	public String redirectEmptyTemplateLoad(Model model) {

		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/assignments/load_template"));

		return "redirect:/assignments/add";
	}


	@RequestMapping(
		value = "/load_template",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String loadTemplate(
		@RequestParam("template_id") Long templateId,
		@RequestParam(value = "forResource", required = false) String forUserNumber,
		Model model) throws Exception {

		if (templateId == null) {
			WorkForm form = new WorkForm();
			model.addAttribute("form", form);
			return addForm(model, form);
		}

		MessageBundle messages = messageHelper.newBundle(model);

		WorkResponse response = findWorkForForm(templateId);
		authorize(response);

		Work work = response.getWork();
		work.setId(0L);
		work.setWorkNumber(null);

		cleanDeliverablesForCopy(work);
		cleanPartsForCopy(work);

		WorkForm form = toWorkFormConverter.convert(response.getWork());

		/* The workTemplateId was not properly set above because toWorkFormConverter.convert
		 * used the original template, and the original template did not have this field set
		 * The only exception is when the current template is based on a previous template.
		 * In that case the templateId will not be the current template, but the old one
		 * */
		form.setWork_template_id(templateId);
		if (forUserNumber != null) {
			try {
				if (StringUtils.isNumeric(forUserNumber)) {

					User forUser = userService.findUserById(StringUtilities.parseLong(forUserNumber));
					form.setAssignToUserId(forUser.getId());
					model.addAttribute("assignment_for", forUser.getUserNumber());
					messageHelper.addNotice(messages, "work.form.for_user", forUser.getFullName());
				}
			} catch (Exception e) {
				logger.error("Error retrieving User Number", e);
			}
		}

		model.addAttribute("form", form);
		model.addAttribute("template_id", templateId); // TODO necessary?
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/assignments/load_template"));

		return addForm(model, form);
	}

	@RequestMapping(
		value = "/template_create",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String templateCreate(
		@ModelAttribute("form") WorkForm form,
		@ModelAttribute("templateForm") WorkTemplateForm templateForm,
		Model model) throws WorkActionException {

		ManageMyWorkMarket mmw = (ManageMyWorkMarket) model.asMap().get("mmw");

		form.setInternal_owner(getCurrentUser().getId());
		form.setSupport_contact(getCurrentUser().getId());
		form.setIndustry(getDefaultIndustry());
		form.setPayment_terms_days(mmw.getPaymentTermsDays());

		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/assignments/template_create"));

		model.addAttribute("pageTitle", "Create Template");
		model.addAttribute("bodyClass", "accountSettings");

		return templateForm(model, form);
	}

	@RequestMapping(
		value = "/template_create",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String doTemplateCreate(
		final @ModelAttribute("form") WorkForm form,
		BindingResult bindingResult,
		final MessageBundle messages,
		final Model model,
		final RedirectAttributes redirectAttributes) {

		form.setId(0L);

		return doSave(form, bindingResult, WorkSaveType.TEMPLATE, messages, new WorkSaveCallback() {
			@Override
			public String success(WorkSaveRequest request, WorkResponse response) {
				messageHelper.addSuccess(messages, "work.form.saved.template", response.getWork().getWorkNumber());
				redirectAttributes.addFlashAttribute("bundle", messages);
				return "redirect:/settings/manage/templates";
			}

			@Override
			public String error() {
				model.addAttribute("bundle", messages);
				return templateForm(model, form);
			}
		});
	}

	@RequestMapping(
		value = "/template_edit/{workNumber}",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String templateEdit(
		@PathVariable("workNumber") String workNumber,
		@ModelAttribute("templateForm") WorkTemplateForm templateForm,
		Model model) throws Exception {

		WorkResponse response = findWorkForForm(workNumber);
		authorize(response);

		WorkForm form = toWorkFormConverter.convert(response.getWork());

		model.addAttribute("form", form);
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/assignments/template_edit"));

		return templateForm(model, form);
	}

	@RequestMapping(
		value = "/template_edit/{workNumber}",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String doTemplateEdit(
		final @PathVariable("workNumber") String workNumber,
		final @ModelAttribute("form") WorkForm form,
		BindingResult bindingResult,
		final MessageBundle messages,
		final Model model,
		final RedirectAttributes redirectAttributes) throws Exception {

		WorkResponse response = findWorkForFormAuthorization(workNumber);
		authorize(response);

		Work work = response.getWork();

		if (work != null) {
			form.setId(work.getId());
			form.setWorkNumber(work.getWorkNumber());
		}

		return doSave(form, bindingResult, WorkSaveType.TEMPLATE, messages, new WorkSaveCallback() {
			@Override
			public String success(WorkSaveRequest request, WorkResponse response) {
				messageHelper.addSuccess(messages, "work.form.saved.template", response.getWork().getWorkNumber());
				redirectAttributes.addFlashAttribute("bundle", messages);
				return "redirect:/settings/manage/templates";
			}

			@Override
			public String error() {
				model.addAttribute("bundle", messages);
				return templateForm(model, form);
			}
		});
	}

	@RequestMapping(
		value = "/template_copy/{workNumber}",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String doTemplateCopy(
		@PathVariable("workNumber") String workNumber,
		@ModelAttribute("templateForm") WorkTemplateForm templateForm,
		Model model) throws Exception {

		WorkResponse response = findWorkForForm(workNumber);
		authorize(response);

		Work work = response.getWork();
		work.setId(0L);
		work.setWorkNumber(null);
		work.getTemplate().setName("");
		work.getTemplate().setDescription("");

		cleanDeliverablesForCopy(work);
		cleanPartsForCopy(work);

		WorkForm form = toWorkFormConverter.convert(response.getWork());

		model.addAttribute("form", form);
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/assignments/template_copy"));

		return templateForm(model, form);
	}

	@RequestMapping(
		value = "/activate_now",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public void doActivateNow(
		final @ModelAttribute("form") WorkForm form,
		BindingResult bindingResult,
		final MessageBundle messages,
		final Model model) throws Exception {

		String workNumber = doSave(form, bindingResult, WorkSaveType.WORK, messages, new WorkSaveCallback() {
			@Override
			public String success(WorkSaveRequest request, WorkResponse response) {
				String workNumber = response.getWork().getWorkNumber();
				webHookEventService.onWorkCreated(workNumber, form.getAutotaskId());
				messageHelper.addSuccess(messages, "work.form.saved", workNumber);

				model.addAttribute("response", new AjaxResponseBuilder()
					.setRedirect(String.format("/assignments/details/%s", workNumber))
					.setSuccessful(true)
					.setMessages(messages.getAllMessages())
					.addData("assignment_id", workNumber));
				return workNumber;
			}

			@Override
			public String error() {
				model.addAttribute("response", new AjaxResponseBuilder()
					.setSuccessful(false)
					.setMessages(messages.getAllMessages()));
				return null;
			}
		});

		if (workNumber != null) {
			workRoutingService.openWork(workNumber);
		}
	}

	@RequestMapping(
		value = "/activate_now/{workNumber}",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public String activateNow(
		@PathVariable("workNumber") String workNumber,
		MessageBundle messages,
		RedirectAttributes redirectAttributes) throws WorkNotFoundException, WorkActionException {

		WorkRequest workRequest = new WorkRequest()
			.setUserId(getCurrentUser().getId())
			.setWorkNumber(workNumber)
			.setIncludes(Sets.newHashSet(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.INDUSTRY_INFO,
				WorkRequestInfo.PROJECT_INFO,
				WorkRequestInfo.BUYER_INFO,
				WorkRequestInfo.LOCATION_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO
			));

		WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);
		WorkSaveRequest workSaveRequest = new WorkSaveRequest()
			.setUserId(getCurrentUser().getId())
			.setWork(workResponse.getWork());

		List<ConstraintViolation> violations = workSaveRequestValidator.getConstraintViolations(workSaveRequest);

		if (!violations.isEmpty()) {
			messageHelper.addError(messages, "work.form.incomplete");
			redirectAttributes.addFlashAttribute("bundle", messages);
			return "redirect:/assignments/{workNumber}/edit";
		}

		workRoutingService.openWork(workNumber);
		return "redirect:/assignments/details/{workNumber}";
	}

	@RequestMapping(
		value = "/save_bundle_ajax",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public void saveBundleAjax(
		final @ModelAttribute("form") WorkBundleForm form,
		BindingResult bindingResult,
		final MessageBundle messages,
		final Model model) {

		workBundleService.processWorkBundleForm(form);
		saveDraft(form, bindingResult, messages, model);
	}

	@RequestMapping(
		value = "/save_draft",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public void saveDraft(
		final @ModelAttribute("form") WorkForm form,
		BindingResult bindingResult,
		final MessageBundle messages,
		final Model model) {

		//Bundled assignments don't have routing or feed set, so initialize to prevent NPEs
		if (form.getRouting() == null) {
			form.setShow_in_feed(false);
			form.setRouting(new WorkFormRouting());
		}

		if (form.hasRoutingToGroups()) {
			form.setGroupIds(Lists.newArrayList(form.getRouting().getGroupIds()));
			form.setAssign_to_first_resource(form.getRouting().isAssignToFirstToAcceptForGroups());
		}

		Long workId = form.getId();
		WorkResponse response = null;
		WorkSaveType type = WorkSaveType.DRAFT;
		if (workId != null) {
			try {
				response = findWorkForForm(workId);
				if(WorkStatusType.ACTIVE.equals(response.getWork().getStatus().getCode())) {
					String errorMessage = messageHelper.getMessage("work.form.invalid_status");
					model.addAttribute("response", new AjaxResponseBuilder()
						.setSuccessful(false)
						.setMessages(Collections.singletonList(errorMessage)));
					return;
				}
				if (!response.getWork().getStatus().getCode().equals(WorkStatusType.DRAFT)) {
					type = WorkSaveType.WORK;
				}
			} catch (Exception e) {
				logger.error("Error retrieving work", e);
			}
			if (response != null) {
				try {
					authorize(response);
				} catch (HttpException401|HttpException405 e) {
					String errorMessage = messageHelper.getMessage(e.getMessageKey());
					model.addAttribute("response", new AjaxResponseBuilder()
						.setSuccessful(false)
						.setMessages(Lists.newArrayList(errorMessage)));
					return;
				}
			}
		}

		if (form.isMultipleCopies()) {
			String redirect = doMultipleSaves(form, bindingResult, type, form.getNumberOfCopies(), messages);
			if (messages.hasErrors()) {
				model.addAttribute("response", new AjaxResponseBuilder()
					.setSuccessful(false)
					.setMessages(messages.getAllMessages()));
				return;
			}
			model.addAttribute("response", new AjaxResponseBuilder()
				.setRedirect(redirect)
				.setSuccessful(true)
				.setMessages(messages.getAllMessages()));
		} else {
			doSave(form, bindingResult, type, messages, new WorkSaveCallback() {
				@Override
				public String success(WorkSaveRequest request, WorkResponse response) {
					String workNumber = response.getWork().getWorkNumber();
					webHookEventService.onWorkCreated(workNumber, form.getAutotaskId());

					messageHelper.addSuccess(messages, "work.form.saved", workNumber);

					model.addAttribute("response", new AjaxResponseBuilder()
						.setRedirect(String.format("/assignments/details/%s", workNumber))
						.setSuccessful(true)
						.setMessages(messages.getAllMessages())
						.addData("assignment_id", workNumber));
					return null;
				}

				@Override
				public String error() {
					model.addAttribute("response", new AjaxResponseBuilder()
						.setSuccessful(false)
						.setMessages(messages.getAllMessages()));
					return null;
				}
			});
		}
	}

	@RequestMapping(
		value = "/detect_shipping_provider/{trackingNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public @ResponseBody
	AjaxResponseBuilder detectShippingProvider(@PathVariable("trackingNumber") final String trackingNumber) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (StringUtils.isBlank(trackingNumber)) {
			response.addMessage(messageHelper.getMessage("partsAndLogistics.trackingNumber.required"));
			return response;
		}

		ShippingProviderDetectResponse shippingProviderDetectResponse = trackingNumberAdapter.detectShippingProvider(trackingNumber);

		if (!shippingProviderDetectResponse.isSuccessful()) {
			return response;
		}

		return response
			.setSuccessful(true)
			.setData(ImmutableMap.of(
				"successful", shippingProviderDetectResponse.isSuccessful(),
				"responseCode", shippingProviderDetectResponse.getResponseCode().value(),
				"shippingProviders", shippingProviderDetectResponse.getShippingProviders()
			));
	}

	@RequestMapping(
		value = "/save_template",
		method = POST)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public void saveTemplate(
		final @ModelAttribute("templateForm") WorkTemplateForm templateForm,
		BindingResult bindingResult,
		final MessageBundle messages,
		final Model model) {

		// Unserialize the work fields and bind to the WorkForm backing bean

		List<NameValuePair> params = URLEncodedUtils.parse(templateForm.getTemplate(), null);

		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		for (NameValuePair p : params) {
			httpRequest.setParameter(p.getName(), p.getValue());
		}

		WorkForm form = new WorkForm();
		WebRequest webRequest = new ServletWebRequest(httpRequest);
		WebRequestDataBinder dataBinder = new WebRequestDataBinder(form, "form");
		dataBinder.registerCustomEditor(Date.class, new DateOrTimeEditor());
		dataBinder.bind(webRequest);

		List<Long> groupIds = Lists.newArrayList();
		for (NameValuePair pair: params) {
			if (pair.getName().equals(GROUP_ID_PARAM) && (StringUtils.isNumeric(pair.getValue()))) {
				groupIds.add(Long.parseLong(pair.getValue()));
			}
		}
		form.setGroupIds(groupIds);
		doSave(form, bindingResult, WorkSaveType.TEMPLATE, messages, new WorkSaveCallback() {
			@Override
			public void before(WorkSaveRequest request) {
				Work work = request.getWork();
				work.setId(0L);
				cleanDeliverablesForCopy(work);
				cleanPartsForCopy(work);

				work.setTemplate(
					new Template()
						.setName(templateForm.getNew_template_name())
						.setDescription(templateForm.getNew_template_description())
				);
			}

			@Override
			public String success(WorkSaveRequest request, WorkResponse response) {
				model.addAttribute("response", new AjaxResponseBuilder()
					.setSuccessful(true)
					.addData("template_name", response.getWork().getTemplate().getName())
					.addData("template_id", response.getWork().getId()));
				return null;
			}

			@Override
			public String error() {
				model.addAttribute("response", new AjaxResponseBuilder()
					.setSuccessful(false)
					.setMessages(messages.getErrors()));
				return null;
			}
		});
	}

	@RequestMapping(
		value = "/save/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder updateInline(
		@PathVariable String workNumber,
		@RequestParam String newHtml,
		@RequestParam String type) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		Work work = getWork(workNumber).getWork();
		if (work == null) {
			return response;
		}

		response.addData("uniqueIdDisplayName", work.getUniqueExternalIdDisplayName());
		WorkStatusType statusType = workService.findWorkByWorkNumber(workNumber).getWorkStatusType();
		if (WorkProperties.EXTERNAL_ID.getName().equals(type)){
			WorkSaveRequest workSaveRequest = new WorkSaveRequest()
				.setUserId(getCurrentUser().getId())
				.setWork(work);

			work.setUniqueExternalIdValue(newHtml);

			List<ConstraintViolation> errors = workValidationService.validateWorkUniqueId(workSaveRequest, statusType);
			if (!errors.isEmpty()){
				for(ConstraintViolation error : errors)
					messageHelper.addMessage(response, error.getError(), error.getProperty());
				return response;
			}
		}

		if (WorkProperties.DESCRIPTION.getName().equals(type) || WorkProperties.INSTRUCTIONS.getName().equals(type) || WorkProperties.EXTERNAL_ID.getName().equals(type)) {
			try {
				workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap(
					WorkProperties.DESCRIPTION.getName().equals(type) ? type : (WorkProperties.INSTRUCTIONS.getName().equals(type) ? type : WorkProperties.EXTERNAL_ID.getName()), StringUtilities.stripXSSAndEscapeHtml(newHtml)
				));
			} catch (Exception e) {
				logger.error("Error occurred while trying edit work inline", e);
				return response;
			}
		} else {
			if (WorkProperties.EXTERNAL_ID.getName().equals(type))
				messageHelper.addMessage(response, "work.form.inline_edit_fail", StringUtilities.capitalizeFirstLetter(work.getUniqueExternalIdDisplayName()));
			else
				messageHelper.addMessage(response, "work.form.inline_edit_fail", StringUtilities.capitalizeFirstLetter(type));
			return response;
		}

		if (WorkProperties.EXTERNAL_ID.getName().equals(type))
			messageHelper.addMessage(response, "work.form.inline_edit_success", StringUtilities.capitalizeFirstLetter(work.getUniqueExternalIdDisplayName()));
		else
			messageHelper.addMessage(response, "work.form.inline_edit_success", StringUtilities.capitalizeFirstLetter(type));

		if (WorkProperties.DESCRIPTION.getName().equals(type)){
			WorkPropertyChangeLog propertyChangeLog = new WorkPropertyChangeLog(work.getId(), getCurrentUser().getId(), getCurrentUser().getMasqueradeUserId(), null);
			propertyChangeLog.setPropertyName(type);
			propertyChangeLog.setOldValue(work.getDescription());
			propertyChangeLog.setNewValue(newHtml);
			workChangeLogService.saveWorkChangeLog(propertyChangeLog);
		}
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/{workNumber}/eligible_worker_count",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Integer getEligibleGroupWorkerCount(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "groupIds", required = false) Long[] groupIds) throws WorkNotFoundException, SearchException {
		GroupRoutingStrategy routingStrategy = new GroupRoutingStrategy();
		Set<Long> groupSet = new HashSet<>(Arrays.asList(groupIds));
		com.workmarket.domains.work.model.Work work = workService.findWorkByWorkNumber(workNumber);

		routingStrategy.setWork(work);
		routingStrategy.setCreatorId(getCurrentUser().getId());
		routingStrategy.setUserGroups(groupSet);

		return workRoutingVisitor.getWorkersWithinTravelDistanceForStrategy(routingStrategy, work.getId()).size();
	}

	private String addForm(Model model, WorkForm form) {
		setupAdd(model);

		if (form.getRequiredCustomField() == null) {
			form.setRequiredCustomField(getRequiredCustomFieldGroupId());
		}

		form.setPaymentTermsDurations(companyService.findPaymentTermsDurations(getCurrentUser().getCompanyId()));

		repopulateDocuments(form);
		repopulateDeliverableRequirements(form);
		repopulateParts(form);
		repopulateSurveys(form);
		repopulateUniqueExternalId(form);

		return "web/pages/assignments/form";
	}

	private WorkForm buildFormFromWork(WorkForm prevForm, Long templateId) {
		WorkResponse response;
		try {
			response = findWorkForForm(templateId != null ? templateId : prevForm.getId());
			response.setWork(toWorkConverter.convert(prevForm, response.getWork()));
		} catch (WorkActionException e) {
			// the work was not found, sanitize the form values and return a new form
			Work tempWork = toWorkConverter.convert(prevForm);
			return toWorkFormConverter.convert(tempWork);
		}

		authorize(response);

		if (templateId != null) {
			response.getWork().setId(0L);
			response.getWork().setWorkNumber(null);
		}
		WorkForm tempForm = toWorkFormConverter.convert(response.getWork());
		if (templateId != null) {
			tempForm.setWork_template_id(templateId);
		}

		copyFormValues(tempForm, prevForm);
		return tempForm;
	}

	private WorkForm buildFormFromWork(WorkForm prevForm) throws WorkActionException {
		return buildFormFromWork(prevForm, null);
	}

	private WorkForm copyFormValues(WorkForm dest, WorkForm src) {

		if (src.getTitle() != null) {
			dest.setTitle(src.getTitle());
		}
		if (src.getDescription() != null) {
			dest.setDescription(src.getDescription());
		}
		if (src.getInstructions() != null) {
			dest.setInstructions(src.getInstructions());
		}
		if (src.getPrivateInstructions() != null) {
			dest.setPrivateInstructions(src.getPrivateInstructions());
		}
		if (src.getLocation_name() != null) {
			dest.setLocation_name(src.getLocation_name());
		}
		if (src.getLocation_address1() != null) {
			dest.setLocation_address1(src.getLocation_address1());
		}
		if (src.getLocation_address2() != null) {
			dest.setLocation_address2(src.getLocation_address2());
		}
		if (src.getLocation_city() != null) {
			dest.setLocation_city(src.getLocation_city());
		}
		if (src.getLocation_state() != null) {
			dest.setLocation_state(src.getLocation_state());
		}
		if (src.getLocation_postal_code() != null) {
			dest.setLocation_postal_code(src.getLocation_postal_code());
		}
		if (src.getLocation_longitude() != null) {
			dest.setLocation_longitude(src.getLocation_longitude());
		}
		if (src.getLocation_latitude() != null) {
			dest.setLocation_latitude(src.getLocation_latitude());
		}
		if (src.getAutotaskId() != null) {
			dest.setAutotaskId(src.getAutotaskId());
		}
		if (src.getCustomfield() != null) {
			dest.setCustomfield(src.getCustomfield());
		}
		if (src.getCustomfields() != null) {
			dest.setCustomfields(src.getCustomfields());
		}
		if (src.getCustomFieldsJson() != null) {
			dest.setCustomFieldsJson(src.getCustomFieldsJson());
		}
		if (src.getPricing() != null) {
			dest.setPricing(src.getPricing());
		}
		if (src.getFlat_price() != null) {
			dest.setFlat_price(src.getFlat_price());
		}
		if (src.getPer_hour_price() != null) {
			dest.setPer_hour_price(src.getPer_hour_price());
		}
		if (src.getMax_number_of_hours() != null) {
			dest.setMax_number_of_hours(src.getMax_number_of_hours());
		}
		if (src.getPer_unit_price() != null) {
			dest.setPer_unit_price(src.getPer_unit_price());
		}
		if (src.getMax_number_of_units() != null) {
			dest.setMax_number_of_units(src.getMax_number_of_units());
		}
		if (src.getInitial_number_of_hours() != null) {
			dest.setInitial_number_of_hours(src.getInitial_number_of_hours());
		}
		if (src.getInitial_per_hour_price() != null) {
			dest.setInitial_per_hour_price(src.getInitial_per_hour_price());
		}
		if (src.getAdditional_per_hour_price() != null) {
			dest.setAdditional_per_hour_price(src.getAdditional_per_hour_price());
		}
		if (src.getMax_blended_number_of_hours() != null) {
			dest.setMax_blended_number_of_hours(src.getMax_blended_number_of_hours());
		}
		if (src.getFrom() != null) {
			dest.setFrom(src.getFrom());
		}
		if (src.getFromtime() != null) {
			dest.setFromtime(src.getFromtime());
		}
		if (src.getVariable_from() != null) {
			dest.setVariable_from(src.getVariable_from());
		}
		if (src.getVariable_fromtime() != null) {
			dest.setVariable_fromtime(src.getVariable_fromtime());
		}
		if (src.getTo() != null) {
			dest.setTo(src.getTo());
		}
		if (src.getTotime() != null) {
			dest.setTotime(src.getTotime());
		}
		if (src.getScheduling()) {
			dest.setScheduling(src.getScheduling());
		}
		return dest;
	}

	private String copyForm(Model model, WorkForm form) {

		model.addAttribute("is_copy", true);
		model.addAttribute("numberOfCopies", assignmentCopyQuantities);
		return addForm(model, form);
	}

	private String editForm(WorkForm form) {
		if (form.getRequiredCustomField() == null) {
			form.setRequiredCustomField(getRequiredCustomFieldGroupId());
		}

		form.setPaymentTermsDurations(companyService.findPaymentTermsDurations(getCurrentUser().getCompanyId()));

		repopulateDocuments(form);
		repopulateDeliverableRequirements(form);
		repopulateParts(form);
		repopulateUniqueExternalId(form);

		return "web/pages/assignments/form";
	}

	private void repopulateDeliverableRequirements(WorkForm form) {
		if (form.getResourceCompletionForm() != null) {
			form.setResourceCompletionJson(jsonService.toJson(form.getResourceCompletionForm()));
		}
	}

	private void repopulateDocuments(WorkForm form) {
		List<WorkAssetForm> attachments = form.getAttachments();

		if (CollectionUtils.isEmpty(attachments)) {
			attachments = Lists.newArrayList();
		}

		form.setAttachmentsJson(jsonService.toJson(attachments));
	}

	private void repopulateParts(WorkForm form) {
		PartGroupForm partGroup = form.getPartGroup();
		if (partGroup != null && isNotEmpty(partGroup.getParts())) {
			form.setPartsJson(jsonService.toJsonIdentity(partGroup.getParts()));
		}
	}

	private void repopulateSurveys(WorkForm form) {
		form.setAssessmentsJson(CollectionUtils.isNotEmpty(form.getAssessments()) ? jsonService.toJson(form.getAssessments()) : "''");
	}

	private void repopulateUniqueExternalId(WorkForm form) {
		CompanyPreference companyPreference =
			companyService.findCompanyById(getCurrentUser().getCompanyId()).getCompanyPreference();
		if (companyPreference.isExternalIdActive()) {
			form.setUniqueExternalIdDisplayName(companyPreference.getExternalIdDisplayName());
			form.setRequiresUniqueExternalId(true);
		} else {
			form.setRequiresUniqueExternalId(false);
		}
	}

	private String templateForm(Model model, WorkForm form) {
		setupTemplate(model);

		repopulateDocuments(form);
		repopulateDeliverableRequirements(form);
		repopulateParts(form);

		if (form.getRequiredCustomField() == null) {
			form.setRequiredCustomField(getRequiredCustomFieldGroupId());
		}

		form.setPaymentTermsDurations(companyService.findPaymentTermsDurations(getCurrentUser().getCompanyId()));

		return "web/pages/assignments/form";
	}

	private String doMultipleSaves(
		final WorkForm form,
		BindingResult bindingResult,
		final WorkSaveType type,
		int selectedNumberOfCopies,
		MessageBundle messageBundle) {

		int min = 1;
		int max = assignmentCopyQuantities.get(assignmentCopyQuantities.size()-1);
		if (selectedNumberOfCopies < min || selectedNumberOfCopies > max) {
			logger.error("[Copy Assignment] Number of copies selected is invalid: " + selectedNumberOfCopies);
			messageHelper.addError(messageBundle, "work.form.error");
			return null;
		}

		if (type == WorkSaveType.WORK) {
			User userBuyer = userService.findUserByUserNumber(getCurrentUser().getUserNumber());
			bulkWorkRouteValidator.validateMultipleWorkCopy(form, selectedNumberOfCopies, max, userBuyer, messageBundle);
			if (messageBundle.hasErrors()) {
				return null;
			}
		}

		WorkSaveCallback workSaveCallback = new WorkSaveCallback() {
			// TODO: Tech Debt
			// Instead of having this success callback clutter up the controller, we should move this a non-transactional service
			@Override
			public String success(WorkSaveRequest request, WorkResponse response) {
				String workNumber = response.getWork().getWorkNumber();

				form.setWorkNumber(workNumber);
				webHookEventService.onWorkCreated(workNumber, form.getAutotaskId());
				return "/assignments/";
			}

			@Override
			public String error() {
				return null;
			}
		};
		String redirect = "/assignments/";
		try {
			for (int ix = 0; ix < selectedNumberOfCopies; ix++) {
				redirect = doSave(form, bindingResult, type, messageBundle, workSaveCallback);
				if (redirect == null) {
					return null;
				}
				if (type == WorkSaveType.WORK && form.isShownInFeed() && form.getWorkNumber() != null) {
					workRoutingService.openWork(form.getWorkNumber());
				}
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error occurred while trying to openWork", e);
			return null;
		}

		boolean groupRouted = form.hasRoutingToGroups();
		boolean resourceRouted = form.hasRoutingToUsers();
		boolean postedOnWorkFeed = form.isShownInFeed();

		if (type == WorkSaveType.DRAFT) {
			messageHelper.addSuccess(messageBundle, "work.form.multiple.saved");
		} else if (type == WorkSaveType.WORK) {
			if (groupRouted) {
				messageHelper.addSuccess(messageBundle, "work.form.multiple.saved.group_routed");
			}
			if (resourceRouted) {
				messageHelper.addSuccess(messageBundle, "work.form.multiple.saved.resource_routed");
			}
			if (postedOnWorkFeed) {
				messageHelper.addSuccess(messageBundle, "work.form.multiple.saved.show_in_feed");
			}
			redirect = "redirect:" + redirect;
		}
		return redirect;
	}

	private String doSave(
		WorkForm form,
		BindingResult bindingResult,
		WorkSaveType type,
		MessageBundle messages,
		WorkSaveCallback callback) {

		form.collapseNullLists();

		workBundleService.processWorkBundleForm(form);

		Work work = toWorkConverter.convert(form);
		WorkSaveRequest saveRequest = new WorkSaveRequest()
			.setUserId(getCurrentUser().getId())
			.setUseMboServices(form.getUseMboServices())
			.setWork(work);
		if (form.getGroupIds() != null) {
			saveRequest.setGroupIds(form.getGroupIds());
		}

		if (callback != null) {
			callback.before(saveRequest);
		}

		// TODO Ideally these are caught in the save request validation below...
		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			if (callback != null) {
				return callback.error();
			}
			return null;
		}

		try {
			WorkResponse saveResponse;
			switch (type) {
				case DRAFT:
					saveRequest.getWork().setUniqueExternalIdValue(form.getUniqueExternalId());
					saveResponse = tWorkFacadeService.saveOrUpdateWorkDraft(saveRequest);
					break;
				case TEMPLATE:
					saveResponse = tWorkFacadeService.saveOrUpdateWorkTemplate(saveRequest);
					break;
				case WORK:
				default:
					// add selected resources (if any) to the assignment
					if (form.getAssignToUserId() != null) {
						saveRequest.setAssignTo(new com.workmarket.thrift.core.User().setId(form.getAssignToUserId()));
					}

					WorkFormRouting resourceRouting = form.getRouting();
					if (resourceRouting != null) {

						WorkFormRoutingAdapter workFormRoutingAdapter = new WorkFormRoutingAdapter(form.getRouting());

						List<RoutingStrategy> userRoutingStrategies = workFormRoutingAdapter.asRoutingStrategiesForUsers();
						saveRequest.addToRoutingStrategies(userRoutingStrategies);

						List<RoutingStrategy> vendorRoutingStrategies = workFormRoutingAdapter.asRoutingStrategiesForVendors();
						saveRequest.addToRoutingStrategies(vendorRoutingStrategies);

						List<RoutingStrategy> groupRoutingStrategies = workFormRoutingAdapter.asRoutingStrategiesForGroups(getCurrentUser().getId());
						saveRequest.addToRoutingStrategies(groupRoutingStrategies);
					}

					saveRequest.getWork().setUniqueExternalIdValue(form.getUniqueExternalId());
					saveResponse = tWorkFacadeService.saveOrUpdateWork(saveRequest);

			}

			if (saveResponse != null && isNotEmpty(saveResponse.getWorkAuthorizationResponses()) && saveResponse.getWorkAuthorizationResponses().contains(WorkAuthorizationResponse.SUCCEEDED)) {
				if (saveResponse.isInWorkBundle()) {
					workBundleService.updateBundleCalculatedValues(saveResponse.getWorkBundleParent().getId());
				}

				if (callback != null) {
					return callback.success(saveRequest, saveResponse);
				}
			} else {
				workAuthorizationFailureHelper.handleErrorsFromAuthResponse(saveRequest, saveResponse, messages);
				if (callback != null) {
					return callback.error();
				}
			}
		} catch (ValidationException e) {
			logger.error("Error saving work: ", e);
			messageHelper.setErrors(messages, ThriftValidationMessageHelper.buildBindingResult(e));
			if (callback != null) {
				return callback.error();
			}
		} catch (Exception e) {
			logger.error("Error saving work: ", e);
			messageHelper.addError(messages, "work.form.error");
			if (callback != null) {
				return callback.error();
			}
		}
		return null;
	}

	private void cleanDeliverablesForCopy(Work work) {
		Assert.notNull(work);

		DeliverableRequirementGroupDTO deliverableRequirementGroupDTO = work.getDeliverableRequirementGroupDTO();
		if (deliverableRequirementGroupDTO != null) {
			deliverableRequirementGroupDTO.setId(null);

			List<DeliverableRequirementDTO> deliverableRequirementDTOs = deliverableRequirementGroupDTO.getDeliverableRequirementDTOs();
			if (CollectionUtils.isNotEmpty(deliverableRequirementDTOs)) {
				for (DeliverableRequirementDTO deliverableRequirementDTO : deliverableRequirementDTOs) {
					deliverableRequirementDTO.setId(null);
				}
			}
		}
	}

	private void cleanPartsForCopy(Work work) {
		Assert.notNull(work);

		PartGroupDTO partGroup = work.getPartGroup();

		if (partGroup == null) {
			return;
		}

		List<PartDTO> parts = partGroup.getParts();
		if (CollectionUtils.isEmpty(parts) && partGroup.getUuid() != null) {
			parts = partService.getPartsByGroupUuid(partGroup.getUuid());
		}

		partGroup.setId(null);
		partGroup.setUuid(null);

		if (partGroup.hasShipToLocation()) {
			partGroup.getShipToLocation().setId(null);
		}

		if (partGroup.hasReturnToLocation()) {
			partGroup.getReturnToLocation().setId(null);
		}

		if (CollectionUtils.isNotEmpty(parts)) {
			for (PartDTO partDTO : parts) {
				partDTO.setId(null);
				partDTO.setUuid(null);
			}
			partGroup.setParts(parts);
		}
	}

	private void setupAdd(Model model) {
		model.addAttribute("allow_draft_save", true);
	}

	private void setupTemplate(Model model) {
		model.addAttribute("isTemplate", true);
	}

	@ModelAttribute("is_admin")
	public Boolean isAdmin() {
		return getCurrentUser().hasAnyRoles("ACL_ADMIN", "ACL_MANAGER");
	}

	@SuppressWarnings("unchecked") @ModelAttribute("followers")
	public Map<Long, String> getFollowers() {
		return formOptionsDataHelper.getFollowers(getCurrentUser().getCompanyId(), Collections.EMPTY_LIST);
	}

	@ModelAttribute("workFee")
	public BigDecimal getWorkFee() {
		AccountRegister register = pricingService.findDefaultRegisterForCompany(getCurrentUser().getCompanyId());
		return register.getCurrentWorkFeePercentage();
	}

	@ModelAttribute("states")
	public Map<String, Map<String, String>> getStates() {
		return formOptionsDataHelper.getStatesAsOptgroup();
	}

	@ModelAttribute("countries")
	public Map<String, String> getCountries() {
		return formOptionsDataHelper.getCountries();
	}

	@ModelAttribute("location_types")
	public Map<Long, String> getLocationTypes() {
		return formOptionsDataHelper.getLocationTypes();
	}

	@ModelAttribute("dress_codes")
	public Map<Long, String> getDressCodes() {
		return formOptionsDataHelper.getDressCodes();
	}

	@ModelAttribute("shipping_destinations")
	public Map<ShippingDestinationType, String> getShippingDestinations() {
		return SHIPPING_DESTINATIONS;
	}

	@ModelAttribute("partsConstantsJson")
	public String getPartsConstantsJson() {
		return jsonService.toJsonIdentity(PartDTO.PARTS_CONSTANTS);
	}

	@ModelAttribute("pricing_strategies")
	public Map<Long, String> getPricingStrategies() {
		List<PricingStrategy> strategies = ImmutableList.copyOf(pricingService.findAllPricingStrategies());
		return CollectionUtilities.extractKeyValues(strategies, "id", "name");
	}

	@ModelAttribute("client_company_list")
	public Map<Long, String> getClients() {
		return formOptionsDataHelper.getClients(getCurrentUser());
	}

	@ModelAttribute("projects")
	public Map<Long, String> getProjects() {
		return formOptionsDataHelper.getProjects(getCurrentUser());
	}

	@ModelAttribute("mbo")
	public Map<String, Boolean> getMbo() {
		Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());

		boolean mboEnabled = companyOptionsService.hasOption(company, CompanyOption.MBO_ENABLED, "true");
		boolean mboUsageRequired = mboEnabled && companyOptionsService.hasOption(company, CompanyOption.MBO_REQUIRED, "true");

		Map<String, Boolean> map = new HashMap<>();

		map.put("mboEnabled", mboEnabled);
		map.put("mboUsageRequired", mboUsageRequired);

		return map;
	}

	@ModelAttribute("mmw")
	public ManageMyWorkMarket getMmw() {
		return companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId());
	}

	@ModelAttribute("mmw_global")
	public String getMmwJson() {
		return jsonService.toJson(getMmw());
	}

	@ModelAttribute("payterms_available")
	public Boolean getPaytermsAvailable() {
		ManageMyWorkMarket mmw = getMmw();

		if (mmw.getPaymentTermsEnabled() || mmw.getPaymentTermsOverride()) return true;
		return companyService.hasConfirmedBankAccounts(getCurrentUser().getCompanyId());
	}

	@ModelAttribute("show_payterms")
	public Boolean getShowPayterms() {
		ManageMyWorkMarket mmw = getMmw();

		if (mmw.getStatementsEnabled()) return false;
		if (mmw.getPaymentTermsEnabled() || mmw.getPaymentTermsOverride()) return true;
		return companyService.hasConfirmedBankAccounts(getCurrentUser().getCompanyId());
	}

	@ModelAttribute("mmw_templates")
	public String getTemplatesJson() {
		Map<Long, String> templates = workTemplateService.findAllActiveWorkTemplatesIdNameMap(getCurrentUser().getCompanyId());

		return jsonService.toJson(templates);
	}

	@ModelAttribute("surveys")
	public Map<Long, String> getSurveys() {
		return assessmentService.findActiveSurveysByCompany(getCurrentUser().getCompanyId());
	}

	@ModelAttribute("customfields")
	public Map<Long, String> getCustomFields() {
		List<WorkCustomFieldGroup> groups = customFieldService.findWorkCustomFieldGroups(getCurrentUser().getCompanyId());
		return CollectionUtilities.extractKeyValues(groups, "id", "name");
	}

	@ModelAttribute("require_flag_id")
	public Long getRequiredCustomFieldGroupId() {
		WorkCustomFieldGroup group = customFieldService.findRequiredWorkCustomFieldGroup(getCurrentUser().getCompanyId());
		if (group != null)
			return group.getId();
		return null;
	}

	@ModelAttribute("industries")
	public Map<Long, String> getIndustries() {
		return formOptionsDataHelper.getIndustries();
	}

	public Long getDefaultIndustry() {
		Long profileId = profileService.findProfileDTO(getCurrentUser().getId()).getProfileId();
		Industry defaultIndustry = industryService.getDefaultIndustryForProfile(profileId);
		return defaultIndustry.getId();
	}

	@ModelAttribute("spendLimit")
	protected BigDecimal getSpendLimit() {
		if (isAuthenticated()) {
			return accountRegisterServicePrefundImpl.calcSufficientBuyerFundsByCompany(getCurrentUser().getCompanyId());
		}
		return null;
	}

	@ModelAttribute("apLimit")
	protected BigDecimal getAPLimit() {
		if (isAuthenticated() && companyService.hasPaymentTermsEnabled(getCurrentUser().getCompanyId())) {
			return pricingService.calculateRemainingAPBalance(getCurrentUser().getCompanyId());
		}
		return null;
	}

	@ModelAttribute("available_balance")
	protected BigDecimal getAvailableBalance() throws Exception {
		if (isAuthenticated()) {
			return accountRegisterServicePrefundImpl.calculateWithdrawableCashByCompany(getCurrentUser().getCompanyId());
		}
		return null;
	}

	@ModelAttribute("payment_terms_enabled")
	protected Boolean getPayTermsEnabled() throws Exception {
		if (isAuthenticated()) {
			return companyService.hasPaymentTermsEnabled(getCurrentUser().getCompanyId());
		}
		return null;
	}

	@ModelAttribute("assignment_pricing_type")
	protected Integer getAssignmentPricingType() throws Exception {
		if (isAuthenticated()) {
			return companyService.findCompanyById(getCurrentUser().getCompanyId()).getPaymentConfiguration().getPaymentCalculatorType();
		}
		return 0;
	}

	@ModelAttribute("is_subscription")
	protected Boolean isSubscription() throws Exception {
		if (isAuthenticated()) {
			return companyService.findCompanyById(getCurrentUser().getCompanyId()).getPaymentConfiguration().isSubscriptionPricing();
		}
		return Boolean.FALSE;
	}

	@ModelAttribute("has_accounts")
	protected Boolean getBankAccountVerified() throws Exception {
		if (isAuthenticated()) {
			return companyService.hasConfirmedBankAccounts(getCurrentUser().getCompanyId());
		}
		return null;
	}

	@ModelAttribute("requirementSets")
	protected List<RequirementSet> getRequirementSets() {
		return requirementSetsService.findAllActive();
	}

	@ModelAttribute("users")
	public Map<Long, String> getUsers() {
		return formOptionsDataHelper.getActiveUsers(getCurrentUser());
	}

	@ModelAttribute("requireProject")
	public boolean doesRequireProjectEnabled() {
		Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
		if (company.getManageMyWorkMarket() != null) {
			return company.getManageMyWorkMarket().getRequireProjectEnabledFlag();
		}
		return false;
	}

	@ModelAttribute("deliverableFulfillmentDurationsMap")
	public Map<Integer, String> getDeliverableFulfillmentDurations() {
		return WorkSaveRequestValidator.VALID_DELIVERABLE_DEADLINE_HOURS;
	}

	@ModelAttribute("visibilitySettings")
	public List<VisibilityType> getVisibilitySettings() {
		return visibilityService.getVisibilitySettings();
	}

	@ModelAttribute("visibilitySettingsMap")
	public String getVisibilitySettingsMap() {
		return jsonService.toJson(visibilityService.getVisibilityDescriptionsAsMap());
	}

	@ModelAttribute("defaultVisibilitySetting")
	public String getDefaultVisibilitySetting() {
		return VisibilityType.DEFAULT_VISIBILITY;
	}

	@ModelAttribute("routable_groups")
	public Map<Long, String> getRoutableGroups() {
		return super.getRoutableGroups();
	}

	@ModelAttribute("routableGroupsJson")
	public List<Object> getRoutableGroupsJson() {
		Map<Long, String> routableGroups = getRoutableGroups();
		List<Object> routableGroupsJson = new ArrayList<>();
		for (Map.Entry<Long, String> entry : routableGroups.entrySet())
		{
			Map<String, Object> routableGroup = CollectionUtilities.newObjectMap();
			routableGroup.put("id", entry.getKey());
			routableGroup.put("name", entry.getValue());
			routableGroupsJson.add(jsonService.toJson(routableGroup));
		}
		return routableGroupsJson;
	}
}
