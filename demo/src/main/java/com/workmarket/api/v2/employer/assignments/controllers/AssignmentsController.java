package com.workmarket.api.v2.employer.assignments.controllers;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.AvailableFundsApiDTO;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;
import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentAvailableFundsService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentConfigurationService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentCustomFieldGroupsService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentDeliverablesService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentDocumentsService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentLocationService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentPricingService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentRoutingService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentScheduleService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentShipmentsService;
import com.workmarket.api.v2.employer.assignments.services.AssignmentSurveysService;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.api.v2.model.SurveyDTO;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.dto.ClientCompanyDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@Api(tags = {"Assignments"})
@RequestMapping(value = {"/v2/employer/assignments", "/employer/v2/assignments"})
public class AssignmentsController extends ApiBaseController {
	@Autowired private AssignmentService assignmentService;
	@Autowired private AssignmentScheduleService assignmentScheduleService;
	@Autowired private AssignmentPricingService assignmentPricingService;
	@Autowired private AssignmentLocationService assignmentLocationService;
	@Autowired private AssignmentRoutingService assignmentRoutingService;
	@Autowired private AssignmentCustomFieldGroupsService assignmentCustomFieldGroupsService;
	@Autowired private AssignmentShipmentsService assignmentShipmentsService;
	@Autowired private AssignmentSurveysService assignmentSurveysService;
	@Autowired private AssignmentConfigurationService assignmentConfigurationService;
	@Autowired private AssignmentDocumentsService assignmentDocumentsService;
	@Autowired private AssignmentDeliverablesService assignmentDeliverablesService;
	@Autowired private AssignmentAvailableFundsService assignmentAvailableFundsService;
	@Autowired private CRMService crmService;

