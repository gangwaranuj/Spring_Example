package com.workmarket.web.editors;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.NumberUtils;

import java.beans.PropertyEditorSupport;

public class NullSafeNumberEditor  extends PropertyEditorSupport {
	private final Class<? extends Number> numberClass;
	private final boolean allowZero;

	public NullSafeNumberEditor(Class<? extends Number> numberClass) {
		this.numberClass = numberClass;
		this.allowZero = true;
	}

	public NullSafeNumberEditor(Class<? extends Number> numberClass, boolean allowZero) {
		this.numberClass = numberClass;
		this.allowZero = allowZero;
	}

	@Override
	public void setAsText(String s) throws IllegalArgumentException {
		if (StringUtils.isBlank(s))
			return;
		Number value = NumberUtils.parseNumber(s, numberClass);
		if (!allowZero && isZero(value))
			return;
		setValue(value);
	}

	@Override
	public String getAsText() {
		Object o = getValue();
		if (o == null)
			return "";
		Number value = NumberUtils.convertNumberToTargetClass((Number)o, numberClass);
		if (!allowZero && isZero(value))
			return "";
		return value.toString();
	}

	private boolean isZero(Number value) {
		return NumberUtils.convertNumberToTargetClass(0, numberClass).equals(value);
	}
}
