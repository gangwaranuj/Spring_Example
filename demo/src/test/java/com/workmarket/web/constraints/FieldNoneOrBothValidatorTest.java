package com.workmarket.web.constraints;

import org.hibernate.validator.engine.ConstraintValidatorContextImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by nick on 9/27/12 11:54 AM
 */
@RunWith(MockitoJUnitRunner.class)
public class FieldNoneOrBothValidatorTest {

	@Mock ConstraintValidatorContextImpl context;
	@InjectMocks FieldNoneOrBothValidator validator;

	public class FieldTest {
		private String firstField;
		private String secondField;

		public FieldTest(String firstField, String secondField) {
			this.firstField = firstField;
			this.secondField = secondField;
		}

		public String getFirstField() {
			return firstField;
		}

		public String getSecondField() {
			return secondField;
		}
	}

	@Test
	public void fieldNoneOrBothValidatorTest() {
		@FieldNoneOrBoth(first = "firstField", second = "secondField") final class testClass1 {};
		FieldNoneOrBoth annot = testClass1.class.getAnnotation(FieldNoneOrBoth.class);

		validator = new FieldNoneOrBothValidator();
		validator.initialize(annot);

		assertTrue(validator.isValid(new FieldTest("notnull", "notnullllll"), context));
		assertTrue(validator.isValid(new FieldTest(null, null), context));

		assertFalse(validator.isValid(new FieldTest("notnull", null), context));
		assertFalse(validator.isValid(new FieldTest(null, "notnull"), context));

	}
}
