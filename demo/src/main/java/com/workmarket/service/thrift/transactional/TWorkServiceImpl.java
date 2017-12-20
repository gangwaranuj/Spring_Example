package com.workmarket.service.thrift.transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.service.wrapper.response.Response;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.crm.ClientContactDAO;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.DeliverableRequirementGroup;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.AssignmentsSurveyDeletionRope;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.RoutingStrategyGroupDAO;
import com.workmarket.domains.work.facade.service.WorkFacadeService;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.model.route.RoutingStrategyGroup;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkActionRequestFactory;
import com.workmarket.domains.work.service.WorkQuestionService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.resource.WorkResourceAddOptions;
import com.workmarket.domains.work.service.resource.WorkResourceChangeLogService;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.domains.work.service.route.WorkRoutingValidator;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.validator.WorkSaveRequestValidator;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.redis.RedisFilters;
import com.workmarket.search.qualification.MutateResponse;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationBuilder;
import com.workmarket.search.qualification.QualificationClient;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.ValidationService;
import com.workmarket.service.business.dto.AssessmentDTO;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.ClientContactDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.dto.WorkTemplateDTO;
import com.workmarket.service.business.event.BulkWorkUploadEvent;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.qualification.QualificationAssociationService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.InvalidParameterException;
import com.workmarket.service.exception.account.DuplicateWorkNumberException;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.option.OptionsService;
import com.workmarket.service.thrift.TWorkUploadService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.service.thrift.transactional.work.WorkResponseBuilder;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.core.Asset;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.core.Upload;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.DeclineWorkOfferRequest;
import com.workmarket.thrift.work.MultipleWorkSendRequest;
import com.workmarket.thrift.work.RescheduleRequest;
import com.workmarket.thrift.work.Resource;
import com.workmarket.thrift.work.ResourceNoteRequest;
import com.workmarket.thrift.work.RoutingStrategy;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.VoidWorkRequest;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkActionResponse;
import com.workmarket.thrift.work.WorkActionResponseCodeType;
import com.workmarket.thrift.work.WorkQuestionRequest;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.thrift.work.WorkSendRequest;
import com.workmarket.thrift.work.uploader.WorkUpload;
import com.workmarket.thrift.work.uploader.WorkUploadError;
import com.workmarket.thrift.work.uploader.WorkUploadException;
import com.workmarket.thrift.work.uploader.WorkUploadInvalidFileTypeException;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import com.workmarket.thrift.work.uploader.WorkUploadResponse;
import com.workmarket.thrift.work.uploader.WorkUploadRowLimitExceededException;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import rx.functions.Action1;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service(value = "workHandler")
public class TWorkServiceImpl implements TWorkService {
	private static final Log logger = LogFactory.getLog(TWorkServiceImpl.class);
	public static final String WORK_TEMPLATE = RedisConfig.WORK_TEMPLATE;

	private static final String QUALIFICATION_FEATURE = "addWorkQualification";

	@Autowired private AuthenticationService authenticationService;
	@Autowired private CRMService crmService;
	@Autowired private DirectoryService directoryService;
	@Autowired private WorkService workService;
	@Autowired private WorkFacadeService workFacadeService;
	@Autowired private CustomFieldService customFieldService;
	@Autowired private WorkRoutingService workRoutingService;
	@Autowired private RoutingStrategyService routingStrategyService;
	@Autowired private WorkTemplateService workTemplateService;
	@Autowired private ValidationService validationService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private ProjectService projectService;
	@Autowired private WorkResourceChangeLogService workResourceChangeLogService;
	@Autowired private WorkQuestionService workQuestionService;
	@Autowired private WorkFollowService workFollowService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private WorkResponseBuilder responseBuilder;
	@Autowired private WorkSaveRequestValidator saveRequestValidator;
	@Autowired private ProfileService profileService;
	@Autowired private DeliverableService deliverableService;
	@Autowired @Qualifier("workOptionsService") private OptionsService<AbstractWork> workOptionsService;
	@Autowired private BaseWorkDAO abstractWorkDAO;
	@Autowired private ClientContactDAO clientContactDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private WorkCustomFieldDAO customFieldDAO;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private TWorkUploadService uploader;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private WorkRoutingValidator workRoutingValidator;
	@Autowired private InvariantDataService invariantService;
	@Autowired private PartService partService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private WorkAuditService workAuditService;
	@Autowired private WorkActionRequestFactory workActionRequestFactory;
	@Autowired private RoutingStrategyGroupDAO routingStrategyGroupDAO;
	@Autowired private QualificationClient qualificationClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private QualificationAssociationService qualificationAssociationService;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired @Qualifier("assignmentsDoorman") Doorman assignmentsDoorman;

	@Override
	public Work saveWork(WorkSaveRequest request) throws ValidationException {
		final com.workmarket.domains.model.User currentUser = authenticationService.getCurrentUser();
		saveRequestValidator.validateWork(request);

		WorkDTO dto = buildWorkDTO(request, currentUser);
		dto.setUniqueExternalId(request.getWork().getUniqueExternalIdValue());
		com.workmarket.domains.work.model.Work work = workFacadeService.saveOrUpdateWork(currentUser.getId(), dto);

		saveProject(request, work);
		savePartLogistics(request, work);
		saveRequirementSets(request, work);
		saveDeliverableRequirement(request, work);
		saveOptions(request, work);
		saveQualifications(request, work.getId());

		return work;
	}

	private WorkResponse saveOrUpdateWork(WorkSaveRequest request) throws ValidationException {
		final com.workmarket.domains.model.User currentUser = authenticationService.getCurrentUser();
		saveRequestValidator.validateWork(request);

		try {
			WorkDTO dto = buildWorkDTO(request, currentUser);

			com.workmarket.domains.work.model.Work work = workFacadeService.saveOrUpdateWork(currentUser.getId(), dto);

			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
			eventRouter.sendEvent(new UserSearchIndexEvent(currentUser.getId()));

			saveProject(request, work);
			saveAssets(request, work.getId());
			savePartLogistics(request, work);
			saveCustomFields(request, work.getId());
			saveAssessments(request, work.getId());
			saveRequirementSets(request, work);
			saveResource(request, work.getId());
			saveFollowers(request, work.getId());
			saveRoutingStrategy(request, work.getId());
			saveDeliverableRequirement(request, work);
			saveOptions(request, work);
			saveQualifications(request, work.getId());

			Set<WorkRequestInfo> emptySet = Sets.newHashSet();
			return responseBuilder.buildWorkResponse(work, currentUser, emptySet);
		} catch (Exception e) {
			logger.error("There was an error saving/updating work", e);
			throw new RuntimeException("There was an error saving/updating work", e);
		}
	}

	@Override
	public WorkDTO buildWorkDTO(WorkSaveRequest request, com.workmarket.domains.model.User currentUser) {
		WorkDTO dto = new WorkDTO();
		saveWorkCore(request, dto);
		saveSchedule(request, dto);
		savePricing(request, dto);
		saveConfiguration(request, dto);
		saveTemplate(request, dto);
		saveClientCompany(request, dto);
		saveLocation(request, dto, currentUser);
		saveSupportContact(request, dto);
		return dto;
	}

