package com.workmarket.reporting.format;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.Assert;
import com.workmarket.domains.model.reporting.GenericField;

public class BooleanFormat extends Format {

	private static final long serialVersionUID = 7850329438749984187L;

	public String format(GenericField genericField){
		try {
			Assert.notNull(genericField, "genericField can't be null");
			Boolean value = BooleanUtils.toBoolean(NumberUtils.toInt(genericField.getValue().toString()));
			return BooleanUtils.toStringYesNo(value);
		} catch(Exception e) {
			return BooleanUtils.toStringYesNo(false);
		}
	}
}