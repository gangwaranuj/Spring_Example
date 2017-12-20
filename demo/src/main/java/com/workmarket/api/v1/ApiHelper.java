
package com.workmarket.api.v1;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.api.model.ApiPhoneNumberDTO;
import com.workmarket.api.v1.model.*;
import com.workmarket.configuration.Constants;
import com.workmarket.data.report.work.WorkSubStatusTypeReportRow;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.RoleType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.service.WorkQuestionService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.search.request.LocationFilter;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.business.DateTimeService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.search.SearchService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.core.Asset;
import com.workmarket.thrift.core.DeliverableAsset;
import com.workmarket.thrift.core.Note;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.LogEntry;
import com.workmarket.thrift.work.LogEntryType;
import com.workmarket.thrift.work.Negotiation;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.RequestContext;
import com.workmarket.thrift.work.RoutingStrategy;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.SubStatus;
import com.workmarket.thrift.work.SubStatusActionType;
import com.workmarket.thrift.work.TimeTrackingEntry;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

//TODO API - split util functions not requiring autowiring in to an ApiUtilities class
@Component
public class ApiHelper {
	private static final Logger logger = LoggerFactory.getLogger(ApiHelper.class);

	@Autowired private UserService userService;
	@Autowired private PricingService pricingService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private RatingService ratingService;
	@Autowired private WorkService workService;
	@Autowired private WorkQuestionService workQuestionService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private SearchService searchService;
	@Autowired private DateTimeService dateTimeService;
	@Autowired private ProfileService profileService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private WorkRoutingService workRoutingService;
	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired private ProjectService projectService;

	private final Map<String,String> internalStatusMap;
	private final Set<String> validStatuses;
	private final Map<PricingStrategyType,Integer> validPricingStrategies;
	private final Map<String,String> workStatusesForList;

	{
		internalStatusMap = ImmutableMap.of(
			"in_progress", WorkStatusType.INPROGRESS,
			"payment_pending", WorkStatusType.PAYMENT_PENDING
		);

		validStatuses = new ImmutableSet.Builder<String>()
			.add(WorkStatusType.ACTIVE)
			.add(WorkStatusType.COMPLETE)
			.add(WorkStatusType.PAID)
			.add(WorkStatusType.DECLINED)
			.add(WorkStatusType.SENT)
			.add(WorkStatusType.DRAFT)
			.add(WorkStatusType.REFUNDED)
			.add(WorkStatusType.VOID)
			.add(WorkStatusType.CANCELLED)
			.add(WorkStatusType.INPROGRESS)
			.add(WorkStatusType.EXCEPTION)
			.add(WorkStatusType.PAYMENT_PENDING)
			.add(WorkStatusType.AVAILABLE)
			.add(WorkStatusType.SENT_WITH_OPEN_QUESTIONS)
			.add(WorkStatusType.SENT_WITH_OPEN_NEGOTIATIONS)
			.build();

		validPricingStrategies = new ImmutableMap.Builder<PricingStrategyType, Integer>()
			.put(PricingStrategyType.FLAT , PricingStrategyType.FLAT.ordinal() + 1)
			.put(PricingStrategyType.PER_HOUR, PricingStrategyType.PER_HOUR.ordinal() + 1)
			.put(PricingStrategyType.PER_UNIT, PricingStrategyType.PER_UNIT.ordinal() + 1)
			.put(PricingStrategyType.BLENDED_PER_HOUR, PricingStrategyType.BLENDED_PER_HOUR.ordinal() + 1)
			.put(PricingStrategyType.BLENDED_PER_UNIT, PricingStrategyType.BLENDED_PER_UNIT.ordinal() + 1)
			.put(PricingStrategyType.INTERNAL, PricingStrategyType.INTERNAL.ordinal() + 1)
			.build();

		workStatusesForList = Collections.unmodifiableMap(
			CollectionUtilities.newStringMap(
				WorkStatusType.ACTIVE, "Assigned",
				WorkStatusType.CANCELLED, "Cancelled",
				WorkStatusType.COMPLETE, "Complete",
				WorkStatusType.DECLINED, "Declined",
				WorkStatusType.DRAFT, "Draft",
				WorkStatusType.EXCEPTION, "Exception",
				WorkStatusType.INPROGRESS, "In Progress",
				WorkStatusType.PAID, "Paid",
				WorkStatusType.PAYMENT_PENDING, "Invoiced",
				WorkStatusType.REFUNDED, "Refunded",
				WorkStatusType.SENT, "Sent",
				WorkStatusType.VOID, "Voided"
			)
		);
	}

	public boolean hasInternalStatus(String code) {
		return internalStatusMap.containsKey(code);
	}

	public String getInternalStatus(String code) {
		return (internalStatusMap.containsKey(code)) ? internalStatusMap.get(code) : code;
	}

	public boolean hasValidStatus(String status) {
		return validStatuses.contains(status);
	}

	public boolean isValidOwnerId(Long creatorId, Long ownerId) {
		Long creatorCompanyId = profileService.findCompany(creatorId).getId();
		Long ownerCompanyId = profileService.findCompany(ownerId).getId();

		return creatorCompanyId.equals(ownerCompanyId);
	}

	public boolean hasValidPricingStrategy(PricingStrategyType strategyType) {
		return validPricingStrategies.containsKey(strategyType);
	}

	public Integer getPricingStrategyId(PricingStrategyType strategyType) {
		return validPricingStrategies.get(strategyType);
	}

	public Map<String,String> getWorkStatusesForList() {
		return workStatusesForList;
	}

	public String getErrorDescription(final ApiV1Exception e) {
		return "HTTP status " + e.getStatusCode() + ": " + messageHelper.getMessage(
				e.getMessage(), e.getMessageArguments());
	}

	public WorkResponse getWorkResponse(String workNumber, Set<WorkRequestInfo> includes, boolean permitActiveResource) throws
																																																											ApiV1Exception {
		if (null == includes) {
			includes = new HashSet<>();
		}

		includes.add(WorkRequestInfo.CONTEXT_INFO); // required for authentication

		User user = authenticationService.getCurrentUser();

		WorkRequest workRequest = new WorkRequest();
		workRequest.setUserId(user.getId());
		workRequest.setWorkNumber(workNumber);
		workRequest.setIncludes(includes);

		WorkResponse workResponse = null;
		boolean throwException = false;
		try {
			workResponse = tWorkFacadeService.findWork(workRequest);
		} catch (Exception e) {
			throwException = true;
		}

		if (throwException || !checkAccess(workResponse, user.getId(), permitActiveResource)) {
			throw new ApiV1Exception(
					"api.v1.assignments.invalid.authorization", HttpStatus.SC_UNAUTHORIZED, workNumber);
		}

		return workResponse;
	}

	public Work getWork(String workNumber, Set<WorkRequestInfo> includes, boolean permitActiveResource) throws
																																																			ApiV1Exception {
		return getWorkResponse(workNumber, includes, permitActiveResource).getWork();
	}

	public WorkResponse getDetailedWorkResponse(String workNumber, boolean permitActiveResource) throws ApiV1Exception {
		User user = authenticationService.getCurrentUser();
		WorkRequest workRequest = new WorkRequest();
		workRequest.setUserId(user.getId());
		workRequest.setWorkNumber(workNumber);

		WorkResponse workResponse = null;
		boolean throwException = false;
		try {
			workResponse = tWorkFacadeService.findWorkDetail(workRequest);
		} catch (Exception e) {
			throwException = true;
		}

		if (throwException || !checkAccess(workResponse, user.getId(), permitActiveResource)) {
			throw new ApiV1Exception(
					"api.v1.assignments.invalid.authorization", HttpStatus.SC_UNAUTHORIZED, workNumber);
		}

		return workResponse;
	}

