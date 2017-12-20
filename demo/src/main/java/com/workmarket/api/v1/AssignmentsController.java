package com.workmarket.api.v1;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.ApiException;
import com.workmarket.api.v1.model.ApiAssignmentDetailsDTO;
import com.workmarket.api.v1.model.ApiAssignmentListItemDTO;
import com.workmarket.api.v1.model.ApiAttachmentDTO;
import com.workmarket.api.v1.model.ApiSendResultsDTO;
import com.workmarket.api.v1.model.ApiStatusDTO;
import com.workmarket.api.model.resolver.ApiArgumentResolver;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.domains.work.facade.service.WorkFacadeService;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.service.DocumentService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkQuestionService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.dto.WorkRatingDTO;
import com.workmarket.service.business.rating.RatingValidatingService;
import com.workmarket.service.business.wrapper.CloseWorkResponse;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.RatingException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.DeliverableAsset;
import com.workmarket.thrift.core.Industry;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Project;
import com.workmarket.thrift.work.RoutingStrategy;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.SerializationUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.helpers.ValidationMessageHelper;
import com.workmarket.web.models.MessageBundle;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = "Assignments")
@Controller("apiAssignmentsController")
@RequestMapping(value = {"/v1/employer/assignments", "/api/v1/assignments"})
public class AssignmentsController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(AssignmentsController.class);

	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private TWorkService tWorkService;
	@Autowired private WorkService workService;
	@Autowired private WorkReportService workReportService;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private ApiHelper apiHelper;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private AssetManagementService assetService;
	@Autowired private WorkQuestionService workQuestionService;
	@Autowired private ApiResponseBuilder apiResponseBuilder;
	@Autowired private ProfileService profileService;
	@Autowired private PricingService pricingService;
	@Autowired private AssignmentsListControllerHelper assignmentsListControllerHelper;
	@Autowired private DocumentService documentService;
	@Autowired private PartService partService;
	@Autowired private WorkFacadeService workFacadeService;
	@Autowired private RatingService ratingService;
	@Autowired private RatingValidatingService ratingValidatingService;


	// TODO: consolidate create/edit logic

	/**
	 * Edit assignments.
	 *
	 * @param workNumber                     is the work number of the existing assignment
	 * @param title                          is Assignment's title - required (per online doc)
	 * @param description                    is Assignment's description - required (per online doc)
	 * @param instructions                   is Assignment's instructions
	 * @param desiredSkills                  is Assignment's desired skills
	 * @param industryId                     is Industry ID - required (per online doc)
	 * @param ownerId                        - The assignment owner's Work Market user number
	 * @param ownerEmail                     - The assignment owner's Work Market login / email address. Can be used instead of owner_id.
	 * @param supportContactId               - The support contact's Work Market user number
	 * @param supportContactEmail            - The support contact's Work Market login / email address. Can be used instead of support_contact_id.
	 * @param clientId                       - Client company ID
	 * @param projectId                      - Project ID
	 * @param scheduledStart                 -  Start time of an assignment in Unix time - required (per online doc)
	 * @param scheduledStartDate             -  Start time of an assignment in Standard Date time - required either scheduledStart or scheduledStartDate
	 * @param scheduledEnd                   -  If the schedule is a range, end time of an assignment in Unix time
	 * @param scheduledEndDate               -  If the schedule is a range, end time of an assignment in Standard Date time
	 * @param pricingMode                    -  One of <b>spend</b> or <b>pay</b>
	 * @param pricingType                    -  One of <b>flat</b>, <b>per_hour</b>, <b>per_unit</b>, <b>blended_per_hour</b> or <b>internal</b> - required (per online doc)
	 * @param pricingFlatPrice               - pricing flat price
	 * @param pricingPerHourPrice            - pricing per hour price
	 * @param pricingMaxNumberOfHours        - pricing max number of hours
	 * @param pricingPerUnitPrice            - pricing per unit price
	 * @param pricingMaxNumberOfUnits        - pricing max number of units
	 * @param pricingInitialPerHourPrice     - pricing initial per hour price
	 * @param pricingInitialNumberOfHours    - pricing initial number of hours
	 * @param pricingAdditionalPerHourPrice  - pricing additional per hour price
	 * @param pricingMaxBlendedNumberOfHours - pricing max blended number of hours
	 * @param locationOffsite                - Whether or not the location is at an offsite location.
	 * @param locationId                     - By passing an id, an existing location will be associated with this assignment; if set, all other location fields are ignored.
	 * @param locationName                   - location name
	 * @param locationNumber                 - location number
	 * @param locationInstructions           - location instructions
	 * @param locationAddress1               - location address line 1
	 * @param locationAddress2               - location address line 2
	 * @param locationCity                   - location city
	 * @param locationState                  - 2-letter state code.
	 * @param locationZip                    - location zip
	 * @param locationCountry                - 3-letter country code. One of USA
	 * @param locationDressCode              - See Constants - Dress Codes (ex: 5)
	 * @param locationType                   - See Constants - Location Types (ex: 1)
	 * @param primaryLocationContact         - primary and secondary location contacts.
	 *                                       An assignment can have as many as two location contacts.
	 *                                       location_contacts[<N>][id] - By passing an id, an existing contact will
	 *                                       be associated with this assignment; if set,
	 *                                       all other location contact fields are ignored.
	 *                                       location_contacts[<N>][first_name] - ex: Shiba
	 *                                       location_contacts[<N>][last_name] - ex: Mayes
	 *                                       location_contacts[<N>][email] - ex: shiba@workmarket.com
	 *                                       location_contacts[<N>][phone] - ex: 2125559663
	 *                                       location_contacts[<N>][phone_extension] - ex: 123
	 * @param secondaryLocationContact       - same as primaryLocationContact, different index
	 * @param partGroup                      - Parts & Logistics
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Edit assignment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "{id}/edit", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> editAssignmentAction(@PathVariable(value = "id") String workNumber,
							 @RequestParam(value = "title", required = false) String title,
							 @RequestParam(value = "description", required = false) String description,
							 @RequestParam(value = "instructions", required = false) String instructions,
							 @RequestParam(value = "desired_skills", required = false) String desiredSkills,
							 @RequestParam(value = "industry_id", required = false) Long industryId,
							 @RequestParam(value = "owner_id", required = false) String ownerId,
							 @RequestParam(value = "owner_email", required = false) String ownerEmail,
							 @RequestParam(value = "support_contact_id", required = false) String supportContactId,
							 @RequestParam(value = "support_contact_email", required = false) String supportContactEmail,
							 @RequestParam(value = "client_id", required = false) Long clientId,
							 @RequestParam(value = "project_id", required = false) Long projectId,
							 @RequestParam(value = "scheduled_start", required = false) Long scheduledStart,
							 @RequestParam(value = "scheduled_start_date", required = false) String scheduledStartDate,
							 @RequestParam(value = "scheduled_end", required = false) Long scheduledEnd,
							 @RequestParam(value = "scheduled_end_date", required = false) String scheduledEndDate,
							 @RequestParam(value = "pricing_mode", required = false) String pricingMode,
							 @RequestParam(value = "pricing_type", required = false) String pricingType,
							 @RequestParam(value = "pricing_flat_price", required = false) Double pricingFlatPrice,
							 @RequestParam(value = "pricing_per_hour_price", required = false) Double pricingPerHourPrice,
							 @RequestParam(value = "pricing_max_number_of_hours", required = false) Double pricingMaxNumberOfHours,
							 @RequestParam(value = "pricing_per_unit_price", required = false) Double pricingPerUnitPrice,
							 @RequestParam(value = "pricing_max_number_of_units", required = false) Double pricingMaxNumberOfUnits,
							 @RequestParam(value = "pricing_initial_per_hour_price", required = false) Double pricingInitialPerHourPrice,
							 @RequestParam(value = "pricing_initial_number_of_hours", required = false) Double pricingInitialNumberOfHours,
							 @RequestParam(value = "pricing_additional_per_hour_price", required = false) Double pricingAdditionalPerHourPrice,
							 @RequestParam(value = "pricing_max_blended_number_of_hours", required = false) Double pricingMaxBlendedNumberOfHours,
							 @RequestParam(value = "location_offsite", required = false) String locationOffsite,
							 @RequestParam(value = "location_id", required = false) Long locationId,
							 @RequestParam(value = "location_name", required = false) String locationName,
							 @RequestParam(value = "location_number", required = false) String locationNumber,
							 @RequestParam(value = "location_instructions", required = false) String locationInstructions,
		@RequestParam(value = "location_address1", required = false) String locationAddress1,
							 @RequestParam(value = "location_address2", required = false) String locationAddress2,
							 @RequestParam(value = "location_city", required = false) String locationCity,
							 @RequestParam(value = "location_state", required = false) String locationState,
							 @RequestParam(value = "location_zip", required = false) String locationZip,
							 @RequestParam(value = "location_country", required = false) String locationCountry,
							 @RequestParam(value = "location_dress_code", required = false) String locationDressCode,
							 @RequestParam(value = "location_type", required = false) String locationType,
							 @RequestParam(value = "requirement_set_ids", required = false) Long[] requirementSetIds,
							 @ApiArgumentResolver("0") com.workmarket.thrift.core.User primaryLocationContact,
							 @ApiArgumentResolver("1") com.workmarket.thrift.core.User secondaryLocationContact,
							 @ApiArgumentResolver CustomFieldGroup[] customFieldGroups,
							 @ApiArgumentResolver PartGroupDTO partGroup) {

		MessageBundle bundle = messageHelper.newBundle();

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "id");
		}

		Work work;

		try {
			work = apiHelper.getDetailedWork(workNumber, false);
		}
		catch (ApiV1Exception e) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.create.error");
		}

		if (!CollectionUtilities.containsAny(
			work.getStatus().getCode(),
			WorkStatusType.DRAFT,
			WorkStatusType.SENT,
			WorkStatusType.DECLINED)) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.edit.invalid_status");
		}

		Long userId = authenticationService.getCurrentUser().getId();

		work.setTitle(StringUtilities.defaultString(title, work.getTitle()));
		work.setDescription(StringUtilities.defaultString(description, work.getDescription()));
		work.setInstructions(StringUtilities.defaultString(instructions, work.getInstructions()));
		work.setDesiredSkills(StringUtilities.defaultString(desiredSkills, work.getDesiredSkills()));

		if (industryId != null) {
			work.setIndustry(new Industry(industryId, null));
		} else {
			work.setIndustry(work.getIndustry());
		}

		PricingStrategy pricingStrategy;

		if (work.getPricing() == null) {
			pricingStrategy = new PricingStrategy();
			work.setPricing(pricingStrategy);
		} else {
			pricingStrategy = work.getPricing();
		}

		if (StringUtils.isNotBlank(pricingType)) {
			PricingStrategyType pricingStrategyType = apiHelper.getPricingStrategyType(pricingType);

			if ((pricingStrategyType != null) && apiHelper.hasValidPricingStrategy(pricingStrategyType)) {
				pricingStrategy.setId(apiHelper.getPricingStrategyId(pricingStrategyType));
			}
		}

		if (pricingFlatPrice != null) {
			pricingStrategy.setFlatPrice(pricingFlatPrice);
		}

		if (pricingPerHourPrice != null) {
			pricingStrategy.setPerHourPrice(pricingPerHourPrice);
		}

		if (pricingMaxNumberOfHours != null) {
			pricingStrategy.setMaxNumberOfHours(pricingMaxNumberOfHours);
		}

		if (pricingPerUnitPrice != null) {
			pricingStrategy.setPerUnitPrice(pricingPerUnitPrice);
		}

		if (pricingMaxNumberOfUnits != null) {
			pricingStrategy.setMaxNumberOfUnits(pricingMaxNumberOfUnits);
		}

		if (pricingInitialPerHourPrice != null) {
			pricingStrategy.setInitialPerHourPrice(pricingInitialPerHourPrice);
		}

		if (pricingInitialNumberOfHours != null) {
			pricingStrategy.setInitialNumberOfHours(pricingInitialNumberOfHours);
		}

		if (pricingAdditionalPerHourPrice != null) {
			pricingStrategy.setAdditionalPerHourPrice(pricingAdditionalPerHourPrice);
		}

		if (pricingMaxBlendedNumberOfHours != null) {
			pricingStrategy.setMaxBlendedNumberOfHours(pricingMaxBlendedNumberOfHours);
		}

		work.setPricing(pricingStrategy);

		if (work.getConfiguration() == null) {
			work.setConfiguration(new ManageMyWorkMarket());
		}

		work.getConfiguration().setUseMaxSpendPricingDisplayModeFlag("spend".equals(pricingMode));

		// NOTE We have to convert user numbers to ID
		Long buyerId = apiHelper.getUserIdFromNumberOrEmail(ownerId, ownerEmail);

		if (buyerId != null) {
			com.workmarket.thrift.core.User buyer = new com.workmarket.thrift.core.User();

			// Check if assignment editor and ownerEmail belong to the same company
			if (!apiHelper.isValidOwnerId(userId, buyerId)) {
				return apiResponseBuilder.createErrorResponse("api.v1.assignments.invalid.ownerEmail");
			}

			buyer.setId(buyerId);
			work.setBuyer(buyer);
		}

		if (StringUtils.isNotEmpty(supportContactEmail) || StringUtils.isNotEmpty(supportContactId)) {
			Long supportContactUserId = apiHelper.getUserIdFromNumberOrEmail(supportContactId, supportContactEmail);

			if (supportContactUserId != null) {
				com.workmarket.thrift.core.User supportContact = new com.workmarket.thrift.core.User();
				supportContact.setId(supportContactUserId);
				work.setSupportContact(supportContact);
			}
		}

		editLocation(
			work,
			locationOffsite,
			locationId,
			locationName,
			locationNumber,
			locationInstructions,locationAddress1,
			locationAddress2,
			locationCity,
			locationState,
			locationZip,
			locationCountry,
			locationDressCode,
			locationType
		);

		work.setLocationContact(primaryLocationContact);
		work.setSecondaryLocationContact(secondaryLocationContact);

		if (scheduledStart != null) {
			apiHelper.setWorkSchedule(scheduledStart, scheduledEnd, work);
		} else if (scheduledStartDate != null) {
			try {
				apiHelper.setWorkSchedule(scheduledStartDate, scheduledEndDate, work);
			}
			catch (ParseException ex) {
				logger.error("Unable to parse scheduling values", ex);
				messageHelper.addError(bundle, "api.v1.assignments.create.schedule_parse_error");
			}
		}

		if (projectId != null) {
			Project project = new Project();
			project.setId(projectId);
			work.setProject(project);
		}

		if (clientId != null) {
			com.workmarket.thrift.core.Company clientCompany = new com.workmarket.thrift.core.Company();
			clientCompany.setId(clientId);
			work.setClientCompany(clientCompany);
		}

		if (partGroup != null) {
			work.setPartGroup(partGroup);
		} else if (work.isSetPartGroup()) {
			work.getPartGroup().setParts(partService.getPartsByGroupUuid(work.getPartGroup().getUuid()));
		}

		if (customFieldGroups != null && customFieldGroups.length > 0) {
			work.setCustomFieldGroups(Arrays.asList(customFieldGroups));
		}

		if (requirementSetIds != null) {
			work.setRequirementSetIds(Arrays.asList(requirementSetIds));
		} else {
			work.setRequirementSetIds(new ArrayList<Long>());
		}

		WorkSaveRequest workSaveRequest = new WorkSaveRequest();
		workSaveRequest.setWork(work);
		workSaveRequest.setUserId(userId);

		try {
			tWorkFacadeService.saveOrUpdateWork(workSaveRequest);
		}
		catch (ValidationException ex) {
			BindingResult bindingResult = ThriftValidationMessageHelper.newBindingResult();
			ThriftValidationMessageHelper.rejectViolations(ex.getErrors(), bindingResult);
			messageHelper.setErrors(bundle, bindingResult);
		}
		catch (Exception ex) {
			logger.error("error saving assignment", ex);
			messageHelper.addError(bundle, "api.v1.assignments.create.error");
		}

		if (bundle.hasErrors()) {
			return ApiV1Response.of(false, bundle.getErrors());
		}

		return ApiV1Response.of(true);
	}

	/* Edit custom fields endpoint.  Unlike normal /edit endpoint, this allows
	updating regardless of assignment status.
	 */
	@ApiOperation(value = "Update custom fields")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "{id}/custom_fields", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> editAssignmentFieldsAction(@PathVariable(value = "id") String workNumber, @ApiArgumentResolver CustomFieldGroup[] groups) {
		MessageBundle bundle = messageHelper.newBundle();

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "id");
		}

		Work work;

		try {
			work = apiHelper.getDetailedWork(workNumber, false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.create.error");
		}

		Long userId = authenticationService.getCurrentUser().getId();

		work.setCustomFieldGroups(Arrays.asList(groups));
		WorkSaveRequest workSaveRequest = new WorkSaveRequest();
		workSaveRequest.setWork(work);
		workSaveRequest.setUserId(userId);

		try {
			tWorkService.saveCustomFields(workSaveRequest, work.getId());
		}
		catch (Exception ex) {
			logger.error("error updating custom fields", ex);
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.edit.error");
		}

		return ApiV1Response.of(true);
	}

	private void editLocation(Work work,
							String locationOffsite,
							Long locationId,
							String locationName,
							String locationNumber,
							String locationInstructions,
		String locationAddress1,
							String locationAddress2,
							String locationCity,
							String locationState,
							String locationZip,
							String locationCountry,
							String locationDressCode,
							String locationType) {

		List<Object> locationProperties = Lists.<Object>newArrayList(
			locationOffsite,
			locationId,
			locationName,
			locationNumber,
			locationInstructions,locationAddress1,
			locationAddress2,
			locationCity,
			locationState,
			locationZip,
			locationCountry,
			locationDressCode,
			locationType);

		boolean isUpdatingLocation = Collections.frequency(locationProperties, null) != locationProperties.size();

		if (isUpdatingLocation) {
			if ("1".equals(locationOffsite)) {
				work.setOffsiteLocation(true);
				work.setNewLocation(false);
				work.setLocation(null);

			} else if (locationId != null) {
				Location location = new Location();
				location.setId(locationId);
				location.setAddress(new Address());
				work.setLocation(location);
				work.setNewLocation(false);
				work.setOffsiteLocation(false);

			} else {
				// use existing and update or create new
				Location location = work.getLocation();
				if (location == null) {
					location = new Location();
				}

				location.setNumber(StringUtilities.defaultString(locationNumber, location.getNumber()));
				location.setName(StringUtilities.defaultString(locationName, location.getName()));
				location.setInstructions(StringUtilities.defaultString(locationInstructions, location.getInstructions()));

				Address address = (location.getAddress() == null ? new Address() : work.getLocation().getAddress());
				address.setAddressLine1(StringUtilities.defaultString(locationAddress1, address.getAddressLine1()));
				address.setAddressLine2(StringUtilities.defaultString(locationAddress2, address.getAddressLine2()));
				address.setCity(StringUtilities.defaultString(locationCity, address.getCity()));

				if (locationState != null) {
					address.setState(StringUtilities.defaultString(locationState.toUpperCase(), address.getState()));
				}

				if (locationZip != null) {
					address.setZip(StringUtilities.defaultString(StringUtils.remove(locationZip, " "), address.getZip()));
				}

				if (locationCountry != null) {
					address.setCountry(StringUtilities.defaultString(locationCountry.toUpperCase(), address.getCountry()));
				}

				address.setDressCode(StringUtilities.defaultString(locationDressCode, address.getDressCode()));
				address.setType(StringUtilities.defaultString(locationType, address.getType()));
				location.setAddress(address);
				work.setLocation(location);
				work.setNewLocation(true);
				work.setOffsiteLocation(false);
			}
		}
	}

	/**
	 * List assignments.
	 *
	 * @param status is WorkStatusType status - one of active, complete, paid, declined, sent, draft,
	 *               refunded, void, cancelled, exception, paymentPending or invoiced
	 * @param start  is pagination offset - defaults to 0
	 * @param limit  is pagination limit - defaults to 25
	 * @return ApiResponse
	 */
	@ApiOperation(value = "List assignments")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1Pagination<ApiAssignmentListItemDTO>> listAssignmentsAction(
		@RequestParam(value = "status", required = false) String status,
		@RequestParam(value = "labels", required = false) String[] labelIds,
		@RequestParam(value = "client_id", required = false) Long clientId,
		@RequestParam(value = "start", required = false, defaultValue = "0") int start,
		@RequestParam(value = "limit", required = false, defaultValue = "25") int limit,
		@RequestParam(value = "sort_dir", required = false, defaultValue = "ASC") String sortDir) {

		// Grab company id and user id.
		User currentUser = authenticationService.getCurrentUser();
		final Long companyId = currentUser.getCompany().getId();
		final Long userId = currentUser.getId();

		// See if we need to filter by status.
		if (StringUtilities.isNotEmpty(status)) {
			if (apiHelper.hasInternalStatus(status)) {
				status = apiHelper.getInternalStatus(status);
				logger.debug(
					"resolved to internal status to: \"{}\" on assignment list for companyId={} and userId={}",
					status,
					companyId,
					userId
				);
			}

			if (!apiHelper.hasValidStatus(status) && !WorkStatusType.AVAILABLE.equals(status) && !"all_declined".equals(status)) {
				status = WorkStatusType.EXCEPTION;

				logger.debug(
					"resolved invalid status to: \"{}\" on assignment list for companyId={} and userId={}",
					status,
					companyId,
					userId
				);
			}
		} else {
			status = WorkStatusType.INPROGRESS;
			logger.debug(
				"resolved status to: \"{}\" on assignment list for companyId={} and userId={}",
				status,
				companyId,
				userId
			);
		}

		// At this time we are only supporting buyer view.
		// String type = "managing";

		start = (start < 0) ? 0 : start;
		limit = ((limit <= 0) || (limit > Constants.MAX_ASSIGNMENT_LIST_RESULTS)) ? Constants.MAX_ASSIGNMENT_LIST_RESULTS : limit;

		logger.debug(
			"retrieving assignment list for companyId={} and userId={} using requested parameters: status={}, start={} and limit={}, sort_dir={}",
			companyId,
			userId,
			status,
			start,
			limit,
			sortDir
		);

		ApiV1Pagination<ApiAssignmentListItemDTO> pagination = assignmentsListControllerHelper.getAssignmentList(
			status,
			companyId,
			userId,
			start,
			limit,
			labelIds,
			clientId,
			sortDir
		);

		ApiV1Response<ApiV1Pagination<ApiAssignmentListItemDTO>> response = new ApiV1Response<>();
		response.setResponse(pagination);
		return response;
	}


	/**
	 * List updated assignments.
	 *
	 * @param status        is WorkStatusType status: one of active, complete, paid, declined, sent, draft,
	 *                      refunded, void, cancelled, exception, paymentPending or invoiced
	 * @param start         is pagination offset
	 * @param limit         is pagination limit
	 * @param modifiedSince is modification date of assignments in Unix time - required
	 * @return ApiResponse
	 */
	@ApiOperation(value = "List updated assignments")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/list_updated", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1Pagination<ApiAssignmentListItemDTO>> listUpdatedAction(
		@RequestParam(value = "status", required = false) String status,
		@RequestParam(value = "modified_since", required = false) Long modifiedSince,
		@RequestParam(value = "start", required = false, defaultValue = "0") int start,
		@RequestParam(value = "limit", required = false, defaultValue = "500") int limit,
		@RequestParam(value = "sort_dir", required = false, defaultValue = "ASC") String sortDir) {
		// Require a date cutoff
		if (modifiedSince == null) {
			throw new HttpException400("Missing modified_since parameter.");
		}

		Calendar twoMonthsAgo = DateUtilities.getMidnightNMonthsAgo(2);
		Calendar modifiedOn = Calendar.getInstance();
		modifiedOn.setTimeInMillis(modifiedSince * 1000L);

		if (modifiedOn.before(twoMonthsAgo)) {
			throw new HttpException400("modified_since parameter can not be more than 2 months ago.");
		}

		// Grab company id and user id.
		User currentUser = authenticationService.getCurrentUser();
		Long companyId = currentUser.getCompany().getId();
		Long userId = currentUser.getId();

		// See if we need to filter by status.
		status = isBlank(status) ? null : apiHelper.getApiStatusFilter(status);

		start = (start < 0) ? 0 : start;
		limit = ((limit <= 0) || (limit > 500)) ? 500 : limit;

		logger.debug(
			"list updated assignments for companyId={} and userId={} using requested parameters: status={}, modified_since={}, start={} and limit={}, sort_dir={}",
			companyId,
			userId,
			status,
			modifiedSince,
			start,
			limit,
			sortDir
		);

		ApiV1Pagination<ApiAssignmentListItemDTO> pagination = assignmentsListControllerHelper.getAssignmentListUpdated(status,
			companyId,
			userId,
			start,
			limit,
			modifiedSince,
			sortDir
		);

		return new ApiV1Response<>(pagination);
	}

	@ApiOperation(value = "Rate assignments")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/rate_assignment", method = POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> saveSinglePendingResourceRatingData(
			@RequestParam(value = "id") String workNumber,
			@RequestParam(value = "value", required = false) Integer value,
			@RequestParam(value = "quality", required = false) Integer quality,
			@RequestParam(value = "professionalism", required = false) Integer professionalism,
			@RequestParam(value = "communication", required = false) Integer communication,
			@RequestParam(value = "review", required = false) String review) {

		final Long raterUserId = authenticationService.getCurrentUserId();
		final Long workId = workService.findWorkId(workNumber);
		final Long ratedUserId = workService.findActiveWorkerId(workId);

		//check if you can even view the assignment

		try {
			apiHelper.getDetailedWork(workNumber, false);
		} catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		//check if there is an active resource to rate before making the rating of the active resource

		if (ratedUserId == null) {
			return apiResponseBuilder.createErrorResponse("assignment.add_rating.noresource");
		}

		final WorkRatingDTO workRatingDTO = new WorkRatingDTO(raterUserId, ratedUserId, workId, value, quality, professionalism,
				communication, review);

		try {
			ratingService.rateWork(workRatingDTO);
		} catch (RatingException ex) {
			return apiResponseBuilder.createErrorResponse(ex.getMessage());
		}
		return ApiV1Response.of(true);
	}

	/**
	 * List assignments with a specific type of pre-canned filter.
	 *
	 * @param start         is pagination offset
	 * @param limit         is pagination limit
	 * @param exceptionType is exception type
	 * @return ApiResponse
	 */
	@ApiOperation(value = "List exception-status assignments")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/list_exceptions", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1Pagination<ApiAssignmentListItemDTO>> listAssignmentExceptionsAction(
		@RequestParam(value = "start", required = false, defaultValue = "0") int start,
		@RequestParam(value = "limit", required = false, defaultValue = "500") int limit,
		@RequestParam(value = "exception_type", required = false) String exceptionType) {

		User currentUser = authenticationService.getCurrentUser();
		Long companyId = currentUser.getCompany().getId();
		Long userId = currentUser.getId();

		start = (start < 0) ? 0 : start;
		limit = ((limit <= 0) || (limit > 500)) ? 500 : limit;

		logger.debug(
			"list assignment exceptions for companyId={} and userId={} using requested parameters: exception_type={}, start={} and limit={}",
			companyId,
			userId,
			exceptionType,
			start,
			limit
		);

		WorkSearchDataPagination pagination = new WorkSearchDataPagination();
		pagination.setStartRow(start);
		pagination.setResultsLimit(limit);
		pagination.setSortColumn(WorkSearchDataPagination.SORTS.LAST_MODIFIED_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.ASC);
		pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.COMPANY_ID, String.valueOf(companyId));
		pagination.setShowAllCompanyAssignments(true);

		switch (exceptionType) {
			case "declined_reschedule_requests":
				pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, WorkStatusType.SENT);
				pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_TYPE, "reschedule");
				pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_INITIATED_BY_RESOURCE, "0");
				pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_APPROVAL_STATUS, "2");
				break;
			case "pending_reschedule_requests":
				pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, WorkStatusType.INPROGRESS);
				pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_TYPE, "reschedule");
				pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_INITIATED_BY_RESOURCE, "1");
				pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.NEGOTIATION_APPROVAL_STATUS, "0");
				break;
			default:
				throw new HttpException400("Invalid exception_type parameter.");
		}

		pagination = workReportService.generateWorkDashboardReportBuyer(companyId, userId, pagination);

		List<SolrWorkData> results = pagination.getResults();
		List<ApiAssignmentListItemDTO> assignments = apiHelper.buildAssignmentList(results);

		ApiV1Response<ApiV1Pagination<ApiAssignmentListItemDTO>> response = new ApiV1Response<>();
		response.setResponse(new ApiV1Pagination<>(
			pagination.getRowCount(),
			assignments.size(),
			start,
			limit,
			assignments
		));
		return response;
	}

	/**
	 * Returns a list of assignment statuses.
	 *
	 * @return ApiResponse
	 */
	@ApiOperation(value = "List statuses")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/statuses", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiStatusDTO>> listAssignmentStatusesAction() {

		List<ApiStatusDTO> statusDTOs = new LinkedList<>();

		for (Map.Entry<String, String> entry : apiHelper.getWorkStatusesForList().entrySet()) {
			String code = entry.getKey();
			String name = entry.getValue();

			if (apiHelper.hasInternalStatus(code)) {
				code = apiHelper.getInternalStatus(code);
			}

			statusDTOs.add(new ApiStatusDTO.Builder()
				.withId(code)
				.withName(name)
				.build());
		}

		return new ApiV1Response<>(statusDTOs);
	}

	@ApiOperation(value = "Add expense reimbursement")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = {"{id}/pricing/expense_reimbursement", "{id}/pricing/expense_reimbursement/"}, method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> reimburseExpensePostAction(@PathVariable("id") String workNumber, @RequestParam(value = "amount") Double amount) {

		if (amount <= 0) {
			return apiResponseBuilder.createErrorResponse("Size.reimbursementForm");
		}

		WorkResponse workResponse;

		try {
			workResponse = apiHelper.getWorkResponse(workNumber, null, false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		boolean isAdmin = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setAdditionalExpenses(amount);
		dto.setTimeZoneId(workResponse.getWork().getTimeZoneId());
		dto.setInitiatedByResource(!isAdmin);
		dto.setPreapproved(isAdmin);

		try {
			workNegotiationService.createExpenseIncreaseNegotiation(workResponse.getWork().getId(), dto);
		}
		catch (Exception ex) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.reimburse_expense.error");
		}

		return ApiV1Response.of(true);
	}

	@ApiOperation(value = "Add expense reimbursement", hidden = true)
	@RequestMapping(value = {"{id}/pricing/expense_reimbursement", "{id}/pricing/expense_reimbursement/"}, method = RequestMethod.PUT)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> reimburseExpensePutAction(@PathVariable("id") String workNumber, @RequestParam(value = "amount") Double amount) {
		return reimburseExpensePutAction(workNumber, amount);
	}

	/**
	 * Increase an assignment's budget.
	 *
	 * @param workNumber
	 * @param flatPrice
	 * @param maxNumberOfHours
	 * @param maxNumberOfUnits
	 * @param maxBlendedNumberOfHours
	 * @return
	 */
	@ApiOperation(value = "Add budget increase")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = {"{id}/pricing/budget_increase", "{id}/pricing/budget_increase/"}, method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> increaseBudget(@PathVariable("id") String workNumber,
															 @RequestParam(value = "flat_price", required = false) Double flatPrice,
															 @RequestParam(value = "max_number_of_hours", required = false) Double maxNumberOfHours,
															 @RequestParam(value = "max_number_of_units", required = false) Double maxNumberOfUnits,
															 @RequestParam(value = "max_blended_number_of_hours",
																						 required = false) Double maxBlendedNumberOfHours) {

		Work work;
		try {
			work = apiHelper.getWork(workNumber, buildPricingWorkRequestIncludes(), false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		Set<String> possibleTypes = new HashSet<>();
		PricingStrategy strategy = work.getPricing();

		if (null != flatPrice) {
			possibleTypes.add("FLAT");
			strategy.setFlatPrice(flatPrice);
		}

		if (null != maxNumberOfHours) {
			possibleTypes.add("PER_HOUR");
			strategy.setMaxNumberOfHours(maxNumberOfHours);
		}

		if (null != maxNumberOfUnits) {
			possibleTypes.add("PER_UNIT");
			strategy.setMaxNumberOfUnits(maxNumberOfUnits);
		}

		if (null != maxBlendedNumberOfHours) {
			possibleTypes.add("BLENDED_PER_HOUR");
			strategy.setMaxBlendedNumberOfHours(maxBlendedNumberOfHours);
		}

		if (possibleTypes.size() != 1) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.increase_budget.incompatible_attributes");
		}

		return increaseSpendLimit(
			workNumber,
			strategy.getType().toString(),
			null,
			strategy.getFlatPrice(),
			strategy.getPerHourPrice(),
			strategy.getMaxNumberOfHours(),
			strategy.getPerUnitPrice(),
			strategy.getMaxNumberOfUnits(),
			strategy.getInitialPerHourPrice(),
			strategy.getInitialNumberOfHours(),
			strategy.getAdditionalPerHourPrice(),
			strategy.getMaxBlendedNumberOfHours()
		);
	}

	/**
	 * Increase assignment spend limit. This will be deprecated soon.
	 * When this is deprecated, it will be replaced wit a prices/edit endpoint
	 * The new endpoint will not have a restriction of only being able to increase spend limits, but it will
	 * only be permitted on assignments not yet assigned (or without a workResource -- needs clarification
	 *
	 * @param workNumber
	 * @param type
	 * @param pricingMode
	 * @param flatPrice
	 * @param perHourPrice
	 * @param maxNumberOfHours
	 * @param perUnitPrice
	 * @param maxNumberOfUnits
	 * @param initialPerHourPrice
	 * @param initialNumberOfHours
	 * @param additionalPerHourPrice
	 * @param maxBlendedNumberOfHours
	 * @return
	 */
	@ApiOperation(value = "Increase spend limit")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@Deprecated
	@RequestMapping(value = "/increase_spendlimit", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> increaseSpendLimit(@RequestParam(value = "id", required = false) String workNumber,
																	 @RequestParam(value = "type", required = false) String type,
																	 @RequestParam(value = "pricing_mode", required = false) String pricingMode,
																	 @RequestParam(value = "flat_price", required = false) Double flatPrice,
																	 @RequestParam(value = "per_hour_price", required = false) Double perHourPrice,
																	 @RequestParam(value = "max_number_of_hours",
																								 required = false) Double maxNumberOfHours,
																	 @RequestParam(value = "per_unit_price", required = false) Double perUnitPrice,
																	 @RequestParam(value = "max_number_of_units",
																								 required = false) Double maxNumberOfUnits,
																	 @RequestParam(value = "initial_per_hour_price",
																								 required = false) Double initialPerHourPrice,
																	 @RequestParam(value = "initial_number_of_hours",
																								 required = false) Double initialNumberOfHours,
																	 @RequestParam(value = "additional_per_hour_price",
																								 required = false) Double additionalPerHourPrice,
																	 @RequestParam(value = "max_blended_number_of_hours",
																								 required = false) Double maxBlendedNumberOfHours) {

		MessageBundle bundle = messageHelper.newBundle();

		WorkResponse workResponse;
		try {
			workResponse = apiHelper.getWorkResponse(workNumber, buildPricingWorkRequestIncludes(), false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		PricingStrategyType strategyType = apiHelper.getPricingStrategyType(type);

		if ((strategyType != null) && apiHelper.hasValidPricingStrategy(strategyType)) {
			boolean isAdmin = workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);

			Work work = workResponse.getWork();
			WorkNegotiationDTO workDTO = new WorkNegotiationDTO();
			workDTO.setTimeZoneId(work.getTimeZoneId());
			workDTO.setInitiatedByResource(!isAdmin);
			workDTO.setPreapproved(isAdmin);
			workDTO.setUseMaxSpendPricingDisplayModeFlag("spend".equals(pricingMode));
			workDTO.setAdditionalExpenses(work.getPricing().getAdditionalExpenses());

			switch (strategyType) {
				case FLAT: {
					workDTO.setFlatPrice(flatPrice);
					workDTO.setPricingStrategyId(PricingStrategyType.getId(PricingStrategyType.FLAT));
				}
				break;

				case PER_HOUR: {
					workDTO.setPerHourPrice(perHourPrice);
					workDTO.setMaxNumberOfHours(maxNumberOfHours);
					workDTO.setPricingStrategyId(PricingStrategyType.getId(PricingStrategyType.PER_HOUR));
				}
				break;

				case PER_UNIT: {
					workDTO.setPerUnitPrice(perUnitPrice);
					workDTO.setMaxNumberOfUnits(maxNumberOfUnits);
					workDTO.setPricingStrategyId(PricingStrategyType.getId(PricingStrategyType.PER_UNIT));
				}
				break;

				case BLENDED_PER_HOUR: {
					workDTO.setInitialPerHourPrice(initialPerHourPrice);
					workDTO.setInitialNumberOfHours(initialNumberOfHours);
					workDTO.setAdditionalPerHourPrice(additionalPerHourPrice);
					workDTO.setMaxBlendedNumberOfHours(maxBlendedNumberOfHours);
					workDTO.setPricingStrategyId(PricingStrategyType.getId(PricingStrategyType.BLENDED_PER_HOUR));
				}
				break;
			}

			try {
				List<ConstraintViolation> violations = workService.repriceWork(work.getId(), workDTO);

				if (violations.isEmpty()) {
					return ApiV1Response.of(true);
				}

					for (ConstraintViolation violation : violations) {
						messageHelper.addError(bundle, String.format("work.%s", violation.getKey()), violation.getParams());
				}
			}
			catch (Exception ex) {
				messageHelper.addError(bundle, "api.v1.assignments.increase_budget.error");
			}
		} else {
			messageHelper.addError(bundle, "api.v1.assignments.invalid.strategy");
		}

		return ApiV1Response.of(false, bundle.getErrors(), HttpStatus.SC_BAD_REQUEST);
	}

	/**
	 * Adds document to assignment (specifying document visibility not supported)
	 *
	 * @param workNumber
	 * @param base64Attachment
	 * @param filename
	 * @param description
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "Add document")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/attachments/add", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiAttachmentDTO> addDocument(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "attachment", required = false) String base64Attachment,
		@RequestParam(value = "filename", required = false) String filename,
		@RequestParam(value = "description", required = false) String description) throws IOException {

		if (StringUtils.isEmpty(workNumber)) {
			return ApiV1Response.wrap(ApiAttachmentDTO.class, apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number"));
		}

		if (StringUtils.isEmpty(filename)) {
			return ApiV1Response.wrap(ApiAttachmentDTO.class, apiResponseBuilder.createErrorResponse("NotEmpty", "File name"));
		}

		if (StringUtils.isEmpty(base64Attachment)) {
			return ApiV1Response.wrap(ApiAttachmentDTO.class, apiResponseBuilder.createErrorResponse("NotEmpty", "Base64 encoded attachment"));
		}

		try {
			// Called for authorization validation side-effects
			apiHelper.getWorkResponse(workNumber, null, true);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return ApiV1Response.wrap(ApiAttachmentDTO.class, apiResponseBuilder.createErrorResponse(e));
		}

		File tempFile = File.createTempFile("api_work_attachment_" + workNumber, ".dat");

		try {
			int bytesCopied = SerializationUtilities.decodeBase64File(base64Attachment, tempFile);

			if (bytesCopied > Constants.MAX_UPLOAD_SIZE) {
				return ApiV1Response.wrap(ApiAttachmentDTO.class, apiResponseBuilder.createErrorResponse("assignment.add_attachment.sizelimit"));
			}

			ApiV1Response<ApiAttachmentDTO> apiResponse = new ApiV1Response<>();
			try {
				AssetDTO dto = new AssetDTO();
				dto.setMimeType(MimeTypeUtilities.guessMimeType(filename));
				dto.setName(filename);
				dto.setDescription(StringUtils.isNotEmpty(description) ? description : "Assignment Attachment");
				dto.setAssociationType(WorkAssetAssociationType.ATTACHMENT);
				dto.setFileByteSize(bytesCopied);
				dto.setSourceFilePath(tempFile.getAbsolutePath());
				WorkAssetAssociation workAssetAssociation = documentService.addDocument(workNumber, dto);

				// Pass nulls for everything except uuid here to honor pre-refactor v1 response schema...
				apiResponse.setResponse(new ApiAttachmentDTO.Builder()
					.withUuid(workAssetAssociation.getAsset().getUUID())
					.build()
				);
			}
			catch (Exception ex) {
				return ApiV1Response.wrap(ApiAttachmentDTO.class,apiResponseBuilder.createErrorResponse("assignment.add_attachment.exception"));
			}

			return apiResponse;
		}
		finally {
			FileUtils.deleteQuietly(tempFile);
		}
	}

	@ApiOperation(value = "List attachments")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/attachments/list", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiAttachmentDTO>> listAttachments(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "closeout", required = false) Boolean filterForDeliverables) {

		if (StringUtils.isEmpty(workNumber)) {
			ApiV1ResponseMeta meta = apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number").getMeta();
			ApiV1Response<List<ApiAttachmentDTO>> apiResponse = new ApiV1Response(meta);

			return apiResponse;
		}

		WorkResponse workResponse;

		try {
			workResponse = apiHelper.getWorkResponse(workNumber, Sets.newHashSet(WorkRequestInfo.ASSETS_INFO), true);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));

			ApiV1ResponseMeta meta = apiResponseBuilder.createErrorResponse(e).getMeta();
			ApiV1Response<List<ApiAttachmentDTO>> apiResponse = new ApiV1Response(meta);

			return apiResponse;
		}

		List<ApiAttachmentDTO> apiAttachmentDTOs = new LinkedList<>();
		Work work = workResponse.getWork();

		if (Boolean.TRUE.equals(filterForDeliverables) && work.isSetDeliverableAssets()) {
			for (DeliverableAsset deliverableAsset : work.getDeliverableAssets()) {
				// Pass nulls for some fields to honor pre-refactor v1 schema
				apiAttachmentDTOs.add(new ApiAttachmentDTO.Builder()
					.withUuid(deliverableAsset.getUuid())
					.withFilename(deliverableAsset.getName())
					.build()
				);
			}
		} else if (!Boolean.TRUE.equals(filterForDeliverables) && work.isSetAssets()) {  // use !TRUE instead of FALSE to capture null as well
			for (com.workmarket.thrift.core.Asset asset : work.getAssets()) {
				// Pass nulls for some fields to honor pre-refactor v1 schema
				apiAttachmentDTOs.add(new ApiAttachmentDTO.Builder()
					.withUuid(asset.getUuid())
					.withFilename(asset.getName())
					.build()
				);
			}
		}

		return new ApiV1Response<>(apiAttachmentDTOs);
	}

	@ApiOperation(value = "Get attachment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/attachments/get", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiAttachmentDTO> getAttachment(@RequestParam(value = "uuid", required = false) String uuid) throws IOException {

		if (StringUtils.isEmpty(uuid)) {
			return ApiV1Response.wrap(ApiAttachmentDTO.class, apiResponseBuilder.createErrorResponse("NotEmpty", "Uuid"));
		}

		Asset asset = assetService.findAssetByUuid(uuid);

		if (asset == null) {
			throw new HttpException404();
		}

		URL url;

		try {
			String assetUri = assetService.getAuthorizedUriByUuid(uuid);
			url = new URL(assetUri);
		}
		catch (HostServiceException ex) {
			//TODO switch to apiResponseBuilder
			logger.error("Unavailable URI for requested asset uuid={}", new Object[]{uuid}, ex);
			throw new HttpException404();
		}
		catch (MalformedURLException ex) {
			//TODO switch to apiResponseBuilder
			logger.error("Malformed URL for requested asset uuid={}", new Object[]{uuid}, ex);
			throw new HttpException404();
		}

		InputStream in = null;

		try {
			in = url.openStream();
			byte[] bytes = FileCopyUtils.copyToByteArray(in);
			String base64string = SerializationUtilities.encodeBase64(bytes);

			ApiAttachmentDTO apiAttachmentDTO = new ApiAttachmentDTO.Builder()
				.withFilename(asset.getName())
				.withDescription(asset.getDescription())
				.withSize(asset.getFileByteSize())
				.withAttachment(base64string)
				.build();

			return new ApiV1Response<>(apiAttachmentDTO);
		}
		catch (Exception ex) {
			logger.error("failed to get asset with uuid={} using URL={}", new Object[]{uuid, url}, ex);
			return ApiV1Response.wrap(ApiAttachmentDTO.class, apiResponseBuilder.createErrorResponse(401, "assignment.get_attachment.not_authorized"));
		}
		finally {
			if (in != null) {
				in.close();
			}
		}
	}

	@ApiOperation(value = "Remove attachment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/attachments/remove", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> removeAttachment(@RequestParam(value = "id", required = false) String workNumber,
																 @RequestParam(value = "uuid", required = false) String uuid) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		if (StringUtils.isEmpty(uuid)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Uuid");
		}

		Work work;
		try {
			work = apiHelper.getWork(workNumber, null, true);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		try {
			Asset asset = assetService.findAssetByUuid(uuid);

			if (asset == null) {
				throw new HttpException404();
			}

			assetService.removeAssetFromWork(asset.getId(), work.getId());
		}
		catch (Exception ex) {
			return apiResponseBuilder.createErrorResponse("assignment.remove_attachment.exception");
		}

		return apiResponseBuilder.standardResponse();
	}

	/**
	 * Get the details of an assignment.
	 *
	 * @param workNumber is work assignment number
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Get assignment details")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiAssignmentDetailsDTO> get(@RequestParam(value = "id", required = false) String workNumber) {

		if (StringUtils.isEmpty(workNumber)) {
			return ApiV1Response.wrap(ApiAssignmentDetailsDTO.class, apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number"));
		}

		Work work;

		try {
			work = apiHelper.getDetailedWork(workNumber, false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return ApiV1Response.wrap(ApiAssignmentDetailsDTO.class, apiResponseBuilder.createErrorResponse(e));
		}

		ApiAssignmentDetailsDTO assignmentDTO = apiHelper.buildAssignmentForApi(work);
		ApiV1Response<ApiAssignmentDetailsDTO> apiResponse = new ApiV1Response<>(assignmentDTO);

		return apiResponse;
	}

	/**
	 * Add note to assignment.
	 *
	 * @param workNumber is work Assignment number
	 * @param content    is note content
	 * @param isPrivate  is private note indicator
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Add message")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/add_note", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> addNote(@RequestParam(value = "id", required = false) String workNumber,
												@RequestParam(value = "content", required = false) String content,
												@RequestParam(value = "is_private", required = false, defaultValue = "false") boolean isPrivate,
												@RequestParam(value = "is_privileged",
																			required = false,
																			defaultValue = "false") boolean isPrivileged) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		if (StringUtils.isEmpty(content)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Note");
		}

		Long workId;
		try {
			workId = apiHelper.getWork(workNumber, null, false).getId();
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		// Construct note structure.
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.setContent(content);
		noteDTO.setIsPrivate(isPrivate);
		noteDTO.setPrivileged(isPrivileged);

		workNoteService.addNoteToWork(workId, noteDTO);

		return apiResponseBuilder.standardResponse();
	}

	/**
	 * Delete work.
	 *
	 * @param workNumber is a work assignment number.
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Delete assignment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> delete(@RequestParam(value = "id", required = false) String workNumber) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		MessageBundle bundle = messageHelper.newBundle();

		// Load assignment.
		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (work != null) {
			// Make sure it's a draft
			if (!WorkStatusType.DRAFT.equals(work.getWorkStatusType().getCode())) {
				messageHelper.addError(bundle, "api.v1.assignments.delete.invalid_status");
			} else if (work instanceof com.workmarket.domains.work.model.WorkTemplate) {
				messageHelper.addError(bundle, "api.v1.assignments.delete.invalid_type");
			}
			else
				if (!workService.deleteDraft(authenticationService.getCurrentUser().getId(), work.getId())) {
					messageHelper.addError(bundle, "api.v1.assignments.delete.error");

			}

			return ApiV1Response.of(true);
		} else {
			messageHelper.addError(bundle, "api.v1.assignments.invalid.workNumber");
		}

		return ApiV1Response.of(false, bundle.getErrors(), HttpStatus.SC_BAD_REQUEST);
	}

	/**
	 * Void work.
	 *
	 * @param workNumber is a work assignment number.
	 * @param message    is a note associated with voiding work assignment.
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Void assignment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/void", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> doVoid(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "note", required = false) String message) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		MessageBundle bundle = messageHelper.newBundle();

		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			messageHelper.addError(bundle, "api.v1.assignments.invalid.workNumber");
		} else {
			List<ConstraintViolation> violations = workFacadeService.voidWork(work.getId(), message);
			if (!violations.isEmpty()) {
				messageHelper.addError(bundle, "api.v1.assignments.void.error");
			}
		}

		if (bundle.hasErrors()) {
			return ApiV1Response.of(false, bundle.getErrors(), HttpStatus.SC_BAD_REQUEST);
		}

		return ApiV1Response.of(true);
	}

	/**
	 * Cancel work.
	 *
	 * @param workNumber is work assignment number
	 * @param note       is cancellation note
	 * @param amount     is cancellation amount
	 * @param reason     is cancellation reason code
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Cancel assignment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> cancel(@RequestParam(value = "id", required = false) String workNumber,
											 @RequestParam(value = "note", required = false) String note,
											 @RequestParam(value = "amount", required = false) Double amount,
											 @RequestParam(value = "reason", required = false) String reason) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		MessageBundle bundle = messageHelper.newBundle();

		// Load assignment.
		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			messageHelper.addError(bundle, "api.v1.assignments.invalid.workNumber");
		} else if (WorkStatusType.COMPLETE.equals(work.getWorkStatusType().getCode())) {
			messageHelper.addError(bundle, "api.v1.assignments.cancel.invalid_status_complete");
		} else if (!CollectionUtilities.containsAny(
			work.getWorkStatusType().getCode(),
			WorkStatusType.ACTIVE,
			WorkStatusType.INPROGRESS)) {
			messageHelper.addError(bundle, "api.v1.assignments.cancel.invalid_status");
		} else {
			if (StringUtils.isEmpty(reason)) {
				messageHelper.addError(bundle, "NotEmpty", "Reason");
			}

			if (amount == null || amount < 0) {
				messageHelper.addError(bundle, "api.v1.assignments.cancel.amount_empty");
			}

			if (!bundle.hasErrors()) {
				CancelWorkDTO cancelWorkDTO = new CancelWorkDTO();
				cancelWorkDTO.setWorkId(work.getId());
				cancelWorkDTO.setNote(note);
				cancelWorkDTO.setPrice(amount);
				cancelWorkDTO.setCancellationReasonTypeCode(reason);

				try {
					List<ConstraintViolation> violations = workFacadeService.cancelWork(cancelWorkDTO);
					if (!violations.isEmpty()) {
						messageHelper.addError(bundle, "api.v1.assignments.cancel.error");
					}
				}
				catch (Exception ex) {
					logger.error("error cancelling assignment with workId={}", new Object[]{work.getId()}, ex);
					messageHelper.addError(bundle, "api.v1.assignments.cancel.error");
				}
			}
		}

		if (bundle.hasErrors()) {
			return ApiV1Response.of(false, bundle.getErrors(), HttpStatus.SC_BAD_REQUEST);
		}

		return ApiV1Response.of(true);
	}

	/**
	 * Approve payment.
	 *
	 * @param workNumber is work assignment number
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Approve payment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/approve_payment", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> approvePayment(@RequestParam(value = "id", required = false) String workNumber) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		MessageBundle bundle = messageHelper.newBundle();
		Long workId;

		try {
			workId = apiHelper.getWork(workNumber, null, false).getId();
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		CloseWorkResponse response = null;
		try {
			response = workService.closeWork(workId);
			logger.warn("LOCK Released API approvePayment");
		}
		catch (Exception ex) {
			logger.warn("LOCK Released API approvePayment");
			logger.error("error approving payment for workId={}", new Object[]{workId}, ex);
			messageHelper.addError(bundle, "api.v1.assignments.approve_payment.error");
		}

		boolean isSuccessful = response != null && response.isSuccessful();
		if (isSuccessful) {
			messageHelper.setErrors(bundle, response);
		}

		if (bundle.hasErrors()) {
			return ApiV1Response.of(false, bundle.getErrors(), HttpStatus.SC_BAD_REQUEST);
		}

		return ApiV1Response.of(true);
	}

	/**
	 * Reject payment: transitions the assignment from "Pending Approval" status back to "In Progress"
	 *
	 * @param workNumber is work assignment number
	 * @param note       documenting for the resource why the assignment was not approved for payment
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Reject payment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/reject_payment", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> rejectPayment(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "note", required = false) String note) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		Long workId;
		try {
			workId = apiHelper.getWork(workNumber, null, false).getId();
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		try {
			workService.incompleteWork(workId, note);
		}
		catch (Exception ex) {
			logger.error("error rejecting payment for workId={}", new Object[]{workId}, ex);
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.reject_payment.error");
		}

		return apiResponseBuilder.standardResponse();
	}

	//XXX This should probably share the same code as WorkModalController.java
	@ApiOperation(value = "Request reschedule")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/reschedule", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> reschedule(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "scheduled_start", required = false) String scheduleStart,
		@RequestParam(value = "scheduled_end", required = false) String scheduleEnd) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		Work work;

		try {
			// Getting detailed work here for lack of a better approach at this time.  Probably not ideal.
			work = apiHelper.getDetailedWork(workNumber, false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		if (CollectionUtilities.containsAny(
			work.getStatus().getCode(),
			WorkStatusType.DRAFT,
			WorkStatusType.SENT,
			WorkStatusType.DECLINED)) {
			// Assignment still in draft/sent/declined, no need to create a negotiation
			try {
				apiHelper.setWorkSchedule(scheduleStart, scheduleEnd, work);
			}
			catch (ParseException ex) {
				logger.error("Unable to parse scheduling values", ex);
				return apiResponseBuilder.createErrorResponse("api.v1.assignments.create.schedule_parse_error");
			}

			WorkSaveRequest workSaveRequest = new WorkSaveRequest();
			workSaveRequest.setWork(work);
			workSaveRequest.setUserId(authenticationService.getCurrentUser().getId());

			try {
				tWorkFacadeService.saveOrUpdateWork(workSaveRequest);
			}
			catch (ValidationException ex) {
				return apiResponseBuilder.createErrorResponse("api.v1.assignments.reschedule.error");
			}
		} else {
			try {
				// Assignment is not still in draft/sent, so needs to be negotiated
				WorkNegotiationDTO workNegotiationDTO = new WorkNegotiationDTO();

				TimeZone timeZone = invariantDataService.findTimeZonesByTimeZoneId("GMT");
				Date scheduleStartDate = (scheduleStart == null) ? null : apiHelper.parseScheduleForWork(scheduleStart, work);
				Date scheduleEndDate = (scheduleEnd == null) ? null : apiHelper.parseScheduleForWork(scheduleEnd, work);

				if (scheduleStartDate != null) {
					workNegotiationDTO.setScheduleFromString(DateUtilities.getISO8601(scheduleStartDate));
				}

				if (scheduleEndDate != null) {
					workNegotiationDTO.setScheduleThroughString(DateUtilities.getISO8601(scheduleEndDate));
				}

				workNegotiationDTO.setIsScheduleRange((scheduleEndDate != null));

				workNegotiationDTO.setScheduleNegotiation(Boolean.TRUE);
				workNegotiationDTO.setTimeZoneId(timeZone.getId());
				workNegotiationService.createRescheduleNegotiation(work.getId(), workNegotiationDTO);
			}
			catch (ParseException ex) {
				logger.error("Unable to parse scheduling values", ex);
				return apiResponseBuilder.createErrorResponse("api.v1.assignments.create.schedule_parse_error");
			}
			catch (Exception ex) {
				return apiResponseBuilder.createErrorResponse("api.v1.assignments.reschedule.error");
			}
		}

		return apiResponseBuilder.standardResponse();
	}

	@ApiOperation(value = "Accept reschedule request")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/accept_reschedule", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> acceptReschedulePostAction(@RequestParam(value = "id", required = false) String workNumber) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		Work work;
		try {
			work = apiHelper.getWork(workNumber, buildNegotiationWorkRequestIncludes(), false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		if (null == work.getActiveResource() || null == work.getActiveResource().getRescheduleNegotiation()) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.negotiation.not_found");
		}

		Long negotiationId = work.getActiveResource().getRescheduleNegotiation().getId();

		return acceptNegotiationById(negotiationId);
	}

	@ApiOperation(value = "Accept reschedule request", hidden = true)
	@RequestMapping(value = "/accept_reschedule", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> acceptRescheduleGetAction(@RequestParam(value = "id", required = false) String workNumber) {
		return acceptReschedulePostAction(workNumber);
	}

	@ApiOperation(value = "Accept budget increase request")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/accept_budget_increase", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> acceptBudgetIncreasePostAction(@RequestParam(value = "id", required = false) String workNumber) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		Work work;
		try {
			work = apiHelper.getWork(workNumber, buildNegotiationWorkRequestIncludes(), false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		if (null == work.getActiveResource() || null == work.getActiveResource().getBudgetNegotiation()) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.negotiation.not_found");
		}

		Long negotiationId = work.getActiveResource().getBudgetNegotiation().getId();

		return acceptNegotiationById(negotiationId);
	}

	@ApiOperation(value = "Accept budget increase request", hidden = true)
	@RequestMapping(value = "/accept_budget_increase", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> acceptBudgetIncreaseGetAction(@RequestParam(value = "id", required = false) String workNumber) {
		return acceptBudgetIncreasePostAction(workNumber);
	}

	@ApiOperation(value = "Accept expense reimbursement")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/accept_expense_reimbursement", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> acceptExpenseReimbursementPostAction(@RequestParam(value = "id", required = false) String workNumber) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		Work work;
		try {
			work = apiHelper.getWork(workNumber, buildNegotiationWorkRequestIncludes(), false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		if (null == work.getActiveResource() || null == work.getActiveResource().getExpenseNegotiation()) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.negotiation.not_found");
		}

		Long negotiationId = work.getActiveResource().getExpenseNegotiation().getId();

		return acceptNegotiationById(negotiationId);
	}

	@ApiOperation(value = "Accept expense reimbursement", hidden = true)
	@RequestMapping(value = "/accept_expense_reimbursement", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> acceptExpenseReimbursementGetAction(@RequestParam(value = "id", required = false) String workNumber) {
		return acceptExpenseReimbursementPostAction(workNumber);
	}

	@ApiOperation(value = "Accept bonus request")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/accept_bonus", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> acceptBonusPostAction(@RequestParam(value = "id", required = false) String workNumber) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		Work work;
		try {
			work = apiHelper.getWork(workNumber, buildNegotiationWorkRequestIncludes(), false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		if (null == work.getActiveResource() || null == work.getActiveResource().getBonusNegotiation()) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.negotiation.not_found");
		}

		Long negotiationId = work.getActiveResource().getBonusNegotiation().getId();

		return acceptNegotiationById(negotiationId);
	}

	@ApiOperation(value = "Accept bonus request", hidden = true)
	@RequestMapping(value = "/accept_bonus", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> acceptBonusGetAction(@RequestParam(value = "id", required = false) String workNumber) {
		return acceptBonusPostAction(workNumber);
	}

	@ApiOperation(value = "Decline reschedule request")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/decline_reschedule", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> declineReschedulePostAction(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "reason", required = false) String declineNote) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		Work work;
		try {
			work = apiHelper.getWork(workNumber, buildNegotiationWorkRequestIncludes(), false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		if (null == work.getActiveResource() || null == work.getActiveResource().getRescheduleNegotiation()) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.negotiation.not_found");
		}

		Long negotiationId = work.getActiveResource().getRescheduleNegotiation().getId();

		return declineNegotiationById(negotiationId, declineNote);
	}

	@ApiOperation(value = "Decline reschedule request", hidden = true)
	@RequestMapping(value = "/decline_reschedule", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> declineRescheduleGetAction(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "reason", required = false) String declineNote) {

		return declineReschedulePostAction(workNumber, declineNote);
	}

	@ApiOperation(value = "Decline budget increase request")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/decline_budget_increase", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> declineBudgetIncreasePostAction(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "reason", required = false) String declineNote) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		Work work;
		try {
			work = apiHelper.getWork(workNumber, buildNegotiationWorkRequestIncludes(), false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		if (null == work.getActiveResource() || null == work.getActiveResource().getBudgetNegotiation()) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.negotiation.not_found");
		}

		Long negotiationId = work.getActiveResource().getBudgetNegotiation().getId();

		return declineNegotiationById(negotiationId, declineNote);
	}

	@ApiOperation(value = "Decline budget increase request")
	@RequestMapping(value = "/decline_budget_increase", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> declineBudgetIncreaseGetAction(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "reason", required = false) String declineNote) {

		return declineBudgetIncreasePostAction(workNumber, declineNote);
	}

	@ApiOperation(value = "Decline expense reimbursement request")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/decline_expense_reimbursement", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> declineExpenseReimbursementPostAction(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "reason", required = false) String declineNote) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		Work work;
		try {
			work = apiHelper.getWork(workNumber, buildNegotiationWorkRequestIncludes(), false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		if (null == work.getActiveResource() || null == work.getActiveResource().getExpenseNegotiation()) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.negotiation.not_found");
		}

		Long negotiationId = work.getActiveResource().getExpenseNegotiation().getId();

		return declineNegotiationById(negotiationId, declineNote);
	}

	@ApiOperation(value = "Decline expense reimbursement request", hidden = true)
	@RequestMapping(value = "/decline_expense_reimbursement", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> declineExpenseReimbursementGetAction(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "reason", required = false) String declineNote) {

		return declineExpenseReimbursementPostAction(workNumber, declineNote);
	}

	@ApiOperation(value = "Decline bonus request")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/decline_bonus", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> declineBonusPostAction(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "reason", required = false) String declineNote) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment number");
		}

		Work work;
		try {
			work = apiHelper.getWork(workNumber, buildNegotiationWorkRequestIncludes(), false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		if (null == work.getActiveResource() || null == work.getActiveResource().getBonusNegotiation()) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.negotiation.not_found");
		}

		Long negotiationId = work.getActiveResource().getBonusNegotiation().getId();

		return declineNegotiationById(negotiationId, declineNote);
	}

	@ApiOperation(value = "Decline bonus request", hidden = true)
	@RequestMapping(value = "/decline_bonus", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> declineBonusGetAction(
		@RequestParam(value = "id", required = false) String workNumber,
		@RequestParam(value = "reason", required = false) String declineNote) {

		return declineBonusPostAction(workNumber, declineNote);
	}

	/**
	 * Send your assignment to resources or groups
	 *
	 * @param workNumber            is the assignment work number
	 * @param groupIds              is a list of groups to route to
	 * @param radius                sets the max distance filter for group send
	 * @param assignToFirstToAccept - Indicates if the worker should have to apply, or if can they accept the assignment.
	 * @param workerIds             is a list of resources to route to
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Send assignment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "{id}/send", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiSendResultsDTO> send(
		@PathVariable(value = "id") String workNumber,
		@RequestParam(value = "group_id", required = false) Set<Long> groupIds,
		@RequestParam(value = "send_radius", required = false) Long radius,
		@RequestParam(value = "resource_id", required = false) Set<String> workerIds,
		@RequestParam(value = "assign_to_first_to_accept", required = false) boolean assignToFirstToAccept,
		@RequestParam(value = "auto_invite", required = false) Boolean autoInvite) throws IOException, ValidationException {

		Work work;

		if (CollectionUtils.isEmpty(groupIds) && CollectionUtils.isEmpty(workerIds) && Boolean.FALSE.equals(autoInvite)) {
			return ApiV1Response.wrap(ApiSendResultsDTO.class, apiResponseBuilder.createErrorResponse("api.v1.assignments.send.missing_paramaters"));
		}

		// Find the assignment
		try {
			work = apiHelper.getDetailedWork(workNumber, false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return ApiV1Response.wrap(ApiSendResultsDTO.class, apiResponseBuilder.createErrorResponse(e));
		}

		// Ensure assignment is in Draft, Sent, or Declined status
		if (!CollectionUtilities.containsAny(work.getStatus().getCode(),
																				 WorkStatusType.SENT,
																				 WorkStatusType.DRAFT,
																				 WorkStatusType.DECLINED)) {
			return ApiV1Response.wrap(ApiSendResultsDTO.class,apiResponseBuilder.createErrorResponse("api.v1.assignments.send.invalid_status"));
		}

		boolean successful = false;

		if (autoInvite != null) {
			work.getConfiguration().setSmartRoute(autoInvite);
		}

		Long userId = authenticationService.getCurrentUser().getId();
		WorkSaveRequest saveRequest = new WorkSaveRequest();

		saveRequest.setWork(work);
		saveRequest.setUserId(userId);

		MessageBundle bundle = messageHelper.newBundle();

		long num_sent = 0;
		boolean showNumberOfSentResources = false;

		// For group or auto routing strategy, we'll need to re-save the assignment
		if (CollectionUtils.isNotEmpty(groupIds) || Boolean.TRUE.equals(autoInvite)) {
			// If we got group ids, create routing strategies for them
			if (CollectionUtils.isNotEmpty(groupIds)) {
				showNumberOfSentResources = true;

				List<Long> invalidGroups = apiHelper.checkForInvalidGroups(groupIds);

				if (CollectionUtils.isNotEmpty(invalidGroups)) {

					ApiV1Response errorResponse = apiResponseBuilder.createErrorResponse("api.v1.assignments.send.invalid_group_id");
					ApiSendResultsDTO.Builder builder = new ApiSendResultsDTO.Builder();
					ApiV1ResponseMeta errorMeta = errorResponse.getMeta();

					builder.withInvalidGroups(invalidGroups)
						.withSuccessful(false);

					// ALL groups must be valid, or we fail
					return new ApiV1Response(builder.build(), errorMeta);
				}

				RoutingStrategy groupRouteStrategy = apiHelper.createRoutingStrategy(
					work,
					groupIds,
					radius,
					assignToFirstToAccept
				);
				saveRequest.addToRoutingStrategies(groupRouteStrategy);
				num_sent += apiHelper.calculateNumberOfEligibleResources(work, groupRouteStrategy.getFilter());
			}

			try {
				tWorkFacadeService.saveOrUpdateWork(saveRequest);

				// We consider this a success, even if attempts to send directly to resources fail later
				successful = true;
			}
			catch (Exception e) {
				logger.error("error saving with new routing strategy", e);
				if(e instanceof ValidationException) {
					throw e;
				}
				if(e instanceof StaleObjectStateException) {
					throw new ApiException("Work[" + workNumber + "] was simultaneously modified by another request - try this request again");
				}
				messageHelper.addError(bundle, "api.v1.assignments.send.error");
			}
		}

		ApiSendResultsDTO.Builder sendResultsBuilder = new ApiSendResultsDTO.Builder();

		// If we have worker IDs... add em'!
		if (CollectionUtils.isNotEmpty(workerIds)) {
			showNumberOfSentResources = true;
			Map<WorkAuthorizationResponse, List<String>> mapOfRoutingResults = apiHelper.sendToResources(
				work,
				workerIds,
				assignToFirstToAccept
			);

			if (mapOfRoutingResults != null && mapOfRoutingResults.containsKey(WorkAuthorizationResponse.SUCCEEDED)) {
				// We had at least 1 successful resource ID
				// Increase total num sent (must add to count from group too
				num_sent += mapOfRoutingResults.get(WorkAuthorizationResponse.SUCCEEDED).size();

				Set<String> sentResources = Sets.newHashSet(mapOfRoutingResults.get(WorkAuthorizationResponse.SUCCEEDED));

				// Now, let's look for failed send attempts
				if (sentResources.size() != workerIds.size()) {
					messageHelper.addError(bundle, "api.v1.assignments.send.resource_id_error");
				}
				successful = true;
			} else {
				// Something went wrong and zero resources were successful, put 'em all in failed
				if (mapOfRoutingResults == null) {
					mapOfRoutingResults = Maps.newHashMap();
					mapOfRoutingResults.put(WorkAuthorizationResponse.FAILED, Lists.newArrayList(workerIds));
				}
				messageHelper.addError(bundle, "api.v1.assignments.send.resource_id_error");
			}

			// Provide the outcomes for each resource that they tried to send to
			sendResultsBuilder.withMapOfRoutingResults(mapOfRoutingResults);

		}

		// Some special construction of the response since we can have "partial" failure
		ApiV1Response<ApiSendResultsDTO> apiResponse = new ApiV1Response<>();

		sendResultsBuilder.withSuccessful(successful);

		if (showNumberOfSentResources) {
			// this still does not match the actual number of eligible group send resources...
			sendResultsBuilder.withNumberOfResourcesSent(num_sent);
		}

		apiResponse.setResponse(sendResultsBuilder.build());

		if (bundle.hasErrors()) {
			apiResponse.getMeta().setErrorMessages(bundle.getErrors());
		}

		if (!successful) {
			apiResponse.getMeta().setStatusCode(HttpStatus.SC_BAD_REQUEST);
		}

		return apiResponse;
	}

	/**
	 * Answer question for an assignment
	 *
	 * @param questionId is the ID of the question being answered
	 * @param answer     is the body of the answer
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Answer question")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/questions/answer", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> answerQuestion(
		@RequestParam(value = "id", required = false) Long questionId,
		@RequestParam(value = "answer", required = false) String answer) {

		if (questionId == null) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Question ID");
		}

		if (StringUtils.isEmpty(answer)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Answer");
		}

		Optional<WorkQuestionAnswerPair> workQuestionAnswerPairOptional = workQuestionService.findQuestionAnswerPairById(
						questionId);

		if (!workQuestionAnswerPairOptional.isPresent()) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.questions.answer.invalid");
		}

		Long workId = workQuestionService.findWorkIdByQuestionId(questionId);
		AbstractWork work = workService.findWork(workId);

		if (work == null) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.questions.answer.error");
		}

		// authorize we can answer this question
		try {
			apiHelper.getWork(work.getWorkNumber(), null, false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}


																																							 Long userId =authenticationService.getCurrentUser()
																																											 .getId();
																																							 WorkQuestionAnswerPair response = workQuestionService.saveAnswerToQuestion(questionId, userId,answer,
																																							 workId);

		if (response == null) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.questions.answer.error");
		}

		return apiResponseBuilder.standardResponse();
	}

	/**
	 * Accept an offer
	 *
	 * @param negotiationId is the ID of the negotiation being accepted
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Accept offer/application")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/offers/accept", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> acceptOffer(@RequestParam(value = "id", required = false) Long negotiationId) {
		return acceptNegotiationById(negotiationId);
	}

	/**
	 * Decline an offer
	 *
	 * @param negotiationId is the ID of the negotiation being declined
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Decline offer/application")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/offers/decline", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> declineOffer(
		@RequestParam(value = "id", required = false) Long negotiationId,
		@RequestParam(value = "reason", required = false) String declineNote) {

		return declineNegotiationById(negotiationId, declineNote);
	}


	@ApiOperation(value = "Complete assignment for worker")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "{workNumber}/complete", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> complete(
		@PathVariable(value = "workNumber") String workNumber,
		@RequestParam(value = "resolution", required = false) String resolution,
		@RequestParam(value = "hours_worked", required = false) Double hoursWorked,
		@RequestParam(value = "units", required = false) Double units,
		@RequestParam(value = "override_additional_expenses", required = false) Double overrideExpenses,
		@RequestParam(value = "override_price", required = false) Double overridePrice,
		@RequestParam(value = "tax_rate", required = false) Double taxRate,
		@RequestParam(value = "approve_payment", required = false) Boolean approve) {

		if (StringUtils.isEmpty(workNumber)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Assignment ID");
		}

		if (StringUtils.isEmpty(resolution)) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Resolution");
		}

		Company company = profileService.findCompanyById(authenticationService.getCurrentUser().getCompany().getId());

		if (company.isSuspended()) {
			return apiResponseBuilder.createErrorResponse("assignment.complete.suspended");
		}

		Work work;
		// Find the assignment
		try {
			work = apiHelper.getDetailedWork(workNumber, false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		if (!work.getStatus().getCode().equals(WorkStatusType.ACTIVE)) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.send.invalid_status");
		}

		// Validate pricing
		com.workmarket.domains.model.pricing.PricingStrategy pricingStrategy =
			pricingService.findPricingStrategyById(work.getPricing().getId());
		if (pricingStrategy instanceof PerHourPricingStrategy || pricingStrategy instanceof BlendedPerHourPricingStrategy) {
			if (hoursWorked == null || hoursWorked == 0) {
				return apiResponseBuilder.createErrorResponse("hours_worked_required");
			}
		} else if (pricingStrategy instanceof PerUnitPricingStrategy) {
			if (units == null || units == 0) {
				return apiResponseBuilder.createErrorResponse("units_processed_required");
			}
		}

		// Validate additional expense override
		if (overrideExpenses != null && overrideExpenses > work.getPricing().getAdditionalExpenses()) {
			return apiResponseBuilder.createErrorResponse(
				"additional_expenses_exceeded",
				work.getPricing().getAdditionalExpenses());
		}

		// Validate existence of required custom fields
		if (work.getCustomFieldGroupsSize() > 0) {
			CustomFieldGroup fieldGroup = work.getCustomFieldGroupsIterator().next();
			if (fieldGroup.getFieldsSize() > 0) {
				Iterable<CustomField> resourceFields = Iterables.filter(fieldGroup.getFields(), new Predicate<CustomField>() {
					@Override
					public boolean apply(@Nullable CustomField field) {
						return field != null && field.isVisibleToResource();
					}
				});
				for (CustomField field : resourceFields) {
					if (field.isIsRequired() && org.apache.commons.lang3.StringUtils.isEmpty(field.getValue())) {
						return apiResponseBuilder.createErrorResponse("assignment.complete.custom_fields_missing");
					}
				}
			}
		}

		CompleteWorkDTO completeWork = new CompleteWorkDTO();
		completeWork.setAdditionalExpenses(
			NumberUtilities.firstNonZero(overrideExpenses,
			work.getPricing().getAdditionalExpenses())
		);
		completeWork.setBonus(work.getPricing().getBonus());
		completeWork.setResolution(resolution);

		// Taxes
		if (taxRate != null) {
			if (NumberUtilities.isWithinRange(taxRate, 0, 100)) {
				completeWork.setSalesTaxRate(taxRate);
				completeWork.setSalesTaxCollectedFlag(true);
			} else {
				return apiResponseBuilder.createErrorResponse("assignment.complete.invalidtax");
			}
		}

		if (pricingStrategy instanceof FlatPricePricingStrategy || pricingStrategy instanceof InternalPricingStrategy) {
			completeWork.setOverridePrice(overridePrice);
		} else if (pricingStrategy instanceof PerHourPricingStrategy || pricingStrategy instanceof BlendedPerHourPricingStrategy) {
			completeWork.setOverridePrice(overridePrice);
			completeWork.setHoursWorked(hoursWorked);
		} else if (pricingStrategy instanceof PerUnitPricingStrategy) {
			completeWork.setOverridePrice(overridePrice);
			completeWork.setUnitsProcessed(units);
		}

		Long activeResourceId = work.getActiveResource().getId();List<ConstraintViolation> completionResult = workService.completeWork(work.getId(),
																																					activeResourceId,
																																					completeWork);

		MessageBundle bundle = messageHelper.newBundle();
		BindingResult bindingResult = ThriftValidationMessageHelper.newBindingResult();

		if (completionResult.isEmpty()) {
			if (approve != null && approve) {
				return approvePayment(workNumber);
			}

			return apiResponseBuilder.standardResponse();
		}

		ValidationMessageHelper.rejectViolations(completionResult, bindingResult);
		messageHelper.setErrors(bundle, bindingResult);

		return ApiV1Response.of(false, bundle.getAllMessages());
	}


	private ApiV1Response<ApiV1ResponseStatus> acceptNegotiationById(Long negotiationId) {
		if (negotiationId == null) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Negotiation ID");
		}

		AbstractWorkNegotiation abstractWorkNegotiation;

		try {
			abstractWorkNegotiation = workNegotiationService.findById(negotiationId);
		}
		catch (Exception e) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.offers.accept.error");
		}

		// authorize we can approve this offer
		AbstractWork work = workService.findWork(abstractWorkNegotiation.getWork().getId(), false);

		if (work == null) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.offers.accept.error");
		}

		try {
			apiHelper.getWork(work.getWorkNumber(), null, false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		try {
			WorkNegotiationResponse response = workNegotiationService.approveNegotiation(negotiationId, null);
			if (!response.isSuccessful()) {
				return apiResponseBuilder.createErrorResponse("api.v1.assignments.offers.accept.error");
			}
		}
		catch (Exception e) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.offers.accept.error");
		}

		return apiResponseBuilder.standardResponse();
	}

	private ApiV1Response<ApiV1ResponseStatus> declineNegotiationById(Long negotiationId, String declineNote) {
		if (negotiationId == null) {
			return apiResponseBuilder.createErrorResponse("NotEmpty", "Negotiation ID");
		}

		AbstractWorkNegotiation abstractWorkNegotiation;

		try {
			abstractWorkNegotiation = workNegotiationService.findById(negotiationId);
		}
		catch (Exception e) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.offers.decline.error");
		}

		// authorize we can decline this offer
		AbstractWork work = workService.findWork(abstractWorkNegotiation.getWork().getId());

		if (work == null) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.offers.accept.error");
		}

		try {
			apiHelper.getWork(work.getWorkNumber(), null, false);
		}
		catch (ApiV1Exception e) {
			logger.error(apiHelper.getErrorDescription(e));
			return apiResponseBuilder.createErrorResponse(e);
		}

		try {
			WorkNegotiationResponse response = workNegotiationService.declineNegotiation(negotiationId, declineNote, null);

			if (!response.isSuccessful()) {
				return apiResponseBuilder.createErrorResponse("api.v1.assignments.offers.decline.error");
			}
		}
		catch (Exception e) {
			return apiResponseBuilder.createErrorResponse("api.v1.assignments.offers.decline.error");
		}

		return apiResponseBuilder.standardResponse();
	}

	private static Set<WorkRequestInfo> buildNegotiationWorkRequestIncludes() {
		Set<WorkRequestInfo> includes = Sets.newLinkedHashSet();
		includes.add(WorkRequestInfo.ACTIVE_RESOURCE_INFO);
		includes.add(WorkRequestInfo.ACTIVE_RESOURCE_NEGOTIATION_INFO);
		return includes;
	}

	private static Set<WorkRequestInfo> buildPricingWorkRequestIncludes() {
		return Sets.newHashSet(WorkRequestInfo.PRICING_INFO);
	}
}
