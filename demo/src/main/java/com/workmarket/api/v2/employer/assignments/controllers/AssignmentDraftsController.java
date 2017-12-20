package com.workmarket.api.v2.employer.assignments.controllers;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;
import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
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
@RequestMapping(value = {"/v2/employer/assignments/drafts", "/employer/v2/assignments/drafts"})
public class AssignmentDraftsController extends ApiBaseController {
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

	@ApiOperation(value = "Get assignment details")
	@RequestMapping(
		value = "/{id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentDTO> getAssignmentDraft(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		// TODO[Jim]: Only return the assignment if it's actually a DRAFT
		//   This just returns whatever assignment is requested.
		AssignmentDTO assignmentDTO = assignmentService.get(id);
		return ApiV2Response.valueWithResult(assignmentDTO);
	}

	@ApiOperation(value = "Create a draft assignment")
	@RequestMapping(
		value = {"/", ""},
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentDTO> postAssignmentDraft(
		@RequestBody AssignmentDTO builder
	) throws ValidationException, WorkAuthorizationException {
		AssignmentDTO assignmentDTO = assignmentService.create(builder, false);
		return ApiV2Response.valueWithResult(assignmentDTO);
	}

	@ApiOperation(value = "Create multiple draft assignments (copies)")
	@RequestMapping(
		value = {"/multiple"},
		method = POST,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentDTO> postBulkDraftAssignments(
		@RequestBody AssignmentDTO builder,
		@RequestParam(value = "numberOfCopies") int numberOfCopies
	) throws ValidationException, WorkAuthorizationException {
		List<AssignmentDTO> assignmentDTOs = assignmentService.createMultiple(builder, numberOfCopies, false);
		return ApiV2Response.valueWithResults(assignmentDTOs);
	}

	@ApiOperation(value = "Update a draft assignment")
	@RequestMapping(
		value = "/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentDTO> postUpdateAssignmentDraft(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody AssignmentDTO builder
	) throws Exception {
		AssignmentDTO assignmentDTO = assignmentService.update(id, builder, false);
		return ApiV2Response.valueWithResult(assignmentDTO);
	}

	@ApiOperation(value = "Update a draft assignment")
	@RequestMapping(
		value = {"/{id}"},
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentDTO> putUpdateAssignmentDraft(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody AssignmentDTO builder
	) throws Exception {
		AssignmentDTO assignmentDTO = assignmentService.update(id, builder, false);
		return ApiV2Response.valueWithResult(assignmentDTO);
	}

	@ApiOperation(value = "Validate a draft assignment")
	@RequestMapping(
		value = "/validation_errors",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<List<ApiBaseError>> postValidateAssignmentDraft(
		@RequestBody AssignmentDTO builder
	) throws Exception {
		List<ApiBaseError> errors = assignmentService.validate(builder, false);
		return ApiV2Response.valueWithResult(errors);
	}

	@ApiOperation(value = "Get an assignment schedule")
	@RequestMapping(
		value = "/{id}/schedule",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ScheduleDTO> getAssignmentDraftSchedule(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		ScheduleDTO scheduleDTO = assignmentScheduleService.get(id);
		return ApiV2Response.valueWithResult(scheduleDTO);
	}

	@ApiOperation(value = "Get an assignment schedule")
	@RequestMapping(
		value = "/{id}/schedule",
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ScheduleDTO> putAssignmentDraftSchedule(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ScheduleDTO builder
	) throws WorkActionException, ValidationException {
		ScheduleDTO scheduleDTO = assignmentScheduleService.update(id, builder, false);
		return ApiV2Response.valueWithResult(scheduleDTO);
	}

	@ApiOperation(value = "Get an assignment schedule")
	@RequestMapping(
		value = "/{id}/schedule",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ScheduleDTO> postAssignmentDraftSchedule(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ScheduleDTO builder
	) throws WorkActionException, ValidationException {
		ScheduleDTO scheduleDTO = assignmentScheduleService.update(id, builder, false);
		return ApiV2Response.valueWithResult(scheduleDTO);
	}

	@ApiOperation(value = "Get assignment pricing details")
	@RequestMapping(
		value = "/{id}/pricing",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<PricingDTO> getAssignmentDraftPricing(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		PricingDTO pricingDTO = assignmentPricingService.get(id);
		return ApiV2Response.valueWithResult(pricingDTO);
	}

	@ApiOperation(value = "Update assignment pricing details")
	@RequestMapping(
		value = "/{id}/pricing",
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<PricingDTO> putAssignmentDraftPricing(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody PricingDTO builder
	) throws WorkActionException, ValidationException, WorkAuthorizationException {
		PricingDTO pricingDTO = assignmentPricingService.update(id, builder, false);
		return ApiV2Response.valueWithResult(pricingDTO);
	}

	@ApiOperation(value = "Update assignment pricing details")
	@RequestMapping(
		value = "/{id}/pricing",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<PricingDTO> postAssignmentDraftPricing(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody PricingDTO builder
	) throws WorkActionException, ValidationException, WorkAuthorizationException {
		PricingDTO pricingDTO = assignmentPricingService.update(id, builder, false);
		return ApiV2Response.valueWithResult(pricingDTO);
	}

	@ApiOperation(value = "Get assignment location details")
	@RequestMapping(
		value = "/{id}/location",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<LocationDTO> getAssignmentDraftLocation(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		LocationDTO locationDTO = assignmentLocationService.get(id);
		return ApiV2Response.valueWithResult(locationDTO);
	}

	@ApiOperation(value = "Update assignment location details")
	@RequestMapping(
		value = "/{id}/location",
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<LocationDTO> putAssignmentDraftLocation(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody LocationDTO builder
	) throws WorkActionException, ValidationException {
		LocationDTO locationDTO = assignmentLocationService.update(id, builder, false);
		return ApiV2Response.valueWithResult(locationDTO);
	}

	@ApiOperation(value = "Update assignment location details")
	@RequestMapping(
		value = "/{id}/location",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<LocationDTO> postAssignmentDraftLocation(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody LocationDTO builder
	) throws WorkActionException, ValidationException {
		LocationDTO locationDTO = assignmentLocationService.update(id, builder, false);
		return ApiV2Response.valueWithResult(locationDTO);
	}

	@ApiOperation(value = "Get assignment routing details")
	@RequestMapping(
		value = "/{id}/routing",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<RoutingDTO> getAssignmentDraftRouting(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		RoutingDTO routingDTO = assignmentRoutingService.get(id);
		return ApiV2Response.valueWithResult(routingDTO);
	}

	@ApiOperation(value = "Update assignment routing details")
	@RequestMapping(
		value = "/{id}/routing",
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<RoutingDTO> putAssignmentDraftRouting(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody RoutingDTO builder
	) throws WorkActionException, ValidationException {
		RoutingDTO routingDTO = assignmentRoutingService.update(id, builder, false);
		return ApiV2Response.valueWithResult(routingDTO);
	}

	@ApiOperation(value = "Update assignment routing details")
	@RequestMapping(
		value = "/{id}/routing",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<RoutingDTO> postAssignmentDraftRouting(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody RoutingDTO builder
	) throws WorkActionException, ValidationException {
		RoutingDTO routingDTO = assignmentRoutingService.update(id, builder, false);
		return ApiV2Response.valueWithResult(routingDTO);
	}

	@ApiOperation(value = "Get assignment custom field groups")
	@RequestMapping(
		value = "/{id}/custom_field_groups",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<CustomFieldGroupDTO> getAssignmentDraftCustomFieldGroups(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		Set<CustomFieldGroupDTO> customFieldGroupsDTOs = assignmentCustomFieldGroupsService.get(id);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(customFieldGroupsDTOs));
	}

	@ApiOperation(value = "Create/update assignment custom field groups")
	@RequestMapping(
		value = "/{id}/custom_field_groups",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<CustomFieldGroupDTO> postAssignmentDraftCustomFieldGroups(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<CustomFieldGroupDTO> builders
	) throws WorkActionException, ValidationException {
		Set<CustomFieldGroupDTO> customFieldGroupDTOs =
			assignmentCustomFieldGroupsService.update(id, collectSet(builders), false);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(customFieldGroupDTOs));
	}

	@ApiOperation(value = "Create/update assignment custom field groups")
	@RequestMapping(
		value = "/{id}/custom_field_groups",
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<CustomFieldGroupDTO> putAssignmentDraftCustomFieldGroups(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<CustomFieldGroupDTO> builders
	) throws WorkActionException, ValidationException {
		Set<CustomFieldGroupDTO> customFieldGroupDTOs =
			assignmentCustomFieldGroupsService.update(id, collectSet(builders), false);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(customFieldGroupDTOs));
	}

	@ApiOperation(value = "Get assignment shipments")
	@RequestMapping(
		value = "/{id}/shipments",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ShipmentGroupDTO> getAssignmentDraftShipments(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		ShipmentGroupDTO shipmentGroupDTO = assignmentShipmentsService.get(id);
		return ApiV2Response.valueWithResult(shipmentGroupDTO);
	}

	@ApiOperation(value = "Create/update assignment shipments")
	@RequestMapping(
		value = "/{id}/shipments",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ShipmentGroupDTO> postAssignmentDraftShipments(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ShipmentGroupDTO builder
	) throws WorkActionException, ValidationException {
		ShipmentGroupDTO shipmentGroupDTO = assignmentShipmentsService.update(id, builder, false);
		return ApiV2Response.valueWithResult(shipmentGroupDTO);
	}

	@ApiOperation(value = "Create/update assignment shipments")
	@RequestMapping(
		value = "/{id}/shipments",
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ShipmentGroupDTO> putAssignmentDraftShipments(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ShipmentGroupDTO builder
	) throws WorkActionException, ValidationException {
		ShipmentGroupDTO shipmentGroupDTO = assignmentShipmentsService.update(id, builder, false);
		return ApiV2Response.valueWithResult(shipmentGroupDTO);
	}

	@ApiOperation(value = "Get assignment surveys")
	@RequestMapping(
		value = "/{id}/surveys",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<SurveyDTO> getAssignmentDraftSurveys(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		Set<SurveyDTO> surveyDTOs = assignmentSurveysService.get(id);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(surveyDTOs));
	}

	@ApiOperation(value = "Create/update assignment surveys")
	@RequestMapping(
		value = "/{id}/surveys",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<SurveyDTO> postAssignmentDraftSurveys(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<SurveyDTO> builders
	) throws WorkActionException, ValidationException {
		Set<SurveyDTO> surveyDTOs = assignmentSurveysService.update(id, collectSet(builders), false);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(surveyDTOs));
	}

	@ApiOperation(value = "Create/update assignment surveys")
	@RequestMapping(
		value = "/{id}/surveys",
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<SurveyDTO> putAssignmentDraftSurveys(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<SurveyDTO> builders
	) throws WorkActionException, ValidationException {
		Set<SurveyDTO> surveyDTOs = assignmentSurveysService.update(id, collectSet(builders), false);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(surveyDTOs));
	}

	@ApiOperation(value = "Get assignment configuration details")
	@RequestMapping(
		value = "/{id}/configuration",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ConfigurationDTO> getAssignmentDraftConfiguration(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		ConfigurationDTO configurationDTO = assignmentConfigurationService.get(id);
		return ApiV2Response.valueWithResult(configurationDTO);
	}

	@ApiOperation(value = "Create/update assignment configuration details")
	@RequestMapping(
		value = "/{id}/configuration",
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ConfigurationDTO> putAssignmentDraftConfiguration(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ConfigurationDTO builder
	) throws WorkActionException, ValidationException {
		ConfigurationDTO configurationDTO = assignmentConfigurationService.update(id, builder, false);
		return ApiV2Response.valueWithResult(configurationDTO);
	}

	@ApiOperation(value = "Create/update assignment configuration details")
	@RequestMapping(
		value = "/{id}/configuration",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ConfigurationDTO> postAssignmentDraftConfiguration(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody ConfigurationDTO builder
	) throws WorkActionException, ValidationException {
		ConfigurationDTO configurationDTO = assignmentConfigurationService.update(id, builder, false);
		return ApiV2Response.valueWithResult(configurationDTO);
	}

	@ApiOperation(value = "Get the documents associated with this assignment ")
	@RequestMapping(
		value = "/{id}/documents",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DocumentDTO> getAssignmentDraftDocuments(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		Set<DocumentDTO> documentDTOs = assignmentDocumentsService.get(id);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(documentDTOs));
	}

	@ApiOperation(value = "Create/update the documents associated with this assignment")
	@RequestMapping(
		value = "/{id}/documents",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DocumentDTO> postAssignmentDraftDocuments(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<DocumentDTO> builders
	) throws WorkActionException, ValidationException {
		Set<DocumentDTO> documentDTOs = assignmentDocumentsService.update(id, collectSet(builders), false);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(documentDTOs));
	}

	@ApiOperation(value = "Create/update the documents associated with this assignment")
	@RequestMapping(
		value = "/{id}/documents",
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DocumentDTO> putAssignmentDraftDocuments(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody Set<DocumentDTO> builders
	) throws WorkActionException, ValidationException {
		Set<DocumentDTO> documentDTOs = assignmentDocumentsService.update(id, collectSet(builders), false);
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(documentDTOs));
	}

	@ApiOperation(value = "Get the deliverables associated with this assignment")
	@RequestMapping(
		value = "/{id}/deliverables_group",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DeliverablesGroupDTO> getAssignmentDraftDeliverablesGroup(
		@ApiParam(name = "id", required = true) @PathVariable String id
	) throws WorkActionException {
		DeliverablesGroupDTO deliverablesGroupDTO = assignmentDeliverablesService.get(id);
		return ApiV2Response.valueWithResult(deliverablesGroupDTO);
	}

	@ApiOperation(value = "Update the deliverables associated with this assignment")
	@RequestMapping(
		value = "/{id}/deliverables_group",
		method = PUT,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DeliverablesGroupDTO> putAssignmentDraftDeliverablesGroup(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody DeliverablesGroupDTO builder
	) throws WorkActionException, ValidationException {
		DeliverablesGroupDTO deliverablesGroupDTO = assignmentDeliverablesService.update(id, builder, false);
		return ApiV2Response.valueWithResult(deliverablesGroupDTO);
	}

	@ApiOperation(value = "Update the deliverables associated with this assignment")
	@RequestMapping(
		value = "/{id}/deliverables_group",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DeliverablesGroupDTO> postAssignmentDraftDeliverablesGroup(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody DeliverablesGroupDTO builder
	) throws WorkActionException, ValidationException {
		DeliverablesGroupDTO deliverablesGroupDTO = assignmentDeliverablesService.update(id, builder, false);
		return ApiV2Response.valueWithResult(deliverablesGroupDTO);
	}
}