	public Work getDetailedWork(String workNumber, boolean permitActiveResource) throws ApiV1Exception {
		return getDetailedWorkResponse(workNumber, permitActiveResource).getWork();
	}

	private boolean checkAccess(WorkResponse workResponse, Long userId, boolean permitActiveResource) {
		Set<String> roles = new HashSet<>(Arrays.asList(authenticationService.getRoles(userId)));

		if (roles.contains(RoleType.INTERNAL)) {
			return true;
		}

		if (workResponse.getRequestContexts().contains(RequestContext.UNRELATED)) {
			return false;
		}

		Set<AuthorizationContext> authorizations = Sets.newHashSet(AuthorizationContext.ADMIN);

		if (permitActiveResource) {
			authorizations.add(AuthorizationContext.ACTIVE_RESOURCE);
		}

		return CollectionUtils.containsAny(workResponse.getAuthorizationContexts(), authorizations);
	}

	/**
	 * Build the assignment out in standard format for display and creation purposes.
	 * @param work Work
	 * @return assignments
	 */
	public ApiAssignmentDetailsDTO buildAssignmentForApi(final Work work) {

		List<ApiAssignmentDetailsLabelDTO> apiAssignmentDetailsLabelDTOs = null;

		if (work.getSubStatusesSize() > 0) {
			apiAssignmentDetailsLabelDTOs = Lists.transform(work.getSubStatuses(), new Function<SubStatus, ApiAssignmentDetailsLabelDTO>() {
				@Override
				public ApiAssignmentDetailsLabelDTO apply(SubStatus subStatus) {
					return new ApiAssignmentDetailsLabelDTO.Builder()
						.withCode(subStatus.getCode())
						.withNote(subStatus.getNote())
						.withDescription(subStatus.getDescription())
						.withUserResolvable(subStatus.isUserResolvable())
					  .withColorRgb(subStatus.getColorRgb())
						.withId(subStatus.getId())
						.withSetId(subStatus.isSetId())
						.withSetDescription(subStatus.isSetDescription())
						.withSetCode(subStatus.isSetCode())
						.withSetColorRgb(subStatus.isSetColorRgb())
						.withSetNote(subStatus.isSetNote())
						.build();
				}
			});
		}

		ApiAssignmentDetailsDTO.Builder apiAssignmentDetailsDTOBuilder = new ApiAssignmentDetailsDTO.Builder();

		apiAssignmentDetailsDTOBuilder.withId(work.getWorkNumber());
		apiAssignmentDetailsDTOBuilder.withTitle(HtmlUtils.htmlEscape(work.getTitle()));
		apiAssignmentDetailsDTOBuilder.withDescription(HtmlUtils.htmlEscape(work.getDescription()));
		apiAssignmentDetailsDTOBuilder.withInstructions(HtmlUtils.htmlEscape(work.getInstructions()));
		apiAssignmentDetailsDTOBuilder.withDesiredSkills(HtmlUtils.htmlEscape(work.getDesiredSkills()));
		apiAssignmentDetailsDTOBuilder.withShortUrl(work.getShortUrl());
		apiAssignmentDetailsDTOBuilder.withStatus(work.isInProgress() ? "in_progress" :  getInternalStatus(work.getStatus().getCode()));
		apiAssignmentDetailsDTOBuilder.withSubstatuses(apiAssignmentDetailsLabelDTOs);
		apiAssignmentDetailsDTOBuilder.withLabels(apiAssignmentDetailsLabelDTOs);
		apiAssignmentDetailsDTOBuilder.withProject(work.getProject() == null ? null : work.getProject().getId());
		apiAssignmentDetailsDTOBuilder.withClient(work.getClientCompany() == null ? null : work.getClientCompany().getId());
		apiAssignmentDetailsDTOBuilder.withInternalOwner(work.getBuyer() == null ? null : work.getBuyer().getUserNumber());
		apiAssignmentDetailsDTOBuilder.withInternalOwnerDetails(work.getBuyer() == null ? null : new ApiInternalOwnerDetailsDTO.Builder()
			.withFirstName(work.getBuyer().getName().getFirstName())
			.withLastName(work.getBuyer().getName().getLastName())
			.build()
		);

		apiAssignmentDetailsDTOBuilder.withSentWorkerCount(workService.findWorkerIdsForWork(work.getId()).size());

		apiAssignmentDetailsDTOBuilder.withAssignmentWindowStart((work.isSetSchedule() && work.getSchedule().isSetFrom()) ? work.getSchedule().getFrom() / 1000L : null);
		apiAssignmentDetailsDTOBuilder.withAssignmentWindowStartDate((work.isSetSchedule() && work.getSchedule().isSetFrom()) ? new Date(work.getSchedule().getFrom()).toString() : null);
		apiAssignmentDetailsDTOBuilder.withAssignmentWindowEnd((work.isSetSchedule() && work.getSchedule().isSetThrough()) ? work.getSchedule().getThrough() / 1000L : null);
		// TODO API - EXISTING BUG! This should use getThrough()
		apiAssignmentDetailsDTOBuilder.withAssignmentWindowEndDate((work.isSetSchedule() && work.getSchedule().isSetFrom()) ? new Date(work.getSchedule().getFrom()).toString() : null);

		if (work.isSetActiveResource() && work.getActiveResource().isSetAppointment()) {
			apiAssignmentDetailsDTOBuilder.withScheduledTime(work.getActiveResource().getAppointment().isSetFrom() ? work.getActiveResource().getAppointment().getFrom() / 1000L : null);
			apiAssignmentDetailsDTOBuilder.withScheduledTimeDate((work.getActiveResource().getAppointment().isSetFrom()) ? new Date(work.getActiveResource().getAppointment().getFrom()).toString() : null);
			// Deprecated
			// Note - previous behavior for setting an appointment overrode the values for the scheduled window; reproducing that behavior.
			apiAssignmentDetailsDTOBuilder.withScheduledStart((work.getActiveResource().getAppointment().isSetFrom()) ? work.getActiveResource().getAppointment().getFrom() / 1000L : null);
			if (work.getActiveResource().getAppointment().isRange()) {
				apiAssignmentDetailsDTOBuilder.withScheduledEnd((work.isSetSchedule() && work.getSchedule().isSetThrough()) ? work.getSchedule().getThrough() / 1000L : null);
			}
			else {
				apiAssignmentDetailsDTOBuilder.withScheduledEnd(null);
			}
		} else {
			// Deprecated
			apiAssignmentDetailsDTOBuilder.withScheduledStart((work.isSetSchedule() && work.getSchedule().isSetFrom()) ? work.getSchedule().getFrom() / 1000L : null);
			apiAssignmentDetailsDTOBuilder.withScheduledEnd((work.isSetSchedule() && work.getSchedule().isSetThrough()) ? work.getSchedule().getThrough() / 1000L : null);
		}

		ApiRescheduleRequestDTO rescheduleRequest = buildRescheduleRequest(work);
		if (rescheduleRequest != null) {
			apiAssignmentDetailsDTOBuilder.withRescheduleRequest(rescheduleRequest);
		}

		ApiBudgetIncreaseRequestDTO budgetIncreaseRequest = buildBudgetIncreaseRequest(work);
		if (budgetIncreaseRequest != null) {
			apiAssignmentDetailsDTOBuilder.withBudgetIncreaseRequest(budgetIncreaseRequest);
		}

		ApiExpenseReimbursementRequestDTO expenseReimbursementRequest = buildExpenseReimbursement(work);
		if (expenseReimbursementRequest!= null) {
			apiAssignmentDetailsDTOBuilder.withExpenseReimbursementRequest(expenseReimbursementRequest);
		}

		ApiBonusRequestDTO bonusRequest = buildBonusRequest(work);
		if (bonusRequest!= null) {
			apiAssignmentDetailsDTOBuilder.withBonusRequest(bonusRequest);
		}

		apiAssignmentDetailsDTOBuilder.withResolution(StringUtilities.defaultString(work.getResolution(), null));
		apiAssignmentDetailsDTOBuilder.withIndustry((work.getIndustry() == null) ? null : work.getIndustry().getName());
		apiAssignmentDetailsDTOBuilder.withTimeZone(StringUtilities.defaultString(work.getTimeZone(), null));

		int totalRequiredAttachments = 0;
		if (work.getDeliverableRequirementGroupDTO() != null && work.getDeliverableRequirementGroupDTO().getDeliverableRequirementDTOs() != null) {
			for (DeliverableRequirementDTO deliverableRequirementDTO : work.getDeliverableRequirementGroupDTO().getDeliverableRequirementDTOs()) {
				totalRequiredAttachments += deliverableRequirementDTO.getNumberOfFiles();
			}
		}
		apiAssignmentDetailsDTOBuilder.withRequiredAttachments(totalRequiredAttachments);

		if (work.isSetLocation()) {
			Optional<GeoPoint> coordinates = Optional.fromNullable(work.getLocation().getAddress().getPoint());

			apiAssignmentDetailsDTOBuilder.withLocation(new ApiLocationDTO.Builder()
				.withId(work.getLocation().getId())
				.withName(StringUtilities.defaultString(work.getLocation().getName(), work.getLocation().getAddress().getAddressLine1()))
				.withLocationNumber(work.getLocation().getNumber())
				.withInstructions(work.getLocation().getInstructions())
				.withAddress1(work.getLocation().getAddress().getAddressLine1())
				.withAddress2(work.getLocation().getAddress().getAddressLine2())
				.withCity(work.getLocation().getAddress().getCity())
				.withState(work.getLocation().getAddress().getState())
				.withZip(work.getLocation().getAddress().getZip())
				.withCountry(work.getLocation().getAddress().getCountry())
				.withLatitude(coordinates.isPresent() ? String.valueOf(coordinates.get().getLatitude()) : null)
				.withLongitude(coordinates.isPresent() ? String.valueOf(coordinates.get().getLongitude()) : null)
				.build()
			);

			apiAssignmentDetailsDTOBuilder.withLocationOffsite(Boolean.FALSE);
		} else {
			apiAssignmentDetailsDTOBuilder.withLocation(null);
			apiAssignmentDetailsDTOBuilder.withLocationOffsite(Boolean.TRUE);
		}

		if (work.isSetLocationContact()) {
			List<ApiPhoneNumberDTO> apiPhoneNumberDTOs = new LinkedList<>();
			if (work.getLocationContact().getProfile().getPhoneNumbers() != null) {
				for (Phone phone : work.getLocationContact().getProfile().getPhoneNumbers()) {
					apiPhoneNumberDTOs.add(new ApiPhoneNumberDTO.Builder()
						.withPhone(StringUtilities.defaultString(phone.getPhone(), null))
						.withExtension(StringUtilities.defaultString(phone.getExtension(), null))
						.withType(ContactContextType.valueOf(phone.getType()))
						.build()
					);
				}
			}

			apiAssignmentDetailsDTOBuilder.withLocationContact(new ApiLocationContactDTO.Builder()
				.withId(work.getLocationContact().getId())
				.withFirstName(StringUtilities.defaultString(work.getLocationContact().getName().getFirstName(), null))
				.withLastName(StringUtilities.defaultString(work.getLocationContact().getName().getLastName(), null))
				.withEmail(StringUtilities.defaultString(work.getLocationContact().getEmail(), null))
				.withPhoneNumbers(apiPhoneNumberDTOs)
				.build()
			);
		} else {
			apiAssignmentDetailsDTOBuilder.withLocationContact(null);
		}

		if (work.isSetSupportContact()) {
			User supportContactUser = userService.findUserById(work.getSupportContact().getId());

			List<ApiPhoneNumberDTO> apiPhoneNumberDTOs = buildUserProfilePhones(supportContactUser.getProfile());

			apiAssignmentDetailsDTOBuilder.withSupportContact(new ApiSupportContactDTO.Builder()
				.withId(StringUtilities.defaultString(work.getSupportContact().getUserNumber(), null))
				.withFirstName(StringUtilities.defaultString(work.getSupportContact().getName().getFirstName(), null))
				.withLastName(StringUtilities.defaultString(work.getSupportContact().getName().getLastName(), null))
				.withEmail(StringUtilities.defaultString(work.getSupportContact().getEmail(), null))
				.withPhoneNumbers(apiPhoneNumberDTOs)
				.build()
			);
		} else {
			apiAssignmentDetailsDTOBuilder.withSupportContact(null);
		}

		if (work.isSetActiveResource()) {

			User assignedWorker = userService.findUserById(work.getActiveResource().getUser().getId());

			ApiActiveResourceDTO.Builder activeResourceDTOBuilder = new ApiActiveResourceDTO.Builder()
					.withId(StringUtilities.defaultString(work.getActiveResource().getUser().getUserNumber(), null))
					.withFirstName(StringUtilities.defaultString(work.getActiveResource().getUser().getName().getFirstName(), null))
					.withLastName(StringUtilities.defaultString(work.getActiveResource().getUser().getName().getLastName(), null))
					.withCompanyName(StringUtilities.defaultString(work.getActiveResource().getUser().getCompany().getName(), null))
					.withEmail(StringUtilities.defaultString(work.getActiveResource().getUser().getEmail(), null));

			UserAssetAssociation avatars = userService.findUserAvatars(work.getActiveResource().getUser().getId());
			if (avatars != null) {
				com.workmarket.domains.model.asset.Asset avatarSmall = avatars.getTransformedSmallAsset();
				com.workmarket.domains.model.asset.Asset avatarLarge = avatars.getTransformedLargeAsset();

				if (avatarSmall != null) {
					activeResourceDTOBuilder.withProfilePicture(avatarSmall.getCdnUri());
				}
				if (avatarLarge != null) {
					activeResourceDTOBuilder.withProfilePictureLarge(avatarLarge.getCdnUri());
				}
			}

			if (work.getActiveResource().getUser().isSetRatingSummary()) {
				activeResourceDTOBuilder.withRating((int) work.getActiveResource().getUser().getRatingSummary().getRating());
				activeResourceDTOBuilder.withNumberOfRatings((long) work.getActiveResource().getUser().getRatingSummary().getNumberOfRatings());
			}

			activeResourceDTOBuilder.withConfirmedOnsite(work.getActiveResource().isConfirmed());
			activeResourceDTOBuilder.withConfirmedDate(work.getActiveResource().getConfirmedOn()/1000L);

			activeResourceDTOBuilder.withPhoneNumbers(buildUserProfilePhones(assignedWorker.getProfile()));

			if (work.getActiveResource().getUser().getProfile().isSetAddress()) {
				activeResourceDTOBuilder.withAddress(new ApiAddressDTO.Builder()
					.withAddress1(work.getActiveResource().getUser().getProfile().getAddress().getAddressLine1())
					.withAddress2(work.getActiveResource().getUser().getProfile().getAddress().getAddressLine2())
					.withCity(work.getActiveResource().getUser().getProfile().getAddress().getCity())
					.withState(work.getActiveResource().getUser().getProfile().getAddress().getState())
					.withZip(work.getActiveResource().getUser().getProfile().getAddress().getZip())
					.withCountry(work.getActiveResource().getUser().getProfile().getAddress().getCountry())
					.build()
				);
			} else {
				activeResourceDTOBuilder.withAddress(null);
			}

			List<ApiCheckInOutDTO> checkInOutDTOs = new LinkedList<>();
			if (work.getActiveResource().getTimeTrackingLogSize() > 0) {
				for (TimeTrackingEntry item : work.getActiveResource().getTimeTrackingLog()) {
					String note = "";

					if (item.getNote() != null) {
						note = item.getNote().getText();
					}

					checkInOutDTOs.add(new ApiCheckInOutDTO.Builder()
						.withId(item.getId())
						.withCheckedInOn(item.getCheckedInOn() / 1000L)
						.withCheckoutOutOn(item.getCheckedOutOn() / 1000L)
						.withCreatedOn(item.getCreatedOn() / 1000L)
						.withModifiedOn(item.getModifiedOn() / 1000L)
						.withNote(note)
						.build()
					);
				}
			}

			activeResourceDTOBuilder.withCheckInOut(checkInOutDTOs);
			apiAssignmentDetailsDTOBuilder.withActiveResource(activeResourceDTOBuilder.build());
		} else {
			apiAssignmentDetailsDTOBuilder.withActiveResource(null);
		}

		if (work.isSetPricing()) {
			ApiPricingDTO.Builder pricingDTOBuilder = buildPricingStrategy(work.getPricing(), work, true);
			apiAssignmentDetailsDTOBuilder.withPricing(pricingDTOBuilder.build());
		}
		else {
			apiAssignmentDetailsDTOBuilder.withPricing(null);
		}

		if (work.isSetInvoice()) {
			apiAssignmentDetailsDTOBuilder.withInvoiceNumber(work.getInvoice().getNumber());
		}

		if (work.isSetPayment()) {
			apiAssignmentDetailsDTOBuilder.withPayment(new ApiPaymentDTO.Builder()
				.withMaxSpendLimit(work.getPayment().getMaxSpendLimit())
				.withActualSpendLimit(work.getPayment().getActualSpendLimit())
				.withBuyerFee(work.getPayment().getBuyerFee())
				.withTotalCost(work.getPayment().getTotalCost())
				.withHoursWorked(work.getPayment().getHoursWorked())
				.withPaidOn(work.getPayment().getPaidOn())
				.withPaymentDueOn(work.getPayment().getPaymentDueOn() / 1000L)
				.build()
			);
		} else {
			apiAssignmentDetailsDTOBuilder.withPayment(null);
		}

		List<ApiAssignmentDetailsAttachmentDTO> attachments = new LinkedList<>();

		if (work.getAssetsSize() > 0) {
			for (Asset asset : work.getAssets()) {
				attachments.add(new ApiAssignmentDetailsAttachmentDTO.Builder()
					.withUuid(asset.getUuid())
					.withName(asset.getName())
					.withDescription(asset.getDescription())
					.withRelativeUri(asset.getUri())
					.build()
				);
			}
		}

		if (work.getDeliverableAssetsSize() > 0) {
			for (DeliverableAsset deliverableAsset : work.getDeliverableAssets()) {
				attachments.add(new ApiAssignmentDetailsAttachmentDTO.Builder()
					.withUuid(deliverableAsset.getUuid())
					.withName(deliverableAsset.getName())
					.withDescription(StringUtils.defaultString(deliverableAsset.getDescription()))
					.withRelativeUri(deliverableAsset.getUri())
					.build()
				);
			}
		}

		apiAssignmentDetailsDTOBuilder.withAttachments(attachments);

		List<ApiHistoryDTO> history = new LinkedList<>();
		if (work.getChangelogSize() > 0) {

			for (LogEntry item : work.getChangelog()) {

				ApiHistoryDTO.Builder historyItemBuilder = new ApiHistoryDTO.Builder()
						.withDate(item.getTimestamp() / 1000L)
						.withText(item.getText());

				if (LogEntryType.WORK_SUB_STATUS_CHANGE.equals(item.getType())) {
					if(SubStatusActionType.ADDED.equals(item.getSubStatusActionType())) {
						historyItemBuilder.withSetBy(item.getActor().getUserNumber());
					} else {
						historyItemBuilder.withResolvedBy(item.getActor().getUserNumber());
					}

					historyItemBuilder.withLabelId(item.getSubStatus().getId());
					historyItemBuilder.withLabelName(item.getSubStatus().getDescription());
				}

				if (LogEntryType.WORK_CREATED.equals(item.getType())) {
					apiAssignmentDetailsDTOBuilder.withCreatedOn(item.getTimestamp()/1000L);
				}

				history.add(historyItemBuilder.build());
			}

			apiAssignmentDetailsDTOBuilder.withLastModifiedOn(work.getChangelog().get(0).getTimestamp()/1000L);
		}

		apiAssignmentDetailsDTOBuilder.withHistory(history);

		List<ApiNoteDTO> notes = new LinkedList<>();
		if (work.getNotesSize() > 0) {
			for (Note item : work.getNotes()) {
				notes.add(new ApiNoteDTO.Builder()
					.withDate(item.getCreatedOn()/1000L)
					.withText(item.getText())
					.withCreatedBy(item.getCreator().getName().getFirstName() + " " + item.getCreator().getName().getLastName())
					.withIsPrivate(item.isIsPrivate())
					.withIsPrivileged(item.isIsPrivileged())
					.build()
				);
			}
		}

		apiAssignmentDetailsDTOBuilder.withNotes(notes);

		List<ApiCustomFieldGroupDTO> customFieldGroups = new LinkedList<>();

		if (work.getCustomFieldGroupsSize() > 0) {

			for (CustomFieldGroup group : work.getCustomFieldGroups()) {
				List<ApiCustomFieldDTO> fields = new LinkedList<>();

				if (group.getFieldsSize() > 0) {
					for (CustomField field : group.getFields()) {
						fields.add(new ApiCustomFieldDTO.Builder()
							.withId(field.getId())
							.withName(HtmlUtils.htmlEscape(field.getName()))
							.withDefaultValue(HtmlUtils.htmlEscape(field.getDefaultValue()))
							.withRequired(field.isIsRequired())
							.withValue(HtmlUtils.htmlEscape(field.getValue()))
							.build()
						);
					}
				}

				customFieldGroups.add(new ApiCustomFieldGroupDTO.Builder()
					.withId(group.getId())
					.withName(group.getName())
					.withFields(fields)
					.build()
				);
			}
		}

		apiAssignmentDetailsDTOBuilder.withCustomFields(customFieldGroups);

		ApiPartsDTO.Builder partsBuilder = new ApiPartsDTO.Builder();

		if (work.isSetPartGroup()) {

			PartGroupDTO partGroup = work.getPartGroup();
			partsBuilder.withSuppliedByResource(partGroup.isSuppliedByWorker());
			partsBuilder.withReturnRequired(partGroup.isReturnRequired());
			if (partGroup.isSetShippingDestinationType() && partGroup.getShippingDestinationType().convertToDistributionMethod() != null) {
				partsBuilder.withDistributionMethod(partGroup.getShippingDestinationType().convertToDistributionMethod().name().toLowerCase());
			}

			if (partGroup.hasShipToLocation()) {
				LocationDTO shipToLocation = partGroup.getShipToLocation();
				partsBuilder.withPickupLocation(new ApiPartLocationDTO.Builder()
					.withName(shipToLocation.getName())
					.withAddress1(shipToLocation.getAddress1())
					.withAddress2(shipToLocation.getAddress2())
					.withCity(shipToLocation.getCity())
					.withState(shipToLocation.getState())
					.withZip(shipToLocation.getPostalCode())
					.withCountry(shipToLocation.getCountry())
					.build()
				);
			}

			if (partGroup.hasReturnToLocation()) {
				LocationDTO returnToLocation = partGroup.getReturnToLocation();
				partsBuilder.withReturnLocation(new ApiPartLocationDTO.Builder()
					.withName(returnToLocation.getName())
					.withAddress1(returnToLocation.getAddress1())
					.withAddress2(returnToLocation.getAddress2())
					.withCity(returnToLocation.getCity())
					.withState(returnToLocation.getState())
					.withZip(returnToLocation.getPostalCode())
					.withCountry(returnToLocation.getCountry())
					.build()
				);
			}

			for (PartDTO part : partGroup.getParts()) {
				if (part.isReturn()) {
					if (part.isSetPartValue()) {
						partsBuilder.withReturnPartValue(part.getPartValue().doubleValue());
					}
					if (part.isSetShippingProvider()) {
						partsBuilder.withReturnShippingProvider(part.getShippingProvider().name().toLowerCase());
					}
					if (part.isSetTrackingNumber()) {
						partsBuilder.withReturnTrackingNumber(part.getTrackingNumber());
					}
				} else {
					if (part.isSetPartValue()) {
						partsBuilder.withPickupPartValue(part.getPartValue().doubleValue());
					}
					if (part.isSetShippingProvider()) {
						partsBuilder.withPickupShippingProvider(part.getShippingProvider().name().toLowerCase());
					}
					if (part.isSetTrackingNumber()) {
						partsBuilder.withPickupTrackingNumber(part.getTrackingNumber());
					}
				}
			}
			apiAssignmentDetailsDTOBuilder.withParts(partsBuilder.build());
		} else {
			apiAssignmentDetailsDTOBuilder.withParts(null);
		}

		List<ApiApplicationDTO> applicationList = new LinkedList<>();
		if (work.getPendingNegotiationsSize() > 0) {

			for (Negotiation offer : work.getPendingNegotiations()) {
				User resource = userService.findUserById(offer.getRequestedBy().getId());

				ApiApplicantProfileDTO.Builder applicantBuilder = new ApiApplicantProfileDTO.Builder()
					.withId(resource.getUserNumber())
					.withFirstName(resource.getFirstName())
					.withLastName(resource.getLastName())
					.withEmail(resource.getEmail())
					.withCompanyName((resource.getCompany() == null) ? null : resource.getCompany().getName())
					.withRating(ratingService.findSatisfactionRateForUser(resource.getId()))
					.withNumberOfRatings(ratingService.countAllUserRatings(resource.getId()))
					.withPhoneNumbers(buildUserProfilePhones(resource.getProfile()));

				if (resource.getProfile() != null) {
					applicantBuilder.withJobTitle(resource.getProfile().getJobTitle());
					applicantBuilder.withOverview(resource.getProfile().getOverview());
				}

				ApiApplicationDTO.Builder applicationBuilder = new ApiApplicationDTO.Builder()
						.withId(offer.getId())
						.withResource(applicantBuilder.build());


				if (offer.getNote() != null) {
					applicationBuilder.withNote(offer.getNote().getText());
				}

				if (offer.isIsScheduleNegotiation()) {
					ApiRescheduleRequestDTO.Builder schedulingBuilder = buildSchedule(offer.getSchedule());

					if (work.getActiveResource() != null && work.getActiveResource().getRescheduleNegotiation() != null) {
						Note note = work.getActiveResource().getRescheduleNegotiation().getNote();
						if (note != null) {
							schedulingBuilder.withNote(note.getText());
						}

						schedulingBuilder.withIsRequestedByResource(work.getActiveResource().getRescheduleNegotiation().isInitiatedByResource());

						schedulingBuilder.withRequestedOn(work.getActiveResource().getRescheduleNegotiation().getRequestedOn() / 1000L);
					}
					applicationBuilder.withScheduling(schedulingBuilder.build());
				}

				if (offer.isIsPriceNegotiation()) {
					ApiPricingDTO.Builder pricingDTOBuilder = buildPricingStrategy(offer.getPricing(), work, false);
					applicationBuilder.withPricing(pricingDTOBuilder.build());
				}

				if (offer.isSetExpiresOn()) {
					applicationBuilder.withExpiresOn(offer.getExpiresOn() / 1000L);
					applicationBuilder.withExpired(offer.isIsExpired());
				}

				applicationList.add(applicationBuilder.build());
			}
		}

		apiAssignmentDetailsDTOBuilder.withPendingOffers(applicationList);

		List<ApiDeclinedResourceDTO> declinedResourceList = new LinkedList<>();

		for (Long resourceId : workService.findDeclinedResourceIds(work.getId())) {
			User declinedResource = userService.findUserById(resourceId);

			declinedResourceList.add(new ApiDeclinedResourceDTO.Builder()
				.withId(declinedResource.getUserNumber())
				.withFirstName(declinedResource.getFirstName())
				.withLastName(declinedResource.getLastName())
				.withEmail(declinedResource.getEmail())
				.withPhoneNumbers(buildUserProfilePhones(declinedResource.getProfile()))
				.build()
			);
		}

		apiAssignmentDetailsDTOBuilder.withDeclinedResources(declinedResourceList);

		List<ApiQuestionAnswerPairDTO> questionList = new LinkedList<>();

		for (WorkQuestionAnswerPair workQuestionAnswerPair : workQuestionService.findQuestionAnswerPairs(work.getId())) {
			ApiQuestionAnswerPairDTO.Builder questionAnswerPairBuilder = new ApiQuestionAnswerPairDTO.Builder()
					.withId(workQuestionAnswerPair.getId())
					.withQuestion(workQuestionAnswerPair.getQuestion());


			if (workQuestionAnswerPair.isAnswered())
				questionAnswerPairBuilder.withAnswer(workQuestionAnswerPair.getAnswer());

			questionList.add(questionAnswerPairBuilder.build());
		}

		apiAssignmentDetailsDTOBuilder.withQuestions(questionList);
		return apiAssignmentDetailsDTOBuilder.build();
	}

