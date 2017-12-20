package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * User: micah
 * Date: 1/28/14
 * Time: 1:36 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class UserUserGroupAssociationTest {
	UserUserGroupAssociation userUserGroupAssociation1;
	UserUserGroupAssociation userUserGroupAssociation2;

	UserGroup userGroup1;
	UserGroup userGroup2;

	User user1;
	User user2;

	@Before
	public void setup() {
		userGroup1 = new UserGroup();
		userGroup2 = new UserGroup();

		user1 = new User();
		user2 = new User();

		userGroup1.setId(1000L);
		userGroup2.setId(1000L);

		user1.setId(2000L);
		user2.setId(2000L);

		userUserGroupAssociation1 = new UserUserGroupAssociation();
		userUserGroupAssociation2 = new UserUserGroupAssociation();

		userUserGroupAssociation1.setUser(user1);
		userUserGroupAssociation1.setUserGroup(userGroup1);
		userUserGroupAssociation2.setUser(user2);
		userUserGroupAssociation2.setUserGroup(userGroup2);
	}

	@Test
	public void equals_allNulls() {

		userGroup1.setId(null);
		userGroup2.setId(null);
		user1.setId(null);
		user2.setId(null);

		assertEquals(true, userUserGroupAssociation1.equals(userUserGroupAssociation2));

		userUserGroupAssociation1.setUser(null);
		userUserGroupAssociation1.setUserGroup(null);
		userUserGroupAssociation2.setUser(null);
		userUserGroupAssociation2.setUserGroup(null);

		assertEquals(true, userUserGroupAssociation1.equals(userUserGroupAssociation2));
	}

	@Test
	public void equals_mixedNulls() {
		userUserGroupAssociation1.setUser(null);
		userUserGroupAssociation1.setUserGroup(null);

		assertEquals(false, userUserGroupAssociation1.equals(userUserGroupAssociation2));
	}

	@Test
	public void equals_Reflexive() {
		assertEquals(true, userUserGroupAssociation1.equals(userUserGroupAssociation1));
	}

	@Test
	public void equals_Symmetric() {
 		assertEquals(true, userUserGroupAssociation1.equals(userUserGroupAssociation2));
		assertEquals(true, userUserGroupAssociation2.equals(userUserGroupAssociation1));
	}

	@Test
	public void hashCode_equals() {
		assertEquals(userUserGroupAssociation1.hashCode(), userUserGroupAssociation2.hashCode());
	}

	@Test
	public void equals_False() {
		user1.setId(3000L);
		assertEquals(false, userUserGroupAssociation1.equals(userUserGroupAssociation2));
	}
}
