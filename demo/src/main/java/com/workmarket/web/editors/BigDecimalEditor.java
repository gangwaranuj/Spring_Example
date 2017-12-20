package com.workmarket.web.editors;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

public class BigDecimalEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		BigDecimal value = null;

		if (StringUtils.hasText(text)) {
			try {
				value = new BigDecimal(text);
			} catch (Exception ex) {
				throw new IllegalArgumentException("failed to init BigDecimal from: " + text, ex);
			}
		}

		setValue(value);
	}

	@Override
	public String getAsText() {
		BigDecimal value = (BigDecimal)getValue();
		
		if ((value != null) && (value.doubleValue() != 0D)) {
			return String.valueOf(value);
		}

		return "";
	}
}