	private ApiRescheduleRequestDTO buildRescheduleRequest(Work work) {
		// Putting this here instead of inside the "active_resource" element, in case we start showing
		// pending buyer reschedules too.

		if (work.isSetActiveResource() && work.getActiveResource().isSetRescheduleNegotiation()) {

			Schedule schedule = work.getActiveResource().getRescheduleNegotiation().getSchedule();
			ApiRescheduleRequestDTO.Builder rescheduleRequestBuilder = buildSchedule(schedule);

			Note note = work.getActiveResource().getRescheduleNegotiation().getNote();

			if (note != null) {
				rescheduleRequestBuilder.withNote(note.getText());
			}
			rescheduleRequestBuilder.withIsRequestedByResource(work.getActiveResource().getRescheduleNegotiation().isInitiatedByResource());
			rescheduleRequestBuilder.withRequestedOn(work.getActiveResource().getRescheduleNegotiation().getRequestedOn() / 1000L);
		  return rescheduleRequestBuilder.build();
		}
		return null;
	}

	private ApiBudgetIncreaseRequestDTO buildBudgetIncreaseRequest(Work work) {
		if (work.isSetActiveResource() && work.getActiveResource().getBudgetNegotiation() != null) {
			Negotiation negotiation = work.getActiveResource().getBudgetNegotiation();
			ApiBudgetIncreaseRequestDTO.Builder budgetIncreaseBuilder = new ApiBudgetIncreaseRequestDTO.Builder(buildPricingStrategy(negotiation.getPricing(), work, false));

			Note note = negotiation.getNote();

			if(note != null) {
				budgetIncreaseBuilder.withNote(note.getText());
			}

			budgetIncreaseBuilder.withRequestedOn(negotiation.getRequestedOn() / 1000L);

			return budgetIncreaseBuilder.build();
		}
		return null;
	}

