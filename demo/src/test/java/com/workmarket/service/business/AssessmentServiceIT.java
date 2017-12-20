package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.dao.assessment.AssessmentUserAssociationDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.service.UserGroupRequirementSetService;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AbstractItem;
import com.workmarket.domains.model.assessment.AbstractItemWithChoices;
import com.workmarket.domains.model.assessment.AssessmentPagination;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.assessment.AssessmentUserAssociationPagination;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.assessment.AttemptResponse;
import com.workmarket.domains.model.assessment.Choice;
import com.workmarket.domains.model.assessment.GradedAssessment;
import com.workmarket.domains.model.assessment.ManagedAssessment;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination;
import com.workmarket.domains.model.assessment.WorkScopedAttempt;
import com.workmarket.domains.model.assessment.item.MultipleChoiceItem;
import com.workmarket.domains.model.assessment.item.SingleChoiceRadioItem;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.request.RequestPagination;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.dto.AggregatesDTO;
import com.workmarket.dto.AssessmentUserPagination;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.dto.AssessmentChoiceDTO;
import com.workmarket.service.business.dto.AssessmentDTO;
import com.workmarket.service.business.dto.AssessmentItemDTO;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.AttemptResponseDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.assessment.AssessmentAttemptTimedOutException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.velvetrope.Venue;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class AssessmentServiceIT extends BaseServiceIT {

	@Autowired RedisAdapter redis;
	@Autowired UserGroupRequirementSetService userGroupRequirementSetService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private LaneService laneService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private IndustryService industryService;
	@Autowired private SessionFactory factory;
	@Autowired private ResourceLoader resourceLoader;
	@Autowired private RequestService requestService;
	@Autowired private AssessmentUserAssociationDAO assessmentUserAssociationDAO;
	@Autowired private AdmissionService admissionService;
	private AbstractAssessment assessment;
	private AbstractAssessment activeAssessment;

	private User testTaker;
	private User testCreator;

	@Before
	public void initAssessment() throws Exception {
		testTaker = newRegisteredWorker();
		testCreator = newContractor();

		assessment = createAssessment("Test Quiz", testCreator, AssessmentStatusType.DRAFT,
			AbstractAssessment.GRADED_ASSESSMENT_TYPE, new AssessmentItemDTO[]{
				createAssessmentItemDTO("What's 0 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 1 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO(
					"How many drinks of alcohol does it take to affect your driving?",
					AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("Where?", AbstractItem.MULTIPLE_CHOICE),
				createAssessmentItemDTO("What's 3 + 2?", AbstractItem.SINGLE_CHOICE_RADIO)
			});

		activeAssessment = createAssessment("Test Quiz", testCreator, AssessmentStatusType.ACTIVE,
			AbstractAssessment.GRADED_ASSESSMENT_TYPE, new AssessmentItemDTO[]{
				createAssessmentItemDTO("What's 0 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 1 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 2 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 3 + 2?", AbstractItem.SINGLE_CHOICE_RADIO)
			});
	}

	// Retrieving and reading assessments
	@Test
	public void findAssessment_returnsResult() throws Exception {
		User user = newContractor();
		AbstractAssessment assessment = newAssessmentForUser(user, null, false);

		assertNotNull(assessment);
		assertEquals(assessment.getId(), assessment.getId());
	}

	@Test
	public void findAssessmentsForUser_withIdFilter_returnResult() throws Exception {
		User user = newContractor();
		admissionService.saveAdmissionForCompanyIdAndVenue(user.getCompany().getId(), Venue.MARKETPLACE);
		AbstractAssessment assessment = newAssessmentForUser(user, null, false);
		ManagedAssessmentPagination pagination =
			new ManagedAssessmentPagination().setIdFilter(Lists.newArrayList(assessment.getId()));
		pagination = assessmentService.findAssessmentsForUser(user.getId(), pagination);
		assertNotNull(pagination.getResults());
		assertEquals(pagination.getResults().size(), 1);
		assertEquals(pagination.getResults().get(0).getAssessmentId(), assessment.getId());
	}

	@Test
	public void findRecommendedAssessmentsForUser_cacheMiss_thenCacheHit_returnResultBothTimes() throws Exception {
		User user = newContractor();
		redis.delete(RedisFilters.recommendedAssessmentKeyFor(user.getId()));
		AbstractAssessment assessment = newAssessmentForUser(user, Industry.TECHNOLOGY_AND_COMMUNICATIONS, true);

		// Cache miss (default filters are used)
		ManagedAssessmentPagination pagination = assessmentService.findRecommendedAssessmentsForUser(user.getId());
		Set<Long> cacheMissIds = new HashSet<>(pagination.getResultIds());

		// Cache hit (default filters are not used, we filter only with the cached assessment ids)
		pagination = assessmentService.findRecommendedAssessmentsForUser(user.getId());
		Set<Long> cacheHitIds = new HashSet<>(pagination.getResultIds());

		// Assert that both results sets are equal and non empty
		assertEquals(cacheHitIds, cacheMissIds);
		assertTrue(!cacheHitIds.isEmpty() && !cacheMissIds.isEmpty());

		// Assert that recommended assessments are the same industry as user, and that assessment is public
		Profile profile = profileService.findProfile(user.getId());
		List<Long> userIndustryIds = CollectionUtilities.newListPropertyProjection(
			industryService.getIndustryDTOsForProfile(profile.getId()),
			"id"
		);
		for (ManagedAssessment result : pagination.getResults()) {
			assertThat(result.getIndustryId(), equalTo(Industry.TECHNOLOGY_AND_COMMUNICATIONS.getId()));
			assertTrue(userIndustryIds.contains(result.getIndustryId()));
			assertFalse(assessment.isInvitationOnly());
		}
	}

	@Test
	public void findAssessmentUserAssociationsByUser_statusTypeActive_returnsOneAssessment() throws Exception {
		User user = newContractor();
		AbstractAssessment assessment = newAssessmentForUser(user, null, false);
		Attempt attempt = assessmentService.saveAttemptForAssessment(user.getId(), assessment.getId());
		assessmentService.completeAttemptForAssessment(attempt.getId());

		AssessmentUserAssociationPagination pagination = new AssessmentUserAssociationPagination();
		pagination.setReturnAllRows();
		pagination = assessmentService.findAssessmentUserAssociationsByUser(user.getId(), pagination);

		assertNotNull(assessment);
		assertEquals(Integer.valueOf(1), pagination.getRowCount());
	}

	@Test
	public void findAssessmentUserAssociationsByUser_statusTypeRemoved_returnsNoAssessments() throws Exception {
		User user = newContractor();
		AbstractAssessment assessment = newAssessmentForUser(user, null, false);
		Attempt attempt = assessmentService.saveAttemptForAssessment(user.getId(), assessment.getId());
		assessmentService.completeAttemptForAssessment(attempt.getId());
		assessmentService.updateAssessmentStatus(assessment.getId(), AssessmentStatusType.REMOVED);

		AssessmentUserAssociationPagination pagination = new AssessmentUserAssociationPagination();
		pagination.setReturnAllRows();
		pagination = assessmentService.findAssessmentUserAssociationsByUser(user.getId(), pagination);

		assertNotNull(assessment);
		assertEquals(Integer.valueOf(0), pagination.getRowCount());
	}

	@Test
	public void getCompanyOwnedAssessmentsWithPagination() throws Exception {
		User testCreator = newEmployeeWithCashBalance();
		createAssessment("Driving Test", testCreator, AssessmentStatusType.ACTIVE, AbstractAssessment.GRADED_ASSESSMENT_TYPE);
		createAssessment("Test Quiz", testCreator, AssessmentStatusType.DRAFT, AbstractAssessment.GRADED_ASSESSMENT_TYPE);
		createAssessment("Test Quiz", testCreator, AssessmentStatusType.INACTIVE, AbstractAssessment.GRADED_ASSESSMENT_TYPE);
		createAssessment("Test Quiz", testCreator, AssessmentStatusType.ACTIVE, AbstractAssessment.GRADED_ASSESSMENT_TYPE);
		createAssessment("Test Quiz", testCreator, AssessmentStatusType.ACTIVE, AbstractAssessment.GRADED_ASSESSMENT_TYPE);

		List<AbstractAssessment> assessments = assessmentService.findAssessmentsByCompany(testCreator.getCompany().getId(),
			new AssessmentPagination(true)).getResults();
		Integer activeAssessmentCount = assessmentService.countAssessmentsByCompany(testCreator.getCompany().getId());

		assertEquals(5, assessments.size());
		assertEquals(3, activeAssessmentCount.intValue());
		assertEquals("Driving Test", assessments.get(0).getName());
		assertEquals(AssessmentStatusType.ACTIVE, assessments.get(0).getAssessmentStatusType().getCode());
		assertEquals(AssessmentStatusType.DRAFT, assessments.get(1).getAssessmentStatusType().getCode());
		assertEquals(AssessmentStatusType.INACTIVE, assessments.get(2).getAssessmentStatusType().getCode());
	}

	@Test
	public void getAssessmentQuestions() throws Exception {

		AbstractAssessment assessment = createAssessment("Test Quiz", testCreator,
			AssessmentStatusType.DRAFT,
			AbstractAssessment.GRADED_ASSESSMENT_TYPE, new AssessmentItemDTO[]{
				createAssessmentItemDTO("What's 0 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 1 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 2 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("Where?", AbstractItem.MULTIPLE_CHOICE)

			});
		List<AbstractItem> questions = assessment.getItems();

		assertNotNull(questions);
		assertEquals(4, questions.size());
		assertEquals(SingleChoiceRadioItem.class, questions.get(0).getClass());
		assertEquals(MultipleChoiceItem.class, questions.get(3).getClass());
	}

	@Test
	@Ignore
	public void getAssessmentQuestionAssets() throws Exception {
		AbstractItem question = assessment.getItems().get(0);
		Set<Asset> assets = question.getAssets();

		assertNotNull(assets);
		assertEquals(1, assets.size());
	}

	@Test
	public void getAssessmentAnswers() throws Exception {

		List<Choice> answers = ((AbstractItemWithChoices) assessment.getItems().get(0)).getChoices();

		assertNotNull(answers);
		assertEquals(4, answers.size());
	}

	// Constructing an assessment

	@Test
	public void createAssessment() throws Exception {
		User user = newContractor();

		AssessmentDTO assessmentDTO = new AssessmentDTO();
		assessmentDTO.setName("New Quiz");
		assessmentDTO.setDescription("A description.");
		assessmentDTO.setIndustryId(INDUSTRY_1_ID);
		assessmentDTO.setPassingScore(80.0);
		assessmentDTO.setAssessmentStatusTypeCode(AssessmentStatusType.DRAFT);

		AbstractAssessment assessment = assessmentService.saveOrUpdateAssessment(user.getId(), assessmentDTO);

		assertNotNull(assessment);
		assertTrue(assessment.getId() > 0);
		assertEquals("New Quiz", assessment.getName());
		assertEquals(Double.valueOf(80.0), assessment.getPassingScore());
		assertEquals(AssessmentStatusType.DRAFT, assessment.getAssessmentStatusType().getCode());

		AssessmentItemDTO questionDTO1 = new AssessmentItemDTO();
		questionDTO1.setPrompt("What's 1 + 2?");
		questionDTO1.setType(AbstractItem.SINGLE_CHOICE_RADIO);

		AssessmentItemDTO questionDTO2 = new AssessmentItemDTO();
		questionDTO2.setPrompt("What's 2 + 2?");
		questionDTO2.setType(AbstractItem.SINGLE_CHOICE_RADIO);

		AssessmentItemDTO questionDTO3 = new AssessmentItemDTO();
		questionDTO3.setPrompt("What's 3 + 2?");
		questionDTO3.setType(AbstractItem.SINGLE_CHOICE_RADIO);

		List<AbstractItem> questions = assessmentService.saveOrUpdateItemsInAssessment(
			assessment.getId(),
			new AssessmentItemDTO[]{questionDTO1, questionDTO2, questionDTO3}
		);

		assertEquals(3, questions.size());
		assertTrue(questions.get(0).getId() > 0);
		assertEquals("What's 1 + 2?", questions.get(0).getPrompt());
		assertEquals(SingleChoiceRadioItem.class, questions.get(0).getClass());

		AssessmentChoiceDTO answerDTO1 = new AssessmentChoiceDTO();
		answerDTO1.setValue("1");
		answerDTO1.setIsCorrect(Boolean.FALSE);

		AssessmentChoiceDTO answerDTO2 = new AssessmentChoiceDTO();
		answerDTO2.setValue("2");
		answerDTO2.setIsCorrect(Boolean.FALSE);

		AssessmentChoiceDTO answerDTO3 = new AssessmentChoiceDTO();
		answerDTO3.setValue("3");
		answerDTO3.setIsCorrect(Boolean.TRUE);

		AssessmentChoiceDTO answerDTO4 = new AssessmentChoiceDTO();
		answerDTO4.setValue("4");
		answerDTO4.setIsCorrect(Boolean.FALSE);

		List<Choice> answers = assessmentService.saveOrUpdateChoicesInItem(
			questions.get(0).getId(),
			new AssessmentChoiceDTO[]{answerDTO1, answerDTO2, answerDTO3, answerDTO4}
		);

		assertEquals(4, answers.size());
		assertTrue(answers.get(0).getId() > 0);
		assertEquals("1", answers.get(0).getValue());
		assertFalse(answers.get(0).getIsCorrect());
		assertTrue(answers.get(2).getIsCorrect());
	}

	@Test
	public void createAssessmentPiecemeal() throws Exception {
		User user = newContractor();

		AssessmentDTO assessmentDTO = new AssessmentDTO();
		assessmentDTO.setName("New Quiz");
		assessmentDTO.setDescription("A description.");
		assessmentDTO.setIndustryId(INDUSTRY_1_ID);
		assessmentDTO.setPassingScore(80.0);
		assessmentDTO.setAssessmentStatusTypeCode(AssessmentStatusType.DRAFT);

		AbstractAssessment assessment = assessmentService.saveOrUpdateAssessment(user.getId(), assessmentDTO);

		assertNotNull(assessment);
		assertTrue(assessment.getId() > 0);
		assertEquals("New Quiz", assessment.getName());
		assertEquals(AssessmentStatusType.DRAFT, assessment.getAssessmentStatusType().getCode());

		AssessmentItemDTO questionDTO;
		questionDTO = new AssessmentItemDTO();
		questionDTO.setPrompt("What's 1 + 2?");
		questionDTO.setType(AbstractItem.SINGLE_CHOICE_RADIO);
		assessmentService.addOrUpdateItemInAssessment(assessment.getId(), questionDTO);

		questionDTO = new AssessmentItemDTO();
		questionDTO.setPrompt("What's 2 + 2?");
		questionDTO.setType(AbstractItem.SINGLE_CHOICE_RADIO);
		assessmentService.addOrUpdateItemInAssessment(assessment.getId(), questionDTO);

		questionDTO = new AssessmentItemDTO();
		questionDTO.setPrompt("What's 3 + 2?");
		questionDTO.setType(AbstractItem.SINGLE_CHOICE_RADIO);
		assessmentService.addOrUpdateItemInAssessment(assessment.getId(), questionDTO);

		assessment = assessmentService.findAssessment(assessment.getId());
		List<AbstractItem> questions = assessment.getItems();

		assertEquals(3, questions.size());
		assertTrue(questions.get(0).getId() > 0);
		assertEquals("What's 1 + 2?", questions.get(0).getPrompt());
		assertEquals(SingleChoiceRadioItem.class, questions.get(0).getClass());

		AssessmentChoiceDTO answerDTO;
		answerDTO = new AssessmentChoiceDTO();
		answerDTO.setValue("1");
		answerDTO.setIsCorrect(Boolean.FALSE);
		assessmentService.addOrUpdateChoiceInItem(questions.get(0).getId(), answerDTO);

		answerDTO = new AssessmentChoiceDTO();
		answerDTO.setValue("2");
		answerDTO.setIsCorrect(Boolean.FALSE);
		assessmentService.addOrUpdateChoiceInItem(questions.get(0).getId(), answerDTO);

		answerDTO = new AssessmentChoiceDTO();
		answerDTO.setValue("3");
		answerDTO.setIsCorrect(Boolean.TRUE);
		assessmentService.addOrUpdateChoiceInItem(questions.get(0).getId(), answerDTO);

		answerDTO = new AssessmentChoiceDTO();
		answerDTO.setValue("4");
		answerDTO.setIsCorrect(Boolean.FALSE);
		assessmentService.addOrUpdateChoiceInItem(questions.get(0).getId(), answerDTO);

		assessment = assessmentService.findAssessment(assessment.getId());
		questions = assessment.getItems();
		List<Choice> answers = ((AbstractItemWithChoices) questions.get(0)).getChoices();

		assertEquals(4, answers.size());
		assertTrue(answers.get(0).getId() > 0);
		assertEquals("1", answers.get(0).getValue());
		assertFalse(answers.get(0).getIsCorrect());
		assertTrue(answers.get(2).getIsCorrect());
	}

	@Test
	public void updateAssessment() throws Exception {
		User user = newContractor();

		AssessmentDTO assessmentDTO = new AssessmentDTO();
		assessmentDTO.setName("New Quiz");
		assessmentDTO.setDescription("A description.");
		assessmentDTO.setIndustryId(INDUSTRY_1_ID);
		assessmentDTO.setPassingScore(80.0);
		assessmentDTO.setAssessmentStatusTypeCode(AssessmentStatusType.DRAFT);
		AbstractAssessment assessment = assessmentService.saveOrUpdateAssessment(user.getId(), assessmentDTO);

		assessmentDTO = new AssessmentDTO();
		assessmentDTO.setId(assessment.getId());
		assessmentDTO.setName("Updated New Quiz");
		assessmentDTO.setDescription("A description. Again.");
		assessmentDTO.setPassingScore(90.0);
		assessmentDTO.setIndustryId(INDUSTRY_1_ID);
		assessmentDTO.setAssessmentStatusTypeCode(AssessmentStatusType.ACTIVE);

		assessment = assessmentService.saveOrUpdateAssessment(user.getId(), assessmentDTO);

		assertNotNull(assessment);
		assertEquals("Updated New Quiz", assessment.getName());
		assertEquals(Double.valueOf(90.0), assessment.getPassingScore());
		assertEquals(AssessmentStatusType.ACTIVE, assessment.getAssessmentStatusType().getCode());
	}

	@Test
	public void updateAssessmentStatus() throws Exception {

		AbstractAssessment inactiveAssessment = createAssessment("Test Quiz", testCreator, AssessmentStatusType.INACTIVE,
			AbstractAssessment.GRADED_ASSESSMENT_TYPE, new AssessmentItemDTO[]{
				createAssessmentItemDTO("What's 0 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 1 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 2 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 3 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("Where?", AbstractItem.MULTIPLE_CHOICE)
			});

		assertEquals(AssessmentStatusType.INACTIVE, inactiveAssessment.getAssessmentStatusType().getCode());

		assessmentService.updateAssessmentStatus(inactiveAssessment.getId(), AssessmentStatusType.ACTIVE);

		inactiveAssessment = assessmentService.findAssessment(inactiveAssessment.getId());

		assertEquals(AssessmentStatusType.ACTIVE, inactiveAssessment.getAssessmentStatusType().getCode());
	}

	@Test
	@Ignore
	public void updateAssessmentStatusNULL() throws Exception {
		assessmentService.updateAssessmentStatus(1050L, AssessmentStatusType.ACTIVE);
	}

	@Test
	public void updateAssessmentQuestions() throws Exception {

		List<AbstractItem> questions = assessment.getItems();

		List<AssessmentItemDTO> questionDTOs = Lists.newArrayList();
		for (AbstractItem q : questions) {
			AssessmentItemDTO questionDTO = new AssessmentItemDTO();
			questionDTO.setItemId(q.getId());
			questionDTO.setPrompt(q.getPrompt());
			questionDTO.setType(q.getType());
			questionDTOs.add(questionDTO);
		}

		// Add and reorder
		AssessmentItemDTO newQuestionDTO = new AssessmentItemDTO();
		newQuestionDTO.setPrompt("A white painted curb means?");
		newQuestionDTO.setType(AbstractItem.SINGLE_CHOICE_RADIO);
		questionDTOs.add(newQuestionDTO);

		questions = assessmentService.saveOrUpdateItemsInAssessment(
			assessment.getId(),
			new AssessmentItemDTO[]{
				questionDTOs.get(5), questionDTOs.get(2), questionDTOs.get(0), questionDTOs.get(1), questionDTOs.get(3)
			}
		);

		assertEquals(5, questions.size());
		assertTrue(questions.get(0).getId() > 0);
		assertEquals("A white painted curb means?", questions.get(0).getPrompt());
		assertEquals("How many drinks of alcohol does it take to affect your driving?", questions.get(1).getPrompt());
		assertEquals(SingleChoiceRadioItem.class, questions.get(0).getClass());
		assertEquals(MultipleChoiceItem.class, questions.get(4).getClass());

		// Remove and reorder

		questions = assessmentService.saveOrUpdateItemsInAssessment(
			assessment.getId(),
			new AssessmentItemDTO[]{questionDTOs.get(2), questionDTOs.get(1), questionDTOs.get(3), questionDTOs.get(0)}
		);

		assertEquals(4, questions.size());
		assertTrue(questions.get(0).getId() > 0);
		assertEquals("How many drinks of alcohol does it take to affect your driving?", questions.get(0).getPrompt());
		assertEquals(SingleChoiceRadioItem.class, questions.get(1).getClass());
		assertEquals(MultipleChoiceItem.class, questions.get(2).getClass());
	}

	@Test
	public void updateAssessmentQuestionsPiecemeal() throws Exception {

		// Add and reorder

		AssessmentItemDTO newQuestionDTO = new AssessmentItemDTO();
		newQuestionDTO.setPrompt("A white painted curb means?");
		newQuestionDTO.setType(AbstractItem.SINGLE_CHOICE_RADIO);
		assessmentService.addOrUpdateItemInAssessment(assessment.getId(), newQuestionDTO);

		assessment = assessmentService.findAssessment(assessment.getId());
		List<AbstractItem> questions = assessment.getItems();

		assessmentService.reorderItemsInAssessment(assessment.getId(), new Long[]{
			questions.get(5).getId(),
			questions.get(2).getId(),
			questions.get(0).getId(),
			questions.get(1).getId(),
			questions.get(3).getId(),
			questions.get(4).getId()
		});

		assessment = assessmentService.findAssessment(assessment.getId());
		questions = assessment.getItems();

		assertEquals(6, questions.size());
		assertTrue(questions.get(0).getId() > 0);
		assertEquals("A white painted curb means?", questions.get(0).getPrompt());
		assertEquals("How many drinks of alcohol does it take to affect your driving?", questions.get(1).getPrompt());
		assertEquals(SingleChoiceRadioItem.class, questions.get(0).getClass());
		assertEquals(SingleChoiceRadioItem.class, questions.get(1).getClass());
		assertEquals(MultipleChoiceItem.class, questions.get(4).getClass());

		// Remove and reorder

		assessmentService.removeItemFromAssessment(assessment.getId(), questions.get(0).getId());

		assessment = assessmentService.findAssessment(assessment.getId());
		questions = assessment.getItems();

		assessmentService.reorderItemsInAssessment(assessment.getId(), new Long[]{
			questions.get(4).getId(),
			questions.get(2).getId(),
			questions.get(1).getId(),
			questions.get(3).getId()
		});

		assessment = assessmentService.findAssessment(assessment.getId());
		questions = assessment.getItems();

		assertEquals(6, questions.size());
		assertTrue(questions.get(0).getId() > 0);
		assertEquals("Where?", questions.get(0).getPrompt());
		assertEquals(SingleChoiceRadioItem.class, questions.get(2).getClass());
	}

	@Test
	@Ignore
	public void saveAssetToAssessmentQuestions() throws Exception {

		List<AbstractItem> questions = assessment.getItems();

		AssetDTO dto = new AssetDTO();
		dto.setSourceFilePath(IMAGE_TEST_FILE);
		dto.setName("Asset Name");
		dto.setDescription("Asset Description");
		dto.setMimeType(MimeType.IMAGE_PNG.getMimeType());

		Asset asset = assetManagementService.storeAssetForAssessmentItem(dto, questions.get(0).getId());

		assertNotNull(asset);
		assertTrue(asset.getId() > 0);
		assertEquals(MimeType.IMAGE_PNG.getMimeType(), asset.getMimeType());
	}

	@Test
	@Ignore
	public void removeAssetFromAssessmentQuestions() throws Exception {

		List<AbstractItem> questions = assessment.getItems();

		int count = questions.get(0).getAssets().size();

		AssetDTO dto = new AssetDTO();
		dto.setSourceFilePath(IMAGE_TEST_FILE);
		dto.setName("Asset Name");
		dto.setDescription("Asset Description");
		dto.setMimeType(MimeType.IMAGE_PNG.getMimeType());

		Asset asset = assetManagementService.storeAssetForAssessmentItem(dto, questions.get(0).getId());

		assertEquals(count + 1, assessmentService.findAssessment(1L).getItems().get(0).getAssets().size());

		assetManagementService.removeAssetFromAssessmentItem(asset.getId(), questions.get(0).getId());

		assertEquals(count, assessmentService.findAssessment(1L).getItems().get(0).getAssets().size());
	}

	@Test
	public void updateAssessmentQuestionAnswers() throws Exception {

		assessment = createAssessment("Test Quiz", testCreator, AssessmentStatusType.DRAFT,
			AbstractAssessment.GRADED_ASSESSMENT_TYPE, new AssessmentItemDTO[]{
				createAssessmentItemDTO("What's 0 + 2?", AbstractItem.SINGLE_CHOICE_RADIO)
			});
		AbstractItem question = assessment.getItems().get(0);
		List<Choice> answers = ((AbstractItemWithChoices) question).getChoices();

		List<AssessmentChoiceDTO> answerDTOs = Lists.newArrayList();
		for (Choice a : answers) {
			AssessmentChoiceDTO answerDTO = new AssessmentChoiceDTO();
			answerDTO.setChoiceId(a.getId());
			answerDTO.setValue(a.getValue());
			answerDTO.setIsCorrect(a.getIsCorrect());
			answerDTOs.add(answerDTO);
		}

		// Add and reorder

		AssessmentChoiceDTO newAnswerDTO = new AssessmentChoiceDTO();
		newAnswerDTO.setValue("Bear crossing!");
		newAnswerDTO.setIsCorrect(false);
		answerDTOs.add(newAnswerDTO);

		answers = assessmentService.saveOrUpdateChoicesInItem(
			question.getId(),
			new AssessmentChoiceDTO[]{answerDTOs.get(4), answerDTOs.get(2), answerDTOs.get(0), answerDTOs.get(1)}
		);

		assertEquals(4, answers.size());
		assertTrue(answers.get(0).getId() > 0);
		assertEquals("Bear crossing!", answers.get(0).getValue());
		assertTrue(answers.get(2).getIsCorrect());

		// Remove and reorder

		answers = assessmentService.saveOrUpdateChoicesInItem(
			question.getId(),
			new AssessmentChoiceDTO[]{answerDTOs.get(0), answerDTOs.get(2), answerDTOs.get(1)}
		);

		assertEquals(3, answers.size());
		assertTrue(answers.get(0).getId() > 0);
		assertEquals("1", answers.get(0).getValue());
		assertTrue(answers.get(0).getIsCorrect());
	}

	@Test
	public void updateAssessmentQuestionAnswersPiecemeal() throws Exception {

		AbstractAssessment assessment = createAssessment("Test Quiz", testCreator, AssessmentStatusType.DRAFT,
			AbstractAssessment.GRADED_ASSESSMENT_TYPE, new AssessmentItemDTO[]{
				createAssessmentItemDTO("What's 0 + 2?", AbstractItem.SINGLE_CHOICE_RADIO)
			});

		AbstractItem question = assessment.getItems().get(0);

		// Add and reorder

		AssessmentChoiceDTO newAnswerDTO = new AssessmentChoiceDTO();
		newAnswerDTO.setValue("Bear crossing!");
		newAnswerDTO.setIsCorrect(false);
		assessmentService.addOrUpdateChoiceInItem(question.getId(), newAnswerDTO);

		assessment = assessmentService.findAssessment(assessment.getId());
		question = assessment.getItems().get(0);
		List<Choice> answers = ((AbstractItemWithChoices) question).getChoices();

		assessmentService.reorderChoicesInItem(question.getId(), new Long[]{
			answers.get(4).getId(),
			answers.get(2).getId(),
			answers.get(0).getId(),
			answers.get(1).getId()
		});

		assessment = assessmentService.findAssessment(assessment.getId());
		question = assessment.getItems().get(0);
		answers = ((AbstractItemWithChoices) question).getChoices();

		assertEquals(4, answers.size());
		assertTrue(answers.get(0).getId() > 0);
		assertEquals("Bear crossing!", answers.get(0).getValue());
		assertTrue(answers.get(2).getIsCorrect());
	}

	// Taking/attempting an assessment

	@Test
	public void createAndCompletePassingAssessmentAttempt() throws Exception {

		AbstractAssessment assessment = createAssessment("Test Quiz", testCreator, AssessmentStatusType.ACTIVE,
			AbstractAssessment.GRADED_ASSESSMENT_TYPE, new AssessmentItemDTO[]{
				createAssessmentItemDTO("What's 0 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 1 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("What's 2 + 2?", AbstractItem.SINGLE_CHOICE_RADIO),
				createAssessmentItemDTO("Where?", AbstractItem.MULTIPLE_CHOICE)
			});
		Attempt attempt = assessmentService.saveAttemptForAssessment(EMPLOYEE_USER_ID, assessment.getId());

		assertNotNull(attempt);
		assertTrue(attempt.getId() > 0);

		// Supply correct answer

		List<AttemptResponse> responses = assessmentService.submitResponsesForItem(
			attempt.getId(),
			assessment.getItems().get(0).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(((AbstractItemWithChoices) assessment.getItems().get(0)).getChoices().get(0).getId(), null)
			}
		);

		assertTrue(responses.get(0).getChoice().getIsCorrect());

		// Supply wrong answer

		responses = assessmentService.submitResponsesForItem(
			attempt.getId(),
			assessment.getItems().get(1).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(((AbstractItemWithChoices) assessment.getItems().get(0)).getChoices().get(1).getId(), null)
			}
		);

		assertFalse(responses.get(0).getChoice().getIsCorrect());

		// Remedy wrong answer to correct

		responses = assessmentService.submitResponsesForItem(
			attempt.getId(),
			assessment.getItems().get(1).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(((AbstractItemWithChoices) assessment.getItems().get(1)).getChoices().get(0).getId(), null)
			}
		);

		assertTrue(responses.get(0).getChoice().getIsCorrect());

		// Supply correct answer

		responses = assessmentService.submitResponsesForItem(
			attempt.getId(),
			assessment.getItems().get(2).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(((AbstractItemWithChoices) assessment.getItems().get(2)).getChoices().get(0).getId(), null)
			}
		);

		assertTrue(responses.get(0).getChoice().getIsCorrect());

		// Supply correct answers for multiple choice

		responses = assessmentService.submitResponsesForItem(
			attempt.getId(),
			assessment.getItems().get(3).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(((AbstractItemWithChoices) assessment.getItems().get(3)).getChoices().get(0).getId(), null),
				new AttemptResponseDTO(((AbstractItemWithChoices) assessment.getItems().get(3)).getChoices().get(1).getId(), null)
			}
		);

		assertEquals(2, responses.size());
		assertTrue(responses.get(0).getChoice().getIsCorrect());

		// Complete assessment attempt and get score

		attempt = assessmentService.completeAttemptForAssessment(attempt.getId());

		assertNotNull(attempt);
		assertNotNull(attempt.getCompletedOn());
		assertTrue(attempt.isComplete());

		// FIXME Failing to populate score when test is transactional
		// assertEquals((new BigDecimal(100)).setScale(2), new BigDecimal(attempt.getScore()).setScale(2));

		AssessmentUserAssociation association = attempt.getAssessmentUserAssociation();

		assertNotNull(association);
		assertTrue(association.getId() > 0);
		assertTrue(association.isComplete());

		// FIXME Failing to populate score when test is transactional
		// assertTrue(association.getPassedFlag());
		// assertEquals((new BigDecimal(100)).setScale(2), new BigDecimal(association.getScore()).setScale(2));
	}

	@Test
	public void createAndCompletePassingAssessmentLatestAttempt() throws Exception {

		Attempt attempt = assessmentService.saveAttemptForAssessment(testTaker.getId(), activeAssessment.getId());

		// Supply correct answer
		List<AttemptResponse> responses = assessmentService.submitResponsesForItemInAssessment(
			testTaker.getId(),
			activeAssessment.getId(),
			activeAssessment.getItems().get(0).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(
					((AbstractItemWithChoices) activeAssessment.getItems().get(0)).getChoices().get(0).getId(), null)
			}
		);

		assertTrue(responses.get(0).getChoice().getIsCorrect());

		// Supply correct answer
		responses = assessmentService.submitResponsesForItemInAssessment(
			testTaker.getId(),
			activeAssessment.getId(),
			activeAssessment.getItems().get(1).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(
					((AbstractItemWithChoices) activeAssessment.getItems().get(1)).getChoices().get(0).getId(), null)
			}
		);

		assertTrue(responses.get(0).getChoice().getIsCorrect());

		// Supply correct answer
		responses = assessmentService.submitResponsesForItemInAssessment(
			testTaker.getId(),
			activeAssessment.getId(),
			activeAssessment.getItems().get(2).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(
					((AbstractItemWithChoices) activeAssessment.getItems().get(2)).getChoices().get(0).getId(), null)
			}
		);

		assertTrue(responses.get(0).getChoice().getIsCorrect());


		assertTrue(responses.get(0).getChoice().getIsCorrect());

		// Complete assessment attempt and get score

		attempt = assessmentService.completeAttemptForAssessment(testTaker.getId(), activeAssessment.getId());

		assertNotNull(attempt);
		assertNotNull(attempt.getCompletedOn());
		assertTrue(attempt.isComplete());

		// FIXME Failing to populate score when test is transactional
		// assertEquals((new BigDecimal(100)).setScale(2), new BigDecimal(attempt.getScore()).setScale(2));

		AssessmentUserAssociation association = attempt.getAssessmentUserAssociation();

		assertNotNull(association);
		assertTrue(association.getId() > 0);
		assertTrue(association.isComplete());

		// FIXME Failing to populate score when test is transactional
		// assertTrue(association.getPassedFlag());
		// assertEquals((new BigDecimal(100)).setScale(2), new BigDecimal(association.getScore()).setScale(2));
	}

	@Test
	public void updateAssessmentAttemptAnswer() throws Exception {

		User worker = newRegisteredWorker();

		Attempt attempt = assessmentService.saveAttemptForAssessment(worker.getId(), activeAssessment.getId());

		List<AttemptResponse> responses = assessmentService.submitResponsesForItem(
			attempt.getId(),
			activeAssessment.getItems().get(0).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(
					((AbstractItemWithChoices) activeAssessment.getItems().get(0)).getChoices().get(1).getId(), null)
			}
		);

		assertFalse(responses.get(0).getChoice().getIsCorrect());

		responses = assessmentService.submitResponsesForItem(
			attempt.getId(),
			activeAssessment.getItems().get(0).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(
					((AbstractItemWithChoices) activeAssessment.getItems().get(0)).getChoices().get(0).getId(), null)
			}
		);

		assertTrue(responses.get(0).getChoice().getIsCorrect());
	}

	@Test(expected = IllegalStateException.class)
	public void multipleAnswersForFixedQuestionFailure() throws Exception {
		User user = newContractor();

		Attempt attempt = assessmentService.saveAttemptForAssessment(user.getId(), assessment.getId());

		assessmentService.submitResponsesForItem(
			attempt.getId(),
			assessment.getItems().get(1).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(
					((AbstractItemWithChoices) assessment.getItems().get(1)).getChoices().get(0).getId(), null),
				new AttemptResponseDTO(
					((AbstractItemWithChoices) assessment.getItems().get(1)).getChoices().get(1).getId(), null)
			}
		);
	}

	@Test
	public void completeFailingAssessmentLatestAttempt() throws Exception {

		assessmentService.saveAttemptForAssessment(testTaker.getId(), activeAssessment.getId());
		submitResponse(testTaker.getId(), activeAssessment, 0, 0);
		submitResponse(testTaker.getId(), activeAssessment, 1, 0);
		submitResponse(testTaker.getId(), activeAssessment, 2, 0);
		submitResponse(testTaker.getId(), activeAssessment, 3, 1);

		Attempt attempt = assessmentService.completeAttemptForAssessment(testTaker.getId(), activeAssessment.getId());

		assertNotNull(attempt);
		assertNotNull(attempt.getCompletedOn());
		assertTrue(attempt.isComplete());
		assertEquals((new BigDecimal(75)).setScale(2), attempt.getScore().setScale(2)); // 80 is passing
	}

	@Ignore
	@Test
	public void completePassingAssessmentAttempt() throws Exception {

		User worker = newRegisteredWorker();
		AbstractAssessment assessment =
			createAssessment("test quiz", testCreator, AssessmentStatusType.ACTIVE, AbstractAssessment.SURVEY_ASSESSMENT_TYPE);
		assessmentService.saveAttemptForAssessment(worker.getId(), activeAssessment.getId());
		Attempt attempt =
			assessmentService.findAttemptsForAssessmentByUser(activeAssessment.getId(), worker.getId()).iterator().next();

		assessmentService.submitResponsesForItem(
			attempt.getId(),
			assessment.getItems().get(2).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(((AbstractItemWithChoices) assessment.getItems().get(2)).getChoices().get(0).getId(), null)
			}
		);

		attempt = assessmentService.completeAttemptForAssessment(attempt.getId());

		assertNotNull(attempt);
		assertNotNull(attempt.getCompletedOn());
		assertTrue(attempt.isComplete());
		assertEquals((new BigDecimal(100)).setScale(2), attempt.getScore().setScale(2));
	}

	@Test
	public void completePassingAssessmentLatestAttempt() throws Exception {

		assessmentService.saveAttemptForAssessment(testTaker.getId(), activeAssessment.getId());
		submitResponse(testTaker.getId(), activeAssessment, 0, 0);
		submitResponse(testTaker.getId(), activeAssessment, 1, 0);
		submitResponse(testTaker.getId(), activeAssessment, 2, 0);
		submitResponse(testTaker.getId(), activeAssessment, 3, 0);

		Attempt attempt = assessmentService.completeAttemptForAssessment(testTaker.getId(), activeAssessment.getId());

		assertNotNull(attempt);
		assertNotNull(attempt.getCompletedOn());
		assertTrue(attempt.isComplete());
		assertEquals((new BigDecimal(100)).setScale(2), attempt.getScore().setScale(2));
	}

	@Test
	public void completePassingAssessmentAttemptForAssessmentWithNoQuestions() throws Exception {

		AbstractAssessment assessmentNoQuestions = createAssessment(
			"Test Quiz", testCreator, AssessmentStatusType.ACTIVE,
			AbstractAssessment.GRADED_ASSESSMENT_TYPE, new AssessmentItemDTO[]{});

		Attempt attempt = assessmentService.saveAttemptForAssessment(CONTRACTOR_USER_ID, assessmentNoQuestions.getId());

		attempt = assessmentService.completeAttemptForAssessment(attempt.getId());

		assertNotNull(attempt);
		assertNotNull(attempt.getCompletedOn());
		assertTrue(attempt.isComplete());
		assertEquals((new BigDecimal(100)).setScale(2), attempt.getScore().setScale(2));

		AssessmentUserAssociation association = attempt.getAssessmentUserAssociation();

		assertNotNull(association);
		assertTrue(association.getId() > 0);
		assertTrue(association.isComplete());
		assertTrue(association.getPassedFlag());
	}

	@Test
	public void attemptAllowedForAssessmentByUser() throws Exception {

		assertTrue(assessmentService.isAttemptAllowedForAssessmentByUser(activeAssessment.getId(), ANONYMOUS_USER_ID));

		Attempt attempt = assessmentService.saveAttemptForAssessment(FRONT_END_USER_ID, activeAssessment.getId());

		assertTrue(assessmentService.isAttemptAllowedForAssessmentByUser(activeAssessment.getId(), FRONT_END_USER_ID));

		assessmentService.completeAttemptForAssessment(attempt.getId());

		// Users are allowed to reattempt tests as necessary
		// assertFalse(assessmentService.attemptAllowedForAssessmentByUser(assessment.getId(), FRONT_END_USER_ID));
	}

	@Test
	@Ignore
	//TODO - This test needs to be checked
	public void toggleAttemptAllowedForAssessmentByUser() throws Exception {

		Attempt attempt = assessmentService.saveAttemptForAssessment(FRONT_END_USER_ID, activeAssessment.getId());
		assessmentService.completeAttemptForAssessment(attempt.getId());

		// Users are allowed to reattempt tests as necessary
		// assertFalse(assessmentService.attemptAllowedForAssessmentByUser(assessment.getId(), FRONT_END_USER_ID));

		allowAttemptForAssessmentByUser(assessment.getId(), FRONT_END_USER_ID, true);

		assertTrue(assessmentService.isAttemptAllowedForAssessmentByUser(activeAssessment.getId(), FRONT_END_USER_ID));

		allowAttemptForAssessmentByUser(assessment.getId(), FRONT_END_USER_ID, false);

		assertFalse(assessmentService.isAttemptAllowedForAssessmentByUser(activeAssessment.getId(), FRONT_END_USER_ID));
	}

	@Test
	public void completePassingAssessmentAttemptScopedToWork() throws Exception {

		assertTrue(assessmentService.isAttemptAllowedForAssessmentByUserScopedToWork(
			activeAssessment.getId(), FRONT_END_USER_ID, WORK_ID));

		Attempt attempt = assessmentService.saveAttemptForAssessmentScopedToWork(
			FRONT_END_USER_ID, activeAssessment.getId(), WORK_ID, null);

		assessmentService.submitResponsesForItem(
			attempt.getId(),
			activeAssessment.getItems().get(2).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(
					((AbstractItemWithChoices) activeAssessment.getItems().get(2)).getChoices().get(0).getId(), null)
			}
		);

		attempt = assessmentService.completeAttemptForAssessment(attempt.getId());

		assertNotNull(attempt);
		assertTrue(attempt instanceof WorkScopedAttempt);
		assertFalse(assessmentService.isAttemptAllowedForAssessmentByUserScopedToWork(
			activeAssessment.getId(), FRONT_END_USER_ID, WORK_ID));
		assertTrue(assessmentService.isAttemptAllowedForAssessmentByUserScopedToWork(
			activeAssessment.getId(), FRONT_END_USER_ID, WORK_2_ID));
	}

	@Test
	public void findAssessmentInvitationsForAssessment() throws Exception {
		AbstractAssessment assessment = newAssessment();

		User worker1 = newRegisteredWorker();
		User worker2 = newRegisteredWorker();
		User worker3 = newRegisteredWorker();
		User worker4 = newRegisteredWorker();
		for (Long uid : new Long[]{EMPLOYEE_USER_ID, worker1.getId(), worker2.getId(), worker3.getId(), worker4.getId()}) {
			requestService.inviteUserToAssessment(EMPLOYEE_USER_ID, uid, assessment.getId());
		}

		assertEquals(5, requestService.findAssessmentInvitationRequestsByAssessment(assessment.getId()).size());
		assertEquals(Integer.valueOf(5), requestService.findAssessmentInvitationRequestsByAssessment(
			assessment.getId(), new RequestPagination(true))
			.getRowCount());
	}

	// Assessments and work

	@Test
	@Ignore
	public void findAssessmentsForWork() throws Exception {
		Work work = workService.findWork(1L);

		assertTrue(work.getAssessments().size() > 0);

		work = workService.findWork(2L);

		assertTrue(work.getAssessments().size() > 0);
		assertEquals("Driving Test", work.getAssessments().iterator().next().getName());

		work = workService.findWork(3L);

		assertEquals(0, work.getAssessments().size());
	}

	@Test
	public void addAssessmentToWork() throws Exception {

		Work work = newWork(testCreator.getId());

		assertEquals(0, assessmentService.findAllWorkAssessmentAssociationByWork(work.getId()).size());

		assessmentService.addAssessmentToWork(assessment.getId(), false, work.getId());

		work = workService.findWork(work.getId());

		assertTrue(assessmentService.findAllWorkAssessmentAssociationByWork(work.getId()).size() > 0);
	}

	@Test
	@Ignore
	public void requestContext() throws Exception {
		User employee1 = newFirstEmployeeWithCashBalance();
		User employee2 = newCompanyEmployeeSharedWorkerConfirmed(employee1.getCompany().getId());
		User contractor1 = newContractorIndependentLane4ReadyWithCashBalance();
		User contractor2 = newContractorIndependentLane4ReadyWithCashBalance();
		User contractor3 = newContractorIndependentLane4ReadyWithCashBalance();

		AbstractAssessment assessment = newAssessment(employee1);

		laneService.addUserToCompanyLane1(employee1.getId(), employee1.getCompany().getId());
		laneService.addUserToCompanyLane1(employee2.getId(), employee1.getCompany().getId());
		laneService.addUserToCompanyLane2(contractor2.getId(), employee1.getCompany().getId());
		requestService.inviteUserToAssessment(employee1.getId(), contractor3.getId(), assessment.getId());

		authenticationService.setCurrentUser(employee1);
		List<RequestContext> contexts = assessmentService.getRequestContext(assessment.getId());
		assertEquals(2, contexts.size());
		assertTrue(contexts.contains(RequestContext.PUBLIC));
		assertTrue(contexts.contains(RequestContext.OWNER));

		authenticationService.setCurrentUser(employee2);
		contexts = assessmentService.getRequestContext(assessment.getId());
		assertEquals(3, contexts.size());
		assertTrue(contexts.contains(RequestContext.PUBLIC));
		assertTrue(contexts.contains(RequestContext.COMPANY_OWNED));
		assertTrue(contexts.contains(RequestContext.WORKER_POOL));
		assertFalse(contexts.contains(RequestContext.INVITED));

		authenticationService.setCurrentUser(contractor1);
		contexts = assessmentService.getRequestContext(assessment.getId());
		assertEquals(1, contexts.size());
		assertTrue(contexts.contains(RequestContext.PUBLIC));
		assertFalse(contexts.contains(RequestContext.COMPANY_OWNED));
		assertFalse(contexts.contains(RequestContext.WORKER_POOL));
		assertFalse(contexts.contains(RequestContext.INVITED));

		authenticationService.setCurrentUser(contractor2);
		contexts = assessmentService.getRequestContext(assessment.getId());
		assertEquals(2, contexts.size());
		assertTrue(contexts.contains(RequestContext.PUBLIC));
		assertFalse(contexts.contains(RequestContext.COMPANY_OWNED));
		assertTrue(contexts.contains(RequestContext.WORKER_POOL));
		assertFalse(contexts.contains(RequestContext.INVITED));

		authenticationService.setCurrentUser(contractor3);
		contexts = assessmentService.getRequestContext(assessment.getId());
		assertEquals(2, contexts.size());
		assertTrue(contexts.contains(RequestContext.PUBLIC));
		assertFalse(contexts.contains(RequestContext.COMPANY_OWNED));
		assertFalse(contexts.contains(RequestContext.WORKER_POOL));
		assertTrue(contexts.contains(RequestContext.INVITED));
	}

	@Test
	@Ignore
	public void requestResourceContext() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		User contractor1 = newContractorIndependentLane4ReadyWithCashBalance();
		User contractor2 = newContractorIndependentLane4ReadyWithCashBalance();

		AbstractAssessment assessment = newAssessment(employee);
		assessmentService.saveOrUpdateAssessment(assessment);

		laneService.addUserToCompanyLane1(employee.getId(), employee.getCompany().getId());
		laneService.addUserToCompanyLane2(contractor1.getId(), employee.getCompany().getId());
		laneService.addUserToCompanyLane2(contractor2.getId(), employee.getCompany().getId());

		Work work = newWork(employee.getId());
		assessmentService.addAssessmentToWork(assessment.getId(), true, work.getId());
		workRoutingService.addToWorkResources(
			work.getWorkNumber(), Sets.newHashSet(contractor1.getUserNumber(), contractor2.getUserNumber()));
		workService.acceptWork(contractor1.getId(), work.getId());

		authenticationService.setCurrentUser(employee);
		List<RequestContext> contexts = assessmentService.getRequestContext(assessment.getId());
		assertEquals(2, contexts.size());
		assertTrue(contexts.contains(RequestContext.PUBLIC));
		assertTrue(contexts.contains(RequestContext.WORKER_POOL));
		assertFalse(contexts.contains(RequestContext.RESOURCE));

		authenticationService.setCurrentUser(contractor1);
		contexts = assessmentService.getRequestContext(assessment.getId());
		assertEquals(3, contexts.size());
		assertTrue(contexts.contains(RequestContext.PUBLIC));
		assertTrue(contexts.contains(RequestContext.WORKER_POOL));
		assertTrue(contexts.contains(RequestContext.RESOURCE));

		authenticationService.setCurrentUser(contractor2);
		contexts = assessmentService.getRequestContext(assessment.getId());
		assertEquals(2, contexts.size());
		assertTrue(contexts.contains(RequestContext.PUBLIC));
		assertTrue(contexts.contains(RequestContext.WORKER_POOL));
		assertFalse(contexts.contains(RequestContext.RESOURCE));
	}

	@Test
	public void findAllAssessmentUsers() throws Exception {
		User user = newEmployeeWithCashBalance();
		admissionService.saveAdmissionForCompanyIdAndVenue(user.getCompany().getId(), Venue.MARKETPLACE);
		User worker = newRegisteredWorker();
		authenticationService.setCurrentUser(user);
		UserGroup userGroup = newPublicUserGroup(user);
		Industry technology = invariantDataService.findIndustry(1000L);
		GradedAssessment assessment = (GradedAssessment) newAssessmentForUser(user, technology, true);
		GradedAssessment assessmentForGroup = (GradedAssessment) newAssessmentForUser(user, technology, true);
		userGroupRequirementSetService.addTestRequirement(userGroup.getId(), assessmentForGroup.getId());
		requestService.inviteUsersToGroup(user.getId(), new long[]{worker.getId()}, userGroup.getId());

		AssessmentUserPagination pagination = new AssessmentUserPagination(true);

		pagination = assessmentService.findAllAssessmentUsers(assessment.getId(), pagination);
		assertTrue(pagination.getRowCount() > 0);

		AggregatesDTO dto = assessmentService.countAssessmentUsers(assessment.getId(), pagination);
		assertTrue(dto.getCountForStatus(AssessmentUserPagination.NOT_INVITED) > 0);
		assertTrue(dto.getCountForStatus(AssessmentUserPagination.PASSED) == 0);
		assertTrue(dto.getCountForStatus(AssessmentUserPagination.FAILED) == 0);
		assertTrue(dto.getCountForStatus(AssessmentUserPagination.INVITED) == 0);

		assertEquals(pagination.getRowCount(), dto.getCountForStatus(AssessmentUserPagination.ALL));

		requestService.inviteUserToAssessment(worker.getId(), worker.getId(), assessment.getId());

		ManagedAssessmentPagination invitationPagination = new ManagedAssessmentPagination(true);

		invitationPagination
			.setInvitationFilter(ManagedAssessmentPagination.INVITATION_FILTER_KEYS.DIRECTLY_INVITED)
			.setTakeabilityFilter(ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS.TAKEABLE)
			.setActivityFilter(ManagedAssessmentPagination.ACTIVITY_FILTER_KEYS.ACTIVE)
			.setTypeFilter(ManagedAssessmentPagination.TYPE_FILTER_KEYS.GRADED)
			.setPrivacyFilter(ManagedAssessmentPagination.PRIVACY_FILTER_KEYS.PRIVACY_PUBLIC);
		invitationPagination = assessmentService.findAssessmentsForUser(worker.getId(), invitationPagination);
		assertEquals(invitationPagination.getRowCount().intValue(), 1);
		assertEquals(invitationPagination.getResults().get(0).getAssessmentId(), assessment.getId());

		invitationPagination
			.setInvitationFilter(ManagedAssessmentPagination.INVITATION_FILTER_KEYS.GROUP_INVITED)
			.setTakeabilityFilter(ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS.TAKEABLE)
			.setActivityFilter(ManagedAssessmentPagination.ACTIVITY_FILTER_KEYS.ACTIVE)
			.setTypeFilter(ManagedAssessmentPagination.TYPE_FILTER_KEYS.GRADED)
			.setPrivacyFilter(ManagedAssessmentPagination.PRIVACY_FILTER_KEYS.PRIVACY_PUBLIC);
		invitationPagination = assessmentService.findAssessmentsForUser(worker.getId(), invitationPagination);
		assertEquals(invitationPagination.getRowCount().intValue(), 1);
		assertEquals(invitationPagination.getResults().get(0).getAssessmentId(), assessmentForGroup.getId());

		invitationPagination
			.setInvitationFilter(ManagedAssessmentPagination.INVITATION_FILTER_KEYS.DIRECTLY_OR_GROUP_INVITED)
			.setTakeabilityFilter(ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS.TAKEABLE)
			.setActivityFilter(ManagedAssessmentPagination.ACTIVITY_FILTER_KEYS.ACTIVE)
			.setTypeFilter(ManagedAssessmentPagination.TYPE_FILTER_KEYS.GRADED)
			.setPrivacyFilter(ManagedAssessmentPagination.PRIVACY_FILTER_KEYS.PRIVACY_PUBLIC);
		invitationPagination = assessmentService.findAssessmentsForUser(worker.getId(), invitationPagination);
		assertEquals(invitationPagination.getRowCount().intValue(), 2);
	}

	@Test
	public void findAssessment_withBlockedUser() throws Exception {
		User user = newContractor();
		admissionService.saveAdmissionForCompanyIdAndVenue(user.getCompany().getId(), Venue.MARKETPLACE);

		User worker = newRegisteredWorker();

		ManagedAssessmentPagination pagination = new ManagedAssessmentPagination();
		pagination.setReturnAllRows();
		pagination = assessmentService.findAssessmentsForUser(worker.getId(), pagination);
		int testCount = pagination.getResults().size();
		newAssessmentForUser(user, null, true);
		pagination = assessmentService.findAssessmentsForUser(worker.getId(), pagination);
		assertEquals(testCount + 1, pagination.getResults().size());

		userService.blockUser(user.getId(), worker.getId());
		pagination = assessmentService.findAssessmentsForUser(worker.getId(), pagination);
		assertEquals(testCount, pagination.getResults().size());
	}

	public void allowAttemptForAssessmentByUser(
		Long assessmentId,
		Long userId,
		boolean allowFlag) {
		AssessmentUserAssociation association =
			assessmentService.findAssessmentUserAssociationByUserAndAssessment(userId, assessmentId);
		if (association == null) {
			return;
		}
		association.setReattemptAllowedFlag(allowFlag);
		assessmentUserAssociationDAO.saveOrUpdate(association);
	}

	public AssessmentService getAssessmentService() {
		return assessmentService;
	}

	public void setAssessmentService(AssessmentService assessmentService) {
		this.assessmentService = assessmentService;
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public SessionFactory getFactory() {
		return factory;
	}

	public void setFactory(SessionFactory factory) {
		this.factory = factory;
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public RequestService getRequestService() {
		return requestService;
	}

	public void setRequestService(RequestService requestService) {
		this.requestService = requestService;
	}

	private AbstractAssessment createAssessment(
		String name,
		User owner,
		String status,
		String type) {
		return createAssessment(name, owner, status, type, null);
	}

	private AbstractAssessment createAssessment(
		String name,
		User owner,
		String status,
		String type,
		AssessmentItemDTO[] assessmentItems) {
		AssessmentDTO assessmentDTO = new AssessmentDTO();
		assessmentDTO.setName(name);
		assessmentDTO.setDescription("A description.");
		assessmentDTO.setIndustryId(INDUSTRY_1_ID);
		assessmentDTO.setPassingScore(80.0);
		assessmentDTO.setType(type);
		assessmentDTO.setAssessmentStatusTypeCode(status);

		AbstractAssessment assessment = assessmentService.saveOrUpdateAssessment(owner.getId(), assessmentDTO);

		if (assessmentItems != null) {
			List<AbstractItem> questions = assessmentService.saveOrUpdateItemsInAssessment(
				assessment.getId(), assessmentItems);

			AssessmentChoiceDTO[] choices = new AssessmentChoiceDTO[]{
				createChoice("1", true),
				createChoice("2", false),
				createChoice("3", false),
				createChoice("4", false)
			};

			for (AbstractItem question : questions) {
				assessmentService.saveOrUpdateChoicesInItem(question.getId(), choices);
			}
		}
		return assessmentService.findAssessment(assessment.getId());
	}

	private AssessmentChoiceDTO createChoice(
		String value,
		boolean correct) {
		AssessmentChoiceDTO answerDTO = new AssessmentChoiceDTO();
		answerDTO.setValue(value);
		answerDTO.setIsCorrect(correct);
		return answerDTO;
	}

	private AssessmentItemDTO createAssessmentItemDTO(
		String prompt,
		String type) {
		AssessmentItemDTO item = new AssessmentItemDTO();
		item.setPrompt(prompt);
		item.setType(type);
		item.setGraded(true);
		return item;
	}

	private void submitResponse(
		Long testTakerId,
		AbstractAssessment assessment,
		int itemIndex,
		int choiceIndex)
		throws AssetTransformationException, AssessmentAttemptTimedOutException, HostServiceException, IOException {

		assessmentService.submitResponsesForItemInAssessment(
			testTakerId,
			assessment.getId(),
			assessment.getItems().get(itemIndex).getId(),
			new AttemptResponseDTO[]{
				new AttemptResponseDTO(
					((AbstractItemWithChoices) assessment.getItems().get(itemIndex)).getChoices().get(choiceIndex).getId(), null)
			}
		);
	}
}
