package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.exceptions.BadRequestApiException;
import com.workmarket.api.exceptions.ForbiddenException;
import com.workmarket.api.exceptions.GenericApiException;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.fulfillment.NegotiationFulfillmentProcessor;
import com.workmarket.api.v2.worker.model.NegotiationDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@RequestMapping("/worker/v2/assignments")
@Controller(value = "workerAssignmentPricingController")
public class AssignmentNegotiationController extends ApiBaseController {
	private static final Log logger = LogFactory.getLog(AssignmentsController.class);

	@Autowired private NegotiationFulfillmentProcessor negotiationFulfillmentProcessor;

	@ApiOperation(value="Request a budget increase")
	@RequestMapping(value = "/{workNumber}/pricing/budgetIncrease",
									method = POST,
									produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postBudgetIncreaseRequest(@PathVariable("workNumber") final String workNumber,
																								 @RequestBody final NegotiationDTO pricingModel,
																								 final HttpServletResponse response) throws Exception {

		final List<String> inputValidationErrors = validateBudgetIncreaseRequest(pricingModel);

		if (CollectionUtils.isNotEmpty(inputValidationErrors)) {
			throw new GenericApiException("Unable to validate budget increase request", inputValidationErrors);
		}

		final FulfillmentPayloadDTO result = negotiationFulfillmentProcessor.requestBudgetIncrease(workNumber,
																																															 pricingModel);
		if (!result.isSuccessful()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}

		if(result.isSuccessful()) {
			return new ApiV2Response(new ApiJSONPayloadMap(), result.getPayload());
		}
		throw new BadRequestApiException("Unable to request budget increase");
	}

	@ApiOperation(value="Request an expense reimbursement")
	@RequestMapping(value = "/{workNumber}/pricing/expenseReimbursement",
									method = POST,
									produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postExpenseReimbursementRequest(@PathVariable("workNumber") final String workNumber,
																											 @RequestBody final NegotiationDTO reimburseModel,
																											 final HttpServletResponse response) throws Exception {

		final List<String> inputValidationErrors = validateReimbursementRequest(reimburseModel);

		if (CollectionUtils.isNotEmpty(inputValidationErrors)) {
			throw new GenericApiException("Unable to validate reimbursement request", inputValidationErrors);
		}

		final FulfillmentPayloadDTO result = negotiationFulfillmentProcessor.requestReimbursement(workNumber,
																																															reimburseModel);
		if(result.isSuccessful()) {
			return new ApiV2Response(new ApiJSONPayloadMap(), result.getPayload());
		}
		throw new ForbiddenException("Not allowed to request reimbursement");
	}

	@ApiOperation(value="Request a bonus")
	@RequestMapping(value = "/{workNumber}/pricing/bonus", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postBonusRequest(@PathVariable("workNumber") final String workNumber,
																				@RequestBody final NegotiationDTO bonusModel,
																				final HttpServletResponse response) throws Exception {

		final List<String> inputValidationErrors = validateBonusRequest(bonusModel);

		if (CollectionUtils.isNotEmpty(inputValidationErrors)) {
			throw new GenericApiException("Unable to validate bonus request", inputValidationErrors);
		}

		final FulfillmentPayloadDTO result = negotiationFulfillmentProcessor.requestBonus(workNumber, bonusModel);
		if(result.isSuccessful()) {
			return new ApiV2Response(new ApiJSONPayloadMap(), result.getPayload());
		}
		throw new ForbiddenException("Not allowed to request bonus");
	}


	private List<String> validateBudgetIncreaseRequest(final NegotiationDTO negotiationDTO) {

		final List<String> errors = new LinkedList<>();

		if (StringUtils.isBlank(negotiationDTO.getNote())) {
			errors.add(messageHelper.getMessage("NotEmpty.budgetIncreaseForm.note"));
		}

		if ((negotiationDTO.getFlatPrice() == null || negotiationDTO.getFlatPrice() <= 0) && (negotiationDTO.getBlendedPerHour() == null || negotiationDTO
						.getBlendedPerHour() <= 0) && (negotiationDTO.getInitialHours() == null || negotiationDTO.getInitialHours() <= 0) && (negotiationDTO
						.getMaxAdditionalHours() == null || negotiationDTO.getMaxAdditionalHours() <= 0) && (negotiationDTO.getMaxHours() == null || negotiationDTO
						.getMaxHours() <= 0) && (negotiationDTO.getMaxUnits() == null || negotiationDTO.getMaxUnits() <= 0) && (negotiationDTO
						.getPerAdditionalHour() == null || negotiationDTO.getPerAdditionalHour() <= 0) && (negotiationDTO.getPerHour() == null || negotiationDTO
						.getPerHour() <= 0) && (negotiationDTO.getPerUnit() == null || negotiationDTO.getPerUnit() <= 0)) {

			errors.add(messageHelper.getMessage("NotEmpty.budgetIncreaseForm.price"));
		}

		return errors;
	}

	private List<String> validateReimbursementRequest(final NegotiationDTO negotiationDTO) {

		final List<String> errors = new LinkedList<>();

		if (negotiationDTO.getReimbursement() == null) {
			errors.add(messageHelper.getMessage("NotNull.reimbursementForm.additional_expenses"));
		}
		if (negotiationDTO.getReimbursement() != null && negotiationDTO.getReimbursement() <= 0) {
			errors.add(messageHelper.getMessage("Size.reimbursementForm"));
		}
		if (StringUtils.isBlank(negotiationDTO.getNote())) {
			errors.add(messageHelper.getMessage("NotEmpty.reimbursementForm.note"));
		}

		return errors;
	}

	private List<String> validateBonusRequest(final NegotiationDTO negotiationDTO) {

		final List<String> errors = new LinkedList<>();

		if (negotiationDTO.getBonus() == null || negotiationDTO.getBonus() <= 0) {
			errors.add(messageHelper.getMessage("NotNull.bonusForm.bonus"));
		}
		if (StringUtils.isBlank(negotiationDTO.getNote())) {
			errors.add(messageHelper.getMessage("NotEmpty.bonusForm.note"));
		}

		return errors;
	}
}
