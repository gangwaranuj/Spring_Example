package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.worker.model.RescheduleDTO;
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
@Controller("RescheduleController")
@RequestMapping("/worker/v2/assignments")
public class RescheduleController extends ApiBaseController {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(
					RescheduleController.class);

	@Autowired private XAssignment xAssignment;

	@ApiOperation("Reschedule an assignment")
	@RequestMapping(value = "/{workNumber}/reschedule", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postRescheduleAssignment(@PathVariable("workNumber") final String workNumber,
																	@Valid @RequestBody final RescheduleDTO rescheduleDTO) {

		return ApiV2Response.OK(xAssignment.reschedule(workNumber, rescheduleDTO));
	}

	@ApiOperation("[DEPRECATED] Reschedule an assignment")
	@RequestMapping(value = "/reschedule/{workNumber}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postRescheduleAssignmentDeprecated(@PathVariable("workNumber") final String workNumber,
																						@Valid @RequestBody final RescheduleDTO rescheduleDTO) {

		return ApiV2Response.OK(xAssignment.reschedule(workNumber, rescheduleDTO));
	}

    /*
			@RequestMapping(
      value = "/reschedule/{workNumber}/cancel",
      method = POST,
      produces = APPLICATION_JSON_VALUE
      )
      public
      @ResponseBody ApiResponse
      reschedule(@PathVariable("workNumber") String workNumber) {

      return ApiResponse.OK(xAssignment.cancelReschedule(getCurrentUser(),
      workNumber));
      }
    */
}
