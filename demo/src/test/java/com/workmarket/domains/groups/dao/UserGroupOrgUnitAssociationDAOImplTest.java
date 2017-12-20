package com.workmarket.domains.groups.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroupOrgUnitAssociation;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserGroupOrgUnitAssociationDAOImplTest extends BaseServiceIT {

	@Autowired UserGroupOrgUnitAssociationDAO userGroupOrgUnitAssociationDAO;

	private final String
			USER_GROUP_UUID = "userGroupUUID",
			ORG_UNIT_ONE_UUID = "orgUnitOneUUID",
			ORG_UNIT_TWO_UUID = "orgUnitTwoUUID";
	private final List<String>
			ONE_ORG_UNIT = ImmutableList.of(ORG_UNIT_ONE_UUID),
			TWO_ORG_UNITS = ImmutableList.of(ORG_UNIT_ONE_UUID, ORG_UNIT_TWO_UUID);

	@Test
	@Transactional
	public void setUserGroupOrgUnitAssociation_setWithTwoOrgUnits_twoAssociationsCreated() {
		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, TWO_ORG_UNITS);

		final List<UserGroupOrgUnitAssociation> userGroupOrgUnitAssociations =
				userGroupOrgUnitAssociationDAO.findOrgUnitAssociationsByGroupId(USER_GROUP_UUID);

		assertEquals(TWO_ORG_UNITS.size(), userGroupOrgUnitAssociations.size());
		assertEquals(USER_GROUP_UUID, userGroupOrgUnitAssociations.get(0).getUserGroupUuid());
		assertEquals(ORG_UNIT_ONE_UUID, userGroupOrgUnitAssociations.get(0).getOrgUnitUuid());
		assertEquals(USER_GROUP_UUID, userGroupOrgUnitAssociations.get(1).getUserGroupUuid());
		assertEquals(ORG_UNIT_TWO_UUID, userGroupOrgUnitAssociations.get(1).getOrgUnitUuid());
	}

	@Test
	@Transactional
	public void setUserGroupOrgUnitAssociation_setOneOrgUnitAfterTwoOrgsWereSet_lastSetWins() {
		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, TWO_ORG_UNITS);
		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, ONE_ORG_UNIT);

		final List<UserGroupOrgUnitAssociation> userGroupOrgUnitAssociations =
				userGroupOrgUnitAssociationDAO.findOrgUnitAssociationsByGroupId(USER_GROUP_UUID);

		assertEquals(ONE_ORG_UNIT.size(), userGroupOrgUnitAssociations.size());
		assertEquals(USER_GROUP_UUID, userGroupOrgUnitAssociations.get(0).getUserGroupUuid());
		assertEquals(ONE_ORG_UNIT.get(0), userGroupOrgUnitAssociations.get(0).getOrgUnitUuid());
	}

	@Test
	@Transactional
	public void setUserGroupOrgUnitAssociation_setNullOrgUnitsAfterOneOrgWasSet_allAssociationsDeleted() {
		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, ONE_ORG_UNIT);
		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, null);

		final List<UserGroupOrgUnitAssociation> userGroupOrgUnitAssociations =
				userGroupOrgUnitAssociationDAO.findOrgUnitAssociationsByGroupId(USER_GROUP_UUID);

		assertEquals(0, userGroupOrgUnitAssociations.size());
	}

	@Test
	@Transactional
	public void setUserGroupOrgUnitAssociation_setEmptyOrgUnitsAfterOneOrgWasSet_allAssociationsDeleted() {
		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, ONE_ORG_UNIT);
		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, Lists.<String>newArrayList());

		final List<UserGroupOrgUnitAssociation> userGroupOrgUnitAssociations =
				userGroupOrgUnitAssociationDAO.findOrgUnitAssociationsByGroupId(USER_GROUP_UUID);

		assertEquals(0, userGroupOrgUnitAssociations.size());
	}

	@Test
	@Transactional
	public void findAllOrgUnitAssociationsByGroupId_onlyDeletedAssociationsExist_deletedAssociationsFound() {
		List<UserGroupOrgUnitAssociation> userGroupOrgUnitAssociations =
				userGroupOrgUnitAssociationDAO.findAllOrgUnitAssociationsByGroupId(USER_GROUP_UUID);

		assertEquals(0, userGroupOrgUnitAssociations.size());

		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, TWO_ORG_UNITS);
		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, Lists.<String>newArrayList());

		userGroupOrgUnitAssociations =
				userGroupOrgUnitAssociationDAO.findAllOrgUnitAssociationsByGroupId(USER_GROUP_UUID);

		assertEquals(TWO_ORG_UNITS.size(), userGroupOrgUnitAssociations.size());
		assertEquals(true, userGroupOrgUnitAssociations.get(0).getDeleted());
		assertEquals(true, userGroupOrgUnitAssociations.get(1).getDeleted());
	}

	@Test
	@Transactional
	public void findAllOrgUnitAssociationsByGroupId_oneDeletedAndOneActiveAssociationExist_bothAssociationsFound() {
		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, TWO_ORG_UNITS);
		userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(USER_GROUP_UUID, ONE_ORG_UNIT);

		final List<UserGroupOrgUnitAssociation> userGroupOrgUnitAssociations =
				userGroupOrgUnitAssociationDAO.findAllOrgUnitAssociationsByGroupId(USER_GROUP_UUID);

		assertEquals(TWO_ORG_UNITS.size(), userGroupOrgUnitAssociations.size());
		assertEquals(ORG_UNIT_ONE_UUID, userGroupOrgUnitAssociations.get(0).getOrgUnitUuid());
		assertEquals(false, userGroupOrgUnitAssociations.get(0).getDeleted());
		assertEquals(ORG_UNIT_TWO_UUID, userGroupOrgUnitAssociations.get(1).getOrgUnitUuid());
		assertEquals(true, userGroupOrgUnitAssociations.get(1).getDeleted());
	}
}
