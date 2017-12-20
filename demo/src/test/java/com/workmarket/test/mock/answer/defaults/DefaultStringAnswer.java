package com.workmarket.test.mock.answer.defaults;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class DefaultStringAnswer implements Answer<String> {
	private final String toAnswerWith;
	
	public DefaultStringAnswer(String toAnswerWith) {
		this.toAnswerWith = toAnswerWith;
	}
	
	@Override
	public String answer(InvocationOnMock invocation) throws Throwable {
		return toAnswerWith;
	}

}
