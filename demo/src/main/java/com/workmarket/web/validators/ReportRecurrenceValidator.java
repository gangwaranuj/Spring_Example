package com.workmarket.web.validators;

import com.workmarket.service.business.dto.ReportRecurrenceDTO;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by nick on 7/31/12 12:07 PM
 */
@Component("reportRecurrenceValidator")
public class ReportRecurrenceValidator implements Validator {
	@Override public boolean supports(Class<?> aClass) {
		return ReportRecurrenceDTO.class.isAssignableFrom(aClass);
	}

	@Override public void validate(Object o, Errors errors) {
		ReportRecurrenceDTO dto = (ReportRecurrenceDTO) o;

		if (dto.getRecurrenceEnabledFlag() == null) {
			errors.rejectValue("recurrenceEnabledFlag", "reports.recurrence_save.exception");
		}

		if (dto.isWeekly()) {
			if (CollectionUtils.isEmpty(dto.getWeeklyDays()))
				errors.rejectValue("weeklyDays", "reports.recurrence_save.weekly.empty");
			else if (dto.getWeeklyDays().size() > 7)
				errors.rejectValue("weeklyDays", "reports.recurrence_save.weekly.invalid");

		} else if (dto.isMonthly()) {
			if (dto.getMonthlyUseDayOfMonthFlag() == null)
				errors.rejectValue("monthlyUseDayOfMonthFlag", "reports.recurrence_save.exception");
			if (dto.getMonthlyUseDayOfMonthFlag()) {
				if (dto.getMonthlyFrequencyDay() == null)
					errors.rejectValue("monthlyFrequencyDay", "reports.recurrence_save.exception");
				else if (!NumberUtilities.isWithinRange(dto.getMonthlyFrequencyDay(), 1, 31)) {
					// allow up to 31 -- on report generation we will consider 28-31 the same depending on the month
					errors.rejectValue("monthlyFrequencyDay", "reports.recurrence_save.exception");
				}
			} else {
				if (!NumberUtilities.isWithinRange(dto.getMonthlyFrequencyWeekday(), 1, 7))
					errors.rejectValue("monthlyFrequencyWeekday", "reports.recurrence_save.exception");
				if (!NumberUtilities.isWithinRange(dto.getMonthlyFrequencyWeekdayOrdinal(), 1, 4))
					errors.rejectValue("monthlyFrequencyWeekdayOrdinal", "reports.recurrence_save.exception");
			}

		} else if (!dto.isDaily()) {
			// invalid option
			errors.rejectValue("recurrenceType", "reports.recurrence_save.exception");
		}

		// validate recipients - must not be null and must be valid email addresses
		if (CollectionUtils.isEmpty(dto.getRecipients())) {
			errors.rejectValue("recipients", "reports.recurrence_save.recipients_empty");
		} else {
			EmailValidator emailValidator = EmailValidator.getInstance();

			for (String recipient : dto.getRecipients()) {
				if (!emailValidator.isValid(recipient)) {
					errors.rejectValue("recipients", "reports.recurrence_save.recipients_invalid");
					break;
				}
			}
		}

		if (dto.getTimeMorningFlag() == null)
			errors.rejectValue("timeMorningFlag", "reports.recurrence_save.recipients_empty");
	}
}
