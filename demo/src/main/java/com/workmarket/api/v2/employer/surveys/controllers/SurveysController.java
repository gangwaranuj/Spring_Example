package com.workmarket.api.v2.employer.surveys.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.SurveyDTO;
import com.workmarket.domains.model.assessment.SurveyAssessment;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.infra.business.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(tags = "Surveys")
@Controller("employerSurveysController")
@RequestMapping(value = {"/v2/employer/surveys", "/employer/v2/surveys"})
public class SurveysController extends ApiBaseController {
	@Autowired private AssessmentService assessmentService;
	@Autowired private AuthenticationService authenticationService;

	@ApiOperation(value = "List surveys")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<SurveyDTO> getSurveys() throws Exception {
		List<SurveyDTO> surveyDTOs = new LinkedList<>();

		for (SurveyAssessment entry : assessmentService.getSurveys()) {
			surveyDTOs.add(new SurveyDTO.Builder(entry).build());
		}

		return ApiV2Response.valueWithResults(surveyDTOs);
	}
}
