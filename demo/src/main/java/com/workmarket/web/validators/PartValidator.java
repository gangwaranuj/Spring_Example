package com.workmarket.web.validators;

import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

@Component
public class PartValidator implements Validator {

	@Autowired private MessageBundleHelper messageHelper;

	private static final BigDecimal PART_VALUE_MAX = PartDTO.PART_VALUE_MAX;
	private static final BigDecimal PART_VALUE_MIN = PartDTO.PART_VALUE_MIN;
	private static final int PART_NAME_MAX = PartDTO.NAME_MAX;
	private static final int PART_TRACKING_NUMBER_MAX = PartDTO.TRACKING_NUMBER_MAX;

	@Override
	public boolean supports(Class<?> aClass) {
		return PartDTO.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		PartDTO part = (PartDTO)o;

		final String name = part.getName();
		if (StringUtils.isBlank(name)) {
			errors.rejectValue("name", "NotNull", messageHelper.getMessage("partsAndLogistics.name.required"));
		} else if (PART_NAME_MAX < name.length()) {
			errors.rejectValue(
				"name", "Max",
				new Object[]{ PART_NAME_MAX },
				messageHelper.getMessage("partsAndLogistics.name.Max", PART_NAME_MAX)
			);
		}

		final String trackingNumber = part.getTrackingNumber();
		if (StringUtils.isBlank(trackingNumber)) {
			errors.rejectValue("trackingNumber", "NotNull", messageHelper.getMessage("partsAndLogistics.trackingNumber.required"));
		} else if (PART_TRACKING_NUMBER_MAX < trackingNumber.length()) {
			errors.rejectValue(
				"trackingNumber", "Max",
				new Object[]{ PART_TRACKING_NUMBER_MAX },
				messageHelper.getMessage("partsAndLogistics.trackingNumber.Max", PART_TRACKING_NUMBER_MAX)
			);
		}

		final BigDecimal partValue = part.getPartValue();
		if (partValue != null && (PART_VALUE_MAX.compareTo(partValue) < 0 || PART_VALUE_MIN.compareTo(partValue) > 0)) {
			errors.rejectValue(
				"partValue", "OutOfRange",
				new Object[]{ PART_VALUE_MIN, PART_VALUE_MAX },
				messageHelper.getMessage("partsAndLogistics.partValue.outOfRange", PART_VALUE_MIN, PART_VALUE_MAX)
			);
		}
	}
}
