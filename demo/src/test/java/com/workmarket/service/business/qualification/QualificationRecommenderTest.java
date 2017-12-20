package com.workmarket.service.business.qualification;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.qualification.SkillRecommenderDTO;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.SkillPagination;
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

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for QualificationRecommender.
 */
@RunWith(MockitoJUnitRunner.class)
public class QualificationRecommenderTest {

    private static final String NAME1 = "name1";
    private static final String NAME2 = "name2";
    private static final String NAME3 = "name3";

    private static final String UUID1 = "uuid1";
    private static final String UUID2 = "uuid2";

    private static final Qualification QUALIFICATION = Qualification.builder()
        .setName(NAME1)
        .setUuid(UUID1)
        .setQualificationType(QualificationType.job_title)
        .build();
    private static final Qualification SIMILAR_QUALIFICATION = Qualification.builder()
        .setName(NAME2)
        .setUuid(UUID2)
        .setQualificationType(QualificationType.job_title)
        .build();

    private static final ArgumentCaptor<SearchRequest> SEARCH_REQUEST_ARG = ArgumentCaptor.forClass(SearchRequest.class);
    private static final ArgumentCaptor<Worker> WORKER_ARG = ArgumentCaptor.forClass(Worker.class);

    @Mock private QualificationClient qualificationClientMock;
    @Mock private SkillRecommenderClient skillRecommenderClientMock;
    @Mock private SkillService skillService;
    @Mock private SpecialtyService specialtyService;
    @Mock private SuggestionService suggestionService;

    private RequestContext requestContext;
    private QualificationRecommender qualificationRecommender;


    @Before
    public void setUp() {
        requestContext = new RequestContext(UUID.randomUUID().toString(), "DUMMY_TENANT_ID");
        requestContext.setUserId("workmarket");
        qualificationRecommender =
            spy(new QualificationRecommender(
                qualificationClientMock,
                skillRecommenderClientMock,
                skillService,
                specialtyService,
                suggestionService));
    }

    /**
     * Tests search similar qualifications by uuid returns no qualifications.
     * @throws Exception
     */
    @Test
    public void testSearchSimilarQualificationsByUuidReturnNoQualifications() throws Exception {
        when(qualificationClientMock.searchSimilarQualifications(SEARCH_REQUEST_ARG.capture(), any(RequestContext.class)))
            .thenReturn(Observable.from(Lists.<Qualification>newArrayList()));
        final List<Qualification> qualificationsWithSimilarity = qualificationRecommender
            .searchSimilarQualifications(UUID1, requestContext)
            .toList().toBlocking().single();
        assertEquals(1, SEARCH_REQUEST_ARG.getValue().getUuids().size());
        assertEquals(UUID1, SEARCH_REQUEST_ARG.getValue().getUuids().get(0));
        assertEquals(0, qualificationsWithSimilarity.size());
    }

    /**
     * Tests search similar qualification by uuid returns multiple similar qualifications.
     * @throws Exception
     */
    @Test
    public void testSearchSimilarQualificationsByUuidReturnMultipleQualifications() throws Exception {
        when(qualificationClientMock.searchSimilarQualifications(SEARCH_REQUEST_ARG.capture(), any(RequestContext.class)))
            .thenReturn(Observable.just(QUALIFICATION, SIMILAR_QUALIFICATION));
        final List<Qualification> qualificationsWithSimilarity = qualificationRecommender
            .searchSimilarQualifications(UUID1, requestContext)
            .toList().toBlocking().single();
        assertEquals(1, SEARCH_REQUEST_ARG.getValue().getUuids().size());
        assertEquals(UUID1, SEARCH_REQUEST_ARG.getValue().getUuids().get(0));
        assertEquals(2, qualificationsWithSimilarity.size());
        for (Qualification qualification : qualificationsWithSimilarity) {
            assertTrue(qualification.getUuid().equals(UUID1) || qualification.getUuid().equals(UUID2));
        }
    }

