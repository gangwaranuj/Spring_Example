package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.worker.model.UpdateAssetDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("AssetsController")
@RequestMapping("/worker/v2/assignments")
public class AssetsController extends ApiBaseController {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(
					AssetsController.class);

	@Autowired private XAssignment xAssignment;

	@ApiOperation(value="Get an asset for an assignment")
	@RequestMapping(value = "/{workNumber}/assets/{assetUUID}", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getAssignmentAsset(@PathVariable("workNumber") final String workNumber,
																@PathVariable("assetUUID") final String assetUUID) {

		return ApiV2Response.OK(xAssignment.getAsset(getCurrentUser(), workNumber, assetUUID));
	}

	@ApiOperation(value="Update an asset of an assignment")
	@RequestMapping(value = "/{workNumber}/assets/{assetUUID}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postUpdateAssignmentAsset(@PathVariable("workNumber") final String workNumber,
																	 @PathVariable("assetUUID") final String assetUUID,
																	 @Valid @RequestBody final UpdateAssetDTO updateAssetDTO,
																	 final BindingResult bindingResult) {

		return ApiV2Response.OK(xAssignment.updateAsset(getCurrentUser(),
																										workNumber,
																										assetUUID,
																										updateAssetDTO,
																										bindingResult));
	}
}