	private ApiExpenseReimbursementRequestDTO buildExpenseReimbursement(Work work) {
		if (work.isSetActiveResource() && work.getActiveResource().getExpenseNegotiation() != null) {
			ApiExpenseReimbursementRequestDTO.Builder expenseReimbursementBuilder = new ApiExpenseReimbursementRequestDTO.Builder();

			Negotiation negotiation = work.getActiveResource().getExpenseNegotiation();
			expenseReimbursementBuilder.withExpenseReimbursement(negotiation.getPricing().getAdditionalExpenses());

			Note note = negotiation.getNote();

			if(note != null) {
				expenseReimbursementBuilder.withNote(note.getText());
			}

			expenseReimbursementBuilder.withRequestedOn(negotiation.getRequestedOn() / 1000L);

			return expenseReimbursementBuilder.build();
		}
		return null;
	}

	private ApiBonusRequestDTO buildBonusRequest(Work work) {
		if (work.isSetActiveResource() && work.getActiveResource().getBonusNegotiation() != null) {
			Negotiation negotiation = work.getActiveResource().getBonusNegotiation();

			ApiBonusRequestDTO.Builder bonusRequestBuilder = new ApiBonusRequestDTO.Builder();
			bonusRequestBuilder.withBonus(negotiation.getPricing().getBonus());

			Note note = negotiation.getNote();
			if(note != null) {
				bonusRequestBuilder.withNote(note.getText());
			}

			bonusRequestBuilder.withRequestedOn(negotiation.getRequestedOn() / 1000L);

			return bonusRequestBuilder.build();
		}
		return null;
	}

