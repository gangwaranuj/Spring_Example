package com.workmarket.web.controllers.assignments;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.workmarket.api.v2.employer.assignments.models.RecurrenceDTO;
import com.workmarket.api.v2.employer.assignments.services.RecurrenceService;
import com.workmarket.business.decision.gen.Messages.Decision;
import com.workmarket.business.decision.gen.Messages.DecisionResult;
import com.workmarket.business.decision.gen.Messages.GetDoableDecisionsRequest;
import com.workmarket.business.decision.gen.Messages.QueryDecisionsRequest;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.assessment.AssessmentUserAssociationDAO;
import com.workmarket.dao.decisionflow.CompanyToDecisionFlowTemplateAssociationDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeliverableType;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.audit.ViewType;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.model.integration.autotask.AutotaskTicket;
import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import com.workmarket.domains.model.option.CompanyOption;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.domains.velvetrope.guest.ThriftGuest;
import com.workmarket.domains.velvetrope.rope.AvoidScheduleConflictsThriftRope;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.dashboard.WorkDashboardService;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.domains.work.service.resource.WorkResourceChangeLogService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.validator.WorkSaveRequestValidator;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.VisibilityTypeService;
import com.workmarket.service.business.asset.AssetUploaderService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.integration.hooks.autotask.AutotaskIntegrationService;
import com.workmarket.service.business.requirementsets.EligibilityService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.decisionflow.DecisionFlowService;
import com.workmarket.service.infra.EncryptionService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.option.OptionsService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.WorkResponsePricingHelper;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.CustomFieldGroupSaveRequest;
import com.workmarket.thrift.work.CustomFieldGroupSet;
import com.workmarket.thrift.work.LogEntry;
import com.workmarket.thrift.work.LogEntryType;
import com.workmarket.thrift.work.RequestContext;
import com.workmarket.thrift.work.Resource;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.converters.ThriftWorkToWorkFormConverter;
import com.workmarket.web.converters.WorkFormToThriftWorkConverter;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.assignments.WorkNegotiationForm;
import com.workmarket.web.helpers.FormHelper;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MenuHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.UploadHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.CustomFieldGroupSaveRequestValidator;
import com.workmarket.web.validators.FilenameValidator;
import com.workmarket.web.views.HTML2PDFView;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import static ch.lambdaj.Lambda.exists;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static com.workmarket.utility.CollectionUtilities.contains;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

@Service
public class BaseWorkController extends BaseController {

	private static final Log logger = LogFactory.getLog(BaseWorkController.class);

	@Autowired protected WorkStatusService workStatusService;
	@Autowired protected WorkSubStatusService workSubStatusService;
	@Autowired protected WorkResourceChangeLogService workResourceChangeLogService;
	@Autowired protected WorkService workService;
	@Autowired protected CustomFieldService customFieldService;
	@Autowired protected WorkBundleService workBundleService;
	@Autowired protected WorkResourceService workResourceService;
	@Autowired protected WorkNegotiationService workNegotiationService;
	@Autowired protected TWorkService thriftWorkService;
	@Autowired protected TWorkFacadeService tWorkFacadeService;
	@Autowired protected WorkTemplateService workTemplateService;
	@Autowired protected WorkFormToThriftWorkConverter toWorkConverter;
	@Autowired protected ThriftWorkToWorkFormConverter toWorkFormConverter;
	@Autowired protected WorkSaveRequestValidator workSaveRequestValidator;
	@Autowired protected UserService userService;
	@Autowired protected ProfileService profileService;
	@Autowired protected CompanyService companyService;
	@Autowired protected ProjectService projectService;
	@Autowired protected WorkDashboardService dashboardService;
	@Autowired protected PricingService pricingService;
	@Autowired protected AssessmentService assessmentService;
	@Autowired protected UserGroupService groupService;
	@Autowired protected JsonSerializationService jsonService;
	@Autowired protected InvariantDataService invariantDataService;
	@Autowired protected AssetManagementService assetManagementService;
	@Autowired protected AssetUploaderService assetUploaderService;
	@Autowired protected AuthenticationService authenticationService;
	@Autowired protected CRMService crmService;
	@Autowired protected BillingService billingService;
	@Autowired protected WorkReportService workReportService;
	@Autowired protected RatingService ratingService;
	@Autowired protected EncryptionService encryptionService;
	@Autowired protected DirectoryService directoryService;
	@Autowired protected UserNotificationService userNotificationService;
	@Autowired private FilenameValidator filenameValidator;
	@Autowired protected MessageBundleHelper messageHelper;
	@Autowired protected FormHelper formHelper;
	@Autowired protected FormOptionsDataHelper formDataHelper;
	@Autowired protected EligibilityService eligibilityService;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private AssessmentUserAssociationDAO assessmentUserAssociationDAO; // TODO: get rid of this, should use the service
	@Autowired private CompanyToDecisionFlowTemplateAssociationDAO companyToDecisionFlowTemplateAssociationDAO;
	@Autowired private DecisionFlowService decisionFlowService;
	@Autowired private TaxService taxService;
	@Autowired private AutotaskIntegrationService autotaskIntegrationService;
	@Autowired private WorkFollowService workFollowService;
	@Autowired private MenuHelper menuHelper;
	@Autowired protected FeatureEvaluator featureEvaluator;
	@Autowired private WorkValidationService workValidationService;
	@Autowired protected WorkSearchService workSearchService;
	@Autowired protected DeliverableService deliverableService;
	@Autowired protected VisibilityTypeService visibilityService;
	@Autowired private CustomFieldGroupSaveRequestValidator customFieldGroupValidator;
	@Autowired private PartService partService;
	@Autowired private VendorService vendorService;
	@Autowired private WorkResponsePricingHelper workResponsePricingHelper;
	@Autowired protected OptionsService<Company> companyOptionsService;
	@Autowired private OptionsService<AbstractWork> workOptionsService;
	@Autowired private UploadService uploadService;
	@Autowired private RecurrenceService recurrenceService;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private UserRoleService userRoleService;

	@Qualifier("avoidScheduleConflictsThriftDoorman")
	@Autowired private Doorman doorman;

	protected static final String TEMP_FILE = "work_attachment_";
	private static final Map<String, Map<String, String>> navMap = Maps.newHashMap();
	private static String
		VISIBILITY_SETTINGS_JSON,
		DELIVERABLES_CONSTANTS_JSON,
		PARTS_CONSTANTS_JSON;
	private static List<VisibilityType> VISIBILITY_SETTINGS;

	static {
		try {
			Properties props = PropertiesLoaderUtils.loadAllProperties("nav.assignment.details.properties");
			for (String navElem : props.stringPropertyNames()) {
				String[] navProps = StringUtils.split(navElem, ".");

				if (navProps.length > 1) {
					Map<String, String> map = navMap.get(navProps[0]);

					if (map != null)
						navMap.get(map.put(navProps[1], props.getProperty(navElem)));
					else
						navMap.put(navProps[0], CollectionUtilities.newStringMap(navProps[1], props.getProperty(navElem)));
				} else
					logger.error(String.format("invalid log entry: %s", navElem));
			}
		} catch (IOException e) {
			logger.error("Failed to load assignment detail navigation mappings", e);
		}
	}

	protected enum WorkSaveType {
		WORK, DRAFT, TEMPLATE
	}

