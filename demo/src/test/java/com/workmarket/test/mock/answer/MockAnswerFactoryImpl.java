package com.workmarket.test.mock.answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.test.mock.answer.defaults.DefaultExtendedUserDetailsAnswer;
import com.workmarket.test.mock.answer.defaults.DefaultStringAnswer;

public class MockAnswerFactoryImpl  {
	
	public Answer<String> defaultStringAnswer(String toAnswerWith) {
		return new DefaultStringAnswer(toAnswerWith);
	}
	
	public Answer<ExtendedUserDetails> defaultExtendedUserDetailsAnswer(String ... grantedAuthorities) {
		return new DefaultExtendedUserDetailsAnswer(Arrays.asList(grantedAuthorities));
	}

	public Answer<ConstraintViolation> answerWithConstraintViolation(Map<String, Object> params) {
		final ConstraintViolation cv = new ConstraintViolation("key", params.get("key"));
		return new Answer<ConstraintViolation>() {
			@Override
			public ConstraintViolation answer(InvocationOnMock invocation) throws Throwable {
				return cv;
			}
		};
	}
	
	public Answer<List<ConstraintViolation>> answerWithListOfConstraintViolations(Map<String, Object> params) {
		final ConstraintViolation cv = new ConstraintViolation("key", params.get("key"));
		return new Answer<List<ConstraintViolation>>() {
			@Override
			public List<ConstraintViolation> answer(InvocationOnMock invocation) throws Throwable {
				List<ConstraintViolation> cvList = new ArrayList<ConstraintViolation>();
				cvList.add(cv);
				return cvList;
			}
		};
	}
	
}