	public List<ApiPhoneNumberDTO> buildUserProfilePhones(Profile profile) {
		List<ApiPhoneNumberDTO> phones = new LinkedList<>();

		if (profile == null)
			return phones;

		if (profile.getWorkPhone() != null) {
			phones.add(new ApiPhoneNumberDTO.Builder()
				.withCountryCode(profile.isWorkPhoneInternationalCodeSet() ? profile.getWorkPhoneInternationalCode().getCallingCodeId() : "")
				.withPhone(profile.getWorkPhone())
				.withType(ContactContextType.WORK)
				.withExtension(profile.getWorkPhoneExtension())
				.build()
			);
		}

		if (profile.getMobilePhone() != null) {
			phones.add(new ApiPhoneNumberDTO.Builder()
				.withCountryCode(profile.isMobilePhoneInternationalCodeSet() ? profile.getMobilePhoneInternationalCode().getCallingCodeId() : "")
				.withPhone(profile.getMobilePhone())
				.withType(ContactContextType.MOBILE)
				.build()
			);
		}

		return phones;
	}

	private ApiPricingDTO.Builder buildPricingStrategy(PricingStrategy pricingStrategy, Work work, boolean showAdditionalExpenses) {
		ApiPricingDTO.Builder pricingDTOBuilder = new ApiPricingDTO.Builder();

		if (pricingStrategy == null)
			return pricingDTOBuilder;

		pricingDTOBuilder.withType(pricingStrategy.getType().getDescription().toUpperCase());
		pricingDTOBuilder.withSpendLimit(pricingStrategy.getMaxSpendLimit());

		Double reimbursements = pricingStrategy.getAdditionalExpenses();
		Double bonus = pricingStrategy.getBonus();
		Double additionalExpenses = pricingStrategy.getMaxSpendLimit() - pricingService.calculateOriginalMaximumResourceCost(work.getId()).doubleValue();
		Double budgetIncreases = additionalExpenses - reimbursements - bonus;

		pricingDTOBuilder.withBudgetIncreases(String.format("%.1f", budgetIncreases));
		pricingDTOBuilder.withExpenseReimbursements(reimbursements);
		pricingDTOBuilder.withBonuses(bonus);

		if (showAdditionalExpenses) {
			//Additional Expenses represents all the increases in price since the assignments acceptance
			//In the rest of the code base, "additionalExpenses" has come to mean only expense reimbursements
			//The inconsistency is due to the API already used this terminology.
			pricingDTOBuilder.withAdditionalExpenses(additionalExpenses);
		}

		if (pricingStrategy.getType() == PricingStrategyType.FLAT) {
			pricingDTOBuilder.withFlatPrice(pricingStrategy.getFlatPrice());
		}
		else if (pricingStrategy.getType() == PricingStrategyType.PER_HOUR) {
			pricingDTOBuilder.withPerHourPrice(pricingStrategy.getPerHourPrice());
			pricingDTOBuilder.withMaxNumberOfHours(pricingStrategy.getMaxNumberOfHours());
		}
		else if (pricingStrategy.getType() == PricingStrategyType.PER_UNIT) {
			pricingDTOBuilder.withPerUnitPrice(pricingStrategy.getPerUnitPrice());
			pricingDTOBuilder.withMaxNumberOfUnits(pricingStrategy.getMaxNumberOfUnits());
		}
		else if (pricingStrategy.getType() == PricingStrategyType.BLENDED_PER_HOUR) {
			pricingDTOBuilder.withInitialPerHourPrice(pricingStrategy.getInitialPerHourPrice());
			pricingDTOBuilder.withInitialNumberOfHours(pricingStrategy.getInitialNumberOfHours());
			pricingDTOBuilder.withAdditionalPerHourPrice(pricingStrategy.getAdditionalPerHourPrice());
			pricingDTOBuilder.withMaxBlendedNumberOfHours(pricingStrategy.getMaxBlendedNumberOfHours());
		}

		return pricingDTOBuilder;
	}

