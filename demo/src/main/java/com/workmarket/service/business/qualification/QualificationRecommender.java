package com.workmarket.service.business.qualification;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.qualification.SkillRecommenderDTO;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationClient;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.search.qualification.SearchRequest;
import com.workmarket.business.recommendation.SkillRecommenderClient;
import com.workmarket.business.recommendation.skill.model.RecommendSkillResponse;
import com.workmarket.business.recommendation.skill.model.Worker;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.SpecialtyService;
import com.workmarket.service.infra.business.SuggestionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Recommendation of qualification.
 */
@Service
public class QualificationRecommender {
  private static final Logger logger = LoggerFactory.getLogger(QualificationRecommender.class);
  @Autowired
  private MetricRegistry metricRegistry;

  private WMMetricRegistryFacade wmMetricRegistryFacade;
  private QualificationClient qualificationClient;
  private SkillRecommenderClient skillRecommenderClient;
  private SkillService skillService;
  private SpecialtyService specialtyService;
  private SuggestionService suggestionsService;

  @PostConstruct
  void init() {
    wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "qualificationservice");
  }

  @Autowired
  public QualificationRecommender(
      final QualificationClient qualificationClient,
      final SkillRecommenderClient skillRecommenderClient,
      final SkillService skillService,
      final SpecialtyService specialtyService,
      final SuggestionService suggestionService) {
    this.qualificationClient = qualificationClient;
    this.skillRecommenderClient = skillRecommenderClient;
    this.skillService = skillService;
    this.specialtyService = specialtyService;
    this.suggestionsService = suggestionService;
  }

  /**
   * Gets a list of qualifications.
   *
   * @param qualificationType qualification type
   * @param requestContext    request context
   * @return Observable
   */
  public Observable<Qualification> getQualifications(
      final QualificationType qualificationType,
      final RequestContext requestContext) {
    final SearchRequest qualificationRequest = SearchRequest.builder()
        .setQualificationType(qualificationType)
        .setIsApproved(Boolean.TRUE)
        .build();
    return qualificationClient.searchQualifications(qualificationRequest, requestContext);
  }

  /**
   * Recommends skills.
   *
   * @param skillRecommenderDTO skill recommender dto
   * @param requestContext      request context
   * @return Observable
   */
  public Observable<RecommendSkillResponse> recommendSkills(
      final SkillRecommenderDTO skillRecommenderDTO,
      final RequestContext requestContext) {
    final List<String> bundledJobTitles = Lists.newArrayList();

    if (StringUtils.isNotBlank(skillRecommenderDTO.getJobTitle())) {
      searchSimilarQualifications(skillRecommenderDTO.getJobTitle(), QualificationType.job_title, requestContext)
          .subscribe(
              new Action1<Qualification>() {
                @Override
                public void call(com.workmarket.search.qualification.Qualification qualification) {
                  bundledJobTitles.add(qualification.getName());
                }
              },
              new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                  logger.error("Failed to fetch job titles from qualification service: " + throwable);
                }
              });
    }

    return recommendSkills(skillRecommenderDTO, bundledJobTitles, requestContext);
  }

  /**
   * Recommends skills given skill recommender dto and a list of similar job titles (bundled).
   *
   * @param skillRecommenderDTO skill recommender dto
   * @param bundledJobTitles    a list of similar (bundled) job titles
   * @param requestContext      request context
   * @return Observable
   */
  public Observable<RecommendSkillResponse> recommendSkills(
      final SkillRecommenderDTO skillRecommenderDTO,
      final List<String> bundledJobTitles,
      final RequestContext requestContext) {
    final String jobTitles = bundledJobTitles.size() > 0 ? StringUtils.join(bundledJobTitles, ", ") :
        skillRecommenderDTO.getJobTitle();

    final Worker worker = Worker.builder()
        .setJobTitle(jobTitles)
        .setIndustryIds(getImmutableListOf(skillRecommenderDTO.getIndustries()))
        .setRemovedSkills(getImmutableListOf(skillRecommenderDTO.getRemovedSkills()))
        .setDefinedSkill(getImmutableListOf(skillRecommenderDTO.getDefinedSkills()))
        .setSelectedSkills(getImmutableListOf(skillRecommenderDTO.getSelectedSkills()))
		.setOffset(skillRecommenderDTO.getOffset() == null ? null : skillRecommenderDTO.getOffset().toString())
		.setLimit(skillRecommenderDTO.getLimit() == null ? null : skillRecommenderDTO.getLimit().toString())
        .build();

    return skillRecommenderClient.recommendSkills(
        worker,
        requestContext);

  }

  /**
   * Suggest a list of qualifications.
   *
   * @param searchTerm        prefix to suggest from, null or empty will result in no results
   * @param qualificationType qualification type
   * @param requestContext    request context
   * @return Observable
   */
  public Observable<Qualification> getSuggestedQualifications(
      final String searchTerm,
      final QualificationType qualificationType,
      final RequestContext requestContext) {
    List<String> names = new ArrayList<>(1);
    names.add(searchTerm);

    final SearchRequest qualificationRequest = SearchRequest.builder()
        .setQualificationType(qualificationType)
        .setNames(names)
        .setIsApproved(Boolean.TRUE)
        .build();
    return qualificationClient.suggestQualifications(qualificationRequest, requestContext);
  }

  /**
   * Suggest a list of skills with monolith db results mixed in. This is needed as an interim method
   * as not all skills data has been migrated over to the micro-service.
   *
   * This method will filter out duplicates by skill name.
   *
   * @param searchTerm     search query
   * @param requestContext
   * @return Concatenated observable of microservice skills data + monolith skill data.
   */
  public Observable<Qualification> getSuggestedSkillsMerged(
      final String searchTerm,
      final RequestContext requestContext) {
    return getSuggestedQualifications(searchTerm, QualificationType.skill, requestContext)
        .concatWith(Observable.from(suggestionsService.suggestSkills(searchTerm))
            .map(new Func1<SuggestionDTO, Qualification>() {
              @Override
              public Qualification call(final SuggestionDTO dto) {
                return Qualification.builder()
                    .setUuid(String.valueOf(dto.getId()))
                    .setName(dto.getValue())
                    .build();
              }
            }))
        .distinct(new Func1<Qualification, String>() {
          @Override
          public String call(final Qualification qualification) {
            return qualification.getName().toLowerCase();
          }
        });
  }

  /**
   * Searches a list of similar (bundled) qualifications given a qualification uuid.
   *
   * @param qualificationUuid qualification uuid
   * @param requestContext    request context
   * @return Observable
   */
  public Observable<Qualification> searchSimilarQualifications(
      final String qualificationUuid,
      final RequestContext requestContext) {
    final SearchRequest searchRequest = SearchRequest.builder()
        .setUuids(Lists.newArrayList(qualificationUuid))
        .build();
    return qualificationClient.searchSimilarQualifications(searchRequest, requestContext);
  }

  /**
   * Searches a list of similar (bundled) qualifications given a qualification name and qualification type.
   *
   * @param qualificationName qualification name
   * @param qualificationType qualification type
   * @param requestContext    request context
   * @return Observable
   */
  public Observable<Qualification> searchSimilarQualifications(
      final String qualificationName,
      final QualificationType qualificationType,
      final RequestContext requestContext) {
    final SearchRequest searchRequest = SearchRequest.builder()
        .setNames(Lists.newArrayList(qualificationName))
        .setQualificationType(qualificationType)
        .build();
    return qualificationClient.searchQualifications(searchRequest, requestContext)
        .flatMap(new Func1<Qualification, Observable<? extends Qualification>>() {
          @Override
          public Observable<? extends Qualification> call(Qualification qualification) {
            return QualificationRecommender.this.searchSimilarQualifications(qualification.getUuid(), requestContext);
          }
        });
  }

  /**
   * Return the recommended set of skills.
   *
   * @param dto            request
   * @param requestContext context
   * @return
   */
  public Observable<com.workmarket.domains.onboarding.model.Qualification> recommendSkillsAndHydrate(
      final SkillRecommenderDTO dto,
      final RequestContext requestContext) {
    return recommendSkills(dto, requestContext)
        .flatMap(
            new Func1<RecommendSkillResponse, Observable<com.workmarket.domains.onboarding.model.Qualification>>() {
              @Override
              public Observable<com.workmarket.domains.onboarding.model.Qualification> call(
                  final RecommendSkillResponse skillResponse) {
                return hydrate(skillResponse);
              }
            });
  }

  private Observable<com.workmarket.domains.onboarding.model.Qualification> hydrate(
      final RecommendSkillResponse skillResponse) {
    final List<com.workmarket.domains.onboarding.model.Qualification> qualifications = Lists.newArrayList();
    final HashMap<Long, Double> skillsMap = new HashMap<>();
    final HashMap<Long, Double> specialtiesMap = new HashMap<>();

    for (com.workmarket.business.recommendation.skill.model.Qualification qualification : skillResponse.getSkills()) {
      skillsMap.put(Long.parseLong(qualification.getId()), qualification.getScore());
    }

    for (com.workmarket.business.recommendation.skill.model.Qualification qualification : skillResponse.getSpecialties()) {
      specialtiesMap.put(Long.parseLong(qualification.getId()), qualification.getScore());
    }

    if (skillsMap.keySet().size() > 0) {
      List<Skill> skills = skillService
          .findSkillsByIds(skillsMap.keySet().toArray(new Long[skillsMap.keySet().size()]));
      for (Skill skill : skills) {
        qualifications.add(
            new com.workmarket.domains.onboarding.model.Qualification(
                skill.getId(),
                skill.getName(),
                skillsMap.get(skill.getId()),
                com.workmarket.domains.onboarding.model.Qualification.Type.SKILL));
      }
    }

    if (specialtiesMap.keySet().size() > 0) {
      List<Specialty> specialties =
          specialtyService.findSpecialtiesByIds(specialtiesMap.keySet().toArray(new Long[0]));
      for (Specialty specialty : specialties) {
        qualifications.add(
            new com.workmarket.domains.onboarding.model.Qualification(
                specialty.getId(),
                specialty.getName(),
                specialtiesMap.get(specialty.getId()),
                com.workmarket.domains.onboarding.model.Qualification.Type.SPECIALTY));
      }
    }

    Collections.sort(
        qualifications,
        new Comparator<com.workmarket.domains.onboarding.model.Qualification>() {
          @Override
          public int compare(
              com.workmarket.domains.onboarding.model.Qualification q1,
              com.workmarket.domains.onboarding.model.Qualification q2) {
            return q2.getScore().compareTo(q1.getScore());
          }
        }
    );

    return Observable.from(qualifications);
  }

  private ImmutableList<String> getImmutableListOf(final List<String> items) {
    return CollectionUtils.isEmpty(items) ? new ImmutableList.Builder<String>().build() : ImmutableList.copyOf(items);
  }
}
