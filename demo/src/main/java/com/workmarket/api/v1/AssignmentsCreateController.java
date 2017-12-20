package com.workmarket.api.v1;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.model.resolver.ApiArgumentResolver;
import com.workmarket.api.v1.model.ApiAssignmentCreationResponseDTO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.service.EntityToObjectFactory;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Industry;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Project;
import com.workmarket.thrift.work.RoutingStrategy;
import com.workmarket.thrift.work.Template;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.WebUtilities;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.ThriftCustomFieldGroupHelper;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.models.MessageBundle;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = "Assignments")
@Controller("apiAssignmentsCreateController")
@RequestMapping(value = {"/v1/employer/assignments", "/api/v1/assignments"})
public class AssignmentsCreateController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(AssignmentsCreateController.class);

	@Autowired private ProfileService profileService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private ApiHelper apiHelper;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private DirectoryService directoryService;
	@Autowired private EntityToObjectFactory objectFactory;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private CustomFieldService customFieldService;

	/**
	 * Create assignments.
	 *
	 * @param templateId                     is template id (workNumber)
	 * @param title                          is Assignment's title - required (per online doc)
	 * @param autotaskId                     is autotask id number (optional)
	 * @param description                    is Assignment's description - required (per online doc)
	 * @param instructions                   is Assignment's instructions
	 * @param privateInstructions            is Assignment's instructions private
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
	 * @param locationOffsite - Whether or not the location is at an offsite location.
	 * @param locationId - By passing an id, an existing location will be associated with this assignment; if set, all other location fields are ignored.
	 * @param locationName - location name
	 * @param locationNumber - location number
	 * @param locationInstructions - location travel instructions
	 * @param locationAddress1 - location address line 1
	 * @param locationAddress2 - location address line 2
	 * @param locationCity - location city
	 * @param locationState - 2-letter state code.
	 * @param locationZip - location zip
	 * @param locationCountry - 3-letter country code. One of USA
	 * @param locationDressCode - See Constants - Dress Codes (ex: 5)
	 * @param locationType - See Constants - Location Types (ex: 1)
	 * @param primaryLocationContact - primary and secondary location contacts.
	 *                         An assignment can have as many as two location contacts.
	 *                         location_contacts[<N>][id] - By passing an id, an existing contact will
	 *                         								be associated with this assignment; if set,
	 *                         								all other location contact fields are ignored.
	 *                         location_contacts[<N>][first_name] - ex: Shiba
	 *                         location_contacts[<N>][last_name] - ex: Mayes
	 *                         location_contacts[<N>][email] - ex: shiba@workmarket.com
	 *                         location_contacts[<N>][phone] - ex: 2125559663
	 *                         location_contacts[<N>][phone_extension] - ex: 123
	 * @param secondaryLocationContact - same as primaryLocationContact, different index
	 * @param groups - Custom Fields: an assignment can have multiple custom field groups.
	 *                         custom_field_groups[<N>][id] - Custom field group identifier
	 *                         custom_field_groups[<N>][fields][<N>][id] - Custom field identifier
	 *                         custom_field_groups[<N>][fields][<N>][value] - Custom field value
	 * @param partGroup - Part group
	 * @param routingFilter - An assignment can be routed to multiple groups.
	 *                     		send_to_groups[<N>] - Group identifiers
	 * @param radius - maximum radius to send assignment (for group send)
	 * @param resourceIds - array of resource user numbers to send the assignment to
	 * @param assignToFirstToAccept - Indicates if the worker should have to apply, or if can they accept the assignment.
	 * @return ApiResponse
	 */
	@ApiOperation(value = "Create new assignment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiAssignmentCreationResponseDTO> create(
			@RequestParam(value="template_id", required=false) String templateId,
			@RequestParam(value="external_work_id", required = false) Long autotaskId,
			@RequestParam(value="title", required=false) String title,
			@RequestParam(value="description", required=false) String description,
			@RequestParam(value="instructions", required=false) String instructions,
			@RequestParam(value="private_instructions", required=false) Boolean privateInstructions,
			@RequestParam(value="desired_skills", required=false) String desiredSkills,
			@RequestParam(value="industry_id", required=false) Long industryId,
			@RequestParam(value="owner_id", required=false) String ownerId,
			@RequestParam(value="owner_email", required=false) String ownerEmail,
			@RequestParam(value="support_contact_id", required=false) String supportContactId,
			@RequestParam(value="support_contact_email", required=false) String supportContactEmail,
			@RequestParam(value="client_id", required=false) Long clientId,
			@RequestParam(value="project_id", required=false) Long projectId,
			@RequestParam(value="scheduled_start", required=false) Long scheduledStart,
			@RequestParam(value="scheduled_start_date", required = false) String scheduledStartDate,
			@RequestParam(value="scheduled_end", required=false) Long scheduledEnd,
			@RequestParam(value="scheduled_end_date", required = false) String scheduledEndDate,
			@RequestParam(value="pricing_mode", required=false) String pricingMode,
			@RequestParam(value="pricing_type", required=false) String pricingType,
			@RequestParam(value="pricing_flat_price", required=false) Double pricingFlatPrice,
			@RequestParam(value="pricing_per_hour_price", required=false) Double pricingPerHourPrice,
			@RequestParam(value="pricing_max_number_of_hours", required=false) Double pricingMaxNumberOfHours,
			@RequestParam(value="pricing_per_unit_price", required=false) Double pricingPerUnitPrice,
			@RequestParam(value="pricing_max_number_of_units", required=false) Double pricingMaxNumberOfUnits,
			@RequestParam(value="pricing_initial_per_hour_price", required=false) Double pricingInitialPerHourPrice,
			@RequestParam(value="pricing_initial_number_of_hours", required=false) Double pricingInitialNumberOfHours,
			@RequestParam(value="pricing_additional_per_hour_price", required=false) Double pricingAdditionalPerHourPrice,
			@RequestParam(value="pricing_max_blended_number_of_hours", required=false) Double pricingMaxBlendedNumberOfHours,
			@RequestParam(value="location_offsite", required=false) String locationOffsite,
			@RequestParam(value="location_id", required=false) Long locationId,
			@RequestParam(value="location_name", required=false) String locationName,
			@RequestParam(value="location_number", required=false) String locationNumber,
			@RequestParam(value="location_instructions", required=false) String locationInstructions,
			@RequestParam(value="location_address1", required=false) String locationAddress1,
			@RequestParam(value="location_address2", required=false) String locationAddress2,
			@RequestParam(value="location_city", required=false) String locationCity,
			@RequestParam(value="location_state", required=false) String locationState,
			@RequestParam(value="location_zip", required=false) String locationZip,
			@RequestParam(value="location_country", required=false) String locationCountry,
			@RequestParam(value="location_dress_code", required=false) String locationDressCode,
			@RequestParam(value="location_type", required=false) String locationType,
			@RequestParam(value="requirement_set_ids", required=false) Long[] requirementSetIds,
			@RequestParam(value="send_radius", required=false) Long radius,
			@RequestParam(value="resource_id", required=false) Set<String> resourceIds,
			@RequestParam(value="assign_to_first_to_accept", required=false) boolean assignToFirstToAccept,
			@RequestParam(value="auto_invite", required=false) Boolean autoInvite,
			@RequestParam(value="show_in_work_feed", required=false) Boolean showInWorkFeed,
			@RequestParam(value="payment_terms_days", required=false) Integer paymentTermsDays,
			@ApiArgumentResolver("0") User primaryLocationContact,
			@ApiArgumentResolver("1") User secondaryLocationContact,
			@ApiArgumentResolver CustomFieldGroup[] groups,
			@ApiArgumentResolver PartGroupDTO partGroup,
			@ApiArgumentResolver PeopleSearchRequest routingFilter,
			HttpServletRequest request) {

		ApiV1Response<ApiAssignmentCreationResponseDTO> apiResponse = new ApiV1Response<>();
		MessageBundle bundle = messageHelper.newBundle();
		Work baseWork = null;
		Long userId = authenticationService.getCurrentUser().getId();

		if (StringUtils.isNotEmpty(templateId)) {
			WorkRequest workRequest = new WorkRequest();
			workRequest.setUserId(userId);
			workRequest.setWorkNumber(templateId);
			workRequest.setIncludes(ImmutableSet.<WorkRequestInfo>builder()
				.add(WorkRequestInfo.CONTEXT_INFO)
				.add(WorkRequestInfo.STATUS_INFO)
				.add(WorkRequestInfo.INDUSTRY_INFO)
				.add(WorkRequestInfo.PROJECT_INFO)
				.add(WorkRequestInfo.CLIENT_COMPANY_INFO)
				.add(WorkRequestInfo.BUYER_INFO)
				.add(WorkRequestInfo.LOCATION_CONTACT_INFO)
				.add(WorkRequestInfo.SUPPORT_CONTACT_INFO)
				.add(WorkRequestInfo.LOCATION_INFO)
				.add(WorkRequestInfo.SCHEDULE_INFO)
				.add(WorkRequestInfo.PRICING_INFO)
				.add(WorkRequestInfo.ASSETS_INFO)
				.add(WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO)
				.add(WorkRequestInfo.WORKFLOW_INFO)
				.add(WorkRequestInfo.PARTS_INFO)
				.add(WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO)
				.add(WorkRequestInfo.PAYMENT_INFO)
				.add(WorkRequestInfo.REQUIRED_ASSESSMENTS_INFO)
				.add(WorkRequestInfo.REQUIREMENT_SET_INFO)
				.add(WorkRequestInfo.DELIVERABLES_INFO)
				.add(WorkRequestInfo.FOLLOWER_INFO)
				.build()
			);

			try {
				WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);
				baseWork = workResponse.getWork();
			} catch (Exception ex) {
				throw new HttpException404("The assignment template you are looking for does not exist.");
			}
		}

		Work work;
		WorkSaveRequest workSaveRequest = new WorkSaveRequest();

		if (baseWork == null) {
			work = new Work();
		} else {
			// need to make a copy of the template so it's id and workNumber don't change
			Template template = new Template().setId(baseWork.getId());
			work = baseWork;
			work.setId(0L)
					.setWorkNumber(null)
					.setTemplate(template);

			DeliverableRequirementGroupDTO deliverableRequirementGroupDTO = work.getDeliverableRequirementGroupDTO();
			if (deliverableRequirementGroupDTO != null) {
				deliverableRequirementGroupDTO.setId(null);

				List<DeliverableRequirementDTO> deliverableRequirementDTOs = deliverableRequirementGroupDTO.getDeliverableRequirementDTOs();
				if (CollectionUtils.isNotEmpty(deliverableRequirementDTOs)) {
					for (DeliverableRequirementDTO deliverableRequirementDTO : deliverableRequirementDTOs) {
						deliverableRequirementDTO.setId(null);
					}
				}
				work.setDeliverableRequirementGroupDTO(deliverableRequirementGroupDTO);
			}

			// must put the template ID in the save request as well or it isn't recorded in work_template_id field
			workSaveRequest.setTemplateId(template.getId());
		}

		work.setTitle(StringUtilities.defaultString(title, work.getTitle()));
		work.setDescription(StringUtilities.defaultString(description, work.getDescription()));
		work.setInstructions(StringUtilities.defaultString(instructions, work.getInstructions()));
		work.setDesiredSkills(StringUtilities.defaultString(desiredSkills, work.getDesiredSkills()));

		if (privateInstructions == null && work.getPrivateInstructions() == null) {
			work.setPrivateInstructions(Boolean.FALSE);
		} else {
			work.setPrivateInstructions(MoreObjects.firstNonNull(privateInstructions, work.getPrivateInstructions()));
		}

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

		Company userCompany = profileService.findCompany(userId);
		com.workmarket.domains.model.ManageMyWorkMarket mmw = userCompany.getManageMyWorkMarket();

		// Set payment terms days
		if (paymentTermsDays != null && userCompany.isPaymentTermsEnabled()) {
			work.getConfiguration().setPaymentTermsDays(paymentTermsDays);
		} else if (mmw != null && work.isSetConfiguration()) {
			// If not provided by the template, set the assignment's payments terms to the company default
			if (!work.getConfiguration().isSetPaymentTermsDays()) {
				work.getConfiguration().setPaymentTermsDays(mmw.getPaymentTermsDays());
			}
		}
		
		if (mmw != null && work.isSetConfiguration()) {
			// If not using a template, set resource assignment policy to company-wide setting
			if (work.getTemplate() == null) {
				work.getConfiguration().setAssignToFirstResource(mmw.getAssignToFirstResource());
			}
		}

		if (showInWorkFeed != null) {
			work.getConfiguration().setShowInFeed(showInWorkFeed);
		} else {
			// fall back to template value...
			work.getConfiguration().setShowInFeed(work.isShowInFeed());
		}

		// NOTE We have to convert user numbers to ID
		Long buyerId = apiHelper.getUserIdFromNumberOrEmail(ownerId, ownerEmail);

		if (buyerId == null) {
			buyerId = (work.getBuyer() != null) ? work.getBuyer().getId() : userId;
		}

		// Check if assignment creator and buyer belong to the same company
		if (!apiHelper.isValidOwnerId(userId,buyerId)) {
			messageHelper.addError(bundle, "api.v1.assignments.invalid.ownerEmail");
			apiResponse.getMeta().setErrorMessages(bundle.getErrors());
			return apiResponse;
		}

		User buyer = new User();
		buyer.setId(buyerId);
		work.setBuyer(buyer);

		if (StringUtils.isNotEmpty(supportContactEmail) || StringUtils.isNotEmpty(supportContactId)) {
			Long supportContactUserId = apiHelper.getUserIdFromNumberOrEmail(supportContactId, supportContactEmail);

			if (supportContactUserId != null) {
				User supportContact = new User();
				supportContact.setId(supportContactUserId);
				work.setSupportContact(supportContact);
			}
		}

		if ("1".equals(locationOffsite)) {
			work.setOffsiteLocation(true);
			work.setNewLocation(true);
		} else if (locationId != null) {
			// We need to get the zip of this existing location for use in group send
			// we just have to put it in the Address temporarily, it'll get overwritten on save
			com.workmarket.domains.model.Location mlocation = directoryService.findLocationById(locationId);
			Location location = objectFactory.newLocation(mlocation);
			work.setLocation(location);
			work.setNewLocation(false);
			work.setOffsiteLocation(false);
		} else if (StringUtils.isNotBlank(locationAddress1)) {
			Location location = new Location();
			location.setName(locationName);
			location.setNumber(locationNumber);
			location.setInstructions(locationInstructions);
			location.setAddress(new Address(
					locationAddress1,
					locationAddress2,
					locationCity,
					locationState.toUpperCase(),
					StringUtils.remove(locationZip, " "), // for international postal codes
					locationCountry.toUpperCase(),
					null,    // geo point
					locationDressCode,
					locationType
			));

			work.setLocation(location);
			work.setNewLocation(true);
			work.setOffsiteLocation(false);
		}

		work.setLocationContact(primaryLocationContact);
		work.setSecondaryLocationContact(secondaryLocationContact);

		if(scheduledStart != null) {
			apiHelper.setWorkSchedule(scheduledStart, scheduledEnd, work);
		} else if (scheduledStartDate != null) {
			try {
				apiHelper.setWorkSchedule(scheduledStartDate, scheduledEndDate, work);
			} catch (ParseException ex) {
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

		/* If any parts fields provided, we toss everything from the template parts config */
		work.setPartGroup(partGroup != null ? partGroup : work.getPartGroup());

		List<CustomFieldGroup> mergedGroupList = mergeRequestAndTemplateFieldGroups(groups, work.getCustomFieldGroups());

		work.setCustomFieldGroups(mergedGroupList);

		WorkCustomFieldGroup requiredGroup = customFieldService.findRequiredWorkCustomFieldGroup(userCompany.getId());
		if (requiredGroup != null) {
			ThriftCustomFieldGroupHelper.setRequiredThriftCustomFieldGroup(requiredGroup, work);
		}

		if (requirementSetIds != null) {
			work.setRequirementSetIds(Arrays.asList(requirementSetIds));
		} else {
			work.setRequirementSetIds(MoreObjects.firstNonNull(work.getRequirementSetIds(), new ArrayList<Long>()));
		}

		if (autoInvite != null) {
			work.getConfiguration().setSmartRoute(autoInvite);
		}

		workSaveRequest.setWork(work);
		workSaveRequest.setUserId(userId);

		RoutingStrategy strategy = new RoutingStrategy();

		List<Long> invalidGroups = null;

		if (routingFilter != null) {
			invalidGroups = apiHelper.checkForInvalidGroups(routingFilter.getGroupFilter());

			if (CollectionUtils.isNotEmpty(invalidGroups)) {
				// Notify user we had invalid groups
				messageHelper.addError(bundle, "api.v1.assignments.send.invalid_group_id");
			}

			strategy = apiHelper.createRoutingStrategy(work, routingFilter, radius, assignToFirstToAccept);
			workSaveRequest.setRoutingStrategies(Arrays.asList(strategy));

			// overwrite original routing filter with one we updated via createRoutingStrategy
			routingFilter = strategy.getFilter();
		}

		WorkResponse workResponse = null;

		try {
			workResponse = tWorkFacadeService.saveOrUpdateWork(workSaveRequest);
		} catch (ValidationException ex) {
			BindingResult bindingResult = ThriftValidationMessageHelper.newBindingResult();
			ThriftValidationMessageHelper.rejectViolations(ex.getErrors(), bindingResult);
			messageHelper.setErrors(bundle, bindingResult);
		} catch (Exception ex) {
			logger.error("error creating new assignment", ex);
			messageHelper.addError(bundle, "api.v1.assignments.create.error");
		}

		String workNumber = null;
		Long numSent = null;
		String requestURL = null;
		Map<WorkAuthorizationResponse, List<String>> mapOfRoutingResults = null;

		if (workResponse != null && workResponse.isSetWork()) {
			// In order to tell the user how many resources we sent this to, we have to replicate the group
			// send eligibility logic with another PeopleSearch request... not great, but that's what we do.
			if (routingFilter != null) {
				// set the new ID so we can just pass this to the helper
				work.setId(workResponse.getWork().getId());
				numSent = apiHelper.calculateNumberOfEligibleResources(work, strategy.getFilter());
			}

			// handle integration actions
			if (!bundle.hasErrors()) {
				workNumber = workResponse.getWork().getWorkNumber();
				webHookEventService.onWorkCreated(workResponse.getWork().getId(), userCompany.getId(), autotaskId);
			}

			// If we have worker IDs... add em'!
			if (CollectionUtils.isNotEmpty(resourceIds)) {
				mapOfRoutingResults = apiHelper.sendToResources(workResponse.getWork(), resourceIds, assignToFirstToAccept);

				 if (mapOfRoutingResults != null && mapOfRoutingResults.containsKey(WorkAuthorizationResponse.SUCCEEDED)) {
					 // We had at least 1 successful resource ID
					 // Increase total num sent (must add to count from group too)
					 if (numSent == null) {
						 numSent = (long) mapOfRoutingResults.get(WorkAuthorizationResponse.SUCCEEDED).size();
					 } else {
						 numSent += mapOfRoutingResults.get(WorkAuthorizationResponse.SUCCEEDED).size();
					 }

					 Set<String> sentResources = Sets.newHashSet(mapOfRoutingResults.get(WorkAuthorizationResponse.SUCCEEDED));

					 // Now, let's look for failed send attempts
					 if (sentResources.size() != resourceIds.size()) {
						 messageHelper.addError(bundle, "api.v1.assignments.send.resource_id_error");
					 }
				} else {
					 // Something went wrong and zero resources were successful, put 'em all in failed
					 if (mapOfRoutingResults == null) {
						 mapOfRoutingResults = Maps.newHashMap();
						mapOfRoutingResults.put(WorkAuthorizationResponse.FAILED, Lists.newArrayList(resourceIds));
					 }
					 messageHelper.addError(bundle, "api.v1.assignments.send.resource_id_error");
				}
			}
		}

		if (MediaType.TEXT_HTML_VALUE.equals(request.getContentType()) ||
				WebUtilities.isPageRequestedAsHtml(request)) {
			try {
				requestURL = RequestUtils.absoluteURL(request, "").toString();
				apiResponse.getMeta().setHtmlTemplate("/template/html/ApiCreateWorkResponseTemplate.vm");
			} catch (MalformedURLException ex) {
				logger.error("Unable to parse URL", ex);
			}
		}

		ApiAssignmentCreationResponseDTO.Builder builder = new ApiAssignmentCreationResponseDTO.Builder()
			.withId(workNumber)
			.withRequestURL(requestURL);

		builder.withMapOfRoutingResults(mapOfRoutingResults)
			.withInvalidGroups(invalidGroups)
			.withNumberOfResourcesSent(numSent);

		apiResponse.setResponse(builder
			.build()
		);

		if (bundle.hasErrors()) {
			apiResponse.getMeta().setErrorMessages(bundle.getErrors());
			apiResponse.getMeta().setStatusCode(HttpStatus.SC_BAD_REQUEST);
		}

		return apiResponse;
	}

	public List<CustomFieldGroup> mergeRequestAndTemplateFieldGroups(CustomFieldGroup[] requestCustomFieldGroups, List<CustomFieldGroup> templateCustomFieldGroups) {
		/* Start with the request-based custom field groups (highest priority) */
		List<CustomFieldGroup> mergedCustomFieldGroupList = new ArrayList<>(Arrays.asList(requestCustomFieldGroups));
		if (CollectionUtils.isEmpty(templateCustomFieldGroups)) {
			return mergedCustomFieldGroupList;
		}

		/* Add in any template-based custom field groups that weren't already overridden */
		for (CustomFieldGroup templateGroup : templateCustomFieldGroups) {
			boolean isInMergedGroup = false;

			for (CustomFieldGroup mergedGroup : mergedCustomFieldGroupList) {
				if (templateGroup.getId() == mergedGroup.getId()) {
					isInMergedGroup = true;
				}
			}

			if (!isInMergedGroup) {
				/* Template group not overridden via request, so add template group to merged set */
				for (CustomField field : templateGroup.getFields()) {
					/* Set the field value to the default value if it's currently blank (to mimick front-end behavior) */
					if (field.getValue().isEmpty() && !field.getDefaultValue().isEmpty()) {
						field.setValue(field.getDefaultValue());
					}
				}
				mergedCustomFieldGroupList.add(templateGroup);
			}
		}
		return mergedCustomFieldGroupList;
	}

}