	private ApiRescheduleRequestDTO.Builder buildSchedule(Schedule schedule) {
		ApiRescheduleRequestDTO.Builder schedulingBuilder = new ApiRescheduleRequestDTO.Builder();

		if (schedule != null) {
			if (schedule.isRange()) {
				schedulingBuilder.withRequestWindowStart(schedule.getFrom() / 1000L);
				schedulingBuilder.withRequestWindowEnd(schedule.getThrough() / 1000L);
			} else {
				schedulingBuilder.withRequestScheduledTime(schedule.getFrom() / 1000L);
			}
		}
		return schedulingBuilder;
	}

	/**
	 * Build that assignment array for display.
	 * @param results is alist of WorkDashboardReportRow objects
	 * @return assessment list
	 */
	public List<ApiAssignmentListItemDTO> buildAssignmentList(List<SolrWorkData> results) {
		List<ApiAssignmentListItemDTO> assignmentListItemDTOs = new LinkedList<>();

		for (SolrWorkData item : results) {
			// Format some stats codes.
			String status = item.getWorkStatusTypeCode();

			if (WorkStatusType.ACTIVE.equals(status)) {
				if (item.isConfirmed()) {
					status = "Confirmed";
				}
				else if (item.isResourceConfirmationRequired() && !item.isConfirmed()) {
					status = "Unconfirmed";
				}
				else {
					status = "Assigned";
				}
			}
			else if (WorkStatusType.COMPLETE.equals(status)) {
				status = "Pending Approval";
			}
			else if (WorkStatusType.INPROGRESS.equals(status)) {
				status = "In Progress";
			}
			else if (WorkStatusType.COMPLETE.equals(status)) {
				status = "Pending Approval";
			}
			else if (WorkStatusType.PAYMENT_PENDING.equals(status)) {
				status = "Payment Pending";
			}

			// Last modified on
			Long lastModified = DateUtilities.getUnixTime(item.getModifiedOn());

			String modifierFirstName = StringUtils.isNotEmpty(item.getModifierFirstName()) ? item.getModifierFirstName().substring(0, 1) : null;

			Long paidOn = (item.getPaidOn() == null) ? null : DateUtilities.getUnixTime(item.getPaidOn());

			List<ApiLabelDTO> apiLabelDTOs = Lists.transform(item.getWorkSubStatusTypes(), new Function<WorkSubStatusTypeReportRow, ApiLabelDTO>() {
				@Nullable
				@Override
				public ApiLabelDTO apply(WorkSubStatusTypeReportRow workSubStatusTypeReportRow) {
					return new ApiLabelDTO.Builder()
						.withId(workSubStatusTypeReportRow.getCode())
						.withName(workSubStatusTypeReportRow.getDescription())
						.build();
				}
			});

			assignmentListItemDTOs.add(
				new ApiAssignmentListItemDTO.Builder()
					.withId(item.getWorkNumber())
					.withProjectId(item.getProjectId())
					.withProjectName(item.getProjectName())
					.withTitle(item.getTitle())
					.withScheduledStart(DateUtilities.getUnixTime(item.getScheduleFrom()))
					.withScheduledEnd(DateUtilities.getUnixTime(item.getScheduleThrough()))
					.withCity(item.getCity())
					.withState(item.getState())
					.withPostalCode(item.getPostalCode())
					.withLocationId(item.getLocationNumber())
					.withSpendLimit(item.isInternal() ? "Internal" : StringUtilities.defaultString(item.getSpendLimit(), null))
					.withModifiedStatus(StringUtils.capitalize(status))
					.withStatus(getInternalStatus (item.getWorkStatusTypeCode()))
					.withLabels(apiLabelDTOs)
					.withSubstatuses(apiLabelDTOs)
					.withInternalOwner(item.getBuyerFullName())
					.withClient(item.getClientCompanyName())
					.withClientId(item.getClientCompanyId())
					.withInvoiceNumber(item.getInvoiceNumber())
					.withPaidDate(paidOn)
					.withTotalCost(item.getBuyerTotalCost().toString())
					.withResourceCompanyName(item.getAssignedResourceCompanyName())
					.withResourceUserNumber(item.getAssignedResourceUserNumber())
					.withResourceFullName(item.getAssignedResourceFullName())
					.withLastModifiedOn(lastModified)
					.withModifierFirstName(modifierFirstName)
					.withModifierLastName(item.getModifierLastName())
					.build()
			);
		}

		return assignmentListItemDTOs;
	}

