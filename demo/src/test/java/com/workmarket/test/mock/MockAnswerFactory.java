package com.workmarket.test.mock;

import org.mockito.stubbing.Answer;

import com.workmarket.domains.model.validation.ConstraintViolation;

public interface MockAnswerFactory {

	
	Answer<ConstraintViolation> answerWith(ConstraintViolation cv);

}
