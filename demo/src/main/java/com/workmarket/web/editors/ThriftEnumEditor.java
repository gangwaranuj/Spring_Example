package com.workmarket.web.editors;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Method;

public class ThriftEnumEditor extends PropertyEditorSupport {
	private Class klazz;

	public ThriftEnumEditor(Class klazz) {
		this.klazz = klazz;
	}

	@Override
	public void setAsText(String s) throws IllegalArgumentException {
		try {
			Method method = BeanUtils.findMethodWithMinimalParameters(klazz, "findByValue");
			Object value = method.invoke(null, Integer.valueOf(s));
			setValue(value);
		} catch (Exception e) {
			super.setAsText(s);
		}
	}

	@Override
	public String getAsText() {
		try {
			Method method = BeanUtils.findMethodWithMinimalParameters(klazz ,"getValue");
			return (getValue() == null) ? "" : String.valueOf(method.invoke(getValue()));
		} catch (Exception e) {
			return null;
		}
	}
}
