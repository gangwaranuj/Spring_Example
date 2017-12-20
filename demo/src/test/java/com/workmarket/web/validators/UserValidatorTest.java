package com.workmarket.web.validators;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.comment.UserComment;
import com.workmarket.configuration.Constants;
import com.workmarket.thrift.core.Company;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class UserValidatorTest {
	private User user;

	private UserValidator validator = new UserValidator();

	private BindingResult validate(Object o) {
		BindingResult binding = new BeanPropertyBindingResult(o, "user");
		validator.validate(o, binding);
		return binding;
	}

	private Errors getErrors(User user) {
		MapBindingResult errors = new MapBindingResult(Maps.newHashMap(), "feedback");
		validator.validate(user, errors);
		return errors;
	}

	private boolean hasErrorCode(Errors errors, final String errorCode) {
		return Iterables.tryFind(errors.getAllErrors(), new Predicate<ObjectError>() {
			@Override
			public boolean apply(ObjectError objectError) {
				return Arrays.asList(objectError.getCodes()).contains(errorCode);
			}
		}).isPresent();
	}

	@Before
	public void setUp() {
		user = mock(User.class);

		when(user.getId()).thenReturn(1L);
		when(user.getFirstName()).thenReturn("Walds");
		when(user.getLastName()).thenReturn("Cousin");
		when(user.getFirstName()).thenReturn("communityservice@wald.com");
		when(user.getStartDate()).thenReturn(Calendar.getInstance());
	}

	@Test
	public void testBadUser() {
		User badUser = new User();
		badUser.setId(null);
		badUser.setFirstName("");
		badUser.setLastName("");
		badUser.setEmail("");
		badUser.setStartDate(null);

		assertTrue(validate(badUser).hasErrors());
	}

	@Test
	public void testGoodUser() {
		User goodUser = new User();
		goodUser.setId(1L);
		goodUser.setFirstName("Walds");
		goodUser.setLastName("Cousin");
		goodUser.setEmail("communityservice@wald.com");
		goodUser.setStartDate(Calendar.getInstance());

		assertFalse(validate(goodUser).hasErrors());
	}

	@Test
	public void validate_InvalidFirst_fail() {
		when(user.getFirstName()).thenReturn(null);
		assertTrue(hasErrorCode(getErrors(user), "user.validation.firstNameRequired"));
	}

	@Test
	public void validate_tooLongFirstname_fail() {
		when(user.getFirstName()).thenReturn(RandomStringUtils.randomAlphanumeric(Constants.FIRST_NAME_MAX_LENGTH + 1));
		assertTrue(hasErrorCode(getErrors(user), "user.validation.firstNameMaxLength"));
	}

	@Test
	public void validate_InvalidLastname_fail() {
		when(user.getLastName()).thenReturn(null);
		assertTrue(hasErrorCode(getErrors(user), "user.validation.lastNameRequired"));
	}

	@Test
	public void validate_tooLongLastname_fail() {
		when(user.getLastName()).thenReturn(RandomStringUtils.randomAlphanumeric(Constants.LAST_NAME_MAX_LENGTH + 1));
		assertTrue(hasErrorCode(getErrors(user), "user.validation.lastNameMaxLength"));
	}

	@Test
	public void validate_InvalidEmail_fail() {
		when(user.getEmail()).thenReturn(null);
		assertTrue(hasErrorCode(getErrors(user), "user.validation.emailRequired"));
	}

	@Test
	public void validate_tooLongEmail_fail() {
		when(user.getEmail()).thenReturn(RandomStringUtils.randomAlphanumeric(Constants.EMAIL_MAX_LENGTH + 1));
		assertTrue(hasErrorCode(getErrors(user), "user.validation.emailMaxLength"));
	}

	@Test
	public void validate_ValidSupports_success() {
		assertTrue(validator.supports(User.class));
	}

	@Test
	public void validate_InvalidSupports_fail() {
		assertFalse(validator.supports(ArrayList.class));
		assertFalse(validator.supports(Integer.class));
		assertFalse(validator.supports(Set.class));
		assertFalse(validator.supports(Company.class));
		assertFalse(validator.supports(UserComment.class));
	}

}
