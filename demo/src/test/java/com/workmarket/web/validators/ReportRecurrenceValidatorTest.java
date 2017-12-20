package com.workmarket.web.validators;

import com.google.common.collect.Sets;
import com.workmarket.service.business.dto.ReportRecurrenceDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by nick on 8/8/12 4:36 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ReportRecurrenceValidatorTest extends BaseValidatorTest {

	private ReportRecurrenceValidator validator = new ReportRecurrenceValidator();

	@Test
	public void testValidateDaily() throws Exception {
		ReportRecurrenceDTO dto = getDefaultRecurrence();
		dto.setRecurrenceType(ReportRecurrenceDTO.MONTHLY);
		dto.setDailyWeekdaysOnlyFlag(true);
		assertFalse(validate(dto).hasErrors());
	}

	@Test
	public void testValidateWeekly() throws Exception {
		ReportRecurrenceDTO dto = getDefaultRecurrence();

		// null days list
		dto.setRecurrenceType(ReportRecurrenceDTO.WEEKLY);
		dto.setWeeklyDays(null);
		assertTrue(validate(dto).hasErrors());

		// empty weekdays
		dto.setWeeklyDays(Sets.<Integer>newHashSet());
		assertTrue(validate(dto).hasErrors());

		// invalid weekdays
		dto.setWeeklyDays(Sets.newHashSet(1, 2, 3, 4, 5, 6, 7, 8));
		assertTrue(validate(dto).hasErrors());

		// positive case
		dto.setWeeklyDays(Sets.newHashSet(1, 2, 3, 7));
		assertFalse(validate(dto).hasErrors());
	}

	@Test
	public void testValidateMonthly() throws Exception {
		ReportRecurrenceDTO dto = getDefaultRecurrence();
		dto.setRecurrenceType(ReportRecurrenceDTO.MONTHLY);

		// specific day of month tests *********************************************************************************
		dto.setMonthlyUseDayOfMonthFlag(true);

		// no frequency day set
		dto.setMonthlyFrequencyDay(null);
		assertTrue(validate(dto).hasErrors());

		// invalid day of month
		dto.setMonthlyUseDayOfMonthFlag(true);
		dto.setMonthlyFrequencyDay(500);
		assertTrue(validate(dto).hasErrors());

		// positive case
		dto.setMonthlyFrequencyDay(3);
		assertFalse(validate(dto).hasErrors());

		// ordinal style tests *****************************************************************************************
		dto.setMonthlyUseDayOfMonthFlag(false);

		// invalid weekday set
		dto.setMonthlyFrequencyWeekday(8);
		assertTrue(validate(dto).hasErrors());

		// invalid ordinal set
		dto.setMonthlyFrequencyWeekdayOrdinal(0);
		assertTrue(validate(dto).hasErrors());

		// invalid weekday
		dto.setMonthlyFrequencyWeekdayOrdinal(3);
		dto.setMonthlyFrequencyWeekday(9);
		assertTrue(validate(dto).hasErrors());

		// invalid ordinal
		dto.setMonthlyFrequencyWeekday(9);
		dto.setMonthlyFrequencyWeekdayOrdinal(20);
		assertTrue(validate(dto).hasErrors());

		// positive case
		dto.setMonthlyFrequencyWeekday(4);
		dto.setMonthlyFrequencyWeekdayOrdinal(2);
		assertFalse(validate(dto).hasErrors());
	}

	@Test
	public void testValidateRecipients() {
		ReportRecurrenceDTO dto = getDefaultRecurrence();

		// null case
		dto.setRecipients(null);
		assertTrue(validate(dto).hasErrors());

		// empty case
		dto.setRecipients(Sets.<String>newHashSet());
		assertTrue(validate(dto).hasErrors());

		// invalid case
		dto.setRecipients(Sets.newHashSet("dumbass_email_person"));
		assertTrue(validate(dto).hasErrors());

		// invalid case 2
		dto.setRecipients(Sets.newHashSet("@@@"));
		assertTrue(validate(dto).hasErrors());

		// positive case
		dto.setRecipients(Sets.newHashSet("nick@workmarket.com", "q.a+test@workmarket.com", "william@microsoft.co.uk"));
		assertFalse(validate(dto).hasErrors());
	}

	@Test
	public void testValidateTimeOfDay() {
		ReportRecurrenceDTO dto = getDefaultRecurrence();

		// null case
		dto.setTimeMorningFlag(null);
		assertTrue(validate(dto).hasErrors());

		// positive case
		dto.setTimeMorningFlag(true);
		assertFalse(validate(dto).hasErrors());
	}

	@Test
	public void testValidateGeneral() throws Exception {
		ReportRecurrenceDTO dto = getDefaultRecurrence();

		// invalid recurrence type
		dto.setRecurrenceType("pancakes");
		assertTrue(validate(dto).hasErrors());

		// totally wrong test
		dto.setMonthlyFrequencyDay(null);
		dto.setMonthlyFrequencyWeekday(null);
		dto.setMonthlyFrequencyWeekdayOrdinal(null);
		dto.setMonthlyUseDayOfMonthFlag(null);
		dto.setDailyWeekdaysOnlyFlag(null);
		dto.setWeeklyDays(null);
		assertTrue(validate(dto).hasErrors());

		// defaults test
		assertFalse(validate(getDefaultRecurrence()).hasErrors());
	}

	private ReportRecurrenceDTO getDefaultRecurrence() {
		ReportRecurrenceDTO dto = new ReportRecurrenceDTO();
		dto.getRecipients().add("jwald@workmarket.com");
		return dto;
	}

	protected Validator getValidator() {
		return validator;
	}
}