	protected class WorkUnauthorizedException extends Exception {
		private static final long serialVersionUID = -2429455859881368752L;
		protected String workNumber;
		protected String errorMessageKey;

		public WorkUnauthorizedException(String workNumber, String errorMessageKey) {
			this.workNumber = workNumber;
			this.errorMessageKey = errorMessageKey;
		}

		public String getWorkNumber() {
			return workNumber;
		}

		public String getErrorMessageKey() {
			return errorMessageKey;
		}
	}

	@PostConstruct
	public void init() {
		DELIVERABLES_CONSTANTS_JSON = jsonService.toJson(ImmutableMap.of(
			"MAX_UPLOAD_SIZE", Constants.MAX_UPLOAD_SIZE,
			"DELIVERABLE_TYPES", DeliverableType.getMapping(),
			"UNSUPPORTED_FILE_MESSAGE", messageHelper.getMessage("upload.invalid_no_extension"),
			"UNSUPPORTED_IMAGE_FILE_MESSAGE", messageHelper.getMessage("deliverable.validation.photos.error.wrong_format"),
			"INVALID_FILE_MESSAGE", messageHelper.getMessage("upload.invalid")
		));
		VISIBILITY_SETTINGS_JSON = jsonService.toJson(visibilityService.getVisibilitySettingsAsMap());
		VISIBILITY_SETTINGS = visibilityService.getVisibilitySettings();
		PARTS_CONSTANTS_JSON = jsonService.toJsonIdentity(PartDTO.PARTS_CONSTANTS);
	}

	@ExceptionHandler(WorkUnauthorizedException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public String handleUnauthorizedException(WorkUnauthorizedException ex, HttpServletRequest httpRequest) {
		MessageBundle messages = messageHelper.newBundle();
		messageHelper.addError(messages, ex.getErrorMessageKey());
		FlashMap flashMap = RequestContextUtils.getOutputFlashMap(httpRequest);
		flashMap.put("bundle", messages);
		return "redirect:/assignments/details/" + ex.getWorkNumber();
	}

	protected AbstractWork getWorkByNumber(String workNumber) {
		if (!StringUtils.isNumeric(workNumber)) {
			throw new HttpException400();
		}

		AbstractWork work;
		try {
			work = workService.findWorkByWorkNumber(workNumber);
		} catch (Exception e) {
			throw new HttpException404();
		}
		return work;
	}


	/**
	 * * Gets work and checks if WorkContexts are valid, and if not, returns 401
	 *
	 * @param workNumber -
	 * @param validContexts -
	 * @return AbstractWork
	 */
	protected AbstractWork getAndAuthorizeWorkByNumber(String workNumber, List<WorkContext> validContexts) {

		AbstractWork work = getWorkByNumber(workNumber);
		List<WorkContext> context = workService.getWorkContext(work.getId(), getCurrentUser().getId());

		if (!CollectionUtils.containsAny(context, validContexts) && !getCurrentUser().hasAnyRoles("ROLE_INTERNAL"))
			throw new HttpException401();

		return work;
	}

	/**
	 * Gets work and checks if WorkContexts are valid, and if not, redirects to main details page with the given flash message.
	 *
	 * @param workNumber -
	 * @param validContexts -
	 * @param errorMessageKey -
	 * @return AbstractWork
	 * @throws WorkUnauthorizedException
	 *
	 */
	protected AbstractWork getAndAuthorizeWorkByNumber(String workNumber, List<WorkContext> validContexts, String errorMessageKey) throws WorkUnauthorizedException {

		AbstractWork work;
		try {
			work = getAndAuthorizeWorkByNumber(workNumber, validContexts);
		} catch (Exception e) {
			throw new WorkUnauthorizedException(workNumber, errorMessageKey);
		}

		return work;
	}

	protected WorkResponse getWorkForWorkDetails(String workNumber) {
		if (!StringUtils.isNumeric(workNumber)) {
			throw new HttpException400();
		}

		WorkResponse workResponse = null;

		try {
			WorkRequest workRequest = new WorkRequest();
			workRequest.setUserId(getCurrentUser().getId())
				.setWorkNumber(workNumber)
				.setViewType(ViewType.WEB);
			workResponse = tWorkFacadeService.findWorkDetailLight(workRequest);
		} catch (Exception e) {
			logger.error(e);
		}

		return workResponse;
	}

	protected WorkResponse loadWorkWithIncludesRespectBuyerSpend(Long userId, String workNumber, Set<WorkRequestInfo> includes) throws Exception {
		WorkRequest workRequest = new WorkRequest();
		workRequest.setUserId(userId);
		workRequest.setWorkNumber(workNumber);
		workRequest.setIncludes(includes);
		WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);

		// If buyer had selected "I want to spend", then we need to convert everything back to the buyer's perception of spend limit.
		if (workResponse.getWork().getConfiguration().isUseMaxSpendPricingDisplayModeFlag()) {
			final int pricingStrategyId = (int) workResponse.getWork().getPricing().getId() - 1;
			final double workFee = workResponse.getWork().getPayment().getBuyerFeePercentage() / 100D;

			if (PricingStrategyType.FLAT.ordinal() == pricingStrategyId) {
				double price = workResponse.getWork().getPricing().getFlatPrice() * (1D + workFee);
				workResponse.getWork().getPricing().setFlatPrice(price);
			} else if (PricingStrategyType.PER_HOUR.ordinal() == pricingStrategyId) {
				double price = workResponse.getWork().getPricing().getPerHourPrice() * (1D + workFee);
				workResponse.getWork().getPricing().setPerHourPrice(price);
			} else if (PricingStrategyType.PER_UNIT.ordinal() == pricingStrategyId) {
				double price = workResponse.getWork().getPricing().getPerUnitPrice() * (1D + workFee);
				workResponse.getWork().getPricing().setPerUnitPrice(price);
			} else if (PricingStrategyType.BLENDED_PER_HOUR.ordinal() == pricingStrategyId) {
				double price = workResponse.getWork().getPricing().getInitialPerHourPrice() * (1D + workFee);
				workResponse.getWork().getPricing().setInitialPerHourPrice(price);
				price = workResponse.getWork().getPricing().getAdditionalPerHourPrice() * (1D + workFee);
				workResponse.getWork().getPricing().setAdditionalPerHourPrice(price);
			}
		}

		return workResponse;
	}

	protected ManageMyWorkMarket getManageMyWorkMarket(Long userId) {
		Company company = profileService.findCompany(userId);
		return (company != null) ? company.getManageMyWorkMarket() : null;
	}