	@Override
	public WorkResponse saveOrUpdateWorkDraft(WorkSaveRequest request) throws ValidationException {
		return saveOrUpdateWorkDraft(request, Sets.<WorkRequestInfo>newHashSet());
	}

	@Override
	public WorkResponse saveOrUpdateWorkDraft(WorkSaveRequest request, Set<WorkRequestInfo> includes) throws ValidationException {
		final com.workmarket.domains.model.User currentUser = authenticationService.getCurrentUser();
		// don't validate bulk upload assignments - it already happens previously
		if (!request.isPartOfBulk()) {
			saveRequestValidator.validateWorkDraft(request);
		}

		int step = 1;
		StopWatch timer = new StopWatch();
		try {
			WorkDTO dto = new WorkDTO();
			timer.start((step++) + ". work core");
			saveWorkCore(request, dto);
			timer.stop();

			timer.start((step++) + ". schedule");
			saveSchedule(request, dto);
			timer.stop();

			timer.start((step++) + ". pricing");
			savePricing(request, dto);
			timer.stop();

			timer.start((step++) + ". configuration");
			saveConfiguration(request, dto);
			timer.stop();

			timer.start((step++) + ". template");
			saveTemplate(request, dto);
			timer.stop();

			timer.start((step++) + ". client company");
			saveClientCompany(request, dto);
			timer.stop();

			timer.start((step++) + ". location");
			saveLocation(request, dto, currentUser);
			timer.stop();

			timer.start((step++) + ". support contact");
			saveSupportContact(request, dto);
			timer.stop();

			timer.start((step++) + ". work");
			dto.setPartOfBulk(request.isPartOfBulk());
			com.workmarket.domains.work.model.Work work = workFacadeService.saveOrUpdateWork(currentUser.getId(), dto);
			timer.stop();

			timer.start((step++) + ". project");
			saveProject(request, work);
			timer.stop();

			timer.start((step++) + ". assets");
			saveAssets(request, work.getId());
			timer.stop();

			timer.start((step++) + ". part logistics");
			savePartLogistics(request, work);
			timer.stop();

			timer.start((step++) + ". custom fields");
			saveCustomFields(request, work.getId());
			timer.stop();

			timer.start((step++) + ". assessments");
			saveAssessments(request, work.getId());
			timer.stop();

			timer.start((step++) + ". requirement sets");
			saveRequirementSets(request, work);
			timer.stop();

			timer.start((step++) + ". followers");
			saveFollowers(request, work.getId());
			timer.stop();

			timer.start((step++) + ". labels");
			saveLabels(request, work.getId());
			timer.stop();

			timer.start((step++) + ". deliverable requirement");
			saveDeliverableRequirement(request, work);
			timer.stop();

			timer.start((step++) + ". options");
			saveOptions(request, work);
			timer.stop();

			timer.start((step++) + ". groups");
			saveGroups(request, work.getId());
			timer.stop();

			timer.start((step++) + ". build response");
			WorkResponse response = responseBuilder.buildWorkResponse(work, currentUser, includes);
			timer.stop();

			timer.start(step + ". qualifications");
			saveQualifications(request, work.getId());
			timer.stop();

			logger.info("finished bulk save: " + timer.prettyPrint());
			response.setWorkAuthorizationResponses(Sets.newHashSet(WorkAuthorizationResponse.SUCCEEDED));
			return response;
		} catch (Exception e) {
			logger.error("saveOrUpdateWorkDraft failed: ", e);
			try {
				timer.stop();
			} catch (IllegalStateException ise) {
				logger.error("Timer not started when original exception was thrown");
			} finally {
				logger.info("Did NOT finish bulk save: " + timer.prettyPrint());
			}
			throw new RuntimeException(e);
		}
	}