	public String getApiStatusFilter(String code) {
		if (internalStatusMap.containsKey(code)) {
			code = internalStatusMap.get(code);
		}

		if (!validStatuses.contains(code) && (!WorkStatusType.AVAILABLE.equals(code)) &&
				!"all_declined".equals(code)) {
			return WorkStatusType.EXCEPTION;
		}

		return code.trim();
	}

	public PricingStrategyType getPricingStrategyType(String type) {
		try {
			return PricingStrategyType.valueOf(type.toUpperCase());
		} catch (Exception t) {
			logger.warn("unknown pricing strategy type: {}", new Object[]{type}, t);
		}

		return null;
	}

	public Long getUserIdFromNumberOrEmail(String number, String email) {
		if (StringUtils.isNotEmpty(number)) {
			return userService.findUserId(number);
		}
		else if (StringUtils.isNotEmpty(email)) {
			return userService.findUserIdByEmail(email);
		}
		else {
			return null;
		}
	}

	private com.workmarket.domains.model.datetime.TimeZone getTimeZoneForWork(Work work) {
		if (work.getLocation() != null && work.getLocation().getAddress() != null) {
			PostalCode postal = invariantDataService.getPostalCodeByCodeCountryStateCity(
					work.getLocation().getAddress().getZip(),
					work.getLocation().getAddress().getCountry(),
					work.getLocation().getAddress().getState(),
					work.getLocation().getAddress().getCity());
			if (postal != null) {
				return postal.getTimeZone();
			}
		} else {
			if (work.getBuyer() != null) {
				Profile buyerProfile = profileService.findProfile(work.getBuyer().getId());
				if(buyerProfile.getTimeZone() != null) {
					return buyerProfile.getTimeZone();
				}
			}
		}

		return dateTimeService.findTimeZonesById(Constants.WM_TIME_ZONE_ID);
	}

	protected Date parseScheduleForWork(String scheduleString, Work work) throws ParseException {
		try {
			Long unixTime = Long.parseLong(scheduleString);
			return new Date(unixTime * 1000L); // * 1000L for milliseconds
		} catch (NumberFormatException ex) {
			// continue
		}

		com.workmarket.domains.model.datetime.TimeZone workTimeZone = getTimeZoneForWork(work);

		// Allowed multiple date to be parsed.
		String[] formatStrings = {"yyyy/MM/dd hh:mm a z","MM/dd/yyyy hh:mm a z","yyyy/MM/dd hh:mm a","MM/dd/yyyy hh:mm a"};
		for (String formatString : formatStrings) {
			try {
				// try to parse with no time zone, and infer time zone from work location
				SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
				dateFormat.setTimeZone(TimeZone.getTimeZone(workTimeZone.getTimeZoneId()));
				dateFormat.setLenient(false);
				return dateFormat.parse(scheduleString);
			}
			catch (ParseException e) {}
		}
		throw new ParseException("api.v1.assignments.create.schedule_parse_error",0);
	}

	protected void setWorkSchedule(String scheduledStartDate, String scheduledEndDate, Work work) throws ParseException {
		//      2013/05/25 10:30 PM PST
		// or   2013/05/25 10:30 PM GMT-08:00
		// or   2013/05/25 10:30 PM     <--- will use work local time, or company local time if virtual

		Schedule schedule = (work.getSchedule() != null) ? work.getSchedule() : new Schedule();
		schedule.setRange((scheduledEndDate != null));

		if(scheduledStartDate != null) {
			schedule.setFrom(parseScheduleForWork(scheduledStartDate, work).getTime());
		}

		if(schedule.isRange()) {
			if(scheduledEndDate != null) {
				schedule.setThrough(parseScheduleForWork(scheduledEndDate, work).getTime());
			}
		}

		work.setSchedule(schedule);
	}

	protected void setWorkSchedule(Long scheduledStart, Long scheduledEnd, Work work) {
		Schedule schedule = (work.getSchedule() != null) ? work.getSchedule() : new Schedule();
		schedule.setRange( (scheduledEnd != null) );

		if (scheduledStart != null) {
			schedule.setFrom(scheduledStart * 1000L);
		}

		if (schedule.isRange()) {
			if (scheduledEnd != null) {
				schedule.setThrough(scheduledEnd * 1000L);
			}
		}
		work.setSchedule(schedule);
	}