	protected WorkResponse getWork(String workNumber) {
		return getWork(workNumber, ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO), Sets.<AuthorizationContext>newHashSetWithExpectedSize(0), null, null);
	}

	protected WorkResponse getWork(String workNumber, Set<WorkRequestInfo> includes) {
		return getWork(workNumber, includes, Sets.<AuthorizationContext>newHashSetWithExpectedSize(0), null, null);
	}

	protected WorkResponse getWork(String workNumber, Set<WorkRequestInfo> includes, Set<AuthorizationContext> authz) {
		return getWork(workNumber, includes, authz, null, null);
	}

	protected WorkResponse getWork(String workNumber, Set<AuthorizationContext> authz, String messageKey) {
		return getWork(workNumber, ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO), authz, messageKey, null);
	}

	protected WorkResponse getWork(String workNumber, Set<WorkRequestInfo> includes, Set<AuthorizationContext> authz, String messageKey) {
		return getWork(workNumber, includes, authz, messageKey, null);
	}

	protected WorkResponse getWork(String workNumber, Set<WorkRequestInfo> includes, Set<AuthorizationContext> authz, String messageKey, ExtendedUserDetails extendedUserDetails) {
		messageKey = (StringUtils.isNotEmpty(messageKey)) ? "assignment." + messageKey : "assignment";

		if (includes == null) {
			includes = Sets.newHashSetWithExpectedSize(1);
		}

		if (extendedUserDetails == null) {
			extendedUserDetails = getCurrentUser();
		}

		if (!includes.contains(WorkRequestInfo.CONTEXT_INFO)) {
			includes = Sets.newHashSet(includes);
			includes.add(WorkRequestInfo.CONTEXT_INFO);
		}

		WorkRequest workRequest = new WorkRequest()
			.setUserId(extendedUserDetails.getId())
			.setWorkNumber(workNumber)
			.setIncludes(includes);
		WorkResponse workResponse;
		try {
			workResponse = tWorkFacadeService.findWork(workRequest);
		} catch (Exception e) {
			logger.error("Error finding work ", e);
			throw new HttpException404(messageHelper.getMessage(messageKey + ".invalid"));
		}

		if (extendedUserDetails.isInternal()) {
			return workResponse;
		}

		if (workResponse.getRequestContexts().contains(RequestContext.UNRELATED)) {
			throw new HttpException401(messageHelper.getMessage(messageKey + ".not_authorized"));
		}

		if (workResponse.getAuthorizationContexts().isEmpty()) {
			throw new HttpException401(messageHelper.getMessage(messageKey + ".not_authorized"));
		}

		return workResponse;
	}

	protected List<WorkResponse> getWorkAndAuthorizeNotCancelled(List<String> workNumbers, Set<WorkRequestInfo> includes, Set<AuthorizationContext> authz, String messageKey) {
		List<WorkResponse> workResponses = getWorks(workNumbers, includes, authz, messageKey);
		for (WorkResponse workRes : workResponses) {
			AbstractWork work = workService.findWorkByWorkNumber(workRes.getWork().getWorkNumber());
			Long workBuyerId = work.getBuyer().getId();
			Long workCompanyID = work.getCompany().getId();
			Long userCompanyId = getCurrentUser().getCompanyId();
			Long currentUser = getCurrentUser().getId();

			if (work.isCancelled() && (!workBuyerId.equals(currentUser)) && (!workCompanyID.equals(userCompanyId))) {
				throw new HttpException401(messageHelper.getMessage(messageKey + ".not_authorized"));
			}
		}
		return workResponses;
	}

	@SuppressWarnings("unchecked")
	protected List<WorkResponse> getWorks(List<String> workNumbers, Set<WorkRequestInfo> includes, Set<AuthorizationContext> authz, String messageKey) {
		messageKey = (StringUtils.isNotEmpty(messageKey)) ? "assignment." + messageKey : "assignment";
		List workRequests = new ArrayList();
		List<WorkResponse> workResponses;

		if (CollectionUtilities.isEmpty(workNumbers)) {
			throw new HttpException404(messageHelper.getMessage(messageKey + ".invalid"));
		}

		for (String workNumber : workNumbers) {
			WorkRequest workRequest = new WorkRequest()
				.setUserId(getCurrentUser().getId())
				.setWorkNumber(workNumber)
				.setIncludes(includes);
			workRequests.add(workRequest);
		}

		try {
			workResponses = tWorkFacadeService.findWorks(workRequests);
			if (CollectionUtilities.isEmpty(workResponses)) {
				throw new HttpException404();
			}
		} catch (Exception e) {
			throw new HttpException404(messageHelper.getMessage(messageKey + ".invalid"));
		}

		if (getCurrentUser().isInternal())
			return workResponses;

		for (WorkResponse workRes : workResponses) {
			if (workRes.getRequestContexts().contains(RequestContext.UNRELATED)) {
				throw new HttpException401(messageHelper.getMessage(messageKey + ".not_authorized"));
			}

			if (!CollectionUtilities.containsAny(workRes.getAuthorizationContexts(), authz)) {
				throw new HttpException401(messageHelper.getMessage(messageKey + ".not_authorized"));
			}
		}
		return workResponses;
	}

	protected WorkResponse findWorkForFormAuthorization(String workNumber) throws WorkActionException {
		WorkRequest request = new WorkRequest()
			.setUserId(getCurrentUser().getId())
			.setWorkNumber(workNumber)
			.setIncludes(Sets.newHashSet(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO
			));
		return tWorkFacadeService.findWork(request);
	}

	protected WorkResponse findWorkForForm(Long workId) throws WorkActionException {
		return findWorkForForm(new WorkRequest().setWorkId(workId));
	}

	protected WorkResponse findWorkForForm(String workNumber) throws WorkActionException {
		return findWorkForForm(new WorkRequest().setWorkNumber(workNumber));
	}

	private WorkResponse findWorkForForm(WorkRequest request) throws WorkActionException {
		request
			.setUserId(getCurrentUser().getId())
			.setIncludes(Sets.newHashSet(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.INDUSTRY_INFO,
				WorkRequestInfo.PROJECT_INFO,
				WorkRequestInfo.CLIENT_COMPANY_INFO,
				WorkRequestInfo.BUYER_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SUPPORT_CONTACT_INFO,
				WorkRequestInfo.LOCATION_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.ASSETS_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.PARTS_INFO,
				WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO,
				WorkRequestInfo.PAYMENT_INFO,
				WorkRequestInfo.REQUIRED_ASSESSMENTS_INFO,
				WorkRequestInfo.REQUIREMENT_SET_INFO,
				WorkRequestInfo.FOLLOWER_INFO,
				WorkRequestInfo.DELIVERABLES_INFO,
				WorkRequestInfo.GROUP_INFO
			));

		WorkResponse response = tWorkFacadeService.findWork(request);

		normalizeWorkResponsePricing(response.getWork());

		return response;
	}

	protected Map<String, Object> doSaveCustomFields(
		String workNumber,
		CustomFieldGroupSet form,
		BindingResult bindingResult,
		boolean onComplete) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "save_custom_fields");

		AbstractWork work = workService.findWork(workResponse.getWork().getId());

		MessageBundle bundle = messageHelper.newBundle();

		if (form == null || work == null) {
			messageHelper.addError(bundle, "assignment.save_custom_fields.notfound");
			return CollectionUtilities.newObjectMap(
				"successful", false,
				"errors", bundle.getErrors());
		}

		List<CustomFieldGroup> groupSet = form.getCustomFieldGroupSet();

		boolean isActiveResource = contains(workResponse.getAuthorizationContexts(), AuthorizationContext.ACTIVE_RESOURCE);
		boolean isAdmin = contains(workResponse.getAuthorizationContexts(), AuthorizationContext.ADMIN);
		boolean completeAction = onComplete || work.isFinished();
		boolean isSentAction = work.isSent();

		try {
			for (CustomFieldGroup customFieldGroup : groupSet) {

				if (customFieldGroup == null) {
					// This can happen if API user used bad indexes
					continue;
				}

				WorkCustomFieldGroup fieldGroup = customFieldService.findWorkCustomFieldGroup(customFieldGroup.getId());

				CustomFieldGroupSaveRequest customFieldGroupSaveRequest = new CustomFieldGroupSaveRequest(customFieldGroup, fieldGroup, isActiveResource, isAdmin, completeAction, isSentAction);

				List<WorkCustomFieldDTO> dtos = Lists.newArrayList();

				customFieldGroupValidator.validate(customFieldGroupSaveRequest, bindingResult);
				if (!bindingResult.hasErrors()) {
					for (WorkCustomField field : fieldGroup.getActiveWorkCustomFields()) {

						long fieldId = field.getId();
						CustomField submittedField = selectFirst(customFieldGroup.getFields(),
							having(on(CustomField.class).getId(), equalTo(fieldId))
						);
						if (submittedField == null || (field.isOwnerType() && isActiveResource)) {
							continue;
						}
						String submittedValue = submittedField.getValue();
						dtos.add(new WorkCustomFieldDTO(fieldId, submittedValue));
					}
				}

				messageHelper.setErrors(bundle, bindingResult);

				if (bundle.hasErrors()) {
					return CollectionUtilities.newObjectMap("successful", false, "errors", bundle.getErrors());
				}

				customFieldService.addWorkCustomFieldGroupToWork(customFieldGroup.getId(), workResponse.getWork().getId(), customFieldGroup.getPosition());
				customFieldService.saveWorkCustomFieldsForWorkAndIndex(dtos.toArray(new WorkCustomFieldDTO[dtos.size()]), workResponse.getWork().getId());
			}

			return CollectionUtilities.newObjectMap("successful", true);

		} catch (Exception e) {
			logger.error("Error saving custom fields", e);
			messageHelper.addError(bundle, "assignment.save_custom_fields.failure");
			return CollectionUtilities.newObjectMap("successful", false, "errors", bundle.getErrors());
		}
	}

	protected Map<String, Object> buildDeliverableAssetResponse(
		WorkAssetAssociation workAssetAssociation,
		Integer position,
		String workNumber,
		Long deliverableRequirementId
	) {
		com.workmarket.domains.model.asset.Asset asset = workAssetAssociation.getAsset();
		return CollectionUtilities.newObjectMap(
			"successful", true,
			"id", asset.getId(),
			"file_name", asset.getName(),
			"uuid", asset.getUUID(),
			"largeUuid", (workAssetAssociation.getTransformedLargeAsset() != null ? workAssetAssociation.getTransformedLargeAsset().getUUID() : null),
			"mimeType", asset.getMimeType(),
			"isDeliverable", true,
			"mime_type_icon", UploadHelper.getMimeTypeIcon(asset.getMimeType()),
			"uploadedBy", userService.getFullName(asset.getCreatorId()),
			"uploadDate", asset.getCreatedOn().getTimeInMillis(),
			"workNumber", workNumber,
			"deliverableRequirementId", deliverableRequirementId,
			"asset", asset,
			"position", position
		);
	}

	protected Map<String, Object> buildDocumentResponse(
		WorkAssetAssociation workAssetAssociation,
		String workNumber) {
		com.workmarket.domains.model.asset.Asset asset = workAssetAssociation.getAsset();
		return CollectionUtilities.newObjectMap(
			"successful", true,
			"id", asset.getId(),
			"file_name", asset.getName(),
			"uuid", asset.getUUID(),
			"largeUuid", (workAssetAssociation.getTransformedLargeAsset() != null ? workAssetAssociation.getTransformedLargeAsset().getUUID() : null),
			"mimeType", asset.getMimeType(),
			"description", asset.getDescription(),
			"mime_type_icon", UploadHelper.getMimeTypeIcon(asset.getMimeType()),
			"workNumber", workNumber,
			"visibilityCode", VisibilityType.DEFAULT_VISIBILITY
		);
	}

	protected Map<String, Object> doAddAttachments(
		List<WorkResponse> workResponses,
		List<Long> workIds,
		String fileName,
		String description,
		String assetType,
		String contentType,
		long contentLength,
		InputStream inputStream) throws IOException {

		boolean isActiveResource;
		for (WorkResponse workResp : workResponses) {
			isActiveResource = CollectionUtilities.containsAny(workResp.getAuthorizationContexts(), AuthorizationContext.ACTIVE_RESOURCE);
			if (!isActiveResource) break;
		}

		MessageBundle messages = uploadService.validateContentMetadata(contentType, contentLength);
		if (messages.hasErrors()) {
			return CollectionUtilities.newObjectMap(
				"successful", false,
				"errors", messages.getErrors()
			);
		}

		File file;
		FileOutputStream fos = null;
		try {
			file = File.createTempFile(String.format("%s%s", TEMP_FILE, workResponses.get(0).getWork().getWorkNumber()), ".dat");
			fos = new FileOutputStream(file);
			IOUtils.copy(inputStream, fos);
		} catch (IOException ex) {
			messageHelper.addError(messages, "assignment.add_attachment.exception");
			logger.error(String.format("Error creating temp file on attachment upload for assignment %s", workResponses.get(0).getWork().getWorkNumber()), ex);

			return CollectionUtilities.newObjectMap(
				"successful", false,
				"errors", messages.getErrors());
		} finally {
			if (fos != null)
				fos.close();
		}

		try {
			AssetDTO dto = configureAssetDto(file, fileName, description, contentType, assetType, null, false);
			com.workmarket.domains.model.asset.Asset asset = dto.toAsset();

			assetUploaderService.uploadAssets(dto, asset, workIds, authenticationService.getCurrentUser());
			return CollectionUtilities.newObjectMap(
				"successful", true,
				"id", asset.getUUID(),
				"file_name", fileName,
				"uuid", asset.getUUID(),
				"mimeType", contentType,
				"description", dto.getDescription(),
				"type", assetType,
				"mime_type_icon", MimeTypeUtilities.getMimeIconName(contentType),
				"asset", asset,
				"assetdto", dto
			);
		} catch (Exception e) {
			logger.error("", e);
			FileUtils.deleteQuietly(file);
		}

		messageHelper.addError(messages, "assignment.add_attachment.exception");
		return CollectionUtilities.newObjectMap(
			"successful", false,
			"errors", messages.getErrors());
	}

	protected AssetDTO configureAssetDto(
		File file, String fileName, String description, String mimeType,
		String associationType, String availabilityType, boolean isDeliverable) {

		AssetDTO dto = new AssetDTO();

		dto.setMimeType(mimeType);
		dto.setName(fileName);
		dto.setDescription(StringUtils.isNotEmpty(description) ? description : "");
		dto.setAssociationType(associationType);
		dto.setDeliverable(isDeliverable);
		dto.setFileByteSize(Long.valueOf(file.length()).intValue());
		dto.setSourceFilePath(file.getAbsolutePath());
		dto.setLargeTransformation(true);
		if (StringUtils.isNotEmpty(availabilityType)) {
			dto.setAvailabilityTypeCode(availabilityType);
		}

		return dto;
	}

	protected Map<String, Object> doConfirmAssignment(String workNumber) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "confirmation");

		Work work = workResponse.getWork();

		MessageBundle bundle = messageHelper.newBundle();

		if (work.getActiveResource() == null) {
			messageHelper.addError(bundle, "assignment.confirmation.exception");
			return CollectionUtilities.newObjectMap(
				"successful", false,
				"errors", bundle.getErrors());
		}

		try {
			WorkResource result = workService.confirmWorkResource(work.getActiveResource().getUser().getId(), work.getId());
			if (result != null) {
				messageHelper.addSuccess(bundle, "assignment.confirmation.success");
				return CollectionUtilities.newObjectMap("successful", true);
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		messageHelper.addError(bundle, "assignment.confirmation.exception");
		return CollectionUtilities.newObjectMap(
			"successful", false,
			"errors", bundle.getErrors());
	}

	// If buyer had selected "I want to spend", then we need to convert everything back to the buyer's perception of spend limit.
	protected void normalizeWorkResponsePricing(Work work) {
		workResponsePricingHelper.normalizePricing(work);
	}

	private boolean shouldBeIgnored(boolean isInWorkCompany, boolean isActiveResource, boolean isInternal) {
		return !isInWorkCompany && !isActiveResource && !isInternal;
	}

	private static final String[] IGNORE_ATTRIBUTES = {
		"accountPricingType", "approvedAdditionalExpenses", "approvedBonus",
		"buyer", "buyerSupportContactEMail", "checkinCallRequired",
		"checkinContactName", "checkinContactPhone", "checkoutNoteInstructions",
		"checkoutNoteRequiredFlag", "company", "delegationAllowed",
		"desiredSkills", "documents", "followers", "groups", "id", "inProgress",
		"industry", "instructions", "isConfirmable", "location", "offsiteLocation",
		"pendingPaymentFulfillment", "privateInstructions", "project",
		"questionAnswerPairs", "requirementSetIds", "resourceConfirmationHours",
		"resourceConfirmationRequired", "robocallAllowed", "robocallAvailable",
		"shortUrl", "showCheckoutNotesFlag", "showInFeed",
		"timeZone", "timeZoneId", "timetrackingRequired",
		"title"
	};

	private String[] getIgnoredAttributes(boolean isInWorkCompany, boolean isActiveResource, boolean isInternal) {
		return (shouldBeIgnored(isInWorkCompany, isActiveResource, isInternal)) ? IGNORE_ATTRIBUTES : new String[0];
	}

	protected void detailsModelHelper(
		String workNumber,
		ModelMap model,
		HttpServletRequest request,
		final WorkResponse workResponse) {

		if (workResponse == null) {
			throw new HttpException404().setMessageKey("assignment.details.notfound").setRedirectUri("redirect:/assignments");
		}

		final ExtendedUserDetails currentUser = getCurrentUser();
		final Long userId = currentUser.getId();
		final Work work = workResponse.getWork();
		final Resource activeResource = work.getActiveResource();

		if (work == null) {
			logger.error(String.format("Work not found for assignment id=%s, user=%d", workNumber, userId));
			throw new HttpException404()
				.setMessageKey("assignment.details.notavailable")
				.setRedirectUri("redirect:/assignments");
		}

		// Don't show available work between blocked companies
		if (userService.isUserBlockedByCompany(userId, currentUser.getCompanyId(), work.getCompany().getId()) && WorkStatusType.SENT.equals(work.getStatus().getCode())) {
			throw new HttpException401()
				.setMessageKey("assignment.details.notavailable")
				.setRedirectUri("redirect:/assignments");
		}

		final boolean isInternal = request.isUserInRole("ROLE_INTERNAL");
		Set<RequestContext> requestContexts = workResponse.getRequestContexts();
		Set<AuthorizationContext> authContexts = workResponse.getAuthorizationContexts();

		if (requestContexts == null ||
			(requestContexts.contains(RequestContext.UNRELATED) && !isInternal) ||
			authContexts.contains(AuthorizationContext.READ_ONLY)) {
			HttpException401 exception401 = (HttpException401) new HttpException401()
				.setMessageKey("assignment.details.notavailable")
				.setRedirectUri("redirect:/assignments");

			if (authContexts.contains(AuthorizationContext.READ_ONLY)) {
				exception401.setPrintStackTrace(false);
			}

			throw exception401;
		}

		// BASE ATTRIBUTES
		final boolean isAdmin = authContexts.contains(AuthorizationContext.ADMIN);
		final boolean isResource = authContexts.contains(AuthorizationContext.RESOURCE);
		final boolean isOwner = authContexts.contains(AuthorizationContext.BUYER);
		final boolean isActiveResource = authContexts.contains(AuthorizationContext.ACTIVE_RESOURCE);
		final boolean isDeputy = authenticationService.userHasAclRole(userId, AclRole.ACL_DEPUTY);
		final boolean isViewOnly = authenticationService.userHasAclRole(userId, AclRole.ACL_VIEW_ONLY);
		final boolean isManager = authenticationService.userHasAclRole(userId, AclRole.ACL_MANAGER);
		final boolean isDispatcher = authContexts.contains(AuthorizationContext.DISPATCHER);
		final boolean isPay = authContexts.contains(AuthorizationContext.PAY) || userRoleService.hasPermissionsForCustomAuth(userId, Permission.APPROVE_WORK_AUTH);
		final boolean isInWorkCompany = work.getCompany() != null && work.getCompany().getId() == currentUser.getCompanyId();
		final boolean isRatingShown = WorkStatusType.RATING_SHOWN_WORK_STATUS_TYPES.contains(work.getStatus().getCode());
		final boolean isRatingEditable = WorkStatusType.PAYMENT_PENDING_STATUS_TYPES.contains(work.getStatus().getCode());
		final boolean isBuyerAuthorizedToEditPrice = isInWorkCompany && currentUser.getEditPricingCustomAuth();
		final boolean isBuyerAuthorizedToApproveCounter = isInWorkCompany & (currentUser.getApproveWorkCustomAuth() || currentUser.getCounterOfferCustomAuth());
		final boolean isDeclinedResource = requestContexts.contains(RequestContext.DECLINED_RESOURCE);
		final boolean isDeclinedVendor = isDispatcher ? vendorService.getDeclinedVendorIdsByWork(work.getId()).contains(currentUser.getCompanyId()) : false;
		final boolean isAssignToFirstToAcceptVendor = isDispatcher ? vendorService.getAssignToFirstToAcceptVendorIdsByWork(work.getId()).contains(currentUser.getCompanyId()) : false;
		final boolean isPendingNegotiation =
			activeResource == null ? false : activeResource.isBudgetNegotiationPending() || activeResource.isExpenseNegotiationPending() || activeResource.isBonusNegotiationPending();
		final boolean resourceBlocked =
			work.isSetActiveResource() && work.getActiveResource().getUser() != null && isInWorkCompany &&
				userService.isUserBlockedByCompany(activeResource.getUser().getId(), activeResource.getUser().getCompany().getId(), currentUser.getCompanyId());
		model.addAttribute("workResponse", workResponse);
		model.addAttribute("work", work);
		model.addAttribute("workTitle", work.getTitle()); // Used to set the page title on assignment details pages
		model.addAttribute("active_resource", activeResource);
		model.addAttribute("is_cancelled_resource", requestContexts.contains(RequestContext.CANCELLED_RESOURCE));
		model.addAttribute("is_declined_resource", isDeclinedResource || isDeclinedVendor);
		model.addAttribute("is_rating_shown", isRatingShown);
		model.addAttribute("is_rating_editable", isRatingEditable);
		model.addAttribute("is_admin", isAdmin);
		model.addAttribute("is_resource", isResource);
		model.addAttribute("is_owner", isOwner);
		model.addAttribute("can_pay", isPay);
		model.addAttribute("is_active_resource", isActiveResource);
		model.addAttribute("read_only", authContexts.contains(AuthorizationContext.READ_ONLY));
		model.addAttribute("is_internal", isInternal);
		model.addAttribute("is_deputy", isDeputy);
		model.addAttribute("is_dispatcher", isDispatcher);
		model.addAttribute("is_view_only", isViewOnly);
		model.addAttribute("is_manager", isManager);
		model.addAttribute("is_invited_resource", requestContexts.contains(RequestContext.INVITED));
		model.addAttribute("is_work_feed", work.isShowInFeed());
		model.addAttribute("is_in_work_company", isInWorkCompany);
		model.addAttribute("isBuyerAuthorizedToEditPrice", isBuyerAuthorizedToEditPrice);
		model.addAttribute("companyName", work.getCompany().getName());
		model.addAttribute("ownerFullName", StringUtilities.fullName(work.getBuyer().getName().getFirstName(), work.getBuyer().getName().getLastName()));
		model.addAttribute("ownerEmail", work.getBuyer().getEmail());
		model.addAttribute("hasActiveResource", work.isSetActiveResource());
		model.addAttribute("isCompanyResource", currentUser.getCompanyId().equals(work.getCompany().getId()));
		model.addAttribute("isWorkBundle", workResponse.isWorkBundle());
		model.addAttribute("isInWorkBundle", workResponse.isInWorkBundle());
		model.addAttribute("workStatusTypes", ModelEnumUtilities.workStatusTypes);
		model.addAttribute("workSubStatusTypes", ModelEnumUtilities.workSubStatusTypes);
		model.addAttribute("pricingStrategyTypes", ModelEnumUtilities.pricingStrategyTypes);
		model.addAttribute("laneTypes", ModelEnumUtilities.laneTypes);
		model.addAttribute("assignment_tz", DateUtilities.getShortTimeZoneName(work.getTimeZone()));
		model.addAttribute("assignment_tz_millis_offset", java.util.TimeZone.getTimeZone(work.getTimeZone()).getOffset(Calendar.getInstance().getTimeInMillis()));
		model.addAttribute("workFee", work.getPayment() != null ? work.getPayment().getBuyerFeePercentage() : "");
		model.addAttribute("maxWorkFee", Constants.MAX_WORK_FEE);
		model.addAttribute("pricingNotEditable", !work.isPricingEditable());
		model.addAttribute("isAssignToFirstToAcceptVendor", isAssignToFirstToAcceptVendor);
		model.addAttribute("isPendingNegotiation", isPendingNegotiation);
		model.addAttribute("resourceBlocked", resourceBlocked);
		model.addAttribute("hasScheduleConflicts", false);

		if (isResource) {
			List<AbstractWork> conflicts = Lists.newArrayList();
			doorman.welcome(
				new ThriftGuest(work.getBuyer()),
				new AvoidScheduleConflictsThriftRope(
					workResourceDAO,
					workService,
					work,
					getCurrentUser().getId(),
					conflicts
				)
			);
			if (!conflicts.isEmpty()) {
				model.addAttribute("hasScheduleConflicts", true);
				model.addAttribute("scheduleConflicts", conflicts);
			}
		}

		final Optional<String> flowUuid = decisionFlowService.getDecisionFlowUuid(work.getId());
		if (flowUuid.isPresent()) {
			final List<Decision> doableDecisions = getOpenDecisions(flowUuid.get());
			if (!CollectionUtils.isEmpty(doableDecisions)) {
				model.addAttribute("numRemainingDecisions", doableDecisions.size());
			}

			final List<Decision> currentUserDecisions = getCurrentUserDecisions(flowUuid.get(), currentUser.getUuid());
			if (!CollectionUtils.isEmpty(currentUserDecisions)) {
				model.addAttribute("decisionResult", currentUserDecisions.get(0).getDecisionResult());
				model.addAttribute("flowUuid", currentUserDecisions.get(0).getFlowUuid());
				model.addAttribute("decisionUuid", currentUserDecisions.get(0).getUuid());
				model.addAttribute("deciderUuid", currentUserDecisions.get(0).getDecider().getUuid());
			}
		}

		// STATUS DEPENDENT ATTRIBUTES
		if (WorkStatusType.SENT.equals(work.getStatus().getCode())) {
			addSentStatusModelAttributes(model, work, isResource);

			// Having an active resource is equivalent to the work being in any status beyond sent in the assignment lifecycle
		} else if (work.isSetActiveResource() && work.getActiveResource().getUser() != null) {
			addAssignedStatusModelAttributes(model, work);
		}

		// AUTH INFO (json)
		model.addAttribute("authEncoded", jsonService.toJson(CollectionUtilities.newObjectMap(
			"isAdmin", isAdmin,
			"isResource", isResource,
			"isOwner", isOwner,
			"isActiveResource", isActiveResource,
			"isInternal", isInternal,
			"isDeputy", isDeputy,
			"isDispatcher", isDispatcher,
			"isBuyerAuthorizedToApproveCounter", isBuyerAuthorizedToApproveCounter,
			"isIndividualBundledAssignment", workResponse.isInWorkBundle(),
			"isParentBundle", workResponse.isWorkBundle()
		)));

		// WORK PROPERTIES (json)
		String[] ignore = getIgnoredAttributes(isInWorkCompany, isActiveResource, isInternal);
		String workJson;
		try {
			workJson = jsonService.toJson(work, ignore);
		} catch (Exception e) {
			workJson = "{}";
		}
		model.addAttribute("work_encoded", workJson);

		// SECONDARY PROPERTIES
		model.addAttribute("showDocuments", !workResponse.isWorkBundle() && (isAdmin || isInternal || !CollectionUtilities.isEmpty(work.getAssets())));
		model.addAttribute("visibilitySettingsJson", VISIBILITY_SETTINGS_JSON);
		model.addAttribute("visibilitySettings", VISIBILITY_SETTINGS);

		model.addAttribute("deliverablesConstantsJson", DELIVERABLES_CONSTANTS_JSON);

		model.addAttribute("partsConstants", PartDTO.PARTS_CONSTANTS);
		model.addAttribute("partsConstantsJson", PARTS_CONSTANTS_JSON);

		model.addAttribute("hasRequiredAssessments", exists(work.getAssessments(), having(on(Assessment.class).isIsRequired(), is(true))));

		model.addAttribute("buyerScoreCard", analyticsService.getBuyerScoreCardByCompanyId(work.getBuyer().getCompany().getId()));

		addCustomFieldsModelAttributes(model, work);

		// FILTER LABELS
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
		model.addAttribute("available_labels", availableLabels);

		// NAV MENU
		List<Object> result = menuHelper.populateNavigationList(model, work.getStatus().getCode(), currentUser.hasAnyRoles("ROLE_MASQUERADE"));

		// Remove add_resources menu if work is in a bundle
		if (workResponse.isInWorkBundle()) {
			result = menuHelper.removeItem("add_resources", result);
			result = menuHelper.removeItem("negotiate", result);
		}

		// Remove print option if assignment printout is disabled
		if (!work.getConfiguration().isEnableAssignmentPrintout()) {
			result = menuHelper.removeItem("print_option", result);
		}

		WorkResponse changelogResponse = getWork(workNumber, ImmutableSet.of(WorkRequestInfo.CHANGE_LOG_INFO));
		if (changelogResponse.getWork() != null && changelogResponse.getWork().getChangelog() != null) {
			for (LogEntry entry : changelogResponse.getWork().getChangelog()) {
				if (LogEntryType.WORK_STATUS_CHANGE.equals(entry.getType()) && "Invoiced".equals(entry.getStatus())) {
					result = menuHelper.removeItem("unassign", result);
					break;
				}
			}
		}

		model.addAttribute("nav", result);
		model.addAttribute("navMap", navMap);

		// AUTOTASK
		final boolean isAutotask = autotaskIntegrationService.isCreatedByAutotask(work.getId());
		model.addAttribute("is_autotask", isAutotask);
		final AutotaskUser autotaskUser = autotaskIntegrationService.findAutotaskUserByCompanyId(currentUser.getCompanyId());
		if (isAutotask && autotaskUser != null) {
			model.addAttribute("zoneUrl", autotaskUser.getZoneUrl());
			Optional<AutotaskTicket> optTicket = autotaskIntegrationService.findAutotaskTicketByWorkId(autotaskUser, work.getId());
			if (optTicket.isPresent()) {
				model.addAttribute("autotaskID", optTicket.get().getTicketId());
			}
		}

		// BUYER SIDE ONLY ATTRIBUTES
		if (isAdmin || isOwner || isInternal) {
			model.addAttribute(
				"followers",
				formDataHelper.getFollowers(work.getCompany().getId(), ImmutableList.of(work.getBuyer().getId()))
			);
			model.addAttribute(
				"is_following",
				userId.equals(work.getBuyer().getId()) || workFollowService.isFollowingWork(work.getId(), userId)
			);
			model.addAttribute("hasInvitedAtLeastOneVendor", vendorService.hasInvitedAtLeastOneVendor(work.getId()));

			if (isAdmin) {
				Company company = profileService.findCompanyById(currentUser.getCompanyId());
				model.addAttribute("isAutoPayEnabled", company.getManageMyWorkMarket().getAutoPayEnabled());
			}
		}

		// WORKER SIDE ONLY ATTRIBUTES
		if (isActiveResource) {
			if (!PricingStrategyType.INTERNAL.equals(work.getPricing().getType())) {

				// Tax setting for assigned resource
				boolean hasVerifiedTaxEntity = false;
				boolean hasRejectedTaxEntity = false;
				boolean hasTaxEntity = false;
				boolean needUSATaxEntity = false;

				if (!work.isOffsiteLocation()) {
					if (work.getLocation() != null && work.getLocation().getAddress() != null) {
						needUSATaxEntity = Country.USA_COUNTRY.equals(Country.newInstance(work.getLocation().getAddress().getCountry()));
					}
				}

				AbstractTaxEntity activeTaxEntity = taxService.findActiveTaxEntity(work.getActiveResource().getUser().getId());
				if (activeTaxEntity != null) {
					hasTaxEntity = true;
					if (activeTaxEntity instanceof UsaTaxEntity) {
						hasVerifiedTaxEntity = activeTaxEntity.getStatus().isApproved();
						hasRejectedTaxEntity = activeTaxEntity.getStatus().isRejected();
					}
				}

				if (!hasTaxEntity || (!hasVerifiedTaxEntity && needUSATaxEntity)) {
					model.addAttribute("showTaxAlert", true);
					model.addAttribute("hasRejectedTaxEntity", hasRejectedTaxEntity);
				}
			}
		}

		// MBO
		model.addAttribute(
			"mboEnabled",
			workOptionsService.hasOptionByEntityId(work.getId(), WorkOption.MBO_ENABLED, "true")
		);

		RecurrenceDTO recurrence = recurrenceService.getRecurrence(work.getId());
		if(recurrence != null) {
			model.addAttribute("recurrenceDescription", recurrence.getDescription());
			model.addAttribute("recurrenceUUID", recurrence.getUuid());
		}

		boolean shouldHideContact = companyOptionsService.hasOptionByEntityId(work.getCompany().getId(), CompanyOption.HIDE_CONTACT_ENABLED, "true");
		model.addAttribute("showContact", (isAdmin || (shouldHideContact ? isActiveResource : isResource)));
	}

	private List<Decision> getOpenDecisions(final String flowUuid) {
		final QueryDecisionsRequest queryDecisionsRequest = QueryDecisionsRequest.newBuilder()
				.addDecisionFlowUuid(flowUuid)
				.setDecisionResult(DecisionResult.DECISION_OPEN)
				.build();
		return decisionFlowService.queryDecisions(queryDecisionsRequest);
	}

	private List<Decision> getCurrentUserDecisions(final String flowUuid, final String deciderUuid) {
		final GetDoableDecisionsRequest currentUserDoableDecisionsRequest = GetDoableDecisionsRequest.newBuilder()
				.setDecisionFlowUuid(flowUuid)
				.setDeciderUuid(deciderUuid)
				.build();
		return decisionFlowService.getDoableDecisions(currentUserDoableDecisionsRequest);
	}

	private void addSentStatusModelAttributes(ModelMap model, Work work, boolean isResource) {
		model.addAttribute("hasTransactionalPricing", work.hasTransactionalPricing());
		model.addAttribute("is_employee", getCurrentUser().isSeller() && getCurrentUser().getCompanyId().equals(work.getCompany().getId()));

		if (isResource) {
			model.addAttribute("applyForm", WorkNegotiationForm.newInstance(work));
			model.addAttribute("eligibility", eligibilityService.getEligibilityFor(getCurrentUser().getId(), work));
			model.addAttribute("displayUnblock", userService.isCompanyBlockedByUser(
				getCurrentUser().getId(), getCurrentUser().getCompanyId(), work.getCompany().getId()
				)
			);
			model.addAttribute(
				"isAcceptableOrApplyable",
				workValidationService.isWorkResourceValidForWork(
					getCurrentUser().getId(),
					getCurrentUser().getCompanyId(),
					work.getCompany() != null ? work.getCompany().getId() : null
				)
			);
		}
	}

	private void addAssignedStatusModelAttributes(ModelMap model, Work work) {
		final String
			resourceFullName = work.getActiveWorkerFullName(),
			resourceEmail = work.getActiveResource().getUser().getEmail();
		final Long workerId = work.getActiveResource().getUser().getId();
		final Profile activeWorkerProfile = profileService.findProfile(workerId);

		model.addAttribute("attempt_on_behalf", assessmentUserAssociationDAO.findSurveysCompletedForWorkOnBehalf(work.getId(), workerId));
		model.addAttribute("resource_mobile_phone", StringUtilities.formatPhoneNumber(activeWorkerProfile.getMobilePhone()));
		model.addAttribute("resource_work_phone", StringUtilities.formatPhoneNumber(activeWorkerProfile.getWorkPhone()));
		model.addAttribute("dispatcher", workResourceService.getDispatcherForWorkAndWorker(work.getId(), workerId));
		model.addAttribute("resourceFullName", resourceFullName);
		model.addAttribute("resourceEmail", resourceEmail);

		AbstractWork abstractWork = getWorkByNumber(work.getWorkNumber());
		if (abstractWork.isComplete()) {
			BigDecimal expectedPrice = getExpectedPrice(abstractWork);
			model.addAttribute("expectedPrice", expectedPrice);
			model.addAttribute("overridePriceDiffersFromExpectedPrice", overridePriceDiffers(abstractWork, expectedPrice));
		}
	}

	private BigDecimal getExpectedPrice(AbstractWork abstractWork) {
		WorkResource resource = workResourceService.findActiveWorkResource(abstractWork.getId());
		BigDecimal expectedPrice = pricingService.calculateTotalResourceCostWithoutOverride(abstractWork, resource);
		expectedPrice = expectedPrice.setScale(2, RoundingMode.HALF_UP);
		return expectedPrice;
	}

	private boolean overridePriceDiffers(AbstractWork abstractWork, BigDecimal expectedPrice) {
		BigDecimal overridePrice = abstractWork.getPricingStrategy().getFullPricingStrategy().getOverridePrice();
		return overridePrice != null && overridePrice.compareTo(expectedPrice) != 0;
	}

	private void addCustomFieldsModelAttributes(ModelMap model, Work work) {
		if (isNotEmpty(work.getCustomFieldGroups())) {

			Collection<CustomField> headerDisplayFields = Lists.newArrayList();
			boolean hasOwnerFields = false;
			boolean hasResourceFields = false;

			for (CustomFieldGroup fieldGroup : work.getCustomFieldGroups()) {
				if (CollectionUtils.isEmpty(fieldGroup.getFields())) continue;

				for (CustomField field : fieldGroup.getFields()) {
					if (field == null) {
						continue;
					}
					if (field.isShowInAssignmentHeader()) {
						headerDisplayFields.add(field);
					}
					if (WorkCustomFieldType.RESOURCE.equals(field.getType())) {
						hasResourceFields = true;
					} else {
						hasOwnerFields = true;
					}
				}
			}

			model.addAttribute("headerDisplayFields", headerDisplayFields);
			model.addAttribute("hasResourceFields", hasResourceFields);
			model.addAttribute("hasOwnerFields", hasOwnerFields);
		}

		model.addAttribute("client_only_cf_sets", customFieldService.findClientFieldSetIdsMap(getCurrentUser().getCompanyId()));
	}

	protected Map<Long, String> getRoutableGroups() {
		List<ManagedCompanyUserGroupRow> groups = groupService.findSharedAndOwnedGroups(getCurrentUser().getCompanyId());
		Map<Long, String> results = Maps.newLinkedHashMap();
		for (ManagedCompanyUserGroupRow group : groups) {
			String groupName = group.getName();
			if (!group.getCompanyId().equals(getCurrentUser().getCompanyId())) {
				groupName += " [SHARED]";
			}
			results.put(group.getGroupId(), groupName);
		}
		return results;
	}

	/**
	 * Gets the number of Work Resources available by WORK_RESOURCE_STATUS
	 *
	 * @param workId -
	 * @param status -
	 * @return Long
	 */
	protected Long numWorkByResourceStatus(Long workId, String status) {

		try {
			WorkResourcePagination pagination = new WorkResourcePagination();
			pagination.setResultsLimit(5);
			pagination.setStartRow(0);
			pagination.addFilter(WorkResourcePagination.FILTER_KEYS.WORK_RESOURCE_STATUS, status);
			WorkResourcePagination results = workService.findWorkResources(workId, pagination);

			return (results == null) ? 0L : results.getResults().size();

		} catch (Exception e) {
			logger.error("unable to get work pagination for status lookup", e);
		}
		return 0L;
	}

	/**
	 * Generate work details PDF and send it to the resource.
	 * Uses UserNotificationService to send the notification email asynchronously.
	 *
	 * TODO: this needs to go in the service
	 *
	 * @param workNumber -
	 * @param resourceId -
	 * @param request -
	 * @param model -
	 */
	protected void sendAcceptedWorkDetailsPDFtoResource(String workNumber, Long resourceId, HttpServletRequest request, Model model) {
		// Generate work order PDF
		HTML2PDFView pdfView;
		String html = null;
		try {
			pdfView = generateViewForPDF(workNumber, "pdf/assignment", model);
			if (model != null) {
				html = pdfView.getResponseAsString(model.asMap(), request);
			} else {
				html = pdfView.getResponseAsString(null, request);
			}

		} catch (Exception e) {
			logger.error("Error creating work order PDF");
			logger.error(e.toString());
		}

		// Send work details to resource
		if (isNotBlank(html)) {
			userNotificationService.sendWorkDetailsToResource(resourceId, workService.findWorkId(workNumber), html);
		}
	}

	protected BindingResult getFilenameErrors(String fileName) {
		MapBindingResult bind = new MapBindingResult(Maps.newHashMap(), "fileName");
		filenameValidator.validate(fileName, bind);
		return bind;
	}

	@Deprecated
	protected HTML2PDFView generateViewForPDF(String workNumber, String path, Model model) throws Exception {
		WorkRequest workRequest = new WorkRequest()
			.setUserId(getCurrentUser().getId())
			.setWorkNumber(workNumber)
			.setIncludes(Sets.newHashSet(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.PROJECT_INFO,
				WorkRequestInfo.COMPANY_INFO,
				WorkRequestInfo.CLIENT_COMPANY_INFO,
				WorkRequestInfo.BUYER_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SUPPORT_CONTACT_INFO,
				WorkRequestInfo.LOCATION_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.PAYMENT_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.PARTS_INFO,
				WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO,
				WorkRequestInfo.ASSETS_INFO,
				WorkRequestInfo.DELIVERABLES_INFO
			));

		WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);

		if (workResponse.getRequestContexts().contains(RequestContext.UNRELATED) && !getCurrentUser().hasAnyRoles("ROLE_INTERNAL")) {
			throw new HttpException401();
		}

		if (model != null) {
			model.addAttribute("work", workResponse.getWork());
			model.addAttribute("workResponse", workResponse);
			model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
			model.addAttribute("isResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.RESOURCE));
			model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));
			model.addAttribute("workStatusTypes", ModelEnumUtilities.workStatusTypes);
		}

		return new HTML2PDFView(path);
	}

	public View generatePdf(String workNumber, Model model) {
		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.PROJECT_INFO,
			WorkRequestInfo.COMPANY_INFO,
			WorkRequestInfo.CLIENT_COMPANY_INFO,
			WorkRequestInfo.BUYER_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO,
			WorkRequestInfo.LOCATION_CONTACT_INFO,
			WorkRequestInfo.SUPPORT_CONTACT_INFO,
			WorkRequestInfo.LOCATION_INFO,
			WorkRequestInfo.SCHEDULE_INFO,
			WorkRequestInfo.PRICING_INFO,
			WorkRequestInfo.PAYMENT_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
			WorkRequestInfo.PARTS_INFO,
			WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO,
			WorkRequestInfo.ASSETS_INFO,
			WorkRequestInfo.DELIVERABLES_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "generatePDF");

		Work work = workResponse.getWork();
		if (work.getPartGroup() != null) {
			work.getPartGroup().setParts(partService.getPartsByGroupUuid(work.getPartGroup().getUuid()));
		}
		model.addAttribute("work", work);
		model.addAttribute("workResponse", workResponse);
		model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		model.addAttribute("isResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.RESOURCE));
		model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));
		model.addAttribute("workStatusTypes", ModelEnumUtilities.workStatusTypes);

		return new HTML2PDFView("pdf/assignment", workNumber + ".pdf");
	}
}
