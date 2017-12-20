package com.workmarket.api.v2.worker.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.exceptions.ApiException;
import com.workmarket.api.exceptions.GenericApiException;
import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.worker.ex.WorkInvalidException;
import com.workmarket.api.v2.worker.marshaller.AssignmentMarshaller;
import com.workmarket.api.v2.worker.model.AbandonAssignmentDTO;
import com.workmarket.api.v2.worker.model.AddDeliverableDTO;
import com.workmarket.api.v2.worker.model.AddLabelDTO;
import com.workmarket.api.v2.worker.model.AddMessageDTO;
import com.workmarket.api.v2.worker.model.CheckInDTO;
import com.workmarket.api.v2.worker.model.CheckOutDTO;
import com.workmarket.api.v2.worker.model.CompleteDTO;
import com.workmarket.api.v2.worker.model.QuestionDTO;
import com.workmarket.api.v2.worker.model.RescheduleDTO;
import com.workmarket.api.v2.worker.model.SaveCustomFieldsDTO;
import com.workmarket.api.v2.worker.model.UpdateAssetDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.compliance.service.ComplianceService;
import com.workmarket.domains.model.AbstractEntityUtilities;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.model.note.WorkMessagePagination;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.AvoidScheduleConflictsModelRope;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkMessageService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkQuestionService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.AssetService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.CustomFieldGroupSaveRequest;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.SerializationUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.forms.assignments.AddLabelForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.ValidationMessageHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.CustomFieldGroupSaveRequestValidator;
import com.workmarket.web.validators.DateRangeValidator;
import com.workmarket.web.validators.DeliverableValidator;
import com.workmarket.web.validators.FilenameValidator;
import groovy.lang.Tuple2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.MapBindingResult;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.CoreMatchers.equalTo;

@Component
public class XAssignment {

	private static final Log logger = LogFactory.getLog(XAssignment.class);

	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private WorkService workService;
	@Autowired private XWork xWork;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private ComplianceService complianceService;
	@Autowired private ProfileService profileService;
	@Autowired private PricingService pricingService;
	@Autowired private WorkSearchService workSearchService;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private WorkQuestionService workQuestionService;
	@Autowired private WorkMessageService workMessageService;
	@Autowired private UserService userService;
	@Autowired private DeliverableService deliverableService;
	@Autowired private FilenameValidator filenameValidator;
	@Autowired private DeliverableValidator deliverableValidator;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private AssignmentMarshaller assignmentMarshaller;
	@Autowired private CustomFieldService customFieldService;
	@Autowired private CustomFieldGroupSaveRequestValidator customFieldGroupValidator;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private DateRangeValidator dateRangeValidator;
	@Autowired private AssetService assetService;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Qualifier("avoidScheduleConflictsModelDoorman")
	@Autowired
	private Doorman doorman;

	private void throwMessageSourceApiException(String message) throws ApiException {
		throw new MessageSourceApiException(message);
	}

	private void throwMessageSourceApiException(String message, Object... arguments) throws ApiException {
		throw new MessageSourceApiException(message, arguments);
	}

	private void throwGenericApiException(String message) throws ApiException {
		throw new GenericApiException(message);
	}

	private List emptyResults() {
		return new ArrayList<>();
	}

	public List cancelApplication(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber
	) throws ApiException {
		final AbstractWork work = xWork.getWorkByNumber(workNumber);
		try {
			workNegotiationService.cancelPendingNegotiationsByUserForWork(
				extendedUserDetails.getId(),
				work.getId()
			);
		} catch (Exception e) {
			throwMessageSourceApiException("Exception in cancelling negotiation", e);
		}

		return emptyResults();
	}

	public AcceptWorkResponse accept(
		final Long userId,
		final String workNumber
	) throws ApiException {
		final AbstractWork work = xWork.getWorkByNumber(workNumber);
		final Long workId = work.getId();

		// These checks needs to be moved to tWorkFacadeService.acceptWork
		if (hasWorkAlreadyBeenAccepted(workId)) {
			throwMessageSourceApiException("assignment.accept.closed");
		}

		if (isUserActingOnOwnWork(userId, workId)) {
			throwMessageSourceApiException("assignment.accept.own");
		}

		if (!isUserCompliant(userId, workId)) {
			throwMessageSourceApiException("assignment.compliance.user_accept_not_allowed");
		}

		List<AbstractWork> conflicts = Lists.newArrayList();
		doorman.welcome(
			new UserGuest(work.getBuyer()),
			new AvoidScheduleConflictsModelRope(
				workResourceDAO,
				workService,
				work,
				userId,
				conflicts
			)
		);
		if (!conflicts.isEmpty()) {
			throwMessageSourceApiException("assignment.accept.user_has_conflicts");
		}

		final AcceptWorkResponse acceptWorkResponse = tWorkFacadeService.acceptWork(userId, workId);

		if (!acceptWorkResponse.isSuccessful()) {
			throwGenericApiException("Failed to accept work");
		}

		return acceptWorkResponse;
	}