	protected Map<WorkAuthorizationResponse, List<String>> sendToResources(Work work, Set<String> resourceIds, boolean assignToFirstToAccept) {
		final Map<String, String> userNumberToEmail = Maps.newHashMap();
		WorkRoutingResponseSummary routingResultToUsers = new WorkRoutingResponseSummary();

		/* Filter all the resIds and differentiate btw user number or email */
		List<String> invalidIds = Lists.newArrayList(Iterables.filter(resourceIds, new Predicate<String>() {
			@Override
			public boolean apply(@Nullable String resId) {
				if (StringUtils.isNumeric(resId)) {
					/* Looks like a user number */
					userNumberToEmail.put(resId, null);
					return false;
				} else {
					/* Assume it's a user email address */
					User user = userService.findUserByEmail(resId);
					if (user != null) {
						userNumberToEmail.put(user.getUserNumber(), resId);
						return false;
					} else {
						/* No user w/matching email found */
						return true;
					}
				}
			}
		}));

		/* Try to add to work */
		WorkAuthorizationResponse moneyAuthorization = WorkAuthorizationResponse.UNKNOWN;
		try {
			moneyAuthorization = accountRegisterAuthorizationService.authorizeWork(work.getId());
			logger.warn("LOCK Released API createAndRoute");
			if (moneyAuthorization.fail()) {
				routingResultToUsers.addToWorkAuthorizationResponse(moneyAuthorization, userNumberToEmail.keySet());
			} else {
				routingResultToUsers = workRoutingService.addToWorkResources(work.getWorkNumber(), userNumberToEmail.keySet(), assignToFirstToAccept);
			}
		} catch (Exception ex) {
			logger.error("Error sending to worker IDs: ", ex);
			if (moneyAuthorization.success()) {
				accountRegisterAuthorizationService.deauthorizeWork(work.getId());
			}
			routingResultToUsers.addToWorkAuthorizationResponse(WorkAuthorizationResponse.FAILED, userNumberToEmail.keySet());
		}

		/* Extract just the user numbers from the outcome */
		Map<WorkAuthorizationResponse, Set<String>> routingResultToUserNumbers = routingResultToUsers.getResponse();

		/* We need to swap out user numbers for email addresses if that's what the user provided.. do that here */
		Map<WorkAuthorizationResponse, List<String>> routingResultsToUserNumbersOrEmails = Maps.newHashMap();

		for (Map.Entry<WorkAuthorizationResponse, Set<String>> entry : routingResultToUserNumbers.entrySet()) {
			List<String> userNumberOrEmail = Lists.newArrayList();
			for (String userNumber : entry.getValue()) {
				userNumberOrEmail.add(StringUtilities.defaultString(userNumberToEmail.get(userNumber), userNumber));
			}
			routingResultsToUserNumbersOrEmails.put(entry.getKey(), userNumberOrEmail);
		}

		/* Finally, merge all the bad email addresses into the INVALID USER bucket with anything that was already there */
		if (routingResultsToUserNumbersOrEmails.get(WorkAuthorizationResponse.FAILED) != null) {
			invalidIds.addAll(routingResultsToUserNumbersOrEmails.get(WorkAuthorizationResponse.FAILED));
		}

		if (! CollectionUtilities.isEmpty(invalidIds)) {
			routingResultsToUserNumbersOrEmails.put(WorkAuthorizationResponse.FAILED, invalidIds);
		}

		if (CollectionUtils.isEmpty(routingResultsToUserNumbersOrEmails.get(WorkAuthorizationResponse.SUCCEEDED))) {
			accountRegisterAuthorizationService.deauthorizeWork(work.getId());
		}

		return routingResultsToUserNumbersOrEmails;
	}

	protected long calculateNumberOfEligibleResources(Work work, PeopleSearchRequest filter) {
		AssignmentResourceSearchRequest assignmentSearchRequest = new AssignmentResourceSearchRequest();
		assignmentSearchRequest.setWorkNumber(work.getWorkNumber());
		filter.setIndustryFilter(Sets.newHashSet(work.getIndustry().getId()));
		assignmentSearchRequest.setRequest(filter);
		assignmentSearchRequest.setBoostIndustryId(work.getIndustry().getId());
		assignmentSearchRequest.setDescription(work.getDescription());
		assignmentSearchRequest.setSkills(work.getDesiredSkills());

		long num_sent = 0;
		try {
			PeopleSearchResponse searchResponse;
			if (PricingStrategyType.INTERNAL.equals(work.getPricing().getType())) {
				searchResponse = searchService.searchInternalAssignmentResources(assignmentSearchRequest);
			} else {
				searchResponse = searchService.searchAssignmentResources(assignmentSearchRequest);
			}

			// This still doesn't match the actual # of eligible resources... but it's much closer than it was.
			num_sent = NumberUtilities.min(Constants.GROUP_SEND_RESOURCES_LIMIT, searchResponse.getTotalResultsCount());

		} catch (Exception e) {
			// this is not good, but also not necessarily a showstoppers
			logger.error("error calculating number of sent resources", e);
		}

		return num_sent;
	}

	private Long normalizeRadius(Long radius) {
		// if 0 or null, use default travel distance
		if(radius == null || radius.equals(0L)) {
			return (long) Constants.MAX_TRAVEL_DISTANCE;
		}
		else return (NumberUtilities.isWithinRange(radius, 1, Constants.MAX_GROUP_SEND_RADIUS.intValue()) ?
				radius : Constants.MAX_GROUP_SEND_RADIUS.intValue());
	}

	protected RoutingStrategy createRoutingStrategy(Work work, Set<Long> groupIds, Long radius, boolean assignToFirst) {
		radius = normalizeRadius(radius);

		PeopleSearchRequest filter = new PeopleSearchRequest()
				.setGroupFilter(groupIds)
				.setUserId(authenticationService.getCurrentUser().getId());

		return createRoutingStrategy(work, filter, radius, assignToFirst);
	}

	protected RoutingStrategy createRoutingStrategy(Work work, PeopleSearchRequest filter, Long radius, boolean assignToFirstToAccept) {
		if (!work.getOffsiteLocation() && work.getLocation().getAddress() != null) {
			radius = normalizeRadius(radius);

			filter.setLocationFilter(
				new LocationFilter()
					.setWillingToTravelTo(work.getLocation().getAddress().getZip())
					.setMaxMileFromResourceToLocation(radius.intValue()));

			filter.setCountryFilter(Sets.newHashSet(work.getLocation().getAddress().getCountry()));
		}

		filter.setUserId(authenticationService.getCurrentUser().getId());

		return new RoutingStrategy()
			.setFilter(filter)
			.setDelayMinutes(0)
			.setAssignToFirstToAccept(assignToFirstToAccept);
	}

	/* NOTE: returns groups that either do NOT belong to this company or do NOT exist */
	protected List<Long> checkForInvalidGroups(Set<Long> groupIds) {
		Long userId = authenticationService.getCurrentUser().getId();
		ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();
		ManagedCompanyUserGroupRowPagination results = userGroupService.findMyCompanyGroups(userId, pagination);

		List<Long> companyGroupIds = Lists.newArrayList();
		for (ManagedCompanyUserGroupRow row : results.getResults()) {
			companyGroupIds.add(row.getGroupId());
		}

		if (isNotEmpty(results.getResults())) {
			return Lists.newArrayList(CollectionUtils.subtract(groupIds, companyGroupIds));
		}

		return null;
	}
}
