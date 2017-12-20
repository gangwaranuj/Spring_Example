package com.workmarket.web.validators;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.comment.UserComment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserCommentValidatorTest extends BaseValidatorTest {
	@InjectMocks UserCommentValidator validator;
	@Mock UserComment comment;
	@Mock User user;

	@Before
	public void setup() {
		when(comment.getComment()).thenReturn(ANY_STRING);
		when(comment.getUser()).thenReturn(user);
	}

	@Test
	public void itShouldValidateTrueForNonEmptyComment() {
		assertFalse(validate(comment).hasErrors());
	}

	@Test
	public void itShouldValidateFalseForNullUser() {
		when(comment.getUser()).thenReturn(null);
		assertTrue(validate(comment).hasErrors());
	}

	@Test
	public void itShouldValidateFalseForNullComment() {
		when(comment.getComment()).thenReturn(null);
		assertTrue(validate(comment).hasErrors());
	}

	@Test
	public void itShouldValidateFalseForEmptyComment() {
		when(comment.getComment()).thenReturn(EMPTY_TOKEN);
		assertTrue(validate(comment).hasErrors());
	}

	protected Validator getValidator() {
		return validator;
	}
}