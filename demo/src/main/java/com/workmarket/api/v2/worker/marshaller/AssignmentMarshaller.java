package com.workmarket.api.v2.worker.marshaller;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.ApiV2Pagination;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.model.AttachmentDTO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.AssessmentAttemptPair;
import com.workmarket.thrift.assessment.Attempt;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Asset;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.core.DeliverableAsset;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.Note;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.core.Status;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.thrift.work.Negotiation;
import com.workmarket.thrift.work.PaymentSummary;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Resource;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.SubStatus;
import com.workmarket.thrift.work.TimeTrackingEntry;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkMilestones;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.validators.FastFundsValidator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Data marshaller takes result object models from external services and translates them to API assignment objects
 */
@Service
public class AssignmentMarshaller extends ApiMarshaller {

	@Autowired AssetManagementService assetManagementService;
	@Autowired WorkNegotiationService workNegotiationService;
	@Autowired com.workmarket.service.business.UserService userService;
	@Autowired FastFundsValidator fastFundsValidator;
	@Autowired WorkResourceService workResourceService;
	@Autowired BillingService billingService;
	@Autowired SecurityContextFacade securityContextFacade;
	@Autowired UserRoleService userRoleService;

	private static final Log logger = LogFactory.getLog(AssignmentMarshaller.class);

	private static final int MINUTES_PER_HOUR = 60;

	private static final String WORK_CONTROLLER_PAGINATION_PAGE_KEY = "page";
	private static final String WORK_CONTROLLER_PAGINATION_TOTAL_PAGES_KEY = "totalPages";
	private static final String WORK_CONTROLLER_PAGINATION_TOTAL_RESULTS_KEY = "totalResults";
	private static final String WORK_CONTROLLER_PAGINATION_PAGE_SIZE_KEY = "pageSize";

	protected static final String EXPENSE_NEGOTIATION_TYPE = "expense";
	protected static final String BUDGET_NEGOTIATION_TYPE = "budget";
	protected static final String BONUS_NEGOTIATION_TYPE = "bonus";
	protected static final String SCHEDULE_NEGOTIATION_TYPE = "schedule";
	protected static final String APPLY_NEGOTIATION_TYPE = "apply";

	public AssignmentMarshaller() {
	}

	@VisibleForTesting
	public AssignmentMarshaller(
					final AssetManagementService assetManagementService,
					final FastFundsValidator fastFundsValidator,
					final SecurityContextFacade securityContextFacade) {
		this.assetManagementService = assetManagementService;
		this.securityContextFacade = securityContextFacade;
		this.fastFundsValidator = fastFundsValidator;
	}

	public void getListFulfillmentResponse(final List<Map<String, Object>> results,
																				 final Map<String, WorkResponse> workNumberToWorkResponseMap,
																				 final FulfillmentPayloadDTO response) {

		// Do not instantiate a new response. FulfillmentResponse may be populated by multiple marshallers at once,
		// and should be passed in by the FulfillmentProcessor
		if (response == null) {
			return;
		}

		List payload = new ArrayList();

		for (final Map<String, Object> result : results) {

			final WorkResponse workResponse = (workNumberToWorkResponseMap != null) ? (WorkResponse) workNumberToWorkResponseMap
							.get((String) result.get("work_number")) : null;

            /*
							response.addResponseResult
              (
              getListResultToApiModel(result,workResponse)
              );
            */

			payload.add(getListResultToApiModel(result, workResponse));
		}

		response.setPayload(payload);
	}

	public void getDetailsFulfillmentResponse(final WorkResponse workResponse,
																						final List<CustomField> customFields,
																						final List<PartDTO> assignmentParts,
																						final FulfillmentPayloadDTO response) {
		getDetailsFulfillmentResponse(workResponse, customFields, assignmentParts, response, null);
	}

	public void getDetailsFulfillmentResponse(final WorkResponse workResponse,
																						final List<CustomField> customFields,
																						final List<PartDTO> assignmentParts,
																						final FulfillmentPayloadDTO response,
																						final Long userId) {

		if (response == null || workResponse == null || workResponse.getWork() == null) {
			return;
		}

		final Work assignment = workResponse.getWork();

		final ApiJSONPayloadMap map = new ApiJSONPayloadMap();

		map.put("id", assignment.getWorkNumber());
		map.put("assignmentId", assignment.getId());
		map.put("title", assignment.getTitle());
		map.put("description", assignment.getDescription());

		map.put("workBundle", workResponse.isWorkBundle());

		if (workResponse.isWorkBundle()) {
			putNonZeroIntoMap(map, "workBundleId", assignment.getId());
		}
		else if (workResponse.isInWorkBundle()) {
			putNonZeroIntoMap(map, "workBundleId", workResponse.getWorkBundleParent().getId());
		}

		if (assignment.getStatus() != null) {
			// from src/main/webapp/WEB-INF/views/mobile/pages/v2/assignments/details.jsp
			final String status = assignment.isInProgress() ? "inprogress" : assignment.getStatus().getCode();
			map.put("status", status);
		}

		final Resource activeResource = assignment.getActiveResource();

		if(assignment.isSetPrivateInstructions() && assignment.getPrivateInstructions()) {
			// instructions are private, only show to active worker
			if (activeResource != null && activeResource.getUser().getId() == userId) {
				// you are the active worker, you CAN see private instructions
				map.put("specialInstructions", assignment.getInstructions());
			} else {
				// you are NOT the active worker, you can't see private instructions
				map.put("specialInstructions", "");
			}
		}
		else {
			// instructions are not private, show to anyone
			map.put("specialInstructions", assignment.getInstructions());
		}

		map.put("desiredSkills", assignment.getDesiredSkills());

		map.put("locationOffsite", assignment.isOffsiteLocation());
		if (!assignment.isOffsiteLocation()) {
			map.put("location", getDetailsResultToLocationModel(workResponse));
		}

		map.put("enablePrintout", assignment.getConfiguration().isEnableAssignmentPrintout());
		map.put("enablePrintSignature", assignment.getConfiguration().isEnablePrintoutSignature());

		if (activeResource != null) {
			if (activeResource.getConfirmedOn() > 0) {
				map.put("confirmedDate", activeResource.getConfirmedOn());
			}
		}
		if (assignment.getConfirmableDate() != null) {
			map.put("confirmWindowStart", assignment.getConfirmableDate().getTimeInMillis());
		}
		if (assignment.getConfirmByDate() != null) {
			map.put("confirmWindowEnd", assignment.getConfirmByDate().getTimeInMillis());
		}
		map.put("workerSuppliesParts",
						workResponse.getWork().getPartGroup() == null || workResponse.getWork()
										.getPartGroup()
										.isSuppliedByWorker() == null ? Boolean.FALSE : workResponse.getWork()
										.getPartGroup()
										.isSuppliedByWorker());

		map.put("isAssignToFirstResource", assignment.getConfiguration().isAssignToFirstResource());
		map.put("termsOfAgreement", assignment.getConfiguration().getStandardTerms());
		map.put("codeOfConduct", assignment.getConfiguration().getStandardInstructions());
		map.put("attachments", getDetailsResultToAttachmentModel(workResponse));

		map.put("company", getDetailsResultToCompanyModel(workResponse));
		map.put("clientCompany", getDetailsResultToClientCompanyModel(workResponse));
		map.put("schedule", getDetailsResultToScheduleModel(workResponse));
		map.put("pricing", getDetailsResultToPricingModel(workResponse));
		map.put("messages", getDetailsResultToMessagesModel(workResponse));
		map.put("assets", getDetailsResultToAssetsModel(workResponse));
		map.put("deliverablesConfiguration", getDetailsResultToDeliverablesConfigurationModel(workResponse));
		map.put("customFields", getDetailsResultToCustomFieldsModel(workResponse));
		map.put("contacts", getDetailsResultToContactsModel(workResponse));
		map.put("timeTracking", getDetailsResultToTimeTrackingModel(workResponse));
		map.put("labels", getDetailsResultToLabelsModel(workResponse));
		map.put("milestones", getDetailsResultToMilestonesModel(workResponse));
		map.put("assessments", getDetailsResultToAssessmentsModel(workResponse));
		map.put("shipments", getDetailsResultToShipmentsModel(workResponse, assignmentParts));
		map.put("headerCustomFields", getDetailsResultToHeaderCustomFieldsModel(workResponse, customFields));
		if (isPriceHidden()) {
			map.put("payment", getEmptyPayment());
		} else {
			map.put("payment", getDetailsResultToPaymentModel(workResponse));
		}
		map.put("negotiations", getDetailsResultToNegotiationsModel(workResponse, userId));
		addFastFundsFields(map, workResponse.getWork());

		List payload = new ArrayList();
		payload.add(map);
		//response.addResponseResult(map);
		response.setPayload(payload);
	}