	@ApiOperation(value = "Get assignment by id")
	@RequestMapping(
		value = "/{id}",
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentDTO> getAssignment(
		@ApiParam(name = "id", required = true) @PathVariable String id) throws WorkActionException {
		AssignmentDTO assignmentDTO = assignmentService.get(id);
		return ApiV2Response.valueWithResult(assignmentDTO);
	}

	@ApiOperation(value = "Create a new assignment")
	@RequestMapping(
		value = {"/", ""},
		method = POST,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentDTO> postCreateAssignment(@RequestBody AssignmentDTO builder) throws ValidationException, WorkAuthorizationException {
		AssignmentDTO assignmentDTO = assignmentService.create(builder, true);
		return ApiV2Response.valueWithResult(assignmentDTO);
	}

	@ApiOperation(value = "Create multiple new assignments (copies)")
	@RequestMapping(
		value = {"/multiple"},
		method = POST,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentDTO> postCreateBulkAssignment(
		@RequestBody AssignmentDTO builder,
		@RequestParam(value = "numberOfCopies") int numberOfCopies
	) throws ValidationException, WorkAuthorizationException {
		List<AssignmentDTO> assignmentDTOs = assignmentService.createMultiple(builder, numberOfCopies, true);
		return ApiV2Response.valueWithResults(assignmentDTOs);
	}

	@ApiOperation(value = "Update an assignment", notes = "All objects in the assignment graph will be affected")
	@RequestMapping(
		value = {"/{id}"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentDTO> putUpdateAssignment(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody AssignmentDTO builder
	) throws ValidationException, WorkActionException, WorkAuthorizationException {
		AssignmentDTO assignmentDTO = assignmentService.update(id, builder, true);
		return ApiV2Response.valueWithResult(assignmentDTO);
	}

	@ApiOperation(value = "Create an assignment", notes = "All objects in the assignment graph will be affected")
	@RequestMapping(
		value = {"/{id}"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentDTO> postCreateAssignment(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody AssignmentDTO builder
	) throws ValidationException, WorkActionException, WorkAuthorizationException {
		AssignmentDTO assignmentDTO = assignmentService.update(id, builder, true);
		return ApiV2Response.valueWithResult(assignmentDTO);
	}

	@ApiOperation(value = "Validate an assignment")
	@RequestMapping(
		value = {"/validation_errors"},
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<List<ApiBaseError>> postValidateAssignment(@RequestBody AssignmentDTO builder) throws Exception {
		List<ApiBaseError> errors= assignmentService.validate(builder, true);
		return ApiV2Response.valueWithResult(errors);
	}

	@ApiOperation(value = "Get an assignment's copy quantity")
	@RequestMapping(
		value = {"/assignment_copy_quantities"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<List<Integer>> getAssignmentCopyQuantity() throws Exception {
		List<Integer> assignmentCopyQuantities= assignmentService.getAssignmentCopyQuantities();
		return ApiV2Response.valueWithResult(assignmentCopyQuantities);
	}

	@ApiOperation(value = "Get an assignment's schedule")
	@RequestMapping(
		value = {"/{id}/schedule"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ScheduleDTO> getAssignmentSchedule(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		ScheduleDTO scheduleDTO = assignmentScheduleService.get(id);
		return ApiV2Response.valueWithResult(scheduleDTO);
	}

	@ApiOperation(value = "Update an assignment's schedule")
	@RequestMapping(
		value = {"/{id}/schedule"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ScheduleDTO> putUpdateAssignmentSchedule(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ScheduleDTO builder
	) throws WorkActionException, ValidationException {
		ScheduleDTO scheduleDTO = assignmentScheduleService.update(id, builder, true);
		return ApiV2Response.valueWithResult(scheduleDTO);
	}

	@ApiOperation(value = "Create an assignment's schedule")
	@RequestMapping(
		value = {"/{id}/schedule"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ScheduleDTO> postCreateAssignmentSchedule(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ScheduleDTO builder
	) throws WorkActionException, ValidationException {
		ScheduleDTO scheduleDTO = assignmentScheduleService.update(id, builder, true);
		return ApiV2Response.valueWithResult(scheduleDTO);
	}

	@ApiOperation(value = "Get an assignment's pricing details")
	@RequestMapping(
		value = {"/{id}/pricing"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<PricingDTO> getAssignmentPricing(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		PricingDTO pricingDTO = assignmentPricingService.get(id);
		return ApiV2Response.valueWithResult(pricingDTO);
	}

	@ApiOperation(value = "Update an assignment's pricing details")
	@RequestMapping(
		value = {"/{id}/pricing"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<PricingDTO> putUpdateAssignmentPricing(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody PricingDTO builder
	) throws WorkActionException, ValidationException, WorkAuthorizationException {
		PricingDTO pricingDTO = assignmentPricingService.update(id, builder, true);
		return ApiV2Response.valueWithResult(pricingDTO);
	}

	@ApiOperation(value = "Create an assignment's pricing details")
	@RequestMapping(
		value = {"/{id}/pricing"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<PricingDTO> postCreateAssignmentPricing(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody PricingDTO builder
	) throws WorkActionException, ValidationException, WorkAuthorizationException {
		PricingDTO pricingDTO = assignmentPricingService.update(id, builder, true);
		return ApiV2Response.valueWithResult(pricingDTO);
	}

	@ApiOperation(value = "Get an assignment's location details")
	@RequestMapping(
		value = {"/{id}/location"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<LocationDTO> getAssignmentLocation(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		LocationDTO locationDTO = assignmentLocationService.get(id);
		return ApiV2Response.valueWithResult(locationDTO);
	}

	@ApiOperation(value = "Update an assignment's location details")
	@RequestMapping(
		value = {"/{id}/location"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<LocationDTO> putUpdateAssignmentLocation(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody LocationDTO builder
	) throws WorkActionException, ValidationException {
		LocationDTO locationDTO = assignmentLocationService.update(id, builder, true);
		return ApiV2Response.valueWithResult(locationDTO);
	}

	@ApiOperation(value = "Update an assignment's location details")
	@RequestMapping(
		value = {"/{id}/location"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<LocationDTO> postAssignmentLocation(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody LocationDTO builder
	) throws WorkActionException, ValidationException {
		LocationDTO locationDTO = assignmentLocationService.update(id, builder, true);
		return ApiV2Response.valueWithResult(locationDTO);
	}

	@ApiOperation(value = "Get an assignment's routing details")
	@RequestMapping(
		value = {"/{id}/routing"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<RoutingDTO> getAssignmentRouting(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		RoutingDTO routingDTO = assignmentRoutingService.get(id);
		return ApiV2Response.valueWithResult(routingDTO);
	}

	@ApiOperation(value = "Update an assignment's routing details")
	@RequestMapping(
		value = {"/{id}/routing"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<RoutingDTO> putUpdateAssignmentRouting(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody RoutingDTO builder
	) throws WorkActionException, ValidationException {
		RoutingDTO routingDTO = assignmentRoutingService.update(id, builder, true);
		return ApiV2Response.valueWithResult(routingDTO);
	}

	@ApiOperation(value = "Create an assignment's routing details")
	@RequestMapping(
		value = {"/{id}/routing"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<RoutingDTO> postCreateAssignmentRouting(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody RoutingDTO builder
	) throws WorkActionException, ValidationException {
		RoutingDTO routingDTO = assignmentRoutingService.update(id, builder, true);
		return ApiV2Response.valueWithResult(routingDTO);
	}

	@ApiOperation(value = "Get an assignment's custom field groups")
	@RequestMapping(
		value = {"/{id}/custom_field_groups"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<CustomFieldGroupDTO> getAssignmentCustomFieldGroups(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		Set<CustomFieldGroupDTO> customFieldGroupsDTOs = assignmentCustomFieldGroupsService.get(id);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(customFieldGroupsDTOs));
	}

	@ApiOperation(value = "Update an assignment's custom field groups")
	@RequestMapping(
		value = {"/{id}/custom_field_groups"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<CustomFieldGroupDTO> putUpdateAssignmentCustomFieldGroups(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<CustomFieldGroupDTO> builders
	) throws WorkActionException, ValidationException {
		Set<CustomFieldGroupDTO> customFieldGroupDTOs =
			assignmentCustomFieldGroupsService.update(id, collectSet(builders), true);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(customFieldGroupDTOs));
	}

	@ApiOperation(value = "Create an assignment's custom field groups")
	@RequestMapping(
		value = {"/{id}/custom_field_groups"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<CustomFieldGroupDTO> postCreateAssignmentCustomFieldGroups(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<CustomFieldGroupDTO> builders
	) throws WorkActionException, ValidationException {
		Set<CustomFieldGroupDTO> customFieldGroupDTOs =
			assignmentCustomFieldGroupsService.update(id, collectSet(builders), true);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(customFieldGroupDTOs));
	}

	@ApiOperation(value = "Get an assignment's shipments")
	@RequestMapping(
		value = {"/{id}/shipments"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ShipmentGroupDTO> getAssignmentShipments(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		ShipmentGroupDTO shipmentGroupDTO = assignmentShipmentsService.get(id);
		return ApiV2Response.valueWithResult(shipmentGroupDTO);
	}

	@ApiOperation(value = "Update an assignment's shipments")
	@RequestMapping(
		value = {"/{id}/shipments"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ShipmentGroupDTO> putUpdateAssignmentShipments(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ShipmentGroupDTO builder
		) throws WorkActionException, ValidationException {
		ShipmentGroupDTO shipmentGroupDTO =
			assignmentShipmentsService.update(id, builder, true);
		return ApiV2Response.valueWithResult(shipmentGroupDTO);
	}

	@ApiOperation(value = "Create an assignment's shipments")
	@RequestMapping(
		value = {"/{id}/shipments"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ShipmentGroupDTO> postCreateAssignmentShipments(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ShipmentGroupDTO builder
	) throws WorkActionException, ValidationException {
		ShipmentGroupDTO shipmentGroupDTO =
			assignmentShipmentsService.update(id, builder, true);
		return ApiV2Response.valueWithResult(shipmentGroupDTO);
	}

	@ApiOperation(value = "Get an assignment's surveys")
	@RequestMapping(
		value = {"/{id}/surveys"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<SurveyDTO> getAssignmentSurveys(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		Set<SurveyDTO> surveyDTOs = assignmentSurveysService.get(id);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(surveyDTOs));
	}

	@ApiOperation(value = "Update an assignment's surveys")
	@RequestMapping(
		value = {"/{id}/surveys"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<SurveyDTO> putUpdateAssignmentSurveys(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<SurveyDTO> builders
	) throws WorkActionException, ValidationException {
		Set<SurveyDTO> surveyDTOs =
			assignmentSurveysService.update(id, collectSet(builders), true);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(surveyDTOs));
	}

	@ApiOperation(value = "Create an assignment's surveys")
	@RequestMapping(
		value = {"/{id}/surveys"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<SurveyDTO> postCreateAssignmentSurveys(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<SurveyDTO> builders
	) throws WorkActionException, ValidationException {
		Set<SurveyDTO> surveyDTOs =
			assignmentSurveysService.update(id, collectSet(builders), true);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(surveyDTOs));
	}

	@ApiOperation(value = "Get an assignment's configuration details")
	@RequestMapping(
		value = {"/{id}/configuration"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ConfigurationDTO> getAssignmentConfiguration(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		ConfigurationDTO configurationDTO = assignmentConfigurationService.get(id);
		return ApiV2Response.valueWithResult(configurationDTO);
	}

	@ApiOperation(value = "Create an assignment's configuration details")
	@RequestMapping(
		value = {"/{id}/configuration"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ConfigurationDTO> postCreateAssignmentConfiguration(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ConfigurationDTO builder
	) throws WorkActionException, ValidationException {
		ConfigurationDTO configurationDTO =
			assignmentConfigurationService.update(id, builder, true);
		return ApiV2Response.valueWithResult(configurationDTO);
	}

	@ApiOperation(value = "Update an assignment's configuration details")
	@RequestMapping(
		value = {"/{id}/configuration"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ConfigurationDTO> putUpdateAssignmentConfiguration(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ConfigurationDTO builder
	) throws WorkActionException, ValidationException {
		ConfigurationDTO configurationDTO =
			assignmentConfigurationService.update(id, builder, true);
		return ApiV2Response.valueWithResult(configurationDTO);
	}

	@ApiOperation(value = "Get an assignment's documents")
	@RequestMapping(
		value = {"/{id}/documents"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DocumentDTO> getAssignmentDocuments(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		Set<DocumentDTO> documentDTOs = assignmentDocumentsService.get(id);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(documentDTOs));
	}

	@ApiOperation(value = "Update an assignment's documents")
	@RequestMapping(
		value = {"/{id}/documents"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DocumentDTO> putUpdateAssignmentDocuments(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<DocumentDTO> builders
	) throws WorkActionException, ValidationException {
		Set<DocumentDTO> documentDTOs =
			assignmentDocumentsService.update(id, collectSet(builders), true);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(documentDTOs));
	}

	@ApiOperation(value = "Create an assignment's documents")
	@RequestMapping(
		value = {"/{id}/documents"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DocumentDTO> postCreateAssignmentDocuments(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<DocumentDTO> builders
	) throws WorkActionException, ValidationException {
		Set<DocumentDTO> documentDTOs =
			assignmentDocumentsService.update(id, collectSet(builders), true);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(documentDTOs));
	}

	@ApiOperation(value = "Get an assignment's deliverables")
	@RequestMapping(
		value = {"/{id}/deliverables_group"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DeliverablesGroupDTO> getAssignmentDeliverablesGroup(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		DeliverablesGroupDTO deliverablesGroupDTO = assignmentDeliverablesService.get(id);
		return ApiV2Response.valueWithResult(deliverablesGroupDTO);
	}

	@ApiOperation(value = "Update an assignment's deliverables")
	@RequestMapping(
		value = {"/{id}/deliverables_group"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DeliverablesGroupDTO> putUpdateAssignmentDeliverablesGroup(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody DeliverablesGroupDTO builder
	) throws WorkActionException, ValidationException {
		DeliverablesGroupDTO deliverablesGroupDTO =
			assignmentDeliverablesService.update(id, builder, true);
		return ApiV2Response.valueWithResult(deliverablesGroupDTO);
	}

	@ApiOperation(value = "Create an assignment's deliverables")
	@RequestMapping(
		value = {"/{id}/deliverables_group"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DeliverablesGroupDTO> postCreateAssignmentDeliverablesGroup(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody DeliverablesGroupDTO builder
	) throws WorkActionException, ValidationException {
		DeliverablesGroupDTO deliverablesGroupDTO =
			assignmentDeliverablesService.update(id, builder, true);
		return ApiV2Response.valueWithResult(deliverablesGroupDTO);
	}

	@ApiOperation(value = "Add a new Client for any of user's assignments")
	@RequestMapping(
			value = {"/add_client"},
			method = {POST},
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ClientCompanyDTO> postCreateAssignmentClient(
			@RequestBody ClientCompanyDTO client
	) throws WorkActionException, ValidationException {
		ClientCompany clientCompany = crmService.saveOrUpdateClientCompany(getCurrentUser().getId(), client, null);
		if (clientCompany != null) {
			client.setClientCompanyId(clientCompany.getId());
		}

		return ApiV2Response.valueWithResult(client);
	}

	@ApiOperation(value = "Get current user's available funds")
	@RequestMapping(
			value = {"/availableFunds"},
			method = GET,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AvailableFundsApiDTO> getUserAvailableFunds(
	) throws WorkActionException {
		AvailableFundsApiDTO availableFundsApiDTO = assignmentAvailableFundsService.get();
		return ApiV2Response.valueWithResult(availableFundsApiDTO);
	}
}
