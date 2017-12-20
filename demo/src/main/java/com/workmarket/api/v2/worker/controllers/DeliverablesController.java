package com.workmarket.api.v2.worker.controllers;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.model.AddDeliverableDTO;
import com.workmarket.api.v2.worker.model.AttachmentDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.domains.model.asset.Asset;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("DeliverablesController")
@RequestMapping("/worker/v2/assignments")
public class DeliverablesController extends ApiBaseController {
	@Autowired private XAssignment xAssignment;

	@ApiOperation("Add a deliverable to an assignment")
	@RequestMapping(
		value = "/{workNumber}/deliverables",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response postAddDeliverable(
		@PathVariable("workNumber") final String workNumber,
		@Valid @RequestBody final AddDeliverableDTO addDeliverableDTO
	) throws Exception {
		final List result = xAssignment.addDeliverable(
			getCurrentUser(),
			workNumber,
			null,
			null,
			addDeliverableDTO
		);

		final Asset asset = (Asset) result.get(0);

		return ApiV2Response.OK(
			ImmutableList.of(
				new AttachmentDTO(
					asset.getUUID(),
					String.format("/asset/%s", asset.getUUID()),
					asset.getDescription(),
					asset.getName(),
					asset.getMimeType(),
					asset.getFileByteSize()
				)
			)
		);
	}

	@ApiOperation("Add a deliverable to a requirement set")
	@RequestMapping(
		value = "/{workNumber}/deliverables/{deliverableRequirementId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response postAddDeliverableToRequirementSet(
		@PathVariable("workNumber") final String workNumber,
		@PathVariable("deliverableRequirementId") final Long deliverableRequirementId,
		@Valid @RequestBody final AddDeliverableDTO addDeliverableDTO
	) throws Exception {
		return ApiV2Response.OK(
			xAssignment.addDeliverable(
				getCurrentUser(),
				workNumber,
				deliverableRequirementId,
				null,
				addDeliverableDTO
			)
		);
	}

	@ApiOperation("Update a deliverable for a requirement set")
	@RequestMapping(
		value = "/{workNumber}/deliverables/{deliverableRequirementId}/{deliverableId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response postUpdateDeliverable(
		@PathVariable("workNumber") final String workNumber,
		@PathVariable("deliverableRequirementId") final Long deliverableRequirementId,
		@PathVariable("deliverableId") final Long deliverableId,
		@Valid @RequestBody final AddDeliverableDTO addDeliverableDTO
	) throws Exception {
		return ApiV2Response.OK(
			xAssignment.addDeliverable(
				getCurrentUser(),
				workNumber,
				deliverableRequirementId,
				deliverableId,
				addDeliverableDTO
			)
		);
	}

	@ApiOperation("Delete a deliverable by Id")
	@RequestMapping(
		value = "/{workNumber}/deliverables/{deliverableId}/remove",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response postDeleteDeliverableById(
		@PathVariable("workNumber") final String workNumber,
		@PathVariable("deliverableId") final Long deliverableId
	) {
		return ApiV2Response.OK(
			xAssignment.deleteDeliverable(
				getCurrentUser(),
				workNumber,
				deliverableId
			)
		);
	}

	@ApiOperation("Delete a deliverable by UUID")
	@RequestMapping(
		value = "/{workNumber}/deliverables/{assetUuid}/removeUuid",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response postDeleteDeliverableByUUID(
		@PathVariable("workNumber") final String workNumber,
		@PathVariable("assetUuid") final String assetUuid
	) {
		return ApiV2Response.OK(
			xAssignment.deleteDeliverable(
				getCurrentUser(),
				workNumber,
				assetUuid
			)
		);
	}
}
