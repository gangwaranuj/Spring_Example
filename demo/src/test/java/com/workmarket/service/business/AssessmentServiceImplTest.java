package com.workmarket.service.business;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.workmarket.dao.assessment.AbstractAssessmentDAO;
import com.workmarket.dao.assessment.AbstractItemDAO;
import com.workmarket.dao.assessment.AssessmentGroupAssociationDAO;
import com.workmarket.dao.assessment.AttemptDAO;
import com.workmarket.dao.assessment.ManagedAssessmentDAO;
import com.workmarket.dao.assessment.WorkAssessmentAssociationDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.dao.requirement.TestRequirementDAO;
import com.workmarket.dao.skill.SkillDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentConfiguration;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.assessment.ManagedAssessment;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination;
import com.workmarket.domains.model.assessment.WorkAssessmentAssociation;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.assessment.RecommendedAssessmentCache;
import com.workmarket.service.business.dto.AssessmentDTO;
import com.workmarket.service.business.event.EventFactoryImpl;
import com.workmarket.service.infra.event.EventRouterImpl;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyLong;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentServiceImplTest {

	@Mock EventRouterImpl eventRouter;
	@Mock EventFactoryImpl eventFactory;
	@Mock ProfileDAO profileDAO;
	@Mock ManagedAssessmentDAO assessmentUserInvitationDAO;
	@Mock RecommendedAssessmentCache recommendedAssessmentCache;
	@Mock Optional<List<Long>> assessmentIds;
	@Mock Profile profile;
	@Mock User user;
	@Mock Company company;
	@Mock WorkAssessmentAssociationDAO workAssessmentAssociationDAO;
	@Mock BaseWorkDAO baseWorkDAO;
	@Mock AbstractAssessmentDAO assessmentDAO;
	@Mock AssessmentGroupAssociationDAO assessmentGroupAssociationDAO;
	@Mock TestRequirementDAO testRequirementDAO;
	@Mock WorkService workService;
	@Mock AttemptDAO attemptDAO;
	@Mock AbstractItemDAO abstractItemDAO;
	@Mock SkillDAO skillDAO;
	@InjectMocks AssessmentServiceImpl assessmentService = spy(new AssessmentServiceImpl());

	private static final List<Long> EMPTY_LIST = ImmutableList.of();

	private static Long
		USER_ID = 1L,
		TEST_ID = 2L,
		PROFILE_ID = 3L,
		COMPANY_ID = 4L,
		WORK_ID1 = 5L,
		WORK_ID2 = 6L;
	private static String USER_NUMBER = "1001";
	Set<String> resourceUserNumbers = Sets.newHashSet();
	List<ManagedAssessment> results = Lists.newArrayList();
	ManagedAssessmentPagination pagination = new ManagedAssessmentPagination();
	List<AssessmentDTO> assessmentDTOs;
	AssessmentDTO assessmentDTO;
	List<WorkAssessmentAssociation> workAssessmentAssociations;
	WorkAssessmentAssociation workAssessmentAssociation;
	AbstractWork work;
	AbstractAssessment assessment;
	Attempt attempt;
	AssessmentConfiguration config;

	@Before
	public void setup() {
		assessmentDTO = mock(AssessmentDTO.class);
		assessmentDTOs = Lists.newArrayList();
		assessmentDTOs.add(assessmentDTO);
		when(assessmentDTO.getId()).thenReturn(TEST_ID);
		resourceUserNumbers.add("1");
		resourceUserNumbers.add("2");

		work = mock(AbstractWork.class);
		assessment = mock(AbstractAssessment.class);
		attempt = mock(Attempt.class);
		config = mock(AssessmentConfiguration.class);
		when(baseWorkDAO.get(WORK_ID1)).thenReturn(work);
		when(assessmentDAO.get(TEST_ID)).thenReturn(assessment);
		when(assessment.getConfiguration()).thenReturn(config);

		workAssessmentAssociation = mock(WorkAssessmentAssociation.class);
		workAssessmentAssociations = Lists.newArrayList();
		workAssessmentAssociations.add(workAssessmentAssociation);
		when(assessmentService.findAllWorkAssessmentAssociationByWork(WORK_ID1)).thenReturn(workAssessmentAssociations);
		when(workAssessmentAssociationDAO.findByWorkAndAssessment(WORK_ID1, TEST_ID)).thenReturn(workAssessmentAssociation);

		when(profileDAO.findByUser(USER_ID)).thenReturn(profile);
		when(profile.getId()).thenReturn(PROFILE_ID);
		when(profile.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(USER_ID);
		when(user.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(COMPANY_ID);
		pagination.setResults(results);

		doReturn(workAssessmentAssociation).when(assessmentService).makeWorkAssessmentAssociation();
	}

	@Test
	public void addUsersToTest_eventFired() {
		assessmentService.addUsersToTest(USER_ID, USER_NUMBER, TEST_ID, resourceUserNumbers);

		verify(eventFactory).buildInviteUsersToAssessmentEvent(USER_ID, resourceUserNumbers, TEST_ID);
	}

	@Test
	public void findRecommendedAssessmentsForUser_cacheMiss() {
		when(recommendedAssessmentCache.get(USER_ID)).thenReturn(assessmentIds);
		when(assessmentIds.isPresent()).thenReturn(false);
		doReturn(pagination).when(assessmentService).findAssessmentsForUser(anyLong(), any(ManagedAssessmentPagination.class));

		assessmentService.findRecommendedAssessmentsForUser(USER_ID);

		verify(recommendedAssessmentCache).set(USER_ID, pagination);
	}

	@Test
	public void findRecommendedAssessmentsForUser_cacheHit() {
		when(recommendedAssessmentCache.get(USER_ID)).thenReturn(assessmentIds);
		when(assessmentIds.isPresent()).thenReturn(true);
		doReturn(pagination).when(assessmentService).findAssessmentsForUser(anyLong(), any(ManagedAssessmentPagination.class));

		assessmentService.findRecommendedAssessmentsForUser(USER_ID);

		verify(recommendedAssessmentCache, never()).set(USER_ID, pagination);
	}

	@Test
	public void setAssessmentsForWork_withNullAssessmentsList_earlyReturn() {
		assessmentService.setAssessmentsForWork(null, WORK_ID1);

		verify(workAssessmentAssociation, never()).setDeleted(anyBoolean());
	}

	@Test
	public void setAssessmentsForWork_withEmptyAssessmentsList_earlyReturn() {
		assessmentService.setAssessmentsForWork(Lists.<AssessmentDTO>newArrayList(), WORK_ID1);

		verify(workAssessmentAssociation, never()).setDeleted(anyBoolean());
	}

	@Test(expected = IllegalArgumentException.class)
	public void setAssessmentsForWork_withNullWorkId_throwException() {
		assessmentService.setAssessmentsForWork(assessmentDTOs, null);
	}

	@Test
	public void setAssessmentsForWork_deleteExistingAssessments() {
		assessmentService.setAssessmentsForWork(assessmentDTOs, WORK_ID1);

		verify(workAssessmentAssociation).setDeleted(true);
	}

	@Test
	public void setAssessmentsForWork_addNewAssessments() {
		assessmentService.setAssessmentsForWork(assessmentDTOs, WORK_ID1);

		verify(assessmentService).addAssessmentToWork(assessmentDTO.getId(), assessmentDTO.isRequired(), WORK_ID1);
	}

	@Test
	public void addAssessmentsToWork_noExistingAssociation_createNewRecord() {
		when(workAssessmentAssociationDAO.findByWorkAndAssessment(WORK_ID1, TEST_ID)).thenReturn(null);

		assessmentService.addAssessmentToWork(TEST_ID, true, WORK_ID1);

		verify(assessmentService).makeWorkAssessmentAssociation();
		verify(workAssessmentAssociation).setWork(work);
		verify(workAssessmentAssociation).setAssessment(assessment);
		verify(workAssessmentAssociation).setDeleted(false);
		verify(workAssessmentAssociation).setRequired(true);
		verify(workAssessmentAssociationDAO).saveOrUpdate(workAssessmentAssociation);
	}

	@Test
	public void addAssessmentsToWork_withExistingAssociation_updateRecord() {
		assessmentService.addAssessmentToWork(TEST_ID, true, WORK_ID1);

		verify(assessmentService, never()).makeWorkAssessmentAssociation();
		verify(workAssessmentAssociation, never()).setWork(work);
		verify(workAssessmentAssociation, never()).setAssessment(assessment);
		verify(workAssessmentAssociation).setDeleted(false);
		verify(workAssessmentAssociation).setRequired(true);
		verify(workAssessmentAssociationDAO).saveOrUpdate(workAssessmentAssociation);
	}

	@Test
	public void nonFeaturedAssessment_withWorkerInvitedToGroup_canTake() {
		when(assessmentGroupAssociationDAO.isUserAllowedToTakeAssessment(TEST_ID, USER_ID)).thenReturn(true);

		assertTrue(assessmentService.isUserAllowedToTakeAssessment(TEST_ID, USER_ID));
	}

	@Test
	public void nonFeaturedAssessment_withWorkerInvitedToWork_canTake() {
		when(testRequirementDAO.findSentWorkIdsWithTestRequirement(TEST_ID, USER_ID)).thenReturn(ImmutableList.of(WORK_ID1));
		when(workService.isUserWorkResourceForWork(USER_ID, WORK_ID1)).thenReturn(true);

		assertTrue(assessmentService.isUserAllowedToTakeAssessment(TEST_ID, USER_ID));
	}

	@Test
	public void nonFeaturedAssessment_withWorkerInvitedToWorkWithoutAssessmentWithGroupAssessment_canTake() {
		when(testRequirementDAO.findSentWorkIdsWithTestRequirement(TEST_ID, USER_ID)).thenReturn(EMPTY_LIST);
		when(testRequirementDAO.findSentWorkIdsWithTestRequirementFromGroup(TEST_ID, USER_ID)).thenReturn(ImmutableList.of(WORK_ID1));
		when(workService.isUserWorkResourceForWork(USER_ID, WORK_ID1)).thenReturn(true);

		assertTrue(assessmentService.isUserAllowedToTakeAssessment(TEST_ID, USER_ID));
	}

	@Test
	public void nonFeaturedAssessment_withWorkerInvitedToWorkWithAssessmentAndGroupAssessment_canTake() {
		when(testRequirementDAO.findSentWorkIdsWithTestRequirement(TEST_ID, USER_ID)).thenReturn(ImmutableList.of(WORK_ID1));
		when(testRequirementDAO.findSentWorkIdsWithTestRequirementFromGroup(TEST_ID, USER_ID)).thenReturn(ImmutableList.of(WORK_ID2));
		when(workService.isUserWorkResourceForWork(USER_ID, WORK_ID1)).thenReturn(false);
		when(workService.isUserWorkResourceForWork(USER_ID, WORK_ID2)).thenReturn(true);

		assertTrue(assessmentService.isUserAllowedToTakeAssessment(TEST_ID, USER_ID));
	}

	@Test
	public void nonFeaturedAssessment_withWorkInWorkFeed_canTake() {
		when(testRequirementDAO.findSentWorkIdsWithTestRequirement(TEST_ID, USER_ID)).thenReturn(ImmutableList.of(WORK_ID1));
		when(workService.isWorkShownInFeed(WORK_ID1)).thenReturn(true);

		assertTrue(assessmentService.isUserAllowedToTakeAssessment(TEST_ID, USER_ID));
	}

	@Test
	public void nonFeaturedAssessment_notInvitedWorker_cannotTake() {
		when(testRequirementDAO.findSentWorkIdsWithTestRequirement(TEST_ID, USER_ID)).thenReturn(ImmutableList.of(WORK_ID1));

		assertFalse(assessmentService.isUserAllowedToTakeAssessment(TEST_ID, USER_ID));
	}

	@Test
	public void nonFeaturedAssessment_notSentWorkAssociatedWithAssessment_cannotTake() {
		when(testRequirementDAO.findSentWorkIdsWithTestRequirement(TEST_ID, USER_ID)).thenReturn(EMPTY_LIST);
		when(testRequirementDAO.findSentWorkIdsWithTestRequirementFromGroup(TEST_ID, USER_ID)).thenReturn(EMPTY_LIST);

		assertFalse(assessmentService.isUserAllowedToTakeAssessment(TEST_ID, USER_ID));
	}

	@Test
	public void getTimeUntilAttemptExpires_noDurationExists_returnsZero() {
		when(config.getDurationMinutes()).thenReturn(null);
		when(attemptDAO.findLatestForAssessmentByUser(TEST_ID, USER_ID)).thenReturn(null);

		Long minutesUntilAttemptExpires = TimeUnit.MILLISECONDS.toMinutes(assessmentService.getTimeUntilAttemptExpires(TEST_ID, USER_ID));
		assertEquals(minutesUntilAttemptExpires, Long.valueOf(0));
	}

	@Test
	public void getTimeUntilAttemptExpires_noAttemptExists_returnsFullDurationLeft() {
		when(config.getDurationMinutes()).thenReturn(5);
		when(attemptDAO.findLatestForAssessmentByUser(TEST_ID, USER_ID)).thenReturn(null);

		Long minutesUntilAttemptExpires = TimeUnit.MILLISECONDS.toMinutes(assessmentService.getTimeUntilAttemptExpires(TEST_ID, USER_ID));
		assertEquals(minutesUntilAttemptExpires, Long.valueOf(5));
	}

	@Test
	public void getTimeUntilAttemptExpires_attemptExists_returnsTimeLeft() {
		final Calendar attemptStartDate = DateUtilities.addMinutes(DateUtilities.getCalendarNow(), -1);
		when(config.getDurationMinutes()).thenReturn(5);
		when(attempt.getCreatedOn()).thenReturn(attemptStartDate);
		when(attemptDAO.findLatestForAssessmentByUser(TEST_ID, USER_ID)).thenReturn(attempt);

		Long minutesUntilAttemptExpires = TimeUnit.MILLISECONDS.toMinutes(assessmentService.getTimeUntilAttemptExpires(TEST_ID, USER_ID) + 500);
		assertEquals(minutesUntilAttemptExpires, Long.valueOf(4));
	}
}
