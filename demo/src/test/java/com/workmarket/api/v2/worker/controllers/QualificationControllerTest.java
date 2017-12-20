package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.ApiJobTitleDTO;
import com.workmarket.api.v2.model.SkillApiDTO;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.qualification.SkillRecommenderDTO;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationBuilder;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.service.business.qualification.QualificationRecommender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class QualificationControllerTest extends BaseApiControllerTest {
  private static final TypeReference<ApiV2Response<ApiJobTitleDTO>> apiV2ResponseTypeJobTitleDto =
      new TypeReference<ApiV2Response<ApiJobTitleDTO>>() { };
  private static final TypeReference<ApiV2Response<SkillApiDTO>> apiV2ResponseTypeSkillDto =
      new TypeReference<ApiV2Response<SkillApiDTO>>() { };
  private static final TypeReference<ApiV2Response<com.workmarket.domains.onboarding.model.Qualification>>
      apiV2ResponseTypeQualification =
      new TypeReference<ApiV2Response<com.workmarket.domains.onboarding.model.Qualification>>() { };
  private static final String AUTO_COMPLETE_JOB_TITLE_URL = "/v2/suggest/jobTitle?q={query}";
  private static final String AUTO_COMPLETE_SKILL_URL = "/v2/suggest/skill?q={query}";
  private static final String AUTO_COMPLETE_RECOMMEND_SKILL_URL = "/v2/recommend/skill";

  @Mock
  private QualificationRecommender qualificationRecommender;

  @InjectMocks
  private QualificationController controller;

  @Before
  public void setup() throws Exception {
    super.setup(controller);
  }

  @Test
  public void autoCompleteJobTitle_withValidQuery_shouldReturn200() throws Exception {
    final String query = "c";
    final ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    final Qualification[] titles = new Qualification[1];
    final QualificationBuilder builder = new QualificationBuilder();
    final String jobTitle = "Computer Programmer";
    titles[0] = builder.setName(jobTitle).setIsApproved(true).setQualificationType(QualificationType.job_title).build();

    when(qualificationRecommender
        .getSuggestedQualifications(queryCaptor.capture(), eq(QualificationType.job_title), any(RequestContext.class)))
        .thenReturn(Observable.from(titles));

    final MvcResult result =
        mockMvc
            .perform(get(AUTO_COMPLETE_JOB_TITLE_URL, query).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    final ApiV2Response<ApiJobTitleDTO> response = expectApiV2Response(result, apiV2ResponseTypeJobTitleDto);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(200, responseMeta);

    assertEquals(query, queryCaptor.getValue());
  }

  @Test
  public void autoCompleteJobTitle_withEmptyQuery_shouldReturn200() throws Exception {
    final String query = "";
    final ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    final Qualification[] titles = new Qualification[1];
    final QualificationBuilder builder = new QualificationBuilder();
    final String jobTitle = "Computer Programmer";
    titles[0] = builder.setName(jobTitle).setIsApproved(true).setQualificationType(QualificationType.job_title).build();

    when(qualificationRecommender.getSuggestedQualifications(queryCaptor.capture(), eq(QualificationType.job_title),
        any(RequestContext.class)))
        .thenReturn(Observable.from(titles));

    final MvcResult result =
        mockMvc
            .perform(get(AUTO_COMPLETE_JOB_TITLE_URL, query).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    final ApiV2Response<ApiJobTitleDTO> response = expectApiV2Response(result, apiV2ResponseTypeJobTitleDto);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(200, responseMeta);

    assertEquals(query, queryCaptor.getValue());
  }

  @Test
  public void autoCompleteJobTitle_shouldThrow500OnException() throws Exception {
    final String query = "c";

    when(qualificationRecommender.getSuggestedQualifications(eq(query), eq(QualificationType.job_title),
        any(RequestContext.class)))
        .thenReturn(Observable.<Qualification>error(new Exception("uh-oh")));

    final MvcResult result =
        mockMvc
            .perform(get(AUTO_COMPLETE_JOB_TITLE_URL, query).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(500))
            .andReturn();

    final ApiV2Response<ApiJobTitleDTO> response = expectApiV2Response(result, apiV2ResponseTypeJobTitleDto);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(500, responseMeta);
  }

  @Test
  public void autoCompleteSkills_shouldReturn200() throws Exception {
    final String query = "c";
    final ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    final Qualification[] skills = new Qualification[1];
    final QualificationBuilder builder = new QualificationBuilder();
    final String skill = "SharePoint";
    skills[0] = builder.setName(skill).setIsApproved(true).setQualificationType(QualificationType.skill).build();

    when(qualificationRecommender
        .getSuggestedSkillsMerged(queryCaptor.capture(), any(RequestContext.class)))
        .thenReturn(Observable.from(skills));

    final MvcResult result =
        mockMvc
            .perform(get(AUTO_COMPLETE_SKILL_URL, query).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    final ApiV2Response<SkillApiDTO> response = expectApiV2Response(result, apiV2ResponseTypeSkillDto);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(200, responseMeta);

    assertEquals(1, response.getResults().size());
    assertEquals(skill, response.getResults().get(0).getName());
    assertEquals(query, queryCaptor.getValue());
  }

  @Test
  public void autoCompleteSkills_shouldReturn500OnError() throws Exception {
    final String query = "c";

    when(qualificationRecommender.getSuggestedSkillsMerged(eq(query), any(RequestContext.class)))
        .thenReturn(Observable.<Qualification>error(new Exception("uh-oh")));

    final MvcResult result =
        mockMvc
            .perform(get(AUTO_COMPLETE_SKILL_URL, query).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(500))
            .andReturn();

    final ApiV2Response<SkillApiDTO> response = expectApiV2Response(result, apiV2ResponseTypeSkillDto);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(500, responseMeta);
  }

  @Test
  public void shouldRecommendSkillsReturn200() throws Exception {
    final SkillRecommenderDTO dto = new SkillRecommenderDTO();
    final String body = new ObjectMapper().writeValueAsString(dto);
    final com.workmarket.domains.onboarding.model.Qualification qualification =
        new com.workmarket.domains.onboarding.model.Qualification(
            1L, "name", 1.1D, com.workmarket.domains.onboarding.model.Qualification.Type.SKILL);

    when(qualificationRecommender.recommendSkillsAndHydrate(any(SkillRecommenderDTO.class), any(RequestContext.class)))
        .thenReturn(Observable.just(qualification));

    final MvcResult result =
        mockMvc
            .perform(post(AUTO_COMPLETE_RECOMMEND_SKILL_URL)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    final ApiV2Response<com.workmarket.domains.onboarding.model.Qualification> response
        = expectApiV2Response(result, apiV2ResponseTypeQualification);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(200, responseMeta);

    assertEquals(1, response.getResults().size());
    final com.workmarket.domains.onboarding.model.Qualification q = response.getResults().get(0);
    assertEquals((Long)1L, q.getId());
    assertEquals("name", q.getName());
    assertEquals((Double)1.1D, q.getScore());
    assertEquals(com.workmarket.domains.onboarding.model.Qualification.Type.SKILL, q.getType());
  }
}
