package com.workmarket.web.editors;

import com.workmarket.search.request.user.RatingsChoice;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Component("ratingsChoiceEditor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RatingsChoiceEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(String s) throws IllegalArgumentException {
		RatingsChoice rating = null;

		if (StringUtils.isNotEmpty(s)) {
			try {
				Integer value = Integer.valueOf(s);
				rating = RatingsChoice.findByValue(value);
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException(String.format("Unable to parse '%s' to Integer!", s), ex);
			}
		}

		setValue(rating);
	}
}
