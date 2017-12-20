package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.worker.model.CheckInDTO;
import com.workmarket.api.v2.worker.model.CheckOutDTO;
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
@Controller("CheckInOutController")
@RequestMapping("/worker/v2/assignments")
public class CheckInOutController extends ApiBaseController {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(
					CheckInOutController.class);

	@Autowired private XAssignment xAssignment;

	@ApiOperation("Check-in to an assignment")
	@RequestMapping(value = "/{workNumber}/checkin", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postCheckInAssignment(@PathVariable("workNumber") final String workNumber,
															 @Valid @RequestBody(required = false) final CheckInDTO checkInDTO) {

		xAssignment.checkIn(getCurrentUser().getId(), workNumber, null, checkInDTO);

		return ApiV2Response.OK();
	}

	@ApiOperation("[DEPRECATED] Check-in to an assignment")
	@Deprecated
	@RequestMapping(value = "/checkin/{workNumber}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postCheckInAssignmentDeprecated(@PathVariable("workNumber") final String workNumber,
																				 @Valid @RequestBody(required = false) final CheckInDTO checkInDTO) {

		xAssignment.checkIn(getCurrentUser().getId(), workNumber, null, checkInDTO);

		return ApiV2Response.OK();
	}

	@ApiOperation("Update a Check-in")
	@RequestMapping(value = "/{workNumber}/checkin/{checkInOutPairId}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postUpdateCheckInAssignment(@PathVariable("workNumber") final String workNumber,
																		 @PathVariable("checkInOutPairId") final Long checkInOutPairId,
																		 @Valid @RequestBody final CheckInDTO checkInDTO) {

		xAssignment.checkIn(getCurrentUser().getId(), workNumber, checkInOutPairId, checkInDTO);

		return ApiV2Response.OK();
	}

	@ApiOperation("[DEPRECATED] Update a Check-in")
	@Deprecated
	@RequestMapping(value = "/checkin/{workNumber}/{checkInOutPairId}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postUpdateCheckInAssignmentDeprecated(@PathVariable("workNumber") final String workNumber,
																							 @PathVariable("checkInOutPairId") Long checkInOutPairId,
																							 @Valid @RequestBody final CheckInDTO checkInDTO) {

		xAssignment.checkIn(getCurrentUser().getId(), workNumber, checkInOutPairId, checkInDTO);

		return ApiV2Response.OK();
	}

	@ApiOperation("Check-out of an assignment")
	@RequestMapping(value = "/{workNumber}/checkout", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postCheckOutAssignment(@PathVariable("workNumber") final String workNumber,
																@Valid @RequestBody(required = false) final CheckOutDTO checkOutDTO) {

		xAssignment.checkOut(getCurrentUser().getId(), workNumber, null, checkOutDTO);

		return ApiV2Response.OK();
	}

	@ApiOperation("[DEPRECATED] Check-out of an assignment")
	@Deprecated
	@RequestMapping(value = "/checkout/{workNumber}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postCheckOutAssignmentDeprecated(@PathVariable("workNumber") final String workNumber,
																					@Valid @RequestBody(required = false) CheckOutDTO checkOutDTO) {

		xAssignment.checkOut(getCurrentUser().getId(), workNumber, null, checkOutDTO);

		return ApiV2Response.OK();
	}

	@ApiOperation("Update a Check-out")
	@RequestMapping(value = "/{workNumber}/checkout/{checkInOutPairId}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postUpdateCheckOutAssignment(@PathVariable("workNumber") final String workNumber,
																			@PathVariable("checkInOutPairId") final Long checkInOutPairId,
																			@Valid @RequestBody final CheckOutDTO checkOutDTO) {

		xAssignment.checkOut(getCurrentUser().getId(), workNumber, checkInOutPairId, checkOutDTO);

		return ApiV2Response.OK();
	}

	@ApiOperation("[DEPRECATED] Update a Check-out")
	@Deprecated
	@RequestMapping(value = "/checkout/{workNumber}/{checkInOutPairId}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postUpdateCheckOutAssignmentDeprecated(@PathVariable("workNumber") final String workNumber,
																								@PathVariable("checkInOutPairId") final Long checkInOutPairId,
																								@Valid @RequestBody final CheckOutDTO checkOutDTO) {

		xAssignment.checkOut(getCurrentUser().getId(), workNumber, checkInOutPairId, checkOutDTO);

		return ApiV2Response.OK();
	}
}