	private ApiJSONPayloadMap getEmptyPayment() {
		final ApiJSONPayloadMap map = new ApiJSONPayloadMap();
		map.put("total", 0);
		map.put("paymentTermsDays", 0);
		map.put("paymentTermsEnabled", false);
		return map;
	}

	/**
	 * Returns all deliverables not attached to a requirement set.
	 *
	 * @param workResponse
	 * @return
	 */
	private List<AttachmentDTO> getDetailsResultToAttachmentModel(final WorkResponse workResponse) {
		final ImmutableList.Builder<AttachmentDTO> builder = ImmutableList.builder();
		final List<WorkAssetAssociation> attachments = assetManagementService.findAllAssetAssociationsByWorkId(ImmutableList
																																																									 .of(workResponse
																																																															 .getWork()
																																																															 .getId()));
		for (final WorkAssetAssociation a : attachments) {
			if (a.isDeliverable() && a.getDeliverableRequirementId() == null) { // deliverable not attached to requirement set
				final com.workmarket.domains.model.asset.Asset asset = a.getAsset();
				builder.add(new AttachmentDTO(asset.getUUID(),
																			String.format("/asset/%s", asset.getUUID()),
																			asset.getDescription(),
																			asset.getName(),
																			asset.getMimeType(),
																			asset.getFileByteSize()));
			}
		}

		return builder.build();
	}

	public ApiV2Pagination generatePaginationFromWorkControllerResponse(final Map<String, Object> servicePageData,
																																			final HttpServletRequest request) {

		final Long page = new Long((Integer) servicePageData.get(WORK_CONTROLLER_PAGINATION_PAGE_KEY));
		final Long pageSize = new Long((Integer) servicePageData.get(WORK_CONTROLLER_PAGINATION_PAGE_SIZE_KEY));
		final Long totalPageCount = new Long((Integer) servicePageData.get(WORK_CONTROLLER_PAGINATION_TOTAL_PAGES_KEY));
		final Long totalRecordCount = new Long((Integer) servicePageData.get(WORK_CONTROLLER_PAGINATION_TOTAL_RESULTS_KEY));

		return new ApiV2Pagination.ApiPaginationBuilder().page(page)
						.pageSize(pageSize)
						.totalPageCount(totalPageCount)
						.totalRecordCount(totalRecordCount)
						.build(request);
	}

	public ApiJSONPayloadMap getListResultToApiModel(final Map<String, Object> result, final WorkResponse workResponse) {

		final ApiJSONPayloadMap map = new ApiJSONPayloadMap();

        /*
					ListResponse listResponse = new ListResponse
          .Builder()
          .id()
          .title()
          .status()
          .locationOffsite()
          .company()
          .location()
          .pricing()
          .pricingDetails()
          .schedule()
          .payments()
          .milestone()
          .build();
        */

		map.put("id", result.get("id"));
		map.put("title", result.get("title"));
		map.put("status", result.get("status"));
		map.put("locationOffsite", result.get("location_offsite"));
		map.put("company", getListResultToCompanyModel(result));
		map.put("location", getListResultToLocationModel(result));
		map.put("pricing", getListResultToPricingModel(result));

		if (workResponse != null) {

			if (workResponse.isInWorkBundle()) {
				putNonZeroIntoMap(map, "workBundleId", workResponse.getWorkBundleParent().getId());
			}

			final Work work = workResponse.getWork();
			if (work != null) {
				if (work.getPricing() != null) {
					final ApiJSONPayloadMap pricingMap = getPricing(work);
					map.put("pricingDetails", pricingMap);
				}
				map.put("isAssignToFirstResource", work.getConfiguration().isAssignToFirstResource());
				addFastFundsFields(map, work);
			}
		}

		//map.put("schedule", getListResultToScheduleModel(result));
		map.put("schedule", getDetailsResultToScheduleModel(workResponse));
		if (isPriceHidden()) {
			map.put("payment", getEmptyPayment());
		} else {
			map.put("payment", getListResultToPaymentModel(result));
		}
		//map.put("milestones", getListResultToMilestoneModel(result));
		map.put("milestones", getDetailsResultToMilestonesModel(workResponse));
		//map.put("customFields", getDetailsResultToCustomFieldsModel(workResponse));

		return map;
	}

	private ApiJSONPayloadMap getPricing(final Work work) {
		final ApiJSONPayloadMap pricingMap = new ApiJSONPayloadMap();
		final PricingStrategy pricingStrategy = work.getPricing();

		boolean priceHidden = isPriceHidden();

		if (!priceHidden) {
			getPricingStrategy(pricingMap, pricingStrategy);
		} else {
			pricingMap.put("internal", Boolean.TRUE);
		}

		boolean isInternal = PricingStrategyType.INTERNAL.equals(pricingStrategy.getType());
		boolean disablePriceNegotiationChecked = work.getConfiguration().isDisablePriceNegotiation();

		pricingMap.put("disablePriceNegotiationChecked", disablePriceNegotiationChecked);
		pricingMap.put("disablePriceNegotiation", disablePriceNegotiationChecked || isInternal || priceHidden);
		return pricingMap;
	}

	private boolean isPriceHidden() {
		ExtendedUserDetails extendedUserDetails = securityContextFacade.getCurrentUser();
		if (extendedUserDetails.isCompanyHidesPricing()) {
			//putting this inside if to not incur unneeded service call
			com.workmarket.domains.model.User user = userService.getUserWithRoles(securityContextFacade.getCurrentUser().getId());
			return !userRoleService.hasAnyAclRole(user, AclRole.ACL_ADMIN, AclRole.ACL_MANAGER, AclRole.ACL_DISPATCHER);
		}

		if (extendedUserDetails.isEmployeeWorker()) {
			//EmployeeWorkers shouldn't see price
			return true;
		}

		return false;
	}

	private void addFastFundsFields(final ApiJSONPayloadMap map, final Work work) {
		final boolean isFastFundable = !isPriceHidden() && fastFundsValidator.isWorkFastFundable(work.getWorkNumber());
		map.put("isFastFundable", isFastFundable);
		if (isFastFundable) {
			final Long workId = work.getId();
			final BigDecimal totalResourceCost = billingService.calculateTotalResourceCostOnWork(workId);
			final BigDecimal fastFundFeeCost = billingService.calculateFastFundsFeeCostOnWork(workId, totalResourceCost);
			map.put("totalResourceCost", totalResourceCost);
			map.put("fastFundsFeeCost", fastFundFeeCost);
		}
	}

	public ApiJSONPayloadMap getListResultToMilestoneModel(final ApiJSONPayloadMap result) {

		final ApiJSONPayloadMap milestones = new ApiJSONPayloadMap();

		putNonZeroIntoMap(milestones, "createdDate", (Long) result.get("created_date"));
		putNonZeroIntoMap(milestones, "sentDate", (Long) result.get("sent_date"));
		putNonZeroIntoMap(milestones, "completedDate", (Long) result.get("completed_date"));

		return milestones;
	}

	public ApiJSONPayloadMap getListResultToLocationModel(final Map<String, Object> result) {

		final ApiJSONPayloadMap locationMap = new ApiJSONPayloadMap();

		locationMap.put("city", result.get("city"));
		locationMap.put("state", result.get("state"));
		locationMap.put("postalCode", result.get("postal_code"));
		locationMap.put("address1", result.get("address"));

		if (result.get("latitude") != null && result.get("longitude") != null) {
			ApiJSONPayloadMap coordinateMap = new ApiJSONPayloadMap();
			coordinateMap.put("longitude", result.get("longitude"));
			coordinateMap.put("latitude", result.get("latitude"));
			locationMap.put("coordinates", coordinateMap);
		}

		return locationMap;
	}