    /**
     * Tests search similar qualifications by name returns no qualifications.
     * @throws Exception
     */
    @Test
    public void testSearchSimilarQualificationsByNameReturnNoQualifications() throws Exception {
        when(qualificationClientMock.searchQualifications(SEARCH_REQUEST_ARG.capture(), any(RequestContext.class)))
            .thenReturn(Observable.from(Lists.<Qualification>newArrayList()));
        final List<Qualification> qualificationsWithSimilarity = qualificationRecommender
            .searchSimilarQualifications(NAME3, QualificationType.job_title, requestContext)
            .toList().toBlocking().single();
        assertEquals(1, SEARCH_REQUEST_ARG.getValue().getNames().size());
        assertEquals(NAME3, SEARCH_REQUEST_ARG.getValue().getNames().get(0));
        assertEquals(QualificationType.job_title, SEARCH_REQUEST_ARG.getValue().getQualificationType());
        assertEquals(0, qualificationsWithSimilarity.size());
    }

    /**
     * Tests search similar qualification by name returns multiple similar qualifications.
     * @throws Exception
     */
    @Test
    public void testSearchSimilarQualificationsByNameReturnMultipleQualifications() throws Exception {
        when(qualificationClientMock.searchQualifications(any(SearchRequest.class), any(RequestContext.class)))
            .thenReturn(Observable.just(QUALIFICATION));
        when(qualificationClientMock.searchSimilarQualifications(SEARCH_REQUEST_ARG.capture(),
            any(RequestContext.class)))
            .thenReturn(Observable.just(QUALIFICATION, SIMILAR_QUALIFICATION));
        final List<Qualification> qualificationsWithSimilarity = qualificationRecommender
            .searchSimilarQualifications(NAME1, QualificationType.job_title, requestContext)
            .toList().toBlocking().single();
        assertEquals(1, SEARCH_REQUEST_ARG.getValue().getUuids().size());
        assertEquals(UUID1, SEARCH_REQUEST_ARG.getValue().getUuids().get(0));
        assertNull(SEARCH_REQUEST_ARG.getValue().getQualificationType());
        assertEquals(2, qualificationsWithSimilarity.size());
        for (Qualification qualification : qualificationsWithSimilarity) {
            assertTrue(qualification.getUuid().equals(UUID1) || qualification.getUuid().equals(UUID2));
        }
    }

    /**
     * Tests recommend skills method with a list of bundled (similar) qualifications (job title).
     * Expects that the request contains the concatenation of a list of qualifications.
     * @throws Exception
     */
    @Test
    public void testRecommendSkillsWithAListOfBundledJobTitles() throws Exception {
        final SkillRecommenderDTO dto = createSkillRecommenderDTO(NAME1);
        when(skillRecommenderClientMock.recommendSkills(WORKER_ARG.capture(), any
            (RequestContext.class)))
            .thenReturn(Observable.<RecommendSkillResponse>empty());
        final List<String> bundledJobTitles = Lists.newArrayList(NAME1, NAME2);
        qualificationRecommender.recommendSkills(dto, bundledJobTitles, requestContext);
        assertTrue(WORKER_ARG.getValue().getJobTitle().contains(NAME1));
        assertTrue(WORKER_ARG.getValue().getJobTitle().contains(NAME2));
    }

    /**
     * Tests recommend skills method with a list of empty qualifications (job title).
     * Expects that the request contains the job title from SkillRecommenderDTO.
     * @throws Exception
     */
    @Test
    public void testRecommendSkillsWithEmptyListOfBundledJobTitles() throws Exception {
        final SkillRecommenderDTO dto = createSkillRecommenderDTO(NAME1);
        when(skillRecommenderClientMock.recommendSkills(WORKER_ARG.capture(), any(RequestContext.class)))
            .thenReturn(Observable.<RecommendSkillResponse>empty());
        final List<String> bundledJobTitles = Lists.newArrayList();
        qualificationRecommender.recommendSkills(dto, bundledJobTitles, requestContext);
        assertTrue(WORKER_ARG.getValue().getJobTitle().equals(NAME1));
    }

    /**
     * Tests recommend skills method with empty job title in SkillRecommenderDTO.
     * Expects empty job title in recommendation request.
     * @throws Exception
     */
    @Test
    public void testRecommendSkillsWithEmptyJobTitle() throws Exception {
        final SkillRecommenderDTO dto = createSkillRecommenderDTO("");
        when(skillRecommenderClientMock.recommendSkills(WORKER_ARG.capture(), any(RequestContext.class)))
            .thenReturn(Observable.<RecommendSkillResponse>empty());
        qualificationRecommender.recommendSkills(dto, requestContext);
        assertTrue(StringUtils.isBlank(WORKER_ARG.getValue().getJobTitle()));
    }

