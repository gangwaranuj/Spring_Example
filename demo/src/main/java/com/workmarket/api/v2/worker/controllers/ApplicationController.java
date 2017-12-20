package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.fulfillment.NegotiationFulfillmentProcessor;
import com.workmarket.api.v2.worker.model.AssignmentApplicationDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("ApplicationController")
@RequestMapping("/worker/v2/assignments")
public class ApplicationController extends ApiBaseController {
	@Autowired private XAssignment xAssignment;
	@Autowired private NegotiationFulfillmentProcessor negotiationFulfillmentProcessor;

	@ApiOperation(value="Apply for an assignment")
	@RequestMapping(value = "{workNumber}/application", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postApplyAssignment(
		@PathVariable("workNumber") final String workNumber,
		@Valid @RequestBody final AssignmentApplicationDTO form) throws Exception {

		final FulfillmentPayloadDTO response = negotiationFulfillmentProcessor.applyForAssignment(workNumber, form);

		if (response.isSuccessful()) {
			return ApiV2Response.OK(response.getPayload());
		} else {
			throw new MessageSourceApiException("assignment.apply.failure");
		}
	}

	@ApiOperation(value="Cancel an assignment application")
	@RequestMapping(value = "{workNumber}/application/cancel", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postCancelApplyAssignment(@PathVariable("workNumber") final String workNumber) {
		return ApiV2Response.OK(xAssignment.cancelApplication(getCurrentUser(), workNumber));
	}
}
