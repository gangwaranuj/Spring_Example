package com.workmarket.api.v2.employer.surveys.controllers;

import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.api.v2.model.SurveyDTO;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.domains.model.assessment.SurveyAssessment;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.dto.AssessmentDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mapType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.surveyType;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class SurveysControllerIT extends ApiV2BaseIT {
	private static final String ENDPOINT = "/employer/v2/surveys";

	@Autowired private AssessmentService assessmentService;

	private SurveyAssessment survey;

	@Before
	public void setUp() throws Exception {
		login();

		AssessmentDTO assessmentDTO = new AssessmentDTO();
		assessmentDTO.setName("A Survey");
		assessmentDTO.setDescription("A description.");
		assessmentDTO.setIndustryId(INDUSTRY_1000_ID);
		assessmentDTO.setAssessmentStatusTypeCode(AssessmentStatusType.ACTIVE);
		assessmentDTO.setType(AbstractAssessment.SURVEY_ASSESSMENT_TYPE);

		survey = (SurveyAssessment) assessmentService.saveOrUpdateAssessment(user.getId(), assessmentDTO);
	}

	@Test
	public void getSurveys() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, hasEntry("id", String.valueOf(survey.getId())));
		assertThat(result, hasEntry("name", String.valueOf(survey.getName())));
	}

	@Test
	public void getSurveysWithFields() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "id", "name", "description")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, hasEntry("id", String.valueOf(survey.getId())));
		assertThat(result, hasEntry("name", String.valueOf(survey.getName())));
		assertThat(result, hasEntry("description", String.valueOf(survey.getDescription())));
	}

	@Test
	public void getSurveysWithOneField() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT).param("fields", "id")
		).andExpect(status().isOk()).andReturn();

		SurveyDTO surveyDTO = getFirstResult(mvcResult, surveyType);

		assertNotNull("Expected Survey result to have an id", surveyDTO.getId());
		assertNull("Expected Survey result to have no name", surveyDTO.getName());
		assertNull("Expected Survey result to not have name value", surveyDTO.getName());
		assertNull("Expected Survey result to not have description value", surveyDTO.getDescription());
		assertNull("Expected Survey result to not have required value", surveyDTO.getRequired());
	}

	@Test
	public void getSurveysWithTwoFields() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT).param("fields", "name", "description")
		).andExpect(status().isOk()).andReturn();

		SurveyDTO surveyDTO = getFirstResult(mvcResult, surveyType);

		assertNull("Expected Survey result have no id", surveyDTO.getId());
		assertNotNull("Expected Survey result to have a  name", surveyDTO.getName());
		assertNotNull("Expected Survey result to have a description", surveyDTO.getDescription());
		assertNull("Expected Survey result to not have required value", surveyDTO.getRequired());
	}

	@Test
	public void getSurveysWithWonkyField() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT).param("fields", "wonkyField")
		).andExpect(status().isOk()).andReturn();

		SurveyDTO surveyDTO = getFirstResult(mvcResult, surveyType);

		assertNull("Expected Survey result to not have required value", surveyDTO.getRequired());
	}
}
