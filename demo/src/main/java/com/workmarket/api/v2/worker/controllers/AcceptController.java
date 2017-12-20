package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.worker.model.ApiAcceptWorkDTO;
import com.workmarket.api.v2.worker.model.ApiDeclineWorkDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("AcceptController")
@RequestMapping("/worker/v2/assignments")
public class AcceptController extends ApiBaseController {
	@Autowired private XAssignment xAssignment;

	@ApiOperation(value="Accept an assignment")
	@RequestMapping(value = "/{workNumber}/accept", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response<ApiAcceptWorkDTO> postAcceptAssignment(@PathVariable("workNumber") final String workNumber) {
		return acceptAssignment(getCurrentUser().getId(), workNumber);
	}

	@ApiOperation(value="[DEPRECATED] Accept an assignment")
	@Deprecated
	@RequestMapping(value = "/accept/{workNumber}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response<ApiAcceptWorkDTO> postAcceptAssignmentDeprecated(@PathVariable("workNumber") String workNumber) {
		return acceptAssignment(getCurrentUser().getId(), workNumber);
	}

	private ApiV2Response<ApiAcceptWorkDTO> acceptAssignment(final Long userId, final String workNumber) {
		final AcceptWorkResponse acceptWorkResponse = xAssignment.accept(userId, workNumber);
		return ApiV2Response.valueWithResult(new ApiAcceptWorkDTO.Builder(acceptWorkResponse).build());
	}

	@ApiOperation(value="Decline an assignment")
	@RequestMapping(value = "/{workNumber}/decline", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response<ApiDeclineWorkDTO> postDeclineAssignment(@PathVariable("workNumber") final String workNumber) {
		return ApiV2Response.valueWithResult(new ApiDeclineWorkDTO.Builder(xAssignment.decline(getCurrentUser().getId(), workNumber)).build());
	}

	@ApiOperation(value="[DEPRECATED] Decline an assignment")
	@Deprecated
	@RequestMapping(value = "/reject/{workNumber}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response<ApiDeclineWorkDTO> postRejectAssignmentDeprecated(@PathVariable("workNumber") final String workNumber) {
		return ApiV2Response.valueWithResult(new ApiDeclineWorkDTO.Builder(xAssignment.decline(getCurrentUser().getId(), workNumber)).build());
	}
}
