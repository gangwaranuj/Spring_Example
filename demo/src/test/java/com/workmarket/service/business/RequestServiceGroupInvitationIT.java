package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.request.Request;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.service.business.dto.InvitationDTO;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RequestServiceGroupInvitationIT extends BaseServiceIT {

	@Autowired private RequestService requestService;
	@Autowired private UserGroupService groupService;

	private User employee;
	private Long companyId;
	private Long employeeId;
	private Long contractorId;
	private Long contractor2Id;
	private User contractor;
	private User contractor2;
	private UserGroup group;
	private InvitationDTO newUserInvitationDTO;

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

		newUserInvitationDTO = new InvitationDTO();
		newUserInvitationDTO.setFirstName("Ren");
		newUserInvitationDTO.setLastName("Hoek");
		newUserInvitationDTO.setEmail("ren@somecompanythatdoesntexist.com");
		newUserInvitationDTO.setInviterUserId(employeeId);

		group = newCompanyUserGroup(companyId);
		setUpIsDone = true;
	}

	@Test
	@Transactional
	public void inviteUserToGroup_InviteSingleUser_RequestPaginationContainsUser() throws Exception {

		requestService.inviteUserToGroup(employeeId, contractorId, group.getId());
		UserPagination p = groupService.findAllInvitedUsers(group.getId(), new UserPagination(true));

		assertEquals(1, p.getRowCount().intValue());
		assertEquals(contractorId, first(p.getResults()).getId());
	}

	@Test
	@Transactional
	public void inviteUserToGroup_InviteSingleUser_LatestInvitationContainsUser() throws Exception {

		requestService.inviteUserToGroup(employeeId, contractorId, group.getId());
		final UserGroupInvitation invitation = requestService.findLatestUserGroupInvitationRequestByInvitedUserAndUserGroup(contractorId, group.getId());

		assertEquals(group.getId(), invitation.getUserGroup().getId());
		assertEquals(employeeId, invitation.getRequestor().getId());
	}


	@Test
	@Transactional
	public void inviteUserToGroup_InviteMultipleUsers_RequestExistsForAll() throws Exception {

		requestService.inviteUsersToGroup(employeeId,
				new long[]{contractorId, contractor2Id},
				group.getId());

		UserPagination p = groupService.findAllInvitedUsers(group.getId(), new UserPagination(true));

		assertEquals(2, p.getRowCount().intValue());
		assertTrue(requestService.userHasInvitationToGroup(contractorId, group.getId()));
		assertTrue(requestService.userHasInvitationToGroup(contractor2Id, group.getId()));
	}

	@Test
	@Transactional
	public void deleteRequest_SingleUserRequest_DeleteRequest_CountIsZero() throws Exception {

		requestService.inviteUserToGroup(employeeId, contractorId, group.getId());

		List<Request> requests = requestService.findRequestsByInvitedUser(contractorId);

		assertEquals(1, requests.size());

		requestService.deleteRequest(last(requests).getId());

		requests = requestService.findRequestsByInvitedUser(contractorId);

		assertTrue(requests.isEmpty());
	}

	@Test
	@Transactional
	public void inviteUser_InviteNewUserToGroup_RequestExists() throws Exception {

		Invitation invitation = registrationService.inviteUser(newUserInvitationDTO, new Long[]{group.getId()});

		List<Request> requests = requestService.findRequestsByInvitation(invitation.getId());

		assertEquals(1, requests.size());
		assertTrue(first(requests) instanceof UserGroupInvitation);
		assertEquals(group.getId(), ((UserGroupInvitation) first(requests)).getUserGroup().getId());
	}

	@Test
	@Transactional
	public void inviteUser_InviteNewUserToMultipleGroups_AllRequestsExist() throws Exception {

		UserGroup group2 = newCompanyUserGroup(companyId);
		Invitation invitation = registrationService.inviteUser(newUserInvitationDTO, new Long[]{group.getId(),group2.getId()});

		List<Request> requests = requestService.findRequestsByInvitation(invitation.getId());

		assertEquals(2, requests.size());
		Request request1 = first(requests);
		Request request2 = requests.get(1);

		assertTrue(request1 instanceof UserGroupInvitation);
		assertTrue(request2 instanceof UserGroupInvitation);
		assertEquals(group.getId(), ((UserGroupInvitation) request1).getUserGroup().getId());
		assertEquals(group2.getId(), ((UserGroupInvitation) request2).getUserGroup().getId());
	}
}
