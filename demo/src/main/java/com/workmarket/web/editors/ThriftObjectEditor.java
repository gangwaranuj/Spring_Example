package com.workmarket.web.editors;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Method;

public class ThriftObjectEditor<T> extends PropertyEditorSupport {
	private Class<T> klazz;
	public ThriftObjectEditor(Class<T> klazz) {
		this.klazz = klazz;
	}

	@Override
	public void setAsText(String s) throws IllegalArgumentException {
		try {
			T instance = klazz.newInstance();
			Method setter = BeanUtils.findMethodWithMinimalParameters(klazz, "setId");
			setter.invoke(instance, Long.valueOf(s).longValue());
			setValue(instance);
		} catch (Exception e) {
			super.setAsText(s);
		}
	}

	@Override
	public String getAsText() {
		try {
			T instance = (T)getValue();
			Method getter = BeanUtils.findMethod(klazz, "getId");
			return String.valueOf(getter.invoke(instance));
		} catch (Exception e) {
			return super.getAsText();
		}
	}
}