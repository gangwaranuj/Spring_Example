package com.workmarket.testutils;

import com.workmarket.domains.model.User;

import static org.mockito.Mockito.mock;

/**
 * Created by nick on 6/11/12 3:31 PM
 */
public class UserMocker {

	public static User mockUser(Long id, String firstName, String lastName) {
		User user = mock(User.class);
		user.setId(id);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		return user;
	}
}
