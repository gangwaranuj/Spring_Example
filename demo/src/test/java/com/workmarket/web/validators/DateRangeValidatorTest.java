package com.workmarket.web.validators;

import com.workmarket.domains.model.DateRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.Validator;
import java.util.Calendar;

/**
 * User: iloveopt
 * Date: 11/14/14
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class DateRangeValidatorTest extends BaseValidatorTest {

	private DateRangeValidator validator = new DateRangeValidator();
	private DateRange dateRange;
	Calendar now;
	Calendar yesterday;
	Calendar tomorrow;

	@Before
	public void setup() {
		now = Calendar.getInstance();

		yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);

		tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_YEAR, 1);
	}

	@Test
	public void validate_fromIsNull_fail() {
		dateRange = new DateRange();
		Assert.assertTrue(hasErrorCode(validate(dateRange), "NotNull"));
	}

	@Test
	public void validate_fromInThePast_fail() {
		dateRange = new DateRange(yesterday);
		Assert.assertTrue(hasErrorCode(validate(dateRange), "inpast"));
	}

	@Test
	public void validate_fromAfterThroughDate_fail() {
		dateRange = new DateRange(tomorrow, now);
		Assert.assertTrue(hasErrorCode(validate(dateRange), "invalid"));
	}

	@Test
	public void validate_throughInThePast_fail() {
		dateRange = new DateRange(tomorrow, yesterday);
		Assert.assertTrue(hasErrorCode(validate(dateRange), "invalid"));
	}

	protected Validator getValidator() {
		return validator;
	}

}