    /**
     * Tests recommend skills method without similar (bundled) qualifications (job titles).
     * @throws Exception
     */
    @Test
    public void testRecommendSkillsWithoutSimilarJobTitleMatch() throws Exception {
        final SkillRecommenderDTO dto = createSkillRecommenderDTO(NAME1);
        when(qualificationClientMock.searchQualifications(any(SearchRequest.class), any(RequestContext.class)))
            .thenReturn(Observable.<Qualification>empty());
        when(skillRecommenderClientMock.recommendSkills(WORKER_ARG.capture(), any(RequestContext.class)))
            .thenReturn(Observable.<RecommendSkillResponse>empty());
        qualificationRecommender.recommendSkills(dto, requestContext);
        assertTrue(WORKER_ARG.getValue().getJobTitle().equals(NAME1));
    }

    /**
     * Tests recommend skills method with similar (bundled) qualifications (job titles).
     * @throws Exception
     */
    @Test
    public void testRecommendSkillsWithSimilarJobTitlesMatch() throws Exception {
        final SkillRecommenderDTO dto = createSkillRecommenderDTO(NAME1);
        when(qualificationClientMock.searchQualifications(any(SearchRequest.class), any(RequestContext.class)))
            .thenReturn(Observable.just(QUALIFICATION));
        when(qualificationClientMock.searchSimilarQualifications(any(SearchRequest.class), any(RequestContext.class)))
            .thenReturn(Observable.just(QUALIFICATION, SIMILAR_QUALIFICATION));
        when(skillRecommenderClientMock.recommendSkills(WORKER_ARG.capture(),
            any(RequestContext.class)))
            .thenReturn(Observable.<RecommendSkillResponse>empty());
        qualificationRecommender.recommendSkills(dto, requestContext);
        assertTrue(WORKER_ARG.getValue().getJobTitle().contains(NAME1));
        assertTrue(WORKER_ARG.getValue().getJobTitle().contains(NAME2));
    }

    @Test
    public void shouldGetSkillsDataFromDatabase() {
        final String term = "some-term";
        final String skillName = "skill-name";
        final SuggestionDTO suggestionDto = new SuggestionDTO();
        suggestionDto.setValue(skillName);
        suggestionDto.setId(1L);
        when(suggestionService.suggestSkills(term)).thenReturn(ImmutableList.of(suggestionDto));
        when(qualificationClientMock.suggestQualifications(any(SearchRequest.class), any(RequestContext.class)))
            .thenReturn(Observable.<Qualification>empty());

        final List<Qualification> result =
            qualificationRecommender
                .getSuggestedSkillsMerged(term, requestContext).toList().toBlocking().single();

        assertEquals(1, result.size());
        assertEquals(skillName, result.get(0).getName());
    }

    @Test
    public void shouldGetSkillsDataFromMicroservice() {
        when(skillService.findAllSkills(any(SkillPagination.class), eq(true))).thenReturn(new SkillPagination());
        when(qualificationClientMock.suggestQualifications(any(SearchRequest.class), any(RequestContext.class)))
            .thenReturn(Observable.just(Qualification.builder().setName("skill-name-2").build()));

        final List<Qualification> result =
            qualificationRecommender
                .getSuggestedSkillsMerged("some-term", requestContext).toList().toBlocking().single();

        assertEquals(1, result.size());
        assertEquals("skill-name-2", result.get(0).getName());
    }

    @Test
    public void shouldConcatenateSkillsFromMicroserviceAndDatabase() {
        final String term = "some-term";
        final String skillNameFirst = "skill-name-first";
        final String skillNameSecond = "skill-name-second";
        final SuggestionDTO suggestionDto = new SuggestionDTO();
        suggestionDto.setValue(skillNameSecond);
        suggestionDto.setId(1L);
        when(suggestionService.suggestSkills(term)).thenReturn(ImmutableList.of(suggestionDto));
        when(qualificationClientMock.suggestQualifications(any(SearchRequest.class), any(RequestContext.class)))
            .thenReturn(Observable.just(Qualification.builder().setName(skillNameFirst).build()));

        final List<Qualification> result =
            qualificationRecommender
                .getSuggestedSkillsMerged(term, requestContext).toList().toBlocking().single();

        assertEquals(2, result.size());
        assertEquals(skillNameFirst, result.get(0).getName());
        assertEquals(skillNameSecond, result.get(1).getName());
    }

