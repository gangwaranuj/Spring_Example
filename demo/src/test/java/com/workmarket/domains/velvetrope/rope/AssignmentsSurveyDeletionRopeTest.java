package com.workmarket.domains.velvetrope.rope;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.assessment.WorkAssessmentAssociation;
import com.workmarket.service.business.AssessmentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentsSurveyDeletionRopeTest {
	@Mock AssessmentService assessmentService;
	@InjectMocks AssignmentsSurveyDeletionRope assignmentsSurveyDeletionRope;

	private static long WORK_ID = 1L, ASSESMENT_ID = 2L;

	List<WorkAssessmentAssociation> preExistingWorkAssessmentAssociations;
	WorkAssessmentAssociation preExistingWorkAssessmentAssociation;

	@Before
	public void setUp() {
		preExistingWorkAssessmentAssociation = mock(WorkAssessmentAssociation.class);

		preExistingWorkAssessmentAssociations = Lists.newArrayList();
		preExistingWorkAssessmentAssociations.add(preExistingWorkAssessmentAssociation);

		when(assessmentService.findAllWorkAssessmentAssociationByWork(anyLong()))
			.thenReturn(preExistingWorkAssessmentAssociations);
	}

	@Test
	public void enter_preExistingAssessmentAssociationsExist_assessmentAssociationsRetrieved() {
		assignmentsSurveyDeletionRope.enter();

		verify(assessmentService, times(1)).findAllWorkAssessmentAssociationByWork(anyLong());
	}

	@Test
	public void enter_preExistingAssessmentAssociationsExist_assessmentAssociationsSoftDeleted() {
		assignmentsSurveyDeletionRope.enter();

		verify(preExistingWorkAssessmentAssociation).setDeleted(true);
	}

	@Test
	public void enter_preExistingAssessmentAssociationsDoNotExist_assessmentAssociationsNotSoftDeleted() {
		when(assessmentService.findAllWorkAssessmentAssociationByWork(anyLong()))
			.thenReturn(Lists.<WorkAssessmentAssociation>newArrayList());

		assignmentsSurveyDeletionRope.enter();

		verify(preExistingWorkAssessmentAssociation, never()).setDeleted(true);
	}
}
