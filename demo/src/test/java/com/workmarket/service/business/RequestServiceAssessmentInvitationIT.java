package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AbstractItemWithChoices;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.request.AssessmentInvitation;
import com.workmarket.domains.model.request.Request;
import com.workmarket.service.business.dto.AttemptResponseDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.workmarket.utility.CollectionUtilities.first;
import static com.workmarket.utility.CollectionUtilities.last;
import static org.junit.Assert.*;

/**
 * Created by nick on 7/16/13 7:04 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RequestServiceAssessmentInvitationIT extends BaseServiceIT {

	@Autowired private AssessmentService assessmentService;
	@Autowired private RequestService requestService;

	private User employee;
	private Long companyId;
	private Long employeeId;
	private Long contractorId;
	private Long contractor2Id;
	private AbstractAssessment assessment;
	private User contractor;
	private User contractor2;

	private boolean setUpIsDone = false;

	@Before
	public void initData() throws Exception {
		if (setUpIsDone) {
			return;
		}
		employee = newWMEmployee();
		employeeId = employee.getId();
		companyId = employee.getCompany().getId();

		contractor = newContractorIndependentlane4Ready();
		contractorId = contractor.getId();
		contractor2 = newContractorIndependentlane4Ready();
		contractor2Id = contractor2.getId();
		laneService.addUsersToCompanyLane2(Lists.newArrayList(contractorId, contractor2Id), companyId);

		assessment = newAssessment(employee);
		setUpIsDone = true;
	}

	@Test
	@Transactional
	public void inviteUserToAssessment_InviteSingleUser_CountIncreasesByOne() throws Exception {

		int requesterCount = requestService.findRequestsByRequestor(employeeId).size();
		int inviteeCount = requestService.findRequestsByInvitedUser(contractorId).size();

		requestService.inviteUserToAssessment(employeeId, contractorId, assessment.getId());

		List<Request> inviteeRequests = requestService.findRequestsByInvitedUser(contractorId);

		assertEquals(requesterCount + 1, requestService.findRequestsByRequestor(employeeId).size());
		assertEquals(inviteeCount + 1, inviteeRequests.size());
		assertTrue(last(inviteeRequests) instanceof AssessmentInvitation);
	}

	@Test
	@Transactional
	public void inviteUserToAssessment_InviteMultipleUsers_CountIncreasesBySameAmount() throws Exception {
		int requesterCount = requestService.findRequestsByRequestor(employeeId).size();
		int inviteeCount = requestService.findRequestsByInvitedUser(contractorId).size();
		int inviteeCount2 = requestService.findRequestsByInvitedUser(contractor2Id).size();

		requestService.inviteUsersToAssessment(employeeId,
				Lists.newArrayList(contractorId, contractor2Id),
				assessment.getId());

		List<Request> inviteeRequests = requestService.findRequestsByInvitedUser(contractorId);
		List<Request> invitee2Requests = requestService.findRequestsByInvitedUser(contractor2Id);

		assertEquals(requesterCount + 2, requestService.findRequestsByRequestor(employeeId).size());
		assertEquals(inviteeCount + 1, inviteeRequests.size());
		assertEquals(inviteeCount2 + 1, invitee2Requests.size());
		assertTrue(last(inviteeRequests) instanceof AssessmentInvitation);
	}


	//@Test(expected = IllegalStateException.class) TODO: redo this one
	@Transactional
	public void test_hasUserPassedAssessment() throws Exception {

		AbstractAssessment assessment = assessmentService.findAssessment(1L);
		Attempt attempt = first(assessmentService.findAttemptsForAssessmentByUser(assessment.getId(), employeeId));

		List<AttemptResponseDTO> responses = Lists.newArrayList(new AttemptResponseDTO(((AbstractItemWithChoices) assessment.getItems()
				.get(2)).getChoices().get(0).getId(), null));

		assessmentService.submitResponsesForItem(attempt.getId(), assessment.getItems().get(2).getId(),
				responses.toArray(new AttemptResponseDTO[responses.size()]));

		attempt = assessmentService.completeAttemptForAssessment(attempt.getId());

		assertNotNull(attempt);
		assertNotNull(attempt.getCompletedOn());
		assertTrue(attempt.getCompletedOn() != null);

		assertTrue(assessmentService.hasUserPassedAssessment(FRONT_END_USER_ID, assessment.getId()));

		requestService.inviteUserToAssessment(assessment.getCreatorId(), FRONT_END_USER_ID, assessment.getId());
	}
}
