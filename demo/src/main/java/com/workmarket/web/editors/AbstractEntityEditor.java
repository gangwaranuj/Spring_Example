package com.workmarket.web.editors;

import com.workmarket.domains.model.AbstractEntity;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

public abstract class AbstractEntityEditor<T extends AbstractEntity> extends PropertyEditorSupport {
	public abstract T getEntity(Long id);

	@Override
	public String getAsText() {
		T value = (T)getValue();
		return (value == null) ? null : String.valueOf(value.getId());
	}

	@Override
	public void setAsText(String s) throws IllegalArgumentException {
		T entity = null;

		if (StringUtils.hasText(s)) {
			try {
				entity = getEntity(Long.valueOf(s));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(e);
			}
		}

		setValue(entity);
	}
}
