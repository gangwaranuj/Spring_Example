package com.workmarket.dao.assessment;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.service.UserGroupRequirementSetService;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentConfiguration;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.RequestService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/*
Rules:

1) Is the assessment private? If not, then the user can take it.
2) If private, is it a requirement for any truly public groups? If so, then the user can take it.
3) Is it a requirement for any invite only groups? If so, then is the user invited to any of the invite only groups for which it is a requirement? If so, then the user can take it.
4) If we are here, then the user can't take it.
*/
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class AssessmentGroupAssociationDAOIT extends BaseServiceIT {
	private enum Status {
		OPEN_MEMBERSHIP(true),
		NOT_OPEN_MEMBERSHIP(false),
		SEARCHABLE(true),
		NOT_SEARCHABLE(false),
		ACTIVE(true),
		NOT_ACTIVE(false);

		private final boolean status;

		private Status(boolean status) {
			this.status = status;
		}

		public boolean getStatus() {
			return status;
		}
	}

	@Autowired AssessmentGroupAssociationDAO assessmentGroupAssociationDAO;
	@Autowired RequestService requestService;
	@Autowired UserGroupRequirementSetService userGroupRequirementSetService;

	private AbstractAssessment assessment;

	@Before
	public void setup() throws Exception {
		// private assessment
		assessment = newAssessment();
		AssessmentConfiguration assessmentConfiguration = new AssessmentConfiguration();
		assessmentConfiguration.setFeatured(false);
		assessment.setConfiguration(assessmentConfiguration);
	}

	@Test
	public void addAssessmentToAllGroupTypes_OnlyAddedToActiveGroup() throws Exception {
		UserGroup openSearchableUserGroup = getUserGroup(Status.SEARCHABLE, Status.ACTIVE);
		userGroupRequirementSetService.addTestRequirement(openSearchableUserGroup.getId(), assessment.getId());

		UserGroup openNotSearchableUserGroup = getUserGroup(Status.NOT_SEARCHABLE, Status.ACTIVE);
		userGroupRequirementSetService.addTestRequirement(openNotSearchableUserGroup.getId(), assessment.getId());

		UserGroup openSearchableNotActiveUserGroup = getUserGroup(Status.SEARCHABLE, Status.NOT_ACTIVE);
		userGroupRequirementSetService.addTestRequirement(openSearchableNotActiveUserGroup.getId(), assessment.getId());

		List<UserGroupDTO> results = assessmentGroupAssociationDAO.findGroupAssociationsByAssessmentId(assessment.getId());
		assertNotNull(results);
		assertEquals(results.size(), 2);
	}

	// This exercises rule 2 above
	@Test
	@Transactional
	public void addAssessmentToSearchableAndNonSearchableGroups_OnlySearchableGroupsAllowedToTake() throws Exception {
		User contractor = newContractor();

		UserGroup ug = getUserGroup(Status.NOT_SEARCHABLE, Status.ACTIVE);
		userGroupRequirementSetService.addTestRequirement(ug.getId(), assessment.getId());

		assertFalse(
			assessmentGroupAssociationDAO.isUserAllowedToTakeAssessment(assessment.getId(), contractor.getId()
		));

		ug = getUserGroup(Status.SEARCHABLE, Status.ACTIVE);
		userGroupRequirementSetService.addTestRequirement(ug.getId(), assessment.getId());

		assertTrue(
			assessmentGroupAssociationDAO.isUserAllowedToTakeAssessment(assessment.getId(), contractor.getId()
		));
	}

	// This exercises rule 3 above
	@Test
	@Transactional
	public void addAssessmentToNotSearchableGroupAndInviteUser_AllowedToTakeAssessment() throws Exception {
		User contractor = newContractor();

		UserGroup ug = getUserGroup(Status.NOT_SEARCHABLE, Status.ACTIVE);
		userGroupRequirementSetService.addTestRequirement(ug.getId(), assessment.getId());

		requestService.inviteUserToGroup(ug.getOwner().getId(), contractor.getId(), ug.getId());

		assertTrue(
			assessmentGroupAssociationDAO.isUserAllowedToTakeAssessment(assessment.getId(), contractor.getId()
		));
	}


	private UserGroup getUserGroup(Status searchable, Status active) {
		UserGroupDTO dto = new UserGroupDTO();
		dto.setCompanyId(1L);
		dto.setOwnerId(1L);
		dto.setOpenMembership(Status.OPEN_MEMBERSHIP.getStatus());
		dto.setSearchable(searchable.getStatus());
		dto.setActiveFlag(active.getStatus());
		String info =
			"group -" + RandomUtilities.generateAlphaNumericString(5) +
				"- open membership: " + Status.OPEN_MEMBERSHIP +
				", searchable: " + searchable + ", active: " + active;
		dto.setName(info);
		dto.setDescription(info);
		return userGroupService.saveOrUpdateCompanyUserGroup(dto);
	}

}