	public boolean decline(final Long userId, final String workNumber) throws ApiException {

		final AbstractWork work = xWork.getWorkByNumber(workNumber);

		final Long workId = work.getId();

		if (isUserActingOnOwnWork(userId, workId)) {
			throwMessageSourceApiException("assignment.decline.not_owner");
		}

		if (!work.isSent()) {
			throwMessageSourceApiException("assignment.decline.invalid_status");
		}

		workService.declineWork(userId, work.getId());

		return true;
	}

	public List confirm(final ExtendedUserDetails extendedUserDetails, final String workNumber) throws ApiException {

		final WorkResponse workResponse = xWork.getWork(
			extendedUserDetails,
			workNumber,
			ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO
			),
			ImmutableSet.of(
				AuthorizationContext.ADMIN,
				AuthorizationContext.ACTIVE_RESOURCE
			)
		);

		final Work work = workResponse.getWork();
		final Long workId = work.getId();

		if (work.getActiveResource() == null) {
			throwMessageSourceApiException("assignment.confirmation.exception");
		}

		workService.confirmWorkResource(work.getActiveResource().getUser().getId(), workId);

		return emptyResults();
	}

	public List abandonAssignment(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final AbandonAssignmentDTO dto
	) throws ApiException {

		final WorkResponse workResponse = xWork.getWork(
			extendedUserDetails,
			workNumber,
			ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO
			),
			ImmutableSet.of(
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.DISPATCHER
			)
		);

		final String message = dto.getMessage();
		final Work work = workResponse.getWork();
		final Long workId = work.getId();

		if (work.getActiveResource() == null) {
			throwMessageSourceApiException("assignment.abandon_work.mobilenotauthorized");
		}

		workService.abandonWork(work.getActiveResource().getUser().getId(), workId, message);

		return emptyResults();
	}


	public TimeTrackingResponse checkIn(
		final Long userId,
		final String workNumber,
		final Long timeTrackingId,
		final CheckInDTO checkInDTO
	) throws ApiException {

		final Long workId = xWork.getWorkId(workNumber);

		final TimeTrackingRequest request = new TimeTrackingRequest();

		if (timeTrackingId != null) {
			request.setTimeTrackingId(timeTrackingId);
		}
		request.setWorkId(workId);
		request.setDate(Calendar.getInstance());
		if (checkInDTO != null) {
			request.setLatitude(checkInDTO.getLatitude());
			request.setLongitude(checkInDTO.getLongitude());
		}

		final TimeTrackingResponse response = tWorkFacadeService.checkInActiveResource(request);

		if (!response.isSuccessful()) {
			if (response.getMessage() != null) {
				throwGenericApiException(response.getMessage());
			} else {
				throwMessageSourceApiException("generic.error");
			}
		}

		return response;
	}

	public TimeTrackingResponse checkOut(
		final Long userId,
		final String workNumber,
		final Long timeTrackingId,
		final CheckOutDTO checkOutDTO
	) throws ApiException {

		final Long workId = xWork.getWorkId(workNumber);

		final TimeTrackingRequest request = new TimeTrackingRequest();

		if (timeTrackingId != null) {
			request.setTimeTrackingId(timeTrackingId);
		}
		request.setWorkId(workId);
		request.setDate(Calendar.getInstance());
		if (checkOutDTO != null) {
			request.setNoteOnCheckOut(checkOutDTO.getNoteText());
			request.setLatitude(checkOutDTO.getLatitude());
			request.setLongitude(checkOutDTO.getLongitude());
		}

		final TimeTrackingResponse response = tWorkFacadeService.checkOutActiveResource(request);

		if (!response.isSuccessful()) {
			if (response.getMessage() != null) {
				throwGenericApiException(response.getMessage());
			} else {
				throwMessageSourceApiException("generic.error");
			}
		}

		return response;
	}

	public List complete(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		CompleteDTO completeDTO,
		final Long onBehalfOf,
		BindingResult bindingResult
	) throws ApiException, BindException {
		final Company company = profileService.findCompanyById(extendedUserDetails.getCompanyId());

		if (company.isSuspended()) {
			throwMessageSourceApiException("assignment.complete.suspended");
		}

		final WorkResponse workResponse = xWork.getWork(
			extendedUserDetails,
			workNumber,
			ImmutableSet.of(
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.BUYER_INFO,
				WorkRequestInfo.ASSETS_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO,
				WorkRequestInfo.ACTIVE_RESOURCE_INFO
			),
			(onBehalfOf != null) ? ImmutableSet.of(AuthorizationContext.ADMIN,
				AuthorizationContext.BUYER) : ImmutableSet.of(AuthorizationContext.ACTIVE_RESOURCE));

		final Work work = workResponse.getWork();

		if (!work.getStatus().getCode().equals(WorkStatusType.ACTIVE)) {
			throwMessageSourceApiException("assignment.complete.notinprogress");
		}

		// Validate pricing
		final PricingStrategy pricingStrategy = pricingService.findPricingStrategyById(work.getPricing().getId());

		if (pricingStrategy instanceof PerHourPricingStrategy || pricingStrategy instanceof BlendedPerHourPricingStrategy) {
			final Integer minutes = completeDTO.getOverrideMinutesWorked();

			if (minutes == null) {
				throwMessageSourceApiException("overtime_minutes_worked_required");
			}

			if (minutes == 0) {
				throwMessageSourceApiException("assignment.complete.notime");
			}
		} else if (pricingStrategy instanceof PerUnitPricingStrategy) {
			final Integer units = completeDTO.getUnits();

			if (units == null) {
				throwMessageSourceApiException("units_processed_required");
			}
		}

		// Validate additional expense override
		if (completeDTO.getAdditionalExpenses() != null && completeDTO.getAdditionalExpenses() > work.getPricing().getAdditionalExpenses()) {
			throwMessageSourceApiException("additional_expenses_exceeded", new Object[]{work.getPricing().getAdditionalExpenses()});
		}

		// Validate existence of required custom fields
		if (work.getCustomFieldGroupsSize() > 0) {
			for (final CustomFieldGroup customFieldGroup : work.getCustomFieldGroups()) {
				if (!customFieldGroup.hasFields()) {
					continue;
				}

				for (final CustomField field : customFieldGroup.getFields()) {

					// if buyer validate all required fields, if worker only validate worker required fields
					if (field.isIsRequired() && StringUtils.isEmpty(field.getValue()) && (
						field.getType().equals(WorkCustomFieldType.RESOURCE) ||
							!extendedUserDetails.getId().equals(work.getActiveResource().getUser().getId())
					)
						) {
						// TODO: All custom field validation errors should be collected - kenmc
						throwMessageSourceApiException("assignment.complete.specific_custom_fields_missing", new Object[]{field.getName()});
					}
				}
			}
		}

		final CompleteWorkDTO completeWork = new CompleteWorkDTO();

		completeWork.setResolution(completeDTO.getResolution());
		completeWork.setAdditionalExpenses(completeDTO.getAdditionalExpenses());
		completeWork.setBonus(work.getPricing().getBonus());
		completeWork.setSalesTaxCollectedFlag(true); /* always true now, only thing that changes is % */
		completeWork.setSalesTaxRate(NumberUtilities.defaultValue(completeDTO.getTaxPercent(), 0D));

		if (pricingStrategy instanceof FlatPricePricingStrategy || pricingStrategy instanceof InternalPricingStrategy) {
			completeWork.setOverridePrice(completeDTO.getOverridePrice());
		} else if (pricingStrategy instanceof PerHourPricingStrategy || pricingStrategy instanceof BlendedPerHourPricingStrategy) {
			completeWork.setOverridePrice(completeDTO.getOverridePrice());
			completeWork.setHoursWorked(
				DateUtilities.getDecimalHours(
					0,
					completeDTO.getOverrideMinutesWorked(),
					Constants.PRICING_STRATEGY_ROUND_SCALE
				)
			);
		} else if (pricingStrategy instanceof PerUnitPricingStrategy) {
			completeWork.setOverridePrice(completeDTO.getOverridePrice());
			completeWork.setUnitsProcessed(completeDTO.getUnits().doubleValue());
		}

		final List<ConstraintViolation> completionResult = workService.completeWork(work.getId(), onBehalfOf, completeWork);

		if (!completionResult.isEmpty()) {
			bindingResult = ValidationMessageHelper.newBindingResult();

			for (final ConstraintViolation v : completionResult) {
				ValidationMessageHelper.rejectViolation(v, bindingResult);
			}

			//messageHelper.setErrors(bundle,bindingResult);

			throw new BindException(bindingResult);
		}

		workSearchService.reindexWorkAsynchronous(work.getId());

		return emptyResults();
	}

	public List reschedule(final String workNumber, final RescheduleDTO rescheduleDTO) throws ApiException {
		final AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			throw new WorkInvalidException();
		}

		final String tz = work.getTimeZone().getTimeZoneId();
		final DateRange dateRange = getDateRange(tz, rescheduleDTO);
		final List<String> errors = validateRescheduleDateRange(dateRange);
		if (CollectionUtils.isNotEmpty(errors)) {
			throw new GenericApiException(Joiner.on("; ").join(errors));
		}

		Tuple2<ImmutableList<String>, String> result = workNegotiationService.reschedule(
			work.getId(),
			dateRange,
			rescheduleDTO.getNote()
		);

		if (CollectionUtils.isNotEmpty(result.getFirst())) {
			throw new GenericApiException(Joiner.on("; ").join(result.getFirst()));
		} else if (StringUtils.isNotBlank(result.getSecond())) {
			return ImmutableList.of(result.getSecond());
		}

		return ImmutableList.of();
	}

	private List<String> validateRescheduleDateRange(final DateRange dateRange) {
		final BindingResult dateBindingResult = new DataBinder(dateRange, "assignment.reschedule").getBindingResult();
		dateRangeValidator.validate(dateRange, dateBindingResult);
		return messageHelper.getAllFieldErrors(dateBindingResult);
	}

	private DateRange getDateRange(final String workTimeZoneId, final RescheduleDTO rescheduleDTO) {
		final Long start = rescheduleDTO.getStart();
		final Long startWindowBegin = rescheduleDTO.getStartWindowBegin();
		final Long startWindowEnd = rescheduleDTO.getStartWindowEnd();

		if (start != null) {
			final Calendar from = DateUtilities.getCalendarFromMillis(start, workTimeZoneId);
			return new DateRange(from, null);
		} else if (startWindowBegin != null && startWindowEnd != null) {
			final Calendar from = DateUtilities.getCalendarFromMillis(startWindowBegin, workTimeZoneId);
			final Calendar to = DateUtilities.getCalendarFromMillis(startWindowEnd, workTimeZoneId);
			return new DateRange(from, to);
		}

		throw new MessageSourceApiException("assignment.reschedule.exception");
	}

	public List<Map<String, Object>> getQuestions(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final Integer page,
		final Integer pageSize
	) {
		final WorkResponse workResponse = xWork.getWork(
			extendedUserDetails,
			workNumber,
			ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SUPPORT_CONTACT_INFO,
				WorkRequestInfo.SCHEDULE_INFO
			),
			ImmutableSet.of(
				AuthorizationContext.RESOURCE,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			)
		);

		final Work work = workResponse.getWork();

		List<Map<String, Object>> workQuestionAnswerPairs = maskQuestions(
			workQuestionService.findQuestionAnswerPairs(
				work.getId(),
				extendedUserDetails.getId(),
				extendedUserDetails.getCompanyId()
			)
		);

		return workQuestionAnswerPairs;
	}

	public WorkQuestionAnswerPair askQuestion(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final QuestionDTO questionDTO) {

		final WorkResponse workResponse = xWork.getWork(
			extendedUserDetails,
			workNumber,
			ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.SUPPORT_CONTACT_INFO,
				WorkRequestInfo.SCHEDULE_INFO
			),
			ImmutableSet.of(
				AuthorizationContext.RESOURCE,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			)
		);

		final Work work = workResponse.getWork();

		final WorkQuestionAnswerPair workQuestionAnswerPair = workQuestionService.saveQuestion(
			work.getId(),
			extendedUserDetails.getId(),
			questionDTO.getText()
		);

		return workQuestionAnswerPair;
	}

	public NotePagination getMessages(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final Integer page,
		final Integer pageSize
	) throws ApiException {
		final Long workId = xWork.getWorkId(workNumber);

		// The underlying code does not yet support pagination, just filtering and sorting...
		final WorkMessagePagination messagePagination = new WorkMessagePagination();

		final NotePagination notePagination = workMessageService.findAllMessagesByWork(workNumber, messagePagination);

		return notePagination;
	}

	public Note addMessage(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final AddMessageDTO addMessageDTO
	) throws ApiException {

		final Long workId = xWork.getWorkId(workNumber);

		final NoteDTO noteDTO = new NoteDTO();
		noteDTO.setContent(addMessageDTO.getMessage());

		final Note note = workMessageService.addWorkMessage(workId, noteDTO);

		return note;
	}

	public List addDeliverable(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final Long deliverableRequirementId,
		final Long deliverableId,
		final AddDeliverableDTO addDeliverableDTO
	) throws ApiException, BindException, java.io.IOException {

		final MessageBundle bundle = messageHelper.newBundle();

		final String description = StringUtils.isEmpty(addDeliverableDTO.getDescription()) ?
			"Assignment Attachment" : addDeliverableDTO.getDescription(); // TODO API - handle default in builder?

		final String fileName = addDeliverableDTO.getName();

		final BindingResult bindingResult = getFilenameErrors(fileName);
		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		final File tempFile = File.createTempFile("api_work_attachment_" + workNumber, ".dat");

		final int bytesCopied = SerializationUtilities.decodeBase64File(addDeliverableDTO.getData(), tempFile);

		if (bytesCopied > Constants.MAX_UPLOAD_SIZE) {
			throwMessageSourceApiException("assignment.add_attachment.sizelimit");
		}

		// guessMimeType can return "" for unknown...
		final String mimeType = MimeTypeUtilities.guessMimeType(addDeliverableDTO.getName());

		deliverableValidator.validate(deliverableRequirementId, mimeType, bundle);

		if (bundle.hasErrors()) {
			throwMessageSourceApiException(bundle.getErrors().get(0));
		}

		try {
			// Called for authorization validation side-effects
			final WorkResponse workResponse = xWork.getWork(
				extendedUserDetails,
				workNumber,
				ImmutableSet.of(
					WorkRequestInfo.CONTEXT_INFO,
					WorkRequestInfo.STATUS_INFO
				),
				ImmutableSet.of(
					AuthorizationContext.BUYER,
					AuthorizationContext.ACTIVE_RESOURCE,
					AuthorizationContext.ADMIN
				)
			);

			final Work work = workResponse.getWork();

			final AssetDTO assetDTO = new AssetDTO();
			Integer position = addDeliverableDTO.getPosition();
			if (deliverableRequirementId != null) {
				position = getNextAvailablePosition(deliverableRequirementId);
			}
			assetDTO.setDeliverableRequirementId(deliverableRequirementId);
			assetDTO.setMimeType(mimeType);
			assetDTO.setName(fileName);
			assetDTO.setDescription(description);
			assetDTO.setPosition(position);
			assetDTO.setAssociationType(com.workmarket.domains.model.asset.type.WorkAssetAssociationType.ATTACHMENT);
			assetDTO.setFileByteSize(bytesCopied);
			assetDTO.setSourceFilePath(tempFile.getAbsolutePath());
			assetDTO.setLargeTransformation(MimeTypeUtilities.isImage(mimeType));

			final InputStream inputStream = new FileInputStream(tempFile);

			if (deliverableId != null) {

				WorkAssetAssociation workAssetAssociationForDeliverableId =
					assetManagementService.findAssetAssociationsByWorkAndAsset(
						work.getId(),
						deliverableId
					);

				if (workAssetAssociationForDeliverableId == null) {
					throw new Exception();
				}

				// set position to what it was before
				assetDTO.setPosition(workAssetAssociationForDeliverableId.getPosition());
			}

			final WorkAssetAssociation workAssetAssociation =
				deliverableService.addDeliverable(
					workNumber,
					inputStream,
					assetDTO
				);

			// Set position to id within work_asset_association table for thread safety
			assetService.setPositionToWorkAssetAssociationId(deliverableRequirementId);
			final Asset asset = workAssetAssociation.getAsset();

			// According to spec, we should _not_ return an array, but we do.
			final List<Object> results = new ArrayList<>();
			results.add(asset);

			return results;
		} catch (final Exception e) {
			logger.error(e.getMessage());
			throwMessageSourceApiException("assignment.add_attachment.exception");
			return emptyResults();
		}
	}

	@VisibleForTesting
	protected Integer getNextAvailablePosition(final Long deliverableRequirementId) {
		final DeliverableRequirement deliverableRequirement =
			deliverableService.findDeliverableRequirementById(deliverableRequirementId);
		final List<Integer> takenPositions =
			assetService.findDeliverableAssetPositionsByDeliverableRequirementId(deliverableRequirementId);

		if (CollectionUtils.isEmpty(takenPositions)) {
			return 0;
		} else if (takenPositions.size() >= deliverableRequirement.getNumberOfFiles()) {
			return takenPositions.size(); // all slots taken; auto-increment to next position
		}

		int i = 0;
		for (; i < takenPositions.size(); i++) {
			if (i < takenPositions.get(i)) {
				return i;
			}
		}

		return i;
	}

	public List deleteDeliverable(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final Long deliverableId
	) {
		logger.debug("deliverableId[" + deliverableId);
		return deleteDeliverable(extendedUserDetails, workNumber, new Function<Long, Void>() {
			@Nullable
			@Override
			public Void apply(@Nullable final Long workId) {
				assetManagementService.removeAssetFromWork(deliverableId, workId);
				return null;
			}
		});
	}

	public List deleteDeliverable(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final String assetUuid
	) {
		logger.debug("assetUuid[" + assetUuid);
		return deleteDeliverable(extendedUserDetails, workNumber, new Function<Long, Void>() {
			@Nullable
			@Override
			public Void apply(@Nullable final Long workId) {
				assetManagementService.removeAssetFromWork(assetUuid, workId);
				return null;
			}
		});
	}

	// Note: The existing assetManagementService silently swallows invalid deliverableIds! kenmc
	public List deleteDeliverable(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final Function<Long, Void> func
	) throws ApiException {

		logger.debug("workNumber[" + workNumber);

		final WorkResponse workResponse = xWork.getWork(
			extendedUserDetails,
			workNumber,
			ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO),
			ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			)
		);

		try {
			func.apply(workResponse.getWork().getId());
		} catch (final Exception e) {
			throwMessageSourceApiException("assignment.remove_attachment.exception");
		}

		return emptyResults();
	}

	public List getCustomFields(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber
	) throws ApiException {

		final WorkResponse workResponse = xWork.getWork(
			extendedUserDetails,
			workNumber,
			ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.COMPANY_INFO,
				WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO
			),
			ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.RESOURCE,
				AuthorizationContext.ADMIN
			)
		);

		List<? extends Map<String, Object>> results = assignmentMarshaller.getDetailsResultToCustomFieldsModel(workResponse);

		if (results == null) {
			results = new ArrayList<Map<String, Object>>();
		}

		return results;
	}

	public List saveCustomFields(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final SaveCustomFieldsDTO saveCustomFieldsDTO,
		final boolean onComplete,
		final BindingResult bindingResult
	) throws ApiException, BindException {

		final MessageBundle bundle = messageHelper.newBundle();
		final WorkResponse workResponse = xWork.getWork(
			extendedUserDetails,
			workNumber,
			ImmutableSet.of(
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.CONTEXT_INFO
			),
			ImmutableSet.of(
				AuthorizationContext.ADMIN,
				AuthorizationContext.ACTIVE_RESOURCE
			)
		);

		final Work workResponseWork = workResponse.getWork();
		final Long workId = workResponseWork.getId();

		final AbstractWork work = workService.findWork(workId);

		// Convert our DTOs into the thrift versions.
		final List<CustomFieldGroup> customFieldGroupSet = new ArrayList<CustomFieldGroup>();

		for (final CustomFieldGroupDTO customFieldGroupDTO : saveCustomFieldsDTO.getCustomFields()) {
			final CustomFieldGroup customFieldGroup = new CustomFieldGroup();

			customFieldGroup.setId(customFieldGroupDTO.getId());

			final List<CustomField> customFields = new ArrayList<>();

			for (final CustomFieldDTO customFieldDTO : customFieldGroupDTO.getFields()) {
				final CustomField customField = new CustomField();
				customField.setId(customFieldDTO.getId());
				customField.setValue(customFieldDTO.getValue());

				customFields.add(customField);
			}

			customFieldGroup.setFields(customFields);

			customFieldGroupSet.add(customFieldGroup);
		}

		final List<CustomFieldGroup> groupSet = customFieldGroupSet;
		final boolean isActiveResource = isActiveResource(workResponse);
		final boolean isAdmin = isAdmin(workResponse);
		final boolean completeAction = onComplete || work.isFinished();
		final boolean isSentAction = work.isSent();

		try {
			for (final CustomFieldGroup customFieldGroup : groupSet) {
				if (customFieldGroup == null) {
					// This can happen if API user used bad indexes
					continue;
				}

				final WorkCustomFieldGroup fieldGroup = customFieldService.findWorkCustomFieldGroup(customFieldGroup.getId());

				final CustomFieldGroupSaveRequest customFieldGroupSaveRequest = new CustomFieldGroupSaveRequest(
					customFieldGroup,
					fieldGroup,
					isActiveResource,
					isAdmin,
					completeAction,
					isSentAction
				);

				customFieldGroupValidator.validate(customFieldGroupSaveRequest, bindingResult);

				if (bindingResult.hasErrors()) {
					messageHelper.setErrors(bundle, bindingResult);

					if (bundle.hasErrors()) {
						//return CollectionUtilities.newObjectMap("successful",false,"errors",bundle.getErrors());
						throw new BindException(messageBundleToBindingResult(bundle));
					}
				}

				final List<WorkCustomFieldDTO> dtos = Lists.newArrayList();

				for (final WorkCustomField field : fieldGroup.getActiveWorkCustomFields()) {
					final long fieldId = field.getId();
					final CustomField submittedField = selectFirst(
						customFieldGroup.getFields(),
						having(on(CustomField.class).getId(), equalTo(fieldId))
					);
					if (submittedField == null || (field.isOwnerType() && isActiveResource)) {
						continue;
					}

					final String submittedValue = submittedField.getValue();

					dtos.add(new WorkCustomFieldDTO(fieldId, submittedValue));
				}

				customFieldService.addWorkCustomFieldGroupToWork(
					customFieldGroup.getId(),
					workResponse.getWork().getId(),
					customFieldGroup.getPosition()
				);

				customFieldService.saveWorkCustomFieldsForWorkAndIndex(
					dtos.toArray(new WorkCustomFieldDTO[dtos.size()]),
					workResponse.getWork().getId()
				);
			}
		} catch (final Exception e) {
			messageHelper.addError(bundle, "assignment.save_custom_fields.failure");
			throw new BindException(messageBundleToBindingResult(bundle));
		}

		return emptyResults();
	}

	public List getLabels(final ExtendedUserDetails extendedUserDetails, final String workNumber) throws ApiException {
		final WorkResponse workResponse = xWork.getWork(
			extendedUserDetails,
			workNumber,
			ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.PARTS_INFO,
				WorkRequestInfo.SCHEDULE_INFO
			),
			ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			)
		);

		final WorkSubStatusTypeFilter filter = new WorkSubStatusTypeFilter();
		filter.setShowSystemSubStatus(true);
		filter.setShowCustomSubStatus(true);
		filter.setShowDeactivated(false);
		filter.setClientVisible(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		filter.setResourceVisible(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));

		final List<WorkSubStatusType> labels = workSubStatusService.findAllEditableSubStatusesByWork(
			workResponse.getWork().getId(),
			filter
		);

		final List<ApiJSONPayloadMap> results = assignmentMarshaller.getDomainModelSubStatuses(labels);

		return results; //labels;
	}

	public List addLabel(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final Long labelId,
		final AddLabelDTO addLabelDTO,
		BindingResult bindingResult
	) throws ApiException {

		final WorkResponse workResponse = xWork.getWork(
			extendedUserDetails,
			workNumber,
			ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.PARTS_INFO,
				WorkRequestInfo.SCHEDULE_INFO
			),
			ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			)
		);

		final RescheduleDTO schedule = addLabelDTO.getSchedule();

		final AddLabelForm form = new AddLabelForm();
		form.setLabel_id(labelId);
		form.setNote(addLabelDTO.getMessage());

		final Work work = workResponse.getWork();

		final MessageBundle bundle = messageHelper.newBundle();

		final WorkSubStatusTypeFilter filter = new WorkSubStatusTypeFilter();
		filter.setShowSystemSubStatus(true);
		filter.setShowCustomSubStatus(true);
		filter.setShowDeactivated(false);
		filter.setClientVisible(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		filter.setResourceVisible(workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));

		final List<WorkSubStatusType> labels = workSubStatusService.findAllEditableSubStatusesByWork(
			workResponse.getWork().getId(),
			filter
		);
		final Map<Long, WorkSubStatusType> labelLookup = AbstractEntityUtilities.newEntityIdMap(labels);

		final WorkSubStatusType label = labelLookup.get(labelId);

		if (label == null) {
			throwMessageSourceApiException("assignment.mobile.add_label.notallowed");
		}

		if (label.isNoteRequired() && addLabelDTO.getMessage() == null) {
			throwMessageSourceApiException("assignment.add_label.note_required");
		}

		com.workmarket.domains.model.DateRange dateRange = null;
		final boolean isScheduleRequired = label.isScheduleRequired();
		if (isScheduleRequired && schedule != null) {
			final String tz = work.getTimeZone();
			dateRange = getDateRange(schedule, tz);
			dateRangeValidator.validate(dateRange, bindingResult);
		}

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			throwMessageSourceApiException("");
		}

		try {
			final List<Long> workIds = java.util.Arrays.asList(work.getId());

			if (dateRange != null) {
				workSubStatusService.addSubStatus(workIds, labelId, form.getNote(), dateRange);
			} else {
				workSubStatusService.addSubStatus(workIds, labelId, form.getNote());
			}

			return emptyResults();
		} catch (final Exception e) {

			messageHelper.addError(bundle, "assignment.mobile.add_label.exception");
			logger.error(e);
			throwMessageSourceApiException(e.getMessage());
			return emptyResults();
		}
	}

	private DateRange getDateRange(final RescheduleDTO schedule, final String timeZone) {
		if (schedule.getStart() != null) {
			final Calendar from = DateUtilities.getCalendarFromMillis(schedule.getStart(), timeZone);
			return new DateRange(from, null);
		} else if (schedule.getStartWindowBegin() != null && schedule.getStartWindowEnd() != null) {
			final Calendar from = DateUtilities.getCalendarFromMillis(schedule.getStartWindowBegin(), timeZone);
			final Calendar to = DateUtilities.getCalendarFromMillis(schedule.getStartWindowEnd(), timeZone);
			return new DateRange(from, to);
		}
		return null;
	}

	public List getAsset(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final String assetUUID
	) throws ApiException {
		return emptyResults();
	}

	public List updateAsset(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		final String assetUUID,
		final UpdateAssetDTO updateAssetDTO,
		final BindingResult bindingResult
	) throws ApiException {
		return emptyResults();
	}

	private boolean isAdmin(final WorkResponse workResponse) {
		return workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);
	}

	private boolean isActiveResource(final WorkResponse workResponse) {
		return workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE);
	}

	private boolean hasWorkAlreadyBeenAccepted(final Long workId) {
		final WorkResourcePagination pagination = new WorkResourcePagination();

		pagination.setResultsLimit(5);
		pagination.setStartRow(0);
		pagination.addFilter(WorkResourcePagination.FILTER_KEYS.WORK_RESOURCE_STATUS, WorkResourceStatusType.ACCEPTED);

		final WorkResourcePagination results = workService.findWorkResources(workId, pagination);

		return (results == null) ? false : results.getResults().size() > 0;
	}

	private boolean isUserActingOnOwnWork(final Long userId, final Long workId) {
		final boolean isResource = workService.isUserWorkResourceForWork(userId, workId);
		final List<WorkContext> context = workService.getWorkContext(workId, userId);

		return !isResource && context.contains(WorkContext.OWNER);
	}

	private boolean isUserCompliant(final Long userId, final Long workId) {
		final Compliance compliance = complianceService.getComplianceFor(userId, workId);
		return compliance.isCompliant();
	}

	protected BindingResult getFilenameErrors(final String fileName) {
		final MapBindingResult bind = new MapBindingResult(com.google.common.collect.Maps.newHashMap(), "fileName");
		filenameValidator.validate(fileName, bind);
		return bind;
	}

	private BindingResult messageBundleToBindingResult(final MessageBundle bundle) {
		final MapBindingResult bind = new MapBindingResult(com.google.common.collect.Maps.newHashMap(), "messageBundle");
		return bind;
	}

	private List<Map<String, Object>> maskQuestions(final List<WorkQuestionAnswerPair> pairs) {
		final List<Map<String, Object>> results = Lists.newArrayListWithExpectedSize(pairs.size());

		for (final WorkQuestionAnswerPair pair : pairs) {
			results.add(maskQuestion(pair));
		}

		return results;
	}

	private Map<String, Object> maskQuestion(final WorkQuestionAnswerPair pair) {
		final List<Object> responses = new ArrayList<>();

		if (pair.getAnsweredOn() != null) {
			responses.add(
				new ImmutableMap.Builder<String, Object>()
					.put("id", pair.getId())
					.put("createdByNumber", Long.valueOf(userService.findUserNumber(pair.getAnswererId())))
					.put("createdDate", pair.getAnsweredOn())
					.put("text", pair.getAnswer())
					.build()
			);
		}

		return new ImmutableMap.Builder<String, Object>()
			.put("id", pair.getId())
			.put("createdByNumber", Long.valueOf(userService.findUserNumber(pair.getQuestionerId())))
			.put("createdDate", pair.getCreatedOn())
			.put("text", pair.getQuestion())
			.put("responses", responses)
			.build();
	}
}
