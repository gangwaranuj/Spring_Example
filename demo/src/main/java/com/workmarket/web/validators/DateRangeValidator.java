package com.workmarket.web.validators;

import com.workmarket.domains.model.DateRange;
import com.workmarket.utility.DateUtilities;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("dateRangeValidator")
public class DateRangeValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return DateRange.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		DateRange dateRange = (DateRange)target;

		if (dateRange.getFrom() == null) {
			errors.rejectValue("from", "NotNull", new Object[] {"From date"}, "");
		} else if (DateUtilities.isInPast(dateRange.getFrom())) {
			errors.rejectValue("from", "inpast", new Object[] {"From date"}, "");
		} else if (dateRange.isRange()) {
			if (dateRange.getFrom().after(dateRange.getThrough())) {
				errors.rejectValue("from", "invalid", new Object[] {"From date"}, "");
			}

			if (DateUtilities.isInPast(dateRange.getThrough())) {
				errors.rejectValue("through", "invalid", new Object[] {"To date"}, "");
			}
		}
	}
}
