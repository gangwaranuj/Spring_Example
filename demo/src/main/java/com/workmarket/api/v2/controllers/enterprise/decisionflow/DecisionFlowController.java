package com.workmarket.api.v2.controllers.enterprise.decisionflow;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.parameter.EmptyResponse;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.enterprise.decisionflow.DecisionDTO;
import com.workmarket.api.v2.model.enterprise.decisionflow.DecisionFlowDTO;
import com.workmarket.api.v2.model.enterprise.decisionflow.DecisionStepDTO;
import com.workmarket.api.v2.utils.enterprise.decision.DecisionFlowConverter;
import com.workmarket.business.decision.gen.Messages.CreateDecisionFlowTemplateReq;
import com.workmarket.business.decision.gen.Messages.CreateDecisionFlowTemplateResp;
import com.workmarket.business.decision.gen.Messages.DecisionFlowTemplateResponse;
import com.workmarket.business.decision.gen.Messages.Status;
import com.workmarket.domains.model.Company;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.decisionflow.DecisionFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Api(tags = "Profile")
@Controller("decisionFlowController")
@RequestMapping(value = {"/v2/decision_flow"})
public class DecisionFlowController extends ApiBaseController {

	@Autowired private CompanyService companyService;
	@Autowired private DecisionFlowService decisionFlowService;

	@RequestMapping(
		value = "/{uuid}",
		method = RequestMethod.GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "Get a decision flow by UUID")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	public ApiV2Response<DecisionFlowDTO> getDecisionFlow(@PathVariable("uuid") final String uuid) {
		final DecisionFlowTemplateResponse response = decisionFlowService.getDecisionFlowTemplate(uuid);
		if (!response.getStatus().getSuccess()) {
			final String message = StringUtils.join(response.getStatus().getMessageList(), " ");
			throw new RuntimeException(message);
		}
		final DecisionFlowDTO result = DecisionFlowConverter.asApiResponse(response.getDecisionFlowTemplate());
		return ApiV2Response.valueWithResult(result);
	}

	@RequestMapping(
		value = "/update/{uuid}",
		method = RequestMethod.PUT,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "Update a decision flow by UUID")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	public ApiV2Response<DecisionFlowDTO> updateDecisionFlow(
		@PathVariable("uuid") final String uuid,
		@RequestBody DecisionFlowDTO dto
	) throws Exception {
		final CreateDecisionFlowTemplateReq request =
			CreateDecisionFlowTemplateReq.newBuilder()
				.setDescription(dto.getDescription())
				.setName(dto.getName())
				.setNamespace(dto.getNamespace())
				.addAllDecisionStepTemplateNode(
					DecisionFlowConverter.asDecisionStepTemplateNodes(dto.getDecisionSteps()))
				.build();

		CreateDecisionFlowTemplateResp response = decisionFlowService.updateDecisionFlow(
			request,
			companyService.findCompanyById(getCurrentUser().getCompanyId()),
			uuid
		);

		if (!response.getStatus().getSuccess()) {
			throw new RuntimeException(response.getStatus().getMessage(0));
		}

		final DecisionFlowDTO decisionFlowDTO = new DecisionFlowDTO.Builder()
				.withUuid(response.getUuid())
				.build();
		return ApiV2Response.valueWithResult(decisionFlowDTO);
	}

	@RequestMapping(
		value = "/create",
		method = RequestMethod.POST,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "Create a decision flow")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	public ApiV2Response<DecisionFlowDTO> createDecisionFlow(@RequestBody DecisionFlowDTO dto) throws Exception {

		final CreateDecisionFlowTemplateReq request =
			CreateDecisionFlowTemplateReq.newBuilder()
				.setDescription(dto.getDescription())
				.setName(dto.getName())
				.setNamespace(dto.getNamespace())
				.addAllDecisionStepTemplateNode(
					DecisionFlowConverter.asDecisionStepTemplateNodes(dto.getDecisionSteps()))
				.build();

		CreateDecisionFlowTemplateResp response = decisionFlowService.createDecisionFlowTemplate(
			request,
			companyService.findCompanyById(getCurrentUser().getCompanyId())
		);

		if (!response.getStatus().getSuccess()) {
			throw new RuntimeException(response.getStatus().getMessage(0));
		}

		final DecisionFlowDTO decisionFlowDTO = new DecisionFlowDTO.Builder()
				.withUuid(response.getUuid())
				.build();
		return ApiV2Response.valueWithResult(decisionFlowDTO);
	}

	@RequestMapping(
			value = "/list/companyNumber/{companyNumber}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "Get the decision flow template UUIDs for a company.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Retrieved decision flow template UUIDs."),
			@ApiResponse(code = 400, message = "Decision flow template UUIDs not found."),
			@ApiResponse(code = 500, message = "We encountered an error while getting decision flow template UUIDs.")})
	@ResponseBody
	public ApiV2Response<String> getDecisionFlowUuidsByCompanyNumber(
			@PathVariable("companyNumber") final String companyNumber) {
		final Company company = companyService.findCompanyByNumber(companyNumber);
		final List<String> decisionFlowTemplateUuids =
				decisionFlowService.getDecisionFlowTemplateUuids(company.getId());
		return ApiV2Response.valueWithResults(decisionFlowTemplateUuids);
	}

	@RequestMapping(
			value = "/decide",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "Save a decision.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Decision saved."),
			@ApiResponse(code = 400, message = "Decision not found."),
			@ApiResponse(code = 500, message = "We encountered an error while saving the decision.")})
	@ResponseBody
	public ApiV2Response<String> decide(@RequestBody DecisionDTO dto) {
		final Status status = decisionFlowService.approve(dto.getUuid(), dto.getDecider().getUuid());
		final String messages = String.valueOf(status.getMessageList());
		final HttpStatus httpStatus = status.getSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return ApiV2Response.valueWithMessage(messages, httpStatus);
	}

	@RequestMapping(
			value = "/activate/{decisionFlowUuid}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "Activate a decision flow.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Decision flow activated."),
			@ApiResponse(code = 400, message = "Decision flow not found."),
			@ApiResponse(code = 500, message = "We encountered an error while activating the decision flow.")})
	@ResponseBody
	public ApiV2Response<String> activate(@PathVariable("decisionFlowUuid") final String decisionFlowUuid) {
		final Status status = decisionFlowService.activate(decisionFlowUuid);
		final String messages = String.valueOf(status.getMessageList());
		final HttpStatus httpStatus = status.getSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return ApiV2Response.valueWithMessage(messages, httpStatus);
	}

	@RequestMapping(
			value = "/deactivate/{decisionFlowUuid}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "Deactivate a decision flow.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Decision flow deactivated."),
			@ApiResponse(code = 400, message = "Decision flow not found."),
			@ApiResponse(code = 500, message = "We encountered an error while deactivating the decision flow.")})
	@ResponseBody
	public ApiV2Response<String> deactivate(@PathVariable("decisionFlowUuid") final String decisionFlowUuid) {
		final Status status = decisionFlowService.deactivate(decisionFlowUuid);
		final String messages = String.valueOf(status.getMessageList());
		final HttpStatus httpStatus = status.getSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return ApiV2Response.valueWithMessage(messages, httpStatus);
	}
}