    @Test
    public void shouldDedupDuplicateSkillsByName() {
        final String term = "some-term";
        final String skillName= "skill-name";
        final String skillNameUpper= "SKILL-NAME";
        final SuggestionDTO suggestionDto = new SuggestionDTO();
        suggestionDto.setValue(skillName);
        suggestionDto.setId(1L);
        when(suggestionService.suggestSkills(term)).thenReturn(ImmutableList.of(suggestionDto));
        when(qualificationClientMock.suggestQualifications(any(SearchRequest.class), any(RequestContext.class)))
            .thenReturn(Observable.just(Qualification.builder().setName(skillNameUpper).build()));

        final List<Qualification> result =
            qualificationRecommender
                .getSuggestedSkillsMerged(term, requestContext).toList().toBlocking().single();

        assertEquals(1, result.size());
        assertEquals(skillName.toLowerCase(), result.get(0).getName().toLowerCase());
    }

    @Test
    public void shouldHydrateRecommendedList() {
        final String skillId = "2";
        final Double skillScore = 1.1D;
        final SkillRecommenderDTO dto = createSkillRecommenderDTO("Developer");
        final RecommendSkillResponse response = new RecommendSkillResponse(
            ImmutableList.of(new com.workmarket.business.recommendation.skill.model.Qualification(skillId, skillScore)),
            ImmutableList.<com.workmarket.business.recommendation.skill.model.Qualification>of(),
            ImmutableList.<com.workmarket.business.recommendation.skill.model.Qualification>of());

        when(qualificationClientMock.searchQualifications(SEARCH_REQUEST_ARG.capture(), any(RequestContext.class)))
            .thenReturn(Observable.just(Qualification.builder().setName("skill-1").setUuid("uuid").build()));
        when(qualificationClientMock.searchSimilarQualifications(any(SearchRequest.class), any(RequestContext.class)))
            .thenReturn(Observable.just(Qualification.builder().setName("job-title-1").build()));
        when(skillRecommenderClientMock.recommendSkills(any(Worker.class), any(RequestContext.class)))
            .thenReturn(Observable.just(response));
        final Skill skillFromDb = new Skill();
        skillFromDb.setName("db-name");
        skillFromDb.setId(2L);
        when(skillService.findSkillsByIds(any(Long[].class))).thenReturn(ImmutableList.of(skillFromDb));

        final List<com.workmarket.domains.onboarding.model.Qualification> result =
            qualificationRecommender.recommendSkillsAndHydrate(dto, requestContext).toList().toBlocking().single();

        // Test that we first search qualifications by similar job titles
        assertEquals(SEARCH_REQUEST_ARG.getValue().getQualificationType(), QualificationType.job_title);
        assertEquals(SEARCH_REQUEST_ARG.getValue().getNames().get(0), "Developer");

        // verify db hydration
        verify(skillService).findSkillsByIds(new Long[]{ (Long)Long.parseLong(skillId)});

        assertEquals(1, result.size());
        com.workmarket.domains.onboarding.model.Qualification q = result.get(0);
        assertEquals("db-name", q.getName());
        assertEquals((Long) Long.parseLong(skillId), q.getId());
        assertEquals(skillScore, q.getScore());
    }


    private SkillRecommenderDTO createSkillRecommenderDTO(final String jobTitle) {
        final SkillRecommenderDTO dto =  new SkillRecommenderDTO();
        dto.setJobTitle(jobTitle);
        dto.setIndustries(Lists.newArrayList("1000"));
        dto.setDefinedSkills(Lists.<String>newArrayList());
        dto.setSelectedSkills(Lists.<String>newArrayList());
        dto.setRemovedSkills(Lists.<String>newArrayList());
        return dto;
    }
}
