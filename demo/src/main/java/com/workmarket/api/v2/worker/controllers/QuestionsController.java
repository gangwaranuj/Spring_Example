package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.ApiV2Pagination;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.marshaller.AssignmentMarshaller;
import com.workmarket.api.v2.worker.model.QuestionDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.service.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("QuestionsController")
@RequestMapping("/worker/v2/assignments")
public class QuestionsController extends ApiBaseController {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(
					QuestionsController.class);

	@Autowired private XAssignment xAssignment;
	@Autowired private AssignmentMarshaller assignmentMarshaller;
	@Autowired private UserService userService;

	@ApiOperation("Get all questions for an assignment")
	@RequestMapping(value = "/{workNumber}/questions", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getQuestions(@PathVariable final String workNumber,
																		@RequestParam(required = false) final Integer page,
																		@RequestParam(required = false) final Integer pageSize,
																		final HttpServletRequest request) {

		final List<Map<String, Object>> workQuestionAnswerPairs = xAssignment.getQuestions(getCurrentUser(),
																																											 workNumber,
																																											 page,
																																											 pageSize);

		final List<Map<String, Object>> results = workQuestionAnswerPairs;

		final ApiV2Pagination apiPagination = new ApiV2Pagination.ApiPaginationBuilder().page(1L)
						.pageSize(100L)
						.totalPageCount(1L)
						.totalRecordCount(new Long(workQuestionAnswerPairs.size()))
						.build(request);

		final FulfillmentPayloadDTO fulfillmentResponse = new FulfillmentPayloadDTO();
		fulfillmentResponse.setPayload(results);
		fulfillmentResponse.setPagination(apiPagination);

		final ApiJSONPayloadMap metadataBuilder = new ApiJSONPayloadMap();
		metadataBuilder.put("code", HttpServletResponse.SC_OK);

		return new ApiV2Response(metadataBuilder,
														 fulfillmentResponse.getPayload(),
														 fulfillmentResponse.getPagination());
	}

	@ApiOperation("Add a question to an assignment")
	@RequestMapping(value = "/{workNumber}/questions", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postAddQuestion(@PathVariable final String workNumber,
																	 @Valid @RequestBody QuestionDTO questionDTO) {

		WorkQuestionAnswerPair pair = xAssignment.askQuestion(getCurrentUser(), workNumber, questionDTO);

		List results = new ArrayList();
		results.add(pair);

		return ApiV2Response.OK();//results);
	}
}
