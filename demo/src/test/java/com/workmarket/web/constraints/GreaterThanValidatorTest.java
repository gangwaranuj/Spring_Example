package com.workmarket.web.constraints;

import org.hibernate.validator.engine.ConstraintValidatorContextImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GreaterThanValidatorTest {
	@Mock ConstraintValidatorContextImpl context;
	@InjectMocks GreaterThanValidator validator;

	GreaterThan longAnnotation;
	GreaterThan bigDecimalAnnotation;
	GreaterThan bigIntegerAnnotation;

	@Before
	public void setUp() throws Exception {
		longAnnotation = LongFieldMock.class.getDeclaredField("value").getAnnotation(GreaterThan.class);
		bigIntegerAnnotation = BigIntegerFieldMock.class.getDeclaredField("value").getAnnotation(GreaterThan.class);
		bigDecimalAnnotation = BigDecimalFieldMock.class.getDeclaredField("value").getAnnotation(GreaterThan.class);

		validator = new GreaterThanValidator();
	}

	@Test
	public void isValid_WhenItsANullLong_IsTrue() throws Exception {
		LongFieldMock mock = new LongFieldMock(null);
		validator.initialize(longAnnotation);
		assertTrue(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsAHigherLong_IsTrue() throws Exception {
		LongFieldMock mock = new LongFieldMock(5L);
		validator.initialize(longAnnotation);
		assertTrue(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsAnEqualLong_IsFalse() throws Exception {
		LongFieldMock mock = new LongFieldMock(4L);
		validator.initialize(longAnnotation);
		assertFalse(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsALowerLong_IsFalse() throws Exception {
		LongFieldMock mock = new LongFieldMock(3L);
		validator.initialize(longAnnotation);
		assertFalse(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsANullBigDecimal_IsTrue() throws Exception {
		BigDecimalFieldMock mock = new BigDecimalFieldMock(null);
		validator.initialize(bigDecimalAnnotation);
		assertTrue(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsAHigherBigDecimal_IsTrue() throws Exception {
		BigDecimalFieldMock mock = new BigDecimalFieldMock(new BigDecimal("5"));
		validator.initialize(bigDecimalAnnotation);
		assertTrue(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsAnEqualBigDecimal_IsFalse() throws Exception {
		BigDecimalFieldMock mock = new BigDecimalFieldMock(new BigDecimal("4"));
		validator.initialize(bigDecimalAnnotation);
		assertFalse(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsALowerBigDecimal_IsFalse() throws Exception {
		BigDecimalFieldMock mock = new BigDecimalFieldMock(new BigDecimal("3"));
		validator.initialize(bigDecimalAnnotation);
		assertFalse(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsANullBigInteger_IsTrue() throws Exception {
		BigIntegerFieldMock mock = new BigIntegerFieldMock(null);
		validator.initialize(bigIntegerAnnotation);
		assertTrue(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsAHigherBigInteger_IsTrue() throws Exception {
		BigIntegerFieldMock mock = new BigIntegerFieldMock(new BigInteger("5"));
		validator.initialize(bigIntegerAnnotation);
		assertTrue(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsAnEqualBigInteger_IsFalse() throws Exception {
		BigIntegerFieldMock mock = new BigIntegerFieldMock(new BigInteger("4"));
		validator.initialize(bigIntegerAnnotation);
		assertFalse(validator.isValid(mock.value, context));
	}

	@Test
	public void isValid_WhenItsALowerBigInteger_IsFalse() throws Exception {
		BigIntegerFieldMock mock = new BigIntegerFieldMock(new BigInteger("3"));
		validator.initialize(bigIntegerAnnotation);
		assertFalse(validator.isValid(mock.value, context));
	}

	// Stub classes for tests
	public class LongFieldMock {
		@GreaterThan(4L) private final Long value;

		public LongFieldMock(Long value) {
			this.value = value;
		}
	}

	public class BigIntegerFieldMock {
		@GreaterThan(4L) private final BigInteger value;

		public BigIntegerFieldMock(BigInteger value) {
			this.value = value;
		}
	}

	public class BigDecimalFieldMock {
		@GreaterThan(4L) private final BigDecimal value;

		public BigDecimalFieldMock(BigDecimal value) {
			this.value = value;
		}
	}
}
