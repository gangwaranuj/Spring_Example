package com.workmarket.web.editors;

import com.workmarket.search.request.user.CompanyType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Component("companyTypeEditor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CompanyTypeEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(String s) throws IllegalArgumentException {
		CompanyType type = null;

		if (StringUtils.isNotEmpty(s)) {
			try {
				Integer value = Integer.valueOf(s);
				type = CompanyType.findByValue(value);
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException(String.format("Unable to parse '%s' to Integer!", s), ex);
			}
		}

		setValue(type);
	}
}
