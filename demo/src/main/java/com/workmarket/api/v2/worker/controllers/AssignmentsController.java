package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.model.resolver.ApiArgumentResolver;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.fulfillment.AssignmentFulfillmentProcessor;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.model.AssignmentsRequestDTO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(tags = {"assignments", "worker"})
@RequestMapping("/worker/v2/assignments")
@Controller(value = "workerAssignmentsController")
public class AssignmentsController extends ApiBaseController {

	private static final Log logger = LogFactory.getLog(AssignmentsController.class);

	@Autowired private AssignmentFulfillmentProcessor assignmentFulfillmentProcessor;

	@ApiOperation("Get a list of assignments")
	@RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getWorkerAssignments(
			@ApiParam @ApiArgumentResolver final AssignmentsRequestDTO assignmentsRequestDTO,
			final HttpServletRequest request) {
		final FulfillmentPayloadDTO fulfillmentResponse = assignmentFulfillmentProcessor.getListPage(getCurrentUser(),
																																																 assignmentsRequestDTO,
																																																 request);

		return new ApiV2Response(new ApiJSONPayloadMap(),
														 fulfillmentResponse.getPayload(),
														 fulfillmentResponse.getPagination());
	}

	@ApiOperation("Get the details of the given assignment")
	@RequestMapping(value = "/{workNumber}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getWorkerAssignmentDetails(@ApiParam(name = "workNumber", required = true) @PathVariable("workNumber") final String workNumber,
																	final HttpServletRequest request) {

		final FulfillmentPayloadDTO result = assignmentFulfillmentProcessor.getAssignmentDetails(getCurrentUser(),
																																														 workNumber);

		return new ApiV2Response(new ApiJSONPayloadMap(), result.getPayload(), null);
	}
}
