package com.workmarket.web.editors;

import com.workmarket.domains.model.LookupEntity;

import java.beans.PropertyEditorSupport;

public class LookupEntityEditor<T extends LookupEntity> extends PropertyEditorSupport {
	private Class<T> klazz;
	public LookupEntityEditor(Class<T> klazz) {
		this.klazz = klazz;
	}

	@Override
	public String getAsText() {
		T value = (T)getValue();
		if (value != null)
			return value.getCode();
		return super.getAsText();
	}

	@Override
	public void setAsText(String s) throws IllegalArgumentException {
		try {
			T value = klazz.newInstance();
			value.setCode(s);
			setValue(value);
		} catch (Exception e) {
			super.setAsText(s);
		}
	}
}