	@Override
	@CacheEvict(
		value = WORK_TEMPLATE,
		key = "#root.target.WORK_TEMPLATE + #request.getWork().getTemplate().getId() + ':' + #request.getUserId()",
		condition = "#request.isSetWork() && #request.getWork().isSetTemplate() && #request.getWork().getTemplate().getId() > 0"
	)
	public WorkResponse saveOrUpdateWorkTemplate(WorkSaveRequest request) throws ValidationException, RuntimeException {
		final com.workmarket.domains.model.User currentUser = authenticationService.getCurrentUser();
		saveRequestValidator.validateTemplate(request);

		try {
			WorkTemplateDTO dto = new WorkTemplateDTO();
			saveWorkCore(request, dto);
			saveSchedule(request, dto);
			savePricing(request, dto);
			saveConfiguration(request, dto);
			saveTemplate(request, dto);
			saveClientCompany(request, dto);
			saveLocation(request, dto, currentUser);
			saveSupportContact(request, dto);

			com.workmarket.domains.work.model.WorkTemplate work = workTemplateService.saveOrUpdateWorkTemplate(currentUser.getId(), dto);

			saveGroups(request, work.getId());
			saveProject(request, work);
			saveAssets(request, work.getId());
			savePartLogistics(request, work);
			clearCustomFieldsIfDefaultValue(request);
			saveCustomFields(request, work.getId());
			saveAssessments(request, work.getId());
			saveFollowers(request, work.getId());
			saveRequirementSets(request, work);
			saveDeliverableRequirement(request, work);

			saveOptions(request, work);

			Set<WorkRequestInfo> emptySet = Sets.newHashSet();
			WorkResponse response = responseBuilder.buildWorkResponse(work, currentUser, emptySet);
			response.setWorkAuthorizationResponses(Sets.newHashSet(WorkAuthorizationResponse.SUCCEEDED));
			return response;
		} catch (Exception e) {
			logger.error("There was an error saving the assignment", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void saveResource(WorkSaveRequest request, long workId) {
		if (!request.isSetAssignTo()) {
			return;
		}

		try {
			workRoutingService.addToWorkResources(workId, request.getAssignTo().getId(), request.isAssignToFirstToAccept());
			} catch (WorkNotFoundException e) {
				logger.error("Error routing assignment: " + workId, e);
			}
	}

	@Override
	public void saveRoutingStrategy(WorkSaveRequest request, long workId) {

		if (request.getWork() != null && request.isSmartRoute()) {
			routingStrategyService.addAutoRoutingStrategy(workId, request.isAssignToFirstToAccept());
			return;
		}

		if (isEmpty(request.getRoutingStrategies())) {
			return;
		}

		RoutingStrategyGroup routingStrategyGroup = new RoutingStrategyGroup();
		routingStrategyGroupDAO.saveOrUpdate(routingStrategyGroup);

		for (RoutingStrategy s : request.getRoutingStrategies()) {
			if (s.getFilter() != null) {
				routingStrategyService.addPeopleSearchRequestRoutingStrategy(workId, routingStrategyGroup.getId(), s.getFilter(), s.getDelayMinutes(), s.isAssignToFirstToAccept());
			} else if (isNotEmpty(s.getVendorCompanyNumbers())) {
				routingStrategyService.addVendorRoutingStrategyByCompanyNumbers(workId, routingStrategyGroup.getId(), s.getVendorCompanyNumbers(), s.getDelayMinutes(), s.isAssignToFirstToAccept());
			} else if (isNotEmpty(s.getRoutingUserNumbers())) {
				routingStrategyService.addUserNumbersRoutingStrategy(workId, routingStrategyGroup.getId(), s.getRoutingUserNumbers(), s.getDelayMinutes(), s.isAssignToFirstToAccept());
			}
		}

		routingStrategyService.scheduleExecuteRoutingStrategyGroup(workId, routingStrategyGroup.getId(), 0);
	}

	@Override
	public void saveOptions(WorkSaveRequest request, AbstractWork work) {
		if (workOptionsService.hasOption(work, WorkOption.MBO_ENABLED, "true") || request.isUseMboServices()) {
			workOptionsService.setOption(work, WorkOption.MBO_ENABLED, String.valueOf(request.isUseMboServices()));
		}
		if (request.getWork() != null && request.getWork().getPricing() != null) {
			workService.setOfflinePayment(work, request.getWork().getPricing().isOfflinePayment());
		}
	}

	public void saveWorkCore(WorkSaveRequest request, WorkDTO dto) {
		if (request.getWork().isSetId()) {
			dto.setId(request.getWork().getId());
		}
		dto.setTitle(request.getWork().getTitle());
		dto.setDescription(StringUtilities.removeControlCharsIfAny(request.getWork().getDescription()));
		dto.setInstructions(request.getWork().getInstructions());
		dto.setDocumentsEnabled((request.getWork().getDocumentsEnabled()));
		dto.setDesiredSkills(request.getWork().getDesiredSkills());

		if (request.getWork().isSetPrivateInstructions()) {
			dto.setPrivateInstructions(request.getWork().getPrivateInstructions());
		}

		dto.setBuyerId(request.getWork().getBuyer().getId());

		if (request.getWork().isSetIndustry()) {
			dto.setIndustryId(request.getWork().getIndustry().getId());
		}

		// blatantly stolen from com/workmarket/web/converters/WorkFormToThriftWorkConverter.java:197...
		// Base the assignment's time zone relative to the location that it's to occur in;
		// Otherwise default to the creator's timezone.
		com.workmarket.thrift.work.Work work = request.getWork();
		Long timeZoneId = null;
		if (work.getLocation() != null && work.getLocation().getAddress() != null) {
			String country = work.getLocation().getAddress().getCountry();
			String zip = work.getLocation().getAddress().getZip();

			zip = (isBlank(zip) && isNotBlank(country)) ? Constants.NO_POSTALCODE : zip;

			if (isBlank(zip) && isBlank(country) && work.getLocation().isSetId()) {
				ClientLocation location = crmService.findClientLocationById(work.getLocation().getId());

				if (location == null) { // bad data, abort
					work.setLocation(null);
					work.setOffsiteLocation(null);

				} else if (location.getAddress() != null)  {
					zip = location.getAddress().getPostalCode();
					country = work.getLocation().getAddress().getCountry();
					work.getLocation().setName( location.getName() );
					work.getLocation().setNumber( location.getLocationNumber() );
					work.getLocation().getAddress()
						.setAddressLine1( location.getAddress().getAddress1() )
						.setAddressLine2( location.getAddress().getAddress2() )
						.setCity( location.getAddress().getCity() )
						.setState( location.getAddress().getState().getName() )
						.setZip( zip )
						.setCountry( location.getAddress().getCountry().getId() )
						.setDressCode( location.getAddress().getDressCode().getDescription() );
				}
			}
			if (isNotBlank(zip)) {
				PostalCode postalCode = isNotBlank(country) ?
					invariantService.getPostalCodeByCodeAndCountryId(zip, country) :
					invariantService.getPostalCodeByCode(zip);
				if (postalCode != null) {
					timeZoneId = postalCode.getTimeZone().getId();
				}
				dto.setTimeZoneId(timeZoneId);
			}
		}
		if (timeZoneId == null) {
			dto.setTimeZoneId(work.getTimeZoneId());
		}

		dto.setUniqueExternalId(request.getWork().getUniqueExternalIdValue());

		logger.trace("\t work core ID: " + dto.getId());
	}

	private void saveConfiguration(WorkSaveRequest request, WorkDTO dto) {
		dto.setResourceConfirmationRequired(request.getWork().isResourceConfirmationRequired());
		dto.setResourceConfirmationHours(request.getWork().getResourceConfirmationHours());
		dto.setCheckinRequired(request.getWork().getConfiguration().isCheckinRequiredFlag());
		dto.setCheckinCallRequired(request.getWork().isCheckinCallRequired());
		dto.setCheckinContactName(request.getWork().getCheckinContactName());
		dto.setCheckinContactPhone(request.getWork().getCheckinContactPhone());
		dto.setShowCheckoutNotes(request.getWork().isShowCheckoutNotesFlag());
		dto.setCheckoutNoteRequired(request.getWork().isCheckoutNoteRequiredFlag());
		dto.setCheckoutNoteInstructions(request.getWork().getCheckoutNoteInstructions());
		dto.setRequireTimetracking(request.getWork().isTimetrackingRequired());
		dto.setIvrActive(request.getWork().getConfiguration().isIvrEnabledFlag());
		dto.setBadgeShowClientName(request.getWork().getConfiguration().isBadgeShowClientName());
		dto.setDisablePriceNegotiation(request.getWork().getConfiguration().isDisablePriceNegotiation());
		dto.setAssignToFirstResource(request.getWork().getConfiguration().isAssignToFirstResource());
		dto.setShowInFeed(request.getWork().getConfiguration().isShowInFeed());
		dto.setUseRequirementSets(request.getWork().getConfiguration().isUseRequirementSets());
		dto.setCustomFieldsEnabledFlag(request.getWork().getConfiguration().isCustomFieldsEnabledFlag());
		dto.setCustomCloseOutEnabledFlag(request.getWork().getConfiguration().isCustomCloseOutEnabledFlag());
		dto.setAssessmentsEnabled(request.getWork().getConfiguration().isAssessmentsEnabled());
		dto.setPartsLogisticsEnabledFlag(request.getWork().getConfiguration().isPartsLogisticsEnabledFlag());
		dto.setDocumentsEnabled(request.getWork().getDocumentsEnabled());
	}

	private void saveClientCompany(WorkSaveRequest request, WorkDTO dto) {
		if (!request.getWork().isSetClientCompany()) {
			return;
		}
		dto.setClientCompanyId(request.getWork().getClientCompany().getId());
		logger.trace("\t client company ID: " + dto.getClientCompanyId());
	}

	private void saveTemplate(WorkSaveRequest request, WorkDTO dto) {
		if (!request.getWork().isSetTemplate()) {
			return;
		}

		dto.setWorkTemplateId((request.getTemplateId() != null) ?
			request.getTemplateId() :
			request.getWork().getTemplate().getId());

		if (request.getWork().isCheckinCallRequired()) {
			dto.setCheckinContactName(request.getWork().getCheckinContactName());
			dto.setCheckinContactPhone(request.getWork().getCheckinContactPhone());
		}

		if (dto instanceof WorkTemplateDTO) {
			((WorkTemplateDTO) dto).setTemplateName(request.getWork().getTemplate().getName());
			((WorkTemplateDTO) dto).setTemplateDescription(request.getWork().getTemplate().getDescription());
		}
		logger.trace("\t template ID: " + dto.getWorkTemplateId());
	}

	private void savePricing(WorkSaveRequest request, WorkDTO dto) {
		if (!request.getWork().isSetPricing()) {
			return;
		}

		if (request.getWork().isSetId()) {
			AbstractWork workModel = workService.findWork(request.getWork().getId());

			if (!workModel.isPricingEditable()) {
				return;
			}
		}

		dto.setUseMaxSpendPricingDisplayModeFlag(request.getWork().getConfiguration().isUseMaxSpendPricingDisplayModeFlag());

		dto.setPricingStrategyId(request.getWork().getPricing().getId());
		if (request.getWork().getPricing().isSetFlatPrice()) {
			dto.setFlatPrice(request.getWork().getPricing().getFlatPrice());
		}
		if (request.getWork().getPricing().isSetMaxFlatPrice()) {
			dto.setMaxFlatPrice(request.getWork().getPricing().getMaxFlatPrice());
		}
		if (request.getWork().getPricing().isSetPerHourPrice()) {
			dto.setPerHourPrice(request.getWork().getPricing().getPerHourPrice());
		}
		if (request.getWork().getPricing().isSetMaxNumberOfHours()) {
			dto.setMaxNumberOfHours(request.getWork().getPricing().getMaxNumberOfHours());
		}
		if (request.getWork().getPricing().isSetPerUnitPrice()) {
			dto.setPerUnitPrice(request.getWork().getPricing().getPerUnitPrice());
		}
		if (request.getWork().getPricing().isSetMaxNumberOfUnits()) {
			dto.setMaxNumberOfUnits(request.getWork().getPricing().getMaxNumberOfUnits());
		}
		if (request.getWork().getPricing().isSetInitialPerHourPrice()) {
			dto.setInitialPerHourPrice(request.getWork().getPricing().getInitialPerHourPrice());
		}
		if (request.getWork().getPricing().isSetInitialNumberOfHours()) {
			dto.setInitialNumberOfHours(request.getWork().getPricing().getInitialNumberOfHours());
		}
		if (request.getWork().getPricing().isSetAdditionalPerHourPrice()) {
			dto.setAdditionalPerHourPrice(request.getWork().getPricing().getAdditionalPerHourPrice());
		}
		if (request.getWork().getPricing().isSetMaxBlendedNumberOfHours()) {
			dto.setMaxBlendedNumberOfHours(request.getWork().getPricing().getMaxBlendedNumberOfHours());
		}
		if (request.getWork().getPricing().isSetInitialPerUnitPrice()) {
			dto.setInitialPerUnitPrice(request.getWork().getPricing().getInitialPerUnitPrice());
		}
		if (request.getWork().getPricing().isSetInitialNumberOfUnits()) {
			dto.setInitialNumberOfUnits(request.getWork().getPricing().getInitialNumberOfUnits());
		}
		if (request.getWork().getPricing().isSetAdditionalPerUnitPrice()) {
			dto.setAdditionalPerUnitPrice(request.getWork().getPricing().getAdditionalPerUnitPrice());
		}
		if (request.getWork().getPricing().isSetMaxBlendedNumberOfUnits()) {
			dto.setMaxBlendedNumberOfUnits(request.getWork().getPricing().getMaxBlendedNumberOfUnits());
		}

		if (request.getWork().getConfiguration().isSetPaymentTermsDays()) {
			dto.setPaymentTermsDays(request.getWork().getConfiguration().getPaymentTermsDays());
			dto.setPaymentTermsEnabled((dto.getPaymentTermsDays() > 0));
		}

		logger.trace("\t pricing strategy ID: " + dto.getPricingStrategyId());
	}

	private void saveSchedule(WorkSaveRequest request, WorkDTO dto) {
		if (!request.getWork().isSetSchedule())
			return;

		dto.setIsScheduleRange(request.getWork().getSchedule().isRange());
		dto.setScheduleFromString(DateUtilities.getISO8601(request.getWork().getSchedule().getFrom()));
		dto.setScheduleThroughString(DateUtilities.getISO8601(request.getWork().getSchedule().getThrough()));

		logger.trace("\t schedule from " + dto.getScheduleFromString() + " to " + dto.getScheduleThroughString());
	}

	private void saveLocation(WorkSaveRequest request, WorkDTO dto, com.workmarket.domains.model.User currentUser) {
		if (request.getWork().isSetLocation()) {
			dto.setIsOnsiteAddress(true);
		} else if (request.getWork().getOffsiteLocation() != null) {
			dto.setIsOnsiteAddress(!request.getWork().getOffsiteLocation());
		}

		if (!request.getWork().isSetLocation()) {
			return;
		}

		if (request.getWork().isSetClientCompany() && request.getWork().getLocation().getId() != 0) {
			dto.setLocationId(request.getWork().getLocation().getId());
			saveLocationContacts(request, dto, currentUser);
			return;
		}

		LocationDTO locationDTO = new LocationDTO();
		if (request.getWork().getLocation().isSetId()) {
			locationDTO.setId(request.getWork().getLocation().getId());
		}
		locationDTO.setName(request.getWork().getLocation().getName());
		locationDTO.setLocationNumber(request.getWork().getLocation().getNumber());
		locationDTO.setInstructions(request.getWork().getLocation().getInstructions());
		locationDTO.setCompanyId(currentUser.getCompany().getId());
		locationDTO.setAddress1(request.getWork().getLocation().getAddress().getAddressLine1());
		locationDTO.setAddress2(request.getWork().getLocation().getAddress().getAddressLine2());
		locationDTO.setCity(request.getWork().getLocation().getAddress().getCity());
		locationDTO.setState(request.getWork().getLocation().getAddress().getState());
		locationDTO.setPostalCode(request.getWork().getLocation().getAddress().getZip());
		if (request.getWork().getLocation().getAddress().getPoint() != null) {
			locationDTO.setLatitude(new BigDecimal(request.getWork().getLocation().getAddress().getPoint().getLatitude()));
			locationDTO.setLongitude(new BigDecimal(request.getWork().getLocation().getAddress().getPoint().getLongitude()));
		}
		locationDTO.setCountry(request.getWork().getLocation().getAddress().getCountry());
		locationDTO.setAddressTypeCode("company");
		locationDTO.setLocationTypeId(request.getWork().getLocation().getAddress().getLocationType());
		locationDTO.setDressCodeId(DressCode.valueOf(request.getWork().getLocation().getAddress().getDressCode()));

		Set<javax.validation.ConstraintViolation<LocationDTO>> locationErrors = validationService.validateEntity(locationDTO);
		if (locationErrors.isEmpty()) {
			Location location;
			if (request.getWork().isSetClientCompany()) {
				location = crmService.saveOrUpdateClientLocation(request.getWork().getClientCompany().getId(), locationDTO, null);
			} else {
				location = directoryService.saveOrUpdateLocation(locationDTO);
			}
			dto.setLocationId(location.getId());
		} else {
			dto.setIsOnsiteAddress(false);
			logger.debug("Skipped saving location.");
		}

		// Should be caught by the validator...
		if (dto.isSetOnsiteAddress() && dto.getIsOnsiteAddress() && dto.getLocationId() == null) {
			Assert.state(false, "You must select a location.");
		}

		saveLocationContacts(request, dto, currentUser);
		logger.trace("\t location ID: " + dto.getLocationId());
	}

	private com.workmarket.domains.model.crm.ClientContact findOrSaveLocationContact(
		User tcontact,
		WorkDTO dto,
		com.workmarket.domains.model.User currentUser) {

		logger.debug("TWorkServiceImpl.findOrSaveLocationContact() -- begin");
		com.workmarket.domains.model.crm.ClientContact contact;

		if (tcontact.isSetId()) {
			contact = clientContactDAO.get(tcontact.getId());

			Assert.notNull(contact, "Please select a location contact for this location.");
		} else {
			ClientContactDTO contactDTO = new ClientContactDTO();

			if (dto.getClientCompanyId() != null) {
				contactDTO.setClientCompanyId(dto.getClientCompanyId());
				contactDTO.setClientLocationId(dto.getLocationId());
			}

			contactDTO.setFirstName(tcontact.getName().getFirstName());
			contactDTO.setLastName(tcontact.getName().getLastName());

			if (tcontact.isSetEmail()) {
				contactDTO.getEmails().add(new EmailAddressDTO(tcontact.getEmail()));
			}


			if (tcontact.getProfile() != null && tcontact.getProfile().isSetPhoneNumbers()) {
				for (Phone n : tcontact.getProfile().getPhoneNumbers()) {
					if (n.getPhone() == null)
						continue;
					contactDTO.getPhoneNumbers().add(new PhoneNumberDTO(n.getPhone(), n.getExtension(), ContactContextType.WORK));

				}
			}

			// If the work has a location attached to it, associated this contact with location
			if (dto.getLocationId() != null) {
				contactDTO.setClientLocationId(dto.getLocationId());
			}

			contact = crmService.saveOrUpdateClientContact(currentUser.getCompany().getId(), contactDTO, null);
		}
		logger.trace("\t contact: " + dto.getContactName());
		return contact;
	}

	private void saveLocationContacts(WorkSaveRequest request, WorkDTO dto, com.workmarket.domains.model.User currentUser) {
		logger.debug("TWorkServiceImpl.saveLocationContact() -- begin");
		if (request.getWork().isSetLocationContact()) {
			com.workmarket.domains.model.crm.ClientContact contact = findOrSaveLocationContact(
				request.getWork().getLocationContact(), dto, currentUser);
			dto.setServiceClientContactId(contact.getId());
		}

		if (request.getWork().isSetSecondaryLocationContact()) {
			com.workmarket.domains.model.crm.ClientContact contact = findOrSaveLocationContact(
				request.getWork().getSecondaryLocationContact(), dto, currentUser);
			dto.setSecondaryClientContactId(contact.getId());
		}
		logger.trace("\t secondary contact ID: " + dto.getSecondaryClientContactId());
	}

	private void saveSupportContact(WorkSaveRequest request, WorkDTO dto) {
		if (!request.getWork().isSetSupportContact())
			return;

		Assert.state(request.getWork().getSupportContact().isSetId());

		com.workmarket.domains.model.User contact = userDAO.findUserById(request.getWork().getSupportContact().getId());

		Assert.notNull(contact);

		dto.setSupportName(StringUtilities.fullName(contact.getFirstName(), contact.getLastName()));
		dto.setSupportEmail(contact.getEmail());
		List<com.workmarket.domains.model.directory.Phone> contactPhones = profileService.findPhonesByProfileId(contact.getProfile().getId());
		com.workmarket.domains.model.directory.Phone phoneNumber = null;
		if (isNotEmpty(contactPhones)) {
			phoneNumber = contactPhones.iterator().next();
			dto.setSupportPhone(phoneNumber.getPhone());
		}

		dto.setBuyerSupportUserId(contact.getId());
		dto.setBuyerSupportContactFirstName(contact.getFirstName());
		dto.setBuyerSupportContactLastName(contact.getLastName());
		dto.setBuyerSupportContactEMail(contact.getEmail());
		if (phoneNumber != null) {
			dto.setBuyerSupportContactPhone(phoneNumber.getPhone());
			dto.setBuyerSupportContactPhoneExtension(phoneNumber.getExtension());
		}

		if (dto.getServiceClientContactId() != null && dto.getBuyerSupportUserId() != null) {
			dto.setSameServiceContact(dto.getServiceClientContactId().equals(dto.getBuyerSupportUserId()));
		}
		logger.trace("\t support contact: " + dto.getSupportName());
	}

	private void saveDeliverableRequirement(WorkSaveRequest request, AbstractWork work) {
		if (request == null ||
			request.getWork() == null ||
			request.getWork().getDeliverableRequirementGroupDTO() == null) {
			return;
		}

		DeliverableRequirementGroupDTO deliverableRequirementGroupDTO = request.getWork().getDeliverableRequirementGroupDTO();

		// Clean instructions before saving to DB
		String cleanedInstructions = StringUtilities.stripXSSAndEscapeHtml(deliverableRequirementGroupDTO.getInstructions());
		deliverableRequirementGroupDTO.setInstructions(cleanedInstructions);

		DeliverableRequirementGroup deliverableRequirementGroup =
			deliverableService.saveOrUpdateDeliverableRequirementGroup(deliverableRequirementGroupDTO);

		work.setDeliverableRequirementGroup(deliverableRequirementGroup);
	}

	private void saveProject(WorkSaveRequest request, com.workmarket.domains.work.model.AbstractWork work) {
		if (request.getWork().isSetProject() && request.getWork().getProject().isSetId()) {
			projectService.addWorkToProject(work.getId(), request.getWork().getProject().getId());
			logger.trace("\t project ID: " + request.getWork().getProject().getId());
		} else {
			work.setProject(null);
			logger.trace("\t no project specified");
		}
	}

	@Override
	public void saveAssets(WorkSaveRequest request, long workId) {
		if (request.getWork().isSetAssets()) {
			List<AssetDTO> dtos = Lists.newArrayList();
			for (Asset a : request.getWork().getAssets()) {
				AssetDTO dto = new AssetDTO();
				dto.setAssetId(a.getId());
				dto.setName(a.getName());
				dto.setDescription(a.getDescription());
				dto.setAssociationType(WorkAssetAssociationType.ATTACHMENT);
				if (StringUtils.isNotEmpty(a.getVisibilityCode())) {
					dto.setVisibilityTypeCode(a.getVisibilityCode());
				}
				dtos.add(dto);
			}
			assetManagementService.setAssetsForWork(dtos.toArray(new AssetDTO[dtos.size()]), workId);
			logger.trace("\t assets attached: " + dtos.size());
		}

		if (request.getWork().isSetUploads()) {
			for (Upload u : request.getWork().getUploads()) {
				UploadDTO dto = new UploadDTO();
				dto.setUploadUuid(u.getUuid());
				dto.setName(u.getName());
				dto.setDescription(u.getDescription());
				dto.setAssociationType(WorkAssetAssociationType.ATTACHMENT);
				if (StringUtils.isNotEmpty(u.getVisibilityCode())) {
					dto.setVisibilityTypeCode(u.getVisibilityCode());
				}
				try {
					assetManagementService.addUploadToWork(dto, workId);
				} catch (HostServiceException e) {
					logger.error("Error saving assessments: " + workId, e);
				}
			}
			logger.trace("\t uploads attached: " + request.getWork().getUploads().size());
		}
	}

	private void savePartLogistics(WorkSaveRequest request, com.workmarket.domains.work.model.AbstractWork work) {
		Assert.notNull(request);
		Assert.notNull(request.getWork());

		PartGroupDTO partGroup = request.getWork().getPartGroup();
		if (partGroup == null) {
			partService.deletePartGroup(work.getId());
			return;
		}
		partGroup.setWorkId(work.getId());
		partService.saveOrUpdatePartGroup(partGroup);
	}

	/**
	 * If the request does not have any custom fields, existing groups are deleted
	 *
	 * @param request The instructions passed from the calling service
	 * @param workId  The model of the existing/newly-created assignment
	 */
	@Override
	public void saveCustomFields(WorkSaveRequest request, long workId) {
		if (!request.getWork().isSetCustomFieldGroups()) {
			customFieldService.deleteWorkCustomFieldGroupsFromWork(workId);
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
			return;
		}

		//! TODO: sort by position to get order, then flatten to remove any missing index values
		//! E.g.: If user sets positions 3, 5, 6, we save as 0, 1, 2
		Map<Long, Integer> customFieldGroupIds = new HashMap<>();
		for (CustomFieldGroup g : request.getWork().getCustomFieldGroups()) {
			customFieldGroupIds.put(g.getId(), g.getPosition());
		}

		//Save the groups
		customFieldService.setWorkCustomFieldGroupsForWork(workId, CollectionUtilities.sortMapByValues(customFieldGroupIds));

		//Save the groups' fields
		for (CustomFieldGroup g : request.getWork().getCustomFieldGroups()) {
			if (!g.hasFields()) {
				continue;
			}

			List<WorkCustomFieldDTO> customFieldDTOs = Lists.newArrayList();
			for (CustomField f : g.getFields()) {
				WorkCustomFieldDTO field = new WorkCustomFieldDTO();
				field.setId(f.getId());
				field.setValue(f.getValue());
				customFieldDTOs.add(field);
			}
			WorkCustomFieldDTO[] customFieldsArray = new WorkCustomFieldDTO[customFieldDTOs.size()];
			customFieldService.saveWorkCustomFieldsForWorkAndIndex(customFieldDTOs.toArray(customFieldsArray), workId);
		}
		logger.trace("\t custom fields: " + customFieldGroupIds.size());
	}

	@Override
	public void clearCustomFieldsIfDefaultValue(WorkSaveRequest request) {
		if (!request.getWork().isSetCustomFieldGroups()) {
			return;
		}
		for (CustomFieldGroup fieldGroup : request.getWork().getCustomFieldGroups()) {
			if (!fieldGroup.hasFields()) {
				continue;
			}
			List<WorkCustomField> fields = customFieldDAO.findAllFieldsForCustomFieldGroup(fieldGroup.getId());
			if (CollectionUtils.isNotEmpty(fields)) {
				for (CustomField thriftField : fieldGroup.getFields()) {
					WorkCustomField actualField = null;
					for (WorkCustomField field : fields) {
						if (field.getId().equals(thriftField.getId())) {
							actualField = field;
						}
					}
					if (actualField == null ||
						thriftField.getValue() == null ||
						thriftField.getValue().equals(actualField.getDefaultValue())
						) {
						thriftField.setValue(null);
					}
				}
			}
		}
	}

	@Override
	public void saveAssessments(WorkSaveRequest request, long workId) {
		if (!request.getWork().isSetAssessments()) {
			return;
		}

		List<AssessmentDTO> assessmentDTOs = Lists.newArrayListWithCapacity(request.getWork().getAssessmentsSize());
		for (Assessment a : request.getWork().getAssessments()) {
			if (!a.isSetId()) {
				continue;
			}
			AssessmentDTO dto = makeAssessmentDTO();
			dto.setId(a.getId());
			dto.setRequired(a.isIsRequired());
			assessmentDTOs.add(dto);
		}

		assignmentsDoorman.welcome(
			new UserGuest(authenticationService.getCurrentUser()),
			new AssignmentsSurveyDeletionRope(assessmentService, workId));

		if (isNotEmpty(assessmentDTOs)) {
			assessmentService.setAssessmentsForWork(assessmentDTOs, workId);
			workAuditService.auditAndReindexWork(workActionRequestFactory.create(workId, WorkAuditType.ADD_ASSESSMENT));
			logger.trace("\t assessments: " + request.getWork().getAssessments().size());
		}
	}

	public AssessmentDTO makeAssessmentDTO() {
		return new AssessmentDTO();
	}

	@Override
	public void saveGroups(WorkSaveRequest request, long workId) {
		workService.clearGroupsForWork(workId);
		if (request.getRoutingStrategies() != null) {
			for (RoutingStrategy routingStrategy : request.getRoutingStrategies()) {
				if (routingStrategy.getFilter() == null) {
					continue;
				}
				Set<Long> groups = routingStrategy.getFilter().getGroupFilter();
				if (CollectionUtils.isEmpty(groups)) {
					continue;
				}
				if (routingStrategy.isAssignToFirstToAccept()) {
					workService.addFirstToAcceptGroupsForWork(routingStrategy.getFilter().getGroupFilter(), workId);
				} else {
					workService.addGroupsForWork(routingStrategy.getFilter().getGroupFilter(), workId);
				}
			}
			return;
		}
		// TODO: we can probably remove the following when the old assignment UI (used only in templates now) is retired
		if (!request.getWork().getFirstToAcceptGroups().isEmpty()) {
			workService.addFirstToAcceptGroupsForWork(request.getWork().getFirstToAcceptGroups(), workId);
		}
		if (!request.getWork().getNeedToApplyGroups().isEmpty()) {
			workService.addGroupsForWork(request.getWork().getNeedToApplyGroups(), workId);
		}
	}

	private void saveRequirementSets(WorkSaveRequest request, com.workmarket.domains.work.model.AbstractWork work) {
		if (request.getWork() != null && request.getWork().getRequirementSetIds() != null) {
			workService.addRequirementSetsToWork(work, request.getWork().getRequirementSetIds());
		}
	}

	@Override
	public void saveWorkResources(final WorkSaveRequest request, final long workId) throws Exception {
		Assert.notNull(request);
		Assert.notNull(request.getWork());
		if (!request.getWork().isSetResources()) {
			return;
		}
		saveWorkResources(request, workService.findWork(workId));
	}

	void saveWorkResources(final WorkSaveRequest request, final AbstractWork work) throws Exception {
		Assert.notNull(request);
		Assert.notNull(request.getWork());
		Assert.notNull(work);
		if (!request.getWork().isSetResources()) {
			return;
		}

		Set<Long> userIdsToAdd = Sets.newHashSetWithExpectedSize(request.getWork().getResourcesSize());
		for (Resource resource : request.getWork().getResources()) {
			userIdsToAdd.add(resource.getId());
		}

		workRoutingService.addToWorkResources((Work)work, userIdsToAdd, new WorkResourceAddOptions(true), request.isAssignToFirstToAccept());
	}

	@Override
	public WorkActionResponse voidWork(VoidWorkRequest voidRequest) throws WorkActionException {

		try {
			WorkActionRequest workAction = new WorkActionRequest();
			WorkActionRequest voidRequestAction = voidRequest.getWorkAction();
			com.workmarket.domains.model.User onBehalfOfUser = userDAO.findUserByUserNumber(voidRequestAction.getOnBehalfOfUserNumber(), false);
			authenticationService.setCurrentUser(onBehalfOfUser);
			Long masqueradeUser = userDAO.findUserId(voidRequestAction.getMasqueradeUserNumber());
			if (masqueradeUser != null) {
				authenticationService.startMasquerade(masqueradeUser, onBehalfOfUser.getId());
				workAction.setMasqueradeId(masqueradeUser);
			} else {
				authenticationService.setMasqueradeUser(null);
			}
			Work work = workService.findWorkByWorkNumber(voidRequestAction.getWorkNumber());
			workAction.setWorkId(work.getId());
			//TODO: do we need to set this to the buyer?
			workAction.setAuditType(WorkAuditType.VOID);
			workAction.setModifierId(onBehalfOfUser.getId());
			workService.voidWork(work.getId(), null);
			//TODO: more stuff

			return new WorkActionResponse().setResponseCode(WorkActionResponseCodeType.SUCCESS);
		} catch (Exception e) {
			logger.error(e);
			throw new WorkActionException(e);
		}
	}

	@Override
	public WorkActionResponse askQuestion(WorkQuestionRequest questionRequest) throws WorkActionException {

		try {
			Long questionAsker = userDAO.findUserId(questionRequest.getWorkAction().getResourceUserNumber());
			Long onBehalfOf = userDAO.findUserId(questionRequest.getWorkAction().getOnBehalfOfUserNumber());
			Long masqueradeUser = userDAO.findUserId(questionRequest.getWorkAction().getMasqueradeUserNumber());
			authenticationService.setCurrentUser(questionAsker);
			if (masqueradeUser != null) {
				authenticationService.startMasquerade(masqueradeUser, questionAsker);
			} else {
				authenticationService.setMasqueradeUser(null);
			}
			Work work = workService.findWorkByWorkNumber(questionRequest.getWorkAction().getWorkNumber());
			if (work == null) {
				throw new WorkActionException("Work not found for question attempt ");
			}
			workQuestionService.saveQuestion(work.getId(), questionAsker, questionRequest.getQuestion(), onBehalfOf);
			return new WorkActionResponse().setResponseCode(WorkActionResponseCodeType.SUCCESS);
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException();
		}
	}

	@Override
	public WorkActionResponse noteWorkResource(ResourceNoteRequest request)
		throws WorkActionException {
		try {
			logger.info(request);
			workResourceChangeLogService.resourceNoteSuccess(request);
		} catch (InvalidParameterException e) {
			throw new WorkActionException("work resource change service threw an error", request);
		}
		return new WorkActionResponse().setResponseCode(WorkActionResponseCodeType.SUCCESS);
	}

	@Override
	public WorkActionResponse declineWorkOnBehalf(DeclineWorkOfferRequest request)
		throws WorkActionException {

		Assert.notNull(request.getWorkAction());
		Assert.notNull(request.getWorkAction().getOnBehalfOfUserNumber());

		com.workmarket.domains.model.User masqueradeUser = null;
		com.workmarket.domains.model.User workerUser = userDAO.findUserByUserNumber(request.getWorkAction().getResourceUserNumber(), true);
		com.workmarket.domains.model.User onBehalfOfUser;

		if (request.getWorkAction().isSetMasqueradeUserNumber()) {
			masqueradeUser = userDAO.findUserByUserNumber(request.getWorkAction().getMasqueradeUserNumber(), false);
			authenticationService.startMasquerade(masqueradeUser.getId(), workerUser.getId());
		} else {
			authenticationService.setMasqueradeUser(null);
		}

		authenticationService.setCurrentUser(workerUser.getId());
		Work work;
		try {
			work = workService.findWorkByWorkNumber(request.getWorkAction().getWorkNumber());
		} catch (Exception e) {
			logger.error("Couldn't find work from decline work offer request: " + request, e);
			throw new WorkActionException("Couldn't find work from decline work offer request: " + request);
		}

		onBehalfOfUser = userDAO.findUserByUserNumber(request.getWorkAction().getOnBehalfOfUserNumber(), false);
		try {
			workService.declineWork(workerUser.getId(), work.getId(), onBehalfOfUser != null ? onBehalfOfUser.getId() : null);
			if (onBehalfOfUser != null) {
				//Return the current user to be the user logged in
				authenticationService.setCurrentUser(onBehalfOfUser.getId());
			}

			workResourceChangeLogService.declineWorkSuccess(request, workerUser, onBehalfOfUser, work, masqueradeUser);
		} catch (Exception e) {
			logger.error("Decline work failed: " + request, e);
			throw new WorkActionException("Decline work failed: " + request);
		}
		return new WorkActionResponse().setResponseCode(WorkActionResponseCodeType.SUCCESS);
	}

	@Override
	public WorkActionResponse rescheduleWork(RescheduleRequest request) throws WorkActionException, ValidationException {

		WorkRequest tWorkRequest = new WorkRequest();
		tWorkRequest.setWorkNumber(request.getWorkAction().getWorkNumber());

		WorkResponse tFindWorkResponse;
		long buyerId;
		try {
			buyerId = workService.getBuyerIdByWorkNumber(request.getWorkAction().getWorkNumber());
		} catch (DuplicateWorkNumberException e1) {
			logger.error("Couldn't find buyerId.", e1);
			throw new WorkActionException("Couldn't find the buyer ID for work number " + request);
		}
		try {
			tWorkRequest.setUserId(buyerId);
			long workId = workService.findWorkId(tWorkRequest.getWorkNumber());
			tWorkRequest.setIncludes(Sets.newHashSet(WorkRequestInfo.getWorkDetailInfoEnumSet()));
			tFindWorkResponse = responseBuilder.buildWorkResponse(workId, buyerId, tWorkRequest.getIncludes());
		} catch (Exception e) {
			logger.error("Couldn't find the work.", e);
			throw new WorkActionException("Couldn't find the work " + request);
		}

		com.workmarket.thrift.work.Work workToSave = tFindWorkResponse.getWork();
		workToSave.setSchedule(createSchedule(request, workToSave.getTimeZone()));
		WorkSaveRequest workSaveRequest = new WorkSaveRequest().setWork(workToSave).setUserId(authenticationService.getCurrentUser().getId());

		workSaveRequest.setOnBehalfOfUserNumber(request.getWorkAction().getOnBehalfOfUserNumber());
		workSaveRequest.setUserId(buyerId);
		try {
			saveOrUpdateWork(workSaveRequest);
		} catch (RuntimeException e) {
			String error = "Work save request failed for unknown reason. " + workSaveRequest + " Reschedule request: " + request;
			logger.error(error, e);
			return new WorkActionResponse(WorkActionResponseCodeType.GENERAL_ERROR);
		}

		return new WorkActionResponse(WorkActionResponseCodeType.SUCCESS);
	}

	private Schedule createSchedule(RescheduleRequest request, String timeZone) {
		Schedule schedule = new Schedule();
		long from = request.getAssignmentTimeRange().getFrom();
		int offset = java.util.TimeZone.getTimeZone(timeZone).getOffset(from);

		schedule.setFrom(from - offset);
		schedule.setRange(request.getAssignmentTimeRange().isSetTo());
		if (schedule.isRange()) {
			long to = request.getAssignmentTimeRange().getTo();
			schedule.setThrough(to - offset);
		}
		return schedule;
	}

	@Override
	public WorkActionResponse resendAllAssignments(WorkActionRequest request) throws WorkActionException {
		logger.info("Resending All Assignments. " + request);

		try {
			Response response = workService.resendInvitationsAsync(request.getWorkNumber());
			if (!response.isSuccessful()) {
				return new WorkActionResponse()
					.setMessage("Work must be in SENT status")
					.setResponseCode(WorkActionResponseCodeType.INVALID_WORK_STATE);
			}

		} catch (DuplicateWorkNumberException e) {
			logger.error("Duplicate work number exception while resending. " + request, e);
			throw new WorkActionException(request);
		} catch (Exception e) {
			logger.error("Unknown error while resending. " + request, e);
			throw new WorkActionException(request);
		}
		return new WorkActionResponse().setResponseCode(WorkActionResponseCodeType.SUCCESS);
	}

	@Override
	public WorkActionResponse sendWork(WorkSendRequest request) throws WorkActionException {
		Assert.notNull(request);
		Assert.isTrue(request.isGroupSendRequest() || request.isAutoSendRequest());

		long workId = abstractWorkDAO.findWorkId(request.getWorkNumber());
		if (request.isGroupSendRequest()) {
			routingStrategyService.addGroupIdsRoutingStrategy(workId, Sets.newHashSet(request.getGroupIds()), 0, request.isAssignToFirstToAccept());
		}
		else if (request.isAutoSendRequest()) {
			routingStrategyService.addAutoRoutingStrategy(workId, request.isAssignToFirstToAccept());
		}
		return new WorkActionResponse().setResponseCode(WorkActionResponseCodeType.SUCCESS);
	}

	@Override
	public WorkActionResponse sendMultipleWork(MultipleWorkSendRequest request) throws WorkActionException {
		// TODO: Alex - make bulk routing async all the way through - move this loop to the workers
		for (WorkSendRequest r : request.getRequests()) {
			sendWork(r);
		}
		return WorkActionResponse.success();
	}

	@Override
	public void startUploadEventHelper(WorkUploadRequest uploadRequest, Long userId) {
		WorkUploadResponse uploadResponse;
		authenticationService.setCurrentUser(userId);

		try {
			uploadResponse = uploader.uploadWork(uploadRequest);

			final BindingResult bindingResult = ThriftValidationMessageHelper.newBindingResult();

			if (uploadResponse.getErrorUploads() != null) {
				for (final WorkUpload u : uploadResponse.getErrorUploads()) {
					for (final WorkUploadError e : u.getErrors()) {
						ThriftValidationMessageHelper.rejectViolation(e.getViolation(), bindingResult);

						// NOTE We're explicitly updating the value for later presentation.

						final ObjectError lastError = CollectionUtilities.last(bindingResult.getAllErrors());
						e.getViolation().setWhy(messageHelper.getMessage(lastError));
					}
				}
			}

			if (uploadResponse.getErrorUploadsSize() > 0) {
				notificationDispatcher.dispatchNotification(notificationTemplateFactory.buildBulkUploadFailedNotificationTemplate(userId, uploadResponse.getErrorUploads()));
				String uploadProgressKey = RedisFilters.userBulkUploadProgressKey(authenticationService.getCurrentUserId());
				redisAdapter.set(uploadProgressKey, String.valueOf(1), (long) DateTimeConstants.SECONDS_PER_DAY);
			}
		} catch (WorkUploadInvalidFileTypeException e) {
			logger.error("Wrong file type received for bulk upload request.");
		} catch (WorkUploadRowLimitExceededException e) {
			logger.error("Row limit exceeded for bulk upload request");
		} catch (WorkUploadException e) {
			logger.error("Error processing bulk upload request");
		} catch (HostServiceException e) {
			logger.error("Error connecting to host during bulk upload request");
		} catch (Exception e) {
			logger.error("Error processing bulk upload request");
		}
	}

	// Bulk save code starts here:
	@Override
	public void uploadWorkAsync(List<WorkSaveRequest> requests) throws ValidationException {
		String uploadKey = RedisFilters.userBulkUploadKey(authenticationService.getCurrentUserId(), System.currentTimeMillis());
		String uploadSizeKey = RedisFilters.userBulkUploadSizeKey(authenticationService.getCurrentUserId(), System.currentTimeMillis());
		// As long as the upload doesn't take more than 24 hours the process will work.
		redisAdapter.set(uploadSizeKey, String.valueOf(requests.size()), (long) DateTimeConstants.SECONDS_PER_DAY);
		// Create events to asynchronously save assignments
		final BulkWorkUploadEvent event = eventFactory.buildBulkWorkUploadEvent(requests, uploadKey, uploadSizeKey);
		eventRouter.sendEvent(event);
	}

	@Override
	public Set<WorkAuthorizationResponse> validateWorkForRouting(long workId) {
		Work work = workService.findWork(workId);
		Assert.notNull(work);
		return workRoutingValidator.validateWorkForRouting(work);
	}

	// currently only supports one label
	private void saveLabels(WorkSaveRequest request, Long workId) {
		if (request.isSetLabelId()) {
			workSubStatusService.addSubStatus(workId, request.getLabelId(), null);
		}
	}

	@Override
	public void saveFollowers(WorkSaveRequest request, long workId) {
		workFollowService.saveFollowers(workId, request.getWork().getFollowers(), true);
	}

	private void saveQualifications(final WorkSaveRequest request, final Long workId) {
		if (featureEvaluator.hasGlobalFeature(QUALIFICATION_FEATURE)) {
			final Qualification qualification = addJobTitleToQualificationService(request.getWork().getDesiredSkills());
			if (qualification.getUuid() != null) {
				qualificationAssociationService.setWorkQualifications(
					workId, request.getUserId(), QualificationType.job_title, Lists.newArrayList(qualification));
			}
		}
	}

	private Qualification addJobTitleToQualificationService(final String desiredSkills) {
		final int jobTitlePrefixPosition = StringUtils.lastIndexOf(desiredSkills, "--");
		final QualificationBuilder builder = Qualification.builder();
		if (jobTitlePrefixPosition > -1) {
			final String jobTitle = desiredSkills.substring(jobTitlePrefixPosition + 2, desiredSkills.length());
			final RequestContext context = webRequestContextProvider.getRequestContext();
			builder
				.setQualificationType(QualificationType.job_title)
				.setName(jobTitle)
				.setIsApproved(Boolean.FALSE);
			final Qualification qualificationRequest = builder.build();

			qualificationClient.createQualification(qualificationRequest, context)
				.subscribe(
					new Action1<MutateResponse>() {
						@Override
						public void call(MutateResponse mutateResponse) {
							// TODO [lu]: once the user_to_qualification table is ready, store successful uuid to the table
							if (mutateResponse.isSuccess()) {
								builder.setUuid(mutateResponse.getUuid());
							} else {
								logger.warn("failed to create job title: " + mutateResponse.getMessage() + ", " + context.toString());
							}
						}
					},
					new Action1<Throwable>() {
						@Override
						public void call(Throwable throwable) {
							logger.error("Failed to create job title at qualification service, " + context.toString(), throwable);
						}
					});
		}
		return builder.build();
	}
}
