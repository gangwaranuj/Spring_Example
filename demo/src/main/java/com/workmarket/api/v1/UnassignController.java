package com.workmarket.api.v1;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.workmarket.api.ApiBaseController;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.UnassignDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Api(tags = "Assignments")
@Controller("unassignController")
@RequestMapping(value = {"/v1/employer/assignments", "/api/v1/assignments"})
public class UnassignController extends ApiBaseController {

	@Autowired private WorkService workService;
	
	@ApiOperation(value = "Unassign worker")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/{assignmentId}/unassign", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> unassignAssignment(
		@PathVariable(value = "assignmentId") String assignmentId,
		@RequestParam(value = "cancellation_reason") String cancellationReasonTypeCode,
		@RequestParam(value = "rollback_to_original_price") boolean rollbackToOriginalPrice,
		@RequestParam(value = "note") String note) throws Exception {

		final Work work = workService.findWorkByWorkNumber(assignmentId);

		if (work == null || !Objects.equals(work.getCompany().getId(), getCurrentUser().getCompanyId())) {
			return ApiV1Response.of(false, HttpStatus.SC_FORBIDDEN);
		}

		UnassignDTO unassignDTO = new UnassignDTO()
			.setCancellationReasonTypeCode(cancellationReasonTypeCode)
			.setRollbackToOriginalPrice(rollbackToOriginalPrice)
			.setNote(note)
			.setWorkId(work.getId());
		
		List<ConstraintViolation> violations = workService.unassignWorker(unassignDTO);

		if(isNotEmpty(violations)) {
			return ApiV1Response.of(false, convertConstraintsToStrings(violations));
		}

		return ApiV1Response.of(true);
	}

	private List<String> convertConstraintsToStrings(List<ConstraintViolation> constraintViolations) {
		return Lists.transform(constraintViolations, new Function<ConstraintViolation, String>() {
			@Override
			public String apply(ConstraintViolation constraintViolation) {
				return constraintViolation.toString();
			}
		});
	}
}
