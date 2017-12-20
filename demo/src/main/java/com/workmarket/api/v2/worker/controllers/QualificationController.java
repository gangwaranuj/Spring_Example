package com.workmarket.api.v2.worker.controllers;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.ApiJobTitleDTO;
import com.workmarket.api.v2.model.SkillApiDTO;
import com.workmarket.domains.model.qualification.SkillRecommenderDTO;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.service.business.qualification.QualificationRecommender;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.NumberUtilities;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import rx.functions.Action1;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = "Qualitifications")
@Controller(value = "qualificationController")
public class QualificationController extends ApiBaseController {

  private static final Log logger = LogFactory.getLog(QualificationController.class);
  @Autowired
  private QualificationRecommender qualificationRecommender;
  @Autowired
  private WebRequestContextProvider webRequestContextProvider;

  @ApiOperation(
      value = "Get a list of predefined job titles prefixed by q query param",
      notes = "Returns an empty array if query param is empty",
      response = ApiJobTitleDTO.class)
  @ApiResponses(value = {
      @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden"),
      @io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error")})
  @RequestMapping(value = "/v2/suggest/jobTitle", method = GET, produces = APPLICATION_JSON_VALUE)
  public @ResponseBody ApiV2Response<ApiJobTitleDTO> getJobTitlesWithPrefix(
      @RequestParam(value = "q", required = false, defaultValue = "") final String searchTerm) throws Exception {
    final ImmutableList.Builder<ApiJobTitleDTO> jobTitlesBuilder = ImmutableList.builder();
    qualificationRecommender
        .getSuggestedQualifications(searchTerm, QualificationType.job_title, webRequestContextProvider.getRequestContext())
        .subscribe(
            new Action1<Qualification>() {
              @Override
              public void call(final Qualification qualification) {
                jobTitlesBuilder.add(new ApiJobTitleDTO(qualification.getUuid(), qualification.getName()));
              }
            });

    return ApiV2Response.OK(jobTitlesBuilder.build());
  }

  @ApiOperation(
      value = "Get a list of predefined skills prefixed by q query param. Used for type-ahead purposes. " +
          "This is more fuzzy name resolver and is not a recommender. For that see /v2/recommend/skill.",
      notes = "Returns an empty array if query param is empty",
      response = SkillApiDTO.class)
  @ApiResponses(value = {
      @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden"),
      @io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error")})
  @RequestMapping(value = "/v2/suggest/skill", method = GET, produces = APPLICATION_JSON_VALUE)
  public @ResponseBody ApiV2Response<SkillApiDTO> getSkillsWithPrefix(
      @RequestParam(value = "q", required = false, defaultValue = "") final String searchTerm) throws Exception {
    final ImmutableList.Builder<SkillApiDTO> skillsBuilder = ImmutableList.builder();
    qualificationRecommender
        .getSuggestedSkillsMerged(searchTerm, webRequestContextProvider.getRequestContext())
        .subscribe(
            new Action1<Qualification>() {
              @Override
              public void call(final Qualification qualification) {
                final SkillApiDTO.Builder builder = new SkillApiDTO.Builder().name(qualification.getName());

                // Because we merge db data with microservice data, sometimes the UUID returned is an ID (mono db
                // data has IDs not UUIDs; micro-service has UUIDs but not IDs), so let's parse and check
                // and react appropriately
                final Long id = NumberUtilities.safeParseLong(qualification.getUuid());
                if (id == null) {
                  builder.uuid(qualification.getUuid());
                } else {
                  builder.id(id);
                }

                skillsBuilder.add(builder.build());
              }
            });

    return ApiV2Response.OK(skillsBuilder.build());
  }

  @ApiOperation(
      value = "Recommend a list of skills given some contextual information, industry ID, job titles, etc.",
      notes = "Returns an empty array if query param is empty",
      response = com.workmarket.domains.onboarding.model.Qualification.class)
  @ApiResponses(value = {
      @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden"),
      @io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error")})
  @RequestMapping(value = "/v2/recommend/skill", method = POST, produces = APPLICATION_JSON_VALUE)
  public @ResponseBody ApiV2Response<com.workmarket.domains.onboarding.model.Qualification> postRecommendSkills(
      @RequestBody SkillRecommenderDTO dto) throws Exception {

    return ApiV2Response.OK(
        qualificationRecommender
            .recommendSkillsAndHydrate(dto, webRequestContextProvider.getRequestContext())
            .toList()
            .toBlocking()
            .single());
  }
}