	public ApiJSONPayloadMap getDetailsResultToLocationModel(final WorkResponse workResponse) {
		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork().getLocation() == null) {
			return null;
		}

		final Location location = workResponse.getWork().getLocation();

		final ApiJSONPayloadMap locationMap = new ApiJSONPayloadMap();

		locationMap.put("id", location.getId());

		final String name = location.getName();
		if (StringUtils.isNotBlank(name)) {
			locationMap.put("name", name);
		}

		final String number = location.getNumber();
		if (StringUtils.isNotBlank(number)) {
			locationMap.put("locationNumber", number);
		}

		if (StringUtils.isNotBlank(location.getInstructions())) {
			locationMap.put("instructions", location.getInstructions());
		}

		final Address address = location.getAddress();
		if (address != null) {
			locationMap.put("address1", address.getAddressLine1());
			locationMap.put("address2", address.getAddressLine2());
			locationMap.put("city", address.getCity());
			locationMap.put("state", address.getState());
			locationMap.put("postalCode", address.getZip());
			locationMap.put("country", address.getCountry());
		}

		if (address.getPoint() != null) {

			final ApiJSONPayloadMap coordinateMap = new ApiJSONPayloadMap();

			coordinateMap.put("latitude", address.getPoint().getLatitude());
			coordinateMap.put("longitude", address.getPoint().getLongitude());
			locationMap.put("coordinates", coordinateMap);
		}


		final List<ApiJSONPayloadMap> contactList = getContactList(workResponse.getWork());
		if (contactList.size() > 0) {
			locationMap.put("contacts", contactList);
		}

		return locationMap;
	}

	private List<ApiJSONPayloadMap> getContactList(final Work work) {
		final List<ApiJSONPayloadMap> contactList = new LinkedList<>();
		final User locationContact = work.getLocationContact();

		if (locationContact != null) {
			final ApiJSONPayloadMap contactMap = getUserToContactMap(locationContact);
			if (MapUtils.isNotEmpty(contactMap)) {
				contactList.add(contactMap);
			}
		}

		final User secondaryLocationContact = work.getSecondaryLocationContact();
		if (secondaryLocationContact != null) {
			final ApiJSONPayloadMap contactMap = getUserToContactMap(secondaryLocationContact);
			if (MapUtils.isNotEmpty(contactMap)) {
				contactList.add(contactMap);
			}
		}
		return contactList;
	}

	public ApiJSONPayloadMap getListResultToPricingModel(final Map<String, Object> result) {

		final ApiJSONPayloadMap pricingMap = new ApiJSONPayloadMap();

		final String pricingType = (String) result.get("pricing_type");
		final String price = (String) result.get("price");
		//final String spendLimit = (String) result.get("spend_limit");

		final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

		try {

			//pricingMap.put("maxSpendLimit", currencyFormatter.parse(spendLimit));

			if (StringUtils.isNotBlank(pricingType) && StringUtils.isNotBlank(price)) {

				pricingMap.put("pricingType", pricingType);

				if ("Flat".equals(pricingType)) {
					pricingMap.put("flatPrice", currencyFormatter.parse(price));
				}
				else if ("Hourly".equals(pricingType)) {
					pricingMap.put("perHour", currencyFormatter.parse(price));
				}
				else if ("Unit".equals(pricingType)) {
					pricingMap.put("perUnit", currencyFormatter.parse(price));
				}
				else if ("Blended".equals(pricingType)) {
					pricingMap.put("blendedPerHour", currencyFormatter.parse(price));
				}

				if ("Internal".equals(pricingType)) {
					pricingMap.put("internal", Boolean.TRUE);
				}
				else {
					pricingMap.put("internal", Boolean.FALSE);
				}
			}
			else {

				if (StringUtils.isNotBlank(price)) {
					pricingMap.put("flatPrice", currencyFormatter.parse(price));
				}
			}
		}
		catch (final ParseException pe) {

			logger.error("Error trying to parse assignment price for assignment w/ work number " + result.get("id"));
			pricingMap.put("flatPrice", "NaN");
		}

		return pricingMap;
	}

	public ApiJSONPayloadMap getDetailsResultToPricingModel(final WorkResponse workResponse) {
		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork().getPricing() == null) {
			return null;
		}

		final PricingStrategy pricingStrategy = workResponse.getWork().getPricing();

		final ApiJSONPayloadMap pricingMap = new ApiJSONPayloadMap();

		boolean priceHidden = isPriceHidden();

		if (priceHidden) {
			pricingMap.put("internal", Boolean.TRUE);
		} else {
			getPricingStrategy(pricingMap, pricingStrategy);
		}

		boolean isInternal = PricingStrategyType.INTERNAL.equals(pricingStrategy.getType());

		boolean disablePriceNegotiationChecked = workResponse.getWork().getConfiguration().isDisablePriceNegotiation();

		pricingMap.put("disablePriceNegotiationChecked", disablePriceNegotiationChecked);
		pricingMap.put("disablePriceNegotiation", disablePriceNegotiationChecked || isInternal || priceHidden);
		pricingMap.put("offlinePayment", pricingStrategy.isOfflinePayment());

		return pricingMap;
	}

	public void getPricingStrategy(final ApiJSONPayloadMap map, final PricingStrategy pricingStrategy) {

		switch (pricingStrategy.getType()) {
			case FLAT:
				map.put("flatPrice", pricingStrategy.getFlatPrice());
				map.put("internal", Boolean.FALSE);
				break;
			case PER_HOUR:
				map.put("perHour", pricingStrategy.getPerHourPrice());
				map.put("maxHours", pricingStrategy.getMaxNumberOfHours());
				map.put("internal", Boolean.FALSE);
				break;
			case PER_UNIT:
				map.put("perUnit", pricingStrategy.getPerUnitPrice());
				map.put("maxUnits", pricingStrategy.getMaxNumberOfUnits());
				map.put("internal", Boolean.FALSE);
				break;
			case BLENDED_PER_HOUR:
				map.put("blendedPerHour", pricingStrategy.getInitialPerHourPrice());
				map.put("perAdditionalHour", pricingStrategy.getAdditionalPerHourPrice());
				map.put("initialHours", pricingStrategy.getInitialNumberOfHours());
				map.put("maxAdditionalHours", pricingStrategy.getMaxBlendedNumberOfHours());
				map.put("internal", Boolean.FALSE);
				break;
			case INTERNAL:
				map.put("internal", Boolean.TRUE);
				break;
			default:
		}

		map.put("offlinePayment", pricingStrategy.isOfflinePayment());
		if (pricingStrategy.getMaxSpendLimit() > 0) {
			map.put("maxSpendLimit", pricingStrategy.getMaxSpendLimit());
		}
		if (pricingStrategy.getAdditionalExpenses() > 0) {
			map.put("additionalExpenses", pricingStrategy.getAdditionalExpenses());
		}
		if (pricingStrategy.getBonus() > 0) {
			map.put("bonus", pricingStrategy.getBonus());
		}
		if (pricingStrategy.getOverridePrice() > 0) {
			map.put("overridePrice", pricingStrategy.getOverridePrice());
		}
	}

	public ApiJSONPayloadMap getListResultToScheduleModel(final ApiJSONPayloadMap result) {

		final ApiJSONPayloadMap scheduleMap = new ApiJSONPayloadMap();

		if (result.containsKey("start_datetime_millis")) {

			if (result.containsKey("end_datetime_millis")) {
				scheduleMap.put("startWindowBegin", result.get("start_datetime_millis"));
			}
			else {
				scheduleMap.put("start", result.get("start_datetime_millis"));
			}
		}

		if (result.containsKey("end_datetime_millis")) {
			scheduleMap.put("startWindowEnd", result.get("end_datetime_millis"));
		}

		return scheduleMap;
	}

	public ApiJSONPayloadMap getDetailsResultToScheduleModel(final WorkResponse workResponse) {

		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork().getSchedule() == null) {
			return null;
		}

		final ApiJSONPayloadMap scheduleMap = new ApiJSONPayloadMap();

		final Schedule schedule = workResponse.getWork().getSchedule();

		if (schedule.getThrough() <= 0) {
			scheduleMap.put("start", schedule.getFrom());
		}
		else {
			scheduleMap.put("startWindowBegin", schedule.getFrom());
			scheduleMap.put("startWindowEnd", schedule.getThrough());
		}

		return scheduleMap;
	}

	public ApiJSONPayloadMap getDetailsResultToMilestonesModel(final WorkResponse workResponse) {
		if (workResponse == null || workResponse.getWorkMilestones() == null) {
			return null;
		}

		final ApiJSONPayloadMap milestoneMap = new ApiJSONPayloadMap();

		final WorkMilestones milestones = workResponse.getWorkMilestones();

		putNonZeroIntoMap(milestoneMap, "sentDate", milestones.getSentOn());
		putNonZeroIntoMap(milestoneMap, "acceptedDate", milestones.getAcceptedOn());
		putNonZeroIntoMap(milestoneMap, "activeDate", milestones.getActiveOn());
		putNonZeroIntoMap(milestoneMap, "cancelledDate", milestones.getCancelledOn());
		putNonZeroIntoMap(milestoneMap, "closedDate", milestones.getClosedOn());
		putNonZeroIntoMap(milestoneMap, "completedDate", milestones.getCompleteOn());
		putNonZeroIntoMap(milestoneMap, "createdDate", milestones.getCreatedOn());
		putNonZeroIntoMap(milestoneMap, "declinedDate", milestones.getDeclinedOn());
		putNonZeroIntoMap(milestoneMap, "draftDate", milestones.getDraftOn());
		putNonZeroIntoMap(milestoneMap, "paidDate", milestones.getPaidOn());
		putNonZeroIntoMap(milestoneMap, "refundedDate", milestones.getRefundedOn());
		putNonZeroIntoMap(milestoneMap, "voidedDate", milestones.getVoidOn());
		putNonZeroIntoMap(milestoneMap, "dueDate", milestones.getDueOn());

		return milestoneMap;
	}

	public ApiJSONPayloadMap getListResultToCompanyModel(final Map<String, Object> result) {

		final ApiJSONPayloadMap companyMap = new ApiJSONPayloadMap();

		companyMap.put("id", result.get("company_id"));
		companyMap.put("name", result.get("company"));
		companyMap.put("number", result.get("company_number"));
		companyMap.put("uuid", result.get("company_uuid"));

		return companyMap;
	}

	public ApiJSONPayloadMap getDetailsResultToCompanyModel(final WorkResponse workResponse) {

		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork().getCompany() == null) {
			return null;
		}

		final Company company = workResponse.getWork().getCompany();

		final ApiJSONPayloadMap companyMap = new ApiJSONPayloadMap();

		putNonZeroIntoMap(companyMap, "id", company.getId());
		companyMap.put("number", company.getCompanyNumber());
		companyMap.put("name", company.getName());
		companyMap.put("uuid", company.getCompanyUuid());
		companyMap.put("customSignatureLine", company.getCustomSignatureLine());

		return companyMap;
	}

	public ApiJSONPayloadMap getDetailsResultToClientCompanyModel(final WorkResponse workResponse) {

		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork().getClientCompany() == null) {
			return null;
		}

		final Company clientCompany = workResponse.getWork().getClientCompany();

		final ApiJSONPayloadMap companyMap = new ApiJSONPayloadMap();

		putNonZeroIntoMap(companyMap, "id", clientCompany.getId());
		companyMap.put("name", clientCompany.getName());

		return companyMap;
	}

	public ApiJSONPayloadMap getListResultToPaymentModel(final Map<String, Object> result) {

		final ApiJSONPayloadMap paymentMap = new ApiJSONPayloadMap();

		paymentMap.put("total", result.get("amount_earned"));
		if (result.get("paid_on_millis") != null && (Long) result.get("paid_on_millis") > 0) {
			paymentMap.put("paidDate", result.get("paid_on_millis"));
		}
		if (result.get("due_on_millis") != null && (Long) result.get("due_on_millis") > 0) {
			paymentMap.put("dueDate", result.get("due_on_millis"));
		}
		paymentMap.put("paymentTermsEnabled", result.get("payment_terms_enabled"));
		paymentMap.put("paymentTermsDays", result.get("payment_terms_days"));

		return paymentMap;
	}

	public ApiJSONPayloadMap getDetailsResultToPaymentModel(final WorkResponse workResponse) {

		if (workResponse == null || workResponse.getWork() == null || (workResponse.getWork()
						.getPayment() == null && workResponse.getWork().getConfiguration() == null)) {
			return null;
		}

		final ApiJSONPayloadMap paymentMap = new ApiJSONPayloadMap();

		final PaymentSummary paymentSummary = workResponse.getWork().getPayment();

		if (paymentSummary != null) {
			getPaymentSummary(paymentMap, paymentSummary);
		}

		final ManageMyWorkMarket configuration = workResponse.getWork().getConfiguration();

		if (configuration != null) {
			getManageMyWorkMarket(paymentMap, configuration);
		}

		return paymentMap;
	}

	public void getPaymentSummary(final ApiJSONPayloadMap paymentMap, final PaymentSummary paymentSummary) {
		paymentMap.put("minutesWorked", paymentSummary.getHoursWorked() * MINUTES_PER_HOUR);
		putNonZeroIntoMap(paymentMap, "unitsCompleted", paymentSummary.getUnitsProcessed());
		putNonZeroIntoMap(paymentMap, "additionalExpenses", paymentSummary.getAdditionalExpenses());
		putNonZeroIntoMap(paymentMap, "bonus", paymentSummary.getBonus());
		putNonZeroIntoMap(paymentMap, "total", paymentSummary.getActualSpendLimit());
		if (paymentSummary.getPaymentDueOn() > 0) {
			paymentMap.put("dueDate", paymentSummary.getPaymentDueOn());
		}
		if (paymentSummary.getPaidOn() > 0) {
			paymentMap.put("paidDate", paymentSummary.getPaidOn());
		}
	}

	public void getManageMyWorkMarket(final ApiJSONPayloadMap paymentMap, final ManageMyWorkMarket configuration) {
		paymentMap.put("paymentTermsEnabled", configuration.getPaymentTermsDays() > 0);
		paymentMap.put("paymentTermsDays", configuration.getPaymentTermsDays());
	}

	public List<ApiJSONPayloadMap> getDetailsResultToMessagesModel(final WorkResponse workResponse) {
		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork().getNotes() == null) {
			return null;
		}
		return getThriftNotes(workResponse.getWork().getNotes());
	}

	public List<ApiJSONPayloadMap> getThriftNotes(final List<com.workmarket.thrift.core.Note> notes) {
		final List<ApiJSONPayloadMap> messageList = new LinkedList<>();
		for (com.workmarket.thrift.core.Note note : notes) {
			final ApiJSONPayloadMap message = getMessage(note);
			messageList.add(message);
		}
		return messageList;
	}

	private ApiJSONPayloadMap getMessage(final Note note) {
		final ApiJSONPayloadMap message = new ApiJSONPayloadMap();

		if (note.getCreatedOn() > 0) {
			message.put("createdDate", note.getCreatedOn());
		}
		message.put("text", note.getText());

		if (note.getCreator() != null) {
			//newMessage.put("createdById", note.getCreator().getId());
			message.put("createdByNumber", note.getCreator().getUserNumber());
			if (note.getCreator().getName() != null) {
				message.put("createdBy", note.getCreator().getName().getFullName());
			}
		}
		if (note.isIsPrivileged()) {
			message.put("visibility", PrivacyType.PRIVILEGED.name());
		}
		else if (note.isIsPrivate()) {
			message.put("visibility", PrivacyType.PRIVATE.name());
		}
		else {
			message.put("visibility", PrivacyType.PUBLIC.name());
		}
		if (note.getOnBehalfOf() != null) {
			message.put("onBehalfOf", note.getOnBehalfOf().getName().getFullName());
		}
		return message;
	}

	public List<ApiJSONPayloadMap> getDomainModelNotes(final List<com.workmarket.domains.model.note.Note> notes,
																										 final Map<Long, com.workmarket.domains.model.User> userMap) {

		final List<ApiJSONPayloadMap> messageList = new LinkedList<>();
		for (final com.workmarket.domains.model.note.Note note : notes) {
			final ApiJSONPayloadMap newMessage = getMessage(userMap, note);
			messageList.add(newMessage);
		}
		return messageList;
	}

	private ApiJSONPayloadMap getMessage(
			final Map<Long, com.workmarket.domains.model.User> userMap,
			final com.workmarket.domains.model.note.Note note) {

		final ApiJSONPayloadMap message = new ApiJSONPayloadMap();
		message.put("text", note.getContent());
		message.put("visibility", note.getPrivacy().name());

		if (note.getCreatedOn() != null) {
			message.put("createdDate", note.getCreatedOn().getTimeInMillis());
		}

		if (note.getCreatorId() != null) {
			com.workmarket.domains.model.User user = userMap.get(note.getCreatorId());
			if (user != null) {
				message.put("createdByNumber", user.getUserNumber());
				if (user.getFullName() != null) {
					message.put("createdBy", user.getFullName());
				}
			}
		}

		if (note.getOnBehalfFirstName() != null && note.getOnBehalfLastName() != null) {
			message.put("onBehalfOf", note.getOnBehalfFirstName() + note.getOnBehalfLastName());
		}
		return message;
	}

	public List<ApiJSONPayloadMap> getDetailsResultToAssetsModel(final WorkResponse workResponse) {
		if (workResponse == null || workResponse.getWork() == null || CollectionUtils.isEmpty(workResponse.getWork()
																																																	.getAssets())) {
			return null;
		}

		final List<ApiJSONPayloadMap> assetList = new LinkedList<>();
		for (final Asset asset : workResponse.getWork().getAssets()) {
			final ApiJSONPayloadMap assetMap = getAsset(asset);
			assetList.add(assetMap);
		}

		return assetList;
	}

	private ApiJSONPayloadMap getAsset(final Asset asset) {
		final ApiJSONPayloadMap assetMap = new ApiJSONPayloadMap();
		assetMap.put("id", asset.getId());
		assetMap.put("uuid", asset.getUuid());
		assetMap.put("name", asset.getName());
		assetMap.put("description", asset.getDescription());
		assetMap.put("mimeType", asset.getMimeType());
		assetMap.put("uri", asset.getUri());
		assetMap.put("type", asset.getType());
		assetMap.put("visibility", asset.getVisibilityCode());
		return assetMap;
	}

	public ApiJSONPayloadMap getDetailsResultToDeliverablesConfigurationModel(final WorkResponse workResponse) {

		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork()
						.getDeliverableRequirementGroupDTO() == null) {
			return null;
		}

		final DeliverableRequirementGroupDTO deliverables = workResponse.getWork().getDeliverableRequirementGroupDTO();

		final ApiJSONPayloadMap deliverableConfigMap = new ApiJSONPayloadMap();

		deliverableConfigMap.put("overview", deliverables.getInstructions());

		if (deliverables.getHoursToComplete() > 0) {
			deliverableConfigMap.put("hoursToComplete", deliverables.getHoursToComplete());
		}
		if (CollectionUtils.isNotEmpty(deliverables.getDeliverableRequirementDTOs())) {

			// holds references to all requirements for the assignment keyed by id, so we can easily add the deliverable
			// assets to them next.
			final Map<Long, ApiJSONPayloadMap> allRequirementsMap = new HashMap();

			final List requirements = new LinkedList();

			ApiJSONPayloadMap requirementMap;

			for (DeliverableRequirementDTO deliverable : deliverables.getDeliverableRequirementDTOs()) {

				requirementMap = new ApiJSONPayloadMap();

				putNonZeroIntoMap(requirementMap, "id", deliverable.getId());
				requirementMap.put("type", deliverable.getType());
				requirementMap.put("instructions", deliverable.getInstructions());
				requirementMap.put("requiredNumberOfFiles", deliverable.getNumberOfFiles());

				requirements.add(requirementMap);
				allRequirementsMap.put(deliverable.getId(), requirementMap);
			}

			// go through the deliverable assets, generate objects and add them to the deliverables list for the
			// associated requirement
			if (workResponse.getWork().getDeliverableAssets() != null) {

				for (final DeliverableAsset deliverableAsset : workResponse.getWork().getDeliverableAssets()) {

					final ApiJSONPayloadMap assetMap = getAsset(deliverableAsset);

					requirementMap = allRequirementsMap.get(deliverableAsset.getDeliverableRequirementId());
					if (requirementMap == null) {
						continue;
					}

					List deliverableList = (List) requirementMap.get("deliverables");

					if (deliverableList == null) {
						deliverableList = new LinkedList();
						requirementMap.put("deliverables", deliverableList);
					}
					deliverableList.add(assetMap);
				}
			}
			deliverableConfigMap.put("deliverableRequirements", requirements);
		}

		return deliverableConfigMap;
	}

	private ApiJSONPayloadMap getAsset(final DeliverableAsset deliverableAsset) {
		final ApiJSONPayloadMap assetMap = new ApiJSONPayloadMap();

		assetMap.put("id", deliverableAsset.getId());
		assetMap.put("name", deliverableAsset.getName());
		assetMap.put("description", deliverableAsset.getDescription());
		assetMap.put("position", deliverableAsset.getPosition());
		assetMap.put("uploadedBy", deliverableAsset.getUploadedBy());
		if (deliverableAsset.getUploadDate() > 0) {
			assetMap.put("uploadDate", deliverableAsset.getUploadDate());
		}
		assetMap.put("rejectedDate", deliverableAsset.getRejectedOn());
		assetMap.put("rejectionReason", deliverableAsset.getRejectionReason());
		assetMap.put("rejectedBy", deliverableAsset.getRejectedBy());
		assetMap.put("uuid", deliverableAsset.getUuid());
		assetMap.put("transformSmallUuid", deliverableAsset.getTransformSmallUuid());
		assetMap.put("transformLargeUuid", deliverableAsset.getTransformLargeUuid());
		assetMap.put("mimeType", deliverableAsset.getMimeType());
		assetMap.put("uri", deliverableAsset.getUri());
		return assetMap;
	}

	public List<ApiJSONPayloadMap> getDetailsResultToCustomFieldsModel(final WorkResponse workResponse) {
		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork()
						.getCustomFieldGroups() == null) {
			return null;
		}

		final List<ApiJSONPayloadMap> customFieldSets = new LinkedList<>();

		for (final CustomFieldGroup customFieldGroup : workResponse.getWork().getCustomFieldGroups()) {
			if (customFieldGroup.getFieldsSize() == 0) {
				continue;
			}
			final ApiJSONPayloadMap customFieldSetMap = getCustomFieldSet(customFieldGroup);
			customFieldSets.add(customFieldSetMap);
		}

		return customFieldSets;
	}

	private ApiJSONPayloadMap getCustomFieldSet(final CustomFieldGroup customFieldGroup) {
		final ApiJSONPayloadMap customFieldSetMap = new ApiJSONPayloadMap();
		putNonZeroIntoMap(customFieldSetMap, "id", customFieldGroup.getId());
		customFieldSetMap.put("name", customFieldGroup.getName());
		final Integer customFieldGroupPosition = customFieldGroup.getPosition();
		customFieldSetMap.put("position", customFieldGroupPosition != null ? customFieldGroupPosition.longValue() : 0L);

		final List<ApiJSONPayloadMap> customFields = new LinkedList<>();
		for (final CustomField customField : customFieldGroup.getFields()) {
			final ApiJSONPayloadMap fieldMap = getCustomField(customField);
			customFields.add(fieldMap);
		}
		customFieldSetMap.put("fields", customFields);
		return customFieldSetMap;
	}

	private ApiJSONPayloadMap getCustomField(final CustomField customField) {
		final ApiJSONPayloadMap fieldMap = new ApiJSONPayloadMap();

		if (customField.getId() > 0) {
			fieldMap.put("id", customField.getId());
		}
		fieldMap.put("name", customField.getName());
		fieldMap.put("type", customField.getType());
		fieldMap.put("workerEditable", !customField.isReadOnly());
		fieldMap.put("default", customField.getDefaultValue());
		fieldMap.put("required", customField.isIsRequired());
		fieldMap.put("value", customField.getValue());
		fieldMap.put("showOnPrintout", customField.isShowOnPrintout());
		fieldMap.put("showInAssignmentHeader", customField.isShowInAssignmentHeader());
		return fieldMap;
	}

	public ApiJSONPayloadMap getDetailsResultToContactsModel(final WorkResponse workResponse) {
		if (workResponse == null || workResponse.getWork() == null) {
			return null;
		}

		final ApiJSONPayloadMap contactsMap = new ApiJSONPayloadMap();

		if (workResponse.getWork().getSupportContact() != null) {
			contactsMap.put("supportContact", getUserToContactMap(workResponse.getWork().getSupportContact()));
		}
		if (workResponse.getWork().getBuyer() != null) {
			contactsMap.put("owner", getUserToContactMap(workResponse.getWork().getBuyer()));
		}

		return contactsMap;
	}

	public List<ApiJSONPayloadMap> getDetailsResultToNegotiationsModel(final WorkResponse workResponse, Long userId) {
		if (workResponse == null || workResponse.getWork() == null) {
			return null;
		}

		final List<ApiJSONPayloadMap> negotiations = new LinkedList<>();

		final Resource activeResource = workResponse.getWork().getActiveResource();

		if (activeResource != null) {
			if (activeResource.getExpenseNegotiation() != null) {
				negotiations.add(getNegotiationToApi(activeResource.getExpenseNegotiation(), EXPENSE_NEGOTIATION_TYPE));
			}
			if (activeResource.getBudgetNegotiation() != null) {
				negotiations.add(getNegotiationToApi(activeResource.getBudgetNegotiation(), BUDGET_NEGOTIATION_TYPE));
			}
			if (activeResource.getBonusNegotiation() != null) {
				negotiations.add(getNegotiationToApi(activeResource.getBonusNegotiation(), BONUS_NEGOTIATION_TYPE));
			}
			if (activeResource.getRescheduleNegotiation() != null) {
				negotiations.add(getNegotiationToApi(activeResource.getRescheduleNegotiation(), SCHEDULE_NEGOTIATION_TYPE));
			}
		}
		else if (userId != null) {
			final WorkNegotiation negotiation = workNegotiationService.findLatestByUserForWork(userId,
																																												 workResponse.getWork()
																																																 .getId());
			if (negotiation != null) {
				String negotiationType;
				if (negotiation.isPriceNegotiation()) {
					negotiationType = BUDGET_NEGOTIATION_TYPE;
				}
				else if (negotiation.isScheduleNegotiation()) {
					negotiationType = SCHEDULE_NEGOTIATION_TYPE;
				}
				else {
					negotiationType = APPLY_NEGOTIATION_TYPE;
				}

				negotiations.add(getNegotiationToApi(convertNegotiation(negotiation), negotiationType));
			}
		}

		if (workResponse.getBuyerRescheduleNegotiation() != null) {
			negotiations.add(getNegotiationToApi(workResponse.getBuyerRescheduleNegotiation(), SCHEDULE_NEGOTIATION_TYPE));
		}

		return negotiations;
	}

	private Negotiation convertNegotiation(final WorkNegotiation negotiation) {
		final Note note = convertNote(negotiation.getNote());
		final User requestedBy = convertUser(negotiation.getRequestedBy());
		final User approvedByUser = convertUser(negotiation.getApprovedBy());
		final Status approvalStatus = convertStatus(negotiation.getApprovalStatus());
		final PricingStrategy pricingStrategy = convertPricingStrategy(negotiation.getPricingStrategy());
		final Schedule schedule = convertSchedule(negotiation.getSchedule());
		return new Negotiation(negotiation.getId(),
													 negotiation.getEncryptedId(),
													 note,
													 requestedBy,
													 negotiation.getRequestedOn().getTimeInMillis(),
													 approvedByUser,
													 negotiation.getApprovedOn() == null ? 0L : negotiation.getApprovedOn().getTimeInMillis(),
													 approvalStatus,
													 negotiation.getExpiresOn() == null ? 0L : negotiation.getExpiresOn().getTimeInMillis(),
													 negotiation.getExpiresOn() != null,
													 negotiation.isPriceNegotiation(),
													 pricingStrategy,
													 null,
													 negotiation.isScheduleNegotiation(),
													 schedule,
													 approvalStatus,
													 negotiation.isInitiatedByResource(),
													 -1);
	}

	private Schedule convertSchedule(DateRange schedule) {
		if (schedule == null || (schedule.getFrom() == null && schedule.getThrough() == null)) {
			return null;
		}

		return new Schedule(schedule.getFrom() != null ? schedule.getFrom().getTimeInMillis() : -1,
												schedule.getThrough() != null ? schedule.getThrough().getTimeInMillis() : -1,
												schedule.getThrough() != null,
												-1);
	}

	private PricingStrategy convertPricingStrategy(com.workmarket.domains.model.pricing.PricingStrategy ps) {
		if (ps == null || ps.getFullPricingStrategy() == null) {
			return null;
		}

		final FullPricingStrategy fps = ps.getFullPricingStrategy();
		return new PricingStrategy(ps.getId(),
															 fps.getPricingStrategyType(),
															 fps.getFlatPrice() == null ? 0.0 : fps.getFlatPrice().doubleValue(),
															 fps.getMaxFlatPrice() == null ? 0.0 : fps.getMaxFlatPrice().doubleValue(),
															 fps.getPerHourPrice() == null ? 0.0 : fps.getPerHourPrice().doubleValue(),
															 fps.getMaxNumberOfHours() == null ? 0.0 : fps.getMaxNumberOfHours().doubleValue(),
															 fps.getPerUnitPrice() == null ? 0.0 : fps.getPerUnitPrice().doubleValue(),
															 fps.getMaxNumberOfUnits() == null ? 0.0 : fps.getMaxNumberOfUnits().doubleValue(),
															 fps.getInitialPerHourPrice() == null ? 0.0 : fps.getInitialPerHourPrice().doubleValue(),
															 fps.getInitialNumberOfHours() == null ? 0.0 : fps.getInitialNumberOfHours()
																			 .doubleValue(),
															 fps.getAdditionalPerHourPrice() == null ? 0.0 : fps.getAdditionalPerHourPrice()
																			 .doubleValue(),
															 fps.getMaxBlendedNumberOfHours() == null ? 0.0 : fps.getMaxBlendedNumberOfHours()
																			 .doubleValue(),
															 fps.getInitialPerUnitPrice() == null ? 0.0 : fps.getInitialPerUnitPrice().doubleValue(),
															 fps.getInitialNumberOfUnits() == null ? 0.0 : fps.getInitialNumberOfUnits()
																			 .doubleValue(),
															 fps.getAdditionalPerUnitPrice() == null ? 0.0 : fps.getAdditionalPerUnitPrice()
																			 .doubleValue(),
															 fps.getMaxBlendedNumberOfUnits() == null ? 0.0 : fps.getMaxBlendedNumberOfUnits()
																			 .doubleValue(),
															 -1,
															 fps.getAdditionalExpenses() == null ? 0.0 : fps.getAdditionalExpenses().doubleValue(),
															 fps.getBonus() == null ? 0.0 : fps.getBonus().doubleValue(),
															 fps.getOverridePrice() == null ? 0.0 : fps.getOverridePrice().doubleValue());
	}

	private Status convertStatus(ApprovalStatus approvalStatus) {
		if (approvalStatus != null) {
			return new Status(ApprovalStatus.lookupByCode(approvalStatus.getCode()).toString(), null, null);
		}
		return null;
	}

	private User convertUser(com.workmarket.domains.model.User requestedBy) {
		if (requestedBy == null) {
			return null;
		}
		final User user = new User();
		user.setName(new Name(requestedBy.getFirstName(), requestedBy.getLastName()));
		return user;
	}

	private Note convertNote(WorkNote note) {
		if (note == null) {
			return null;
		}

		final User user = new User();
		if (note.getCreatorId() != null) {
			final Map<String, Object> map = userService.getProjectionMapById(note.getCreatorId(), "firstName", "lastName");
			user.setName(new Name((String) map.get("firstName"), (String) map.get("lastName")));
		}

		final long createdOnInMillis = note.getCreatedOn().getTimeInMillis();
		return new Note(note.getId(), note.getContent(), note.getIsPrivate(), user, createdOnInMillis);
	}

	public ApiJSONPayloadMap getNegotiationToApi(final Negotiation negotiation, final String negotiationType) {

		if (negotiation == null) {
			return null;
		}

		final ApiJSONPayloadMap negotiationMap = new ApiJSONPayloadMap();

		putNonZeroIntoMap(negotiationMap, "id", negotiation.getId());
		negotiationMap.put("requestedBy", negotiation.getRequestedBy().getName().getFullName());
		putNonZeroIntoMap(negotiationMap, "requestedOn", negotiation.getRequestedOn());
		putNonZeroIntoMap(negotiationMap, "expiresOn", negotiation.getExpiresOn());
		negotiationMap.put("initiatedByResource", negotiation.isInitiatedByResource());
		if (negotiation.getApprovalStatus() != null) {
			negotiationMap.put("approvalStatus", negotiation.getApprovalStatus().getCode());
		}
		if (negotiation.getApprovedBy() != null) {
			negotiationMap.put("approvedBy", negotiation.getApprovedBy().getName().getFullName());
		}
		putNonZeroIntoMap(negotiationMap, "approvedOn", negotiation.getApprovedOn());
		if (negotiation.getNote() != null) {
			Note note = negotiation.getNote();
			ApiJSONPayloadMap noteMap = new ApiJSONPayloadMap();
			noteMap.put("text", note.getText());
			if (note.getCreator() != null && note.getCreator().getName() != null) {
				noteMap.put("createdBy", negotiation.getNote().getCreator().getName().getFullName());
			}
			negotiationMap.put("message", noteMap);
		}
		negotiationMap.put("type", negotiationType);

		if (BUDGET_NEGOTIATION_TYPE.equals(negotiationType) || BONUS_NEGOTIATION_TYPE.equals(negotiationType) || EXPENSE_NEGOTIATION_TYPE
						.equals(negotiationType)) {

			final ApiJSONPayloadMap pricingMap = new ApiJSONPayloadMap();
			final PricingStrategy pricing = negotiation.getPricing();

			putNonZeroIntoMap(pricingMap, "flatPrice", pricing.getFlatPrice());
			putNonZeroIntoMap(pricingMap, "perHour", pricing.getPerHourPrice());
			putNonZeroIntoMap(pricingMap, "maxHours", pricing.getMaxNumberOfHours());
			putNonZeroIntoMap(pricingMap, "initialHours", pricing.getInitialNumberOfHours());
			putNonZeroIntoMap(pricingMap, "perAdditionalHour", pricing.getAdditionalPerHourPrice());
			putNonZeroIntoMap(pricingMap, "maxAdditionalHours", pricing.getMaxBlendedNumberOfHours());
			putNonZeroIntoMap(pricingMap, "perUnit", pricing.getPerUnitPrice());
			putNonZeroIntoMap(pricingMap, "maxUnits", pricing.getMaxNumberOfUnits());
			putNonZeroIntoMap(pricingMap, "blendedPerHour", pricing.getInitialPerHourPrice());
			pricingMap.put("internal", pricing.getType() == PricingStrategyType.INTERNAL);
			putNonZeroIntoMap(pricingMap, "maxSpendLimit", pricing.getMaxSpendLimit());
			putNonZeroIntoMap(pricingMap, "additionalExpenses", pricing.getAdditionalExpenses());
			putNonZeroIntoMap(pricingMap, "bonus", pricing.getBonus());
			putNonZeroIntoMap(pricingMap, "overridePrice", pricing.getOverridePrice());

			negotiationMap.put("pricing", pricingMap);
		}

		if (SCHEDULE_NEGOTIATION_TYPE.equals(negotiationType) || negotiation.isIsScheduleNegotiation()) {

			final ApiJSONPayloadMap scheduleMap = new ApiJSONPayloadMap();

			final Schedule schedule = negotiation.getSchedule();

			if (schedule.isRange()) {
				putNonZeroIntoMap(scheduleMap, "startWindowBegin", schedule.getFrom());
				putNonZeroIntoMap(scheduleMap, "startWindowEnd", schedule.getThrough());
			}
			else {
				putNonZeroIntoMap(scheduleMap, "start", schedule.getFrom());
			}
			negotiationMap.put("schedule", scheduleMap);
		}

		return negotiationMap;
	}

	public ApiJSONPayloadMap getDetailsResultToTimeTrackingModel(final WorkResponse workResponse) {

		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork().getActiveResource() == null) {
			return null;
		}

		final ApiJSONPayloadMap timeTrackingMap = new ApiJSONPayloadMap();

		final Work work = workResponse.getWork();

		timeTrackingMap.put("checkInRequired",
												Boolean.valueOf(work.isCheckinCallRequired() || (work.getConfiguration() != null && work.getConfiguration()
																.isCheckinRequiredFlag())));
		timeTrackingMap.put("checkInCallRequired", Boolean.valueOf(work.isCheckinCallRequired()));
		timeTrackingMap.put("checkInRequiredFlag",
												Boolean.valueOf(work.getConfiguration() == null ? false : work.getConfiguration()
																.isCheckinRequiredFlag()));
		timeTrackingMap.put("checkInContactName", work.getCheckinContactName());
		timeTrackingMap.put("checkInContactPhone", work.getCheckinContactPhone());
		timeTrackingMap.put("showCheckOutNote", Boolean.valueOf(work.isShowCheckoutNotesFlag()));
		timeTrackingMap.put("checkOutNoteRequired", Boolean.valueOf(work.isCheckoutNoteRequiredFlag()));
		timeTrackingMap.put("checkOutNoteInstructions", work.getCheckoutNoteInstructions());

		if (workResponse.getWork().getActiveResource().getTimeTrackingLog() != null) {

			final List<ApiJSONPayloadMap> trackingEntries = new LinkedList<>();
			ApiJSONPayloadMap checkInOutPairMap;
			ApiJSONPayloadMap timeTrackingEntryMap;

			for (final TimeTrackingEntry ttEntry : workResponse.getWork().getActiveResource().getTimeTrackingLog()) {

				checkInOutPairMap = new ApiJSONPayloadMap();
				checkInOutPairMap.put("id", ttEntry.getId());

				// check in time tracking entry
				timeTrackingEntryMap = new ApiJSONPayloadMap();
				if (ttEntry.getCheckedInOn() > 0) {
					timeTrackingEntryMap.put("timestamp", ttEntry.getCheckedInOn());
				}
				if (ttEntry.getCheckedInBy() != null) {
					timeTrackingEntryMap.put("createdBy", ttEntry.getCheckedInBy().getId());
				}
				timeTrackingEntryMap.put("distance", ttEntry.getDistanceIn());
				checkInOutPairMap.put("checkIn", timeTrackingEntryMap);

				// check out time tracking entry
				timeTrackingEntryMap = new ApiJSONPayloadMap();
				if (ttEntry.getCheckedOutOn() > 0) {
					timeTrackingEntryMap.put("timestamp", ttEntry.getCheckedOutOn());
				}
				if (ttEntry.getCheckedOutBy() != null) {
					timeTrackingEntryMap.put("createdBy", ttEntry.getCheckedOutBy().getId());
				}
				timeTrackingEntryMap.put("distance", ttEntry.getDistanceOut());
				checkInOutPairMap.put("checkOut", timeTrackingEntryMap);

				trackingEntries.add(checkInOutPairMap);
			}
			timeTrackingMap.put("trackingEntries", trackingEntries);
		}

		return timeTrackingMap;
	}

	public List<ApiJSONPayloadMap> getDetailsResultToAssessmentsModel(final WorkResponse workResponse) {

		if (workResponse == null || workResponse.getWork() == null || CollectionUtils.isEmpty(workResponse.getWork()
																																																	.getAssessments())) {
			return null;
		}

		final List<ApiJSONPayloadMap> assessmentList = new LinkedList<>();
		final Map<Long, ApiJSONPayloadMap> assessmentsMap = new HashMap<>();

		ApiJSONPayloadMap assessmentMap;

		for (final Assessment assessment : workResponse.getWork().getAssessments()) {

			assessmentMap = new ApiJSONPayloadMap();

			assessmentMap.put("id", assessment.getId());
			assessmentMap.put("name", assessment.getName());
			assessmentMap.put("required", assessment.isIsRequired());

			assessmentList.add(assessmentMap);
			assessmentsMap.put(assessment.getId(), assessmentMap);
		}

		if (workResponse.getWork().getActiveResource() != null && workResponse.getWork()
						.getActiveResource()
						.getAssessmentAttempts() != null) {

			Attempt attempt;
			ApiJSONPayloadMap attemptMap;

			for (final AssessmentAttemptPair attemptPair : workResponse.getWork()
							.getActiveResource()
							.getAssessmentAttempts()) {

				if (attemptPair.getAssessment() == null || attemptPair.getLatestAttempt() == null) {
					continue;
				}

				assessmentMap = assessmentsMap.get(attemptPair.getAssessment().getId());
				if (assessmentMap == null) {
					continue;
				}

				attemptMap = new ApiJSONPayloadMap();
				attempt = attemptPair.getLatestAttempt();
				putNonZeroIntoMap(attemptMap, "id", attempt.getId());
				if (attempt.getStatus() != null) {
					attemptMap.put("status", attempt.getStatus().getCode());
				}
				attemptMap.put("respondedToAllItems", attempt.isRespondedToAllItems());
				assessmentMap.put("latestAttempt", attemptMap);
			}
		}

		return assessmentList;
	}

	public List<ApiJSONPayloadMap> getDetailsResultToShipmentsModel(final WorkResponse workResponse,
																																	final List<PartDTO> assignmentParts) {

		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork().getPartGroup() == null) {
			return null;
		}

		final List<ApiJSONPayloadMap> shipmentList = new LinkedList<>();

		final PartGroupDTO partGroup = workResponse.getWork().getPartGroup();

		ApiJSONPayloadMap shipmentMap ;

		boolean hasShippingParts = false;
		boolean hasReturnParts = false;

		if (CollectionUtils.isNotEmpty(assignmentParts)) {

			for (final PartDTO part : assignmentParts) {

				shipmentMap = new ApiJSONPayloadMap();

				shipmentMap.put("name", part.getName());
				shipmentMap.put("trackingNumber", part.getTrackingNumber());
				shipmentMap.put("shippingProvider", part.getShippingProvider().getCode());
				shipmentMap.put("value", part.getPartValue());
				if (part.isReturn()) {
					shipmentMap.put("shippingAddress", getLocationDTOToLocationModel(partGroup.getReturnToLocation()));
					shipmentMap.put("returnShipment", Boolean.TRUE);
					hasReturnParts = true;
				}
				else {
					shipmentMap.put("shippingAddress", getLocationDTOToLocationModel(partGroup.getShipToLocation()));
					shipmentMap.put("returnShipment", Boolean.FALSE);
					hasShippingParts = true;
				}
				shipmentList.add(shipmentMap);
			}
		}

		if (partGroup.hasShipToLocation() && !hasShippingParts) {
			shipmentMap = new ApiJSONPayloadMap();
			shipmentMap.put("shippingAddress", getLocationDTOToLocationModel(partGroup.getShipToLocation()));
			shipmentMap.put("returnShipment", Boolean.FALSE);
			shipmentList.add(shipmentMap);
		}

		if (partGroup.hasReturnToLocation() && !hasReturnParts) {
			shipmentMap = new ApiJSONPayloadMap();
			shipmentMap.put("shippingAddress", getLocationDTOToLocationModel(partGroup.getReturnToLocation()));
			shipmentMap.put("returnShipment", Boolean.TRUE);
			shipmentList.add(shipmentMap);
		}

		return shipmentList;
	}

	public List<ApiJSONPayloadMap> getDetailsResultToHeaderCustomFieldsModel(final WorkResponse workResponse,
																																					 final List<CustomField> customFields) {

		if (customFields == null) {
			return null;
		}

		final List<ApiJSONPayloadMap> headerCustomFields = new LinkedList<>();

		for (final CustomField customField : customFields) {

			final ApiJSONPayloadMap fieldMap = new ApiJSONPayloadMap();

			fieldMap.put("name", customField.getName());
			fieldMap.put("value", customField.getValue());

			headerCustomFields.add(fieldMap);
		}

		return headerCustomFields;
	}

	public List<ApiJSONPayloadMap> getDetailsResultToLabelsModel(final WorkResponse workResponse) {

		if (workResponse == null || workResponse.getWork() == null || workResponse.getWork().getSubStatuses() == null) {
			return null;
		}

		return getThriftSubStatuses(workResponse.getWork().getSubStatuses());
	}

	public List<ApiJSONPayloadMap> getThriftSubStatuses(final List<com.workmarket.thrift.work.SubStatus> subStatuses) {
		final List<ApiJSONPayloadMap> labelList = new LinkedList<>();
		for (final com.workmarket.thrift.work.SubStatus substatus : subStatuses) {
			final ApiJSONPayloadMap labelMap = getLabel(substatus);
			labelList.add(labelMap);
		}
		return labelList;
	}

	private ApiJSONPayloadMap getLabel(final SubStatus substatus) {
		final ApiJSONPayloadMap labelMap = new ApiJSONPayloadMap();
		labelMap.put("code", substatus.getCode());
		labelMap.put("description", substatus.getDescription());
		labelMap.put("note", substatus.getNote());
		labelMap.put("workerCanUpdate", substatus.isUserResolvable());
		labelMap.put("colorHexCode", substatus.getColorRgb());
		return labelMap;
	}

	public List<ApiJSONPayloadMap> getDomainModelSubStatuses(final List<com.workmarket.domains.work.model.state.WorkSubStatusType> subStatuses) {
		final List<ApiJSONPayloadMap> labelList = new LinkedList<>();
		for (final com.workmarket.domains.work.model.state.WorkSubStatusType substatus : subStatuses) {
			final ApiJSONPayloadMap labelMap = getLabel(substatus);
			labelList.add(labelMap);
		}
		return labelList;
	}

	private ApiJSONPayloadMap getLabel(final WorkSubStatusType substatus) {
		final ApiJSONPayloadMap labelMap = new ApiJSONPayloadMap();
		labelMap.put("id", substatus.getId());
		labelMap.put("code", substatus.getCode());
		labelMap.put("description", substatus.getDescription());
		labelMap.put("workerCanUpdate", substatus.isUserResolvable());
		labelMap.put("colorHexCode", substatus.getCustomColorRgb());
		return labelMap;
	}

	public ApiJSONPayloadMap getUserToContactMap(final User user) {
		if (user == null) {
			return null;
		}

		final ApiJSONPayloadMap contactMap = new ApiJSONPayloadMap();

		if (user.getName() != null) {
			contactMap.put("firstName", user.getName().getFirstName());
			contactMap.put("lastName", user.getName().getLastName());
		}
		contactMap.put("email", user.getEmail());
		contactMap.put("userNumber", user.getUserNumber());

		final Iterator<Phone> phoneIterator = user.getProfile().getPhoneNumbersIterator();

		if (phoneIterator != null && phoneIterator.hasNext()) {
			final List<ApiJSONPayloadMap> phoneNumberList = new LinkedList<>();

			while (phoneIterator.hasNext()) {
				final Phone phone = phoneIterator.next();
				final ApiJSONPayloadMap phoneMap = new ApiJSONPayloadMap();
				phoneMap.put("number", phone.getPhone());
				phoneMap.put("extension", phone.getExtension());
				phoneMap.put("type", phone.getType());
				phoneNumberList.add(phoneMap);
			}

			contactMap.put("phoneNumbers", phoneNumberList);
		}

		return contactMap;
	}

	public ApiJSONPayloadMap getLocationDTOToLocationModel(final LocationDTO locationDTO) {

		if (locationDTO == null) {
			return null;
		}

		final ApiJSONPayloadMap locationMap = new ApiJSONPayloadMap();

		locationMap.put("id", locationDTO.getId());
		locationMap.put("name", locationDTO.getName());
		locationMap.put("address1", locationDTO.getAddress1());
		locationMap.put("address2", locationDTO.getAddress2());
		locationMap.put("city", locationDTO.getCity());
		locationMap.put("state", locationDTO.getState());
		locationMap.put("postalCode", locationDTO.getPostalCode());
		locationMap.put("country", locationDTO.getCountry());

		if (locationDTO.getLongitude() != null && locationDTO.getLatitude() != null) {

			final ApiJSONPayloadMap coordinateMap = new ApiJSONPayloadMap();

			coordinateMap.put("latitude", locationDTO.getLatitude());
			coordinateMap.put("longitude", locationDTO.getLongitude());

			locationMap.put("coordinates", coordinateMap);
		}

		return locationMap;
	}
}
